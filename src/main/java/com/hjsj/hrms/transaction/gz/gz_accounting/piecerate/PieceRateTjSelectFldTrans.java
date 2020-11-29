package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceReportDefBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class PieceRateTjSelectFldTrans extends IBusiness {

	public void execute() throws GeneralException {	
		this.getFormHM().put("needClose","false");
		String	setId="S01";
        HashMap requestMap =(HashMap)this.getFormHM().get("requestPamaHM");
        
        String defId="-1";
        String model= (String) requestMap.get("model");
        if ((model==null)|| "".equals(model)) model="add";
        this.getFormHM().put("model",model);
        if ("edit".equals(model)){
        	defId= (String) requestMap.get("defid");
            if ((defId==null)|| "".equals(defId)) defId="-1";
            this.getFormHM().put("reportId",defId);
        }     
        
        
		PieceReportDefBo defBo =null;
		if ("edit".equals(model)){
			defBo =new PieceReportDefBo(this.frameconn,Integer.parseInt(defId));	
			this.getFormHM().put("selectedFieldList",defBo.getSelectedFiledList(defBo.getShowFields()));
			
			if (defBo.isGroup())
				  this.getFormHM().put("useGroup","1");
			else 
				this.getFormHM().put("useGroup","0");	
			this.getFormHM().put("tjWhere",defBo.getCondClause());
			this.getFormHM().put("groupFlds",defBo.getGroupFields());
			this.getFormHM().put("summaryFlds",defBo.getSummaryFlds());
			this.getFormHM().put("summaryMapFlds",(HashMap)defBo.getSummaryMap());
			this.getFormHM().put("orderFlds",defBo.getOrderFields());
			//this.getFormHM().put("orderMapFlds",(HashMap)defBo.getOrderMap());
			
			this.getFormHM().put("reportKind",defBo.getReportKind());
			this.getFormHM().put("reportName",defBo.getReportName());
			
			
			
		}
		else {
			defBo =new PieceReportDefBo(this.frameconn);
			this.getFormHM().put("selectedFieldList",new ArrayList());
			this.getFormHM().put("useGroup","0");	
			this.getFormHM().put("tjWhere","");
			this.getFormHM().put("groupFlds","");
			this.getFormHM().put("summaryFlds","");
			this.getFormHM().put("summaryMapFlds",new HashMap());
			this.getFormHM().put("orderFlds","");
			//this.getFormHM().put("orderMapFlds",(HashMap)defBo.getOrderMap());
			
			this.getFormHM().put("reportKind","");
			this.getFormHM().put("reportName","");
		}		
		
		this.getFormHM().put("setId",setId);
		this.getFormHM().put("setList",defBo.getSetList());		
		this.getFormHM().put("leftFieldList",defBo.getLeftFieldList(setId));

	}
}
