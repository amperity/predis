(ns predis.zsets-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            (clojure.test.check
              [clojure-test :refer [defspec]]
              [generators :as gen]
              [properties :as prop])
            [taoensso.carmine :as carmine]
            (predis
              [core :as r]
              [mock :as mock])
            [predis.test-utils :as test-utils]))

(def carmine-client (test-utils/carmine-client))

(use-fixtures :each test-utils/flush-redis)

(defspec test-zadd-one
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   score gen/int
                   m (gen/not-empty gen/string-alphanumeric)]
      (is (= (r/zadd mock-client k score m) (r/zadd carmine-client k score m)))
      (test-utils/dbs-equal mock-client carmine-client))))

(defspec test-zadd-many
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec]
      (is (= (r/zadd mock-client k kvs) (r/zadd carmine-client k kvs)))
      (test-utils/dbs-equal mock-client carmine-client))))

(defspec test-zcard
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec]
      (test-utils/assert-zadd mock-client carmine-client k kvs)
      (is (= (r/zcard mock-client k) (r/zcard carmine-client k)))
      (test-utils/dbs-equal mock-client carmine-client))))

(defspec test-zcount
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec

                   min-score gen/int
                   max-score-incr gen/s-pos-int]
      (let [max-score (+ min-score max-score-incr)]
        (test-utils/assert-zadd mock-client carmine-client k kvs)
        (is (= (r/zcount mock-client k min-score max-score)
               (r/zcount carmine-client k min-score max-score)))
        (test-utils/dbs-equal mock-client carmine-client)))))

(defspec test-zincrby
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec

                   increment gen/int]
      (test-utils/assert-zadd mock-client carmine-client k kvs)
      (let [m (second (first (shuffle kvs)))]
        (is (= (r/zincrby mock-client k increment m)
               (r/zincrby carmine-client k increment m)))
        (test-utils/dbs-equal mock-client carmine-client)))))

;;(zinterstore [this dest numkeys ks weights])
;;(zlexcount [this k min-val max-val])

(defspec test-zrange
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec

                   min-score gen/int
                   max-score-incr gen/s-pos-int]
      (test-utils/assert-zadd mock-client carmine-client k kvs)
      (let [max-score (+ min-score max-score-incr)]
        (is (= (r/zrange mock-client k min-score max-score)
               (r/zrange carmine-client k min-score max-score)))
        (is (= (r/zrange mock-client k min-score max-score {:withscores true})
               (r/zrange carmine-client k min-score max-score {:withscores true})))
        (test-utils/dbs-equal mock-client carmine-client)))))

;;(zrangebylex [this k min-val max-val opts?])

(defspec test-zrangebyscore
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec

                   min-score gen/int
                   max-score-incr gen/s-pos-int]
      (let [max-score (+ min-score max-score-incr)]
        (test-utils/assert-zadd mock-client carmine-client k kvs)
        (is (= (r/zrangebyscore mock-client k min-score max-score)
               (r/zrangebyscore carmine-client k min-score max-score)))

        (is (= (r/zrangebyscore mock-client k min-score max-score {:withscores true})
               (r/zrangebyscore carmine-client k min-score max-score {:withscores true})))

        ; TODO: limit, offset

        (test-utils/dbs-equal mock-client carmine-client)))))

(defspec test-zrank
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec]
      (test-utils/assert-zadd mock-client carmine-client k kvs)
      (let [m (second (first (shuffle kvs)))]
        (is (= (r/zrank mock-client k m) (r/zrank carmine-client k m)))
        (is (= (r/zrank mock-client k ["fake-member"]) (r/zrank carmine-client k ["fake-member"])))
        (is (= (r/zrank mock-client "fake-key" m) (r/zrank carmine-client "fake-key" m)))
        (test-utils/dbs-equal mock-client carmine-client)))))

(defspec test-zrem
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec]
      (test-utils/assert-zadd mock-client carmine-client k kvs)
      (let [num-members (inc (rand-int (dec (count kvs))))
            ms (map second (take num-members (shuffle kvs)))]
        (is (= (r/zrem mock-client k ms) (r/zrem carmine-client k ms)))
        (is (= (r/zrem mock-client k ["fake-member"]) (r/zrem carmine-client k ["fake-member"])))
        (is (= (r/zrem mock-client "fake-key" ms) (r/zrem carmine-client "fake-key" ms)))
        (test-utils/dbs-equal mock-client carmine-client)))))

;;(zremrangebylex [this k min-val max-val])
;(zremrangebyscore [this k min-score max-score])

(defspec test-zrevrange
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec

                   min-score gen/int
                   max-score-incr gen/s-pos-int]
      (test-utils/assert-zadd mock-client carmine-client k kvs)
      (let [max-score (+ min-score max-score-incr)]
        (is (= (r/zrevrange mock-client k min-score max-score)
               (r/zrevrange carmine-client k min-score max-score)))
        (is (= (r/zrevrange mock-client k min-score max-score {:withscores true})
               (r/zrevrange carmine-client k min-score max-score {:withscores true})))
        (test-utils/dbs-equal mock-client carmine-client)))))

;(zrevrangebyscore [this k max-score min-score opts])

(defspec test-zrevrank
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec]
      (test-utils/assert-zadd mock-client carmine-client k kvs)
      (let [m (second (first (shuffle kvs)))]
        (is (= (r/zrevrank mock-client k m) (r/zrevrank carmine-client k m)))
        (is (= (r/zrevrank mock-client k ["fake-member"]) (r/zrevrank carmine-client k ["fake-member"])))
        (is (= (r/zrevrank mock-client "fake-key" m) (r/zrevrank carmine-client "fake-key" m)))
        (test-utils/dbs-equal mock-client carmine-client)))))

(defspec test-zscore
  test-utils/nruns
  (let [mock-client (mock/->redis)]
    (prop/for-all [k gen/string-alphanumeric
                   kvs test-utils/gen-kvs-vec]
      (test-utils/assert-zadd mock-client carmine-client k kvs)
      (let [m (second (first (shuffle kvs)))]
        (is (= (r/zscore mock-client k m) (r/zscore carmine-client k m)))
        (is (= (r/zscore mock-client k "fake-m") (r/zscore carmine-client k "fake-m")))
        (is (= (r/zscore mock-client "fake-k" m) (r/zscore carmine-client "fake-k" m)))
        (test-utils/dbs-equal mock-client carmine-client)))))

;(zunionstore [dest numkeys ks weights])
;;(zscan [this k cursor] [this k cursor opts])
