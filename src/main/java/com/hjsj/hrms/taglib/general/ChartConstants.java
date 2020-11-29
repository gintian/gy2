package com.hjsj.hrms.taglib.general;

/**
 * <p>Title:ChartConstants</p>
 * <p>Description:图形类型方式</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2005:1:24:01 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public interface ChartConstants {
    int AREA = 0;
    int AREA_XY = 1;
    int HORIZONTAL_BAR = 2;
    int HORIZONTAL_BAR_3D = 3;
    int LINE_VERTICAL = 4;
    int LINE_VERTICAL3D=30;
    int PIE = 5;
    int SCATTER = 6;
    int STACKED_HORIZONTAL_BAR = 7;
    int STACKED_VERTICAL_BAR = 8;
    int STACKED_VERTICAL_BAR_3D = 9;
    int TIME_SERIES = 10;
    int VERTICAL_BAR = 11;
    int VERTICAL_BAR_3D = 12;
    int CATEGORY_BAR=29;  //分组柱状图  VERTICAL
    int CATEGORY_3DBAR=31;  //3d分组柱状图 VERTICAL
    
    int CATEGORY_HORIZONTAL_BAR=32;  //分组柱状图  HORIZONTAL
    int CATEGORY_HORIZONTAL_3DBAR=33;  //3d分组柱状图 HORIZONTAL
    
    int XY = 13;
    int CANDLE_STICK = 14;
    int HIGH_LOW = 15;
    int GANTT = 16;
    int WIND = 17;
    int SIGNAL = 18;
    int VERRTICAL_XY_BAR = 19;
    int PIE_3D = 20;
    int OVERLAY_XY = 21;
    int OVERLAY_CATEGORY = 22;
    int COMBINED_XY = 23;
    int METER = 24;
    int STACKED_AREA = 25;
    int BUBBLE = 26;
    int LINE_HORIZONTAL = 27;
    int LINE_HORIZONTAL3D=28;
    int Bar_Line_CHART=40;//柱状占比图
    /**雷达图*/
    int RADAR=41;
    
    int Circular_Gauge=42;//仪表盘
    int Vertical_Gauge=43;//温度计
    int Circular_Bind=44;//双刻度仪表盘
}
