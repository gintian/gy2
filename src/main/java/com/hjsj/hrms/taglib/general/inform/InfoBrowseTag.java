/*
 * Created on 2006-2-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.taglib.general.inform;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.FilterSetBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.StationPosView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InfoBrowseTag extends TagSupport {	
	private String nid;      //id
	private String pre;      //人员库
	private String infokind; //1人员2单位3职位
	private String isinfoself;//是否是信息自助0员工自助1其他的自助2招聘自助
	private String setflag;
	private String orgtype;
	private String contentTypeField;
	private String orgBriefField;
	private HashMap fieldSetMap;
	private HashMap fieldMap;
	private String state;
	private UserView uv=null;
	private Pattern p = Pattern.compile("[a-zA-Z]+://[^\\s]*");
	private String fenlei_type="";
	public int doStartTag() throws JspException{
		getContentTypeAndField();
		initMap();
		if(this.orgtype==null||this.orgtype.length()<=0)
			this.orgtype="org";
		return SKIP_BODY; 
	}
	public int doEndTag() throws JspException{
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		
		printoutscript();
		showInfoData();
		if(this.orgtype==null||this.orgtype.length()<=0)
			this.orgtype="org";
		return SKIP_BODY; 
	}
	private void showInfoData()
	{
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		this.uv=userview;
		List fieldlist=null;
		List setlist=null;		//人员库
		
		if("1".equals(infokind))
		{
			if("0".equals(isinfoself))               //自助
			{
				fieldlist=userview.getPrivFieldList("A01",0);   //自助主集的指标
				setlist=userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);   //自助所有权限子集
			}
		    else if("1".equals(isinfoself))
		    {	
		    	getOneFenleiYype(userview,this.pre,this.nid);
		    	if(this.fenlei_type!=null&&this.fenlei_type.length()>0&&!userview.isSuper_admin())
				{
					//得到分类授权子集
		    		InfoUtils infoUtils=new InfoUtils();
		    		fieldlist=infoUtils.getSubPrivFieldList(userview,"A01",this.fenlei_type);
		    		setlist=infoUtils.getPrivFieldSetList(userview,this.fenlei_type,Constant.EMPLOY_FIELD_SET);
					if(fieldlist==null||fieldlist.size()<=0)//如果分类中得不到指标则用默认权限的
						fieldlist=userview.getPrivFieldList("A01");   //获得当前子集的所有属性
					if(setlist==null||setlist.size()<=0)
						setlist=userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
				}else{
		    	    fieldlist=userview.getPrivFieldList("A01");  //人员主集的指标
		    	    setlist=userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);    //人员所有权限子集
				}
		    }
		    else if("2".equals(isinfoself))
		    {
		    	fieldlist=getZpMainsetFieldList();
		    	setlist=getZpSetsList();
		    }
			showemployeedata(fieldlist,setlist,nid,pre);
		}else if("2".equals(infokind))          //单位库
		{
			/**lzw 20080630 机构和职位信息不分自助和业务平台 人员信息分*/
			/*if("0".equals(isinfoself))               //自助
			{*/
				//fieldlist=userview.getPrivFieldList("B01",0);   //自助主集的指标
				//setlist=userview.getPrivFieldSetList(Constant.UNIT_FIELD_SET,0);   //自助所有权限子集
			/*}
			 else if("1".equals(isinfoself))
		    {*/	
			 
		    	fieldlist=userview.getPrivFieldList("B01");  //人员主集的指标
		    	setlist=userview.getPrivFieldSetList(Constant.UNIT_FIELD_SET);    //人员所有权限子集
		    /*}*/
		    if(!userview.isSuper_admin())
			{
		    	boolean isCorrect=false;
		    	if(fieldlist!=null&&fieldlist.size()>0)
		    	{
		    		for(int i=0;i<fieldlist.size();i++)
		    		{
		    			FieldItem fo=(FieldItem)fieldlist.get(i);
		    			if("b0110".equalsIgnoreCase(fo.getItemid()))
		    			{
		    				isCorrect=true;
		    				continue;
		    			}
		    			
		    		}
		    	}
		    	if(!isCorrect)
		    	{
		    		FieldItem fi=DataDictionary.getFieldItem("B0110");
					fieldlist.add(0,fi);
		    	}
			}
			showeunitdata(fieldlist,setlist,nid,pre);
		}else if("3".equals(infokind))          //职位库
		{
			ArrayList fieldlists=new ArrayList();
			FieldItem fielditem=new FieldItem();
			fielditem.setCodesetid("UM");
			fielditem.setItemtype("A");
			fielditem.setItemid("e0122");
			fielditem.setItemdesc(ResourceFactory.getProperty("lable.pos.e0122"));
			fieldlists.add(fielditem);
			/*if("0".equals(isinfoself))               //自助
			{
				fieldlist=userview.getPrivFieldList("K01",0);   //自助主集的指标
				setlist=userview.getPrivFieldSetList(Constant.POS_FIELD_SET,0);   //自助所有权限子集
			}
			else if("1".equals(isinfoself))
		    {	*/
		    	fieldlist=userview.getPrivFieldList("K01");  //人员主集的指标
		    	setlist=userview.getPrivFieldSetList(Constant.POS_FIELD_SET);    //人员所有权限子集
		   /* }*/
			for(int i=0;fieldlist!=null&&i<fieldlist.size();i++)
				fieldlists.add(fieldlist.get(i));
			showposdata(fieldlists,setlist,nid,pre);
		}else if("4".equals(infokind)){
		    	fieldlist=userview.getPrivFieldList("H01");  //人员主集的指标
		    	setlist=userview.getPrivFieldSetList(Constant.JOB_FIELD_SET);    //人员所有权限子集
		    if(!userview.isSuper_admin())
			{
		    	boolean isCorrect=false;
		    	if(fieldlist!=null&&fieldlist.size()>0)
		    	{
		    		for(int i=0;i<fieldlist.size();i++)
		    		{
		    			FieldItem fo=(FieldItem)fieldlist.get(i);
		    			if("h0100".equalsIgnoreCase(fo.getItemid()))
		    			{
		    				isCorrect=true;
		    				continue;
		    			}
		    			
		    		}
		    	}
		    	if(!isCorrect)
		    	{
		    		FieldItem fi=DataDictionary.getFieldItem("h0100");
					fieldlist.add(0,fi);
		    	}
			}
		    showjobdata(fieldlist,setlist,nid);
			
		}

	}
	private ArrayList getZpMainsetFieldList()
	{
		 ArrayList zpfieldlist=new ArrayList();
		 RecordVo constantfield_vo=ConstantParamter.getRealConstantVo("ZP_FIELD_LIST");
		 String fieldStr=constantfield_vo.getString("str_value");
		 if(fieldStr!=null && fieldStr.length()>0)
	     {
			String fieldsubStr=fieldStr.substring(fieldStr.indexOf("A01{"));
		   	if(fieldsubStr!=null && fieldsubStr.length()>4)
				fieldStr=fieldsubStr.substring(4,fieldsubStr.indexOf("}"));
			ArrayList infofieldlist=DataDictionary.getFieldList("A01",Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<infofieldlist.size();i++)
			{
			 	 FieldItem fielditem=(FieldItem)infofieldlist.get(i);
			 	 if(fieldStr.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
			 	 {
			 	 	zpfieldlist.add(fielditem);
			 	 }
		    } 
	    }
		return zpfieldlist;
	}
	private ArrayList getZpSetsList()
	{
		RecordVo constantset_vo=ConstantParamter.getRealConstantVo("ZP_SUBSET_LIST");
		ArrayList infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		String setStr=constantset_vo.getString("str_value");
		ArrayList zpsetlist=new ArrayList();
		try{	    	
	    	if(!infoSetList.isEmpty())
	    	{
	    	   for(int i=0;i<infoSetList.size();i++)
	    	   {
	    	   	 FieldSet fieldset=(FieldSet)infoSetList.get(i);
	    	    if(setStr!=null && setStr.indexOf(fieldset.getFieldsetid())!=-1)
	    	    {
	    	    	zpsetlist.add(fieldset);
	    	    }
	    	   }
	    	}	    
	    }catch(Exception e){
	       e.printStackTrace();
	    }    
	    return zpsetlist;
	}
	//显示打印出人员的信息
	private void showemployeedata(List fieldlist,List setlist,String nid,String pre)
	{
		if(!fieldlist.isEmpty())
		{
			StringBuffer strsql=new StringBuffer();
			strsql.append("select ");
			for(int i=0;i<fieldlist.size();i++)
			{
				 FieldItem fieldItem=(FieldItem)fieldlist.get(i);
				 strsql.append(fieldItem.getItemid());
				 strsql.append(",");
			}
			strsql.append("a0100,state from ");
			strsql.append(pre);
			strsql.append("A01 where a0100='");
			strsql.append(nid);
			strsql.append("'");			
			List rs = ExecuteSQL.executeMyQuery(strsql.toString());
			String state="";
			if(rs!=null && rs.size()>0)
			{
				printinfo(rs,fieldlist,setlist);
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				state=(String)rec.get("state");				
			}
		    	
			else
				printnoinfo(nid,fieldlist,setlist);
			pageContext.setAttribute("state",state);	
		}
	}
	
	/**
	 * 显示打印出单位的信息
	 * @param fieldlist
	 * @param setlist
	 * @param nid
	 * @param pre
	 */
	private void showeunitdata(List fieldlist,List setlist,String nid,String pre)
	{
		if(!fieldlist.isEmpty())
		{
			//组织机构信息浏览中不显示设置的编制子集和薪酬总额相关子集还有薪资管理和保险管理中的相关子集  20160831 dengcan
			 Connection conn = null;
			try{
				conn=AdminDb.getConnection();
			
				//不显示设置的编制子集
				PosparameXML pos = new PosparameXML(conn);//得到constant表中UNIT_WORKOUT对应的参数
				String ps_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
			
				//不显示设置的薪酬总额相关子集
				GzAmountXMLBo gzAmountXMLBo=new GzAmountXMLBo(conn,1);
				HashMap gzXmlMap=gzAmountXMLBo.getValuesMap();
				String totalTable=(String)gzXmlMap.get("setid");
			
				//薪资管理和保险管理中的相关子集 
				GzAmountXMLBo xmlbo = new GzAmountXMLBo(conn,0);
				String viewname = xmlbo.getValue("base_set"); //gz
				if(viewname==null)
					viewname="";
				String insviewname = xmlbo.getValue("ins_base_set"); //ins
				if(insviewname!=null)
					viewname+=","+insviewname;
				
				if(viewname.length()>0){
					String[] viewnames = viewname.split(",");
					for(int i=0;i<viewnames.length;i++){
						if(viewnames[i]!=null&&viewnames[i].trim().length()>0)
						{
							for(int j=0;j<setlist.size();j++){
								FieldSet fs=(FieldSet)setlist.get(j);
								if(viewnames[i].trim().equalsIgnoreCase(fs.getFieldsetid()))
								{
									setlist.remove(j);
								} 
							} 
						}
					}
				} 
				for(int j=0;j<setlist.size();j++){
					FieldSet fs=(FieldSet)setlist.get(j);
					if(ps_set!=null&&ps_set.equalsIgnoreCase(fs.getFieldsetid()))
					{
						setlist.remove(j);
					}
					if(totalTable!=null&&totalTable.equalsIgnoreCase(fs.getFieldsetid()))
					{
						setlist.remove(j);
					}
				}
				
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				PubFunc.closeDbObj(conn);
			}
			
			
			if(setflag!=null&& "1".equals(setflag)){
				for(int j=0;j<setlist.size();j++){
					FieldSet fs=(FieldSet)setlist.get(j);
					String sqls="select b0110 from "+fs.getFieldsetid() +" where b0110='"+nid+"'";
					List temprs = ExecuteSQL.executeMyQuery(sqls);
					if(temprs.size()<1){
						setlist.remove(j);
						j--;
					}
				}
			}
			if(nid==null||nid.length()<=0)
			{
				if(this.uv.isSuper_admin())
				{
					String sql="select codeitemid from organization where codeitemid=parentid";
					List rs = ExecuteSQL.executeMyQuery(sql);
					if(rs!=null && rs.size()>0)
					{
						LazyDynaBean rec=(LazyDynaBean)rs.get(0);
						nid=(String)rec.get("codeitemid");
					}
				}else{
					if("UN".equals(this.uv.getManagePrivCode()))
						nid=this.uv.getManagePrivCode();
				}
			}
			StringBuffer strsql=new StringBuffer();
			strsql.append("select ");
			for(int i=0;i<fieldlist.size();i++)
			{
				 FieldItem fieldItem=(FieldItem)fieldlist.get(i);
				 strsql.append(fieldItem.getItemid());
				 strsql.append(",");
			}
			strsql.append("b0110 from ");
			strsql.append("b01 where b0110='");
			strsql.append(nid);
			strsql.append("'");
			List rs = ExecuteSQL.executeMyQuery(strsql.toString());
			//System.out.println(strsql.toString());
			if(rs!=null && rs.size()>0)
		    	printinfo(rs,fieldlist,setlist);
			else
				printnoinfo(nid,fieldlist,setlist);
		}
	}
	/**
	 * 显示打印出职位的信息
	 * @param fieldlist
	 * @param setlist
	 * @param nid
	 * @param pre
	 */
	private void showposdata(List fieldlist,List setlist,String nid,String pre)
	{
		if(!fieldlist.isEmpty())
		{
			if(setflag!=null&& "1".equals(setflag)){
				for(int j=0;j<setlist.size();j++){
					FieldSet fs=(FieldSet)setlist.get(j);
					String sqls="select * from "+fs.getFieldsetid() +" where e01a1='"+nid+"'";
					List temprs = ExecuteSQL.executeMyQuery(sqls);
					if(temprs.size()<1){
						setlist.remove(j);
						j--;
					}
				}
			}
			
			StringBuffer strsql=new StringBuffer();
			strsql.append("select ");
			for(int i=0;i<fieldlist.size();i++)
			{
				 FieldItem fieldItem=(FieldItem)fieldlist.get(i);
				 strsql.append(fieldItem.getItemid());
				 strsql.append(",");
			}
			strsql.append("e01a1 from ");
			strsql.append("k01 where e01a1='");
			strsql.append(nid);
			strsql.append("'");
			List rs = ExecuteSQL.executeMyQuery(strsql.toString());			
			if(rs!=null && rs.size()>0)
		    	printinfo(rs,fieldlist,setlist);
			else
				printnoinfo(nid,fieldlist,setlist);
		}
	}
	/**
	 * 显示打出印基准岗位信息
	 * @param fieldlist
	 * @param setlist
	 * @param nid
	 */
	private void showjobdata(List fieldlist,List setlist,String nid){
		if(!fieldlist.isEmpty())
		{
			if(setflag!=null&& "1".equals(setflag)){
				for(int j=0;j<setlist.size();j++){
					FieldSet fs=(FieldSet)setlist.get(j);
					String sqls="select * from "+fs.getFieldsetid() +" where h0100='"+nid+"'";
					List temprs = ExecuteSQL.executeMyQuery(sqls);
					if(temprs.size()<1){
						setlist.remove(j);
						j--;
					}
				}
			}
			
			StringBuffer strsql=new StringBuffer();
			strsql.append("select ");
			for(int i=0;i<fieldlist.size();i++)
			{
				 FieldItem fieldItem=(FieldItem)fieldlist.get(i);
				 strsql.append(fieldItem.getItemid());
				 strsql.append(",");
			}
			strsql.append("h0100 from ");
			strsql.append("h01 where h0100='");
			strsql.append(nid);
			strsql.append("'");
			List rs = ExecuteSQL.executeMyQuery(strsql.toString());
			
			RecordVo constent = ConstantParamter.getConstantVo("PS_C_CODE");
			String codesetid = constent.getString("str_value");
			String sql = "select codeitemdesc from codeitem where codesetid='"+codesetid+"' and codeitemid='"+nid+"'";
			 List codelist = ExecuteSQL.executeMyQuery(sql);
			 if(codelist.size()>0){
			  LazyDynaBean ldb = (LazyDynaBean)codelist.get(0);
			  FieldItem fi = new FieldItem();
			  fi.setItemdesc("岗位名称");
			  fi.setItemid("e0122");
			  fi.setItemtype("A");
			  fi.setCodesetid("0");
			  fieldlist.add(0, fi);
			  LazyDynaBean ldbs = new LazyDynaBean();
			  if(rs.size()>0)
				  ldbs = (LazyDynaBean)rs.get(0);
			  ldbs.set("e0122", ldb.get("codeitemdesc"));
			  rs.clear();
			  rs.add(ldbs);
			 }
			if(rs!=null && rs.size()>0)
		    	printinfo(rs,fieldlist,setlist);
			else
				printnoinfo(nid,fieldlist,setlist);
		}
	}
	
	private void printnoinfo(String nid,List fieldlist,List setlist)
	{
		JspWriter out=pageContext.getOut();
		int n=0;
		int flag=0;
		String org_browse_format=this.getOrg_Browse_format();
		if(setflag!=null&& "1".equals(setflag)){
			for(int j=0;j<setlist.size();j++){
				FieldSet fs=(FieldSet)setlist.get(j);
				String sqls="";
				if("1".equals(this.infokind))
					 sqls ="select * from "+pre+fs.getFieldsetid() +" where a0100='"+nid+"'";
				else if("2".equals(this.infokind))
					 sqls="select b0110 from "+fs.getFieldsetid() +" where b0110='"+nid+"'";
				else if("3".equals(this.infokind))
					sqls="select * from "+fs.getFieldsetid() +" where e01a1='"+nid+"'";
				else
					sqls="select * from "+fs.getFieldsetid() +" where h0100='"+nid+"'";
				List temprs = ExecuteSQL.executeMyQuery(sqls);
				if(temprs.size()<1){
					setlist.remove(j);
					j--;
				}
			}
		}
		
		 try
		 {
			 /*if(infokind.equals("2"))
		     {*/
		    	 out.println("<table  border=\"0\" width=\"100\"  cellspacing=\"0\"  align=\"center\" cellpadding=\"0\"><tr>");
		    	 out.println("<td align=\"center\" nowrap>");
		    	 out.println("没有信息");
		    	 out.println("</td>");
		    	 out.println("</tr>");
		    	 out.println("</table>");
		    /* }else{
		     out.println("<link href=\"/css/css1.css\" rel=\"stylesheet\" type=\"text/css\">");
		     out.println("<table border=\"0\" cellspacing=\"1\"  align=\"left\" cellpadding=\"1\">");
		     out.println("<tr>");
		     out.println("<td align=\"left\" nowrap valign=\"top\">");
		     out.println("<div id=\"maininfo\" style=\"background-color:transparent;\" >");
		     out.println("<table  border=\"0\" width=\"800\"  cellspacing=\"1\"  align=\"left\" cellpadding=\"1\" class=\"ListTable\">");	    
			 if(!fieldlist.isEmpty())
			 {
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fieldItem=(FieldItem)fieldlist.get(i);
					if(fieldMap.get(fieldItem.getItemid().toUpperCase())!=null)
						continue;
					if("N".equalsIgnoreCase(fieldItem.getItemtype()))
					{
						if(org_browse_format.equals("0"))
						 {
							 out.println("<tr class=\"trShallow\">");
						 }else
						 {
				    	   if(flag==0){
				             if(n%2==0){
				            	out.println("<tr class=\"trShallow\">");
			    	         }else
			    	         {
		    		         	out.println("<tr class=\"trDeep\">");
		     		         }
			    	         n++;
			                flag=1;  
		    		       }else
		     		       {
		                     flag=0; 
		                    }
						 }
						if(org_browse_format.equals("0"))
						 {
							 out.println("<td align=\"left\" nowrap valign=\"top\">");
							 out.println("&nbsp;&nbsp;"+fieldItem.getItemdesc() + ":");
			    			 out.println("</td>");
			    			 out.println("</tr>");
						 }
						else
						{
					   out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
					   out.println("</td>");
					   out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;");
					   out.println("</td>");
					   if(flag==0){
					   	out.println("</tr>");
					   }
					   else
					   {
					   	 if(fieldlist.size()-1==i){ 
			               out.println("<td colspan=\"2\">");
					   	   out.println("</td>");
					   	   out.println("</tr>");
					   	  }
					   }	
						}
					}else if("D".equalsIgnoreCase(fieldItem.getItemtype()))
					{
						if(org_browse_format.equals("0"))
						 {
							 out.println("<tr class=\"trShallow\">");
						 }else
						 {
					   if(flag==0){
				         if(n%2==0){
				         	out.println("<tr class=\"trShallow\">");
				         }else
				         {
				         	out.println("<tr class=\"trDeep\">");
				         }
				         n++;
			             flag=1;  
				       }else
				       {
		                  flag=0; 
		               }
						 }
						if(org_browse_format.equals("0"))
						 {
							out.println("<td align=\"left\" nowrap valign=\"top\">");
							 out.println("&nbsp;&nbsp;"+fieldItem.getItemdesc() + ":");
			    			 out.println("</td>");
			    			 out.println("</tr>");
						 }else
						 {
					   out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
					   out.println("</td>");
					   out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;");
                 	   out.println("</td>");
					   if(flag==0){
					   	out.println("</tr>");
					   }
					   else
					   {
					   	 if(fieldlist.size()-1==i){ 
			               out.println("<td colspan=\"2\">");
					   	   out.println("</td>");
					   	   out.println("</tr>");
					   	  }
					   }
						 }
					}else if("A".equalsIgnoreCase(fieldItem.getItemtype()))
					{	
						if(org_browse_format.equals("0"))
						 {
							 out.println("<tr class=\"trShallow\">");
						 }else
						 {
					   if(flag==0){
				         if(n%2==0){
				         	out.println("<tr class=\"trShallow\">");
				         }else
				         {
				         	out.println("<tr class=\"trDeep\">");
				         }
				         n++;
			             flag=1;  
				       }else
				       {
		                  flag=0; 
		               }
						 }
						if(org_browse_format.equals("0"))
						 {
							out.println("<td align=\"left\" nowrap valign=\"top\">");
							 out.println("&nbsp;&nbsp;"+fieldItem.getItemdesc() + ":");
							 if(!"0".equals(fieldItem.getCodesetid()))
							   {
								   if("b0110".equalsIgnoreCase(fieldItem.getItemid()) || "e01a1".equalsIgnoreCase(fieldItem.getItemid()))
								   {
									      if(this.orgtype==null||!this.orgtype.equalsIgnoreCase("vorg"))
									      {
									    	  List codefindset = ExecuteSQL.executeMyQuery("select  codeitemdesc from organization where codeitemid='" + nid + "'");
										  	  if(!codefindset.isEmpty())
										   	  {
										   	      LazyDynaBean recset=(LazyDynaBean)codefindset.get(0);
										   	      out.println(recset.get("codeitemdesc").toString());
										   	  }
									      }else
									      {
									    	  String sqls="SELECT codeitemdesc from vorganization where codeitemid='"+nid+"'";
									    	  List temprs =ExecuteSQL.executeMyQuery(sqls);
									    	  if(!temprs.isEmpty())
										   	  {
										   	      LazyDynaBean recset=(LazyDynaBean)temprs.get(0);
										   	      out.println(recset.get("codeitemdesc").toString());
										   	  }
									      }
								   
									   	  
									}else if("e0122".equalsIgnoreCase(fieldItem.getItemid()))
									{
									   		Connection conn=null;
									   		try{
									   			conn=AdminDb.getConnection();
									   		    StationPosView posview=getStationPos(nid,conn);
									   		   if(posview!=null)
									    	   {
										    	  if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0){
										    		  String bumen=AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"";
										    		  if(bumen.length()<1){
										    			  bumen=AdminCode.getCode("UN",posview.getItemvalue())!=null?AdminCode.getCode("UN",posview.getItemvalue()).getCodename():"";
										    		  }
//										    		  out.println(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"");
										    		  out.println(bumen);
											   }
									    	   }
									   		}catch(Exception e)
									   		{
									   			e.printStackTrace();
									   		}finally
									   		{
									   			if (conn != null){
													conn.close();
												}
									   		}
								   }else
									   out.println("&nbsp;");
							   }
							   else
							      out.println("&nbsp;");  	 
			    			 out.println("</td>");
			    			 out.println("</tr>");
				
						 }else
						 {
					   out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
					   out.println("</td>");
					   out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\">");
					   if(!"0".equals(fieldItem.getCodesetid()))
					   {
						   if("b0110".equalsIgnoreCase(fieldItem.getItemid()) || "e01a1".equalsIgnoreCase(fieldItem.getItemid()))
						   {
							      if(this.orgtype==null||!this.orgtype.equalsIgnoreCase("vorg"))
							      {
							    	  List codefindset = ExecuteSQL.executeMyQuery("select  codeitemdesc from organization where codeitemid='" + nid + "'");
								  	  if(!codefindset.isEmpty())
								   	  {
								   	      LazyDynaBean recset=(LazyDynaBean)codefindset.get(0);
								   	      out.println(recset.get("codeitemdesc").toString());
								   	  }
							      }else
							      {
							    	  String sqls="SELECT codeitemdesc from vorganization where codeitemid='"+nid+"'";
							    	  List temprs =ExecuteSQL.executeMyQuery(sqls);
							    	  if(!temprs.isEmpty())
								   	  {
								   	      LazyDynaBean recset=(LazyDynaBean)temprs.get(0);
								   	      out.println(recset.get("codeitemdesc").toString());
								   	  }
							      }
						   
							   	  
							}else if("e0122".equalsIgnoreCase(fieldItem.getItemid()))
							{
							   		Connection conn=null;
							   		try{
							   			conn=AdminDb.getConnection();
							   		    StationPosView posview=getStationPos(nid,conn);
							   		   if(posview!=null)
							    	   {
								    	  if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0){
								    		  String bumen=AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"";
								    		  if(bumen.length()<1){
								    			  bumen=AdminCode.getCode("UN",posview.getItemvalue())!=null?AdminCode.getCode("UN",posview.getItemvalue()).getCodename():"";
								    		  }
//								    		  out.println(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"");
								    		  out.println(bumen);
									   }
							    	   }
							   		}catch(Exception e)
							   		{
							   			e.printStackTrace();
							   		}finally
							   		{
							   			if (conn != null){
											conn.close();
										}
							   		}
						   }else
							   out.println("&nbsp;");
					   }
					   else
					      out.println("&nbsp;");
					   out.println("</td>");
					   if(flag==0){
					   	out.println("</tr>");
					   }
					   else
					   {
					   	 if(fieldlist.size()-1==i){ 
			               out.println("<td colspan=\"2\">");
					   	   out.println("</td>");
					   	   out.println("</tr>");
					   	  }
					   }	
						 }
					}else if("M".equalsIgnoreCase(fieldItem.getItemtype()))
					{
						if(org_browse_format.equals("0"))
						 {
							 out.println("<tr class=\"trShallow\">");
							 out.println("<td align=\"left\" nowrap valign=\"top\">");
							 out.println("&nbsp;&nbsp;"+fieldItem.getItemdesc() + ":");
							 out.println("</td>");
						   	 out.println("</tr>");
						 }else
						 {
						if(flag==0){	
				          if(n%2==0){
				          	out.println("<tr class=\"trShallow\">");
				          }else
				          {
				          	out.println("<tr class=\"trDeep\">");
				          }
				          n++;
				          flag=1;
				          out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
				          out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
				          out.println("</td>");
				          out.println("<td align=\"left\"  valign=\"top\"  colspan=\"3\">");
				          out.println("&nbsp;");
				          out.println("</td>");
				          
				          
				          
				        }else
				        {
				        	flag=0;
				        	out.println("<td colspan=\"2\">");
				        	out.println("</td>");
				        	out.println("</tr>");
				        	 if(flag==0){
				                if(n%2==0){
				                	out.println("<tr class=\"trShallow\">");			                	
				                }else{
				                	out.println("<tr class=\"trDeep\">"); 
				                }
				                n++;
				                flag=1; 
				             }else
				             {
				             	flag=0;
				             }
				        	 out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
				        	 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
				             out.println("</td>");
				             out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\" colspan=\"3\">");
				             out.println("&nbsp;");
				             out.println("</td>");
				             out.println("</tr>");
				        }
						flag=0;
						 }
						
						
						
						
						
						
					}
				}
		   } 
		   out.println("</table>");
		   out.println("</div>");
		   out.println("</td>");
		   out.println("</tr>");
//		   if ("1".equals(this.getWriteable())&&!this.getA01state(pre,nid).equals("0")){
//				out.println("<tr class=\"trDeep\">");
//				out.println("<td colspans='4' align='center'>");
//				out.println("<button name='sd' class='mybutton' onclick='updateinfo()'>修改</button>");
//				out.println("</td>");
//				out.println("</tr>");
//			} 
		   out.println("<tr>");
		   out.println("<td align=\"left\" nowrap valign=\"top\">");		   
		   out.println("<div id=\"set\">");
		   printset(setlist,out);
		   out.println("</div>");
		   out.println("</td>");
		   out.println("</tr>");
		   out.println("</table>");
		 }*/
	   
		}catch(Exception e)
		 {
		   System.out.println("显示主集出错!");
		   e.printStackTrace();	
	     } 
	}
	private StationPosView getStationPos(String code,Connection conn)
	{
		
		
		String pre="@K";	
		Statement stmt = null;
		ResultSet rs=null;
		boolean ispos=false;
		boolean isdep=false;
		boolean isorg=false;
		StringBuffer strsql=new StringBuffer();
		try{
		    ContentDAO db=new ContentDAO(conn);
		    if(code!=null)
		    {
			while(!"UM".equalsIgnoreCase(pre))
			{
			  strsql.delete(0,strsql.length());
			  strsql.append("select * from organization");
			  strsql.append(" where codeitemid='");
			  strsql.append(code);
			  strsql.append("'");					
			  rs =db.search(strsql.toString());	//执行当前查询的sql语句	
			 if(rs.next())
			 {
				pre=rs.getString("codesetid");
				if("UM".equalsIgnoreCase(pre))
				{
					StationPosView posview=new StationPosView();
					posview.setItem("e0122");
					posview.setItemvalue(rs.getString("codeitemid"));
					posview.setItemviewvalue(rs.getString("codeitemdesc"));
					return posview;
				}
				code=rs.getString("parentid");				
			 }			
			}
		   }
		    return null;
		    }catch (SQLException sqle){
				sqle.printStackTrace();
			}finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}	
			

		return null;
	}
	private void printinfo(List rs,List fieldlist,List setlist)
	{
		LazyDynaBean rec=(LazyDynaBean)rs.get(0);
		JspWriter out=pageContext.getOut();  
		int n=0;
		int flag=0;
		String org_browse_format=this.getEmp_Browse_format();
		try
		{
			if(setflag!=null&& "1".equals(setflag)){
				for(int j=0;j<setlist.size();j++){
					FieldSet fs=(FieldSet)setlist.get(j);
					String sqls="";
					if("1".equals(this.infokind))
						 sqls ="select * from "+pre+fs.getFieldsetid() +" where a0100='"+nid+"'";
					else if("2".equals(this.infokind))
						 sqls="select b0110 from "+fs.getFieldsetid() +" where b0110='"+nid+"'";
					else if("3".equals(this.infokind)){
						if("K00".equalsIgnoreCase(fs.getFieldsetid())){
							sqls="select * from "+fs.getFieldsetid() +" where e01a1='"+nid+"' and flag<>'K'";
						}else
							sqls="select * from "+fs.getFieldsetid() +" where e01a1='"+nid+"'";
					}
					else{
						
						sqls = "select * from "+fs.getFieldsetid()+" where h0100 ='"+nid+"'";
					}
					List temprs = ExecuteSQL.executeMyQuery(sqls);
					if(temprs.size()<1){
						setlist.remove(j);
						j--;
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	    try
	    {
	    	
		     out.println("<hrms:themes/>");
		     out.println("<table border='0' cellspacing='1'  align='left' cellpadding='1' width='100%'>");
		     out.println("<tr>");
		     out.println("<td align='left' nowrap valign='top'>");
		     out.println("<div id='maininfo' style='background-color:transparent;' >");
		     out.println("<table  border='0' width='100%'  cellspacing='1'  align='left' cellpadding='1' class=''>");
		     Connection conn = null;
		     String org_brief="";
			try{
				conn=AdminDb.getConnection();
				ParameterXMLBo bo = new ParameterXMLBo(conn);
				org_brief=bo.getBriefParaValue();
			   }catch (Exception sqle){
					sqle.printStackTrace();
				}finally{
					try{
						if (conn != null){
							conn.close();
						}
					}catch (SQLException sql){
						//sql.printStackTrace();
					}
				}
		     
				String orgFieldID="";
				String contentType="";
				if(org_brief != null && org_brief.trim().length()>0){
					String[] org_brief_Arr = org_brief.split(",");
				    orgFieldID=org_brief_Arr[0];
				    contentType=org_brief_Arr[1];
				}
		     if(!fieldlist.isEmpty())
			 {
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fieldItem=(FieldItem)fieldlist.get(i);					
					if(fieldMap.get(fieldItem.getItemid().toUpperCase())!=null)
						continue;					
					if(rec.get(fieldItem.getItemid())==null|| "".equals((String)rec.get(fieldItem.getItemid())))
						continue;
					if("N".equalsIgnoreCase(fieldItem.getItemtype()))
					{
						
						 if("0".equals(org_browse_format))
						 {
							 if(n%2==0){
		    			         out.println("<tr class='trShallow1'>");
		    			     }else{
		    			         out.println("<tr class='trDeep1'>");
	    				     }
							 n++;
						 }else
						 {
		    			   if(flag==0){
		    		         if(n%2==0){
	    			         	out.println("<tr class='trShallow1'>");
	    			         }else
	    			         {
	    			         	out.println("<tr class='trDeep1'>");
    				         }
    				         n++;
    			             flag=1;  
		    		       }else
		    		       {
		                      flag=0; 
		                   }
						 }
						 if("0".equals(org_browse_format))
						 {
							 out.println("<td align='right' nowrap valign='middle' class='RecordCellleft'>");
							 out.println("&nbsp;&nbsp;"+fieldItem.getItemdesc()/* + ":"*/);
			    			 out.println("</td>");
			    			 out.println("<td align='left' nowrap valign='middle' class='RecordCellright'>");
							 out.println(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()).toString(),fieldItem.getDecimalwidth()) + "&nbsp;");
			    			 out.println("</td>");
			    			 out.println("</tr>");
						 }else
						 {
		    			   out.println("<td align='right' nowrap valign='middle' class='RecordCellleft'>");
	    				   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
		    			   out.println("</td>");
		    			   out.println("<td align='left' nowrap valign='middle' class='RecordCellright'>");
		    			   out.println(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()).toString(),fieldItem.getDecimalwidth()) + "&nbsp;");
		    			   out.println("</td>");
	     				   if(flag==0){
	     					  out.println("<td width='60%'>");
	    				   	   out.println("</td>");
	    				   	out.println("</tr>");
	    				   }
						 }
		    		/*	}*/
		     		}else if("D".equalsIgnoreCase(fieldItem.getItemtype()))
					{
		     			/*if(org_browse_format.equals("0"))
		     			{
		     				
		     			}
		     			else
		     			{*/
		     			if("0".equals(org_browse_format))
		     			{
							 if(n%2==0){
		    			         out.println("<tr class='trShallow1'>");
		    			     }else{
		    			         out.println("<tr class='trDeep1'>");
	    				     }
							 n++;
		     			}
		     			else
		     			{
		    			   if(flag==0){
		    		         if(n%2==0){
		    		         	out.println("<tr class='trShallow1'>");
		    		         }else
		    		         {
		    		         	out.println("<tr class='trDeep1'>");
		    		         }
		    		         n++;
		    	             flag=1;  
	    			       }else
		    		       {
		                      flag=0; 
		                   }
		     			}
		     			if("0".equals(org_browse_format))
		     			{
		     				out.println("<td align='right' class='RecordCellleft' nowrap valign='middle'>");
		     				out.println( "&nbsp;&nbsp;"+fieldItem.getItemdesc()/* + ":"*/);
			    			   out.println("</td>"); 
			    			   out.println("<td align='left'  nowrap valign='middle' class='RecordCellright'>");
			     				 if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==10)
				    			   {
				        			   	out.println(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)) + "&nbsp;");
			     				   }else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)
				    			   {
				        			   	out.println(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)) + "&nbsp;");
				    			   }else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==7)
				    			   {
					        		   	out.println(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)) + "&nbsp;");
				    			   }else
		                           {
				        			    out.println("&nbsp;");
		                           }					
				    			   out.println("</td>");
			    			   out.println("</tr>");
		     			}
		     			else
		     			{
		    			   out.println("<td align='right' nowrap valign='middle' class='RecordCellleft'>");
				    	   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
			    		   out.println("</td>");
		    			   out.println("<td align='left' nowrap valign='middle' class='RecordCellright'>");
		    			   String dateTime = rec.get(fieldItem.getItemid().toLowerCase()).toString();
		    			   if (StringUtils.isNotEmpty(dateTime)) {
		    				   if (dateTime.length() >= 10 && fieldItem.getItemlength() == 18) {
		    					   out.println(new FormatValue().format(fieldItem, dateTime.substring(0, 19)) + "&nbsp;");
		    				   } else if (dateTime.length() >= 10 && fieldItem.getItemlength() == 16) {
		    					   out.println(new FormatValue().format(fieldItem, dateTime.substring(0, 16)) + "&nbsp;");
		    				   } else if (dateTime.length() >= 10 && fieldItem.getItemlength() == 10) {
		    					   out.println(new FormatValue().format(fieldItem, dateTime.substring(0, 10)) + "&nbsp;");
		    				   } else if (dateTime.length() >= 10 && fieldItem.getItemlength() == 4) {
		    					   out.println(new FormatValue().format(fieldItem, dateTime.substring(0, 4)) + "&nbsp;");
		    				   } else if (dateTime.length() >= 10 && fieldItem.getItemlength() == 7) {
		    					   out.println(new FormatValue().format(fieldItem, dateTime.substring(0, 7)) + "&nbsp;");
		    				   } else {
		    					   out.println("&nbsp;");
		    				   }
		    			   } else {
		    				   out.println("&nbsp;");
		    			   }
		    			   out.println("</td>");
		     			   if(flag==0){
		     				  out.println("<td width='60%'>");
	    				   	   out.println("</td>");
		        			   	out.println("</tr>");
		    			   }
		     			}
		     		/*	}*/
					}else if("A".equalsIgnoreCase(fieldItem.getItemtype()))
					{		
						StringBuffer text=new StringBuffer();
						if("0".equals(org_browse_format))
						{
							
							 if(n%2==0){
		    			         text.append("<tr class='trShallow1'>");
		    			     }else{
		    			         text.append("<tr class=\"trDeep1\">");
	    				     }
							 n++;
						}
						else
						{
     					   if(flag==0){
	        			        if(n%2==0){
	            			         text.append("<tr class=\"trShallow1\">");
    				            }else
    				             {
		     		             	text.append("<tr class=\"trDeep1\">");
		    		              }
	    			         n++;
	     		             flag=1;  
		    		       }else
				           {
		                      flag=0; 
		                   }
						}
						if("0".equals(org_browse_format)) {
							text.append("<td align=\"right\" nowrap valign=\"middle\" class=\"RecordCellleft\">");
							text.append("&nbsp;&nbsp;" + fieldItem.getItemdesc());
							text.append("</td>");
							text.append("<td align=\"left\" nowrap valign=\"middle\" class=\"RecordCellright\">");
							if (!"0".equals(fieldItem.getCodesetid())) {
								String codevalue = rec.get(fieldItem.getItemid()) != null ? rec.get(fieldItem.getItemid()).toString() : "";
								if (codevalue != null && codevalue.trim().length() > 0 && fieldItem.getCodesetid() != null && fieldItem.getCodesetid().trim().length() > 0) {
									String unum = "";

									if (this.orgtype != null && "vorg".equalsIgnoreCase(this.orgtype)) {
										String sqls = "SELECT codeitemdesc from vorganization where codeitemid='" + nid + "'";
										List temprs = ExecuteSQL.executeMyQuery(sqls);
										if (!temprs.isEmpty()) {
											LazyDynaBean recset = (LazyDynaBean) temprs.get(0);
											unum = recset.get("codeitemdesc").toString();
										} else {
											unum = AdminCode.getCodeName("UM", codevalue);
										}
									}

									if(StringUtils.isBlank(unum)){
										if ("UN".equalsIgnoreCase(fieldItem.getCodesetid())) {
											unum=AdminCode.getCodeName("UN", codevalue);
										}else{
											unum = AdminCode.getCodeName(fieldItem.getCodesetid(), codevalue);
											if (StringUtils.isBlank(unum))
												unum = AdminCode.getCodeName("UM", codevalue);
											if (StringUtils.isBlank(unum))
												unum = AdminCode.getCodeName("UN", codevalue);
										}
									}
									if(StringUtils.isBlank(unum)){
										n--;
										continue;
									}else{
										text.append(unum+"&nbsp;");
									}
								} else
									text.append("&nbsp;");
							} else {
								/** 不知道为什么b01表里有些值会变成字符串“null”   在这里将其过滤掉 **/
								String desc = (String) rec.get(fieldItem.getItemid());
								desc = "null".equalsIgnoreCase(desc) ? "" : desc;
								text.append(PubFunc.toAjax(desc) + "&nbsp;");
							}
							text.append("</td>");
							text.append("</tr>");
							out.println(text.toString());
						}
						else
						{
							out.println(text.toString());
	    				   out.println("<td align=\"right\" nowrap valign=\"middle\" class=\"RecordCellleft\">");
          				   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
	    				   out.println("</td>");
	    				   out.println("<td align=\"left\" nowrap valign=\"middle\" class=\"RecordCellright\">");
	    				   if(!"0".equals(fieldItem.getCodesetid()))
	    				   {
	    				   	  String codevalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
//					  	  List codefindset = ExecuteSQL.executeMyQuery("select  codesetid from organization where codeitemid='" + codevalue + "'");
//					   	  String codeset="UN";
//					  	  if(!codefindset.isEmpty())
//					   	  {
//					   	      LazyDynaBean recset=(LazyDynaBean)codefindset.get(0);
//					   	      codeset=recset.get("codesetid").toString();
//					   	   }
					  	  //System.out.println(codevalue + "d" + fieldItem.getItemid() + "fi" + fieldItem.getItemdesc());
		    			  	  if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
		    			  	  {		
		    			  		  String unum=AdminCode.getCodeName("UN"/*codeset*/,codevalue);
   
		    			  		  if(this.orgtype!=null&& "vorg".equalsIgnoreCase(this.orgtype))

			    		  		  {
					  			  
			     		  			String sqls="SELECT codeitemdesc from vorganization where codeitemid='"+nid+"'";
			    			    	  List temprs =ExecuteSQL.executeMyQuery(sqls);
			    			    	  if(!temprs.isEmpty())
			    				   	  {
			    				   	      LazyDynaBean recset=(LazyDynaBean)temprs.get(0);
			    				   	      unum=recset.get("codeitemdesc").toString();
			    				   	  }else{
			    				   		if(unum.length()<1){
			    				  			  unum=AdminCode.getCodeName("UM"/*codeset*/,codevalue);
			    				  		  }
			    				   	  }
					  			  
					  			
			    		  		  }else
		    			  		  {
		    			  			if(unum.length()<1){
		    				  			  unum=AdminCode.getCodeName("UM"/*codeset*/,codevalue);
		    				  		  }
		    			  		  }
					  		  
		    			  		  if("UN".equalsIgnoreCase(fieldItem.getCodesetid())){

//					  	  	   out.println(AdminCode.getCodeName(fieldItem.getCodesetid()/*codeset*/,codevalue) + "&nbsp;");
//					  	  	   System.out.println(AdminCode.getCodeName(fieldItem.getCodesetid()/*codeset*/,codevalue));

//					  	  	   out.println(AdminCode.getCodeName(fieldItem.getCodesetid()/*codeset*/,codevalue) + "&nbsp;");
					  	  	   //System.out.println(AdminCode.getCodeName(fieldItem.getCodesetid()/*codeset*/,codevalue));

			    		  	  	   out.println(unum);
			    		   	    }
			    		  	  	   else 
		    				    {
					  	  		   
				    		        String codedesc=AdminCode.getCodeName(fieldItem.getCodesetid(),codevalue);
				    		        if(codedesc==null||codedesc.length()<=0)
				    		        	codedesc=AdminCode.getCodeName("UN"/*codeset*/,codevalue);
			    			    	out.println( codedesc+ "&nbsp;");
			    			    }
			    		  	  }
			    		   	  else
		    				    out.println("&nbsp;");
		    			   }
		    			   else{
		    				   /** 不知道为什么b01表里有些值会变成字符串“null”   在这里将其过滤掉 **/
		    				   String desc = (String)rec.get(fieldItem.getItemid());
		    				   desc = "null".equalsIgnoreCase(desc)?"":desc;
		    			      out.println(PubFunc.toAjax(desc) + "&nbsp;");
		    			      //out.println(PubFunc.toAjax((String)rec.get(fieldItem.getItemid())) + "&nbsp;");
		    			   }
		    			   out.println("</td>");
		    			   if(flag==0){
		    				   out.println("<td width=\"60%\">");
	    				   	   out.println("</td>");
		     			   	out.println("</tr>");
	    				   }
						}
						/*}*/
					}else if("M".equalsIgnoreCase(fieldItem.getItemtype()))
					{
						/*if(org_browse_format.equals("0"))
						{
							
						}
						else
						{*/
						if("0".equals(org_browse_format))
						{
							String type="";
							
							/*if(n%2==0){
		    			         out.println("<tr class=\"trLine\">");
		    			     }else{*/
		    			         out.println("<tr class=\"trDeep1\">");
	    				     //}
							
							String fieldvalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
							String fieldvalue2html="";
							if((fieldItem.getItemid().equalsIgnoreCase(orgFieldID))||((!fieldItem.getItemid().equalsIgnoreCase(orgFieldID)&&1==fieldItem.getInputtype()))){
								fieldvalue2html=fieldvalue;
							}else{
								fieldvalue2html=PubFunc.toHtml(fieldvalue);
							}
							if(contentTypeField!=null)
							{
							   type=(String)rec.get(contentTypeField.toLowerCase());
							}
							
							out.println("<td align=\"right\"  valign=\"middle\" class=\"RecordCellleft "+(n%2==0?"tdLine":"")+"\">");
		    			    out.println("&nbsp;&nbsp;"+fieldItem.getItemdesc() /*+ ":"*/);
		    			    out.println("</td>");
		    			    
							if(fieldvalue.trim().length()>0&&fieldItem.getItemid().equalsIgnoreCase(this.orgBriefField))
	    			        {
		    			    	out.println("<td align=\"left\"  valign=\"middle\"  colspan=\"2\" "+(n%2==0?"class=\"tdLine\"":"")+">");
	    			        }
		    			    else
		    			    {
		    			    	out.println("<td align=\"left\"  valign=\"middle\" class=\"RecordCellright "+(n%2==0?"tdLine":"")+"\">");
		    			    	
		    			    }
							 n++;
		    			    Matcher m=p.matcher(fieldvalue);
					         
					          
		    		          if(this.contentTypeField!=null&&this.contentTypeField.length()>0)
			    	          {
			    	        	 if(fieldItem.getItemid().equalsIgnoreCase(this.orgBriefField))
			    	        	 {
			     	            	 if(type!=null&&type.length()>0)
			    	            	 {
			    	            		 if("0".equals(type))
			    	            		 {
				                			 if(m.find())
			    	            			   out.println("<a href=\""+fieldvalue+"\" target=\"_blank\">");
			    	            		 }
			    	            	 }
			    	        	 }
			     	          }
		     				  //out.println(fieldvalue + "&nbsp;");
		    		          out.println(fieldvalue2html + "&nbsp;");
		     				 if(type!=null&&type.length()>0)
			    			 {
			    				 if("0".equalsIgnoreCase(type))
			    				 {
			    					 if(m.find())
			    		               out.println("</a>");
		    					 }
		    				 }
					          
		    		          out.println("</td>");
		    		          out.println("</tr>");
						}
						else
						{
	    					if(flag==0){
    				          /*if(n%2==0){
    				          	out.println("<tr class=\"trShallow\">");
    				          }else
    				          {
	     			          	out.println("<tr class=\"trDeep1\">");
    				         }*/
	     			          	/*if(n%2==0){
			    			         out.println("<tr class=\"trLine\">");
			    			     }else{*/
			    			         out.println("<tr class=\"trDeep1\">");
		    				     //}
	    			          
	    			          flag=1;
	    					
	    			          String type="";
	    			          String fieldvalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
	    			          String fieldvalue2html="";
								if((fieldItem.getItemid().equalsIgnoreCase(orgFieldID))||((!fieldItem.getItemid().equalsIgnoreCase(orgFieldID)&&1==fieldItem.getInputtype()))){
									fieldvalue2html=fieldvalue;
								}else{
									fieldvalue2html=PubFunc.toHtml(fieldvalue);
								}
	    			          if(contentTypeField!=null){
	    			              type=(String)rec.get(contentTypeField.toLowerCase());
	    					}
	    			          //fieldItem.getItemid().equalsIgnoreCase(this.orgBriefField)
	    			          if(fieldvalue.trim().length()>0&&fieldItem.getItemid().equalsIgnoreCase(this.orgBriefField))
	    			          {
	   			             
		     		              out.println("<td align=\"left\"  valign=\"top\"  colspan=\"5\" "+(n%2==0?"class=\"tdLine\"":"")+">");
		    		          }
		    		          else
		    		          {
				        	     out.println("<td align=\"right\" nowrap valign=\"middle\" class=\"RecordCellleft "+(n%2==0?"tdLine":"")+"\">");
					             out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
					             out.println("</td>");
					             out.println("<td align=\"left\"  valign=\"middle\"  colspan=\"4\" class=\"RecordCellright "+(n%2==0?"tdLine":"")+"\">");
		    		          }
	    			          n++;
			    	          Matcher m=p.matcher(fieldvalue);
				         
				          
		    		          if(this.contentTypeField!=null&&this.contentTypeField.length()>0)
			    	          {
			    	        	 if(fieldItem.getItemid().equalsIgnoreCase(this.orgBriefField))
			    	        	 {
			     	            	 if(type!=null&&type.length()>0)
			    	            	 {
			    	            		 if("0".equals(type))
			    	            		 {
				                			 if(m.find())
			    	            			   out.println("<a href=\""+fieldvalue+"\" target=\"_blank\">");
			    	            		 }
			    	            	 }
			    	        	 }
			     	          }
		     				  //out.println(fieldvalue + "&nbsp;");
		     				out.println(fieldvalue2html + "&nbsp;");
		     				 if(type!=null&&type.length()>0)
			    			 {
			    				 if("0".equalsIgnoreCase(type))
			    				 {
			    					 if(m.find())
			    		               out.println("</a>");
		    					 }
		    				 }
					          
		    		          out.println("</td>");
			    	        }else
			    	        {
			    	        	flag=0;
			    	        	out.println("<td colspan=\"3\">");
			    	        	out.println("</td>");
				            	out.println("</tr>");
				            	 if(flag==0){
				                  /* if(n%2==0){
				                    	out.println("<tr class=\"trShallow\">");			                	
				                   }else{*/
				                    	out.println("<tr class=\"trDeep\">"); 
		     		                //}
			    	                
			    	                flag=1; 
		    		             }else
		    		             {
		    		             	flag=0;
		    		             }
				            	 String type="";
			    		         String fieldvalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
			    		         String fieldvalue2html="";
									if((fieldItem.getItemid().equalsIgnoreCase(orgFieldID))||((!fieldItem.getItemid().equalsIgnoreCase(orgFieldID)&&1==fieldItem.getInputtype()))){
										fieldvalue2html=fieldvalue;
									}else{
										fieldvalue2html=PubFunc.toHtml(fieldvalue);
									}
			    		         if(contentTypeField!=null){
			    		         
		    			              type=(String)rec.get(contentTypeField.toLowerCase());
			    		         }
			    		         
			    		         out.println("<td align=\"right\" nowrap valign=\"middle\" class=\"RecordCellleft "+(n%2==0?"tdLine":"")+"\">");
	    			             out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
		    		             out.println("</td>");
		    		             
				    	          if(fieldvalue.trim().length()>0&&fieldItem.getItemid().equalsIgnoreCase(this.orgBriefField))
				    	              out.println("<td align=\"center\"  valign=\"middle\"  colspan=\"4\" "+(n%2==0?"class=\"tdLine\"":"")+">");
				     	          else
				     	        	  out.println("<td align=\"left\"  valign=\"top\"  colspan=\"4\" class=\"RecordCellright "+(n%2==0?"tdLine":"")+"\">");
				    	          
				    	          n++;
				    	          Matcher m=p.matcher(fieldvalue);
				    	         
					          
		    			          if(this.contentTypeField!=null&&this.contentTypeField.length()>0)
			    		          {
			    		        	 if(fieldItem.getItemid().equalsIgnoreCase(this.orgBriefField))
			    		        	 {
			    		            	 if(type!=null&&type.length()>0)
			    		            	 {
			    		            		 if("0".equals(type))
			    		            		 {
			      		            			 if(m.find())
			    		            			   out.println("<a href=\""+fieldvalue+"\" target=\"_blank\">");
			    		            		 }
			    		            	 }
			    		        	 }
			    		          }
		    			        //out.println(fieldvalue + "&nbsp;");
				     				out.println(fieldvalue2html + "&nbsp;");
			    				 if(type!=null&&type.length()>0)
				    			 {
				    				 if("0".equalsIgnoreCase(type))
				    				 {
				    					 if(m.find())
				     		               out.println("</a>");
				     				 }
				     			 }
				                 out.println("</td>");
				                 out.println("</tr>");
	    	    	 	        }
	        					flag=0;
	      	    			}
	    				/*}*/
    				}
    		   }
				if(flag==1&&!"0".equals(org_browse_format)){
					 out.println("<td colspan=\"3\">");
				   	   out.println("</td>");
				   	   out.println("</tr>");
				}
		   }
		   out.println("</table>");
		   out.println("</div>");
		   out.println("</td>");
		   out.println("</tr>");
