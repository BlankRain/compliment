(ns compliment.t-core
  (:require [midje.sweet :refer :all]
            [compliment.core :as core]
            [compliment.context :as ctx]))

;; This namespace contains only sanity checks for the public API. For
;; in-depth source testing see their respective test files.

(facts "about completion"
  (fact "`completions` takes a prefix, optional namespace and a context.
  If context is missing, `nil` should be passed"
    (core/completions "redu" nil)
    => (just #{"reductions" "reduce" "reduce-kv"})

    (core/completions "fac" (find-ns 'midje.sweet) nil)
    => (just #{"fact-group" "fact" "facts"})

    (core/completions "fac" (find-ns 'clojure.core) nil)
    => ())

  (fact "many sources allow some sort of fuzziness in prefixes"
    (core/completions "re-me" nil)
    => (just #{"remove-method" "reset-meta!"})

    (core/completions "cl.co." nil)
    => (just #{"clojure.core.protocols" "clojure.core.unify"})

    (core/completions ".gSV" nil)
    => (just #{".getSpecificationVersion" ".getSpecificationVendor"}))

  (fact "context can help some sources to give better candidates list"
    (def a-str "a string")
    (core/completions ".sub" (ctx/parse-context '(__prefix__ a-str)))
    => (just #{".subSequence" ".substring"})

    (def a-big-int 42M)
    (core/completions ".sub" (ctx/parse-context '(__prefix__ a-big-int)))
    => [".subtract"]))

(facts "about documentation"
  (fact "`documentation` takes a symbol string which presumably can be
  resolved to something that has a dostring.")
  (core/documentation "reduce")         => (every-pred string? not-empty)
  (core/documentation ".suspend")       => (every-pred string? not-empty)
  (core/documentation "jus.t g:arbage") => "")
