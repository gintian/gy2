package com.hjsj.hrms.transaction.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchSeasonInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		String year = (String)this.getFormHM().get("year");
		String in_type = (String)this.getFormHM().get("in_type");
		String type = (String)this.getFormHM().get("type");
		String season = (String)this.getFormHM().get("season");
		NewWorkPlanBo nwb = new NewWorkPlanBo(this.frameconn,this.userView);
		String opt = (String)this.getFormHM().get("opt");
		String isdept = (String)this.getFormHM().get("isdept");
		String desc = "";
		String code = "";
		String message = "";
		String states = "";
		if("1".equals(opt)){      //当前登录用户进入
			if("1".equals(type)){ //季报入口
				String months = nwb.getMonthsBySeason(season);
				if("1".equals(in_type.trim())){	     //提取		
					message = nwb.getOldMessageByYearSeason(Integer.parseInt(year), Integer.parseInt(season));
				}else if("2".equals(in_type.trim())){//按钮切换
					message = nwb.getMessageByYearAndSeason(Integer.parseInt(year), Integer.parseInt(season),opt,"",isdept);
				}
				states = nwb.getStatesByYearSeason(Integer.parseInt(year), Integer.parseInt(season),opt,"",isdept);
				
				if(!"".equals(months)){
					String [] month = months.split(",");
					this.getFormHM().put("startMonth", month[0]);
					this.getFormHM().put("endMonth", month[1]);
				}
			}else if("2".equals(type)){
				states = nwb.getStatesByYear(Integer.parseInt(year),opt,"",isdept);
				if("1".equals(in_type.trim())){			
					message = nwb.getOldMessageByYear(Integer.parseInt(year));
				}else if("2".equals(in_type.trim())){
					message = nwb.getYearMessageByYear(Integer.parseInt(year),opt,"",isdept);
				}
			}
		}else if("2".equals(opt)){
			//String a0100 = (String)this.getFormHM().get("a0100");
			//String nbase = (String)this.getFormHM().get("nbase");
			String p0100 = (String)this.getFormHM().get("p0100");
			String isread = (String)this.getFormHM().get("isread");
			if("1".equals(type)){ //季报入口
				String months = nwb.getMonthsBySeason(season);
				if("1".equals(in_type.trim())){	     //提取		
					message = nwb.getOldMessageByYearSeason(Integer.parseInt(year), Integer.parseInt(season));
				}else if("2".equals(in_type.trim())){//按钮切换
					p0100 = nwb.getP0100ByCheckSeason(Integer.parseInt(year), Integer.parseInt(season), isdept, type);
					message = nwb.getMessageByYearAndSeason(Integer.parseInt(year), Integer.parseInt(season),opt,p0100,"1");
					this.getFormHM().put("p0100", p0100);
				}
				states = nwb.getStatesByYearSeason(Integer.parseInt(year), Integer.parseInt(season),opt,p0100,"1");
				
				if(!"".equals(months)){
					String [] month = months.split(",");
					this.getFormHM().put("startMonth", month[0]);
					this.getFormHM().put("endMonth", month[1]);
				}
			}else if("2".equals(type)){
				states = nwb.getStatesByYear(Integer.parseInt(year),opt,p0100,"1");
				if("1".equals(in_type.trim())){			
					message = nwb.getOldMessageByYear(Integer.parseInt(year));
				}else if("2".equals(in_type.trim())){
					//年的时候 season可以随意传入一个整型参数 方法里面会判断 用不上
					p0100 = nwb.getP0100ByCheckSeason(Integer.parseInt(year), 0, opt, type);
					message = nwb.getYearMessageByYear(Integer.parseInt(year),opt,p0100,"1");
					this.getFormHM().put("p0100", p0100);
				}
			}
			if("1".equals(isread)){
				if("".equals(message.trim())){
					this.getFormHM().put("disable", "1");
				}else{
					this.getFormHM().put("disable", "");
				}
			}else{
				this.getFormHM().put("disable", "");
			}
		}
		if(!"".equals(states.trim())){
			String [] state = states.split(",");
			desc = state[0];
			code = state[1];
		}
		this.getFormHM().put("code", code);
		this.getFormHM().put("states", desc);
		message = SafeCode.encode(message);
		this.getFormHM().put("message", message);
	}
}
