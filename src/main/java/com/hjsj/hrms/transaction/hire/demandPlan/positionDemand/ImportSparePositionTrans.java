package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * <p>Title:ImportSparePositionTrans.java</p>
 * <p>Description:引入空闲职位</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 21, 2006 3:33:27 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class ImportSparePositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String[] sparePositionIDs=(String[])this.getFormHM().get("sparePositionIDs");		
		HashMap  sparePositionMap=(HashMap)this.getFormHM().get("sparePositionMap");
		PositionDemand positionDemand=new PositionDemand(this.getFrameconn());
		positionDemand.InsertSparePosition(sparePositionMap,sparePositionIDs,this.getUserView());
	
	}

}
