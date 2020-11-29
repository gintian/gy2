/*
 * Created on 2006-4-20
 *
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.xmlparameter;


import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * <p>Title: XmlParameter </p>
 * <p>Description: 薪酬表XML</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2014-9-11 下午5:46:35</p>
 * @author 
 * @version 1.0
 */
public class XmlParameter {
   
	/**代码类*/
    private String codesetid;
    /**代码项*/
    private String codeitemid;
    /**父代码*/
    private String parentid;  
    private String type="0";
    /** 按关联代码类UN的登记表,电脑*/
    private String card_id="0";
    /** 按关联代码类UN的登记表,手机*/
    private String mobcardid;
    private String relating="";
    private String flag="true";
    private ArrayList codenamelist=new ArrayList();
    private ArrayList codesetlist=new ArrayList();
    private ArrayList cardnolist=new ArrayList();
    /** 按单位显示登记表移动服务app设置参数增加属性*/
    private ArrayList mobcardnoList=new ArrayList();
    private Connection conn;
    private String xmlcontent="";
    private Document doc;
    private String mustflag="";
    private ArrayList musteredlist=new ArrayList();
    private String musterid="";
    private String year_restrict;
    public String getYear_restrict() {
		return year_restrict;
	}
	public void setYear_restrict(String year_restrict) {
		this.year_restrict = year_restrict;
	}
	public String getMusterid() {
		return musterid;
	}
	public void setMusterid(String musterid) {
		this.musterid = musterid;
	}
	/**<?xml version="1.0" encoding="GBK"?>
     <card>
      <b0110 code="" cardid="15" type="0" />
     </card>
    */
    public XmlParameter(){
    }   
    public XmlParameter(String codesetid,String codeitemid,String parentid) {
        this.codeitemid=codeitemid;
        this.codesetid=codesetid;
        this.parentid=parentid;
    }
    public void  ReadOutParameterXml(String constant,Connection conn,String codesetid,String codeitemid,String parentid,String codename)
    {
    	this.codeitemid=codeitemid;
    	this.codesetid=codesetid;
    	this.parentid=parentid;
    	ReadOutParameterXml(constant,conn,codename);
    }
    public void  ReadOutParameterXml(String constant,Connection conn)
    {
    	ReadOutParameterXml(constant,conn,"all");
    }
    
