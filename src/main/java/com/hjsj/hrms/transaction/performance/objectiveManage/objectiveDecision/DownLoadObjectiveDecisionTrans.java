package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveDecision;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;

/**
 * <p>Title:SearchObjectiveDecisionTrans.java</p>
 * <p>Description>:下载目标卡模板</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 10, 2011 10:15:36 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class DownLoadObjectiveDecisionTrans extends IBusiness
{
	public void execute() throws GeneralException 
	{
		try(
			HSSFWorkbook workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
		)
		{
			String plan_id = PubFunc.decryption((String)this.getFormHM().get("plan_id"));
			String object_ids=SafeCode.decode((String)this.getFormHM().get("object_id"));
			 
			String model = (String)this.getFormHM().get("model");
			String body_id = (String)this.getFormHM().get("body_id");
			String opt = (String)this.getFormHM().get("opt");
			
			String searchOrBatch = (String)this.getFormHM().get("searchOrBatch");
			
			String outName="";
			String[] items = object_ids.replaceAll("／", "/").split("/");

			ArrayList sheetNames = new ArrayList();    //存放已经导出的sheet名
			for (int i = 0; i < items.length; i++)
			{				
				String object_id=PubFunc.decryption(items[i]);
				
				// 批量下载时，考核对象id在在职人员库中可能已经不存在，导致无法导出，现把不存在的人员过滤掉 chent 20160314 end
				ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView(),model,body_id,opt);	
				if("serch".equalsIgnoreCase(searchOrBatch))//下载模板不导出评价人和评价人签批项
					bo.setIsShowOpinion(1);
				
				bo.setSheetNames(sheetNames);
				outName=bo.downLoadObjectiveData(workbook);	
				sheetNames = bo.getSheetNames();
			}
					
			//20/3/6 xus vfs改造		
			this.getFormHM().put("outName",PubFunc.encrypt(outName));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}			
	}	
}
