(require 'boot.repl)

(set-env! :dependencies '[[boot-bundle "0.1.0-SNAPSHOT" :scope "test"]
                          [me.raynes/fs "1.4.6"]])

(require '[boot-bundle :refer [expand-keywords]]
         '[me.raynes.fs :as fs])

;; (let [old-value @boot-bundle/bundle-file-path]
;;   (reset! boot-bundle/bundle-file-path (fs/expand-home "~/.boot/boot.bundle.edn"))

;;   (set-env! :dependencies (expand-keywords [:tools-nrepl :boot-http :raynes-fs]))

;;   (reset! boot-bundle/bundle-file-path old-value))

(defn reset-bundle-map! []
  (reset! boot-bundle/bundle-file-path (fs/expand-home "~/.boot/boot.bundle.edn"))
  (reset! boot-bundle/bundle-map (boot-bundle/read-from-file)))

(defn expand-keywords! [coords]
  (reset-bundle-map!)
  (boot-bundle/expand-keywords coords))

(defn read-boot-bundle-tasks []
  (clojure.edn/read-string (slurp (clojure.java.io/file (fs/expand-home "~/.boot/boot.bundle.tasks.edn")))))

(defn coord-tasks [coord]
  (get (read-boot-bundle-tasks) coord))

(defn tasks [& coords]
  (mapcat coord-tasks coords))

;;(apply require (tasks :boot-http))

;; (deftask serve-static []
;;   (serve))
