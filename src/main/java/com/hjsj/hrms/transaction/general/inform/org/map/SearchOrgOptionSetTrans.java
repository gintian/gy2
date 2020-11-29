/*
 * Created on 2006-5-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchOrgOptionSetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//ORG_MAPOPTION    /*机构图常量*/
		String report_relations = (String)this.getFormHM().get("report_relations");	
		this.getFormHM().put("report_relations",report_relations);
		List dblist=this.userView.getPrivDbList();
		String dbnames="Usr";
		if(dblist!=null && !dblist.isEmpty())
			dbnames=(String)dblist.get(0);
		ArrayList parametervaluelist=new SetOrgOptionParameter().ReadOutParameterXml("ORG_MAPOPTION",this.getFrameconn(),this.userView,dbnames);
		//System.out.println(parametervaluelist.size()+"&&&&&&&&&&&&&&&&&");
		if(parametervaluelist.isEmpty() || parametervaluelist.size()==0)
		{
			this.getFormHM().put("cellletteralignleft","align-left");
			this.getFormHM().put("cellletteralignright","noalign-right");
			this.getFormHM().put("cellletteraligncenter","noalign-center");
			this.getFormHM().put("celllettervaligncenter","valign-center");
			this.getFormHM().put("cellletterfitsize","false");
			this.getFormHM().put("cellletterfitline","true");
			this.getFormHM().put("fontfamily","song");
			this.getFormHM().put("fontstyle","general");
			this.getFormHM().put("fontsize","12");
			this.getFormHM().put("fontcolor","#000000");
			this.getFormHM().put("cellhspacewidth","10");
			this.getFormHM().put("cellvspacewidth","10");
			this.getFormHM().put("celllinestrokewidth","1");
			this.getFormHM().put("cellshape","rect");
			this.getFormHM().put("cellwidth","80");
			this.getFormHM().put("cellheight","60");
			this.getFormHM().put("isshowpersonconut","false");
			this.getFormHM().put("isshowpersonname","false");
			this.getFormHM().put("namesinglecell","false");			
			this.getFormHM().put("graph3d","false");
			this.getFormHM().put("graphaspect","true");
			this.getFormHM().put("cellaspect","false");
			this.getFormHM().put("cellcolor","#FFFFA6");
			this.getFormHM().put("dbnames",dbnames);
            this.getFormHM().put("isshowposname", "false");
            this.getFormHM().put("isshowdeptname", "false");
            this.getFormHM().put("deptlevel", "");  
            this.getFormHM().put("unitlevel", "");
            this.getFormHM().put("isshoworgconut", "false");
            this.getFormHM().put("linecolor", "#000000");
            
		}
		else
		{
			boolean ispos=false;
			boolean isdept=false;
			for(int i=0;i<parametervaluelist.size();i++)
			{
				LabelValueView labelvalue=(LabelValueView)parametervaluelist.get(i);
				/*if(labelvalue.getLabel().equals("isshowpersonconut")||labelvalue.getLabel().equals("isshowpersonname")||labelvalue.getLabel().equals("unitlevel")||labelvalue.getLabel().equals("isshowdeptname")||labelvalue.getLabel().equals("deptlevel")){
					System.out.println(labelvalue.getLabel()+":"+labelvalue.getValue());
				}*/
				this.getFormHM().put(labelvalue.getLabel(),labelvalue.getValue());
				if(labelvalue.getLabel()!=null&& "isshowposname".equalsIgnoreCase(labelvalue.getLabel()))
					ispos=true;
				if(labelvalue.getLabel()!=null&& "isshowdeptname".equalsIgnoreCase(labelvalue.getLabel()))
					isdept=true;
				//System.out.println(labelvalue.getLabel() + labelvalue.getValue());
			}
			if(!ispos)
				this.getFormHM().put("isshowposname", "false");
			if(!isdept)
				this.getFormHM().put("isshowdeptname", "false");
		}
		
		
		this.getFormHM().put("catalog_id", (String)this.getFormHM().get("catalog_id"));
		this.getFormHM().put("code", (String)this.getFormHM().get("code"));
		this.getFormHM().put("kind", (String)this.getFormHM().get("kind"));
	    this.getFormHM().put("orgtype", (String)this.getFormHM().get("orgtype"));
		/*String backdate=(String)this.getFormHM().get("backdate");
		if(backdate==null||backdate.length()<=0)
			backdate=DateUtils.format(new Date(), "yyyy-MM-dd");
        this.getFormHM().put("backdate", backdate);
	    */
	}

}
