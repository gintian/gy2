package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class AddGzReportTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap  hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String rsid=(String)hm.get("rsid");
			String gz_model=(String)hm.get("gz_model");
			String rsdtlid="";
			String rsname="";
			String bgroup="0";
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn(),this.getUserView());
			ArrayList itemlist=new ArrayList();
			ArrayList selectedList = new ArrayList();
			ArrayList setList = bo.getSalarySetCommonDataList(gz_model,this.userView);
			String salaryid="";
			if(setList!=null&&setList.size()>0)
			{
				CommonData bean =(CommonData)setList.get(0);
				salaryid=bean.getDataValue();
			}
			String ownerType="1";
			if("new".equals(opt))
			{
				itemlist=bo.getGzProjectList(" UPPER(itemid)<>'-1' ",salaryid,rsid);
			}
			else
			{
				rsdtlid=(String)hm.get("rsdtlid");
				selectedList=bo.getSelectedItemList(rsdtlid);
				StringBuffer buf = new StringBuffer("'-1'");
				if(selectedList!=null&&selectedList.size()>0)
				{
					buf.setLength(0);
					for(int i=0;i<selectedList.size();i++)
					{
						CommonData cd = (CommonData)selectedList.get(i);
						buf.append(" UPPER(itemid)<>");
						buf.append("'"+cd.getDataValue().toUpperCase()+"' and ");
					}
				}
				if(buf==null|| "".equals(buf.toString()))
					buf.append(" UPPER(itemid)<>'-1' and ");
				
				buf.setLength(buf.length()-4);	
				itemlist=bo.getGzProjectList(buf.toString(),salaryid,rsid);
				HashMap map = bo.getName(rsid, rsdtlid);
				rsname=(String)map.get("name");
				bgroup=(String)map.get("bgroup");
				ownerType = (String)map.get("ownertype");
			}
			this.getFormHM().put("reportTabId",rsid);
			this.getFormHM().put("rsname",rsname);
			this.getFormHM().put("itemlist",itemlist);
			this.getFormHM().put("rsdtlid",rsdtlid);
			this.getFormHM().put("bgroup",bgroup);
			this.getFormHM().put("setList",setList);
			this.getFormHM().put("selectedList", selectedList);
			this.getFormHM().put("gz_module", gz_model);
			this.getFormHM().put("rsid", rsid);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("ownerType", ownerType);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
