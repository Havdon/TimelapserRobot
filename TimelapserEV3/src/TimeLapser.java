import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.DebugMessages;

public class TimeLapser {
	

	public static void main(String[] args) {

		DebugMessages debug = new DebugMessages();

		EV3LargeRegulatedMotor horizontalMotor = new EV3LargeRegulatedMotor(BrickFinder.getDefault().getPort("D"));
		EV3LargeRegulatedMotor verticalMotor = new EV3LargeRegulatedMotor(BrickFinder.getDefault().getPort("C"));
		
		BluetoothCommunicator bluetooth = new BluetoothCommunicator(debug, horizontalMotor, verticalMotor);
		bluetooth.start();
		
		
		while(!Button.ESCAPE.isDown()) {}
		System.exit(0);
		

	}
}
