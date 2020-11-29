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

public class GetNlszGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		String point_id=(String)this.getFormHM().get("point_id");
		String twid=this.getFormHM().get("twid")!=null?(String)this.getFormHM().get("twid"):"";
		String pos0=this.getFormHM().get("pos0")!=null?(String)this.getFormHM().get("pos0"):"";
		String pos1=this.getFormHM().get("pos1")!=null?(String)this.getFormHM().get("pos1"):"";
		String pos2=this.getFormHM().get("pos2")!=null?(String)this.getFormHM().get("pos2"):"";
//		System.out.println("body="+pos2);
//		System.out.println("table="+twid);
		int wid = Integer.valueOf(pos2)-Integer.valueOf(twid)-100;
		wid = wid<400?400:wid;
		wid = wid>800?800:wid;
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
				dataHtml.append("<table width='"+wid+"' border='0'  align='center' cellsPacing='0' cellPadding='0' class='ListTable'   > ");
				dataHtml.append("<thead><tr> <td align='left' style='border-right:0px;' class='TableRow' nowrap >");
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
					dataHtml.append("<font size=4 >");
				dataHtml.append("&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.pointExplain"));
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
					dataHtml.append("</font>");
				
				dataHtml.append("</td>");
				dataHtml.append("<td align='right' style='border-left:0px;' class='TableRow' nowrap >");
				dataHtml.append("<img src='/images/del.gif' onclick=\"hidden_nlsz();\" />");
				dataHtml.append("</td></tr> </thead>");
				
				dataHtml.append("<tr><td align='center' colspan='2' class='RecordRow' >");
				dataHtml.append("<div align='left' class='common_border_color' style=' margin-right: -5px;border-right:1px solid; OVERFLOW-Y: auto; HEIGHT: 90px; WIDTH: "+wid+";'>");
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
					dataHtml.append("<font size=4 >");
				description=description.replaceAll(" ","&nbsp;&nbsp;");
				dataHtml.append(description.replaceAll("\r\n","<br>"));
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))  //中国联通
					dataHtml.append("</font>");
				dataHtml.append("</div>");
				dataHtml.append("</td></tr>");
				dataHtml.append("</table>");
//				dataHtml.append("<tr><td align='center' colspan='2' class='RecordRow' >");
				
				PersonPostModalBo ppo = new PersonPostModalBo(this.frameconn);
				String per_comTable = "per_grade_competence"; // 能力素质标准标度
				wid = wid+7;
				//【60750】VFS+UTF-8+达梦：能力素质/评估计划/打分界面指标说明顺序不对
				this.frowset=dao.search("select pgt.gradedesc gradedesc_0,pg.* from per_grade pg,"+per_comTable+" pgt where pg.gradecode=pgt.grade_template_id and  pg.point_id='"+point_id+"' order by pg.grade_id asc ");
			dataHtml.append("<div class='common_border_color' style=' float:left; border:1px solid;border-top:0px; OVERFLOW: auto; HEIGHT: 310px; WIDTH: "+wid+"; '>");
				dataHtml.append("<table width='100%'  border='0' bgColor='#FFFFFF' bordercolor='#0066cc'  cellspacing='0'  align='center' cellpadding='0' class='ListTable'   > ");
				dataHtml.append("<thead><tr> <td  width='14%'  align='center' style='border-left:0px;border-top:0px;' class='TableRow' nowrap >"+ResourceFactory.getProperty("label.performance.standardGrade")+"</td>");
				dataHtml.append("<td width='50%' align='center' style='border-left:0px;border-top:0px;' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.gradeContext")+"&nbsp;&nbsp;&nbsp;</td>");			
				dataHtml.append("<td width='12%' align='center' style='border-left:0px;border-top:0px;' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.scale")+"&nbsp;&nbsp;&nbsp;</td>");			
				dataHtml.append("<td width='12%' align='center' style='border-left:0px;border-top:0px;' class='TableRow' nowrap > &nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.upperValue")+"&nbsp;&nbsp;</td>");
				dataHtml.append("<td width='12%' align='center' style='border-left:0px;border-right:0px;border-top:0px;' class='TableRow' nowrap >&nbsp;&nbsp;"+ResourceFactory.getProperty("label.performance.bottomValue")+"&nbsp;&nbsp;</td> </tr>  </thead>");
//				dataHtml.append("<tr><td colspan='5' class='RecordRow'><div  style='border:0px; OVERFLOW-Y: auto; height: 350px'>");
//				dataHtml.append("<table width='100%''  border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'  >");
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
					
					dataHtml.append("><td width='14%' align='left' style='border-left:0px;border-top:0px;' class='RecordRow'  nowrap >"+this.frowset.getString("gradedesc_0")+"</td>");
					dataHtml.append("<td width='50%' align='left' style='0px;border-top:0px;' class='RecordRow' >"+Sql_switcher.readMemo(this.frowset,"gradedesc")+"</td>");
					dataHtml.append("<td width='12%' align='left' style='border-top:0px;' class='RecordRow' nowrap >"+gradevalue+"</td>");
					dataHtml.append("<td width='12%' align='left' style='border-top:0px;' class='RecordRow' nowrap >"+topValue+"</td>");
					dataHtml.append("<td width='12%' align='left' style='border-right:0px;border-top:0px;' class='RecordRow' nowrap >"+bottomValue+"</td>");
					dataHtml.append("</tr>");
					i++;
				}
//				dataHtml.append("</table>");
//				dataHtml.append("</div></td></tr>");
				dataHtml.append("</table>");
			dataHtml.append("</div>");
//			dataHtml.append("</td></tr>");
//			dataHtml.append("</table>");
			
			this.getFormHM().put("dataHtml",SafeCode.encode(dataHtml.toString()));
			this.getFormHM().put("twid",twid);
			this.getFormHM().put("pos0",pos0);
			this.getFormHM().put("pos1",pos1);
			this.getFormHM().put("wid", wid);
			this.getFormHM().put("srcobj_width",srcobj_width);
			this.getFormHM().put("srcobj_height",srcobj_height);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	}

}
