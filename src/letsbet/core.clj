(ns letsbet.core
  (:gen-class))
(require 'clojure.java.io)
(require 'clojure.string)
(require 'clojure.pprint)

(defn -main
  [arg]

  ; Open file.
  (with-open [rdr (clojure.java.io/reader arg)]

  ; create atoms.
  (def vertices_ (atom []))
  (def texturecoords_ (atom []))
  (def normals_ (atom []))
  (def faces_ (atom []))

    ; iterate over lines in file.
    (doseq [line (line-seq rdr)]

        ; :vertices
        (when (clojure.string/includes? line "v ")
          (def lineparse (clojure.string/split line #" "))
          (def filtered (into [] (remove #{"v"} lineparse)))
          (swap! vertices_ conj
          (if (get filtered 3)
            (sorted-map :w (read-string (get filtered 3))
            :x (read-string (get filtered 0))
            :y (read-string (get filtered 1))
            :z (read-string (get filtered 2)))
            (sorted-map :w 1.0
            :x (read-string (get filtered 0))
            :y (read-string (get filtered 1))
            :z (read-string (get filtered 2))))
          ))

        ; :texture-coords
        (when (clojure.string/includes? line "vt" )
          (def lineparse (clojure.string/split line #" "))
          (def filtered (into [] (remove #{"vt"} lineparse)))
          (swap! texturecoords_ conj
          (if (get filtered 2)
            (sorted-map :w (read-string (get filtered 2))
            :u (read-string (get filtered 0))
            :v (read-string (get filtered 1)))
            (sorted-map :w 0.0
            :u (read-string (get filtered 0))
            :v (read-string (get filtered 1))))
          ))

        ; :normals
        (when (clojure.string/includes? line "vn" )
          (def lineparse (clojure.string/split line #" "))
          (def filtered (into [] (remove #{"vn"} lineparse)))
          (swap! normals_ conj
            (sorted-map :x (read-string (get filtered 0))
            :y (read-string (get filtered 1))
            :z (read-string (get filtered 2)))
          ))

        ; :faces
        (when (clojure.string/includes? line "f " )
          (def lineparse (clojure.string/split line #" "))
          (def filtered (into [] (remove #{"f"} lineparse)))
          (swap! faces_ conj
          (sorted-map :elements
            (into []
            (for [x [0 1 2]]
                (sorted-map :vertex (if (clojure.string/blank? (get (clojure.string/split (get filtered x) #"/") 0))
                                      nil
                                      (read-string (get (clojure.string/split (get filtered x) #"/") 0)))
                            :texture-coord (if (clojure.string/blank? (get (clojure.string/split (get filtered x) #"/") 1))
                                              nil
                                              (read-string (get (clojure.string/split (get filtered x) #"/") 1)))
                            :normal (if (clojure.string/blank? (get (clojure.string/split (get filtered x) #"/") 2))
                                      nil
                                      (read-string (get (clojure.string/split (get filtered x) #"/") 2))))))
          ))))

        ; build and pprint output.
        (def finaloutput_ (hash-map
                           :vertices (deref vertices_)
                           :texture-coords (deref texturecoords_)
                           :normals (deref normals_)
                           :faces (deref faces_)))
        (clojure.pprint/pprint finaloutput_)))
