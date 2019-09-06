package org.matsim.contrib.accidents;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.testcases.MatsimTestUtils;

public class RunTestEquil {
	
	private static String configFile;
	private static String outputDirectory;
    private static String runId;
    
    @Rule public MatsimTestUtils utils = new MatsimTestUtils();
    
    @Test 
    public void test1() 
    {
    configFile = utils.getPackageInputDirectory() + "/equil_scenario/config.xml";
    outputDirectory = utils.getOutputDirectory();
    runId = "run1";
    
    Config config = ConfigUtils.loadConfig(configFile);
    config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
    
    config.controler().setOutputDirectory(outputDirectory);
    config.controler().setRunId(runId);
    //Is better to take the initial config where just one of the routes is selected or better take the plans of the tenth iteration?
    config.controler().setLastIteration(0);
    
    
    AccidentsConfigGroup accidentsSettings = ConfigUtils.addOrGetModule(config,  AccidentsConfigGroup.class);
    accidentsSettings.setEnableAccidentsModule(true);
    
    final Scenario scenario = ScenarioUtils.loadScenario(config);
    Controler controler = new Controler (scenario);
    controler.addOverridingModule(new AccidentsModule(scenario));
    //how can be changed the parameters of the accidents Module
    //TODO: changing through programming the free speed and number o lanes of some links so they would not be allways categorized with the same roadtype.
    //wich implications has the type of the config file? the test does not work with the output configs
 
    controler.run();

    //the programm runs there is problems with the calculation of the total costs from the link 1
    //Link 1 three vehicles are not taken into account for the calculation
    
    BufferedReader br = IOUtils.getBufferedReader(outputDirectory + "ITERS/it.0/run1.0.accidentCosts_BVWP.csv");
	
	String line = null;
	try {
		line = br.readLine();
	} catch (IOException e) {
		e.printStackTrace();
	} // headers

	try {
		int lineCounter = 0;
		while ((line = br.readLine()) != null) {
			
			String[] columns = line.split(";");
			for (int column = 0; column < columns.length; column++) {
				
				if (lineCounter == 1 && column == 121) {
					double accidentCosts = Double.valueOf(columns[column]);
					Assert.assertEquals("wrong accident costs", 2162.475, accidentCosts , 0.01);	
					//Manuel nachgerechnet: STIMMT!
				}
				
				if (lineCounter == 12 && column == 121) {
					double accidentCosts = Double.valueOf(columns[column]);
					Assert.assertEquals("wrong accident costs", 617.85, accidentCosts , 0.01);
					//Manuel nachgerechnet: STIMMT!
				}
									
			}
			
			lineCounter++;
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}