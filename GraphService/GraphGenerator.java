import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphGenerator {

    public static void generateGraph(String crypto, List<DataPoint> data, String outputPath) throws IOException {
        TimeSeries series = new TimeSeries(crypto);

        for (DataPoint point : data) {
            Minute minute = new Minute(point.timestamp.getMinute(), point.timestamp.getHour(),
                    point.timestamp.getDayOfMonth(), point.timestamp.getMonthValue(), point.timestamp.getYear());

            series.addOrUpdate(minute, point.price);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Precio de " + crypto,
                "Tiempo",
                "USD",
                dataset);

        ChartUtils.saveChartAsPNG(new File(outputPath), chart, 800, 600);
    }

    public static void generateMultiGraph(List<String> cryptos, int hours, String outputPath) throws IOException {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        Map<String, List<DataPoint>> allData = DataReader.readMultipleCryptoHistory(cryptos, hours);

        for (String crypto : cryptos) {
            List<DataPoint> data = allData.getOrDefault(crypto, new ArrayList<>());
            TimeSeries series = new TimeSeries(crypto);

            for (DataPoint point : data) {
                Minute minute = new Minute(point.timestamp.getMinute(), point.timestamp.getHour(),
                        point.timestamp.getDayOfMonth(), point.timestamp.getMonthValue(), point.timestamp.getYear());

                series.addOrUpdate(minute, point.price);
            }

            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Criptomonedas seleccionadas",
                "Tiempo",
                "USD",
                dataset);

        ChartUtils.saveChartAsPNG(new File(outputPath), chart, 800, 600);
    }

    public static class DataPoint {
        public LocalDateTime timestamp;
        public double price;

        public DataPoint(LocalDateTime timestamp, double price) {
            this.timestamp = timestamp;
            this.price = price;
        }
    }
}
