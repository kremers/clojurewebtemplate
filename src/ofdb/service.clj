(ns ofdb.service
  (:require [cheshire.core :as c]
            [ofdb.util :as u]
            [validateur.validation :as v]))

(def calories1g  {:fat 9 :carbs 4 :protein 4 :alcohol 7})
(defn #^Number calc-calories [{:keys [#^Number protein #^Number carbs #^Number fat additionalinfo]}]
  (format "%.0f"
     (+ (* (:fat calories1g) (or fat 0.0))
        (* (:protein calories1g) (or protein 0.0))
        (* (:carbs calories1g) (or carbs 0.0))
        (* (:alcohol calories1g) (or (:alcohol additionalinfo) 0.0)))))

;; persisted to a bunch of json files based on 100g
(defrecord product [name calories protein carbs sugar fat waterpercentage additionalinfo])
(defn _product [params] (map->product (merge params {:calories (calc-calories params)})))
(defn invalid-product? [p]
  (let [vs (apply v/validation-set
            (v/presence-of :name :calories :protein :carbs :sugar :fat :waterpercentage)
            (v/validate-by :additionalinfo #(or (nil? %) (map? %)))
            (map #(v/numericality-of % :gte 0 :lte 100)
                 '(:protein :carbs :sugar :fat :waterpercentage)) 
            )
        result (vs p)]
    (if (empty? result) false result)))

(def wine
  (_product
   {:name "wine"
    :protein 0.07
    :carbs 2.61
    :sugar 0.0
    :fat 0.0
    :waterpercentage 86.49
    :additionalinfo {:alcohol 10.6}}))

(def lowfatquark
  (_product
   {:name "low fat quark"
    :protein 12
    :carbs 4
    :sugar 4
    :fat 0.5
    :waterpercentage 80
    :additionalinfo {:cholesterin 0.0007}}))

(def egg
  (_product
   {:name "egg"
    :protein 12.8
    :carbs 0.7
    :sugar 0.7
    :fat 11.3
    :waterpercentage 76
    :additionalinfo {:cholesterin 0.00396}}))

(def products (atom [wine lowfatquark egg]))

(defn getproducts
  ([] @products)
  ([name] (filter #(re-matches (re-pattern (str ".*" name ".*")) (:name %)) (getproducts))))

(defn- add_product [product] (swap! products conj product))
(defn addproduct [http_request] (let [product (_product (u/json-in http_request))]
                                  (if-let [errors (invalid-product? product)]
                                    (do (clojure.pprint/pprint errors) errors)
                                    (add_product product))))

