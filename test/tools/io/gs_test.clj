(ns tools.io.gs-test
  (:require
   [clj-gcloud.storage :as gs]
   [clojure.test :refer [deftest is]]
   [tools.io :as tio]
   [tools.io.gs])
  (:import
   (com.google.cloud.storage.contrib.nio.testing LocalStorageHelper)))


(defn- storage-with
  "An in-memory storage holding an empty object at each of `uris`."
  [& uris]
  (let [storage (.getService (LocalStorageHelper/getOptions))]
    (doseq [uri uris]
      (gs/create-blob storage (gs/blob-info (gs/->blob-id uri) {})))
    storage))


(deftest list-files-test
  (let [storage (storage-with "gs://b/d/a.txt" "gs://b/d/sub/x.txt" "gs://b/e/y.txt")]
    (is (= ["gs://b/d/a.txt" "gs://b/d/sub/x.txt"]
           (tio/list-files "gs://b/d" {:storage storage})))))


(deftest list-dirs-test
  (let [storage (storage-with "gs://b/d/" "gs://b/d/a.txt" "gs://b/d/sub/x.txt"
                              "gs://b/d/sub2/y/z.txt")]
    (is (= ["gs://b/d/sub/" "gs://b/d/sub2/"]
           (tio/list-dirs "gs://b/d" {:storage storage})
           (tio/list-dirs "gs://b/d/" {:storage storage})))))
