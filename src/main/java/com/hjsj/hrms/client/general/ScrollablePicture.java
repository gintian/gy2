/**
 * 
 */
package com.hjsj.hrms.client.general;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-11:下午01:22:50</p> 
 *@author cmq
 *@version 4.0
 */
public class ScrollablePicture extends JLabel implements Scrollable,
		MouseMotionListener {

    private int maxUnitIncrement = 1;
    private boolean missingPicture = false;
   
    public ScrollablePicture(ImageIcon i, int m) {
        super(i);
        if (i == null) {
            missingPicture = true;
            setText("No picture found.");
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            setBackground(Color.white);
        }
        maxUnitIncrement = m;
        //Let the user scroll by dragging to outside the window.
        setAutoscrolls(true); //enable synthetic drag events
        addMouseMotionListener(this); //handle mouse drags
    }    
    
	@Override
    public Dimension getPreferredSize() {
        if (missingPicture) {
            return new Dimension(320, 480);
        } else {
            return super.getPreferredSize();
        }
	}
	
	@Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
	}

	@Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
	}

	@Override
    public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
    public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation, int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                             (currentPosition / maxUnitIncrement)
                              * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                   * maxUnitIncrement
                   - currentPosition;
        }

	}

	@Override
    public void mouseDragged(MouseEvent e) {
        //The user is dragging us, so scroll!
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
	}

	@Override
    public void mouseMoved(MouseEvent e) {

	}

    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }	
}
