package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.axis.encoding.Base64;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.sql.RowSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @ClassName: TransResumeBo
 * @Description: TODO解析简历服务
 * @author xmsh
 * @date 2013-12-27 下午02:12:44
 * 
 */
public class TransResumeBo {

	private String url;
	private static String nameSpace = "http://tempuri.org/";
	private String methodName;
	private String userName;
	private String password;

	private String blacklistLog = ""; // 黑名单log
	private String PlistLog = ""; // 人员库log
	private String ClistLog = ""; // 代码对应log
	private String FlistLog = ""; // 解析不正确的文件(文件解析错误,或标识指标,次关键指标未解析)
	private String fileName = ""; //解析的文件名
	/**
	 * 成功导入人员数
	 */
	private int importNum = 0;

	// 链接工厂类
	private SOAPConnectionFactory factory = null;
	// 创建一个链接
	private SOAPConnection conn = null;
	private MessageFactory reqMsgFactory = null;
	private SOAPMessage reqMsg = null;
	private Connection connection = null;

	/**
	 * 
	 * <p>
	 * Title:构造函数
	 * </p>
	 * <p>
	 * Description: 初始化简历解析服务
	 * </p>
	 * 
	 * @param url
	 * @param methodName
	 * @param userName
	 * @param password
	 */
	public TransResumeBo(String url, String methodName, String userName, String password, Connection conn) {
		this.url = url;
		this.methodName = methodName;
		this.userName = userName;
		this.password = password;
		this.connection = conn;
		try {
			this.init();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() throws GeneralException {
		try {
			factory = SOAPConnectionFactory.newInstance();
			conn = factory.createConnection();
			reqMsgFactory = MessageFactory.newInstance();
			reqMsg = reqMsgFactory.createMessage();

		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/**
	 * 
	 * @Title: getResumeList
	 * @Description: TODO返回解析简历节点集
	 * @param jl  简历内容的字符串
	 * @return NodeList
	 * @throws GeneralException 
	 * @throws
	 */
	public NodeList getResumeList(String jl,String fileName) throws GeneralException {
		this.fileName = fileName;

		NodeList list = null;
		try {

			StringBuffer strXml = new StringBuffer();
			jl = jl.replaceAll("\r", " ");
			jl = jl.replaceAll("\n", " ");
			jl = jl.replaceAll("&", " 与 ");
			strXml.append("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
			strXml.append("xmlns:tem='http://tempuri.org/'>");
			strXml.append("<soapenv:Header/>");
			strXml.append("<soapenv:Body>");
			strXml.append("<tem:TransResume>");
			strXml.append("<tem:username>" + this.userName + "</tem:username>");
			strXml.append("<tem:pwd>" + this.password + "</tem:pwd>");
			strXml.append("<tem:original><![CDATA[" + jl + "]]></tem:original>");
			strXml.append("</tem:TransResume>");
			strXml.append("</soapenv:Body>");
			strXml.append("</soapenv:Envelope>");
			String xmlString=strXml.toString();
			xmlString=xmlString.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
			//这些错误的发生是由于一些不可见的特殊字符的存在，而这些字符对于XMl文件来说又是非法的，
			//所以XML解析器在解析时会发生异常，官方定义了XML的无效字符分为三段0x00 - 0x08 0x0b - 0x0c 0x0e - 0x1f 防止xml编码出错
			Reader reader = new StringReader(xmlString);
			Source source = new StreamSource(reader);
			SOAPMessage reqMsg1 = reqMsgFactory.createMessage();
			SOAPPart soapPart = reqMsg1.getSOAPPart();
			soapPart.setContent(source);
			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.setPrefix("soapenv");
			envelope.setAttribute("xmlns:urn", "urn:DefaultNamespace");
			envelope.getHeader().setPrefix("soapenv");
			SOAPBody soapBody = envelope.getBody();
			soapBody.setPrefix("soapenv");
			URL cUrl = new URL(url);
			SOAPMessage respMsg = conn.call(reqMsg1, cUrl);
			list = respMsg.getSOAPBody().getChildNodes();

		} catch (Exception e) {
			 e.printStackTrace();
			 FlistLog +=fileName+";";
			//throw GeneralExceptionHandler.Handle(e);
		}
		return list;

	}

	
	
	/**
	 * 
	 * @Title: ProceedReturnInfo
	 * @Description: TODO解析简历节点集
	 * @param list
	 * @return 指标集下指标项对应的值
	 * @throws GeneralException
	 *             ArrayList
	 * @throws
	 */
	public ArrayList ProceedReturnInfoForFile(NodeList list) throws GeneralException {
		ArrayList ReturnInfoList = new ArrayList();
		
		try {
			if (list != null) {
			
				for (int i = 0; i < list.getLength(); i++) {
					Node node = (Node) list.item(i);
					if ("TransResumeByXmlStringForFileResponse".equals(node.getNodeName())) {

						Node resultnode = node.getChildNodes().item(0);
						if (resultnode!=null&&"TransResumeByXmlStringForFileResult".equals(resultnode.getNodeName())) {
						   Node textNode=resultnode.getChildNodes().item(0);
						   String xmlstr=textNode.getTextContent();
						   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
					        DocumentBuilder db = dbf.newDocumentBuilder();  
					        Document domTree = db.parse(new InputSource(new StringReader(xmlstr)));  
					        Element  root = domTree.getDocumentElement();  
							for (int j = 0; j < root.getChildNodes().getLength(); j++) {
								Node childNode = root.getChildNodes().item(j);
								 if (childNode.getNodeType() != Node.ELEMENT_NODE) {
								     continue;
								 }
								if ("EducationInfo".equals(childNode.getNodeName())) { // 教育信息
                                    int i9999I=0;
									for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
										HashMap hm = new HashMap();
										Node EduNode = childNode.getChildNodes().item(k);
										if (EduNode.getNodeType() != Node.ELEMENT_NODE) {
		                                     continue;
		                                 }
										i9999I++;
										for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
										    Node xNode = EduNode.getChildNodes().item(x);
										    if (xNode.getNodeType() != Node.ELEMENT_NODE) {
	                                             continue;
	                                         }
											int i9999 =i9999I;
											hm.put("setid", childNode.getNodeName());
											hm.put(xNode.getNodeName(), xNode.getTextContent());
											hm.put("i9999", String.valueOf(i9999));
											
										}
										ReturnInfoList.add(hm);
									}
								} else if ("ExperienceInfo".equals(childNode.getNodeName())) { // 工作经历
								    int i9999I=0;
									for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
										HashMap hm = new HashMap();
										Node EduNode = childNode.getChildNodes().item(k);
										if (EduNode.getNodeType() != Node.ELEMENT_NODE) {
                                            continue;
                                        }
                                        i9999I++;
										for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
										    Node xNode = EduNode.getChildNodes().item(x);
										    if (xNode.getNodeType() != Node.ELEMENT_NODE) {
                                                continue;
                                            }
											int i9999 =i9999I;
											hm.put("setid", childNode.getNodeName());
											
											hm.put(xNode.getNodeName(), xNode.getTextContent());
											hm.put("i9999", String.valueOf(i9999));
											
										}
										ReturnInfoList.add(hm);
									}
								} else if ("TrainingInfo".equals(childNode.getNodeName())) { // 培训经历
								    int i9999I=0;
									for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
										HashMap hm = new HashMap();
										Node EduNode = childNode.getChildNodes().item(k);
										if (EduNode.getNodeType() != Node.ELEMENT_NODE) {
                                            continue;
                                        }
                                        i9999I++;
										for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
										    Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) {
                                                continue;
                                            }
											int i9999 = i9999I;
											hm.put("setid", childNode.getNodeName());
											hm.put(xNode.getNodeName(), xNode.getTextContent());
											hm.put("i9999", String.valueOf(i9999));
											
										}
										ReturnInfoList.add(hm);
									}
								} else if ("ProjectInfo".equals(childNode.getNodeName())) { // 项目经验
								    int i9999I=0;
									for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
										HashMap hm = new HashMap();
										Node EduNode = childNode.getChildNodes().item(k);
										if (EduNode.getNodeType() != Node.ELEMENT_NODE) {
                                            continue;
                                        }
                                        i9999I++;
										for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
										    Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) {
                                                continue;
                                            }
											int i9999 =i9999I;
											hm.put("setid", childNode.getNodeName());
											hm.put(xNode.getNodeName(), xNode.getTextContent());
											hm.put("i9999", String.valueOf(i9999));
											
										}
										ReturnInfoList.add(hm);
									}
								} else if ("GradeOfEnglish".equals(childNode.getNodeName())) { // 英语等级
									HashMap hm = new HashMap();
                                    int i9999I=0;
									for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {

										Node EduNode = childNode.getChildNodes().item(k);
										if (EduNode.getNodeType() != Node.ELEMENT_NODE) {
                                            continue;
                                        }
										i9999I++;
										int i9999 = i9999I;
										hm.put("setid", childNode.getNodeName());
										hm.put(EduNode.getNodeName(), EduNode.getTextContent());
										hm.put("i9999", String.valueOf(i9999));
										
									}
									ReturnInfoList.add(hm);
								} else if ("LanguagesSkills".equals(childNode.getNodeName())) { // 语言能力
								    int i9999I=0;
									for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
										HashMap hm = new HashMap();
										Node EduNode = childNode.getChildNodes().item(k);
										if (EduNode.getNodeType() != Node.ELEMENT_NODE) {
                                            continue;
                                        }
                                        i9999I++;
										for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
										    Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) {
                                                continue;
                                            }
											int i9999 =i9999I;
											hm.put("setid", childNode.getNodeName());
											hm.put(xNode.getNodeName(), xNode.getTextContent());
											hm.put("i9999", String.valueOf(i9999));
											
										}
										ReturnInfoList.add(hm);
									}
								} else if ("ITSkills".equals(childNode.getNodeName())) { // IT技能
								    int i9999I=0;
									for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
										HashMap hm = new HashMap();
										Node EduNode = childNode.getChildNodes().item(k);
										if (EduNode.getNodeType() != Node.ELEMENT_NODE) {
                                            continue;
                                        }
										i9999I++;
										for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
										    Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) {
                                                continue;
                                            }
											int i9999 = i9999I;
											hm.put("setid", childNode.getNodeName());
											hm.put(xNode.getNodeName(), xNode.getTextContent());
											hm.put("i9999", String.valueOf(i9999));
										}
										ReturnInfoList.add(hm);
									}
								} else { // 基本类型
									HashMap hm = new HashMap();
									hm.put("setid", "");
									hm.put("i9999", "1");
									hm.put(childNode.getNodeName(), childNode.getTextContent());
									ReturnInfoList.add(hm);
								}
							}
						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("你的帐号或密码错误,请重新输入服务密码!"));
		}

		return ReturnInfoList;
	}

	/**
	 * 清除html文本格式
	 * 
	 * @param htmlStr
	 * @return
	 */
	public static String getPlainText(String str) {
		try {
			Parser parser = new Parser();
			parser.setInputHTML(str);
			StringBean sb = new StringBean();
			sb.setLinks(false);
			sb.setReplaceNonBreakingSpaces(true);
			sb.setCollapse(true);
			parser.visitAllNodesWith(sb);
			str = sb.getStrings();
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 
	 * @Title: addA01
	 * @Description: TODO增加简历信息到人才库
	 * @param ResumeInfoList
	 * @param blacklist_per
	 * @param blacklist_field
	 * @param blacklist_value
	 * @param ForeignJob
	 * @param PostID
	 * @param ResumeName
	 * @param userView
	 * @throws GeneralException
	 *             void
	 * @throws
	 */
	public void addA01(ArrayList ResumeInfoList, String blacklist_per, String blacklist_field, String blacklist_value, String ForeignJob, String PostID, UserView userView, String fileName)
			throws GeneralException {
		this.fileName = fileName;
		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(connection);
		EmployNetPortalBo bo = new EmployNetPortalBo(connection);
		ContentDAO dao = new ContentDAO(connection);
		RowSet rs = null;
		String flag = "0"; // 增加人员库标志0为人员库不存在,1,2,3为人员库中存在,分别为不导入,替换,追加
		String mainItem="";			//标识指标对应的简历值
		String secItem="";			//次关键指标对应的值

		try {
			// 获取人才库
			RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbName = "";

			if (vo != null) {
                dbName = vo.getString("str_value");
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
            }

			StringBuffer sb = userView.getDbpriv();// 用户人员库权限
			if (!userView.isSuper_admin() && !sb.toString().contains(dbName)) {
				throw GeneralExceptionHandler.Handle(new Exception("您没有操作应聘人员库权限!"));
			}

			String tableName = dbName + "A01"; // 人才库表名

			/** 黑名单检查 */
			if (blacklist_field != null && !"".equals(blacklist_field) && blacklist_per != null && !"".equals(blacklist_per)) {
				if (bo.isBlackPerson(blacklist_field, blacklist_per, blacklist_value)) {
					// String msg = "";
					blacklistLog += mainItem +"\t"+ secItem+ ";";
				}
			}
			// 人员库检查
			String username = "";
			String A0100 = "";
			
			ArrayList schemeParameterList = resumeImportSchemeXmlBo.getSchemeParameterList();
			String identifyfld="";
			String sencondfld="";
			for (int j = 0; j < schemeParameterList.size(); j++) {
				LazyDynaBean bean = (LazyDynaBean) schemeParameterList.get(j);
				identifyfld = (String) bean.get("identifyfld");			//标识指标
				sencondfld = (String) bean.get("sencondfld");			//次关键指标

			}
			StringBuffer Str= new StringBuffer("select * from "+dbName+ "A01 ");
			for (int i = 0; i < ResumeInfoList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) ResumeInfoList.get(i);

				
				if(bean.get("ehrfld").equals(identifyfld)&&bean.get("value")!=null&&!"".equals(bean.get("value"))){
					mainItem = (String) bean.get("value");
				}
				else if(bean.get("ehrfld").equals(sencondfld)&&bean.get("value")!=null&&!"".equals(bean.get("value"))){
					secItem = (String) bean.get("value");
				}
				else if("A0101".equals(bean.get("ehrfld"))&&bean.get("value")!=null&&!"".equals(bean.get("value"))){
					username = (String) bean.get("value");
				}
				
			}
			Str.append(" where "+identifyfld+"='"+mainItem+"'");
			if(!"".equals(sencondfld)){					//次关键指标不为空
				Str.append(" and "+sencondfld+"='"+secItem+"'");
			}
			//判断次关键指标
			boolean xflag = false;
			if(!"".equals(sencondfld)&&!"".equals(secItem)){
				xflag =true;
			}else if("".equals(sencondfld)){
				xflag = true;
			}
			
			
			//标识指标和标识指标对应简历的值不为空;若次关键指标不为空,则指标值也不为空
			if ((!"".equals(identifyfld)&&!"".equals(mainItem))||xflag) {
			rs = dao.search(Str.toString());
			if (rs.next()) {

				// 判断更新方式 imptype 不导入,替换,追加
				String imptype = resumeImportSchemeXmlBo.getimptype();
				if ("1".equals(imptype) || "".equals(imptype)) {
					
					
					flag = "1";
				} else if ("2".equals(imptype)) {
					A0100 = rs.getString("a0100");
					flag = "2";
					importNum++;
				} else if ("3".equals(imptype)) {
					flag = "3";
					importNum++;
				}

			} else {
				flag = "0";
				importNum++;
			}
		}else{
			//没有定义标识指标或标识指标未对应,不导入
			flag="4";
			FlistLog +=fileName+";";
		}
			
			
			
			if (flag == "1") {
				// 不导入
				PlistLog += mainItem +" "+secItem+ ";";
				// String msg = "您已经存在人才库中，不能导入简历！";
				// throw GeneralExceptionHandler.Handle(new Exception(msg));
			} else if (flag == "2") {
				
				// 替换
				RecordVo resumeVo = new RecordVo(tableName);
				resumeVo.setString("a0100", A0100);
				resumeVo = dao.findByPrimaryKey(resumeVo);

				//删除该人员的所有主集和子集信息
				ArrayList fieldSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);// 所有已构库人员库子集
				for(int i=0;i<fieldSetList.size();i++){
					FieldSet set = (FieldSet) fieldSetList.get(i);
					String tabName = dbName + set.getFieldsetid();
					String sql = "delete from "+tabName+" where a0100= '"+A0100+"'";
					dao.delete(sql, new ArrayList());
				}
				
				
				//新增该人员
				RecordVo recVo = new RecordVo(tableName);
				recVo.setString("a0100", A0100);
				dao.addValueObject(recVo);
				for (int i = 0; i < ResumeInfoList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) ResumeInfoList.get(i);
					String itemid = (String) bean.get("ehrfld");

					// 判断是主集和子集
					rs = dao.search("select fieldsetid from fielditem where Lower(itemid) ='" + itemid.toLowerCase() + "'");
					if (rs.next()) {
						String setID = rs.getString("fieldsetid");
						AddResumeInfo(dbName, A0100, setID, bean, mainItem, secItem);
					}

				}
				//删除该人员应聘岗位
				dao.delete("delete from zp_pos_tache where a0100="+A0100, new ArrayList());
				
				String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				StringBuffer str = new StringBuffer("select * from z03 where ");
				str.append(Sql_switcher.year("Z0329") + "*10000+" + Sql_switcher.month("Z0329") + "*100+" + Sql_switcher.day("Z0329") + "<=" + now);
				str.append(" and " + Sql_switcher.year("Z0331") + "*10000+" + Sql_switcher.month("Z0331") + "*100+" + Sql_switcher.day("Z0331") + ">=" + now);
				rs = dao.search(str.toString());
				while (rs.next()) {

					PostID = PostID.trim();
					String forJob = rs.getString(ForeignJob.toUpperCase());		//对外发布岗位
					String Job = rs.getString("z0338");							//需求岗位
					PostID = PubFunc.keyWord_reback(PostID);
					forJob = PubFunc.keyWord_reback(forJob);
					if (forJob != null) {
						forJob = forJob.trim();
					}

					if (forJob.equals(PostID)&&(forJob!=null||!"".equals(forJob))) {
						// 关联对外应聘岗位
						RecordVo zpVo = new RecordVo("zp_pos_tache");
						zpVo.setString("a0100", A0100);
						// String ss = this.frowset.getString("zp_pos_id");
						zpVo.setString("zp_pos_id", rs.getString("z0301"));
						zpVo.setString("thenumber", "1");
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
						zpVo.setDate("apply_date", df.format(new Date()));
						zpVo.setString("resume_flag", "10");
						zpVo.setString("status", "0");

						dao.addValueObject(zpVo);

					}
					else if((forJob==null|| "".equals(forJob))&&Job.equals(PostID)){
						RecordVo zpVo = new RecordVo("zp_pos_tache");
						zpVo.setString("a0100", A0100);
						// String ss = this.frowset.getString("zp_pos_id");
						zpVo.setString("zp_pos_id", rs.getString("z0301"));
						zpVo.setString("thenumber", "1");
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
						zpVo.setDate("apply_date", df.format(new Date()));
						zpVo.setString("resume_flag", "10");
						zpVo.setString("status", "0");

						dao.addValueObject(zpVo);
					}
				}
				
			} else if (flag == "0" || flag == "3") {
				// 新增人员
				RecordVo resumeVo = new RecordVo(tableName);//这时的tableName is:nbse+'A01'
				String a0100 = DbNameBo.insertMainSetA0100(dbName + "A01", connection);
				resumeVo.setString("a0100", a0100);
				resumeVo = dao.findByPrimaryKey(resumeVo);
				

				for (int i = 0; i < ResumeInfoList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) ResumeInfoList.get(i);
					String itemid = (String) bean.get("ehrfld");
					// 判断是主集和子集
					rs = dao.search("select fieldsetid from fielditem where Lower(itemid) ='" + itemid.toLowerCase() + "'");
					if (rs.next()) {
						String setID = rs.getString("fieldsetid");
						AddResumeInfo(dbName, a0100, setID, bean, mainItem,secItem);
					}

				}
				String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				StringBuffer str = new StringBuffer("select * from z03 where ");
				str.append(Sql_switcher.year("Z0329") + "*10000+" + Sql_switcher.month("Z0329") + "*100+" + Sql_switcher.day("Z0329") + "<=" + now);
				str.append(" and " + Sql_switcher.year("Z0331") + "*10000+" + Sql_switcher.month("Z0331") + "*100+" + Sql_switcher.day("Z0331") + ">=" + now);
				rs = dao.search(str.toString());
				while (rs.next()) {

					PostID = PostID.trim();
					String forJob = rs.getString(ForeignJob.toUpperCase());		//对外发布岗位
					String Job = rs.getString("z0338");			//需求岗位
					PostID = PubFunc.keyWord_reback(PostID);
					forJob = PubFunc.keyWord_reback(forJob);
					if (forJob != null) {
						forJob = forJob.trim();
					}

					if (forJob.equals(PostID)) {
						// 关联对外应聘岗位
						RecordVo zpVo = new RecordVo("zp_pos_tache");
						zpVo.setString("a0100", a0100);
						// String ss = this.frowset.getString("zp_pos_id");
						zpVo.setString("zp_pos_id", rs.getString("z0301"));
						zpVo.setString("thenumber", "1");
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
						zpVo.setDate("apply_date", df.format(new Date()));
						zpVo.setString("resume_flag", "10");
						zpVo.setString("status", "0");

						dao.addValueObject(zpVo);

					}
					else if((forJob==null|| "".equals(forJob))&&Job.equals(PostID)){
						RecordVo zpVo = new RecordVo("zp_pos_tache");
						zpVo.setString("a0100", A0100);
						// String ss = this.frowset.getString("zp_pos_id");
						zpVo.setString("zp_pos_id", rs.getString("z0301"));
						zpVo.setString("thenumber", "1");
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
						zpVo.setDate("apply_date", df.format(new Date()));
						zpVo.setString("resume_flag", "10");
						zpVo.setString("status", "0");

						dao.addValueObject(zpVo);
					}
				}

				// dao.updateValueObject(resumeVo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @Title: AddResumeInfo
	 * @Description: TODO 插入简历信息到人才库
	 * @param dbname
	 * @param a0100
	 * @param setID
	 * @param bean
	 * @param ResumeName
	 * @throws GeneralException
	 *             void
	 * @throws
	 */
	public void AddResumeInfo(String dbname, String a0100, String setID, LazyDynaBean bean, String mainItem,String secItem) throws GeneralException {
		ContentDAO dao = new ContentDAO(connection);
		DbNameBo bo = new DbNameBo(connection);
		EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(connection);
		try {
			String tableName = dbname.toLowerCase() + setID; // 人才库表名

			RecordVo vo = new RecordVo(tableName);
			vo.setString("a0100", a0100);
			//增加主集信息
			if ("a01".equalsIgnoreCase(setID)) {
				vo = dao.findByPrimaryKey(vo);
				vo = getRecordVo(vo, bean, mainItem,secItem);
				dao.updateValueObject(vo);

			} else {
				String i9999 = (String) bean.get("i9999");
				// i9999=employNetPortalBo.getI9999(setID, a0100, dbname)+"";
				vo.setInt("i9999", Integer.parseInt(i9999));

				boolean flag = true;
				while (flag) {
					if (employNetPortalBo.getI9999(setID, a0100, dbname) >= Integer.parseInt(i9999)) {
						vo = dao.findByPrimaryKey(vo);
						vo = getRecordVo(vo, bean, mainItem,secItem);
						if(vo!=null){
							dao.updateValueObject(vo);
							flag = false;	
						}

					} else {
						bo.insertSubSetA0100(tableName, a0100, connection);
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/**
	 * 
	 * @Title: getRecordVo
	 * @Description: TODO 插入子集信息
	 * @param vo
	 * @param bean
	 * @param ResumeName
	 * @return
	 * @throws GeneralException
	 *             RecordVo
	 * @throws
	 */
	public RecordVo getRecordVo(RecordVo vo, LazyDynaBean bean, String mainItem,String secItem) throws GeneralException {
		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(connection);
		ContentDAO dao = new ContentDAO(connection);
		RowSet rs = null;
		try {
			String itemid = (String) bean.get("ehrfld");//字段的itemid
			String value = (String) bean.get("value");//字段对应的值
			// 判断指标是否为代码型
			rs = dao.search("select codesetid from FIELDITEM where UPPER(ITEMID)='" + itemid.toUpperCase() + "' and UPPER(CODESETID)<>'0'");
			if (rs.next()) {
				String codesetid = rs.getString("codesetid");

				String resumefld = (String) bean.get("resumefld");//字段的描述
				String resumeset = (String) bean.get("resumeset");//指标集描述
				String CodeItem = "";
				if(!"".equals(value)){
					CodeItem = resumeImportSchemeXmlBo.getCodeItem(resumeset, resumefld, value, codesetid);
				}
				
				// 若codeitem为空则代码型指标未对应
				if ("".equals(CodeItem)&&!"".equals(value)) {
					vo.setString(((String) bean.get("ehrfld")).toLowerCase(), CodeItem);

					ClistLog += (mainItem+"\t"+secItem + "(" + resumeset + ")" + ":" + resumefld + "未对应" + "\r\n");
					// String msg = "代码指标未对应";
					// throw GeneralExceptionHandler.Handle(new Exception(
					// msg));
				} else {
					vo.setString(((String) bean.get("ehrfld")).toLowerCase(), CodeItem);
				}
			} else { // 不为代码型指标
				String itemtype = (String) bean.get("itemtype");
				if ("A".equals(itemtype)) {
					vo.setString(((String) bean.get("ehrfld")).toLowerCase(), value);

				} else if ("D".equals(itemtype)) {
					value = value.replaceAll("\\.", "-");
					value = value.replace("年", "-");
					value = value.replace("月", "-");
					value = value.replace("日", "-");
					value = value.replaceAll("\\'", "");
					String[] dd = value.split("-");
					String year = dd[0];
					String month = "01";
					String day = "01";
					if (dd.length >= 2 && dd[1] != null && dd[1].trim().length() > 0) {
                        month = dd[1];
                    }
					if (dd.length >= 3 && dd[2] != null && dd[2].trim().length() > 0) {
                        day = dd[2];
                    }
					Calendar d = Calendar.getInstance();
					if ("至今".equals(value)) {
						value = null;
						vo.setDate(((String) bean.get("ehrfld")).toLowerCase(), value);
					}
					else if (year != null && !"".equals(year)) {
						d.set(Calendar.YEAR, Integer.parseInt(year));
						d.set(Calendar.MONTH, Integer.parseInt(month) - 1);
						d.set(Calendar.DATE, Integer.parseInt(day));
						vo.setDate(((String) bean.get("ehrfld")).toLowerCase(), year + "-" + month + "-" + day);
					}

				} else if ("M".equals(itemtype)) {
					vo.setString(((String) bean.get("ehrfld")).toLowerCase(), value);
				} else if ("N".equals(itemtype)) {

					vo.setInt(itemid.toLowerCase(), Integer.parseInt(value));

				} else {
                    vo.setString(itemid.toLowerCase(), null);
                }

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return vo;

	}

	/**
	 * 获取黑名单Log
	 * 
	 * @Title: getBlacklistLog
	 * @Description: TODO
	 * @return String
	 * @throws
	 */
	public String getBlacklistLog() {
		return blacklistLog;
	}

	/**
	 * 获取人员库Log
	 * 
	 * @Title: getPlistLog
	 * @Description: TODO
	 * @return String
	 * @throws
	 */
	public String getPlistLog() {
		return PlistLog;
	}

	/**
	 * 获取代码对应Log
	 * 
	 * @Title: getClistLog
	 * @Description: TODO
	 * @return String
	 * @throws
	 */
	public String getClistLog() {
		return ClistLog;
	}

	public String getFlistLog() {
		return FlistLog;
	}
	/**
	 * 校验并解析所传内容
	 * @param content
	 * @param bo
	 * @param mailTitle
	 * @param menuList
	 * @param confirmMap
	 * @param uv
	 */
	public void resumeContent(String content,TransResumeBo bo,String fileName,ArrayList menuList,HashMap confirmMap,UserView uv,int Num){
		String ForeignJob = ""; // 简历解析关联的对外岗位指标
		String PostID = ""; // 简历的应聘岗位
		String ResumeName = "";
		String blacklist_per=(String) confirmMap.get("blacklist_per");
		String blacklist_field=(String) confirmMap.get("blacklist_field");
		String blacklist_value="";
		String sendtime=(String) confirmMap.get("sendtime");
		String contentError=(String) confirmMap.get("contentError");
		Num=Integer.parseInt((String) confirmMap.get("Num"));
		String ATTACHMENT=(String) confirmMap.get("ATTACHMENT");
		try{		
			ArrayList ResumeInfoList = new ArrayList(); // 个人简历信息集
			boolean isText=false;//是否是文本类型的
			if("false".equalsIgnoreCase(ATTACHMENT)){
				isText=true;
			}
			boolean exist=false;//附件中是否有和文本的人同名的
			String name="";
			int length=0;
			if(content!=null) {
                length=content.getBytes().length;
            }
			if (content!=null&&content.length() > 0&&length>150) {
				NodeList list = bo.getResumeList(content,fileName);
				ArrayList ReturnInfoList = bo.ProceedReturnInfo(list);
				for (int k = 0; k < ReturnInfoList.size(); k++) {
					HashMap hm = (HashMap) ReturnInfoList.get(k);
					if("".equals(hm.get("setid"))){
						if (hm.containsKey("Name")){
							name=(String) hm.get("Name");
						}
					}
					for (int l = 0; l < menuList.size(); l++) {
						LazyDynaBean baen = (LazyDynaBean) menuList.get(l);
						// 判断setid是否相等,若为空则为基本信息
						if (hm.get("setid").equals(baen.get("setid"))) {
							if (hm.containsKey(baen.get("itemid"))) {
								if ("应聘岗位".equals(baen.get("resumefld"))) {
									PostID = (String) hm.get(baen.get("itemid"));
								}
								if ("姓名".equals(baen.get("resumefld"))) {
									ResumeName = (String) hm.get(baen.get("itemid"));
									if(!isText){//解析附件 与文本姓名作比较 若一致不解析
										if(ResumeName.equals(confirmMap.get("name"))){
											exist=true;
											break;
										}

									}
								}
	
								// 判断指标是否导入
								if ("1".equals(baen.get("valid"))) {
									LazyDynaBean ResumeBean = new LazyDynaBean();
									ResumeBean.set("itemtype", baen.get("itemtype"));
									ResumeBean.set("itemlength", baen.get("itemlength"));
									ResumeBean.set("valid", baen.get("valid"));
									ResumeBean.set("ehrfld", baen.get("ehrfld"));
									ResumeBean.set("resumefld", baen.get("resumefld"));
									ResumeBean.set("resumeset", baen.get("resumeset"));
									ResumeBean.set("itemformat", baen.get("itemformat"));
									ResumeBean.set("setid", baen.get("setid"));
									ResumeBean.set("itemid", baen.get("itemid"));
									ResumeBean.set("commonvalue", baen.get("commonvalue"));
									ResumeBean.set("itemid", baen.get("itemid"));
									ResumeBean.set("value", hm.get(baen.get("itemid")));
									ResumeBean.set("i9999", hm.get("i9999"));
									// 若指标等于黑名单指标
									if (((String) baen.get("ehrfld")).equalsIgnoreCase(blacklist_field)) {
										blacklist_value = (String) hm.get(baen.get("itemid"));
									}
									ResumeInfoList.add(ResumeBean);
								}
							}
						}
	
					}

				}
					if(isText) {
                        confirmMap.put("name", name);//将解析出的文本中的姓名存入内存
                    }
					
				if(!exist){
					if(!isText) {
                        Num+=1;
                    }
						confirmMap.put("Num", Num+"");//解析的简历数
						// 增加简历信息
						if (ResumeInfoList.size() > 0) {
							bo.addA01(ResumeInfoList, blacklist_per, blacklist_field, blacklist_value, ForeignJob, PostID, uv, fileName);
	
						}else{
							if(FlistLog.indexOf(fileName)==-1){
								contentError +="邮件或附件："+fileName+"; ";
								confirmMap.put("contentError", contentError);
							}

						}

				}
				
			}else{
				if(isText) {
                    confirmMap.put("name", name);//将解析出的文本中的姓名存入内存
                }
				if(!isText) {
                    Num+=1;
                }
				confirmMap.put("Num", Num+"");//解析的简历数
				if(!"格式错误".equals(content)){
					if(FlistLog.indexOf(fileName)==-1){
						contentError +="邮件或附件："+fileName+"; ";
						confirmMap.put("contentError", contentError);
					};	
				}

			}
			/***************若正文内容出现异常 但附件有内容 正文不作为一封简历********************************/
			if(!isText){
				if(contentError.indexOf("邮件或附件："+confirmMap.get("mailTitle")+"; ")!=-1){
					contentError =contentError.replace("邮件或附件："+confirmMap.get("mailTitle")+"; ", "  ");
					Num =Num-1;
					confirmMap.put("contentError", contentError);
					confirmMap.put("Num", Num+"");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
/**
 * 获取邮件内容
 * @param part
 * @param confirmMap
 * @param bo
 * @param Num
 * @param menuList
 * @param uv
 * @param mailTitle
 * @return
 * @throws MessagingException
 * @throws IOException
 * @throws GeneralException 
 */
	public void getMailContent(Part part,HashMap confirmMap, TransResumeBo bo,int Num,ArrayList menuList,UserView uv,String mailTitle) throws MessagingException, IOException, GeneralException {
		String contentType = part.getContentType();
		int nameindex = contentType.indexOf("name");
		StringBuffer bodytext = new StringBuffer();
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}
		String description=part.getDisposition();
		Num=Integer.parseInt((String) confirmMap.get("Num"));
		InputStream is=null;
		InputStream zs=null;
		BufferedReader br = null;
        BufferedReader bu = null;
        OutputStream outputStream = null;
		try {
	
			if (part.isMimeType("text/plain") && !conname) {
				bodytext.append((String) part.getContent());
				confirmMap.put("textContent", confirmMap.get("textContent")==null?"":confirmMap.get("textContent")+bodytext.toString());
			} else if (part.isMimeType("text/html") && !conname) {
				bodytext.append((String) part.getContent());
				confirmMap.put("textContent", confirmMap.get("textContent")==null?"":confirmMap.get("textContent")+bodytext.toString());
				if("1".equals(confirmMap.get("name"))){//到此邮件正文就完了？
					String textContent=(String) confirmMap.get("textContent");//文本内容
					textContent=TransResumeBo.getPlainText(textContent);
					resumeContent(textContent, bo, mailTitle, menuList,confirmMap,uv, Num);///解析文本内容
				}
			} else if (part.isMimeType("multipart/*")) {
				Multipart multipart = (Multipart) part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++) {
					getMailContent(multipart.getBodyPart(i),confirmMap,bo,Num, menuList, uv, mailTitle);
				}
			} else if (part.isMimeType("message/rfc822")) {
					getMailContent((Part) part.getContent(),confirmMap,bo,Num, menuList, uv, mailTitle);
			}
			/************** 邮件中包含附件 **********************/
			if(description!=null&& "ATTACHMENT".equalsIgnoreCase(description)){
				confirmMap.put("ATTACHMENT", "true");// 邮件带附件
				String fileName=part.getFileName();
				fileName=MimeUtility.decodeText(fileName);
				is=part.getInputStream();
				zs=part.getInputStream();
				String content="";
				String extention=fileName.substring(fileName.lastIndexOf(".")+1);
				String name=fileName.substring(0,fileName.lastIndexOf("."));
				if (!"".equals(fileName) && "txt".equalsIgnoreCase(extention)) { // 解析txt格式的简历
					br = new BufferedReader(new InputStreamReader(is));
					String str;
					while ((str = br.readLine()) != null) {
						content += str;
					};
					resumeContent(content, bo, fileName, menuList,confirmMap,uv, Num);
				} else if (!"".equals(fileName) // 解析htm或html格式的简历
						&& ("htm".equalsIgnoreCase(extention) || "html".equalsIgnoreCase(extention))) {
					br = new BufferedReader(new InputStreamReader(is));
					String charset = "unicode";
					String line = "";
					while ((line = br.readLine()) != null) {
						if (line.contains("<meta") && line.contains("charset")) {
							if (line.contains("UTF-8") || line.contains("utf-8")) {
								charset = "utf-8";
								break;
							} else if (line.contains("gbk") || line.contains("GBK")) {
								charset = "gbk";
								break;
							} else if (line.contains("gb2312") || line.contains("GB2312")) {
								charset = "gb2312";
								break;
							}
						}
	
					}
	
                    bu = new BufferedReader(new InputStreamReader(is, charset));
					String str = "";
					while ((str = bu.readLine()) != null) {
						content += str;
					}
	
					content = TransResumeBo.getPlainText(content);
					resumeContent(content, bo, fileName, menuList,confirmMap,uv, Num);
				} else if (!"".equals(fileName) && "mht".equalsIgnoreCase(extention)) {
					Session mailSession = Session.getDefaultInstance(System.getProperties(), null);
					MimeMessage msg = new MimeMessage(mailSession, is);
					Object content1 = msg.getContent();
					if (content1 instanceof Multipart) {
						MimeMultipart mp = (MimeMultipart) content1;
						MimeBodyPart bp1 = (MimeBodyPart) mp.getBodyPart(0);
	
						// 获取mht文件内容代码的编码
						String strEncodng = "gb2312";
						strEncodng = TransResumeBo.getEncoding(bp1);
						content = TransResumeBo.getHtmlText(bp1, strEncodng);
						content = TransResumeBo.getPlainText(content);
					}
					resumeContent(content, bo, fileName, menuList,confirmMap,uv, Num);
				}
				else if (!"".equals(fileName) && "pdf".equalsIgnoreCase(extention)) { // 解析pdf文档
	
					PDFParser parser = new PDFParser(is);
	
					parser.parse();
	
					PDDocument document = parser.getPDDocument();
	
					PDFTextStripper stripper = new PDFTextStripper();
					content = stripper.getText(document);
					resumeContent(content, bo, fileName, menuList,confirmMap,uv, Num);
				}else if(!"".equals(fileName) && ("doc".equalsIgnoreCase(extention)|| "docx".equalsIgnoreCase(extention))){//简历解析支持word(2003~2010格式)
				    ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
	                 byte [] fileByte = new byte[1024];
	                 int len=0;
	                 while(( len = is.read(fileByte)) != -1){
	                     out.write(fileByte, 0, len);
	                 }
	                 Base64 bin = new Base64();
	                 
				    String fileString =bin.encode(out.toByteArray());
				    resumeContentForUpFile(fileString, bo, fileName, menuList,confirmMap,uv, Num,extention);
				}
				else if (!"".equals(fileName) && "zip".equalsIgnoreCase(extention)) { // 解析zip压缩文件
					String ferror="";
					/*首先上传zip压缩包至服务器端*/	
					
					String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + name;
					outputStream = new FileOutputStream(filePath);
					int bytesRead = 0;
					byte[] buffer = new byte[8192];
					while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
					outputStream.close();
					// 从服务器获得zipfile对象并解析
					ZipFile zipFile = new ZipFile(filePath); // 根据路径取得需要解压的Zip文件
					zipFile.setFileNameCharset("GBK");
					List fileHeaderList = zipFile.getFileHeaders();
					for (int i = 0; i < fileHeaderList.size(); i++) {
						FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
						if(fileHeader.isDirectory())//若是文件夹 continue
                        {
                            continue;
                        }
						 fileName = "";
						 extention = "";
						if (fileHeader.getFileName().length() > 0 && fileHeader.getFileName() != null) { // --截取文件名
							int j = fileHeader.getFileName().lastIndexOf(".");
							int k = fileHeader.getFileName().lastIndexOf("/");
							
							if (j > -1 && j < fileHeader.getFileName().length()&&!fileHeader.isDirectory()) {
								fileName = fileHeader.getFileName().substring(k + 1, j); // --文件名
								extention = fileHeader.getFileName().substring(j + 1); // --扩展名
							}
						}
						// System.out.println(fileName);
						String jl = "";
						ArrayList ResumeInfoList = new ArrayList(); // 个人简历信息集

						if (!"".equals(fileName) && "txt".equalsIgnoreCase(extention)) { // 解析txt格式的简历
							is = zipFile.getInputStream(fileHeader);
							br = new BufferedReader(new InputStreamReader(is));
							String str;

							while ((str = br.readLine()) != null) {
								jl += str;
							}
							resumeContent(jl, bo, fileName, menuList,confirmMap,uv, Num);

						} else if (!"".equals(fileName) // 解析htm或html格式的简历
								&& ("htm".equalsIgnoreCase(extention) || "html".equalsIgnoreCase(extention))) {
							is = zipFile.getInputStream(fileHeader);
							zs = zipFile.getInputStream(fileHeader);
							// Encoding code = Encoding.GetEncoding("UTF-8");

							br = new BufferedReader(new InputStreamReader(is));

							String charset = "unicode";
							String line = "";
							while ((line = br.readLine()) != null) {
								// System.out.println(line);
								if (line.contains("<meta") && line.contains("charset")) {
									if (line.contains("UTF-8") || line.contains("utf-8")) {
										charset = "utf-8";
										break;
									} else if (line.contains("gbk") || line.contains("GBK")) {
										charset = "gbk";
										break;
									} else if (line.contains("gb2312") || line.contains("GB2312")) {
										charset = "gb2312";
										break;
									}
								}

							}

							bu = new BufferedReader(new InputStreamReader(zs, charset));
							String str = "";
							while ((str = bu.readLine()) != null) {
								jl += str;
							}

							jl = TransResumeBo.getPlainText(jl);
							// System.out.println(jl);
							resumeContent(jl, bo, fileName, menuList,confirmMap,uv, Num);

						} else if (!"".equals(fileName) && "mht".equalsIgnoreCase(extention)) {
							is = zipFile.getInputStream(fileHeader);
							Session mailSession = Session.getDefaultInstance(System.getProperties(), null);
							MimeMessage msg = new MimeMessage(mailSession, is);
							Object content1 = msg.getContent();
							if (content1 instanceof Multipart) {
								MimeMultipart mp = (MimeMultipart) content1;
								MimeBodyPart bp1 = (MimeBodyPart) mp.getBodyPart(0);

								// 获取mht文件内容代码的编码
								String strEncodng = "gb2312";
								strEncodng = TransResumeBo.getEncoding(bp1);
								jl = TransResumeBo.getHtmlText(bp1, strEncodng);
								jl = TransResumeBo.getPlainText(jl);
							}

							resumeContent(jl, bo, fileName, menuList,confirmMap,uv, Num);

						}
						else if (!"".equals(fileName) && "pdf".equalsIgnoreCase(extention)) { // 解析pdf文档

							is = zipFile.getInputStream(fileHeader);

							PDFParser parser = new PDFParser(is);

							parser.parse();

							PDDocument document = parser.getPDDocument();

							PDFTextStripper stripper = new PDFTextStripper();
							jl = stripper.getText(document);

							resumeContent(jl, bo, fileName, menuList,confirmMap,uv, Num);

						} else if (!fileHeader.isDirectory()) {
							// 格式错误
							jl="格式错误";
							fileName= fileName + "." + extention + ";";
							confirmMap.put("ferror", (confirmMap.get("ferror")==null?"":confirmMap.get("ferror"))+fileName);
							resumeContent(jl, bo, fileName, menuList,confirmMap,uv, Num);
//							Num++;
						}
					}
				}	
	
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(is);
			PubFunc.closeIoResource(zs);
			PubFunc.closeIoResource(br);
			PubFunc.closeIoResource(bu);
			PubFunc.closeIoResource(outputStream);
		}		
	}
	
	
	/** 
 * @Title: resumeContentForUpFile 
 * @Description: 用于解析上传文件类型的导入文档
 * @param fileString
 * @param bo
 * @param fileName2
 * @param menuList
 * @param confirmMap
 * @param uv
 * @param num void   
 * @throws 
*/
private void resumeContentForUpFile(String content,TransResumeBo bo,String fileName,ArrayList menuList,HashMap confirmMap,UserView uv,int Num,String extention) {

    String ForeignJob = ""; // 简历解析关联的对外岗位指标
    String PostID = ""; // 简历的应聘岗位
    String ResumeName = "";
    String blacklist_per=(String) confirmMap.get("blacklist_per");
    String blacklist_field=(String) confirmMap.get("blacklist_field");
    String blacklist_value="";
    String sendtime=(String) confirmMap.get("sendtime");
    String contentError=(String) confirmMap.get("contentError");
    Num=Integer.parseInt((String) confirmMap.get("Num"));
    String ATTACHMENT=(String) confirmMap.get("ATTACHMENT");
    try{        
        ArrayList ResumeInfoList = new ArrayList(); // 个人简历信息集
        boolean isText=false;//是否是文本类型的
        if("false".equalsIgnoreCase(ATTACHMENT)){
            isText=true;
        }
        boolean exist=false;//附件中是否有和文本的人同名的
        String name="";
        int length=0;
        if(content!=null) {
            length=content.getBytes().length;
        }
        if (content!=null&&content.length() > 0&&length>150) {
            NodeList list = bo.getResumeListForByteFile(content, fileName, extention);
            ArrayList ReturnInfoList = bo.ProceedReturnInfoForFile(list);
            for (int k = 0; k < ReturnInfoList.size(); k++) {
                HashMap hm = (HashMap) ReturnInfoList.get(k);
                if("".equals(hm.get("setid"))){
                    if (hm.containsKey("Name")){
                        name=(String) hm.get("Name");
                    }
                }
                for (int l = 0; l < menuList.size(); l++) {
                    LazyDynaBean baen = (LazyDynaBean) menuList.get(l);
                    // 判断setid是否相等,若为空则为基本信息
                    if (hm.get("setid").equals(baen.get("setid"))) {
                        if (hm.containsKey(baen.get("itemid"))) {
                            if ("应聘岗位".equals(baen.get("resumefld"))) {
                                PostID = (String) hm.get(baen.get("itemid"));
                            }
                            if ("姓名".equals(baen.get("resumefld"))) {
                                ResumeName = (String) hm.get(baen.get("itemid"));
                                if(!isText){//解析附件 与文本姓名作比较 若一致不解析
                                    if(ResumeName.equals(confirmMap.get("name"))){
                                        exist=true;
                                        break;
                                    }

                                }
                            }

                            // 判断指标是否导入
                            if ("1".equals(baen.get("valid"))) {
                                LazyDynaBean ResumeBean = new LazyDynaBean();
                                ResumeBean.set("itemtype", baen.get("itemtype"));
                                ResumeBean.set("itemlength", baen.get("itemlength"));
                                ResumeBean.set("valid", baen.get("valid"));
                                ResumeBean.set("ehrfld", baen.get("ehrfld"));
                                ResumeBean.set("resumefld", baen.get("resumefld"));
                                ResumeBean.set("resumeset", baen.get("resumeset"));
                                ResumeBean.set("itemformat", baen.get("itemformat"));
                                ResumeBean.set("setid", baen.get("setid"));
                                ResumeBean.set("itemid", baen.get("itemid"));
                                ResumeBean.set("commonvalue", baen.get("commonvalue"));
                                ResumeBean.set("itemid", baen.get("itemid"));
                                ResumeBean.set("value", hm.get(baen.get("itemid")));
                                ResumeBean.set("i9999", hm.get("i9999"));
                                // 若指标等于黑名单指标
                                if (((String) baen.get("ehrfld")).equalsIgnoreCase(blacklist_field)) {
                                    blacklist_value = (String) hm.get(baen.get("itemid"));
                                }
                                ResumeInfoList.add(ResumeBean);
                            }
                        }
                    }

                }

            }
                if(isText) {
                    confirmMap.put("name", name);//将解析出的文本中的姓名存入内存
                }
                
            if(!exist){
                if(!isText) {
                    Num+=1;
                }
                    confirmMap.put("Num", Num+"");//解析的简历数
                    // 增加简历信息
                    if (ResumeInfoList.size() > 0) {
                        bo.addA01(ResumeInfoList, blacklist_per, blacklist_field, blacklist_value, ForeignJob, PostID, uv, fileName);

                    }else{
                        if(FlistLog.indexOf(fileName)==-1){
                            contentError +="邮件或附件："+fileName+"; ";
                            confirmMap.put("contentError", contentError);
                        }

                    }

            }
            
        }else{
            if(isText) {
                confirmMap.put("name", name);//将解析出的文本中的姓名存入内存
            }
            if(!isText) {
                Num+=1;
            }
            confirmMap.put("Num", Num+"");//解析的简历数
            if(!"格式错误".equals(content)){
                if(FlistLog.indexOf(fileName)==-1){
                    contentError +="邮件或附件："+fileName+"; ";
                    confirmMap.put("contentError", contentError);
                };  
            }

        }
        /***************若正文内容出现异常 但附件有内容 正文不作为一封简历********************************/
        if(!isText){
            if(contentError.indexOf("邮件或附件："+confirmMap.get("mailTitle")+"; ")!=-1){
                contentError =contentError.replace("邮件或附件："+confirmMap.get("mailTitle")+"; ", "  ");
                Num =Num-1;
                confirmMap.put("contentError", contentError);
                confirmMap.put("Num", Num+"");
            }
        }
    }catch(Exception e){
        e.printStackTrace();
    }

    
}

    /**
     * 
     * @Title: ProceedReturnInfo
     * @Description: TODO解析简历节点集
     * @param list
     * @return 指标集下指标项对应的值
     * @throws GeneralException
     *             ArrayList
     * @throws
     */
    public ArrayList ProceedReturnInfo(NodeList list) throws GeneralException {
        ArrayList ReturnInfoList = new ArrayList();
        try {
            if (list != null) {

                for (int i = 0; i < list.getLength(); i++) {
                    Node node = (Node) list.item(i);
                    if ("TransResumeResponse".equals(node.getNodeName())) {
                        
                        Node resultnode = node.getChildNodes().item(0);
                        if (resultnode!=null&&"TransResumeResult".equals(resultnode.getNodeName())) {

                            for (int j = 0; j < resultnode.getChildNodes().getLength(); j++) {
                                Node childNode = resultnode.getChildNodes().item(j);
                                if ("EducationInfo".equals(childNode.getNodeName())) { // 教育信息

                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ExperienceInfo".equals(childNode.getNodeName())) { // 工作经历
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("TrainingInfo".equals(childNode.getNodeName())) { // 培训经历
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ProjectInfo".equals(childNode.getNodeName())) { // 项目经验
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("GradeOfEnglish".equals(childNode.getNodeName())) { // 英语等级
                                    HashMap hm = new HashMap();
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {

                                        Node EduNode = childNode.getChildNodes().item(k);

                                        int i9999 = k + 1;
                                        hm.put("setid", childNode.getNodeName());
                                        hm.put(EduNode.getNodeName(), EduNode.getTextContent());
                                        hm.put("i9999", String.valueOf(i9999));
                                    }
                                    ReturnInfoList.add(hm);
                                } else if ("LanguagesSkills".equals(childNode.getNodeName())) { // 语言能力
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ITSkills".equals(childNode.getNodeName())) { // IT技能
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else { // 基本类型
                                    HashMap hm = new HashMap();
                                    hm.put("setid", "");
                                    hm.put("i9999", "1");
                                    //System.out.println(childNode.getNodeName()+"1"+childNode.getTextContent());
                                    hm.put(childNode.getNodeName(), childNode.getTextContent());
                                    ReturnInfoList.add(hm);
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
//          e.printStackTrace();
            
            throw GeneralExceptionHandler.Handle(new Exception("你的帐号或密码错误,请重新输入服务密码!"));
        }

        return ReturnInfoList;
    }

    /** 
	 * 获取mht文件中的内容代码 
	 * @param bp 
	 * @param strEncoding 该mht文件的编码 
	* @return 
	 */  
	public static String getHtmlText(MimeBodyPart bp, String strEncoding) {    
	    InputStream textStream = null;    
	    BufferedInputStream buff = null;    
	    BufferedReader br = null;    
	    Reader r = null;    
	    try {    
	        textStream = bp.getInputStream();    
	        buff = new BufferedInputStream(textStream);    
	        r = new InputStreamReader(buff, strEncoding);   
	        br = new BufferedReader(r);    
	        StringBuffer strHtml = new StringBuffer("");    
	        String strLine = null;    
	        while ((strLine = br.readLine()) != null) {    
	            strHtml.append(strLine + "\r\n");    
	        }    
	        return strHtml.toString();    
	    } catch (Exception e) {    
	        e.printStackTrace();    
	    } finally{  
	        PubFunc.closeIoResource(textStream);
	        PubFunc.closeIoResource(buff);
	        PubFunc.closeIoResource(r);
	        PubFunc.closeIoResource(br);
	    }    
	    return null;    
	}  
	  
	/** 
	 * 获取mht网页文件中内容代码的编码 
	 * @param bp 
	 * @return 
	 */  
	public static String getEncoding(MimeBodyPart bp) {  
	    if(bp==null){  
	        return null;  
	    }  
	    try {    
	        Enumeration list = bp.getAllHeaders();    
	        while (list.hasMoreElements()) {    
	           javax.mail.Header head = (javax.mail.Header)list.nextElement();    
	            if (head.getName().compareTo("Content-Type") == 0) {    
	                String strType = head.getValue();    
	                int pos = strType.indexOf("charset=");    
	                if (pos>=0) {    
	                    String strEncoding = strType.substring(pos + 8, strType.length());    
	                    if(strEncoding.startsWith("\"") || strEncoding.startsWith("\'")){  
	                        strEncoding = strEncoding.substring(1 , strEncoding.length());  
	                    }  
	                    if(strEncoding.endsWith("\"") || strEncoding.endsWith("\'")){  
	                        strEncoding = strEncoding.substring(0 , strEncoding.length()-1);  
	                    }  
//	                    if (strEncoding.toLowerCase().compareTo("gb2312") == 0) {    
//	                        strEncoding = "gbk";    
//	                    }    
	                    return strEncoding;    
	                }    
	            }  
	        }    
	    } catch (MessagingException e) {    
	        e.printStackTrace();    
	    }  
	    return "gb2312";   
	}  

	/**
	 * 记录导入信息
	 * 
	 * @throws GeneralException
	 */
	public String WriteImportDetail(UserView userview,String blacklistLog, String plistLog, String ferror,String contentError, String Clist,String Flist,String mode,int Num) throws GeneralException {
		File file = null;
		BufferedWriter output = null;
		String outname = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new Date());
			outname = "";
			String pathFile = System.getProperty("java.io.tmpdir");
			pathFile += "\\" + "ResumeImportLog.txt";
			file = new File(pathFile);
			output = new BufferedWriter(new FileWriter(file,true));
			// output.write(date+"\n");

			StringBuffer Log = new StringBuffer();
			
			Log.append(userview.getUserName()+"\t"+date+"\t"+mode+"\t"+"共解析"+Num+"份简历,成功"+importNum+"份简历");
			if(Num-importNum>0&&importNum>0){
				Log.append(","+(Num-importNum)+"份导入不成功");
				outname="导入部分成功!";
			}else if(Num-importNum==0){
				outname="导入成功!";
			}else if(importNum==0){
				outname="导入不成功!";
			}
			Log.append("\r\n");
			if(Num>importNum||!"".equals(Clist)){			
				Log.append("详细信息:\r\n");
			}

			if (!"".equals(ferror)) {
				Log.append("以下文件不符合格式,不能导入:\r\n"+ferror+"\r\n");
			}
			if (contentError!=null&&!"".equals(contentError.trim())) {
				Log.append("以下文件内容不是简历或者与导入方案不对应,不能导入:\r\n"+contentError+"\r\n");
			}
			if(!"".equals(Flist)){
				Log.append("以下记录未能正确解析:" + "\r\n");
				Log.append(Flist+"\r\n");
			}
			if (!"".equals(blacklistLog)) {
				Log.append("以下记录在黑名单库中存在,不能导入:"+blacklistLog+"\r\n");

			}
			if (!"".equals(plistLog)) {
				Log.append("以下记录人员库中已存在,不能导入:" + "\r\n");
				Log.append(plistLog + "\r\n");
			}
			if (!"".equals(Clist)) {
				Log.append("以下人员已导入,但有些代码型指标未对应:" + "\r\n");
				Log.append(Clist + "\r\n");
			}

			output.write(Log.toString() + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(output);
			PubFunc.closeResource(file);
		}
		return outname;
	}

    /**
     * @param extention 文件类型 
     * @Title: getResumeListForByteFile 
     * @Description: TODO
     * @param wordBuffer Base64字节码文件
     * @param string 文件名字
     * @return NodeList   
     * @throws 
    */
    public NodeList getResumeListForByteFile(String wordString, String fileName, String extention) {
        this.fileName = fileName+"."+extention;

        NodeList list = null;
        try {

            StringBuffer strXml = new StringBuffer();
            strXml.append("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
            strXml.append("xmlns:tem='http://tempuri.org/'>");
            strXml.append("<soapenv:Header/>");
            strXml.append("<soapenv:Body>");
            strXml.append("<tem:TransResumeByXmlStringForFile>");
            strXml.append("<tem:username>" + this.userName + "</tem:username>");
            strXml.append("<tem:pwd>" + this.password + "</tem:pwd>");
            strXml.append("<tem:content>" + wordString+ "</tem:content>");
            strXml.append("<tem:ext>" + "."+extention.toUpperCase()+"</tem:ext>");
            strXml.append("</tem:TransResumeByXmlStringForFile>");
            strXml.append("</soapenv:Body>");
            strXml.append("</soapenv:Envelope>");
            Reader reader = new StringReader(strXml.toString());
            Source source = new StreamSource(reader);
            SOAPMessage reqMsg1 = reqMsgFactory.createMessage();
            SOAPPart soapPart = reqMsg1.getSOAPPart();
            soapPart.setContent(source);
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.setPrefix("soapenv");
            envelope.setAttribute("xmlns:urn", "urn:DefaultNamespace");
            envelope.getHeader().setPrefix("soapenv");
            SOAPBody soapBody = envelope.getBody();
            soapBody.setPrefix("soapenv");
            URL cUrl = new URL(this.url);
            SOAPMessage respMsg = conn.call(reqMsg1, cUrl);

            list = respMsg.getSOAPBody().getChildNodes();

        } catch (Exception e) {
             e.printStackTrace();
             FlistLog +=fileName+";";
            //throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }

}
