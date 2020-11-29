package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:UnitLeaderTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Nov 24, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class UnitLeaderTrans extends IBusiness {
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String a_code=(String)this.getFormHM().get("a_code");
		String code="";
		String kind="";
		if(a_code==null||a_code.length()<=0)
		{
			if("UN".equalsIgnoreCase(this.userView.getManagePrivCode()))
				kind="2";
			else if("UM".equalsIgnoreCase(this.userView.getManagePrivCode()))
				kind="1";
			else if("@K".equalsIgnoreCase(this.userView.getManagePrivCode()))
				kind="0";
			code=this.userView.getManagePrivCodeValue();
		}else
		{
			if(a_code.indexOf("UN")!=-1)
			{
				kind="2";
			}else if(a_code.indexOf("UM")!=-1)
			{
				kind="1";
			}else if(a_code.indexOf("@K")!=-1)
			{
				kind="0";
			}
			code=a_code.substring(2);
		}
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("a_code",a_code);
		
		HashMap hm=(HashMap) this.getFormHM().get("requestPamaHM");
		String tablename =(String)hm.get("tablename");
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		String columns = leadarParamXML.getTextValue(LeadarParamXML.UNIT_ZJ);
		String unitcard = leadarParamXML.getTextValue(LeadarParamXML.UNIT_CARD);
		String select_file="";
		StringBuffer strsql = new StringBuffer();
		strsql.append(" ");
		StringBuffer column =new StringBuffer();
		column.append("");
		ArrayList dblist = new ArrayList();
		ArrayList unitfieldlist = new ArrayList();
		ArrayList itemlist = new ArrayList();
		ArrayList unitlist = new ArrayList();
		//CommonData data1 = new CommonData("0","请选择单位子集");
		//unitlist.add(data1);
		if(!"".equalsIgnoreCase(columns)){
			String[] col = columns.split(",");
			ContentDAO dao=new ContentDAO(this.frameconn);
			for(int z=0;z<col.length;z++){
				dblist.add(col[z].toString());
				String sql = "select fieldsetdesc from fieldset where Upper(fieldsetid)='"+col[z].toString().toUpperCase()+"'";
				try {
					RowSet rs = dao.search(sql);
					while(rs.next()){
						CommonData data = new CommonData(col[z].toString(),rs.getString("fieldsetdesc").toString());
						unitlist.add(data);
					}
				} catch (SQLException e) {e.printStackTrace();}
			}
			if(tablename==null)
				tablename=col[0];
			String sql="select itemid,itemdesc,itemtype,codesetid,itemlength from fielditem where Upper(fieldsetid) ='"+tablename.toUpperCase()+"' and Upper(useflag)='1' order by displayid";
			try {
				RowSet rs=dao.search(sql);
				while(rs.next())
				{
					CommonData data=new CommonData();
					LazyDynaBean bean = new LazyDynaBean();
					data.setDataName(rs.getString("itemdesc"));
					data.setDataValue(rs.getString("itemid"));
					unitfieldlist.add(data);
					column.append(rs.getString("itemid")+",");
					bean.set("itemid",rs.getString("itemid").toString().toLowerCase());
					bean.set("itemtype",rs.getString("itemtype").toString().toLowerCase());
					bean.set("codesetid",rs.getString("codesetid").toString().toLowerCase());
					itemlist.add(bean);
				}
			} catch (SQLException e) {e.printStackTrace();}
			try{
			column.setLength(column.length()-1);
			}catch(Exception e){
				
			}
			strsql.append("select ");
			for(int i=0;i<unitfieldlist.size();i++){
				CommonData data=new CommonData();
				data = (CommonData)unitfieldlist.get(i);
				LazyDynaBean bean = (LazyDynaBean)itemlist.get(i);
				String itemtype = (String) bean.get("itemtype");
				/*if(itemtype.equalsIgnoreCase("D")){
					//strsql.append("convert(varchar(10),"+data.getDataValue()+",120) ");
					strsql.append(Sql_switcher.dateValue(Sql_switcher.isnull(data.getDataValue(),"''")));
				}*/
				strsql.append(data.getDataValue()+",");
			}
			strsql.setLength(strsql.length()-1);
			strsql.append(" from "+tablename +" where B0110 = '"+code+"'");
			select_file = tablename;
		}
		this.getFormHM().put("select_file",select_file);
		this.getFormHM().put("fieldlist",unitlist);
		this.getFormHM().put("unitfilelist",unitfieldlist);
		this.getFormHM().put("columns",column.toString());
		this.getFormHM().put("strsql",strsql.toString());
		this.getFormHM().put("unitlist",itemlist);
		this.getFormHM().put("unitcard",unitcard);
			
		/*ArrayList unitlist = new ArrayList();
		this.getFormHM().put("columns",columns);
		StringBuffer strsql = new StringBuffer();
		strsql.append(" ");
		StringBuffer cond_str = new StringBuffer();
		cond_str.append("");
		ArrayList dbs = new ArrayList();
		ArrayList dblist = new ArrayList();
		ArrayList itemlist = new ArrayList();
		
		if(!columns.equalsIgnoreCase("")){
			
			StringBuffer fieldsetid_sql = new StringBuffer();
			
			String[] col = columns.split(",");
			
			for(int z=0;z<col.length;z++){
				dblist.add(col[z].toString());
			}
			ContentDAO dao=new ContentDAO(this.frameconn);
			if(col.length!=1){
				fieldsetid_sql.append("select distinct(fieldsetid) from fielditem where itemid in ('");
				for(int x=0;x<col.length;x++)
					fieldsetid_sql.append(col[x]+"','");
				fieldsetid_sql.setLength(fieldsetid_sql.length()-2);
				fieldsetid_sql.append(")");
				
				try {
					RowSet rs = dao.search(fieldsetid_sql.toString());
					while(rs.next()){
						dbs.add(rs.getString("fieldsetid"));
						//if(rs.getString("fieldsetid").toString().equalsIgnoreCase("B01"))
					}
				} catch (SQLException e) {e.printStackTrace();}
			}
			
			//String columns2 = "";
			
			int index = 0;
			for(int i=0;i<dblist.size();i++){
				if(dblist.get(i).toString().equalsIgnoreCase("b0110"))
					index=i;
			}
			strsql.append("select ");
			if(columns.indexOf("b0110")!=-1){
				String columns2 = columns.replaceAll("b0110","b01.b0110");
				strsql.append(columns2+" from b01 ");
				for(int i=1;i<dbs.size();i++){
					strsql.append("left join "+dbs.get(i)+" on b01.b0110="+dbs.get(i)+".b0110 and "+dbs.get(i)+".I9999 = (select max(I9999) from "+dbs.get(i)+" where b01.b0110="+dbs.get(i)+".b0110"+") ");
				}
			}else{
				strsql.append(columns+" from b01 ");
				for(int i=0;i<dbs.size();i++){
					strsql.append("left join "+dbs.get(i)+" on b01.b0110="+dbs.get(i)+".b0110 and "+dbs.get(i)+".I9999 = (select max(I9999) from "+dbs.get(i)+" where b01.b0110="+dbs.get(i)+".b0110"+") ");
				}
			}
			
			StringBuffer itemsql = new StringBuffer();
			itemsql.append("select itemid,itemtype,codesetid from fielditem where itemid in (");
			for(int j=0;j<dblist.size();j++){
				itemsql.append("'"+dblist.get(j)+"',");
			}
			itemsql.setLength(itemsql.length()-1);
			itemsql.append(")");
			//System.out.println(itemsql.toString());
			try {
				RowSet rs1 = dao.search(itemsql.toString());
				int temp = 0;
				while(rs1.next()){
					LazyDynaBean bean = new LazyDynaBean();
					if(columns.indexOf("b0110")!=-1){
						if(temp==index){
							LazyDynaBean bean1 = new LazyDynaBean();
							bean1.set("itemid","B0110");
							bean1.set("itemtype","");
							bean1.set("codesetid","");
							itemlist.add(bean1);
						}
					}
					bean.set("itemid",rs1.getString(1).toString());
					bean.set("itemtype",rs1.getString(2).toString());
					bean.set("codesetid",rs1.getString(3).toString());
					itemlist.add(bean);
					temp++;
				}
			} catch (SQLException e) {e.printStackTrace();}
		}
		this.getFormHM().put("strsql",strsql.toString());
			
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);
		String unitfile_field=leadarParamXML.getTextValue(LeadarParamXML.UNIT_ZJ);			
		ArrayList unitfilelist=leaderParam.getFields(unitfile_field);
		this.getFormHM().put("unitfilelist",unitfilelist);
		this.getFormHM().put("unitlist",itemlist);*/
	}
}
