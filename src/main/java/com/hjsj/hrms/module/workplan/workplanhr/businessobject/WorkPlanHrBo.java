package com.hjsj.hrms.module.workplan.workplanhr.businessobject;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.businessobject.workplan.plan_track.RelatePerformancePlanBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.workplanhr.transaction.WorkPlanHrMainTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unchecked")
public class WorkPlanHrBo {
	private Connection conn = null;
	private UserView userView = null;
	private ArrayList fScoreList = new ArrayList();// 直接上级对当前人的评分记录 临时存储
	private ArrayList fStandardGradeList = new ArrayList();// 指标标准标度
	private ArrayList templateItemList;// 关联模板项目列表
	private String templateId = "";// 关联模板id

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public WorkPlanHrBo(Connection conn, UserView userView) {
		this.setConn(conn);
		this.setUserView(userView);
	}

	/**
	 * 筛选-输入查询
	 * 
	 * @param map
	 * @throws GeneralException
	 */
	public int fastSearchByInput(HashMap<?, ?> map) throws GeneralException {
		int errorcode = 1;
		try {
			// 输入的查询条件
			ArrayList<String> valuesList = (ArrayList<String>) map.get("inputValues");

			
			TableDataConfigCache catche = (TableDataConfigCache) this.getUserView().getHm().get(WorkPlanHrMainTrans.WORKPLAN_HR_SUBMODULEID);
			StringBuilder querySql = new StringBuilder();
			// 根据查询条件组成sql片段
			if (valuesList != null && valuesList.size() > 0) {
				querySql.append(" and ( ");
			}
			for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
				String queryVal = valuesList.get(i);
				queryVal = SafeCode.decode(queryVal);
				//防止sql注入
				queryVal = PubFunc.hireKeyWord_filter(queryVal);
				if (i != 0) {
					querySql.append("or ");
				}
				// 名称
				querySql.append("(a0101 like '%" + queryVal + "%'");
				// 单位部门岗位
				List<String> itemids = this.getCodeByLikeDesc(queryVal);
				if (itemids.size() > 0) {
					StringBuffer itemBuf = new StringBuffer();
					for (String itemid : itemids) {
						itemBuf.append("'" + itemid + "',");
					}
					itemBuf.setLength(itemBuf.length() - 1);
					querySql.append(" or B0110 in (" + itemBuf + ") or e0122 in (" + itemBuf + ") or e01a1 in (" + itemBuf + ")");
				}
				// email
				querySql.append("or email like '" + queryVal + "%'");//左匹配
				// 拼音简码
				querySql.append("or pinyin like '%" + queryVal + "%'");
				
				querySql.append(")");
			}
			if (valuesList != null && valuesList.size() > 0) {
				querySql.append(")");
			}
			StringBuffer tempSql = new StringBuffer();
			tempSql.append(catche.getQuerySql());
			int sIndex = tempSql.indexOf("and 2=2");
			int eIndex = tempSql.lastIndexOf("and 2=2");
			if(sIndex>-1 && eIndex>-1 && sIndex < eIndex) {
				tempSql = tempSql.replace(sIndex, eIndex+7, "");
			}
			if(querySql.length()>0)
				tempSql.append(" and 2=2"+querySql+" and 2=2");
			
			catche.setQuerySql(tempSql.toString());
			
			errorcode = 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return errorcode;
	}

