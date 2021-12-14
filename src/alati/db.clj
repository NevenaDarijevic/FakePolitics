(ns alati.db

  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import (java.util Date)))


(def db (-> "mongodb://127.0.0.1/alati-test"
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
(defn createArticle [title body]
  (mc/insert db "articles"
             {:title   title
              :body    body
              :created (new Date)}))

;Testing in REPL
;(alati.db/createArticle "TestArticle" "...")

;Function which returns all articles
(defn returnAllArticles []
  (mc/find-maps db "articles") )
;Testing in REPL
;(alati.db/returnAllArticles)