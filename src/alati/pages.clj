(ns alati.pages
  (:require [hiccup.page :refer [html5]]                    ;https://github.com/weavejester/hiccup
            [hiccup.form :as form]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            )
 )

;Basic template for all pages, this is good practice
(defn basePageTemplate [& body]
  (html5
    [:head [:title "Project-Articles"]
     ;from: https://www.bootstrapcdn.com/
     [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" :integrity "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" :crossorigin "anonymous"}]]
    [:body
     [:div.container
      [:nav.navbar.navbar-expand-lg.navabr-light.bd-light
       [:a.navbar-brand {:href "/"} "List of all articles"]
       [:div.navbar-nav.ml-auto
        [:a.nav-item.nav-link {:href  "/articles/new"} "New article"]
        [:a.nav-item.nav-link {:href  "/admin/login"} "LogIN"]
        [:a.nav-item.nav-link {:href  "/admin/logout"} "LogOUT"]
        ]]
      body]]))

;mac lentght for text of every articles shown on index page as preview
(def previewLengthForArticles 500)

;private function for trimming text used on index page for previewing articles
(defn- trimText [text] (if (> (.length text) previewLengthForArticles)
                         (subs text 0 previewLengthForArticles)
                         text))

;index page which displays all articles using hiccup pages
(defn index [articles]
  (basePageTemplate (for [a articles]
                      [:div
                       [:h2 [:a {:href (str "/articles/" (:_id a))} (:title a)]]
                       [:p (-> a :body trimText)]
                       ]
                     )))

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
(defn adminLogin [ & [message]]
  (basePageTemplate
    (when message [:div.alert.alert-danger message])
    (form/form-to
      [:post "/admin/login"]

      [:div.form-group
       (form/label "username" "Username")
       (form/text-field {:class "form-control"} "username")]

      [:div.form-group
       (form/label "password" "Password")
       (form/password-field {:class "form-control"}  "password")]


      (anti-forgery-field)
      (form/submit-button {:class "btn btn-primary"}  "Login"))))