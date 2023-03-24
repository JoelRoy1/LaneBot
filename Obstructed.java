package LaneBot;

import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;


/**
 * The Obstructed behaviour makes the bot stop if it is about to collide
 * with an object or wall.
 * @author Mustaeen Siddiqui, Joel Roy, Wilf Walrdon, Younis Abdi
 *
 */
public class Obstructed implements Behavior{
	private LaneBotPilot pilot;
	private EV3UltrasonicSensor sensorfront;
	private float[] samples = new float[1];
	private float distThres = (float) .06;
	private SampleProvider spfront;

	public Obstructed(LaneBotPilot x, EV3UltrasonicSensor y) {
		this.pilot = x;
		this.sensorfront = y;
		spfront = sensorfront.getDistanceMode();
		
	}

	@Override
	public boolean takeControl() {
		spfront.fetchSample(samples, 0);
		return ((distThres > samples[0]));
	}

	@Override
	public void action() {
		System.out.println(samples[0]);
		pilot.setDriveMotorSpeed(0);
		while((distThres < samples[0])) {
			spfront.fetchSample(samples, 0);
			Delay.msDelay(50);
		}
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub
	}

}