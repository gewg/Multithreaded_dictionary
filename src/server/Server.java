package server;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

	// mark whether the system is regular
	static boolean systemSucc = true;

	// the cut-off line in UI's terminal
	final static String CUT_OFF = "\n------------------------------------------------------------\n";

	// the ui
	static ServerUI ui;

	// the port number
	static int portNum = 0;
	// the file path
	static String filePath;
	// the total number of client
	static int clientNum = 0;
	// the database which stores the dictionary's information
	// one server contains only one database
	static DataBase dataBase = new DataBase();
	
	public static void main(String[] args){
		// invoke the server UI
		ui = new ServerUI();
		ui.start();

		try {
			// set the arguments
			portNum = Integer.parseInt(args[0]);
			filePath = args[1];

			// create the server socket
			ServerSocket serverSocket = establishSocket(portNum);

			// load dictionary
			try {
				loadFile();
			} catch (FileNotFoundException e) {
				setUIReq("Load the dictionary failed: File can not be found. A new dictionary file will be generated.");
				setUIReq(CUT_OFF);
			}

			// listen the request from the clients
			while (true) {

				// capture the client socket from request
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					setUIReq("Connect with the incoming Client Socket failed: IOException");
					setUIReq(CUT_OFF);
					systemSucc = false;
				}

				// add the number of clients
				setClientNum(getClientNum() + 1);
				// output to the UI
				setUIReq(String.format("A new client '%s' connect the server.\nCurrent total number of clients:%d", clientSocket, getClientNum()));
				setUIReq(CUT_OFF);

				// establish a 'Thread-per-connection' type thread for the current client socket
				SingleServerThread singleServerThread = new SingleServerThread(clientSocket);
				// invoke the thread
				singleServerThread.start();
			}
		} catch (NumberFormatException e){
			setUIReq("Can not establish the server. Check the port number and restart the server.");
			setUIReq(CUT_OFF);
			systemSucc = false;
		} catch (IllegalArgumentException e){
			setUIReq("The port number is not valid. Notice that only 0-65536 can be port number. Check the port number and restart the server.");
			setUIReq(CUT_OFF);
			systemSucc = false;
		} catch (NullPointerException e) {
			setUIReq("Can not establish the server. Check the port number and restart the server.");
			setUIReq(CUT_OFF);
			systemSucc = false;
		} catch (UnsupportedEncodingException e) {
			setUIReq("Load the dictionary failed: Unsupported encoding format");
			setUIReq(CUT_OFF);
			systemSucc = false;
		}  catch (ArrayIndexOutOfBoundsException e){
			setUIReq("Can not establish the server. Please enter the port number, the path for dictionary file.");
			setUIReq(CUT_OFF);
			systemSucc = false;
		}
	}
	
	/**
	 * Initialize the server socket
	 * @param portNum the port number for the server
	 * @return the server socket
	 */
	private static ServerSocket establishSocket(int portNum) {
		
		// create the client socket
		ServerSocket serverSocket = null;
		
		// establish the connection
		try {
			serverSocket = new ServerSocket(portNum);
		} catch (UnknownHostException e) {
			setUIReq("Establish the Server Socket failed: Unknown Host. Check the port number");
			setUIReq(CUT_OFF);
			systemSucc = false;
		} catch (IOException e) {
			setUIReq("Establish the Server Socket failed: IOException. Check the port number");
			setUIReq(CUT_OFF);
			systemSucc = false;
		}

		return serverSocket;
	}


	/**
	 * Handle all kinds of request from the client
	 * @param jsonReq the json format request from the client
	 * @return response to client
	 */
	public static JSONObject handleRequest(JSONObject jsonReq){

		// the response to client, the default message is "not support"
		JSONObject resMessage = new JSONObject();

		// record the request
		setUIReq(String.format("A client send a %s request\n", jsonReq.getString("type")));
		setUIReq(String.format("The word be manipulated: %s", jsonReq.getString("word")));
		setUIReq(CUT_OFF);

		// check the type of request and send it to the corresponding handle
		// default response
		String[] result = new String[]{"FAIL", "", "Server error: current request is not supported by the Server. Please contact the developer."};
		// ADD
		if (jsonReq.getString("type").equals("ADD")){
			result = addOperation(jsonReq);
		}
		// QUERY
		else if (jsonReq.getString("type").equals("QUERY")){
			result = queryOperation(jsonReq);
		}
		// UPDATE
		else if (jsonReq.getString("type").equals("UPDATE")){
			result = updateOperation(jsonReq);
		}
		// DELETE
		else if (jsonReq.getString("type").equals("DELETE")){
			result = deleteOperation(jsonReq);
		}
		// APPEND
		else if (jsonReq.getString("type").equals("APPEND")){
			result = appendOperation(jsonReq);
		}

		resMessage.put("flag", result[0]);
		resMessage.put("content", result[1]);
		resMessage.put("terminal", result[2]);

		return resMessage;
	}


	/**
	 * Accept the add request from client and manipulate the database
	 * @param jsonReq the json format request from the client
	 * @return response to client
	 */
	public static String[] addOperation(JSONObject jsonReq) {

		// the response to client
		String[] resMessage = new String[3];

		// get the information from the request
		String word = (String) jsonReq.get("word");
		String[] meanings = (String[]) jsonReq.get("meanings");

		// check whether the word is duplicate
		if (dataBase.wordExistCheck(word)){
			resMessage[0] = "FAIL";
			resMessage[1] = "";
			resMessage[2] = "Add failed: The word is duplicate. Try to update it.";
			return resMessage;
		}

		// operate the addition
		dataBase.addToDic(word, meanings);
		resMessage[0] = "SUCC";
		resMessage[1] = dataBase.queryToDic(word);
		resMessage[2] = "Add Successfully.";

		return resMessage;
	}

	/**
	 * Accept the delete request from client and manipulate the database
	 * @param jsonReq the json format request from the client
	 * @return response to client
	 */
	private static String[] deleteOperation(JSONObject jsonReq){

		// the response to client
		String[] resMessage = new String[3];

		// get the information from the client
		String word = (String) jsonReq.get("word");

		// check whether the word is duplicate
		if (!dataBase.wordExistCheck(word)){
			resMessage[0] = "FAIL";
			resMessage[1] = "";
			resMessage[2] = "Delete failed: The word does not exist. Try to add it.";
			return resMessage;
		}

		// operate the delete
		dataBase.deleteToDic(word);
		resMessage[0] = "SUCC";
		resMessage[1] = "";
		resMessage[2] = "Delete Successfully.";

		return resMessage;
	}

	/**
	 * Accept the query request from client and manipulate the database
	 * @param jsonReq the json format request from the client
	 * @return response to client
	 */
	private static String[] queryOperation(JSONObject jsonReq){

		// the response to client
		String[] resMessage = new String[3];

		// get the information from the client
		String word = (String) jsonReq.get("word");
		// check whether the word exists
		if (!dataBase.wordExistCheck(word)){
			resMessage[0] = "FAIL";
			resMessage[1] = "";
			resMessage[2] = "Query failed: The word does not exist. Try to add it.";
			return resMessage;
		}

		// operate the query
		String meanings = dataBase.queryToDic(word);

		// return the result to client
		resMessage[0] = "SUCC";
		resMessage[1] = meanings;
		resMessage[2] = "Query successfully.";

		return resMessage;
	}

	/**
	 * Accept the update request from client and manipulate the database
	 * @param jsonReq the json format request from the client
	 * @return response to client
	 */
	private static String[] updateOperation(JSONObject jsonReq){

		// the response to client
		String[] resMessage = new String[3];

		// get the information from the request
		String word = (String) jsonReq.get("word");
		String[] meanings = (String[]) jsonReq.get("meanings");

		// check whether the word is duplicate
		if (!dataBase.wordExistCheck(word)){
			resMessage[0] = "FAIL";
			resMessage[1] = "";
			resMessage[2] = "Update failed: The word does not exist. Try to add it.";
			return resMessage;
		}

		// operate the addition
		dataBase.updateToDic(word, meanings);
		resMessage[0] = "SUCC";
		resMessage[1] = dataBase.queryToDic(word);
		resMessage[2] = "Update Successfully.";

		return resMessage;
	}


	/**
	 * Append the meaning to corresponding word
	 * @param jsonReq the json format request from the client
	 * @return response to client
	 */
	private static String[] appendOperation(JSONObject jsonReq){
		// the response to client
		String[] resMessage = new String[3];

		// get the information from the request
		String word = (String) jsonReq.get("word");
		String[] meanings = (String[]) jsonReq.get("meanings");

		// check whether the word is duplicate
		if (!dataBase.wordExistCheck(word)){
			resMessage[0] = "FAIL";
			resMessage[1] = "";
			resMessage[2] = "Append failed: The word does not exist. Try to add it.";
			return resMessage;
		}

		// operate the addition
		dataBase.appendToDic(word, meanings);
		resMessage[0] = "SUCC";
		resMessage[1] = dataBase.queryToDic(word);
		resMessage[2] = "Append Successfully. If there are duplicate meanings of the word, they will be deduplicated.";

		return resMessage;
	}


	/**
	 * Load the file into database's dictionary
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void loadFile() throws FileNotFoundException, UnsupportedEncodingException, NullPointerException {
		// get the file
		File file = new File(filePath);

		// initialize the io
		InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		// read the file
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null){
				// get word and meanings
				String word = line.split(":")[0];
				String meanings = line.split(":")[1];
				// separate meanings
				String[] separateMeanings = meanings.split(",");
				// store into the dictionary
				dataBase.addToDic(word, separateMeanings);

				inputStreamReader.close();
				bufferedReader.close();
			}
		} catch (ArrayIndexOutOfBoundsException e){
			setUIReq("The dictionary file's content is invalid. Please check it.");
			setUIReq(CUT_OFF);
			systemSucc = false;
		} catch (IOException e){
			// finish reading
		}
	}


	/**
	 * Store the dictionary into the file
	 * @throws IOException NullPointerException
	 */
	private static void writeFile() throws IOException, NullPointerException{
		// if there is error, the export can not be applied
		if (systemSucc == false){
			setUIReq("Export Failed: Please deal with the error above and restart the application.");
			setUIReq(CUT_OFF);
			return;
		}
		// initialize the io
		File file = new File(filePath);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
		// get the dictionary
		ConcurrentHashMap<String, List<String>> dictionary = dataBase.getDictionary();
		// write the dictionary into the file
		for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
			// get the word
			String currWord = entry.getKey();
			// get meanings
			List<String> currMeaningsList = entry.getValue();
			// transfer meanings into string
			StringBuffer currMeaningsString = new StringBuffer();
			for (String eachMeaning : currMeaningsList) {
				currMeaningsString.append(eachMeaning);
				currMeaningsString.append(",");
			}
			// remove the last command
			currMeaningsString = currMeaningsString.deleteCharAt(currMeaningsString.length() - 1);
			// write into the file
			bufferedWriter.write(currWord + ":" + currMeaningsString + "\n");
		}
		bufferedWriter.flush();
		bufferedWriter.close();
		// notice the terminal
		setUIReq("Export Successfully.");
		setUIReq(CUT_OFF);
	}


	/**
	 * The API for other class to call the storing of dictionary
	 */
	public static void storeDictionary(){
		// check the port number
		if (portNum < 0 || portNum > 65536){
			setUIReq("Store the dictionary failed: Invalid port number. Check the port number and restart the server.");
			setUIReq(CUT_OFF);
			return;
		}

		try {
			writeFile();
		} catch (IOException e){
			setUIReq("Store the dictionary failed: Can not find the file.");
			setUIReq(CUT_OFF);
			systemSucc = false;
		} catch (NullPointerException e){
			setUIReq("Store the dictionary failed: Can not establish the server. Check the port number and restart the server.");
			setUIReq(CUT_OFF);
			systemSucc = false;
		}
	}

	/**
	 * Append string to the UI window's request area
	 * @param appendString input string
	 */
	public static void setUIReq(String appendString){
		ui.getOutputRequest().append(appendString);
	}

	/**
	 * Set the client's total number
	 * @param num the total number
	 */
	public static void setClientNum(int num){
		clientNum = num;
	}

	/**
	 * Get current client's total number
	 * @return
	 */
	public static int getClientNum(){
		return clientNum;
	}

}