    //同步代码
    public void syncYkCardConfigCode(String constant,Connection conn){
    	
    	try{
    		RecordVo card_vo=ConstantParamter.getRealConstantVo(constant);
    	  	if(card_vo==null)
    	  		return;
    	  	String str_value = (String)card_vo.getString("str_value");
    	  	if(str_value==null || str_value.length()<1 || str_value.indexOf("xml")==-1)
    	  		return;
    	  	
    	  	if(!"UN".equalsIgnoreCase(this.getCodesetid()))
    	  		return;
    	  	
    		Document doc = PubFunc.generateDom(str_value); //读入xml	    	    
    	    Element root = doc.getRootElement(); // 取得根节点
    	    Element node=root.getChild("b0110" + this.getCodeitemid().toLowerCase());
    	    if(node==null)
    	    	return;
    	    List codeConfigList = node.getChildren();
    	    if(codeConfigList.size()<1)
    	    	return;
    	    String codeset = ((Element)codeConfigList.get(0)).getAttributeValue("codeset");
    	    String field = ((Element)codeConfigList.get(0)).getName();
    	    
    	    HashMap valueMap = new HashMap();
    	    for(int i=0;i<codeConfigList.size();i++){
	    	    Element c = (Element)codeConfigList.get(i);
	    	    valueMap.put(c.getAttributeValue("codeitem"), c.clone());
    	    }
    	    node.removeContent();
    	    
  	    	StringBuffer sql=new StringBuffer();
        	sql.append("SELECT codeitemid,codesetid FROM codeitem");
        	sql.append(" where  codesetid='"+codeset.toUpperCase()+"' order by codeitemid");
  	        List codes= ExecuteSQL.executeMyQuery(sql.toString(), conn);
  	        for(int i=0;i<codes.size();i++){
  	        		String codeitemid = ((LazyDynaBean)codes.get(i)).get("codeitemid").toString().toLowerCase();
  	        		if(valueMap.containsKey(codeitemid)){
  	        			Object el = valueMap.get(codeitemid);
  	        			codes.set(i, el);
  	        		}else{
  	        			Element el = new Element(field);
  	        			el.setAttribute("codeset", codeset);
  	        			el.setAttribute("codeitem", codeitemid);
  	        			el.setAttribute("cardno", "");
  	        			el.setAttribute("mustered", "");
  	        			codes.set(i, el);
  	        		}
  	        }
  	        node.addContent(codes);
  	        this.conn = conn;
  	        this.doc = doc;
  	        saveParameter();
    	      }catch(Exception e){
    	    	  	e.printStackTrace();
    	      }
    	
    }
    
    
    public void  ReadOutParameterXml(String constant,Connection conn,String codename)
    {
	     if(codename==null||codename.length()<=0)
	    	 codename="";	    
   	   try{
   		   	RecordVo card_vo=ConstantParamter.getRealConstantVo(constant);
	        if (card_vo!=null) {
		        String str_value = card_vo.getString("str_value").toLowerCase();  	
	        	//System.out.println(str_value);
	        	if(str_value!=null && str_value.trim().length()>0 && str_value.indexOf("xml")!=-1)
	        	{
	    	      Document doc = PubFunc.generateDom(str_value); //读入xml	    	    
	    	      Element root = doc.getRootElement(); // 取得根节点
	    	      if("UN".equalsIgnoreCase(this.getCodesetid()))
	    	      {
	    	        Element node=root.getChild("b0110" + this.getCodeitemid().toLowerCase());
	     	        if(node!=null)
	    	        {
	    	        	this.card_id=node.getAttributeValue("cardid");
	    	        	if(this.card_id==null||this.card_id.length()<=0) {
	    	        		readB0110XMl(root,"cardid");	  ////薪酬表设置 ,登记表  	        		
	    	        	}
	    	        	this.mobcardid = node.getAttributeValue("mobcardid");
	    	        	if(this.mobcardid == null || this.mobcardid.length() == 0) {
	    	        		readB0110XMl(root,"mobcardid");	  ////薪酬表设置 ,登记表  	        		
	    	        	}
	    	        	this.flag=node.getAttributeValue("flag");
	    	        	this.type=node.getAttributeValue("type");
	    	        	this.relating=node.getAttributeValue("relating");
	    	        	this.year_restrict=node.getAttributeValue("year_restrict");
	    	        	this.mustflag=node.getAttributeValue("mustflag");
	    	        	this.musterid=node.getAttributeValue("mustered");	    	        	
	    	        	if(this.musterid==null||this.musterid.length()<=0)//薪酬表高级花名册设置  ,按单位选择花名册 
	    	        	{
	    	        		readB0110XMl(root,"mustered");
	    	        	}	
	    	        	List childlist=node.getChildren();
	    	        	Iterator r = childlist.iterator();
	    	        	Element element=null;	
	    	        	CommonData dataobj=null;
	    	        	while(r.hasNext())
	    	        	{
	    	        		element=(Element)r.next();	
	    	        		String name=element.getName();	    	        		
	    	        		String codeset=element.getAttributeValue("codeset");
	    	        		String codeitem=element.getAttributeValue("codeitem");
	    	        		String cardno=element.getAttributeValue("cardno");
	    	        		String mustered=element.getAttributeValue("mustered");
	    	        		if(name==null||name.length()<=0)
	    	        			continue;
	    	        		else
	    	        		  this.codenamelist.add(name);
	    	        		if(codename!=null&&!"all".equals(codename))
	    	        		{
	    	        			if(!name.equalsIgnoreCase(codename))
	    	        				break;
	    	        		}
	    	        	    if(codeset==null||codeset.length()<=0)
	    	        	    	continue;
	    	        	    dataobj=new CommonData();
	    	        		dataobj.setDataName(codeset.toUpperCase());
	    	        		dataobj.setDataValue(codeitem.toUpperCase());
	    	        	    this.codesetlist.add(dataobj);
	    	        	    if(cardno==null||cardno.length()<=0)
	    	        	    	cardno="";
	    	        	    this.cardnolist.add(cardno);
	    	        	    if(mustered==null||mustered.length()<=0)
	    	        	    	mustered="";
	    	        	    this.musteredlist.add(mustered);
	    	        	    // 按单位显示登记表移动服务app设置参数增加属性mobcardid保存
	    	        		String mobcardno = element.getAttributeValue("mobcardno");
	    	        		mobcardno = mobcardno == null ? "" : mobcardno;
	    	        		this.mobcardnoList.add(mobcardno);
	    	        	}
	    	        	
	    	        	if(this.cardnolist==null||this.cardnolist.size()<=0)
	    	        	{
	    	        		readB0110XMl(root,"cardnolist");
	    	        	}
	    	        	if(this.mobcardnoList == null || this.mobcardnoList.size() == 0) {
	    	        		this.readB0110XMl(root, "mobcardnoList");
	    	        	}
	    	        	if(this.musteredlist==null||this.musteredlist.size()<=0)
	    	        	{
	    	        		readB0110XMl(root,"musteredlist");
	    	        	}
	    	        }
	    	        else
	    	        {
	    	        	 Element sunode=root.getChild("b0110");
	    	        	 if(sunode!=null)
	    	        	 {
	    	        		 this.card_id = sunode.getAttributeValue("cardid");
	    	        		 this.mobcardid = sunode.getAttributeValue("mobcardid");
			    	         this.type=sunode.getAttributeValue("type");
			    	         this.flag=sunode.getAttributeValue("flag");
			    	         this.relating=sunode.getAttributeValue("relating");
			    	         this.mustflag=sunode.getAttributeValue("mustflag");
			    	         this.musterid=sunode.getAttributeValue("mustered");
			    	         this.year_restrict=sunode.getAttributeValue("year_restrict");
			    	         List childlist=sunode.getChildren();
			    	         Iterator r = childlist.iterator();
			    	         Element element=null;	
			    	         CommonData dataobj=null;
			    	         while(r.hasNext())
			    	         {
			    	        		element=(Element)r.next();	
			    	        		String name=element.getName();
			    	        		String codeset=element.getAttributeValue("codeset");
			    	        		String codeitem=element.getAttributeValue("codeitem");
			    	        		String cardno=element.getAttributeValue("cardno");
			    	        		// 按单位显示登记表移动服务app设置参数增加属性mobcardid保存
			    	        		String mobcardno=element.getAttributeValue("mobcardno");
			    	        		mobcardno = mobcardno == null ? "" : mobcardno;
			    	        		this.mobcardnoList.add(mobcardno);
			    	        		String mustered=element.getAttributeValue("mustered");
			    	        		if(name==null||name.length()<=0)
			    	        			continue;
			    	        		else
			    	        	      this.codenamelist.add(name);
			    	        		if(codeset==null||codeset.length()<=0)
			    	        	    	codeset="";
			    	        		dataobj=new CommonData();
			    	        		dataobj.setDataName(codeset.toUpperCase());
			    	        		dataobj.setDataValue(codeitem.toUpperCase());
			    	        	    this.codesetlist.add(dataobj);
			    	        	    if(cardno==null||cardno.length()<=0)
			    	        	    	cardno="";
			    	        	    this.cardnolist.add(cardno);
			    	        	    if(mustered==null||mustered.length()<=0)
			    	        	    	mustered="";
			    	        	    this.musteredlist.add(mustered);
			    	        } 
	    	        	 }
	    	        	 
	    	        }
	    	      }	    	          	      
	           	}
	        	
	        }
         }catch (Exception ee)
         {
            ee.printStackTrace();
            GeneralExceptionHandler.Handle(ee);
         }
         finally
	     {
	        } 
    }
    private void readB0110XMl(Element root,String name)
    {
    	 Element sunode=root.getChild("b0110");
    	 if(sunode!=null)
    	 {
    		 if("cardid".equals(name))
    		 {
    			 //薪酬表设置 ,登记表按单位显示登记表
    			 this.card_id=sunode.getAttributeValue("cardid");	
    			 this.type=sunode.getAttributeValue("type");
    			 this.flag=sunode.getAttributeValue("flag");
    			 this.relating=sunode.getAttributeValue("relating");
			} else if ("mobcardid".equals(name)) {
				this.mobcardid = sunode.getAttributeValue("mobcardid");
			} else if("mustered".equals(name)) {
    			 //薪酬表高级花名册设置  ,按单位选择花名册 
    			 this.musterid=sunode.getAttributeValue("mustered");
    			 this.mustflag=sunode.getAttributeValue("mustflag");    	       
    		 }else if("cardnolist".equals(name))
    		 {
    			 List childlist=sunode.getChildren();//按类型显示登记表
    	         Iterator r = childlist.iterator();
    	         Element element=null;	
    	         CommonData dataobj=null;
    	         while(r.hasNext())
    	         {
    	        		element=(Element)r.next();
    	        		String cardno = element.getAttributeValue("cardno");
    	        		String codeset=element.getAttributeValue("codeset");
    	        		String codeitem=element.getAttributeValue("codeitem");
    	        		String name_E=element.getName();    	        		
    	        		if(name_E==null||name_E.length()<=0)
    	        			continue;
    	        		else
    	        	      this.codenamelist.add(name_E);
    	        		if(codeset==null||codeset.length()<=0)
    	        	    	codeset="";
    	        		dataobj=new CommonData();
    	        		dataobj.setDataName(codeset.toUpperCase());
    	        		dataobj.setDataValue(codeitem.toUpperCase());
    	        	    this.codesetlist.add(dataobj);
    	        	    if(cardno==null||cardno.length()<=0)
    	        	    	cardno="";
    	        	    this.cardnolist.add(cardno);
    	        } 
			} else if ("mobcardnoList".equals(name)) {
				List childlist = sunode.getChildren();// 按类型显示登记表
				Iterator r = childlist.iterator();
				Element element = null;
				while (r.hasNext()) {
					element = (Element) r.next();
					String mobcardno = element.getAttributeValue("mobcardno");
					mobcardno = mobcardno == null || mobcardno.length() == 0 ? "" : mobcardno;
					this.mobcardnoList.add(mobcardno);
				}
			} else if("musteredlist".equals(name))
    		 {
    			 List childlist=sunode.getChildren();//薪酬表高级花名册设置  //按类型选择花名册 
    	         Iterator r = childlist.iterator();
    	         Element element=null;	    
    	         //CommonData dataobj=null;
    	         while(r.hasNext())
    	         {
    	        		element=(Element)r.next();	
    	        		/*String codeset=element.getAttributeValue("codeset");
    	        		String codeitem=element.getAttributeValue("codeitem");
    	        		String name_E=element.getName();    	        		
    	        		if(name_E==null||name_E.length()<=0)
    	        			continue;
    	        		else
    	        	      this.codenamelist.add(name_E);
    	        		if(codeset==null||codeset.length()<=0)
    	        	    	codeset="";
    	        		dataobj=new CommonData();
    	        		dataobj.setDataName(codeset.toUpperCase());
    	        		dataobj.setDataValue(codeitem.toUpperCase());
    	        	    this.codesetlist.add(dataobj);*/
    	        		String mustered=element.getAttributeValue("mustered");
    	        		if(mustered==null||mustered.length()<=0)
    	        	    	mustered="";
    	        	    this.musteredlist.add(mustered);
    	        } 
    		 }
    	 }
    }
    public  void WriteOutParameterXml(String constant,String cardid,String type,String flag,Connection conn,String codesetid,String codeitemid,String parentid)
    {
    	this.codesetid=codesetid;
    	this.codeitemid=codeitemid;
    	this.parentid=parentid;
    	this.flag=flag;
    	WriteOutParameterXml(constant,true,cardid,false,"",type,flag,"",conn);
    }
    public  void WriteOutParameterMustXml(String constant,String cardid,String mustid,String type,String flag,String mustflag,Connection conn,String codesetid,String codeitemid,String parentid)
    {
    	this.codesetid=codesetid;
    	this.codeitemid=codeitemid;
    	this.parentid=parentid;
    	this.flag=flag;
    	WriteOutParameterXml(constant,false,"",true,mustid,type,flag,mustflag,conn);
    }
    
