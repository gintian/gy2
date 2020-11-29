package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 添加服务号前查询数据
 * 
 * @author caoqy 2018-5-28 14:59:06 wxsetid 微信程序类型标识 serverid 微信应用编号 name 应用名称
 *         APPID 微信应用appid号 AppSecret 微信应用app_secret号 url 应用程序服务地址 app_type 应用类型
 *         description 应用简介 str_value 菜单数据
 */
public class SearchWXServerParamTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		HashMap serverParam = new HashMap();// 服务号详细配置信息
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap formMap = this.getFormHM();
		StringBuffer sql = new StringBuffer();
		MorphDynaBean map = (MorphDynaBean) formMap.get("serverParam");
		if (map == null) {// 没获取到数据返回
			return;
		}
		int serverid = (Integer) map.get("serverid");
		HashMap servers = new HashMap();
		ArrayList server = new ArrayList();
		sql.append("SELECT wxsetid,wxitemid,wxname,appid,app_secret,url,app_type,description FROM t_sys_weixin_param WHERE wxitemid = ?");
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString(), Arrays.asList(serverid));
			if (rs.next()) {
				servers.put("serverid", rs.getString("wxsetid"));
				servers.put("serverid", rs.getInt("wxitemid"));
				servers.put("servername", rs.getString("wxname"));
				servers.put("APPID", rs.getString("appid"));
				servers.put("AppSecret", rs.getString("app_secret"));
				servers.put("url", rs.getString("url"));
				servers.put("servertype", rs.getString("app_type"));
				servers.put("description", rs.getString("description"));
				server.add(servers);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    PubFunc.closeResource(rs);
		}
		serverParam.put("servers", server);
		this.getFormHM().put("serverParam", serverParam);

	}
}
