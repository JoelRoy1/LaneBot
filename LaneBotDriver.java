package LaneBot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;


/**
 * The Driver class for LaneBot.
 * This is the Program that will be run on the EV3 Brick.
 * the program first displays a start screen, 
 * connects to the android app via bluetooth, 
 * and sets up all sensors and motors and creates the arbitrator. 
 * @author Mustaeen Siddiqui, Joel Roy, Wilf Walrdon, Younis Abdi
 *
 */
public class LaneBotDriver {
	
	
	//Variables for Bluetooth connection.
	public static BTConnector connector;
    public static NXTConnection connection;
    public static BufferedReader btReader;
    
    
	public static void main(String[] args){
		//Shows start screen
		String startScreen = "Mustaeen Siddiqui \n Wilf Waldron \n Joel Roy \n Younis Abdi \n press button to continue"; 
		LCD.drawString(startScreen, 1, 1);
		Button.waitForAnyPress();
		//setup motors and sensors.
		BaseRegulatedMotor mDrive = new EV3LargeRegulatedMotor(MotorPort.A);
		BaseRegulatedMotor mSteer = new EV3MediumRegulatedMotor(MotorPort.D);
		EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S4);
		NXTSoundSensor soundSensor = new NXTSoundSensor(SensorPort.S1);
		EV3ColorSensor colourSensor = new EV3ColorSensor(SensorPort.S2);
		Brick EV3 = BrickFinder.getDefault();
		//start bluetooth connection.
		openConnection();
		//create move pilot
		LaneBotPilot pilot = new LaneBotPilot(mDrive, mSteer);
		
		//create behaviours.
		Obstructed obstructed = new Obstructed(pilot, usSensor);
		DetectSound detectSound =  new DetectSound(pilot, soundSensor);
		FollowLane followLane = new FollowLane(pilot, btReader);
		LowBattery lowBatt  = new LowBattery();
		ChangeSpeed changeSpeed = new ChangeSpeed(pilot, colourSensor);
		EmergencyStop eStop = new EmergencyStop(pilot, EV3);
		//create and start arbitrator.
		Arbitrator ab = new Arbitrator(new Behavior[] {followLane, obstructed, lowBatt, detectSound, changeSpeed, eStop});
		ab.go();
	}
	
	
	/**
	 * The openConnection method starts a Bluetooth Connection with
	 * the android app and creates a reader from the input stream. 
	 */
	public static void openConnection() {
        connector = new BTConnector();
        LCD.drawString("Waiting for Connection", 3, 1);
        connection = connector.waitForConnection(0, NXTConnection.RAW);
        LCD.clear();
        LCD.drawString("Connected", 3, 5);  
        InputStream is = connection.openInputStream();
    	btReader = new BufferedReader(new InputStreamReader(is), 1);
    }
}