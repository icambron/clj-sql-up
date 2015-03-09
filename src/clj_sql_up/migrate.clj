(ns clj-sql-up.migrate
  (:require [clojure.set :as set]
            [clojure.java.jdbc :as sql]
            [clj-sql-up.migration-files :as files]))

(defn- migration-tbl-exists? [db]
  ; Here we'll use the JDBC connection's API for finding if a table exists, as the
  ; `create table if exists` isn't quite universally accepted, and querying for a table's
  ; existence is very DB-specific
  (let [tables (-> (sql/get-connection db)
                   .getMetaData
                   (.getTables nil nil nil nil)
                   sql/metadata-result)]
    (not (empty? (filter #(= (.toLowerCase (:table_name %)) "clj_sql_migrations") tables)))))

(defn create-migrations-tbl [db]
  (if-not (migration-tbl-exists? db)
    (sql/db-do-commands
     db
     (sql/create-table-ddl :clj_sql_migrations [:name "varchar(20)" "NOT NULL"])
     "create unique index clj_sql_migrations_name_index on clj_sql_migrations (name)")))

(defn completed-migrations
  ([db] (completed-migrations db (files/get-migration-files)))
  ([db migration-files]
    (sql/query
     db
     ["SELECT name FROM clj_sql_migrations ORDER BY name DESC"]
     :row-fn #(files/migration-filename
                 (:name %) migration-files))))

(defn pending-migrations
  [db]
  (let [migration-files (files/get-migration-files)]
    (sort-by str (set/difference (set migration-files)
                                 (set (completed-migrations db migration-files))))))

(defn run-migrations [db files direction]
  (doseq [file files]
    (files/load-migration-file file)
    (let [sql-arr ((resolve direction))
          migr-id (files/migration-id file)]
      (sql/db-transaction* db (fn [trans_db]
       (if (= direction 'down)
         (do (println (str "Reversing: " file))
           (sql/delete! trans_db :clj_sql_migrations ["name=?" migr-id]))
         (do (println (str "Migrating: " file))
           (sql/insert! trans_db :clj_sql_migrations {:name migr-id})))
       (doseq [s sql-arr]
         (sql/db-do-commands trans_db s)))))))

(defn migrate [db]
  (create-migrations-tbl db)
  (run-migrations db (pending-migrations db) 'up))

(defn rollback [db n]
  (create-migrations-tbl db)
  (let [n (long (or n 1))]
    (run-migrations db (take n (completed-migrations db)) 'down)))
