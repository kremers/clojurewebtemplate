(ns ofdb.util
  (:require [cemerick.friend :as friend]
            [cheshire.core :refer :all]
            [compojure.core :refer :all]
            [ring.util.response :refer :all]
            [stencil.core :refer :all]
            [stencil.loader :refer [unregister-all-templates]])
  (:gen-class))

(defmacro TGET [route & body]
  "GET with routename corresponding to template name"
  `(GET ~route [] (render-file (str "templates" ~route) ~@body)))

(defn routes-by-convention [pages] (apply routes (map #(TGET % {}) pages)))

(def development? (= "development" (get (System/getenv) "APP_ENV")))

(defn erender
  ([content template params]
     (do (if development? (unregister-all-templates))
         (render-file template (merge (into {} params) {:capsule content}))))
  ([template params]
     (do (if development? (unregister-all-templates))
         (render-file template params))))

(defn wrap-template [handler template]
  (fn [request]
    (if-let [response (handler request)]
      (update-in response [:body] erender template request))))

(defn wrap-authflags [handler]
  (fn [request]
    (if-let [identity (friend/identity request)]
      (handler (merge request {:loggedin true})) (handler request))))

(defn wrap-utf8 [handler]
  (fn [request]
    (if-let [response (handler request)]
      (content-type response "text/html;charset=UTF-8"))))

(defn gen-uuid [] (clojure.string/replace (str (java.util.UUID/randomUUID)) #"-" ""))

(defn jresp "JSON Response" [body]
  {:status 200
   :headers { "Content-Type" "application/json" }
   :body (generate-string body)})

(defn load-properties
  [file-name]
  (with-open [reader (ClassLoader/getSystemResourceAsStream file-name)] 
    (let [props (java.util.Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))

