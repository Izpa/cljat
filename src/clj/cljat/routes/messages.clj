(ns cljat.routes.messages
  (:require
   [cljat.db.core :as db]
   [cljat.middleware :as middleware]
   [ring.util.http-response :as response]
   [clojure.data.json :as json]
   [clojure.string :as str]
   [clojure.tools.logging :as log]))

(defn db-message->json [{text :message_text timestamp :message_timestamp :keys [id author]}]
  {:id id
   :text text
   :author author
   :timestamp (-> timestamp
                  (str)
                  (str/replace #"T" " ")
                  (str/split #"\.")
                  (first))})

(defn messages-handler [{{id :id} :params}]
  (log/info "request messages for id: " id)
  (let [messages (map db-message->json (db/get-messages {:id id}))]
    (response/ok (json/write-str {:status "success" :messages messages}))))

(defn messages-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/messages" {:get messages-handler}]])

