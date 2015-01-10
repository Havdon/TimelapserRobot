import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;



import com.oskarkoli.timelapserandroid.RobotMessage;

import lejos.hardware.Bluetooth;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.NXTCommConnector;
import lejos.remote.nxt.NXTConnection;
import lejos.utility.DebugMessages;



/**
 * Thread that handles Bluetooth communication with Android device.
 */
public class BluetoothCommunicator extends Thread {
	
	
	
	public static DataOutputStream dataOutputStream;
	public static ObjectInputStream inputStream;
	public static NXTConnection bluetoothConnection;
	
	private NXTCommConnector btConnector;
	
	private SystemState state;
	private DebugMessages debug;
	private BTConnection connection;
	private boolean connected = false;
	
	
	public BluetoothCommunicator(SystemState state, DebugMessages debug) {
		this.state = state;
		this.debug = debug;
		System.out.close();
	}
	
	
	/**
	 * Opens the connection to the Android device.
	 * @return True if connection successful.
	 */
	private boolean connect() {
		debug.echo("Connecting...");

		if(btConnector == null) {
			btConnector = Bluetooth.getNXTCommConnector();
		}
		bluetoothConnection = btConnector.waitForConnection(100, NXTConnection.RAW);

		if (bluetoothConnection == null) {
			debug.echo("timed out.");
			return false;
		}
		
		
		dataOutputStream = bluetoothConnection.openDataOutputStream();
		try {
			inputStream = new ObjectInputStream(bluetoothConnection.openDataInputStream());
		} catch (IOException e1) {
			return false;
		}
		debug.echo("got  in!");
		if (dataOutputStream == null) {
			return false;
		}
		return true;
	}
	
	
	
	
	public void run() {
		
		if(!connect()) {
			return;
		}
		
		debug.echo("Connected!");
		while(true) {
			try {
				RobotMessage robotMessage = (RobotMessage) inputStream.readObject();
				this.state.horizontalMotor.rotateTo(robotMessage.getHorizontalRotation(), true);
				this.state.horizontalMotor.setSpeed(robotMessage.getHorizontalSpeed());
				this.state.horizontalMotor.setAcceleration(robotMessage.getHorizontalAcceleration());
				
				this.state.verticalMotor.rotateTo(robotMessage.getVerticalRotation(), true);
				this.state.verticalMotor.setSpeed(robotMessage.getVerticalSpeed());
				this.state.verticalMotor.setAcceleration(robotMessage.getVerticalAcceleration());
				debug.echo(robotMessage.toString());
			} catch (EOFException e) {
				// There is an error in LejOS which crashes the application if we try to search for connections again after loosing one.
				// There is nothing I can do, except start working on the LejOS source code. Which is a bit out of scope for this project.
				debug.echo("Bluetooth connection error. Please restart.");
				return;
			} catch (IOException | ClassNotFoundException | NullPointerException e) {
				debug.echo(e.getClass().getSimpleName());
			}
		}
		
		
	}

}
