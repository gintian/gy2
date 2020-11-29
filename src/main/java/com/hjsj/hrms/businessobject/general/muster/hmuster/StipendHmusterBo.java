package com.hjsj.hrms.businessobject.general.muster.hmuster;

import com.hjsj.hrms.businessobject.common.CreateTable;
import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.gz.*;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class StipendHmusterBo {
	private Connection conn = null;
	private UserView userView=null;
	private ArrayList fieldsList=new ArrayList();
	private float lt = 0; // 表格整体往左靠，需减的像素
	private String musterName="";
	private String salaryid="";
	/** 工资发放/审批数据表 */
	private String salaryDataTable = "";
	/** 包括salaryDataTable表和dbname表条件 */
	private StringBuffer salaryDataTableCond = new StringBuffer("");
	
	private String isGroupPoint="0";  //是否分组显示
	private String groupPoint="";     //分组指标
	private String isGroupPoint2="0";
	private String groupPoint2="";
	private String layerid2="";
	private String rix=""; //排序指标
	private String manageUserName=""; //共享管理员用户名
	private String layerid="";
	private String filterWhl="";
	private String sortitem="";
	private String model="";//=0工资发放，=1工资审批=3薪资历史数据
	private String checkdata=""; //审批日期
	private String checknum=""; //审批次数
	private String temptable=""; //临时表
	private String salaryorder=""; //薪资顺序
	private String privSQL="";
	private String moduleSQL="";
	private String combineField;//是否汇总
	/**所得税页面的查询语句*/
	private String conSQL="";
	private String groupCount;//我的薪酬中，是否分组合计=1分组合计，=0不分组合计
	private String taxTable="gz_tax_mx";
	//31320	中交上海航道局-自助我的薪酬查看功能优化 start
	private String strSql="";//设置cs花名册子集记录
	private String strWhere="";//设置cs花名册条件查询
	
	/**
	 * 我的薪酬 高级花名册设置条件查询
	 * **/
	public String getStrWhere() {
		return strWhere;
	}
	public void setStrWhere(String strWhere) {
		this.strWhere = strWhere;
	}
	/**
	 * 我的薪酬 高级花名册设置子集记录 
	 * **/
	public String getStrSql() {
		return strSql;
	}
	public void setStrSql(String strSql) {
		this.strSql = strSql;
	}
	//31320	中交上海航道局-自助我的薪酬查看功能优化 end
	public StipendHmusterBo(){}
	public StipendHmusterBo(Connection con)
	{
		this.conn=con;
	}
	public StipendHmusterBo(Connection con,UserView userView)
	{
		this.conn=con;
		this.userView=userView;
	}
	public StipendHmusterBo(Connection con,UserView userView,String salaryid)
	{
		this.conn=con;
		this.userView=userView;
		this.salaryid=salaryid;
	}
	
	public void setSelfMusterName(String tabid)
	{
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search("select cname from muster_name where tabid="+tabid);
			if(rowSet.next())
			{
				this.musterName=rowSet.getString("cname");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * 根据条件从档案库中导入数据到高级花名册的临时表中
	 * 
	 * @param tabid
	 *            高级花名册的id
	 * @param infor_Flag
	 *            信息群标识
	 * @param dbpre
	 *            应用库表前缀
	 * @param queryScope
	 *            查询的范围
	 * @param flag
	 *            "0":无 "1"有子集指标无年月标识,可按最后一条历史纪录查 "2"有子集指标无年月标识,可按取部分历史纪录查
	 *            "3"有子集指标和年月标识，可按某次的历史纪录查
	 * @param year
	 *            年;month 月;count 次
	 * @param fromScope
	 * @param toScope
	 * @param selectedPoint
	 * @param isGroupPoint
	 *            是否选用分组指标 1:选用
	 * @param groupPoint;
	 *            已选的分组指标
	 * @return void
	 * @author dengc created: 2003/03/22
	 * 
	 */

	public String importData(String a0100,String tabid,String dbpre,String operate
			 ,String year, String month,String startDate,String endDate,String quarter,UserView userView)
			throws GeneralException {
		this.userView=userView;
		String isSuccess ="1";                                   //成功
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/** 创建临时表 */
			DbWizard dbWizard = new DbWizard(this.conn);
			MusterBo mbo = new MusterBo(this.conn,this.userView);
			HmusterBo hmb=new HmusterBo(this.conn,this.userView);
			HashMap cFactorMap = hmb.getCfactor(tabid);
			
			Table temp_table= createMusterTempTable(tabid,dbWizard,3,cFactorMap,mbo);
		                                   //表中有指标没构库                                       
			String sql=createSQL2(tabid,dbpre,operate,year,month,quarter,startDate,endDate,a0100);	
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName().trim().replaceAll(" ", "")+"_muster_"+tabid;
            }
			DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tablename);
			dao.delete("delete from "+tablename+" where a0100='"+a0100+"'",new ArrayList());
			dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表
			//updateMusterRecidx("muster_"+tabid);
			//dbWizard.addPrimaryKey(temp_table);		
			this.transformMidvariable(dbpre, tabid, tablename);
			runCountFormula(tabid,dbpre,userView,a0100);
			
			transformCode(tabid,tablename, "1"); // 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}

		return isSuccess;

	}
	
	
	public boolean isSequence(int dbflag)
	{
		boolean flag=false;
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(dbflag==Constant.ORACEL){
				rowSet=dao.search("select   sequence_name   from   user_sequences   where lower(sequence_name)='xxx'");
				if(rowSet.next()) {
                    flag=true;
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	/**
	 * 更新花名册的排序指标序号
	 * @param name
	 */
	public void updateMusterRecidxC(String name)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		int dbflag=Sql_switcher.searchDbServer();
		try
		{
			DbWizard db=new DbWizard(this.conn);
			switch(dbflag)
			{
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" add xxx int identity(1,1)");
				break;
			default:		
				 if(isSequence(dbflag))
				 {
				    	db.execute("drop sequence xxx");	
				 }
				strsql.append("create sequence xxx increment by 1 start with 1");
				break;
			}
			
			db.execute(strsql.toString());
			strsql.setLength(0);
			switch(dbflag)
			{
			case Constant.MSSQL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			case Constant.DB2:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=nextval for xxx");			
				break;
			case Constant.ORACEL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx.nextval");					
				break;
			default:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			}	
			db.execute(strsql.toString());	
			strsql.setLength(0);			
			switch(dbflag)
			{
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" drop column xxx");
				break;
			default:
				strsql.append(" drop sequence xxx");
				break;
			}		
			db.execute(strsql.toString());	
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);			
		}
	
	}
	
	synchronized static private void createMusterRecidx(Connection dbconn,String name) throws GeneralException {
		try{
			StringBuffer strsql = new StringBuffer();
			int dbflag = Sql_switcher.searchDbServer();
			DbWizard db = new DbWizard(dbconn);
			HmusterBo hmusterBo=new HmusterBo(dbconn);
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" add xxx int identity(1,1)");
				break;
			default:
				if (hmusterBo.isSequence(dbflag)) {
					db.execute("drop sequence xxx");
				}
				strsql.append("create sequence xxx increment by 1 start with 1");
				break;
			}
			db.execute(strsql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新花名册的排序指标序号
	 * 
	 * @param name
	 */
	synchronized static public void updateMusterRecidxS(Connection dbconn,String name) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		int dbflag = Sql_switcher.searchDbServer();
		StipendHmusterBo shmusterBo=new StipendHmusterBo(dbconn);
		DbWizard dbw=new DbWizard(dbconn);
		try {
			DbWizard db = new DbWizard(dbconn);
			switch (dbflag) {
			case Constant.MSSQL:
				if(!dbw.isExistField(name, "xxx", false)){
					strsql.append("alter table ");
					strsql.append(name);
					strsql.append(" add xxx int identity(1,1)");
				}
				break;
			default:
				if (shmusterBo.isSequence(dbflag)) {
					db.execute("drop sequence xxx");
				}
				strsql
						.append("create sequence xxx increment by 1 start with 1");
				break;
			}

			if(strsql.length()>0) {
                db.execute(strsql.toString());
            }
			
			strsql.setLength(0);
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			case Constant.DB2:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=nextval for xxx");
				break;
			case Constant.ORACEL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx.nextval");
				break;
			default:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			}
			db.execute(strsql.toString());
			strsql.setLength(0);
			switch (dbflag) {
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" drop column xxx");
				break;
			default:
				strsql.append(" drop sequence xxx");
				break;
			}
			db.execute(strsql.toString());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	
	
	/**
	 * 根据条件从薪资表中导入数据到高级花名册的临时表中
	 * @param tabid
	 * @param salaryid
	 * @param a_code
	 * @param dateWhere
	 * @param num
	 * @param preWhere
	 * @param sumFlag
	 * @return
	 * @throws GeneralException
	 */
	public String importData(String tabid,String a_code,String dateWhere,
			String preWhere,boolean sumFlag)
			throws GeneralException {
		String isSuccess ="1";                                   //成功
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/**创建临时表*/
			DbWizard dbWizard = new DbWizard(this.conn);
			MusterBo mbo = new MusterBo(this.conn,this.userView);
			HmusterBo hmb=new HmusterBo(this.conn,this.userView);
			HashMap cFactorMap = hmb.getCfactor(tabid);
			createMusterTempTable(tabid,dbWizard,15,cFactorMap,mbo);
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tablename);
		    //表中有指标没构库                                       
			String sql=createGzSQL(tabid,a_code,cFactorMap,dateWhere,preWhere,sumFlag);		
			dao.delete("delete from "+tablename,new ArrayList());
			StipendHmusterBo.createMusterRecidx(this.conn,tablename);
			dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表	
			StipendHmusterBo.updateMusterRecidxS(this.conn,tablename);
			//this.transformMidvariable(dbpre, tabid, tablename);
