import lejos.hardware.Button;
import lejos.utility.DebugMessages;

public class TimeLapser {
	
	

	public static void main(String[] args) {

		SystemState state = new SystemState();
		DebugMessages debug = new DebugMessages();

		BluetoothCommunicator bluetooth = new BluetoothCommunicator(state, debug);
		Movement movement = new Movement(state, debug);
		
		bluetooth.start();
		movement.start();
		
		
		while(!Button.ESCAPE.isDown()) {}
		System.exit(0);
		

	}
}
