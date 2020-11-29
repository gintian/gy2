package com.hjsj.hrms.transaction.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class SaveQuarterTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		String isok = "保存失败!";
		
		NewWorkPlanBo nw = new NewWorkPlanBo(this.frameconn,this.userView);
		String type = (String)this.getFormHM().get("type");
		String opt = (String)this.getFormHM().get("opt");
		String isdept = (String)this.getFormHM().get("isdept");
		
		String states = "";
		
		String year = (String)this.getFormHM().get("year");
		NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn,this.userView);
		String message = (String)this.getFormHM().get("message");
		message = PubFunc.keyWord_reback(message);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if("".equals(message.trim())){
			isok = "请输入需要填写的内容!";
		}else{
			if("1".equals(opt)){
				if("1".equals(type)){  //季报入口
					int season = Integer.parseInt((String)this.getFormHM().get("season"));
					String startMonth = (String)this.getFormHM().get("startMonth");
					String endMonth = (String)this.getFormHM().get("endMonth");
					//message = SafeCode.decode(message);
					String startDate = year + "-" + startMonth + "-01";
					String endDate = year + "-" + endMonth + "-01";
					try {
						if(nw.SaveSeason(message, new Date(sdf.parse(startDate).getTime()), new Date(sdf.parse(endDate).getTime()),season,Integer.parseInt(year),type,opt,"",isdept)){
							isok = "保存成功!";
							states = bo.getStatesByYearSeason(Integer.parseInt(year), season,opt,"",isdept);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						this.getFormHM().put("shows", isok);
					}
				}else if("2".equals(type)){
					String startDate = year + "-01-01";
					String endDate = year + "-12-31";
					try {
						if(nw.SaveSeason(message, new Date(sdf.parse(startDate).getTime()), new Date(sdf.parse(endDate).getTime()),0,Integer.parseInt(year),type,opt,"",isdept)){
							isok = "保存成功!";
							states = bo.getStatesByYear(Integer.parseInt(year),opt,"",isdept);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						this.getFormHM().put("shows", isok);
					}
				}
			}else if("2".equals(opt)){
				//String a0100 = (String)this.getFormHM().get("a0100");
				//String nbase = (String)this.getFormHM().get("nbase");
				//String p0100 = (String)this.getFormHM().get("p0100");
				if("1".equals(type)){  //季报入口
					int season = Integer.parseInt((String)this.getFormHM().get("season"));
					String p0100 = bo.getP0100ByCheckSeason(Integer.parseInt(year), season, isdept, type);
					String startMonth = (String)this.getFormHM().get("startMonth");
					String endMonth = (String)this.getFormHM().get("endMonth");
					//message = SafeCode.decode(message);
					String startDate = year + "-" + startMonth + "-01";
					String endDate = year + "-" + endMonth + "-01";
					try {
						if(nw.SaveSeason(message, new Date(sdf.parse(startDate).getTime()), new Date(sdf.parse(endDate).getTime()),season,Integer.parseInt(year),type,opt,p0100,"1")){
							isok = "保存成功!";
							states = bo.getStatesByYearSeason(Integer.parseInt(year), season,opt,p0100,"1");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						this.getFormHM().put("shows", isok);
					}
				}else if("2".equals(type)){
					String startDate = year + "-01-01";
					String endDate = year + "-12-31";
					String p0100 = bo.getP0100ByCheckSeason(Integer.parseInt(year), 0, isdept, type);
					try {
						if(nw.SaveSeason(message, new Date(sdf.parse(startDate).getTime()), new Date(sdf.parse(endDate).getTime()),0,Integer.parseInt(year),type,opt,p0100,"1")){
							isok = "保存成功!";
							states = bo.getStatesByYear(Integer.parseInt(year),opt,p0100,"1");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						this.getFormHM().put("shows", isok);
						this.getFormHM().remove("shows");
						this.getFormHM().put("shows", "");
					}
				}
			}
			if(!"".equals(states.trim())){
				String [] state = states.split(",");
				String desc = state[0];
				String code = state[1];
				this.getFormHM().put("code", code);
				this.getFormHM().put("states", desc);
			}
		}
	}
	
}
