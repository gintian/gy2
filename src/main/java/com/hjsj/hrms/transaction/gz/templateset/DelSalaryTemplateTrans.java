package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;


/**
 * 删除工资类别
 *<p>Title:DelSalaryTemplateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 24, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class DelSalaryTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList selectedList=(ArrayList)this.getFormHM().get("selectedList");
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			if(selectedList!=null&&selectedList.size()>0)
			{
				String[] salaryids=new String[selectedList.size()];
				for(int i=0;i<selectedList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)selectedList.get(i);
					salaryids[i]=(String)abean.get("salaryid");
					safeBo.isSalarySetResource(salaryids[i],null);
				} 
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<salaryids.length;i++)
				{
					whl.append(","+salaryids[i]);				
				}				
				//-------------------------------删除帐套记入日志 zhaoxg add 2015-4-28-----------------------
				ContentDAO dao=new ContentDAO(this.frameconn);
				RowSet rs = dao.search("select salaryid,cname from salarytemplate where salaryid in ("+whl.substring(1)+")");
				StringBuffer context = new StringBuffer();
				while(rs.next()){
					if(context.length()==0)//是第一个,lis修改
						context.append("删除（帐套）:"+rs.getString("cname")+"("+rs.getString("salaryid")+")");
					else context.append(","+rs.getString("cname")+"("+rs.getString("salaryid")+")");
				}							
				//----------------------------------end---------------------------------------------------
				SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,0);
				pgkbo.deleteSalaryTemplate(salaryids);
				this.getFormHM().put("@eventlog", context.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
