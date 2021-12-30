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
(def reportedNewsCollection  "reported")

;Extracting connection uri
(def  dbConnectionUri  (or (System/getenv "alatiMongoUri")
                         "mongodb://127.0.0.1/alati-test"))

;Create a connection
(def db (-> dbConnectionUri
            mg/connect-via-uri
            :db))
;Article functions
(defn createArticle [title body author portal tag]
  (mc/insert db articlesCollection
             {:title   title
              :body    body
              :created (new Date)
              :author  author
              :portal  portal
              :tag     tag}))

(defn updateArticle [art-id title body author portal tag]
  (mc/update-by-id db articlesCollection (ObjectId. art-id)
                   {$set{:title   title
                         :body    body
                         :author author
                         :portal portal
                         :tag tag
                         }}))

(defn returnAllArticles []
  (mc/find-maps db articlesCollection))

(defn returnArticleById [article-id]
  (mc/find-map-by-id db articlesCollection (ObjectId. article-id))) ;ObjectID is for importing ID


(defn deleteArticle [article-id]
  (mc/remove-by-id db articlesCollection (ObjectId. article-id)))

;Comments functions
(defn createComment [user article text ]
  (mc/insert db commentsCollection
             {:user   user
              :article    article
              :text text}))

(defn findCommentsByArticleId [article-id]
  (mc/find-maps db commentsCollection { :article article-id })) ;return lazy seq of maps

;Portals functions
(defn returnAllPortals []
  (mc/find-maps db portalsCollection))


;Functions for filtering articles
(defn findTrueNews []
  (mc/find-maps db articlesCollection {:tag "true"} )) ;return lazy seq of maps

(defn findFakeNews []
  (mc/find-maps db articlesCollection  {:tag "false"} )) ;return lazy seq of maps

(defn findArticlesFromPortal [portal-name]
  (mc/find-maps db articlesCollection {:portal portal-name }))

;Fuctions for statistics
(defn countArticles []
  (count(mc/find-maps db articlesCollection)))

(defn countArticlesFromPortal [portal-name]
  (count(mc/find-maps db articlesCollection {:portal portal-name })))

(defn countTrueArticles []
  (count(mc/find-maps db articlesCollection {:tag "true"} )))

(defn countFakeArticles []
  (count(mc/find-maps db articlesCollection {:tag "false"} )))

(defn countTrueArticlesForPortal [portal]
  (count(mc/find-maps db articlesCollection {:tag "true" :portal portal} )))

(defn countFalseArticlesForPortal [portal]
  (count(mc/find-maps db articlesCollection {:tag "false" :portal portal} )))

(defn findMaxFake []
  (let [maxFake (max (alati.db/countFalseArticlesForPortal "Blic") (alati.db/countFalseArticlesForPortal "Rts") (alati.db/countFalseArticlesForPortal "Politika"))]
    (if (= maxFake (alati.db/countFalseArticlesForPortal "Blic")) "Blic" (if (= maxFake (alati.db/countFalseArticlesForPortal "Rts")) "Rts" (if (= maxFake (alati.db/countFalseArticlesForPortal "Politika")) "Politika" ) ) )
    ))
(defn findMaxTrue []
  (let [maxTrue (max (alati.db/countTrueArticlesForPortal "Blic") (alati.db/countTrueArticlesForPortal "Rts") (alati.db/countTrueArticlesForPortal "Politika"))]
    (if (= maxTrue (alati.db/countTrueArticlesForPortal "Blic")) "Blic" (if (= maxTrue (alati.db/countTrueArticlesForPortal "Rts")) "Rts" (if (= maxTrue (alati.db/countTrueArticlesForPortal "Politika")) "Politika" ) ) )
    ))


;Functions for reporting fake news and reviewing reported news
(defn reportFakeNew [link reason author portal]
  (mc/insert db reportedNewsCollection
             {:link   link
              :reason  reason
              :author  author
              :portal  portal}))

(defn returnReported []
  (mc/find-maps db reportedNewsCollection)  )

(defn countReports []
  (count(mc/find-maps db reportedNewsCollection)))

(defn deleteReported [rep-id]
  (mc/remove-by-id db reportedNewsCollection (ObjectId. rep-id)))


