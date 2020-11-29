package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 重命名薪资类别
 *<p>Title:RenameSalaryTemplateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 24, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class RenameSalaryTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList selectedList=(ArrayList)this.getFormHM().get("selectedList");
			String salarySetName=SafeCode.decode((String)this.getFormHM().get("salarySetName"));
			if(selectedList!=null&&selectedList.size()>0)
			{
				String salaryid="";
				for(int i=0;i<selectedList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)selectedList.get(i);
					salaryid=(String)abean.get("salaryid");
				}
				
				SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,0);
				//-------------------------------删除帐套记入日志 zhaoxg add 2015-4-28-----------------------
				ContentDAO dao=new ContentDAO(this.frameconn);
				RowSet rs = dao.search("select salaryid,cname from salarytemplate where salaryid = "+salaryid+"");
				StringBuffer context = new StringBuffer();
				while(rs.next()){
					context.append("重命名（帐套）:("+rs.getString("salaryid")+"):"+rs.getString("cname")+"---->"+salarySetName);
				}							
				this.getFormHM().put("@eventlog", context.toString());
				//----------------------------------end---------------------------------------------------
				pgkbo.renameSalaryTemplate(salaryid,salarySetName);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
