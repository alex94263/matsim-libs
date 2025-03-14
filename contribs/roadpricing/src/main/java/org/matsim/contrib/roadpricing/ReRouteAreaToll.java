/*
 *  *********************************************************************** *
 *  * project: org.matsim.*
 *  * ReRouteAreaToll.java
 *  *                                                                         *
 *  * *********************************************************************** *
 *  *                                                                         *
 *  * copyright       : (C) 2015 by the members listed in the COPYING, *
 *  *                   LICENSE and WARRANTY file.                            *
 *  * email           : info at matsim dot org                                *
 *  *                                                                         *
 *  * *********************************************************************** *
 *  *                                                                         *
 *  *   This program is free software; you can redistribute it and/or modify  *
 *  *   it under the terms of the GNU General Public License as published by  *
 *  *   the Free Software Foundation; either version 2 of the License, or     *
 *  *   (at your option) any later version.                                   *
 *  *   See also COPYING, LICENSE and WARRANTY file                           *
 *  *                                                                         *
 *  * ***********************************************************************
 */

package org.matsim.contrib.roadpricing;

import org.matsim.core.config.Config;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;
import org.matsim.core.replanning.selectors.RandomPlanSelector;
import org.matsim.core.router.TripRouter;
import org.matsim.core.utils.timing.TimeInterpretation;

import javax.inject.Inject;
import javax.inject.Provider;

 class ReRouteAreaToll implements Provider<PlanStrategy> {

	private final Config config;
	private RoadPricingScheme roadPricingScheme;
	private Provider<TripRouter> tripRouterFactory;
	private final TimeInterpretation timeInterpretation;
//	private final Provider<PlansCalcRouteWithTollOrNot> factory;

	@Inject ReRouteAreaToll( Config config, RoadPricingScheme roadPricingScheme, Provider<TripRouter> tripRouterFactory, TimeInterpretation timeInterpretation ) {
		this.config = config;
//		this.factory = factory;
		this.roadPricingScheme = roadPricingScheme;
		this.tripRouterFactory = tripRouterFactory;
		this.timeInterpretation = timeInterpretation;
	}

	@Override
	public PlanStrategy get() {
		PlanStrategyImpl.Builder builder = new PlanStrategyImpl.Builder(new RandomPlanSelector<>());
		builder.addStrategyModule(new AbstractMultithreadedModule(config.global()) {
			@Override
			public PlanAlgorithm getPlanAlgoInstance() {
				return new PlansCalcRouteWithTollOrNot( roadPricingScheme, tripRouterFactory, timeInterpretation ) ;
			}
		});
		return builder.build();
	}
}
