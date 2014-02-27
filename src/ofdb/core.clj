(ns ofdb.core
  (:require [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [cemerick.friend.workflows :as workflows]
            [clojure.tools.logging :refer [info]]
            [ofdb.login :as users :refer (users)]
            [ofdb.util :as u]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :refer :all]
            [ring.middleware.file :refer :all]
            [ring.middleware.file-info :refer :all]
            [ofdb.service :as s]
            [cheshire.core :as c])
  (:gen-class))

(defroutes sync_routes
  (u/wrap-utf8
   (routes
    (GET  "/product/:name" [name]
          (u/jresp
           (filter #(re-matches (re-pattern (str ".*" name ".*")) (:name %)) (s/getproducts))))
    (GET  "/products" [] (u/jresp (s/getproducts)))
    (u/wrap-template
     (routes
      (GET  "/" [] (u/erender "templates/hello" {}))
      (GET  "/secret" [] (friend/authorize #{::users/admin} (u/erender "templates/secret" {})))
      (GET  "/login" request (u/erender "templates/login" request))
      (u/routes-by-convention '("/welcome"))
      (friend/logout (ANY "/logout" request (ring.util.response/redirect "/")))
      (ANY  "*" [] {:status 404 :body "sorry, 404"}))
     "templates/master"))))

(def siteconfig
  (-> sync_routes
      (wrap-file "resources")
      wrap-file-info
      u/wrap-authflags
      (friend/authenticate {:credential-fn #(creds/bcrypt-credential-fn @users %)
                            :workflows [(workflows/interactive-form)]})
      handler/site))

(defn -main [& args]
  (if-let [port (Integer/parseInt (first args))]
    (do (info "starting http-endpoint...")
        (run-server siteconfig {:port port})
        (info (str "|- http-endpoint ready (" port ")")))))
