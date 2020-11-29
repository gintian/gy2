package com.hjsj.hrms.transaction.standarduty;

import com.hjsj.hrms.businessobject.standarduty.DutyXmlBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Map;

public class SaveStandarduty extends IBusiness {

	public void execute() throws GeneralException {
		try {
           Map relevantset=(Map)this.getFormHM().get("relevantset");
           ArrayList  sduty=(ArrayList)this.getFormHM().get("sduty");
           ArrayList targetids=(ArrayList)this.getFormHM().get("targetids");
           
           DutyXmlBo dxb=new DutyXmlBo();
           String xml=dxb.createXML(relevantset, sduty, targetids);
           boolean flag = true;
           RecordVo option_vo=ConstantParamter.getConstantVo("POS_STANDARD");
           if(option_vo==null){
        	   flag = false;
           }
           option_vo = new RecordVo("constant");
           option_vo.setString("constant", "POS_STANDARD");
           option_vo.setString("str_value", xml);
           option_vo.setString("type","0");
           option_vo.setString("describe", "基准岗位对应指标");
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			  if(flag)
				dao.updateValueObject(option_vo);
			  else
				dao.addValueObject(option_vo);
				ConstantParamter.putConstantVo(option_vo, "POS_STANDARD");
				this.getFormHM().put("submitflag", "ok");
				
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}

}
