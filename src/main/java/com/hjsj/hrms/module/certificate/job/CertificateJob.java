package com.hjsj.hrms.module.certificate.job;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.htmlparser.Parser;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CertificateJob implements Job {
	private Category cat = null;
	private Connection conn = null;
	private UserView userView;
	private ArrayList certIdlist = new ArrayList();
	private ArrayList listValidity = new ArrayList();
	private ArrayList listReturn = new ArrayList();

	String certSubset;
	String certBorrowSubset;
	String certCategoryItemId;
	String certEndDateItemId;
	String certStatus;
	ArrayList certNbase;
	String certName ;
	ArrayList<LazyDynaBean> emailList = new ArrayList<LazyDynaBean>();
	ArrayList paramList = new ArrayList();
	String strlistValidity = StringUtils.join(listValidity.toArray(), ",");
	String strlistReturn = StringUtils.join(listReturn.toArray(), ",");
	String type;
	HashMap paramsMap;
	HashMap certMap;
	String validity;
	String recede;
	String warnDate;
	String certId;
	String toPerson;

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		// 作业类id
		String jobId = context.getJobDetail().getName();
		// 添加日志
		cat = Category.getInstance(CertificateJob.class);

		try {
			// 人力资源系统的数据库连接
			conn = (Connection) AdminDb.getConnection();
		    userView = new UserView("su", conn);
			userView.canLogin(false);
			// 解析作业参数
			parseJobParam(conn, jobId, userView);
			CertificateConfigBo certificateConfigBo = new CertificateConfigBo(conn, userView);
		    certSubset = certificateConfigBo.getCertSubset();
			certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
			certCategoryItemId = certificateConfigBo.getCertCategoryItemId();
			certEndDateItemId = certificateConfigBo.getCertEndDateItemId();
			certStatus = certificateConfigBo.getCertStatus();
			certNbase = certificateConfigBo.getCertNbase();
			certName = certificateConfigBo.getCertName();
			
			for (int j = 0; j < certNbase.size(); j++) {
				for (int i = 0; i < certIdlist.size(); i++) {
					paramsMap = (HashMap) certIdlist.get(i);
					warnDate = (String) paramsMap.get("warnDate");
					type =(paramsMap.get("type") == null) ? "" :(String) paramsMap.get("type");
					toPerson = (String) paramsMap.get("toPerson");
					String nbase = (String) certNbase.get(j);
					String[] strPerson = toPerson.split(",");
					toPerson = "," + toPerson + ",";

					if ("1".equalsIgnoreCase(type) || "".equalsIgnoreCase(type)) {
						if (StringUtils.isEmpty(certSubset) || StringUtils.isEmpty(certEndDateItemId)
								|| StringUtils.isEmpty(certStatus)) {
							continue;
						}

						this.certSubsetEmail(nbase, strPerson);

					} 
					
					if ("2".equalsIgnoreCase(type) || "".equalsIgnoreCase(type)) {
						if (StringUtils.isEmpty(certBorrowSubset)) {
							continue;
						}
						
						this.certBorrowSubsetEmail(nbase, strPerson);
					}
				}
			}

			if ((emailList != null) && (emailList.size() > 0)) {
				AsyncEmailBo AsyncEmailBo = new AsyncEmailBo(conn, userView);
				AsyncEmailBo.send(emailList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(conn);
		} 
	}

	private void parseJobParam(Connection conn, String jobId, UserView userView) {
		String params = "";
		ContentDAO dao = new ContentDAO(conn);
		ResultSet rs = null;

		try {
			rs = dao.search("select job_param from t_sys_jobs where job_id=" + jobId);

			if (rs.next()) {
				params = rs.getString("job_param");
			}
			
			if ((null == params) || "".equals(params)) {
				certMap = new HashMap();
				toPerson = "manager,employee";
				warnDate ="3";
				certMap.put("warnDate", warnDate);
				certMap.put("toPerson", toPerson);
				certIdlist.add(certMap);
				return;
			}
			JSONArray jsonArray = JSONArray.fromObject(params);
			ArrayList paramsList = (ArrayList) JSONArray.toCollection(jsonArray);

			for (int i = 0; i < paramsList.size(); i++) {
				certMap = new HashMap();
				JSONObject  paramsMap = JSONObject.fromObject(paramsList.get(i));

				validity = (paramsMap.get("validity") == null) ? "" : paramsMap.get("validity").toString();
				recede = (paramsMap.get("return") == null) ? "" : paramsMap.get("return").toString();
				warnDate =(paramsMap.get("warn_date") == null) ? "3" : (String) paramsMap.get("warn_date");
				certId = (paramsMap.get("cert_id") == null) ? "" : paramsMap.get("cert_id").toString();
				toPerson = (paramsMap.get("to") == null) ? "" : paramsMap.get("to").toString();

				if (!paramsMap.containsKey("to") || StringUtils.isEmpty(toPerson)) {
					toPerson = "manager,employee";
				} else {
					toPerson = (String) paramsMap.get("to");
				}

				certMap.put("warnDate", warnDate);
				certMap.put("toPerson", toPerson);

				if (paramsMap.containsKey("validity")) {
					certMap.put("type", "1");

					if ("false".equalsIgnoreCase(validity)) {
						continue;
					}
				}else if (paramsMap.containsKey("return")) {
					certMap.put("type", "2");

					if ("false".equalsIgnoreCase(recede)) {
						continue;
					}
				}else{
					certMap.put("type", "");
				}

				if (!paramsMap.containsKey("cert_id") || StringUtils.isEmpty(certId)) {
					certIdlist.add(certMap);
					continue;
				} else {
					certMap.put("certId", certId);
					certIdlist.add(certMap);

					if (paramsMap.containsKey("validity")) {
						listValidity.add(certId);
					}

					if (paramsMap.containsKey("return")) {
						listReturn.add(certId);
					}

					continue;
				}
			}

			
		} catch (Exception e) {
			cat.error("job_param字段是t_sys_jobs表中新增字段，字段类型为text；如果没有设置此参数，可忽略此错误！");
		}
	}

	private void getManager(Connection conn, String content, UserView userView,RecordVo vo) {
		ContentDAO dao = new ContentDAO(conn);
		ResultSet rs = null;
		ResultSet rsName = null;
		ResultSet rsOPERUSER = null;
		EmailTemplateBo bo = new EmailTemplateBo(conn);
		String email = ConstantParamter.getEmailField().toLowerCase();
		String username = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
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
		
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append("select id , status from t_sys_function_priv  where FUNCTIONPRIV  like '%,4003,%' ");
			rs = dao.search(sql.toString());
			SmsBo sbo = new SmsBo(conn);
			
			while (rs.next()) {
				String id = rs.getString("id");
				int status = rs.getInt("status");
				String nbase = id.substring(0, 3);
				String a0100 = id.substring(3);
				ArrayList params = new ArrayList();
				if (status == 4) {
					if (id.length() > 8) {
						String newContent = getTextByHtml(content);
						sbo.sendMessage(userView, nbase + a0100, newContent);
						DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
						WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");
					}
				} else if (status == 0) {
					sql = new StringBuffer("");
					params.add(id);
					sql.append("select A0100,NBASE from OPERUSER where username =  ?");
					rsName = dao.search(sql.toString(),params);
					if (rsName.next()) {
						if (StringUtils.isNotEmpty(rsName.getString("A0100"))
								&& StringUtils.isNotEmpty(rsName.getString("NBASE"))) {
							a0100 = rsName.getString("A0100");
							nbase = rsName.getString("NBASE");
							String newContent = getTextByHtml(content);
							sbo.sendMessage(userView, nbase + a0100, newContent);
							DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
							WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");
						}
					}
				} else if (status == 1) {
					sql = new StringBuffer("");
					params.add(id);
					sql.append("select status , staff_id from t_sys_staff_in_role where ROLE_ID =  ?");
					rsName = dao.search(sql.toString());
					status = rsName.getInt("status");
					String staffId = rsName.getString("staff_id");
					while (rsName.next()) {
						if (status == 0) {// 业务用户
							sql = new StringBuffer("");
							params.clear();
							params.add(staffId);
							sql.append("select A0100,NBASE from OPERUSER where username =  ?");
							rsOPERUSER = dao.search(sql.toString(), params);
							if (rsOPERUSER.next()) {
								if (StringUtils.isNotEmpty(rs.getString("A0100"))
										&& StringUtils.isNotEmpty(rs.getString("NBASE"))) {
									a0100 = rs.getString("A0100");
									nbase = rs.getString("NBASE");
									String newContent = getTextByHtml(content);
									sbo.sendMessage(userView, nbase + a0100, newContent);
									DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
									WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");
								}
							}

						} else if (status == 1) {
							nbase = staffId.substring(0, 3);
							a0100 = staffId.substring(3);
							if (id.length() > 8) {
								String newContent = getTextByHtml(content);
								sbo.sendMessage(userView, nbase + a0100, newContent);
								DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
								WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");
							}

						} else if (status == 2) {
							sql = new StringBuffer("");
							for (int j = 0; j < certNbase.size(); j++) {
								nbase = (String) certNbase.get(j);
								params.clear();
								params.add(nbase + "A01");
								params.add(staffId + "%");
								params.add(staffId + "%");
								params.add(staffId + "%");
								sql.append("select A0100 from ? where  B0110  like  ? OR  E0122  like  ? OR E01A1 like ? ");
								rsOPERUSER = dao.search(sql.toString(), params);

								if (rsOPERUSER.next()) {
									a0100 = rsOPERUSER.getString("A0100");
									String newContent = getTextByHtml(content);
									vo = new RecordVo(nbase + "A01");
									vo.setString("a0100", a0100);
									if (dao.isExistRecordVo(vo)) {
										if (vo != null) {
											// 邮件地址
											String email_address = vo.getString(email);
											if (!bo.isMail(email_address))
												continue;

											/** 发送邮件 实现附件发送 */
											EMailBo emailBo = new EMailBo(conn, true, nbase);
											emailBo.sendEmail("", content, "", email_address);
										}
									}
									sbo.sendMessage(userView, nbase + a0100, newContent);
									DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
									WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");

								}
							}
						}

					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rsName);
			PubFunc.closeResource(rsOPERUSER);
		}
	}

	private void certSubsetEmail(String nbase, String[] strPerson) {
		ResultSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buf = new StringBuffer(); // 邮件内容
			EmailTemplateBo bo = new EmailTemplateBo(conn);
			String email = ConstantParamter.getEmailField().toLowerCase();
			String username = "";
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
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
			
			if (paramsMap.containsKey("certId")) {
				certId = (String) paramsMap.get("certId");
				warnDate = (String) paramsMap.get("warnDate");
				String nbaseCert = nbase + certSubset;
				StringBuffer sql = new StringBuffer("");
				paramList.clear();
				paramList.add(certId);
				sql.append("select A0100,"+certName+","+certEndDateItemId+" from "+nbaseCert+" where "+certStatus+" = 01 and "+certCategoryItemId+" = ? ");
				sql.append(" AND "+Sql_switcher.dateToChar(Sql_switcher.today())+" > "+Sql_switcher.dateToChar(certEndDateItemId+"-"+warnDate, "yyyy-mm-dd"));
				sql.append(" AND "+Sql_switcher.dateToChar(Sql_switcher.today())+" <= "+Sql_switcher.dateToChar(certEndDateItemId, "yyyy-mm-dd"));
				//	sql.append(" to_char(certEndDateItemId,'yyyy-mm-dd')>to_char(sysdate - ?,'yyyy-mm-dd') ");
				rs = dao.search(sql.toString(), paramList);

				while (rs.next()) {
					String a0101 = "";
					String a0100 = rs.getString("A0100");
					String name = rs.getString(certName);
					
					java.sql.Date date =rs.getDate(certEndDateItemId) ;
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		    		String EndDate = formatter.format(date);
					RecordVo vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);
					if (dao.isExistRecordVo(vo)) {
						if (vo != null) {
							LazyDynaBean bean = new LazyDynaBean();
							a0101 = vo.getString("a0101");
						}
					}
					
					buf.setLength(0);
					buf.append(a0101 + ":<br><br>&nbsp;&nbsp;&nbsp;&nbsp;您好,您的证书（" + name + "证书）将于" + EndDate
							+ "到期，请及时关注处理。<br><br>");

					String content = buf.toString();

					if (dao.isExistRecordVo(vo)) {
						if (vo != null) {
							LazyDynaBean bean = new LazyDynaBean();
							String email_address = vo.getString(email);

							if (!bo.isMail(email_address)) {
								continue;
							}

							bean.set("subject", "证书有效期预警");
							bean.set("bodyText", content);
							if (toPerson.indexOf(",employee,") != -1) {
								bean.set("toAddr", email_address);
								emailList.add(bean);
							}

						}
					}

					for (int a = 0; a < strPerson.length; a++) {
						if ("manager".equalsIgnoreCase(strPerson[a]) || "employee".equalsIgnoreCase(strPerson[a]))
							continue;

						if (bo.isMail(strPerson[a])) {
							EMailBo emailBo = new EMailBo(conn, true, nbase);
							emailBo.sendEmail("证书有效期预警", content, "", strPerson[a]);
						}
					}

					if (toPerson.indexOf(",manager,") != -1) {
						this.getManager(conn, content, userView, vo);
					}

					if (toPerson.indexOf(",employee,") != -1) { 
						SmsBo sbo = new SmsBo(conn);
						String newContent = getTextByHtml(content);
						sbo.sendMessage(userView, nbase + a0100, newContent);
						WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");
						DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
					}
				}
			} else {
				String nbaseCert = nbase + certSubset;
				StringBuffer sql = new StringBuffer("");
				sql.append("select  A0100 ,  "+certName+" , "+certEndDateItemId+" from  "+nbaseCert+" where  "+certStatus+" = 01 AND ");
				sql.append(" "+Sql_switcher.dateToChar(Sql_switcher.today())+" > "+Sql_switcher.dateToChar(certEndDateItemId+"-"+warnDate, "yyyy-mm-dd")+"  AND " );
			    sql.append(" "+Sql_switcher.dateToChar(Sql_switcher.today())+" <= "+Sql_switcher.dateToChar(certEndDateItemId, "yyyy-mm-dd"));
				rs = dao.search(sql.toString());

				while (rs.next()) {
					String a0100 = rs.getString("A0100");
					String a0101 = "";
					String name = rs.getString(certName);
					java.sql.Date date =rs.getDate(certEndDateItemId) ;
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		    		String EndDate = formatter.format(date);
					RecordVo vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);
					if (dao.isExistRecordVo(vo)) {
						if (vo != null) 
							a0101 = vo.getString("a0101");
					}

					buf.setLength(0);
					buf.append(a0101 + ":<br><br>&nbsp;&nbsp;&nbsp;&nbsp;您好，您的证书（" + name + "证书）将于" + EndDate
							+ "到期，请及时关注处理。<br><br>");

					String content = buf.toString();
				    vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);

					if (dao.isExistRecordVo(vo)) {
						if (vo != null) {
							LazyDynaBean bean = new LazyDynaBean();
							String email_address = vo.getString(email);

							if (!bo.isMail(email_address)) {
								continue;
							}

							bean.set("subject", "证书有效期预警");
							bean.set("bodyText", content);
							bean.set("toAddr", email_address);
							emailList.add(bean);
						}
					}

					for (int a = 0; a < strPerson.length; a++) {
						if ("manager".equalsIgnoreCase(strPerson[a]) || "employee".equalsIgnoreCase(strPerson[a]))
							continue;

						if (bo.isMail(strPerson[a])) {
							EMailBo emailBo = new EMailBo(conn, true, nbase);
							emailBo.sendEmail("证书有效期预警", content, "", strPerson[a]);
						}
					}

					if (toPerson.indexOf(",manager,") != -1) {
						this.getManager(conn, content, userView, vo);
					}

					if (toPerson.indexOf(",employee,") != -1) {
						SmsBo sbo = new SmsBo(conn);
						String newContent = getTextByHtml(content);
						sbo.sendMessage(userView, nbase + a0100, newContent);
						WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");
						DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	private void certBorrowSubsetEmail(String nbase, String[] strPerson) {
		ResultSet rs = null;
		String username = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
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
		try {
			ContentDAO dao = new ContentDAO(conn);
			UserView userView = new UserView("su", conn);
			userView.canLogin(false);
			StringBuffer buf = new StringBuffer(); // 邮件内容
			EmailTemplateBo bo = new EmailTemplateBo(conn);
			String email = ConstantParamter.getEmailField().toLowerCase();
			String nbaseCert = nbase + certBorrowSubset;
			RecordVo vo = new RecordVo(nbase + "A01");

			if (paramsMap.containsKey("certId")) {
				certId = (String) paramsMap.get("certId");
				StringBuffer sql = new StringBuffer("");
				
				paramList.clear();
				paramList.add(certId);
				sql.append("select  A0100 , "+certBorrowSubset + "05 , "+certBorrowSubset + "11  from  "+nbaseCert+" where "+certBorrowSubset + "19 ='03' AND "+certBorrowSubset + "23 = '2' AND "+certBorrowSubset + "01 = ?  AND  ");
				sql.append(Sql_switcher.dateToChar(Sql_switcher.today())+" > "+Sql_switcher.dateToChar(certBorrowSubset + "11 -"+warnDate, "yyyy-mm-dd")+" AND ");
				sql.append(Sql_switcher.dateToChar(Sql_switcher.today())+" <= "+Sql_switcher.dateToChar(certBorrowSubset + "11", "yyyy-mm-dd"));
				rs = dao.search(sql.toString(), paramList);
				while (rs.next()) {
					String a0100 = rs.getString("A0100");
					String a0101 = "";
					String name = rs.getString(certBorrowSubset + "05");
					java.sql.Date date =rs.getDate(certBorrowSubset + "11") ;
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		    		String EndDate = formatter.format(date);
					
					vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);
					if (dao.isExistRecordVo(vo)) {
						if (vo != null) 
							a0101 = vo.getString("a0101");
					}

					buf.setLength(0);
					buf.append(a0101 + ":<br><br>&nbsp;&nbsp;&nbsp;&nbsp;您好，您借阅的证书（" + name + "证书）将于" + EndDate
							+ "到期，请及时安排时间归还。<br><br>");

					String content = buf.toString();
					String content2 = buf.toString();
					vo.setString("a0100", a0100);

					if (dao.isExistRecordVo(vo)) {
						if (vo != null) {
							LazyDynaBean bean = new LazyDynaBean();
							String email_address = vo.getString(email);

							if (!bo.isMail(email_address)) {
								continue;
							}

							bean.set("subject", "证书归还预警");
							bean.set("bodyText", content);
							bean.set("toAddr", email_address);
							emailList.add(bean);
						}
					}

					for (int a = 0; a < strPerson.length; a++) {
						if ("manager".equalsIgnoreCase(strPerson[a]) || "employee".equalsIgnoreCase(strPerson[a]))
							continue;

						if (bo.isMail(strPerson[a])) {
							EMailBo emailBo = new EMailBo(conn, true, nbase);
							emailBo.sendEmail("证书归还预警", content, "", strPerson[a]);
						}
					}

					if (toPerson.indexOf(",manager,") != -1) {
						this.getManager(conn, content, userView, vo);
					}

					if (toPerson.indexOf(",employee,") != -1) {
						SmsBo sbo = new SmsBo(conn);
						String newContent = getTextByHtml(content);
						sbo.sendMessage(userView, nbase + a0100, newContent);
						DTalkBo.sendMessage(vo.getString(username), "证书归还预警", newContent, "", "");
						String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
						if(corpid!=null&&corpid.length()>0){
							WeiXinBo.sendMsgToPerson(nbase, a0100, "证书归还预警", content, "", "");
						}
					}

				}

				paramList.clear();
				paramList.add(certId);
				
			    sql = new StringBuffer("");
				sql.append("select "+Sql_switcher.diffDays(Sql_switcher.sqlNow(),certBorrowSubset + "11")+" as DAY,  A0100 , "+certBorrowSubset + "05 , "+certBorrowSubset + "11  from  "+nbaseCert+" where "+certBorrowSubset + "19 ='03' AND "+certBorrowSubset + "23 = '2' AND "+certBorrowSubset + "01 = ?  AND  ");
				sql.append(Sql_switcher.dateToChar(Sql_switcher.today())+" > "+Sql_switcher.dateToChar(certBorrowSubset + "11", "yyyy-mm-dd"));
				rs = dao.search(sql.toString(), paramList);

				while (rs.next()) {
					String a0100 = rs.getString("A0100");
					String a0101 = "";
					String differDay = rs.getString("day");
					differDay = differDay.split("\\.")[0];
					vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);
					if (dao.isExistRecordVo(vo)) {
						if (vo != null) {
							LazyDynaBean bean = new LazyDynaBean();
							a0101 = vo.getString("a0101");
						}
					}
					
					String name = rs.getString(certBorrowSubset + "05");
					java.sql.Date date =rs.getDate(certBorrowSubset + "11") ;
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		    		String endDate = formatter.format(date);
  
					buf.setLength(0);
					buf.append(a0101 + ":<br><br>&nbsp;&nbsp;&nbsp;&nbsp;您好，您借阅的证书（" + name + "证书）应于" + endDate
							+ "归还，当前已逾期" +differDay+ "天，请及时归还。<br><br>");

					String content = buf.toString();
					vo.setString("a0100", a0100);

					if (dao.isExistRecordVo(vo)) {
						if (vo != null) {
							LazyDynaBean bean = new LazyDynaBean();
							String email_address = vo.getString(email);

							if (!bo.isMail(email_address)) {
								continue;
							}

							bean.set("subject", "证书归还预警");
							bean.set("bodyText", content);
							bean.set("toAddr", email_address);
							emailList.add(bean);
						}
					}

					if (toPerson.indexOf(",manager,") != -1) {
						this.getManager(conn, content, userView, vo);
					}

					if (toPerson.indexOf(",employee,") != -1) {
						SmsBo sbo = new SmsBo(conn);
						String newContent = getTextByHtml(content);
						sbo.sendMessage(userView, nbase + a0100, newContent);
						DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
						WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");
					}

				}
			} else {
				StringBuffer sql = new StringBuffer("");
				paramList.clear();
				sql.append("select A0100,"+certBorrowSubset+"05,"+certBorrowSubset+"11   from  "+nbaseCert+" where "+certBorrowSubset+"19 ='03' AND "+certBorrowSubset+"23 = '2'   AND ");
				sql.append(Sql_switcher.dateToChar(Sql_switcher.today())+" > "+Sql_switcher.dateToChar(certBorrowSubset + "11 -"+warnDate, "yyyy-mm-dd")+" AND ");
			    sql.append(Sql_switcher.dateToChar(Sql_switcher.today())+" <= "+Sql_switcher.dateToChar(certBorrowSubset + "11", "yyyy-mm-dd"));
				rs = dao.search(sql.toString());

				while (rs.next()) {
					String a0100 = rs.getString("A0100");
					String a0101 = "";
					String name = rs.getString(certBorrowSubset + "05");
					java.sql.Date date =rs.getDate(certBorrowSubset + "11") ;
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		    		String EndDate = formatter.format(date);
				    vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);
					if (dao.isExistRecordVo(vo)) {
						if (vo != null) 
							a0101 = vo.getString("a0101");
					}

					buf.setLength(0);
					buf.append(a0101 + ":<br><br>&nbsp;&nbsp;&nbsp;&nbsp;您好，您借阅的证书（" + name + "证书）将于" + EndDate
							+ "到期，请及时安排时间归还。<br><br>");

					String content = buf.toString();

					vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);

					if (dao.isExistRecordVo(vo)) {
						if (vo != null) {
							LazyDynaBean bean = new LazyDynaBean();
							String email_address = vo.getString(email);

							if (!bo.isMail(email_address)) {
								continue;
							}

							bean.set("subject", "证书归还预警");
							bean.set("bodyText", content);
							bean.set("toAddr", email_address);
							emailList.add(bean);
						}
					}

					for (int a = 0; a < strPerson.length; a++) {
						if ("manager".equalsIgnoreCase(strPerson[a]) || "employee".equalsIgnoreCase(strPerson[a]))
							continue;

						if (bo.isMail(strPerson[a])) {
							EMailBo emailBo = new EMailBo(conn, true, nbase);
							emailBo.sendEmail("证书归还预警", content, "", strPerson[a]);
						}
					}

					if (toPerson.indexOf(",manager,") != -1) {
						this.getManager(conn, content, userView, vo);
					}

					if (toPerson.indexOf(",employee,") != -1) {
						SmsBo sbo = new SmsBo(conn);
						String newContent = getTextByHtml(content);
						sbo.sendMessage(userView, nbase + a0100, newContent);
						DTalkBo.sendMessage(vo.getString(username), "证书归还预警", newContent, "", "");
						WeiXinBo.sendMsgToPerson(nbase, a0100, "证书归还预警", newContent, "", "");
					}
				}

				sql = new StringBuffer("");
				paramList.clear();
			    sql = new StringBuffer("");
				sql.append("select "+Sql_switcher.diffDays(Sql_switcher.sqlNow(),certBorrowSubset + "11")+" as DAY ,  A0100 , "+certBorrowSubset + "05 , "+certBorrowSubset + "11  from  "+nbaseCert+" where "+certBorrowSubset + "19 ='03' AND "+certBorrowSubset + "23 = '2' AND ");
				sql.append(Sql_switcher.dateToChar(Sql_switcher.today())+" > "+Sql_switcher.dateToChar(certBorrowSubset + "11", "yyyy-mm-dd"));
				rs = dao.search(sql.toString());
				

				while (rs.next()) {
					String a0100 = rs.getString("A0100");
					String a0101 = "";
					String differDay = rs.getString("day");
					differDay = differDay.split("\\.")[0];
					String name = rs.getString(certBorrowSubset + "05");
					java.sql.Date date =rs.getDate(certBorrowSubset + "11") ;
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		    		String endDate = formatter.format(date);
					vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);
					if (dao.isExistRecordVo(vo)) {
						if (vo != null) 
							a0101 = vo.getString("a0101");
					}

					buf.setLength(0);
					buf.append(a0101 + ":<br><br>&nbsp;&nbsp;&nbsp;&nbsp;您好，您借阅的证书（" + name + "证书）应于" + endDate
							+ "归还，当前已逾期" + differDay + "天，请及时归还。<br><br>");

					String content = buf.toString();

					vo = new RecordVo(nbase + "A01");
					vo.setString("a0100", a0100);

					if (dao.isExistRecordVo(vo)) {
						if (vo != null) {
							LazyDynaBean bean = new LazyDynaBean();
							String email_address = vo.getString(email);

							if (!bo.isMail(email_address)) {
								continue;
							}

							bean.set("subject", "证书归还预警");
							bean.set("bodyText", content);
							bean.set("toAddr", email_address);
							emailList.add(bean);
						}
					}

					for (int a = 0; a < strPerson.length; a++) {
						if ("manager".equalsIgnoreCase(strPerson[a]) || "employee".equalsIgnoreCase(strPerson[a]))
							continue;

						if (bo.isMail(strPerson[a])) {
							EMailBo emailBo = new EMailBo(conn, true, nbase);
							emailBo.sendEmail("证书归还预警", content, "", strPerson[a]);
						}
					}

					if (toPerson.indexOf(",manager,") != -1) {
						this.getManager(conn, content, userView, vo);
					}

					if (toPerson.indexOf(",employee,") != -1) {
						SmsBo sbo = new SmsBo(conn);
						String newContent = getTextByHtml(content);
						sbo.sendMessage(userView, nbase + a0100, newContent);
						DTalkBo.sendMessage(vo.getString(username), "证书有效期预警", newContent, "", "");
						WeiXinBo.sendMsgToPerson(nbase, a0100, "证书有效期预警", newContent, "", "");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	
	 /**
     * 去除html标签
     * @param html
     * @return
     * @throws GeneralException
     */
    public String getTextByHtml(String html) throws GeneralException{
        if(StringUtils.isEmpty(html))
            return "";
        
        Parser parser;
        try {
            //不加div标签会报错
            html = "<div>"+html+"</div>";
            parser = new Parser(html);
            TextExtractingVisitor visitor = new  TextExtractingVisitor();
            parser.visitAllNodesWith(visitor);
            return visitor.getExtractedText(); 
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }

}
