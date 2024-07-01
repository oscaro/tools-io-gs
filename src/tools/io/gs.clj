(ns tools.io.gs
  (:require
   [clj-gcloud.coerce :refer [->clj]]
   [clj-gcloud.storage :as gs]
   [clojure.string :as str]
   [tools.io.core :refer [register-file-pred!
                          mk-input-stream
                          mk-output-stream
                          list-files
                          list-dirs
                          sizeof]])
  (:import java.io.FileNotFoundException
           com.google.cloud.storage.Blob))


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


(defmethod mk-input-stream :gs
  [filename & [options]]
  (let [stream (some->> filename
                        ->gs-blob-id
                        (gs/get-blob (mk-client options))
                        gs/read-channel
                        gs/->input-stream)]
    (if stream
      {:stream stream}
      (throw (FileNotFoundException. filename)))))


(defmethod mk-output-stream :gs
  [filename & [options]]
  (let [{:keys [encoding
                mime-type]
         :or   {encoding  "UTF-8"
                mime-type "text/plain"}} options]
    {:stream (-> (gs/create-blob-writer
                  (mk-client options)
                  (gs/blob-info (gs/->blob-id filename) {:content-encoding encoding
                                                         :content-type     mime-type}))
                 gs/->output-stream)}))


(defmethod list-files :gs
  [path & [options]]
  (->> (gs/ls (mk-client options) path (or options {}))
       (map (fn [blob]
              (let [{:keys [blob-id]} (->clj blob)]
                (str "gs://" (:bucket blob-id) "/" (:name blob-id)))))))


(defmethod list-dirs :gs
  [path & [options]]
  (let [directory (if (= (-> path seq last) \/) path
                    (str path "/"))]
    (->> (gs/ls (mk-client options) directory
                (or options {:current-directory "/"}))
       (map (fn [blob]
              (let [{:keys [blob-id]} (->clj blob)]
                (str "gs://" (:bucket blob-id) "/" (:name blob-id))))))))


(defmethod sizeof :gs
  [path & [options]]
  (->> (gs/ls (mk-client options) path (or options {}))
       (reduce (fn [acc ^Blob blob]
                 (+ acc (.getSize blob))) 0)))
