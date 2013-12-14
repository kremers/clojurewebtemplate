(ns clojurewebtemplate.util
  (:require clojure.java.io)
  (:use [cheshire.core] [stencil.loader :only [unregister-all-templates]] [stencil.core] [ring.util.response]))

(def development? (= "development" (get (System/getenv) "APP_ENV")))

(defn render [content template & params]
  "Render website with template where a {{capsule}}
   element is required for the rendered body"
  (do (if development? (unregister-all-templates))
      (render-file template (merge (into {} params) {:capsule content}))))

(defn wrap-template [handler template]
  (fn [request]
    (if-let [response (handler request)]
      (update-in response [:body] render template request))))

(defn wrap-utf8 [handler]
  (fn [request]
    (if-let [response (handler request)]
      (content-type response "text/html;charset=UTF-8"))))

(defn gen-uuid [] (clojure.string/replace (str (java.util.UUID/randomUUID)) #"-" ""))

(defn jresp "JSON Response" [body]
  {:status  200 :headers { "Content-Type" "application/json" } :body (generate-string body)})
