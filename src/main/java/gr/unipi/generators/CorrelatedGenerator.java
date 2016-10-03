package gr.unipi.generators;

public class CorrelatedGenerator extends DistributionGenerator {

	public CorrelatedGenerator(int dimension) {
		super(dimension);
	}

	@Override
	public float[] nextPoint(int maxValue) {
		maxValue -= 1;
		float dims[] = new float[dimensionCount];
		float sigma1 = 0.15f;
		float sigma2 = 0.05f;
		float mean = 0.5f;
		float a = (float)rand.nextGaussian() * sigma1 + mean;
		float sum = 0;

		for (int s = 0; s < dimensionCount - 1; s++) {
			dims[s] = (float)rand.nextGaussian() * sigma2 + a;
			sum = sum + dims[s];
		}
		dims[dimensionCount - 1] = dimensionCount * a - sum;
		boolean valid = true;
		for (int s = 0; s < dimensionCount; s++) {
			dims[s] = maxValue * dims[s];
			if ((dims[s] > maxValue) || (dims[s] < 0)) {
				valid = false;
			}
		}
		if (valid)
			return dims;
		else
			return nextPoint(maxValue + 1);
	}

}
