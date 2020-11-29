package com.hjsj.hrms.transaction.competencymodal.personmodalmatching;

import com.hjsj.hrms.businessobject.competencymodal.PersonPostMatchingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;



public class InitMatchingTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String objType=(String)map.get("objType"); //  2:岗人匹配 or 1:人岗匹配 			
			String plan_id=(String)map.get("plan_id");
			String objE01A1=(String)map.get("objE01A1"); //  考核对象岗位编号			
			String object_id=(String)map.get("object_id");
			//add by wangchaoqun on 2014-9-10 begin
			String returnURL = (String)map.get("returnURL");
			returnURL = PubFunc.keyWord_reback(returnURL);
			//add by wangchaoqun on 2014-9-10 end
			PersonPostMatchingBo bo = new PersonPostMatchingBo(this.getFrameconn(),this.getUserView());
			ArrayList macthingDegreeList=bo.getAllGeadeClass();
			String degreeid=bo.getGradeClass(plan_id);
			String degreeGradeId="";
			String postCode="";
			if(degreeid==null|| "".equals(degreeid)|| "#".equals(degreeid))
			{
				if(macthingDegreeList.size()>0)
					degreeid=((CommonData)macthingDegreeList.get(0)).getDataValue();
			}
			ArrayList matchingList=bo.getMatchingList(object_id, plan_id, "", degreeid,degreeGradeId,objType,objE01A1);
			ArrayList degreeGradeList = bo.getDegreeDetailInfo(degreeid, 1);
			String degreeflag = bo.getDegreeFlag(degreeid);//等级标识
			if(matchingList.size()>0)
			{
				String post_id=bo.getE01a1(object_id);
				postCode=post_id;
				if(objType!=null && objType.trim().length()>0 && "2".equals(objType))
					postCode=objE01A1;
			}
			
			this.getFormHM().put("objE01A1",objE01A1);
			this.getFormHM().put("objType",objType);
			this.getFormHM().put("object_id", object_id);
			this.getFormHM().put("planId",plan_id);
			this.getFormHM().put("matchingDegree", degreeid);
			this.getFormHM().put("macthingDegreeList", macthingDegreeList);
			this.getFormHM().put("postScope", "");
			this.getFormHM().put("postScopeDesc", "");
			this.getFormHM().put("postCode", postCode);
			this.getFormHM().put("matchingList", matchingList);
			this.getFormHM().put("degreeflag", degreeflag);
			this.getFormHM().put("degreeGradeId", degreeGradeId);
			this.getFormHM().put("degreeGradeList", degreeGradeList);
			this.getFormHM().put("returnURL", returnURL);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
