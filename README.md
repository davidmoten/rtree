rtree
=========
[![Travis CI](https://travis-ci.org/davidmoten/rtree.svg)](https://travis-ci.org/davidmoten/rtree)<br/>
[![Coverity Scan](https://scan.coverity.com/projects/4762/badge.svg?flat=1)](https://scan.coverity.com/projects/4762?tab=overview)<br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/rtree/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/rtree)<br/>
[![codecov](https://codecov.io/gh/davidmoten/rtree/branch/master/graph/badge.svg)](https://codecov.io/gh/davidmoten/rtree)


In-memory immutable 2D [R-tree](http://en.wikipedia.org/wiki/R-tree) implementation in java using [RxJava Observables](https://github.com/ReactiveX/RxJava) for reactive processing of search results. 

Status: *released to Maven Central*

An [R-tree](http://en.wikipedia.org/wiki/R-tree) is a commonly used spatial index.

This was fun to make, has an elegant concise algorithm, is thread-safe, fast, and reasonably memory efficient (uses structural sharing).

The algorithm to achieve immutability is cute. For insertion/deletion it involves recursion down to the 
required leaf node then recursion back up to replace the parent nodes up to the root. The guts of 
it is in [Leaf.java](src/main/java/com/github/davidmoten/rtree/internal/LeafDefault.java) and [NonLeaf.java](src/main/java/com/github/davidmoten/rtree/internal/NonLeafDefault.java).

[Backpressure](https://github.com/ReactiveX/RxJava/wiki/Backpressure) support required some complexity because effectively a
bookmark needed to be kept for a position in the tree and returned to later to continue traversal. An immutable stack containing
 the node and child index of the path nodes came to the rescue here and recursion was abandoned in favour of looping to prevent stack overflow (unfortunately java doesn't support tail recursion!).

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
* uses structural sharing
* supports [backpressure](https://github.com/ReactiveX/RxJava/wiki/Backpressure)
* JMH benchmarks
* visualizer included
* serialization using [FlatBuffers](http://github.com/google/flatbuffers)
* high unit test [code coverage](http://davidmoten.github.io/rtree/cobertura/index.html) 
* R*-tree performs 900,000 searches/second returning 22 entries from a tree of 38,377 Greek earthquake locations on i7-920@2.67Ghz (maxChildren=4, minChildren=1). Insert at 240,000 entries per second.
* requires java 1.6 or later

Number of points = 1000, max children per node 8: 

| Quadratic split | R*-tree split |
| :-------------: | :-----------: |
| <img src="src/docs/quad-1000-8.png?raw=true" /> | <img src="src/docs/star-1000-8.png?raw=true" /> |

Notice that there is little overlap in the R*-tree split compared to the 
Quadratic split. This should provide better search performance (and in general benchmarks show this).

Getting started
----------------
Add this maven dependency to your pom.xml:

```xml
<dependency>
  <groupId>com.github.davidmoten</groupId>
  <artifactId>rtree</artifactId>
  <version>0.8-RC10</version>
</dependency>
```
### Instantiate an R-Tree
Use the static builder methods on the ```RTree``` class:

```java
// create an R-tree using Quadratic split with max
// children per node 4, min children 2 (the threshold
// at which members are redistributed)
RTree<String, Geometry> tree = RTree.create();
```
You can specify a few parameters to the builder, including *minChildren*, *maxChildren*, *splitter*, *selector*:

```java
RTree<String, Geometry> tree = RTree.minChildren(3).maxChildren(6).create();
```
### Geometries
The following geometries are supported for insertion in an RTree:

* `Rectangle`
* `Point`
* `Circle`
* `Line` (requires [JTS](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jts-core%22) dependency, look at [pom.xml](pom.xml))

### Generic typing
If for instance you know that the entry geometry is always ```Point``` then create an ```RTree``` specifying that generic type to gain more type safety:

```java
RTree<String, Point> tree = RTree.create();
```

### R*-tree
If you'd like an R*-tree (which uses a topological splitter on minimal margin, overlap area and area and a selector combination of minimal area increase, minimal overlap, and area):

```
RTree<String, Geometry> tree = RTree.star().maxChildren(6).create();
```

See benchmarks below for some of the performance differences.

### Add items to the R-tree
When you add an item to the R-tree you need to provide a geometry that represents the 2D physical location or 
extension of the item. The ``Geometries`` builder provides these factory methods:

* ```Geometries.rectangle```
* ```Geometries.circle```
* ```Geometries.point```
* ```Geometries.line``` (requires *jts-core* dependency)

To add an item to an R-tree:

```java
RTree<T,Geometry> tree = RTree.create();
tree = tree.add(item, Geometries.point(10,20));
```
or 
```java
tree = tree.add(Entry.entry(item, Geometries.point(10,20));
```

*Important note:* being an immutable data structure, calling ```tree.add(item, geometry)``` does nothing to ```tree```, 
it returns a new ```RTree``` containing the addition. Make sure you use the result of the ```add```!

### Remove an item in the R-tree
To remove an item from an R-tree, you need to match the item and its geometry:

```java
tree = tree.delete(item, Geometries.point(10,20));
```
or 
```java
tree = tree.delete(entry);
```

*Important note:* being an immutable data structure, calling ```tree.delete(item, geometry)``` does nothing to ```tree```, 
it returns a new ```RTree``` without the deleted item. Make sure you use the result of the ```delete```!

### Geospatial geometries (lats and longs)
To handle wraparounds of longitude values on the earth (180/-180 boundary trickiness) there are special factory methods in the `Geometries` class. If you want to do geospatial searches then you should use these methods to build `Point`s and `Rectangle`s:

```java
Point point = Geometries.pointGeographic(lon, lat);
Rectangle rectangle = Geometries.rectangleGeographic(lon1, lat1, lon2, lat2);
```

Under the covers these methods normalize the longitude value to be in the interval [-180, 180) and for rectangles the rightmost longitude has 360 added to it if it is less than the leftmost longitude.

### Custom geometries
You can also write your own implementation of [```Geometry```](src/main/java/com/github/davidmoten/rtree/geometry/Geometry.java). An implementation of ```Geometry``` needs to specify methods to:

* check intersection with a rectangle (you can reuse the distance method here if you want but it might affect performance)
* provide a minimum bounding rectangle
* implement ```equals``` and ```hashCode``` for consistent equality checking
* measure distance to a rectangle (0 means they intersect). Note that this method is only used for search within a distance so implementing this method is *optional*. If you don't want to implement this method just throw a ```RuntimeException```.

For the R-tree to be well-behaved, the distance function if implemented needs to satisfy these properties:

* ```distance(r) >= 0 for all rectangles r```
* ```if rectangle r1 contains r2 then distance(r1)<=distance(r2)```
* ```distance(r) = 0 if and only if the geometry intersects the rectangle r``` 

### Searching
The advantage of an R-tree is the ability to search for items in a region reasonably quickly. 
On average search is ```O(log(n))``` but worst case is ```O(n)```.

Search methods return ```Observable``` sequences:
```java
Observable<Entry<T, Geometry>> results =
    tree.search(Geometries.rectangle(0,0,2,2));
```
or search for items within a distance from the given geometry:
```java
Observable<Entry<T, Geometry>> results =
    tree.search(Geometries.rectangle(0,0,2,2),5.0);
```
To return all entries from an R-tree:
```java
Observable<Entry<T, Geometry>> results = tree.entries();
```

Search with a custom geometry
-----------------------------------
Suppose you make a custom geometry like ```Polygon``` and you want to search an ```RTree<String,Point>``` for points inside the polygon. This is how you do it:

```java
RTree<String, Point> tree = RTree.create();
Func2<Point, Polygon, Boolean> pointInPolygon = ...
Polygon polygon = ...
...
entries = tree.search(polygon, pointInPolygon);
```
The key is that you need to supply the ```intersects``` function (```pointInPolygon```) to the search. It is on you to implement that for all types of geometry present in the ```RTree```. This is one reason that the generic ```Geometry``` type was added in *rtree* 0.5 (so the type system could tell you what geometry types you needed to calculate intersection for) .

Search with a custom geometry and maxDistance
--------------------------------------------------
As per the example above to do a proximity search you need to specify how to calculate distance between the geometry you are searching and the entry geometries:

```java
RTree<String, Point> tree = RTree.create();
Func2<Point, Polygon, Boolean> distancePointToPolygon = ...
Polygon polygon = ...
...
entries = tree.search(polygon, 10, distancePointToPolygon);
```
Example
--------------
```java
import com.github.davidmoten.rtree.RTree;
import static com.github.davidmoten.rtree.geometry.Geometries.*;

RTree<String, Point> tree = RTree.maxChildren(5).create();
tree = tree.add("DAVE", point(10, 20))
           .add("FRED", point(12, 25))
           .add("MARY", point(97, 125));
 
Observable<Entry<String, Point>> entries =
    tree.search(Geometries.rectangle(8, 15, 30, 35));
```

Searching by distance on lat longs
------------------------------------
See [LatLongExampleTest.java](src/test/java/com/github/davidmoten/rtree/LatLongExampleTest.java) for an example. The example depends on [*grumpy-core*](https://github.com/davidmoten/grumpy) artifact which is also on Maven Central.

Another lat long example searching geo circles 
------------------------------------------------
See [LatLongExampleTest.testSearchLatLongCircles()](src/test/java/com/github/davidmoten/rtree/LatLongExampleTest.java) for an example of searching circles around geographic points (using great circle distance).


What do I do with the Observable thing?
-------------------------------------------
Very useful, see [RxJava](http://github.com/ReactiveX/RxJava).

As an example, suppose you want to filter the search results then apply a function on each and reduce to some best answer:

```java
import rx.Observable;
import rx.functions.*;
import rx.schedulers.Schedulers;

Character result = 
    tree.search(Geometries.rectangle(8, 15, 30, 35))
        // filter for names alphabetically less than M
        .filter(entry -> entry.value() < "M")
        // get the first character of the name
        .map(entry -> entry.value().charAt(0))
        // reduce to the first character alphabetically 
        .reduce((x,y) -> x <= y ? x : y)
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
Check out the benchmarks below, but I recommend you do your own benchmarks because every data set will behave differently. If you don't want to benchmark then use the defaults. General rules based on the benchmarks:

* for data sets of <10,000 entries use the default R-tree (quadratic splitter with maxChildren=4)
* for data sets of >=10,000 entries use the star R-tree (R*-tree heuristics with maxChildren=4 by default)

Watch out though, the benchmark data sets had quite specific characteristics. The 1000 entry dataset was randomly generated (so is more or less uniformly distributed) and the *Greek* dataset was earthquake data with its own clustering characteristics. 


How do I just get an Iterable back from a search?
---------------------------------------------------------
If you are not familiar with the Observable API and want to skip the reactive stuff then here's how to get an ```Iterable``` from a search:

```java
Iterable<T> it = tree.search(Geometries.point(4,5))
                     .toBlocking().toIterable();
```

Backpressure
-----------------
The backpressure slow path may be enabled by some RxJava operators. This may slow search performance by a factor of 3 but avoids possible out of memory errors and thread starvation due to asynchronous buffering. Backpressure is benchmarked below.

Visualizer
--------------
To visualize the R-tree in a PNG file of size 600 by 600 pixels just call:
```java
tree.visualize(600,600)
    .save("target/mytree.png");
```
The result is like the images in the Features section above.

Visualize as text
--------------------
The ```RTree.asString()``` method returns output like this:

```
mbr=Rectangle [x1=10.0, y1=4.0, x2=62.0, y2=85.0]
  mbr=Rectangle [x1=28.0, y1=4.0, x2=34.0, y2=85.0]
    entry=Entry [value=2, geometry=Point [x=29.0, y=4.0]]
    entry=Entry [value=1, geometry=Point [x=28.0, y=19.0]]
    entry=Entry [value=4, geometry=Point [x=34.0, y=85.0]]
  mbr=Rectangle [x1=10.0, y1=45.0, x2=62.0, y2=63.0]
    entry=Entry [value=5, geometry=Point [x=62.0, y=45.0]]
    entry=Entry [value=3, geometry=Point [x=10.0, y=63.0]]
```

Serialization
------------------
Release 0.8 includes [flatbuffers](https://github.com/google/flatbuffers) support as a serialization format and as a lower performance but lower memory consumption (approximately one third) option for an RTree. 

The greek earthquake data (38,377 entries) when placed in a default RTree with `maxChildren=10` takes up 4,548,133 bytes in memory. If that data is serialized then reloaded into memory using the `InternalStructure.FLATBUFFERS_SINGLE_ARRAY` option then the RTree takes up 1,431,772 bytes in memory (approximately one third the memory usage). Bear in mind though that searches are much more expensive (at the moment) with this data structure because of object creation and gc pressures (see benchmarks). Further work would be to enable direct searching of the underlying array without object creation expenses required to match the current search routines. 

As of 5 March 2016, indicative RTree metrics using flatbuffers data structure are:

* one third the memory use with log(N) object creations per search
* one third the speed with backpressure (e.g. if `flatMap` or `observeOn` is downstream)
* one tenth the speed without backpressure 

Note that serialization uses an optional dependency on `flatbuffers`. Add the following to your pom dependencies:

```xml
<dependency>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>flatbuffers-java</artifactId>
    <version>1.3.0.1</version>
    <optional>true</optional>
</dependency>
```

## Serialization example

Write an `RTree` to an `OutputStream`:
```java
RTree<String, Point> tree = ...;
OutputStream os = ...;
Serializer<String, Point> serializer = 
  Serializers.flatBuffers().utf8();
serializer.write(tree, os); 
```

Read an `RTree` from an `InputStream` into a low-memory flatbuffers based structure:
```java
RTree<String, Point> tree = 
  serializer.read(is, lengthBytes, InnerStructure.SINGLE_ARRAY);
```

Read an `RTree` from an `InputStream` into a default structure:
```java
RTree<String, Point> tree = 
  serializer.read(is, lengthBytes, InnerStructure.DEFAULT);
```

Dependencies
---------------------
As of 0.7.5 this library does not depend on *guava* (>2M) but rather depends on *guava-mini* (11K). The `nearest` search used to depend on `MinMaxPriorityQueue` from guava but now uses a backport of Java 8 `PriorityQueue` inside a custom `BoundedPriorityQueue` class that gives about 1.7x the throughput as the guava class.

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
Coverity scan
----------------
This codebase is scanned by Coverity scan whenever the branch `coverity_scan` is updated. 

For the project committers if a coverity scan is desired just do this:

```bash
git checkout coverity_scan
git pull origin master
git push origin coverity_scan
```

### Notes
The *Greek* data referred to in the benchmarks is a collection of some 38,377 entries corresponding to the epicentres of earthquakes in Greece between 1964 and 2000. This data set is used by multiple studies on R-trees as a test case.

### Results

These were run on i7-920 @2.67GHz with *rtree* version 0.8-RC7:

```
Benchmark                                                               Mode  Cnt        Score       Error  Units

defaultRTreeInsertOneEntryInto1000EntriesMaxChildren004                thrpt   10   262260.993 ±  2767.035  ops/s
defaultRTreeInsertOneEntryInto1000EntriesMaxChildren010                thrpt   10   296264.913 ±  2836.358  ops/s
defaultRTreeInsertOneEntryInto1000EntriesMaxChildren032                thrpt   10   135118.271 ±  1722.039  ops/s
defaultRTreeInsertOneEntryInto1000EntriesMaxChildren128                thrpt   10   315851.452 ±  3097.496  ops/s
defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004           thrpt   10   278761.674 ±  4182.761  ops/s
defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010           thrpt   10   315254.478 ±  4104.206  ops/s
defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032           thrpt   10   214509.476 ±  1555.816  ops/s
defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128           thrpt   10   118094.486 ±  1118.983  ops/s
defaultRTreeSearchOf1000PointsMaxChildren004                           thrpt   10  1122140.598 ±  8509.106  ops/s
defaultRTreeSearchOf1000PointsMaxChildren010                           thrpt   10   569779.807 ±  4206.544  ops/s
defaultRTreeSearchOf1000PointsMaxChildren032                           thrpt   10   238251.898 ±  3916.281  ops/s
defaultRTreeSearchOf1000PointsMaxChildren128                           thrpt   10   702437.901 ±  5108.786  ops/s
defaultRTreeSearchOfGreekDataPointsMaxChildren004                      thrpt   10   462243.509 ±  7076.045  ops/s
defaultRTreeSearchOfGreekDataPointsMaxChildren010                      thrpt   10   326395.724 ±  1699.043  ops/s
defaultRTreeSearchOfGreekDataPointsMaxChildren032                      thrpt   10   156978.822 ±  1993.372  ops/s
defaultRTreeSearchOfGreekDataPointsMaxChildren128                      thrpt   10    68267.160 ±   929.236  ops/s
rStarTreeDeleteOneEveryOccurrenceFromGreekDataChildren010              thrpt   10   211881.061 ±  3246.693  ops/s
rStarTreeInsertOneEntryInto1000EntriesMaxChildren004                   thrpt   10   187062.089 ±  3005.413  ops/s
rStarTreeInsertOneEntryInto1000EntriesMaxChildren010                   thrpt   10   186767.045 ±  2291.196  ops/s
rStarTreeInsertOneEntryInto1000EntriesMaxChildren032                   thrpt   10    37940.625 ±   743.789  ops/s
rStarTreeInsertOneEntryInto1000EntriesMaxChildren128                   thrpt   10   151897.089 ±   674.941  ops/s
rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004              thrpt   10   237708.825 ±  1644.611  ops/s
rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010              thrpt   10   229577.905 ±  4234.760  ops/s
rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032              thrpt   10    78290.971 ±   393.030  ops/s
rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128              thrpt   10     6521.010 ±    50.798  ops/s
rStarTreeSearchOf1000PointsMaxChildren004                              thrpt   10  1330510.951 ± 18289.410  ops/s
rStarTreeSearchOf1000PointsMaxChildren010                              thrpt   10  1204347.202 ± 17403.105  ops/s
rStarTreeSearchOf1000PointsMaxChildren032                              thrpt   10   576765.468 ±  8909.880  ops/s
rStarTreeSearchOf1000PointsMaxChildren128                              thrpt   10  1028316.856 ± 13747.282  ops/s
rStarTreeSearchOfGreekDataPointsMaxChildren004                         thrpt   10   904494.751 ± 15640.005  ops/s
rStarTreeSearchOfGreekDataPointsMaxChildren010                         thrpt   10   649636.969 ± 16383.786  ops/s
rStarTreeSearchOfGreekDataPointsMaxChildren010FlatBuffers              thrpt   10    84230.053 ±  1869.345  ops/s
rStarTreeSearchOfGreekDataPointsMaxChildren010FlatBuffersBackpressure  thrpt   10    36420.500 ±  1572.298  ops/s
rStarTreeSearchOfGreekDataPointsMaxChildren010WithBackpressure         thrpt   10   116970.445 ±  1955.659  ops/s
rStarTreeSearchOfGreekDataPointsMaxChildren032                         thrpt   10   224874.016 ± 14462.325  ops/s
rStarTreeSearchOfGreekDataPointsMaxChildren128                         thrpt   10   358636.637 ±  4886.459  ops/s
searchNearestGreek                                                     thrpt   10     3715.020 ±    46.570  ops/s

```

