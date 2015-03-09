(ns clj-sql-up.migration-files
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [cpath-clj.core :as cpath]))

(def ^:dynamic *migration-dir* "migrations")

(defn last-part [uri]
  ;;todo: not sure how robust this really is
  (-> uri str (str/split #"/") last))

(defn migration-id [uri]
  (last (re-find #"^([0-9]+)-" (last-part uri))))

(defn- migration-file? [uri]
  (re-find #"([0-9]+)-.*\.clj$" (last-part uri)))

(defn get-migration-files
  ([] (get-migration-files *migration-dir*))
  ([dir-name]
   (let [dir-file (io/as-file dir-name)]
     (->>
        (if (.exists dir-file)
            (map io/as-url (.listFiles dir-file))
            (->> (cpath/resources dir-name)
                 vals
                 (map first)))
         (filter #(-> % last-part migration-file?))
         (sort-by str)))))

(defn get-migration-file-names
  ([] (get-migration-file-names *migration-dir*))
  ([dir-name] (map last-part (get-migration-files dir-name))))

(defn load-migration-file [uri]
 (->
   (slurp uri)
   load-string))

(defn migration-filename [migr-id migr-files]
  "Returns the filename associated with the given migration id"
  [migr-id migr-files]
  (->> migr-files
       (filter #(re-find (re-pattern (str migr-id ".*")) (str %)))
       first))
