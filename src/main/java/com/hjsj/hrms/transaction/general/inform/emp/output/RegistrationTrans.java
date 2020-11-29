package com.hjsj.hrms.transaction.general.inform.emp.output;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class RegistrationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbname = (String)this.getFormHM().get("dbname");
		dbname=dbname!=null?dbname:"";
		
		String a_code = (String)this.getFormHM().get("a_code");
		a_code=a_code!=null?a_code:"";
		a_code= "all".equalsIgnoreCase(a_code)?"":a_code;
		
		String A0100 = (String)this.getFormHM().get("A0100");
		A0100=A0100!=null?A0100:"";
		
		String inforkind = (String)this.getFormHM().get("inforkind");
		inforkind=inforkind!=null?inforkind:"";
		
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null?flag:"";
		//区分调用新版登记 | 旧登记表  
		String type=(String)this.getFormHM().get("type");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		if("0".equals(flag)|| "1".equals(flag)){
			updateResult(dao,dbname,a_code,flag,inforkind,type);
		}else if("2".equals(flag)){
			updateResultSelect(dao,dbname,A0100,inforkind,type);
		}
	}
	private void updateResult(ContentDAO dao,String dbname,String a_code,String result,String infor,String type){
		ArrayList dbprelist = this.userView.getPrivDbList();
		String fieldsetid=dbname;
		String dbpre="Usr";
		for(int i=0;i<dbprelist.size();i++){
			dbpre = (String)dbprelist.get(i);
			if(dbpre!=null&&dbpre.trim().length()>0){
				if(dbname.indexOf(dbpre)!=-1){
					fieldsetid=dbname.replace(dbpre,"");
					break;
				}
			}
		}
		
		String tablename="";
		String itemid="";
		if("1".equals(infor)){
			tablename=dbpre+"A01";
			itemid="A0100";
		}else if("2".equals(infor)){
			tablename="B01";
			dbpre="B";
			itemid="B0110";
		}else if("3".equals(infor)){
			tablename="K01";
			itemid="E01A1";
			dbpre="K";
		}else{
			tablename=dbpre+"A01";
			itemid="A0100";
		}

		StringBuffer sqlstr = new StringBuffer("select "+itemid+" from ");
		sqlstr.append(dbname+" where ");
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);
		String cond=gzbo.whereStrCode(a_code,infor);
		String code="";
		if(a_code!=null&&a_code.length()>0)
			code=a_code.substring(2);
		/*****sunxin 打印输出登记表，当前显示，结果兼职人员没有出不，不对 0018019******/
		InfoUtils infoUtils=new InfoUtils();
		String info_sql=infoUtils.getPartwhere(dbpre, code, this.getFrameconn(),this.userView,result).trim();
		//设置查询结果或者当前显示时，查询兼职人员时应关联操作人单位范围内的兼职人员
		if(info_sql.startsWith("or")) {
			cond+=" and "+info_sql.substring(2);
		}else {
			cond+=info_sql;
		}
		if(cond!=null&&cond.length()>0)
			cond="("+cond+")";// 需要加括号: (当前节点 或 兼职) 且 在查询结果中
		sqlstr.append(cond);
		if("1".equals(result)){
			sqlstr.append(" or "+itemid+" in (select "+itemid+" from ");
			sqlstr.append(this.getUserView().getUserName()+dbpre+"result)");
		}
		try {
			ArrayList recodlist = new ArrayList();
			this.frowset=dao.search(sqlstr.toString());
			while(this.frowset.next()){
				ArrayList list = new ArrayList();
				if("card_new".equalsIgnoreCase(type)) {
					list.add(this.userView.getUserName());
					list.add(dbpre);
					list.add(this.frowset.getString(itemid));
					list.add(infor);
					list.add(this.userView.getStatus());
				}else {
					list.add(this.frowset.getString(itemid));
				}
				recodlist.add(list);
			}
			String addsql="";
			if("card_new".equalsIgnoreCase(type)) {
				dao.update("delete from t_card_result where username='"+this.userView.getUserName()+"'");
				addsql = "insert into t_card_result(username,nbase,objid,flag,status) values(?,?,?,?,?)";
			}else {
				dao.update("delete from "+this.getUserView().getUserName()+dbpre+"result");
				addsql = "insert into "+this.getUserView().getUserName()+dbpre+"result("+itemid+") values(?)";
			}
			dao.batchInsert(addsql,recodlist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void updateResultSelect(ContentDAO dao,String dbname,String A0100,String infor,String type){
		try {
			ArrayList dbprelist = this.userView.getPrivDbList();
			String dbpre="Usr";
			for(int i=0;i<dbprelist.size();i++){
				dbpre = (String)dbprelist.get(i);
				if(dbpre!=null&&dbpre.trim().length()>0){
					if(dbname.indexOf(dbpre)!=-1){
						break;
					}
				}
			}

			String itemid="";
			if("1".equals(infor)){
				itemid="A0100";
			}else if("2".equals(infor)){
				dbpre="B";
				itemid="B0110";
			}else if("3".equals(infor)){
				itemid="E01A1";
				dbpre="K";
			}else{
				itemid="A0100";
			}
			ArrayList recodlist=new ArrayList();
			if(A0100!=null&&A0100.trim().length()>0){
				String[] arr = A0100.split(",");
				for(int i=0;i<arr.length;i++){
					ArrayList list=new ArrayList();
					if(arr[i]!=null&&arr[i].trim().length()>0){
						if("card_new".equalsIgnoreCase(type)) {
							list.add(this.userView.getUserName());
							list.add(dbpre);
							list.add(arr[i]);
							list.add(infor);
							list.add(this.userView.getStatus());
						}else {
							list.add(arr[i]);
						}
						
						recodlist.add(list);
					}
				}
			}
			String addsql="";
			if("card_new".equalsIgnoreCase(type)) {
				dao.update("delete from t_card_result where username='"+this.userView.getUserName()+"'");
				addsql = "insert into t_card_result(username,nbase,objid,flag,status) values(?,?,?,?,?)";
			}else {
				dao.update("delete from "+this.getUserView().getUserName()+dbpre+"result");
				addsql = "insert into "+this.getUserView().getUserName()+dbpre+"result("+itemid+") values(?)";
			}
			dao.batchInsert(addsql,recodlist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
