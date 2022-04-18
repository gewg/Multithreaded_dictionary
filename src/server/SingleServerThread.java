package server;

import java.io.*;
import java.net.Socket;
import com.alibaba.fastjson.JSONObject;

/**
 * This class is a 'Thread-per-Connection' type thread to serve the server.
 * All manipulation in one connection between client and server will be handled by this class.
 */
public class SingleServerThread extends Thread{

	// the cut-off line in UI's terminal
	final static String CUT_OFF = "\n------------------------------------------------------------\n";

	// the client socket to serve
	Socket clientSocket = null;
	
	// the IO
	ObjectInputStream objectInputStream = null;
	ObjectOutputStream objectOutputStream = null;

	// the json request from the client
	JSONObject jsonReq = null;

	/**
	 * The constructor
	 * @param clientSocket the client socket
	 */
	public SingleServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;

		// initialize the io
		try{
			InputStream inputStream = clientSocket.getInputStream();
			OutputStream outputStream = clientSocket.getOutputStream();
			// Server input firstly
			objectInputStream = new ObjectInputStream(inputStream);
			objectOutputStream = new ObjectOutputStream(outputStream);
		} catch (IOException e) {
			Server.setUIReq("Establish the stream communication between Client and Server failed: IOException");
			Server.setUIReq(CUT_OFF);
			Server.systemSucc = false;
		}
	}


	@Override
	public void run() {

		// keep the thread alive until the connection breaks
		while(true) {

			// read the request from client
			try{
				jsonReq = (JSONObject) objectInputStream.readObject();
			} catch (IOException e) {
				// notice the server
				Server.setClientNum(Server.getClientNum() - 1); // update the client total number
				Server.setUIReq(String.format("One client disconnect. Total number of clients: %d", Server.getClientNum()));
				Server.setUIReq(CUT_OFF);
				break; // terminate the thread's live while the objectInputStream is ended which means the client has ended
			} catch (ClassNotFoundException e) {
				Server.setUIReq("JSONObject: ClassNotFoundException.");
				Server.setUIReq(CUT_OFF);
				Server.systemSucc = false;
			}

			// handle the request
			JSONObject jsonRes = Server.handleRequest(jsonReq);

			// response to client
			// send the response to client
			try{
				objectOutputStream.writeObject(jsonRes);
			} catch (IOException e) {
				Server.setUIReq("Send response to client failed: IOException");
				Server.setUIReq(CUT_OFF);
				Server.systemSucc = false;
			}
		}
	}
}
