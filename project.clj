(defproject com.oscaro/tools-io-gs "0.1.6-SNAPSHOT"
  :description "google storage support for tools-io"
  :url "https://github.com/oscaro/tools-io-gs"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
<<<<<<< HEAD
                 [com.oscaro/tools-io "0.3.22"]
=======
                 [com.oscaro/tools-io "0.3.17"]
>>>>>>> 95b871d688372e06e69343d24312bb15490c9275
                 [com.oscaro/clj-gcloud-storage "0.71-1.2"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["shell" "git" "commit" "-am" "Version ${:version} [ci skip]"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["shell" "git" "commit" "-am" "Version ${:version} [ci skip]"]
                  ["vcs" "push"]]
  :plugins [[lein-shell "0.5.0"]]
  :profiles {:dev {:global-vars    {*warn-on-reflection* true}
                   :plugins        [[lein-codox "0.10.2"]]
                   :dependencies   [[org.clojure/tools.namespace "0.2.11"]]}})
