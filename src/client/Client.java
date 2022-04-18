package client;

import java.io.*;
import java.lang.NullPointerException;
import java.net.Socket;

public class Client {

	// the stream
	static ObjectOutputStream objectOutputStream;
	static ObjectInputStream objectInputStream;
	static ClientUI ui;

	public static void main(String[] args) {
		// create and invoke the Client UI
		ui = new ClientUI();
		ui.start();

		try {
			// get the args
			String serverAddr = args[0];
			int serverPortNum = Integer.parseInt(args[1]);

			// initialize the client socket which connects to the server
			Socket clientSocket = establishSocket(serverAddr, serverPortNum);

			// initialize the stream

			OutputStream outputStream = clientSocket.getOutputStream();
			InputStream inputStream = clientSocket.getInputStream();
			// Client output first
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectInputStream = new ObjectInputStream(inputStream);

		} catch (IOException e){
			setUITer("Establish the socket failed: Can not connect to the server. Please check the port address, port number and network.");
		} catch (NullPointerException e) {
			setUITer("Establish the stream failed: Can not connect to the server. Please check the port address, port number and network.");
		} catch (NumberFormatException e){
			setUITer("Establish the stream failed: invalid port number. Please check the port address and port number.");
		} catch (IllegalArgumentException e){
			setUITer("Establish the stream failed: invalid port number. Please check the port address and port number.");
		} catch (ArrayIndexOutOfBoundsException e){
			setUITer("Establish the connection failed: Please enter the port address and port number.");
		}
	}


	/**
	 *  Initialize the Client socket to connect the Server
	 *  @param serverAddr the address of the server socket
	 *  @param serverPortNum the port number of server socket
	 *  @return the client socket
	 */
	private static Socket establishSocket(String serverAddr, int serverPortNum) throws IOException{

		// establish the connection
		Socket clientSocket = new Socket(serverAddr, serverPortNum);

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

	/**
	 * Append string to the UI window's request area
	 * @param appendString
	 */
	public static void setUITer(String appendString){
		ui.getOutputTerminal().setText(appendString);
	}
}
