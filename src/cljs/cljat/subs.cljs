(ns cljat.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 :messages
 (fn [{messages :messages} _]
   (vals messages)))

(reg-sub
 :login
 (fn [{login :login} _]
   login))

(reg-sub
 :error
 (fn [{error :error} _]
   error))
