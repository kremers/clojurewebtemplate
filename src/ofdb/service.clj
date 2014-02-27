(ns ofdb.service)

;; persisted to a bunch of json files based on 100g

(defrecord product [name calories protein carbs sugar fat waterpercentage additionalinfo])
(def calories1g  {:fat 9 :carbs 4 :protein 4 :alcohol 7})
(def wine        (->product "wine" 85  0.07 2.61 0.0 0.0  86.49 {:alcohol 10.6}))
(def lowfatquark (->product "low fat quark" 70  12   4    4   0.5     80 {:cholesterin 0.0007}))
(def egg         (->product "egg" 156 12.8 0.7  0.7 11.3    76 {:cholesterin 0.00396}))

(defn getproducts [] [wine lowfatquark egg])






