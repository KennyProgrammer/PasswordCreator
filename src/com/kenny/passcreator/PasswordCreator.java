package com.kenny.passcreator;

import java.nio.file.Files;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.google.gson.*;

public class PasswordCreator implements Runnable {
	
	private JFrame window;
	private static final int WIDTH = 400, 
							 HEIGHT = 150;
	private static final int X = 
			(Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH) / 2;
	private static final int Y = 
			(Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT) / 2;
	
	private static final String SAVE_PATH = System.getProperty("user.dir") + "\\";
	
	private String lastEnteredLogin = null;
	private String lastEnteredPassword = null;
	
	private File file = new File(SAVE_PATH + "logins.json");
	private Map<String, String> data = new HashMap<>();
	
	public void run() {
		window = new JFrame("Password Creator v0.1");
		window.setBounds(X, Y, WIDTH, HEIGHT);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setVisible(true);
		
		JPanel panel = new JPanel(true);
		
		JButton btnNewFile = new JButton("New File");
		btnNewFile.addActionListener((ActionEvent e) ->  {
			String filepath = SAVE_PATH + JOptionPane.showInputDialog("Choose file name: ");
			if(filepath.contains("."))
				filepath = filepath.substring(0, filepath.indexOf('.')) + ".json";
			else 
				filepath += ".json";
			
			file = new File(filepath);
			try {
				file.createNewFile();
			} catch (IOException e1) { e1.printStackTrace(); }
		});
		
		JButton btnClearThisFile = new JButton("Clear This List");
		btnClearThisFile.addActionListener((ActionEvent e) -> {
			if(data.isEmpty()) 
				JOptionPane.showMessageDialog(panel, "This list '" + file.getAbsolutePath() + "' is already empty!");
			else 
				data.clear();
		});
		
		JButton btnAddAccount = new JButton("Add Account");
		btnAddAccount.addActionListener((ActionEvent e) -> {
			lastEnteredLogin = JOptionPane.showInputDialog("Enter login/username: ");
			lastEnteredPassword = JOptionPane.showInputDialog("Enter password: ");
			data.put(lastEnteredLogin, lastEnteredPassword);
		});
		
		JButton btnSave = new JButton("Save");
		btnSave.setLocation(100, 100);
		btnSave.addActionListener((ActionEvent e) -> {
			JsonObject root = new JsonObject();
			
			for(var entry : data.entrySet()) {
				JsonObject data = new JsonObject();
				data.addProperty("login", entry.getKey());
				data.addProperty("password", entry.getValue());
				root.add("Data ["+entry.getKey()+"]", data);
			}
			
			// Gets gson instance from builder.
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String out = gson.toJson(root);
					
			File file = new File(this.file.toPath() + SAVE_PATH);
			try {
				file.createNewFile();
				Files.writeString(file.toPath(), out);
			} catch (IOException e1) { e1.printStackTrace(); }
		});
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener((ActionEvent e) -> {
			file = new File(openFileDialog("*.json"));
			String data = null;
			try {
				data = Files.readString(file.toPath());
			} catch (IOException e1) { e1.printStackTrace(); }
			
			JsonElement jsonElement = JsonParser.parseString(data);
			var root = jsonElement.getAsJsonObject();
			
			for(var entry : root.entrySet()) {
				this.data.clear();
				this.data.put(entry.getKey(), entry.getValue().getAsString());
			}
			
		});
		
		panel.add(btnNewFile);
		panel.add(btnClearThisFile);
		panel.add(btnSave);
		panel.add(btnLoad);
		panel.add(btnAddAccount);
		
		
		window.add(panel);
	}
	
	private String fileDialog(int mode, String rootDir, String format) {
		Frame f = new Frame("");
		String title = mode == FileDialog.LOAD ? "Choose a file" : "Save file to";
		FileDialog fd = new FileDialog(f, title, mode);
		fd.setDirectory((rootDir == null || rootDir.isEmpty()) ? file.getAbsolutePath() : rootDir);
		fd.setFile(format);
		fd.setAutoRequestFocus(true);
		fd.setAlwaysOnTop(true);
		fd.setFocusableWindowState(true);
		fd.setVisible(true);
		if(fd.getDirectory() == null)
			return null;
		return fd.getDirectory() + fd.getFile();
	}
	
	public String openFileDialog(String rootDir, String format) {
		return fileDialog(FileDialog.LOAD, rootDir, format);
	}
	
	public String openFileDialog(String format) {
		return openFileDialog(file.getAbsolutePath(), format);
	}
	
	public String saveFileDialog(String rootDir, String format) {
		return fileDialog(FileDialog.SAVE, rootDir, format);
	}
	
	public String saveFileDialog(String format) {
		return saveFileDialog(file.getAbsolutePath(), format);
	}
	
	public static final void main(String... cmdargs) {
		new PasswordCreator().run();
	}
}
