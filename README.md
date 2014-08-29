rtree
=========

In-memory immutable 2D [R-tree](http://en.wikipedia.org/wiki/R-tree) implementation in java using [RxJava Observables](https://github.com/ReactiveX/RxJava) for reactive streaming of search results. 

Status: *pre-alpha*

This was fun to make, the implementation has really concise and readable source (even without lambdas), is thread-safe and pretty fast.

Continuous integration with Jenkins: <a href="https://xuml-tools.ci.cloudbees.com/"><img src="https://xuml-tools.ci.cloudbees.com/job/rtree/badge/icon"/></a>

Maven site reports are [here](http://davidmoten.github.io/rtree/index.html) including [javadoc](http://davidmoten.github.io/rtree/apidocs/index.html).

Features
------------
* Immutable R-tree suitable for concurrency
* Pluggable splitting heuristic ([```Splitter```](src/main/java/com/github/davidmoten/rtree/Splitter.java)). Default is [Guttman's quadratic split](http://www-db.deis.unibo.it/courses/SI-LS/papers/Gut84.pdf)).
* Pluggable insert heuristic ([```Selector```](src/main/java/com/github/davidmoten/rtree/Selector.java)). Default is least mbr area increase.
* Search returns Observable 
* Search is cancelled by unsubscription
* over 80K inserts per second on i7 single thread
* search is O(log(N)) on average
* all search methods return lazy-evaluated streams offering a lot of flexibility and opportunity for functional composition and concurrency
* balanced delete

Number of points = 100, max children per node 4:

<img src="https://raw.githubusercontent.com/davidmoten/rtree/master/src/docs/rtree.png"/>

Example
--------------
```java
RTree tree = RTree.maxChildren(5).create()
    .add(new Entry("DAVE", 10, 20)
    .add(new Entry("FRED", 12, 25)
    .add(new Entry("MARY", 97, 125);
 
Observable<Entry> entries = tree.search(Rectangle.create(8, 15, 30, 35));
```

What do I do with the Observable thing?
----------------------------------------
Very useful, see [RxJava](http://github.com/ReactiveX/RxJava).

As a simple example:

```java
List<String> list = 
    tree.search(Rectangle.create(8, 15, 30, 35))
        .take(2)
        .map(entry-> entry.object().toString())
        .filter(name -> name >= "E")
        .toList()
        .toBlocking().single();
System.out.println(list);
```
output is 
```
[FRED]
 ```

Todo
---------- 
* backpressure (?)
 



