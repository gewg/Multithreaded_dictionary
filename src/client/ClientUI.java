package client;

import client.Client;
import java.awt.EventQueue;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class ClientUI extends Thread{

	private JFrame frmDictionary;
	private JTextField inputMeaning;
	private JTextField inputWord;
	
	@Override
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ClientUI window = new ClientUI();
					window.frmDictionary.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDictionary = new JFrame();
		frmDictionary.setResizable(false);
		frmDictionary.setTitle("Dictionary");
		frmDictionary.setBounds(100, 100, 825, 350);
		frmDictionary.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDictionary.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Dictinoary");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblNewLabel.setBounds(19, 13, 153, 26);
		frmDictionary.getContentPane().add(lblNewLabel);

		inputMeaning = new JTextField();
		inputMeaning.setBounds(19, 119, 220, 26);
		frmDictionary.getContentPane().add(inputMeaning);
		inputMeaning.setColumns(10);

		JLabel wordLabel = new JLabel("Input Word");
		wordLabel.setBounds(19, 51, 116, 16);
		frmDictionary.getContentPane().add(wordLabel);

		JLabel meaningLabel = new JLabel("Input Meaning");
		meaningLabel.setBounds(19, 100, 116, 16);
		frmDictionary.getContentPane().add(meaningLabel);

		JLabel detailsLabel = new JLabel("Content Output");
		detailsLabel.setBounds(19, 146, 116, 26);
		frmDictionary.getContentPane().add(detailsLabel);

		JTextArea outputTerminal = new JTextArea();
		outputTerminal.setEditable(false);
		outputTerminal.setText("This window shows the response from the terminal. For example: Add successfully.");
		outputTerminal.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		outputTerminal.setBounds(272, 75, 282, 65);
		outputTerminal.setLineWrap(true);
		outputTerminal.setWrapStyleWord(true);
		frmDictionary.getContentPane().add(outputTerminal);

		JScrollPane scrollPaneOutputTerminal = new JScrollPane();
		scrollPaneOutputTerminal.setBounds(272, 75, 282, 65);
		scrollPaneOutputTerminal.setViewportView(outputTerminal);
		frmDictionary.getContentPane().add(scrollPaneOutputTerminal);

		JTextArea outputContent = new JTextArea();
		outputContent.setEditable(false);
		outputContent.setText("This window shows the response from your operation. For example: Meanings of word.\n\nPress the HELP button for help.");
		outputContent.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		outputContent.setBounds(19, 173, 220, 119);
		outputContent.setLineWrap(true);
		outputContent.setWrapStyleWord(true);

		JScrollPane scrollPaneOutputContent = new JScrollPane();
		scrollPaneOutputContent.setBounds(19, 173, 220, 119);
		scrollPaneOutputContent.setViewportView(outputContent);
		frmDictionary.getContentPane().add(scrollPaneOutputContent);

		JTextArea outputHistory = new JTextArea();
		outputHistory.setWrapStyleWord(true);
		outputHistory.setLineWrap(true);
		outputHistory.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		outputHistory.setEditable(false);
		outputHistory.setBounds(588, 75, 220, 217);
		frmDictionary.getContentPane().add(outputHistory);

		JScrollPane scrollPaneOutputHistory = new JScrollPane();
		scrollPaneOutputHistory.setBounds(588, 75, 220, 217);
		scrollPaneOutputHistory.setViewportView(outputHistory);
		frmDictionary.getContentPane().add(scrollPaneOutputHistory);

		JButton addButton = new JButton("ADD");
		addButton.setForeground(Color.BLUE);
		addButton.setBackground(Color.WHITE);
		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// create the request
				String word = inputWord.getText();
				String meaning = inputMeaning.getText();
				String[] request = new String[]{"ADD", word, meaning};

				// request to client
				String[] resp = Client.handleRequest(request);
				String content = resp[1];
				String terminal = resp[2];

				// show the response
				outputTerminal.setText(terminal);
				outputContent.setText(content);

				if (resp[0].equals("SUCC")) {
					// backup the content to the history
					StringBuffer contentHistory = new StringBuffer();
					contentHistory.append("Request: ADD\n");
					contentHistory.append(String.format("Word: %s\n", word));
					contentHistory.append(String.format("Meanings: %s\n", meaning));
					outputHistory.append(contentHistory.toString());
					outputHistory.append("\r\n");
				}
			}
		});
		addButton.setBounds(272, 217, 86, 26);
		frmDictionary.getContentPane().add(addButton);

		JButton deleteButton = new JButton("DELETE");
		deleteButton.setForeground(Color.RED);
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// create the request
				String word = inputWord.getText();
				String[] backupRequest = new String[]{"QUERY", word};
				String[] mainRequest = new String[]{"DELETE", word};

				// query to back up the meanings
				String backupMeanings = Client.handleRequest(backupRequest)[1];

				// delete
				String[] resp = Client.handleRequest(mainRequest);
				String content = resp[1];
				String terminal = resp[2];
				// show the response
				outputTerminal.setText(terminal);
				outputContent.setText(content);

				if (resp[0].equals("SUCC")) {
					// backup the content to the history
					StringBuffer contentHistory = new StringBuffer();
					contentHistory.append("Request: DELETE\n");
					contentHistory.append(String.format("Word: %s\n", word));
					contentHistory.append(String.format("Meanings before delete: \n%s\n", backupMeanings));
					outputHistory.append(contentHistory.toString());
				}
			}
		});
		deleteButton.setBounds(370, 169, 86, 26);
		frmDictionary.getContentPane().add(deleteButton);

		JButton queryButton = new JButton("QUERY");
		queryButton.setForeground(Color.BLUE);
		queryButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// create the request
				String word = inputWord.getText();
				String[] request = new String[]{"QUERY", word};

				// get the response
				String[] resp = Client.handleRequest(request);
				String content = resp[1];
				String terminal = resp[2];
				// show the response
				outputTerminal.setText(terminal);
				outputContent.setText(content);

				if (resp[0].equals("SUCC")) {
					// backup the content to the history
					StringBuffer contentHistory = new StringBuffer();
					contentHistory.append("Request: QUERY\n");
					contentHistory.append(String.format("Word: %s\n", word));
					contentHistory.append(String.format("Meanings: \n%s\n", content));
					outputHistory.append(contentHistory.toString());
					outputHistory.append("\r\n");
				}
			}
		});
		queryButton.setBounds(272, 169, 86, 26);
		frmDictionary.getContentPane().add(queryButton);

		JButton updateButton = new JButton("UPDATE");
		updateButton.setForeground(Color.RED);
		updateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// create the request
				String word = inputWord.getText();
				String meaning = inputMeaning.getText();
				String[] backupRequest = new String[]{"QUERY", word};
				String[] mainRequest = new String[]{"UPDATE", word, meaning};

				// query to back up the meanings
				String backupMeanings = Client.handleRequest(backupRequest)[1];

				// delete
				String[] resp = Client.handleRequest(mainRequest);
				String content = resp[1];
				String terminal = resp[2];
				// show the response
				outputTerminal.setText(terminal);
				outputContent.setText(content);

				// backup
				if (resp[0].equals("SUCC")) {
					// backup the content to the history
					StringBuffer contentHistory = new StringBuffer();
					contentHistory.append("Request: UPDATE\n");
					contentHistory.append(String.format("Word: %s\n", word));
					contentHistory.append(String.format("Meanings before updating: \n%s\n", backupMeanings));
					outputHistory.append(contentHistory.toString());
				}
			}
		});
		updateButton.setBounds(370, 217, 86, 26);
		frmDictionary.getContentPane().add(updateButton);

		inputWord = new JTextField();
		inputWord.setColumns(10);
		inputWord.setBounds(19, 70, 220, 26);
		frmDictionary.getContentPane().add(inputWord);

		JButton appendButton = new JButton("APPEND");
		appendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// create the request
				String word = inputWord.getText();
				String meaning = inputMeaning.getText();
				String[] mainRequest = new String[]{"APPEND", word, meaning};

				// append
				String[] resp = Client.handleRequest(mainRequest);
				String content = resp[1];
				String terminal = resp[2];
				// show the response
				outputTerminal.setText(terminal);
				outputContent.setText(content);

				if (resp[0].equals("SUCC")) {
					// backup the content to the history
					StringBuffer contentHistory = new StringBuffer();
					contentHistory.append("Request: APPEND\n");
					contentHistory.append(String.format("Word: %s\n", word));
					contentHistory.append(String.format("Meanings be appended: %s\n", meaning));
					outputHistory.append(contentHistory.toString());
					outputHistory.append("\r\n");
				}
			}
		});
		appendButton.setForeground(Color.BLUE);
		appendButton.setBounds(272, 266, 86, 26);
		frmDictionary.getContentPane().add(appendButton);

		JLabel terminalOutput = new JLabel("Terminal Output");
		terminalOutput.setBounds(272, 51, 116, 16);
		frmDictionary.getContentPane().add(terminalOutput);

		JButton clearButton = new JButton("CLEAR");
		clearButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// empty all the areas
				inputMeaning.setText("");
				inputWord.setText("");
				outputContent.setText("");
				outputTerminal.setText("");
			}
		});
		clearButton.setBounds(468, 169, 86, 26);
		frmDictionary.getContentPane().add(clearButton);

		JButton helpButton = new JButton("HELP");
		helpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				StringBuffer helpContent = new StringBuffer();
				helpContent.append("1. QUERY\nFunction: Search the meanings of the entered word.\nLimit: One word can be searched each time.\n\n");
				helpContent.append("2. ADD\nFunction: Add the entered meanings to the entered word.\nLimit: One word can be attached each time. The meanings should be separated by the comma.\n\n");
				helpContent.append("3. APPEND\nFunction: Append the entered meanings to the entered word.\nLimit: One word can be entered each time. The meanings should be separated by the comma.\n\n");
				helpContent.append("4. DELETE\nFunction: Delete the entered word with corresponding meanings.\nLimit: One word can be deleted each time.\nWarning: The word will be completely removed. Check the History area for backup.\n\n");
				helpContent.append("5. UPDATE\nFunction: Replace the entered word's original meanings by the entered meanings.\nLimit: One word can be attached each time. The meanings should be separated by the comma.\nWarning: The original meanings will be completely removed. Check the History area for backup.\n\n");
				helpContent.append("6. CLEAR\nFunction: Clear all 5 areas.\n\n");
				helpContent.append("7. HELP\nFunction: Exhibits the functions of buttons.");
				outputContent.setText(helpContent.toString());
			}
		});
		helpButton.setBounds(468, 217, 86, 26);
		frmDictionary.getContentPane().add(helpButton);

		JLabel historyLabel = new JLabel("History");
		historyLabel.setBounds(588, 51, 116, 16);
		frmDictionary.getContentPane().add(historyLabel);
	}
}