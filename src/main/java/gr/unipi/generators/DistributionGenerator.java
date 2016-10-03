package gr.unipi.generators;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public abstract class DistributionGenerator {
	protected int dimensionCount;
	protected Random rand;
	
	public DistributionGenerator(int dimensionCount) {
		this.dimensionCount = dimensionCount;
		this.rand = new Random();
	}
	
	public static String convertPointToString(long id, float[] point) {
		StringBuilder result = new StringBuilder(id + "");
		for (int i = 0; i < point.length; i++) {
			result.append('\t');
			result.append(point[i]);
		}
		return result.toString();
	}
	
	public static String convertPointToString(long id, BigDecimal[] point) {
		StringBuilder result = new StringBuilder(id + "");
		for (int i = 0; i < point.length; i++) {
			result.append('\t');
			result.append(point[i].toPlainString());
		}
		return result.toString();
	}
	
	public static void generateToFile(DistributionGenerator generator, String fileName, long pointCount, int maxValue) throws IOException {
		PrintWriter out = new PrintWriter(fileName);
		try {
			float[] point;
			for (long i = 0; i < pointCount; i++) {
				point = generator.nextPoint(maxValue);
				out.println(convertPointToString(i, point));
			}
		}
		finally {
			out.close();
		}
	}
	
	public static void generateNormalizedToFile(DistributionGenerator generator, String fileName, long pointCount) throws IOException {
		PrintWriter out = new PrintWriter(fileName);
		try {
			float[] point;
			for (long i = 0; i < pointCount; i++) {
				point = generator.nextNormalizedPointF();
				out.println(convertPointToString(i, point));
			}
		}
		finally {
			out.close();
		}
	}
	
	public abstract float[] nextPoint(int maxValue);
	
	/**
     * Round to certain number of decimals
     * 
     * @param d
     * @param decimalPlace
     * @return
     */
	/*
	public static int convertToInt(float d, int digits) {
        //BigDecimal bd = new BigDecimal(Float.toString(d));
        //bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        //return bd.floatValue();
		int count = (int) Math.pow(10, digits);
		return (int) (d * count);
    }*/
	/*
	public String nextNormalizedPointS() {
		BigDecimal[] bPoint = nextNormalizedPointB();
		String res = bPoint[0].toPlainString();
		for (int i = 1; i < bPoint.length; i++) {
			res += "\t" + bPoint[i].toPlainString();
		}
		if (res.contains("-")) {
			throw new RuntimeException("111Weighting vectors negative!\t" + res);
		}
		
		return res;
	}*/
	
	public float[] nextNormalizedPointF() {
		BigDecimal[] bPoint = nextNormalizedPointB();
		float[] point = new float[bPoint.length];
		for (int i = 0; i < bPoint.length; i++) {
			point[i] = bPoint[i].floatValue();
		}
		
		return point;
	}
	
	public double[] nextNormalizedPointD() {
		BigDecimal[] bPoint = nextNormalizedPointB();
		double[] point = new double[bPoint.length];
		for (int i = 0; i < bPoint.length; i++) {
			point[i] = bPoint[i].doubleValue();
		}
		
		return point;
	}
	
	public BigDecimal[] nextNormalizedPointB() {
		//Generates a normalized point according to the exact descendant (i.e distribution) of this base class
		//To produce the normalized point, we first calculate the SUM of all the point[i] values.
		//This SUM is treated as the max value in the normalization formula, while the MIN value in the formula is always assumed as zero.
		
		float[] point = nextPoint(10000);
		
		BigDecimal[] bPoint = new BigDecimal[point.length];
		for (int i = 0; i < point.length; i++) {
			bPoint[i] = new BigDecimal(Float.toString(point[i]));
			bPoint[i].setScale(4, BigDecimal.ROUND_DOWN);
		}
		
		BigDecimal sum = new BigDecimal(0);
		sum.setScale(4, BigDecimal.ROUND_DOWN);
		BigDecimal sum1 = new BigDecimal(1);
		sum1.setScale(4, BigDecimal.ROUND_DOWN);
		for (int i = 0; i < bPoint.length; i++) {
			sum = sum.add(bPoint[i]);
		}
		
		for (int i = 0; i < bPoint.length; i++) {
			bPoint[i] = bPoint[i].divide(sum, 4, RoundingMode.DOWN);
			sum1 = sum1.subtract(bPoint[i]);
		}
		
		//A slight difference might appear in the calculated sum, because of the rounded values.
		bPoint[0] = bPoint[0].add(sum1);
		
		sum = new BigDecimal(0);
		sum.setScale(5, BigDecimal.ROUND_DOWN);
		sum1 = new BigDecimal(0);
		sum1.setScale(4, BigDecimal.ROUND_DOWN);
		for (int i = 0; i < bPoint.length; i++) {
			if (bPoint[i].compareTo(sum1) == -1) {
				throw new RuntimeException("Weighting vectors negative!" + i);
			}
			sum = sum.add(bPoint[i]);
		}
		
		sum1 = new BigDecimal(1);
		sum1.setScale(5, BigDecimal.ROUND_DOWN);
		if (!(sum.compareTo(sum1) == 0)) {
			throw new RuntimeException("Weighting vectors are not correct!");
		}
		
		return bPoint;
	}
	
	/*
	public float[] nextNormalizedPoint() {
		//Generates a normalized point according to the exact descendant (i.e distribution) of this base class
		//To produce the normalized point, we first calculate the SUM of all the point[i] values.
		//This SUM is treated as the max value in the normalization formula, while the MIN value in the formula is always assumed as zero.
		
		float[] point = nextPoint(10000);
		
		BigDecimal[] bPoint = new BigDecimal[point.length];
		for (int i = 0; i < point.length; i++) {
			bPoint[i] = new BigDecimal(Float.toString(point[i]));
			bPoint[i].setScale(4, BigDecimal.ROUND_DOWN);
		}
		
		
		BigDecimal sum = new BigDecimal(0);
		sum.setScale(4, BigDecimal.ROUND_DOWN);
		BigDecimal sum1 = new BigDecimal(1);
		sum1.setScale(4, BigDecimal.ROUND_DOWN);
		for (int i = 0; i < bPoint.length; i++) {
			sum = sum.add(bPoint[i]);
		}
		
		for (int i = 0; i < bPoint.length; i++) {
			bPoint[i] = bPoint[i].divide(sum, 4, RoundingMode.DOWN);
			sum1 = sum1.subtract(bPoint[i]);
		}
		
		//A slight difference might appear in the calculated sum, because of the rounded values.
		bPoint[0] = bPoint[0].add(sum1);
		
		sum = new BigDecimal(0);
		sum.setScale(4, BigDecimal.ROUND_DOWN);
		for (int i = 0; i < point.length; i++) {
			point[i] = bPoint[i].floatValue();
			if (point[i] < 0) {
				throw new RuntimeException("Weighting vectors negative!" + i);
			}
			sum = sum.add(bPoint[i]);
		}
		
		float summ = sum.floatValue();
		if (summ != 1.0f) {
			String err = "";
			for (int i = 0; i < point.length; i++) {
				err += point[i] + "\t";
			}
			err += "sum: " + summ;
			throw new RuntimeException("Weighting vectors are not correct!" + err);
		}
		
		return point;
	}*/

	public int getDimensionCount() {
		return dimensionCount;
	}

	public void setDimensionCount(int dimensionCount) {
		this.dimensionCount = dimensionCount;
	}
}
