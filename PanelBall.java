import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Dimension;

import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;
import javax.swing.JComponent;

public class PanelBall extends JPanel{
	public double[] acc = new double[2];
	public double[] vit = new double[2];
	public double[] pos = new double[2];
	int x = 0;
	int y = 0;
	int cote;
	int diametre;
	
	public PanelBall(double x0, double y0, int cote){
		acc[1] = 100;
		pos[0] = x0;
		pos[1] = y0;
		this.cote = cote;
		this.diametre = (int)(cote*3/4);
		this.setOpaque(false);
	}
	
	protected void paintComponent(Graphics g){
		g.setColor(new Color(0,0,0,0));
		g.fillRect(x,y,diametre+2,diametre+2);
		g.setColor(Color.YELLOW);
		g.fillOval((int)pos[0],(int)pos[1],diametre,diametre);
		try{
			Thread.sleep(20);
		} catch (InterruptedException e) {}
		anim();
	}
	
	
	public void anim() {
		this.x = (int)(this.pos[0]-(diametre+1)/2);
		this.y = (int)(this.pos[1]-(diametre+1)/2);
		if ((this.pos[0]+this.vit[0]/50>(this.getWidth()-25*cote)/2)&&(this.pos[0]+this.vit[0]/50+diametre<(this.getWidth()+25*cote)/2)) {
			this.pos[0]+=this.vit[0]/50;
		}
		if ((this.pos[1]+this.vit[1]/50>(this.getHeight()-15*4*cote/3.)/2)&&(this.pos[1]+this.vit[1]/50+diametre<(this.getHeight()+15*cote)/2)) {
			this.pos[1]+=this.vit[1]/50;
		}
		this.vit[0]+=this.acc[0]/50;
		this.vit[1]+=this.acc[1]/50;
		this.repaint();
	}
}