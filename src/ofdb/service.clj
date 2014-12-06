(ns ofdb.service)

(def calories1g  {:fat 9 :carbs 4 :protein 4 :alcohol 7})
(defn calc-calories [{:keys [protein carbs fat additionalinfo]}]
  (format "%.0f"
     (+ (* (:fat calories1g) fat)
        (* (:protein calories1g) protein)
        (* (:carbs calories1g) carbs)
        (* (:alcohol calories1g) (or (:alcohol additionalinfo) 0)))))

;; persisted to a bunch of json files based on 100g
(defrecord product [name calories protein carbs sugar fat waterpercentage additionalinfo])
(defn _product [params] (map->product (merge params {:calories (calc-calories params)})))

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

(defn addproduct [http_request])

#_(defn add_product [product] (swap! products conj product))

#_(defn save-new
  "Creates and inserts a new entity in a specified collection. Parameters get checked against map keys"
  [req #^String collection keymap]
  (json-embed (let [data (json-in req)] (insert collection (merge-by data keymap)))))

