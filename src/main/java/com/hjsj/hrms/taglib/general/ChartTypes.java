package com.hjsj.hrms.taglib.general;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Title:ChartTypes</p>
 * <p>Description:图形类型</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2005:1:22:04 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ChartTypes {

    /** All type strings in an array */
    public static final String[] typeNames =    {
        "area",
        "areaxy",
        "horizontalbar",
        "horizontalbar3d",
        "line",
        "pie",
        "scatter",
        "stackedhorizontalbar",
        "stackedverticalbar",
        "stackedverticalbar3d",
        "timeseries",
        "verticalbar",
        "verticalbar3d",
        "xy",
        "candlestick",
        "highlow",
        "gantt",
        "wind",
        "signal",
        "verticalxybar",
        "pie3d",
        "overlaidxy",
        "overlaidcategory",
        "combinedxy",
        "meter",
        "stackedarea",
        "bubble"
    };

    /**
     * The whole typeNames array inside of a list.
     * @see #typeNames
     */
    public static final List typeList = Arrays.asList(typeNames);

    private ChartTypes() {
    }
}
