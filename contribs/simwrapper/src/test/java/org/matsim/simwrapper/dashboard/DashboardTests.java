package org.matsim.simwrapper.dashboard;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.application.MATSimApplication;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.simwrapper.Dashboard;
import org.matsim.simwrapper.SimWrapper;
import org.matsim.simwrapper.TestScenario;
import org.matsim.testcases.MatsimTestUtils;

import java.nio.file.Path;

public class DashboardTests {
	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();

	private void run(Dashboard... dashboards) {

		Config config = TestScenario.loadConfig(utils);

		SimWrapper sw = SimWrapper.create();
		for (Dashboard d : dashboards) {
			sw.addDashboard(d);
		}

		Controler controler = MATSimApplication.prepare(new TestScenario(sw), config);
		controler.run();
	}

	@Test
	public void stuckAgents() {

		Path out = Path.of(utils.getOutputDirectory(), "analysis", "population");

		run(new StuckAgentDashboard());

		Assertions.assertThat(out)
				.isDirectoryContaining("glob:**stuck_agents.md");

	}

	@Test
	public void trip() {

		Path out = Path.of(utils.getOutputDirectory(), "analysis", "population");

		run(new TripDashboard());

		Assertions.assertThat(out)
				.isDirectoryContaining("glob:**trip_stats.csv")
				.isDirectoryContaining("glob:**mode_share.csv");

	}

}
