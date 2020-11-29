/**
 * 
 */
package com.hjsj.hrms.servlet.sys;

import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**
 * <p>Title:CreateJnlpXml</p>
 * <p>Description:生成jnpl的xml文件,主要解决从jsp动态传些参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-27:15:30:38</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateJnlpXml {
	/**用户名,需通过后台Servlet传过来*/
	private String name;
	/**口令*/
	private String password;
	private String app;
	private String ctrl;

	public CreateJnlpXml(String name,String password,String app,String ctrl) {
		this.name=name;
		this.password=password;
		this.app=app;
		this.ctrl=ctrl;
	}
	
	/**
	 * 检查属性是否加密，加密的话返回解密后内容。
	 * @param proValue： 属性值，第一个字符是@表示为加密串
	 * @return： 返回结果
	 */
	private String getDecryptProperty(String proValue) {
	    if(proValue==null || proValue.length()==1 || proValue.charAt(0)!='@') {
	    	return proValue;
	    }else{
	    	Des des=new Des();
	    	return des.DecryPwdStr(proValue.substring(1));
	    }
    }	
	
	public String outPutJnlpXml(String codebase,int wboc)
	{
		StringBuffer strcontent=new StringBuffer();
        Element root = new Element("jnlp");
        root.setAttribute("spec","1.0+");
        root.setAttribute("codebase",codebase);
        root.setAttribute("href","/sys/downjnlp");
        Document myDocument = new Document(root);
        try
        {
        	/**information*/
            Element child = new Element("information");
            
            Element cchild= new Element("title");
            cchild.setText("Hrms");
        	child.addContent(cchild);
        	/**vendor*/
            cchild= new Element("vendor");
            cchild.setText("HJSJ, Inc.");
        	child.addContent(cchild);
        	
        	cchild =new Element("homepage");
        	cchild.setAttribute("href","http://www.hrp2000.com");
        	child.addContent(cchild);
        	
        	cchild =new Element("description");
        	cchild.setText("hrms");
        	child.addContent(cchild);
        	
        	cchild =new Element("description");
        	cchild.setAttribute("kind","short");
        	cchild.setText("Hrms");
        	child.addContent(cchild);        	

        	cchild =new Element("icon");
        	cchild.setAttribute("href","images/left.gif");
        	cchild.setAttribute("kind","splash");        	
        	child.addContent(cchild);         	

        	cchild =new Element("icon");
        	cchild.setAttribute("href","images/addrnote_set.gif");
        	child.addContent(cchild);         	

        	cchild =new Element("offline-allowed");
        	child.addContent(cchild); 
        	
        	root.addContent(child);
            /**security*/
        	child=new Element("security");
        	cchild=new Element("all-permissions");
        	child.addContent(cchild);
        	root.addContent(child);
        	/**resources*/
        	child=new Element("resources");
        	cchild=new Element("j2se");
        	cchild.setAttribute("version","1.4+");
        	child.addContent(cchild);

        	cchild=new Element("jar");
        	cchild.setAttribute("href","lib/jws_ehr.jar");
        	child.addContent(cchild);
        	
        	cchild=new Element("jar");        	
        	cchild.setAttribute("href","lib/ehr.jar");
        	child.addContent(cchild);        	

        	root.addContent(child);        	
        	/**application-desc*/
        	child=new Element("application-desc");
        	child.setAttribute("main-class","com.hrms.hjsj.utils.Load_eHr");
        	/**dbserver position,后台数据库服务器配置参数*/
        	cchild=new Element("argument");
        	String dbserver_ip=getDecryptProperty(SystemConfig.getProperty("dbserver_addr"));
        	if("".equals(dbserver_ip))
        		cchild.setText("127.0.0.1");        		
        	else
        		cchild.setText(dbserver_ip);
        	child.addContent(cchild);

        	cchild=new Element("argument");
        	String dbserver_port=getDecryptProperty(SystemConfig.getProperty("dbserver_port"));
        	if("".equals(dbserver_port))
        		dbserver_port="1433";
        	else
        		cchild.setText(dbserver_port);
        	child.addContent(cchild);
        	
        	cchild=new Element("argument");
        	String dbname=getDecryptProperty(SystemConfig.getProperty("dbname"));
        	if("".equals(dbname))
        		cchild.setText("ykchr");        		
        	else
        		cchild.setText(dbname);
        	child.addContent(cchild);
        	
        	cchild=new Element("argument");
        	String db_type=Integer.toString(Sql_switcher.searchDbServer());
        	if("".equals(db_type))
        		cchild.setText("mssql");        		
        	else
        		cchild.setText(db_type);
        	child.addContent(cchild);
        	
        	cchild=new Element("argument");
        	String db_user=getDecryptProperty(SystemConfig.getProperty("db_user"));
        	if("".equals(db_user))
        		cchild.setText("#");         		
        	else
        		cchild.setText(db_user);
        	child.addContent(cchild);
        	
        	cchild=new Element("argument");
        	String db_pwd=getDecryptProperty(SystemConfig.getProperty("db_user_pwd"));
        	if("".equals(db_pwd))
        		cchild.setText("#");    		
        	else
        		cchild.setText(db_pwd);
        	child.addContent(cchild);
        	
        	/**username=,password=*/
        	cchild=new Element("argument");
        	if("".equals(this.name))
        		cchild.setText("#");         		
        	else
        		cchild.setText(this.name);
        	child.addContent(cchild);

        	cchild=new Element("argument");
        	if("".equals(this.password))
        		cchild.setText("#");        		
        	else
        		cchild.setText(this.password);
        	child.addContent(cchild);
        	
        	/**版本号,集团版或别的什么版本*/
        	cchild=new Element("argument");
        	cchild.setText(this.ctrl/*"98"*/);
        	child.addContent(cchild);
        	/**显示应用面板*/
        	cchild=new Element("argument");
        	cchild.setText(this.app);
        	child.addContent(cchild);
        	/**版权控制*/
        	cchild=new Element("ctrl");
        	cchild.setText(this.ctrl);
        	child.addContent(cchild);
        	/**绩效机读控制*/
        	cchild=new Element("wboc");
        	cchild.setText(String.valueOf(wboc));
        	child.addContent(cchild);
        	
        	root.addContent(child);
        	
	        XMLOutputter outputter = new XMLOutputter();
	        Format format=Format.getPrettyFormat();
	        //format.setEncoding("utf-16");
	        outputter.setFormat(format);
	        strcontent.append(outputter.outputString(myDocument));
	        //System.out.println("---->"+strcontent.toString());
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        
		return strcontent.toString();
	}
}
