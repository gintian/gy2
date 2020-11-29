package com.hjsj.hrms.servlet.sms;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.util.List;

public class SmsAcceptGSTXServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SmsAcceptGSTXServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 数据库连接
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		SmsBo bo = new SmsBo(conn);
		String strOption = request.getParameter("OPTION");
		String args = request.getParameter("args");
		if ("GET".equalsIgnoreCase(strOption)
				|| "ALT".equalsIgnoreCase(strOption)
				|| "PUT".equalsIgnoreCase(strOption)) {// 金格平台接收短信
			if ("GET".equalsIgnoreCase(strOption)) {
				String strRecord = request.getParameter("RECORD");
				String strMobile = request.getParameter("MOBILE");
				String strContent = request.getParameter("CONTENT");
				String strDateTime = request.getParameter("DATETIME");
				try {
					bo.addAcceptMessage(strMobile, "", strContent, 2,
							strDateTime);
				} catch (Exception e) {
					System.out.println(e.toString());
				}

				try {
					bo.invokYWInterface(strMobile, "", strContent, strDateTime);
				} catch (Exception ee) {
					ee.printStackTrace();
				}

			}
		} else if (args != null && args.length() > 0) {// socket服务
			request.setCharacterEncoding("GB2312");
			response.setCharacterEncoding("GB2312");
			args = request.getParameter("args");
			//args = new String(args.getBytes("iso8859-1"),"GB2312");
			PrintWriter out = response.getWriter();
			try {
				// 用SAX方式读取xml
				Document doc = PubFunc.generateDom(args);
				// xml路径
//				String str_path = "/messages/msg";
//				XPath xpath = XPath.newInstance(str_path);
				// 所有短信信息
				List list = doc.getRootElement().getChildren();
				for (int i = 0; i < list.size(); i++) {
					Element el = (Element) list.get(i);
					String id = el.getChildText("id");
					String phone = el.getChildText("phones");
					String content = el.getChildText("content");
					String stime = el.getChildText("stime");
					bo.addAcceptMessage(phone, "",
							content, 2, stime);
					bo.invokYWInterface(phone, "", content, stime);
				}
				
				out.println("0");
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
				out.println("-1");
				out.flush();
			} finally {
				out.close();
			}
		} else {// 光闪通讯接收短信

			// 设置类型
			response.setContentType("text/xml");
			// 设置编码
			request.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			// 获得得到的xml字符窜
			String xml = getXml(request);
			// 保存正确的id
			StringBuffer rightBuff = new StringBuffer();
			// 保存错误的id
			StringBuffer erroBuff = new StringBuffer();

			if (xml == null || xml.length() == 0) {
				return;
			} else {

				try {
					// 用SAX方式读取xml
					Document doc = PubFunc.generateDom(xml);
					// xml路径
					String str_path = "/ZWTSMSMessage/MOSms";
					XPath xpath = XPath.newInstance(str_path);
					// 所有短信信息
					List list = xpath.selectNodes(doc);

					// 逐个处理短信
					for (int i = 0; i < list.size(); i++) {
						Element el = (Element) list.get(i);
						Element msEl = el.getChild("Message");
						Element SmsIDEl = el.getChild("SmsID");
						Element targetEl = el.getChild("Target");
						Element sourceEl = el.getChild("Source");
						Element channelEl = el.getChild("Channel");
						Element MoDateEl = el.getChild("MoDate");
						String senders = targetEl.getText();
						String[] sender = senders.split(",");
						boolean flag = false;
						for (int j = 0; j < sender.length; j++) {
							if (sender[j] != null && sender[j].length() > 0) {
								String msg = msEl.getText();
								if (msg == null || msg.length() == 0) {
									break;
								}
								String receiver = sourceEl.getText()
										+ channelEl.getText();
								String date = MoDateEl.getText();
								flag = bo.addAcceptMessage(sender[j], receiver,
										msg, 2, date);
								try {
									bo.invokYWInterface(sender[j], receiver,
											msg, date);
								} catch (Exception ee) {
									ee.printStackTrace();
								}

							}
						}

						// 保存成功的
						if (flag) {
							rightBuff.append(",");
							rightBuff.append(SmsIDEl.getText());
						} else {// 保存失败的
							erroBuff.append(",");
							erroBuff.append(SmsIDEl.getText());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 响应
			out.println("<ZWTSMSMessage type=\"resp\">");
			out.println("<Status>");
			if (rightBuff.length() > 0) {
				out.println("<SmsID>");
				out.println(rightBuff.substring(1));
				out.print("</SmsID>");
				out.println("<Data>0</Data>");
			}
			if (erroBuff.length() > 0) {
				out.println("<SmsID>");
				out.println(erroBuff.substring(1));
				out.print("</SmsID>");
				out.println("<Data>1</Data>");
			}
			out.println("</Status>");
			out.println("</ZWTSMSMessage>");

			out.flush();
			out.close();
		}

		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String MarkText(String str) {
		String oldStr = "'";
		String newStr = "''";
		int n = 0;
		if (str.indexOf(oldStr) > -1)
			while (str.indexOf(oldStr, n) > -1) {
				int i = str.length();
				if (str.indexOf(oldStr, n) == 0) {
					str = String.valueOf(newStr)
							+ String.valueOf(str.substring(1, i));
					n = 2;
				} else {
					int t = str.indexOf(oldStr, n);
					str = String.valueOf(String
							.valueOf((new StringBuffer(String.valueOf(String
									.valueOf(str.substring(0, t))))).append(
									newStr).append(str.substring(t + 1, i))));
					n = t + 2;
				}
			}
		return str;
	}

	/**
	 * 求移动电话号码
	 * 
	 * @return
	 */
	private String getMobileNumber() {
		RecordVo vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
		if (vo == null)
			return "";
		String field_name = vo.getString("str_value");
		if (field_name == null || "".equals(field_name))
			return "";
		FieldItem item = DataDictionary.getFieldItem(field_name);
		if (item == null)
			return "";
		/** 分析是否构库 */
		if ("0".equals(item.getUseflag()))
			return "";
		return field_name;
	}

	/**
	 * 获得短信xml
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String xml字符窜
	 */
	private String getXml(HttpServletRequest request) {

		StringBuffer buff = new StringBuffer();
		try {
			InputStream input = request.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input, "utf-8"));
			String read = "";
			// 按行读取，并加上换行符
			while ((read = reader.readLine()) != null) {
				buff.append(read + "\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buff.toString();
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
