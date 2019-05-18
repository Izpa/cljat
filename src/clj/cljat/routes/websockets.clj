(ns cljat.routes.websockets
  (:require
   [org.httpkit.server
    :refer [send! with-channel on-close on-receive]]
   [cljat.db.core :as db]
   [cognitect.transit :as t]
   [clojure.tools.logging :as log]
   [clojure.set]
   [cljat.middleware :as middleware]
   [clojure.data.json :as json]
   [cljat.routes.messages :refer [db-message->json]]
   [clojure.data.json :as json]))

(defonce channels (atom #{}))

(defn connect! [channel]
  (log/info "channel open")
  (swap! channels conj channel))

(defn disconnect! [channel status]
  (log/info "channel closed:" status)
  (swap! channels #(remove #{channel} %)))

(defn notify-clients [message {{author :identity} :session}]
  (log/info "new message: " message "from user-id:" author)
  (let [message (db/create-message! {:text message, :author author})
        json-message (json/write-str {:messages [(db-message->json message)]})]
    (log/info "message: " message)
    (doseq [channel @channels]
      (send! channel json-message))))

(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel #(notify-clients % request))))

(defn websocket-routes []
  [""
   {:middleware [middleware/wrap-auth
                 middleware/wrap-restricted]}
   ["/ws" ws-handler]])
