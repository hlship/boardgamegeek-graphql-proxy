(ns bgg-graphql-proxy.server
  (:require
    [io.pedestal.http :as http]
    [io.pedestal.http.route :as route]
    [io.pedestal.interceptor :refer [interceptor]]
    [clojure.java.io :as io]
    [clojure.data.json :as json]
    [com.walmartlabs.graphql :refer [execute]]))


(defn ^:private index-handler
  "Handles the index request as if it were /graphiql/index.html."
  [request]
  {:status 200
   :headers {"Content-Type" "text/html;charset-UTF-8"}
   :body (-> "graphiql/index.html"
             io/resource
             slurp)})

(defn variable-map
  "Reads the `variables` query parameter, which contains a JSON string
  for any and all GraphQL variables to be associated with this request.

  Returns a map of the variables (using keyword keys)."
  [request]
  (if-let [vars (get-in request [:query-params :variables])]
    (json/read-str vars :key-fn keyword)
    {}))

(defn extract-query
  [request]
  (case (:request-method request)
    :get (get-in request [:query-params :query])
    :post (slurp (:body request))
    :else ""))

(defn ^:private graphql-handler
  "Accepts a GraphQL query via POST, and executes the query.
  Returns the result as text/json."
  [compiled-schema]
  (fn [request]
    (let [vars (variable-map request)
          query (extract-query request)
          result (execute compiled-schema query vars nil)
          status (if (-> result :errors seq)
                   400
                   200)]
      {:status status
       :headers {"Content-Type" "application/json"}
       :body (json/write-str result)})))

(defn ^:private routes
  [compiled-schema]
  (let [query-handler (graphql-handler compiled-schema)]
    (route/expand-routes
      #{["/" :get index-handler :route-name :graphiql-ide-index]
        ["/graphql" :post query-handler :route-name :graphql-post]
        ["/graphql" :get query-handler :route-name :graphql-get]})))

(defn pedestal-server
  "Creates and returns server instance, ready to be started."
  [compiled-schema]
  (http/create-server {:env :dev
                       ::http/routes (routes compiled-schema)
                       ::http/resource-path "graphiql"
                       ::http/port 8888
                       ::http/type :jetty
                       ::http/join? false}))