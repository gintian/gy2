package com.hjsj.hrms.transaction.board;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SendEmail extends IBusiness {

	public void execute() throws GeneralException {
		String msg = this.getFormHM().get("msg").toString();//短信/邮件 模板

		String sperson = this.getFormHM().get("selectPerson").toString();//选中的人或角色
		String sp = this.getFormHM().get("sperson").toString();//01 人员 02角色
		RecordVo rv = (RecordVo) this.getFormHM().get("boardov");
		String content = rv.getString("content");//内容
		String topic = rv.getString("topic");//标题
		String trainId = this.getFormHM().get("trainid").toString();//当前培训班ID

		if (content != null) {
			content = content.replaceAll("&sup1;", "1");
			content = content.replaceAll("&sup2;", "2");
			content = content.replaceAll("&sup3;", "3");
			content = content.replaceAll("&ordm;", "o");
			content = content.replaceAll("&acirc;", "a");
			content = content.replaceAll("&eth;", "d");
			content = content.replaceAll("&yacute;", "y");
			content = content.replaceAll("&thorn;", "t");
			content = content.replaceAll("&ETH;", "D");
			content = content.replaceAll("&THORN;", "T");
			content = content.replaceAll("&Yacute;", "Y");
		}
		this.sendEMail(sperson, msg, content, topic, trainId,sp);
		this.getFormHM().put("chflag", "a");
	}

	public void sendEMail(String sperson, String msg, String contents, String topic, String trainid,String sp) {
		ResultSet rs = null;
		try {
			String email = ConstantParamter.getEmailField().toLowerCase();

			RecordVo vo = null;
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			StringBuffer buf = new StringBuffer();// 邮件内容
			StringBuffer title = new StringBuffer();// 邮件标题

			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			String templateId = "";
			templateId = msg;
			if (templateId != null && templateId.length() > 0) {//如果模板不为空
				HashMap hashmap = bo.getSubject(templateId);
				ArrayList fieldList = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);
				String tmpNbase = "";
				String tmpA0100 = "";
				if (!"".equals(sperson) && sperson != null) { //如果人员/角色 不为空
					String[] s = sperson.split(",");
					for (int j = 0; j < s.length; j++) {
						if("01".equals(sp)){
							String spersons = s[j] + "";
							tmpNbase = spersons.substring(2, 5);
							tmpA0100 = spersons.substring(5, spersons.length());
							vo = new RecordVo(tmpNbase + "A01");
							vo.setString("a0100", tmpA0100);
							if (dao.isExistRecordVo(vo)) {
								if (vo != null) {
									buf.setLength(0);
									title.setLength(0);
									String email_address = vo.getString(email);

									String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), tmpNbase + tmpA0100, fieldList, userView);
									content = content.replaceAll("\r\n", "<br/>");
									content = content.replace("\r", "<br/>");
									content = content.replace("\n", "<br/>");
									EMailBo mailbo = new EMailBo(this.getFrameconn(), true, tmpNbase);
									String attach = "";
									if(null != hashmap.get("attach") && !"".equals(hashmap.get("attach").toString().trim())){
										attach = hashmap.get("attach").toString().trim();										
									}
									mailbo.sendEmail((String) hashmap.get("subject"), content, attach, email_address);
								}
							}
						}else if("02".equals(sp)){
							String spersons = s[j]+"";
							spersons = spersons.substring(2,spersons.length());
							ArrayList persons = this.FindPersonId(spersons);
							for(int i = 0 ; i < persons.size(); i++){
								String person = persons.get(i).toString();
								tmpNbase = person.substring(0,3);
								tmpA0100 = person.substring(3,person.length());
								vo = new RecordVo(tmpNbase + "A01");
								vo.setString("a0100", tmpA0100);
								if (dao.isExistRecordVo(vo)) {
									if (vo != null) {
										buf.setLength(0);
										title.setLength(0);
										String email_address = vo.getString(email);

										String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), tmpNbase + tmpA0100, fieldList, userView);
										content = content.replaceAll("\r\n", "<br/>");
										content = content.replace("\r", "<br/>");
										content = content.replace("\n", "<br/>");
										EMailBo mailbo = new EMailBo(this.getFrameconn(), true, tmpNbase);
										String attach = "";
										if(null != hashmap.get("attach") && !"".equals(hashmap.get("attach").toString().trim())){
											attach = hashmap.get("attach").toString().trim();										
										}
										mailbo.sendEmail((String) hashmap.get("subject"), content, attach, email_address);
									}
								}
							}
						}
						
					}
				} else {
					HashMap hs = this.FindStudent(trainid);
					for (Iterator iter = hs.entrySet().iterator(); iter.hasNext();) {
						Map.Entry entry = (Map.Entry) iter.next(); // map.entry
																	// 同时取出键值
						Object key = entry.getKey();
						Object value = entry.getValue();
						tmpA0100 = key.toString();
						tmpNbase = value.toString();

						vo = new RecordVo(tmpNbase + "A01");
						vo.setString("a0100", tmpA0100);
						if (dao.isExistRecordVo(vo)) {
							if (vo != null) {
								buf.setLength(0);
								title.setLength(0);
								String email_address = vo.getString(email);

								String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), tmpNbase + tmpA0100, fieldList, userView);
								content = content.replaceAll("\r\n", "<br/>");
								content = content.replace("\r", "<br/>");
								content = content.replace("\n", "<br/>");
								EMailBo mailbo = new EMailBo(this.getFrameconn(), true, tmpNbase);

								String attach = "";
								if(null != hashmap.get("attach") && !"".equals(hashmap.get("attach").toString().trim())){
									attach = hashmap.get("attach").toString().trim();										
								}
								mailbo.sendEmail((String) hashmap.get("subject"), content, attach, email_address);
							}
						}
					}
				}
			} else {
				String tmpA0100 = "";
				String tmpNbase = "";
				if (!"".equals(sperson) && sperson != null) {
					String[] s = sperson.split(",");
					for (int j = 0; j < s.length; j++) {
						if("01".equals(sp)){ //如果选择的是人员
							String spersons = s[j] + "";
							tmpNbase = spersons.substring(2, 5);
							tmpA0100 = spersons.substring(5, spersons.length());
							vo = new RecordVo(tmpNbase + "A01");
							vo.setString("a0100", tmpA0100);
							if (dao.isExistRecordVo(vo)) {
								if (vo != null) {
									buf.setLength(0);
									title.setLength(0);
									String email_address = vo.getString(email);
									if(!bo.isMail(email_address))
									    continue;
									
									EMailBo mailbo = new EMailBo(this.getFrameconn(), true, tmpNbase);
									mailbo.sendEmail(topic, contents, "", email_address);
								}
							}
						}else if("02".equals(sp)){//如果选择的是角色
							String spersons = s[j]+"";
							spersons = spersons.substring(2,spersons.length());
							ArrayList persons = this.FindPersonId(spersons);
							for(int i = 0 ; i < persons.size(); i++){
								String person = persons.get(i).toString();
								tmpNbase = person.substring(0,3);
								tmpA0100 = person.substring(3,person.length());
								vo = new RecordVo(tmpNbase + "A01");
								vo.setString("a0100", tmpA0100);
								if (dao.isExistRecordVo(vo)) {
									if (vo != null) {
										buf.setLength(0);
										title.setLength(0);
										String email_address = vo.getString(email);
										EMailBo mailbo = new EMailBo(this.getFrameconn(), true, tmpNbase);
										mailbo.sendEmail(topic, contents, "", email_address);
									}
								}
							}
							}
						
					}
				} else {
					HashMap hs = this.FindStudent(trainid);
					for (Iterator iter = hs.entrySet().iterator(); iter.hasNext();) {
						Map.Entry entry = (Map.Entry) iter.next(); // map.entry
																	// 同时取出键值
						Object key = entry.getKey();
						Object value = entry.getValue();
						tmpA0100 = key.toString();
						tmpNbase = value.toString();
						vo = new RecordVo(tmpNbase + "A01");
						vo.setString("a0100", tmpA0100);
						if (dao.isExistRecordVo(vo)) {
							if (vo != null) {
								buf.setLength(0);
								title.setLength(0);
								String email_address = vo.getString(email);

								EMailBo mailbo = new EMailBo(this.getFrameconn(), true, tmpNbase);
								mailbo.sendEmail(topic, contents, "", email_address);
							}
						}
					}
				}

			}
		} catch (Exception e) {
			 e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	//查找当前培训班已批学员
	public HashMap FindStudent(String trainid) {
		HashMap hs = new HashMap();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select r4001,nbase from r40 where r4005 = '" + trainid + "' and r4013 = '03'";
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				hs.put(rs.getString("r4001"), rs.getString("nbase"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hs;
	}
	
	//查找当前角色所在的所有人员
	public ArrayList FindPersonId(String roleId){
		ArrayList personId = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select staff_id from t_sys_staff_in_role where role_id = '"+roleId+"' and status = 1";
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				personId.add(rs.getString("staff_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return personId;
	}
}