//			ArrayList dbList=getDbList(salaryid);
//			runCountFormula2(tabid,dbList,userView,salaryid);
			transformCode(tabid,tablename, "1"); // 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}

		return isSuccess;

	}
	/**
	 * 根据条件从薪资发放中导入数据到高级花名册临时表
	 * @param tabid
	 * @param salaryid
	 * @param acode
	 * @param userView
	 * @return
	 * @throws GeneralException
	 */
	public String importData(String tabid,String salaryid,String a_code,String condid,int nmodel)
			throws GeneralException {
		String isSuccess ="1";                                   //成功
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/**创建临时表*/
			DbWizard dbWizard = new DbWizard(this.conn);
			MusterBo mbo = new MusterBo(this.conn,this.userView);
			HmusterBo hmb=new HmusterBo(this.conn,this.userView);
			HashMap cFactorMap = hmb.getCfactor(tabid);
			createMusterTempTable(tabid,dbWizard,nmodel,cFactorMap,mbo);
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tablename);
		    //表中有指标没构库                                       
			String sql=createSQL(tabid,salaryid,a_code,condid,cFactorMap);
			dao.delete("delete from "+tablename,new ArrayList());
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                StipendHmusterBo.createMusterRecidx(this.conn,tablename);  // 需要先建排序字段，否则mssql2012会出现顺序不正确
            }
			dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表	
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                StipendHmusterBo.updateMusterRecidxS(this.conn,tablename);
            }
			ArrayList dbList=getDbList(tablename);
			if(dbList==null||dbList.size()==0) {
                return isSuccess;
            }
			runCountFormula2(tabid,dbList,userView,salaryid);
			transformCode(tabid,tablename, "1"); // 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据
			rexToA0000(dao,tablename);
			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				layerOrg(tablename,groupPoint,1);
			}
			if (isGroupPoint2 != null && isGroupPoint2.trim().length() > 0&& "1".equals(isGroupPoint2)) // 选用分组指标
			{
				layerOrg(tablename,groupPoint2,2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}

		return isSuccess;

	}
	
	private void rexToA0000(ContentDAO dao,String tablename){
		if(getRix()!=null&&getRix().trim().length()>0){
			try {
				dao.update("update "+tablename+" set "+getRix()+"=recidx");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 根据条件从审批表中导入数据到高级花名册的临时表中
	 * 所得税管理
	 * @param tabid
	 * @param acode
	 * @param date
	 * @param dbpre
	 * @return
	 * @throws GeneralException
	 */
	public String importSpData(String tabid,String a_code,String date,String dbpre)
			throws GeneralException {
		String isSuccess ="1";                                   //成功
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			MusterBo mbo = new MusterBo(this.conn,this.userView);
			HmusterBo hmb=new HmusterBo(this.conn,this.userView);
			HashMap cFactorMap = hmb.getCfactor(tabid);
			//liuy 2015-1-29 6990：所得税管理：高级花名册取数，报表或视图不存在 start
			TaxMxBo taxbo=new TaxMxBo(this.conn,this.userView);
			//liuy 2015-7-7 10838：高级花名册：非su用户登录，个人所得税高级花名册，前台取不出数据来 begin
			String filterByMdule = "0";
			if(taxbo.hasModulePriv()) {
                filterByMdule="1";
            }
			//liuy 2015-7-7 end
			this.setModuleSQL(taxbo.getPrivPre(filterByMdule));
			//liuy 2015-1-29 end
			/**不汇总*/
			if(this.getCombineField()==null|| "".equals(this.getCombineField())|| "0".equals(this.getCombineField()))
			{
		    	/**取临时变量信息*/
	    		//this.getMidvariable(tabid);
	    		/** 创建临时表 */
    			DbWizard dbWizard = new DbWizard(this.conn);
	    		createMusterTempTable(tabid,dbWizard,15,cFactorMap,mbo);
	    		String tablename = "";
	    		if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                    tablename = getTemptable();
                } else {
                    tablename = userView.getUserName()+"_muster_"+tabid;
                }
	    		DbSecurityImpl dsi = new DbSecurityImpl();
				dsi.encryptTableName(conn, tablename);
		       //表中有指标没构库                                       
    			String sql=createSpSQL(tabid,a_code,date,dbpre);		
	    		dao.delete("delete from "+tablename,new ArrayList());
				if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                    StipendHmusterBo.createMusterRecidx(this.conn,tablename);
                }
	    		dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表	
    			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
    	        {
    		    	StipendHmusterBo.updateMusterRecidxS(this.conn,tablename);
    	        }
	    		//TaxMxBo taxbo=new TaxMxBo(this.conn);
	    		runCountFormula3(tabid,cloneFielditem(taxbo.getFieldlist()),dbpre,this.getTaxTable());
		    	if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
	    		{
	    			layerOrg(tablename,groupPoint,1);
	    		}
		    	if (isGroupPoint2 != null && isGroupPoint2.trim().length() > 0&& "1".equals(isGroupPoint2)) // 选用分组指标
	    		{
	    			layerOrg(tablename,groupPoint2,2);
	    		}
	     		/**计算临时变量的值，加到高级花名册临时表中*/
	    		this.transformMidvariable(dbpre, tabid, tablename);
	    		transformCode(tabid,tablename, "1"); // 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据
			}
			else
			{
				/**取临时变量信息*/
	    		/** 创建临时表 */
    			DbWizard dbWizard = new DbWizard(this.conn);
    			this.createMusterTempCombineTable(tabid, dbWizard, 15,cFactorMap,mbo);
	    		createMusterTempTable(tabid,dbWizard,15,cFactorMap,mbo);
	    		String tablename = "T#"+this.userView.getUserName()+"_mus";                           
    			String sql=this.createCombineSQL(tabid, a_code, date, dbpre);//createSpSQL(tabid,a_code,date,dbpre);		
	    		dao.delete("delete from "+userView.getUserName()+"_muster_"+tabid,new ArrayList());
				if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                    StipendHmusterBo.createMusterRecidx(this.conn,tablename);
                }
	    		dao.insert(sql, new ArrayList()); // 将不用计算的数据全部插入临时表	
    			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
    	        {
    		    	StipendHmusterBo.updateMusterRecidxS(this.conn,tablename);
    	        }
	    		//TaxMxBo taxbo=new TaxMxBo(this.conn);
	    		this.runCountFormulaCombine(tabid, cloneFielditem(taxbo.getFieldlist()), dbpre, this.getTaxTable());
	    		//runCountFormula3(tabid,cloneFielditem(taxbo.getFieldlist()),dbpre,"gz_tax_mx");
		    	
		    	if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
	    		{
	    			layerOrg(tablename,groupPoint,1);
	    		}
		    	if (isGroupPoint2 != null && isGroupPoint2.trim().length() > 0&& "1".equals(isGroupPoint2)) // 选用分组指标
	    		{
	    			layerOrg(tablename,groupPoint2,1);
	    		}
	     		/**计算临时变量的值，加到高级花名册临时表中*/
	    		this.transformMidvariable(dbpre, tabid, tablename);
	    		transformCode(tabid,tablename, "1"); // 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据
	    		/**将临时表数据导入花名册临时表*/
	    		this.importDataCombine(tabid, 15);
	    		//dbWizard.dropTable(tablename);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}

		return isSuccess;

	}
	public void importDataCombine(String tabid,int nModule)
	{
		RowSet rset = null;
		try
		{
            String  tableName="T#"+this.userView.getUserName()+"_mus";
			String tablename = this.userView.getUserName()+"_muster_"+tabid;
			StringBuffer strsql=new StringBuffer("");
			strsql.append("select GridNo,SetName,Field_Name,Field_Type,Slope,Flag,Field_Hz from muster_cell where flag!='G' and flag!='R' and flag!='H' and flag!='S' and flag!='P'  and tabid=");
			strsql.append(tabid);
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(strsql.toString());
			StringBuffer sql_insert=new StringBuffer(" insert into "+tablename+" (recidx,salaryid,tax_max_id,A0000,A0100,B0110,E0122,NBASE,A00Z0,A00Z1,DECLARE_TAX");
			StringBuffer sql_select = new StringBuffer(" select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append("1,");
            }
			sql_select.append("salaryid,tax_max_id,A0000,A0100,B0110,E0122,NBASE,A00Z0,A00Z1,DECLARE_TAX");
			StringBuffer sb = new StringBuffer();
			StringBuffer ex = new StringBuffer("");
			ex.append(" select max(recidx) as recidx,max(salaryid) as salaryid ,max(tax_max_id) as tax_max_id,max(A0000) as a0000,A0100,max(B0110) as b0110,max(E0122) as e0122" +
			",max(NBASE) as NBASE,max(A00Z0) as A00Z0,max(A00Z1) as a00z1,taxmode,max(DECLARE_TAX) as DECLARE_TAX");
			sb.append(" select max(recidx) as recidx,max(salaryid) as salaryid ,max(tax_max_id) as tax_max_id,max(A0000) as a0000,A0100,max(B0110) as b0110,max(E0122) as e0122" +
					",max(NBASE) as NBASE,max(A00Z0) as A00Z0,max(A00Z1) as a00z1,deptid,taxmode,max(DECLARE_TAX) as DECLARE_TAX");
			if (isGroupPoint != null&&isGroupPoint.trim().length()>0 && "1".equals(isGroupPoint)){
				sql_insert.append(",GroupN,GroupV");
				sql_select.append(",GroupN,GroupV");
				sb.append(",MAX(GROUPN) as GROUPN,MAX(GROUPV) as GROUPV");
				ex.append(",MAX(GROUPN) as GROUPN,MAX(GROUPV) as GROUPV");
			}
			if (isGroupPoint2 != null&&isGroupPoint2.trim().length()>0 && "1".equals(isGroupPoint2)){
				sql_insert.append(",GroupN2,GroupV2");
				sql_select.append(",GroupN2,GroupV2");
				sb.append(",MAX(GROUPN2) as GROUPN2,MAX(GROUPV2) as GROUPV2");
				ex.append(",MAX(GROUPN2) as GROUPN2,MAX(GROUPV2) as GROUPV2");
			}
			while(rset.next())
			{
				String fieldname = rset.getString("Field_Name")!=null?rset.getString("Field_Name"):"";
				String aSetName = rset.getString("SetName")!=null?rset.getString("SetName"):"";
				String type = rset.getString("Field_Type");
				String gridNo = rset.getString("GridNo");
				String flags = rset.getString("Flag");
				if((fieldname!=null&&fieldname.trim().length()>0&&!"V".equalsIgnoreCase(flags))|| "C".equalsIgnoreCase(flags))
				{
				    sql_insert.append(",C"+gridNo);
				    sql_select.append(",C"+gridNo);
				
			    	if("sl".equalsIgnoreCase(fieldname)|| "Sskcs".equalsIgnoreCase(fieldname)|| "basedata".equalsIgnoreCase(fieldname))
			    	{
			    		sb.append(",max(C"+gridNo+") as C"+gridNo);
			    		ex.append(",max(C"+gridNo+") as C"+gridNo);
			    	}
			    	else  if("N".equalsIgnoreCase(type))
			    	{
			    		sb.append(",sum(C"+gridNo+") as C"+gridNo);
			    		ex.append(",sum(C"+gridNo+") as C"+gridNo);
		    		}
		    		else
		    		{
			    		sb.append(",max(C"+gridNo+") as C"+gridNo);
			    		ex.append(",max(C"+gridNo+") as C"+gridNo);
			    	}
		    	}
			}
			/* 标识：1754 全部人员库取数，结果非在职库的人员在高级花名册中丢失 xiaoyun 2014-5-27 start */
			String declare_tax = Sql_switcher.year("declare_tax")+","+Sql_switcher.month("declare_tax");  // 同一个月进行汇总  // "declare_tax"
			sb.append(" from "+tableName+ " group by NBASE,a0100,"+declare_tax+",taxmode,deptid ");//order by max(recidx)
			/* 标识：1754 全部人员库取数，结果非在职库的人员在高级花名册中丢失 xiaoyun 2014-5-27 end */
			ex.append(" from ("+sb.toString()+") T");
			if(this.getModuleSQL()!=null&&!"".equals(this.getModuleSQL())) {
                ex.append(" where ("+this.getModuleSQL()+")");
            }
			
			/* 标识：1754 全部人员库取数，结果非在职库的人员在高级花名册中丢失 xiaoyun 2014-5-27 start */
			sql_select.append(" from ("+ex.toString()+" group by NBASE,a0100,"+declare_tax+",taxmode) F");
			/* 标识：1754 全部人员库取数，结果非在职库的人员在高级花名册中丢失 xiaoyun 2014-5-27 end */
			sql_select.append(" order by recidx ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				String ss=sql_insert+") select RowNum,a.* from ("+sql_select.toString()+") a";
				dao.insert(sql_insert+") select RowNum,a.* from ("+sql_select.toString()+") a", new ArrayList());
			}
			else
			{
				String sql = sql_insert+")"+sql_select.toString();
		    	dao.insert(sql, new ArrayList());
		    	StipendHmusterBo.updateMusterRecidxS(this.conn,tablename);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public boolean layerOrg(String tablename,String groupPoint,int type){
		boolean check = false;
		
		if(type==1)
		{
	    	if(layerid==null||layerid.trim().length()<1) {
                return true;
            }
		}else if(type==2)
		{
			if(layerid2==null||layerid2.trim().length()<1) {
                return true;
            }
		}
		if(groupPoint==null) {
            return true;
        }
		String codesetid="";
		if("B0110".equalsIgnoreCase(groupPoint)) {
            codesetid="UN";
        } else if("E0122".equalsIgnoreCase(groupPoint)) {
            codesetid="UM";
        }
		if("".equals(codesetid.trim()))
		{
			FieldItem item = DataDictionary.getFieldItem(groupPoint.toLowerCase());
			if(item!=null) {
                codesetid=item.getCodesetid();
            }
			if(!"UM".equalsIgnoreCase(codesetid)&&!"UN".equalsIgnoreCase(codesetid)) {
                codesetid="";
            }
		}
		if(codesetid.trim().length()>0){
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			try {
				StringBuffer buf = new StringBuffer();
				buf.append("SELECT A.codeitemid,  B.codeitemid AS parentid,B.codeitemdesc as codeitemdesc "); 
				buf.append("FROM organization A LEFT JOIN (SELECT codeitemid,codeitemdesc FROM organization ");
				buf.append("WHERE codesetid='");
				buf.append(codesetid);
				buf.append("' AND layer='");
				if(type==1) {
                    buf.append(layerid);
                }
				if(type==2) {
                    buf.append(layerid2);
                }
				buf.append("') B ON (");
				if (Sql_switcher.searchDbServer() == Constant.ORACEL
						||Sql_switcher.searchDbServer() == Constant.DB2) {
					buf.append("substr(A.codeitemid,0,LENGTH(B.codeitemid))=B.codeitemid AND A.codeitemid<>B.codeitemid");
				}else{
					buf.append("LEFT(A.codeitemid, LEN(B.codeitemid))=B.codeitemid AND A.codeitemid<>B.codeitemid");	
				}
				buf.append(") WHERE A.codesetid='");
				buf.append(codesetid);
				buf.append("' and B.codeitemid is not null");
				rowSet = dao.search(buf.toString());

				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("update ");
				sqlstr.append(tablename);
				if(type==1) {
                    sqlstr.append(" set GroupN=?,GroupV=? where GroupN=?");
                }
				if(type==2) {
                    sqlstr.append(" set GroupN2=?,GroupV2=? where GroupN2=?");
                }
				ArrayList updatelist = new ArrayList();
				while(rowSet.next()){
					String codeitemid = rowSet.getString("codeitemid");
					String parentid = rowSet.getString("parentid");
					String codeitemdesc = rowSet.getString("codeitemdesc");
					if(parentid!=null&&parentid.trim().length()>0){
						ArrayList list = new ArrayList();
						list.add(parentid);
						list.add(codeitemdesc);
						list.add(codeitemid);
						updatelist.add(list);
					}
				}
				if(updatelist.size()>0) {
                    dao.batchUpdate(sqlstr.toString(),updatelist);
                }
				check = true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try
				{
					if(rowSet!=null) {
                        rowSet.close();
                    }
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return check;
	}
	public ArrayList cloneFielditem(ArrayList fieldlist)
	{
		ArrayList fielditemlist=new ArrayList();
		for(int i=0;i<fieldlist.size();i++){
			Field field = (Field)fieldlist.get(i);
			if(field!=null){
				FieldItem fielditem = new FieldItem();
				fielditem.setItemid(field.getName());
				fielditem.setItemdesc(field.getLabel());
				fielditem.setItemtype(typeDate(field.getDatatype()));
				fielditem.setItemlength(field.getLength());
				fielditem.setDecimalwidth(field.getDecimalDigits());
				fielditemlist.add(fielditem);
			}
		}
		
		return fielditemlist;
	}
	public String typeDate(int type){
		String strType="A";
		if(type==DataType.INT){
			strType="N";
		}else if(type==DataType.STRING){
			strType="A";
		}else if(type==DataType.DATE){
			strType="D";
		}else if(type==DataType.FLOAT){
			strType="N";
		}
		return strType;
	}
	
	/**
	 * 取得薪资表数据涉及到的应用库
	 * @param salaryid
	 * @return
	 */
	public ArrayList getDbList(String tableName)
	{
		ArrayList dbList=new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search("select distinct nbase from "+tableName);
			while(rowSet.next()) {
                dbList.add(rowSet.getString(1));
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return dbList;
	}
	
	
	/**
	 * 计算公式
	 * @param tabid
	 * @param dbpre
	 * @param userView
	 * @param a0100
	 * @throws GeneralException
	 */
	public void runCountFormula2(String tabid,ArrayList dbList,UserView userView,String salaryid)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			ArrayList allUsedFields=getSalaryList(salaryid);
			allUsedFields.addAll(getMidVariableList(salaryid));
			
			ArrayList list = getFormula(dao,tabid);
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),userView);
			ArrayList templist = getFormulaTempList(list);
			/*liuy 2015-9-14 12339：保险管理，保险报表，计算项计算不出值，不对
			 * templist.size()==0有的业务会有问题
			if(templist.size()==0){
				return;
			}*/
			String tableName="";
			if(model!=null&& "1".equals(model)){
				if(checkdata!=null&&checknum!=null){
					gzbo.setCurrym(checkdata);
					gzbo.setCurrcount(checknum);
				}
				tableName="T#"+userView.getUserName()+"_gzsp";
				salryHistoryTemp(salaryid);
				gzbo.addMidVarIntoGzTable(tableName,"",templist);
				dao.update("create index INDEX_"+tableName+" on "+ tableName +" (NBASE,A00Z0,A0100,A00Z1) ");
			}else{
				gzbo.getYearMonthCount3();
				if("3".equals(model) || "4".equals(model)){
					if(gzbo.getCurrym()==null){
						if(checkdata!=null&&checknum!=null){
							gzbo.setCurrym(checkdata);
							gzbo.setCurrcount(checknum);
						}
					}
				}
				gzbo.addMidVarIntoGzTable("",templist);
			}
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			for (int i=0;i<list.size();i++) {
				HashMap hm = (HashMap)list.get(i);
				if(hm==null) {
                    continue;
                }
				String gridNo = (String)hm.get("gridno");
				String fieldType = (String)hm.get("field_type");
				String queryCond = (String)hm.get("QueryCond");
				String codeSetID = (String)hm.get("CodeId");
				String extendAttr=(String)hm.get("extendAttr");
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员
                //  解析公式
				YksjParser yp = new YksjParser(userView, allUsedFields,YksjParser.forSearch, varType, infoGroup, "Ht", (String)dbList.get(0));
				try{
	            	ArrayList fieldList=yp.getFormulaFieldList1(queryCond);
	            	String ttname=yp.getTempTableName();
	            	ArrayList createlist = new ArrayList();
	            	if(fieldList.size()!=0)
	            	{
	            		createlist = analyseOptTable2(tabid,fieldList,salaryid);
	            	}
	            	yp = new YksjParser(this.userView,allUsedFields,YksjParser.forNormal,varType,YksjParser.forPerson,"","");
	            	yp.setCon(this.conn);
	            	
	            	//yp.run(queryCond);   
	            	//yp.run(queryCond,this.conn,"",atableName);
	            	yp.run(queryCond,this.conn,"",tablename);
	       //     	fieldList=yp.getFormulaFieldList1(queryCond);
	            	String FSQL=yp.getSQL();
	            	ArrayList al = new ArrayList();
	            	ArrayList allist=new ArrayList();
	            	if(yp.getUsedFieldMap()!=null&&yp.getUsedFieldMap().size()>0)
	            	{
	            		Set keySet = yp.getUsedFieldMap().keySet();
	            		for(Iterator t=keySet.iterator();t.hasNext();)
	            		{
	            			String key = (String)t.next();
	            			FieldItem item = (FieldItem)yp.getUsedFieldMap().get(key);
	            			if(item.getVarible()==2) {
                                al.add(item);
                            }
	            		}
	            		if(al.size()>0) {
                            allist = analyseOptTable4(tabid,al,salaryid);
                        }
	            	}
	            	if(varType==9)
	            	{
	            		dao.update("update "+tablename+" set C"+gridNo+"="+Sql_switcher.dateToChar(FSQL,"yyyy.mm.dd"));
	            	}
	            	else
	            	{
				    	dao.update("update "+tablename+" set C"+gridNo+"="+FSQL);
	            	}
					DBMetaModel dbmodel=new DBMetaModel(this.conn);
					dbmodel.reloadTableModel(tablename);
					if(createlist!=null&&createlist.size()>0)
					{
						gzbo.dropCloumTemp(tablename,createlist);
					}
					if(allist.size()>0) {
                        gzbo.dropCloumTemp(tablename,allist);
                    }
				}
				catch(Exception e)
				{
					// 后面公式继续算
					e.printStackTrace();
				}
			}
			if(model!=null&& "1".equals(model)){
				DbWizard dbWizard = new DbWizard(this.conn);
				if(dbWizard.isExistTable(tableName, false)) {
                    dbWizard.dropTable(tableName);
                }
			}else{
				gzbo.dropCloumTemp(templist);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private ArrayList getFormula(ContentDAO dao,String tabid){
		ArrayList formulalist=new ArrayList();
		RowSet rowSet = null;
		try {
			rowSet = dao.search("select * from muster_cell where tabid="
					+ tabid + " and flag='C'");
			while (rowSet.next()) {
				HashMap hm = new HashMap();
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				String codeSetID = rowSet.getString("CodeId");
				String extendAttr = rowSet.getString("ExtendAttr")==null?"":rowSet.getString("ExtendAttr");
				hm.clear();
				hm.put("gridno", gridNo+"");
				hm.put("field_type", fieldType);
				hm.put("QueryCond", queryCond);
				hm.put("CodeId", codeSetID);
				hm.put("extendAttr",extendAttr);
				formulalist.add(hm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return formulalist;
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getFormulaTempList(ArrayList formulaList)throws GeneralException{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset=null;
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(this.salaryid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next()){
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			
			
			//过滤薪资类别  计算公式用不到的临时变量
			FieldItem item=null;
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++){
				HashMap hm=(HashMap)formulaList.get(i);
				if(hm==null) {
                    continue;
                }
				String formula = ((String)hm.get("QueryCond")).toLowerCase();
				for(int j=0;j<fieldlist.size();j++){
					item=(FieldItem)fieldlist.get(j);
					String item_id=item.getItemid().toLowerCase();
					String item_desc=item.getItemdesc().trim().toLowerCase();
					if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null){
						new_fieldList.add(item);
						map.put(item_id, "1");
					}
				}
			}

		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		//return fieldlist;
		return new_fieldList;
	}
	/**
	 * 计算公式
	 * @param tabid
	 * @param dbpre
	 * @param userView
	 * @param a0100
	 * @throws GeneralException
	 */
	public void runCountFormula3(String tabid,ArrayList allUsedFields,String dbpre,String tableName)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try{
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			rowSet = dao.search("select * from muster_cell where tabid="+ tabid + " and flag='C'");
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");				
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员
				ArrayList mlist  = this.getAllMidVariable();
				allUsedFields.addAll(mlist);
                //  解析公式
				YksjParser yp = new YksjParser(userView, allUsedFields,YksjParser.forSearch, varType, infoGroup, "Ht",dbpre);
            	ArrayList fieldList=yp.getFormulaFieldList1(queryCond);              	
            	if(fieldList.size()!=0)
            	{
            		analyseOptTable3(tabid,fieldList,tableName);
            	}
                //  解析公式
            	yp = new YksjParser(
            			this.userView//Trans交易类子类中可以直接获取userView
            			,fieldList
            			,YksjParser.forNormal
            			,varType//此处需要调用者知道该公式的数据类型
            			,YksjParser.forPerson
            			,"","");
            	yp.run(queryCond);                	
            	String FSQL=yp.getSQL();
				dao.update("update "+tablename+" set C"+gridNo+"="+FSQL);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 动态创建表结构
	 * @param tabid
	 * @param fieldList
	 * @param dbpre
	 * @throws GeneralException
	 */
	public void analyseOptTable3(String tabid,ArrayList fieldList,String tableName)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			DbWizard dbWizard = new DbWizard(this.conn);
			Table table = new Table(tablename);
			
			HashMap existColumnMap=new HashMap();
			rowSet=dao.search("select * from "+tablename+" where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				existColumnMap.put(metaData.getColumnName(i).toLowerCase(),"1");
			}
			
			int num=0;
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				FieldItem fieldItem1=(FieldItem)t.next();
				if(existColumnMap.get(fieldItem1.getItemid().toLowerCase())!=null) {
                    continue;
                }
				Field a_field=fieldItem1.cloneField();
				table.addField(a_field);
				num++;
			}
			if(num!=0) {
                dbWizard.addColumns(table);
            }
			
			String musterName=tablename;
			StringBuffer whl=new StringBuffer("");
			/*whl.append(musterName+".a0100= "+tableName+".a0100");
			whl.append(" and "+musterName+".nbase="+tableName+".nbase");
			whl.append(" and "+musterName+".a00z0="+tableName+".a00z0");
			whl.append(" and "+musterName+".a00z1="+tableName+".a00z1");
			whl.append(" and "+musterName+".salaryid="+tableName+".salaryid");*/
			whl.append(" "+musterName+".tax_max_id="+tableName+".tax_max_id");//and 
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				/*StringBuffer sql=new StringBuffer("update "+musterName+" set ");	*/	
				FieldItem fieldItem1=(FieldItem)t.next();
				/*sql.append(" "+musterName+"."+fieldItem1.getItemid()+"=(select "+tableName+"."+fieldItem1.getItemid());
				sql.append(" from "+tableName+" where "+whl.toString()+")");
			    sql.append(" where exists (select null from "+tableName+" where "+whl.toString()+")");
				dao.update(sql.toString());*/
				dbWizard.updateRecord(musterName, tableName, whl.toString(), musterName+"."+fieldItem1.getItemid()+"="+tableName+"."+fieldItem1.getItemid(), "", "");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(String salaryid){
		ArrayList fieldlist=new ArrayList();
		RowSet rset = null;
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(salaryid);
			buf.append("')");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
					item.setItemtype("A");
					break;
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldlist;
	}
	/**
	 * 薪资项目表中取list
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getSalaryList(String salaryid){
		ArrayList fieldlist=new ArrayList();
		RowSet rset=null;
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select FIELDSETID,ITEMID,ITEMDESC,ITEMLENGTH,DECWIDTH,CODESETID");
			buf.append(",FORMULA,ITEMTYPE from salaryset where salaryid=");
			buf.append(salaryid);
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("ITEMID"));
				item.setFieldsetid(rset.getString("FIELDSETID"));//没有实际含义
				item.setItemdesc(rset.getString("ITEMDESC"));
				item.setItemlength(rset.getInt("ITEMLENGTH"));
				item.setDecimalwidth(rset.getInt("DECWIDTH"));
				item.setCodesetid(rset.getString("CODESETID"));
				item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
				item.setItemtype(rset.getString("ITEMTYPE"));
			//	item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldlist;
	}
	
	/**
	 * 动态创建表结构
	 * @param tabid
	 * @param fieldList
	 * @param dbpre
	 * @throws GeneralException
	 */
	public ArrayList analyseOptTable2(String tabid,ArrayList fieldList,String salaryid)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		ArrayList templist = new ArrayList();
		try
		{
			/* 标识：3030 薪资历史数据--报表-用户自定义表，打开后报附件中错误。并且“薪资总额”列（插入的是计算公式）没有算出来值 xiaoyun 2014-7-15 start */
			//String tablename = "";
			String musterName = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
				//tablename = getTemptable();
				musterName = getTemptable();
			}
			else{
				//tablename = userView.getUserName()+"_muster_"+tabid;
				musterName = userView.getUserName()+"_muster_"+tabid;
			}
			DbWizard dbWizard = new DbWizard(this.conn);
			Table table = new Table(musterName);
			/* 标识：3030 薪资历史数据--报表-用户自定义表，打开后报附件中错误。并且“薪资总额”列（插入的是计算公式）没有算出来值 xiaoyun 2014-7-15 end */
			HashMap existColumnMap=new HashMap();
			rowSet=dao.search("select * from "+musterName+" where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				existColumnMap.put(metaData.getColumnName(i).toLowerCase(),"1");
			}
			
			int num=0;
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				FieldItem fieldItem1=(FieldItem)t.next();
				if(existColumnMap.get(fieldItem1.getItemid().toLowerCase())!=null) {
                    continue;
                }
				templist.add(fieldItem1);
				Field a_field=fieldItem1.cloneField();
				table.addField(a_field);
				num++;
			}
			if(num!=0) {
                dbWizard.addColumns(table);
            }
			
			String  tableName="";
			/* 标识：3030 薪资历史数据--报表-用户自定义表，打开后报附件中错误。并且“薪资总额”列（插入的是计算公式）没有算出来值 xiaoyun 2014-7-15 start */
			// 是否为历史数据标识
			boolean isSalaryId = false;
			/* 标识：3030 薪资历史数据--报表-用户自定义表，打开后报附件中错误。并且“薪资总额”列（插入的是计算公式）没有算出来值 xiaoyun 2014-7-15 end */
			if(model!=null&& "1".equals(model)){
				tableName="T#"+userView.getUserName()+"_gzsp";
			}
			/* 标识：3030 薪资历史数据--报表-用户自定义表，打开后报附件中错误。并且“薪资总额”列（插入的是计算公式）没有算出来值 xiaoyun 2014-7-15 start */
			else if (StringUtils.equals(model, "3") || StringUtils.equals(model, "4")) {
				tableName = "salaryhistory";
				if (StringUtils.equals(model, "4")) {
					tableName = "salaryarchive";
				}
				isSalaryId = true;
			}
			/* 标识：3030 薪资历史数据--报表-用户自定义表，打开后报附件中错误。并且“薪资总额”列（插入的是计算公式）没有算出来值 xiaoyun 2014-7-15 end */
			else {
				if (manageUserName != null && manageUserName.trim().length() > 0) {
					tableName = manageUserName + "_salary_" + salaryid;
				} else {
                    tableName = this.userView.getUserName() + "_salary_" + salaryid;
                }
			}
			StringBuffer whl=new StringBuffer(musterName+".a0100= "+tableName+".a0100");
			whl.append(" and "+musterName+".nbase="+tableName+".nbase");
			whl.append(" and "+musterName+".a00z0="+tableName+".a00z0");
			whl.append(" and "+musterName+".a00z1="+tableName+".a00z1");
			/* 标识：3030 薪资历史数据--报表-用户自定义表，打开后报附件中错误。并且“薪资总额”列（插入的是计算公式）没有算出来值 xiaoyun 2014-7-15 start */
			if(isSalaryId) {
				whl.append(" and "+musterName+".salaryId="+tableName+".salaryid");
			}
			/* 标识：3030 薪资历史数据--报表-用户自定义表，打开后报附件中错误。并且“薪资总额”列（插入的是计算公式）没有算出来值 xiaoyun 2014-7-15 end */
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				
				StringBuffer sql=new StringBuffer("update "+musterName+" set ");		
				FieldItem fieldItem1=(FieldItem)t.next();
				if(fieldItem1.getVarible()==2) {
                    continue;
                }
				sql.append(" "+musterName+"."+fieldItem1.getItemid()+"=(select "+tableName+"."+fieldItem1.getItemid());
				sql.append(" from "+tableName+" where "+whl.toString()+")");
			    sql.append(" where exists (select null from "+tableName+" where "+whl.toString()+")");
				dao.update(sql.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return templist;
	}
	public ArrayList analyseOptTable4(String tabid,ArrayList fieldList,String salaryid)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		ArrayList templist = new ArrayList();
		try
		{
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			DbWizard dbWizard = new DbWizard(this.conn);
			Table table = new Table(tablename);
			
			HashMap existColumnMap=new HashMap();
			rowSet=dao.search("select * from "+tablename+" where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				existColumnMap.put(metaData.getColumnName(i).toLowerCase(),"1");
			}
			
			int num=0;
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				FieldItem fieldItem1=(FieldItem)t.next();
				if(existColumnMap.get(fieldItem1.getItemid().toLowerCase())!=null) {
                    continue;
                }
				templist.add(fieldItem1);
				Field a_field=fieldItem1.cloneField();
				table.addField(a_field);
				num++;
			}
			if(num!=0) {
                dbWizard.addColumns(table);
            }
			
			String  tableName="";
			if(model!=null&& "1".equals(model)){
				tableName="T#"+userView.getUserName()+"_gzsp";
			}else{
				if(manageUserName!=null&&manageUserName.trim().length()>0){
					tableName=manageUserName+"_salary_"+salaryid;
				}else {
                    tableName=this.userView.getUserName()+"_salary_"+salaryid;
                }
			}
			String musterName=tablename;
			StringBuffer whl=new StringBuffer(musterName+".a0100= "+tableName+".a0100");
			whl.append(" and "+musterName+".nbase="+tableName+".nbase");
			whl.append(" and "+musterName+".a00z0="+tableName+".a00z0");
			whl.append(" and "+musterName+".a00z1="+tableName+".a00z1");
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				
				StringBuffer sql=new StringBuffer("update "+musterName+" set ");		
				FieldItem fieldItem1=(FieldItem)t.next();
				//19984：宁夏医科大学总医院：薪资自定义花名册部门设置显示层级的计算公式后，后台可以取出数据，前台不能
				if(dbWizard.isExistField(tableName, fieldItem1.getItemid(),false)){						
					sql.append(" "+musterName+"."+fieldItem1.getItemid()+"=(select "+tableName+"."+fieldItem1.getItemid());
					sql.append(" from "+tableName+" where "+whl.toString()+")");
					sql.append(" where exists (select null from "+tableName+" where "+whl.toString()+")");
					dao.update(sql.toString());
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return templist;
	}
	/**
	 * 动态创建表结构
	 * @param tabid
	 * @param fieldList
	 * @param dbpre
	 * @throws GeneralException
	 */
	public void salryHistoryTemp(String salaryid)
	{
		try
		{
			DbWizard dbWizard = new DbWizard(this.conn);
			if(dbWizard.isExistTable("T#"+userView.getUserName()+"_gzsp", false)) {
                dbWizard.dropTable("T#"+userView.getUserName()+"_gzsp");
            }

			StringBuffer sql_whl=new StringBuffer();
			if(checkdata!=null&&checkdata.trim().length()>1
					&&checknum!=null&&checknum.trim().length()>0&&model!=null&& "1".equals(model)){
				String wherestr = dataWhere("salaryHistory",checkdata,checknum,salaryid);
				if(wherestr!=null&&wherestr.trim().length()>0) {
                    sql_whl.append(wherestr);
                }
				if(wherestr!=null&&wherestr.trim().length()>0) {
                    sql_whl.append(" and salaryHistory.salaryid='"+salaryid+"'");
                } else {
                    sql_whl.append(" salaryHistory.salaryid='"+salaryid+"'");
                }
			}
			CreateTable createtable = new CreateTable(this.conn);
			createtable.copyTable("salaryHistory", "T#"+userView.getUserName()+"_gzsp", "*", sql_whl.toString(), "");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 计算公式
	 * @param tabid
	 * @param dbpre
	 * @param userView
	 * @param a0100
	 * @throws GeneralException
	 */
	public void runCountFormula(String tabid,String dbpre,UserView userView,String a0100)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			rowSet = dao.search("select * from muster_cell where tabid="+ tabid + " and flag='C'");
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");				
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员
				
                //  解析公式
				YksjParser yp = new YksjParser(userView, allUsedFields,
						YksjParser.forSearch, varType, infoGroup, "Ht", dbpre);
            	ArrayList fieldList=yp.getFormulaFieldList(queryCond);              	
            	if(fieldList.size()!=0)
            	{
            	//if(fieldList.size()==0)
            	//	continue;
            		analyseOptTable(tabid,fieldList,dbpre,a0100);
            	}
            	
            	  
                //  解析公式
            	yp = new YksjParser(
            			this.userView//Trans交易类子类中可以直接获取userView
            			,fieldList
            			,YksjParser.forSearch
            			,varType//此处需要调用者知道该公式的数据类型
            			,YksjParser.forPerson
            			,"Ht",dbpre);
            	yp.setCon(this.conn);
            	yp.run(queryCond);                	
            	String FSQL=yp.getSQL();
            	StringBuffer sqlstr = new StringBuffer();
            	sqlstr.append("update "+tablename+" set C"+gridNo);
            	sqlstr.append("=");
            	if(FSQL!=null&&FSQL.indexOf("SELECT_")!=-1){
            		sqlstr.append("(select "+FSQL);
            		sqlstr.append(" from temp_"+this.userView.getUserName());
            		sqlstr.append(" where A0100=");
            		sqlstr.append(tablename+".A0100)");
            	}else {
                    sqlstr.append(FSQL);
                }
            	sqlstr.append(" where a0100='"+a0100+"'");
            	
				dao.update(sqlstr.toString());
            	
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 动态创建表结构
	 * @param tabid
	 * @param fieldList
	 * @param dbpre
	 * @throws GeneralException
	 */
	public void analyseOptTable(String tabid,ArrayList fieldList,String dbpre,String a0100)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			DbWizard dbWizard = new DbWizard(this.conn);
			Table table = new Table(tablename);
			
			HashMap existColumnMap=new HashMap();
			rowSet=dao.search("select * from "+tablename+" where recidx=-1");
			ResultSetMetaData metaData=rowSet.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				existColumnMap.put(metaData.getColumnName(i).toLowerCase(),"1");
			}
			
			int num=0;
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				FieldItem fieldItem1=(FieldItem)t.next();
				if(existColumnMap.get(fieldItem1.getItemid().toLowerCase())!=null) {
                    continue;
                }
				Field a_field=fieldItem1.cloneField();
			//	a_field.setLength(50);
				table.addField(a_field);
				num++;
			}
			if(num!=0) {
                dbWizard.addColumns(table);
            }
			
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				StringBuffer sql=new StringBuffer("update "+tablename+" set ");		
				FieldItem fieldItem1=(FieldItem)t.next();
				String tableName=dbpre+fieldItem1.getFieldsetid();
				sql.append(fieldItem1.getItemid()+"=(select "+fieldItem1.getItemid());
				sql.append(" from "+tableName+" where "+tablename+".a0100= ");
				sql.append(tableName);
				sql.append(".a0100");
				if(!fieldItem1.isMainSet()){
					sql.append(" and "+tablename+".i9999=");
					sql.append(tableName);
					sql.append(".i9999");
				}
				sql.append(" and "+tableName+".a0100='"+a0100+"' ) where a0100='"+a0100+"'");
				dao.update(sql.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 取工资发放用户管理范围高级条件
	 * @return
	 */
	private String getSalaryUserManageAdvCond() throws GeneralException {
		String result = null;
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn, Integer.parseInt(salaryid), this.userView);
		String dbpres=gzbo.getTemplatevo().getString("cbase");
		String[] dbarr=StringUtils.split(dbpres, ",");
		StringBuffer sub_str=new StringBuffer("");
		for(int i=0;i<dbarr.length;i++)
		{
			String pre=dbarr[i];
			if(!this.userView.isSuper_admin()&&this.userView.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
			{
				sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and 1=2 )");
			}
			else
			{
				sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and a0100 in (select a0100 "+this.userView.getPrivSQLExpression(pre, false)+" ) )");
			}
			
		}
		if(sub_str.length()>0)
		{
			result = "("+sub_str.substring(3)+")";
		}
		return result;
	}
	 
//	取sql得到连接符
	 public  String getString2()
     {
     	String operate="";
    	switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  operate=" and ";
		  	break;
		  }
		  case Constant.DB2:
		  {
			  operate=" and ";
		  	break;
		  }
		  case Constant.ORACEL:
		  {
			  operate=" or ";
		  	break;
		  }
		}
    	return operate;
     }

	/**
	 * 将临时表中的代码型数据转换成业务数据,如果机构或职位信息库中有人员名单字段也填上数据 (默认在职人员库)
	 */
	public void transformCode(String tabid, String tableName, String inforFlag)
			throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer fieldsSql = new StringBuffer("");
		ArrayList fieldList = new ArrayList(); // 记下人员名单的字段名称
		fieldsSql.append("select GridNo,SetName,Field_Name,Field_Type,Flag,Slope,codeId,Field_Hz  from muster_cell where   tabid=");
		fieldsSql.append(tabid);
		int i = 0;
		StringBuffer nbasesql = new StringBuffer();
		DbWizard db= new DbWizard(this.conn);
		try {
			rowSet = dao.search(fieldsSql.toString());
			StringBuffer sql_str = new StringBuffer(" update " + tableName+ " set ");
			while (rowSet.next()) {
				StringBuffer sql_update = new StringBuffer("");
				String gridNo = rowSet.getString("GridNo");
				String codeId = rowSet.getString("codeId");
				codeId=codeId!=null?codeId:"";
				String flag = rowSet.getString("Flag");
				flag=flag!=null?flag:"";
				String field_Hz = rowSet.getString("Field_Hz");
				field_Hz=field_Hz!=null?field_Hz:"";
				String Field_Name = rowSet.getString("Field_Name");
				
				if(Field_Name!=null&& "NBASE".equalsIgnoreCase(Field_Name)){
					/*nbasesql.append("update "+tableName+" set ");
					nbasesql.append("C" + gridNo+"=(select DBName from dbname where Upper(Pre)=Upper(");
					nbasesql.append(tableName+".C"+gridNo+")) where exists(select null from dbname where UPPER(pre)=UPPER("+tableName+".C"+gridNo+"))");*/
					//liuy 2015-4-2 8419：朝阳卫生局： 薪资审批-薪资报表-用户自定义表，插入的人员库标识，取不出来数据，不对。
					db.updateRecord(tableName, "dbname", "UPPER("+tableName+".C"+gridNo+")=UPPER(dbname.pre)", tableName+".C"+gridNo+"=dbname.dbname", " exists(select null from dbname where UPPER(pre)=UPPER("+tableName+".C"+gridNo+"))", "");
					continue;
				}
				
				if ("A".equals(flag)&& field_Hz.equals(ResourceFactory.getProperty("hmuster.label.mainlist"))&& !"1".equals(inforFlag)) {
                    fieldList.add("C" + gridNo);
                }

				if (codeId != null && !"".equals(codeId) && !"0".equals(codeId)) {
					i++;
					if ("UN".equalsIgnoreCase(codeId) || "UM".equalsIgnoreCase(codeId)|| "@K".equalsIgnoreCase(codeId)) {
						/*sql_update.append("C");
						sql_update.append(gridNo);
						sql_update.append("=(");
						sql_update.append("select codeitemdesc from organization  where  "+ tableName+ ".C"+ gridNo+ "=codeitemid");
						sql_update.append(" and UPPER(codesetid)='"+codeId.toUpperCase()+"') where exists(select null from organization where "+tableName+".C"+gridNo+"=codeitemid");
						sql_update.append(" and UPPER(codesetid)='"+codeId.toUpperCase()+"')");
*/						//where exists (select null from organization S where "+tableName+".GroupN=S.codeitemid)
						
						/* 标识：1755 所得税管理高级花名册单位显示问题 2014-5-28 xiaoyun start */
						String dwhere="";
						String swhere = "";
						if("UM".equalsIgnoreCase(codeId)|| "UN".equalsIgnoreCase(codeId)) { // 部门关联UM，可以维护UN的值//liuy 2015-1-5 6456：中亚时代：薪资分析自定义表取数问题（修改62时发现）
							//dwhere = "UPPER("+tableName+".C"+gridNo+") in (select codeitemid from organization where UPPER(codesetid)='"+codeId.toUpperCase()+"' or UPPER(codesetid)='UN')";
							dwhere = "UPPER("+tableName+".C"+gridNo+") in (select codeitemid from organization where UPPER(codesetid) in('UM','UN'))";
							swhere = "UPPER(organization.codesetid) in('UM','UN')";
						} else {
							dwhere = "UPPER("+tableName+".C"+gridNo+") in (select codeitemid from organization where UPPER(codesetid)='"+codeId.toUpperCase()+"')";
							swhere = "UPPER(organization.codesetid)='"+codeId.toUpperCase()+"'";
						}
						//db.updateRecord(tableName, "organization", tableName+".C"+gridNo+"=organization.codeitemid", tableName+".C"+gridNo+"=organization.codeitemdesc", 
								//temp, "UPPER(organization.codesetid)='"+codeId.toUpperCase()+"'");
						db.updateRecord(tableName, "organization", tableName+".C"+gridNo+"=organization.codeitemid", tableName+".C"+gridNo+"=organization.codeitemdesc", 
								dwhere, swhere);
						/* 标识：1755 所得税管理高级花名册单位显示问题 2014-5-28 xiaoyun end */

					} else {
						/*sql_update.append("C");
						sql_update.append(gridNo);
						sql_update.append("=(");
						sql_update.append("select codeitemdesc from codeitem  where codesetid='"+ codeId+ "' and  "+ tableName+ ".C"+ gridNo + "=codeitemid");
						sql_update.append(") where exists (select null from codeitem where UPPER(codesetid)='"+codeId+"' and "+tableName+".C"+gridNo+"=codeitemid");
						sql_update.append(")");*/
						String temp="UPPER("+tableName+".C"+gridNo+") in (select codeitemid from codeitem where UPPER(codesetid)='"+codeId.toUpperCase()+"')";
						db.updateRecord(tableName, "codeitem", tableName+".C"+gridNo+"=codeitem.codeitemid", tableName+".C"+gridNo+"=codeitem.codeitemdesc", temp, "UPPER(codeitem.codesetid)='"+codeId.toUpperCase()+"'");

					}
					//dao.update(sql_str+" "+sql_update.toString());

				}
				
				
			}
			/*if (i != 0) {
				sql_str.append(sql_update.substring(2));
				dao.update(sql_str.toString());

			}*/
/*
			if(nbasesql!=null&&nbasesql.length()>10){
				dao.update(nbasesql.toString());
			}*/
			if(groupPoint.length()>0)
			{
				if("nbase".equalsIgnoreCase(groupPoint))
				{
					String codeTable="dbname";
					String temp="UPPER("+tableName+".GroupN) in (select UPPER(pre) from "+codeTable+")";
			    	db.updateRecord(tableName, codeTable, tableName+".GroupN="+codeTable+".pre", tableName+".GroupV="+codeTable+".dbname", temp, "");
				}
				else
				{
			    	String codeId="";
			    	FieldItem item=DataDictionary.getFieldItem(groupPoint.toLowerCase());
			    	if(item!=null&&item.getCodesetid()!=null&&!"0".equals(item.getCodesetid()))
			    	{
			    		codeId=item.getCodesetid();
			    	}
			    	else if("b0110".equals(groupPoint.toLowerCase())) {
                        codeId = "UN";
                    } else if("e0122".equals(groupPoint.toLowerCase())|| "deptid".equals(groupPoint.toLowerCase())) {
                        codeId = "UM";
                    } else if("e01a1".equals(groupPoint.toLowerCase())) {
                        codeId="@K";
                    }
			    	String whl_extend="";
			     	String codeTable="codeitem";
			    	if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
                        codeTable="organization";
                    } else {
                        whl_extend=" and codesetid='"+ codeId+ "'";
                    }
					
			    	//dao.update("update "+tableName+" set GroupV=(select codeitemdesc from "+codeTable+"  where  "+tableName+".GroupN=codeitemid "+whl_extend+")" +
						//" where exists(select null from "+codeTable+" where "+tableName+".GroupN="+codeTable+".codeitemid "+whl_extend+")");
			    	/* 标识：1755 所得税管理高级花名册单位显示问题 2014-5-28 xiaoyun start */
			    	String dwhere = "";
			    	String swhere = "";
			    	if("UM".equals(codeId)) {
			    		dwhere = "UPPER("+tableName+".GroupN) in (select UPPER(codeitemid) from "+codeTable+" where UPPER(codesetid) in('UM','UN'))";
			    		swhere = "UPPER("+codeTable+".codesetid) in('UM', 'UN')";
			    	}else {
			    		dwhere = "UPPER("+tableName+".GroupN) in (select UPPER(codeitemid) from "+codeTable+" where UPPER(codesetid)='"+codeId.toUpperCase()+"')";
			    		swhere = "UPPER("+codeTable+".codesetid)='"+codeId.toUpperCase()+"'";
			    	}
			    	db.updateRecord(tableName, codeTable, tableName+".GroupN="+codeTable+".codeitemid", tableName+".GroupV="+codeTable+".codeitemdesc", dwhere, swhere);
			    	//String temp="UPPER("+tableName+".GroupN) in (select UPPER(codeitemdesc) from "+codeTable+" where UPPER(codesetid)='"+codeId.toUpperCase()+"')";
			    	//db.updateRecord(tableName, codeTable, tableName+".GroupN="+codeTable+".codeitemid", tableName+".GroupV="+codeTable+".codeitemdesc", temp, "UPPER("+codeTable+".codesetid)='"+codeId.toUpperCase()+"'");
			    	/* 标识：1755 所得税管理高级花名册单位显示问题 2014-5-28 xiaoyun end */
				}

			}
			if(this.groupPoint2.length()>0)
			{
				if("nbase".equalsIgnoreCase(groupPoint2))
				{
					String codeTable="dbname";
					String temp="UPPER("+tableName+".GroupN2) in (select UPPER(pre) from "+codeTable+")";
			    	db.updateRecord(tableName, codeTable, tableName+".GroupN2="+codeTable+".pre", tableName+".GroupV2="+codeTable+".dbname", temp, "");
				}
				else
				{
			    	String codeId="";
			    	FieldItem item=DataDictionary.getFieldItem(groupPoint2.toLowerCase());
			    	if(item!=null&&item.getCodesetid()!=null&&!"0".equals(item.getCodesetid()))
			    	{
			    		codeId=item.getCodesetid();
			    	}
			    	else if("b0110".equals(groupPoint2.toLowerCase())) {
                        codeId="UN";
                    } else if("e0122".equals(groupPoint2.toLowerCase())) {
                        codeId="UM";
                    } else if("e01a1".equals(groupPoint2.toLowerCase())) {
                        codeId="@K";
                    }
			    	String whl_extend="";
			     	String codeTable="codeitem";
			    	if ("UN".equals(codeId) || "UM".equals(codeId)|| "@K".equals(codeId)) {
                        codeTable="organization";
                    } else {
                        whl_extend=" and codesetid='"+ codeId+ "'";
                    }
					
			    	//dao.update("update "+tableName+" set GroupV=(select codeitemdesc from "+codeTable+"  where  "+tableName+".GroupN=codeitemid "+whl_extend+")" +
						//" where exists(select null from "+codeTable+" where "+tableName+".GroupN="+codeTable+".codeitemid "+whl_extend+")");
			    	/* 标识：1755 所得税管理高级花名册单位显示问题 2014-5-28 xiaoyun start */
			    	String dwhere = "";
			    	String swhere = "";
			    	if("UM".equals(codeId)) {
			    		dwhere = "UPPER("+tableName+".GroupN2) in (select UPPER(codeitemid) from "+codeTable+" where UPPER(codesetid) in('UM', 'UN'))";
			    		swhere = "UPPER("+codeTable+".codesetid) in('UM', 'UN')";
			    	}else {
			    		dwhere = "UPPER("+tableName+".GroupN2) in (select UPPER(codeitemid) from "+codeTable+" where UPPER(codesetid)='"+codeId.toUpperCase()+"')";
			    		swhere = "UPPER("+codeTable+".codesetid)='"+codeId.toUpperCase()+"'";
			    	}
			    	db.updateRecord(tableName, codeTable, tableName+".GroupN2="+codeTable+".codeitemid", tableName+".GroupV2="+codeTable+".codeitemdesc", dwhere, swhere);
					//String temp = "UPPER("+tableName+".GroupN2) in (select UPPER(codeitemid) from "+codeTable+" where UPPER(codesetid)='"+codeId.toUpperCase()+"')";
			    	//db.updateRecord(tableName, codeTable, tableName+".GroupN2="+codeTable+".codeitemid", tableName+".GroupV2="+codeTable+".codeitemdesc", temp, "UPPER("+codeTable+".codesetid)='"+codeId.toUpperCase()+"'");
			    	/* 标识：1755 所得税管理高级花名册单位显示问题 2014-5-28 xiaoyun end */
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 根据条件生成可直接插入临时表数据的sql语句
	 * @author dengc created: 2007/12/01
	 */

	public String createSQL(String tabid,String salaryid,String a_code,String condid,HashMap cFactorMap) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		try
		{
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			ArrayList musterCellList=getMusterCellList(tabid);  //取得花名册不用计算的数据字段
			String  tableName="";
			//String controlByUnitcode="0";
			String orgid="";
			String deptid="";
			SalaryReportBo srb=new SalaryReportBo(this.conn,salaryid,this.userView);
			if(StringUtils.equals(model,"1")||StringUtils.equals(model,"3")){
				tableName="salaryHistory";
			}else if(StringUtils.equals(model,"4")){
				tableName="salaryarchive";
			}else{
				SalaryCtrlParamBo bb=new SalaryCtrlParamBo(this.conn,Integer.parseInt(this.salaryid));
				this.salaryorder=bb.getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
				if(manageUserName!=null&&manageUserName.trim().length()>0){
					tableName=manageUserName+"_salary_"+salaryid;
				}else {
                    tableName=this.userView.getUserName()+"_salary_"+salaryid;
                }
				//controlByUnitcode=srb.controlByUnitcode();
		    	orgid = bb.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
				orgid = orgid != null ? orgid : "";
			    deptid = bb.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
				deptid = deptid != null ? deptid : "";
			}
			salaryDataTable = tableName;
			salaryDataTableCond.setLength(0);
			
			String group="";
			String group2="";
			StringBuffer sql_insert=new StringBuffer(" insert into "+tablename+" (recidx,salaryId,a0000,a0100,b0110,e0122,NBASE,a00z0,a00z1,A00Z2,A00Z3");
			HmusterBo mus=new HmusterBo(conn);
			mus.setSortitem(sortitem);
			StringBuffer sql_from = new StringBuffer(" from "+mus.getGzTableSortView(tableName, null)+",dbname");
			StringBuffer sql_whl = new StringBuffer("");
		
			StringBuffer sql_select = new StringBuffer(" select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append("1,");
            }
			sql_select.append(" '"+salaryid+"',A0000,A0100,B0110,E0122,NBASE,A00Z0,A00Z1,A00Z2,A00Z3");
			SalarySetItemBo salsetbo = new SalarySetItemBo();
			HashMap itemMap = salsetbo.fieldItemMap(conn,salaryid,userView);
			if(cFactorMap.get("groupN")!=null)
			{
				String groupN = (String)cFactorMap.get("groupN");
				Field  fields = (Field)itemMap.get(groupN.toUpperCase());
				if(fields!=null){
					group=groupN;
					sql_insert.append(",groupN,groupV");
					sql_select.append(","+groupN+" as groupN,"+groupN+" as groupV ");	
				}
			}
			if(cFactorMap.get("groupN2")!=null)
			{
				String groupN2 = (String)cFactorMap.get("groupN2");
				Field  fields = (Field)itemMap.get(groupN2.toUpperCase());
				if(fields!=null){
					group2=groupN2;
					sql_insert.append(",groupN2,groupV2");
					sql_select.append(","+groupN2+" as groupN2,"+groupN2+" as groupV2 ");	
				}
			}
			ArrayList fielditemlist = getFieldList(salaryid);
			for(Iterator t=musterCellList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String gridno=(String)abean.get("gridno");
				String setname=(String)abean.get("setname");
				String fieldname=(String)abean.get("fieldname");
				String fieldtype=(String)abean.get("fieldtype");
				String codeid=(String)abean.get("codeid");
				String flag=(String)abean.get("flag");
				String slope=(String)abean.get("slope");
				if(fieldname!=null&&fieldname.trim().length()>0)
				{
					sql_insert.append(",C"+gridno);
					String[] fields={fieldname,fieldtype,slope,codeid};			
					String temp=getField(fields,fielditemlist);
					if(temp.toUpperCase().indexOf("AS")==-1|| "nbase".equalsIgnoreCase(temp)) {
                        sql_select.append(","+temp+" as C"+gridno);
                    } else {
                        sql_select.append(","+temp);
                    }
					if("A0000".equalsIgnoreCase(fieldname)){
						setRix("C"+gridno);
					}
				}
				
			}
			sql_insert.append(")");
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userView);
			
			sql_whl.append(" where Upper("+tableName+".nbase)=Upper(dbname.Pre)");
			com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo newgzbo=
				new	com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userView);
			if(this.userView.getVersion()>=70){
				if(newgzbo.getManager()!=null&&newgzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(newgzbo.getManager()))//共享非管理员
                {
                    sql_whl.append( newgzbo.getWhlByUnits(tableName,true));
                }
				sql_whl.append(codeWhere(a_code));//薪资分析自定义报表 选择机构树 权限控制
			}else{
				if("0".equals(this.model)&&(orgid.length()>0||deptid.length()>0)/*&&controlByUnitcode.equals("1")*/) {
                    sql_whl.append(this.codeWhereByUnitCode(a_code, deptid, orgid));
                } else {
                    sql_whl.append(codeWhere(a_code));
                }
			}
			//if(!condid.equalsIgnoreCase("all")&&(this.getFilterWhl()==null||this.getFilterWhl().trim().length()<1)){
			if(!"all".equalsIgnoreCase(condid)){//liuy 2015-6-19 10366
				String wherestr = gzbo.getFilterWhere(condid,tableName);
				if(wherestr!=null&&wherestr.trim().length()>1) {
                    sql_whl.append(" and ("+wherestr+")");
                }
			}
			if(!this.userView.isSuper_admin()||(this.userView.isSuper_admin()/*&&controlByUnitcode.equals("1")*/&&
			        manageUserName!=null&&!"".equals(manageUserName)))
			{
				if(manageUserName!=null&&!"".equals(manageUserName)&&!manageUserName.equalsIgnoreCase(this.userView.getUserName()))
				{
				    //工资发放中(只发放中这样)，当设置了归属单位指标或者设置了归属部门指标，按这个规则走
					if("0".equals(this.model)/*&&(orgid.length()>0||deptid.length()>0)&&controlByUnitcode.equals("1")*/)
					{
						String b_units = userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
						String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
						if(b_units.length()==0&&(unitIdByBusiOutofPriv!=null&& "1".equals(unitIdByBusiOutofPriv))){
							String privSql = srb.getWhlByUnits();
							sql_whl.append(privSql);
						}else{
							if(this.userView.getVersion()>=70){//70锁以上版本使用最新的薪资权限控制，旧锁沿用之前
								if(newgzbo.getManager()!=null&&newgzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(newgzbo.getManager()))//共享非管理员
                                {
                                    sql_whl.append( newgzbo.getWhlByUnits(tableName,true));
                                }
							}else{
								sql_whl.append(" and " + gzbo.getPrivSQL(userView.isSuper_admin()?"1":"0","",salaryid,b_units));
							}
						}
					}else 
					{
						if(!StringUtils.equals(this.model,"3")||!StringUtils.equals(this.model,"4"))
						{
							String unit_id=this.userView.getUnit_id();
							if(unit_id!=null&&unit_id.length()>=3)
		    				{
		    		    		String[] arr = unit_id.split("`");
		    		    		StringBuffer t_buf = new StringBuffer();
		    			    	for(int i=0;i<arr.length;i++)
		    		    		{
		    			    		String temp = arr[i];
		    				    	if(arr[i]==null|| "".equals(arr[i])) {
                                        continue;
                                    }
		    				    	if("UN".equalsIgnoreCase(temp.substring(0, 2))) {
                                        t_buf.append(" or b0110 ");
                                    } else {
                                        t_buf.append(" or e0122 ");
                                    }
		    				    	t_buf.append(" like '"+arr[i].substring(2)+"%'");
		    		    		}
		    			    	sql_whl.append(" and ("+t_buf.toString().substring(3)+")");
		    				}else
		    				{
		    					String privCode = this.userView.getManagePrivCode();
		    					if(privCode!=null&&privCode.length()>0)
		    					{
		    						String privCodeValue=this.userView.getManagePrivCodeValue();
		    						 if("UN".equalsIgnoreCase(privCode))
		             				{
		    							 sql_whl.append(" and (b0110 like '"+(privCodeValue==null?"":privCodeValue)+"%'");
		    			        		if(privCodeValue==null) {
                                            sql_whl.append(" or b0110 is null");
                                        }
		    			        		sql_whl.append(")");
			            			}
			            			if("UM".equalsIgnoreCase(privCode))
			    	         		{
			            				sql_whl.append(" and (e0122 like '"+(privCodeValue==null?"":privCodeValue)+"%'");
			    		        		if(privCodeValue==null) {
                                            sql_whl.append(" or e0122 is null");
                                        }
			    		        		sql_whl.append(")");
			    	        		}
			            			// 工资发放非管理员管理范围高级条件
			            			if("0".equals(model)){
			            				String cond=getSalaryUserManageAdvCond();
			            				if(cond!=null) {
                                            sql_whl.append(" and " +cond);
                                        }
			            			}
		    					}
		    					else
		    					{
		    						sql_whl.append(" and 1=2 ");
		    					}
		    				}
						}
						else
						{
							HistoryDataBo hbo = new HistoryDataBo(this.conn, this.userView);
							String pri=hbo.getPrivPre("1",salaryid);
							if(pri.length()>0) {
                                sql_whl.append(" and ("+pri+")");
                            }
						}
					}
				}
			}
			if ("salaryarchive".equalsIgnoreCase(tableName)) {
    			HistoryDataBo hbo = new HistoryDataBo(conn, this.userView);
    			String[] salaryids = {salaryid};
    		    String priv_str=hbo.getPrivPre_His(salaryids);  // 用户管理范围
    		    if (priv_str.length() > 0)
    		    {
    		        sql_whl.append(" and (" + priv_str + ")");
    		    }
			}

			if(checkdata!=null&&checkdata.trim().length()>1&&checknum!=null&&checknum.trim().length()>0&&
					(StringUtils.equals(model,"1")||StringUtils.equals(model,"3")||StringUtils.equals(model,"4"))){
				String wherestr = dataWhere(tableName,checkdata,checknum,salaryid);
				if(wherestr!=null&&wherestr.trim().length()>0) {
                    sql_whl.append(" and ("+wherestr+")");
                }
				sql_whl.append(" and "+tableName+".salaryid='"+salaryid+"'");
			}
			sql_whl.append(this.getFilterWhl());
			salaryDataTableCond.append(sql_whl);
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
	    		sql.append(sql_insert.toString());
		    	sql.append(sql_select.toString());
		    	sql.append(sql_from.toString());
		    	sql.append(sql_whl.toString());
		    	if(sortitem.trim().length()>1){
		     		sql.append(getSortStr(tableName,true,false));
		    	}else{
		    		if(this.salaryorder!=null&&this.salaryorder.trim().length()>0){
		    			this.salaryorder=","+this.salaryorder;
		    			this.salaryorder=this.salaryorder.replaceAll(",", ","+tableName+".");
		    			if(!isExistErrorItem(this.salaryorder,gzbo)){
		    				sql.append("  order by ");
		    				sql.append(this.salaryorder.substring(1));
		    			}else{
			     			sql.append("  order by dbname.dbid, "+tableName+".A0000, "+tableName+".A00Z0,"+tableName+".A00Z1");
		    			}
		    		}else{
			    		if((group!=null&&group.trim().length()>0)||(group2!=null&&group2.length()>0)){
			    			sql.append("  order by ");
			    			if(group!=null&&group.trim().length()>0) {
                                sql.append(tableName+"."+group);
                            }
			    			if(group2!=null&&group2.trim().length()>0)
			    			{
			    				if(group!=null&&group.trim().length()>0) {
                                    sql.append(",");
                                }
			    				sql.append(tableName+"."+group2);
			    			}
				    		sql.append(",dbname.dbid, "+tableName+".A0000, "+tableName+".A00Z0, "+tableName+".A00Z1");
				    		
				    	}else{
			     			sql.append("  order by dbname.dbid, "+tableName+".A0000, "+tableName+".A00Z0,"+tableName+".A00Z1");
			    		}
		    		}
		    	}
			}
			else
			{
				sql.append(sql_insert);
				sql.append(" select RowNum,T.* from (");
				sql.append(sql_select+" "+sql_from+" "+sql_whl);
				if(sortitem.trim().length()>1){
		     		sql.append(getSortStr(tableName,true,false));
		    	}else{
		    		if(this.salaryorder!=null&&this.salaryorder.trim().length()>0){
		    			this.salaryorder=","+this.salaryorder;
		    			this.salaryorder=this.salaryorder.replaceAll(",", ","+tableName+".");
		    			if(!isExistErrorItem(this.salaryorder,gzbo)){
		    				sql.append("  order by ");
		    				sql.append(this.salaryorder.substring(1));
		    			}else{
			     			sql.append("  order by dbname.dbid, "+tableName+".A0000, "+tableName+".A00Z0,"+tableName+".A00Z1");
		    			}
		    		}else{
		    			if((group!=null&&group.trim().length()>0)||(group2!=null&&group2.length()>0)){
			    			sql.append("  order by ");
			    			if(group!=null&&group.trim().length()>0) {
                                sql.append(tableName+"."+group);
                            }
			    			if(group2!=null&&group2.trim().length()>0)
			    			{
			    				if(group!=null&&group.trim().length()>0) {
                                    sql.append(",");
                                }
			    				sql.append(tableName+"."+group2);
			    			}
				    		sql.append(",dbname.dbid, "+tableName+".A0000, "+tableName+".A00Z0, "+tableName+".A00Z1");
				    		
				    	}else{
			     			sql.append("  order by dbname.dbid, "+tableName+".A0000, "+tableName+".A00Z0,"+tableName+".A00Z1");
			    		}
		    		}
		    	}
				sql.append(") T");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		
		
		return sql.toString();
	}
	
	public String codeWhere(String a_code){
		StringBuffer sql_whl= new StringBuffer();
		if(a_code==null|| "".equals(a_code.trim())) {
            return "";
        }
		String codesetid=a_code.substring(0, 2);
		String value=a_code.substring(2);
		if(value!=null&&value.length()>0){
			if("UN".equalsIgnoreCase(codesetid))
			{
				sql_whl.append(" and (B0110 like '");
				sql_whl.append(value);
				sql_whl.append("%'");	
				if("".equalsIgnoreCase(value))
				{
					sql_whl.append(" or B0110 is null");
				}
				sql_whl.append(")");
			}else if("UM".equalsIgnoreCase(codesetid)){
				sql_whl.append(" and E0122 like '");
				sql_whl.append(value);
				sql_whl.append("%'");
			}
		}
		return sql_whl.toString();
	}
	/***
	 * 工资发放中，按归属单位或归属部门来进行权限控制
	 * @param a_code
	 * @param deptid
	 * @param orgid
	 * @param unitByCode
	 * @return
	 */
	public String codeWhereByUnitCode(String a_code,String deptid,String orgid){
		StringBuffer sql_whl= new StringBuffer("");
		if(a_code==null|| "".equals(a_code.trim())) {
            return sql_whl.toString();
        }
		String codesetid=a_code.substring(0, 2);
		String value=a_code.substring(2);
		if(value!=null&&value.length()>0){
			if(deptid.length()>0&&orgid.length()>0)
			{
				sql_whl.append(" and (");
				sql_whl.append(orgid+" like '"+value+"%' ");
				if("".equalsIgnoreCase(value))
				{
					sql_whl.append(" or "+orgid+" is null");
				}
				sql_whl.append(" or "+deptid+" like '"+value+"%' ");
				if("".equalsIgnoreCase(value))
				{
					sql_whl.append(" or "+deptid+" is null");
				}
				sql_whl.append(")");
			}else if(orgid.length()>0)
			{
				sql_whl.append(" and (");
				sql_whl.append(orgid+" like '"+value+"%' ");
				if("".equalsIgnoreCase(value))
				{
					sql_whl.append(" or "+orgid+" is null");
				}
				sql_whl.append(")");
			}else
			{
				sql_whl.append(" and (");
				sql_whl.append(deptid+" like '"+value+"%' ");
				if("".equalsIgnoreCase(value))
				{
					sql_whl.append(" or "+deptid+" is null");
				}
				sql_whl.append(")");
			}
		}
		return sql_whl.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param orgOrder
	 * @return
	 * @see HmusterBo#getSortStr(String, boolean)
	 */
	private String  getSortStr(String tableName, boolean orgOrder, boolean isTax){
		StringBuffer fieldsetStr= new StringBuffer();
		sortitem=sortitem!=null?sortitem:"";
		if(sortitem.length()>1) {
            fieldsetStr.append(" order by ");
        }
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String[] itemarr = arr[i].split(":");
				if(itemarr!=null&&itemarr.length==3){
                    String itemid=itemarr[0];
					FieldItem item = DataDictionary.getFieldItem(itemarr[0]);
                    boolean isOrgItem = (item!=null) && ("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
                            "@K".equalsIgnoreCase(item.getCodesetid()));
                    if(isOrgItem && orgOrder) {
                        itemid="org"+itemarr[0];
                    }
					if(item!=null){
						String setid=item.getFieldsetid();
						/**oracle按照汉字排序，默认是根据二进制编码排的，此处改为按拼音编码排*/
						if("A".equalsIgnoreCase(item.getItemtype())&& "0".equals(item.getCodesetid())&&Sql_switcher.searchDbServer()==Constant.ORACEL)
						{
							fieldsetStr.append("nlssort(");
							fieldsetStr.append(tableName);
							fieldsetStr.append(".");
							fieldsetStr.append(itemid);
							fieldsetStr.append(",'NLS_SORT=SCHINESE_PINYIN_M') ");
							if("0".equals(itemarr[2])) {
                                fieldsetStr.append("DESC,");
                            } else {
                                fieldsetStr.append("ASC,");
                            }
						}
						else
						{
					    	fieldsetStr.append(tableName);
					    	fieldsetStr.append(".");
					    	fieldsetStr.append(itemid);
					     	fieldsetStr.append(" ");
					    	if("0".equals(itemarr[2])) {
                                fieldsetStr.append("DESC,");
                            } else {
                                fieldsetStr.append("ASC,");
                            }
						}
					}else{
						fieldsetStr.append(tableName);
				    	fieldsetStr.append(".");
				    	fieldsetStr.append(itemid);
				     	fieldsetStr.append(" ");
				    	if("0".equals(itemarr[2])) {
                            fieldsetStr.append("DESC,");
                        } else {
                            fieldsetStr.append("ASC,");
                        }
					}
				}
			}
		}
		
		// 增加默认排序指标
		tableName = tableName.toLowerCase();
        if(!isTax) //如果是从个税表生成花名册,需去掉此排序指标 2014-04-01 dengcan
        {
            if(fieldsetStr.toString().toLowerCase().indexOf(tableName+".dbid")==-1) {
                fieldsetStr.append(tableName+".dbid,");
            }
        }
		if(fieldsetStr.toString().toLowerCase().indexOf(tableName+".a0000")==-1) {
            fieldsetStr.append(tableName+".A0000,");
        }
		if(fieldsetStr.toString().toLowerCase().indexOf(tableName+".a00z0")==-1) {
            fieldsetStr.append(tableName+".A00Z0,");
        }
		if(fieldsetStr.toString().toLowerCase().indexOf(tableName+".a00z1")==-1) {
            fieldsetStr.append(tableName+".A00Z1,");
        }
		
		if(fieldsetStr.length()>1) {
            return fieldsetStr.toString().substring(0, fieldsetStr.toString().length()-1);
        } else {
            return "";
        }
	}
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getFieldList(String salaryid){
		ArrayList fieldlist=new ArrayList();
		RowSet rset = null;
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(salaryid);
			buf.append("')");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
					item.setItemtype("A");
					break;
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			String sqlstr = "select * from salaryset";
			if(salaryid!=null&&salaryid.trim().length()>0){
				sqlstr+=" where salaryid="+salaryid;
			}
			rset=dao.search(sqlstr);
			while(rset.next()){
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("ITEMID"));
				item.setItemdesc(rset.getString("ITEMDESC"));
				item.setFieldsetid(rset.getString("FIELDSETID"));
				item.setItemlength(rset.getInt("ITEMLENGTH"));
				item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
				item.setDecimalwidth(rset.getInt("DECWIDTH"));
				item.setItemtype(rset.getString("ITEMTYPE"));
				item.setVarible(1);
				fieldlist.add(item);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldlist;
	}
	
	/**
	 * 根据条件生成可直接插入临时表数据的sql语句
	 * @author dengc created: 2007/12/01
	 */

	public String createSpSQL(String tabid,String a_code,String declaredate,String dbpre) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		try
		{
			ArrayList musterCellList=getMusterCellList(tabid);  //取得花名册不用计算的数据字段
			String  tableName=this.getTaxTable();
			
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }

			StringBuffer sql_insert=new StringBuffer(" insert into "+tablename+" (recidx,salaryid,tax_max_id,A0000,A0100,B0110,E0122,NBASE,A00Z0,A00Z1,declare_tax");
			StringBuffer sql_from = new StringBuffer(" from "+tableName+" a");
            //      StringBuffer sql_whl = new StringBuffer(" where upper(NBASE)='");
            //      sql_whl.append(dbpre.toUpperCase()+"'");
            StringBuffer sql_whl = new StringBuffer(" where 1=1 ");
            if(dbpre!=null&&!"ALL".equalsIgnoreCase(dbpre.trim())) //2014-04-01 dengcan
            {
                sql_whl.append(" and upper(NBASE)='"+dbpre.toUpperCase()+"'");
            }
			
			/*if(this.getPrivSQL()!=null&&!this.getPrivSQL().trim().equals(""))
				sql_whl.append(" and ("+this.getPrivSQL()+")");*/
			if(this.getModuleSQL()!=null&&!"".equals(this.getModuleSQL().trim())) {
                sql_whl.append(" and ("+this.getModuleSQL()+")");
            }
			if(this.getConSQL()!=null&&!"".equals(this.getConSQL()))
			{
				sql_whl.append(" and ("+this.getConSQL()+")");
			}
			StringBuffer sql_select = new StringBuffer(" ");
			sql_select.append(" select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append(" 1,");
            }
			sql_select.append("a.salaryid,a.tax_max_id,a.A0000,a.A0100,a.B0110,a.E0122,a.NBASE,a.A00Z0,a.A00Z1,declare_tax");
			
			TaxMxBo taxbo=new TaxMxBo(this.conn);
			ArrayList fieldlist=taxbo.getFieldlist();
			
			for(Iterator t=musterCellList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String gridno=(String)abean.get("gridno");
				String fieldname=(String)abean.get("fieldname");
				String fieldtype=(String)abean.get("fieldtype");
				String codeid=(String)abean.get("codeid");
				String slope=(String)abean.get("slope");
				String flag=(String)abean.get("flag");
				/**flag=v是临时变量，临时变量不能直接导入*/
				if(fieldname!=null&&fieldname.trim().length()>0&&!"V".equalsIgnoreCase(flag))
				{
					sql_insert.append(",C"+gridno);
					String[] fields={fieldname,fieldtype,slope,codeid};	
					String n=getFieldTax(fields,fieldlist);
					if(n.toUpperCase().indexOf("AS")==-1|| "A.NBASE".equalsIgnoreCase(n.toUpperCase())|| "nbase".equalsIgnoreCase(n)) {
                        n+=" as C"+gridno;
                    }
					sql_select.append(","+n);
				}
				
			}
			if(declaredate!=null&&declaredate.trim().length()>2&&!"all".equalsIgnoreCase(declaredate)){
				sql_whl.append(" and "+declaredate);
			}
			if (isGroupPoint != null&&isGroupPoint.trim().length()>0 && "1".equals(isGroupPoint)){
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)){
					sql_select.append(",");
					sql_select.append("a.");
					sql_select.append(groupPoint);
					sql_select.append(" as GroupN,organization.codeitemdesc as GroupV");
					sql_from.append(" left join organization on ");
					sql_from.append("a.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				}else{
					FieldItem fielditem = DataDictionary.getFieldItem(groupPoint);
					if(fielditem!=null&&fielditem.isCode()){
						sql_select.append(",a." + groupPoint+ " as GroupN,codeitem.codeitemdesc as GroupV");
					}else{
						sql_select.append(",a." + groupPoint);
						sql_select.append(" as GroupN,a." + groupPoint+" as GroupV");
					}
					if(fielditem!=null&&fielditem.isCode()){
						sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='");
						sql_from.append(groupPoint+"' )) codeitem ");
						sql_from.append(" on codeitem.codeitemid="+ "a." + groupPoint);
					}
				}
				sql_insert.append(",GroupN,GroupV");
			}
			if (isGroupPoint2 != null&&isGroupPoint2.trim().length()>0 && "1".equals(isGroupPoint2)){
				FieldItem item = DataDictionary.getFieldItem(groupPoint2);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint2) || "E01A1".equals(groupPoint2)|| "E0122".equals(groupPoint2)){
					sql_select.append(",");
					sql_select.append("a.");
					sql_select.append(groupPoint2);
					sql_select.append(" as GroupN2,organization.codeitemdesc as GroupV2");
					sql_from.append(" left join organization on ");
					sql_from.append("a.");
					sql_from.append(groupPoint2);
					sql_from.append("=organization.codeitemid");
				}else{
					FieldItem fielditem = DataDictionary.getFieldItem(groupPoint2);
					if(fielditem!=null&&fielditem.isCode()){
						sql_select.append(",a." + groupPoint2+ " as GroupN2,codeitem.codeitemdesc as GroupV2");
					}else{
						sql_select.append(",a." + groupPoint2);
						sql_select.append(" as GroupN2,a." + groupPoint2+" as GroupV2");
					}
					if(fielditem!=null&&fielditem.isCode()){
						sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='");
						sql_from.append(groupPoint2+"' )) codeitem ");
						sql_from.append(" on codeitem.codeitemid="+ "a." + groupPoint2);
					}
				}
				sql_insert.append(",GroupN2,GroupV2");
			}
			sql_insert.append(")");
			
			if(sortitem!=null&&sortitem.trim().length()>1){
				sql_whl.append(getSortStr("a", false,true));
			}else{
				sql_whl.append(" order by a.A0000");
			}
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
		    	sql.append(sql_insert.toString());
		    	sql.append(sql_select.toString());
		    	sql.append(sql_from.toString());
		    	sql.append(sql_whl.toString());
			}
			else
			{
				sql.append(sql_insert);
				sql.append("select RowNum,a.* from ");
				sql.append("("+sql_select+sql_from+sql_whl+") a");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		return sql.toString();
	}
	
	/**
	 * 根据条件生成可直接插入临时表数据的sql语句
	 * @param tabid
	 * @param a_code
	 * @param declaredate 时间
	 * @param num  次数 
	 * @param sumFlag  汇总 
	 * @param condwhere  过滤条件 
	 * @return
	 * @throws GeneralException
	 */

	public String createGzSQL(String tabid,String a_code,HashMap cFactorMap,String declaredate,
			String condwhere,boolean sumFlag) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		try
		{
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			ArrayList musterCellList=getMusterCellList(tabid);  //取得花名册不用计算的数据字段
			String  tableName="salaryhistory";

			StringBuffer sql_insert=new StringBuffer(" insert into "+tablename+" (recidx,a0000,a0100,b0110,e0122,NBASE,a00z0,a00z1");
			StringBuffer sql_from = new StringBuffer(" from "+tableName+" a ");
			StringBuffer sql_whl = new StringBuffer(" where 1=1");
			StringBuffer groupBy = new StringBuffer(" group by A0100,A0000,B0110,E0122,NBASE,A00Z0,A00Z1 ");
		
			StringBuffer sql_select = new StringBuffer(" ");
			sql_select.append(" select 1,A0000,A0100,B0110,E0122,NBASE,A00Z0,A00Z1");
			groupBy.append("");
			
			if(a_code!=null&&a_code.trim().length()>1){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if(value!=null&&value.length()>0){
					if("UN".equalsIgnoreCase(codesetid))
					{
						sql_whl.append(" and (B0110 like '");
						sql_whl.append(value);
						sql_whl.append("%'");	
						if("".equalsIgnoreCase(value))
						{
							sql_whl.append(" or B0110 is null");
						}
						sql_whl.append(")");
					}else if("UM".equalsIgnoreCase(codesetid)){
						sql_whl.append(" and E0122 like '");
						sql_whl.append(value);
						sql_whl.append("%'");
					}
					
				}
			}
			String wheredate = declaredate;
			if(wheredate!=null&&wheredate.trim().length()>1){
				sql_whl.append(" and ");
				sql_whl.append(wheredate);
			}

			if(condwhere!=null&&condwhere.trim().length()>4){
				sql_whl.append(" and ");
				sql_whl.append(condwhere);
			}

			for(Iterator t=musterCellList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String gridno=(String)abean.get("gridno");
				String fieldname=(String)abean.get("fieldname");
				String fieldtype=(String)abean.get("fieldtype");
				String codeid=(String)abean.get("codeid");
				String slope=(String)abean.get("slope");
				if(fieldname!=null&&fieldname.trim().length()>0)
				{
					sql_insert.append(",C"+gridno);
					String[] fields={fieldname,fieldtype,slope,codeid};
					if(sumFlag){
						String itemid = getField(fields);
						if(fieldtype!=null&& "N".equalsIgnoreCase(fieldtype)){
							sql_select.append(",(select sum("+itemid+")");
							sql_select.append("from salaryhistory ");
							sql_select.append(sql_whl);
							sql_select.append(" and A0100=a.A0100 ) as "+itemid);
						}else{
							sql_select.append(","+itemid);
							groupBy.append(","+itemid);
						}
					}else{
						sql_select.append(","+getField(fields));
					}
				}
				
			}
			sql_insert.append(")");

			sql.append(sql_insert.toString());
			sql.append(sql_select.toString());
			sql.append(sql_from.toString());
			sql.append(sql_whl.toString());
			if(sumFlag){
				sql.append(groupBy.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		
		
		return sql.toString();
	}
	
	/**
	 * 取得报税时间过滤条件
	 * @param declaredate
	 * @return
	 */
	private String getFilterCond(String declaredate)
	{
		StringBuffer buf=new StringBuffer();
		if(declaredate==null|| "".equalsIgnoreCase(declaredate)|| "all".equalsIgnoreCase(declaredate)) {
            return "";
        }
		String[] datearr=StringUtils.split(declaredate, ".");
		String theyear=datearr[0];
		String themonth=datearr[1];
		buf.append(Sql_switcher.year("Declare_tax"));
		buf.append("=");
		buf.append(theyear);
		buf.append(" and ");
		buf.append(Sql_switcher.month("Declare_tax"));
		buf.append("=");
		buf.append(themonth);		
		return buf.toString();
	}
	
	private boolean isPersonView(String setname) {
	    return setname != null && setname.toUpperCase().startsWith("V_EMP_");
	}
	
	/**
	 * 根据条件生成可直接插入临时表数据的sql语句
	 * @param tabid  高级花名册的id
	 * @param operate  0:无条件  1：按年查询   2：按月查询  3 按季度查询  4 按时间段查询
	 * 
	 *
	 *
	 * @author dengc created: 2003/03/22
	 */
	public static String year_restrict="";
	public String createSQL2(String tabid,String dbpre,String operate,String year,String month,
			String quarter,String startDate,String endDate,String a0100) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		try
		{
			ArrayList musterCellList=getMusterCellList(tabid);  //取得花名册不用计算的数据字段
			
			String asetname="";
			for(Iterator t=musterCellList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String a_setname=((String)abean.get("setname")).toUpperCase();
				if(a_setname.trim().length()>0)
				{
					if(!"A01".equals(a_setname)) {
                        asetname=a_setname;
                    }
				}
				
			}
			String tablename = "";
			if(getTemptable()!=null&&getTemptable().trim().length()>0) {
                tablename = getTemptable();
            } else {
                tablename = userView.getUserName()+"_muster_"+tabid;
            }
			StringBuffer sql_insert=new StringBuffer(" insert into "+tablename+" (recidx,a0000,a0100,b0110,e0122");
			// linbz	20160907  校验子集是否是年月变化子集-22442
			FieldSet vo = null;
			if(asetname!=null&&asetname.trim().length()>0)
			{
				vo = DataDictionary.getFieldSetVo(asetname.toLowerCase());
			}
			if(vo!=null && !"0".equals(vo.getChangeflag())) {
                sql_insert.append(",i9999,a00z0,a00z1 ");
            }
			StringBuffer sql_from = new StringBuffer(" from "+dbpre+"A01");
			StringBuffer sql_whl = new StringBuffer("");
			String orderSql = ""; // 排序sql
		
			StringBuffer sql_select = new StringBuffer(" ");
			sql_select.append(" select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append(" 1,");
            }
			sql_select.append(dbpre);
			sql_select.append("A01.A0000,");
			sql_select.append(dbpre);
			sql_select.append("A01.A0100,");
			sql_select.append(dbpre);
			sql_select.append("A01.B0110,");
			sql_select.append(dbpre);
			sql_select.append("A01.E0122");
			// linbz	20160907  校验子集是否是年月变化子集-22442
			if(vo!=null && !"0".equals(vo.getChangeflag())){
			    if (isPersonView(asetname)) {
                    sql_select.append(",i9999,"+asetname+"."+asetname+"z0 a00z0,"+asetname+"."+asetname+"z1 a00z1");
                } else {
                    sql_select.append(",i9999,"+dbpre+asetname+"."+asetname+"z0 a00z0,"+dbpre+asetname+"."+asetname+"z1 a00z1");
                }
			}

			if (isGroupPoint != null && isGroupPoint.trim().length() > 0&& "1".equals(isGroupPoint)) // 选用分组指标
			{
				FieldItem item = DataDictionary.getFieldItem(groupPoint);
				if ((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())||
				        "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equals(groupPoint) || "E01A1".equals(groupPoint)||
				        "E0122".equals(groupPoint)) // 采用的硬编码指标
				{
					
					sql_insert.append(",GroupN,GroupV");
					sql_select.append(",");
					sql_select.append(dbpre);
					sql_select.append("A01.");
					sql_select.append(groupPoint);
					sql_select.append(" as groupN,organization.codeitemdesc as groupV");

					sql_from.append(" left join organization on ");
					sql_from.append(dbpre);
					sql_from.append("A01.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				} else {
					// linbz 20160906    缺陷 22440 去除多余的列（添加数据时没有GroupN,GroupV这两列，也无需查询出来）  asetname+
//					sql_select.append("," + dbpre + "A01." + groupPoint+ " as groupN,codeitem.codeitemdesc as groupV");
					// linbz 20160914    缺陷 22756	 判断分组指标是否在主集里
					if("A01".equals(item.getFieldsetid())){
						sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem "+
						                        "where itemid='"+ groupPoint + "' )) codeitem ");
						sql_from.append(" on   codeitem.codeitemid=" + dbpre+ "A01." + groupPoint);
					}
				}
			}
			HashMap fieldSetMap=new HashMap();
			fieldSetMap.put("A01","1");
			
			for(Iterator t=musterCellList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String gridno=(String)abean.get("gridno");
				String setname=(String)abean.get("setname");
				String fieldname=(String)abean.get("fieldname");
				String fieldtype=(String)abean.get("fieldtype");
				String codeid=(String)abean.get("codeid");
				String flag=(String)abean.get("flag");
				String slope=(String)abean.get("slope");
				if(fieldname!=null&&fieldname.trim().length()>0)
				{
					sql_insert.append(",C"+gridno);
					String xx=" C"+gridno;
					String tabname = null;
					if(isPersonView(setname)) {
                        tabname = setname;
                    } else {
                        tabname = dbpre+setname;
                    }
					String[] fields = {tabname+"."+fieldname,fieldtype,slope,codeid,setname};
					if("NBASE".equalsIgnoreCase(fieldname)){
						sql_select.append(", (select DBName from dbname where Pre='"+dbpre+"') AS "+xx);
					}else
					{
						String tt=getField(fields);
						if(tt.toUpperCase().indexOf("AS")==-1|| "NBASE".equalsIgnoreCase(tt)) {
                            sql_select.append(","+tt+" AS "+xx);
                        } else {
                            sql_select.append(","+getField(fields));
                        }
					}
					//sql_select.append(","+   dbpre+setname+"."+fieldname);
				}
				if(setname!=null&&setname.trim().length()>0) {
                    fieldSetMap.put(setname.toUpperCase(),"1");
                }
			}
			
			if(fieldSetMap.size()>2) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.conant.only.item")+"！"));
            }
			
			sql_insert.append(")");
			Set keySet=fieldSetMap.keySet();
			String setname="";
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String tempSet=((String)t.next()).toUpperCase();
				if(!"A01".equals(tempSet))
				{
					setname=tempSet;
					if(isPersonView(tempSet)) {
    					sql_from.append(","+tempSet);
    					sql_whl.append(" where "+dbpre+"A01.a0100=");
    					sql_whl.append(tempSet);
    					sql_whl.append(".a0100 and ");
                        sql_whl.append("upper("+tempSet+".NBASE) = '"+dbpre.toUpperCase()+"' and ");
    					sql_whl.append(tempSet+".a0100 = '"+a0100+"' ");
					}
					else {
						if(strSql!=null&&!"".equals(strSql))  //31320	中交上海航道局-自助我的薪酬查看功能优化
                        {
                            sql_from.append(" , "+strSql);
                        } else {
                            sql_from.append(","+dbpre+tempSet);
                        }
                        //linbz 20160914 校验分组指标是否属于该子集
						FieldItem groupItem = DataDictionary.getFieldItem(groupPoint, tempSet);
						if(groupItem!=null && "1".equals(groupItem.getUseflag())){
							sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem "+
									"where itemid='"+ groupPoint + "' )) codeitem ");
							sql_from.append(" on   codeitem.codeitemid=" + dbpre+ tempSet+"." + groupPoint);
						}
	                    sql_whl.append(" where "+dbpre+"A01.a0100=");
	                    sql_whl.append(dbpre+tempSet);
	                    sql_whl.append(".a0100 and ");
	                    sql_whl.append(dbpre+tempSet);
	                    sql_whl.append(".a0100='"+a0100+"' ");
					}
				}
			}
			if(keySet.size()==1) {
                sql_whl.append(" where "+dbpre+"A01.a0100='"+a0100+"'");
            }
			
			
		    sql_whl.append(getWhl(setname,dbpre,operate,year,month,quarter,startDate,endDate));
		    if(strWhere!=null&&!"".equals(strWhere))//31320	中交上海航道局-自助我的薪酬查看功能优化
            {
                sql_whl.append(" and "+strWhere);
            }
		    if(!"".equals(year_restrict)) {
                sql_whl.append(" and "+Sql_switcher.year(setname+"z0")+">="+year_restrict);
            }
			sql.append(sql_insert.toString());
			sql.append(sql_select.toString());
			sql.append(sql_from.toString());
			sql.append(sql_whl.toString());
			if(asetname.length()>0) {
                sql.append(" order by i9999");
            }
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sql.setLength(0);
				sql.append(sql_insert.toString());
				sql.append(" select RowNum,a.* from (");
				sql.append(sql_select.toString()+sql_from.toString()+sql_whl);
				if(asetname.length()>0) {
                    sql.append(" order by i9999");
                }
				sql.append(") a");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		
		
		return sql.toString();
	}
	
	
	
	/**
	 * 根据条件生成相应格式的sql字段
	 * 
	 * @param String[0]:
	 *            指标字段名称
	 * @param String[1]:
	 *            指标字段的数据类型
	 * @param String[2]:
	 *            需要显示的格式 1,2,3,4对数值型为数值精度,也即小数点位数对日期而言的控制位 =6 1992.12.2 =7
	 *            99.2.23 =8 1992.2 =9 98.2 =10 1990年2月10日 =11 1992年10月 =12
	 *            99年4月10日 =13 90年6月
	 */
	public String getField(String[] fields) {
		StringBuffer field = new StringBuffer("");
		String aField = "";
		if (fields[0].indexOf(".") == -1) {
            aField = fields[0];
        } else {
			aField = fields[0].substring(fields[0].indexOf(".") + 1);
		}
		String setname = "";
		if(fields.length >= 5) {
            setname = fields[4];
        }
		FieldItem item = null;
		if(setname.length() > 0) {
            item = DataDictionary.getFieldItem(aField, setname);
        } else {
            item = DataDictionary.getFieldItem(aField);
        }
		//liuy 2014-11-17 5097：我的薪酬，高级花名册中有指标已并勾库掉删除掉，上来就报错。
		if(item != null && "0".equals(item.getUseflag())){//日期型的指标也应该判断是否构库
			field.append("null ");//liuy end
		}else if ("D".equals(fields[1])) {
			if (item != null && "D".equals(item.getItemtype())) {
				field.append(" case when " + fields[0] + " is null then ''");
				 if("19".equals(fields[2])){
				     field.append(" when "+Sql_switcher.month(fields[0])+">9 then ");
				     field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
					 field.append(getString() + "'.'" + getString());
					 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
					 field.append(" else  ");
					 field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
					 field.append(getString() + "'.0'" + getString());
					 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
				}else{
					field.append(" else ");
	
					if ("6".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
	
					} else if ("7".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
	
					} else if ("8".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
					} else if ("9".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
	
					} else if ("10".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
					} else if ("11".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
					} else if ("12".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
					} else if ("13".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
					} else if ("14".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "' '" + getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'");
					} else if ("15".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
					} else if ("16".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
					} else if ("17".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day(fields[0]))));
					}else {
						field.append(fields[0]);
					}
				}
				if (fields[0].indexOf(".") == -1) {
                    field.append(" end as " + fields[0]+"_time");
                } else {
					field.append(" end as "+ fields[0].substring(fields[0].indexOf(".") + 1)+"_time");

				}
			} else{
				if(item!=null) {
                    field.append(fields[0]);
                } else {
                    field.append("null ");//as "+fields[0]
                }
			}

		} else {
			if(item!=null) {
                field.append(fields[0]);
            } else {
                field.append("null ");//as "+fields[0]
            }
		}
		return field.toString();
	}
	/**
	 * 根据条件生成相应格式的sql字段
	 * 
	 * @param String[0]:
	 *            指标字段名称
	 * @param String[1]:
	 *            指标字段的数据类型
	 * @param String[2]:
	 *            需要显示的格式 1,2,3,4对数值型为数值精度,也即小数点位数对日期而言的控制位 =6 1992.12.2 =7
	 *            99.2.23 =8 1992.2 =9 98.2 =10 1990年2月10日 =11 1992年10月 =12
	 *            99年4月10日 =13 90年6月
	 */
	public String getField(String[] fields,ArrayList fielditemlist) {
		StringBuffer field = new StringBuffer("");
		
		String aField = "";
		if (fields[0].indexOf(".") == -1) {
            aField = fields[0];
        } else {
			aField = fields[0].substring(fields[0].indexOf(".") + 1);

		}
		FieldItem item = null;
		for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem = (FieldItem)fielditemlist.get(i);
			if(aField.equalsIgnoreCase(fielditem.getItemid())) {
                item=fielditem;
            }
		}
		
		if ("D".equals(fields[1])) {

			if (item != null && "D".equals(item.getItemtype())) {
				field.append(" case when " + fields[0] + " is null then ''");
				if("19".equals(fields[2]))
				{
					 field.append(" when "+Sql_switcher.month(fields[0])+">9 then ");
				     field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
					 field.append(getString() + "'.'" + getString());
					 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
					 field.append(" else  ");
					 field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
					 field.append(getString() + "'.0'" + getString());
					 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
				}else{
					field.append(" else ");
					if ("6".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
	
					} else if ("7".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
	
					} else if ("8".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
					} else if ("9".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
	
					} else if ("10".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
					} else if ("11".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
					} else if ("12".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
					} else if ("13".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
					} else if ("14".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "' '" + getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'");
					} else if ("15".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
					} else if ("16".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
					} else if ("17".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day(fields[0]))));
					}else {
						field.append(fields[0]);
					}
			    } 
				if (fields[0].indexOf(".") == -1) {
                    field.append(" end as " + fields[0]+"_time");
                } else {
					field.append(" end as "+ fields[0].substring(fields[0].indexOf(".") + 1)+"_time");
				}
		} else {
			field.append("null ");//as "+aField
		}

		} else {

			if(item!=null) {
                field.append(aField);
            } else {
                field.append("null ");//as "+aField
            }

		}
		return field.toString();
	}
	public String dateStr(String date){
		StringBuffer datestr = new StringBuffer();;
		switch (Sql_switcher.searchDbServer()) {
		
		case Constant.MSSQL: {
			datestr.append("case when len(");
			datestr.append(date);
			datestr.append(")>1 then ");
			datestr.append(date);
			datestr.append(" else '0'");
			datestr.append(getString());
			datestr.append(date);
			datestr.append(" end ");
			break;
		}
		case Constant.DB2: {
			datestr.append("case when length(");
			datestr.append(date);
			datestr.append(")>1 then ");
			datestr.append(date);
			datestr.append(" else '0'");
			datestr.append(getString());
			datestr.append(date);
			datestr.append(" end ");
			break;
		}
		case Constant.ORACEL: {
			datestr.append("case when length(");
			datestr.append(date);
			datestr.append(")>1 then ");
			datestr.append(date);
			datestr.append(" else '0'");
			datestr.append(getString());
			datestr.append(date);
			datestr.append(" end ");
			break;
		}
		default:{
			datestr.append("case when len(");
			datestr.append(date);
			datestr.append(")>1 then ");
			datestr.append(date);
			datestr.append(" else '0'");
			datestr.append(getString());
			datestr.append(date);
			datestr.append(" end ");
			break;
		}
		}
		return datestr.toString();
	}
	/**
	 * 根据条件生成相应格式的sql字段
	 * 
	 * @param String[0]:
	 *            指标字段名称
	 * @param String[1]:
	 *            指标字段的数据类型
	 * @param String[2]:
	 *            需要显示的格式 1,2,3,4对数值型为数值精度,也即小数点位数对日期而言的控制位 =6 1992.12.2 =7
	 *            99.2.23 =8 1992.2 =9 98.2 =10 1990年2月10日 =11 1992年10月 =12
	 *            99年4月10日 =13 90年6月
	 */
	public String getFieldTax(String[] fields,ArrayList fielditemlist) {
		StringBuffer field = new StringBuffer("");
		
		String aField = "";
		if (fields[0].indexOf(".") == -1) {
            aField = fields[0];
        } else {
			aField = fields[0].substring(fields[0].indexOf(".") + 1);

		}
		Field item = null;
		for(int i=0;i<fielditemlist.size();i++){
			Field fielditem = (Field)fielditemlist.get(i);
			if(aField.equalsIgnoreCase(fielditem.getName())) {
                item=fielditem;
            }
		}

		if ("D".equals(fields[1])) {

			if (item != null ) {
				field.append(" case when " + fields[0] + " is null then ''");
				if ("19".equals(fields[2])) {
					 field.append(" when "+Sql_switcher.month(fields[0])+">9 then ");
				     field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
					 field.append(getString() + "'.'" + getString());
					 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
					 field.append(" else  ");
					 field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
					 field.append(getString() + "'.0'" + getString());
					 field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
				}else{
					field.append(" else ");
					if ("6".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
	
					} else if ("7".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
	
					} else if ("8".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
					} else if ("9".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'.'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
	
					} else if ("10".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
					} else if ("11".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
					} else if ("12".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "'"+ getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.day(fields[0])));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.day")+ "' ");
					} else if ("13".equals(fields[2])) {
						field.append(Sql_switcher.substr(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])), "3","2"));
						field.append(getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'" + getString());
						field.append(Sql_switcher.numberToChar(Sql_switcher.month(fields[0])));
						field.append(getString()+ "'"+ ResourceFactory.getProperty("hmuster.label.month") + "' ");
	
					} else if ("14".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "' '" + getString() + "'"+ ResourceFactory.getProperty("hmuster.label.year")+ "'");
					} else if ("15".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
					} else if ("16".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
					} else if ("17".equals(fields[2])) {
						field.append(Sql_switcher.numberToChar(Sql_switcher.year(fields[0])));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.month(fields[0]))));
						field.append(getString() + "'.'" + getString());
						field.append(dateStr(Sql_switcher.numberToChar(Sql_switcher.day(fields[0]))));
					}else {
						field.append(fields[0]);
					}
				}
				if (fields[0].indexOf(".") == -1) {
                    field.append(" end as " + fields[0]+"_time");
                } else {
					field.append(" end as "+ fields[0].substring(fields[0].indexOf(".") + 1)+"_time");

				}

			} else {
				field.append("null ");//as "+aField
			}
		} else {

			if(item!=null) {
                field.append(aField);
            } else {
                field.append("null ");//as "+aField
            }

		}
		return field.toString();
	}
	
