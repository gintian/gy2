package com.hjsj.hrms.transaction.performance.objectiveManage.myObjective;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;

/**
 * <p>Title:ExportObjectiveCardTrans.java</p>
 * <p>Description>:导出目标卡 logo 1:我的目标；2:员工目标；3:目标评分；4:目标执行情况；5:团队绩效 </p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-09-01 下午03:57:31</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ExportObjectiveCardTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		HSSFWorkbook workbook= null;
		try
		{
			//a0100-planid/a0100-planid
			String records=(String)this.getFormHM().get("records");
			String model=(String)this.getFormHM().get("model");	// 1:团对 2:我的目标 3:目标制订 4.目标评估 5.目标结果 6:目标执行情况 7:目标卡代制订 8:评分调整					
			String body_id=(String)this.getFormHM().get("body_id"); // 主体类别
			String opt=(String)this.getFormHM().get("underOpt");	// 0:查看 1：操作 2.打分
			String logo=(String)this.getFormHM().get("logo");	// 1:我的目标；2:员工目标；3:目标评分；4:目标执行情况；5:团队绩效
			String fileName="";
//			if("3".equals(logo)) {
//    			HSSFWorkbook workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
//                ArrayList sheetNames = new ArrayList();    //存放已经导出的sheet名
//                opt = opt.substring(0, opt.length()-1);
//                body_id= body_id.substring(0, body_id.length()-1);
//    			String[] arr=records.split("/");
//    			String[] opts=opt.split("`");
//    			String[] body_ids=body_id.split("`");
//    			for(int u=0;u<arr.length;u++)
//                {
//    			    String[] str=arr[u].split("-");
//    			    ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),str[1],str[0],this.getUserView(),model,body_ids[u].trim(),opts[u].trim());
//    			    bo.setBody_id(body_id);
//    			    bo.setSheetNames(sheetNames);
//    			    bo.setObjectFlag("objectFlag");
//    			    bo.setWorkbook(workbook);
//    			    fileName=bo.getObjectCardExcel();
//    			    sheetNames = bo.getSheetNames();
//                }
//			} else {
//			  MyObjectiveBo bo = new MyObjectiveBo(this.getFrameconn());
//	          if(model!=null)
//	               bo.setModel(model);
//	          fileName=bo.ExportEXCEL(records, model,this.getUserView(), body_id, opt, logo);
//			}
			
		  workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
          ArrayList sheetNames = new ArrayList();    //存放已经导出的sheet名
          opt = opt.substring(0, opt.length()-1);
          body_id= body_id.substring(0, body_id.length()-1);
			String[] arr=records.replaceAll("／", "/").split("/");
			String[] opts=opt.split("`");
			String[] body_ids=body_id.split("`");
			for(int u=0;u<arr.length;u++)
          {
			    String[] str=arr[u].split("-");
			   //if ("1".equals(model)||"2".equals(model)){//团队绩效、我的目标 需解密 其他等前台放开再改 wangrd 20141129
			        str[0]=PubFunc.decryption(str[0]);                
			        str[1]=PubFunc.decryption(str[1]);
              //  }
			    ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),str[1],str[0],this.getUserView(),model,body_ids[u].trim(),opts[u].trim());
			    bo.setBody_id(body_id);
			    bo.setSheetNames(sheetNames);
			    bo.setObjectFlag("objectFlag");
			    bo.setWorkbook(workbook);
			    fileName=bo.getObjectCardExcel();
			    sheetNames = bo.getSheetNames();
          }
			//20/3/6 xus vfs改造
			this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(workbook);
		}
		
	}

}
