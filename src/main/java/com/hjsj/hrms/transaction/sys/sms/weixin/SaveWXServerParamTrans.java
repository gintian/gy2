package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
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
public class SaveWXServerParamTrans extends IBusiness {

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
		String optiontype = (String) this.getFormHM().get("service");
		String name = (String) map.get("name");// 名
		String APPID = (String) map.get("APPID");// appid
		String AppSecret = (String) map.get("AppSecret");
		String url = (String) map.get("url");
		String wxsetid = (String) map.get("wxsetid");
//		String servertype = (String) map.get("servertype");
//		String description = (String) map.get("description");
		if ("add".equals(optiontype)) {// 添加服务
			HashMap serverid = new HashMap();
/*            StringBuffer menuTempXMl = new StringBuffer();
            menuTempXMl.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
            menuTempXMl.append("<param><menu menuname=\"我要应聘\"   menutype=\"recruit\" order=\"1\"></menu>");
            menuTempXMl.append("<menu menuname=\"个人中心\"   menutype=\"selfCode\" order=\"2\"></menu>");
            menuTempXMl.append("<menu menuname=\"了解我们\"   menutype=\"other\" order=\"3\"></menu>");
            menuTempXMl.append(" </param>");*/
            sql.append("SELECT MAX(wxitemid) FROM t_sys_weixin_param");
			int itemid = 1;
			RowSet rs =null;
			try {
				rs = dao.search(sql.toString());
				sql.setLength(0);
				if (rs.next()) {
					itemid = rs.getInt(1) + 1;
				} else {
					itemid = 1;
				}
				sql.append(
						"INSERT into t_sys_weixin_param (wxsetid,wxitemid,wxname,appid,app_secret,url) VALUES (?,?,?,?,?,?)");
				list.add(wxsetid);
				list.add(itemid);
				list.add(name);
				list.add(APPID);
				list.add(AppSecret);
				list.add(url);
//				list.add(menuTempXMl.toString());
//				list.add(servertype);
//				list.add(description);
				dao.insert(sql.toString(), list);
				sql.setLength(0);
				list.clear();
				serverid.put("serverid", itemid);
				this.getFormHM().put("serverid", serverid);
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
			    PubFunc.closeResource(rs);
			}
		} else if ("update".equals(optiontype)) {// 修改服务号配置
			int serverid = (Integer) map.get("serverid");
			sql.append(
					"UPDATE t_sys_weixin_param SET appid=? , app_secret=? , url=? WHERE wxitemid=?");
//			list.add(servertype);
//			list.add(name);
			list.add(APPID);
			list.add(AppSecret);
			list.add(url);
//			list.add(servertype);
//			list.add(description);
			list.add(serverid);
			try {
				dao.update(sql.toString(), list);
				sql.setLength(0);
				list.clear();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
