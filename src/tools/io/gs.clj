(ns tools.io.gs
  (:require
   [clj-gcloud.storage :as gs]
   [clojure.string :as str]
   [tools.io.core :refer [register-file-pred!
                          mk-input-stream
                          mk-output-stream
                          list-files
                          list-dirs
                          exists?
                          sizeof]])
  (:import java.io.FileNotFoundException
           java.time.Instant
           (com.google.cloud.storage Blob BlobId Storage Storage$BlobWriteOption)))


(defn- gs-file?
  [path]
  (str/starts-with? (str/lower-case (str path)) "gs://"))


(defn mk-client
  "Make storage client from option map"
  [{:keys [storage] :as options}]
  (or storage (gs/init options)))


(register-file-pred! :gs gs-file?)

(defn- ->gs-blob-id
  [uri]
  (try
    (gs/->blob-id uri)
    (catch NullPointerException _ nil)))


(defn- ->versioned-blob-id
  "Returns the blob id of `uri`, pinned to `generation` when given."
  [uri generation]
  (when-let [^BlobId id (->gs-blob-id uri)]
    (if generation
      (BlobId/of (.getBucket id) (.getName id) (long generation))
      id)))


(defn object-meta
  "Returns the metadata of the object at `uri`, or nil if there is none.

  Keys: `:generation`, `:size` in bytes, `:crc32c` (base64, as Cloud Storage
  reports it) and `:created-at`, the instant this generation was written.

  With `:generation` in `options`, returns that generation's metadata, or nil
  once it is no longer live (unless the bucket keeps noncurrent versions)."
  [uri & [options]]
  (when-let [^Blob blob (some->> (->versioned-blob-id uri (:generation options))
                                 (gs/get-blob (mk-client options)))]
    {:generation (.getGeneration blob)
     :size       (.getSize blob)
     :crc32c     (.getCrc32c blob)
     :created-at (Instant/ofEpochMilli (.getCreateTime blob))}))


(defmethod mk-input-stream :gs
  [filename & [options]]
  (let [stream (some->> (->versioned-blob-id filename (:generation options))
                        (gs/get-blob (mk-client options))
                        gs/read-channel
                        gs/->input-stream)]
    (if stream
      {:stream stream}
      (throw (FileNotFoundException. filename)))))


(defmethod mk-output-stream :gs
  [filename & [options]]
  (let [{:keys [encoding
                mime-type
                content-disposition
                cache-control
                content-language
                metadata]
         :or   {encoding  "UTF-8"
                mime-type "text/plain"}} options
        option-map (cond-> {:content-encoding encoding
                            :content-type     mime-type}
                     content-disposition (assoc :content-disposition content-disposition)
                     content-language    (assoc :content-language content-language)
                     cache-control       (assoc :cache-control cache-control)
                     metadata            (assoc :metadata metadata))]
    {:stream (-> (.writer ^Storage (mk-client options)
                          (gs/blob-info (gs/->blob-id filename) option-map)
                          (into-array Storage$BlobWriteOption
                                      (when-let [generation (:if-generation-match options)]
                                        [(Storage$BlobWriteOption/generationMatch (long generation))])))
                 gs/->output-stream)}))


(defn- blob->uri
  [^Blob blob]
  (str "gs://" (.getBucket blob) "/" (.getName blob)))


(defmethod list-files :gs
  [path & [options]]
  (->> (gs/ls (mk-client options) path (or options {}))
       (map blob->uri)))


(defmethod list-dirs :gs
  [path & [options]]
  (let [path      (str path)
        directory (if (str/ends-with? path "/") path (str path "/"))]
    (->> (gs/ls (mk-client options) directory
                (assoc options :current-directory true))
         (filter #(.isDirectory ^Blob %))
         (map blob->uri))))


(defmethod exists? :gs
  [path & [options]]
  (some? (object-meta path options)))


(defmethod sizeof :gs
  [path & [options]]
  (->> (gs/ls (mk-client options) path (or options {}))
       (reduce (fn [acc ^Blob blob]
                 (+ acc (.getSize blob))) 0)))
