(ns bgg-graphql-proxy.cache
  "A little bit of in-memory caching so we don't overload BGG.

  THe cache is simply a standard atom."
  (:require [clojure.set :as set]))

(defn resolve-by-id
  [cache category id]
  (get-in @cache [category id]))

(defn resolve-by-ids
  [cache category ids]
  (let [category-cache (get @cache category {})
        cached-values (keep category-cache ids)
        cached-ids (->> cached-values
                        (map :id)
                        set)
        uncached-ids (set/difference (set ids) cached-ids)]
    [cached-values (seq uncached-ids)]))

(defn ^:private map-by-id
  [values]
  (persistent! (reduce (fn [m v]
                         (assoc! m (:id v) v))
                       (transient {})
                       values)))

(defn fill
  [cache category values]
  (swap! cache update category merge (map-by-id values)))
