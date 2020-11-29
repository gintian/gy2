package com.hjsj.hrms.transaction.general.template.historydata;

import com.hjsj.hrms.businessobject.general.template.HistoryDataBo;
import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time: 2010 05 25 11:39:04 AM</p> 
 *@author xieguiquan
 *@version 5.0
 */
public class SearchHistorydataTrans extends IBusiness { 

	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hmMap=(HashMap)this.getFormHM().get("requestPamaHM");
		String tabid = (String)hmMap.get("tabid");
		hmMap.remove("tabid");
		if(tabid==null|| "".equals(tabid)){
			tabid=(String)this.getFormHM().get("tabid");
		}else if("laodonghetong".equals(tabid)){
			tabid=(String)this.getFormHM().get("tabidtemp");
			this.getFormHM().put("returnback", "returnback");
		}
		else{
			this.getFormHM().put("tabid", tabid);
		}
		if(tabid==null|| "".equals(tabid))
			throw new GeneralException("没有模板权限！");
//		HttpSession session2 = (HttpSession)this.getFormHM().get("session2");
//		if(session2!=null){
			
//			TemplateListForm templateListForm=(TemplateListForm)session2.getAttribute("templateListForm");
//			if(templateListForm!=null){
				
				HistoryDataBo historybo = new HistoryDataBo(tabid,this.getFrameconn(),this.userView);
				//自动维护template_archive
				historybo.synctemplate_archiveField("template_archive");
				TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
				String condition = this.getFormHM().get("condition")==null?"":(String)this.getFormHM().get("condition");
				bo.setClass_type(1);
				ArrayList celllist = bo.getAllCell();
			ArrayList headSetList =	historybo.getAllCells(celllist);
			int operationtype = bo.getOperationtype();
			String _codeid = "";//templateListForm.getCodeid();//单位公用，显示不同单位下的历史数据
			String  returnflag="";//templateListForm.getReturnflag();
			_codeid="";
			returnflag="7";
			HashMap historyMap =	historybo.getHistoryTableData2(headSetList, operationtype, _codeid,condition);
			this.getFormHM().put("headSetList", headSetList);
//			this.getFormHM().put("historylist", historylist);
			this.getFormHM().put("table_name", "template_archive");
			this.getFormHM().put("columns", historyMap.get("columns"));
			this.getFormHM().put("strsql", historyMap.get("sql_str"));
			this.getFormHM().put("orderBy", "");
			this.getFormHM().put("tabid", tabid);
			this.getFormHM().put("display_e0122", historyMap.get("display_e0122"));
	//		this.getFormHM().put("startdate", historybo.getStartDate());
	//		this.getFormHM().put("appDate", historybo.getEndDate());
			this.getFormHM().put("codeid",_codeid);
			this.getFormHM().put("hmuster_sql", historybo.getHmuster_sql());
			this.getFormHM().put("condition",condition);
			this.getFormHM().put("returnflag",returnflag);
	//		}
			
	//	}
		
		
	}

}
