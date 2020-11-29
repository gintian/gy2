package com.hjsj.hrms.transaction.mobileapp.setting;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p>Title: SettingFeedbackTrans</p>
 * <p>Description: 设置模块 </p>
 * <p> Company: hjsj </p>
 * <p> create time: 2013-11-18 上午9:53:31 </p>
 * 此交易类未使用
 * @author yangj
 * @version 1.0
 */
public class SettingTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String FIND = "1";// 获取邮件地址
	private final String SEND = "2";// 发送邮件

	public void execute() throws GeneralException {
//		HashMap hm = this.getFormHM();
//		String message = "";
//		String succeed = "true";
//		// 邮件地址
//		String email = "";
//		try {
//			String transType = (String) hm.get("transType");
//			hm.remove("transType");
//			UserView userView = this.getUserView();
//			// 不同业务流程分支点
//			if (transType != null) {
//				if (FIND.equals(transType)) {
//					email = userView.getUserEmail();
//					hm.put("email", email);
//					hm.put("transType", FIND);
//				} else if (SEND.equals(transType)) {
//					hm.put("transType", SEND);
//					// 邮件地址
//					email = (String) hm.get("email");
//					// 邮件标题
//					String topic = ResourceFactory.getProperty("mobileapp.setup.email.topic");
//					// 邮件内容
//					String msg = (String) hm.get("msg");
//					msg = this.msgToHtml(msg);
//					// 如果传入的邮箱地址为空时，设置为匿名用户
//					if (email != null) {
//						if (!this.emailFormat(email)) {
//							email = "";
//						}
//					}
//					// 调用系统邮箱服务器给这两个邮箱发邮件support@hjsoft.com.cn、dev@hjsoft.com.cn
//					Connection conn = this.getFrameconn();
//					EMailBo mailbo = new EMailBo(conn, true, "");
//					mailbo.sendEmail(topic, msg, null, email, "support@hjsoft.com.cn");
//					mailbo.sendEmail(topic, msg, null, email, "dev@hjsoft.com.cn");
//				}
//			} else {
//				message = ResourceFactory
//						.getProperty("mobileapp.setup.error.transTypeError");
//				hm.put("message", message);
//			}
//
//		} catch (Exception e) {
//			succeed = "false";
//			e.printStackTrace();
//			throw GeneralExceptionHandler.Handle(e);
//		} finally {
//			hm.put("succeed", succeed);
//		}

	}

	/**
	 * 
	 * @Title: emailFormat
	 * @Description:验证邮箱是否合法
	 * @param email
	 * @return boolean
	 * @throws
	 */
	private boolean emailFormat(String email) {
		boolean tag = true;
		final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	/**
	 * 
	 * @Title: msgToHtml
	 * @Description: 将传人的字符串转换为网页格式
	 * @param msg
	 *            需要转换的字符串
	 * @return String
	 * @throws
	 */
	private String msgToHtml(String msg) {
		if (msg == null || "".equals(msg)) {
			return "";
		}
		msg = msg.replaceAll(">", "&rt;");
		msg = msg.replaceAll("\"", "");
		msg = msg.replaceAll("'", "'");
		msg = msg.replaceAll(" ", "&nbsp;");
		msg = msg.replaceAll("\r\n", "<br>");
		msg = msg.replaceAll("\r", "<br>");
		msg = msg.replaceAll("\n", "<br>");
		return msg;
	}
}
