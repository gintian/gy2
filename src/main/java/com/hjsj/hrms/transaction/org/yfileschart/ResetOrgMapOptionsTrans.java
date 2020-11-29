package com.hjsj.hrms.transaction.org.yfileschart;

import com.hjsj.hrms.businessobject.org.yfileschart.MapThemeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Iterator;

public class ResetOrgMapOptionsTrans extends IBusiness{

	public void execute() throws GeneralException {
		String maptheme = this.getFormHM().get("maptheme").toString(); 
		
		HashMap optionMap = MapThemeBo.getThemeOptions(new HashMap(), Integer.parseInt(maptheme));
		Iterator ite = optionMap.keySet().iterator();
		
		if(this.getFormHM().containsKey("isPersonReport")){
			//因人员汇报关系参数名称和机构图参数名称有的不一样，转一下
			HashMap oName2pName = new HashMap();
			oName2pName.put("graphaspect", "graphaspect");
			oName2pName.put("isshowshadow", "isshowshadow");
			oName2pName.put("cellcolor", "bgColor");
			oName2pName.put("transitcolor", "transitcolor");
			oName2pName.put("borderwidth", "border_width");
			oName2pName.put("bordercolor", "border_color");
			oName2pName.put("linewidth", "linewidth");
			oName2pName.put("linecolor", "linecolor");
			oName2pName.put("cellhspacewidth", "lr_spacing");
			oName2pName.put("cellvspacewidth", "tb_spacing");
			oName2pName.put("fontfamily", "fontName");
			oName2pName.put("fontsize", "fontSize");
			oName2pName.put("fontstyle", "fontstyle");
			oName2pName.put("fontcolor", "fontcolor");
			
			while(ite.hasNext()){
		    	  String  key = (String)ite.next();
		    	  this.formHM.put(oName2pName.get(key), optionMap.get(key));
		    }
			return;
		}
		
	    while(ite.hasNext()){
	    	String  key = (String)ite.next();
	    	this.formHM.put(key, optionMap.get(key));
	    }
	}
}
