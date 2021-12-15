(ns alati.pages
  (:require [hiccup.page :refer [html5]]                    ;https://github.com/weavejester/hiccup
            [hiccup.form :as form]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            )
 )

;Basic template for all pages, this is good practice
(defn basePageTemplate [& body]
  (html5
    [:head [:title "Project-Articles"]]
    [:body
     [:a {:href "/"} [:h1 "List of all articles"]]
     [:a {:href  "/articles/new"} "New article"]
     [:hr]
     body]))

;index page which displays all articles using hiccup pages
(defn index [articles]
  (basePageTemplate (for [a articles]
                      [:h2 [:a {:href (str "/articles/" (:_id a))} (:title a)]])))

;Page for articles
(defn article [a]
  (basePageTemplate
                    [:a {:href (str "/articles/" (:_id a) "/edit")} "Edit article"]
                    [:hr]
                    [:small (:created a)]
                    [:h1 (:title a)]
                    [:p (:body a)]
                    )
  )

;Edit article or create an article if doesn't exist
(defn editArticle [a]
  (basePageTemplate
    (form/form-to
      [:post (if a
               (str "/articles/" (:_id a))
               "/articles")]

       (form/label "title" "Title")
       (form/text-field  "title" (:title a))


       (form/label "body" "Body")
       (form/text-area  "body" (:body a))


      (anti-forgery-field)
      (form/submit-button  "Save")
      )
    )
  )

;Login page
(defn adminLogin []
  (basePageTemplate
    (form/form-to
      [:post "/admin/login"]

      (form/label "username" "Username")
      (form/text-field "username")

      (form/label "password" "Password")
      (form/password-field "password")

      (anti-forgery-field)
      (form/submit-button "Login"))))