
package com.hjsj.hrms.businessobject.report.auto_fill_report;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;

/**
 * <p>Title:设置扫描库及截止日期</p>
 * <p>Description:XML文件字符串操作类</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */

public class AnalyseParams {
	
	
  /*XML文件字符串格式
   *存放于Constant表的PR_PARAM字段中 
   <?xml version="1.0" encoding="GB2312"?>
	<param>
	  <user id="su" flag="0">
	    <database databaselist="Usr,Ret,Oth," result="true" appdate="2006-06-26" />
	  </user>
	</param>
	注：
	<?xml version="1.0" encoding="GB2312"?>
	<param>
	  <user id="用户名" flag="0">
	    <database databaselist="DB前缀列表" result="是否扫描查询结果" appdate="截止日期"  startdate="起始日期" />
	  </user>
	</param>
  */
	private Connection conn;
	private String xml;
	private Document doc;
	
	/**
	 * 通过常量类获取XML字符串
	 */
	public AnalyseParams() {		
		//连接DB从中获得XML
		RecordVo vo = ConstantParamter.getConstantVo("RP_PARAM");
		if(vo != null){
			this.setXml(vo.getString("STR_VALUE"));
		}else{	
			xml = "";
		}
	}
	
	
	/**
	 * 通过传入的XML文件字符串-构造器
	 * @param xml XML文件字符串
	 */
	public AnalyseParams(String xml){
		//System.out.println(xml);
		if(xml==null || "".equals(xml)){
			this.xml="";
		}else{
			this.xml=xml;
		}
	}
	
	/**
	 * 通过传入DB连接-构造器
	 * @param conn
	 */
	public AnalyseParams(Connection conn){
		this.conn = conn;
		this.initXML();
	}
	
