(ns clojurewebtemplate.core
  (:gen-class)
  (:require (compojure [handler :as handler])
            [clojurewebtemplate.util :as u]
            [clojurewebtemplate.login :as l]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds]))
  (:use [compojure core]
        [clojure.tools.logging :only (info debug error)]
        [org.httpkit.server]
        [ring.middleware content-type file file-info params reload]))

(defroutes sync_routes
  (u/wrap-utf8
   (u/wrap-template
    (routes
     (GET  "/" [] (u/erender {} "templates/hello"))
     (GET  "/secret" [] (friend/authorize #{::admin} (u/erender {} "templates/secret")))
     (u/routes-by-convention '("/welcome"))
     (friend/logout (ANY "/logout" request (ring.util.response/redirect "/")))
     (ANY  "*" [] {:status 404 :body "sorry, 404"}))
    "templates/master")))

(def siteconfig
  (-> sync_routes
      (wrap-file "resources")
      wrap-file-info
      (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn l/users)
                            :workflows [(workflows/interactive-form)]})
      handler/site))

(defn -main [& args]
  (if-let [port (Integer/parseInt (first args))]
    (do (info "starting http-endpoint...")
        (run-server siteconfig {:port port})
        (info (str "|- http-endpoint ready (" port ")")))))
