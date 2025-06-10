import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GraphGenerator {

    public static void generateGraph(String crypto, List<DataPoint> data, String outputPath) throws IOException {
        TimeSeries series = new TimeSeries(crypto);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        for (DataPoint point : data) {
            LocalDateTime timestamp = LocalDateTime.parse(point.timestamp, formatter);
            Minute minute = new Minute(timestamp.getMinute(), timestamp.getHour(),
                    timestamp.getDayOfMonth(), timestamp.getMonthValue(), timestamp.getYear());

            series.addOrUpdate(minute, point.price);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Precio de " + crypto,
                "Tiempo",
                "USD",
                dataset
        );

        ChartUtils.saveChartAsPNG(new File(outputPath), chart, 800, 600);
    }

    public static void generateMultiGraph(List<String> cryptos, int hours, String outputPath) throws IOException {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        for (String crypto : cryptos) {
            List<DataPoint> data = DataReader.readCryptoHistory(crypto, hours);
            TimeSeries series = new TimeSeries(crypto);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            for (DataPoint point : data) {
                LocalDateTime timestamp = LocalDateTime.parse(point.timestamp, formatter);
                Minute minute = new Minute(timestamp.getMinute(), timestamp.getHour(),
                        timestamp.getDayOfMonth(), timestamp.getMonthValue(), timestamp.getYear());

                series.addOrUpdate(minute, point.price);
            }

            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Criptomonedas seleccionadas",
                "Tiempo",
                "USD",
                dataset
        );

        ChartUtils.saveChartAsPNG(new File(outputPath), chart, 800, 600);
    }


    public static class DataPoint {
        public String timestamp;
        public double price;

        public DataPoint(String timestamp, double price) {
            this.timestamp = timestamp;
            this.price = price;
        }
    }
}
