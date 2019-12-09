import javax.swing.*;
import java.awt.*;
import java.io.*;

import java.awt.event.*;
import javax.imageio.ImageIO;

public class MapEditor extends javax.swing.JPanel implements ActionListener{
	int [][] map = new int[15][25];
	int cote;
	int nI;
	int[] posI;
	double xr;
	double yr;
	private BufferedReader bR;
	private BufferedWriter bW;
	Image[] imgs = new Image[10];
	Bouton[] bo = new Bouton[25*15];
	
	public MapEditor(int x, int y, int cote, int nCarte) {
		this.cote = cote;
		this.setSize(x,y);
		this.nI = nCarte;
		
		
		try{
			bR= new BufferedReader(new FileReader(new File("Cartes","Carte"+nI+".txt")));
			for (int i = 0 ; i<15; i++) {
				map[i] = analyseStr(bR.readLine());
			}
			posI = analyseStr(bR.readLine());
		}catch (IOException e){
			System.out.println("Error 404 : Carte n°"+nCarte+" Not Found !");
			try {
				Robot robot = new Robot();
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);
			} catch (AWTException awtE) {}
		} finally {
			try {
				bR.close();
			} catch (IOException io) {}
		}
		
		for (int c = 0; c <imgs.length; c++) {
			Image img = null;
			try {
				img = ImageIO.read(new File("Images","Case"+c+".png"));
			} catch (IOException e) {
				System.out.println("Error 404 : Image n°"+c+" Not Found !");
			}
			imgs[c] = img;
		}
		
		xr = ((x-25*cote)/2+(posI[0]+1/4.)*cote);
		yr = ((y-15*cote)/2+(posI[1]+1/4.)*cote);
		
		this.setLayout(new GridLayout(15,25));
		
		for (int i = 0; i<bo.length; i++) {
			bo[i] = new Bouton(i) {
				public void paintComponent(Graphics g) {
					g.drawImage(imgs[map[(number-number%25)/25][number%25]], 0, 0, this.getWidth(), this.getHeight(), this);
				}
			};
			bo[i].setSize(cote*15/4-10,cote*15/4-10);
			bo[i].repaint();
			this.add(bo[i]);
			bo[i].addActionListener(this);
		}
		
	}
	
	public void actionPerformed(ActionEvent arg0) {
		int i = 0;
		while (i < bo.length && arg0.getSource()!=bo[i]) {
			i++;
		}
		if (i==bo.length) {
			System.out.println("bite");
		} else {
			if (map[(i-i%25)/25][i%25]<9) {
				map[(i-i%25)/25][i%25]++;
			} else {
				map[(i-i%25)/25][i%25]=0;
			}
			this.repaint();
		}
	}
	
	public int[] analyseStr(String s) {
		int[] ligne = new int[25];
		int curseur=0;
		int curseurLigne = 0;
		for (int i =0; i< s.length(); i++) {
			if (((int)(s.charAt(i))==44)&&(curseurLigne<25)) {
				ligne[curseurLigne]=Integer.parseInt(s.substring(curseur,i));
				curseurLigne++;
				curseur = i+1;
			}
		}
		ligne[curseurLigne]=Integer.parseInt(s.substring(curseur,s.length()));
		return ligne;
	}
}