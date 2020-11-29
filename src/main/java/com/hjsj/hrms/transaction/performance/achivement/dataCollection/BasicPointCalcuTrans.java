package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:BasicPointCalcuTrans.java</p>
 * <p>Description:业绩数据采集基本指标计算</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-17 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class BasicPointCalcuTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
		String planId = (String)this.getFormHM().get("planId");
		String point = (String)this.getFormHM().get("point");
		
		String pratical = (String)this.getFormHM().get("pratical"); // 实际值
		String basic = (String)this.getFormHM().get("basic");       // 基本分
		String standard = (String)this.getFormHM().get("standard");	// 标准值
		
		String objectId = (String)this.getFormHM().get("objectId");
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(),planId,point,this.getUserView());
		//基本型指标简单和分段计算规则的数据计算
		HashMap map = bo.basciPointCalcu(pratical,basic,standard);	  
		
		String addF = (String)map.get("addF");
		String deducF = (String)map.get("deducF");  
		String objDF = (String)map.get("objDF");  
		
		this.getFormHM().put("objectId", objectId);
		this.getFormHM().put("objAdd", addF);
		this.getFormHM().put("objRedu", deducF);
		this.getFormHM().put("objDF", objDF);	
    }
}
