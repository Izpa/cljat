(ns cljat.db
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader]
            [cljat.env :as env]
            [haslett.client :as ws]
            [haslett.format :as fmt]
            [cljs.core.async :refer [<!]]
            [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(s/def ::id int?)
(s/def ::author string?)
(s/def ::timesatamp int?)
(s/def ::text string?)
(s/def ::message (s/keys :req-un [::id ::author ::timestamp ::text]))
(s/def ::messages (s/and
                   (s/map-of ::id ::message)
                   #(map? %)
                   #(sorted? %)
                   ))
(s/def ::db (s/keys :req-un [::messages ::ws]))

(def default-db {:messages (sorted-map)
                 :ws (go (<! (ws/connect (str "ws://" env/domain "/ws") {:format fmt/edn})))})


(def ls-key "cljat-reframe")

(defn messages->local-store
  "Puts messages into localStorage"
  [messages]
  (.setItem js/localStorage ls-key (str messages)))

(re-frame/reg-cofx
 :local-store-messages
 (fn [cofx _]
   (assoc cofx :local-store-messages
          (into (sorted-map)
                (some->> (.getItem js/localStorage ls-key)
                         (cljs.reader/read-string))))))
