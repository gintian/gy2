package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class FindItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String codesetid = SafeCode.decode((String) (reqhm.get("codesetid")));
		String currnodetext=(String)reqhm.get("codesetid")+" "+SafeCode.decode((String)reqhm.get("currnodetext"));
		String sqlstr = "select  fieldsetdesc,itemid,itemdesc,useflag ";
		String column = "fieldsetdesc,itemid,itemdesc,useflag";
		StringBuffer where =new StringBuffer();
		where.append("from (select fieldsetdesc,itemid,itemdesc,fit.useflag ");
		where.append("from fielditem  fit ");
		where.append("left join ");
		where.append("(select * from fieldset) fat ");
		where.append(" on fit.fieldsetid=fat.fieldsetid ");
		where.append(" where codesetid='"+codesetid+"'");
		where.append(" union all ");
		where.append(" select fieldsetdesc,itemid,itemdesc,thb.useflag from t_hr_busifield thb ");
		where.append(" left join  ");
		where.append("(select * from t_hr_busitable) tht ");
		where.append("on tht.fieldsetid=thb.fieldsetid");
		where.append(" where codesetid='"+codesetid+"'");
		where.append(") ccc");
		this.getFormHM().put("sqlstr", sqlstr);
		this.getFormHM().put("column", column);
		this.getFormHM().put("where", where.toString());
		this.getFormHM().put("currnodetext",currnodetext);
		reqhm.remove("codesetid");
		reqhm.remove("currnodetext");
	}

}
