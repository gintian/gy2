package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveReport3Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String sava_type=(String)hm.get("sava_type");  // 1：保存  2:提交
			String id=(String)this.getFormHM().get("id");
			String opt=(String)this.getFormHM().get("opt");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String current_values=(String)this.getFormHM().get("current_values");
			String t3_desc=(String)this.getFormHM().get("t3_desc");
			String info="1";
			
			ArrayList u03DataList=new ArrayList();
			ArrayList compareDataList=new ArrayList();  //表3比较数据
			ArrayList compareDataList_5=new ArrayList(); //表5比较数据
			
			
			ArrayList dataHeadList=new ArrayList();
			ArrayList dataHeadList_u05=new ArrayList();
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);
			if(t3_desc!=null&&t3_desc.trim().length()>0)
			{
				
				ab.saveDesc(unitcode,Integer.parseInt(id),"t3_desc",t3_desc);
			}
			else
				t3_desc="";
		
			
			ab.saveU03Values(unitcode,id,vo.getString("theyear"),current_values);	
			ab.saveU05Values(unitcode,id,vo.getString("theyear"));	
			 
			dataHeadList=ab.getDataHeadList_U03();
			dataHeadList_u05=ab.getDataHeadList_U05();
			u03DataList=ab.getU03DataList(id,unitcode,dataHeadList,"");
			String kmethod=String.valueOf(vo.getInt("kmethod"));
			if(vo.getInt("kmethod")==0)
			{
				compareDataList=ab.getCompareDataList(id,unitcode,dataHeadList);
				compareDataList_5=ab.getCompareDataList_u05(id,unitcode,vo.getString("theyear"),dataHeadList_u05);
			}
		
			
			if((compareDataList.size()>0||compareDataList_5.size()>0))
			{
				info=ab.checkCompareDataList(compareDataList,compareDataList_5,id,unitcode,dataHeadList,dataHeadList_u05);  // 1:成功 0：表3不成功 －1：表5不成功
			}
			
			int flag=0;
			ab.saveReportStatus(unitcode,"U03",id,flag);
			ab.saveReportStatus(unitcode,"U05",id,flag);
			this.getFormHM().put("kmethod",kmethod);
			this.getFormHM().put("reportStatus",String.valueOf(flag));
			this.getFormHM().put("dataHeadList", dataHeadList);
			this.getFormHM().put("u03DataList", u03DataList);
			this.getFormHM().put("compareDataList",compareDataList);
			this.getFormHM().put("t3_desc",t3_desc);
			this.getFormHM().put("info",info);
			
		}
		catch(Exception e)
		{
		
				e.printStackTrace();
		
		}

	}

}
