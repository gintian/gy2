package com.hjsj.hrms.businessobject.workplan.plan_task;

import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:PlanTaskBo.java</p>
 * <p>Description:任务业务对象类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2014-7-12 15:46:11</p>
 * @author 刘蒙
 * @version 1.0
 */
@SuppressWarnings("all")
public class PlanTaskBo {
	public void setFromflag(String fromflag) {
		this.fromflag = fromflag;
	}

	String fromflag=""; //来自hr模块？   "hr_create":工作计划制定
	
	public PlanTaskBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}
	
	private Connection conn = null;
	private UserView userView = null;

	/** ########################################################################### */
	
	/** 根据任务id查找任务 */
	public RecordVo getTask(int p0800) throws Exception {
		return getRecordVo("P08", "p0800", new Integer(p0800));
	}
	
	/** 根据计划id查找计划 */
	public RecordVo getPlan(int p0700) throws Exception {
		return getRecordVo("P07", "p0700", new Integer(p0700));
	}
	
	
	/** 根据计划id查找计划 */
	public RecordVo getRecordVo(String table, String pkField, Object pkValue){
		RecordVo vo = null;
		try {
			vo = new RecordVo(table);
			vo.setObject(pkField.toLowerCase(), pkValue);
			vo = new ContentDAO(conn).findByPrimaryKey(vo);
		} catch (Exception e) {
		    vo = null;
			e.printStackTrace();
		}
		return vo;
	}
	
	/** 根据FieldItem将字段的值转换成字符串的形式 */
	public static String getFieldStringValue(FieldItem item, Object actualValue) throws Exception {
		try {
			String type = item.getItemtype();
			
			if (actualValue == null) {
				if ("N".equals(item.getItemtype())) {
					return "0";
				} else {
					return "";
				}
			}
			
			// 处理特殊类型的数据（日期、大文本）
			if ("D".equals(type)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				return sdf.format((Date) actualValue);
			} else if ("M".equals(type)) {				
				return SafeCode.encode(actualValue + "");
			} else if ("A".equals(type)) {
				if (item.isCode()) { // 代码类型的数据
					String codesetId = item.getCodesetid();
					return AdminCode.getCodeName(codesetId, (String) actualValue);
				} else {
					return actualValue + "";
				}
			} else if ("N".equals(type)) { // 根据小数位长度，确定字符串格式：0.00
				if (((Number) actualValue).doubleValue() == 0) {
					return "0";
				}
				
				int decimalWidth = item.getDecimalwidth();
				
				StringBuffer pattern = new StringBuffer("#");
				if (decimalWidth > 0) {
					pattern.append(".");
				}
				for (int i = 0; i < decimalWidth; i++) {
					pattern.append("#");
				}
				
				NumberFormat nf = new DecimalFormat(pattern.toString());
				return nf.format(actualValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return "";
	}
	
	/** 根据FieldItem将字符串形式的字段值转换成实际类型的值 */
	public static Object getFieldActualValue(FieldItem item, String strValue, boolean flag) throws Exception {
		try {
			String type = item.getItemtype();

			// 处理特殊类型的数据（日期、大文本）
			if ("D".equals(type)) {
				if ("".equals(strValue)) {
					return null;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
				return sdf.parse(strValue);
			} else if ("M".equals(type) || "A".equals(type)) { // 文本型和代码型本身就是字符串
				return strValue;
			} else if ("N".equals(type)) {
				Float n = null;
				String num = "".equals(strValue) ? "0" : strValue;
				try{
					n = new Float(num);
				}catch (Exception e) {
					if(flag){
						n = null;
					}else{
						e.printStackTrace();
						throw e;
					}
					
				}
				return n;
			}
		} catch (Exception e) {
			if(!flag){
				e.printStackTrace();
				throw e;
			}
			
		}
		
		return null;
	}
	
	
	/**
	 * 将请求传递的参数p0700,p0800,p0723,objectid经过转码解密后存入新的集合中,返回
	 * @param formHM
	 * @return
	 */
	public static Map setOutParams(Map formHM) {
		String p0700 = (String) (formHM.get("p0700") == null ? "" : formHM.get("p0700")); // 计划id
		p0700 = "undefined".equals(p0700) ? "" : p0700;
		String p0800 = (String) (formHM.get("p0800") == null ? "" : formHM.get("p0800")); // 任务id
		String p0723 = (String) (formHM.get("p0723") == null ? "" : formHM.get("p0723")); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) (formHM.get("objectid") == null ? "" : formHM.get("objectid")); // 对象id: usr00000019
		objectid = "undefined".equals(objectid) ? "" : objectid;
		String othertask = (String) (formHM.get("othertask") == null ? "0" : formHM.get("othertask")); //1是穿透任务
		String myP0700 = (String) (formHM.get("myP0700") == null ? "0" : formHM.get("myP0700")); //当前页面的工作计划
		String concerned_bteam = (String)(formHM.get("concerned_bteam") == null ? "" : formHM.get("concerned_bteam")); //1是我的部门
		String fromflag = (String) (formHM.get("fromflag") == null ? "" : formHM.get("fromflag")); //// "hr_create":工作计划制定
		// 解码
		p0700 = WorkPlanUtil.decryption(p0700);
		myP0700 = WorkPlanUtil.decryption(myP0700);
		p0800 = WorkPlanUtil.decryption(p0800);
		p0723 = WorkPlanUtil.decryption(p0723);
		objectid = WorkPlanUtil.decryption(objectid);
		
		Map params = new HashMap();
		if (!"".equals(p0700)) {
			params.put("p0700", p0700);
		}
		if (!"".equals(p0800)) {
			params.put("p0800", p0800);
		}
		if (!"".equals(p0723)) {
			params.put("p0723", p0723);
		}
		if (!"".equals(objectid)) {
			params.put("objectid", objectid);
		}
		params.put("myP0700", myP0700);
		params.put("othertask", othertask);
		params.put("concerned_bteam", concerned_bteam);
		params.put("fromflag",fromflag);
		
		return params;
	}
	
	/**
	 * 查找可编辑字段及其可编辑状态
	 * @param params(p0700,p0800,p0723,objectid)
	 * @return 有总是可编辑字段和按钮触发编辑字段的map
	 * @throws Exception
	 */
	public Map getEditableFields(Map params) throws Exception {
		String p0800 = (String) params.get("p0800"); // 任务id
		String othertask = (String) params.get("othertask"); // 1是穿透任务
		int iP0800 = Integer.parseInt(p0800);
		int flag = 0;//1：是创建者或负责人或是上级登录；2：参与人或是上级且任务是其他人分配的；3：穿透节点
		//是否是上级登录
		int superiorEdit = isSuperiorEdit(params);
		Map editableFields = new HashMap();
		
		try {
			if("1".equals(othertask)){
				flag = findParentNode(p0800);
			}
			
			StringBuffer always = new StringBuffer(); // 总是可编辑的字段
			StringBuffer normal = new StringBuffer(); // 需要按钮触发编辑的字段
			
			RecordVo task = getTask(iP0800);
			if ("5".equals(task.getString("p0809")) ||
					WorkPlanConstant.TaskChangedStatus.Cancel == task.getInt("p0833")) { // 如果是取消状态的任务,也不允许修改
				return editableFields;
			}
				
			if (flag == 1 || isCreater(params) || isDirector(iP0800) || (superiorEdit>=1 && superiorEdit<=4)) { // 负责人或创建人或者是上级查看下级计划且任务不是取消状态
				always.append("P0835,director,member,follower,subTask,");
				normal.append("P0803,P0823,P0841,RANK,");
				
				// P08表中固定可编辑的字段
				normal.append("P0801,P0813,P0815,");
				
				// P08表中动态可编辑的字段
				List usedFields = DataDictionary.getFieldList("P08", Constant.USED_FIELD_SET); // 可用的字段
				for (Iterator iter = usedFields.iterator(); iter.hasNext();) {
					FieldItem item = (FieldItem) iter.next();
					
					if (item.isVisible() && WorkPlanConstant.TaskInfo.TASK_EXCLUDE_FIELD.indexOf(item.getItemid().toUpperCase()) == -1) {
						normal.append(item.getItemid()).append(",");
					}
				}
			} else if(flag == 2 || isMember(iP0800) || superiorEdit == 5) { // 参与人或是上级且任务是其他人分配的
				normal.append("P0841,RANK,subtask");
			}
			
			if (always.length() > 0) {
				editableFields.put("always", always.toString().toUpperCase());
			}
			if (normal.length() > 0) {
				editableFields.put("normal", normal.toString().toUpperCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return editableFields;
	}
	
	/**
	 * @author lis
	 * @Description: 当前节点是穿透节点时查找父节点，循环递归，直到不是穿透节点
	 * @date 2016-3-3
	 * @param params
	 * @return flag:1是上级，2是任务成员，0是穿透任务
	 * @throws GeneralException
	 */
	public int findParentNode(String p0800) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		Map tempParams = new HashMap();
		ResultSet rs = null;
		int flag = 0;//0：是顶级节点；1：是创建者或负责人或是上级登录；2：参与人或是上级且任务是其他人分配的；3：穿透节点
		try {
			String sql = "select p08.*,p07.*  from p08,p07 where p08.p0700=p07.p0700 and  p08.p0800=(select p0831 from p08 where p0800=?)";
			rs = dao.search(sql,Arrays.asList(p0800));
			if(rs.next()){
				String tempP0800 = rs.getString("p0800");
				int Ip0800 = Integer.valueOf(tempP0800);
				String p0700 = rs.getString("p0700");
				String p0723 = rs.getString("p0723");
				String objectid = "";
				if("1".equals(p0723)) {
                    objectid = rs.getString("nbase")+rs.getString("a0100");
                } else if("2".equals(p0723)) {
                    objectid = rs.getString("p0707");
                }
				tempParams.put("p0800", tempP0800);
				tempParams.put("p0700", p0700);
				tempParams.put("p0723", p0723);
				tempParams.put("objectid", objectid);
				//是否是上级登录
				int superiorEdit = isSuperiorEdit(tempParams);
				if(isCreater(tempParams) || isDirector(Ip0800) || (superiorEdit>=1 && 0<=4)){
					flag = 1;
				}
				else if(isMember(Ip0800) || superiorEdit == 5){// 参与人或是上级且任务是其他人分配的
					flag = 2;
				}
				else{//穿透节点
					if(!p0800.equals(tempP0800))//顶级节点不再递归
                    {
                        flag = findParentNode(tempP0800);
                    }
				}
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		
		return flag;
	}
	
	/**
	 * @author lis
	 * @Description: 当前节点是穿透节点时查找父节点，循环递归，直到不是穿透节点
	 * @date 2016-3-3
	 * @param params
	 * @return
	 * @throws GeneralException
	 */
	public Map findParentNodeP(String p0800) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		Map params = new HashMap();
		Map tempParams = new HashMap();
		ResultSet rs = null;
		int flag = 0;//0：是顶级节点；1：是创建者或负责人或是上级登录；2：参与人或是上级且任务是其他人分配的；3：穿透节点
		try {
			String sql = "select p08.*,p07.*  from p08,p07 where p08.p0700=p07.p0700 and  p08.p0800=(select p0831 from p08 where p0800=?)";
			rs = dao.search(sql,Arrays.asList(p0800));
			if(rs.next()){
				String tempP0800 = rs.getString("p0800");
				int Ip0800 = Integer.valueOf(tempP0800);
				String p0700 = rs.getString("p0700");
				String p0723 = rs.getString("p0723");
				String objectid = "";
				if("1".equals(p0723)) {
                    objectid = rs.getString("nbase")+rs.getString("a0100");
                } else if("2".equals(p0723)) {
                    objectid = rs.getString("p0707");
                }
				tempParams.put("p0800", tempP0800);
				tempParams.put("p0700", p0700);
				tempParams.put("p0723", p0723);
				tempParams.put("objectid", objectid);
				//是否是上级登录
				int superiorEdit = isSuperiorEdit(tempParams);
				if(isCreater(tempParams) || isDirector(Ip0800) || (superiorEdit>=1 && 0<=4)){
					params = tempParams;
				}
				else if(isMember(Ip0800) || superiorEdit == 5){// 参与人或是上级且任务是其他人分配的
					params = tempParams;
				}
				else{//穿透节点
					if(!p0800.equals(tempP0800))//顶级节点不再递归
                    {
                        findParentNode(tempP0800);
                    }
				}
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		
		return tempParams;
	}
	
	/**   
     * @Title: isSuperiorEdit  
     * @Description: 上级修改下级任务
     * @param @param nbase
     * @param @param a0100
     * @param @return   0:不是上级   1:是上级且下属本人创建的任务  2:是上级且上级分派的任务（上级本人计划中创建的）3:是上级且上级分派的任务（上级在其他下属计划中创建的） 4:是上级且上级创建的任务（在下属计划中创建）   5: 是上级且其他人分派的任务
     * @return String
     * @author:wusy   
     * @throws   
    */
	public int isSuperiorEdit(Map params){
		int superiorEdit = 0;
		String p0700 = (String) params.get("p0700");
		if(StringUtils.isBlank(p0700)){
			return superiorEdit;
		}
		
		if(this.fromflag!=null&& "hr_create".equals(this.fromflag)) {
            return 4;
        }
		
		String p0723 = (String) params.get("p0723");
		String p0800 = (String) params.get("p0800");
		String objectid = (String) params.get("objectid");
		WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
		//任务相关人员的主岗位
		String planPersonE01a1 = "";
		String planPersonID = "";
		if("1".equals(p0723)){//个人
			planPersonE01a1 = wpUtil.getMyMainE01a1(objectid.substring(0, 3), objectid.substring(3));
			planPersonID = objectid;
		}
		if("2".equals(p0723)){//团队
			planPersonE01a1 = wpUtil.getDeptLeaderE01a1(objectid);//根据人员id获取主岗位
			planPersonID = wpUtil.getFirstDeptLeaders(objectid);//根据部门id获取部门负责人id
		}
		//登陆人岗位列表
		String myE01a1s = wpUtil.getMyE01a1s(this.userView.getDbname(), this.userView.getA0100());
		//获取计划状态:发布,批准....
		RowSet rs = null;
		StringBuffer sbf = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		sbf.append("select p0719 from p07 where p0700 = ?");
		try {
			rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0700}));
			if(rs.next()){
				int createInWhoPlan = createInWhoPlan(params);
				//判断登陆人是否是任务相关人员的上级
				if(wpUtil.isMySubE01a1(myE01a1s, planPersonE01a1) && ("1".equals(rs.getString("p0719")) || "2".equals(rs.getString("p0719")))){
					if(createInWhoPlan == 1){//上级分派的任务（上级本人计划中创建的）
						superiorEdit = 2;
					}else if(createInWhoPlan == 2){//上级在下级计划中创建的任务
						superiorEdit = 3;
					}else if(createInWhoPlan == 3){//上级在其他下级计划中创建的任务
						superiorEdit = 4;
					}else{//其他人分派的任务
						superiorEdit = 5;
						if(isCreateByMe(params)){//下级创建的任务
							superiorEdit = 1;
						}
					}
				}
			}
			//formHM.put("superiorEdit", superiorEdit);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return superiorEdit;
	}
	/**
	 * 任务相关人员是否是该任务的创建者wusy
	 * 此方法只用于isSuperiorEdit,当上述条件都不满足后,走到此方法,才能判定是下级本人创建的任务
	 * @param p0800
	 * @param objectid
	 * @param p0723
	 * @return
	 */
	private boolean isCreateByMe(Map params){
		boolean b = false;
		String p0700 = (String) params.get("p0700");
		String p0800 = (String) params.get("p0800");

		if("".equals(p0800)){
			b = true;
		}else{
			try {
				RecordVo p08Vo = getTask(Integer.parseInt(p0800));
				if(p0700.equals(p08Vo.getString("p0700"))){
					b = true;
				}
				
				if(!b){
					WorkPlanUtil wutl = new WorkPlanUtil(conn, userView);
					String objectid = (String) params.get("objectid");
					String p0723 = (String) params.get("p0723");
					String usrName = "";
					if("1".equals(p0723)){
						usrName = wutl.getUserNameByA0100(objectid.substring(0, 3), objectid.substring(3));
					}else{
						usrName = wutl.getFirstDeptLeaders(objectid);
					}
					
					if(usrName.equals(p08Vo.getString("create_user"))){
						b = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return b;
	}
	
	/**
	 * 任务是否在上级(只限定登陆人)计划中创建  wusy
	 * 1:上级在自己计划中创建的任务
	 * 2:上级在下属计划中创建
	 * 3:上级在其他下属计划中创建
	 * @return
	 */
	private int createInWhoPlan(Map params){
		int who = 0;
		String p0700 = (String) params.get("p0700");
		String p0723 = (String) params.get("p0723");
		String p0800 = (String) params.get("p0800");
		String objectid = (String) params.get("objectid");
		WorkPlanUtil wpUtil =  new WorkPlanUtil(conn, userView);
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sbf = new StringBuffer();
		sbf.append("select p0700, create_user from p08 where p0800 = ?");
		RowSet rs = null;
		RowSet rset = null;
		try {
			rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0800}));
			if(rs.next()){
				if(this.userView.getUserName().equals(rs.getString("create_user"))){//上级本人创建的,p08表中存了任务创建人的用户名
					StringBuffer sbf2 = new StringBuffer();
					sbf2.append("select p0707, nbase, a0100 from p07 where p0700 = ?");
					rset = dao.search(sbf2.toString(), Arrays.asList(new Object[]{rs.getString("p0700")}));
					if(rset.next()){
						if(rset.getString("p0707")!=null && (wpUtil.isMyDept(rset.getString("p0707"))) || (this.userView.getDbname()+this.userView.getA0100()).equals(rset.getString("nbase")+rset.getString("a0100"))){
							//上级在自己的计划中创建的任务
							who = 1;
						}else if(("2".equals(p0723) && objectid.equals(rset.getString("p0707"))) || ("1".equals(p0723) && objectid.equals(rset.getString("nbase")+rset.getString("a0100")))){
							//上级在下属计划中创建
							who = 2;
						}else{
							//上级在其他下属计划中创建
							who = 3;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rset);
		}
		return who;
	}
	
	/**
	 * 其他人分配过来的任务 wusy
	 * @return
	 */
	/*private boolean isCreateByOther(Map params){
		boolean b = false;
		String p0700 = (String) params.get("p0700");
		String p0723 = (String) params.get("p0723");
		String p0800 = (String) params.get("p0800");
		String objectid = (String) params.get("objectid");
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		RowSet rs = null;
		StringBuffer sbf = new StringBuffer();
		sbf.append("select * from UsrA01 u left join P08 p on u.UserName = p.create_user where p.P0800 = ?");
		try {
			rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0800}));
			if(rs.next()){
				String myE01a1s = wpUtil.getMyE01a1s(this.userView.getDbname(), this.userView.getA0100());
				//判断登陆人是否是某人员的上级
				if(wpUtil.isMySubE01a1(myE01a1s, wpUtil.getMyMainE01a1(rs.getString("a0100")))){
					b = true;
				}
			}
			rs.close();
			rs = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}*/
	
	
	
	/**
	 * 如果当前用户不是任务创建人、负责人和参与人中的任何一种，返回false
	 * @param params 包含p0700,p0800,p0723,objectid
	 */
	public boolean isMyTask(Map params) throws Exception {
		if (isCreater(params)) {
			return true;
		}
		
		String p0800 = (String) params.get("p0800"); // 任务id
		
		String sql = "SELECT * FROM P09 WHERE p0905 IN (1,2) AND p0903=? AND P0901=2";
		RowSet rs = null;
		try {
			rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] {new Integer(p0800)}));
			while (rs.next()) {
				String nbase = rs.getString("nbase");
				String a0100 = rs.getString("a0100");
				
				if ((userView.getDbname() + userView.getA0100()).equals(nbase + a0100)) {
					return true;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return false;
	}
	
	/**
	 * 查看下级在任务中单人的角色,如果是创建人,负责人,参与人中的一种,就返回true    wusy
	 * @param params
	 * @return
	 */
	public boolean isSubCanEdit(Map params){
		boolean b = false;
		String p0800 = (String) params.get("p0800");
		String objectid = (String) params.get("objectid");
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sbf = new StringBuffer();
		sbf.append("select org_id, nbase, a0100 from per_task_map where flag in (1,2,5) and p0800 = ?");
		try {
			rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0800}));
			while(rs.next()){
				if((rs.getString("nbase") != null && rs.getString("a0100") != null) && objectid.equals(rs.getString("nbase")+ rs.getString("a0100"))){
					b = true;
				}else if(rs.getString("org_id") != null && objectid.equals(rs.getString("org_id"))){
					b = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return b;
	}
	
	/**
	 * 根据给定单位查找其负责人的岗位是否与当前用户是同一岗位
	 * @param p0707 单位id
	 * @return 如果是当前用户负责的单位
	 * @throws Exception
	 */
	public boolean isMyselfResponsibleUnit(String p0707) throws Exception {
		boolean rtValue = false;
		
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			String currUsrPosId = userView.getUserPosId(); // 当前用户的岗位id
			
			FieldItem b01ps = DataDictionary.getFieldItem(WorkPlanConstant.DEPTlEADERFld);
			if (b01ps != null) {
				// 查找计划指定单位的负责人所在岗位的id
				rs = dao.search("SELECT "+WorkPlanConstant.DEPTlEADERFld+" FROM B01 WHERE b0110='" + p0707 + "'");
				if (rs.next()) {
					if (currUsrPosId.equalsIgnoreCase(rs.getString(WorkPlanConstant.DEPTlEADERFld))) { // 是自己的团队计划
						rtValue = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		
		return rtValue;
	}
	
	/**
	 * 查看当前用户在任务中的身份
	 * @param p0800 任务id
	 * @return 1:负责人, 2:参与人(协办人), 5:创建人
	 * @throws Exception
	 */
	public String getMyselfPosition(String p0800) throws Exception {
		StringBuffer buf = new StringBuffer();
		
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT flag FROM per_task_map WHERE p0800=").append(p0800);
			sql.append(" AND nbase='").append(userView.getDbname()).append("'");
			sql.append(" AND a0100='").append(userView.getA0100()).append("'");
			
			rs = dao.search(sql.toString());
			while (rs.next()) {
				int flag = rs.getInt("flag");
				buf.append(flag).append(",");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * 根据可编辑字段的常量值来确定给定字段的可编辑状态
	 * @param editableFields 可编辑字段(已经按其可编辑状态分类存储)
	 * @return always:总是可编辑, normal:需要按钮触发, none:总是不可编辑
	 */
	public String getEditStatus(Map editableFields, String fieldName) {
		editableFields = editableFields == null ? Collections.EMPTY_MAP : editableFields;
		
		String always = (String) editableFields.get("always");
		String normal = (String) editableFields.get("normal");
		
		if (always != null && always.contains(fieldName.toUpperCase())) {
			return "always";
		} else if (normal != null && normal.contains(fieldName.toUpperCase())) {
			return "normal";
		} else {
			return "none";
		}
	}
	
	/**
	 * 任务详情的链接
	 * @param formHM 包含加密的p0700,p0723,objectid
	 * @param p0800 当前任务的id
	 * @return 链接
	 */
	public String getTaskUrl(Map formHM, String p0800) {
		StringBuffer url = new StringBuffer();
		url.append("/workplan/plan_task.do?br_task=link");
		url.append("&p0700=").append(formHM.get("p0700"));
		url.append("&p0800=").append(WorkPlanUtil.encryption(p0800));
		url.append("&objectid=").append(formHM.get("objectid"));
		url.append("&p0723=").append(formHM.get("p0723"));
		url.append("&returnurl=").append(formHM.get("returnurl"));
		
		return url.toString();
	}
	
	/** 根据nabase+a0100查找对应人员的姓名  修改不用为findByPrimaryKey --wusy*/
	public String getA0101(String objectId) {
		String a0101 = "";
		if(StringUtils.isBlank(objectId)){
			return a0101;
		}
		String dbname = objectId.substring(0, 3);
		String a0100 = objectId.substring(3);
		String sql = "select * from "+dbname+"a01 where A0100 = ?";
		RowSet rs = null;
		try {
			rs = new ContentDAO(conn).search(sql, Arrays.asList(a0100));
			if(rs.next()){
				a0101 =  rs.getString("a0101");
			}
			if(a0101 == null) {
                a0101 = "";
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return a0101;
	}
	
	/* ############################# 邮件正文模板 ################################ */
	/** 人员(负责人、参与人、关注人)设定模板 */
	public static String getTplOfStaffSetting() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好!<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#将您指定为 任务 #taskName# 的#position#，");
		buf.append("请在#endDate#之前完成此项任务。");
		
		return buf.toString();
	}
	
	/** 人员(负责人、参与人、关注人)设定模板(不含截止日期) */
	public static String getTplOfStaffSettingWithoutEndDate() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#将您指定为 任务 #taskName# 的#position#。");
		
		return buf.toString();
	}
	
	/** 人员(负责人、参与人)设定模板(不含截止日期)   参与人\关注人合并 wusy */
	public static String getTplOfStaffSettingWithoutEndDateMember(int len) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#给你分配了以下");
		buf.append(len+"项任务.<br/>");
		for(int i=0; i<len; i++){
			buf.append("&nbsp;&nbsp;&nbsp;&nbsp;"+(i+1));
			buf.append(". <a href=#href"+i+"#>");
			buf.append("#taskName"+i+ "#</a>。<br/>");
		}
		return buf.toString();
	}
	
	/** 人员(负责人、参与人、关注人)设定模板(不含截止日期)   关注人合并  wusy */
	public static String getTplOfStaffSettingWithoutEndDateFocus(int len) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#发布了");
		buf.append(len+"项任务.并将您添加为任务的关注人<br/>");
		for(int i=0; i<len; i++){
			buf.append("&nbsp;&nbsp;&nbsp;&nbsp;"+(i+1));
			buf.append(". <a href=#href"+i+"#>");
			buf.append("#taskName"+i+ "#</a>。<br/>");
		}
		return buf.toString();
	}
	
	/** 人员(负责人、参与人、关注人)移除模板 */
	public static String getTplOfStaffRemoving() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#已经将您从任务 #taskName# 的#position#名单中移除.");
		
		return buf.toString();
	}
	
	/** 任务取消模板 */
	public static String getTplOfTaskCanceling() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#已经取消了任务 #taskName#。");
		
		return buf.toString();
	}
	
	/** 设置、变更任务完成时间通知模板 */
	public static String getTplOfEndDateChange(boolean hasEndDate) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#已经调整了任务 #taskName# 的完成时间，");
		if (hasEndDate) {
			buf.append("请在#endDate#之前完成此项任务。");
		} else {
			buf.append("具体时间待定。");
		}
		
		return buf.toString();
	}
	
	/**
	 * @author lis
	 * @Description: 任务名称改变通知模板
	 * @date 2016-3-19
	 * @return
	 */
	public static String getTplOfTaskNameChange() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#已经将任务 #oldTaskName# 的名称修改为 #newTaskName#。");
		
		return buf.toString();
	}
	
	/**
	 * @author lis
	 * @Description: 任务权重改变通知模板
	 * @date 2016-3-19
	 * @return
	 */
	public static String getTplOfRankChange() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#已经将任务 #taskName# 的权重从 #oldRank# 调整为  #newRank#。");
		
		return buf.toString();
	}
	
	/** 任务评价邀请通知 */
	public static String getTplOfTaskEvaluation() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#object#已经完成了任务 #taskName#，特邀请对他的工作完成情况进行评价。<br />");
		buf.append("<br />#operator#");
		
		return buf.toString();
	}
	
	/** 任务报批模板 */
	public static String getTplOfSubmit() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好!<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#&nbsp;&nbsp;&nbsp;&nbsp;已经变更了任务 #taskName# ，提交了报批申请。请查阅任务变更情况并审批。");
		
		return buf.toString();
	}
	
	/** 任务批准模板 */
	public static String getTplOfApprove() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("#target#，您好<br />&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("#operator#已经批准了您的  #taskName# 任务变更申请。");
		
		return buf.toString();
	}
	
	/* ##################################################################### */
	
	/**
	 * 获取邮件的正文
	 * @param body 包含正文信息的bean
	 * @param tpl 模板
	 * @return 用body里的信息替换模板对应位置的标识符
	 */
	public static String getBodyText(LazyDynaBean body, String tpl) {
		String bodyText = tpl;
		
		Map attrs = body.getMap();
		for (Iterator iter = attrs.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			String value = (String) attrs.get(key) + "";
			
			bodyText = bodyText.replace("#" + key + "#", value);
		}
		
		return bodyText;
	}
	
	/** 已发布的任务才会发送邮件 */
	public void send(LazyDynaBean email, String taskStatus) {
		if ("02".equals(taskStatus) || "03".equals(taskStatus)) { // 报批或已批状态下发送邮件
			new AsyncEmailBo(conn, userView).send(email);
			new WorkPlanUtil(conn, userView).sendWeixinMessageFromEmail(email);
		}
	}
	
	/** 已发布的任务才会发送邮件 */
	public void send(List emails, String taskStatus) {
		if ("02".equals(taskStatus) || "03".equals(taskStatus)) { // 报批或已批状态下发送邮件
			new AsyncEmailBo(conn, userView).send(emails);
			new WorkPlanUtil(conn, userView).sendWeixinMessageFromEmail(emails);
		}
	}
	
	/** 发布任务后为任务每一位成员发送邮件
	 * @param params 解码后可以阅读的数据: { p0700, p0800, p0723, objectid }
	 * @param action set|cancel|endDate 设置相关责任人|取消任务|结束时间改变
	 */
	public void sendEmailsToAll(Map params, String action) {
		String p0800 = (String) params.get("p0800"); // 任务id
		int superiorEdit = 0;
		ArrayList targetNameList = new ArrayList();
		if("cancel".equals(action)){
			superiorEdit = Integer.parseInt(params.get("superiorEdit").toString());
		}
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		RowSet rst = null;
		try {
			StringBuffer sql = new StringBuffer();
			
			// 依次是：任务审批状态、任务名称、任务截止日期、成员标识、应用库、用户编号、用户姓名
			sql.append("SELECT P08.p0809,P08.p0811,P08.p0801,P08.p0815,P09.p0905,P09.nbase,P09.a0100,P09.p0913 FROM P09");
			sql.append(" LEFT JOIN P08 ON P09.p0903 = P08.p0800");
			sql.append(" WHERE P09.p0901=2 AND P09.p0903=").append(p0800);
			
			String taskStatus = "";
			
			rs = dao.search(sql.toString());
			List emails = new ArrayList();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			while (rs.next()) {
				taskStatus = rs.getString("p0811");
				String taskName = rs.getString("p0801") + "";
				Date endDate = rs.getDate("p0815");
				int p0905 = rs.getInt("p0905"); // 用户成员标识
				String nbase = rs.getString("nbase") + "";
				String a0100 = rs.getString("a0100") + "";
				if ((nbase + a0100).equals(userView.getDbname() + userView.getA0100())) { // 当前操作人无需邮件通知
					continue;
				}
				
				String fullName = rs.getString("p0913") + "";
				targetNameList.add(fullName);
				LazyDynaBean body = new LazyDynaBean();
				body.set("target", "".equals(fullName) ? getA0101(nbase + a0100) : fullName);
				body.set("operator", userView.getUserFullName());
				body.set("taskName", taskName);
				body.set("position", getPosition(p0905));
				String bodyText = "";
				String subject="任务提醒";
				if ("set".equals(action)) { // 设定任务相关责任人的邮件
					if (endDate != null) {
						body.set("endDate", sdf.format(endDate));
						bodyText += getBodyText(body, getTplOfStaffSetting());
					} else {
						bodyText += getBodyText(body, getTplOfStaffSettingWithoutEndDate());
					}
					subject=userView.getUserFullName()+"将您添加为任务 \""+taskName+"\" 的"+getPosition(p0905)+",请查看";
				} else if ("cancel".equals(action)) { // 取消任务
					bodyText += getBodyText(body, getTplOfTaskCanceling());
					subject=userView.getUserFullName()+"取消了任务 \""+taskName+"\"";
				} else if ("endDate".equals(action)) { // 结束时间被改变
					if (endDate != null) {
						body.set("endDate", sdf.format(endDate));
					}
					bodyText += getBodyText(body, getTplOfEndDateChange(endDate != null));
					subject=userView.getUserFullName()+"调整了任务 \""+taskName+"\" 的时间,请查看";
				} else if ("changeTaskName".equals(action)) { // 任务名称被改变
					String oldTaskName = (String)params.get("oldTaskName");//修改后的任务名称
					body.set("newTaskName", taskName);
					body.set("oldTaskName", oldTaskName);
					bodyText += getBodyText(body, getTplOfTaskNameChange());
					subject=userView.getUserFullName()+"修改了任务 \""+taskName+"\" 的名称,请查看";
				} else if ("changeRank".equals(action)) { // 任务权重被改变
					float newRank = (Float)params.get("newRank");
					float oldRank = (Float)params.get("oldRank");
					body.set("newRank", newRank == 0 ? "0" : ((String.valueOf(newRank).split("\\.")[0]) + "%"));
					body.set("oldRank", oldRank == 0 ? "0" : (String.valueOf(oldRank).split("\\.")[0] + "%"));
					bodyText += getBodyText(body, getTplOfRankChange());
					subject=userView.getUserFullName()+"修改了任务 \""+taskName+"\" 的权重,请查看";
				}
				
				LazyDynaBean email = new LazyDynaBean();
				email.set("objectId", nbase + a0100);
				email.set("subject", subject);
				email.set("bodyText", bodyText);
				email.set("bodySubject", "任务提醒");
				
				Map p = new HashMap();
				p.putAll(params);
				p.put("logonUser", nbase + a0100);
				p.put("objectid", nbase + a0100);
				email.set("href", getHref(p));

				emails.add(email);
			}
			
			/** 发送给父节点 邮件   start */
			
			sql.setLength(0);
			sql.append("SELECT P08.p0809,P08.p0811,P08.p0801,P08.p0815,P09.p0905,P09.nbase,P09.a0100,P09.p0913 FROM P09");
			sql.append(" LEFT JOIN P08 ON P09.p0903 = P08.p0800");
			sql.append(" WHERE P09.p0901=2  and P0905=1");
			sql.append(" and P0903=(select p0831 from P08 where P0800!=P0831 and P0800=?)");
			emails.addAll(getEamils(params, targetNameList, dao, sql.toString(), p0800, action));
			
			/** 发送给父节点 邮件   end */
			
			//上级取消任务,需要给任务创建人(从per_task_map表中拿)发邮件
			StringBuffer sbf = new StringBuffer();
			sbf.append("SELECT P08.p0809,P08.p0811,P08.p0801,P08.p0815,P08.create_user, ptm.org_id, ptm.nbase,ptm.a0100 FROM per_task_map ptm left join p08 on ptm.p0800 = p08.p0800");
			sbf.append(" where ptm.flag = 5 and p08.p0800 = ?");
			WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
			rst = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0800}));
			
			if("cancel".equals(action)&&(superiorEdit >= 1 && superiorEdit <= 4)){
				if(rst.next()){
					String nbase = rst.getString("nbase") == null ? "" : rst.getString("nbase") + "";
					String a0100 = rst.getString("a0100") == null ? "" : rst.getString("a0100") + "";
					String org_id = rst.getString("org_id") == null ? "" : rst.getString("org_id");
					String userid = "";
					String trueCreaterId = wpUtil.getUserId(rst.getString("create_user"));//真正的任务创建者id(p08中的人)
					if(nbase == null){
						userid = wpUtil.getFirstDeptLeaders(org_id);
						nbase = userid.substring(0, 3);
						a0100 = userid.substring(3);
					}
					String username = (String) wpUtil.getUserNamePassword(nbase, a0100).get("usrname");
					taskStatus = rst.getString("p0811");
					String taskName = rst.getString("p0801") + "";
					Date endDate = rst.getDate("p0815");
					//创建人不是当前登录人且p08表中的任务创建人(真实)跟per_task_map中获取的创建人不一样
					if (!((nbase + a0100).equals(userView.getDbname() + userView.getA0100())) && !(rst.getString("create_user").equals(username))) { // 当前操作人无需邮件通知
					LazyDynaBean body = new LazyDynaBean();
					body.set("target", getA0101(nbase + a0100));
					body.set("operator", userView.getUserFullName());
					body.set("taskName", taskName);
					body.set("position", "创建者");
					String bodyText = "";
					String subject="任务提醒";
					bodyText += getBodyText(body, getTplOfTaskCanceling());
					subject=userView.getUserFullName()+"取消了任务 \""+taskName+"\"";
					LazyDynaBean email = new LazyDynaBean();
					email.set("objectId", nbase + a0100);
					email.set("subject", subject);
					email.set("bodyText", bodyText);
					email.set("bodySubject", "任务提醒");
					
					Map p = new HashMap();
					p.putAll(params);
					p.put("logonUser", nbase + a0100);
					p.put("objectid", nbase + a0100);
					email.set("href", getHref(p));

					emails.add(email);
					}
				}
			}
			//下级取消上级在自己计划下创建的任务,需要通知上级
			//查下自己在per_task_map中身为创建者的任务,但p08表中对应的真实创建者不是自己,这条任务就是上级在我计划下创建的任务,取消时,应该通知上级
			
			if(rst.next()){
				String nbase = rst.getString("nbase") == null ? "" : rst.getString("nbase") + "";
				String a0100 = rst.getString("a0100") == null ? "" : rst.getString("a0100") + "";
				String org_id = rst.getString("org_id") == null ? "" : rst.getString("org_id");
				String userid = "";
				String trueCreaterId = wpUtil.getUserId(rst.getString("create_user"));//真正的任务创建者id(p08中的人)
				if(nbase.length() == 0){
					userid = wpUtil.getFirstDeptLeaders(org_id);
					nbase = userid.substring(0, 3);
					a0100 = userid.substring(3);
				}
				String username = (String) wpUtil.getUserNamePassword(nbase, a0100).get("username");
				if(this.userView.getUserName().equals(username) && !username.equals(rst.getString("create_user"))){
						taskStatus = rst.getString("p0811");
						String taskName = rst.getString("p0801") + "";
						Date endDate = rst.getDate("p0815");
						LazyDynaBean body = new LazyDynaBean();
						body.set("target", wpUtil.getUsrA0101(nbase, a0100));
						body.set("operator", userView.getUserFullName());
						body.set("taskName", taskName);
						body.set("position", "创建者");
						String bodyText = "";
						String subject="任务提醒";
						bodyText += getBodyText(body, getTplOfTaskCanceling());
						subject=userView.getUserFullName()+"取消了任务 \""+taskName+"\"";
						LazyDynaBean email = new LazyDynaBean();
						email.set("objectId", nbase + a0100);
						email.set("subject", subject);
						email.set("bodyText", bodyText);
						email.set("bodySubject", "任务提醒");
						Map p = new HashMap();
						p.putAll(params);
						p.put("logonUser", trueCreaterId);
						p.put("objectid", trueCreaterId);
						email.set("href", getHref(p));
	
						emails.add(email);
					}
			}
			send(emails, taskStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rst);
			PubFunc.closeDbObj(rs);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 获得发送对象
	 * @date 2016-2-29
	 * @param params 相关参数
	 * @param targetNameList 已发邮件姓名
	 * @param dao
	 * @param sql 
	 * @param p0800 任务id
	 * @param action
	 * @return
	 * @throws GeneralException
	 */
	private List getEamils(Map params,ArrayList targetNameList,ContentDAO dao,String sql,String p0800,String action) throws GeneralException{
		RowSet rs = null;
		List emails = new ArrayList();
		try {
			rs = dao.search(sql,Arrays.asList(p0800));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			while (rs.next()) {
				String taskStatus = rs.getString("p0811");
				String taskName = rs.getString("p0801") + "";
				Date endDate = rs.getDate("p0815");
				int p0905 = rs.getInt("p0905"); // 用户成员标识
				String nbase = rs.getString("nbase") + "";
				String a0100 = rs.getString("a0100") + "";
				if ((nbase + a0100).equals(userView.getDbname() + userView.getA0100())) { // 当前操作人无需邮件通知
					continue;
				}
				String fullName = rs.getString("p0913") + "";
				if(targetNameList.contains(fullName)) {
                    continue;
                }

				LazyDynaBean body = new LazyDynaBean();
				body.set("target", "".equals(fullName) ? getA0101(nbase + a0100) : fullName);
				body.set("operator", userView.getUserFullName());
				body.set("taskName", taskName);
				body.set("position", getPosition(p0905));
				String bodyText = "";
				String subject="任务提醒";
				if ("set".equals(action)) { // 设定任务相关责任人的邮件
					if (endDate != null) {
						body.set("endDate", sdf.format(endDate));
						bodyText += getBodyText(body, getTplOfStaffSetting());
					} else {
						bodyText += getBodyText(body, getTplOfStaffSettingWithoutEndDate());
					}
					 subject=userView.getUserFullName()+"将您添加为任务 \""+taskName+"\" 的"+getPosition(p0905)+",请查看";
				} else if ("cancel".equals(action)) { // 取消任务
					bodyText += getBodyText(body, getTplOfTaskCanceling());
					subject=userView.getUserFullName()+"取消了任务 \""+taskName+"\"";
				} else if ("endDate".equals(action)) { // 结束时间被改变
					if (endDate != null) {
						body.set("endDate", sdf.format(endDate));
					}
					bodyText += getBodyText(body, getTplOfEndDateChange(endDate != null));
					subject=userView.getUserFullName()+"调整了任务 \""+taskName+"\"的时间,请查看";
				} else if ("changeTaskName".equals(action)) { // 任务名称被改变
					String oldTaskName = (String)params.get("oldTaskName");//修改后的任务名称
					body.set("newTaskName", taskName);
					body.set("oldTaskName", oldTaskName);
					bodyText += getBodyText(body, getTplOfTaskNameChange());
					subject=userView.getUserFullName()+"修改了任务 \""+taskName+"\" 的名称,请查看";
				} else if ("changeRank".equals(action)) { // 任务权重被改变
					float newRank = (Float)params.get("newRank");
					float oldRank = (Float)params.get("oldRank");
					body.set("newRank", newRank == 0 ? 0 : ((String.valueOf(newRank).split("\\.")[0]) + "%"));
					body.set("oldRank", oldRank == 0 ? 0 : (String.valueOf(oldRank).split("\\.")[0] + "%"));
					bodyText += getBodyText(body, getTplOfRankChange());
					subject=userView.getUserFullName()+"修改了任务 \""+taskName+"\" 的权重,请查看";
				}
				
				LazyDynaBean email = new LazyDynaBean();
				email.set("objectId", nbase + a0100);
				email.set("subject", subject);
				email.set("bodyText", bodyText);
				email.set("bodySubject", "任务提醒");
				
				Map p = new HashMap();
				p.putAll(params);
				p.put("logonUser", nbase + a0100);
				p.put("objectid", nbase + a0100);
				email.set("href", getHref(p));

				emails.add(email);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return emails;
	}
	
	/** 发布任务后为任务每一位成员发送邮件
	 * @param params 解码后可以阅读的数据: { p0700, p0800, p0723, objectid }
	 * @param action set|cancel|endDate 设置相关责任人|取消任务|结束时间改变
	 */
	public void sendEmailsToAllSingle(Map params, String action) {
		String p0800 = (String) params.get("p0800"); // 任务id
		String snbase = (String) params.get("nbase");
		String sa0100 = (String) params.get("a0100");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			if(!"set".equals(action)){
				// 依次是：任务审批状态、任务名称、任务截止日期、成员标识、应用库、用户编号、用户姓名
				sql.append("SELECT P08.p0809,P08.p0811,P08.p0801,P08.p0815,P09.p0905,P09.nbase,P09.a0100,P09.p0913 FROM P09");
				sql.append(" LEFT JOIN P08 ON P09.p0903 = P08.p0800");
				sql.append(" WHERE P09.p0901=2 AND P09.p0903=").append(p0800);
				rs = dao.search(sql.toString());
			}else{
				sql.append("SELECT P08.p0809,P08.p0811,P08.p0801,P08.p0815,P09.p0905,P09.nbase,P09.a0100,P09.p0913 FROM P09 LEFT JOIN P08 ON P09.p0903 = P08.p0800 WHERE P09.p0901=2 AND P09.p0903=? AND Nbase = ? AND A0100=?");
				//sql.append("SELECT P08.p0811,P08.p0801 FROM  P08  WHERE P0800=?");
				rs = dao.search(sql.toString(), Arrays.asList(new Object[]{p0800, snbase, sa0100}));
			}
			String taskStatus = "";
			List emails = new ArrayList();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			while (rs.next()) {
				taskStatus = rs.getString("p0811");
				String taskName = rs.getString("p0801") + "";
				Date endDate = rs.getDate("p0815");
				int p0905 = rs.getInt("p0905"); // 用户成员标识
				String nbase = rs.getString("nbase") + "";
				String a0100 = rs.getString("a0100") + "";
				if ((nbase + a0100).equals(userView.getDbname() + userView.getA0100())) { // 当前操作人无需邮件通知
					continue;
				}
				String fullName = rs.getString("p0913") + "";
				LazyDynaBean body = new LazyDynaBean();
				body.set("target", "".equals(fullName) ? getA0101(nbase + a0100) : fullName);
				body.set("operator", userView.getUserFullName());
				body.set("taskName", taskName);
				body.set("position", getPosition(p0905));
				String bodyText = "";
				String subject="任务提醒";
				if ("set".equals(action)) { // 设定任务相关责任人的邮件
					if (endDate != null) {
						body.set("endDate", sdf.format(endDate));
						bodyText += getBodyText(body, getTplOfStaffSetting());
					} else {
						bodyText += getBodyText(body, getTplOfStaffSettingWithoutEndDate());
					}
					subject=userView.getUserFullName()+"将您添加为任务 \""+taskName+"\" 的"+getPosition(p0905)+",请查看";
				} else if ("cancel".equals(action)) { // 取消任务
					bodyText += getBodyText(body, getTplOfTaskCanceling());
					subject=userView.getUserFullName()+"取消了任务 \""+taskName+"\"";
				} else if ("endDate".equals(action)) { // 结束时间被改变
					if (endDate != null) {
						body.set("endDate", sdf.format(endDate));
					}
					bodyText += getBodyText(body, getTplOfEndDateChange(endDate != null));
					subject=userView.getUserFullName()+"调整了任务 \""+taskName+"\"的时间,请查看";
				}
				
				LazyDynaBean email = new LazyDynaBean();
				email.set("objectId", nbase + a0100);
				email.set("subject", subject);
				email.set("bodyText", bodyText);
				email.set("bodySubject", "任务提醒");
				
				Map p = new HashMap();
				p.putAll(params);
				p.put("logonUser", nbase + a0100);
				p.put("objectid", (String) params.get("objectid"));
				email.set("href", getHref(p));

				emails.add(email);
			}
			
			send(emails, taskStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/** 发布任务后为任务每一位成员发送邮件
	 * @param params 解码后可以阅读的数据: { p0700, p0800s, p0723, objectid }
	 * @param action set|cancel|endDate 设置相关责任人|取消任务|结束时间改变
	 */
	public void sendEmailsToAllMulti(Map params, String action) {
		List p0800s = (List) params.get("p0800s"); // 任务id
		String flag = (String) params.get("flag");
		String nbase = (String) params.get("nbase"); 
		String a0100 = (String) params.get("a0100"); 
		ContentDAO dao = new ContentDAO(conn);
		try {
			StringBuffer sql = new StringBuffer();
			String taskStatus = "";
			List emails = new ArrayList();
			LazyDynaBean body = new LazyDynaBean();
			body.set("target", getA0101(nbase + a0100));
			body.set("operator", userView.getUserFullName());
			String subject="任务提醒";
			LazyDynaBean email = new LazyDynaBean();
			String taskName = "";
			String bodyText = "";
			RowSet rs = null;
			// 依次是：任务审批状态、任务名称、任务截止日期、成员标识、应用库、用户编号、用户姓名
			//sql.append("SELECT P08.p0809,P08.p0811,P08.p0801,P08.p0815,P09.p0905,P09.nbase,P09.a0100,P09.p0913 FROM P09 LEFT JOIN P08 ON P09.p0903 = P08.p0800 WHERE P09.p0901=2 AND P09.p0903=? AND Nbase = ? AND A0100=?");
			sql.append("SELECT P08.p0811,P08.p0801 FROM  P08  WHERE P0800=?");
			boolean b = true;
			for(int i=0; i<p0800s.size(); i++){
				rs = dao.search(sql.toString(), Arrays.asList(new Object[]{p0800s.get(i)}));
				if (rs.next()) {
					taskStatus = rs.getString("p0811");
					taskName = rs.getString("p0801") + "";
					if(b && "member".equals(flag)){
						subject=userView.getUserFullName()+"将 \""+taskName+"\" 等"+p0800s.size()+"项任务分派给你,请查看";
						b = false;
					}
					if(b && "focus".equals(flag)){
						subject = userView.getUserFullName() + "将您添加为\"" + taskName + "\"等" +p0800s.size()+"项任务的关注人,请查看";
						b = false;
					}
					Map p = new HashMap();
					p.putAll(params);
					p.put("p0800", p0800s.get(i) +"");
					p.put("logonUser", nbase + a0100);
					p.put("objectid", nbase + a0100);
					body.set("href"+i, getHref(p));
					body.set("taskName" + i, taskName);
				}
			}
			if ("set".equals(action)) { // 设定任务相关责任人的邮件
				if("member".equals(flag)){
					bodyText += getBodyText(body, getTplOfStaffSettingWithoutEndDateMember(p0800s.size()));
				}
				if("focus".equals(flag)){
					bodyText += getBodyText(body, getTplOfStaffSettingWithoutEndDateFocus(p0800s.size()));
				}
			} 
			email.set("objectId", nbase + a0100);
			email.set("subject", subject);
			email.set("bodyText", bodyText);
			email.set("bodySubject", "任务提醒");
//			Map p = new HashMap();
//			p.putAll(params);
//			p.put("logonUser", nbase + a0100);
//			p.put("objectid", nbase + a0100);
//			email.set("href", getHref(p));

			emails.add(email);
			
			
			send(emails, taskStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 提醒邮件内查看任务的连接
	 * @param params 解码后可以阅读的数据: { p0700, p0800, p0723, objectid }
	 */
	public String getHref(Map params) throws Exception {
		String href = "";
		
		String p0700 = (String) params.get("p0700"); // 计划id
//		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String role = (String) params.get("role");	//计划已发布,指定任务关注人后发送邮件时候确定是关注人的标识
		String flag = (String) params.get("flag");	//计划没有发布,指定任务关注人发布计划后发送邮件确定是关注人的标识
		String p0800 = (String) params.get("p0800"); // 任务id
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		String logonUser = (String) params.get("logonUser"); // 登录人id: usr00000019
		logonUser = WorkPlanUtil.nvl(logonUser, "");
		if (logonUser.length() == 0) {
			throw new Exception("邮件接收方无效");
		}

		WorkPlanBo bo = new WorkPlanBo(conn, userView);
		RecordVo plan = getPlan(Integer.parseInt(p0700));
		
		RecordVo p07vo = null;
		if(("follower".equals(role)) || "focus".equals(flag)){
			//给关注人发邮件,跟关注人有没有计划没关系
			p07vo = plan;
		}else{
			// 定位到当前人的计划,如果与plan同一时间段没有计划，则新建一个
			if ("evaluate".equals(params.get("from"))) {
				p07vo = plan;
			} 
			else if ("approve".equals(params.get("from"))) {// 审批 给上级发邮件 wangrd 20141202
	            p07vo = plan;
	        }         
			else {
				p07vo = bo.getPeoplePlanVo(plan, objectid);
			}
		}
		
		if (p07vo != null) {
			href += bo.getRemindEmail_TaskHref(logonUser, p07vo, Integer.parseInt(p0800));
		}
		
		return href;
	}
	
	/** 查找任务中用户成员标识对应的文字描述，用于发送邮件 */
	public String getPosition(int flag) {
		switch (flag) {
			case 1 : return "负责人";
			case 2 : return "成员";
			case 3 : return "关注人";
			default : return "";
		}
	}
	
	/** 根据objectId查询人员:Usr00000009 */
	public RecordVo getPersonByObjectId(String objectId) throws Exception {
		if (objectId == null || objectId.length() < 4 || "null".equals(objectId)) {
			throw new Exception("错误的objectId: " + objectId);
		}
		
		String nbase = objectId.substring(0, 3);
		String a0100 = objectId.substring(3);
		
		return getRecordVo(nbase + "A01", "a0100", a0100);
	}
	
	/** 根据objectId查询员工，返回LazyDynaBean(id,name,unit,photo,email) */
	public LazyDynaBean getPersonBean(String objectId) throws GeneralException {
		LazyDynaBean bean = new LazyDynaBean();
		try {
			RecordVo vo = getPersonByObjectId(objectId);
			if (vo == null) {
				throw new Exception("未找到对应的人员，查询失败");
			}
			
			bean.set("id", WorkPlanUtil.encryption(objectId));
			bean.set("name", new WorkPlanUtil(conn, userView).getTruncateA0101(vo.getString("a0101")));
			bean.set("unit", AdminCode.getCodeName("UM", vo.getString("e0122")));
			
			// 邮箱
			String emailFld = new WorkPlanUtil(conn, userView).getEmailFld();
			bean.set("email", "".equals(emailFld) ? "" : vo.getString(emailFld.toLowerCase()));
	
			String photo = new WorkPlanBo(conn, userView).getPhotoPath(objectId.substring(0, 3), objectId.substring(3));
			bean.set("photo", photo);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return bean;
	}
	
	/** 当前用户是不是任务的创建人
	 * 
	 * @param params 包含p0700,p0800,p0723,objectId
	 * @deprecated replaced by <code>PlanTaskBo.isCreater(p0800)</code>
	 */
	public boolean isCreater(Map params) throws Exception {
		String p0800 = (String) params.get("p0800"); // 任务id
		if (p0800 == null || "".equals(p0800)) {
			return true;
		}
		/* 屏蔽 既然不建议用 此方法应该是有问题 就屏蔽吧 wangrd 2015-09-19
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		
		if ("1".equals(p0723)) {
			RecordVo task = getTask(Integer.parseInt(p0800));
			RecordVo plan = getPlan(task.getInt("p0700"));
			
			String creater = "";
			if (plan.getInt("p0723") == 1) {
				creater = plan.getString("nbase") + plan.getString("a0100");
			} else if (plan.getInt("p0723") == 2) {
				creater = new WorkPlanUtil(conn, userView).getFirstDeptLeaders(plan.getString("p0707"));
			}
			return (userView.getDbname() + userView.getA0100()).equals(creater);
		} else if ("2".equals(p0723)) {
			return new WorkPlanUtil(conn, userView).isMyDept(objectid);
		}
		return false;
		*/
		return isCreater(Integer.parseInt(p0800));
	}
	
	
	/** 当前用户是不是任务的创建人 */
	public boolean isCreater(int p0800) throws Exception {
		return isCreater(p0800, userView.getDbname() + userView.getA0100());
	}
	
	/** 指定用户是不是任务的创建人 */
	public boolean isCreater(int p0800, String objectId) throws Exception {
		WorkPlanUtil util = new WorkPlanUtil(conn, userView);
		
		try {
			RecordVo task = getTask(p0800);
			RecordVo srcPlan = getPlan(task.getInt("p0700")); // 任务所属计划
			
			String creater = "";
			int p0723 = srcPlan.getInt("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
			if (p0723 == 1) {
				creater = srcPlan.getString("nbase") + srcPlan.getString("a0100");
			} else if (p0723 == 2) {
				creater = util.getFirstDeptLeaders(srcPlan.getString("p0707"));
			}
			
			return objectId.equals(creater);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/** 判断指定人员是不是当前用户的上级 */
	public boolean isMySuperior(String superior) {
		return isMySuperior(superior, userView.getDbname() + userView.getA0100());
	}
	
	/** 判断指定人员是不是指定用户的上级 */
	public boolean isMySuperior(String superior, String me) {
		if (PlanTaskBo.isEmpty(superior) || PlanTaskBo.isEmpty(me)) {
			return false;
		}
		
		WorkPlanUtil util = new WorkPlanUtil(conn, userView);
		String allSuperiors = util.getMyAllSuperPerson(me, "1");
		
		return allSuperiors.contains(superior);
	}
	
	/** 判断指定元素是否为空 */
	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		} else if (o instanceof String) {
			return "".equals(((String) o).trim());
		} else if (o instanceof Collection) {
			return ((Collection) o).size() == 0;
		} else if (o instanceof Map) {
			return ((Map) o).size() == 0;
		}

		return false;
	}
	
	/** 当前用户是不是任务的负责人 */
	public boolean isDirector(int p0800) throws Exception {
		return _is(p0800, 1);
	}
	public boolean isDirector(int p0800, String objectId) throws Exception {
		return _is(p0800, 1, objectId);
	}
	
	/** 当前用户是不是任务的参与人 */
	public boolean isMember(int p0800) throws Exception {
		return _is(p0800, 2);
	}
	public boolean isMember(int p0800, String objectId) throws Exception {
		return _is(p0800, 2, objectId);
	}
	
	/** 当前用户是不是任务的关注人 */
	public boolean isFollower(int p0800) throws Exception {
		return _is(p0800, 3);
	}
	public boolean isFollower(int p0800, String objectId) throws Exception {
		return _is(p0800, 3, objectId);
	}
	
	/** 当前用户是不是任务的成员之一(负责人或参与人或关注人) */
	public boolean isStaff(Map params) throws GeneralException {
		String p0800 = (String) params.get("p0800"); // 任务id
		if (p0800 == null || "".equals(p0800)) {
			return false;
		}
		
		String sql = "SELECT * FROM P09 WHERE nbase=? AND a0100=? AND p0903=? AND P0901=2";
		RowSet rs = null;
		try {
			rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] {
				userView.getDbname(),
				userView.getA0100(),
				new Integer(p0800)
			}));
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	/** 判断是否是任务负责人或参与人或关注人,查询p9表 */
	private boolean _is(int p0800, int p0905, String objectId) throws GeneralException  {
		String sql = "SELECT * FROM P09 WHERE p0905=? AND p0903=? AND P0901=2";
		RowSet rs = null;
		try {
			rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] {new Integer(p0905), new Integer(p0800)}));
			while (rs.next()) {
				String nbase = rs.getString("nbase");
				String a0100 = rs.getString("a0100");
				
				if ((objectId).equals(nbase + a0100)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	/** 判断是否是任务负责人或参与人或关注人,查询p9表 */
	private boolean _is(int p0800, int p0905) throws GeneralException {
		return _is(p0800, p0905, userView.getDbname() + userView.getA0100());
	}
	
	/** 根据计划状态、任务审批状态及当前用户查询任务的变更状态 */
	public LazyDynaBean taskChangedStatus(Map params) throws Exception {
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		String objectId = (String) params.get("objectid"); // 计划所有者
		String p0723 = (String) params.get("p0723");
		int iP0700 = "".equals(p0700) ? 0 : Integer.parseInt(p0700);
		int iP0800 = "".equals(p0800) ? 0 : Integer.parseInt(p0800);
		
		RecordVo plan = getPlan(iP0700);
		RecordVo task = getTask(iP0800);
		
		// 任务变更状态: 0=未变更, 1=新增, 2=已取消, 3=已变更, 4=其他修改
		LazyDynaBean bean = new LazyDynaBean();
		int taskChangedStatus = task.getInt("p0833"); // 任务变更状态
		String approveBtn = null; // 任务审批按钮的文字
		switch (plan.getInt("p0719")) { // 计划状态
			case WorkPlanConstant.PlanApproveStatus.Draft: { // 起草状态
				taskChangedStatus = -1;
				break;
			}
			case WorkPlanConstant.PlanApproveStatus.Pass: { // 已批准
				String p0811 = task.getString("p0811"); // 任务审批状态
				if (WorkPlanConstant.TaskStatus.DRAFT.equals(p0811)) { // 起草
					if (isCreater(iP0800) || isDirector(iP0800)) { // 报批按钮可见
						approveBtn = "submit";
					}
				} else if (WorkPlanConstant.TaskStatus.APPROVE.equals(p0811)) { // 待批准
					WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
					wpbo.initPlan(iP0700);
					String usrId = objectId;
					if("2".equals(p0723)){
						usrId = new WorkPlanUtil(conn, userView).getFirstDeptLeaders(objectId);
					}
					if (wpbo.isMyDirectSubTeamPeople() && (isCreater(iP0800, usrId) || isDirector(iP0800, usrId))) {
						approveBtn = "approve";
					}
				} else if (WorkPlanConstant.TaskStatus.APPROVED.equals(p0811)) { // 已批准
					if ((isCreater(iP0800) || isDirector(iP0800))
							&& (taskChangedStatus == WorkPlanConstant.TaskChangedStatus.Changed
								|| taskChangedStatus == WorkPlanConstant.TaskChangedStatus.Cancel)
							&& !WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(task.getString("p0809"))) {
						approveBtn = "submit";
					}
				}
				
				break;
			}
			default:;
		}
		
		if (!"".equals(taskChangedDesc(taskChangedStatus))) {
			bean.set("taskChangedDesc", taskChangedDesc(taskChangedStatus));
		}
		if (approveBtn != null) {
			bean.set("approveBtn", approveBtn);
		}
		bean.set("taskChangedStatus", taskChangedStatus + "");
		
		return bean;
	}
	
	/** 任务变更状态对应的文字描述 */
	private String taskChangedDesc(int s) {
		switch (s) {
			case WorkPlanConstant.TaskChangedStatus.Normal: return "未变更";
			case WorkPlanConstant.TaskChangedStatus.add: return "新增";
			case WorkPlanConstant.TaskChangedStatus.Cancel: return "已取消";
			case WorkPlanConstant.TaskChangedStatus.Changed: return "已变更";
//			case WorkPlanConstant.TaskChangedStatus.OtherChanged: return "其它修改";
			default: return "";
		}
	}
	
	/** 任务状态迁移 */
	public void transit(Map params) throws Exception {
		ContentDAO dao = new ContentDAO(conn);
		
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		
		int iP0700 = "".equals(p0700) ? 0 : Integer.parseInt(p0700);
		int iP0800 = "".equals(p0800) ? 0 : Integer.parseInt(p0800);
		
		RecordVo plan = getPlan(iP0700);
		RecordVo task = getTask(iP0800);
		
		if (plan == null || task == null) {
			return;
		}
		
		// 只有已批准的计划才涉及到任务状态的迁移
		if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.Pass) {
			String p0811 = task.getString("p0811"); // 任务审批状态
			int taskChangedStatus = task.getInt("p0833"); // 任务变更状态
			
			WorkPlanUtil util = new WorkPlanUtil(conn, userView);
			
			if (WorkPlanConstant.TaskStatus.DRAFT.equals(p0811)) { // 起草
				if (isCreater(iP0800) || isDirector(iP0800)) { // 创建人和负责人有权限报批
					task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVE);
					dao.updateValueObject(task);

					sendEmailsToAll(params, "set");
					
					String superior = util.getMyApprovedSuperPerson(userView.getDbname(), userView.getA0100());
					sendTaskApproveEmail(params, superior, 1);
				}
			} else if (WorkPlanConstant.TaskStatus.APPROVE.equals(p0811)) { // 批准操作：待批准 → 已批准
				WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
				wpbo.initPlan(iP0700);
				if (wpbo.isMyDirectSubTeamPeople()) {
					task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
					if (task.getInt("p0833") != WorkPlanConstant.TaskChangedStatus.Cancel) {
						task.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
					} else {
						task.setString("p0809", WorkPlanConstant.TaskExecuteStatus.CANCEL);
					}
					dao.updateValueObject(task);
					
					String target = objectid;
					if ("2".equals(params.get("p0723"))) { // 部门计划时，邮件接收者为部门负责人
						target = util.getFirstDeptLeaders(objectid);
					}
					sendTaskApproveEmail(params, target, 2);
				}
			} else if (WorkPlanConstant.TaskStatus.APPROVED.equals(p0811)) { // 报批操作：已批准 → 待批准
				if ((taskChangedStatus == 2 || taskChangedStatus == 3) && (isCreater(iP0800) || isDirector(iP0800))) {
					task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVE);
					dao.updateValueObject(task);
					String logcontent = "提交了任务变更申请";
					new WorkPlanOperationLogBo(conn, userView).addLog(Integer.parseInt(p0800), logcontent);
					String superior = util.getMyDirectSuperPerson(userView.getDbname(), userView.getA0100());
					sendTaskApproveEmail(params, superior, 1);
				}
			}
		}
	}
	
	/**
	 * 任务审批状态在报批与批准之间转换时，发送邮件
	 * @param params p0700,p0800,objectid
	 * @param target 接收者的编号
	 * @param type 报批邮件还是批准邮件(1=报批, 2=批准)
	 */
	private void sendTaskApproveEmail(Map params, String target, int type) throws Exception {
		if (target == null || "".equals(target)) { // 没有上级无需发送邮件
			return;
		}
		
		String p0800 = (String) params.get("p0800"); // 任务id
		
		RecordVo task = getTask(Integer.parseInt(p0800));
		String tpl = type == 1 ? PlanTaskBo.getTplOfSubmit() : PlanTaskBo.getTplOfApprove();
		
		LazyDynaBean body = new LazyDynaBean();
		body.set("target", getA0101(target));
		body.set("operator", userView.getUserFullName());
		body.set("taskName", task.getString("p0801"));
		String bodyText = PlanTaskBo.getBodyText(body, tpl);
		String subject = type == 1 ? 
				userView.getUserFullName()+"提交了任务\""+task.getString("p0801")+"\"的变更申请，请批准 ": 
				userView.getUserFullName()+"批准了您的\""+task.getString("p0801")+"\"任务变更申请 ";
		
		LazyDynaBean email = new LazyDynaBean();
		email.set("objectId", target);
		String bodySubject = type == 1 ? "任务变更报批提醒" : "任务变更审批提醒";
		email.set("subject", subject);
		email.set("bodyText", bodyText);
		email.set("bodySubject", bodySubject);
		
		params.put("logonUser", target); // 需要将id设成接收者的id
		params.put("from", "approve");
		email.set("href", getHref(params));
		send(email, task.getString("p0811"));
	}
	
	/** 新增一个子任务,返回新增的子任务 */
	public RecordVo addSubtask(Map params) throws Exception {
		ContentDAO dao = new ContentDAO(conn);
		IDGenerator idg = new IDGenerator(2, conn); // id生成器
		WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
		WorkPlanBo bo = new WorkPlanBo(conn, userView);
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String p0800 = (String) params.get("p0800"); // 任务id
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		//String superiorEdits =  (String) params.get("superiorEdit");
		String taskName = params.get("taskName")!= null ? (String) params.get("taskName") : "";  // 子任务名称
		String p0813 = params.get("p0813") != null  ? (String) params.get("p0813") : ""; // 开始时间
		String p0815 = params.get("p0815") != null  ? (String) params.get("p0815") : ""; // 结束时间
		String director = params.get("director")!= null ? (String) params.get("director") : "";  // 负责人id
		String othertask = params.get("othertask")!= null ? (String) params.get("othertask") : "";//父级是否是穿透任务
		
		//区分部门和个人登录情况
		String usrId = "";
		String deptName = "";
		if("1".equals(p0723)){
			usrId = objectid;
		}
		if("2".equals(p0723)){
			usrId = wpUtil.getFirstDeptLeaders(objectid);
			if(StringUtils.isBlank(usrId)){
				throw new Exception("该部门没有负责人,不可添加任务！");
			}
			deptName = wpUtil.getOrgDesc(objectid)+"的";
		}
		
		director = director == null || "".equals(director) ? usrId : director;
		Float rank = params.get("rank") == null || "".equals(params.get("rank")) ? null : Float.valueOf((String) params.get("rank")); // 权重
		String createUserFullName = this.userView.getUserFullName();
		String createUser = this.userView.getUserName();
		boolean isEditSub = false;
		
		//如果usrId和登陆人id不相符,上级在查看下级计划
		if(!usrId.equals(this.userView.getDbname()+this.userView.getA0100())){
			createUser = (String) wpUtil.getUserNamePassword(usrId.substring(0, 3), usrId.substring(3)).get("username");
			createUserFullName = wpUtil.getUsrA0101(usrId.substring(0, 3), usrId.substring(3));
			isEditSub = true;
		}
		
		int superiorEdit = isSuperiorEdit(params);
		//superiorEdit: 1:是上级且下属本人创建的任务  2:是上级且上级分派的任务（上级本人计划中创建的）3:是上级且上级分派的任务（上级在其他下属计划中创建的） 4:是上级且上级创建的任务（在下属计划中创建）
		if(superiorEdit >=1 && superiorEdit <=4){
			//上级在下级计划中创建任务,org_id,nbase,a0100要保存成下级的,create_user还是保存当前登录用户
			if("2".equals(p0723)){
				director = wpUtil.getFirstDeptLeaders(objectid);//负责人  lis 21060628
				//director = wpUtil.getFirstDeptLeaders(objectid);
			}else{
				director = director;//负责人  lis 21060628
				//director = objectid;
			}
//			createUserFullName = wpUtil.getUsrA0101(director.substring(0, 3), director.substring(3));
//			createUser = wpUtil.getUserName(director.substring(0, 3), director.substring(3));
		}
		
		RecordVo plan = getPlan(Integer.parseInt(p0700));
		if (plan == null) {
			return null;
		}
		//superiorEdit: 1:是上级且下属本人创建的任务  2:是上级且上级分派的任务（上级本人计划中创建的）3:是上级且上级分派的任务（上级在其他下属计划中创建的） 4:是上级且上级创建的任务（在下属计划中创建）
		//我是上级
		if(((superiorEdit >= 1 && superiorEdit <= 4) && isSubCanEdit(params)) || isEditSub){
			//是上级,获得下级在此任务中的权限,如果下级能动,上级就能动
		}else{
			if (!isMyTask(params)) {//othertask,是穿透任务时不判断权限
				throw new Exception("您没有添加子任务的权限！");
			}
		}
		
        //检查任务是否重名 
		PlanTaskTreeTableBo treeBo = new PlanTaskTreeTableBo(conn, Integer.parseInt(p0700));
        if (treeBo.taskNameIsRepeated(p0800, "",taskName)){            
            throw new GeneralException("已存在同名任务,不能保存！");
        }
		
		/** ##################################### p08 ####################################### */
		RecordVo subtask = new RecordVo("p08");
		
		// 验证任务起止时间是否合逻辑
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date dP0813 = null; // 开始
		Date dP0815 = null; // 结束
		if (p0813 != null && p0813.length() > 0) {
		    try{
		        dP0813 = format.parse(p0813);
		    }
		    catch (Exception e){
		        throw new Exception("开始日期格式不正确！");  
		    }
			subtask.setDate("p0813", dP0813);
		}
		if (p0815 != null && p0815.length() > 0) {
		    try{
		        dP0815 = format.parse(p0815);
            }
            catch (Exception e){
                throw new Exception("结束日期格式不正确！");  
            }
			
			subtask.setDate("p0815", dP0815);
		}
		if (dP0813 != null && dP0815 != null && dP0813.after(dP0815)) { // 开始结束日期都不为空且结束日期早于开始日期，抛异常
			throw new Exception("开始日期大于结束日期, 保存失败！");
		}

		String id_p08 = idg.getId("P08.P0800");
		subtask.setInt("p0800", Integer.parseInt(id_p08));
		subtask.setInt("p0700", Integer.parseInt(p0700));
		subtask.setString("p0801", taskName);
		subtask.setString("p0809", "1");
		
		// 计划处于报批状态，则新增的子任务默认是报批状态
		if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.HandIn) {
			subtask.setString("p0811", WorkPlanConstant.TaskStatus.APPROVE);
		} else {
			subtask.setString("p0811", WorkPlanConstant.TaskStatus.DRAFT);
		}
//		if(isEditSub){
//			subtask.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
//		}
		if((superiorEdit >=1 && superiorEdit <= 4) || isEditSub){
			RecordVo p07V0 = getPlan(Integer.parseInt(p0700));
			if(p07V0.getInt("p0719") == 2){
				subtask.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
				subtask.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
			}
		}else{
			// 起草中的计划，新增子任务的变更状态是未变更
			if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.Draft) {
				subtask.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
			} else {
				subtask.setInt("p0833", WorkPlanConstant.TaskChangedStatus.add);
			}
		}
		
		
		
		// 如果当前用户是公司最高领导(没有上级)，则新增的任务默认为已报批,未变更
		if (isTopLeader(objectid, p0723)) {
			// 计划处于已批准的状态
			if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.Pass) {
				subtask.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
				subtask.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
			}
		}
		
		String p0831 = "".equals(p0800) ? id_p08 : p0800; // 父任务号
		subtask.setInt("p0831", Integer.parseInt(p0831));
		subtask.setInt("p0823", 1);
		subtask.setInt("p0835", 0);
		subtask.setDate("create_time", new Date());
		subtask.setString("create_fullname", userView.getUserFullName());
		subtask.setString("create_user", userView.getUserName());
		subtask.setInt("p0845", 0);//非协作任务

		dao.addValueObject(subtask);
		//新建任务后,更新操作日志wusy
		String content = "创建了任务";
		new WorkPlanOperationLogBo(conn, userView).addLog(Integer.parseInt(id_p08), content);
		//上级修改下级任务,给下级发送提醒邮件
		if(isEditSub || (superiorEdit >=1 && superiorEdit <=4)){
			ArrayList list= new ArrayList();
			String subNbase = usrId.substring(0, 3);
			String subA0100 = usrId.substring(3);
			String plan_title = wpUtil.getPlanPeriodDesc(plan.getInt("p0725")+"", plan.getInt("p0727")+"", plan.getInt("p0729")+"", plan.getInt("p0731")+"");
			String subject= "    " + this.userView.getUserFullName()+"在您的"+deptName+plan_title+"工作计划新增了任务,请查看";
            String bodyText=bo.getRemindSubEmail_BodyText(wpUtil.getUsrA0101(subNbase, subA0100),deptName,plan_title, taskName);  
            String href = bo.getRemindSubEmail_PlanHref(subNbase,subA0100, objectid, "", p0723, plan.getInt("p0725")+"", plan.getInt("p0727")+"", plan.getInt("p0729")+"", plan.getInt("p0731")+"", true);  
            LazyDynaBean emailBean = bo.getEmailBean(subNbase+subA0100,subject, bodyText, href,"去查看计划");                   
            emailBean.set("bodySubject", "新增任务提醒");
            list.add(emailBean);
            AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userView);         
            emailBo.send(list);
            //发送微信
            wpUtil.sendWeixinMessageFromEmail(list);
		}
		// 清除当前任务一条线上的所有权重
		params.put("p0800", subtask.getString("p0800"));
		if (rank!= null && rank.floatValue() != 0) {
			clearBranchRank(Integer.parseInt(p0700), subtask.getInt("p0800"));
			String clearIDs = getClearRankTaskIds(Integer.parseInt(p0700),  subtask.getInt("p0800"));
			params.put("clearIDs", clearIDs);
		}
		
		/* ################################ per_task_map表: 创建人 ################################ */
		//分配任务到部门的部门id				wusy
		
		RecordVo builder_map = new RecordVo("per_task_map");
		String id_builder_map = idg.getId("per_task_map.id");
		builder_map.setInt("id", Integer.parseInt(id_builder_map));
		builder_map.setInt("p0800", Integer.parseInt(id_p08));
		builder_map.setInt("flag", 5);
		builder_map.setObject("rank", rank == null ? rank : Float.valueOf(rank.floatValue() / 100));
		builder_map.setInt("p0700", Integer.parseInt(p0700));
		builder_map.setDate("create_time", new Date());
		builder_map.setString("create_user", createUser);
		builder_map.setString("create_fullname", createUserFullName);
		if ("2".equals(p0723)) { // 团队计划
			builder_map.setInt("seq", treeBo.getSeq(objectid, Integer.parseInt(p0831),2));
			builder_map.setString("org_id", objectid);
			// 任务分解到部门,增加字段wusy
			builder_map.setInt("belongflag", Integer.parseInt(p0723));
		} else {
			builder_map.setInt("seq", treeBo.getSeq(objectid, Integer.parseInt(p0831),1));
			builder_map.setString("nbase", objectid.substring(0, 3));
			builder_map.setString("a0100", objectid.substring(3));
			
			// 任务分解到部门,增加字段wusy
			builder_map.setInt("belongflag", Integer.parseInt(p0723));
		}
		dao.addValueObject(builder_map);
		//部门计划：需要判断当前部门负责是否有个人计划
		WorkPlanBo planBo= new WorkPlanBo(this.conn,this.userView);
		planBo.initPlan(Integer.parseInt(p0700));
		if("2".equals(p0723)){
		    planBo.addPlan(planBo.getP07_vo(),usrId.substring(0, 3)+usrId.substring(3));
        }
		
		/* ################################ per_task_map表: 负责人 ################################ */
		// 团队的任务(需要在该人员个人计划下再创建一条)或者个人任务且负责人不是本人(指定了别的负责人)，则新建一条负责人记录
		String b0110 = "";
		if ("2".equals(p0723) || ("1".equals(p0723) && !director.equalsIgnoreCase(usrId))) {
			RecordVo director_map = new RecordVo("per_task_map");
//			String id_director_map = idg.getId("per_task_map.id");
//			director_map.setInt("id", Integer.parseInt(id_director_map));
			director_map.setInt("p0800", Integer.parseInt(id_p08));
			director_map.setInt("p0700", Integer.parseInt(p0700));
			director_map.setInt("seq", treeBo.getSeq(director, Integer.parseInt(p0831), 1));
			director_map.setDate("create_time", new Date());
			director_map.setString("create_user", createUser);
			director_map.setString("create_fullname", createUserFullName);
			director_map.setInt("flag", 1);
			director_map.setString("nbase", director.substring(0, 3));
			director_map.setString("a0100", director.substring(3));
			director_map.setDouble("rank", 0.0);
			String nbase = director.substring(0,3);
			String a0100 = director.substring(3);
			if(!director.equalsIgnoreCase(usrId)){
				b0110 = new WorkPlanUtil(conn, userView).getFristMainDept(nbase, a0100);
				director_map.setInt("belongflag", Integer.parseInt(p0723));//部门计划
				if(!"".equals(b0110)){
					director_map.setString("org_id", b0110);
				}
			}
			director_map.setInt("belongflag", Integer.parseInt(p0723));
			String id_director_map = idg.getId("per_task_map.id");
			director_map.setInt("id", Integer.parseInt(id_director_map));
			director_map.setInt("dispatchflag", 0);
			dao.addValueObject(director_map);
			//分派任务到部门
			if(!"".equals(b0110)){
					addDeptTask(b0110, p0700, id_p08,p0723);
			}
			planBo.addPlan(planBo.getP07_vo(),director);
		}
		
		/* ################################ P09表: 负责人 ################################ */
		RecordVo director_p09 = new RecordVo("p09");
		String id_p09 = idg.getId("P09.P0900");
		director_p09.setInt("p0900", Integer.parseInt(id_p09));
		//如果是任务分配到部门,p09中需要插入org_id
		if(!"".equals(b0110)){
			director_p09.setString("org_id", b0110);
		}
		director_p09.setInt("p0901", 2);
		director_p09.setInt("p0903", Integer.parseInt(id_p08));
//		if(isEditSub){
//			director = usrId;
//		}
		director_p09.setString("nbase", director.substring(0, 3));
		director_p09.setString("a0100", director.substring(3));
		
		director_p09.setInt("p0905", 1);
	
		RecordVo a01 = new RecordVo(director.substring(0, 3) + "A01");
		a01.setString("a0100", director.substring(3));
		a01 = dao.findByPrimaryKey(a01);
		director_p09.setString("p0907", a01.getString("b0110"));
		director_p09.setString("p0909", a01.getString("e0122"));
		director_p09.setString("p0911", a01.getString("e01a1"));
		director_p09.setString("p0913", a01.getString("a0101"));

		dao.addValueObject(director_p09);
		
		if(isEditSub){//上级查看下级 创建协作任务时，发送协办申请
			WorkPlanBo workPlanBo = new WorkPlanBo(this.conn, this.userView);
			workPlanBo.SuperiorOperation(Integer.parseInt(id_p08));
		}
		
		return subtask;
	}
	
	/** 删除任务 */
	public void delete(Map params) throws Exception {
		String p0800 = (String) params.get("p0800"); // 任务id
		RecordVo task = getTask(Integer.parseInt(p0800));
		
		if (!WorkPlanConstant.TaskStatus.DRAFT.equals(task.getString("p0811"))) {
			throw new Exception("只有起草状态的任务可以删除");
		}
		
		ContentDAO dao = new ContentDAO(conn);
		dao.deleteValueObject(task); // 删除P08
		
		String del_p09 = "DELETE FROM P09 WHERE p0901=2 AND p0903=?";
		String del_map = "DELETE FROM per_task_map WHERE p0800=?";
		dao.delete(del_p09.toString(), Arrays.asList(new Object[] {new Integer(task.getInt("p0800"))})); // P09
		dao.delete(del_map.toString(), Arrays.asList(new Object[] {new Integer(task.getInt("p0800"))})); // per_task_map
	}
	
	/*移除部门计划wusy*/
	public void delDeptTask(String p0800, String org_id){
		ContentDAO dao = new ContentDAO(conn);
		String sql1 = "delete from per_task_map where org_id = ? and p0800 = ? and dispatchFlag = 1";
		String sql2 = "update 	per_task_map set org_id = null where p0800 = ? and org_id = ?";
		String sql3 = "update P09 set Org_id = null where P0903 = ? and org_id = ?";
		try {
			dao.delete(sql1, Arrays.asList(new Object[]{org_id,Integer.parseInt(p0800)}));
			dao.update(sql2, Arrays.asList(new Object[]{Integer.parseInt(p0800),org_id}));
			dao.update(sql3, Arrays.asList(new Object[]{Integer.parseInt(p0800), org_id}));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/** 取消任务 */
	public void cancel(Map params) throws Exception {
		
		// p0809 任务状态(1:未开始, 2:进行中, 3:完成, 4:暂缓, 5:取消)
		// p0811 审批状态(01:起草, 02:已报批, 03:已批)
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		if (p0800 == null || "".equals(p0800) || p0700 ==null || "".equals(p0700)) {
			return;
		}
		
		RecordVo plan = getPlan(Integer.parseInt(p0700));
		RecordVo task = getTask(Integer.parseInt(p0800));

		String p0723 = plan.getString("p0723");
		String objectid = "";
		if ("1".equals(p0723)) {
			objectid = plan.getString("nbase") + plan.getString("a0100");
		} else if ("2".equals(p0723)) {
			objectid = plan.getString("p0707");
		}
		
		if (!WorkPlanConstant.TaskStatus.DRAFT.equals(task.getString("p0811"))) {
			ContentDAO dao = new ContentDAO(conn);
			task.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Cancel);
			// 首先不是公司领导(没有上级)发起的操作，才涉及到变更状态的切换
			if (isTopLeader(objectid, p0723)) {
				task.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
				task.setString("p0809", WorkPlanConstant.TaskExecuteStatus.CANCEL);
			}
			
			dao.updateValueObject(task);
			
			// 处理当前任务之下所有的后代任务
			List ids = getAllSubTaskIDs(task.getInt("p0800"));
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE P08 set p0833=? WHERE p0800 IN (0");
			if (ids.size()>0){
			    sql.append(",");
			    sql.append(WorkPlanUtil.join(ids)).append("");
			}
			sql.append(")");
			dao.update(sql.toString(), Arrays.asList(new Object[] {
					Integer.valueOf(WorkPlanConstant.TaskChangedStatus.Cancel)
			}));
			//清除取消任务的权重
			ids.add(task.getInt("p0800"));
			StringBuffer sqlsbf = new StringBuffer();
			sqlsbf.append("update per_task_map set rank = 0 where p0800 = ?");
			for(int i=0; i<ids.size(); i++){
				List list = new ArrayList();
				list.add(ids.get(i));
				dao.update(sqlsbf.toString(), list);
			}
		} else {
			throw new Exception("不满足任务取消的条件，取消失败");
		}
	}
	
	/** 对基本数据类型或简单数据类型进行比较 */
	public static boolean equal(Object obj, Object another) {
		if (obj == another) {
			return true;
		}
		
		if (obj == null || another == null) {
			return false;
		}
		
		if (obj instanceof Number && another instanceof Number) {
			Number _obj = (Number) obj;
			Number _another = (Number) another;
			
			return _obj.doubleValue() == _another.doubleValue();
		} else if (obj instanceof String && another instanceof String) {
			String _obj = (String) obj;
			String _another = (String) another;
			
			return _obj.equals(_another);
		} else if (obj instanceof Date && another instanceof Date) {
			Date _obj = (Date) obj;
			Date _another = (Date) another;
			
			return _obj.compareTo(_another) == 0;
		}
		
		return false;
	}
	
	/** #################################### 任务加载时需要查询的数据 ############################################### */

	/** 删除,取消,发布权限,以及任务变更状态 */
	public LazyDynaBean privilege(Map params) throws Exception {
		LazyDynaBean b = new LazyDynaBean();
		
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		String othertask = (String) params.get("othertask"); //1:是穿透任务
		RecordVo task = getTask(Integer.parseInt(p0800));
		int superiorEdit = isSuperiorEdit(params);
		//是否显示移除部门计划按钮(per_task_map表的dispatchFlag=1)  wusy
		b.set("delDeptTask", dispatchFlag(p0800, objectid ));
		if ("0".equals(othertask) && (isCreater(params) || (superiorEdit >= 1 && superiorEdit <= 4))) { // 只有创建人(上级)才可以删除,取消
			if (WorkPlanConstant.TaskStatus.DRAFT.equals(task.getString("p0811"))) {
				// 任务处于起草状态才可以删除
				if (isDeletable(Integer.parseInt(p0700), Integer.parseInt(p0800))) {
					b.set("deletable", "true");
				}
			} /*else if ((WorkPlanConstant.TaskStatus.APPROVE.equals(task.getString("p0811")) ||
					WorkPlanConstant.TaskStatus.APPROVED.equals(task.getString("p0811"))) &&
					WorkPlanConstant.TaskChangedStatus.Cancel != task.getInt("p0833")) {
				// 任务处于报批或批准状态，且变更状态不是已取消，才允许取消
				b.set("cancelable", "true");
			}*/
			else if (WorkPlanConstant.TaskChangedStatus.Cancel != task.getInt("p0833")) {
				    if((WorkPlanConstant.TaskStatus.APPROVE.equals(task.getString("p0811")))){
					// 任务处于报批，且变更状态不是已取消，允许取消和删除 
				    	b.set("deletable", "true");
				    	b.set("cancelable", "true");
				    }
				    else if(WorkPlanConstant.TaskStatus.APPROVED.equals(task.getString("p0811"))){
				    // 任务处于报批，且变更状态不是已取消，允许取消不允许删除 
				    	b.set("cancelable", "true");
				    }
			}
		}
		
		if (getEditableFields(params).size() > 0) {
			b.set("editable", "true");
		}
		
		b.set("taskExecuteStatus", task.getString("p0809"));
		// 报批的任务任务执行情况改为"待批准"
		if (WorkPlanConstant.TaskStatus.APPROVE.equals(task.getString("p0811")) &&
				!isTopLeader(objectid, p0723)) {
			b.set("taskExecuteDesc", "待批准");
		} else {
			FieldItem item = DataDictionary.getFieldItem("p0809", 1);
			b.set("taskExecuteDesc", PlanTaskBo.getFieldStringValue(item, task.getString("p0809")));
		}
		
		// 任务变更状态（变更状态描述和任务状态迁移按钮的文字）
		LazyDynaBean bean = taskChangedStatus(params);
		b.set("taskChangedStatus", bean.get("taskChangedStatus"));
		if (bean.get("taskChangedDesc") != null) {
			b.set("taskChangedDesc", bean.get("taskChangedDesc"));
		}
		if (bean.get("approveBtn") != null) {
			b.set("approveBtn", bean.get("approveBtn"));
		}
		
		b = b.getMap().size() == 0 ? null : b;
		return b;
	}
	
	private String getObjectId(RecordVo plan) {
		int p0723 = plan.getInt("p0723");
		
		if (p0723 == 1) {
			return plan.getString("nbase") + plan.getString("a0100");
		} else if (p0723 == 2) {
			return plan.getString("p0707");
		} else {
			return "";
		}
	}
	
	/** 判断表中某个字段值是否被改变 */
	public boolean isRankModified(int p0700, int p0800, Number value) throws GeneralException {
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			RecordVo plan = getPlan(p0700);
			if (plan == null) {
				return false;
			}
			
			int p0723 = plan.getInt("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
			String objectid = getObjectId(plan);
			
			String _objId = null;
			StringBuffer subStr = new StringBuffer();
			if (1 == p0723) {
				subStr.append(" nbase ").append(Sql_switcher.concat()).append(" a0100 = ?");
				_objId = objectid.substring(0, 3) + objectid.substring(3);
			} else if (2 == p0723) {
				subStr.append(" org_id=?");
				_objId = objectid;
			}
			
			// 先查询原来的值
			StringBuffer srcValSql = new StringBuffer();
			srcValSql.append("SELECT rank FROM per_task_map WHERE p0800=? AND ").append(subStr);
			rs = dao.search(srcValSql.toString(), Arrays.asList(new Object[] {
				new Integer(p0800),
				_objId
			}));
			if (rs.next()) {
				Object oValue = rs.getObject("rank");
				oValue = oValue == null ? 0.0 : oValue;
				Float fValue = null;
				fValue = Float.parseFloat(oValue.toString());
				return !PlanTaskBo.equal(fValue, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return !PlanTaskBo.equal(null, value); // 根据业务，这条语句基本不会执行
	}

	/**
	 * 从数据库中获得权重rank的值
	 */
	public float GetRankValue(int p0700, int p0800) throws GeneralException {
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			RecordVo plan = getPlan(p0700);
			if (plan == null) {
				return 0 ;
			}
			
			int p0723 = plan.getInt("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
			String objectid = getObjectId(plan);
			
			String _objId = null;
			StringBuffer subStr = new StringBuffer();
			if (1 == p0723) {
				subStr.append(" nbase ").append(Sql_switcher.concat()).append(" a0100 = ?");
				_objId = objectid.substring(0, 3) + objectid.substring(3);
			} else if (2 == p0723) {
				subStr.append(" org_id=?");
				_objId = objectid;
			}
			
			// 先查询原来的值
			StringBuffer srcValSql = new StringBuffer();
			srcValSql.append("SELECT rank FROM per_task_map WHERE p0800=? AND ").append(subStr);
			rs = dao.search(srcValSql.toString(), Arrays.asList(new Object[] {
				new Integer(p0800),
				_objId
			}));
			if (rs.next()) {
				return  rs.getFloat("rank");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0 ;
		
		
	}
//=========================================================================================================================================
	/** 计算当前用户所有可见任务的权重总和
	 * 
	 * @param ids 同一条分支的所有任务
	 * @param tasks 当前用户计划内所有的任务
	 * @param value 新的权重值
	 * @return
	 */
	public float totalRank(List ids, List tasks, Number value) {
		float sum = 0.0F;
		
		for (int i = 0, len = tasks.size(); i < len; i++) {
			LazyDynaBean task = (LazyDynaBean) tasks.get(i);
			if (ids.contains(task.get("p0800"))) { // 排除当前任务分支
				continue;
			}
    		String p0833 =(String) task.get("p0833"); // 任务变更状态
            String p0809 =(String) task.get("p0809"); // 任务执行状态       
            if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)
                      || String.valueOf(WorkPlanConstant.TaskChangedStatus.Cancel).equals(p0833)) { // 已取消
                 continue;
            }
			String rank = (String) task.get("rank");
			float fRank = rank == null || "".equals(rank) ? 0.0F : Float.parseFloat(rank);
			sum += fRank;
		}
		
		return sum + (value == null ? 0 : value.floatValue());
	}
	
	/** 将id字符创转换成id集合 */
	public List split(String sIDs) {
		List rt = new ArrayList();
		
		String[] arrIDs = sIDs.split(",");
		
		for (int i = 0; i < arrIDs.length; i++) {
			String id = arrIDs[i];
			if (id != null && !"".equals(id)) {
				rt.add(id);
			}
		}
		
		return rt;
	}
	
	/** 保存前查询上下级任务之间权重的设置 */
	public String getRankMessage(Map params, Number rank) throws Exception {
		StringBuffer msg = new StringBuffer();
		
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		float oldRankValue=GetRankValue(Integer.parseInt(p0700),Integer.parseInt(p0800));
    	if (isRankModified(Integer.parseInt(p0700), Integer.parseInt(p0800), rank)) 
    	{ // 权重值被修改了
    		PlanTaskTreeTableBo treeBo = new PlanTaskTreeTableBo(conn, Integer.parseInt(p0700));
            Map taskIds = treeBo.getSameBranchTaskIds(p0800);
            String p = (String) taskIds.get("parentIds");
            String c = (String) taskIds.get("childIds");
            
            String parentHaveRank = (String) taskIds.get("parentHaveRank");
            String childHaveRank = (String) taskIds.get("childHaveRank");
            
            float sumRank = totalRank(split(p + "," + p0800 + c), treeBo.getTableData(""), rank);
            FieldItem item = DataDictionary.getFieldItem("rank", "per_task_map");
            String value = WorkPlanUtil.formatDouble(sumRank, item.getDecimalwidth());
            sumRank = Float.parseFloat(value);
            if (oldRankValue == 0.0) {
                if ("true".equals(parentHaveRank) && "true".equals(childHaveRank)) { // 上下都有任务
//                    if (sumRank > 1) {
//                        msg.append("当前任务设置为考核任务后，所有上下级任务将自动设置为非考核任务。并且考核权重之和超过100。是否保存？");
//                    } else {
//                        msg.append("当前任务设置为考核任务后，所有上下级任务将自动设置为非考核任务。是否保存？");
//                    }
                } else if ("true".equals(parentHaveRank) ) {
//                    if (sumRank > 1) {
//                        msg.append("当前任务设置为考核任务后，所有上级任务将自动设置为非考核任务。并且考核权重之和超过100。是否保存？");
//                    } else {
//                        msg.append("当前任务设置为考核任务后，所有上级任务将自动设置为非考核任务。是否保存？");
//                    }
                } else if ("true".equals(childHaveRank)) {
//                    if (sumRank > 1) {
//                        msg.append("当前任务设置为考核任务后，所有下级任务将自动设置为非考核任务。并且考核权重之和超过100。是否保存？");
//                    } else {
//                        msg.append("当前任务设置为考核任务后，所有下级任务将自动设置为非考核任务。是否保存？");
//                    }
                } else {
                    if (sumRank > 1) {
                        msg.append("考核权重之和超过100，是否保存？");
                    }
                }
            } else {
                if (sumRank > 1) {
                    msg.append("考核权重之和超过100。是否保存？");
                }
            }
    	}
		return msg.toString();
	}
	
	
	   /** 新增任务时 如果添加权重时 提醒信息*/
    public String getNewTaskRankMessage(String p0700,String parent_p0800,Number rank) throws Exception {
        StringBuffer msg = new StringBuffer();        
        if (rank==null || rank.doubleValue() <=0){
            return msg.toString();
        }
        PlanTaskTreeTableBo treeBo = new PlanTaskTreeTableBo(conn, Integer.parseInt(p0700));
        String p="";
        String parentHaveRank = "";
        String currentHaveRank = "";
        if (parent_p0800!=null && parent_p0800.length()>0 && !"0".equals(parent_p0800)){
            Map taskIds = treeBo.getSameBranchTaskIds(parent_p0800);
            p = (String) taskIds.get("parentIds");
            p=p+","+parent_p0800;
            parentHaveRank = (String) taskIds.get("parentHaveRank");
            currentHaveRank = (String) taskIds.get("currentHaveRank");
        }
        
        float sumRank = totalRank(split(p), treeBo.getTableData(""), rank); 
        FieldItem item=DataDictionary.getFieldItem("rank", "per_task_map");
        String value=WorkPlanUtil.formatDouble(sumRank, item.getDecimalwidth());
        sumRank=Float.parseFloat(value);
        if ("true".equals(parentHaveRank)||"true".equals(currentHaveRank)) {
            if (sumRank > 1) {
                msg.append("当前任务设置为考核任务后，所有上级任务将自动设置为非考核任务。并且考核权重之和超过100。是否保存？");
            } else {
                //msg.append("当前任务设置为考核任务后，所有上级任务将自动设置为非考核任务。是否保存？");
            	msg.append("");//取消提醒
            }
        } else {
            if (sumRank > 1) {
                msg.append("考核权重之和超过100，是否保存？");
            }
        }
        return msg.toString();
    }
	
	/** 查找所有的任务评价 */
	public List getAllEvaluations(Map params) throws GeneralException {
		String p0800 = (String) params.get("p0800"); // 任务id
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		
		int iP0800 = "".equals(p0800) ? 0 : Integer.parseInt(p0800);
		
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM per_task_evaluation WHERE p0800=? AND ?=");
			sql.append("(CASE WHEN flag=1 THEN upper(nbase)").append(Sql_switcher.concat()).append("a0100 WHEN flag=2 THEN org_id ELSE 'NULL' END)");
			sql.append(" order by evaluate_time desc");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString(), Arrays.asList(new Object[] { Integer.valueOf(iP0800), objectid.toUpperCase() }));
			return dao.getDynaBeanList(rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** 查看能否对当前任务发表评价 */
	public boolean isEvaluable(Map params) throws GeneralException {
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		
		try {
			RecordVo task = getTask(Integer.parseInt(p0800));
			if (task == null) {
				return false;
			}
			
			// 任务符合被评价的条件: 已批准且不是取消状态
			if (WorkPlanConstant.TaskStatus.APPROVED.equals(task.getString("p0811")) ||
					!WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(task.getString("p0809"))) {
				WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
				wpbo.initPlan(Integer.parseInt(p0700));
				
				// 当前用户是被查看人的上级或者本身就是任务中的成员
				if (wpbo.isMySubTeamPeople() || isStaff(params)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return false;
	}
	
	/**
	 * @author lis
	 * @Description: 查找当前用户可见的任务评价
	 * @date 2016-3-8
	 * @param params
	 * @return 页面接收评价对象的集合(role: myself, superior, dm, member, follower)
	 * @throws GeneralException
	 */
	public List getVisibleEvaluations(Map params) throws GeneralException {
		List visible = new ArrayList(); // 对当前用户可见的任务评价
		
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		String who = (String) params.get("who");
		String object_Id = objectid;
		WorkPlanUtil util = new WorkPlanUtil(conn, userView);
		if ("2".equals(p0723)) {
			objectid = util.getFirstDeptLeaders(objectid);
		}
		String directSuperId = util.getMyDirectSuperPerson(objectid.substring(0, 3), objectid.substring(3)); 
		int iP0700 = "".equals(p0700) ? 0 : Integer.parseInt(p0700);
		int iP0800 = "".equals(p0800) ? 0 : Integer.parseInt(p0800);
		
		try {
			List all = getAllEvaluations(params); // 当前任务所有的评价
			
			WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
			wpbo.initPlan(iP0700);
			
			List staffs = getAllStaffs(iP0800);//查询任务中所有的成员
			
			// 当前用户查看自己的评价
			if ("self".equals(who)) {
				// 所有的评价中除了任务中人员的评价即是上级的评价
				for (int i = 0, len = all.size(); i < len; i++) {
					LazyDynaBean bean = (LazyDynaBean) all.get(i);
					String evaluatorId = bean.get("evaluator_nbase") + "" + bean.get("evaluator_a0100");
					
					// 如果不是上级过滤掉曾经是任务的成员但是评价过后被删除的人
					if (isMySuperior(evaluatorId) || (WorkPlanUtil.retrieve(staffs, "objectId", evaluatorId) == null &&
							!isMySuperior(evaluatorId))) {
						LazyDynaBean _js = convertToJsEvaluation(bean, false, evaluatorId);
						if (_js != null) {
							_js.set("role", "superior");
							if(evaluatorId.equals(directSuperId)){//直接上级
								_js.set("direcSuper", "true");
							}else{
								_js.set("direcSuper", "false");
							}
							visible.add(_js);
						}
					}
				}
			}else if ("super".equals(who)) { // 上级
				// 自己的评价
				//LazyDynaBean evaluation_db = getEvaluationByEvaluator(all, userView.getDbname() + userView.getA0100());
				List<LazyDynaBean> myEvaluation = getSupEvaluation(all, object_Id, p0723);
				//LazyDynaBean evaluation_js = convertToJsEvaluation(evaluation_db, false, userView.getDbname() + userView.getA0100());
				for(LazyDynaBean scoreBean : myEvaluation){
					String evaluatorId = scoreBean.get("evaluator_nbase") + "" + scoreBean.get("evaluator_a0100");
					LazyDynaBean evaluation_js = convertToJsEvaluation(scoreBean, false, userView.getDbname() + userView.getA0100());
					if (evaluation_js == null) {
						evaluation_js = new LazyDynaBean();
						evaluation_js.set("needEvaluate", "true");
					} else {
						if(evaluatorId.equals(directSuperId)){//直接上级
							evaluation_js.set("direcSuper", "true");
						}else{
							evaluation_js.set("direcSuper", "false");
						}
						if((userView.getDbname()+userView.getA0100()).equals(evaluatorId)){
							evaluation_js.set("isLoader", "yes");
						}else{
							evaluation_js.set("isLoader", "no");
						}
						evaluation_js.set("reScore", "true");
					}
					evaluation_js.set("role", "superior");//每条评价的评价人员的角色,上级
					
					visible.add(evaluation_js);
				}

//				boolean invite = wpbo.isMyDirectSubTeamPeople(); // 只有直接上级可以发送评价邀请
				boolean invite = false; // 修改:所有上级都可以邀请
				
				for (Iterator iter = staffs.iterator(); iter.hasNext();) {
					LazyDynaBean bean = (LazyDynaBean) iter.next();
					//过滤掉任务成员是上级的
					if (bean == null ||
							(userView.getDbname() + userView.getA0100()).equals(bean.get("objectId"))) {
						continue;
					}
					
					// 如果任务中的人员同时是被查看人的上级，则不需要邀请
					String _id = (String) bean.get("objectId");
					String nbase = _id.substring(0, 3);//人员库前缀
					String a0100 = _id.substring(3);//人员id

					WorkPlanUtil planUtil = new WorkPlanUtil(conn, userView);
					boolean isExist = planUtil.isPersonExist(nbase, a0100);//判断在人员库中是否存在
					if(!isExist) {
                        continue;
                    }
					invite = isMySuperior(_id, objectid) ? false : true;
					
					LazyDynaBean _db = getEvaluationByEvaluator(all, _id);
					LazyDynaBean _js = convertToJsEvaluation(_db, invite, _id); // 页面展现评价信息的bean
					if(_js == null)//如果页面展现评价信息的bean为空则跳过
                    {
                        continue;
                    }
					
					// 负责人或参与人: "dm", 关注人: "follower"
					String p0905 = (String)bean.get("p0905");
					String _role = "1,2".contains(p0905) ? "dm" : "follower";
					
					// 如果任务中的角色同时是被查看人的领导，将其放到superior的集合中
					if (isMySuperior(_id, objectid)) {
						_role = "superior";
					}
					if (_js != null && !_id.equals(objectid)) { // 尚无自评
						_js.set("role", _role);
						visible.add(_js);
					}
				}
			} else if ("director".equals(who)) { // 负责人
				// 自己的评价
				LazyDynaBean evaluation_db = getEvaluationByEvaluator(all, userView.getDbname() + userView.getA0100());
				LazyDynaBean evaluation_js = convertToJsEvaluation(evaluation_db, false, userView.getDbname() + userView.getA0100());
				if (evaluation_js != null) {
					evaluation_js.set("role", "myself");
					evaluation_js.set("reScore", "true");
					visible.add(evaluation_js);
				}
				
				// 参与人的评价
				for (Iterator iter = staffs.iterator(); iter.hasNext();) {
					LazyDynaBean bean = (LazyDynaBean) iter.next();
					if (bean == null || !"2".equals(bean.get("p0905")) ||
							(userView.getDbname() + userView.getA0100()).equals(bean.get("objectId"))) {
						continue;
					}
					
					String _id = (String) bean.get("objectId");
					LazyDynaBean _db = getEvaluationByEvaluator(all, _id);
					LazyDynaBean _js = convertToJsEvaluation(_db, false, _id); // 页面展现评价信息的bean

					// 负责人或参与人: "dm"
					String _role = "dm";
					
					// 如果任务中的角色同时是被查看人的领导，将其放到superior的集合中
					if (isMySuperior(_id, objectid)) {
						_role = "superior";
					}
					
					if (_js != null) {
						_js.set("role", _role);
						visible.add(_js);
					}
				}
			} else if ("member".equals(who)) { // 参与人,关注人
				// 自己的评价
				LazyDynaBean evaluation_db = getEvaluationByEvaluator(all, userView.getDbname() + userView.getA0100());
				LazyDynaBean evaluation_js = convertToJsEvaluation(evaluation_db, false, userView.getDbname() + userView.getA0100());
				if (evaluation_js != null) {
					evaluation_js.set("role", "myself");
					evaluation_js.set("reScore", "true");
					visible.add(evaluation_js);
				}
			}else if("otherMember".equals(who)){
				 // 上级
				// 自己的评价
				StringBuffer staffStr = new StringBuffer(",");
				for (Iterator iter = staffs.iterator(); iter.hasNext();) {
					LazyDynaBean bean = (LazyDynaBean) iter.next();
					staffStr.append((String)bean.get("objectId") + ",");
				}
				List<LazyDynaBean> myEvaluation = all;
				for(LazyDynaBean scoreBean : myEvaluation){
					String evaluatorId = scoreBean.get("evaluator_nbase") + "" + scoreBean.get("evaluator_a0100");
					LazyDynaBean evaluation_js = convertToJsEvaluation(scoreBean, false, userView.getDbname() + userView.getA0100());
					if (evaluation_js == null) {
						evaluation_js = new LazyDynaBean();
						evaluation_js.set("needEvaluate", "true");
					} else {
						if(staffStr.indexOf(","+evaluatorId +",") >= 0)//是任务中的成员，跳过
                        {
                            continue;
                        }
						if(evaluatorId.equals(directSuperId)){//直接上级
							evaluation_js.set("direcSuper", "true");
						}else{
							evaluation_js.set("direcSuper", "false");
						}
						if((userView.getDbname()+userView.getA0100()).equals(evaluatorId)){
							evaluation_js.set("isLoader", "yes");
						}else{
							evaluation_js.set("isLoader", "no");
						}
						evaluation_js.set("reScore", "true");
					}
					evaluation_js.set("role", "superior");//每条评价的评价人员的角色,上级
					
					visible.add(evaluation_js);
				}

//				boolean invite = wpbo.isMyDirectSubTeamPeople(); // 只有直接上级可以发送评价邀请
				boolean invite = false; // 修改:所有上级都可以邀请
				
				for (Iterator iter = staffs.iterator(); iter.hasNext();) {
					LazyDynaBean bean = (LazyDynaBean) iter.next();
					//过滤掉任务成员是上级的
					if (bean == null ||
							(userView.getDbname() + userView.getA0100()).equals(bean.get("objectId"))) {
						continue;
					}
					
					// 如果任务中的人员同时是被查看人的上级，则不需要邀请
					String _id = (String) bean.get("objectId");
					String nbase = _id.substring(0, 3);//人员库前缀
					String a0100 = _id.substring(3);//人员id

					WorkPlanUtil planUtil = new WorkPlanUtil(conn, userView);
					boolean isExist = planUtil.isPersonExist(nbase, a0100);//判断在人员库中是否存在
					if(!isExist) {
                        continue;
                    }
					invite = isMySuperior(_id, objectid) ? false : true;
					
					LazyDynaBean _db = getEvaluationByEvaluator(all, _id);
					LazyDynaBean _js = convertToJsEvaluation(_db, invite, _id); // 页面展现评价信息的bean
					if(_js == null)//如果页面展现评价信息的bean为空则跳过
                    {
                        continue;
                    }
					
					// 负责人或参与人: "dm", 关注人: "follower"
					String p0905 = (String)bean.get("p0905");
					String _role = "1,2".contains(p0905) ? "dm" : "follower";
					
					// 如果任务中的角色同时是被查看人的领导，将其放到superior的集合中
					if (isMySuperior(_id, objectid)) {
						_role = "superior";
					}
					if (_js != null && !_id.equals(objectid)) { // 尚无自评
						_js.set("role", _role);
						visible.add(_js);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return visible;
	}
	
	/** 从评价的集合中查找指定人员的评价 */
	private LazyDynaBean getEvaluationByEvaluator(List evaluations, String evaluator) {
		for (int i = 0, len = evaluations.size(); i < len; i++) {
			LazyDynaBean bean = (LazyDynaBean) evaluations.get(i);
			if (evaluator.equals(bean.get("evaluator_nbase") + "" + bean.get("evaluator_a0100"))) {
				return bean;
			}
		}
		
		return null;
	}
	
	/** 从评价的集合中查找指定人员的评价 */
	private List getEvaluationByEvaluatorList(List evaluations, String evaluator) {
		List myEvaluations = new ArrayList();
		for (int i = 0, len = evaluations.size(); i < len; i++) {
			LazyDynaBean bean = (LazyDynaBean) evaluations.get(i);
			if (evaluator.equals(bean.get("evaluator_nbase") + "" + bean.get("evaluator_a0100"))) {
				myEvaluations.add(bean);
			}
		}
		
		return myEvaluations;
	}
	
	private List getSupEvaluation(List evaluations, String objectid, String p0723) {
		List myEvaluations = new ArrayList();
		WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
		String mySuperIds = ","+wpUtil.getMyAllSuperPerson(objectid, p0723);
		for (int i = 0, len = evaluations.size(); i < len; i++) {
			LazyDynaBean bean = (LazyDynaBean) evaluations.get(i);
			if (mySuperIds.indexOf(","+bean.get("evaluator_nbase") + bean.get("evaluator_a0100"))>-1) {
				myEvaluations.add(bean);
			}
		}
		
		return myEvaluations;
	}
	
	/**
	 * 将数据库的评价bean转换成js的bean
	 * @param evaluation_db per_task_evaluation的记录
	 * @param invite 是否需要邀请
	 * @param evaluatorId 评价人的编号，发送邀请邮件的时候用
	 */
	private LazyDynaBean convertToJsEvaluation(LazyDynaBean evaluation_db, boolean invite, String evaluatorId) throws Exception {
		LazyDynaBean evaluation = new LazyDynaBean();
		String evaluator = null;
		
		if (evaluation_db != null) {
			String evaluationId = (String) evaluation_db.get("id");
			String score = (String) evaluation_db.get("score");
			String description = (String) evaluation_db.get("description");
			String date = (String) evaluation_db.get("evaluate_time");
			evaluator = evaluation_db.get("evaluator_nbase") + "" + evaluation_db.get("evaluator_a0100");
			evaluation.set("evaluationId", evaluationId);
			evaluation.set("invite", "false"); // 无需邀请
			evaluation.set("date", date); // 无需邀请
			evaluation.set("score", score == null ? "0" : score);
//			evaluation.set("description", SafeCode.encode(WorkPlanUtil.formatText(description)));
			evaluation.set("description",WorkPlanUtil.formatText(description));
			evaluation.set("editDescription",SafeCode.encode(description));
//			evaluation.set("description", description);
		} else {
			if (invite) {
				if (evaluatorId == null || "".equals(evaluatorId)) {
					return null;
				}
				evaluator = evaluatorId;
				
				String sex = "1"; // 性别: 1=男, 2=女
				RecordVo vo = getPersonByObjectId(evaluator);
				if (vo != null) {
					sex = vo.getString("a0107");
				}
				sex = WorkPlanUtil.nvl(sex, "1");
				
				evaluation.set("sex", sex);
				evaluation.set("invite", "true");
			} else {
				return null;
			}
		}
		
		String fullName = getA0101(evaluator);
		
		if(StringUtils.isBlank(fullName)) {
            return null;
        }
		evaluation.set("evaluator_id", WorkPlanUtil.encryption(evaluator));
		evaluation.set("evaluator_name", new WorkPlanUtil(conn, userView).getTruncateA0101(fullName));
		evaluation.set("evaluator_fullName", fullName);
		
		return evaluation;
	}
	
	/** 查询任务中所有的成员
	 * bean = {objectId:"Usr00000009", nbase:"Usr", a0100:"00000009", p0905:"1"}
	 */
	public List getAllStaffs(int p0800) throws GeneralException {
		List staffs = new ArrayList();
		
		RowSet rs = null;
		try {
			String sql = "SELECT * FROM P09 WHERE p0901=2 AND p0903=? ORDER BY p0905 ASC";
			rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] {Integer.valueOf(p0800)}));
			
			while (rs.next()) {
				int p0905 = rs.getInt("p0905"); // 成员标识
				String nbase = rs.getString("nbase");
				String a0100 = rs.getString("a0100");
				
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("objectId", nbase + a0100);
				bean.set("nbase", nbase);
				bean.set("a0100", a0100);
				bean.set("p0905", String.valueOf(p0905));
				
				staffs.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return staffs;
	}
	
	/**
	 * @author lis
	 * @Description: 获得要清除上下级权重的任务id
	 * @date 2016-3-21
	 * @param p0700 计划id
	 * @param p0800 任务id
	 * @return String
	 * @throws GeneralException
	 */
	public String getClearRankTaskIds(int p0700, int p0800) throws GeneralException{
		String sP0800 = String.valueOf(p0800);
		StringBuffer clearIDs = new StringBuffer(); // ,1,2,3,4,5
		RowSet rs = null;
		try {
			Map taskIds = new PlanTaskTreeTableBo(conn, p0700).getSameBranchTaskIds(sP0800);
			String ids = taskIds.get("parentIds") + "" + taskIds.get("childIds");
			String[] arrIDs = ids.split(",");
			for (int i = 0; i < arrIDs.length; i++) {
				if (arrIDs[i] == null || "".equals(arrIDs[i]) || arrIDs[i].equals(sP0800)) {
					continue;
				}
				clearIDs.append(",").append(arrIDs[i]);
			}
			
            RecordVo plan = getPlan(p0700);
            int p0723 = plan.getInt("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
            String objectid = getObjectId(plan);
            
            String _objId = null;
            StringBuffer subStr = new StringBuffer();
            if (1 == p0723) {
                subStr.append(" nbase ").append(Sql_switcher.concat()).append(" a0100 = ?");
                _objId = objectid.substring(0, 3) + objectid.substring(3);
            } else if (2 == p0723) {
                subStr.append(" org_id=?");
                _objId = objectid;
            }
        
            
            // 清除权重SQL
            StringBuffer clearSQL = new StringBuffer();
            clearSQL.append("select * from per_task_map WHERE p0800 IN(0 ");
            clearSQL.append(clearIDs).append(")");
            clearSQL.append(" AND ").append(subStr);
            
            clearIDs.setLength(0);
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(clearSQL.toString(), Arrays.asList(new Object[] {_objId}));
            
            while(rs.next()){
            	sP0800 = rs.getString("p0800");
            	if("5".equals(rs.getString("flag")) || "1".equals(rs.getString("flag"))) {
                    clearIDs.append(",").append(WorkPlanUtil.encryption(sP0800));
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return clearIDs.toString()+",";
	}
	
	/** 清除当前任务所在任务分支上其它任务的权重
	 * @param p0700 当前用户的计划id
	 * @param p0800 当前修改或新增的任务id
	 * @return 更新了的行数
	 * @throws GeneralException
	 */
	public void clearBranchRank(int p0700, int p0800) throws GeneralException {
		String sP0800 = String.valueOf(p0800);
		StringBuffer clearIDs = new StringBuffer(); // ,1,2,3,4,5
		try {
			Map taskIds = new PlanTaskTreeTableBo(conn, p0700).getSameBranchTaskIds(sP0800);
			String ids = taskIds.get("parentIds") + "" + taskIds.get("childIds");
			String[] arrIDs = ids.split(",");
			for (int i = 0; i < arrIDs.length; i++) {
				if (arrIDs[i] == null || "".equals(arrIDs[i]) || arrIDs[i].equals(sP0800)) {
					continue;
				}
				clearIDs.append(",").append(arrIDs[i]);
			}
			
            RecordVo plan = getPlan(p0700);
            int p0723 = plan.getInt("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
            String objectid = getObjectId(plan);
            
            String _objId = null;
            StringBuffer subStr = new StringBuffer();
            if (1 == p0723) {
                subStr.append(" nbase ").append(Sql_switcher.concat()).append(" a0100 = ?");
                _objId = objectid.substring(0, 3) + objectid.substring(3);
            } else if (2 == p0723) {
                subStr.append(" org_id=?");
                _objId = objectid;
            }
        
            // 清除权重SQL
            StringBuffer clearSQL = new StringBuffer();
            clearSQL.append("UPDATE per_task_map SET rank=null WHERE p0800 IN(0 ");
            clearSQL.append(clearIDs).append(")");
            clearSQL.append(" AND ").append(subStr);
            
            ContentDAO dao = new ContentDAO(conn);
            dao.update(clearSQL.toString(), Arrays.asList(new Object[] {_objId}));
                
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/** 更新权重的值
	 * @param p0700 当前用户的计划id
	 * @param p0800 当前修改或新增的任务id
	 * @param value 修改后的值,页面传递过来未经修饰的值
	 * @param flag 	标识,1记录日志,0不记录
	 * @return 更新后新旧权重是否相等
	 * @throws GeneralException
	 */
	public boolean updateRank(int p0700, int p0800, String value, int flag) throws GeneralException {
		int count = 0; // 更新的行数
		
		Float fValue = Float.parseFloat((value == null || "".equals(value) ? 0.0 : value).toString());
		fValue =  Float.valueOf(fValue.floatValue() / 100);
		fValue = fValue == 0.0 ? null : fValue;
		try {
			if (!isRankModified(p0700, p0800, fValue)) { // 新旧权重值相等
				return false;
			}
			
			RecordVo plan = getPlan(p0700);

			int p0723 = plan.getInt("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
			String objectid = getObjectId(plan);
			
			String _objId = null;
			StringBuffer subStr = new StringBuffer();
			if (1 == p0723) {
				subStr.append(" nbase ").append(Sql_switcher.concat()).append(" a0100 = ? AND "+Sql_switcher.isnull("dispatchFlag", "0")+" =0");
				_objId = objectid.substring(0, 3) + objectid.substring(3);
			} else if (2 == p0723) {
				subStr.append(" org_id=? AND nbase is null and A0100 is null");
				_objId = objectid;
			}
			
			// 更新
			float oldRank = GetRankValue(p0700, p0800);
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE per_task_map SET rank=? WHERE p0800=? AND ").append(subStr);
			count = new ContentDAO(conn).update(sql.toString(), Arrays.asList(new Object[] {
				fValue, new Integer(p0800), _objId
			}));
			//记录日志 调整任务权重 wusy
			String logcontent = "";
			if(flag == 1 && count > 0 && fValue != null){
				if(oldRank != 0 ){
					logcontent = "将任务权重从" + String.valueOf(oldRank*100).split("\\.")[0] +"%调整为了" + (fValue==0?0:(String.valueOf(fValue*100).split("\\.")[0]) + "%");
				}else{
					logcontent = "将任务权重从" + 0 +"调整为了" + String.valueOf(fValue*100).split("\\.")[0] + "%";
				}
			}
			if(flag == 1 && count > 0 && fValue == null){
				logcontent = "将任务权重从" + String.valueOf(oldRank*100).split("\\.")[0] +"%调整为了0";
			}
			RecordVo p08Vo = new RecordVo("p08");
	    	p08Vo.setInt("p0800", p0800);
	    	try {
				p08Vo = new ContentDAO(conn).findByPrimaryKey(p08Vo);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
				new WorkPlanOperationLogBo(conn, userView).addLog(p0800, logcontent);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return count != 0;
	}
	
	/** 被查看人的任务列表中是否包含需要批准的任务 lium */
	public boolean ifNeedToApprove(int p0700, int p0800) throws GeneralException {
		try {
			WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
			wpbo.initPlan(p0700);
			RecordVo plan = wpbo.getP07_vo();
			RecordVo task = getTask(p0800);
			
			int p0719 = plan.getInt("p0719"); // 计划状态
			String p0811 = task.getString("p0811"); // 任务审批状态
			if (p0719 != WorkPlanConstant.PlanApproveStatus.Pass) {
				return false;
			}
			if (!wpbo.isMyDirectSubTeamPeople()) {return false;}		
		
			if (WorkPlanConstant.TaskStatus.APPROVE.equals(p0811)) {
				String objectId = plan.getInt("p0723") == 2
					? new WorkPlanUtil(conn, userView).getFirstDeptLeaders(plan.getString("p0707")) 
						: plan.getString("nbase")+ plan.getString("a0100");

				if (isCreater(p0800, objectId) || isDirector(p0800, objectId)) {
					return true;
				}
			}
			
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/** 被查看人的任务列表中是否包含需要批准的任务 lium */
	public boolean ifNeedToApproveForPlan(int p0700, int p0800) throws GeneralException {
		try {
			WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
			wpbo.initPlan(p0700);
			RecordVo plan = wpbo.getP07_vo();
			RecordVo task = getTask(p0800);
			
			int p0719 = plan.getInt("p0719"); // 计划状态
			String p0811 = task.getString("p0811"); // 任务审批状态
			if (p0719 != WorkPlanConstant.PlanApproveStatus.Pass) {
				return false;
			}
			if (!wpbo.isMyDirectSubTeamPeople()) {return false;}		
		
			if (WorkPlanConstant.TaskStatus.APPROVE.equals(p0811)) {
				String objectId = plan.getInt("p0723") == 2
					? new WorkPlanUtil(conn, userView).getFirstDeptLeaders(plan.getString("p0707")) 
						: plan.getString("nbase")+ plan.getString("a0100");

				if (isCreater(p0800, objectId) || isDirector(p0800, objectId)) {
					return true;
				}
			}
			
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/** 变更状态变化的任务是否需要发布 lium */
	public boolean ifNeedToPublish(int p0700, int p0800) throws Exception {
		try {
			if (!isCreater(p0800) && !isDirector(p0800)) {
				return false;
			}
			
			RecordVo plan = getPlan(p0700);
			RecordVo task = getTask(p0800);
			if (plan == null || task == null) {
				return false;
			}
			
			//任务已取消，不更改变更状态 wangrd 20141202
			String p0809 = task.getString("p0809"); // 任务执行状态
	        if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)) { // 已取消
	            return false;
	        }
	        
	        int p0719 = plan.getInt("p0719"); // 计划审批状态
	        if (p0719 != WorkPlanConstant.PlanApproveStatus.Pass) {return false;}
	        
	        String p0811 = task.getString("p0811"); // 任务审批状态
	        int p0833 = task.getInt("p0833"); // 任务变更状态
			
			if  (
					WorkPlanConstant.TaskStatus.DRAFT.equals(p0811) // 起草状态
					|| (
							WorkPlanConstant.TaskStatus.APPROVED.equals(p0811) // 批准状态
							&& p0833 != WorkPlanConstant.TaskChangedStatus.Normal // 产生了变更
							&& !WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(task.getString("p0809")) // 未执行完毕
						)
				) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/** 变更状态变化的任务是否需要发布 lium */
	public boolean isTaskChanged(int p0700, int p0800) throws Exception {
		try {
			if (!isCreater(p0800) && !isDirector(p0800)) {
				return false;
			}
			
			RecordVo plan = getPlan(p0700);
			RecordVo task = getTask(p0800);
			if (plan == null || task == null) {
				return false;
			}
			
			//任务已取消，不更改变更状态 wangrd 20141202
			String p0809 = task.getString("p0809"); // 任务执行状态
	        if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)) { // 已取消
	            return false;
	        }
	        
	        int p0719 = plan.getInt("p0719"); // 计划审批状态
	        if (p0719 != WorkPlanConstant.PlanApproveStatus.Pass) {return false;}
	        
	        String p0811 = task.getString("p0811"); // 任务审批状态
	        int p0833 = task.getInt("p0833"); // 任务变更状态
			
			if  (
					WorkPlanConstant.TaskStatus.DRAFT.equals(p0811) // 起草状态
					|| (
							p0833 != WorkPlanConstant.TaskChangedStatus.Normal // 产生了变更
							&& !WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(task.getString("p0809")) // 未执行完毕
						)
				) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/** 根据计划和任务判断当前操作会否产生变更状态改变 */
	public boolean ifCauseChangedStatusAltering(RecordVo plan, RecordVo task) {
		if (plan == null || task == null) {
			return false;
		}
		
		int p0719 = plan.getInt("p0719"); // 计划审批状态
		String p0811 = task.getString("p0811"); // 任务审批状态
		int p0833 = task.getInt("p0833"); // 任务变更状态
		//任务已取消，不更改变更状态 wangrd 20141202
		String p0809 = task.getString("p0809"); // 任务执行状态		
        if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)) { // 已取消
            return false;
        }
		
		switch (p0719) {
			case WorkPlanConstant.PlanApproveStatus.HandIn: { // 计划处于报批状态
				if (WorkPlanConstant.TaskStatus.APPROVE.equals(p0811)) { // 任务处于报批状态
					if (p0833 == WorkPlanConstant.TaskChangedStatus.Normal) { // 变更状态是未变更
						return true;
					}
				}
				if (WorkPlanConstant.TaskStatus.APPROVED.equals(p0811)) { // 任务处于批准状态
					return true;
				}
			}
			case WorkPlanConstant.PlanApproveStatus.Pass: { // 计划处于批准状态
				if (WorkPlanConstant.TaskStatus.DRAFT.equals(p0811)) { // 任务处于起草状态
					return false;
				}
				if (WorkPlanConstant.TaskStatus.APPROVE.equals(p0811)) { // 任务处于报批状态
					return false;
				}
				if (WorkPlanConstant.TaskStatus.APPROVED.equals(p0811)) { // 任务处于批准状态
					return true;
				}
			}
			case WorkPlanConstant.PlanApproveStatus.Reject: { // 计划处于退回状态
				if (WorkPlanConstant.TaskStatus.DRAFT.equals(p0811)) { // 任务处于起草状态
					if (p0833 == WorkPlanConstant.TaskChangedStatus.Normal) { // 变更状态是未变更
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/** 获得当前任务下可见的子任务 */
	public int[] getVisibleSubtaskIDs(int p0700, int p0800) throws Exception {
		int[] rows = null;
		
		PlanTaskTreeTableBo bo = new PlanTaskTreeTableBo(conn, p0700);
		
		StringBuffer sql = new StringBuffer();
		String subSQL = bo.getTableDatasql("");
		int index = subSQL.indexOf("order");
		if (index > 0) {
			subSQL = subSQL.substring(0, index-1);
		}
		sql.append(subSQL);
		sql.append(" AND p08.p0831=").append(p0800).append(" AND p08.p0800<>").append(p0800);
		
		RowSet rs = null;
		try {
			rs = new ContentDAO(conn).search(sql.toString());
			
			rows = new int[getRowCount(rs)];
			
			// 定位到行首，开始遍历
			for (int i = 0; rs.next(); i++) {
				rows[i] = rs.getInt("p0800");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		
		return rows;
	}

	/** 获得当前任务下所有子任务 */
	public int[] getAllSubtaskIDs(int p0800) throws Exception {
		int[] rows = null;

		String sql = "SELECT p0800 FROM p08 WHERE p0831<>p0800 AND p0831=?";
		RowSet rs = null;
		try {
			rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] { Integer.valueOf(p0800) }));

			rows = new int[getRowCount(rs)];

			// 定位到行首，开始遍历
			for (int i = 0; rs.next(); i++) {
				rows[i] = rs.getInt("p0800");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return rows;
	}
	
	/** 从结果集中查询记录数 */
	private int getRowCount(ResultSet rs) throws Exception {
		int count = 0;
		int curr = 0; // 记录下当前光标所在行
		
		if (rs == null) {
			return count;
		}
		
		try {
			curr = rs.getRow();
			
			rs.last();
			count = rs.getRow();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// 恢复到当前行
			if (curr > 0) {
				rs.absolute(curr);
			} else {
				rs.beforeFirst();
			}
		}
		
		return count;
	}
	
	/** 根据任务编号查询当前任务的负责人 */
	public RecordVo getDirector(int p0800) throws Exception {
		String sql = "SELECT * FROM P09 WHERE p0905=1 AND p0903=? AND p0901=2";
		
		RowSet rs = null;
		try {
			rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] {Integer.valueOf(p0800)}));
			if (rs.next()) {
				String director = rs.getString("nbase") + rs.getString("a0100");
				return getPersonByObjectId(director);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		
		return null;
	}
	
	   /** 根据任务编号查询当前任务的负责人 */
    public String getDirectorA0101(int p0800)  {
        String sql = "SELECT * FROM P09 WHERE p0905=1 AND p0903=? AND p0901=2";
        String director ="";
        RowSet rs = null;
        try {
            rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] {Integer.valueOf(p0800)}));
            if (rs.next()) {
                director = rs.getString("p0913");
               
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        
        return director;
    }
	
	/** 查询任务的创建人 */
	public String getCreater(int p0800) throws Exception {
		try {
			RecordVo task = getTask(p0800);
			RecordVo plan = getPlan(task.getInt("p0700"));
			
			if (plan == null) {
				return null;
			}
			
			String creater = "";
			if (plan.getInt("p0723") == 1) {
				creater = plan.getString("nbase") + plan.getString("a0100");
			} else if (plan.getInt("p0723") == 2) {
				creater = new WorkPlanUtil(conn, userView).getFirstDeptLeaders(plan.getString("p0707"));
			}
			
			return creater;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/** per_task_map内的成员 lium */
	public List getMemberFromPTMap(int p0800) throws Exception {
		List m = new ArrayList();
		
		String sql = "SELECT id FROM per_task_map WHERE flag=? AND p0800=?";
		
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql, Arrays.asList(new Object[] {
					Integer.valueOf(WorkPlanConstant.MemberType.MEMBER),
					Integer.valueOf(p0800)
			}));
			while (rs.next()) {
				int id = rs.getInt("id");
				m.add(getRecordVo("per_task_map", "id", Integer.valueOf(id)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return m;
	}
	
	/** p09内的成员 lium */
	public List getMemberFromP09(int p0800) throws Exception {
		List m = new ArrayList();
		
		String sql = "SELECT P0900 FROM p09 WHERE p0905=? AND p0903=?";
		
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql, Arrays.asList(new Object[] {
					Integer.valueOf(WorkPlanConstant.MemberType.MEMBER),
					Integer.valueOf(p0800)
			}));
			while (rs.next()) {
				int P0900 = rs.getInt("P0900");
				m.add(getRecordVo("p09", "P0900", Integer.valueOf(P0900)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return m;
	}
	
	/** 指定任务下当前用户创建的后代任务
	 * @param p0700 被查看人的计划id
	 * @param p0800 被查看的任务id
	 * @return
	 */
	public boolean isDeletable(int p0700, int p0800) {
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			
			String sql = "SELECT * FROM P08 WHERE p0831=? AND p0800<>p0831";
			rs = dao.search(sql, Arrays.asList(new Object[] {Integer.valueOf(p0800)}));
			List tasks = dao.getDynaBeanList(rs);
			
			for (int i = 0, len = tasks.size(); i < len; i++) {
				LazyDynaBean task = (LazyDynaBean) tasks.get(i);
				
				int iP0700 = Integer.parseInt((String) task.get("p0700")); // 子任务所在计划编号
				if (p0700 != iP0700) { // 不是我创建的任务
					return false;
				} else {
					String sP0811 = (String) task.get("p0811"); // 子任务审批编号
					if (!WorkPlanConstant.TaskStatus.DRAFT.equals(sP0811)) { // 子任务不是起草状态，不满足删除条件
						return false;
					} else { // 子任务满足起草条件的，继续向下递归
						int iP0800 = Integer.parseInt((String) task.get("p0800")); // 子任务编号
						if (!isDeletable(iP0700, iP0800)) {
							return false;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	/** 判断是否显示移除部门计划,dispatchFlag=1,返回"true",显示按钮,否则,返回"false" wusy
	 * @param p0700 计划id
	 * @param p0800 任务id
	 * @param orgid  部门id
	 * @return String
	 */
	public String dispatchFlag(String p0800, String orgid){
		Integer dispatchFlag = 0;
		try {
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(conn);
			String sql = "SELECT dispatchFlag FROM per_task_map WHERE nbase is null AND A0100 is null AND p0800 = ? and org_id = ?";
				rs = dao.search(sql, Arrays.asList(new Object[] {
						Integer.parseInt(p0800),
						orgid
						}));
				while(rs.next()){
					dispatchFlag  = rs.getInt("dispatchFlag");
				}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dispatchFlag == 1 ? "true" : "false";
	}
	
	/** 指定任务下所有的后代任务id */
	public List getAllSubTaskIDs(int parentID) {
		List ids = new ArrayList();
		
		String sql = "SELECT p0800 FROM P08 WHERE p0831=? AND p0831<>p0800";
		RowSet rs = null;
		try {
			rs = new ContentDAO(conn).search(sql, Arrays.asList(new Object[] {Integer.valueOf(parentID)}));
			while (rs.next()) {
				int p0800 = rs.getInt("p0800");
				ids.add(Integer.valueOf(p0800));
				ids.addAll(getAllSubTaskIDs(p0800));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ids;
	}
	
	/** 组装staff节点 */
	public LazyDynaBean getStaffNode(String staffId) {
		LazyDynaBean bean = null;
		
		if (staffId == null || staffId.length() < 4) {
			return bean;
		}
		
		try {
			bean = new LazyDynaBean();
			
			String a0101 = getA0101(staffId);

			bean.set("id", WorkPlanUtil.encryption(staffId));
			bean.set("fullName", a0101);
			bean.set("photo", new WorkPlanBo(conn, userView).getPhotoPath(staffId.substring(0, 3), staffId.substring(3)));
			bean.set("abbr", new WorkPlanUtil(conn, userView).getTruncateA0101(a0101));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bean;
	}

	public boolean isTopLeader(String objectid,String p0723) {
		WorkPlanUtil util = new WorkPlanUtil(conn, userView);
		return !util.isHaveDirectSuper(objectid, p0723);
	}
	//根据主键查询任务
	public RowSet getTask(String p0800){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		String sql="select * from P08 where p0800="+p0800;
		try {
			rs=dao.search(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
		
	}
	//设置userView的workPlanChangeFlg状态
	public void setWorkPlanChangeFlg(String status){
		userView.getHm().put("workPlanChangeFlg", status);//是否需要重新刷新flg
	}
	
	/**
     * @Title:addDeptTask
     * @Description:分配任务到部门
     * @param b0110:分配到部门负责人所在部门的部门id
     * @param p0700:计划id
     * @param directorId:新负责人id
     * @param p0800:任务id
     * @return void 
     * @author:wusy   
     */
    public void addDeptTask(String b0110, String p0700, String p0800,String belongflag){
    	try {
	    	RecordVo newDirector_map = new RecordVo("per_task_map");
	    	IDGenerator idg = new IDGenerator(2, this.conn);
	    	WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
	    	RecordVo task = getTask(Integer.parseInt(p0800));
	    	int teamPlanId = wpbo.addDeptPlan(p0700, b0110);
	    	newDirector_map.setInt("seq", new PlanTaskTreeTableBo(conn, teamPlanId).getSeq(b0110, task.getInt("p0831"), 2));
			newDirector_map.setString("nbase", null);
			newDirector_map.setString("a0100", null);
			newDirector_map.setInt("flag", 1);
			newDirector_map.setInt("p0700", Integer.parseInt(p0700));
			newDirector_map.setInt("belongflag", Integer.parseInt(belongflag));
			newDirector_map.setInt("p0800", Integer.parseInt(p0800));
			newDirector_map.setDate("create_time", new Date());
			newDirector_map.setString("create_user", userView.getUserName());
			newDirector_map.setString("create_fullname", userView.getUserFullName());
			newDirector_map.setString("org_id", b0110);
			String id_newDirector_map = idg.getId("per_task_map.id");
			newDirector_map.setInt("id", Integer.parseInt(id_newDirector_map));
			newDirector_map.setInt("dispatchflag", 1);//分配到部门
			ContentDAO dao = new  ContentDAO(conn);
			dao.addValueObject(newDirector_map);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
}
