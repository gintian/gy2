/*
 * Created on 2006-3-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.general.statics.singlestatic;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SingleStaticBo {
	
	private Connection conn=null;
	private UserView userView;
	public SingleStaticBo(Connection conn) {
		this.conn=conn;
	}
	public SingleStaticBo(Connection conn,UserView userView) {
		this.conn=conn;
		this.userView=userView;
	}
	//返回统计出来的数值和数量
	public String getvalueandcount(String dbpre,String setname,String select,String fieldname,String time,String flag,String query,String querywhere, boolean find,String usr)throws GeneralException
	{
//		通过字典得到字段类型和有关信息

		FieldItem fielditem = DataDictionary.getFieldItem(fieldname);
		String itemtype=fielditem.getItemtype();
		String fieldsetid = fielditem.getFieldsetid();
		String fieldset = fieldsetid.substring(0,1);
		String dbname = "";
		String dbvalue = "";
		String sql = "";
		String countsql = "";
		String value = "";
		String count ="";
		if("A".equals(fieldset))
		{
			dbname = "A01";
			dbvalue = "A0100";			
		}
		else if("B".equals(fieldset))
		{
			dbname ="B01";
			dbvalue ="B0110";
		}
		else
		{
			dbname = "K01";
			dbvalue = "E01A1";
		}
		if("D".equals(itemtype))
		{
			float year =Integer.parseInt(time.substring(0,4));
	        float month=Integer.parseInt(time.substring(5,7));
	        float date=Integer.parseInt(time.substring(8));
	        String datavalue = String.valueOf(year+month*0.01+date*0.0001);
	        String datavaluey = String.valueOf(year+month*0.01);
			if("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid))
			{
				sql = gettimesetsql(select,fieldname,dbpre,setname,datavalue,datavaluey,flag,querywhere,find,usr,query);
				//System.out.println(sql);
				value = queryvaluesql(sql);
				countsql = getsettimecountsql(dbpre,setname,select,datavalue,fieldname,value,flag,datavaluey,querywhere,query,find,usr);
				//System.out.println(countsql);
				count = querycountsql(countsql);
			}
			else
			{
				sql = gettimeitemsql(select,fieldname,dbpre,setname,datavalue,dbname,dbvalue,datavaluey,flag,query,find,usr);
				value = queryvaluesql(sql);
				countsql = getitemtimecountsql(dbpre,setname,select,datavalue,dbname,dbvalue,fieldname,value,flag,datavaluey,query,find,usr);
				count = querycountsql(countsql);
			}
		}
	
		
		else if("N".equals(itemtype))
		{
			if("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid))
			{
				sql = getsetsql(select,fieldname,dbpre,setname,querywhere,find,usr,query);
				value = queryvaluesql(sql);
				countsql = getsetcountsql(dbpre,setname,select,fieldname,value,querywhere,query,find,usr);
				count = querycountsql(countsql);
			}
			else
			{
				sql = getitemsql(select,fieldname,dbpre,setname,dbname,dbvalue,query,find,usr);
				value = queryvaluesql(sql);
				countsql = getitemcountsql(dbpre,setname,select,dbname,dbvalue,fieldname,value,query ,find,usr);
				count = querycountsql(countsql);
			}
		}
		if(value==null)
		{
			value = "00000.00000";
		}
		return value+","+count;

		}
	public String getvalueandcount(String dbpre,String setname,String select,String fieldname,String time,String flag,String query,String querywhere, boolean find,String usr,String userbases)throws GeneralException
	{
//		通过字典得到字段类型和有关信息

		FieldItem fielditem = DataDictionary.getFieldItem(fieldname);
		String itemtype=fielditem.getItemtype();
		String fieldsetid = fielditem.getFieldsetid();
		String fieldset = fieldsetid.substring(0,1);
		String dbname = "";
		String dbvalue = "";
		String sql = "";
		String countsql = "";
		String value = "";
		String count ="";
		if("A".equals(fieldset))
		{
			dbname = "A01";
			dbvalue = "A0100";			
		}
		else if("B".equals(fieldset))
		{
			dbname ="B01";
			dbvalue ="B0110";
		}
		else
		{
			dbname = "K01";
			dbvalue = "E01A1";
		}
		if("D".equals(itemtype))
		{
			float year =Integer.parseInt(time.substring(0,4));
	        float month=Integer.parseInt(time.substring(5,7));
	        float date=Integer.parseInt(time.substring(8));
	        String datavalue = String.valueOf(year+month*0.01+date*0.0001);
	        String datavaluey = String.valueOf(year+month*0.01);
			if("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid))
			{
				sql = gettimesetsql(select,fieldname,dbpre,setname,datavalue,datavaluey,flag,querywhere,find,usr,query);
				//System.out.println(sql);
				sql=this.getCombineSQL(dbpre, userbases, sql, setname);
				value = queryvaluesql(sql);
				countsql = getsettimecountsql(dbpre,setname,select,datavalue,fieldname,value,flag,datavaluey,querywhere,query,find,usr);
				countsql = this.getCombineSQL1(dbpre, userbases, countsql, setname);
				//System.out.println(countsql);
				count = querycountsql(countsql);
			}
			else
			{
				sql = gettimeitemsql(select,fieldname,dbpre,setname,datavalue,dbname,dbvalue,datavaluey,flag,query,find,usr);
				sql=this.getCombineSQL(dbpre, userbases, sql, setname);
				value = queryvaluesql(sql);
				countsql = getitemtimecountsql(dbpre,setname,select,datavalue,dbname,dbvalue,fieldname,value,flag,datavaluey,query,find,usr);
				countsql = this.getCombineSQL1(dbpre, userbases, countsql, setname);
				count = querycountsql(countsql);
			}
		}
	
		
		else if("N".equals(itemtype))
		{
			if("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid))
			{
				sql = getsetsql(select,fieldname,dbpre,setname,querywhere,find,usr,query);
				sql = this.getCombineSQL(dbpre, userbases, sql, setname);
				value = queryvaluesql(sql);
				countsql = getsetcountsql(dbpre,setname,select,fieldname,value,querywhere,query,find,usr);
				countsql = this.getCombineSQL(dbpre, userbases, countsql, setname);
				count = querycountsql(countsql);
			}
			else
			{
				sql = getitemsql(select,fieldname,dbpre,setname,dbname,dbvalue,query,find,usr);
				sql = this.getCombineSQL(dbpre, userbases, sql, setname);
				value = queryvaluesql(sql);
				countsql = getitemcountsql(dbpre,setname,select,dbname,dbvalue,fieldname,value,query ,find,usr);
				countsql = this.getCombineSQL(dbpre, userbases, countsql, setname);
				count = querycountsql(countsql);
			}
		}
		if(value==null)
		{
			value = "00000.00000";
		}
		return value+","+count;

		}
	//返回显示最大值和最小值的SQL
	public String getshowsql(String datavalue,String dbpre,String setname,String fieldname,String time,String flag,String query,String realdata)throws GeneralException
	{
		FieldItem fielditem = DataDictionary.getFieldItem(fieldname);
		String itemtype=fielditem.getItemtype();
		String fieldsetid = fielditem.getFieldsetid();
		String fieldset = fieldsetid.substring(0,1);
		String dbname = "";
		String dbvalue = "";
		String sql = "";
		if("A".equals(fieldset))
		{
			dbname = "A01";
			dbvalue = "A0100";
		}
		else if("B".equals(fieldset))
		{
			dbname ="B01";
			dbvalue ="B0110";
		}
		else
		{
			dbname = "K01";
			dbvalue = "E01A1";
		}
		if("N".equals(itemtype))
		{
			if("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid))
			{
				sql = getsetshowsql(fieldname,dbpre,setname,datavalue,query,realdata);			    
			}
			else
			{
				sql = getitemshowsql(fieldname,dbpre,setname,datavalue,dbname,dbvalue,query,realdata);
			}
		}
		else if("D".equals(itemtype))
		{
			float year =Integer.parseInt(time.substring(0,4));
	        float month=Integer.parseInt(time.substring(5,7));
	        float date=Integer.parseInt(time.substring(8));
	        String datavalued = String.valueOf(year+month*0.01+date*0.0001);
	        String datavaluem = String.valueOf(year+month*0.01);
	        if("A01".equals(fieldsetid)|| "B01".equals(fieldsetid)|| "K01".equals(fieldsetid))
	        {
	        	sql = getsettimeshowsql(fieldname,dbpre,setname,datavalue,flag,datavaluem,datavalued,query,realdata);
	        }
	        else
	        {
	        	sql = getitemtimeshowsql(fieldname,dbpre,setname,datavalue,flag,datavaluem,datavalued,dbname,dbvalue,query,realdata);
	        }
		}
		
		
		return sql;
	}
	//通过参数进行保存
	public void savesinglestatic(String dbpre,ArrayList valuelist,String user) throws GeneralException
	{
		if(!"".equals(dbpre)||dbpre!=null)
		{			
		    String sql = getsavesql(dbpre,user);
		    ContentDAO dao=new ContentDAO(this.conn);
		    try
		    {
		    	StringBuffer buf=new StringBuffer();
		    	if(this.userView.getStatus()==0){
			    	buf.append("delete from ");
			    	buf.append(user);
			    	buf.append(dbpre);
			    	buf.append("result");
		    	}else if (this.userView.getStatus()==0){
		    		buf.append("delete from t_sys_result");
		    	}
		    	dao.update(buf.toString());
			    ArrayList paralist=getparalist(dbpre,valuelist);
			    querysql(sql,paralist);
		    	
		    }
		    catch(Exception ex)
		    {
		    	ex.printStackTrace();
		    	throw GeneralExceptionHandler.Handle(ex);
		    }
		}
	}
	
//	得到主集日期型sql语句
	private String gettimesetsql(String select,String fieldname,String dbpre,String setname,String datavalue,String datavaluey,String flag,String querywhere, boolean find,String usr,String query)
	{
		StringBuffer strsql=new StringBuffer();
		if("1".equals(select))
		{
            if("N".equals(flag))
            {
            	strsql.append("select sum("+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01))/COUNT(*) as a ");   			
    			strsql.append("from "+dbpre+setname+querywhere);
            }
            else if("Y".equals(flag))
            {
            	strsql.append("select sum("+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001))/COUNT(*) as a ");
    			strsql.append("from "+dbpre+setname+querywhere);
            }	
		}
		if("2".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select min("+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)) as a ");
				strsql.append("from "+dbpre+setname+querywhere);
			}
			else if("Y".equals(flag))
			{
				strsql.append("select min("+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)) as a ");
				strsql.append("from "+dbpre+setname+querywhere);
			}
		}
		if("3".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select max("+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)) as a ");
				strsql.append("from "+dbpre+setname+querywhere);
			}
			else if("Y".equals(flag))
			{
				strsql.append("select max("+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)) as a ");
				strsql.append("from "+dbpre+setname+querywhere);
			}
		}
		if("4".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select sum("+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)) as a ");
				strsql.append("from "+dbpre+setname+" where 1=1 "+query);//+querywhere);	
			}
			else if("Y".equals(flag))
			{
				strsql.append("select sum("+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)) as a ");
				strsql.append("from "+dbpre+setname+querywhere);
			}
		}
		if(find==true)
		{
			
			if(this.userView.getStatus()==0){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					strsql.append(usr+dbpre+"Result )");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM ");
					strsql.append(usr+"BResult )");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					strsql.append(usr+"KResult )");
				}	
			}else if(this.userView.getStatus()==4){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
					strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					strsql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					strsql.append(" where upper(nbase)='K' and flag=2)");
				}	
			}
		}
		String sql=strsql.toString();
		return sql;
	}
	
//	得到主集数字型sql语句
	private String getsetsql(String select,String fieldname,String dbpre,String setname,String querywhere,boolean find,String usr,String query)
	{
		StringBuffer strsql=new StringBuffer();
		if("1".equals(select))
		{
			strsql.append("select sum("+fieldname+")/COUNT(*) as a ");
			strsql.append("from "+dbpre+setname+querywhere);
		}
		else if("2".equals(select))
		{
			strsql.append("select min("+fieldname+") as a ");
			strsql.append("from "+dbpre+setname+querywhere);
		}
		else if("3".equals(select))
		{
			strsql.append("select max("+fieldname+") as a ");
			strsql.append("from "+dbpre+setname+querywhere);
		}
		else if("4".equals(select))
		{
			strsql.append("select sum("+fieldname+") as a ");
			strsql.append("from "+dbpre+setname+" where 1=1 "+query); //+querywhere);
		}
		if(find==true)
		{
			if(this.userView.getStatus()==0){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					strsql.append(usr+dbpre+"Result )");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM ");
					strsql.append(usr+dbpre+"BResult )");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					strsql.append(usr+"KResult )");
				}
			}else if(this.userView.getStatus()==4){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
					strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					strsql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					strsql.append(" where upper(nbase)='K' and flag=2)");
				}	
			}
		}
		return strsql.toString();
	}
//	得到子集数字型sql语句
	private String getitemsql(String select,String fieldname,String dbpre,String setname,String dbname,String dbvalue,String query,boolean find,String usr)
	{
		StringBuffer strsql=new StringBuffer();
		if("1".equals(select))
		{
			strsql.append("select sum("+fieldname+")/COUNT(*) as a ");
			strsql.append("from ("+dbpre+dbname);
			strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
			strsql.append("(select max(I9999) from "+dbpre+setname);
			strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
			strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
			strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
		}
		else if("2".equals(select))
		{
			strsql.append("select min("+Sql_switcher.isnull(fieldname,"0")+") as a ");
			strsql.append("from ("+dbpre+dbname);
			strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
			strsql.append("(select max(I9999) from "+dbpre+setname);
			strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
			strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
			strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
		}
		else if("3".equals(select))
		{
			strsql.append("select max("+fieldname+") as a ");
			strsql.append("from ("+dbpre+dbname);
			strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
			strsql.append("(select max(I9999) from "+dbpre+setname);
			strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
			strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
			strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
		}
		else if("4".equals(select))
		{
			strsql.append("select sum("+fieldname+") as a ");
			strsql.append("from ("+dbpre+dbname);
			strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
			strsql.append("(select max(I9999) from "+dbpre+setname);
			strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
			strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
			strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
		}
		if(find==true)
		{
			if(this.userView.getStatus()==0){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					strsql.append(usr+dbpre+"Result )");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM ");
					strsql.append(usr+"BResult )");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					strsql.append(usr+"KResult )");
				}	
			}else if (this.userView.getStatus()==4){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
					strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					strsql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					strsql.append(" where upper(nbase)='K' and flag=2)");
				}	
			}
		}
		return strsql.toString();
	}
//	得到子集日期型sql语句
	private String gettimeitemsql(String select,String fieldname,String dbpre,String setname,String datavalue,String dbname,String dbvalue,String datavaluey,String flag,String query,boolean find,String usr)
	{   
		StringBuffer strsql=new StringBuffer();
		if("1".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select sum("+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01))/COUNT(*) as a ");
				strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
			}
			else if("Y".equals(flag))
			{
				strsql.append("select sum("+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001))/COUNT(*) as a ");
				strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
			}			
		}
		else if("2".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select min("+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)) as a ");
				strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
			}
			else if("Y".equals(flag))
			{
				strsql.append("select min("+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)) as a ");
				strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
			}
		}
		else if("3".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select max("+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)) as a ");
				strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
			}
			else if("Y".equals(flag))
			{
				strsql.append("select max("+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)) as a ");
				strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
			}
		}
		else if("4".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select sum("+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)) as a ");
				strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
			}
			else if("Y".equals(flag))
			{
				strsql.append("select sum("+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)) as a ");
				strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
			}
		}
		if(find==true)
		{
			if(this.userView.getStatus()==0){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					strsql.append(usr+dbpre+"Result )");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM ");
					strsql.append(usr+"BResult )");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					strsql.append(usr+"KResult )");
				}
			}else if(this.userView.getStatus()==4){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
					strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					strsql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					strsql.append(" where upper(nbase)='K' and flag=2)");
				}	
			}
		}
		return strsql.toString();
	}
//	得到主集数字型数量sql语句
	private String getsetcountsql(String dbpre,String setname,String select,String fieldname,String value,String querywhere,String query,boolean find,String usr)
	{
		StringBuffer strsql=new StringBuffer();
		if("1".equals(select)|| "4".equals(select))
		{
			strsql.append("select count(*) as a from "+dbpre+setname+" where 1=1 "+query); //querywhere);
		}
		else if("2".equals(select)|| "3".equals(select))
		{
			strsql.append("select count(*) as a from "+dbpre+setname);
			strsql.append(" where (1=1) and "+fieldname+"="+value+query);
		}	
		if(find==true)
		{
			if(this.userView.getStatus()==0){
				if("2".equals(select)|| "3".equals(select))
			     {
					strsql.append(" and ");
				 }else{
				    strsql.append(" and ");
				 }
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					strsql.append(usr+dbpre+"Result )");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM ");
					strsql.append(usr+"BResult )");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					strsql.append(usr+"KResult )");
				}	
			}else if(this.userView.getStatus()==4){
				if("2".equals(select)|| "3".equals(select))
			     {
					strsql.append(" and ");
				 }else{
				    strsql.append(" and ");
				 }
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
					strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					strsql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					strsql.append(" where upper(nbase)='K' and flag=2)");
				}
			}
		}
		return strsql.toString();
	}
//	得到子集数字型数量sql语句
	private String getitemcountsql(String dbpre,String setname,String select,String dbname,String dbvalue,String fieldname,String value,String query,boolean find,String usr)
	{
		StringBuffer strsql=new StringBuffer();
		if("1".equals(select)|| "4".equals(select))
		{
			strsql.append("select count(*) as a from ("+dbpre+dbname);
			strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
			strsql.append("(select max(I9999) from "+dbpre+setname);
			strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
			strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
			strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
		}
		else if("2".equals(select)|| "3".equals(select))
		{
			strsql.append("select count(*) as a from ("+dbpre+dbname);
			strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
			strsql.append("(select max(I9999) from "+dbpre+setname);
			strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
			strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
			strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+" and "+Sql_switcher.isnull(fieldname,"0")+"="+value+query);
		}
		if(find==true)
		{
			if(this.userView.getStatus()==0){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					strsql.append(usr+dbpre+"Result )");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM ");
					strsql.append(usr+"BResult )");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					strsql.append(usr+"KResult )");
				}	
			}else if (this.userView.getStatus()==4){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
					strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					strsql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					strsql.append(" where upper(nbase)='K' and flag=2)");
				}
			}
		}
		return strsql.toString();
	}
//	得到主集日期型数量sql语句
	private String getsettimecountsql(String dbpre,String setname,String select,String datavalue,String fieldname,String value,String flag,String datavaluey,String querywhere,String query,boolean find,String usr)
	{
		StringBuffer strsql=new StringBuffer();
		if("1".equals(select)|| "4".equals(select))
		{
		//	strsql.append("select count(*) as a from "+dbpre+setname+querywhere);
			strsql.append("select count(*) as a from "+dbpre+setname+" where 1=1 "+query);
		}
		else if("2".equals(select)|| "3".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select count(*) as a from "+dbpre+setname);
				strsql.append(" where (1=1) and "+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)"+"="+value+query);
			}
			else if("Y".equals(flag))
			{
				strsql.append("select count(*) as a from "+dbpre+setname);
				strsql.append(" where (1=1) and "+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)"+"="+value+query);
			}
			
		}
		if(find==true)
		{
			if(this.userView.getStatus()==0){
				if(strsql.toString().toLowerCase().indexOf("where")!=-1 || strsql.toString().indexOf("WHERE")!=-1 || "2".equals(select)|| "3".equals(select))
			     {
					strsql.append(" and ");
				 }else{
				    strsql.append(" where ");
				 }
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					strsql.append(usr+dbpre+"Result )");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM ");
					strsql.append(usr+"BResult )");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					strsql.append(usr+"KResult )");
				}	
			}else if(this.userView.getStatus()==4){
				if(strsql.toString().toLowerCase().indexOf("where")!=-1 || strsql.toString().indexOf("WHERE")!=-1 || "2".equals(select)|| "3".equals(select))
			     {
					strsql.append(" and ");
				 }else{
				    strsql.append(" where ");
				 }
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
					strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					strsql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					strsql.append(" where upper(nbase)='K' and flag=2)");
				}	
			}
		}
        return strsql.toString();
	}
//	得到子集日期型数量sql语句
	private String getitemtimecountsql(String dbpre,String setname,String select,String datavalue,String dbname,String dbvalue,String fieldname,String value,String flag,String datavaluey,String query,boolean find,String usr)
	{
		StringBuffer strsql=new StringBuffer();
		if("1".equals(select)|| "4".equals(select))
		{
			strsql.append("select count(*) as a from ("+dbpre+dbname);
			strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
			strsql.append("(select max(I9999) from "+dbpre+setname);
			strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
			strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
			strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+query);
		}
		else if("2".equals(select)|| "3".equals(select))
		{
			if("N".equals(flag))
			{
				strsql.append("select count(*) as a from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))");
				strsql.append(" and "+datavaluey+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)"+"="+value+query);
			}
			else if("Y".equals(flag))
			{
				strsql.append("select count(*) as a from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))");
				strsql.append(" and "+datavalue+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)"+"="+value+query);
			}
		}
		if(find==true)
		{
			if(this.userView.getStatus()==0){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					strsql.append(usr+dbpre+"Result )");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM ");
					strsql.append(usr+"BResult )");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					strsql.append(usr+"KResult )");
				}	
			}else if(this.userView.getStatus()==4){
				strsql.append(" and ");
				if("A".equals(setname.substring(0,1)))
				{
					strsql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
					strsql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					strsql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					strsql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					strsql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					strsql.append(" where upper(nbase)='K' and flag=2)");
				}	
			}
		}
		return strsql.toString();
	}
//	执行sql数据压向前台
	private String queryvaluesql(String sql) throws GeneralException
	{   
		String value ="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		try
        {
            recset=dao.search(sql);
            while(recset.next())
            {
              value=recset.getString("a");
            }
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}	
		finally
		{
//			   try
//			   {
//				if(recset!=null)
//					recset.close();
//			   }
//			   catch(Exception ee)
//			   {
//				   ee.printStackTrace();
//			   }			
		}
		return value;
	}
	private String querycountsql(String sql) throws GeneralException
	{
		String count ="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		try
        {
            recset=dao.search(sql);
            while(recset.next())
            {
              count=recset.getString("a");
            }
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}	
		finally
		{
//			   try
//			   {
//				if(recset!=null)
//					recset.close();
//			   }
//			   catch(Exception ee)
//			   {
//				   ee.printStackTrace();
//			   }			
		}
		return count;
	}
	//得到显示结果的子集SQL语句
	private String getitemshowsql(String fieldname,String dbpre,String setname,String datavalue,String dbname,String dbvalue,String query,String realdata)
	{
		StringBuffer strsql=new StringBuffer();
		if("b".equals(dbpre))
		{
			strsql.append("select "+dbname+".b0110,"+fieldname+" as a "+"#");
			strsql.append("from ("+dbname);
			strsql.append(" left outer join "+setname+" on ("+setname+"."+dbvalue+"="+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+setname+".I9999=");
			strsql.append("(select max(I9999) from "+setname);
			strsql.append(" where "+dbname+"."+dbvalue+"="+setname+"."+dbvalue);
			strsql.append(" group by "+setname+"."+dbvalue+")");
			strsql.append(" OR "+setname+".I9999 IS NULL))"+" and "+Sql_switcher.isnull(fieldname,"0")+"="+realdata+"#");
			strsql.append("b0110,a,");
			
		}
		else if("k".equals(dbpre))
		{
			strsql.append("select "+dbname+".e01a1,"+fieldname+" as a "+"#");
			strsql.append("from ("+dbname);
			strsql.append(" left outer join "+setname+" on ("+setname+"."+dbvalue+"="+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+setname+".I9999=");
			strsql.append("(select max(I9999) from "+setname);
			strsql.append(" where "+dbname+"."+dbvalue+"="+setname+"."+dbvalue);
			strsql.append(" group by "+setname+"."+dbvalue+")");
			strsql.append(" OR "+setname+".I9999 IS NULL))"+" and "+Sql_switcher.isnull(fieldname,"0")+"="+realdata+"#");
			strsql.append("e01a1,a,");
		}
		else
		{
			strsql.setLength(0);
	        strsql.append("select B0110,A0101,"+dbpre+"A01.a0100 as b ,"+fieldname+" as a ,'``' as db"+"#");
	        strsql.append("from ("+dbpre+dbname);
			strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
			strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
			strsql.append("(select max(I9999) from "+dbpre+setname);
			strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
			strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
			strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))"+" and "+Sql_switcher.isnull(fieldname,"0")+"="+realdata+query+"#");
			strsql.append("b0110,a0101,b,a,db");
		}					
		return strsql.toString();
	}
	//得到显示结果的主集SQL语句
	private String getsetshowsql(String fieldname,String dbpre,String setname,String datavalue,String query,String realdata)
	{
		StringBuffer strsql=new StringBuffer();
		if("b".equals(dbpre))
		{
			strsql.append("select b0110,"+fieldname+" as a "+"#");
			strsql.append("from "+setname+" where "+fieldname+"="+realdata+"#");
			strsql.append("b0110,a,");
		}
		else if("k".equals(dbpre))
		{
			strsql.append("select e01a1,"+fieldname+" as a "+"#");
			strsql.append("from "+setname+" where "+fieldname+"="+realdata+"#");
			strsql.append("e01a1,a,");
		}
		else
		{
			strsql.setLength(0);
	        strsql.append("select B0110,A0101,"+dbpre+"A01.a0100 as b ,"+fieldname+" as a ,'``' as db"+"#");
		    strsql.append("from "+dbpre+"A01"+" where "+fieldname+"="+realdata+query+"#");
		    strsql.append("b0110,a0101,b,a,db");
		}						
		return strsql.toString();
	}
	//得到显示时间的子集SQL语句
	private String getitemtimeshowsql(String fieldname,String dbpre,String setname,String datavalue,String flag,String datavaluem,String datavalued,String dbname,String dbvalue,String query,String realdata)
	{
		StringBuffer strsql=new StringBuffer();
		if("b".equals(dbpre))
		{
			if("N".equals(flag))
			{
				strsql.append("select "+dbname+"."+"b0110,"+datavalue+" as a "+"#");
				strsql.append("from ("+dbname);
				strsql.append(" left outer join "+setname+" on ("+setname+"."+dbvalue+"="+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+setname+".I9999=");
				strsql.append("(select max(I9999) from "+setname);
				strsql.append(" where "+dbname+"."+dbvalue+"="+setname+"."+dbvalue);
				strsql.append(" group by "+setname+"."+dbvalue+")");
				strsql.append(" OR "+setname+".I9999 IS NULL))");
				strsql.append(" and "+datavaluem+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)"+"="+realdata+"#");
				strsql.append("b0110,a,");
			}
			else if("Y".equals(flag))
			{
				strsql.append("select "+dbname+"."+"b0110,"+datavalue+" as a "+"#");
				strsql.append("from ("+dbname);
				strsql.append(" left outer join "+setname+" on ("+setname+"."+dbvalue+"="+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+setname+".I9999=");
				strsql.append("(select max(I9999) from "+setname);
				strsql.append(" where "+dbname+"."+dbvalue+"="+setname+"."+dbvalue);
				strsql.append(" group by "+setname+"."+dbvalue+")");
				strsql.append(" OR "+setname+".I9999 IS NULL))");
				strsql.append(" and "+datavalued+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)"+"="+realdata+"#");
				strsql.append("b0110,a,");
			}
			
		}
		else if("k".equals(dbpre))
		{
			if("N".equals(flag))
			{
				strsql.append("select "+dbname+"."+"e01a1,"+datavalue+" as a "+"#");
				strsql.append("from ("+dbname);
				strsql.append(" left outer join "+setname+" on ("+setname+"."+dbvalue+"="+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+setname+".I9999=");
				strsql.append("(select max(I9999) from "+setname);
				strsql.append(" where "+dbname+"."+dbvalue+"="+setname+"."+dbvalue);
				strsql.append(" group by "+setname+"."+dbvalue+")");
				strsql.append(" OR "+setname+".I9999 IS NULL))");
				strsql.append(" and "+datavaluem+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)"+"="+realdata+"#");
				strsql.append("e01a1,a,");
			}
			else if("Y".equals(flag))
			{
				strsql.append("select "+dbname+"."+"e01a1,"+datavalue+" as a "+"#");
				strsql.append("from ("+dbname);
				strsql.append(" left outer join "+setname+" on ("+setname+"."+dbvalue+"="+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+setname+".I9999=");
				strsql.append("(select max(I9999) from "+setname);
				strsql.append(" where "+dbname+"."+dbvalue+"="+setname+"."+dbvalue);
				strsql.append(" group by "+setname+"."+dbvalue+")");
				strsql.append(" OR "+setname+".I9999 IS NULL))");
				strsql.append(" and "+datavalued+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)"+"="+realdata+"#");
				strsql.append("e01a1,a,");
			}
		}
		else
		{
			if("N".equals(flag))
			{
				strsql.setLength(0);
		        strsql.append("select B0110,A0101,"+dbpre+"A01.a0100 as b ,"+datavalue+" as a ,'``' as db"+"#");
		        strsql.append("from ("+dbpre+dbname);
		        strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))");
				strsql.append(" and "+datavaluem+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)"+"="+realdata+query+"#");
				strsql.append("b0110,a0101,b,a,db");
			}
			else if("Y".equals(flag))
			{
				strsql.setLength(0);
		        strsql.append("select B0110,A0101,"+dbpre+"A01.a0100 as b ,"+datavalue+" as a ,'``' as db"+"#");
		        strsql.append("from ("+dbpre+dbname);
				strsql.append(" left outer join "+dbpre+setname+" on ("+dbpre+setname+"."+dbvalue+"="+dbpre+dbname+"."+dbvalue+"))");
				strsql.append(" where (1 = 1) and (("+dbpre+setname+".I9999=");
				strsql.append("(select max(I9999) from "+dbpre+setname);
				strsql.append(" where "+dbpre+dbname+"."+dbvalue+"="+dbpre+setname+"."+dbvalue);
				strsql.append(" group by "+dbpre+setname+"."+dbvalue+")");
				strsql.append(" OR "+dbpre+setname+".I9999 IS NULL))");
				strsql.append(" and "+datavalued+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)"+"="+realdata+query+"#");
				strsql.append("b0110,a0101,b,a,db");
			}
		}
		
		return strsql.toString();
	}
	//得到显示时间的主集SQL语句
	private String getsettimeshowsql(String fieldname,String dbpre,String setname,String datavalue,String flag,String datavaluem,String datavalued,String query,String realdata)
	{
		StringBuffer strsql=new StringBuffer();
		if("b".equals(dbpre))
		{
			if("N".equals(flag))
			{
				strsql.append("select b0110,"+datavalue+" as a "+"#");
				strsql.append("from "+setname+" where "+datavaluem+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)"+"="+realdata+"#");
				strsql.append("b0110,a,");
			}
			else if("Y".equals(flag))
			{
				strsql.append("select b0110,"+datavalue+" as a "+"#");
				strsql.append("from "+setname+" where "+datavalued+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)"+"="+realdata+"#");
				strsql.append("b0110,a,");
			}
			
		}
		else if("k".equals(dbpre))
		{
			if("N".equals(flag))
			{
				strsql.append("select e01a1,"+datavalue+" as a "+"#");
				strsql.append("from "+setname+" where "+datavaluem+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)"+"="+realdata+"#");
				strsql.append("e01a1,a,");
			}
			else if("Y".equals(flag))
			{
				strsql.append("select e01a1,"+datavalue+" as a "+"#");
				strsql.append("from "+setname+" where "+datavalued+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)"+"="+realdata+"#");
				strsql.append("e01a1,a,");
			}
		}
		else
		{
			if("N".equals(flag))
			{
				strsql.setLength(0);
		        strsql.append("select B0110,A0101,"+dbpre+"A01.a0100 as b ,"+datavalue+" as a ,'``' as db"+"#");
		        strsql.append("from "+dbpre+"A01"+" where "+datavaluem+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01)"+"="+realdata+query+"#");
		        strsql.append("b0110,a0101,b,a,db");
			}
			else if("Y".equals(flag))
			{
				strsql.setLength(0);
				strsql.append("select B0110,A0101,"+dbpre+"A01.a0100 as b ,"+datavalue+" as a ,'``' as db"+"#");
				strsql.append("from "+dbpre+"A01"+" where "+datavalued+"-("+Sql_switcher.year(fieldname)+"+"+Sql_switcher.month(fieldname)+"*0.01+"+Sql_switcher.day(fieldname)+"*0.0001)"+"="+realdata+query+"#");
				strsql.append("b0110,a0101,b,a,db");
			}
		}
		return strsql.toString();
	}
//	执行SQL语句，结果LIST压往前台
	private ArrayList querysql(String sql,String dbpre) throws GeneralException
	{
		ArrayList valuelist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		if("b".equals(dbpre))
		{
			 try
		        {
		           recset=dao.search(sql);
		            while(recset.next())
		            {
		            	LazyDynaBean rec=new LazyDynaBean();
		            	rec.set("b0110",recset.getString("b0110"));
		            	rec.set("a",recset.getString("a"));
		            	valuelist.add(rec);
		            }   
		        }
			 catch(Exception ex)
				{
					ex.printStackTrace();
				    throw GeneralExceptionHandler.Handle(ex);
				}	
		}
		else if("k".equals(dbpre))
		{
			try
	        {
	           recset=dao.search(sql);
	            while(recset.next())
	            {
	            	LazyDynaBean rec=new LazyDynaBean();
	            	rec.set("e01a1",recset.getString("e01a1"));
	            	rec.set("a",recset.getString("a"));
	            	valuelist.add(rec);
	            }   
	        }
			catch(Exception ex)
			{
				ex.printStackTrace();
			    throw GeneralExceptionHandler.Handle(ex);
			}	
		}
		else
		{
			try
	        {
	           recset=dao.search(sql);
	            while(recset.next())
	            {
	            	LazyDynaBean rec=new LazyDynaBean();
	            	rec.set("b0110",recset.getString("b0110"));
	            	rec.set("a0101",recset.getString("a0101"));
	            	rec.set("a",recset.getString("a"));
	            	rec.set("b",recset.getString("b"));
	            	valuelist.add(rec);
	            }   
	        }
			catch(Exception ex)
			{
				ex.printStackTrace();
			    throw GeneralExceptionHandler.Handle(ex);
			}	
		}
		return valuelist;
	}
	//得到保存用的SQL语句
	private String getsavesql(String dbpre,String user)
	{
		StringBuffer strsql=new StringBuffer();
		if(this.userView.getStatus()==0){
			strsql.append("insert into ");
			strsql.append(user);
			strsql.append(dbpre);
			strsql.append("result");
			if("b".equals(dbpre))
			{
				strsql.append("(b0110) values(?)");
			}
			else if("k".equals(dbpre))
			{
				strsql.append("(e01a1) values(?)");
			}
			else
			{
				strsql.append("(b0110,a0100) values(?,?)");
			}
		}else if(this.userView.getStatus()==4){
			strsql.append("insert into t_sys_result (username,nbase,obj_id,flag) values(?,?,?,?)");
		}
		return strsql.toString();
	}
	//得到保存用的LIST参数
	private ArrayList getparalist(String dbpre,ArrayList valuelist)
	{   
		ArrayList paralist = new ArrayList();
		if(this.userView.getStatus()==0){
			if("b".equals(dbpre))
			{
				for(int i=0;i<valuelist.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)(valuelist.get(i));
					String b0110 = (String)rec.get("b0110");
					ArrayList para = new ArrayList();
					para.add(b0110);
					paralist.add(para);				
				} 
			}
			else if("k".equals(dbpre))
			{
				for(int i=0;i<valuelist.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)(valuelist.get(i));
					String e01a1 = (String)rec.get("e01a1");
					ArrayList para = new ArrayList();
					para.add(e01a1);
					paralist.add(para);				
				} 
			}
			else
			{
				for(int i=0;i<valuelist.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)(valuelist.get(i));
					String b0110 = (String)rec.get("b0110");
					String a0100 = (String)rec.get("b");
					ArrayList para = new ArrayList();
					para.add(b0110);
					para.add(a0100);
					paralist.add(para);				
				} 
			}
		}else if (this.userView.getStatus()==4){
			//username,nbase,obj_id,flag
			if("b".equals(dbpre))
			{
				for(int i=0;i<valuelist.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)(valuelist.get(i));
					String b0110 = (String)rec.get("b0110");
					ArrayList para = new ArrayList();
					para.add(this.userView.getUserName());
					para.add("B");
					para.add(b0110);
					para.add(new Integer(1));
					paralist.add(para);				
				} 
			}
			else if("k".equals(dbpre))
			{
				for(int i=0;i<valuelist.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)(valuelist.get(i));
					String e01a1 = (String)rec.get("e01a1");
					ArrayList para = new ArrayList();
					para.add(this.userView.getUserName());
					para.add("K");
					para.add(e01a1);
					para.add(new Integer(2));
					paralist.add(para);				
				} 
			}
			else
			{
				for(int i=0;i<valuelist.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)(valuelist.get(i));
					String db = (String)rec.get("db");
					String a0100 = (String)rec.get("b");
					ArrayList para = new ArrayList();
					para.add(this.userView.getUserName());
					para.add(db);
					para.add(a0100);
					para.add(new Integer(0));
					paralist.add(para);				
				} 
			}
		}
		return paralist;
	}
	//执行保存操作
	private void querysql(String sql,ArrayList paralist) throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
	       {
			dao.batchUpdate(sql,paralist);
	       }
	       catch(SQLException sqle)
	       {
	         sqle.printStackTrace();
	 	     throw GeneralExceptionHandler.Handle(sqle);                
	       }	      				
	}
	
	private String getCombineSQL(String dbpre,String userbases,String sql,String setName){
		sql = sql.toUpperCase();
		
		// 获得列
		StringBuffer colums = new StringBuffer();
		String colum = "";
		List list = this.userView.getPrivFieldList(setName,
				Constant.USED_FIELD_SET);
		for (int i = 0; i < list.size(); i++) {
			FieldItem fielditem=(FieldItem)list.get(i);
			colums.append(",");
			colums.append(fielditem.getItemid());
		}
		if (colums.length() > 0) {
			colum = colums.substring(1);
		}
		
		if(dbpre.length()>0){
			if(userbases.indexOf("`")==-1){
				sql=sql.replaceAll(dbpre, userbases);
			}else{
				int a = sql.indexOf("EXTRACT(MONTH FROM");
				if (a == -1) {
					a = 1;
				} else {
					a += 18;
				}
				int b = sql.indexOf("EXTRACT(DAY FROM");
				if (b == -1) {
					b = a;
				} else {
					b += 16;
				}
				int index=sql.indexOf("FROM",b);
				String sqlstr=sql.substring(0, index);
				String wherestr=sql.substring(index);
				StringBuffer sb= new StringBuffer();
				String[] tmpbases=userbases.split("`");
				for(int i=0;i<tmpbases.length;i++){
					String base=tmpbases[i];
					if(base.length()==3){
						if(sb.length()>0){
							sb.append(" union all select "+colum+ " "+wherestr.replaceAll(dbpre, base));
						}else{
							sb.append("select "+colum+ " "+wherestr.replaceAll(dbpre, base));
						}
					}
				}
				sql=sqlstr+"from ("+sb.toString()+") tt";
			}
		}
		return sql;
	}
	
	private String getCombineSQL1(String dbpre,String userbases,String sql,String setName){
		sql = sql.toUpperCase();
		
		// 获得列
		StringBuffer colums = new StringBuffer();
		String colum = "";
		List list = this.userView.getPrivFieldList(setName,
				Constant.USED_FIELD_SET);
		for (int i = 0; i < list.size(); i++) {
			FieldItem fielditem=(FieldItem)list.get(i);
			colums.append(",");
			colums.append(fielditem.getItemid());
		}
		if (colums.length() > 0) {
			colum = colums.substring(1);
		}
		
		if(dbpre.length()>0){
			if(userbases.indexOf("`")==-1){
				sql=sql.replaceAll(dbpre, userbases);
			}else{
				/*int a = sql.indexOf("EXTRACT(MONTH FROM");
				if (a == -1) {
					a = 1;
				} else {
					a += 18;
				}*/
				int index=sql.indexOf("FROM");
				String sqlstr=sql.substring(0, index);
				String wherestr=sql.substring(index);
				StringBuffer sb= new StringBuffer();
				String[] tmpbases=userbases.split("`");
				for(int i=0;i<tmpbases.length;i++){
					String base=tmpbases[i];
					if(base.length()==3){
						if(sb.length()>0){
							sb.append(" union all select "+colum+ " "+wherestr.replaceAll(dbpre, base));
						}else{
							sb.append("select "+colum+ " "+wherestr.replaceAll(dbpre, base));
						}
					}
				}
				sql=sqlstr+"from ("+sb.toString()+") tt";
			}
		}
		return sql;
	}
}
