/* *********************************************************************** *
 * project: org.matsim.*
 * CANode.java
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

package teach.multiagent07.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.matsim.basic.v01.BasicNodeImpl;
import org.matsim.basic.v01.IdImpl;
import org.matsim.utils.geometry.CoordI;

import teach.multiagent07.simulation.Vehicle;


public class CANode extends BasicNodeImpl {

	public void move(int time) {
		randomMove(time);
		//moveVorfahrt12(time);
		//moveAmpel15(time);
	}

	private void moveVehicleOverNode(CALink link) {
		Vehicle veh = link.getFirstVeh();

		if (veh != null) {
			CALink outlink = veh.getNextLink(outlinks.values());

			if (outlink.hasSpace()) {
				link.removeFirstVeh();
				outlink.addVeh(veh);
				// Tell vehicle it is one link further
				veh.setCurrentLink(outlink);
			}
		}
	}

	private void randomMove(int time) {
		List<CALink> inList = new ArrayList<CALink>();

		for (Object in : inlinks.values()) inList.add((CALink)in);
		Collections.shuffle(inList);

		for (CALink link : inList) {
			moveVehicleOverNode(link);
		}

	}

	private IdImpl vorfahrtLinkId = new IdImpl("15");

	private void moveVorfahrt12(int time) {
		if (this.id.equals(new IdImpl("12")) ){

			CALink specialLink = (CALink)inlinks.get(vorfahrtLinkId);
			moveVehicleOverNode(specialLink);

			// Code from randomMove!
			List<CALink> inList = new ArrayList<CALink>();
			for (Object link : inlinks.values()) inList.add((CALink)link);
			Collections.shuffle(inList);

			for (CALink link : inList) {
				if (!link.getId().equals(vorfahrtLinkId))
					 moveVehicleOverNode(link);
			}
		} else randomMove(time);
	}

	private IdImpl graderLinkId = new IdImpl("22");
	private IdImpl ungraderLinkId = new IdImpl("26");

	private void moveAmpel15(int time) {
		if (this.id.equals(new IdImpl("15")) ){

			int phase = time /60;
			if (phase %2 == 0 ) {
				CALink specialLink = (CALink)inlinks.get(graderLinkId);
				moveVehicleOverNode(specialLink);
			}else {
				CALink specialLink = (CALink)inlinks.get(ungraderLinkId);
				moveVehicleOverNode(specialLink);
			}
		} else randomMove(time);
	}

	public CANode(String id) {
		super(id);
	}

	// additional data
	private CoordI coord;

	public void setCoord(CoordI coord) {
		this.coord = coord;
	}

	@Override
	public CoordI getCoord() {
		return this.coord;
	}

}
