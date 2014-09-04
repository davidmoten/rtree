package com.github.davidmoten.rtree;

import java.io.IOException;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.runner.RunnerException;

public class BenchmarksMain {

	public static void main(String[] args) throws RunnerException, IOException {
		Main.main(args);
		// Options opt = new OptionsBuilder()
		// //
		// .include(BenchmarksRTree.class.getSimpleName())
		// //
		// .forks(1)
		// //
		// .warmupIterations(10)
		// //
		// .measurementIterations(10)
		// //
		// .jvmArgs("-Xmx512m")
		// //
		// .build();
		// new Runner(opt).run();
	}
}
