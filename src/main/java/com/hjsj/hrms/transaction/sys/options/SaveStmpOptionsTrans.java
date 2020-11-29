/**
 * 
 */
package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-13:14:37:19</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveStmpOptionsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String host=(String)this.getFormHM().get("stmp_addr");
		String username=(String)this.getFormHM().get("log_user");
		String password=(String)this.getFormHM().get("log_pwd");
		String authy=(String)this.getFormHM().get("authy");
		String fromaddr=(String)this.getFormHM().get("from_addr");
		String maxsend=(String)this.getFormHM().get("maxsend");
		String port=(String)this.getFormHM().get("port");
		String encryption = (String)this.getFormHM().get("encryption");
		String sendername = (String)this.getFormHM().get("sendername");
		//系统管理，邮件服务器参数还原  jingq add 2014.09.22
		host = PubFunc.keyWord_reback(host);
		username = PubFunc.keyWord_reback(username);
		password = PubFunc.keyWord_reback(password);
		fromaddr = PubFunc.keyWord_reback(fromaddr);
		maxsend = PubFunc.keyWord_reback(maxsend);
		port = PubFunc.keyWord_reback(port);
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select str_value from constant where UPPER(constant)='SS_STMP_SERVER'");
			Document doc=null;
			String xml="";
			/**要把cs设置的其他参数保留2010-07-20 lizw*/
			while(this.frowset.next())
			{
				xml=Sql_switcher.readMemo(this.frowset,"str_value");
			}
			if(xml==null|| "".equals(xml))
			{
				StringBuffer strxml=new StringBuffer("");
				strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
				strxml.append("<mailserver>");
				strxml.append("</mailserver>");	
				xml=strxml.toString();
			}
			byte[] b = xml.getBytes();
			InputStream ip = new ByteArrayInputStream(b);
			doc = PubFunc.generateDom(ip);
			XPath xPath = XPath.newInstance("/mailserver/stmp");
			Element stmp = (Element) xPath.selectSingleNode(doc);
			boolean flag=false;
			if(stmp==null)
			{
				stmp=new Element("stmp");
				flag=true;
			}
			
			/*Element root=new Element("mailserver");
			doc.setRootElement(root);
			Element stmp=new Element("stmp");*/
			stmp.setAttribute("host",host);
			stmp.setAttribute("username",username);
			stmp.setAttribute("sendername",sendername);
			stmp.setAttribute("password",password);//前台对密码验证了  后台直接保存 wangb 20170916 31639
			    
			stmp.setAttribute("authy",authy);
			stmp.setAttribute("from_addr",fromaddr);
			stmp.setAttribute("max_send",maxsend);
			stmp.setAttribute("port",port);
			if(encryption!=null)
				stmp.setAttribute("encryption",encryption);
			
			if(flag)
			{
				XPath xPath2 = XPath.newInstance("/mailserver");
				Element stmp2 = (Element) xPath2.selectSingleNode(doc);
				stmp2.addContent(stmp);
			}
		    XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
		    format.setEncoding("UTF-8");
		    outputter.setFormat(format);
	        RecordVo  vo=new RecordVo("constant");
	        vo.setString("constant","SS_STMP_SERVER");
	        vo.setString("str_value",outputter.outputString(doc));
	        vo.setString("describe","EMAIL SERVER");
	        dao.deleteValueObject(vo);
            dao.addValueObject(vo);
            ConstantParamter.putConstantVo(vo,"SS_STMP_SERVER");		    
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
