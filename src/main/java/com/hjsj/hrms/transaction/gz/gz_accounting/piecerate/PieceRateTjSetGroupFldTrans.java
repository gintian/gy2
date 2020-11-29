package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class PieceRateTjSetGroupFldTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			ArrayList list = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap requestMap =(HashMap)this.getFormHM().get("requestPamaHM");
			String groupFlds = (String)requestMap.get("groupFlds");
			
			ArrayList selectedList = (ArrayList)this.getFormHM().get("selectedFieldList");
			if (groupFlds==null) groupFlds="";
			groupFlds=(","+groupFlds+",").toUpperCase();
			LazyDynaBean bean = null;
			for (int i=0;i<selectedList.size();i++)	{
				CommonData cData = (CommonData)selectedList.get(i);
				bean = new LazyDynaBean();
				bean.set("itemid", cData.getDataValue());
				bean.set("itemname", cData.getDataName());
				if (groupFlds.indexOf( ","+cData.getDataValue().toUpperCase()+",")>-1){
					bean.set("isselected", "1");
				}
				else{
					bean.set("isselected", "0");	
				}				
				list.add(bean);
			}

			this.getFormHM().put("groupFldList", list);


		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}

	}

}