//	取sql得到连接符
	 public  String getString()
    {
    	String operate="";
   	switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  operate="+";
		  	break;
		  }
		  case Constant.DB2:
		  {
			  operate="+";
		  	break;
		  }
		  case Constant.ORACEL:
		  {
			  operate="||";
		  	break;
		  }
		}
   	return operate;
    }
	
	
	
	
	
	
	
	/**
	 * 取得条件sql
	 * @param dbpre
	 * @param operate   0:无条件  1：按年查询   2：按月查询  3 按季度查询  4 按时间段查询
	 * @param year
	 * @param month
	 * @param quarter
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public String getWhl(String setname,String dbpre,String operate,String year,String month,String quarter,String startDate,String endDate )
	{
		StringBuffer whl=new StringBuffer("");
		if(!"0".equals(operate))
		{
		    String tabname = isPersonView(setname) ? setname : dbpre+setname;
			String column = tabname+"."+setname+"Z0";
			if("1".equals(operate)&&year!=null&&year.trim().length()>0)
			{
				whl.append(" and "+Sql_switcher.year(column)+"="+year);
			}
			else if("2".equals(operate))
			{
				if(year!=null&&year.trim().length()>0) {
                    whl.append(" and "+Sql_switcher.year(column)+"="+year);
                }
				
				whl.append(" and "+Sql_switcher.month(column)+"="+month);
			}
			else if("3".equals(operate))
			{
				if(year!=null&&year.trim().length()>0) {
                    whl.append(" and "+Sql_switcher.year(column)+"="+year);
                }
				if("1".equals(quarter))
				{
					whl.append(" and ( "+Sql_switcher.month(column)+"=1");
					whl.append(" or "+Sql_switcher.month(column)+"=2");
					whl.append(" or "+Sql_switcher.month(column)+"=3 ) ");
				}
				else if("2".equals(quarter))
				{
					whl.append(" and ( "+Sql_switcher.month(column)+"=4");
					whl.append(" or "+Sql_switcher.month(column)+"=5");
					whl.append(" or "+Sql_switcher.month(column)+"=6 ) ");
				}
				else if("3".equals(quarter))
				{
					whl.append(" and ( "+Sql_switcher.month(column)+"=7");
					whl.append(" or "+Sql_switcher.month(column)+"=8");
					whl.append(" or "+Sql_switcher.month(column)+"=9 ) ");
				}
				else if("4".equals(quarter))
				{
					whl.append(" and ( "+Sql_switcher.month(column)+"=10");
					whl.append(" or "+Sql_switcher.month(column)+"=11");
					whl.append(" or "+Sql_switcher.month(column)+"=12 ) ");
				}
				
			}
			else if("4".equals(operate))
			{
				if(startDate!=null&&startDate.trim().length()>0)
				{
					startDate=startDate.replaceAll("\\.","-");
					whl.append(" and ");
					whl.append(getDataValue(column,">=",startDate));
				}
				if(endDate!=null&&endDate.trim().length()>0)
				{
					endDate=endDate.replaceAll("\\.","-");
					whl.append(" and ");
					whl.append(getDataValue(column,"<=",endDate));
				}
			}
		}
		return whl.toString();
	}
	
	
	
	

	public String getDataValue(String fielditemid,String operate,String value)
	{
		StringBuffer a_value=new StringBuffer("");		
		try
		{

				if("=".equals(operate))
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
					a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
					a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(" ) ");
				}
				else 
				{
					a_value.append("(");
					if(">=".equals(operate))
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+" ) or ( ");						
					}
					else if("<=".equals(operate))
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+"<"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"<"+value.substring(5,7)+" ) or ( ");						
					}
					else
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
						
					}
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(") ) ");
				}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_value.toString();
	}
	
	
	
	
	
	
	
	/**
	 * 取得花名册不用计算的数据字段
	 * @param tabid
	 * @return
	 */
	public ArrayList getMusterCellList(String tabid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			rowSet=dao.search("select GridNo,SetName,Field_Name,Field_Type,Flag,codeid,slope from muster_cell where flag!='E' and flag!='G' and flag!='R' and flag!='H'  and flag!='C' and flag!='S' and flag!='P'  and tabid="+tabid);	
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("gridno",rowSet.getString("gridno"));
				abean.set("setname",rowSet.getString("SetName"));
				abean.set("fieldname",rowSet.getString("Field_Name"));
				abean.set("fieldtype",rowSet.getString("Field_Type"));
				String codeid = rowSet.getString("codeid");
				codeid=codeid!=null&&codeid.trim().length()>0?codeid:"0";
				abean.set("codeid",codeid);
				abean.set("flag",rowSet.getString("Flag"));
				abean.set("slope",rowSet.getString("slope"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
		
	}
	
	/**
	 * 判断花名册是否是年月变化子集
	 * @param tabid
	 * @param yearList
	 * @return
	 */
	public boolean isTimeIdentifine(String tabid,ArrayList yearList,String dbpre,String a0100)throws GeneralException 
	{
		boolean flags=false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			ArrayList musterCellList=getMusterCellList(tabid);
			HashMap setMap=new HashMap();
			setMap.put("A01","1");
			String setname="";
			for(Iterator t=musterCellList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String a_setname=((String)abean.get("setname")).toUpperCase();
				if(a_setname.trim().length()>0)
				{
					setMap.put(a_setname,"1");
					if(!"A01".equals(a_setname)) {
                        setname=a_setname;
                    }
				}
			}
			if(setMap.size()>2) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.pay.only.item")+"！"));
            }
			if(setname!=null&&setname.trim().length()>0)
			{
				FieldSet vo=DataDictionary.getFieldSetVo(setname.toLowerCase());
				if(!"0".equals(vo.getChangeflag())) {
                    flags=true;
                }
			}
			if(flags)
			{
				ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.conn);
				String b0110=managePrivCode.getB0110FromA0100(a0100,dbpre);		 
				if(b0110==null||b0110.length()<=0)
				{
					b0110=managePrivCode.getPrivOrgId();  
				}	
				XmlParameter xml=new XmlParameter("UN",b0110,"00");
				xml.ReadOutParameterXml("SS_SETCARD",conn,"all");	
				String year_restrict=xml.getYear_restrict();
				StringBuffer sql= new StringBuffer();
				//我的薪酬列表方式默认与表格方式一致，设置musterFlag=infoself参数先查薪资发放时间, musterFlag=statCount 默认系统当前时间
				sql.append("select "+Sql_switcher.dateToChar("max(a00z0)")+" a00z0 from (");
	          	sql.append("select max(a00z0) as a00z0 from SalaryHistory ");
	          	if(a0100!=null&&a0100.length()>0)
	          	{
	          		sql.append("where a0100='"+a0100+"' and lower(nbase)='"+dbpre.toUpperCase()+"'");
	          	}
	          	sql.append(" union select max(a00z0) as a00z0 from salaryarchive ");
	          	if(a0100!=null&&a0100.length()>0)
	          	{
	          		sql.append("where a0100='"+a0100+"' and lower(nbase)='"+dbpre.toUpperCase()+"'");
	          	}
	          	sql.append(")T ");
				/*sql.append("select DISTINCT ");
				sql.append(Sql_switcher.year(setname+"Z0"));
				sql.append(" from ");
				if(isPersonView(setname))
				    sql.append(setname);
				else
				    sql.append(dbpre+setname);
				sql.append(" where a0100='");
				sql.append(a0100);
				sql.append("'");
				if(isPersonView(setname))
				    sql.append(" and NBASE = '" + dbpre.toUpperCase() + "'");
				if(year_restrict!=null&&year_restrict.trim().length()==4){
					sql.append(" and ");
					sql.append(Sql_switcher.year(setname+"Z0"));
					sql.append(">=");
					sql.append(year_restrict);
				}
				sql.append(" order by "+Sql_switcher.year(setname+"Z0")+" desc ");*/
				rowSet=dao.search(sql.toString());
				String date="";
				while(rowSet.next())
				{
					date=rowSet.getString(1);
				}
				SimpleDateFormat sdf=null;
				int year=0;
				Calendar cal=Calendar.getInstance();
				
				if(StringUtils.isNotEmpty(date)){
					if(date.length()<11){
						sdf=new SimpleDateFormat("yyyy-MM-dd");
						
					}else{
						sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					}
					cal.setTime(sdf.parse(date));
					year=cal.get(Calendar.YEAR);
				}else{
					year=cal.get(Calendar.YEAR);
				}
				CommonData data1=new CommonData(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE),
						cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE));
				yearList.add(data1);
				for (int i = 0; i < 10; i++) {
					if(year_restrict!=null&&year_restrict.trim().length()==4&&(year-i<Integer.parseInt(year_restrict))) {
                        break;
                    }
					CommonData data=new CommonData((year-i)+"",(year-i)+"");
					yearList.add(data);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return flags;
	}
	
	
	
	
	
	
	/**
	 * 根据用户名及花名册id创建临时高级花名册表
	 * 
	 * @param username
	 *            用户名称
	 * @param tabid
	 *            高级花名册id
	 * @author dengc created:2006/03/21
	 */

	public Table createMusterTempTable(String tabid,DbWizard dbWizard,int nModule,HashMap cFactoryMap,MusterBo mbo)
			throws GeneralException {
		String tablename = "";
		if(getTemptable()!=null&&getTemptable().trim().length()>0) {
            tablename = getTemptable();
        } else {
            tablename = userView.getUserName().trim().replaceAll(" ", "")+"_muster_"+tabid;
        }
		Table table = new Table(tablename);		
		ContentDAO dao = new ContentDAO(this.conn);		
		RowSet rowset = null;
		try {
			ArrayList fieldlist = null;
			if(nModule==15||nModule==11) {
                fieldlist=getSpMusterFields(tabid,nModule,cFactoryMap,mbo);
            } else {
                fieldlist=getMusterFields(tabid,nModule,cFactoryMap,mbo);
            }
			table.setCreatekey(false);
			ArrayList fields=new ArrayList();
			for (int i = 0; i < fieldlist.size(); i++)
			{
				table.addField((Field) fieldlist.get(i));
				fields.add(((Field) fieldlist.get(i)).getName().toLowerCase()+"/"+((Field) fieldlist.get(i)).getDatatype());
			}
			if (dbWizard.isExistTable(table.getName(),false))
			{
				rowset=dao.search("select * from "+tablename+" where 1=2");
				ResultSetMetaData meta=rowset.getMetaData();
				boolean isEdited=false;
				HashMap tableColumn=new HashMap();
				for(int i=0;i<meta.getColumnCount();i++)
				{
					
					String tempName=meta.getColumnName(i+1).toLowerCase();
					tableColumn.put(tempName,"1/"+meta.getColumnType(i+1));
				
				}
				for(int i=0;i<fields.size();i++)
				{
					String temp=(String)fields.get(i);
					String[] temps=temp.split("/");
					if(tableColumn.get(temps[0])==null)
					{
						isEdited=true;
						break;
					} 
					else
					{
						String[] dd=((String)tableColumn.get(temps[0])).split("/");
						if("12".equals(dd[1])&&!temps[1].equals(String.valueOf(DataType.STRING)))
						{
							isEdited=true;
							break;
						}
						else if(("6".equals(dd[1])|| "8".equals(dd[1])|| "4".equals(dd[1]))&&!temps[1].equals(String.valueOf(DataType.FLOAT)))
						{
							isEdited=true;
							break;
						}
						
					}
				}
//				if(isEdited)
//				{
					dbWizard.dropTable(table);
					dbWizard.createTable(table);
//				}
			}
			else {
                dbWizard.createTable(table);
            }
			//dbWizard.addPrimaryKey(table);
			
		} catch (Exception ex) {
			
			throw GeneralExceptionHandler.Handle(ex);
		} finally{
			try
			{
				if(rowset!=null) {
                    rowset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return table;
	}
	
	
	/**
	 * 取得花名册结构指标项列表
	 * 
	 * @param tabid
	 *            flag:信息集
	 * @param nModule  模块标志
	 * @return
	 */
	public ArrayList getMusterFields(String tabid,int nModule,HashMap cFactorMap,MusterBo mbo)
			throws GeneralException {
	
		this.fieldsList =getMusterFixupFields(tabid,"A",nModule,cFactorMap,mbo);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select GridNo,SetName,Field_Name,Field_Type,Flag,codeid from muster_cell ");
		strsql.append("where flag!='R' and flag!='H' and flag!='S' and flag!='P' and tabid=");
		strsql.append(tabid);
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(strsql.toString());
			HashMap setMap=new HashMap();
			setMap.put("A01","1");
			HashMap itemMap=null;
			if(salaryid!=null&&salaryid.length()>0)
			{
				SalarySetItemBo salsetbo = new SalarySetItemBo();
				itemMap = salsetbo.fieldItemMap(conn,salaryid,userView);
			}
			while (rset.next()) {
				String fieldname = rset.getString("Field_Name");
				String aSetName = rset.getString("SetName");
				if(aSetName!=null&&aSetName.trim().length()>0&&!"A00".equalsIgnoreCase(aSetName)) {
                    setMap.put(aSetName.toUpperCase(),"1");
                }
				String type = rset.getString("Field_Type");
				String gridNo = rset.getString("GridNo");
				String flags = rset.getString("Flag");
				String codeid=rset.getString("codeid");
				codeid=(codeid==null|| "".equals(codeid.trim()))?"0":codeid;
				if (fieldname != null && !"".equals(fieldname)) {

					/* 判断该指标是否已被删除或还没构库 */
					if(salaryid!=null&&salaryid.length()>0)
					{
						Field field = (Field)itemMap.get(fieldname.toUpperCase());
						if (field == null)
						{
							throw GeneralExceptionHandler.Handle(new Exception(fieldname+musterName+ResourceFactory.getProperty("workdiary.message.roster.del")));	
						}
					}
					else
					{
						FieldItem field=DataDictionary.getFieldItem(fieldname.toLowerCase());
						if (field == null)
						{
							throw GeneralExceptionHandler.Handle(new Exception(fieldname+musterName+ResourceFactory.getProperty("workdiary.message.roster.del")));	
						}
						
					}
					
					
					Field obj = new Field("C" + gridNo, "C" + gridNo);
					if ("A".equals(type)) {
						obj.setDatatype(DataType.STRING);
						obj.setVisible(false);
						if(!"0".equals(codeid))
						{
							if("UN".equalsIgnoreCase(codeid)|| "UM".equalsIgnoreCase(codeid)|| "@K".equalsIgnoreCase(codeid)) {
                                obj.setLength(mbo.getOrgdesc_length());
                            } else {
                                obj.setLength(mbo.getCodedesc_length());
                            }
						}
						else
						{
					    	obj.setLength(255);
						}
					 	obj.setAlign("left");

					} else if ("M".equals(type)) {
						obj.setDatatype(DataType.CLOB);					
						obj.setVisible(false);
						obj.setAlign("left");
					}else if ("D".equals(type)) {
						obj.setDatatype(DataType.STRING);
						obj.setLength(20);						
						obj.setVisible(false);
						obj.setFormat("yyyy.MM.dd");
						obj.setAlign("right");
					} else if ("N".equals(type)) {
						obj.setDatatype(DataType.FLOAT);
						obj.setDecimalDigits(4);
						obj.setLength(15);						
						obj.setVisible(false);
						obj.setAlign("left");

					}					
					fieldsList.add(obj);
				} else {

					if ("C".equals(flags)|| "G".equals(flags)) {
						Field temp3 = new Field("C" + gridNo, ResourceFactory.getProperty("hmuster.label.expressions"));
						if("G".equals(flags)) {
                            type="A";
                        }
						if("N".equals(type))
						{
							temp3.setDatatype(DataType.FLOAT);
							temp3.setDecimalDigits(4);
							temp3.setLength(15);							
							temp3.setVisible(false);
						}
						else if("A".equals(type)|| "M".equals(type))
						{
							temp3.setDatatype(DataType.STRING);							
							temp3.setVisible(false);
							if("A".equalsIgnoreCase(type))
							{
								if(codeid!=null&&!"0".equals(codeid))
								{
									if("UN".equalsIgnoreCase(codeid)|| "UM".equalsIgnoreCase(codeid)|| "@K".equalsIgnoreCase(codeid)) {
                                        temp3.setLength(mbo.getOrgdesc_length());
                                    } else {
                                        temp3.setLength(mbo.getCodedesc_length());
                                    }
										
								}
								else
								{
									temp3.setLength(255);
								}
							}else
							{
					    		temp3.setLength(255);
							}
							temp3.setAlign("left");
						}
						else if("D".equals(type))
						{
							temp3.setDatatype(DataType.STRING);
							temp3.setLength(20);							
							temp3.setVisible(false);
							temp3.setFormat("yyyy.MM.dd");
							temp3.setAlign("right");
						}
						fieldsList.add(temp3);
					}
				}
			}
			if(setMap.size()>2) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.conant.only.item")+"！"));
            }
			

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldsList;
	}
	/**
	 * 取得花名册结构指标项列表
	 * 
	 * @param tabid
	 *            flag:信息集
	 * @param nModule  模块标志
	 * @return
	 */
	public ArrayList getSpMusterFields(String tabid,int nModule,HashMap cFactoryMap,MusterBo mbo)
			throws GeneralException {
	
		this.fieldsList =getMusterFixupFields(tabid,"A",nModule,cFactoryMap,mbo);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select GridNo,SetName,Field_Name,Field_Type,Slope,Flag,Field_Hz from muster_cell where flag!='G' and flag!='R' and flag!='H' and flag!='S' and flag!='P'  and tabid=");
		strsql.append(tabid);
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(strsql.toString());	
			HashMap setMap=new HashMap();
			setMap.put("A01","1");
			while (rset.next()) {
				String fieldname = rset.getString("Field_Name")!=null?rset.getString("Field_Name"):"";
				String aSetName = rset.getString("SetName")!=null?rset.getString("SetName"):"";
				if(aSetName!=null&&aSetName.trim().length()>0&&!"A00".equalsIgnoreCase(aSetName)) {
                    setMap.put(aSetName.toUpperCase(),"1");
                }
				String type = rset.getString("Field_Type");
				String gridNo = rset.getString("GridNo");
				String flags = rset.getString("Flag");
				String Field_Hz = rset.getString("Field_Hz");
				int Slope = rset.getInt("Slope");
				
				int length=15;
				int dewidth=4;

				if("A00Z1".equalsIgnoreCase(fieldname)){
					length=10;
					dewidth=0;
				}
				if (fieldname != null && !"".equals(fieldname)) {

					/* 判断该指标是否已被删除或还没构库 */
//					FieldItem item = DataDictionary.getFieldItem(fieldname);
//					if (item == null||item.getUseflag().equals("0"))
//					{
//						throw GeneralExceptionHandler.Handle(new Exception(musterName+"  花名册中 "+fieldname+" 指标已被删除或还没构库"));	
//					}
//					
//					Field obj = new Field("C" + gridNo, item.getItemdesc());
					
					FieldItem item = DataDictionary.getFieldItem(fieldname);
					if (item == null) {
						Field a_temp = new Field("C" + gridNo, fieldname);
						item=(FieldItem)this.midvariableMap.get(fieldname.toUpperCase());
						if(item!=null){
							length = item.getItemlength();
							dewidth = item.getDecimalwidth();
						}
						if(nModule==15)
						{
							
							if ("A".equals(type)) {
									if(item!=null&& "0".equals(item.getCodesetid())) {
                                        a_temp.setLength(item.getItemlength());
                                    } else
									{
										if(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))) {
                                            a_temp.setLength(mbo.getOrgdesc_length());
                                        } else if(item!=null&&!"0".equals(item.getCodesetid())) {
                                            a_temp.setLength(mbo.getCodedesc_length());
                                        } else {
                                            a_temp.setLength(60);
                                        }
									}
									a_temp.setDatatype(DataType.STRING);
									a_temp.setVisible(false);
									a_temp.setAlign("left");

							} else if ("M".equals(type)) {
								a_temp.setDatatype(DataType.CLOB);					
								a_temp.setVisible(false);
								a_temp.setAlign("left");
							}else if ("D".equals(type)) {
								a_temp.setDatatype(DataType.STRING);
								a_temp.setLength(20);						
								a_temp.setVisible(false);
								a_temp.setFormat("yyyy.MM.dd");
								a_temp.setAlign("right");
							} else if ("N".equals(type)) {
								if(Slope>0){
									a_temp.setDatatype(DataType.FLOAT);
									a_temp.setDecimalDigits(Slope);
									a_temp.setLength(length);
								}else{
									a_temp.setDatatype(DataType.INT);
								}			
								a_temp.setVisible(false);
								a_temp.setAlign("left");

					    	}
						}
						else
						{
				    		if("N".equalsIgnoreCase(type)){
					    		if(Slope<1) {
                                    a_temp.setDatatype(DataType.INT);
                                } else{
						    		a_temp.setDatatype(DataType.FLOAT);
						    		a_temp.setDecimalDigits(Slope);
						    		a_temp.setLength(15);
					    		}
					    	}else if("M".equalsIgnoreCase(type)){
					    		a_temp.setDatatype(DataType.CLOB);
					    	}else {
					    		a_temp.setDatatype(DataType.STRING);
					    		if(item!=null&& "0".equals(item.getCodesetid())) {
                                    a_temp.setLength(item.getItemlength());
                                } else
								{
									if(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))) {
                                        a_temp.setLength(mbo.getOrgdesc_length());
                                    } else if(item!=null&&!"0".equals(item.getCodesetid())) {
                                        a_temp.setLength(mbo.getCodedesc_length());
                                    } else {
                                        a_temp.setLength(255);
                                    }
								}
					    	}
					    	a_temp.setVisible(false);
					    	a_temp.setAlign("left");
						}
						fieldsList.add(a_temp);
						continue;
					}
					
					
					if(item!=null){
						length = item.getItemlength();
						dewidth = item.getDecimalwidth();
					}
					Field obj = new Field("C" + gridNo, Field_Hz);
					if ("A".equals(type)) {
						if(item!=null&& "0".equals(item.getCodesetid())) {
                            obj.setLength(item.getItemlength());
                        } else
						{
							if(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))) {
                                obj.setLength(mbo.getOrgdesc_length());
                            } else if(item!=null&&!"0".equals(item.getCodesetid())) {
                                obj.setLength(mbo.getCodedesc_length());
                            } else {
                                obj.setLength(60);
                            }
						}
							obj.setDatatype(DataType.STRING);
							obj.setVisible(false);
							obj.setAlign("left");

					} else if ("M".equals(type)) {
						obj.setDatatype(DataType.CLOB);					
						obj.setVisible(false);
						obj.setAlign("left");
					}else if ("D".equals(type)) {
						obj.setDatatype(DataType.STRING);
						obj.setLength(20);						
						obj.setVisible(false);
						obj.setFormat("yyyy.MM.dd");
						obj.setAlign("right");
					} else if ("N".equals(type)) {
						if(Slope>0){
							obj.setDatatype(DataType.FLOAT);
							obj.setDecimalDigits(Slope);
							obj.setLength(length);		
						}else{
							obj.setDatatype(DataType.INT);
						}			
						obj.setVisible(false);
						obj.setAlign("left");

					}					
					fieldsList.add(obj);
				} else {

					if ("C".equals(flags)) {
						Field temp3 = new Field("C" + gridNo, ResourceFactory.getProperty("hmuster.label.expressions"));
						
						if("N".equals(type))
						{
							if(Slope>0){
								temp3.setDatatype(DataType.FLOAT);
								temp3.setDecimalDigits(8/*Slope*/);  // 同CS
								temp3.setLength(length);	
							}else{
								temp3.setDatatype(DataType.INT);
							}					
							temp3.setVisible(false);
						}
						else if("A".equals(type)|| "M".equals(type))
						{
							temp3.setDatatype(DataType.STRING);							
							temp3.setVisible(false);
							temp3.setLength(255);
							temp3.setAlign("left");
						}
						else if("D".equals(type))
						{
							temp3.setDatatype(DataType.STRING);
							temp3.setLength(20);							
							temp3.setVisible(false);
							temp3.setFormat("yyyy.MM.dd");
							temp3.setAlign("right");
						}
						fieldsList.add(temp3);
					}
				}
			}
