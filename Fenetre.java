import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Fenetre extends JFrame implements ActionListener{
	int cote;
	Bouton[] bout = new Bouton[12];
	JPanel cardPane = new JPanel();
	JPanel pane= new JPanel();
	JPanel containerC = new JPanel();
	ContainerJeu containerJ;
	CardLayout cardL = new CardLayout();
	JLabel ech = new JLabel("Appuyez sur [ECHAP] si vous voulez quitter un niveau ou le jeu",JLabel.CENTER);
	boolean jeu = false;
	int niv = 0;
	BufferedWriter bW;
	BufferedReader bR;
	
	public Fenetre(){
		this.setDefaultLookAndFeelDecorated(true);
		this.setUndecorated(true);
		this.setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);
		this.setAlwaysOnTop(true);
		this.setBackground(Color.GRAY);
		this.setVisible(true);
		this.setVisible(false);
		
		double ch = this.getWidth()/25;
		double cl = this.getHeight()/16;
		cote = Math.min((int)ch,(int)cl);
		
		try{
			bR= new BufferedReader(new FileReader(new File("Niveau.txt")));
			String level = bR.readLine();
			try {
				niv = Integer.parseInt(level.substring(0,2));
			}catch (java.lang.NumberFormatException num) {
				niv = Integer.parseInt(level.substring(0,1));
			} 
		} catch (IOException e) {
			System.out.println("Error 404 : Level not Found !\n");
		} finally {
			try {
				bR.close();
			} catch (IOException io) {}
		}
		
		cardPane.requestFocusInWindow();
		
		cardPane.setLayout(cardL);
		
		containerC.setLayout(new GridLayout(3,4));
		for (int i = 0; i < 12; i++) {			
			bout[i] = new Bouton(i);
			bout[i].setPreferredSize(new Dimension(cote*15/4-10,cote*15/4-10));
			bout[i].repaint();
			containerC.add(bout[i]);
			if (i<=niv) {
				bout[i].setEnabled(true);
			} else {
				bout[i].setEnabled(false);
			}
			bout[i].addActionListener(this);
		}
		
		cardPane.add(containerC,"containerC");
		
		cardPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(27,0),"echap");
		cardPane.getActionMap().put("echap",new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				if (jeu) {
					try {
						bW = new BufferedWriter(new FileWriter(new File("Niveau.txt")));
						bW.flush();
						niv = containerJ.nI;
						String chaine= containerJ.nI+",";
						bW.write(chaine,0,chaine.length());
					} catch (IOException io) {
						System.out.println("Error writing the Level File\n");
					} finally {
						try {
							bW.close();
						} catch (IOException io) {}
					}
					for (int lvl = 0; lvl <=niv; lvl++) {
						bout[lvl].setEnabled(true);
					}
					cardL.show(cardPane,"containerC");
					jeu = false;
					containerC.requestFocusInWindow();
				} else {
					dispose();
				}
			}
		});

		pane.setLayout(new BorderLayout());
		ech.setFont(new Font("Trebuchet MS",0,cote-4));
		ech.setForeground(Color.RED);
		ech.setOpaque(true);
		ech.setBackground(new Color(100,100,100));
		
		pane.add(cardPane,BorderLayout.CENTER);
		pane.add(ech,BorderLayout.NORTH);
		this.setContentPane(pane);
		
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		int i = 0;
		while (i < 12 && arg0.getSource()!=bout[i]) {
			i++;
		}
		if (i == 12){
			System.out.println("Action Performed by Unknown Component !\n");
		} else {
			containerJ = new ContainerJeu(containerC.getWidth(),containerC.getHeight(),cote,i);
			cardPane.add(containerJ,"containerJ");
			jeu = true;
			cardL.show(cardPane,"containerJ");
			containerJ.requestFocusInWindow();
			containerJ.pB.requestFocusInWindow();
			containerJ.setLayout(new BorderLayout());
			containerJ.add(containerJ.pB,BorderLayout.CENTER);
			containerJ.revalidate();
		}
	}

	
	public static void main (String[] args) {
		Fenetre fen = new Fenetre();
	}
}