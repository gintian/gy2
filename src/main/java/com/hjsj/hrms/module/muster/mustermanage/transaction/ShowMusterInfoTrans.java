package com.hjsj.hrms.module.muster.mustermanage.transaction;

import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;
/**
 * @Titile: ShowMusterInfoTrans
 * @Description:花名册新增初始化交易类
 * @Company:hjsj
 * @Create time: 2019年4月4日下午6:09:16
 * @author: Luzy
 * @version 1.0
 *
 */
public class ShowMusterInfoTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		MusterManageServiceImpl addmusterService = new MusterManageServiceImpl(this.frameconn,this.userView);
		try {
		    //1=人员库权限；2=备选树结构指标 ；3=数据范围的store ；4=查询花名册类型 ；5=返回查重数据；6=查询子集名称       8 = 获取当前子集的全部指标
			int operate = (Integer) this.getFormHM().get("operate");
			String musterType= (String) this.formHM.get("musterType");   //花名册类型
			String musterName= (String) this.formHM.get("musterName");   //花名册名称
			String tabid = (String) this.formHM.get("tabid");            //花名册id
			String FieldSet = (String) this.formHM.get("FieldSet");      //子集ID
			String moreRecordField = (String) this.formHM.get("data");
			String data = "";
			String itemRecord = (String) this.formHM.get("itemRecord");
			if(operate == 7) {
			    data = moreRecordField;
			}
			if (operate == 8) {
			    String fielditemid = (String) this.formHM.get("fielditemid"); //子集的一个指标
			    List arealist =addmusterService.getFielditemList(fielditemid);
			    List itemList =addmusterService.getItemList(fielditemid);
			    this.formHM.put("areaData", arealist);
			    this.formHM.put("itemData", itemList);
            }else {
                ArrayList list = addmusterService.addMusterInfo(operate,musterType,musterName,tabid,FieldSet,data);
                this.formHM.put("data", list);
            }
		}catch(Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
	}	
	
}

