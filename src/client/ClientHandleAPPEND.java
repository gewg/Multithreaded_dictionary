package client;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientHandleAPPEND extends ClientHandleRequest{

    public ClientHandleAPPEND(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream){
        super(objectOutputStream, objectInputStream);
    }

    /**
     * Send the Append request from client to server
     * @param request the request from ui
     * @return the response to ui
     */
    @Override
    public String[] operation(String[] request){
        // the response message return to the UI
        String[] resMessage;

        // get the word and meaning
        String word = request[1];
        String meanings = request[2];

        // check the word's format
        if (word.length() == 0){
            resMessage = createResponse("FAIL", "","Append failed: Input a word before adding");
            return resMessage;
        }

        // check meaning's format
        if (meanings.length() == 0){
            resMessage = createResponse("FAIL", "", "Append failed: Input the meaning(s) before adding");
            return resMessage;
        }

        // detect the meanings' number and split the meanings
        // replace all ", " with "," to avoid the different users' habit
        meanings = meanings.replaceAll(", ", ",");
        String[] meaningsArray = meanings.split(",");

        // generate the json
        JSONObject jsonReq = new JSONObject();
        // transfer the input word and meaning into json format
        jsonReq.put("type", "APPEND");
        jsonReq.put("word", word);
        jsonReq.put("meanings", meaningsArray);

        // communicate with the server
        try {
            JSONObject jsonRes = communicateWithServer(jsonReq);
            // get information
            String flag = (String) jsonRes.get("flag");
            String outputContent = (String) jsonRes.get("content");
            String outputTerminal = (String) jsonRes.get("terminal");
            return createResponse(flag, outputContent, outputTerminal);

        } catch(IOException e) {
            String flag = "FAIL";
            String outputContent = "";
            String outputTerminal = "Append failed: Communication between client and server failed. Please check the network or contact the Server's administrator.";
            return createResponse(flag, outputContent, outputTerminal);
        } catch (ClassNotFoundException e) {
            String flag = "FAIL";
            String outputContent = "";
            String outputTerminal = "Append failed: Response from server is an unknown object. Please contact the developer";
            return createResponse(flag, outputContent, outputTerminal);
        } catch (NullPointerException e){
            String flag = "FAIL";
            String outputContent = "";
            String outputTerminal = "Append failed: Can not connect the server. Please check the network, server address, server port number or contact the developer.";
            return createResponse(flag, outputContent, outputTerminal);
        }
    }
}
