package com.hjsj.hrms.module.kq.kqself.kqempcal.transaction;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.module.kq.kqself.kqempcal.businessobject.KqEmployeeCalendar;
import com.hjsj.hrms.module.template.utils.TemplateServiceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 员工考勤日历交易类
 * <p>Title: KqEmpCalTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2016-10-24 下午02:21:54</p>
 * @author linbz
 * @version 1.0
 */
public class KqEmpCalTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
        try 
        {
        	String flag = (String) this.getFormHM().get("flag");
        	String self = (String) this.getFormHM().get("self");
        	String nbase = (String) this.getFormHM().get("nbase");
    		String a0100 = (String) this.getFormHM().get("a0100");
    		
        	if("self".equalsIgnoreCase(self)){
        		nbase = this.userView.getDbname();
        		a0100 = this.userView.getA0100();
        	} else {
        	    nbase = PubFunc.decrypt(nbase);
        	    a0100 = PubFunc.decrypt(a0100);
        	}
        	
        	boolean version = PubFunc.isUseNewPrograme(this.userView);
        	if(version){
        		this.getFormHM().put("version", "0");//新版本
        	}else{
        		this.getFormHM().put("version", "1");//旧版本
        	}
        	
        	//一个考勤期间的开始，结束日期
        	String startday = "";
        	String endday = "";
        	//获取未封存期间的第一天
        	ArrayList daylist = RegisterDate.getKqDayList(this.getFrameconn());
        	String firstday = (String) daylist.get(0);
        	
        	KqEmployeeCalendar kqEmployeeCalendar = new KqEmployeeCalendar(this.getFrameconn(), this.userView, a0100, nbase);

        	flag = "," + flag + ",";
        	if(flag.contains(",all,") || flag.contains(",duration,")){
        		
        		//取当天之前考勤期间
        		ArrayList kqDurationslist = kqEmployeeCalendar.getKqDurations(this.getFrameconn());
        		JSONArray kqDurationsjson = new JSONArray();
        		if (kqDurationslist == null || kqDurationslist.size() <= 0){
        			throw new GeneralException(ResourceFactory.getProperty("kq.register.session.nosave"));
        		}
     		
        		for(int i=0;i<kqDurationslist.size();i++){
        			JSONObject jo = new JSONObject();
        			CommonData vo = (CommonData) kqDurationslist.get(i);
        			jo.put("duration", vo.getDataValue());
        			kqDurationsjson.add(jo);
        		}
        		
        		//本考勤期间
        		String nowDuration = (String) this.getFormHM().get("nowDuration");
        		if(nowDuration == null || nowDuration.length() <= 0){
        			CommonData vo = (CommonData) kqDurationslist.get(0);
        			nowDuration = vo.getDataValue();
        		}
        		// 获取考勤期间内所有的日期
        		ArrayList datelist = RegisterDate.getOneDurationDateList(this.getFrameconn(), nowDuration, "-1", "");
        		startday = datelist.get(0).toString();
            	endday = datelist.get(datelist.size()-1).toString();
        		JSONArray datesjson = new JSONArray();
        		for(int i=0;i<datelist.size();i++){
        			JSONObject jo = new JSONObject();
        			jo.put("date", (String) datelist.get(i));
        			datesjson.add(jo);
        		}
        		
        		//已封存考勤期间不显示申请模板=1显示，=0不显示
        		if(!DateUtils.getDate((String)datelist.get(0), "yyyy.MM.dd").before(DateUtils.getDate(firstday, "yyyy.MM.dd"))){
        			this.getFormHM().put("barflag", "1");
        		}else{
        			this.getFormHM().put("barflag", "0");
        		}
        		
        		JSONArray dailyInfojson = new JSONArray();
        		ArrayList dailyInfolist = kqEmployeeCalendar.getDailyInfo(startday, endday);
        		for(int i=0;i<dailyInfolist.size();i++){
        			LazyDynaBean dailyInfo = (LazyDynaBean) dailyInfolist.get(i);
        			JSONObject jo = new JSONObject();
        			String date = (String) dailyInfo.get("date");
        			String state = (String) dailyInfo.get("state");//=0 正常；=1 异常(迟到、早退、旷工)
        			String leave = (String) dailyInfo.get("leave");//=1请假
        			String overtime = (String) dailyInfo.get("overtime");//=1加班
        			String outwork = (String) dailyInfo.get("outwork");//=1公出
        			String rest = (String) dailyInfo.get("rest");//=1休息（应出勤为0）
        			String normal = (String) dailyInfo.get("normal");//=1(实出勤>=应出勤)
        			String spflag = (String) dailyInfo.get("spflag");//=1已报批;=0其他状态
        			
        			jo.put("date", date);
        			jo.put("spflag", spflag);
        			if("1".equals(state) && "1".equals(overtime) && ("1".equals(leave)|| "1".equals(outwork))){//异常+加班+(请假||公出)
        				jo.put("state", "ltotexp");
        			}else if("1".equals(normal) && "1".equals(overtime) && ("1".equals(leave)|| "1".equals(outwork))){//正常+加班+(请假||公出)
        				jo.put("state", "ltotnor");
        			}else if(("1".equals(leave) && "1".equals(state)) || ("1".equals(outwork) && "1".equals(state))){//请假+异常 || 公出+异常
        				jo.put("state", "ltexp");
        			}// 45776 暂时取消请假公出 与正常共存的状态
//        			else if((leave.equals("1") && state.equals("0")) || (outwork.equals("1") && state.equals("0"))){//请假+正常 || 公出+正常
//        				jo.put("state", "ltnor");
//        			}
        			else if(("1".equals(leave) && "1".equals(overtime)) || ("1".equals(outwork) && "1".equals(overtime))){//请假+加班 || 公出+加班
        				jo.put("state", "ltot");
        			}else if("0".equals(state) && "1".equals(overtime)  && "1".equals(normal)){//正常+加班
        				jo.put("state", "normalot");
        			}else if("1".equals(state) && "1".equals(overtime)){//异常+加班
        				jo.put("state", "exceptot");
        			}else if("1".equals(state)){//异常
        				jo.put("state", "except");
        			}else if("1".equals(leave) || "1".equals(outwork)){//请假||公出
        				jo.put("state", "lt");
        			}else if("1".equals(overtime)){//加班
        				jo.put("state", "ot");
        			}else if("0".equals(state) && "0".equals(rest) && "1".equals(normal)){//正常出勤
        				jo.put("state", "normal");
        			}else if("1".equals(rest)){//正常休息
        				jo.put("state", "rest");
        			}else {//qita
        				jo.put("state", "rest");
        			}
        			
        			dailyInfojson.add(jo);
        		}
        		
        		this.getFormHM().put("nowDuration", nowDuration);
        		this.getFormHM().put("durationsjson", kqDurationsjson);
        		this.getFormHM().put("datesjson", datesjson);
        		this.getFormHM().put("dailyInfojson", dailyInfojson);
        		
        	}
        	//汇总信息
        	if(flag.contains(",all,") || flag.contains(",summary,")){
        		//开始结束日期  格式 2016.01.30 
        		String startValue = (String) this.getFormHM().get("startday");
        		startValue = startValue==null?"":startValue;
        		String endValue = (String) this.getFormHM().get("endday");
        		endValue = endValue==null?"":endValue;
        		if(!"".equals(startValue) && !"".equals(endValue)){
        			startday = startValue;
        			endday = endValue;
        		}
        		
        		float normal = 0;//正常
        		float be_late = 0;//迟到
        		float absent = 0;//旷工
        		float leave_early = 0;//早退
        		float leave = 0;//请假
        		float overtime = 0;//加班
        		float office_leave = 0;//公出
        		
        		if(!"".equals(startday) && !"".equals(endday)){
        			HashMap<String, Float> sumInfomap = kqEmployeeCalendar.getSumInfo(startday, endday);
        			normal = sumInfomap.get("normal");
        			be_late = sumInfomap.get("be_late");
        			absent = sumInfomap.get("absent");
        			leave_early = sumInfomap.get("leave_early");
        			leave = sumInfomap.get("leave");
        			overtime = sumInfomap.get("overtime");
        			office_leave = sumInfomap.get("office_leave");
        		}
        		
        		JSONObject sumjo = new JSONObject();
        		sumjo.put("normal", normal);
        		sumjo.put("be_late", be_late);
        		sumjo.put("absent", absent);
        		sumjo.put("leave_early", leave_early);
        		sumjo.put("leave", leave);
        		sumjo.put("office_leave", office_leave);
        		sumjo.put("overtime", overtime);
        		
        		this.getFormHM().put("sumjson", sumjo);
        	}
        	
        	//获取当天出勤信息
        	if(flag.contains(",all,") || flag.contains(",detailinfo,")){
        		
        		//-------------------时间轴班次数据----------------------------------------------continue
        		
        		String datevalue = (String) this.getFormHM().get("datevalue");
        		if(datevalue == null || datevalue.length() <= 0){
        			datevalue = DateUtils.format(new Date(), "yyyy.MM.dd");
        		} else {
        		    datevalue = datevalue.replaceAll("-", ".");
        		}
        		
        		//已封存考勤期间不显示申请模板=1显示，=0不显示
        		if(!DateUtils.getDate(datevalue, "yyyy.MM.dd").before(DateUtils.getDate(firstday, "yyyy.MM.dd"))){
        			this.getFormHM().put("barflag", "1");
        		}else{
        			this.getFormHM().put("barflag", "0");
        		}
        		
        		HashMap dailyInfo = kqEmployeeCalendar.getDailyDetail(datevalue);
        		
        		ArrayList classlist = (ArrayList)dailyInfo.get("classtime");
        		String onduty_start = "";//上班刷卡时间起
        		String onduty = "";//上班
        		String offduty = "";//下班
        		String offduty_end = "";//下班刷卡时间止
              
        		LazyDynaBean ldb = new LazyDynaBean();
        		JSONArray classjson = new JSONArray();
        		for (int i = 0; i < classlist.size(); i++) {
        			ldb = new LazyDynaBean();
    				ldb = (LazyDynaBean) classlist.get(i);
	              	JSONObject classjo = new JSONObject();
	              	
	              	onduty_start = (String) ldb.get("onduty_start");
	              	if(!StringUtils.isEmpty(onduty_start)){
	              		classjo = new JSONObject();
		              	classjo.put("classTime", onduty_start);
		                classjo.put("info", "上班刷卡起  ");
		              	classjson.add(classjo);
	              	}
                	onduty = (String) ldb.get("onduty");
                	if(!StringUtils.isEmpty(onduty)){
	                	classjo = new JSONObject();
	                	classjo.put("classTime", onduty);
		                classjo.put("info", "上班  ");
	                	classjson.add(classjo);
                	}
                	offduty = (String) ldb.get("offduty");
                	if(!StringUtils.isEmpty(offduty)){
	                	classjo = new JSONObject();
	                	classjo.put("classTime", offduty);
                		classjo.put("info", "下班  ");
	                	classjson.add(classjo);
                	}
                    offduty_end = (String) ldb.get("offduty_end");
                    if(!StringUtils.isEmpty(offduty_end)){
	                    classjo = new JSONObject();
	                    classjo.put("classTime", offduty_end);
                		classjo.put("info", "下班刷卡止  ");
		                classjson.add(classjo);
                    }
        		}
        		
        		String className = (String)dailyInfo.get("classname");
        		JSONObject namejo = new JSONObject();
        		namejo.put("name", className);
        		namejo.put("date", datevalue);
        		
        		ArrayList ctoslist = (ArrayList)dailyInfo.get("cardinfo");
        		//刷卡时间 与 班次时间   比较
    			JSONArray cardToclassjson = new JSONArray();
    			JSONObject cardToclass = new JSONObject();
    			for(int i=0;i<ctoslist.size();i++){
    				ldb = new LazyDynaBean();
    				ldb = (LazyDynaBean) ctoslist.get(i);
    				String cardtime = (String) ldb.get("worktime");
    				String infomsg = (String) ldb.get("infomsg");
    				String workdate = (String) ldb.get("workdate");
    				String spinfo = (String) ldb.get("spinfo");
    				
    				cardToclass.put("card", cardtime);
    				cardToclass.put("info", infomsg);
    				cardToclass.put("spinfo", spinfo);
    				cardToclass.put("cardDate", workdate);
    				cardToclassjson.add(cardToclass);
    			}
        		
				this.getFormHM().put("cardToclass", cardToclassjson);
        		this.getFormHM().put("classinfo", classjson);
//        		this.getFormHM().put("cardtime", cardjson);
        		this.getFormHM().put("classname", namejo);
        		
        		// 各个申请单的申请类型
        		HashMap itemsMap = (HashMap) dailyInfo.get("allItems");
        		this.getFormHM().put("itemsMap", itemsMap);
        	}
            
        	//获取申请模板信息
        	if(flag.contains(",all,") || flag.contains(",templates,")){
        		//--------------考勤业务相关模板--------------------------------------------------
        		HashMap<String, ArrayList<String>> temp =  kqEmployeeCalendar.getKqTemplates();
        		
        		//请假
        		JSONArray leaveTemps = new JSONArray();
        		JSONObject leaveTemp = new JSONObject();
        		ArrayList<String> leaveTemplates = (ArrayList<String>) temp.get("leave");
        		for(int i=0;i<leaveTemplates.size();i++){
        			// 34936 微信端接口模板参数用
        			leaveTemp.put("leaveTemp", leaveTemplates.get(i));
        			// 其他接口模板参数
        			leaveTemp.put("temp", leaveTemplates.get(i));
        			leaveTemps.add(leaveTemp);
    			}
        		this.getFormHM().put("leaveTemplates", leaveTemps);
        		// 销假-请假
        		JSONArray qxjq15s = new JSONArray();
        		JSONObject qxjq15 = new JSONObject();
        		ArrayList<String> qxjq15Templates = (ArrayList<String>) temp.get("qxjq15");
        		for(int i=0;i<qxjq15Templates.size();i++){
        			qxjq15.put("temp", qxjq15Templates.get(i));
        			qxjq15s.add(qxjq15);
    			}
        		this.getFormHM().put("qxjQ15Templates", qxjq15s);
        		//公出
        		JSONArray officeleaveTemps = new JSONArray();
        		JSONObject officeleaveTemp = new JSONObject();
                ArrayList<String> officeleaveTemplates = (ArrayList<String>) temp.get("officeleave");
                for(int i=0;i<officeleaveTemplates.size();i++){
                	// 微信端接口模板参数用
                	officeleaveTemp.put("officeleaveTemp", officeleaveTemplates.get(i));
                	// 其他接口模板参数
                	officeleaveTemp.put("temp", officeleaveTemplates.get(i));
        			officeleaveTemps.add(officeleaveTemp);
    			}
        		this.getFormHM().put("officeleaveTemplates", officeleaveTemps);
        		// 销假-公出
        		JSONArray qxjq13s = new JSONArray();
        		JSONObject qxjq13 = new JSONObject();
        		ArrayList<String> qxjq13Templates = (ArrayList<String>) temp.get("qxjq13");
        		for(int i=0;i<qxjq13Templates.size();i++){
        			qxjq13.put("temp", qxjq13Templates.get(i));
        			qxjq13s.add(qxjq13);
    			}
        		this.getFormHM().put("qxjQ13Templates", qxjq13s);
                //加班
        		JSONArray overtimeTemps = new JSONArray();
        		JSONObject overtimeTemp = new JSONObject();
                ArrayList<String> overtimeTemplates = (ArrayList<String>) temp.get("overtime");
                for(int i=0;i<overtimeTemplates.size();i++){
                	// 微信端接口模板参数用
                	overtimeTemp.put("overtimeTemp", overtimeTemplates.get(i));
                	// 其他接口模板参数
                	overtimeTemp.put("temp", overtimeTemplates.get(i));
                	overtimeTemps.add(overtimeTemp);
    			}
        		this.getFormHM().put("overtimeTemplates", overtimeTemps);
        		// 销假-加班
        		JSONArray qxjq11s = new JSONArray();
        		JSONObject qxjq11 = new JSONObject();
        		ArrayList<String> qxjq11Templates = (ArrayList<String>) temp.get("qxjq11");
        		for(int i=0;i<qxjq11Templates.size();i++){
        			qxjq11.put("temp", qxjq11Templates.get(i));
        			qxjq11s.add(qxjq11);
    			}
        		this.getFormHM().put("qxjQ11Templates", qxjq11s);
                //刷卡
        		JSONArray cardTemps = new JSONArray();
        		JSONObject cardTemp = new JSONObject();
                ArrayList<String> cardTemplates = (ArrayList<String>) temp.get("card");
                for(int i=0;i<cardTemplates.size();i++){
                	cardTemp.put("temp", cardTemplates.get(i));
                	cardTemps.add(cardTemp);
                }
                this.getFormHM().put("cardTemplates", cardTemps);
                //自助  补签加班请假公出的功能权限
        		JSONObject priv = new JSONObject();
        		priv.put("cardp", this.userView.hasTheFunction("0B041")?"1":"0");//补签权限
        		priv.put("leavep", this.userView.hasTheFunction("0B223")?"1":"0");//请假
        		priv.put("leavepxj", this.userView.hasTheFunction("0B220")?"1":"0");//请假销假
        		priv.put("overp", this.userView.hasTheFunction("0B213")?"1":"0");//加班
        		priv.put("overpxj", this.userView.hasTheFunction("0B210")?"1":"0");//加班销假
        		priv.put("officep", this.userView.hasTheFunction("0B233")?"1":"0");//公出
        		priv.put("officepxj", this.userView.hasTheFunction("0B230")?"1":"0");//公出销假
        		this.getFormHM().put("privs", priv);
        	}
        	
        	//获取年度各类请假数据
        	if(flag.contains(",holidayData,")) {
        	    String kqYear = (String)this.getFormHM().get("kqyear");
        	    ArrayList holidays = new ArrayList();
        	    holidays = kqEmployeeCalendar.getPersonHoliday(kqYear);
        	    
        	    //转成json
                JSONArray holidaysJson = new JSONArray();
                JSONObject holidayJson = new JSONObject();
                
                for(int i=0;i<holidays.size();i++){
                    HashMap<String, String> hm = (HashMap<String, String>)holidays.get(i);
                    holidayJson.put("type", (String)hm.get("type"));
                    holidayJson.put("name", (String)hm.get("name"));
                    holidayJson.put("used", (String)hm.get("used"));
                    holidayJson.put("remain", (String)hm.get("remain"));
                    holidayJson.put("flag", (String)hm.get("flag"));
                    holidayJson.put("unit", (String)hm.get("unit"));
                    holidayJson.put("lastYesrUsed", (String)hm.get("lastYesrUsed"));
                    holidaysJson.add(holidayJson);
                }
                this.getFormHM().put("holidayData", holidaysJson);
        	}
        	
        	//获取某类假的请假明细
        	if(flag.contains(",holidayDetail,")) {
        	    String kqYear = (String)this.getFormHM().get("kqyear");
        	    String appType = (String)this.getFormHM().get("type");
        	    
        	    ArrayList holidayDetails = new ArrayList();
        	    
        	    ArrayList<Date> yearScope = kqEmployeeCalendar.getKqDateScope(kqYear, appType);
        	    if(yearScope != null && yearScope.size() == 2) {
        	        Date yearStart = yearScope.get(0);
        	        Date yearEnd = yearScope.get(1);
        	        holidayDetails = kqEmployeeCalendar.getPersonAppDetail(appType, yearStart, yearEnd);
        	    }
        	    
        	    //转成json
        	    JSONArray holidayDetailsJson = new JSONArray();
                JSONObject holidayDetailJson = new JSONObject();
                
                for(int i=0;i<holidayDetails.size();i++){
                    HashMap<String, String> hm = (HashMap<String, String>)holidayDetails.get(i);
                    holidayDetailJson.put("id", (String)hm.get("id"));
                    holidayDetailJson.put("oldId", (String)hm.get("oldId"));
                    holidayDetailJson.put("type", (String)hm.get("type"));
                    holidayDetailJson.put("typeName", (String)hm.get("typeName"));
                    holidayDetailJson.put("applyTime", (String)hm.get("applyTime"));
                    holidayDetailJson.put("beginTime", (String)hm.get("beginTime"));
                    holidayDetailJson.put("endTime", (String)hm.get("endTime"));
                    holidayDetailJson.put("unit", (String)hm.get("unit"));
                    holidayDetailJson.put("timeLen", (String)hm.get("timeLen"));
                    holidayDetailJson.put("reason", (String)hm.get("reason"));
                    holidayDetailsJson.add(holidayDetailJson);
                }
                this.getFormHM().put("holidayDetail", holidayDetailsJson);
        	}     
        	// 区间内所有申请单据表单
        	if(flag.contains(",all,") || flag.contains(",applyData,")){
        		// 开始结束日期  格式 2016.01.30 
        		String startValue = (String) this.getFormHM().get("startday");
        		startValue = startValue==null?"":startValue;
        		String endValue = (String) this.getFormHM().get("endday");
        		endValue = endValue==null?"":endValue;
        		if(!"".equals(startValue) && !"".equals(endValue)){
        			startday = startValue;
        			endday = endValue;
        		}
        		
        		HashMap<String, ArrayList<String>> applyData = kqEmployeeCalendar.getDailyApplyList(startday, endday);
        		this.getFormHM().put("applyData", applyData);
        	}
        	// 增加假单模板数据驳回后继续走人事异动模板流程
        	if(flag.contains(",rejectAppData,")) {
        		// 模板id
        		String tabId = (String) this.getFormHM().get("tabId");
        		if(StringUtils.isEmpty(tabId)) {
        			this.getFormHM().put("task_id", "0");
        			return;
        		}
        		// 驳回的单号
        		String rejectNum = (String) this.getFormHM().get("rejectNum");
        		TemplateServiceBo tsbo = new TemplateServiceBo(this.frameconn,this.userView);
    			LazyDynaBean ldb = tsbo.getTemplateInfoForKq(rejectNum, Integer.parseInt(tabId));
    			String task_id = ldb.get("task_id_e")==null?"0":(String)ldb.get("task_id_e");
    			this.getFormHM().put("task_id", task_id);
        	}
        	
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }


}
