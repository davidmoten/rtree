package my;


public class Functions {
		
	public static float calculateScore(MyItem w, MyItem p) {
		return calculateScore(w.getValues(), p.getValues());
	}
	
	public static float calculateScore(float[] w, float[] p) {
		if (w.length != p.length) {
			throw new IllegalArgumentException("Must have the same dimentions in order to calculate f!!!");
		}
		
		float score = 0;
		for (int i = 0; i < w.length; i++) {
			score += w[i] * p[i];
		}

		return score;
	}
	
	public static double calculateCosine(float[] values) {
		double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < values.length; i++) {
	        dotProduct += values[i] / values.length;
	        normA += Math.pow(values[i], 2);
	        normB += Math.pow(1 / values.length, 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
}
