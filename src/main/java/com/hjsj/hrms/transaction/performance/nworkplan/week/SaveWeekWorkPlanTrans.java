package com.hjsj.hrms.transaction.performance.nworkplan.week;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * SaveWeekWorkPlanTrans.java
 * Description: 保存周报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 8, 2013 9:54:41 AM Jianghe created
 */
public class SaveWeekWorkPlanTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String saveflag = (String)map.get("saveflag");
			NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			String type=(String)this.getFormHM().get("type");
			String log_type = (String)this.getFormHM().get("log_type");
			String p0100=(String)this.getFormHM().get("p0100");
			String personPage=(String)this.getFormHM().get("personPage");
			String isChuZhang=(String)this.getFormHM().get("isChuZhang");
			String planYear_start=(String)this.getFormHM().get("planYear_start");
			String planMonth_start=(String)this.getFormHM().get("planMonth_start");
			String planDay_start=(String)this.getFormHM().get("planDay_start");
			String planYear_end=(String)this.getFormHM().get("planYear_end");
			String planMonth_end=(String)this.getFormHM().get("planMonth_end");
			String planDay_end=(String)this.getFormHM().get("planDay_end");
			String summarizeYear=(String)this.getFormHM().get("summarizeYear");
			String summarizeTime=(String)this.getFormHM().get("summarizeTime");
			summarizeTime=summarizeTime.replaceAll("－", "-"); 
			SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
			Date start_date =format.parse(planYear_start+"-"+planMonth_start+"-"+planDay_start);
			Date end_date =format.parse(planYear_end+"-"+planMonth_end+"-"+planDay_end);
		    String t_p0100=p0100;
			if("1".equals(type)){
				//新增
				if("1".equals(log_type)){
					t_p0100=bo.saveRecordWeek(personPage,isChuZhang,type,log_type,p0100,"",(ArrayList)this.getFormHM().get("jihuaFieldsList"),summarizeTime,start_date,end_date);
				}else{
					t_p0100=bo.saveRecordWeek(personPage,isChuZhang,type,log_type,p0100,"",(ArrayList)this.getFormHM().get("zongjieFieldsList"),summarizeTime,start_date,end_date);
				}
				this.getFormHM().put("record_num", "");
			}else{
				//修改
				String record_num=(String)map.get("record_num");
				if("1".equals(log_type)){
					bo.saveRecordWeek(personPage,isChuZhang,type,log_type,p0100,record_num,(ArrayList)this.getFormHM().get("jihuaFieldsList"),summarizeTime,start_date,end_date);
				}else{
					bo.saveRecordWeek(personPage,isChuZhang,type,log_type,p0100,record_num,(ArrayList)this.getFormHM().get("zongjieFieldsList"),summarizeTime,start_date,end_date);
				}
			
			}
			if("2".equals(saveflag)&& "1".equals(type)){
				String planFields = (String)this.getFormHM().get("planFields");
				ArrayList jihuaFieldsList = bo.getJihuaOrZongjieFieldsList(p0100,"",type,log_type,planFields);
				this.getFormHM().put("jihuaFieldsList", jihuaFieldsList);
				String summarizeFields = (String)this.getFormHM().get("summarizeFields");
				ArrayList zongjieFieldsList = bo.getJihuaOrZongjieFieldsList(p0100,"",type,log_type,summarizeFields);
				this.getFormHM().put("zongjieFieldsList", zongjieFieldsList);
			}else if("2".equals(saveflag)){
				String summarizeFields = (String)this.getFormHM().get("summarizeFields");
				ArrayList zongjieFieldsList = bo.getJihuaOrZongjieFieldsList(p0100,"",type,log_type,summarizeFields);
				this.getFormHM().put("zongjieFieldsList", zongjieFieldsList);
				String planFields = (String)this.getFormHM().get("planFields");
				ArrayList jihuaFieldsList = bo.getJihuaOrZongjieFieldsList(p0100,"",type,log_type,planFields);
				this.getFormHM().put("jihuaFieldsList", jihuaFieldsList);
			}
			this.getFormHM().put("saveflag", saveflag);
			this.getFormHM().put("summarizeTime", bo.getSummarizeTime());
			this.getFormHM().put("p0100", t_p0100);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
