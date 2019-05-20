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
                (.preventDefault %)
                (dispatch [:login-request usr pass]))]
    (fn []
      [:form.form-signin {:on-submit send}
       [:h1.h2.mb-3.font-weight-normal.text-center "Sign in/sign up"]
       [:label.sr-only {:for "input-login"} "Login"]
       [:input#input-login.form-control {:type "text"
                                         :placeholder "Login"
                                         :value @username
                                         :auto-focus true
                                         :maxLength 30
                                         :required true
                                         :on-change   #(reset! username (-> % .-target .-value))}]
       [:label.sr-only {:for "input-password"} "Password"]
       [:input#input-password.form-control {:type "password"
                                            :placeholder "Password"
                                            :value @password
                                            :required true
                                            :maxLength 300
                                            :on-change   #(reset! password (-> % .-target .-value))}]
       [:button.btn.btn-lg.btn-primary.btn-block {:type "submit"} "Login"]
       [error]])))

(defn right-message [{:keys [author text timestamp]}]
  [:div.direct-chat-msg.right
   [:div.direct-chat-info.clearfix
    [:span.direct-chat-name.pull-right author]
    [:span.direct-chat-timestamp.pull-left timestamp]]
   [:div.direct-chat-text text]])

(defn left-message [{:keys [author text timestamp]}]
  [:div.direct-chat-msg
   [:div.direct-chat-info.clearfix
    [:span.direct-chat-name.pull-left author]
    [:span.direct-chat-timestamp.pull-right timestamp]]
   [:div.direct-chat-text text]])

(defn message [{author :author :as message} login]
  (if (= author login) [left-message message] [right-message message]))

(defn messages [login]
  [:div.box-body {:windowscroll (dispatch [:debug-print "azaza"])}
   [:div.direct-chat-messages
    (for [msg @(subscribe [:messages])]
      ^{:key (:id msg)} [message msg login])]])

(defn send-message []
  (let [val (reagent/atom "")
        send #(let [v (-> @val str str/trim)]
                (.preventDefault %)
                (dispatch [:send-message v])
                (reset! val ""))]
    (fn []
      [:div.box-footer
       [:form {:on-submit send}
        [:div.input-group.input-group-sm
         [:input.form-control {:type "text"
                               :placeholder "Type your message here"
                               :value @val
                               :auto-focus true
                               :required true
                               :maxLength 500
                               :on-change #(reset! val (-> % .-target .-value))}]
         [:div.input-group-append
          [:button.btn.btn-outline-success  {:type "submit"} "Send"]]]]])))

(defn logout []
  (fn []
    [:div.box-tools.pull-right
     [:button.btn.btn-warning.btn-sm {:on-click #(dispatch [:logout-request])} "Logout"]]))

(defn chat [login]
  [:div.container
   [:div.row.justify-content-center
    [:div.col-12
     [:div.box.box-primary.direct-chat.direct-chat-primary
      [:div.box-header.with-border
       [:h3.box-title "Cljat"]
       [logout]]
      [messages login]
      [send-message]]]]])

(defn cljat-app []
  (let [l @(subscribe [:login])]
    (if (nil? l) [login] [chat l])))
