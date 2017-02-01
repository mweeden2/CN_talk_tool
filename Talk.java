/**
 * Created by Matt Weeden (9/23/15)
 * 
 * The Talk class is a bidirectional talk program for two-party conversations.
 *
 *  Optional command-line arguments:
 *
 *    -h [hostname | IPadress] [-p portnumber]
 *          Connect as a client to [hostname | IPaddress] on port portnumber.
 *
 *    -s [-p portnumber]
 *          Listen as a server on port portnumber.
 *
 *    -a [hostname | IPaddress] [-p portnumber]
 *          Enter "auto" mode. Start as a client then listen as a server if connection
 *          is not made.
 *
 *    -help
 *          Prints author's name (Matt Weeden) and usage instructions.
 */

import java.io.*;
import java.net.*;
public class Talk {
	
	// thread for handling incoming messages
	public class Listen implements Runnable {
		BufferedReader in;
		String msg;
		
		public Listen(BufferedReader x) {
			in = x;
		}
		
		public void run() {
			try{
				try{
				    while(true){
					if (in.ready()) {
					    msg=in.readLine();
					    System.out.println("[remote] " +msg);}
				    }
				}
				catch (IOException e) {
				    System.out.println("Read failed");
				    System.exit(-1);
				}
			}
			catch(Exception e) {}
		}
	}
	
    public static void main(String [] args) {	
	
		int mode = 0; 			// 0 => auto mode; 1 => client mode; 2 => server mode
		String host = "localhost"; 		// hostname or IPaddress
		int portNumber = 12987;	// default port number
		
		for (int i = 0; i <args.length; i++) {
		    
			// client mode
			if (args[i].equals("-h")) {
		    	mode = 1;
		    	if (args.length >4) {
		    		System.out.println("You have passed too many arguments");
		    		System.exit(1);
		    	}
		    	if ((args.length > 1) && (!(args[i+1].equals("-p")))) {
           			host = args[i+1];        			
		    	}
		    }
		    
			// server mode
			else if (args[i].equals("-s")) {
				mode = 2;
				if (args.length > 3) {
					System.out.println("You have passed too many arguments.");
					System.exit(1);
				}
				if (args.length > 1 && (!(args[i+1].equals("-p")))) {
					System.out.println("Use the \"-p\" argument to pass a port number.");
					System.exit(1);
				}
			}
			
			// auto mode
			else if (args[i].equals("-a")) {
				mode = 0;
				if (args.length > 4) {
					System.out.println("You have passed too many arguments.");
					System.exit(1);
				}
				if ((args.length > 1) && (!(args[i+1].equals("-p")))) {
		    		host = args[i+1];
		    	}
			}
			
			else if (args[i].equals("-p")) {
				if (args.length < 3) {
					System.out.println("You must specify the mode before defining the"
					+ " portnumber using \"-h\" for client mode, \"-s\" for server mode,"
					+ "or \"-a\" for auto mode.");
					System.exit(1);
				}
				try {
    				portNumber = Integer.parseInt(args[i+1]);
 				}
 					catch (NumberFormatException nfe) {    
           			System.out.println("The portnumber argument must be an integer.");
		       		System.exit(1);
		    	}
		    	
			}
			
			else if (args[i].equals("-help")) {
				if (args.length > 1) {
					System.out.println("You have passed too many arguments.");
					System.exit(1);
				}
				System.out.println("\nTalk.class by Matt Weeden\nThe Talk class is a "
						+ "bidirectional talk program for two-party conversations.\n\n"
						+ "Optional command-line arguments:\n\n\t-h [hostname | IPadress] "
						+ "[-p portnumber]\n\t\tConnect as a client to [hostname | "
						+ "IPaddress] on port portnumber.\n\n\t-s [-p portnumber]\n\t\t"
						+ "Listen as a server on port portnumber.\n\n\t-a [hostname | "
						+ "IPaddress] [-p portnumber]\n\t\tEnter \"auto\" mode. Start as a "
						+ "client then listen as a server if connection is not made.\n\n\t"
						+ "-help\n\t\t Prints author's name (Matt Weeden and these usage "
						+ "instructions.\n");
				System.exit(0);
			}
		}
		
				
		/** 
		 * Attempt to establish a connection as a client
		 */
		 
		if ((mode == 0) || (mode == 1)) {
	    	
			System.out.println("Starting TalkClient");                            
	        
	        BufferedReader systemIn=null;
	        BufferedReader serverIn=null;
	        PrintWriter out=null;
	        
	        String message=null;
	        Socket socket=null;
	        
	        try{
	            socket = new Socket(host, portNumber);
	            
	            systemIn = new BufferedReader(new InputStreamReader(System.in));
	            serverIn = new 
	            		BufferedReader(new InputStreamReader(socket.getInputStream()));
	            out = new PrintWriter(socket.getOutputStream(), true);
	  
	    		Thread t1 = new Thread(new Talk().new Listen(serverIn));
	            
	            // start the listening thread
	            t1.start();
	            // send out messages
	            while(true) {
	                message = systemIn.readLine();
	                if (message.equals("STATUS")) {
				    	System.out.println("hostname: " + host +
				    			"\nportnumber: " +portNumber);
				    }
	                else out.println(message); 
	            }
	        } catch (UnknownHostException e) {
	            System.out.println("Uknown Host:"+host);
	            if (mode == 1) {
	            	System.out.println("\nClient unable to communicate with server.");
	            	System.exit(1);
	            }
	        } catch (IOException e) {
	            System.out.println("No I/O");
	            if (mode == 1) {
	            	System.out.println("\nClient unable to communicate with server.");
	            	System.exit(1);
	            }
	        }
		}
		
		/** 
		 * Attempt to start listening on port as a server
		 */
		 
		if ((mode == 0) || (mode == 2)) {
			        
			System.out.println("Starting TalkServer");
			
			BufferedReader systemIn=null;
			BufferedReader clientIn=null;
			PrintWriter out=null;
			
			String message=null;
			Socket client=null;
			ServerSocket server=null;
			
			try{
			    server= new ServerSocket(portNumber);
			    System.out.println("Server listening on port "+portNumber);}
			catch (IOException e){
			    System.out.println("Server unable to listen on specified port (" +
			    		portNumber +").");
			    System.exit(1);
			}
			try{
			    client=server.accept();
			    System.out.println("Server accepted connection from "+
			    		client.getInetAddress());}
			catch (IOException e){
			    System.out.println("Accept failed on port "+portNumber);
			    System.exit(1);
			}
			try{
			    clientIn = new BufferedReader(new 
			    		InputStreamReader(client.getInputStream()));
			    systemIn = new BufferedReader(new InputStreamReader(System.in));
			    out = new PrintWriter(client.getOutputStream(), true);
			}
			catch (IOException e){
			    System.out.println("Couldn't get an inputStream from the client");
			    System.exit(1);
			}
			
			Thread t1 = new Thread(new Talk().new Listen(clientIn));
			t1.start();
			
			try {
				// send out messages
				while(true) {
	                message = systemIn.readLine();
	                if (message.equals("STATUS")) {
				    	System.out.println("hostname: " + host +
				    			"\nportnumber: " +portNumber);
				    }
	                else out.println(message); 
	            }
			} catch (IOException e) {
            System.out.println("No I/O");
			}
		}
    }
}
