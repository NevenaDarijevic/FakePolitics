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
         [:a.nav-link.active {:href "/"} "Home page (all articles)"]]
        [:li.nav-item
         [:a.nav-link {:href "/articles/new"} "New article"]]
        [:li.nav-item
         [:a.nav-link.right  {:href "/admin/login"} "Login"]]
        [:li.nav-item
         [:a.nav-link {:href "/admin/logout"} "Logout"]]

        ] ]]


     [:nav.navbar.navbar-expand-sm.bg-light.navbar-light
      [:div.container-fluid
       [:ul.navbar-nav
        [:li.nav-item
         [:div.dropdown
          [:button.dropbtn "Filter articles"]
          [:div.dropdown-content
           [:a {:href "/truenews"} "   True articles "]
           [:a {:href "/fakenews"} "Fake articles "]]] ]
         [:li.nav-item
           [:div.dropdown
           [:button.dropbtn" Filter by portals"]
           [:div.dropdown-content
             [:a {:href "/filterbyportals/Blic"} "Blic"]

             [:a {:href "/filterbyportals/Rts"} "Rts"]

             [:a {:href "/filterbyportals/Politika"} "Politika"]]]]

        [:li.nav-item
         [:a.nav-link {:href "/blogstatistics"} "Blog statistics"]]
        [:li.nav-item
         [:a.nav-link {:href "/articles/reportfake"} "Report fake news"]]
        [:li.nav-item
         [:a.nav-link {:href "/articles/reported"} "View reports"]]
        ] ]
      ]body
     ]))

(defn blogstatistics []
  (basePageTemplate
    [:style "#stat {
  font-family: Arial, Helvetica, sans-serif;
  border-collapse: collapse;
  width: 70%;
}

#stat td, #stat th {
  border: 1px solid #ddd;
  padding: 8px;
}

#stat tr:nth-child(even){background-color: #f2f2f2;}

#stat tr:hover {background-color: #ddd;}

#stat th {
  padding-top: 12px;
  padding-bottom: 12px;
  text-align: left;
  background-color: #61C0DF;
  color: white;
}"] [:div {:style "margin-left:300px"} [:body
     [:br]
                 [:h1 {:style "margin-left: 150px; color: #61C0DF;"}"Blog statistics table"]
     [:br]
                 [:table#stat
                  [:tr
                   [:th "Statistic"]
                   [:th "Number"]]
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
                  [:tr
                   [:td "Portal with the most fake news:"]
                   [:td (db/findMaxFake)]
                   ]
                  [:tr
                   [:td "Portal with the most truthful news:"]
                   [:td (db/findMaxTrue)]
                   ]
                  [:tr
                   [:td "Number of reports to check:"]
                   [:td (db/countReports)]
                   ]
                  ]]]))

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
                       [:h2  (:title a)]
                       [:p (-> a :body trimText )]
                       [:a.link {:href (str "/articles/" (:_id a))}"Read more"]
                       ]
                      )))


(defn onlyTrueNews [articles]   ;as paramether filtered list
  (basePageTemplate [:h3 {:style "margin: 35px; color: #61C0DF;"} "True news"]
                     (for [a articles]
                       [ :div.container.p-5.my-5.border
                        [:h2 [:a {:href (str "/articles/" (:_id a))} (:title a)]]
                        [:p (-> a :body trimText )]
                        ]
                       )
                     ))

