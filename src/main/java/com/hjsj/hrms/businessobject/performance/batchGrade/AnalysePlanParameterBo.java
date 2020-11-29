package com.hjsj.hrms.businessobject.performance.batchGrade;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class AnalysePlanParameterBo {
	Connection conn=null;
	Document doc;
	static Hashtable returnHt=null;
	
	public AnalysePlanParameterBo(Connection con)
	{
		conn=con;
	}
	public void init()
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		try
		{
			rs=dao.search("select str_value from constant where constant='PER_PARAMETERS'");
			String values="";
			if(rs.next())
			{
				values=Sql_switcher.readMemo(rs,"str_value");
			}
			if("".equals(values.trim()))
			{
				values="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Per_Parameters></Per_Parameters>";
			}
			this.doc = PubFunc.generateDom(values);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void restore(){
		String values="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Per_Parameters></Per_Parameters>";
		try {
			this.doc = PubFunc.generateDom(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setParam(String nodeName,String textValue,HashMap attributeMap)
	{
		try
		{
			if(this.doc!=null)
			{
		    	Element root=this.doc.getRootElement();
		    	XPath xpath=XPath.newInstance("/"+root.getName()+"/"+nodeName);
	    		Element element=(Element)xpath.selectSingleNode(doc);
		    	boolean isNew=false;
		    	if(element==null)
		    	{
		    		element=new Element(nodeName);
			    	isNew = true;
		    	}
		    	element.setText(textValue);
		    	Set keySet = attributeMap.keySet();
		    	for(Iterator t=keySet.iterator();t.hasNext();)
		    	{
		    		String attributeName=(String)t.next();
		    		String attributeValue=(String)attributeMap.get(attributeName);
		    		element.setAttribute(attributeName, attributeValue);
		    	}
		    	if(isNew)
			    	root.addContent(element);	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void saveParam()
	{	
		DbSecurityImpl dbS = new DbSecurityImpl();
		ContentDAO dao=new ContentDAO(this.conn);
		PreparedStatement pstmt = null;	
		try
		{
			if(this.doc!=null)
			{
	    		XMLOutputter outputter = new XMLOutputter();
	    		Format format = Format.getPrettyFormat();
	    		format.setEncoding("UTF-8");
		    	outputter.setFormat(format);
	     		String temp= outputter.outputString(doc);
		    	RecordVo vo = new RecordVo("constant");
		    	vo.setString("constant", "PER_PARAMETERS");
		    	StringBuffer strsql=new StringBuffer();
		    	if(dao.isExistRecordVo(vo))
		    	{
		    		strsql.append("update constant set str_value=? where constant='PER_PARAMETERS'");
		      		pstmt = this.conn.prepareStatement(strsql.toString());	
				    switch(Sql_switcher.searchDbServer())
		    		{
			    	  case Constant.MSSQL:
			    	  {
			    		  pstmt.setString(1, temp);
				    	  break;
			    	  }
			     	  case Constant.ORACEL:
			    	  {
			    		  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(temp.getBytes())), temp.length());
				    	  break;
			    	  }
			    	  case Constant.DB2:
			    	  {
				    	  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(temp.getBytes())), temp.length());
				    	  break;
			    	  }
		    	   }
		    	}
		     	else
		    	{
		    		strsql.append("insert into constant(constant,str_value) values(?,?)");	
		     		pstmt = this.conn.prepareStatement(strsql.toString());				
			    	pstmt.setString(1, "PER_PARAMETERS");
		    		pstmt.setString(2,temp);
		    	}
		    	// 打开Wallet
		    	dbS.open(this.conn, strsql.toString());
		    	pstmt.executeUpdate();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(pstmt!=null)
			{
				try
				{
					pstmt.close();
					// 关闭Wallet
					dbS.close(this.conn);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	public Hashtable analyseParameterXml()
	{	
		if(returnHt==null)
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=null;
			try 
			{
				
				
				rs=dao.search("select str_value from constant where constant='PER_PARAMETERS'");
				String values="";
				if(rs.next())
				{
					values=Sql_switcher.readMemo(rs,"str_value");
				}
				if("".equals(values.trim()))
				{
					values="<?xml version=\"1.0\" encoding=\"GB2312\"?><Per_Parameters></Per_Parameters>";
				}
				if(!"".equals(values.trim()))
				{
					Element root;
					try 
					{
							Document doc = PubFunc.generateDom(values);
							root = doc.getRootElement();
							Hashtable tempHash = new Hashtable();
							tempHash=getElements(root);
							returnHt=tempHash;
						} catch (Exception ex) 
						{
								ex.printStackTrace();
						}
					}
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
		return this.returnHt;
	}
	
	
	
	
	
	/**
	 * 根据定义的参数生成Hashtable
	 * @param root
	 * @return
	 */
	public Hashtable getElements(Element root) 
	{		
		Hashtable ht=new Hashtable();
		String MarkingMode="1";       //打分方式  1：下拉框  2：平铺
		String TargetTraceItem="";    //目标卡跟踪显示指标
		String TargetCollectItem="";
		String TargetDefineItem="";   //目标卡指标
		String creatCard_mail="false";    //目标卡制作 发送email
		String creatCard_mail_template="";
		String evaluateCard_mail="false"; //目标卡评估 发送email
		String evaluateCard_mail_template="";
		String appeal_template="";        //申诉模板
		String interview_template="";	  //面谈模板
		String TogetherCommit="False";    //多人打分统一提交, Ture, False, 默认为False
		String SubSet="";//岗位职责子集
		HashMap TargetPostDuty= new HashMap();//目标卡指标与岗位职责子集对应关系	
		String TargetItem="";//岗位职责子集中，与项目名称对应的指标
		
		String DescriptionItem="";  //指标解释指标（P04中的备注型指标)
		String PrincipleItem="";    //评分说明指标（P04中的备注型指标）
		String AllowLeaderTrace="False";
		/**组织机构考核指标设置参数*/
		String pointset_menu="";//考核指标子集
		String pointcode_menu="";//指标编号子集
		String pointname_menu="";//指标名称子集
		String showmenus="";//其他显示指标（逗号分隔）
		String rightCtrlByPerObjType = "true";//按计划考核对象类型权限控制 默认为true
		/*xml.setAttributeValue("/Per_Parameters/ResultVisible", "evaluate", e_str);
		xml.setAttributeValue("/Per_Parameters/ResultVisible", "objective", o_str);*/
		String evaluate="";//360度考评结果显示内容
		String objective="";//目标考评结果先死内容
		
		String departDutySet="";//部门职责自己
		String projectField="";//部门职责子集中项目指标
		String validDateField="";//部门职责子集中 时间指标
		String departTextValue="";//部门职责子集中与目标卡对应指标
		//xml.setAttributeValue("/Per_Parameters/TargetDeptDuty", "SubSet", departDutySet);
		//xml.setAttributeValue("/Per_Parameters/TargetDeptDuty", "TargetItem", projectField);
		//xml.setAttributeValue("/Per_Parameters/TargetDeptDuty", "DataItem", validDateField);
		//xml.setTextValue("/Per_Parameters/TargetDeptDuty", departTextValue.toUpperCase());
		if (root.getChildren() != null) 
		{
			List list=root.getChildren();
			
			for(int i=0;i<list.size();i++)
			{
				Element node = (Element) list.get(i);
				if("TargetDeptDuty".equalsIgnoreCase(node.getName())){
					if(node.getAttributeValue("SubSet")!=null&&!"".equals(node.getAttributeValue("SubSet")))
						departDutySet=node.getAttributeValue("SubSet");
					if(node.getAttributeValue("TargetItem")!=null&&!"".equals(node.getAttributeValue("TargetItem")))
						projectField=node.getAttributeValue("TargetItem");
					if(node.getAttributeValue("DataItem")!=null&&!"".equals(node.getAttributeValue("DataItem")))
						validDateField=node.getAttributeValue("DataItem");
					if(node.getText()!=null&&!"".equals(node.getText()))
						departTextValue=node.getText();
				}
				else if("Plan".equalsIgnoreCase(node.getName()))
				{
					if (node.getAttributeValue("MarkingMode") != null
							&& !"".equals(node.getAttributeValue("MarkingMode"))) {
						MarkingMode = node.getAttributeValue("MarkingMode");
					}
					if (node.getAttributeValue("TogetherCommit") != null
							&& !"".equals(node.getAttributeValue("TogetherCommit"))) {
						TogetherCommit = node.getAttributeValue("TogetherCommit");
					}
				}
				else if("DescriptionItem".equalsIgnoreCase(node.getName()))
				{
					if (node.getText() != null
							&& !"".equals(node.getText())) {
						DescriptionItem = node.getText();
					}
				}
				else if("PrincipleItem".equalsIgnoreCase(node.getName()))
				{
					if (node.getText() != null
							&& !"".equals(node.getText())) {
						PrincipleItem = node.getText();
					}
				}
				else if("TargetTraceItem".equalsIgnoreCase(node.getName()))
				{
					if (node.getText() != null
							&& !"".equals(node.getText())) {
						TargetTraceItem = node.getText();
					}
				}
				else if("TargetDefineItem".equalsIgnoreCase(node.getName()))
				{
					if (node.getText() != null
							&& !"".equals(node.getText().trim())) {
						TargetDefineItem = node.getText().trim();
					}
				}
				else if("AllowLeaderTrace".equalsIgnoreCase(node.getName()))
				{
					if(node.getText()!=null&&!"".equals(node.getText()))
						AllowLeaderTrace=node.getText();
				}
				else if("TargetCalcItem".equalsIgnoreCase(node.getName()))
				{
					if (node.getText() != null
							&& !"".equals(node.getText())) {
						TargetCollectItem = node.getText();
					}
				}
				
				else if("TargetCard".equalsIgnoreCase(node.getName()))
				{
					if (node.getAttributeValue("email") != null&& !"".equals(node.getAttributeValue("email"))) {
						creatCard_mail = node.getAttributeValue("email");
					}
					if (node.getAttributeValue("template") != null&& !"".equals(node.getAttributeValue("template"))) {
						creatCard_mail_template = node.getAttributeValue("template");
					}
				}
				else if("TargetAppraises".equalsIgnoreCase(node.getName()))
				{
					if (node.getAttributeValue("email") != null&& !"".equals(node.getAttributeValue("email"))) {
						evaluateCard_mail = node.getAttributeValue("email");
					}
					if (node.getAttributeValue("template") != null&& !"".equals(node.getAttributeValue("template"))) {
						evaluateCard_mail_template = node.getAttributeValue("template");
					}
				}
				else if("Appeal".equalsIgnoreCase(node.getName()))
				{
					if (node.getAttributeValue("template") != null&& !"".equals(node.getAttributeValue("template"))) {
						appeal_template = node.getAttributeValue("template");
					}
				}
				else if("Interview".equalsIgnoreCase(node.getName()))
				{
					if (node.getAttributeValue("template") != null&& !"".equals(node.getAttributeValue("template"))) {
						interview_template = node.getAttributeValue("template");
					}
				}
				else if("TargetPostDuty".equalsIgnoreCase(node.getName()))//目标卡指标与岗位职责子集对应关系
				{
					if(node.getAttributeValue("SubSet")!=null&&!"".equals(node.getAttributeValue("SubSet")))
					{
						SubSet=node.getAttributeValue("SubSet");
						if(node.getText()!=null&&!"".equals(node.getText()))
						{
							String content=node.getText();
							String[] c_arr=content.split(",");
							for(int j=0;j<c_arr.length;j++)
							{
								String temp=c_arr[j];
								if(temp==null|| "".equals(temp))
									continue;
								temp = temp.replaceAll("＝", "=");
								TargetPostDuty.put(temp.split("=")[0].toUpperCase(),temp.split("=")[1]);
							}
						}
					}
					if(node.getAttributeValue("TargetItem")!=null&&!"".equals(node.getAttributeValue("TargetItem")))
					{
						TargetItem=node.getAttributeValue("TargetItem");
					}
				}
			//	else if(node.getName().equalsIgnoreCase("RightCtrlByPerObjType"))
			//	{
			//		rightCtrlByPerObjType= node.getTextTrim();
			//	}
				else if("ORG_POINT".equalsIgnoreCase(node.getName()))
				{
					if(node.getText()!=null&&!"".equals(node.getText()))
						pointset_menu=node.getText();
					if(node.getAttributeValue("pointcode_menu")!=null&&!"".equals(node.getAttributeValue("pointcode_menu")))
						pointcode_menu=node.getAttributeValue("pointcode_menu");
					if(node.getAttributeValue("pointname_menu")!=null&&!"".equals(node.getAttributeValue("pointname_menu")))
						pointname_menu=node.getAttributeValue("pointname_menu");
					if(node.getAttributeValue("showmenus")!=null&&!"".equals(node.getAttributeValue("showmenus")))
						showmenus=node.getAttributeValue("showmenus");
						
				}
				else if("ResultVisible".equalsIgnoreCase(node.getName()))
				{
					if(node.getAttributeValue("evaluate")!=null)
						evaluate=node.getAttributeValue("evaluate");
					if(node.getAttributeValue("objective")!=null)
					    objective=node.getAttributeValue("objective");
				}
			}
		}
		ht.put("SubSet", SubSet);
		ht.put("TargetItem", TargetItem);
		if(TargetPostDuty.size()!=0)
		{
			ht.put("TargetPostDuty", TargetPostDuty);
		}
		ht.put("TargetDefineItem",TargetDefineItem);
		ht.put("DescriptionItem",DescriptionItem);
		ht.put("PrincipleItem", PrincipleItem);
		ht.put("TogetherCommit", TogetherCommit);
		ht.put("appeal_template",appeal_template);
		ht.put("interview_template", interview_template);
		ht.put("TargetTraceItem",TargetTraceItem);
		ht.put("TargetCollectItem",TargetCollectItem);
		ht.put("MarkingMode",MarkingMode);	
	//	creatCard_mail="False";
	//	evaluateCard_mail="False";
		ht.put("creatCard_mail",creatCard_mail);
		ht.put("creatCard_mail_template",creatCard_mail_template);	
		ht.put("evaluateCard_mail",evaluateCard_mail);
		ht.put("evaluateCard_mail_template",evaluateCard_mail_template);	
		ht.put("AllowLeaderTrace",AllowLeaderTrace);
		ht.put("pointset_menu", pointset_menu);
		ht.put("pointcode_menu", pointcode_menu);
		ht.put("pointname_menu", pointname_menu);
		ht.put("showmenus", showmenus);
		ht.put("rightCtrlByPerObjType", rightCtrlByPerObjType);
		ht.put("evaluate",evaluate);
		ht.put("objective",objective);
		ht.put("departDutySet", departDutySet);
		ht.put("projectField", projectField);
		ht.put("validDateField", validDateField);
		ht.put("departTextValue", departTextValue);
		return ht;
	}
	
	
	
	
	
	

	public static Hashtable getReturnHt() {
		return returnHt;
	}

	public static void setReturnHt(Hashtable returnHt) {
		AnalysePlanParameterBo.returnHt = returnHt;
	}
	
	
	

}
