package com.hjsj.hrms.transaction.pos.yfilesposmap;

import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePosMapOptionsTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList nodenamevaluelist=new ArrayList();
		HashMap hm = this.getFormHM();
		LabelValueView labelvalue0=new LabelValueView((String)hm.get("graphaspect")!=null?(String)hm.get("graphaspect"):"true","graphaspect");
		LabelValueView labelvalue1=new LabelValueView((String)hm.get("isshowshadow")!=null?(String)hm.get("isshowshadow"):"false","isshowshadow");
		LabelValueView labelvalue2=new LabelValueView((String)hm.get("cellcolor")!=null?PubFunc.keyWord_reback((String)hm.get("cellcolor")):"#39C9F3","cellcolor");
		LabelValueView labelvalue3=new LabelValueView((String)hm.get("transitcolor")!=null?PubFunc.keyWord_reback((String)hm.get("transitcolor")):"#09A9ED","transitcolor");
		LabelValueView labelvalue6=new LabelValueView((String)hm.get("borderwidth")!=null?(String)hm.get("borderwidth"):"1","borderwidth");
		LabelValueView labelvalue7=new LabelValueView((String)hm.get("bordercolor")!=null?PubFunc.keyWord_reback((String)hm.get("bordercolor")):"","bordercolor");
		LabelValueView labelvalue8=new LabelValueView((String)hm.get("linewidth")!=null?(String)hm.get("linewidth"):"1","linewidth");
		LabelValueView labelvalue9=new LabelValueView((String)hm.get("linecolor")!=null?PubFunc.keyWord_reback((String)hm.get("linecolor")):"#000000","linecolor");
		LabelValueView labelvalue10=new LabelValueView((String)hm.get("cellhspacewidth")!=null?(String)hm.get("cellhspacewidth"):"20","cellhspacewidth");
		LabelValueView labelvalue11=new LabelValueView((String)hm.get("cellvspacewidth")!=null?(String)hm.get("cellvspacewidth"):"20","cellvspacewidth");
		LabelValueView labelvalue12=new LabelValueView((String)hm.get("fontfamily")!=null?(String)hm.get("fontfamily"):"song","fontfamily");
		LabelValueView labelvalue13=new LabelValueView((String)hm.get("fontstyle")!=null?(String)hm.get("fontstyle"):"general","fontstyle");
		LabelValueView labelvalue14=new LabelValueView((String)hm.get("fontsize")!=null?(String)hm.get("fontsize"):"12","fontsize");
		LabelValueView labelvalue15=new LabelValueView((String)hm.get("fontcolor")!=null?PubFunc.keyWord_reback((String)hm.get("fontcolor")):"#000000","fontcolor");
		LabelValueView labelvalue16=new LabelValueView((String)hm.get("isshowposup")!=null?(String)hm.get("isshowposup"):"false","isshowposup");
		LabelValueView labelvalue23=new LabelValueView((String)this.getFormHM().get("maptheme")!=null?(String)this.getFormHM().get("maptheme"):"1","maptheme");
		nodenamevaluelist.add(labelvalue0);
       	nodenamevaluelist.add(labelvalue1);
       	nodenamevaluelist.add(labelvalue2);
       	nodenamevaluelist.add(labelvalue3);
       	nodenamevaluelist.add(labelvalue6);
       	nodenamevaluelist.add(labelvalue7);
       	nodenamevaluelist.add(labelvalue8);
       	nodenamevaluelist.add(labelvalue9);
       	nodenamevaluelist.add(labelvalue10);
       	nodenamevaluelist.add(labelvalue11);
       	nodenamevaluelist.add(labelvalue12);
       	nodenamevaluelist.add(labelvalue13);
       	nodenamevaluelist.add(labelvalue14);
       	nodenamevaluelist.add(labelvalue15);
       	nodenamevaluelist.add(labelvalue16);
       	nodenamevaluelist.add(labelvalue23);
       	new SetOrgOptionParameter().WriteOutParameterXml("POS_MAPOPTION",this.getFrameconn(),nodenamevaluelist);
	}

}
