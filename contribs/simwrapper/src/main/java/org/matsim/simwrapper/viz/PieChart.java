package org.matsim.simwrapper.viz;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PieChart extends Viz {

	@JsonProperty(required = true)
	public String dataset;

	/**
	 * If set to true, only the last row of the datafile
	 * will be used to build the pie chart. For example,
	 * this is useful for MATSim outputs which list every
	 * iteration's output, if you are only in the final iteration.
	 */
	@JsonProperty(required = true)
	public String useLastRow;

	/**
	 * Array of strings. List the column names of any columns
	 * that should be left out of the pie chart (e.g. Iteration).
	 * Example: ['Iteration']
	 */
	public String ignoreColumns;

	public PieChart() {
		super("pie");
	}
}
