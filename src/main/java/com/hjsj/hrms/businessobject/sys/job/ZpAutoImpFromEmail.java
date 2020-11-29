package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.ResumeImportEmailXmlBo;
import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hjsj.hrms.businessobject.hire.TransResumeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class ZpAutoImpFromEmail implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		Store store = null;
		Connection conn = null;
		try {


			RowSet rs = null;
			conn = AdminDb.getConnection();
			String url = "http://cv.edge-tech.com.cn/ResumeService.asmx";
			String nameSpace = "http://tempuri.org/";
			String methodName = "TransResume";
			String userName = ""; // 简历解析用户名
			String password = "";
			String ForeignJob = ""; // 简历解析关联的对外岗位指标
			String PostID = ""; // 简历的应聘岗位
			String ResumeName = "";
			ParameterXMLBo parameterXMLBo = new ParameterXMLBo(conn, "1");
			HashMap map = parameterXMLBo.getAttributeValues();
			int Num = 0; //简历总数

			ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(conn);
			//获取userview
			ContentDAO dao1 = new ContentDAO(conn);
			String password1 = "";
			rs = dao1.search("select Password from operuser where UserName='su'");
			String check = "0";
			if (rs.next()) {
				password1 = rs.getString("Password");
				password1 = password1 != null ? password1 : "";
				check = "1";
			}
			UserView uv = new UserView("su", password1, conn);
			uv.canLogin();

			// 获取黑名单库
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String blacklist_per = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST, "base");// 黑名单人员库
			String blacklist_field = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST, "field");// 黑名单人员指标
			String blacklist_value = ""; // 黑名单值

			if (map != null && map.get("resumeAnalysisMap") != null) {
				HashMap resumeAnalysisMap = (HashMap) map.get("resumeAnalysisMap");
				userName = (String) resumeAnalysisMap.get("resumeAnalysisName");
				password = (String) resumeAnalysisMap.get("resumeAnalysisPassword");
				ForeignJob = (String) resumeAnalysisMap.get("resumeAnalysisForeignJob");
			}

			TransResumeBo bo = new TransResumeBo(url, methodName, userName, password, conn);

			ArrayList resumeXmlList = resumeImportSchemeXmlBo.getSchemeList();
			ArrayList menuList = resumeImportSchemeXmlBo.getResumeSchemeXML(); // 获取所有指标的属性(包括xml和数据库)
			StringBuffer buf = new StringBuffer();
			buf.append("select * from t_sys_jobs  order by job_id");

			ContentDAO dao = new ContentDAO(conn);
			String[] Email = null;
			rs = dao.search(buf.toString());
			while (rs.next()) {
				String jobclass = rs.getString("jobclass");
				if (jobclass.indexOf(this.getClass().getName()) != -1) {
					String status = rs.getString("status");
					if ("com.hjsj.hrms.businessobject.sys.job.ZpAutoImpFromEmail".equals(jobclass)) {
						String job_param = rs.getString("job_param")==null?"":rs.getString("job_param");
						Email = job_param.split("；");
						
					}
					break;
				}
			}
			if(Email==null||Email.length==0){
				throw GeneralExceptionHandler.Handle(new Exception("请配置后台作业参数，格式如下：邮箱,密码,邮件接收服务器,端口号,yyyy-MM-dd HH:mm:ss;邮箱,密码,邮件接收服务器,端口号，yyyy-MM-dd HH:mm:ss"));
			}
			String contentError = "";//内容不对应
			HashMap confirmMap=new HashMap();
			confirmMap.put("blacklist_per",blacklist_per );// 黑名单人员库
			confirmMap.put("blacklist_field",blacklist_field );// 黑名单人员指标
			confirmMap.put("contentError", contentError);//内容不对应
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startDate="";
			Date date=new Date();
			System.out.println("自动导入简历开始--"+df.format(date));
			HashMap mailMap=new HashMap();
			for (int i = 0; i < Email.length; i++) {
				Properties props = new Properties();
				String[] emailParam = Email[i].split(",");
				String account = "";
				String psd = "";
				String MailServer = "";
				int port = 0;
				try{
					account = emailParam[0].trim();
					psd = emailParam[1].trim();
					if(mailMap.get(account)==null)//同名邮箱只解析一次
					{
						mailMap.put(account, psd);
					} else {
						continue;
					}
					MailServer = emailParam[2];
					port = Integer.parseInt(emailParam[3]);
					startDate=emailParam[4];
					date=df.parse(startDate);
					startDate=df.format(date);
					//Security = emailParam[4];
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new Exception("请配置后台作业参数，格式如下：邮箱,密码,邮件接收服务器,端口号,yyyy-MM-dd HH:mm:ss;邮箱,密码,邮件接收服务器,端口号，yyyy-MM-dd HH:mm:ss"));
				}
				

