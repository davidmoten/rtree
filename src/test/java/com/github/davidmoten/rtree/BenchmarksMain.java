package com.github.davidmoten.rtree;

import java.io.IOException;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarksMain {

	public static void main(String[] args) throws RunnerException, IOException {
		Options opt = new OptionsBuilder()
		//
				.include(BenchmarksRTree.class.getSimpleName())
				//
				.forks(1)
				//
				.warmupIterations(10)
				//
				.measurementIterations(10)
				//
				.jvmArgs("-Xmx512m")
				//
				.build();
		new Runner(opt).run();
	}
}
