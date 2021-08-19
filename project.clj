(defproject com.oscaro/tools-io-gs "0.1.6-SNAPSHOT"
  :description "google storage support for tools-io"
  :url "https://github.com/oscaro/tools-io-gs"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [com.oscaro/clj-gcloud-storage "0.159-1.0"]
                 [com.oscaro/tools-io "0.3.22"]]
  :profiles {:dev {:global-vars    {*warn-on-reflection* true}
                   :plugins        [[lein-codox "0.10.2"]]
                   :dependencies   [[org.clojure/tools.namespace "1.1.0"]]}})
