## Predis

Predis is a Clojure protocol for [Redis](http://redis.io/), which allows for multiple client implementations with a common API.
Most notably Predis provides an in-memory mock client implementation for testing purposes, similar to, and inspired by [brigade/mock_redis](https://github.com/brigade/mock_redis).

### Installation
TODO

![](https://circleci.com/gh/andrewberls/predis.svg?style=shield&circle-token=b3151df11a25e1354af007e40c727ead5b9e676e)

### Usage
```clj
(require '[predis.core :as redis])

; Using the in-memory mock
(require '[predis.mock :as mock])
(def mock-client (mock/->redis))
(redis/set mock-client "foo" "bar") ; => "OK"
(redis/get mock-client "foo") ; => "bar"
```

The API is the same for the real Carmine client:

```clj
(require '[predis.carmine :as carmine])

; Default config optionally shown
(def carmine-client (carmine/->redis {:pool {} :spec {:host "127.0.0.1" :port 6379}}))
(redis/set carmine-client "foo" "bar") ; => "OK"
(redis/get carmine-client "foo") ; => "bar"
```
