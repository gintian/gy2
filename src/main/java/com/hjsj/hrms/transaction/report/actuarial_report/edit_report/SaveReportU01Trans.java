package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 保存表1-特别事项
 * @author Owner
 *
 */
public class SaveReportU01Trans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String submit=(String)hm.get("submit");
		String unitcode=(String)this.getFormHM().get("unitcode");
		ArrayList fieldlsitU01=(ArrayList)this.getFormHM().get("fieldlsitU01");
		String id=(String)this.getFormHM().get("id");
		if(id==null||id.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.edit_report.nocycle"),"",""));
		RecordVo vo=new RecordVo("U01");
		vo.setString("unitcode", unitcode);
		vo.setString("id", id);
		String sql="select * from U01 where unitcode='"+unitcode+"' and id='"+id+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				vo=dao.findByPrimaryKey(vo);
				vo=new RecordVo("U01");
				vo.setString("unitcode", unitcode);
				vo.setString("id", id);
				 for(int i=0;i<fieldlsitU01.size();i++)
			     {
			        FieldItem field=(FieldItem)fieldlsitU01.get(i);
			        vo.setString(field.getItemid(), field.getValue());
			     }
				 dao.updateValueObject(vo);
			}else
			{
				vo=new RecordVo("U01");
				vo.setString("unitcode", unitcode);
				vo.setString("id", id);
				 for(int i=0;i<fieldlsitU01.size();i++)
			     {
			        FieldItem field=(FieldItem)fieldlsitU01.get(i);
			        vo.setString(field.getItemid(), field.getValue());
			     }
				 dao.addValueObject(vo);
			}
			dao.update("delete from tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+id+" and report_id='U01'");
			if(submit!=null&& "true".equals(submit))
			{
				sql="insert into  tt_calculation_ctrl (report_id,id,flag,unitcode) values ('U01',"+id+",1,'"+unitcode+"')";
				dao.update(sql);
				this.getFormHM().put("flag","1");
			}
			else
			{	sql="insert into  tt_calculation_ctrl (report_id,id,flag,unitcode) values ('U01',"+id+",0,'"+unitcode+"')";
				dao.update(sql);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
