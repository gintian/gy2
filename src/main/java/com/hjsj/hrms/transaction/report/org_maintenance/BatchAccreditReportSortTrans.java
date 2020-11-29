package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class BatchAccreditReportSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		StringBuffer rtUnitCodes = new StringBuffer(); 
		ArrayList unitList = (ArrayList) this.getFormHM().get("selectedlist");
		
		//System.out.println(unitList.size());
		
		if(unitList == null || unitList.size() == 0){
			Exception e = new Exception(ResourceFactory.getProperty("edit_report.info7")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}else{
			for(int i=0; i<unitList.size(); i++){
				LazyDynaBean vo = (LazyDynaBean)unitList.get(i);
				//RecordVo vo = (RecordVo)unitList.get(i);
				String uc = (String)vo.get("unitcode");
				rtUnitCodes.append(uc);
				rtUnitCodes.append(",");
			}
		}
		if(rtUnitCodes != null || !"".equals(rtUnitCodes)){
			rtUnitCodes.deleteCharAt(rtUnitCodes.length()-1);
		}
		//System.out.println("批量授权表类填报单位:" + rtUnitCodes.toString());
		this.getFormHM().put("rtunitcodes",rtUnitCodes.toString());
		
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		String sql = "select name , tsortid ,sdes  from tsort";
		try{
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				RecordVo vo = new RecordVo("tsort");
				vo.setString("name" , this.frowset.getString("name"));				
				String temp = String.valueOf(this.frowset.getInt("tsortid"));
				vo.setString("tsortid",temp);			
				vo.setString("sdes" ,this.frowset.getString("sdes"));
				vo.setString("sid" ,"0");				
				list.add(vo);			
			}		
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		this.getFormHM().put("reporttypelist",list);
	}

}
