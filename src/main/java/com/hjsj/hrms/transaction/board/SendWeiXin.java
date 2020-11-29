package com.hjsj.hrms.transaction.board;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title: 发送通知 </p>
 * <p>Description:将培训通知发送到微信 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-4-21 下午2:23:52</p>
 * @author jingq
 * @version 1.0
 */
public class SendWeiXin extends IBusiness{

	public void execute() throws GeneralException {
		String msg = this.getFormHM().get("msg").toString();// 短信/邮件/微信 模板

		String sperson = this.getFormHM().get("selectPerson").toString();// 选中的人或角色
		String sp = this.getFormHM().get("sperson").toString();// 01 人员 02角色
		RecordVo rv = (RecordVo) this.getFormHM().get("boardov");
		String content = rv.getString("content");// 内容
		String topic = rv.getString("topic");// 标题
		String trainId = this.getFormHM().get("trainid").toString();// 当前培训班ID

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
			//content = delHTMLTag(content);
		}
		String picUrl = "http://www.hjsoft.com.cn:8089/UserFiles/Image/announce.png";
		this.sendWeiXin(sperson, msg, content, topic, trainId, sp, picUrl, "");
		this.getFormHM().put("chflag", "a");
	}
	
	private void sendWeiXin(String sperson, String msg, String contents,
			String topic, String trainid, String sp, String picurl, String url) {
		ResultSet rs = null;
		try {
		    //判断是否是给钉钉发送消息
		    boolean dingTalk = ((HashMap)this.getFormHM().get("requestPamaHM")).get("b_sendDingTalk") != null; 
		    this.getFormHM().remove("b_sendDingTalk");
			String username = getUserName();
			RecordVo vo = null;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			if (msg != null && msg.length() > 0) {// 如果模板不为空
				ArrayList fieldList = bo.getTemplateFieldInfo(Integer.parseInt(msg), 2);
				String tmpNbase = "";
				String tmpA0100 = "";
				if (!"".equals(sperson) && sperson != null) { // 如果人员/角色 不为空
					String[] s = sperson.split(",");
					for (int j = 0; j < s.length; j++) {
						if ("01".equals(sp)) {//人员
							String spersons = s[j] + "";
							tmpNbase = spersons.substring(2, 5);
							tmpA0100 = spersons.substring(5, spersons.length());
							vo = new RecordVo(tmpNbase + "A01");
							vo.setString("a0100", tmpA0100);
							if (dao.isExistRecordVo(vo)) {
								if (vo != null) {
									String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(msg)), tmpNbase+ tmpA0100, fieldList, userView);
									content = content.replaceAll("\r\n","<br/>");
									content = content.replace("\r", "<br/>");
									content = content.replace("\n", "<br/>");
									if(!dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid")))
									    WeiXinBo.sendMsgToPerson(vo.getString(username),topic, content, picurl, url);
									
									//推送至钉钉 chenxg 2017-06-01
                                    if(dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid"))){
                                        DTalkBo.sendMessage(vo.getString(username),topic, contents, "", "");
                                    }
								}
							}
						} else if ("02".equals(sp)) {//角色
							String spersons = s[j] + "";
							spersons = spersons.substring(2, spersons.length());
							ArrayList persons = this.FindPersonId(spersons);
							for (int i = 0; i < persons.size(); i++) {
								String person = persons.get(i).toString();
								tmpNbase = person.substring(0, 3);
								tmpA0100 = person.substring(3, person.length());
								vo = new RecordVo(tmpNbase + "A01");
								vo.setString("a0100", tmpA0100);
								if (dao.isExistRecordVo(vo)) {
									if (vo != null) {
										String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(msg)),tmpNbase + tmpA0100, fieldList,userView);
										content = content.replaceAll("\r\n","<br/>");
										content = content.replace("\r", "<br/>");
										content = content.replace("\n", "<br/>");
										if(!dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid")))
										    WeiXinBo.sendMsgToPerson(vo.getString(username), topic,content, picurl, url);
										
										//推送至钉钉 chenxg 2017-06-01
                                        if(dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid"))){
                                            DTalkBo.sendMessage(vo.getString(username),topic, contents, "", "");
                                        }
									}
								}
							}
						}

					}
				} else {
					HashMap hs = this.FindStudent(trainid);
					for (Iterator iter = hs.entrySet().iterator(); iter.hasNext();) {
						Map.Entry entry = (Map.Entry) iter.next(); 
						Object key = entry.getKey();
						Object value = entry.getValue();
						tmpA0100 = key.toString();
						tmpNbase = value.toString();
						vo = new RecordVo(tmpNbase + "A01");
						vo.setString("a0100", tmpA0100);
						if (dao.isExistRecordVo(vo)) {
							if (vo != null) {
								String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(msg)),tmpNbase + tmpA0100, fieldList,userView);
								content = content.replaceAll("\r\n", "<br/>");
								content = content.replace("\r", "<br/>");
								content = content.replace("\n", "<br/>");
								if(!dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid")))
								    WeiXinBo.sendMsgToPerson(vo.getString(username),topic, content, picurl, url);
								
								//推送至钉钉 chenxg 2017-06-01
                                if(dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid"))){
                                    DTalkBo.sendMessage(vo.getString(username),topic, contents, "", "");
                                }
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
						if ("01".equals(sp)) { // 如果选择的是人员
							String spersons = s[j] + "";
							tmpNbase = spersons.substring(2, 5);
							tmpA0100 = spersons.substring(5, spersons.length());
							vo = new RecordVo(tmpNbase + "A01");
							vo.setString("a0100", tmpA0100);
							if (dao.isExistRecordVo(vo)) {
								if (vo != null) {
								    if(!dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid")))
								        WeiXinBo.sendMsgToPerson(vo.getString(username),topic, contents, picurl, url);
								    
								    //推送至钉钉 chenxg 2017-06-01
                                    if(dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid"))){
                                        DTalkBo.sendMessage(vo.getString(username),topic, contents, "", "");
                                    }
								}
							}
						} else if ("02".equals(sp)) {// 如果选择的是角色
							String spersons = s[j] + "";
							spersons = spersons.substring(2, spersons.length());
							ArrayList persons = this.FindPersonId(spersons);
							for (int i = 0; i < persons.size(); i++) {
								String person = persons.get(i).toString();
								tmpNbase = person.substring(0, 3);
								tmpA0100 = person.substring(3, person.length());
								vo = new RecordVo(tmpNbase + "A01");
								vo.setString("a0100", tmpA0100);
								if (dao.isExistRecordVo(vo)) {
									if (vo != null) {
									    if(!dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid")))
									        WeiXinBo.sendMsgToPerson(vo.getString(username), topic,contents, picurl, url);
									    
									    //推送至钉钉 chenxg 2017-06-01
		                                if(dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid"))){
		                                    DTalkBo.sendMessage(vo.getString(username),topic, contents, "", "");
		                                }
									}
								}
							}
						}

					}
				} else {
					HashMap hs = this.FindStudent(trainid);
					for (Iterator iter = hs.entrySet().iterator(); iter.hasNext();) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Object value = entry.getValue();
						tmpA0100 = key.toString();
						tmpNbase = value.toString();
						vo = new RecordVo(tmpNbase + "A01");
						vo.setString("a0100", tmpA0100);
						if (dao.isExistRecordVo(vo)) {
							if (vo != null) {
							    if(!dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid")))
							        WeiXinBo.sendMsgToPerson(vo.getString(username),topic, contents, picurl, url);
							    
							    //推送至钉钉 chenxg 2017-06-01
	                            if(dingTalk && StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid"))) {
	                                DTalkBo.sendMessage(vo.getString(username),topic, contents, "", "");
	                            }
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
					e.printStackTrace();
				}
			}
		}
	}
	
	// 查找当前培训班已批学员
	private HashMap FindStudent(String trainid) {
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

	// 查找当前角色所在的所有人员
	private ArrayList FindPersonId(String roleId) {
		ArrayList personId = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select staff_id from t_sys_staff_in_role where role_id = '" + roleId + "' and status = 1";
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				personId.add(rs.getString("staff_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return personId;
	}

	/**
	 * 获取用户登录指标
	 * 
	 * @return
	 */
	public String getUserName() {
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
		return username;
	}
	
	
}
