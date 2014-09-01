rtree
=========

In-memory immutable 2D [R-tree](http://en.wikipedia.org/wiki/R-tree) implementation in java using [RxJava Observables](https://github.com/ReactiveX/RxJava) for reactive processing of search results. 

Status: *alpha*, release candidate in Maven Central

This was fun to make, has an elegant concise algorithm, is thread-safe and fast.

The algorithm to achieve immutability is cute. For insertion/deletion it involves recursion down to the 
required leaf node then recursion back up (using a stack built as we recurse down) to replace the parent nodes up to the root. The guts of 
it is in [Leaf.java](src/main/java/com/github/davidmoten/rtree/Leaf.java).

[Backpressure](https://github.com/ReactiveX/RxJava/wiki/Backpressure) support required some complexity because effectively a
bookmark needed to be kept for a position in the tree and returned to later to continue traversal. An immutable stack containing
 the node and child index of the path nodes came to the rescue here and recursion was abandoned in favour of looping to prevent stack overflow (unfortunately java doesn't support tail recursion!).

Continuous integration with Jenkins: <a href="https://xuml-tools.ci.cloudbees.com/"><img src="https://xuml-tools.ci.cloudbees.com/job/rtree/badge/icon"/></a>

Maven site reports are [here](http://davidmoten.github.io/rtree/index.html) including [javadoc](http://davidmoten.github.io/rtree/apidocs/index.html).

Features
------------
* immutable R-tree suitable for concurrency
* type-safe
* pluggable splitting heuristic ([```Splitter```](src/main/java/com/github/davidmoten/rtree/Splitter.java)). Default is [Guttman's quadratic split](http://www-db.deis.unibo.it/courses/SI-LS/papers/Gut84.pdf).
* pluggable insert heuristic ([```Selector```](src/main/java/com/github/davidmoten/rtree/Selector.java)). Default is least minimum bounding rectangle area increase.
* search returns [```Observable```](http://reactivex.io/RxJava/javadoc/rx/Observable.html) 
* search is cancelled by unsubscription
* over 80K inserts per second on i7 single thread
* search is ```O(log(N))``` on average
* insert, delete are ```O(N)``` worst case
* all search methods return lazy-evaluated streams offering a lot of flexibility and opportunity for functional composition and concurrency
* balanced delete
* supports [backpressure](https://github.com/ReactiveX/RxJava/wiki/Backpressure)

Number of points = 100, max children per node 4:

<img src="https://raw.githubusercontent.com/davidmoten/rtree/master/src/docs/rtree.png"/>

Getting started
----------------
Add this maven dependency to your pom.xml:

```xml
<dependency>
  <groupId>com.github.davidmoten</groupId>
  <artifactId>rtree</artifactId>
  <version>0.1-RC1</version>
</dependency>
```

How to build
----------------
```
git clone https://github.com/davidmoten/rtree.git
cd rtree
mvn clean install
```
Example
--------------
```java
import com.github.davidmoten.rtree.RTree;
import static com.github.davidmoten.rtree.geometry.Geometries.*;

RTree<String> tree = RTree.maxChildren(5).create();
tree = tree.add("DAVE", point(10, 20))
           .add("FRED", point(12, 25))
           .add("MARY", point(97, 125));
 
Observable<Entry<String>> entries = tree.search(Rectangle.create(8, 15, 30, 35));
```

What do I do with the Observable thing?
----------------------------------------
Very useful, see [RxJava](http://github.com/ReactiveX/RxJava).

As an example, suppose you want filter the search results then apply a function on each in parallel and reduce to some best answer:

```java
import rx.Observable;
import rx.functions.*;
import rx.schedulers.Schedulers;

Func1<Entry<String>, Character> firstCharacter = entry -> entry.object().charAt(0);
Func2<Character,Character,Character> firstAlphabetically = (x,y) -> x <=y ? x : y;

Character result = 
    tree.search(Geometries.rectangle(8, 15, 30, 35))
        // filter for names alphabetically less than M
        .filter(entry -> entry.value() < "M")
        // use a different scheduler for each entry
        .flatMap(entry -> Observable.just(entry).subscribeOn(Schedulers.computation())
        // get the first character of the name
        .map(entry -> firstCharacter(entry.value()))
        // reduce to the first character alphabetically 
        .reduce((x,y) -> firstAlphabetically(x,y))
        // subscribe to the stream and block for the result
        .toBlocking().single();
System.out.println(list);
```
output:
```
D
```

How do I just get an Iterable back from a search?
-------------------------------------------------------
If you are not familiar with the Observable API and want to skip the reactive stuff then here's how to get an Iterable from a search:

```java
Iterable<T> it = tree.search(Geometries.point(4,5)).toBlocking().toIterable();
```