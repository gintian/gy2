package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class UniqueCorcodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String codesetid = (String)this.getFormHM().get("codesetid");
		String corcode = (String)this.getFormHM().get("corcode");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new java.util.Date());
		String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
		String msg="ok";
			try {
				if("1".equals(corcode_unique)&&corcode.length()>0){
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					this.frowset=dao.search("select count(corcode) c from (select corcode from organization where corcode='"+corcode+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  union all select corcode from vorganization where corcode='"+corcode+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ) tt");
					if(this.frowset.next()){
						if(this.frowset.getInt("c")>0){
							if("@K".equals(codesetid))
								msg="岗位代码值\""+corcode+"\"在系统中已存在，必需唯一!";
							if("UM".equals(codesetid))
								msg="部门代码值\""+corcode+"\"在系统中已存在，必需唯一!";
							if("UN".equals(codesetid))
								msg="单位代码值\""+corcode+"\"在系统中已存在，必需唯一!";
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally{
				msg=com.hrms.frame.codec.SafeCode.encode(msg);
				this.getFormHM().put("msg", msg);
			}
		
	}

}