	/**
	 * 筛选-计划区间、应报已报
	 * 
	 * @param map：
	 * @throws GeneralException
	 */
	public int fastSearchByCondition(HashMap<?, ?> map) throws GeneralException {

		TableDataConfigCache cache = (TableDataConfigCache) this.getUserView().getHm().get(WorkPlanHrMainTrans.WORKPLAN_HR_SUBMODULEID);
		int errorcode = 1;
		try {
			String periodType = String.valueOf(map.get("periodtype"));// 计划区间1、年度2、半年3、季度4、月份5、周
			String periodYear = String.valueOf(map.get("periodyear"));// 年份
			String periodMonth = String.valueOf(map.get("periodmonth"));// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
			String periodWeek = String.valueOf(map.get("periodweek"));// 第几周
			String submittype = String.valueOf(map.get("submittype"));// 应报已报标识 0：应报 1：未报 2：已报 3：未批 4：已批 5：已变更
			String planType = String.valueOf(map.get("plantype"));// 计划类型

			// 计划区间筛选
			StringBuilder typeSql = new StringBuilder();
			typeSql.append(" and (p0725 = " + periodType);
			if (!WorkPlanConstant.Cycle.YEAR.equals(periodType)) {
				typeSql.append(" and p0729 = " + periodMonth);
			}
			if (WorkPlanConstant.Cycle.WEEK.equals(periodType)) {
				typeSql.append(" and p0731 = " + periodWeek);
			}
			typeSql.append(" and p0727= " + periodYear+")");
			String tableSql = (String)this.getUserView().getHm().get("workplanhr_tablsql");
			tableSql = tableSql.replace("{0}", typeSql);
			//填报人员范围sql
			String tfsql = getTaskFuncSql(planType,periodType);
			tableSql = tableSql.replace("{1}", tfsql);
			
			cache.setTableSql(tableSql);
			
			
			// 应报已报筛选
			StringBuilder querySql = new StringBuilder();
			if ("0".equals(submittype)) {// 应报
				querySql.append(" and 1=1 ");
			} else if ("1".equals(submittype)) {// 未报
				querySql.append(" and " + Sql_switcher.isnull("p0719", "0") + "in (0, 3)");
			} else if ("2".equals(submittype)) {// 已报
				querySql.append(" and " + Sql_switcher.isnull("p0719","0") + "in (1, 2)");
			} else if ("3".equals(submittype)) {// 未批
				querySql.append(" and " + Sql_switcher.isnull("p0719", "0") + "=1");
			} else if ("4".equals(submittype)) {// 已批
				querySql.append(" and " + Sql_switcher.isnull("p0719", "0") + "=2");
			} else if ("5".equals(submittype)) {// 已变更
				querySql.append(" and " + Sql_switcher.isnull("changeflag", "0") + "=1");
			}
			
			StringBuilder tempSql = new StringBuilder(cache.getQuerySql());
			if(tempSql.indexOf("and 1=2")>-1)
				tempSql.replace(tempSql.indexOf("and 1=2"), tempSql.indexOf("and 1=2")+7, "");
			
			// 替换复杂查询的查询条件
			int sIndex = tempSql.indexOf("and 0=0");
			int eIndex = tempSql.lastIndexOf("and 0=0");
			if(sIndex>-1 && eIndex>-1 && sIndex < eIndex) {
				tempSql = tempSql.replace(sIndex, eIndex+7, "");
			}
			if(querySql.length()>0)
				tempSql.append("and 0=0"+querySql+" and 0=0");
			
			cache.setQuerySql(tempSql.toString());
			errorcode = 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return errorcode;
	}
	/**
	 * 计划类型
	 * @param periodType 计划区间1->、年度2、半年3、季度4、月份5、周
	 * @return
	 */
	private String getTaskFuncSql(String planType,String periodType) {
		
		String tfSql = "";
		String planFld = "";
		if("1".equals(periodType))
		{	
			planFld = "1".equals(planType)?"P00":"P01";
		}else if("2".equals(periodType))
		{
			planFld = "1".equals(planType)?"P10":"P11";
		}else if("3".equals(periodType))
		{
			planFld = "1".equals(planType)?"P20":"P21";;
		}else if("4".equals(periodType))
		{
			planFld = "1".equals(planType)?"P30":"P31";;
		}else if("5".equals(periodType))
		{
			planFld = "1".equals(planType)?"P40":"P41";;
		}
		tfSql+="and "+planFld+"=1";
		return tfSql;
	}

	/**
	 * 提醒写、批准邮件
	 * 
	 * @param map
	 * @return
	 * @throws GeneralException
	 */
	public int remind(HashMap<?, ?> map) throws GeneralException {
		int errorcode = 1;
		try {
			String submit_type = String.valueOf(map.get("submit_type"));
			String objectids = String.valueOf(map.get("objectids"));
			String planType = String.valueOf(map.get("plantype"));// 计划类型1：人员 2：团队
			String periodType = String.valueOf(map.get("periodtype"));// 计划区间1、年度2、半年3、季度4、月份5、周
			String periodYear = String.valueOf(map.get("periodyear"));// 年份
			String periodMonth = String.valueOf(map.get("periodmonth"));// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
			String periodWeek = String.valueOf(map.get("periodweek"));// 第几周

			// 提醒全部
			if (StringUtils.isEmpty(objectids)) {
				this.remindMyTeamToSubmitPlan(planType, periodType, periodYear, periodMonth, periodWeek, submit_type);
			} 
			// 提醒单人或多人
			else {
				String[] objectArr = objectids.split(",");
				
				WorkPlanBo planBo = new WorkPlanBo(this.getConn(), this.userView);
				for(String obj : objectArr) {
					obj = PubFunc.decrypt(obj.trim());
					planBo.remindSubmitPlan(planType, periodType, periodYear, periodMonth, periodWeek, obj, submit_type);
				}
				
			}
			errorcode = 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return errorcode;
	}
	private String getWorkPlanHrAllSql() {
		String sql = "";
		
		TableDataConfigCache cache = (TableDataConfigCache) this.getUserView().getHm().get(WorkPlanHrMainTrans.WORKPLAN_HR_SUBMODULEID);
		sql = cache.getTableSql();
		sql = "select * from ("+sql+") myGridData where 1=1 ";
		if(cache.getQuerySql()!=null)
			sql += cache.getQuerySql();
		
		
		return sql;
	}
	/**
	 * 通过单位或部门名称模糊查询
	 * 
	 * @param codeDesc
	 * @return
	 * @throws GeneralException
	 */
	private List<String> getCodeByLikeDesc(String codeDesc) throws GeneralException {
		List<String> itemidList = new ArrayList<String>();
		String sql = "select codeitemid from organization where codeitemdesc like '%" + codeDesc + "%' and codesetid in ('UN','UM')";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getConn());
			rs = dao.search(sql);
			while (rs.next()) {
				String codeitemid = rs.getString("codeitemid");
				itemidList.add(codeitemid);
			}
			return itemidList;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}

	/**
	 * 提醒写、批准邮件
	 * 
	 * @param p0723
	 * @param periodtype
	 * @param periodyear
	 * @param periodmonth
	 * @param periodweek
	 * @param submit_type
	 * @return
	 */
	public boolean remindMyTeamToSubmitPlan(String p0723, String periodtype, String periodyear, String periodmonth, String periodweek, String submit_type) {

		if ("2".equals(submit_type)) {
			return true;
		}
		boolean b = false;

		ContentDAO dao = new ContentDAO(this.getConn());
		RowSet rs = null;
		try {
			rs = dao.search(this.getWorkPlanHrAllSql());

			WorkPlanBo planBo = new WorkPlanBo(this.getConn(), this.userView);
			b = planBo.remindSubmitPlan(rs, p0723, periodtype, periodyear, periodmonth, periodweek, submit_type);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return b;

	}

	/**
	 * 按计划状态取计划记录数
	 * 
	 * @param submit_type
	 * @param planType
	 * @param periodtype
	 * @param periodyear
	 * @param periodmonth
	 * @param periodweek
	 * @return
	 */
	public String getCountSqlByPlanStatus(String submit_type,String planType, String periodtype, String periodyear, String periodmonth, String periodweek) {
		String strsql = "";
		String tablename = "";
		if ("2".equals(planType)) {
			tablename = getTeamDeptSql();
		} else {
			tablename = getTeamPeopleSql();
		}
		tablename = tablename.replace("{1}", getTaskFuncSql(planType,periodtype));
		if ("".equals(tablename)) {
			return "";
		}
		tablename = "(" + tablename + ") T";
		String relateFld = "";
		DbWizard dbw = new DbWizard(this.getConn());
		if (dbw.isExistField("p07", "relate_planid", false)) {
			relateFld = ",p07.relate_planid ";
		}
		if ("2".equals(planType)) {
			strsql = " Select T.e01a1,T.B0110,T.E0122,p07.p0700,p07.p0719" + relateFld + ",p07.p0707,p07.changeflag from " + tablename + " " + "left join (select * from P07 where "
					+ this.getPlanPublicWhere(planType, periodtype, periodyear, periodmonth, periodweek) + ") P07 on p07.p0707 =T.B0110 ";
		} else {
			strsql = " select T.*,p07.p0700,P07.p0719,p07.E0122 as p07e0122,p07.e01a1 as p07e01a1" + relateFld + ",p07.changeflag from " + tablename
					+ " left join p07 on P07.nbase=T.nbase and P07.a0100=T.a0100" + " and " + this.getPlanPublicWhere(planType, periodtype, periodyear, periodmonth, periodweek);
		}

		// 应报已报筛选
		/*strsql = " select count(*) from (" + strsql + ") F where 1=1 ";
		if("0".equals(submit_type)) {// 所有
			
		} else if ("1".equals(submit_type)) {// 未报
			strsql += " and " + Sql_switcher.sqlNull("F.p0719", 0) + "in (0, 3) ";
		} else if ("2".equals(submit_type)) {// 已报
			strsql += " and " + Sql_switcher.sqlNull("F.p0719", 0) + "in (1, 2)";
		} else if ("3".equals(submit_type)) {// 未批
			strsql += " and " + Sql_switcher.sqlNull("F.p0719", 0) + "=1 ";
		} else if ("4".equals(submit_type)) {// 已批
			strsql+= " and " + Sql_switcher.sqlNull("F.p0719", 0) + "=2 ";
		} else if ("5".equals(submit_type)) {// 已变更
			strsql += " and " + Sql_switcher.sqlNull("F.changeflag", 0) + "=1 ";
		}*/
		
		return strsql;
	}

	/**
	 * 获取管理范围内机构sql
	 * 
	 * @return
	 */
	public String getTeamDeptSql() {
		String operOrg = this.userView.getUnitIdByBusi("5");
		String orgWhere = "1=1";
		if (operOrg != null && operOrg.length() > 3) {
			StringBuffer tempSql = new StringBuffer("");
			String[] temp = operOrg.split("`");
			for (int i = 0; i < temp.length; i++) {
				tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");

			}
			orgWhere = tempSql.substring(3);
		}

		StringBuffer strSql = new StringBuffer();
		try {
			strSql.append("select A.* from (");
			strSql.append("select E1.*,E2.nbase"+Sql_switcher.concat()+"E2.a0100 as dept_leader_id,E2.a0101,E2.pinyin,E2.email,E2.guidkey from (");
			strSql.append("select b01." + WorkPlanConstant.DEPTlEADERFld + " as e01a1,b01.b0110,b01.b0110 as e0122,O.codeitemdesc from B01,organization O where B01.b0110=O.codeitemid and B01.");
			strSql.append(WorkPlanConstant.DEPTlEADERFld + " in (" + " select codeitemid from  organization where codesetid='@K'  and (" + orgWhere + " ))");
			//获取岗位在编人员的sql语句  
			String e01a1Sql = this.getPeopleSqlByE01a1();
			strSql.append(") E1 left join ("+e01a1Sql+") E2 on E1.E01A1 = E2.E01A1");
			strSql.append(") A,per_task_func ptf where ptf.guidkey=A.guidkey {1}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strSql.toString();
	}

	/**
	 * 获取管理范围内人员
	 * 
	 * @return
	 */
	public String getTeamPeopleSql() {
		DbWizard dw = new DbWizard(this.conn);
		String operOrg = this.userView.getUnitIdByBusi("5");
		String orgWhere = "";
		if (operOrg != null && operOrg.length() > 3) {
			StringBuffer tempSql = new StringBuffer("");
			String[] temp = operOrg.split("`");
			for (int i = 0; i < temp.length; i++) {
				if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
					tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
				else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
					tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
			}
			orgWhere = tempSql.substring(3);
		}

		String strSql = "";
		try {
			// 取人员范围条件参数
			RecordVo paramsVo = ConstantParamter.getConstantVo("OKR_CONFIG");
			WorkPlanConfigBo bo = new WorkPlanConfigBo(this.getConn(), this.userView);
			String xmlValue = "";
			Map mapXml = new HashMap();
			// 有缓存则取缓存数据
			if (null != paramsVo) {
				xmlValue = paramsVo.getString("str_value");
			}
			mapXml = bo.parseXml(xmlValue);
			String dbValue = mapXml.get("nbases") == null ? "" : (String) mapXml.get("nbases");
			String emp_scope = mapXml.get("emp_scope") == null ? "" : (String) mapXml.get("emp_scope");
			String[] arrpre = dbValue.split(",");
			if (arrpre.length < 1) {
				throw new GeneralException("未设置认证人员库！");
			}

			for (int i = 0; i < arrpre.length; i++) {
				String pre = arrpre[i];
				String a01tab = pre + "A01";
				String sql = "";
				if(StringUtils.isNotBlank(orgWhere)){
				sql = "select " + a01tab + ".a0100 from " + a01tab + "" + " where " + orgWhere + " ";
					// 排除 在当前期间制定计划时岗位不属于我的团队 由其他部门调来的
					sql = sql + " and not EXISTS  ( select a0100 from p07" + " where p0723=1 and not EXISTS "
							+ " (select " + a01tab + ".a0100 from " + a01tab + "" + " where " + orgWhere + "and "+a01tab+".a0100=p07.a0100)" + "and "+a01tab+".a0100=p07.a0100)";
	
					// 加上 在当前期间制定计划时属于我的团队，从我部门调走的
					sql = sql + " union select a0100 from p07 " + " where " + orgWhere+" and p0723=1";
				}
				if (!"".equals(strSql)) {
					strSql = strSql + " union ";
				}
				String pyField = getPinYinFld();
				if(StringUtils.isNotBlank(pyField) && dw.isExistField(a01tab, pyField)) {
					pyField +=" as pinyin,";
				}else {
					pyField = "'' as pinyin,";
				}
				String emailField = getEmailField();
				if(!StringUtils.isEmpty(emailField) && dw.isExistField(a01tab, emailField)) {
					emailField +=" as email,";
				}else {
					emailField="'' as email,";
				}
				strSql = strSql + "select "+pyField+emailField+"A0100,a0101,b0110,e0122,e01a1," + "'" + pre + "' as nbase " + "," + i + " as dbid,A0000" + " from  " + a01tab + ",per_task_func ptf where";
				if(StringUtils.isNotBlank(sql))
					//strSql+=" EXISTS (" + sql+") and ";
					//此处应该使用in比较好，因为内表数据少，并且之前使用exists 也没有跟内表关联
					// 比如t1.a0100=t2.a0100,导致exists 根本没有效果
					strSql+=" a0100 in (" + sql+") and ";
				// OKR人员范围sql条件
				String whereIn = WorkPlanConfigBo.getOkrWhereINSql(pre, emp_scope);
				if (!StringUtils.isEmpty(whereIn)) {
					StringBuffer nbaseSql = new StringBuffer();
					nbaseSql.append(" a0100 in ("); 
					nbaseSql.append(" select " + pre + "A01.a0100 ").append(whereIn).append(") and ");

					strSql += nbaseSql.toString();
				}
				//人员填报范围
				strSql+= " ptf.guidkey="+a01tab+".guidkey {1}";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strSql;
	}

	/**
	 * 当前计划的条件
	 * 
	 * @param planType
	 * @param periodType
	 * @param periodYear
	 * @param periodMonth
	 * @param periodWeek
	 * @return
	 */
	private String getPlanPublicWhere(String planType, String periodType, String periodYear, String periodMonth, String periodWeek) {
		String sql = " p07.p0723=" + planType + " and p07.p0725 =" + periodType + " and p07.P0727=" + periodYear;
		if (!WorkPlanConstant.Cycle.YEAR.equals(periodType)) {
			sql = sql + " and p07.P0729=" + periodMonth;
		}
		if (WorkPlanConstant.Cycle.WEEK.equals(periodType)) {
			sql = sql + " and p07.P0731=" + periodWeek;
		}
		return sql;
	}

	public int relate(HashMap<?, ?> map) throws GeneralException {

		int errorcode = 1;
		try {
			String planType = (String) map.get("plantype");// 计划类型1：人员 2：团队
			String periodType = (String) map.get("periodtype");// 计划区间1、年度2、半年3、季度4、月份5、周
			String periodYear = (String) map.get("periodyear");// 年份
			String periodMonth = (String) map.get("periodmonth");// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
			String periodWeek = (String) map.get("periodweek");// 第几周

			String reRelalte = (String) map.get("rerelalte");
			String plan_id = (String) map.get("planid");
			String objectIds = (String) map.get("objectids");
			objectIds = (objectIds == null) ? "" : objectIds;
			String[] arrObjectIds = objectIds.split(",");
			boolean bAll = true;
			for (int i = 0; i < arrObjectIds.length; i++) {
				String objectid1 = PubFunc.decrypt(arrObjectIds[i]);
				if (objectid1.length() > 0) {
					bAll = false;
					arrObjectIds[i] = objectid1;
				}
			}
			errorcode = this.relatePlan(plan_id, arrObjectIds, bAll, "true".equals(reRelalte), planType, periodType, periodYear, periodMonth, periodWeek);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return errorcode;
	}

	/**
	 * @Title: relatePlan @Description:关联计划 @param @param khplan_id @param @param
	 *         arrobjectIds @param @param bAll @param @param
	 *         map @param @return @return boolean @author:wangrd @throws
	 */
	public int relatePlan(String khplan_id, String[] arrobjectIds, boolean bAll, boolean reRelate, String planType, String periodtype, String periodyear, String periodmonth, String periodweek) {
		int errorcode = 1;
		String strsql = "";
		RowSet rset = null;
		ArrayList paramList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getConn());
		try {
			RelatePerformancePlanBo relateBo = new RelatePerformancePlanBo(this.conn, this.userView);
			PerformanceImplementBo ImplementBo = new PerformanceImplementBo(this.conn, this.userView, khplan_id);
			String superBoydid = relateBo.getBodyIdByBodyType(khplan_id, "1");// 上级
			String superSuperBoydid = relateBo.getBodyIdByBodyType(khplan_id, "0");// 上上级
			// 需要检查的工作计划
			strsql = getCheckPeopleSql(paramList, arrobjectIds, bAll, planType, periodtype, periodyear, periodmonth, periodweek);
			rset = dao.search(strsql, paramList);
			while (rset.next()) {
				String p0700 = rset.getString("p0700");
				String _p0719 = rset.getString("p0719");
				if (p0700 == null)
					continue;
				if (_p0719 == null)
					_p0719 = "";
				if (!"2".equals(_p0719)) {
					continue;
				}
				if (!reRelate) {// 已关联的不再关联
					int relate_planid = rset.getInt("relate_planid");
					if (relate_planid > 0) {
						continue;
					}
				}
				strsql = "update p07 set relate_planid =" + khplan_id + " where p0700=" + p0700;
				dao.update(strsql);
				// 新增考核对象
				String objectid = "";
				if ("2".equals(planType)) {
					objectid = rset.getString("p0707");
					if (objectid != null && objectid.length() > 0) {
						ImplementBo.handInsertObjects("'" + objectid + "'", khplan_id, "1");
					}
				} else {
					objectid = rset.getString("a0100");
					if (objectid != null && objectid.length() > 0) {
						ImplementBo.handInsertObjects("'" + objectid + "'", khplan_id, "2");
					}
				}
				initP04Record(khplan_id, objectid, planType);
				// 新增考核主体
				String e01a1 = "";
				if (bAll) {
					if ("2".equals(planType)) {
						e01a1 = rset.getString("e01a1");
					} else {
						e01a1 = rset.getString("p07e01a1");
					}
				} else {
					if ("2".equals(planType)) {
						WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
						e01a1 = workPlanUtil.getDeptLeaderE01a1(objectid);
					} else {
						e01a1 = rset.getString("e01a1");
					}
				}
				e01a1 = (e01a1 == null) ? "" : e01a1;
				// 如果有团队负责人类别 则插入团队负责人
				String deptLeader = "";// 记录负责人的人员编号，如果此负责人还有其他领导身份的话，则不再添加。
				if ("2".equals(planType)) {
					String deptBody_id = relateBo.getBodyIdByBodyType(khplan_id, "5");// 团队负责人
					if ("-1".equals(deptBody_id) && e01a1.length() > 0) {
						WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
						String mainBodyA0100 = workPlanUtil.getFirstE01a1Leaders(e01a1, "USR");
						if (mainBodyA0100.length() > 0) {
							deptLeader = mainBodyA0100;
							ImplementBo.selMainBody("'" + mainBodyA0100 + "'", khplan_id, deptBody_id, objectid, "false");
						}

					}

				}
				String superE01a1 = "";
				if (superBoydid.length() > 0 || superSuperBoydid.length() > 0) {
					WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
					superE01a1 = workPlanUtil.getDirectSuperE01a1(e01a1);
				}
				if (superBoydid.length() > 0 && superE01a1.length() > 0) {
					WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
					String mainBodyA0100 = workPlanUtil.getFirstE01a1Leaders(superE01a1, "USR");
					if (mainBodyA0100.length() > 0 && !deptLeader.equals(mainBodyA0100)) {
						ImplementBo.selMainBody("'" + mainBodyA0100 + "'", khplan_id, superBoydid, objectid, "false");
					}
				}
				if (superSuperBoydid.length() > 0 && superE01a1.length() > 0) {
					WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
					String superSuperE01a1 = workPlanUtil.getDirectSuperE01a1(superE01a1);
					if (superSuperE01a1.length() > 0) {
						String mainBodyA0100 = workPlanUtil.getFirstE01a1Leaders(superSuperE01a1, "USR");
						if (mainBodyA0100.length() > 0 && !deptLeader.equals(mainBodyA0100)) {
							ImplementBo.selMainBody("'" + mainBodyA0100 + "'", khplan_id, superSuperBoydid, objectid, "false");
						}
					}
				}

				// 更新目标卡
				if (!updateTargetCard(khplan_id, objectid, planType, p0700)) {
					break;
				}

			}
			errorcode = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return errorcode;
	}

	/**
	 * @Title: getCheckPeopleData @Description: 获取需要检查的人员计划集合 @param @param
	 *         rPeopleSet @param @param arrobjectIds @param @param
	 *         bAll @param @return @author:wangrd @throws
	 */
	public String getCheckPeopleSql(ArrayList paramList, String[] arrobjectIds, boolean bAll, String planType, String periodtype, String periodyear, String periodmonth, String periodweek) {
		String strsql = "";
		try {
			if (bAll) {
				paramList.clear();
				strsql = getCountSqlByPlanStatus("2", planType, periodtype, periodyear, periodmonth, periodweek).replace("count(*)", "*");

			} else {
				String odjectIds = "";
				for (int i = 0; i < arrobjectIds.length; i++) {
					String objectid = arrobjectIds[i];
					if ("2".equals(planType)) {
						if ("".equals(odjectIds)) {
							odjectIds = "'" + objectid + "'";
						} else {
							odjectIds = odjectIds + "," + "'" + objectid + "'";
						}
					} else {
						if (objectid.length() < 4)
							continue;
						String nbase = objectid.substring(0, 3);
						String a0100 = objectid.substring(3);
						if ("".equals(odjectIds)) {
							odjectIds = "'" + a0100 + "'";
						} else {
							odjectIds = odjectIds + "," + "'" + a0100 + "'";
						}

					}
				}
				if ("2".equals(planType)) {
					strsql = " select * from  p07 where p0707 in (" + odjectIds + ")" + " and " + this.getPlanPublicWhere(planType, periodtype, periodyear, periodmonth, periodweek);

				} else {
					strsql = " select * from  p07 where upper(nbase) ='USR'" + " and a0100 in (" + odjectIds + ")" + " and "
							+ this.getPlanPublicWhere(planType, periodtype, periodyear, periodmonth, periodweek);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strsql;

	}

	/**
	 * @Title: initP04Record @Description: 新增考核对象后，初始化p04表
	 *         把共性指标copy过来。 @param @param kh_planid @param @param
	 *         objectid @param @param p0723 @return void @author:wangrd @throws
	 */
	public void initP04Record(String kh_planid, String objectid, String p0723) {
		ContentDAO dao = new ContentDAO(this.getConn());
		try {
			String sql = "select count(*) from p04  where plan_id=" + kh_planid;
			if ("1".equalsIgnoreCase(p0723))
				sql += " and a0100='" + objectid + "'";
			else
				sql += " and b0110='" + objectid + "'";
			RowSet frowset = dao.search(sql);
			if (frowset.next()) {
				if (frowset.getInt(1) == 0) {
					KhTemplateBo bo = new KhTemplateBo(this.conn, "1", objectid, kh_planid, "targetCard");
					bo.insertObjTarget_commonPoint(objectid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @Title: updateTargetCard @Description: 更新目标卡 @param @param
	 *         khplan_id @param @param p0700 @param @return @return
	 *         boolean @author:wangrd @throws
	 */
	public boolean updateTargetCard(String khplan_id, String objectid, String p0723, String p0700) {

		ContentDAO dao = new ContentDAO(this.getConn());
		fScoreList.clear();
		boolean b = true;
		RowSet rtaskset = null;
		try {
			RelatePerformancePlanBo relateBo = new RelatePerformancePlanBo(this.conn, this.userView);
			if (templateId == null || templateId.length() < 1) {
				templateId = relateBo.getTemplateId(khplan_id);
				templateItemList = relateBo.getTemplateItemList(templateId);
			}
			FieldItem item = DataDictionary.getFieldItem("p0823", "P08");
			if (item == null)
				return b;
			String codeSetId = item.getCodesetid();

			String objCode = "P" + objectid;
			if ("2".equals(p0723)) {
				objCode = "UN" + objectid;
			}
			KhTemplateBo bo = new KhTemplateBo(this.conn, "1", objCode, khplan_id, "targetCard");

			String point_explain_item = "";
			String point_evaluate_item = "";
			AnalysePlanParameterBo PlanParameterBo = new AnalysePlanParameterBo(this.conn);
			Hashtable ht_table = PlanParameterBo.analyseParameterXml();
			if (ht_table != null) {
				if (ht_table.get("DescriptionItem") != null)
					point_explain_item = (String) ht_table.get("DescriptionItem");
				if (ht_table.get("PrincipleItem") != null)
					point_evaluate_item = (String) ht_table.get("PrincipleItem");
			}
			// 判断是否要更新直接上级的打分记录
			boolean bUpdScore = false;
			String superMainBodyId = "";
			String superBoydid = relateBo.getBodyIdByBodyType(khplan_id, "1");// 上级主体Id
			String sql = "select * from per_mainbody where  plan_id=" + khplan_id + " and object_id ='" + objectid + "' and body_id=" + superBoydid + " and status in (0,1)";
			RowSet rSet = dao.search(sql);
			if (rSet.next()) {// 未提交的打分可以提交
				bUpdScore = true;
				superMainBodyId = rSet.getString("mainbody_id");

			}
			// bUpdScore=false;

			PlanTaskTreeTableBo taskTreeBo = new PlanTaskTreeTableBo(this.conn, Integer.parseInt(p0700));
			FieldItem rankItem = DataDictionary.getFieldItem("rank", "per_task_map");

			sql = taskTreeBo.getTableDatasql("");

			sql = sql.replace("order by ptm.seq","order by p08.p0831,p08.p0800 asc");
			rtaskset = dao.search(sql);
			HashMap p08ScoreMap = new HashMap();
			if (bUpdScore) {// 获取分值
				ArrayList dataList = new ArrayList();
				while (rtaskset.next()) {
					String p0800 = rtaskset.getString("p0800");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0800", p0800);
					dataList.add(bean);
					// todo 调用公用取值方法
				}
				p08ScoreMap = taskTreeBo.getTaskScoreMap(dataList);
				rtaskset.beforeFirst();
			}
			ArrayList toDeleteTaskList = new ArrayList();
			String p0800s = "";
			while (rtaskset.next()) {
				p0800s+=rtaskset.getString("p0800");
				if (isNeedAddToP04(rtaskset, khplan_id, objectid, p0723)) {// 需要考核
					double rank = rtaskset.getFloat("rank");
					String fmtRank = WorkPlanUtil.formatDouble(rank, rankItem.getDecimalwidth());
					BigDecimal rankDecimal = new BigDecimal(fmtRank);
					rank = rankDecimal.doubleValue();// 解决p04表权重字段小数位过多的问题。

					String p0823 = rtaskset.getString("p0823") == null ? "" : rtaskset.getString("p0823");
					if (codeSetId.length() > 0)
						;
					p0823 = AdminCode.getCodeName(codeSetId, p0823);
					LazyDynaBean bean = relateBo.getTemplateItem(templateItemList, p0823);
					if (bean == null && p0823.length() > 0) {
						relateBo.addTemplateItem(templateId, p0823);
						templateItemList = relateBo.getTemplateItemList(templateId);
						bean = relateBo.getTemplateItem(templateItemList, p0823);
						if (bean == null) {
							throw new Exception("新增模板项目出错！");
						} else {
							// 项目权限表增加字段
							Table table = new Table("PER_ITEMPRIV_" + khplan_id);
							Field obj = new Field("C_" + (String) bean.get("item_id"));
							obj.setDatatype(DataType.INT);
							obj.setKeyable(false);
							table.addField(obj);
							DbWizard dbw = new DbWizard(this.getConn());
							dbw.addColumns(table);
							DBMetaModel dbmodel = new DBMetaModel(this.conn);
							dbmodel.reloadTableModel("PER_ITEMPRIV_" + khplan_id);
							sql = "update PER_ITEMPRIV_" + khplan_id + " set " + "C_" + (String) bean.get("item_id") + "=1";
							dao.update(sql);
							// 结果表增加字段
							table = new Table("PER_RESULT_" + khplan_id);
							obj = new Field("T_" + (String) bean.get("item_id"));
							obj.setDatatype(DataType.FLOAT);
							obj.setLength(12);
							obj.setDecimalDigits(6);
							obj.setKeyable(false);
							table.addField(obj);
							dbw.addColumns(table);
							dbmodel.reloadTableModel("PER_RESULT_" + khplan_id);
						}
					}
					if (bean != null) {
						// 更新目标卡
						String itemid = (String) bean.get("item_id");
						String taskname = rtaskset.getString("p0801");
						String p0800 = rtaskset.getString("p0800");
						int p0400 = bo.insertTargetTaskFromP08(taskname, itemid, p0800, rank);
						// 更新任务描述、评价标准
						if (p0400 > 0) {
							RecordVo vo = new RecordVo("P04");
							vo.setInt("p0400", p0400);
							vo = dao.findByPrimaryKey(vo);
							if (point_explain_item != null && point_explain_item.length() > 0) {
								String taskdesc = rtaskset.getString("p0803"); // 任务描述
								if (taskdesc != null)
									vo.setString(point_explain_item.toLowerCase(), taskdesc);
							}
							if (point_evaluate_item != null && point_evaluate_item.length() > 0) {
								String P0841 = rtaskset.getString("p0841"); // 评价标准
								if (P0841 != null)
									vo.setString(point_evaluate_item.toLowerCase(), P0841);
							}
							// 完成进度
							String p0835 = rtaskset.getString("p0835");
							if (p0835 != null)
								vo.setInt("p0419", Integer.parseInt(p0835));

							// 进度说明:p0837=>p0409 ps:中交一公局:p0837=>p04z9
							String p0837 = rtaskset.getString("p0837");
							if (p0837 != null)
								vo.setString("p0409", p0837);

							dao.updateValueObject(vo);

							// 获取直接上级的评分
							if (bUpdScore && p08ScoreMap.get(p0800 + "/score") != null) {
								Integer intScore = (Integer) p08ScoreMap.get(p0800 + "/score");
								double p0413 = vo.getDouble("p0413");
								double p0415 = vo.getDouble("p0415");
								double score = ((intScore * 1.0000) / 10) * p0413;
								LazyDynaBean scoreBean = new LazyDynaBean();
								scoreBean.set("objectId", objectid);
								scoreBean.set("mainBodyId", superMainBodyId);
								scoreBean.set("p0413", p0413 + "");
								scoreBean.set("p0400", p0400 + "");
								scoreBean.set("score", score + "");
								fScoreList.add(scoreBean);

							}
						}
					}
				} else {// 需要删除的任务
					toDeleteTaskList.add(rtaskset.getString("p0800"));
				}
			}
			//删除p04中的已经在工作计划中被删除的考核指标 haosl 2018年4月28日
			sql = "select p0401 from p04 where fromflag=5 and plan_id=?";
			List values = new ArrayList();
			if ("1".equalsIgnoreCase(p0723)) {
				sql += " and a0100=?";
			}else {
				sql += " and b0110=?";
			}
			values.add(khplan_id);
			values.add(objectid);
			rtaskset = dao.search(sql,values);
			while(rtaskset.next()) {
				String p0401 = rtaskset.getString("p0401");
				if(p0800s.indexOf(p0401)<0) {
					//p04中来自计划的考核指标在计划中已经被删除，需同时删除p04里的记录
					toDeleteTaskList.add(p0401);
				}
			}
			deleteTask(toDeleteTaskList, khplan_id, objectid, p0723);
			// 更新直接上级评分
			if (bUpdScore && fScoreList.size() > 0) {
				updateTargetScore(khplan_id, superMainBodyId, objectid);
			}
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rtaskset);
		}

		return b;
	}

	/**
	 * @Title: deleteTask
	 * @Description: 删除权重为0的任务 及已取消的任务
	 * @param @param
	 *            deleteList
	 * @param @return
	 * @return boolean
	 */
	public boolean deleteTask(ArrayList deleteList, String kh_planid, String objectid, String p0723) {
		boolean b = false;
		ContentDAO dao = new ContentDAO(this.getConn());
		try {
			if (kh_planid != null && kh_planid.length() > 0) {
				try {
					String sql = "delete from p04  where fromflag=5 and plan_id=" + kh_planid;
					if ("1".equalsIgnoreCase(p0723))
						sql += " and a0100='" + objectid + "'";
					else
						sql += " and b0110='" + objectid + "'";
					String taskIds = "";
					for (int i = 0; i < deleteList.size(); i++) {
						if ("".equals(taskIds)) {
							taskIds = "'" + (String) deleteList.get(i) + "'";
						} else {
							taskIds = taskIds + "," + "'" + (String) deleteList.get(i) + "'";
						}
						if (i > 0 && i % 50 == 0) {
							String strSql = sql + " and p0401 in (" + taskIds + ")";
							dao.delete(strSql, new ArrayList());
							taskIds = "";
						}
					}
					if (taskIds.length() > 0) {
						String strSql = sql + " and p0401 in (" + taskIds + ")";
						dao.delete(strSql, new ArrayList());
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * @Title: updateTargetScore
	 * @Description: 更新目标卡打分记录
	 * @param @param
	 *            khplan_id 考核计划编号
	 * @param @param
	 *            mainbodyId 考核直接上级
	 * @param @param
	 *            objectId 被考核人
	 * @param @return
	 * @return boolean
	 */
	public boolean updateTargetScore(String khplan_id, String mainbodyId, String objectId) {
		ContentDAO dao = new ContentDAO(this.getConn());
		boolean b = true;
		try {
			ArrayList gradeList = getStandardGradeList();
			String sql = "select * from per_target_evaluation where plan_id =" + khplan_id + " and object_id ='" + objectId + "' and mainbody_id='" + mainbodyId + "'";
			RowSet rSet = dao.search(sql);
			// 需要更新的打分记录
			ArrayList updList = new ArrayList();
			while (rSet.next()) {
				String id = rSet.getString("id");
				String p0400 = rSet.getString("p0400");
				boolean bExist = false;
				int index = 0;
				LazyDynaBean scoreBean = null;
				for (int i = 0; i < fScoreList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) fScoreList.get(i);
					if (p0400.equals((String) bean.get("p0400"))) {
						bExist = true;
						index = i;
						scoreBean = bean;
						break;
					}
				}
				if (bExist) {// 需要更新
					String score = (String) scoreBean.get("score");
					String p0413 = (String) scoreBean.get("p0413");
					String degreeId = getDegreeIdByScore(gradeList, p0413, score);
					ArrayList list = new ArrayList();
					list.add(score);
					list.add(degreeId);
					list.add(id);
					updList.add(list);
					fScoreList.remove(index);
				}
			}
			// 需要新增的打分记录
			ArrayList newList = new ArrayList();
			for (int i = 0; i < fScoreList.size(); i++) {
				LazyDynaBean scoreBean = (LazyDynaBean) fScoreList.get(i);
				String p0400 = (String) scoreBean.get("p0400");
				String score = (String) scoreBean.get("score");
				String p0413 = (String) scoreBean.get("p0413");
				String degreeId = getDegreeIdByScore(gradeList, p0413, score);

				ArrayList list = new ArrayList();
				IDGenerator idg = new IDGenerator(2, this.conn);
				String id = idg.getId("per_target_evaluation.id");
				list.add(new Integer(id));
				list.add(new Integer(khplan_id));
				list.add(objectId);
				list.add(mainbodyId);
				list.add(new Integer(p0400));
				list.add(new Double(score));
				list.add(degreeId);
				newList.add(list);
			}

			// 新增记录
			if (newList.size() > 0) {
				StringBuffer _sql = new StringBuffer("insert into per_target_evaluation ");
				_sql.append("(id,plan_id,object_id,mainbody_id,p0400,score,degree_id)");
				_sql.append("values (?,?,?,?,?,?,?)");
				dao.batchInsert(_sql.toString(), newList);
			}
			// 更新记录
			if (updList.size() > 0) {
				StringBuffer _sql = new StringBuffer("update per_target_evaluation ");
				_sql.append(" set score=?,degree_id=?");
				_sql.append(" where id =?");
				dao.batchUpdate(_sql.toString(), updList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return b;
	}

	/**
	 * @Title: getGradeList
	 * @Description: 获取指标标准标度
	 * @param @return
	 * @return ArrayList
	 */
	public ArrayList getStandardGradeList() {
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select * from per_grade_template order by grade_template_id";
			rs = dao.search(sql);
			LazyDynaBean abean = null;
			while (rs.next()) {
				abean = new LazyDynaBean();
				abean.set("gradedesc", rs.getString("gradedesc"));
				abean.set("grade_template_id", rs.getString("grade_template_id"));
				abean.set("top_value", rs.getString("top_value") != null ? rs.getString("top_value") : "");
				abean.set("bottom_value", rs.getString("bottom_value") != null ? rs.getString("bottom_value") : "");
				abean.set("gradevalue", rs.getString("gradevalue") != null ? rs.getString("gradevalue") : "");
				list.add(abean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}

	/**
	 * @Title: getDegreeIdByScore
	 * @Description: 通过分数得到 标度值
	 * @param @param
	 *            gradeList 标准标度
	 * @param @param
	 *            p0413 目标卡分值
	 * @param @param
	 *            score 打分分值
	 * @param @return
	 * @return String
	 */
	public String getDegreeIdByScore(ArrayList gradeList, String p0413, String score) {
		String degree_id = "";
		try {
			if (fStandardGradeList == null || fStandardGradeList.size() < 1) {
				fStandardGradeList = getStandardGradeList();
			}
			for (int j = 0; j < fStandardGradeList.size(); j++) {
				LazyDynaBean gradeBean = (LazyDynaBean) fStandardGradeList.get(j);
				String grade_template_id = (String) gradeBean.get("grade_template_id");
				String _gradevalue = (String) gradeBean.get("gradevalue");
				String top_value = (String) gradeBean.get("top_value");
				String bottom_value = (String) gradeBean.get("bottom_value");

				double value = Double.parseDouble(score);
				double top = Double.parseDouble(PubFunc.multiple(top_value, p0413, 2));
				double bottom = Double.parseDouble(PubFunc.multiple(bottom_value, p0413, 2));
				if (value <= top && value >= bottom) {
					degree_id = grade_template_id;
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return degree_id;
	}

	/**
	 * @Title: isNeedAddToP04 @Description:判断是否需要添加到p04表 @param @param
	 *         rtaskset @param @return @return boolean @author:wangrd @throws
	 */
	public boolean isNeedAddToP04(RowSet rtaskset, String kh_planid, String objectid, String p0723) {
		boolean b = false;
		try {
			// 是否有权重
			double rank = rtaskset.getFloat("rank");
			if (rank == 0) {
				return b;
			}
			// 是否已取消 p0833= 2
			int p0833 = rtaskset.getInt("p0833"); // 任务变更状态
			String p0809 = rtaskset.getString("p0809"); // 任务执行状态
			if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809) || WorkPlanConstant.TaskChangedStatus.Cancel == p0833) { // 已取消
				return b;
			}
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return b;
	}

	public int modifyP04(HashMap<?, ?> map) throws GeneralException {

		int errorcode = 1;
		try {
			String objectids = String.valueOf(map.get("objectids"));
			String planType = String.valueOf(map.get("plantype"));// 计划类型1：人员 2：团队
			String periodType = String.valueOf(map.get("periodtype"));// 计划区间1、年度2、半年3、季度4、月份5、周
			String periodYear = String.valueOf(map.get("periodyear"));// 年份
			String periodMonth =String.valueOf(map.get("periodmonth"));// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
			String periodWeek = String.valueOf(map.get("periodweek"));// 第几周
			objectids = (objectids == null) ? "" : objectids;
			String[] arrObjectIds = objectids.split(",");
			boolean bAll = true;
			for (int i = 0; i < arrObjectIds.length; i++) {
				String objectid1 = PubFunc.decrypt(arrObjectIds[i]);
				if (objectid1.length() > 0) {
					bAll = false;
					arrObjectIds[i] = objectid1;
				}
			}
			this.batchUpdateTargetCard(arrObjectIds, bAll, planType, periodType, periodYear, periodMonth, periodWeek);
			errorcode = 0;

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return errorcode;
	}

	/**
	 * 计划是否存在
	 * 
	 * @param plan_id
	 * @return
	 */
	public boolean isExistsPlan(String plan_id) {
		boolean b = false;
		ContentDAO dao = new ContentDAO(this.getConn());
		try {
			ArrayList paramList = new ArrayList();
			paramList.add(Integer.valueOf(plan_id));
			String strsql = "select template_id from per_plan where plan_id =? ";
			RowSet rset = dao.search(strsql, paramList);
			if (rset.next()) {
				b = true;
			}

		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;

	}

	public boolean checkCanEditTemplate(String khplan_id, String[] arrobjectIds, boolean bAll, HashMap map, boolean reRelate, String planType, String periodType, String periodYear, String periodMonth,
			String periodWeek) {
		ContentDAO dao = new ContentDAO(this.getConn());

		boolean b = true;
		String strsql = "";
		RowSet rset = null;
		ArrayList paramList = new ArrayList();
		try {
			// 首先此模板已关联其他计划
			strsql = "select template_id from per_plan where plan_id =?";
			paramList.clear();
			paramList.add(Integer.valueOf(khplan_id));
			rset = dao.search(strsql, paramList);
			String template_id = "";
			if (rset.next()) {
				template_id = rset.getString("template_id");
			}

			if (!isOtherPlanUseThisTemplate(khplan_id, template_id)) {
				return b;
			}

			String itemdescs = "";
			strsql = "select * from per_template_item where template_id ='" + template_id + "'";
			rset = dao.search(strsql);
			while (rset.next()) {
				itemdescs = itemdescs + "," + rset.getString("itemdesc");
			}
			itemdescs = "," + itemdescs + ",";

			FieldItem item = DataDictionary.getFieldItem("p0823", "P08");
			if (item == null)
				return b;
			String codeSetId = item.getCodesetid();
			// 需要检查的工作计划
			b = true;
			String taskdesc = "";
			paramList.clear();
			strsql = getCheckPeopleSql(paramList, arrobjectIds, bAll, planType, periodType, periodYear, periodMonth, periodWeek);
			rset = dao.search(strsql, paramList);
			while (rset.next()) {
				String p0700 = rset.getString("p0700");
				String _p0719 = rset.getString("p0719");
				if (p0700 == null)
					continue;
				if (_p0719 == null)
					_p0719 = "";
				if (!"2".equals(_p0719)) {
					continue;
				}
				if (!reRelate) {// 已关联的不再关联
					int relate_planid = rset.getInt("relate_planid");
					if (relate_planid > 0) {
						continue;
					}
				}
				PlanTaskTreeTableBo taskTreeBo = new PlanTaskTreeTableBo(this.conn, Integer.parseInt(p0700));
				String sql = taskTreeBo.getTableDatasql("");
				RowSet rtaskset = dao.search(sql);
				boolean bBreak = false;
				while (rtaskset.next()) {
					if (isNeedAddToP04(rtaskset, "", "", "")) {// 需要考核
						String p0823 = rtaskset.getString("p0823") == null ? "" : rtaskset.getString("p0823");
						if (codeSetId.length() > 0)
							p0823 = AdminCode.getCodeName(codeSetId, p0823);
						if (itemdescs.indexOf("," + p0823 + ",") < 0) {
							taskdesc = p0823;
							bBreak = true;
							break;
						}
					}
				}
				if (bBreak) {
					b = false;
					break;
				}
			}
			map.put("taskDesc", taskdesc);

		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * @Title: isOtherPlanUseThisTemplate @Description: 是否其他计划关联了此模板 @param @param
	 *         plan_id @param @param template_id @param @return @return
	 *         boolean @author:wangrd @throws
	 */
	public boolean isOtherPlanUseThisTemplate(String plan_id, String template_id) {
		ContentDAO dao = new ContentDAO(this.getConn());
		boolean b = false;
		try {
			ArrayList paramList = new ArrayList();
			paramList.add(Integer.valueOf(plan_id));
			String strsql = "select template_id from per_plan where plan_id <>? and template_id='" + template_id + "'";
			RowSet rset = dao.search(strsql, paramList);
			if (rset.next()) {
				b = true;
			}

		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;

	}

	/**
	 * 批量更新目标卡
	 * @param arrobjectIds
	 * @param bAll
	 * @param planType
	 * @param periodType
	 * @param periodYear
	 * @param periodMonth
	 * @param periodWeek
	 * @return
	 */
	public HashMap batchUpdateTargetCard(String[] arrobjectIds, boolean bAll, String planType, String periodType, String periodYear, String periodMonth, String periodWeek) {
		HashMap planMap = new HashMap();
		planMap.put("planIds", ",");
		String strsql = "";
		RowSet rset = null;
		ArrayList paramList = new ArrayList();
		String relate_planids = ",";
		ContentDAO dao = new ContentDAO(this.getConn());
		try {
			RelatePerformancePlanBo relateBo = new RelatePerformancePlanBo(this.conn, this.userView);
			strsql = getCheckPeopleSql(paramList, arrobjectIds, bAll, planType, periodType, periodYear, periodMonth, periodWeek);
			rset = dao.search(strsql, paramList);
			while (rset.next()) {
				String p0700 = rset.getString("p0700");
				if (p0700 == null)
					continue;
				String _p0719 = rset.getString("p0719");
				if (_p0719 == null)
					_p0719 = "";
				if (!"2".equals(_p0719)) {
					continue;
				}
				String objectid = "";
				if ("1".equals(planType)) {
					objectid = rset.getString("nbase") + rset.getString("a0100");
				} else {
					objectid = rset.getString("p0707");
				}

				int relate_planid = rset.getInt("relate_planid");
				if (relate_planid < 1) {
					continue;
				}
				if (!isExistsPlan(String.valueOf(relate_planid))) {
					continue;
				}
				WorkPlanBo planBo = new WorkPlanBo(this.conn, this.userView);
				planBo.initPlan(objectid, planType, periodType, periodYear, periodMonth, periodWeek);
				// 检查是否能修改模板
				String[] arrObjectIds = objectid.split(",");
				HashMap map = new HashMap();
				if (!checkCanEditTemplate(String.valueOf(relate_planid), arrObjectIds, false, map, true, planType, periodType, periodYear, periodMonth, periodWeek)) {
					continue;
				}
				if ("1".equals(planType)) {
					objectid = objectid.substring(3);
				}

				if (!relate_planids.contains("," + String.valueOf(relate_planid) + ","))
					relate_planids = relate_planids + String.valueOf(relate_planid) + ",";
				updateTargetCard(String.valueOf(relate_planid), objectid, planType, p0700);
			}
			planMap.put("planIds", relate_planids);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return planMap;
	}

    /**
     * 重置考核主体的打分状态，清空打分分数
     * @param map
     */
    public void clearMainBodyScore(HashMap<String,List<String>> map,String planType) {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            //1、重置考核主体的评分状态
            StringBuffer sqlpm = new StringBuffer();
            sqlpm.append("update per_mainbody set status=0,score = null where");
            sqlpm.append(" plan_id=? and Object_id in({str})");
            StringBuffer sqlp04 = new StringBuffer();
            sqlp04.append("update per_target_evaluation");
            sqlp04.append(" set score=null where plan_id=? and object_id in ({str})");
            List values = new ArrayList();
            for(Map.Entry<String,List<String>> entry : map.entrySet()){
               String plan_id = entry.getKey();
                List<String> persons = entry.getValue();
                values.clear();
                values.add(plan_id);
                String temp = "";
                RecordVo vo = getPlanVo(plan_id);
                String title = vo.getString("name")+"_(评分)";
                String url = "";
                if("1".equals(planType))
                {
                    // 把展现单个目标卡的链接换成展现计划打分列表的链接
                    url = "/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&returnflag=8&planid="+plan_id+"&opt=4&entranceType=0&isSort=1";

                }else if("2".equals(planType))
                {
                    // 把展现单个目标卡的链接换成展现计划打分列表的链接
                    url = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&returnflag=8&planid="+plan_id+"&opt=4&entranceType=0&isSort=1";
                }
                for(int i=0;i<persons.size();i++){
                    String objectid =PubFunc.decrypt(persons.get(i));
                    values.add(objectid);
                    //发送代办
                    sendMessageToPT(title,objectid,"Usr", url,plan_id);
                    if(i>0){
                        temp+=",";
                    }
                    temp+="?";
                }
                dao.update(sqlpm.toString().replace("{str}",temp),values);
                dao.update(sqlp04.toString().replace("{str}",temp),values);
            }
        }catch (Exception e){

        }finally {

        }
    }

    public HashMap<String, String> getQueryNum(HashMap<?, ?> map) throws GeneralException {
		HashMap<String, String> numMap = new HashMap<String, String>();

		ContentDAO dao = new ContentDAO(this.getConn());
		try {
			String planType = String.valueOf(map.get("plantype"));// 计划类型1：人员 2：团队
			String periodType = String.valueOf(map.get("periodtype"));// 计划区间1、年度2、半年3、季度4、月份5、周
			String periodYear = String.valueOf(map.get("periodyear"));// 年份
			String periodMonth = String.valueOf(map.get("periodmonth"));// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
			String periodWeek = String.valueOf(map.get("periodweek"));// 第几周

			int sum_count = 0;// 总数
			int approve_count = 0;// 已批准
			int unapprove_count = 0;// 未批准
			int submit_count = 0;// 已提交
			int unsubmit_count = 0;// 未提交
			int change_count = 0;// 已变更
			RowSet rset = null;
			String strsql = "";
			ArrayList paramList = new ArrayList();
			strsql = getCountSqlByPlanStatus("0", planType, periodType, periodYear, periodMonth, periodWeek);
			if (strsql.trim().length() > 0) {
				rset = dao.search(strsql, paramList);
				while (rset.next()) {
					// 所有
					sum_count++; 
					
					int p0719 = rset.getInt("p0719");
					int changeflag = rset.getInt("changeflag");
					//  未报
					if(p0719==0 || p0719==3) {
						unsubmit_count++;
					}
					// 已报
					if(p0719==1 || p0719==2) {
						submit_count++;
					}
					// 未批
					if(p0719==1) {
						unapprove_count++;
					}
					// 已批
					if(p0719==2) {
						approve_count++;
					}
					// 已变更
					if(changeflag==1) {
						change_count++;
					}
				}
			}
			/*strsql = getCountSqlByPlanStatus("1",planType, periodType, periodYear, periodMonth, periodWeek);
			if (strsql.trim().length() > 0) {
				rset = dao.search(strsql, paramList);
				if (rset.next()) {
					unsubmit_count = rset.getInt(1);
				}
				paramList.clear();
			}
			strsql = getCountSqlByPlanStatus("2", planType, periodType, periodYear, periodMonth, periodWeek);
			if (strsql.trim().length() > 0) {
				rset = dao.search(strsql, paramList);
				if (rset.next()) {
					submit_count = rset.getInt(1);
				}
				paramList.clear();
			}
			strsql = getCountSqlByPlanStatus("3", planType, periodType, periodYear, periodMonth, periodWeek);
			if (strsql.trim().length() > 0) {
				rset = dao.search(strsql, paramList);
				if (rset.next()) {
					unapprove_count = rset.getInt(1);
				}
				paramList.clear();
			}
			strsql = getCountSqlByPlanStatus("4", planType, periodType, periodYear, periodMonth, periodWeek);
			if (strsql.trim().length() > 0) {
				rset = dao.search(strsql, paramList);
				if (rset.next()) {
					approve_count = rset.getInt(1);
				}
				paramList.clear();
			}
			strsql = getCountSqlByPlanStatus("5",planType, periodType, periodYear, periodMonth, periodWeek);// 已变更
			if (strsql.trim().length() > 0) {
				rset = dao.search(strsql, paramList);
				if (rset.next()) {
					change_count = rset.getInt(1);
				}
				paramList.clear(); // 修改 haosl 20160627
			}*/

			numMap.put("sum_count", sum_count + "");
			numMap.put("submit_count", submit_count + "");
			numMap.put("unapprove_count", unapprove_count + "");
			numMap.put("approve_count", approve_count + "");
			numMap.put("unsubmit_count", unsubmit_count + "");
			numMap.put("change_count", change_count + "");

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return numMap;

	}

	/**
	 * plantype
	 * 
	 * @return
	 */
	public ArrayList<ColumnsInfo> getColumnsList(String plantype) {
		ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setQueryable(true);
		if ("1".equals(plantype)) {
			// 姓名
			columnsInfo.setColumnId("a0101");
			columnsInfo.setColumnDesc("姓名");
			columnsInfo.setRendererFunc("WorkPlanhr_me.a0101");
			columnsList.add(columnsInfo);
		}
		// 单位名称
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("b0110");
		columnsInfo.setCodesetId("UN");
		columnsInfo.setColumnDesc("单位名称");
		if("2".equals(plantype)) {
			columnsInfo.setRendererFunc("WorkPlanhr_me.b0110");
		}
		columnsInfo.setColumnType("A");
		columnsInfo.setColumnWidth(150);
		columnsList.add(columnsInfo);
		
		if ("2".equals(plantype)) {
			// 负责人
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("a0101");
			columnsInfo.setColumnDesc("负责人");
			columnsInfo.setColumnType("A");
			columnsList.add(columnsInfo);
			//负责人id加密
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("dept_leader_id");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.setEncrypted(true);//加密
			columnsInfo.setEncrypted_64base(true);//加密_base
			columnsList.add(columnsInfo);
		}
		// 部门
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("e0122");
		columnsInfo.setColumnDesc("部门");
		columnsInfo.setCodesetId("UM");
		columnsInfo.setColumnType("A");
		columnsInfo.setColumnWidth(150);
		if ("2".equals(plantype)) {
			columnsInfo.setRendererFunc("WorkPlanhr_me.orgrender");
			columnsList.add(0, columnsInfo);
		} else {
			columnsList.add(columnsInfo);
		}

		// 岗位
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("e01a1");
		columnsInfo.setColumnDesc("岗位");
		columnsInfo.setColumnType("A");
		columnsInfo.setCodesetId("@K");
		columnsInfo.setColumnWidth(150);
		columnsList.add(columnsInfo);
		// 计划权重
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("rank");
		columnsInfo.setSortable(false);
		columnsInfo.setColumnDesc("计划权重");
		columnsInfo.setTextAlign("center");
		columnsInfo.setRendererFunc("WorkPlanhr_me.totalRank");
		columnsList.add(columnsInfo);
		// 状态
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("p0719");
		columnsInfo.setTextAlign("center");
		columnsInfo.setColumnDesc("状态");
		columnsInfo.setRendererFunc("WorkPlanhr_me.p0719");
		columnsList.add(columnsInfo);
		// 审批人
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("p0735");
		columnsInfo.setColumnDesc("审批人");
		columnsInfo.setSortable(false);
		columnsInfo.setTextAlign("center");
		columnsInfo.setRendererFunc("WorkPlanhr_me.p0735");
		columnsList.add(columnsInfo);
		// 计划关注人
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("follower");// TODO 计划关注人重命名
		columnsInfo.setColumnDesc("计划关注人");
		columnsInfo.setSortable(false);
		columnsInfo.setRendererFunc("WorkPlanhr_me.follower");
		columnsInfo.setColumnWidth(200);
		columnsList.add(columnsInfo);
		// 操作
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("operations");// TODO 计划关注人重命名
		columnsInfo.setColumnDesc("操作");
		columnsInfo.setSortable(false);
		columnsInfo.setRendererFunc("WorkPlanhr_me.operations");
		columnsInfo.setColumnWidth(150);
		columnsList.add(columnsInfo);
		
		//个人计划页面：nbase+a0100加密; 部门计划为机构号加密 
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("objectid_safe");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsInfo.setEncrypted(true);//加密
		columnsList.add(columnsInfo);
		
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("objectid_url");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsInfo.setEncrypted(true);//加密
		columnsInfo.setEncrypted_64base(true);//加密_base
		columnsList.add(columnsInfo);
		
		//变更标记
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("changeflag");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);
        //关联计划的ID
        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("relate_planid");
        columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnsList.add(columnsInfo);
		//变更标记
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("p0700");
		columnsInfo.setEncrypted(true);
		columnsInfo.setEncrypted_64base(true);//加密_base
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
		columnsList.add(columnsInfo);
		
		return columnsList;
	}

	// 按钮list
	public ArrayList getButtonList(String plantype) {
		ArrayList buttonList = new ArrayList();
		ButtonInfo buttonInfo = new ButtonInfo("提醒大家写计划", "WorkPlanhr_me.noticeAllWrite");
		buttonInfo.setId("noticeAllWrite");
		buttonList.add(buttonInfo);

		buttonInfo = new ButtonInfo("提醒批准计划", "WorkPlanhr_me.noticeApprove");
		buttonInfo.setId("noticeApprove");
		buttonList.add(buttonInfo);

		buttonInfo = new ButtonInfo("关联考核计划", "WorkPlanhr_me.relatePlanAll_btn");
		buttonInfo.setId("selectPlan");
		buttonList.add(buttonInfo);

		buttonInfo = new ButtonInfo("更新目标卡", "WorkPlanhr_me.updateCard_btn");
		buttonInfo.setId("updateCard");
		buttonList.add(buttonInfo);

		ButtonInfo queryBox = new ButtonInfo();
		queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
		if("1".equals(plantype)) {//个人
			queryBox.setText("请输入姓名、拼音简码、部门、Email...");
		}else {//部门
			queryBox.setText("请输入负责人、拼音简码、部门、Email...");
		}
		queryBox.setFunctionId("WP50000001");
		queryBox.setShowPlanBox(false);
		buttonList.add(queryBox);
		return buttonList;
	}
	/**
	 * 获得表格控件主sql 
	 * @throws GeneralException 
	 */
	public String getTableSql(String plantype) {
		String strsql = "";
		String tablename = "";
		if ("2".equals(plantype)) {
			tablename = getTeamDeptSql();
		} else {
			tablename = getTeamPeopleSql();
		}
		if ("".equals(tablename)) {
			return "";
		}
		tablename = "(" + tablename + ") T";
		String relateFld = "";
		DbWizard dbw = new DbWizard(this.getConn());
		if (dbw.isExistField("p07", "relate_planid", false)) {
			relateFld = ",p07.relate_planid ";
		}
		if ("2".equals(plantype)) {
			strsql = " Select T.email,T.pinyin,T.e01a1,T.B0110,T.B0110 as objectid_safe,T.B0110 as objectid_url,T.E0122,T.a0101,T.dept_leader_id,p07.p0700,p07.p0725,p07.p0727,p07.p0729,P07.p0719,P07.p0735" + relateFld + ",p07.p0707,p07.p0731,p07.p0733,p07.changeflag,T.codeitemdesc from " + tablename + " " + "left join (select * from P07 where p07.p0723=2) P07 on p07.p0707 =T.B0110 {0}";
		} else {
			strsql = " select T.nbase"+Sql_switcher.concat()+"T.a0100 as objectid_safe,T.nbase"+Sql_switcher.concat()+"T.a0100 as objectid_url, T.*,p07.p0700,p07.p0725,p07.p0727,p07.p0729,p07.p0731,p07.p0733,P07.p0719,p07.E0122 as p07e0122,p07.e01a1 as p07e01a1,p07.p0735" + relateFld + ",p07.changeflag from " + tablename
					+ " left join p07 on P07.nbase=T.nbase and P07.a0100=T.a0100" + " and  p07.p0723=1 {0}";
		}

		return strsql;
	}

	/**
	 * 获取默认查询指标
	 * 
	 * @return
	 */
	public ArrayList getDefaultQuery() {
		ArrayList<HashMap<String, String>> defaultQuery = new ArrayList<HashMap<String, String>>();
		// 查询模板预置
		HashMap<String, String> hm = new HashMap<String, String>();
		hm = new HashMap<String, String>();
		hm.put("fieldsetid", "A01");
		hm.put("itemid", "E0122");
		hm.put("itemdesc", "部门");
		hm.put("itemtype", "A");
		hm.put("codesetid", "UM");
		defaultQuery.add(hm);
		hm = new HashMap<String, String>();
		hm.put("fieldsetid", "A01");
		hm.put("itemid", "A0101");
		hm.put("itemdesc", "姓名");
		hm.put("itemtype", "A");
		hm.put("codesetid", "0");
		defaultQuery.add(hm);
		hm = new HashMap<String, String>();
		hm.put("fieldsetid", "A01");
		hm.put("itemid", getPinYinFld());
		hm.put("itemdesc", "拼音简码");
		hm.put("itemtype", "A");
		hm.put("codesetid", "0");
		defaultQuery.add(hm);
		return defaultQuery;
	}

	/**
	 * 取得拼音指标
	 * 
	 * @Title: getPinYinFld
	 * @Description:
	 * @return
	 */
	private String getPinYinFld() {
		// 获取拼音简码的字段
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
		String pinyinFld = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		if (null == pinyinFld || "".equals(pinyinFld.trim()))
			pinyinFld = "";

		if (!fieldInA01(pinyinFld))
			pinyinFld = "";

		return pinyinFld;
	}

	/**
	 * 是否是主集指标并已构库
	 * 
	 * @Title: fieldInA01
	 * @Description:
	 * @param field
	 * @return
	 */
	private boolean fieldInA01(String field) {
		boolean inA01 = false;
		if (null == field || "".equals(field.trim()))
			return inA01;

		FieldItem fieldItem = DataDictionary.getFieldItem(field, "a01");
		inA01 = null != fieldItem && "1".equals(fieldItem.getUseflag());

		return inA01;
	}
	public String getEmailField() {
		String emailId = "";
		try {
			emailId=ConstantParamter.getEmailField().toLowerCase(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailId;
	}
	/**
	 * 获得计划期间相关参数
	 * @param formHM
	 * @return
	 */
	public void getPeriodParams(HashMap formHM) {
	WorkPlanUtil workPlanUtil=new WorkPlanUtil(this.conn,userView);
	if(formHM.get("periodtype") != null) {
		int periodyear=2017;//用于给前台返回
		int periodmonth=1;//用于给前台返回
		int periodweek=1;//用于给前台返回
		int weeknum = 4;//用于给前台返回
		String periodtype =(String)formHM.get("periodtype");
		Date now =new Date();
		int curYear = DateUtils.getYear(now); ;
		int curMonth = DateUtils.getMonth(now); 
		int[] weeks= workPlanUtil.getLocationPeriod(periodtype,curYear,curMonth);
		periodyear = weeks[0];
		if (WorkPlanConstant.Cycle.WEEK.equals(periodtype)){
			periodmonth = weeks[1];  
			periodweek=weeks[2];
			weeknum = workPlanUtil.getWeekNum(String.valueOf(curYear),String.valueOf(curMonth));
			
		}            
		else if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodtype)        
				||WorkPlanConstant.Cycle.QUARTER.equals(periodtype) 
				|| WorkPlanConstant.Cycle.MONTH.equals(periodtype)){   
			periodmonth=weeks[1];
			
		}
		formHM.put("periodyear", periodyear);
		formHM.put("periodmonth", periodmonth);
		formHM.put("periodweek", periodweek);
		formHM.put("weeknum", weeknum);
	}
     
	// 当前日期的信息
	if(formHM.get("getcurrentinfo") != null) {
		Date now = new Date();
		int curYear = DateUtils.getYear(now);
		int curMonth = DateUtils.getMonth(now);
		formHM.put("curyear", curYear);
		formHM.put("curmonth", curMonth);
		formHM.put("curhalfyear", workPlanUtil.getLocationPeriod(WorkPlanConstant.Cycle.HALFYEAR, curYear, curMonth)[1]);// 当前上半年还是下半年
		formHM.put("curquarter", workPlanUtil.getLocationPeriod(WorkPlanConstant.Cycle.QUARTER, curYear, curMonth)[1]);// 当前处于第几季度
		formHM.put("curweek", workPlanUtil.getLocationPeriod(WorkPlanConstant.Cycle.WEEK, curYear, curMonth)[2]);
	}
	
    // 指定月份共有几周
		if(formHM.get("gettotalnum") != null) {
			String year = String.valueOf(formHM.get("year"));
			String month = String.valueOf(formHM.get("month"));
			int num = workPlanUtil.getWeekNum(String.valueOf(year), String.valueOf(month));
			formHM.put("num", num);
		}
	}
	 private String getFollower(String p0700) throws GeneralException {
		 	ContentDAO dao = new ContentDAO(this.conn);
		 	RowSet rs = null;
		 	StringBuffer followers = new StringBuffer();
	        try{
		        String strsql="select P0913,Nbase,A0100 from p09 where P0901=1 and p0905=3 and p0903="+p0700;
		        rs = dao.search(strsql);
	            while (rs.next()){
	                String name=rs.getString("P0913");
	                String Nbase=rs.getString("Nbase");
	                String A0100=rs.getString("A0100");
	                if(!StringUtils.isEmpty(name)) {
	                	// 增加已选关注人信息 chent add 20171218
	                	String nbsa0100_e = PubFunc.encrypt(Nbase+A0100);
	                	followers.append(name+"_"+nbsa0100_e+"、");
	                }
	            }
	            int len = followers.length();
	            if(len>0) 
	            	followers.setLength(len-1);
	            return followers.toString();
	        }catch(Exception e){           
	           throw GeneralExceptionHandler.Handle(e);
	        }finally {
				PubFunc.closeResource(rs);
			}
	    }
	/**
	 * 检查当前选中的计划是否可以关联
	 * @param map
	 * @return
	 * @throws GeneralException
	 */
	public HashMap checkSuperBodyType(HashMap map) throws GeneralException {
		
		HashMap resMap = new HashMap();
		
		String planType = (String) map.get("plantype");// 计划类型1：人员 2：团队
		String periodType = (String) map.get("periodtype");// 计划区间1、年度2、半年3、季度4、月份5、周
		String periodYear = (String) map.get("periodyear");// 年份
		String periodMonth = (String) map.get("periodmonth");// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
		String periodWeek = (String) map.get("periodweek");// 第几周
		String plan_id = String.valueOf(map.get("planId"));
		String objectIds = String.valueOf(map.get("objectids"));
		
		objectIds = (objectIds == null) ? "" : objectIds;
		String[] arrObjectIds = objectIds.split(",");
		boolean bAll = true;
		for (int i = 0; i < arrObjectIds.length; i++) {
			String objectid1 = PubFunc.decrypt(arrObjectIds[i]);
			if (objectid1.length() > 0) {
				bAll = false;
				arrObjectIds[i] = objectid1;
			}
		}
		String info = this.checkHaveSuperBodyType(plan_id, arrObjectIds, bAll, planType, periodType, periodYear, periodMonth, periodWeek);
		if (info.length() > 0) {
			resMap.put("errorcode", "1");
			resMap.put("info", info);
		}else {
			resMap.put("errorcode", "0");
		}
		return resMap;
	}
    /**
     * 检查是否有上级、上上级主体类别 如果主体类别没设上上级、而且所选考核对象有上上级，则提示，否则不提示
     * @param khplan_id
     * @param arrobjectIds
     * @param bAll
     * @param planType
     * @param periodType
     * @param periodYear
     * @param periodMonth
     * @param periodWeek
     * @return
     */
    public String checkHaveSuperBodyType(String khplan_id,String [] arrobjectIds,boolean bAll, String planType, String periodType, String periodYear, String periodMonth, String periodWeek) 
    {
        String info="";
        ContentDAO dao = new ContentDAO(this.getConn());
        try {
            RowSet rset = null;
            ArrayList paramList = new ArrayList();
           
            RelatePerformancePlanBo relateBo =new RelatePerformancePlanBo(this.conn,this.userView);
            String superBoydid = relateBo.getBodyIdByBodyType(khplan_id,"1");//上级
            String superSuperBoydid = relateBo.getBodyIdByBodyType(khplan_id,"0");//上上级
            boolean bHaveSuper=false;
            boolean bHaveSuperSuper=false;
            if (superBoydid.length()>0){//设置了上级 不检查
                bHaveSuper=false;
            }
            else {                
                String strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll, planType, periodType, periodYear, periodMonth, periodWeek);  
                rset = dao.search(strsql,paramList);  
                bHaveSuper=checkHaveSuperPeople(rset,bAll,true, planType, periodType, periodYear, periodMonth, periodWeek);
            
            }
            if (superSuperBoydid.length()>0){
                bHaveSuperSuper=false;
            }
            else {
                String strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll, planType, periodType, periodYear, periodMonth, periodWeek);  
                rset = dao.search(strsql,paramList);  
                bHaveSuperSuper=checkHaveSuperPeople(rset,bAll,false, planType, periodType, periodYear, periodMonth, periodWeek);
            }
            if (bHaveSuper){
                info="上级";
            }
            if (bHaveSuperSuper){
                if (info.length()>0){
                    info=info+"、";
                }
                info=info+"上上级";
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
 }
    /**   
     * @Title: checkHaveSuperPeople   
     * @Description: 查看当前所选人员是否有上级、上上级    
     * @param @param rset
     * @param @param bAll
     * @param @param checkSuper true:判断上级 false 判断上上级
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    private boolean checkHaveSuperPeople(RowSet rset,boolean bAll,boolean checkSuper, String planType, String periodType, String periodYear, String periodMonth, String periodWeek) 
    {
        boolean b = false;
        try {
        	 WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
            while (rset.next()) {
                String _p0719 = rset.getString("p0719");
                if (_p0719 == null)
                    _p0719 = "";
                if (!"2".equals(_p0719)) {
                    continue;
                }

                if ("1".equals(planType)) {
                    String nbase = rset.getString("nbase");
                    if (!"USR".equalsIgnoreCase(nbase)) {
                        continue;
                    }
                }

                String objectid = "";
                if ("2".equals(planType)) {
                    objectid = rset.getString("p0707");

                } else {
                    objectid = rset.getString("a0100");
                }
                String e01a1 = "";
                if (bAll) {
                    if ("2".equals(planType)) {
                        e01a1 = rset.getString("e01a1");
                    } else {
                        e01a1 = rset.getString("p07e01a1");
                    }
                } else {
                    if ("2".equals(planType)) {
                        e01a1 = workPlanUtil.getDeptLeaderE01a1(objectid);
                    } else {
                        e01a1 = rset.getString("e01a1");
                    }
                }
                e01a1 = (e01a1 == null) ? "" : e01a1;

                String superE01a1 = workPlanUtil.getDirectSuperE01a1(e01a1);
                String superA0100 = workPlanUtil.getFirstE01a1Leaders(superE01a1, "USR");
                if (superA0100.length() > 0) {
                    if (!checkSuper) {
                        String superSuperE01a1 = workPlanUtil.getDirectSuperE01a1(superE01a1);
                        if (superSuperE01a1.length() > 0) {
                            String mainBodyA0100 = workPlanUtil.getFirstE01a1Leaders(superSuperE01a1, "USR");
                            if (mainBodyA0100.length() > 0) {
                                b = true;
                                break;
                            }
                        }
                    } else {
                        b = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;  
        
    }
    /**
     * 检查是所选人员是否已关联过
     * @param reqMap
     * @return
     * @throws GeneralException
     */
    public HashMap checkIsRelated(HashMap reqMap) throws GeneralException {
    	HashMap resMap = new HashMap();
		
		String planType = (String) reqMap.get("plantype");// 计划类型1：人员 2：团队
		String periodType = (String) reqMap.get("periodtype");// 计划区间1、年度2、半年3、季度4、月份5、周
		String periodYear = (String) reqMap.get("periodyear");// 年份
		String periodMonth = (String) reqMap.get("periodmonth");// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
		String periodWeek = (String) reqMap.get("periodweek");// 第几周
		String plan_id = String.valueOf(reqMap.get("planId"));
		String objectIds = String.valueOf(reqMap.get("objectids"));
		
		objectIds = (objectIds == null) ? "" : objectIds;
		String[] arrObjectIds = objectIds.split(",");
		boolean bAll = true;
		for (int i = 0; i < arrObjectIds.length; i++) {
			String objectid1 = PubFunc.decrypt(arrObjectIds[i]);
			if (objectid1.length() > 0) {
				bAll = false;
				arrObjectIds[i] = objectid1;
			}
		}
		HashMap map = new HashMap();
		boolean b = this.checkIsRelatedPlan(arrObjectIds, bAll, map, planType, periodType, periodYear, periodMonth, periodWeek);
		if (b) {
			resMap.put("errorcode", "1");
			resMap.put("objectName", (String) map.get("A0101s"));
			resMap.put("objectCount", (String) map.get("count"));
		} else {
			resMap.put("errorcode", "0");
		}
		
		return resMap;
    }
    /**   
     * @Title: checkIsRelatedPlan   
     * @Description: 检查是否有已经关联计划的人员   
     * @param @param objectIds
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public boolean checkIsRelatedPlan(String [] arrobjectIds,boolean bAll,HashMap map, String planType, String periodType, String periodYear, String periodMonth, String periodWeek) 
    {
        boolean b=false;
        ContentDAO dao = new ContentDAO(this.getConn());
        try {
            RowSet rset = null;
            ArrayList paramList = new ArrayList();
            String strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll, planType, periodType, periodYear, periodMonth, periodWeek);  
            rset = dao.search(strsql,paramList);  
            int count=0;
            String A0101s="";
            while (rset.next()){
                String _p0719= rset.getString("p0719");
                if (_p0719==null) _p0719="";               
                if (!"2".equals(_p0719)){
                    continue;
                }                                         
               
     
                if("1".equals(planType)){ 
                    String nbase=rset.getString("nbase");
                    if (!"USR".equalsIgnoreCase(nbase)){
                        continue;
                    }
                }               
               
                String planid=rset.getString("relate_planid");
                if (planid!=null && planid.length()>0 && !"0".equals(planid)){
                    String a0101="";
                    if("1".equals(planType)){ 
                        String nbase=rset.getString("nbase");
                        String a0100=rset.getString("a0100");
                        WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
                        a0101=workPlanUtil.getUsrA0101(nbase, a0100);
                    }               
                    else { 
                        String p0707=rset.getString("p0707");
                        a0101 =AdminCode.getCodeName("UN", p0707);
                        if ("".equals(a0101)){
                            a0101 =AdminCode.getCodeName("UM", p0707);
                        }
                    }                     
                    if (count<5){
                        if (A0101s.length()<1){
                            A0101s=a0101;
                        }
                        else {
                            A0101s=A0101s+"、"+a0101; 
                        } 
                    }
                    count++;
                    b=true;
                }
            }
            map.put("A0101s",A0101s);
            if (count <=5)
                map.put("count","");
            else 
                map.put("count",count+"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
 }
    /**
     * 校验考核计划的模板
     * @param reqMap
     * @return
     */
    public HashMap checkTemplate(HashMap reqMap) {
    	
    	HashMap resMap = new HashMap();
    	
    	String planType = (String) reqMap.get("plantype");// 计划类型1：人员 2：团队
		String periodType = (String) reqMap.get("periodtype");// 计划区间1、年度2、半年3、季度4、月份5、周
		String periodYear = (String) reqMap.get("periodyear");// 年份
		String periodMonth = (String) reqMap.get("periodmonth");// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
		String periodWeek = (String) reqMap.get("periodweek");// 第几周
    	
    	String reRelalte = (String) reqMap.get("reRelalte");
		String plan_id = (String) reqMap.get("planId");
		String objectIds = (String) reqMap.get("objectids");
		
		
		objectIds = (objectIds == null) ? "" : objectIds;
		String[] arrObjectIds = objectIds.split(",");
		boolean bAll = true;
		for (int i = 0; i < arrObjectIds.length; i++) {
			String objectid1 = PubFunc.decrypt(arrObjectIds[i]);
			if (objectid1.length() > 0) {
				bAll = false;
				arrObjectIds[i] = objectid1;
			}
		}
		HashMap map = new HashMap();
		boolean b = this.checkCanEditTemplate(plan_id, arrObjectIds, bAll, map, "true".equals(reRelalte), planType, periodType, periodYear, periodMonth, periodWeek);
		if (!b) {
			resMap.put("errorcode", "1");
			resMap.put("taskDesc", (String) map.get("taskDesc"));
		} else {
			resMap.put("errorcode", "0");
		}
		
		return resMap;
    }
    
    /**
     * 复杂查询
     * @param items
     */
	public int complexQuery(ArrayList<MorphDynaBean> items) {
		int errorcode = 1;
		WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.conn,this.userView);
		String[] dbnames = workPlanUtil.getHrSelfUserDbs();
		String itemtype = "";	//公共查询
		String itemid = "";	//公共查询
		String codesetid = "";	//公共查询
		String fieldsetid = "";	//公共查询
		String value = "";	//公共查询
		String minDate = "";	//公共查询  起始日期 不填为*
		String maxDate = "";	//公共查询  终止日期 不填为*
		String minValue = "";	//公共查询  起始日期 不填为*
		String maxValue = "";	//公共查询  终止日期 不填为*
		String itemdesc = "";	//公共查询  
		String dateType = "";	//公共查询  日期类型 年、月、日
		StringBuffer querySql = new StringBuffer();
			
		if(items!=null && items.size()>0){//haosl 20170315 update 删除筛选条件后，items不为null但是长度为0 querySql 拼接错误(出现 “ or or ”)
			for (int index=0;index<dbnames.length;index++){
				int j=0;
				for (MorphDynaBean bean : items) {
					if(index > 0 && j==0){
						
					}else {
						querySql.append(" and ");
					}
					HashMap dynaBeanMap = PubFunc.DynaBean2Map(bean);
					itemtype = (String)dynaBeanMap.get("itemtype");
					itemid = (String)dynaBeanMap.get("itemid");
					itemdesc = (String)dynaBeanMap.get("itemdesc");
					value = (String) dynaBeanMap.get("value");
					if("A".equalsIgnoreCase(itemtype)){
						codesetid = (String) dynaBeanMap.get("codesetid");
					}
					if(!"AD".equalsIgnoreCase(codesetid)){
						fieldsetid = (String) dynaBeanMap.get("fieldsetid");
					}
					String[] split = null;
					if("D".equalsIgnoreCase(itemtype)){//日期
						dateType = (String) dynaBeanMap.get("type");
						split = value.split("~");
						minDate = split[0];
						maxDate = split[1];
						if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//主集
							querySql.append(" a0100 in (select a0100 from ");
							querySql.append(dbnames[index]+fieldsetid);
							querySql.append(" where ");
						}
						if("area".equalsIgnoreCase(dateType)){
							if(minDate.length()==10){
								minDate = minDate+" 00:00:00";
								minDate = Sql_switcher.dateValue(minDate);
							}
							if(maxDate.length()==10){
								maxDate = maxDate+" 23:59:59";
								maxDate = Sql_switcher.dateValue(maxDate);
							}
							if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" between "+minDate+" and "+maxDate+" ");
							}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" <= "+maxDate+"");
							}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" >= "+minDate+" ");
							}
						}else if(StringUtils.equalsIgnoreCase(dateType, "year"))//年限
						{
							if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.diffYears(Sql_switcher.today(),itemid)+" between '"+minDate+"' and '"+maxDate+"'");
							}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.diffYears(Sql_switcher.today(),itemid)+">='"+minDate+"'");
							}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.diffYears(Sql_switcher.today(),itemid)+"<='"+maxDate+"'");
							}
						}else if(StringUtils.equalsIgnoreCase(dateType, "month"))//月份
						{
							if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.month(itemid)+" between '"+minDate+"' and '"+maxDate+"'");
							}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.month(itemid)+">='"+minDate+"'");
							}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.month(itemid)+"<='"+maxDate+"'");
							}
						}else if(StringUtils.equalsIgnoreCase(dateType, "day"))//天
						{
							if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.day(itemid)+" between '"+minDate+"' and '"+maxDate+"'");
							}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.day(itemid)+">='"+minDate+"'");
							}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
							{
								querySql.append(Sql_switcher.day(itemid)+"<='"+minDate+"'");
							}
						}
						querySql.append(")");
					}else if("N".equalsIgnoreCase(itemtype)){//数值
						split = value.split("~");
						minValue = split[0];
						maxValue = split[1];
						if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//主集
							querySql.append(" a0100 in (select a0100 from ");
							querySql.append(dbnames[index]+fieldsetid);
							querySql.append(" where ");
						}
						
						if((!"*".equalsIgnoreCase(minValue))&&(!"*".equalsIgnoreCase(maxValue))){
							querySql.append(Sql_switcher.isnull(itemid, "0")+" between '"+minValue+"' and '"+maxValue+"'");
						}else if(("*".equalsIgnoreCase(minValue))&&(!"*".equalsIgnoreCase(maxValue))){
							querySql.append(Sql_switcher.isnull(itemid, "0")+" <= '"+maxValue+"'");
						}else if((!"*".equalsIgnoreCase(minValue))&&("*".equalsIgnoreCase(maxValue))){
							querySql.append(Sql_switcher.isnull(itemid, "0")+" >= '"+minValue+"'");
						}
						querySql.append(")");
					}else{
						if(StringUtils.isNotEmpty(value)&&!"D".equalsIgnoreCase(itemtype)){
							split = value.split(",");
							if(!"zp_flow_status".equalsIgnoreCase(fieldsetid)){
								
								if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//主集
										querySql.append(" a0100 in (select a0100 from ");
										querySql.append(dbnames[index]+fieldsetid);
										querySql.append(" where ");
								}
								for (int i = 0; i < split.length; i++) {
									if(StringUtils.isNotEmpty(codesetid)&&!"0".equalsIgnoreCase(codesetid)){
										if("UN".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"AM".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)){//单位和部门
											querySql.append(itemid);
											querySql.append(" like '"+split[i]+"%'");
											querySql.append(" or ");
										}else{
											querySql.append(itemid);
											querySql.append(" ='"+split[i]+"'");
											querySql.append(" or ");
										}
									}else if(StringUtils.isNotEmpty(codesetid)&&"0".equalsIgnoreCase(codesetid)){
										
										querySql.append(itemid);
										if(!"zp_pos_tache".equalsIgnoreCase(fieldsetid)){
											querySql.append(" like '%"+split[i]+"%'");
										}else{
											querySql.append(" ='"+split[i]+"'");
										}
										querySql.append(" or ");
									}
								}
							}else{
								for(int i = 0;i<split.length;i++){
									querySql.append("( "+ itemid);
									querySql.append(" like '%"+split[i]+"%'");
									querySql.append(" or ");
								}
							}
							querySql.setLength(querySql.length()-3);
							querySql.append(")");
						}
					}
					j++;
				}
					
				querySql.append(" or ");
			}
			if(querySql.length()>0)
				querySql.setLength(querySql.length()-3);
		}
		if(" or  ".equals(querySql.toString())){
			querySql.setLength(0);
		}
		
		TableDataConfigCache cache = (TableDataConfigCache) this.userView.getHm().get(WorkPlanHrMainTrans.WORKPLAN_HR_SUBMODULEID);
		StringBuffer tempSql = new StringBuffer();
		tempSql.append(cache.getQuerySql());
		
		//替换复杂查询的查询条件
		int sIndex = tempSql.indexOf("and 3=3");
		int eIndex = tempSql.lastIndexOf("and 3=3");
		if(sIndex>-1 && eIndex>-1 && sIndex < eIndex) {
			tempSql = tempSql.replace(sIndex, eIndex+7, "");
		}
		if(querySql.length()>0)
			tempSql.append(" and 3=3"+querySql+" and 3=3");
		cache.setQuerySql(tempSql.toString());
		
		errorcode = 0;
		
		return errorcode;
	}
	/**
	 * 
	 * @param objectid  nbase+a0100 | e0122
	 * @param formHM  
	 * @return
	 * @throws GeneralException
	 */
	public String getRankForObject(String objectid,HashMap formHM) throws GeneralException{
		String total_rank="0%";
		String plantype = String.valueOf(formHM.get("plantype"));
		StringBuffer sbf = new StringBuffer();
        sbf.append("select sum(ptm.rank) as total_rank from p08,per_task_map ptm,p07");
        sbf.append(" where ptm.p0700=p07.p0700");
        sbf.append(" and ptm.p0800=p08.p0800");
        sbf.append(" and p08.p0809<>5");
        //统计计划权重时，与个人计划的权重合计保持一致  haosl 2018-5-17
        if("1".equals(plantype)) {
        	 sbf.append(" and ptm.nbase=? and ptm.a0100=?");
        	 sbf.append(" and ((p07.p0723=1 and (( ptm.flag<>5 and p08.p0811 in ('02','03')) or ptm.flag=5)");
        	 sbf.append(" ) or ( ");
        	 sbf.append("p07.p0723=2 and ( ptm.flag<>5 and  p08.p0811 in ('02','03'))");
        	 sbf.append("))");
        }else {
        	 sbf.append(" and ptm.org_id=?");
        	 sbf.append(" and (ptm.flag=5  or ((ptm.flag=1 or ptm.flag=2) and dispatchFlag=1 and  p08.p0811 in ('02','03')))");
        }
        String periodType = String.valueOf(formHM.get("periodtype"));// 计划区间1、年度2、半年3、季度4、月份5、周
		String periodYear = String.valueOf(formHM.get("periodyear"));// 年份
		String periodMonth = String.valueOf(formHM.get("periodmonth"));// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
		String periodWeek = String.valueOf(formHM.get("periodweek"));// 第几周

		sbf.append(" and p0725 = " + periodType);
		sbf.append(" and p0727= " + periodYear);
		if (!WorkPlanConstant.Cycle.YEAR.equals(periodType)) {
			sbf.append(" and p0729 = " + periodMonth);
		}
		if (WorkPlanConstant.Cycle.WEEK.equals(periodType)) {
			sbf.append(" and p0731 = " + periodWeek);
		}
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			List values = null;
			values = new ArrayList();
			if("1".equals(plantype)) {
				String nabase = objectid.substring(0,3);
				String a0100 = objectid.substring(3);
				values.add(nabase);
				values.add(a0100);
			}else {
				values.add(objectid);
			}
			rs = dao.search(sbf.toString(),values);
			if(rs.next()) {
				float str = rs.getFloat("total_rank")*100;
        		total_rank = String.valueOf(str);
        		total_rank = total_rank.substring(0, total_rank.indexOf(".")) + "%";
			}
			return total_rank;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 获得关注人、计划权重、部门计划下的单位、负责人的信息
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap<String,HashMap<String,String>> getMappings(HashMap formHM) throws GeneralException {
		HashMap<String,HashMap<String,String>> map = new HashMap<String,HashMap<String,String>>();
		ContentDAO dao = new ContentDAO(this.conn);
		WorkPlanUtil wpUtil = new WorkPlanUtil(this.conn, this.userView);
		RowSet rs = null;
		String plantype = String.valueOf(formHM.get("plantype"));
		String periodType = String.valueOf(formHM.get("periodtype"));
		try {
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get(WorkPlanHrMainTrans.WORKPLAN_HR_SUBMODULEID);
			String tsql = catche.getTableSql();
			
			tsql = tsql.replace("{1}", getTaskFuncSql(plantype,periodType));
			
			String qSql = catche.getQuerySql();
			String sql = "select * from ("+tsql+") myGridData where 1=1 ";
			if(StringUtils.isNotEmpty(qSql)){
				sql += qSql;
			}
			rs = dao.search(sql);
			HashMap<String,String> followerMap = new HashMap<String,String>();
			HashMap<String,String> rankMap = new HashMap<String,String>();
			HashMap<String,String> b0110Map = new HashMap<String,String>();
			while(rs.next()) {
				String objectid = rs.getString("objectid_safe");
				if("2".equals(plantype)) {
					//部门计划 下的单位信息
					String e0122 = rs.getString("e0122");
					String b0110 = wpUtil.getDirectParentUnit(e0122);
					if(StringUtils.isNotBlank(b0110))
						b0110Map.put(PubFunc.encrypt(objectid), AdminCode.getCodeName("UN", b0110));
				}
				String p0700 = rs.getString("p0700");
				if(StringUtils.isEmpty(p0700))
					continue;
				
				//关注人信息
				String follower = this.getFollower(p0700);
				if(StringUtils.isNotBlank(follower))
					followerMap.put(PubFunc.encrypt(objectid), follower);
				//任务权重信息
				String total_rank = this.getRankForObject(objectid, formHM);
				rankMap.put(PubFunc.encrypt(objectid), total_rank);
				
			}
			map.put("followerMap",followerMap);
			map.put("rankMap",rankMap);
			map.put("b0110Map",b0110Map);
			return map;
		}catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 更换审批人
	 * @param map
	 * @return
	 * @throws GeneralException
	 */
	public int modifyP0733(HashMap map) throws GeneralException{
		int errorcode = 1;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String p0700 = String.valueOf(map.get("p0700"));
			if(StringUtils.isNotBlank(p0700))
				p0700 = WorkPlanUtil.decryption(p0700);
			String objectid = String.valueOf(map.get("objectid"));// 计划类型1：人员 2：团队
			objectid = PubFunc.decrypt(objectid);
			
			WorkPlanUtil util = new WorkPlanUtil(this.getConn(), this.getUserView());
			String a0101 = util.getUsrA0101(objectid.substring(0, 3), objectid.substring(3));
			
			String sql = "update p07 set p0733=?, p0735=? where p0700=?";
			int res = dao.update(sql, Arrays.asList(new String[] {objectid, a0101, p0700}));
			if(res == 1) {
				// 发送消息
				sendEmailOnUpdatePlanStatusNew(p0700, "1");
				errorcode = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return errorcode;
	}

	/**
	 * 发送待办
	 * @param plan_id
	 * @param planStatus
	 */
	public void sendEmailOnUpdatePlanStatusNew(String plan_id, String planStatus) {
		ContentDAO dao = new ContentDAO(this.conn);

		try {
			RecordVo p07Vo = new RecordVo("p07");
			p07Vo.setString("p0700", plan_id);
			p07Vo = dao.findByPrimaryKey(p07Vo);
			String periodType = p07Vo.getString("p0725");
			String periodYear = p07Vo.getString("p0727");
			String periodMonth = p07Vo.getString("p0729");
			String periodWeek = p07Vo.getString("p0731");
			String P0723 = p07Vo.getString("p0723");
			String nbase = "";
			String a0100 = "";
			String objectId = "";
			String deptLeaderId = "";

			WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
			if ("1".equals(P0723)) {
				nbase = p07Vo.getString("nbase");
				a0100 = p07Vo.getString("a0100");
				objectId = p07Vo.getString("nbase") + p07Vo.getString("a0100");
			} else {
				objectId = p07Vo.getString("p0707");
				// 部门负责人
				deptLeaderId = workPlanUtil.getFirstDeptLeaders(objectId);

				if (deptLeaderId != null && !"".equals(deptLeaderId)) {
					nbase = deptLeaderId.substring(0, 3);
					a0100 = deptLeaderId.substring(3);
				}
			}

			String strsql = "";
			ArrayList list = new ArrayList();

			// 给上级发邮件
			if ("1".equals(planStatus)) {
				String plan_title = workPlanUtil.getPlanPeriodDesc(periodType, periodYear, periodMonth, periodWeek) + "工作计划";

				// 发给指定的审批人
				String superid = p07Vo.getString("p0733");
				String superNbase = "";
				String superA0100 = "";
				String superA0101 = "";
				if (superid != null && !"".equals(superid)) {
					superNbase = superid.substring(0, 3);
					superA0100 = superid.substring(3);
					superA0101 = workPlanUtil.getUsrA0101(superNbase, superA0100);
				}
				if (superA0100.length() > 0) {
					String deptName = "";
					if ("2".equals(P0723)) {
						deptName = workPlanUtil.getOrgDesc(objectId) + "的";
					}
					String subject = "    " + getUsrA0101(nbase, a0100) + "发布了" + deptName + plan_title + ",请批准";
					String bodyText = this.getPublishEmail_BodyText(superA0101, deptName, plan_title);
					String href = getRemindEmail_PlanHref(superNbase, superA0100, true, P0723, periodType, periodYear, periodMonth, periodWeek, objectId, deptLeaderId);
					LazyDynaBean emailBean = getEmailBean(superNbase + superA0100, subject, bodyText, href, "去查看计划");
					emailBean.set("bodySubject", "工作计划审批提醒");
					list.add(emailBean);

					// 发送待办
					String pending_title = "";
					if (deptName.length() == 0) {// 个人计划
						pending_title = getUsrA0101(nbase, a0100) + "的" + plan_title;
					} else {
						pending_title = getUsrA0101(nbase, a0100) + "负责部门" + deptName + "的" + plan_title;
					}
					pending_title = pending_title + "(审批)";
					HashMap palnParamMap = new HashMap();
					palnParamMap.put("p0723", P0723);
					palnParamMap.put("periodtype", periodType);
					palnParamMap.put("periodyear", periodYear);
					palnParamMap.put("periodmonth", periodMonth);
					palnParamMap.put("periodweek", periodWeek);
					palnParamMap.put("objectId", objectId);
					palnParamMap.put("dept_leaderid", deptLeaderId);
					palnParamMap.put("planType", "1");
					String pending_url = getPendingPlanUrl(palnParamMap);
					LazyDynaBean pendingBean = new LazyDynaBean();
					pendingBean.set("pending_url", pending_url);
					pendingBean.set("pending_title", pending_title);
					String receiver = workPlanUtil.getUserNameByA0100(superNbase, superA0100);
					// 审批待办 。清除老领导还没有审批的待办
					workPlanUtil.updatePending_approvePlan(plan_id);
					// 发送审批待办
					workPlanUtil.sendPending_publishPlan(this.userView.getUserName(), receiver, pendingBean, plan_id);

				}
			}

			AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userView);
			emailBo.send(list);
			// 发送微信
			workPlanUtil.sendWeixinMessageFromEmail(list);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 获取邮件bean
	 * @param objectId
	 * @param subject
	 * @param bodyText
	 * @param href
	 * @param hrefDesc
	 * @return
	 */
	public LazyDynaBean getEmailBean(String objectId, String subject, String bodyText, String href, String hrefDesc) {
		LazyDynaBean emailBean = new LazyDynaBean();
		emailBean.set("objectId", objectId);
		emailBean.set("subject", subject);
		emailBean.set("bodyText", bodyText);
		emailBean.set("href", href);
		emailBean.set("hrefDesc", hrefDesc);

		return emailBean;
	}
    /**
     * 获取url
     * @param logonNbase
     * @param logonA0100
     * @param bDesignPlan
     * @param P0723
     * @param periodType
     * @param periodYear
     * @param periodMonth
     * @param periodWeek
     * @param objectId
     * @param deptLeaderId
     * @return
     */
    public String getRemindEmail_PlanHref(String logonNbase,String logonA0100,boolean bDesignPlan, String P0723, String periodType, String periodYear,  String periodMonth, 
			String periodWeek, String objectId, String deptLeaderId) {
		HashMap palnParamMap = new HashMap();
		palnParamMap.put("p0723", P0723);
		palnParamMap.put("periodtype", periodType);
		palnParamMap.put("periodyear", periodYear);
		palnParamMap.put("periodmonth", periodMonth);
		palnParamMap.put("periodweek", periodWeek);
		palnParamMap.put("objectId", objectId);
		palnParamMap.put("dept_leaderid", deptLeaderId);

		return getRemindEmail_PlanHref(palnParamMap, logonNbase, logonA0100, bDesignPlan, P0723);
	}
    /**
     * 获取map的值
     * @param paramMap
     * @param paramName
     * @return
     */
	public String getParamValue(HashMap paramMap, String paramName) {
		String value = "";
		if (paramMap.get(paramName) != null) {
			value = (String) paramMap.get(paramName);
		}
		return value;
	}
    /**
     * 获取链接
     * @param planParam
     * @param logonNbase
     * @param logonA0100
     * @param bDesignPlan
     * @param P0723
     * @return
     */
    public String getRemindEmail_PlanHref(HashMap planParam,String logonNbase,String logonA0100,boolean bDesignPlan, String P0723)  {        
        String url = "";
        WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getConn(), this.getUserView());
        LazyDynaBean abean=workPlanUtil.getUserNamePassword(logonNbase , logonA0100);
        if(abean!=null && abean.get("username")!=null)
        {
            String periodtype =getParamValue(planParam,"periodtype");
            String periodyear =getParamValue(planParam,"periodyear");
            String periodmonth =getParamValue(planParam,"periodmonth");
            String periodweek =getParamValue(planParam,"periodweek");
            String p0723 =getParamValue(planParam,"p0723");
            String objectid =getParamValue(planParam,"objectId");
            String dept_leaderid =getParamValue(planParam,"dept_leaderid");
            
            String username=(String)abean.get("username");
            String pwd=(String)abean.get("password");
            url = this.userView.getServerurl()
            +"/workplan/work_plan.do?br_query=link"
            +"&p0723="+WorkPlanUtil.encryption(p0723) 
            +"&objectid="+WorkPlanUtil.encryption(objectid) 
            +"&periodtype="+periodtype
            +"&periodyear="+periodyear
            +"&periodmonth="+periodmonth 
            +"&fromflag=email"
            +"&periodweek="+periodweek
            +"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(
                    PubFunc.convertTo64Base(username+","+pwd))
                    +""; 
            if("2".equals(P0723)){
                url=url+"&deptleader=" +WorkPlanUtil.encryption(dept_leaderid);
            }
            if (!bDesignPlan){
                url=url+"&needcheck=true";   //别人取消关注后 不能再查看           
            }
        }  
        else {
            url = this.userView.getServerurl();
        }
        return url;
        
    }
	 /**
	  * 获取邮件体
	 * @param a0101
	 * @param planOwerName
	 * @param plan_title
	 * @return
	 */
	public String getPublishEmail_BodyText(String a0101,  String planOwerName,String plan_title)  { 
	        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
	        StringBuffer bodytext = new StringBuffer();
	        bodytext.setLength(0);
	        bodytext.append(a0101).append(", 您好！").append("<br />");
	        bodytext.append(getHtmlBlank(4));
	        bodytext.append("    ");
	        bodytext.append(this.userView.getUserFullName());
	        bodytext.append("已经发布了");  
	            			     
	        bodytext.append(planOwerName);
	        bodytext.append(plan_title);
	        bodytext.append("，请查阅并审批他/她的工作计划。");
	        
	        bodytext.append("<br />");
	        bodytext.append("<br />");
	        bodytext.append("<br />");
	        bodytext.append(cur_date);
	        return bodytext.toString();
	    }
	    /**
	     * 获取html
	     * @param num
	     * @return
	     */
	    private String getHtmlBlank(int num) {
	        String str="";
	        for (int i=0;i<num;i++){
	            str=str+"&nbsp;";
	            
	        }
	        return str;
	    }
	    /**
	     * 获取url
	     * @param planParam
	     * @return
	     */
	    public String getPendingPlanUrl(HashMap planParam)  {        
	        String url = "";
	        String periodtype =getParamValue(planParam,"periodtype");
	        String periodyear =getParamValue(planParam,"periodyear");
	        String periodmonth =getParamValue(planParam,"periodmonth");
	        String periodweek =getParamValue(planParam,"periodweek");
	        String p0723 =getParamValue(planParam,"p0723");
	        String objectid =getParamValue(planParam,"objectId");
	        String dept_leaderid =getParamValue(planParam,"dept_leaderid");
	        String planType =getParamValue(planParam,"planType");
	        
	        url = ""
	        +"/workplan/work_plan.do?br_query=link"
	        +"&p0723="+WorkPlanUtil.encryption(p0723) 
	        +"&objectid="+WorkPlanUtil.encryption(objectid) 
	        +"&periodtype="+periodtype
	        +"&periodyear="+periodyear
	        +"&periodmonth="+periodmonth 
	        +"&type="+planType 
	        +"&periodweek="+periodweek   
	        +"&fromflag=hr_create";   
	        if("2".equals(p0723)){
	            url=url+"&deptleader=" +WorkPlanUtil.encryption(dept_leaderid);
	        }
	        return url;
	        
	    }
	    private String getPeopleSqlByE01a1() {
	        String strSql="";
	        DbWizard dw = new DbWizard(this.conn);
	        WorkPlanUtil wpUtil = new WorkPlanUtil(this.conn, this.userView);
	        String [] arrpre=wpUtil.getSelfUserDbs();    
	        //兼职            
	        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
	        /**兼职参数*/
	        String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
	          
	        if( arrpre.length<1 )
	            return "";
	        try{            
	            for (int i=0;i<arrpre.length;i++){                
	                String pre = arrpre[i];                
	                String a01tab=pre+"A01";
	                String pyField = getPinYinFld();
					if(!StringUtils.isEmpty(pyField) && dw.isExistField(a01tab, pyField)) {
						pyField +=" as pinyin,";
					}else {
						pyField = "'' as pinyin,";
					}
					String emailField = getEmailField();
					if(dw.isExistField(a01tab, emailField, false)) {
						emailField = a01tab+"."+emailField+" as email,";// 兼容没有配置邮箱的情况 chent 20171204 modify
					}else {
						emailField="'' as email,";
					}
					//主职
	                String sql="select "+pyField+emailField+" a0100,a0101,E01A1,guidkey,'"+pre+"' as nbase from "+a01tab;
	                //兼职
	                if("true".equals(flag)){
	                    String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
	                    String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
	                    String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
	                    String curPartTab=pre+setid;
	                    if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
	                    {
	                        sql=sql+" union "+"select "+a01tab+"."+pyField+emailField+a01tab+".a0100,"+a01tab+".a0101,"+curPartTab+"."+e01a1_field+" as E01A1,"+a01tab+".guidkey,'"+pre+"' as nbase from "+curPartTab+" left join "+a01tab
	                        +" on "+curPartTab+".a0100="+a01tab+".a0100"
	                        +" where "+appoint_field+"='0'";
	                    }
	                }  
	                
	                if (!"".equals(strSql)){
	                    strSql=strSql+" union ";
	                }
	                strSql=strSql+  sql;
	          
	            }
	            
	        }
	        catch(Exception e){           
	            e.printStackTrace();  
	        }  
	        return strSql;
	    } 
	    /**   
	     * @Title: getUsrA0101   
	     * @Description: 取得人员姓名   
	     * @param @param nbase
	     * @param @param a0100
	     * @param @return 
	     * @return String 
	     * @throws   
	    */
	    public String getUsrA0101(String nbase,String a0100) {
	        String a0101="";
	        if (nbase==null || a0100==null){
	            return "";
	        }
	        RowSet rset=null;
	        String strsql="select * from "+nbase+"A01 where a0100='"+a0100+"'";
	        try{
	        	ContentDAO dao = new ContentDAO(this.getConn());
	            rset=dao.search(strsql);
	            if (rset.next()){
	                a0101= rset.getString("a0101");
	            }
	        }
	        catch(Exception e){           
	            e.printStackTrace();  
	        } finally {
	            PubFunc.closeDbObj(rset);
	        }
	        return a0101;
	    }

    /**
     * 检查考核对象是否已经开始评分
     * @param map
     * @return
     */
    public HashMap checkIsScoring(HashMap map) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        HashMap returnMap = new HashMap();
        String msg = "";
        try{
            String objectids = String.valueOf(map.get("objectids"));
            String planType = (String) map.get("plantype");// 计划类型1：人员 2：团队
            String periodType = (String) map.get("periodtype");// 计划区间1、年度2、半年3、季度4、月份5、周
            String periodYear = (String) map.get("periodyear");// 年份
            String periodMonth = (String) map.get("periodmonth");// 半年：1上半年 2下半年 季度：1:1季度 2：2季度 周：1:1月 2:2月
            String periodWeek = (String) map.get("periodweek");// 第几周

            ArrayList paramList = new ArrayList();
            objectids = (objectids == null) ? "" : objectids;
            String[] arrObjectIds = objectids.split(",");
            boolean bAll = true;
            for (int i = 0; i < arrObjectIds.length; i++) {
                String objectid1 = PubFunc.decrypt(arrObjectIds[i]);
                arrObjectIds[i]=objectid1;
                if (objectid1.length() > 0) {
                    bAll = false;
                    arrObjectIds[i]=objectid1;
                }
            }

            String strsql = getCheckPeopleSql(paramList, arrObjectIds, bAll, planType, periodType, periodYear, periodMonth, periodWeek);
            rs = dao.search(strsql, paramList);
            HashMap<String,List<String>> personPlanMap = new HashMap<String,List<String>>();
            while(rs.next()){
                String plan_id = rs.getString("relate_planid");
                if(StringUtils.isBlank(plan_id)){
                    continue;
                }
                String objectId = "";
                if ("1".equals(planType)) {
                    objectId = rs.getString("a0100");
                } else {
                    objectId = rs.getString("p0707");
                }
                List<String> persons = null;
                if(personPlanMap.containsKey(plan_id)){
                    persons = personPlanMap.get(plan_id);
                    persons.add(objectId);
                }else{
                    persons = new ArrayList<String>();
                    persons.add(objectId);
                }
                personPlanMap.put(plan_id,persons);

            }
           int i=0;
           boolean isEnd = false;
            HashMap<String,List<String>> clearObjMap = new HashMap<String,List<String>>();
           for(Map.Entry<String,List<String>> entry : personPlanMap.entrySet()){
               //查询是否有正在打分的人
               StringBuffer sql = new StringBuffer();
               sql.append("select A0101,object_id from per_object where plan_id=? and  object_id in ");
               sql.append("(select distinct(object_id) from per_mainbody where Plan_id=? and Status in (1,2) and object_id in(");//正在打分|已提交打分
               String plan_id = entry.getKey();
               List<String> persons = entry.getValue();
               if(persons.isEmpty())
                   continue;
               for(String p : persons){
                   sql.append("?,");
               }
               sql.setLength(sql.length()-1);
               sql.append("))");
               List<String> values = new ArrayList<String>();
               values.add(plan_id);
               values.add(plan_id);
               values.addAll(persons);
               rs = dao.search(sql.toString(),values);
               List<String> objectList = null;
               while(rs.next()){
                   String a0101 = rs.getString("A0101");
                   if(clearObjMap.containsKey(plan_id)){
                       objectList = clearObjMap.get(plan_id);
                   }else{
                       objectList = new ArrayList<String>();
                   }
                   objectList.add(PubFunc.encrypt(rs.getString("object_id")));
                   if(i==5){
                       msg+="等";
                   }else if(i<5){
                       if(i>0) {
                           msg+="、";
                       }
                       msg+=a0101;
                   }
               }
               clearObjMap.put(plan_id,objectList);
           }

           if(msg.length()>0){
               msg="人员【"+msg+"】的目标卡有打分记录，继续更新目标卡将清除打分，是否继续？";
           }
            returnMap.put("msg",msg);
           returnMap.put("clearIds",clearObjMap);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return returnMap;
    }

    /**
     * 发送待办并向待办库中加入新的待办
     * @param title
     * @param appealObject_id
     * @param nbase
     * @param url
     */
    public void sendMessageToPT(String title,String appealObject_id,String nbase,String url,String plan_id)
    {
        try
        {
            PendingTask pe = new PendingTask();
            String pendingType="计划打分";
            // 发送待办前先判断是否已给此考核对象或考核主体发送过待办
            LazyDynaBean bean = new LazyDynaBean();
            bean.set("title", title);
            bean.set("url", url);
            bean.set("oper", "start");
            LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.conn, this.userView, nbase+appealObject_id,plan_id,bean,"4");
            if("add".equals(_bean.get("flag"))){
                pe.insertPending("PER"+_bean.get("pending_id"),"P",title,this.userView.getDbname()+this.userView.getA0100(),nbase+appealObject_id,url,0,1,pendingType,this.userView);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * plan_id 计划号
     * 获得某编号的考核计划的所有信息
     */
    public RecordVo getPlanVo(String planid)
    {
        RecordVo vo=new RecordVo("per_plan");
        try
        {
            vo.setInt("plan_id",Integer.parseInt(planid));
            ContentDAO dao = new ContentDAO(this.conn);
            vo=dao.findByPrimaryKey(vo);
            if(vo.getInt("method")==0)
                vo.setInt("method",1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return vo;
    }
}
