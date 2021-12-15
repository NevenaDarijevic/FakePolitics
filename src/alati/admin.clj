(ns alati.admin)

(def adminLoginUsername (or (System/getenv "alatiAdminLoginUsername")))
(def adminLoginPassword (or (System/getenv "alatiAdminLoginPassword")))

(defn adminLogin [username password]
  (and (= username adminLoginUsername) (= password adminLoginPassword)))