package com.hjsj.hrms.businessobject.board;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Title: 发送通知 </p>
 * <p>Description:将ehr中消息发送到微信 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-4-18 下午1:49:52</p>
 * @author jingq
 * @version 1.0
 */
public class SendMsgToWXBo {
	
	/**
	 * 将公告发送到微信
	 * @param conn 
	 * @param userView 
	 * @param id 需要发送的公告ID
	 * @param title 标题
	 * @param description 内容
	 * @param picUrl 图片url
	 * @param url 点击图文消息进入页面地址
	 * @return
	 * @throws SQLException 
	 */
	public static boolean SendBoardToWX(Connection conn, UserView userView, String id,
			String title, String description,String picUrl, String url)
			throws SQLException {
		boolean flag = false;
		ArrayList idlist = new ArrayList();
		ResultSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		try {
			// 取得拥有该公告权限的角色、机构、人员
			BoardBo boardBo = new BoardBo(conn, userView);
			String pArr[] = boardBo.getPriUser(id);
			String selectPerson = "";
			if (pArr != null && pArr.length == 2) {
				selectPerson = pArr[0];
			}
			if (selectPerson.length() > 0) {
				String[] arr = selectPerson.split(",");
				Set set = new HashSet();
				String rolestr = "";
				Set orgset = new HashSet();
				for (int i = 0; i < arr.length; i++) {
					String str = arr[i];
					if (str.indexOf(":") != -1) {
						String index = str.split(":")[0];
						String value = str.split(":")[1];
						if ("1".equals(index)) {// 角色
							rolestr += "'" + value + "',";
						} else if ("4".equals(index)) {// 人员
							set.add(value.substring(3));
						}
					} else {// 机构
						String[] orgs = str.split("`");
						for (int j = 0; j < orgs.length; j++) {
							orgset.add(orgs[j]);
						}
					}
				}
				String users = "";
				// 根据角色取得业务用户、人员、机构
				if (rolestr.length() > 1) {
					String sql = "select staff_id,status from t_sys_staff_in_role where role_id in ("
							+ rolestr.substring(0, rolestr.length() - 1) + ")";
					rs = dao.search(sql);
					while (rs.next()) {
						String status = rs.getString("status");
						String staff_id = rs.getString("staff_id");
						if ("0".equals(status)) {// 业务用户
							users += "'" + staff_id + "',";
						} else if ("1".equals(status)) {// 人员
							set.add(staff_id.substring(3));
						} else if ("2".equals(status)) {// 机构
							orgset.add(staff_id);
						}

					}
				}
				// 根据业务用户取得关联的人员
				if (users.length() > 1) {
					String sql = "select A0100 from OperUser where UserName in ("
							+ users.substring(0, users.length() - 1) + ")";
					rs = dao.search(sql);
					while (rs.next()) {
						String user = rs.getString("A0100") == null ? "" : rs
								.getString("A0100");
						if (!"".equals(user)) {
							set.add(user);
						}
					}
				}
				// 机构
				String orgstr = "";
				for (Iterator iterator = orgset.iterator(); iterator.hasNext();) {
					String obj = (String) iterator.next();
					orgstr += " or E01A1 like '" + obj.substring(2) + "%'";
				}
				int index = 0;
				// 人员
				String userstr = " A0100 in (";
				for (Iterator iterator = set.iterator(); iterator.hasNext();) {
					index++;
					String object = (String) iterator.next();
					if(index%1000!=0){
						userstr += "'" + object + "',";
					} else {
						userstr = userstr.substring(0,userstr.length()-1)+") or A0100 in (";
					}
				}
				if (userstr.length() > 11) {
					userstr = userstr.substring(0, userstr.length() - 1) + ") ";
				} else {
					userstr = "";
					if (orgstr.length() > 0) {
						orgstr = orgstr.substring(3);
					} else {
						orgstr = " 1 = 1";
					}
				}
				// 登陆系统时用的指标
				String username = getUserName();

				String sql = "select " + username + " from UsrA01 where "
						+ userstr + orgstr;
				rs = dao.search(sql);
				while (rs.next()) {
					String user = rs.getString(username) == null ? "" : rs
							.getString(username);
					if (!"".equals(user)) {
                        idlist.add(user);
                    }
				}
				String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userView.getUserName()+","+userView.getPassWord()));
				url = (userView.getServerurl()+"/selfservice/infomanager/board/viewboard.do?b_query=link&a_id="+ id + "&appfwd=1&etoken=" + etoken);
				if(!"".equals(ConstantParamter.getAttribute("wx", "corpid"))) {
                    flag = WeiXinBo.sendMsgToPerson(idlist,title,description,picUrl,url);
                }
				if(!"".equals(ConstantParamter.getAttribute("DINGTALK", "corpid"))) {
                    flag =DTalkBo.sendMessage(idlist,title,description,picUrl,url);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return flag;
	}
	
	/**
	 * 获取用户登陆指标
	 * @return
	 */
	public static String getUserName(){
		String username = "";
		RecordVo login_vo = ConstantParamter
				.getConstantVo("SS_LOGIN_USER_PWD");
		if (login_vo == null) {
			username = "username";
		} else {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx == -1) {
				username = "username";
			} else {
				username = login_name.substring(0, idx);
				if ("#".equals(username) || "".equals(username)) {
					username = "username";
				}
			}
		}
		return username;
	}
	
}
