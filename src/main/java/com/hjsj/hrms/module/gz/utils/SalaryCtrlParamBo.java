package com.hjsj.hrms.module.gz.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalaryCtrlParamBo {
	public static HashMap docMap=new HashMap(); //key:salaryid  value:Document
	
	/**检索条件模式*/
	public final static int COND_MODE=0;
	/**变动对应模板*/	
	public final static int TEMPLATE=1;
	/**计税时间指标项,日期型指标*/
	public final static int TAX_DATE_FIELD=2;
	/**应纳税所得额 ,id为计算公式号*/
	public final static int YS_FIELDITEM=3;
	/**发薪标识*/
	public final static int PAY_FLAG=4;
	/**报税时间,日期型指标*/
	public final static int DECLARE_TAX=5;
	/**计税模式*/
	public final static int TAX_MODE=6;
	/**计税项目说明*/
	public final static int TAX_DESC=7;
	/**审批流程控制*/
	public final static int FLOW_CTRL=8;
	/**发放审批指标*/
	public final static int COMPARE_FIELD=9;
	/**计件工资项目*/
	public final static int  PIECERATE=10;
	/**总额控制标志*/
	public final static int AMOUNT_CTRL=11;
	/**人员范围权限过滤标志*/
	public final static int PRIV_MODE=12;
	/** 共享类别 */
	public final static int SHARE_SET=13;
	/**工资审批通知*/
	public final static int NOTE=14;
	
	public final static int A01Z0=15;
	
	private Connection conn=null;

	/**薪资类别号*/
	private int salaryid=-1;

	private Document doc;
	private Document doc1;
	String xml2="";
	/**薪资类别控制参数内容*/
	private String xmlcontent="";	
	/**奖金项目*/
	public final static int BONUS=16;
	/** 默认排序sql  b0110 desc,e0122 asc,XXXXX */
	public final static int  DEFAULT_ORDER=17;
	/** 汇总指标 */
	public final static int SUM_FIELD=18;
	
	/**审核公式控制标识 */
	public final static int VERIFY_CTRL=19;
	/**隶属部门指标*/
	public final static int LS_DEPT=20;
	/**指标权限控制*/
	public final static int FIELD_PRIV=21;
	
	/**汇总审批金额指标*/
    public final static int COLLECT_JE_FIELD=22;	
    /**读权限指标允许重新导入*/
    public final static int READ_FIELD=23;
    
    public final static int ROYALTIES=24;
    
    public final static int PIECEPAY=25;
	
	
	public final static int ADD_MAN_FIELD=26;//新增人员指标  zhaoxg add 
	
	public final static int DEL_MAN_FIELD=27;//减少人员指标
	
	public final static int TAX_UNIT=28;//计税单位指标
	
	public final static int FILLING_AGENCYS=29;//应用机构
	
	public final static int FILLING_AGENCY=30;//人员机构详细信息
	
	public final static int HIRE_DATE=31;//入职时间指标
	
	public final static int DISABILITY=32;//是否残疾人指标
	
	
	
	
	
	String subordinateunits = "";//所属单位
	/**
	 * 取得对应的参数在XML节点的名称
	 * @param param_type
	 * @return
	 */
	public String getElementName(int param_type)
	{
		String name="";
		switch(param_type)
		{
		case COND_MODE:
			name="cond_mode";
			break;
		case TEMPLATE:
			name="template";
			break;
		case TAX_DATE_FIELD:
			name="tax_date_field";
			break;	
		case VERIFY_CTRL:
			name="verify_ctrl";
			break;
		case YS_FIELDITEM:
			name="ys_fielditem";
			break;	
		case PAY_FLAG:
			name="pay_flag";
			break;	
		case DECLARE_TAX:
			name="declare_tax";
			break;	
		case TAX_MODE:
			name="tax_mode";
			break;	
		case TAX_DESC:
			name="tax_desc";
			break;	
		case TAX_UNIT:
			name="tax_unit";
			break;
		case HIRE_DATE:
			name="hiredate";
			break;	
		case DISABILITY:
			name="disability";
			break;  
		case FLOW_CTRL:
			name="flow_ctrl";
			break;		
		case COMPARE_FIELD:
			name="compare_field";
			break;	
		case PIECERATE:
			name="piecerate";
			break;
		case AMOUNT_CTRL:
			name="amount_ctrl";
			break;
		case PRIV_MODE:
			name="priv_mode";
			break;
		case SHARE_SET:
			name="manager";
			break;
		case NOTE:
			name="note";
			break;
		case A01Z0:
			name="a01z0";
			break;
		case BONUS:
		    name="bonus";
		    break;
		case DEFAULT_ORDER:
			name="default_order";
			break;
		case SUM_FIELD:
			name="sum_field";
			break;
		case LS_DEPT:
			name="ls_dept";
			break;
		case FIELD_PRIV:
			name="fieldpriv";
			break;
		case COLLECT_JE_FIELD:
			name="collect_je_field";
			break;
		case READ_FIELD:
			name="readfield";
			break;
		case ROYALTIES:
			name="royalties";
			break;
		case PIECEPAY:
			name="piecepay";
			break;
		case ADD_MAN_FIELD:
			name="add_man_field";
			break;
		case DEL_MAN_FIELD:
			name="del_man_field";
			break;
		case FILLING_AGENCYS:
			name="filling_agencys";
			break;
		case FILLING_AGENCY:
			name="filling_agency";
			
		}
		return name;
	}
	/**初始化*/
	private void init()
	{
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<param>");
		strxml.append("</param>");	
		RowSet rset=null;
		try
		{
			//后期用缓存技术提高性能 2015-07-23
	//		if(docMap.get(String.valueOf(this.salaryid))==null)
			{
				ContentDAO dao=new ContentDAO(this.conn);
				DbWizard dbw = new DbWizard(this.conn);
				if(!dbw.isExistField("salarytemplate", "b0110", false)){
					Table table=new Table("salarytemplate");
					Field field=new Field("b0110","b0110");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					table.addField(field);	
					dbw.addColumns(table);
				}
				StringBuffer buf=new StringBuffer();
				buf.append("select ctrl_param,b0110 from salarytemplate where salaryid=?");
				ArrayList paramlist=new ArrayList();
				paramlist.add(new Integer(this.salaryid));
				rset=dao.search(buf.toString(),paramlist);
				if(rset.next()){
					xmlcontent=Sql_switcher.readMemo(rset, "ctrl_param");
					this.subordinateunits = rset.getString("b0110")==null?"":rset.getString("b0110");
				}
				if(xmlcontent==null|| "".equalsIgnoreCase(xmlcontent))
					xmlcontent=strxml.toString();
				doc = PubFunc.generateDom(xmlcontent.toString());
				docMap.put(String.valueOf(this.salaryid),doc);
				this.setDoc1((Document) doc.clone());//变化前的参数，用于比对 zhaoxg add 2015-4-23
			}
	//		else{
	//			doc=(Document)docMap.get(String.valueOf(this.salaryid));
	//		}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
	}
	
	/**
	 * 在静态变量 docmap中压入全部薪资类别的控制参数，同时更新旧版SalaryCtrlParamBo类中的docmap。
	 * 
	 * @author zhanghua
	 * @date 2017年7月17日 上午11:30:51
	 */
	public void initAllSalaryCtrlParam()
	{
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<param>");
		strxml.append("</param>");	
		RowSet rset=null;
		try{	
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select ctrl_param,salaryid from salarytemplate");
			rset=dao.search(buf.toString());
			String xml="";
			String id="";
			while(rset.next()){
				xml=rset.getString("ctrl_param");
				id=rset.getString("salaryid");
				if(xml==null|| "".equalsIgnoreCase(xml))
					xml=strxml.toString();
				docMap.put(id,PubFunc.generateDom(xml));
			}
			
			com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo.docMap=(HashMap) docMap.clone();
		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
	}
	/**
	 * 在静态变量 docmap中压入传入薪资类别的控制参数，同时更新旧版SalaryCtrlParamBo类中的docmap。
	 * 
	 * @author zhanghua
	 * @date 2017年7月17日 上午11:30:51
	 */
	public void initAllSalaryCtrlParam(String Salaryid)
	{
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<param>");
		strxml.append("</param>");	
		RowSet rset=null;
		try{	
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select ctrl_param,salaryid from salarytemplate where salaryid in ("+Salaryid+")");
			rset=dao.search(buf.toString());
			String xml="";
			String id="";
			while(rset.next()){
				xml=rset.getString("ctrl_param");
				id=rset.getString("salaryid");
				if(xml==null|| "".equalsIgnoreCase(xml))
					xml=strxml.toString();
				docMap.put(id,PubFunc.generateDom(xml));
			}
			
			com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo.docMap=(HashMap) docMap.clone();
		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
	}
	
	public SalaryCtrlParamBo(Connection conn,int salaryid,String ctrl_param) {
		super();
		this.conn = conn;
		this.salaryid=salaryid;
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<param>");
		strxml.append("</param>");			
		try
		{
			xmlcontent=ctrl_param;
			if(xmlcontent==null|| "".equalsIgnoreCase(xmlcontent))
				xmlcontent=strxml.toString();
			doc=PubFunc.generateDom(xmlcontent.toString());
			docMap.put(String.valueOf(this.salaryid),doc);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
	public SalaryCtrlParamBo(Connection conn,int salaryid) {
		super();
		this.conn = conn;
		this.salaryid=salaryid;
		init();
	}
	public SalaryCtrlParamBo() {
		// TODO Auto-generated constructor stub
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
			String str_path="/param/"+name;
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
	public boolean setValue(int param_type,String value,UserView view)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/param/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()==0)
			{
				element=new Element(name);
				element.setText(value);
				element.setAttribute("user_name", view.getUserName());
				doc.getRootElement().addContent(element);
			}
			else
			{
				element=(Element)childlist.get(0);
				element.setAttribute("user_name", view.getUserName());
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
			String str_path="/param/"+name;
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
	 * 设置节点的属性值
	 * @param param_type
	 * @param property
	 * @param value
	 * @return
	 */
	public boolean setValue(int param_type,String propertyname,String propertyvalue,String ysvalue)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(propertyvalue==null)
			propertyvalue="";
		if(ysvalue==null)
			ysvalue="";
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/param/"+name+"[@"+propertyname+"='"+propertyvalue+"']";
			XPath xpath=XPath.newInstance(str_path);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element==null)
			{
				element=new Element(name);
				element.setAttribute(propertyname,propertyvalue);
				element.setText(ysvalue);
				doc.getRootElement().addContent(element);
			}else{
				element.removeContent();
				element.setAttribute(propertyname,propertyvalue);
				element.setText(ysvalue);
				element.setText(ysvalue);
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
	 * 设置节点的属性值
	 * @param param_type
	 * @param property
	 * @param value
	 * @return
	 */
	public boolean setValue(int param_type,String propertyname,String propertyvalue,String modename,
			String modevalue,String ysvalue){
		boolean bflag=true;
		String name=getElementName(param_type);
		if(propertyvalue==null)
			propertyvalue="";
		if(ysvalue==null)
			ysvalue="";
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/param/"+name+"[@"+propertyname+"="+propertyvalue+"]";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()==0)
			{
				element=new Element(name);
				element.setAttribute(propertyname,propertyvalue);
				element.setAttribute(modename,modevalue);
				element.setText(ysvalue);
				doc.getRootElement().addContent(element);
			}else{
				element=(Element)childlist.get(0);
				element.setAttribute(modename,modevalue);
				element.removeContent();
				element.setText(ysvalue);
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
	 * 设置节点的子节点的属性内容，因为子节点是相同的平行节点，名称相同，这样第一次添加属性，创建新节点，后面进来的只要更新就行
	 * @param param_type
	 * @param value
	 * @return
	 */
	public boolean setValue(int param_type,int parent_param_name,String property,String value,int flag)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		String parentName = getElementName(parent_param_name);
		if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(parentName))
		{
		  try
		  {
			String str_path="/param/"+parentName+"/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element = null;
			if(flag == -1) {
				element=new Element(name);
				element.setAttribute(property,value);
				doc.getRootElement().getChild(parentName).addContent(element);
			}else {
				element=(Element)childlist.get(flag);
				element.setAttribute(property,value);
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
	 * 获取对应子节点的属性集合
	 * @param param_type
	 * @param property
	 * @return
	 */
	public List getValue(int param_type,int parent_param_name)
	{
		String name=getElementName(param_type);
		String parentName = getElementName(parent_param_name);
		List childlist = new ArrayList();
		if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(parentName))
		{
		  try
		  {
			String str_path="/param/"+parentName+"/"+name;
			XPath xpath=XPath.newInstance(str_path);
			childlist=xpath.selectNodes(doc);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return childlist;		
	}
	
	/**
	 * 移除所有节点属性集合
	 * @param param_type
	 * @param property
	 * @return
	 */
	public void removeNode(int param_name)
	{
		String parentName = getElementName(param_name);
		if(StringUtils.isNotBlank(parentName))
		{
		  try
		  {
			String str_path="/param";
			XPath xpath=XPath.newInstance(str_path);
			Element parentElement=(Element)xpath.selectSingleNode(doc);
			parentElement.removeChildren(parentName);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
	}
	
   public boolean updateXML_IDValue(int param_type,String newid,int j)
   {
	   boolean bflag=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			  String parentpath="/param";
			  XPath xpath=XPath.newInstance(parentpath);
			  Element parentElement=(Element)xpath.selectSingleNode(doc);
			  
			String str_path="/param/"+name;
			xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			parentElement.removeChildren(name);
			ArrayList list =new ArrayList();
			if(childlist.size()==0)
			{
				
			}else{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					if(j==i)
					{
						element.setAttribute("id",newid);
					}
					list.add(element);
				}
				for(int i=0;i<list.size();i++)
				{
					Element aelement = (Element)list.get(i);
					parentElement.addContent(aelement);
				}
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
	 * 根据输入的属性名和属性值取得对应节点然后取出该节点下某一属性值
	 * @param param_type
	 * @param propertyname
	 * @param propertyvalue
	 * @return
	 */
	public String getValue(int param_type ,String propertyname,String propertyvalue,String requestPropertyName)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/param/"+name+"[@"+propertyname+"="+propertyvalue+"]";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				element=(Element)childlist.get(0);
				value=element.getAttributeValue(requestPropertyName);
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
	 * 根据输入的属性名和属性值取得对应值
	 * @param param_type
	 * @param propertyname
	 * @param propertyvalue
	 * @return
	 */
	public String getValue(int param_type ,String propertyname,String propertyvalue)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/param/"+name+"[@"+propertyname+"="+propertyvalue+"]";
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
			String str_path="/param/"+name;
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
	public String getValue(int param_type,UserView view)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/param/"+name+"[@user_name='"+view.getUserName()+"']";
			XPath xpath=XPath.newInstance(str_path);
			Element element=(Element)xpath.selectSingleNode(this.doc);
			if(element!=null)
			{
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
			String str_path="/param/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				element=(Element)childlist.get(0);
				value=element.getAttributeValue(property);	
			}			
			//人员范围权限过滤 默认为过滤
			if("priv_mode".equalsIgnoreCase(name)&& "flag".equalsIgnoreCase(property)&&value.length()==0)
				value="1";
			
			if("a01z0".equalsIgnoreCase(name)&& "flag".equalsIgnoreCase(property)&&value.length()==0)
				value="0";				
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return value==null?"":value;		
	}
	/**
	 * 根据薪资类别获取状态位   0：薪资  1：保险  
	 * zhaoxg add 2013-10-26
	 * @param salaryid
	 * @return
	 */
	public String getCstate(String salaryid){
		String flag="0";
		  try
		  {
			  ContentDAO dao=new ContentDAO(this.conn);
			  String sql = "select cstate from salarytemplate where salaryid = "+salaryid+"";
			  RowSet rs = dao.search(sql);
			  if(rs.next()){
				  flag=rs.getString("cstate");
			  }
			  if(flag==null|| "".equals(flag)){
				  flag="0";
			  }
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		return flag;
	}
	/**
	 * 参数保存
	 * @throws GeneralException
	 */
	public void saveParameter()throws GeneralException
	{
		try
		{
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			docMap.put(String.valueOf(this.salaryid),doc);
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setInt("salaryid", this.salaryid);
			vo.setString("ctrl_param", buf.toString());
			dao.updateValueObject(vo);
			this.setXml2(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public void setDoc1(Document doc1) {
		this.doc1 = doc1;
	}
	public Document getDoc1() {
		return doc1;
	}
	public String getXml2() {
		return xml2;
	}
	public void setXml2(String xml2) {
		this.xml2 = xml2;
	}
	public String getSubordinateunits() {
		return subordinateunits;
	}
	public void setSubordinateunits(String subordinateunits) {
		this.subordinateunits = subordinateunits;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
}
