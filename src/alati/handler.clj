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


(defroutes app-routes                                       ;reader routes
           (GET "/" [] (pages/indexReader (db/returnAllArticles)))
           (GET "/articles/reportfake" [] (pages/reportfakenews nil))
           (POST "/allfakenews/:link" [link reason author portal] (do (db/reportFakeNew link reason author portal) (response/redirect "/")))
           (POST "/allfakenews" [link reason author portal] (do (db/reportFakeNew link reason author portal) (response/redirect "/")))
           (GET "/articlesReader/:article-id" [article-id] (pages/articleReader (db/returnArticleById article-id) (db/findCommentsByArticleId article-id)))

           (GET "/truenews" [] (pages/onlyTrueNews (db/findTrueNews)))
           (GET "/fakenews" [] (pages/onlyFakeNews (db/findFakeNews)))
           (GET "/filterbyportals/:portal-name" [portal-name] [] (pages/articlesForPortal (db/findArticlesFromPortal portal-name) portal-name))
           (GET "/admin/login" [:as {session :session}]
             (if (:admin session)
               (response/redirect "/")
               (pages/adminLogin)))
           (POST "/admin/login" [username password] (if (admin/adminLogin username password)
                                                      (-> (response/redirect "/indexadmin")
                                                          (assoc-in [:session :admin] true))
                                                      (pages/adminLogin "INVALID USERNAME OR PASSWORD! TRY AGAIN!")))
           (GET "/admin/logout" [] (-> (response/redirect "/")
                                       (assoc-in [:session :admin] false)))

           ;  (GET "/comments/new" [] (pages/addComment nil ))

           (GET "/comments/:article-id/newcomment" [article-id] (pages/addComment nil article-id)) ;opens ok
           ;(POST "/comments" [user article-id text ] (do (db/createComment user article-id text) (response/redirect "/")))
           (POST "/comments/:article-id" [user article-id text] (do (db/createComment user article-id text) (response/redirect (str "/articlesReader/" article-id))))

           (route/not-found "Not Found"))

(defroutes adminRoutes
           (GET "/indexadmin" [] (pages/indexAdmin (db/returnAllArticles)))
           (GET "/articlesAdmin/:article-id" [article-id] (pages/articleAdmin (db/returnArticleById article-id)))
           ;routes for creating new article, only admin can do it
           (GET "/articles/new" [] (pages/editArticle nil))
           (POST "/articles" [title body  author portal tag
                              ] (do (db/createArticle title body author portal tag) (response/redirect "/indexadmin")))
           ;routes for editing articles, only admin can do it
           (GET "/articles/:article-id/edit" [article-id] (pages/editArticle (db/returnArticleById article-id)))
           (POST "/articlesAdmin/:art-id" [art-id title body author portal tag] (do (db/updateArticle art-id title body author portal tag) (response/redirect (str "/articlesAdmin/" art-id))))
           ;deleting article
           (DELETE "/articles/:art-id" [art-id] (do (db/deleteArticle art-id) (response/redirect "/indexadmin")))
           (GET "/blogstatistics" [] (pages/blogstatistics))

           (GET "/articles/reported" [] (pages/displayReported (db/returnReported)))
           (DELETE "/articles/reported/:art-id" [rep-id] (do (db/deleteReported rep-id) (response/redirect "/articles/reported")))
           )

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

