/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Category;
import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title:SearchSAPMainData
 * </p>
 * <p>
 * Description:后台作业中执行的类，将SAP主数据保存到codeitem表中
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-08-08
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SearchSAPMainData implements Job {
	// 日志
	private Category log = Category.getInstance(SearchSAPMainData.class
			.getName());

	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();

			// 公司代码codesetid
			String companySetId = SystemConfig
					.getPropertyValue("nxyp_maindata_companycodesetid");

			// 成本中心codesetid
			String costcenterSetId = SystemConfig
					.getPropertyValue("nxyp_maindata_costcentersetid");

			// 利润中心codesetid
			String profitcenterSetId = SystemConfig
					.getPropertyValue("nxyp_maindata_profitcentersetid");

			// 凭证类型codesetid
			String voucherSetId = SystemConfig
					.getPropertyValue("nxyp_maindata_vouchersetid");

			// 总账科目codesetid
			String subjectSetId = SystemConfig
					.getPropertyValue("nxyp_maindata_subjectsetid");

			// wbs项目codesetid
			String wbsSetId = SystemConfig
					.getPropertyValue("nxyp_maindata_wbssetid");

			String sapUrl = SystemConfig.getPropertyValue("nxyp_maindata_url");

			if (sapUrl == null || sapUrl.trim().length() <= 0) {
				log
						.error("未成功获取SAP的IP地址，请在system.properties文件中设置nxyp_maindata_url的值！");
				return;
			}
			

			// 公司代码
			if (companySetId != null && companySetId.trim().length() > 0) {
				try {
					// 调用webservice获取数据
					String responseStr = getSAPContent("H_T001", sapUrl);
					
//					if (responseStr == null || responseStr.length() <= 0) {
//						responseStr = "<?xml version='1.0' encoding='GBK'?><Rowsets><Rowset></Rowset></Rowsets>";
//					}
					if (responseStr != null && responseStr.length() > 0) {
						// 处理获取的内容
						handlerStr("/Rowsets/Rowset/Row", "BUKRS", "BUTXT",
								companySetId, responseStr, conn, null, null);
					}

				} catch (Exception e) {
					e.printStackTrace();
					log.error("公司代码同步失败！");
				}
			} else {
				log
						.warn("公司代码代码类代号为空，system.properties文件中未设置nxyp_maindata_companycodesetid的值！");
			}

			// 成本中心
			if (costcenterSetId != null && costcenterSetId.trim().length() > 0) {

				// 调用webservice获取数据
				String responseStr = getSAPContent("ZTRAC_KOSTL", sapUrl);
				
//				if (responseStr == null || responseStr.length() <= 0) {
//					responseStr = "<?xml version='1.0' encoding='GBK'?><Rowsets><Rowset></Rowset></Rowsets>";
//				}

				if (responseStr != null && responseStr.length() > 0) {
					// 处理获取的内容
					handlerStr("/Rowsets/Rowset/Row", "KOSTL", "KTEXT",
							costcenterSetId, responseStr, conn, "DATAB", "DATBI");
				}
				

			} else {
				log
						.warn("成本中心代码类代号为空，system.properties文件中未设置nxyp_maindata_costcentersetid的值！");
			}

			// 利润中心
			if (profitcenterSetId != null
					&& profitcenterSetId.trim().length() > 0) {

				// 调用webservice获取数据
				String responseStr = getSAPContent("PRCTS", sapUrl);
				
//				if (responseStr == null || responseStr.length() <= 0) {
//					responseStr = "<?xml version='1.0' encoding='GBK'?><Rowsets><Rowset></Rowset></Rowsets>";
//				}

				if (responseStr != null && responseStr.length() > 0) {
					// 处理获取的内容
					handlerStr("/Rowsets/Rowset/Row", "PRCTR", "MCTXT",
							profitcenterSetId, responseStr, conn, null, null);
				}
			} else {
				log
						.warn("利润中心代码类代号为空，system.properties文件中未设置nxyp_maindata_profitcentersetid的值！");
			}

			// 凭证类型
			if (voucherSetId != null && voucherSetId.trim().length() > 0) {
				// 调用webservice获取数据
				String responseStr = getSAPContent("H_T003", sapUrl);
				
//				if (responseStr == null || responseStr.length() <= 0) {
//					responseStr = "<?xml version='1.0' encoding='GBK'?><Rowsets><Rowset></Rowset></Rowsets>";
//				}

				if (responseStr != null && responseStr.length() > 0) {
					// 处理获取的内容
					handlerStr("/Rowsets/Rowset/Row", "BLART", "LTEXT",
							voucherSetId, responseStr, conn, null, null);
				}
			} else {
				log
						.warn("凭证类型代码类代号为空，system.properties文件中未设置nxyp_maindata_vouchersetid的值！");
			}

			// 总账科目
			if (subjectSetId != null && subjectSetId.trim().length() > 0) {
				// 调用webservice获取数据
				String responseStr = getSAPContent("ZSH_HKONT", sapUrl);
				
//				if (responseStr == null || responseStr.length() <= 0) {
//					responseStr = "<?xml version='1.0' encoding='GBK'?><Rowsets><Rowset></Rowset></Rowsets>";
//				}
				
				if (responseStr != null && responseStr.length() > 0) {
					// 处理获取的内容
					handlerStr("/Rowsets/Rowset/Row", "BUKRS,RACCT", "TXT50",
							subjectSetId, responseStr, conn, null, null);
				}
			} else {
				log
						.warn("总账科目代码类代号为空，system.properties文件中未设置nxyp_maindata_subjectsetid的值！");
			}

			// wbs项目
			if (wbsSetId != null && wbsSetId.trim().length() > 0) {
				// 调用webservice获取数据
				String responseStr = getSAPContent("ZSH_PRSTP", sapUrl);
				
//				if (responseStr == null || responseStr.length() <= 0) {
//					responseStr = "<?xml version='1.0' encoding='GBK'?><Rowsets><Rowset></Rowset></Rowsets>";
//				}

				if (responseStr != null && responseStr.length() > 0) {
					// 处理获取的内容
					handlerStr("/Rowsets/Rowset/Row", "POSID", "POST1",
							wbsSetId, responseStr, conn, null, null);
				}
			} else {
				log
						.warn("wbs项目代码类代号为空，system.properties文件中未设置nxyp_maindata_wbssetid的值！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (conn == null) {
				log
						.error("未连接到数据库，请检查配置参数！代码conn = AdminDb.getConnection()执行时抛异常！");
			}
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void handlerStr(String path, String codeName, String descName,
			String codeSetId, String responseStr, Connection conn, String startTime, String endTime) {
		// code集合
		ArrayList dataList = new ArrayList();
		try {
			// 解析xml
			PareXmlUtils utils = new PareXmlUtils(responseStr);
			List elList = utils.getNodes("/Rowsets/Rowset/Row");
			if (elList != null) {
				for (int i = 0; i < elList.size(); i++) {
					Element el = (Element) elList.get(i);
					
					if (startTime != null && startTime.length() > 0 && endTime != null && endTime.length() > 0) {
						String start = el.getChildText(startTime);
						String end = el.getChildText(endTime);
						
						Date startDate = DateUtils.getDate(start, "yyyyMMdd");
						Date endDate = DateUtils.getDate(end, "yyyyMMdd");
						Date nowDate = new Date();
						
						if (!(nowDate.getTime() >= startDate.getTime() && nowDate.getTime() <= endDate.getTime())) {
							continue;
						}
						
					}
										
					ArrayList list = new ArrayList();
					list.add(codeSetId);

					String[] code = codeName.split(",");
					String codeId = "";
					for (int j = 0; j < code.length; j++) {
						codeId += el.getChildText(code[j]);
					}
					// 编号
					list.add(codeId);
					// 名称
					list.add(el.getChildText(descName));
					// parentid
					list.add(codeId);
					// childid
					list.add(codeId);
					// 结束时间
					list.add(DateUtils.getSqlDate("9999-12-31 00:00:00",
							"yyyy-MM-dd HH:mm:ss"));
					// 开始时间
					list.add(DateUtils.getSqlDate("1949-10-01 00:00:00",
							"yyyy-MM-dd HH:mm:ss"));

					dataList.add(list);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("解析xml时出错，错误原因：" + e.getMessage());
		}
		// 删除公司代码
		deleteCodeItem(codeSetId, conn);

		// 添加公司代码
		insertToCodeItem(dataList, conn);

	}

	/**
	 * 添加代码
	 * 
	 * @param list
	 * @param conn
	 */
	private void insertToCodeItem(ArrayList list, Connection conn) {
		String sql = "insert into codeitem(codesetid, codeitemid, codeitemdesc,parentid,childid,invalid,layer,end_date,start_date)"
				+ "values(?,?,?,?,?,1,1,?,?)";
		try {
//			for (int i = 0; i < list.size(); i++) {
//				ArrayList li = (ArrayList) list.get(i);
//				System.out.println(li.get(1) + "	" + li.get(2));
//			}
			ContentDAO dao = new ContentDAO(conn);
			dao.batchInsert(sql, list);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("批量添加代码项时失败！错误原因：" + e.getMessage());
		}
	}

	/**
	 * 删除代码
	 * 
	 * @param codeSetId
	 * @param conn
	 */
	private void deleteCodeItem(String codeSetId, Connection conn) {
		String sql = "delete from codeitem where codesetid='" + codeSetId
				+ "' ";
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.delete(sql, new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("删除代码类代号为" + codeSetId + "的代码时失败！错误原因：" + e.getMessage());
		}
	}

	/**
	 * 调用sapwebservice，返回消息
	 * 
	 * @param param
	 * @param url
	 * @return
	 */
	private String getSAPContent(String param, String url) {
		SOAPConnection con = null;
		StringBuffer responseStr = new StringBuffer();
//		String mainUrl = SystemConfig.getPropertyValue("nxyp_maindata_url");
//		url = url + mainUrl;

		try {
			SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
			con = factory.createConnection();
			SOAPMessage request = MessageFactory.newInstance().createMessage();

			SOAPBody body = request.getSOAPBody();
			// 方法
			SOAPElement getMessage = body.addChildElement("Z_HR_SEARCH_HELP",
					"urn", "urn:sap-com:document:sap:rfc:functions");
			getMessage.setEncodingStyle(SOAPConstants.URI_NS_SOAP_ENCODING);
			// 第一个参数
			SOAPElement in0 = getMessage.addChildElement("ET_XML");
			SOAPElement xml = in0.addChildElement("item")
					.addChildElement("XML");

			xml.addTextNode("0");
			// 第二个参数
			SOAPElement in1 = getMessage.addChildElement("I_MAXROWS");
			in1.addTextNode("0");
			// 第三个参数
			SOAPElement in2 = getMessage.addChildElement("I_SHLPNAME");
			in2.addTextNode(param);
//			request.writeTo(System.out);
			SOAPMessage response = con.call(request, url);

			SOAPBody responseBody = response.getSOAPBody();
			NodeList it = responseBody.getFirstChild().getChildNodes();
			for (int j = 0; j < it.getLength(); j++) {
				Node node = it.item(j);
				if ("ET_XML".equalsIgnoreCase(node.getNodeName())) {
					NodeList nodeList = node.getChildNodes();
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node child = nodeList.item(i);
						responseStr.append(child.getFirstChild()
								.getTextContent());
					}
				}
			}

			
//			 System.out.println("\n响应内容："+responseStr.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			String desc = "";
			// 公司代码
			if ("H_T001".equals(param)) {
				desc = "公司代码";
			} else if ("PRCTS".equals(param)) {
				desc = "利润中心";
			} else if ("TRAC_KOSTL".equals(param)) {
				desc = "成本中心";
			} else if ("H_T003".equals(param)) {
				desc = "凭证类型";
			} else if ("ZSH_HKONT".equals(param)) {
				desc = "总账科目";
			} else if ("ZSH_PRSTP".equals(param)) {
				desc = "WBS项目";
			}

			if (responseStr == null
					|| responseStr.toString().trim().length() <= 0) {
				log.error(desc + "调用SAP的webservice失败,未获得有效返回值！");
			}
			
			
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (responseStr.length() != 0) {
			responseStr.insert(0, "<?xml version='1.0' encoding='GBK'?>");
		} else  {
			return "";
		}
		
		return responseStr.toString();
	}

	public static void main(String[] args) {
		SearchSAPMainData data = new SearchSAPMainData();
		// 公司代码
//		data.getSAPContent("H_T001", "http://zypdev.eppen.com.cn:8000");
//
//		// 利润中心：PRCTS
//		data.getSAPContent("PRCTS", "http://zypdev.eppen.com.cn:8000");
//
//		// 成本中心：TRAC_KOSTL
//		data.getSAPContent("TRAC_KOSTL", "http://zypdev.eppen.com.cn:8000");
//
//		// 凭证类型：H_T003
//		data.getSAPContent("H_T003", "http://zypdev.eppen.com.cn:8000");
//
//		// 总账科目：ZSH_HKONT
//		data.getSAPContent("ZSH_HKONT", "http://zypdev.eppen.com.cn:8000");
		
		// wbs主数据
//		data.getSAPContent("ZSH_PRSTP", "http://zypdev.eppen.com.cn:8000");
		
		Des des = new Des();
		System.out.println(des.EncryPwdStr("103176|2012-09-25 14:30:00|OA"));
	}
}
