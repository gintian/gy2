/**
 * 
 */
package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.*;

/**
 *<p>Title:SalaryLProgramBo</p> 
 *<p>Description:薪资类别中的LProgram内容解释</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-10:下午04:02:13</p> 
 *@author cmq
 *@version 4.0
 */

/**
 * 存放内容格式
 <?xml version="1.0" encoding="GB2312"?>
<Params>
<Serive>
	<SeiveItem ID="1" Name="参保人员" Expr="1+2" Factor="Z0106=1`Z0107=1`"/>#人员筛选过滤条件
	<ConfirmType>A00`2;A01`2;A04`2;A66`1;AZG`1;AZH`2;</ConfirmType>#数据提交方式
	<filters>1,2,3</filters>#项目过滤条件id
</Serive>
</Params>
=0,不变(不处理)
=1,新增
=2,更新
 */
public class SalaryLProgramBo {
	/**过滤条件*/
	public final static int SERVICE_ITEM=0;	
	/**数据提交方式*/
	public final static int CONFIRM_TYPE=1;
	/**项目过滤*/
	public final static int FILTERS=2;
	
	private String lprogram;
	
	private Document doc;
	private UserView userview;
	/**薪资类别控制参数内容*/
	public SalaryLProgramBo(String lprogram) {
		super();
		this.lprogram = lprogram;
		init();
	}
	/**薪资类别控制参数内容*/
	public SalaryLProgramBo(String lprogram,UserView userview) { //xieguiquan add 20100827
		super();
		this.lprogram = lprogram;
		this.userview = userview;
		init();
	}

