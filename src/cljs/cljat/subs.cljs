(ns cljat.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(defn sorted-messages
  [db _]
  (:messages db))
(reg-sub :sorted-messages sorted-messages)

(reg-sub
 :messages
 (fn [query-v _]
   (subscribe [:sorted-messages]))
 (fn [sorted-messages query-v _]
   (vals sorted-messages)))