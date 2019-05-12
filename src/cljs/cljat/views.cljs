(ns cljat.views
  (:require [reagent.core  :as reagent]
            [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]))

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
        on-send #(when (seq %) (dispatch [:new-message "author1" 1122 %]))
        send #(let [v (-> @val str str/trim)]
                (on-send v)
                (reset! val ""))]
    (fn [_]
      [:input {:type "text"
               :placeholder "Type your message here"
               :value @val
               :auto-focus true
               :on-change   #(reset! val (-> % .-target .-value))
               :on-key-down #(when (= (.-which %) 13) (send))}])))

(defn cljat-app []
  [:div
   (when (seq @(subscribe [:messages]))
     [messages])
   [new-message]])
