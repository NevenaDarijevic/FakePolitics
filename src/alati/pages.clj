(ns alati.pages
  (:require [hiccup.page :refer [html5]]                    ;https://github.com/weavejester/hiccup
            ))
;Creating of route for index page which displays all articles using hiccup pages
(defn index [articles]
  (html5 [:h1 "ARTICLES"]
         (for [a articles]
           [:h2 (:title a)])
         )
 )
