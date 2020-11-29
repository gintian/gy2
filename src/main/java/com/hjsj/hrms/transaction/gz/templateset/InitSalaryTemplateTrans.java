package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 
 *<p>Title:InitSalaryTemplateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 7, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class InitSalaryTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String initType=(String)this.getFormHM().get("initType");  // 1:全部  2：时间范围
			String startDate=(String)this.getFormHM().get("startDate");
			String endDate=(String)this.getFormHM().get("endDate");
			ArrayList selectedList=(ArrayList)this.getFormHM().get("selectedList");
			if(selectedList!=null&&selectedList.size()>0)
			{
				String[] salaryids=new String[selectedList.size()];
				for(int i=0;i<selectedList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)selectedList.get(i);
					salaryids[i]=(String)abean.get("salaryid");
				}
				
				SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,0);
				pgkbo.initSalaryHistoryData(initType,startDate,endDate,salaryids);
				
				//---------------------------------------历史数据初始化，把相应待办删除  sunjian add 2017-8-23--------------
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				PendingTask pt = new PendingTask();
				RowSet rs = null;
				for(int i=0;i<salaryids.length;i++){
					StringBuffer str = new StringBuffer();
					if("1".equals(initType)){
						str.append(" where ext_flag like 'GZSP_%' and ext_flag like '%_"+salaryids[i]+"' ");
					}else{
						
						if(startDate.length()>0)
							startDate=startDate.replaceAll("-","").substring(0,6);
						else
							startDate="198001";
						if(endDate.length()>0)
							endDate=endDate.replaceAll("-","").substring(0,6);
						else
							endDate="210001";
						
						str.append(" where ext_flag like 'GZSP_%' and ext_flag like '%_"+salaryids[i]+"' ");
						str.append(" and "+Sql_switcher.substr("ext_flag" ,"6", "6")+" between ");
						str.append("'"+startDate+"'");
						str.append(" and ");
						str.append("'"+endDate+"'");
					}
					rs = dao.search("select pending_id from t_hr_pendingtask "+str);
					while(rs.next()){
						pt.updatePending("G", "G"+rs.getString("pending_id"), 100, "薪资审批", this.userView);
					}
					dao.delete("delete from t_hr_pendingtask "+str, new ArrayList());
				}
				PubFunc.closeResource(rs);
				
				/////////////////////////////////
			//	SalaryPropertyBo dd=new SalaryPropertyBo(this.getFrameconn(),"2",0);
			//	dd.getDbList();
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
