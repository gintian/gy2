package com.hjsj.hrms.taglib.general;

import java.awt.*;
/**
 * <p>Title:ColorHelper</p>
 * <p>Description:色值转换成RGB</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2005:12:11:50 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ColorHelper {

    /**
     * 
     */
    public ColorHelper() {
        super();
        // TODO Auto-generated constructor stub
    }
    public static final Color getColor(String hexString) {
        try {
             if (hexString == null || hexString.length() < 7) {
                return Color.black;
            }
            final int red = Integer.parseInt(hexString.substring(1, 3), 16);
            final int green = Integer.parseInt(hexString.substring(3, 5), 16);
            final int blue = Integer.parseInt(hexString.substring(5, 7), 16);
            int alpha = 0;
            if (hexString.length() > 8) {
                alpha = Integer.parseInt(hexString.substring(7, 9), 16);
            }
            if (alpha > 0) {
                return new Color(red, green, blue, alpha);
            } else {
                return new Color(red, green, blue);
            }
        } catch (NumberFormatException nfe) {
            return Color.black;
        }
    }
}
