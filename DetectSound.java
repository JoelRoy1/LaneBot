package LaneBot;

import lejos.hardware.sensor.NXTSoundSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;


/**
 * The DetectSound method makes the robot pull over when hearing a sound.
 * @author Mustaeen Siddiqui, Joel Roy, Wilf Walrdon, Younis Abdi
 *
 */
public class DetectSound implements Behavior {
	private NXTSoundSensor sensor;
	private LaneBotPilot pilot;
	private SampleProvider ears;
	private float[] samples = new float[1];
	private float Thres = (float) .8;
	
	public DetectSound(LaneBotPilot x, NXTSoundSensor y) {
		this.sensor = y;
		this.pilot = x;
		this.ears = sensor.getDBAMode();
	}

	@Override
	public boolean takeControl() {
		ears.fetchSample(samples, 0);
		return ((Thres < samples[0]));
	}

	@Override
	public void action() {
		pilot.pullOver();
		System.out.println("PULLING OVER!");
		ears.fetchSample(samples, 0);
		while((Thres < samples[0])) {
			ears.fetchSample(samples, 0);
			Delay.msDelay(50);
		}
		pilot.pullIn();
	}

	@Override
	public void suppress() {
	}
}