package gr.unipi.generators;

public class UniformGenerator extends DistributionGenerator {
	
	public UniformGenerator(int dimension) {
		super(dimension);
	}

	@Override
	public float[] nextPoint(int maxValue) {
		maxValue -= 1;
		float dims[] = new float[dimensionCount];
		for (int s = 0; s < dimensionCount; s++) {
			dims[s] = maxValue * rand.nextFloat();
			while ((dims[s] > maxValue) || (dims[s] < 0)) {
				//throw new RuntimeErrorException(null);
				dims[s] = maxValue * rand.nextFloat();
			}
		}
		return dims;
	}
	
	public double[] nextPointD(int maxValue) {
		maxValue -= 1;
		double dims[] = new double[dimensionCount];
		for (int s = 0; s < dimensionCount; s++) {
			dims[s] = maxValue * rand.nextDouble();
			while ((dims[s] > maxValue) || (dims[s] < 0)) {
				//throw new RuntimeErrorException(null);
				dims[s] = maxValue * rand.nextDouble();
			}
		}
		return dims;
	}

}
