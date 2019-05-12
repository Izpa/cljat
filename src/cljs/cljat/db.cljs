(ns cljat.db
  (:require [cljs.reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(s/def ::id int?)
(s/def ::author string?)
(s/def ::timesatamp int?)
(s/def ::text string?)
(s/def ::message (s/keys :req-un [::id ::author ::timestamp ::text]))

(def sorted-map? (every-pred map? sorted?))

(s/def ::messages (s/and
                   (s/map-of ::id ::message)
                   #(map? %)
                   ; TODO почему-то не работает
                   ;#(sorted? %)
                   ))
(s/def ::db (s/keys :req-un [::messages]))

(def default-db {:messages (sorted-map)})

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
