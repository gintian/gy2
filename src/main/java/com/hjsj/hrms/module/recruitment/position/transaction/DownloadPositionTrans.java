package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.ImportPositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class DownloadPositionTrans extends IBusiness{

	private int index = 0;
	private HSSFSheet codesetSheet=null;
	
	@Override
    public void execute() throws GeneralException {
		//long startTime=System.currentTimeMillis(); 
		String outName="";
		String items="";
		String fieldsetidlist="";
		String selectitemslist="";
		
		try{			
			ContentDAO dao = new ContentDAO(this.frameconn);			
			ImportPositionBo bo = new ImportPositionBo(this.frameconn, dao, this.userView);
			outName=bo.creatExcel();
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			outName = PubFunc.encrypt(outName);
			this.getFormHM().put("outName", outName);
		}		
	}		
}