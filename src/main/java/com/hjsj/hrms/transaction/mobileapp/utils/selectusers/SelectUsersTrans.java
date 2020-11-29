package com.hjsj.hrms.transaction.mobileapp.utils.selectusers;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * <p>Title: SelectUsersTrans </p>
 * <p>Description: 选择用户交易类</p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-1-15 下午4:14:00</p>
 * @author yangj
 * @version 1.0
 */
public class SelectUsersTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/** 显示自助用户 */
	private final String CREATE_EMPS = "1";
	/** 显示业务用户 */
	private final String CREATE_MANAGERS = "2";

	public void execute() throws GeneralException {
		String message = "";
		String succeed = "false";
		HashMap hm = this.getFormHM();
		try {
			String transType = (String) hm.get("transType");
			hm.remove("transType");
			hm.remove("message");
			hm.remove("succeed");
			UserView userView = this.getUserView();
			Connection conn = this.getFrameconn();
			SelectUsersBo selectUsersBo = new SelectUsersBo(conn, userView);
			// 不同业务流程分支点
			if (transType != null) {
				if (CREATE_EMPS.equals(transType) || CREATE_MANAGERS.equals(transType)) {// 人员显示
					this.createUsers(hm, selectUsersBo, transType);
					succeed = "true";
				} else {
					message = ResourceFactory.getProperty("mobileapp.selectusers.error.transTypeError");
					hm.put("message", message);
				}
			} else {
				message = ResourceFactory.getProperty("mobileapp.selectusers.error.transTypeError");
				hm.put("message", message);
			}
		} catch (Exception e) {
			succeed = "false";
			message = ResourceFactory.getProperty("mobileapp.selectusers.error.transTypeError");
			hm.put("message", message);
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			hm.put("succeed", succeed);
		}

	}

	/**
	 * 
	 * @Title: createUsers   
	 * @Description: 获取人员类型  
	 * @param hm
	 * @param selectUsersBo
	 * @param transType
	 * @return void    
	 * @throws GeneralException
	 */
	private void createUsers(HashMap hm, SelectUsersBo selectUsersBo, String transType) throws GeneralException {
		String url = (String) hm.get("url");
		// 第几页
		String pageIndex = (String) hm.get("pageIndex");
		pageIndex = pageIndex == null ? "1" : pageIndex;

		// 每页条数
		String pageSize = (String) hm.get("pageSize");
		pageSize = pageSize == null ? "10" : pageSize;

		// 模糊查询
		String keywords = (String) hm.get("keywords");
		keywords = keywords == null ? "" : keywords;

		// 根据组织机构ID查询
		String unitID = (String) hm.get("unitID");
		unitID = unitID == null || unitID.length() == 0 ? "" : unitID;

		// 根据权限查询库
		String dbpreFlag = (String) hm.get("dbpreFlag");
		dbpreFlag = dbpreFlag == null || dbpreFlag.length() == 0 ? "" : dbpreFlag;

		if(("2".equals(dbpreFlag)||"3".equals(dbpreFlag))&&unitID.length()==0){
			unitID = userView.getManagePrivCode()+"`"+userView.getManagePrivCodeValue();
		}
		
		List usersList = null;
		if (CREATE_EMPS.equals(transType)) {
			usersList = selectUsersBo.searchEmpInfoList(unitID, keywords, url, pageIndex, pageSize, dbpreFlag);
		} else if (CREATE_MANAGERS.equals(transType)) {
			usersList = selectUsersBo.searchManagerInfoList(unitID, keywords, url, pageIndex, pageSize);
		}

		hm.put("usersList", usersList);
		hm.put("transType", transType);
	}

}
