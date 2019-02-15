/**
 * 
 */
package robopipe;

import java.awt.GraphicsEnvironment;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class App {

	/// TODO: Always remember to set to false before building
	static boolean isEclipse = true;

	String[] robotValues;
	List<NetworkTableEntry> tableEntrys = new ArrayList<NetworkTableEntry>();
	String output;
	double encoder, navX;
	NetworkTableEntry encoderEntry, encoderEntryR, encoderEntryL, navXEntry;
	ServerSocket serverSocket;
	static int PORT_NUMBER = 4388;
	Socket clientSocket;
	OutputStream os;
	InputStream in;
	static boolean EXIT = false;

	public static void main(String[] args) {
		// Thanks to Frezze98 bolalo on StackOverflow for the code to create a batch file
    	// to start the program in a command window
    	Console console = System.console();
    	if(console == null && !GraphicsEnvironment.isHeadless()) {
    		String filename = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    		try {
    			File batch = new File("Launcher.bat");
    			if(!batch.exists()){
    				batch.createNewFile();
    				PrintWriter writer = new PrintWriter(batch);
    				writer.println("@echo off");
    				writer.println("java -jar "+ filename + " " + args[0]);
    				writer.println("exit");
    				writer.flush();
    			}
    			Runtime.getRuntime().exec("cmd /c start \"\" "+batch.getPath());
    		} catch(IOException e) {
    			e.printStackTrace();
    		}
    	} else {
    		PORT_NUMBER = Integer.parseInt(args[0]);
    		System.out.println("Robopipe Start");
    		while (!EXIT) {
    			new App().run();
    		}
    		EXIT = true;
    	}
	}

	public void run() {
		try {
			NetworkTableInstance inst = NetworkTableInstance.getDefault();
			NetworkTable table = inst.getTable("/SmartDashboard");
			System.out.println("Connecting to Unity local server...");
			//serverSocket = new ServerSocket(portNumber);
			clientSocket = new Socket("127.0.0.1", PORT_NUMBER);
			os = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			System.out.println("Connected");
			byte[] byteRequest = new byte[100];
			in.read(byteRequest);
			String request = new String(byteRequest, StandardCharsets.UTF_8);
			robotValues = request.split(",");
			System.out.println("Request Recieved: " + request + robotValues.length);
			for (String value : robotValues) {
				tableEntrys.add(table.getEntry(value.trim()));
			}
			inst.startClientTeam(4388);
			inst.startDSClient();

			while (!EXIT) {
				toUnity();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void toUnity() throws IOException {
		output = "";
		for (NetworkTableEntry entry : tableEntrys) {
			output += round(entry.getDouble(0), 5);
			output += "|";
			System.out.println("'" + entry.getName() + "'");
		}
		
		output = output.substring(0, output.length() - 1);
		byte[] byteOutput = output.getBytes(StandardCharsets.UTF_8);
		System.out.println("Sending '" + output + "' of length: " + byteOutput.length + " to Unity...");
		os.write(byteOutput);
		
		System.out.println("Waiting for Response...");
		byte[] byteInput = new byte[4];
		in.read(byteInput);
		String stringInput = new String(byteInput, StandardCharsets.UTF_8);
		stringInput = stringInput.substring(0, 4);
		if (stringInput.equals("EXIT")) {
			EXIT = true;
		}
		System.out.println("Response: " + stringInput);
	}

	public static BigDecimal round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd;
	}
}