	/**初始化*/
	private void init()
	{
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<Params>");
		strxml.append("</Params>");	
		if(lprogram==null|| "".equalsIgnoreCase(lprogram))
			lprogram=strxml.toString();
		try
		{
			doc=PubFunc.generateDom(lprogram);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 取得对应的参数在XML节点的名称
	 * @param param_type
	 * @return
	 */
	private String getElementName(int param_type)
	{
		String name="";
		switch(param_type)
		{
		case SERVICE_ITEM:
			name="SeiveItem";
			break;
		case CONFIRM_TYPE:
			name="ConfirmType";
			break;
		case FILTERS:
			name="filters";
			break;
		}
		return name;
	}
	/**
	 * 删除过滤条件
	 * @param Id
	 */
	public void removeServiceItem(String Id)
	{
		try
		{
			String str_path="/Params/Serive/SeiveItem[@ID="+Id+"]";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			if(childlist.size()!=0)
				childlist.remove(0);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	public void reName(String id,String name)
	{
		try
		{
	    	String str_path="/Params/Serive/SeiveItem[@ID="+id+"]";
	    	XPath xpath=XPath.newInstance(str_path);
	    	Element element = (Element)xpath.selectSingleNode(doc);
	    	if(element!=null)
	    	{
	    		element.removeAttribute("Name");
	    		element.setAttribute("Name", name);
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void removeItem(String id)
	{
		try
		{
			String path="/Params/Serive/SeiveItem";
			XPath xpath=XPath.newInstance(path);
			List childlist=xpath.selectNodes(doc);
			ArrayList newList= new ArrayList();
			if(childlist.size()!=0)
			{
		       // Iterator t=childlist.iterator();
				
				for(int i=0;i<childlist.size();i++)
				{
					Element element=(Element)childlist.get(i);
					if(id.equalsIgnoreCase(element.getAttributeValue("ID")))
					{
					}
					else
					{
						newList.add(element);
					}
					
				}
				((Element)XPath.newInstance("/Params/Serive").selectSingleNode(doc)).removeChildren("SeiveItem");	
			}
			if(newList.size()!=0)
			{
				path="/Params/Serive";
				xpath=XPath.newInstance(path);
				Element element=(Element)xpath.selectSingleNode(doc);
				for(int i=0;i<newList.size();i++)
				{
					Element node=(Element)newList.get(i);
					element.addContent(node);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 设置属性值
	 * @param propertymp  Name="参保人员" Expr="1" Factor="Z0106=1`
	 */
	public int setSeiveItem(HashMap propertymp,String expr)
	{
		int id=1;
		try
		{
			String str_path="/Params/Serive/SeiveItem";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			ArrayList list=new ArrayList();
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					list.add(element.getAttributeValue("ID"));
				}//for end.
				Arrays.sort(list.toArray());
				id=Integer.parseInt((String)list.get(childlist.size()-1))+1;
			}else 
			{
				xpath=XPath.newInstance("/Params/Serive");
				element=(Element)xpath.selectSingleNode(doc);
				if(element==null)
				{
					xpath=XPath.newInstance("/Params");
					Element pElement=(Element)xpath.selectSingleNode(doc);
					element= new Element("Serive");
					pElement.addContent(element);
				}
			}
			Element childElement=new Element("SeiveItem");
			childElement.setAttribute("ID", String.valueOf(id));
			childElement.setAttribute("user_name", (String)propertymp.get("user_name") ); //xieguiquan 20100828
			childElement.setAttribute("Name",(String)propertymp.get("Name") );
			childElement.setAttribute("Expr", expr );
			childElement.setAttribute("Factor", (String)propertymp.get("Factor") );
		
			if(childlist.size()==0)
			{
				element.addContent(childElement);
			}
			else
			{
			    element=(Element)element.getParent();
			    element.addContent(childElement);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return id;
	}
	
	/**
	 * 取人员过滤条件的有序哈希表 zhanghua
	 * @return
	 */
	public LinkedHashMap<String,LazyDynaBean> getItemlist()
	{
		LinkedHashMap<String,LazyDynaBean> list = new LinkedHashMap<String,LazyDynaBean>();
		try
		{
			String str_path="/Params/Serive/SeiveItem";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;

			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("Name", element.getAttributeValue("Name"));
					bean.set("Expr", element.getAttributeValue("Expr"));
					bean.set("Factor", element.getAttributeValue("Factor"));
					if(element.getAttributeValue("user_name")!=null)
						bean.set("user_name", element.getAttributeValue("user_name"));
					list.put(element.getAttributeValue("ID").toString(), bean);
				}//for end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	public void setProperty(String ID,String Name,String Expr,String Factor)
	{
		try
		{
			String str_path="/Params/Serive";
			XPath xpath=XPath.newInstance(str_path);
			Element pElement=(Element)xpath.selectSingleNode(doc);
			Element childElement=new Element("SeiveItem");
			childElement.setAttribute("ID", ID);
			childElement.setAttribute("Name",Name);
			childElement.setAttribute("Expr", Expr );
			childElement.setAttribute("Factor", Factor);
			if(this.userview!=null)
			{
				childElement.setAttribute("user_name", this.userview.getUserName());
			}
			pElement.addContent(childElement);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void setProperty(String ID,String Name,String Expr,String Factor,String user_name)
	{
		try
		{
			String str_path="/Params/Serive";
			XPath xpath=XPath.newInstance(str_path);
			Element pElement=(Element)xpath.selectSingleNode(doc);
			Element childElement=new Element("SeiveItem");
			childElement.setAttribute("ID", ID);
			childElement.setAttribute("Name",Name);
			childElement.setAttribute("Expr", Expr );
			childElement.setAttribute("Factor", Factor);
			if(user_name!=null)
			{
				childElement.setAttribute("user_name", user_name);
			}
			pElement.addContent(childElement);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void deleteProperty()
	{
		try
		{
			String str_path="/Params/Serive";
			XPath xpath=XPath.newInstance(str_path);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element!=null)
			{
				element.removeChildren("SeiveItem");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 求薪资发放确认各子集数据提交方式
	 * @return
	 */
	public HashMap getSubmitMap()
	{
		HashMap map=new HashMap();
		try
		{
			String str=getValue(CONFIRM_TYPE);
			String[] typearr=StringUtils.split(str,";");
			for(int i=0;i<typearr.length;i++)
			{
				String tmp=typearr[i];
				if(tmp.length()==0)
					continue;
				String[] tmparr=StringUtils.split(tmp,"`");
				if(tmparr.length!=2)
					continue;
				map.put(tmparr[0], tmparr[1]);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}
	/**
	 * 求每个条件过滤号对应的表达式
	 * 例   1        1+2|AXXXX=2`AYYYY=32`
	 * @return
	 */
	public HashMap getServiceItemMap()
	{
		HashMap map=new HashMap();
		try
		{
			String str_path="/Params/Serive/SeiveItem";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					map.put(element.getAttributeValue("ID"), element.getAttributeValue("Expr")+"|"+element.getAttributeValue("Factor"));
				}//for end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}
	/**
	 * 求过滤条件列表
	 * @return
	 */
	public ArrayList getServiceItemList()
	{
		ArrayList list=new ArrayList();
		try
		{
			String str_path="/Params/Serive/SeiveItem";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;

			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					//判断user_name是否存在 xieguiquan 20100828
					if(element.getAttribute("user_name")!=null&&this.userview!=null){
						if(element.getAttributeValue("user_name")!=null&&element.getAttributeValue("user_name").equalsIgnoreCase(this.userview.getUserName())){
							CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name"));
							list.add(comm);
						}else{//超级用户组能看到别人的项目
							if(this.userview!=null&&this.userview.isSuper_admin()){
								CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name"));
								list.add(comm);
								}
						}
					}else{
						CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name"));
						list.add(comm);
					}
//					CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name"));
//					list.add(comm);
				}//for end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 求过滤条件列表 读取项特殊处理 超级用户才能读取旧的数据 (人员刷选)
	 * @return
	 */
	public ArrayList getServiceItemList2()
	{
		ArrayList list=new ArrayList();
		try
		{
			String str_path="/Params/Serive/SeiveItem";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;

			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					//判断user_name是否存在
					if(element.getAttribute("user_name")!=null&&this.userview!=null){
						if(element.getAttributeValue("user_name")!=null&&element.getAttributeValue("user_name").equalsIgnoreCase(this.userview.getUserName())){
							CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name").replaceAll("\"", "”"));
							list.add(comm);
						}else{	//超级用户组能看到别人的项目
							if(this.userview!=null&&this.userview.isSuper_admin()){
								CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name").replaceAll("\"", "”"));
								list.add(comm);	
						}
						}
					}else{
						if(this.userview!=null&&this.userview.isSuper_admin()){
						CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name").replaceAll("\"", "”"));
						list.add(comm);
						}
					}
//					CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name"));
//					list.add(comm);
				}//for end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	public HashMap getItemMap()
	{
		HashMap map = new HashMap();
		try
		{
			String str_path="/Params/Serive/SeiveItem";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;

			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					LazyDynaBean bean = new LazyDynaBean();
					/*childElement.setAttribute("ID", String.valueOf(id));
					childElement.setAttribute("Name",(String)propertymp.get("Name") );
					childElement.setAttribute("Expr", expr );
					childElement.setAttribute("Factor", (String)propertymp.get("Factor") );*/
					String id=element.getAttributeValue("ID");
					bean.set("Name", element.getAttributeValue("Name"));
					bean.set("Expr", element.getAttributeValue("Expr"));
					bean.set("Factor", element.getAttributeValue("Factor"));
					if(element.getAttributeValue("user_name")!=null)
						bean.set("user_name", element.getAttributeValue("user_name"));
					map.put(id, bean);
				}//for end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}
	/**
	 * 读取参数的值 TEXT
	 * @param param_type
	 * @return
	 */
	public String getValue(int param_type)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/Params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				element=(Element)childlist.get(0);
				value=element.getText();
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return value;		
	}	
	
	
	/**
	 * 设置对应节点的属性值
	 * @param param_type
	 * @param property
	 * @return
	 */
	public String getValue(int param_type,String property)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/Params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				element=(Element)childlist.get(0);
				value=element.getAttributeValue(property);	
			}
			
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return value;		
	}
	
	/**
	 * 设置节点的属性值
	 * @param param_type
	 * @param property
	 * @param value
	 * @return
	 */
	public boolean setValue(int param_type,String property,String value)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(value==null)
			value="";
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/Params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()==0)
			{
				element=new Element(name);
				element.setAttribute(property,value);
				doc.getRootElement().addContent(element);
			}
			else
			{
				element=(Element)childlist.get(0);
				element.setAttribute(property,value);
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  bflag=false;
		  }
		}		
		return bflag;
	}
	
	
	
	

	/**
	 * 设置节点text内容
	 * @param param_type
	 * @param value
	 * @return
	 */
	public boolean setValue(int param_type,String value)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/Params/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()==0)
			{
				element=new Element(name);
				element.setText(value);
				doc.getRootElement().addContent(element);
			}
			else
			{
				element=(Element)childlist.get(0);
				element.setText(value);
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}
		return bflag;
	}
	
	/**
	 * 输出xml内容格式
	 * @return
	 */
	public String outPutContent()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return buf.toString();
	}
	public String getServiceItemField(String id)
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			String str_path="/Params/Serive/SeiveItem[@ID="+id+"]";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
				element=(Element)childlist.get(0);
			if(element!=null)
			{
				String factor=element.getAttributeValue("Factor");
				String[] temp=factor.split("`");
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i]==null|| "".equals(temp[i]))
					  continue;
					buf.append(",'");
					buf.append(temp[i].substring(0,5));
					buf.append("'");
				}
				//String name=element.getAttributeValue("Name");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(buf.length()>0)
			return buf.toString().substring(1);
		else
			return "";
	}
	public void updateServiceItem(String id,HashMap map,String expr)
	{
		try
		{
			String str_path="/Params/Serive/SeiveItem[@ID="+id+"]";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
				element=(Element)childlist.get(0);
			if(element!=null)
			{
				/**将原先的值删除*/
				element.removeAttribute("Name");
				element.removeAttribute("Expr");
				element.removeAttribute("Factor");
				/**更新新值*/
				element.setAttribute("Name",(String)map.get("Name"));
				element.setAttribute("Expr",expr);
				element.setAttribute("Factor",(String)map.get("Factor"));
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
