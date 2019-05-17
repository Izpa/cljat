(ns cljat.events
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [cljs.spec.alpha :as s]
   [cljat.env :as env]
   [haslett.client :as ws-client]
   [haslett.format :as fmt]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [cljs.core.async :refer [<! >!]]
   [cognitect.transit :as t]))

(reg-event-db
 :initialise-db
 (fn [_ _]
   {:messages (sorted-map)
    :ws nil
    :login nil
    :error nil}))


(defn reg-ws-dispatcher [ws]
  (go-loop []
    (when-let [message (-> (<! (:source ws))
                           (#(t/read (t/reader :json) %)))]
      (dispatch [:receive-message message])
      (recur))))

(reg-event-db
 :login
 (fn [db [_ login]]
   (go
     (let [ws (<! (ws-client/connect (str "ws://" env/domain "/ws")))]
       (dispatch [:merge-db (merge db {:ws ws :login login :error nil})])
       (reg-ws-dispatcher ws)))
   db))


(reg-event-db
 :merge-db
 (fn [db [_ to-merge]]
   (merge db to-merge)))

(reg-event-fx
 :login-request
 (fn [_ [_ login password]]
   {:http-xhrio {:method :post
                 :uri (str "http" (if env/use-http "" "s") "://" env/domain "/login")
                 :on-success [:login login]
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format (ajax/json-request-format)
                 :on-failure [:merge-db {:error "Incorrect password for exist user"}]
                 :body (doto (js/FormData.)
                         (.append "login" login)
                         (.append "password" password))}}))

(reg-event-fx
 :logout-request
 (fn [{:keys [db]} _]
   {:http-xhrio {:method :get
                 :uri (str "http://" env/domain "/logout")
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format (ajax/json-request-format)
                 :on-success [:logout]}}))

(reg-event-db
 :logout
 (fn [{ws :ws :as db} _]
   (ws-client/close ws)
   (merge db {:login nil :ws nil})))

(reg-event-db
 :receive-message
 (fn [db [_ {:strs [id author timestamp text]}]]
   (assoc-in db [:messages id] {:id id :author author :timestamp timestamp :text text})))

(reg-event-db
 :send-message
 (fn [{ws :ws :as db} [_ text]]
   (go (>! (:sink ws) text))
   db))
