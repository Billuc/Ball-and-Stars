import javax.swing.JButton;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class Bouton extends JButton{
	int number;
	private Image img = null;
	
	public Bouton(int no) {
		number = no;
		try {
			img = ImageIO.read(new File("Images","Bouton"+number+".png"));
		} catch (IOException e) {
			System.out.println("Error 404 : Bouton "+ number + " not found !");
		}
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
	}
}