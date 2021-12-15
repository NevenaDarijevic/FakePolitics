(ns alati.admin)

(defn adminLoginUsername (or (System/getenv "alatiAdminLoginUsername")))
(defn adminLoginPassword (or (System/getenv "alatiAdminLoginPassword")))

(defn adminLogin [username password]
  (and (= username adminLoginUsername) (= password adminLoginPassword)))