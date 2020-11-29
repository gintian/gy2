package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:GetPerGradeTrans.java</p>
 * <p>Description:多人考评指标标准标度</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class GetPerGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		String point_id=(String)this.getFormHM().get("point_id");
		
		String pos0=this.getFormHM().get("pos0")!=null?(String)this.getFormHM().get("pos0"):"";
		String pos1=this.getFormHM().get("pos1")!=null?(String)this.getFormHM().get("pos1"):"";
		String srcobj_width=this.getFormHM().get("srcobj_width")!=null?(String)this.getFormHM().get("srcobj_width"):"";
		String srcobj_height=this.getFormHM().get("srcobj_height")!=null?(String)this.getFormHM().get("srcobj_height"):"";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			
			this.frowset=dao.search("select * from per_point where point_id='"+point_id+"'");
			String description="";
			String visible="2";
			if(this.frowset.next())
			{
				if(this.frowset.getString("visible")!=null)
					visible=this.frowset.getString("visible");
				if(this.frowset.getString("description")!=null)
					description=Sql_switcher.readMemo(this.frowset,"description");
			}
			StringBuffer dataHtml=new StringBuffer("");
			if("2".equals(visible))
			{
				PersonPostModalBo ppo = new PersonPostModalBo(this.frameconn);
				String per_comTable = "per_grade_template"; // 绩效标准标度
				if(ppo.getComOrPer(point_id,"poi"))
					per_comTable = "per_grade_competence"; // 能力素质标准标度
				this.frowset=dao.search("select pgt.gradedesc gradedesc_0,pg.* from per_grade pg,"+per_comTable+" pgt where pg.gradecode=pgt.grade_template_id and  pg.point_id='"+point_id+"'");
				
				dataHtml.append("<table width='600' border='0' cellspacing='0' bgColor='#FFFFFF'  align='center' cellpadding='0' class='ListTable'   > ");
				dataHtml.append("<thead><tr> <td  width='80'  align='center' class='TableRow' nowrap >"+ResourceFactory.getProperty("label.performance.standardGrade")+"</td>");
				dataHtml.append("<td width='260' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.gradeContext")+"&nbsp;&nbsp;&nbsp;</td>");			
				dataHtml.append("<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.scale")+"&nbsp;&nbsp;&nbsp;</td>");			
				dataHtml.append("<td width='100' align='center' class='TableRow' nowrap > &nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.upperValue")+"&nbsp;&nbsp;</td>");
				dataHtml.append("<td width='100' align='center' class='TableRow' nowrap >&nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.bottomValue")+"&nbsp;&nbsp;</td> </tr>  </thead>");
				int i=0;
				while(this.frowset.next())
				{
					String gradevalue="";
					String topValue="";
					String bottomValue="";
					if(this.getFrowset().getString("gradevalue")!=null)
					{
						gradevalue=this.getFrowset().getString("gradevalue").trim();
						if(gradevalue!=null && gradevalue.trim().length()>0)
							gradevalue=Double.toString(Double.parseDouble(gradevalue));//去掉小数点后面的0																			
						
//						gradevalue=this.getFrowset().getString("gradevalue").trim();
					}
					if(this.getFrowset().getString("top_value")!=null)
					{
						topValue=this.getFrowset().getString("top_value").trim();
						if(topValue!=null && topValue.trim().length()>0)
							topValue=Double.toString(Double.parseDouble(topValue));//去掉小数点后面的0																			
						
//						topValue=this.getFrowset().getString("top_value").trim();
					}
					if(this.getFrowset().getString("bottom_value")!=null)
					{
						bottomValue=this.getFrowset().getString("bottom_value").trim();
						if(bottomValue!=null && bottomValue.trim().length()>0)
							bottomValue=Double.toString(Double.parseDouble(bottomValue));//去掉小数点后面的0																									
						
//						bottomValue=this.getFrowset().getString("bottom_value").trim();
					}
	
					dataHtml.append("<tr");
					if(i%2==0)
						dataHtml.append(" background-color: #FFFFFF; ");
					else
						dataHtml.append(" class='trDeep' ");
					
					dataHtml.append("><td align='left' class='RecordRow'  nowrap >"+this.frowset.getString("gradedesc_0")+"</td>");
					dataHtml.append("<td align='left'  class='RecordRow' >"+Sql_switcher.readMemo(this.frowset,"gradedesc")+"</td>");
					dataHtml.append("<td  align='left' class='RecordRow' nowrap >"+gradevalue+"</td>");
					dataHtml.append("<td  align='left' class='RecordRow' nowrap >"+topValue+"</td>");
					dataHtml.append("<td  align='left' class='RecordRow' nowrap >"+bottomValue+"</td></tr>");
					i++;
				}
				dataHtml.append("</table>");	
			}
			else
			{
				//dataHtml.append("<table width='250'  border='1' cellspacing='3' bordercolor='#0066cc'  bgcolor='#ffffff' ><tr><td align='left' valign='top'>&nbsp;&nbsp;&nbsp;&nbsp;");
				//dataHtml.append(description.replaceAll("\r\n","#@#"));
				//dataHtml.append("<br>&nbsp;</td></tr></table>");
				
				dataHtml.append("<table width='600'  border='0'  bgColor='#FFFFFF' bordercolor='#0066cc'  cellspacing='0'  align='center' cellpadding='0' class='ListTable'   > ");
				dataHtml.append("<thead><tr> <td  width='100%'  align='center' class='TableRow' nowrap >");
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
					dataHtml.append("<font size=4 >");
				dataHtml.append(ResourceFactory.getProperty("label.performance.pointExplain"));
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
					dataHtml.append("</font>");
				
				dataHtml.append("</td></tr> </thead>");
				dataHtml.append("<tr background-color: #FFFFFF; ><td align='left' class='RecordRow' >");
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
					dataHtml.append("<font size=4 >");
				description=description.replaceAll(" ","&nbsp;&nbsp;");
				dataHtml.append(description.replaceAll("\r\n","<br>"));
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
					dataHtml.append("</font>");
				dataHtml.append("<br>&nbsp;</td></tr></table>");
				
			}
			
			
			this.getFormHM().put("dataHtml",SafeCode.encode(dataHtml.toString()));
			
			this.getFormHM().put("pos0",pos0);
			this.getFormHM().put("pos1",pos1);
			this.getFormHM().put("srcobj_width",srcobj_width);
			this.getFormHM().put("srcobj_height",srcobj_height);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	}

}
