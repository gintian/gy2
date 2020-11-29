/**
 * 
 */
package com.hjsj.hrms.businessobject.report.reportanalyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Title:报表归档数据分析类</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 4, 2006:3:26:18 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportPDBAnalyse {

	private Connection conn;
	private ArrayList reportList = new ArrayList();			//报表下拉数据集
	private ArrayList reportYearidList = new ArrayList();   //报表归档年份数据集
	private ArrayList reportCountidList = new ArrayList();  //报表归档信息数据集（月份/季度/上下半年/次数）
	private ArrayList reportWeekList=new ArrayList();
	private String reportCountInfo = "null";                //报表次数季度等信息描述
	private String reportWeekInfo="null";
	private String reportHtml;                              //报表表格
	private String reportState = "null";                    //此表无可分析历史数据！信息
	private String reportGridDBInfo = "";                   //表格数据
    private String reportTypes="";
	
	
	private String reportGridTitle="";                      //图表显示单元格描述信息
	private ArrayList chartDBList = new ArrayList();        //图表显示数据集合
	private HashMap   chartDBmap=new HashMap();
	private String reportExist = "yes";
	private UserView userView=null;
	private String currentReport;
	
	private TnameBo tnameBo=null;
	private String scopeid = "0";
	/** 判断是否是员工总量变化趋势过来的 */
	private boolean browseFlag = false;
	
	/**
	 * 报表归档表数据分析
	 * @param conn  DB连接
	 */
	public ReportPDBAnalyse(Connection conn){
		this.conn = conn;
	}
	
	
	/**
	 * 获得报表的归档类型/报表标识
	 * 1，一般 2，年 3，半年 4，季报 5，月报
	 * @return
	 * @throws GeneralException
	 */
	public int getReportFlag(String tabid){
		int n = 1;
		String sql = "select narch from tname where tabid = " + tabid;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs= dao.search(sql.toString());
			if (rs.next()) {
				n = rs.getInt("narch");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			   }

			}*/
		
		return n;
	}

	/**
	 * 用户操作填报单位树后的连动反应
	 * @param unitCode      填报单位编码
	 * @param userID        用户ID
	 * @param userName      用户名
	 * @param currentReqort 默认打开分析的报表id
	 * @throws GeneralException
	 */
	public void changeReportUnitTree(String unitCode , String userID ,String userName ,String currentReport,String reportSortID) throws GeneralException{
		
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		String reportTypes = "0";
		
		String tabid=currentReport;
		String yearid = "0";
		String countid = "0";
		RowSet rs = null;
		
		try
		{
		
		if(reportSortID.trim().length()==0) {
            this.reportExist ="no";
        }
		/***********************获得用户负责的报表分类*******************************/
		//判断默认报表是否存在
		boolean b=false;
		if(tabid!=null&&tabid.trim().length()>0)
		{
			if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,tabid)) {
                b=true;
            }
				
		}
			/***********************初始化填报单位负责的报表集合（下拉列表）*******************************/
		/*if(!b&&reportSortID!=null&&reportSortID.trim().length()>0){lzw*/
		if(reportSortID!=null&&reportSortID.trim().length()>0){
			sql.append("select name , tabid  from tname where tsortid="+reportSortID+" order by tabid");
				//获得初始的报表，为了显示对应的年份（目的：用户操作树第一次显示的年份信息）
           /* rs = dao.search(sql.toString());	
			while(rs.next()){
				String reportTabid = String.valueOf(rs.getInt("tabid"));
				if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,reportTabid))
				{
							tabid = reportTabid;
							break;
				}
			}	*/
		}
		this.setCurrentReport(tabid);
			
			//报表列表封装 用户JSP页面报表下拉框显示
		if(sql.length()>0){
			rs = dao.search(sql.toString());
			while(rs.next()) {
						String reportTabid = String.valueOf(rs.getInt("tabid"));
						String reportName = "("+ reportTabid + ")" + rs.getString("name");
						CommonData vo=new CommonData(reportTabid,reportName);
						
						if(this.userView==null) {
                            reportList.add(vo);
                        } else if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,reportTabid))
						{
							reportList.add(vo);
						}
			 }
		}
		
		/***********************初始化填报单位负责的特定报表的年份集合（下拉列表）*******************************/
		if(tabid!=null&&tabid.trim().length()>0)
		{
			TnameExtendBo teb = new TnameExtendBo(this.conn);
			TnameBo tbo = new TnameBo(this.conn,tabid,userID,userName,"temp");
			
			//是否存在报表归档表并且归档表中有数据
			if(teb.execute_Ta_table(tabid,tbo) && this.isTa_reportExistDB(tabid,unitCode)){
				
				//归档类型
				int reportFlag = this.getReportFlag(tabid);
				
				if(reportFlag == 2){//报表归档类型为年报
					
					sql.delete(0, sql.length());
					sql.append("select DISTINCT  yearid from ta_");
					sql.append(tabid);
					sql.append(" where unitcode = '");
					sql.append(unitCode);
					sql.append("'");
					sql.append("  order by yearid  desc  ");
					
					//System.out.println("年报类型报表年SQL=" + sql.toString());
					rs = null;
					try {
						rs = dao.search(sql.toString());
						if(rs.next()){
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							yearid = reportYearid; //默认年份
						}
	
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					rs = null;
					try {
						rs = dao.search(sql.toString());	
						while(rs.next()) {
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							CommonData vo=new CommonData(reportYearid,reportYearid);
							reportYearidList.add(vo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					this.reportCountInfo="null";
							
					ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.conn);
					this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , yearid , "1" ,tbo ,String.valueOf(reportFlag));
					this.reportState ="null";
					
					//图表显示第一行第一列分析
					String col = (String)tbo.getColMap().get("1");
					String row = (String)tbo.getRowMap().get("1");
					this.changeReportGrid(unitCode,tabid,row,col);
	
				}else{
					
					sql.delete(0, sql.length());
					sql.append("select DISTINCT  yearid from ta_");
					sql.append(tabid);
					sql.append(" where unitcode = '");
					sql.append(unitCode);
					sql.append("'");
					sql.append("  order by yearid  desc ");
					
					//System.out.println("其他类型年SQL=" + sql.toString());
					
					rs = null;
					try {
						rs = dao.search(sql.toString());
						if(rs.next()){
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							yearid = reportYearid;
						}
	
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					rs = null;
					try {
						rs = dao.search(sql.toString());	
						while(rs.next()) {
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							CommonData vo=new CommonData(reportYearid,reportYearid);
							reportYearidList.add(vo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					/**********初始化填报单位负责的特定报表的特定年份的的（月份/上下半年/次数/季度）集合（下拉列表）***********/
					
					sql.delete(0, sql.length());
					sql.append("select distinct countid from ta_");
					sql.append(tabid);
					sql.append(" where unitcode = '");
					sql.append(unitCode);
					sql.append("'  and yearid = ");
					sql.append(yearid);
					sql.append(" order by countid ");
					
					//System.out.println(sql);
					
					rs = null;
					try {
						rs = dao.search(sql.toString());
						if(rs.next()){
							countid = String.valueOf(rs.getInt("countid"));	
						}
	
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					rs = null;
					try {
						rs = dao.search(sql.toString());
						while(rs.next()) {
							int cid = rs.getInt("countid");
							String value = String.valueOf(cid);
							String info = this.getReportCountidInfo(reportFlag,cid);
							//System.out.println(info + "  " + value);
							CommonData vo=new CommonData(value,info);
							reportCountidList.add(vo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					/**********构造报表显示HTML表格***********/
					
					ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.conn);
					this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , yearid , countid ,tbo ,String.valueOf(reportFlag));
					this.reportState ="null";
					
					//图表显示第一行第一列分析
					String col = (String)tbo.getColMap().get("1");
					String row = (String)tbo.getRowMap().get("1");
					this.changeReportGrid(unitCode,tabid,row,col);
					
				}//end if
			
			}else{
				/**********构造报表显示HTML表格***********/	
				ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.conn);
				this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , "0" , "0" ,tbo ,reportTypes);
				this.reportState =ResourceFactory.getProperty("report_collect.info8")+"！";
	
			}
		}
		else
		{
			this.reportHtml="";
			this.reportState =ResourceFactory.getProperty("report.usernotreport");
		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}
	
	
	/**
	 * 用户操作填报单位树后的连动反应
	 * @param unitCode      填报单位编码
	 * @param userID        用户ID
	 * @param userName      用户名
	 * @param currentReqort 默认打开分析的报表id
	 * @throws GeneralException
	 */
	public void changeReportUnitTree2(String unitCode , String userID ,String userName ,String currentReport,String reportSortID) throws GeneralException{
		
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		String reportTypes = "0";
		
		String tabid=currentReport;
		String yearid = "0";
		String countid = "0";
		RowSet rs = null;
		
		/***********************获得用户负责的报表分类*******************************/
		sql.delete(0, sql.length());
		sql.append("select reporttypes from tt_organization where unitcode = '");
		sql.append(unitCode);
		sql.append("'");
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				//当前用户负责的报表类信息
				reportTypes = (String) rs.getString("reporttypes");
				if (reportTypes == null) {
					// 用户没有权限操作任何报表
					this.reportExist ="no";
					
				} else {
					if (reportTypes.charAt(reportTypes.length() - 1) == ',') {
						reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
					}

				}

			} else {
					// 用户没有权限操作任何报表
					this.reportExist ="no";
					/*Exception e = new Exception("当前用户未负责任何报表");
					throw GeneralExceptionHandler.Handle(e);*/
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						throw GeneralExceptionHandler.Handle(e1);
					}
			   }

			}*/
		
		
		//判断默认报表是否存在
		if(!"no".equals(this.reportExist))
		{
			boolean b = false;
			sql.delete(0, sql.length());
			if(tabid!=null&&tabid.trim().length()>0)
			{
				sql.append("select name , tabid  from tname where tsortid in (");
				sql.append(reportTypes);
				sql.append(") and tabid='");
				sql.append(tabid);
				sql.append("'");
				rs = null;
				try {
					rs = dao.search(sql.toString());	
					if(rs.next()){
						b=true;
					}
		
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}
			/***********************初始化填报单位负责的报表集合（下拉列表）*******************************/
			
			//下拉列表中显示的报表列表
			sql.delete(0, sql.length());
			sql.append("select name , tabid  from tname where tsortid="+reportSortID+" order by tabid");
		//	sql.append("select name , tabid  from tname where tsortid in (");
		//	sql.append(reportTypes);
		//	sql.append(")");
			
			if(!b){
				
				//获得初始的报表，为了显示对应的年份（目的：用户操作树第一次显示的年份信息）
				rs = null;
				try {
					rs = dao.search(sql.toString());	
					if(rs.next()){
						String reportTabid = String.valueOf(rs.getInt("tabid"));
						tabid = reportTabid;
					}
	
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}
			this.setCurrentReport(tabid);
			
			//报表列表封装 用户JSP页面报表下拉框显示
			rs = null;
			try {
				rs = dao.search(sql.toString());
				while(rs.next()) {
					String reportTabid = String.valueOf(rs.getInt("tabid"));
					String reportName = "("+ reportTabid + ")" + rs.getString("name");
					CommonData vo=new CommonData(reportTabid,reportName);
					
					if(this.userView==null) {
                        reportList.add(vo);
                    } else if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,reportTabid))
					{
						reportList.add(vo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}/*finally{
				   if(rs!=null){
						try {
							rs.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
							throw GeneralExceptionHandler.Handle(e1);
						}
				   }
	
				}*/
		}
		
		/***********************初始化填报单位负责的特定报表的年份集合（下拉列表）*******************************/
		if(tabid!=null&&tabid.trim().length()>0)
		{
			TnameExtendBo teb = new TnameExtendBo(this.conn);
			TnameBo tbo = new TnameBo(this.conn,tabid,userID,userName,"temp");
			
			//是否存在报表归档表并且归档表中有数据
			if(teb.execute_Ta_table(tabid,tbo) && this.isTa_reportExistDB(tabid,unitCode)){
				
				//归档类型
				int reportFlag = this.getReportFlag(tabid);
				
				if(reportFlag == 2){//报表归档类型为年报
					
					sql.delete(0, sql.length());
					sql.append("select DISTINCT  yearid from ta_");
					sql.append(tabid);
					sql.append(" where unitcode = '");
					sql.append(unitCode);
					sql.append("'");
					sql.append("  order by yearid  desc ");
					
					//System.out.println("年报类型报表年SQL=" + sql.toString());
					rs = null;
					try {
						rs = dao.search(sql.toString());
						if(rs.next()){
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							yearid = reportYearid; //默认年份
						}
	
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}/*finally{
						   if(rs!=null){
								try {
									rs.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
									throw GeneralExceptionHandler.Handle(e1);
								}
						   }
					}*/
					
					rs = null;
					try {
						rs = dao.search(sql.toString());	
						while(rs.next()) {
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							CommonData vo=new CommonData(reportYearid,reportYearid);
							reportYearidList.add(vo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}/*finally{
						   if(rs!=null){
								try {
									rs.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
									throw GeneralExceptionHandler.Handle(e1);
								}
						   }
	
						}*/
					this.reportCountInfo="null";
							
					ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.conn);
					this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , yearid , "1" ,tbo ,String.valueOf(reportFlag));
					this.reportState ="null";
					
					//图表显示第一行第一列分析
					String col = (String)tbo.getColMap().get("1");
					String row = (String)tbo.getRowMap().get("1");
					this.changeReportGrid(unitCode,tabid,row,col);
	
				}else{
					
					sql.delete(0, sql.length());
					sql.append("select DISTINCT  yearid from ta_");
					sql.append(tabid);
					sql.append(" where unitcode = '");
					sql.append(unitCode);
					sql.append("'");
					sql.append("  order by yearid  desc ");
					
					//System.out.println("其他类型年SQL=" + sql.toString());
					
					rs = null;
					try {
						rs = dao.search(sql.toString());
						if(rs.next()){
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							yearid = reportYearid;
						}
	
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}/*finally{
						   if(rs!=null){
								try {
									rs.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
									throw GeneralExceptionHandler.Handle(e1);
								}
						   }
	
						}
					*/
					rs = null;
					try {
						rs = dao.search(sql.toString());	
						while(rs.next()) {
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							CommonData vo=new CommonData(reportYearid,reportYearid);
							reportYearidList.add(vo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}/*finally{
						   if(rs!=null){
								try {
									rs.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
									throw GeneralExceptionHandler.Handle(e1);
								}
						   }
	
						}*/
					
					/**********初始化填报单位负责的特定报表的特定年份的的（月份/上下半年/次数/季度）集合（下拉列表）***********/
					
					sql.delete(0, sql.length());
					sql.append("select distinct countid from ta_");
					sql.append(tabid);
					sql.append(" where unitcode = '");
					sql.append(unitCode);
					sql.append("'  and yearid = ");
					sql.append(yearid);
					sql.append(" order by countid ");
					
					//System.out.println(sql);
					
					rs = null;
					try {
						rs = dao.search(sql.toString());
						if(rs.next()){
							countid = String.valueOf(rs.getInt("countid"));	
						}
	
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}/*finally{
						   if(rs!=null){
								try {
									rs.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
									throw GeneralExceptionHandler.Handle(e1);
								}
						   }
	
						}
					*/
					//System.out.println(sql);
					rs = null;
					try {
						rs = dao.search(sql.toString());
						while(rs.next()) {
							int cid = rs.getInt("countid");
							String value = String.valueOf(cid);
							String info = this.getReportCountidInfo(reportFlag,cid);
							//System.out.println(info + "  " + value);
							CommonData vo=new CommonData(value,info);
							reportCountidList.add(vo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}/*finally{
						   if(rs!=null){
								try {
									rs.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
									throw GeneralExceptionHandler.Handle(e1);
								}
						   }
	
						}
	*/
					/**********构造报表显示HTML表格***********/
					
					ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.conn);
					this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , yearid , countid ,tbo ,String.valueOf(reportFlag));
					this.reportState ="null";
					
					//图表显示第一行第一列分析
					String col = (String)tbo.getColMap().get("1");
					String row = (String)tbo.getRowMap().get("1");
					this.changeReportGrid(unitCode,tabid,row,col);
					
				}//end if
			
			}else{
				/**********构造报表显示HTML表格***********/	
				ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.conn);
				this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , "0" , "0" ,tbo ,reportTypes);
				this.reportState =ResourceFactory.getProperty("report_collect.info8")+"！";
	
			}
		}
		else
		{
			this.reportHtml="";
			this.reportState =ResourceFactory.getProperty("report.usernotreport");
		}
		
		
	}
	
	
	public ArrayList getReportTabList(String sortid)
	{
		 ArrayList list=new ArrayList();
		 RowSet rs=null;
		 try {
			 ContentDAO dao = new ContentDAO(this.conn);
			 rs=dao.search("select name , tabid  from tname where tsortid="+sortid);
			 while(rs.next()) {
					String reportTabid = String.valueOf(rs.getInt("tabid"));
					String reportName = "("+ reportTabid + ")" + rs.getString("name");
					if(this.userView==null)
					{
						CommonData vo=new CommonData(reportTabid,reportName);
						list.add(vo);
					}
					else if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,reportTabid))
					{
						CommonData vo=new CommonData(reportTabid,reportName);
						list.add(vo);
					}
				}
		 }
		 catch(Exception e)
		 {
				e.printStackTrace();
		 }
		 return list;
	}
	
	public ArrayList getReportSortList(String unitCode) throws GeneralException
	{
		    ArrayList list=new ArrayList();
		    ContentDAO dao = new ContentDAO(this.conn);
		    try {
		    	String[] reportTypes=null;
				StringBuffer strsql=new StringBuffer("");
				strsql.append("select reporttypes,analysereports from tt_organization  where  unitcode='");
				strsql.append(unitCode);
				strsql.append("'");				
				RowSet rs =dao.search(strsql.toString());	
				if(rs.next())
				{
					String analysereports = Sql_switcher.readMemo(rs,"analysereports");
					if(Sql_switcher.readMemo(rs,"reporttypes").trim().length()>0) {
                        reportTypes=(Sql_switcher.readMemo(rs,"reporttypes")).trim().split(",");
                    }
					if(analysereports!=null&&analysereports.length()>0){
						String reports [] =	analysereports.split(",");
						String temptypes =",";
						TTorganization ttorganization=new TTorganization(this.conn);
						HashMap reportmap =  ttorganization.getReportTsort();
						ArrayList templist= new ArrayList();
						for(int i=0;i<reports.length;i++){
							if(reports[i].trim().length()>0&&temptypes.indexOf(","+reportmap.get(reports[i].trim())+",")==-1&&reportmap.get(reports[i].trim())!=null){
								temptypes+=reportmap.get(reports[i].trim())+",";
								templist.add(reportmap.get(reports[i].trim()));
							}
						}
						if(temptypes.length()>1){
							String reporttypestemp= "";
							Collections.sort(templist);
							for(int i=0;i<templist.size();i++){
								reporttypestemp+= templist.get(i)+",";
							}
							if(reporttypestemp.length()>1) {
                                reporttypestemp=reporttypestemp.substring(0,reporttypestemp.length()-1);
                            }
							reportTypes = reporttypestemp.split(",");
						}
						}
				}
				StringBuffer sql=new StringBuffer("select tsortid,name from tsort where ");
				if(reportTypes!=null)
				{						
					//wangcq 2014-12-25 begin
					for(int i=0; i<reportTypes.length; i++){
						boolean haveResource = false;
						String sortid = reportTypes[i];
						haveResource = sortidResource(unitCode, sortid, dao);
						if(!haveResource) {
                            reportTypes[i] = "";
                        }
					}
					//wangcq 2014-12-25 end
					StringBuffer sql_sub=new StringBuffer("");
					for(int i=0;i<reportTypes.length;i++)
					{
						if(StringUtils.isNotEmpty(reportTypes[i])){
							sql_sub.append(" or ");
							sql_sub.append(" tsortid=");
							sql_sub.append(reportTypes[i]);
						}
					}
					if(StringUtils.isNotEmpty(sql_sub.toString())){
						String sql1=sql.append(sql_sub.substring(3)).toString();						
						rs=dao.search(sql1+" order by tsortid");
						while(rs.next())
						{
//						if(isNode(rs.getString("tsortid"),unitCode))
//						{
							CommonData vo=new CommonData(rs.getString("tsortid"),rs.getString("tsortid")+":"+rs.getString("name"));
							list.add(vo);
//						}
						}
					}
					
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	
	/**
	 * 判断表类下是否有报表
	 * @param sortid
	 * @return
	 */
	public boolean isNode(String sortid,String unitcode)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			sql.append("select * from treport_ctrl where unitcode='"+unitcode+"' and tabid in (select TabId from tname  where TSortId="+sortid+" )");
			RowSet rowSet=dao.search(sql.toString());
			int num=0;
			while(rowSet.next())
			{
				String tabid=rowSet.getString("tabid");
				if(this.userView!=null&&!this.userView.isHaveResource(IResourceConstant.REPORT,tabid)) {
                    continue;
                }
				num++;
			}
			if(num>0) {
                flag=true;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	
	
	
	
	
	
	String countid="0";
	String yearid="0";
	/**
	 * 改变报表后的连动反应
	 * @param tabid      改变的报表ID
	 * @param unitCode   填报单位编码
	 * @param userID     用户ID
	 * @param userName   用户名
	 * @throws GeneralException
	 */
	public void changeReportTabid(String tabid , String unitCode ,String userID ,String userName,TnameBo tbo) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		String reportTypes ="";
		String week="0";
		RowSet rs = null;
		
		//liuy 2014-8-29 begin
		ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.conn);
		if(browseFlag){//根据browseFlag的值，判断是否更改生成报表时的上间距
			rahbo.setToolBarRows(1);//1,表示一行 30px；2表示两行 60px
		}//liuy 2014-8-29 end
		
		//判断是否存在报表归档数据表，是否归档数据表中有数据
		TnameExtendBo teb = new TnameExtendBo(this.conn);
		tbo.setScopeid(this.scopeid);
		if(teb.execute_Ta_table(tabid,tbo) && this.isTa_reportExistDB(tabid ,unitCode)){
		
			//System.out.println("归档表存在！");
			
			/***********************初始化填报单位负责的特定报表的年份集合（下拉列表）*******************************/

			int reportFlag = this.getReportFlag(tabid);
			this.reportTypes=String.valueOf(reportFlag);
			if(reportFlag == 2){//报表归档类型为年报
				
				sql.delete(0, sql.length());
				sql.append("select DISTINCT  yearid from ta_");
				sql.append(tabid);
				sql.append(" where unitcode = '");
				sql.append(unitCode);
				sql.append("'");
				if(this.scopeid!=null&&this.scopeid.length()>0&&!"0".equals(this.scopeid)) {
                    sql.append(" and scopeid="+this.scopeid);
                }
					sql.append(" order by yearid   desc ");
				
				//System.out.println("年报类型报表年SQL=" + sql.toString());
				
				//取出默认的年份/页面显示的年份
				rs = null;
				try {
					rs = dao.search(sql.toString());
					if(rs.next()){
						String reportYearid = String.valueOf(rs.getInt("yearid"));	
						yearid = reportYearid; //默认年份
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}/*finally{
					   if(rs!=null){
							try {
								rs.close();
							} catch (SQLException e1) {
								e1.printStackTrace();
								throw GeneralExceptionHandler.Handle(e1);
							}
					   }
				}*/
				
				rs = null;
				try {
					rs = dao.search(sql.toString());	
					while(rs.next()) {
						String reportYearid = String.valueOf(rs.getInt("yearid"));	
						CommonData vo=new CommonData(reportYearid,reportYearid);
						reportYearidList.add(vo);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}/*finally{
					   if(rs!=null){
							try {
								rs.close();
							} catch (SQLException e1) {
								e1.printStackTrace();
								throw GeneralExceptionHandler.Handle(e1);
							}
					   }

					}*/

				/**********构造报表显示HTML表格***********/
				
				
				rahbo.setUserView(userView);
				this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , yearid , "1" ,tbo ,String.valueOf(reportFlag));
				this.reportState ="null";
				
			}else{

				sql.delete(0, sql.length());
				sql.append("select DISTINCT  yearid from ta_");
				sql.append(tabid);
				sql.append(" where unitcode = '");
				sql.append(unitCode);
				sql.append("'");
				if(this.scopeid!=null&&this.scopeid.length()>0&&!"0".equals(this.scopeid)) {
                    sql.append(" and scopeid="+this.scopeid);
                }
				sql.append(" order by yearid desc ");
				
				//取出默认的年份/页面显示的年份
				if("0".equals(yearid))
				{
					rs = null;
					try {
						rs = dao.search(sql.toString());
						if(rs.next()){
							String reportYearid = String.valueOf(rs.getInt("yearid"));	
							yearid = reportYearid; //默认年份
						}
	
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
				}
				
				//年份集合
				rs = null;
				try {
					rs = dao.search(sql.toString());	
					while(rs.next()) {
						String reportYearid = String.valueOf(rs.getInt("yearid"));	
						CommonData vo=new CommonData(reportYearid,reportYearid);
						this.reportYearidList.add(vo);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
				/**********初始化填报单位负责的特定报表的特定年份的的（月份/上下半年/次数/季度）集合（下拉列表）***********/
				
				  
					sql.delete(0, sql.length());
					sql.append("select distinct countid from ta_");
					sql.append(tabid);
					sql.append(" where unitcode = '");
					sql.append(unitCode);
					sql.append("'  and yearid = ");
					sql.append(yearid);
					if(this.scopeid!=null&&this.scopeid.length()>0&&!"0".equals(this.scopeid)) {
                        sql.append(" and scopeid="+this.scopeid);
                    }
					sql.append(" order by countid ");
					
					//System.out.println("次数SQL=" + sql.toString());
					//默认次数信息
					if("0".equals(countid))
					{
						rs = null;
						try {
							rs = dao.search(sql.toString());
							if(rs.next()){
								countid = String.valueOf(rs.getInt("countid"));	
							}
	
						} catch (Exception e) {
							e.printStackTrace();
							throw GeneralExceptionHandler.Handle(e);
						}
					}
					//次数集合
					rs = null;
					try {
						rs = dao.search(sql.toString());
						while(rs.next()) {
							int cid = rs.getInt("countid");
							String value = String.valueOf(cid);
							String info = this.getReportCountidInfo(reportFlag,cid);
							CommonData vo=new CommonData(value,info);
							this.reportCountidList.add(vo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					
					
					if(reportFlag==6)//周报
					{
						sql.setLength(0);
						sql.append("select distinct weekid from ta_");
						sql.append(tabid);
						sql.append(" where unitcode = '");
						sql.append(unitCode);
						sql.append("'  and yearid = ");
						sql.append(yearid);
						sql.append(" and countid="+countid);
						if(this.scopeid!=null&&this.scopeid.length()>0&&!"0".equals(this.scopeid)) {
                            sql.append(" and scopeid="+this.scopeid);
                        }
						sql.append(" order by weekid ");
						
						//System.out.println("次数SQL=" + sql.toString());
						//默认次数信息
						rs = null;
						try {
							rs = dao.search(sql.toString());
							if(rs.next()){
								week = String.valueOf(rs.getInt("weekid"));	
								rahbo.setWeekid(week);
							}

						} catch (Exception e) {
							e.printStackTrace();
							throw GeneralExceptionHandler.Handle(e);
						}
						
						//周数集合
						rs = null;
						try {
							rs = dao.search(sql.toString());
							while(rs.next()) {
								int cid = rs.getInt("weekid");
								String value = String.valueOf(cid);
								String info = ResourceFactory.getProperty("hmuster.label.d")+cid+ResourceFactory.getProperty("kq.wizard.week");
								CommonData vo=new CommonData(value,info);
								this.reportWeekList.add(vo);
							}
						} catch (Exception e) {
							e.printStackTrace();
							throw GeneralExceptionHandler.Handle(e);
						}
						
					}

					/**********构造报表显示HTML表格***********/

					rahbo.setUserView(userView);
					this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , yearid , countid ,tbo ,String.valueOf(reportFlag));
					this.reportState ="null";

			}//end if

				//图表显示第一行第一列分析
				String col = (String)tbo.getColMap().get("1");
				String row = (String)tbo.getRowMap().get("1");
				col=col==null?"0":col;
				row=row==null?"0":row;
				this.changeReportGrid(unitCode,tabid,row,col);
				
			
		}else{
			
		//	System.out.println("归档表不存在！");
			/**********构造报表显示HTML表格***********/
	
			this.reportHtml= rahbo.creatHtmlView(unitCode ,tabid , "0" , "0" ,tbo ,reportTypes);
			this.reportState =ResourceFactory.getProperty("report_collect.info8")+"！";
		}
		
	}
	
	/**
	 * 改变年份后的报表分析联动
	 * @param tabid       报表表号
	 * @param unitCode    填报单位编号
	 * @param yearid      年号
	 * @param userName    用户名
	 * @param userID      用户ID
	 * @return
	 * @throws GeneralException
	 */
	public String changeReportYearid(String  tabid , String unitCode , String yearid ,String userName,String userID) throws GeneralException{
		
		StringBuffer result = new StringBuffer(); //返回的结果
		
		//判断报表类型
		int reportFlag = this.getReportFlag(tabid);
		
		if(reportFlag == 2){//报表归档类型为年报
			this.reportCountInfo="null";
			//调用添充表格方法
			TnameExtendBo teb = new TnameExtendBo(this.conn);
			TnameBo tbo = new TnameBo(this.conn,tabid,userID,userName,"temp");
			ArrayList list = teb.getReportAnalyseResult(unitCode,yearid,"1",tabid,tbo,String.valueOf(reportFlag));
			this.getReportGridDBInfo(list);
			return "null";
		}else{
			ContentDAO dao = new ContentDAO(this.conn);
			String countid = "0";
			RowSet rs = null;
			
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct countid  from ta_");
			sql.append(tabid);
			sql.append(" where unitcode = '");
			sql.append(unitCode);
			sql.append("' and yearid = ");
			sql.append(yearid);
			sql.append(" order by countid ");
			
			rs = null;
			try {
				rs = dao.search(sql.toString());
				if(rs.next()){
					countid = String.valueOf(rs.getInt("countid"));	
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			/*
			 * 年份变化后的参数下拉框变化信息
			 * 格式 描述信息##返回值@描述信息##返回值@
			 */
			rs = null;
			try {
				rs = dao.search(sql.toString());
				while(rs.next()) {
					int cid = rs.getInt("countid");
					String value = String.valueOf(cid);
					String info = this.getReportCountidInfo(reportFlag,cid);
					String temp = info + "##" + value+"@";
					result.append(temp);
				
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
			//调用填充表格数据方法
			TnameExtendBo teb = new TnameExtendBo(this.conn);
			TnameBo tbo = new TnameBo(this.conn,tabid,userID,userName,"temp");
			ArrayList list = teb.getReportAnalyseResult(unitCode,yearid,countid,tabid,tbo,String.valueOf(reportFlag));
			this.getReportGridDBInfo(list);
		}
		
		if(result == null || "".equals(result)){
			return "null";
		}
		return result.toString();
	}
	
	
	/**
	 * 改变countid数据连动
	 * @param tabid
	 * @param yearid
	 * @param countid
	 * @param unitCode
	 * @param userName
	 * @param userID
	 * @throws GeneralException
	 */
	public void changeReportCountid(String  tabid ,  String yearid , String countid , 
			String unitCode , String userName,String userID,String weekid) throws GeneralException{
		//归档表类型
		int reportFlag = this.getReportFlag(tabid);
		
		//调用填充表格数据方法
		TnameExtendBo teb = new TnameExtendBo(this.conn);
		TnameBo tbo = new TnameBo(this.conn,tabid,userID,userName,"temp");
		tnameBo=tbo;
		teb.setWeekid(weekid);
		ArrayList list = teb.getReportAnalyseResult(unitCode,yearid,countid,tabid,tbo,String.valueOf(reportFlag));
		this.getReportGridDBInfo(list);
		
		//图表不显示更新
		
	}
	
	/**
	 * 单击归档表格数据图表联动
	 * @param unitCode    填报单位编码
	 * @param tabid       报表
	 * @param row         单元格行
	 * @param col         单元个列
	 * @throws GeneralException 
	 */
	public void changeReportGrid(String unitCodes , String tabid ,String row , String col ) throws GeneralException{
		TnameBo tbo = new TnameBo(this.conn,tabid);
		this.reportTypes=String.valueOf(this.getReportFlag(tabid));
		ReportAnalyseHtmlBo rahb = new ReportAnalyseHtmlBo(this.conn);
		ArrayList list = rahb.getGridInfoList(Integer.parseInt(row),Integer.parseInt(col),tabid,tbo);
		if(list.size()>0){
			//归档表中的行列标识
			String col_archiveName = (String)list.get(0);
			String row_archiveName = (String)list.get(1);
			
			//表格对应的标题名字
			String tnameColName = ((String)list.get(2)).replaceAll("`","");
			String tnameRowName = ((String)list.get(3)).replaceAll("`","");
			this.reportGridTitle = tnameRowName +"-"+ tnameColName;
			this.chartDBList = this.createReportChartInfo(tabid,row_archiveName,col_archiveName,unitCodes);
		}
	}
	/**
	 * 单击归档表格数据图表联动
	 * @param unitCode    填报单位编码
	 * @param tabid       报表
	 * @param row         单元格行
	 * @param col         单元个列
	 * @throws GeneralException 
	 */
	public void changeReportGrid3(String scopeids , String tabid ,String row , String col ) throws GeneralException{
		TnameBo tbo = new TnameBo(this.conn,tabid);
		this.reportTypes=String.valueOf(this.getReportFlag(tabid));
		ReportAnalyseHtmlBo rahb = new ReportAnalyseHtmlBo(this.conn);
		ArrayList list = rahb.getGridInfoList(Integer.parseInt(row),Integer.parseInt(col),tabid,tbo);
		if(list.size()>0){
			//归档表中的行列标识
			String col_archiveName = (String)list.get(0);
			String row_archiveName = (String)list.get(1);
			
			//表格对应的标题名字
			String tnameColName = ((String)list.get(2)).replaceAll("`","");
			String tnameRowName = ((String)list.get(3)).replaceAll("`","");
			this.reportGridTitle = tnameRowName +"-"+ tnameColName;
			this.chartDBList = this.createReportChartInfo2(tabid,row_archiveName,col_archiveName,scopeids);
		}
	}
	
	
	/**
	 * 单击归档表格数据图表联动
	 * @param unitCode    填报单位编码
	 * @param tabid       报表
	 * @param row_cols
	 * @throws GeneralException 
	 */
	public void changeReportGrid2(String unitCode , String tabid ,String row_cols) throws GeneralException{
		TnameBo tbo = new TnameBo(this.conn,tabid);
		ReportAnalyseHtmlBo rahb = new ReportAnalyseHtmlBo(this.conn);
		String[] temps=row_cols.substring(1).split("/");
		ArrayList rowColArray=new ArrayList();
		this.reportTypes=String.valueOf(this.getReportFlag(tabid));
		for(int i=0;i<temps.length;i++)
		{
			String[] a_temp=temps[i].split(",");
			ArrayList list = rahb.getGridInfoList(Integer.parseInt(a_temp[0]),Integer.parseInt(a_temp[1]),tabid,tbo);
			if(list.size()>0){
				//归档表中的行列标识
				String col_archiveName = (String)list.get(0);
				String row_archiveName = (String)list.get(1);
	//			表格对应的标题名字
				String tnameColName = ((String)list.get(2)).replaceAll("`","");
				String tnameRowName = ((String)list.get(3)).replaceAll("`","");
				
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("row_num", a_temp[1]);
				abean.set("col_num", a_temp[0]);
				abean.set("col_archiveName",col_archiveName);
				abean.set("row_archiveName",row_archiveName);
				abean.set("tnameColName",tnameColName);
				abean.set("tnameRowName",tnameRowName);
				
				rowColArray.add(abean);
			}
			
		}
		//表格对应的标题名字
		if(rowColArray.size()>0){
			this.reportGridTitle ="";
		 	this.chartDBList = this.createReportChartInfo(tabid,rowColArray,unitCode);
		}
	}
	
	/**
	 * 单击归档表格,单击选中一行，单击选中一列数据图表联动
	 * @param unitCode
	 * @param tabid
	 * @param row_cols
	 * @param selectType
	 * @throws GeneralException
	 */
	public void changeReportSelectGrids(String unitCode , String tabid ,String row_cols,String selectType,ArrayList yearList) throws GeneralException{
		TnameBo tbo = new TnameBo(this.conn,tabid);
		ReportAnalyseHtmlBo rahb = new ReportAnalyseHtmlBo(this.conn);
		String[] temps=row_cols.substring(1).split("/");
		ArrayList rowColArray=new ArrayList();
		this.reportTypes=String.valueOf(this.getReportFlag(tabid));
		for(int i=0;i<temps.length;i++)
		{
			String[] a_temp=temps[i].split(",");
			ArrayList list = rahb.getGridInfoList(Integer.parseInt(a_temp[0]),Integer.parseInt(a_temp[1]),tabid,tbo);
			if(list.size()>0){
				//归档表中的行列标识
				String col_archiveName = (String)list.get(0);
				String row_archiveName = (String)list.get(1);
				//表格对应的标题名字
				String tnameColName = ((String)list.get(2)).replaceAll("`","");
				String tnameRowName = ((String)list.get(3)).replaceAll("`","");
				
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("row_num", a_temp[1]);
				abean.set("col_num", a_temp[0]);
				abean.set("col_archiveName",col_archiveName);
				abean.set("row_archiveName",row_archiveName);
				abean.set("tnameColName",tnameColName);
				abean.set("tnameRowName",tnameRowName);
				
				rowColArray.add(abean);
			}
			
		}
		//表格对应的标题名字
		if(rowColArray.size()>0){
			this.reportGridTitle ="";
		 	this.chartDBList = this.createChangeReportChartInfo(tabid,rowColArray,unitCode,selectType,yearList);
		}
	}
	
	/**
	 * 根据选中数据的类型（选中点、一行、一列）创建归档表多单元格数据图表显示
	 * @param tabid
	 * @param row_cols
	 * @param unitCode
	 * @param selectType
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList createChangeReportChartInfo(String tabid ,ArrayList row_cols,String unitCode,String selectType,ArrayList yearList) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null,rset =null;
		ArrayList list = new ArrayList();
		int reportFlag = this.getReportFlag(tabid);
		HashMap dbMap=new HashMap();
		
		try {
			StringBuffer str = new StringBuffer();
			str.append("select distinct countid,yearid");
			if("6".equals(this.reportTypes)) {
                str.append(",weekid");
            }
			str.append(" from ta_"+tabid);
			str.append(" order by yearid");
			rset = dao.search(str.toString());
			while(rset.next()) {
				String time_str = "";
				int yearid = rset.getInt("yearid");
				//近几年
				boolean flag = false;
				for(int i=0;i<yearList.size();i++){
					if(yearid == (Integer)yearList.get(i)) {
                        flag = true;
                    }
				}
				if(!flag) {
                    continue;
                }
				int countid = rset.getInt("countid");
				String temp = this.getReportCountidInfo(reportFlag,countid);
				time_str=yearid+ResourceFactory.getProperty("kq.wizard.year")+temp;
				if("6".equals(this.reportTypes)){
					time_str+=ResourceFactory.getProperty("hmuster.label.d")+rset.getString("weekid")+ResourceFactory.getProperty("kq.wizard.week");
				}
				ArrayList commonList=new ArrayList();
				for(int i=0;i<row_cols.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)row_cols.get(i);
					String col_archiveName=(String)abean.get("col_archiveName");
					String row_archiveName=(String)abean.get("row_archiveName");
					String tnameColName=(String)abean.get("tnameColName");
					String tnameRowName=(String)abean.get("tnameRowName");
					
					StringBuffer sql = new StringBuffer();
					sql.append("select a.");
					sql.append(col_archiveName);
					if("6".equals(this.reportTypes)) {
                        sql.append(",weekid");
                    }
					sql.append(" , a.yearid , a.countid,a.unitcode from ta_");
					sql.append(tabid);
					sql.append(" a where   a.unitcode='");
					sql.append(unitCode);
					sql.append("' and a.yearid=");
					sql.append(yearid);
					sql.append(" and a.countid=");
					sql.append(countid);
					sql.append(" and a.row_item ='");
					sql.append(row_archiveName+"'");
					if(!"0".equals(this.scopeid)){
						sql.append(" and scopeid="+this.scopeid);
					}
				//	sql.append("' order by a.yearid,a.countid,a.unitcode ");
					if("6".equals(this.reportTypes)) {
                        sql.append(" order by a.yearid,a.countid,a.weekid,a.unitcode ");
                    } else {
                        sql.append(" order by a.yearid,a.countid,a.unitcode ");
                    }
					//String row_num=(String)abean.get("row_num");
					//String col_num=(String)abean.get("col_num");
					String distinct="";
					if("spot".equals(selectType)){
						distinct=tnameRowName +"-"+ tnameColName;//+"("+row_num+","+col_num+")";
					}else if("rows".equals(selectType)){
						distinct=tnameColName;//+"("+row_num+","+col_num+")";
					}else if ("cols".equals(selectType)) {
						distinct=tnameRowName;//+"("+row_num+","+col_num+")";
					}
					
					rs = dao.search(sql.toString());
					while(rs.next()) {
						double value = rs.getDouble(col_archiveName);
						CommonData vo=new CommonData(String.valueOf(value),distinct);
						commonList.add(vo);
					}
				}
				LazyDynaBean a_abean=new LazyDynaBean();
				a_abean.set("categoryName", time_str);
				a_abean.set("dataList",commonList);
				list.add(a_abean);
				dbMap.put(time_str, commonList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		this.chartDBmap=dbMap;
		return list;
	}

	/**
	 * 创建归档表多单元格数据图表显示
	 * @param tabid             标号
	 * @param row_cols
	 * @param unitCode          填报单位编码
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList createReportChartInfo(String tabid ,ArrayList row_cols,String unitCode ) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		int reportFlag = this.getReportFlag(tabid);
		HashMap dbMap=new HashMap();
		
		try {
			for(int i=0;i<row_cols.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)row_cols.get(i);
				String col_archiveName=(String)abean.get("col_archiveName");
				String row_archiveName=(String)abean.get("row_archiveName");
				String tnameColName=(String)abean.get("tnameColName");
				String tnameRowName=(String)abean.get("tnameRowName");
				
				StringBuffer sql = new StringBuffer();
				sql.append("select a.");
				sql.append(col_archiveName);
				if("6".equals(this.reportTypes)) {
                    sql.append(",weekid");
                }
				sql.append(" , a.yearid , a.countid,a.unitcode from ta_");
				sql.append(tabid);
				sql.append(" a where   a.unitcode='");
				sql.append(unitCode);
				sql.append("' and a.row_item ='");
				sql.append(row_archiveName+"'");
				if(!"0".equals(this.scopeid)){
					sql.append(" and scopeid="+this.scopeid);
				}
			//	sql.append("' order by a.yearid,a.countid,a.unitcode ");
				if("6".equals(this.reportTypes)) {
                    sql.append(" order by a.yearid,a.countid,a.weekid,a.unitcode ");
                } else {
                    sql.append(" order by a.yearid,a.countid,a.unitcode ");
                }
				String row_num=(String)abean.get("row_num");
				String col_num=(String)abean.get("col_num");
				String distinct=tnameRowName +"-"+ tnameColName+"("+row_num+","+col_num+")";
				
				rs = dao.search(sql.toString());
				ArrayList commonList=new ArrayList();
				while(rs.next()) {
					double value = rs.getDouble(col_archiveName);
					int yearid = rs.getInt("yearid");
					int countid = rs.getInt("countid");
					String temp = this.getReportCountidInfo(reportFlag,countid);
					String time_str=yearid+ResourceFactory.getProperty("kq.wizard.year")+temp;
					if("6".equals(this.reportTypes)) {
                        time_str+=ResourceFactory.getProperty("hmuster.label.d")+rs.getString("weekid")+ResourceFactory.getProperty("kq.wizard.week");
                    }
					CommonData vo=new CommonData(String.valueOf(value),time_str);
					commonList.add(vo);
				}
				
				LazyDynaBean a_abean=new LazyDynaBean();
				a_abean.set("categoryName", distinct);
				a_abean.set("dataList",commonList);
				list.add(a_abean);
				dbMap.put(distinct, commonList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
		this.chartDBmap=dbMap;
		return list;
	}
	
	
	
	
	
	
	/**
	 * 创建归档表单元格数据图表显示
	 * @param tabid             标号
	 * @param row_archiveName   数据行
	 * @param col_archiveName   数据列
	 * @param unitCode          填报单位编码
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList createReportChartInfo(String tabid , String row_archiveName , 
											String col_archiveName ,String unitCode ){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		int reportFlag = this.getReportFlag(tabid);
		
		StringBuffer unitcode_str=new StringBuffer("");
		String[] temps=unitCode.split(",");
		for(int i=0;i<temps.length;i++) {
            unitcode_str.append(",'"+temps[i]+"'");
        }
		
		HashMap dbMap=new HashMap();
		StringBuffer sql = new StringBuffer();
		sql.append("select a.");
		sql.append(col_archiveName);
		if("6".equals(this.reportTypes)) {
            sql.append(",weekid");
        }
		sql.append(" , a.yearid , a.countid,a.unitcode,b.unitname from ta_");
		sql.append(tabid);
		sql.append(" a,tt_organization b where a.unitcode=b.unitcode and a.unitcode in (");
		sql.append(unitcode_str.substring(1));
		sql.append(") and a.row_item ='");
		sql.append(row_archiveName);
		//sql.append("' order by a.yearid,a.countid,a.unitcode ");
		if("6".equals(this.reportTypes)) {
            sql.append("' order by a.yearid,a.countid,a.weekid,a.unitcode ");
        } else {
            sql.append("' order by a.yearid,a.countid,a.unitcode ");
        }
		//System.out.println(sql.toString());
		
		try {
			rs = dao.search(sql.toString());
			
			String distinct="";
			ArrayList commonList=new ArrayList();
			while(rs.next()) {
				double value = rs.getDouble(col_archiveName);
				int yearid = rs.getInt("yearid");
				int countid = rs.getInt("countid");
				String unitname=rs.getString("unitname");
				String temp = this.getReportCountidInfo(reportFlag,countid);
				String time_str=yearid+ResourceFactory.getProperty("kq.wizard.year")+temp;
				if("6".equals(this.reportTypes)) {
                    time_str+=ResourceFactory.getProperty("hmuster.label.d")+rs.getString("weekid")+ResourceFactory.getProperty("kq.wizard.week");
                }
				if("".equals(distinct)) {
                    distinct=time_str;
                }
				
				if(!distinct.equals(time_str))
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("categoryName", distinct);
					abean.set("dataList",commonList);
					list.add(abean);
					dbMap.put(distinct, commonList);
					commonList=new ArrayList();
					distinct=time_str;
				}
				CommonData vo=new CommonData(String.valueOf(value),unitname);
				commonList.add(vo);
			}
			if(!"".equals(distinct))
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("categoryName", distinct);
				abean.set("dataList",commonList);
				list.add(abean);
				dbMap.put(distinct, commonList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
//			throw GeneralExceptionHandler.Handle(e);
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						throw GeneralExceptionHandler.Handle(e1);
					}
			   }

			}*/
		this.chartDBmap=dbMap;
		return list;
	}
	/**
	 * 创建归档表单元格数据图表显示
	 * @param tabid             标号
	 * @param row_archiveName   数据行
	 * @param col_archiveName   数据列
	 * @param unitCode          填报单位编码
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList createReportChartInfo2(String tabid , String row_archiveName , 
											String col_archiveName ,String scopeids ){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		int reportFlag = this.getReportFlag(tabid);
		
		StringBuffer unitcode_str=new StringBuffer("");
		String sql2 = "select scopeid ,owner_unit,name from tscope ";
		HashMap map = new HashMap();
		HashMap map2 = new HashMap();
		try {
			rs = dao.search(sql2);
			while(rs.next()){
				map.put(rs.getString("scopeid"), rs.getString("owner_unit").replace("UM", "").replace("UN", ""));
				map2.put(rs.getString("scopeid"), rs.getString("name"));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] temps=scopeids.split(",");
		for(int i=0;i<temps.length;i++){
			if(map!=null&&map.get(temps[i])!=null) {
                unitcode_str.append(",'"+map.get(temps[i])+"'");
            }
		}
		
		HashMap dbMap=new HashMap();
		StringBuffer sql = new StringBuffer();
		sql.append("select a.");
		sql.append(col_archiveName);
		if("6".equals(this.reportTypes)) {
            sql.append(",weekid");
        }
		if(scopeids.length()>0){
			sql.append(",a.scopeid");
		}
		sql.append(" , a.yearid , a.countid,a.unitcode from ta_");
		sql.append(tabid);
		sql.append(" a where a.unitcode in (");
		sql.append(unitcode_str.substring(1));
		sql.append(") and a.row_item ='");
		sql.append(row_archiveName+"'");
		//sql.append("' order by a.yearid,a.countid,a.unitcode ");
		if(scopeids.length()>0){
			if(scopeids.endsWith(",")) {
                scopeids=scopeids.substring(0,scopeids.length()-1);
            }
			sql.append(" and a.scopeid in ("+scopeids+")");
		}
		if("6".equals(this.reportTypes)) {
            sql.append(" order by a.yearid,a.countid,a.weekid,a.unitcode ");
        } else {
            sql.append(" order by a.yearid,a.countid,a.unitcode ");
        }
		//System.out.println(sql.toString());
		
		try {
			rs = dao.search(sql.toString());
			
			String distinct="";
			ArrayList commonList=new ArrayList();
			while(rs.next()) {
				double value = rs.getDouble(col_archiveName);
				int yearid = rs.getInt("yearid");
				int countid = rs.getInt("countid");
				String unitname="";
				if(scopeids.length()>0&&map2!=null&&map2.get(rs.getString("scopeid"))!=null){
					unitname = (String)	map2.get(rs.getString("scopeid"));
				}
				
				String temp = this.getReportCountidInfo(reportFlag,countid);
				String time_str=yearid+ResourceFactory.getProperty("kq.wizard.year")+temp;
				if("6".equals(this.reportTypes)) {
                    time_str+=ResourceFactory.getProperty("hmuster.label.d")+rs.getString("weekid")+ResourceFactory.getProperty("kq.wizard.week");
                }
				if("".equals(distinct)) {
                    distinct=time_str;
                }
				
				if(!distinct.equals(time_str))
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("categoryName", distinct);
					abean.set("dataList",commonList);
					list.add(abean);
					dbMap.put(distinct, commonList);
					commonList=new ArrayList();
					distinct=time_str;
				}
				CommonData vo=new CommonData(String.valueOf(value),unitname);
				commonList.add(vo);
			}
			if(!"".equals(distinct))
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("categoryName", distinct);
				abean.set("dataList",commonList);
				list.add(abean);
				dbMap.put(distinct, commonList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
//			throw GeneralExceptionHandler.Handle(e);
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						throw GeneralExceptionHandler.Handle(e1);
					}
			   }

			}*/
		this.chartDBmap=dbMap;
		return list;
	}

	/**
	 * 获得报表列表集合
	 * @return
	 */
	public ArrayList getReportList(String sortid,String unitcode) {
		ArrayList areportList=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			TnameBo tnamebo  = new TnameBo(this.conn);
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0) {
                tabids=tabids.substring(0,tabids.length()-1);
            }
			StringBuffer strsql=new StringBuffer("");
			strsql.append("select reporttypes,analysereports from tt_organization  where  unitcode='");
			strsql.append(unitcode);
			strsql.append("'");				
			RowSet rs =dao.search(strsql.toString());
			String analysereports ="";
			if(rs.next())
			{
				 analysereports = Sql_switcher.readMemo(rs,"analysereports");
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select tc.tabid,tname.name from treport_ctrl tc,tname where tc.tabid=tname.tabid and tc.unitcode='"+unitcode+"'  and tname.tsortid="+sortid+" ");
			
			if(tabids.length()>0) {
                sql.append(" and tname.tabid not in("+tabids+") ");
            }
			sql.append(" order by tc.tabid ");
			if(analysereports!=null&&analysereports.length()>0){
				String reports [] =	analysereports.split(",");
				String reportids ="";
				TTorganization ttorganization=new TTorganization(this.conn);
				HashMap reportmap =  ttorganization.getReportTsort();
				for(int i=0;i<reports.length;i++){
					if(reports[i].trim().length()>0&&reportmap.get(reports[i].trim())!=null&&reportmap.get(reports[i].trim()).equals(sortid)){
						reportids+=reports[i].trim()+",";
					}
				}
				if(reportids.length()>1){
					reportids = reportids.substring(0,reportids.length()-1);
					sql.setLength(0);
					sql.append("select tabid,name from tname where ");
					sql.append(" tabid in ("+reportids+")");
					if(tabids.length()>0) {
                        sql.append(" and tname.tabid not in("+tabids+") ");
                    }
					sql.append(" order by tabid ");
				}
				}
			RowSet rowSet=dao.search(sql.toString());
			while(rowSet.next())
			{
				CommonData vo=new CommonData(rowSet.getString("tabid"),"("+rowSet.getString("tabid")+")"+rowSet.getString("name"));
				if(this.userView==null) {
                    areportList.add(vo);
                } else if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,rowSet.getString("tabid")))
				{
					areportList.add(vo);
				}
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return areportList;
	}
	/**
	 * 获得报表列表集合
	 * @return
	 */
	public ArrayList getReportList(String unitcode) {
		ArrayList areportList=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			TnameBo tnamebo  = new TnameBo(this.conn);
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0) {
                tabids=tabids.substring(0,tabids.length()-1);
            }
			String sql ="";
			if(tabids.length()>0){
				sql = "select tc.tabid,tname.name from treport_ctrl tc,tname where tc.tabid=tname.tabid and tc.unitcode='"+unitcode+"' and tname.tabid in("+tabids+")   order by tc.tabid";
			}else{
				sql = "select tc.tabid,tname.name from treport_ctrl tc,tname where tc.tabid=tname.tabid and tc.unitcode='"+unitcode+"'   order by tc.tabid";
			}
				
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				CommonData vo=new CommonData(rowSet.getString("tabid"),"("+rowSet.getString("tabid")+")"+rowSet.getString("name"));
				if(this.userView==null) {
                    areportList.add(vo);
                } else if(this.userView!=null&&userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,rowSet.getString("tabid")))
				{
					areportList.add(vo);
				}
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return areportList;
	}
	
	
	/**
	 * 获得初始化填报单位负责的特定报表的特定年份的的（月份/上下半年/次数/季度）集合（下拉列表）的描述信息即：显示值
	 * @param reportFlag 报表归档类型
	 * @param countid    特定报表的特定年份的的（月份/上下半年/次数/季度）DB中的值，下拉列表的value值
	 * @return
	 */
	public String getReportCountidInfo(int reportFlag ,int countid){
		String info = "";
		switch(reportFlag){
			case 1://一般报表
				this.reportCountInfo =ResourceFactory.getProperty("hmuster.label.counts")+":";
				info = countid + ResourceFactory.getProperty("hmuster.label.count");
				break;
			case 3: //半年报表
				this.reportCountInfo =ResourceFactory.getProperty("jx.khplan.halfyear")+":";
				if(countid == 1){
					info = ResourceFactory.getProperty("report.pigeonhole.uphalfyear");
				}else if(countid == 2){
					info = ResourceFactory.getProperty("report.pigeonhole.downhalfyear");
				}
				break;
			case 4://季度报表
				this.reportCountInfo =ResourceFactory.getProperty("jx.khplan.quarter")+":";
				switch(countid){
					case 1:
						info = ResourceFactory.getProperty("report.pigionhole.oneQuarter");
						break;
					case 2:
						info = ResourceFactory.getProperty("report.pigionhole.twoQuarter");
						break;
					case 3:
						info = ResourceFactory.getProperty("report.pigionhole.threeQuarter");
						break;
					case 4:
						info = ResourceFactory.getProperty("report.pigionhole.fourQuarter");
						break;
				}
				break;
			case 5://月报
				this.reportCountInfo =ResourceFactory.getProperty("gz.acount.month");
				switch(countid){
					case 1:
						info = ResourceFactory.getProperty("date.month.january");
						break;
					case 2:
						info = ResourceFactory.getProperty("date.month.february");
						break;
					case 3:
						info = ResourceFactory.getProperty("date.month.march");
						break;
					case 4:
						info = ResourceFactory.getProperty("date.month.april");
						break;
					case 5:
						info = ResourceFactory.getProperty("date.month.may");
						break;
					case 6:
						info =  ResourceFactory.getProperty("date.month.june");
						break;
					case 7:
						info = ResourceFactory.getProperty("date.month.july");
						break;
					case 8:
						info =ResourceFactory.getProperty("date.month.auguest");
						break;
					case 9:
						info = ResourceFactory.getProperty("date.month.september");
						break;
					case 10:
						info = ResourceFactory.getProperty("date.month.october");
						break;
					case 11:
						info = ResourceFactory.getProperty("date.month.november");
						break;
					case 12:
						info =  ResourceFactory.getProperty("date.month.december");
						break;
				}
				break;
			case 6://周报
				this.reportCountInfo =ResourceFactory.getProperty("gz.acount.month");
				switch(countid){
					case 1:
						info = ResourceFactory.getProperty("date.month.january");
						break;
					case 2:
						info = ResourceFactory.getProperty("date.month.february");
						break;
					case 3:
						info = ResourceFactory.getProperty("date.month.march");
						break;
					case 4:
						info = ResourceFactory.getProperty("date.month.april");
						break;
					case 5:
						info =  ResourceFactory.getProperty("date.month.may");
						break;
					case 6:
						info = ResourceFactory.getProperty("date.month.june");
						break;
					case 7:
						info =ResourceFactory.getProperty("date.month.july");
						break;
					case 8:
						info = ResourceFactory.getProperty("date.month.auguest");
						break;
					case 9:
						info = ResourceFactory.getProperty("date.month.september");
						break;
					case 10:
						info = ResourceFactory.getProperty("date.month.october");
						break;
					case 11:
						info = ResourceFactory.getProperty("date.month.november");
						break;
					case 12:
						info =  ResourceFactory.getProperty("date.month.december");
						break;
				}
				break;
					
		}
		
		return info;
		
	}

	
	/**
	 * 判断报表归档表中是否有数据
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	public boolean isTa_reportExistDB(String tabid , String unitCode) throws GeneralException{
		boolean b = false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String sql = "select * from ta_" + tabid + " where unitcode = '" + unitCode +"'";
		
		//System.out.println(sql.toString());
		
		rs = null;
		try {
			rs = dao.search(sql.toString());
			if(rs.next()) {
				b = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
						throw GeneralExceptionHandler.Handle(e1);
					}
			   }

			}*/
	
		return b;
	}
	
	/**
	 * 获取动态更新表格数据的字符串表现
	 * @param list
	 */
	public void getReportGridDBInfo(ArrayList list){
		StringBuffer result = new StringBuffer();
		for(int i = 0 ; i< list.size() ; i++){
			String [] temp = (String[]) list.get(i);
			for(int j = 0 ; j < temp.length; j++){
				result.append(i);
				result.append("##");
				result.append(j);
				result.append("##");
				if (temp[j]!=null&&Float.parseFloat(temp[j]) == 0) {
                    result.append("0");
                } else {
                    result.append(temp[j]);
                }
				result.append("@");
				
				//System.out.println(temp[j]);
			}
		}
		//System.out.println( result.toString());
		this.reportGridDBInfo = result.toString();
	}
	
	public String tableColumnChange(String tabid ,String userID , String userName,TnameBo tbo){
		String columnflag ="0";
		HashMap map = getTa_xColumn(tabid);
		for(int i=0;i<tbo.getRowInfoBGrid().size();i++)
		 {
		    	RecordVo vo=(RecordVo)tbo.getRowInfoBGrid().get(i);
		    	String fieldname="C"+(i+1);
		    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                    fieldname=vo.getString("archive_item");
                }
		    	if(map!=null&&map.get(fieldname)==null){
		    		columnflag="1";
		    	}
		}
		return columnflag;
	}
/**
 * 获得归档表的字段
 * @param tabid
 * @return
 */
	public HashMap getTa_xColumn(String tabid)
	{
		boolean issuccess=true;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		HashMap map = new HashMap();
		try
		{
				recset=dao.search("select * from ta_"+tabid+" where 1=2");
				ResultSetMetaData data=recset.getMetaData();
				
				for(int i=0;i<data.getColumnCount();i++)
				{
					String temp=data.getColumnName(i+1).toUpperCase().trim();
					map.put(temp,temp);
					
				}
				
				
				if(data!=null) {
                    data=null;
                }
					
		}
		catch(Exception e)
		{
			e.printStackTrace();
			issuccess=false;
		}
	
		return map;
	}
	
	/**
	 * 获得指定报表的总高度
	 * @param tabid
	 * @return
	 * @author liuy
	 */
	public String getRepoetAnalyseHeigh(String tabid) {
		String reportHeight = "";
		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		RowSet rs=null;
		try
		{
			int height = 0;
			sql.append("select rheight from TGrid3 where TabId=");
			sql.append(tabid);
			sql.append(" and (flag=0 or flag=2)");
			rs=dao.search(sql.toString());
			while(rs.next()){
				height = height + rs.getInt("rheight");
			}
			reportHeight = height+"";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return reportHeight;
	}
	
	/**
	 * 判断某填报单位表类下的报表是否至少有一个有权限，如有则返回true
	 * @param unitCode   填报单位
	 * @param sortid     表类号
	 * @param dao        
	 * @return
	 * wangcq 2014-12-25
	 */
	private boolean sortidResource(String unitCode, String sortid ,ContentDAO dao){
		boolean haveResource = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select tc.tabid,tname.name from treport_ctrl tc,tname where tc.tabid=tname.tabid and tc.unitcode='"+unitCode+"'  and tname.tsortid="+sortid+" ");
		sql.append(" order by tc.tabid ");
		RowSet rs1;
		try {
			rs1 = dao.search(sql.toString());
			while(rs1.next())
			{
				if(userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,rs1.getString("tabid"))) {
                    haveResource = true;
                }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return haveResource;
	}
	
	/**
	 * 获得特定报表特定年份信息列表（月份/上下半年/次数/季度）
	 * @return
	 */
	public ArrayList getReportCountidList() {
		return reportCountidList;
	}

	/**
	 * 获得报表列表集合
	 * @return
	 */
	public ArrayList getReportList() {
		return reportList;
	}

	/**
	 * 获得报表年份集合
	 * @return
	 */
	public ArrayList getReportYearidList() {
		return reportYearidList;
	}

	/**
	 * 获得Count描述信息
	 * @return
	 */
	public String getReportCountInfo() {
		return reportCountInfo;
	}

	/**
	 * 获取报表数据显示HTML
	 * @return
	 */
	public String getReportHtml() {
		return reportHtml;
	}


	public String getReportState() {
		return reportState;
	}


	public String getReportGridDBInfo() {
		return reportGridDBInfo;
	}


	public String getReportGridTitle() {
		return reportGridTitle;
	}


	public ArrayList getChartDBList() {
		return chartDBList;
	}


	public String getReportExist() {
		return reportExist;
	}


	public String getCurrentReport() {
		return currentReport;
	}


	public void setCurrentReport(String currentReport) {
		this.currentReport = currentReport;
	}


	public HashMap getChartDBmap() {
		return chartDBmap;
	}


	public void setChartDBmap(HashMap chartDBmap) {
		this.chartDBmap = chartDBmap;
	}


	public UserView getUserView() {
		return userView;
	}


	public void setUserView(UserView userView) {
		this.userView = userView;
	}


	public ArrayList getReportWeekList() {
		return reportWeekList;
	}


	public void setReportWeekList(ArrayList reportWeekList) {
		this.reportWeekList = reportWeekList;
	}


	public String getReportTypes() {
		return reportTypes;
	}


	public void setReportTypes(String reportTypes) {
		this.reportTypes = reportTypes;
	}


	public String getReportWeekInfo() {
		return reportWeekInfo;
	}


	public void setReportWeekInfo(String reportWeekInfo) {
		this.reportWeekInfo = reportWeekInfo;
	}


	public String getCountid() {
		return countid;
	}


	public void setCountid(String countid) {
		this.countid = countid;
	}


	public String getYearid() {
		return yearid;
	}


	public void setYearid(String yearid) {
		this.yearid = yearid;
	}


	public TnameBo getTnameBo() {
		return tnameBo;
	}


	public void setTnameBo(TnameBo tnameBo) {
		this.tnameBo = tnameBo;
	}


	public String getScopeid() {
		return scopeid;
	}


	public void setScopeid(String scopeid) {
		this.scopeid = scopeid;
	}


	public void setBrowseFlag(boolean browseFlag) {
		this.browseFlag = browseFlag;
	}


	
	
	
	
}
