(ns cljat.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[cljat started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[cljat has shut down successfully]=-"))
   :middleware identity})
