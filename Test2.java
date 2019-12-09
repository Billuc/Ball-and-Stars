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

import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;

public class Test2 extends JFrame implements ActionListener{
	int cote;
	Bouton[] bout = new Bouton[12];
	JPanel pane = new JPanel();
	JPanel containerC = new JPanel();
	test containerJ;
	CardLayout cardL = new CardLayout();
	boolean jeu = false;
	
	public Test2(){
		this.setTitle("Ballz (Pour quitter, appuyez sur [ECHAP] )");
		this.setSize((int)getToolkit().getScreenSize().getWidth(), ((int)getToolkit().getScreenSize().getHeight() - 39));
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
			containerJ = new test(containerC.getWidth(),containerC.getHeight(),cote,i);
			pane.add(containerJ,"containerJ");
			jeu = true;
			cardL.show(pane,"containerJ");
			containerJ.requestFocusInWindow();
			containerJ.pB.requestFocusInWindow();
			containerJ.setLayout(new BorderLayout());
			containerJ.add(containerJ.pB,BorderLayout.CENTER);
			containerJ.revalidate();
		}
	}

	
	public static void main (String[] args) {
		Test2 fen = new Test2();
	}
}