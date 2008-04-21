/* *********************************************************************** *
 * project: org.matsim.*
 * KmlNetworkWriter.java
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
package org.matsim.trafficlights.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.matsim.basic.v01.Id;
import org.matsim.basic.v01.IdImpl;
import org.matsim.utils.collections.Tuple;
import org.matsim.utils.io.MatsimXmlParser;
import org.xml.sax.Attributes;


/**
 * @author dgrether
 *
 */
public class SignalGroupDefinitionParser extends MatsimXmlParser {

	private static final Logger log = Logger.getLogger(SignalGroupDefinitionParser.class);

	private static final String SIGNALGROUP = "signalGroup";

	private static final String  FROMLANE = "fromLane";

	private static final String TOLANE = "toLane";

	private static final String TURNIFRED = "turnIfRed";

	private static final String LINK = "link";

	private static final String PASSINGCLEARINGTIME = "passingClearingTime";

	private static final String ID = "id";

	private static final String REFID = "refId";

	private static final String LANES = "lanes";

	private static final String LENGTH = "length";

	private List<SignalGroupDefinition> signalGroups;

	private SignalGroupDefinition currentSignalGroup;

	private Id currentLaneId;

	private double currentLaneLength;

	private SignalLane currentLane;

	/**
	 * This a bit sophisticated map has as key value a tuple consisting of the link id and the id of the SignalLane
	 * respectively.
	 */
	private Map<Tuple<Id, Id>, SignalLane> linkLaneMap = new HashMap<Tuple<Id, Id>, SignalLane>();

	/**
	 *
	 */
	public SignalGroupDefinitionParser(List<SignalGroupDefinition> signalGroups) {
		this.signalGroups = signalGroups;
		this.setNamespaceAware(true);
	}

	/**
	 * @see org.matsim.utils.io.MatsimXmlParser#endTag(java.lang.String, java.lang.String, java.util.Stack)
	 */
	@Override
	public void endTag(String name, String content, Stack<String> context) {
		if (TURNIFRED.equalsIgnoreCase(name)) {
			this.currentSignalGroup.setTurnIfRed("true".compareToIgnoreCase(content.trim()) == 0);
		}
		else if (PASSINGCLEARINGTIME.equalsIgnoreCase(name)) {
			this.currentSignalGroup.setPassingClearingTime(Integer.valueOf(content.trim()));
		}
		else if (SIGNALGROUP.equalsIgnoreCase(name)) {
			this.signalGroups.add(this.currentSignalGroup);
		}
		else if (TOLANE.equalsIgnoreCase(name)) {
			this.currentSignalGroup.addToLane(this.currentLane);
		}
		else if (FROMLANE.equalsIgnoreCase(name)) {
			this.currentSignalGroup.addFromLane(this.currentLane);
		}

	}

	/**
	 * @see org.matsim.utils.io.MatsimXmlParser#startTag(java.lang.String, org.xml.sax.Attributes, java.util.Stack)
	 */
	@Override
	public void startTag(String name, Attributes atts, Stack<String> context) {
		if (SIGNALGROUP.equalsIgnoreCase(name)) {
			this.currentSignalGroup = new SignalGroupDefinition(new IdImpl(atts.getValue(ID)));
		}
		else if (FROMLANE.equalsIgnoreCase(name) || TOLANE.equalsIgnoreCase(name)) {
			this.currentLaneId = new IdImpl(atts.getValue(ID));
			this.currentLaneLength = Double.parseDouble(atts.getValue(LENGTH));
		}
		else if (LINK.equalsIgnoreCase(name)) {
			Id id = new IdImpl(atts.getValue(REFID));
			Tuple<Id, Id> key = new Tuple<Id, Id>(id, this.currentLaneId);
			this.currentLane = this.linkLaneMap.get(key);
			if (this.currentLane == null) {
				this.currentLane = new SignalLane(this.currentLaneId, id);
				this.currentLane.setLength(this.currentLaneLength);
				this.linkLaneMap.put(key, this.currentLane);
			}
			else {
				//if the lane for the link already exists it was added to another signalgroup. this
				// implies it must be a mixed lane
				this.currentLane.setMixedLane(true);
				if (this.currentLaneLength != this.currentLane.getLength()) {
					throw new IllegalArgumentException("Data is corrupt: Lane is defined in two or more SignalGroups with different length!");
				}
			}
		}

	}

}
