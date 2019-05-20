(ns cljat.events
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [cljs.spec.alpha :as s]
   [cljat.env :as env]
   [haslett.client :as ws-client]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [cljs.core.async :refer [<! >!]]
   [cognitect.transit :as t]
   [cemerick.url :refer [url]]
   [clojure.walk :refer [keywordize-keys]]))

(defn reg-ws-dispatcher [ws]
  (go-loop []
    (when-let [messages (<! (:source ws))]
      (dispatch [:receive-messages messages])
      (recur))))

(defn http-url->ws [http-url]
  (let [parsed-http-url (url http-url)
        ws-protocol (if (= (:protocol parsed-http-url) "https") "wss" "ws")]
    (str (assoc parsed-http-url :protocol ws-protocol))))

(defn api-ajax-request [path params]
  (let [request {:method :get
                 :uri (str env/api-url path)
                 :format (ajax/json-request-format)
                 :response-format (ajax/raw-response-format)
                 :headers {"X-CSRF-Token" js/csrfToken}}]
    {:http-xhrio (merge request params)}))

(reg-event-db
 :initialise-db
 (fn [_ _]
   {:messages (sorted-map)
    :ws nil
    :login nil
    :error nil}))

(reg-event-db
 :merge-db
 (fn [db [_ to-merge]]
   (merge db to-merge)))

(reg-event-fx
 :login-request
 (fn [_ [_ login password]]
   (let [params {:method :post
                 :body (doto (js/FormData.)
                         (.append "login" login)
                         (.append "password" password))
                 :on-success [:login login]
                 :on-failure [:merge-db {:error "Incorrect password for exist user"}]}]
     (assoc (api-ajax-request "login" params) :dispatch [:messages-history-request]))))

(reg-event-db
 :login
 (fn [db [_ login]]
   (go
     (let [ws (<! (ws-client/connect (str (http-url->ws env/api-url) "ws")))]
       (dispatch [:merge-db (merge db {:ws ws :login login :error nil})])
       (reg-ws-dispatcher ws)))
   db))

(reg-event-fx
 :logout-request
 (fn []
   (api-ajax-request "logout" {:on-success [:logout]})))

(reg-event-db
 :logout
 (fn [{ws :ws :as db} _]
   (ws-client/close ws)
   (merge db {:login nil :ws nil})))

(reg-event-fx
 :messages-history-request
 (fn [{{messages :messages} :db} _]
   (let [last-id (last (keys messages))
         params (when last-id [:params {:id last-id}])
         request (conj {:on-success [:receive-messages]} params)]
     (api-ajax-request "messages" request))))

(reg-event-db
 :receive-messages
 (fn [db [_ response]]
   (let [read-json #(t/read (t/reader :json) %)
         messages (-> response
                      (#(t/read (t/reader :json) %))
                      (get "messages"))
         messages (map keywordize-keys messages)
         message->db #(assoc-in %1 [:messages (:id %2)] %2)]
     (reduce message->db db messages))))

(reg-event-db
 :receive-message
 (fn [db [_ {:strs [id author timestamp text]}]]
   (assoc-in db [:messages id] {:id id :author author :timestamp timestamp :text text})))

(reg-event-db
 :send-message
 (fn [{ws :ws :as db} [_ text]]
   (go (>! (:sink ws) text))
   db))

