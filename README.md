rtree
=========

In-memory immutable [R-tree](http://en.wikipedia.org/wiki/R-tree) implementation in java using [RxJava Observables](https://github.com/ReactiveX/RxJava) for reactive streaming of searches. 

Status: *pre-alpha*

Features
------------
* Immutable R-tree suitable for concurrency
* Pluggable splitting heuristic (default is [Guttman's quadratic split](http://www-db.deis.unibo.it/courses/SI-LS/papers/Gut84.pdf).
* Search returns Observable 
* Search can be cancelled by unsubscription
* over 100K inserts a second on i7 single thread
* search is O(log(N)) on average
* backpressure (TODO)

<img src="https://raw.githubusercontent.com/davidmoten/rtree/master/src/docs/rtree.png">rtree</img>

