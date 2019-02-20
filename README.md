# RoboPipe
RoboPipe is a java-based script that allows the user to pull SmartDashboard values into other programs. The API uses [.NET sockets](https://docs.microsoft.com/en-us/dotnet/standard/get-started) and connects to the SmartDashboard using the [Network Tables Libraries](https://wpilib.screenstepslive.com/s/currentCS/m/75361/l/851714-creating-a-client-side-program). RoboPipe will take a startup request and will send the requester a stream of the requested values.

For information on installing RoboPipe, see Installation. For information on creating a program to interact with RoboPipe, see Setup.

## Installation
To install Robopipe, start by downloading the latest jar from [Releases](https://github.com/Team4388/WPILib-Network-Tables-RoboPipe/releases) page. Put it into its own folder so that the batch file it creates won't clutter up your files. Then, finish by either running RoboPipe.jar (opens socket on default port), or use the command line to set a specific port.
``` CMD
cd (Directory of RoboPipe)
java -jar RoboPipe.jar (Port Number)
```

## Setup
Connecting to RoboPipe is as follows:
- When RoboPipe starts, it attempts to connect to a server socket on the given port (default 4388).
- RoboPipe communicates using [UTF-8 encoded](https://www.fileformat.info/info/unicode/utf8.htm) strings, so to communicate you will need to convert your messages into a string and then encode it into a UTF-8 byte array.
- Once connected, send RoboPipe a comma separated string with the exact notation of the values you want to grab from SmartDashboard (**a,b,c**).

Then RoboPipe will enter into a loop that does the following:
- It will send a comma separated response in the form of (**a,b,c**).
- Then send back the 4 byte string "CONT" to get another value

When you want to close RoboPipe you can do so automatically by sending it "EXIT"
