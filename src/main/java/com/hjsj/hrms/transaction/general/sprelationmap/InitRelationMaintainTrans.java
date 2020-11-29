package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author lizw
 *
 */
public class InitRelationMaintainTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String currId=(String)this.getFormHM().get("currId");
			RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.getUserView());
			ArrayList upPersonList = bo.getUpPersonList(currId);
			ArrayList spRelationList=bo.spRelationList(currId);
			String relation_id="-1";
			if(spRelationList.size()>0)
				relation_id=((CommonData)spRelationList.get(0)).getDataValue();
			ArrayList downPersonList = bo.getDownPersonList(relation_id, currId);
			//在upPersonList中取出存放关系映射人的部门、单位、职位、姓名信息的Map数据形式如：'部门名/编号'、
			//如果集合有两个集合那么第一个为查询的人员下拉菜单信息，的二个为关系人部门、单位等信息如果是1个 那么第一是关系人信息
			Map approverInfoMap = new HashMap();
			if(upPersonList.size()==2){
				approverInfoMap = (Map)upPersonList.get(1);
				upPersonList.remove(1);
			}else{
				if(upPersonList.size()==1){
					approverInfoMap = (Map)upPersonList.get(0);
					upPersonList.remove(0);
				}
			}

			this.getFormHM().put("department",(String)approverInfoMap.get("department") );
			this.getFormHM().put("unit",(String)approverInfoMap.get("unit") );
			this.getFormHM().put("position",(String)approverInfoMap.get("position") );
			this.getFormHM().put("name",(String)approverInfoMap.get("name") );
			//清除upPersonList存储的关系映射信息 避免前台循环出空的option
			this.getFormHM().put("upPersonList", upPersonList);
			this.getFormHM().put("spRelationList", spRelationList);
			this.getFormHM().put("downPersonList",downPersonList);
			this.getFormHM().put("upId",currId);
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
