rtree
=========

In-memory immutable [R-tree](http://en.wikipedia.org/wiki/R-tree) implementation in java using [RxJava Observables](https://github.com/ReactiveX/RxJava) for reactive streaming of searches. 

Status: *pre-alpha*

Continuous integration with Jenkins: <a href="https://xuml-tools.ci.cloudbees.com/"><img src="https://xuml-tools.ci.cloudbees.com/job/rtree/badge/icon"/></a>

Maven site reports are [here](http://davidmoten.github.io/rtree/index.html) including [javadoc](http://davidmoten.github.io/rtree/apidocs/index.html).

Features
------------
* Immutable R-tree suitable for concurrency
* Pluggable splitting heuristic (default is [Guttman's quadratic split](http://www-db.deis.unibo.it/courses/SI-LS/papers/Gut84.pdf).
* Search returns Observable 
* Search can be cancelled by unsubscription
* over 100K inserts a second on i7 single thread
* search is O(log(N)) on average
* backpressure (TODO)

Number of points = 100, max children per node 4:

<img src="https://raw.githubusercontent.com/davidmoten/rtree/master/src/docs/rtree.png"/>

Example
--------------
```java
RTree tree = new RTree(5)
    .add(new Entry("DAVE", 10, 20)
    .add(new Entry("FRED", 12, 25)
    .add(new Entry("MARY", 97, 125);
 
 Observable<Entry> entries = tree.search(Rectangle.create(8, 15, 30, 35));
 ```
 
What do I do with the Observable thing?
----------------------------------------
Very useful, see [RxJava](http://github.com/ReactiveX/RxJava).

As an simple example:

```java
List<String> list = 
    tree.search(Rectangle.create(8, 15, 30, 35))
        .take(2)
        .map(entry-> entry.object().toString())
        .toList()
        .toBlocking().single();
System.out.println(list);
```
output is 
```
[DAVE, FRED]
 ```
 



