(defproject com.oscaro/tools-io-gs "0.1.22-SNAPSHOT"
  :description "google storage support for tools-io"
  :url "https://github.com/oscaro/tools-io-gs"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [com.oscaro/clj-gcloud-storage "0.240-3.0"]
                 [com.oscaro/tools-io "0.3.41"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases true}]]
  :signing {:gpg-key "github-cicd@oscaro.com"}
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
                   :plugins        [[lein-codox "0.10.8"]]
                   :dependencies   [[org.clojure/tools.namespace "1.5.0"]]}})
