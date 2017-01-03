(ns bgg-graphql-proxy.schema
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [com.walmartlabs.graphql.schema :as schema]))

(defn bgg-schema
  []
  (-> (io/resource "bgg-schema.edn")
      slurp
      edn/read-string
      identity                                              ; placeholder
      schema/compile))
