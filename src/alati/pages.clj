(ns alati.pages
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [alati.db :as db]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

;Basic template for all pages, this is good practice
(defn basePageTemplate [& body]
  (html5
    [:head [:title "Project-Articles"]
     ;from: https://www.bootstrapcdn.com/
     [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" :integrity "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" :crossorigin "anonymous"}]]
    [:body
     [:nav.navbar.navbar-expand-sm.bg-dark.navbar-dark
      [:div.container-fluid
       [:ul.navbar-nav
        [:li.nav-item
         [:a.nav-link.active {:href "/"} "All articles"]]
        [:li.nav-item
         [:a.nav-link {:href "/articles/new"} "New article"]]
        [:li.nav-item
         [:a.nav-link {:href "/admin/login"} "Login"]]
        [:li.nav-item
         [:a.nav-link {:href "/admin/logout"} "Logout"]]] ]] body
     ]))

;mac lentght for text of every articles shown on index page as preview
(def previewLengthForArticles 500)

;private function for trimming text used on index page for previewing articles
(defn- trimText [text] (if (> (.length text) previewLengthForArticles)
                         (subs text 0 previewLengthForArticles)
                         text))

;index page which displays all articles using hiccup pages
(defn index [articles]
  (basePageTemplate (for [a articles]
                      [ :div.container.p-5.my-5.border
                       [:h2 [:a {:href (str "/articles/" (:_id a))} (:title a)]]
                       [:p (-> a :body trimText )]
                       ]
                     )))

;Page for articles
(defn article [a]
  (basePageTemplate
    [ :div.container.p-5.my-5.border
     (form/form-to [:delete (str "/articles/" (:_id a))]

                   (anti-forgery-field)
                   [:a.btn.btn-primary {:href (str "/articles/" (:_id a) "/edit")} "Edit"]
                   (form/submit-button {:class "btn btn-danger"} "Delete"))
                    [:small (:created a)]
                    [:h1 (:title a)]
                    [:p (:body a)]
     [:small (:author a)]
     [:br]
     ; [:small ((db/findPortalById (:portal a)) :name)]
     [:small (:portal a)]
     [:br]
     [:small (:tag a)]
     ]
    [:div.container.p-5.my-5.border
     ;comments
     [:h5 (str "Comments")]
     (def sizeComments (count (db/findCommentsByArticleId (:_id a))))
     (for [x (range sizeComments)]
       (:small (.get (.get (into '() (db/findCommentsByArticleId (:_id a))) x) :user)
          )
       (:small sizeComments))
     ]))

;Edit article or create an article if doesn't exist
(defn editArticle [a]
  (basePageTemplate
    (form/form-to
      [:post (if a
               (str "/articles/" (:_id a))
               "/articles")]

      [ :div.container.p-5.my-5.border
       (form/label "title" "Title")
       (form/text-field {:class "form-control"} "title" (:title a))
       [:br]

       (form/label "body" "Body")
       (form/text-area {:class "form-control"}  "body" (:body a))
       (form/label "author" "Author")
       (form/text-area {:class "form-control"}  "author" (:author a))
       [:br]
       (form/label "portal" "Portal")
       (form/text-area {:class "form-control"}  "portal" (:portal a))
       ;(form/text-area {:class "form-control"}  "portal" ((db/findPortalById (:portal a)) :name))
       [:br]
       (form/label "tag" "tag")
       (form/text-area {:class "form-control"}  "tag" (:tag a))
       [:br]
       [:br]
      (anti-forgery-field)
      (form/submit-button {:class "btn btn-primary"} "Save")]
      )
    )
  )

;Login page
(defn adminLogin [ & [message]]
  (basePageTemplate
    (when message [:div.alert.alert-danger message])
    (form/form-to
      [:post "/admin/login"]


      [:div.container.p-5.my-5.border
       [:label.form-label {:for "email"} "Email:"]
       [:input#email.form-control {:type "username" :placeholder "Enter username" :name "username"}]
       [:label.form-label {:for "pwd"} "Password:"]
       [:input#pwd.form-control {:type "password" :placeholder "Enter password" :name "password"}]
       [:div.form-check.mb-3
        [:label.form-check-label
         [:input.form-check-input {:type "checkbox" :name "remember"}] "Remember me"]]
       (anti-forgery-field)
       (form/submit-button {:class "btn btn-primary"}  "Login")]
)))