//		   
//			if ("1".equals(this.getWriteable())&&!this.getA01state(pre,nid).equals("0")){
//				
//				out.println("<tr class=\"trDeep\">");
//				out.println("<td colspans='4' align='center'>");
//				out.println("<button name='sd' class='mybutton' onclick='updateinfo()'>修改</button>");
//				out.println("</td>");
//				out.println("</tr>");
//			} 
		   
		   out.println("<tr>");
		   out.println("<td align=\"left\" nowrap valign=\"top\">");		   
		   out.println("<div id=\"set\">");
		   String setid=printset(setlist,out);
		   out.println("</div>");
		   out.println("</td>");
		   out.println("</tr>");
		   out.println("</table>");
		   if(setid!=null&&setid.length()>0)
		   {
			   out.println("<script language=\"JavaScript\">");
			   out.println("showsetinfo('setid" + setid + "','" + setid + "list')");			  
			   out.println("</script>");
		   }
	   
		}catch(Exception e)
		{
		   System.out.println("显示主集出错!");
		   e.printStackTrace();	
        } 
	}
	
	/**
	 * 输出子集名称
	 * @param setlist
	 * @param out
	 * @throws Exception
	 */
	private String printset(List setlist,JspWriter out) throws Exception
	{
		
		out.println("<table name=\"setset\"  border=\"0\" cellspacing=\"0\"  align=\"left\" cellpadding=\"0\">");
    	String setid="";
		if(!setlist.isEmpty())
		 {
    		String gzzeSetid=getGzzeSetid();
    		for(int i=0;i<setlist.size();i++)
		 	{
		 		FieldSet fieldset=(FieldSet)setlist.get(i);
		 		if(!"01".equals(fieldset.getFieldsetid().substring(1,3)))
		 		{
		 			if(fieldSetMap.get(fieldset.getFieldsetid().toUpperCase())!=null)
			 			continue;
			 		if(gzzeSetid!=null&&gzzeSetid.equalsIgnoreCase(fieldset.getFieldsetid()))
			 			continue;				 		
			 	    out.println("<tr>");
			 		out.println("<td>");
		 			out.println("<a id='id" +fieldset.getFieldsetid()+"'></a>"+
		 					"<table name=\"set" + fieldset.getFieldsetid() + "1\"  border=\"0\" cellspacing=\"0\"  align=\"left\" cellpadding=\"0\" class=\"ListTable\">");
			 		if(i%2==0)
			 			out.println("<tr >");
			 		else
			 			out.println("<tr >");
	
			 		out.println("<td align=\"left\"  nowrap valign=\"top\">");
			 		out.println("<a href=\"#id" +fieldset.getFieldsetid()+"#"+
			 				"\" onclick=\"showsetinfo('setid" + fieldset.getFieldsetid() + "','" + fieldset.getFieldsetid() + "list')\">");		 		
			 		out.println(fieldset.getCustomdesc()/*getFieldsetdesc()*/);
			 		out.println("</a>");
			 		out.println("<div scroll=\"AUTO\" id=\"setid" + fieldset.getFieldsetid() + "\" style=\"display:none\">");
			 		out.println("</div>");		 		
			 		out.println("</td>");
			 		out.println("</tr>");
					out.println("</table>");
					if("".equals(setid))
						setid=fieldset.getFieldsetid();
		 		}

			 out.println("</td>");
		 	 out.println("</tr>");
		 	}
		 }
    	 out.println("</table>");
    	 return setid;
	}
	
	private void printoutscript()
	{
		JspWriter out=pageContext.getOut(); 
		try{
		  out.println("<script language=\"javascript\">");
		  /**从后台取得相应的数据,初始化前台*/
		  out.println("var setinfo=\"\";");
		  out.println("var rowlist;");
		  
		  out.println("function showInfo(outparamters)");
		  out.println("{");
		  out.println("setinfo=outparamters.getValue(\"setinfo\");");
		  out.println("rowlist=outparamters.getValue(\"setdata\");");
		  out.println("}");

		  
		   /**加载子集数据的脚本函数*/		  
		  out.println("function LoadSetInfo(setid)");
		  out.println("{");
		  out.println("var pars=\"setid=\"+setid + \"&pre=" + this.getPre() + "&a0100=" + this.getNid() + "&isinfoself=" + this.getIsinfoself() + "&infokind=" + this.getInfokind() + "&fenleitype="+this.fenlei_type+"\";");
		  out.println(" var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showInfo,functionId:'0402020002'});");
		  out.println("}");
		  
		 
		  out.println("function replaceAll( str, from, to ) {  var idx = str.indexOf( from );   while ( idx > -1 ) {");
	      out.println("  str = str.replace( from, to );   idx = str.indexOf( from );");
	      out.println(" } return str; }");
		  
		  out.println("function showsetinfo(setid,setdataid)");
		  out.println("{");
		  out.println("if($(setid).style.display=='none'){");
		  out.println("LoadSetInfo(setid);");
		  out.println("$(setid).innerHTML=setinfo;");
		  out.println("AjaxBind.bind($(setdataid),rowlist);");
		  out.println("$(setid).style.display='';");		 
		  out.println("}else{");
		  out.println("$(setid).style.display='none';");
		  out.println("}");
		  out.println("}");
		  out.println("");
		  out.println("</script>");
		}
		catch(Exception e)
		{
		   System.out.println("输出脚本错误!");
		   e.printStackTrace();	
        } 		
	}
    public void release(){
	 super.release();
    }
	/**
	 * @return Returns the infokind.
	 */
	public String getInfokind() {
		return infokind;
	}
	/**
	 * @param infokind The infokind to set.
	 */
	public void setInfokind(String infokind) {
		this.infokind = infokind;
	}
	/**
	 * @return Returns the nid.
	 */
	public String getNid() {
		return nid;
	}
	/**
	 * @param nid The nid to set.
	 */
	public void setNid(String nid) {
		this.nid = nid;
	}
	/**
	 * @return Returns the pre.
	 */
	public String getPre() {
		return pre;
	}
	/**
	 * @param pre The pre to set.
	 */
	public void setPre(String pre) {
		this.pre = pre;
	}
	/**
	 * @return Returns the isinfoself.
	 */
	public String getIsinfoself() {
		return isinfoself;
	}
	/**
	 * @param isinfoself The isinfoself to set.
	 */
	public void setIsinfoself(String isinfoself) {
		this.isinfoself = isinfoself;
	}
	public String getSetflag() {
		return setflag;
	}
	public void setSetflag(String setflag) {
		this.setflag = setflag;
	}
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";
		this.orgtype = orgtype;
	}
	public void getContentTypeAndField()
	{
		Connection conn=null;
		try
		{
			conn=AdminDb.getConnection();
			ParameterXMLBo parameterXMLBo = new ParameterXMLBo(conn);
			String IDs=parameterXMLBo.getBriefParaValue();
			if(IDs!=null&&IDs.trim().length()>0){
				if(IDs.indexOf(",")!=-1){
			       String str_a[] = IDs.split(",");
			       orgBriefField=str_a[0];
			       contentTypeField=str_a[1];
				}
			}
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally
		{
			if(conn!=null)
			{
				try
				{
					conn.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	public void initMap()
	{
	   Connection conn=null;
	   try
	   {

			conn=AdminDb.getConnection();
			FilterSetBo bo = new FilterSetBo(conn);
			bo.putParameters("constant","constant", "GZ_PARAM", 0, "", "/Params/ins_base_set", 1, ",", "str_value", 1,1);
			bo.putParameters("constant","constant", "GZ_PARAM", 0, "", "/Params/base_set", 1, ",", "str_value", 1,1);
			bo.putValue(this.contentTypeField, 0);
			fieldSetMap=bo.getFieldSetMap();
			fieldMap = bo.getFieldMap();
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		   if(conn!=null)
		   {
			   try
			   {
				   conn.close();
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
	}
	public String getOrg_Browse_format()
	{
		String type="0";
		Connection conn=null;
		try
		{
			conn=AdminDb.getConnection();
			Sys_Oth_Parameter sop = new Sys_Oth_Parameter(conn);
			type=sop.getValue(Sys_Oth_Parameter.ORG_BROWSE_FORMAT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn!=null)
				{
					conn.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return type;
	}
	public String getEmp_Browse_format()
	{
		String type="0";
		Connection conn=null;
		try
		{
			conn=AdminDb.getConnection();
			Sys_Oth_Parameter sop = new Sys_Oth_Parameter(conn);
			if("1".equals(this.infokind))
				type=sop.getValue(Sys_Oth_Parameter.INFOSORT_BROWSE);
			else
			   type=sop.getValue(Sys_Oth_Parameter.ORG_BROWSE_FORMAT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn!=null)
				{
					conn.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return type;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * 得到工资总额子集 
	 * @return
	 */
	private String getGzzeSetid()
	{
		Connection conn=null;
		String setid="";
		
		try
		{
			conn=AdminDb.getConnection();
			GzAmountXMLBo bo = new GzAmountXMLBo(conn,1);
			HashMap map =bo.getValuesMap();
			if(map!=null)
				setid=((String)map.get("setid"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn!=null)
				{
					conn.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return setid;
	}	
	/**
	 * 得到某人的分类权限指标
	 * @param nbase
	 * @param a0100
	 * @param conn
	 * @return
	 */
	public void  getOneFenleiYype(UserView userview,String nbase,String a0100)
	{
		if(userview.isSuper_admin())
		{
			this.fenlei_type= "";
			return;
		}
		String sql="select * from constant where upper(constant)='SYS_INFO_PRIV'  and type='1'";
    	String value="";
    	String type="";
		RowSet rs=null;
		Connection conn=null;		
		try {
			conn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(sql);			
			if(rs.next())
			{
				value=rs.getString("str_value");
			}
			if(value==null||value.length()<=0)
			{
				this.fenlei_type= "";
				return;
			}	
			String []arr=value.split(",");
			if(arr==null||arr.length!=2)
			{
				this.fenlei_type= "";
				return;
			}							
			String field=arr[0];
			rs=dao.search("select "+field+" as field from "+nbase+"A01 where a0100='"+a0100+"'");
	    	if(rs.next())
	    		type=rs.getString("field");
	    	type=type!=null?type:"";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(conn!=null)
				{
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		this.fenlei_type=type;	
		ArrayList list=userview.getSubFieldPrivList(type,"1");
		if(list==null||list.size()<=0)
		{
			list=userview.getSubFieldPrivList(type,"2");
			if(list==null||list.size()<=0)
			{
				this.fenlei_type="";
				
			}	
		}		
	}
}
