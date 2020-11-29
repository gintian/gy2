package com.hjsj.hrms.businessobject.ykcard;

import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.HashMap;
import java.util.List;

public class XmlSubdomain {

	public XmlSubdomain()
	{
		
	}
	private String func;
	private Document doc;
	public XmlSubdomain(String xmlcontent)
	{
		try
		{
			if(xmlcontent!=null&&xmlcontent.length()>0) {
                this.doc=PubFunc.generateDom(xmlcontent.toString());
            }
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		
	}	
	public String getFunc() {
		if(this.doc==null) {
            return "0";
        }
		StringBuffer xptah=new StringBuffer();		
		xptah.append("/sub_para/para");
		String func_v="";
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				func_v=element.getAttributeValue("func");
				if(func_v==null||func_v.length()<=0) {
                    func_v="";
                }
			}
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		this.func=func_v;
		return this.func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	//############################/
    private String colhead;//是否显示指标列名
    private String hl;//是否输出横纵（记录之间）
    private String vl;//是否输出竖线（指标之间）
    private String fields;//指标代码列表
    private String setname;
    private String colheadheight;//0表示指定的标题行高(毫米、浮点数)，其他值表示自动计算标题行高
    private String datarowcount;//0表示指定数据行数，数据行行高均匀分布；其他值表示有几条记录显示几行，
    private String customcolhead;//指标列自动和子集区域上方单元格对齐, 默认false
    private String multimedia;//是否显示附件, true显示，false不显示(默认值)
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public String getColhead() {
		return colhead;
	}
	public void setColhead(String colhead) {
		this.colhead = colhead;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getHl() {
		return hl;
	}
	public void setHl(String hl) {
		this.hl = hl;
	}
	public String getVl() {
		return vl;
	}
	public void setVl(String vl) {
		this.vl = vl;
	}
	//func="0" colheadheight="10" datarowcount="10"
	public void getParaAttribute()
	{
		StringBuffer xptah=new StringBuffer();		
		xptah.append("/sub_para/para");
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				this.colhead=element.getAttributeValue("colhead");
				this.hl=element.getAttributeValue("hl");
				this.vl=element.getAttributeValue("vl");
				this.setname=element.getAttributeValue("setname");
				this.fields=element.getAttributeValue("fields");
				this.colheadheight=element.getAttributeValue("colheadheight");
				this.datarowcount=element.getAttributeValue("datarowcount");
				this.customcolhead=element.getAttributeValue("customcolhead");
				this.multimedia=element.getAttributeValue("multimedia");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
	} 
	public String getParaAttribute(String attribute)
	{
		StringBuffer xptah=new StringBuffer();		
		xptah.append("/sub_para/para");
		String value="";
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				value=element.getAttributeValue(attribute); 
			}
		}catch(Exception e)
		{
			value="";
			e.printStackTrace();
		}finally{
		}
		return value;
	}  
	public String getRecString()
	{
		StringBuffer str=new StringBuffer();
		try
		{
			Element root = this.doc.getRootElement();
			List childlist=root.getChildren();
			for(int i=0;i<childlist.size();i++)
			{
				Element element=(Element)childlist.get(i);
				str.append(element.getAttributeValue("name")+"&nbsp;"+element.getAttributeValue("date")+"\n"+element.getValue()+"\n");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
		}
		return str.toString();
	}
    public HashMap getFieldAttribute(String name)
    {
    	HashMap hash=new HashMap();
    	StringBuffer xptah=new StringBuffer();		
		xptah.append("/sub_para/field[@name='"+name+"']");
		try
		{
			XPath xPath = XPath.newInstance(xptah.toString());
			List childlist=xPath.selectNodes(this.doc);
			if(childlist.size()!=0)
			{
				Element element=(Element)childlist.get(0);
				hash.put("need",element.getAttributeValue("need"));
				hash.put("width",element.getAttributeValue("width"));
				hash.put("title",element.getAttributeValue("title"));
				hash.put("default",element.getAttributeValue("default"));
				hash.put("slop",element.getAttributeValue("slop"));
				hash.put("pre",element.getAttributeValue("pre"));
				hash.put("align",element.getAttributeValue("align"));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
    	return hash;
    }
	public String getColheadheight() {
		return colheadheight;
	}
	public void setColheadheight(String colheadheight) {
		this.colheadheight = colheadheight;
	}
	public String getDatarowcount() {
		return datarowcount;
	}
	public void setDatarowcount(String datarowcount) {
		this.datarowcount = datarowcount;
	}
	public String getCustomcolhead() {
		return customcolhead;
	}
	public void setCustomcolhead(String customcolhead) {
		this.customcolhead = customcolhead;
	}
	public String getMultimedia() {
		return multimedia;
	}
	public void setMultimedia(String multimedia) {
		this.multimedia = multimedia;
	}
	
}
