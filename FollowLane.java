package LaneBot;

import java.io.BufferedReader;
import java.io.IOException;
import lejos.robotics.subsumption.Behavior;
/**
 * The FollowLane behaviour allows our bot to follow a lane
 * it receives a steering angle from the app and will control the bot
 * so it drives and steers at the given angle.
 * @author Mustaeen Siddiqui, Joel Roy, Wilf Walrdon, Younis Abdi
 *
 */
public class FollowLane implements Behavior{
	    private int speed = 70;
	    private BufferedReader btReader;
	    private LaneBotPilot pilot;
	    private final int steeringConstant = 45;
	    private float  savedAngle =  0;

	    public FollowLane(LaneBotPilot pilot, BufferedReader btReader) {
	    	this.pilot = pilot;
	    	this.btReader = btReader;
	    	this.pilot.setDriveMotorSpeed(speed);
	    	//openConnection();
	    }
	    
	    public boolean takeControl() {
	    	return true;
	    }
	    
	    public void action() {
	    	try {
	    		String input;
				String tempLine  = "0";
				while ((input = btReader.readLine()) != null) {
				       tempLine = input;
				       float angle = Float.parseFloat(tempLine);
				       if (angle > 0.95) {
				    	   angle = savedAngle;
				       }else {
				    	   savedAngle = angle;
				       }
				       float steeringAngle = angle * steeringConstant;
				       pilot.steerMotor(steeringAngle);
				       pilot.drive();
				   }
				btReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void suppress() {
	    	
	    }
	    
}