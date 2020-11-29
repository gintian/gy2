package com.hjsj.hrms.transaction.general.sprelationmap.yfilesmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchReportDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		HashMap reportData = new HashMap();
		
		String a_code = (String)this.formHM.get("a_code");
		a_code = a_code == null || a_code.trim().length() == 0?"":a_code;
		RelationMapBo bo=new RelationMapBo(this.getFrameconn(),this.userView);
		bo.chartParam = bo.getChartParameter();
		 
		ContentDAO dao = new ContentDAO(frameconn);
		bo.resetDescAndHintList();
		ArrayList toolTextList = bo.getToolTextList(bo.chartParam.getDesc_items(),"desc");
		ArrayList hintTextList = bo.getToolTextList(bo.chartParam.getHint_items(), "hint");
		
		String currentId = this.userView.getDbname()+this.userView.getA0100();
		//获取登录人的主关系
		Map currentRelationMap = new HashMap();
		//登录人放入currentRelationMap
		if(this.userView.isAdmin())
			currentRelationMap.put("isAdmin","1");
		else
		{
			currentRelationMap.put(currentId.toUpperCase(), currentId.toUpperCase());
			bo.getCurrentRelationMap(currentId,currentRelationMap);
		}
		
		ArrayList relationList = bo.getRelationList(a_code);
		
		if(a_code.length()<1){
			for(int i=0;i<relationList.size();i++){
				LazyDynaBean rldb = (LazyDynaBean)relationList.get(i);
				String relationId = (String)rldb.get("relation_id");
				ArrayList alist = new ArrayList();
				ArrayList childIdArr = bo.getRelationAllChild(relationId);
				for(int b=0;b<childIdArr.size();b++){
					String chId = childIdArr.get(b).toString();
					LazyDynaBean chBean = bo.getBaseInfo(chId,"",toolTextList,hintTextList);
					if(chBean==null)
						continue;
					alist.add(chBean);
				}
				
				rldb.set("alist", alist);
			}
		}else{
		
			LazyDynaBean ldb = bo.getBaseInfo(a_code,"",toolTextList,hintTextList);
			reportData.put("selectObj", ldb);
			
			for(int i=0;i<relationList.size();i++){
				LazyDynaBean rldb = (LazyDynaBean)relationList.get(i);
				String relationId = (String)rldb.get("relation_id");
				String currCode = (String)rldb.get("a_code");
				HashMap amap = new HashMap();
				ArrayList alist = new ArrayList();
				bo.doMethod(dao, currCode, relationId, alist,toolTextList,hintTextList,amap,1,currentRelationMap);
				rldb.set("alist",alist);
			}
		}
		reportData.put("relationList", relationList);
		
		this.getFormHM().put("reportData", reportData);
	}

	
	
	
	
	
	
	
	
	
}
