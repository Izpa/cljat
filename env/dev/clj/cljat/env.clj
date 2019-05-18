(ns cljat.env
  (:require
   [selmer.parser :as parser]
   [clojure.tools.logging :as log]
   [cljat.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[cljat started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[cljat has shut down successfully]=-"))
   :middleware wrap-dev})
