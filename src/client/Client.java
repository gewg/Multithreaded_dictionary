package client;

import java.io.*;
import java.lang.NullPointerException;
import java.net.Socket;

public class Client {

	// the stream
	static ObjectOutputStream objectOutputStream;
	static ObjectInputStream objectInputStream;

	public static void main(String[] args) {
		// get the args
		String serverAddr = args[0];
		int serverPortNum = Integer.parseInt(args[1]);

		// initialize the client socket which connects to the server
		Socket clientSocket = establishSocket(serverAddr, serverPortNum);

		// initialize the stream
		try {
			OutputStream outputStream = clientSocket.getOutputStream();
			InputStream inputStream = clientSocket.getInputStream();
			// Client output first
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectInputStream = new ObjectInputStream(inputStream);
		}catch (IOException e){
			System.out.print("Establish the stream failed: IOException. Please contact the developer.\n");
		} catch (NullPointerException e) {
			System.out.print("Establish the stream failed: NullPointerException. Please contact the developer.\n");
		}

		// create and invoke the Client UI
		ClientUI ui = new ClientUI();
		ui.start();
	}


	/**
	 *  Initialize the Client socket to connect the Server
	 *  @param serverAddr the address of the server socket
	 *  @param serverPortNum the port number of server socket
	 *  @return the client socket
	 */
	private static Socket establishSocket(String serverAddr, int serverPortNum) {
		// the client socket
		Socket clientSocket = null;
		
		// establish the connection
		try {
			clientSocket = new Socket(serverAddr, serverPortNum);
		} catch (IOException e) {
			System.out.print("Establish Client Socket failed: IOException. Please contact the developer.\n");
		}
		return clientSocket;
	}


	/**
	 * Accept request from the UI
	 * @param request the request from the UI
	 * @return the result after operation. There is 3 parts of result: 1. The content output. 2. The terminal output. 3. The success mark
	 */
	public static String[] handleRequest(String[] request){
		// the result
		String[] result = new String[3];

		// get the request code
		String operationCode = request[0];

		// the request handler
		ClientHandleRequest requestHandler;

		if (operationCode.equals("ADD")){
			requestHandler = new ClientHandleADD(objectOutputStream, objectInputStream);
			result = requestHandler.operation(request);
		}
		else if (operationCode.equals("QUERY")){
			requestHandler = new ClientHandleQUERY(objectOutputStream, objectInputStream);
			result = requestHandler.operation(request);
		}
		else if (operationCode.equals("DELETE")){
			requestHandler = new ClientHandleDELETE(objectOutputStream, objectInputStream);
			result = requestHandler.operation(request);
		}
		else if (operationCode.equals("UPDATE")){
			requestHandler = new ClientHandleUPDATE(objectOutputStream, objectInputStream);
			result = requestHandler.operation(request);
		}
		else if (operationCode.equals("APPEND")){
			requestHandler = new ClientHandleAPPEND(objectOutputStream, objectInputStream);
			result = requestHandler.operation(request);
		}

		return result;
	}
}
