package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.OperateDataTable;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
/**
 * 补刷考勤卡
 * <p>Title:RepairKqCard.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 18, 2007 3:55:29 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class RepairKqCard {
	private Connection conn;
	private UserView userView;
	private String ip_adr;
	private String into_flag;
	private String causation;
	private Date oper_time;
	private final String new_add_columns="inout_flag,oper_cause,oper_user,oper_time,oper_mach";//出入标志，补刷原因，补刷操作员，补刷时间，机器ip或机器名
	public RepairKqCard()
	{		
	}
	public RepairKqCard(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}
	public RepairKqCard(Connection conn,UserView userView,String ip_adr,String into_flag,String causation,Date oper_time)
	{
		this.conn=conn;
		this.userView=userView;
		this.ip_adr=ip_adr;
		this.into_flag=into_flag;
		this.causation=causation;
		this.oper_time=oper_time;
	}
	/**
	 * 建立补刷刷卡人员信息临时表
	 * @param userView
	 * @param conn
	 * @param cardno_field
	 * @return
	 * @throws GeneralException
	 */
	 public String ceaterRepairTempEmp(UserView userView,String cardno_field) throws GeneralException
	 {
		 if(cardno_field==null||cardno_field.length()<=0) {
             throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.card.nocreate.card_no"),"",""));
         }
		 String table_name="t#_kq_rep_"+userView.getUserName();
		 table_name=table_name.toLowerCase();
		 DbWizard dbWizard =new DbWizard(conn);
		 Table table=new Table(table_name);			
		 if(dbWizard.isExistTable(table_name,false))
		 {
			OperateDataTable operateDataTable=new OperateDataTable(this.conn);
			operateDataTable.dropTable(table_name);
		 }
		 Field temp = new Field("nbase","人员库");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 Field temp1=new Field("A0100","人员编号");
		 temp1.setDatatype(DataType.STRING);
		 temp1.setLength(50);
		 temp1.setKeyable(false);			
		 temp1.setVisible(false);
		 table.addField(temp1);		
		 temp=new Field("B0110","单位");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("E0122","部门");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("E01A1","职位");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("A0101","姓名");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("g_no","工号");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);		
		 temp=new Field("card_no","考勤卡号");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);		
		 temp=new Field("flag","标志");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(10);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("inout_flag","出入标志");
		 temp.setDatatype(DataType.INT);		
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("oper_cause","补刷原因");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(250);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("oper_user","补刷操作员");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("oper_time","补刷操作时间");
		 temp.setDatatype(DataType.DATE);		
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 temp=new Field("oper_mach","补刷操作ip");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
				try
				{
					dbWizard.createTable(table);
				}catch(Exception e)
				{
					e.printStackTrace();
				}	
			/**重新加载数据模型*/

			DBMetaModel dbmodel=new DBMetaModel(conn);
			dbmodel.reloadTableModel(table_name);	  
			return table_name;
	 }
	 /**
	  * 补刷刷卡人员信息临时表字段
	  * @param cardno_field
	  * @return
	  */
	 public String getRepairTempEmpColumn(String cardno_field)
	 {
		 StringBuffer column=new StringBuffer();
		 column.append("nbase,a0100,b0110,e0122,");
		 column.append("e01a1,a0101,");
		 column.append("card_no,");
		 column.append("flag");
		 return column.toString();
	 }
	 
	 /**
	  * 补刷刷卡人员信息临时表字段
	  * @param cardno_field
	  * @return
	  */
	 public String getRepairTempEmpColumn1(String cardno_field)
	 {
		 StringBuffer column=new StringBuffer();
		 column.append("nbase,a0100,b0110,e0122,");
		 column.append("e01a1,a0101,g_no,");
		 column.append("card_no,");
		 column.append("flag");
		 return column.toString();
	 }
	 /**
	  * 临时表插入人员信息
	  * @param cardno_field
	  * @param kq_type
	  * @param temp_table
	  * @param a_code
	  * @param nbaselist
	  */
	 public void insertRepairTempEmpData(String cardno_field,String g_no,String kq_type,String temp_table,String a_code,ArrayList nbaselist,
			 String start_date,String end_date, String noCardFlag)
	 {
		 KqCardData kqCardData =new KqCardData();
		 String nbase="";
		 String whereIN="";
		 String column=getRepairTempEmpColumn1(cardno_field);
		 StringBuffer sql=null;
		 ContentDAO dao=new ContentDAO(this.conn);
		 ArrayList list=new ArrayList();
		 if(start_date!=null&&start_date.length()>0) {
             start_date=start_date.replaceAll("-","\\.");
         }
	     if(end_date!=null&&end_date.length()>0) {
             end_date=end_date.replaceAll("-","\\.");
         }
	     String start_time = "00:00";
	     String end_time = "23:59";
	     String z1 =start_date+" "+start_time;
	     String z3=end_date+" "+end_time;
		 try
		 {
			 for(int i=0;i<nbaselist.size();i++)
			 {
				 nbase=nbaselist.get(i).toString();	
				 whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
				 String where_code=kqCardData.getCodeItemWhere(a_code,nbase);
				 sql=new StringBuffer();
				 sql.append("insert into "+temp_table);
				 sql.append(" ("+column+")");
				 sql.append("select '"+nbase+"',a0100,b0110,e0122,");
				 sql.append("e01a1,a0101," + g_no + "," + cardno_field + ",1");
				 sql.append(" from "+nbase+"a01 a");
				 sql.append(" where 1=1 and ");		   
				 sql.append(where_code);
				 sql.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") "); 
				 //sql.append(" and ("+cardno_field+" is not null or "+cardno_field+"<>'')");
				 sql.append(" and "+Sql_switcher.isnull(cardno_field, "'##'")+"<>'##'");
				 sql.append(" and "+kq_type+"='02'");
				 sql.append(" and NOT EXISTS(SELECT 1 FROM "+temp_table+" b");
				 sql.append(" WHERE  a.A0100=b.A0100 and nbase='"+nbase+"')");
				 
				 if("1".equalsIgnoreCase(noCardFlag)){
					sql.append(" and NOT EXISTS (");
					sql.append("select 1 ");
					sql.append(" from kq_originality_data ");
					sql.append(" where kq_originality_data.a0100=a.a0100 and ");         
					sql.append(where_code);
					sql.append(" and work_date>='"+start_date+"'");
					sql.append(" and work_time>='"+start_time+"'");
					sql.append(" and work_date<='"+end_date+"'");
					sql.append(" and work_time<='"+end_time+"'");        
					sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");           
					sql.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") ");  
					sql.append(")");
					/***公出没有刷卡的***/
					sql.append(" and NOT EXISTS(");
					sql.append(" select 1 from q13 where q13.a0100=a.a0100 and ");
					sql.append(where_code);
					sql.append(" and ((q13z1>="+Sql_switcher.dateValue(z1));
					sql.append(" and q13z1<="+Sql_switcher.dateValue(z3)+")");    
					sql.append(" or (q13z3>"+Sql_switcher.dateValue(z1));
					sql.append(" and q13z3<"+Sql_switcher.dateValue(z3)+")"); 
					sql.append(" or (q13z1<="+Sql_switcher.dateValue(z1));
					sql.append(" and q13z3>="+Sql_switcher.dateValue(z3)+")");
					sql.append(")");
					sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");           
					sql.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") ");  
					sql.append(")");
					/***请假没有刷卡的***/
					sql.append(" and NOT EXISTS(");
					sql.append(" select 1 from q15 where q15.a0100=a.a0100 and ");
					sql.append(where_code);
					sql.append(" and ((q15z1>="+Sql_switcher.dateValue(z1));
					sql.append(" and q15z1<="+Sql_switcher.dateValue(z3)+")");    
					sql.append(" or (q15z3>"+Sql_switcher.dateValue(z1));
					sql.append(" and q15z3<"+Sql_switcher.dateValue(z3)+")"); 
					sql.append(" or (q15z1<="+Sql_switcher.dateValue(z1));
					sql.append(" and q15z3>="+Sql_switcher.dateValue(z3)+")");
					sql.append(")");
					sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");           
					sql.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") ");            
					sql.append(")");
				 } 
				 //System.out.println(sql.toString());//必须机器考勤方式，有卡号
				 dao.insert(sql.toString(),list);
			 }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	 /**
	  * 临时表插入人员信息
	  * @param cardno_field
	  * @param kq_type
	  * @param temp_table  //临时表
	  * @param a_code
	  * @param nbaselist
	  * table 表名 kt_XX_dd
	  */
	 public void insertRepairTempEmpData2(String cardno_field,String kq_type,String temp_table,ArrayList listvalue,String table)
	 {
		 String column=getRepairTempEmpColumn(cardno_field);		 
		 StringBuffer sql=new StringBuffer();
		 StringBuffer update=new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.conn);
		 ArrayList list=new ArrayList();
		 try
		 {
			 String a0100="";
			 String nbase="";
			 for(int p=0;p<listvalue.size();p++)
			 {
				 LazyDynaBean rec=(LazyDynaBean)listvalue.get(p); 
				 a0100=(String)rec.get("a0100");
				 nbase=(String)rec.get("nbase");
				 //q03z0=(String)rec.get("q03z0");
				 if(p==0)
				 {
					 sql.append("select '"+nbase+"',a0100,b0110,e0122,e01a1,a0101,"+cardno_field+",1 from "+nbase+"a01");
					 sql.append(" where a0100='"+a0100+"'");
				 }else
				 {
					 sql.append(" union all select '"+nbase+"',a0100,b0110,e0122,e01a1,a0101,"+cardno_field+",1 from "+nbase+"a01");
					 sql.append(" where a0100='"+a0100+"'");
				 }
			 }
			 update.append("insert into "+temp_table);
			 update.append(" ("+column+") ");
			 update.append(sql.toString());
			 dao.insert(update.toString(),list);

		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	 /**
	  * 临时表插入人员信息
	  * @param cardno_field
	  * @param kq_type
	  * @param temp_table
	  * @param a_code
	  * @param nbaselist
	  */
	 public void insertRepairTempEmpData(String cardno_field,String g_no,String kq_type,String temp_table,ArrayList  selectedinfolist)
	 {
		 
		 String nbase="";
		 String column=getRepairTempEmpColumn1(cardno_field);
		 StringBuffer sql=null;
		 ContentDAO dao=new ContentDAO(this.conn);
		 ArrayList list=new ArrayList();
		 try
		 {
			 for(int i=0;i<selectedinfolist.size();i++)
			 {
				 LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
				 String a0100=rec.get("a0100").toString();
				 nbase=rec.get("nbase").toString();				
				 sql=new StringBuffer();
				 sql.append("insert into "+temp_table);
				 sql.append(" ("+column+")");
				 sql.append("select '"+nbase+"',a0100,b0110,e0122,");
				 sql.append("e01a1,a0101," + g_no + "," + cardno_field + ",1");
				 sql.append(" from "+nbase+"a01 a");
				 sql.append(" where 1=1 "); 
				 sql.append(" and a0100='"+a0100+"'"); 
				 //sql.append(" and ("+cardno_field+" is not null or "+cardno_field+"<>'')");
				 sql.append(" and "+Sql_switcher.isnull(cardno_field, "'##'")+"<>'##'");
				 sql.append(" and "+kq_type+"='02'");
				 sql.append(" and NOT EXISTS(SELECT 1 FROM "+temp_table+" b");
				 sql.append(" WHERE  a.A0100=b.A0100 and nbase='"+nbase+"')");	
				 //System.out.println(sql.toString());
				 dao.insert(sql.toString(),list);
			 }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	 /**************进行补刷*******************/
	 /**
	  * 建立刷卡临时表
	  */
	 public String createTemp()
	 {
		 KqCardData kqCardData=new KqCardData(this.userView,this.conn);
		 String originality_Tab="kq_originality_data"; 
         String temp_Tab=kqCardData.createTemp(originality_Tab);
         return temp_Tab;
	 }
	 /**
	  * 建立时间临时表
	  * @return
	  */
	 
	 public String createTimeTemp(String table_name)
	 {
		 if(table_name==null||table_name.length()<=0)
		 {
//			 table_name="kq_t_"+userView.getUserName();
			 table_name = "t#"+userView.getUserName()+"_kq_t";
			 table_name=table_name.toLowerCase();
		 }		 
		 DbWizard dbWizard =new DbWizard(conn);
		 Table table=new Table(table_name);			
		 if(dbWizard.isExistTable(table_name,false))
		 {
			OperateDataTable operateDataTable=new OperateDataTable(this.conn);
			operateDataTable.dropTable(table_name);
		 }
		 Field temp = new Field("sDate","工作日期");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 Field temp1=new Field("work_time","工作时间");
		 temp1.setDatatype(DataType.STRING);
		 temp1.setLength(50);
		 temp1.setKeyable(false);			
		 temp1.setVisible(false);
		 table.addField(temp1);		
		 temp=new Field("time_status","状态");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(10);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 try
			{
				dbWizard.createTable(table);
			}catch(Exception e)
			{
				e.printStackTrace();
			}	
		/**重新加载数据模型*/

		DBMetaModel dbmodel=new DBMetaModel(conn);
		dbmodel.reloadTableModel(table_name);	
		//System.out.println(table_name);
		return table_name;
	 }
	 /**
	  * 修改补刷人员临时表,删除没有选定的
	  * @param selectedinfolist
	  * @param temp_emp_table
	  */
	 public void updateTempEmpFlag(ArrayList selectedinfolist,String temp_emp_table)
	 {
		 ArrayList list=new ArrayList();		
		 //A0100,nbase,A0101,B0110,E0122,E01A1,
		 for(int i=0;i<selectedinfolist.size();i++)
		 {
			 LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
			 ArrayList onew_list=new ArrayList();
			 onew_list.add("2");
			 onew_list.add((String)rec.get("a0100"));
			 onew_list.add((String)rec.get("nbase"));			
			 list.add(onew_list);
		 }
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.conn);
			 ArrayList updatelist=new ArrayList();
			 updatelist.add("1");
			 /**每次取选中数据时，都先update成没有选定的**/
			 String updateSQL="update "+temp_emp_table+" set flag=?";
			 dao.update(updateSQL,updatelist);
			 
			 String update ="update "+temp_emp_table+" set flag=? where a0100=? and nbase=?";
			 dao.batchUpdate(update,list);
//			 list=new ArrayList();
//			 list.add("1");
//			 /**删除没有选定的**/
//			 String deleteSQL="delete from "+temp_emp_table+" where flag=?";
//			 dao.delete(deleteSQL,list);
			 ArrayList up_list=new ArrayList();			 
			 up_list.add(this.ip_adr);
			 up_list.add(this.into_flag);
			 up_list.add(this.causation);
			 up_list.add(DateUtils.getTimestamp(DateUtils.format(this.oper_time,"yyyy-MM-dd HH:mm"),"yyyy-MM-dd HH:mm"));
			 up_list.add(this.userView.getUserFullName());
			 update="update "+temp_emp_table+" set oper_mach=?,inout_flag=?,oper_cause=?,oper_time=?,oper_user=? where flag='2'";
			 dao.update(update,up_list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }		 
	 }
	 //
	 /**
	  * 修改补刷人员临时表,删除没有选定的
	  * @param selectedinfolist
	  * @param temp_emp_table
	  */
	 public void updateTempEmpInout_flag(String temp_emp_table,String inout_flag)
	 {
		 try
		 {
			 
			 ContentDAO dao=new ContentDAO(this.conn);
			 ArrayList up_list=new ArrayList();			 
			 up_list.add(inout_flag);			
			 String update="update "+temp_emp_table+" set inout_flag=?";
			 dao.update(update,up_list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }		 
	 }
	 /************循环补刷卡*************/
	 /***
	  * 循环补刷卡添加时间临时表
	  * @param cycle_date
	  * @param cycle_time
	  * @param cycle_num
	  */
	 public void insertCycleTimeTemp(String time_table,String cycle_date,String cycle_time,String cycle_num)throws GeneralException
	 {
		 int diff=Integer.parseInt(cycle_num);		 
		 String op_date_to=cycle_date.replaceAll("-","\\.");
		 if(op_date_to==null||op_date_to.length()<=0) {
             throw GeneralExceptionHandler.Handle(new GeneralException("","没有补刷卡日期！","",""));
         }
		 Date d_date=DateUtils.getDate(op_date_to,"yyyy.MM.dd");
		 StringBuffer insertSQL=new StringBuffer();
		 insertSQL.append("insert into "+time_table);
		 insertSQL.append(" (sDate,work_time,time_status)");
		 insertSQL.append(" values (?,?,?)");
		 ArrayList list=new ArrayList();
		 for(int i=0;i<diff;i++)
		 {
			 if(i>0)
  	    	  {
				 d_date=DateUtils.addDays(d_date,1);    	    		  
	    	     op_date_to=DateUtils.format(d_date,"yyyy.MM.dd");   	    	      
  	    	  }
			  ArrayList one_list=new ArrayList();
			  one_list.add(op_date_to);
			  one_list.add(cycle_time);
			  one_list.add("0");
			  list.add(one_list);
		 }
		 ContentDAO dao=new ContentDAO(this.conn);
		 try
		 {
			 dao.batchInsert(insertSQL.toString(),list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
	 }
	 /**
		 * 建立按循环补刷卡临时表
		 * @param temp_emp_table
		 * @param time_table
		 * @return
		 */
		 public String createCycleTemp(String temp_emp_table,String time_table)
		 {
			 String table_name="t#"+userView.getUserName()+"_kq_re";
			 DbWizard dbWizard =new DbWizard(conn);
			 if(dbWizard.isExistTable(table_name,false))
			 {
				OperateDataTable operateDataTable=new OperateDataTable(this.conn);
				operateDataTable.dropTable(table_name);
			 }
			 StringBuffer columns=new StringBuffer();
			 columns.append("nbase,A0100,A0101,B0110,E0122,E01A1,card_no,");
			 columns.append("sDate,work_time,inout_flag,oper_cause,oper_user,oper_time,oper_mach");
			 StringBuffer sqlStr=new StringBuffer();
			 sqlStr.append("(select nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate,work_time,inout_flag,oper_cause,oper_user,oper_time,oper_mach from ");
			 sqlStr.append(" "+temp_emp_table+","+time_table);
			 sqlStr.append(" WHERE " + temp_emp_table +".flag = 2) sss");
			 KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
			 kqUtilsClass.createTempTable(sqlStr.toString(), table_name, columns.toString(),"","");			 
			 return table_name;
		 }
		 /**
			 * 数据处理补刷卡，建立按循环补刷卡临时表
			 * @param temp_emp_table
			 * @param time_table
			 * @return
			 */
			 public String createCycleTemp2(String temp_emp_table,String time_table)
			 {
				 String table_name="t#"+userView.getUserName()+"_kq_re";
				 DbWizard dbWizard =new DbWizard(conn);
				 if(dbWizard.isExistTable(table_name,false))
				 {
					OperateDataTable operateDataTable=new OperateDataTable(this.conn);
					operateDataTable.dropTable(table_name);
				 }
				 StringBuffer columns=new StringBuffer();
				 columns.append("nbase,A0100,A0101,B0110,E0122,E01A1,card_no,");
				 columns.append("sDate,work_time,inout_flag,oper_cause,oper_user,oper_time,oper_mach");
				 StringBuffer sqlStr=new StringBuffer();
				 sqlStr.append("(select nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate,work_time,inout_flag,oper_cause,oper_user,oper_time,oper_mach from ");
				 sqlStr.append(" "+temp_emp_table+","+time_table);
				 sqlStr.append(" WHERE " + temp_emp_table +".flag = 1) sss");
				 KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
				 kqUtilsClass.createTempTable(sqlStr.toString(), table_name, columns.toString(),"","");			 
				 return table_name;
			 }
		 /**
		  * 修改按循环补刷临时数据，添加到刷卡原始数据表里面
		  * @param tmpTab
		  */
		 public void insertCycleTemp(String tmpTab)
		 {
			 StringBuffer sql=new StringBuffer();		
			 sql.append("INSERT INTO kq_originality_data(nbase,A0100,A0101,B0110,E0122,E01A1,card_no,work_date,location,work_time,sp_flag,datafrom,"+this.new_add_columns+")");
			 sql.append(" select nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate AS work_date,'补刷',work_time,'02','1',"+this.new_add_columns+" ");
			 sql.append(" from "+tmpTab);
			 sql.append(" where NOT EXISTS(SELECT 1 FROM kq_originality_data a");
			 sql.append(" WHERE a.nbase=" + tmpTab + ".nbase AND a.A0100=" + tmpTab + ".A0100");
			 sql.append(" AND a.work_date=" + tmpTab + ".sDate AND a.work_time=" + tmpTab + ".work_time)");
			 ArrayList list=new ArrayList();
			 ContentDAO dao=new ContentDAO(this.conn);
			 try
			 {
				 dao.insert(sql.toString(),list);
	             //zxj 20150401 增加一步，既然是考勤员来补签，那么如果刚好个人也有补签记录并且未提交，那么一并置为已批状态
                 updateDraftCardDataForCycleRepair(dao, tmpTab, "02");
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 
		 }
		 
		 /**
		  * 数据处理补刷卡，添加到刷卡原始数据表里面
		  * @param tmpTab
		  */
		 public void insertCycleTemp2(String tmpTab)
		 {
			 StringBuffer sql=new StringBuffer();		
			 sql.append("INSERT INTO kq_originality_data(nbase,A0100,A0101,B0110,E0122,E01A1,card_no,work_date,location,work_time,sp_flag,datafrom,"+this.new_add_columns+")");
			 sql.append(" select nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate AS work_date,'补刷',work_time,'03','1',"+this.new_add_columns+" ");
			 sql.append(" from "+tmpTab);
			 sql.append(" where NOT EXISTS(SELECT 1 FROM kq_originality_data a");
			 sql.append(" WHERE a.nbase=" + tmpTab + ".nbase AND a.A0100=" + tmpTab + ".A0100");
			 sql.append(" AND a.work_date=" + tmpTab + ".sDate AND a.work_time=" + tmpTab + ".work_time)");
			 ArrayList list=new ArrayList();
			 ContentDAO dao=new ContentDAO(this.conn);
			 try
			 {
				 dao.insert(sql.toString(),list);
				 
	             //zxj 20150401 增加一步，既然是考勤员来补签，那么如果刚好个人也有补签记录并且未提交，那么一并置为已批状态
                 updateDraftCardDataForCycleRepair(dao, tmpTab, "03");
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
		 
		 private void updateDraftCardDataForCycleRepair(ContentDAO dao, String tmpTab, String spFlag) throws Exception {
		     //zxj 20150401 增加一步，既然是考勤员来补签，那么如果刚好个人也有补签记录并且未提交，那么一并置为待批状态
             StringBuffer sql = new StringBuffer();
             sql.append("UPDATE kq_originality_data");
             sql.append(" SET sp_flag='").append(spFlag).append("'");
             sql.append(" WHERE sp_flag='01'");
             sql.append(" AND EXISTS(SELECT 1 FROM " + tmpTab);
             sql.append(" WHERE kq_originality_data.nbase=" + tmpTab + ".nbase");
             sql.append(" AND kq_originality_data.A0100=" + tmpTab + ".A0100");
             sql.append(" AND kq_originality_data.work_date=" + tmpTab + ".sDate");
             sql.append(" AND kq_originality_data.work_time=" + tmpTab + ".work_time)");
             dao.update(sql.toString());
		 
		 }
     /************按班次补刷*************/
	 /**
	  * 按条件添加时间临时表
	  * @param statr_date
	  * @param end_date
	  */
	 public void insertInstanceTimeTemp(String time_table,String statr_date_str,String end_date_str)throws GeneralException
	 {
		 statr_date_str=statr_date_str.replaceAll("-","\\.");
		 end_date_str=end_date_str.replaceAll("-","\\.");
		 Date statr_date=DateUtils.getDate(statr_date_str,"yyyy.MM.dd");
		 Date end_date=DateUtils.getDate(end_date_str,"yyyy.MM.dd");
		 int diff=RegisterDate.diffDate(statr_date,end_date);  
		 Date d_date=statr_date;
		 String op_date_to=statr_date_str;
		 StringBuffer insertSQL=new StringBuffer();
		 insertSQL.append("insert into "+time_table);
		 insertSQL.append(" (sDate,time_status)");
		 insertSQL.append(" values (?,?)");
		 ArrayList list=new ArrayList();
		 for(int i=0;i<=diff;i++)
	     {  
			 if(i>0)
 	    	  {
				 d_date=DateUtils.addDays(d_date,1);    	    		  
	    	     op_date_to=DateUtils.format(d_date,"yyyy.MM.dd");   	    	      
 	    	  }
			  ArrayList one_list=new ArrayList();
			  one_list.add(op_date_to);			  
			  one_list.add("0");
			  list.add(one_list);
	     }
		 ContentDAO dao=new ContentDAO(this.conn);
		 try
		 {
			 dao.batchInsert(insertSQL.toString(),list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
	 }
	 /**
	  * 按班次刷卡表
	  * @param temp_emp_table人员临时表
	  * @param time_table时间临时表
	  * @return
	  */
	 public String createInstanceTemp(String temp_emp_table,String time_table)
	 {
//		 String table_name="kq_re_"+userView.getUserName();
		 String table_name="t#"+userView.getUserName()+"_kq_re";
		 DbWizard dbWizard =new DbWizard(conn);
		 if(dbWizard.isExistTable(table_name,false))
		 {
			OperateDataTable operateDataTable=new OperateDataTable(this.conn);
			operateDataTable.dropTable(table_name);
		 }
		 StringBuffer columns=new StringBuffer();
		 StringBuffer strSQL=new StringBuffer();
		 StringBuffer strSrcTab=new StringBuffer();
		 switch(Sql_switcher.searchDbServer())
		 {
				  case Constant.MSSQL:
			      {
			    	 columns.append("nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate,inout_flag,oper_cause,oper_user,oper_time,oper_mach,");
			 		 columns.append("class_id,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,");
			 		 columns.append("onduty_card_1,onduty_card_2,onduty_card_3,");
			 		 columns.append("offduty_card_1,offduty_card_2,offduty_card_3,overflag");
			 		 
			 		 strSQL.append("(SELECT nbase,B0110,E0122,E01A1,A0101,A0100,card_no,sDate,inout_flag,oper_cause,oper_user,oper_time,oper_mach");
			 		 strSQL.append(" FROM " + temp_emp_table + "," + time_table+"");
			 		 strSQL.append(" WHERE " + temp_emp_table +".flag = 2) empdate ");
			 		 
			 		 strSrcTab.append("(SELECT empdate.*,b.class_id,c.onduty_1,c.offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,");
			 		 strSrcTab.append("onduty_card_1,onduty_card_2,onduty_card_3,");
			 		 strSrcTab.append("offduty_card_1,offduty_card_2,offduty_card_3, 0 as overflag");
			 		 strSrcTab.append(" FROM (" + strSQL.toString() + " LEFT JOIN kq_employ_shift b");
			 		 strSrcTab.append("  ON empdate.nbase=b.nbase AND empdate.A0100=b.A0100 AND empdate.sDate=b.Q03Z0");
			 		 strSrcTab.append(") LEFT JOIN  kq_class c ON c.class_id=b.class_id");
			 		 strSrcTab.append(" ) XXX");
			    	  break;
			      }
				  case Constant.ORACEL:
				  {
					     columns.append("nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate,inout_flag,oper_cause,oper_user,oper_time,oper_mach,");
				 		 columns.append("class_id,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,");
				 		 columns.append("onduty_card_1,onduty_card_2,onduty_card_3,");
				 		 columns.append("offduty_card_1,offduty_card_2,offduty_card_3,overflag");
				 		 
				 		 strSQL.append("(SELECT nbase,B0110,E0122,E01A1,A0101,A0100,card_no,sDate,inout_flag,oper_cause,oper_user,oper_time,oper_mach");
				 		 strSQL.append(" FROM " + temp_emp_table + "," + time_table+"");
				 		 strSQL.append(" WHERE " + temp_emp_table +".flag = 2) empdate ");
				 		 
				 		 strSrcTab.append("(SELECT empdate.*,b.class_id,c.onduty_1,c.offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,");
				 		 strSrcTab.append("onduty_card_1,onduty_card_2,onduty_card_3,");
				 		 strSrcTab.append("offduty_card_1,offduty_card_2,offduty_card_3, 0 as overflag");
				 		 strSrcTab.append(" FROM " + strSQL.toString() + " LEFT JOIN kq_employ_shift b");
				 		 strSrcTab.append("  ON empdate.nbase=b.nbase AND empdate.A0100=b.A0100 AND empdate.sDate=b.Q03Z0");
				 		 strSrcTab.append(" LEFT JOIN  kq_class c ON c.class_id=b.class_id");
				 		 strSrcTab.append(" ) XXX");
					  break;
				  }
				  case Constant.DB2:
				  {
					  break;
				  }
	     }
		 
		 KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
		 kqUtilsClass.createTempTable(strSrcTab.toString(), table_name, columns.toString(),"","");
		 /****清除休息班的*****/
		 ContentDAO dao=new ContentDAO(this.conn);
		 try
		 {
			 StringBuffer sql=new StringBuffer();
			 sql.append("delete from "+table_name+" ");
			 sql.append(" where class_id=?");
			 ArrayList list=new ArrayList();
			 list.add("0");
			 dao.delete(sql.toString(),list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return table_name;
	 }
	 /**
	  * 数据处理，补签到按班次刷卡表
	  * @param temp_emp_table人员临时表
	  * @param time_table时间临时表
	  * @return
	  */
	 public String createInstanceTemp2(String temp_emp_table,String time_table)
	 {
//		 String table_name="kq_re_"+userView.getUserName();
		 String table_name="t#"+userView.getUserName()+"_kq_re";
		 DbWizard dbWizard =new DbWizard(conn);
		 if(dbWizard.isExistTable(table_name,false))
		 {
			OperateDataTable operateDataTable=new OperateDataTable(this.conn);
			operateDataTable.dropTable(table_name);
		 }
		 StringBuffer columns=new StringBuffer();
		 StringBuffer strSQL=new StringBuffer();
		 StringBuffer strSrcTab=new StringBuffer();
		 switch(Sql_switcher.searchDbServer())
		 {
				  case Constant.MSSQL:
			      {
			    	 columns.append("nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate,inout_flag,oper_cause,oper_user,oper_time,oper_mach,");
			 		 columns.append("class_id,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,");
			 		 columns.append("onduty_card_1,onduty_card_2,onduty_card_3,");
			 		 columns.append("offduty_card_1,offduty_card_2,offduty_card_3,overflag");
			 		 
			 		 strSQL.append("(SELECT nbase,B0110,E0122,E01A1,A0101,A0100,card_no,sDate,inout_flag,oper_cause,oper_user,oper_time,oper_mach");
			 		 strSQL.append(" FROM " + temp_emp_table + "," + time_table+"");
			 		 strSQL.append(" WHERE " + temp_emp_table +".flag = 1) empdate ");
			 		 
			 		 strSrcTab.append("(SELECT empdate.*,b.class_id,c.onduty_1,c.offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,");
			 		 strSrcTab.append("onduty_card_1,onduty_card_2,onduty_card_3,");
			 		 strSrcTab.append("offduty_card_1,offduty_card_2,offduty_card_3, 0 as overflag");
			 		 strSrcTab.append(" FROM (" + strSQL.toString() + " LEFT JOIN kq_employ_shift b");
			 		 strSrcTab.append("  ON empdate.nbase=b.nbase AND empdate.A0100=b.A0100 AND empdate.sDate=b.Q03Z0");
			 		 strSrcTab.append(") LEFT JOIN  kq_class c ON c.class_id=b.class_id");
			 		 strSrcTab.append(" ) XXX");
			    	  break;
			      }
				  case Constant.ORACEL:
				  {
					     columns.append("nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate,inout_flag,oper_cause,oper_user,oper_time,oper_mach,");
				 		 columns.append("class_id,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,");
				 		 columns.append("onduty_card_1,onduty_card_2,onduty_card_3,");
				 		 columns.append("offduty_card_1,offduty_card_2,offduty_card_3,overflag");
				 		 
				 		 strSQL.append("(SELECT nbase,B0110,E0122,E01A1,A0101,A0100,card_no,sDate,inout_flag,oper_cause,oper_user,oper_time,oper_mach");
				 		 strSQL.append(" FROM " + temp_emp_table + "," + time_table+"");
				 		 strSQL.append(" WHERE " + temp_emp_table +".flag = 1) empdate ");
				 		 
				 		 strSrcTab.append("(SELECT empdate.*,b.class_id,c.onduty_1,c.offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,");
				 		 strSrcTab.append("onduty_card_1,onduty_card_2,onduty_card_3,");
				 		 strSrcTab.append("offduty_card_1,offduty_card_2,offduty_card_3, 0 as overflag");
				 		 strSrcTab.append(" FROM " + strSQL.toString() + " LEFT JOIN kq_employ_shift b");
				 		 strSrcTab.append("  ON empdate.nbase=b.nbase AND empdate.A0100=b.A0100 AND empdate.sDate=b.Q03Z0");
				 		 strSrcTab.append(" LEFT JOIN  kq_class c ON c.class_id=b.class_id");
				 		 strSrcTab.append(" ) XXX");
					  break;
				  }
				  case Constant.DB2:
				  {
					  break;
				  }
	     }
		 
		 KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
		 kqUtilsClass.createTempTable(strSrcTab.toString(), table_name, columns.toString(),"","");
		 /****清除休息班的*****/
		 ContentDAO dao=new ContentDAO(this.conn);
		 try
		 {
			 StringBuffer sql=new StringBuffer();
			 sql.append("delete from "+table_name+" ");
			 sql.append(" where class_id=?");
			 ArrayList list=new ArrayList();
			 list.add("0");
			 dao.delete(sql.toString(),list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return table_name;
	 }
	 /**
	  * 修改按班次补刷临时数据，添加到刷卡原始数据表里面
	  * @param tmpTab
	  * @param class_flag
	  * @throws GeneralException
	  */
	 public void classRepairFromTemp(String tmpTab,String class_flag)throws GeneralException
	 {
		 String timeFldOnduty="";
		 String timeFldOffduty="";
		 String needCardOn="";
		 String needCardOff="";
		 StringBuffer strOnWhr=null;
		 StringBuffer strOffWhr=null;
		 StringBuffer sql=new StringBuffer();
		 StringBuffer insertSQL=null;
		 sql.append("INSERT INTO kq_originality_data(nbase,A0100,A0101,B0110,E0122,E01A1,card_no,work_date,sp_flag,location,datafrom,"+this.new_add_columns+",work_time)");
		 sql.append(" select nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate AS work_date,'02','补刷','1',"+this.new_add_columns+",");
		 String  strWhr = " WHERE class_id IS NOT NULL";
		 StringBuffer strExists=new StringBuffer();
		 strExists.append(" AND NOT EXISTS(SELECT 1 FROM kq_originality_data a");
		 strExists.append(" WHERE a.nbase=" + tmpTab + ".nbase AND a.A0100=" + tmpTab + ".A0100");
		 strExists.append(" AND a.work_date=" + tmpTab + ".sDate AND a.work_time=" + tmpTab + ".");
		 ContentDAO dao=new ContentDAO(this.conn);
		 ArrayList list =new ArrayList();
		 try
		 {
			 for(int i=1;i<4;i++)
			 {
				 timeFldOnduty = "onduty_"+i ;
			     timeFldOffduty = "offduty_" + i;
			     needCardOn  = "onduty_card_" + i;
			     needCardOff = "offduty_card_" + i;
			     insertSQL=new StringBuffer();
				 strOnWhr=new StringBuffer();
				 strOffWhr=new StringBuffer();				 
				 
				 switch(Sql_switcher.searchDbServer())
				 {
					case Constant.MSSQL:
					{
						 strOnWhr.append(" AND (" +needCardOn+ "<>'0' AND (" +timeFldOnduty+ "<>'' or "+timeFldOnduty+" IS NOT NULL))");
						 strOffWhr.append(" AND (" + needCardOff + "<>'0' AND ("+ timeFldOffduty+" <> '' or "+timeFldOffduty+" IS NOT NULL))");
						 break;
					}
					case Constant.ORACEL:
					{ 
						 strOnWhr.append(" AND ("+needCardOn+"<>'0' AND " + timeFldOnduty  + " IS NOT NULL)");
						 strOffWhr.append(" AND ("+needCardOff+"<>'0' AND " + timeFldOffduty + " IS NOT NULL)");
						 break;
					}
				    case Constant.DB2:
					{
						 strOnWhr.append(" AND ("+needCardOn+"<>'0' AND " + timeFldOnduty  + " IS NOT NULL)");
						 strOffWhr.append(" AND ("+needCardOff+"<>'0' AND " + timeFldOffduty + " IS NOT NULL)");
						 break;
				    }
			    }
				if("0".equals(class_flag))
				{
					 /****上下班全补***/
					if(i>1)
					{
						overZeroTimeTreate(tmpTab,timeFldOnduty);
					}
					insertSQL.append(sql.toString()+""+timeFldOnduty +" as work_time from "+tmpTab);
					insertSQL.append(strWhr +""+strExists.toString()+""+timeFldOnduty+")");
					insertSQL.append(strOnWhr.toString());
					//System.out.println("上班---->"+insertSQL.toString());		//补刷必须刷卡时间
					updateTempEmpInout_flag(tmpTab,"1");					
					updateDraftCardDataForClassRepair(dao, tmpTab, timeFldOnduty, strOnWhr.toString(), "02");
					dao.insert(insertSQL.toString(),list);
					
					overZeroTimeTreate(tmpTab,timeFldOffduty);
					insertSQL=new StringBuffer();
					insertSQL.append(sql.toString()+""+timeFldOffduty +" as work_time from "+tmpTab);
					insertSQL.append(strWhr +""+strExists.toString()+""+timeFldOffduty+")");
					insertSQL.append(strOffWhr.toString());	
					updateTempEmpInout_flag(tmpTab,"-1");
					updateDraftCardDataForClassRepair(dao, tmpTab, timeFldOffduty, strOffWhr.toString(), "02");
					dao.insert(insertSQL.toString(),list);
				}else if("1".equals(class_flag))
				{
					 /***只补上班***/
					if(i>1)
					{
						overZeroTimeTreate(tmpTab,timeFldOnduty);
					}
					insertSQL.append(sql.toString()+""+timeFldOnduty +" as work_time from "+tmpTab);
					insertSQL.append(strWhr +""+strExists.toString()+""+timeFldOnduty+")");
					insertSQL.append(strOnWhr.toString());
					updateTempEmpInout_flag(tmpTab,"1");
					updateDraftCardDataForClassRepair(dao, tmpTab, timeFldOnduty, strOnWhr.toString(), "02");
					dao.insert(insertSQL.toString(),list);
				}else if("2".equals(class_flag))
				{
					 /****只补下班****/
					overZeroTimeTreate(tmpTab,timeFldOffduty);
					insertSQL.append(sql.toString()+""+timeFldOffduty +" as work_time from "+tmpTab);
					insertSQL.append(strWhr +""+strExists.toString()+""+timeFldOffduty+")");
					insertSQL.append(strOffWhr.toString());
					updateTempEmpInout_flag(tmpTab,"-1");
					updateDraftCardDataForClassRepair(dao, tmpTab, timeFldOffduty, strOffWhr.toString(), "02");
					dao.insert(insertSQL.toString(),list);
				} 
			}
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 
	 }
	 
	 private void updateDraftCardDataForClassRepair(ContentDAO dao, String tmpTab, String timeFld, String strOnWhr, String spFlag) throws Exception {
	     //zxj 20150401 增加一步，既然是考勤员来补签，那么如果刚好个人也有补签记录并且未提交，那么一并置为待批状态
         StringBuffer uptSql = new StringBuffer();
         uptSql.append("UPDATE kq_originality_data");
         uptSql.append(" SET sp_flag='").append(spFlag).append("'");
         uptSql.append(" WHERE sp_flag='01'");
         uptSql.append(" AND EXISTS(SELECT 1 FROM " + tmpTab);
         uptSql.append(" WHERE kq_originality_data.nbase=" + tmpTab + ".nbase");
         uptSql.append(" AND kq_originality_data.A0100=" + tmpTab + ".A0100");
         uptSql.append(" AND kq_originality_data.work_date=" + tmpTab + ".sDate");
         uptSql.append(" AND kq_originality_data.work_time=" + tmpTab + "." + timeFld);
         uptSql.append(strOnWhr);
         uptSql.append(")");
         dao.update(uptSql.toString());
	 }
	 /**
	  * 数据处理补签，复杂规则修改按班次补刷临时数据，添加到刷卡原始数据表里面
	  * @param tmpTab
	  * @param class_flag
	  * @throws GeneralException
	  */
	 public void classRepairFromTemp2(String tmpTab,String class_flag)throws GeneralException
	 {
		 String timeFldOnduty="";
		 String timeFldOffduty="";
		 String needCardOn="";
		 String needCardOff="";
		 StringBuffer strOnWhr=null;
		 StringBuffer strOffWhr=null;
		 StringBuffer sql=new StringBuffer();
		 StringBuffer insertSQL=null;
		 sql.append("INSERT INTO kq_originality_data(nbase,A0100,A0101,B0110,E0122,E01A1,card_no,work_date,sp_flag,location,datafrom,"+this.new_add_columns+",work_time)");
		 sql.append(" select nbase,A0100,A0101,B0110,E0122,E01A1,card_no,sDate AS work_date,'03','补刷','1',"+this.new_add_columns+",");
		 String  strWhr = " WHERE class_id IS NOT NULL";
		 StringBuffer strExists=new StringBuffer();
		 strExists.append(" AND NOT EXISTS(SELECT 1 FROM kq_originality_data a");
		 strExists.append(" WHERE a.nbase=" + tmpTab + ".nbase AND a.A0100=" + tmpTab + ".A0100");
		 strExists.append(" AND a.work_date=" + tmpTab + ".sDate AND a.work_time=" + tmpTab + ".");
		 ContentDAO dao=new ContentDAO(this.conn);
		 ArrayList list =new ArrayList();
		 try
		 {
			 for(int i=1;i<4;i++)
			 {
				 timeFldOnduty = "onduty_"+i ;
			     timeFldOffduty = "offduty_" + i;
			     needCardOn  = "onduty_card_" + i;
			     needCardOff = "offduty_card_" + i;
			     insertSQL=new StringBuffer();
				 strOnWhr=new StringBuffer();
				 strOffWhr=new StringBuffer();				 
				 
				 switch(Sql_switcher.searchDbServer())
				 {
					case Constant.MSSQL:
					{
						 strOnWhr.append(" AND (" +needCardOn+ "<>'0' AND (" +timeFldOnduty+ "<>'' or "+timeFldOnduty+" IS NOT NULL))");
						 strOffWhr.append(" AND (" + needCardOff + "<>'0' AND ("+ timeFldOffduty+" <> '' or "+timeFldOffduty+" IS NOT NULL))");
						 break;
					}
					case Constant.ORACEL:
					{ 
						 strOnWhr.append(" AND ("+needCardOn+"<>'0' AND " + timeFldOnduty  + " IS NOT NULL)");
						 strOffWhr.append(" AND ("+needCardOff+"<>'0' AND " + timeFldOffduty + " IS NOT NULL)");
						 break;
					}
				    case Constant.DB2:
					{
						 strOnWhr.append(" AND ("+needCardOn+"<>'0' AND " + timeFldOnduty  + " IS NOT NULL)");
						 strOffWhr.append(" AND ("+needCardOff+"<>'0' AND " + timeFldOffduty + " IS NOT NULL)");
						 break;
				    }
			    }
				if("0".equals(class_flag))
				{
					 /****上下班全补***/
					if(i>1)
					{
						overZeroTimeTreate(tmpTab,timeFldOnduty);
					}
					insertSQL.append(sql.toString()+""+timeFldOnduty +" as work_time from "+tmpTab);
					insertSQL.append(strWhr +""+strExists.toString()+""+timeFldOnduty+")");
					insertSQL.append(strOnWhr.toString());
					//System.out.println("上班---->"+insertSQL.toString());		//补刷必须刷卡时间
					updateTempEmpInout_flag(tmpTab,"1");	
					updateDraftCardDataForClassRepair(dao, tmpTab, timeFldOnduty, strOnWhr.toString(), "03");
					dao.insert(insertSQL.toString(),list);
					overZeroTimeTreate(tmpTab,timeFldOffduty);
					insertSQL=new StringBuffer();
					insertSQL.append(sql.toString()+""+timeFldOffduty +" as work_time from "+tmpTab);
					insertSQL.append(strWhr +""+strExists.toString()+""+timeFldOffduty+")");
					insertSQL.append(strOffWhr.toString());	
					updateTempEmpInout_flag(tmpTab,"-1");
					updateDraftCardDataForClassRepair(dao, tmpTab, timeFldOnduty, strOnWhr.toString(), "03");
					dao.insert(insertSQL.toString(),list);
				}else if("1".equals(class_flag))
				{
					 /***只补上班***/
					if(i>1)
					{
						overZeroTimeTreate(tmpTab,timeFldOnduty);
					}
					insertSQL.append(sql.toString()+""+timeFldOnduty +" as work_time from "+tmpTab);
					insertSQL.append(strWhr +""+strExists.toString()+""+timeFldOnduty+")");
					insertSQL.append(strOnWhr.toString());
					updateTempEmpInout_flag(tmpTab,"1");
					updateDraftCardDataForClassRepair(dao, tmpTab, timeFldOnduty, strOnWhr.toString(), "03");
					dao.insert(insertSQL.toString(),list);
				}else if("2".equals(class_flag))
				{
					 /****只补下班****/
					overZeroTimeTreate(tmpTab,timeFldOffduty);
					insertSQL.append(sql.toString()+""+timeFldOffduty +" as work_time from "+tmpTab);
					insertSQL.append(strWhr +""+strExists.toString()+""+timeFldOffduty+")");
					insertSQL.append(strOffWhr.toString());
					updateTempEmpInout_flag(tmpTab,"-1");
					updateDraftCardDataForClassRepair(dao, tmpTab, timeFldOnduty, strOnWhr.toString(), "03");
					dao.insert(insertSQL.toString(),list);
				} 
			}
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		 
	 }
	 /**
	  * 跨零点处理
	  * @param temp_table
	  * @param offduty
	  */
	 public void overZeroTimeTreate(String temp_table,String offduty)
	 {
		 StringBuffer sql=new StringBuffer();
		 sql.append("UPDATE " + temp_table + " SET overflag=1,sDate=");
		 switch(Sql_switcher.searchDbServer())
		 {
				  case Constant.MSSQL:
			      {
					  sql.append("convert(varchar(10),(convert(datetime,sDate)+1),102)");
					  break;
			      }
				  case Constant.ORACEL:
				  { 
					  sql.append("To_Char("+Sql_switcher.charToDate("sDate")+"+1,'YYYY.MM.dd')");
					  break;
				  }
				  case Constant.DB2:
				  {
					  sql.append("replace(char("+Sql_switcher.charToDate("sDate")+"+1),'-','.')");
					  break;
				  }
		 }
		 sql.append(" where class_id IS NOT NULL AND overflag=0 AND ");
		 //szk跨24小时班
		 String sql1="";
		 if(offduty.startsWith("on"))
		 {
			 sql1= "offduty_"+ (Integer.parseInt(offduty.substring(offduty.length()-1))-1) +">="+offduty;
		 }else {
			 sql1= "onduty_"+offduty.substring(offduty.length()-1)+">="+offduty;
		}
		 sql.append(sql1);
		 sql.append(" and ("+offduty+" is not null or "+offduty+"<>'')");		 
		 ContentDAO dao=new ContentDAO(this.conn);
		 try
		 {
			 dao.update(sql.toString());
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	 
		/**
		 * 判断是否超过当前考勤期间补刷卡次数
		 * @param bays
		 * @param nabse
		 * @param a0100
		 * @param statr_d
		 * @param end_d
		 * @return
		 */
		public boolean isOverTopRepairdaynum(int days,String nbase,String a0100,String statr_d,String end_d)
		{
			StringBuffer sql=new StringBuffer ();
			sql.append("select count(*) as num from kq_originality_data ");
			sql.append(" where a0100='"+a0100+"' and nbase='"+nbase+"'");
			sql.append(" and work_date>='"+statr_d+"' and work_date<='"+end_d+"'");
			sql.append(" and datafrom='1'");
			boolean isCorrect=false;
			RowSet rs=null;
			try
			{
				ContentDAO dao=new ContentDAO(this.conn);
				rs=dao.search(sql.toString());
				int num=0;
				if(rs.next()) {
                    num=rs.getInt("num");
                }
				if(num>=days) {
                    isCorrect=true;
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally
			{
				KqUtilsClass.closeDBResource(rs);
			}
			return isCorrect;
		}
		/**
		 * 批量判断补刷卡临时表的人员是否超过当前考勤期间补刷卡次数
		 * @param days
		 * @param tmpTab
		 * @return
		 */
		public ArrayList isOverTopRepairday(int days,String tmpTab)
		{
			StringBuffer sql=new StringBuffer();		
			sql.append(" select nbase,A0100,a0101,sDate AS work_date");
			sql.append(" from "+tmpTab);	
			ArrayList list=new ArrayList();
			RowSet rs=null;
			try
			{
				String nbase="";
				String a0100="";
				String statr_d="";
				String end_d="";
				String work_date="";
				ContentDAO dao=new ContentDAO(this.conn);
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					nbase=rs.getString("nbase");
					work_date=rs.getString("work_date");
					a0100=rs.getString("a0100");					
					ArrayList datelist =RegisterDate.getKq_duration(work_date,this.conn); 
					if(datelist!=null&&datelist.size()>0)
				    {
						statr_d=datelist.get(0).toString();
						end_d=datelist.get(datelist.size()-1).toString();	
						if(isOverTopRepairdaynum(days,nbase,a0100,statr_d,end_d))
						{
							list.add(rs.getString("a0101"));
						}
				    }
					
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally
			{
			    KqUtilsClass.closeDBResource(rs);
			}
			return list;
		}
		
		/**
         * 批量判断按班次补刷卡临时表的人员是否超过当前考勤期间补刷卡次数
         * @param days 参数中定义的补刷次数限制
         * @param tmpTab 补刷人员临时表
         * @param classFlag 按班次补刷方式 0：上下班全补 1：补上班 2：补下班
         * @return
         */
        public ArrayList isOverTopRepairTimes(int days,String tmpTab, String classFlag)
        {
            StringBuffer ondutyCardSumSql = new StringBuffer();
            ondutyCardSumSql.append(Sql_switcher.isnull("onduty_card_1", "0"));
            ondutyCardSumSql.append("+");
            ondutyCardSumSql.append(Sql_switcher.isnull("onduty_card_2", "0"));
            ondutyCardSumSql.append("+");
            ondutyCardSumSql.append(Sql_switcher.isnull("onduty_card_3", "0"));
            
            StringBuffer offdutyCardSumSql = new StringBuffer();
            offdutyCardSumSql.append(Sql_switcher.isnull("offduty_card_1", "0"));
            offdutyCardSumSql.append("+");
            offdutyCardSumSql.append(Sql_switcher.isnull("offduty_card_2", "0"));
            offdutyCardSumSql.append("+");
            offdutyCardSumSql.append(Sql_switcher.isnull("offduty_card_3", "0"));
            
            String needCardSumFld = "";
            if ("0".equals(classFlag)) {
                needCardSumFld = ondutyCardSumSql.toString() + "+" + offdutyCardSumSql.toString();
            } else if ("1".equals(classFlag)) {
                needCardSumFld = ondutyCardSumSql.toString();
            } else if ("2".equals(classFlag)) {
                needCardSumFld = offdutyCardSumSql.toString();
            }
            
            needCardSumFld = "sum(" + needCardSumFld + ") as needtimes";
            
            StringBuffer sql = new StringBuffer(); 
            sql.append("SELECT A.nbase,A.a0100,A.a0101,sDate AS work_date,B.needtimes");
            sql.append(" FROM ").append(tmpTab).append(" A left join (");
            sql.append(" select nbase,A0100,").append(needCardSumFld);
            sql.append(" from ").append(tmpTab); 
            sql.append(" group by nbase,a0100");
            sql.append(")B");
            sql.append(" ON A.nbase=B.nbase and A.a0100=B.a0100");
            
            ArrayList list = new ArrayList();
            ArrayList nbaseA0100List = new ArrayList();
            RowSet rs = null;
            try {
                String nbase = "";
                String a0100 = "";
                String statr_d = "";
                String end_d = "";
                String work_date = "";
                int needTimes = 0;
                ContentDAO dao = new ContentDAO(this.conn);
                rs = dao.search(sql.toString());
                while(rs.next()){
                    nbase = rs.getString("nbase");
                    work_date = rs.getString("work_date");
                    a0100 = rs.getString("a0100");  
                    needTimes = rs.getInt("needtimes");
                    
                    //zxj 20151017 同一人如果超过了次数限制，就不需要重复检查了。
                    if (nbaseA0100List.contains(nbase + a0100)) {
                        continue;
                    }
                    
                    ArrayList datelist = RegisterDate.getKq_duration(work_date,this.conn); 
                    if(datelist!=null && datelist.size()>0) {
                        statr_d = datelist.get(0).toString();
                        end_d = datelist.get(datelist.size()-1).toString();   
                        if(isOverTopRepairdaynum(days-needTimes,nbase,a0100,statr_d,end_d)){
                            list.add(rs.getString("a0101"));
                            nbaseA0100List.add(nbase + a0100);
                        }
                    }                    
                }
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                KqUtilsClass.closeDBResource(rs);
            }
            return list;
        }
		
		public int getRepairCardNumLimit() {
		    KqParam kqParam = KqParam.getInstance();
		    
		    // 是否限制刷卡次数
		    String repairCardNumStatus = kqParam.getRepairCardNumStatus(this.conn, this.userView);
		    if (!"1".equals(repairCardNumStatus)) {
                return 0;
            }
		    
		    // 刷卡次数
	        String repair_card_num = kqParam.getRepairCardNum(this.conn, this.userView);
	        try {
	            return Integer.parseInt(repair_card_num);
	        } catch (Exception e) {
	            return 0;
	        }
		}
		
}
