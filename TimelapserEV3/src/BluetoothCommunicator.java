import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.oskarkoli.timelapserandroid.RobotMessage;

import lejos.hardware.Bluetooth;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.remote.nxt.NXTCommConnector;
import lejos.remote.nxt.NXTConnection;
import lejos.utility.DebugMessages;



/**
 * Thread that connects to the Android application, and executes all commands send to it by the application.
 */
public class BluetoothCommunicator extends Thread {
	
	
	
	public static DataOutputStream dataOutputStream;
	public static ObjectInputStream inputStream;
	public static NXTConnection bluetoothConnection;
	
	private EV3LargeRegulatedMotor horizontalMotor;
	private EV3LargeRegulatedMotor verticalMotor;
	
	private NXTCommConnector btConnector;

	private DebugMessages debug;
	
	public BluetoothCommunicator(DebugMessages debug,  EV3LargeRegulatedMotor horizontalMotor, EV3LargeRegulatedMotor verticalMotor) {
		this.debug = debug;
		this.horizontalMotor = horizontalMotor;
		this.verticalMotor = verticalMotor;
		System.out.close(); // Kills LejOS debug info from showing on robot screen.
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
				this.horizontalMotor.rotateTo(robotMessage.getHorizontalRotation(), true);
				this.horizontalMotor.setSpeed(robotMessage.getHorizontalSpeed());
				this.horizontalMotor.setAcceleration(robotMessage.getHorizontalAcceleration());
				
				this.verticalMotor.rotateTo(robotMessage.getVerticalRotation(), true);
				this.verticalMotor.setSpeed(robotMessage.getVerticalSpeed());
				this.verticalMotor.setAcceleration(robotMessage.getVerticalAcceleration());
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
