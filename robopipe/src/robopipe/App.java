/**
 * For more information on using java sockets:
 * https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */
package robopipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class App {
	
	NetworkTableEntry encoderEntry, navXEntry;
	double encoder = 46.83583538, navX = 3.37583994;
	ServerSocket serverSocket;
	int portNumber = 4388;
    Socket clientSocket;
    OutputStream os;
    InputStream in;
    String output;
	
	public static void main( String[] args )
	{
		System.out.println("Robopipe Start");
        new App().run();
    }

    public void run() {
    	
    	try {
    		NetworkTableInstance inst = NetworkTableInstance.getDefault();
            NetworkTable table = inst.getTable("/SmartDashboard");
            encoderEntry = table.getEntry("averageEncoder"); //Replace x with name of avg encoder value
            navXEntry = table.getEntry("navX"); //Replace x with name of NavX rotation
            inst.startClientTeam(4388);
            inst.startDSClient();
        	
    		System.out.println("Waiting for Unity client connection...");
            serverSocket = new ServerSocket(portNumber);
    	    clientSocket = serverSocket.accept();
    	    os = clientSocket.getOutputStream();
    	    in = clientSocket.getInputStream();
    	    
    	    System.out.println("Connected");
    	    
    	    toUnity();
    	    System.out.println("Waiting for Response...");
            while (true) {
            	if (in.read() != -1) {
            		System.out.println("Response Received");
            		toUnity();
            		System.out.println("Waiting for Response...");
            	}
            }
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    private void toUnity() throws IOException {
    	try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException");
            return;
        }
        encoder = encoderEntry.getDouble(0.0);
        navX = navXEntry.getDouble(0.0);
        System.out.println("Encoder Value: " + encoder + " Rotation: " + navX);
    	
    	output = round(encoder, 3) + "|" + round(navX, 3);
    	byte[] byteOutput = output.getBytes(StandardCharsets.UTF_8);
    	System.out.println("Sending '" + output + "' of length " + byteOutput.length + " to Unity...");
    	os.write(byteOutput);
    }
    
    public static BigDecimal round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       
        return bd;
    }
}
