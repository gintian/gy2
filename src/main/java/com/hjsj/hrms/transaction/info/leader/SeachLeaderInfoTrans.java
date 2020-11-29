package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.info.leader.LeaderUtils;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SeachLeaderInfoTrans extends IBusiness {
	String statid ="";
	public void execute() throws GeneralException {

		String b0110 = (String) this.getFormHM().get("b0110");
		String i9999 = (String) this.getFormHM().get("i9999");
		String emp_e = (String) this.getFormHM().get("emp_e");
		String link_field = (String) this.getFormHM().get("link_field");
		String b0110field = (String)this.getFormHM().get("b0110field");
		String orderbyfield = (String)this.getFormHM().get("orderbyfield");
		String orglike = (String)this.getFormHM().get("orglike");
		
		//orglike=orglike==null||orglike.length()==0?"0":orglike;
		orglike="0";
		//HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
        String leaderTypeValue = (String) this.getFormHM().get("leaderTypeValue");
        String sessionValue = (String) this.getFormHM().get("sessionValue");
		
		String leadNext = (String)this.getFormHM().get("leadNext");
		//if(getDataSql(emp_e,link_field,b0110field,i9999,orderbyfield,leadNext,b0110,leaderTypeValue,sessionValue,new ArrayList()))
		//   return;
		
//		ArrayList dbprelist = this.userView.getPrivDbList();
//		StringBuffer selectsql=new StringBuffer("select i9999,dbpre,a0100,b0110,a0101");
//		StringBuffer strsql = new StringBuffer(
//				"select i9999,'usr' dbpre,usrA01.a0100 a0100,usrA01.b0110 b0110,usrA01.a0101 a0101");
//		StringBuffer cond_str = new StringBuffer(" from usrA01 left join usr"
//				+ emp_e + " on usrA01.a0100=usr" + emp_e + ".a0100 where (usr"
//				+ emp_e + "." + link_field + "=" + i9999);
//		cond_str.append(" and usr"+emp_e+"."+b0110field+"='"+b0110+"')");
//		StringBuffer columns = new StringBuffer(",i9999,dbpre,a0100,B0110,A0101");
//		String order_by = "order by "+b0110field+","+orderbyfield;
//		ArrayList browsefields = new ArrayList();
//		StringBuffer sql = new StringBuffer();
//		try {
//			ContentDAO dao = new ContentDAO(this.frameconn);
//			FieldItem item = null;
//			if (!this.userView.analyseFieldPriv("B0110").equals("0")) {
//				item = DataDictionary.getFieldItem("B0110");
//				if (item != null) {
//					browsefields.add(item);
//				}
//			}
//			if (!this.userView.analyseFieldPriv("A0101").equals("0")) {
//				item = DataDictionary.getFieldItem("A0101");
//				if (item != null) {
//					browsefields.add(item);
//				}
//			}
//			ArrayList fields = this.userView.getPrivFieldList(emp_e);
//			if (fields != null) {
//				for (int i = 0; i < fields.size(); i++) {
//					item = (FieldItem) fields.get(i);
//					String itemid = item.getItemid();
//					if ("a0100".equalsIgnoreCase(itemid)
//							|| "i9999".equalsIgnoreCase(itemid)
//							|| (emp_e + "Z0").equalsIgnoreCase(itemid)
//							|| (emp_e + "z1").equalsIgnoreCase(itemid)
//							/*|| link_field.equalsIgnoreCase(itemid)|| b0110field.equalsIgnoreCase(itemid)*/)
//						continue;
//					if(!orderbyfield.equalsIgnoreCase(itemid)&&!link_field.equalsIgnoreCase(itemid)&&!b0110field.equalsIgnoreCase(itemid)){
//						browsefields.add(item);
//					}
//					strsql.append(",usr"+emp_e+"."+itemid+" "+itemid);
//					selectsql.append(","+itemid);
//					columns.append(","+itemid);
//				}
//			}
//			if(columns.indexOf(orderbyfield)==-1){
//				strsql.append(",usr"+emp_e+"."+orderbyfield+" "+orderbyfield);
//				selectsql.append(","+orderbyfield);
//				columns.append(","+orderbyfield);
//			}
//			if(columns.indexOf(link_field)==-1){
//				strsql.append(",usr"+emp_e+"."+link_field+" "+link_field);
//				selectsql.append(","+link_field);
//				columns.append(","+link_field);
//			}
//			if(columns.indexOf(b0110field)==-1){
//				strsql.append(",usr"+emp_e+"."+b0110field+" "+b0110field);
//				selectsql.append(","+b0110field);
//				columns.append(","+b0110field);
//			}
//			StringBuffer sqltable = new StringBuffer();
//			ArrayList childcodeitemidlist = new ArrayList();
//			if("1".equals(orglike)){
//				String tmp = "select codeitemid from organization where codesetid<>'@K' and codeitemid like '"+b0110+"%' and codeitemid<>'"+b0110+"' union select codeitemid from vorganization where codesetid<>'@K' and codeitemid like '"+b0110+"%' and codeitemid<>'"+b0110+"'";
//				this.frowset = dao.search(tmp);
//				while(this.frowset.next()){
//					childcodeitemidlist.add(this.frowset.getString("codeitemid"));
//				}
//			}
//			for(int i=0;i<dbprelist.size();i++){
//				String dbpre = (String)dbprelist.get(i);
//				sqltable.append(" union all "+strsql.toString().replaceAll("usr", dbpre)+cond_str.toString().replaceAll("usr", dbpre));
//				if("1".equals(orglike)){
//					sqltable.append(this.getChildPerson(childcodeitemidlist,dbpre, emp_e, link_field, b0110field, b0110,dao));
//				}
//			}
//			
//			if(sqltable.length()>11){
//				sql.append("from ("+sqltable.substring(11)+") tt");
//			}else{
//				sql.append("from (select * from usrA01 where 1=2) tt");
//			}
//			
//			
//			this.frowset = dao.search("select count(a0100) c "+sql.toString());
//			int len=0;
//			if(this.frowset.next()){
//				len = this.frowset.getInt("c");
//			}
//			this.getFormHM().put("len", new Integer(len));
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			this.getFormHM().put("strsql", selectsql.toString());
//			this.getFormHM().put("cond_str", sql.toString());
//			this.getFormHM().put("columns", columns.toString());
//			this.getFormHM().put("order_by", order_by);
//			this.getFormHM().put("browsefields", browsefields);
//		}
		
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		String display_field=leadarParamXML.getTextValue(LeadarParamXML.DISPLAY);
		if(display_field.trim().length()<5)
			throw GeneralExceptionHandler.Handle(new Exception("请您在班子参数配置中设置班子浏览指标！"));
		
		ConstantXml xml = new ConstantXml(this.frameconn,"ORG_LEADER_STRUCT");
		String leaderType = PubFunc.nullToStr(xml.getNodeAttributeValue("/param/org_m", "team_type"));
		String sessionitem = PubFunc.nullToStr(xml.getNodeAttributeValue("/param/org_m", "term"));
		String org_m = xml.getValue("org_m");
		ArrayList browsefields = new ArrayList();
		LeaderUtils lu = new LeaderUtils();
		ArrayList dblist = userView.getPrivDbList();
		String sql = 
		lu.createLeaderInfoSql(org_m,emp_e, link_field, b0110field, i9999, orderbyfield, display_field, b0110,leadNext,leaderType,leaderTypeValue,sessionitem,sessionValue,browsefields,dblist,userView);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search("select count('1') num from ("+sql+") a");
			if(this.frowset.next()){
				int num = frowset.getInt("num");
				this.getFormHM().put("len", new Integer(num));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		String selectsql = "select * ";
		String wheresql = " from ("+sql+") ta";
		String order_by = " order by "+b0110field+","+orderbyfield;
		String columns = "a0100,i9999,dbpre,"+orderbyfield+","+b0110field+","+link_field+","+display_field;
		//wangcq 2014-12-15 begin 将领导班子人员库放入form
		String dbvalue = "";
		String bz_pre=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);
      	String[] dbpres = bz_pre.split(",");
      	if(dbpres.length>0 && !"".equals(dbpres[0])){
      		for(int i=0; i<dbpres.length; i++){
          		if(this.userView.hasTheDbName(dbpres[i])){
          			if(i!=dbpres.length-1)
          				dbvalue += dbpres[i] + ",";
          			else
          				dbvalue += dbpres[i];
          		}
          	}
      	}else{
      		for(int i=0; i<dblist.size(); i++){
      			if(i!=dblist.size()-1)
      				dbvalue += dblist.get(i) + ",";
      			else
      				dbvalue += dblist.get(i);
      		}
      	}
      	//wangcq 2014-12-15 end
		this.getFormHM().put("strsql", selectsql);
		this.getFormHM().put("cond_str", wheresql);
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("order_by", order_by);
		this.getFormHM().put("browsefields", browsefields);
		this.getFormHM().put("dbvalue", dbvalue);
		//this.getFormHM().put("leadNext", "1");
		

	}
	
//	private String getChildPerson(ArrayList childcodeitemidlist,String dbpre,String emp_e,String link_field,String b0110field,String b0110,ContentDAO dao) throws SQLException{
//		String org_m=(String)this.getFormHM().get("org_m");
//		StringBuffer str = new StringBuffer();
//		for(int i=0;i<childcodeitemidlist.size();i++){
//			String codeitemid = (String)childcodeitemidlist.get(i);
//			String tmpsql = "select max(i9999) maxi9999 from "+org_m+" where b0110='"+codeitemid+"'";
//			this.frowset = dao.search(tmpsql);
//			if(this.frowset.next()){
//				int maxi9999=this.frowset.getInt("maxi9999");
//				str.append(" or("+dbpre+emp_e+"."+b0110field+"='"+codeitemid+"' and "+link_field+"="+maxi9999+")");
//			}
//		}
//		return str.toString();
//	}
	
}
