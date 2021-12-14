(ns alati.pages
  (:require [hiccup.page :refer [html5]]                    ;https://github.com/weavejester/hiccup
            ))

;Basic template for all pages, this is good practice
(defn basePageTemplate [& body]
  (html5
    [:head [:title "Project-Articles"]]
    [:body [:h1 "Articles"] body]))

;index page which displays all articles using hiccup pages
(defn index [articles]
  (basePageTemplate (for [a articles]
                      [:h2 (:title a)])))

;Page for articles
(defn article [article]
  (basePageTemplate [:small (:created article)]
                    [:h1 (:title article)]
                    [:p (:body article)]))
