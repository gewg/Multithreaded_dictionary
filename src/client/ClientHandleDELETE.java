package client;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientHandleDELETE extends ClientHandleRequest{

    public ClientHandleDELETE(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream){
        super(objectOutputStream, objectInputStream);
    }


    /**
     * Send the DELETE request from client to server
     * @param request the request from ui
     * @return the response to ui
     */
    @Override
    public String[] operation(String[] request) {
        // the response message return to the UI
        String[] resMessage;

        // get the word and meaning
        String word = request[1];

        // check the word's format
        if (word.length() == 0){
            resMessage = createResponse("FAIL", "","Delete failed: Input a word before adding");
            return resMessage;
        }

        // generate the json
        JSONObject jsonReq = new JSONObject();
        // transfer the input word and meaning into json format
        jsonReq.put("type", "DELETE");
        jsonReq.put("word", word);

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
            String outputTerminal = "Delete failed: Communication between client and server failed. Please contact the developer.";
            return createResponse(flag, outputContent, outputTerminal);
        } catch (ClassNotFoundException e) {
            String flag = "FAIL";
            String outputContent = "";
            String outputTerminal = "Delete failed: Response from server is an unknown object. Please contact the developer";
            return createResponse(flag, outputContent, outputTerminal);
        } catch (NullPointerException e){
            String flag = "FAIL";
            String outputContent = "";
            String outputTerminal = "Delete failed: Can not connect the server. Please check the network or contact the developer.";
            return createResponse(flag, outputContent, outputTerminal);
        }
    }
}
