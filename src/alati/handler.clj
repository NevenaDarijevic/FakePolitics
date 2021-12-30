(ns alati.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [alati.db :as db]
            [alati.pages.pagesAdmin :as Admin]
            [alati.pages.pagesReader :as Reader]
            [ring.util.response :as response ]
            [ring.middleware.session :as session]
            [alati.admin :as admin]
  ))


(defroutes app-routes                                       ;reader routes
           ;home page reader and app home page
           (GET "/" [] (Reader/indexReader (db/returnAllArticles)))
           ;report fake news
           (GET "/articles/reportfake" [] (Reader/reportfakenews nil))
           (POST "/allfakenews/:link" [link reason author portal] (do (db/reportFakeNew link reason author portal) (response/redirect "/")))
           (POST "/allfakenews" [link reason author portal] (do (db/reportFakeNew link reason author portal) (response/redirect "/")))
           ;display article with comments
           (GET "/articlesReader/:article-id" [article-id] (Reader/articleReader (db/returnArticleById article-id) (db/findCommentsByArticleId article-id)))
           ;filter news: true and fake
           (GET "/truenews" [] (Reader/onlyTrueNews (db/findTrueNews)))
           (GET "/fakenews" [] (Reader/onlyFakeNews (db/findFakeNews)))
           ;filter news by portal
           (GET "/filterbyportals/:portal-name" [portal-name] [] (Reader/articlesForPortal (db/findArticlesFromPortal portal-name) portal-name))
           ;login option
           (GET "/admin/login" [:as {session :session}]
             (if (:admin session)
               (response/redirect "/")
               (Reader/adminLogin)))
           (POST "/admin/login" [username password] (if (admin/adminLogin username password)
                                                      (-> (response/redirect "/indexadmin")
                                                          (assoc-in [:session :admin] true))
                                                      (Reader/adminLogin "INVALID USERNAME OR PASSWORD! TRY AGAIN!")))
           (GET "/admin/logout" [] (-> (response/redirect "/")
                                       (assoc-in [:session :admin] false)))
           ;add comment to article
           (GET "/comments/:article-id/newcomment" [article-id] (Reader/addComment nil article-id)) ;opens ok
           (POST "/comments/:article-id" [user article-id text] (do (db/createComment user article-id text) (response/redirect (str "/articlesReader/" article-id))))
           (route/not-found "Not Found"))

(defroutes adminRoutes
           ;home page for admin
           (GET "/indexadmin" [] (Admin/indexAdmin (db/returnAllArticles)))
           ;display article
           (GET "/articlesAdmin/:article-id" [article-id] (Admin/articleAdmin (db/returnArticleById article-id)))
           ;routes for creating new article, only admin can do it
           (GET "/articles/new" [] (Admin/editArticle nil))
           (POST "/articles" [title body  author portal tag
                              ] (do (db/createArticle title body author portal tag) (response/redirect "/indexadmin")))
           ;routes for editing articles, only admin can do it
           (GET "/articles/:article-id/edit" [article-id] (Admin/editArticle (db/returnArticleById article-id)))
           (POST "/articlesAdmin/:art-id" [art-id title body author portal tag] (do (db/updateArticle art-id title body author portal tag) (response/redirect (str "/articlesAdmin/" art-id))))
           ;deleting article
           (DELETE "/articles/:art-id" [art-id] (do (db/deleteArticle art-id) (response/redirect "/indexadmin")))
           ;stat
           (GET "/blogstatistics" [] (Admin/blogstatistics))
           ;reported news
           (GET "/articles/reported" [] (Admin/displayReported (db/returnReported)))
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

