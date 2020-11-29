package com.hjsj.hrms.businessobject.general.inform.map;

import com.hjsj.hrms.businessobject.general.orgmap.ParameterBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateOrgMap {
    private UserView userView;
    private Connection conn;
    private boolean bShowPos=true;
	private boolean bShowDept=true;
	private int iDeptlevel=-1;
	private int iUnitlevel=-1;
	private String backdate;
	private String tmpTableNamePrefix;
	public CreateOrgMap(UserView userView,Connection conn,boolean bShowDept,boolean bShowPos,int iDeptlevel,String backdate,int iUnitlevel)
	{
		this.userView=userView;
		this.conn=conn;
		this.bShowDept=bShowDept;
		this.bShowPos=bShowPos;
		this.iDeptlevel=iDeptlevel;
		this.iUnitlevel=iUnitlevel;
		this.backdate=backdate;
		this.tmpTableNamePrefix="t#"+this.userView.getUserName();
	}
	public String createOrgMapTempTable(boolean rsIsEmpty,String orgtype,HashMap paramehashmap,ParameterBo parameterbo,String code,String kind)
	{
		StringBuffer sql=new StringBuffer();
		String dbname=paramehashmap.get("dbnames").toString();	
		if(rsIsEmpty){
			/********显示人数,显示姓名***********/
			if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype)){
				if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
				{
					/*********删除临时表*********/
					sql.delete(0,sql.length());
					sql.append("drop table ");
					sql.append(tmpTableNamePrefix);
					sql.append("organization");
					try{
						KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
						kqUtilsClass.dropTable(tmpTableNamePrefix+"organization");
					  
					}catch(Exception e)
					{
						
						
					}
					/*********创建临时表*********/
					sql.delete(0,sql.length());
					sql.append("CREATE TABLE ");
					sql.append(tmpTableNamePrefix);
					sql.append("organization (codesetid varchar (2),");
					sql.append("codeitemid varchar (100) not null,");
					sql.append("codeitemdesc varchar (200),");
					sql.append("parentid varchar (100),");
					sql.append("childid varchar (100),");
					sql.append("state varchar (10),");
					sql.append("a0000 int null,");
					sql.append("grade int)");
					try{
						  /*********建立主键*********/
						  ExecuteSQL.createTable(sql.toString(),this.conn);
						  sql.delete(0,sql.length());
						  sql.append("ALTER TABLE ");
						  sql.append(tmpTableNamePrefix);
						  sql.append("organization ADD PRIMARY KEY (codeitemid)");
						  ExecuteSQL.createTable(sql.toString(),this.conn);					  
				    }catch(Exception e)
					{
						e.printStackTrace();
					}	
				    sql.delete(0,sql.length());
				    /*********插入code下面的组织*********/
				    sql.append("INSERT INTO ");
				    sql.append(tmpTableNamePrefix);
				    sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from vorganization where codeitemid like '");
				    sql.append(code);
				    sql.append("%'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				    ContentDAO dao=new ContentDAO(this.conn);
				    
				    try{
				     
				     dao.insert(sql.toString(),new ArrayList());
				     sql.delete(0,sql.length());
				     sql.append("insert into ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
				     sql.append("select 'zz',e.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,'',(select grade from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=e.b0110) from ");
				     sql.append(dbname);
				     sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null  and e.b0110=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
				     sql.append(code);
				     sql.append("%' and e.state=1 and a.a0100=e.a0100 and e.dbase='"+dbname+"'");
				     dao.insert(sql.toString(),new ArrayList());
				     sql.delete(0,sql.length());
				     sql.append("UPDATE ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization d");
				     sql.append(" WHERE d.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
				     sql.append(" WHERE  EXISTS (SELECT * FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization c");
				     sql.append(" WHERE c.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
				     dao.update(sql.toString());
				    }catch(Exception e)
					{
				    	e.printStackTrace();
				    }
				    setEmpGradeNull(tmpTableNamePrefix+"organization");
				    
				}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
				{
					
				    sql.delete(0,sql.length());				   
				    createTempTable(code, kind,sql,orgtype);
				    ContentDAO dao=new ContentDAO(this.conn);
				    
				    try{
				     //dao.insert(sql.toString(),new ArrayList());
				     if(this.bShowPos)
				     {
				    	 sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',a.e01a1 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,a.e01a1,a.e01a1 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
			
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e01a1) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
					     dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
					     dao.insert(sql.toString(),new ArrayList());
				     }else
				     {
				    	 sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',e.b0110 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=e.b0110) from ");
					     sql.append(dbname);
					     sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and e.b0110=c.codeitemid and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%' and e.state=1 and a.a0100=e.a0100 and e.dbase='"+dbname+"'");
					     dao.insert(sql.toString(),new ArrayList());
				     }
				     sql.delete(0,sql.length());
				     sql.append("UPDATE ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization d");
				     sql.append(" WHERE d.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
				     sql.append(" WHERE  EXISTS (SELECT * FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization c");
				     sql.append(" WHERE c.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
				     dao.update(sql.toString());
				    }catch(Exception e)
					{
				    	e.printStackTrace();
				    }
				    setEmpGradeNull(tmpTableNamePrefix+"organization");
				
				}
				else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
				{				
					
				}
				else
				{
					
				}
			}else{
				if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
				{
					/*********删除临时表*********/
					sql.delete(0,sql.length());
					sql.append("drop table ");
					sql.append(tmpTableNamePrefix);
					sql.append("organization");
					try{
						KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
						kqUtilsClass.dropTable(tmpTableNamePrefix+"organization");
					  
					}catch(Exception e)
					{
						
						
					}
					/*********创建临时表*********/
					sql.delete(0,sql.length());
					
					createTempTable(code,kind,sql,orgtype);
					ContentDAO dao=new ContentDAO(this.conn);
				    sql.delete(0,sql.length());
				    /*********插入code下面的组织*********/
					try{
						sql.delete(0,sql.length());
						if(this.bShowPos){
							sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',");
						     sql.append("a.e01a1 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append("a.a0100,a.a0101,a.e01a1,a.e01a1 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e01a1) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date ");
						     switch(Sql_switcher.searchDbServer())
							 {
								  case Constant.MSSQL:
								  {
									  sql.append(" and c.codeitemid like '"+code+"%'");
									  break;
								  }
								  case Constant.ORACEL:
								  {
									  sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
									  break;
								  }
								  case Constant.DB2:
								  {
									  sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
									  break;
								  }
							 }
						     if(!this.userView.isSuper_admin())
						     {
								String whereRen=InfoUtils.getWhereINSql(this.userView,dbname);
								sql.append(" and a.a0100 in(select a0100 "+whereRen+")");	
							 }
						      //System.out.println(sql.toString());
						     dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',a.e0122 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     if(!this.userView.isSuper_admin())
						     {
								String whereRen=InfoUtils.getWhereINSql(this.userView,dbname);
								sql.append(" and a.a0100 in(select a0100 "+whereRen+")");	
							 }
						     dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',a.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.b0110) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     if(!this.userView.isSuper_admin())
						     {
								String whereRen=InfoUtils.getWhereINSql(this.userView,dbname);
								sql.append(" and a.a0100 in(select a0100 "+whereRen+")");	
							 }
						     dao.insert(sql.toString(),new ArrayList());
						}else
						{
							//查询数人员数据库+A01中的数据跟插入想对应的suorganization表中，把同一单位的人员都放在单位级别下面
							 sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',a.e0122 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',a.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.b0110) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());
						}
						 sql.delete(0,sql.length());
					     sql.append("UPDATE "); //更新 下级指标
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization d");
					     sql.append(" WHERE d.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
					     sql.append(" WHERE  EXISTS (SELECT * FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization c");
					     sql.append(" WHERE c.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
					     dao.update(sql.toString());
					}catch(Exception e){
						e.printStackTrace();
					}
					setEmpGradeNull(tmpTableNamePrefix+"organization");
					
				}else{
					//显示人数并且显示姓名
					if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString())){
						/*********删除临时表*********/
						sql.delete(0,sql.length());
						sql.append("drop table ");
						sql.append(tmpTableNamePrefix);
						sql.append("organization");
						try{
							KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
							kqUtilsClass.dropTable(tmpTableNamePrefix+"organization");
						 
						}catch(Exception e)
						{
							
							
						}
						/*********创建临时表*********/
						sql.delete(0,sql.length());
						
						createTempTable(code,kind, sql,orgtype);
						ContentDAO dao=new ContentDAO(this.conn);
					    sql.delete(0,sql.length());
					    /*********插入code下面的组织*********/
					    try{
					     //dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     if(this.bShowPos)
						 {
					    	 sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',");
						     sql.append("a.e01a1 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append("a.a0100,a.a0101,a.e01a1,a.e01a1 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e01a1) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date ");
						     switch(Sql_switcher.searchDbServer())
							 {
								  case Constant.MSSQL:
								  {
									  sql.append(" and c.codeitemid like '"+code+"%'");
									  break;
								  }
								  case Constant.ORACEL:
								  {
									  sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
									  break;
								  }
								  case Constant.DB2:
								  {
									  sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
									  break;
								  }
							 }
						      //System.out.println(sql.toString());
						     dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',a.e0122 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',a.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.b0110) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());	
						 }else
						 {  //查询数人员数据库+A01中的数据跟插入想对应的suorganization表中，把同一单位的人员都放在单位级别下面
							 sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',a.e0122 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',a.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.b0110) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());
						 }
					     
					     sql.delete(0,sql.length());
					     sql.append("UPDATE "); //更新 下级指标
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization d");
					     sql.append(" WHERE d.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
					     sql.append(" WHERE  EXISTS (SELECT * FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization c");
					     sql.append(" WHERE c.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
					     dao.update(sql.toString());
					    }catch(Exception e)
						{
					    	e.printStackTrace();
					    }
					    setEmpGradeNull(tmpTableNamePrefix+"organization");
					    
					}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString())){ 
						/**只显示姓名，不显示人数**/
						sql.delete(0,sql.length());
						sql.append("drop table ");
						sql.append(tmpTableNamePrefix);
						sql.append("organization");
						try{
						  ExecuteSQL.createTable(sql.toString(),this.conn);
						}catch(Exception e)
						{
							//e.printStackTrace();
						}
						
					    sql.delete(0,sql.length());
//					    System.out.println("---> = "+sql);
					    createTempTable(code,kind, sql,orgtype);
					    ContentDAO dao=new ContentDAO(this.conn);
					    try{
						    // dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     if(this.bShowPos)
						     {
						    	 sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
							     sql.append("select 'zz',a.e01a1 ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,a.a0101,a.e01a1,a.e01a1 ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");		
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,'',(select grade from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e01a1) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
							     sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
							     sql.append("select 'zz',a.e0122 ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,'',(select grade from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
							     sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
							     sql.append("select 'zz',a.b0110 ");			     
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,'',(select grade from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.b0110 is not null and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
						     }else
						     {
						    	 sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
							     sql.append("select 'zz',a.e0122 ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,'',(select grade from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     //System.out.println(sql.toString());
							     dao.insert(sql.toString(),new ArrayList());					     
							     sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
							     sql.append("select 'zz',a.b0110 ");			     
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" ' ' ");
							     sql.append(SqlDifference.getJoinSymbol());
							     sql.append(" a.a0100,'',(select grade from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.b0110 is not null and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
						     }				     
						     sql.delete(0,sql.length());
						     sql.append("UPDATE ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization d");
						     sql.append(" WHERE d.parentid = ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
						     sql.append(" WHERE  EXISTS (SELECT * FROM ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization c");
						     sql.append(" WHERE c.parentid = ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
						     dao.update(sql.toString());
						    }catch(Exception e)
							{
						    	e.printStackTrace();
						    }
						    setEmpGradeNull(tmpTableNamePrefix+"organization");
						    
					}
					else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString())){ 
						
					
					}else{
					
					}
				}
			}
		}
		else
		{
			//没有记录的
			if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype))
			{
				if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
				{	
					
					
				    createTempTable(code,kind, sql,orgtype);
				    ContentDAO dao=new ContentDAO(this.conn);
				    
				    try{			   
				     sql.delete(0,sql.length());				    
				     sql.delete(0,sql.length());
				     sql.append("insert into ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
				     sql.append("select 'zz',e.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,'',(select grade from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=e.b0110) from ");
				     sql.append(dbname);
				     sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null and e.b0110=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
				     sql.append(code);
				     sql.append("%' and e.state=1 and a.a0100=e.a0100 and upper(e.dbase)='"+dbname.toUpperCase()+"'");
				     //System.out.println(sql.toString());
				     dao.insert(sql.toString(),new ArrayList());
				     sql.delete(0,sql.length());
				     sql.append("UPDATE ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization d");
				     sql.append(" WHERE d.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
				     sql.append(" WHERE  EXISTS (SELECT * FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization c");
				     sql.append(" WHERE c.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
				     //System.out.println(sql.toString());
				     dao.update(sql.toString());
				    }catch(Exception e)
					{
				    	e.printStackTrace();
				    }
				}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
				{				
					createTempTable(code,kind, sql,orgtype);
				   
				    ContentDAO dao=new ContentDAO(this.conn);	
				    
				try{
				  		   
				     sql.delete(0,sql.length());
				     
				     sql.delete(0,sql.length());
				     sql.append("insert into ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
				     sql.append("select 'zz',e.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,'',(select grade from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=e.b0110) from ");
				     sql.append(dbname);
				     sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null and e.b0110=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
				     sql.append(code);
				     sql.append("%' and e.state=1 and a.a0100=e.a0100 and e.dbase='"+dbname+"'");
				     
				     dao.insert(sql.toString(),new ArrayList());
				     sql.delete(0,sql.length());
				     sql.append("UPDATE ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization d");
				     sql.append(" WHERE d.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
				     sql.append(" WHERE  EXISTS (SELECT * FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization c");
				     sql.append(" WHERE c.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
				     dao.update(sql.toString());
				     //this.conn.commit();
				    }catch(Exception e)
					{
				    	e.printStackTrace();
				    }
				}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
				{
					
				}else
				{
					
				}
			}else//不是虚拟机构的
			{
				if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
				{	
					
					
				    createTempTable(code,kind, sql,orgtype);
				    ContentDAO dao=new ContentDAO(this.conn);				    
				    try{			   
				     if(this.bShowPos)
				     {
				    	 sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',a.a0100,a.e01a1 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,a.e01a1,a.e01a1 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e01a1) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.e01a1 is not null and a.e01a1=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
					     dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',a.a0100,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
					     dao.insert(sql.toString(),new ArrayList());
				     }else
				     {
				    	 sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',a.a0100,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
					     dao.insert(sql.toString(),new ArrayList());
				     }				     
				     sql.delete(0,sql.length());
				     sql.append("insert into ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
				     sql.append("select 'zz',a.a0100,a.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,'',(select grade from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=a.b0110) from ");
				     sql.append(dbname);
				     sql.append("a01 a,organization c where a.b0110 is not null and a.b0110=c.codeitemid  and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
				     sql.append(code);
				     sql.append("%'");
				     dao.insert(sql.toString(),new ArrayList());
				     couplingGarbageData(dbname,dao);
				     //
				     sql.delete(0,sql.length());
				     sql.append("UPDATE ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization d");
				     sql.append(" WHERE d.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
				     sql.append(" WHERE  EXISTS (SELECT * FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization c");
				     sql.append(" WHERE c.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
				     //System.out.println(sql.toString());
				     dao.update(sql.toString());
				    }catch(Exception e)
					{
				    	e.printStackTrace();
				    }
				    setEmpGradeNull(tmpTableNamePrefix+"organization");
				    
					
				}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
				{				
					createTempTable(code,kind, sql,orgtype);
				   
				    ContentDAO dao=new ContentDAO(this.conn);	
				    
				try{
				  	if(this.bShowPos)
				  	{
				  		 sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',a.a0100,a.e01a1 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,a.e01a1,a.e01a1 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e01a1) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
					     dao.insert(sql.toString(),new ArrayList());				     
					     sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',a.a0100,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.e0122 is not null and c.codeitemid=a.e0122 and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
				  	}else
				  	{
				  		 sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid,a0100 codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',a.a0100,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.e0122) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.e0122 is not null and c.codeitemid=a.e0122 and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
				  	}   
				     
				     ///System.out.println(sql.toString());
				     dao.insert(sql.toString(),new ArrayList());
				     sql.delete(0,sql.length());
				     sql.append("insert into ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
				     sql.append("select 'zz',a.a0100,a.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" ' ' ");
				     sql.append(SqlDifference.getJoinSymbol());
				     sql.append(" a.a0100,'',(select grade from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization b where b.codeitemid=a.b0110) from ");
				     sql.append(dbname);
				     sql.append("a01 a,organization c where a.b0110 is not null and c.codeitemid=a.b0110 and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
				     sql.append(code);
				     sql.append("%'");
				     //System.out.println("a0100" + sql.toString());
				     dao.insert(sql.toString(),new ArrayList());
				     couplingGarbageData(dbname,dao);
				     sql.delete(0,sql.length());
				     sql.append("UPDATE ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization d");
				     sql.append(" WHERE d.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
				     sql.append(" WHERE  EXISTS (SELECT * FROM ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization c");
				     sql.append(" WHERE c.parentid = ");
				     sql.append(tmpTableNamePrefix);
				     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
				     dao.update(sql.toString());
				     //this.conn.commit();
				    }catch(Exception e)
					{
				    	e.printStackTrace();
				    }
				    
				    			
				
				}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
				{
					
					
				}else
				{
					
				}
			}
			
		}
		return tmpTableNamePrefix+"organization";
	}
	private void createTempTable(String code,String kind, StringBuffer sql,String orgtype) {
		sql.delete(0,sql.length());
		sql.append("drop table ");
		sql.append(tmpTableNamePrefix);
		sql.append("organization");
		try{
		  ExecuteSQL.createTable(sql.toString(),this.conn);
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		
		switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  sql.delete(0,sql.length());
			  sql.append("CREATE TABLE ");
			  sql.append(tmpTableNamePrefix);
			  sql.append("organization (codesetid varchar (2),");
			  sql.append("codeitemid varchar (100) not null,");
			  sql.append("codeitemdesc varchar (200),");
			  sql.append("parentid varchar (100),");
			  sql.append("childid varchar (100),");
			  sql.append("a0100 varchar (100),");
			  sql.append("state varchar (10),");
			  sql.append("a0000 int null,");
			  sql.append("seqId Int IDENTITY(1,1),");
			  sql.append("grade int,layer int)");					
			  break;
		  }
		  case Constant.DB2:
		  {
			  sql.delete(0,sql.length());
			  sql.append("CREATE TABLE ");
			  sql.append(tmpTableNamePrefix);
			  sql.append("organization (codesetid varchar (2),");
			  sql.append("codeitemid varchar (100) not null,");
			  sql.append("codeitemdesc varchar (200),");
			  sql.append("parentid varchar (100),");
			  sql.append("childid varchar (100),");
			  sql.append("a0100 varchar (100),");
			  sql.append("state varchar (10),");
			  sql.append("a0000 int null,");
			  sql.append("seqId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),");
			  sql.append("grade int,,layer int)");		
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  sql.delete(0,sql.length());
			  sql.append("CREATE TABLE ");
			  sql.append(tmpTableNamePrefix);
			  sql.append("organization (codesetid varchar (2),");
			  sql.append("codeitemid varchar (100) not null,");
			  sql.append("codeitemdesc varchar (200),");
			  sql.append("parentid varchar (100),");
			  sql.append("childid varchar (100),");
			  sql.append("a0100 varchar (100),");
			  sql.append("state varchar (10),");
			  sql.append("a0000 int null,");
			  sql.append("seqId int,");
			  sql.append("grade int,layer int)");		
			  //sql.append("CREATE TABLE ");					
			  break;
		  }
		
		}
		
		try{
			  ExecuteSQL.createTable(sql.toString(),this.conn);
			  sql.delete(0,sql.length());
			  sql.append("ALTER TABLE ");
			  sql.append(tmpTableNamePrefix);
			  sql.append("organization ADD PRIMARY KEY (codeitemid)");
			  ExecuteSQL.createTable(sql.toString(),this.conn);					  
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype))
			  {
				  sql.delete(0,sql.length());
				  sql.append("INSERT INTO ");
				  sql.append(tmpTableNamePrefix);
				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer int from vorganization where codeitemid like '");
				  sql.append(code);
				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  order by a0000,codeitemid,codesetid");	
			  }else
			  {
                  
				  if(!this.bShowDept)
				  {
					  sql.delete(0,sql.length());
					  if(this.bShowPos)
	                  {
						  sql.append("INSERT INTO ");
        				  sql.append(tmpTableNamePrefix);
        				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
        				  sql.append(code);
        				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and (codesetid='UN' or codesetid='@K')  order by a0000,codeitemid,codesetid");
	                  }else
	                  {
	                	  if(kind!=null&& "1".equals(kind))
	                	  {
	                		  sql.append("INSERT INTO ");
	        				  sql.append(tmpTableNamePrefix);
	        				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
	        				  sql.append(code);
	        				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and (codesetid='UN' or codeitemid='"+code+"')  order by a0000,codeitemid,codesetid");
	                	  }else
	                	  {
	                		  sql.append("INSERT INTO ");
	        				  sql.append(tmpTableNamePrefix);
	        				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
	        				  sql.append(code);
	        				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and (codesetid='UN')  order by a0000,codeitemid,codesetid");
	                	  }
	                  }
				  }else
				  {
					  if(this.bShowPos)
	                  {
	                	  sql.delete(0,sql.length());
	    				  sql.append("INSERT INTO ");
	    				  sql.append(tmpTableNamePrefix);
	    				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
	    				  sql.append(code);
	    				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date   order by a0000,codeitemid,codesetid"); 
	                  }else
	                  {
	                	  sql.delete(0,sql.length());
	                	  if(kind!=null&& "0".equals(kind))
	                	  {
	                		  sql.append("INSERT INTO ");
	        				  sql.append(tmpTableNamePrefix);
	        				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
	        				  sql.append(code);
	        				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and (codesetid<>'@K' or codeitemid='"+code+"')  order by a0000,codeitemid,codesetid");
	                	  }else
	                	  {
	                		  sql.append("INSERT INTO ");
	        				  sql.append(tmpTableNamePrefix);
	        				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
	        				  sql.append(code);
	        				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid<>'@K'  order by a0000,codeitemid,codesetid");
	                	  }
	                		  
	    				  
	                  }
				  }				  
				  
				  	
			  }	
			  break;
		  }
		  case Constant.DB2:
		  {
			  if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype))
			  {
				  sql.delete(0,sql.length());
				  sql.append("INSERT INTO ");
				  sql.append(tmpTableNamePrefix);
				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from vorganization where codeitemid like '");
				  sql.append(code);
				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  order by a0000,codeitemid,codesetid");		
				  break;
			  }else
			  {
				  if(!this.bShowDept)
				  {
					  sql.delete(0,sql.length());
    				  sql.append("INSERT INTO ");
    				  sql.append(tmpTableNamePrefix);
    				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
    				  sql.append(code);
    				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid='UN' order by a0000,codeitemid,codesetid");		
    				  break;
				  }else
				  {
					  if(this.bShowPos)
	                  {
						  sql.delete(0,sql.length());
						  sql.append("INSERT INTO ");
						  sql.append(tmpTableNamePrefix);
						  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
						  sql.append(code);
						  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  order by a0000,codeitemid,codesetid");		
						  break;
	                  }else
	                  {
	                	  sql.delete(0,sql.length());
	    				  sql.append("INSERT INTO ");
	    				  sql.append(tmpTableNamePrefix);
	    				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
	    				  sql.append(code);
	    				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid<>'@K' order by a0000,codeitemid,codesetid");		
	    				  break;
	                  }
				  }	
				  
				  
			  }
			  
		  }
		  case Constant.ORACEL:
		  {
			  if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype))
			  {
				  sql.delete(0,sql.length());
				  sql.append("INSERT INTO ");
				  sql.append(tmpTableNamePrefix);
				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, RowNum,layer from vorganization where codeitemid like '");
				  sql.append(code);
				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  order by a0000,codeitemid,codesetid");
			  }else
			  {
				  if(!this.bShowDept)
				  {
					  sql.delete(0,sql.length());
					  if(this.bShowPos)
					  {
						  sql.append("INSERT INTO ");
	    				  sql.append(tmpTableNamePrefix);
	    				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where (codeitemid like '");
	    				  sql.append(code);
	    				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and (codesetid='UN' or codesetid='@K' ))  order by a0000,codeitemid,codesetid");	
					  }else
					  {
						  if(kind!=null&& "1".equals(kind))
	                	  {
							  sql.append("INSERT INTO ");
		    				  sql.append(tmpTableNamePrefix);
		    				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where (codeitemid like '");
		    				  sql.append(code);
		    				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid='UN') or(codeitemid='"+code+"') order by a0000,codeitemid,codesetid");		
	                	  }else
	                	  {
	                		  sql.append("INSERT INTO ");
		    				  sql.append(tmpTableNamePrefix);
		    				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where (codeitemid like '");
		    				  sql.append(code);
		    				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid='UN')  order by a0000,codeitemid,codesetid");		
	                	  }
	    				 
					  }
					  
					  
					 
    				  break;
				  }else
				  {
					  if(this.bShowPos)
	                  {
						  sql.delete(0,sql.length());
						  sql.append("INSERT INTO ");
						  sql.append(tmpTableNamePrefix);
						  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
						  sql.append(code);
						  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  order by a0000,codeitemid,codesetid");		
						  break;
	                  }else
	                  {
	                	  sql.delete(0,sql.length());
	    				  sql.append("INSERT INTO ");
	    				  sql.append(tmpTableNamePrefix);
	    				  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,layer from organization where codeitemid like '");
	    				  sql.append(code);
	    				  sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid<>'@K' order by a0000,codeitemid,codesetid");		
	    				  break;
	                  }
				  }	
				  
				  
			  }			  				
			  break;
		  }		
		}
   
		ContentDAO dao=new ContentDAO(this.conn);
		//System.out.println(sql.toString());
		try{			
		 dao.insert(sql.toString(),new ArrayList());
		 //更新临时表中的A0000大排序
		
		 switch(Sql_switcher.searchDbServer())
			{
			  case Constant.MSSQL:
			  {
				  UpdateTempA0000(dao);
				  break;
			  }
			  case Constant.DB2:
			  {
				 // UpdateTempA0000(dao);
				  break;
			  }
			  case Constant.ORACEL:
			  {
				  UpdateTempA0000(dao);
				  break;
			  }
			
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		orderTempChildid(tmpTableNamePrefix+"organization");
		filtrateDeptLevel(tmpTableNamePrefix+"organization");
		
	}
    private void UpdateTempA0000(ContentDAO dao)
    {
    	 try{
    		 StringBuffer A0000sql=new StringBuffer();
    		 A0000sql.append("update ");
    		 A0000sql.append(tmpTableNamePrefix);
    		 A0000sql.append("organization set A0000=seqId");
		     dao.update(A0000sql.toString());
		     checkTempA0000(dao);
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    }
    private void checkTempA0000(ContentDAO dao)
    {
    	String table=tmpTableNamePrefix+"organization";
    	String codeitemid="";
    	int max=0;
    	int min=0;
    	try
    	{
    		String sql="select codeitemid,a0000 from "+table+" where codeitemid=parentid order by a0000";
    		List rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.conn);
    		if(!rootnode.isEmpty())
    		{
    			for(int i=0;i<rootnode.size();i++)
				{
    				LazyDynaBean orgmapbean=(LazyDynaBean)rootnode.get(i);
    				codeitemid=orgmapbean.get("codeitemid").toString();
    				min=Integer.parseInt(orgmapbean.get("a0000").toString());
    				sql="select max(a0000)as a0000 from "+table+" where codeitemid like '"+codeitemid+"%' and codeitemid<>parentid";
    				RowSet rs=dao.search(sql);
    				if(rs.next())
    				{
    					max=rs.getInt("a0000");
    					sql="select codeitemid  from "+table+" where codeitemid like '"+codeitemid+"%' and codeitemid<>parentid order by codeitemid,codesetid";
    					rs=dao.search(sql);
    					ArrayList listp=new ArrayList();
    					int r=1;
    					while(rs.next())
    					{
    						ArrayList listo=new ArrayList();
    						listo.add(new Integer(min+r)); 
    						listo.add(rs.getString("codeitemid")); 
    						listp.add(listo);
    						if(min+r>=max) {
                                break;
                            }
    						r++;    						   						
    					}    					
    					sql="update "+table+" set a0000=? where codeitemid=?";
    					dao.batchUpdate(sql, listp);
    				}else
    				{
    					continue;
    				}
    					
				}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    
    private void  checkorg(String tablename)
	{
		 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.conn);
		 try{
			 sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(tablename);
		     sql.append(" SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append(tablename + " d");
		     sql.append(" WHERE d.parentid = ");
		     sql.append(tablename);
			 sql.append(".codeitemid  AND d.parentid <> d.codeitemid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append(tablename);
		     sql.append(" c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append(tablename);
		     sql.append(".codeitemid AND c.parentid <> c.codeitemid)");
		     //System.out.println(sql.toString());
		     dao.update(sql.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}
    /**
     * 找孩子
     * @param tablename
     */
    private void orderTempChildid(String tablename)
    {
    	 if(this.bShowPos&&this.bShowDept) {
             return;
         }
    	 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.conn);
		 
		 sql.append("select codeitemid,childid from "+tablename+" where codeitemid<>childid");
		 List list=ExecuteSQL.executeMyQuery(sql.toString(),this.conn);	
		 String codeitemid="";
		 String childid="";
		 ArrayList arlist=new ArrayList();
		 ArrayList chlist=new ArrayList();
		 if(!list.isEmpty())
		 {
			 for(int i=0;i<list.size();i++)
			 {
				 LazyDynaBean orgmapbean=(LazyDynaBean)list.get(i);
				 childid=orgmapbean.get("childid").toString();
				 codeitemid=orgmapbean.get("codeitemid").toString();
				 //
				
				 //
				 sql.delete(0,sql.length());				 
				 sql.append("select codeitemid from "+tablename+" where codeitemid='"+childid+"'");
				 List childlist=ExecuteSQL.executeMyQuery(sql.toString(),this.conn);	
				 if(childlist.isEmpty()&&childlist.size()<=0)
				 {
					 sql.delete(0,sql.length());					 
					 sql.append("select codeitemid from "+tablename+" where parentid='"+codeitemid+"' order by a0000,codeitemid,codesetid ");
					 List codeitemist=ExecuteSQL.executeMyQuery(sql.toString(),this.conn);
					 if(!codeitemist.isEmpty())
					 {
						 LazyDynaBean codeitembean=(LazyDynaBean)codeitemist.get(0);
						 childid=codeitembean.get("codeitemid").toString();
						 chlist=new ArrayList();
						 chlist.add(childid);
						 chlist.add(codeitemid);
						 arlist.add(chlist);
					 }
				 }
			 }
		 }
		 sql.delete(0,sql.length());
		 sql.append("update  "+tablename+" set childid=? where codeitemid=?");		 	 
		 try {
			dao.batchUpdate(sql.toString(), arlist);
			if(!this.bShowDept)
			{
				/* sql.delete(0,sql.length());
				 sql.append("select codeitemid from "+tablename+" where codeitemid=childid and parentid not in(select codeitemid from "+tablename+")");
				 List childlist=ExecuteSQL.executeMyQuery(sql.toString(),this.conn);	*/
				 sql.delete(0,sql.length());			 
				 sql.append("select codeitemid,childid,grade from "+tablename+" where codeitemid<>childid  order by a0000,codeitemid,codesetid");
				 list=ExecuteSQL.executeMyQuery(sql.toString(),this.conn);
				 for(int i=0;i<list.size();i++)
				 {
					 LazyDynaBean orgmapbean=(LazyDynaBean)list.get(i);
					 String parentid=orgmapbean.get("codeitemid").toString();
					 String grade=orgmapbean.get("grade").toString();
					 //System.out.println(parentid);
					 int gg= Integer.parseInt(grade)+1;
					 sql.delete(0,sql.length());	
					 sql.append("update "+tablename+" set parentid='"+parentid+"',grade="+gg+" where");
					 sql.append(" codeitemid in(select codeitemid from "+tablename+" a where a.codeitemid=a.childid");
					 sql.append(" and a.parentid not in(select codeitemid from "+tablename+" b where b.codeitemid=b.childid) and a.codeitemid like '"+parentid+"%')");
					 dao.update(sql.toString());
				 }
				 
			}
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		
		 
    }
    private String exportSqlFunctFront(String functionName)
    {
    	StringBuffer buf=new StringBuffer();
    	switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			 
			  break;
		  }
		  case Constant.DB2:
		  {
			  
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  if(functionName!=null&& "isnull".equals(functionName)) {
                  buf.append(" nvl(");
              }
			  break;
		  }
		
		}
        return buf.toString();
    	//return "";
    }
    private String exportSqlFunctEnd(String functionName)
    {
    	StringBuffer buf=new StringBuffer();
    	switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			 
			  break;
		  }
		  case Constant.DB2:
		  {
			  
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  if(functionName!=null&& "isnull".equals(functionName)) {
                  buf.append(" ,'xzcvbnm')");
              }
			  break;
		  }
		
		}
    	//return "";
    	return buf.toString();
    }
    /**
     * 删除部门多余的层级
     * @param srcTab
     */
    private void filtrateDeptLevel(String srcTab)
    {
    	/*KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
    	String destTab=srcTab+"_UM_Level";
    	String strFldlst="codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000";
    	String strWhere="codesetid='UM'";
    	String strGroupBy="order by a0000,codeitemid,codesetid";
    	kqUtilsClass.createTempTable(srcTab,destTab,strFldlst,strWhere,strGroupBy);*/
    	/*if(this.iDeptlevel<1)
    		return;
    	String sql="select codeitemid from "+srcTab+" where layer>"+this.iDeptlevel+" and codesetid='UM'";
    	ContentDAO dao=new ContentDAO(this.conn);
    	try {
			RowSet rs=dao.search(sql);
		    while(rs.next())
		    {
		    	sql="delete from "+srcTab+" where codeitemid like '"+rs.getString("codeitemid")+"%'";
		    	dao.delete(sql, new ArrayList());
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	if(this.iDeptlevel<1&&this.iUnitlevel<1) {
            return;
        }
    	String sql="";
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rs =null;
    	try {
    		if(this.iUnitlevel>0){
    			sql="select codeitemid from "+srcTab+" where layer>"+this.iUnitlevel+" and codesetid='UN'";
				rs=dao.search(sql);
			    while(rs.next())
			    {
			    	sql="delete from "+srcTab+" where codeitemid like '"+rs.getString("codeitemid")+"%'";
			    	dao.delete(sql, new ArrayList());
			    }
    		}
			if(this.iDeptlevel>0){
				sql="select codeitemid from "+srcTab+" where layer>"+this.iDeptlevel+" and codesetid='UM'";
				rs=dao.search(sql);
			    while(rs.next())
			    {
			    	sql="delete from "+srcTab+" where codeitemid like '"+rs.getString("codeitemid")+"%'";
			    	dao.delete(sql, new ArrayList());
			    }
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
    }
	 private void setEmpGradeNull(String table)
	    {
	    	
	    	if(this.bShowDept)
	    	{
	    		if(this.iDeptlevel<1) {
                    return;
                }
	    		String sql="select Distinct parentid,grade,a0000  from "+table+" where codesetid='zz' and grade is not null order by grade desc";
	    		ContentDAO dao=new ContentDAO(this.conn);
	        	try {
	    			RowSet rs=dao.search(sql);		
	    			ArrayList list=new ArrayList();
	    		    while(rs.next())
	    		    {
	    		    	LazyDynaBean bean=new LazyDynaBean();
	    		    	bean.set("parentid", rs.getString("parentid"));
	    		    	bean.set("grade", rs.getString("grade"));
	    		    	bean.set("a0000", rs.getString("a0000")!=null?rs.getString("a0000"):"");
	    		    	list.add(bean);
	    		    }
	    		    for(int i=0;i<list.size();i++)
	    		    {
	    		    	LazyDynaBean bean=(LazyDynaBean)list.get(i);
	    		    	sql="update "+table+" set grade='"+(String)bean.get("grade")+"',a0000='"+(String)bean.get("a0000")+"' where codesetid='zz' and grade is null and parentid like '"+(String)bean.get("parentid")+"%'";
	    		        dao.update(sql);
	    		    }
	    		    sql="delete from "+table+" where codesetid='zz' and grade is null";
	    		    dao.delete(sql, new ArrayList());    		    
	    		} catch (SQLException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}else
	    	{
	    		String sql="delete from "+table+" where codesetid='zz' and grade is null";
	    		ContentDAO dao=new ContentDAO(this.conn);
	        	try {
	    			dao.delete(sql, new ArrayList());		    			
	    		   
	    		} catch (SQLException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	 }
	 /**
	  * 耦合垃圾数据，主要就是e01a1,e0122,b0110不存在的数据
	  */
	 private void couplingGarbageData(String dbname,ContentDAO dao)
	 {
		  StringBuffer sql=new StringBuffer();
		  
		  if(this.bShowPos)
		  {
			     sql.append("insert into ");
			     sql.append(tmpTableNamePrefix);
			     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.a0100,a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(tmpTableNamePrefix);
			     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
			     sql.append(tmpTableNamePrefix);
			     sql.append("organization b where b.codeitemid=a.e0122) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e0122=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and c.codeitemid in(");
			     sql.append("select Distinct e0122 from "+dbname+"A01 where a0100 not in(select a0100 from "+tmpTableNamePrefix+"organization where a0100 is not null)");
			     sql.append(" and e01a1 is not null and e01a1<>''");
			     sql.append(")");
			     sql.append(" and a0100 not in(select a0100 from "+tmpTableNamePrefix+"organization where a0100 is not null)");
			     try {
					dao.insert(sql.toString(),new ArrayList());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
		     sql.delete(0, sql.length());
		     sql.append("insert into ");
		     sql.append(tmpTableNamePrefix);
		     sql.append("organization(codesetid,a0100, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
		     sql.append("select 'zz',a.a0100,a.b0110 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',(select grade from ");
		     sql.append(tmpTableNamePrefix);
		     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
		     sql.append(tmpTableNamePrefix);
		     sql.append("organization b where b.codeitemid=a.b0110) from ");
		     sql.append(dbname);
		     sql.append("a01 a,organization c where a.b0110=c.codeitemid and  "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and c.codeitemid in(");
		     sql.append("select Distinct b0110 from "+dbname+"A01 where a0100 not in(select a0100 from "+tmpTableNamePrefix+"organization where a0100 is not null)");
		     sql.append(" and ((e0122 is not null and e0122<>'') or (a.e01a1 is not null or a.e01a1<>''))");
		     sql.append(")");
		     sql.append(" and a0100 not in(select a0100 from "+tmpTableNamePrefix+"organization where a0100 is not null)");
		     try {
		    	 //System.out.println(sql.toString());
				dao.insert(sql.toString(),new ArrayList());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
}
