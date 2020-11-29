package com.hjsj.hrms.businessobject.general.operation;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TwfdefineBo {
	private String bsp_flag;
	private String sp_flag;
	private String email;
	private String sms;
	private String edit_form;
	private String appeal_form;
	private ArrayList edit_param=new ArrayList();
	private ArrayList appeal_param=new ArrayList();
	public ArrayList getAppeal_param() {
		return appeal_param;
	}

	public void setAppeal_param(ArrayList appeal_param) {
		this.appeal_param = appeal_param;
	}

	public ArrayList getEdit_param() {
		return edit_param;
	}

	public void setEdit_param(ArrayList edit_param) {
		this.edit_param = edit_param;
	}

	public  Document paraxml(String xml) throws Exception{
		Document doc=null;
		if(xml==null||xml.length()<1){
			this.setBsp_flag("0");
			doc=this.createxmlctrl_para();
		}
		else{
			//xus 20/4/23 xml 编码改造
			 doc = PubFunc.generateDom(xml);		}
		//System.out.println(xml);
		Element element=null;
		
		String xpath="/params/sp_flag";
		XPath findPath = XPath.newInstance(xpath);
		element = (Element) findPath.selectSingleNode(doc);
		String mode=element.getAttribute("mode").getValue();
		if("".equals(mode)||mode.length()<1){
			this.setBsp_flag("0");
		}else{
			this.setBsp_flag("1");
		}
		this.setSp_flag(mode);
		xpath="/params/notes";
		findPath = XPath.newInstance(xpath);
		element = (Element) findPath.selectSingleNode(doc);
		String email=element.getAttributeValue("email");
		String sms=element.getAttributeValue("sms");
		this.setEmail(email);
		this.setSms(sms);
		xpath="/params/edit_form";
		findPath = XPath.newInstance(xpath);
		element = (Element) findPath.selectSingleNode(doc);
		String inputurl=element.getAttributeValue("url");
		this.setEdit_param(getParamList(element));		
		this.setEdit_form(inputurl);
		xpath="/params/appeal_form";
		findPath = XPath.newInstance(xpath);
		element = (Element) findPath.selectSingleNode(doc);
		String appurl=element.getAttributeValue("url");
		this.setAppeal_param(getParamList(element));		
		this.setAppeal_form(appurl);
		return doc;
	}
	
	private Document createxmlctrl_para(){
		
		Element params=new Element("params");
		Element notes =new Element("notes");
		notes.setAttribute("email","false");
		notes.setAttribute("sms","false");
		Element sp_flag=new Element("sp_flag");
		sp_flag.setAttribute("mode","");
		Element edit_form=new Element("edit_form");
		edit_form.setAttribute("url","");
		Element appeal_form=new Element("appeal_form");
		appeal_form.setAttribute("url","");
		params.addContent(notes);
		params.addContent(sp_flag);
		params.addContent(edit_form);
		params.addContent(appeal_form);
		Document doc =new Document(params);
		return doc;
	}
	public String getctrl_para(String sp_flag,String email,String sms) throws JDOMException{
		/*
		 * flag=0修改自定义表单审批模式 =1 修改固定表单审批模式
		 */
		Document doc=this.createxmlctrl_para();
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element sp_flagel=element.getChild("sp_flag");
			sp_flagel.getAttribute("mode").setValue(sp_flag);
			Element notes=element.getChild("notes");
			notes.getAttribute("email").setValue(email);
			notes.getAttribute("sms").setValue(sms);
		}
		return outputter.outputString(doc);
	}
	public String updatetwfctrl_para(String xml,String sp_flag,String email,String sms) throws Exception{
		/*
		 * flag=0修改自定义表单审批模式 =1 修改固定表单审批模式
		 */
		 
		//xus 20/4/23 xml 编码改造
		 Document doc = PubFunc.generateDom(xml);
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element sp_flagel=element.getChild("sp_flag");
			sp_flagel.getAttribute("mode").setValue(sp_flag);
			Element notes=element.getChild("notes");
			notes.getAttribute("email").setValue(email);
			notes.getAttribute("sms").setValue(sms);
		}
		return outputter.outputString(doc);
	}
	public String updatectrl_para(String xml,String inputurl,String appurl) throws Exception{
		Document doc=this.paraxml(xml);
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element edit_form=element.getChild("edit_form");
			edit_form.getAttribute("url").setValue(inputurl);
			Element appeal_form=element.getChild("appeal_form");
			appeal_form.getAttribute("url").setValue(appurl);
		
		}
		return outputter.outputString(doc);
	}
	public String updatectrl_Formpara(String xml,String[] inputname,String[] inputparam,String[] appname,String[] appparam) throws Exception
	{
		Document doc=this.paraxml(xml);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="";		
		Element element=null;		
		if(inputname!=null&&inputname.length>0&&inputparam!=null&&inputparam.length>0)
		{
			xpath="/params/edit_form";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点	
			element =(Element) findPath.selectSingleNode(doc);
			element.removeContent();
			for(int i=0;i<inputname.length;i++)
			{
				Element childelement=new Element("br_para");
				childelement.setAttribute("name", inputname[i]);
				childelement.setAttribute("value", inputparam[i]);
				element.addContent(childelement);
			}
		}
		if(appname!=null&&appname.length>0&&appparam!=null&&appparam.length>0)
		{
			xpath="/params/appeal_form";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点	
			element =(Element) findPath.selectSingleNode(doc);
			element.removeContent();
			for(int i=0;i<appname.length;i++)
			{
				Element childelement=new Element("br_para");
				childelement.setAttribute("name", appname[i]);
				childelement.setAttribute("value", appparam[i]);
				element.addContent(childelement);
			}
		}
		//System.out.println(outputter.outputString(doc));
		return outputter.outputString(doc);
	}
	/**
	 * 
	 * @param element
	 * @param tag
	 * @return
	 */
	private ArrayList getParamList(Element element)
	{
		ArrayList list=new ArrayList();
		List childlist=element.getChildren();
		Iterator i = childlist.iterator();
		Element childelement=null;		
		
		while(i.hasNext())
		{
			childelement=(Element)i.next();
			HashMap map=new HashMap();
			map.put("name",childelement.getAttributeValue("name")!=null&&childelement.getAttributeValue("name").length()>0?childelement.getAttributeValue("name"):"");			
			map.put("value",childelement.getAttributeValue("value")!=null&&childelement.getAttributeValue("value").length()>0?childelement.getAttributeValue("value"):"");
		    list.add(map);
		}
		return list;
	}
	//获得业务办理人的姓名
	public String getBusinessPerson(String enduser,String endusertype,Connection conn){
		String name ="";
		try
		{
			 ContentDAO dao=new ContentDAO(conn);
			 RowSet rowSet=dao.search("select * from dbname ");
			 String pre = "";
			 boolean flag =false;
			 while(rowSet.next()){
				if(enduser.startsWith(rowSet.getString("pre"))){
					flag=true;
					pre = rowSet.getString("pre");
					break;
				}
			 }
			 if(endusertype!=null&& "1".equals(endusertype)){
				 String a0100 = enduser.substring(pre.length(),enduser.length());
				 rowSet=dao.search("select * from  "+pre+"A01  where A0100='"+a0100+"'" );
				 if(rowSet.next()){
					name = rowSet.getString("a0101") ;
				 }
			 }
			 else if(endusertype!=null&& "0".equals(endusertype)){

				 rowSet=dao.search("select fullname,username from OperUser    where username='"+enduser+"'" );
				 if(rowSet.next()){
					if( rowSet.getString("fullname")!=null&&rowSet.getString("fullname").length()>0 ){
						name = rowSet.getString("fullname") ;
					}else{
						name = rowSet.getString("username") ;
					}
				 } 
			  
			 }
		}catch(Exception e){
			
		}
		return name;
	}
	public String getctrl_paraAttributeValue(String xml,String elementname ,String attributename) throws Exception{
		/*
		 * flag=0修改自定义表单审批模式 =1 修改固定表单审批模式
		 */
		 String value="";
		//xus 20/4/23 xml 编码改造
		 Document doc = PubFunc.generateDom(xml);
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element sp_flagel=element.getChild(elementname);
			if(sp_flagel==null){
				value=null;
			}else{
				value =sp_flagel.getAttribute(attributename)==null?"":sp_flagel.getAttribute(attributename).getValue();
			}
			
		}
		return value;
	}
	public String updatectrl_paraAttributevalue(String sp_flag,String elementname,String attributename,String flag,String xml) throws Exception{
		/*
		 * flag=0修改自定义表单审批模式 =1 修改固定表单审批模式
		 */
		//xus 20/4/23 xml 编码改造
		 Document doc = PubFunc.generateDom(xml);
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element sp_flagel=element.getChild(elementname);
			if(sp_flagel==null){
				sp_flagel=new Element(elementname);
				if("sp_flag".equals(elementname)){
				sp_flagel.setAttribute("mode","0");
				}
				element.addContent(sp_flagel);
			}
			if(sp_flagel.getAttribute(attributename)==null){
				sp_flagel.setAttribute(attributename,"");
			}
			if(sp_flag!=null) {
                sp_flagel.getAttribute(attributename).setValue(sp_flag);
            }
			
		}
		
		return outputter.outputString(doc);
	}
	/**更新驳回方式  郭峰
	 * @throws Exception */
	public String updatectrl_paraValue(String reject_type_value,String xml) throws Exception{
		/*
		 * flag=0修改自定义表单审批模式 =1 修改固定表单审批模式
		 */
		//xus 20/4/23 xml 编码改造
		 Document doc = PubFunc.generateDom(xml);
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
		element =(Element) findPath.selectSingleNode(doc);
		String reject_flag_value = "1";
		if(element!=null){
			Element child=element.getChild("rejectFlag");
			if(child==null){//如果暂时还没有这个节点
				child=new Element("rejectFlag");
				child.setText(reject_type_value);
				element.addContent(child);
			}else{//已经有节点了
				child.setText(reject_type_value);
			}
		}
		return outputter.outputString(doc);
	}
	
	//获得审批或者抄送邮件模版
	public ArrayList getEmail_name(Connection conn){
		ArrayList list = new ArrayList();
		CommonData dt=new CommonData("","");
		list.add(dt);
		try
		{
			 ContentDAO dao=new ContentDAO(conn);
			 RowSet rowSet=dao.search("select * from email_name where name is not null and nModule in (1,2,5)");//20171215 只显示nModule为1\2\5的  过滤掉招聘和绩效的邮件模板
             while (rowSet.next()) {
                if (!"".equals(rowSet.getString("name").trim()) ){ //20141215 xyy当name值为null或为空字符串时不取这条记录
                    dt = new CommonData(rowSet.getString("id"),
                            rowSet.getString("id") + "."
                                    + rowSet.getString("name"));
                    list.add(dt);

                }

            }
			 
			 rowSet.close();
		}catch(Exception e){
			
		}
		return list;
	}
	public String getBsp_flag() {
		return bsp_flag;
	}
	public void setBsp_flag(String bsp_flag) {
		this.bsp_flag = bsp_flag;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSms() {
		return sms;
	}
	public void setSms(String sms) {
		this.sms = sms;
	}
	public String getSp_flag() {
		return sp_flag;
	}
	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getAppeal_form() {
		return appeal_form;
	}

	public void setAppeal_form(String appeal_form) {
		this.appeal_form = appeal_form;
	}

	public String getEdit_form() {
		return edit_form;
	}

	public void setEdit_form(String edit_form) {
		this.edit_form = edit_form;
	}
	
}
