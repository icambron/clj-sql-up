(defproject clj-sql-up "0.3.5"
  :description "A simple leiningen plugin for managing sql migrations"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cpath-clj "0.1.2"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [com.cemerick/pomegranate "0.3.0"]]
  :url "https://github.com/ckuttruff/clj-sql-up"
  :profiles {:dev {:dependencies [[org.hsqldb/hsqldb "2.3.2"]]}}
  :eval-in-leiningen true)
