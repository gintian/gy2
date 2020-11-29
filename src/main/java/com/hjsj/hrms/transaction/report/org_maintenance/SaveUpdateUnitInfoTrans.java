
package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveUpdateUnitInfoTrans extends IBusiness {
	
	//填报单位的信息修改操作
	public void execute() throws GeneralException {
		
		//获得提交填报单位数据(post方式)
		String unitcode = (String)this.getFormHM().get("unitCode");
		String temp = (String)this.getFormHM().get("unitName");
		String start_date=((String)this.getFormHM().get("start_date")).trim();
		String end_date=((String)this.getFormHM().get("end_date")).trim();
		
		String unitname = temp.replaceAll("<br>","");
		
		StringBuffer strsql = new StringBuffer();
		strsql.delete(0,strsql.length());
		
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("tt_organization");
			vo.setString("unitname",unitname);
			vo.setString("unitcode",unitcode);
			vo.setDate("start_date",start_date);
			vo.setDate("end_date",end_date);
			dao.updateValueObject(vo);
			
			
			//SQL
		/*	strsql.append("update tt_organization set unitname= '");
			strsql.append(unitname);
			strsql.append("' where unitcode = '");
			strsql.append(unitcode);
			strsql.append("'");		
			//执行SQL
			dao.update(strsql.toString());*/
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		//添加要跳转到页面的参数
		this.getFormHM().put("unitCodeFalg",unitcode);
		this.getFormHM().put("addFlag","update");
	}
}
