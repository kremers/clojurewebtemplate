(ns clojurewebtemplate.core
  (:gen-class)
  (:require (compojure [handler :as handler]) [clojurewebtemplate.util :as u])
  (:use [compojure core]
        [clojure.tools.logging :only (info debug error)]
        [org.httpkit.server]
        [ring.middleware content-type file file-info params reload]))

(defroutes sync_routes
  (u/wrap-utf8
   (u/wrap-template
    (routes
     (GET  "/" [] (u/erender {} "templates/hello"))
     (u/routes-by-convention '("/welcome"))
     (ANY  "*" [] {:status 404 :body "sorry, 404"}))
    "templates/master")))

(def siteconfig
  (-> sync_routes
      (wrap-file "resources")
      wrap-file-info
      handler/site))

(defn -main [& args]
  (if-let [port (Integer/parseInt (first args))]
    (do (info "starting http-endpoint...")
        (run-server siteconfig {:port port})
        (info (str "|- http-endpoint ready (" port ")")))))
