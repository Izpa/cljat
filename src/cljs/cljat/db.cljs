(ns cljat.db
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader]
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
(s/def ::login (s/nilable string?))
(s/def ::db (s/keys :req-un [::messages ::ws ::login]))

(def default-db {:messages (sorted-map)
                 :ws nil
                 :login nil})

(def messages-ls-key "cljat-reframe-messages")

(defn messages->local-store
  "Puts messages into localStorage"
  [messages]
  (.setItem js/localStorage messages-ls-key (str messages)))

(re-frame/reg-cofx
 :local-store-messages
 (fn [cofx _]
   (assoc cofx :local-store-messages
          (into (sorted-map)
                (some->> (.getItem js/localStorage messages-ls-key)
                         (cljs.reader/read-string))))))

(def login-ls-key "cljat-reframe-login")

(defn login->local-store
  "Puts login into localStorage"
  [login]
  (.setItem js/localStorage login-ls-key (str login)))

(re-frame/reg-cofx
 :local-store-login
 (fn [cofx _]
   (assoc cofx :local-store-login
          (into (sorted-map)
                (some->> (.getItem js/localStorage login-ls-key)
                         (cljs.reader/read-string))))))

(def login-ls-key "cljat-reframe-")

(defn login->local-store
  "Puts login into localStorage"
  [login]
  (.setItem js/localStorage login-ls-key (str login)))

(re-frame/reg-cofx
 :local-store-login
 (fn [cofx _]
   (assoc cofx :local-store-login
          (into (sorted-map)
                (some->> (.getItem js/localStorage login-ls-key)
                         (cljs.reader/read-string))))))

