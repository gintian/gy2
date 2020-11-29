package com.hjsj.hrms.interfaces.report;

import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.Iterator;
import java.util.List;

public class ReportParameterXml {
   public ReportParamterVo ReadOutParameterXml(String xmlContent,String xpath)
   {
	   ReportParamterVo revo=new ReportParamterVo();
	   
	   if(xmlContent!=null&&xmlContent.trim().length()>0)
	   {
		   try
		   {
		       Document doc=PubFunc.generateDom(xmlContent);//读入xml
		       XPath reportPath = XPath.newInstance(xpath);// 取得根节点
		      
		       List childlist=reportPath.selectNodes(doc);  
		       
			   Iterator i = childlist.iterator();
			   if(i.hasNext())
			   {
				   /**报表基本参数**/
				   Element childR=(Element)i.next();
				   revo.setName(childR.getAttributeValue("name")); //报表名称
				   
				   revo.setPagetype(childR.getAttributeValue("pagetype"));//报表纸张
				   revo.setUnit(childR.getAttributeValue("unit"));//报表长度单位
				   revo.setOrientation(childR.getAttributeValue("orientation"));//纸张方向
				   revo.setTop(childR.getAttributeValue("top"));//报表头边距
				   revo.setLeft(childR.getAttributeValue("left"));//报表左边距
				   revo.setRight(childR.getAttributeValue("right"));//报表右边距				   
				   revo.setBottom(childR.getAttributeValue("bottom"));//报表尾边距
				   revo.setValue(childR.getAttributeValue("value"));//值
				   revo.setWidth(childR.getAttributeValue("width"));//纸的长宽
				   revo.setHeight(childR.getAttributeValue("height"));//纸的长宽				  			   
				   /**节点,报表标题**/
				   Element title_item=childR.getChild("title");
				   if(title_item!=null)
					   revo.setTitle_c(title_item.getAttributeValue("content"));				  
				   /**节点,报表表头**/
				   Element head_item=childR.getChild("head");
				   if(head_item!=null)
					   revo.setHead_c(head_item.getAttributeValue("content"));
				   
				   /**节点,报表表尾**/
				   Element tile_item=childR.getChild("tile");
				   if(tile_item!=null)
					   revo.setTile_c(tile_item.getAttributeValue("content"));
				   
				   /**节点,报表表体**/
				   Element body_item=childR.getChild("body");
				   if(body_item!=null)
					   revo.setBody_c(body_item.getAttributeValue("content"));				   
			   }
		    
		   }catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   return revo;
   }
   public  String  WriteOutParameterXml(String xmlContent,String xpath,ReportParamterVo revo)
   {
	   
	   if(xmlContent!=null&&xmlContent.length()>0)
	   {
		   try{
		   Document doc=PubFunc.generateDom(xmlContent);//读入xml
	       XPath reportPath = XPath.newInstance(xpath);// 取得根节点
	       List childlist=reportPath.selectNodes(doc);
	       Iterator i = childlist.iterator();
	       if(i.hasNext())
	       {
	    	   Element childR=(Element)i.next();	    	  
	    	   childR.setAttribute("name",revo.getName());
	    	   childR.setAttribute("pagetype",revo.getPagetype());
	    	   childR.setAttribute("unit",revo.getUnit());
	    	   childR.setAttribute("orientation",revo.getOrientation());
	    	   childR.setAttribute("top",revo.getTop());
	    	   childR.setAttribute("left",revo.getLeft());
	    	   childR.setAttribute("right",revo.getRight());
	    	   childR.setAttribute("bottom",revo.getBottom());
	    	   childR.setAttribute("value",revo.getValue());
	    	   childR.setAttribute("width",revo.getWidth());
	    	   childR.setAttribute("height",revo.getHeight());
	    	   		  
			   /**节点,报表标题**/
			   Element title_item=childR.getChild("title");
			   title_item.setAttribute("content",revo.getTitle_c());	   
			   
			   /**节点,报表表头**/
			   Element head_item=childR.getChild("head");
			   head_item.setAttribute("content",revo.getHead_c());		   
			   
			   /**节点,报表表尾**/
			   Element tile_item=childR.getChild("tile");
			   tile_item.setAttribute("content",revo.getTile_c());		  
			   
			   /**节点,报表表体**/
			   Element body_item=childR.getChild("body");
			   body_item.setAttribute("content",revo.getBody_c());			   
			   /**节点，页脚**/			  		   
			   
			   XMLOutputter outputter = new XMLOutputter();
  	           Format format=Format.getPrettyFormat();
  	           format.setEncoding("UTF-8");
  	           outputter.setFormat(format);  	           
  	           xmlContent=outputter.outputString(doc);  	        
	       }
	       
		   }catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   
	   return xmlContent;
   }
}
