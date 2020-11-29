package com.hjsj.hrms.interfaces.webservice;

import com.hjsj.hrms.businessobject.general.template.MatterTaskList;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.hire.ZpPendingtaskBo;
import com.hjsj.hrms.businessobject.infor.PersonMatterTask;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppList;
import com.hjsj.hrms.businessobject.kq.interfaces.KqMatterTask;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportBulletinList;
import com.hjsj.hrms.businessobject.report.report_isApprove.Report_isApproveBo;
import com.hjsj.hrms.businessobject.sys.dataimport.DataImportBo;
import com.hjsj.hrms.service.HrpServiceParam;
import com.hjsj.hrms.service.IssuanceServiceXml;
import com.hjsj.hrms.transaction.sys.warn.ScanTotal;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.codehaus.xfire.transport.http.XFireServletController;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HrIssuanceInterf implements HrpServiceParam {
	private UserView userView = null;
	private String username = "";
	private String password = "";
	private String user_falg = "";


	public HrIssuanceInterf() {
	}

	public UserView getSetView(String username, String password,
			String validatepwd, Connection conn) {
		
		
		//获取登录验证类路径
		String logonclass = SystemConfig.getPropertyValue("logonclass");
		if(logonclass!=null && logonclass.length()>0 && username!=null && username.length()>0){
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(conn);
			ArrayList<String> userlist = new ArrayList<String>();
			userlist.add(username);
			try {
				//判断是否是业务用户，如果是业务用户则不加前缀
				rs = dao.search("select username from operuser where username=?", userlist);
				if(!rs.next()){
					String logonprefix =  SystemConfig.getPropertyValue("logonprefix");
					if(logonprefix==null || logonprefix.trim().length()==0){
						//username = "u"+username;
					}else{
						username= logonprefix.trim()+username;
					}
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
			}
		}
		

		if (validatepwd != null && "false".equals(validatepwd))
			this.userView = new UserView(username, conn);
		else{
			 if (ConstantParamter.isEncPwd()&&password.length()>0) {//如果设置帐号密码加密且密码不是空，将密码加密。
				 Des des = new Des();
				 password=des.EncryPwdStr(password);
			 }
			this.userView = new UserView(username, password, conn);
		}
		try {
			//解决获取代办连接是旧链接问题 begin 给userview赋值锁版本。
			HttpServletRequest request = XFireServletController.getRequest();
			HttpSession session = request.getSession();
			EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
			if (lockclient==null) {							
	        	String lock=SystemConfig.getPropertyValue("lock_version");
				if(StringUtils.isBlank(lock)){//默认赋值目前最高版本，
					lock="76";
				}
				this.userView.setVersion(Integer.parseInt(lock));
				this.userView.setVersion_flag(1);
				System.out.println("必须有任意用户登录过系统，调用接口时才能获取正确的系统版本！");
			}
			else {
				this.userView.setVersion(lockclient.getVersion());
				this.userView.setVersion_flag(lockclient.getVersion_flag());
			}
			//解决获取代办连接是旧链接问题 end 
			this.username = username;
			if (!this.userView.canLogin())
				return null;
			
			// 加密的密码需要脱密
			 if (ConstantParamter.isEncPwd()) {
				 Des des = new Des();
				 this.password = des.DecryPwdStr(this.userView.getPassWord());
				
			} else {
				this.password = this.userView.getPassWord();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userView;
	}

	public String getHrSysWarn(Connection conn) {
		StringBuffer str = new StringBuffer();

		IssuanceServiceXml issuanceServiceXml = new IssuanceServiceXml();
		str.append(issuanceServiceXml.saveParamAttribute(
				getHrSysWarnList(conn), this.userView.getUserName()));
		return str.toString();
	}

	private ArrayList getHrSysWarnList(Connection conn) {
//		WarnScanResult st = new WarnScanResult();
//		ArrayList alTotal = st.getWarnScanResult(this.userView, conn);
		ScanTotal st = new ScanTotal(this.userView);
		ArrayList alTotal = st.execute();
		int iRows = alTotal.size();
		String url = "";
		String title = "";
		LazyDynaBean bean = null;
		ArrayList list = new ArrayList();
		for (int i = 0; i < iRows; i++) {
			CommonData cData = (CommonData) alTotal.get(i);
			if (this.user_falg != null && "bjzz".equals(this.user_falg))
				url = "/system/skippage.jsp?ship=warn&id="
						+ cData.getDataValue();
			else
				url = "/system/warn/result_manager.do?b_query=link&warn_wid="
						+ cData.getDataValue()
						+ "&etoken="
						+ PubFunc.convertUrlSpecialCharacter(PubFunc
								.convertTo64Base(this.username + ","
										+ this.password)) + "&appfwd=1";
			title = cData.getDataName();
			bean = new LazyDynaBean();
			bean.set("url", url);
			bean.set("title", title);
			bean.set("description", "Hrp预警通知");
			list.add(bean);
		}

		return list;
	}

	public String getHrBoardContent(Connection conn) {
		StringBuffer str = new StringBuffer();
		try {
			IssuanceServiceXml issuanceServiceXml = new IssuanceServiceXml();
			str.append(issuanceServiceXml.saveParamAttribute(
					getHrBoardContentList(conn), this.userView.getUserName()));
		} catch (Exception e) {

		}

		return str.toString();
	}

	public ArrayList getHrBoardContentList(Connection conn) {
		String a_tempstr = "("
				+ Sql_switcher.diffDays(Sql_switcher.sqlNow(), "approvetime")
				+ ")<period";
		String diff = "("
				+ Sql_switcher.diffDays(Sql_switcher.sqlNow(), "approvetime")
				+ ")";
		String sql = "select id,topic,viewcount,approvetime,priority," + diff + " days ";
		sql = sql + " from announce where approve=1 and " + a_tempstr;
		StringBuffer strsql = new StringBuffer();
		strsql.append(sql);
		strsql.append(" order by priority,createtime desc");
		ContentDAO dao = new ContentDAO(conn);
		String url = "";
		String title = "";
		String approvetime = "";
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {

			rs = dao.search(strsql.toString());
			LazyDynaBean bean = null;

			while (rs.next()) {
				String id = rs.getString("id");
				if (!(this.userView.isHaveResource(IResourceConstant.ANNOUNCE,
						id))) {
					continue;
				}

				approvetime = PubFunc.FormatDate(rs.getDate("approvetime"),"yyyy-MM-dd HH:mm:ss");				
				if(approvetime==null || approvetime.trim().length()<=0)
					approvetime = "";
				
				if (this.user_falg != null && "bjzz".equals(this.user_falg))
					url = "/system/skippage.jsp?ship=board&id=" + id;
				else
					url = "/selfservice/welcome/welcome.do?b_view=link&a_id="
							+ id
							+ "&appfwd=1&etoken="
							+ PubFunc.convertUrlSpecialCharacter(PubFunc
									.convertTo64Base(this.username + ","
											+ this.password));

				title = subText(rs.getString("topic"))
						+ "("
						+ (rs.getString("viewcount") == null
								|| rs.getString("viewcount").length() <= 0 ? "0"
								: rs.getString("viewcount")) + "次)";
				bean = new LazyDynaBean();
				bean.set("url", url);
				bean.set("title", title);
				bean.set("description", "Hrp系统公告");
				bean.set("datetime", approvetime);
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	/**
	 * 报表信息
	 * 
	 * @param conn
	 * @return
	 */
	public String getHrReportContent(Connection conn) {
		StringBuffer str = new StringBuffer();
		try {
			IssuanceServiceXml issuanceServiceXml = new IssuanceServiceXml();
			str.append(issuanceServiceXml.saveParamAttribute(
					getHrReportContentList(conn), this.userView.getUserName()));
		} catch (Exception e) {
			return getErrorMessage("得到报表时发生错误");
		}

		return str.toString();
	}

	private ArrayList getHrReportContentList(Connection conn) {
		String url = "";
		ArrayList list = new ArrayList();
		try {
			ReportBulletinList reportBulletinList = new ReportBulletinList(conn);
			ArrayList reportList = reportBulletinList
					.getReportList(this.userView);
			LazyDynaBean bean = null;

			if (reportList != null) {
				int j = 0;
				for (int i = 0; i < reportList.size(); i++) {
					RecordVo temp = (RecordVo) reportList.get(i);
					if (!(this.userView.isHaveResource(
							IResourceConstant.REPORT, temp.getString("tabid"))))
						continue;
					String hzname = temp.getString("name");
					j = hzname.indexOf(".");
					hzname = hzname.substring(j + 1);
					int status = temp.getInt("paper");
					String ctrollflag = "0";
					if (status == -1 || status == 0 || status == 2) {

					} else {
						ctrollflag = "1";
					}
					url = "/report/edit_report/reportSettree.do?b_query2=query&operateObject=1&operates=1&print=5&status="
							+ status
							+ "&ctrollflag="
							+ ctrollflag
							+ "&home=5&ver=5&flag=1&code="
							+ temp.getString("tabid")
							+ "&dbpre=&appfwd=1&etoken="
							+ PubFunc.convertUrlSpecialCharacter(PubFunc
									.convertTo64Base(this.username + ","
											+ this.password));
					bean = new LazyDynaBean();
					bean.set("url", url);
					bean.set("title", hzname);
					bean.set("description", "Hr报表信息");
					list.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 请假、加班、公出申请
	 * 
	 * @param conn
	 * @return
	 */
	public String getHrkqContent(Connection conn) {
		StringBuffer str = new StringBuffer();
		String url = "";
		try {
			/*
			//获取请假，考勤，公出接口-原逻辑注释，将方法做单独处理
			KqAppList bo = new KqAppList();
			// 请假信息
			ArrayList q15List = bo.getQ15List(this.userView.getA0100(),
					this.userView.getDbname());
			// 公出信息
			ArrayList q13List = bo.getQ13List(this.userView.getA0100(),
					this.userView.getDbname());
			// 加班信息
			ArrayList q11List = bo.getQ11List(this.userView.getA0100(),
					this.userView.getDbname());*/
			
			// 请假信息
			ArrayList q15List = this.getQ15List(this.userView.getA0100(),
					this.userView.getDbname());
			// 公出信息
			ArrayList q13List = this.getQ13List(this.userView.getA0100(),
					this.userView.getDbname());
			// 加班信息
			ArrayList q11List = this.getQ11List(this.userView.getA0100(),
					this.userView.getDbname());
			ArrayList list = new ArrayList();
			list = getAllKQList(q15List, list, "q15");
			list = getAllKQList(q13List, list, "q13");
			list = getAllKQList(q11List, list, "q11");
			IssuanceServiceXml issuanceServiceXml = new IssuanceServiceXml();
			str.append(issuanceServiceXml.saveParamAttribute(list,
					this.userView.getUserName()));
		} catch (Exception e) {
			return getErrorMessage("得到报表时发生错误");
		}

		return str.toString();
	}

	
	/**
	 * 获取请假，考勤，公出接口-获得已报批的公出信息
	 * @return
	 */
	public ArrayList getQ13List(String a0100, String nbase) {
		return getList("q13",a0100, nbase);
	}
	
	/**
	 *获取请假，考勤，公出接口- 获得已报批的加班信息
	 * @return
	 */
	public ArrayList getQ11List(String a0100, String nbase) {
		return getList("q11", a0100, nbase);
	}
	
	/**
	 * 获取请假，考勤，公出接口-获得已报批的请假信息
	 * @return
	 */
	public ArrayList getQ15List(String a0100, String nbase) {
		return getList("q15", a0100, nbase);
	}
	
	/**
	 * 获取请假，考勤，公出接口-获得报批的所有申请
	 * @return List<RecordVo>
	 */
	private ArrayList getList(String table, String a0100, String nbase) {
		if (a0100 == null || a0100.length() == 0) {
			return new ArrayList();
		}
		if (nbase == null || nbase.length() == 0) {
			return new ArrayList();
		}
		table = table.toLowerCase();
		String field = getField();
		if (field == null || field.length() == 0) {
			return  new ArrayList();
		}
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = AdminDb.getConnection();
			
			KqUtilsClass kqUtilsClass = new KqUtilsClass(conn, this.userView);
			
			String kq_start = "";
			String kq_end = "";
			ArrayList datelist = RegisterDate.getKqDayList(conn);
			if (datelist != null && datelist.size() > 0) {
				kq_start = datelist.get(0).toString();
				//kq_end = datelist.get(datelist.size() - 1).toString();
				Date date = new Date();
				kq_end = DateUtils.format(date, "yyyy") + "-12-31";
				
				if (kq_start != null && kq_start.length() > 0)
					kq_start = kq_start.replaceAll("\\.", "-");

				if (kq_end != null && kq_end.length() > 0)
					kq_end = kq_end.replaceAll("\\.", "-");
			}
			kq_start = kqUtilsClass.getSafeCode(kq_start);
			kq_end = kqUtilsClass.getSafeCode(kq_end);
			
			
			String column_z1 = table + "z1";
			String column_z3 = table + "z3";
			//String column_05 = table + "05";
			sql.append("select ");
			sql.append(table);
			sql.append("01,nbase,a0100,a0101,");
			sql.append(table);
			sql.append("05,");
			sql.append(table);
			sql.append("07 from ");
			sql.append(table);
			//sql.append(" where (");
			//sql.append(table);
			//sql.append("z5='02' or ");
			//sql.append(table);
			//sql.append("z5='08') and e01a1 in (");
			sql.append(" where ");
			sql.append(table);
			sql.append("z5='02' ");
			if(kq_start != null && kq_start.length() > 0 && kq_end != null	&& kq_end.length() > 0) {
				String z1 = kq_start + " 00:00:00";
				String z3 = kq_end + " 23:59:59";
				sql.append(" and ((" + column_z1 + ">="	+ Sql_switcher.dateValue(z1));
				sql.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(z3) + ")");
				sql.append(" or (" + column_z3 + ">=" + Sql_switcher.dateValue(z1));
				sql.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(z3) + ")");
				sql.append(" or (" + column_z1 + "<=" + Sql_switcher.dateValue(z1));
				sql.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z3) + ")");
				sql.append(")");
			}
			
			sql.append(" and e01a1 in (");
			sql.append("select e01a1 from k01 where ");
			sql.append(field);
			sql.append(" in (select e01a1 from ");
			sql.append(nbase);
			sql.append("a01 where a0100='");
			sql.append(a0100);
			sql.append("'))");
			
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			if(rs.next()) {
				RecordVo vo = new RecordVo(table);
				vo.setString(table + "01", rs.getString(table + "01"));
				vo.setString("nbase", rs.getString("nbase"));
				vo.setString("a0100", rs.getString("a0100"));
				vo.setString("a0101", rs.getString("a0101"));
				//vo.setString(table + "05", rs.getString(table + "05"));
				vo.setString(table + "07", rs.getString(table + "07"));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	
	/**
	 * 获取请假，考勤，公出接口-获取配置的直接上级字段值
	 * @return
	 */
	private String getField() {
		String field = "";
		String sql = "select str_value from constant where constant='PS_SUPERIOR'";
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				field = rs.getString("str_value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();				
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return field;
	}
	
	/**
	 * 请假、加班、公出申请
	 * 
	 * @param conn
	 * @return
	 */
	public String getHrkqContent(Connection conn, String type) {
		if ("0".equals(type)) {
			return getHrkqContent(conn);
		}
		StringBuffer str = new StringBuffer();
		String url = "";
		try {
			KqAppList bo = new KqAppList();
			// 请假信息
			ArrayList q15List = bo.getQ15List(this.userView.getA0100(),
					this.userView.getDbname());
			// 公出信息
			ArrayList q13List = bo.getQ13List(this.userView.getA0100(),
					this.userView.getDbname());
			// 加班信息
			ArrayList q11List = bo.getQ11List(this.userView.getA0100(),
					this.userView.getDbname());

			ArrayList list = new ArrayList();
			if ("2".equals(type)) {// 请假
				list = getAllKQList(q15List, list, "q15");
			} else if ("3".equals(type)) {// 公出
				list = getAllKQList(q13List, list, "q13");
			} else if ("1".equals(type)) {// 加班
				list = getAllKQList(q11List, list, "q11");
			}
			IssuanceServiceXml issuanceServiceXml = new IssuanceServiceXml();
			str.append(issuanceServiceXml.saveParamAttribute(list,
					this.userView.getUserName()));
		} catch (Exception e) {
			return getErrorMessage("得到报表时发生错误");
		}

		return str.toString();
	}

	/**
	 * 获取权限范围内第一个人员库前缀
	 * @return
	 */
	private String getDbPre()
	{
		ArrayList dblist=null;
		String pre="";		
		try
		{
			dblist=this.userView.getPrivDbList();
			if(dblist!=null&&dblist.size()>0){
				pre=(String)dblist.get(0);
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return pre;
	}
	
	/**
	 * 获取待办数据列表
	 * // P：绩效；T：人事异动;G:工资;W：日志；Z:招聘；L：工作纪实；B：报表审批；R:人员信息审核；All：所有待办
	 * 
	 * @param conn
	 * @return
	 */
	public ArrayList getHrMatterList(Connection conn,String types) {
		
		if(types == null || types.length() <= 0) {
			types = "All";
		}
		
		boolean isagent = this.userView.isBAgent();
		String dbper = getDbPre();
		String clientName = "";
		try {
			clientName = SystemConfig.getPropertyValue("clientName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList matterList=new ArrayList();
		try 
		{
			
			
			if (types.toUpperCase().indexOf("Z") != -1 || "All".equalsIgnoreCase(types)) {
				ZpPendingtaskBo zpbo = new ZpPendingtaskBo(conn, this.userView);
				ArrayList zpdatalist = zpbo.getZpapprDta();
				for(int m = 0; m < zpdatalist.size();m++){
					CommonData zpdata = (CommonData) zpdatalist.get(m);
									
					matterList.add(zpdata);
				}
			}
			
			
			
			MatterTaskList matterTaskList=new MatterTaskList(conn,this.userView);
			//解决获取代办连接是旧链接问题 begin
			matterTaskList.setReturnflag("12");
			//解决获取代办连接是旧链接问题 end
						
			if (types.toUpperCase().indexOf("K") != -1 || "All".equalsIgnoreCase(types)) {
				KqMatterTask kqMatterTask = new KqMatterTask(conn, this.userView);
				//考勤刷卡审批
				matterList = kqMatterTask.getKqCardTask(matterList);
				//加班申请审批待办
				matterList = kqMatterTask.getKqOvertimeTask(matterList); 
			}
			//我的工作纪实
			matterTaskList.setReturnURL("/templates/index/portal.do?b_query=link");
			matterTaskList.setTarget("_self");
			if("gw".equalsIgnoreCase(clientName)){//国家电网，代办只要绩效的
				//代理人则不显示 JiangHe 
				if(!isagent){
					if (types.toUpperCase().indexOf("P") != -1 || "All".equalsIgnoreCase(types)) {
						matterList=matterTaskList.getPerformancePending(matterList);  
//						matterList=matterTaskList.getScoreList(matterList);
					}
				}
			}else{
				//代理人则不显示 JiangHe 
				if(!isagent){
					//只有干警考核才有工作纪实
					if(clientName!=null && "gjkhxt".equalsIgnoreCase(clientName.trim())){
						if (types.toUpperCase().indexOf("L") != -1 || "All".equalsIgnoreCase(types)) {
							matterList = matterTaskList.getWorkPlanList(matterList);
						}
					}
					
				}
				
				if (types.toUpperCase().indexOf("T") != -1 || "All".equalsIgnoreCase(types)) {
		        	matterList=matterTaskList.getWaitTaskList(matterList);
		      //  	matterList=matterTaskList.getInstanceList(matterList);   //已处理的任务 不显示
		        	matterList=matterTaskList.getTmessageList(matterList);  
				}
	        	//代理人则不显示 JiangHe 
				if(!isagent){
					
					if (types.toUpperCase().indexOf("P") != -1 || "All".equalsIgnoreCase(types)) {
						matterList=matterTaskList.getPerformancePending(matterList);   
	//					matterList=matterTaskList.getScoreList(matterList);
					}
				}
	        	WorkdiarySelStr WorkdiarySelStr=new WorkdiarySelStr(); 
	        	WorkdiarySelStr.setReturnURL("/templates/index/portal.do?b_query=link");
	        	WorkdiarySelStr.setTarget("_self");
	        	//防止冲掉其他任务
	        	ArrayList listtemp = new ArrayList();
	        	//代理人则不显示 JiangHe 
				if(!isagent) {
					if (types.toUpperCase().indexOf("W") != -1 || "All".equalsIgnoreCase(types)) {
						listtemp=WorkdiarySelStr.getLogWaittask(conn, this.userView, matterList);
					}
				}
	        	if(listtemp!=null&&listtemp.size()>0)
	        		matterList =listtemp;
			}

        	if(matterList!=null) {
	        	for(int i=0;i<matterList.size();i++) {        	
	        		CommonData cData=(CommonData)matterList.get(i);
	        		String url = cData.getDataValue()+"&dbpre="+dbper+"&home=5&ver=5";
	    					
						
	    			cData.setDataValue(url);
	        	}
        	}
        	
        	
        	if (types.toUpperCase().indexOf("G") != -1 || "All".equalsIgnoreCase(types)) {
	        	SalaryPkgBo salaryPkgBo=new SalaryPkgBo(conn,this.userView); 
	//			ArrayList salarylist=salaryPkgBo.getEndorseRecords(); //审批薪资
				ArrayList salarylist=salaryPkgBo.getGzPending(); //审批薪资  读取待办表中数据   zhaoxg add 2014-7-25
				LazyDynaBean abean=new LazyDynaBean();
	        	if(salarylist!=null)
	        	{
		        	for(int i=0;i<salarylist.size();i++)
		        	{        	
	
		        		abean=(LazyDynaBean)salarylist.get(i);
		        		String url =  abean.get("url")+"&home=5&ver=5&itemid1=default";
						
						abean.set("url", url);
						matterList.add(abean);
		        		
		        	}
	        	}
        	}
        	
        	if (types.toUpperCase().indexOf("B") != -1 || "All".equalsIgnoreCase(types)) {
        		//------------------------报表审批  zhaoxg 2013-1-28--------------------------------
				Report_isApproveBo report_isApproveBo = new Report_isApproveBo(conn,this.userView);
				ArrayList approveList = new ArrayList();
				LazyDynaBean approvebean=new LazyDynaBean();
				approveList = report_isApproveBo.getApprovelist(approveList);
				if(approveList!=null){
					for(int t=0;t<approveList.size();t++){
	
						approvebean=(LazyDynaBean)approveList.get(t);
						String url =  approvebean.get("url")+"&home=5&ver=5";
						
						approvebean.set("url", url);
						matterList.add(approvebean);
					}
	
				}
        	
			
			
				ArrayList returnList = new ArrayList();
				LazyDynaBean returnbean=new LazyDynaBean();
				returnList = report_isApproveBo.getReturnList(returnList);
				if(returnList!=null){
					for(int t=0;t<returnList.size();t++){
	
		        		returnbean=(LazyDynaBean)returnList.get(t);
						String url =  returnbean.get("url")+"&home=5&ver=5";
						
						returnbean.set("url", url);
						matterList.add(returnbean);
					}
					
					
				}
			
        	}
			
        	
        	if (types.toUpperCase().indexOf("R") != -1 || "All".equalsIgnoreCase(types)) {
        		//------------------人员信息审核审批-----------
				ArrayList personChangeList = new PersonMatterTask(conn, userView).getPersonInfoChange();
				if(personChangeList!=null){
					for(int g=0;g<personChangeList.size();g++){
	
						CommonData cData=(CommonData)personChangeList.get(g); 
						String url =  cData.getDataValue()+"&home=5&ver=5";
						
						cData.setDataValue(url);
						matterList.add(cData);
					}
				}
        	}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return matterList;
			
	}	
			
			
		public String getHrMatterContent(Connection conn) {
			StringBuffer str = new StringBuffer();
			ArrayList list = new ArrayList();
			
			try {
			ArrayList matterList = getHrMatterList(conn, "All");
			LazyDynaBean bean = null;
			if (matterList != null) {

				for (int i = 0; i < matterList.size(); i++) {
					
					String name = "";
					String url = "";
					String applyname = "";
					if (matterList.get(i) instanceof CommonData) {
						CommonData cData = (CommonData) matterList.get(i);
	
						name = cData.getDataName();
						//解决获取代办连接是旧链接问题 begin
						if (PubFunc.isUseNewPrograme(this.userView)&&cData.getDataValue().startsWith("/module/template/templatemain/templatemain.html")){
			            	url=userView.getServerurl()+"/module/utils/jsp.do?br_query=link&param="
			                +SafeCode.encode(cData.getDataValue()) 
			                +"&appfwd=1&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))); 
			            }
			            else
			            {
				            url = cData.getDataValue() + 
				              "&appfwd=1&etoken=" + 
				              PubFunc.convertUrlSpecialCharacter(
				              PubFunc.convertTo64Base(new StringBuilder(String.valueOf(this.username)).append(",")
				              .append(this.password).toString())) ;
			            }		
						//解决获取代办连接是旧链接问题 end
						applyname = "";
						
					} else {
						LazyDynaBean getbean = (LazyDynaBean) matterList.get(i);
						
						name = (String) getbean.get("title");
						if(name==null || name.trim().length()<=0)
							name = (String) getbean.get("name");	
						//解决获取代办连接是旧链接问题 begin
						if (PubFunc.isUseNewPrograme(this.userView)&&((String)getbean.get("url")).startsWith("/module/template/templatemain/templatemain.html")){
			            	url=userView.getServerurl()+"/module/utils/jsp.do?br_query=link&param="
			                +SafeCode.encode((String)getbean.get("url"))
			                +"&appfwd=1&etoken="+(PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))); 
			            }
			            else
			            {
			            url = (String)getbean.get("url") + 
			              "&appfwd=1&etoken=" + 
			              PubFunc.convertUrlSpecialCharacter(
			              PubFunc.convertTo64Base(new StringBuilder(String.valueOf(this.username)).append(",")
			              .append(this.password).toString())) + 
			              "";
			            }
						//解决获取代办连接是旧链接问题 end	
					}
					
					if(url!=null&&url.indexOf("encryptParam")==-1){
						int index = url.indexOf("&");
						if(index>-1){
							String allurl = url.substring(0,index);
							String allparam = url.substring(index);
							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
					} 
					
					bean = new LazyDynaBean();
					bean.set("url", url);
					bean.set("title", name);
					bean.set("applyname", applyname);
					bean.set("description", "Hr待办任务");
					list.add(bean);
				}
			}
			IssuanceServiceXml issuanceServiceXml = new IssuanceServiceXml();
			
			if (this.userView.isBThreeUser()){
				str.append(issuanceServiceXml.saveParamAttribute(new ArrayList(),this.userView.getUserName()));
			} else {
				str.append(issuanceServiceXml.saveParamAttribute(list,this.userView.getUserName()));
			}
		} catch (Exception e) {
			return getErrorMessage("得到待办时发生错误");
		}

		return str.toString();
	}

	/**
	 * 待办信息
	 * 
	 * @param conn
	 * @return
	 */
	public String getHrMatterContent2(Connection conn) {
		StringBuffer str = new StringBuffer();
		
		String erroStr = "<?xml version='1.0' encoding='UTF-8'?><pendingWorks><totalnumber><![CDATA[0]]></totalnumber></pendingWorks>";
		try {
			
			
			ArrayList matterList = this.getHrMatterList(conn, "all");
			
			String logonUrl = SystemConfig.getPropertyValue("hrp_logon_url");
			ArrayList list = new ArrayList();
			str.append("<?xml version='1.0' encoding='UTF-8'?><pendingWorks><totalnumber><![CDATA[");
			if (matterList == null || matterList.size() == 0) {
				str.append("0");
			} else {
				str.append(matterList.size());
			}
			str.append("]]></totalnumber>");

			LazyDynaBean bean = null;
			if (matterList != null) {
				int j = 0;
				for (int i = 0; i < matterList.size(); i++) {
					String name = "";
					String url = "";
					if (matterList.get(i) instanceof CommonData) {
						CommonData cData = (CommonData) matterList.get(i);
	
						name = cData.getDataName();
						url = cData.getDataValue();
						
						
					
					} else {
						LazyDynaBean getbean = (LazyDynaBean) matterList.get(i);
						
						name = (String) getbean.get("title");
						if(name==null || name.trim().length()<=0)
							name = (String) getbean.get("name");							
						url = ((String) getbean.get("url"));
						
						
						
					}
					
					if(url!=null&&url.indexOf("encryptParam")==-1){
						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
						int index = url.indexOf("&");
						if(index>-1){
							String allurl = url.substring(0,index);
							String allparam = url.substring(index);
							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
					}
					
					// 以下是每条待办数据的相关信息
					str.append("<pendingwork>");
					// 待办名称或者待办的描述信息
					str.append("<taskName><![CDATA[");
					str.append(name);
					str.append("]]></taskName>");

					str.append("<appSendUID><![CDATA[");
					str.append("]]></appSendUID>");
					// 该业务系统待办类型，如：公司发文，项目流程，出差申请等
					str.append("<taskType><![CDATA[");
					str.append("待办信息");
					str.append("]]></taskType>");
					// 待办接收时间 格式必须是yyyy-mm-dd 24hh:MM:ss
					str.append("<sendTime><![CDATA[");
					str.append(DateUtils.FormatDate(new Date(),
							"yyyy-MM-dd HH:mm:ss"));
					str.append("]]></sendTime>");
					// 待办超期时间 格式必须是yyyy-mm-dd 24hh:MM:ss-->
					str.append("<endTime><![CDATA[");
					str.append(DateUtils.FormatDate(
							DateUtils.addMonths(new Date(), 12),
							"yyyy-MM-dd HH:mm:ss"));
					str.append("]]></endTime>");
					// 待办事项信息紧急程度，越小越紧急,0:特急 1:紧急 2:一般,缺省2 -->
					str.append("<priority><![CDATA[");
					str.append("2");
					str.append("]]></priority>");
					// 待办单点访问URL地址，必须可以实现单点登录，并且要能直接打开待办的处理页面 -->
					str.append("<url><![CDATA[");
					str.append(logonUrl + url);
					str.append("]]></url>");
					// 待办的描述信息，如果没有则可以为待办完整名称 -->
					str.append("<taskDesc><![CDATA[");
					str.append(name);
					str.append("]]></taskDesc>");
					str.append("</pendingwork>");
					
					

				}
			}

			str.append("</pendingWorks>");

		} catch (Exception e) {
			return erroStr;
		}

		return str.toString();
	}

	/**
	 * 待办
	 * 
	 * @param conn
	 * @return
	 */
	public String getZJHrMatterContent(Connection conn) {

		StringBuffer buff = new StringBuffer();
		ArrayList list = new ArrayList();
		try {
			
			ArrayList matterList = this.getHrMatterList(conn, "all");

			String logonUrl = SystemConfig.getPropertyValue("hrp_logon_url");

			buff.append("<?xml version='1.0' encoding='UTF-8'?>");
			buff.append("<cscec_data>");
			buff.append("<header send_time='");
			buff.append(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			buff.append("' data_rows='");
			buff.append(matterList.size());
			buff.append("' mess_type='ApprTask'>");
			buff.append("<sender>A19_HR</sender>");
			buff.append("<security encrypt='false'/>");
			buff.append("<transaction required='true' type='ALL'/>");
			buff.append("</header>");
			buff.append("<body>");
			buff.append("<datas name='ApprTask'>");

			LazyDynaBean bean = null;
			if (matterList != null) {
				for (int i = 0; i < matterList.size(); i++) {
					
					String name = "";
					String url = "";
					if (matterList.get(i) instanceof CommonData) {
						CommonData cData = (CommonData) matterList.get(i);
	
						name = cData.getDataName();
						url = cData.getDataValue();
						
						
					
					} else {
						LazyDynaBean getbean = (LazyDynaBean) matterList.get(i);
						
						name = (String) getbean.get("title");
						if(name==null || name.trim().length()<=0)
							name = (String) getbean.get("name");							
						url = ((String) getbean.get("url"));
						
						
						
					}
					
					url = url
							+ "&etoken="
							+ PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(this.username + "," + this.password))
							+ "&appfwd=1";
					
					
					if(url!=null&&url.indexOf("encryptParam")==-1){
						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
						int index = url.indexOf("&");
						if(index>-1){
							String allurl = url.substring(0,index);
							String allparam = url.substring(index);
							url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
					}
					
					bean = new LazyDynaBean();
					bean.set("url", url);
					bean.set("title", name);
					bean.set("description", "Hr待办任务");

					buff.append("<object action='I'>");
					buff.append("<props>");
					buff.append("<prop name='task_id' iskey='1'>");
					buff.append(getPendingCode());
					buff.append("</prop>");
					buff.append("<prop name='task_nm'>HR待办信息</prop>");
					buff.append("<prop name='task_titile'>");
					buff.append(name);
					buff.append("</prop>");
					buff.append("<prop name='task_status'>待办</prop>");
					buff.append("<prop name='task_nms'>");
					String username = this.userView.getUserFullName();
					if (username == null || username.length() <= 0) {
						username = this.userView.getUserName();
					}
					buff.append(username);
					buff.append("</prop>");
					buff.append("<prop name='ou_nm'>");

					String deptDesc = this.userView.getUserDeptDesc();
					if (null != deptDesc) {
						buff.append(deptDesc);
					}

					buff.append("</prop>");
					buff.append("<prop name='end_date'></prop>");
					buff.append("<prop name='task_url'>");
					buff.append("<![CDATA[" + logonUrl + url + "]]>");
					buff.append("</prop>");
					buff.append("</props>");
					buff.append("</object>");

				}
			}

			buff.append("</datas>");
			buff.append("</body>");
			buff.append("</cscec_data>");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buff.toString();
	}

	// 待办信息在应用系统的唯一标识代号
	private String getPendingCode() {
		Date d = new Date();
		return "HRMS-" + d.getTime()
				+ Math.round(Math.ceil(Math.random() * 10));
	}

	private ArrayList getAllKQList(ArrayList datalist, ArrayList xmlList,
			String table) {
		LazyDynaBean bean = null;
		for (int i = 0; i < datalist.size(); i++) {
			RecordVo vo = (RecordVo) datalist.get(i);
			String url = "/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table="
					+ table
					+ "&returnvalue=&sp_flag=03&appfwd=1&etoken="
					+ PubFunc.convertUrlSpecialCharacter(PubFunc
							.convertTo64Base(this.username + ","
									+ this.password));
			bean = new LazyDynaBean();
			bean.set("url", url);
			if("q15".equals(table)) {
				bean.set("title", "请假申请");
				bean.set("description", "Hr请假信息");
				
			}else if("q13".equals(table)) {
				bean.set("title", "公出申请");
				bean.set("description", "Hr公出信息");
				
			}else if("q11".equals(table)) {
				bean.set("title", "加班申请");
				bean.set("description", "Hr加班信息");
			}
			/*bean.set("title", "请假申请(" + vo.getString("a0101") + ")");
			bean.set("description", "Hr考勤请假信息");*/
			xmlList.add(bean);
		}
		return xmlList;
	}

	public String getHrStatics(Connection conn) {
		StringBuffer str = new StringBuffer();
		try {
			IssuanceServiceXml issuanceServiceXml = new IssuanceServiceXml();
			str.append(issuanceServiceXml.saveParamAttribute(
					getHrStaticsList(conn), this.userView.getUserName()));
		} catch (Exception e) {

			e.printStackTrace();
		}
		return str.toString();
	}

	public ArrayList getHrStaticsList(Connection conn) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from sname where infokind=1 order by snorder");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			String url = "";
			String title = "";
			int j = 0;
			rs = dao.search(sql.toString());
			LazyDynaBean bean = null;

			while (rs.next()) {
				if (!(this.userView.isHaveResource(IResourceConstant.STATICS,
						rs.getString("id"))))
					continue;
				String hzname = rs.getString("name");// (String)statvo.get("name");
				j = hzname.indexOf(".");
				hzname = hzname.substring(j + 1);
				
				String dbpriv = "Usr";
				List dblist = (List)this.userView.getPrivDbList();
				if(dblist.size() > 0) {
					dbpriv = (String) dblist.get(0);
				}
				
				if ("1".equals(rs.getString("type"))) {
					url = "/general/static/commonstatic/statshow.do?b_chart=link&querycond=&infokind=1&isshowstatcond=1&home=1&ver=5&userbase=" + dbpriv + "&statid="+ rs.getString("id");
				} else {
					url = "/general/static/commonstatic/statshow.do?b_doubledata=data&querycond=&infokind=1&isshowstatcond=1&home=1&ver=5&userbase=" + dbpriv + "&statid="+ rs.getString("id");
				}
				
				url = url
						+ "&appfwd=1&etoken="
						+ PubFunc.convertUrlSpecialCharacter(PubFunc
								.convertTo64Base(this.username + ","
										+ this.password));
				title = hzname;
				bean = new LazyDynaBean();
				bean.set("url", url);
				bean.set("title", title);
				bean.set("description", "Hrp常用统计");
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	public String getErrorMessage(String message) {
		IssuanceServiceXml issuanceServiceXml = new IssuanceServiceXml();
		return issuanceServiceXml.errorMessage(message);
	}

	private String subText(String text) {
		if (text == null || text.length() <= 0)
			return "";
		if (text.length() < 18)
			return text;
		text = text.substring(0, 18) + "...";
		return text;
	}

	/**
	 * 调用webservice，验证他们的信息是否正确
	 * 
	 * @param usercode
	 *            当前用户的身份证号码，
	 * @param token
	 * @return
	 */
	public boolean checkZJValide(String token) {
		boolean flag = true;
		String zj_valided = SystemConfig.getPropertyValue("zj_valided");
		if(zj_valided!=null && "1".equals(zj_valided))
		{
    		String zj_key = SystemConfig.getPropertyValue("zj_valide_key");
    		String zj_url = SystemConfig.getPropertyValue("zj_valide_url");
    		String Namespace = SystemConfig.getPropertyValue("zj_valide_namespace");
    		flag = invalide(Namespace, "valide", new String[] { "token", "key" },
    				zj_url, new String[] { token, zj_key });
		}
		return flag;
	}

	/**
	 * 根据空间名和参数名获得webservice接口返回的数据（解决调用.net的webservice时出错的问题）
	 * 
	 * @param targetNamespace
	 *            空间名xml文件中targetNamespace的值
	 * @param paramName
	 *            方法sendSyncMsg中的参数名 （.net程序必须为xmlMessage）
	 * @param url
	 *            webservice的url，不带?wsdl
	 * @param paramName
	 *            参数值
	 * @return 调用webservice返回的结果
	 */
	private boolean invalide(String targetNamespace, String methodName,
			String paramName[], String url, String[] paramValue) {
		boolean flag = false;

		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(url));
			call.setReturnType(XMLType.XSD_STRING);
			call.setUseSOAPAction(true);
			call.setOperationName(new QName(targetNamespace, methodName));
			call.addParameter(new QName(targetNamespace, paramName[0]),
					XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter(new QName(targetNamespace, paramName[1]),
					XMLType.XSD_STRING, ParameterMode.IN);
			call.setSOAPActionURI(targetNamespace + methodName);
			// paramValue = new
			// String(paramValue.getBytes(),"ISO-8859-1");//如果没有加这段，中文参数将会乱码
			flag = ((Boolean) call.invoke(new Object[] { paramValue[0],
					paramValue[1] })).booleanValue();
			// mess = new
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}

   public boolean checkKey(String key,String flag)
   {
	   
	   if(flag==null||flag.length()<=0)
		   flag="bjzz";
	   
	   this.user_falg=flag;
	   int inFlag=getInFlag(flag);
	   
	   String md5Password="B6J2E1E1A3A6E3E5JB";
	   String account="";
	   String random="";
	   
	   switch(inFlag)
	   {
	       case bj_zzjt: //住总集团
	    	   md5Password="B6J2Z1Z1A3A6J3T5JB";
	    	   random="090602";
	    	   account="BJZZJT";
	    	   break;
	       case cscec_4: //中建四
	    	   md5Password="C6S2C1E1C3A4J3T5HJ";
	    	   random="090903";
	    	   account="BJXLJ";
	    	   break;
	       case cni_23: //核二三:ORs1P9DMem9byoEeSArcK+T60iqKxo6h2M7ONzAhR7w=
	    	   md5Password="CNIC6S2C1E1C3A4J323";
	    	   random="101109";
	    	   account="cni_23";	    	   
	    	   break;
	       case cncec://化学工程：ZkMhYprHJ9ZvBMCpzsT3+1YfsDXkcy6R7iEniGcg8Ug8fMlpbBDWHw==	                            
	    	   md5Password="b5e988dddaa94c3c750912cbbe1ce79e";
	    	   random="101110";
	    	   account="cncec";
	    	   break;
	       case cscec: //中建股份公司:7WH0c+64J1jHNYzaaf2mOXHpjUmPRcjA
	           md5Password = "B6J2E1E1A3A6E3E5JB";
	           random = "727021";
	           account = "cscec";
	           break;
	       case cetc_11: //中电科技11所：S4th2PwSSHhcRz7J7IiA8yxhhJqOtuj+
	           random = "019021";
	           account = "cetc_11";
	   }
	   
	   Des des=new Des();	  
	   String token_data=des.EncryPwdStr(md5Password+random,account);   
	   if(key!=null&&key.equals(token_data))
		   return true;
	   else
		   return false;
   }
   private int getInFlag(String flag)
   {
	  if(flag!=null&& "bjzz".equals(flag))
		  return bj_zzjt;
	  else if(flag!=null&& "cscec4".equals(flag))
		  return cscec_4;
	  else if(flag!=null&& "cni23".equals(flag))
		  return cni_23;
	  else if(flag!=null&& "cncec".equals(flag))
		  return cncec;
	  else if(flag!=null&& "cscec".equals(flag))
	      return cscec;
	  else if(flag!=null&& "cetc11".equals(flag))
	      return cetc_11;
	  return 0;
   }
   
   /**
    * 获得种类代号
    * @param kind
    * @return
    */
   private int getKind (String kind) {
	   if ("S".equalsIgnoreCase(kind)) {
		   return S;
	   } else if ("B".equalsIgnoreCase(kind)) {
		   return B;
	   } else if ("B".equalsIgnoreCase(kind)) {
		   return B;
	   } else if ("W".equalsIgnoreCase(kind)) {
		   return W;
	   } else if ("R".equalsIgnoreCase(kind)) {
		   return R;
	   } else if ("K".equalsIgnoreCase(kind)) {
		   return K;
	   } else if ("M".equalsIgnoreCase(kind)) {
		   return M;
	   } else if ("N".equalsIgnoreCase(kind)) {
		   return N;
	   }
	   
	   return 0;
   }
   
   
   public String getInfo(Connection conn, String kind, String type) {
	   ArrayList matterList = new ArrayList();
	   StringBuffer str = new StringBuffer();
	   IssuanceServiceXml issuanceServiceXml=new IssuanceServiceXml();   
	   try  {	
		   switch (getKind(kind)) {
		   		case 101:// 常用统计
		   			matterList.addAll(getHrStaticsList(conn));	
		   			
		   			str.append(issuanceServiceXml.saveParamAttribute(matterList, this.userView.getUserName()));	
		   			break;
		   			
		   		case 102:// 公告
		   			matterList.addAll(getHrBoardContentList(conn));
		   			str.append(issuanceServiceXml.saveParamAttribute(matterList, this.userView.getUserName()));	
		   			break;
		   		case 103: // 预警
		   			matterList.addAll(getHrSysWarnList(conn));
		   			str.append(issuanceServiceXml.saveParamAttribute(matterList, this.userView.getUserName()));	
		   			break;
		   			
		   		case 104: //报表
		   			matterList.addAll(getHrReportContentList(conn));
		   			str.append(issuanceServiceXml.saveParamAttribute(matterList, this.userView.getUserName()));	
		   			break;
		   		case 105: // 考勤申请
		   			KqAppList bo = new KqAppList();
		 		   // 请假信息
		 		   ArrayList q15List = bo.getQ15List(this.userView.getA0100(), this.userView.getDbname());
		 		   // 公出信息
		 		   ArrayList q13List  = bo.getQ13List(this.userView.getA0100(), this.userView.getDbname());
		 		   // 加班信息
		 		   ArrayList q11List = bo.getQ11List(this.userView.getA0100(), this.userView.getDbname());

		 		   ArrayList list= new ArrayList();
		 		    
		 		   if (!type.contains("1") && !type.contains("2") && !type.contains("3") ){
		 			  list = getAllKQList(q15List, list, "q15");
		 			  list = getAllKQList(q13List, list, "q13");
		 			  list = getAllKQList(q11List, list, "q11");
		 		   } else {
		 			  if (type.contains("1")){// 加班
			 			   list = getAllKQList(q11List, list, "q11");
			 		   }
		 			  if (type.contains("2")) {// 请假
			 			   list = getAllKQList(q15List, list, "q15");
			 		   } 
			 		   if (type.contains("3")){// 公出
			 			   list = getAllKQList(q13List, list, "q13");
			 		   } 		 		   
		 		   }
		 		   
		 		  matterList.addAll(list);
		 		  str.append(issuanceServiceXml.saveParamAttribute(matterList, this.userView.getUserName()));	
		   			break;
		   		case 106: {// 代办
		   			MatterTaskList matterTaskList=new MatterTaskList(conn,this.userView);
		   			KqMatterTask kqMatter = new KqMatterTask(conn, userView);
		   			ArrayList li = new ArrayList();
		   			if (!type.contains("T") && !type.contains("P") && !type.contains("G") && !type.contains("W") && !type.contains("K")&& !type.contains("Z")&& !type.contains("L")&& !type.contains("B")&& !type.contains("R")) {// P：绩效；T：人事异动;G:工资;W：日志；Z:招聘；L：工作纪实；B：报表审批；R:人员信息审核；
		   				
		   				li = this.getHrMatterList(conn, "All");
		   			    	
		   			    	
		   			} else {
		   				li = this.getHrMatterList(conn, type);

		   			}
		   			
		   			LazyDynaBean bean=null;
	   			    if (li != null) {
	   			    	
	   			    	
	   			    	for (int i = 0; i < matterList.size(); i++) {
	   						
	   						String name = "";
	   						String url = "";
	   						if (matterList.get(i) instanceof CommonData) {
	   							CommonData cData = (CommonData) matterList.get(i);
	   		
	   							name = cData.getDataName();
	   							url = cData.getDataValue();
	   							
	   							
	   						
	   						} else {
	   							LazyDynaBean getbean = (LazyDynaBean) matterList.get(i);
	   							
	   							name = (String) getbean.get("title");
	   							if(name==null || name.trim().length()<=0)
	   								name = (String) getbean.get("name");							
	   							url = ((String) getbean.get("url"));
	   							
	   							
	   							
	   						}
	   						
	   						url = url
	   								+ "&etoken="
	   								+ PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(this.username + "," + this.password))
	   								+ "&appfwd=1";
	   						
	   						
	   						if(url!=null&&url.indexOf("encryptParam")==-1){
	   							//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
	   							int index = url.indexOf("&");
	   							if(index>-1){
	   								String allurl = url.substring(0,index);
	   								String allparam = url.substring(index);
	   								url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
	   							}
	   							//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
	   						}
	   			    	
	   						bean = new LazyDynaBean();
	   						bean.set("url", url);
	   						bean.set("title", name);
	   						bean.set("description", "Hr待办任务");
	   						matterList.add(bean);
	   			    	
	   				}
	   			    }
	   			    str.append(issuanceServiceXml.saveParamAttribute(matterList, this.userView.getUserName()));	
		   			break;
		   		}
		   			
		   		case 110: // 代办条数
		   		{
		   			MatterTaskList matterTaskList=new MatterTaskList(conn,this.userView);
		   			KqMatterTask kqMatter = new KqMatterTask(conn, userView);
		   			ArrayList li = new ArrayList();
		   			if (!type.contains("T") && !type.contains("P") && !type.contains("G") && !type.contains("W") && !type.contains("K")&& !type.contains("Z")&& !type.contains("L")&& !type.contains("B")&& !type.contains("R")) {// P：绩效；T：人事异动;G:工资;W：日志；Z:招聘；L：工作纪实；B：报表审批；R:人员信息审核；
		   				li= this.getHrMatterList(conn, "All");
		   			    		   			    	
		   			} else {
		   				
		   				li = this.getHrMatterList(conn, type);

		   			}
		   			str.append(li.size());	
		   			break;
		   		}
		   
		   }
		   
		   		   		
	   }catch(Exception e) {
		   return getErrorMessage("得到报表时发生错误");
	   }
	   
	   return str.toString();
   }
   
   public static void main(String[] args) {
//	   String md5Password = getMD5Str("cncec");
//	   System.out.println(md5Password);
//	   String random = "101110";
//	   String account = "cncec";
//	   Des des=new Des();	  
//	   String token_data=des.EncryPwdStr(md5Password+random,account);
//	   System.out.println(token_data);
	   
	   

//	   System.out.println(PubFunc.convertUrlSpecialCharacter(PubFunc
//					.convertTo64Base("su" + ","
//							+ "8555hdtc")));
	   String etoken = PubFunc.convert64BaseToString(URLDecoder.decode("d3l0LC93VVhRajFGaE1zckFSdzVlRit3Z3c9PQ%3D%3D"));
	   String[] str = etoken.split(",");
	   Des des = new Des();
	   System.out.println(des.DecryPwdStr(str[1]));

   }
   
	private static String getMD5Str(String str) {  
		MessageDigest messageDigest = null;  

		try {  
		   	messageDigest = MessageDigest.getInstance("MD5");  
		   	messageDigest.reset();  
		   	messageDigest.update(str.getBytes("UTF-8"));  
		} catch (NoSuchAlgorithmException e) {  
			System.out.println("NoSuchAlgorithmException caught!");  
			  
		} catch (UnsupportedEncodingException e) {  
	   		e.printStackTrace();  
		}  
	   
		byte[] byteArray = messageDigest.digest();  
		StringBuffer md5StrBuff = new StringBuffer(); 
		
	    for (int i = 0; i < byteArray.length; i++) {              
	    	if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
	    		md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
	    	else  
	    		md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
	    }  
	 
	    return md5StrBuff.toString();  
	}  

	/**
	 * 外部系统调用此方法，通知 e-HR系统执行数据导入功能
	 * 
	 * @param xml_param
	 *            <?xml version="1.0" encoding="GB2312"?>
	 *            <hr>
	 *            <params> <!--数据导入/映射关系的作业类标识，程序需按哪几个映射关系执行导入操作-->
	 *            <taskinfo>xxxx</taskinfo> <!--外部数据过滤条件,SQL片段-->
	 *            <filter_str><![CDATA[ b0125 is not null]]> </filter_str>
	 *            <!--eHR数据保护条件,SQL片段,如果无保护条件，此节点不存在--> <protect_str><![CDATA[
	 *            b0125 is not null]]></ protect _str>
	 *            <!--目标库信息，需执行移库操作移库操作默认将原库信息删掉,如果不执行移库操作，此节点不存在-->
	 *            <to_nbase>Oth</to_nbase> </params>
	 *            </hr>
	 * 
	 * @return 1:成功 2：失败
	 * @author dengc
	 */
	public String impInfoByMidtable(String xml_param, Connection conn) {
		String flag = "2";
		try {
			LazyDynaBean abean_param = getParamBean(xml_param);
			DataImportBo bo = new DataImportBo(conn);
			flag =bo.innerImportData(conn, (String) abean_param.get("taskinfo"),
					(String) abean_param.get("protect_str"),
					(String) abean_param.get("filter_str"), "rsyd", abean_param);

		} catch (Exception e) {
			flag = "2";
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 读取xml中的参数信息
	 * 
	 * @param xml_param
	 * @return
	 * @throws GeneralException
	 */
	private LazyDynaBean getParamBean(String xml_param) throws GeneralException {
		LazyDynaBean paramBean = new LazyDynaBean();
		try {

			Document a_doc = PubFunc.generateDom(xml_param);
			Element element = null;

			XPath xPath = XPath.newInstance("/hr/params/taskinfo");
			element = (Element) xPath.selectSingleNode(a_doc);
			if (element != null) {
				paramBean.set("taskinfo", element.getValue());
			} else
				throw GeneralExceptionHandler.Handle(new Exception(
						"没有定义数据导入/映射关系的作业类标识！"));

			xPath = XPath.newInstance("/hr/params/filter_str");
			element = (Element) xPath.selectSingleNode(a_doc);
			if (element != null) {
				paramBean.set("filter_str", element.getValue());
			} else
				paramBean.set("filter_str", "");

			xPath = XPath.newInstance("/hr/params/protect_str");
			element = (Element) xPath.selectSingleNode(a_doc);
			if (element != null) {
				paramBean.set("protect_str", element.getValue());
			} else
				paramBean.set("protect_str", "");

			xPath = XPath.newInstance("/hr/params/to_nbase");
			element = (Element) xPath.selectSingleNode(a_doc);
			if (element != null) {
				paramBean.set("to_nbase", element.getValue());
			} else
				paramBean.set("to_nbase", "");

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return paramBean;
	}
	
	

}
