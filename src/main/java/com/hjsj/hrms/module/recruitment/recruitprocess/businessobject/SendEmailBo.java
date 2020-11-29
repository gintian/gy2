package com.hjsj.hrms.module.recruitment.recruitprocess.businessobject;

import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import java.sql.Connection;

public class SendEmailBo {
	private Connection conn=null;
    private UserView userview;
    
    public SendEmailBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    public String sendEmail(String c0102,String title,String content)
	{
    	//c0102 = "east_xiaodong@163.com";
		String msg = "";
		try {
			AsyncEmailBo emailbo = new AsyncEmailBo(conn, userview);
			String fromAddr=this.getFromAddr();
			boolean bool = true;
			if(fromAddr==null|| "".equals(fromAddr.trim()))
			{
				msg="系统未设置邮件服务器！";
				bool = false;
			}
			LazyDynaBean emails = new LazyDynaBean();
			content = content.replace("\n", "<br/>");
			//content = content.replace(" ", "&nbsp;");
			emails.set("subject", title);
			emails.set("bodyText", content);
			emails.set("toAddr", c0102);
			try
			{
				if(c0102!=null&&c0102.length()>0)
				{					
					if(bool)
					{
						emailbo.send(emails);
						msg = "1";
					}
				}else{
					msg="当前简历邮箱信息为空！";
				}
			}
			catch(Exception e)
			{
				msg="系统邮件服务器配置不正确，请联系系统管理员！";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return msg;
	}
	public String getFromAddr() throws GeneralException {
		String str = "";
		RecordVo stmp_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
		if (stmp_vo == null)
			return "";
		String param = stmp_vo.getString("str_value");
		if (param == null || "".equals(param))
			return "";
		Document doc = null;
		try {
			doc = PubFunc.generateDom(param);
			Element root = doc.getRootElement();
			Element stmp = root.getChild("stmp");
			str = stmp.getAttributeValue("from_addr");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeResource(doc);
		}
		return str;
	}
}
