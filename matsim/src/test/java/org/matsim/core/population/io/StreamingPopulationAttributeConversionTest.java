
/* *********************************************************************** *
 * project: org.matsim.*
 * PopulationAttributeConversionTest.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
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

package org.matsim.core.population.io;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.utils.objectattributes.AttributeConverter;

public class StreamingPopulationAttributeConversionTest {

	@Rule
	public final MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void testDefaults() {
		final String path = utils.getOutputDirectory() + "/plans.xml";

		testWriteAndRereadStreaming((w, persons) -> {
			w.startStreaming(path);
			persons.forEach(w::writePerson);
			w.closeStreaming();
		}, r -> r.readFile(path));
	}

	public void testWriteAndRereadStreaming(
			final BiConsumer<StreamingPopulationWriter, Collection<? extends Person>> writeMethod,
			final Consumer<StreamingPopulationReader> readMethod) {
		final Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		final Population population = scenario.getPopulation();
		final PopulationFactory populationFactory = population.getFactory();

		final Id<Person> personId = Id.createPersonId("Gaston");
		final Person person = populationFactory.createPerson(personId);
		final CustomClass attribute = new CustomClass("homme à tout faire");
		person.getAttributes().putAttribute("job", attribute);
		population.addPerson(person);

		final StreamingPopulationWriter writer = new StreamingPopulationWriter();
		writer.putAttributeConverter(CustomClass.class, new CustomClassConverter());
		writeMethod.accept(writer, population.getPersons().values());

		final Scenario readScenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		final Scenario streamingReadScenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		final StreamingPopulationReader reader = new StreamingPopulationReader(streamingReadScenario);
		reader.addAlgorithm(readScenario.getPopulation()::addPerson);
		reader.putAttributeConverter(CustomClass.class, new CustomClassConverter());
		readMethod.accept(reader);

		final Person readPerson = readScenario.getPopulation().getPersons().get(personId);
		final Object readAttribute = readPerson.getAttributes().getAttribute("job");

		Assert.assertEquals(
				"unexpected read attribute",
				attribute,
				readAttribute);
	}

	private static class CustomClass {

		private final String value;

		private CustomClass(String value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			CustomClass that = (CustomClass) o;
			return Objects.equals(value, that.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(value);
		}
	}

	private static class CustomClassConverter implements AttributeConverter<CustomClass> {

		@Override
		public CustomClass convert(String value) {
			return new CustomClass(value);
		}

		@Override
		public String convertToString(Object o) {
			return ((CustomClass) o).value;
		}
	}
}
