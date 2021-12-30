(ns alati.pagesAdmin
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [alati.db :as db]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [hiccup.page :refer [html5 include-css include-js]]))

;Basic template for all pages for admin
(defn basePageTemplateAdmin [& body]
  (html5
    [:head [:title "Project-Articles"]
     [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" :integrity "sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" :crossorigin "anonymous"}]
     [:link {:rel "stylesheet"
             :href "/blogstyle.css"}]]
    [:body {:style "background-color:#FFFFFF"}
     [:nav.navbar.navbar-expand-sm.bg-dark.navbar-dark
      [:div.container-fluid
       [:ul.navbar-nav
        [:li.nav-item
         [:a.nav-link.active {:href "/indexadmin"} "Home page"]]
        [:li.nav-item
         [:a.nav-link {:href "/articles/new"} "New article"]]
        [:li.nav-item
         [:a.nav-link {:href "/blogstatistics"} "Blog statistics"]]
        [:li.nav-item
         [:a.nav-link {:href "/articles/reported"} "View reports"]]
        [:li.nav-item
         [:a.nav-link {:href "/admin/logout"} "Logout"]]]]]
   body
     [:footer {:style "text-align: center;\n  padding: 3px;\n  background-color: black;\n  color: white;"}
      [:br]
      [:p "Author: Nevena Darijevic" [:br]
       [:a {:href "https://rs.linkedin.com/in/nevena-darijević-53876415b" :target "_blank"} "LinkedIn Profile"]]]
     ]))


;Statistics page for admin
(defn blogstatistics []
  (basePageTemplateAdmin
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
                  ]]][:br][:br][:br]))

;max lentght for text of every articles shown on index page as preview
(def previewLengthForArticles 500)

;private function for trimming text used on index page for previewing articles
(defn- trimText [text] (if (> (.length text) previewLengthForArticles)
                         (subs text 0 previewLengthForArticles)
                         text))


;index page for admin
(defn indexAdmin [articles]
  (basePageTemplateAdmin
    [ :div.container.p-5.my-5.border
     [:h1 {:style "margin-left : 270px; color : red; font-family:georgia,garamond,serif;font-size:60px;font-style:italic;"} "Welcome to " [:strong "FakePolitics"] ]
     [:br]
     [:p {:style "margin-left: 140px; color: #61C0DF;font-size:35px"} "We doubt and question for you. It's up to you to trust us."
      ]
     [:br]
     [:img {:src "https://yaow.org/wp-content/uploads/2017/05/Newspaper.jpg" :style "width: 1250px; height: 500px; margin-left: 0px; "}]
     ]
    (for [a articles]
      [ :div.container.p-5.my-5.border
       [:h2  (:title a)]
       [:p (-> a :body trimText )]
       [:a.link {:href (str "/articlesAdmin/" (:_id a))}"Read more"]
       ]
      )))


;Page for display specific article for admin
(defn articleAdmin [a]
  (basePageTemplateAdmin
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
       [:small (str "Author: " (:author a)) ])
     [:br]
     [:small (str "Portal: " (:portal a)) ]
     [:br]
     [:small (str "Tag: " (:tag a)) ]
     [:br]]))


;Edit article or create an article if doesn't exist, option for admin
(defn editArticle [a]
  (basePageTemplateAdmin [:br] [:h3 {:style "margin-left: 600px; color: #61C0DF;"} "Article information"]
    (form/form-to
      [:post (if a
               (str "/articlesAdmin/" (:_id a))
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
       [:br]
       (form/label "tag" "Tag")
       (form/drop-down {:class "form-control"} "tag" [["True" "true"] ["False" "false"]] (:tag a))
       [:br]
       [:br]
       (anti-forgery-field)
       (form/submit-button {:class "btn btn-primary"} "Save")])))



;Page for admin which displays all reported news, reported by readers of blog
(defn displayReported [reported]
  (basePageTemplateAdmin
    [:br]
    [:h3 {:style "margin-left: 600px; color: #61C0DF;"} "Reports from readers"]
    [:small {:style "margin-left: 690px; color: #61C0DF;"} (str "Count: " (db/countReports))]
 [:br]
    [:style "#report {
    table-layout: fixed;
      width: 1400px;
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
    [:div {:style "margin-left:30px" "margin-bottom:30px" "margin-right:60px"}
    [:body  [:br] (for [r reported]
                    [:table#report
                     (form/form-to
                       [:delete (str "/reported/" (:_id r))]
                       [:tr
                        [:th "Link"]
                        [:th "Reason"]
                        [:th "Author"]
                        [:th "Portal"]
                        [:th "Action"]]
                       [:tr
                        [:td  {:style "color:red"} (if (= "" (:link r)) "No information about article" [:a.link {:href (:link r) :target "_blank" }
                                                                                    "Reported article"])]
                        [:td (if (= "" (:reason r)){:style "color:red"}) (if (= "" (:reason r) ) "No information about reason" (:reason r))]
                        [:td (if (= "" (:author r)){:style "color:red"}) (if (= "" (:author r)) "No information about author"(:author r))]
                        [:td (:portal r)]
                        (anti-forgery-field)
                        [:td (form/submit-button {:class "btn btn-danger"} "Reject")]])] ) ]]
    [:br]
    [:br]))







