import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.Robot;
import java.io.IOException;

import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;

public class Frame extends javax.swing.JFrame implements ActionListener {
	MapEditor containerJ;
	int cote;
	Bouton[] bout = new Bouton[12];
	JPanel pane = new JPanel();
	JPanel containerC = new JPanel();
	CardLayout cardL = new CardLayout();
	boolean jeu = false;
	int niv = 0;
	BufferedWriter bW;
	BufferedReader bR;
	
	public Frame() {
		this.setTitle("MapEditor");
		this.setDefaultLookAndFeelDecorated(true);
		this.setExtendedState(this.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setBackground(Color.WHITE);
		
		double ch = this.getWidth()/25;
		double cl = this.getHeight()/15;
		cote = Math.min((int)ch,(int)cl);
		
		pane.requestFocusInWindow();
		pane.setLayout(cardL);
		
		containerC.setLayout(new GridLayout(3,4));
		for (int i = 0; i < 12; i++) {			
			bout[i] = new Bouton(i);
			bout[i].setSize(cote*15/4-10,cote*15/4-10);
			bout[i].repaint();
			containerC.add(bout[i]);
			bout[i].addActionListener(this);
		}
		
		pane.add(containerC,"containerC");
		
		this.setContentPane(pane);
		
		pane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(27,0),"echap");
		pane.getActionMap().put("echap",new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				if (jeu) {
					try {
						bW = new BufferedWriter(new FileWriter(new File("Cartes","Carte"+containerJ.nI+".txt")));
						bW.flush();
						for (int lar = 0; lar < 15; lar++) {
							for (int lon =0; lon <24 ; lon++) {
								String chaine = containerJ.map[lar][lon]+",";
								bW.write(chaine,0,chaine.length());
							}
							String chaine = containerJ.map[lar][24]+"";
							bW.write(chaine,0,chaine.length());
							bW.newLine();
						}
						String chaine= containerJ.posI[0]+","+containerJ.posI[1];
						bW.write(chaine,0,chaine.length());
						bW.newLine();
					} catch (IOException io) {
						System.out.println("Error writing the Level File");
					} finally {
						try {
							bW.close();
						} catch (IOException io) {}
					}
					cardL.show(pane,"containerC");
					jeu = false;
					containerC.requestFocusInWindow();
				} else {
					dispose();
				}
			}
		});
		
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		int i = 0;
		while (i < 12 && arg0.getSource()!=bout[i]) {
			i++;
		}
		if (i == 12){
			System.out.println("OMGGGG !!!!!");
		} else {
			containerJ = new MapEditor(25*cote,15*cote,cote,i);
			pane.add(containerJ,"containerJ");
			jeu = true;
			cardL.show(pane,"containerJ");
			containerJ.requestFocusInWindow();
			containerJ.setLayout(new BorderLayout());
			containerJ.revalidate();
		}
	}
	
	public static void main (String[] args) {
		Frame f = new Frame();
	}
}