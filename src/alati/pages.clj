(ns alati.pages
  (:require [hiccup.page :refer [html5]]                    ;https://github.com/weavejester/hiccup
            ))

;Basic template for all pages, this is good practice
(defn basePageTemplate [& body]
  (html5
    [:head [:title "Project-Articles"]]
    [:body [:a {:href "/"}]
     [:h1 "Articles"] body]))

;index page which displays all articles using hiccup pages
(defn index [articles]
  (basePageTemplate (for [a articles]
                      [:h2 [:a {:href (str "/articles/" (:_id a))} (:title a)]])))

;Page for articles
(defn article [a]
  (basePageTemplate [:small (:created a)]
                    [:h1 (:title a)]
                    [:p (:body a)]
                    ))

