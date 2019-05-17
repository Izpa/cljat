(ns cljat.views
  (:require [reagent.core  :as reagent]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]))

(defn error []
  (let [error  @(subscribe [:error])]
    (when (some? error) [:p (str error)])))

(defn login []
  (let [username (reagent/atom "")
        password (reagent/atom "")
        send #(let [usr (-> @username str str/trim)
                    pass (-> @password str str/trim)]
                (dispatch [:login-request usr pass]))]
    (fn []
      [:div.inputs
       [:input {:type "text"
                :placeholder "Username"
                :value @username
                :auto-focus true
                :on-change   #(reset! username (-> % .-target .-value))
                :on-key-down #(when (= (.-which %) 13) (send))}]
       [:input {:type "password"
                :placeholder "Password"
                :value @password
                :on-change   #(reset! password (-> % .-target .-value))
                :on-key-down #(when (= (.-which %) 13) (send))}]
       [:button  {:on-click send} "Login"]
       [error]])))

(defn message [{:keys [author timestamp text]} login]
  (let [is-outgoing-message (= author login)]
    [(if is-outgoing-message :div.chat :div.chat.chat_other)
     (if (not is-outgoing-message) [:div.chat_name author])
     [:div.chat_message text]
     [:div.chat_name timestamp]]))

(defn messages [login]
  [:div#chat_s
   (for [msg @(subscribe [:messages])]
     ^{:key (:id msg)} [message msg login])])

(defn send-message []
  (let [val (reagent/atom "")
        send #(let [v (-> @val str str/trim)]
                (dispatch [:send-message v])
                (reset! val ""))]
    (fn []
      [:div.inputs
       [:input {:type "text"
                :placeholder "Type your message here"
                :value @val
                :auto-focus true
                :on-change   #(reset! val (-> % .-target .-value))
                :on-key-down #(when (= (.-which %) 13) (send))}]
       [:button  {:on-click send} "Send"]])))

(defn logout []
  (fn []
    [:div.chat_header
     [:button {:on-click #(dispatch [:logout-request])} "Logout"]]))

(defn chat [login]
  [:div
   [logout]
   [messages login]
   [send-message]])

(defn cljat-app []
  (let [l @(subscribe [:login])
        elem (if (nil? l)
               [login]
               [chat l])]
    [:div#chat elem]))
