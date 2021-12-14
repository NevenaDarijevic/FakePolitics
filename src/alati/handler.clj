(ns alati.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [alati.db :as db]
  ))

;Creating of route for index page which displays all articles
(defn index [_]
  (->> (db/returnAllArticles)
       (map #(str "<h2>" (:title %) "</h2>"))
       (apply str  "<h1>Testni naslov </h1>")))

(defroutes app-routes
  (GET "/" [] index)
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
