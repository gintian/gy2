package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CheckExistPigeonhole extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String catalog_id=(String)hm.get("catalog_id");
		if(catalog_id==null||catalog_id.length()==0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			catalog_id=sdf.format(new Date());
		}
		StringBuffer sql=new StringBuffer();
		sql.append("select catalog_id from hr_org_history where catalog_id ='");
		sql.append(getCatalog_id(catalog_id));
		sql.append("'");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
				this.getFormHM().put("isexist","true");
			else
				this.getFormHM().put("isexist","false");
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/*生成历史机构的ID*/
	private String getCatalog_id(String archive_date)
	{
		if(archive_date!=null && archive_date.length()>=10)
			return archive_date.substring(0,4) + archive_date.substring(5,7) + archive_date.substring(8,10);
		return Calendar.getInstance().get(Calendar.YEAR) + "" +   (Calendar.getInstance().get(Calendar.MONTH)+1) + Calendar.getInstance().get(Calendar.DATE);
	}

}
