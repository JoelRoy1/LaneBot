package LaneBot;

import lejos.hardware.Brick;
import lejos.hardware.Key;
import lejos.robotics.subsumption.Behavior;


/**
 * The EmergencyStop behaviour causes the robot to stop execution
 * on the press of the escape button.
 * @author Mustaeen Siddiqui, Joel Roy, Wilf Walrdon, Younis Abdi
 *
 */
public class EmergencyStop implements Behavior{
	private LaneBotPilot pilot;
	private boolean notification;
	private Brick brick;
	private Key escape = brick.getKey("Escape");
	
	public EmergencyStop(LaneBotPilot pilot, Brick brick) {
		this.pilot = pilot;
		this.brick = brick;
	}
	public boolean takeControl() {
		return escape.isDown();
		//return true;
	}
	public void action() {
		notification = false;
		
		pilot.stopMotor();
	}
	public void suppress() {
		notification = true;
	}
	
}