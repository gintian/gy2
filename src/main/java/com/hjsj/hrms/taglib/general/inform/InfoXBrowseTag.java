/*
 * Created on 2006-2-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.taglib.general.inform;

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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InfoXBrowseTag extends TagSupport {	
	private String nid;      //id
	private String pre;      //人员库
	private String infokind; //1人员2单位3职位
	private String isinfoself;//是否是信息自助0员工自助1其他的自助2招聘自助
	private String states;//判断是否需要状态 可选项
	private String isApprove;//判断是否需要审批 可选选
	public int doEndTag() throws JspException{
		printoutscript();
		showInfoData();
		return SKIP_BODY; 
	}
	private void showInfoData()
	{
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		List fieldlist=null;
		List setlist=null;
		//人员库
		if("1".equals(infokind))
		{
			if("0".equals(isinfoself))               //自助
			{
				fieldlist=userview.getPrivFieldList("A01",0);   //自助主集的指标
				setlist=userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);   //自助所有权限子集
			}
		    else if("1".equals(isinfoself))
		    {	
		    	fieldlist=userview.getPrivFieldList("A01");  //人员主集的指标
		    	setlist=userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);    //人员所有权限子集
		    }
		    else if("2".equals(isinfoself))
		    {
		    	fieldlist=getZpMainsetFieldList();
		    	setlist=getZpSetsList();
		    }
			showemployeedata(fieldlist,setlist,nid,pre);
		}else if("2".equals(infokind))          //单位库
		{
			if("0".equals(isinfoself))               //自助
			{
				fieldlist=userview.getPrivFieldList("B01",0);   //自助主集的指标
				setlist=userview.getPrivFieldSetList(Constant.UNIT_FIELD_SET,0);   //自助所有权限子集
			}
			 else if("1".equals(isinfoself))
		    {	
		    	fieldlist=userview.getPrivFieldList("B01");  //人员主集的指标
		    	setlist=userview.getPrivFieldSetList(Constant.UNIT_FIELD_SET);    //人员所有权限子集
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
			if("0".equals(isinfoself))               //自助
			{
				fieldlist=userview.getPrivFieldList("K01",0);   //自助主集的指标
				setlist=userview.getPrivFieldSetList(Constant.POS_FIELD_SET,0);   //自助所有权限子集
			}
			else if("1".equals(isinfoself))
		    {	
		    	fieldlist=userview.getPrivFieldList("K01");  //人员主集的指标
		    	setlist=userview.getPrivFieldSetList(Constant.POS_FIELD_SET);    //人员所有权限子集
		    }
			for(int i=0;i<fieldlist.size();i++)
				fieldlists.add(fieldlist.get(i));
			showposdata(fieldlists,setlist,nid,pre);
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
			strsql.append("state,");
			strsql.append("a0100 from ");
			strsql.append(pre);
			strsql.append("A01 where a0100='");
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
			//System.out.println(strsql.toString());
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
		 try
		 {
		     out.println("<link href=\"/css/css1.css\" rel=\"stylesheet\" type=\"text/css\">");
		     out.println("<table border=\"0\" cellspacing=\"0\"  align=\"left\" cellpadding=\"0\">");
		     out.println("<tr>");
		     out.println("<td align=\"left\" nowrap valign=\"top\">");
		     out.println("<div id=\"maininfo\" style=\"background-color:transparent;\" >");
		     out.println("<table  border=\"0\" width=\"800\"  cellspacing=\"0\"  align=\"left\" cellpadding=\"0\" class=\"ListTable\">");
			 if(!fieldlist.isEmpty())
			 {
				 if("true".equals(this.getStates())){
					 FieldItem statefi=new FieldItem();
					 statefi.setItemdesc("状态");
					 statefi.setItemtype("A");
				 	statefi.setItemid("state");
				 	statefi.setCodesetid("0");
				 	fieldlist.add(statefi);
				 }
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fieldItem=(FieldItem)fieldlist.get(i);
					if("N".equalsIgnoreCase(fieldItem.getItemtype()))
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
					}else if("D".equalsIgnoreCase(fieldItem.getItemtype()))
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
					}else if("A".equalsIgnoreCase(fieldItem.getItemtype()))
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
					   out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
					   out.println("</td>");
					   out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\">");
					   if(!"0".equals(fieldItem.getCodesetid()))
					   {
						   if("b0110".equalsIgnoreCase(fieldItem.getItemid()) || "e01a1".equalsIgnoreCase(fieldItem.getItemid()))
						   {
							   	  List codefindset = ExecuteSQL.executeMyQuery("select  codeitemdesc from organization where codeitemid='" + nid + "'");
							  	  if(!codefindset.isEmpty())
							   	  {
							   	      LazyDynaBean recset=(LazyDynaBean)codefindset.get(0);
							   	      out.println(recset.get("codeitemdesc").toString());
							   	  }
							}else if("e0122".equalsIgnoreCase(fieldItem.getItemid()))
							{
							   		Connection conn=null;
							   		try{
							   			conn=AdminDb.getConnection();
							   		    StationPosView posview=getStationPos(nid,conn);
							   		   if(posview!=null)
							    	   {
								    	  if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0)
								    		  out.println(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"");
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
					}else if("M".equalsIgnoreCase(fieldItem.getItemtype()))
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
				          out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\"  colspan=\"3\">");
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
		   out.println("</table>");
		   out.println("</div>");
		   out.println("</td>");
		   out.println("</tr>");
		   out.println("<tr>");
		   out.println("<td align=\"left\" nowrap valign=\"top\">");		   
		   out.println("<div id=\"set\">");
		   printset(setlist,out);
		   out.println("</div>");
		   out.println("</td>");
		   out.println("</tr>");
		   
		   out.println("<tr>");
		   out.println("<td colspans='4'>");
		   out.println("<button name='sd' class='mybutton'>状态</button>");
		   out.println("</td>");
		   out.println("</tr>");
		   out.println("</table>");
		  
	   
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
	    try
	    {
		     out.println("<link href=\"/css/css1.css\" rel=\"stylesheet\" type=\"text/css\">");
		     out.println("<table border=\"0\" cellspacing=\"1\"  align=\"left\" cellpadding=\"1\">");
		     out.println("<tr>");
		     out.println("<td align=\"left\" nowrap valign=\"top\">");
		     out.println("<div id=\"maininfo\" style=\"background-color:transparent;\" >");
		     out.println("<table  border=\"0\" width=\"800\"  cellspacing=\"0\"  align=\"left\" cellpadding=\"0\" class=\"ListTable\">");
			 if(!fieldlist.isEmpty())
			 {
				 FieldItem statefi=new FieldItem();
				 statefi.setItemdesc("状态");
				 statefi.setItemid("state");
				 statefi.setItemtype("A");
				 statefi.setFieldsetid("A01");
				 statefi.setCodesetid("0");
				 fieldlist.add(statefi);
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fieldItem=(FieldItem)fieldlist.get(i);
					if("N".equalsIgnoreCase(fieldItem.getItemtype()))
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
					   out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
					   out.println("</td>");
					   out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\">");
					   out.println(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()).toString(),fieldItem.getDecimalwidth()) + "&nbsp;");
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
					}else if("D".equalsIgnoreCase(fieldItem.getItemtype()))
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
					   out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
					   out.println("</td>");
					   out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\">");
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
					}else if("A".equalsIgnoreCase(fieldItem.getItemtype()))
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
					   out.println("<td align=\"right\" width=\"200\" nowrap valign=\"top\">");
					   out.println("&nbsp;&nbsp;" + fieldItem.getItemdesc() + "&nbsp;");
					   out.println("</td>");
					   out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\">");
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
					   	    if("UN".equalsIgnoreCase(fieldItem.getCodesetid()))
					  	  	   out.println(AdminCode.getCodeName(fieldItem.getCodesetid()/*codeset*/,codevalue) + "&nbsp;");
						    else 
						    {
						        String codedesc=AdminCode.getCodeName(fieldItem.getCodesetid(),codevalue);
						    	out.println( codedesc+ "&nbsp;");
						    }
					   	  else
						    out.println("&nbsp;");
					   }
					   else{
						   if(!"state".equalsIgnoreCase(fieldItem.getItemid())){
							   out.println(rec.get(fieldItem.getItemid()) + "&nbsp;");
						   }else{
							   String sst=(String) rec.get(fieldItem.getItemid());
							   out.println(ResourceFactory.getProperty("info.appleal.state"+sst) + "&nbsp;");
						   }
					      
					   }
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
					}else if("M".equalsIgnoreCase(fieldItem.getItemtype()))
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
				          out.println("<td align=\"left\" width=\"200\" nowrap valign=\"top\"  colspan=\"3\">");
				          String fieldvalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
						  out.println(fieldvalue + "&nbsp;");
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
				             String fieldvalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
				             out.println("&nbsp;" + fieldvalue + "&nbsp;");
				             out.println("</td>");
				             out.println("</tr>");
				        }
						flag=0;
					}
				}
		   } 

			 
		   out.println("</table>");
		   out.println("</div>");
		   out.println("</td>");
		   out.println("</tr>");
			if ("true".equals(this.getIsApprove())){
				out.println("<tr class=\"trDeep\">");
				out.println("<td colspans='4' align='center'>");
				out.println("<button name='sd' class='mybutton'>批准</button>");
				out.println("</td>");
				out.println("</tr>");
			} 
		   out.println("<tr>");
		   out.println("<td align=\"left\" nowrap valign=\"top\">");		   
		   out.println("<div id=\"set\">");
		   printset(setlist,out);
		   out.println("</div>");
		   out.println("</td>");
		   out.println("</tr>");
		   out.println("</table>");
		  
	   
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
	private void printset(List setlist,JspWriter out) throws Exception
	{
		out.println("<table name=\"setset\"  border=\"0\" cellspacing=\"0\"  align=\"left\" cellpadding=\"0\">");
    	 if(!setlist.isEmpty())
		 {
		 	for(int i=0;i<setlist.size();i++)
		 	{
		 	    out.println("<tr>");
		 		out.println("<td>");
		 		
		 		FieldSet fieldset=(FieldSet)setlist.get(i);
		 		if(!"01".equals(fieldset.getFieldsetid().substring(1,3))){
		 			out.println("<a id='id" +fieldset.getFieldsetid()+"'></a>"+
		 					"<table name=\"set" + fieldset.getFieldsetid() + "1\"  border=\"0\" cellspacing=\"0\"  align=\"left\" cellpadding=\"0\" class=\"ListTable\">");
			 		if(i%2==0)
			 			out.println("<tr >");
			 		else
			 			out.println("<tr >");
	
			 		out.println("<td align=\"left\"  nowrap valign=\"top\">");
			 		out.println("<a href=\"#id" +fieldset.getFieldsetid()+
			 				"\" onclick=\"showsetinfo('setid" + fieldset.getFieldsetid() + "','" + fieldset.getFieldsetid() + "list')\">");		 		
			 		out.println(fieldset.getFieldsetdesc());
			 		out.println("</a>");
			 		out.println("<div scroll=\"AUTO\" id=\"setid" + fieldset.getFieldsetid() + "\" style=\"display:none\">");
			 		out.println("</div>");		 		
			 		out.println("</td>");
			 		out.println("</tr>");
			 		if("true".equals(this.getIsApprove())){
			 			out.println("<tr class=\"trDeep\">");
			 			out.println("<td>");
			 			out.println("<div scroll=\"AUTO\" id=\"ssetid" + fieldset.getFieldsetid() + "\" style=\"display:none\">");
			 				 		
			 			out.println("<button name='ok"+fieldset.getFieldsetid()+"' class='mybutton' onclick='b_oks(this.name);'>批准</button>&nbsp;");
			 			out.println("<button name='bc"+fieldset.getFieldsetid()+"' class='mybutton' onclick='b_rej(this.name);'>驳回</button>");
			 			out.println("</div>");	
			 			out.println("</td>");
			 			out.println("</tr>");
			 		}
					out.println("</table>");
		 		}

			 out.println("</td>");
		 	 out.println("</tr>");
		 	}
		 }
    	 out.println("</table>");
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
		  out.println("var pars=\"setid=\"+setid + \"&pre=" + this.getPre() + "&a0100=" + this.getNid() + "&isinfoself=" + this.getIsinfoself() + "&infokind=" + this.getInfokind() + "&sts=1\";");
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
		  if("true".equals(this.getIsApprove()))
			  out.println("showbutton(setid);");
		  out.println("}else{");
		  out.println("$(setid).style.display='none';");
		  if("true".equals(this.getIsApprove()))
			  out.println("showbutton(setid);");
		  out.println("}");
		  out.println("}");
		  
		  out.println("function showbutton(setids){" );
		  out.println("var setid='s'+setids");
		  out.println("if($(setid).style.display=='none'){");
		  out.println("$(setid).style.display='';");		 
		  out.println("}else{");
		  out.println("$(setid).style.display='none';");
		  out.println("}");
		  out.println("}");

		  
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
	public String getStates() {
		return states;
	}
	public void setStates(String states) {
		this.states = states;
	}
	public String getIsApprove() {
		return isApprove;
	}
	public void setIsApprove(String isApprove) {
		this.isApprove = isApprove;
	}	
}
