package com.hjsj.hrms.businessobject.org.yfileschart;

import y.view.NodeRealizer;
import y.view.ShinyPlateNodePainter;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MyNodePainter extends ShinyPlateNodePainter{

	public MyNodePainter() {}

	Image image;
	
	

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
    public void paint(NodeRealizer context, Graphics2D gfx) {

      if(!context.isVisible()) {
        return;
      }

      gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintNode(context, gfx, false);
        context.paintText(gfx);
    }

    @Override
    public void paintSloppy(NodeRealizer context, Graphics2D gfx) {
      paint(context, gfx);
    }
    
    @Override
    protected Color createSelectionColor(Color original) {
      // don't modify the fill color here - we are
      // changing the fill color externally upon selection
      return original;
    }

    @Override
    protected void paintNode(NodeRealizer context, Graphics2D graphics, boolean sloppy) {
      Shape shape = createShape(context);
      paintFilledShape(context, graphics, shape);
      paintBorder(context, graphics, shape);
      
      //graphics.drawString("abce", (int)context.getX(), (int)context.getY());
      
    }

    @Override
    protected void backupGraphics(Graphics2D graphics) {}

    @Override
    protected void restoreGraphics(Graphics2D graphics) {}

    /**
     * Fill the shape using a custom gradient color and a semi-transparent effect
     */
    private void paintFilledShape(NodeRealizer context, Graphics2D graphics, Shape shape) {
      double x = context.getX();
      double y = context.getY();
      double width = context.getWidth();
      double height = context.getHeight();

      Color c1 = getFillColor(context, context.isSelected());

      if (c1 != null && !context.isTransparent()) {
        Color c2 = getFillColor2(context, context.isSelected());
        if (c2 != null) {
          graphics.setPaint(
              new GradientPaint((float) (x + 0.5*width), (float) y, c1, (float) (x + 0.5*width), (float) (y+height), c2)
          );
        } else {
          graphics.setColor(c1);
        }
        graphics.fill(shape);
        
//        AffineTransform localAffineTransform = AffineTransform.getTranslateInstance(context.getX(), context.getY());
//        localAffineTransform.scale(context.getWidth() /image.getWidth(null), context.getHeight() / image.getHeight(null));
//        graphics.drawImage(image, localAffineTransform, null);

      }
    }

    private Shape createShape(NodeRealizer context) {
      double x, y, width, height;
      if (context != null) {
        x = context.getX();
        y = context.getY();
        width = context.getWidth();
        height = context.getHeight();
      } else {
        x = 0;
        y = 0;
        width = 1;
        height = 1;
      }
      return new RoundRectangle2D.Double(x, y, width, height, 8.0, 8.0);
    }

    private void paintBorder(NodeRealizer context, Graphics2D graphics, Shape shape) {
      Color lc = getLineColor(context, context.isSelected());
      if (lc != null) {
        graphics.setStroke(context.getLineType());
        graphics.setColor(lc);
        graphics.draw(shape);
      }
    }
}
