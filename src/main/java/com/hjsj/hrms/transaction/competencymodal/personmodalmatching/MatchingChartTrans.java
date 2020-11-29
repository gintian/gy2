package com.hjsj.hrms.transaction.competencymodal.personmodalmatching;

import com.hjsj.hrms.businessobject.competencymodal.PersonPostMatchingBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;



public class MatchingChartTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			
			PersonPostMatchingBo bo = new PersonPostMatchingBo(this.getFrameconn(),this.getUserView());
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id=(String)this.getFormHM().get("planId");
			String object_id=(String)this.getFormHM().get("object_id");
			String postCode = (String)this.getFormHM().get("postCode");
			String objType=(String) map.get("objType");  //2岗人匹配   1人岗
			HashMap dataMap =new HashMap();
			String isShowPercentVal = (String)this.getFormHM().get("isShowPercentVal");//是否按百分比显示分值  0不按百分比，1按百分比，2按等级
			isShowPercentVal = (isShowPercentVal==null || isShowPercentVal!=null && isShowPercentVal.length()==0)?"0":isShowPercentVal;
			String byModel="";
			String sql="select * from per_plan where plan_id='"+plan_id+"'";
			ContentDAO dao=new ContentDAO(this.frameconn);
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				byModel=this.frowset.getString("byModel");
			}
			if("1".equals(byModel)){
				if("1".equals(objType)){
					dataMap = bo.getDataMapByModel1(object_id, postCode, plan_id, isShowPercentVal);//人岗匹配
				}else if("2".equals(objType)){
					dataMap = bo.getDataMapByModel2(object_id, postCode, plan_id, isShowPercentVal);//岗人匹配
				}
				
			}else{
				dataMap =  bo.getDataMap(object_id, postCode, plan_id, isShowPercentVal);
			}
			
			ChartParameter chartParam=new ChartParameter();
			chartParam.setChartTitle("");
			chartParam.setLineNodeIsMarked(1);
			this.getFormHM().put("chartParam", chartParam);
			this.getFormHM().put("dataMap", dataMap);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
