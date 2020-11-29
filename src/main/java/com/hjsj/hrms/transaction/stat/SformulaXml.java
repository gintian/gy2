package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SformulaXml {
	private Connection conn;
	private Document doc;
	private String xml;
	private String snameid;
	private ContentDAO dao;
	public SformulaXml(Connection conn,String snameid){
		this.conn=conn;
		this.dao=new ContentDAO(conn);
		this.snameid=snameid;
		init(snameid);
		try{
			doc=PubFunc.generateDom(xml.toString());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * <?xml version="1.0" encoding="GB2312"?>
		<Params>
			<item id="count" title="统计方式名称"> 
			</item>
			<item id="max" title="统计方式名称">
				   公式
			</item>
			<item id="min" title="统计方式名称">
				   公式
			</item>
			<item id="avg" title="统计方式名称">
				   公式
			</item>
			<item id="sum" title="统计方式名称">
				   公式
			</item>
		</Params>

	 */
	private void init(String snameid){
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<params>");
		temp_xml.append("</params>");
		RowSet rs =null;
		try{
			rs = dao.search("select sformula from sname where id="+snameid);
			if(rs.next())
				xml=rs.getString("sformula");
			if(xml==null|| "".equals(xml)){
				xml=temp_xml.toString();
			}
			doc=PubFunc.generateDom(xml.toString());
		}catch(Exception ex){
			xml=temp_xml.toString();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public List getAllChildren(){
		List list = null;
		try {
			XPath xpath = XPath.newInstance("/params");
			Element root=(Element)xpath.selectSingleNode(doc);
			list = root.getChildren();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	 
	public String setValue(String id,String title,String value,String type,String decimalwidth){
		//xiegh bug:29189 按照前台代码逻辑  个数的计算公式可以为空的  故完善该处判断
		if((type.length()>0&&title.length()>0&&value.length()>0&&decimalwidth.length()>0&&!"count".equalsIgnoreCase(type))||(type.length()>0&&title.length()>0&&decimalwidth.length()>0&&"count".equalsIgnoreCase(type))){
			try{
				List childlist=this.getAllChildren();
				Element element=null;
				if(id.length()>0){
					if(childlist!=null&&childlist.size()!=0){
						for(int i=0;i<childlist.size();i++){
							element=(Element)childlist.get(i);
							String tmpid=element.getAttributeValue("id");
							if(id.equalsIgnoreCase(tmpid)){
								element.setAttribute("title", title);
								element.setAttribute("type",type);
								element.setAttribute("decimalwidth",decimalwidth);
								element.setText(value);
								break;
							}
						}
					}
				}else{
					id="1";
					if(childlist!=null&&childlist.size()!=0){
						for(int i=0;i<childlist.size();i++){
							element=(Element)childlist.get(i);
							String tmpid=element.getAttributeValue("id");
							id=Integer.parseInt(id)>=Integer.parseInt(tmpid)?id:tmpid;
						}
						id=(Integer.parseInt(id)+1)+"";
					}
					XPath xpath=XPath.newInstance("params");
					Element spElement = (Element) xpath.selectSingleNode(doc);
					Element element1=null;
					element1=new Element("item");
					element1.setAttribute("id", id);
					element1.setAttribute("title",title);
					element1.setAttribute("type",type);
					element1.setAttribute("decimalwidth",decimalwidth);
					element1.setText(value);
					spElement.addContent(element1);
					Element element2=null;
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}	
		return id;
	}
	
	public String setCountValue(String id,String title,String value,String type,String decimalwidth,String del){
			try{

				Element element=new Element("item");
				element.setAttribute("id",id);

				element.setAttribute("title", title);
				element.setAttribute("type",type);
				element.setAttribute("decimalwidth",decimalwidth);
				//0标示未删除，1标示删除
				element.setAttribute("del",del);
				element.setText(value);

				XPath xpath=XPath.newInstance("params");
				Element spElement = (Element) xpath.selectSingleNode(doc);
				spElement.addContent(0, element);

			} catch(Exception ex){
				ex.printStackTrace();
			}	
		return id;
	}
	
	public void delValue(String id){
		if(id.length()>0){
			try{
				List childlist=this.getAllChildren();
				Element element=null;
				XPath xpath=XPath.newInstance("params");
				Element spElement = (Element) xpath.selectSingleNode(doc);
				if(childlist!=null&&childlist.size()!=0){
					for(int i=0;i<childlist.size();i++){
						element=(Element)childlist.get(i);
						String tmpid=element.getAttributeValue("id");
						String del = element.getAttributeValue("del");
						if(id.indexOf(","+tmpid+",")!=-1){
							if (!"0".equals(del)) {
								spElement.removeContent(element);
								i--;
							} else {
								element.setAttribute("del", "1");
							}
						}
					}
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}				
	}
	
	public Element getElement(String id){ 
		if(id.length()>0){
			try{
				XPath xpath=XPath.newInstance("/params/item");
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0){
					for(int i=0;i<childlist.size();i++){
						element=(Element)childlist.get(i);
						String tmpid=element.getAttributeValue("id");
						/**liubq  2015-11-04  start**/
						String del = element.getAttributeValue("del");
						if("0".equals(id)){
							if(del!=null&&!("").equals(del)&&!"".equals(del)&& "1".equals(del)){
								if(childlist.size()!=1){
									return (Element)childlist.get(1);
								}
							}else if(del!=null&&!("").equals(del)&&!"".equals(del)&& "0".equals(del)){
									return element;
							}
						}else{
							if(id.equalsIgnoreCase(tmpid)){
								return element;
							}
						}
//						if(del!=null&&!("").equals(del)&&del!=""&&del.equals("1")){
//							if(childlist.size()!=1){
//								return (Element)childlist.get(1);
//							}
//						}else{
//							if(id.equalsIgnoreCase(tmpid)){
//								return element;
//							}
//						}
						/**liubq  2015-11-04  end**/
					}
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public Element getFirstElement(){ 
			try{
				XPath xpath=XPath.newInstance("/params/item");
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist!=null&&childlist.size()!=0){
						element=(Element)childlist.get(0);
						return element;
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
			return null;
	}
	
	public String getFistSformula(){
		String sformula = "0";
		try {
			XPath xpath = XPath.newInstance("/params");
			Element root=(Element)xpath.selectSingleNode(doc);
			List childList = root.getChildren();
			if (childList == null || childList.size() == 0) {
				setCountValue("0", "个数", "", "count", "0", "0");
				saveStrValue();
				sformula = "0";
			} else {
				//此处获取统计方式id号，容易越界 优化  wangbo 2019-12-07 bug 56128
				List<String> sformulaList = new ArrayList();
				for ( int i = 0 ; i < childList.size(); i++) {
					Element el = (Element)childList.get(i);
					if (!"1".equals(el.getAttributeValue("del"))) {
						el = (Element)childList.get(i);
						sformulaList.add(el.getAttributeValue("id"));
					}
				}
				if(!sformulaList.contains(sformula)){
					sformula = sformulaList.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sformula;
	}
	
	
	public void orders(ArrayList ids){
		try {
			ArrayList list = new ArrayList();
			XPath xpath=XPath.newInstance("/params");
			Element root = (Element)xpath.selectSingleNode(doc);
			
			for (int i = 0; i < ids.size(); i++) {
				if(ids.get(i)!=null&&!("").equals(ids.get(i))&&""!=ids.get(i)){
					Element element = (Element) xpath.selectSingleNode(doc, "/params/item[@id='" + ids.get(i) + "']");
					list.add(element.clone());
					element.getParent().removeContent(element);
				}else{
					Element element = (Element) xpath.selectSingleNode(doc, "/params/item[@id='" + (Integer.parseInt((String)ids.get(i-1))+1) + "']");
					list.add(element.clone());
					element.getParent().removeContent(element);
				}
				
			}
			for (int i = 0; i < list.size(); i++) {		
				root.addContent((Element)list.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存xml格式的内容
	 *
	 */
	public void saveStrValue(){
		PreparedStatement pstmt = null;		
		StringBuffer strsql=new StringBuffer();
		try{
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			strsql.append("update sname set sformula=? where id="+snameid);
			pstmt = this.conn.prepareStatement(strsql.toString());	
			switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
					  pstmt.setString(1, buf.toString());
					  break;
				 case Constant.ORACEL:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());
					  break;
				  case Constant.DB2:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());
					  break;
			}
			pstmt.executeUpdate();	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	

	
}
