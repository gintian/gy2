package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SetRelationTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String[] oppositeItem=(String[])this.getFormHM().get("oppositeItem");   //对应指标 
			String[] relationItem=(String[])this.getFormHM().get("relationItem");  //关联指标
			String salaryid=(String)this.getFormHM().get("salaryid");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			/* 薪资发放-导入-没对应数据结果错误问题(转码) xiaoyun 2014-9-25 start */
			String[] temp;
			if(relationItem != null) {
				temp = new String[relationItem.length];
				for (int i = 0; i < relationItem.length; i++) {
					temp[i] = PubFunc.keyWord_reback(relationItem[i]);
				}
				System.arraycopy(temp, 0, relationItem, 0, relationItem.length);
			}
			if(oppositeItem != null) {				
				temp = new String[oppositeItem.length];
				for (int i = 0; i < oppositeItem.length; i++) {
					temp[i] = PubFunc.keyWord_reback(oppositeItem[i]);
				}
				System.arraycopy(temp, 0, oppositeItem, 0, oppositeItem.length);
			}
			/* 薪资发放-导入-没对应数据结果错误问题(转码) xiaoyun 2014-9-25 end */
			ArrayList relationItemList=gzbo.getArrayList(relationItem);
			ArrayList oppositeItemList=gzbo.getArrayList(oppositeItem);
			
			
			
			this.getFormHM().put("relationItemList",relationItemList);
			this.getFormHM().put("oppositeItemList",oppositeItemList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	

}
