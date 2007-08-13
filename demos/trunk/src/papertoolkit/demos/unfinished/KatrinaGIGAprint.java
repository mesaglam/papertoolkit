package papertoolkit.demos.unfinished;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import papertoolkit.util.graphics.ImageUtils;


/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * For good chart colors, see http://www.personal.psu.edu/cab38/ColorBrewer/ColorBrewer_intro.html
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class KatrinaGIGAprint {

	public static void main(String[] args) {
		new KatrinaGIGAprint();
	}

	/**
	 * Creates a new demo instance.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public KatrinaGIGAprint() {
		CategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);
		BufferedImage image = chart.createBufferedImage(640, 480);
		ImageUtils.writeImageToPNG(image, new File("output/KatrinaChart.png"));
		
		
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(500, 270));

		JFrame f = new JFrame();
		f.setSize(1024, 768);
		f.add(chartPanel);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Creates a sample chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return The chart.
	 */
	private JFreeChart createChart(CategoryDataset dataset) {

		// create the chart...
		JFreeChart chart = ChartFactory.createBarChart("Hurricane Katrina Effects", // chart title
				"Category", // domain axis label
				"Value", // range axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
				false, // include legend
				false, // tooltips?
				false // URLs?
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);
		chart.getTitle().setFont(new Font("Trebuchet MS", Font.BOLD, 36));
		// System.out.println("Padding: " + chart.getPadding());
		// System.out.println("Antialiased: " + chart.getAntiAlias()); // true by default

		// get a reference to the plot for further customisation...
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		rangeAxis.setVisible(false); // hide the range

		// disable bar outlines...
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);

		renderer.setItemMargin(.0); // margin between bars

		// set up gradient paints for series...
		renderer.setSeriesPaint(0, new Color(239, 138, 98, 200));
		renderer.setSeriesPaint(1, new Color(103, 169, 207, 200));

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(0));
		System.out.println("Category Margin: " + domainAxis.getCategoryMargin());
		domainAxis.setCategoryMargin(.2);
		System.out.println("Width Ratio: " + domainAxis.getMaximumCategoryLabelWidthRatio());
		domainAxis.setMaximumCategoryLabelWidthRatio(0);
		domainAxis.setLabel(""); // the category axis label
		// OPTIONAL CUSTOMISATION COMPLETED.
		return chart;
	}

	/**
	 * Returns a sample dataset.
	 * 
	 * @return The dataset.
	 */
	private CategoryDataset createDataset() {

		// row keys...
		String series1 = "Before Katrina";
		String series2 = "After Katrina";

		// column keys...
		String category1 = "Housing";
		String category2 = "Unemployed";
		String category3 = "Employed";
		String category4 = "Population 18 years and over";
		String category5 = "Population Total";

		// create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(1.0, series1, category1);
		dataset.addValue(4.0, series1, category2);
		dataset.addValue(3.0, series1, category3);
		dataset.addValue(5.0, series1, category4);
		dataset.addValue(5.0, series1, category5);

		dataset.addValue(5.0, series2, category1);
		dataset.addValue(7.0, series2, category2);
		dataset.addValue(6.0, series2, category3);
		dataset.addValue(8.0, series2, category4);
		dataset.addValue(4.0, series2, category5);

		return dataset;

	}

}
