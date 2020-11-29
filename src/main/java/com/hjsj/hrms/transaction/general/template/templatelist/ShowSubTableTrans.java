package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:显示模板子集信息</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 8, 2010 11:11:07 AM</p> 
 *@author dengc
 *@version 5.0
 */
public class ShowSubTableTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String taskid=(String)this.getFormHM().get("taskid");
			String tabid=(String)this.getFormHM().get("tabid");
			String table_name=(String)this.getFormHM().get("table_name");
			String a0100=(String)this.getFormHM().get("a0100");
			String basepre=(String)this.getFormHM().get("basepre");
			String isAppealTable=(String)this.getFormHM().get("isAppealTable");
			String seqnum=(String)this.getFormHM().get("seqnum");
			String columnName=(String)this.getFormHM().get("columnName");
			String sub_domain=(String)this.getFormHM().get("sub_domain");
			
			
			TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			String subHtml="";
			if("22".equals(isAppealTable)&&taskid!=null&&taskid.length()>0)//历史归档数据-附件
			{
				subHtml=bo.getAffixfileTableHtml(taskid);
			}
			else
				subHtml=bo.getSubSetTableHtml(table_name,a0100,basepre,isAppealTable,seqnum,columnName,sub_domain);
			
			this.getFormHM().put("tableHtml", SafeCode.encode(subHtml));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
