package com.hjsj.hrms.interfaces.general;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.DynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.List;

public class CreateCodeTreeXML {
	/**
	 * 创建代码树
	 * @return xmls
	 * @throws Exception
	 */
	public String outCodeTree(String codesetid,String codeitemid,String parentid)throws GeneralException
	{
		StringBuffer xmls = new StringBuffer();
		Element root = new Element("TreeNode");

		root.setAttribute("id","$$00");
		root.setAttribute("text","root");
		root.setAttribute("title","codeitem");
		Document myDocument = new Document(root);
		try{
			FieldItem item = DataDictionary.getFieldItem(codesetid);
			String code = item.getCodesetid();
			StringBuffer buf = new StringBuffer();
			buf.append("select * from codeitem where codesetid='");
			buf.append(code+"'");
			if(parentid!=null&&parentid.trim().length()>0){
				buf.append(" and parentid='");
				buf.append(codeitemid+"'");
			}
			List rs=ExecuteSQL.executeMyQuery(buf.toString()); 
			for(int i=0;i<rs.size();i++){
				DynaBean rec=(DynaBean)rs.get(i);
				String codeid=(String)rec.get("codsetid");
				codeid=codeid!=null?codeid:"";

				String itemid=(String)rec.get("codeitemid");
				itemid=itemid!=null?itemid:"";

				String codeitemdesc=(String)rec.get("codeitemdesc");
				codeitemdesc=codeitemdesc!=null?codeitemdesc:"";

				String parid=(String)rec.get("parentid");
				parid=parid!=null?parid:"";

				String childid=(String)rec.get("childid");
				childid=childid!=null?childid:"";
				StringBuffer xml = new StringBuffer();
				xml.append("/general/inform/code_tree.jsp?codesetid=");
				xml.append(codesetid);
				xml.append("&codeitemid=");
				xml.append(itemid);
				xml.append("&parentid=");
				xml.append(parid);
				if(childid.equalsIgnoreCase(itemid)&&parid.equalsIgnoreCase(itemid)){
					Element child = new Element("TreeNode");
					child.setAttribute("id",itemid);
					child.setAttribute("text", codeitemdesc);
					child.setAttribute("title",codeitemdesc);
					child.setAttribute("target","mil_body");
					child.setAttribute("href", "/general/inform/get_data_table.do?b_query=link&flag=1&a_code="+code+":"+codesetid+":"+itemid);
					child.setAttribute("icon","/images/cards.bmp");
					root.addContent(child);
				}else{
					if(parentid!=null&&parentid.trim().length()>0){
						if(childid.equalsIgnoreCase(itemid)){
							Element child = new Element("TreeNode");
							child.setAttribute("id",itemid);
							child.setAttribute("text", codeitemdesc);
							child.setAttribute("title",codeitemdesc);
							child.setAttribute("target","mil_body");
							child.setAttribute("href", "/general/inform/get_data_table.do?b_query=link&flag=1&a_code="+code+":"+codesetid+":"+itemid);
							child.setAttribute("icon","/images/cards.bmp");
							root.addContent(child);
						}else{
							if(!parid.equalsIgnoreCase(itemid)){
								Element child = new Element("TreeNode");
								child.setAttribute("id",itemid);
								child.setAttribute("text", codeitemdesc);
								child.setAttribute("title",codeitemdesc);
								child.setAttribute("target","mil_body");
								child.setAttribute("href", "/general/inform/get_data_table.do?b_query=link&flag=1&a_code="+code+":"+codesetid+":"+itemid);
								child.setAttribute("xml",xml.toString());
								child.setAttribute("icon","/images/cards.bmp");
								root.addContent(child);
							}
						}
					}else{
						if(parid.equalsIgnoreCase(itemid)){
							Element child = new Element("TreeNode");
							child.setAttribute("id",itemid);
							child.setAttribute("text", codeitemdesc);
							child.setAttribute("title",codeitemdesc);
							child.setAttribute("target","mil_body");
							child.setAttribute("href", "/general/inform/get_data_table.do?b_query=link&flag=1&a_code="+code+":"+codesetid+":"+itemid);
							child.setAttribute("xml",xml.toString());
							child.setAttribute("icon","/images/cards.bmp");
							root.addContent(child);
						}
					}
				}
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		}catch (Exception ee){
			ee.printStackTrace();
		}
		return xmls.toString();        
	}
	/**
	 * 创建代码树
	 * @return xmls
	 * @throws Exception
	 */
	public String outCodeOrgTree(String codesetid,String codeitemid,String parentid)throws GeneralException
	{
		StringBuffer xmls = new StringBuffer();
		Element root = new Element("TreeNode");

		root.setAttribute("id","$$00");
		root.setAttribute("text","root");
		root.setAttribute("title","codeitem");
		Document myDocument = new Document(root);
		try{
			FieldItem item = DataDictionary.getFieldItem(codesetid);
			String code = item.getCodesetid();
			StringBuffer buf = new StringBuffer();
			buf.append("select * from codeitem where codesetid='");
			buf.append(code+"'");
			if(parentid!=null&&parentid.trim().length()>0){
				buf.append(" and parentid='");
				buf.append(codeitemid+"'");
			}
			List rs=ExecuteSQL.executeMyQuery(buf.toString()); 
			for(int i=0;i<rs.size();i++){
				DynaBean rec=(DynaBean)rs.get(i);
				String codeid=(String)rec.get("codsetid");
				codeid=codeid!=null?codeid:"";

				String itemid=(String)rec.get("codeitemid");
				itemid=itemid!=null?itemid:"";

				String codeitemdesc=(String)rec.get("codeitemdesc");
				codeitemdesc=codeitemdesc!=null?codeitemdesc:"";

				String parid=(String)rec.get("parentid");
				parid=parid!=null?parid:"";

				String childid=(String)rec.get("childid");
				childid=childid!=null?childid:"";
				StringBuffer xml = new StringBuffer();
				xml.append("/general/inform/orgcode_tree.jsp?codesetid=");
				xml.append(codesetid);
				xml.append("&codeitemid=");
				xml.append(itemid);
				xml.append("&parentid=");
				xml.append(parid);
				if(childid.equalsIgnoreCase(itemid)&&parid.equalsIgnoreCase(itemid)){
					Element child = new Element("TreeNode");
					child.setAttribute("id",itemid);
					child.setAttribute("text", codeitemdesc);
					child.setAttribute("title",codeitemdesc);
					child.setAttribute("target","ril_body1");
					child.setAttribute("href", "/general/inform/get_data_table.do?b_rmain=link&flag=1&a_code="+code+":"+codesetid+":"+itemid);
					child.setAttribute("icon","/images/cards.bmp");
					root.addContent(child);
				}else{
					if(parentid!=null&&parentid.trim().length()>0){
						if(childid.equalsIgnoreCase(itemid)){
							Element child = new Element("TreeNode");
							child.setAttribute("id",itemid);
							child.setAttribute("text", codeitemdesc);
							child.setAttribute("title",codeitemdesc);
							child.setAttribute("target","ril_body1");
							child.setAttribute("href", "/general/inform/get_data_table.do?b_rmain=link&flag=1&a_code="+code+":"+codesetid+":"+itemid);
							child.setAttribute("icon","/images/cards.bmp");
							root.addContent(child);
						}else{
							if(!parid.equalsIgnoreCase(itemid)){
								Element child = new Element("TreeNode");
								child.setAttribute("id",itemid);
								child.setAttribute("text", codeitemdesc);
								child.setAttribute("title",codeitemdesc);
								child.setAttribute("target","ril_body1");
								child.setAttribute("href", "/general/inform/get_data_table.do?b_rmain=link&flag=1&a_code="+code+":"+codesetid+":"+itemid);
								child.setAttribute("xml",xml.toString());
								child.setAttribute("icon","/images/cards.bmp");
								root.addContent(child);
							}
						}
					}else{
						if(parid.equalsIgnoreCase(itemid)){
							Element child = new Element("TreeNode");
							child.setAttribute("id",itemid);
							child.setAttribute("text", codeitemdesc);
							child.setAttribute("title",codeitemdesc);
							child.setAttribute("target","ril_body1");
							child.setAttribute("href", "/general/inform/get_data_table.do?b_rmain=link&flag=1&a_code="+code+":"+codesetid+":"+itemid);
							child.setAttribute("xml",xml.toString());
							child.setAttribute("icon","/images/cards.bmp");
							root.addContent(child);
						}
					}
				}
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		}catch (Exception ee){
			ee.printStackTrace();
		}
		return xmls.toString();        
	}
	   /**
     * 创建代码树
     * @return xmls
     * @throws Exception
     */
	   public String outCodeUnitTree(String codesetid,String codeitemid,
			   String parentid,String infor)throws GeneralException
	    {
	        StringBuffer xmls = new StringBuffer();
	        Element root = new Element("TreeNode");
	        
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","codeitem");
	        Document myDocument = new Document(root);
	        try{
	          FieldItem item = DataDictionary.getFieldItem(codesetid);
	          String code = item.getCodesetid();
	          StringBuffer buf = new StringBuffer();
	          buf.append("select * from codeitem where codesetid='");
	          buf.append(code+"'");
	          if(parentid!=null&&parentid.trim().length()>0){
	        	  buf.append(" and parentid='");
	        	  buf.append(codeitemid+"'");
	          }
	          List rs=ExecuteSQL.executeMyQuery(buf.toString()); 
	          for(int i=0;i<rs.size();i++){
	        	  DynaBean rec=(DynaBean)rs.get(i);
	        	  String codeid=(String)rec.get("codsetid");
	        	  codeid=codeid!=null?codeid:"";
	        	  
	        	  String itemid=(String)rec.get("codeitemid");
	        	  itemid=itemid!=null?itemid:"";
	        	  
	        	  String codeitemdesc=(String)rec.get("codeitemdesc");
	        	  codeitemdesc=codeitemdesc!=null?codeitemdesc:"";
	        	  
	        	  String parid=(String)rec.get("parentid");
	        	  parid=parid!=null?parid:"";
	        	  
	        	  String childid=(String)rec.get("childid");
	        	  childid=childid!=null?childid:"";
	        	  StringBuffer xml = new StringBuffer();
				  xml.append("/org/orgdata/code_tree.jsp?codesetid=");
				  xml.append(codesetid);
				  xml.append("&infor="+infor+"&codeitemid=");
				  xml.append(itemid);
				  xml.append("&parentid=");
				  xml.append(parid);
	        	  if(childid.equalsIgnoreCase(itemid)&&parid.equalsIgnoreCase(itemid)){
	        			Element child = new Element("TreeNode");
	        			child.setAttribute("id",itemid);
	        			child.setAttribute("text", codeitemdesc);
	        			child.setAttribute("title",codeitemdesc);
	        			child.setAttribute("target","mil_body");
	        			child.setAttribute("href", "/org/orgdata/orgdata.do?b_query=link&infor="+infor+"&a_code="+code+":"+codesetid+":"+itemid);
	        			child.setAttribute("icon","/images/cards.bmp");
	        			root.addContent(child);
	        	  }else{
	        		  if(parentid!=null&&parentid.trim().length()>0){
	        			  if(childid.equalsIgnoreCase(itemid)){
	        				  	Element child = new Element("TreeNode");
	  	        				child.setAttribute("id",itemid);
	  	        				child.setAttribute("text", codeitemdesc);
	  	        				child.setAttribute("title",codeitemdesc);
	  	        				child.setAttribute("target","mil_body");
	  	        				child.setAttribute("href", "/org/orgdata/orgdata.do?b_query=link&infor="+infor+"&a_code="+code+":"+codesetid+":"+itemid);
	  	        				child.setAttribute("icon","/images/cards.bmp");
	  	        				root.addContent(child);
	        			  }else{
	        				  if(!parid.equalsIgnoreCase(itemid)){
	        				  	Element child = new Element("TreeNode");
	        				  	child.setAttribute("id",itemid);
	        				  	child.setAttribute("text", codeitemdesc);
	        				  	child.setAttribute("title",codeitemdesc);
	        				  	child.setAttribute("target","mil_body");
	        				  	child.setAttribute("href", "/org/orgdata/orgdata.do?b_query=link&infor="+infor+"&a_code="+code+":"+codesetid+":"+itemid);
	        				  	child.setAttribute("xml",xml.toString());
	        				  	child.setAttribute("icon","/images/cards.bmp");
	        				  	root.addContent(child);
	        				  }
	        			  }
	        		  }else{
	        			  if(parid.equalsIgnoreCase(itemid)){
	        				  Element child = new Element("TreeNode");
	        				  child.setAttribute("id",itemid);
	        				  child.setAttribute("text", codeitemdesc);
	        				  child.setAttribute("title",codeitemdesc);
	        				  child.setAttribute("target","mil_body");
	        				  child.setAttribute("href", "/org/orgdata/orgdata.do?b_query=link&infor="+infor+"&a_code="+code+":"+codesetid+":"+itemid);
	        				  child.setAttribute("xml",xml.toString());
	        				  child.setAttribute("icon","/images/cards.bmp");
	        				  root.addContent(child);
	        			  }
	        		  }
	        	  }
	          }

	          XMLOutputter outputter = new XMLOutputter();
	          Format format=Format.getPrettyFormat();
	          format.setEncoding("UTF-8");
	          outputter.setFormat(format);
	          xmls.append(outputter.outputString(myDocument));
	        }catch (Exception ee){
	          ee.printStackTrace();
	        }
	      return xmls.toString();        
	    }
	   /**
	     * 创建代码树
	     * @return xmls
	     * @throws Exception
	     */
		   public String outCodePosTree(String codesetid,String codeitemid,
				   String parentid,String infor)throws GeneralException
		    {
		        StringBuffer xmls = new StringBuffer();
		        Element root = new Element("TreeNode");
		        
		        root.setAttribute("id","$$00");
		        root.setAttribute("text","root");
		        root.setAttribute("title","codeitem");
		        Document myDocument = new Document(root);
		        try{
		          FieldItem item = DataDictionary.getFieldItem(codesetid);
		          String code = item.getCodesetid();
		          StringBuffer buf = new StringBuffer();
		          buf.append("select * from codeitem where codesetid='");
		          buf.append(code+"'");
		          if(parentid!=null&&parentid.trim().length()>0){
		        	  buf.append(" and parentid='");
		        	  buf.append(codeitemid+"'");
		          }
		          List rs=ExecuteSQL.executeMyQuery(buf.toString()); 
		          for(int i=0;i<rs.size();i++){
		        	  DynaBean rec=(DynaBean)rs.get(i);
		        	  String codeid=(String)rec.get("codsetid");
		        	  codeid=codeid!=null?codeid:"";
		        	  
		        	  String itemid=(String)rec.get("codeitemid");
		        	  itemid=itemid!=null?itemid:"";
		        	  
		        	  String codeitemdesc=(String)rec.get("codeitemdesc");
		        	  codeitemdesc=codeitemdesc!=null?codeitemdesc:"";
		        	  
		        	  String parid=(String)rec.get("parentid");
		        	  parid=parid!=null?parid:"";
		        	  
		        	  String childid=(String)rec.get("childid");
		        	  childid=childid!=null?childid:"";
		        	  StringBuffer xml = new StringBuffer();
					  xml.append("/org/orgdata/get_code_tree.jsp?codesetid=");
					  xml.append(codesetid);
					  xml.append("&infor="+infor+"&codeitemid=");
					  xml.append(itemid);
					  xml.append("&parentid=");
					  xml.append(parid);
		        	  if(childid.equalsIgnoreCase(itemid)&&parid.equalsIgnoreCase(itemid)){
		        			Element child = new Element("TreeNode");
		        			child.setAttribute("id",itemid);
		        			child.setAttribute("text", codeitemdesc);
		        			child.setAttribute("title",codeitemdesc);
		        			child.setAttribute("target","ril_body1");
		        			child.setAttribute("href", "/org/orgdata/orgdata.do?b_rmain=link&infor="+infor+"&a_code="+code+":"+codesetid+":"+itemid);
		        			child.setAttribute("icon","/images/cards.bmp");
		        			root.addContent(child);
		        	  }else{
		        		  if(parentid!=null&&parentid.trim().length()>0){
		        			  if(childid.equalsIgnoreCase(itemid)){
		        				  	Element child = new Element("TreeNode");
		  	        				child.setAttribute("id",itemid);
		  	        				child.setAttribute("text", codeitemdesc);
		  	        				child.setAttribute("title",codeitemdesc);
		  	        				child.setAttribute("target","ril_body1");
		  	        				child.setAttribute("href", "/org/orgdata/orgdata.do?b_rmain=link&infor="+infor+"&a_code="+code+":"+codesetid+":"+itemid);
		  	        				child.setAttribute("icon","/images/cards.bmp");
		  	        				root.addContent(child);
		        			  }else{
		        				  if(!parid.equalsIgnoreCase(itemid)){
		        				  	Element child = new Element("TreeNode");
		        				  	child.setAttribute("id",itemid);
		        				  	child.setAttribute("text", codeitemdesc);
		        				  	child.setAttribute("title",codeitemdesc);
		        				  	child.setAttribute("target","ril_body1");
		        				  	child.setAttribute("href", "/org/orgdata/orgdata.do?b_rmain=link&infor="+infor+"&a_code="+code+":"+codesetid+":"+itemid);
		        				  	child.setAttribute("xml",xml.toString());
		        				  	child.setAttribute("icon","/images/cards.bmp");
		        				  	root.addContent(child);
		        				  }
		        			  }
		        		  }else{
		        			  if(parid.equalsIgnoreCase(itemid)){
		        				  Element child = new Element("TreeNode");
		        				  child.setAttribute("id",itemid);
		        				  child.setAttribute("text", codeitemdesc);
		        				  child.setAttribute("title",codeitemdesc);
		        				  child.setAttribute("target","ril_body1");
		        				  child.setAttribute("href", "/org/orgdata/orgdata.do?b_rmain=link&infor="+infor+"&a_code="+code+":"+codesetid+":"+itemid);
		        				  child.setAttribute("xml",xml.toString());
		        				  child.setAttribute("icon","/images/cards.bmp");
		        				  root.addContent(child);
		        			  }
		        		  }
		        	  }
		          }

		          XMLOutputter outputter = new XMLOutputter();
		          Format format=Format.getPrettyFormat();
		          format.setEncoding("UTF-8");
		          outputter.setFormat(format);
		          xmls.append(outputter.outputString(myDocument));
		        }catch (Exception ee){
		          ee.printStackTrace();
		        }
		      return xmls.toString();        
		    }
}
