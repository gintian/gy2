package com.hjsj.hrms.businessobject.sys.options;

import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class ParseSYS_OTH_PARAM {
	public ParseSYS_OTH_PARAM(){
		
	}

	private Document doc;
	/**
	 * 构建函数
	 * @param xml
	 * @throws JDOMException
	 * @throws IOException
	 */
	public ParseSYS_OTH_PARAM(String xml) throws JDOMException, IOException{
		this.getParamXml(xml);
	}
	public ParseSYS_OTH_PARAM(Document docs) throws JDOMException, IOException{
		this.doc=docs;
	}
	/**
	 * 将字符串转化成doc文件
	 * @param xml 字符串
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public Document getParamXml(String xml) throws JDOMException, IOException{
		xml=xml.replaceFirst("encoding=\"GBK\"", "encoding=\"UTF-8\""); //GBK tomcat6不支持
	    try {
            this.doc= PubFunc.generateDom(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return doc;
	}
	/**
	 * 原子节点
	 * @param ename 节点名称
	 * @param eAtrr 节点属性列表,节点包括多个属性
	 * @param evalue 节点值
	 */
	public Element CreateAtomElement(String ename,String evalue,List eAtrr){
		Element e=new Element(ename);
		for(Iterator it=eAtrr.iterator();it.hasNext();){
			String descatrr=(String)it.next();
			String[] atrrvalue=descatrr.split("=");
			if(atrrvalue.length>1) {
                e.setAttribute(atrrvalue[0],atrrvalue[1]);
            }
		}
		e.setName(ename);
		e.setText(evalue);
		return e;
	}
/**
 * 原子节点
 * @param ename 节点名称
 * @param evalue 节点属性，节点只有一个属性
 * @param eAtrr 节点值
 * @return
 */	
	public Element CreateAtomElement(String ename,String evalue,String eAtrr){
		Element e=new Element(ename);
		e.setName(ename);
		String[] attrvalue=eAtrr.split("=");
		if(attrvalue.length>1) {
            e.setAttribute(attrvalue[0],attrvalue[1]);
        }
		e.setText(evalue);
		return e;
	}
	
	/**
	 * 分子节点
	 * @param ename 节点名称
	 * @param eAtrr 节点属性单个属性
	 * @param atomelist 多个子节点，节点的子节点
	 * @return
	 */
	public Element CreateMoleculeElement(String ename,String eAtrr,List atomelist){
		Element e=new Element(ename);
		e.setName(ename);
		String[] attrvalue=eAtrr.split("=");
		e.setAttribute(attrvalue[0],attrvalue[1]);
		for(Iterator it=atomelist.iterator();it.hasNext();){
			Element atome=(Element)it.next();
			e.addContent(atome);
		}
		return e;
	}
	/**
	 * 分子节点
	 * @param ename 节点名称
	 * @param eAtrr  格式'attr=value' 节点属性单个属性
	 * @param atome 单个节点的子节点
	 * @return
	 */
	public Element CreateMoleculeElement(String ename,String eAtrr,Element atome){
		Element e=new Element(ename);
		e.setName(ename);
		String[] attrvalue=eAtrr.split("=");
		if(attrvalue.length>1) {
            e.setAttribute(attrvalue[0],attrvalue[1]);
        }
		e.addContent(atome);
		return e;
	}
	/**
	 * 分子节点
	 * @param ename 节点名称
	 * @param eAtrrlist 节点属性多个属性
	 * @param atome 单个节点的子节点
	 * @return
	 */
	public Element CreateMoleculeElement(String ename,List eAtrrlist,Element atome){
		Element e=new Element(ename);
		e.setName(ename);
		for(Iterator it=eAtrrlist.iterator();it.hasNext();){
			String eAtrr=(String)it.next();
			String[] attrvalue=eAtrr.split("=");
			if(attrvalue.length>1) {
                e.setAttribute(attrvalue[0],attrvalue[1]);
            }
		}		
		e.addContent(atome);
		return e;
	}
	/**
	 * 分子节点
	 * @param ename 节点名称
	 * @param eAtrrlist 节点属性多个属性
	 * @param atomelist 节点的多个子节点
	 * @return
	 */
	public Element CreateMoleculeElement(String ename,List eAtrrlist,List atomelist){
		Element e=new Element(ename);
		e.setName(ename);
		for(Iterator it=eAtrrlist.iterator();it.hasNext();){
			String eAtrr=(String)it.next();
			String[] attrvalue=eAtrr.split("=");
			if(attrvalue.length>1) {
                e.setAttribute(attrvalue[0],attrvalue[1]);
            }
		}
		for(Iterator it=atomelist.iterator();it.hasNext();){
			Element atome=(Element)it.next();
			e.addContent(atome);
		}
		return e;
	}
	/**
	 * 增加doc节点
	 * @param e 一个子节点
	 * @return 
	 */
	public Document AddDoc(Element e){
		Element rootElement=doc.getRootElement();
		rootElement.addContent(e);
		return doc;
	}
	/**
	 * 增加doc节点
	 * @param elist 多个子节点
	 * @return
	 */
	public Document AddDoc(List elist){
		Element rootElement=doc.getRootElement();
		for(Iterator it=elist.iterator();it.hasNext();){
			Element e=(Element)it.next();
			rootElement.addContent(e);
		}	
		return doc;
	}
	/**
	 * 将文件转化成字符串
	 * @param doc 文件
	 * @return
	 */
	public String docToString(Document doc){
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		// 创建的XML文件以字符串形式表示
		String docStr = outputter.outputString(doc);
		return docStr;
	}
	/**
	 * 指定xpath路径，修改属性
	 * @param xpath xpath 路径 例如/param/user[@user='yuxiaochun']
	 * @param eAtrr 属性 user=yuxiaochun
	 * @return
	 * @throws JDOMException
	 */
	public Document updateElementAtrr(String xpath,String eAtrr) throws JDOMException{
		XPath xPath = XPath.newInstance(xpath);
		Element e=(Element) xPath.selectSingleNode(doc);
		String[] eatrrStr=eAtrr.split("=");
		if(e!=null){
			Attribute tea=e.getAttribute(eatrrStr[0]);
			if(tea==null){
//				增加属性
				e.setAttribute(eatrrStr[0],eatrrStr[1]);
			}else{
				e.getAttribute(eatrrStr[0]).setValue(eatrrStr[1]);
			}
		}
		return doc;
		
	}
	/**
	 * 指定xpath路径，修改多个属性
	 * @param xpath 如/param/user或/param/user[@user='yuxiaochun']
	 * @param eAtrrlist 属性列表
	 * @return
	 * @throws JDOMException
	 */
	public Document updateElementAtrr(String xpath,List eAtrrlist) throws JDOMException{
		XPath xPath = XPath.newInstance(xpath);
		Element e=(Element) xPath.selectSingleNode(doc);
		
		if(e!=null){
			for(Iterator it=eAtrrlist.iterator();it.hasNext();){
				String eAtrr=(String) it.next();
				String[] eatrrStr=eAtrr.split("=");
				e.getAttribute(eatrrStr[0]).setValue(eatrrStr[1]);
			}
		}
		return doc;
		
	}
	/**
	 * 修改元素值
	 * @param xpath
	 * @param eValue
	 * @return
	 * @throws JDOMException
	 */
	public Document updateELementValue(String xpath,String eValue) throws JDOMException{
		XPath xPath = XPath.newInstance(xpath);
		Element e=(Element) xPath.selectSingleNode(doc);
		if(e!=null){
			e.removeContent();
			e.addContent(eValue);
		}
		return doc;
	}
	/**
	 * 取得指定xpath路径，属性值
	 * @param xpath
	 * @return hashMap
	 * @throws JDOMException
	 */
	public Map serachAtrr(String xpath) throws JDOMException{
		Map myMap=null;
		
		XPath xPath = XPath.newInstance(xpath);
		Element e=(Element) xPath.selectSingleNode(doc);
		if(e!=null){
			myMap=new HashMap();
			List evaluelist=e.getAttributes();
			for(Iterator it=evaluelist.iterator();it.hasNext();){
				Attribute atrr=(Attribute) it.next();
				myMap.put(atrr.getName(),atrr.getValue());
			}
		}
		return myMap;
	}
	/**
	 * 查找指定xpath路径元素的值
	 * @param xpath
	 * @return
	 * @throws JDOMException
	 */
	public Map serachatomElemetValue(String xpath) throws JDOMException{
		Map myMap=null;
		
		XPath xPath = XPath.newInstance(xpath);
		Element e=(Element) xPath.selectSingleNode(doc);
		if(e!=null){
			myMap=new HashMap();
			myMap.put(e.getName(),e.getValue());
		}
		return myMap;
	}
	/**
	 * 解析元素，生成一个Map对象
	 * @param e 元素
	 * @return Map对象
	 */
	public Map ParasXml(Element e){
		Map reMap=new HashMap();
		List cl=e.getChildren();
		if(cl==null||cl.size()==0){
			List al=e.getAttributes();
			StringBuffer sbvaorat=new StringBuffer();
			sbvaorat.append(e.getName());
			if(al!=null&&al.size()>0){	
				for(Iterator it=al.iterator();it.hasNext();){
					Attribute tempatrr=(Attribute)it.next();
					sbvaorat.append(","+tempatrr.getName()+"="+tempatrr.getValue());
				}
			Map myMap=new HashMap();
			myMap.put("value"+sbvaorat.toString(),e.getValue());
			myMap.put("atrr"+sbvaorat.toString(),sbvaorat.toString());
			reMap= myMap;	
			}
		}else{
			for(Iterator it=cl.iterator();it.hasNext();){
				Map molceMap=new HashMap();
				Element ce=(Element) it.next();
				List al=ce.getAttributes();
				StringBuffer sbvaorat=new StringBuffer();
				sbvaorat.append(ce.getName());
				for(Iterator its=al.iterator();its.hasNext();){
					Attribute tempatrr=(Attribute)its.next();
					sbvaorat.append(","+tempatrr.getName()+"="+tempatrr.getValue());
				}
				molceMap.put("value"+sbvaorat.toString(),this.ParasXml(ce));
				molceMap.put("atrr"+sbvaorat.toString(),sbvaorat.toString());
				reMap= molceMap;
			}
			
		}
		return reMap;
	}
/**
 * 获得文件的根节点
 * @return 返回ELement对象
 */
	public Element getRootUri(){
		Element root=doc.getRootElement();
		return root;
	}
	public static void main(String[] args) throws JDOMException, IOException{
		ParseSYS_OTH_PARAM psp=new ParseSYS_OTH_PARAM();
		//System.out.println(psp.docToString(psp.doc));
	}
}
