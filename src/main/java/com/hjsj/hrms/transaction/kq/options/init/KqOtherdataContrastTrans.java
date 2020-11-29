package com.hjsj.hrms.transaction.kq.options.init;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.FormatValue;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 考勤其他数据对比
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 30, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class KqOtherdataContrastTrans  extends IBusiness {
	private boolean isNull(String str)
	{
		boolean boo=false;
		if(!(str==null|| "".equals(str)))
		{
			boo=true;
		}
		
		return boo;
	}
	private String z1="";
	private String z3="";
	public void execute() throws GeneralException 
	{
		String out=(String)this.getFormHM().get("out");//公出
		String otime=(String)this.getFormHM().get("outime");//加班
		String rest=(String)this.getFormHM().get("rest");//休息
		String scope=(String)this.getFormHM().get("scope");//时间范围标记
//		String tstart=(String)this.getFormHM().get("Tstart");//开始时间
//		String tend=(String)this.getFormHM().get("Tend");//结束时间
		String q19=(String)this.getFormHM().get("q19");//调班申请
		String q21=(String)this.getFormHM().get("q21");//替班申请
		String txsq=(String)this.getFormHM().get("txsq");//调休申请表
		String jqgl=(String)this.getFormHM().get("jqgl");//假期信息表
		String bzry=(String)this.getFormHM().get("bzry");//班组人员
		String kqbz=(String)this.getFormHM().get("kqbz");//考勤班组
		String rypb=(String)this.getFormHM().get("rypb");//员工排班信息表
		String ygsk=(String)this.getFormHM().get("ygsk");//员工刷卡数据表
		String staffl=(String)this.getFormHM().get("staffl");//员工刷卡数据表
		String staffy=(String)this.getFormHM().get("staffy");//员工刷卡数据表

		String tstart=(String)this.getFormHM().get("count_start");
		String tend=(String)this.getFormHM().get("count_end");
		FormatValue fv=new FormatValue();
		String sta=fv.formatItemType("D",0,10,tstart);
		String end=fv.formatItemType("D",0,10,tend);
		this.z1=sta;
		this.z3=end;		
		this.getFormHM().put("scope","1");
		String tempTable=createContrastTemp();
		ArrayList dblist=this.userView.getPrivDbList();
		boolean isCorrect=true;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String where2="";
		for(int i=0;i<dblist.size();i++)
		{
			String nbase=dblist.get(i).toString();
			String whereIN = RegisterInitInfoData.getWhereINSql(this.userView,nbase);			
			if(this.isNull(out))//公出表
			{
				where2=getWhere2("q13");
				isCorrect=isContrastAll("Q13",tempTable,nbase,whereIN,scope,where2,dao);
			}
			if(!isCorrect)
				break;
			if(this.isNull(otime))//加班
			{
				where2=getWhere2("q11");
				isCorrect=isContrastAll("Q11",tempTable,nbase,whereIN,scope,where2,dao);
			}
			if(!isCorrect)
				break;
			if(this.isNull(rest))//请假
			{
				where2=getWhere2("q15");
				isCorrect=isContrastAll("Q15",tempTable,nbase,whereIN,scope,where2,dao);
			}
			if(!isCorrect)
				break;
			if(this.isNull(q19))//调班管理
			{
				where2=getWhere2("q19");
				isCorrect=isContrastAll("Q19",tempTable,nbase,whereIN,scope,where2,dao);
			}
			if(!isCorrect)
				break;
			if(this.isNull(q21))//替班管理
		    {
				where2=getWhere2("q21");
				isCorrect=isContrastAll("Q21",tempTable,nbase,whereIN,scope,where2,dao);
		    }
			if(!isCorrect)
				break;
			if(this.isNull(txsq))//调休管理
			{
				where2=getWhere2("q25");
				isCorrect=isContrastAll("Q25",tempTable,nbase,whereIN,scope,where2,dao);
			}
			if(!isCorrect)
				break;
			if(this.isNull(jqgl))//假期信息表
			{
				where2=getWhere2("q17");
				isCorrect=isContrastAll("Q17",tempTable,nbase,whereIN,scope,"",dao);
			}
			if(this.isNull(bzry))//假期信息表
			{
				synchronizationInit("kq_group_emp",nbase,dao);				
			}
			if(!isCorrect)
				break;
			if(this.isNull(rypb)) //员工排班信息表 kq_employ_shift
			{
				where2=getWhere2("kq_employ_shift");
				isCorrect=isContrastAll2("kq_employ_shift",tempTable,nbase,whereIN,scope,"",dao,tstart,tend);
			}
			if(!isCorrect)
				break;
			if(this.isNull(ygsk)) //员工刷卡数据表 kq_originality_data
			{
				where2=getWhere2("kq_originality_data");
				isCorrect=isContrastAll2("kq_originality_data",tempTable,nbase,whereIN,scope,"",dao,tstart,tend);
			}
			if(!isCorrect)
				break;
			if(this.isNull(kqbz))  //考勤班组
			{
				where2=getWhere2("kq_group_emp");
				isCorrect=isContrastAll3("kq_group_emp",tempTable,nbase,whereIN,scope,"",dao);
			}
			if(this.isNull(staffl))  //员工明细
			{
				where2=getWhere2("Q03");
				isCorrect = isContrastAll2("Q03", tempTable, nbase, whereIN, scope, where2, dao, tstart, tend);
			}
			if(this.isNull(staffy))  //员工月汇总
			{
				where2=getWhere2("Q05");
				isCorrect = isContrastAll2("Q05", tempTable, nbase, whereIN, scope, where2, dao, tstart, tend);
			}
			if(!isCorrect)
				break;
		}
//		if(this.isNull(kqbz))//假期信息表
//		{
//			StringBuffer dsql=new StringBuffer();
//    		dsql.append("delete from kq_group_emp where group_id not in (select group_id from kq_shift_group)");
//    		try
//    		{
//    			dao.delete(dsql.toString(), new ArrayList());
//    		}catch(Exception e)
//    		{
//    			e.printStackTrace();
//    		}
//    		
//		}
		if(isCorrect)
			this.getFormHM().put("mess","2");
		else 
			this.getFormHM().put("mess","1");
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		kqUtilsClass.dropTable(tempTable);
	}
	private String getWhere2(String table)
	{
		StringBuffer where=new StringBuffer();
		where.append(table+"01 not in(select "+table+"01 from "+table+" where ");
		where.append(Sql_switcher.isnull(table+"z0","''")+"='01' and "+Sql_switcher.isnull(table+"z5","''")+"='03'");
		where.append(")");
		return where.toString();
	}
    private boolean isContrastAll(String table,String tempTable,String nbase,String whereIN,String scope,String where2,ContentDAO dao)throws GeneralException 
    {
    	    if(where2==null||where2.length()<=0)
    	    	where2="1=1";
    	    boolean isCorrect=true;
    	    StringBuffer sql=new StringBuffer();
//    		String column_z1="b."+table+"z1";
//    		String column_z3="b."+table+"z3";
    		String column_z1=table+"."+table+"z1";
    		String column_z3=table+"."+table+"z3";
    		sql.append("INSERT INTO " + tempTable + "(nbase,A0100,B0110,E0122,E01A1,A0101)");
   	        sql.append(" SELECT DISTINCT '"+nbase+"' as nbase,A0100,B0110,E0122,E01A1,A0101");
   	        sql.append(" FROM "+nbase+"A01 a ");
//   	       sql.append(" WHERE a0100 in (SELECT a0100 FROM "+table+" b WHERE 1=1");
   	        sql.append(" WHERE a0100 in (SELECT a0100 FROM "+table+" WHERE 1=1");
   	        StringBuffer sql2=new StringBuffer();
   	        if("2".equals(scope))
   	        {
   	        	/*
	    		 * 因为Q19与Q25表中 Z3字段格式为 2009.10.21 在用 to_tate 就会转成 1929-02-20 会出现 ORA-01861:
	    		 *  文字与格式字符串不匹配 错误;wy
	    		 */ 
   	        	if("q19".equalsIgnoreCase(table)|| "q25".equalsIgnoreCase(table))
   	        	{
   	        		sql2.append(" and (("+column_z1+">='"+(this.z1)+"'");
   	   	        	sql2.append(" and "+column_z1+"<='"+(this.z3)+"')");	
   	   	        	sql2.append(" or ("+column_z3+">'"+(this.z1)+"'");
   	   	        	sql2.append(" and "+column_z3+"<'"+(this.z3)+"')");	
   	   	        	sql2.append(" or ("+column_z1+"<='"+(this.z1)+"'");
   	   	        	sql2.append(" and "+column_z3+">='"+(this.z3)+"')");
   	   	        	sql2.append(")");
   	   	        	sql.append(sql2.toString());
   	        	}else
   	        	{
   	        		sql2.append(" and (("+column_z1+">="+Sql_switcher.dateValue(this.z1));
   	   	        	sql2.append(" and "+column_z1+"<="+Sql_switcher.dateValue(this.z3)+")");	
   	   	        	sql2.append(" or ("+column_z3+">"+Sql_switcher.dateValue(this.z1));
   	   	        	sql2.append(" and "+column_z3+"<"+Sql_switcher.dateValue(this.z3)+")");	
   	   	        	sql2.append(" or ("+column_z1+"<="+Sql_switcher.dateValue(this.z1));
   	   	        	sql2.append(" and "+column_z3+">="+Sql_switcher.dateValue(this.z3)+")");
   	   	        	sql2.append(")");
   	   	        	sql.append(sql2.toString());
   	        	}
//   	        	sql2.append(" and (("+column_z1+">="+Sql_switcher.dateValue(this.z1));
//   	        	sql2.append(" and "+column_z1+"<="+Sql_switcher.dateValue(this.z3)+")");	
//   	        	sql2.append(" or ("+column_z3+">"+Sql_switcher.dateValue(this.z1));
//   	        	sql2.append(" and "+column_z3+"<"+Sql_switcher.dateValue(this.z3)+")");	
//   	        	sql2.append(" or ("+column_z1+"<="+Sql_switcher.dateValue(this.z1));
//   	        	sql2.append(" and "+column_z3+">="+Sql_switcher.dateValue(this.z3)+")");
//   	        	sql2.append(")");
//   	        	sql.append(sql2.toString());
   	        } 
//   	        sql.append(" AND b.nbase='"+nbase+"'");
//   	        sql.append(" AND a.A0100=b.A0100 ");
//   	        sql.append(" AND ("+ Sql_switcher.isnull("a.B0110","''")+"<>"+ Sql_switcher.isnull("b.B0110","''")+"");
//   	        sql.append(" OR "+ Sql_switcher.isnull("a.E0122","''")+" <>"+ Sql_switcher.isnull("b.E0122","''")+"");
//   	        sql.append(" OR "+ Sql_switcher.isnull("a.E01A1","''")+" <>"+ Sql_switcher.isnull("b.E01A1","''")+"");
//   	        sql.append(" OR "+ Sql_switcher.isnull("a.A0101","''")+" <>"+ Sql_switcher.isnull("b.A0101","''")+"");
   	        sql.append(" AND "+table+".nbase='"+nbase+"'");
	        sql.append(" AND a.A0100="+table+".A0100 ");
	        sql.append(" AND ("+ Sql_switcher.isnull("a.B0110","'##'")+"<>"+ Sql_switcher.isnull(table+".B0110","'##'")+"");
	        sql.append(" OR "+ Sql_switcher.isnull("a.E0122","'##'")+" <>"+ Sql_switcher.isnull(table+".E0122","'##'")+"");
	        sql.append(" OR "+ Sql_switcher.isnull("a.E01A1","'##'")+" <>"+ Sql_switcher.isnull(table+".E01A1","'##'")+"");
	        sql.append(" OR "+ Sql_switcher.isnull("a.A0101","'##'")+" <>"+ Sql_switcher.isnull(table+".A0101","'##'")+"");
   	        sql.append(") and a0100 in(select a0100 "+whereIN+"))");
   	        
   	       String destTab=table;//目标表
 	       String srcTab=tempTable;//源表
 		   String strJoin=destTab+".A0100="+srcTab+".A0100 and "+srcTab+".nbase="+destTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
 		   String  strSet=destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+".E01A1="+srcTab+".E01A1`"+destTab+".A0101="+srcTab+".A0101";//更新串  xxx.field_name=yyyy.field_namex,....
 		   String strDWhere=destTab+".nbase='"+nbase+"'  and "+destTab+".a0100 in(select a0100 from "+srcTab+") "+sql2.toString();//更新目标的表过滤条件 		   
 		   String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,"");	  
 		   update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
		   
			try {			
				ArrayList list = new ArrayList();				
				dao.insert(sql.toString(),list);				
				dao.update(update);
				dao.update("delete from "+tempTable);
			} catch (Exception e) {
				isCorrect=false;
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
    	    
    	return isCorrect;
    }
    public String createContrastTemp()throws GeneralException
	 {
		 String table_name="t#" +userView.getUserName()+"_kq_ctt";
		 table_name=table_name.toLowerCase();
		 KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		 kqUtilsClass.dropTable(table_name);
		 DbWizard dbWizard =new DbWizard(this.getFrameconn());
		 Table table=new Table(table_name);		
		 Field temp = new Field("nbase","组织编号");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 Field temp1=new Field("a0100","人员编号");
		 temp1.setDatatype(DataType.STRING);
		 temp1.setLength(50);
		 temp1.setKeyable(false);			
		 temp1.setVisible(false);
		 table.addField(temp1);		
		 Field temp2=new Field("a0101","人员姓名");
		 temp2.setDatatype(DataType.STRING);
		 temp2.setLength(50);
		 temp2.setKeyable(false);			
		 temp2.setVisible(false);
		 table.addField(temp2);
		 temp2=new Field("b0110","单位");
		 temp2.setDatatype(DataType.STRING);
		 temp2.setLength(50);
		 temp2.setKeyable(false);			
		 temp2.setVisible(false);
		 table.addField(temp2);
		 temp2=new Field("e0122","部门");
		 temp2.setDatatype(DataType.STRING);
		 temp2.setLength(50);
		 temp2.setKeyable(false);			
		 temp2.setVisible(false);
		 table.addField(temp2);
		 temp2=new Field("e01a1","职位");
		 temp2.setDatatype(DataType.STRING);
		 temp2.setLength(50);
		 temp2.setKeyable(false);			
		 temp2.setVisible(false);
		 table.addField(temp2);
		 try
		 {
			dbWizard.createTable(table);
		 }catch(Exception e)
		 {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		 }	
		 return table_name;
	 }
    public void synchronizationInit(String destTab,String nbase,ContentDAO dao)throws GeneralException
	{
		
		 
			 String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);				
			 String srcTab=nbase+"A01";//源表
			 String strJoin=destTab+".A0100="+srcTab+".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
			 String  strSet=destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+".E01A1="+srcTab+".E01A1";//更新串  xxx.field_name=yyyy.field_namex,....
			 String strDWhere=destTab+".nbase='"+nbase+"'";//更新目标的表过滤条件
			 String strSWhere=srcTab+".a0100 in(select a0100 "+whereIN+")";//源表的过滤条件  
			 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);	
			 String othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
			 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);
			
			try {			
				dao.update(update);
			} catch (Exception e) {
				e.printStackTrace();
				//throw GeneralExceptionHandler.Handle(e);
				return;
			}
		
	}
    private boolean isContrastAll2(String table,String tempTable,String nbase,String whereIN,String scope,String where2,ContentDAO dao,String tstart,String tend)throws GeneralException 
    {
    	tstart=tstart.replaceAll("-","\\.");
    	tend=tend.replaceAll("-","\\.");
	    if(where2==null||where2.length()<=0)
	    	where2="1=1";
	    boolean isCorrect=true;
	    StringBuffer sql=new StringBuffer();
	    String column_z1="";
    	String column_z3="";
	    if("kq_employ_shift".equalsIgnoreCase(table)|| "Q03".equalsIgnoreCase(table)|| "Q05".equalsIgnoreCase(table))
	    {
	    	column_z1=table+".Q03Z0";
	    	column_z3=table+".Q03Z0";
	    }else
	    {
	    	column_z1=table+".work_date";
	    	column_z3=table+".work_date";
	    }
		sql.append("INSERT INTO " + tempTable + "(nbase,A0100,B0110,E0122,E01A1,A0101)");
        sql.append(" SELECT DISTINCT '"+nbase+"' as nbase,A0100,B0110,E0122,E01A1,A0101");
        sql.append(" FROM "+nbase+"A01 a ");
        //sql.append(" WHERE a0100 in (SELECT a0100 FROM "+table+" b WHERE 1=1");
        sql.append(" WHERE a0100 in (SELECT a0100 FROM "+table+" WHERE 1=1");
        StringBuffer sql2=new StringBuffer();
        if("2".equals(scope))
        {
        	if ("Q05".equalsIgnoreCase(table))
			{
        		KqUtilsClass kqUtilsClass = new KqUtilsClass();
        		String where_time = kqUtilsClass.procWhere(tstart, "Q03z0", tend, "Q03z0", 3);
				sql2.append(" and" + where_time);
			}else 
			{
				sql2.append(" and ("+column_z1+">='"+tstart+"'");
				sql2.append(" and "+column_z3+"<='"+tend+"'");	
				sql2.append(")");
			}
        	sql.append(sql2.toString());
        } 
        sql.append(" AND "+table+".nbase='"+nbase+"'");
        sql.append(" AND a.A0100="+table+".A0100 ");
        // 汉字，修改前为空字符'',在oracle中，比较一个空字符是得不到任何结果的。
        sql.append(" AND ("+ Sql_switcher.isnull("a.B0110","'##'")+"<>"+ Sql_switcher.isnull(table+".B0110","'##'")+"");
        sql.append(" OR "+ Sql_switcher.isnull("a.E0122","'##'")+" <>"+ Sql_switcher.isnull(table+".E0122","'##'")+"");
        sql.append(" OR "+ Sql_switcher.isnull("a.E01A1","'##'")+" <>"+ Sql_switcher.isnull(table+".E01A1","'##'")+"");
        sql.append(" OR "+ Sql_switcher.isnull("a.A0101","'##'")+" <>"+ Sql_switcher.isnull(table+".A0101","'##'")+"");
        sql.append(") and a0100 in(select a0100 "+whereIN+"))");
        
        String destTab=table;//目标表
        String srcTab=tempTable;//源表
	    String strJoin=destTab+".A0100="+srcTab+".A0100 and "+srcTab+".nbase="+destTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
	    String  strSet=destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+".E01A1="+srcTab+".E01A1`"+destTab+".A0101="+srcTab+".A0101";//更新串  xxx.field_name=yyyy.field_namex,....
	    String strDWhere=destTab+".nbase='"+nbase+"'  and "+destTab+".a0100 in(select a0100 from "+srcTab+") "+sql2.toString();//更新目标的表过滤条件 		   
	    String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,"");	  
	    update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
	   
		try {			
			ArrayList list = new ArrayList();				
			dao.insert(sql.toString(),list);				
			dao.update(update);
			dao.update("delete from "+tempTable);
		} catch (Exception e) {
			isCorrect=false;
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	    
    	return isCorrect;
    }
    private boolean isContrastAll3(String table,String tempTable,String nbase,String whereIN,String scope,String where2,ContentDAO dao)throws GeneralException 
    {
    	
    	    if(where2==null||where2.length()<=0)
    	    	where2="1=1";
    	    boolean isCorrect=true;
    	    StringBuffer sql=new StringBuffer();
//    	    String column_z1="";
//	    	String column_z3="";
//    	    if(table.equalsIgnoreCase("kq_employ_shift"))
//    	    {
//    	    	column_z1=table+".Q03Z0";
//    	    	column_z3=table+".Q03Z0";
//    	    }else
//    	    {
//    	    	column_z1=table+".work_date";
//    	    	column_z3=table+".work_date";
//    	    }
    		sql.append("INSERT INTO " + tempTable + "(nbase,A0100,B0110,E0122,E01A1,A0101)");
   	        sql.append(" SELECT DISTINCT '"+nbase+"' as nbase,A0100,B0110,E0122,E01A1,A0101");
   	        sql.append(" FROM "+nbase+"A01 a ");
//   	       sql.append(" WHERE a0100 in (SELECT a0100 FROM "+table+" b WHERE 1=1");
   	        sql.append(" WHERE a0100 in (SELECT a0100 FROM "+table+" WHERE 1=1");
   	        StringBuffer sql2=new StringBuffer();
//   	        if(scope.equals("2"))
//   	        {
//   	        		sql2.append(" and ("+column_z1+">='"+tstart+"'");
//   	   	        	sql2.append(" and "+column_z1+"<="+tend+"'");	
//   	   	        	sql2.append(")");
//   	   	        	sql.append(sql2.toString());
//   	        } 
   	        sql.append(" AND "+table+".nbase='"+nbase+"'");
	        sql.append(" AND a.A0100="+table+".A0100 ");
	        sql.append(" AND ("+ Sql_switcher.isnull("a.B0110","'##'")+"<>"+ Sql_switcher.isnull(table+".B0110","'##'")+"");
	        sql.append(" OR "+ Sql_switcher.isnull("a.E0122","'##'")+" <>"+ Sql_switcher.isnull(table+".E0122","'##'")+"");
	        sql.append(" OR "+ Sql_switcher.isnull("a.E01A1","'##'")+" <>"+ Sql_switcher.isnull(table+".E01A1","'##'")+"");
	        sql.append(" OR "+ Sql_switcher.isnull("a.A0101","'##'")+" <>"+ Sql_switcher.isnull(table+".A0101","'##'")+"");
   	        sql.append(") and a0100 in(select a0100 "+whereIN+"))");
   	        
   	       String destTab=table;//目标表
 	       String srcTab=tempTable;//源表
 		   String strJoin=destTab+".A0100="+srcTab+".A0100 and "+srcTab+".nbase="+destTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
 		   String  strSet=destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+".E01A1="+srcTab+".E01A1`"+destTab+".A0101="+srcTab+".A0101";//更新串  xxx.field_name=yyyy.field_namex,....
 		   String strDWhere=destTab+".nbase='"+nbase+"'  and "+destTab+".a0100 in(select a0100 from "+srcTab+") "+sql2.toString();//更新目标的表过滤条件 		   
 		   String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,"");	  
 		   update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
		   
			try {			
				ArrayList list = new ArrayList();				
				dao.insert(sql.toString(),list);				
				dao.update(update);
				dao.update("delete from "+tempTable);
			} catch (Exception e) {
				isCorrect=false;
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
    	    
    	return isCorrect;
    }
}
