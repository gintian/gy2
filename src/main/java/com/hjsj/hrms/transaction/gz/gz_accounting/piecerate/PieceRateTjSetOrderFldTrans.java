package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class PieceRateTjSetOrderFldTrans extends IBusiness {
	public void execute() throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			ArrayList selectedList = (ArrayList)this.getFormHM().get("selectedFieldList");
			String useGroup = (String)this.getFormHM().get("useGroup");
			String groupFlds = (String)this.getFormHM().get("groupFlds");
			String summaryFlds = (String)this.getFormHM().get("summaryFlds");
			String tjWhere = (String)this.getFormHM().get("tjWhere");
			tjWhere=PubFunc.keyWord_reback(tjWhere); 
			String orderFlds = (String)this.getFormHM().get("orderFlds")+",";
			
			if ("1".equals(useGroup)){//检查分组之外的指标是否设置了汇总,检查分组指标是否设置了汇总
				for (int i=0;i<selectedList.size();i++)	{
					CommonData cData = (CommonData)selectedList.get(i);		
					if ((","+groupFlds+",").toUpperCase().indexOf(","+cData.getDataValue().toUpperCase()+",")<0){
						if ((summaryFlds).toUpperCase().indexOf(""+cData.getDataValue().toUpperCase()+":")<0){								
							summaryFlds=summaryFlds+","+cData.getDataValue()+":count";
						}
					}
				}	
				ArrayList summaryList= new ArrayList();
				String tempSummary="";
			    String[] arrValue= summaryFlds.split(",");		  
				for (int i=0;i<arrValue.length;i++){
					String[] arrValue1= arrValue[i].split(":");
					if (arrValue1.length>1){
						if ((","+groupFlds+",").toUpperCase().indexOf(","+arrValue1[0].toUpperCase()+",")<0){
							tempSummary=tempSummary+arrValue[i]+','	;	
						}

					}
				}
				summaryFlds=tempSummary;
			}
			

			
			ArrayList list = new ArrayList();
			ArrayList OrderList= new ArrayList();
			HashMap orderMapFlds = new HashMap();
		    String[] arrValue= orderFlds.split(",");		  
			for (int i=0;i<arrValue.length;i++){
				String[] arrValue1= arrValue[i].split(":");
				if (arrValue1.length>1){
					orderMapFlds.put(arrValue1[0],arrValue1[1]); 	
					for (int j=0;j<selectedList.size();j++){
						CommonData cData = (CommonData)selectedList.get(j);
						if (arrValue1[0].equals(cData.getDataValue())){
							OrderList.add(cData);
							break;
							
						}
					}
				}
			}
			
			LazyDynaBean bean = null;
			for (int i=0;i<selectedList.size();i++)	{
				CommonData cData = (CommonData)selectedList.get(i);
				if (orderMapFlds.get(cData.getDataValue())!=null)
					continue;
				OrderList.add(cData);
			}
			
			
			for (int i=0;i<OrderList.size();i++)	{
				CommonData cData = (CommonData)OrderList.get(i);
				if ("1".equals(useGroup)){
					if ((","+groupFlds+",").toUpperCase().indexOf(","+cData.getDataValue().toUpperCase()+",")<0){
						continue;
					}
				}
				bean = new LazyDynaBean();
				bean.set("itemid", cData.getDataValue());
				bean.set("itemname", cData.getDataName());
				String ordertype="";
				if (orderMapFlds.get(cData.getDataValue())!=null)
					ordertype=(String)orderMapFlds.get(cData.getDataValue());	
				bean.set("ordertype", ordertype);
				list.add(bean);
			}

			this.getFormHM().put("orderFldList", list);
			this.getFormHM().put("groupFlds", groupFlds);
			this.getFormHM().put("summaryFlds", summaryFlds);
			this.getFormHM().put("tjWhere", tjWhere);
			

		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}

	}
}

