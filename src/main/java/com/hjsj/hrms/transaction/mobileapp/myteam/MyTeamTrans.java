package com.hjsj.hrms.transaction.mobileapp.myteam;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * <p>Title: MyTeamTrans </p>
 * <p>Description: 我的团队交易类</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-12-26 下午5:09:49</p>
 * @author yangj
 * @version 1.0
 */
public class MyTeamTrans extends IBusiness {

	private static final long serialVersionUID = 1L;
	/** 显示我的团队 */
	private final String CREATE_MYTEAM = "1";
	
	public void execute() throws GeneralException {
		String message = "";
		String succeed = "false";
		HashMap hm = this.getFormHM();
		try {
			String transType = (String) hm.get("transType");
			hm.remove("message");
			UserView userView = this.getUserView();
			Connection conn = this.getFrameconn();
			MyTeamBo myTeamBo = new MyTeamBo(conn, userView);
			// 不同业务流程分支点
			if (CREATE_MYTEAM.equals(transType)) {// 主界面我的团队展示
				this.createMyTeam(hm, myTeamBo);
				succeed = "true";		
			} else {
				//异常
				message = ResourceFactory.getProperty("mobileapp.myteam.error.transTypeError");
				hm.put("message", message);
			}
		} catch (Exception e) {
			succeed = "false";
            String errorMsg=e.toString();
            int index_i=errorMsg.indexOf("description:");
            message=errorMsg.substring(index_i+12);
            hm.put("message", message);
            e.printStackTrace();
            this.cat.error(e.getMessage());
		} finally {
			hm.put("succeed", succeed);
		}
	}

	/**
	 * 
	 * @Title: createMyTeam
	 * @Description: 我的团队人员展示
	 * @param hm
	 * @param myTeamBo
	 * @return void
	 * @throws GeneralException
	 */
	private void createMyTeam(HashMap hm, MyTeamBo myTeamBo) throws GeneralException {
		//网络地址
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
		String unitID = (String) hm.get("mUnitID");
		unitID = unitID == null || unitID.length() == 0 ? "" : unitID;	
		
		List mEmplist = myTeamBo.searchInfoList(unitID, keywords, url,pageIndex, pageSize);
		hm.put("mEmplist", mEmplist);

	}
}
