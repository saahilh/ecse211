package lab4localization;

import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class Lab4 {

	/* 
	 * Static Resources:
	 * Left motor connected to output A
	 * Right motor connected to output D
	 * Ultrasonic sensor port connected to input S1
	 * Color sensor port connected to input S2
	 */
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final Port usPort = LocalEV3.get().getPort("S1");		
	private static final Port colorPort = LocalEV3.get().getPort("S2");		

	/* 
	 * TODO for lab:
	 * PART 1	-	USLocalizer.java
	 * 	TASK
	 * 		1. localize and determine position
	 * 		2. turn robot towards its "0�" heading relative to (0, 0)
	 * 			NOTE: robot should not have moved other than rotating in this first part.
	 * 				  the robot must stop after turning to 0� so the TA can measure
	 * 		 	  	  the error of its angle. use "Button.waitForAnyPress();" for this
	 *	GRADING
	 *	[10 / 30] points are given for orienting the robot on its 0� axis within an error 
	 *			  tolerance of �10�. A penalty of -2 points per �5� is imposed after the 
	 *		      initial �10�.
	 *
	 * PART 2	-	LightLocalizer.java
	 * 		3. turn robot back to (0, 0) and move to it
	 * 		4. turn to your robot's 0� heading again
	 * GRADING 
	 * 	[10 / 30] points are given for reaching point (0, 0) within an error tolerance
	 * 			  of 1cm using Euclidean distance. A penalty of -1 point per cm is
	 * 			  imposed after this initial 1cm
	 * 	[10 / 30] points are given for orienting the robot along its 0� axis at point 
	 * 			  (0, 0) within an error tolerance of �10�. A penalty of -2 points per 
	 * 			  �5� is imposed after the initial �10�. 
	 */
	
	public static void main(String[] args) {
		
		/* 
		 * Setup ultrasonic sensor
		 * 1. Create a port object attached to a physical port (done above)
		 * 2. Create a sensor instance and attach to port
		 * 3. Create a sample provider instance for the above and initialize operating mode
		 * 4. Create a buffer for the sensor data
		 */
		@SuppressWarnings("resource")							    	// Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned
		
		/* 
		 * Setup color sensor
		 * 1. Create a port object attached to a physical port (done above)
		 * 2. Create a sensor instance and attach to port
		 * 3. Create a sample provider instance for the above and initialize operating mode
		 * 4. Create a buffer for the sensor data
		 */
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");			// colorValue provides samples from this instance
		float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
				
		// setup the odometer and display
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		LCDInfo lcd = new LCDInfo(odo);
		
		//setup navigation that uses the instantiated odometer
		Navigation nav = new Navigation(odo);
		
		while (Button.waitForAnyPress() != Button.ID_RIGHT);
		
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, nav, usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE);
		usl.doLocalization();
		
		while (Button.waitForAnyPress() != Button.ID_RIGHT);
		
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, nav, colorValue, colorData);
		lsl.doLocalization();	
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
		
	}

}
