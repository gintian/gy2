package com.hjsj.hrms.module.system.personalsoduku.boxreport.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * 
* <p>Title:SaveBoxReportDataTrans </p>
* <p>Description: 保存盒式报表</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 12, 2015 1:10:49 PM
 */
public class SaveBoxReportDataTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		
		String cassid = (String)this.getFormHM().get("cassid");
		String status = (String)this.getFormHM().get("status");
		String obj = String.valueOf(this.getFormHM().get("flag"));
		int flag = Integer.valueOf(obj);
		String cassettename = (String)this.getFormHM().get("cassettename");
		String datasource = (String)this.getFormHM().get("datasource");
		String lateral_index = (String)this.getFormHM().get("lateral_index");
		String lateral_desc = (String)this.getFormHM().get("lateral_desc");
		String longitudinal_index = (String)this.getFormHM().get("longitudinal_index");
		String longitudinal_desc = (String)this.getFormHM().get("longitudinal_desc");
		String time_dimension = (String)this.getFormHM().get("time_dimension");
		String analysis_interval = (String)this.getFormHM().get("analysis_interval");
		String personnel_range = (String)this.getFormHM().get("personnel_range");
		String percentage = (String)this.getFormHM().get("percentage");
		String staff_view_url = (String)this.getFormHM().get("staff_view_url");
		String staff_listview_url = (String)this.getFormHM().get("staff_listview_url");
		
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			RecordVo resultVo = new RecordVo("t_sys_box_report");
			String cassette_id="";
			if(flag==0){//新增
				IDGenerator idg = new IDGenerator(2,this.getFrameconn());
				cassette_id = idg.getId("t_sys_box_report.box_id");
			}
			else if(flag==1){//修改
				cassette_id = cassid;
			}
			resultVo.setInt("box_id", Integer.valueOf(cassette_id));
			resultVo.setString("name", cassettename);
			resultVo.setString("data_from", datasource);
			resultVo.setString("h_field", lateral_index);
			resultVo.setString("h_field_desc", lateral_desc);
			resultVo.setString("v_field", longitudinal_index);
			resultVo.setString("v_field_desc", longitudinal_desc);
			resultVo.setString("time_dim_field", time_dimension);
			resultVo.setString("time_dim_type", analysis_interval);
			resultVo.setString("static_ids", personnel_range);
			resultVo.setInt("show_percent", Integer.valueOf(percentage));
			resultVo.setString("staff_view_url", staff_view_url);
			resultVo.setString("staff_listview_url", staff_listview_url);
			resultVo.setString("status", status);
			if(flag==0){//新增
				dao.addValueObject(resultVo);
				this.formHM.put("status", status);
				this.formHM.put("cassette_id", Integer.valueOf(cassette_id));
			}
			else if(flag==1){
				dao.updateValueObject(resultVo);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (GeneralException e) {
			e.printStackTrace();
		}
	}
}
