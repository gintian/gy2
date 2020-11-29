package com.hjsj.hrms.transaction.workplan.plan_track;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_track.WorkPlanHrBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkPlanHrMainTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        try {
            String oprType=(String)hm.get("oprType");
            //视图类型
            String viewType =(String)hm.get("viewType");
            viewType=(viewType==null)?"1":viewType;        
          //计划类型
            String planType =(String)hm.get("planType");
            planType=(planType==null)?"1":planType;        
            //计划期间 
            String periodType =(String)hm.get("periodType");
            periodType=(periodType==null)?"":periodType;                           
            String periodYear =(String)hm.get("periodYear");
            periodYear=(periodYear==null)?"":periodYear; 
            String periodMonth =(String)hm.get("periodMonth");
            periodMonth=(periodMonth==null)?"":periodMonth; 
            String periodWeek =(String)hm.get("periodWeek"); 
            periodWeek=(periodWeek==null)?"":periodWeek; 
            
            String submitType =(String)hm.get("submitType"); 
            submitType=(submitType==null)?"":submitType; 
            String curPage =(String)hm.get("curPage"); 
            curPage=(curPage==null)?"1":curPage; 
            
            String pageSize =(String)hm.get("pageSize"); 
            pageSize=(pageSize==null)?"10":pageSize; 
            
            String objectid =(String)hm.get("objectId");
            objectid=(objectid==null)?"":objectid;  
            objectid =SafeCode.decode(objectid);
            objectid=WorkPlanUtil.decryption(objectid); 
            
            String queryType =(String)hm.get("queryType");
            queryType=(queryType==null)?"":queryType;  
            
            
            String queryText =(String)hm.get("queryText");
            queryText=(queryText==null)?"":queryText;  
            queryText =SafeCode.decode(queryText);
            
            WorkPlanHrBo planHrBo= new WorkPlanHrBo(this.frameconn,this.userView);  
            planHrBo.setQueryType(queryType);
            planHrBo.setQueryText(queryText);
            // planHrBo.setQueryType("2");
            // planHrBo.setQueryText("e0122=010101`a0101=ddddd'");
            // planHrBo.setQueryText("集团");
            String subModuleId = (String)this.getFormHM().get("subModuleId");
            ArrayList<MorphDynaBean> items = (ArrayList<MorphDynaBean>)this.getFormHM().get("items");
            boolean hasTheFunction = this.userView.hasTheFunction("0KR02020101");
            hm.put("saveQuery", hasTheFunction); 
            if("workplan_hr_SubModuleId".equals(subModuleId)){
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
            	WorkPlanParseItemsBo parseBo = new WorkPlanParseItemsBo();
            	String queryString = parseBo.queryString(items,new WorkPlanUtil(this.frameconn,this.userView));
            	planHrBo.setQueryString(queryString);
            	String info = planHrBo.getCommonQueryDetailList(submitType, Integer.parseInt(pageSize),Integer.parseInt(curPage));
                hm.put("info", info); 
                hm.put("viewType", viewType); 
            }else if ("getPlanInfo".equals(oprType)){//初始进入计划界面              
                if ("".equals(periodType)){
                    planHrBo.initPlan(planType, viewType);
                }
                else {
                    planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek);
                }
                String info="false";
                if ("false".equals(planHrBo.getReturnInfo())){ //初始有错                   
                    info="false";                           
                }  
                else {                    
                    info=planHrBo.getPlanInfoList();
                }
                info= SafeCode.encode(info);
                hm.put("info", info); 
                hm.put("defaultQuery", planHrBo.getDefaultQuery());
                
                // 填报期间范围权限 chent 20170112 start
				WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(this.frameconn, this.userView);
	            List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
	            hm.put("plan_cycle_function", SafeCode.encode(JSONArray.fromObject(configList).toString()));
	            // 填报期间范围权限 chent 20170112 end
            }
            else if ("refreshPlanInfo".equals(oprType)){ //刷新计划                
               // WorkPlanHrBo planHrBo= new WorkPlanHrBo(this.frameconn,this.userView); 
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek);
                String info=planHrBo.getPlanInfoList();
                
                info= SafeCode.encode(info);
                hm.put("info", info);  
            }
            else if ("getDeatilList".equals(oprType)){ // 获取计划列表明细               
               // WorkPlanHrBo planHrBo= new WorkPlanHrBo(this.frameconn,this.userView,
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
           
                String info=planHrBo.getDetailList(submitType,Integer.parseInt(pageSize)
                        ,Integer.parseInt(curPage));
                info= SafeCode.encode(info);
                hm.put("info", info);  
            }
            else if ("checkIsCanReadPlan".equals(oprType)){//切换计划期间时 ，检查是否有权限查看   
                String locationCurWeek =(String)hm.get("locationCurWeek");
                locationCurWeek=(locationCurWeek==null)?"false":locationCurWeek;  
               // WorkPlanHrBo planHrBo= new WorkPlanHrBo(this.frameconn,this.userView); 
                if("true".equals(locationCurWeek)){
                    planHrBo.LocationCurPeriodPlan(periodType,periodYear,periodMonth,periodWeek) ;
                    hm.put("periodType", planHrBo.getPeriodType());  
                    hm.put("periodYear", planHrBo.getPeriodYear());  
                    hm.put("periodMonth", planHrBo.getPeriodMonth());  
                    hm.put("periodWeek", planHrBo.getPeriodWeek());  
                    hm.put("weekNum", planHrBo.getWeekNum()+"");  
                }           
                hm.put("info", "true");  
            }    
            else if ("remindTeam".equals(oprType)){//提醒制定、批准计划   
                if ("".equals(objectid)){
                   // WorkPlanHrBo planHrBo= new WorkPlanHrBo(this.frameconn,this.userView);  
                    planHrBo.remindMyTeamToSubmitPlan(planType,periodType,periodYear,
                            periodMonth,periodWeek,submitType) ; 
                }
                else {
                    WorkPlanBo planBo= new WorkPlanBo(this.frameconn,this.userView); 
                    planBo.remindSubmitPlan(planType,periodType,periodYear,periodMonth,periodWeek,objectid,submitType) ; 
                }
                String info="true";                                     
                hm.put("info", info);  
            }  
