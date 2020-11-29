package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class CourseShowDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		String id = this.getFormHM().get("id").toString();
		id = PubFunc.decrypt(SafeCode.decode(id));
		StringBuffer sqlstr = new StringBuffer();
		StringBuffer columns=new StringBuffer();
		StringBuffer strwhere=new StringBuffer();
		sqlstr.append("select ");
		switch(Sql_switcher.searchDbServer()){
		case Constant.MSSQL:
		    sqlstr.append("b0110,e0122,e01a1,a0101,convert(varchar(10),start_date,20) as start_date,convert(varchar(10),end_date,20) as end_date");
			break;
		case Constant.ORACEL:
		    sqlstr.append("b0110,e0122,e01a1,a0101,to_char(start_date,'yyyy-mm-dd') as start_date,to_char(end_date,'yyyy-mm-dd') as end_date");
			break;
		}
		columns.append("b0110,e0122,e01a1,a0101,start_date,end_date");
		strwhere.append(" from tr_selected_lesson");
		strwhere.append(" where 1=1");
		strwhere.append(" and r5000="+id);
		
		this.getFormHM().put("strsql", sqlstr.toString());
		this.getFormHM().put("strwhere", strwhere.toString());
		this.getFormHM().put("columns", columns.toString());
		
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String acode = (String)hm.get("a_code");
		acode = acode == null ? "" : acode;
		this.getFormHM().put("a_code", acode);
		hm.remove("a_code");
        //重置翻页
		this.getFormHM().put("initPage", "true");
	}
	
}	
