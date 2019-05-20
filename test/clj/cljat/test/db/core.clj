(ns cljat.test.db.core
  (:require
   [cljat.db.core :refer [*db*] :as db]
   [luminus-migrations.core :as migrations]
   [clojure.test :refer :all]
   [clojure.java.jdbc :as jdbc]
   [cljat.config :refer [env]]
   [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'cljat.config/env
     #'cljat.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= {:login "user" :pass "password"}
           (db/create-user!
              t-conn
              {:login "user"
               :password "password"})))
    (is (= {:login "user" :pass "password"}
           (db/get-user t-conn {:login "user"})))))
