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
           (GET "/truenews" [] (pages/onlyTrueNews (db/findTrueNews)))
           (GET "/fakenews" [] (pages/onlyFakeNews (db/findFakeNews)))
           (GET "/articles/:article-id" [article-id] (pages/article (db/returnArticleById article-id)))
           (GET "/admin/login" [:as {session :session}]
             (if (:admin session)
               (response/redirect "/")
               (pages/adminLogin)))
           (POST "/admin/login" [username password] (if (admin/adminLogin username password)
                                                      (-> (response/redirect "/")
                                                          (assoc-in [:session :admin] true))
                                                      (pages/adminLogin "INVALID USERNAME OR PASSWORD! TRY AGAIN!")))
           (GET "/admin/logout" [] (-> (response/redirect "/")
                                       (assoc-in [:session :admin] false)))

           ;  (GET "/comments/new" [] (pages/addComment nil ))
           (GET "/comments/:article-id/newcomment" [article-id] (pages/addComment nil article-id)) ;opens ok
           ;(POST "/comments" [user article-id text ] (do (db/createComment user article-id text) (response/redirect "/")))
           (POST "/comments/:article-id" [user article-id text] (do (db/createComment user article-id text) (response/redirect "/")))

           (route/not-found "Not Found"))

(defroutes adminRoutes
           ;routes for creating new article, only admin can do it
           (GET "/articles/new" [] (pages/editArticle nil))
           (POST "/articles" [title body  author portal tag
                              ] (do (db/createArticle title body author portal tag) (response/redirect "/")))
           ;routes for editing articles, only admin can do it
           (GET "/articles/:article-id/edit" [article-id] (pages/editArticle (db/returnArticleById article-id)))
           (POST "/articles/:art-id" [art-id title body author portal tag] (do (db/updateArticle art-id title body author portal tag) (response/redirect (str "/articles/" art-id))))
           ;deleting article
           (DELETE "/articles/:art-id" [art-id] (do (db/deleteArticle art-id) (response/redirect "/")))
           (GET "/blogstatistics" [] (pages/blogstatistics)))

;middleware for admin
(defn wrapAdmin [handler]
  (fn [request]
    (if (-> request :session :admin)                        ;if user is admin
      (handler request)                                     ;then call handler
      (response/redirect "/admin/login")                    ;else open login page
                                     )))
(def app
  (-> (routes (wrap-routes adminRoutes wrapAdmin)  app-routes)
      (wrap-defaults site-defaults)
      session/wrap-session) )

