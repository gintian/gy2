package com.hjsj.hrms.service.core.http;

import com.hjsj.hrms.businessobject.general.template.MatterTaskList;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.hire.ZpPendingtaskBo;
import com.hjsj.hrms.businessobject.infor.PersonMatterTask;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppList;
import com.hjsj.hrms.businessobject.kq.interfaces.KqMatterTask;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportBulletinList;
import com.hjsj.hrms.businessobject.report.report_isApprove.Report_isApproveBo;
import com.hjsj.hrms.service.HrpServiceParam;
import com.hjsj.hrms.transaction.sys.warn.ScanTotal;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HrHttpIssuanceInterf implements HrpServiceParam {
	private UserView userView = null;
	private String username = "";
	private String password = "";
	private String user_falg = "";

	public HrHttpIssuanceInterf() {
	}

	/**
	 * 设置UserView对象
	 * @param username
	 * @param password
	 * @param validatepwd
	 * @param conn
	 * @param request
	 * @return
	 * @throws GeneralException
	 */
	public UserView getSetView(String username, String password,
			String validatepwd, Connection conn, HttpServletRequest request) throws GeneralException {
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
		else
			this.userView = new UserView(username, password, conn);
		//解决获取代办连接是旧链接问题 begin 给userview赋值锁版本。
		try {
			HttpSession session = request.getSession();
			EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
			this.userView.setVersion(lockclient.getVersion());//获取锁版本 70/60
			this.userView.setVersion_flag(lockclient.getVersion_flag());// 1:专业版 0:标准版
		} catch (Exception e1) {
			throw new GeneralException("request获取session为空");
		}
		//解决获取代办连接是旧链接问题 end 
		try {
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

	/**
	 * 当前人员的预警信息
	 * @param conn
	 * @return
	 */
	public String getHrSysWarn(Connection conn) {
		StringBuffer str = new StringBuffer();

		IssuanceServiceJson IssuanceServiceJson = new IssuanceServiceJson();
		str.append(IssuanceServiceJson.saveParamAttribute(
				getHrSysWarnList(conn), this.userView.getUserName()));
		return str.toString();
	}
	private ArrayList getHrSysWarnList(Connection conn) {
		//WarnScanResult st = new WarnScanResult();
		//ArrayList alTotal = st.getWarnScanResult(this.userView, conn);
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

	/**
	 * 当前人员的公告信息
	 * @param conn
	 * @return
	 */
	public String getHrBoardContent(Connection conn) {
		StringBuffer str = new StringBuffer();
		try {
			IssuanceServiceJson IssuanceServiceJson = new IssuanceServiceJson();
			str.append(IssuanceServiceJson.saveParamAttribute(
					getHrBoardContentList(conn), this.userView.getUserName()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str.toString();
	}
	private ArrayList getHrBoardContentList(Connection conn) {
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
							+ "&etoken="
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
	 * @param conn
	 * @return
	 */
	public String getHrReportContent(Connection conn) {
		StringBuffer str = new StringBuffer();
		try {
			IssuanceServiceJson IssuanceServiceJson = new IssuanceServiceJson();
			str.append(IssuanceServiceJson.saveParamAttribute(
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
							+ "&dbpre=&+&etoken="
							+ PubFunc.convertUrlSpecialCharacter(PubFunc
									.convertTo64Base(this.username + ","
											+ this.password))
							+ "&appfwd=1";//链接进不去,加上appfwd=1  2018-3-21
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
	 * @param conn
	 * @return
	 */
	public String getHrkqContent(Connection conn) {
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
			list = getAllKQList(q15List, list, "q15");
			list = getAllKQList(q13List, list, "q13");
			list = getAllKQList(q11List, list, "q11");
			IssuanceServiceJson IssuanceServiceJson = new IssuanceServiceJson();
			str.append(IssuanceServiceJson.saveParamAttribute(list,
					this.userView.getUserName()));
		} catch (Exception e) {
			return getErrorMessage("得到报表时发生错误");
		}

		return str.toString();
	}

	private ArrayList getAllKQList(ArrayList datalist, ArrayList xmlList,
			String table) {
		LazyDynaBean bean = null;
		for (int i = 0; i < datalist.size(); i++) {
			RecordVo vo = (RecordVo) datalist.get(i);
			String url = "/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table="
					+ table
					+ "&returnvalue=&etoken="
					+ PubFunc.convertUrlSpecialCharacter(PubFunc
							.convertTo64Base(this.username + ","
									+ this.password));
			bean = new LazyDynaBean();
			bean.set("url", url);
			bean.set("title", "请假申请(" + vo.getString("a0101") + ")");
			bean.set("description", "Hr考勤请假信息");
			xmlList.add(bean);
		}
		return xmlList;
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
	 * 待办消息
	 * @param conn
	 * @return
	 */
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
				              "&etoken=" + 
				              PubFunc.convertUrlSpecialCharacter(
				              PubFunc.convertTo64Base(new StringBuilder(String.valueOf(this.username)).append(",")
				              .append(this.password).toString())) + 
				              "&appfwd=1";
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
			              "&etoken=" + 
			              PubFunc.convertUrlSpecialCharacter(
			              PubFunc.convertTo64Base(new StringBuilder(String.valueOf(this.username)).append(",")
			              .append(this.password).toString())) + 
			              "&appfwd=1";
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
			IssuanceServiceJson IssuanceServiceJson = new IssuanceServiceJson();
			
			if (this.userView.isBThreeUser()){
				str.append(IssuanceServiceJson.saveParamAttribute(new ArrayList(),this.userView.getUserName()));
			} else {
				str.append(IssuanceServiceJson.saveParamAttribute(list,this.userView.getUserName()));
			}
		} catch (Exception e) {
			return getErrorMessage("得到待办时发生错误");
		}
		return str.toString();
	}

	/**
	 * 获取待办数据列表
	 * // P：绩效；T：人事异动;G:工资;W：日志；Z:招聘；L：工作纪实；B：报表审批；R:人员信息审核；All：所有待办
	 * 
	 * @param conn
	 * @return
	 */
	private ArrayList getHrMatterList(Connection conn, String types) {
		if (types == null || types.length() <= 0) {
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
		ArrayList matterList = new ArrayList();
		try {
			if (types.toUpperCase().indexOf("Z") != -1 || "All".equalsIgnoreCase(types)) {
				ZpPendingtaskBo zpbo = new ZpPendingtaskBo(conn, this.userView);
				ArrayList zpdatalist = zpbo.getZpapprDta();
				for (int m = 0; m < zpdatalist.size(); m++) {
					CommonData zpdata = (CommonData) zpdatalist.get(m);
					matterList.add(zpdata);
				}
			}
			MatterTaskList matterTaskList = new MatterTaskList(conn, this.userView);
			// 解决获取代办连接是旧链接问题 begin
			matterTaskList.setReturnflag("12");
			// 解决获取代办连接是旧链接问题 end

			if (types.toUpperCase().indexOf("K") != -1 || "All".equalsIgnoreCase(types)) {
				KqMatterTask kqMatterTask = new KqMatterTask(conn, this.userView);
				// 考勤刷卡审批
				matterList = kqMatterTask.getKqCardTask(matterList);
				// 加班申请审批待办
				matterList = kqMatterTask.getKqOvertimeTask(matterList);
			}
			// 我的工作纪实
			matterTaskList.setReturnURL("/templates/index/portal.do?b_query=link");
			matterTaskList.setTarget("_self");
			if ("gw".equalsIgnoreCase(clientName)) {// 国家电网，代办只要绩效的
				// 代理人则不显示 JiangHe
				if (!isagent) {
					if (types.toUpperCase().indexOf("P") != -1 || "All".equalsIgnoreCase(types)) {
						matterList = matterTaskList.getPerformancePending(matterList);
						// matterList=matterTaskList.getScoreList(matterList);
					}
				}
			} else {
				// 代理人则不显示 JiangHe
				if (!isagent) {
					// 只有干警考核才有工作纪实
					if (clientName != null && "gjkhxt".equalsIgnoreCase(clientName.trim())) {
						if (types.toUpperCase().indexOf("L") != -1 || "All".equalsIgnoreCase(types)) {
							matterList = matterTaskList.getWorkPlanList(matterList);
						}
					}
				}
				if (types.toUpperCase().indexOf("T") != -1 || "All".equalsIgnoreCase(types)) {
					matterList = matterTaskList.getWaitTaskList(matterList);
					// matterList=matterTaskList.getInstanceList(matterList); //已处理的任务 不显示
					matterList = matterTaskList.getTmessageList(matterList);
				}
				// 代理人则不显示 JiangHe
				if (!isagent) {

					if (types.toUpperCase().indexOf("P") != -1 || "All".equalsIgnoreCase(types)) {
						matterList = matterTaskList.getPerformancePending(matterList);
						// matterList=matterTaskList.getScoreList(matterList);
					}
				}
				WorkdiarySelStr WorkdiarySelStr = new WorkdiarySelStr();
				WorkdiarySelStr.setReturnURL("/templates/index/portal.do?b_query=link");
				WorkdiarySelStr.setTarget("_self");
				// 防止冲掉其他任务
				ArrayList listtemp = new ArrayList();
				// 代理人则不显示 JiangHe
				if (!isagent) {
					if (types.toUpperCase().indexOf("W") != -1 || "All".equalsIgnoreCase(types)) {
						listtemp = WorkdiarySelStr.getLogWaittask(conn, this.userView, matterList);
					}
				}
				if (listtemp != null && listtemp.size() > 0)
					matterList = listtemp;
			}
			if (matterList != null) {
				for (int i = 0; i < matterList.size(); i++) {
					CommonData cData = (CommonData) matterList.get(i);
					String url = cData.getDataValue() + "&dbpre=" + dbper + "&home=5&ver=5";
					cData.setDataValue(url);
				}
			}
			if (types.toUpperCase().indexOf("G") != -1 || "All".equalsIgnoreCase(types)) {
				SalaryPkgBo salaryPkgBo = new SalaryPkgBo(conn, this.userView);
				// ArrayList salarylist=salaryPkgBo.getEndorseRecords(); //审批薪资
				ArrayList salarylist = salaryPkgBo.getGzPending(); // 审批薪资 读取待办表中数据 zhaoxg add 2014-7-25
				LazyDynaBean abean = new LazyDynaBean();
				if (salarylist != null) {
					for (int i = 0; i < salarylist.size(); i++) {
						abean = (LazyDynaBean) salarylist.get(i);
						String url = abean.get("url") + "&home=5&ver=5&itemid1=default";
						abean.set("url", url);
						matterList.add(abean);
					}
				}
			}
			if (types.toUpperCase().indexOf("B") != -1 || "All".equalsIgnoreCase(types)) {
				// ------------------------报表审批 zhaoxg 2013-1-28--------------------------------
				Report_isApproveBo report_isApproveBo = new Report_isApproveBo(conn, this.userView);
				ArrayList approveList = new ArrayList();
				LazyDynaBean approvebean = new LazyDynaBean();
				approveList = report_isApproveBo.getApprovelist(approveList);
				if (approveList != null) {
					for (int t = 0; t < approveList.size(); t++) {
						approvebean = (LazyDynaBean) approveList.get(t);
						String url = approvebean.get("url") + "&home=5&ver=5";
						approvebean.set("url", url);
						matterList.add(approvebean);
					}
				}
				ArrayList returnList = new ArrayList();
				LazyDynaBean returnbean = new LazyDynaBean();
				returnList = report_isApproveBo.getReturnList(returnList);
				if (returnList != null) {
					for (int t = 0; t < returnList.size(); t++) {

						returnbean = (LazyDynaBean) returnList.get(t);
						String url = returnbean.get("url") + "&home=5&ver=5";

						returnbean.set("url", url);
						matterList.add(returnbean);
					}
				}
			}
			if (types.toUpperCase().indexOf("R") != -1 || "All".equalsIgnoreCase(types)) {
				// ------------------人员信息审核审批-----------
				ArrayList personChangeList = new PersonMatterTask(conn, userView).getPersonInfoChange();
				if (personChangeList != null) {
					for (int g = 0; g < personChangeList.size(); g++) {
						CommonData cData = (CommonData) personChangeList.get(g);
						String url = cData.getDataValue() + "&home=5&ver=5";
						cData.setDataValue(url);
						matterList.add(cData);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return matterList;
	}
			
	/**
	 * 当前人员的常用统计信息
	 * @param conn
	 * @return
	 */
	public String getHrStatics(Connection conn) {
		StringBuffer str = new StringBuffer();
		try {
			IssuanceServiceJson IssuanceServiceJson = new IssuanceServiceJson();
			str.append(IssuanceServiceJson.saveParamAttribute(
					getHrStaticsList(conn), this.userView.getUserName()));
		} catch (Exception e) {

			e.printStackTrace();
		}
		return str.toString();
	}
	private ArrayList getHrStaticsList(Connection conn) {
		
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
						+ "&etoken="
						+ PubFunc.convertUrlSpecialCharacter(PubFunc
								.convertTo64Base(this.username + ","
										+ this.password))
						+"&appfwd=1";//链接进不去,加上appfwd=1  2018-3-21
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

	/**
	 * 返回Json格式的错误信息
	 * @param message
	 * @return
	 */
	public String getErrorMessage(String message) {
		IssuanceServiceJson IssuanceServiceJson = new IssuanceServiceJson();
		return IssuanceServiceJson.errorMessage(message);
	}

	/**
	 * 截取文本,如果超过18个字符用...代替
	 * @param text
	 * @return
	 */
	private String subText(String text) {
		if (text == null || text.length() <= 0)
			return "";
		if (text.length() < 18)
			return text;
		text = text.substring(0, 18) + "...";
		return text;
	}
}
