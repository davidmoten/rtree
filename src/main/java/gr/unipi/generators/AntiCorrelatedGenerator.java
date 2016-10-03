package gr.unipi.generators;

import java.util.Random;

public class AntiCorrelatedGenerator extends DistributionGenerator {
	private Random uniform;
	
	public AntiCorrelatedGenerator(int dimension) {
		super(dimension);
		this.uniform = new Random();
	}

	@Override
	public float[] nextPoint(int maxValue) {
		maxValue -= 1;
		float sigma = 0.005f; // Tao uses 0.005
		float mean = 0.5f;
		float dims[] = new float[dimensionCount];
		float a = (float)rand.nextGaussian() * sigma + mean;
		float sum = 0;
		for (int s = 0; s < dimensionCount - 1; s++) {
			dims[s] = uniform.nextFloat();
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
