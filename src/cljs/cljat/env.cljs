(ns cljat.env
  (:require
   [cemerick.url :refer [url]]))

(goog-define custom-api-url false)
(def api-url (or custom-api-url (str (url (-> js/window .-location .-href) "/"))))
