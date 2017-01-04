(ns bgg-graphql-proxy.schema
  (:require
    [clojure.java.io :as io]
    [clojure.edn :as edn]
    [com.walmartlabs.graphql.schema :as schema]
    [com.walmartlabs.graphql.util :refer [attach-resolvers]]
    [bgg-graphql-proxy.client :as client]))

(defn ^:private resolve-board-game
  [_ args _value]
  ;; TODO: Error handling, including not found
  [(client/get-board-game (:id args)) nil])

(defn ^:private resolve-search
  [_ args _value]
  [(client/search (:term args)) nil])

(defn ^:private resolve-game-publishers
  [_ args board-game]
  (let [{:keys [limit]} args
        publisher-ids (cond->> (:publisher-ids board-game)
                        limit (take limit))]
    [(client/publishers publisher-ids) nil]))

(defn bgg-schema
  []
  (-> (io/resource "bgg-schema.edn")
      slurp
      edn/read-string
      (attach-resolvers {:resolve-game resolve-board-game
                         :resolve-search resolve-search
                         :resolve-game-publishers resolve-game-publishers})
      schema/compile))
