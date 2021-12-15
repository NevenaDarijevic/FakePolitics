(ns alati.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [alati.db :as db]
            [alati.pages :as pages]
            [ring.util.response :as response ]
            [ring.middleware.session :as session]
            [alati.admin :as admin]
  ))


(defroutes app-routes
  (GET "/" [] (pages/index (db/returnAllArticles)))
  (GET "/articles/:article-id" [article-id] (pages/article (db/returnArticleById article-id)))

  (GET "/admin/login" [:as {session :session}]
    (if (:admin session)
      (response/redirect "/")
      (pages/adminLogin)))
   (POST "/admin/login" [username password] (if (admin/adminLogin username password)
                                              (-> (response/redirect "/")
                                                  (assoc-in [:session :admin] true))
                                              (pages/adminLogin)))
     (GET "/admin/logout" [] (-> (response/redirect "/")
                                       (assoc-in [:session :admin] false)))
   (route/not-found "Not Found"))

(defroutes adminRoutes
           ;routes for creating new article, only admin can do it
           (GET "/articles/new" [] (pages/editArticle nil))
           (POST "/articles" [title body] (do (db/createArticle title body) (response/redirect "/")))
           ;routes for editing articles, only admin can do it
           (GET "/articles/:article-id/edit" [article-id] (pages/editArticle (db/returnArticleById article-id)))
           (POST "/articles/:art-id" [art-id title body] (do (db/updateArticle art-id title body) (response/redirect (str "/articles/" art-id)))))

;middleware for admin
(defn wrapAdmin [handler]
  (fn [request]
    (if (-> request :session :admin)                        ;if user is admin
      (handler request)                                     ;then call handler
      (response/redirect "/admin/login")                    ;else open login page
                                     )))
(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      session/wrap-session) )

