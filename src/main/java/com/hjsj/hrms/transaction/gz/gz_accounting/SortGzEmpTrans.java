package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 *<p>Title:SortGzEmpTrans</p> 
 *<p>Description:薪资审批人员排序</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-14:下午01:20:22</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SortGzEmpTrans extends IBusiness{
	
	public void execute() throws GeneralException 
	{
		String salaryid=(String)this.getFormHM().get("salaryid");
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		ArrayList sortemplist = gzbo.getFieldlist();
		this.getFormHM().put("sortemplist",sortemplist);
		StringBuffer sort_table = new StringBuffer();
		sort_table.append("<table width=\"100%\" border=\"0\" class=\"ListTable\"><tr><td class=\"TableRow\" align=\"left\">&nbsp;</td>");
		sort_table.append("<td class=\"TableRow\" align=\"center\">指标名称</td>");
		sort_table.append("<td class=\"TableRow\" align=\"center\">状态");
		sort_table.append("</td></tr>");
		this.getFormHM().put("sort_table",sort_table.toString());
	
	}
	

}
