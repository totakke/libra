# Libra

Simple benchmarking framework for Clojure.

## Installation

With Leiningen/Boot:

```clojure
[net.totakke/libra "0.1.0-SNAPSHOT"]
```

Leiningen plugin:

```clojure
:plugins [[net.totakke/lein-libra "0.1.0-SNAPSHOT"]]
```

Boot task:

```clojure
[net.totakke/boot-libra "0.1.0-SNAPSHOT" :scope "test"]
```

## Usage

```clojure
(require '[libra.bench :refer :all])

(defn slow-inc [n]
  (Thread/sleep 10)
  (inc n))

(defbench slow-inc-bench
  (measure (dur 10 (slow-inc 100))))

(run-benches)
;; Measuring user
;;
;; slow-inc-bench (:xx)
;;
;; time: 11.725818 ms, sd: 1.073600 ms
;;=> nil
```

## License

Copyright © 2017 Toshiki Takeuchi

Distributed under the MIT License.
