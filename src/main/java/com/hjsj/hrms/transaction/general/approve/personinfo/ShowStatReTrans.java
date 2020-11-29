package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.general.approve.personinfo.ApproveSQLStr;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowStatReTrans extends IBusiness {
	private String firstdb;
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm = this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		String org_id=(String) reqhm.get("a_code");
		org_id=org_id!=null&&org_id.length()>0?org_id:"";
		
		UserView uv=this.getUserView();
		String pdbflag="";
		String orgid="";			
        if(org_id.length()>2)
		 orgid=org_id.substring(2,org_id.length());
	
		pdbflag=(String) hm.get("pdbflag");
		// 获得用户的权限人员库
		List list = this.userView.getPrivDbList();
		String dbname = "";
		if (list != null && list.size() > 0) {
			dbname = (String) list.get(0);
		}		
		pdbflag=pdbflag!=null&&pdbflag.trim().length()>0?pdbflag:dbname;
		ApproveSQLStr ass=new ApproveSQLStr();
		String[] sqlStr=ass.getStatesql2(dao,uv,orgid,pdbflag);
		String selectsql = sqlStr[0]+" " +sqlStr[2];
		hm.put("sql",selectsql);
		hm.put("where","");
		hm.put("column",sqlStr[1]);	
		try {
			String selstr = this.getSelstr(dao,uv,pdbflag);
			hm.put("selstr",selstr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hm.put("pdbflag",pdbflag);
		//部门层级
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);   
	}
	public String getSelstr(ContentDAO dao,UserView uv,String pdbflag) throws Exception{
		StringBuffer sbsel=new StringBuffer();
		StringBuffer sbsql=new StringBuffer();
		sbsel.append("<select name=\"pdbflag\" onchange=\"selchange();\">");
		ArrayList  dblist=uv.getPrivDbList();
		if(dblist.size()>0){
		sbsql.append("select dbname,pre from dbname where");
		for(int i=0;i<dblist.size();i++){
			if(i==0){
				sbsql.append(" pre='"+dblist.get(i)+"'");
				this.setFirstdb((String) dblist.get(i));
			}else{
				sbsql.append(" or pre='"+dblist.get(i)+"'");
			}
		}
		RowSet rs =dao.search(sbsql.toString());
		while(rs.next()){
			if(pdbflag==null){
				sbsel.append("<option value=\""+rs.getString("pre")+"\">");
				sbsel.append(rs.getString("dbname"));
				sbsel.append("</option>");
			}else{
				if(pdbflag.equalsIgnoreCase(rs.getString("pre"))){
					sbsel.append("<option value=\""+rs.getString("pre")+"\"  selected= \"selected\">");
					sbsel.append(rs.getString("dbname"));
					sbsel.append("</option>");
				}else{
					sbsel.append("<option value=\""+rs.getString("pre")+"\">");
					sbsel.append(rs.getString("dbname"));
					sbsel.append("</option>");
				}
			}
		}
		sbsel.append("</select>");
		}
		return sbsel.toString();
	}
	public ArrayList getFieldsetList(UserView uv,String abkflag){
		ArrayList fieldlist=null;
		if("A".equalsIgnoreCase(abkflag)){
		fieldlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		}
		if("B".equalsIgnoreCase(abkflag)){
			fieldlist=uv.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		}
		if("K".equalsIgnoreCase(abkflag)){
			fieldlist=uv.getPrivFieldSetList(Constant.POS_FIELD_SET);
		}
		return fieldlist;
	}
	public String getFirstdb() {
		return firstdb;
	}
	public void setFirstdb(String firstdb) {
		this.firstdb = firstdb;
	}
}
