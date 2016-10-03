package my;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import gr.unipi.generators.UniformGenerator;

public class App {

	public static void main(String[] args) {
		int dimensions = 4;
		int countS = 682503;
		//int countW = 1000;
		//int k = 10;
		
		RTree<Object, Point> tree = RTree.star().create();
		UniformGenerator generator = new UniformGenerator(dimensions);
		float[] q = new float[]{198878.2f, 193482.569f, 116926.0f, 130030.834f};
		int count = 0;
		Point p;
		//ArrayList<MyItem> list = new ArrayList<>();
		while (count < countS) {
			p = Geometries.point(generator.nextPoint(10000000));
			if (Dominance.dominate(p.values(), q) >= 0) {
				tree = tree.add(null, p);
				//list.add(p);
				count++;
			}
		}
		System.out.println("DONE!");
		System.out.printf("Tree depth: %d\n", tree.calculateDepth());
		System.out.printf("Tree entries count: %d\n", tree.size());
	}

}
