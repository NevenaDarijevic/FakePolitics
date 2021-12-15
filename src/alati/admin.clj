(ns alati.admin)

(def adminLoginUsername (or (System/getenv "alatiAdminLoginUsername") "admin")) ;admin is default username and passowrd
(def adminLoginPassword (or (System/getenv "alatiAdminLoginPassword") "admin"))

(defn adminLogin [username password]
  (and (= username adminLoginUsername) (= password adminLoginPassword)))