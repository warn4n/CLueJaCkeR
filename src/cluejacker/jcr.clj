(ns cluejacker.jcr
  (:import (javax.jcr SimpleCredentials Session Node)
           (javax.jcr.util TraversingItemVisitor)
           (org.apache.jackrabbit.rmi.client ClientRepositoryFactory))
  (:use [clojure.set :only (union)]
    clojure.contrib.import-static
    [clojure.contrib.seq-utils :only (includes?)]
    (clojure.contrib str-utils)))

(defn session [config]
  (let [{:keys [url user password workspace]} config
        creds (if user (SimpleCredentials. user (.toCharArray password)))]
       (System/setProperty "java.rmi.server.useCodebaseOnly" "true")
       (.. (ClientRepositoryFactory.) (getRepository url) (login creds workspace))))

(defn item [session path]
         (.getItem session path))

(defmacro connect [instance configs]
         `(def ~instance (session (~configs (quote ~instance)))))

(def jcr-configs {'local-cq4 {:url "//127.0.0.1:1099/crx" :user "admin" :password "admin" :workspace "live_author"}
                  'local-publish-cq4 {:url "//127.0.0.1:1099/crx" :user "admin" :password "admin" :workspace "live_publish"}})


(comment
  (connect local-cq4 jcr-configs)

  ; or, just

  (def local-cq4 (session {:url "//127.0.0.1:1099/crx" :user "admin" :password "admin" :workspace "live_author"}))

  (map #(.getPath %) (iterator-seq (.. local-cq4 getRootNode getNodes)))

  )