/***************************以下为关联计划相关 **************************************/          
            else if ("checkIsApproved".equals(oprType)){//检查计划是否有批准的         
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
                hm.put("info", "false");
                if (planHrBo.checkHaveIsApprovedPlan()){
                    hm.put("info", "true");
                }
            }  
            else if ("checkSuperBodyType".equals(oprType)){//检查是否设置了上级 上上级
                String plan_id =(String)hm.get("planId"); 
                String objectIds =(String)hm.get("objectIds");  
                objectIds=(objectIds==null)?"":objectIds;
                String [] arrObjectIds= objectIds.split(",");
                boolean bAll=true;
                for (int i=0;i<arrObjectIds.length;i++){
                    String objectid1 = WorkPlanUtil.decryption(arrObjectIds[i]);
                    if (objectid1.length()>0){
                        bAll=false;
                        arrObjectIds[i]=objectid1;
                    }
                }
                hm.put("info", "true");
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
                String info =planHrBo.checkHaveSuperBodyType(plan_id,arrObjectIds,bAll);
                if (info.length()>0){
                    hm.put("info", "false");
                    hm.put("bodySet", info);
                }
            }  
            
            else if ("checkIsRelated".equals(oprType)){//检查是否关联 
                String objectIds =(String)hm.get("objectIds");  
                objectIds=(objectIds==null)?"":objectIds;
                String [] arrObjectIds= objectIds.split(",");
                boolean bAll=true;
                for (int i=0;i<arrObjectIds.length;i++){
                    String objectid1 = WorkPlanUtil.decryption(arrObjectIds[i]);
                    if (objectid1.length()>0){
                        bAll=false;
                        arrObjectIds[i]=objectid1;
                    }
                }
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
                HashMap map= new HashMap();
                boolean b =planHrBo.checkIsRelatedPlan(arrObjectIds,bAll,map);
                if (b){                    
                    hm.put("info", "false");
                    hm.put("objectName",(String)map.get("A0101s"));
                    hm.put("objectCount", (String)map.get("count"));
                } 
                else {
                    hm.put("info", "true");
                }
            }  
            else if ("checkTemplate".equals(oprType)){//检查模板是否能修改 
                String reRelalte =(String)hm.get("reRelalte");  
                String plan_id =(String)hm.get("planId");  
                String objectIds =(String)hm.get("objectIds");  
                objectIds=(objectIds==null)?"":objectIds;
                String [] arrObjectIds= objectIds.split(",");
                boolean bAll=true;
                for (int i=0;i<arrObjectIds.length;i++){
                    String objectid1 = WorkPlanUtil.decryption(arrObjectIds[i]);
                    if (objectid1.length()>0){
                        bAll=false;
                        arrObjectIds[i]=objectid1;
                    }
                }
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
                HashMap map= new HashMap();
                boolean b =planHrBo.checkCanEditTemplate(plan_id,arrObjectIds,bAll,map,
                        "true".equals(reRelalte));
                if (!b){                    
                    hm.put("info", "false");
                    hm.put("taskDesc",(String)map.get("taskDesc"));
                } 
                else {
                    hm.put("info", "true");
                }
            }  
            else if ("relatePlan".equals(oprType)){//关联计划
                String reRelalte =(String)hm.get("reRelalte");  
                String plan_id =(String)hm.get("planId");  
                String objectIds =(String)hm.get("objectIds");  
                objectIds=(objectIds==null)?"":objectIds;
                String [] arrObjectIds= objectIds.split(",");
                boolean bAll=true;
                for (int i=0;i<arrObjectIds.length;i++){
                    String objectid1 = WorkPlanUtil.decryption(arrObjectIds[i]);
                    if (objectid1.length()>0){
                        bAll=false;
                        arrObjectIds[i]=objectid1;
                    }
                }
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
                HashMap map= new HashMap();
                boolean b = planHrBo.relatePlan(plan_id,arrObjectIds,bAll,map,"true".equals(reRelalte));
                if (!b){                    
                    hm.put("info", "false");
                } 
                else {
                    hm.put("info", "true");
                }
            }  
            else if ("updateTargetCard".equals(oprType)){//更新目标卡  
                WorkPlanBo planBo= new WorkPlanBo(this.frameconn,this.userView); 
                planBo.initPlan(objectid, planType, periodType,periodYear,periodMonth,periodWeek);
                RecordVo p07vo= planBo.getP07_vo();
                hm.put("info", "true");
                if (p07vo!=null){
                    String p0700= p07vo.getString("p0700");
                    String khplan_id= p07vo.getString("relate_planid");
                    if (khplan_id!=null && khplan_id.length()>0){
                        if (!planHrBo.isExistsPlan(khplan_id)){
                            hm.put("info", "false");
                            hm.put("desc", "关联计划已被删除，不能更新！");
                            return;
                        }
                        
                        //检查是否能修改模板                       
                        String [] arrObjectIds= objectid.split(",");
                        HashMap map= new HashMap();
                        planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
                        boolean b =planHrBo.checkCanEditTemplate(khplan_id,arrObjectIds,false,map,
                                true);
                        if (!b){                    
                            hm.put("info", "editTemplate");
                            hm.put("taskDesc",(String)map.get("taskDesc"));
                        } 
                        else {
                            if ("1".equals(planType)){
                                objectid=objectid.substring(3);
                            }
                            boolean b1 = planHrBo.updateTargetCard(khplan_id, objectid, planType, p0700);
                            if (!b1){                    
                                hm.put("info", "false");
                            } 
                        }
                        
                        // 更新完成后需要校验权重或总分之和是否超限 lium
                        formHM.put("planId", khplan_id);
                    }
                }
            }  
            else if ("batchUpdateTargetCard".equals(oprType)){//批量更新目标卡            	
                String objectIds =(String)hm.get("objectIds");  
                objectIds=(objectIds==null)?"":objectIds;
                String [] arrObjectIds= objectIds.split(",");
                boolean bAll=true;
                for (int i=0;i<arrObjectIds.length;i++){
                    String objectid1 = WorkPlanUtil.decryption(arrObjectIds[i]);
                    if (objectid1.length()>0){
                        bAll=false;
                        arrObjectIds[i]=objectid1;
                    }
                }
                planHrBo.initPlan(planType, viewType,periodType,periodYear,periodMonth,periodWeek); 
                HashMap planMap = planHrBo.batchUpdateTargetCard(arrObjectIds,bAll);                
                hm.put("planIds", (String)planMap.get("planIds"));
   
            }  
            else if ("getP08TaskInfo".equals(oprType)){  
                 HashMap  params =getPlanEvalParam();   
                 
                 hm.put("p0800", WorkPlanUtil.encryption((String) params.get("p0800")));
                 hm.put("p0700",WorkPlanUtil.encryption((String) params.get("p0700")));
                 hm.put("p0723", WorkPlanUtil.encryption((String)params.get("p0723")));
                 hm.put("objectid", WorkPlanUtil.encryption((String)params.get("objectid")));
               
            }  
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
 
    }

    
    private HashMap getPlanEvalParam() {
        HashMap params= new HashMap();
        try {
            String p0800 = (String) (formHM.get("p0800") == null ? "" : formHM.get("p0800"));
            String p0400 = (String) (formHM.get("p0400") == null ? "" : formHM.get("p0400"));
            p0400 = WorkPlanUtil.decryption(p0400);
            p0800 = WorkPlanUtil.decryption(p0800);
            PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
            RecordVo vo=  bo.getTask(Integer.parseInt(p0800));
            int p0700= vo.getInt("p0700");
            ContentDAO dao = new ContentDAO(this.frameconn);
            ArrayList paramList =new ArrayList();
            paramList.add(Integer.valueOf(p0400));
            String sql="select * from p04 where p0400= ?";
            RowSet rs = dao.search(sql,paramList);
            String p0723="1";
            String objectid="";
            if (rs.next()) {
                String nbase = rs.getString("nbase");
                String a0100 = rs.getString("a0100");
                objectid=a0100;
                WorkPlanBo planBo = new WorkPlanBo(this.frameconn,this.userView);
                planBo.initPlan(p0700);
                RecordVo p07vo= planBo.getP07_vo();
                if (nbase==null || nbase.length()<1){
                    p0723="2";
                }
                else {
                    objectid=nbase+a0100; 
                    p07vo = planBo.getPeoplePlanVo(p07vo, objectid);
                }
                p0700=p07vo.getInt("p0700");
            }
            if (p0700>0) {
                params.put("p0700", p0700+"");
            }
            if (!"".equals(p0800)) {
                params.put("p0800", p0800);
            }
            if (!"".equals(p0723)) {
                params.put("p0723", p0723);
            }
            if (!"".equals(objectid)) {
                params.put("objectid", objectid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        
        return params;
    }
}


