package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 
 * <p>Title:InitSalaryTemplateTrans.java</p>
 * <p>Description>:历史数据初始化</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 29, 2016 1:23:43 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class InitSalaryTemplateTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String initType=(String)this.getFormHM().get("initType");  // 1:全部  2：时间范围
			String startDate=(String)this.getFormHM().get("startDate");
			String endDate=(String)this.getFormHM().get("endDate");
			String selectedList=(String)this.getFormHM().get("selectedList");
			String[] ids = selectedList.substring(1).split(",");
			String[] salaryids=new String[ids.length];
			for(int i=0;i<ids.length;i++)
			{
				salaryids[i]=PubFunc.decrypt(SafeCode.decode(ids[i]));
			}
			SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.userView);
			bo.initSalaryHistoryData(initType,startDate,endDate,salaryids);
			
			//---------------------------------------历史数据初始化，把相应待办删除  zhaoxg add 2016-7-29--------------
			ContentDAO dao=new ContentDAO(this.frameconn);
			PendingTask pt = new PendingTask();
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
				RowSet rs = dao.search("select pending_id from t_hr_pendingtask "+str);
				while(rs.next()){
					pt.updatePending("G", "G"+rs.getString("pending_id"), 100, "薪资审批", this.userView);
				}
				dao.delete("delete from t_hr_pendingtask "+str, new ArrayList());
			}
			//--------------------------------------end-----------------------------------------------------------
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
