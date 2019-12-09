import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.Font;
import javax.swing.JLabel;

import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
 
public class test extends JPanel{
	int [][] map = new int[15][25];
	double t = 0.1;
	boolean collide = false;
	int cote;
	int nI;
	int[] posI;
	double xr;
	double yr;
	PanelBall pB;
	private BufferedReader bR;
	Image[] imgs = new Image[11];
	
	public test (int x, int y, int cote, int nCarte) {
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
		
		xr = ((x-25*cote)/2+posI[0]*cote);
		yr = ((y-15*cote)/2+posI[1]*cote);
		
		pB = new PanelBall();
		pB.setSize(x,y);
		
		
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0,false),"left");
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0,false),"right");
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0,true),"leftR");
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0,true),"rightR");
		this.getActionMap().put("left",new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				pB.vit[0]=-4*cote;
				pB.acc[0]=0;
			}
		});
		this.getActionMap().put("right", new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				pB.vit[0]=4*cote;
				pB.acc[0]=0;
			}
		});
		this.getActionMap().put("leftR", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pB.vit[0]=0;
			}
		});
		this.getActionMap().put("rightR", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pB.vit[0]=0;
			}
		});
	}
	
	class PanelBall extends JPanel{
		public double[] acc = new double[2];
		public double[] vit = new double[2];
		public double[] pos = new double[2];
		int x = 0;
		int y = 0;
		int diametre;
		
		public PanelBall(){
			acc[1] = 448*cote/27;
			pos[0] = xr;
			pos[1] = yr;
			this.diametre = (int)(cote*2/3);
			this.setOpaque(false);
		}
		
		protected void paintComponent(Graphics g){
			g.setColor(new Color(0,0,0,0));
			g.fillRect(x,y,diametre+2,diametre+2);
			g.setColor(Color.YELLOW);
			g.fillOval((int)pos[0],(int)pos[1],diametre,diametre);
			g.setColor(Color.BLACK);
			g.drawOval((int)pos[0],(int)pos[1],diametre,diametre);
			try{
				Thread.sleep(25);
			} catch (InterruptedException e) {}
			anim();
		}
		
		
		private void anim() {
			this.x = (int)(this.pos[0]-(diametre+1)/2);
			this.y = (int)(this.pos[1]-(diametre+1)/2);
			if ((this.pos[0]+this.vit[0]/40>(this.getWidth()-25*cote)/2)&&(this.pos[0]+this.vit[0]/40+diametre<(this.getWidth()+25*cote)/2)) {
				this.pos[0]+=this.vit[0]/40;
			}
			this.pos[1]+=this.vit[1]/40;
			if (this.pos[1]+diametre>(this.getHeight()+15*cote)/2){
				this.vit[0] = 0;
				this.vit[1] = 0;
				this.pos[0] = xr;
				this.pos[1] = yr;
				resetStars();
			} else {
				this.vit[0]+=this.acc[0]/40;
				this.vit[1]+=this.acc[1]/40;
				detekColl(this);
			}
			this.repaint();
		}
	}
	
	protected void paintComponent(Graphics g){
		g.setColor(new Color(100,100,100));
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		
		int x0 = (int)((this.getWidth()-25*cote)/2);
		int y0 = (int)((this.getHeight()-15*cote)/2);
		
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 25; j++) {
				g.drawImage(imgs[map[i][j]], x0 + cote*j, y0 + cote*i,cote, cote, this);
			}
		}	
		
		g.setFont(new Font("Arial",0,this.cote));
		g.setColor(Color.BLACK);
		g.drawString(String.valueOf(nI+1),(int)x0/4,y0+cote);
	}
	
	public void detekColl(PanelBall panBall) {
		double xc = panBall.pos[0]+panBall.diametre/2;
		double yc = panBall.pos[1]+panBall.diametre/2;
		
		int x0 = (int)((this.getWidth()-25*cote)/2);
		int y0 = (int)((this.getHeight()-15*cote)/2);
		
		int caseX = (int)((xc-x0)/cote);
		int caseY = (int)((yc-y0)/cote);
		
		int[] coord = new int[2];
		
		boolean coll = false;
		
		for (int dx = -1; dx<=1; dx+=2) {
			if ((caseX+dx>=0)&&(caseX+dx<=24)&&(Math.abs(x0+(caseX+(dx+1)/2)*cote-xc)<panBall.diametre/2)){
				coord[0] = caseX+dx;
				coord[1] = caseY;
				collision(panBall, coord , dx+2);
				if ((map[caseY][caseX+dx]!=0)&&(map[caseY][caseX+dx]!=10)&&(map[caseY][caseX+dx]!=11)&&(map[caseY][caseX+dx]!=9)) {
					coll = true;
				}
			}
		}
		for (int dy = -1; dy<=1 ; dy+=2) {
			if ((caseY+dy>=0)&&(caseY+dy<=14)&&(Math.abs(y0+(caseY+(dy+1)/2)*cote-yc)<panBall.diametre/2)) {
				coord[0] = caseX;
				coord[1] = caseY+dy;
				collision(panBall, coord , dy+1);
				if ((map[caseY+dy][caseX]!=0)&&(map[caseY+dy][caseX]!=10)&&(map[caseY+dy][caseX]!=11)&&(map[caseY+dy][caseX]!=9)) {
					coll = true;
				}
			}
		}
		
		if (coll == false) {
			int dx=0;
			int dy=0;
			int xCoin = -100;
			int yCoin = -100;
			//Détermination du coin touché
			for (int ab = -1; ab <= 1; ab+=2) {
				for (int or = -1; or<=1; or+=2) {
					int xf = x0+(caseX + (ab+1)/2)*cote+ab;
					int yf = y0+(caseY + (or+1)/2)*cote+or;
					if (((xc-xf)*(xc-xf)+(yc-yf)*(yc-yf)<=panBall.diametre*panBall.diametre/4)&&(caseX+ab>=0)&&(caseX+ab<=24)&&(caseY+or>=0)&&(caseY+or<=14)) {
						dx=ab;
						dy=or;
						xCoin = xf;
						yCoin = yf;
						System.out.println("coin:"+dx+"/"+dy);
					}
				}
			}
			
			if ((dx!=0)&&(dy!=0)) {
				coord[0] = caseX+dx;
				coord[1] = caseY+dy;
				if ((dy==-1)&&(Math.abs(yCoin-yc)>=Math.abs(xCoin-xc))){
					//collision haut
					collision(panBall, coord , 0);
				} else if ((dx==-1)&&(Math.abs(yCoin-yc)<Math.abs(xCoin-xc))) {
					//collision gauche
					collision(panBall, coord , 1);
				} else if ((dy==1)&&(Math.abs(yCoin-yc)>=Math.abs(xCoin-xc))) {
					//collision bas
					collision(panBall, coord, 2);
				} else if ((dx==1)&&(Math.abs(yCoin-yc)<Math.abs(xCoin-xc))) {
					//collision droite
					collision(panBall, coord , 3);
				}
			}
		}
	}
	
	public void collision(PanelBall panBall, int[] coordCase, int noColl) {
		
		double xc = panBall.pos[0]+panBall.diametre/2;
		double yc = panBall.pos[1]+panBall.diametre/2;
		
		int x0 = (int)((this.getWidth()-25*cote)/2);
		int y0 = (int)((this.getHeight()-15*cote)/2);
		
		int caseX = (int)((xc-x0)/cote);
		int caseY = (int)((yc-y0)/cote);
		
		switch(noColl) {
			case 0:
				switch(map[coordCase[1]][coordCase[0]]) {
					case 1:
						panBall.vit[1]=0;
						panBall.pos[1]=y0+caseY*cote;
						break;
					case 2:
						panBall.vit[1]=0;
						panBall.pos[1]=y0+caseY*cote;
						break;
					case 3:
						panBall.vit[1]=0;
						panBall.pos[1]=y0+caseY*cote;
						break;
					case 4:
						panBall.vit[1]=0;
						panBall.pos[1]=y0+caseY*cote;
						break;
					case 5:
						panBall.vit[1]=0;
						panBall.pos[1]=y0+caseY*cote;
						break;
					case 6:
						panBall.vit[1]=0;
						panBall.pos[1]=y0+caseY*cote;
						break;
					case 7:
						panBall.vit[1]=0;
						panBall.pos[1]=y0+caseY*cote;
						break;
					case 8:
						panBall.vit[0] = 0;
						panBall.vit[1] = 0;
						panBall.pos[0] = xr;
						panBall.pos[1] = yr;
						resetStars();
						try{
							Thread.sleep(200);
						}catch(InterruptedException e) {}
						break;
					case 9:
						map[coordCase[1]][coordCase[0]]=10;
						gagne();
						break;
					default:
						break;						
				}
				break;
			case 1:
				switch(map[coordCase[1]][coordCase[0]]) {
					case 1:
						panBall.pos[0]=x0+caseX*cote+10;
						break;						
					case 2:
						panBall.pos[0]=x0+caseX*cote+10;
						break;
					case 3:
						panBall.pos[0]=x0+caseX*cote+10;
						break;
					case 4:
						panBall.pos[0]=x0+caseX*cote+10;
						break;
					case 5:
						panBall.pos[0]=x0+caseX*cote+10;
						break;
					case 6:
						panBall.pos[0]=x0+caseX*cote+10;
						break;
					case 7:
						panBall.pos[0]=x0+caseX*cote+10;
						break;
					case 8:
						panBall.vit[0] = 0;
						panBall.vit[1] = 0;
						panBall.pos[0] = xr;
						panBall.pos[1] = yr;
						resetStars();
						try{
							Thread.sleep(200);
						}catch(InterruptedException e) {}
						break;
					case 9:
						map[coordCase[1]][coordCase[0]]=10;
						gagne();
						break;
					default:
						break;						
				}
				break;
			case 2:
				switch(map[coordCase[1]][coordCase[0]]) {
					case 1:
						panBall.vit[1]=-112*cote/18;
						panBall.pos[1]=y0+(caseY+1)*cote-panBall.diametre;
						break;
					case 2:
						panBall.vit[1]=-112*cote/18;
						panBall.pos[1]=y0+(caseY+1)*cote-panBall.diametre;
						break;
					case 3:
						panBall.vit[1]=-112*cote/18;
						panBall.pos[1]=y0+(caseY+1)*cote-panBall.diametre;
						break;
					case 4:
						panBall.vit[1]=-112*cote/18;
						panBall.pos[1]=y0+(caseY+1)*cote-panBall.diametre;
						break;
					case 5:
						panBall.vit[1]=-112*cote/18;
						panBall.pos[1]=y0+(caseY+1)*cote-panBall.diametre;
						break;
					case 6:
						panBall.vit[1]=-112*cote/18;
						panBall.pos[1]=y0+(caseY+1)*cote-panBall.diametre;
						break;
					case 7:
						panBall.vit[1]=-112*cote/18;
						panBall.pos[1]=y0+(caseY+1)*cote-panBall.diametre;
						break;
					case 8:
						panBall.vit[0] = 0;
						panBall.vit[1] = 0;
						panBall.pos[0] = xr;
						panBall.pos[1] = yr;
						resetStars();
						try{
							Thread.sleep(200);
						}catch(InterruptedException e) {}
						break;
					case 9:
						map[coordCase[1]][coordCase[0]]=10;
						gagne();
						break;
					default:
						break;						
				}
				break;
			case 3:
				switch(map[coordCase[1]][coordCase[0]]) {
					case 1:
						panBall.pos[0]=x0+(caseX+1)*cote-panBall.diametre-10;
						break;
					case 2:
						panBall.pos[0]=x0+(caseX+1)*cote-panBall.diametre-10;
						break;
					case 3:
						panBall.pos[0]=x0+(caseX+1)*cote-panBall.diametre-10;
						break;
					case 4:
						panBall.pos[0]=x0+(caseX+1)*cote-panBall.diametre-10;
						break;
					case 5:
						panBall.pos[0]=x0+(caseX+1)*cote-panBall.diametre-10;
						break;
					case 6:
						panBall.pos[0]=x0+(caseX+1)*cote-panBall.diametre-10;
						break;
					case 7:
						panBall.pos[0]=x0+(caseX+1)*cote-panBall.diametre-10;
						break;
					case 8:
						panBall.vit[0] = 0;
						panBall.vit[1] = 0;
						panBall.pos[0] = xr;
						panBall.pos[1] = yr;
						resetStars();
						try{
							Thread.sleep(200);
						}catch(InterruptedException e) {}
						break;
					case 9:
						map[coordCase[1]][coordCase[0]]=10;
						gagne();
						break;
					default:
						break;						
				}
				break;
			default:
				break;
		}
	}
	
	public void gagne(){
		boolean gg = true;
	
		
		for (int i = 0; i<15;i++) {
			for (int j = 0; j<25 ; j++) {
				if (map[i][j]==9) {
					gg=false;
				}
			}
		}
		
		
		if ((gg)&&(this.nI<11)) {
			nI++;
			try{
				bR= new BufferedReader(new FileReader(new File("Cartes","Carte"+nI+".txt")));
				for (int p = 0 ; p<15; p++) {
					String str = bR.readLine();
					map[p] = analyseStr(str);
				}
				posI = analyseStr(bR.readLine());
			}catch (IOException e){
				System.out.println("Error 404 : Carte n°"+nI+" Not Found !");
				try {
					Robot robot = new Robot();
					robot.keyPress(KeyEvent.VK_ESCAPE);
					robot.keyRelease(KeyEvent.VK_ESCAPE);
				} catch (AWTException awtE) {}
			}
			xr = ((this.getWidth()-25*cote)/2+posI[0]*cote);
			yr = ((this.getHeight()-15*cote)/2+posI[1]*cote);
			
			pB.pos[0] = xr;
			pB.pos[1] = yr;
			pB.vit[0] = 0;
			pB.vit[1] = 0;
		} else if (this.nI==11) {
			try {
				Robot robot = new Robot();
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);
			} catch (AWTException e) {}
		}
	}
	
	private int[] analyseStr(String s) {
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
	
	private void resetStars() {
		for (int i = 0; i<25 ; i++) {
			for (int j = 0; j<15; j++) {
				if(map[j][i]==10) {
					map[j][i] = 9;
				}
			}
		}
	}
}