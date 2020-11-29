package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassObject;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 刷卡数据数据分析
 * <p>Title:DataAnalyse.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 30, 2007 2:00:48 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class DataAnalyse implements DateAnalyseImp{
  private Connection conn;
  private UserView userView;
  private String analyseType; //0: 非机考人员 1：机考人员 100: 全部
  private String kq_type;
  private String kq_card;
  private String card_no_temp_field="card_no";
  private String kq_sDate="sDate";
  private String kq_dkind="dkind";  
  //private Connection conn_A = null;   
  private Statement stmt = null;
  private ResultSet resultSet = null;
  //private RowSet resultSet=null;
  private ArrayList columnlist=new ArrayList(); 
  private RowSet rs_FEmpDatas=null;
  private HashMap fLeaveBackDataH=new HashMap();//销假数据集
  private KqEmpClassBean empClassBean=new KqEmpClassBean();
  private boolean[][] cardPoints=new boolean[4][4];
  private HashMap kqItem_hash=new HashMap();
  private String dataUpdateType="";//更新数据类型0:全部1：只更新业务数据  
  private String temp_Table;
  private String pick_flag="0";
  private boolean calcNight;
  private boolean haveOTime;
  private float nightTimeLen;
  private float factOTimeLen;
  private String initflag;//是否是初始化日明晰
  private ArrayList db_list=new ArrayList();
  private boolean isRestLeave1=false;//是否休息时段1范围内离开
  private boolean isRestLeave2=false;//是否休息时段2范围内离开
  private float fFactLeaveTime=0;//离岗的时间
  private Date inTime = null;
  private Date outTime = null;
  private String restLeaveCalcTimeType="0";//按什么情况统计离岗时间
  private String fAnalyseTempTab;//数据处理表
  private String fExceptCardTab;//临时异常表的名称
  private String fTranOverTimeTab;//临时延时加班表
  private String fBusiCompareTab;////申请比对表
  private Date [] rstTranOverTime=new Date[2];//延时加班数据
  private float fFactFlextime=0;//弹性时间
  private float fSecFactFlextime=0; //实际适用的下午上班弹性时间
  private ContentDAO dao=null;
  private KqParam kqParam=null;
  private HashMap class_hash=new HashMap();
  private String date_format="yyyy.MM.dd HH:mm";
  private HashMap fTranOverTimeApps=new HashMap();////延时加班
  private HashMap overApplys=new HashMap();
  private RowSet fBusiData=null;//申请业务数据集
  private RowSet fLeaveBackData=null;//销假业务数据集
  private String no_tranData="";
 
public String getInitflag() {
	return initflag;
}
public void setInitflag(String initflag) {
	this.initflag = initflag;
}
public void setPick_flag(String pick_flag) {
	this.pick_flag = pick_flag;
}
public void setColumnlist(ArrayList columnlist) 
  {
		this.columnlist = columnlist;//q03数据集
  }
  public DataAnalyse()
  {
	  
  }
  public DataAnalyse(Connection conn,UserView userView)
  {
	  this.conn=conn;
	  this.userView=userView;	  
	  init();
	  
  }
  public DataAnalyse(Connection conn,UserView userView,String analyseType,String kq_type,String kq_card,String dataUpdateType,ArrayList db_list)
  {
	    
		this.conn=conn;		
		this.userView=userView;
		this.analyseType=analyseType;//0: 非机考人员 1：机考人员 100: 全部,101:集中处理
		init();
		this.kq_type=kq_type;//考勤方式字段
		this.kq_card=kq_card;//考勤卡号字段		
		this.dataUpdateType=dataUpdateType;//更新数据类型0:全部1：只更新业务数据
		this.db_list=db_list;
		this.dao=new ContentDAO(this.conn);
		kqParam = KqParam.getInstance();		
		try
		{
			KqClassObject kqClassObject=new KqClassObject(this.conn);
			kqClassObject.checkKqClassTable();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
  }  
  private void init()
  {
	 /* this.fAnalyseTempTab="kqtmp_"+this.userView.getUserName()+"_daydata";
      this.fExceptCardTab ="kqtmp_" +this.userView.getUserName() + "_exceptcard";
      this.fTranOverTimeTab= "kqtmp_" +this.userView.getUserName() + "_tranovertime";
	  this.fBusiCompareTab= "kqtmp_" +this.userView.getUserName() + "_busicompare";*/
	  String mark= KqParam.getInstance().getData_processing();  //数据处理：0：分用户处理 1：集中处理 wangy
	  if("1".equals(mark))
	  {
		  this.fAnalyseTempTab="kq_analyse_result"; 
		  this.fExceptCardTab ="kq_analyse_exceptcard";  //changed at 20091203 kqtmp_xxxx_exceptcard
		  this.fTranOverTimeTab= "kq_analyse_tranovertime";//changed at 20091203 kqtmp_xxxx_tranovertime
		  this.fBusiCompareTab= "kq_analyse_busicompare";//changed at 20091203 kqtmp_xxxx_busicompare
//		  this.fExceptCardTab ="kt_" +this.userView.getUserName() + "_ed";  //changed at 20091203 kqtmp_xxxx_exceptcard
//		  this.fTranOverTimeTab= "kt_" +this.userView.getUserName() + "_tt";//changed at 20091203 kqtmp_xxxx_tranovertime
//		  this.fBusiCompareTab= "kt_" +this.userView.getUserName() + "_bc";//changed at 20091203 kqtmp_xxxx_busicompare
	  }else
	  {
		  this.fAnalyseTempTab="kt_"+this.userView.getUserName()+"_dd"; 
		  this.fExceptCardTab ="kt_" +this.userView.getUserName() + "_ed";  //changed at 20091203 kqtmp_xxxx_exceptcard
		  this.fTranOverTimeTab= "kt_" +this.userView.getUserName() + "_tt";//changed at 20091203 kqtmp_xxxx_tranovertime
		  this.fBusiCompareTab= "kt_" +this.userView.getUserName() + "_bc";//changed at 20091203 kqtmp_xxxx_busicompare
	  }
	  //原始
//	  this.fAnalyseTempTab="kt_"+this.userView.getUserName()+"_dd"; 
//	  this.fExceptCardTab ="kt_" +this.userView.getUserName() + "_ed";  //changed at 20091203 kqtmp_xxxx_exceptcard
//	  this.fTranOverTimeTab= "kt_" +this.userView.getUserName() + "_tt";//changed at 20091203 kqtmp_xxxx_tranovertime
//	  this.fBusiCompareTab= "kt_" +this.userView.getUserName() + "_bc";//changed at 20091203 kqtmp_xxxx_busicompare
  }
  
  /**
   * 创建数据分析临时表
   * @return
   */
  public String createDataAnalyseTmp(String table_name)throws GeneralException
  {
	  
	  KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn);
	  
	  DbWizard dbWizard =new DbWizard(this.conn);
	  dbWizard.dropTable(table_name);
	  
	  Table table=new Table(table_name);	 
	  StringBuffer sql=new StringBuffer();
	  sql.append("q03,kq_class");
	  StringBuffer columns=new StringBuffer();
	  columns.append("q03.*,kq_class.*");	 
	  kqUtilsClass.createTempTable(sql.toString(), table_name, columns.toString(),"1=2","");	
	  Field temp = new Field("card_no","工作卡号");
	  temp.setDatatype(DataType.STRING);
	  temp.setLength(50);
	  temp.setKeyable(false);			
	  temp.setVisible(false);
	  table.addField(temp);
	  Field temp1=new Field("card_time","工作时间");
	  temp1.setDatatype(DataType.STRING);
	  temp1.setLength(500);
	  temp1.setKeyable(false);			
	  temp1.setVisible(false);
	  table.addField(temp1);
	  temp1=new Field(this.kq_dkind,"日期类型");
	  temp1.setDatatype(DataType.STRING);
	  temp1.setLength(50);
	  temp1.setKeyable(false);			
	  temp1.setVisible(false);
	  table.addField(temp1);	
	  temp=new Field("flag","有效状态");//是否生效
	  temp.setDatatype(DataType.STRING);
	  temp.setLength(10);
	  temp.setKeyable(false);			
	  temp.setVisible(false);
	  table.addField(temp);
	  temp=new Field("IsOk","是否正常");//是否正常
	  temp.setDatatype(DataType.STRING);
	  temp.setLength(50);
	  temp.setKeyable(false);			
	  temp.setVisible(false);
	  table.addField(temp);
	  temp=new Field("LackCard","缺刷标记");//缺刷标记
	  temp.setDatatype(DataType.STRING);
	  temp.setLength(10);
	  temp.setKeyable(false);			
	  temp.setVisible(false);
	  table.addField(temp);
	  try
	  {
		  dbWizard.addColumns(table);		 
		  /*****删除主键****/
		  table=new Table(table_name);
		  Field d_temp = new Field("class_id","班次编号");		  
		  table.addField(d_temp);
		  d_temp=new Field("name","班次名称");		  
		  table.addField(d_temp);	
		  /*d_temp= new Field("a0100","人员编号");	  
		  table.addField(d_temp);
		  d_temp=new Field("nbase","人员库");	  
		  table.addField(d_temp);
		  d_temp=new Field("q03z0","日期");		  
		  table.addField(d_temp);*/
		  dbWizard.dropColumns(table);
		  table=new Table(table_name);
		  Field a_temp = new Field("class_id","班次编号");
		  a_temp.setDatatype(DataType.INT);
		  a_temp.setLength(50);
		  a_temp.setKeyable(false);			
		  a_temp.setVisible(false);
		  table.addField(a_temp);
		  a_temp = new Field("name","班次名称");
		  a_temp.setDatatype(DataType.STRING);
		  a_temp.setLength(200);
		  a_temp.setKeyable(false);			
		  a_temp.setVisible(false);
		  table.addField(a_temp);
		  dbWizard.addColumns(table);
		  Table pk_table=new Table(table_name);
		  Field pk_temp = new Field("a0100","人员编号");
		  pk_temp.setLength(100);
		  pk_temp.setKeyable(true);
		  temp.setNullable(false);
		  temp.setDatatype(DataType.STRING);
		  pk_table.addField(pk_temp);
		  pk_temp = new Field("nbase","人员库");
		  pk_temp.setLength(100);
		  pk_temp.setKeyable(true);
		  temp.setNullable(false);
		  temp.setDatatype(DataType.STRING);
		  pk_table.addField(pk_temp);
		  pk_temp = new Field("q03z0","日期");
		  pk_temp.setLength(100);
		  pk_temp.setKeyable(true);
		  temp.setNullable(false);
		  temp.setDatatype(DataType.STRING);
		  pk_table.addField(pk_temp);
		  dbWizard.addPrimaryKey(pk_table);
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
	  * 建立时间临时表
	  * @return
	  */	 
	 public String createTimeTemp()throws GeneralException
	 {
		 String table_name="analyse_time_"+userView.getUserName();
		 table_name=table_name.toLowerCase();
		 
		 DbWizard dbWizard =new DbWizard(this.conn);
		 dbWizard.dropTable(table_name);
		 
		 Table table=new Table(table_name);		
		 Field temp = new Field("orgid","组织编号");
		 temp.setDatatype(DataType.STRING);
		 temp.setLength(50);
		 temp.setKeyable(false);			
		 temp.setVisible(false);
		 table.addField(temp);
		 Field temp1=new Field("sDate","考勤日期");
		 temp1.setDatatype(DataType.STRING);
		 temp1.setLength(20);
		 temp1.setKeyable(false);			
		 temp1.setVisible(false);
		 table.addField(temp1);		
		 Field temp2=new Field(this.kq_dkind,"标志");
		 temp2.setDatatype(DataType.STRING);
		 temp2.setLength(2);
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
	 /**
	  * 初始化日期临时表数据表
	  * @param b0110
	  * @param date_Table
	  * @param start_date
	  * @param end_date
	  * @throws GeneralException
	  */
	 private void initializtion_date_Table(String b0110,String date_Table,String start_date,String end_date)throws GeneralException
	 {
		 BaseClassShift baseClassShift=new BaseClassShift(this.userView,this.conn);
		 ArrayList date_list=baseClassShift.getDatelist(start_date,end_date);   
		 ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.conn);
		 String rest_date=restList.get(0).toString();
		 String rest_b0110=restList.get(1).toString();		 
		 baseClassShift.initializtion_date_Table(date_list,rest_date,date_Table,rest_b0110,b0110);
	 }
	 /**
	  * 插入人员信息
	  * @param date_table
	  */
	 private void insertAnalyeEmps(String analyse_Tmp,String date_Table,String code,String kind,String start_date,String end_date)throws GeneralException
	 {
		 HashMap hashMap=null;
		 String codewhere="";
		 if(code!=null&&code.length()>0&&!"-1".equals(kind))
		 {
			 hashMap=new HashMap();
			 String b0110=getB0110(code,kind);
			 ArrayList dblist=new ArrayList();
			 if(this.db_list==null||this.db_list.size()<=0) {
                 dblist=RegisterInitInfoData.getB0110Dase(hashMap,userView,conn,b0110);
             } else {
                 dblist=this.db_list;
             }
			 if(dblist==null||dblist.size()<=0) {
                 return;
             }
			 String nbase="";
			 String whereIN="";
			 
			 if("1".equals(kind))
			 {
				 codewhere="e0122 like '"+code+"%'";
			 }else if("0".equals(kind))
			 {
					codewhere="e01a1 like '"+code+"%'";	
			 }else if("2".equals(kind))
			 {
					codewhere="b0110 like '"+code+"%'";	
			 }
			 for(int i=0;i<dblist.size();i++)
			 {
				 nbase=(String)dblist.get(i);
				 whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
				 initializtion_date_Table(b0110,date_Table,start_date,end_date);
				 insertEmpIntoTmp(analyse_Tmp,date_Table,nbase ,codewhere,whereIN);
			 }
		 }else if("-1".equals(kind))
		 {
			 String a0100=code.substring(3);
			 String nbase=code.substring(0,3);
			 String b0110=getB0110ForA0100(nbase,a0100);
			 codewhere=" a0100='"+a0100+"'";			 
			 initializtion_date_Table(b0110,date_Table,start_date,end_date);
			 insertEmpIntoTmp(analyse_Tmp,date_Table,nbase ,codewhere,"");			 
		 }else
		 {
			 ArrayList dblist=new ArrayList();
			 if(this.db_list==null||this.db_list.size()<=0) {
                 dblist=this.userView.getPrivDbList();
             } else {
                 dblist=this.db_list;
             }
			 for(int r=0;r<dblist.size();r++)
		 	 {
				 String userbase=dblist.get(r).toString();
			 	    
		 	        String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,userbase);		    
				     	    
		                //公休日
			   	    if(!userView.isSuper_admin())
					{   
			   	    	 String whereB0110=RegisterInitInfoData.selcet_OrgId(userbase,"b0110",whereIN);
						 ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.conn,whereB0110,"b0110");
						 for(int t=0;t<orgidb0110List.size();t++)
						 {
							 hashMap=new HashMap();
							 String b0110_one=orgidb0110List.get(t).toString();
							 String nbase=RegisterInitInfoData.getOneB0110Dase(hashMap,this.userView,userbase,b0110_one,this.conn);
							 /********按照该单位的人员库的操作*********/
							 if(nbase!=null&&nbase.length()>0)
							 {
								 initializtion_date_Table(b0110_one,date_Table,start_date,end_date); 
								 codewhere="b0110 like '"+b0110_one+"%'";
								 insertEmpIntoTmp(analyse_Tmp,date_Table,nbase ,codewhere,whereIN);
							 }
						 }
					}else
					{
						 ArrayList b0100list=RegisterInitInfoData.getAllBaseOrgid(userbase,"b0110",whereIN,this.conn);
						 for(int n=0;n<b0100list.size();n++)
						 {
							 hashMap=new HashMap();
							 String b0110_one=b0100list.get(n).toString();
							 String nbase=RegisterInitInfoData.getOneB0110Dase(hashMap,this.userView,userbase,b0110_one,this.conn);
							 /********按照该单位的人员库的操作*********/
							 if(nbase!=null&&nbase.length()>0)
							 {
								 initializtion_date_Table(b0110_one,date_Table,start_date,end_date);
								 codewhere="b0110 like '"+b0110_one+"%'";
								 insertEmpIntoTmp(analyse_Tmp,date_Table,nbase ,codewhere,whereIN);
							 }
						 }
					} 
		 	 }
		 }
		 if(this.initflag==null||!"1".equals(this.initflag)) {
             synchronizationInitTemp_Table(analyse_Tmp);//同步考勤方式
         }
	 }	
	 /********************************分析临时表初始化人员和班次信息*******************************************/
	 /**
	  * 初始化人员临时表把人员库的信息更新到里面
	  * @param analyse_Tmp
	  * @param date_table
	  * @param nbase
	  * @param codewhere
	  * @param whereIN
	  */
	 private void insertEmpIntoTmp(String analyse_Tmp,String date_table,String nbase ,String codewhere,String whereIN)
	 {
		StringBuffer sql=new StringBuffer();
		sql.append("insert into "+analyse_Tmp+"(q03z0,nbase,a0100,b0110,e0122,e01a1,a0101,");
		sql.append(""+this.card_no_temp_field+","+this.kq_dkind+",q03z3,flag)");
		sql.append(" select "+date_table+"."+this.kq_sDate+" as q03z0,");
		sql.append("'"+nbase+"' as nbase,");
		sql.append("a0100,b0110,e0122,e01a1,a0101,"+this.kq_card+" as "+this.card_no_temp_field+",");
		sql.append(this.kq_dkind+","+this.kq_type+",'1'");
		sql.append(" from "+nbase+"A01,"+date_table);
		sql.append(" where 1=1 ");	
		sql.append(" and NOT EXISTS(SELECT 1 FROM "+analyse_Tmp+" t1 where");
		sql.append(" "+date_table+"."+this.kq_sDate+"=t1.q03z0 and t1.nbase='"+nbase+"' and "+nbase+"A01.a0100=t1.a0100)");
		String kqtypeWhr=getKqTypeWhr(this.kq_type);
		if(kqtypeWhr!=null&&kqtypeWhr.length()>0)
		{
			sql.append(" and "+kqtypeWhr);
		}
		if(codewhere!=null && codewhere.length()>0)
		{
			sql.append(" and "+codewhere);
		}
		if(this.analyseType!=null&& "1".equals(this.analyseType))
		{
			//sql.append(" and ("+this.kq_card+" is not null or "+this.kq_card+"<>'')");
			sql.append(" and ("+Sql_switcher.isnull(this.kq_card, "'##'")+"<>'##')");
		}
		if(whereIN!=null&&whereIN.length()>0)
		{
			sql.append(" and a0100 in(select a0100 "+whereIN+")");
		}		
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			ArrayList list =new ArrayList();
			//System.out.println("初始化数据---〉"+sql.toString());
			dao.insert(sql.toString(),list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	 }
	 /**
	  * 从指定表插入（如：kq_emloy_change等保存有人员基本信息的表）	
	  * @param analyse_Tmp
	  * @param change_Temp
	  * @param date_Table
	  * @param start_date
	  * @param end_date
	  * @throws GeneralException
	  */
	 private void insertEmpIntoEmp(String analyse_Tmp,String change_Temp,String date_Table,String start_date,String end_date)throws GeneralException
	 {
		    StringBuffer changeSQL=new StringBuffer();
		    String where="flag=4 and status=1";
		    changeSQL.append("select DISTINCT b0110 from "+change_Temp);
		    changeSQL.append(" where "+where);
		    String b0110="";
		    ContentDAO dao=new ContentDAO(this.conn);
		    StringBuffer sql=null;
		    ArrayList list=new ArrayList();
		    RowSet rs=null;
		    try
		    {
		    	rs=dao.search(changeSQL.toString());
		    	while(rs.next())
		    	{
		    		sql=new StringBuffer();
		    		b0110=rs.getString("b0110");
				    initializtion_date_Table(b0110,date_Table,start_date,end_date);
					sql.append("insert into "+analyse_Tmp+"(q03z0,nbase,a0100,b0110,e0122,e01a1,a0101,"+this.kq_dkind+",flag)");					
					sql.append(" select "+date_Table+"."+this.kq_sDate+" as q03z0,");
					sql.append(" nbase,a0100,b0110,e0122,e01a1,a0101,"+this.kq_dkind+",'1'");					
					sql.append(" from "+change_Temp+","+date_Table);
					sql.append(" where  "+change_Temp+".flag=4 and "+change_Temp+".status=1");					
					dao.insert(sql.toString(),list);
		    	}
		    	changeSQL=new StringBuffer();
			    changeSQL.append("select DISTINCT nbase from "+change_Temp);
			    changeSQL.append(" where "+where);
			    rs=dao.search(changeSQL.toString());
		    	while(rs.next())
		    	{
		    	   String destTab=analyse_Tmp;
		    	   String srcTab=rs.getString("nbase")+"A01";//源表
		    	   String nbase=rs.getString("nbase");
		    	   if(srcTab==null||srcTab.length()<0) {
                       continue;
                   }
		   		   String strJoin=destTab+".A0100="+srcTab+".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
		   		   String  strSet=destTab+".q03z3="+srcTab+"."+this.kq_type+"`"+destTab+"."+this.card_no_temp_field+"="+srcTab+"."+this.kq_card;//更新串  xxx.field_name=yyyy.field_namex,....
		   		   String strDWhere=" "+destTab+".nbase='"+nbase+"'";//更新目标的表过滤条件
		   		   String strSWhere="";//源表的过滤条件  
		   		   String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);	
		   		   //System.out.println("更新人员的考勤方式--->"+update);
		   		   update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");		   		   
		   		   dao.update(update);
		    	}
		    }catch(Exception e)
		    {
		    	e.printStackTrace();
		    } finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
	 }
	 
	 /**
	  * 更新人员的考勤方式//以q03为标准
	  * @param table_temp
	  * @throws GeneralException
	  */
	 private void synchronizationInitTemp_Table(String table_temp)throws GeneralException
	 {
		 String destTab=table_temp;//目标表
		 String srcTab="q03";//源表
		 String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
		 strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".q03z3","'kq'")+"<>"+Sql_switcher.isnull(srcTab+".q03z3","'kq'");
		 String  strSet=destTab+".q03z3="+srcTab+".q03z3";//更新串  xxx.field_name=yyyy.field_namex,....
		 String strDWhere="";//更新目标的表过滤条件		 
		 //String strSWhere=srcTab+".nbase IS NOT NULL";//源表的过滤条件  
		 String strSWhere=Sql_switcher.isnull(srcTab+".nbase", "'##'")+"<>'##'";			
		 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
		 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
		 //System.out.println("更新人员的考勤方式--->"+update);
		 ContentDAO dao = new ContentDAO(this.conn);
		 try {			
			dao.update(update);
		 } catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}			
	 }
	 /**
	  * 删除临时表中部要求处理的考勤类型
	  * 
	  * @param temp_table
	  */
	 private void delDifferKqType(String temp_table,String start_date,String end_date)
	 {
		 if(this.analyseType==null||analyseType.length()<=0) {
             this.analyseType="100";
         }
		 StringBuffer sql=new StringBuffer();
		 ArrayList list =new ArrayList();
		 if(!"100".equals(this.analyseType))
		 {
			 sql.append("delete from "+temp_table);
			 sql.append(" where NOT ("+getKqTypeWhr("q03z3")+")");
			 ContentDAO dao=new ContentDAO(this.conn);
			 try
			 {
				 //System.out.println("删除临时表中部要求处理的考勤类型---->"+sql.toString());
				 dao.delete(sql.toString(),list);	
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		 }else
		 {
			 updateHandCard(temp_table);
		 }		
		 try
		 {
			 sql=new StringBuffer();	
			 sql.append("delete from "+temp_table+" WHERE  EXISTS (");
			 sql.append("select 1 from q03 where ");
			 sql.append("q03.q03z0>='"+start_date+"'");
			 sql.append(" and q03.q03z0<='"+end_date+"'");
			 sql.append(" and q03.q03z5 in ('02','03','04','08')");
			 sql.append(" and "+temp_table+".a0100=q03.a0100");
			 sql.append(" and "+temp_table+".nbase=q03.nbase");
			 sql.append(")");
			 dao.delete(sql.toString(),list);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
     /**
      * 从人员排班表中得到日班次
      * @param temp_table
      * @throws GeneralException
      */
	 private void insertEmployeeShiftToTmp(String temp_table)throws GeneralException
	 {
		 String destTab=temp_table;//目标表
		 String srcTab="kq_employ_shift";//源表
		 String strJoin=destTab+".A0100="+srcTab+".A0100 and  "+destTab+".nbase="+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
		 String  strSet=destTab+".class_id="+srcTab+".class_id";//更新串  xxx.field_name=yyyy.field_namex,....
		 String strDWhere="";//更新目标的表过滤条件
		 String strSWhere="";//源表的过滤条件  
		 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
		 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
		 //System.out.println("从人员排班表中得到日班次--->"+update);
		 ContentDAO dao = new ContentDAO(this.conn);
		 try {			
			dao.update(update);
		 } catch (Exception e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		}			
	 }
	 /**
	  * 把班次信息更新到临时表里面
	  * @param temp_table
	  * @throws GeneralException
	  */
	 private void insertClassInfoToTmp(String temp_table)throws GeneralException
	 {
		 String destTab=temp_table;//目标表
		 String srcTab="kq_class";//源表
		 String strJoin=destTab+".class_id="+srcTab+".class_id";
		 StringBuffer strSet=new StringBuffer();
		 strSet.append(destTab+".onduty_card_1="+srcTab+".onduty_card_1`");
		 strSet.append(destTab+".offduty_card_1="+srcTab+".offduty_card_1`");
		 strSet.append(destTab+".onduty_start_1="+srcTab+".onduty_start_1`");
		 strSet.append(destTab+".onduty_1="+srcTab+".onduty_1`");
	
		 strSet.append(destTab+".onduty_flextime_1="+srcTab+".onduty_flextime_1`");//弹性班
		 strSet.append(destTab+".be_late_for_1="+srcTab+".be_late_for_1`");
		 strSet.append(destTab+".absent_work_1="+srcTab+".absent_work_1`");
		 strSet.append(destTab+".onduty_end_1="+srcTab+".onduty_end_1`");		 
		 strSet.append(destTab+".offduty_start_1="+srcTab+".offduty_start_1`");
		 strSet.append(destTab+".leave_early_absent_1="+srcTab+".leave_early_absent_1`");
		 strSet.append(destTab+".leave_early_1="+srcTab+".leave_early_1`");
		 strSet.append(destTab+".offduty_1="+srcTab+".offduty_1`");
		 strSet.append(destTab+".offduty_flextime_1="+srcTab+".offduty_flextime_1`");//弹性班
		 strSet.append(destTab+".offduty_end_1="+srcTab+".offduty_end_1`");
		 
		 strSet.append(destTab+".onduty_card_2="+srcTab+".onduty_card_2`");
		 strSet.append(destTab+".offduty_card_2="+srcTab+".offduty_card_2`");
		 strSet.append(destTab+".onduty_start_2="+srcTab+".onduty_start_2`");
		 strSet.append(destTab+".onduty_2="+srcTab+".onduty_2`");
		 strSet.append(destTab+".onduty_flextime_2="+srcTab+".onduty_flextime_2`");//弹性班
		 strSet.append(destTab+".be_late_for_2="+srcTab+".be_late_for_2`");
		 strSet.append(destTab+".absent_work_2="+srcTab+".absent_work_2`");
		 strSet.append(destTab+".onduty_end_2="+srcTab+".onduty_end_2`");		 
		 strSet.append(destTab+".offduty_start_2="+srcTab+".offduty_start_2`");
		 strSet.append(destTab+".leave_early_absent_2="+srcTab+".leave_early_absent_2`");
		 strSet.append(destTab+".leave_early_2="+srcTab+".leave_early_2`");
		 strSet.append(destTab+".offduty_2="+srcTab+".offduty_2`");
		 strSet.append(destTab+".offduty_flextime_2="+srcTab+".offduty_flextime_2`");//弹性班
		 strSet.append(destTab+".offduty_end_2="+srcTab+".offduty_end_2`");
		 
		 strSet.append(destTab+".onduty_card_3="+srcTab+".onduty_card_3`");
		 strSet.append(destTab+".offduty_card_3="+srcTab+".offduty_card_3`");
		 strSet.append(destTab+".onduty_start_3="+srcTab+".onduty_start_3`");
		 strSet.append(destTab+".onduty_3="+srcTab+".onduty_3`");
		 strSet.append(destTab+".onduty_flextime_3="+srcTab+".onduty_flextime_3`");//弹性班
		 strSet.append(destTab+".be_late_for_3="+srcTab+".be_late_for_3`");
		 strSet.append(destTab+".absent_work_3="+srcTab+".absent_work_3`");
		 strSet.append(destTab+".onduty_end_3="+srcTab+".onduty_end_3`");		 
		 strSet.append(destTab+".offduty_start_3="+srcTab+".offduty_start_3`");
		 strSet.append(destTab+".leave_early_absent_3="+srcTab+".leave_early_absent_3`");
		 strSet.append(destTab+".leave_early_3="+srcTab+".leave_early_3`");
		 strSet.append(destTab+".offduty_3="+srcTab+".offduty_3`");
		 strSet.append(destTab+".offduty_flextime_3="+srcTab+".offduty_flextime_3`");//弹性班
		 strSet.append(destTab+".offduty_end_3="+srcTab+".offduty_end_3`");
		 
		 strSet.append(destTab+".onduty_card_4="+srcTab+".onduty_card_4`");
		 strSet.append(destTab+".offduty_card_4="+srcTab+".offduty_card_4`");
		 strSet.append(destTab+".onduty_start_4="+srcTab+".onduty_start_4`");
		 strSet.append(destTab+".onduty_4="+srcTab+".onduty_4`");
		 strSet.append(destTab+".onduty_flextime_4="+srcTab+".onduty_flextime_4`");//弹性班
		 strSet.append(destTab+".be_late_for_4="+srcTab+".be_late_for_4`");
		 strSet.append(destTab+".absent_work_4="+srcTab+".absent_work_4`");
		 strSet.append(destTab+".onduty_end_4="+srcTab+".onduty_end_4`");		 
		 strSet.append(destTab+".offduty_start_4="+srcTab+".offduty_start_4`");
		 strSet.append(destTab+".leave_early_absent_4="+srcTab+".leave_early_absent_4`");
		 strSet.append(destTab+".leave_early_4="+srcTab+".leave_early_4`");
		 strSet.append(destTab+".offduty_4="+srcTab+".offduty_4`");
		 strSet.append(destTab+".offduty_flextime_4="+srcTab+".offduty_flextime_4`");//弹性班
		 strSet.append(destTab+".offduty_end_4="+srcTab+".offduty_end_4`");
		 //other
		 strSet.append(destTab+".night_shift_start="+srcTab+".night_shift_start`");
		 strSet.append(destTab+".night_shift_end="+srcTab+".night_shift_end`");
		 strSet.append(destTab+".zeroflag="+srcTab+".zeroflag`");
		 strSet.append(destTab+".domain_count="+srcTab+".domain_count`");
		 strSet.append(destTab+".work_hours="+srcTab+".work_hours`");
		 strSet.append(destTab+".zero_absent="+srcTab+".zero_absent`");
		 strSet.append(destTab+".one_absent="+srcTab+".one_absent");	
		 String strDWhere="";//更新目标的表过滤条件
		 String strSWhere="";//源表的过滤条件  
		 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet.toString(),strDWhere,strSWhere);	
		 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
		 //System.out.println("从人员排班表中得到日班次--->"+update);
		 
		 ContentDAO dao = new ContentDAO(this.conn);
		 try {			
			dao.update(update);
			dao.update(setOnDuty(temp_table));
			if(!"1".equals(this.analyseType))//这个是初始化的时候不分析刷卡纪录
			{
				dao.update(upMustCard(temp_table));
			}
		 } catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	 }
	 /**
	  * 修改应出勤
	  * @param temp_table
	  * @return
	  */
	 private String  setOnDuty(String temp_table)
	 {
		 HashMap item_Map=(HashMap)this.kqItem_hash.get(kqItem_ONDUTY);	
		 String itemUnit="";
		 if(item_Map!=null)
		 {
			  itemUnit=(String)item_Map.get("item_unit");
		 }		
		 String ondutyTime = "work_hours";
		 if(itemUnit==null||itemUnit.length()<=0)
	   	 {
	   		 itemUnit=unit_HOUR;
	   	 }
	   	 if(itemUnit.equals(unit_HOUR))
	   	 {
	   		ondutyTime=ondutyTime+"/60";
	     }else if(itemUnit.equals(unit_MINUTE))
	   	 {
	   		 
	   	 }else 
	   	 {
	   		ondutyTime="1";
	   	 }
		 StringBuffer sql=new StringBuffer();
		 sql.append("update "+temp_table+" set");
		 sql.append(" q03z1 ="+ondutyTime+" where work_hours>0");
		 //System.out.println(sql.toString());
		 return sql.toString();
	 }
	 /**
	  * 如果分析的不完全是机器考勤人员的数据，则把所有班次置为无需刷卡状态
	  * @return
	  */
	 private String upMustCard(String temp_table)
	 {
		 StringBuffer sql=new StringBuffer();
		 sql.append("update "+temp_table+" set");
		 sql.append(" onduty_card_1='0',offduty_card_1='0',onduty_card_2 ='0',offduty_card_2='0',");
		 sql.append(" onduty_card_3='0',offduty_card_3='0',onduty_card_4 ='0',offduty_card_4='0'");
		 return sql.toString();
	 }
	 /*******************************人员信息初始化.结束*************************************/
	 
	 /**
	 * 得到申请表集,返回的hashmap是为了和Delphi Locate一样能有个定位的效果
	 * @param sTdate
	 * @param eTdate
	 * @param codewhere
	 * @return
	 */
	 private HashMap getApplyRecords(String fDayDataTmp,String sTdate,String eTdate,String codewhere)
	 {
		 //sTdate=start_date+" "+"00:00:00";
		 //eTdate=end_date+" "+"23:59:59";
		 
		 ArrayList dblist=new ArrayList();
		 if(this.db_list==null||this.db_list.size()<=0) {
             dblist=this.userView.getPrivDbList();
         } else {
             dblist=this.db_list;
         }
		 ArrayList list=null;
		 HashMap app_hash=new HashMap();
		 HashMap hashM=null;		 
		 String whereIN="";
		 String nbase="";
		 StringBuffer sql=null;
		 String o_Key="";
		 String n_Key="";
		 ContentDAO dao=new ContentDAO(this.conn);
		 SearchAllApp searchAllApp=new SearchAllApp(this.conn,this.userView);
		 String dert_itemid=searchAllApp.isDeductResttime("q11");//扣除考勤人员
		 RowSet rs=null;
		 try
		 {
			
			 for(int i=0;i<dblist.size();i++)
			 {
				 sql=new StringBuffer();
				 nbase=dblist.get(i).toString();
				 whereIN=RegisterInitInfoData.getWhereINSql(this.userView,nbase);		
				 ///加班申请表
				 if(dert_itemid==null||dert_itemid.length()<=0) {
                     sql.append("select nbase,a0100,e0122,q1103,q11z1,q11z3,q1104 from q11 ");
                 } else {
                     sql.append("select nbase,a0100,e0122,q1103,q11z1,q11z3,q1104,"+dert_itemid+" from q11 ");
                 }
			     sql.append(" where q11z5='03' and q11z0='01' and nbase='"+nbase+"'");
			     if(codewhere!=null&&codewhere.length()>0)
				 {
						sql.append(" and "+codewhere);
				 }
			     sql.append(kq_app_dateSQL("q11",sTdate,eTdate));
			     sql.append(" and Exists(select 1 from "+fDayDataTmp+" A where q11.a0100=A.a0100 and q11.nbase=A.nbase)");
			     sql.append(" and a0100 in(select a0100 "+whereIN+")");
			     sql.append(" order by a0100");
			     rs=dao.search(sql.toString());
			     while(rs.next())
			     {
			    	 n_Key=nbase+rs.getString("a0100")+"q11";
			    	 n_Key=n_Key.toUpperCase();
			    	 if(!n_Key.equals(o_Key))
			    	 {
			    		 app_hash.put(o_Key,list); 
			    		 list=new ArrayList();
			    		 o_Key=n_Key;
			    	 }
			    	 hashM=new HashMap();
			    	 hashM.put("nbase",nbase);
			    	 hashM.put("a0100",rs.getString("a0100"));
			    	 hashM.put("apptype",rs.getString("q1103"));
			    	 hashM.put("s_date",rs.getTimestamp("q11z1"));
			    	 hashM.put("e_date",rs.getTimestamp("q11z3"));
			    	 hashM.put("refShift",rs.getString("q1104"));
			    	 if(dert_itemid==null||dert_itemid.length()<=0) {
                         hashM.put("dert_itemid","0");
                     } else {
                         hashM.put("dert_itemid",rs.getString(dert_itemid));
                     }
			    	 list.add(hashM);
			     }
			     app_hash.put(n_Key,list); 
			     //公出申请表
			     sql=new StringBuffer();
			     sql.append("select nbase,a0100,e0122,q1303,q13z1,q13z3 from q13 ");
			     sql.append(" where q13z5='03' and q13z0='01' and nbase='"+nbase+"'");
			     if(codewhere!=null&&codewhere.length()>0)
				 {
						sql.append(" and "+codewhere);
				 }
			     sql.append(kq_app_dateSQL("q13",sTdate,eTdate));
			     sql.append(" and Exists(select 1 from "+fDayDataTmp+" A where q13.a0100=A.a0100 and q13.nbase=A.nbase)");
			     sql.append(" and a0100 in(select a0100 "+whereIN+")");
			     sql.append(" order by a0100");
			     rs=dao.search(sql.toString());
			     n_Key="";
			     o_Key="";
			     while(rs.next())
			     {
			    	 n_Key=nbase+rs.getString("a0100")+"q13";
			    	 n_Key=n_Key.toUpperCase();
			    	 if(!n_Key.equals(o_Key))
			    	 {
			    		 app_hash.put(o_Key,list); 
			    		 list=new ArrayList();
			    		 o_Key=n_Key;
			    	 }
			    	 hashM=new HashMap();
			    	 hashM.put("nbase",nbase);
			    	 hashM.put("a0100",rs.getString("a0100"));
			    	 hashM.put("apptype",rs.getString("q1303"));
			    	 hashM.put("s_date",rs.getTimestamp("q13z1"));
			    	 hashM.put("e_date",rs.getTimestamp("q13z3"));
			    	 hashM.put("refShift","");
			    	 //hashM.put("refShift",rs.getString("q1304"));
			    	 list.add(hashM);
			     }
			     app_hash.put(n_Key,list); 
			     //请假表
			     sql=new StringBuffer();
			     sql.append("select q1501,nbase,a0100,e0122,q1503,q15z1,q15z3 from q15 ");
			     sql.append(" where q15z5='03' and q15z0='01' and nbase='"+nbase+"' and "+Sql_switcher.isnull("q1517","0")+"=0"); 
			     if(codewhere!=null&&codewhere.length()>0)
				 {
						sql.append(" and "+codewhere);
				 }
			     sql.append(kq_app_dateSQL("q15",sTdate,eTdate));
			     sql.append(" and Exists(select 1 from "+fDayDataTmp+" A where q15.a0100=A.a0100 and q15.nbase=A.nbase)");
			     sql.append(" and a0100 in(select a0100 "+whereIN+")");
			     sql.append(" order by a0100");
			     rs=dao.search(sql.toString());
			     n_Key="";
			     o_Key="";
			     while(rs.next())
			     {
			    	 n_Key=nbase+rs.getString("a0100")+"q15";
			    	 n_Key=n_Key.toUpperCase();
			    	 if(!n_Key.equals(o_Key))
			    	 {
			    		 app_hash.put(o_Key,list); 
			    		 list=new ArrayList();
			    		 o_Key=n_Key;
			    	 }
			    	 hashM=new HashMap();
			    	 hashM.put("nbase",nbase);
			    	 hashM.put("a0100",rs.getString("a0100"));
			    	 hashM.put("apptype",rs.getString("q1503"));
			    	 hashM.put("s_date",rs.getTimestamp("q15z1"));
			    	 hashM.put("id",rs.getString("q1501"));			    	 
			    	 /*Date cancel_z3=getCancelDate(rs.getString("q1501"));
			    	 if(cancel_z3==null)
			    	   hashM.put("e_date",rs.getTimestamp("q15z3"));
			    	 else 
			    	   hashM.put("e_date",cancel_z3);*/
			    	 hashM.put("e_date",rs.getTimestamp("q15z3"));
			    	 hashM.put("refShift","");
			    	 //hashM.put("refShift",rs.getString("q1504"));
			    	 list.add(hashM);
			     }
			     app_hash.put(n_Key,list);
			     //销假
			     sql=new StringBuffer();
			     sql.append("select q1501,nbase,a0100,e0122,q1503,q15z1,q15z3,q1519 from q15 ");
			     sql.append(" where q15z5='03' and q15z0='01' and nbase='"+nbase+"' and "+Sql_switcher.isnull("q1517","0")+"=1"); 
			     if(codewhere!=null&&codewhere.length()>0)
				 {
						sql.append(" and "+codewhere);
				 }
			     sql.append(kq_app_dateSQL("q15",sTdate,eTdate));
			     sql.append(" and Exists(select 1 from "+fDayDataTmp+" A where q15.a0100=A.a0100 and q15.nbase=A.nbase)");
			     sql.append(" and a0100 in(select a0100 "+whereIN+")");
			     sql.append(" order by a0100");
			     rs=dao.search(sql.toString());
			     n_Key="";
			     o_Key="";
			     list=new ArrayList();
			     while(rs.next())
			     {
			    	 n_Key=nbase+rs.getString("a0100")+"q15";
			    	 n_Key=n_Key.toUpperCase();
			    	 if(!n_Key.equals(o_Key))
			    	 {
			    		 this.fLeaveBackDataH.put(o_Key,list); 
			    		 list=new ArrayList();
			    		 o_Key=n_Key;
			    	 }
			    	 hashM=new HashMap();
			    	 hashM.put("nbase",nbase);
			    	 hashM.put("a0100",rs.getString("a0100"));
			    	 hashM.put("apptype",rs.getString("q1503"));
			    	 hashM.put("s_date",rs.getTimestamp("q15z1"));
			    	 hashM.put("id",rs.getString("q1501"));
			    	 hashM.put("q1519",rs.getString("q1519"));			    	
			    	 hashM.put("e_date",rs.getTimestamp("q15z3"));
			    	 list.add(hashM);
			     }
			     this.fLeaveBackDataH.put(n_Key,list);
			 }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
		 return app_hash;
	 }
	 /**
	  * 查看请销假
	  * @param q1501
	  * @return
	  */
	 public Date getCancelDate(String q1501)
	 {
		 StringBuffer sql=new StringBuffer();
		 sql.append("select q15z3 from q15 where q1519='"+q1501+"'");
		 sql.append(" and q1517=1 and q15z5='03' and q15z0='01'");
		 RowSet rs=null;
		 Date z3=null;
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.conn);
			 rs=dao.search(sql.toString());
			 if(rs.next())
			 {
				 z3=rs.getTimestamp("q15z3");
			 }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
		 return z3;
	 }
	 /**********************************基本方法***********************************************/
	 /**
	  * 返回考勤方式where语句
	  * @return
	  */
	 private String getKqTypeWhr(String kqType)
	 {
		 String where="";
		 if(analyseType!=null&& "1".equals(analyseType))
		 {
			 where=Sql_switcher.isnull(kqType,"'kq'")+"='"+KqType_Machine+"' and "+Sql_switcher.isnull(kqType,"'kq'")+"<>'"+kqType_Leavekq+"'";
		 }else if(analyseType!=null&& "0".equals(analyseType))
		 {
			 where=Sql_switcher.isnull(kqType,"'kq'")+"<>'"+KqType_Machine+"' and "+Sql_switcher.isnull(kqType,"'kq'")+"<>'"+kqType_Leavekq+"'";
		 }else
		 {
			 where=Sql_switcher.isnull(kqType,"'kq'")+"<>'kq' and "+Sql_switcher.isnull(kqType,"'kq'")+"<>'"+kqType_Leavekq+"'";
		 }
		 return where;
	 }	
	 /**
	  * 修改临时表手工考勤人员都是部要求刷卡
	  * @param table_temp
	  */
	 private void updateHandCard(String table_temp)
	 {
	    	StringBuffer sql=new StringBuffer();
	    	sql.append("update "+table_temp+" set ");
	    	sql.append("onduty_card_1=?,offduty_card_1=?,onduty_card_2=?,offduty_card_2=?,");
	    	sql.append("onduty_card_3=?,offduty_card_3=?,onduty_card_4=?,offduty_card_4=? ");
	    	sql.append(" where "+getKqTypeWhr("q03z3"));
	    	ArrayList list=new ArrayList();
	    	list.add("0");
	    	list.add("0");
	    	list.add("0");
	    	list.add("0");
	    	list.add("0");
	    	list.add("0");
	    	list.add("0");
	    	list.add("0");
	    	try
	    	{
	    		ContentDAO dao=new ContentDAO(this.conn);
	    		//System.out.println(sql.toString());
	    		dao.update(sql.toString(),list);
	    	}catch(Exception e)
	    	{
	    	   e.printStackTrace();
	    	}
	 }
	 /**
		 * 返回单位编号
		 * @return
		 * @throws Exception
		 */
	 private  String getB0110(String code ,String kind)throws GeneralException
	 {
		 String b0110=code;
	     String codesetid="";
	     if("1".equals(kind)|| "0".equals(kind))
	     {
	         codesetid=code;
	         do
	         {
	        	String codeset[]=RegisterInitInfoData.getB0100(b0110,conn);
	        	if(codeset!=null&&codeset.length>=0)
	            {
	            	codesetid=codeset[0];
	            	b0110=codeset[1];
	            }
	         }while(!"UN".equals(codesetid));
	        	
	     }				       
	     return b0110;   
	 }
	 /**
	  * 返回开始时间业务表的时间范围的where
	  * @param app_type
	  * @param start_date
	  * @param end_date
	  * @return
	  */
	 private  String kq_app_dateSQL(String app_type,String start_date,String end_date)
	 {
			StringBuffer selectSQL=new StringBuffer();	
			selectSQL.append(" and (");
		    selectSQL.append(" ("+app_type+"z1<="+Sql_switcher.dateValue(start_date));	
		    selectSQL.append(" and "+app_type+"z3>="+Sql_switcher.dateValue(end_date)+")");
		    selectSQL.append(" or ("+app_type+"z1>="+Sql_switcher.dateValue(start_date));	
		    selectSQL.append(" and "+app_type+"z3<="+Sql_switcher.dateValue(end_date)+")");		   
		    selectSQL.append(" or ("+app_type+"z1<="+Sql_switcher.dateValue(start_date));
		    selectSQL.append(" and "+app_type+"z3>="+Sql_switcher.dateValue(start_date));
		    selectSQL.append(" and "+app_type+"z3<="+Sql_switcher.dateValue(end_date)+")");		   
		    selectSQL.append(" or ("+app_type+"z1>="+Sql_switcher.dateValue(start_date));	
		    selectSQL.append(" and "+app_type+"z1<="+Sql_switcher.dateValue(end_date));
		    selectSQL.append(" and "+app_type+"z3>="+Sql_switcher.dateValue(end_date)+")");
		    selectSQL.append(" )");
			return selectSQL.toString();
	 }	
	 /**
	  * 考勤规则的一个hashmap集
	  * @return
	  * @throws GeneralException
	  */
	 public HashMap count_Leave() throws GeneralException
	 {
	    	RowSet rs=null;	    	
	    	String kq_item_sql="select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";    	    	
	    	ContentDAO dao=new ContentDAO(this.conn);
	    	HashMap hashM=new HashMap();
	    	String fielditemid="";
	    	try
	    	{
	    	   rs =dao.search(kq_item_sql);
	    	   while(rs.next())
	    	   { 
	    		   HashMap hashm_one=new HashMap();	    		  
	    		   if(rs.getString("fielditemid")==null||rs.getString("fielditemid").length()<=0) {
                       continue;
                   }
	    		   ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    
	    		   for(int i=0;i<fielditemlist.size();i++)
	   	    	   {
	   	   	          FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	   	   	          fielditemid=rs.getString("fielditemid");	   	   	          
	   	   	          if(fielditemid.equalsIgnoreCase(fielditem.getItemid()))
	   	   	          {
	   	   	            //System.out.println(fielditemid+"---------"+fielditem.getItemid());
	   	   	            hashm_one.put("fielditemid",rs.getString("fielditemid"));
		    		    hashm_one.put("has_rest",PubFunc.DotstrNull(rs.getString("has_rest")));
		    		    hashm_one.put("has_feast",PubFunc.DotstrNull(rs.getString("has_feast")));
		    		    hashm_one.put("item_unit",PubFunc.DotstrNull(rs.getString("item_unit")));
		    		    hashm_one.put("sdata_src",PubFunc.DotstrNull(rs.getString("sdata_src")));
		    		    hashM.put(rs.getString("item_id"),hashm_one);
		    		    continue;
	   	   	          }
	   	    	   }
	    		   
	    	   }
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(e);
	    	}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
	    	return hashM;	    	
	 }
	 /**
	  * 返回临时表的一个结果集
	  * @param temp_table
	  * @return
	  * @throws GeneralException
	  */
	 private String  getAnalyseTempColumn(String temp_table)throws GeneralException
	  {
		  
		  StringBuffer columns=new StringBuffer();
		  columns.append("a0100,nbase,q03z0,a0101,b0110,e0122,e01A1,q03z1,");
		  columns.append(kqClassShiftColumns());
		  columns.append(",card_time,"+this.kq_dkind+",q03z3");		 
		  String sql="select "+columns.toString()+" from "+temp_table+" order by a0100,nbase,q03z0";		
		  return sql;
	  }
	  
	 /************开始分析*************/
     private boolean beginAnalyse(String analyse_Tmp,String codewhere,String statr_date,String end_date)throws GeneralException
     {
    	 boolean isCorrect=false;
    	 int notCPNum=0;
    	 statr_date=statr_date+" "+"00:00:00";
    	 end_date=end_date+" "+"23:59:59"; 
    	 
    	 DbSecurityImpl dbs = new DbSecurityImpl();
    	 this.stmt=createStatement();
    	 String sql=getAnalyseTempColumn(analyse_Tmp); 
    	 HashMap app_hash=getApplyRecords(analyse_Tmp,statr_date,end_date,codewhere);    	    	
    	 try
 		 {
    		 //System.out.println(sql);
    		 //ContentDAO dao=new ContentDAO(this.conn);
             // this.resultSet=dao.search(sql);
    	     dbs.open(conn, sql);
    		 this.resultSet=this.stmt.executeQuery(sql);    		 
        	 if(this.resultSet==null) {
                 return isCorrect;
             }
    		 String nbase="";
    		 String a0100="";
    		 String strDate="";
    		 Date d_curDate=null;
    		 String class_id="";
    		 String kqType="";    		
    		 float absentLen=0;//缺刷卡1次时间
    		 float overTimeLen=0;//平时加班时间长度
    		 HashMap item_hash=new HashMap();
    		 String dbkind="";//日期类型
    		 String strExcept=""; //异常情况类型
 		     while(this.resultSet.next())
 		     {
 		    	nbase=this.resultSet.getString("nbase");
 		    	a0100=this.resultSet.getString("a0100");
 		    	strDate=this.resultSet.getString("q03z0"); 	
 		    	d_curDate=DateUtils.getDate(strDate,"yyyy.MM.dd");
 		    	class_id=this.resultSet.getString("class_id");
 		    	kqType=this.resultSet.getString("q03z3");
 		    	dbkind=this.resultSet.getString(this.kq_dkind); 
 		    	this.inTime = null;
 		        this.outTime = null;
 		        this.fFactLeaveTime = 0;
 		        this.rs_FEmpDatas=null;//一天的刷开原始数据
 		        strExcept="";
 		    	/*if(a0100.equals("00000115")&&nbase.equals("Usr")&&strDate.equals("2009.07.07"))
 		    	{
 		    		System.out.println(strDate) ;
 		    	}else
 		    	{
 		    		continue;
 		    	}*/
 		      
 		    	if(class_id==null||class_id.length()<=0)
 		    	{
 		    		if(!"1".equals(this.analyseType)||!checkUnsteadyShiftClass(analyse_Tmp,this.resultSet,nbase,a0100,strDate,d_curDate))
 		    		{
 		    			this.empClassBean=new KqEmpClassBean();
 		    			this.empClassBean=this.empClassBean.getKqEmpClassBean(this.resultSet); 				    	
 		    			this.nightTimeLen=this.empClassBean.getNightTimeLen(strDate);
 		    			this.nightTimeLen=getfatNightTime(strDate,app_hash,nbase,a0100);
 		    			this.factOTimeLen=this.empClassBean.getOverTimeLen();
 		    			if("100".equals(this.analyseType))
 		    			{
 		    				if (kqType_hand.equals(kqType)) //日明细初始化时调用
 	 		    			{
 	 		    				calcBusiData(analyse_Tmp,nbase,a0100,strDate,d_curDate,app_hash,this.empClassBean); 	 		    				
 	 		    			} 
 		    			}else
 		    			{
 		    				if (kqType_hand.equals(kqType)||KqType_Machine.equals(kqType))
 	 		    			{
 	 		    				calcBusiData(analyse_Tmp,nbase,a0100,strDate,d_curDate,app_hash,this.empClassBean); 	 		    				
 	 		    			} 
 		    			}
 		    			
 		    			if(this.nightTimeLen>0)
 		    			{
 		    				HashMap night_Map=(HashMap)this.kqItem_hash.get(kqItem_Night); 
 		    				calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,night_Map,this.nightTimeLen,false);
 		    			} 	
 		    			if(this.factOTimeLen>0)
 		    			{
 		    				HashMap oTime_Map=(HashMap)this.kqItem_hash.get(kqItem_OGeneral); 
 		    				calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,oTime_Map,this.factOTimeLen,true);
 		    			}
 		    			//分析休息班刷卡
 		    			if("1".equals(this.analyseType)&&(class_id==null|| "".equals(class_id)|| "0".equals(class_id)))
 		    			{
 		    				getEmpDatasByDate(analyse_Tmp,nbase,a0100,strDate);
 		    				if(this.rs_FEmpDatas!=null)
 		    				{
 		    					this.rs_FEmpDatas.isBeforeFirst(); 		    					
 		    					while(this.rs_FEmpDatas.next())
 		    					{
 		    						ArrayList one_list=new ArrayList();
 		    						one_list.add(this.rs_FEmpDatas.getString("a0100"));
 		    						one_list.add(this.rs_FEmpDatas.getString("nbase"));
 		    						one_list.add(this.rs_FEmpDatas.getString("work_date"));
 		    						one_list.add(this.rs_FEmpDatas.getString("work_time"));
 		    						setCardDataStatus(one_list,1); 	 		    					
 		    					}
 		    				}
 		    			}
 		    			continue;
 		                
 		    		}
 		    	}
 		    	this.empClassBean=new KqEmpClassBean();
 		    	this.empClassBean=this.empClassBean.getKqEmpClassBean(this.resultSet);
		    	if("100".equals(this.analyseType))
	    		{
		    		this.nightTimeLen=this.empClassBean.getNightTimeLen(strDate);
		    		this.nightTimeLen=getfatNightTime(strDate,app_hash,nbase,a0100);
		    		this.factOTimeLen=this.empClassBean.getOverTimeLen();
		    		if (kqType_hand.equals(kqType)) //日明细初始化时调用
		    		{
		    			calcBusiData(analyse_Tmp,nbase,a0100,strDate,d_curDate,app_hash,empClassBean); 	 		    				
		    		} 
		    		HashMap night_Map=(HashMap)this.kqItem_hash.get(kqItem_Night); 	    					
	    			if(this.nightTimeLen>0) {
                        calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,night_Map,this.nightTimeLen,false);
                    }
	    			if(this.factOTimeLen>0)
		    			{
	    				    HashMap oTime_Map=(HashMap)this.kqItem_hash.get(kqItem_OGeneral); 
		    				calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,oTime_Map,this.factOTimeLen,true);
		    			}
	    		}else if (kqType_hand.equals(kqType)||KqType_Machine.equals(kqType))
	    		{
	    			calcBusiData(analyse_Tmp,nbase,a0100,strDate,d_curDate,app_hash,empClassBean);
	    				//***夜班**/
	    			HashMap night_Map=(HashMap)this.kqItem_hash.get(kqItem_Night); 
	    			this.nightTimeLen=this.empClassBean.getNightTimeLen(strDate);	
	    			this.nightTimeLen=getfatNightTime(strDate,app_hash,nbase,a0100);
	    			this.factOTimeLen=this.empClassBean.getOverTimeLen();
	    			if(this.nightTimeLen>0) {
                        calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,night_Map,this.nightTimeLen,false);
                    }
	    			if(this.factOTimeLen>0)
		    	    {
		    				HashMap oTime_Map=(HashMap)this.kqItem_hash.get(kqItem_OGeneral); 
		    				calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,oTime_Map,this.factOTimeLen,true);
		    		}
	    		} 	
		    	/*if(this.analyseType.equals("1"))
		    		continue;*/
		    	//情况一：休息班次
 		    	if("0".equals(this.empClassBean.getClass_id()))//休息班次
 		    	{
 		    		getEmpDatasByDate(analyse_Tmp,nbase,a0100,strDate);
 		    		if(this.rs_FEmpDatas!=null)
	    			{
	    					this.rs_FEmpDatas.isBeforeFirst(); 		    					
	    					while(this.rs_FEmpDatas.next())
	    					{
	    						ArrayList one_list=new ArrayList();
	    						one_list.add(this.rs_FEmpDatas.getString("a0100"));
	    						one_list.add(this.rs_FEmpDatas.getString("nbase"));
	    						one_list.add(this.rs_FEmpDatas.getString("work_date"));
	    						one_list.add(this.rs_FEmpDatas.getString("work_time"));
	    						setCardDataStatus(one_list,1); 	 		    					
	    					}
	    			}
 		    		continue;
 		    	}
 		    	//情况二：不要求刷卡的班次
 		    	if(!this.empClassBean.getIsMustCard())//不要求刷卡
 		    	{
 		    			//***平时加班**//*
 		    		if(!kqType_hand.equals(kqType)&&!KqType_Machine.equals(kqType))
		    		{
 		    			item_hash=(HashMap)this.kqItem_hash.get(kqItem_OGeneral); 
 		    			overTimeLen=empClassBean.getOverTimeLen();
 		    			if(overTimeLen>0)
 		    			{
 		    				calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,item_hash,overTimeLen,true);
	 		    		    //显示刷卡的结果是什么类型的　
	 		    			updateExceptDetial(analyse_Tmp,nbase,a0100,strDate,"加班");
 		    			}		
		    		}
 		    		continue;
 		    	}    
 		    	//情况三： 要求刷卡的班次
 		    	//得到班次的有效刷卡记录
 		    	isCorrect=getEmpDatasByClass(analyse_Tmp,nbase,a0100,strDate);
 		    			//得到班次的有效刷卡记录
 		    	if(this.empClassBean.getIsMustCard()&&!isCorrect)
 		    	{
                    continue;// 班次信息定义不完整！
 		    	}    
