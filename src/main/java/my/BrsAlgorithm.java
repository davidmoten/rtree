package my;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import gr.unipi.generators.UniformGenerator;

public class BrsAlgorithm {
	private MyItem weightVector;
	private double queryScore;
	private int k;
	private ArrayList<Point> buffer;
	
	public BrsAlgorithm(int k) {
		buffer = new ArrayList<Point>(k);
		this.k = k;
	}
	
	private int processEntry(Entry<Object, Point> e) {
		if (buffer.contains(e.geometry())) {
			return 0;
		}
		else {
			//if (buffer.size() < 2 * k) {
				buffer.add(e.geometry());
			//}
			return 1;
		}
	}
	
	private int getCountOfPointsInNode(Node<Object, Point> n) {
		if (n instanceof Leaf) {
			int result = 0;
			List<Entry<Object, Point>> entries = ((Leaf<Object, Point>)n).entries();
			for (Entry<Object, Point> entry: entries) {
				result += processEntry(entry);
			}
			return result;
		}
		else {
			NonLeaf<Object, Point> nodes = (NonLeaf<Object, Point>)n;
			int result = 0;
			for (Node<Object, Point> child: nodes.children()) {
				result += getCountOfPointsInNode(child);
			}
			return result;
		}
	}
	
	private int processNode(Node<Object, Point> parent, int max) {
		int count = 0;
		if (parent instanceof NonLeaf) {
			NonLeaf<Object, Point> pNode = (NonLeaf<Object, Point>) parent;
			for (Node<Object, Point> child: pNode.children()) {
				if (Functions.calculateScore(child.geometry().mbr().high(), weightVector.values) < queryScore) {
					count += getCountOfPointsInNode(child);
				}
				else if (Functions.calculateScore(child.geometry().mbr().low(), weightVector.values) < queryScore) {
					count += processNode(child, max);
					if (count >= max) {
						return count;
					}
				}
			}
		}
		else {
			Leaf<Object, Point> lNode = (Leaf<Object, Point>) parent;
			for (Entry<Object, Point> child: lNode.entries()) {
				if (Functions.calculateScore(child.geometry().low(), weightVector.values) < queryScore) {
					count += processEntry(child);
				}
			}
		}
		return count;
		
		//return count;
	}
	
	private int checkBuffer() {
		int count = 0;
		for (Point p: buffer) {
			if (Functions.calculateScore(p.low(), weightVector.values) < queryScore) {
				count++;
			}
		}
		return count;
	}
	
	public boolean isWeightVectorInRtopk(MyItem queryPoint, RTree<Object, Point> tree, MyItem weight) {
		if ((tree.size() == 0) || (k == 0)) {
			return false;
		}
		
		this.weightVector = weight;
		this.queryScore = Functions.calculateScore(queryPoint, weight);
		
		int count = k - checkBuffer();
		if (count <= 0) {
			return false;
		}
		//System.out.println(count);
		return processNode(tree.root().get(), count) < count;
		//return processNodes(tree.getRoot());
	}
	
	public List<MyItem> computeRTOPk(RTree<Object, Point> tree, MyItem[] W, MyItem q) {
		
		List<MyItem> result = new ArrayList<MyItem>();
		for (int i = 0; i < W.length; i++) {
			if (isWeightVectorInRtopk(q, tree, W[i])) {
				result.add(W[i]);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		int dimensions = 4;
		int countS = 682503;
		int countW = 1000000;
		int k = 10;
		
		RTree<Object, Point> tree = RTree.create();
		UniformGenerator generator = new UniformGenerator(dimensions);
		MyItem q = new MyItem(new float[]{198878.2f,193482.569f,116926.0f,130030.834f});
		int count = 0;
		Point p;
		//ArrayList<MyItem> list = new ArrayList<>();
		while (count < countS) {
			p = Geometries.point(generator.nextPoint(10000000));
			if (Dominance.dominate(p.low(), q.values) >= 0) {
				tree = tree.add(null, p);
				//list.add(p);
				count++;
			}
		}
		System.out.println("CREATED RTREE!");
		System.out.printf("RTree size: %d\n", tree.size());
		System.out.printf("RTree depth: %d\n", tree.calculateDepth());
		//System.out.printf("List size: %d\n", list.size());
		long start = System.currentTimeMillis();
		//tree.sort();
		long end = System.currentTimeMillis();
		long elapsed = end - start;
		System.out.printf("Sorted in %d millis\n", elapsed);
		
		MyItem w;
		
		int count1 = 0, count2 = 0, count3 = 0;
		BrsAlgorithm brs = new BrsAlgorithm(k);
		//Rta rta = new Rta(k);
		//RtaWithTree rtat = new RtaWithTree();
		start = System.currentTimeMillis();
		for (int i = 0; i < countW; i++) {
			w = new MyItem(generator.nextNormalizedPointF());
			if (brs.isWeightVectorInRtopk(q, tree, w)) {
				count1++;
			}
			//if (rta.isWeightVectorInRtopk(list, w, q.values)) {
			//	count2++;
			//}
			//if (rtat.isWeightVectorInRtopk(tree, w, q.values, k)) {
			//	count3++;
			//}
		}
		end = System.currentTimeMillis();
		elapsed = end - start;
		double msecPerQuery = ((double) elapsed) / (double) countW;
		System.out.printf("Result count1: %d\nResult count2: %d\nResult count3: %d\nMillis elapsed: %d\nmsecPerQuery: %f\n",
				count1, count2, count3, elapsed, msecPerQuery);
		
	}
}
