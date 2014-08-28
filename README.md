rtree
=========

In-memory immutable [R-tree](http://en.wikipedia.org/wiki/R-tree) implementation in java using [RxJava Observables](https://github.com/ReactiveX/RxJava) for reactive streaming of search results. 

Status: *pre-alpha*

This was fun to make, the implementation has really concise and readable source (even without lambdas),is thread-safe and pretty fast.

Continuous integration with Jenkins: <a href="https://xuml-tools.ci.cloudbees.com/"><img src="https://xuml-tools.ci.cloudbees.com/job/rtree/badge/icon"/></a>

Maven site reports are [here](http://davidmoten.github.io/rtree/index.html) including [javadoc](http://davidmoten.github.io/rtree/apidocs/index.html).

Features
------------
* Immutable R-tree suitable for concurrency
* Pluggable splitting heuristic (default is [Guttman's quadratic split](http://www-db.deis.unibo.it/courses/SI-LS/papers/Gut84.pdf)).
* Search returns Observable 
* Search can be cancelled by unsubscription
* over 80K inserts per second on i7 single thread
* search is O(log(N)) on average
* all search methods including ```nearest``` and ```furthest``` are streaming instead of *nearest-k* so much more flexible

Number of points = 100, max children per node 4:

<img src="https://raw.githubusercontent.com/davidmoten/rtree/master/src/docs/rtree.png"/>

Example
--------------
```java
RTree tree = RTree.builder().maxChildren(5).build()
    .add(new Entry("DAVE", 10, 20)
    .add(new Entry("FRED", 12, 25)
    .add(new Entry("MARY", 97, 125);
 
Observable<Entry> entries = tree.search(Rectangle.create(8, 15, 30, 35));
```
 
Search
------------
All search methods (```nearest```,```furthest```,```entries```, ```search```) themselves call the one super-duper search method below. So
if you want to do something fancy, check that method out first. Bear in mind that filtering on ```Entry``` is best done by the 
```Observable``` api (```Observable.filter```).

```java
RTree.search(Func1<Rectangle,Boolean>, Optional<Comparator<Rectangle>>)
```

Nearest and Furthest
----------------------
To find the nearest k neighbours to a rectangle:

```java
RTree tree = ...
Observable<Entry> entries = 
    tree.nearest(Rectangle.create(8, 15 ,20 ,35)).take(k);
```
    
To find the furthest k neighbours to a rectangle:

```java
RTree tree = ...
Observable<Entry> entries = 
    tree.furthest(Rectangle.create(8, 15 ,20 ,35)).take(k);
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
* implement ```RTree.delete```
* backpressure (?)
 



