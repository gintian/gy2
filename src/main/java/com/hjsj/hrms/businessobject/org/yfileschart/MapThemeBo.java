package com.hjsj.hrms.businessobject.org.yfileschart;

import java.util.HashMap;

public class MapThemeBo {

	public static final int THEMES_BULE = 0;
	public static final int THEMES_GREEN = 1;
	public static final int THEMES_RED = 2;
	public static final int THEMES_GOLD = 3;
	public static final int THEMES_GRAY = 4;
	
	public static HashMap getThemeOptions(HashMap map,int themes){
		HashMap optionMap = new HashMap(map);
		
		switch(themes){
		  case THEMES_BULE: 
			    optionMap.put("graphaspect", "true"); //垂直 true 水平 false
			    optionMap.put("isshowshadow", "false"); //阴影
				optionMap.put("cellcolor", "#46b5f4"); //
				optionMap.put("transitcolor", "#46b5f4");
				optionMap.put("borderwidth", "1");
				optionMap.put("bordercolor", "#46b5f4");
				optionMap.put("linewidth", "1");
				optionMap.put("linecolor", "#46b5f4");
				optionMap.put("cellhspacewidth", "20");
				optionMap.put("cellvspacewidth", "20");
				optionMap.put("fontfamily", "song");
				optionMap.put("fontstyle", "general");
				optionMap.put("fontsize", "16");
				optionMap.put("fontcolor", "#ffffff");
			   break;
		  case THEMES_GREEN:
			    optionMap.put("graphaspect", "true");
			    optionMap.put("isshowshadow", "false");
				optionMap.put("cellcolor", "#33cc66");
				optionMap.put("transitcolor", "#33cc66");
				optionMap.put("borderwidth", "1");
				optionMap.put("bordercolor", "#33cc66");
				optionMap.put("linewidth", "1");
				optionMap.put("linecolor", "#33cc66");
				optionMap.put("cellhspacewidth", "20");
				optionMap.put("cellvspacewidth", "20");
				optionMap.put("fontfamily", "song");
				optionMap.put("fontstyle", "general");
				optionMap.put("fontsize", "16");
				optionMap.put("fontcolor", "#000000");
			   break;
		  case THEMES_RED:
			    optionMap.put("graphaspect", "true");
			    optionMap.put("isshowshadow", "false");
				optionMap.put("cellcolor", "#ff3300");
				optionMap.put("transitcolor", "#ff3300");
				optionMap.put("borderwidth", "1");
				optionMap.put("bordercolor", "#ff3300");
				optionMap.put("linewidth", "1");
				optionMap.put("linecolor", "#ff3300");
				optionMap.put("cellhspacewidth", "20");
				optionMap.put("cellvspacewidth", "20");
				optionMap.put("fontfamily", "song");
				optionMap.put("fontstyle", "general");
				optionMap.put("fontsize", "16");
				optionMap.put("fontcolor", "#ffffff");
			   break;
		  case THEMES_GOLD:
			    optionMap.put("graphaspect", "true");
			    optionMap.put("isshowshadow", "false");
				optionMap.put("cellcolor", "#ffcc33");
				optionMap.put("transitcolor", "#ffcc33");
				optionMap.put("borderwidth", "1");
				optionMap.put("bordercolor", "#ffcc33");
				optionMap.put("linewidth", "1");
				optionMap.put("linecolor", "#ffcc33");
				optionMap.put("cellhspacewidth", "20");
				optionMap.put("cellvspacewidth", "20");
				optionMap.put("fontfamily", "song");
				optionMap.put("fontstyle", "general");
				optionMap.put("fontsize", "16");
				optionMap.put("fontcolor", "#000000");
			   break;
		  case THEMES_GRAY:
			    optionMap.put("graphaspect", "true");
			    optionMap.put("isshowshadow", "false");
				optionMap.put("cellcolor", "#cccccc");
				optionMap.put("transitcolor", "#cccccc");
				optionMap.put("borderwidth", "1");
				optionMap.put("bordercolor", "#cccccc");
				optionMap.put("linewidth", "1");
				optionMap.put("linecolor", "#000000");
				optionMap.put("cellhspacewidth", "20");
				optionMap.put("cellvspacewidth", "20");
				optionMap.put("fontfamily", "song");
				optionMap.put("fontstyle", "general");
				optionMap.put("fontsize", "16");
				optionMap.put("fontcolor", "#000000");
			   break;
		}
		return optionMap;
	}
}
