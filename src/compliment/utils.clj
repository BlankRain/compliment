(ns compliment.utils
  "Functions and utilities for source implementations."
  (:require clojure.main
            [clojure.string :as string]))

(defn fuzzy-matches?
  "Tests if symbol matches the prefix when symbol is split into parts on
  separator."
  [prefix, ^String symbol, separator]
  (when (or (.startsWith symbol prefix) (= (first prefix) (first symbol)))
    (loop [pre (rest prefix), sym (rest symbol), skipping false]
      (cond (empty? pre) true
            (empty? sym) false
            skipping (if (= (first sym) separator)
                       (recur (if (= (first pre) separator)
                                (rest pre) pre)
                              (rest sym) false)
                       (recur pre (rest sym) true))
            (= (first pre) (first sym)) (recur (rest pre) (rest sym) false)
            :else (recur pre (rest sym) (not (= (first sym) separator)))))))

(defn fuzzy-matches-no-skip?
  "Tests if symbol matches the prefix when test checks whether character is a
  separator. Unlike `fuzzy-maches?` requires separator characters to be present
  in prefix."
  [prefix, ^String symbol, test]
  (when (or (.startsWith symbol prefix) (= (first prefix) (first symbol)))
    (loop [pre prefix, sym symbol, skipping false]
      (cond (empty? pre) true
            (empty? sym) false
            skipping (if (test (first sym))
                       (recur pre sym false)
                       (recur pre (rest sym) true))
            (= (first pre) (first sym)) (recur (rest pre) (rest sym) false)
            :else (recur pre (rest sym) true)))))

(defn resolve-symbol
  "Tries to resolve a symbol in the current namespace, or returns nil
  if the symbol can't be resolved."
  [sym]
  (try (resolve sym)
       (catch Exception e
         (when (not= ClassNotFoundException
                     (class (clojure.main/repl-exception e)))
           (throw e)))))

(defn resolve-class
  "Tries to resolve a classname from the given symbol, or returns nil
  if classname can't be resolved."
  [sym]
  (when-let [val (resolve-symbol sym)]
    (when (class? val) val)))

(defn resolve-namespace
  "Tries to resolve a namespace from the given symbol, either from a
  fully qualified name or an alias in the given namespace."
  [sym ns]
  (or (find-ns sym) ((ns-aliases ns) sym)))
