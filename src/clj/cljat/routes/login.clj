(ns cljat.routes.login
  (:require
   [cljat.db.core :as db]
   [buddy.hashers :as hashers]
   [cljat.middleware :as middleware]
   [ring.util.http-response :as response]
   [clojure.data.json :as json]))

(def status-success (json/write-str {:status "success"}))

(defn create-user!
  [login password]
  (db/create-user! {:login login :password (hashers/derive password)}))

(defn auth-user!
  [login password]
  (let [user (db/get-user {:login login})]
    (if (nil? user)
      (create-user! login password)
      (when (hashers/check password (:pass user)) user))))

(defn login-handler [{{:keys [login password]} :params
                      session :session}]
  (if-let [user (auth-user! login password)]
    (assoc (response/ok status-success)
           :session (assoc session :identity login))
    (response/unauthorized)))

(defn logout-handler [{session :session}]
  (assoc (response/ok status-success)
         :session (dissoc session :identity)))

(defn login-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/login" {:post login-handler}]
   ["/logout" {:get logout-handler}]])
