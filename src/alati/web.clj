(ns alati.web
  (:require
    [ring.adapter.jetty :as jetty]
    [compojure.handler :as handler]
    [alati.handler :as alatii])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer. (or (System/getenv "articlesBlogPORT")
                           3006))]
    (jetty/run-jetty (handler/site #'alatii/app)
                     {:port port
                      :join? false})))