	/**
	 * 查询DB 获取XML文件字符串
	 */
	private void initXML(){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{	
			//常量表中查找rp_param常量
			rs=dao.search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
			if(rs.next()){
				//获取XML文件
				int dbserver = Sql_switcher.searchDbServer();
				if(dbserver == 2){//oracle
					xml = Sql_switcher.readMemo(rs,"STR_VALUE");
				}else{ //mssql
					//获取XML文件
					xml = rs.getString("STR_VALUE");	
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private void init() throws GeneralException{
        try {
            doc = PubFunc.generateDom(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//判断用户相关配置是否存在
	public boolean checkUserid(String userid) throws GeneralException{
		boolean b = false;
		if(xml == null || "".equals(xml)){
			return b;
		}else{
			init();
		}
		if(userid == null || "".equals(userid)){
			return b;
		}
		StringBuffer temp = new StringBuffer();
		temp.append("/param/user[@id='");
		temp.append(userid.toLowerCase());
		temp.append("']");
		try {
			XPath xPath = XPath.newInstance(temp.toString());
			Element user = (Element) xPath.selectSingleNode(doc);
			if (user == null) {
			} else {
				b=true;
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return b;
	}
	//判断表类是否配置所属时间
	public boolean checkBelongdateSortid(String sortid) throws GeneralException{
		boolean b = false;
		if(xml == null || "".equals(xml)){
			return b;
		}else{
			init();
		}
		if(sortid == null || "".equals(sortid)){
			return b;
		}
		StringBuffer temp = new StringBuffer();
		temp.append("/param/belongdates/belongdate[@sortid='");
		temp.append(sortid.toLowerCase());
		temp.append("']");
		try {
			XPath xPath = XPath.newInstance(temp.toString());
			Element belongdate = (Element) xPath.selectSingleNode(doc);
			if (belongdate == null) {
			} else {
				b=true;
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return b;
	}
	//创建用户相关配置文件
	private String createParamXML(String userid ,String flag , String dblist, String result , String appdate,String startdate){
		String temp = null;
		Element param = new Element("param");
		Element user = new Element("user");
		user.setAttribute("id",userid);
		user.setAttribute("flag",flag);
		Element database = new Element("database");
		database.setAttribute("databaselist",dblist);
		database.setAttribute("result",result);
		database.setAttribute("appdate",appdate);
		database.setAttribute("startdate",startdate);
		user.addContent(database);
		param.addContent(user);
		Document myDocument = new Document(param);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		temp= outputter.outputString(myDocument);
		
	/*	System.out.println("*********创建XML**************");
		System.out.println(temp);
		System.out.println("********************");*/

		return temp;
	}
	
	//追加用户相关配置
	private String addParams(String userid ,String flag , String dblist, String result , String appdate,String startdate){
		String temp = null;
		Element param = doc.getRootElement();
		Element user = new Element("user");
		user.setAttribute("id",userid);
		user.setAttribute("flag",flag);
		Element database = new Element("database");
		database.setAttribute("databaselist",dblist);
		database.setAttribute("result",result);
		database.setAttribute("appdate",appdate);
		database.setAttribute("startdate",startdate);
		user.addContent(database);
		param.addContent(user);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		temp = outputter.outputString(doc);
		
	/*	System.out.println("***********追加*************");
		System.out.println(temp);
		System.out.println("********************");
		*/
		return temp;
	}
	
	//修改用户相关配置
	private String updateParams(String userid , String flag , String dblist, String result , String appdate,String startdate) throws GeneralException{
		String temp =null;
		StringBuffer temp1 = new StringBuffer();
		temp1.append("/param/user[@id='");
		temp1.append(userid);
		temp1.append("']");
		try {
			XPath xPath = XPath.newInstance(temp1.toString());
			Element user = (Element) xPath.selectSingleNode(doc);
			user.getAttribute("id").setValue(userid);
			user.getAttribute("flag").setValue(flag);
			Element database = user.getChild("database");
			database.getAttribute("databaselist").setValue(dblist);
			database.getAttribute("result").setValue(result);
			database.getAttribute("appdate").setValue(appdate);
			if(database.getAttribute("startdate")!=null) {
                database.getAttribute("startdate").setValue(startdate);
            } else
			{
				database.setAttribute("startdate",startdate);
			}
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			temp = outputter.outputString(doc);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	/*	System.out.println("**********修改************");
		System.out.println(temp);
		System.out.println("********************");*/
		return temp;
	}
	
	//用户相关配置参数控制器
	public String paramSet(String userid ,String flag , String dblist, String result , String appdate,String startdate) throws GeneralException{
		String temp = null;	
		//System.out.println("xml=" +this.xml);
		if(xml == null || "".equals(xml)){//创建XML
			temp = this.createParamXML(userid,flag,dblist,result,appdate,startdate);
		}else{
			init();	
			if(this.checkUserid(userid)){//修改XML
				temp = this.updateParams(userid,flag,dblist,result,appdate,startdate);
			}else{//追加XML
				temp = this.addParams(userid,flag,dblist,result,appdate,startdate);
			}
		}
		return temp;
	}

	// 获得特定用户XML文件元素的相关属性值
	public String getAttributeValue(String userid, String attribute) throws GeneralException {
		String value = null;
		if(xml == null || "".equals(xml)){
			return value;
		}else{
			init();
		}
		if(userid ==null || "".equals(userid) ||attribute == null || "".equals(attribute) ){
			return value;
		}
		StringBuffer temp = new StringBuffer();
		temp.append("/param/user[@id='");
		temp.append(userid);
		temp.append("']");
		try {
			XPath xPath = XPath.newInstance(temp.toString());
			Element user = (Element) xPath.selectSingleNode(doc);
			if (user == null) {
			} else {
				if ("flag".equals(attribute)) {
					value = user.getAttributeValue(attribute);
				} else {
					Element database = user.getChild("database");
					value = database.getAttributeValue(attribute);
				}
			}

		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return value;
	}

	//获得特定用户XML文件元素的相关属性值集合
	public HashMap getAttributeValues(String userid) throws GeneralException {
		HashMap hm = new HashMap();
		if(xml == null || "".equals(xml)){
			if(userid != null && !"".equals(userid)){
					String appdate=ConstantParamter.getAppdate(userid);
					String aap="";
					if(appdate!=null&&appdate.length()!=0&&appdate.indexOf(".")!=-1){
						String []pa=appdate.split("\\.");
						for(int i=0;i<pa.length;i++){
							if(i!=pa.length-1) {
                                aap+=pa[i]+"-";
                            } else {
                                aap+=pa[i];
                            }
						}
						
					}
					if(appdate!=null&&appdate.trim().length()!=0){
						hm.put("appdate", aap);
					}
			}
			return hm;
		}else{
			init();
		}
		if(userid == null || "".equals(userid)){
			return hm;
		}
		StringBuffer temp = new StringBuffer();
		temp.append("/param/user[@id='");
		temp.append(userid.toLowerCase());
		temp.append("']");
		try {
			XPath xPath = XPath.newInstance(temp.toString());
			Element user = (Element) xPath.selectSingleNode(doc);
			if (user == null) {
				
				if(userid != null && !"".equals(userid)){
					String appdate=ConstantParamter.getAppdate(userid);
					String aap="";
					if(appdate!=null&&appdate.length()!=0&&appdate.indexOf(".")!=-1){
						String []pa=appdate.split("\\.");
						for(int i=0;i<pa.length;i++){
							if(i!=pa.length-1) {
                                aap+=pa[i]+"-";
                            } else {
                                aap+=pa[i];
                            }
						}
						
					}
					if(appdate!=null&&appdate.trim().length()!=0){
						hm.put("appdate", aap);
					}
			}
				
				
			} else {
				hm.put(user.getAttribute("flag").getName(), user.getAttributeValue("flag"));
				Element database = user.getChild("database");
			//    System.out.println(database.getAttributeValue("databaselist"));
			    hm.put(database.getAttribute("databaselist").getName(),database.getAttributeValue("databaselist"));
				hm.put(database.getAttribute("databaselist").getName(),database.getAttributeValue("databaselist"));
				hm.put(database.getAttribute("result").getName(), database.getAttributeValue("result"));
				String appdate=ConstantParamter.getAppdate(userid);
			
				String aap="";
				if(appdate!=null&&appdate.length()!=0&&appdate.indexOf(".")!=-1){
					String []pa=appdate.split("\\.");
					for(int i=0;i<pa.length;i++){
						if(i!=pa.length-1) {
                            aap+=pa[i]+"-";
                        } else {
                            aap+=pa[i];
                        }
					}
					
				}
				if(appdate!=null&&appdate.trim().length()!=0&&database.getAttributeValue("appdate")!=null){
					hm.put(database.getAttribute("appdate").getName(), aap);
				}
				
				
				if(database.getAttribute("startdate")!=null) {
                    hm.put(database.getAttribute("startdate").getName(), database.getAttributeValue("startdate"));
                } else {
                    hm.put("startdate","");
                }
			}

		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return hm;
	}
	//获得特定表类XML文件元素的相关属性值集合
	public HashMap getAttributeSortidValues(String sortid) throws GeneralException {
		HashMap hm = new HashMap();
		if(xml == null || "".equals(xml)){
			return hm;
		}else{
			init();
		}
		if(sortid == null || "".equals(sortid)){
			return hm;
		}
		StringBuffer temp = new StringBuffer();
		temp.append("/param/belongdates/belongdate[@sortid='");
		temp.append(sortid.toLowerCase());
		temp.append("']");
		try {
			XPath xPath = XPath.newInstance(temp.toString());
			Element belongdate = (Element) xPath.selectSingleNode(doc);
			if (belongdate == null) {
			} else {
				hm.put(belongdate.getAttribute("sortid").getValue(), belongdate.getValue());
				
			}

		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return hm;
	}


	public String addBelongDate(String sortId,String timeValue){
		String temp = null;
		Element param = doc.getRootElement();
		Element belongdate = param.getChild("belongdates");;
		Element database = new Element("belongdate");
		database.setAttribute("sortid",sortId);
		database.setText(timeValue);
		belongdate.addContent(database);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		temp = outputter.outputString(doc);
		return temp;
	}
	public String createBelongDate(String sortId,String timeValue){
		String temp = null;
		Element param=null;
		if(this.doc!=null){
			param=doc.getRootElement();
		}else{
			param = new Element("param");
		}
		Element belongdate =null;
		if(param.getChild("belongdates")!=null){
			belongdate=param.getChild("belongdates");
		}else{
			belongdate= new Element("belongdates");
		}
		
		Element database = new Element("belongdate");
		database.setAttribute("sortid",sortId);
		database.setText(timeValue);
		belongdate.addContent(database);
		if(param.getChild("belongdates")!=null){
		}else{
			param.addContent(belongdate);
		}
		if(this.doc==null){
			Document myDocument = new Document(param);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			temp= outputter.outputString(myDocument);
		}else{
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			temp = outputter.outputString(doc);
		}
		return temp;
	}
	public String updateBelongDate(String sortId,String timeValue){
		String temp =null;
		StringBuffer temp1 = new StringBuffer();
		temp1.append("/param/belongdates/belongdate[@sortid='");
		temp1.append(sortId);
		temp1.append("']");
		try {
			XPath xPath = XPath.newInstance(temp1.toString());
			Element belongdate = (Element) xPath.selectSingleNode(doc);
			belongdate.setText(timeValue);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			temp = outputter.outputString(doc);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	/*	System.out.println("**********修改************");
		System.out.println(temp);
		System.out.println("********************");*/
		return temp;
	}
	public boolean isExitsNode(String node){
		boolean flag=false;
		try {
			this.init();
			String temp = null;
			Element param = doc.getRootElement();
			Element element =null;
			if(param.getChild(node)!=null){
				flag=true;
			}else{
				flag=false;
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		
		return flag;
	}
	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
}
