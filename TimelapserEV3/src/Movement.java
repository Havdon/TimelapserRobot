
import lejos.utility.DebugMessages;

public class Movement extends Thread {

	private SystemState state;
	private DebugMessages debug;
	public Movement(SystemState state, DebugMessages debug) {
		this.state = state;
		this.debug = debug;
	}
	
	public void run() {
		
		while(true) {
			//this.state.horizontalMotor.rotate(1);
		}
		
	}

}
