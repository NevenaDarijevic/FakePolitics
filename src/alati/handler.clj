(ns alati.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [alati.db :as db]
            [alati.pages :as pages]
  ))


(defroutes app-routes
  (GET "/" [] (pages/index (db/returnAllArticles)))
   (GET "/articles/:article-id" [articleID] (pages/article {db/returnArticleById articleID}))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
