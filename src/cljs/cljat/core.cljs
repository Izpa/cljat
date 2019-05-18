(ns cljat.core
  (:require [goog.events :as events]
            [reagent.core :as reagent]
            [re-frame.core :as reframe]
            [cljat.events]
            [cljat.subs]
            [cljat.views]))

(enable-console-print!)

(reframe/dispatch-sync [:initialise-db])

(defn init! []
  (reframe/clear-subscription-cache!)
  (reagent/render [cljat.views/cljat-app]
                  (.getElementById js/document "app")))
