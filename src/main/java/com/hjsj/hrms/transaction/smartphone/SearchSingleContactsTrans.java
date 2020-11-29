/**
 * 
 */
package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

/**
 * @author cmq
 * Dec 28, 20101:09:56 PM
 */
public class SearchSingleContactsTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			//SS_EMAIL ,SS_MOBILE_PHONE
			RecordVo vo=ConstantParamter.getRealConstantVo("SS_EMAIL");
			String email_field=vo.getString("str_value");	

				
			vo=ConstantParamter.getRealConstantVo("SS_MOBILE_PHONE");
			String phone_field=vo.getString("str_value");	
			
			String nbase=(String)this.getFormHM().get("nbase");
			String a0100=(String)this.getFormHM().get("a0100");
			StringBuffer buf=new StringBuffer();
			buf.append("select a0101,b0110,e0122,e01a1");
			buf.append(",");
			buf.append(phone_field);
			if(!(email_field==null|| "#".equalsIgnoreCase(email_field)||email_field.length()==0))
			{
				buf.append(",");
				buf.append(email_field);				
			}	
			buf.append(" from ");
			buf.append(nbase);
			buf.append("a01 where a0100='");
			buf.append(a0100);
			buf.append("'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(buf.toString());
			/**内容*/
			/*
            <li style="height:60px">
                <img src="/images/1.jpg"/>
            </li>
            <li>
                <h3>部门：研究一院人力资源部薪酬处</h3>
            </li>  
            <li>
                <h3>姓名：陈猛清</h3>
            </li>    
            <li>
                <h3>电话：13801297310</h3>
				<a href="tel:013801297310"></a>                
            </li> 
            
            <li>
                <h3>邮箱：jdoe@foo.com</h3>
				<a href="mailto:jdoe@foo.com"></a>                  
            </li> 			 
			 */
			buf.setLength(0);
			if(rset.next())
			{
				
		        String filename=ServletUtilities.createOleFile(nbase+"A00",a0100,this.getFrameconn());	
		        
				//<ul data-role="listview" data-inset="true">
				buf.append("<ul data-role=\"listview\" data-inset=\"true\" id=\"myul\">");
				buf.append("<li style=\"height:60px\">");
				//<hrms:ole name="element" dbpre="${nbase}" a0100="a0100" href="###" scope="page" width="85" />
				buf.append("<img src=\"/servlet/DisplayOleContent?filename=");
				buf.append(filename);
				buf.append("\" width=\"85\" border=0 />");
				//buf.append("<img src=\"/images/1.jpg\"/>");
				buf.append("</li>");
				/**部门*/
				buf.append("<li>");				
				buf.append("<h3>");
				buf.append("部门:");
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
					display_e0122="0";				
				String codedesc=AdminCode.getOrgUpCodeDesc(rset.getString("e0122"), Integer.parseInt(display_e0122), 0);
				codedesc=codedesc==null?"":codedesc;
				buf.append(codedesc);
				buf.append("</h3>");
				buf.append("</li>");
				/**姓名*/
				buf.append("<li>");				
				buf.append("<h3>");
				buf.append("姓名:");
				String a0101=rset.getString("a0101");
				a0101=a0101==null?"":a0101;
				buf.append(a0101);
				buf.append("</h3>");
				buf.append("</li>");	
				/**电话*/
				buf.append("<li>");				
				buf.append("<h3>");
				buf.append("电话:");
				String phone=rset.getString(phone_field);
				phone=phone==null?"":phone;
				buf.append(phone);
				buf.append("</h3>");
				
				buf.append("<a href=\"tel:");
				buf.append(phone);
				buf.append("\">");
				buf.append("</a>");
				buf.append("</li>");
				/**邮箱*/
				buf.append("<li>");				
				buf.append("<h3>");
				buf.append("邮箱:");
				if(!(email_field==null|| "#".equalsIgnoreCase(email_field)||email_field.length()==0))
				{
					String email=rset.getString(email_field);
					email=email==null?"":email;
					buf.append(email);
				}
				buf.append("</h3>");
				if(!(email_field==null|| "#".equalsIgnoreCase(email_field)||email_field.length()==0))
				{				
					buf.append("<a href=\"mailto:");
					String email=rset.getString(email_field);
					email=email==null?"":email;
					buf.append(email);
					buf.append("\">");
					buf.append("</a>");
				}
				buf.append("</li>");	
				buf.append("</ul>");
			}
			rset.close();
			this.getFormHM().put("html", buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(ex);    			
		}

	}

}
