package com.businessobjects.samples;


import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

import com.crystaldecisions.sdk.exception.SDKException;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class CrystalCredentialGui extends JFrame implements ActionListener {
		private static JFrame frame;
		private static JPanel panel;
		private static JTextField serverField;
		private static JTextField userField;
		private static JPasswordField passwordField;
		private static JTextField credfileField;
		private static JButton credfilesearchButton;
		private static JButton runButton;
		
		private static JLabel serverLabel;
		private static JLabel userLabel;
		private static JLabel passwordLabel;
		private static JLabel credfileLabel;
		private static JLabel outputLabel;
		
		private static String CRED_FILEPATH_TEXT;
		private static String SERVER_TEXT;
		private static String USER_TEXT;
		private static String PASS_TEXT;
		
		public static void main(String[] args) {
			init();
			setIcon("./cr_changer.png");
			frame.setSize(400, 400);
			frame.setVisible(true);
			
			
		}
		
		private static void init() {
			frame = new JFrame("Crystal Reports Credential Utility");
			panel = new JPanel();
			panel.setLayout(new BoxLayout(panel,1));
			panel.setBorder(new EmptyBorder(10, 10, 10, 10));
			serverField = new JTextField("",20);
			serverField.setMaximumSize(new Dimension(400,20));
			userField = new JTextField("",20);
			userField.setMaximumSize(new Dimension(300,20));
			passwordField = new JPasswordField("",20);
			passwordField.setMaximumSize(new Dimension(300,20));
			credfileField = new JTextField("",40);
			credfileField.setMaximumSize(new Dimension(700,20));
			credfilesearchButton = new JButton("Browse");
			credfilesearchButton.addActionListener(e -> {
				try {
					CRED_FILEPATH_TEXT = getFilePath();
					credfileField.setText(CRED_FILEPATH_TEXT);
				} catch (IOException e1) {
					System.out.println("IO File Error");
					outputLabel.setText("IO FILE ERROR");
				}
			});
			runButton = new JButton("Run");
			runButton.addActionListener(e -> {
				outputLabel.setText("Running...");
				panel.revalidate();
				panel.repaint();
				SERVER_TEXT = serverField.getText();
				USER_TEXT = userField.getText();
				PASS_TEXT = passwordField.getText();
				CrystalCredChanger ccc = new CrystalCredChanger(SERVER_TEXT, USER_TEXT, PASS_TEXT);
//				try {
//					System.out.println(ccc.readCsv(CRED_FILEPATH_TEXT));
//				} catch (FileNotFoundException e2) {
//					// TODO Auto-generated catch block
//					e2.printStackTrace();
//				}
				try {
					changeCredentials(ccc);
				} catch (SDKException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			
			serverLabel = new JLabel("Server");
			userLabel = new JLabel("User");
			passwordLabel = new JLabel("Password");
			credfileLabel = new JLabel("Credential mapping file");
			outputLabel = new JLabel("Output");
			
			frame.add(panel);
			panel.add(serverLabel);
			panel.add(serverField);
			panel.add(userLabel);
			panel.add(userField);
			panel.add(passwordLabel);
			panel.add(passwordField);
			panel.add(Box.createVerticalStrut(20));
			panel.add(credfileLabel);
			panel.add(credfileField);
			panel.add(credfilesearchButton);
			panel.add(Box.createVerticalStrut(80));
			panel.add(outputLabel);
			panel.add(Box.createVerticalStrut(10));
			panel.add(runButton);
		}
		
		private static void setIcon(String filepath) {
			try {
				File imgFile = new File(filepath);
				Image img = ImageIO.read(imgFile);
				frame.setIconImage(img);
			}catch(IOException e) {
				System.out.println(e.getMessage());
			}finally {}
		}
		
		private static int changeCredentials(CrystalCredChanger ccc) throws Exception {
			return ccc.changeReports();
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String e = arg0.getActionCommand();
		}
		
		private static String getFilePath() throws IOException {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			fileChooser.setDialogTitle("Select migration CSV");
			int result = fileChooser.showOpenDialog(null);
			if(result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				return selectedFile.getAbsolutePath();
			}
			return "";
		}
}

