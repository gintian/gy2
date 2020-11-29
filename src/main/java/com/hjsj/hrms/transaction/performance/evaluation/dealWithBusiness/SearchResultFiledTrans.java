package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.ResultFiledBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchResultFiledTrans.java</p>
 * <p>Description:结果归档</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-28 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class SearchResultFiledTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planId = (String) hm.get("planid");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
		boolean _flag = _bo.isHavePriv(this.userView, planId);
		if(!_flag){
			return;
		}
		String busitype = (String) hm.get("busitype"); // 业务分类字段 =0(绩效考核); =1(能力素质)
		//	团队计划 有团队负责人时候 点击的按钮标志 1 试归档：显示试归档和结束 2 归档：显示归档和结束 
		String dispBt = (String) hm.get("dispBt");//按钮显示标志
		hm.remove("dispBt");
		if(dispBt==null)
			dispBt="all";
		String subSetName = (String) hm.get("subSetName");
		//归档类型　1--人员的归档 2--单位 部门 团队的归档 4--团队负责人的归档
		String filedType = (String) hm.get("filedType");
		hm.remove("filedType");	
		
		ResultFiledBo bo = new ResultFiledBo(planId,this.getFrameconn(),filedType);
		filedType=bo.getFiledType();
		
		bo.generateTempTable(busitype);//生成归档的临时表
		ArrayList list = bo.getSubSet();	//考核结果归档子集	
		this.getFormHM().put("subSet", list);	
		String setName = bo.getSetName();
	    if (subSetName!=null)
	    	setName=subSetName;
		this.getFormHM().put("setName", setName);
		list = bo.getPoints(setName,busitype);
		this.getFormHM().put("sourcePoints", list);
		this.getFormHM().put("filedType", filedType);
		this.getFormHM().put("dispBtFlag", dispBt);
		this.getFormHM().put("busitype", busitype);
		
    }

}
