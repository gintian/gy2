package com.hjsj.hrms.transaction.report.retport_status;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetUnitMenInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");
			String unitcode=(String)this.getFormHM().get("unitcode");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("desc".equals(opt))
			{
				StringBuffer context=new StringBuffer("");
				this.frowset=dao.search("select * from operUser where unitcode='"+unitcode+"'");
				context.append("&nbsp;<img src='/images/man.gif' />&nbsp; "+ResourceFactory.getProperty("orglist.reportunitlist.reportprincipal"));
				context.append("<table bgColor='#FFFFFF'  border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'> ");
				context.append("<thead><tr> <td  width='30'  align='center' class='TableRow' nowrap >"+ResourceFactory.getProperty("kh.field.seq")+"</td>");
				context.append("<td width='60' align='center' class='TableRow' nowrap > "+ResourceFactory.getProperty("hire.zp_persondb.username")+"</td>");			
				context.append("<td width='80' align='center' class='TableRow' nowrap > "+ResourceFactory.getProperty("hire.zp_persondb.fullName")+"</td>");			
				context.append("<td width='150' align='center' class='TableRow' nowrap > "+ResourceFactory.getProperty("system.sms.mobimun")+"</td>");
				context.append("<td width='150' align='center' class='TableRow' nowrap >"+ResourceFactory.getProperty("t_template.approve.mode.email")+"</td> </tr>  </thead>");
				int i=1;
				while(this.frowset.next())
				{
					
			        if(i%2==0)
			        	  context.append("<tr class='trShallow'>");
			        else
			        	  context.append("<tr class='trDeep'>");
					context.append("<td align='left' class='RecordRow' nowrap>"+i+"</td>");
					String username=this.frowset.getString("username")==null?"&nbsp;":this.frowset.getString("username");
					String fullname=this.frowset.getString("fullname")==null?"&nbsp;":this.frowset.getString("fullname");
					String phone=this.frowset.getString("phone")==null?"&nbsp;":this.frowset.getString("phone");
					String email=this.frowset.getString("email")==null?"&nbsp;":this.frowset.getString("email");
					
					context.append("<td align='left' class='RecordRow' nowrap>"+username+"</td>");
					context.append("<td align='left' class='RecordRow' nowrap>"+fullname+"</td>");
					context.append("<td align='left' class='RecordRow' nowrap>"+phone+"</td>");
					context.append("<td align='left' class='RecordRow' nowrap>"+email+"</td>");
					
					context.append("</tr>");
					i++;
				}
				context.append("</table>");
				this.getFormHM().put("context",SafeCode.encode(context.toString()));
			}
			else
			{
				String selfUnitCode="";
				this.frowset=dao.search("select unitcode from operuser where username='"+this.getUserView().getUserName()+"'");
				if(this.frowset.next())
					selfUnitCode=this.frowset.getString("unitcode");
				
				String sql="select unitcode from tt_organization where unitcode=(select parentid from tt_organization where unitcode='"+unitcode+"' and parentid like '"+selfUnitCode+"%'  )";
				this.frowset=dao.search(sql);
				String parent_unitcode="";
				if(this.frowset.next())
					parent_unitcode=this.frowset.getString("unitcode");
				else
					parent_unitcode=selfUnitCode;
				this.getFormHM().put("parent_unitcode",parent_unitcode);
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
