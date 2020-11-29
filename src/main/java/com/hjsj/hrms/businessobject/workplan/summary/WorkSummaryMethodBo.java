package com.hjsj.hrms.businessobject.workplan.summary;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *工作总结的公告方法
 * 
 * @author Administrator
 * 
 */
public class WorkSummaryMethodBo {

	private Connection conn;
	private UserView userView;

	private WorkPlanUtil workPlanUtil;
	private WorkPlanBo wp;
	private ContentDAO dao;
	private String superiorFld; // 上级指标 用于多次使用上级指标时
	private String dbNameMsg;	//haosl 20160630	用于存储人员库设置情况的消息

	public String getDbNameMsg() {
		return dbNameMsg;
	}

	public void setDbNameMsg(String dbNameMsg) {
		this.dbNameMsg = dbNameMsg;
	}

	public WorkSummaryMethodBo() {
	}

	public WorkSummaryMethodBo(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
		workPlanUtil = new WorkPlanUtil(conn, userView);
		wp = new WorkPlanBo(conn, userView);
		dao = new ContentDAO(this.conn);
	}

	/**
	 * 根据条件查询一条工作总结
	 * 
	 * @param nbase
	 * @param a0100
	 * @param p014
	 * @param state
	 * @return
	 */
	public LazyDynaBean findWorkSummary(String nbase, String a0100, String p014, String state) {
		LazyDynaBean bean = null;

		StringBuffer bufferSql = new StringBuffer();
		bufferSql.append("select P0100,P0109,P0111,P0120,P0113，P0115 ");
		bufferSql.append("from p01 where nbase='" + nbase + "' ");
		bufferSql.append("and A0100='" + a0100 + "' ");
		bufferSql.append("and P0104='" + p014 + "' ");
		bufferSql.append("and state='" + state + "'");

		RowSet findResult = null;

		try {
			findResult = dao.search(bufferSql.toString());
			if (findResult.next()) {
				bean = new LazyDynaBean();
				bean.set("P0100", findResult.getString("P0100"));
				bean.set("P0109", findResult.getString("P0109"));
				bean.set("P0111", findResult.getString("P0111"));
				bean.set("P0120", findResult.getString("P0120"));
				bean.set("P0113", findResult.getString("P0113"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			WorkPlanUtil.closeDBResource(findResult);
		}

		return bean;
	}

	/**
	 * 添加工作总结
	 * 
	 * @param performance
	 *            本周工作总结
	 * @param nextWorkPlan
	 *            下周工作计划
	 * @param workSummaryType
	 *            日报/周报/月报
	 * @param startTime
	 *            工作总结开始时间
	 * @param endTime
	 *            工作总结结束时间
	 * @param whichWeek
	 *            周期数
	 * @param saveState
	 *            审批状态
	 * @param submitDate
	 *            提交时间
	 * @param scope
	 *            可见范围
	 * @param e0122
	 *            部门编号
	 * @param belong_type
	 *            所属类型 0 or null个人；2 部门
	 * @return
	 */
	public String addWorkSummary(String nbase, String a0100, String workSummaryType, String startTime, String endTime, int whichWeek, String saveState, String submitDate, int scope, String e0122,
			String e0a01,String belong_type) {

		String b0110 = "";
		String a0101 = "";

		PlanTaskBo planTaskBo = new PlanTaskBo(conn, userView);
		RecordVo p01Vo = new RecordVo("p01");

		IDGenerator idg = new IDGenerator(2, this.conn);
		String pid = "";
		try {
			RecordVo a01Vo = planTaskBo.getPersonByObjectId(nbase + a0100);
			b0110 = a01Vo.getString("b0110");
			a0101 = a01Vo.getString("a0101");

			pid = idg.getId("P01.P0100");
			p01Vo.setString("p0100", pid);
			p01Vo.setString("a0100", a0100);
			p01Vo.setString("nbase", nbase);
			p01Vo.setString("b0110", b0110);
			p01Vo.setString("e0122", e0122);
			p01Vo.setString("a0101", a0101);
			//上级岗位
			String supere01a1 = workPlanUtil.getSuperE01a1s(e0a01);
			p01Vo.setString("e01a1", e0a01);
			p01Vo.setString("supere01a1", supere01a1);
			p01Vo.setString("p0115", saveState);
			p01Vo.setString("state", workSummaryType);// 0:日报、1：周报、
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			p01Vo.setDate("p0104", format.parse(startTime));// 开始时间
			p01Vo.setDate("p0106", format.parse(endTime));// 结束时间
			p01Vo.setDate("p0114", format.parse(submitDate));// 提交时间
			p01Vo.setString("time", whichWeek + "");// 当月第几周
			p01Vo.setString("p0109", "");
			p01Vo.setString("p0120", "");
			p01Vo.setString("score", "-1");

			p01Vo.setString("belong_type", belong_type);// 部门报
			p01Vo.setInt("scope", scope);// 可见范围

			dao.addValueObject(p01Vo);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return pid;
	}

    /**   
     * @Title: getPeopleP0100   
     * @Description: 返回某人或部门总结id   
     * @param @param nbase
     * @param @param a0100
     * @param @param e0122
     * @param @param summaryCycle
     * @param @param p0104
     * @param @param p0106
     * @param @param belong_type
     * @param @return 
     * @return int 
     * @author:wangrd   
     * @throws   
    */
    public int getPeopleP0100(String nbase,String a0100,String e0122,
            String summaryCycle,String p0104,String p0106,String belong_type) { 
        int plan_id=0;
        try{           
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT p0100,P0113,p0109,p0114,p0115,p0120,score,scope");
            sql.append(" FROM P01 ");
            sql.append(" WHERE state=" + summaryCycle);
            sql.append(" AND P0104=" + Sql_switcher.dateValue(p0104));
            sql.append(" AND P0106=" + Sql_switcher.dateValue(p0106));

          
           if ("2".equals(belong_type)) { //部门
                sql.append(" AND e0122='" + e0122 + "'");
                sql.append(" AND belong_type=2");
            } else  {
                sql.append(" AND nbase='" + nbase + "'");
                sql.append(" AND a0100='" + a0100 + "'");
                sql.append(" AND (belong_type is null or belong_type=0)");
            }

            try {
                RowSet rset = dao.search(sql.toString());
                if (rset.next()) {
                    plan_id= rset.getInt("p0100");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }     
        return plan_id;
    } 
	
	/**
	 * 工作总结添加
	 * 
	 * @param bean
	 * @return
	 */
	public String addWorkSummary(LazyDynaBean bean) {
	    //检查是否存在此人的总结？wangrd 2015-03-30
	    int p0100=getPeopleP0100(userView.getDbname(),userView.getA0100(),(String) bean.get("e0122"),
	            (String) bean.get("state"),(String) bean.get("p0104"),
	            (String) bean.get("p0106"),(String) bean.get("belong_type"));
	    if (p0100>0){
	        return String.valueOf(p0100);
	    }
		RecordVo p01Vo = new RecordVo("p01");
		IDGenerator idg = new IDGenerator(2, this.conn);

		String pid = "";
		try {
			pid = idg.getId("P01.P0100");
			pid = String.valueOf(Integer.parseInt(pid));
			p01Vo.setInt("p0100", Integer.parseInt(pid));
			p01Vo.setString("a0100", userView.getA0100());
			p01Vo.setString("nbase", userView.getDbname());
			p01Vo.setString("b0110", userView.getUserOrgId());
			p01Vo.setString("e0122", (String) bean.get("e0122"));
			p01Vo.setString("a0101", userView.getUserFullName());
			p01Vo.setString("e01a1", (String) bean.get("b01ps"));
			//上级岗位
			String supere01a1 = workPlanUtil.getSuperE01a1s( (String) bean.get("b01ps"));
	
			p01Vo.setString("supere01a1",supere01a1);
			p01Vo.setString("p0115", (String) bean.get("p0115"));
			p01Vo.setInt("state", Integer.parseInt(bean.get("state").toString()));// 0:日报、1：周报、
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			p01Vo.setDate("p0104", df.parse((String) bean.get("p0104")));// 开始时间
			p01Vo.setDate("p0106", df.parse((String) bean.get("p0106")));// 结束时间
			p01Vo.setDate("p0114", df2.parse((String) bean.get("p0114")));// 提交时间
			p01Vo.setInt("time", Integer.parseInt(bean.get("time").toString()));// 当月第几周
			p01Vo.setInt("score", Integer.parseInt(bean.get("score").toString()));
			p01Vo.setString("p0109", (String) bean.get("p0109"));
			p01Vo.setString("p0120", (String) bean.get("p0120"));
			p01Vo.setInt("belong_type", Integer.parseInt(bean.get("belong_type").toString()));// 部门报
			p01Vo.setInt("scope", Integer.parseInt(bean.get("scope").toString()));// 可见范围

			dao.addValueObject(p01Vo);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pid;
	}

	/**
	 * 修改，工作总结，下周计划
	 * 
	 * @param p0100
	 * @param performance
	 * @param nextWorkPlan
	 * @param e0122 
	 * @return
	 */
	public boolean updateWorkSummary(String p0100, String e0122,String e0a01,  String performance, String nextWorkPlan, int scope) {

		StringBuffer updateSql = new StringBuffer();
		updateSql.append("update p01");
		updateSql.append(" set p0109=?,p0120=?,scope=?,a0100=?,nbase=?,b0110=?,e0122=?,e01a1=?,supere01a1=?,a0101=?");
		updateSql.append(" where p0100=?");
		if (e0122 != null && e0122.trim().length() > 0) {
            e0122 = WorkPlanUtil.decryption(e0122);
        }else {
			e0122 = userView.getUserDeptId();
		}
		if (e0a01 != null && e0a01.trim().length() > 0) {
			e0a01 = WorkPlanUtil.decryption(e0a01);
		}else {
			e0a01 =  userView.getUserPosId();
		}
		ArrayList params = new ArrayList();
		params.add(performance);
		params.add(nextWorkPlan);
		params.add(Integer.valueOf(scope));
		params.add(userView.getA0100());
		params.add(userView.getDbname());
		params.add(userView.getUserOrgId());
		params.add(e0122);
		params.add(e0a01);
		//上级岗位
		String supere01a1 = workPlanUtil.getSuperE01a1s(e0a01);
		params.add(supere01a1);
		params.add(userView.getUserFullName());
		params.add(p0100);

		try {
			int resultRow = dao.update(updateSql.toString(), params);
			return resultRow > 0;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 编辑，真正修改的时候，需要修改审批状态，p0115='01'(起草)
	 * 
	 * @param p0100
	 * @return
	 */
	public LazyDynaBean editWorkSummary(String p0100) {

		LazyDynaBean bean = new LazyDynaBean();
		RowSet rs = null;
		StringBuffer updateSignSql = new StringBuffer();
		updateSignSql.append("update P01 set p0115='01' where P0100 =" + p0100);

		try {
			// 发布状态改为起草状态
			int resultRow = dao.update(updateSignSql.toString());
			if (resultRow > 0) {
				String sql = "select p0100,p0109,p0115,p0120,scope from p01 where p0100=" + p0100;
				rs = dao.search(sql);
				while (rs.next()) {
					bean.set("P0100", rs.getString("P0100"));
					bean.set("P0109", rs.getString("P0109"));
					bean.set("P0115", rs.getString("P0115"));
					bean.set("P0120", rs.getString("P0120"));
					bean.set("scope", rs.getString("scope"));
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			WorkPlanUtil.closeDBResource(rs);
		}
		return bean;
	}

	/**
	 * 发布
	 * 
	 * @param workSummaryID
	 * @param e0122 
	 * @param saveState 
	 * @return
	 */
	public String publishWorkSummary(String workSummaryID, String e0122,String e0a01, String performance, String nextWorkPlan, int scope, String saveState) {
		StringBuffer bufferSql = new StringBuffer();
		List bufferList = new ArrayList();
		
		bufferSql.append("update P01 set P0114="+Sql_switcher.sqlNow()+",p0115='"+saveState+"',");
		//直接拼接，字符串太长的话，oracle会报错，采用占位符方式
		bufferSql.append("p0109=?,");//'" + performance + "'
		bufferSql.append("p0120 =? ,");//'" + nextWorkPlan + "'
		
		bufferList.add(performance);
		bufferList.add(nextWorkPlan);
		
		bufferSql.append("nbase = '" + userView.getDbname() + "',");
		bufferSql.append("a0100 = '" + userView.getA0100() + "',");
		if (e0122 != null && e0122.trim().length() > 0) {
            e0122 = WorkPlanUtil.decryption(e0122);
        }else {
			e0122 = userView.getUserDeptId();
		}
		if (e0a01 != null && e0a01.trim().length() > 0) {
			e0a01 = WorkPlanUtil.decryption(e0a01);
		}else {
			e0a01 =  userView.getUserPosId();
		}
		//上级岗位
		String supere01a1 = workPlanUtil.getSuperE01a1s(e0a01);
		bufferSql.append("supere01a1 = '" + supere01a1 + "',");
		bufferSql.append("e0122 = '" + e0122 + "',");
		bufferSql.append("b0110 = '" + userView.getUserOrgId() + "',");
		bufferSql.append("e01a1 = '" +e0a01 + "',");
		bufferSql.append("a0101 = '" + userView.getUserFullName() + "',");
		bufferSql.append("scope = " + scope);
		bufferSql.append(" where P0100=" + Integer.parseInt(workSummaryID));

		RowSet rs = null;
		try {
			int editCount = dao.update(bufferSql.toString(),bufferList);
			if (editCount > 0) {
				String sql = "select p0114 from P01 where p0100=" + workSummaryID;
				rs = dao.search(sql);
				if (rs.next()) {
					Date d = rs.getDate("p0114");
					if(Sql_switcher.searchDbServer() == 2){
						d = new Date(rs.getTimestamp("p0114").getTime());
					}
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					return df.format(d);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			WorkPlanUtil.closeDBResource(rs);
		}
		return null;
	}

	/**
	 * 领导打分
	 * 
	 * @param p0100
	 * @param score
	 * @param comment
	 * @return
	 */
	public boolean leaderGrade(String p0100, String score, String comment) {
		StringBuffer gradeSql = new StringBuffer();
		gradeSql.append("update P01 set P0113='");
		gradeSql.append(comment + "',");
		gradeSql.append("score=");
		gradeSql.append(score);
		gradeSql.append(" where P0100 =" + p0100);

		RowSet rs = null;
		try {
			int gradeCount = dao.update(gradeSql.toString());
			if (gradeCount > 0) {
				return true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			WorkPlanUtil.closeDBResource(rs);
		}
		return false;
	}

	// 部门自动汇总,查询该期间内，该部门员工的工作总结
	public List collectDepartWorkSummary(String e0122, String startTime, String endTime, String state) {
		ArrayList departWorkSummaryList = new ArrayList();

		StringBuffer collectDepartSql = new StringBuffer("select P0100,P0109　from p01");
		collectDepartSql.append(" where e0122 = '" + e0122 + "'");
		collectDepartSql.append(" and P0104 < '");
		collectDepartSql.append(Sql_switcher.dateValue(endTime) + "'");
		collectDepartSql.append(" and P0106 > '");
		collectDepartSql.append(Sql_switcher.dateValue(startTime) + "'");
		if (state != null) {
            collectDepartSql.append(" and state =" + state);
        }

		LazyDynaBean departWorkSummaryBean = null;
		RowSet departWorkSummaryResult = null;
		try {
			departWorkSummaryResult = dao.search(collectDepartSql.toString());
			while (departWorkSummaryResult.next()) {
				departWorkSummaryBean = new LazyDynaBean();
				departWorkSummaryBean.set("P0100", departWorkSummaryResult.getString("P0100"));
				departWorkSummaryBean.set("P0109", departWorkSummaryResult.getString("P0109"));

				departWorkSummaryList.add(departWorkSummaryBean);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			WorkPlanUtil.closeDBResource(departWorkSummaryResult);
		}

		return departWorkSummaryList;
	}

	private void initSuperiorFld() {
		if (superiorFld == null || "".equals(superiorFld)) {
			RecordVo ps_superior_vo = ConstantParamter.getRealConstantVo("PS_SUPERIOR", this.conn);
			if (ps_superior_vo != null) {
                superiorFld = ps_superior_vo.getString("str_value");
            }
		}
	}

	/**
	 * 获取p01表的条件
	 * 
	 * @param p0104
	 *            期间开始时间
	 * @param p0106
	 *            结束时间
	 * @param state
	 *            周报/月报/年报
	 * @return
	 */
	private String getP01Where(String p0104, String p0106, String state) {
		String sql = "belong_type = 0 and p0104=" + Sql_switcher.dateValue(p0104) + " and p0106=" + Sql_switcher.dateValue(p0106) + " and p01.state=" + state;
		return sql;
	}
	
	public String getTeamPeopleSql(String e01a1, boolean includeTransferE01a1, String p0104, String p0106, String state) {
		boolean bFindHistory=true;
        String [] arrpre=workPlanUtil.getSelfUserDbs();    
        if( arrpre.length<1 ) {
            return "";
        }
        String strSql="";
        try{ 
        	for (int i=0;i<arrpre.length;i++){                
	                String pre = arrpre[i];
	                String a01tab=pre+"A01";
	                if (bFindHistory) {// 查找目前在岗的下级成员
						String sql="select a0100 from "+a01tab+" where e01a1='"+e01a1+"'";
						// 排除 在当前期间制定计划时岗位不属于我的团队 由其他部门调来的
						if (includeTransferE01a1) {
							sql = sql + " and a0100 not in  (";
							sql = sql + " select a0100 from p01,K01 where p01.E01A1 = K01.E01A1"
							      +" and k01.e01a1='"+e01a1+"'";
							sql = sql + " and p01.nbase ='" + pre + "' and " + this.getP01Where(p0104, p0106, state);

							sql = sql + ")";
						}

						// 加上 在当前期间制定计划时属于我的团队，从我部门调走的
						if (includeTransferE01a1) {
							//按自己的部门
							sql = sql + " union select a0100 from p01,K01   where p01.E01A1 = K01.E01A1"  
							      +" and k01.e01a1='"+e01a1+"'";
							sql = sql + "and nbase ='" + pre + "' and " + this.getP01Where(p0104, p0106, state);
							
							 //按上级部门 supere01a1
	                        sql = sql + " union select a0100 from p01 "
	                            +" where p01.superE01a1 ='"+e01a1+"'";
	                        sql=sql+" and nbase ='"+pre+"'" +" and "+this.getP01Where(p0104, p0106, state);   
	                     
						}

						if (!"".equals(strSql)) {
							strSql = strSql + " union ";
						}

						strSql = strSql + "select A0100,a0101,b0110,e0122,e01a1,'" + pre + "' as nbase  from  " + a01tab + " where a0100 in (select A.a0100 from (" + sql + ") A )";
	                	
	                }
                }
          }
       catch (Exception e) {
        	  e.printStackTrace();
		 }
		return strSql;
	}
	
	
	public String getTeamPeopleSql(String nbase, String a0100, boolean includeTransferE01a1, String p0104, String p0106, String state) {
		String ps_superior = "";

		boolean bFindHistory = true;
		// 本人
		if ((this.userView.getDbname().equals(nbase)) && (this.userView.getA0100().equals(a0100))) {
			bFindHistory = false;
		}
		// 我的下级
		if (workPlanUtil.isMyTeamPeople(nbase, a0100)) {
			bFindHistory = false;
		}
		String historyE01a1 = "";
		if (bFindHistory) {
			String sql = "select e01a1 from p01 where nbase='" + nbase + "' and a0100='" + a0100 + "'  and " + this.getP01Where(p0104, p0106, state);

			try {
				RowSet rset = dao.search(sql);
				if (rset.next()) {
					historyE01a1 = rset.getString("e01a1");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		RecordVo ps_superior_vo = ConstantParamter.getRealConstantVo("PS_SUPERIOR", this.conn);
		if (ps_superior_vo != null) {
			ps_superior = ps_superior_vo.getString("str_value");
		}
		String strSql = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
		String strpres = "";
		if (login_vo != null) {
            strpres = login_vo.getString("str_value");
        }
		String[] arrpre = strpres.split(",");

		// 兼职
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
		/** 兼职参数 */
		String flag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "flag");
		String setid = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "setid");
		String e01a1_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
		String appoint_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");

		if (arrpre.length < 1) {
            return "";
        }

		try {
			for (int i = 0; i < arrpre.length; i++) {
				String pre = arrpre[i];
				String cura01tab = nbase + "A01";
				String a01tab = pre + "A01";
				if (!bFindHistory) {// 查找目前在岗的下级成员
					// 主职
					String sql = "select " + a01tab + ".a0100 from " + a01tab + ",K01  where " + a01tab + ".E01A1 = K01.E01A1  and (exists ( select 1 from " + cura01tab + " where a0100='"
							+ a0100 + "' and e01a1 =K01." + ps_superior + ")  ";
					// 兼职
					if ("true".equals(flag)) {
						String curPartTab = nbase + setid;
						if (!(("".equals(setid)) || ("".equals(e01a1_field)) || "".equals(appoint_field))) {
							sql = sql + " or exists (select 1 from " + curPartTab + " where a0100='" + a0100 + "' and " + e01a1_field + " =K01." + ps_superior + " and " + appoint_field + "=0)";
						}

					}
					sql = sql + ")";
					// 排除 在当前期间制定计划时岗位不属于我的团队 由其他部门调来的
					if (includeTransferE01a1) {
						sql = sql + " and a0100 not in  (";
						sql = sql + " select a0100 from p01,K01   where p01.E01A1 = K01.E01A1  and ( not exists ( select 1 from " + cura01tab + " where a0100='" + a0100
								+ "' and e01a1 =K01." + ps_superior + ")  ";
						// 兼职
						if ("true".equals(flag)) {
							String curPartTab = nbase + setid;
							if (!(("".equals(setid)) || ("".equals(e01a1_field)) || "".equals(appoint_field))) {
								sql = sql + " and not exists (select 1 from " + curPartTab + " where a0100='" + a0100 + "' and " + e01a1_field + " =K01." + ps_superior + " and " + appoint_field+ "='0')";
							}
						}
						
						sql = sql + ") and p01.nbase ='" + pre + "' and " + this.getP01Where(p0104, p0106, state);

						sql = sql + ")";
					}

					// 加上 在当前期间制定计划时属于我的团队，从我部门调走的
					if (includeTransferE01a1) {
						sql = sql + " union select a0100 from p01,K01   where p01.E01A1 = K01.E01A1  and (exists ( select 1 from " + cura01tab + " where a0100='" + a0100
								+ "' and e01a1 =K01." + ps_superior + ")  ";
						// 兼职
						if ("true".equals(flag)) {
							String curPartTab = nbase + setid;
							if (!(("".equals(setid)) || ("".equals(e01a1_field)) || "".equals(appoint_field))) {
								sql = sql + " or exists (sele" +
										"ct 1 from " + curPartTab + " where a0100='" + a0100 + "' and " + e01a1_field + " =K01." + ps_superior + " and " + appoint_field + "='0')";
							}
						}
						sql = sql + ") and nbase ='" + pre + "' and " + this.getP01Where(p0104, p0106, state);
						
						   //按上级部门 supere01a1
                        sql = sql + " union select a0100 from p01 where ( p01.superE01a1 in (select e01a1 from "+cura01tab+" where a0100='"+a0100+"') ";
                        
                            //兼职
                        if("true".equals(flag)){
                            String curPartTab=nbase+setid;
                            if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
                            {
                                sql=sql+" or  p01.superE01a1 in  (select "+e01a1_field+" from "+curPartTab+" where a0100='"+a0100+"'"+" and "+appoint_field+"='0')";           
                            }
                        }  
                        sql=sql+") and nbase ='"+pre+"'" +" and "+this.getP01Where(p0104, p0106, state);   
                     
					}

					if (!"".equals(strSql)) {
						strSql = strSql + " union ";
					}

					strSql = strSql + "select A0100,a0101,b0110,e0122,e01a1,'" + pre + "' as nbase  from  " + a01tab + " where a0100 in (select A.a0100 from (" + sql + ") A )";
				}
				else {// 查找历史中的下级成员
					// 主岗位的下级成员
					String sql = "  select a0100 from p01,K01  where p01.E01A1 = K01.E01A1 and K01." + ps_superior + "='" + historyE01a1 + "' and p01.nbase ='" + pre + "' and "+ this.getP01Where(p0104, p0106, state);

					if (!"".equals(strSql)) {
						strSql = strSql + " union ";
					}
					strSql = strSql + "select A0100,a0101,b0110,e0122,e01a1,'" + pre + "' as nbase from  " + a01tab + " where a0100 in (select A.a0100 from (" + sql + ") A )";
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return strSql;
	}

	// 团队
	public String getTeamPeopleSql(String nbase, String a0100, boolean includeTransferE01a1) {
		String ps_superior = "";

		RecordVo ps_superior_vo = ConstantParamter.getRealConstantVo("PS_SUPERIOR", this.conn);
		if (ps_superior_vo != null) {
			ps_superior = ps_superior_vo.getString("str_value");
		}

		String strSql = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
		String strpres = "";
		if (login_vo != null) {
            strpres = login_vo.getString("str_value");
        }
		String[] arrpre = strpres.split(",");

		// 兼职
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
		/** 兼职参数 */
		String flag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "flag");

		if (arrpre.length < 1) {
            return "";
        }

		try {
			for (int i = 0; i < arrpre.length; i++) {
				String pre = arrpre[i];
				String cura01tab = nbase + "A01";
				String a01tab = pre + "A01";
				// 主职
				String sql = "select " + a01tab + ".a0100 from " + a01tab + ",K01  where " + a01tab + ".E01A1 = K01.E01A1  and (exists ( select 1 from " + cura01tab + " where a0100='"
						+ a0100 + "' and e01a1 =K01." + ps_superior + ")  ";
				// 兼职
				if ("true".equals(flag)) {
					String setid = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "setid");
					String e01a1_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
					String appoint_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");
					String curPartTab = nbase + setid;
					if (!(("".equals(setid)) || ("".equals(e01a1_field)) || "".equals(appoint_field))) {
						sql = sql + " or exists (select 1 from " + curPartTab + " where a0100='" + a0100 + "' and " + e01a1_field + " =K01." + ps_superior + " and " + appoint_field + "=0)";
					}

				}
				sql = sql + ")";
				if (includeTransferE01a1) {
					sql = sql + " union select a0100 from p01,K01   where p01.E01A1 = K01.E01A1  and (exists ( select 1 from " + cura01tab + " where a0100='" + a0100 + "' and e01a1 =K01."
							+ ps_superior + ")  ";
					// 兼职
					if ("true".equals(flag)) {
						String setid = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "setid");
						String e01a1_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
						String appoint_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");
						String curPartTab = nbase + setid;
						if (!(("".equals(setid)) || ("".equals(e01a1_field)) || "".equals(appoint_field))) {
							sql = sql + " or exists (select 1 from " + curPartTab + " where a0100='" + a0100 + "' and " + e01a1_field + " =K01." + ps_superior + " and " + appoint_field + "=0)";
						}

					}
					sql = sql + ")";
					sql = sql + " and nbase ='" + pre + "'";
				}

				if (!"".equals(strSql)) {
					strSql = strSql + " union ";
				}

				strSql = strSql + "select A0100,a0101,b0110,e0122,e01a1, '" + pre + "' as nbase   from  " + a01tab + " where a0100 in (select A.a0100 from (" + sql + ") A )";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return strSql;
	}

	/**
	 * @param stateSign
	 * @param para
	 * @Title: getMySubDeptMainInfo
	 * @Description: 下属部门主界面展现
	 * @param @param e01a1list
	 * @param @return
	 * @return String
	 * @author:szk
	 * @throws
	 */
	public ArrayList getMySubDeptPerson(ArrayList e01a1list, String startTime, String endTime, String state, String stateSign, boolean isHR) {
		ArrayList list = new ArrayList();

		String tablename = "";
		String e01a1s = "";
		StringBuffer strsql = new StringBuffer();

		initSuperiorFld();
		if (superiorFld == null || "".equals(superiorFld)) {
			return list;
		}
		superiorFld = "k01." + superiorFld;
		if (isHR) {
			superiorFld = "k01.e01a1";
		}
		if (e01a1list.size() < 1) {
			return list;
		}

		for (int i = 0; i < e01a1list.size(); i++) {
			LazyDynaBean e01abean = (LazyDynaBean) e01a1list.get(i);
			String e01a1 = (String) e01abean.get("e01a1");
			if ("".equals(e01a1)) {
                continue;
            }
			if(e01a1s.contains(e01a1)){
			    continue;
            }
			if (e01a1s.length() > 0) {
                e01a1s = e01a1s + ",'" + e01a1 + "'";
            } else {
                e01a1s = e01a1s + "'" + e01a1 + "'";
            }
		}

		try {
			// 有负责部门的岗位。
			e01a1s = cutString(e01a1s);
			tablename = "select k01.e01a1,b01.b0110,O.codeitemdesc from k01,B01,organization O   where k01.e01a1=b01." + WorkPlanConstant.DEPTlEADERFld + " and B01.b0110=O.codeitemid  and ";
            //查询机构的时候需要把历史机构(不在有效期内的机构)给过滤掉 haosl 2019-4-17

            if (WorkPlanConstant.SummaryCycle.YEAR.equals(state)) {
                String year = startTime.split("-")[0];
                tablename+="('"+year+"'>="+Sql_switcher.dateToChar("O.start_date","yyyy");
                tablename+=" and '"+year+"'<="+Sql_switcher.dateToChar("O.end_date","yyyy")+")";
            } else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(state)
                    || WorkPlanConstant.SummaryCycle.QUARTER.equals(state)) {
                String temp =startTime.split("-")[0]+"-"+startTime.split("-")[1];
                String temp2 = endTime.split("-")[0]+"-"+endTime.split("-")[1];
                tablename+="(";
                tablename += "('"+temp+"'>="+Sql_switcher.dateToChar("O.start_date","yyyy-MM")+" and '"+temp+"'<="+Sql_switcher.dateToChar("O.end_date","yyyy-MM");
                tablename += ") or ('"+temp2+"'>="+Sql_switcher.dateToChar("O.start_date","yyyy-MM")+" and '"+temp2+"'<="+Sql_switcher.dateToChar("O.end_date","yyyy-MM");
                tablename += ") or ('"+temp+"'<="+Sql_switcher.dateToChar("O.start_date","yyyy-MM")+" and '"+temp2+"'>="+Sql_switcher.dateToChar("O.end_date","yyyy-MM");
                tablename += "))";
            }  else if (WorkPlanConstant.SummaryCycle.MONTH.equals(state)) {
                String temp =startTime.split("-")[0]+"-"+startTime.split("-")[1];
                tablename+="('"+temp+"'>="+Sql_switcher.dateToChar("O.start_date","yyyy-MM");
                tablename+=" and '"+temp+"'<="+Sql_switcher.dateToChar("O.end_date","yyyy-MM")+")";
            } else if (WorkPlanConstant.SummaryCycle.WEEK.equals(state)) {
                String temp =startTime.split("-")[0]+"-"+startTime.split("-")[1];
                String temp2 = endTime.split("-")[0]+"-"+endTime.split("-")[1];
                tablename+="(";
                tablename += "('"+temp+"'>="+Sql_switcher.dateToChar("O.start_date","yyyy-MM-dd")+" and '"+temp+"'<="+Sql_switcher.dateToChar("O.end_date","yyyy-MM-dd");
                tablename += ") or ('"+temp2+"'>="+Sql_switcher.dateToChar("O.start_date","yyyy-MM-dd")+" and '"+temp2+"'<="+Sql_switcher.dateToChar("O.end_date","yyyy-MM-dd");
                tablename += ") or ('"+temp+"'<="+Sql_switcher.dateToChar("O.start_date","yyyy-MM-dd")+" and '"+temp2+"'>="+Sql_switcher.dateToChar("O.end_date","yyyy-MM-dd");
                tablename += "))";
            }

            tablename+=" and "+superiorFld + " in (" + e01a1s + ") ";
			tablename="(" + tablename + ") a";

			// 关联p01
			strsql.append("select a.B0110,a.codeitemdesc,"); // a.nbase,a.A0100,a.A0101,
			strsql.append(" a.E01A1 ,p.P0115,p.score,p.P0100,p.P0109,p.p0120"); // o2.codeitemdesc
			// E0122,
			strsql.append(" from " + tablename);
			strsql.append(" left join organization o3 on a.E01A1 = o3.codeitemid");
			strsql.append(" left join P01 p");
			strsql.append(" on p.e0122 = a.b0110");
			strsql.append(" and p.P0104=" + Sql_switcher.dateValue(startTime));
			strsql.append(" and p.P0106=" + Sql_switcher.dateValue(endTime));
			strsql.append(" and p.belong_type = 2 and p.state=" + state);

			// if ("p011501".equalsIgnoreCase(stateSign)) {
			// // 未提交
			// strsql = " and (P0115 !='03' or P0115 is NULL)";
			// } else if ("p011503".equalsIgnoreCase(stateSign)) {
			// // 已提交
			// strsql = " and P0115 ='03'";
			// } else if ("score".equalsIgnoreCase(stateSign)) {
			// // 以打分
			// strsql = " and score > -1";
			// }
			if ("p011501".equalsIgnoreCase(stateSign)) {
				// 未提交
				strsql.append(" where (P0115 ='01' or P0115 is NULL)");
			}
			else if ("p011503".equalsIgnoreCase(stateSign)) {
				// 已提交
				strsql.append(" where  P0115 in ('02', '03')");
			}
			else if ("commit".equalsIgnoreCase(stateSign)) {
				// 已提交
				strsql.append(" where P0115 in('02','03')");
			}
			else if ("ratified".equals(stateSign)){
				strsql.append(" where P0115 = '02'");
			}
			else if ("score".equalsIgnoreCase(stateSign)) {

				if (isHR) {
                    strsql.append(" where  P0115 ='03'"); // 已批准
                } else {
                    strsql.append(" where  score > -1 and P0115 ='03'"); // 以打分
                }
			}
			else if ("hrscoreremind".equalsIgnoreCase(stateSign)) {
				strsql.append(" where  score = -1 and (P0115 ='02' or P0115 ='03')"); // 未打分
			}

			RowSet rset = dao.search(strsql.toString());
			strsql.setLength(0);
			while (rset.next()) {
				HashMap map = new HashMap();
				String p0115 = rset.getString("p0115");
				String e0122 = rset.getString("b0110");
				String e0122desc = rset.getString("codeitemdesc");
				String e01a1 = rset.getString("e01a1");
				String score = rset.getString("score");
				String nbase = "";
				String a0100 = "";
				String a0101 = "";
				String haveleader = "no"; // 是否有负责人，no:没有
				tablename = workPlanUtil.getPeopleSqlByE01a1(e01a1);

				if (!"".equals(tablename)) {
					tablename = "(" + tablename + ") T";
					strsql.append("select T.* from " + tablename);
					RowSet rset1 = dao.search(strsql.toString());
					strsql.setLength(0);
					if (rset1.next()) {// 有人负责

						nbase = rset1.getString("nbase");
						a0100 = rset1.getString("a0100");
						a0101 = rset1.getString("a0101");
						haveleader = "yes";
					}
				}

				map.put("nbase", WorkPlanUtil.encryption(nbase));
				map.put("a0100", WorkPlanUtil.encryption(a0100));
				map.put("nbaseA0100", WorkPlanUtil.encryption(nbase + a0100));
				map.put("a0101", a0101);
				map.put("e0122", WorkPlanUtil.encryption(e0122));
				map.put("haveleader", haveleader);

				map.put("e0122desc", e0122desc);
				map.put("e01a1", e01a1);
				map.put("p0115", p0115);
				map.put("p0109", rset.getString("p0109"));
				map.put("p0120", rset.getString("p0120"));
				map.put("score", score);
				String photoUrl = wp.getPhotoPath(nbase, a0100);
				map.put("photoUrl", photoUrl);

				list.add(map);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取团队成员的 sql
	 * 
	 * @param nbase
	 * @param a0100
	 * @param startTime
	 * @param endTime
	 * @param state
	 *            工作总结标志，（周报，月报，季报，年报等）
	 * @param stateSign
	 *            状态标志，（以提交，未提交，以打分）
	 * @param para
	 * @param commonpara
	 * @param isHR
	 * @return
	 * @throws GeneralException
	 */
	public StringBuffer getSelectTeamWeeklySQL(String nbase, String a0100, String startTime, String endTime, String state, String stateSign, String para, String commonpara, boolean isHR, String e01a1)
			throws GeneralException {

		String A01Table = "";
		StringBuffer sql = new StringBuffer();
		try{
			if (isHR) {
                A01Table = this.getPersonScope(para, commonpara);
            } else if (e01a1 != null && e01a1.trim().length() > 0){ //汇总个人总结时用
				initSuperiorFld();
				if (superiorFld == null || "".equals(superiorFld)) {
					return new StringBuffer();
				}else{
					A01Table = "select a.a0100,a0101,a.b0110,a.e0122,a.e01a1,'"+nbase+"' as nbase from "+nbase+"A01 a,K01 k where a.E01A1 = k.E01A1 and k."+superiorFld+" ='"+e01a1+"'";
				}
			}else {
                A01Table = this.getTeamPeopleSql(nbase, a0100, true, startTime, endTime, state);
            }
			//修改  haosl	20160629
			if(A01Table == null|| "".equals(A01Table)){
				return new StringBuffer();	
			}
			A01Table = "(" + A01Table + ") a";
			sql.append("select * from (");
			sql.append("select a.nbase,a.A0100,a.A0101,a.B0110,a.E0122,a.E01A1 ,p.P0115,p.score,p.P0100,p.P0109,p.P0120,p.E0122 as p01e0122,p.e01a1 as p01e01a1");
			if (isHR) {
                sql.append(" ,a.dbid,a.A0000 ");
            }
			sql.append(" from " + A01Table);
			sql.append(" left join P01 p");
			sql.append(" on p.nbase=a.nbase AND p.A0100=a.A0100");
			sql.append(" and p.P0104=" + Sql_switcher.dateValue(startTime));
			sql.append(" and p.P0106=" + Sql_switcher.dateValue(endTime));
			sql.append(" and p.belong_type = 0 and p.state=" + state);
			sql.append(") AAA");
			sql.append(" WHERE 1=1");

			if ("p011501".equalsIgnoreCase(stateSign)) {
				// 未提交
				sql.append(" and (P0115 ='01' or P0115 is NULL)");
			}
			else if ("p011503".equalsIgnoreCase(stateSign)) {
				// 已提交 已批准的也是提交过的
				sql.append(" and P0115  in ('02','03')");
			}
			else if ("commit".equalsIgnoreCase(stateSign)) {
				// 已提交
				sql.append(" and P0115 in('02','03')");
			}
			else if("ratified".equals(stateSign)){
				sql.append(" and p0115 = '02'");
			}
			else if ("score".equalsIgnoreCase(stateSign)) {
//				if (isHR)
					sql.append(" and P0115 ='03'"); // 已批准
//				else
//					sql.append(" and score > -1 and P0115 ='03'"); // 以打分
			}
			else if ("hrscoreremind".equalsIgnoreCase(stateSign)) {
				sql.append(" and score = -1 and (P0115 ='02' or P0115 ='03')"); // 未打分
			}
			
			if (isHR) {
                sql.append(" order by dbid,A0000");
            }
			return sql;
		}catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	/**
	 * @return HR 的人员范围
	 * @author szk 2014-9-25上午11:45:41
	 * @param para
	 * @param commonpara
	 * @throws GeneralException
	 */
	private String getPersonScope(String para, String commonpara) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		RecordVo paramsVo=ConstantParamter.getConstantVo("OKR_CONFIG");
		WorkPlanConfigBo bo = new WorkPlanConfigBo(this.conn, this.userView);
		//取人员范围条件参数
		String xmlValue = "";
		Map mapXml = new HashMap();
		// 有缓存则取缓存数据
		if(null != paramsVo){
			xmlValue = paramsVo.getString("str_value");
		}
		mapXml = bo.parseXml(xmlValue);
		String dbValue = mapXml.get("nbases")==null?"":(String)mapXml.get("nbases");
		String emp_scope = mapXml.get("emp_scope")==null?"":(String)mapXml.get("emp_scope");
		String[] arrpre=dbValue.split(",");
		if (arrpre.length <= 0) {
			this.setDbNameMsg("未设置认证人员库！");//修改没有设置认证库时，用于向前台返回消息	haosl	20160629
		}
		String operOrg = this.userView.getUnitIdByBusi("5");
		String orgWhere = "1=1";
		if (operOrg != null && operOrg.length() > 3) {
			StringBuffer tempSql = new StringBuffer("");
			String[] temp = operOrg.split("`");
			for (int i = 0; i < temp.length; i++) {
				if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                    tempSql.append(" or  a.b0110 like '" + temp[i].substring(2) + "%'");
                } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                    tempSql.append(" or  a.e0122 like '" + temp[i].substring(2) + "%'");
                }
			}
			orgWhere = "(" + tempSql.substring(3) + ")";
		}

		WorkPlanUtil wpu = new WorkPlanUtil(this.conn, this.userView);
		if (commonpara != null && !"".equals(commonpara)) {
			// 获取拼音简码的字段
			String pinyinFld = wpu.getPinYinFld();
			String pinyin = "";
			String a0101 = "";
			String e0122 = "";
			ArrayList paramList = this.setQueryText(commonpara);
			for (int i = 0; i < paramList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) paramList.get(i);
				String name = (String) bean.get("name");
				String value = (String) bean.get("value");
				if ("pinyin".equalsIgnoreCase(name)) {
					pinyin = value;
				}
				else if ("a0101".equalsIgnoreCase(name)) {
					a0101 = value;
				}
				else if ("e0122".equalsIgnoreCase(name)) {
					e0122 = value;
				}
				else {
					continue;
				}
			}
			StringBuffer searchSql = new StringBuffer("");
			searchSql.append(" and (1=1 ");
			//searchSql.append(" and (a.a0101 LIKE '%" + a0101 + "%'");
            if (a0101.length()>0){
                searchSql.append(" and (");
                String[] tmp=a0101.split("\\|"); 
                for (int j=0;j<tmp.length;j++){
                  if (j>0){
                      searchSql.append(" or ") ;
                  }
                  searchSql.append("  A0101 LIKE '%" + tmp[j] + "%'");
                }
                searchSql.append(")");
            }
			if ( (pinyin.length() > 0 && pinyinFld.length() > 0)) {
				searchSql.append(" and a." + pinyinFld + " like '" + pinyin + "%'");
			}
			if (e0122.length() > 0) {
                searchSql.append(" and o.codeitemid like '" + e0122 + "%'");
            }
			orgWhere += searchSql.toString() + ")";
		}

		if (para != null && !"".equals(para)) {
			// 获取拼音简码的字段
			String pinyinFld = wpu.getPinYinFld();
			// 得到电子邮箱指标
			String emailFld = wpu.getEmailFld();
			StringBuffer searchSql = new StringBuffer("");
			searchSql.append(" and (a.a0101 LIKE '%" + para + "%'");
			if (!"".equals(pinyinFld)) {
                searchSql.append(" OR a." + pinyinFld + " like '" + para + "%'");
            }
			if (!"".equals(emailFld)) {
                searchSql.append(" OR a." + emailFld + " like '" + para + "%'");
            }
			searchSql.append(" OR o.codeitemdesc like '%" + para + "%'");
			orgWhere += searchSql.toString() + ")";
		}
		for (int i = 0; i < arrpre.length; i++) {
			String pre = arrpre[i];
			sql.append("select a.A0000,A0100,a0101,b0110,e0122,e01a1,'" + pre + "' as nbase ,"+i+" as dbid from  " + pre + "A01 ");
			sql.append("a left join organization o  on a.E0122 = o.codeitemid ");
			sql.append("where " + orgWhere) ;
			//OKR人员范围sql条件
			String whereIn = bo.getOkrWhereINSql(pre, emp_scope);
			if(!StringUtils.isEmpty(whereIn)){
				 sql.append(" and a.a0100 in(");
				 sql.append(" select "+pre+"A01.a0100 ").append(whereIn).append(") ");
			}
			
			sql.append(" union ");
		}
		if(sql.length()>0){
			sql.setLength(sql.length() - 7);
		}

		return sql.toString();
	}

	/**
	 *重组织条件
	 */
	public ArrayList setQueryText(String queryText) {
		queryText = queryText.trim();
		ArrayList commonQueryParamList = new ArrayList();
		if (queryText.length() > 0) {
			String arr[] = queryText.split("`");
			for (int i = 0; i < arr.length; i++) {
				String s = arr[i];
				int p = s.indexOf("=");
				if (p > 0) {
					String key = s.substring(0, p);
					String value = s.substring(p + 1);
					value = value.replace("'", "");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("name", key);
					bean.set("value", value);
					commonQueryParamList.add(bean);
				}

			}
		}
		return commonQueryParamList;
	}

	/**
	 * 查询出，我的部门所以人员的周报提交情况
	 * 
	 * @param nbase
	 * @param a0100
	 * @param startTime
	 * @param endTime
	 * @param state
	 *            工作总结标志，（周报，月报，季报，年报等）
	 * @param stateSign
	 *            状态标志，（以提交，未提交，以打分）
	 * @param para
	 * @param commonpara
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList selectTeamWeekly(String nbase, String a0100, String startTime, String endTime, String state, String stateSign, String para, String commonpara, boolean isHR, String e0122)
			throws GeneralException {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			String sql = this.getSelectTeamWeeklySQL(nbase, a0100, startTime, endTime, state, stateSign, para, commonpara, isHR, e0122).toString();
			if(sql==null||"".equals(sql)){	//haosl 20160629 
				return new ArrayList();
			}
			rs = dao.search(sql);
			while (rs.next()) {
				HashMap map = new HashMap();
				String photoUrl = wp.getPhotoPath(rs.getString("nbase"), rs.getString("a0100"));
				String _e01a1 = rs.getString("E01a1");
				if (rs.getString("p01e01a1") != null && rs.getString("p01e01a1").length() > 0) {
					_e01a1 = rs.getString("p01e01a1");
				}
				String _e0122 = rs.getString("E0122");
				if (rs.getString("p01e0122") != null && rs.getString("p01e0122").length() > 0) {
					_e0122 = rs.getString("p01e0122");
				}
				String e01a1 =  AdminCode.getCodeName("@K", _e01a1) ;
				if (!"".equals(e01a1)) {
					e01a1 = "（" + e01a1 + "）";
				}
				map.put("photoUrl", photoUrl);
				map.put("departName", AdminCode.getCodeName("UM", _e0122) + e01a1);
				map.put("p0115", rs.getString("P0115"));
				map.put("score", rs.getString("score"));
				map.put("nbase", WorkPlanUtil.encryption(rs.getString("nbase")));
				map.put("a0100", WorkPlanUtil.encryption(rs.getString("A0100")));
				map.put("nbaseA0100", WorkPlanUtil.encryption(rs.getString("nbase") + rs.getString("A0100")));
				map.put("a0101", rs.getString("A0101"));
				map.put("p0100", WorkPlanUtil.encryption(rs.getString("P0100")));
				map.put("e0122", ""); // 用于 hr个人总结 单人发邮件

				list.add(map);
			}
		}
		catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		finally {
			WorkPlanUtil.closeDBResource(rs);
		}
		return list;
	}

	public ArrayList selectTeamWeekly(ArrayList e01a1List, String startTime,String endTime, String state, String stateSign, String para,String commonpara, boolean isHR, String e0122)
			throws GeneralException {
		
		ArrayList list = new ArrayList();
		RowSet rs = null;
		int pagesize=e01a1List.size();
		
		String sql ="";
	    for(int i=0;i<pagesize;i++){
		         try {
		             LazyDynaBean bean = (LazyDynaBean) (e01a1List.get(i));
		   			 String e01a1= (String) bean.get("e01a1");
	   				 String tablename=this.getTeamPeopleSql(e01a1, true, startTime, endTime, state);
					 tablename="("+tablename+") T";
					 sql= " select T.*,p01.p0115,p01.score,p01.p0100,p01.E0122 as p01e0122,p01.e01a1 as p01e01a1 from " +tablename
	                 +" left join p01  on p01.a0100=T.a0100 and T.nbase=T.nbase"
	                 +" and  p01.P0104=" + Sql_switcher.dateValue(startTime)+"and p01.P0106=" + Sql_switcher.dateValue(endTime);
					 rs = dao.search(sql);
					 while (rs.next()) {
						 if("p011501".equals(stateSign)){//未报
							 if(WorkPlanConstant.SummaryStatus.UNCOMMITTED.equals(rs.getString("p0115")) || StringUtils.isBlank(rs.getString("p0115"))){
								
							 }else{
								 continue;
							 }
						 }else if("commit".equals(stateSign)){//已报 (包含已提交和已报批,对于与工作总结来说(三种状态),不是未报即可)
							 if(WorkPlanConstant.SummaryStatus.UNCOMMITTED.equals(rs.getString("p0115")) || StringUtils.isBlank(rs.getString("p0115"))){
								 continue;
							 }
						 }else if("p011503".equals(stateSign)){//未批
							 if(WorkPlanConstant.SummaryStatus.COMMITTED.equals(rs.getString("p0115"))){
								 
							 }else{
								 continue;
							 }
						 }else if("score".equals(stateSign)){//已批
							 if(WorkPlanConstant.SummaryStatus.EVALUATED.equals(rs.getString("p0115"))){
								 
							 }else{
								 continue;
							 }
						 }
						HashMap map = new HashMap();
						String photoUrl = wp.getPhotoPath(rs.getString("nbase"), rs.getString("a0100"));
						String _e01a1 = rs.getString("E01a1");
						if (rs.getString("p01e01a1") != null && rs.getString("p01e01a1").length() > 0) {
							_e01a1 = rs.getString("p01e01a1");
						}
						String _e0122 = rs.getString("E0122");
						if (rs.getString("p01e0122") != null && rs.getString("p01e0122").length() > 0) {
							_e0122 = rs.getString("p01e0122");
						}
					    e01a1 =  AdminCode.getCodeName("@K", _e01a1) ;
						if (!"".equals(e01a1)) {
							e01a1 = "（" + e01a1 + "）";
						}
						map.put("photoUrl", photoUrl);
						map.put("departName", AdminCode.getCodeName("UM", _e0122) + e01a1);
						map.put("p0115", rs.getString("P0115"));
						map.put("score", rs.getString("score"));
						map.put("nbase", WorkPlanUtil.encryption(rs.getString("nbase")));
						map.put("a0100", WorkPlanUtil.encryption(rs.getString("A0100")));
						map.put("nbaseA0100", WorkPlanUtil.encryption(rs.getString("nbase") + rs.getString("A0100")));
						map.put("a0101", rs.getString("A0101"));
						map.put("p0100", WorkPlanUtil.encryption(rs.getString("P0100")));
						map.put("e0122", ""); // 用于 hr个人总结 单人发邮件
		
						list.add(map);
					}
			 } catch (SQLException e) {
				e.printStackTrace();
			 } finally {
				WorkPlanUtil.closeDBResource(rs);
			 }
	    }
		return list;
	}

	/**
	 * @param nbase
	 * @param a0100
	 * @return
	 * @author szk 2014-7-31下午05:56:29
	 * @param num
	 * @param rownum
	 */
	public LazyDynaBean getTeamList(String nbase, String a0100, int rownum, int num, String startTime, String endTime, String state) {
			LazyDynaBean mybean = new LazyDynaBean();
			ArrayList list = new ArrayList();
			RowSet rset = null;
			String count="0";			
			int subnum=0;
			HashMap existsMap= new HashMap();
			String strsql="";
	        	try {
	        		    //岗位有人的情况
         		    	 String tablename = this.getTeamPeopleSql(nbase,a0100, true, startTime, endTime, state);
      					 tablename="("+tablename+") T";
      					 strsql= " select T.*,p01.E0122 as p01e0122,p01.e01a1 as p01e01a1 from " +tablename
      		                +" left join p01  on p01.a0100=T.a0100 and p01.nbase='"+nbase+"'"
      		                +" and  p01.P0104=" + Sql_switcher.dateValue(startTime)+"and p01.P0106=" + Sql_switcher.dateValue(endTime)
		        			+"  AND (belong_type is null or belong_type=0) where T.A0100 not in ('"+a0100+"')";//工作总结人力地图排除本人 chent
      					 rset=dao.search(strsql);
		        			while(rset.next()){
		        				// 要存的数据
		        				HashMap mp = new HashMap();
		        				WorkPlanBo pb = new WorkPlanBo(conn, userView);
		        				String url = pb.getPhotoPath(rset.getString("nbase"), rset.getString("a0100"));
		        				String a0101 = rset.getString("a0101");

		        				String e0122 = rset.getString("E0122");
		        				String e01a1 = rset.getString("E01a1");
		        				existsMap.put(e01a1, "1");
		        				if (rset.getString("p01e01a1") != null && rset.getString("p01e01a1").length() > 0) {
		        					e01a1 = rset.getString("p01e01a1");
		        				}

		        				if (rset.getString("p01e0122") != null && rset.getString("p01e0122").length() > 0) {
		        					e0122 = rset.getString("p01e0122");
		        				}
		        				String org = AdminCode.getCodeName("UM", e0122);
		        				String pos = AdminCode.getCodeName("@K", e01a1);
		        				String sql="select count(*) cno from ("+this.getTeamPeopleSql(rset.getString("nbase"), rset.getString("a0100"), true, startTime, endTime, state)+") b" ;
		        				RowSet rset1 = dao.search(sql);
		        				if (rset1.next()) {
		        					count=rset1.getString(1);
		        					mp.put("count", count);// 总条数
		        					count="0";
		        				}
		        				mp.put("flag", "true");
		        				mp.put("url", url);
		        				mp.put("nbase", WorkPlanUtil.encryption(rset.getString("nbase")));
		        				mp.put("a0100", WorkPlanUtil.encryption(rset.getString("a0100")));
		        				mp.put("org", org);
		        				mp.put("pos", pos);
		        				mp.put("a0101", a0101);
		        				mp.put("e0122", e0122);
		        				mp.put("e01a1", WorkPlanUtil.encryption(e01a1));
		        				list.add(mp);
		        			}
		        			mybean.set("list", list);
	    			
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}finally {
	    			WorkPlanUtil.closeDBResource(rset);
	    		}
	        //岗位无人的情况
		 //获得当前登录人的所有下级岗位
		try {
			WorkPlanBo planbo = new WorkPlanBo(conn, userView);
			ArrayList e01a1list = planbo.getAllSubE01a1List(nbase, a0100);
			int pagesize = e01a1list.size();

			for (int j = 0; j < pagesize; j++) {
				LazyDynaBean bean = (LazyDynaBean) (e01a1list.get(j));
				String e01a1 = (String) bean.get("e01a1");
				if (existsMap.containsKey((e01a1))) {
                    continue;//已有此岗位的人
                }
				strsql = "select * from " + this.userView.getDbname()
						+ "A01 where E01A1='" + bean.get("e01a1") + "'";// 01010501
				rset = dao.search(strsql);
				if (!rset.next()) {// 当前岗位没人
					String e0122 = (String) bean.get("e0122");
					String org = AdminCode.getCodeName("UM", e0122);
					String codeitemdesc = (String) bean.get("codeitemdesc");
					initSuperiorFld();
					if (superiorFld == null || "".equals(superiorFld)) {
						break;
					}
					// 先查出当前岗位的所有下属岗位
					strsql = "select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+superiorFld+"='"
							+ e01a1 + "'";
					RowSet rset1 = dao.search(strsql);
					subnum = 0;
					while (rset1.next()) {
						String sube01a1 = (String) rset1.getString("e01a1");
						String tablename = this.getTeamPeopleSql(sube01a1,
								true, startTime, endTime, state);
						String sql2 = "select count (*) from " + "("
								+ tablename + ") T";
						RowSet rset2 = dao.search(sql2);
						if (rset2.next()) {
							subnum = subnum + rset2.getInt(1);
						}
					}

					if (subnum > 0) {
						HashMap mp = new HashMap();
						mp.put("flag", "false");
						mp.put("url", "/images/photo.jpg");
						mp.put("nbase", "");
						mp.put("a0100", "");
						mp.put("a0101", codeitemdesc);
						mp.put("org", org);
						mp.put("e01a1", WorkPlanUtil.encryption(e01a1));
						mp.put("count", subnum + "");
						subnum = 0;
						list.add(mp);
					}
				}

			}
			mybean.set("list", list);

		} catch (Exception e) {
			e.printStackTrace();
		}
	        int subcount=list.size();
	        mybean.set("count", subcount+"");
	        if (subcount==0){
	        	num=1;
	        }
	        else {
	        	while ((num - 1) * rownum - subcount >= 0 || (num - 1) * rownum > subcount) {
	        		if (num > 1) {
                        num = num - 1;
                    }
	        	}
	        	
	        }
				mybean.set("num", num + "");
			return mybean;
		
	}
	
	public LazyDynaBean getTeamList(ArrayList e01a1list, int rownum, int num, String startTime, String endTime, String state) {
		LazyDynaBean mybean = new LazyDynaBean();
		ArrayList list = new ArrayList();
		RowSet rset = null;
		String count="0";
		int subnum=0;
		String strsql="";
		int pagesize=e01a1list.size();
        for(int i=0;i<pagesize;i++){
        	try {
        		    //岗位有人的情况
        		    LazyDynaBean bean = (LazyDynaBean) (e01a1list.get(i));
	         		    	 String tablename = this.getTeamPeopleSql((String) (bean.get("e01a1")), true, startTime, endTime, state);
	      					 tablename="("+tablename+") T";
	      					 strsql= " select T.*,p01.E0122 as p01e0122,p01.e01a1 as p01e01a1 from " +tablename
	      		                +" left join p01  on p01.a0100=T.a0100 and p01.nbase=T.nbase"
	      		                +" and  p01.P0104=" + Sql_switcher.dateValue(startTime)+"and p01.P0106=" + Sql_switcher.dateValue(endTime)
	      					 	+"  AND (belong_type is null or belong_type=0)";
	      					    rset=dao.search(strsql);
			        			while(rset.next()){
			        				// 要存的数据
			        				HashMap mp = new HashMap();
			        				WorkPlanBo pb = new WorkPlanBo(conn, userView);
			        				String url = pb.getPhotoPath(rset.getString("nbase"), rset.getString("a0100"));
			        				String a0101 = rset.getString("a0101");
	
			        				String e0122 = rset.getString("E0122");
			        				String e01a1 = rset.getString("E01a1");
			        				if (rset.getString("p01e01a1") != null && rset.getString("p01e01a1").length() > 0) {
			        					e01a1 = rset.getString("p01e01a1");
			        				}
	
			        				if (rset.getString("p01e0122") != null && rset.getString("p01e0122").length() > 0) {
			        					e0122 = rset.getString("p01e0122");
			        				}
			        				String org = AdminCode.getCodeName("UM", e0122);
			        				String pos = AdminCode.getCodeName("@K", e01a1);
			        				String sql="select count(*) cno from ("+this.getTeamPeopleSql(rset.getString("nbase"), rset.getString("a0100"), true, startTime, endTime, state)+") b" ;
			        				RowSet rset1 = dao.search(sql);
			        				if (rset1.next()) {
			        					count=rset1.getString(1);
			        					mp.put("count", count);// 总条数
			        					count="0";
			        				}
			        				mp.put("flag", "true");
			        				mp.put("url", url);
			        				mp.put("nbase", WorkPlanUtil.encryption(rset.getString("nbase")));
			        				mp.put("a0100", WorkPlanUtil.encryption(rset.getString("a0100")));
			        				mp.put("org", org);
			        				mp.put("pos", pos);
			        				mp.put("a0101", a0101);
			        				mp.put("e0122", e0122);
			        				mp.put("e01a1", WorkPlanUtil.encryption(e01a1));
			        				list.add(mp);
			        			}
			        			mybean.set("list", list);
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}finally {
    			WorkPlanUtil.closeDBResource(rset);
    		}
        } 
        //岗位无人的情况
       for(int j=0;j<pagesize;j++){
           	try {
	           	   initSuperiorFld();
	      		   if (superiorFld == null || "".equals(superiorFld)) {
	      			   break;
	      		   }
	           	   LazyDynaBean bean = (LazyDynaBean) (e01a1list.get(j));
	           	   strsql = "select * from "+this.userView.getDbname()+"A01 where E01A1='" + bean.get("e01a1") + "'";//01010501
	          	   rset=dao.search(strsql);
	          	   if(!rset.next()){//当前岗位没人
	          		     String e01a1= (String) bean.get("e01a1");
	          		     String e0122 =(String) bean.get("e0122");
	          		     String org = AdminCode.getCodeName("UM", e0122);
	          			 String codeitemdesc= (String) bean.get("codeitemdesc");
	          			
	          			 //先查出当前岗位的所有下属岗位
	                     strsql="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+superiorFld+"='"+e01a1+"'";
	          			 RowSet rset1=dao.search(strsql);
	                     while(rset1.next()){
	                   	 String sube01a1= (String) rset1.getString("e01a1");
	                   	 String sql2="select count(*) from "+this.userView.getDbname()+"A01 where E01A1='"+sube01a1+"'";
	                   	 RowSet rset2=dao.search(sql2);
	                   	 if (rset2.next()){
	                             subnum =subnum +rset2.getInt(1);
	                        }
	                   	 
	                   	 String sql3="select * from "+this.userView.getDbname()+"A01 where E01A1='"+sube01a1+"'";
	                   	 RowSet rset3=dao.search(sql3);
	                   	 if (rset3.next()){
	                   		 HashMap mp = new HashMap();
	                   			mp.put("flag", "false");
			                   	mp.put("url", "/images/photo.jpg");
			                   	mp.put("nbase", WorkPlanUtil.encryption("Usr"));
		        				mp.put("a0100", WorkPlanUtil.encryption(rset3.getString("a0100")));
		        				mp.put("a0101", codeitemdesc);
		        				mp.put("org", org);
		        				mp.put("e01a1", WorkPlanUtil.encryption(e01a1));
		        				mp.put("count", subnum+"");
	                            subnum=0;
		        				list.add(mp);
	                        }
	                    }            
	                     mybean.set("list", list);
	          	   }
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
		}
        int subcount=list.size();
        mybean.set("count", subcount+"");
        if (subcount==0){
        	num=1;
        }
        else {
        	while ((num - 1) * rownum - subcount >= 0 || (num - 1) * rownum > subcount) {
        		if (num > 1) {
                    num = num - 1;
                }
        	}
        	
        }
			mybean.set("num", num + "");
		return mybean;
	}
	
	/*public LazyDynaBean getTeamList(String nbase, String a0100, int rownum, int num, String startTime, String endTime, String state) {
		ArrayList list = new ArrayList();
		LazyDynaBean bean = new LazyDynaBean();
		RowSet rs = null;
		String count = "0";
		try {
			// String teamsql = this.getTeamPeopleSql(nbase, a0100, true);
			String teamsql = this.getTeamPeopleSql(nbase, a0100, true, startTime, endTime, state);
			StringBuffer strsql = new StringBuffer();
			strsql.append("select count(*) cno from (");
			strsql.append(teamsql);
			strsql.append(") b");

			RowSet rset = dao.search(strsql.toString());
			if (rset.next()) {
				count = rset.getString(1); // 总条数
			}
			bean.set("count", count); // 总条数

			if ((num - 1) * rownum - Integer.parseInt(count) == 0 || (num - 1) * rownum > Integer.parseInt(count)) {
				if (num > 1)
					num = num - 1;
			}
			bean.set("num", num + "");
			strsql.setLength(0); // 清空
			strsql.append("select top " + rownum + " NBASE,E0122,E01a1,A0100,A0101,p01E0122,p01E01a1 from (");
			strsql.append("select ROW_NUMBER() OVER (ORDER BY a.a0100) AS RowNo,a.A0100,a.a0101,a.b0110,a.nbase,a.E0122,a.E01a1,p.E0122 p01E0122 ,p.E01a1 p01E01a1 from (");
			strsql.append(teamsql);
			strsql.append(") a left join P01 p ");
			strsql.append(" on p.nbase=a.nbase AND p.A0100=a.A0100");
			strsql.append(" and p.P0104=" + Sql_switcher.dateValue(startTime));
			strsql.append(" and p.P0106=" + Sql_switcher.dateValue(endTime));
			strsql.append(" and p.belong_type = 0 and p.state=" + state);
			strsql.append(") b where RowNo > " + rownum + "*(" + num + "-1)");
			rs = dao.search(strsql.toString());
			while (rs.next()) {
				// 要存的数据
				HashMap mp = new HashMap();
				WorkPlanBo pb = new WorkPlanBo(conn, userView);
				String url = pb.getPhotoPath(rs.getString("nbase"), rs.getString("a0100"));
				String a0101 = rs.getString("a0101");

				String e0122 = rs.getString("E0122");
				String e01a1 = rs.getString("E01a1");
				if (rs.getString("p01e01a1") != null && rs.getString("p01e01a1").length() > 0) {
					e01a1 = rs.getString("p01e01a1");
				}

				if (rs.getString("p01e0122") != null && rs.getString("p01e0122").length() > 0) {
					e0122 = rs.getString("p01e0122");
				}
				String org = AdminCode.getCodeName("UM", e0122);
				String pos = AdminCode.getCodeName("@K", e01a1);
				strsql.setLength(0); // 清空
				strsql.append("select count(*) cno from (");
				strsql.append(this.getTeamPeopleSql(rs.getString("nbase"), rs.getString("a0100"), true, startTime, endTime, state));
				strsql.append(") b");
				rset = dao.search(strsql.toString());
				if (rset.next()) {
					mp.put("count", rset.getString(1)); // 总条数
				}

				mp.put("url", url);
				mp.put("nbase", WorkPlanUtil.encryption(rs.getString("nbase")));
				mp.put("a0100", WorkPlanUtil.encryption(rs.getString("a0100")));
				mp.put("org", org);
				mp.put("pos", pos);
				mp.put("a0101", a0101);
				mp.put("e0122", e0122);
				mp.put("e01a1", e01a1);
				list.add(mp);
			}
			bean.set("list", list);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			WorkPlanUtil.closeDBResource(rs);
		}
		return bean;
	}*/

	/**
	 * 保存 评价
	 * 
	 * @param p0100
	 * @param p0113
	 * @param score
	 * @author szk 2014-8-9下午05:21:11
	 * @throws GeneralException
	 */
	public void saveContent(String p0100, String p0113, String score) throws GeneralException {
		StringBuffer updateSignSql = new StringBuffer();
		updateSignSql.append("update P01");
		updateSignSql.append(" set p0115='03',p0113='" + p0113 + "',score =" + score);
		updateSignSql.append(" where P0100=" + p0100);

		try {
			dao.update(updateSignSql.toString());
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException("保存评价发生错误！");
		}
	}

	/**
	 * 汇总团队的个人总结
	 * 
	 * @param nbase
	 * @param a0100
	 * @param string
	 * @param string2
	 * @param cyclenow
	 * @param string3
	 * @return
	 * @author szk 2014-9-16下午04:27:11
	 * @throws GeneralException
	 */
	public ArrayList CollectByPerson(String nbase, String a0100, String e01a1, String startTime, String endTime, String state, String stateSign) throws GeneralException {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			StringBuffer thisweek = new StringBuffer();
			StringBuffer nextweek = new StringBuffer();
			String sql = this.getSelectTeamWeeklySQL(nbase, a0100, startTime, endTime, state, stateSign, "", "", false, e01a1).toString();
			rs = dao.search(sql);

			int num = 0;
			while (rs.next() && num < 10) {
				thisweek.append("\n" + rs.getString("a0101") + "的工作总结\n");
				nextweek.append("\n" + rs.getString("a0101") + "的下期工作计划\n");
				if (rs.getString("p0100") != null) {
					if ("03".equals(rs.getString("p0115")) || "02".equals(rs.getString("p0115")) ) {
						thisweek.append("\n" + rs.getString("p0109") + "\n");
						nextweek.append("\n" + rs.getString("P0120") + "\n");
					}
					else {
						thisweek.append("\n未提交！\n");
						nextweek.append("\n未提交！\n");
					}
				}
				else {
					thisweek.append("\n未填写！\n");
					nextweek.append("\n未填写！\n");
				}
				num++;
			}
			list.add(thisweek.toString());
			list.add(nextweek.toString());
			// 取最后一条记录号，即rs长度
			rs.last();
			String warn = "";
			if (rs.getRow() > 10) {
                warn = "warnperson";
            }
			list.add(warn);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally {
			WorkPlanUtil.closeDBResource(rs);
		}
		return list;
	}

	/**
	 * 汇总下属部门总结
	 * 
	 * @param nbase
	 * @param a0100
	 * @param e01a1list
	 * @param string
	 * @param string2
	 * @param cyclenow
	 * @param string3
	 * @return
	 * @author szk 2014-9-17下午01:38:52
	 * @param cyclenow 
	 */
	public ArrayList CollectByOrg(String nbase, String a0100, String e01a1, String startTime, String endTime, String state) {
		ArrayList sumlist = new ArrayList();
		StringBuffer thisweek = new StringBuffer();
		StringBuffer nextweek = new StringBuffer();

		ArrayList e01a1list = new ArrayList();
		LazyDynaBean bean = new LazyDynaBean();
		if (null != e01a1 && !"".equals(e01a1)) {
			bean.set("e01a1", e01a1);
			bean.set("ispart", "0");
			e01a1list.add(bean);
		}
		else {
			e01a1list = workPlanUtil.getMyE01a1List(nbase, a0100);
		}
		ArrayList list = this.getMySubDeptPerson(e01a1list, startTime, endTime, state, "", false);
		for (int i = 0; i < list.size() && i < 10; i++) {
			HashMap map = (HashMap) list.get(i);
			
			thisweek.append("\n" + map.get("e0122desc") + "的工作总结\n");
			nextweek.append("\n" + map.get("e0122desc") + "的下期工作计划\n");
			//暂不显示姓名
//			thisweek.append("\n" + map.get("e0122desc") + "(" + map.get("a0101") + ")的工作总结\n");
//			nextweek.append("\n" + map.get("e0122desc") + "(" + map.get("a0101") + ")的下期工作计划\n");
			if (map.get("p0100") != null) {
				if ("03".equals(map.get("p0115")) || "02".equals(map.get("p0115"))) {
					thisweek.append("\n" + map.get("p0109") + "\n");
					nextweek.append("\n" + map.get("P0120") + "\n");
				}
				else {
					thisweek.append("\n未提交！\n");
					nextweek.append("\n未提交！\n");
				}
			}
			else {
				thisweek.append("\n未填写！\n");
				nextweek.append("\n未填写！\n");
			}
		}
		sumlist.add(thisweek.toString());
		sumlist.add(nextweek.toString());
		String warn = "";
		if (list.size() > 10) {
            warn = "warnorg";
        }
		sumlist.add(warn);
		return sumlist;
	}

	/**
	 * 批准 03
	 * 
	 * @param p0100
	 * @author szk 2014-9-19下午02:30:23
	 */
	public void approveWorkSummary(String p0100) {
		StringBuffer updateSignSql = new StringBuffer();
		//updateSignSql.append("update P01 set p0115='02'");
		updateSignSql.append("update P01 set p0115='03'");
		updateSignSql.append(" where P0100=" + p0100);

		try {
			dao.update(updateSignSql.toString());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 退回 07
	 * 
	 * @param p0100
	 * @author 
	 */
	public void rejectWorkSummary(String p0100) {
		StringBuffer updateSignSql = new StringBuffer();
		updateSignSql.append("update P01 set p0115='07'");
		updateSignSql.append(" where P0100=" + p0100);

		try {
			dao.update(updateSignSql.toString());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

	}
	/**
	 * hr用户部门总结人员
	 * 
	 * @return
	 * @author szk 2014-10-13下午02:47:41
	 * @param para
	 * @param commonpara
	 * @throws GeneralException
	 */
	public ArrayList getHre01a1list(String para, String commonpara) throws GeneralException {
		String sql = this.getPersonScope(para, commonpara);
		if(sql==null||sql.length()==0){
			return new ArrayList();	//返回空的集合
		}
		ArrayList e01a1list = new ArrayList();
		RowSet u09Set = null;
		RowSet mainSet = null;
		try {
			mainSet = dao.search(sql);
			StringBuffer u09sql = new StringBuffer();
			while (mainSet.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				String e01a1 = mainSet.getString("e01a1");
				if (null != e01a1 && !"".equals(e01a1)) {
					bean.set("e01a1", e01a1);
					bean.set("ispart", "0");
					e01a1list.add(bean);
				}
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
				String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
	            String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
	            String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
	            String curPartTab=mainSet.getString("nbase")+setid;
	            if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
	            {
	            	u09sql.append(" select "+e01a1_field+" as e01a1 ,1 as ispart from "+curPartTab+" where a0100="+"'"+mainSet.getString("a0100")+"'"  +" and " +appoint_field+"='0' union ");
	            }
			}
			//szk查兼职 
			if (u09sql.length()>0) {
				u09sql.setLength(u09sql.length()-7);
				u09Set = dao.search(u09sql.toString());
				while (u09Set.next()) {
					LazyDynaBean bean = new LazyDynaBean();
					String e01a1 = u09Set.getString("e01a1");
					if (null != e01a1 && !"".equals(e01a1)) {
						bean.set("e01a1", e01a1);
						bean.set("ispart", "1");
						e01a1list.add(bean);
					}
				}
			}
		}
		//select * from  UsrA09 where a0100 in ('00000060','00000049') and C0901 = '0'
		catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			WorkPlanUtil.closeDBResource(mainSet);
			WorkPlanUtil.closeDBResource(u09Set);
		}
		return e01a1list;
	}

	/**
	 * 保存范围
	 * @param p0100
	 * @param scope
	 * @author szk
	 * 2014-11-3下午05:13:59
	 */
	public void saveScopeSummary(String p0100, int scope) {
		StringBuffer updateSignSql = new StringBuffer();
		updateSignSql.append("update P01 set scope="+scope);
		updateSignSql.append(" where P0100=" + p0100);

		try {
			dao.update(updateSignSql.toString());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

			
	}
	/**
	 * 解决ORA-01795: 列表中的最大表达式数为 1000 
	 * @param value
	 * @return 999个为一组的值
	 * @author chent
	 * 
	 * */
	private String cutString(String value){
		StringBuilder sqlString = new StringBuilder();
		String[] list = value.split(",");
		
		for (int i = 0; i < list.length; i++) {  
			  
            if (i == (list.length - 1)) {  
                sqlString.append(list[i]); //SQL拼装，最后一条不加“,”。  
            }else if((i%999)==0 && i>0){  
                sqlString.append(list[i]).append(") or "+superiorFld+" in ("); //解决ORA-01795问题  
            }else{  
                sqlString.append(list[i]).append(",");  
            }  
        }
		return sqlString.toString();
	}
	
	 /**
     * 校验填报期间范围，控制填报总结
     * @param periodWeek2 
     * @param periodMonth2 
     * @param periodYear2 
     * @param periodType2
     * @param validPre
     * @param validNow
     */
	public boolean validPreNow(String periodType, String periodYear, String periodMonth, String periodWeek, int validPre, int validNow) {
		boolean fillPlan = false;//是否可以填报计划 false 不能填报
		Calendar calPre = Calendar.getInstance();//期间起始 日期
		Calendar calNow = Calendar.getInstance();//期间结束 日期
		Calendar curTime = Calendar.getInstance();//当前时间
		if("4".equals(periodType)) {//year
			//根据年末的时间计算期间的起始结束日期
			calPre.set(Integer.valueOf(periodYear)-1,Calendar.DECEMBER, 31);
			calNow.set(Integer.valueOf(periodYear), Calendar.JANUARY, 1);
		}else if("5".equals(periodType)) {//半年
			if("1".equals(periodWeek)) {
				calPre.set(Integer.valueOf(periodYear)-1, Calendar.DECEMBER, 31);
				calNow.set(Integer.valueOf(periodYear), Calendar.JANUARY, 1);
			}else if("2".equals(periodWeek)){
				calPre.set(Integer.valueOf(periodYear), Calendar.JUNE, 30);
				calNow.set(Integer.valueOf(periodYear), Calendar.JULY, 1);
			}
		}else if("3".equals(periodType)) {//季度
			if("1".equals(periodWeek)) {
				calPre.set(Integer.valueOf(periodYear)-1, Calendar.DECEMBER, 31);
				calNow.set(Integer.valueOf(periodYear), Calendar.JANUARY, 1);
			}else if("2".equals(periodWeek)){
				calPre.set(Integer.valueOf(periodYear), Calendar.MARCH, 31);
				calNow.set(Integer.valueOf(periodYear), Calendar.APRIL, 1);
			}else if("3".equals(periodWeek)) {
				calPre.set(Integer.valueOf(periodYear), Calendar.JUNE, 30);
				calNow.set(Integer.valueOf(periodYear), Calendar.JULY, 1);
			}else if("4".equals(periodWeek)) {
				calPre.set(Integer.valueOf(periodYear), Calendar.SEPTEMBER, 30);
				calNow.set(Integer.valueOf(periodYear), Calendar.OCTOBER, 1);
			}
		}else if("2".equals(periodType)) {//月
			if("1".equals(periodMonth)) {
                calPre.set(Integer.valueOf(periodYear)-1, Calendar.DECEMBER, 31);
            } else{
                calPre.set(Integer.valueOf(periodYear),Integer.valueOf(periodMonth)-1,1);
                calPre.add(Calendar.DATE,-1);
            }
			calNow.set(Integer.valueOf(periodYear),Integer.valueOf(periodMonth)-1, 1);
		}else if("1".equals(periodType)) {//周
			 WorkPlanSummaryBo wpsBo= new WorkPlanSummaryBo(null, this.conn);
	          String startDate = wpsBo.getMondayOfDate(Integer.parseInt(periodYear), 
	                    Integer.parseInt(periodMonth), Integer.parseInt(periodWeek));
	          String endDate = wpsBo.getSunDayOfDate(Integer.parseInt(periodYear), 
	                    Integer.parseInt(periodMonth), Integer.parseInt(periodWeek));
	          calPre.setTime(DateUtils.getDate(startDate, "yyyy-MM-dd"));
	          calNow.setTime(DateUtils.getDate(endDate, "yyyy-MM-dd"));
		}
		
		//填报日期在填报范围内允许填报，否则不可填报
		calPre.add(Calendar.DATE,-validPre);
		calNow.add(Calendar.DATE,validNow);
		if(calPre.before(curTime) && curTime.before(calNow)) {
			fillPlan = true; 
		}
		return fillPlan;
	}
}
