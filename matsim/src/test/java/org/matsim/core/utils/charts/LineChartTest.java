/* *********************************************************************** *
 * project: org.matsim.*
 * BarChartTest.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.core.utils.charts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Rule;
import org.junit.Test;
import org.matsim.testcases.MatsimTestUtils;

/**
 * Test for {@link LineChart}
 *
 * @author mrieser
 */
public class LineChartTest {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();


	/**
	 * Test that a file was really generated, and that the image, when loaded, has the specified size.
	 * @throws IOException possible exception when reading the image for validation
	 */
	@Test public void testLineChartDemo() throws IOException {
		String imageFilename = utils.getOutputDirectory() + "linechart.png";
		Demo demo = new Demo();
		demo.createLineChart(imageFilename);

		File imagefile = new File(imageFilename);
		assertTrue(imagefile.exists());

		BufferedImage image = ImageIO.read(imagefile);
		assertEquals(800, image.getWidth(null));
		assertEquals(600, image.getHeight(null));
	}

}
