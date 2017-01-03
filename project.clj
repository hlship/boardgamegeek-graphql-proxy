(defproject com.howardlewisship/bgg-graphql-proxy "0.12.0"
  :description "GraphQL interface to BoardGameGeek"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [io.aviso/logging "0.1.0"]
                 [com.walmartlabs/graphql "0.12.0"]
                 [com.walmartlabs/graphiql "0.1.0"]
                 [clj-http "2.3.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.xml "0.0.8"]
                 [io.pedestal/pedestal.service "0.5.1"]
                 [io.pedestal/pedestal.jetty "0.5.1"]]
  :repositories [["ereceipts-releases"
                  "http://dfw-receipts-jenkins.mobile.walmart.com:8081/nexus/content/repositories/releases/"]]
  :codox {:source-uri ""
          :metadata {:doc/format :markdown}})
