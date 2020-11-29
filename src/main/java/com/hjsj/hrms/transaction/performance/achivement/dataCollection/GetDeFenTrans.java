package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:GetDeFenTrans.java</p>
 * <p>Description:数据采集计算得分</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-17 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */  

public class GetDeFenTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		String planId = (String)this.getFormHM().get("planId");
		String point = (String)this.getFormHM().get("point");
		String scoreStr = (String)this.getFormHM().get("scoreStr");
		scoreStr = PubFunc.keyWord_reback(scoreStr);		
		String objectId = (String)this.getFormHM().get("objectId");
		String[] scoreArray = scoreStr.split("<@>");
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(),planId,point,this.getUserView());
		HashMap map = bo.computDF(objectId,scoreArray);
		String dfscore = (String)map.get("df");
		String cz = (String)map.get("cz");	
		
		this.getFormHM().put("objectId", objectId);
		this.getFormHM().put("objDF", dfscore);
		this.getFormHM().put("cz", cz);
		
    }
    
}