//			if(setMap.size()>2)
//				throw GeneralExceptionHandler.Handle(new Exception("花名册中只可以包含一个子集的指标！"));
			

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fieldsList;
	}
	

	/**
	 * 取得花名册结构固定指标项列表
	 * 
	 * @param tabid
	 * @param nModule 1: 保险台帐,
	 				  2: 合同台帐,
	 				  3: 人员(忽略),
	 				  4: 工资,
	 				  5: 调资模板,
	 				  6: 工资分析,
	 				  7: 报批花名册,
	 				  21: 机构花名册(忽略),
	 				  41: 职位名册(忽略),
	 				  61: 培训名册,
	 				  81: 考勤 
	 * @return
	 */
	public ArrayList getMusterFixupFields(String tabid, String flag,int nModule,HashMap cFactorMap,MusterBo mbo) {
		ArrayList list = new ArrayList();

		Field a_temp=new Field("recidx",ResourceFactory.getProperty("recidx.label"));
		a_temp.setNullable(false);
		a_temp.setKeyable(true);
		a_temp.setDatatype(DataType.INT);
		a_temp.setSortable(true);	
		list.add(a_temp);
		
		if(nModule==3||nModule==4||nModule==15||nModule==6||nModule==11)
		{
			Field temp0 = new Field("NBASE", ResourceFactory.getProperty("popedom.db"));
			temp0.setDatatype(DataType.STRING);						
			temp0.setVisible(false);
			temp0.setLength(50);
			list.add(temp0);
			
			
			if(nModule==15||nModule==11){
				Field temp1 = new Field("salaryId","工资类别号");
				temp1.setDatatype(DataType.INT);						
				temp1.setVisible(false);
				list.add(temp1);
			}
			
			if(nModule==15){
				Field temp2 = new Field("tax_max_id","个税编号");
				temp2.setDatatype(DataType.INT);						
				temp2.setVisible(false);
				list.add(temp2);
			}

			Field temp01 = new Field("A00Z0", ResourceFactory.getProperty("gz.columns.a00z0"));
			temp01.setDatatype(DataType.DATE);						
			temp01.setVisible(false);
			list.add(temp01);
			
			Field temp02 = new Field("A00Z1",ResourceFactory.getProperty("gz.columns.a00z1"));
			temp02.setDatatype(DataType.INT);						
			temp02.setVisible(false);
			list.add(temp02);
			if(nModule==15||nModule==11){//添加发放日期与发放次数
				Field temp03 = new Field("A00Z2", "发放日期");
				temp03.setDatatype(DataType.DATE);						
				temp03.setVisible(false);
				list.add(temp03);
				
				Field temp04 = new Field("A00Z3","发放次数");
				temp04.setDatatype(DataType.INT);						
				temp04.setVisible(false);
				list.add(temp04);
			}
			
			Field temp = new Field("A0000", ResourceFactory.getProperty("hmuster.label.innerSerial"));
			temp.setDatatype(DataType.INT);			
			temp.setVisible(false);	
			list.add(temp);
			
			
			Field temp2 = new Field("A0100", ResourceFactory.getProperty("hmuster.label.machineNo"));
			temp2.setDatatype(DataType.STRING);						
			temp2.setVisible(false);
			temp2.setLength(8);
			list.add(temp2);
			
			Field temp3 =null;
			temp3=new Field("B0110", ResourceFactory.getProperty("hmuster.label.unitNo"));			
			temp3.setDatatype(DataType.STRING);			
			temp3.setVisible(false);
			temp3.setLength(mbo.getOrg_length());
			list.add(temp3);

			Field temp4 =null;			
			temp4=new Field("E0122", ResourceFactory.getProperty("hmuster.label.departmentNo"));			
			temp4.setDatatype(DataType.STRING);
			temp4.setVisible(false);
			temp4.setLength(mbo.getOrg_length());
			list.add(temp4);
			
			Field temp5 = new Field("ext", ResourceFactory.getProperty("hmuster.label.ext"));
			temp5.setDatatype(DataType.STRING);
			temp5.setVisible(false);
			temp5.setLength(8);
			list.add(temp5);
			
		} 
		
		Field temp21 = new Field("I9999", ResourceFactory.getProperty("hmuster.label.no"));
		temp21.setDatatype(DataType.INT);
		temp21.setVisible(false);
		list.add(temp21);
		String groupPoint = (String)cFactorMap.get("groupN");
		String groupPoint2=(String)cFactorMap.get("groupN2");

		Field temp31 = new Field("GroupN", ResourceFactory.getProperty("hmuster.label.groupValue"));
		temp31.setDatatype(DataType.STRING);
		temp31.setVisible(false);
		if(groupPoint!=null&&groupPoint.length()>0)
		{
			FieldItem item = DataDictionary.getFieldItem(groupPoint.toLowerCase());
	    	if(item!=null)
	    	{
	    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                    temp31.setLength(mbo.getOrg_length());
                } else if(item.isCode()) {
                    temp31.setLength(mbo.getCode_length());
                } else {
                    temp31.setLength(item.getItemlength());
                }
	    	}else{
	    		temp31.setLength(30);
	    	}
		}else{
			temp31.setLength(30);
		}
		list.add(temp31);
	
		Field temp41 = new Field("GroupV", ResourceFactory.getProperty("hmuster.label.groupName"));
		temp41.setDatatype(DataType.STRING);
		temp41.setVisible(false);
		if(groupPoint!=null&&groupPoint.trim().length()>0)
		{
	    	FieldItem item = DataDictionary.getFieldItem(groupPoint.toLowerCase());
	    	if(item!=null)
	    	{
	    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                    temp41.setLength(mbo.getOrgdesc_length());
                } else if(item.isCode()) {
                    temp41.setLength(mbo.getCodedesc_length());
                } else {
                    temp41.setLength(item.getItemlength());
                }
	    	}else{
	    		temp41.setLength(50);
	    	}
		}else
		{
			temp41.setLength(50);
		}
		list.add(temp41);
		/**新加双分组指标*/
		Field temp32 = new Field("GroupN2", ResourceFactory.getProperty("hmuster.label.groupValue"));
		temp32.setDatatype(DataType.STRING);
		temp32.setVisible(false);
		if(groupPoint2!=null&&groupPoint2.trim().length()>0)
		{
	    	FieldItem item = DataDictionary.getFieldItem(groupPoint2.toLowerCase());
	    	if(item!=null)
	    	{
	    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                    temp32.setLength(mbo.getOrg_length());
                } else if(item.isCode()) {
                    temp32.setLength(mbo.getCode_length());
                } else {
                    temp32.setLength(item.getItemlength());
                }
	    	}else{
	    		temp32.setLength(30);
	    	}
		}else
		{
			temp32.setLength(30);
		}
		list.add(temp32);

		Field temp42 = new Field("GroupV2", ResourceFactory.getProperty("hmuster.label.groupName"));
		temp42.setDatatype(DataType.STRING);
		temp42.setVisible(false);
		if(groupPoint2!=null&&groupPoint2.trim().length()>0)
		{
	    	FieldItem item = DataDictionary.getFieldItem(groupPoint2.toLowerCase());
	    	if(item!=null)
	    	{
	    		if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                    temp42.setLength(mbo.getOrgdesc_length());
                } else if(item.isCode()) {
                    temp42.setLength(mbo.getCodedesc_length());
                } else {
                    temp42.setLength(item.getItemlength());
                }
	    	}else{
	    		temp42.setLength(50);
	    	}
		}else
		{
			temp42.setLength(50);
		}
		list.add(temp42);
		if(nModule==15)
		{
			Field temp43 = new Field("declare_tax", "declare_tax");
			temp43.setDatatype(DataType.DATE);						
			temp43.setVisible(false);
			list.add(temp43);
		}
		return list;

	}
	/**
	 * 将时间转换成sql语句
	 * @param tablename  库前缀
	 * @param datatime  日期
	 * @param num 次
	 * @return
	 */
	private String dataWhere(String tablename,String datatime,String num,String salaryid){
		StringBuffer buf = new StringBuffer("");
		//liuy 2015-5-15 9595：朝阳卫生：13号帐套  平房工资 报批给 区局， 区局登录，薪资审批-用户自定义报表，取不出来数据了，不对。 begin
		if(StringUtils.equals(this.model,"3")||StringUtils.equals(this.model,"4")){//A00Z0归属日期A00z1归属次数，薪资历史控制归属日期
			WorkdiarySQLStr wss=new WorkdiarySQLStr();
			String tempend=wss.getDataValue(tablename+".A00Z0","=",datatime.replace(".","-"));
			buf.append(tempend);
			if(!"0".equals(this.getModel())) {
                buf.append(" and "+tablename+".A00Z3="+num); //薪资历史数据按照次数 不应按照归属次数
            }
		}else  if(this.model!=null&& "1".equals(this.model)){//A00z2发放日期A00z3发放次数，薪资发放不控制发放日期
			buf.append(tablename+".A00Z3="+num);
	    	/*buf.append(" and (( "+tablename+".curr_user='");
    		buf.append(this.userView.getUserId());
     		buf.append("' and ( "+tablename+".sp_flag='02' or ");
     		buf.append(tablename+".sp_flag='07' ) ) ");
	    	if(this.userView.isSuper_admin()){
	    		buf.append(" or "+tablename+".sp_flag='06' or "+tablename+".sp_flag='03' or ");
	    		buf.append(tablename+".sp_flag is null) ");
	    	}else{
	    		buf.append(" or ( ( ");
	    		buf.append(tablename+".AppUser is null or "+tablename+".AppUser Like '%;");
	    		buf.append(this.userView.getUserName());
	    		buf.append(";%' ) and  ( "+tablename+".sp_flag='06' or "+tablename+".sp_flag='03')) ) ");
	    	}*/

			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn, Integer.parseInt(salaryid), this.userView);

			buf.append(" and (( "+tablename+".curr_user='"+this.userView.getUserId()+"' ) or " +
					"( ( ("+tablename+".AppUser is null  "+gzbo.getPrivWhlStr(tablename)+"  ) or "+tablename+".AppUser Like '%;"+this.userView.getUserName()+";%' ) ) ) ");
		}
		return buf.toString();
	}
    public void getPrivSQLStr(String info_flag,UserView view,String dbpre,String fixpre)
    {
    	RowSet rs = null;
    	try
    	{
    		StringBuffer sql = new StringBuffer("");
			String priStrSql = InfoUtils.getWhereINSql(view, dbpre);
			sql.append("select "+dbpre+"a01.A0100 ");
			if (priStrSql.length() > 0) {
                sql.append(priStrSql);
            } else {
                sql.append(" from "+dbpre+"a01");
            }
			ContentDAO dao = new ContentDAO(this.conn);
		    rs = dao.search(sql.toString());
			StringBuffer sb = new StringBuffer("");
			while(rs.next())
			{
				sb.append(" OR "+fixpre+".A0100='"+rs.getString("a0100")+"'");
			}
			if(rs!=null) {
                rs.close();
            }
			String str="";
			if(sb.toString().length()>0) {
                str=sb.toString().substring(3);
            }
			this.setPrivSQL(str);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
    }
    public Document getDoc()
	{
		Document doc=null;
		try
		{
			RecordVo ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", conn);
			if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
			{
				doc = PubFunc.generateDom(ctrlvo.getString("str_value"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return doc;
		
	}
    /***
	 * 取得是否支持按隶属部门进行所得税管理
	 * @return
	 */
	public String getDeptID()
	{
		String deptid="false";
		Document doc = this.getDoc();
		try
		{
			if(doc!=null)
			{
				String path ="/param";
				XPath xpath = XPath.newInstance(path);
				Element items = (Element)xpath.selectSingleNode(doc);
				if(items.getAttributeValue("deptid")!=null&&items.getAttributeValue("deptid").trim().length()>0)
				{
					deptid=items.getAttributeValue("deptid");
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return deptid;
	}
	public String getPrivPre(String filterByMdule,String dbpre)
	{
		StringBuffer pre=new StringBuffer("");
		try
		{
			/**是否按隶属部门来控制*/
			String deptid=this.getDeptID();
			if("false".equalsIgnoreCase(deptid))
			{
				/**不按模块权限*/
				if("0".equals(filterByMdule))//changxy 20160920 [22960] filterByMdule 为空时不可用filterByMdule.enqules("0")
				{
			        /**加入高级授权*/
				    if("ALL".equals(dbpre)) {
		                ArrayList a_list=this.userView.getPrivDbList();
		                if(a_list==null||a_list.size()==0) {
		                    pre.append("1=2");
		                }
		                else {
		                    for(int i=0;i<a_list.size();i++){
		                        String db=(String)a_list.get(i);
		                        StringBuffer sql = new StringBuffer("");
		                        String priStrSql = InfoUtils.getWhereINSql(this.userView, db);
		                        sql.append("select "+db+"a01.A0100 ");
		                        if (priStrSql.length() > 0) {
                                    sql.append(priStrSql);
                                } else {
                                    sql.append(" from "+db+"a01");
                                }
                                if(i!=0) {
                                    pre.append(" or ");
                                }
		                        pre.append("(upper(nbase)='");
		                        pre.append(db.toUpperCase()+"'");
		                        pre.append(" and a0100 in ("+sql.toString()+"))");
		                    }
		                }
				    }
				    else {
                        StringBuffer sql = new StringBuffer("");
                        String priStrSql = InfoUtils.getWhereINSql(this.userView, dbpre);
                        sql.append("select "+dbpre+"a01.A0100 ");
                        if (priStrSql.length() > 0) {
                            sql.append(priStrSql);
                        } else {
                            sql.append(" from "+dbpre+"a01");
                        }
                        
                        pre.append("(upper(nbase)='");
                        pre.append(dbpre.toUpperCase()+"'");
                        pre.append(" and a0100 in ("+sql.toString()+"))");
				    }
			    }
				/**按模块权限*/
				else
				{
					/*if(this.userView.isSuper_admin()||this.userView.getGroupId().equals("1"))
					{
						pre.append(" 1=1" );
					}
					else*/
					{
						String nunit=this.userView.getUnitIdByBusi("3");
			    		if(nunit==null|| "".equals(nunit))
		    			{
		    				pre.append(" 1=2 ");
			    		}
			    		else
			    		{
			    			String unitarr[] =nunit.split("`");
			    			for(int i=0;i<unitarr.length;i++)
			    			{
				    			String codeid=unitarr[i];
				    			if(codeid==null|| "".equals(codeid)) {
                                    continue;
                                }
					    		if(codeid!=null&&codeid.trim().length()>2)
				    			{
					    			if("UN".equalsIgnoreCase(codeid.substring(0,2)))
					    			{
				                     	pre.append(" or b0110 ='"+codeid.substring(2)+"' ");
					    			}
					    			else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
					    			{
					    				pre.append(" or e0122='"+codeid.substring(2)+"' ");
					    			}
				                }
					    		else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
					    		{
					    			pre.append(" or 1=1 ");
				                }	
					    	}
					    	if(pre.toString().length()>0)
				    		{
				     			String str=pre.toString().substring(3);
				    			pre.setLength(0);
				    			pre.append(str);
				    		}
			    		}
					}
				}
			}
			else
			{
				/*if(this.userView.isSuper_admin()||this.userView.getGroupId().equals("1"))
				{
					pre.append(" 1=1 ");
				}
				else */if("0".equals(filterByMdule))//此处有问题？
				{
			        /**加入高级授权*/
		            StringBuffer sql = new StringBuffer("");
				    String priStrSql = InfoUtils.getWhereINSql(this.userView, dbpre);
			     	sql.append("select "+dbpre+"a01.A0100 ");
				    if (priStrSql.length() > 0) {
                        sql.append(priStrSql);
                    } else {
                        sql.append(" from "+dbpre+"a01");
                    }
    			    pre.append("(upper(nbase)='");
    		        pre.append(dbpre.toUpperCase()+"'");
    			    pre.append(" and a0100 in ("+sql.toString()+"))");
    			      
			    }
				else
				{
			    	String nunit=this.userView.getUnitIdByBusi("3");
		    		if(nunit==null|| "".equals(nunit))
	    			{
	    				pre.append(" 1=2 ");
		    		}
		    		else
		    		{
		    			String unitarr[] =nunit.split("`");
		    			for(int i=0;i<unitarr.length;i++)
		    			{
			    			String codeid=unitarr[i];
			    			if(codeid==null|| "".equals(codeid)) {
                                continue;
                            }
				    		if(codeid!=null&&codeid.trim().length()>2)
			    			{
				    			/* 所得税管理/文件/高级花名册，取出的数据不对，只取出了维护了工资归属部门的人员，工资归属部门为空的人员没有取出来 xiaoyun 2014-10-11 start */
			                 	//pre.append(" or deptid = '"+codeid.substring(2)+"' ");
				    			pre.append(" or deptid = '"+codeid.substring(2)+"'or e0122='"+codeid.substring(2)+"' ");
				    			/* 所得税管理/文件/高级花名册，取出的数据不对，只取出了维护了工资归属部门的人员，工资归属部门为空的人员没有取出来 xiaoyun 2014-10-11 end */
			                 }
				    		else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
				    		{
				    			pre.append(" or 1=1 ");
			                 }	
				    	}
				    	if(pre.toString().length()>0)
			    		{
			     			String str=pre.toString().substring(3);
			    			pre.setLength(0);
			    			pre.append(str);
			    		}
    				}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pre.toString();
	}
	 public void transformMidvariable(String dbpre,String tabid,String tablename)
	    {
	    	try
	    	{
	    		if(this.midvariableList.size()>0)
	    		{
	    			DBMetaModel dbmodel=new DBMetaModel(this.conn);
	    			dbmodel.reloadTableModel(tablename);
	    			RecordVo vo=new RecordVo(tablename);
	    			DbWizard dbw=new DbWizard(this.conn);
	    			Table table=new Table(tablename);
	    			ContentDAO dao=new ContentDAO(this.conn);
	    			StringBuffer buf=new StringBuffer();
	    			boolean bflag=false;
	    			for(int i=0;i<this.midvariableList.size();i++)
	    			{
	    				FieldItem item=(FieldItem)midvariableList.get(i);
	    				String fieldname=item.getItemid();
	    				/**变量如果未加，则构建*/
	    				if(!vo.hasAttribute(fieldname.toLowerCase()))
	    				{
	    					Field field=item.cloneField();
	    					bflag=true;
	    					table.addField(field);
	    				}//if end.
	    			}
	    			if(bflag)
	    			{
	    				dbw.addColumns(table);
	    				dbmodel.reloadTableModel(tablename);					
	    			}
	    			/*String stry=currym.substring(0, 4);
	    			String strm=currym.substring(5, 7);
	    			String strc=currcount;
	    			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));*/
	    			for(int j=0;j<midvariableList.size();j++)
					{
						StringBuffer strFilter=new StringBuffer();

						FieldItem item=(FieldItem)midvariableList.get(j);
						String fldtype=item.getItemtype();
						String fldname=item.getItemid();
						
						ArrayList usedlist=initUsedFields();
						ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
								Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
						YksjParser yp = new YksjParser(this.userView, allUsedFields,
								YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
						yp.setStdTmpTable(tablename);
						yp.setTargetFieldDecimal(item.getDecimalwidth());
						appendUsedFields(midvariableList,usedlist);
						FieldItem fielditem=new FieldItem("A01","AAAAA");
						fielditem.setItemdesc("AAAAA");
						fielditem.setCodesetid(item.getCodesetid());
						fielditem.setItemtype(fldtype);
						fielditem.setItemlength(item.getItemlength());
						fielditem.setDecimalwidth(item.getDecimalwidth());
						usedlist.add(fielditem);
						String tmptable="T#"+this.userView.getUserName()+"_mus_1";
						if(createMidTable(usedlist,tmptable,"A0100"))
						{
							buf.setLength(0);
							buf.append("insert into ");
							buf.append(tmptable);
							buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
							buf.append(dbpre+"A01");
							buf.append(" where A0100 in (select A0100 from ");
							buf.append(tablename);
							buf.append(" where upper(nbase)='");
							buf.append(dbpre.toUpperCase());
							buf.append("'");
								
								/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select a0100 from ");
							strFilter.append(tablename);
							strFilter.append(" where upper(nbase)='");
							strFilter.append(dbpre.toUpperCase());
							strFilter.append("')");	
							buf.append(")");
							dao.update(buf.toString());
						}
						yp.run(item.getFormula(),null,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
						
						buf.setLength(0);
						buf.append("where upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
						String strcond=buf.substring(6);
						String gridno=(String)this.midvarCellMap.get(fldname.toUpperCase());
						//dbw.updateRecord(tablename,tmptable,tablename+".A0100="+tmptable+".A0100", tablename+".C"+gridno+"="+tmptable+".AAAAA", strcond, strcond);
						StringBuffer sql = new StringBuffer();
						sql.append("update "+tablename+" set "+tablename+".C"+gridno+"=(select ");
						if("D".equalsIgnoreCase(fldtype))
						{
							sql.append(Sql_switcher.dateToChar(tmptable+".AAAAA", "yyyy.MM.dd"));
						}
						else
						{
							sql.append(tmptable+".AAAAA");
						}
						sql.append(" from "+tmptable+" where "+tmptable+".a0100="+tablename+".a0100 and UPPER("+tablename+".nbase)='"+dbpre.toUpperCase()+"')");
						sql.append(" where exists (select null from "+tmptable+" where "+tmptable+".a0100="+tablename+".a0100 and UPPER("+tablename+".nbase)='"+dbpre.toUpperCase()+"')");
						dao.update(sql.toString());
					}
	    			
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    }
	 private HashMap midvariableMap = new HashMap();
	 private ArrayList midvariableList = new ArrayList();
	 private HashMap midvarCellMap = new HashMap();
	    public void getMidvariable(String tabid)
	    {
	    	//ArrayList list = new ArrayList();
	    	RowSet rset = null;
	    	try
	    	{
	    		StringBuffer buf=new StringBuffer();
				buf.append("select a.cname,a.chz,a.ntype,a.cvalue,a.fldlen,a.flddec,a.codesetid,b.gridno from ");
				buf.append(" midvariable a,muster_cell b ");
				/*buf.append(" where nflag=3 and (cstate is null or cstate='");
				buf.append(tabid+"')");*/
				buf.append(" where UPPER(a.cname)=UPPER(b.field_name) and UPPER(b.flag)='V'");
				buf.append(" and b.tabid='"+tabid+"'");
				buf.append(" order by sorting");
				ContentDAO dao=new ContentDAO(this.conn);
				rset=null;
				rset=dao.search(buf.toString());
				while(rset.next())
				{
					FieldItem item=new FieldItem();
					item.setItemid(rset.getString("cname"));
					item.setFieldsetid(/*"A01"*/"");//没有实际含义
					item.setItemdesc(rset.getString("chz"));
					item.setItemlength(rset.getInt("fldlen"));
					item.setDecimalwidth(rset.getInt("flddec"));
					item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
					item.setCodesetid(rset.getString("codesetid")==null?"":rset.getString("codesetid"));
					switch(rset.getInt("ntype"))
					{
					case 1://
						item.setItemtype("N");
						break;
					case 2:
					case 4://代码型					
						item.setItemtype("A");
						break;
					case 3:
						item.setItemtype("D");
						break;
					}
					item.setVarible(1);
					this.midvariableMap.put(rset.getString("cname").toUpperCase(), item);
					this.midvariableList.add(item);
					this.midvarCellMap.put(rset.getString("cname").toUpperCase(), rset.getString("gridno"));
				}// while loop end.
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}finally{
				try
				{
					if(rset!=null) {
                        rset.close();
                    }
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
	    }

	    /**
		 * 初始设置使用字段列表
		 * @return
		 */
		private ArrayList initUsedFields()
		{
			ArrayList fieldlist=new ArrayList();
			/**人员排序号*/
			FieldItem fielditem=new FieldItem("A01","A0000");
			fielditem.setItemdesc("a0000");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**人员编号*/
			fielditem=new FieldItem("A01","A0100");
			fielditem.setItemdesc("a0100");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(8);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**单位名称*/
			fielditem=new FieldItem("A01","B0110");
			fielditem.setItemdesc("单位名称");
			fielditem.setCodesetid("UN");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**姓名*/
			fielditem=new FieldItem("A01","A0101");
			fielditem.setItemdesc("姓名");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**人员排序号*/
			fielditem=new FieldItem("A01","I9999");
			fielditem.setItemdesc("I9999");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**部门名称*/
			fielditem=new FieldItem("A01","E0122");
			fielditem.setItemdesc("部门");
			fielditem.setCodesetid("UM");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);		
			return fieldlist;
		}
		private int getDataType(String type)
		{
			int datatype=0;
			switch(type.charAt(0))
			{
			case 'A':  
				datatype=YksjParser.STRVALUE;
				break;
			case 'D':
				datatype=YksjParser.DATEVALUE;
				break;
			case 'N':
				datatype=YksjParser.FLOAT;
				break;
			}
			return datatype;
		}
		private void appendUsedFields(ArrayList slist,ArrayList dlist)
		{
			boolean bflag=false;
			for(int i=0;i<slist.size();i++)
			{
				FieldItem fielditem=(FieldItem)slist.get(i);
				String itemid=fielditem.getItemid();
				for(int j=0;j<dlist.size();j++)
				{
					bflag=false;
					FieldItem fielditem0=(FieldItem)dlist.get(j);
					String ditemid=fielditem0.getItemid();
					if(itemid.equalsIgnoreCase(ditemid))
					{
						bflag=true;
						break;
					}

				}//for j loop end.
				if(!bflag) {
                    dlist.add(fielditem);
                }
			}//for i loop end.
		}
		private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield)
		{
			boolean bflag=true;
			try
			{
				DbWizard dbw=new DbWizard(this.conn);
				if(dbw.isExistTable(tablename, false)) {
                    dbw.dropTable(tablename);
                }
				Table table=new Table(tablename);
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fielditem=(FieldItem)fieldlist.get(i);
					Field field=fielditem.cloneField();
					if(field.getName().equalsIgnoreCase(keyfield))
					{
						field.setNullable(false);
						field.setKeyable(true);
					}
					table.addField(field);
				}//for i loop end.
				Field field=new Field("userflag","userflag");
				field.setLength(50);
				field.setDatatype(DataType.STRING);
				table.addField(field);
				dbw.createTable(table);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				bflag=false;
			}
			return bflag;
		}
	public String getMusterName() {
		return musterName;
	}




	public void setMusterName(String musterName) {
		this.musterName = musterName;
	}
	public String getGroupPoint() {
		return groupPoint;
	}
	public void setGroupPoint(String groupPoint) {
		this.groupPoint = groupPoint;
	}
	public String getIsGroupPoint() {
		return isGroupPoint;
	}
	public void setIsGroupPoint(String isGroupPoint) {
		this.isGroupPoint = isGroupPoint;
	}
	public String getRix() {
		return rix;
	}
	public void setRix(String rix) {
		this.rix = rix;
	}
	public String getManageUserName() {
		return manageUserName;
	}
	public void setManageUserName(String manageUserName) {
		this.manageUserName = manageUserName;
	}
	public String getLayerid() {
		return layerid;
	}
	public void setLayerid(String layerid) {
		this.layerid = layerid;
	}
	public String getFilterWhl() {
		return filterWhl;
	}
	public void setFilterWhl(String filterWhl) {
		this.filterWhl = filterWhl;
	}
	public String getSortitem() {
		return sortitem;
	}
	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCheckdata() {
		return checkdata;
	}
	public void setCheckdata(String checkdata) {
		this.checkdata = checkdata;
	}
	public String getChecknum() {
		return checknum;
	}
	public void setChecknum(String checknum) {
		this.checknum = checknum;
	}
	public String getTemptable() {
		return temptable;
	}
	public void setTemptable(String temptable) {
		this.temptable = temptable;
	}
	public String getSalaryorder() {
		return salaryorder;
	}
	public void setSalaryorder(String salaryorder) {
		this.salaryorder = salaryorder;
	}
	public String getPrivSQL() {
		return privSQL;
	}
	public void setPrivSQL(String privSQL) {
		this.privSQL = privSQL;
	}
	public String getModuleSQL() {
		return moduleSQL;
	}
	public void setModuleSQL(String moduleSQL) {
		this.moduleSQL = moduleSQL;
	}
	public String getCombineField() {
		return combineField;
	}
	public void setCombineField(String combineField) {
		this.combineField = combineField;
	}
	
	public Table createMusterTempCombineTable(String tabid,DbWizard dbWizard,int nModule,HashMap cFactorMap,MusterBo mbo)
		throws GeneralException {
		String tablename = "T#" + this.userView.getUserName() + "_mus";
		Table table = new Table(tablename);
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList fieldlist = getSpMusterFields(tabid, nModule, cFactorMap,
					mbo);
			table.setCreatekey(false);
			ArrayList fields = new ArrayList();

			for (int i = 0; i < fieldlist.size(); i++) {
				table.addField((Field) fieldlist.get(i));
				fields.add(((Field) fieldlist.get(i)).getName().toLowerCase()
						+ "/" + ((Field) fieldlist.get(i)).getDatatype());
			}
			/*
			 * Field field=new Field("declare_tax","declare_tax");
			 * field.setDatatype(DataType.DATE); table.addField(field);
			 */
			Field field = new Field("taxmode", "taxmode");
			field.setDatatype(DataType.STRING);
			field.setLength(10);
			table.addField(field);
			field = new Field("deptid", "deptid");
			field.setDatatype(DataType.STRING);
			field.setLength(20);
			table.addField(field);
			if (dbWizard.isExistTable(table.getName(), false)) {

				dbWizard.dropTable(table);
				dbWizard.createTable(table);
			} else {
                dbWizard.createTable(table);
            }

		} catch (Exception ex) {

			throw GeneralExceptionHandler.Handle(ex);
		}

		return table;
}
	public String createCombineSQL(String tabid,String a_code,String declaredate,String dbpre) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		try
		{
			ArrayList musterCellList=getMusterCellList(tabid);  //取得花名册不用计算的数据字段
			String  tableName=this.getTaxTable();
			//tableName=PubFunc.decrypt(tableName);// changxy 20160829 tableName未解码  //取消在此bo解密 在取值时解密
			String tablename = "T#"+this.userView.getUserName()+"_mus";
			StringBuffer sql_insert=new StringBuffer(" insert into "+tablename+" (recidx,salaryid,tax_max_id,A0000,A0100,B0110,E0122,NBASE,A00Z0,A00Z1,taxmode,declare_tax,deptid");
			StringBuffer sql_from = new StringBuffer(" from "+tableName+" a");
            StringBuffer sql_whl = new StringBuffer(" where 1=1 ");
            if(dbpre!=null&&!"ALL".equalsIgnoreCase(dbpre.trim())) //2014-04-01 dengcan
            {
                sql_whl.append(" and upper(NBASE)='"+dbpre.toUpperCase()+"'");
            }
			
			/*if(this.getPrivSQL()!=null&&!this.getPrivSQL().trim().equals(""))
				sql_whl.append(" and ("+this.getPrivSQL()+")");*/
			if(this.getModuleSQL()!=null&&!"".equals(this.getModuleSQL().trim()))  // FIXME a0100 in: A0100重复有问题?
            {
                sql_whl.append(" and a0100 in( select a0100 from "+this.getTaxTable()+" where ("+this.getModuleSQL()+"))");//取消表名解密 xus 20170214
            }
			if(this.getConSQL()!=null&&!"".equals(this.getConSQL())) {
                sql_whl.append(" and ("+this.getConSQL()+")");
            }
			StringBuffer sql_select = new StringBuffer(" ");
			sql_select.append(" select ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql_select.append(" 1,");
            }
			sql_select.append("a.salaryid,a.tax_max_id,a.A0000,a.A0100,a.B0110,a.E0122,a.NBASE,a.A00Z0,a.A00Z1,a.taxmode as taxmode,a.declare_tax as declare_tax,deptid");
			
			TaxMxBo taxbo=new TaxMxBo(this.conn);
			ArrayList fieldlist=taxbo.getFieldlist();
			
			for(Iterator t=musterCellList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String gridno=(String)abean.get("gridno");
				String fieldname=(String)abean.get("fieldname");
				String fieldtype=(String)abean.get("fieldtype");
				String codeid=(String)abean.get("codeid");
				String slope=(String)abean.get("slope");
				String flag=(String)abean.get("flag");
				/**flag=v是临时变量，临时变量不能直接导入*/
				if(fieldname!=null&&fieldname.trim().length()>0&&!"V".equalsIgnoreCase(flag))
				{
					sql_insert.append(",C"+gridno);
					String[] fields={fieldname,fieldtype,slope,codeid};	
					String n=getFieldTax(fields,fieldlist);
					if(n.toUpperCase().indexOf("AS")==-1|| "A.NBASE".equalsIgnoreCase(n.toUpperCase())|| "nbase".equalsIgnoreCase(n)) {
                        n+=" as C"+gridno;
                    }
					sql_select.append(","+n);
				}
				
			}
			
			
			/*if(a_code!=null&&a_code.trim().length()>1){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if(value!=null&&value.length()>0){
					if(codesetid.equalsIgnoreCase("UN"))
					{
						sql_whl.append(" and (B0110 like '");
						sql_whl.append(value);
						sql_whl.append("%'");	
						if(value.equalsIgnoreCase(""))
						{
							sql_whl.append(" or B0110 is null");
						}
						sql_whl.append(")");
					}else if(codesetid.equalsIgnoreCase("UM")){
						sql_whl.append(" and E0122 like '");
						sql_whl.append(value);
						sql_whl.append("%'");
					}
					
				}
			}*/
			if(declaredate!=null&&declaredate.trim().length()>2&&!"all".equalsIgnoreCase(declaredate)){
				sql_whl.append(" and "+declaredate);
			}
			if (isGroupPoint != null&&isGroupPoint.trim().length()>0 && "1".equals(isGroupPoint)){
				FieldItem fielditem = DataDictionary.getFieldItem(groupPoint);
				if ("B0110".equals(groupPoint) || "E01A1".equals(groupPoint)|| "E0122".equals(groupPoint)||(fielditem!=null&&("UN".equalsIgnoreCase(fielditem.getCodesetid())|| "UM".equalsIgnoreCase(fielditem.getCodesetid())|| "@K".equalsIgnoreCase(fielditem.getCodesetid())))){
					sql_select.append(",");
					sql_select.append("a.");
					sql_select.append(groupPoint);
					sql_select.append(" as GroupN,organization.codeitemdesc as GroupV");
					sql_from.append(" left join organization on ");
					sql_from.append("a.");
					sql_from.append(groupPoint);
					sql_from.append("=organization.codeitemid");
				}else{
					if(fielditem!=null&&fielditem.isCode()){
						sql_select.append(",a." + groupPoint
							+ " as GroupN,codeitem.codeitemdesc as GroupV");
					}else{
						sql_select.append(",a." + groupPoint);
						sql_select.append(" as GroupN,a." + groupPoint+" as GroupV");
					}
					if(fielditem!=null&&fielditem.isCode()){
						sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='");
						sql_from.append(groupPoint+"' )) codeitem ");
						sql_from.append(" on codeitem.codeitemid="+ "a." + groupPoint);
					}
				}
				sql_insert.append(",GroupN,GroupV");
			}
			if (this.isGroupPoint2 != null&&this.isGroupPoint2.trim().length()>0 && "1".equals(this.isGroupPoint2)){
				FieldItem fielditem = DataDictionary.getFieldItem(groupPoint2);
				if ("B0110".equals(groupPoint2) || "E01A1".equals(groupPoint2)|| "E0122".equals(groupPoint2)||(fielditem!=null&&("UN".equalsIgnoreCase(fielditem.getCodesetid())|| "UM".equalsIgnoreCase(fielditem.getCodesetid())|| "@K".equalsIgnoreCase(fielditem.getCodesetid())))){
					sql_select.append(",");
					sql_select.append("a.");
					sql_select.append(groupPoint2);
					sql_select.append(" as GroupN2,organization.codeitemdesc as GroupV2");
					sql_from.append(" left join organization on ");
					sql_from.append("a.");
					sql_from.append(groupPoint2);
					sql_from.append("=organization.codeitemid");
				}else{
					if(fielditem!=null&&fielditem.isCode()){
						sql_select.append(",a." + groupPoint2
							+ " as GroupN2,codeitem.codeitemdesc as GroupV2");
					}else{
						sql_select.append(",a." + groupPoint2);
						sql_select.append(" as GroupN2,a." + groupPoint2+" as GroupV2");
					}
					if(fielditem!=null&&fielditem.isCode()){
						sql_from.append(" left join ( select * from  codeitem where codesetid=(select codesetid from fielditem where itemid='");
						sql_from.append(groupPoint2+"' )) codeitem ");
						sql_from.append(" on codeitem.codeitemid="+ "a." + groupPoint2);
					}
				}
				sql_insert.append(",GroupN2,GroupV2");
			}
			sql_insert.append(")");
			
			if(sortitem!=null&&sortitem.trim().length()>1){
				sql_whl.append(getSortStr("a", false, true));
			}else{
				sql_whl.append(" order by a.A0000");
			}
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
		    	sql.append(sql_insert.toString());
		    	sql.append(sql_select.toString());
		    	sql.append(sql_from.toString());
		    	sql.append(sql_whl.toString());
			}
			else
			{
				sql.append(sql_insert);
				sql.append("select RowNum,T.* from ");
				sql.append("("+sql_select+sql_from+sql_whl+")T");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		return sql.toString();
	}
	
	/**
	 * 如果排序指标是否有效
	 * @param sort_str
	 * @param gzbo
	 * @return
	 */
	private boolean isExistErrorItem(String sort_str,SalaryTemplateBo gzbo)
	{
		boolean flag=true;
		String[] temps=sort_str.toUpperCase().split(",");
		String zgItemStr=gzbo.getStandardGzItemStr();
		for(int i=0;i<temps.length;i++)
		{
			if(temps[i].length()>0)
			{
				String _str=temps[i].replaceAll("ASC", "");
				_str=_str.replaceAll("DESC", "");
				_str=_str.trim();
				if(DataDictionary.getFieldItem(_str.toLowerCase())!=null&&zgItemStr.indexOf(_str+"/")==-1)
				{
					flag=false;
					break;
				}
			}
			
			
		}
		return flag;
	}
	
	
	public void runCountFormulaCombine(String tabid,ArrayList allUsedFields,String dbpre,String tableName)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try{
			String tablename = "T#"+this.userView.getUserName()+"_mus";
			rowSet = dao.search("select gridno,field_type,QueryCond from muster_cell where tabid="+ tabid + " and flag='C'");
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");				
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员
				
                //  解析公式
				YksjParser yp = new YksjParser(userView, allUsedFields,
						YksjParser.forSearch, varType, infoGroup, "Ht",dbpre);
            	ArrayList fieldList=yp.getFormulaFieldList1(queryCond);              	
            	if(fieldList.size()!=0)
            	{
            		analyseOptTableCombine3(tabid,fieldList,tableName);
            	}
                //  解析公式
            	yp = new YksjParser(
            			this.userView//Trans交易类子类中可以直接获取userView
            			,fieldList
            			,YksjParser.forNormal
            			,varType//此处需要调用者知道该公式的数据类型
            			,YksjParser.forPerson
            			,"","");
            	
            	yp.run(queryCond);                	
            	String FSQL=yp.getSQL();
             //   System.out.println(FSQL);
				dao.update("update "+tablename+" set C"+gridNo+"="+FSQL);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void analyseOptTableCombine3(String tabid,ArrayList fieldList,String tableName)throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			String tablename = "T#"+this.userView.getUserName()+"_mus";
			DbWizard dbWizard = new DbWizard(this.conn);
			Table table = new Table(tablename);
			
			HashMap existColumnMap=new HashMap();
			rowSet=dao.search("select * from "+tablename+" where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				existColumnMap.put(metaData.getColumnName(i).toLowerCase(),"1");
			}
			
			int num=0;
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				FieldItem fieldItem1=(FieldItem)t.next();
				if(existColumnMap.get(fieldItem1.getItemid().toLowerCase())!=null) {
                    continue;
                }
				Field a_field=fieldItem1.cloneField();
				table.addField(a_field);
				num++;
			}
			if(num!=0) {
                dbWizard.addColumns(table);
            }
			
			String musterName=tablename;
			StringBuffer whl=new StringBuffer("");
			/*whl.append(musterName+".a0100= "+tableName+".a0100");
			whl.append(" and "+musterName+".nbase="+tableName+".nbase");
			whl.append(" and "+musterName+".a00z0="+tableName+".a00z0");
			whl.append(" and "+musterName+".a00z1="+tableName+".a00z1");
			whl.append(" and "+musterName+".salaryid="+tableName+".salaryid");*/
			whl.append(" "+musterName+".tax_max_id="+tableName+".tax_max_id");// and 
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				//StringBuffer sql=new StringBuffer("update "+musterName+" set ");		
				FieldItem fieldItem1=(FieldItem)t.next();
				//sql.append(" "+musterName+"."+fieldItem1.getItemid()+"=(select "+tableName+"."+fieldItem1.getItemid());
				//sql.append(" from "+tableName+" where "+whl.toString()+")");
			   //.append(" where exists (select null from "+tableName+" where "+whl.toString()+")");
				//dao.update(sql.toString());
				dbWizard.updateRecord(musterName, tableName, whl.toString(), musterName+"."+fieldItem1.getItemid()+"="+tableName+"."+fieldItem1.getItemid(), "", "");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public ArrayList getAllMidVariable()
	{
		ArrayList list = new ArrayList();
		RowSet rset = null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select a.cname,a.chz,a.ntype,a.cvalue,a.fldlen,a.flddec,a.codesetid from ");
			buf.append(" midvariable a ");
			buf.append(" order by a.sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid")==null?"":rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				list.add(item);
			}// while loop end.
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	public String getConSQL() {
		return conSQL;
	}
	public void setConSQL(String conSQL) {
		this.conSQL = conSQL;
	}
	public String getIsGroupPoint2() {
		return isGroupPoint2;
	}
	public void setIsGroupPoint2(String isGroupPoint2) {
		this.isGroupPoint2 = isGroupPoint2;
	}
	public String getGroupPoint2() {
		return groupPoint2;
	}
	public void setGroupPoint2(String groupPoint2) {
		this.groupPoint2 = groupPoint2;
	}
	public String getLayerid2() {
		return layerid2;
	}
	public void setLayerid2(String layerid2) {
		this.layerid2 = layerid2;
	}
	public String getGroupCount() {
		return groupCount;
	}
	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}
	public String getTaxTable() {
		return taxTable;
	}
	public void setTaxTable(String taxTable) {
		this.taxTable = taxTable;
	}
    public String getSalaryDataTable() {
        return salaryDataTable;
    }
    public void setSalaryDataTable(String salaryDataTable) {
        this.salaryDataTable = salaryDataTable;
    }
    public StringBuffer getSalaryDataTableCond() {
        return salaryDataTableCond;
    }
    public void setSalaryDataTableCond(StringBuffer salaryDataTableCond) {
        this.salaryDataTableCond = salaryDataTableCond;
    }
	
	
	
}
