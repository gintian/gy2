package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.general.approve.personinfo.ApproveSQLStr;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectSumReTrans extends IBusiness {
	private String firstdb;
	private ArrayList setidlist=new ArrayList();
	private ArrayList setnamelist=new ArrayList();
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		String akflag=(String) reqhm.get("abkflag");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String pdbflag=(String) hm.get("pdbflag");
		String abkflag=(String) hm.get("abkflag");
		String state =(String)hm.get("state");
		if(state==null||state.length()<1){
			state="0";
		}
		if(abkflag==null||abkflag.length()<1){
			abkflag="A";
		}
		UserView uv=this.getUserView();
		String selstr="";
		try {
			selstr = this.getSelstr(dao,uv,pdbflag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("approve.approve.priv"),"",""));
		}
		if(pdbflag==null){
			pdbflag=this.getFirstdb();
		}
		if(pdbflag==null){
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("approve.approve.priv"),"",""));
		}
		ArrayList fieldlist = this.getFieldsetList(uv,abkflag);
		ArrayList tablelist = this.getSumTable(pdbflag,fieldlist,abkflag);
		String[] sumStr=ApproveSQLStr.getSelSumStr(tablelist,state,uv,pdbflag,abkflag);
		if("A".equalsIgnoreCase(akflag)){
			hm.put("abkflag","A");
		}
		hm.put("stateselstr",this.getStateSelStr(state));
		hm.put("pdbflag",pdbflag);
		hm.put("selstr",selstr);
		hm.put("sql",sumStr[0]);
		hm.put("where",sumStr[1]);
		hm.put("column",sumStr[2]);
		hm.put("setnamelist",this.getSetnamelist());
		hm.put("setidlist",this.getSetidlist());
		hm.put("state",state);
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
	public ArrayList getSumTable(String dbflag,ArrayList fieldlist,String abkflag ){
		ArrayList tablelist=new ArrayList();
		ArrayList setnamelist=this.setnamelist;
		ArrayList setidlist = this.setidlist;
		if("A".equalsIgnoreCase(abkflag)){
			for(int i=0;i<fieldlist.size();i++){
				FieldSet fs=(FieldSet) fieldlist.get(i);
				String setname=fs.getFieldsetid();
				String setdesc=fs.getFieldsetdesc();
				String tablename=dbflag+setname;
				String setid = fs.getFieldsetid();
				tablelist.add(tablename);
				setnamelist.add(setdesc);
				setidlist.add(setid);
			}
		}else{
			for(int i=0;i<fieldlist.size();i++){
				FieldSet fs=(FieldSet) fieldlist.get(i);
				String setname=fs.getFieldsetid();
				String tablename=setname;
				String setdesc=fs.getFieldsetdesc();
				String setid = fs.getFieldsetid();
				tablelist.add(tablename);
				setnamelist.add(setdesc);
				setidlist.add(setid);
			}
		}
		this.setSetidlist(setidlist);
		this.setSetnamelist(setnamelist);
		return tablelist;
	}
	public String getStateSelStr(String state){
		StringBuffer sbselstr=new StringBuffer();
		sbselstr.append("<select name=\"state\" onchange=\"selchange();\">");
		if("0".equals(state)){
			sbselstr.append("<option value=\"0\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"0\">");
		}
		
		sbselstr.append(ResourceFactory.getProperty("approve.approve.d"));
		sbselstr.append("</option>");
		if("1".equals(state)){
			sbselstr.append("<option value=\"1\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"1\">");
		}
		sbselstr.append(ResourceFactory.getProperty("button.appeal"));
		sbselstr.append("</option>");
		if("2".equals(state)){
			sbselstr.append("<option value=\"2\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"2\">");
		}
		sbselstr.append(ResourceFactory.getProperty("button.reject"));
		sbselstr.append("</option>");
		if("3".equals(state)){
			sbselstr.append("<option value=\"3\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"3\">");
		}
		
		sbselstr.append(ResourceFactory.getProperty("button.approve"));
		sbselstr.append("</option>");
		
		if("4".equals(state)){
			sbselstr.append("<option value=\"4\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"4\">");
		}
		
		sbselstr.append("申请");
		sbselstr.append("</option>");
		if("5".equals(state)){
			sbselstr.append("<option value=\"5\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"5\">");
		}
		
		sbselstr.append("可修改");
		sbselstr.append("</option>");
		
//		if(state.equals("6")){
//			sbselstr.append("<option value=\"6\" \"  selected= \"selected\">");
//		}else{
//			sbselstr.append("<option value=\"6\">");
//		}
//		
//		sbselstr.append("不同意修改");
		sbselstr.append("</option>");
		sbselstr.append("</select>");
		return sbselstr.toString();
	}
	public String getFirstdb() {
		return firstdb;
	}
	public void setFirstdb(String firstdb) {
		this.firstdb = firstdb;
	}
	public ArrayList getSetnamelist() {
		return setnamelist;
	}
	public void setSetnamelist(ArrayList setnamelist) {
		this.setnamelist = setnamelist;
	}
	public ArrayList getSetidlist() {
		return setidlist;
	}
	public void setSetidlist(ArrayList setidlist) {
		this.setidlist = setidlist;
	}
}
