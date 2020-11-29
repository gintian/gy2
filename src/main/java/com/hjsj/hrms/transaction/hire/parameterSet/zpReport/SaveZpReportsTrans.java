package com.hjsj.hrms.transaction.hire.parameterSet.zpReport;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SaveZpReportsTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String zpReport=(String)this.getFormHM().get("zpReport");
		String zpReportContent=(String)this.getFormHM().get("zpReportContent");
		if(zpReportContent!=null&&zpReportContent.trim().length()!=0){
			zpReportContent=PubFunc.keyWord_reback(zpReportContent);
		}
		StringBuffer sql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.frameconn);
		RecordVo vo=new RecordVo("constant");
		
		if(zpReport!=null&&zpReport.length()!=0){
			
				try {
					
					sql.append("select * from constant where constant='");
					vo.setString("type", "A");
					if("1".equals(zpReport)){
						vo.setString("constant", "ZP_SY_MESSAGE");
						vo.setString("describe","首页报告");
						sql.append("ZP_SY_MESSAGE'");
						EmployNetPortalBo.ZP_SY_MESSAGE=zpReportContent;
					}
					if("2".equals(zpReport)){
						vo.setString("constant", "ZP_SOCIAL_MESSAGE");
						vo.setString("describe","社会招聘公告");
						sql.append("ZP_SOCIAL_MESSAGE'");
						EmployNetPortalBo.ZP_SOCIAL_MESSAGE=zpReportContent;
					}
					if("3".equals(zpReport)){
						vo.setString("constant", "ZP_SCHOOL_MESSAGE");
						vo.setString("describe","校园招聘公告");
						sql.append("ZP_SCHOOL_MESSAGE'");
						EmployNetPortalBo.ZP_SCHOOL_MESSAGE=zpReportContent;
					}
					
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next()){
						vo=dao.findByPrimaryKey(vo);
						if(zpReportContent!=null){
							if(zpReportContent.trim().length()==0){
								vo.setString("str_value","");
							}else{
								vo.setString("str_value",zpReportContent);
							}
						}
							
						dao.updateValueObject(vo);
					}else{
						if(zpReportContent!=null){
							if(zpReportContent.trim().length()==0){
								vo.setString("str_value","");
							}else{
								vo.setString("str_value",zpReportContent);
							}
						}
						dao.addValueObject(vo);
					}
				} catch (Exception e) {
						e.printStackTrace();
				}
			this.getFormHM().put("zpReport", zpReport);
		}
	}

}
