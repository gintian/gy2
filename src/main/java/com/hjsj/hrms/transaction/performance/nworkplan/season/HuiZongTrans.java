package com.hjsj.hrms.transaction.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class HuiZongTrans extends IBusiness{

	public void execute() throws GeneralException {
		String year = (String)this.getFormHM().get("year");
		NewWorkPlanBo nw = new NewWorkPlanBo(this.frameconn,this.userView);
		String type = (String)this.getFormHM().get("type");
		String isdept = (String)this.getFormHM().get("isdept");
		String opt = (String)this.getFormHM().get("opt");
		String isok = "";
		String states = "";
		String season = "";
		if("1".equals(type)){
			season = (String)this.getFormHM().get("season");
		}
		NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn,this.userView);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String message = bo.getHuizongInfo(type, year, season,isdept);
		
		if("1".equals(opt)){
			if("1".equals(type)){  //季报入口
				String startMonth = (String)this.getFormHM().get("startMonth");
				String endMonth = (String)this.getFormHM().get("endMonth");
				//message = SafeCode.decode(message);
				String startDate = year + "-" + startMonth + "-01";
				String endDate = year + "-" + endMonth + "-01";
				try {
					if(nw.SaveSeason(message, new Date(sdf.parse(startDate).getTime()), new Date(sdf.parse(endDate).getTime()),Integer.parseInt(season),Integer.parseInt(year),type,opt,"",isdept)){
						isok = "保存成功!";
						states = bo.getStatesByYearSeason(Integer.parseInt(year), Integer.parseInt(season),opt,"",isdept);
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
		}
		if(!"".equals(states.trim())){
			String [] state = states.split(",");
			String desc = state[0];
			String code = state[1];
			this.getFormHM().put("code", code);
			this.getFormHM().put("states", desc);
		}
		message = SafeCode.encode(message);
		this.getFormHM().put("message", message);
	}
	}
