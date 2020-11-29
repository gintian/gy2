package com.hjsj.hrms.transaction.competencymodal.personmodalmatching;

import com.hjsj.hrms.businessobject.competencymodal.PersonPostMatchingBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;



public class QueryMatchingTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String oper=(String)map.get("oper");
			String objType=(String)this.getFormHM().get("objType"); //  1:岗人匹配 or 2:人岗匹配 
			String objE01A1=(String)this.getFormHM().get("objE01A1"); //  岗位编号 
			String plan_id=(String)this.getFormHM().get("planId");
			String object_id=(String)this.getFormHM().get("object_id");
			PersonPostMatchingBo bo = new PersonPostMatchingBo(this.getFrameconn(),this.getUserView());
			ArrayList macthingDegreeList=bo.getAllGeadeClass();
			String matchingDegree=(String)this.getFormHM().get("matchingDegree");
			String postCode = (String)this.getFormHM().get("postCode");
			String postScope = (String)this.getFormHM().get("postScope");
			String postScopeDesc=(String)this.getFormHM().get("postScopeDesc");
			String degreeGradeId=(String)this.getFormHM().get("degreeGradeId");
			ArrayList matchingList=bo.getMatchingList(object_id, plan_id, postScope, matchingDegree,degreeGradeId,objType,objE01A1);
			ArrayList degreeGradeList = bo.getDegreeDetailInfo(matchingDegree, 1);
			if(matchingList.size()>0&& "1".equals(oper))
			{
				LazyDynaBean bean=(LazyDynaBean)matchingList.get(0);
				String post_id=(String)bean.get("codeitemid");
				postCode=post_id;
				if(objType!=null && objType.trim().length()>0 && "2".equals(objType))
					postCode=objE01A1;
			}else{
	
			}
			
			this.getFormHM().put("objE01A1",objE01A1);
			this.getFormHM().put("objType",objType);
			this.getFormHM().put("object_id", object_id);
			this.getFormHM().put("planId",plan_id);
			this.getFormHM().put("matchingDegree", matchingDegree);
			this.getFormHM().put("macthingDegreeList", macthingDegreeList);
			this.getFormHM().put("postScope", postScope);
			this.getFormHM().put("postScopeDesc", postScopeDesc);
			this.getFormHM().put("postCode",postCode);
			this.getFormHM().put("matchingList", matchingList);
			this.getFormHM().put("degreeGradeId", degreeGradeId);
			this.getFormHM().put("degreeGradeList", degreeGradeList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
