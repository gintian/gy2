package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

/**
 * 
 * 
 *<p>Title:ImportDataTrans.java</p> 
 *<p>Description:导入数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 17, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class ImportDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String[] oppositeItem=(String[])this.getFormHM().get("oppositeItem");   //对应指标 
			String[] relationItem=(String[])this.getFormHM().get("relationItem");  //关联指标
			String salaryid=(String)this.getFormHM().get("salaryid");
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			FormFile form_file = (FormFile) getFormHM().get("file");
			ArrayList originalDataList=(ArrayList)this.getFormHM().get("originalDataList");  ////源数据 列信息
			ArrayList updateDateList=(ArrayList)this.getFormHM().get("updateDateList"); //取得excel数据 lis
			/**薪资类别*/
            /**薪资类别*/
            try{
                SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
                int rowNums=gzbo.importFileDataToSalary(oppositeItem,relationItem,form_file,originalDataList,updateDateList);
                this.getFormHM().put("rowNums",String.valueOf(rowNums));
            }finally {
                form_file=null;
                originalDataList=null;
                updateDateList=null;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
