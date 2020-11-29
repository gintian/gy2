package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResumeImportSchemeXmlBo {
	private Connection conn;
	private String xml;
	private Document doc;
	public static  HashMap  hm;
	DbSecurityImpl dbS = new DbSecurityImpl();
	
	public  ResumeImportSchemeXmlBo(Connection conn){
		this.conn=conn;
		this.initXML();
	}
	
	
	/**
	 * 查询DB 获取XML文件字符串
	 */
	private void initXML(){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{	
			//常量表中查找ZP_IMPORT_SCHEME常量
			rs=dao.search("select str_value  from CONSTANT where UPPER(CONSTANT)='ZP_IMPORT_SCHEME'");
			if(rs.next()){
				//获取XML文件
				xml = Sql_switcher.readMemo(rs,"STR_VALUE");
//		        String pathFile = System.getProperty("java.io.tmpdir");
//		        pathFile += "\\" + "contant.xml";
//		        StringBuffer Log = new StringBuffer();
//		        File file = new File(pathFile);
//		        if(file.exists()){
//		            file.delete();
//		        }
//		        BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
//		        Log.append(xml);
//	            output.write(Log.toString() + "\r\n");
//	            output.close();
				//System.out.println(xml);
			}
			else
			{
				ArrayList list = new ArrayList();
				list.add("ZP_IMPORT_SCHEME");
				list.add("A");
				list.add("简历导入—导入方案");
				list.add("");
				dao.insert("insert into CONSTANT values(?,?,?,?)", list);
				InitImportScheme();
			}
			if(xml!=null&&"".equals(xml.trim())){
				InitImportScheme();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 首次
	 * 初始化xml
	 * 根据简历指标集
	 */
	private void InitImportScheme(){
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			

		Element root = new Element("scheme");
		
		root.addContent(new Element("identifyfld"));
		root.addContent(new Element("sencondfld"));
		root.addContent(new Element("imptype"));
		Element sets = new Element("sets");
		ArrayList list=getSchemeXmlList();
		for(int i=0;i<list.size();i++){
			//set节点
			
			Element set = new Element("set");
			set.addContent(new Element("menus"));
			set.setAttribute("resumeset", (String) list.get(i));
			sets.addContent(set);
			
		}
		root.addContent(sets);
		root.addContent(new Element("codesets"));
		Document xml = new Document(root);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		XMLOutputter XMLOut = new XMLOutputter(FormatXML());
		
		XMLOut.output(xml, bo);
		UpdateConstantXml(bo.toString());
//		dao.update("update CONSTANT set STR_VALUE='"+bo.toString()+"' where UPPER(CONSTANT)='ZP_IMPORT_SCHEME'");
		initXML();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	   public Format FormatXML(){  
		        //格式化生成的xml文件，如果不进行格式化的话，生成的xml文件将会是很长的一行...   
		        Format format = Format.getCompactFormat();  
		        format.setEncoding("UTF-8");  
		        format.setIndent(" ");  
		        return format;  
		   } 
	   
	/**
	 * 检测数据库中的简历指标集列表与xml中的是否一致
	 * 如不一致则删除数据库中的数据重建
	 */
	public void UpdateImportScheme(){
		try{
			ArrayList schemeXmlList = getSchemeXmlList();
			ArrayList schemeXmlList1 = (ArrayList) schemeXmlList.clone();// zzk clone

			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets");
			Element sets = (Element) xPath.selectSingleNode(this.doc);
			List list=(List) sets.getChildren();
			//数据库中的xml和resumeFld.xml中的set节点比较 
	        for(int i=0;i<list.size();i++){
	        	Element element=(Element)list.get(i);
	        	String set = element.getAttributeValue("resumeset");// zzk contain
	        	
	        	schemeXmlList.remove(set);

//	        	for(int j=0;j<schemeXmlList.size();j++){
//	        		if(set.equals(schemeXmlList.get(j))){
//	        			schemeXmlList.remove(j);
//	        		}
//	        	}

	        }
	        for(int i=0;i<schemeXmlList1.size();i++){

	        	for(int j=0;j<list.size();j++){
	        		Element element=(Element)list.get(j);
		        	String set = element.getAttributeValue("resumeset");// zzk contain
//		        	schemeXmlList1.remove(set);
	        		if(schemeXmlList1.contains(set)){
	        			list.remove(j);
	        		}
	        	}

	        }
        	if(schemeXmlList!=null&&schemeXmlList.size()>0){
        		AddImportScheme(schemeXmlList);
        	}
	        if(list!=null&&list.size()>0){
	        	DeleteImportScheme(list);//zzk xml 没处理？ 
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public String getXml() {
		return xml;
	}


	public void setXml(String xml) {
		this.xml = xml;
	}


	/**
	 * 增加数据库中xml的set节点
	 * @param list
	 */
	public void AddImportScheme(ArrayList list){
//		ContentDAO dao = new ContentDAO(this.conn);
		try{
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets");
			Element sets = (Element) xPath.selectSingleNode(this.doc);
			for(int i=0;i<list.size();i++){
				Element set = new Element("set");
				set.addContent(new Element("menus"));
				set.setAttribute("resumeset", (String) list.get(i));
				sets.addContent(set);
			}
//			root.addContent(sets);
//			Document xml = new Document(root);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			XMLOutputter XMLOut = new XMLOutputter(FormatXML());
			
			XMLOut.output(doc, bo);
			UpdateConstantXml(bo.toString());

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 删除set节点
	 * @param list
	 */
	public void DeleteImportScheme(List list){
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets");
			Element sets = (Element) xPath.selectSingleNode(this.doc);
			List set = (List) sets.getChildren();
			for(int i=0;i<list.size();i++){
				Element e = (Element) list.get(i);
				for(int j=0;j<set.size();j++){
					Element child = (Element) set.get(j);
					String x=child.getAttribute("resumeset").getValue();
					String y=e.getAttribute("resumeset").getValue();
					if(x.equals(y)){
						sets.removeContent(child);
					}
				}
			}
//			root.addContent(sets);
//			Document xml = new Document(root);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			XMLOutputter XMLOut = new XMLOutputter(FormatXML());
			
			XMLOut.output(doc, bo);
			UpdateConstantXml(bo.toString());
		//	dao.update("update CONSTANT set STR_VALUE='"+bo.toString()+"' where UPPER(CONSTANT)='ZP_IMPORT_SCHEME'");

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 取得考核模板列表xml
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getSchemeXmlList() throws GeneralException{
		ArrayList schemeXmlList=new ArrayList();
		InputStream ip = null;
		try {
//			byte[] b = xml.getBytes();
			ip = this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/resumeFld.xml");
			//xus 20/4/23 xml 编码改造
			this.doc = PubFunc.generateDom(ip);
			XPath xPath = XPath.newInstance("/scheme/sets");
			Element sets = (Element) xPath.selectSingleNode(this.doc);
			List list=(List) sets.getChildren();
	        for(int i=0;i<list.size();i++){
	        	Element element=(Element)list.get(i);
	        	schemeXmlList.add(element.getAttributeValue("resumeset"));
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} finally {
            PubFunc.closeResource(ip);
		}
		return schemeXmlList;
	}		
	/**
	 * 取得数据库中xml的指标集列表
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getResumeXmlList() throws GeneralException{
		ArrayList resumeXmlList=new ArrayList();
		try {
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets");

			Element sets = (Element) xPath.selectSingleNode(this.doc);
			List list=(List) sets.getChildren();
	        for(int i=0;i<list.size();i++){
	        	Element element=(Element)list.get(i);
	        	LazyDynaBean bean=new LazyDynaBean();
	        	bean.set("resumeset", element.getAttributeValue("resumeset"));
	        	if(element.getAttribute("ehrset")!=null){
	        		bean.set("ehrset", element.getAttributeValue("ehrset"));
	        	}else{
	        		bean.set("ehrset", "");
	        	}
	        	
	        	resumeXmlList.add(bean);
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return resumeXmlList;
	}
	
	/**
	 * 得到resumeFld中的set信息
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSchemeList() throws GeneralException{
		ArrayList resumeXmlList=new ArrayList();
		InputStream ip = null;
		try {
//			byte[] b = xml.getBytes();
//			InputStream ip = new ByteArrayInputStream(b);
			ip =this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/resumeFld.xml");
			//xus 20/4/23 xml 编码改造
			doc = PubFunc.generateDom(ip);
//			Element root = doc.getRootElement();
			XPath xPath = XPath.newInstance("/scheme/sets");
//			Element sets = (Element) xPath.selectNodes(root);
			Element sets = (Element) xPath.selectSingleNode(this.doc);
			List list=(List) sets.getChildren();
	        for(int i=0;i<list.size();i++){
	        	Element element=(Element)list.get(i);
	        	LazyDynaBean bean=new LazyDynaBean();
	        	bean.set("setname", element.getAttributeValue("setname"));

	        		bean.set("setid", element.getAttributeValue("setid"));
	        	
	        	resumeXmlList.add(bean);
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} finally {
			PubFunc.closeResource(ip);//资源释放 jingq 2014.12.29
		}
		return resumeXmlList;
	}
	
	/**
	 * 得到数据库中xml的menu
	 * @param resumeset
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSchemeitemXmlList(String resumeset) throws GeneralException{
		ArrayList schemeParameterList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='"+resumeset+"']/menus");
			Element menus = (Element) xPath.selectSingleNode(this.doc);
			
			List list=(List) menus.getChildren();
	        for(int i=0;i<list.size();i++){
	        	Element element=(Element)list.get(i);
	        	LazyDynaBean bean=new LazyDynaBean();
//	        	String xxx = element.getAttributeValue("itemid");
	        	bean.set("resumefld", element.getAttributeValue("resumefld"));
	        	if(element.getAttribute("ehrfld")!=null){
	        		bean.set("ehrfld", element.getAttributeValue("ehrfld"));
	        	}else{
	        		bean.set("ehrfld", "");
	        	}
	        	if(element.getAttribute("valid")!=null){
	        		bean.set("valid", element.getAttributeValue("valid"));
	        	}else{
	        		bean.set("valid", "");
	        	}
	        	
	        	schemeParameterList.add(bean);
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return schemeParameterList;
		
		
		
	}
	

	
	/**
	 * 获取主标识和更新方式
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSchemeParameterList() throws GeneralException{
		ArrayList schemeParameterList = new ArrayList();
		try{
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme");
			Element scheme = (Element) xPath.selectSingleNode(this.doc);
			LazyDynaBean bean=new LazyDynaBean();
			bean.set("identifyfld", scheme.getChildText("identifyfld"));
			bean.set("sencondfld", scheme.getChildText("sencondfld"));
			bean.set("imptype", scheme.getChildText("imptype"));
			schemeParameterList.add(bean);


		}catch(Exception e){
			e.printStackTrace();
		}
		return schemeParameterList;
		
	}
	
	/**
	 * 更新Constant STR_VALUE大文本xml
	 * @param StrValue
	 * @throws SQLException
	 */
	public void UpdateConstantXml(String StrValue) throws SQLException{
		PreparedStatement ps = null;
		try {
			String sql = "update CONSTANT set STR_VALUE=?  where UPPER(CONSTANT)='ZP_IMPORT_SCHEME'";
			ps = this.conn.prepareStatement(sql);
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL:
				ps.setString(1, StrValue);
				break;
			case Constant.ORACEL:
				ps.setCharacterStream(1, new InputStreamReader(
						new ByteArrayInputStream(StrValue.getBytes())),
						StrValue.length());
				break;
			case Constant.DB2:
				ps.setCharacterStream(1, new InputStreamReader(
						new ByteArrayInputStream(StrValue.getBytes())),
						StrValue.length());
				break;
			}
			// 打开Wallet
			dbS.open(conn, sql);
			ps.executeUpdate();
			xml = StrValue;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(ps);
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * 获取代码项Commonvalue
	 * @param resumeset
	 * @param itemname
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getCommonvalueXmlList(String resumeset,String itemname) throws GeneralException{
		ArrayList resumeXmlList=new ArrayList();
		InputStream ip = null;
		try {
			ip =this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/resumeFld.xml");

			//xus 20/4/23 xml 编码改造
			doc = PubFunc.generateDom(ip);	
//			Element root = doc.getRootElement();
			XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='"+resumeset+"']/menus/menu[@itemname='"+itemname+"']");
			XPath xPath1 = XPath.newInstance("/scheme/sets/set[@resumeset='"+resumeset+"']");
			Element set = (Element) xPath1.selectSingleNode(this.doc);
//			Element sets = (Element) xPath.selectNodes(root);
			Element menu = (Element) xPath.selectSingleNode(this.doc);

	        	LazyDynaBean bean=new LazyDynaBean();
//	        	String xxx = element.getAttributeValue("itemid");
	        	bean.set("setid", set.getAttributeValue("setid"));
	        	bean.set("itemid", menu.getAttributeValue("itemid"));
	        	bean.set("itemname", menu.getAttributeValue("itemname"));
	        	bean.set("itemtype", menu.getAttributeValue("itemtype"));
	        	bean.set("itemlength",menu.getAttributeValue("itemlength"));
	        	bean.set("itemformat", menu.getAttributeValue("itemformat"));
	        	if(menu.getAttribute("commonvalue")!=null){
	        		bean.set("commonvalue", menu.getAttributeValue("commonvalue"));
	        	}else{
	        		bean.set("commonvalue", "");
	        	}
	        	
	        	resumeXmlList.add(bean);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}finally{
			PubFunc.closeResource(ip);
		}
		return resumeXmlList;
	}
	

	/**
	 * 获取所有指标的属性(包括xml和数据库)
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getResumeSchemeXML() throws GeneralException {
		ArrayList resumeXmlList=new ArrayList();
		try{
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets");
			Element sets = (Element) xPath.selectSingleNode(this.doc);
			List child = sets.getChildren();
			//循环所有sets
			for(int i=0;i<child.size();i++){
				Element element = (Element) child.get(i);
				
				Element menus = (Element) element.getChildren("menus").get(0);
				List menulist = menus.getChildren();
				//循环所有menu
				for(int j=0;j<menulist.size();j++){
					LazyDynaBean bean=new LazyDynaBean();
					Element menu = (Element) menulist.get(j);
					bean.set("resumeset", element.getAttributeValue("resumeset"));
					bean.set("valid", menu.getAttributeValue("valid"));
					bean.set("resumefld", menu.getAttributeValue("resumefld"));
					bean.set("ehrfld", menu.getAttributeValue("ehrfld"));
					//获取resumeFld.xml中特定的menu下的所有属性
					ArrayList list = getCommonvalueXmlList(element.getAttributeValue("resumeset"),menu.getAttributeValue("resumefld"));
					for(int k=0;k<list.size();k++){
						LazyDynaBean baen=new LazyDynaBean();
						baen = (LazyDynaBean) list.get(k);
						bean.set("setid", baen.get("setid"));
						bean.set("itemid", baen.get("itemid"));
						bean.set("itemtype", baen.get("itemtype"));
						bean.set("itemlength", baen.get("itemlength"));
						bean.set("itemformat", baen.get("itemformat"));
						bean.set("commonvalue", baen.get("commonvalue"));
					}
					resumeXmlList.add(bean);
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		
		
		
		return resumeXmlList;
	}
	
	/**
	 * 获得代码项
	 * @param resumeset：指标集描述
	 * @param resumefld：字段的描述
	 * @param resumeitemid：itemid所对应的value 字段对应的值
	 * @param codesetid 代码类的名称（AM...）
	 * @return
	 * @throws GeneralException
	 */
	public String getCodeItem(String resumeset,String resumefld,String resumeitemid,String codesetid) throws GeneralException {
		String CodeItem="";
		try{
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/codesets/codeset[@resumeset='"+resumeset+"' and @resumefld='"+resumefld+"']/codeitems");
			Element codeitems = (Element) xPath.selectSingleNode(this.doc);
			List codeitem = codeitems.getChildren();
			
			for(int i=0;i<codeitem.size();i++){//挨着循环当前代码类
				Element element = (Element) codeitem.get(i);
				//人员库代码名称+自定义代码名称
				String reitemid = element.getAttributeValue("resumeitemid");//这个是在代码对应中配置的简历信息
				if(reitemid==null){
					reitemid = "";
				}
				String itemname = AdminCode.getCodeName(codesetid, element.getAttributeValue("ehritemid"))+";"+reitemid+";";
				if(itemname.contains(resumeitemid+";")&&!"".equals(resumeitemid)){
					CodeItem = element.getAttributeValue("ehritemid");
					break;
				}
				if("籍贯".equals(resumefld)&&!"".equals(resumeitemid)){
					String[] jgArr=resumeitemid.split("-");
					boolean flag = true;
					for(int j=0;j<jgArr.length;j++){
						if(!itemname.contains(jgArr[j])){
							flag =false;
						}
					}
					if(flag){
						CodeItem = element.getAttributeValue("ehritemid");
						break;
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		
		
		
		return CodeItem;
		
	}
	
	/**
	 * 获取更新方式
	 * @return
	 * @throws GeneralException
	 */
	public String getimptype() throws GeneralException {
		String imptype = "";
		try{
			
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/imptype");
			Element element = (Element) xPath.selectSingleNode(this.doc);
			imptype = element.getText();
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}
		return imptype;
		
	}
	
	/**
	 * 检查子集指标是否改变
	 * @param resumeset
	 */
	public void checkMenu(String resumeset){
		try{
			ArrayList schemeXmlList = getResumeitemXmlList(resumeset);//取之resumeFld.xml中的数据
			ArrayList schemeXmlList1 = (ArrayList) schemeXmlList.clone();// zzk clone

			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='"+resumeset+"']/menus");
			Element menus = (Element) xPath.selectSingleNode(this.doc);//取之数据库中的字段数据
			if(menus!=null){
				
			
			List list=(List) menus.getChildren();
			//数据库中的xml和resumeFld.xml中的menu节点比较 
	        for(int i=0;i<list.size();i++){
	        	Element element=(Element)list.get(i);
	        	String menu = element.getAttributeValue("resumefld");// zzk contain
	        	
	        	schemeXmlList.remove(menu);


	        }
	        for(int i=0;i<schemeXmlList1.size();i++){

	        	for(int j=list.size()-1;j>=0;j--){
	        		Element element=(Element)list.get(j);
		        	String menu = element.getAttributeValue("resumefld");// zzk contain
	        		if(schemeXmlList1.contains(menu)){
	        			list.remove(j);
	        		}
	        	}

	        }
        	if(schemeXmlList!=null&&schemeXmlList.size()>0){
        		addSchemeMenu(resumeset,schemeXmlList);
        	}
	        if(list!=null&&list.size()>0){
	        	deleteSchemeMenu(resumeset, list);
	        }
			}else{
				XPath xPath1 = XPath.newInstance("/scheme/codesets/codeset[@resumeset='"+resumeset+"']");
				List codeset = xPath1.selectNodes(this.doc);
				if(codeset!=null){
					for(int i=0;i<codeset.size();i++){
						Element child = (Element) codeset.get(i);
						child.getParentElement().removeContent(child);
					}
				}
				
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				XMLOutputter XMLOut = new XMLOutputter(FormatXML());
				
				XMLOut.output(doc, bo);
				xml = bo.toString();
				UpdateConstantXml(bo.toString());
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	/**
	 * 获取resumeFld.xml子集中的指标
	 * @param resumeset
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getResumeitemXmlList(String resumeset) throws GeneralException {
		ArrayList menulist = new ArrayList();
		InputStream ip = null;
		try {
			ip = this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/resumeFld.xml");
			//xus 20/4/23 xml 编码改造
			doc = PubFunc.generateDom(ip);
			// Element root = doc.getRootElement();
			XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='" + resumeset + "']/menus");
			// Element sets = (Element) xPath.selectNodes(root);
			Element menus = (Element) xPath.selectSingleNode(this.doc);

			if (menus != null) {

				List list = (List) menus.getChildren();
				for (int i = 0; i < list.size(); i++) {
					Element element = (Element) list.get(i);

					menulist.add(element.getAttributeValue("itemname"));

				}
			}
			// addSchemeMenu(resumeset,menulist);// zzk 干嘛用的？
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
		    if(ip != null) {
                PubFunc.closeResource(ip);
            }
		        
		}
		return menulist;

	}
	
	/**
	 * 增加子集的指标
	 * 
	 * @param resumeset
	 * @param menulist
	 * @throws GeneralException
	 */
	public void addSchemeMenu(String resumeset,ArrayList menulist) throws GeneralException {
		ArrayList schemeParameterList = new ArrayList();
		try{

			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='"+resumeset+"']/menus");
			Element menus = (Element) xPath.selectSingleNode(this.doc);
			List list = menus.getChildren();
//			if(list.size()==0){
				for(int i=0;i<menulist.size();i++){
					Element element = new Element("menu");
					element.setAttribute("resumefld", (String) menulist.get(i));
					menus.addContent(element);
				}
//			}

			
			
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			XMLOutputter XMLOut = new XMLOutputter(FormatXML());
			
			XMLOut.output(doc, bo);
			xml = bo.toString();
			UpdateConstantXml(bo.toString());
		//	dao.update("update CONSTANT set STR_VALUE='"+bo.toString()+"' where UPPER(CONSTANT)='ZP_IMPORT_SCHEME'");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除menu节点
	 * @param list
	 */
	public void deleteSchemeMenu(String resumeset,List list){
//		ContentDAO dao = new ContentDAO(this.conn);
		try{
			doc = PubFunc.generateDom(xml);
			XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='"+resumeset+"']/menus");
			Element menus = (Element) xPath.selectSingleNode(this.doc);
			List menu = (List) menus.getChildren();
			for(int i=0;i<list.size();i++){
				Element e = (Element) list.get(i);
				for(int j=0;j<menu.size();j++){
					Element child = (Element) menu.get(j);
					String x=child.getAttribute("resumefld").getValue();
					String y=e.getAttribute("resumefld").getValue();
					if(x.equals(y)){
						menus.removeContent(child);
					}
				}
				XPath xPath1 = XPath.newInstance("/scheme/codesets/codeset[@resumeset='"+resumeset+"' and @resumefld='"+e.getAttribute("resumefld").getValue()+"']");
				Element codeset = (Element) xPath1.selectSingleNode(this.doc);
				if(codeset!=null){
					codeset.getParentElement().removeContent(codeset);
				}
			}
//			root.addContent(sets);
//			Document xml = new Document(root);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			XMLOutputter XMLOut = new XMLOutputter(FormatXML());
			
			XMLOut.output(doc, bo);
			UpdateConstantXml(bo.toString());
		//	dao.update("update CONSTANT set STR_VALUE='"+bo.toString()+"' where UPPER(CONSTANT)='ZP_IMPORT_SCHEME'");

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
