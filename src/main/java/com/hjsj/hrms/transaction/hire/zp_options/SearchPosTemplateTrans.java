package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 显示岗位责任说明书设置
 * <p>Title:SearchPosTemplateTrans.java</p>
 * <p>Description>:SearchPosTemplateTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 18, 2010 2:03:27 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class SearchPosTemplateTrans extends IBusiness {
	
	public void execute() throws GeneralException {	
		//RecordVo vo = new RecordVo("CONSTANT");
		//vo.setString("constant", "PS_CARD_ATTACH");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String value="";
		try {
			//vo=dao.findByPrimaryKey(vo);
			String sql="select str_value from constant where upper(constant)='PS_CARD_ATTACH'";
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
		this.getFormHM().put("ps_card_attach", value);
		try {
			//vo=dao.findByPrimaryKey(vo);
			String sql="select str_value from constant where upper(constant)='ZP_POS_TEMPLATE'";
			/*RecordVo vo = new RecordVo("CONSTANT");
			vo.setString("constant", "PS_CARD_ATTACH");*/		
			RecordVo posTemplatevo = new RecordVo("CONSTANT");
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				posTemplatevo.setString("str_value" ,this.frowset.getString("str_value"));				
			}
			this.getFormHM().put("posTemplatevo",posTemplatevo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	    
	}

}
