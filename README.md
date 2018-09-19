## Predis

Predis is a Clojure protocol for [Redis](http://redis.io/), allowing for for multiple client implementations with a common API.
Predis provides an in-memory mock client implementation for testing purposes, similar to, and inspired by [brigade/mock_redis](https://github.com/brigade/mock_redis),
as well as a real client that passes operations through to [Carmine](https://github.com/ptaoussanis/carmine/).

### Installation
[![Clojars Project](http://clojars.org/amperity/predis/latest-version.svg)]()

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

### Documentation

[API Docs](http://andrewberls.github.io/predis/)

Note that not every single Redis API operation is supported; consult the [protocol documentation](http://andrewberls.github.io/predis/predis.core.html) for the up-to-date
list of supported operations.

### Contributing
Please use the [GitHub issues page](https://github.com/andrewberls/predis/issues) for questions/comments/suggestions (pull requests welcome!).
You can also find me on [Twitter](https://twitter.com/aberls).