(defn onlyFakeNews [articles]                               ;as paramether filtered list
  (basePageTemplate [:h3 {:style "margin: 35px; color: #61C0DF;"} "Fake news               "   [:a.btn.btn-primary {:href  "https://www.youtube.com/watch?v=AkwWcHekMdo&list=PLkdPn_rERIsmV4jWxQlq_rN_XmezcKcur" :target "_blank"} "How to recognize fake news"]
                     ]
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
     [:h5 (str "Comments ") [:a.btn.btn-primary {:href (str "/comments/" (:_id a) "/newcomment")} "New comment"]]

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
  (basePageTemplate [:br] [:h3 {:style "margin-left: 600px; color: #61C0DF;"} "Article information"]
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
(defn reportfakenews [a]
  (basePageTemplate
    (form/form-to
      [:post (if a
               (str "/allfakenews/" (:link a))
               (str "/allfakenews"))]
      [:h5 {:style "margin: 35px; color: #61C0DF;"} "On this page, you can report news that you suspect could be fake, and our administrators will check it within a reasonable time.\n"  ]
      [:h6 {:style "margin: 35px; color: #61C0DF;"} [:a.link {:href "https://www.youtube.com/watch?v=AkwWcHekMdo&list=PLkdPn_rERIsmV4jWxQlq_rN_XmezcKcur" :target "_blank" }"How to recognize fake news?"]]
      [:div.container.p-5.my-5.border
       [:h3 "What is " [:strong "Fake News"]"?"]
      [:p "“Fake news” is a term that has come to mean different things to different people. At its core, we are defining “fake news” as those news stories that are false: the story itself is fabricated, with no verifiable facts, sources or quotes. Sometimes these stories may be propaganda that is intentionally designed to mislead the reader, or may be designed as “clickbait” written for economic incentives (the writer profits on the number of people who click on the story). In recent years, fake news stories have proliferated via social media, in part because they are so easily and quickly shared online."]
      [:h3 "Misinformation and Disinformation (other types of \"fake news\")"]
      [:p "The universe of “fake news” is much larger than simply false news stories. Some stories may have a nugget of truth, but lack any contextualizing details. They may not include any verifiable facts or sources. Some stories may include basic verifiable facts, but are written using language that is deliberately inflammatory, leaves out pertinent details or only presents one viewpoint. \"Fake news\" exists within a larger ecosystem of mis- and disinformation. \n\nMisinformation is false or inaccurate information that is mistakenly or inadvertently created or spread; the intent is not to deceive. Disinformation is false information that is deliberately created and spread \"in order to influence public opinion or obscure the truth\" (https://www.merriam-webster.com/dictionary/disinformation). \n\nClaire Wardle of FirstDraftNews.com has created the helpful visual image below to help us think about the ecosystem of mis- and disinformation. And as she points out, \"it's complicated.\""]
       [:img {:alt "7 types of mis/disinformation" :src "https://firstdraftnews.com/wp-content/uploads/2017/02/FDN_7Types_Misinfo-01-1024x576.jpg?x40896" :style "width: 1200px; height: 600px;"}]
      [:small "Source: " [:a.link {:href "https://guides.lib.umich.edu/fakenews" :target "_blank" }"Read article"]]
       ]
       [:div.container.p-5.my-5.border
        [:h3 {:style "margin-left: 550px; color: #61C0DF;"}  "Report form"]
        [:br]
           (form/label "link" "Site url")
          (form/text-field {:type "url":class "form-control"} "link" (:link a))
       [:br]

       (form/label "reason" "Reason for reporting")
       (form/text-area { :class "form-control"} "reason" (:reason a))
        [:br]
       (form/label "author" "Author")
       (form/text-area {:class "form-control"} "author" (:author a))
        [:br]
       (form/label "portal" "Portal")
       (form/drop-down {:class "form-control"} "portal" (into [] (for [p (alati.db/returnAllPortals)]
                                                                   (get p :name ) )))
       [:br]

       (anti-forgery-field)
       (form/submit-button {:class "btn btn-primary"} "Report")]
      )
    )
  )

(defn displayReported [reported]
  (basePageTemplate
    [:br]
    [:h3 {:style "margin-left: 600px; color: #61C0DF;"} "Reports from readers"]
    [:small {:style "margin-left: 690px; color: #61C0DF;"} (str "Count: " (db/countReports))]
 [:br]

    [:style "#report {
    table-layout: fixed;
      width: 1500px;
       font-family: Arial, Helvetica, sans-serif;
       border-collapse: collapse;

}

          #report td, #report th {
            border: 1px solid #ddd;
            padding: 8px;
          }

        #report tr:nth-child(even){background-color: #f2f2f2;}

        #report tr:hover {background-color: #ddd;}

        #report th {
          padding-top: 12px;
          padding-bottom: 12px;
          text-align: left;
          background-color: #61C0DF;
          color: white;
        }"]
    [:div {:style "margin-left:30px" "margin-bottom:30px" "margin-right:60px"} [:body  [:br] (for [r reported] [:table#report
                                                                       [:tr
                                                                        [:th "Link"]
                                                                        [:th "Reason"]
                                                                        [:th "Author"]
                                                                        [:th "Portal"]
                                                                        [:th "Action"]]
                                                                       [:tr
                                                                        [:td   (:link r)]
                                                                        [:td (:reason r)]
                                                                        [:td (:author r)]
                                                                        [:td (:portal r)]
                                                                        [:td (form/submit-button {:class "btn btn-danger"} "Reject")
                                                                         ]
                                                                        ]
                                                                       ]) ]]))

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
;(defn filterByPortals [portals]
;  (basePageTemplate     [:h3 {:style "margin: 35px; color: #61C0DF;"} "Filter articles by portal"]
;                    (form/form-to
;                      [:div.container.p-5.my-5.border
;                       [:br]
;                       (form/label "portal" "Portal")
;
;                       (form/drop-down {:class "form-control"} "portal" (into [] (for [p portals]
;                                                                                   (get p :name ) )))])))


(defn articlesForPortal [articles portal]                          ;fine
  (basePageTemplate
    [:br] [:h3 {:style "margin-left: 600px; color: #61C0DF;"} (str "Articles from " portal )]
    (if (= portal "Blic")
    [:img {:src "https://is5-ssl.mzstatic.com/image/thumb/Purple116/v4/75/06/ff/7506ff38-bfba-c5d1-0724-c9b57c1f91be/BlicIcon-1x_U007emarketing-0-9-0-85-220.png/1200x600wa.png" :style "width: 600px; height: 300px; margin-left: 390px; ":alt "Blic"}]
    (if (= portal "Rts")
      [:img {:src "data:image/png;base64,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" :style "width: 600px; height: 300px; margin-left: 390px; ":alt "Rts"}]
      [:img {:src "data:image/png;base64,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" :style "width: 600px; height: 300px; margin-left: 450px; ":alt "Politika"}]
    ) )
    [ :div.container.p-5.my-5.border
    (if (= portal "Blic")
    [:p {:style "margin-left: 50px; color: #61C0DF;"} [:strong "Blic (Cyrillic: Блиц, Blic) is a daily middle-market tabloid newspaper in Serbia.
    Founded in 1996, Blic is owned by Ringier Axel Springer Media AG, a joint venture between Ringier media corporation from Switzerland and Axel Springer
     AG from Germany." ] ]
      (if (= portal "Rts")
      [:p {:style "margin-left: 50px; color: #61C0DF;"} [:strong "Radio Television of Serbia (Serbian: Radio-televizija Srbije, Serbian Cyrillic: Радио-телевизија Србије;
      abbr. RTS/PTC) is Serbia's public broadcaster. It broadcasts and produces news, drama, and sports programming through radio, television and the Internet.
       RTS is a member of the European Broadcasting Union. Radio Television of Serbia has four organizational units - radio, television, music production, and
       record label (PGP-RTS). It is financed primarily through monthly subscription fees and advertising revenue."]]
        [:p {:style "margin-left: 50px; color: #61C0DF;"} [:strong "Politika (Serbian Cyrillic: Политика; Politics) is a Serbian daily newspaper, published in Belgrade.
         Founded in 1904 by Vladislav F. Ribnikar, it is the oldest daily newspaper still in circulation in the Balkans and is considered to be one of Serbia's
         newspapers of record."]]
        ) )]
    (for [a articles]
      [ :div.container.p-5.my-5.border
       [:h2  (:title a)]
       [:p (-> a :body trimText )]
       [:a.link {:href (str "/articles/" (:_id a))}"Read more"]
       ]
      )))


