(ns alati.db
  ;Guide: http://clojuremongodb.info/articles/getting_started.html
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer [$set]])
  (:import
    [org.bson.types ObjectId]
    (java.util Date)))


;Extracting collection of articles as variable because it is a good practice
(def articlesCollection "articles")
(def portalsCollection  "portals")
(def commentsCollection  "comments")
;Extracting connection uri
(def  dbConnectionUri  (or (System/getenv "alatiMongoUri")
                         "mongodb://127.0.0.1/alati-test"))

;Create a connection
(def db (-> dbConnectionUri
            mg/connect-via-uri
            :db))
;REPL Insert new values into articles
;(mc/insert alati.db/db "articles" {:title "First article" :body "Hello" :created (new java.util.Date)})
;=> #object[com.mongodb.WriteResult 0x1ab4fb64 "WriteResult{n=0, updateOfExisting=false, upsertedId=null}"]
;(mc/find-maps alati.db/db "articles")
;=>
;({:_id #object[org.bson.types.ObjectId 0xb2e784 "61b8743224950a19a40d3447"],
;  :title "First article",
;  :body "Hello",
;  :created #inst"2021-12-14T10:38:42.738-00:00"})

;Function which creates new article
(defn createArticle [title body author portal tag]
  (mc/insert db articlesCollection
             {:title   title
              :body    body
              :created (new Date)
              :author  author
              :portal  portal
              :tag     tag}))

(defn createComment [user article text ]
  (mc/insert db commentsCollection
             {:user   user
              :article    article
              :text text}))

;Testing in REPL
;(alati.db/createArticle "TestArticle" "...")

;Function which updates article
(defn updateArticle [art-id title body author portal tag]
  (mc/update-by-id db articlesCollection (ObjectId. art-id)
                   {$set{:title   title
                         :body    body
                         :author author
                         :portal portal
                         :tag tag
                         }}))

;Function which returns all articles
(defn returnAllArticles []
  (mc/find-maps db articlesCollection) )
;Testing in REPL
;(alati.db/returnAllArticles)

;Function which returns article by ID
(defn returnArticleById [article-id]
  (mc/find-map-by-id db articlesCollection (ObjectId. article-id))) ;ObjectID is for importing ID

;Delete article
(defn deleteArticle [article-id]
  (mc/remove-by-id db articlesCollection (ObjectId. article-id)))

(defn findPortalById [portal-id]
  (mc/find-map-by-id db portalsCollection (ObjectId. portal-id)))

(defn returnAllPortals []
  (mc/find-maps db portalsCollection)  )

(defn findCommentsByArticleId [article-id]
  (mc/find-maps db commentsCollection { :article article-id }) ;return lazy seq of maps
  )

(defn findTrueNews []
  (mc/find-maps db articlesCollection {:tag "true"} )) ;return lazy seq of maps)

(defn findFakeNews []
  (mc/find-maps db articlesCollection  {:tag "false"} )) ;return lazy seq of maps)

(defn findArticlesFromPortal [portal-name]
  (mc/find-maps db articlesCollection {:portal portal-name })
  )
(defn countArticles []
  (count(mc/find-maps db articlesCollection))
  )
(defn countArticlesFromPortal [portal-name]
  (count(mc/find-maps db articlesCollection {:portal portal-name }))
  )
(defn countTrueArticles []
  (count(mc/find-maps db articlesCollection {:tag "true"} )))
(defn countFakeArticles []
  (count(mc/find-maps db articlesCollection {:tag "false"} )))



