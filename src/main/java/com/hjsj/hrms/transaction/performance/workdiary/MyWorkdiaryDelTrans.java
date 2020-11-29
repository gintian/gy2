package com.hjsj.hrms.transaction.performance.workdiary;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class MyWorkdiaryDelTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		String state = (String)hm.get("state");
		state=state!=null&&state.trim().length()>0?state:"";
		if("0".equals(state)){
			ArrayList seldiary=(ArrayList) hm.get("seldiary");
			for(int i=0;i<seldiary.size();i++){
				DynaBean dynabean=(DynaBean)seldiary.get(i);
				String p0100=(String) dynabean.get("p0100");
				RecordVo p01Vo=new RecordVo("P01");
				p01Vo.setString("p0100",p0100);
				try {
					dao.deleteValueObject(p01Vo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}else{
			HashMap reqhm=(HashMap) hm.get("requestPamaHM");
			String p0100 = (String)reqhm.get("p0100");
			reqhm.remove("p0100");
			p0100=p0100!=null&&p0100.trim().length()>0?p0100:"";
			String arr[] = p0100.split(",");
			for(int i=0;i<arr.length;i++){
				RecordVo p01Vo=new RecordVo("P01");
				p01Vo.setString("p0100",arr[i]);
				try {
					dao.deleteValueObject(p01Vo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
		
	}

}
