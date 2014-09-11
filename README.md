rtree
=========

In-memory immutable 2D [R-tree](http://en.wikipedia.org/wiki/R-tree) implementation in java using [RxJava Observables](https://github.com/ReactiveX/RxJava) for reactive processing of search results. 

Status: *beta*, deployed to Maven Central

An [R-tree](http://en.wikipedia.org/wiki/R-tree) is a commonly used spatial index.

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
* Guttman's heuristics (Quadratic splitter) ([paper](https://www.google.com.au/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&cad=rja&uact=8&ved=0CB8QFjAA&url=http%3A%2F%2Fpostgis.org%2Fsupport%2Frtree.pdf&ei=ieEQVJuKGdK8uATpgoKQCg&usg=AFQjCNED9w2KjgiAa9UI-UO_0eWjcADTng&sig2=rZ_dzKHBHY62BlkBuw3oCw&bvm=bv.74894050,d.c2E))
* R*-tree heuristics ([paper](http://dbs.mathematik.uni-marburg.de/publications/myPapers/1990/BKSS90.pdf))
* Customizable [splitter](src/main/java/com/github/davidmoten/rtree/Splitter.java) and [selector](src/main/java/com/github/davidmoten/rtree/Selector.java)
* search returns [```Observable```](http://reactivex.io/RxJava/javadoc/rx/Observable.html) 
* search is cancelled by unsubscription
* search is ```O(log(n))``` on average
* insert, delete are ```O(n)``` worst case
* all search methods return lazy-evaluated streams offering efficiency and flexibility of functional style including functional composition and concurrency
* balanced delete
* supports [backpressure](https://github.com/ReactiveX/RxJava/wiki/Backpressure)
* JMH benchmarks
* visualizer included
* R*-tree performs 736,000 searches/second returning 22 entries from a tree of 38,377 Greek earthquake locations on i7-920@2.67Ghz


Number of points = 1000, max children per node 8, Quadratic split: 

<img src="src/docs/quad-1000-8.png?raw=true"/>

Number of points = 1000, max children per node 8, R*-tree split. Notice that there is little overlap compared to the 
Quadratic split. This should provide better search performance (and in general benchmarks show this).

<img src="src/docs/star-1000-8.png?raw=true"/>

Getting started
----------------
Add this maven dependency to your pom.xml:

```xml
<dependency>
  <groupId>com.github.davidmoten</groupId>
  <artifactId>rtree</artifactId>
  <version>0.2.2</version>
</dependency>
```
###Instantiate an R-Tree
Use the static builder methods on the ```RTree``` class:

```java
// create an R-tree using Quadratic split with max
// children per node 4, min children 2 (the threshold
// at which members are redistributed)
RTree<String> tree = RTree.create();
```
You can specify a few parameters to the builder, including *minChildren*, *maxChildren*, *splitter*, *selector*:

```java
RTree<String> tree = RTree.minChildren(3).maxChildren(6).create();
```

###R*-tree
If you'd like an R*-tree (which uses a topological splitter on minimal margin, overlap area and area and a selector combination of minimal area increase, minimal overlap, and area):

```
RTree<String> tree = RTree.star().maxChildren(6).create();
```

See benchmarks below for some of the performance differences.

###Add items to the R-tree
When you add an item to the R-tree you need to provide a geometry that represents the 2D physical location or 
extension of the item. The ``Geometries`` builder provides these factory methods:

* ```Geometries.rectangle```
* ```Geometries.circle```
* ```Geometries.point```

To add an item to an R-tree:

```java
RTree<T> tree = RTree.create();
tree = tree.add(item, Geometries.point(10,20));
```
or 
```java
tree = tree.add(Enry.entry(item, Geometries.point(10,20));
```

###Remove an item in the R-tree
To remove an item from an R-tree, you need to match the item and its geometry:

```java
tree = tree.delete(item, Geometries.point(10,20));
```
or 
```java
tree = tree.delete(entry);
```

###Custom geometries
You can also write your own implementation of [```Geometry```](src/main/java/com/github/davidmoten/rtree/geometry/Geometry.java). An implementation of ```Geometry``` needs to specify methods to:

* measure distance to a rectangle (0 means they intersect)
* check intersection with a rectangle (you can reuse the distance method here if you want but it might affect performance)
* provide a minimum bounding rectangle
* implement ```equals``` and ```hashCode``` for consistent equality checking

For the R-tree to be well-behaved, the distance function needs to satisfy these properties:

* ```distance(r) >= 0 for all rectangles r```
* ```if rectangle r1 contains r2 then distance(r1)<=distance(r2)```
* ```distance(r) = 0 if and only if the geometry intersects the rectangle r``` 

###Searching
The advantage of an R-tree is the ability to search for items in a region reasonably quickly. 
On average search is ```O(log(n))``` but worst case is ```O(n)```.

Search methods return ```Observable``` sequences:
```java
Observable<Entry<T>> results = tree.search(Geometries.rectangle(0,0,2,2));
```
or search for items within a distance from the given geometry:
```java
Observable<Entry<T>> results = tree.search(Geometries.rectangle(0,0,2,2),5.0);
```
To return all entries from an R-tree:
```java
Observable<Entry<T>> results = tree.entries();
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
-------------------------------------------
Very useful, see [RxJava](http://github.com/ReactiveX/RxJava).

As an example, suppose you want to filter the search results then apply a function on each and reduce to some best answer:

```java
import rx.Observable;
import rx.functions.*;
import rx.schedulers.Schedulers;

Func1<Entry<String>, Character> firstCharacter = entry -> entry.value().charAt(0);
Func2<Character,Character,Character> firstAlphabetically = (x,y) -> x <=y ? x : y;

Character result = 
    tree.search(Geometries.rectangle(8, 15, 30, 35))
        // filter for names alphabetically less than M
        .filter(entry -> entry.value() < "M")
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

How to configure the R-tree for best performance
--------------------------------------------------
Check out the benchmarks below, but even better do your own benchmarks. Here are some quick points
* Quadratic split (the default) is faster for insert
* Quadratic split is faster for search with low maxChildren and smaller numbers of points (~1000).
* R*-tree is 50% faster for search  for larger numbers of points (>10K) but has slower insert

How do I just get an Iterable back from a search?
---------------------------------------------------------
If you are not familiar with the Observable API and want to skip the reactive stuff then here's how to get an ```Iterable``` from a search:

```java
Iterable<T> it = tree.search(Geometries.point(4,5)).toBlocking().toIterable();
```

Backpressure
-----------------
The backpressure slow path may be enabled by some RxJava operators. This may slow search performance by a factor of 5 but avoids possible out of memory errors and thread starvation due to asynchronous buffering. Backpressure is benchmarked for a backpressure case below.

Visualizer
--------------
To visualize the R-tree in a PNG file of size 600 by 600 pixels just call:
```java
tree.visualize(600,600).save("target/mytree.png");
```
The result is like the images in the Features section above.

Dependencies
---------------------
This library has a dependency on *guava* 18.0 which is about 2.2M. If you are coding for Android you may want to use *ProGuard* to trim 
the final application size. The dependency is driven by extensive use of ```Optional```,```Preconditions```,```Objects```, and the use of
```MinMaxPriorityQueue``` for the *nearest-k* search. I'm open to the possibility of internalizing these dependencies if people care
about the dependency size a lot. Let me know.


How to build
----------------
```
git clone https://github.com/davidmoten/rtree.git
cd rtree
mvn clean install
```

How to run benchmarks
--------------------------
Benchmarks are provided by 
```
mvn clean install -Pbenchmark
```

### Notes
The *Greek* data referred to in the benchmarks is a collection of some 38,377 entries corresponding to the epicentres of earthquakes in Greece between 1964 and 2000. This data set is used by multiple studies on R-trees as a test case.

### Results

These were run on i7-920@2.67GHz.

```
Benchmark                                                                                  Mode  Samples        Score  Score error  Units
c.g.d.r.BenchmarksRTree.defaultRTreeInsertOneEntryInto1000EntriesMaxChildren004           thrpt       10   197106.763     3523.246  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeInsertOneEntryInto1000EntriesMaxChildren010           thrpt       10   190123.367     3873.774  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeInsertOneEntryInto1000EntriesMaxChildren032           thrpt       10   235106.025     3834.102  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeInsertOneEntryInto1000EntriesMaxChildren128           thrpt       10   201112.114     3797.343  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004      thrpt       10   192934.913     2151.479  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010      thrpt       10   208524.899     1490.592  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032      thrpt       10   155217.982     1929.824  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128      thrpt       10    71172.184      991.131  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeSearchOf1000PointsMaxChildren004                      thrpt       10  2196533.763    45770.848  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeSearchOf1000PointsMaxChildren010                      thrpt       10   782026.330    12266.905  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeSearchOf1000PointsMaxChildren032                      thrpt       10   427869.860     5037.435  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeSearchOf1000PointsMaxChildren128                      thrpt       10   691201.561    11513.860  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeSearchOfGreekDataPointsMaxChildren004                 thrpt       10   408654.499     9820.088  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeSearchOfGreekDataPointsMaxChildren010                 thrpt       10   364353.395     4941.693  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeSearchOfGreekDataPointsMaxChildren032                 thrpt       10   251596.970     2749.937  ops/s
c.g.d.r.BenchmarksRTree.defaultRTreeSearchOfGreekDataPointsMaxChildren128                 thrpt       10    68216.748     1089.106  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeInsertOneEntryInto1000EntriesMaxChildren010              thrpt       10   114795.121     2011.726  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeInsertOneEntryInto1000EntriesMaxChildren032              thrpt       10    27731.188     1136.587  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeInsertOneEntryInto1000EntriesMaxChildren128              thrpt       10    91505.838     1753.986  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004         thrpt       10   143930.482     2350.608  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010         thrpt       10   123686.264     1038.223  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032         thrpt       10    10773.871      271.267  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128         thrpt       10     1735.674       29.568  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeSearchOf1000PointsMaxChildren010                         thrpt       10  1922839.603    24788.838  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeSearchOf1000PointsMaxChildren032                         thrpt       10   581628.600     8290.489  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeSearchOf1000PointsMaxChildren128                         thrpt       10   364421.975     5628.882  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeSearchOfGreekDataPointsMaxChildren004                    thrpt       10   544696.459     3953.354  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeSearchOfGreekDataPointsMaxChildren010                    thrpt       10   815565.650     9258.820  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeSearchOfGreekDataPointsMaxChildren010WithBackpressure    thrpt       10   255261.297     4400.142  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeSearchOfGreekDataPointsMaxChildren032                    thrpt       10   289651.038     4925.593  ops/s
c.g.d.r.BenchmarksRTree.rStarTreeSearchOfGreekDataPointsMaxChildren128                    thrpt       10    14219.495      122.499  ops/s
```
