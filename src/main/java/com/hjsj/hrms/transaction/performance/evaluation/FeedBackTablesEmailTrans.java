package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.hire.HireOrderBo;
import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.ykcard.YkcardPdf;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * <p>Title:DistriPlanEmailTrans.java</p>
 * <p>Description:给考核对象发送考核反馈表 如果考核对象类别是团队则发送给团队负责人</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-03-30 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 */
public class FeedBackTablesEmailTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		String plan_id = (String) this.getFormHM().get("plan_id");
		String backTables = (String) this.getFormHM().get("backTables");
		ContentDAO dao = new ContentDAO(this.frameconn);
		PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
		RecordVo plan_vo = pb.getPerPlanVo(plan_id);
		String object_type = String.valueOf(plan_vo.getInt("object_type")); // 1部门 2：人员
		ArrayList emailReceivorList = new ArrayList();// 邮件接收人地址列表
		ArrayList emailReceivorList2 = new ArrayList();// 邮件接收人姓名列表
		ArrayList emailReceivorList3 = new ArrayList();// 考核对象
		String templateId = "";// 邮件模板
		String errorInfo = "";
		String appealTemplateId = "";//申诉模版
		String oper = (String) this.getFormHM().get("oper");
		try
		{
			if (oper != null && "test".equalsIgnoreCase(oper))
			{
				this.getFormHM().put("backTables", backTables);
				this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
				if (this.frowset.next())
				{
					String str_value = this.frowset.getString("str_value");
					if (str_value == null || (str_value != null && "".equals(str_value)))
					{
			
					} else
					{
						Document doc = PubFunc.generateDom(str_value);
						String xpath = "//Per_Parameters";
						XPath xpath_ = XPath.newInstance(xpath);
						Element ele = (Element) xpath_.selectSingleNode(doc);
						Element child;
						if (ele != null)
						{
							if("test".equalsIgnoreCase(oper)) {
								child = ele.getChild("FeedBack");
								if (child != null)
								{
									templateId = child.getAttributeValue("template");
								}
							}
						}
					}
				}
				if ("-1".equals(templateId))
				{
					this.getFormHM().put("resultFlag", "0");
					errorInfo = "没有指定反馈表邮件模板!请到\"参数设置\"-\"配置参数\"中设置。";
					this.getFormHM().put("errorInfo", SafeCode.encode(errorInfo));
					return;
				}
				RecordVo vo = ConstantParamter.getConstantVo("SS_EMAIL");
				if (vo==null || "#".equals(vo.getString("str_value")))
				{
					this.getFormHM().put("resultFlag", "0");
					errorInfo = "系统没有设置邮件指标,运行错误!";
					this.getFormHM().put("errorInfo", SafeCode.encode(errorInfo));
					return;
				}
				this.getFormHM().put("resultFlag", "1");
			} else if (oper != null && "sendBackTables".equalsIgnoreCase(oper))
			{

				String sql = "";

				// 考核对象类别为人员时给考核对象发邮件，考核对象类别为团队给团队负责人发邮件
				if ("2".equals(object_type))
					sql = "select object_id,a0101 from  per_object where plan_id=" + plan_id;
				else
					sql = "select mainbody_id,a0101,object_id from  per_mainbody where plan_id=" + plan_id + " and body_id=-1";

				String whl = pb.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
				sql+=whl;

				this.frowset = dao.search(sql);
				while (this.frowset.next())
				{
					emailReceivorList.add("Usr" + this.frowset.getString(1));
					emailReceivorList2.add(this.frowset.getString(2));
					emailReceivorList3.add(this.frowset.getString("object_id"));
				}
					
				this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
				if (this.frowset.next())
				{
					String str_value = this.frowset.getString("str_value");
					if (str_value == null || (str_value != null && "".equals(str_value)))
					{

					} else
					{
						Document doc = PubFunc.generateDom(str_value);
						String xpath = "//Per_Parameters";
						XPath xpath_ = XPath.newInstance(xpath);
						Element ele = (Element) xpath_.selectSingleNode(doc);
						Element child;
						if (ele != null)
						{
							child = ele.getChild("FeedBack");
							if (child != null)
							{
								templateId = child.getAttributeValue("template");
							}
							
							child = ele.getChild("Appeal");
							if (child != null)
							{
								appealTemplateId = child.getAttributeValue("template");
							}
						}
					}
				}
				if ("-1".equals(templateId))
				{
					this.getFormHM().put("resultFlag", "0");
					// throw GeneralExceptionHandler.Handle(new Exception("没有指定反馈表邮件模板!请到\"参数设置\"-\"配置参数\"中设置。"));
					errorInfo = "没有指定反馈表邮件模板!请到\"参数设置\"-\"配置参数\"中设置。";
					this.getFormHM().put("errorInfo", errorInfo);
					return;
				}

				EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
				/** 得邮件指标 */
				String emailfield = bo.getEmailField(templateId);
				/** 得模板标题 */
				String subject = bo.getEmailTemplateSubject(templateId);
				/** 得包含邮件指标的主集，以便取得实际的邮件地址 */
				String emailfieldset = this.getEmailFieldSetId(emailfield);
				if(emailfieldset==null||emailfieldset.length()<=0)
				{
					this.getFormHM().put("resultFlag", "0");
					errorInfo = "模板邮件指标定义错误！";
					this.getFormHM().put("errorInfo", SafeCode.encode(errorInfo));
					return;
				}
				/** 得模板项目列表 */
				ArrayList list = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);

				/** 取邮件模板内容 */
				String contentTemp = bo.getEmailContent(Integer.parseInt(templateId));

				RecordVo vo = ConstantParamter.getConstantVo("SS_EMAIL");
				if ("#".equals(vo.getString("str_value")))
				{
					this.getFormHM().put("resultFlag", "0");
					// throw GeneralExceptionHandler.Handle(new Exception("系统没有设置邮件指标,运行错误!"));
					errorInfo = "系统没有设置邮件指标,运行错误!";
					this.getFormHM().put("errorInfo", SafeCode.encode(errorInfo));
					return;
				}

				HireOrderBo hirebo = new HireOrderBo();
				String fromAddr = hirebo.getFromAddr();

				EMailBo emailbo=null;
				try
				{
					emailbo = new EMailBo(this.frameconn, true, "Usr");
				}catch(Exception e)
				{
					this.getFormHM().put("resultFlag", "2");
					return;
				}
				
				ResultSetMetaData data = getPlanResult(plan_id);//获取到表结构，这样就不用一遍一遍的循环多次获取
				String planName = getPlanName(dao,plan_id);//获取计划名称
				for (int i = 0; i < emailReceivorList.size(); i++)
				{
					String name = (String)emailReceivorList2.get(i);
					String prea0100 = (String) emailReceivorList.get(i);
					String object_id=(String)emailReceivorList3.get(i);
					
					LazyDynaBean abean = getUserNamePassword(prea0100);//获得用户名和密码
					String username=(String)abean.get("username");
					String password=(String)abean.get("password");
					//String emailContent = bo.getFactContent(contentTemp, prea0100, list, this.userView);
					String emailContent = this.getKHFactContent(planName, contentTemp, prea0100, list, bo, plan_id, appealTemplateId, username, password, data, object_id);//因为要做特殊处理，重写了这个方法，这样，不用两个特殊处理了
					String toAddr = this.getEmailValue(emailfield, emailfieldset, prea0100.substring(3), templateId, dao);
					boolean b=false;
					if(toAddr!=null)
					{
						Pattern p = Pattern.compile("\\w+@(\\w+\\.)+[a-z]{2,3}"); 
						Matcher m = p.matcher(toAddr); 
						b = m.matches(); 
					}
					//#自动登录连接#
					String url="";
					emailContent=emailContent.replaceAll("#"+ResourceFactory.getProperty("label.gz.autologonaddress")+"#",url);
					if(!b)
					{
						errorInfo +="\r\n"+name+"的邮件地址设置错误,邮件发送失败！";
						continue;
					}
					
					if(emailbo!=null)
					{										
						 
				//		if (toAddr == null || (toAddr != null && (toAddr.trim().equals("") || toAddr.trim().equals("无")))) 
				//			continue;
	
						/** 在此添加生成pdf附件的代码 */
						ArrayList filelist = this.getFileList(backTables, plan_id, object_id);// 附件列表
	
						ArrayList bodylist = new ArrayList();// 内容列表
						bodylist.add(emailContent);
	
						ArrayList toAddress = new ArrayList();// 接收人列表
						toAddress.add(toAddr);
						
						String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
						if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
							WeiXinBo.sendMsgToPerson("Usr", object_id, subject, emailContent, "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
						}
						String ddcorpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");  
						if(ddcorpid!=null&&ddcorpid.length()>0){//推送钉钉公众号  xus add 2016-6-2
							DTalkBo.sendMessage(object_id,"Usr",  subject, emailContent, "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
						}
						
						emailbo.sendEqualEmail(subject, bodylist, filelist, fromAddr, toAddress, null);// 为了发送多个附件 用这个方法
					}
				}
				
				if(errorInfo!=null&&errorInfo.trim().length()>0)
				{
					this.getFormHM().put("resultFlag", "0");
					this.getFormHM().put("errorInfo", SafeCode.encode(errorInfo));
					return;
				}
			}
			this.getFormHM().put("resultFlag", "1");
			
		} catch (Exception e)
		{
			this.getFormHM().put("resultFlag", "0");
			e.printStackTrace();			
			throw GeneralExceptionHandler.Handle(e);
		}
		

	}
	
	public String getEmailValue(String emailField, String emailFieldSet, String a0100, String templateId, ContentDAO dao) throws SQLException
	{

		String emailValue = "";
		StringBuffer sql = new StringBuffer();
		if ("a01".equalsIgnoreCase(emailFieldSet))
		{
			sql.append("select a." + emailField + " address from Usr" + emailFieldSet + " a where a.a0100='" + a0100 + "'");
		} else
		{
			sql.append("select a." + emailField + " address from Usr" + emailFieldSet + " a where a.a0100=" + a0100 + " and i9999=(select max(i9999) i9999 from Usr" + emailFieldSet
					+ " b where b.a0100='" + a0100 + "')");
		}
		RowSet rs = dao.search(sql.toString());
		if (rs.next())
			emailValue = rs.getString(1);
		return emailValue;
	}

	public ArrayList getFileList(String backTables, String plan_id, String nid) throws GeneralException
	{
		ArrayList list = new ArrayList();
		CardTagParamView cardparam = new CardTagParamView();

		String cyear = Integer.toString(cardparam.getCyear());
		String querytype = Integer.toString(cardparam.getQueryflagtype());
		String cmonth = Integer.toString(cardparam.getCmonth());
		String userpriv = "noinfo";
		String istype = "1";
		String season = Integer.toString(cardparam.getSeason());
		String ctimes = Integer.toString(cardparam.getCtimes());
		String cdatestart = cardparam.getCdatestart();
		String cdateend = cardparam.getCdateend();
		String infokind = "5";
		String userbase = "USR";
		String fieldpurv = null;

		YkcardPdf ykcardPdf = new YkcardPdf(this.getFrameconn());
		ykcardPdf.setPlan_id(plan_id);
		
		String[] cardids = backTables.split(",");
		try
		{
			for (int i = 0; i < cardids.length; i++)
			{
				String cardid = cardids[i];
				if (cardid.trim().length() == 0)
					continue;
				String url = ykcardPdf.executePdf(Integer.parseInt(cardid), nid, userbase, this.userView, cyear, querytype, cmonth, userpriv, istype, season, ctimes, cdatestart, cdateend, infokind,
						fieldpurv,"");
				String filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + url;
				list.add(filename);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	public String getEmailFieldSetId(String itemid)
	{
		String fieldsetid="";
		try
		{
			String sql = "select fieldsetid from fielditem where itemid='"+itemid+"'";
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				fieldsetid=rs.getString("fieldsetid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return fieldsetid;
	}
	
	private String getKHFactContent(String planName,String content,String prea0100,ArrayList fieldList,EmailTemplateBo bo, String plan_id, String appealTemplateId, String name, String password, ResultSetMetaData data, String object_id) {
		String fact_content=content;
		RowSet rs = null;
		HashMap map = new HashMap();
		try
		{
			PerEvaluationBo perBo=new PerEvaluationBo(this.getFrameconn(),plan_id,"",this.userView);
			Hashtable ht=perBo.getPlanParamSet();
			int KeepDecimal = Integer.parseInt(ht.get("KeepDecimal").toString());//获取保存的小数位
			
			String pre=prea0100.substring(0,3);
			String a0100=prea0100.substring(3);
			StringBuffer buf = new StringBuffer();
			StringBuffer table_name=new StringBuffer();
			HashSet name_set = new HashSet();
			StringBuffer where_sql=new StringBuffer();
			StringBuffer where_sql2= new StringBuffer();
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			map = getNeedPerResultValue(plan_id,object_id,dao,KeepDecimal);
			for(int i=0;i<fieldList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)fieldList.get(i);
				String id=(String)bean.get("id");
				String fieldtitle=(String)bean.get("fieldtitle");
				String fieldtype=((String)bean.get("fieldtype")).trim();
				String fieldcontent=(String)bean.get("fieldcontent");
				String fieldid=(String)bean.get("fieldid");
				String dateformat=(String)bean.get("dateformat");
				String fieldlen=(String)bean.get("fieldlen");
				String ndec=(String)bean.get("ndec");
				String codeset=(String)(bean.get("codeset")==null?"":bean.get("codeset"));
				String nflag=(String)bean.get("nflag");
				String fieldset=(String)bean.get("fieldset");
				String replace="";//要被替换的内容
				String factcontent="";
				String setid="";
				if("0".equals(nflag))
				{
					replace="\\$"+fieldid+":"+fieldtitle.trim()+"\\$";
				}
				if("1".equals(nflag))
				{
					replace="\\#"+fieldid+":"+fieldtitle.trim()+"\\#";
				}
				/**指标和公式项目处理不同*/
				if("0".equals(nflag))//指标
				{
					String fieldsetid="";
					if("A0101".equalsIgnoreCase(fieldcontent.trim())|| "B0110".equalsIgnoreCase(fieldcontent.trim())|| "E0122".equalsIgnoreCase(fieldcontent.trim())|| "e01a1".equalsIgnoreCase(fieldcontent.trim()))
						fieldsetid="a01";
					else
			    		fieldsetid=bo.getFieldSetId(fieldcontent.trim());
					if(fieldsetid==null||fieldsetid.length()<=0) {//可能指标不再fielditem里面，在计划表中，这里判断是否在计划表中
						
						replace="\\$sys:"+fieldtitle.trim()+"\\$";
						if(map.size() > 0) {
							if(fieldtitle.indexOf("考核申述") != -1) {
								if(this.userView.getVersion() >= 70)//70锁以上版本
									factcontent = "<a href='"+ this.userView.getServerurl() +"/module/utils/jsp.do?br_query=link&param=" + SafeCode.encode("/module/template/templatemain/templatemain.html?encryptParam="+PubFunc.encrypt("b_query=link&approve_flag=1&module_id=9&return_flag=14&tab_id=" + appealTemplateId + "&view_type=card")) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(name+","+password))+"'>考核申诉</a>";
								else 
									factcontent = "<a href='"+ this.userView.getServerurl() +"/module/utils/jsp.do?br_query=link&param=" + SafeCode.encode("/template/myapply/busiTemplate.do?encryptParam="+PubFunc.encrypt("b_query=link&ins_id=0&returnflag=12&tabid=" + appealTemplateId)) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(name+","+password))+"'>考核申诉</a>";
							}else if(fieldtitle.indexOf("考核计划名称") != -1){
								factcontent = planName;
							}else {
								for(int j = 1; j <= data.getColumnCount(); j++)
								{
									String columnName=data.getColumnName(j).toLowerCase();
									String columnType = data.getColumnTypeName(j);
									if(columnName.equalsIgnoreCase(fieldcontent))
									{
										if("confirmflag".equalsIgnoreCase(fieldcontent)) {
											//factcontent = "<a href='"+ this.userView.getServerurl() +"/servlet/performance/confirmResult?encryptParam="+SafeCode.encode("plan_id=" + plan_id + "&object_id=" + a0100 + "&nbase=")+"'>考核结果确认</a>";
											factcontent = "<a href='"+ this.userView.getServerurl() +"/module/utils/jsp.do?br_query=link&param=" + SafeCode.encode("/servlet/performance/confirmResult?encryptParam="+PubFunc.encrypt("plan_id=" + plan_id + "&object_id=" + object_id + "&nbase=")) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(name+","+password))+"'>考核结果确认</a>";
										}else if("body_id".equalsIgnoreCase(fieldcontent)) {
											int bodyid = map.get(fieldcontent) == null? -100 : (Integer)map.get(fieldcontent);//没有bodyid，这时候设置-100表示没有
											rs = dao.search("select name from per_mainbodyset where body_id = " + bodyid);
											if(rs.next()) {
												factcontent = rs.getString("name");
											}
										}
										else 
											factcontent = map.get(fieldcontent) == null? "" : (String)map.get(fieldcontent);
										break;
									}
								}
							}
						}else
							factcontent = "";
						//无论在哪这里就不用走下面方法了
						fact_content=fact_content.replaceAll(replace,factcontent);
						continue;
					}
					buf.append("select ");
					buf.append(fieldcontent);
					buf.append(" from ");
					buf.append(pre+fieldsetid);
					buf.append(" where ");
					
						buf.append(pre+fieldsetid+".a0100='");
						buf.append(a0100+"'");
						if("a01".equalsIgnoreCase(fieldsetid))
						{
							
						}		
						else
						{
							buf.append(" and ");
						    buf.append(pre+fieldsetid+".i9999=(select max(i9999) from ");
						    buf.append(pre+fieldsetid);
						    buf.append(" where ");
						    buf.append(pre+fieldsetid+".a0100='");
						    buf.append(a0100+"')");
						}						
						rs=dao.search(buf.toString());
						while(rs.next())
						{			
							//  JinChunhai 2011.05.19  orcle库遇到日期型的字段后台会报错
							if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))  // 代码型
							{
								factcontent=rs.getString(fieldcontent.trim());
								
							}else if("N".equalsIgnoreCase(fieldtype.trim()))  // 数值型按格式显示
							{
								factcontent=rs.getString(fieldcontent.trim());
								
							}else if("D".equalsIgnoreCase(fieldtype.trim()))  // 日期型按格式显示
							{
								java.sql.Date dd=rs.getDate(fieldcontent.trim());
								if(dd!=null){
									SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
									factcontent=format.format(dd);
								}
								
							}else 
							{
								factcontent=rs.getString(fieldcontent.trim());
							}
							
						}
						
						if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))//代码型
						{
							factcontent=AdminCode.getCodeName(codeset,factcontent);
						}
						
						/**日期型按格式显示*/
						if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat)))
							factcontent=bo.getYMDFormat(Integer.parseInt(dateformat),factcontent);
						/**数值型按格式显示*/
						if("N".equalsIgnoreCase(fieldtype.trim()))
							factcontent=bo.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
						if(factcontent==null||factcontent.trim().length()==0)
			    		{
			    			if("N".equalsIgnoreCase(fieldtype.trim()))
			    				factcontent="0.0";
			    			else
			    				factcontent=" ";
			    		}
						fact_content=fact_content.replaceAll(replace,factcontent);
					    buf.setLength(0);
						table_name.setLength(0);
						where_sql.setLength(0);
						where_sql2.setLength(0);
					
				}
				if("1".equals(nflag))//公式
				{
					ArrayList list = new ArrayList();
					String zh_sql=bo.getSql(fieldcontent,fieldtype,this.userView,list);
					if(zh_sql==null||zh_sql.length()<=0)
						continue;
					if("D".equalsIgnoreCase(fieldtype.trim()))//日期型是否直接查
					{
					    setid=bo.getFieldSetId(zh_sql.trim());
						buf.append("select ");
						buf.append(zh_sql);
						buf.append(" from ");
						if(setid!=null&&!"".equals(setid))
							buf.append(pre+setid);
						else
							buf.append(" usra01 ");
						buf.append(" where a0100='");
						buf.append(a0100+"'");
					}
					else
					{
				    	if(list.size()>0)
			    		{
				    		buf.append("select ");
				    		buf.append(zh_sql);
				    		buf.append(" from ");
			    			for(int j=0;j<list.size();j++)
			    			{
			    				String fieldsetid = (String)list.get(j);
					    		String tableName=pre+fieldsetid;
					    		name_set.add(tableName);
					    		if(j!=0)
						    		where_sql2.append(" and ");
					     		where_sql2.append(tableName+".a0100='");
					      		where_sql2.append(a0100+"'");
					    		if("a01".equalsIgnoreCase(fieldsetid))
					    		{
					    		}
					    		else
					    		{
					    			if(j!=0)
					    				where_sql.append(" and ");
				    	    		where_sql.append(tableName);
					        		where_sql.append(".i9999=(");
				         			where_sql.append("select max(i9999) from ");
					        		where_sql.append(tableName+" "+tableName);
					        		where_sql.append(" where ");
					        		where_sql.append(tableName+".a0100='");
					        		where_sql.append(a0100+"') ");
					    		}
					    	}
				    		buf.append(name_set.toString().substring(1,name_set.toString().length()-1));
				    		buf.append(" where ");
				    		buf.append(where_sql);
					    	if(where_sql.length()>0)
					    	      buf.append(" and ");
			    	    	buf.append(where_sql2);
			    		}
			    		else
			    		{
			    			buf.append("select ");
				    		buf.append(zh_sql);
				    		buf.append(" from ");
				    		buf.append("usra01 where ");
				    		buf.append("a0100='");
				    		buf.append(a0100+"'");
				    	}	
			    	}					
					rs=dao.search(buf.toString());
		    		while(rs.next())
		    		{
		    			if("D".equalsIgnoreCase(fieldtype))
		    			{
		    				{
		    					factcontent = rs.getString(1);
		    				}
		    			}
		    			else
	    	    			factcontent=rs.getString(1);
		    		}
		    		
		    		/**日期型按格式显示*/
					if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat)))
						factcontent=bo.getYMDFormat(Integer.parseInt(dateformat),factcontent);
					/**数值型按格式显示*/
					if("N".equalsIgnoreCase(fieldtype.trim()))
						factcontent=bo.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
					if(factcontent==null||factcontent.trim().length()==0)
		    		{
		    			if("N".equalsIgnoreCase(fieldtype.trim()))
		    				factcontent="0.0";
		    			else
		    				factcontent=" ";
		    		}
				    fact_content=fact_content.replaceAll(replace,factcontent);
			    	buf.setLength(0);
			    	table_name.setLength(0);
			    	name_set.clear();
		    		where_sql.setLength(0);
		    		where_sql2.setLength(0);
				}
			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return fact_content;
	}
	
	 /**  
     *  获得用户名和密码
     */
    private LazyDynaBean getUserNamePassword(String value)
	{
		if(value==null||value.length()<=0)
		{
			return null;
		}
		String nbase=value.substring(0,3);
		String a0100=value.substring(3);
		AttestationUtils utils=new AttestationUtils();
		LazyDynaBean fieldbean=utils.getUserNamePassField();
		String username_field=(String)fieldbean.get("name");
	    String password_field=(String)fieldbean.get("pass");
	    
	    StringBuffer sql=new StringBuffer();
	    sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
	    sql.append(" where a0100='"+a0100+"'");
	    List rs=ExecuteSQL.executeMyQuery(sql.toString());
	    
	    LazyDynaBean rec=null;
	    if(rs!=null&&rs.size()>0)
	    {
	    	rec=(LazyDynaBean)rs.get(0);	    	
	    }
	    return rec;
	}      
    
    /**
     * 获取到表结构，这样就不用一遍一遍的循环多次获取
     * @param plan_id
     * @return
     */
    private ResultSetMetaData getPlanResult(String plan_id) {
    	ResultSetMetaData data= null;
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	try {
    		String sql = "select * from per_result_"+plan_id+" where 1=2";
			this.frowset = dao.search(sql);
			data = this.frowset.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
    }
    
    /**
     * 获取所有需要的per_result_XX表的值
     * 这样写就不用再循环指标的时候多次去查询数据库
     * 这里获取的指标是绩效通知模板中考核结果表的指标（对于单位名称，部门名称等，直接取数据字典中的，不是自己新增的，新增的$sys:XXXX$,是以sys形式）
     * @return
     */
    private HashMap getNeedPerResultValue(String plan_id,String A0100,ContentDAO dao,int KeepDecimal) {
    	HashMap map = new HashMap();
    	RowSet rs = null;
		try {
			//需要查询的字段
			String columns = "body_id,original_score,score,org_ordering,ordering,exS_GrpAvg,exS_GrpMin,exS_GrpMax,exX_object,resultdesc,confirmflag";
			String sqlOther = "select " + columns + " from per_result_" + plan_id + " where object_id = ?";//先查出来，不用每个指标多次查询
			ArrayList list = new ArrayList();
			list.add(A0100);
			rs=dao.search(sqlOther,list);
			if(rs.next()) {
				map.put("body_id", rs.getInt("body_id"));//对象类别
				map.put("original_score", PubFunc.round(rs.getString("original_score"),KeepDecimal));//计算总分
				map.put("score", PubFunc.round(rs.getString("score"),KeepDecimal));//总分
				map.put("org_ordering", rs.getString("org_ordering"));//部门排名
				map.put("ordering", rs.getString("ordering"));//组内排名
				map.put("exS_GrpAvg", PubFunc.round(rs.getString("exS_GrpAvg"),KeepDecimal));//组内平均分
				map.put("exS_GrpMin", PubFunc.round(rs.getString("exS_GrpMin"),KeepDecimal));//组内最低分
				map.put("exS_GrpMax", PubFunc.round(rs.getString("exS_GrpMax"),KeepDecimal));//组内最高分
				map.put("exX_object", PubFunc.round(rs.getString("exX_object"),KeepDecimal));//等级系数
				map.put("resultdesc", rs.getString("resultdesc"));//等级
				map.put("confirmflag", rs.getInt("confirmflag"));//考核结果确认
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
    	
    	return map;
    }
    
    /**
     * 获取计划名称/先查出来，不用每个发送人多次查询
     * @param plan_id
     * @return
     */
    private String getPlanName(ContentDAO dao,String plan_id) {
    	String planName = "";
    	RowSet rs = null;
    	ArrayList list = new ArrayList();
    	try {
    		String sql = "select name from per_plan where plan_id = ?";
			list.add(plan_id);
			rs=dao.search(sql,list);
			if(rs.next()) {
				planName = rs.getString("name");
			}
    	}catch (Exception e) {
    		e.printStackTrace();
    	}finally {
    		PubFunc.closeResource(rs);
    	}
    	return planName;
    }
}
