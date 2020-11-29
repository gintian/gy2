package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


/**
 * 项目名称 ：ehr7.x
 * 类名称：DelSalaryTemplateTrans
 * 类描述：删除工资类别
 * 创建人： lis
 * 创建时间：2015-11-12
 */
public class DelSalaryTypeTrans extends IBusiness {

	@Override
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
					String salaryid=(String)selectedList.get(i);
					salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
					salaryids[i]=salaryid;
					safeBo.isSalarySetResource(salaryid,null);
				} 
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<salaryids.length;i++)
				{
					whl.append(","+salaryids[i]);				
				}				
				//-------------------------------删除帐套记入日志 zhaoxg add 2015-4-28-----------------------
				ContentDAO dao=new ContentDAO(this.frameconn);
				this.frowset = dao.search("select salaryid,cname from salarytemplate where salaryid in ("+whl.substring(1)+")");
				StringBuffer context = new StringBuffer();
				while(this.frowset.next()){
					if(context.length()==0)//是第一个,lis修改
						context.append(ResourceFactory.getProperty("gz_new.gz_deleteSalary")+this.frowset.getString("cname")+"("+this.frowset.getString("salaryid")+")");
					else context.append(","+this.frowset.getString("cname")+"("+this.frowset.getString("salaryid")+")");
				}							
				//----------------------------------end---------------------------------------------------
				SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.userView);
				bo.deleteSalaryTemplate(salaryids);
				this.getFormHM().put("@eventlog", context.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(this.frowset);
		}
	}
}
