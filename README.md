# Libra

[![Clojars Project](https://img.shields.io/clojars/v/net.totakke/libra.svg)](https://clojars.org/net.totakke/libra)
[![CircleCI](https://circleci.com/gh/totakke/libra.svg?style=svg)](https://circleci.com/gh/totakke/libra)

Simple benchmarking framework for Clojure.

## Installation

With Leiningen/Boot:

```clojure
[net.totakke/libra "0.1.1"]
```

Leiningen plugin:

```clojure
:plugins [[net.totakke/lein-libra "0.1.2"]]
```

Boot task:

```clojure
[net.totakke/boot-libra "0.1.0" :scope "test"]
```

## Getting started

Libra provides clojure.test-like functions and macros for benchmarking. For
example, `defbench` defines a benchmark and `run-benches` measures defined
benchmarks in the namespace.

```clojure
(require '[libra.bench :refer :all])

(defn slow-inc [n]
  (Thread/sleep 10)
  (inc n))

(defbench slow-inc-bench
  (is (dur 10 (slow-inc 100))))

(run-benches)
;; Measuring user
;;
;; slow-inc-bench (:xx)
;;
;;   time: 11.725818 ms, sd: 1.073600 ms
;;=> nil
```

## Basics

Basic usage is writing benchmarks in a separate directory (e.g. `bench`) from
`src` and running them with command-line. See [example project](https://github.com/totakke/libra/tree/master/example)
and try running benchmark.

The project consists of the following files.

```
example/
├── bench/
│   └── example/
│       └── core_bench.clj
├── project.clj or build.boot
└── src/
    └── example/
        └── core.clj
```

Locate your awesome codes in `src/example/core.clj` as usual, and write benchmarking programs in `bench/example/core_bench.clj`.

```clojure
(ns example.core-bench
  (:require [libra.bench :refer :all]
            [example.core :refer :all]))

(defbench primes-with-trial-div-bench
  (is (dur 10 (doall (primes-with-trial-div 100000)))))

(defbench primes-with-eratosthenes-bench
  (is (dur 10 (doall (primes-with-eratosthenes 100000)))))
```

### With Leiningen

To run the benchmark with Leiningen,

```console
$ lein libra
```

lein-libra looks benchmark files in `bench` directory by default. You can change
this by placing the following in `project.clj`:

```clojure
:libra {:bench-paths ["path/to/bench"]}
```

You can supply predicates to determine whether to run a benchmark or not, which
takes `defbench` metadata as argument:

```clojure
:libra {:bench-selectors {:default (complement :slow)
                          :slow :slow}}
```

### With Boot

To run the benchmark with Boot,

```console
$ lein benchmarking libra
```

boot-libra provides `libra` task. Benchmark directory needs to be included in
the classpath, so that you should add a profile task for benchmarking:

```clojure
(require '[libra.boot :refer [libra]])

(deftask benchmarking []
  (set-env! :source-paths #(conj % "bench"))
  identity)
```

### Criterium integration

`libra.criterium` provides wrapper macros of [Criterium](https://github.com/hugoduncan/criterium/).

```clojure
(require '[libra.criterium :as c])

(defbench primes-with-eratosthenes-bench
  (is (c/quick-bench (doall (primes-with-eratosthenes 100000)))))
```

## License

Copyright © 2017 Toshiki Takeuchi

Distributed under the MIT License.
