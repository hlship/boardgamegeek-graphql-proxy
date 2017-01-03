(ns bgg-graphql-proxy.client
  "Client code for accessing BoardGameGeek, and converting XML responses into Clojure data."
  (:require
    [clj-http.client :as client]
    [clojure.data.xml :as xml])
  (:import (java.io StringReader)))

(def base-url "https://www.boardgamegeek.com/xmlapi")

(require '[clojure.pprint :as pp])
(defn tap [label x]
  (println label (pp/write x :stream nil))
  x)

(defn ^:private expect-tag [tag element]
  (when-not (= tag (:tag element))
    (throw (ex-info "Wrong tag."
                    {:expected-tag tag
                     :actual-tag (:tag element)
                     :element element})))
  element)

(defn ^:private parse-int [s] (Integer/parseInt s))

(defn ^:private prefix-with-https [s] (str "https:" s))

(def ^:private boardgame-content-renames
  {:yearpublished [:year-published parse-int]
   :minplayers [:min-players parse-int]
   :maxplayers [:max-players parse-int]
   :playingtime [:playing-time parse-int]
   :age [:min-player-age parse-int]
   :description [:description str]
   :thumbnail [:thumbnail prefix-with-https]
   :image [:image prefix-with-https]})

(defmulti process-bg-content
  (fn [_bg element]
    (:tag element)))

(defmethod process-bg-content :default
  [bg element]
  (let [tag (:tag element)
        [output-key parser] (get boardgame-content-renames tag)]
    (if output-key
      (assoc bg output-key (-> element :content first parser))
      bg)))

(defmethod process-bg-content :name
  [bg element]
  ;; non-primary names are usually translations to other languages
  (if (-> element :attrs :primary)
    ;; TODO: Trim/reformat loose HTML
    (assoc bg :name (-> element :content first))
    bg))


(defn ^:private xml->board-game
  [element]
  (reduce process-bg-content
          {:id (-> element :attrs :objectid)}
          (:content element)))

(defn get-board-game
  [id]
  (->> (client/get (str base-url "/boardgame/" id)
                   {:accept "text/xml"
                    :throw-exceptions false})
       #_(tap :raw-response)
       :body
       StringReader.
       xml/parse
       #_ (tap :parsed-body)
       (expect-tag :boardgames)
       :content
       first
       #_(tap :raw-board-game)
       xml->board-game))
