/**
 * 
 */
package com.hjsj.hrms.client.card;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * @author chenmengqing
 *
 */
public class CardView extends JComponent implements Observer {

	/**
	 * 
	 */
	public CardView() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public void update(Observable o, Object arg) {
		System.out.println("===>"+arg);
	}

	@Override
    public void paint(Graphics g) {
		Graphics2D g2D=(Graphics2D)g;
		g2D.setPaint(Color.RED);
		g2D.draw3DRect(10, 10, 200, 300, true);
		g2D.drawString("hello world!", 60,100);
		
	}

	
}
