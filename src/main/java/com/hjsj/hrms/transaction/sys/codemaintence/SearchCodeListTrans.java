package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchCodeListTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String status=(String)reqhm.get("status");
		if(status==null||status.length()<0)
			status="1";
		String sel = (String)reqhm.get("sel")==null?"":(String)reqhm.get("sel");
		reqhm.remove("sel");
		String[] sql = null;
		sel = com.hrms.frame.codec.SafeCode.decode(sel);
		//安全过滤
        sel = PubFunc.hireKeyWord_filter(sel);
		
		String categories = (String)reqhm.get("categories")==null?"":(String)reqhm.get("categories");
		reqhm.remove("categories");
		categories = com.hrms.frame.codec.SafeCode.decode(categories);
		
		if(sel.length()>0)
			sql = this.getSqlStr(status,sel);
		else
			sql = this.getSqlStr1(status,categories);
		this.getFormHM().put("sqlstrf", sql[0]);
		this.getFormHM().put("wheref", sql[1]);
		this.getFormHM().put("columnf", sql[2]);
		this.getFormHM().put("selcodesetid","");
	}
	/**
	 * 查询代码
	 * @param status
	 * @return
	 */
	public String[] getSqlStr(String status,String sel) 
	{
		String[] sql = new String[4];
		sql[0] = "select codesetid,codesetdesc,maxlength,status";
		//if(status.equals("0"))//全部
		//系统管理，代码体系，查询不区分大小写   jingq upd 2014.12.06
		//	sql[1] = "from codeset where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' and codesetid<>'@@' and codesetid like '"+sel+"%' or codesetdesc like '%"+sel+"%'";
		sql[1] = "from codeset where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' and codesetid<>'@@' and upper(codesetid) like '%"+sel.toUpperCase()+"%' or upper(codesetdesc) like '%"+sel.toUpperCase()+"%'";
		//else if(status.equals("1")) //用户代码
			//sql[1] = "from codeset where (codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN') and (status is null or status = '' or status='0') and codesetid like '"+sel+"%'";
		//else //2　系统代码
			//sql[1] = "from codeset where (codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN') and (status=2 or status = 1) and codesetid like '"+sel+"%'";
			
		sql[2] = "codesetid,codesetdesc,maxlength,status";

		return sql;
	}

	public String[] getSqlStr1(String status,String categories) 
	{
		String[] sql = new String[4];
		sql[0] = "select codesetid,codesetdesc,maxlength,status,categories";
		//if(status.equals("0"))//全部
		if(categories.length()>0){
			sql[1] = "from codeset where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' and codesetid<>'@@' and categories='"+categories+"'";
		}else{
			sql[1] = "from codeset where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN' and codesetid<>'@@' ";
		}
			
		//else if(status.equals("1")){ //用户代码
			//if(categories.length()>0){
				//sql[1] = "from codeset where (codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN') and (status is null or status = '' or status='0') and categories='"+categories+"'";
			//}else
				//sql[1] = "from codeset where (codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN') and (status is null or status = '' or status='0') ";
		
		//}else {//2　系统代码
			//if(categories.length()>0){
				//sql[1] = "from codeset where (codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN') and (status=2 or status=1) and categories='"+categories+"'";
			//}else
				//sql[1] = "from codeset where (codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN') and (status=2 or status=1) ";
		//}	
		sql[2] = "codesetid,codesetdesc,maxlength,status,categories";

		return sql;
	}
}
