(ns alati.pages)
;Creating of route for index page which displays all articles
(defn index [articles]
  (->> articles
       (map #(str "<h2>" (:title %) "</h2>"))
       (apply str  "<h1>Testni naslov </h1>")))
