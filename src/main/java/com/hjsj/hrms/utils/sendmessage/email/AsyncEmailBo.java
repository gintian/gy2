package com.hjsj.hrms.utils.sendmessage.email;

import com.hjsj.hrms.businessobject.sys.MyAuthenticator;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.security.Security;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:AsyncEmailBo.java</p>
 * <p>Description:邮件业务对象类</p>
 * <p>Company:hjsj</p>
 * @author ZhangHua
 * @date 11:25 2018/11/16
 * @version v75
 *
 */
public class AsyncEmailBo {

    public AsyncEmailBo(Connection frameconn, UserView userView) {
        this.frameconn = frameconn;
        this.userView = userView;
        this.props = this.getStmpOptions();
        this.template = this.getDefaultTemplate();
    }

    /**
     * 更新发送状态
     *
     * @param frameconn
     * @param userView
     * @param RetIF     更新发送状态接口实现方法，此接口中完成数据库状态更新。
     */
    public AsyncEmailBo(Connection frameconn, UserView userView, IAsyncEmailIsSuccessIF RetIF) {
        this.frameconn = frameconn;
        this.userView = userView;
        this.props = this.getStmpOptions();
        this.template = this.getDefaultTemplate();
        this.RetIF = RetIF;
    }

    private Connection frameconn;
    private UserView userView;

    /**
     * 邮箱服务器地址,端口和认证参数;发件人用户名,密码和地址
     */
    private Properties props = null;
    /**
     * 邮件模板,可在程序中设定
     */
    private String template = null;
    private IAsyncEmailIsSuccessIF RetIF = null;

    /** ################################################################################# */

    /**
     * 邮件发送器,用线程来实现
     */
    private class TransmitterThread implements Runnable {
        //		private Message msg;
        private List msgs;

//		public TransmitterThread(Message msg) {
//			this.msg = msg;
//		}

        public TransmitterThread(List msgs) {
            this.msgs = msgs;
        }

        public void run() {
            Category log = Category.getInstance(AsyncEmailBo.class.getName());
//			if (msg != null) {
//				try {
//					address=msg.getRecipients(Message.RecipientType.TO);
//					name=address[0].toString();
//					Transport.send(msg);
//					if(RetIF!=null){
//						RetIF.sendEmailIsSuccess("1");
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					if(RetIF!=null){
//						RetIF.sendEmailIsSuccess("2");
//					}
//				}
//			} else
            if (msgs != null) {
                for (int i = 0, len = msgs.size(); i < len; i++) {
                    Message msg = (Message) ((LazyDynaBean) msgs.get(i)).get("msg");
                    try {
                        Transport.send(msg);
                        if (RetIF != null)
                            RetIF.sendEmailIsSuccess((LazyDynaBean) ((LazyDynaBean) msgs.get(i)).get("emails"), "");

                    } catch (Exception e) {
                        //xus 邮件地址没有配置时 不抛异常，只记录下日志信息
                        log.error(e.getMessage());
//						e.printStackTrace();
                        if (RetIF != null)
                            RetIF.sendEmailIsSuccess((LazyDynaBean) ((LazyDynaBean) msgs.get(i)).get("emails"), e.getMessage());

                    }
                }
            }
        }
    }

    /**
     * 发送单个邮件。最终发送的是模板字符串(被替换过参数之后的字符串)，如果需要自定义模板，
     * 请执行setTemplate方法,将邮件完整内容(不包含附件)以字符串的形式替换掉默认模板
     *
     * @param email <p>LazyDynaBean(objectId|toAddr,subject,bodyText,href,hrefDesc),属性区分大小写</p>
     *              <ul>
     *              <li>objectId|toAddr 接收方的邮件地址，如果传递objectId参数，则根据objectId查找对应的邮件地址</li>
     *              <li>subject 邮件标题</li>
     *              <li>bodyText 邮件正文，不包含查看链接(如果有的话)</li>
     *              <li>href 查看链接，传入URL即可</li>
     *              <li>hrefDesc 查看链接的描述，默认是"查 看"</li>
     *              </ul>
     */
    public void send(LazyDynaBean email) {
//		try {
//			Message msg = getMessage(email);
//			if (msg != null) {
//				new Thread(new TransmitterThread(msg)).start();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        //全部使用list的方法传值 2016.08.05 zhanghua
        List ls = new ArrayList();
        ls.add(email);
        send(ls);
    }

