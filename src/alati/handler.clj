(ns alati.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [alati.db :as db]
            [alati.pages :as pages]
            [ring.util.response :as response ]
  ))


(defroutes app-routes
  (GET "/" [] (pages/index (db/returnAllArticles)))
  (GET "/articles/:article-id" [article-id] (pages/article (db/returnArticleById article-id)))
  (GET "/articles/:article-id/edit" [article-id] (pages/editArticle (db/returnArticleById article-id)))
  (GET "/articles/new" [] (pages/editArticle nil))
   (POST "/articles" [title body] (do (db/createArticle title body)
                                   (response/redirect "/")           ))
   (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
