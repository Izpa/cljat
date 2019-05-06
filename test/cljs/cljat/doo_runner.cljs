(ns cljat.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [cljat.core-test]))

(doo-tests 'cljat.core-test)

