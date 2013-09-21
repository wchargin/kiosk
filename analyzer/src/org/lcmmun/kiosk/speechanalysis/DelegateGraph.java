package org.lcmmun.kiosk.speechanalysis;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class DelegateGraph extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The index used for data.
	 */
	private final SpeechIndex index;

	/**
	 * The plot for this graph.
	 */
	private final ChartPanel cp;

	/**
	 * Creates the graph with the given {@code SpeechIndex}.
	 * 
	 * @param index
	 *            the index to use
	 */
	public DelegateGraph(SpeechIndex index) {
		super(new BorderLayout());
		this.index = index;
		cp = new ChartPanel(createChart());
		add(cp, BorderLayout.CENTER);
	}

	/**
	 * Creates a chart with the current data.
	 * 
	 * @return the new chart
	 */
	private JFreeChart createChart() {
		LinkedHashSet<String> names = new LinkedHashSet<String>();
		for (SpeechAnalysis analysis : index) {
			names.add(analysis.delegateName);
		}
		DefaultCategoryDataset dcd = new DefaultCategoryDataset();
		Map<String, Integer> totalSpeeches = new HashMap<String, Integer>();
		for (String name : names) {
			totalSpeeches.put(name, index.getTotalSpeechesByDelegate(name));
		}

		List<String> mapKeys = new ArrayList<String>(totalSpeeches.keySet());
		List<Integer> mapValues = new ArrayList<Integer>(totalSpeeches.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);
		Collections.reverse(mapValues);
		Collections.reverse(mapKeys);

		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

		Iterator<Integer> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Integer val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				String key = keyIt.next();
				String comp1 = totalSpeeches.get(key).toString();
				String comp2 = val.toString();
				if (comp1.equals(comp2)) {
					totalSpeeches.remove(key);
					mapKeys.remove(key);
					sortedMap.put(key, val);
					break;
				}
			}
		}

		for (String name : sortedMap.keySet()) {
			dcd.addValue(index.getCaucusSpeechesByDelegate(name),
					Messages.getString("DelegateGraph.Caucus"), name); //$NON-NLS-1$
			dcd.addValue(index.getGslSpeechesByDelegate(name), Messages.getString("DelegateGraph.GSL"), //$NON-NLS-1$
					name);
		}

		JFreeChart chart = ChartFactory.createStackedBarChart(Messages.getString("DelegateGraph.GraphTitle"), //$NON-NLS-1$
				Messages.getString("DelegateGraph.AxisHorizontal"), Messages.getString("DelegateGraph.AxisVertical"), dcd, PlotOrientation.VERTICAL, //$NON-NLS-1$ //$NON-NLS-2$
				true, true, false);
		chart.getCategoryPlot().getRangeAxis()
				.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		return chart;
	}

	/**
	 * Refreshes the plot with the current data.
	 */
	public void refreshPlot() {
		JFreeChart chart = createChart();
		cp.setChart(chart);
		cp.repaint();
	}
}
