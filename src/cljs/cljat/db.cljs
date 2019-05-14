(ns cljat.db
  ;(:require-macros
   ;[cljat.env :refer [cljs-env]])
  (:require [cljs.reader]
            [cljs.spec.alpha :as s]
            [cljs.core.async :refer [<!]]
            [cljat.env :as env]
            [haslett.client :as ws]
            [haslett.format :as fmt]
            [re-frame.core :as re-frame]))

(s/def ::id int?)
(s/def ::author string?)
(s/def ::timesatamp int?)
(s/def ::text string?)
(s/def ::message (s/keys :req-un [::id ::author ::timestamp ::text]))
(s/def ::messages (s/and
                   (s/map-of ::id ::message)
                   #(map? %)
                   ; TODO почему-то не работает
                   ;#(sorted? %)
                   ))
(s/def ::db (s/keys :req-un [::messages ::ws]))

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

(def default-db {:messages (sorted-map)
                 :ws (<! (ws/connect (str "ws://" env/domain "/ws")
                          ;(cljs-env :domain)
                                     {:format fmt/edn}))})

