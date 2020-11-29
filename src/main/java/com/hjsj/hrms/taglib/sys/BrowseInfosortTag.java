package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BrowseInfosortTag extends BodyTagSupport {
  private List subsort_list=new ArrayList();
  private HashMap infoMap=new HashMap();
  private String userbase="";
  private String a0100="";
  private String userpriv;
  private int uplevel;
  private String prv_flag;
  private String returnvalue;
    public String getUserpriv() {
	return userpriv;
}
public void setUserpriv(String userpriv) {
	this.userpriv = userpriv;
}
	public String getA0100() {
	return a0100;
}
public void setA0100(String a0100) {
	this.a0100 = a0100;
}
public String getUserbase() {
	return userbase;
}
public void setUserbase(String userbase) {
	this.userbase = userbase;
}
	public int doEndTag() throws JspException 
    {
	    if(subsort_list==null||subsort_list.size()<=0)
	    	return SKIP_BODY;
	    if(infoMap==null)
	    	return SKIP_BODY;
	    if(userpriv==null||!"selfinfo".equalsIgnoreCase(userpriv))
    	{
    		userpriv="noinfo";	    	
    	}
	    String sortname="";
	    JspWriter out=pageContext.getOut();	    
	    try
	    {
	    	out.println("<script language='javascript'>");
	    	out.println("function showinfo(divId)");
		    out.println("{");
		    out.println(" var fObj=document.getElementById(divId);  ");
		    out.println(" if(fObj.style.display=='none')");
		    out.println(" {");
		    out.println("  fObj.style.display='block';");
		    out.println(" }else");
		    out.println(" {");
		    out.println("  fObj.style.display='none';");
		    out.println(" }");
		    out.println("}");
		    out.println("</script>");
		    //下面要判断权限，获取userview
		    UserView userView = (UserView)this.pageContext.getSession().getAttribute(WebConstant.userView);
	    	for(int s=0;s<subsort_list.size();s++)
		    {
		    	sortname=(String)subsort_list.get(s);
		    	if(sortname!=null&& "未分类指标".equalsIgnoreCase(sortname))
		    	{
		    		ArrayList infolist=(ArrayList)infoMap.get(sortname);
			    	out.println("<tr class='trShallow1'><td colspan='4'>");
			    	out.println("<table  border='0' cellspacing='1' cellpadding='1' width='100%' class='ListTable3'>");
			    	if(infolist!=null&&infolist.size()>0)
			    	{
			    		int i=0;
			    		int flag=0;
			    		for(int n=0;n<infolist.size();n++)
			    		{
			    			FieldItemView fieldItemView=(FieldItemView)infolist.get(n);			    			
			    			 if("b0110".equalsIgnoreCase(fieldItemView.getItemid()))
							 {
							    	String UNIT_LEN=getValues("UNIT_LEN");
							    	if(UNIT_LEN!=null&& "0".equals(UNIT_LEN))
							    		continue;
							 }else if("e01a1".equalsIgnoreCase(fieldItemView.getItemid()))
							 {
							    	String POS_LEN_str =getValues("POS_LEN");
							    	if(POS_LEN_str!=null&& "0".equals(POS_LEN_str))
							    		continue;
							 }
			    			/* if(fieldItemView.getState().equals("1")&&fieldItemView.getDisplaywidth()>0)
			    			{*/
			    				if("D".equalsIgnoreCase(fieldItemView.getItemtype()))
			    				{
			    					if(flag==0)
			    					{
			    			           if(i%2==0){
			    			              out.println(" <tr class='trShallow1'>");           
			    			            }else{
			    			            	out.println("<tr class='trDeep1'>"); 
			    			            }
			    			             i++;
			    			             flag=1;          
			    			         }else{
			    			               flag=0;           
			    			         }
			    					out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
			    					out.println(""+fieldItemView.getItemdesc()+"");              
			    					out.println("</td>");
			    					out.println("<td align='left'  nowrap valign='middle' class='AddTableRow'>");     
			    					out.println("<input type='text' name='fieldvalue' value='"+fieldItemView.getFieldvalue()+"' readonly='readonly' class='textColorWrite'>");
			    					out.println("</td>"); 
			    			        if(flag==0){          
			    			        	out.println("</tr>");
			    			        }else{
			    			            if(fieldItemView.getRowflag().equals(""+n))
			    			        	{
			    			        		out.println("<td colspan='2' class='AddTableRow'>");
				    			        	out.println("  </td>");
				    			        	out.println("  </tr>");
			    			        	}
			    			        }
			    				}else if("A".equalsIgnoreCase(fieldItemView.getItemtype()))
			    				{
			    					 if(flag==0)
			    					 {
			    			             if(i%2==0){
			    			            	 out.println("<tr class='trShallow1'>");            
			    			             }else
			    			             {
			    			            	 out.println("<tr class='trDeep1'>");  
			    			             }
			    			             i++;
			    			             flag=1;          
			    			          }else{
			    			               flag=0;           
			    			          }
			    					 if(!"e0122".equalsIgnoreCase(fieldItemView.getItemid())){
				    					 out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
				    					 out.println(""+fieldItemView.getItemdesc()+"");              
					    			     out.println("</td>");
					    				 out.println("<td align='left'  nowrap valign='middle' class='AddTableRow'>");  
					    				 out.println("<input type='text' name='fieldvalue' value='"+fieldItemView.getFieldvalue()+"' readonly='readonly' class='textColorWrite'>");
				    				 }else{
					    				out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
					    				out.println(""+fieldItemView.getItemdesc()+"");              
						    			out.println("</td>");
						    			out.println("<td align='left'  nowrap valign='middle' class='AddTableRow'>");  
						    			out.println("<input type='text' name='fieldvalue' value='"+/*fieldItemView.getFieldvalue()*/codeToName(fieldItemView.getViewvalue())+"' readonly='readonly' title='"+codeToName(fieldItemView.getViewvalue())+"' class='textColorWrite'>");
				    				 }
			    					 if("a0101".equalsIgnoreCase(fieldItemView.getItemid()))
				    				 {
				    					 //out.println("<a href='/general/inform/synthesisbrowse/mycard.do?b_mysearch=link&userbase="+userbase+"&a0100="+a0100+"&multi_cards=-1&inforkind=1&npage=1&userpriv=${browseForm.userpriv}&flick=1' target='_blank'>");
			    						 String a0100_encrypt=a0100;
				    					 if(a0100.startsWith("~")) {
				    						 a0100_encrypt=PubFunc.convert64BaseToString(a0100.substring(1));
				    					 }
				    					 out.println(" <a href='###' onclick=\"openwin('/general/inform/synthesisbrowse/mycard.do?b_mysearch=link`userbase="+userbase+"`a0100="+a0100_encrypt+"`multi_cards=-1`inforkind=1`npage=1`userpriv="+userpriv+"`flick=1`flag="+prv_flag+"');\">");
				    					 out.println(" <img src='/images/view.gif' border=0 title='员工登记表' align='middle'>");
				    		             out.println("</a>");  
				    				 }else if("e01a1".equalsIgnoreCase(fieldItemView.getItemid()))
				    				 {
				    					 if(fieldItemView.getFieldvalue()!=null&&fieldItemView.getFieldvalue().length()>0)
				    					 {
				    						 out.println("<a href='###'  onclick=\"openwin('/workbench/browse/showposinfo.do?b_browse=link`a0100="+PubFunc.encrypt(fieldItemView.getViewvalue())+"`userbase="+userbase+"`npage=1`infokind=4`flag="+prv_flag+"');\">");
				    						 out.println(" d<img src='/images/view.gif' border=0 title='岗位说明书'> align='middle'");
					    		             out.println("</a>"); 
				    					 }
				    					 out.println(positionManual(this.userbase,this.a0100));
				    				 }else if(/*"relation".equalsIgnoreCase(this.returnvalue)&&*/"e0122".equalsIgnoreCase(fieldItemView.getItemid()))
				    				 {
				    					 if(fieldItemView.getFieldvalue()!=null&&fieldItemView.getFieldvalue().length()>0)
				    					 {
				    						 out.println("<hrms:priv func_id='23011'>");
				    						 out.println("<a href='###'  onclick=\"openwin('/general/inform/org/searchorgbrowse.do?b_search=link`code="+fieldItemView.getViewvalue()+"`kind=1`orgtype=org`returnvalue="+this.returnvalue+"');\">");
				    						 out.println(" d<img src='/images/view.gif' border=0> align='middle'");
					    		             out.println("</a>"); 
					    		             out.println("</hrms:priv>"); 
				    					 }
				    					 out.println(positionManual(this.userbase,this.a0100));
				    				 }
				    				 out.println("</td>");
				    				 if(flag==0){          
				    			        	out.println("</tr>");
				    			     }else{
				    			            if(fieldItemView.getRowflag().equals(""+n))
				    			        	{
				    			        		out.println("<td colspan='2' class='AddTableRow'>");
					    			        	out.println("  </td>");
					    			        	out.println("  </tr>");
				    			        	}
				    			     }
			    				}else if("N".equalsIgnoreCase(fieldItemView.getItemtype()))
			    				{
			    					if(flag==0)
			    					 {
			    			             if(i%2==0){
			    			            	 out.println("<tr class='trShallow1'>");            
			    			             }else
			    			             {
			    			            	 out.println("<tr class='trDeep1'>");  
			    			             }
			    			             i++;
			    			             flag=1;          
			    			          }else{
			    			               flag=0;           
			    			          }
			    					out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
			    					out.println(""+fieldItemView.getItemdesc()+"");              
				    			    out.println("</td>");
				    			    out.println("<td align='left'  nowrap valign='middle' class='AddTableRow'>");     
				    			    out.println("<input type='text' name='fieldvalue' value='"+fieldItemView.getFieldvalue()+"' readonly='readonly' class='textColorWrite'>");
				    			    out.println("</td>");
				    			    if(flag==0){          
			    			        	out.println("</tr>");
			    			        }else{
			    			            if(fieldItemView.getRowflag().equals(""+n))
			    			        	{
			    			        		out.println("<td colspan='2' class='AddTableRow'>");
				    			        	out.println("  </td>");
				    			        	out.println("  </tr>");
			    			        	}
			    			       }		    		         
			    				}else if("M".equalsIgnoreCase(fieldItemView.getItemtype()))
			    				{
			    					if(flag==0)
			    					 {
			    			             if(i%2==0){
			    			            	 out.println("<tr class='trShallow1'>");            
			    			             }else
			    			             {
			    			            	 out.println("<tr class='trDeep1'>");  
			    			             }
			    			             i++;
			    			             flag=1;      
			    			             out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
					    				 out.println(""+fieldItemView.getItemdesc()+"");              
						    			 out.println("</td>");
						    			 out.println("<td align='left' valign='middle' colspan='3' class='AddTableRow'>");
						    			 //员工管理模块，备注型指标应该显示为文本域 jingq upd 2014.10.21
						    			 out.println("<textarea readonly='true' rows='10' cols='66' style='width:550px;height:100px;' styleClass='textColorWrite'>"+fieldItemView.getFieldvalue()+"</textarea>");
						    			 out.println("</td>");
			    			          }else{
			    			               flag=0;   
			    			               out.println("<td colspan='2' class='AddTableRow'>");
			    			               out.println("</td>");
			    			               out.println("</tr>");
			    			               if(flag==0){
			    			                   if(i%2==0){
			    			                	   out.println("<tr class='trShallow1'>");            
			    			                  }
			    			                  else
			    			                  {
			    			                	  out.println("<tr class='trDeep1'>");  
			    			                  }
			    			                  i++;
			    			                  flag=1;          
			    			               }else{
			    			                    flag=0;           
			    			               }     
			    			               out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
						    			   out.println(""+fieldItemView.getItemdesc()+"");              
							    		   out.println("</td>");
							    		   out.println("<td align='left' valign='middle' colspan='3' class='AddTableRow'>");
							    		   out.println("<textarea readonly='true' rows='10' cols='66' style='width:550px;height:100px;' styleClass='textColorWrite'>"+fieldItemView.getFieldvalue()+"</textarea>");
							    		   out.println("</td>");
			    			          }
			    					  flag=0;
			    					  out.println("</tr>");
			    				}
			    			//}
			    		}
			    		if(flag==1){ 
			    			out.println("<td></td><td></td>");
			    		}
			    	}
			    	out.println("</table>");
			    	out.println("</td></tr>");
		    	}else
		    	{
		    		ArrayList infolist=(ArrayList)infoMap.get(sortname);		    		
		    		if(infolist==null||infolist.size()<=0)
		    			continue;
		    		out.println("<tr class='trDeep1'><td colspan='4'>");
			    	out.println("<img src='/images/new_target_wiz.gif'>&nbsp;<a href='javascript:void(0)' onclick=\"showinfo('show"+s+"')\">"+sortname+"");	
			    	out.println("</td></tr>");
			    	
			    	out.println("<tr class='trShallow1'><td colspan='4'>");
			    	if(s==0)
			    	  out.println("<div id=\"show"+s+"\" style='display:block;'>");
			    	else
			    	  out.println("<div id=\"show"+s+"\" style='display:none;'>");
			    	out.println("<table  border='0' cellspacing='1' cellpadding='1' width='100%' class='ListTable3'>");
			    	if(infolist!=null&&infolist.size()>0)
			    	{
			    		int i=0;
			    		int flag=0;
			    		int sd=0;  //用来判断右边是否有值
			    		for(int n=0;n<infolist.size();n++)
			    		{
			    			FieldItemView fieldItemView=(FieldItemView)infolist.get(n);
			    			if("b0110".equalsIgnoreCase(fieldItemView.getItemid()))
							 {
							    	String UNIT_LEN=getValues("UNIT_LEN");
							    	if(UNIT_LEN!=null&& "0".equals(UNIT_LEN))
							    		continue;
							 }else if("e01a1".equalsIgnoreCase(fieldItemView.getItemid()))
							 {
							    	String POS_LEN_str =getValues("POS_LEN");
							    	if(POS_LEN_str!=null&& "0".equals(POS_LEN_str))
							    		continue;
							 }	
			    			/*if(fieldItemView.getState().equals("1")&&fieldItemView.getDisplaywidth()>0)
			    			{*/
			    				if("D".equalsIgnoreCase(fieldItemView.getItemtype()))
			    				{
			    					if(flag==0)
			    					{
			    			           if(i%2==0){
			    			              out.println(" <tr class='trShallow1'>");           
			    			            }else{
			    			            	out.println("<tr class='trDeep1'>"); 
			    			            }
			    			             i++;
			    			             flag=1;          
			    			         }else{
			    			               flag=0;           
			    			         }
			    					out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
			    					out.println(""+fieldItemView.getItemdesc()+"");              
			    					out.println("</td>");
			    					out.println("<td align='left'  nowrap valign='middle' class='AddTableRow'>");     
			    					out.println("<input type='text' name='fieldvalue' value='"+fieldItemView.getFieldvalue()+"' readonly='readonly' class='textColorWrite'>");
			    					out.println("</td>"); 
			    			        if(flag==0){          
			    			        	out.println("</tr>");
			    			        }else{
			    			            if(fieldItemView.getRowflag().equals(""+n))
			    			        	{
			    			        		out.println("<td colspan='2' class='AddTableRow'>");
				    			        	out.println("  </td>");
				    			        	out.println("  </tr>");
			    			        	}
			    			        }
			    				}else if("A".equalsIgnoreCase(fieldItemView.getItemtype()))
			    				{
			    					 if(flag==0)
			    					 {
			    			             if(i%2==0){
			    			            	 out.println("<tr class='trShallow1'>");            
			    			             }else
			    			             {
			    			            	 out.println("<tr class='trDeep1'>");  
			    			             }
			    			             i++;
			    			             flag=1;          
			    			          }else{
			    			               flag=0;           
			    			          }
			    					 if(!"e0122".equalsIgnoreCase(fieldItemView.getItemid())){
				    					 out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
				    					 out.println(""+fieldItemView.getItemdesc()+"");              
					    			     out.println("</td>");
					    				 out.println("<td align='left' nowrap valign='middle' class='AddTableRow'>");  
					    				 out.println("<input type='text' name='fieldvalue' value='"+fieldItemView.getFieldvalue()+"' readonly='readonly' class='textColorWrite'>");
				    				 }else{
				    					 out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
				    					 out.println(""+fieldItemView.getItemdesc()+"");              
					    			     out.println("</td>");
					    				 out.println("<td align='left'  nowrap valign='middle' class='AddTableRow'>");  
					    				 out.println("<input type='text' name='fieldvalue' value='"+/*fieldItemView.getFieldvalue()*/codeToName(fieldItemView.getViewvalue())+"' title='"+codeToName(fieldItemView.getViewvalue())+"' readonly='readonly' class='textColorWrite'>");
				    				 }
				    				 cardIf:if("a0101".equalsIgnoreCase(fieldItemView.getItemid()))
				    				 {
				    					 //out.println("<a href='/general/inform/synthesisbrowse/mycard.do?b_mysearch=link&userbase="+userbase+"&a0100="+a0100+"&multi_cards=-1&inforkind=1&npage=1&userpriv=${browseForm.userpriv}&flick=1' target='_blank'>");
				    					 /*配置了人员登记表并且有此登记表权限才显示查看按钮 guodd 2018-01-08*/
				    					 Connection conn = null;
				    					 String empcard = "";
				    					 try {
				    						 conn = AdminDb.getConnection();
				    						 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
					    					 empcard=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
				    					 }catch(Exception e) {
				    					 }finally {
				    						 PubFunc.closeResource(conn);
				    					 }
				    					 if(empcard==null || empcard.length()<1 || !userView.isHaveResource(IResourceConstant.CARD, empcard))
				    						 break cardIf;
				    					 String a0100_encrypt=a0100;
				    					 if(a0100.startsWith("~")) {
				    						 a0100_encrypt=PubFunc.convert64BaseToString(a0100.substring(1));
				    						 a0100_encrypt=PubFunc.encrypt(a0100_encrypt);
				    					 }
				    					 out.println(" <a href='###' onclick=\"openwin('/general/inform/synthesisbrowse/mycard.do?b_mysearch=link`userbase="+userbase+"`a0100="+a0100_encrypt+"`multi_cards=-1`inforkind=1`npage=1`userpriv="+userpriv+"`flick=1`flag="+prv_flag+"');\">");
				    					 out.println(" <img src='/images/view.gif' border=0 title='员工登记表' align='middle'>");
				    		             out.println("</a>");  
				    				 }else if("e01a1".equalsIgnoreCase(fieldItemView.getItemid()))
				    				 {
				    					 /*配置了岗位说明书并且有此登记表权限才显示查看按钮 guodd 2018-01-08*/
				    					 RecordVo vo = ConstantParamter.getRealConstantVo("ZP_POS_TEMPLATE");
				    					 String poscard = vo.getString("str_value");
		    							 if(poscard==null || poscard.length()<1 || !userView.isHaveResource(IResourceConstant.CARD, poscard))
				    						 break cardIf;
				    					 if(fieldItemView.getFieldvalue()!=null&&fieldItemView.getFieldvalue().length()>0)
				    					 {
				    						 out.println("<a href='###'  onclick=\"openwin('/workbench/browse/showposinfo.do?b_browse=link`a0100="+PubFunc.encrypt(fieldItemView.getViewvalue())+"`userbase="+userbase+"`npage=1`infokind=4`flag="+prv_flag+"');\">");
				    						 //out.println("<a href='/workbench/browse/showposinfo.do?b_browse=link&a0100="+fieldItemView.getViewvalue()+"&userbase="+userbase+"&npage=1' target='_blank'>");
				    						 out.println(" <img src='/images/view.gif' border=0 title='岗位说明书' align='middle'>");
					    		             out.println("</a>"); 
				    					 }
				    					 out.println(positionManual(this.userbase,this.a0100));
				    				 }else if(/*"relation".equalsIgnoreCase(this.returnvalue)&&*/"e0122".equalsIgnoreCase(fieldItemView.getItemid()))
				    				 {
				    					 if(fieldItemView.getFieldvalue()!=null&&fieldItemView.getFieldvalue().length()>0)
				    					 {
				    						 //out.println("<hrms:priv func_id='23011'>");
				    						 if(userView.hasTheFunction("23011")){
				    						 out.println("<a href='###'  onclick=\"openwin('/general/inform/org/searchorgbrowse.do?b_search=link`code="+fieldItemView.getViewvalue()+"`kind=1`orgtype=org`returnvalue="+this.returnvalue+"');\">");
				    						 out.println(" <img src='/images/view.gif' border=0 title='机构信息' align='middle'>");
					    		             out.println("</a>"); 
				    					 	}
					    		             //out.println("</hrms:priv>"); 
				    					 }
				    					 out.println(positionManual(this.userbase,this.a0100));
				    				 }
				    				 out.println("</td>");
				    				 if(flag==0){          
				    			        	out.println("</tr>");
				    			     }else{
				    			            if(fieldItemView.getRowflag().equals(""+n))
				    			        	{
				    			        		out.println("<td colspan='2' class='AddTableRow'>");
					    			        	out.println("  </td>");
					    			        	out.println("  </tr>");
				    			        	}
				    			     }
			    				}else if("N".equalsIgnoreCase(fieldItemView.getItemtype()))
			    				{
			    					if(flag==0)
			    					 {
			    			             if(i%2==0){
			    			            	 out.println("<tr class='trShallow1'>");            
			    			             }else
			    			             {
			    			            	 out.println("<tr class='trDeep1'>");  
			    			             }
			    			             i++;
			    			             flag=1;          
			    			          }else{
			    			               flag=0;           
			    			          }
			    					out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
			    					out.println(""+fieldItemView.getItemdesc()+"");              
				    			    out.println("</td>");
				    			    out.println("<td align='left'  nowrap valign='middle' class='AddTableRow'>");     
				    			    out.println("<input type='text' name='fieldvalue' value='"+fieldItemView.getFieldvalue()+"' readonly='readonly' class='textColorWrite'>");
				    			    out.println("</td>");
				    			    if(flag==0){          
			    			        	out.println("</tr>");
			    			        }else{
			    			            if(fieldItemView.getRowflag().equals(""+n))
			    			        	{
			    			        		out.println("<td colspan='2' class='AddTableRow'>");
				    			        	out.println("  </td>");
				    			        	out.println("  </tr>");
			    			        	}
			    			       }		    		         
			    				}else if("M".equalsIgnoreCase(fieldItemView.getItemtype()))
			    				{
			    					if(flag==0)
			    					 {
			    			             if(i%2==0){
			    			            	 out.println("<tr class='trShallow1'>");            
			    			             }else
			    			             {
			    			            	 out.println("<tr class='trDeep1'>");  
			    			             }
			    			             i++;
			    			             flag=1;      
			    			             out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
					    				 out.println(""+fieldItemView.getItemdesc()+"");              
						    			 out.println("</td>");
						    			 out.println("<td align='left'   valign='middle'  colspan='3' class='AddTableRow'>");
						    			 out.println("<textarea readonly='true' rows='10' cols='66' style='width:550px;height:100px;' styleClass='textColorWrite'>"+fieldItemView.getFieldvalue()+"</textarea>");
						    			 out.println("</td>");
			    			          }else{
			    			               flag=0;   
			    			               out.println("<td colspan='2' class='AddTableRow'>");
			    			               out.println("</td>");
			    			               out.println("</tr>");
			    			               if(flag==0){
			    			                   if(i%2==0){
			    			                	   out.println("<tr class='trShallow1'>");            
			    			                  }
			    			                  else
			    			                  {
			    			                	  out.println("<tr class='trDeep1'>");  
			    			                  }
			    			                  i++;
			    			                  flag=1;          
			    			               }else{
			    			                    flag=0;           
			    			               }     
			    			               out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");        
						    			   out.println(""+fieldItemView.getItemdesc()+"");              
							    		   out.println("</td>");
							    		   out.println("<td align='left'   valign='middle' colspan='3' class='AddTableRow'>");
							    		   out.println("<textarea readonly='true' rows='10' cols='66' style='width:550px;height:100px;' styleClass='textColorWrite'>"+fieldItemView.getFieldvalue()+"</textarea>");
							    		   out.println("</td>");
			    			          }
			    					  flag=0;
			    					  out.println("</tr>");
			    				//}
			    				sd++;
			    			}
			    		}
			    		//增加一个空表格 增加了一个sd
			    		if(i%2!=0){
			    			if(sd%2!=0){
			    				out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");                     
				    		    out.println("</td>");
				    		    out.println("<td align='left' valign='middle' colspan='3' class='AddTableRow'>");
				    		    out.println("</td>");
			    			}
			    		}else{
			    			if(sd%2!=0){
				    			 out.println("<td align='right' width='20%' nowrap valign='middle' class='AddTableRow'>");                     
					    		   out.println("</td>");
					    		   out.println("<td align='left' valign='middle' colspan='3' class='AddTableRow'>");
					    		   out.println("</td>");
				    		}
			    		}
			    		if(flag==1){ 
			    			out.println("<td></td><td></td>");
			    		}
			    	}
			    	out.println("</div>");
			    	out.println("</table>");
			    	out.println("</td></tr>");
		    	}
		    	
		    	
		    }
	    }catch(Exception e)
	    {
	      e.printStackTrace();	
	    }
	    
	    return SKIP_BODY;	
    }
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	public HashMap getInfoMap() {
		return infoMap;
	}
	public void setInfoMap(HashMap infoMap) {
		this.infoMap = infoMap;
	}
	public List getSubsort_list() {
		return subsort_list;
	}
	public void setSubsort_list(List subsort_list) {
		this.subsort_list = subsort_list;
	}
	public String getValues(String contant){
		Connection conn=null;		
		String values = "1";
		try{
			conn=AdminDb.getConnection();
			RecordVo vo=new RecordVo("constant");
			vo.setString("constant",contant);
			ContentDAO dao=new ContentDAO(conn);
			try {
				vo=dao.findByPrimaryKey(vo);
				if(vo!=null)
					values=vo.getString("str_value");
			} catch (GeneralException e) {
				// TODO Auto-generated catch block				
			//	e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				
			//	e.printStackTrace();
			}
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}finally
		{
			try{
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return values;
	}
	
	/**
	 * 职位说明书
	 * @param dao
	 * @param e01a1
	 * @return
	 */
	private String positionManual(String nbase,String a0100)
	{
		String e01a1="";		
		StringBuffer str=new StringBuffer();
		StringBuffer sql=new StringBuffer();
		RowSet rs=null;
		InputStream in = null;
		Connection conn=null;	
		try {
			conn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			sql.append("select str_value from constant where upper(constant)='PS_CARD_ATTACH'");				
			rs=dao.search(sql.toString());
			String value="false";
			if(rs.next())
			{
				value=rs.getString("str_value");
			}
			if(value==null|| "".equals(value)||"false".equals(value))
			 return "";
			sql.setLength(0);
			sql.append("select e01a1 from "+nbase+"A01 where a0100='"+a0100+"'");		
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				e01a1=rs.getString("e01a1");
			}
			if(e01a1==null||e01a1.length()<=0)
				return ""; 
			sql.setLength(0);
			sql.append("select ole,i9999 from k00  where UPPER(flag) = 'K'  ");
			sql.append("and  i9999=(select max(b.i9999) from k00 b where b.e01a1='"+e01a1+"') ");
			sql.append("and e01a1='"+e01a1+"'");
			rs=dao.search(sql.toString());
			if(rs.next()){
				 in = rs.getBinaryStream("ole"); 
				 int i9999=rs.getInt("i9999");
				 if(in!=null)
				 {
					 str.append("&nbsp;<a href=\"/pos/roleinfo/pos_dept_post?usertable=k00");
					 str.append("&usernumber="+e01a1+"&i9999="+i9999+"\">");
					 str.append("<img src=\"/images/attach.gif\" border=0>");
					 str.append("</a>");
				 }	 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
		    PubFunc.closeResource(rs);
		    PubFunc.closeResource(conn);
		    PubFunc.closeResource(in);
		}
		return str.toString();
	}
	
	private String codeToName(String codeitemid){
		CodeItem item = null;
		if(this.uplevel>0){
			item = AdminCode.getCode("UM", codeitemid, this.uplevel);
		}else{
			item = AdminCode.getCode("UM", codeitemid);
		}
		if(item!=null)
		  return item.getCodename();
		else
			return "";
	}
	public int getUplevel() {
		return uplevel;
	}
	public void setUplevel(int uplevel) {
		this.uplevel = uplevel;
	}
	public String getPrv_flag() {
		return prv_flag;
	}
	public void setPrv_flag(String prv_flag) {
		this.prv_flag = prv_flag;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	
}
