package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hjsj.hrms.businessobject.general.deci.definition.StatCutlineBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchStatCutlineTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String object=(String)this.getFormHM().get("object");
		String typeid=(String)this.getFormHM().get("typeid");	
		
		StatCutlineBo statCutlineBo=new StatCutlineBo(this.getFrameconn());
		
		ArrayList typeList=statCutlineBo.getTypeList();     //得到指标图例分类
		ArrayList objectList=statCutlineBo.getObjectList(); //得到对象列表(信息群A/B/K)
		
		if(object==null||object.trim().length()==0){//得到默认信息群对象A
			object=statCutlineBo.getFirstInfoId(objectList);
		}
		if(typeid==null||typeid.trim().length()==0){//得到默认图例类别对象
			typeid=statCutlineBo.getFirstInfoId(typeList);
		}
		
		ArrayList statCutlinelist=statCutlineBo.getStatCutlineList(object,typeid);   ////得到关键指标图例信息集合

		this.getFormHM().put("object",object); //信息群
		this.getFormHM().put("typeid",typeid); //图例类
		this.getFormHM().put("statCutlinelist",statCutlinelist); //当前关键指标图例信息集合
		this.getFormHM().put("typeList",typeList); // 图例类别集合
		this.getFormHM().put("objectList",objectList); //信息群集合
		
	}

	
	

	
	

}

