package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class InitItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String relationType=(String)this.getFormHM().get("relationType");
			if(map!=null&&map.get("opt")!=null&& "init".equalsIgnoreCase((String)map.get("opt"))){
				map.remove("opt");
				String items=(String)map.get("items");
				RelationMapBo rmb = new RelationMapBo(this.getFrameconn(),this.getUserView(),relationType);
				ArrayList selectedList = rmb.getSelectedList(items);
				String inforkind=(String)map.get("kind");
				ArrayList fieldSetList=rmb.getFieldSetList(inforkind);
				String fieldSetid="";
				if(fieldSetList.size()>0){
					fieldSetid=((CommonData)fieldSetList.get(0)).getDataValue();
				}
				ArrayList fieldList = rmb.getItemList(fieldSetid);
				this.getFormHM().put("selectedFieldList", selectedList);
				this.getFormHM().put("fieldList", fieldList);
				this.getFormHM().put("fieldSetList", fieldSetList);
				this.getFormHM().put("relationType", relationType);
			}else{
				RelationMapBo rmb = new RelationMapBo(this.getFrameconn(),this.getUserView(),relationType);
				String fieldsetid=(String)this.getFormHM().get("fieldsetid");
				ArrayList fieldList = rmb.getItemList(fieldsetid);
				this.getFormHM().put("fieldList", fieldList);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	

}
