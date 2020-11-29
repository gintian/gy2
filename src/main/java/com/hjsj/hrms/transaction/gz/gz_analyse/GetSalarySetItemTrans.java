package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class GetSalarySetItemTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String setid=(String)this.getFormHM().get("setid");
			String buf=(String)this.getFormHM().get("buf");
			String gz_model=(String)this.getFormHM().get("gz_model");
			String rsid=(String)this.getFormHM().get("rsid");
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn(),this.getUserView());
			if(buf==null|| "".equals(buf.toString()))
				buf="UPPER(itemid)<>'-1' ";
			else 
			{
				buf=buf.substring(1).replaceAll("/","");
				String[] arr=buf.toString().split(",");
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<arr.length;i++)
				{
					sb.append(" UPPER(itemid)<>");
					sb.append("'"+arr[i].toUpperCase()+"' and ");
				}
				sb.setLength(sb.length()-4);
				buf=sb.toString();
			}
			ArrayList itemlist=bo.getGzProjectList(buf,setid,rsid);
			this.getFormHM().put("resumeFieldsList", itemlist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
