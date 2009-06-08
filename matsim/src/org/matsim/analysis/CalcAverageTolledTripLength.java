/* *********************************************************************** *
 * project: org.matsim.*
 * CalcAverageTolledTripLength.java
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

package org.matsim.analysis;

import java.util.TreeMap;

import org.matsim.api.basic.v01.Id;
import org.matsim.api.basic.v01.events.BasicAgentArrivalEvent;
import org.matsim.api.basic.v01.events.BasicLinkEnterEvent;
import org.matsim.api.basic.v01.events.handler.BasicAgentArrivalEventHandler;
import org.matsim.api.basic.v01.events.handler.BasicLinkEnterEventHandler;
import org.matsim.core.api.network.Link;
import org.matsim.core.api.network.Network;
import org.matsim.roadpricing.RoadPricingScheme;
import org.matsim.roadpricing.RoadPricingScheme.Cost;

/**
 * Calculates the distance of a trip which occurred on tolled links.
 * Requires roadpricing to be on.
 *
 * @author mrieser
 */
public class CalcAverageTolledTripLength implements BasicLinkEnterEventHandler, BasicAgentArrivalEventHandler {

	private double sumLength = 0.0;
	private int cntTrips = 0;
	private RoadPricingScheme scheme = null;
	private Network network = null;
	private TreeMap<Id, Double> agentDistance = null;
	
	private static Double zero = Double.valueOf(0.0);

	public CalcAverageTolledTripLength(final Network network, final RoadPricingScheme scheme) {
		this.scheme = scheme;
		this.network = network;
		this.agentDistance = new TreeMap<Id, Double>();
	}

	public void handleEvent(final BasicLinkEnterEvent event) {
		Cost cost = this.scheme.getLinkCost(event.getLinkId(), event.getTime());
		if (cost != null) {
			Link link = this.network.getLink(event.getLinkId());
			if (link != null) {
				Double length = this.agentDistance.get(event.getPersonId());
				if (length == null) {
					length = zero;
				}
				length = Double.valueOf(length.doubleValue() + link.getLength());
				this.agentDistance.put(event.getPersonId(), length);
			}
		}
	}

	public void handleEvent(final BasicAgentArrivalEvent event) {
		Double length = this.agentDistance.get(event.getPersonId());
		if (length != null) {
			this.sumLength += length.doubleValue();
			this.agentDistance.put(event.getPersonId(), zero);
		}
		this.cntTrips++;
	}

	public void reset(final int iteration) {
		this.sumLength = 0.0;
		this.cntTrips = 0;
	}

	public double getAverageTripLength() {
		if (this.cntTrips == 0) return 0;
		return (this.sumLength / this.cntTrips);
	}
}
