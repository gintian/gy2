package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportOperationFormulaAnalyse;
import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * <p>Title:</p>
 * <p>Description:直属单位汇总&&所有基层汇总</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 13, 2006:5:46:38 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class DirectUnitCollectTrans extends IBusiness {

	public void execute() throws GeneralException {
		String unitcode=(String)this.getFormHM().get("unitcode");		
		ArrayList unitlist=(ArrayList)this.getFormHM().get("unitcodeList");
		ArrayList tablist=(ArrayList)this.getFormHM().get("tabidList");
		String    operate=(String)this.getFormHM().get("operate");
		ArrayList noLeafUnitList=new ArrayList();
		if("3".equals(operate))
		{
			TTorganization ttorganization=new TTorganization(this.getFrameconn());
			ArrayList unitcodeList=ttorganization.getGrassRootsUnit((String)this.getFormHM().get("unitcode"));
			unitlist=new ArrayList();
			RecordVo vo=null;
			for(Iterator t=unitcodeList.iterator();t.hasNext();)
			{
				vo=(RecordVo)t.next();	
				unitlist.add(vo.getString("unitcode")+"§"+vo.getString("unitname"));
				noLeafUnitList.add(vo.getString("unitcode"));
			}
		}
		
	
		ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
		ArrayList  tabidList=new ArrayList();
		String info="success";
		if("7".equals(operate))//逐层汇总
		{
			if(!reportCollectBo.layerCollect(unitcode,tablist,noLeafUnitList))
				info="failed";
			
		}
		else if("4".equals(operate)) //直属汇总-基层汇总比较
		{
			if(!reportCollectBo.compareCollect(unitcode,tablist))
				info="failed";
			
		}
		else	
		{
			if(!reportCollectBo.collectReport(unitcode,tablist,unitlist))
				info="failed";
		}
		ArrayList formulaList=new ArrayList();
		//计算汇总公式
		if(!"4".equals(operate))
		{
			for(Iterator t=tablist.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				String[] tt=temp.split("§");
				tabidList.add(tt[0]);
			}
			
			if(!"7".equals(operate)&&!"3".equals(operate))
			{
				for(int i=0;i<unitlist.size();i++)
				{
					String temp=(String)unitlist.get(i);
					String[] tt=temp.split("§");
					noLeafUnitList.add(tt[0]);
				}
				
			}
			
			if("7".equals(operate)){

					TTorganization ttOrganization=new TTorganization(this.getFrameconn());
					ArrayList LeafUnitList = new ArrayList();
					LeafUnitList =ttOrganization.getAllCollect(LeafUnitList);
				for(int j=0;j<LeafUnitList.size();j++){	
					unitcode =""+LeafUnitList.get(j);
					noLeafUnitList=ttOrganization.getUnderUnit(unitcode);
				ReportOperationFormulaAnalyse reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),noLeafUnitList);
				reportOperationFormulaAnalyse.setUnitCode(unitcode);
				reportOperationFormulaAnalyse.setOperate(operate);
				for(int i=0;i<tabidList.size();i++)
				{
					String a_temp=(String)tabidList.get(i);
					String a_formulaInfo=reportOperationFormulaAnalyse.reportCollectFormulaAnalyse(a_temp);
					//公式描述@错误信息描述#公式描述@错误信息描述
					if(!"null".equals(a_formulaInfo))
						formulaList.add(a_formulaInfo);
				}
				}
				
			}else{
			ReportOperationFormulaAnalyse reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),noLeafUnitList);
			reportOperationFormulaAnalyse.setUnitCode(unitcode);
			reportOperationFormulaAnalyse.setOperate(operate);
			for(int i=0;i<tabidList.size();i++)
			{
				String a_temp=(String)tabidList.get(i);
				String a_formulaInfo=reportOperationFormulaAnalyse.reportCollectFormulaAnalyse(a_temp);
				//公式描述@错误信息描述#公式描述@错误信息描述
				if(!"null".equals(a_formulaInfo))
					formulaList.add(a_formulaInfo);
			}
			}
		}
		this.getFormHM().clear();		
		this.getFormHM().put("info",info);
		this.getFormHM().put("formulaInfo",formulaList);
		
		
	}

}
