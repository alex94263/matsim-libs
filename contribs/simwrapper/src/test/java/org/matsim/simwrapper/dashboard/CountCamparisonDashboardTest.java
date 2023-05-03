package org.matsim.simwrapper.dashboard;

import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.application.MATSimApplication;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.network.NetworkUtils;
import org.matsim.counts.Counts;
import org.matsim.counts.CountsModule;
import org.matsim.counts.CountsWriter;
import org.matsim.examples.ExamplesUtils;
import org.matsim.simwrapper.SimWrapper;
import org.matsim.simwrapper.TestScenario;
import org.matsim.testcases.MatsimTestUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.SplittableRandom;

public class CountCamparisonDashboardTest {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void generate() {

		Config config = TestScenario.loadConfig(utils);

		generateDummyCounts(config);

		SimWrapper sw = SimWrapper.create()
				.addDashboard(new CountCamparisonDashboard());

		Controler controler = MATSimApplication.prepare(new TestScenario(sw), config);
		controler.addOverridingModule(new CountsModule());
		controler.run();
	}

	public void generateDummyCounts(Config config){

		SplittableRandom random = new SplittableRandom(1234);

		URL context = ExamplesUtils.getTestScenarioURL("kelheim");
		Network network = NetworkUtils.readNetwork(context + config.network().getInputFile());

		List<? extends Link> links = List.copyOf(network.getLinks().values());
		int size = links.size();

		Counts<Link> counts = new Counts<>();

		for(int i = 0; i <= 100; i++){
			Link link = links.get(random.nextInt(size));

			counts.createAndAddCount(link.getId(), link.getId().toString() + "_count_station")
					.createVolume(1, random.nextInt(1000));
		}

		try {
			Files.createDirectories(Path.of(utils.getPackageInputDirectory()));
			String absolutPath = Path.of(utils.getPackageInputDirectory()).normalize().toAbsolutePath().toString() + "/dummy_counts.xml";

			config.counts().setInputFile(absolutPath);
			new CountsWriter(counts).write(absolutPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
