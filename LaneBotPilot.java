package LaneBot;

import lejos.utility.Delay;
import lejos.hardware.motor.BaseRegulatedMotor;
import java.lang.Math;


/**
 * The LaneBotPilot class is a custom move pilot
 * for our EV3 bot.
 * It contains methods related to the movement of the bot.
 * @author Mustaeen Siddiqui, Joel Roy, Wilf Waldron, Younis Abdi
 *
 */
public class LaneBotPilot {
	private BaseRegulatedMotor driveMotor;
	private BaseRegulatedMotor steerMotor;
	
	private float offset;
	private float steerAngle;
	//offset is for zeroing 
	
	public LaneBotPilot(BaseRegulatedMotor driveMotor, BaseRegulatedMotor steerMotor) {
		this.driveMotor = driveMotor;
		this.steerMotor = steerMotor;
	}
	
	public void zeroSteering(){
		this.offset = steerMotor.getPosition();
	}
	//steer
	public void steerMotor(float angle) {
		int motorAngle = Math.round(angle - offset);
		steerMotor.rotateTo(motorAngle);
		this.steerAngle = angle;
	}
	
	public void setDriveMotorSpeed(int speed) {
		driveMotor.setSpeed(speed);
	}
	
	public void drive() {
		driveMotor.forward();
	}
	
	public void stopMotor(){
		driveMotor.stop();
		steerMotor.stop();
	}
	
	public void reverse(){
		driveMotor.backward();
	}
	
	public float getSteeringAngle(){
		return this.steerAngle;
	}
	
	public void pullOver() {
		drive();
		steerMotor(70);
		setDriveMotorSpeed(100);
		Delay.msDelay(2000);
		steerMotor(-70);
		Delay.msDelay(2000);
		stopMotor();
	}
	
	public void pullIn() {
		reverse();
		steerMotor(-70);
		setDriveMotorSpeed(100);
		Delay.msDelay(2000);
		steerMotor(70);
		Delay.msDelay(2000);
		stopMotor();
		drive();
	}
}