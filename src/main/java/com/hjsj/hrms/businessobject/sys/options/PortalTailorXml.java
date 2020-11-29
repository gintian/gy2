package com.hjsj.hrms.businessobject.sys.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PortalTailorXml {
	/*
	 * 子节点的name和text，保存的是含有label和value
	 * 两个属性的bean
	 * */
	/**
	 * 首页面板信息保存优化
	 * update by xiegh on 20170914
	 * 优化的方法：
	 * NWriteOutParameterXml,
	 * NReadOutParameterXml,
	 * ReadOutParameterXmlHideValueAndShowOrder
	 * 现xml格式：
	 * <?xml version="1.0" encoding="gbk"?>
 	 * <param>
	 *     <portal id="01">
	 *      	<panel id="0135" scroll="0" twinkle="0" show="1" />
	 *     </portal>
	 *     <portal id="02">
     *   	    <panel id="0221" scroll="0" twinkle="0" show="1" />
	 *     </portal>
     * </param>
	 * portalid=01:领导桌面；=02：hcm和hr
	 */
	private String username;
	private String flag;  
	private int num=8;
	List portalList = new ArrayList();//存放portal的list

    public PortalTailorXml(){
    }
    public ArrayList  ReadOutParameterXml(String constant,Connection conn,String user)
    {
	     ArrayList resultnodenamevaluelist=new ArrayList();
	     String str_value ="";
	     StringBuffer sql=new StringBuffer();
	     RowSet rs=null;
 	     sql.append("select str_value from constant where upper(constant)='"+constant+"'");
 	     ContentDAO dao=new ContentDAO(conn);
 	     try{
 	       rs=dao.search(sql.toString());
 	       if(rs.next()) {
               str_value=Sql_switcher.readMemo(rs, "str_value");
           }
 	     }catch(Exception e)
 	     {
 	    	 e.printStackTrace();
 	     }finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
			
   	    try{
	        
	        //RecordVo option_vo=ConstantParamter.getRealConstantVo(constant);
	        if (str_value!=null) {
	        	str_value =str_value.toLowerCase();  
	        	if(str_value!=null && str_value.trim().length()>0 && str_value.indexOf("xml")!=-1)
	        	{
	    	      Document doc= PubFunc.generateDom(str_value);      //读入xml	  
	        	  XPath reportPath = XPath.newInstance("//user[@id=\"" + user.toLowerCase() + "\"]");// 取得根节点
	       	      List childlist=reportPath.selectNodes(doc);
	    	      //Element root = doc.getRootElement(); // 取得根节点
	    	      //List childnode=root.getChildren();
	       	      if(childlist!=null&&childlist.size()>0){
	       	      Iterator i = childlist.iterator();
				  if(i.hasNext())
				  {	  
	    	      /*for(int i=0;i<childlist.size();i++)
	    	      {
	    	      	Element node=(Element)childlist.get(i);*/
	    	      	Element node=(Element)i.next();
	    	      	List childs=node.getChildren();
	    	      	boolean bMatter=false;
	      			boolean bSalary=false;
	      			HashMap map=new HashMap();
	    	       	if(childs.size()>0){
		    	      	for(int j=0;j<childs.size();j++)
		    	      	{
		    	      		Element nodeitems=(Element)childs.get(j);
		    	      		List childitems=nodeitems.getChildren();
		    	      		if(childitems.size()>0){		    	      			
		    	      			String id="";
			    	      		for(int n=0;n<childitems.size();n++)
			    	      		{
			    	      			Element nodeitem=(Element)childitems.get(n);
			    	      			ArrayList attributelist=new ArrayList();
			    	      			
			    	      			if(!bMatter) {
                                        bMatter=nodeitem.getAttributeValue("id")!=null&& "8".equals(nodeitem.getAttributeValue("id"))?true:false;
                                    }
			    	      			/*if(!bSalary)
			    	      			  bSalary=nodeitem.getAttributeValue("id")!=null&&nodeitem.getAttributeValue("id").equals("9")?true:false;*/
			    	      			if(map.get(nodeitem.getAttributeValue("id"))!=null) {
                                        continue;
                                    }
			    	      			attributelist.add(new LabelValueView(nodeitem.getAttributeValue("id"),"id"));
			    	      			attributelist.add(new LabelValueView(nodeitem.getAttributeValue("scroll"),"scroll"));
			    	      			attributelist.add(new LabelValueView(nodeitem.getAttributeValue("twinkle"),"twinkle"));
			    	      			attributelist.add(new LabelValueView(nodeitem.getAttributeValue("show"),"show"));
			    	      			map.put(nodeitem.getAttributeValue("id"), "0");
			    	      			resultnodenamevaluelist.add(attributelist);
			    	      		}	
			    	      		if(!bMatter)
			    	      		{
			    	      			ArrayList attributelist=new ArrayList();
			    	      			attributelist.add(new LabelValueView("8","id"));
			    	      			attributelist.add(new LabelValueView("0","scroll"));
			    	      			attributelist.add(new LabelValueView("0","twinkle"));
			    	      			attributelist.add(new LabelValueView("0","show"));
			    	      			resultnodenamevaluelist.add(attributelist);
			    	      			bMatter=true;
			    	      		}
			    	      		/*if(!bSalary)
			    	      		{
			    	      			ArrayList attributelist=new ArrayList();
			    	      			attributelist.add(new LabelValueView("9","id"));
			    	      			attributelist.add(new LabelValueView("0","scroll"));
			    	      			attributelist.add(new LabelValueView("0","twinkle"));
			    	      			attributelist.add(new LabelValueView("0","show"));
			    	      			resultnodenamevaluelist.add(attributelist);
			    	      			bSalary=true;
			    	      		}*/
			    	      		
		    	      		}else		    	      			
		    	      		{
		    		        	for(int n=0;n<num;n++)
		    		      		{
		    		       			ArrayList attributelist=new ArrayList();
		    		      			attributelist.add(new LabelValueView(String.valueOf(n+1),"id"));
		    		      			attributelist.add(new LabelValueView("0","scroll"));
		    		      			attributelist.add(new LabelValueView("0","twinkle"));
		    		      			if(n<4) {
                                        attributelist.add(new LabelValueView("1","show"));
                                    } else {
                                        attributelist.add(new LabelValueView("0","show"));
                                    }
		    		      			resultnodenamevaluelist.add(attributelist);
		    		      		}
		    		        }
		    	      	}
	    	      	}
	    	      	else
	       		        {
	    		        	for(int n=0;n<num;n++)
	    		      		{
	    		       			ArrayList attributelist=new ArrayList();
	    		      			attributelist.add(new LabelValueView(String.valueOf(n+1),"id"));
	    		      			attributelist.add(new LabelValueView("0","scroll"));
	    		      			attributelist.add(new LabelValueView("0","twinkle"));
	    		      			if(n<4) {
                                    attributelist.add(new LabelValueView("1","show"));
                                } else {
                                    attributelist.add(new LabelValueView("0","show"));
                                }
	    		      			resultnodenamevaluelist.add(attributelist);
	    		      		}
	    		        }
	    	      }
	    	      }else
	    	      {
			        	for(int n=0;n<num;n++)
			      		{
			       			ArrayList attributelist=new ArrayList();
			      			attributelist.add(new LabelValueView(String.valueOf(n+1),"id"));
			      			attributelist.add(new LabelValueView("0","scroll"));
			      			attributelist.add(new LabelValueView("0","twinkle"));
			      			if(n<4) {
                                attributelist.add(new LabelValueView("1","show"));
                            } else {
                                attributelist.add(new LabelValueView("0","show"));
                            }
			      			resultnodenamevaluelist.add(attributelist);
			      		}
			        
	    	      }
	           	}
	        	else
		        {
		        	for(int n=0;n<num;n++)
		      		{
		       			ArrayList attributelist=new ArrayList();
		      			attributelist.add(new LabelValueView(String.valueOf(n+1),"id"));
		      			attributelist.add(new LabelValueView("0","scroll"));
		      			attributelist.add(new LabelValueView("0","twinkle"));
		      			if(n<4) {
                            attributelist.add(new LabelValueView("1","show"));
                        } else {
                            attributelist.add(new LabelValueView("0","show"));
                        }
		      			resultnodenamevaluelist.add(attributelist);
		      		}
		        }
	        }else
	        {
	        	for(int n=0;n<num;n++)
	      		{
	       			ArrayList attributelist=new ArrayList();
	      			attributelist.add(new LabelValueView(String.valueOf(n+1),"id"));
	      			attributelist.add(new LabelValueView("0","scroll"));
	      			attributelist.add(new LabelValueView("0","twinkle"));
	      			if(n<4) {
                        attributelist.add(new LabelValueView("1","show"));
                    } else {
                        attributelist.add(new LabelValueView("0","show"));
                    }
	      			resultnodenamevaluelist.add(attributelist);
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
        return resultnodenamevaluelist;
    } 
    
    private Document  getPortalList(LazyDynaBean lbean,String portalid){
    	Document doc=null;
    	try{
	    	String str_value = ((String)lbean.get("portalxml")).toLowerCase(); 
	    	if(str_value!=null && str_value.trim().length()>0 && str_value.indexOf("xml")!=-1){
		      	 doc= PubFunc.generateDom(str_value); //读入xml	
		    	 XPath reportPath = XPath.newInstance("//portal[@id=\"" + portalid + "\"]");
		    	 portalList.clear();
		    	 portalList=reportPath.selectNodes(doc);
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return doc;
    }
    public ArrayList  NReadOutParameterXml(Connection conn,String username,String portalid){
	     ArrayList list = new ArrayList();
   	    try{
   	    	  LazyDynaBean lbean = getRealConstantVo(username.toLowerCase(), conn);
   	    	  if(null!=lbean) {
                  getPortalList(lbean,portalid);
              }
       	      if(portalList!=null&&portalList.size()>0){
   	    	  		Element ele = (Element)portalList.get(0);
    	      		List childitems=ele.getChildren();//<portal
    	      		if(childitems.size()>0){	
	    	      		for(int n=0;n<childitems.size();n++){	
	      					Element panelnode = (Element)childitems.get(n);
	    	      			LazyDynaBean bean=new LazyDynaBean();
	    	      			bean.set("id", panelnode.getAttributeValue("id"));
	    	      			bean.set("scroll",panelnode.getAttributeValue("scroll"));
	    	      			bean.set("twinkle",panelnode.getAttributeValue("twinkle"));
	    	      			bean.set("show",panelnode.getAttributeValue("show"));
	    	      			list.add(bean);
	    	      		}
    	      		}	
      		 }
         }catch (Exception ee){
            ee.printStackTrace();
            GeneralExceptionHandler.Handle(ee);
         }
        return list;
    }  

	private LazyDynaBean getRealConstantVo(String username,Connection con) {
		StringBuffer strsql = new StringBuffer();
		strsql.append(" select portalxml from t_sys_table_portal where  LOWER(username) = ?");
		ArrayList valueList = new ArrayList();
		valueList.add(username.toLowerCase());
		ContentDAO dao = new ContentDAO(con);
		LazyDynaBean bean = null;
		ResultSet rset =null;
		try {
			rset= dao.search(strsql.toString(),valueList);
			if (rset.next()) {
				bean = new LazyDynaBean();
				bean.set("portalxml", Sql_switcher.readMemo(rset, "portalxml"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
		}
		return bean;
	}
	
    public  void NWriteOutParameterXml(Connection conn,ArrayList nodenamevaluelist,String desc ,String username,String flag,String portalid){
    	 try{
    		Document doc=null;
    		boolean isupdate =false;
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	ContentDAO dao=new ContentDAO(conn);
   	        ArrayList values = new ArrayList();
	     	LazyDynaBean currentbean  = getRealConstantVo(username.toLowerCase(),conn);//flag=4:自助用户；=0:业务用户
	        if (currentbean!=null) {//非首次保存，做修改操作
	        			  doc = getPortalList(currentbean, portalid);
			        	  Element ele=null;
			        	  if(null!=portalList&&portalList.size()>0){//size>0:存在portal标签；反之，标识不存在
			        			  ele = (Element)portalList.get(0);
			        			  if(null!=ele) {
                                      ele.removeContent();
                                  }
			        	  }else{
			        		  Element element = (Element)doc.getRootElement();
			        		  ele = new Element("portal");
			        		  ele.setAttribute("id", portalid);
			        		  element.addContent(ele);
			        	  }
	        			  for(int j=0;j<nodenamevaluelist.size();j++){//遍历桌面设置信息
	       	    			 LazyDynaBean bean=(LazyDynaBean)nodenamevaluelist.get(j);
		        	    	 Element panelchild = new Element("panel");
		        	    	 panelchild.setAttribute("id",(String)bean.get("id"));  
		        	    	 panelchild.setAttribute("scroll",bean.get("scroll")==null|| "".equals(bean.get("scroll"))?"0":(String)bean.get("scroll"));
		        	    	 panelchild.setAttribute("twinkle",bean.get("twinkle")==null|| "".equals(bean.get("twinkle"))?"0":(String)bean.get("twinkle"));
		        	    	 panelchild.setAttribute("show",(String)bean.get("show")==null|| "".equals(bean.get("show"))?"0":(String)bean.get("show"));
		        	    	 ele.addContent(panelchild);
		        	      }
	        			  isupdate = true;
	        }
	        else{//初次保存，需重新创建XML
	        	 Element root = new Element("param");
        	     doc = new Document(root);
        	     Element portalidchild = new Element("portal");
        	     portalidchild.setAttribute("id", portalid);
        	     root.addContent(portalidchild);
        	     for(int i=0;i<nodenamevaluelist.size();i++){
        	    	 LazyDynaBean bean=(LazyDynaBean)nodenamevaluelist.get(i);
        	    	 Element panelchild = new Element("panel");
        	    	 panelchild.setAttribute("id",(String)bean.get("id"));  
        	    	 panelchild.setAttribute("scroll",bean.get("scroll")==null|| "".equals(bean.get("scroll"))?"0":(String)bean.get("scroll"));
        	    	 panelchild.setAttribute("twinkle",bean.get("twinkle")==null|| "".equals(bean.get("twinkle"))?"0":(String)bean.get("twinkle"));
        	    	 panelchild.setAttribute("show",(String)bean.get("show")==null|| "".equals(bean.get("show"))?"0":(String)bean.get("show"));
        	    	 portalidchild.addContent(panelchild);
        	     }
	        }
	         XMLOutputter outputter = new XMLOutputter();
	   	     Format format=Format.getPrettyFormat();
	   	     format.setEncoding("UTF-8");
	   	     outputter.setFormat(format);
	   	     xmls.setLength(0);
	   	     xmls.append(outputter.outputString(doc));
	   	     strsql.delete(0,strsql.length());
	   	     if(isupdate) {
                 strsql.append(" update  t_sys_table_portal set portalxml =? where username = ? ");
             } else {
                 strsql.append("insert into  t_sys_table_portal (portalxml,username) values(?,?)");
             }
	   	     values.clear();
	   	     values.add(xmls.toString());
     	     values.add(username);
     	     dao.update(strsql.toString(),values);
        }catch (Exception ee){
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
  } 
    
    public  void WriteOutParameterXml(String constant,Connection conn,ArrayList nodenamevaluelist,String desc,String user,String flag)
    {
    	 try{
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo(constant);
	        if (option_vo!=null) {
	        	 String str_value = option_vo.getString("str_value").toLowerCase();       	
	        	if(str_value!=null && str_value.trim().length()>0 && str_value.indexOf("xml")!=-1)
		        {
		    	      Document doc = PubFunc.generateDom(str_value);      //读入xml	  
		        	  XPath reportPath = XPath.newInstance("//user[@id=\"" + user.toLowerCase() + "\"]");// 取得根节点
		       	      List userlist=reportPath.selectNodes(doc);
		       	      if(userlist!=null && userlist.size()>0)
		       	      {
		       	        for(int i=0;i<userlist.size();i++)
		       	        {
		       	           Element usernode=(Element)userlist.get(i);
		       	           Element desknode=usernode.getChild("desk");
		       	    	   if(desknode==null)
		       	    	   {
		       	    		  Element deskchild = new Element("desk");
		       	    		  for(int j=0;j<nodenamevaluelist.size();j++)
			        	      {
			        	    	 ArrayList item=(ArrayList)nodenamevaluelist.get(j);
			        	    	 Element panelchild = new Element("panel");
			        	    	 for(int n=0;n<item.size();n++)
			        	    	 {
			        	    		LabelValueView labelvalue=(LabelValueView)item.get(n);
			        	    		panelchild.setAttribute(labelvalue.getLabel(),labelvalue.getValue());
			           	    	 }   
			        	    	 deskchild.addContent(panelchild);
			        	      }
		       	    		  usernode.addContent(deskchild);
		       	    	   }
		       	    	   else
		       	    	   {
		       	    		  desknode.removeChildren("panel");
		       	    		  for(int j=0;j<nodenamevaluelist.size();j++)
		        	          {
		        	    	    ArrayList item=(ArrayList)nodenamevaluelist.get(j);
		        	    	    Element panelchild = new Element("panel");
		        	    	    for(int n=0;n<item.size();n++)
		        	    	    {
		        	    		  LabelValueView labelvalue=(LabelValueView)item.get(n);
		        	    		  panelchild.setAttribute(labelvalue.getLabel(),labelvalue.getValue());
		           	    	    }   
		        	    	    desknode.addContent(panelchild);
		        	          }
		       	    	   }
		       	         }	
		       	         XMLOutputter outputter = new XMLOutputter();
	       	             Format format=Format.getPrettyFormat();
	       	             format.setEncoding("UTF-8");
	       	             outputter.setFormat(format);
	       	             xmls.append(outputter.outputString(doc));
	       	             strsql.delete(0,strsql.length());
/*	       	             strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='");
	       	             strsql.append(constant);
	       	             strsql.append("'");	  
	       	             pstmt.executeUpdate(strsql.toString());*/
	       	             //System.out.println(xmls.toString());
		       	         ContentDAO dao=new ContentDAO(conn);
		       	         RecordVo vo=new RecordVo("constant");
		       	         vo.setString("constant", constant);
		       	         vo.setString("str_value", xmls.toString());
		       	         dao.updateValueObject(vo);	       	             
		       	      }else
		       	      {
	    	             Element root = doc.getRootElement(); // 取得根节点
		           	     Element userchild = new Element("user");
		         	     userchild.setAttribute("id",user);
		         	     userchild.setAttribute("flag",flag);
		         	     root.addContent(userchild);
		         	     Element deskchild = new Element("desk");
		         	     userchild.addContent(deskchild);
		         	     
		         	     for(int i=0;i<nodenamevaluelist.size();i++)
		         	     {
		         	    	 ArrayList item=(ArrayList)nodenamevaluelist.get(i);
		         	    	 Element panelchild = new Element("panel");
		         	    	 for(int j=0;j<item.size();j++)
		         	    	 {
		         	    		LabelValueView labelvalue=(LabelValueView)item.get(j);
		         	    		panelchild.setAttribute(labelvalue.getLabel(),labelvalue.getValue());
		            	    	 }   
		         	    	 deskchild.addContent(panelchild);
		         	     }
		         	      XMLOutputter outputter = new XMLOutputter();
		       	          Format format=Format.getPrettyFormat();
		       	          format.setEncoding("UTF-8");
		       	          outputter.setFormat(format);
		       	          xmls.append(outputter.outputString(doc));
		       	          strsql.delete(0,strsql.length());
/*		       	          strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='");
		       	          strsql.append(constant);
		       	          strsql.append("'");
		       	          pstmt.executeUpdate(strsql.toString());*/
		       	          
		       	          ContentDAO dao=new ContentDAO(conn);
		       	          RecordVo vo=new RecordVo("constant");
		       	          vo.setString("constant", constant);
		       	          vo.setString("str_value", xmls.toString());
		       	          dao.updateValueObject(vo);
		       	          
		       	      }
		    	   
		    	}else{
		        	 Element root = new Element("param");
	        	     Document doc = new Document(root);
	        	     Element userchild = new Element("user");
	        	     userchild.setAttribute("id",user);
	        	     userchild.setAttribute("flag",flag);
	        	     root.addContent(userchild);
	        	     Element deskchild = new Element("desk");
	        	     userchild.addContent(deskchild);
	        	     
	        	     for(int i=0;i<nodenamevaluelist.size();i++)
	        	     {
	        	    	 ArrayList item=(ArrayList)nodenamevaluelist.get(i);
	        	    	 Element panelchild = new Element("panel");
	        	    	 for(int j=0;j<item.size();j++)
	        	    	 {
	        	    		LabelValueView labelvalue=(LabelValueView)item.get(j);
	        	    		panelchild.setAttribute(labelvalue.getLabel(),labelvalue.getValue());
	           	    	 }   
	        	    	 deskchild.addContent(panelchild);
	        	     }
	    	         XMLOutputter outputter = new XMLOutputter();
	        	     Format format=Format.getPrettyFormat();
	        	     format.setEncoding("UTF-8");
	        	     outputter.setFormat(format);
	        	     xmls.append(outputter.outputString(doc));
	        	     strsql.delete(0,strsql.length());
		       	     strsql.append("insert into  constant(constant,type,str_value,describe)values('" + constant + "','0','" + xmls.toString() + "','" + desc+ "')");
		       	  	ContentDAO dao = new ContentDAO(conn);
		       	  	dao.update(strsql.toString());	
		        }
	        }
	        else
	        {
	        	 Element root = new Element("param");
        	     Document doc = new Document(root);
        	     Element userchild = new Element("user");
        	     userchild.setAttribute("id",user);
        	     userchild.setAttribute("flag",flag);
        	     root.addContent(userchild);
        	     Element deskchild = new Element("desk");
        	     userchild.addContent(deskchild);
        	     
        	     for(int i=0;i<nodenamevaluelist.size();i++)
        	     {
        	    	 ArrayList item=(ArrayList)nodenamevaluelist.get(i);
        	    	 Element panelchild = new Element("panel");
        	    	 for(int j=0;j<item.size();j++)
        	    	 {
        	    		LabelValueView labelvalue=(LabelValueView)item.get(j);
        	    		panelchild.setAttribute(labelvalue.getLabel(),labelvalue.getValue());
           	    	 }   
        	    	 deskchild.addContent(panelchild);
        	     }
    	         XMLOutputter outputter = new XMLOutputter();
        	     Format format=Format.getPrettyFormat();
        	     format.setEncoding("UTF-8");
        	     outputter.setFormat(format);
        	     xmls.append(outputter.outputString(doc));
        	     strsql.delete(0,strsql.length());
	       	     strsql.append("insert into  constant(constant,type,str_value,describe)values('" + constant + "','0','" + xmls.toString() + "','" + desc+ "')");
	       	     ContentDAO dao = new ContentDAO(conn);
	       	     dao.update(strsql.toString());	
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
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 
	 * @param constant
	 * @param conn
	 * @param user
	 * @param id 1:公告；2：预警；3：花名册；4：常用条件；5：常用统计；6：登记表；7：报表列表；8：任务列表	 
	 * @return
	 */
	public HashMap  ReadOutParameterXml(Connection conn,String user,String id){
	    
	    HashMap map=new HashMap();
   	    try{
	        boolean isHave=false;
	        LazyDynaBean lbean = getRealConstantVo(user, conn);
	        if (lbean!=null){
	        	  if(null!=lbean) {
                      getPortalList(lbean,"02");
                  }
	       	      if(portalList!=null&&portalList.size()>0){
	   	    	  		Element ele = (Element)portalList.get(0);
	    	      		List childitems=ele.getChildren();//<portal
	    	      		if(childitems.size()>0){	
		    	      		for(int n=0;n<childitems.size();n++){	
		      					Element panelnode = (Element)childitems.get(n);
		    	      			String panelid = panelnode.getAttributeValue("id");
		    	      			if(null!=panelid&&panelid.equals(id)){
		    	      				map.put("id",panelid);
	 	    	      	    		map.put("scroll",panelnode.getAttributeValue("scroll"));
	 	    	      	    		map.put("twinkle",panelnode.getAttributeValue("twinkle"));
	 	    	      	    		map.put("show",panelnode.getAttributeValue("show"));
	 	    	      	    		isHave=true;
	 	    	      	    		break;
		    	      			}
		    	      		}
	    	      		}	
	      		 }	
	        }
	        if(!isHave)
	        {
	        	map.put("scroll", "0");
	        	map.put("twinkle", "0");
	        	map.put("show", "1");
	        }
         }catch (Exception ee)
         {
            ee.printStackTrace();
            GeneralExceptionHandler.Handle(ee);
         }
        return map;
    }  
	
	
	public String ReadOutParameterXmlHideValueAndShowOrder(Connection conn,String  username,String portalid){
	    String str="";
	    String showorder="";
   	    try{
   	    	  LazyDynaBean lbean  = getRealConstantVo(username.toLowerCase(), conn);
   	    	  if(null!=lbean) {
                  getPortalList(lbean,portalid);
              }
	   	      if(portalList!=null&&portalList.size()>0){
	  	        	Element nodeitems=(Element)portalList.get(0);
		      		List portalitems=nodeitems.getChildren();
		      		for(int i =0;i<portalitems.size();i++ ){
		      	    	Element element=(Element)portalitems.get(i);
		      	    	String cur_id=element.getAttributeValue("id");
		      	    	String show = element.getAttributeValue("show");
		      	    	if("0".equals(show)) {
                            str+=cur_id+",";
                        }
		      	    	if("1".equals(show)) {
                            showorder+=cur_id+",";
                        }
		      		}
	          }
         }catch (Exception ee){
            ee.printStackTrace();
            GeneralExceptionHandler.Handle(ee);
         }
        return str+"`"+showorder;
    }
	
}
