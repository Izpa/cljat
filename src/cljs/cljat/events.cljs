(ns cljat.events
  (:require
   [cljat.db :refer [default-db messages->local-store]]
   [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx path after]]
   [cljs.spec.alpha :as s]))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (after (partial check-and-throw :cljat.db/db)))

(def ->local-store (after messages->local-store))

(def message-interceptors [check-spec-interceptor
                           (path :messages)
                           ->local-store])

;; TODO remove after add working with server db
(defn allocate-next-id
  [messages]
  ((fnil inc 0) (last (keys messages))))

(reg-event-fx
 :initialise-db
 [(inject-cofx :local-store-messages)
  check-spec-interceptor
  ]
 (fn [{:keys [db local-store-messages]} _]
   {:db (assoc default-db :messages local-store-messages)}))

(reg-event-db
 :new-message
 message-interceptors
 (fn [messages [_ author timestamp text]]
   (let [id (allocate-next-id messages)]
     (assoc messages id {:id id :author author :timestamp timestamp :text text}))))