// 		    	**晴空分析结果数据cardPoints
 		    	initCardPoints(this.resultSet);
 		    	float [] rstVals =new float[8];//清空分析结果数据
 		    	clearRstTranOverTime();//清空延时加班数据
 		    	this.fFactFlextime=0;
 		    	
 		    	if(!this.rs_FEmpDatas.next())
 		    	{
                   //检查是否被换班
 		           //检查是否有人替班
 		    		if(this.empClassBean.getIsMustCard())
 		    		{
                        //将因请假，公出错过的刷卡点置为已刷
 		 		    	checkOverCardPoint(a0100,nbase,strDate,app_hash,empClassBean);
// 		 		    ****没有刷卡纪录.旷工
 	 		    		notCPNum=getNotCardPointNum(false);
 	 		    		if(notCPNum>0)
 	 		    		{
 	 		    			float work_hours=Float.parseFloat(this.empClassBean.getWork_hours());
 	 	 		    		item_hash=(HashMap)this.kqItem_hash.get(kqItem_WAbsent);
 	 	 		    		if(item_hash!=null)
 	 	 		    		{
 	 	 		    			String itemUnit=(String)item_hash.get("item_unit");
 	 	 	 		    		String fielditemid=(String)item_hash.get("fielditemid");
 	 	 	 		    		float timeLen=getUtilValue(itemUnit,work_hours,work_hours);
 	 	 	 		    		updateDataToTmp(analyse_Tmp,a0100,nbase,strDate,fielditemid,timeLen+"");
 	 	 	 		    	    updateExceptDetial(analyse_Tmp,nbase,a0100,strDate,"旷工");
 	 	 	 		    	    markedLackCard(analyse_Tmp,nbase,a0100,strDate);//却刷
 	 	 		    		} 
 	 		    		}
 		    		}	    		
 		    		continue;
 		    	}else
 		    	{
 		    		this.rs_FEmpDatas.beforeFirst();
 		    	} 		    	
                
 		    	/*if(!this.empClassBean.getIsMustCard())//不要求刷卡
 		    	{
 		    		continue;
 		    	}*/         
                ////有刷卡数据，则班次与刷卡时间进行对比 
 		    	this.fFactFlextime = this.empClassBean.getFlextimeLen();//实际使用的弹性时长 := 班次定义的弹性时长
 		    	//****班次与刷卡时间比较
 		    	this.rs_FEmpDatas.beforeFirst();  		    	
 		    	int nums=0;
 		    	this.isRestLeave1=false;
 		    	this.isRestLeave2=false;
 		    	this.fFactLeaveTime=0;
 		    	this.outTime=null;
 		    	this.inTime=null;
 		    	while(this.rs_FEmpDatas.next())
 		    	{
 		    		
 		    		checkCardTime(empClassBean,rstVals);	//检测匹配刷卡点
 		    		calcFactLeaveTimeLen(empClassBean,strDate,nums);     //离岗时间统计
 		    	}
 		    	//(2007.10.18)
 		    	////查找延时加班刷卡
 		    	checkTranOvertimeCard(empClassBean,strDate,this.rs_FEmpDatas); 		    	
               //迟到
 		    	if(rstVals[0]>1)
 		    	{
 		    		item_hash=(HashMap)this.kqItem_hash.get(kqItem_WLate);
 		    		calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,item_hash,rstVals[0],true);
 		    		strExcept="迟到";
 		    	}
 		       //早退
 		    	if(rstVals[1]>1)
 		    	{
 		    		item_hash=(HashMap)this.kqItem_hash.get(kqItem_WEarly);
 		    		calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,item_hash,rstVals[1],true);
 		    		strExcept="早退";
 		    	}
                //将因请假，公出错过的刷卡点置为已刷
 		    	checkOverCardPoint(a0100,nbase,strDate,app_hash,empClassBean);
 		       //旷工
               //得到要求刷而未刷卡的点数
 		    	notCPNum=getNotCardPointNum(false);
 		    	if(notCPNum>0) {
                    markedLackCard(analyse_Tmp,nbase,a0100,strDate);//缺刷
                }
 		    		//getNeedCardPointNum();
 		    	if (notCPNum>0&&notCPNum != getNeedCardPointNum())
 		    	{
 		    		absentLen=Float.parseFloat(empClassBean.getZero_absent());//小时 		    		
 		    		if(absentLen>0) {
                        rstVals[2]=rstVals[2]+(absentLen*notCPNum*60);
                    } else {
                        rstVals[2]=rstVals[2]+(Float.parseFloat(empClassBean.getWork_hours()))/2;
                    }
 		    	}
 		    	else if(notCPNum>0)
 		    	{
 		    		float workTiemLen=Float.parseFloat(empClassBean.getWork_hours());
 		    		rstVals[2]=workTiemLen; //对缺刷无旷工标准的，暂时扣减一半工时
 		    	}
 		    	if(rstVals[2]>1)
 		    	{
 		    		item_hash=(HashMap)this.kqItem_hash.get(kqItem_WAbsent);
 		    		calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,item_hash,rstVals[2],true);
 		    		strExcept="旷工";
 		    	}	
 		       //平时加班
 		    	overTimeLen=empClassBean.getOverTimeLen();
 		    	if(overTimeLen>1)
 		    	{
 		    		 //加班段要求刷卡并刷卡完整或加班段不要求刷卡
 		    	   item_hash=(HashMap)this.kqItem_hash.get(kqItem_OGeneral);
 		    	   String  point= calcCardPoints();
 		    	   if("0".equals(point))
 		    	   {
 		    		  calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,item_hash,overTimeLen,true);
 		    		  strExcept = "加班";
 		    	   }else if("1".equals(point))
 		    	   {
 		    		  calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,item_hash,overTimeLen/2,true);
 		    		  strExcept = "加班";
 		    	   }
 		    	}
                //插入延时加班数据
 		    	if(this.rstTranOverTime[0]!=null&&this.rstTranOverTime[1]!=null)
 		    	{
 		    		float t_f=getPartMinute(this.rstTranOverTime[0],this.rstTranOverTime[1]);
 		    		if(t_f>=this.kqParam.getMin_overtime())
 		    		{
 		    			if(haveAppRecord(nbase,a0100,this.rstTranOverTime))
 		    			{
 		    				insertTranOverTimeData(empClassBean,this.rstTranOverTime,d_curDate);
 		    			}
 		    		}
 		    	}//节假日加班（排班排在了节假日，默认为节假日加班）
 		    	float workTiemLen=Float.parseFloat(empClassBean.getWork_hours());
 		    	float spareWorkTiemLen=getWorkTimeLen(workTiemLen,rstVals,overTimeLen);
 		    	if(dbkind.equals(dkHoliday)&&spareWorkTiemLen>1)
 		    	{
 		    		item_hash=(HashMap)this.kqItem_hash.get(kqItem_OFeast);
 		    		calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,item_hash,spareWorkTiemLen,true);
 		    		strExcept = "加班";
 		    	}
