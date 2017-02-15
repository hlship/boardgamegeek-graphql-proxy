(ns bgg-graphql-proxy.schema
  (:require
    [clojure.java.io :as io]
    [clojure.edn :as edn]
    [com.walmartlabs.lacinia.schema :as schema]
    [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
    [bgg-graphql-proxy.client :as client]))

(defn ^:private resolve-board-game
  [context args _value]
  ;; TODO: Error handling, including not found
  (client/get-board-game (:cache context) (:id args)))

(defn ^:private resolve-search
  [context args _value]
  (client/search (:cache context) (:term args)))

(defn ^:private extract-ids
  [board-game key args]
  (let [{:keys [limit]} args]
    (cond->> (get board-game key)
      limit (take limit))))

(defn ^:private resolve-game-publishers
  [context args board-game]
  (client/publishers (:cache context) (extract-ids board-game :publisher-ids args)))

(defn ^:private resolve-game-designers
  [context args board-game]
  (client/designers (:cache context) (extract-ids board-game :designer-ids args)))

(defn bgg-schema
  []
  (-> (io/resource "bgg-schema.edn")
      slurp
      edn/read-string
      (attach-resolvers {:resolve-game resolve-board-game
                         :resolve-search resolve-search
                         :resolve-game-publishers resolve-game-publishers
                         :resolve-game-designers resolve-game-designers})
      schema/compile))
