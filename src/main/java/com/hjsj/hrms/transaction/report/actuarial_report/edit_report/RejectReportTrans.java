package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class RejectReportTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String cycle_id=(String)this.getFormHM().get("cycle_id");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String report_ids=(String)this.getFormHM().get("report_id");
			String cause=SafeCode.decode((String)this.getFormHM().get("cause"));
			String []report_id = report_ids.split(",");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			for(int i =0;i<report_id.length;i++){
				String report = report_id[i];
				if(!"".equals(report.trim())){
			RecordVo vo=new RecordVo("tt_calculation_ctrl");
			vo.setInt("id", Integer.parseInt(cycle_id));
			vo.setString("unitcode",unitcode);
			vo.setString("report_id",report);
			vo=dao.findByPrimaryKey(vo);
			vo.setString("description", cause);
			vo.setInt("flag",2);
			dao.updateValueObject(vo);
			if(report.trim().startsWith("u02")||report.trim().startsWith("U02")){
				 vo=new RecordVo("u02");
				vo.setInt("id", Integer.parseInt(cycle_id));
				vo.setString("unitcode",unitcode);
				
				vo.setString("escope",report.substring(report.indexOf("_")+1));
				String sqlstr=" update u02 set editflag=2 where id= "+Integer.parseInt(cycle_id)+" and unitcode='"+unitcode+"' and escope="+report.substring(report.indexOf("_")+1)+" ";
				
				dao.update(sqlstr);
			}
				}
			}
			while(true)
			{
				this.frowset=dao.search("select parentid from tt_organization where parentid<>unitcode and unitcode='"+unitcode+"'");
				if(this.frowset.next())
				{
					String parentid=this.frowset.getString("parentid");
					for(int i =0;i<report_id.length;i++){
						String report = report_id[i];
						if(!"".equals(report.trim())){
					this.frowset=dao.search("select flag from tt_calculation_ctrl where unitcode='"+parentid+"' and report_id='"+report+"' and flag=1 and id="+cycle_id);
					if(this.frowset.next())
						dao.update("update tt_calculation_ctrl set flag=-1 where unitcode='"+parentid+"' and report_id='"+report+"'  and id="+cycle_id);
						}
					}
					unitcode=parentid;
				}
				else 
					break;
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