//				props.put("mail.smtp.host", "smtp.qq.com");
//				props.put("mail.smtp.auth", "false");

				Session session = Session.getDefaultInstance(props, null);
				URLName urln = new URLName("pop3", MailServer, port, null, account, psd);
				store = session.getStore(urln);
				try{
				    store.connect();
				}catch(Exception e){
				    e.printStackTrace();
				    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.role.zp.parameter.error")));
				}
				
				Folder folder = store.getFolder("INBOX");
				folder.open(Folder.READ_ONLY);

				Message message[] = folder.getMessages();
				//Num = message.length;

				for (int j = 0; j < message.length; j++) {

					Part part = message[j];
					// 获取邮件发送时间
					MimeMessage msg = (MimeMessage) part;
					Date sendDate = msg.getSentDate();
					String sendtime = "";
					if (sendDate != null) {
						sendtime = df.format(sendDate);
                    } else {
                        //当日期为空时，则从邮件头解析含有发送日期的参数获得发送日期   chenxg add 2015-01-04
                        //获取包含有发送人、发送ip、发送日期等信息的参数
                        String[] Received = msg.getHeader("Received");
                        sendtime = getsendtime(Received); // 设置发送时间最小,不导入
                    }

					// 获取邮箱地址
					String mailAddr = account;

					// 获取邮件标题
					String mailTitle="";
					if(msg.getSubject()!=null){
					    mailTitle = MimeUtility.decodeText(msg.getSubject());
					}
					if(mailTitle==null|| "".equals(mailTitle)) {
						mailTitle="空主题("+sendtime+")";
					}
					ResumeImportEmailXmlBo Emailbo = new ResumeImportEmailXmlBo(conn);
					boolean flag = Emailbo.isImportFlag(mailAddr, mailTitle, sendtime,startDate);
					confirmMap.put("sendtime",sendtime );
					confirmMap.put("name", "1");//文本解析姓名用1占位
					if(confirmMap.get("Num")!=null){
						Num=Integer.parseInt((String) confirmMap.get("Num"));
					}else{
						confirmMap.put("Num", Num+"");//解析简历数
					}
					if (flag) {
						Num+=1;
						confirmMap.put("Num", Num+"");//解析简历数
						confirmMap.put("ATTACHMENT", "false");//默认 邮件不带附件
						confirmMap.put("mailTitle",mailTitle);
						// 获取邮件内容
						StringBuffer bodytext = new StringBuffer("");
						if (part.isMimeType("text/plain")) {
							bodytext.append((String) part.getContent());
						} else if (part.isMimeType("text/html")) {
							bodytext.append((String) part.getContent());
						} else if (part.isMimeType("multipart/*")) {
							Multipart multipart = (Multipart) part.getContent();//part 每一封邮件，使用getContent();获取邮件内容，返回一个Object对象。
							int count = multipart.getCount();//获得当前邮件总共有几部分
							for (int k = 0; k < count; k++) {
								bo.getMailContent(multipart.getBodyPart(k),confirmMap,bo,Num, menuList, uv,mailTitle);
							}
						} else if (part.isMimeType("message/rfc822")) {
							    bo.getMailContent((Part) part.getContent(),confirmMap,bo,Num, menuList, uv,mailTitle);
						}
						
						Emailbo.updateEmailInfo(mailAddr, mailTitle, sendtime);//更新最近一封简历的导入时间
					}
				}
			}
			contentError=(String) confirmMap.get("contentError");
			if(confirmMap.get("Num")!=null){
				Num=Integer.parseInt((String) confirmMap.get("Num"));
			}

			String ferror=(String) confirmMap.get("ferror")==null?"":(String) confirmMap.get("ferror");
			bo.WriteImportDetail(uv,bo.getBlacklistLog(), bo.getPlistLog(), ferror,contentError, bo.getClistLog(),bo.getFlistLog(),"自动导入简历",Num);
			date=new Date();
			System.out.println("自动导入简历结束--"+df.format(date));

		} catch (Exception e) {
			e.printStackTrace();
			throw  new JobExecutionException(e);
		}finally{
			try {
				if(store!=null) {
					store.close();
				}
				if(conn!=null)//关闭数据库链接   zhaoxg  2014-4-18
				{
					conn.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 从解析的邮件头中获取邮件的发送日期
	 * @param Receiveds 包含有发送人、发送ip、发送日期等信息的参数
	 * @return string格式的日期，若没有日期，则返回0
	 */
    private String getsendtime(String[] Receiveds) {
        String DateTime = "";
        Date date = null;
        try {
            if (Receiveds == null || Receiveds.length < 1) {
				return "0"; // 设置发送时间最小,不导入
			}

            String Received = Receiveds[0];
            //Received的值目前已知的有如下两种：
            //1.Received: from unknown( HELO szxga02-in.huawei.com)([119.145.14.65])\r\n\tby mx2911-197.mail.sina.com.cn with SMTP\r\n\t20 Mar 2014 09:13:49 +0800 (CST)
            //2.Received: from 172.24.2.119 (EHLO szxeml214-edg.china.huawei.com) ([172.24.2.119])\r\n\tby szxrg02-dlp.huawei.com (MOS 4.3.7-GA FastPath queued)\r\n\twith ESMTP id BRH96537;\r\n\tThu, 20 Mar 2014 09:13:45 +0800 (CST)
            String[] receiveds = Received.split(";");
            if (receiveds.length == 2) {
                String datetime = receiveds[1];
                datetime = datetime.replaceAll("\r\n\t", "").trim();
                datetime = datetime.replaceAll(",", "");
                SimpleDateFormat sfStart = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss zzzz", Locale.ENGLISH);
                date = sfStart.parse(datetime);
            } else {
                String datetime = receiveds[0];
                datetime = datetime.substring(datetime.indexOf("SMTP") + 4);
                datetime = datetime.replaceAll("\r\n\t", "");
                SimpleDateFormat sfStart = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzzz", Locale.ENGLISH);
                date = sfStart.parse(datetime);
            }

            DateTime = DateUtils.FormatDate(date, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DateTime;
    }

}
