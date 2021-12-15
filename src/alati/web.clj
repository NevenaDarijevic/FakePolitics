(ns alati.web
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as com]
            [alati.handler :as blogArticles]
            ))
(defn -main [& args]
  (let [port (Integer. (or (System/getenv "articlesBlogPORT") 3006))] ;set default port
(jetty/run-jetty (com/site #'blogArticles/app) {:port port :join? false})))