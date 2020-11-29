package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceReportDefBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class PieceRateTjSetSummaryFldTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {

			ArrayList list = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap requestMap =(HashMap)this.getFormHM().get("requestPamaHM");
			String summaryFlds = (String)requestMap.get("summaryFlds");
			if (summaryFlds==null) summaryFlds="";
			String groupFlds = (String)requestMap.get("groupFlds");
			if (groupFlds==null) groupFlds="";
			groupFlds=","+groupFlds.toUpperCase()+",";
			
			ArrayList selectedList = (ArrayList)this.getFormHM().get("selectedFieldList");

			HashMap summaryMapFlds = new HashMap();
		    String[] arrValue= summaryFlds.split(",");		  
			for (int i=0;i<arrValue.length;i++){
				String[] arrValue1= arrValue[i].split(":");
				if (arrValue1.length>1){
					summaryMapFlds.put(arrValue1[0],arrValue1[1]); 	
				}
				  
			}
			
			LazyDynaBean bean = null;
			for (int i=0;i<selectedList.size();i++)	{
				CommonData cData = (CommonData)selectedList.get(i);
				if (groupFlds.indexOf(","+cData.getDataValue().toUpperCase()+",")>-1) continue;
				bean = new LazyDynaBean();
				bean.set("itemid", cData.getDataValue());
				bean.set("itemname", cData.getDataName());
				String ctype="count";
				if (summaryMapFlds.get(cData.getDataValue())!=null)
					ctype=(String)summaryMapFlds.get(cData.getDataValue());				
				bean.set("summarytype", ctype);
				
				ctype="A";
				FieldItem item =PieceReportDefBo.getLocalFieldItem(cData.getDataValue());				
				if (item !=null){
					ctype= item.getItemtype();
				}
				if ("S0402".equalsIgnoreCase(cData.getDataValue())
					       || "S0401".equalsIgnoreCase(cData.getDataValue())){
					ctype="A";
				}	
				bean.set("fldtype", ctype);
				list.add(bean);
			}

			this.getFormHM().put("summaryFldList", list);


		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}

	}

}
