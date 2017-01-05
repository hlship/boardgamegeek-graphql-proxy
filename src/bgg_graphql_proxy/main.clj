(ns bgg-graphql-proxy.main
  (:require
    [io.pedestal.http :as http]
    [com.walmartlabs.graphql :refer [execute]]
    [bgg-graphql-proxy.schema :refer [bgg-schema]]
    [bgg-graphql-proxy.server :refer [pedestal-server]]))

(defn stop-server
  [server]
  (http/stop server)
  nil)

(defn start-server
  "Creates and starts Pedestal server, ready to handle Graphql (and Graphiql) requests."
  []
  (-> (bgg-schema)
      pedestal-server
      http/start))
