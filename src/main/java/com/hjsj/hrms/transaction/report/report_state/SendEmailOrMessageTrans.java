package com.hjsj.hrms.transaction.report.report_state;

import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * duml 2011-04-20
 */
public class SendEmailOrMessageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String unitcode = (String) this.getFormHM().get("unitcode");
			String isSub = (String) this.getFormHM().get("isSub");
			String sendtype = (String) this.getFormHM().get("sendtype");
			String content = (String) this.getFormHM().get("content");
			content = PubFunc.keyWord_reback(content);
			String title = (String) this.getFormHM().get("title");
			title = SafeCode.decode(title);
			EMailBo emailBo = null;
			try {
				emailBo = new EMailBo(this.getFrameconn(), true, "");
			} catch (Exception e1) {
				e1.printStackTrace();
				this.getFormHM().put("info", 2);
				return;
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			if ("null".equals(unitcode)) {
				try {
					// 查询报表负责人
					this.frowset = dao.search(
							"select unitcode from operuser where username='" + this.userView.getUserName() + "'");
					if (this.frowset.next())
						unitcode = this.frowset.getString(1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if ("null".equals(unitcode) || StringUtils.isEmpty(unitcode)) {
				this.getFormHM().put("info", 3);
				return;
			}
			int info = 2;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if ("1".equals(sendtype)) { // 邮件通知
				ArrayList<LazyDynaBean> emails = new ArrayList<LazyDynaBean>();
				if ("1".equals(isSub)) { // 包含下级
					ArrayList unitslist = this.getUnitsList(unitcode);
					if (unitslist != null && unitslist.size() > 0) {
						for (int i = 0; i < unitslist.size(); i++) {
							String content1 = content;
							LazyDynaBean abean1 = (LazyDynaBean) unitslist.get(i);
							try {
								sendEmail(content1, title, format, abean1, emailBo);
							} catch (Exception e) {
								info = 4;
							}
						}
					} else {
						info = 4;
					}
				} else { // 不包含下级
					LazyDynaBean abean1 = this.getunit(unitcode);
					try {
						sendEmail(content, title, format, abean1, emailBo);
						info = 1;
					} catch (Exception e) {
						info = 4;
					}
				}
			} else { // 短信通知
				if ("1".equals(isSub)) { // 包含下级
					ArrayList unitslist = this.getUnitsList(unitcode);
					if (unitslist != null) {
						ArrayList destlist = new ArrayList();
						for (int i = 0; i < unitslist.size(); i++) {
							LazyDynaBean bean = (LazyDynaBean) unitslist.get(i);
							LazyDynaBean dyvo = new LazyDynaBean();
							dyvo.set("sender", this.userView.getUserFullName());
							dyvo.set("receiver", (String) bean.get("usrName"));
							if (bean.get("phone") != null && ((String) bean.get("phone")).length() != 0) {
								dyvo.set("phone_num", (String) bean.get("phone"));
							} else {
								continue;
							}
							String usrName = (String) bean.get("usrName");
							if (content != null && content.length() != 0) {
								String content1 = content;
								content1 = content1.replaceAll("\\[", "");
								content1 = content1.replaceAll("\\]", "");
								if (content1.indexOf("(~系统时间~)") != -1) {
									content1 = content1.replaceAll("(~系统时间~)", format.format(new Date()));
								}
								if (content.indexOf("(~报表负责人~)") != -1) {
									content1 = content1.replaceAll("\\(~报表负责人~\\)", usrName);
								}
								content1 = content1.replaceAll(" ", "");
								content1 = content1.replaceAll("\\r", "");
								content1 = content1.replaceAll("\\n", "");
								content1 = content1.replaceAll("\\r\\n", "");
								content1 = title + "\r\n" + content1;
								dyvo.set("msg", content1);
							}

							destlist.add(dyvo);
						}
						/**
						 * xiegh destlist存放的是发送人姓名，接收人姓名，手机号，短信内容 加判断
						 * 如果destlist为空，则直接给info赋2，提示“没有设置服务器信息”标志
						 */
						if (destlist == null || destlist.size() == 0) {
							info = 2;
						} else {
							try {
								info = 1;
								SmsBo smsbo = new SmsBo(this.frameconn);
								smsbo.batchSendMessage(destlist);
							} catch (Exception e) {
								info = 2;
								e.printStackTrace();
							}
						}
					}
				} else { // 不包含下级
					ArrayList destlist = new ArrayList();
					LazyDynaBean bean = this.getunit(unitcode);
					LazyDynaBean dyvo = new LazyDynaBean();
					String usrName = "";
					if (bean != null) { // wangcq 2014-11-17
						dyvo.set("sender", this.userView.getUserFullName());
						dyvo.set("receiver", (String) bean.get("usrName"));
						// update by wangchaoqun on 2014-9-16
						// 增加数据类型判断，当bean.get("phone")不为String类型时会出现ClassCastException
						if (bean.get("phone") != null && bean.get("phone") instanceof String
								&& ((String) bean.get("phone")).length() != 0) {
							dyvo.set("phone_num", (String) bean.get("phone"));
						} else {
							this.getFormHM().put("info", "2");// bug号：46015
																// 按组织机构查阅报表状态/发送通知，没有配置短信接口时发送短信，包含下级时提示发送失败，不包含下级时没有提示
							return;
						}
						usrName = (String) bean.get("usrName");
					}
					if (content != null && content.length() != 0) {
						String content1 = content;
						content1 = content1.replaceAll("\\[", "");
						content1 = content1.replaceAll("\\]", "");
						if (content1.indexOf("(~系统时间~)") != -1) {
							content1 = content1.replaceAll("\\(~系统时间~\\)", format.format(new Date()));
						}
						if (content.indexOf("(~报表负责人~)") != -1) {
							content1 = content1.replaceAll("\\(~报表负责人~\\)", usrName);
						}
						content1 = content1.replaceAll(" ", "");
						content1 = content1.replaceAll("\\r", "");
						content1 = content1.replaceAll("\\n", "");
						content1 = content1.replaceAll("\\r\\n", "");
						dyvo.set("msg", content1);
					}
					destlist.add(dyvo);
					/**
					 * xiegh destlist存放的是发送人姓名，接收人姓名，手机号，短信内容 加判断
					 * 如果destlist为空，则直接给info赋2，提示“没有设置服务器信息”标志
					 */
					if (destlist == null || destlist.size() == 0) {
						info = 2;
					} else {
						try {
							info = 1;
							SmsBo smsbo = new SmsBo(this.frameconn);
							smsbo.batchSendMessage(destlist);
						} catch (Exception e) {
							info = 2;
							e.printStackTrace();
						}
					}
				}

			}
			this.getFormHM().put("info", String.valueOf(info));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @param content
	 * @param title
	 * @param format
	 * @param abean1
	 * @param fromAddr
	 * @param emailBo
	 * @return
	 * @throws GeneralException
	 */
	private void sendEmail(String content, String title, SimpleDateFormat format, LazyDynaBean abean1, EMailBo emailBo)
			throws Exception {
		if (abean1 == null || abean1.get("email") == null)
			throw new Exception("报表负责人没有设置邮箱！");
		String content1 = content;
		String sendTo = (String) abean1.get("email");
		String usrName = (String) abean1.get("usrName");
		if (content != null && content.length() != 0) {
			content1 = content1.replaceAll("\\[", "");
			content1 = content1.replaceAll("\\]", "");
			if (content1.indexOf("(~系统时间~)") != -1) {
				content1 = content1.replaceAll("\\(~系统时间~\\)", format.format(new Date()));
			}
			if (content.indexOf("(~报表负责人~)") != -1) {
				content1 = content1.replaceAll("\\(~报表负责人~\\)", usrName);
			}
			content1 = content1.replaceAll(" ", "&nbsp");
			content1 = content1.replaceAll("\\r", "<br>");
			content1 = content1.replaceAll("\\n", "&nbsp");
			content1 = content1.replaceAll("\\r\\n", "<br>");
		}
		emailBo.sendEmail(title, content1, "", sendTo);
	}

	public ArrayList getUnitsList(String unitcode) {
		ArrayList unitslist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		// liuy 2015-1-24 6808：按组织机构查阅报表状态/发送通知：发送后后台报错
		sql.append("select * from  (select * from operuser where (email is not null and " + Sql_switcher.length("email")
				+ "!=0)or (phone is not null and " + Sql_switcher.length("phone")
				+ "!=0) ) o inner join (select * from tt_organization where unitcode like '");
		sql.append(unitcode);
		sql.append("%') tt on o.unitcode=tt.unitcode");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				if (this.frowset.getString("FullName") != null && this.frowset.getString("FullName").length() != 0) {
					bean.set("usrName", this.frowset.getString("FullName"));
				} else {
					bean.set("usrName", this.frowset.getString("UserName"));
				}
				bean.set("unitcode", this.frowset.getString("unitcode"));
				bean.set("phone", this.frowset.getString("phone"));
				bean.set("email", this.frowset.getString("email"));
				unitslist.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return unitslist;
	}

	public LazyDynaBean getunit(String unitcode) {
		LazyDynaBean bean = null;
		StringBuffer sql = new StringBuffer();
		// update by wangchaoqun on 2014-9-16 对数据库进行判断，加入相应的取长度方法
		sql.append("select * from   (select * from operuser where (email is not null and "
				+ Sql_switcher.length("email") + " !=0)or (phone is not null and " + Sql_switcher.length("phone")
				+ " !=0) ) o inner join (select * from tt_organization where unitcode = '");
		sql.append(unitcode);
		sql.append("') tt on o.unitcode=tt.unitcode");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				bean = new LazyDynaBean();
				if (this.frowset.getString("FullName") != null && this.frowset.getString("FullName").length() != 0) {
					bean.set("usrName", this.frowset.getString("FullName"));
				} else {
					bean.set("usrName", this.frowset.getString("UserName"));
				}
				bean.set("unitcode", this.frowset.getString("unitcode"));
				bean.set("phone", this.frowset.getString("phone"));
				bean.set("email", this.frowset.getString("email"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

}