// 		    	统计离岗时间
                if(this.fFactLeaveTime>0)
                {
                	item_hash=(HashMap)this.kqItem_hash.get(KqItem_LEAVETIME);
 		    		calcOthenLen(nbase,a0100,d_curDate,strDate,analyse_Tmp,item_hash,rstVals[0],true);
                }
                if( strExcept!=null&&strExcept.length()>0) {
                    updateExceptDetial(analyse_Tmp,nbase,a0100,strDate,strExcept);
                }
 		    }
 		    if("1".equals(this.analyseType)&&this.kqParam.getNeed_busicompare()!=null&& "1".equals(this.kqParam.getNeed_busicompare()))
 		    {
// 		    	对比分析业务申请与实际刷卡情况
 		    	compareBusiWithFactCards(analyse_Tmp,statr_date,end_date,codewhere);
 		    }
 		 }catch(Exception e)
 		 {
 			  e.printStackTrace();
 			  //throw GeneralExceptionHandler.Handle(new GeneralException("","数据分析失败！可能的原因：班次信息定义不完整。","",""));
 		 }finally
 		 {
 			dbs.close(conn);
 		 }
 		 return true;
     }
     
     /**
      * 0:完整 1:不完成 2:没有刷
      * @return
      */
     private String calcCardPoints()
     {
    	String  point="";
        //加班时段刷卡状况 0：完整 1：少刷 2：没刷
    	//加班段要求刷卡并刷卡完整或加班段不要求刷卡
    	if((this.cardPoints[3][0]&&this.cardPoints[3][1]&&this.cardPoints[3][2]&&this.cardPoints[3][3])||(!this.cardPoints[3][0]&&!this.cardPoints[3][2]))
  	    {
    		point="0";//两点都要求并都刷or两点都不要求
  	    }else if((this.cardPoints[3][0]&&!this.cardPoints[3][1]&&this.cardPoints[3][2]&&!this.cardPoints[3][3])||(this.cardPoints[3][0]&&!this.cardPoints[3][1]&&!this.cardPoints[3][2])||(this.cardPoints[3][2]&&!this.cardPoints[3][3]&&!this.cardPoints[3][0]))
  	    {
  	    	point="2";
  	    }else
  	    {
  	    	point="1";
  	    }
    	return point;	
     }
     private float getWorkTimeLen(float workTimeLen,float[] rstVals,float overTimeLen)
     {
    	 float timeLen=workTimeLen-rstVals[0]-rstVals[1]-rstVals[2]+overTimeLen;
    	 return timeLen;
     }
     /**
      *  班次与刷卡时间比较  
      *  --------------------------------------------------------------
      * 时段(i)    |上班需打卡  | 上班已打卡 | 下班需打卡 | 下班已打卡
      * --------------------------------------------------------------
      * CardPoints |  [i][0]    |   [i][1]   |    [i][2]  |    [i][3]
      * --------------------------------------------------------------
      * 值         | True/False | True/False | True/False | True/False
      * --------------------------------------------------------------
      * @param empClassBean
      * @return   //刷卡分析结果值 0 迟到 1 早退 2 旷工 3 平时加班 4 夜班 
      */
     private float[] checkCardTime(KqEmpClassBean empClassBean,float[] rstVals)throws GeneralException
     {
    	 try
    	 {
    		 //班次时间定义异常！无法分析
    		 if(empClassBean.getOnduty_1()==null||empClassBean.getOnduty_1().length()<=0)
    		 {
    			 throw GeneralExceptionHandler.Handle(new GeneralException("","班次时间定义异常！无法分析","",""));
    		 }
    		 if(kqParam.getCard_interval()>0&&IsRepeatCardData(this.rs_FEmpDatas))
    		 {
    			 ArrayList one_list=new ArrayList();
				 one_list.add(this.rs_FEmpDatas.getString("a0100"));
				 one_list.add(this.rs_FEmpDatas.getString("nbase"));
				 one_list.add(this.rs_FEmpDatas.getString("work_date"));
				 one_list.add(this.rs_FEmpDatas.getString("work_time"));
    			 setCardDataStatus(one_list,-1);
    			 return rstVals;
    		 }
    		 //
    		 String inout_flag=this.rs_FEmpDatas.getString("inout_flag");
    		 String work_setTiem=this.rs_FEmpDatas.getString("work_time");
    		 Date work_time=DateUtils.getDate(work_setTiem,"HH:mm");
    		 String work_strDate=this.rs_FEmpDatas.getString("work_date");
    		 Date work_date=DateUtils.getDate(work_strDate,"yyyy.MM.dd");
    		 String q03z0Str=this.resultSet.getString("q03z0");
    		 Date q03z0_d=DateUtils.getDate(q03z0Str,"yyyy.MM.dd");    	
    		 DecimalFormat myformat = new DecimalFormat("#.00");
    		 float work_to_qz0=0;
    		 Date  class_Time=null;
    		 String class_strTime="";
    		 Date per_Time=null;
    		 Date oTime=null;
    		 Date cTime=null;
    		 Date lTime=null;    		 
    		 long lflexTime=0;
    		 ArrayList one_list=new ArrayList();
			 one_list.add(this.rs_FEmpDatas.getString("a0100"));
			 one_list.add(this.rs_FEmpDatas.getString("nbase"));
			 one_list.add(this.rs_FEmpDatas.getString("work_date"));
			 one_list.add(this.rs_FEmpDatas.getString("work_time"));
    		 for(int i=1;i<=4;i++)
    		 {
    			 //上班段
    			 //onduty=empClassBean.getOnduty(i+""); 
    			 if(this.cardPoints[i-1][0]&&!this.cardPoints[i-1][1])//只计合法刷卡时间范围内的第一条
    			 {
    				 if(inout_flag!=null&& "-1".equals(inout_flag)) {
                         return rstVals;
                     }
    				 Date on_Time=DateUtils.getDate(empClassBean.getOnduty(i+""),"HH:mm");
    				 class_strTime=empClassBean.getOnduty_start(i+"");
    				 class_Time=DateUtils.getDate(class_strTime,"HH:mm");
    				 per_Time=class_Time;
    				 class_strTime=empClassBean.getBe_late_for(i+"");
    				 class_Time=DateUtils.getDate(class_strTime,"HH:mm");
    				 if(inTimeZone(work_time,per_Time,class_Time))//正常
    				 {
    					 this.cardPoints[i-1][1]=true;     					 
    	    			 setCardDataStatus(one_list,0);
                         //检查弹性时间
    	    			 if(i==1&&empClassBean.getFlextimeLen()>0)//上午上班弹性时间
    	    			 {
    	    				 cTime=DateUtils.getDate(work_strDate+" "+work_setTiem,"yyyy.MM.dd HH:mm");
    	    				 oTime=DateUtils.getDate(q03z0Str+" "+empClassBean.getOnduty_1(),"yyyy.MM.dd HH:mm");
    	    				 if(empClassBean.getOnduty_flextime_1()!=null&&empClassBean.getOnduty_flextime_1().length()>0)
    	    				 {
    	    					 lTime=DateUtils.getDate(q03z0Str+" "+empClassBean.getOnduty_flextime_1(),"yyyy.MM.dd HH:mm");
                                  //如果刷卡时间在上班时间～上班弹性时间之间，
    	    		              //计算实际享有的下班弹性时间
    	    		              //如果刷卡时间上班弹性时间～迟到时间之间
    	    		              //弹性时间长度为定义时长
    	    		              //否则不计弹性时间
    	    					 if(getPartMinute(cTime,oTime)<0&&getPartMinute(cTime,lTime)>=0) {
                                     this.fFactFlextime =getPartMinute(oTime,cTime);
                                 } else if(getPartMinute(cTime,lTime)<0) {
                                     this.fFactFlextime =getPartMinute(oTime,lTime);
                                 } else {
                                     this.fFactFlextime =0;
                                 }
    	    			         if(this.fFactFlextime>empClassBean.getFlextimeLen()) {
                                     this.fFactFlextime=empClassBean.getFlextimeLen();
                                 }
    	    				 }
    	    			 }if(i==2&&empClassBean.getFlextimeLen()>0)
    	    			 {
    	    				 cTime=DateUtils.getDate(work_strDate+" "+work_setTiem,"yyyy.MM.dd HH:mm");
    	    				 oTime=DateUtils.getDate(q03z0Str+" "+empClassBean.getOnduty_2(),"yyyy.MM.dd HH:mm");
    	    				 if(empClassBean.getOnduty_flextime_2()!=null&&empClassBean.getOnduty_flextime_2().length()>0)
    	    				 {
    	    					 lTime=DateUtils.getDate(q03z0Str+" "+empClassBean.getOnduty_flextime_2(),"yyyy.MM.dd HH:mm");
    	    					 if(getPartMinute(cTime,oTime)<0&&getPartMinute(cTime,lTime)>=0) {
                                     this.fSecFactFlextime =getPartMinute(oTime,cTime);
                                 } else if(getPartMinute(cTime,lTime)<0) {
                                     this.fSecFactFlextime =getPartMinute(oTime,lTime);
                                 } else {
                                     this.fSecFactFlextime =0;
                                 }
      	    			         if(this.fSecFactFlextime>empClassBean.getFlextimeLen()) {
                                     this.fSecFactFlextime=empClassBean.getFlextimeLen();
                                 }
    	    				 }
    	    				 if (!this.cardPoints[0][1])//如果第一时段上班没有刷卡 
                             {
                                 this.fFactFlextime = 0;
                             }
    	    				 if(this.fFactFlextime==0)
    	    				 {
    	    					 this.fFactFlextime=this.fSecFactFlextime;
    	    				 }else if(Math.abs(this.fFactFlextime - empClassBean.getFlextimeLen())<=1)
    	    				 {
    	    					 this.fSecFactFlextime = - empClassBean.getFlextimeLen();    	    					 
    	    				 }else
    	    				 {
    	    					 if(this.fSecFactFlextime+this.fFactFlextime-30>1)
    	    					 {
    	    						  rstVals[2] = rstVals[2] + this.fSecFactFlextime + this.fFactFlextime- 30;
    	    			              this.fSecFactFlextime = this.fFactFlextime- 30;
    	    			              this.fFactFlextime = 30;    	    			               
    	    					 }else
    	    					 {
    	    						 this.fFactFlextime=this.fSecFactFlextime + this.fFactFlextime;
    	    					 }
    	    				 }
    	    			 }
    					 break;
    				 }
    				 if (i > 2&&this.fSecFactFlextime !=0) {
                         this.fSecFactFlextime = 0;
                     }

    				 per_Time = class_Time;
    				 class_strTime=empClassBean.getAbsent_work(i+"");
    				 class_Time=DateUtils.getDate(class_strTime,"HH:mm");
    				 ///////迟到
    				 if(inTimeZone(work_time,per_Time,class_Time))
    				 {
    					 this.cardPoints[i-1][1]=true;      					 
    					 rstVals[0]=rstVals[0]+calcTimeSpan(on_Time,work_time);
    					 if (i ==1) {
                             rstVals[0] = rstVals[0] - this.fFactFlextime;
                         }
    	    			 setCardDataStatus(one_list,0);    					
    					 break;
    				 }
    				 per_Time = class_Time;
    				 class_strTime=empClassBean.getOnduty_end(i+"");
    				 class_Time=DateUtils.getDate(class_strTime,"HH:mm");
                     //正常迟到矿工
    				 if(inTimeZone(work_time,per_Time,class_Time))
    				 {
    					 this.cardPoints[i-1][1]=true;
    					 rstVals[2]=rstVals[2]+calcTimeSpan(on_Time,work_time);
    					 if (i ==1) {
                             rstVals[2] = rstVals[2] - this.fFactFlextime;
                         }
    	    			 setCardDataStatus(one_list,0); 
    					 break;
    				 }
    			 }
    			 work_to_qz0=getPartMinute(work_date,q03z0_d);
    			 if(work_to_qz0>0)//防止在零点之前刷卡
                 {
                     break;
                 }
    			 //下班段
    			if(this.cardPoints[i-1][2]&&!this.cardPoints[i-1][3])//下班段不应是进刷卡
    			{
    				if(inout_flag!=null&& "1".equals(inout_flag)) {
                        break;
                    }
    				lflexTime=0;
    				if(this.fFactFlextime>0&&(i-1)==empClassBean.getOffdutyTimeIndex())
    				{
    					String f_s=myformat.format(this.fFactFlextime);
    					lflexTime=Long.parseLong(f_s);
    				}
    				Date off_Time=DateUtils.getDate(empClassBean.getOffduty(i+""),"HH:mm");
    				class_strTime = empClassBean.getOffduty_start(i+"");
    				class_Time=DateUtils.getDate(class_strTime,"HH:mm");
    				class_Time.setTime(class_Time.getTime()+(lflexTime*60*1000L));
    				per_Time=class_Time;
    				class_strTime=empClassBean.getLeave_early_absent(i+"");
    				class_Time=DateUtils.getDate(class_strTime,"HH:mm");
    				if(inTimeZone(work_time,per_Time,class_Time))//早退矿工
   				    {
    					this.cardPoints[i-1][3]=true;
    					rstVals[2]=rstVals[2]+calcTimeSpan(work_time,off_Time);    					
   	    			    setCardDataStatus(one_list,0); 
    					break;
   				    }
    				per_Time = class_Time;
    				class_strTime=empClassBean.getLeave_early(i+""); 
    				class_Time=DateUtils.getDate(class_strTime,"HH:mm");
    				if(inTimeZone(work_time,per_Time,class_Time))//早退
   				    {
    					if(calcTimeSpan(work_time,off_Time)>0.5) {
                            rstVals[1]=rstVals[1]+calcTimeSpan(work_time,off_Time);
                        }
    					this.cardPoints[i-1][3]=true;
    					setCardDataStatus(one_list,0); 
    					break;
   				    }
    				per_Time = class_Time;
    				class_strTime=empClassBean.getOffduty_end(i+"");
    				class_Time=DateUtils.getDate(class_strTime,"HH:mm");
    				if(inTimeZone(work_time,per_Time,class_Time))//正常下班
   				    {
    					this.cardPoints[i-1][3]=true;
    					setCardDataStatus(one_list,0); 
    					break;
   				    }
    			}
    		 }
    		 setCardDataStatus(one_list,1); 
    	 }catch(Exception e)
    	 {
    		 throw GeneralExceptionHandler.Handle(e);
    	 }
    	 return rstVals;
     }
     /**
      * 得到要求刷而未刷卡的点数
      * @return
      */
     private int  getNotCardPointNum()
     {
    	 int num=0;
    	 for(int i=0;i<=2;i++)
    	 {
             //上班要求刷而未刷
    		 if(this.cardPoints[i][0]&&!this.cardPoints[i][1])
    		 {
    			 num++; 
    		 }
              //下班要求刷而未刷
    		 if(this.cardPoints[i][2]&&!this.cardPoints[i][3])
    		 {
    			 num++; 
    		 }
    	 }
    	 return num;
     }
     private int getNeedCardPointNum()
     {
    	 int num=0;
    	 int toNum=3;
    	 for(int i=0;i<=toNum;i++)
    	 {
    		 if(this.cardPoints[i][0]) {
                 num++;
             }
             ;
    	    //下班要求刷
    	    if( this.cardPoints[i][2]) {
                num++;
            }
             ;
    	 }
    	 return num;
     }
     /**
      * 得到要求刷而未刷卡的点数
      * @return
      */
     private int  getNotCardPointNum(boolean isCorrect)
     {
    	 int num=0;
    	 int toNum=2;
    	 if(!isCorrect) {
             toNum=2;
         } else {
             toNum=3;
         }
    	 for(int i=0;i<=toNum;i++)
    	 {
             //上班要求刷而未刷
    		 if(this.cardPoints[i][0]&&!this.cardPoints[i][1]&& "1".equals(this.empClassBean.getOnduty_card((i+1)+"")))
    		 {
    			 num++; 
    		 }
              //下班要求刷而未刷
    		 if(this.cardPoints[i][2]&&!this.cardPoints[i][3]&& "1".equals(this.empClassBean.getOffduty_card((i+1)+"")))
    		 {
    			 num++; 
    		 }
    	 }
    	 return num;
     }
     /**
      * 判断重复刷卡
      * @param CardData
      * @return
      * @throws Exception
      */
     private boolean IsRepeatCardData(RowSet CardData)throws Exception
 	 {
 		boolean isCorrect=false;
 		if(!CardData.next()||CardData.isFirst()||CardData.isLast()) {
            return false;
        }
 		String cur_tiem_str=CardData.getString("work_date")+" "+CardData.getString("work_time")+":00";
 		Date cur_Time=DateUtils.getDate(cur_tiem_str,"yyyy.MM.dd HH:mm:ss");
 		CardData.previous();
 		if(!CardData.isBeforeFirst())
 		{
 			String pre_tiem_str=CardData.getString("work_date")+" "+CardData.getString("work_time")+":00";
 			Date pre_Time=DateUtils.getDate(pre_tiem_str,"yyyy.MM.dd HH:mm:ss");
 			float t_f=getPartMinute(pre_Time,cur_Time);
 			if(Math.abs(t_f)<kqParam.getCard_interval()) {
                return true;
            }
 		} 	
 		CardData.next();
 		return isCorrect;
 	 }
     private boolean inTimeZone(Date cardTime,Date FTD ,Date TTD)
     {
    	 float itemLen1=getPartMinute(FTD,TTD);
    	 if(itemLen1>=0)
    	 {
    		 float f_to_c=getPartMinute(FTD,cardTime);
    		 float c_to_t=getPartMinute(cardTime,TTD);
    		 if(f_to_c>=0&&c_to_t>=0) {
                 return true;
             }
    	 }else//起始结束时间之间跨零点
    	 {
    		 Date zone_1=DateUtils.getDate("24:00","HH:mm");
    		 Date zone_0=DateUtils.getDate("00:00","HH:mm");
    		 float f_to_c=getPartMinute(FTD,cardTime);
    		 float c_to_z1=getPartMinute(cardTime,zone_1);
    		 float c_to_z0=getPartMinute(zone_0,cardTime);
    		 float c_to_t=getPartMinute(cardTime,TTD);
    		 if((f_to_c>=0&&c_to_z1>=0)||(c_to_z0>=0&&c_to_t>=0)) {
                 return true;
             }
    	 }
    	 return false;
     }
     /**
      * 计算时间
      * @param onTime
      * @param nowTime
      * @return
      */
     private int calcTimeSpan(Date onTime,Date nowTime)
     {
    	 float time_f=0;
    	 int time_i=0;
    	 time_f=getPartMinute(onTime,nowTime);
 		 if(time_f<=0)
 		{
 			Date zone_d=DateUtils.getDate("24:00","HH:mm");
 			time_f=getPartMinute(onTime,zone_d);
 			zone_d=DateUtils.getDate("00:00","HH:mm");
 			time_f=getPartMinute(zone_d,nowTime)+time_f;
 		}
 		time_i=Math.round(time_f);
		time_i=Math.abs(time_i);
		return time_i;
     }
     /**
      * 得到班次有效刷卡纪录
      * @param table_temp
      * @param nbase
      * @param a0100
      * @param empClassBean
      * @param strDate
      * @return
      */
     private boolean getEmpDatasByClass(String table_temp,String nbase,String a0100,String strDate)throws GeneralException
     {
    	 try
    	 {
    		 Date FTD=getFromTime(this.empClassBean,strDate);
        	 Date TTD=getToTime(this.empClassBean,strDate);
        	 if(FTD==null||TTD==null) {
                 return false;
             }
        	 float itemLen=getPartMinute(FTD,TTD);
        	 if(itemLen<=0)
        	 {
        		 TTD= DateUtils.addDays(TTD,1);
        	 }
        	 getEmpDatas(table_temp,nbase,a0100,strDate,FTD,TTD,true);        	 
        	 
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();    		 
    		 throw GeneralExceptionHandler.Handle(new GeneralException("","数据分析失败！可能的原因：班次信息定义不完整。","","")); 
    	 } 
    	 return true;
     }
    /**
     * 查找某时间范围内的刷卡记录。 是否将找到的刷卡记录更新到临时表中（正常分析刷卡数据时用）。
     * @param table_temp
     * @param nbase
     * @param a0100
     * @param strDate
     * @param FTD
     * @param TTD
     * @param empClassBean
     * @param isInsertCardTime,是否统计刷卡时间，放到临时表里面
     */
     private void getEmpDatas(String table_temp,String nbase,String a0100,String strDate,Date FTD,Date TTD,boolean isInsertCardTime)
     {
    	 String fWorkDt="work_date"+Sql_switcher.concat()+"work_time";
 		 StringBuffer strWhe=new StringBuffer();
 		 strWhe.append(" where upper(nbase)='"+nbase.toUpperCase()+"'");
 		 strWhe.append(" and a0100='"+a0100+"'");
 		 strWhe.append(" and "+fWorkDt+">='"+DateUtils.format(FTD,"yyyy.MM.ddHH:mm")+"'");
 		 strWhe.append(" and "+fWorkDt+"<='"+DateUtils.format(TTD,"yyyy.MM.ddHH:mm")+"'");
 		 /*strWhe.append(" and "+Sql_switcher.isnull("location","'kq'")+"<>'补签到'");
 		 strWhe.append(" and "+Sql_switcher.isnull("location","'kq'")+"<>'补签退'");*/
 		 strWhe.append(" and sp_flag='03'");
 		 StringBuffer sql=new StringBuffer();
    	 sql.append("select DISTINCT * from kq_originality_data");
    	 sql.append(strWhe.toString());    	 
         sql.append(" ORDER BY work_date, work_time");        
         StringBuffer cardTimes=new StringBuffer();
         try
         {        	
            //将查到的原始记录状态设成为处理
        	String updateSQL="update kq_originality_data set status=NULL "+strWhe.toString();
 			dao.update(updateSQL);
        	this.rs_FEmpDatas=dao.search(sql.toString());
        	if(table_temp==null|| "".equals(table_temp)) {
                return;
            }
         	if(isInsertCardTime)
         	{
         		while(this.rs_FEmpDatas.next())
             	{
             		cardTimes.append(this.rs_FEmpDatas.getString("work_time")+",");
             	}
             	if(cardTimes.toString()!=null&&cardTimes.toString().length()>0)
             	{
             		cardTimes.setLength(cardTimes.length()-1);
             		this.empClassBean.setCard_time(cardTimes.toString());
             		updateDataToTmp(table_temp,a0100,nbase,strDate,"card_time",cardTimes.toString());
             	}
             	this.rs_FEmpDatas.beforeFirst();
         	}         	
         }catch(Exception e)
         {
         	e.printStackTrace();
         }
     }
     /**
      * 取得刷卡起始时间
      * @param empClassBean
      * @return
      */
     private Date getFromTime(KqEmpClassBean empClassBean,String strDate)
     {
    	 Date FT=null;
    	 String onduty_card=empClassBean.getOnduty_card("1");
    	 Date start_FD=null;
    	 if(onduty_card!=null&&onduty_card.length()>0)
    	 {
    		 Date STD=DateUtils.getDate(empClassBean.getOnduty_start_1(),"HH:mm");
    		 FT=DateUtils.getDate(empClassBean.getOnduty_1(),"HH:mm");
    		 start_FD=DateUtils.getDate(strDate+" "+empClassBean.getOnduty_start_1(),"yyyy.MM.dd HH:mm");
    		 float itemLen=getPartMinute(STD,FT);
    		 if(itemLen<=0)
    		 {
    			 start_FD=DateUtils.addDays(start_FD,-1);
    		 }
    	 }else
    	 {
    		 start_FD=DateUtils.getDate(strDate+" "+empClassBean.getOnduty_1(),"yyyy.MM.dd HH:mm");
    	 }
    	 return start_FD;
     }
     /**
      * 取得刷卡结束时间
      * @param empClassBean
      * @return
      */
     private Date getToTime(KqEmpClassBean empClassBean,String strDate)
     {
    	 Date end_TD=null;
    	 for(int i=4;i>=1;i--)
    	 {
    		 String offduty_end=empClassBean.getOffduty_end(i+"");
    		 String offduty=empClassBean.getOffduty(i+"");
    		 if(offduty_end!=null&&offduty_end.length()>0&&offduty!=null&&offduty.length()>0)
    		 {
    			 end_TD=DateUtils.getDate(strDate+" "+offduty_end,"yyyy.MM.dd HH:mm");
    			 Date off_TD=DateUtils.getDate(strDate+" "+offduty,"yyyy.MM.dd HH:mm");
    			 float itemLen=getPartMinute(off_TD,end_TD);
    			 if(itemLen<=0)
    			 {
    				 end_TD=DateUtils.addDays(end_TD,1);    				 
    			 }
    			 break;
    		 }else if(offduty!=null&&offduty.length()>0)
    		 {
    			 Date off_TD=DateUtils.getDate(strDate+" "+offduty,"yyyy.MM.dd HH:mm");
    			 Date on_TD=DateUtils.getDate(strDate+" "+empClassBean.getOnduty(i+""),"yyyy.MM.dd HH:mm");
    			 float itemLen=getPartMinute(on_TD,off_TD);
    			 if(itemLen<=0)
    			 {
    				 end_TD=DateUtils.addDays(off_TD,1);
    			 }else
    			 {
    				 end_TD=off_TD;
    			 }
    			 break;
    		 }
    		 
    	 }
    	 return end_TD;
     }
     /**
      *  //插入刷卡分析结果
      * @param table_temp
      * @param a0100
      * @param nbase
      * @param strDate
      * @param fieldItem
      * @param valueStr
      */
     private void updateDataToTmp(String table_temp,String a0100,String nbase,String strDate,String fieldItem,String valueStr)
     {
    	 if(fieldItem==null||fieldItem.length()<=0) {
             return;
         }
    	 StringBuffer sql=new StringBuffer();
    	 sql.append("update "+table_temp+" set");
    	 sql.append(" "+fieldItem+"=? where a0100=? and nbase=? and q03z0=?");
    	 ArrayList list =new ArrayList();
    	 list.add(valueStr);
    	 list.add(a0100);
    	 list.add(nbase);
    	 list.add(strDate);
    	 try
    	 {
    		 this.dao.update(sql.toString(),list);
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
     }
     /***************考勤申请业务******************/
     private void calcBusiData(String temp_table,String nbase,String a0100,String strDate,Date d_curDate,HashMap app_hash,KqEmpClassBean empClassBean)throws GeneralException
     {
    	 Date perOffTime=null;
    	 String perOffTime_str="";    	  
    	 String strFTime="";//班次开始时间
    	 String strTTime="";//班次结束时间
    	 Date FTime=null;
    	 Date TTime=null;
    	 Date FDT=null;
    	 Date TDT=null;
    	 float time_f=0;
    	 try
    	 {
    		 String kqType=this.resultSet.getString("q03z3");
             if(kqType==null || kqType.length()<=0) {
                 kqType="";
             }
    		 if(kqType.equals(kqType_Nokq)||kqType.length()<=0) {
                 return;
             }
    		  // 夜班时段检测    		 
   		     Date NFDT=null;
   		     Date NTDT=null;
   		     this.calcNight=false;
   		     this.haveOTime=false;
   		     String nightShiftStart=this.resultSet.getString("night_shift_start");
   		     String nightShiftEnd=this.resultSet.getString("night_shift_end");   		     
   		     
   		     if(nightShiftEnd==null||nightShiftEnd.length()<=0||nightShiftStart==null||nightShiftStart.length()<=0||nightShiftStart==nightShiftEnd)
   		     {
   		    	this.calcNight=false;
   		     }else
   		     {
   		    	this.calcNight=true;
   		    	NFDT=DateUtils.getDate(strDate+" "+nightShiftStart,"yyyy.MM.dd HH:mm");
   		    	NTDT=DateUtils.getDate(strDate+" "+nightShiftEnd,"yyyy.MM.dd HH:mm");
   		    	Date nightShiftStartTime=DateUtils.getDate(nightShiftStart,"HH:mm");
   		    	Date nightShiftEndTime=DateUtils.getDate(nightShiftEnd,"HH:mm");
   		    	String onduty_1=this.resultSet.getString("onduty_1");
   		    	if(onduty_1==null||onduty_1.length()<=0) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("","数据分析失败！可能的原因：班次信息定义不完整。","",""));
                }
   		    	Date onTime=DateUtils.getDate(onduty_1,"HH:mm");
   		    	time_f=getPartMinute(nightShiftStartTime,onTime);
   		    	if(time_f>0)
   		    	{
   		    		NFDT=DateUtils.addDays(NFDT,1);
   		    		NTDT=DateUtils.addDays(NTDT,1);
   		    	}
   		    	time_f=getPartMinute(nightShiftStartTime,nightShiftEndTime);
   		    	if(time_f<0)
   		    	{
   		    		NTDT=DateUtils.addDays(NTDT,1);
   		    	}     		    	
   		     }
             //加班时段检测   		       		      
   		      String onduty_4=this.resultSet.getString("onduty_4");
   		      String offduty_4=this.resultSet.getString("offduty_4");
   		      Date OFDT=null;
   		      Date OTDT=null;
   		      if(onduty_4==null||onduty_4.length()<=0||offduty_4==null||offduty_4.length()<=0||onduty_4==offduty_4)
   		      {
   		    	this.haveOTime=false;
   		      }else
   		      {
   		    	this.haveOTime=true;
   		    	OFDT=DateUtils.getDate(strDate+" "+onduty_4,"yyyy.MM.dd HH:mm");
   		    	OTDT=DateUtils.getDate(strDate+" "+offduty_4,"yyyy.MM.dd HH:mm");
   		    	Date onduty_4Time=DateUtils.getDate(onduty_4,"HH:mm");
   		    	Date offduty_4Time=DateUtils.getDate(offduty_4,"HH:mm");
   		    	String onduty_1=this.resultSet.getString("onduty_1");
   		    	if(onduty_1==null||onduty_1.length()<=0) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("","数据分析失败！可能的原因：班次信息定义不完整。","",""));
                }
   		    	Date onTime=DateUtils.getDate(onduty_1,"HH:mm");
   		    	time_f=getPartMinute(onduty_4Time,onTime);
   		    	if(time_f>0)
   		    	{
   		    		OFDT=DateUtils.addDays(OFDT,1);
   		    		OTDT=DateUtils.addDays(OTDT,1);
   		    	}
   		    	time_f=getPartMinute(onduty_4Time,offduty_4Time);
   		    	if(time_f<0)
   		    	{
   		    		OTDT=DateUtils.addDays(OTDT,1);
   		    	}  
   		      }   		    
    		 //非公休日
    		 if(this.resultSet.getString("class_id")!=null&&!"0".equals(this.resultSet.getString("class_id"))&&this.resultSet.getString("class_id").length()>0)
    		 {
    			 for(int i=0;i<3;i++)
    			 {
    				 strFTime=this.resultSet.getString("onduty_"+(i+1));
    				 strTTime=this.resultSet.getString("offduty_"+(i+1));
    				 if((strFTime==null||strFTime.length()<=0)||(strTTime==null||strTTime.length()<=0)) {
                         break;
                     }
    				 FTime=DateUtils.getDate(strFTime,"HH:mm");
    				 TTime=DateUtils.getDate(strTTime,"HH:mm");
    				 if(perOffTime_str!=null&&perOffTime_str.length()>0)
    				 {
    					 perOffTime=DateUtils.getDate(perOffTime_str,"HH:mm");     					
    					 time_f=getPartMinute(perOffTime,FTime);
    					 if(time_f<0)
    					 {
    						strDate=DateUtils.format(DateUtils.addDays(d_curDate,1),"yyyy.MM.dd");
    					 } 
    				 }
    				 perOffTime_str=strTTime;
    				 time_f=getPartMinute(FTime,TTime);
    				 if(time_f>0)
    				 {
    						 FDT=DateUtils.getDate(strDate+" "+strFTime,"yyyy.MM.dd HH:mm");
    						 TDT=DateUtils.getDate(strDate+" "+strTTime,"yyyy.MM.dd HH:mm");
    				 }else
    				 {
    						 FDT=DateUtils.getDate(strDate+" "+strFTime,"yyyy.MM.dd HH:mm");
    						 strDate=DateUtils.format(DateUtils.addDays(d_curDate,1),"yyyy.MM.dd");
    						 TDT=DateUtils.getDate(strDate+" "+strTTime,"yyyy.MM.dd HH:mm");
    				 }    				 
    				 calcAppTimeLen(temp_table,app_hash,nbase,a0100,d_curDate,strDate,FDT,TDT,NFDT,NTDT,OFDT,OTDT);
    			 }
    		 }else//非工作日
    		 {
    			 Date RFDT = DateUtils.getDate(strDate + " 00:00:00","yyyy.MM.dd HH:mm:ss");
    		     Date RTDT = DateUtils.getDate(strDate + " 24:00:00","yyyy.MM.dd HH:mm:ss");
    		     String app_key=nbase+a0100+"q11";//加班申请
    	    	 ArrayList app_list=(ArrayList)app_hash.get(app_key.toUpperCase()); 
    	    	 String class_id="";
    	    	 boolean haveKqClass=false;
    	    	 if(app_list!=null&&app_list.size()>0)
    	    	 {
    	    		 class_id=calcRefShiftTimeLen(app_list,RFDT,RTDT);
    	    		 if("xx".equals(class_id))
    	    		 {
    	    			 String default_rest_classid=this.kqParam.getDefault_rest_kqclass();
    	    			 if(default_rest_classid!=null&&!"0".equals(default_rest_classid))
    	    			 {
    	    				 class_id=default_rest_classid;
    	    			 }else
    	    			 {
    	    				 class_id="0";
    	    			 }
    	    			 haveKqClass=true;
    	    		 }
    	    	 }
    	    	 if(class_id!=null&&!"0".equals(class_id)&&class_id.length()>0)
    	    	 {
    	    	     KqUtilsClass kqUtils = new KqUtilsClass(this.conn);
    	    		 UnKqClassBean classbean = kqUtils.getKqClassShiftFromClassID(class_id);
    	    		 if(classbean.getClass_id()!=null&&!"0".equals(classbean.getClass_id()))
     		    	 {
     		    		 String off_d=classbean.getOffduty();
     		    		 String on_d=classbean.getOnduty();
     		    		 if(off_d!=null&&off_d.length()>0&&on_d!=null&&on_d.length()>0)
     		    		 {
     		    			 Date on_Time=DateUtils.getDate(on_d,"HH:mm");
         		    		 Date off_Time=DateUtils.getDate(off_d,"HH:mm");
         		    		 float time_1=getPartMinute(on_Time,off_Time);
         		    		 if(time_1<0)
         		    		 {
         		    			 FDT = DateUtils.getDate(strDate +" "+ off_d+ ":00","yyyy.MM.dd HH:mm:ss");
         		    		 }           		    		 
     		    		 }
     		    		 calcAppTimeLen(temp_table,app_hash,nbase,a0100,d_curDate,strDate,RFDT,RTDT,NFDT,NTDT,OFDT,OTDT);
     		    	 }
    	    	 }else
    	    	 {
    	    		//*****前一条记录
        		     this.resultSet.previous();    		      
        		     //如果前一天有排班
        		     if(!this.resultSet.isBeforeFirst())
        		     {
        		    	 String nbase_2=this.resultSet.getString("nbase");
            		     nbase_2=nbase_2!=null&&nbase_2.length()>0?nbase_2:"";
            		     String a0100_2=this.resultSet.getString("a0100");
            		     a0100_2=a0100_2!=null&&a0100_2.length()>0?a0100_2:"";  
            		     if(nbase_2.equals(nbase)&&a0100_2.equals(a0100))
            		     {
            		    	 if(this.resultSet.getString("class_id")!=null&&!"0".equals(this.resultSet.getString("class_id")))
            		    	 {
            		    		 Date on_Time=getOndutyTime();
            		    		 Date off_Time=getOffdutyTime(3);
            		    		 if(on_Time!=null&&off_Time!=null)
            		    		 {
            		    			 float time_1=getPartMinute(on_Time,off_Time);
                		    		 if(time_1<0)
                		    		 {
                		    			 FDT = DateUtils.getDate(strDate +" "+ DateUtils.format(off_Time,"HH:mm")+ ":00","yyyy.MM.dd HH:mm:ss");
                		    		 }   
            		    		 }
            		    		 calcAppTimeLen(temp_table,app_hash,nbase,a0100,d_curDate,strDate,RFDT,RTDT,NFDT,NTDT,OFDT,OTDT);
            		    	 }else if(haveKqClass)
                 		     {
                  		    	  calcAppTimeLen(temp_table,app_hash,nbase,a0100,d_curDate,strDate,RFDT,RTDT,NFDT,NTDT,OFDT,OTDT);
                  		     }
            		    }else
            		    {
                           // 判断前一天排的是跨天班        		    	
           		    	   UnKqClassBean classbean=getKqClassShift(nbase,a0100,DateUtils.addDays(d_curDate,-1));
           		    	   if(classbean.getClass_id()!=null&&!"0".equals(classbean.getClass_id()))
           		    	   {
           		    		 String off_d=classbean.getOffduty();
           		    		 String on_d=classbean.getOnduty();
           		    		 if(off_d!=null&&off_d.length()>0&&on_d!=null&&on_d.length()>0)
           		    		 {
           		    			 Date on_Time=DateUtils.getDate(on_d,"HH:mm");
               		    		 Date off_Time=DateUtils.getDate(off_d,"HH:mm");
               		    		 float time_1=getPartMinute(on_Time,off_Time);
               		    		 if(time_1<0)
               		    		 {
               		    			 FDT = DateUtils.getDate(strDate +" "+ off_d+ ":00","yyyy.MM.dd HH:mm:ss");
               		    		 }           		    		 
           		    		 }
           		    		 calcAppTimeLen(temp_table,app_hash,nbase,a0100,d_curDate,strDate,RFDT,RTDT,NFDT,NTDT,OFDT,OTDT);
           		    	   }else if(haveKqClass)
               		       {
               		    	  calcAppTimeLen(temp_table,app_hash,nbase,a0100,d_curDate,strDate,RFDT,RTDT,NFDT,NTDT,OFDT,OTDT);
               		       }
            		    }    		    	 
        		     }else
        		     {
                         //判断前一天排的是跨天班
        		    	 UnKqClassBean classbean=getKqClassShift(nbase,a0100,DateUtils.addDays(d_curDate,-1));
        		    	 if(classbean.getClass_id()!=null&&!"0".equals(classbean.getClass_id()))
        		    	 {
        		    		 String off_d=classbean.getOffduty();
        		    		 String on_d=classbean.getOnduty();
        		    		 if(off_d!=null&&off_d.length()>0&&on_d!=null&&on_d.length()>0)
        		    		 {
        		    			 Date on_Time=DateUtils.getDate(on_d,"HH:mm");
            		    		 Date off_Time=DateUtils.getDate(off_d,"HH:mm");
            		    		 float time_1=getPartMinute(on_Time,off_Time);
            		    		 if(time_1<0)
            		    		 {
            		    			 FDT = DateUtils.getDate(strDate +" "+ off_d+ ":00","yyyy.MM.dd HH:mm:ss");
            		    		 }        		    		 
        		    		 }
        		    		 calcAppTimeLen(temp_table,app_hash,nbase,a0100,d_curDate,strDate,RFDT,RTDT,NFDT,NTDT,OFDT,OTDT);
        		    	 }else if(haveKqClass)
             		     {
             		    	calcAppTimeLen(temp_table,app_hash,nbase,a0100,d_curDate,strDate,RFDT,RTDT,NFDT,NTDT,OFDT,OTDT);
             		     }
        		    	 
        		     }
        		     this.resultSet.next();//******还原
    	    	 }
                 
    		 }
    		 String app_key=nbase+a0100+"q11";//加班申请
    		 ArrayList app_list=(ArrayList)app_hash.get(app_key.toUpperCase()); 
    		 if(app_list!=null&&app_list.size()>0) {
                 calcGernalOverTime(temp_table,app_list,nbase,a0100,d_curDate,strDate,OFDT,OTDT,0);
             }
    		 //平时加班
    		 if(this.haveOTime==true) {
                 getClassOverTime(temp_table,app_hash,nbase,a0100,d_curDate,strDate,NFDT,NTDT,OFDT,OTDT,empClassBean);
             }
    		 
    	 }catch(Exception e){
    		 e.printStackTrace();
    		 throw GeneralExceptionHandler.Handle(e);
    	 }
     }     
    
     /**
      * 计算有效考勤业务数据
      * @param app_hash  考勤业务数据
      * @param nbase
      * @param a0100
      * @param FDT
      * @param TDT
      */
     private void calcAppTimeLen(String temp_table,HashMap app_hash,String nbase,String a0100,Date d_curDate,String strDate,Date FDT,Date TDT,Date NFDT,Date NTDT,Date OFDT,Date OTDT)throws GeneralException
     {
    	 String app_key="";    	 
    	if(this.no_tranData!=null&& "1".equals(no_tranData)) {
            return;
        }
    	 HashMap hash=new HashMap(); 
    	 /*float factNightLen=0;////实上夜班时长
    	 float factTimeLen=0;*/
    	 
    	 app_key=nbase+a0100+"q15";//请假申请
    	 ArrayList app_list=(ArrayList)app_hash.get(app_key.toUpperCase()); 
    	 app_list=(ArrayList)app_hash.get(app_key.toUpperCase());  
    	 if(app_list!=null&&app_list.size()>0)
    	 {
    		 hash=calcTimeLen(temp_table,app_list,nbase,a0100,d_curDate,strDate,FDT,TDT,"q15",NFDT,NTDT,OFDT,OTDT);
    		/* Float nightF=(Float)hash.get("factNightLen");
        	 if(nightF!=null)
        		 factNightLen=nightF.floatValue();
        	 Float otimeF=(Float)hash.get("factOTimeLen");
        	 if(otimeF!=null)
        		 factTimeLen=factTimeLen+otimeF.floatValue();*/
    	 }
    	 app_key=nbase+a0100+"q11";//加班申请
    	 app_list=(ArrayList)app_hash.get(app_key.toUpperCase()); 
    	 if(app_list!=null&&app_list.size()>0)
    	 {
    		 calcTimeLen(temp_table,app_list,nbase,a0100,d_curDate,strDate,FDT,TDT,"q11",NFDT,NTDT,OFDT,OTDT);    		 
    		 //平时加班
    		 //calcGernalOverTime(temp_table,app_list,nbase,a0100,d_curDate,strDate,OFDT,OTDT,0);
    	 }
    	 app_key=nbase+a0100+"q13";//公出申请
    	 app_list=(ArrayList)app_hash.get(app_key.toUpperCase());  
    	 if(app_list!=null&&app_list.size()>0)
    	 {
    		 calcTimeLen(temp_table,app_list,nbase,a0100,d_curDate,strDate,FDT,TDT,"q13",NFDT,NTDT,OFDT,OTDT);
    	 }
     }
     /**
      * 班次排班的加班
      * @param temp_table
      * @param app_hash
      * @param nbase
      * @param a0100
      * @param d_curDate
      * @param strDate
      * @param NFDT
      * @param NTDT
      * @param OFDT
      * @param OTDT
      * @throws GeneralException
      */
     public void getClassOverTime(String temp_table,HashMap app_hash,String nbase,String a0100,Date d_curDate,String strDate,Date NFDT,Date NTDT,Date OFDT,Date OTDT,KqEmpClassBean empClassBean)throws GeneralException
     {
    	 String app_key=nbase+a0100+"q15";//请假申请
    	 ArrayList app_list=(ArrayList)app_hash.get(app_key.toUpperCase());     	
    	 HashMap hashM=null;
    	 String app_type="";
    	 float factNightLen=0;
    	 float factTimeLen=0;
    	 float oTimeOverLen=0;
    	 if(this.haveOTime||this.calcNight)//班次第四个时间段
		 {
    		Date s_date=null;
        	Date e_date=null;
    	    if(app_list!=null&&app_list.size()>0)
    	    {
    	    	for(int i=0;i<app_list.size();i++)
    	    	{
    	    		 hashM=(HashMap)app_list.get(i);
    	    		 s_date=(Date)hashM.get("s_date");
    	    		 e_date=(Date)hashM.get("e_date");
    	    		 HashMap item_Map=(HashMap)this.kqItem_hash.get(kqItem_OGeneral);
        			 if(item_Map==null) {
                         continue;
                     }
        			 app_type=(String)hashM.get("apptype");
        			 String fielditemid=(String)item_Map.get("fielditemid");
        			 if(fielditemid==null||fielditemid.length()<=0) {
                         continue;
                     }
        			 if(this.calcNight)
            		 {
            			 factNightLen =factNightLen+calcTimSpan(NFDT,NTDT,s_date,e_date);
            		 }            		
            		 if(this.haveOTime) {
                         factTimeLen =factTimeLen+calcTimSpan(OFDT,OTDT,s_date,e_date);
                     }
        		 }
    		 }
    	    app_key=nbase+a0100+"q11";//加班
    	    app_list=(ArrayList)app_hash.get(app_key.toUpperCase()); 
    	    if(app_list!=null&&app_list.size()>0)
    	    {
    	    	for(int i=0;i<app_list.size();i++)
    	    	{
    	    		 hashM=(HashMap)app_list.get(i);
    	    		 s_date=(Date)hashM.get("s_date");
    	    		 e_date=(Date)hashM.get("e_date");
    	    		 HashMap item_Map=(HashMap)this.kqItem_hash.get(kqItem_OGeneral);
        			 if(item_Map==null) {
                         continue;
                     }
        			 app_type=(String)hashM.get("apptype");
        			 String fielditemid=(String)item_Map.get("fielditemid");
        			 if(fielditemid==null||fielditemid.length()<=0) {
                         continue;
                     }
        			 if(this.calcNight)
            		 {
            			 factNightLen =factNightLen+calcTimSpan(NFDT,NTDT,s_date,e_date);
            		 }            		 
            		 if(this.haveOTime)
            		 {
            			 oTimeOverLen=calcTimSpan(OFDT,OTDT,s_date,e_date);
                		 factTimeLen=factTimeLen+oTimeOverLen;
            		 }
    	    	}
    		 }
    	     if (this.calcNight)
       	     {
       		    this.nightTimeLen=this.nightTimeLen-factNightLen;
       	     }   
    	     if(this.haveOTime) {
                 this.factOTimeLen=this.factOTimeLen-factTimeLen;
             }
    	    	 
		 }
    			
    	  
    	 
     }
     /**
      * 考勤业务分析基类
      * @param temp_table
      * @param app_list
      * @param nbase
      * @param a0100
      * @param strDate
      * @param FDT
      * @param TDT
      * @param kqItem_hash
      */
     private HashMap calcTimeLen(String temp_table,ArrayList app_list,String nbase,String a0100,Date d_curDate,String strDate,Date FDT,Date TDT,String app_type,Date NFDT,Date NTDT,Date OFDT,Date OTDT)throws GeneralException
     {
    	 HashMap hashM=null;
    	 String apptype="";
    	 Date s_date=null;
    	 Date e_date=null;
    	 HashMap item_Map=null;
    	 String fielditemid="";
    	 float factNightLen=0;
    	 float factTimeLen=0;
    	 float timeLen=0;  
    	 float fLeaveTimeLen=0;
    	 for(int i=0;i<app_list.size();i++)
    	 {
    		 hashM=(HashMap)app_list.get(i);
    		 apptype=(String)hashM.get("apptype");
    		 //System.out.print(apptype);
    		 if(apptype!=null&&apptype.indexOf(kqItem_OGeneral)==0) {
                 continue;//平时加班分出来算
             }
    		 timeLen=0;
    		 fLeaveTimeLen=0;
    		 s_date=(Date)hashM.get("s_date");
    		 e_date=(Date)hashM.get("e_date"); 
    		 String id=(String)hashM.get("id");
    		 
    		 //如果有销假，业务结束时间取销假时间
    		 if(this.fLeaveBackDataH!=null&&checkAppType(kqItem_Leave,apptype))
    		 {
    			String leaveBackkey=(String)hashM.get("nbase")+(String)hashM.get("a0100")+"q15";
    			leaveBackkey=leaveBackkey.toUpperCase();
    			ArrayList oneLeaveBack=(ArrayList)this.fLeaveBackDataH.get(leaveBackkey);
    			if(oneLeaveBack!=null&&oneLeaveBack.size()>0)
    			{
    				HashMap backHash=null;
    				for(int r=0;r<oneLeaveBack.size();r++)
    				{
    					backHash=(HashMap)oneLeaveBack.get(r);
    					String q1519=(String)backHash.get("q1519");
    					if(q1519==null||q1519.length()<=0) {
                            continue;
                        }
    					if(id!=null&&id.equalsIgnoreCase(q1519))
    					{
    						Date s_BackDate=(Date)backHash.get("s_date");
    						Date e_BackDate=(Date)backHash.get("e_date");
    						if(calcTimSpan(s_date,e_date,s_BackDate,e_BackDate)>0.01) {
                                fLeaveTimeLen=calcTimSpan(FDT,TDT,s_BackDate,e_BackDate)+fLeaveTimeLen;
                            }
    					}
    				}
    			}
    				
    		 }
    		 /*System.out.println(DateUtils.format(s_date,"yyyy.MM.dd HH:mm:ss")+"---"+DateUtils.format(d_curDate,"yyyy.MM.dd HH:mm:ss"));
    		 System.out.println(DateUtils.format(FDT,"yyyy.MM.dd HH:mm:ss")+"--"+DateUtils.format(TDT,"yyyy.MM.dd HH:mm:ss"));
    	     System.out.println(DateUtils.format(s_date,"yyyy.MM.dd HH:mm:ss")+"--"+DateUtils.format(e_date,"yyyy.MM.dd HH:mm:ss"));*/
    		    		 
    		 timeLen=calcTimSpan(FDT,TDT,s_date,e_date)-fLeaveTimeLen;
    		 UnKqClassBean class_bean=null;
    		 //这段就是对加班申请的参考班次的应用
    		 if(checkAppType(kqItem_Overtime,apptype)&&timeLen>0.01)
    		 {
    			 String refShift=(String)hashM.get("refShift");
    			 if(refShift!=null&&refShift.length()>0&&!"0".equals(refShift))
    			 {
    				 class_bean=(UnKqClassBean)this.class_hash.get(refShift);
					 if(class_bean!=null)
    				 {
						 Date RFDT=null;
						 Date RTDT=null;
    					 timeLen=0;
        				 String strFTime="";
        				 String strTTime="";
        				 Date FTime=null;
        				 Date TTime=null;
        				 String perOffTime_str="";   
        				 Date perOffTime=null;
        				 float time_f=0;
        				 for(int r=0;r<3;r++)
            			 {
        					 if(r==0)
        					 {
        						 strFTime=class_bean.getOnduty_1();
                				 strTTime=class_bean.getOffduty_1();
        					 }else if(r==1)
        					 {
        						 strFTime=class_bean.getOnduty_2();
                				 strTTime=class_bean.getOffduty_2();
        					 }else
        					 {
        						 strFTime=class_bean.getOnduty_3();
                				 strTTime=class_bean.getOffduty_3();
        					 }
        					 
            				 if((strFTime==null||strFTime.length()<=0)||(strTTime==null||strTTime.length()<=0)) {
                                 break;
                             }
            				 FTime=DateUtils.getDate(strFTime,"HH:mm");
            				 TTime=DateUtils.getDate(strTTime,"HH:mm");
            				 if(perOffTime_str!=null&&perOffTime_str.length()>0)
            				 {
            					 perOffTime=DateUtils.getDate(perOffTime_str,"HH:mm");     					
            					 time_f=getPartMinute(perOffTime,FTime);
            					 if(time_f<0)
            					 {
            						strDate=DateUtils.format(DateUtils.addDays(d_curDate,1),"yyyy.MM.dd");
            					 } 
            				 }
            				 perOffTime_str=strTTime;
            				 time_f=getPartMinute(FTime,TTime);
            				 if(time_f>0)
            				 {
            						 RFDT=DateUtils.getDate(strDate+" "+strFTime,"yyyy.MM.dd HH:mm");
            						 RTDT=DateUtils.getDate(strDate+" "+strTTime,"yyyy.MM.dd HH:mm");
            				 }else
            				 {
            						 RFDT=DateUtils.getDate(strDate+" "+strFTime,"yyyy.MM.dd HH:mm");
            						 strDate=DateUtils.format(DateUtils.addDays(d_curDate,1),"yyyy.MM.dd");
            						 RTDT=DateUtils.getDate(strDate+" "+strTTime,"yyyy.MM.dd HH:mm");
            				 }    	
            				 timeLen=calcTimSpan(RFDT,RTDT,s_date,e_date)+timeLen;
            			 }
        				 timeLen=timeLen-fLeaveTimeLen;
    				 }else
    				 {
    					 String o_time=class_bean.getOnduty();
    					 String f_time=class_bean.getOffduty();
    					 if(o_time==null||o_time.length()<=0||f_time==null||f_time.length()<=0)
    					 {
    						 timeLen=0;
    					 }else
    					 {
    						 Date f_DT=DateUtils.getDate(DateUtils.format(d_curDate, "yyyy.MM.dd")+" "+o_time+":00", "yyyy.MM.dd HH:mm:ss");
    						 Date t_DT=DateUtils.getDate(DateUtils.format(d_curDate, "yyyy.MM.dd")+" "+f_time+":00", "yyyy.MM.dd HH:mm:ss");
    						 timeLen=calcTimSpan(f_DT,t_DT,s_date,e_date);
    						 if(timeLen<0) {
                                 timeLen=0;
                             }
    					 }
    				 }
    			 }
    			 if(timeLen>0.01)
    			 {
    				 String dert_itemid=(String)hashM.get("dert_itemid");
    				 if(dert_itemid!=null&&dert_itemid.length()>0)
    				 {
    					 timeLen=timeLen-Integer.parseInt(dert_itemid);
    				 }
    			 }
    		 }
    		 //参考班次的结束
    		 if(timeLen>0.01)
    		 {
    			 item_Map=(HashMap)this.kqItem_hash.get(apptype);
    			 if(item_Map==null) {
                     continue;
                 }
    			 fielditemid=(String)item_Map.get("fielditemid");
    			 if(fielditemid==null||fielditemid.length()<=0) {
                     continue;
                 }
    			 updateDataToTmp(nbase,a0100,d_curDate,strDate,temp_table,fielditemid,item_Map,timeLen,true,class_bean); 
    			 if(checkAppType(kqItem_Leave,apptype)) {
                     updateExceptDetial(temp_table,nbase,a0100,strDate,"请假");
                 } else if(checkAppType(kqItem_Work,apptype)) {
                     updateExceptDetial(temp_table,nbase,a0100,strDate,"公出");
                 } else {
                     updateExceptDetial(temp_table,nbase,a0100,strDate,"加班");
                 }
    		 }
    		/* if(this.calcNight&&app_type.equals("q15"))
    		 {
    			 factNightLen =factNightLen+calcTimSpan(NFDT,NTDT,s_date,e_date);
    		 }
    		 if(this.haveOTime&&app_type.equals("q15"))
    		 {
    			 factTimeLen =factTimeLen+calcTimSpan(OFDT,OTDT,s_date,e_date);
    		 }*/
    	 }
    	 HashMap hash=new HashMap();
    	 hash.put("factNightLen",new Float(factNightLen));
    	 hash.put("factOTimeLen",new Float(factTimeLen));
    	 return hash;
     }  
     
     /**
      * 公休日有参考班次分析业务的
      * @param temp_table
      * @param app_list
      * @param nbase
      * @param a0100
      * @param strDate
      * @param FDT
      * @param TDT
      * @param kqItem_hash
      */
     private String calcRefShiftTimeLen(ArrayList app_list,Date FDT,Date TDT)throws GeneralException
     {
    	 HashMap hashM=null;
    	 String apptype="";
    	 Date s_date=null;
    	 Date e_date=null;
    	 HashMap item_Map=null;
    	 String fielditemid="";
    	 float timeLen=0;  
    	 String class_id="0";
    	 for(int i=0;i<app_list.size();i++)
    	 {
    		 hashM=(HashMap)app_list.get(i);
    		 apptype=(String)hashM.get("apptype");
    		 //System.out.print(apptype);
    		 if(apptype!=null&&apptype.indexOf(kqItem_OGeneral)==0) {
                 continue;//平时加班分出来算
             }
    		 if(!checkAppType(kqItem_Overtime,apptype)) {
                 continue;//不是加班分出来算
             }
    		 timeLen=0;
    		 s_date=(Date)hashM.get("s_date");
    		 e_date=(Date)hashM.get("e_date"); 
    		 String id=(String)hashM.get("id");    		 
    		 /*System.out.println(DateUtils.format(s_date,"yyyy.MM.dd HH:mm:ss")+"---"+DateUtils.format(d_curDate,"yyyy.MM.dd HH:mm:ss"));
    		 System.out.println(DateUtils.format(FDT,"yyyy.MM.dd HH:mm:ss")+"--"+DateUtils.format(TDT,"yyyy.MM.dd HH:mm:ss"));
    	     System.out.println(DateUtils.format(s_date,"yyyy.MM.dd HH:mm:ss")+"--"+DateUtils.format(e_date,"yyyy.MM.dd HH:mm:ss"));*/
    		    		 
    		 timeLen=calcTimSpan(FDT,TDT,s_date,e_date);
    		 UnKqClassBean class_bean=null;
    		 //这段就是对加班申请的参考班次的应用
    		 if(timeLen>0.01)
    		 {
    			 String refShift=(String)hashM.get("refShift");
    			 if(refShift!=null&&refShift.length()>0&&!"0".equals(refShift))
    			 {
    				 return refShift;
    			 }else
    			 {
    				 return "xx";
    			 }
    		 }
    		
    	 }
    	 return class_id;
     }  
     private float getTimeLen(Date NFDT,Date NTDT)
 	 {
 		float timeLen=0;
 		timeLen=getPartMinute(NFDT,NTDT);
 		return timeLen;
 	 }     
     /**
      * 将因请假，公出错过的刷卡点置为已刷
      * @param a0100
      * @param nbase
      * @param strDate
      * @param app_hash
      */
     private void checkOverCardPoint(String a0100,String nbase,String strDate,HashMap app_hash,KqEmpClassBean empClassBean)
     {
    	 
    	
    	 String on_strTime=empClassBean.getOnduty_1();
    	 Date on_Tiem=DateUtils.getDate(strDate+" "+on_strTime,"yyyy.MM.dd HH:mm");
    	 String cardStrTime="";
    	 Date cardTime=null;
    	 float itemLen=0;
    	 for(int i=0;i<3;i++)
    	 {
           //上班要求刷而未刷
    		 if(this.cardPoints[i][0]&&!this.cardPoints[i][1])
    		 {
    			 if(i==0)
    			 {
    				 cardTime=on_Tiem;
    			 }else
    			 {
    				 cardStrTime=empClassBean.getOnduty((i+1)+"");
    				 cardTime=DateUtils.getDate(strDate+" "+cardStrTime,"yyyy.MM.dd HH:mm");
    				 itemLen=getPartMinute(on_Tiem,cardTime);
    				 if(itemLen<=0) {
                         cardTime=DateUtils.addDays(cardTime,1);
                     }
    			 }
    			 if(inCludeByBusiTimes(a0100,nbase,app_hash,cardTime))
    			 {
    				 this.cardPoints[i][1]=true;
    			 }
    		 }
    		 if(this.cardPoints[i][2]&&!this.cardPoints[i][3])
    		 {
    			 cardStrTime=empClassBean.getOffduty((i+1)+"");
				 cardTime=DateUtils.getDate(strDate+" "+cardStrTime,"yyyy.MM.dd HH:mm");
				 itemLen=getPartMinute(on_Tiem,cardTime);
				 if(itemLen<=0) {
                     cardTime=DateUtils.addDays(cardTime,1);
                 }
				 if(inCludeByBusiTimes(a0100,nbase,app_hash,cardTime))
    			 {
    				 this.cardPoints[i][3]=true;
    			 }
    		 }
    	 }
     }
     private boolean inCludeByBusiTimes(String a0100,String nbase,HashMap app_hash,Date cardTime)
     {
    	 boolean isCorrect =false;
    	 Date fTime=null;
    	 Date tTime=null;
    	 String app_key=nbase+a0100+"q13";//公出申请
    	 ArrayList app_list=(ArrayList)app_hash.get(app_key.toUpperCase()); 
    	 if(app_list==null||app_list.size()<=0) {
             return false;
         }
    	 HashMap hashM=new HashMap();
    	 for(int i=0;i<app_list.size();i++)
    	 {
    		 hashM=(HashMap)app_list.get(i);
    		 fTime=(Date)hashM.get("s_date");
    		 tTime=(Date)hashM.get("e_date");
    		 float itemLen_1=getPartMinute(fTime,cardTime);
    		 float itemLen_2=getPartMinute(cardTime,tTime);
    		 if(itemLen_1>=0&&itemLen_2>=0)
    		 {
    			 return true;
    		 }
    	 }
    	 app_key=nbase+a0100+"q15";//请假申请
    	 app_list=(ArrayList)app_hash.get(app_key.toUpperCase());  
    	 if(app_list!=null&&app_list.size()>0) {
             return false;
         }
    	 for(int i=0;i<app_list.size();i++)
    	 {
    		 hashM=(HashMap)app_list.get(i);
    		 fTime=(Date)hashM.get("s_date");
    		 tTime=(Date)hashM.get("e_date");
    		 String id=(String)hashM.get("q1501");
    		 //如果有销假，业务结束时间取销假时间
    		 if(this.fLeaveBackDataH!=null)
    		 {
    			String leaveBackkey=(String)hashM.get("nbase")+(String)hashM.get("a0100")+"q15";
    			ArrayList oneLeaveBack=(ArrayList)this.fLeaveBackDataH.get(leaveBackkey);
    			
    			if(oneLeaveBack!=null&&oneLeaveBack.size()>0)
    			{
    				HashMap backHash=null;
    				for(int r=0;r<oneLeaveBack.size();r++)
    				{
    					backHash=(HashMap)oneLeaveBack.get(r);
    					String q1519=(String)backHash.get("q1519");
    					if(q1519==null||q1519.length()<=0) {
                            continue;
                        }
    					if(id.equalsIgnoreCase(q1519))
    					{
    						tTime=(Date)backHash.get("e_date");
    					}
    				}
    			}
    				
    		 }
    		 float itemLen_1=getPartMinute(fTime,cardTime);
    		 float itemLen_2=getPartMinute(cardTime,tTime);
    		 if(itemLen_1>=0&&itemLen_2>=0)
    		 {
    			 return true;
    		 }
    	 }
    	 return isCorrect;
     }
     /**
      * 
      * @param temp_table
      * @param app_list
      * @param nbase
      * @param a0100
      * @param d_curDate
      * @param strDate
      * @param OFDT
      * @param OTDT
      * @param factOTimeLen////实际上加班段时长
      * @return
      */
     private void calcGernalOverTime(String temp_table,ArrayList app_list,String nbase,String a0100,Date d_curDate,String strDate,Date OFDT,Date OTDT,float factOtimeLen)throws GeneralException
     {
    	 HashMap hashM=null;
    	 String apptype="";
    	 Date s_date=null;
    	 Date e_date=null;
    	 HashMap item_Map=null;
    	 String fielditemid="";
    	 float timeLen=0;
    	 Date on_Time=null;
    	 Date off_Time=null;
    	 strDate=DateUtils.format(d_curDate,"yyyy.MM.dd");
    	 Date b_Date=DateUtils.getDate(strDate+" "+"00:00:00","yyyy.MM.dd HH:mm:ss");
    	 Date f_Date=DateUtils.getDate(strDate+" "+"23:59:59","yyyy.MM.dd HH:mm:ss");  
    	 UnKqClassBean class_bean=null;
    	 try
    	 {
    		 for(int i=0;i<app_list.size();i++)
        	 {
        		 hashM=(HashMap)app_list.get(i);
        		 apptype=(String)hashM.get("apptype");
        		 if(apptype==null||apptype.indexOf(kqItem_OGeneral)!=0) {
                     continue;
                 }
    			 String refShift=(String)hashM.get("refShift");
    			 if(refShift!=null&&refShift.length()>0&&!"0".equals(refShift))
    			 {
    				 class_bean=(UnKqClassBean)this.class_hash.get(refShift);
    				 if(class_bean.getOnduty()!=null&&class_bean.getOnduty().length()>0&&class_bean.getOffduty()!=null&&class_bean.getOffduty().length()>0)
    				 {
    					 on_Time=DateUtils.getDate(class_bean.getOnduty(),"HH:mm");
        				 off_Time=DateUtils.getDate(class_bean.getOffduty(),"HH:mm");
    				 }else
    				 {
    					 on_Time=null;
    					 off_Time=null;
    				 }
    				 
    			 }else
    			 {
    				 on_Time=getOndutyTime();
            		 off_Time=getOffdutyTime(3);
    			 }
        		 timeLen=0;
        		 s_date=(Date)hashM.get("s_date");
        		 e_date=(Date)hashM.get("e_date");  
        		 
        		 if(on_Time==null||off_Time==null)
        		 {
        			 timeLen=calcTimSpan(b_Date,f_Date,s_date,e_date);
        			 //参考班次
        			 //String refShift=(String)hashM.get("refShift");
        			 if(refShift!=null&&refShift.length()>0&&!"0".equals(refShift))
        			 {
        				 int diff=DateUtils.dayDiff(s_date,d_curDate);
        				 if(diff!=0)
        				 {
        					 timeLen=0;
        				 }
        			 }
        			 item_Map=(HashMap)this.kqItem_hash.get(apptype);
        			 fielditemid=(String)item_Map.get("fielditemid");
        			 if(fielditemid==null||fielditemid.length()<=0) {
                         continue;
                     }
        			 updateDataToTmp(nbase,a0100,d_curDate,strDate,temp_table,fielditemid,item_Map,timeLen,true,class_bean);
        			 updateExceptDetial(temp_table,nbase,a0100,strDate,"加班");
        		 }else
        		 {
        			 float time_1=getPartMinute(on_Time,off_Time);
                	 if(time_1>0)//没有跨零点    		
                	 {
                			 on_Time=DateUtils.getDate(strDate+" "+DateUtils.format(on_Time,"HH:mm")+":00","yyyy.MM.dd HH:mm:ss");
                			 timeLen=calcTimSpan(b_Date,on_Time,s_date,e_date);
                			 off_Time=DateUtils.getDate(strDate+" "+DateUtils.format(off_Time,"HH:mm")+":00","yyyy.MM.dd HH:mm:ss");
                			 timeLen=timeLen+calcTimSpan(off_Time,f_Date,s_date,e_date);
                	  }else
                	  {
                			 on_Time=DateUtils.getDate(strDate+" "+DateUtils.format(on_Time,"HH:mm")+":00","yyyy.MM.dd HH:mm:ss");
                			 timeLen=calcTimSpan(b_Date,on_Time,s_date,e_date);
                	  }
                	  if(timeLen>0.01)
                	  {
                			 item_Map=(HashMap)this.kqItem_hash.get(apptype);
                			 if(item_Map==null) {
                                 continue;
                             }
                			 fielditemid=(String)item_Map.get("fielditemid");
                			 if(fielditemid==null||fielditemid.length()<=0) {
                                 continue;
                             }
                			 item_Map=(HashMap)this.kqItem_hash.get(apptype);
                			 updateDataToTmp(nbase,a0100,d_curDate,strDate,temp_table,fielditemid,item_Map,timeLen,true,class_bean);
                			 updateExceptDetial(temp_table,nbase,a0100,strDate,"加班");
                	  }
                	  /**********中间时间**********/
                	  String strTTime1="";
                	  String strFTime2="";
                	  Date f_time=null;
                	  Date o_time=null;
                	  for(int r=0;r<2;r++)
         			 {
                		  strTTime1=this.resultSet.getString("offduty_"+(r+1));
                		  strFTime2=this.resultSet.getString("onduty_"+(r+2));
         				  if((strFTime2==null||strFTime2.length()<=0)||(strTTime1==null||strTTime1.length()<=0)) {
                              continue;
                          }
         				  f_time=DateUtils.getDate(strTTime1,"HH:mm");
         				  o_time=DateUtils.getDate(strFTime2,"HH:mm");
         				  time_1=getPartMinute(f_time,o_time);
         				  if(time_1>0)//没有跨零点    		
                    	  {
         					 f_time=DateUtils.getDate(strDate+" "+DateUtils.format(f_time,"HH:mm")+":00","yyyy.MM.dd HH:mm:ss");
         					 o_time=DateUtils.getDate(strDate+" "+DateUtils.format(o_time,"HH:mm")+":00","yyyy.MM.dd HH:mm:ss");
         					 timeLen=calcTimSpan(f_time,o_time,s_date,e_date);
                    	  }else
                    	  {
                    		  f_time=DateUtils.getDate(strDate+" "+DateUtils.format(f_time,"HH:mm")+":00","yyyy.MM.dd HH:mm:ss");
                 			  timeLen=calcTimSpan(f_time,f_Date,s_date,e_date);                		  
                    	  }
         				 if(timeLen>0.01)
                   	     {
                   			 item_Map=(HashMap)this.kqItem_hash.get(apptype);
                   			 if(item_Map==null) {
                                 continue;
                             }
                   			 fielditemid=(String)item_Map.get("fielditemid");
                   			 if(fielditemid==null||fielditemid.length()<=0) {
                                 continue;
                             }
                   			 item_Map=(HashMap)this.kqItem_hash.get(apptype);
                   			 updateDataToTmp(nbase,a0100,d_curDate,strDate,temp_table,fielditemid,item_Map,timeLen,true,class_bean);
                   			 updateExceptDetial(temp_table,nbase,a0100,strDate,"加班");
                   	    }
         			}
        		 }
        		 
    		}
        	
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	
    		 
    		 /*if(this.haveOTime)//班次第四个时间段
    		 {
    			 item_Map=(HashMap)this.kqItem_hash.get(apptype);
    			 if(item_Map==null)
    				 continue;
    			 fielditemid=(String)item_Map.get("fielditemid");
    			 if(fielditemid==null||fielditemid.length()<=0)
    				 continue;    			 
    			 float oTimeOverLen=calcTimSpan(OFDT,OTDT,s_date,e_date);
    			 float overTimeLen=getTimeLen(OFDT,OTDT)-factOtimeLen-oTimeOverLen;
    			 if(overTimeLen<0)
    			 {
    				 overTimeLen=0;
    			 }
    			 updateDataToTmp(nbase,a0100,d_curDate,strDate,temp_table,fielditemid,item_Map,overTimeLen,true);
    		 }  */           
    	  	 
     }
     /**
      * 班次其他考勤规则
      * @param nbase
      * @param a0100
      * @param d_curDate
      * @param strDate
      * @param temp_table
      * @param kqItem_hash
      * @param empClassBean
      */
     private void calcOthenLen(String nbase,String a0100,Date d_curDate,String strDate,String temp_table,HashMap item_Hash,float timeLen,boolean addUP)throws GeneralException
     {
    	if(item_Hash==null) {
            return;
        }
  		String fielditemid=(String)item_Hash.get("fielditemid");
  		if(fielditemid==null||fielditemid.length()<=0) {
            return;
        }
  		if(timeLen>0)
  		{
  			updateDataToTmp(nbase,a0100,d_curDate,strDate,temp_table,fielditemid,item_Hash,timeLen,true,null);
  		}
     }     
     /**
      * 修改临时表数据
      * @param nbase
      * @param a0100
      * @param strDate
      * @param temp_table
      * @param fielditemid
      * @param item_hash
      * @param timeLen
      * @param addUP
      */
     private void updateDataToTmp(String nbase,String a0100,Date d_curDate,String strDate,String temp_table,String fielditemid,HashMap item_hash,float timeLen,boolean addUP,UnKqClassBean class_bean)throws GeneralException
     {
    	 String itemUnit=(String)item_hash.get("item_unit");
    	 float unit=0;
    	 String chgItemValue="";
    	 if(itemUnit==null||itemUnit.length()<=0)
    	 {
    		 itemUnit=unit_MINUTE;
    	 }
    	 if(itemUnit.equals(unit_HOUR))
    	 {
    		 unit=timeLen/60;
    		 unit=round(unit+"",1);
    	 }else if(itemUnit.equals(unit_MINUTE))
    	 {
    		 unit=round(timeLen+"",1);
    		 
    	 }else if(itemUnit.equals(unit_DAY))
    	 {
    		 if(class_bean!=null)
    		 {
    			String work_hours=class_bean.getWork_hours();
    			if(work_hours!=null&&Float.parseFloat(work_hours)>0.01) {
                    chgItemValue="("+timeLen+"/"+Sql_switcher.isnull("work_hours",work_hours)+")";
                } else {
                    chgItemValue="("+timeLen+"/"+Sql_switcher.isnull("work_hours","480")+")";
                }
    		 }else {
                 chgItemValue="("+timeLen+"/"+Sql_switcher.isnull("work_hours","480")+")";
             }
    		 //unit=1;
    	 }else if(itemUnit.equals(unit_ONCE))
    	 {
    		 unit=1;
    	 }
    	 /*int wid=getLXtype(fielditemid);
    	 unit=KQRestOper.round(unit+"",wid);*/
    	 if(chgItemValue==null||chgItemValue.length()<=0)
    	 {
    		 float floatItemVal=unit;
    		 if(KQRestOper.round(unit+"",2)<0.01)
    		 {
    			 floatItemVal=0;
    		 }
    		 if(floatItemVal==0)
    		 {
    			 chgItemValue="0";
    		 }else
    		 {
    			 chgItemValue=unit+"";
    		 }
    	 }    	 	 
    	 StringBuffer strWhe=new StringBuffer();
    	 strWhe.append(" where nbase='"+nbase+"'");
    	 strWhe.append(" and a0100='"+a0100+"'");
    	 strWhe.append(" and q03z0='"+DateUtils.format(d_curDate,"yyyy.MM.dd")+"'");
    	 StringBuffer strSQL=new StringBuffer();
    	 strSQL.append("UPDATE "+temp_table+" set");
    	 try
    	 {
    		 if(addUP)
        	 {
        		 strSQL.append(" "+fielditemid+"="+Sql_switcher.isnull(fielditemid,"0")+"+");//累计
        	 }else
        	 {
        		 strSQL.append(" "+fielditemid+"=");//不累计
        	 }
        	 if(itemUnit.equals(unit_DAY))
        	 {
        		 String sql=strSQL.toString()+" "+chgItemValue+strWhe.toString();
        		 sql=sql+" and "+Sql_switcher.isnull("work_hours","0")+">=0.01"; 
        		 this.dao.update(sql);        		 
        		 //以天计,但没有可对照的应出勤        		 
        		 /*sql=strSQL.toString()+"0"+strWhe.toString();
        		 sql=sql+" and "+Sql_switcher.isnull("work_hours","0")+"<0.01";
        		 dao.update(sql);
        		 sql="update "+temp_table+" set "+fielditemid+"=1 "+strWhe.toString()+" and "+fielditemid+">1";*/
        		 sql=strSQL + "1" + strWhe.toString() + " AND " + Sql_switcher.isnull("work_hours","0") + "<0.01";
        		 this.dao.update(sql);
        		 this.dao.update("UPDATE "+temp_table+" set "+fielditemid+"=1 where "+fielditemid+">1");
        		 /*if(unit>0)
        		 {
        			 dao.update(sql);
        		 }*/
        		 
        	 }else
        	 {
        		 strSQL.append(" "+chgItemValue+strWhe.toString());
        		 this.dao.update(strSQL.toString());        		
        	 }
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    		 throw GeneralExceptionHandler.Handle(new GeneralException("",fielditemid+"统计错误，请查看考勤项目中，统计指标是否对应！","",""));
    	 }
    	 
     }
     /**
      * 得到精度
      * @param fielditemid
      * @return
      */
     private  int getLXtype(String fielditemid)
 	 {
 		int wid=0;
 		for(int r=0;r<this.columnlist.size();r++)
  		{
  	   	   FieldItem fielditem=(FieldItem)this.columnlist.get(r); 	   	   
  	   	   if(fielditemid.equalsIgnoreCase(fielditem.getItemid()))
  	   	   {  
  	   		 wid=fielditem.getDecimalwidth();
  	   		 break;
  	   	   }
  		}
 		return wid;
 	 }
     /**
      * 计算时间长度
      * @param FDT班次开始时间
      * @param TDT班次结束时间
      * @param s_app_date申请开始时间
      * @param e_app_date申请结束时间
      * @return
      */
     public float calcTimSpan(Date FDT,Date TDT,Date s_app_date,Date e_app_date)
     {
    	 float timeLen=0;    
    	 float time_1=getPartMinute(FDT,s_app_date);
    	 float time_2=getPartMinute(TDT,e_app_date);
    	 
    	 if(time_1<=0&&time_2>=0)//完全包含在申请时间内
    	 {
    		 timeLen=getPartMinute(FDT,TDT);
    		 return timeLen;
    	 }else if(time_1>=0&&time_2<=0) //申请时间完全包含在工作时段内
    	 {
    		 timeLen=getPartMinute(s_app_date,e_app_date);
    		 return timeLen;
    	 }else 
    	 {
    		 float time_3=getPartMinute(FDT,e_app_date);
    		 if(time_1<=0&&time_3>0) //只包含前一部分
        	 {
        		 timeLen=getPartMinute(FDT,e_app_date);
        		 return timeLen;
        	 }else
        	 {
        		 float time_4=getPartMinute(TDT,s_app_date);//只包含后一部分
        		 if(time_4<0&&time_2>=0)
        		 {
        			 timeLen=getPartMinute(s_app_date,TDT);
        			 return timeLen;
        		 }
        	 }
    	 }
    	 return timeLen;
     } 
     
     /**
      * 根据组织机构查看是否有合适的不定排班的班次，有就把该班次建立进去
      * @param rs
      * @param strDate
      * @param d_curDate
      * @return
      */
     private boolean checkUnsteadyShiftClass(String table_Temp,ResultSet rs,String nbase,String a0100,String strDate,Date d_curDate)
     {
    	 boolean isCorrect =false;
    	 try
    	 {
    		 String class_id="";
    		 String org_id=rs.getString("e01a1");
    		 class_id=searchUnsteadyShiftClass(org_id,a0100,nbase,strDate,d_curDate);
    		 if(class_id!=null&&class_id.length()>0)
    		 {
    			 tranEmpClassToMasterTmep(table_Temp,class_id,nbase,a0100,strDate);
    			 isCorrect=true;
    		 }
    		 org_id=rs.getString("e0122");
    		 class_id=searchUnsteadyShiftClass(org_id,a0100,nbase,strDate,d_curDate);
    		 if(class_id!=null&&class_id.length()>0)
    		 {
    			 tranEmpClassToMasterTmep(table_Temp,class_id,nbase,a0100,strDate);
    			 isCorrect= true;
    		 }
    		 org_id=rs.getString("b0110");
    		 class_id=searchUnsteadyShiftClass(org_id,a0100,nbase,strDate,d_curDate);
    		 if(class_id!=null&&class_id.length()>0)
    		 {
    			 tranEmpClassToMasterTmep(table_Temp,class_id,nbase,a0100,strDate);
    			 isCorrect= true;
    		 }
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 
    	 return isCorrect;
     }
     /**
      * 对于没有排班的员工察看他的不定班次情况
      * @param org_id
      * @param a0100
      * @param nbase
      * @param strDate
      * @param d_curDate
      */
     private String searchUnsteadyShiftClass(String org_id,String a0100,String nbase,String strDate,Date d_curDate)
     {
    	 StringBuffer sql=new StringBuffer();
    	 sql.append("select a.org_dept_id,b.* FROM kq_org_dept_able_shift a LEFT JOIN kq_class b");
    	 sql.append(" ON a.class_id=b.class_id");
    	 sql.append(" WHERE org_dept_id='"+org_id+"'");  
         sql.append(" ORDER BY onduty_start_1");
         String class_id="";
         ContentDAO dao=new ContentDAO (this.conn);
         RowSet rowset=null;
         try
         {
        	 rowset=dao.search(sql.toString());        	
        	 Date on_Time=null;
        	 Date off_Time=null;
        	 UnKqClassBean classBean=null;
        	 ArrayList orig_list=new ArrayList();
        	 LinkedList linklist=new LinkedList();
        	 while(rowset.next())
        	 {
        		 on_Time=getUnFromTime(strDate,rowset);
        		 off_Time=getUnToDate(strDate,rowset,on_Time);
        		 if(on_Time==null||off_Time==null) {
                     continue;
                 }
        		 classBean=new UnKqClassBean(rowset);
        		 orig_list=getOrifDatas(nbase,a0100,on_Time,off_Time);
        		 String[] array=getUNEmpDatasByClass(classBean,orig_list,strDate);
        		 linklist.add(array);        		 
        	 }
        	 class_id=analyUnShif(linklist);
         }catch(Exception e)
         {
        	 e.printStackTrace();
         }finally
 		{
 			if(rowset!=null) {
                try {
                    rowset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
 		}
         return class_id;
     }
     /**
      * 对不定班次取得刷卡起始时间
      * @param a0100
      * @param nbase
      * @param strDate
      * @param rowset
      */
    private Date getUnFromTime(String strDate,RowSet rowset)
    {
    	if(rowset==null) {
            return null;
        }
    	Date sDate=null;
    	 //取得刷卡起始时间
    	try
    	{
    		String onduty_card_1=rowset.getString("onduty_card_1");
    		String on_Time=rowset.getString("onduty_1");
    		String str_Time="";
    		if("1".equals(onduty_card_1))
    		{
    			str_Time=strDate+""+rowset.getString("onduty_start_1");
    			sDate=DateUtils.getDate(str_Time,"yyyy.MM.dd HH:mm");
    			Date d_Time=DateUtils.getDate(rowset.getString("onduty_start_1"),"HH:mm");
        		Date on_d=DateUtils.getDate(on_Time,"HH:mm");
        		float time_f=getPartMinute(d_Time,on_d);
        		if(time_f<0)
        		{
        			sDate=DateUtils.addDays(sDate,-1);     			
        		}
    		}else
    		{
    			str_Time=strDate+""+on_Time;
    			sDate=DateUtils.getDate(str_Time,"yyyy.MM.dd HH:mm");
    		}
    		
    	}catch(Exception e)
    	{
    	  e.printStackTrace();	
    	}  
    	return sDate;
    }

    /**
     * 对不定班次取得刷卡结束时间
     * @param strDate
     * @param rowset
     * @param on_Time
     * @return
     */
    private Date getUnToDate(String strDate,RowSet rowset,Date on_Time)
    {
    	if(rowset==null) {
            return null;
        }
    	Date tDate=null;
    	String offduty_end="";
    	String str_Time="";
    	Date off_time=null;
    	Date offduty_t=null;
    	String offduty="";
    	try
    	{
    		for(int i=4;i>=1;i++)
    		{
    			offduty_end=rowset.getString("offduty_end_"+i);
    			offduty=rowset.getString("offduty_"+i);
    			if(offduty_end!=null&&offduty_end.length()>0)
    			{
    				off_time=DateUtils.getDate(offduty_end,"HH:mm");
    				str_Time=strDate+""+offduty_end;
					tDate=DateUtils.getDate(str_Time,"yyyy.MM.dd HH:mm");
    				float fend_diff=getPartMinute(on_Time,off_time);
    				if(fend_diff<=0)
    				{
    					tDate=DateUtils.addDays(tDate,1);   
    					break;
    				}    				
    				if(offduty!=null&&offduty.length()<=0)
    				{
    					offduty_t=DateUtils.getDate(offduty,"HH:mm");
    					fend_diff=getPartMinute(offduty_t,off_time);
    					tDate=DateUtils.getDate(strDate+" "+offduty,"yyyy.MM.dd HH:mm");
    					if(fend_diff<=0)
    					{
    						tDate=DateUtils.addDays(tDate,1);   
        					break;
    					}
    				}
    				break;
    			}else if(offduty!=null&&offduty.length()>0)
    			{
    				offduty_t=DateUtils.getDate(offduty,"HH:mm");
    				tDate=DateUtils.getDate(strDate+" "+offduty,"yyyy.MM.dd HH:mm");
    				float fend_diff=getPartMinute(on_Time,offduty_t);
    				if(fend_diff<=0) {
                        tDate=DateUtils.addDays(tDate,1);
                    }
    				break;    				
    			}
    		}
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return tDate;
    }
    /**
     * 取得上班时间
     * @param a0100
     * @param nbase
     * @param strDate
     * @param rowset
     */
   private Date getOndutyTime()throws GeneralException
   {
   	if(this.resultSet==null) {
        return null;
    }
   	Date sDate=null;   	
   	try
   	{
   		String on_Time=this.resultSet.getString("onduty_1");
        if(on_Time==null||on_Time.length()<=0) {
            return null;
        }
   		sDate=DateUtils.getDate(on_Time,"HH:mm");
   	}catch(Exception e)
   	{
   	  e.printStackTrace();	
   	 throw GeneralExceptionHandler.Handle(new GeneralException("","班次1上班时间定义异常！无法分析","",""));
   	}  
   	return sDate;
   }

   /**
    * 取得下班时间
    * @param strDate
    * @param rowset
    * @param on_Time
    * @return
    */
   private Date getOffdutyTime(int locat)
   {
   	  if(this.resultSet==null) {
          return null;
      }
   	  Date tDate=null;   	
   	  String offduty="";
   	  try
   	  {
   		for(int i=locat;i>=1;i--)
   		{
   			offduty=this.resultSet.getString("offduty_"+i);
   			if(offduty!=null&&offduty.length()>0)
   			{
   				tDate=DateUtils.getDate(offduty,"HH:mm");
   					break;
   			}  
   		}   		
   	 }catch(Exception e)
   	 {
   		e.printStackTrace();
   	 }
   	 return tDate;
   }
    /**
     * 得到期间内的刷卡数据
     * @param nbase
     * @param a0100
     * @param on_Time
     * @param off_Time
     * @return
     */
    private ArrayList getOrifDatas(String nbase,String a0100,Date on_Time,Date off_Time)
    {
    	StringBuffer sql=new StringBuffer();
    	sql.append("SELECT DISTINCT work_date,work_time FROM kq_originality_data");
        sql.append("WHERE UPPER(nbase)='"+nbase.toUpperCase()+"'");
        sql.append(" and a0100='"+a0100+"'");
        sql.append(" and work_date"+Sql_switcher.concat()+"work_time>='"+DateUtils.format(on_Time,"yyyy.MM.ddHH:mm")+"'");
        sql.append(" and work_date"+Sql_switcher.concat()+"work_time<='"+DateUtils.format(off_Time,"yyyy.MM.ddHH:mm")+"'");
        sql.append(" ORDER BY work_date, work_time");       
        ArrayList list=new ArrayList();
        RowSet rowset=null;
        try
        {        	
        	rowset=this.dao.search(sql.toString());
        	while(rowset.next())
        	{
        		list.add(rowset.getString("work_date")+" "+rowset.getString("work_time"));
        	}
        }catch(Exception e)
        {
        	e.printStackTrace();
        }finally
		{
			if(rowset!=null) {
                try {
                    rowset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
        return list;
    }    
    /**
     * 对于不定排班的一个班次的多次刷卡生成一个分析数组
     * @param classBean
     * @param list
     * @param strDate
     * @return  array[0]班次id array[1]需要刷卡 array[2]已经刷卡 array[3]没有刷卡
     */
    private String[] getUNEmpDatasByClass(UnKqClassBean classBean,ArrayList list,String strDate)
    {
    	if(list==null||list.size()<=0) {
            return null;
        }
    	String on_s="";	
    	String off_s="";
    	String on_card="";
    	String off_card="";
    	Date on_Time=null;
        Date off_Time=null;  
        Date on_Date=null;
        Date off_Date=null;
        Date cur_Date=null;
        int need_card=0;
        int al_card=0;
        int not_card=0;
    	float diff;
    	for(int i=1;i<=4;i++)
    	{
    		on_card=classBean.getOnduty_card(i+"");
    		off_card=classBean.getOffduty_card(i+"");
    		if(on_card!=null&& "1".equals(on_card))
    		{
    			need_card++;
    			on_s=classBean.getOnduty_start(i+"");
    			off_s=classBean.getOnduty_end(i+"");
    			on_Time=DateUtils.getDate(on_s,"HH:mm");
    			off_Time=DateUtils.getDate(off_s,"HH:mm");
    			diff=getPartMinute(on_Time,off_Time);
    			on_Date=DateUtils.getDate(strDate+" "+on_s,"yyyy.MM.dd HH:mm");
    			off_Date=DateUtils.getDate(strDate+" "+off_s,"yyyy.MM.dd HH:mm");
				if(diff<=0) {
                    off_Date=DateUtils.addDays(off_Date,1);
                }
				for(int r=0;r<list.size();r++)
				{
					cur_Date=DateUtils.getDate(list.get(r).toString(),"yyyy.MM.dd HH:mm");
					if(opinionDateInclude(on_Date,off_Date,cur_Date))
	    			{
						al_card++;
						break;
	    			}
				}
				not_card++;
    		} 
    		if(off_card!=null&& "1".equals(off_card))
    		{
    			need_card++;
    			on_s=classBean.getOffduty_start(i+"");
    			off_s=classBean.getOffduty_end(i+"");
    			on_Time=DateUtils.getDate(on_s,"HH:mm");
    			off_Time=DateUtils.getDate(off_s,"HH:mm");
    			diff=getPartMinute(on_Time,off_Time);
    			on_Date=DateUtils.getDate(strDate+" "+on_s,"yyyy.MM.dd HH:mm");
    			off_Date=DateUtils.getDate(strDate+" "+off_s,"yyyy.MM.dd HH:mm");
				if(diff<=0) {
                    off_Date=DateUtils.addDays(off_Date,1);
                }
				for(int r=0;r<list.size();r++)
				{
					cur_Date=DateUtils.getDate(list.get(r).toString(),"yyyy.MM.dd HH:mm");
					if(opinionDateInclude(on_Date,off_Date,cur_Date))
	    			{
						al_card++;
						break;
	    			}
				}
				not_card++;
    		}
    	}   
    	String[] array=new String[4];
    	array[0]=classBean.getClass_id();//班次编号
    	array[1]=need_card+"";
    	array[2]=al_card+"";
    	array[3]=not_card+"";
    	return array;
    }
    /**
     * 判断是否开始结束时间包含当前时间
     * @param f_Date
     * @param t_Date
     * @param c_Date
     * @return
     */
    private boolean opinionDateInclude(Date f_Date,Date t_Date,Date c_Date)
    {
    	if(f_Date==null||t_Date==null||c_Date==null)
    	{
    		return false;
    	}
    	float diff_1=getPartMinute(f_Date,c_Date);
    	float diff_2=getPartMinute(t_Date,c_Date);
    	if(diff_1>=0&&diff_2<=0)
    	{
    		return true;
    	}else
    	{
    		return false;
    	}
    }
    /**
     * 通过刷卡纪录的得到的多个刷卡信息，分析最符合的对应班次
     * @param list
     * @return
     */
    private String analyUnShif(LinkedList list)
    {
    	list=unClassesArray(list);
    	String class_id=getUnShiftClassID(list);
    	return class_id;
    }
    /**
     * 对不定排班的多个班次的对应的刷卡纪录分析数组，进行没有刷卡次数正序排序
     * @param list
     */
    private LinkedList unClassesArray(LinkedList list)
    {
    	String[] array_1=null;
    	String[] array_2=null;    	
    	for(int i=1;i<list.size();i++)
    	{
    		int j=i;
    		array_1=(String[])list.get(j-1);
    		array_2=(String[])list.get(j);
    		while(Integer.parseInt(array_1[3])>Integer.parseInt(array_2[3]))
    		{
    			list.set(j-1,array_2);
    			list.set(j,array_1);
    			j--;
    			if(j<=0) {
                    break;
                }
    			array_1=(String[])list.get(j-1);
        		array_2=(String[])list.get(j);    			
    		}     		
    	}
    	return list;
    }
    /**
     * 通过分析针对刷卡纪录，对分析没有刷卡次数多的一个班次的结果中提取班次id
     * @param list
     * @return
     */
    private String getUnShiftClassID(LinkedList list)
    {
    	String class_id="";
    	if(list==null||list.size()<=0) {
            return "";
        }
    	if(list.size()>2)
    	{
    		String[] array_1=(String[])list.get(0);
        	String[] array_2=(String[])list.get(1);
        	if(Integer.parseInt(array_1[1])>=Integer.parseInt(array_2[1]))
        	{
        		class_id=array_1[0];
        	}else
        	{
        		class_id=array_2[0];
        	}
    	}else
    	{
    		String[] array_1=(String[])list.get(0);
    		class_id=array_1[0];
    	}
    	return class_id;
    }
    /**
     * 通过班次id修改临时表的班次基本信息
     * @param temp_table
     * @param class_id
     * @param nbase
     * @param a0100
     * @param str_Date
     * @throws GeneralException
     */
    private void tranEmpClassToMasterTmep(String temp_table,String class_id,String nbase,String a0100,String str_Date)throws GeneralException
    {
    	 
    	 ContentDAO dao = new ContentDAO(this.conn);
    	 RowSet rs=null;
    	 try {	
			/*StringBuffer update_1=new StringBuffer();
			update_1.append("update "+temp_table+" set class_id='"+class_id+"'");
			update_1.append(" where a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+str_Date+"'");
			dao.update(update_1.toString());
			dao.update(update);*/
			 StringBuffer columns=new StringBuffer();
			 columns.append("class_id,onduty_card_1,offduty_card_1,onduty_card_2,offduty_card_2,");	
			 columns.append("onduty_card_3,offduty_card_3,onduty_card_4,offduty_card_4,"); 
			 columns.append("onduty_start_1,onduty_1,onduty_flextime_1,be_late_for_1,absent_work_1,onduty_end_1,");
			 columns.append("rest_start_1,rest_end_1,offduty_start_1,leave_early_absent_1,leave_early_1,");
			 columns.append("offduty_1,offduty_flextime_1,offduty_end_1,"); 
			  //2
			 columns.append("onduty_start_2,onduty_2,onduty_flextime_2,be_late_for_2,absent_work_2,onduty_end_2,");
			 columns.append("rest_start_2,rest_end_2,offduty_start_2,leave_early_absent_2,leave_early_2,");
			 columns.append("offduty_2,offduty_flextime_2,offduty_end_2,");
			  //3
			 columns.append("onduty_start_3,onduty_3,onduty_flextime_3,be_late_for_3,absent_work_3,onduty_end_3,");
			 columns.append("rest_start_3,rest_end_3,offduty_start_3,leave_early_absent_3,leave_early_3,");
			 columns.append("offduty_3,offduty_flextime_3,offduty_end_3,");
			  //4
			 columns.append("onduty_start_4,onduty_4,onduty_flextime_4,be_late_for_4,absent_work_4,onduty_end_4,");
			 columns.append("rest_start_4,rest_end_4,offduty_start_4,leave_early_absent_4,leave_early_4,");
			 columns.append("offduty_4,offduty_flextime_4,offduty_end_4,");
			  //other
			 columns.append("night_shift_start,night_shift_end,zeroflag,domain_count,work_hours,zero_absent,one_absent");
			String sql="select "+columns.toString()+" from kq_class where class_id='"+class_id+"'";
			
			rs=dao.search(sql);			
			if(rs.next())
			{
				 this.resultSet.updateString("class_id",rs.getString("class_id"));
				 
				 this.resultSet.updateString("onduty_card_1",rs.getString("onduty_card_1"));
				 this.resultSet.updateString("offduty_card_1",rs.getString("offduty_card_1"));
				 this.resultSet.updateString("onduty_start_1",rs.getString("onduty_start_1"));
				 this.resultSet.updateString("onduty_1",rs.getString("onduty_1"));
				 this.resultSet.updateString("onduty_flextime_1",rs.getString("onduty_flextime_1"));
				 this.resultSet.updateString("be_late_for_1",rs.getString("be_late_for_1"));
				 this.resultSet.updateString("absent_work_1",rs.getString("absent_work_1"));
				 this.resultSet.updateString("onduty_end_1",rs.getString("onduty_end_1"));		 
				 this.resultSet.updateString("offduty_start_1",rs.getString("offduty_start_1"));
				 this.resultSet.updateString("leave_early_absent_1",rs.getString("leave_early_absent_1"));
				 this.resultSet.updateString("leave_early_1",rs.getString("leave_early_1"));
				 this.resultSet.updateString("offduty_1",rs.getString("offduty_1"));
				 this.resultSet.updateString("offduty_flextime_1",rs.getString("offduty_flextime_1"));
				 this.resultSet.updateString("offduty_end_1",rs.getString("offduty_end_1"));
				 
				 this.resultSet.updateString("onduty_card_2",rs.getString("onduty_card_2"));
				 this.resultSet.updateString("offduty_card_2",rs.getString("offduty_card_2"));
				 this.resultSet.updateString("onduty_start_2",rs.getString("onduty_start_2"));
				 this.resultSet.updateString("onduty_2",rs.getString("onduty_2"));
				 this.resultSet.updateString("onduty_flextime_2",rs.getString("onduty_flextime_2"));
				 this.resultSet.updateString("be_late_for_2",rs.getString("be_late_for_2"));
				 this.resultSet.updateString("absent_work_2",rs.getString("absent_work_2"));
				 this.resultSet.updateString("onduty_end_2",rs.getString("onduty_end_2"));		 
				 this.resultSet.updateString("offduty_start_2",rs.getString("offduty_start_2"));
				 this.resultSet.updateString("leave_early_absent_2",rs.getString("leave_early_absent_2"));
				 this.resultSet.updateString("leave_early_2",rs.getString("leave_early_2"));
				 this.resultSet.updateString("offduty_2",rs.getString("offduty_2"));
				 this.resultSet.updateString("offduty_flextime_2",rs.getString("offduty_flextime_2"));
				 this.resultSet.updateString("offduty_end_2",rs.getString("offduty_end_2"));
				 
				 this.resultSet.updateString("onduty_card_3",rs.getString("onduty_card_3"));
				 this.resultSet.updateString("offduty_card_3",rs.getString("offduty_card_3"));
				 this.resultSet.updateString("onduty_start_3",rs.getString("onduty_start_3"));
				 this.resultSet.updateString("onduty_3",rs.getString("onduty_3"));
				 this.resultSet.updateString("onduty_flextime_3",rs.getString("onduty_flextime_3"));
				 this.resultSet.updateString("be_late_for_3",rs.getString("be_late_for_3"));
				 this.resultSet.updateString("absent_work_3",rs.getString("absent_work_3"));
				 this.resultSet.updateString("onduty_end_3",rs.getString("onduty_end_3"));		 
				 this.resultSet.updateString("offduty_start_3",rs.getString("offduty_start_3"));
				 this.resultSet.updateString("leave_early_absent_3",rs.getString("leave_early_absent_3"));
				 this.resultSet.updateString("leave_early_3",rs.getString("leave_early_3"));
				 this.resultSet.updateString("offduty_3",rs.getString("offduty_3"));
				 this.resultSet.updateString("offduty_flextime_3",rs.getString("offduty_flextime_3"));
				 this.resultSet.updateString("offduty_end_3",rs.getString("offduty_end_3"));
				 
				 this.resultSet.updateString("onduty_card_4",rs.getString("onduty_card_4"));
				 this.resultSet.updateString("offduty_card_4",rs.getString("offduty_card_4"));
				 this.resultSet.updateString("onduty_start_4",rs.getString("onduty_start_4"));
				 this.resultSet.updateString("onduty_4",rs.getString("onduty_4"));
				 this.resultSet.updateString("onduty_flextime_4",rs.getString("onduty_flextime_4"));
				 this.resultSet.updateString("be_late_for_4",rs.getString("be_late_for_4"));
				 this.resultSet.updateString("absent_work_4",rs.getString("absent_work_4"));
				 this.resultSet.updateString("onduty_end_4",rs.getString("onduty_end_4"));		 
				 this.resultSet.updateString("offduty_start_4",rs.getString("offduty_start_4"));
				 this.resultSet.updateString("leave_early_absent_4",rs.getString("leave_early_absent_4"));
				 this.resultSet.updateString("leave_early_4",rs.getString("leave_early_4"));
				 this.resultSet.updateString("offduty_4",rs.getString("offduty_4"));
				 this.resultSet.updateString("offduty_flextime_4",rs.getString("offduty_flextime_4"));
				 this.resultSet.updateString("offduty_end_4",rs.getString("offduty_end_4"));
				 //other
				 this.resultSet.updateString("night_shift_start",rs.getString("night_shift_start"));
				 this.resultSet.updateString("night_shift_end",rs.getString("night_shift_end"));
				 this.resultSet.updateString("zeroflag",rs.getString("zeroflag"));
				 this.resultSet.updateString("domain_count",rs.getString("domain_count"));
				 this.resultSet.updateString("work_hours",rs.getString("work_hours"));
				 this.resultSet.updateString("zero_absent",rs.getString("zero_absent"));
				 this.resultSet.updateString("one_absent",rs.getString("one_absent"));	
				 HashMap item_Map=(HashMap)this.kqItem_hash.get(kqItem_ONDUTY);	
				 if(item_Map==null) {
                     return;
                 }
				 String itemUnit=(String)item_Map.get("item_unit");
				 String ondutyFld=(String)item_Map.get("fielditemid");
				 float ondutyTime = 0;
				 if(itemUnit==null||itemUnit.length()<=0)
			   	 {
			   		 itemUnit=unit_HOUR;
			   	 }
			   	 if(itemUnit.equals(unit_HOUR))
			   	 {
			   		ondutyTime=Float.parseFloat(rs.getString("work_hours"))/60;
			     }else if(itemUnit.equals(unit_MINUTE))
			   	 {
			    	 ondutyTime=Float.parseFloat(rs.getString("work_hours"));
			   	 }else 
			   	 {
			   		ondutyTime=1;
			   	 }
				 this.resultSet.updateString(ondutyFld,ondutyTime+"");
				 this.resultSet.updateRow();  
			}
		 } catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
    }
    /**
     * 初始化班次刷卡状态数组
     *  --------------------------------------------------------------
     * 时段(i)    |上班需打卡  | 上班已打卡 | 下班需打卡 | 下班已打卡
     * --------------------------------------------------------------
     * CardPoints |  [i][0]    |   [i][1]   |    [i][2]  |    [i][3]
     * --------------------------------------------------------------
     * 值         | True/False | True/False | True/False | True/False
     * --------------------------------------------------------------
     * @param rs
     * @return
     */
    private boolean[][] initCardPoints(ResultSet rs)
    {
    	 
		String onduty_card="";
		String offduty_card="";
		try
		{
             //0: 不刷 1：必刷 2：不限
			for(int i=0;i<4;i++)
	    	{
	    		onduty_card="onduty_card_"+(i+1);
	    		offduty_card="offduty_card_"+(i+1);
	    		if(rs.getString(onduty_card)!=null&&rs.getString(onduty_card).length()>0&&!"0".equals(rs.getString(onduty_card)))
	    		{
	    			this.cardPoints[i][0]=true;//刷卡点
	    			this.cardPoints[i][1]=false;//该点实刷情况
	    		}else
	    		{
	    			this.cardPoints[i][0]=false;
	    			this.cardPoints[i][1]=false;
	    		}
	    		if(rs.getString(offduty_card)!=null&&rs.getString(offduty_card).length()>0&&!"0".equals(rs.getString(offduty_card)))
	    		{
	    			this.cardPoints[i][2]=true;
	    			this.cardPoints[i][3]=false;
	    		}else
	    		{
	    			this.cardPoints[i][2]=false;
	    			this.cardPoints[i][3]=false;
	    		}
	    	}
		}catch(Exception e)
		{
			e.printStackTrace();
		}    	
    	return this.cardPoints;
    }
    /****************库操作***************/
    /**
     * 数据量大用ResultSet
     * @return
     */
    private Statement createStatement()
    {
    	Statement st=null;
    	try
		  {
			  //this.conn_A=AdminDb.getConnection();
			  switch(Sql_switcher.searchDbServer())
			  {
					  case Constant.MSSQL:
					  {
						  st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);			 		  
						  break;
					  }
					  case Constant.ORACEL:
					  {
						  st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);			 
						  break;
					  }
					  case Constant.DB2:
					  {
						  st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);			 
						  break;
					  }
			  }
		      
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }
	   return st;
    }
    /**
     * 关闭数据链接
     *
     */
    public void Close()
    {
        try{
          /*if(this.resultSet!=null)
           	  this.resultSet.close();
          if(this.rs_FEmpDatas!=null)
           	  this.rs_FEmpDatas.close(); */         
          if(this.stmt!=null) {
              this.stmt.close();
          }
        }catch(Exception e){
           e.printStackTrace();
        }
      }		
    /**
     * 得到一个基本班次的类
     * @param nbase
     * @param a0100
     * @param d_date
     * @return
     */
    private UnKqClassBean getKqClassShift(String nbase,String a0100,Date d_date)
    {
    	UnKqClassBean unKqClassBean=new UnKqClassBean();
    	StringBuffer sql=new StringBuffer();
    	sql.append("select b.class_id as b_class_id,");
    	sql.append("onduty_card_1,offduty_card_1,onduty_card_2,offduty_card_2,");	
    	sql.append("onduty_card_3,offduty_card_3,onduty_card_4,offduty_card_4,"); 
    	sql.append("onduty_start_1,onduty_1,onduty_flextime_1,be_late_for_1,absent_work_1,onduty_end_1,");
    	sql.append("rest_start_1,rest_end_1,offduty_start_1,leave_early_absent_1,leave_early_1,");
    	sql.append("offduty_1,offduty_flextime_1,offduty_end_1,"); 
		  //2
    	sql.append("onduty_start_2,onduty_2,onduty_flextime_2,be_late_for_2,absent_work_2,onduty_end_2,");
    	sql.append("rest_start_2,rest_end_2,offduty_start_2,leave_early_absent_2,leave_early_2,");
    	sql.append("offduty_2,offduty_flextime_2,offduty_end_2,");
		  //3
    	sql.append("onduty_start_3,onduty_3,onduty_flextime_3,be_late_for_3,absent_work_3,onduty_end_3,");
    	sql.append("rest_start_3,rest_end_3,offduty_start_3,leave_early_absent_3,leave_early_3,");
    	sql.append("offduty_3,offduty_flextime_3,offduty_end_3,");
		  //4
    	sql.append("onduty_start_4,onduty_4,onduty_flextime_4,be_late_for_4,absent_work_4,onduty_end_4,");
    	sql.append("rest_start_4,rest_end_4,offduty_start_4,leave_early_absent_4,leave_early_4,");
    	sql.append("offduty_4,offduty_flextime_4,offduty_end_4,");
		  //other
    	sql.append("night_shift_start,night_shift_end,zeroflag,domain_count,work_hours,zero_absent,one_absent");
    	sql.append(" FROM kq_employ_shift a LEFT JOIN kq_class b");
   	    sql.append(" ON a.class_id=b.class_id");
   	    sql.append(" where UPPER(nbase)='"+nbase.toUpperCase()+"'");
    	sql.append(" and a0100='"+a0100+"'");
    	sql.append(" and q03z0='"+DateUtils.format(d_date,"yyyy.MM.dd")+"'");    	
    	RowSet rs=null;
    	try{
    		
    		rs=this.dao.search(sql.toString()); 
             if(rs.next())
             {
            	 unKqClassBean.getUnKqClassBean(rs);
            	 unKqClassBean.setClass_id(rs.getString("b_class_id"));
             }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
    	return unKqClassBean;
    }
    
    
    private String kqClassShiftColumns()
    {
          StringBuffer columns=new StringBuffer();
    	  columns.append("class_id,onduty_card_1,offduty_card_1,onduty_card_2,offduty_card_2,");	
		  columns.append("onduty_card_3,offduty_card_3,onduty_card_4,offduty_card_4,"); 
		  columns.append("onduty_start_1,onduty_1,onduty_flextime_1,be_late_for_1,absent_work_1,onduty_end_1,");
		  columns.append("rest_start_1,rest_end_1,offduty_start_1,leave_early_absent_1,leave_early_1,");
		  columns.append("offduty_1,offduty_flextime_1,offduty_end_1,"); 
		  //2
		  columns.append("onduty_start_2,onduty_2,onduty_flextime_2,be_late_for_2,absent_work_2,onduty_end_2,");
		  columns.append("rest_start_2,rest_end_2,offduty_start_2,leave_early_absent_2,leave_early_2,");
		  columns.append("offduty_2,offduty_flextime_2,offduty_end_2,");
		  //3
		  columns.append("onduty_start_3,onduty_3,onduty_flextime_3,be_late_for_3,absent_work_3,onduty_end_3,");
		  columns.append("rest_start_3,rest_end_3,offduty_start_3,leave_early_absent_3,leave_early_3,");
		  columns.append("offduty_3,offduty_flextime_3,offduty_end_3,");
		  //4
		  columns.append("onduty_start_4,onduty_4,onduty_flextime_4,be_late_for_4,absent_work_4,onduty_end_4,");
		  columns.append("rest_start_4,rest_end_4,offduty_start_4,leave_early_absent_4,leave_early_4,");
		  columns.append("offduty_4,offduty_flextime_4,offduty_end_4,");
		  //other
		  columns.append("night_shift_start,night_shift_end,zeroflag,domain_count,work_hours,zero_absent,one_absent");
          return columns.toString();
    }
    private float getUtilValue(String itemUnit,float timeLen,float work_hours)
    {
    	 float unit=0;   	    
   	    if(itemUnit==null||itemUnit.length()<=0)
   	    {
   		 itemUnit=unit_MINUTE;
   	    }
   	    if(itemUnit.equals(unit_HOUR))
   	    {
   		   unit=timeLen/60;
   	    }else if(itemUnit.equals(unit_MINUTE))
   	    {
   		 unit=timeLen;
   	    }else if(itemUnit.equals(unit_DAY))
   	    {
   	    	unit=timeLen/work_hours;
   	    }else if(itemUnit.equals(unit_ONCE))
   	    {
   		   unit=1;
   	    }
   	    return unit;
    }
    
    /**
     * 调整公休节假日考勤项目
     * @param analyse_Tmp
     */
    private void specialDisposal(String analyse_Tmp)throws GeneralException
    {
    	StringBuffer upSQL=null;
    	HashMap item_ORest=(HashMap)this.kqItem_hash.get(kqItem_ORest);
    	HashMap item_OFeast=(HashMap)this.kqItem_hash.get(kqItem_OFeast);
    	HashMap item_Onduty=(HashMap)this.kqItem_hash.get(kqItem_ONDUTY);
    	String fieldORest="";
    	String fieldOFeast="";
    	String fieldOnduty="";
    	if(item_ORest!=null) {
            fieldORest=(String)item_ORest.get("fielditemid");
        }
    	if(item_OFeast!=null) {
            fieldOFeast=(String)item_OFeast.get("fielditemid");
        }
    	if(item_Onduty!=null) {
            fieldOnduty=(String)item_Onduty.get("fielditemid");
        }
    	ContentDAO dao=new ContentDAO(this.conn);
    	try
    	{
    		/*if(fieldORest!=null&&fieldORest.length()>0)//清除非公休排班的公休日加班数据
        	{
        		upSQL=new StringBuffer();
        		upSQL.append("update "+analyse_Tmp+" set ");
            	upSQL.append(fieldORest+"=NULL ");
            	upSQL.append(" where class_id  IS NOT NULL AND class_id<>0 ");
            	
            	dao.update(upSQL.toString());
        	}*/
    		if(fieldOFeast!=null&&fieldOFeast.length()>0&&fieldOnduty!=null&&fieldOnduty.length()>0)//为排在节假日的班次人员，补充节假日加班数据
        	{
        		upSQL=new StringBuffer();
        		upSQL.append("update "+analyse_Tmp+" set ");
        		upSQL.append(fieldOFeast+"="+fieldOnduty);
        		upSQL.append(" where "+this.kq_dkind+"='"+dkHoliday+"'");
        		upSQL.append(" and ("+fieldOFeast+" is null or "+fieldOFeast+"=0)");
        		upSQL.append(" and "+fieldOnduty+">0.01");
        		//System.out.println(upSQL.toString());
        		dao.update(upSQL.toString());
        	}
            //清除公休日考勤项目数据SQL模板
    		StringBuffer sqlRest=null;
    		
            //考勤项目对公休日和节假日数据的要求
    		String has_rest="";
		    String has_feast="";
		    String fielditemid="";
		    
    		for(Iterator it = this.kqItem_hash.entrySet().iterator(); it.hasNext(); )
    		{
    			Map.Entry e = (Map.Entry)it.next();    			
    			if("1".equals(e.getKey().toString().substring(0,1)))//加班不适合该约束
                {
                    continue;
                }
    			HashMap item_hs=(HashMap)e.getValue();    			
    			has_rest=(String)item_hs.get("has_rest");
    			has_feast=(String)item_hs.get("has_feast");
    			if(has_rest!=null&& "0".equals(has_rest))//不包含公休日
    			{
    				fielditemid=(String)item_hs.get("fielditemid");
    				sqlRest=new StringBuffer();
    				if(fielditemid!=null&&fielditemid.length()>0)
    				{
    					sqlRest.append("update "+analyse_Tmp+" set");
    					sqlRest.append(" "+fielditemid+"=NULL ");
    					sqlRest.append(" where (class_id  IS  NULL or class_id=0)");    
    					//System.out.println(sqlRest.toString());
    					dao.update(sqlRest.toString());
    				}    					
    			}
                //不包含节假日
    			if(has_feast!=null&& "0".equals(has_feast))//不包含公休日
    			{
    				fielditemid=(String)item_hs.get("fielditemid");
    				sqlRest=new StringBuffer();
    				if(fielditemid!=null&&fielditemid.length()>0)
    				{
    					sqlRest.append("update "+analyse_Tmp+" set");
    					sqlRest.append(" "+fielditemid+"=NULL ");
    					sqlRest.append(" where "+this.kq_dkind+"='"+dkHoliday+"'");
    					//System.out.println(sqlRest.toString());
    					dao.update(sqlRest.toString());
    				}
    			}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		 //throw GeneralExceptionHandler.Handle(new GeneralException("","统计错误，请查看考勤项目中，统计指标是否对应！","",""));
    	}    	
    }
    /** 处理矿工和夜班
     * 1.先将各考勤项目的值统一转换成小时数,然后整体转换成旷工的单位
       2.旷工 = 应出勤 - 请假s - 公出s
       3.计算条件：旷工=应出勤
       4.只有在全天旷工情况下可用此算法，其它情况无法判断旷工的具体数值
     * @param analyse_Tmp
     */
    private void calcFactAbsent(String analyse_Tmp)throws GeneralException
    {
    	HashMap item_Onduty=(HashMap)this.kqItem_hash.get(kqItem_ONDUTY);
    	if(item_Onduty==null) {
            return;
        }
    	String ondutyFld=(String)item_Onduty.get("fielditemid");
    	HashMap item_WAbsent=(HashMap)this.kqItem_hash.get(kqItem_WAbsent);
    	if(item_WAbsent==null) {
            return;
        }
    	String absentFld=(String)item_WAbsent.get("fielditemid");
    	HashMap item_hs=null;
    	String itemUnit="";
    	String fielditemid="";
    	String formula="";
    	String itemValue="";
    	StringBuffer sql=null;
    	if(absentFld==null||absentFld.length()<=0) {
            return;
        }
    	
    	for(Iterator it = this.kqItem_hash.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry e = (Map.Entry)it.next();  			   		
			if("1".equals(e.getKey().toString().substring(0,1)))//加班不适合该约束
            {
                continue;
            }
			item_hs=(HashMap)e.getValue();	
			fielditemid=(String)item_hs.get("fielditemid");
			if(fielditemid!=null&&fielditemid.length()>0)
			{
				itemValue=tranUnitToHours(item_hs);
				if(itemValue!=null&&itemValue.length()>0) {
                    formula=formula+"-"+itemValue;
                }
			}
		}    	
    	try
    	{
    		if(formula!=null&&formula.length()>0)
        	{
        		String formulaWhr=formula.replaceAll("-","+");
        		formulaWhr=formulaWhr.substring(1);
        		sql=new StringBuffer();
        		sql.append("update "+analyse_Tmp+" set");
        		sql.append(" "+absentFld+"=null");
        		sql.append(" where "+tranUnitToHours(item_Onduty)+"="+formulaWhr);
        		//System.out.println("旷工1-->"+sql.toString());
        		this.dao.update(sql.toString());   
        		return;
        	}
    		if((ondutyFld!=null&&ondutyFld.length()>0)&&(formula!=null&&formula.length()>0)) {
                formula = "(" + tranUnitToHours(item_Onduty) + formula + ")";
            } else {
                return;
            }
    		itemUnit=(String)item_WAbsent.get("itemUnit");
    		if(itemUnit==null||itemUnit.length()<=0) {
                itemUnit="";
            }
    		String tranFormula="";
    		String timesWhr="";
    		if(itemUnit.equals(unit_MINUTE))
       	    {
    			tranFormula="("+formula+"*60)";
       	    }else if(itemUnit.equals(unit_DAY))
       	    {
       	    	tranFormula="("+formula+"/work_hours";
       	    }else if(itemUnit.equals(unit_ONCE))
       	    {
       	    	timesWhr="("+formula+">0.001)";
       	    }else
       	    {
       	    	tranFormula=formula;
       	    }
    		//计算矿工
    		sql=new StringBuffer();
    		sql.append("update"+analyse_Tmp+" set");
    		sql.append(" "+absentFld+"=");
    		if(timesWhr==null||timesWhr.length()<=0)
    		{
    			sql.append(Sql_switcher.isnull(tranFormula,"0"));
    		}else
    		{
    			sql.append("1");
    		}    		
    		sql.append(" where "+Sql_switcher.isnull("class_id", "'##'")+"<>'##'  AND class_id<>0");
    		sql.append(" and "+Sql_switcher.isnull(absentFld,"0")+">0");
    		sql.append(" and "+tranUnitToHours(item_WAbsent)+"="+tranUnitToHours(item_Onduty));
    		if(timesWhr!=null&&timesWhr.length()>0) {
                sql.append(" and "+timesWhr);
            }
    		//System.out.println("旷工2-->"+sql.toString());
    		this.dao.update(sql.toString());   
    		//夜班
    		HashMap nightHash=(HashMap)this.kqItem_hash.get(kqItem_Night);
    		if(nightHash==null) {
                return;
            }
    		String nightFld=(String)nightHash.get("fielditemid");
    		sql=new StringBuffer();
    		sql.append("update "+analyse_Tmp+" set");
    		sql.append(" "+nightFld+"=null");
    		sql.append(" where "+nightFld+">0");
    		sql.append(" and ("+Sql_switcher.isnull(absentFld,"0")+">0");
    		sql.append(" and "+tranUnitToHours(item_WAbsent)+"="+tranUnitToHours(item_Onduty)+")");
    		sql.append(" or ("+formula+"=0)");
    		//System.out.println("旷工3-->"+sql.toString());
    		this.dao.update(sql.toString()); 
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(new GeneralException("","统计错误，请查看考勤项目中，统计指标是否对应！","",""));
    	}
    	
    }
    private String tranUnitToHours(HashMap item_hs)
    {
    	String itemUnit=(String)item_hs.get("item_unit");
    	String fielditemid=(String)item_hs.get("fielditemid");
    	String chgItemValue="";
    	chgItemValue=Sql_switcher.isnull(fielditemid,"0");
    	if(itemUnit.equals(unit_HOUR))
   	    {
    		
   	    }else if(itemUnit.equals(unit_MINUTE))
   	    {
   	    	chgItemValue=chgItemValue+"/60";
   	    }else if(itemUnit.equals(unit_DAY))
   	    {
   	    	chgItemValue=chgItemValue+"*work_hours";
   	    }else
   	    {
   	    	chgItemValue="";
   	    }
    	return chgItemValue;
    }
    /**
     * 更新全部数据
     * @param table_temp
     */
    public void updateDataToQ03(String table_temp,HashMap kqItem_Map)
    {
    	 HashMap item_hs=null;    	 
    	 String destTab="q03";//目标表
		 String srcTab=table_temp;//源表
		 String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase= "+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
		 StringBuffer strSet=new StringBuffer();
		 strSet.append(destTab+".q03z3="+srcTab+".q03z3");//更新串  xxx.field_name=yyyy.field_namex,....
		 StringBuffer srcFlds=new StringBuffer();
		 String srcFld="";
		 for(Iterator it = kqItem_Map.entrySet().iterator(); it.hasNext(); )
		 {
			   Map.Entry e = (Map.Entry)it.next(); 
			   item_hs=(HashMap)e.getValue();	
			   srcFld=(String)item_hs.get("fielditemid");
			   if(srcFld!=null&&srcFld.length()>0)
	  	   	   {
	  	   			strSet.append("`"+destTab+"."+srcFld+"="+srcTab+"."+srcFld);
	  	   		    srcFlds.append(","+srcFld);
	  	   	   }
	  	 }
		 String strSWhere=table_temp+".flag='1'";//源表的过滤条件 
		
		 String onStr=destTab+".A0100="+srcTab+".A0100 and  "+destTab+".nbase= "+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";
		 String strDWhere="EXISTS(SELECT 1 FROM " +srcTab+" WHERE "+onStr+" and "+strSWhere+") and "+destTab+".Q03Z5 in ('01','07')";//更新目标的表过滤条件		 
		 if(getKqTypeWhr("q03.q03z3")!=null&&getKqTypeWhr(this.kq_type).length()>0)
		 {
			 strDWhere=strDWhere+" and "+getKqTypeWhr("q03.q03z3");
		 }
		 //修改已有的
		 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet.toString(),strDWhere,strSWhere);	
		 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
		 //System.out.println("更新人员的考勤方式--->"+update);		
		 //String strSet_2=strSet.toString().replace('`',',');
		 //添加没有的
		 if(this.pick_flag!=null&&!"1".equals(this.pick_flag))
		 {
			 StringBuffer insertSql=new StringBuffer();
			 insertSql.append("INSERT INTO Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",q03z3,q03z5)");
			 insertSql.append(" select Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",Q03Z3,'01' as q03z5");
			 insertSql.append(" from "+srcTab);
			 insertSql.append(" where   "+strSWhere+" and NOT EXISTS(SELECT 1 FROM "+destTab+" WHERE "+onStr+")");		 
		     try
		     {
		    	 ArrayList list=new ArrayList();
		    	 this.dao.update(update);
		    	 this.dao.insert(insertSql.toString(),list);
		     }catch(Exception e)
		     {
		    	 e.printStackTrace();
		     }
		 }
		 
    }
    /**
     * 更新全部数据 集中表 wangy
     * @param table_temp
     */
    public void updateDataToQ03jz(String table_temp,HashMap kqItem_Map,String code,String kind)
    {
    	 HashMap item_hs=null;
    	 String codewhere="";
 		 if("1".equals(kind))
 		 {
 			codewhere=" e0122 like '"+code+"%'";
 		 }else if("0".equals(kind))
 		 {
 			codewhere=" e01a1 like '"+code+"%'";	
 		 }else if("2".equals(kind))
 		 {
 			codewhere=" b0110 like '"+code+"%'";	
 		 }else if("-1".equals(kind))
		 {
			 String t = code.substring(3,code.length());
			 String t1 = code.substring(0,3);
			 codewhere=" a0100='"+t+"' and nbase='"+t1+"'";
		 }
 		 
    	 String destTab="q03";//目标表
		 String srcTab=table_temp;//源表
		 String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase= "+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
		 StringBuffer strSet=new StringBuffer();
		 strSet.append(destTab+".q03z3="+srcTab+".q03z3");//更新串  xxx.field_name=yyyy.field_namex,....
		 StringBuffer srcFlds=new StringBuffer();
		 String srcFld="";
		 for(Iterator it = kqItem_Map.entrySet().iterator(); it.hasNext(); )
		 {
			   Map.Entry e = (Map.Entry)it.next(); 
			   item_hs=(HashMap)e.getValue();	
			   srcFld=(String)item_hs.get("fielditemid");
			   if(srcFld!=null&&srcFld.length()>0)
	  	   	   {
	  	   			strSet.append("`"+destTab+"."+srcFld+"="+srcTab+"."+srcFld);
	  	   		    srcFlds.append(","+srcFld);
	  	   	   }
	  	 }
		 String strSWhere=table_temp+".flag='1'";//源表的过滤条件 
		
		 String onStr=destTab+".A0100="+srcTab+".A0100 and  "+destTab+".nbase= "+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";
		 String strDWhere="EXISTS(SELECT 1 FROM " +srcTab+" WHERE "+onStr+" and "+strSWhere+") and "+destTab+".Q03Z5 in ('01','07')";//更新目标的表过滤条件		 
		 if(getKqTypeWhr("q03.q03z3")!=null&&getKqTypeWhr(this.kq_type).length()>0)
		 {
			 strDWhere=strDWhere+" and "+getKqTypeWhr("q03.q03z3");
		 }
		 //修改已有的
		 String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet.toString(),strDWhere,strSWhere);	
		 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
		 //System.out.println("更新人员的考勤方式--->"+update);		
		 //String strSet_2=strSet.toString().replace('`',',');
		 //添加没有的
		 if(this.pick_flag!=null&&!"1".equals(this.pick_flag))
		 {
			 StringBuffer insertSql=new StringBuffer();
			 insertSql.append("INSERT INTO Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",q03z3,q03z5)");
			 insertSql.append(" select Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",Q03Z3,'01' as q03z5");
			 insertSql.append(" from "+srcTab);
			 insertSql.append(" where   "+strSWhere+" and NOT EXISTS(SELECT 1 FROM "+destTab+" WHERE "+onStr+")");		 
		     try
		     {
		    	 ArrayList list=new ArrayList();
		    	 update+=" and "+codewhere;
		    	 this.dao.update(update);
		    	 insertSql.append(" and "+codewhere);
		    	 this.dao.insert(insertSql.toString(),list);
		     }catch(Exception e)
		     {
		    	 e.printStackTrace();
		     }
		 }
		 
    }
    /**
     * 只更新业务数据
     * sdata_src  数据来源
     */
    private void updateBusiDataToQ03(String table_temp)
    {
    	 HashMap item_hs=null;   	     
   	     String sdata_src="";//数据来源
   	     String destTab="q03";//目标表
		 String srcTab=table_temp;//源表
		 String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase= "+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
		 String strSet="";
		 //strSet.append(destTab+".q03z3="+srcTab+".q03z3");//更新串  xxx.field_name=yyyy.field_namex,....
		 StringBuffer srcFlds=new StringBuffer();
		 String srcFld="";
		 String strSWhere="";//源表的过滤条件 
		 String onStr=destTab+".A0100="+srcTab+".A0100 and  "+destTab+".nbase="+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";
		 String strDWhere="EXISTS(SELECT 1 FROM " +srcTab+" WHERE "+onStr+") and "+destTab+".Q03Z5 in ('01','07')";//更新目标的表过滤条件		 
		 String update="";
		 try
		 {
			 if(this.no_tranData!=null&& "1".equals(no_tranData))
			 {
				  srcFld="q03z1";
				  srcFlds.append(","+srcFld);			    
			      //修改已有的数据来源
			      strSet=destTab+"."+srcFld+"="+srcTab+"."+srcFld;	
				  strSWhere=Sql_switcher.sqlNull(srcTab+"."+srcFld,"0")+">0.01";
				  update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);	
				  update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
				  //System.out.println(update);
				  this.dao.update(update);
			     
			 } else{
				 for(Iterator it = this.kqItem_hash.entrySet().iterator(); it.hasNext(); )
				 {
					   Map.Entry e = (Map.Entry)it.next(); 
					   item_hs=(HashMap)e.getValue();	
					   srcFld=(String)item_hs.get("fielditemid");
					   if(srcFld!=null&&srcFld.length()>0)
			  	   	   {
			  	   			srcFlds.append(","+srcFld);
			  	   	   }
					   sdata_src=(String)item_hs.get("sdata_src");
					   //修改已有的数据来源
					   if(sdata_src!=null&&sdata_src.length()>0)
					   {
						   
						   strSet=destTab+"."+srcFld+"="+srcTab+"."+srcFld;	
						   strSWhere=Sql_switcher.sqlNull(srcTab+"."+srcFld,"0")+">0.01";
						   update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);	
						   update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
						   //System.out.println(update);
						   this.dao.update(update);
					   }				  
			  	 }
			 }
			 
			 strSet=destTab+".q03z3="+srcTab+".q03z3";	
			 update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,"");
			 update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
			 this.dao.update(update);			 
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 //添加没有的
		 if(this.pick_flag!=null&&!"1".equals(this.pick_flag))
		 {
			 StringBuffer insertSql=new StringBuffer();
			 insertSql.append("INSERT INTO Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",q03z3,q03z5)");
			 insertSql.append(" select Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",Q03Z3,'01' as q03z5");
			 insertSql.append(" from "+srcTab);
			 insertSql.append(" where  NOT EXISTS(SELECT 1 FROM "+destTab+" WHERE "+onStr+")");		 
		     try
		     {
		    	 ArrayList list=new ArrayList();
		    	 //System.out.println(insertSql.toString());
		    	 dao.insert(insertSql.toString(),list);
		     }catch(Exception e)
		     {
		    	 e.printStackTrace();
		     }
		 }
		 
    }
   
    private String strSql;
    private String column;
    private ArrayList fieldList=new ArrayList();
    private String whereStr;
	public String getColumn() {
		
		return column;
	}
	public ArrayList getFieldList() {
		return fieldList;
	}
	public String getStrSql() {
		return strSql;
	}
	public String getTemp_Table() {
		return temp_Table;
	}
	public String getOrderBy()
	{
		return "order by nbase,b0110,e0122";
	}
    private void dataExceptionReport()
    {
    	StringBuffer columns=new StringBuffer();
    	FieldItem fielditem_c=new FieldItem();
		ArrayList list=new ArrayList();
		for(int i=0;i<this.columnlist.size();i++)
		{
			FieldItem fielditem=(FieldItem)this.columnlist.get(i);
			columns.append(fielditem.getItemid()+",");
			if("A".equals(fielditem.getItemtype())|| "N".equals(fielditem.getItemtype()))
			{
				if(!"i9999".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid())&&!"q03z5".equals(fielditem.getItemid()))
				{
					if("a0100".equals(fielditem.getItemid()))
					{
						fielditem.setVisible(false);
					}else
					{
						if("1".equals(fielditem.getState()))
						{
							fielditem.setVisible(true);
						}else
						{
							fielditem.setVisible(false);
						}
					}						
				  }else
				  {
					  fielditem.setVisible(false);
				  }
				  list.add(fielditem);
		      }			
		}
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("上班一");
		fielditem_c.setItemid("onduty_1");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);
		list.add(fielditem_c);
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("下班一");
		fielditem_c.setItemid("offduty_1");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);
		list.add(fielditem_c);
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("上班二");
		fielditem_c.setItemid("onduty_2");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);
		list.add(fielditem_c);
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("下班二");
		fielditem_c.setItemid("offduty_2");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);
		list.add(fielditem_c);
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("上班三");
		fielditem_c.setItemid("onduty_3");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);
		list.add(fielditem_c);
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("下班三");
		fielditem_c.setItemid("offduty_3");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);		
		list.add(fielditem_c);
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("加班上");
		fielditem_c.setItemid("onduty_4");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);
		list.add(fielditem_c);
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("加班下");
		fielditem_c.setItemid("offduty_4");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);
		list.add(fielditem_c);
		fielditem_c=new FieldItem();
		fielditem_c.setItemdesc("刷卡时间");
		fielditem_c.setItemid("card_time");
		fielditem_c.setItemtype("A");
		fielditem_c.setCodesetid("0");
		fielditem_c.setVisible(true);
		list.add(fielditem_c);		
		columns.append("onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,onduty_4,offduty_4,card_time");
		this.fieldList=list;
		this.column=columns.toString();		
		this.strSql=" select "+columns.toString();
		this.whereStr=" from "+this.temp_Table;
    }
	public String getWhereStr() {
		return whereStr;
	}
	private String getB0110ForA0100(String nbase,String a0100)
	{
		String b0110="";
		RowSet rs=null;
		try
		{
			String sql="select b0110 from "+nbase+"A01 where a0100='"+a0100+"'";
			rs=this.dao.search(sql);
			if(rs.next())
			{
			 b0110=rs.getString("b0110");				
			}
		}catch(Exception e)
		{
		   e.printStackTrace();	
		}finally
		{
			if(rs!=null)
			{
				 try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
		return b0110;
	}
	/**
	 * 删除临时表
	 * */
	public void dropTable()
	{
		try
		{
			DbWizard dbWizard =new DbWizard(this.conn);
			Table table=new Table(this.temp_Table);
			dbWizard.dropTable(table);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	/**
	 * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static float round(String v,int scale)
    {
        if(scale<0)
        {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
            }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).floatValue();
    }
    /**
     * 相差分钟
     * @param start_date
     * @param end_date
     * @return
     */
    public static long getPartMinute(Date start_date,Date end_date){    	
		int sY=DateUtils.getYear(start_date);
		int sM=DateUtils.getMonth(start_date);
		int sD=DateUtils.getDay(start_date);
		int sH=DateUtils.getHour(start_date);
		int smm=DateUtils.getMinute(start_date);
		
		int eY=DateUtils.getYear(end_date);
		int eM=DateUtils.getMonth(end_date);
		int eD=DateUtils.getDay(end_date);
		int eH=DateUtils.getHour(end_date);
		int emm=DateUtils.getMinute(end_date);
		GregorianCalendar d1= new GregorianCalendar(sY,sM,sD,sH,smm,00);
		GregorianCalendar d2= new GregorianCalendar(eY,eM,eD,eH,emm,00);
		Date date1= d1.getTime();		         
        Date date2= d2.getTime();
        long l1=date1.getTime();
        long l2=date2.getTime();
        long part=(l2-l1)/(60*1000L);
		return part;
	}
    /**
     * 相差小时
     * @param start_date
     * @param end_date
     * @return
     */
    public static long getHourSpan(Date start_date,Date end_date){    	
		int sY=DateUtils.getYear(start_date);
		int sM=DateUtils.getMonth(start_date);
		int sD=DateUtils.getDay(start_date);
		int sH=DateUtils.getHour(start_date);
		int smm=DateUtils.getMinute(start_date);
		
		int eY=DateUtils.getYear(end_date);
		int eM=DateUtils.getMonth(end_date);
		int eD=DateUtils.getDay(end_date);
		int eH=DateUtils.getHour(end_date);
		int emm=DateUtils.getMinute(end_date);
		GregorianCalendar d1= new GregorianCalendar(sY,sM,sD,sH,smm,00);
		GregorianCalendar d2= new GregorianCalendar(eY,eM,eD,eH,emm,00);
		Date date1= d1.getTime();		         
        Date date2= d2.getTime();
        long l1=date1.getTime();
        long l2=date2.getTime();
        long part=(l2-l1)/(60*60*1000L);
		return part;
	}
    public float getfatNightTime(String strDate,HashMap app_hash,String nbase,String a0100)
	{
    	if(this.nightTimeLen<=0) {
            return 0;
        }
    	String app_key=nbase+a0100+"q15";//请假申请
    	ArrayList app_list=(ArrayList)app_hash.get(app_key.toUpperCase()); 
    	if(app_list==null||app_list.size()<=0)
    	{
    		 return this.nightTimeLen;
    	}
		HashMap hashM=null;
   	    String apptype="";
   	    Date s_date=null;
   	    Date e_date=null;
   	    Date NFDT=null;
   	    Date NTDT=null;
   	    float noNightLen=0;
   	    float tiemlen=0;
   	    try
   	    {
   	      String nightShiftStart=this.resultSet.getString("night_shift_start");
 	      String nightShiftEnd=this.resultSet.getString("night_shift_end");   		     
 	      if(nightShiftEnd==null||nightShiftEnd.length()<=0||nightShiftStart==null||nightShiftStart.length()<=0||nightShiftStart==nightShiftEnd)
 	      {
 	    	return 0;
 	      }else
 	      {
 	    	NFDT=DateUtils.getDate(strDate+" "+nightShiftStart,"yyyy.MM.dd HH:mm");
 	    	NTDT=DateUtils.getDate(strDate+" "+nightShiftEnd,"yyyy.MM.dd HH:mm");
 	      }
 	      Date nightShiftStartTime=DateUtils.getDate(nightShiftStart,"HH:mm");
	      Date nightShiftEndTime=DateUtils.getDate(nightShiftEnd,"HH:mm");
	      String onduty_1=this.resultSet.getString("onduty_1");
		  if(onduty_1==null||onduty_1.length()<=0) {
              throw GeneralExceptionHandler.Handle(new GeneralException("","数据分析失败！可能的原因：班次信息定义不完整。","",""));
          }
 	      Date onTime=DateUtils.getDate(onduty_1,"HH:mm");
	      float time_f=getPartMinute(nightShiftStartTime,onTime);
	      if(time_f>0)
	      {
	    		NFDT=DateUtils.addDays(NFDT,1);
	    		NTDT=DateUtils.addDays(NTDT,1);
	      }
	      time_f=getPartMinute(nightShiftStartTime,nightShiftEndTime);
	      if(time_f<0)
	      {
	    		NTDT=DateUtils.addDays(NTDT,1);
	      } 
    	  for(int i=0;i<app_list.size();i++)
 	      {
 		    hashM=(HashMap)app_list.get(i);
 		    apptype=(String)hashM.get("apptype");
 		    //System.out.print(apptype);
 		    if(apptype!=null&&apptype.indexOf(kqItem_OGeneral)==0) {
                continue;//平时加班分出来算
            }
 		    s_date=(Date)hashM.get("s_date");
 		    e_date=(Date)hashM.get("e_date"); 
 		   /* System.out.println(DateUtils.format(s_date,"yyyy.MM.dd HH:mm"));
 		    System.out.println(DateUtils.format(e_date,"yyyy.MM.dd HH:mm"));
 		    System.out.println(DateUtils.format(NFDT,"yyyy.MM.dd HH:mm"));
		    System.out.println(DateUtils.format(NTDT,"yyyy.MM.dd HH:mm"));
		    System.out.println("$$$$$$$$$$$$");*/
 		    noNightLen=noNightLen+calcTimSpan(NFDT,NTDT,s_date,e_date);
 	      }
   	   }catch(Exception e)
   	   {
   	    	e.printStackTrace();
   	   }
   	   tiemlen=this.nightTimeLen-noNightLen;
   	   if(tiemlen<=0) {
           tiemlen=0;
       }
   	   return tiemlen;
   }
    /**
     * 检测延时加班
     * @param empClassBean
     * @param strDate
     * @param fEmpDatas
     */
    private void checkTranOvertimeCard(KqEmpClassBean empClassBean,String strDate,RowSet fEmpDatas)
	{
    	//不检测延时加班
         if(empClassBean.getCheck_tran_overtime()==null|| "".equals(empClassBean.getCheck_tran_overtime())||!"1".equals(empClassBean.getCheck_tran_overtime())) {
             return;
         }
         //定义了固定的加班时段
         if(empClassBean.getOffduty_4()!=null&&empClassBean.getOffduty_4().length()>0) {
             return;
         }
         int i=2;
         for(;i>=0;i--)
         {
        	 if(empClassBean.getOffduty((i+1)+"")!=null&&empClassBean.getOffduty((i+1)+"").length()>0)
        	{
        		 break;
        	}
         }
         if(i<0) {
             return;
         }
//       下班不要求刷卡或者下班没有有效刷卡
         if(!this.cardPoints[i][2]||!this.cardPoints[i][3]) {
             return;
         }
         Date off_Tiem;
         Date on_Tiem;
         String off_str=empClassBean.getOffduty();
    	 String on_str=empClassBean.getOnduty();
    	 if(on_str==null||on_str.length()<=0||off_str==null||off_str.length()<=0) {
             return;
         }
    	 off_Tiem=DateUtils.getDate(strDate+" "+off_str,this.date_format);
    	 long off_L=off_Tiem.getTime();
    	 off_Tiem.setTime(off_L+(empClassBean.getOvertime_from()*60*1000L));
    	 on_Tiem=DateUtils.getDate(strDate+" "+on_str,this.date_format);
    	 float time_f=getPartMinute(on_Tiem,off_Tiem);
    	 if(time_f<=0) {
             off_Tiem=DateUtils.addDays(off_Tiem,1);
         }
    	 Date cardTime=null;
    	 String inoutFlag="";
    	 String status="";
    	 Date tranOtBegin = null;
    	 Date tranOtEnd = null;
    	 try
    	 {
    		 fEmpDatas.beforeFirst();
        	 while(fEmpDatas.next())
        	 {
        		String work_setTiem=fEmpDatas.getString("work_time");         		
         		String work_strDate=fEmpDatas.getString("work_date");         		
         		cardTime=DateUtils.getDate(work_strDate+" "+work_setTiem,"yyyy.MM.dd HH:mm");
         		time_f=getPartMinute(off_Tiem,cardTime);
         		if(time_f<=0) {
                    continue;
                }
         		inoutFlag=fEmpDatas.getString("inoutFlag");
         		status=fEmpDatas.getString("status");
         		if(inoutFlag==null||inoutFlag.length()<=0) {
                    inoutFlag="";
                }
         		if(status==null||status.length()<=0) {
                    status="";
                }
         		if ((tranOtBegin==null)&&(!"-1".equals(inoutFlag)))//如果非出
         		{
         			if("1".equals(inoutFlag)||(!"-1".equals(inoutFlag)&&!fEmpDatas.isLast()))
         			{
         				tranOtBegin=cardTime;
         				continue;
         			}
         		}
         		if(this.kqParam.getCard_interval()>0&& "-1".equals(status)&& "1".equals(inoutFlag))
                {
         			tranOtEnd= cardTime;
                }
                if(tranOtEnd!=null&&tranOtBegin!=null)
                {
                	this.rstTranOverTime[0]=tranOtBegin;
                	this.rstTranOverTime[1]=tranOtEnd;
                }
                /*else if(tranOtBegin!=null&&tranOtEnd==null)
                {
                	this.rstTranOverTime[0]=DateUtils.format(off_Tiem,this.date_format);
                	this.rstTranOverTime[1]=DateUtils.format(tranOtBegin,this.date_format);
                }*/
                else if(tranOtBegin==null&&tranOtEnd!=null)
                {
                	this.rstTranOverTime[0]=off_Tiem;
                	this.rstTranOverTime[1]=tranOtEnd;
                }
        	 }
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 
	}
    /**
     * 计算离岗时间
     * @param empClassBean
     * @param strDate
     * @param nums
     */
    private void calcFactLeaveTimeLen(KqEmpClassBean empClassBean,String strDate,int  nums)
    {
    	try
    	{
    		String inout_flag=this.rs_FEmpDatas.getString("inout_flag");
    		String work_setTiem=this.rs_FEmpDatas.getString("work_time");    		
    		String work_strDate=this.rs_FEmpDatas.getString("work_date");
    		Date card_time=DateUtils.getDate(work_strDate+" "+work_setTiem,"yyyy.MM.dd HH:mm");
    		Date restTB1=null;
    		Date restTE1=null;
    		String on_time_str=empClassBean.getOnduty();
    		String off_time_str=empClassBean.getOffduty();
    		if(on_time_str==null||on_time_str.length()<=0||off_time_str==null||off_time_str.length()<=0) {
                return;
            }
    		/************判断是否在上班时间之内******************/
    		Date on_time=DateUtils.getDate(on_time_str,"HH:mm");
    		Date off_time=DateUtils.getDate(off_time_str,"HH:mm");
    		float time_f=getPartMinute(on_time,off_time);
    		Date on_T=null;
    		Date off_T=null;
    		if(time_f>0)
    		{
    			on_T=DateUtils.getDate(strDate+" "+on_time_str,"yyyy.MM.dd HH:mm");
    			off_T=DateUtils.getDate(strDate+" "+off_time_str,"yyyy.MM.dd HH:mm");
    		}else
    		{
    			on_T=DateUtils.getDate(strDate+" "+on_time_str,"yyyy.MM.dd HH:mm");
    			off_T=DateUtils.getDate(strDate+" "+off_time_str,"yyyy.MM.dd HH:mm");
    			off_T=DateUtils.addDays(off_T,1);
    		}
            //上班前
    		time_f=getPartMinute(card_time,on_T);
    		if(time_f>=0) {
                return;
            }
     		//下班后    		
    		time_f=getPartMinute(off_T,card_time);
    		if(time_f>=0) {
                return;
            }
            //出时间为空并且当前刷卡不是进，或者当前刷卡为出
    		if(this.outTime==null&&(!"1".equals(inout_flag)|| "-1".equals(inout_flag)))
    		{
    			this.outTime=card_time;
    			return;
    		}
            //有出的时间后，再找进入的时间
    		if(this.outTime!=null&&this.inTime==null&&(!"-1".equals(inout_flag)))
    		{
    			this.inTime=card_time;
    			for(int i=1;i<=2;i++)
       		    {
        		   if(empClassBean.getOnduty((i+1)+"")!=null&&empClassBean.getOnduty((i+1)+"").length()>0)//下一段班
        		   {
        			   String fore_on_tiem_str=empClassBean.getOnduty(i+"");
        			   String fore_off_tiem_str=empClassBean.getOffduty(i+"");
        			   String next_on_tiem_str=empClassBean.getOnduty((i+1)+"");    			   
        			   Date fore_on_T=DateUtils.getDate(fore_on_tiem_str,"HH:mm");
        			   Date fore_off_T=DateUtils.getDate(fore_off_tiem_str,"HH:mm");
        			   Date next_on_T=DateUtils.getDate(next_on_tiem_str,"HH:mm");    			   
        			   time_f=getPartMinute(fore_on_T,next_on_T);
        			   if(time_f>0)//判断跨天
        			   {
        				   restTB1=DateUtils.getDate(strDate+" "+fore_off_tiem_str,"yyyy.MM.dd HH:mm");
            			   restTE1=DateUtils.getDate(strDate+" "+next_on_tiem_str,"yyyy.MM.dd HH:mm");
        			   }else
        			   {
        				   time_f=getPartMinute(fore_on_T,fore_off_T);
        				   if(time_f>0)
        				   {
        					   restTB1=DateUtils.getDate(strDate+" "+fore_off_tiem_str,"yyyy.MM.dd HH:mm");
        				   }else
        				   {
        					   restTB1=DateUtils.getDate(strDate+" "+fore_off_tiem_str,"yyyy.MM.dd HH:mm");
        					   restTB1=DateUtils.addDays(restTB1,1);
        				   }    				   
            			   restTE1=DateUtils.getDate(strDate+" "+next_on_tiem_str,"yyyy.MM.dd HH:mm");
            			   restTE1=DateUtils.addDays(restTE1,1);
        			   }
        			   float time_o_B=getPartMinute(this.outTime,restTB1);
        			   float time_o_E=getPartMinute(this.outTime,restTE1);
        			   float time_i_B=getPartMinute(this.inTime,restTB1);
        			   float time_i_E=getPartMinute(this.inTime,restTE1);
                       //出入时间均找到后，检测是否为休息时段出入
        			   if(time_o_B<=0&&time_o_E>=0&&time_i_B<=0&&time_i_E>=0)
        			   {
        				   if("0".equals(this.restLeaveCalcTimeType))//按休息时段时长计
        				   {
        					   this.outTime=restTB1;
        					   this.inTime=restTE1;
        				   }else if("2".equals(this.restLeaveCalcTimeType))//不计
        				   {
        					   this.outTime=null;
        					   this.inTime=null;
        				   }        				   
        				   if(i==1)
        				   {
        					   if(this.isRestLeave1)
        					   {
        						   this.outTime=null;
            					   this.inTime=null; 
        					   }else
        					   {
        						   this.isRestLeave1=true;
        					   }
        				   }
        				   else if(i==2)
        				   {
        					   if(this.isRestLeave2)
        					   {
        						   this.outTime=null;
            					   this.inTime=null; 
        					   }else
        					   {
        						   this.isRestLeave2=true;
        					   }
        				   }
        				   //break;
        			   }else if((time_o_B>=0&&time_i_E<0)||(time_o_B>0&&time_i_E<=0))//2、出入完全包含了休息时段
        			   {
        				   if("2".equals(this.restLeaveCalcTimeType))
        				   {
        					   this.fFactLeaveTime=this.fFactLeaveTime+getPartMinute(this.outTime,restTB1);
        					   this.fFactLeaveTime=this.fFactLeaveTime+getPartMinute(restTE1,this.inTime);
        					   this.outTime=null;
        					   this.inTime=null;
        				   }
        			   }else if(time_o_B>0&&time_i_B>0&&time_i_E<0)//3、出入包含部分休息时段(休息前出，休息中进)
        			   {
        				   if("0".equals(this.restLeaveCalcTimeType))//按休息时段时长计
        				   {
        					   this.inTime=restTE1;
        				   }else if("2".equals(this.restLeaveCalcTimeType))//不计
        				   {
        					   this.inTime=restTB1;
        				   }
        			   }else if(time_o_B<0&&time_o_E>0&&time_i_B<0)//4、出入包含部分休息时段(休息中出，休息后进)
        			   {
        				   if("0".equals(this.restLeaveCalcTimeType))//按休息时段时长计
        				   {
        					   this.outTime=restTB1;
        				   }else if("2".equals(this.restLeaveCalcTimeType))//不计
        				   {
        					   this.outTime=restTE1;
        				   }
        			   }
        			   if(this.outTime!=null&&this.inTime!=null)
        			   {
        				   this.fFactLeaveTime=this.fFactLeaveTime+getPartMinute(this.outTime,this.inTime);
        				   this.outTime=null;
        				   this.inTime=null;
        			   }
        				   
        		   }else
        		   {
        			   break;
        		   }
       		    }
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
	/**
	 * 
	 * @param table_temp
	 * @param nbase
	 * @param a0100
	 * @param strDate
	 * @param empClassBean
	 */
	private void getEmpDatasByDate(String table_temp,String nbase,String a0100,String strDate)
	{
		Date bTime=DateUtils.getDate(strDate+" 00:00","yyyy.MM.dd HH:mm");
		Date eTime=DateUtils.getDate(strDate+" 23:59","yyyy.MM.dd HH:mm");
		Date cur_D=DateUtils.getDate(strDate,"yyyy.MM.dd");
		Date bof_Date=cur_D;
		try
		{
			this.resultSet.previous();//上一行
			if(!this.resultSet.isBeforeFirst())
			{
				String oldNbase=this.resultSet.getString("nbase")!=null&&this.resultSet.getString("nbase").length()>0?this.resultSet.getString("nbase"):"";				
 		    	String oldA0100=this.resultSet.getString("a0100")!=null&&this.resultSet.getString("a0100").length()>0?this.resultSet.getString("a0100"):"";
 		    	String oldStrDate=this.resultSet.getString("q03z0")!=null&&this.resultSet.getString("q03z0").length()>0?this.resultSet.getString("q03z0"):""; 	
 		    	String oldClass=this.resultSet.getString("class_id")!=null&&this.resultSet.getString("class_id").length()>0?this.resultSet.getString("class_id"):"0";
 		    	if(oldStrDate!=null&&oldStrDate.length()>0)
 		    	{
 		    		bof_Date=DateUtils.getDate(oldStrDate,"yyyy.MM.dd");
 		    		float time_f=DateUtils.dayDiff(bof_Date,cur_D);
 		    		if(oldNbase.equalsIgnoreCase(nbase)&&oldA0100.equalsIgnoreCase(a0100)&&Integer.parseInt(oldClass)>0&&time_f>0)
 		    		{
 		    			KqEmpClassBean empClassBean2=new KqEmpClassBean();
 				    	empClassBean2=empClassBean2.getKqEmpClassBean(this.resultSet); 
 				    	String on_duty=empClassBean2.getOnduty();
 				    	String off_duty=empClassBean2.getOffduty();
 				    	String off_duty_end=empClassBean2.getOffduty_end();
 				    	if(on_duty!=null&&on_duty.length()>0&&off_duty!=null&&off_duty.length()>0&&off_duty_end!=null&&off_duty_end.length()>0)
 				    	{
 				    		Date on_duty_T=DateUtils.getDate(on_duty,"HH:mm");
 				    		Date off_duty_T=DateUtils.getDate(off_duty,"HH:mm");
 				    		Date off_duty_end_T=DateUtils.getDate(off_duty_end,"HH:mm");
 				    		float time1=getPartMinute(on_duty_T,off_duty_T);
 				    		float time2=getPartMinute(off_duty_T,off_duty_end_T);
 				    		if((time1>0&&time2<0)||(time1<=0&&time2>=0))
 				    		{
 				    			bTime=DateUtils.getDate(strDate+" "+off_duty_end,"yyyy.MM.dd HH:mm");
 				    		}
 				    	}
 		    		}
 		    	}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try
			{
			   this.resultSet.next();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		getEmpDatas(table_temp,nbase,a0100,strDate,bTime,eTime,true);
	}
	/**
	 * 修改分析出来刷卡数据的状态
	 * @param table_temp
	 * @param up_list
	 * @param aStatus//1：异常；0：正常；-1&null：未分析；
	 */
	private void setCardDataStatus(ArrayList up_list,int aStatus)
	{
		if(up_list==null||up_list.size()<=0) {
            return;
        }
		String a0100=(String)up_list.get(0);
		String nbase=(String)up_list.get(1);
		String work_date=(String)up_list.get(2);
		String work_time=(String)up_list.get(3);
		String sqlwhe="nbase='"+nbase+"' and a0100='"+a0100+"' and work_date='"+work_date+"' and work_time='"+work_time+"'";
		String update="update kq_originality_data set status='"+aStatus+"' where "+sqlwhe;
		try
		{
			//this.dao.batchUpdate(update,up_list);
			this.dao.update(update);
			if(aStatus==1) {
                insertExceptCardData(up_list);
            } else if(aStatus==0) {
                delExceptCardData(up_list);
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 向异常临时表里面添加数据
	 * @param up_list
	 */
	private void  insertExceptCardData(ArrayList up_list)
	{
		if(fExceptCardTab==null||fExceptCardTab.length()<=0) {
            return;
        }
		if(up_list==null||up_list.size()<=0) {
            return;
        }
		StringBuffer sql=null;
		ArrayList list=new ArrayList();
		try
		{
			sql=new StringBuffer();				
			String a0100=(String)up_list.get(0);
			String nbase=(String)up_list.get(1);
			String work_date=(String)up_list.get(2);
			String work_time=(String)up_list.get(3);
			String sqlwhe="nbase='"+nbase+"' and a0100='"+a0100+"' and work_date='"+work_date+"' and work_time='"+work_time+"'";
			sql.append("insert into "+this.fExceptCardTab+"");
			sql.append(" (nbase,A0100,A0101,B0110,E0122,E01A1,card_no,work_date,work_time,location,machine_no)");
			sql.append("  ");
			sql.append(" ");
			sql.append("select nbase,A0100,A0101,B0110,E0122,E01A1,card_no,work_date,work_time,location,machine_no from kq_originality_data");
			sql.append(" where "+sqlwhe);
			sql.append(" and NOT EXISTS(select 1 from "+this.fExceptCardTab+" where "+sqlwhe+")");
		    //System.out.println(sql.toString());
			this.dao.insert(sql.toString(),list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除异常表里面的数据
	 * @param up_list
	 */
	private void delExceptCardData(ArrayList up_list)
	{
		if(fExceptCardTab==null||fExceptCardTab.length()<=0) {
            return;
        }
		if(up_list==null||up_list.size()<=0) {
            return;
        }
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer sql=new StringBuffer();;
			String a0100=(String)up_list.get(0);
			String nbase=(String)up_list.get(1);
			String work_date=(String)up_list.get(2);
			String work_time=(String)up_list.get(3);
			String sqlwhe="nbase='"+nbase+"' and a0100='"+a0100+"' and work_date='"+work_date+"' and work_time='"+work_time+"'";
			sql.append("delete from "+this.fExceptCardTab+" where "+sqlwhe);
			this.dao.delete(sql.toString(),list);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 当天考勤刷卡状态，正常，迟到，矿工．．．．
	 * @param nbase
	 * @param a0100
	 * @param strDate
	 * @param IsOk_value
	 */
	private void updateExceptDetial(String table_tmep,String nbase,String a0100,String strDate,String IsOk_value)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("update "+table_tmep+" set isok='"+IsOk_value+"'");
		sql.append(" where a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+strDate+"'");
		sql.append(" and isok not in('旷工','迟到','早退')");
		try
		{
			this.dao.update(sql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
     * 缺刷卡
     * @param nbase
     * @param a0100
     * @param strDate
     */
	private void markedLackCard(String table_tmep,String nbase,String a0100,String strDate)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("update "+table_tmep+" set LackCard='1'");
		sql.append(" where a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+strDate+"'");		
		try
		{
			this.dao.update(sql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	/**
	 * 清空延时加班数据
	 *
	 */
	private void clearRstTranOverTime()
	{
		this.rstTranOverTime[0]=null;//开始时间
		this.rstTranOverTime[1]=null;//结束时间
	}
    /**
     * 临时异常表
     *
     */
	private void ceartFExceptCardTab(String tempFExceptCardTab)
	{
		DbWizard dbWizard =new DbWizard(this.conn);
		dbWizard.dropTable(tempFExceptCardTab);
		
		Table table=new Table(tempFExceptCardTab);	 
		Field temp = new Field("nbase","人员库");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("a0100","人员编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("a0101","人员姓名");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("b0110","单位编码");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("e0122","部门编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("e01a1","职位编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("card_no","卡号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("work_date","日期");
		temp.setDatatype(DataType.STRING);
		temp.setLength(10);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("work_time","时间");
		temp.setDatatype(DataType.STRING);
		temp.setLength(10);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);	
		temp = new Field("machine_no","机器编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("location","地址");
		temp.setDatatype(DataType.STRING);
		temp.setLength(100);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("ExceptType","异常类型");
		temp.setDatatype(DataType.INT);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("flag","状态");
		temp.setDatatype(DataType.INT);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		try
		 {
			dbWizard.createTable(table);
			this.fExceptCardTab=tempFExceptCardTab;
		 }catch(Exception e)
		 {
			e.printStackTrace();			
		 }	
	}
    //FTranOverTimeTab := 'kqtmp_' + m_rUser.cUserName + '_tranovertime';
	/**
	 * 建立延时加班临时表
	 */
	private void createTranOverTimeTab(String tempFTranOverTimeTab)
	{
		DbWizard dbWizard =new DbWizard(this.conn);
		dbWizard.dropTable(tempFTranOverTimeTab);
		
		Table table=new Table(tempFTranOverTimeTab);	 
		Field temp = new Field("nbase","人员库");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("a0100","人员编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("a0101","人员姓名");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("b0110","单位编码");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("e0122","部门编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("e01a1","职位编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("begin_date","开始时间");
		temp.setDatatype(DataType.DATE);		
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("end_date","结束时间");
		temp.setDatatype(DataType.DATE);		
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("overtime_type","加班类型");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("status","状态");
		temp.setDatatype(DataType.INT);		
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		try
		 {
			dbWizard.createTable(table);
			this.fTranOverTimeTab=tempFTranOverTimeTab;
		 }catch(Exception e)
		 {
			e.printStackTrace();			
		 }
	}
	/**
	 * 业务申请与实际刷卡情况表
	 *
	 */
	private void createCompareBusiWithFactTab(String tempFBusiCompareTab)
	{
		DbWizard dbWizard =new DbWizard(this.conn);
		dbWizard.dropTable(tempFBusiCompareTab);
		
		Table table=new Table(tempFBusiCompareTab);	 
		Field temp = new Field("id","id");
		temp.setDatatype(DataType.INT);		
		temp.setKeyable(false);			
		temp.setVisible(false);
		temp.setNullable(false);
		table.addField(temp);
		temp = new Field("appid","申请id");
		temp.setDatatype(DataType.STRING);		
		temp.setLength(10);
		temp.setKeyable(false);			
		temp.setVisible(false);
		temp.setNullable(false);
		table.addField(temp);
		temp = new Field("supplement","");
		temp.setDatatype(DataType.STRING);		
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		temp.setNullable(false);
		table.addField(temp);
		temp = new Field("nbase","人员库");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("a0100","人员编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("a0101","人员姓名");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("b0110","单位编码");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("e0122","部门编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("e01a1","职位编号");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("q03z0","工作日期");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("busi_begin","业务开始时间");
		temp.setDatatype(DataType.DATETIME);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("busi_end","业务开始时间");
		temp.setDatatype(DataType.DATETIME);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("busi_timelen","业务时长");
		temp.setDatatype(DataType.FLOAT);	
		temp.setDecimalDigits(4);
		temp.setLength(15);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("fact_begin","实际开始时间");
		temp.setDatatype(DataType.DATE);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("fact_end","实际开始时间");
		temp.setDatatype(DataType.DATE);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("fact_timelen","实际时长");
		temp.setDatatype(DataType.FLOAT);	
		temp.setDecimalDigits(4);
		temp.setLength(15);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("busi_type","类型");
		temp.setDatatype(DataType.STRING);	
		temp.setLength(50);
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("status","状态");
		temp.setDatatype(DataType.INT);		
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		temp = new Field("flag","状态");
		temp.setDatatype(DataType.INT);		
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		try
		 {
			dbWizard.createTable(table);
			this.fBusiCompareTab=tempFBusiCompareTab;
		 }catch(Exception e)
		 {
			e.printStackTrace();			
		 }
	}    

	/**
	 * 判断申请类型
	 * @param apptype
	 * @param element
	 * @return
	 */
	private  boolean checkAppType(String apptype,String element) {
		if (element == null) {
            return false;
        }
		if (apptype == null) {
            return false;
        }
		String f_element=element.substring(0,1);
		if(!apptype.equals(f_element)) {
            return false;
        }
		return true;
	}
	/**
	 * 得到所有班次信息
	 * @return
	 */
	private HashMap getAllKqClass()
	{
		String sql="select * from kq_class where class_id<>0";
		HashMap map=new HashMap();
		RowSet rs=null;
		try
		{
			rs=this.dao.search(sql);
			while(rs.next())
			{
				UnKqClassBean classbean=new UnKqClassBean(rs);
				map.put(rs.getString("class_id"),classbean);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return map;
	}
	/**
	 * 看是否同一时间有加班记录
	 * @param nbase
	 * @param a0100
	 * @param TranOverTimeTime
	 * @return
	 */
	private boolean  haveAppRecord(String nbase,String a0100,Date[] TranOverTimeTime)
	{
		boolean isCorrect=false;
		StringBuffer sql=new StringBuffer();
		String start=DateUtils.format(TranOverTimeTime[0],"yyyy.MM.dd HH:mm");
		String end=DateUtils.format(TranOverTimeTime[1],"yyyy.MM.dd HH:mm");
		sql.append("select q1101,q1107 from q11 where nbase='"+nbase+"' and a0100='"+a0100+"'");
		sql.append(" and  ((q11z1>="+Sql_switcher.dateValue(start)+" and q11z3<="+Sql_switcher.dateValue(end)+")");
		sql.append(" or (q11z1<="+Sql_switcher.dateValue(start)+" and q11z3>="+Sql_switcher.dateValue(end)+"))");
		RowSet rs=null;
		try
		{
			rs=this.dao.search(sql.toString());
			if(rs.next())
			{
				isCorrect=true;
				if(rs.getString("q1107")!=null&& "延时加班".equals(rs.getString("q1107")))
				{
					this.fTranOverTimeApps.put(rs.getString("q1107"),rs.getString("q1107"));
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return isCorrect;
	}
	/**
	 * 插入延时加班数据
	 * @param empClassBean
	 * @param TranOverTimeTime
	 */
	private void insertTranOverTimeData(KqEmpClassBean empClassBean,Date[] TranOverTimeTime,Date cur_Date)throws GeneralException
	{
		if(TranOverTimeTime[0]==null||TranOverTimeTime[1]==null) {
            return;
        }
		StringBuffer sql=new StringBuffer();
		/*String start=DateUtils.format(TranOverTimeTime[0],"yyyy.MM.dd HH:mm");
		String end=DateUtils.format(TranOverTimeTime[1],"yyyy.MM.dd HH:mm");*/
		sql.append("insert into "+this.fTranOverTimeTab+" (nbase,A0100,A0101,B0110,E0122,");
		sql.append("E01A1,begin_date,end_date,overtime_type,status) values( ");
		sql.append("?,?,?,?,?,?,?,?,?,?)");
		ArrayList list=new ArrayList();
		list.add(empClassBean.getNbase());
		list.add(empClassBean.getA0100());
		list.add(empClassBean.getA0101());
		list.add(empClassBean.getB0110());
		list.add(empClassBean.getE0122());
		list.add(empClassBean.getE01a1());
		list.add(DateUtils.getSqlDate(TranOverTimeTime[0]));
		list.add(DateUtils.getSqlDate(TranOverTimeTime[1]));
		ValidateAppOper validateAppOper=new ValidateAppOper(this.userView,this.conn);
		if("-1".equals(empClassBean.getOvertime_type()))
		{
			if(validateAppOper.is_Feast(cur_Date))
			{
				list.add("11");
			}else
			{
				list.add("12");
			}
		}else
		{
			list.add(empClassBean.getOvertime_type());
		}
		list.add("0");
		try
		{
			this.dao.insert(sql.toString(),list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 对比分析业务申请与实际刷卡情况
	 * @param fDayDataTmp
	 * @param sTdate
	 * @param eTdate
	 * @param codewhere
	 * @throws GeneralException
	 */
	private void compareBusiWithFactCards(String fDayDataTmp,String sTdate,String eTdate,String codewhere)throws GeneralException
	{
		queryBusiData(fDayDataTmp,sTdate,eTdate,codewhere);//得到业务数据集
		if(this.fBusiData==null) {
            return;
        }
		try
		{ 
			Date busiB;
			Date busiE;
			String nbase="";
			String a0100="";
			String appType="";
			String applyID="";	
			String tranoverApp="";
			String overApply="";
			String next_nbase="";
			String next_a0100="";
			String next_appType="";		
			String supplementId="";
			float busiTimeLen=0;
			float factTimeLen=0;
			Date cardB;
			Date cardE;
			String busiDate_str="";
			Date [] cardDs=new Date[2];
			while(this.fBusiData.next())
			{
				supplementId="";
				factTimeLen=0;
				cardB=null;
				cardE=null;
				busiB=this.fBusiData.getTimestamp("FromTime");
				busiE=this.fBusiData.getTimestamp("ToTime");
				nbase=this.fBusiData.getString("nbase")!=null&&this.fBusiData.getString("nbase").length()>0?this.fBusiData.getString("nbase"):"";
				a0100=this.fBusiData.getString("a0100")!=null&&this.fBusiData.getString("a0100").length()>0?this.fBusiData.getString("a0100"):"";
				appType=this.fBusiData.getString("appType")!=null&&this.fBusiData.getString("appType").length()>0?this.fBusiData.getString("appType"):"";
				applyID=this.fBusiData.getString("applyID")!=null&&this.fBusiData.getString("applyID").length()>0?this.fBusiData.getString("applyID"):"";
				if(checkAppType(kqItem_Overtime,appType))//如果是已确认的延时加班，则不用比对了。
				{
					tranoverApp=(String)this.fTranOverTimeApps.get(applyID);
					if(tranoverApp!=null&&tranoverApp.length()>0) {
                        continue;
                    }
				}
				 //是补申请条已与其它申请合并分析了
				overApply=(String)this.overApplys.get(applyID);
				if(overApply!=null&&overApply.length()>0) {
                    continue;
                }
                //查看下一条是否为补条
				if(!this.fBusiData.last())
				{
					this.fBusiData.next();
					next_nbase=this.fBusiData.getString("nbase")!=null&&this.fBusiData.getString("nbase").length()>0?this.fBusiData.getString("nbase"):"";
					next_a0100=this.fBusiData.getString("a0100")!=null&&this.fBusiData.getString("a0100").length()>0?this.fBusiData.getString("a0100"):"";
					next_appType=this.fBusiData.getString("appType")!=null&&this.fBusiData.getString("appType").length()>0?this.fBusiData.getString("appType"):"";
					if(nbase.equals(next_nbase)&&a0100.equals(next_a0100)&&appType.equals(next_appType)&&getPartMinute(busiE,this.fBusiData.getTimestamp("FromTime"))<3)
					{
						busiE=this.fBusiData.getTimestamp("ToTime");
						supplementId=this.fBusiData.getString("applyID")!=null&&this.fBusiData.getString("applyID").length()>0?this.fBusiData.getString("applyID"):"";
						this.overApplys.put(supplementId,supplementId);
						
					}else//不是补条，则跳回当前正在处理的申请
                    {
                        this.fBusiData.previous();
                    }
				}
				busiTimeLen=getHourSpan(busiB,busiE);
                //长申请时间(非加班)
				if(busiTimeLen>=8&&(!"1".equals(appType.substring(0,1))))
				{
                     //取申请开始＋半小时～申请结束 之间的刷卡记录					
					 Date FTD=busiB;
					 FTD.setTime(FTD.getTime()+(30*60*1000L));
					 getEmpDatas("",nbase,a0100,"",FTD,busiE,false); 
                     //匹配刷卡记录
					 cardDs=checkNoCardForBusi(busiB,busiE,this.rs_FEmpDatas,cardB,cardE);
					 cardB=cardDs[0];
					 cardE=cardDs[1];
				}else
				{
                    //取 申请开始＋提前刷卡～申请结束＋延后刷卡 之间的刷卡记录
					Date FTD=busiB;		
					Date TTD=busiE;
					FTD.setTime(FTD.getTime()-(this.kqParam.getBusi_cardbegin()*60*1000L));
					TTD.setTime(TTD.getTime()+(this.kqParam.getBusi_cardend()*60*1000L));
					getEmpDatas("",nbase,a0100,"",FTD,TTD,false); 
					cardDs=checkCardDataForBusi(busiB,busiE,this.rs_FEmpDatas, "1".equals(appType.substring(0,1)),cardB,cardE);
					cardB=cardDs[0];
					cardE=cardDs[1];
				}
                //如果是平时加班且申请起始刷卡未找到，那么检测是否申请的是延时加班，
		        //如是延时加班，则用下班时间作为起始刷卡
				if(checkAppType(kqItem_Overtime,appType)&&(cardB==null||getPartMinute(cardB,busiB)>0&&(cardE!=null&&busiTimeLen<8)))
				{
					KqEmpClassBean empBean=getOneFEmpQry(fDayDataTmp,nbase,a0100,DateUtils.format(busiB,"yyyy.MM.dd"));
					if(empBean!=null&&empBean.getClass_id()!=null&&empBean.getClass_id().length()>0)
					{
						busiDate_str=DateUtils.format(busiB,"yyyy.MM.dd");
						if(empBean.getClass_id()!=null&&empBean.getClass_id().length()>0&&empBean.getOffduty()!=null&&empBean.getOffduty().length()>0&&empBean.getOnduty()!=null&&empBean.getOnduty().length()>0)
						{
							Date TTD=DateUtils.getDate(busiDate_str+" "+empBean.getOffduty(),"yyyy.MM.dd HH:mm");
							if(cardB==null||getPartMinute(cardB,TTD)>0)
							{
								Date TTD1=TTD;
								TTD1.setTime(TTD.getTime()-(30*60*1000L));
								Date TTD2=TTD;								
								TTD2.setTime(TTD.getTime()+(30*60*1000L));
								if(getPartMinute(TTD1,busiB)>=0&&getPartMinute(TTD2,busiB)<=0)
								{
									cardB=TTD;
								}
							}
						}
					}
				}
                //按找到的刷卡时间来判断申请与实际是否相符
				if(cardB !=null&&cardE!=null)
				{
					factTimeLen = getHourSpan(cardB,cardE);
					if(((factTimeLen >busiTimeLen)&&((factTimeLen - busiTimeLen)*60>this.kqParam.getBusifact_diff()))||((factTimeLen < busiTimeLen)&&((factTimeLen - busiTimeLen)*60>this.kqParam.getBusi_morethan_fact())))
					{
                      //刷卡时长远大于申请时长||申请时长远大于刷卡时长
						combineAndExecuteSQL(this.rs_FEmpDatas,busiTimeLen,busiB,busiE,cardB,cardE,factTimeLen,supplementId);
					}				    
				}
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private Date [] checkCardDataForBusi(Date busiB,Date busiE,RowSet cardData,boolean isOverTime,Date cardB, Date cardE)
	{
		Date [] cardDs=new Date[2];
		if(cardData==null) {
            return cardDs;
        }
		float timeLenB = -1;
		float timeLenE = -1;
        boolean findB = false;
		boolean findE = false;
		String work_date="";
		String work_time="";
		int rowCount=0;
		Date preTime=null;
		Date cardTime;
		try
		{
			cardData.last();
			rowCount=cardData.getRow();
			cardData.first();
			if(rowCount==2) //有两次刷卡，则认为是加班的开始与结束
			{
				work_date=cardData.getString("work_date");
				work_time=cardData.getString("work_time");
				cardB=DateUtils.getDate(work_date+" "+work_time,"yyyy.MM.dd HH:ss");
			}else if(rowCount==1)//有一次刷卡
			{
				work_date=cardData.getString("work_date");
				work_time=cardData.getString("work_time");
				cardTime=DateUtils.getDate(work_date+" "+work_time,"yyyy.MM.dd HH:ss");
				if(getPartMinute(cardTime,busiB)>=0)//在申请开始时间前
                {
                    cardB=cardTime;
                } else if(getPartMinute(cardTime,busiE)<=0)//在申请结束时间后
                {
                    cardE=cardTime;
                } else //在申请时段内
				{
					timeLenB = getHourSpan(busiB,cardTime);
		            timeLenE = getHourSpan(cardTime,busiE);
		            if (timeLenB <= timeLenE) {
                        cardB = cardTime;
                    } else {
                        cardE = cardTime;
                    }
				}
			}else //有多次刷卡，查找与申请起止时间最接近的
			{
				while(cardData.next())
				{
					if(findB&&findE) {
                        break;
                    }
					work_date=cardData.getString("work_date");
					work_time=cardData.getString("work_time");
					cardTime=DateUtils.getDate(work_date+" "+work_time,"yyyy.MM.dd HH:ss");
					if(!findB)//起始时间还未找到
					{
					   if(Math.abs(getPartMinute(cardTime,busiB))<0.01)//绝对时间相差较小
					   {
						   cardB = cardTime;
			               findB = true;
			               timeLenB = -1;
					   }else if(getPartMinute(cardTime,busiB)<0)//刷卡迟于申请
					   {
						   if(timeLenB==-1)
						   {
							   cardB=null;
							   findB = true;
							   cardData.previous();
							   continue;//数据集保持在当前，下一循环开始寻找结束刷卡时间
						   }else
						   {
							   cardB=cardTime;
							   findB = true;
							   timeLenB = -1;
							   preTime =null;
							   cardData.previous();
							   continue;//数据集保持在当前，下一循环开始寻找结束刷卡时间
						   }
					   }else
					   {
						   timeLenB = getHourSpan(cardTime,busiB);  //记录下本次刷卡情况，下一循环将与之比较
			               preTime = cardTime;
					   }
					}else//结束时间还未找到
					{
						if(Math.abs(getPartMinute(cardTime,busiE))<0.01)//绝对时间相差较小
						{
							cardE = cardTime;
			                break;
						}else if(getPartMinute(cardTime,busiB)<0)//刷卡晚于申请结束时间
						{
							cardE = cardTime;
			                findE = true;
			                break;
						}else//刷卡早于申请结束时间
						{
							if(timeLenB==-1)//第一次寻找结束时间，记录下本次刷卡情况，下一循环将与之比较
							{
								timeLenB = getHourSpan(cardTime,busiE);
			                    preTime = cardTime;
			                    cardE = cardTime;
							}else//与上一次刷卡数据比较
							{
								timeLenE = getHourSpan(cardTime,busiE);
								 if (timeLenB < timeLenE ) //上一次更接近申请结束时间
								 {
									 cardE = preTime;
				                     findE = true;
				                     break;
								 }else//本次更接近，则继续寻找
								 {
									 timeLenB = timeLenE;
				                     preTime = cardTime;
				                     cardE = cardTime;
								 }  
							}
						}
					 }
				 }
			}
			cardDs[0]=cardB;
			cardDs[1]=cardE;
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return cardDs;
	}
	private Date [] checkNoCardForBusi(Date busiB,Date busiE,RowSet cardData,Date cardB, Date cardE)
	{
		cardB=busiB;
		cardE=busiE;
		Date [] cardDs=new Date[2];
		try
		{
			if(cardData.next())
			{
				cardDs[0]=busiB;
				String work_date=cardData.getString("work_date");
				String work_time=cardData.getString("work_time");
				cardDs[1]=DateUtils.getDate(work_date+" "+work_time,"yyyy.MM.dd HH:ss");
			}else
			{
				cardDs[0]=busiB;
				cardDs[1]=busiE;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return cardDs;
	}
	/**
	 * //记录到分析表中
	 * @param cardData
	 * @param busiTimeLen
	 * @param busiB
	 * @param busiE
	 */
	private void combineAndExecuteSQL(RowSet cardData,float busiTimeLen,Date busiB,Date busiE,Date cardB,Date cardE,float factTimeLen,String supplementId)//记录分析表中
	{
		StringBuffer sql=new StringBuffer();
		sql.append("INSERT INTO ' "+ this.fBusiCompareTab + "'(ID,nbase,A0100,B0110,E0122,E01A1,");
		sql.append("A0101,Q03Z0,busi_begin,busi_end,busi_timelen,fact_begin,fact_end,");
        sql.append("fact_timelen,busi_type,status,flag,appid,supplement)");
        sql.append(" values ");
        sql.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        try
        {
        	ArrayList list=new ArrayList();
            list.add(getNewID(this.fBusiCompareTab));
            list.add(cardData.getString("nbase"));
            list.add(cardData.getString("a0100"));
            list.add(cardData.getString("B0110"));
            list.add(cardData.getString("E0122"));
            list.add(cardData.getString("E01A1"));
            list.add(cardData.getString("A0101"));
            list.add(DateUtils.format(busiB,"yyyy.MM.dd"));
            list.add(DateUtils.getSqlDate(busiB));
            list.add(DateUtils.getSqlDate(busiE));
            list.add(new Float(busiTimeLen));
            if(cardB!=null) {
                list.add(DateUtils.getSqlDate(cardB));
            } else {
                list.add(null);
            }
            if(cardE!=null) {
                list.add(DateUtils.getSqlDate(cardE));
            } else {
                list.add(null);
            }
            list.add(new Float(factTimeLen));
            list.add(cardData.getString("applytype"));
            list.add(new Integer(0));
            list.add(new Integer(0));
            list.add(cardData.getString("applyid"));
            list.add(supplementId);
            this.dao.insert(sql.toString(),list);
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        

	}
	private String getNewID(String table)
	{
		String id="";
		RowSet rs=null;
		try
		{
			String sql="select Max(id) as id from "+table;
			rs=this.dao.search(sql);
			if(rs.next())
			{
				id=rs.getString("id");
				if(id!=null&&id.length()>0)
				{
					id=(Integer.parseInt(id)+1)+"";
				}else
				{
					id="1";
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return id;
	}
	/**
	 * 申请数据的数据集
	 * @param fDayDataTmp
	 * @param sTdate
	 * @param eTdate
	 * @param codewhere
	 */
	private void queryBusiData(String fDayDataTmp,String sTdate,String eTdate,String codewhere)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select * from ");
		sql.append("("+getBusiSQL("q11",fDayDataTmp,sTdate,eTdate,"",codewhere)+" union ");
		sql.append(getBusiSQL("q13",fDayDataTmp,sTdate,eTdate,"",codewhere)+" union ");
		sql.append(getBusiSQL("q13",fDayDataTmp,sTdate,eTdate,"",codewhere)+" )AAA ");
		sql.append(" WHERE EXISTS(SELECT 1 FROM " + fDayDataTmp + " WHERE nbase=AAA.nbase AND A0100=AAA.A0100)");
        sql.append(" ORDER BY nbase,A0100,FromTime");
        StringBuffer lb_sql=new StringBuffer();
        lb_sql.append(getBusiSQL("q15",fDayDataTmp,sTdate,eTdate,"q1519",codewhere));
        try
        {
        	this.fBusiData=this.dao.search(sql.toString());
        	this.fLeaveBackData=this.dao.search(lb_sql.toString());
        }catch(Exception e)
        {
        	e.printStackTrace();
        }        
	}
	/**
	 * 组成申请数据的sql
	 * @param tab
	 * @param fDayDataTmp
	 * @param sTdate
	 * @param eTdate
	 * @param otherField
	 * @param codewhere
	 * @return
	 */
	private String getBusiSQL(String tab,String fDayDataTmp,String sTdate,String eTdate,String otherField,String codewhere)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select "+tab+"01 as applyID ,"+tab+"03 as applyType,"+tab+"z1 FromTime,"+tab+"z3 as ToTime,");
		sql.append("nbase,A0100,B0110,E0122,E01A1,A0101");
		if(otherField!=null&&otherField.length()>0)
		{
			sql.append(","+otherField);
		}
		sql.append(" from "+tab+" where");
		sql.append(" "+tab+"z0='01' and "+tab+"z5='03' ");
		if(codewhere!=null&&codewhere.length()>0)
		 {
				sql.append(" and "+codewhere);
		 }
	     sql.append(kq_app_dateSQL(tab,sTdate,eTdate));
	     if("q15".equalsIgnoreCase(tab)&&!"q1519".equalsIgnoreCase(otherField)) {
             sql.append( " and "+Sql_switcher.isnull("q1517","0")+"=0");
         } else if("q15".equalsIgnoreCase(tab)&& "q1519".equalsIgnoreCase(otherField))
	     {
	    	 sql.append( " and "+Sql_switcher.isnull("q1517","0")+"=1");
	     }
	     return sql.toString();
	    	 
	}
	/**
	 * 得到指定数据，的数据集
	 * @param analyse
	 * @param nbase
	 * @param a0100
	 * @param q03z0
	 * @return
	 */
	private KqEmpClassBean getOneFEmpQry(String analyse,String nbase,String a0100,String q03z0)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select * from "+analyse);
		sql.append(" where nbase='"+nbase+"' and a0100='"+a0100+"' and q03z0='"+q03z0+"'");
		KqEmpClassBean bean=null;
		RowSet rs=null;
		try
		{
			rs=this.dao.search(sql.toString());	
			if(rs.next())
			{
				bean=new KqEmpClassBean();
				bean.getKqEmpClassBean(rs);
			}		
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return bean;
	}
	/***********给外界返回临时表明***********/
	public void initTempTable()throws GeneralException
	{
		DbWizard dbWizard =new DbWizard(this.conn);
		//数据处理表
		if(!dbWizard.isExistTable(this.fAnalyseTempTab,false))
		{
			createDataAnalyseTmp(this.fAnalyseTempTab);
		}
		if(!dbWizard.isExistTable(this.fExceptCardTab,false))//临时异常表的名称
        {
            ceartFExceptCardTab(this.fExceptCardTab);//异常数据表
        }
		if(!dbWizard.isExistTable(this.fTranOverTimeTab,false))//临时延时加班表
        {
            createTranOverTimeTab(this.fTranOverTimeTab);//延时加班表
        }
		if(!dbWizard.isExistTable(this.fBusiCompareTab,false))////申请比对表
        {
            createCompareBusiWithFactTab(this.fBusiCompareTab);//业务申请与实际刷卡情况表
        }
	}
	public String getFBusiCompareTab() {
		return fBusiCompareTab;
	}
	public void setFBusiCompareTab(String busiCompareTab) {
		fBusiCompareTab = busiCompareTab;
	}
	public String getFTranOverTimeTab() {
		return fTranOverTimeTab;
	}
	public void setFTranOverTimeTab(String tranOverTimeTab) {
		fTranOverTimeTab = tranOverTimeTab;
	}
	public String getFExceptCardTab() {
		return fExceptCardTab;
	}
	public void setFExceptCardTab(String exceptCardTab) {
		fExceptCardTab = exceptCardTab;
	}
	public String getFAnalyseTempTab() {
		return fAnalyseTempTab;
	}
	public void setFAnalyseTempTab(String analyseTempTab) {
		fAnalyseTempTab = analyseTempTab;
	}
	public String getNo_tranData() {
		return no_tranData;
	}
	public void setNo_tranData(String no_tranData) {
		this.no_tranData = no_tranData;
	}
	
}

