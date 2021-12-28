(ns alati.pages
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [alati.db :as db]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [hiccup.page :refer [html5 include-css include-js]]))

;Basic template for all pages, this is good practice
(defn basePageTemplate [& body]
  (html5
    [:head [:title "Project-Articles"]
     ;from: https://www.bootstrapcdn.com/
     [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" :integrity "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" :crossorigin "anonymous"}]
     [:link {:rel "stylesheet"
             :href "/blogstyle.css"}]]
    [:body {:style "background-color:#FFFFFF"}
     [:nav.navbar.navbar-expand-sm.bg-dark.navbar-dark
      [:div.container-fluid
       [:ul.navbar-nav
        [:li.nav-item
         [:a.nav-link.active {:href "/"} "Home page"]]
        [:li.nav-item
         [:a.nav-link {:href "/articles/new"} "New article"]]
        [:li.nav-item
         [:a.nav-link {:href "/admin/login"} "Login"]]
        [:li.nav-item
         [:a.nav-link {:href "/admin/logout"} "Logout"]]] ]]
     [:nav.navbar.navbar-expand-sm.bg-light.navbar-light
      [:div.container-fluid
       [:ul.navbar-nav
        [:li.nav-item
         [:a.nav-link.active {:href "/"} "All articles"]]
        [:li.nav-item
         [:div.dropdown
          [:button.dropbtn "Filter articles\n"]
          [:div.dropdown-content
           [:a {:href "/truenews"} "   True articles "]
           [:a {:href "/fakenews"} "Fake articles "]
           ]]]
        [:li.nav-item
         [:a.nav-link {:href "/blogstatistics"} "Blog statistics"]]
        ] ]
      ]body
     ]))

(defn blogstatistics []
  (basePageTemplate

    [:table {:border "1" :width "100%"} 
   [:tr
    [:th "Statistic"]
    [:th "Number"]
   ]
   [:tr
    [:td  "Number of articles"]
    [:td (db/countArticles)]
   ]
   [:tr
    [:td "Number of articles from BLIC portal:"]
    [:td (db/countArticlesFromPortal "Blic")]
    ]
   [:tr
    [:td "Number of articles from POLITIKA portal:"]
    [:td (db/countArticlesFromPortal "Politika")]
    ]
   [:tr
    [:td "Number of articles from RTS portal:"]
    [:td (db/countArticlesFromPortal "Rts")]
    ]
   [:tr
    [:td "Number of TRUE articles (TRUE NEWS) :"]
    [:td (db/countTrueArticles)]
    ]
   [:tr
       [:td "Number of FALSE articles (FAKE NEWS) :"]
       [:td (db/countFakeArticles)]
       ]



   ]))

;mac lentght for text of every articles shown on index page as preview
(def previewLengthForArticles 500)

;private function for trimming text used on index page for previewing articles
(defn- trimText [text] (if (> (.length text) previewLengthForArticles)
                         (subs text 0 previewLengthForArticles)
                         text))

;index page which displays all articles using hiccup pages
(defn index [articles]
  (basePageTemplate
                    (for [a articles]
                      [ :div.container.p-5.my-5.border
                       [:h2 [:a {:href (str "/articles/" (:_id a))} (:title a)]]
                       [:p (-> a :body trimText )]
                       ]
                      )))


(defn onlyTrueNews [articles]   ;as paramether filtered list
  (basePageTemplate
                     (for [a articles]
                       [ :div.container.p-5.my-5.border
                        [:h2 [:a {:href (str "/articles/" (:_id a))} (:title a)]]
                        [:p (-> a :body trimText )]
                        ]
                       )
                     ))

(defn onlyFakeNews [articles]                               ;as paramether filtered list
  (basePageTemplate
                     (for [a articles]
                       [ :div.container.p-5.my-5.border
                        [:h2 [:a {:href (str "/articles/" (:_id a))} (:title a)]]
                        [:p (-> a :body trimText )]
                        ]
                       )
                     ))

;Page for articles
(defn article [a  comments]
  (basePageTemplate
    [ :div.container.p-5.my-5.border
     (form/form-to [:delete (str "/articles/" (:_id a))]

                   (anti-forgery-field)
                   [:a.btn.btn-primary {:href (str "/articles/" (:_id a) "/edit")} "Edit"]
                   (form/submit-button {:class "btn btn-danger"} "Delete"))
                    [:small (:created a)]
                    [:h1 (:title a)]
                    [:p (:body a)]
     (if (= (get a :author) "")
       [:small (str "Author: unknown")]
       [:small (str "Author: " (:author a)) ]
      )
     ;[:small (str "Author: " (:author a)) ]
     [:br]
     ;[:small ((db/findPortalById (:portal a)) :name)]
     ;  [:small (str "Portal: " ((db/findPortalById (:portal a)) :name)) ]
     [:small (str "Portal: " (:portal a)) ]
     [:br]
     [:small (str "Tag: " (:tag a)) ]
     [:br]
     ]
    [:div.container.p-5.my-5.border
     ;comments
     [:h5 (str "Comments                                                                        ") [:a.btn.btn-primary {:href (str "/comments/" (:_id a) "/newcomment")} "New comment"]]

     [:br]

     [:p  (for [c comments]
                [:p (str "Reader " (get c :user) " write comment:   " (get c :text))]
                )]]
    ))

;Some repl testing
;(get (into [] (for [c (alati.db/findCommentsByArticleId "61c0955dbc538430a4acda76")]
;       ( str "Reader:" (get c :user )" write comment: " (get c :text) ))) 0)
;=> "Reader:Nikolina Maric write comment: I don't like this post"

;(for [c (alati.db/findCommentsByArticleId "61c0955dbc538430a4acda76")]
;  ( str "Reader:" (get c :user )" write comment: " (get c :text) ))
;=> ("Reader:Nikolina Maric write comment: I don't like this post" "Reader:Maja Nikolic write comment: I like this post")


;Edit article or create an article if doesn't exist
(defn editArticle [a]
  (basePageTemplate
    (form/form-to
      [:post (if a
               (str "/articles/" (:_id a))
               "/articles")]

      [:div.container.p-5.my-5.border
       (form/label "title" "Title")
       (form/text-field {:class "form-control"} "title" (:title a))
       [:br]

       (form/label "body" "Body")
       (form/text-area {:class "form-control"} "body" (:body a))
       (form/label "author" "Author")
       (form/text-area {:class "form-control"} "author" (:author a))
       [:br]
       (form/label "portal" "Portal")

       (form/drop-down {:class "form-control"} "portal" (into [] (for [p (alati.db/returnAllPortals)]
                                                                   (get p :name ) )))
       ;(form/text-area {:class "form-control"} "portal" (:portal a))
       ;(form/text-area {:class "form-control"}  "portal" ((db/findPortalById (:portal a)) :name))
       [:br]
       (form/label "tag" "Tag")
       (form/drop-down {:class "form-control"} "tag" [["True" "true"] ["False" "false"]] (:tag a))
       [:br]
       [:br]
       (anti-forgery-field)
       (form/submit-button {:class "btn btn-primary"} "Save")]
      )
    )
  )

(defn addComment [c article-id]                                        ; i need for which article
  (basePageTemplate
    (form/form-to
      [:post (if article-id
               (str "/comments/" article-id )
               (str "/comments"))]
      [:div.container.p-5.my-5.border
       (form/label "user" "User")
       (form/text-field {:class "form-control"} "user" (:user c))
       [:br]
       (form/label "text" "Text")
       (form/text-area {:class "form-control"} "text" (:text c))
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