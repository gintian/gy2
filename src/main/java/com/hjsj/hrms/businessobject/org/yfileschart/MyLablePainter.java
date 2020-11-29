package com.hjsj.hrms.businessobject.org.yfileschart;

import com.hjsj.hrms.servlet.org.yfileschart.OrgMapServlet;
import org.apache.commons.lang.StringUtils;
import y.geom.OrientedRectangle;
import y.view.AbstractCustomLabelPainter;
import y.view.YLabel;

import javax.swing.*;
import java.awt.*;

public class MyLablePainter extends AbstractCustomLabelPainter{

	private String fontcolor = "";
	
	public MyLablePainter(String fontcolor) {
		this.fontcolor = fontcolor;
	}
	
	@Override
    public void paintContent(YLabel paramYLabel,
                             Graphics2D graphics, double x,
                             double y, double w, double h) {
		
	    if(!paramYLabel.isVisible()) {
			return;
		}
		
		Icon icon = paramYLabel.getIcon();
			
		//Shape shape = new RoundRectangle2D.Double(x, y, w, h, 3.0, 3.0);
		
		//zxj 20140603 去掉缩放按钮的背景填充色
		//graphics.setColor(new Color(Integer.parseInt("cccccc", 16)));
		//graphics.fill(shape);
		BasicStroke ba = new BasicStroke(1);
		graphics.setStroke(ba);
		String color = "ffffff";
		if(StringUtils.isNotEmpty(fontcolor)) {
			color = fontcolor.replace("#", "");
		}
		
		graphics.setColor(new Color(Integer.parseInt(color, 16)));
		if(icon.equals(OrgMapServlet.expandicon)){
		   graphics.drawLine((int)(x+h/2),(int)(y+2), (int)(x+h/2), (int)(y+h-2));
		}
		graphics.drawLine((int)(x+2),(int)(y+h/2), (int)(x+w-2), (int)(y+h/2));
		
	}

	@Override
    public OrientedRectangle getTextBox(YLabel paramYLabel) {
		return paramYLabel.getTextBox();
	}

	@Override
    public OrientedRectangle getIconBox(YLabel paramYLabel) {
		// TODO Auto-generated method stub
		return paramYLabel.getIconBox();
	}
}
