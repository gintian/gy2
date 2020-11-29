package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 * <p> Title: SearchRepeatRoleTrans </p>
 * <p>Description: 查询重复账号的用户信息</p>
 * <p>Company: hjsj</p>
 * <p>create time 2013-10-31 下午4:01:49</p>
 * 
 * @author yangj
 * @version 1.0
 */
public class SearchRepeatRoleTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String repeatID = (String) this.getFormHM().get("repeatID");
		if (repeatID == null || "".equals(repeatID))
			return;
		this.getFormHM().put("repeatID", repeatID);
		String dbpre = repeatID.substring(0, 3);
		String a_id = repeatID.substring(3);

		/** 登录参数表,登录用户指定不是username */
		String username = "username";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		/** default值 */
		this.getFormHM().put("userlen", "50");
		// 获得后台设定的用户口令长度，并从后台得到用户名的字段
		if (login_vo == null) {
			username = "username";
		} else {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx > 1) {
				username = login_name.substring(0, idx);
				if ("#".equals(username) || "".equals(username))
					username = "username";
				else {
					FieldItem item = DataDictionary.getFieldItem(username);
					if (item != null)
						this.getFormHM().put("userlen",
								Integer.toString(item.getItemlength()));
				}
			}
		}

		// 获得数据库连接
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		/**
		 * 对在职人员而言。
		 */
		String tablename = dbpre + "a01";
		RecordVo vo = new RecordVo(tablename, 1);
		vo.setString("a0100", a_id);
		RecordVo user_vo = new RecordVo(tablename, 1);
		try {
			vo = dao.findByPrimaryKey(vo);
			user_vo.setString("username", vo.getString(username));
			user_vo.setString("b0110", vo.getString("b0110"));
			user_vo.setString("e0122", vo.getString("e0122"));
			user_vo.setString("e01a1", vo.getString("e01a1"));
			user_vo.setString("a0101", vo.getString("a0101"));
			cat.debug("user_vo=" + user_vo.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("reportUser_vo", user_vo);
	}
}
