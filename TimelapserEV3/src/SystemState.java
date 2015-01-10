import lejos.hardware.BrickFinder;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.robotics.RegulatedMotor;


public class SystemState {

	
	
	EV3LargeRegulatedMotor horizontalMotor = new EV3LargeRegulatedMotor(BrickFinder.getDefault().getPort("D"));
	EV3LargeRegulatedMotor verticalMotor = new EV3LargeRegulatedMotor(BrickFinder.getDefault().getPort("C"));
	
	public int horizontalRotation = 0;
	public int verticalRotaion = 0;
	
}
