package com.hjsj.hrms.module.gz.analyse.historydata.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.SalaryHistoryDataService;
import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.impl.SalaryHistoryDataServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title RevertSalaryHistoryDataTrans
 * @Description 历史数据还原
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/1/13
 * @Version 1.0.0
 */
public class RevertSalaryHistoryDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String return_code = "success";
		String return_msg = "";
		Map return_data=new HashMap();
		Map returnStr=new HashMap();
		try {
			String type=(String)this.getFormHM().get("type");//0全部  1时间范围
			String startDate=(String)this.getFormHM().get("startDate");
			String endDate=(String)this.getFormHM().get("endDate");
			String salaryId=(String)this.getFormHM().get("salaryId");
			salaryId = PubFunc.decrypt(SafeCode.decode(salaryId));
			String strut=(String)this.getFormHM().get("strut");
			
			SalaryHistoryDataService service=new SalaryHistoryDataServiceImpl(this.frameconn, this.userView);
			
			/**
			 * 判断结构是否改变
			 */
			if("0".equals(strut)) {
				boolean flag = service.strutIsChange();
				service.syncSalaryTaxArchiveStrut();
				return_data.put("msg", (flag?"1":"0"));
				return;
			}

			String[] salaryids2 = salaryId.split("`");
			//如果用户没有当前薪资类别的资源权限   
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			for (int i = 0; i < salaryids2.length; i++)
			{
			    String _salaryid = salaryids2[i];
			    if(_salaryid!=null&&_salaryid.trim().length()>0)
			    { 
					safeBo.isSalarySetResource(_salaryid,null);
			    }
			}
			
			String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			if(b_units.length()==0 || StringUtils.equalsIgnoreCase(b_units,"UN")){
				if(!(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())))
				{
						throw new GeneralException("gz.historyData.msg.noRevertPriv");
				}
			}
			
			service.syncSalaryarchiveStrut();
			service.syncSalaryTaxArchiveStrut();
    		String fileName=service.revertSalaryHistoryData(type,startDate, endDate,salaryId);
    		if(!StringUtils.isBlank(fileName)) {
    			return_data.put("fileName", fileName);
    		}

		}catch(GeneralException e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = e.getErrorDescription();
		}finally {
			returnStr.put("return_code", return_code);
			returnStr.put("return_msg", return_msg);
			returnStr.put("return_data", return_data);
			this.formHM.put("returnStr", returnStr);
		}
	}
	
}
