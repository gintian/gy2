package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchJobTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String value="";
		try {
			//vo=dao.findByPrimaryKey(vo);
			String sql="select str_value from constant where upper(constant)='PS_C_CARD_ATTACH'";
			/*RecordVo vo = new RecordVo("CONSTANT");
			vo.setString("constant", "PS_CARD_ATTACH");*/			
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				value=this.frowset.getString("str_value");
			}
			if(value==null|| "".equals(value))
			 value="false";
		
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		this.getFormHM().put("ps_c_card_attach", value);
		try {
			//vo=dao.findByPrimaryKey(vo);
			String sql="select str_value from constant where upper(constant)='ZP_JOB_TEMPLATE'";
			/*RecordVo vo = new RecordVo("CONSTANT");
			vo.setString("constant", "PS_CARD_ATTACH");*/		
			RecordVo jobTemplatevo = new RecordVo("CONSTANT");
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				jobTemplatevo.setString("str_value" ,this.frowset.getString("str_value"));				
			}
			this.getFormHM().put("jobTemplatevo",jobTemplatevo);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
