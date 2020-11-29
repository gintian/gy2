package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportOperationFormulaAnalyse;
import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * <p>Title:</p>
 * <p>Description: </p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 23, 2006:12:05:02 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class SubUnitCollectTrans extends IBusiness {

	public void execute() throws GeneralException {
		String unitcode=(String)this.getFormHM().get("unitcode");
		String tabid=(String)this.getFormHM().get("tabid");
		
		TTorganization ttorganization=new TTorganization(this.getFrameconn());
		
		String tsortid="";
		StringBuffer sql=new StringBuffer();
		sql.append("select tsortid from tname where tabid='"+tabid+"'");
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				tsortid=this.frowset.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ArrayList unitList=ttorganization.getUnderUnitList(unitcode, tsortid);  //得到直属单位
		if(unitList.size()>0)
		{
			ArrayList subUnitList=new ArrayList();
			ArrayList noLeafUnitList=new ArrayList();
			
			for(Iterator t=unitList.iterator();t.hasNext();)
			{
				DynaBean bean=(DynaBean)t.next();
				subUnitList.add((String)bean.get("unitcode")+"§"+(String)bean.get("unitname"));
				noLeafUnitList.add((String)bean.get("unitcode"));
			}
			ArrayList tabidList=new ArrayList();
			tabidList.add(tabid+"§"+tabid);
			
			ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
			reportCollectBo.collectReport(unitcode,tabidList,subUnitList); //直属单位汇总
			//走汇总公式
			ReportOperationFormulaAnalyse reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),noLeafUnitList);
			reportOperationFormulaAnalyse.setUnitCode(unitcode);
			reportOperationFormulaAnalyse.reportCollectFormulaAnalyse(tabid);
		}
	}

}
