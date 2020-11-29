package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSQLStr;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowBusiFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String[] sqlstr=new String[4];
		/*HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		
		if(reqhm.containsKey("param")){
			if(reqhm.containsKey("cid")){
//				查询数据库得到第一个结果的fieldsetid发给页面
				String cid=(String) reqhm.get("cid");
				sqlstr=BusiSQLStr.getFieldStr(this.getFieldSetid(cid));
				
			}
			if(reqhm.containsKey("fieldsetid")){
//				调用busiSQLStr得到现实结果
				String fieldsetid=(String) reqhm.get("fieldsetid");
				sqlstr = BusiSQLStr.getFieldStr(fieldsetid);
				
				hm.put("filedid",fieldsetid);
			}
//			if(!reqhm.containsKey("fieldsetid")&&!reqhm.containsKey("cid")){
//				sqlstr=BusiSQLStr.getFieldStr(this.getFieldSetid(null));
//			}
		}
		else{
//			查询数据库得到第一个第一个类别，第一个子集id 
			sqlstr=BusiSQLStr.getFieldStr(this.getFieldSetid(null));
//			this.getFieldSetid(null);
		}
		reqhm.remove("cid");
		reqhm.remove("fieldsetid");*/
		String fieldsetid = (String)hm.get("fieldsetid");
		sqlstr = BusiSQLStr.getFieldStr(fieldsetid);
		
		hm.put("filedid",fieldsetid);
		hm.put("sql",sqlstr[0]);
		hm.put("where",sqlstr[1]);
		hm.put("column",sqlstr[2]);
		hm.put("orderby",sqlstr[3]);
	}
	public String getFieldSetid(String cid){
		String fieldsetid="";
		if(cid==null){
			String sqls="select id from t_hr_subsys ";
			ArrayList idlist=(ArrayList)ExecuteSQL.executeMyQuery(sqls);
			if(idlist.size()>0){
				DynaBean dynabean=(DynaBean)idlist.get(0);
				cid=(String) dynabean.get("id");
			}
		}
		String sql="select fieldsetid,id,fieldsetdesc,displayorder from t_hr_BusiTable where useflag='1' and id='"+cid+"' order by displayorder";
		ArrayList dynalist =(ArrayList) ExecuteSQL.executeMyQuery(sql);

		if(dynalist.size()>0){
			DynaBean dynabean=(DynaBean) dynalist.get(0);
			fieldsetid=(String) dynabean.get("fieldsetid");
		}
		
		return fieldsetid;
	}

}
