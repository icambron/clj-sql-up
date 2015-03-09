(ns clj-sql-up.migration-files-test
  (:require [clj-sql-up.migration-files :as mf])
  (:use clojure.test))

(def files
  (mf/get-migration-file-names "test/clj_sql_up/migrations"))

(deftest get-migration-files
  (is (= files ["20130719212020374-zzz.clj" "20130719212023948-ccc.clj"
                "20130719212027077-bbb.clj" "20130719212031096-aaa.clj"])))

(deftest migration-filename
  (are [id fname] (= (mf/migration-filename id files) fname)
       "20130719212027077" "20130719212027077-bbb.clj"
       "20130719212023948" "20130719212023948-ccc.clj"))
