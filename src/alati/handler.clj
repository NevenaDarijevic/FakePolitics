(ns alati.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [alati.db :as db]
            [alati.pages :as pages]
  ))


(defroutes app-routes
  (GET "/" [] (pages/index (db/returnAllArticles)))
  (GET "/articles/:article-id" [article-id] (pages/article (db/returnArticleById article-id)))
  (GET "/articles/new" [] (pages/editArticle nil))          ;when argument is nil then we create new article in function editArticle
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
