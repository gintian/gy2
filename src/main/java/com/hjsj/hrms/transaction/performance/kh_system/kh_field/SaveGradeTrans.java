package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveGradeTrans.java</p>
 * <p>Description:保存标准标度</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2011-08-06</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SaveGradeTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String isClose = (String)map.get("isClose");
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id))
				per_comTable = "per_grade_competence"; // 能力素质标准标度
			String type=(String)this.getFormHM().get("type");
			String gradeid=(String)this.getFormHM().get("grade_id");
			String hiddenGradeid=(String)this.getFormHM().get("hiddenGradeid");
			String gradedesc=(String)this.getFormHM().get("gradedesc");
			String top_value=(String)this.getFormHM().get("top_value");
			if(top_value==null || top_value.trim().length()<=0)
				top_value = "0";
			String gradevalue =(String)this.getFormHM().get("gradevalue");
			String bottom_value=(String)this.getFormHM().get("bottom_value");
			if(bottom_value==null || bottom_value.trim().length()<=0)
				bottom_value = "0";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("1".equals(type))
			{
				RecordVo vo = new RecordVo(per_comTable);
				vo.setString("grade_template_id",gradeid);
				vo.setString("gradevalue",gradevalue);
				vo.setString("gradedesc",gradedesc);
				vo.setString("top_value", top_value);
				vo.setString("bottom_value",bottom_value);
				dao.addValueObject(vo);
			}
			else
			{
				StringBuffer buf = new StringBuffer();
				buf.append("update "+per_comTable+" set grade_template_id='");
				buf.append(gradeid+"',gradedesc=?,gradevalue='");
				buf.append(gradevalue+"',top_value='");
				buf.append(top_value+"',bottom_value='");
				buf.append(bottom_value+"' where grade_template_id='");
				buf.append(hiddenGradeid+"'");
				ArrayList list = new ArrayList();
				list.add(gradedesc);
				dao.update(buf.toString(),list);
			}
			this.getFormHM().put("isClose",isClose);
			this.getFormHM().put("type",type);
			this.getFormHM().put("grade_id", "");
			this.getFormHM().put("hiddenGradeid","");
			this.getFormHM().put("gradevalue", "");
			this.getFormHM().put("bottom_value", "");
			this.getFormHM().put("gradedesc", "");
			this.getFormHM().put("top_value", "");
			this.getFormHM().put("isrefresh", "2");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
