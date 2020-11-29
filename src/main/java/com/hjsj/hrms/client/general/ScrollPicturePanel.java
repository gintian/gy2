/**
 * 
 */
package com.hjsj.hrms.client.general;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 *<p>Title:滚动图形面板</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-11:下午01:33:54</p> 
 *@author cmq
 *@version 4.0
 */
public class ScrollPicturePanel extends JPanel implements ItemListener {

    private Rule columnView;
    private Rule rowView;
    private JToggleButton isMetric;
    private ScrollablePicture picture;
    private ImageIcon david = createImageIcon("/com/hjsj/hrms/client/images/car.jpg");

	public ScrollPicturePanel() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setScrollPicture(david);
	}
	/**
	 * 
	 */
	public void setScrollPicture(ImageIcon icon) {
		//Create the row and column headers.
        setRuleLength(icon);
        //Create the corners.
        JPanel buttonCorner = new JPanel(); //use FlowLayout
        isMetric = new JToggleButton("cm", true);
        isMetric.setFont(new Font("SansSerif", Font.PLAIN, 11));
        isMetric.setMargin(new Insets(2,2,2,2));
        isMetric.addItemListener(this);
        buttonCorner.add(isMetric); 

        //Set up the scroll pane.
        picture = new ScrollablePicture(icon, columnView.getIncrement());
        JScrollPane pictureScrollPane = new JScrollPane(picture);
        pictureScrollPane.setPreferredSize(new Dimension(300, 250));
        pictureScrollPane.setViewportBorder(
                BorderFactory.createLineBorder(Color.black));
        pictureScrollPane.setColumnHeaderView(columnView);
        pictureScrollPane.setRowHeaderView(rowView);

        //Set the corners.
        //In theory, to support internationalization you would change
        //UPPER_LEFT_CORNER to UPPER_LEADING_CORNER,
        //LOWER_LEFT_CORNER to LOWER_LEADING_CORNER, and
        //UPPER_RIGHT_CORNER to UPPER_TRAILING_CORNER.  In practice,
        //bug #4467063 makes that impossible (in 1.4, at least).
        pictureScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                                    buttonCorner);
        pictureScrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER,
                                    new Corner());
        pictureScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                                    new Corner());

        //Put it in this panel.
        add(pictureScrollPane);
        //setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	}
	/**
	 * @param icon
	 */
	private void setRuleLength(ImageIcon icon) {
		columnView = new Rule(Rule.HORIZONTAL, true);
        rowView = new Rule(Rule.VERTICAL, true);

        if (icon != null) {
            columnView.setPreferredWidth(icon.getIconWidth());
            rowView.setPreferredHeight(icon.getIconHeight());
        } else {
            columnView.setPreferredWidth(320);
            rowView.setPreferredHeight(480);
        }
	}
	/**
	 * 测试
	 * @param path
	 * @return
	 */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ScrollPicturePanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	@Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            //Turn it to metric.
            rowView.setIsMetric(true);
            columnView.setIsMetric(true);
        } else {
            //Turn it to inches.
            rowView.setIsMetric(false);
            columnView.setIsMetric(false);
        }
        picture.setMaxUnitIncrement(rowView.getIncrement());
	}
	public ImageIcon getDavid() {
		return david;
	}
	public void setDavid(ImageIcon david) {
		this.david = david;
	}
	
	public void setPicture(ImageIcon imageicon)
	{
		setRuleLength(imageicon);
		this.david.setImage(imageicon.getImage());
		this.picture.validate();
	}
}
