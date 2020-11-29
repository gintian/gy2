package com.hjsj.hrms.transaction.workplan.plan_track;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanCommunicationBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskListBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class SelectUnfinishedTaskListTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
	    HashMap hm = this.getFormHM();
        String oprType=(String)hm.get("oprType");
        String periodType=WorkPlanUtil.nvl((String)hm.get("periodType"), "");           
        String periodYear =WorkPlanUtil.nvl((String)hm.get("periodYear"), "");
        String periodMonth =WorkPlanUtil.nvl((String)hm.get("periodMonth"), "");
        String periodWeek =WorkPlanUtil.nvl((String)hm.get("periodWeek"), ""); 
        //计划所有人
        String  objectid =WorkPlanUtil.nvl((String)hm.get("objectId"), "");            
        objectid=WorkPlanUtil.decryption(objectid); 
        
        String p0723 =WorkPlanUtil.nvl((String)hm.get("p0723"), "");               
        p0723="undefined".equals(p0723)?"":p0723;
        p0723=WorkPlanUtil.decryption(p0723); 
      
		try
		{
		    if ("selectUnFinishTask".equals(oprType)){//上期未完成任务
		    	WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.getFrameconn(), this.userView);
		    	if(StringUtils.isEmpty(periodMonth)
		    			&& StringUtils.isEmpty(periodYear)
		    			&& StringUtils.isEmpty(periodWeek)){
		    		Date now =new Date();
		    		int curYear = DateUtils.getYear(now); ;
		    		int curMonth = DateUtils.getMonth(now); 
		    		int[] weeks= workPlanUtil.getLocationPeriod(periodType,curYear,curMonth);
		    		periodYear = String.valueOf(weeks[0]);
		    		if (WorkPlanConstant.Cycle.WEEK.equals(periodType)){
		    			periodMonth = String.valueOf(weeks[1]);  
		    			periodWeek=String.valueOf(weeks[2]);
		    			
		    		}            
		    		else if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)        
		    				||WorkPlanConstant.Cycle.QUARTER.equals(periodType) 
		    				|| WorkPlanConstant.Cycle.MONTH.equals(periodType)){   
		    			periodMonth=String.valueOf(weeks[1]);
		    			
		    		}
		    	}
		        WorkPlanBo planBo= new WorkPlanBo(this.frameconn,this.userView);     
		        planBo.initPlan(objectid,p0723,periodType,periodYear,periodMonth,periodWeek) ; 
		        String p0700="0";
		        if (planBo.getP07_vo()!=null){
		            p0700 =planBo.getP07_vo().getString("p0700");            
                }
				
		        PlanTaskListBo taskListBo= new PlanTaskListBo(this.frameconn,Integer.parseInt(p0700), this.userView);
		        HashMap dataMap=taskListBo.getunFinishedTaskListMap();// 是否选择全部任务 chent 20160415
		        String model=(String)dataMap.get("dataModel");
		        String json=(String)dataMap.get("dataJson");
		        String jsonAll=(String)dataMap.get("dataJsonAll");
		        String columns=(String)dataMap.get("panelColumns");
		        //将期间参数重新传递到前台
		        this.getFormHM().put("periodYear", periodYear);
		        this.getFormHM().put("periodMonth", periodMonth);
		        this.getFormHM().put("periodWeek", periodWeek);
	            this.getFormHM().put("dataModel",SafeCode.encode(model));
	            this.getFormHM().put("dataJson",SafeCode.encode(json));
	            this.getFormHM().put("dataJsonAll",SafeCode.encode(jsonAll));
	            this.getFormHM().put("panelColumns",SafeCode.encode(columns));  
		    }
		    else if ("getSummary".equals(oprType)){//年度总结		
		        objectid =WorkPlanUtil.nvl((String)hm.get("objectid"), "");            
	            objectid=PubFunc.decryption(objectid); 
	            String planid =WorkPlanUtil.nvl((String)hm.get("planid"), "");            
	            planid=PubFunc.decryption(planid);
	         
		        getSummary(planid,objectid);
		        //下载文件
		   }
		  			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
    /**   
     * @Title: getSummary   
     * @Description: 考评打分显示总结   
     * @param @param p0723
     * @param @param objectid
     * @param @throws Exception 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    private void getSummary(String planid,String objectid) throws Exception {
        try
        {        
            ContentDAO dao = new ContentDAO(frameconn);
            WorkPlanUtil wputil = new WorkPlanUtil(frameconn, userView);
            PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
            WorkPlanSummaryBo wpsBo = new WorkPlanSummaryBo(this.userView,this.frameconn);
            
            ExamPlanBo planBo = new ExamPlanBo(this.frameconn,this.userView,planid);
            if (planBo.getPlanVo()==null){
                throw new Exception("找不到当前计划,可能已被删除！"); 
            }
            int state=4;//年报
            int year =Integer.parseInt(planBo.getPlanVo().getString("theyear"));
            String object_type =planBo.getPlanVo().getString("object_type");
            String p0723="1";
            if (!"2".equals(object_type)){
                p0723="2";
            }
            int cycle=planBo.getPlanVo().getInt("cycle");
            int month=1;
            int quater=1;
            
            switch (cycle) {
                case 0: { 
                    state=4;                 
                    break;
                }             
                case 1: { //半年
                    state=5; 
                    quater=Integer.parseInt(planBo.getPlanVo().getString("thequarter"));
                    break;
                }             
                case 2: { //季度
                    state=3;  
                    quater=Integer.parseInt(planBo.getPlanVo().getString("thequarter"));
                    break;
                }             
                case 3: { //月
                    state=2;  
                    month=Integer.parseInt(planBo.getPlanVo().getString("themonth"));
                    break;
                }             
                default:{
                    state=4;
                }       
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            final Calendar c = Calendar.getInstance();
            
            Date start = null; // 查询时间区间的起始
            Date end = null; // 查询时间区间的结尾
     
            String[] first = wpsBo.getSummaryDates(String.valueOf(state), year+"",  month+"", quater);
            start = sdf.parse(first[0]);
            end = sdf.parse(first[1]);
            
            String nbase = null;
            String a0100 = null;
            if ("2".equals(p0723)) { // 团队计划查询相应团队的负责人
                String director = wputil.getFirstDeptLeaders(objectid); // 当前计划位团队计划时有效，部门负责人
                if (director == null || "".equals(director)) {
                    nbase = "";
                    a0100 = "";
                } else {
                    nbase = director.substring(0, 3);
                    a0100 = director.substring(3);
                }
            } else if ("1".equals(p0723)) { // 个人计划
                nbase = "Usr";
                a0100 = objectid;
            } 
            
            
            String sql = "";
            if ("1".equals(p0723)) { // 个人计划查询个人周报
                sql = "SELECT * FROM P01 WHERE p0104>=? AND p0106<=? AND upper(nbase)=? AND a0100=? AND state=? AND (belong_type IS NULL OR belong_type=0) ORDER BY p0104 ASC";
                frowset = dao.search(sql, Arrays.asList(new Object[] {
                    new java.sql.Date(start.getTime()),
                    new java.sql.Date(end.getTime()),
                    nbase.toUpperCase(),
                    a0100,
                    new Integer(state)
                }));
            } else if ("2".equals(p0723)) { // 团队计划
                sql = "SELECT * FROM P01 WHERE p0104>=? AND p0106<=? AND e0122=? AND state=? AND belong_type=2 ORDER BY p0104 ASC";
                frowset = dao.search(sql, Arrays.asList(new Object[] {
                    new java.sql.Date(start.getTime()),
                    new java.sql.Date(end.getTime()),
                    objectid,
                    new Integer(state)
                }));
            }
            
            SimpleDateFormat cnSdf = new SimpleDateFormat("MM月dd日");
            List reportsApproved = new ArrayList(); // 数据库中存在的已批的记录,数据库中会出现断层，即某一周的记录不存在
            int p0100=0;
            String p0109="";
            if (frowset !=null && frowset.next()) {
                p0100 = frowset.getInt("p0100");       
                p0109 = WorkPlanUtil.nvl(frowset.getString("p0109"), ""); // 工作总结
            
            } 
            if (p0109.length()>0){
                LazyDynaBean report = new LazyDynaBean();
                report.set("hasSummary", "true");  
                report.set("summary", WorkPlanUtil.formatText(p0109));
                reportsApproved.add(report);    
            }
            else {
            //无工作总结
                LazyDynaBean report = new LazyDynaBean();
                report.set("hasSummary", "false");
                String type = null;
                // 查看周报的连接
                StringBuffer url = new StringBuffer();
                url.append("/workplan/work_summary.do?b_query=link");
                url.append("&cycle=").append(state);
                url.append("&year=").append(year);
                switch (state) {
                    case 1: { // 周
                        url.append("&month=").append(month);
                        url.append("&week=").append(quater);
                        break;
                    }
                    case 2: { // 月
                        url.append("&month=").append(month);
                        url.append("&week=").append(quater);
                        break;
                    }
                    case 3: { // 季
                        url.append("&week=").append(quater);
                        break;
                    }
                    case 4: { // 年
                        break;
                    }
                    case 5: { // 半年
                        url.append("&week=").append(quater);
                        break;
                    }
                    default: ;
                }
                url.append("&nbase=").append(WorkPlanUtil.encryption(nbase));
                url.append("&a0100=").append(WorkPlanUtil.encryption(a0100));
                if ("1".equals(p0723)) { // 个人计划,objectid为人员id
                    url.append("&belong_type=").append("0");
                    type = "person";
                } else if ("2".equals(p0723)) { // 团队计划,objectid为部门id
                    url.append("&belong_type=").append("2");
                    url.append("&e0122=").append(WorkPlanUtil.encryption(objectid));
                    type = "org";
                }
                url.append("&type=").append(type);
                report.set("viewUrl", url.toString());
                
                // 提醒的连接和提醒文字
                if (a0100.equals(this.userView.getA0100())) { // 自评
                    report.set("remindText", "去写工作总结");
                    report.set("remindUrl", url.toString());
                    report.set("type", "url"); // 类型为链接，表示跳转至总结界面填写总结
                } else  { // 其他人查看
                    StringBuffer js = new StringBuffer();
                    js.append("type=").append(type);
                    js.append("`cycle=").append(state);
                    js.append("`year=").append(year);
                    js.append("`month=").append(month);
                    js.append("`week=").append(quater);
                    js.append("`a0100=").append(WorkPlanUtil.encryption(nbase + a0100));
                    js.append("`e0122=").append("1".equals(p0723) ? WorkPlanUtil.encryption(objectid) : "");
                    report.set("remindUrl", js.toString());
                    report.set("remindText", "提醒写工作总结");
                    report.set("type", "param"); // 类型为参数，表示需要调用发送提醒邮件函数
                }
                reportsApproved.add(report); 
            }
            if (p0100>0)
                getSummaryFiles(String.valueOf(p0100));
            // 报告作者
            String fullName="";
            if (a0100!=null && a0100.length()>0){
                RecordVo authorVo = ptbo.getPersonByObjectId(nbase + a0100);
                if (authorVo == null) {
                    throw new Exception("查询失败");
                }
                fullName=authorVo.getString("a0101");
            }
            if("2".equals(p0723)){
                String deptDesc=wputil.getOrgDesc(objectid);
                fullName=deptDesc+"("+fullName+")";
            }
            String periodType="年";
            switch (state) {            
                case 2 : {periodType ="月";break;}
                case 3 : {periodType ="季";break; }       
                case 5 : {periodType ="半年";break;}
            }
            fullName=fullName+"的"+periodType+"度";
            if("2".equals(p0723)){
                fullName=fullName+ ("3".equals(object_type)?"单位":"部门");
            }
            fullName=fullName+"工作总结";
            LazyDynaBean author = new LazyDynaBean();
            author.set("fullName", fullName);   
            author.set("photo", new WorkPlanBo(frameconn, userView).getPhotoPath(nbase, a0100));
            formHM.put("author", author);  
            formHM.put("reports", reportsApproved);   
        } catch (Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
    
    /**   
     * @Title: getSummaryFiles   
     * @Description:取得总结附件    
     * @param @param p0100
     * @param @throws Exception 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    private void getSummaryFiles(String p0100) throws Exception {
        try
        {        
            WorkPlanCommunicationBo communicationBo = new WorkPlanCommunicationBo(this.frameconn,this.userView);  
            ArrayList resultList = communicationBo.queryAllMessage("3", p0100);
            
            ArrayList rsUpLoadFileList = new ArrayList();
            if(resultList.size()>0){
                for (int i = 0; i < resultList.size(); i++) {
                    ArrayList list = (ArrayList) resultList.get(i);
                    String msgId = (String) list.get(3);
                    msgId = WorkPlanUtil.decryption(msgId);                    
                    ArrayList arrList = communicationBo.queryAllUpLoadFile(msgId);
                    if (arrList.size() == 0)
                        continue;
                    LazyDynaBean bean = new LazyDynaBean();
                    bean.set("id", arrList.get(0));
                    bean.set("fileName", arrList.get(1));
                    bean.set("path", arrList.get(3));
                    
                    rsUpLoadFileList.add(bean);
                }
            }
            
            this.formHM.put("fileList", rsUpLoadFileList);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
}
