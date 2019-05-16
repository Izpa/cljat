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
      [:div
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
       [:button#login  {:on-click send} "Login"]
       [error]])))

(defn message [{:keys [id author timestamp text]}]
  [:li
   [:p author]
   [:p timestamp]
   [:p text]])

(defn messages []
  [:ul#messages
   (for [msg @(subscribe [:messages])]
     ^{:key (:id msg)} [message msg])])

(defn send-message []
  (let [val (reagent/atom "")
        send #(let [v (-> @val str str/trim)]
                (dispatch [:send-message v])
                (reset! val ""))]
    (fn []
      [:div
       [:input {:type "text"
                :placeholder "Type your message here"
                :value @val
                :auto-focus true
                :on-change   #(reset! val (-> % .-target .-value))
                :on-key-down #(when (= (.-which %) 13) (send))}]
       [:button#send  {:on-click send} "Send"]])))

(defn logout []
  (fn []
    [:button#logout  {:on-click #(dispatch [:logout-request])} "Logout"]))

(defn chat []
  [:div
   [logout]
   [messages]
   [send-message]])

(defn cljat-app []
  (if (nil? @(subscribe [:login]))
    [login]
    [chat]))
