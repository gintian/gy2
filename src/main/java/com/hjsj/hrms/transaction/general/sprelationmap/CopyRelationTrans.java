package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CopyRelationTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.getUserView());
			String from =(String)this.getFormHM().get("from");
			String to=(String)this.getFormHM().get("to");
			String object_ids=(String)this.getFormHM().get("object_ids");
			String relation_id=(String)this.getFormHM().get("relation_id");
			String isClear=(String)this.getFormHM().get("isClear");
			String mess=bo.copyRelation(from, to, object_ids, relation_id, isClear);
			if("1".equals(isClear)){
				//ArrayList spRelationList=bo.spRelationList(from);
				//String relationid="-1";
				//if(spRelationList.size()>0)
					//relationid=((CommonData)spRelationList.get(0)).getDataValue();
				ArrayList downPersonList = bo.getDownPersonList(relation_id, from);
				//this.getFormHM().put("spRelationList", spRelationList);
				this.getFormHM().put("downPersonList", downPersonList);
			}
			this.getFormHM().put("isClear", isClear);
			this.getFormHM().put("mess",mess);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}
