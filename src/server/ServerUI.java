package server;

import java.awt.EventQueue;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerUI extends Thread{

    private JFrame dictionaryServerFrame;
    private static JTextArea outputRequest;

    /**
     * Launch the application.
     */
    @Override
    public void run(){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerUI window = new ServerUI();
                    window.dictionaryServerFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public ServerUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        dictionaryServerFrame = new JFrame();
        dictionaryServerFrame.setResizable(false);
        dictionaryServerFrame.setBounds(100, 100, 541, 357);
        dictionaryServerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dictionaryServerFrame.getContentPane().setLayout(null);

        JLabel lblDictinoaryServer = new JLabel("Dictionary  Server");
        lblDictinoaryServer.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        lblDictinoaryServer.setBounds(16, 14, 175, 26);
        dictionaryServerFrame.getContentPane().add(lblDictinoaryServer);

        outputRequest = new JTextArea();
        outputRequest.setWrapStyleWord(true);
        outputRequest.setLineWrap(true);
        outputRequest.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        outputRequest.setEditable(false);
        outputRequest.setBounds(16, 88, 502, 217);
        dictionaryServerFrame.getContentPane().add(outputRequest);

        JScrollPane scrollPaneOutputRequest = new JScrollPane();
        scrollPaneOutputRequest.setBounds(16, 88, 502, 217);
        scrollPaneOutputRequest.setViewportView(outputRequest);
        dictionaryServerFrame.getContentPane().add(scrollPaneOutputRequest);

        JButton clearButton = new JButton("CLEAR");
        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                outputRequest.setText("");
            }
        });
        clearButton.setBounds(302, 47, 102, 29);
        dictionaryServerFrame.getContentPane().add(clearButton);

        JButton exportButton = new JButton("EXPORT");
        exportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               Server.storeDictionary();
            }
        });
        exportButton.setBounds(416, 47, 102, 29);
        dictionaryServerFrame.getContentPane().add(exportButton);

        JLabel lblNewLabel = new JLabel("Request From Client");
        lblNewLabel.setBounds(16, 52, 140, 16);
        dictionaryServerFrame.getContentPane().add(lblNewLabel);
    }


    /**
     * Get the outputRequest area
     * @return
     */
    public JTextArea getOutputRequest(){
        return outputRequest;
    }
}

