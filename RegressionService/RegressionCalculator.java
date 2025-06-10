import java.util.List;

public class RegressionCalculator {
    public static double[] calculateLinearRegression(List<Double> x, List<Double> y) {
        int n = x.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;

        for (int i = 0; i < n; i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXY += x.get(i) * y.get(i);
            sumXX += x.get(i) * x.get(i);
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        return new double[]{slope, intercept}; // y = mx + b
    }
}
