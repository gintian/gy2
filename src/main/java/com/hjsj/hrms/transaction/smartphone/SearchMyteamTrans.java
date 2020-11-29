package com.hjsj.hrms.transaction.smartphone;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchMyteamTrans extends IBusiness {

	public void execute() throws GeneralException {

		String a_code = (String) this.getFormHM().get("a_code");
		StringBuffer strsql = new StringBuffer(
				"select flag,codesetid,codeitemid,codeitemdesc,count,a0000 from (");
		String colums = "flag,codesetid,codeitemid,codeitemdesc,count";
		String p_a_code="";
		String p_codeitemdesc="";
		int allcount=0;
		/*String strcount=(String)this.getFormHM().get("havecount");
		if(strcount==null||strcount.length()==0)
			strcount="0";
		int havecount=Integer.parseInt(strcount);*/
		String showstyle="1";
		try {
			ArrayList dbprelist = this.userView.getPrivDbList();
			if (dbprelist.size() == 0)
				throw new GeneralException(ResourceFactory
						.getProperty("errors.static.notdbname"));
			String codesetid = "UN";
			String codeitemid = "";
			if (a_code.length() < 2) {
				if (!this.userView.isSuper_admin()) {
					codesetid = this.userView.getManagePrivCode();
					codeitemid = this.getUserView().getManagePrivCodeValue();
					if(codeitemid==null||codeitemid.length()==0){
						if(com.hrms.hjsj.utils.Sql_switcher.searchDbServer()==2){
							if(codesetid.length()<=2){
								codeitemid="`";
							}
						}else{
							if(codesetid.length()==0){
								codeitemid="`";
							}
						}
					}
				}
			}else{
				codesetid = a_code.substring(0,2);
				codeitemid = a_code.substring(2);
			}
			if (codesetid == null || codesetid.length() == 0)
				codesetid = "UN";
			if (codeitemid == null)
				codeitemid = "";
			p_codeitemdesc = com.hrms.frame.utility.AdminCode.getCodeName(codesetid, codeitemid);
			StringBuffer tmpsql=new StringBuffer("(");
			for (int i = 0; i < dbprelist.size(); i++) {
				String dbpre = (String) dbprelist.get(i);
				tmpsql
						.append("(select count(a0100) from "
								+ dbpre
								+ "A01 where ### like codeitemid"+Sql_switcher.concat()+"'%') +");
			}
			tmpsql.setLength(tmpsql.length()-2);
			tmpsql.append(")");
			/*String endsql="";
			if(codeitemid.length()>0){
				if("UN".equals(codesetid)){
					endsql=tmpsql.toString().replaceAll("###", "b0110").replaceAll("codeitemid", "'"+codeitemid+"'").replaceAll("union all", "and (e0122 is null or e0122='') and (e01a1 is null or e01a1='') union all");
				}else if("UM".equals(codesetid)){
					endsql=tmpsql.toString().replaceAll("###", "e0122").replaceAll("codeitemid", "'"+codeitemid+"'").replaceAll("union all", "and (e01a1 is null or e01a1='') union all");
				}
			}*/
			if (codeitemid.length() == 0) {
				strsql
						.append("select 'org' flag,codesetid,codeitemid,codeitemdesc,(case when codesetid='UN' then "+tmpsql.toString().replaceAll("###", "b0110")+" when codesetid='UM' then "+tmpsql.toString().replaceAll("###", "e0122")+" when codesetid='@K' then "+tmpsql.toString().replaceAll("###", "e01a1")+" end) count,a0000 from organization where parentid=codeitemid");
			} else {
				if(a_code.length()<2){
					strsql
					.append("select 'org' flag,codesetid,codeitemid,codeitemdesc,(case when codesetid='UN' then "+tmpsql.toString().replaceAll("###", "b0110")+" when codesetid='UM' then "+tmpsql.toString().replaceAll("###", "e0122")+" when codesetid='@K' then "+tmpsql.toString().replaceAll("###", "e01a1")+" end) count,a0000 from organization where codeitemid='"
							+ codeitemid + "'");
				}else{
					strsql
							.append("select 'org' flag,codesetid,codeitemid,codeitemdesc,(case when codesetid='UN' then "+tmpsql.toString().replaceAll("###", "b0110")+" when codesetid='UM' then "+tmpsql.toString().replaceAll("###", "e0122")+" when codesetid='@K' then "+tmpsql.toString().replaceAll("###", "e01a1")+" end) count,a0000 from organization where parentid<>codeitemid and parentid='"
									+ codeitemid + "'");
				}
			}
			if (codeitemid.length() == 0) {
				strsql
						.append(" union select 'vorg' flag,codesetid,codeitemid,codeitemdesc,(case when codesetid='UN' then "+tmpsql.toString().replaceAll("###", "b0110")+" when codesetid='UM' then "+tmpsql.toString().replaceAll("###", "e0122")+" when codesetid='@K' then "+tmpsql.toString().replaceAll("###", "e01a1")+" end) count,a0000 from vorganization where parentid=codeitemid");
			} else {
				if(a_code.length()<2){
					strsql
							.append(" union select 'vorg' flag,codesetid,codeitemid,codeitemdesc,(case when codesetid='UN' then "+tmpsql.toString().replaceAll("###", "b0110")+" when codesetid='UM' then "+tmpsql.toString().replaceAll("###", "e0122")+" when codesetid='@K' then "+tmpsql.toString().replaceAll("###", "e01a1")+" end) count,a0000 from vorganization where codeitemid='"
									+ codeitemid + "'");
				}else{
					strsql
					.append(" union select 'vorg' flag,codesetid,codeitemid,codeitemdesc,(case when codesetid='UN' then "+tmpsql.toString().replaceAll("###", "b0110")+" when codesetid='UM' then "+tmpsql.toString().replaceAll("###", "e0122")+" when codesetid='@K' then "+tmpsql.toString().replaceAll("###", "e01a1")+" end) count,a0000 from vorganization where parentid<>codeitemid and parentid='"
							+ codeitemid + "'");
				}
			}
			if(a_code.length()>2){
				if (codeitemid.length() > 0) {
					if ("UN".equals(codesetid)) {
						for (int i = 0; i < dbprelist.size(); i++) {
							String dbpre = (String) dbprelist.get(i);
							String condprivsql=this.userView.getPrivSQLExpression(dbpre, true);
							strsql
									.append(" union all select 'per' flag,'"
											+ dbpre
											+ "' codesetid,a0100 codeitemid,a0101 codeitemdesc,1 count,9999 a0000 "
											+ condprivsql
											+ " and b0110='"
											+ codeitemid
											+ "' and (e0122 is null or e0122='') and (e01a1 is null or e01a1='')");
							
						}
						
					} else if ("UM".equals(codesetid)) {
						for (int i = 0; i < dbprelist.size(); i++) {
							String dbpre = (String) dbprelist.get(i);
							String condprivsql=this.userView.getPrivSQLExpression(dbpre, true);
							strsql
									.append(" union all select 'per' flag,'"
											+ dbpre
											+ "' codesetid,a0100 codeitemid,a0101 codeitemdesc,1 count,9999 a0000 "
											+ condprivsql + " and e0122='"
											+ codeitemid
											+ "' and (e01a1 is null or e01a1='')");
						}
					} else if ("@K".equals(codesetid)) {
						for (int i = 0; i < dbprelist.size(); i++) {
							String dbpre = (String) dbprelist.get(i);
							String condprivsql=this.userView.getPrivSQLExpression(dbpre, true);
							strsql
									.append(" union all select 'per' flag,'"
											+ dbpre
											+ "' codesetid,a0100 codeitemid,a0101 codeitemdesc,1 count,9999 a0000 "
											+ condprivsql + " and e01a1='"
											+ codeitemid + "'");
						}
					}
				}
			}
			strsql.append(") tt");
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select count(codeitemid) allcount from ("+strsql.toString()+") ttt where count>0";
			this.frowset = dao.search(sql);
			if(this.frowset.next())
				allcount=this.frowset.getInt("allcount");
			strsql.insert(0, "select flag,codesetid,codeitemid,codeitemdesc,count from (");
			strsql.append(") ttt where count>0");
			
			if(codeitemid.length()>0){
				sql = "select parentid from organization where codeitemid='"+codeitemid+"' and codeitemid<>parentid union select parentid from vorganization where codeitemid='"+codeitemid+"' and codeitemid<>parentid";
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					p_a_code=this.frowset.getString("parentid");
					if(!this.userView.isSuper_admin()){
						if(p_a_code.indexOf(this.userView.getManagePrivCodeValue())==-1){
							this.getFormHM().put("a_code", "");
						}
					}
					sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid='"+p_a_code+"' union select codesetid,codeitemid,codeitemdesc from vorganization where codeitemid='"+p_a_code+"'";
					this.frowset = dao.search(sql);
					if(this.frowset.next()){
						p_a_code=this.frowset.getString("codesetid")+this.frowset.getString("codeitemid");
					}
				}
			}
			if(codeitemid.length()>0&&!"@K".equals(codesetid)){
				/*int emptyorgperson=0;
				this.frowset = dao.search(endsql);
				if(this.frowset.next())
					emptyorgperson=this.frowset.getInt("c");
				if(emptyorgperson==allcount)
					showstyle="2";*/
				sql = "select codeitemid from organization where parentid='"+codeitemid+"' union select codeitemid from vorganization where parentid='"+codeitemid+"'";
				this.frowset = dao.search(sql);
				if(!this.frowset.next()){
					showstyle="2";
				}
			}
			if("@K".equals(codesetid))
					showstyle="2";
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("sql", strsql.toString());
			this.getFormHM().put("columns",colums);
            this.getFormHM().put("strwhere","");  
            this.getFormHM().put("p_a_code", p_a_code);
            this.getFormHM().put("allcount", ""+allcount);
            this.getFormHM().put("showstyle", showstyle);
            this.getFormHM().put("p_codeitemdesc", p_codeitemdesc);
		}
	}

}
