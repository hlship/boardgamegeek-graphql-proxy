(ns bgg-graphql-proxy.server
  (:require
    [io.pedestal.http :as http]
    [io.pedestal.http.route :as route]
    [io.pedestal.interceptor :refer [interceptor]]
    [clojure.java.io :as io]))


(defn ^:private index-handler
  "Handles the index request as if it were /graphiql/index.html."
  [request]
  {:status 200
   :headers {"Content-Type" "text/html;charset-UTF-8"}
   :body (-> "graphiql/index.html"
             io/resource
             slurp)})

(def routes
  (route/expand-routes
    #{["/" :get index-handler :route-name :graphiql-ide-index]}))

(defn server
  "Starts a server, which is returned."
  []
  (-> {:env :dev
       ::http/routes routes
       ::http/resource-path "graphiql"
       ::http/port 8888
       ::http/type :jetty
       ::http/join? false}
      http/create-server
      http/start))