    /**
     * 批量发送邮件。最终发送的是模板字符串(被替换过参数之后的字符串)，如果需要自定义模板，
     * 请执行setTemplate方法,将邮件完整内容(不包含附件)以字符串的形式替换掉默认模板.
     * <p>只开启一次会话,将所有的邮件依次发出,异常不会终止邮件的发送</p>
     *
     * @param emails <p>LazyDynaBean(objectId|toAddr,subject,bodyText,href,hrefDesc),属性区分大小写</p>
     *               <ul>
     *               <li>objectId|toAddr 接收方的邮件地址，如果传递objectId参数，则根据objectId查找对应的邮件地址</li>
     *               <li>subject 邮件标题</li>
     *               <li>bodyText 邮件正文，不包含查看链接(如果有的话)</li>
     *               <li>href 查看链接，传入URL即可</li>
     *               <li>hrefDesc 查看链接的描述，默认是"查 看"</li>
     *               <li> attach，邮件的单个附件,这里指的是绝对路径和名字</li>
     *               <li> attachList，邮件的多个附件,里面存放的是各个文件的绝对路径和名字</li>
     *               <li>returnAddress 邮件恢复地址</li>
     *               </ul>
     */
    public void send(List emails) {
        try {
            new Thread(new TransmitterThread(getMessages(emails))).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量发送邮件。最终发送的是模板字符串(被替换过参数之后的字符串)，如果需要自定义模板，
     * 请执行setTemplate方法,将邮件完整内容(不包含附件)以字符串的形式替换掉默认模板.
     * <p>只开启一次会话,将所有的邮件依次发出,异常不会终止邮件的发送</p>
     *
     * @param emails <p>LazyDynaBean(objectId|toAddr,subject,bodyText,href,hrefDesc),属性区分大小写</p>
     *               <ul>
     *               <li>objectId|toAddr 接收方的邮件地址，如果传递objectId参数，则根据objectId查找对应的邮件地址</li>
     *               <li>subject 邮件标题</li>
     *               <li>bodyText 邮件正文，不包含查看链接(如果有的话)</li>
     *               <li>href 查看链接，传入URL即可</li>
     *               <li>hrefDesc 查看链接的描述，默认是"查 看"</li>
     *               <li> attach，邮件的单个附件,这里指的是绝对路径和名字</li>
     *               <li> attachList，邮件的多个附件,里面存放的是各个文件的绝对路径和名字</li>
     *               </ul>
     * @throws MessagingException
     * @throws AddressException
     */
    public void sendSync(LazyDynaBean email) throws AddressException, MessagingException {
        Message mess = getMessage(email);
        Transport.send(mess);
    }

    /**
     * 获取最终的Message对象
     */
    private Message getMessage(LazyDynaBean email) throws AddressException, MessagingException {
        String subject = (String) email.get("subject"); // 邮件主题
        String bodyText = ""; // 正文
        if ("java.lang.StringBuffer".equals(email.get("bodyText").getClass().getName())) {
            bodyText = email.get("bodyText").toString(); // 正文
        } else {
            bodyText = (String) email.get("bodyText"); // 正文
        }
        String returnAddress = (String) email.get("returnAddress");//邮件回复地址
        String href = (String) email.get("href"); // 链接
        String hrefDesc = (String) email.get("hrefDesc");
        String bodySubject = (String) email.get("bodySubject");
        if (bodySubject == null || "".equals(bodySubject)) {
            bodySubject = subject;
        }

        subject = nvl(subject, "");
        bodyText = nvl(bodyText, "");
        href = nvl(href, "");
        hrefDesc = nvl(hrefDesc, "查 看");

        String display = "".equals(href) || "".equals(hrefDesc) ? "none" : "inline-block";
        String sendDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try {
            Message msg = new MimeMessage(getSession());
            if (props.getProperty("from") == null || "".equals(props.getProperty("from"))) {
                return null;
            }
            /** from */
            msg.setFrom(new InternetAddress(props.getProperty("from")));

            /** reply to 答复人*/
            if (returnAddress != null && returnAddress.length() > 0) {
                msg.setReplyTo(InternetAddress.parse(returnAddress));
            }

            /** 主题 */
            msg.setSubject(subject);

            /** 邮件消息发送的时间 */
            msg.setSentDate(new Date());

            /** to */
            String toAddr = "";
            if (email.get("toAddr") != null) {//先判断有无邮箱 wangrd 2015-05-11
                toAddr += email.get("toAddr");
            } else if (email.get("objectId") != null) {
                toAddr += getEmailAddress((String) email.get("objectId"));
            }
            if (toAddr == null || toAddr.length() < 1) {//没有邮箱不发送 wangrd
                return null;
            }
            if (toAddr.indexOf("@") == -1) {//不符合邮箱格式
                return null;
            }

            if (!isMail(toAddr))
                return null;

            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));

            /** 正文 */
            Multipart multi = new MimeMultipart();
            BodyPart body = new MimeBodyPart();
            //如果存在table标签，则使用不限制最大宽度的模板
            Pattern pattern= Pattern.compile("<[ ]*TABLE");
            Matcher matcher=pattern.matcher(bodyText.toUpperCase());

            if(matcher.find()){
                this.setTemplate(this.getAutoWidthTemplate());
            }
            String content = template.replace("#subject#", bodySubject)
                    .replace("#bodyText#", bodyText)
                    .replace("#href#", href)
                    .replace("#hrefDesc#", hrefDesc)
                    .replace("#display#", display)
                    .replace("#sendDate#", sendDate);
            //xus  17/9/26邮件编码格式错乱
            body.setContent(content, "text/html; charset=utf-8");
            multi.addBodyPart(body);

            ArrayList fileList = new ArrayList();
            String attach = (String) email.get("attach");
            if (attach != null)
                fileList.add(attach);
            /**添加发送邮件附件的功能**/
            ArrayList emailList = (ArrayList) email.get("attachList");
            if (emailList != null)
                fileList.addAll(emailList);

            if (!(fileList == null || fileList.size() == 0)) {
                for (int j = 0; j < fileList.size(); j++) {
                    Object o = fileList.get(j);
                    String filename = "";
                    String filepath = "";
                    boolean isBean = o instanceof LazyDynaBean;
                    if (isBean) {
                        LazyDynaBean bean = (LazyDynaBean) fileList.get(j);
                        filename = (String) bean.get("filename");//文件名
                        filepath = (String) bean.get("filepath");//文件路径
                    } else {
                        filepath = (String) fileList.get(j);
                    }

                    if (filepath == null || filepath.trim().length() <= 0) {
                        continue;
                    }


                    FileDataSource fds = new FileDataSource(filepath);
                    BodyPart bp2 = new MimeBodyPart();
                    if (isBean) {
                        bp2.setFileName(MimeUtility.encodeText(filename));
                    } else {
                        bp2.setFileName(MimeUtility.encodeText(fds.getName()));
                    }

                    bp2.setText(MimeUtility.encodeText(fds.getName()));
                    bp2.setHeader("Content-ID", "<" + MimeUtility.encodeText(fds.getName()) + ">");
                    bp2.setDataHandler(new DataHandler(fds));
                    //xiexd 2015.12.22判断当前附件是否存在，不存在则不必添加到Message中，否则发送邮件是会抛出java.lang.NullPointerException
                    File file = new File(fds.getFile().getPath());
                    if (file.isFile()) {
                        multi.addBodyPart(bp2);
                    }
                }
            }
            msg.setContent(multi);
            return msg;
        } catch (AddressException e) {
            e.printStackTrace();
            throw e;
        } catch (MessagingException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 批量发送时,获取最终的Message对象集合
     */
    private List getMessages(List emails) {
        List msgs = new ArrayList();
        LazyDynaBean emailBean;
        for (int i = 0, len = emails.size(); i < len; i++) {
            emailBean = new LazyDynaBean();
            Message msg = null;
            try {
                msg = getMessage((LazyDynaBean) emails.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (msg != null) {
                emailBean.set("emails", (LazyDynaBean) emails.get(i));
                emailBean.set("msg", msg);
                msgs.add(emailBean);
            } else {
                if (RetIF != null)
                    RetIF.sendEmailIsSuccess((LazyDynaBean) emails.get(i), "邮件格式或邮件服务器设置有误！");
            }
        }

        return msgs;
    }

    /**
     * 根据nbase+a0100取得用户的邮箱地址
     */
    private String getEmailAddress(String objectId) throws Exception {
        String addr = "";

        ContentDAO dao = new ContentDAO(frameconn);

        String nbase = objectId.substring(0, 3);
        String a0100 = objectId.substring(3);


        try {
            String emailField = getEmailFld(); // 邮箱字段

            RecordVo vo = new RecordVo(nbase + "A01");
            vo.setString("a0100", a0100);
            vo = dao.findByPrimaryKey(vo);

            addr += vo.getString(emailField.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception("邮箱获取失败,请检查邮箱设置 ", e));
        }

        return addr;
    }

    /**
     * 获取stmp服务器的属性值
     */
    private Properties getStmpOptions() {
        Properties props = new Properties();

        try {
            RecordVo stmp_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
            if (stmp_vo == null) {
                return props;
            }
            String param = stmp_vo.getString("str_value");

            Document doc = PubFunc.generateDom(param);
            Element stmp = doc.getRootElement().getChild("stmp");

            props.put("mail.smtp.host", stmp.getAttributeValue("host"));
            props.put("mail.smtp.port", stmp.getAttributeValue("port"));

            /**
             * zhanghua 2018-12-18
             * 目前无论是否选择身份验证 都创建认证器对象。目的是和老版程序一致避免没有勾选身份验证无法发送邮件
             */
            props.put("mail.smtp.auth", "true" ); // 1为需要认证,其余为无需认证
            props.put("username", stmp.getAttributeValue("username"));
            props.put("password", stmp.getAttributeValue("password"));
            props.put("from", stmp.getAttributeValue("from_addr"));

            String encryption = stmp.getAttributeValue("encryption");
            encryption = encryption == null ? "" : encryption;
            if (encryption.length() < 1)
                return props;

            if ("tls".equals(encryption)) {
                props.put("mail.smtp.user", stmp.getAttributeValue("username"));
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.socketFactory.port", stmp.getAttributeValue("port"));
                props.put("mail.smtp.socketFactory.fallback", "false");
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                props.put("mail.smtp.ssl.socketFactory", sf);
            } else if ("ssl".equals(encryption)) {
                Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.socketFactory.port", stmp.getAttributeValue("port"));
                props.put("mail.smtp.ssl.checkserveridentity", true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return props;
    }

    /**
     * 默认的邮件模板
     */
    private String getDefaultTemplate() {
        StringBuffer buf = new StringBuffer();
        buf.append("<!DOCTYPE html>\n");
        buf.append("<html>\n");
        buf.append("<head>\n");
        buf.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />\n");
        buf.append("<title>软件</title>\n");
        buf.append("</head>\n");
        buf.append("<body>\n");
        buf.append("\t<center><div style='text-align:center;width:600px;background:#F4F4F4;margin:0px auto;padding:40px 20px 40px 20px;'>\n");
        buf.append("\t\t<div style='width:540px;text-align:left;background:#FFFFFF;padding:40px 30px 70px 30px;'>\n");
        buf.append("\t\t\t<h2 style='color:#929292;font-size:30px;font-weight:normal; border-bottom:1px #E2E2E2 solid;padding-bottom:16px; font-family:微软雅黑'>#subject#</h2>\n");
        buf.append("\t\t\t<div style='font-size:14px; font-weight:bolder;margin-top:40px;'>#bodyText#</div>\n");
        buf.append("\t\t\t<div style='margin-top:40px;border-bottom:1px #E2E2E2 solid;padding-bottom:30px;'>\n");
        buf.append("\t\t\t\t<a href='#href#' style='display:#display#;background:#FF7F1E;padding:0 8px;height:30px;line-height:30px;text-align:center;color:#FFF;text-decoration:none;'>#hrefDesc#</a>\n");
        buf.append("\t\t\t</div>\n");
        buf.append("\t\t\t<div style='margin-top:30px;color:#BFBFBF;'>本邮件由e-HR自动发出，请勿回复。</div>\n");
        buf.append("\t\t\t<div style='margin-top:15px;color:#BFBFBF; float:right;'>发送日期：#sendDate#</div>\n");
        buf.append("\t\t</div>\n");
        buf.append("\t</div></center>\n");
        buf.append("</body>\n");
        buf.append("</html>\n");

        return buf.toString();
    }

    /** 不限制宽度的模板 */
    private String getAutoWidthTemplate() {
        StringBuffer buf = new StringBuffer();
        buf.append("<!DOCTYPE html>\n");
        buf.append("<html>\n");
        buf.append("<head>\n");
        buf.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />\n");
        buf.append("<title>软件</title>\n");
        buf.append("</head>\n");
        buf.append("<body>\n");
        buf.append("\t<center><div style='text-align:center;min-width:600px;background:#F4F4F4;margin:0px auto;padding:40px 20px 40px 20px;display:inline-block !important; display:inline;'>\n");
        buf.append("\t\t<div style='min-width:540px;text-align:left;background:#FFFFFF;padding:40px 30px 70px 30px;display:inline-block !important; display:inline;'>\n");
        buf.append("\t\t\t<h2 style='color:#929292;font-size:30px;font-weight:normal; border-bottom:1px #E2E2E2 solid;padding-bottom:16px; font-family:微软雅黑'>#subject#</h2>\n");
        buf.append("\t\t\t<div style='font-size:14px; font-weight:bolder;margin-top:40px;'>#bodyText#</div>\n");
        buf.append("\t\t\t<div style='margin-top:40px;border-bottom:1px #E2E2E2 solid;padding-bottom:30px;'>\n");
        buf.append("\t\t\t\t<a href='#href#' style='display:#display#;background:#FF7F1E;padding:0 8px;height:30px;line-height:30px;text-align:center;color:#FFF;text-decoration:none;'>#hrefDesc#</a>\n");
        buf.append("\t\t\t</div>\n");
        buf.append("\t\t\t<div style='margin-top:30px;color:#BFBFBF;'>本邮件由e-HR自动发出，请勿回复。</div>\n");
        buf.append("\t\t\t<div style='margin-top:15px;color:#BFBFBF; float:right;'>发送日期：#sendDate#</div>\n");
        buf.append("\t\t</div>\n");
        buf.append("\t</div></center>\n");
        buf.append("</body>\n");
        buf.append("</html>\n");

        return buf.toString();
    }
    /**
     * 建立到本地邮箱服务器的连接
     */
    private Session getSession() {
        // 只有需要认证时才会创建一个认证器对象
        Authenticator auth = !Boolean.parseBoolean(props.getProperty("mail.smtp.auth")) ? null : new MyAuthenticator(props.getProperty("username"), props.getProperty("password"));
        Session session = null;
        /*wangrd 201411-10 如果报错 新建一个session，并屏蔽报错
        try{
			session=Session.getDefaultInstance(props, auth);
		} catch (Exception ex) {
			session= Session.getInstance(props, auth);
            //ex.printStackTrace();
        }
        /** guodd 2016-10-26
         *使用 Session.getDefaultInstance 如果修改了邮件服务器参数，生成的session还是用的以前发送的账号密码，不会根据props更新
         *使用 Session.getInstance每次都会根据传入的props重新创建新的session
         */
        session = Session.getInstance(props, auth);
        return session;
    }

    /*
     * 得到电子邮箱指标
     */
    public String getEmailFld() {
        String emailFld = "";

        emailFld = this.getEmailField();
        if (emailFld == null || emailFld.length() <= 0)
            emailFld = "";

        if (!fieldInA01(emailFld))
            emailFld = "";

        return emailFld;
    }

    /**
     * 得到系统邮件指标
     *
     * @return
     */
    public String getEmailField() {
        String str = "";
        try {
            RecordVo stmp_vo = ConstantParamter.getConstantVo("SS_EMAIL");
            if (stmp_vo == null)
                return "";
            String param = stmp_vo.getString("str_value");
            if (param == null || "#".equals(param))
                return "";
            str = param;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return str;
    }

    private boolean fieldInA01(String field) {
        boolean inA01 = false;
        if (null == field || "".equals(field.trim()))
            return inA01;

        FieldItem fieldItem = DataDictionary.getFieldItem(field, "a01");
        inA01 = null != fieldItem && "1".equals(fieldItem.getUseflag());

        return inA01;
    }

    /**
     * 如果text为空返回默认值，否则返回text，同Oracle的nvl函数
     */
    public static String nvl(Object value, String defaultValue) {
        if (value == null || "".equals(value)) {
            return defaultValue;
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * 验证邮件地址是否合法
     *
     * @param emailaddress 邮箱地址
     * @return
     */
    public boolean isMail(String emailaddress) {

        //String emailPattern ="^([a-z0-9A-Z]+[_]*[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        String emailPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return emailaddress.matches(emailPattern);
    }

    /**
     * 获取邮件模板
     */
    public String getTemplate() {
        return template;
    }

    /**
     * 设定邮件模板
     */
    public void setTemplate(String template) {
        this.template = template;
    }

}
