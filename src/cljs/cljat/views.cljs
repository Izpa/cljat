(ns cljat.views
  (:require [reagent.core  :as reagent]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]))

(defn login []
  (let [username (reagent/atom "")
        password (reagent/atom "")
        on-send #(when (seq %) (dispatch [:login %1 %2]))
        send #(let [u (-> @username str str/trim)
                    p (-> @password str str/trim)]
                (on-send u p))]
    (fn [_]
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
       [:button  {:on-click send} "Login"]])))

(defn message []
  (fn [{:keys [id author timestamp text]}]
    [:li
     [:p author]
     [:p timestamp]
     [:p text]]))

(defn messages []
  [:ul#messages
   (for [msg @(subscribe [:messages])]
     ^{:key (:id msg)} [message msg])])

(defn new-message []
  (let [val (reagent/atom "")
        on-send #(when (seq %) (dispatch [:receive-message 1 "author1" 1122 %]))
        send #(let [v (-> @val str str/trim)]
                (on-send v)
                (reset! val ""))]
    (fn [_]
      [:div
       [:input {:type "text"
                :placeholder "Type your message here"
                :value @val
                :auto-focus true
                :on-change   #(reset! val (-> % .-target .-value))
                :on-key-down #(when (= (.-which %) 13) (send))}]
       [:button  {:on-click send} "Send"]])))

(defn chat []
  [:div
   [messages]
   [new-message]])

(defn cljat-app []
  (if @(subscribe [:login])
    [chat]
    [login]))
