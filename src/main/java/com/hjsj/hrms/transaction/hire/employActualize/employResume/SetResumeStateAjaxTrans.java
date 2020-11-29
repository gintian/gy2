package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SetResumeStateAjaxTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String a0100 = (String)this.getFormHM().get("a0100");
			if(a0100 !=null && a0100.length() > 0 && !a0100.matches("^\\d+$"))
			    a0100 = PubFunc.decrypt(a0100);
			
			String zp_pos_id=(String)this.getFormHM().get("zp_pos_id");
			if(zp_pos_id !=null && zp_pos_id.length() > 0 && !zp_pos_id.matches("^\\d+$"))
			    zp_pos_id = PubFunc.decrypt(zp_pos_id);
			
			String state=(String)this.getFormHM().get("state");
			String operate=(String)this.getFormHM().get("operate");
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname=vo.getString("str_value");
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=xmlBo.getAttributeValues();
			String resume_state="";
			if(map.get("resume_state")!=null)
				resume_state=(String)map.get("resume_state");
			if("".equals(resume_state))
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置简历状态指标！"));
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("set".equals(operate))
			{
				dao.update("update zp_pos_tache set resume_flag='"+state+"' where a0100='"+a0100+"' and zp_pos_id='"+zp_pos_id+"' ");
				dao.update("update "+dbname+"a01 set "+resume_state+"='"+state+"' where a0100='"+a0100+"'");
				
			}
			else if("del".equals(operate))
			{
				dao.delete("delete from  zp_pos_tache  where  a0100='"+a0100+"' and zp_pos_id='"+zp_pos_id+"' ",new ArrayList());
			}
			else if("switch".equals(operate))
			{
				String person_type="";
				if(map.get("person_type")!=null)
					person_type=(String)map.get("person_type");
				if("".equals(person_type))
					throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置人才库标识指标！"));
			//	dao.delete("delete from zp_pos_tache where a0100='"+a0100+"'",new ArrayList());
				dao.update("update "+dbname+"a01 set "+resume_state+"='10',"+person_type+"='1'  where a0100='"+a0100+"'");
				
			}
			this.getFormHM().put("operate",operate);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
