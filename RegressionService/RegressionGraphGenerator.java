import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RegressionGraphGenerator {

    public static void generate(String crypto, List<Double> x, List<Double> y, double[] coef, String outputPath) throws IOException {
        XYSeries series = new XYSeries("Datos originales");
        XYSeries regression = new XYSeries("Regresión lineal");

        for (int i = 0; i < x.size(); i++) {
            series.add(x.get(i), y.get(i));
        }

        regression.add((Number) x.get(0), (Number) (coef[0] * x.get(0) + coef[1]));
        regression.add((Number) x.get(x.size() - 1), (Number) (coef[0] * x.get(x.size() - 1) + coef[1]));


        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(regression);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Regresión lineal - " + crypto,
                "Tiempo (indexado)",
                "Precio (USD)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartUtils.saveChartAsPNG(new File(outputPath), chart, 800, 600);
    }
}
