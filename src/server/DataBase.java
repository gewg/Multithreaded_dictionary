package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to motify the database.
 * This class stores all information about the words, meanings and details
 */
public class DataBase {

    // the dictionary to store all the words, meanings and details
    private ConcurrentHashMap<String, List<String>> dictionary = new ConcurrentHashMap<>();

    /**
     * Add the input word and relative meanings to the database
     * @param word the input word
     * @param meanings the word's meanings
     */
    public void addToDic(String word, String[] meanings){
        // transfer the String[] to ArrayList
        ArrayList<String> meaningsList = new ArrayList<>(Arrays.asList(meanings));

        // add to the dictionary
        dictionary.put(word, meaningsList);
    }

    /**
     * Delete the word from the dictionary
     * @param word the input word
     */
    public void deleteToDic(String word){
        // delete from the dictionary
        dictionary.remove(word);
    }


    /**
     * Query the word in the dictionary
     * @param word the input word
     * @return String[] with two elements:1. the meanings 2. the success's notification
     */
    public String queryToDic(String word){

        // the response to client
        String resMessage;

        // get the number of meanings
        int meaningNum = dictionary.get(word).size();
        String[] meanings =  dictionary.get(word).toArray(new String[meaningNum]);

        // assemble the meanings to a string
        StringBuffer meaningsString = new StringBuffer();
        for (int i = 0; i < meaningNum; i ++){
            meaningsString.append(i + 1); // add the number
            meaningsString.append(". "); // add the dot after number
            meaningsString.append(meanings[i]); // add the meaning
            meaningsString.append("\n"); // change the line
        }

        resMessage = meaningsString.toString();

        return resMessage;
    }


    /**
     * Update the input word's meanings
     * @param word the input word
     * @param meanings the word's meaning
     */
    public void updateToDic(String word, String[] meanings){
        // transfer the String[] to ArrayList
        ArrayList<String> meaningsList = new ArrayList<>(Arrays.asList(meanings));

        // update the meanings to the dictionary
        dictionary.put(word, meaningsList);
    }


    /**
     * Append the meanings to corresponding word
     * @param word
     * @param meanings
     * @return
     */
    public void appendToDic(String word, String[] meanings){

        // transfer the String[] to ArrayList
        ArrayList<String> meaningsList = new ArrayList<>(Arrays.asList(meanings));

        // append the meanings to the existing meanings
        dictionary.get(word).addAll(meaningsList);
        // deduplicate the meanings
        HashSet<String> newMeaningsList = new HashSet<>(dictionary.get(word));

        // update the meanings to the dictionary
        dictionary.put(word, new ArrayList<>(newMeaningsList));
    }


    /**
     * Check whether the input word already exists in the dictionary
     * @param word
     * @return true if word exist and false if word does not
     */
    public boolean wordExistCheck(String word){
        return dictionary.containsKey(word);
    }


    /**
     * Get the dictionary
     * @return
     */
    public ConcurrentHashMap<String, List<String>> getDictionary(){
        return dictionary;
    }
}
