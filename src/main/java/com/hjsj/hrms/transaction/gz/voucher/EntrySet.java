package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：EntrySet   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:41:51 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:41:51 PM   
* 修改备注：   获取分录分组指标
* @version    
*
 */

public class EntrySet extends IBusiness {

	public void execute() throws GeneralException {
		
		String temp=(String) this.getFormHM().get("salaryid");
		VoucherBo bo = new VoucherBo(this.frameconn,this.userView);
		if(temp==null|| "".equals(temp)){
	        HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
	        String id=(String) map.get("salaryId");
	        String zhib=(String) map.get("zhib");
	        String[] salaryid=id.split(",");
			ArrayList list=bo.getAllItemList(salaryid[0],"other");//分录汇总指标，需要排除数据类型	
			ArrayList list1=bo.getSalaryList(id);
			ArrayList selectedFieldList = new ArrayList();
			if(zhib!=null&&!"".equals(zhib)&&!"null".equals(zhib))
			selectedFieldList=bo.getRightList(zhib);
			this.getFormHM().put("leftList", list);
			this.getFormHM().put("rightList", selectedFieldList);
			this.getFormHM().put("salaryList", list1);
		}else{
			ArrayList list=bo.getAllItemList(temp,"other");	//分录汇总指标，需要排除数据类型
			this.getFormHM().put("leftList", list);
		}

	}

}
