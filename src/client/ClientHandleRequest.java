package client;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

public abstract class ClientHandleRequest {

    // the stream to communicate with client
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    public abstract String[] operation(String[] request);

    public ClientHandleRequest(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream){
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    /**
     * This function create the response to UI
     * @param flag whether the operation succeed
     * @param outputContent the output as content
     * @param outputTerminal the output for terminal
     * @return the response
     */
    public String[] createResponse(String flag, String outputContent, String outputTerminal){
        String[] result = new String[3];

        result[0] = flag;
        result[1] = outputContent;
        result[2] = outputTerminal;

        return result;
    }

    /**
     * Send request to server and get response from server
     * @param jsonReq
     * @return the response from server
     */
    public JSONObject communicateWithServer(JSONObject jsonReq) throws IOException, ClassNotFoundException, NullPointerException{
        // send request to server
        objectOutputStream.writeObject(jsonReq);
        // receive the response from server
        return (JSONObject) objectInputStream.readObject();
    }
}
