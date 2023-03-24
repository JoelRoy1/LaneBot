package LaneBot;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;


/**
 * The ChangeSpeed behaviour makes the bot change speed
 * when it detects specific colours.
 * @author Mustaeen Siddiqui, Joel Roy, Wilf Walrdon, Younis Abdi
 *
 */
public class ChangeSpeed implements Behavior{
	
	
	private LaneBotPilot pilot;
	private EV3ColorSensor colorSensor;
	private float[] samples = new float[1];
	private SampleProvider spColour;
	
	float redV;
	float greenV;
	float blueV;
	float maxCol;
	
	boolean redLvl;
	boolean greenLvl;
	boolean blueLvl;
			
	public ChangeSpeed(LaneBotPilot pilot, EV3ColorSensor colorSensor) {
		this.pilot = pilot;
		this.colorSensor = colorSensor;
		spColour = colorSensor.getRGBMode();
	}
	
	@Override
	public boolean takeControl() {
		spColour.fetchSample(samples, 0);
		redV = samples[0];
		greenV = samples[1];
		blueV = samples[2];
		
		maxCol = 800;
		boolean redLvl = redV >= maxCol;
		boolean greenLvl = greenV >= maxCol;
		boolean blueLvl = blueV >= maxCol;
		return redLvl || greenLvl || blueLvl;
		
		
	}

	@Override
	public void action() {
		//this.pilot.setSpeed()
		//float maxValue = ;
		if (redLvl) {
			this.pilot.setDriveMotorSpeed(30);
		}
		if (greenLvl) {
			this.pilot.setDriveMotorSpeed(50);
		}
		if (blueLvl) {
			this.pilot.setDriveMotorSpeed(70);
		}
		
	}

	@Override
	public void suppress() {
		
		
	}

}