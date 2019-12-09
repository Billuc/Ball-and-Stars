import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class MessageDefil extends JLabel {
	public String message;
	int curseur;
	java.util.Timer time = new java.util.Timer();
	
	public MessageDefil() {
		this("    ");
	}
	
	public MessageDefil(String s) {
		this.message = s;
		curseur = this.message.length();
		time.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				defil();
			}
		},200,200);
	}
	
	public void defil() {
		this.setText(this.message);
		this.message = this.message.substring(1,this.message.length())+this.message.charAt(0);
		if (this.message.substring(this.message.length()-4,this.message.length()).equals("    ")) {
			this.curseur = this.message.length();
		} else if (curseur>4){
			curseur--;
		}
	}
	
	public void ajout(String s) {
		this.message = this.message.substring(0,curseur)+s+this.message.substring(curseur,this.message.length());
	}
}