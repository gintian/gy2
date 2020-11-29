package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceReportDefBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PieceRateTjSetCompleteTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
	        HashMap requestMap =(HashMap)this.getFormHM().get("requestPamaHM");	
	        String bClose=(String)requestMap.get("bClose");
	        if (bClose!=null){
	        	this.getFormHM().put("needClose", "true");
	        	requestMap.remove("bClose");
	        	//提交
	        	String model= (String) this.getFormHM().get("model");
	        	PieceReportDefBo defBo =new PieceReportDefBo(this.frameconn);
	        	String value="";
	        	value=(String)this.getFormHM().get("reportName");
	        	value=PubFunc.keyWord_reback(value);
	        	value = value.replaceAll("'", "").replaceAll(",", "");
	        	value = value.replaceAll("\"", "");
		        defBo.setReportName(value);	   
		        this.getFormHM().put("reportName",value);
	        	value=(String)this.getFormHM().get("reportKind");
	        	defBo.setReportKind(value);	        	
	        	value=(String)this.getFormHM().get("tjWhere");
	        	defBo.setCondClause(value);  
	        	value=(String)this.getFormHM().get("useGroup");
	        	if ("1".equals(value))
	        		defBo.setGroup(true);
	        	else
	        		defBo.setGroup(false);
	        	value=(String)this.getFormHM().get("rightFields");
	        	defBo.setShowFields(value); 	        	
	        	defBo.setGroupFields((String)this.getFormHM().get("groupFlds"));
	        	defBo.setOrderFields((String) this.getFormHM().get("orderFlds"));
	        	defBo.setSummaryFlds((String)this.getFormHM().get("summaryFlds"));        	
	        	defBo.setSummaryMap((HashMap)this.getFormHM().get("summaryMapFlds"));        	
	        	
	            if ("edit".equals(model)){
	            	String reportId = (String) this.getFormHM().get("reportId");
	            	defBo.setReportId(Integer.parseInt(reportId));
	            	defBo.saveReport();
	 
	            }      
	            else {
	            	defBo.newReport();
	            	this.getFormHM().put("reportId", String.valueOf(defBo.getReportId()));
	            	this.getFormHM().put("reportSortId", String.valueOf(defBo.getReportSortId()));
	            }
	            
	        	
	        	
	        }
	        else {
	        	String orderFlds = (String)this.getFormHM().get("orderFlds");	
	        	
	        	ArrayList selectedList = (ArrayList)this.getFormHM().get("selectedFieldList");
	        	String useGroup = (String)this.getFormHM().get("useGroup");
				String groupFlds = (String)this.getFormHM().get("groupFlds");
				String temporderFlds = (String)this.getFormHM().get("orderFlds")+",";
				
				ArrayList list = new ArrayList();
				ArrayList OrderList= new ArrayList();
				HashMap orderMapFlds = new HashMap();
			    String[] arrValue= temporderFlds.split(",");		  
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
	        	this.getFormHM().put("orderFlds", orderFlds);
	        	this.getFormHM().put("taskTypeList", GetTypeList());	        	
	        }
	
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}

	}
	
	private ArrayList GetTypeList(){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList fieldList = new ArrayList();
		String sqlstr = "select * from codeitem where codesetid ='71' and invalid =1 order by codeitemid";
		ArrayList dylist = null;
		CommonData dataobj =null;
		dataobj=new CommonData("","");
		fieldList.add(dataobj);
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("codeitemid").toString();
				String itemdesc = dynabean.get("codeitemdesc").toString();
				dataobj = new CommonData(itemid,itemdesc);
				fieldList.add(dataobj);
			}
		} catch(GeneralException e) {
			e.printStackTrace();
		}
		return fieldList;
	}	

}
