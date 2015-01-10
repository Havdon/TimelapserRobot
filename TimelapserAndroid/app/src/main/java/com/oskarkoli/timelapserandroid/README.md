####  There are two activities (views) in this application:


1. **TimelapseActivity**, where the user sets the key movement points of the robot.
2. **ExecutionActivity**, where the user selects the movement parameters (image frequency, and final video length), where the user starts the execution of the movement and where debug info is printed when the movement is being executed.

#### Other notable classes:

1. **MainLoop**, thread which ensures the Wifi connection to the GoPro is up and that the Bluetooth connection to the EV3 robot is valid.
2. **RobotMessage**, the class shared by the Android application and the EV3 LejOS code, that is sent over Bluetooth from app to robot.
