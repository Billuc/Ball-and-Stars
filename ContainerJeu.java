import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;
 
public class ContainerJeu extends JPanel{
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
	Image[] imgs = new Image[12];
	boolean fly = false;
	boolean paint = false;
	
	public ContainerJeu (int x, int y, int cote, int nCarte) {
		this.cote = cote;
		this.setPreferredSize(new Dimension(x,y));
		this.setMinimumSize(new Dimension(x,y));
		this.nI = nCarte;
		
		
		try{
			bR= new BufferedReader(new FileReader(new File("Cartes","Carte"+nI+".txt")));
			for (int i = 0 ; i<15; i++) {
				map[i] = analyseStr(bR.readLine());
			}
			posI = analyseStr(bR.readLine());
		}catch (IOException e){
			System.out.println("Error 404 : Carte n°"+nCarte+" Not Found !\n");
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
				System.out.println("Error 404 : Image n°"+c+" Not Found !\n");
			}
			imgs[c] = img;
		}
		
		
		xr = ((x-25*cote)/2+(posI[0]+1/4.)*cote);
		yr = ((y-15*cote)/2+(posI[1]+1/4.)*cote);
		
		pB = new PanelBall();
		pB.setSize(x,y);
		
		
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0,false),"left");
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0,false),"right");
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0,true),"leftR");
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0,true),"rightR");
		this.getActionMap().put("left",new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				pB.vit[0]=-8*cote/2;
				pB.acc[0]=0;
			}
		});
		this.getActionMap().put("right", new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				pB.vit[0]=8*cote/2;
				pB.acc[0]=0;
			}
		});
		this.getActionMap().put("leftR", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pB.acc[0]=-4*pB.vit[0];
			}
		});
		this.getActionMap().put("rightR", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pB.acc[0]=-4*pB.vit[0];
			}
		});
	}
	
	class PanelBall extends JPanel{
		public double[] acc = new double[2];
		public double[] vit = new double[2];
		public double[] pos = new double[2];
		int x = -100;
		int y = -100;
		int diametre;
		private Image img = null;
		double grav = 160*cote/9;
		
		public PanelBall(){
			acc[1] = grav;
			pos[0] = xr;
			pos[1] = yr;
			this.diametre = (int)(cote/2);
			this.setOpaque(false);
			try {
				img = ImageIO.read(new File("Images","balle.png"));
			} catch (IOException e) {
				System.out.println("Error 404 : Balle not found !\n");
			}
		}
		
		protected void paintComponent(Graphics g){
			g.setColor(new Color(192,238,250));
			g.fillOval(x-1,y-1,this.diametre+1,this.diametre+1);
			g.drawImage(img, (int)pos[0], (int)pos[1], this.diametre, this.diametre, this);
			try{
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			anim();
		}
		
		
		private void anim() {
			this.x = (int)(this.pos[0]);
			this.y = (int)(this.pos[1]);
			if ((this.pos[0]+this.vit[0]/100>(this.getWidth()-25*cote)/2)&&(this.pos[0]+this.vit[0]/100+diametre<(this.getWidth()+25*cote)/2)) {
				this.pos[0]+=this.vit[0]/100;
			}
			this.pos[1]+=this.vit[1]/100;
			if (this.pos[1]+diametre>(this.getHeight()+15*cote)/2){
				this.vit[0] = 0;
				this.vit[1] = 0;
				this.pos[0] = xr;
				this.pos[1] = yr;
				reset();
			} else {
				this.vit[1]+=this.acc[1]/100;
					
				if (Math.abs(this.vit[0])<=0.5) {
					this.acc[0]=0;
					this.vit[0]=0;
				}else {
					this.vit[0]+=this.acc[0]/100;
				}
				detekColl(this);
			}
			this.repaint();
		}
	}
	
	protected void paintComponent(Graphics g){
		
		int x0 = (int)((this.getWidth()-25*cote)/2);
		int y0 = (int)((this.getHeight()-15*cote)/2);
		
		if (paint == false) {
			g.setColor(new Color(100,100,100));
			g.fillRect(0,0,this.getWidth(),this.getHeight());
			
			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < 25; j++) {
					g.drawImage(imgs[map[i][j]], x0 + cote*j, y0 + cote*i,cote, cote, this);
				}
			}
			
			g.setFont(new Font("Trebuchet MS",0,this.cote));
			g.setColor(Color.RED);
			g.drawString(String.valueOf(nI+1),(int)x0/4,y0+cote);
			
			paint = true;
		}
		g.setColor(Color.BLACK);
		
		if (nI==0) {
			g.drawString("Utilisez les fleches [<-] et [->] pour deplacer la balle",x0+cote,y0+cote);
			g.drawString("et attraper les etoiles !", x0+5*cote,y0+2*cote);
		}
		if (nI==1) {
			g.drawString("Attention ! Evitez la case eclair !", x0+3*cote,y0+2*cote);
		}
		if (nI==3) {
			g.drawString("Voici une nouvelle case ! Essayez-la !", x0+3*cote,y0+2*cote);
		}
	}
	
	private void detekColl(PanelBall panBall) {
		double xc = panBall.pos[0]+panBall.diametre/2;
		double yc = panBall.pos[1]+panBall.diametre/2;
		
		int x0 = (int)((this.getWidth()-25*cote)/2);
		int y0 = (int)((this.getHeight()-15*cote)/2);
		
		int caseX = (int)((xc-x0)/cote);
		int caseY = (int)((yc-y0)/cote);
		
		int[] coord = new int[2];
		
		boolean coll = false;
		
		for (int dx = -1; dx<=1; dx+=2) {
			if ((caseX+dx>=0)&&(caseX+dx<=24)){
				coord[0] = caseX+dx;
				coord[1] = caseY;
				if ((map[caseY][caseX+dx]==8)&&(Math.abs(x0+(caseX+(dx+1)/2)*cote-xc)<panBall.diametre/4.)){
					collision(panBall, coord , dx+2);
				} else if ((Math.abs(x0+(caseX+(dx+1)/2)*cote-xc)<panBall.diametre/2)&&(map[caseY][caseX+dx]!=8)) {
					collision(panBall, coord , dx+2);
				}
				if ((map[caseY][caseX+dx]!=0)&&(map[caseY][caseX+dx]!=10)&&(map[caseY][caseX+dx]!=11)&&(map[caseY][caseX+dx]!=9)) {
					coll = true;
				}
			}
		}
		for (int dy = -1; dy<=1 ; dy+=2) {
			if ((caseY+dy>=0)&&(caseY+dy<=14)) {
				coord[0] = caseX;
				coord[1] = caseY+dy;
				if ((map[caseY+dy][caseX]==8)&&(Math.abs(y0+(caseY+(dy+1)/2)*cote-yc)<panBall.diametre/4.)) {
					collision(panBall, coord , dy+1);
				} else if ((Math.abs(y0+(caseY+(dy+1)/2)*cote-yc)<panBall.diametre/2)&&(map[caseY+dy][caseX]!=8)) {
					collision(panBall, coord , dy+1);
				}
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
					if (((xc-xf)*(xc-xf)+(yc-yf)*(yc-yf)<=panBall.diametre*panBall.diametre/4)&&(caseX+ab>=0)&&(caseX+ab<=24)&&(caseY+or>=0)&&(caseY+or<=14)&&(map[caseY+or][caseX+ab]!=8)) {
						dx=ab;
						dy=or;
						xCoin = xf;
						yCoin = yf;
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
	
	private void collision(PanelBall panBall, int[] coordCase, int noColl) {
		
		int x0 = (int)((this.getWidth()-25*cote)/2);
		int y0 = (int)((this.getHeight()-15*cote)/2);
		
		switch(noColl) {
			//collision en haut
			case 0:
				switch(map[coordCase[1]][coordCase[0]]) {
					case 1: case 2: case 3: case 4: case 5: case 6: case 7:
						panBall.vit[1]=0;
						panBall.pos[1]=y0+(coordCase[1]+1)*cote;
						fly = false;
						break;
					case 8:
						panBall.acc[0] = 0;
						panBall.vit[0] = 0;
						panBall.vit[1] = 0;
						panBall.pos[0] = xr;
						panBall.pos[1] = yr;
						reset();
						try{
							Thread.sleep(200);
						}catch(InterruptedException e) {}
						fly = false;
						break;
					case 9:
						map[coordCase[1]][coordCase[0]]=10;
						paint = false;
						gagne();
						break;
					default:
						break;						
				}
				break;
			//collision a gauche
			case 1:
				switch(map[coordCase[1]][coordCase[0]]) {
					case 1: case 2: case 3: case 4: case 5: case 6: case 7:
						panBall.pos[0]=x0+(coordCase[0]+1)*cote+1;
						panBall.vit[0]= cote;
						panBall.acc[0] = -4*cote;
						fly = false;
						break;
					case 8:
						panBall.acc[0] = 0;
						panBall.vit[0] = 0;
						panBall.vit[1] = 0;
						panBall.pos[0] = xr;
						panBall.pos[1] = yr;
						reset();
						try{
							Thread.sleep(200);
						}catch(InterruptedException e) {}
						fly = false;
						break;
					case 9:
						map[coordCase[1]][coordCase[0]]=10;
						paint = false;
						gagne();
						break;
					default:
						break;						
				}
				break;
			//collision en bas
			case 2:
				switch(map[coordCase[1]][coordCase[0]]) {
					case 1: case 2: 
						if (fly) {
							panBall.vit[0]=0;
						}
						panBall.vit[1]=-20*cote/3;
						panBall.pos[1]=y0+(coordCase[1])*cote-panBall.diametre;
						fly = false;
						break;
					case 3:
						if (fly) {
							panBall.vit[0]=0;
						}
						panBall.vit[1]=-Math.sqrt(panBall.grav*2*9/(4*cote))*cote;
						panBall.pos[1]=y0+(coordCase[1])*cote-panBall.diametre;
						fly = false;
						break;
					case 4:
						if (fly) {
							panBall.vit[0]=0;
						}
						panBall.vit[1]=-Math.sqrt(panBall.grav*2*2/(4*cote))*cote+cote/9;
						panBall.pos[1]=y0+(coordCase[1])*cote-panBall.diametre;
						fly = false;
						break;
					case 5:
						if (fly) {
							panBall.vit[0]=0;
						}
						map[coordCase[1]][coordCase[0]] = 11;
						panBall.vit[1]=-Math.sqrt(panBall.grav*2*6/(4*cote))*cote;
						panBall.pos[1]=y0+(coordCase[1])*cote-panBall.diametre;
						fly = false;
						paint = false;
						break;
					case 6:
						panBall.acc[0]=0;
						panBall.vit[1]=0;
						panBall.vit[0] = -10*cote;
						panBall.pos[1]=y0+(coordCase[1]+1/4)*cote;
						panBall.pos[0] = x0+(coordCase[0])*cote-panBall.diametre-1;
						fly = true;
						break;
					case 7:
						panBall.acc[0]=0;
						panBall.vit[1]=0;
						panBall.vit[0] = 10*cote;
						panBall.pos[1]=y0+(coordCase[1]+1/4)*cote;
						panBall.pos[0] = x0+(coordCase[0]+1)*cote;
						fly = true;
						break;
					case 8:
						panBall.acc[0] = 0;
						panBall.vit[0] = 0;
						panBall.vit[1] = 0;
						panBall.pos[0] = xr;
						panBall.pos[1] = yr;
						reset();
						try{
							Thread.sleep(200);
						}catch(InterruptedException e) {}
						fly = false;
						break;
					case 9:
						map[coordCase[1]][coordCase[0]]=10;
						paint = false;
						gagne();
						break;
					default:
						break;						
				}
				break;
			//collision a droite
			case 3:
				switch(map[coordCase[1]][coordCase[0]]) {
					case 1: case 2: case 3: case 4: case 5: case 6: case 7:
						panBall.pos[0]=x0+(coordCase[0])*cote-panBall.diametre-1;
						panBall.vit[0]= -cote;
						panBall.acc[0] = 4*cote;
						fly = false;
						break;
					case 8:
						panBall.acc[0] = 0;
						panBall.vit[0] = 0;
						panBall.vit[1] = 0;
						panBall.pos[0] = xr;
						panBall.pos[1] = yr;
						reset();
						try{
							Thread.sleep(200);
						}catch(InterruptedException e) {}
						fly = false;
						break;
					case 9:
						map[coordCase[1]][coordCase[0]]=10;
						paint = false;
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
	
	private void gagne(){
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
				System.out.println("Error 404 : Carte n°"+nI+" Not Found !\n");
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
			xr = ((this.getWidth()-25*cote)/2+(posI[0]+1/4.)*cote);
			yr = ((this.getHeight()-15*cote)/2+(posI[1]+1/4.)*cote);
			
			pB.pos[0] = xr;
			pB.pos[1] = yr;
			pB.vit[0] = 0;
			pB.vit[1] = 0;
			pB.acc[0] = 0;
			pB.x=-100;
			pB.y=-100;
			paint = false;
		} else if ((gg)&&(this.nI==11)) {
			try {
				Robot robot = new Robot();
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);
			} catch (AWTException e) {}
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
	
	private void reset() {
		for (int i = 0; i<25 ; i++) {
			for (int j = 0; j<15; j++) {
				if(map[j][i]==10) {
					map[j][i] = 9;
				}
				if(map[j][i]==11) {
					map[j][i] = 5;
				}
			}
		}
		paint = false;
		pB.x=-100;
		pB.y=-100;
	}
}