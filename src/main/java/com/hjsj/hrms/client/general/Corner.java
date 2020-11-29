/**
 * 
 */
package com.hjsj.hrms.client.general;

import javax.swing.*;
import java.awt.*;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-11:下午01:36:06</p> 
 *@author cmq
 *@version 4.0
 */
public class Corner extends JComponent {
    @Override
    protected void paintComponent(Graphics g) {
        // Fill me with dirty brown/orange.
        g.setColor(new Color(230, 163, 4));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
