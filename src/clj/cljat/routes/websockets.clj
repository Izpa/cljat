(ns cljat.routes.websockets
  (:require
   [org.httpkit.server
    :refer [send! with-channel on-close on-receive]]
   [cognitect.transit :as t]
   [clojure.tools.logging :as log]))

(defonce channels (atom #{}))

(defn connect! [channel]
  (log/info "channel open")
  (swap! channels conj channel))

(defn disconnect! [channel status]
  (log/info "channel closed:" status)
  (swap! channels #(remove #{channel} %)))

(defn notify-clients [msg]
  (log/info "new message:" msg)
  (doseq [channel @channels]
    (send! channel msg)))

(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel #(notify-clients %))))

(def websocket-routes
  ["/ws" ws-handler])