	public void WriteOutParameterXml(String constant, boolean cardtype, String cardid, boolean mustidtype, 
			String mustid, String type, String flag, String mustflag, Connection conn) {
		this.WriteOutParameterXml(constant, cardtype, cardid, mustidtype, mustid, type, flag, mustflag, conn, false);
	}
    
	/**
	 * 
	 * @Title: WriteOutParameterXml   
	 * @Description: 写入   
	 * @param constant 
	 * @param cardtype
	 * @param cardid
	 * @param mustidtype
	 * @param mustid
	 * @param type
	 * @param flag
	 * @param mustflag
	 * @param conn
	 * @param mobapp 是否移动参数写入
	 * @return void
	 */
    public void WriteOutParameterXml(String constant,boolean cardtype,String cardid,boolean mustidtype,String mustid,String type,String flag,String mustflag,Connection conn, boolean mobapp)
    {
    	 try{
	    	StringBuffer xmls=new StringBuffer();	       
            RecordVo card_vo=ConstantParamter.getRealConstantVo(constant);
	        if (card_vo!=null) {
	            String str_value = card_vo.getString("str_value");
	        	if(str_value!=null && str_value.trim().length()>0 && str_value.indexOf("xml")!=-1)
	        	{
	    	      Document doc = PubFunc.generateDom(str_value); //读入xml
	    	      Element root = doc.getRootElement(); // 取得根节点
	    	      if("UN".equalsIgnoreCase(this.getCodesetid()))
	    	      {
	    	    	
	    	        Element node=root.getChild("b0110" + this.getCodeitemid().toLowerCase());
	    	        if(node==null)
	    	        {
	      	        	 Element child = new Element("b0110" + this.codeitemid.toLowerCase());
	    	        	 child.setAttribute("code",this.codeitemid.toLowerCase());
	    	        	 if(cardtype)
	    	        		 child.setAttribute("cardid",cardid); 
	    	        	 if(mustidtype)
	    	        		 child.setAttribute("mustered",mustid); 
	    	        	 if(type!=null&&type.length()>0)
	    	        	   child.setAttribute("type",type);
	    	        	 if(flag!=null&&flag.length()>0)
	    	        	   child.setAttribute("flag",flag);
	    	        	 if(mustflag!=null&&mustflag.length()>0)
	    	        		 child.setAttribute("mustflag",mustflag);
	    	        	 if(relating!=null&&relating.length()>0)
	    	        		 child.setAttribute("relating",relating);
	    	        	 if(this.year_restrict == null)
	    	        		 this.year_restrict = "";
	    	        	 child.setAttribute("year_restrict",year_restrict);
	    	        	 root.addContent(child);	    
	    	        }else
	    	        {
	    	        	if(cardtype) {
	    	        		if(mobapp) {
	    	        			node.setAttribute("mobcardid",cardid); 
	    	        		} else {
	    	        			node.setAttribute("cardid",cardid); 
	    	        		}
	    	        	}
	    	        	 if(mustidtype)
	    	        		 node.setAttribute("mustered",mustid);    	 
	    	        	 if(type!=null&&type.length()>0)	    	        		 
	    	        	   node.setAttribute("type",type);
	    	        	 if(flag!=null&&flag.length()>0)
	    	        	   node.setAttribute("flag",flag);
	    	        	 if(mustflag!=null&&mustflag.length()>0)
	    	        		 node.setAttribute("mustflag",mustflag);
	    	        	 if(relating!=null&&relating.length()>0)
	    	        		 node.setAttribute("relating",relating);
	    	        	 if(this.year_restrict == null)
	    	        		 this.year_restrict = "";
	    	        	 node.setAttribute("year_restrict",year_restrict);

	    	        }
	    	        XMLOutputter outputter = new XMLOutputter();
       	            Format format=Format.getPrettyFormat();
       	            format.setEncoding("UTF-8");
       	            outputter.setFormat(format);
       	            xmls.append(outputter.outputString(doc));       	           
       	            //strsql.delete(0,strsql.length());
       	            //strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='");
       	            //strsql.append(constant);
       	            //strsql.append("'");
       	            //pstmt.executeUpdate(strsql.toString());
                    ConstantXml cst = new ConstantXml(conn);
                    cst.saveValue(constant, xmls.toString());
	    	      }	    	          	      
	           	}
	        	else
	        	{
	        		 Element root = new Element("card");
	        	     Document doc = new Document(root);
	        	     if("UN".equalsIgnoreCase(this.getCodesetid()))
	        	     {
	        	         Element child = new Element("b0110" + this.codeitemid);
	        	         child.setAttribute("code",this.codeitemid);
	        	         if(cardtype)
	        	        	 child.setAttribute("cardid",cardid); 
	    	        	 if(mustidtype)
	    	        		 child.setAttribute("mustered",mustid); 
	    	        	 if(type!=null&&type.length()>0)
	        	            child.setAttribute("type",type);
	        	         if(flag!=null&&flag.length()>0)
	        	           child.setAttribute("flag",flag);
	        	         if(mustflag!=null&&mustflag.length()>0)
	        	        	child.setAttribute("mustflag",mustflag);
	        	         if(relating!=null&&relating.length()>0)
	        	        	 child.setAttribute("relating",relating);
	        	         if(this.year_restrict!=null&&this.year_restrict.length()>0)
	    	        		 child.setAttribute("year_restrict",year_restrict);
	        	         root.addContent(child);
	        	         XMLOutputter outputter = new XMLOutputter();
	        	         Format format=Format.getPrettyFormat();
	        	         format.setEncoding("UTF-8");
	        	         outputter.setFormat(format);
	        	         xmls.append(outputter.outputString(doc));
	                     
	        	         //strsql.delete(0,strsql.length());
	        	         //strsql.append("update  constant set str_value='" + xmls.toString() + "',type='" + type + "' where constant='");
	        	         //strsql.append(constant);
	        	         //strsql.append("'");
	        	         //pstmt.executeUpdate(strsql.toString());
	                     ConstantXml cst = new ConstantXml(conn);
	                     cst.saveValue(constant, xmls.toString());
	        	     }
	        	}
	        }
	        else
	        {
	       		 Element root = new Element("card");
	       	     Document doc = new Document(root);
	       	     if("UN".equalsIgnoreCase(this.getCodesetid()))
	       	     {
	       	         Element child = new Element("b0110" + this.codeitemid);
	       	         child.setAttribute("code",this.codeitemid);
	       	         if(cardtype)
     	        	     child.setAttribute("cardid",cardid); 
 	        	     if(mustidtype)
 	        		     child.setAttribute("mustered",mustid); 
 	        	     if(type!=null&&type.length()>0)
	       	             child.setAttribute("type",type);
 	        	     if(flag!=null&&flag.length()>0)
	       	            child.setAttribute("flag",flag);
 	        	     if(mustflag!=null&&mustflag.length()>0)
       	        	   child.setAttribute("mustflag",mustflag);
 	        	     if(relating!=null&&relating.length()>0)
      	        	   child.setAttribute("relating",relating);
 	        	     if(this.year_restrict!=null&&this.year_restrict.length()>0)
  	        		    child.setAttribute("year_restrict",year_restrict);
	       	         root.addContent(child);
	       	         XMLOutputter outputter = new XMLOutputter();
	       	         Format format=Format.getPrettyFormat();
	       	         format.setEncoding("UTF-8");
	       	         outputter.setFormat(format);
	       	         xmls.append(outputter.outputString(doc));
	       	         ConstantXml cst = new ConstantXml(conn);
	       	         cst.insertNewParameter(constant, type, xmls.toString(), "薪酬表");
	       	     }       	
	        }
        }catch (Exception ee)
        {
            ee.printStackTrace();
            GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
        }
  }    
    public String getCodeitemid() {
    	if(codeitemid==null||codeitemid.length()<=0)
    		codeitemid="";
        return codeitemid;
    }
    public void setCodeitemid(String codeitemid) {
        this.codeitemid = codeitemid;
    }
    public String getCodesetid() {
        return codesetid;
    }
    public void setCodesetid(String codesetid) {
        this.codesetid = codesetid;
    }
	/**
	 * @return Returns the parentid.
	 */
	public String getParentid() {
		return parentid;
	}
	/**
	 * @param parentid The parentid to set.
	 */
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	/**
	 * @return Returns the card_id.
	 */
	public String getCard_id() {
		return card_id;
	}
	/**
	 * @param card_id The card_id to set.
	 */
	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList getCardnolist() {
		return cardnolist;
	}
	public void setCardnolist(ArrayList cardnolist) {
		this.cardnolist = cardnolist;
	}
	public ArrayList getCodenamelist() {
		return codenamelist;
	}
	public void setCodenamelist(ArrayList codenamelist) {
		this.codenamelist = codenamelist;
	}
	public ArrayList getCodesetlist() {
		return codesetlist;
	}
	public void setCodesetlist(ArrayList codesetlist) {
		this.codesetlist = codesetlist;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public XmlParameter(Connection conn,String codeitemid,String flag) {
	this.conn = conn;
	this.codeitemid=codeitemid;
	this.flag=flag;
		init();
		try
		{
			doc=PubFunc.generateDom(xmlcontent.toString());			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
	private void init()
	{
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","SS_SETCARD");
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<card>");
		strxml.append("</card>");		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null)
				xmlcontent=vo.getString("str_value");
			if(xmlcontent==null|| "".equals(xmlcontent))
			{
				xmlcontent=strxml.toString();
			}
		}
		catch(Exception ex)
		{
			xmlcontent=strxml.toString();
			ex.printStackTrace();
		}		
	}
	
	public void setCodeChild(String codeitem,String name,String cardno) {
		this.setCodeChild(codeitem, name, cardno, false);
	}
	
	public void setCodeChild(String codeitem, String name, String cardno, boolean mobapp) {
		StringBuffer xptah = new StringBuffer();
		xptah.append("/card/b0110" + this.getCodeitemid().toLowerCase());
		xptah.append("/" + name.toLowerCase());
		xptah.append("[@codeitem='" + codeitem.toLowerCase() + "']");
		try {
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist = xPath.selectNodes(this.doc);
			if (childlist.size() != 0) {
				Element element = (Element) childlist.get(0);
				/* codeset,codeitem,cardno */
				if (mobapp)
					element.setAttribute("mobcardno", cardno);
				else
					element.setAttribute("cardno", cardno);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setMustChild(String codeitem,String name,String cardno)
	{
		StringBuffer xptah=new StringBuffer();
		xptah.append("/card/b0110" + this.getCodeitemid().toLowerCase());
		xptah.append("/"+name.toLowerCase());
		xptah.append("[@codeitem='"+codeitem.toLowerCase()+"']");		
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				/*codeset,codeitem,cardno*/
				element.setAttribute("mustered",cardno);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	public String getCardno(String name,String codeitem){
		return this.getCardno(name, codeitem, false);
	}
	
	/**
	 * 
	 * @Title: getCardno   
	 * @Description:获取薪酬表设置
	 * @param name
	 * @param codeitem
	 * @param isMobile 是否移动设置
	 * @return String
	 */
	public String getCardno(String name,String codeitem, boolean isMobile)
	{
		StringBuffer xptah=new StringBuffer();
		//see("");
		xptah.append("/card/b0110" + this.getCodeitemid().toLowerCase());
		xptah.append("/"+name.toLowerCase());
		xptah.append("[@codeitem='"+codeitem+"']");
		//HashMap hash=new HashMap();
		String cardno="";
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				/*codeset,codeitem,cardno*/
				if(isMobile) // 移动端
					cardno=element.getAttributeValue("mobcardno");
				else // 电脑端
					cardno=element.getAttributeValue("cardno");
				//hash.put("cardno",cardno);
			}else
			{
				xptah=new StringBuffer();
				xptah.append("/card/b0110");
				xptah.append("/"+name.toLowerCase());
				xptah.append("[@codeitem='"+codeitem+"']");
				xPath = XPath.newInstance(xptah.toString());
				childlist=xPath.selectNodes(this.doc);
				if(childlist.size()!=0)
				{
					Element element=(Element)childlist.get(0);
					/*codeset,codeitem,cardno*/
					if(isMobile) // 移动端
						cardno=element.getAttributeValue("mobcardno");
					else // 电脑端
						cardno=element.getAttributeValue("cardno");
					//hash.put("cardno",cardno);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return cardno == null ?  "" : cardno;
		//see("设置孩子");
	}	
	
	public void setCodeFlag()
	{
		StringBuffer xptah=new StringBuffer();		
		xptah.append("/card/b0110" + this.getCodeitemid().toLowerCase());
		xptah.append("[@code='"+this.getCodeitemid().toLowerCase()+"']");	
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				/*codeset,codeitem,cardno*/
				element.setAttribute("flag",this.flag);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//see("设置孩子");
	}
	public void setMustFlag()
	{
		StringBuffer xptah=new StringBuffer();		
		xptah.append("/card/b0110" + this.getCodeitemid().toLowerCase());
		xptah.append("[@code='"+this.getCodeitemid().toLowerCase()+"']");	
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				/*codeset,codeitem,cardno*/
				element.setAttribute("mustflag",this.flag);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//see("设置孩子");
	}
	/**
	 * 初始化孩子
	 * @param name
	 * @param codesetlist
	 */
	public void initCodeChile(String name,ArrayList codesetlist)
	{
		StringBuffer xptah=new StringBuffer();
		xptah.append("/card/b0110" + this.getCodeitemid().toLowerCase());
		xptah.append("[@code='"+this.getCodeitemid().toLowerCase()+"']");
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				List childlist2=element.getChildren();
			    Iterator t = childlist2.iterator();
			    if(!t.hasNext())
			    {
			    	CommonData dataobj=null;
			    	for(int i=0;i<codesetlist.size();i++)
			    	{
			    		/*codeset,codeitem,cardno*/
			    		dataobj=(CommonData)codesetlist.get(i);
			    		String codeset=dataobj.getDataName();
			    		String codeitem=dataobj.getDataValue();
			    		Element childR=new Element(name.toLowerCase());
			    		childR.setAttribute("codeset",codeset.toLowerCase());
			    		childR.setAttribute("codeitem",codeitem.toLowerCase());
			    		childR.setAttribute("cardno","");
			    		childR.setAttribute("mustered","");
			    		element.addContent(childR);
			    	}
			    }
			 }else
			 {
				 Element element = new Element("b0110" + this.codeitemid.toLowerCase());
				 element.setAttribute("code",this.codeitemid.toLowerCase());
				 element.setAttribute("cardid","");
				 element.setAttribute("type","");
				 CommonData dataobj=null;
			     for(int i=0;i<codesetlist.size();i++)
			     {
			    		/*codeset,codeitem,cardno*/
			    		dataobj=(CommonData)codesetlist.get(i);
			    		String codeset=dataobj.getDataName();
			    		String codeitem=dataobj.getDataValue();
			    		Element childR=new Element(name.toLowerCase());
			    		childR.setAttribute("codeset",codeset.toLowerCase());
			    		childR.setAttribute("codeitem",codeitem.toLowerCase());
			    		childR.setAttribute("cardno","");
			    		childR.setAttribute("mustered","");
			    		element.addContent(childR);
			     }
	        	 Element root = this.doc.getRootElement(); 
	        	 root.addContent(element);
			 }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//see("初始化孩子");
	}
	
	public void removeContent(String name)
	{
		//see("kaishi");
		StringBuffer xptah=new StringBuffer();
		xptah.append("/card/b0110" + this.getCodeitemid().toLowerCase());
		xptah.append("[@code='"+this.getCodeitemid().toLowerCase()+"']");
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				List childlist2=element.getChildren();
			    Iterator t = childlist2.iterator();
			    if(t.hasNext())
			    {
			    	Element childR=(Element)t.next();	
			    	String c_name=childR.getName();
                    if(c_name==null||c_name.length()<=0)
                    	c_name="";
                    if(!c_name.equalsIgnoreCase(name.toLowerCase()))
                    {
                    	childR.getParent().removeContent();
                    }
			    }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//see("清除孩子");
	}
	/**
	 * 保存参数，先设置参数值，再保存
	 * @throws GeneralException
	 */
	public void saveParameter()throws GeneralException
	{
		try
		{
			if_SysConstant_Save("SS_SETCARD");
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("constant");			
			boolean iscorrect=if_vo_Empty("SS_SETCARD");
			if(!iscorrect)
			{
				vo=new RecordVo("constant");
				vo.setString("str_value",buf.toString());
				vo.setString("constant","SS_SETCARD");				
				dao.addValueObject(vo);
			}
			else
			{
				vo.setString("str_value",buf.toString());
				vo.setString("constant","SS_SETCARD");
				dao.updateValueObject(vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
		//see();
	}
	public boolean if_vo_Empty(String constant)
	{
		  String sql="select * from constant where UPPER(Constant)='"+constant.toUpperCase()+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  boolean is_correct=true;
		  RowSet rs=null;
		  try
		  {
			rs=dao.search(sql);		  
			  if(!rs.next())
			  {
				  is_correct=false; 
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }	
		  return is_correct;
	}
	 public void if_SysConstant_Save(String constant)
		{
			  String sql="select * from constant where UPPER(Constant)='"+constant.toUpperCase()+"'";
			  ContentDAO dao = new ContentDAO(conn);
			  RowSet rs=null;
			  try
			  {
				rs=dao.search(sql);		  
				  if(!rs.next())
				  {
					  insertNewSys(constant);
				  }
			  }catch(Exception e)
			  {
				  e.printStackTrace();
			  }		 
		}
	  /**
		 * 插入新的
		 * @param constant
		 */
		public void insertNewSys(String constant)
		{
			String insert="insert into constant(Constant) values (?)";
			ArrayList list=new ArrayList();
			list.add(constant.toLowerCase());			
			ContentDAO dao = new ContentDAO(conn);
			  try
			  {
				dao.insert(insert,list);		  
				  
			  }catch(Exception e)
			  {
				  e.printStackTrace();
			  }		
		}
		public boolean cleanConstantSet(UserView userView)
		{
			boolean isCorrect =true;
			StringBuffer xptah=new StringBuffer();
			xptah.append("/card");
			//xptah.append("/b0110" + this.getCodeitemid().toLowerCase()+"01");			
			try
			{
				XPath xPath = XPath.newInstance(xptah.toString());
				List childlist=xPath.selectNodes(this.doc);
				if(childlist.size()!=0)
				{
					Element element=(Element)childlist.get(0);
					if(!userView.isSuper_admin()&&this.getCodeitemid().toLowerCase().length()>0)
					  element.removeChild("b0110" + this.getCodeitemid().toLowerCase());
					else
					{
						element.removeContent();
					}
				}
			}catch(Exception e)
			{
			  e.printStackTrace();
			  isCorrect=false;
			}				
			return isCorrect;
		}
		public void see(String tt)
		{
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			System.out.println(tt);
			System.out.println(outputter.outputString(doc));
		}
		public ArrayList getMusteredlist() {
			return musteredlist;
		}
		public void setMusteredlist(ArrayList musteredlist) {
			this.musteredlist = musteredlist;
		}
		public String getMustflag() {
			return mustflag;
		}
		public void setMustflag(String mustflag) {
			this.mustflag = mustflag;
		}
		public String getMustered(String name,String codeitem)
		{
			StringBuffer xptah=new StringBuffer();
			//see("");
			xptah.append("/card/b0110" + this.getCodeitemid().toLowerCase());
			xptah.append("/"+name.toLowerCase());
			xptah.append("[@codeitem='"+codeitem+"']");
			HashMap hash=new HashMap();
			String mustered="";
			try
			{
				XPath xPath = XPath.newInstance(xptah.toString());
				List childlist=xPath.selectNodes(this.doc);
				if(childlist.size()!=0)
				{
					Element element=(Element)childlist.get(0);
					/*codeset,codeitem,cardno*/
					mustered=element.getAttributeValue("mustered");
					hash.put("mustered",mustered);
				}else
				{
					xptah=new StringBuffer();
					xptah.append("/card/b0110");
					xptah.append("/"+name.toLowerCase());
					xptah.append("[@codeitem='"+codeitem+"']");
					xPath = XPath.newInstance(xptah.toString());
					childlist=xPath.selectNodes(this.doc);
					if(childlist.size()!=0)
					{
						Element element=(Element)childlist.get(0);
						/*codeset,codeitem,cardno*/
						mustered=element.getAttributeValue("mustered");
						hash.put("mustered",mustered);
					}
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			//see("iii");
			return mustered;
			
		}
		public String getRelating() {
			return relating;
		}
		public void setRelating(String relating) {
			this.relating = relating;
		}
		public String getMobcardid() {
			return mobcardid;
		}
		public void setMobcardid(String mobcardid) {
			this.mobcardid = mobcardid;
		}
		public ArrayList getMobcardnoList() {
			return mobcardnoList;
		}
		public void setMobcardnoList(ArrayList mobcardnoList) {
			this.mobcardnoList = mobcardnoList;
		}
		
		
}
