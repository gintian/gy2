package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 对服务号配置的新增保存操作
 * 
 * @author caoqy 2018-5-28 14:59:06 wxsetid 微信程序类型标识 serverid 微信应用编号 name 应用名称
 *         APPID 微信应用appid号 AppSecret 微信应用app_secret号 url 应用程序服务地址 app_type 应用类型
 *         description 应用简介 str_value 菜单数据
 */
public class DeleteWXServerParamTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ArrayList list = new ArrayList();
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap formMap = this.getFormHM();
		StringBuffer sql = new StringBuffer();
		MorphDynaBean map = (MorphDynaBean) formMap.get("serverParam");
		if (map == null) {// 没获取到数据返回
			return;
		}
		// 删除服务号
		int serverid = (Integer) map.get("serverid");
		sql.append("DELETE FROM t_sys_weixin_param WHERE wxitemid=?");
		list.add(serverid);
		try {
			dao.delete(sql.toString(), list);
			list.clear();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
