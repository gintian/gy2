/*
 * Created on 2006-5-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.xmlparameter;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetOrgOptionParameter {
	/*
	 * 子节点的name和text，保存的是含有label和value
	 * 两个属性的bean
	 * */
    private ArrayList nodenamevaluelist=new ArrayList();
    public SetOrgOptionParameter(){
    }
    public SetOrgOptionParameter(ArrayList nodenamevaluelist) {
        this.nodenamevaluelist=nodenamevaluelist;
    } 
    public ArrayList  ReadOutParameterXml(String constant,Connection conn,UserView userView,String dbnames)
    {
	     ArrayList resultnodenamevaluelist=new ArrayList();
   	    try{
	        RecordVo option_vo=ConstantParamter.getRealConstantVo(constant);
	        if (option_vo!=null) {
	        	  String str_value = option_vo.getString("str_value");
	        	if(str_value!=null && str_value.trim().length()>0 && str_value.indexOf("xml")!=-1)
	        	{		        	
	    	      Document doc = PubFunc.generateDom(str_value);      //读入xml	    	    
	    	      Element root = doc.getRootElement(); // 取得根节点
	    	      List childnode=root.getChildren(); 
	    	      for(int i=0;i<childnode.size();i++)
	    	      {
	    	      	Element node=(Element)childnode.get(i);
	    	      	String name = node.getName();
	    	      	String text = node.getText();
	    	      	if("dbnames".equalsIgnoreCase(name)){
	    	      		if(!userView.hasTheDbName(text)){
	    	      			text=dbnames;
	    	      		}
	    	      	}
	    	      	resultnodenamevaluelist.add(new LabelValueView(text,name));
	    	      }
	           	}	        	
	        }
         }catch (Exception ee)
         {
            ee.printStackTrace();
            GeneralExceptionHandler.Handle(ee);
         }
        return resultnodenamevaluelist;
    }
    public ArrayList  ReadOutParameterXml(String constant)
    {
    	Connection conn=null;
	    
    	ArrayList resultnodenamevaluelist=new ArrayList();
   	    try{
   	    	conn=AdminDb.getConnection();
	        RecordVo option_vo=ConstantParamter.getRealConstantVo(constant);
	        if (option_vo!=null) {
	        	String str_value = option_vo.getString("str_value");//取消转小写，数据库转成小写后匹配不上 .toLowerCase(); guodd 2015-1-27
	        	if(str_value!=null && str_value.trim().length()>0 && str_value.indexOf("xml")!=-1)
	        	{		        	
	    	      Document doc = PubFunc.generateDom(str_value);     //读入xml	    	    
	    	      Element root = doc.getRootElement(); // 取得根节点
	    	      List childnode=root.getChildren(); 
	    	      for(int i=0;i<childnode.size();i++)
	    	      {
	    	      	Element node=(Element)childnode.get(i);
	    	      	resultnodenamevaluelist.add(new LabelValueView(node.getText().replace("＃", "#"),node.getName()));
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
        	 PubFunc.closeDbObj(conn);
	     } 
        return resultnodenamevaluelist;
    }
    public HashMap  ReadOutParameterXml(String constant,boolean bl,String dbname,UserView userView)
    {
    	ArrayList resultnodenamevaluelist=ReadOutParameterXml(constant);
    	HashMap hashmap=new HashMap(); 
    	if(resultnodenamevaluelist.isEmpty() || resultnodenamevaluelist.size()==0)
		{
    		//hashmap.put("cellletteralignleft","noalign-left");
        	//hashmap.put("cellletteralignright","noalign-right");
        	//hashmap.put("cellletteraligncenter","noalign-center");
    		hashmap.put("cellletteralignleft","1");
        	hashmap.put("celllettervaligncenter","novalign-center");
        	hashmap.put("cellletterfitsize","false");
        	hashmap.put("cellletterfitline","false");
        	hashmap.put("fontfamily","Simsun");
        	hashmap.put("fontstyle","general");
        	hashmap.put("fontsize","12");
        	hashmap.put("fontcolor","#000000");
        	hashmap.put("cellhspacewidth","10");
        	hashmap.put("cellvspacewidth","10");
        	hashmap.put("celllinestrokewidth","1");
        	hashmap.put("cellshape","rect");
        	hashmap.put("cellwidth","80");
        	hashmap.put("cellheight","60");
        	hashmap.put("isshowpersonconut","false");
        	hashmap.put("isshoworgconut","false");
        	hashmap.put("isshowpersonname","false");
        	hashmap.put("namesinglecell","false");
        	hashmap.put("cellcolor","#FFFFA6");
        	hashmap.put("cellaspect","true");
        	hashmap.put("graph3d","false");
        	hashmap.put("graphaspect","true");
        	hashmap.put("dbnames",dbname);
        	hashmap.put("rectwidth","10");
        	hashmap.put("font_style", "font-style=\"general\"");
        	hashmap.put("isshowdeptname", "false");
        	hashmap.put("isshowdeptname", "false");
        	hashmap.put("deptlevel", "-1");
        	hashmap.put("unitlevel", "-1");
        	hashmap.put("isshowposname", "false");
		}
    	else
    	{
    		for(int i=0;i<resultnodenamevaluelist.size();i++)
			{
				LabelValueView labelvalue=(LabelValueView)resultnodenamevaluelist.get(i);
				if("fontfamily".equalsIgnoreCase(labelvalue.getLabel()))
				{
					if(labelvalue.getValue()!=null&& "song".equals(labelvalue.getValue()))
					{
						hashmap.put(labelvalue.getLabel(),"");
					}else if(labelvalue.getValue()!=null&& "kaiti".equals(labelvalue.getValue()))
					{
						hashmap.put(labelvalue.getLabel(),"KaiTi_GB2312");
					}else if(labelvalue.getValue()!=null&& "lishu".equals(labelvalue.getValue()))
					{
						hashmap.put(labelvalue.getLabel(),"LiSu");
					}else if(labelvalue.getValue()!=null&& "youyuan".equals(labelvalue.getValue()))
					{
						hashmap.put(labelvalue.getLabel(),"YouYuan");
					}else{
						hashmap.put(labelvalue.getLabel(),"");
					}
				}else if("fontstyle".equalsIgnoreCase(labelvalue.getLabel()))
			    {
						String value=labelvalue.getValue();
						if(value!=null&& "italic".equalsIgnoreCase(value))//斜体
						{
							hashmap.put("font_style", "font-style=\"italic\"");
						}else if(value!=null&& "thick".equalsIgnoreCase(value))//粗体
						{
							hashmap.put("font_style", "font-weight=\"bold\" ");
						}else if(value!=null&& "italicthick".equalsIgnoreCase(value))//斜粗体
						{
							hashmap.put("font_style", "font-style=\"italic\" font-weight=\"bold\"");
						}else
						{
							hashmap.put("font_style", "");
						}
				}else if("dbnames".equalsIgnoreCase(labelvalue.getLabel())){
					String dbpre = labelvalue.getValue();
					if(!userView.hasTheDbName(dbpre)){
						dbpre = dbname;
					}
					hashmap.put(labelvalue.getLabel(),dbpre);
				}
				else
				hashmap.put(labelvalue.getLabel(),labelvalue.getValue());
			}
    		hashmap.put("rectwidth","10");
    	}
        return hashmap;
    }
    public  void WriteOutParameterXml(String constant,Connection conn,ArrayList nodenamevaluelist)
    {
    	WriteOutParameterXml(constant,conn,nodenamevaluelist,"parameter");
    }
    public  void WriteOutParameterXml(String constant,Connection conn,ArrayList nodenamevaluelist,String desc)
    {
    	 try{
    		ContentDAO dao = new ContentDAO(conn);
	    	StringBuffer xmls=new StringBuffer();	       
	     	StringBuffer strsql=new StringBuffer();
	     	RecordVo option_vo=ConstantParamter.getRealConstantVo(constant);
	        if (option_vo!=null) {
	        	String str_value = option_vo.getString("str_value");  
	        	if(str_value!=null && str_value.trim().length()>0 && str_value.indexOf("xml")!=-1)
		        {
		    	      Document doc = PubFunc.generateDom(str_value);  //读入xml
		    	      Element root = doc.getRootElement(); // 取得根节点
		    	      for(int i=0;i<nodenamevaluelist.size();i++)
		    	      {
		    	      	LabelValueView labelvalue=(LabelValueView)nodenamevaluelist.get(i);
		    	      	Element node=root.getChild(labelvalue.getLabel());
		    	      	if(node==null)
		    	      	{
		    	      		Element child = new Element(labelvalue.getLabel());
		    	        	child.setText(labelvalue.getValue());
		    	        	root.addContent(child);	
		    	      	}else
		    	      	{
		    	      		node.setText(labelvalue.getValue());
		    	      	}
		    	      }
		    	      XMLOutputter outputter = new XMLOutputter();
	       	          Format format=Format.getPrettyFormat();
	       	          format.setEncoding("UTF-8");
	       	          outputter.setFormat(format);
	       	          xmls.append(outputter.outputString(doc));
	       	          strsql.delete(0,strsql.length());
	       	          strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='");
	       	          strsql.append(constant);
	       	          strsql.append("'");
	       	          dao.update(strsql.toString());
		    	}else{
	        		 Element root = new Element("parameter");
	        	     Document doc = new Document(root);
	        	     for(int i=0;i<nodenamevaluelist.size();i++)
	        	     {
	        	    	LabelValueView labelvalue=(LabelValueView)nodenamevaluelist.get(i);
	        	     	Element child=new Element(labelvalue.getLabel());
	        	     	child.setText(labelvalue.getValue());
	        	     	root.addContent(child);
	        	     }
        	         XMLOutputter outputter = new XMLOutputter();
	        	     Format format=Format.getPrettyFormat();
	        	     format.setEncoding("UTF-8");
	        	     outputter.setFormat(format);
	        	     xmls.append(outputter.outputString(doc));
	        	     strsql.delete(0,strsql.length());
	        	     strsql.append("update  constant set str_value='" + xmls.toString() + "' where constant='");
	        	     strsql.append(constant);
	        	     strsql.append("'");
	        	     dao.update(strsql.toString());
	        	}
	        }
	        else
	        {
	        	 Element root = new Element("parameter");
        	     Document doc = new Document(root);
        	     for(int i=0;i<nodenamevaluelist.size();i++)
        	     {
        	    	LabelValueView labelvalue=(LabelValueView)nodenamevaluelist.get(i);
        	     	Element child=new Element(labelvalue.getLabel());
        	     	child.setText(labelvalue.getValue());
        	     	root.addContent(child);
        	     }
    	         XMLOutputter outputter = new XMLOutputter();
        	     Format format=Format.getPrettyFormat();
        	     format.setEncoding("UTF-8");
        	     outputter.setFormat(format);
        	     xmls.append(outputter.outputString(doc));
        	     strsql.delete(0,strsql.length());
	       	     strsql.append("insert into  constant(constant,type,str_value,describe)values('" + constant + "','0','" + xmls.toString() + "','" + desc+ "')");
	       	     dao.update(strsql.toString());	
	        }
        }catch (Exception ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
  }     
	/**
	 * @return Returns the nodenamevaluelist.
	 */
	public ArrayList getNodenamevaluelist() {
		return nodenamevaluelist;
	}
	/**
	 * @param nodenamevaluelist The nodenamevaluelist to set.
	 */
	public void setNodenamevaluelist(ArrayList nodenamevaluelist) {
		this.nodenamevaluelist = nodenamevaluelist;
	}
}
