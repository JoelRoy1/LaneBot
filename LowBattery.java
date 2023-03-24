package LaneBot;

import lejos.hardware.lcd.LCD;
import lejos.hardware.BrickFinder;
import lejos.hardware.Power;
import lejos.robotics.subsumption.Behavior;


/**
 * The low battery behaviour sends a warning message when battery is low.
 * @author Mustaeen Siddiqui, Joel Roy, Wilf Walrdon, Younis Abdi
 *
 */
class LowBattery implements Behavior{
	private Power battery = BrickFinder.getDefault().getPower();
	private boolean notification;
	public boolean takeControl() {
		return (battery.getVoltage() < 5);
	}
	
	public void action() {
		notification = false;
		LCD.drawString("LOW BATTERY!!!", 2, 2);
	}
	
	public void suppress() {
		notification = true;
	}
}