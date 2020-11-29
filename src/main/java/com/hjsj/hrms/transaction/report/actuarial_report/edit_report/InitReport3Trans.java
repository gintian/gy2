package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:初始化表3</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 7, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class InitReport3Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			ArrayList u03DataList=new ArrayList();
			ArrayList compareDataList=new ArrayList();  //比较数据
			ArrayList dataHeadList=new ArrayList();
			String opt=(String)hm.get("opt");   //1:可操作  0：只读 
			String from_model=(String)hm.get("from_model");
			hm.remove("from_model");
			String id=(String)hm.get("id");
			String unitcode=(String)hm.get("unitcode");
			String reportStatus="-1";
			String t3_desc="";
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			dataHeadList=ab.getDataHeadList_U03();
			u03DataList=ab.getU03DataList(id,unitcode,dataHeadList,"");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(id));	
			vo=dao.findByPrimaryKey(vo);
			String kmethod=String.valueOf(vo.getInt("kmethod"));
			if(vo.getInt("kmethod")==0)
			{
				compareDataList=ab.getCompareDataList(id,unitcode,dataHeadList);
				this.frowset=dao.search("select * from u01 where id="+id+" and unitcode='"+unitcode+"'");
				if(this.frowset.next())
					t3_desc=Sql_switcher.readMemo(this.frowset,"t3_desc");  
			}
			
			
			this.frowset=dao.search("select flag from tt_calculation_ctrl  where unitcode='"+unitcode+"' and id="+id+" and report_id='U03'");
			if(this.frowset.next())
				reportStatus=this.frowset.getString("flag");
			
			this.getFormHM().put("flagSub",ab.isSub("U03", ab.getSelfUnitCode(), id, "1"));
			if("1".equals(ab.isRootUnit(this.getUserView().getUserName()))){
				this.getFormHM().put("rootUnit", "1");
			}else{
				this.getFormHM().put("rootUnit", "0");
			}
			this.getFormHM().put("kmethod",kmethod);
			this.getFormHM().put("reportStatus",reportStatus);
			this.getFormHM().put("isUnderUnit",ab.isUnderUnit(unitcode));
			this.getFormHM().put("isCollectUnit",ab.isCollectUnit(unitcode));
			
			this.getFormHM().put("cycleStatus", ab.getCycleStatus(id));
			this.getFormHM().put("dataHeadList", dataHeadList);
			this.getFormHM().put("u03DataList", u03DataList);
			this.getFormHM().put("compareDataList",compareDataList);
			this.getFormHM().put("opt",opt);
			this.getFormHM().put("id",id);
			this.getFormHM().put("selfUnitcode", ab.getSelfUnitCode());
			this.getFormHM().put("unitcode",unitcode);
			this.getFormHM().put("info","");
			this.getFormHM().put("t3_desc",t3_desc);
			this.getFormHM().put("from_model",from_model);
			this.getFormHM().put("report_id","U03");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
