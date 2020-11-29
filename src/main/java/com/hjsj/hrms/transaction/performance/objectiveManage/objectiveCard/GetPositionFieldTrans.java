package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ImportDeptFieldBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:GetPositionFieldTrans.java</p>
 * <p>Description>:GetPositionFieldTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 11, 2009 4:27:12 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GetPositionFieldTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String itemid=(String)map.get("item_id");
			String object_id=(String)map.get("object_id");
			String plan_id=(String)map.get("plan_id");
		    plan_id=PubFunc.decryption(plan_id);
		    object_id=PubFunc.decryption(object_id);
			String body_id=(String)map.get("body_id");
			String model=(String)map.get("model");
			String importType=(String)this.getFormHM().get("importType");//=position导入岗位职责指标，=dept导入部门职责指标
			if("position".equalsIgnoreCase(importType)){
				ObjectCardBo bo = new ObjectCardBo(this.getFrameconn(),this.userView,plan_id);
				String positionID=bo.getObjectPositionID(object_id, "USR");
				String i9999=bo.getSelectedPositionField(itemid, object_id);
				ArrayList list = bo.getPositionFieldList(itemid, positionID,i9999,2);
				ArrayList dataList=(ArrayList)list.get(0);
				ArrayList headList = (ArrayList)list.get(1);
				String tableWidth=(String)list.get(2);
				String isHaveRecord=(String)list.get(3);
			    String alertMessage=(String)list.get(4);
				this.getFormHM().put("positionField", dataList);
				this.getFormHM().put("headList", headList);
				this.getFormHM().put("itemid", itemid);
				this.getFormHM().put("positionID", positionID);
				this.getFormHM().put("tableWidth", tableWidth);
				this.getFormHM().put("model",model);  // 1:团对  2:我的目标   3:目标制订  4.目标评估
				this.getFormHM().put("body_id",body_id);
				this.getFormHM().put("isHaveRecord", isHaveRecord);
				this.getFormHM().put("alertMessage", alertMessage);
				this.getFormHM().put("importType", importType);
			}else{
				ImportDeptFieldBo bo = new ImportDeptFieldBo(getFrameconn(), getUserView(), plan_id);
				String deptID=bo.getDeptString(object_id, "USR");
				HashMap i9999=bo.getSelectedDeptField(itemid, object_id,1,"","");
				
				ArrayList list = bo.getDeptFieldList(itemid, i9999,deptID, 2);
				ArrayList dataList=(ArrayList)list.get(0);
				ArrayList headList = (ArrayList)list.get(1);
				String tableWidth=(String)list.get(2);
				String isHaveRecord=(String)list.get(3);
			    String alertMessage=(String)list.get(4);
				this.getFormHM().put("positionField", dataList);
				this.getFormHM().put("headList", headList);
				this.getFormHM().put("itemid", itemid);
				this.getFormHM().put("positionID", "");
				this.getFormHM().put("tableWidth", tableWidth);
				this.getFormHM().put("model",model);  // 1:团对  2:我的目标   3:目标制订  4.目标评估
				this.getFormHM().put("body_id",body_id);
				this.getFormHM().put("isHaveRecord", isHaveRecord);
				this.getFormHM().put("alertMessage", alertMessage);
				this.getFormHM().put("importType", importType);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
