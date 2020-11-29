package com.hjsj.hrms.module.workplan.worklog.transaction;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.module.workplan.weeklysummary.businessobject.WeeklySummaryBo;
import com.hjsj.hrms.module.workplan.worklog.businessobject.WorkLogBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class WorkLogTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		try {
			String employeflag = (String) this.getFormHM().get("employeflag");//=1监控标示
			employeflag = employeflag==null?"":employeflag;
			String nbase = (String) this.getFormHM().get("nbase");
			nbase = PubFunc.decrypt(nbase);
			String a0100 = (String) this.getFormHM().get("a0100");
			a0100 = PubFunc.decrypt(a0100);
			//员工监控查询人员日志时自助用户具有权限也可以查看
			if (!"1".equals(employeflag) && StringUtils.isEmpty(this.userView.getDbname())
					&& StringUtils.isEmpty(this.userView.getA0100()) && StringUtils.isEmpty(nbase) && StringUtils.isEmpty(a0100)) {	
				this.getFormHM().put("error", 1);
				return;
			}
			
			String flag = (String) this.getFormHM().get("flag");
        	String self = (String) this.getFormHM().get("self");
    		String subobjectid1 = (String) this.getFormHM().get("subobjectid");//人力地图获取 usr00000009
			subobjectid1 = WorkPlanUtil.decryption(subobjectid1);
			String mapflag = (String) this.getFormHM().get("mapflag");//地图查看下属日志标示
			mapflag = mapflag==null?"":mapflag;
    		
        	if(StringUtils.isEmpty(nbase) && StringUtils.isEmpty(a0100)){
        		nbase = this.userView.getDbname();
        		a0100 = this.userView.getA0100();
        	}
        	if(StringUtils.isNotEmpty(subobjectid1)){
        		nbase = subobjectid1.substring(0, 3);
        		a0100 = subobjectid1.substring(3);
        	}
        	
        	WorkLogBo worklogBo = new WorkLogBo(this.getFrameconn(), this.userView, a0100, nbase);
//        	if(!worklogBo.getPower() && !mapflag.equals("1")){
//        		this.getFormHM().put("power", 1);
//				return;
//        	}
        	
        	//isSelf =0 是本人  =1 其他人
        	String isSelf = "0";
        	if(!(this.userView.getDbname().equalsIgnoreCase(nbase.toLowerCase()) && this.userView.getA0100().equalsIgnoreCase(a0100.toLowerCase())))
        		isSelf = "1";
        	
        	this.getFormHM().put("isSelf", isSelf);
        	this.getFormHM().put("nbase", PubFunc.encrypt(nbase));
        	this.getFormHM().put("a0100", PubFunc.encrypt(a0100));
        	
        	if("all".equalsIgnoreCase(flag)){
				//获取填写日志期间参数
				this.getFormHM().put("section", worklogBo.getWorkLogSection());
				//工作任务子集
				WeeklySummaryBo weeklySummaryBo = new WeeklySummaryBo(this.getFrameconn(), this.userView);// 工具类
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				data = weeklySummaryBo.getE01a1PlanTask(nbase, a0100);
				this.getFormHM().put("dataE01a1", data);
				
				/* 获取参数设置 日志 耗时相关列参数1显示，2不显示*/  
				String taskTime = worklogBo.getTaskTimeSign();
				this.getFormHM().put("taskTime", taskTime);
				//工作类别代码类
				ArrayList codelist = AdminCode.getCodeItemList("84");
				JSONArray codes = new JSONArray();
				for(int i=0;i<codelist.size();i++){
					JSONObject jo = new JSONObject();
					CodeItem codeobj = (CodeItem) codelist.get(i);
					String codeitem = codeobj.getCodeitem().toString();
					String codename = codeobj.getCodename().toString();
					jo.put("codeitem", codeitem);
					jo.put("codename", codename);
					codes.add(jo);
					
				}
				this.getFormHM().put("codelist", codes);
        	}
        	
        	//某月的日期集合
        	ArrayList dateList = new ArrayList();
        	//切换月份
        	if("all".equalsIgnoreCase(flag) || "duration".equalsIgnoreCase(flag)){
        		
        		String monthdate = (String) this.getFormHM().get("nowMonth");
        		monthdate = monthdate==null?"":monthdate;
        		Calendar cal = Calendar.getInstance();
    			if("null".equals(monthdate) || monthdate.length() == 0){
    				String year = String.valueOf(cal.get(Calendar.YEAR));
    				String month = String.valueOf(cal.get(Calendar.MONTH)+1);
    				if(month.length() == 1)
    					month = "0"+month;
    				monthdate = year+"-"+month;
    			}
    			HashMap hm  = worklogBo.getDateInfoList(monthdate);
    			dateList = (ArrayList) hm.get("dateList");
    			ArrayList dateinfoList = (ArrayList) hm.get("dateListinfo");
    			int normal = 0;
    			int overdue = 0;
    			int isnull = 0;
        		JSONArray datesjson = new JSONArray();
        		for(int i=0;i<dateinfoList.size();i++){
        			JSONObject jo = new JSONObject();
        			LazyDynaBean infoBean = new LazyDynaBean();
        			infoBean = (LazyDynaBean) dateinfoList.get(i);
        			String date = (String) infoBean.get("date");
        			String duty = (String) infoBean.get("duty");//=0 ；=1 例行工作
        			String key = (String) infoBean.get("key");//=0 ；=1  重点工作
        			String oth = (String) infoBean.get("oth");//=0 ；=1  其他工作
//        			String no = (String) infoBean.get("no");//=0 ；=1 为空
//        			String p01flag = (String) infoBean.get("flag");//=0 正常填写；=1 补填
        			String state = (String) infoBean.get("state");
        			String classId = (String) infoBean.get("class_id");
        			
        			jo.put("date", date);
        			jo.put("classId", classId);
        			
        			if("1".equals(duty) && "1".equals(key) && "1".equals(oth)){//例行+重点+其他
        				jo.put("state", "all");
        			}else if("1".equals(duty) && "1".equals(key)){//例行+重点
        				jo.put("state", "dukey");
        			}else if("1".equals(duty) && "1".equals(oth)){//例行+其他
        				jo.put("state", "duoth");
        			}else if("1".equals(key) && "1".equals(oth)){//重点+其他
        				jo.put("state", "keoth");
        			}else if("1".equals(duty)){//例行
        				jo.put("state", "duty");
        			}else if("1".equals(key)){//重点
        				jo.put("state", "key");
        			}else if("1".equals(oth)){//其他
        				jo.put("state", "oth");
        			}else if("0".equals(classId)){//不需要填写
        				jo.put("state", "nofill");
        			}else if("isnull".equals(state)){//未填写
        				jo.put("state", "isnull");
        				isnull++;
        			}else  {//qita
        				jo.put("state", "no");
        			}
        			
        			if("after".equals(state)){//当前日期后则直接不可编辑
        				jo.put("state", "after");
        			}
        			
        			datesjson.add(jo);

        			//填写情况 正常normal 补填outfill
        			if("normal".equals(state)){
        				normal++;
        			}else if("overdue".equals(state)){
        				overdue++;
        			}
        		}
        		HashMap fillstate = new HashMap();
        		fillstate.put("normal", normal);
        		fillstate.put("overdue", overdue);
        		fillstate.put("isnull", isnull);
        		
        		this.getFormHM().put("fillstate", fillstate);
        		this.getFormHM().put("nowMonth", monthdate);
        		this.getFormHM().put("dates", datesjson);
        	}
        	
        	//汇总信息
        	if("all".equalsIgnoreCase(flag) || "duration".equalsIgnoreCase(flag) || "summary".equalsIgnoreCase(flag)){
        		
        		String dropMonth = (String) this.getFormHM().get("dropMonth");
        		dropMonth = dropMonth==null?"":dropMonth;
        		String dropDay = (String) this.getFormHM().get("dropDay");
        		dropDay = dropDay==null?"":dropDay;
        		//切换月汇总=0、周汇总=1
        		String droptype = (String) this.getFormHM().get("droptype");
        		droptype = droptype==null?"":droptype;
        		
        		HashMap hm = worklogBo.getSumTypetime(dateList, dropMonth, dropDay, droptype);
        		this.getFormHM().put("sumjo", hm);
        	}
        	
        	//加载日志表格记录  
        	if("all".equalsIgnoreCase(flag) || "onedaylog".equalsIgnoreCase(flag)){
        		String datevalue = (String) this.getFormHM().get("datevalue");//选中的日期
        		datevalue = datevalue==null?"":datevalue;
        		
        		if(datevalue == null || datevalue.length() <= 0){
        			datevalue = DateUtils.format(new Date(), "yyyy.MM.dd");
        		}
        		
        		String p0100 = (String) this.getFormHM().get("p0100");
        		p0100 = p0100==null?"":p0100;
        		
        		if("1".equals(employeflag) && dateList.size()>0){
        			datevalue = (String) dateList.get(0);
        		}
        		
        		HashMap map  = worklogBo.getTableList(p0100 ,datevalue);
        		
        		this.getFormHM().put("tabledata", map.get("tabledata"));
        		this.getFormHM().put("p0100", map.get("p0100"));
        		this.getFormHM().put("p0115", map.get("p0115"));
        		this.getFormHM().put("flag", map.get("flag"));
        		
        	}
        	
        	//添加日志记录 
        	if("addlog".equalsIgnoreCase(flag)){
        		//选中的日期
        		String datevalue = (String) this.getFormHM().get("datevalue");
        		datevalue = datevalue==null?"":datevalue;
        		String p0100 = (String) this.getFormHM().get("p0100");
        		p0100 = p0100==null?"":p0100;
        		
        		String work_type = (String) this.getFormHM().get("work_type");
        		work_type = work_type==null?"":work_type;
        		String content = (String) this.getFormHM().get("content");
        		content = content==null?"":content;
        		String start_time = (String) this.getFormHM().get("start_time");
        		start_time = start_time==null?"":start_time;
        		String end_time = (String) this.getFormHM().get("end_time");
        		end_time = end_time==null?"":end_time;
        		String work_time = (String) this.getFormHM().get("work_time");
        		work_time = work_time==null?"":work_time;
        		
        		LazyDynaBean bean = new LazyDynaBean();
        		bean.set("work_type", work_type);
        		bean.set("content", SafeCode.decode(content));
        		bean.set("p0100", p0100);
        		bean.set("start_time", start_time);
        		bean.set("end_time", end_time);
        		bean.set("work_time", work_time);
        		
        		HashMap map  = worklogBo.addWorkLog(bean);
        		this.getFormHM().put("errorcode", map.get("result"));
        		this.getFormHM().put("record_num", map.get("record_num"));
        		
        		HashMap tablemap1  = worklogBo.getTableList(p0100, datevalue);
        		this.getFormHM().put("tabledata", tablemap1.get("tabledata"));
        		
        	}
        	//删除记录
        	if("delelog".equalsIgnoreCase(flag)){
        		String p0100 = (String) this.getFormHM().get("p0100");
        		p0100 = p0100==null?"":p0100;
        		ArrayList record_nums = (ArrayList) this.getFormHM().get("record_nums");
        		
				String errorcode = "0";
				for(int i=0; i<record_nums.size(); i++){
					int record_num = Integer.parseInt((String)record_nums.get(i));
					
					HashMap map = worklogBo.deleteRecord(Integer.parseInt(p0100), record_num);
					if("1".equals((String)map.get("errorcode"))){
						errorcode = "1";
					}
				}
				this.getFormHM().put("errorcode", errorcode);
				
				HashMap tablemap1  = worklogBo.getTableList(p0100 ,"");
        		this.getFormHM().put("tabledata", tablemap1.get("tabledata"));
        		
        	}
        	//更新记录
        	if("updatelog".equalsIgnoreCase(flag)){
        		String p0100 = (String) this.getFormHM().get("p0100");
        		p0100 = p0100==null?"":p0100;
        		String record_num = (String) this.getFormHM().get("record_num");
        		record_num = record_num==null?"":record_num;
        		
				String field = (String) this.getFormHM().get("field");
				String value = (String) this.getFormHM().get("value");
				
				HashMap map = worklogBo.updateRecord(Integer.parseInt(p0100), Integer.parseInt(record_num), field, value);
	    		this.getFormHM().put("errorcode", map.get("errorcode"));
	    		
	    		HashMap tablemap1  = worklogBo.getTableList(p0100 ,"");
        		this.getFormHM().put("tabledata", tablemap1.get("tabledata"));
				
			}
			
        	//发布日志
        	if("publishlog".equalsIgnoreCase(flag)){
        		
        		String p0100 = (String) this.getFormHM().get("p0100");
        		p0100 = p0100==null?"":p0100;
        		String classId = (String) this.getFormHM().get("classId");//选中的日期
        		classId = classId==null?"":classId;
        		
				HashMap map = worklogBo.publishLog(Integer.parseInt(p0100), "0".equals(classId)?2:0);
	    		this.getFormHM().put("errorcode", map.get("errorcode"));
	    		
	    		HashMap tablemap1  = worklogBo.getTableList(p0100 ,"");
        		this.getFormHM().put("tabledata", tablemap1.get("tabledata"));
        		this.getFormHM().put("p0115", tablemap1.get("p0115"));
        		
			}
        	
        	//撤回日志
        	if("recalllog".equalsIgnoreCase(flag)){
        		
        		String p0100 = (String) this.getFormHM().get("p0100");
        		p0100 = p0100==null?"":p0100;
				HashMap map = worklogBo.recallLog(Integer.parseInt(p0100));
	    		this.getFormHM().put("errorcode", map.get("errorcode"));
	    		
	    		HashMap tablemap1  = worklogBo.getTableList(p0100 ,"");
        		this.getFormHM().put("tabledata", tablemap1.get("tabledata"));
        		this.getFormHM().put("p0115", tablemap1.get("p0115"));
				
			}
        	
        	//补填日志
        	if("filllog".equalsIgnoreCase(flag)){
        		String datevalue = (String) this.getFormHM().get("datevalue");//选中的日期
        		datevalue = datevalue==null?"":datevalue;
        		
        		String p0100 = (String) this.getFormHM().get("p0100");
        		p0100 = p0100==null?"":p0100;
        		String classId = (String) this.getFormHM().get("classId");//选中的日期
        		classId = classId==null?"":classId;
        		
				HashMap map = worklogBo.publishLog(Integer.parseInt(p0100), "0".equals(classId)?2:1);
	    		this.getFormHM().put("errorcode", map.get("errorcode"));
	    		
	    		HashMap tablemap1  = worklogBo.getTableList(p0100 ,datevalue);
        		this.getFormHM().put("tabledata", tablemap1.get("tabledata"));
        		this.getFormHM().put("p0115", tablemap1.get("p0115"));
        		this.getFormHM().put("flag", tablemap1.get("flag"));
        		
			}
			
        	//加载周计划表格  
        	if("all".equalsIgnoreCase(flag) || "oneweek".equalsIgnoreCase(flag)){
        		String datevalue = (String) this.getFormHM().get("datevalue");//选中的日期
        		datevalue = datevalue==null?"":datevalue;
        		if(datevalue == null || datevalue.length() <= 0){
        			datevalue = DateUtils.format(new Date(), "yyyy.MM.dd");
        		}
        		
        		if("1".equals(employeflag) && dateList.size()>0){
        			datevalue = (String) dateList.get(0);
        		}
        		
        		ArrayList tablelist = worklogBo.getWeekTableList(datevalue);
        		this.getFormHM().put("weekdata", tablelist);
        		if(tablelist.size()==0){
        			this.getFormHM().put("weekplan", 0);
        		}else{
        			this.getFormHM().put("weekplan", 1);
        		}
        	}
        	
        	//加载人力地图
			if("humanmap".equalsIgnoreCase(flag)){
				boolean binit = (Boolean) this.getFormHM().get("binit");
				
				WorkPlanBo bo = new WorkPlanBo(this.getFrameconn(), this.getUserView());
				if(binit){
					bo.setHumanMapType("2");
					bo.setNeedSeeSub("yes");
					String humanInfo =	bo.getHumanMap(true);
					String info = SafeCode.encode(humanInfo);
					this.getFormHM().put("info", info);
					
					String my_image = new PhotoImgBo(this.frameconn).getPhotoPathLowQuality(this.userView.getDbname(), this.userView.getA0100());
					this.getFormHM().put("my_image", my_image);
				} else{
					String subobjectid = (String) this.getFormHM().get("subobjectid");//团队成员 用于查看下级成员(下级成员负责岗位  用","连接)
					subobjectid = WorkPlanUtil.decryption(subobjectid);
					String subPersonFlag = (String) this.getFormHM().get("subpersonflag");//显示人力地图团队人员时下级岗位是否缺编缺编  
					String needSeeSub = (String) this.getFormHM().get("needSeeSub");//前台需要穿透查看计划关注人下级的标识
					String concerned_cur_page = String.valueOf(this.getFormHM().get("concerned_cur_page"));//?
					
					bo.setHumanMapType("2");

					bo.setSubObjectId(subobjectid);
					bo.setSubPersonFlag(subPersonFlag);
					bo.setNeedSeeSub(needSeeSub);
					bo.setHumanMap_cur_page(concerned_cur_page);
					
					String humanInfo =	bo.getHumanMap(false);
					String info = SafeCode.encode(humanInfo);
					this.getFormHM().put("info", info);
				}
        	}
			
			//导出
			if("export".equalsIgnoreCase(flag)){
				//选中的日期
        		String datevalue = (String) this.getFormHM().get("datevalue");
        		//耗时标识
        		String taskTime = (String) this.getFormHM().get("taskTime");
        		String p0100 = (String) this.getFormHM().get("p0100");
        		p0100 = p0100==null?"":p0100;
        		
				ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn());
				WeeklySummaryBo weeklySummaryBo = new WeeklySummaryBo(this.getFrameconn(), this.userView);// 工具类
				//bug 34862 haosl 2018-2-12
				String fullName = this.userView.getUserFullName();
				if(StringUtils.isEmpty(fullName))
					fullName = this.userView.getUserName();
				String fileName = fullName+"的"+datevalue+"号日志"+ ".xls";//根据规则生成Excel名称
				StringBuffer sql = new StringBuffer();
				ArrayList logHeadList = weeklySummaryBo.getHeadList("2", "1".equals(taskTime), "");;
				sql.append("select content, finish_desc, ");
				sql.append(Sql_switcher.dateToChar("start_time", "yyyy-MM-dd HH:mm")).append(" start_time,");
				sql.append(Sql_switcher.dateToChar("end_time", "yyyy-MM-dd HH:mm")).append(" end_time,");
				sql.append(" work_time, other_desc, work_type from per_diary_content where P0100 =").append(Integer.parseInt(p0100));
				ArrayList dataLogList = excelUtil.getExportData(logHeadList, sql.toString());
				excelUtil.exportExcel("工作日志",null, logHeadList, dataLogList, null, 1);
				
				excelUtil.exportExcel(fileName);// 导出表格
				this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));//表格名传进前台
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
	}
    
}
