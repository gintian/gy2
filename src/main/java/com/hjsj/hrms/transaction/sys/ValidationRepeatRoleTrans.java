package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>Title: SaveRepeatRoleTrans </p>
 * <p>Description:验证是否有重复角色 </p>
 * <p>Company: hjsj</p>
 * <p> create time 2013-11-4 下午1:57:18 </p>
 * 
 * @author yangj
 * @version 1.0
 */
public class ValidationRepeatRoleTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/**
	 * 默认方法
	 */
	public void execute() throws GeneralException {
		try {
			// 验证是否有重复用户名是否存在，存在则向页面输送exist标识符，没有则返回ok标识
			if (this.validationRole()) {
				this.getFormHM().put("flag", "ok");
			} else
				this.getFormHM().put("flag", "exist");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	private boolean validationRole() throws GeneralException {
		List repeatList = this.getTableList();
		// 获取动态用户名
		String username = this.getDynamicUserName();
		StringBuffer strsql = new StringBuffer();
		boolean flag = false;
		try {
			// 数据库连接
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			strsql.append("SELECT un.");
			strsql.append(username);
			strsql.append(" FROM(SELECT ");
			strsql.append(username);
			strsql.append(" FROM ");
			strsql.append(repeatList.get(0));
			strsql.append("A01");
			if (repeatList.size() >= 2) {
				for (int i = 1, n = repeatList.size(); i < n; i++) {
					strsql.append(" UNION ALL SELECT ");
					strsql.append(username);
					strsql.append(" FROM ");
					strsql.append(repeatList.get(i));
					strsql.append("A01");
				}
			}
			strsql.append(" UNION ALL select UserName from OperUser where RoleID=0) un group by un.");
			strsql.append(username);
			strsql.append(" HAVING count(un.");
			strsql.append(username);
			strsql.append(")>=2");
			// 查询
			this.frowset = dao.search(strsql.toString());
			// 如果查询到就直接返回，否则进入多表连接查询,oracle遇到空时，不会返回数据库，遇到sqlserver会返回一条空数据。
			int flagI = 0;
			 while(this.frowset.next()) {
				flagI++;
				String uName = this.getFrowset().getString(username);
				flag = (flagI == 1 && (uName == null || "".equals(uName))) ? false : true;
				if(flag == true)
					return true;
				// 如果有两条数据，则认为有重复人员库
				if (flagI == 2){
					return true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

	/**
	 * 
	 * @Title: getRoleBaseList
	 * @Description:获得需要查询的表
	 * @throws GeneralException
	 * @return List
	 * @throws
	 */
	private List getTableList() throws GeneralException {
		/** 登录参数表 */
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
		String strTable = login_vo.getString("str_value");
		/** 系统所有存在的数据库列表usr,oth,trs,ret */
		List RepeatTablelist = new ArrayList();
		try {
			// 如果用户已经选择了应用库，则默认使用它。否则默认使用Uer表
			if (strTable.length() > 0) {
				// 分割字符串
				String[] str = strTable.split(",");
				for (int i = 0, n = str.length; i < n; i++)
					RepeatTablelist.add(str[i]);
			} else
				RepeatTablelist.add("Usr");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return RepeatTablelist;
	}

	/**
	 * 
	 * @Title: getDynamicUserName
	 * @Description:获得动态用户名
	 * @return String
	 * @throws
	 */
	private String getDynamicUserName() {
		/** 登录参数表,登录用户指定默认username */
		String username = "username";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		// 获得后台设定的用户口令，否则使用默认的“UserName”
		if (login_vo != null) {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx > 1) {
				username = login_name.substring(0, idx);
				if ("#".equals(username) || "".equals(username))
					username = "username";
			}
		}
		return username;
	}

}
