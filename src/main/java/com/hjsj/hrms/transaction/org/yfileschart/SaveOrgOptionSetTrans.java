package com.hjsj.hrms.transaction.org.yfileschart;

import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveOrgOptionSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		try{
			List dblist=this.userView.getPrivDbList();
			String dbnames="Usr";
			if(dblist!=null && !dblist.isEmpty())
				dbnames=(String)dblist.get(0);
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
			LabelValueView labelvalue16=new LabelValueView((String)this.getFormHM().get("dbnames")!=null?(String)this.getFormHM().get("dbnames"):dbnames,"dbnames");
			LabelValueView labelvalue17=new LabelValueView((String)this.getFormHM().get("isshowpersonconut")!=null?(String)this.getFormHM().get("isshowpersonconut"):"false","isshowpersonconut");
			LabelValueView labelvalue18=new LabelValueView((String)this.getFormHM().get("isshowpersonname")!=null?(String)this.getFormHM().get("isshowpersonname"):"false","isshowpersonname");
			LabelValueView labelvalue19=new LabelValueView((String)this.getFormHM().get("isshowphoto")!=null?(String)this.getFormHM().get("isshowphoto"):"false","isshowphoto");
			LabelValueView labelvalue20=new LabelValueView((String)this.getFormHM().get("isshoworgconut")!=null?(String)this.getFormHM().get("isshoworgconut"):"false","isshoworgconut");
			LabelValueView labelvalue21=new LabelValueView((String)this.getFormHM().get("isshowposname")!=null?(String)this.getFormHM().get("isshowposname"):"false","isshowposname");
			LabelValueView labelvalue22=new LabelValueView((String)this.getFormHM().get("isshowdeptname")!=null?(String)this.getFormHM().get("isshowdeptname"):"false","isshowdeptname");
			LabelValueView labelvalue23=new LabelValueView((String)this.getFormHM().get("maptheme")!=null?(String)this.getFormHM().get("maptheme"):"1","maptheme");
			// 是否显示兼职人员、兼职人员颜色 chent 20170516
			LabelValueView labelvalue24=new LabelValueView((String)hm.get("isshowpartjobperson")!=null?(String)hm.get("isshowpartjobperson"):"false","isshowpartjobperson");
			LabelValueView labelvalue25=new LabelValueView((String)hm.get("partjobpersoncolor")!=null?PubFunc.keyWord_reback((String)hm.get("partjobpersoncolor")):"#000000","partjobpersoncolor");
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
	       	nodenamevaluelist.add(labelvalue17);
	       	nodenamevaluelist.add(labelvalue18);
	       	nodenamevaluelist.add(labelvalue19);
	       	nodenamevaluelist.add(labelvalue20);
	       	nodenamevaluelist.add(labelvalue21);
	       	nodenamevaluelist.add(labelvalue22);
	       	nodenamevaluelist.add(labelvalue23);
	       	nodenamevaluelist.add(labelvalue24);
	       	nodenamevaluelist.add(labelvalue25);
	       	new SetOrgOptionParameter().WriteOutParameterXml("ORG_MAPOPTION",this.getFrameconn(),nodenamevaluelist); 
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
