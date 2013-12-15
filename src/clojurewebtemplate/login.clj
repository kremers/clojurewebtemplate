(ns clojurewebtemplate.login
  (:require [cemerick.friend.credentials :as creds]))

; User map according to friend. Use your favourite datasource to provide
(def users (atom {"root" {:username "root"
                     :password (creds/hash-bcrypt "admin_password")
                     :roles #{::admin}}
             "jane" {:username "jane"
                     :password (creds/hash-bcrypt "user_password")
                     :roles #{::user}}}))

