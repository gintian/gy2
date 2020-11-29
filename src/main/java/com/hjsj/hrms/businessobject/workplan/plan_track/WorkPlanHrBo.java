package com.hjsj.hrms.businessobject.workplan.plan_track;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>Title:WorkPlanHrBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-10-14 上午10:27:08</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class WorkPlanHrBo {
    /**
     * 
     */
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao; 
    private WorkPlanUtil workPlanUtil;
    private WorkPlanBo planBo;
    private DbWizard dbw;
   
    private String viewType; //视图类型 1 计划视图 2 任务视图
    private String planType; //查看的计划类型 1 人员 2 部门
    private String periodType;//期间类型
    private String periodYear;//年
    private String periodMonth;//月 根据期间类型不同代码月份、季度、上半年
    private String periodWeek;//周
    private int weekNum;//本月周数
    
    private String queryText;//快速搜索字符串 普通查询字符串以`分隔
    private String queryType;//查询类型 1：快速搜索  2：普通搜索   
    private ArrayList commonQueryParamList;//普通搜索参数
    
    private ArrayList templateItemList;//关联模板项目列表
    private String  templateId ="";//关联模板id
 
    private String returnInfo; //返回值 其它类调用 存储需返回的信息

    private ArrayList fScoreList=new ArrayList();//直接上级对当前人的评分记录 临时存储
    private ArrayList fStandardGradeList =new ArrayList();//指标标准标度
    private String queryString;//公共查询语句
    public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	/**与计划无关
     * @param conn
     * @param userview
     */
    public WorkPlanHrBo(Connection conn,UserView userview) {
        this.conn=conn;
        this.userView=userview; 
        createPublicVar();
    }
    /**
     * @param conn
     * @param userview
     * @param plantype 区分个人计划 部门计划
     */
    public WorkPlanHrBo(Connection conn,UserView userview,String plantype) {
        this.conn=conn;
        this.userView=userview; 
        createPublicVar(); 
        
        String periodtype=WorkPlanConstant.Cycle.YEAR;
        Date now =new Date();
        int curYear = DateUtils.getYear(now); ;
        int curMonth = DateUtils.getMonth(now);  
    
        initPlan(plantype,"1",periodtype,
                String.valueOf(curYear), String.valueOf(curMonth),"1");
    }
  
    
    
    /**
     * @param conn
     * @param userview
     * @param plantype 
     * @param viewtype 计划视图 任务视图
     * @param periodtype
     * @param periodyear
     * @param periodmonth
     * @param periodweek
     */
    public WorkPlanHrBo(Connection conn,UserView userview,
            String plantype, String viewtype,
            String periodtype,String periodyear,String periodmonth,String periodweek) {
        this.conn=conn;
        this.userView=userview; 
        createPublicVar();
        planBo=new WorkPlanBo(this.conn,this.userView); 
        initPlan(plantype,viewtype,
                periodtype, periodyear,periodmonth,periodweek);
       
    }
    
    /**   
     * @Title: createPublicVar   
     * @Description:初始化公用变量    
     * @param  
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public void createPublicVar() {
        dbw = new DbWizard(this.conn);
        dao=new ContentDAO(this.conn);
        workPlanUtil=new WorkPlanUtil(this.conn,this.userView); 
        planBo=new WorkPlanBo(this.conn,this.userView); 
        commonQueryParamList = new ArrayList();

    }
    /**
     * @throws GeneralException    
     * @Title: initPlan   
     * @Description:默认定位年计划    
     * @param @param plan_type
     * @param @param view_type 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public void initPlan(String plan_type,String view_type) throws GeneralException {
        String periodtype="";
        
        // 填报期间范围权限 chent 20170112 start
        try {
	        WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(this.conn, this.userView);
	        List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
	        String configStr = JSONArray.fromObject(configList).toString();
	        //没有配置年计划时，默认定位到第一个配置的区间上。
	        if(configStr.indexOf("p0") >-1){
	        	periodtype=WorkPlanConstant.Cycle.YEAR;
	        }else if(configStr.indexOf("p1") > -1){
        		periodtype=WorkPlanConstant.Cycle.HALFYEAR;
        	} else if(configStr.indexOf("p2") > -1){
        		periodtype=WorkPlanConstant.Cycle.QUARTER;
        	} else if(configStr.indexOf("p3") > -1){
        		periodtype=WorkPlanConstant.Cycle.MONTH;
        	} else if(configStr.indexOf("p4") > -1){
        		periodtype=WorkPlanConstant.Cycle.WEEK;
        	}else{
        		throw new Exception("未启用任何类型的工作计划，暂无法查看！");
        	}

        } catch(Exception e) {
        	throw GeneralExceptionHandler.Handle(e);
        }
     	// 填报期间范围权限 chent 20170112 end
        
        Date now =new Date();
        int curYear = DateUtils.getYear(now); ;
        int curMonth = DateUtils.getMonth(now);  
        initPlan(plan_type,view_type,periodtype,
                String.valueOf(curYear), String.valueOf(curMonth),"1");
        
    }
    
    /**   
     * @Title: initPlan   
     * @Description: 初始计划所需参数   
     * @param @param conn
     * @param @param userview
     * @param @param viewtype
     * @param @param plantype
     * @param @param periodtype
     * @param @param periodyear
     * @param @param periodmonth
     * @param @param periodweek 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public void initPlan(String plantype,String viewtype, 
            String periodtype,String periodyear,String periodmonth,String periodweek) {
        this.viewType=viewtype;
        this.planType=plantype;
        this.periodType=periodtype;
        this.periodYear=periodyear;
        this.periodMonth=periodmonth;
        if (periodweek==null || "null".equals(periodweek)) {
            periodweek="1";
        }
        this.periodWeek=periodweek;
        
 }
    
    
    /**   
     * @Title: LocationCurPeriodPlan   
     * @Description: 定位当前期间的计划 如果选当前年份，自动定位当前月、季度、半年，如果选当前月份，自动定位当前周
     *  @param period_type 期间类型
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
     */
    public void LocationCurPeriodPlan(String periodtype,String periodyear,String periodmonth,String periodweek) {
        this.periodType=periodtype;
        Date now = new Date();
        int curYear = DateUtils.getYear(now);
        int curMonth = DateUtils.getMonth(now);
        if (periodyear==null || "".equals(periodyear)){
            periodyear =String.valueOf(curYear);                
        }
        if (periodmonth==null || "".equals(periodmonth)|| "null".equals(periodmonth)){
            periodmonth =String.valueOf(curMonth);                
        }
        
        this.periodYear = periodyear; 
        this.periodMonth=periodmonth;
        this.periodWeek=periodweek;
        if (WorkPlanConstant.Cycle.YEAR.equals(this.periodType)){ 
            
        }  
        else {
            int year=Integer.parseInt(periodyear);
            int month=Integer.parseInt(periodmonth);
            this.weekNum=4;
            int[] weeks= new WorkPlanUtil(this.conn,this.userView).getLocationPeriod(this.periodType,year,month);
            if (WorkPlanConstant.Cycle.WEEK.equals(this.periodType)){ 
                this.periodMonth= String.valueOf(weeks[1]);               
                this.periodWeek=String.valueOf(weeks[2]);
                this.weekNum=workPlanUtil.getWeekNum(this.periodYear, this.periodMonth);
            }            
            else if (WorkPlanConstant.Cycle.HALFYEAR.equals(this.periodType)        
                ||WorkPlanConstant.Cycle.QUARTER.equals(this.periodType) 
                        || WorkPlanConstant.Cycle.MONTH.equals(this.periodType)){   
                this.periodMonth=String.valueOf(weeks[1]);
                               
            }
        }
    }

    /**   
     * @Title: getPlanInfoList   
     * @Description: 获取计划信息   
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getPlanInfoList() {
        String planInfo="{"+quotedDoubleValue("hjsj")+":"+quotedDoubleValue("1");
        planInfo =planInfo+","+getPublicInfo();              
        //显示计划视图表头
        planInfo =planInfo+","+getMyTeamInfoTitle(false);        
        planInfo=planInfo+"}";
        String info="{"+quotedDoubleValue("planinfo")+":"+planInfo+"}";    

        return info;
        
    }
    
    /**   
     * @Title: getPublicInfo   
     * @Description: 获取计划头信息 计划类型 计划期间   
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getPublicInfo() {
        String planInfo="";
        planInfo=planInfo+quotedDoubleValue("period_type_name")+":"
            +quotedDoubleValue(workPlanUtil.getPlanPeriodTypeDesc(this.periodType))         
            +","
            +quotedDoubleValue("period_type")+":"+quotedDoubleValue(this.periodType)                           
            +","
            +quotedDoubleValue("period_year")+":"+quotedDoubleValue(this.periodYear)            
            +","
            +quotedDoubleValue("period_month")+":"+quotedDoubleValue(this.periodMonth)            
            +","
            +quotedDoubleValue("period_week")+":"+quotedDoubleValue(this.periodWeek)            
            +"";
         
        this.weekNum=4;//本月周数
        if (WorkPlanConstant.Cycle.WEEK.equals(this.periodType)){
            this.weekNum=workPlanUtil.getWeekNum(this.periodYear, this.periodMonth);
        }
        planInfo =planInfo+","+quotedDoubleValue("week_num")+":"
            +quotedDoubleValue(String.valueOf(this.weekNum));    
        planInfo=quotedDoubleValue("public_info")+":{"+planInfo+"}"; 
        return planInfo;
        
    }
    
    
    /**   
     * @Title: getMyTeamInfoTitle   
     * @Description: 获取人员总数
     * queryFlag：是否按查询组件条件查询
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getMyTeamInfoTitle(boolean queryFlag) {
        String info="";
        try{
            int sum_count=0;//总数
            int approve_count=0;//已批准
            int submit_count=0;//已提交
            int unsubmit_count=0;//未提交
            int change_count=0;//已变更
            RowSet rset=null;
            String strsql="";
            ArrayList paramList = new ArrayList();
            paramList.clear();
            strsql=getCountSqlByPlanStatus("",paramList);
            //linbz  20160920 按查询组件条件查询需加上该查询条件
            if(this.getQueryString()!=null && queryFlag){
            	strsql=strsql+this.getQueryString();
            }
            if(strsql.trim().length() > 0){
            	rset=dao.search(strsql,paramList);
	            if (rset.next()){               
	                sum_count= rset.getInt(1);
	            }
	            paramList.clear();
            }
	        strsql=getCountSqlByPlanStatus("0",paramList);
	        if(this.getQueryString()!=null && queryFlag){
            	strsql=strsql+this.getQueryString();
            }
	        if(strsql.trim().length() > 0){
	        	rset=dao.search(strsql,paramList);
	            if (rset.next()){               
	                unsubmit_count= rset.getInt(1);
	            }
	            paramList.clear();
	        }
            strsql=getCountSqlByPlanStatus("1",paramList);
            if(this.getQueryString()!=null && queryFlag){
            	strsql=strsql+this.getQueryString();
            }
            if(strsql.trim().length() > 0){
	            rset=dao.search(strsql,paramList);
	            if (rset.next()){               
	                submit_count= rset.getInt(1);
	            }
	            paramList.clear();
            }
            strsql=getCountSqlByPlanStatus("2",paramList);
            if(this.getQueryString()!=null && queryFlag){
            	strsql=strsql+this.getQueryString();
            }
            if(strsql.trim().length() > 0){
	            rset=dao.search(strsql,paramList);
	            if (rset.next()){               
	                approve_count= rset.getInt(1);
	            }
	            paramList.clear(); 		//修改 	haosl	20160627
            }
            strsql=getCountSqlByPlanStatus("4",paramList);//已变更 chent 20160415
            if(this.getQueryString()!=null && queryFlag){
            	strsql=strsql+this.getQueryString();
            }
            if(strsql.trim().length() > 0){
            	rset=dao.search(strsql,paramList);
            	if (rset.next()){               
            		change_count= rset.getInt(1);
            	}
            	paramList.clear();		//修改 	haosl	20160627
            }
            
            
            String orgDesc="2".equals(this.planType)?"部门":"";
            info =quotedDoubleValue("team_title")+":"+quotedDoubleValue(""
                    + workPlanUtil.getPlanPeriodDesc(periodType, periodYear, periodMonth, periodWeek) 
                    +orgDesc+"工作计划提交情况:") 
            +","
            +quotedDoubleValue("sum_count")+":"+quotedDoubleValue(sum_count+"") 
            +","
            +quotedDoubleValue("submit_count")+":"+quotedDoubleValue(submit_count+"") 
            +","
            +quotedDoubleValue("approve_count")+":"+quotedDoubleValue(approve_count+"") 
            +","
            +quotedDoubleValue("unsubmit_count")+":"+quotedDoubleValue(unsubmit_count+"") 
            +","
            +quotedDoubleValue("change_count")+":"+quotedDoubleValue(change_count+"") 
            +"";
            
          
        }
        catch(Exception e){           
            e.printStackTrace();  
        }
        info=quotedDoubleValue("teaminfo_title")+":{"+info+"}";       
        info=quotedDoubleValue("detail_info")+":{"+info+"}"; 
        return info; 
    } 
    
    /**   
     * @Title: getDetailList   
     * @Description:获取计划列表明细    
     * @param @param submit_type
     * @param @param page_size
     * @param @param cur_page
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getDetailList(String submit_type,int page_size,int cur_page) {
        String planInfo="{"+quotedDoubleValue("hjsj")+":"+quotedDoubleValue("1");
        String teamMainInfo="";
        try{
            if ("1".equals(this.viewType)){
                if ("2".equals(this.planType)){
                    teamMainInfo=getMySubDeptMainInfo(submit_type,page_size,cur_page);                        
                }
                else {
                    teamMainInfo=getMyTeamPeopleMainInfo(submit_type,page_size,cur_page);                        
                }
            }
            else {
                teamMainInfo=this.getPlanTaskList(submit_type,page_size,cur_page);   
            }
            if (teamMainInfo.length()>0) { //任务视图直接返回， 再包一层不能读取      
                planInfo =planInfo+","+teamMainInfo;
                if ("2".equals(this.viewType)){
                    return "{"+teamMainInfo+"}";
                }
            }
        }     
        catch(Exception e){
            e.printStackTrace();
        }
   
        planInfo=planInfo+"}";
        String info="{"+quotedDoubleValue("planinfo")+":"+planInfo+"}";    
        
        return info; 
    } 
    
    /**   
     * @Title: getTeamPaginationList   
     * @Description: 数据分页   
     * @param @param submit_type
     * @param @param page_size
     * @param @param cur_page
     * @param @param valueMap
     * @param @param paramList
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getTeamPaginationList(String submit_type,int page_size,
            int cur_page,HashMap valueMap) {
        ArrayList pageList = new ArrayList();
        try{
            ArrayList dataList= getPlanDataList(submit_type);
            int pagecount = 0;
            pagecount = dataList.size();
            if (page_size<1) {
                page_size=10;
            }
            int maxPage=(pagecount/page_size);
            if ((pagecount% page_size)>0){
                maxPage++;
            }
            if (cur_page>1){//是否大于最大页
                if (maxPage>0 && (cur_page>maxPage)){
                    cur_page=maxPage; 
                }
                else  if (maxPage==0){
                    cur_page=1;
                }
            }
            else if (cur_page==-1) {//取最大页
                if (maxPage == 0) {
                    cur_page = 1 ;
                } else {
                    cur_page=maxPage;
                }
            }
            else if (cur_page==0) {//如果当前输入的页码为0，则默认设置为1
            	    cur_page = 1 ;
            }

            int begin_index = (cur_page - 1) * page_size;
            int end_index = cur_page * page_size;
            if (end_index > pagecount) {
                end_index = pagecount;
            }
            valueMap.put("curPage", cur_page+"");
            valueMap.put("sumCount", pagecount+"");
            valueMap.put("sumPage", maxPage+"");
            for (int i = begin_index; i < end_index; i++) {
                LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
                pageList.add(bean);
            }
              
        }
        catch(Exception e){           
            e.printStackTrace();  
        }
        return pageList;
        
    }
    
    /**   
     * @Title: getMyTeamPeopleMainInfo   
     * @Description: 获取个人计划列表   
     * @param @param submitType 提交类型
     * @param @param pageSize 每页显示条数
     * @param @param curPage 第几页
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getMyTeamPeopleMainInfo(String submit_type,int page_size,int cur_page) {
        String info="";
        HashMap valueMap = new HashMap();
        ArrayList paramList = new ArrayList();
       // String
        // strsql=getTeamPaginationSql(submit_type,page_size,cur_page,valueMap,paramList);
        ArrayList dataList= getTeamPaginationList(submit_type,page_size,cur_page,valueMap);
        if (dataList.size()<1){
            //return quotedDoubleValue("teaminfo_detail")+":{"+info+"}"; 
        }     
        boolean  brelate=false;
        if (dbw.isExistField("p07", "relate_planid", false)){
            brelate=true;
        }
        
      //个人计划下权重合计
        StringBuffer sbf = new StringBuffer();
        sbf.append("select sum(ptm.rank) as total_rank from p08,per_task_map ptm,p07");
        sbf.append(" where ptm.p0700=p07.p0700 ");
        sbf.append(" and ptm.p0800=p08.p0800");
        sbf.append(" and p07.p0723 in (1,2)");
        sbf.append(" and ptm.nbase=? and ptm.a0100=?");
        sbf.append(" and ((p07.p0723=1 and (( ptm.flag<>5 and p08.p0811 in ('02','03')) or ptm.flag=5) ) or ( p07.p0723=2 and ( ptm.flag<>5 and  p08.p0811 in ('02','03'))))");
        sbf.append(" and p08.p0809<>5");
        sbf.append(" and p07.p0725 = " + this.periodType);
        sbf.append(" and p07.p0727= " + this.periodYear);
        if (!WorkPlanConstant.Cycle.YEAR.equals(this.periodType)){            
            sbf.append(" and p07.p0729 = " + this.periodMonth);
        }        
        if (WorkPlanConstant.Cycle.WEEK.equals(this.periodType)){
            sbf.append(" and p07.p0731 = " + this.periodWeek);
        }
        RowSet rs = null;
        try{            
         
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
                String _nbase= (String)bean.get("nbase");
                String _a0100= (String)bean.get("a0100");
                rs = null;
                rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{_nbase, _a0100}));
                String total_rank = "0%";
                if(rs.next()){
                	if(rs.getFloat("total_rank")>0){
                		float str = rs.getFloat("total_rank")*100;
                		//total_rank = String.valueOf(str);//不能简单截位。因为精度丢失,sum有可能是99.999999。应该是100,简单截位的话就是99了。  chent 20160906 delete 
                		//total_rank = total_rank.substring(0, total_rank.indexOf(".")) + "%";
                		total_rank = String.valueOf(new BigDecimal(str).setScale(0, BigDecimal.ROUND_HALF_UP))+"%";// 算出的数据四舍五入下，纠正一下因为精度丢失造成的数据不准确。chent 20160906 add
                	}
                }
                String _a0101= (String)bean.get("a0101");              
                String _e0122= (String)bean.get("e0122");
                String _e01a1= (String)bean.get("e01a1");
                String _p07e01a1= (String)bean.get("p07e01a1");                
                if (_p07e01a1!=null && _p07e01a1.length()>0){
                    _e01a1= _p07e01a1;              
                    _e0122= (String)bean.get("p07e0122");                    
                }
                String _p0719= (String)bean.get("p0719");
                String appstatus="未提交";
                String submitType="0";
                String remind="提醒写工作计划";
                if (_p0719 == null) {
                    appstatus = workPlanUtil.getPlanStatusDesc("");
                } else if ("0".equals(_p0719)||"3".equals(_p0719)) {// 起草 驳回
                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
                } else if ("1".equals(_p0719)) {// 发布
                    submitType="1";
                    appstatus = "已提交";                    
                    remind="提醒批准工作计划";
                }
                else if ("2".equals(_p0719)) {// 批准
                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
                    if(new WorkPlanBo(conn, userView).isStateChange(bean.get("p0700").toString())){
                    	appstatus = "已变更";
                    }
                    submitType="2";
                    remind="关联到考核计划";
                    if (brelate){
                        String relate_planid =(String)bean.get("relate_planid");
                        if (relate_planid!=null && relate_planid.length()>0){
                            submitType="3";
                            remind="更新到目标卡";
                        }
                    }
                }    
                String visbleBox="false";
                if ("2".equals(submitType)||"3".equals(submitType)){
                    if (_nbase!=null &&"USR".equalsIgnoreCase(_nbase)) {
                        visbleBox="true";
                    }
                }
                String imagepath =planBo.getPhotoPath(_nbase, _a0100);        
                String dept_desc =AdminCode.getCodeName("UM",_e0122);  
                String e01a1desc=AdminCode.getCodeName("@K",_e01a1);
                if (e01a1desc.length()>0){
                    dept_desc=dept_desc+" ("+e01a1desc+")";                     
                }
          
                String stritem="{"
                    +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(_nbase+_a0100))    
                    +","
                    +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
                    +","
                    +quotedDoubleValue("name")+":"+quotedDoubleValue(_a0101)
                    +","
                    +quotedDoubleValue("dept_desc")+":"+quotedDoubleValue(dept_desc)
                     +","
                    +quotedDoubleValue("appstatus")+":"+quotedDoubleValue(appstatus)
                    +","
                    +quotedDoubleValue("submittype")+":"+quotedDoubleValue(submitType)
                    +","
                    +quotedDoubleValue("remind")+":"+quotedDoubleValue(remind)
                    +","
                    +quotedDoubleValue("p0723")+":"+quotedDoubleValue(WorkPlanUtil.encryption("1"))
                    +","
                    +quotedDoubleValue("total_rank")+":"+quotedDoubleValue(total_rank)
                    +","
                    +quotedDoubleValue("visbleBox")+":"+quotedDoubleValue(visbleBox)
                   +"}"
                    ;
                if ("".equals(info)){                    
                    info = stritem;
                }
                else {
                    info =info+","+ stritem;                    
                }
            }
            info=quotedDoubleValue("detail_list")+":["+info+"]"; 
            info=info+","+quotedDoubleValue("cur_page")+":"+valueMap.get("curPage"); 
            info=info+","+quotedDoubleValue("sum_page")+":"+valueMap.get("sumPage"); 
            info=info+","+quotedDoubleValue("sum_count")+":"+valueMap.get("sumCount"); 
        }
        catch(Exception e){           
            e.printStackTrace();  
        }
        info=quotedDoubleValue("teaminfo_detail")+":{"+info+"}"; 
      
        return info;
    }    
    
    
    
    /**   
     * @Title: getMySubDeptMainInfo   
     * @Description: 获取部门计划列表   
     * @param @param submitType 提交类型
     * @param @param pageSize 每页显示条数
     * @param @param curPage 第几页
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getMySubDeptMainInfo(String submit_type,int page_size,int cur_page) {
        String info="";
        HashMap valueMap = new HashMap();
        ArrayList paramList= new ArrayList();
        ArrayList dataList= getTeamPaginationList(submit_type,page_size,cur_page,valueMap);
        if (dataList.size()<1){
          //  return quotedDoubleValue("teaminfo_detail")+":{"+info+"}"; 
        }    

        try {
            boolean brelate=false;
            if (dbw.isExistField("p07", "relate_planid", false)){
                brelate=true;
            }
            
            //部门权重合计
            StringBuffer sbf = new StringBuffer();
            sbf.append("select sum(ptm.rank) as total_rank from p08,per_task_map ptm,p07");
            sbf.append(" where ptm.p0700=p07.p0700");
            sbf.append(" and ptm.p0800=p08.p0800");
            sbf.append(" and p07.p0723 in (1,2)");
            sbf.append(" and ptm.org_id=?");
            sbf.append(" and (ptm.flag=5  or ((ptm.flag=1 or ptm.flag=2) and dispatchFlag=1 and  p08.p0811 in ('02','03')))");
            sbf.append(" and p08.p0809<>5");
            sbf.append(" and p07.p0725 = " + this.periodType);
            sbf.append(" and p07.p0727= " + this.periodYear);
            if (!WorkPlanConstant.Cycle.YEAR.equals(this.periodType)){            
                sbf.append(" and p07.p0729 = " + this.periodMonth);
            }        
            if (WorkPlanConstant.Cycle.WEEK.equals(this.periodType)){
                sbf.append(" and p07.p0731 = " + this.periodWeek);
            }
            RowSet rs = null;
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
                String _p0719= (String)bean.get("p0719");
                String appstatus="未提交";
                String submitType="0";
                String remind="提醒写工作计划";
                String _e0122 = (String)bean.get("b0110");
                rs = null;
                rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{_e0122}));
                String total_rank = "0%";
                if(rs.next()){
                	if(rs.getFloat("total_rank")>0){
                		float str = rs.getFloat("total_rank")*100;
                		total_rank = String.valueOf(str);
                		total_rank = total_rank.substring(0, total_rank.indexOf(".")) + "%";
                	}
                }
                String _e01a1 = (String)bean.get("e01a1");
                String _e0122desc = (String)bean.get("codeitemdesc");
                String _nbase = "";
                String _a0100 = "";
                String _a0101 = "";                           
                if (_p0719 == null) {
                    appstatus = workPlanUtil.getPlanStatusDesc("");
                } else if ("0".equals(_p0719)||"3".equals(_p0719)) {//起草 驳回
                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
                } else if ("1".equals(_p0719)) {//发布
                    submitType="1";
                    appstatus = "已提交";                    
                    remind="提醒批准工作计划";
                }
                else if ("2".equals(_p0719)) {//批准   添加任务已变更状态 wusy
                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
                    if(new WorkPlanBo(conn, userView).isStateChange(bean.get("p0700").toString())){
                    	appstatus = "已变更";
                    }
                    submitType="2";
                    remind="关联到考核计划";
                    if (brelate){
                        String relate_planid =(String)bean.get("relate_planid");
                        if (relate_planid!=null && relate_planid.length()>0){
                            submitType="3";
                            remind="更新到目标卡";
                        }
                    }
                }   
                String  tablename = workPlanUtil.getPeopleSqlByE01a1(_e01a1);
                if (!"".equals(tablename)) {
                    tablename = "(" + tablename + ") T";
                    String strsql = "select T.* from " + tablename;
                    RowSet rset1 = dao.search(strsql);
                    if (rset1.next()) {// 有人负责
                        _nbase = rset1.getString("nbase");
                        _a0100 = rset1.getString("a0100");            
                        _a0101 = rset1.getString("a0101");
                    }
                };

                String imagepath = "";               
                imagepath =planBo.getPhotoPath(_nbase, _a0100);
                String visbleBox="false";
                if ("2".equals(submitType)||"3".equals(submitType)){
                    if (_nbase!=null &&"USR".equalsIgnoreCase(_nbase)) {
                        visbleBox="true";
                    }
                } 

                String stritem = "{" 
                    + quotedDoubleValue("objectid") + ":" 
                    + quotedDoubleValue(WorkPlanUtil.encryption(_nbase + _a0100)) + "," 
                    + quotedDoubleValue("imagepath") + ":" + quotedDoubleValue(imagepath) 
                    + ","+ quotedDoubleValue("name") + ":" + quotedDoubleValue(_a0101) 
                    + "," + quotedDoubleValue("e0122") + ":" + quotedDoubleValue(WorkPlanUtil.encryption(_e0122)) 
                    + "," + quotedDoubleValue("e0122desc") + ":" + quotedDoubleValue(_e0122desc) 
                    + "," + quotedDoubleValue("appstatus") + ":" + quotedDoubleValue(appstatus) 
                    + "," + quotedDoubleValue("submittype") + ":" + quotedDoubleValue(submitType)
                    + "," + quotedDoubleValue("total_rank") + ":" + quotedDoubleValue(total_rank)
                    + "," + quotedDoubleValue("remind") + ":" + quotedDoubleValue(remind) 
                    +","+quotedDoubleValue("p0723")+":"+quotedDoubleValue(WorkPlanUtil.encryption("2"))
                    +","
                    +quotedDoubleValue("visbleBox")+":"+quotedDoubleValue(visbleBox)
                   + "}";
                if ("".equals(info)) {
                    info = stritem;
                } else {
                    info = info + "," + stritem;
                }
            }

            info = quotedDoubleValue("detail_list") + ":[" + info + "]";
            info=info+","+quotedDoubleValue("cur_page")+":"+valueMap.get("curPage"); 
            info=info+","+quotedDoubleValue("sum_page")+":"+valueMap.get("sumPage"); 
            info=info+","+quotedDoubleValue("sum_count")+":"+valueMap.get("sumCount"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        info = quotedDoubleValue("teaminfo_detail") + ":{" + info + "}";
        return info;
    }
    
    
    /**   
     * @Title: getPlanTaskList   
     * @Description: 获取任务列表   
     * @param @param submit_type
     * @param @param pageSize
     * @param @param curPage
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getPlanTaskList(String submit_type,int page_size,int cur_page) {
        String info="";
        HashMap valueMap=new HashMap();
        ArrayList dataList= getTeamPaginationList(submit_type,page_size,cur_page,valueMap);
        if (dataList.size()<1){
         //   return ""; 
        }
        ArrayList peopleList= new ArrayList();
        try{
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean)dataList.get(i);               
                peopleList.add(bean);
            }
            
            PlanTaskTreeHrBo taskTree =new PlanTaskTreeHrBo(this.conn,this.userView,
                    this.planType,this.periodType,this.periodYear,this.periodMonth,this.periodWeek) ;
            HashMap dataMap =taskTree.getTreePanelMap(peopleList);
            //PlanTaskTreeTableBo treeBo= new PlanTaskTreeTableBo(this.conn,32);
            //HashMap dataMap=treeBo.getTreePanelMap(1,1,1);
           
            info=info+quotedDoubleValue("dataModel")
            +":"+(String)dataMap.get("dataModel")+"";
           
            info=info+","+quotedDoubleValue("panelColumns")
            +":"+(String)dataMap.get("panelColumns")+"";
            info=info+","+quotedDoubleValue("dataJson")
                    +":"+(String)dataMap.get("dataJson")+"";
            String pageInfo= "{"+quotedDoubleValue("cur_page")+":"+valueMap.get("curPage")
            +","+quotedDoubleValue("sum_page")+":"+valueMap.get("sumPage")
            +","+quotedDoubleValue("sum_count")+":"+valueMap.get("sumCount")+"}"; 
           
            info=info+","+quotedDoubleValue("pageInfo")+":"+pageInfo;
           
        }
        catch(Exception e){           
            e.printStackTrace();  
        }
       // info=quotedDoubleValue("teaminfo_detail")+":{"+info+"}"; 
      
        return info;
    }   
    
    /**   
     * @Title: getQuickSqlWhere   
     * @Description: 获取一键搜索的sql条件   
     * @param @param keyword
     * @param @param dbpre 所在库
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getQuickSqlWhere(String dbpre,String keyword,ArrayList paramList)  {
        String pyField = workPlanUtil.getPinYinFld(); // 拼音字段
        String emailField = workPlanUtil.getEmailFld(); // 邮箱字段
        StringBuffer sql = new StringBuffer();
        try {
                if ("2".equals(this.planType)){                   
                    sql.append("SELECT codeitemid FROM  organization");
                    sql.append(" where");                  
                    sql.append(" codeitemdesc").append(" LIKE ? ");  
                    paramList.add(keyword+"%");
                    sql.append(" OR codeitemid").append(" LIKE ? ");             
                    paramList.add(keyword+"%");
                    
                    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
                    String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");            
                    String[] arrpre = workPlanUtil.getHrSelfUserDbs();                    
                    StringBuffer e01a1Sql= new StringBuffer();                     
                    for (int i = 0; i < arrpre.length; i++) {
                        String pre = arrpre[i];
                        StringBuffer a0100Sql=new StringBuffer();
                        ArrayList innerParamList= new ArrayList();
                        a0100Sql.append("SELECT a0100 FROM ");
                        a0100Sql.append(pre).append("A01");
                        a0100Sql.append(" where A0101 LIKE ? ");
                        innerParamList.add("%"+keyword+"%");
                        if (pyField.length()>0){
                            a0100Sql.append(" OR ").append(pyField).append(" LIKE ? ");
                            innerParamList.add(keyword+"%"); 
                        }
                        if (emailField.length()>0){
                            a0100Sql.append(" OR ").append(emailField).append(" LIKE  ? ");           
                            innerParamList.add(keyword+"%");  
                        }                    

                        if (i>0){
                            e01a1Sql.append(" union ");
                        }
                        e01a1Sql.append("select E01a1 from ");
                        e01a1Sql.append(pre).append("A01");
                        e01a1Sql.append(" where a0100 in (").append(a0100Sql.toString()).append(" )");            
                        for (int j=0;j<innerParamList.size();j++){
                            paramList.add(innerParamList.get(j));
                        }
                        if("true".equals(flag)){//兼职
                            String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                            String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
                            String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                            if (!("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field))
                            {
                                e01a1Sql.append(" union select ").append(e01a1_field).append(" from ");
                                e01a1Sql.append(pre+setid);
                                e01a1Sql.append(" where a0100 in ("+a0100Sql.toString()+")");
                                e01a1Sql.append(" and "+appoint_field+"='0'");   
                                for (int j=0;j<innerParamList.size();j++){
                                    paramList.add(innerParamList.get(j));
                                }
                            }
                        }
                    }
                    if (e01a1Sql.toString().length()>0){
                        sql.append(" OR exists (");
                        sql.append("select 1 from b01 where ");
                        sql.append(WorkPlanConstant.DEPTlEADERFld);
                        sql.append(" in (").append(e01a1Sql.toString()).append(")");
                        sql.append(" and b0110 =organization.codeitemid");
                        sql.append(") ");
                    }
                }
                else {
                    sql.append("SELECT a0100 FROM ");
                    sql.append(dbpre).append("A01 A");
                    sql.append(", organization o");
                    sql.append(" where");
                    sql.append(" o.codeitemid=A.e0122 ");
                    sql.append(" and( A.A0101 LIKE ? ");
                    paramList.add("%"+keyword+"%");
                    if (pyField.length()>0){
                        sql.append(" OR A.").append(pyField).append(" LIKE ? ");
                        paramList.add(keyword+"%"); 
                    }
                    if (emailField.length()>0){
                        sql.append(" OR A.").append(emailField).append(" LIKE  ? ");           
                        paramList.add(keyword+"%");  
                    }
                    sql.append(" OR O.codeitemdesc").append(" LIKE ? "); 
                    paramList.add(keyword+"%"); 
                    sql.append(" OR O.codeitemid").append(" LIKE ?");
                    paramList.add(keyword+"%");
                    sql.append(") ");   
                }
               // RowSet rSet =dao.search(sql.toString(),paramList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
    
        }
        return sql.toString();
    }
    
    
    
    /**   
     * @Title: getQuickSqlWhere   
     * @Description: 获取普通搜索的sql条件   
     * @param @param keyword
     * @param @param dbpre 所在库
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getCommonSqlWhere(String dbpre,ArrayList commonParamList,ArrayList paramList)  {
        String pyField = workPlanUtil.getPinYinFld(); // 拼音字段
        if (commonParamList.size()<1){
            return "";
        } 
        String pinyin="",a0101="",e0122="";
        boolean b=false;
        for (int i=0;i<commonParamList.size();i++){
            LazyDynaBean bean = (LazyDynaBean)commonParamList.get(i);
            String name=(String)bean.get("name");
            String value=(String)bean.get("value");            
            if ("pinyin".equalsIgnoreCase(name)){
                pinyin=value;
            }
            else if ("a0101".equalsIgnoreCase(name)){
                a0101=value;
            }
            else if ("e0122".equalsIgnoreCase(name)){
                e0122=value;
            }
            else {
                continue;
            }
           b=true; 
        }
        if (!b){
            return "";
        }
        StringBuffer sql = new StringBuffer();
        try { 
                if ("2".equals(this.planType)){
                    sql.append("SELECT codeitemid FROM organization");
                    sql.append(" where 1=1 ");
                    if (e0122.length()>0){
                        sql.append(" and codeitemid").append(" LIKE ?");
                        paramList.add(e0122+"%");
                        
                    }
                    if ((a0101.length()>0) 
                            ||(pinyin.length()>0 &&pyField.length()>0)) 
                    {
                        
                        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
                        String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");            
                        String[] arrpre = workPlanUtil.getHrSelfUserDbs();                    
                        StringBuffer e01a1Sql= new StringBuffer();                     
                        for (int i = 0; i < arrpre.length; i++) {
                            String pre = arrpre[i];
                            ArrayList innerParamList= new ArrayList();
                            StringBuffer a0100Sql=new StringBuffer();
                            a0100Sql.append("SELECT a0100 FROM ");
                            a0100Sql.append(pre).append("A01");
                            a0100Sql.append(" where 1=1 ");
                            if (a0101.length()>0){
                                a0100Sql.append(" and (");
                                String[] tmp=a0101.split("\\|"); 
                                for (int j=0;j<tmp.length;j++){
                                    if (j>0){
                                        a0100Sql.append(" or ") ;
                                    }
                                    a0100Sql.append("  A0101 LIKE ? ");
                                    innerParamList.add("%"+tmp[j]+"%");
                                }
                                a0100Sql.append(")");
                            }
                            //a0100Sql.append(" and A0101 LIKE ? ");
                           // innerParamList.add("%"+a0101+"%");
                            if (pyField.length()>0 && pinyin.length()>0){
                                a0100Sql.append(" and ").append(pyField).append(" LIKE ? ");
                                innerParamList.add(pinyin+"%");
                            }
                            
                            if (i>0){
                                e01a1Sql.append(" union ");
                            }
                            e01a1Sql.append("select E01a1 from ");
                            e01a1Sql.append(pre).append("A01");
                            e01a1Sql.append(" where a0100 in (").append(a0100Sql.toString()).append(" )");            
                            for (int j=0;j<innerParamList.size();j++){
                                paramList.add(innerParamList.get(j));
                            }
                            if("true".equals(flag)){//兼职
                                String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                                String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
                                String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                                if (!("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field))
                                {
                                    e01a1Sql.append(" union select ").append(e01a1_field).append(" from ");
                                    e01a1Sql.append(pre+setid);
                                    e01a1Sql.append(" where a0100 in ("+a0100Sql.toString()+")");
                                    e01a1Sql.append(" and "+appoint_field+"='0'");   
                                    for (int j=0;j<innerParamList.size();j++){
                                        paramList.add(innerParamList.get(j));
                                    }
                                }
                            }
                        }
                        if (e01a1Sql.toString().length()>0){
                            sql.append(" and exists (");
                            sql.append("select 1 from b01 where ");
                            sql.append(WorkPlanConstant.DEPTlEADERFld);
                            sql.append(" in (").append(e01a1Sql.toString()).append(")");
                            sql.append(" and b0110 =organization.codeitemid");
                            sql.append(") ");
                        }
                    }
                    
                }
                else {
                    sql.append("SELECT a0100 FROM ");
                    sql.append(dbpre).append("A01");
                    sql.append("");
                    sql.append(" where  1=1 ");
                    if (a0101.length()>0){
                       // sql.append(" and  A0101 LIKE ? ");
                      //  paramList.add("%"+a0101+"%");
                        sql.append(" and (");
                        String[] tmp=a0101.split("\\|");                  
                      
                        for (int j=0;j<tmp.length;j++){
                            if (j>0){
                                sql.append(" or ") ;
                            }
                            sql.append("  A0101 LIKE ? ");
                            paramList.add("%"+tmp[j]+"%");
                        }
                        sql.append(")");  
                    }
                    if (pyField.length()>0 && pinyin.length()>0){
                        sql.append(" and ").append(pyField).append(" LIKE ?");
                        paramList.add(pinyin+"%");
                    }
                    if (e0122.length()>0){
                        sql.append(" and e0122").append(" LIKE ?");             
                        paramList.add(e0122+"%"); 
                    }
                    
                }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
    
        }
        
        return sql.toString();
    }
    
    /**   
     * @Title: getTeamPeopleSql   
     * @Description:获取管理范围内人员sql    
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getTeamPeopleSql(ArrayList paramList){   
        String operOrg = this.userView.getUnitIdByBusi("5");
        String orgWhere = "1=1";
        if (operOrg != null && operOrg.length() > 3) {
            StringBuffer tempSql = new StringBuffer("");
            String[] temp = operOrg.split("`");
            for (int i = 0; i < temp.length; i++) {
                if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                    tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                    tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
                }
            }
            orgWhere = tempSql.substring(3);
        }
		
        String strSql = "";
        try {
        	//取人员范围条件参数
        	RecordVo paramsVo=ConstantParamter.getConstantVo("OKR_CONFIG");
        	WorkPlanConfigBo bo = new WorkPlanConfigBo(this.conn, this.userView);
        	String xmlValue = "";
    		Map mapXml = new HashMap();
    		// 有缓存则取缓存数据
    		if(null != paramsVo){
    			xmlValue = paramsVo.getString("str_value");
    		}
        	mapXml = bo.parseXml(xmlValue);
        	String dbValue = mapXml.get("nbases")==null?"":(String)mapXml.get("nbases");
        	String emp_scope = mapXml.get("emp_scope")==null?"":(String)mapXml.get("emp_scope");
        	String[] arrpre=dbValue.split(",");
        	if(arrpre.length<1){
        		throw new GeneralException("未设置认证人员库！");
        	}
        	
            for (int i = 0; i < arrpre.length; i++) {
                String pre = arrpre[i];
                String a01tab = pre + "A01";
                //
                String sql = "select " + a01tab + ".a0100 from " + a01tab + "" + " where " 
                + orgWhere + " ";
                // 排除 在当前期间制定计划时岗位不属于我的团队 由其他部门调来的
                sql = sql + " and a0100 not in  ( select a0100 from p07" + " where  " 
                + this.getPlanPublicWhere() + " and a0100 not in " + " (select " + a01tab 
                + ".a0100 from " + a01tab + "" + " where " + orgWhere + ")" + ")";

                // 加上 在当前期间制定计划时属于我的团队，从我部门调走的
                sql = sql + " union select a0100 from p07 " + " where " + orgWhere + " and " 
                + this.getPlanPublicWhere();

                if (!"".equals(strSql)) {
                    strSql = strSql + " union ";
                }
                
                strSql = strSql + "select A0100,a0101,b0110,e0122,e01a1," + "'" + pre + "' as nbase " 
                    +","+i+" as dbid,A0000"
                    + " from  " + a01tab + " where a0100 in (select A.a0100 from (" + sql + ") A )";
                //OKR人员范围sql条件
                String whereIn = bo.getOkrWhereINSql(pre, emp_scope);
   			 	if(!StringUtils.isEmpty(whereIn)){
   			 		StringBuffer nbaseSql=new StringBuffer();
   			 		nbaseSql.append(" and a0100 in(");
   			 		nbaseSql.append(" select "+pre+"A01.a0100 ").append(whereIn).append(") ");
   			 		
   			 		strSql = strSql + nbaseSql.toString();
   			 	}
                
                String othWhere="";
                if("1".equals(queryType)){//一键搜索
                    othWhere=getQuickSqlWhere(pre,this.queryText,paramList); 
                }
                else if("2".equals(queryType)){
                    othWhere=getCommonSqlWhere(pre,this.commonQueryParamList,paramList);  
                }
                
                if (othWhere.length()>0) {
                    strSql=strSql+" and a0100 in ( "+othWhere+")";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strSql;
    }  
    
    /**   
     * @Title: getTeamDeptSql   
     * @Description: 获取管理范围内机构sql   
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getTeamDeptSql(ArrayList paramList ){   
        String operOrg = this.userView.getUnitIdByBusi("5");
        String orgWhere = "1=1";
        if (operOrg != null && operOrg.length() > 3) {
            StringBuffer tempSql = new StringBuffer("");
            String[] temp = operOrg.split("`");
            for (int i = 0; i < temp.length; i++) {                
                tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
                
            }
            orgWhere = tempSql.substring(3);
        }

       
        String strSql = "";
        try { 
            strSql = "select b01."+ WorkPlanConstant.DEPTlEADERFld+" as e01a1,b01.b0110,O.codeitemdesc from B01,organization O " 
                + " where  B01.b0110=O.codeitemid" 
                + " and B01."+ WorkPlanConstant.DEPTlEADERFld
                + " in (" +" select codeitemid from  organization where codesetid='@K'  and (" +orgWhere+" ))" + "";
            String othWhere="";
            if("1".equals(queryType)){//一键搜索
                othWhere=getQuickSqlWhere("",this.queryText,paramList);
                if (othWhere.length()>0) {
                    strSql=strSql+" and B01.B0110 in ( "+othWhere+")";
                }
                
            }
            else if("2".equals(queryType)){
                othWhere=getCommonSqlWhere("",this.commonQueryParamList,paramList);  
                if (othWhere.length()>0) {
                    strSql=strSql+" and B01.B0110 in ( "+othWhere+")";
                }
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strSql;
    }  
    /**   
     * @Title: getCountSqlByPlanStatus   
     * @Description: 按计划状态取计划记录数   
     * @param @param submit_type
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getCountSqlByPlanStatus(String submit_type,ArrayList paramList) {
        String strsql="";
        String tablename="";
        if ("2".equals(this.planType)){
            tablename=getTeamDeptSql(paramList);
        }
        else {
            tablename=getTeamPeopleSql(paramList);            
        }
        if ("".equals(tablename)){
           return ""; 
        }
        tablename="("+tablename+") T";
        String relateFld="";
        if (dbw.isExistField("p07", "relate_planid", false)){
            relateFld=",p07.relate_planid ";
        }
        if ("2".equals(this.planType)){
            strsql = " Select T.e01a1,T.B0110,T.codeitemdesc,p07.p0700,p07.p0719"+relateFld+",p07.p0707,p07.changeflag from " 
                + tablename + " " 
            + "left join (select * from P07 where " + this.getPlanPublicWhere() 
            + ") P07 on p07.p0707 =T.B0110 ";
        }
        else {
            strsql=" select T.*,p07.p0700,P07.p0719,p07.E0122 as p07e0122,p07.e01a1 as p07e01a1"+relateFld+",p07.changeflag from " +tablename
            +" left join p07 on P07.nbase=T.nbase and P07.a0100=T.a0100"
            +" and "+this.getPlanPublicWhere();
        }
        
        //按计划状态查询
        strsql=" select count(*) from ("+strsql+") F where 1=1 ";
        if ("0".equals(submit_type)){//未提交 已退回
            strsql=strsql+" and ("+Sql_switcher.sqlNull("F.p0719", 0)+"=0"
                +" or "+Sql_switcher.sqlNull("F.p0719", 0)+"=3)";
        }
        else if ("1".equals(submit_type)){
            strsql=strsql+" and "+Sql_switcher.sqlNull("F.p0719", 0)+"in (1, 2)";
        } else if ("2".equals(submit_type)){
            strsql=strsql+" and "+Sql_switcher.sqlNull("F.p0719", 0)+"=2";
        }else if("3".equals(submit_type)){
        	strsql=strsql+" and "+Sql_switcher.sqlNull("F.p0719", 0)+"=1";
        }else if("4".equals(submit_type)){// 已变更（changeflag==1）的条数 chent 20160415
        	strsql=strsql+" and "+Sql_switcher.sqlNull("F.changeflag", 0)+"=1";
        }
        else {//所有
            
        }
        //普通查询 按计划状态查询
        if("2".equals(this.queryType)){
            String plan_status="";
            for (int i=0;i<this.commonQueryParamList.size();i++){
                LazyDynaBean bean = (LazyDynaBean)commonQueryParamList.get(i);
                String name=(String)bean.get("name");
                String value=(String)bean.get("value");            
                if ("plan_status".equalsIgnoreCase(name)){
                    plan_status=value;
                    break;
                }
            } 
            if ("0".equals(plan_status)){//未提交 已退回
                strsql=strsql+" and ("+Sql_switcher.sqlNull("F.p0719", 0)+"=0"
                +" or "+Sql_switcher.sqlNull("F.p0719", 0)+"=3)";
            }
            else if ("1".equals(plan_status)){
                strsql=strsql+" and "+Sql_switcher.sqlNull("F.p0719", 0)+"=1";
            } else if ("2".equals(plan_status)){
                strsql=strsql+" and "+Sql_switcher.sqlNull("F.p0719", 0)+"=2";
            }
        }    

        return strsql; 
    } 
    
    /**   
     * @Title: getTeamPaginationSql   
     * @Description:获取分页sql    
     * @param @param submit_type
     * @param @param page_size
     * @param @param cur_page
     * @param @param returnMap 返回值
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getTeamPaginationSql(String submit_type,int page_size,int cur_page,HashMap returnMap,ArrayList paramList) {
        String strsql="";
   
        strsql=getCountSqlByPlanStatus(submit_type,paramList).replace("count(*)", "*");
        if ("".equals(strsql)){
            return ""; 
        }
        
        String orderbyFld="a0100";
        if ("2".equals(this.planType)){
            orderbyFld="b0110";
        }
        strsql = workPlanUtil.getPaginationSql(strsql, page_size, cur_page,orderbyFld,returnMap,paramList); 
        
        return strsql;
        
    }
    
    
    /**   
     * @Title: getPlanDataList   
     * @Description:获取数据beanlist    
     * @param @param submit_type
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getPlanDataList(String submit_type) {
        ArrayList list= new ArrayList();       
        try{
            String strsql="";    
            ArrayList paramList= new ArrayList();   
            strsql=getCountSqlByPlanStatus(submit_type,paramList).replace("count(*)", "F.p0700, F.*");
            if(this.getQueryString()!=null){
            	strsql=strsql+this.getQueryString();
            }
            if ("".equals(strsql)){
                return list; 
            }
            if ("2".equals(this.planType)){
                strsql=strsql+" order by b0110";
            }
            else {
                strsql=strsql+" order by dbid,a0000";
            }
            
            RowSet rset=dao.search(strsql,paramList);
            list =dao.getDynaBeanList(rset);
        }
        catch(Exception e){           
            e.printStackTrace();  
        }
        return list;       
    }
    
    
    
    /**
     * @Title: getPlanPublicWhere
     * @Description: 当前计划的条件 sql语句用
     * @param
     * @return
     * @return String
     * @author:wangrd
     * @throws
     */
    private String getPlanPublicWhere() {
        String sql=" p07.p0723="+this.planType
            +" and p07.p0725 ="+this.periodType +" and p07.P0727="+this.periodYear;
        if (!WorkPlanConstant.Cycle.YEAR.equals(this.periodType)){            
            sql= sql+" and p07.P0729="+this.periodMonth;
        }        
        if (WorkPlanConstant.Cycle.WEEK.equals(this.periodType)){
            sql= sql+" and p07.P0731="+this.periodWeek;
        }
        return sql;
        
    }   
    
    /**   
     * @Title: remindMyTeamToSubmitPlan   
     * @Description:  提醒写、批准邮件  
     * @param @param p0723
     * @param @param periodtype
     * @param @param periodyear
     * @param @param periodmonth
     * @param @param periodweek
     * @param @param submit_type
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean remindMyTeamToSubmitPlan(String p0723,String periodtype,String periodyear,
            String periodmonth,String periodweek,String submit_type)  { 
        if("2".equals(submit_type)){
            return true;
        }
        boolean b=false;
        this.planType=p0723;
        this.periodType=periodtype;
        this.periodYear=periodyear;
        this.periodMonth=periodmonth;
        this.periodWeek=periodweek;
        try{  
            String strsql = "";
            RowSet rset=null;
            ArrayList paramList = new ArrayList();
            strsql=getCountSqlByPlanStatus(submit_type,paramList).replace("count(*)", "*");
            rset=dao.search(strsql,paramList);
            
            WorkPlanBo planBo= new WorkPlanBo(this.conn,this.userView); 
            b= planBo.remindSubmitPlan(rset, p0723, periodtype, periodyear, periodmonth, periodweek,submit_type);
            b=true;
        }
        catch(Exception e){           
            e.printStackTrace();  
        }

        return b;
        
    }
    
    /**   
     * @Title: checkHaveIsApprovedPlan   
     * @Description:判断当前查询计划列表中是否有已批准的    
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean checkHaveIsApprovedPlan() 
    {
        boolean b=false;
        try {
            ArrayList paramList = new ArrayList();
            String strsql = getCountSqlByPlanStatus("2", paramList).replace("count(*)", "*");
            RowSet rset = dao.search(strsql,paramList);            
            if  (rset.next()){
               b=true; 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
 }
    
    
    /**   
     * @Title: checkHaveSuperPeople   
     * @Description: 查看当前所选人员是否有上级、上上级    
     * @param @param rset
     * @param @param bAll
     * @param @param checkSuper true:判断上级 false 判断上上级
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    private boolean checkHaveSuperPeople(RowSet rset,boolean bAll,boolean checkSuper) 
    {
        boolean b = false;
        try {
            while (rset.next()) {
                String _p0719 = rset.getString("p0719");
                if (_p0719 == null) {
                    _p0719 = "";
                }
                if (!"2".equals(_p0719)) {
                    continue;
                }

                if ("1".equals(this.planType)) {
                    String nbase = rset.getString("nbase");
                    if (!"USR".equalsIgnoreCase(nbase)) {
                        continue;
                    }
                }

                String objectid = "";
                if ("2".equals(this.planType)) {
                    objectid = rset.getString("p0707");

                } else {
                    objectid = rset.getString("a0100");
                }
                String e01a1 = "";
                if (bAll) {
                    if ("2".equals(this.planType)) {
                        e01a1 = rset.getString("e01a1");
                    } else {
                        e01a1 = rset.getString("p07e01a1");
                    }
                } else {
                    if ("2".equals(this.planType)) {
                        e01a1 = workPlanUtil.getDeptLeaderE01a1(objectid);
                    } else {
                        e01a1 = rset.getString("e01a1");
                    }
                }
                e01a1 = (e01a1 == null) ? "" : e01a1;

                String superE01a1 = workPlanUtil.getDirectSuperE01a1(e01a1);
                String superA0100 = workPlanUtil.getFirstE01a1Leaders(superE01a1, "USR");
                if (superA0100.length() > 0) {
                    if (!checkSuper) {
                        String superSuperE01a1 = workPlanUtil.getDirectSuperE01a1(superE01a1);
                        if (superSuperE01a1.length() > 0) {
                            String mainBodyA0100 = workPlanUtil.getFirstE01a1Leaders(superSuperE01a1, "USR");
                            if (mainBodyA0100.length() > 0) {
                                b = true;
                                break;
                            }
                        }
                    } else {
                        b = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;  
        
        
    }
    /**
     * @Title: checkHaveSuperBodyType
     * @Description: 检查是否有上级、上上级主体类别 如果主体类别没设上上级、而且所选考核对象有上上级，则提示，否则不提示
     * @param
     * @param objectIds
     * @param
     * @return
     * @return String
     * @author:wangrd
     * @throws
     */
    public String checkHaveSuperBodyType(String khplan_id,String [] arrobjectIds,boolean bAll) 
    {
        String info="";
        try {
            RowSet rset = null;
            ArrayList paramList = new ArrayList();
           
            RelatePerformancePlanBo relateBo =new RelatePerformancePlanBo(this.conn,this.userView);
            String superBoydid = relateBo.getBodyIdByBodyType(khplan_id,"1");//上级
            String superSuperBoydid = relateBo.getBodyIdByBodyType(khplan_id,"0");//上上级
            boolean bHaveSuper=false;
            boolean bHaveSuperSuper=false;
            if (superBoydid.length()>0){//设置了上级 不检查
                bHaveSuper=false;
            }
            else {                
                String strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll);  
                rset = dao.search(strsql,paramList);  
                bHaveSuper=checkHaveSuperPeople(rset,bAll,true);
            
            }
            if (superSuperBoydid.length()>0){
                bHaveSuperSuper=false;
            }
            else {
                String strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll);  
                rset = dao.search(strsql,paramList);  
                bHaveSuperSuper=checkHaveSuperPeople(rset,bAll,false);
            }
            if (bHaveSuper){
                info="上级";
            }
            if (bHaveSuperSuper){
                if (info.length()>0){
                    info=info+"、";
                }
                info=info+"上上级";
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
 }
    
  
    /**   
     * @Title: checkIsRelatedPlan   
     * @Description: 检查是否有已经关联计划的人员   
     * @param @param objectIds
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public boolean checkIsRelatedPlan(String [] arrobjectIds,boolean bAll,HashMap map) 
    {
        boolean b=false;
        try {
            RowSet rset = null;
            ArrayList paramList = new ArrayList();
            String strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll);  
            rset = dao.search(strsql,paramList);  
            int count=0;
            String A0101s="";
            while (rset.next()){
                String _p0719= rset.getString("p0719");
                if (_p0719==null) {
                    _p0719="";
                }
                if (!"2".equals(_p0719)){
                    continue;
                }                                         
               
     
                if("1".equals(this.planType)){ 
                    String nbase=rset.getString("nbase");
                    if (!"USR".equalsIgnoreCase(nbase)){
                        continue;
                    }
                }               
               
                String planid=rset.getString("relate_planid");
                if (planid!=null && planid.length()>0 && !"0".equals(planid)){
                    String a0101="";
                    if("1".equals(this.planType)){ 
                        String nbase=rset.getString("nbase");
                        String a0100=rset.getString("a0100");
                        a0101=workPlanUtil.getUsrA0101(nbase, a0100);
                    }               
                    else { 
                        String p0707=rset.getString("p0707");
                        a0101 =AdminCode.getCodeName("UN", p0707);
                        if ("".equals(a0101)){
                            a0101 =AdminCode.getCodeName("UM", p0707);
                        }
                    }                     
                    if (count<5){
                        if (A0101s.length()<1){
                            A0101s=a0101;
                        }
                        else {
                            A0101s=A0101s+"、"+a0101; 
                        } 
                    }
                    count++;
                    b=true;
                }
            }
            map.put("A0101s",A0101s);
            if (count <=5) {
                map.put("count","");
            } else {
                map.put("count",count+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
 }
    
    /**   
     * @Title: getCheckPeopleData   
     * @Description: 获取需要检查的人员计划集合   
     * @param @param rPeopleSet
     * @param @param arrobjectIds
     * @param @param bAll
     * @param @return 
     * @author:wangrd   
     * @throws   
    */
    public String  getCheckPeopleSql(ArrayList paramList,String [] arrobjectIds,boolean bAll) 
    {
        String strsql="";
        try
        {
            if (bAll) {
                paramList.clear();
                strsql = getCountSqlByPlanStatus("2", paramList).replace("count(*)", "*");
               
            } else {
                String odjectIds="";
                for (int i = 0; i < arrobjectIds.length; i++) {
                    String objectid = arrobjectIds[i];
                    if ("2".equals(this.planType)) {
                        if ("".equals(odjectIds)){
                            odjectIds="'"+objectid+"'";
                        }
                        else {
                            odjectIds=odjectIds+","+"'"+objectid+"'"; 
                        }                        
                    } else {
                        if (objectid.length() < 4) {
                            continue;
                        }
                        String nbase = objectid.substring(0, 3);
                        String a0100 = objectid.substring(3);
                        if ("".equals(odjectIds)){
                            odjectIds="'"+a0100+"'";
                        }
                        else {
                            odjectIds=odjectIds+","+"'"+a0100+"'"; 
                        }
                        
                    }
                }
                if ("2".equals(this.planType)) {
                    strsql = " select * from  p07 where p0707 in ("+ odjectIds+")"+ " and " 
                    + this.getPlanPublicWhere();
                    
                } else {        
                    strsql = " select * from  p07 where upper(nbase) ='USR'"   
                        + " and a0100 in ("+ odjectIds+")"+ " and " + this.getPlanPublicWhere();
                }
            
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strsql;
    
    }
    
    
    /**   
     * @Title: isOtherPlanUseThisTemplate   
     * @Description: 是否其他计划关联了此模板   
     * @param @param plan_id
     * @param @param template_id
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isOtherPlanUseThisTemplate(String plan_id,String template_id) 
    {
        boolean b=false;
        try {
            ArrayList paramList = new ArrayList();
            paramList.add(Integer.valueOf(plan_id));
            String strsql="select template_id from per_plan where plan_id <>? and template_id='"
                +template_id+"'";
            RowSet rset = dao.search(strsql,paramList);
            if (rset.next()){
                b=true;               
            }
      
        } catch (Exception e) {
            b=false;
            e.printStackTrace();
        }
        return b;
        
    }
    

    /**   
     * @Title: isExistsPlan   
     * @Description: 计划是否存在   
     * @param @param plan_id
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isExistsPlan(String plan_id) 
    {
        boolean b=false;
        try {
            ArrayList paramList = new ArrayList();
            paramList.add(Integer.valueOf(plan_id));
            String strsql="select template_id from per_plan where plan_id =? ";
            RowSet rset = dao.search(strsql,paramList);
            if (rset.next()){
                b=true;               
            }
      
        } catch (Exception e) {
            b=false;
            e.printStackTrace();
        }
        return b;
        
    }
    
    /**   
     * @Title: checkTemplate   
     * @Description:    
     * @param @param arrobjectIds
     * @param @param bAll
     * @param @param map
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean checkCanEditTemplate(String khplan_id,String [] arrobjectIds,
            boolean bAll,HashMap map,boolean reRelate) 
    {
        boolean b=true;
        String strsql="";
        RowSet rset = null;
        ArrayList paramList = new ArrayList();
        try {
            //首先此模板已关联其他计划
            strsql="select template_id from per_plan where plan_id =?";
            paramList.clear();
            paramList.add(Integer.valueOf(khplan_id));
            rset = dao.search(strsql, paramList);
            String template_id="";
            if (rset.next()){
                template_id=rset.getString("template_id");               
            }

            if (!isOtherPlanUseThisTemplate(khplan_id,template_id)){
                return b;
            }
            
            String itemdescs="";
            strsql="select * from per_template_item where template_id ='"
                    +template_id+"'";
            rset = dao.search(strsql);
            while (rset.next()){
                itemdescs=itemdescs+","+rset.getString("itemdesc");           
            }
            itemdescs=","+itemdescs+",";          
            
            FieldItem item = DataDictionary.getFieldItem( "p0823","P08");
            if (item ==null) {
                return b;
            }
            String codeSetId =item.getCodesetid();          
             //需要检查的工作计划 
            b=true;
            String taskdesc="";
            paramList.clear();
            strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll);  
            rset = dao.search(strsql,paramList);            
            while (rset.next()){
                String p0700= rset.getString("p0700");
                String _p0719= rset.getString("p0719");
                if (p0700==null) {
                    continue;
                }
                if (_p0719==null) {
                    _p0719="";
                }
                if (!"2".equals(_p0719)){
                    continue;
                }  
                if (!reRelate){//已关联的不再关联
                    int relate_planid= rset.getInt("relate_planid");
                    if (relate_planid>0){
                        continue;
                    }
                }
                PlanTaskTreeTableBo taskTreeBo= new PlanTaskTreeTableBo(this.conn,Integer.parseInt(p0700));
                String sql =taskTreeBo.getTableDatasql("");
                RowSet rtaskset = dao.search(sql);
                boolean bBreak=false;
                while (rtaskset.next()){
                    if (isNeedAddToP04(rtaskset,"","","")){//需要考核
                        String p0823=rtaskset.getString("p0823")==null?"":rtaskset.getString("p0823");
                        if (codeSetId.length()>0) {
                            p0823= AdminCode.getCodeName(codeSetId, p0823);
                        }
                        if (itemdescs.indexOf(","+p0823+",")<0){
                            taskdesc=p0823;                            
                            bBreak=true;
                            break;
                        }
                    }
                }
                if (bBreak){
                    b=false;
                    break;
                } 
            }
            map.put("taskDesc",taskdesc);

        } catch (Exception e) {
            b=false;
            e.printStackTrace();
        }
        return b;
 }
    
 
    
    /**   
     * @Title: relatePlan   
     * @Description:关联计划    
     * @param @param khplan_id
     * @param @param arrobjectIds
     * @param @param bAll
     * @param @param map
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean relatePlan(String khplan_id,String [] arrobjectIds,boolean bAll,HashMap map,boolean reRelate) 
    {
        boolean b=true;
        String strsql="";
        RowSet rset = null;
        ArrayList paramList = new ArrayList();
        try {
            RelatePerformancePlanBo relateBo =new RelatePerformancePlanBo(this.conn,this.userView);
            PerformanceImplementBo ImplementBo = new PerformanceImplementBo(this.conn,
                    this.userView,khplan_id);
            String superBoydid = relateBo.getBodyIdByBodyType(khplan_id,"1");//上级
            String superSuperBoydid = relateBo.getBodyIdByBodyType(khplan_id,"0");//上上级
             //需要检查的工作计划 
            strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll);  
            rset = dao.search(strsql,paramList);            
            while (rset.next()){
                String p0700= rset.getString("p0700");
                String _p0719= rset.getString("p0719");
                if (p0700==null) {
                    continue;
                }
                if (_p0719==null) {
                    _p0719="";
                }
                if (!"2".equals(_p0719)){
                    continue;
                }    
                if (!reRelate){//已关联的不再关联
                    int relate_planid= rset.getInt("relate_planid");
                    if (relate_planid>0){
                        continue;
                    }
                }
                strsql="update p07 set relate_planid ="+khplan_id+" where p0700="+p0700;
                dao.update(strsql);
                //新增考核对象
                String objectid="";
                if ("2".equals(this.planType)) {
                    objectid=rset.getString("p0707");
                    if (objectid!=null && objectid.length()>0){
                        ImplementBo.handInsertObjects("'"+objectid+"'",khplan_id,"1");
                    }
                }
                else {
                    objectid=rset.getString("a0100");
                    if (objectid!=null && objectid.length()>0){
                        ImplementBo.handInsertObjects("'"+objectid+"'",khplan_id,"2");
                    }  
                }
                initP04Record(khplan_id,objectid,this.planType);
                //新增考核主体
                String e01a1="";
                if (bAll){
                    if ("2".equals(this.planType)){
                        e01a1=rset.getString("e01a1");
                    }
                    else {
                        e01a1=rset.getString("p07e01a1");
                    }
                }
                else {
                    if ("2".equals(this.planType)){
                        e01a1=workPlanUtil.getDeptLeaderE01a1(objectid);
                    }
                    else {
                        e01a1=rset.getString("e01a1");
                    }
                }
                e01a1=(e01a1==null)?"":e01a1;
                //如果有团队负责人类别 则插入团队负责人
                String deptLeader="";// 记录负责人的人员编号，如果此负责人还有其他领导身份的话，则不再添加。
                if ("2".equals(this.planType)){
                    String deptBody_id = relateBo.getBodyIdByBodyType(khplan_id,"5");//团队负责人
                    if ("-1".equals(deptBody_id) && e01a1.length()>0){
                        String mainBodyA0100= workPlanUtil.getFirstE01a1Leaders(e01a1,"USR");
                        if (mainBodyA0100.length()>0){
                        	deptLeader=mainBodyA0100;
                            ImplementBo.selMainBody("'"+mainBodyA0100+"'", 
                                    khplan_id, deptBody_id, objectid,"false");
                        }
                        
                    }
               
                }
                String superE01a1 ="";
                if (superBoydid.length()>0||superSuperBoydid.length()>0){
                    superE01a1 =workPlanUtil.getDirectSuperE01a1(e01a1);
                }
                if (superBoydid.length()>0 && superE01a1.length()>0){
                  String mainBodyA0100= workPlanUtil.getFirstE01a1Leaders(superE01a1,"USR");
                  if (mainBodyA0100.length()>0 && !deptLeader.equals(mainBodyA0100)){
                      ImplementBo.selMainBody("'"+mainBodyA0100+"'", 
                              khplan_id, superBoydid, objectid,"false");
                  }
                }
                if (superSuperBoydid.length()>0 && superE01a1.length()>0){
                    String superSuperE01a1 =workPlanUtil.getDirectSuperE01a1(superE01a1);
                    if (superSuperE01a1.length()>0){
                        String mainBodyA0100= workPlanUtil.getFirstE01a1Leaders(superSuperE01a1,"USR");
                        if (mainBodyA0100.length()>0 && !deptLeader.equals(mainBodyA0100)){
                            ImplementBo.selMainBody("'"+mainBodyA0100+"'", 
                                    khplan_id, superSuperBoydid, objectid,"false");
                        }
                    }
                }
                
                // 更新目标卡
                if (!updateTargetCard(khplan_id,objectid,this.planType,p0700)){
                    break;
                }
                

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
 }
    

    /** 
    * @Title: batchUpdateTargetCard 
    * @Description: 批量更新目标卡
    * @param @param arrobjectIds
    * @param @param bAll
    * @param @return
    * @return boolean
    */ 
    public HashMap batchUpdateTargetCard(String [] arrobjectIds,boolean bAll) 
    {
    	HashMap planMap =new HashMap();
    	planMap.put("planIds", ",");
        String strsql="";
        RowSet rset = null;
        ArrayList paramList = new ArrayList();
        String relate_planids=","; 
        try {
            RelatePerformancePlanBo relateBo =new RelatePerformancePlanBo(this.conn,this.userView);    
            strsql =getCheckPeopleSql(paramList,arrobjectIds,bAll);  
            rset = dao.search(strsql,paramList);            
            while (rset.next()){
                String p0700= rset.getString("p0700");
                if (p0700==null) {
                    continue;
                }
                String _p0719= rset.getString("p0719");
                if (_p0719==null) {
                    _p0719="";
                }
                if (!"2".equals(_p0719)){
                	continue;
                }   
                String objectid= "";
                if ("1".equals(planType)){
                	objectid= rset.getString("nbase")+rset.getString("a0100");
                }
                else {
                	objectid= rset.getString("p0707");
                }
               
                int relate_planid= rset.getInt("relate_planid");
                if (relate_planid<1){
                    continue;
                }
                if (!isExistsPlan(String.valueOf(relate_planid))){
                	continue;
                }
                WorkPlanBo planBo= new WorkPlanBo(this.conn,this.userView); 
            	planBo.initPlan(objectid, planType, periodType,periodYear,periodMonth,periodWeek);            
                //检查是否能修改模板                       
                String [] arrObjectIds= objectid.split(",");
                HashMap map= new HashMap();
    			if (!checkCanEditTemplate(String.valueOf(relate_planid), arrObjectIds,false,map,true)){
    				continue;
    			}
				if ("1".equals(planType)){
					objectid=objectid.substring(3);
				}
				
				if (!relate_planids.contains(","+String.valueOf(relate_planid)+",")) {
                    relate_planids=relate_planids+String.valueOf(relate_planid)+",";
                }
				
				updateTargetCard(String.valueOf(relate_planid), objectid, planType, p0700);			
    		}  
            planMap.put("planIds", relate_planids);
       
        } catch (Exception e) {
            e.printStackTrace();
        }

        return planMap;
 } 
    
    /**   
     * @Title: initP04Record   
     * @Description: 新增考核对象后，初始化p04表 把共性指标copy过来。   
     * @param @param kh_planid
     * @param @param objectid
     * @param @param p0723 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public void initP04Record(String kh_planid,String objectid,String p0723) 
    {
        try{
            String sql = "select count(*) from p04  where plan_id=" + kh_planid;   
            if("1".equalsIgnoreCase(p0723)) {
                sql+=" and a0100='"+objectid+"'";
            } else {
                sql+=" and b0110='"+objectid+"'";
            }
            RowSet  frowset = dao.search(sql);
            if (frowset.next())
            {
                if(frowset.getInt(1)==0)   {
                    KhTemplateBo bo = new KhTemplateBo(this.conn,"1",objectid,kh_planid,"targetCard");  
                    bo.insertObjTarget_commonPoint(objectid);          
                }        
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    /**   
     * @Title: isNeedAddToP04   
     * @Description:判断是否需要添加到p04表    
     * @param @param rtaskset
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isNeedAddToP04(RowSet rtaskset,String kh_planid,String objectid,String p0723) 
    {
        boolean b=false;
        try{
            //是否有权重
            double rank=rtaskset.getFloat("rank");
            if (rank==0){
                return b;  
            }
            //是否已批准
            /*
            String P0811=rtaskset.getString("P0811");
            if (!WorkPlanConstant.TaskStatus.APPROVED.equals(P0811)){
                return b;  
            }
            */
            
            //是否已取消 p0833= 2           
            int p0833 = rtaskset.getInt("p0833"); // 任务变更状态
            String p0809 = rtaskset.getString("p0809"); // 任务执行状态       
            if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)
                    || WorkPlanConstant.TaskChangedStatus.Cancel==p0833) { // 已取消
            	/*
            	if (kh_planid!=null && kh_planid.length()>0){
                    //删除对应的任务
                    String p0800 = rtaskset.getString("p0800");                      
                    String sql = "delete from p04  where fromflag=5 and plan_id="+kh_planid+" and p0401='"+p0800+"' ";
                    if("1".equalsIgnoreCase(p0723))
                        sql+=" and a0100='"+objectid+"'";
                    else 
                        sql+=" and b0110='"+objectid+"'";
                    
                    try
                    {
                        dao.delete(sql, new ArrayList());
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                        throw GeneralExceptionHandler.Handle(e);
                    }   
                }
               */
                return b;
            }
            b=true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        
        return b;
    }
    
    
    /** 
    * @Title: deleteTask 
    * @Description: 删除权重为0的任务 及已取消的任务
    * @param @param deleteList
    * @param @return
    * @return boolean
    */ 
    public boolean deleteTask(ArrayList deleteList,String kh_planid,String objectid,String p0723) 
    {
        boolean b=false;
        try{
         	if (kh_planid!=null && kh_planid.length()>0){
                try
                {
	         		String sql = "delete from p04  where fromflag=5 and plan_id="+kh_planid;
	         		if("1".equalsIgnoreCase(p0723)) {
                        sql+=" and a0100='"+objectid+"'";
                    } else {
                        sql+=" and b0110='"+objectid+"'";
                    }
	         		String taskIds="";
	         		for (int i=0;i<deleteList.size();i++){
	         			if ("".equals(taskIds)){
	         				taskIds= "'"+(String)deleteList.get(i)+"'";
	         			}
	         			else {
	         				taskIds= taskIds+","+"'"+(String)deleteList.get(i)+"'";
	         			}
	         			if (i>0 && i % 50==0) {
	         				String strSql =sql+" and p0401 in ("+taskIds+")";
	         				dao.delete(strSql, new ArrayList());
	         				taskIds="";
	         			}
	         		}
	         		if (taskIds.length()>0){
         				String strSql =sql+" and p0401 in ("+taskIds+")";
         				dao.delete(strSql, new ArrayList());
	         		}
                } catch (SQLException e)
                {
                    e.printStackTrace();
                    throw GeneralExceptionHandler.Handle(e);
                }   
            }
        	

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }
    /** 
    * @Title: delelteP04Task 
    * @Description: 删除目标卡已存在的任务
    * @param @param kh_planid
    * @param @param objectid
    * @param @param p0723
    * @param @return
    * @return boolean
    */ 
    public boolean delelteP04Task(String kh_planid,String objectid,String p0723) 
    { boolean b=false;
    	try{

			String sql = "delete from p04  where fromflag=5 and plan_id="+kh_planid;
			if("1".equalsIgnoreCase(p0723)) {
                sql+=" and a0100='"+objectid+"'";
            } else {
                sql+=" and b0110='"+objectid+"'";
            }
			
			try
			{
				dao.delete(sql, new ArrayList());
			} catch (SQLException e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}   

    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	
    	return b;
    }
    
    /**   
     * @Title: updateTargetCard   
     * @Description: 更新目标卡   
     * @param @param khplan_id
     * @param @param p0700
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean updateTargetCard(String khplan_id,String objectid,String p0723, String p0700) 
    {
    	fScoreList.clear();
    	boolean b=true;
        try {
            RelatePerformancePlanBo relateBo =new RelatePerformancePlanBo(this.conn,this.userView);
            if (templateId==null || templateId.length()<1){
                templateId= relateBo.getTemplateId(khplan_id);            
                templateItemList =relateBo.getTemplateItemList(templateId);  
            }
            FieldItem item = DataDictionary.getFieldItem( "p0823","P08");
            if (item ==null) {
                return b;
            }
            String codeSetId =item.getCodesetid(); 
            
            String objCode="P"+objectid;
            if ("2".equals(this.planType)){
                objCode="UN"+ objectid;
            }
            KhTemplateBo bo = new KhTemplateBo(this.conn,"1",objCode,khplan_id,"targetCard");  
           
            String point_explain_item="";
            String point_evaluate_item="";
            AnalysePlanParameterBo PlanParameterBo = new AnalysePlanParameterBo(this.conn);
            Hashtable ht_table = PlanParameterBo.analyseParameterXml();
            if (ht_table != null) {
                if (ht_table.get("DescriptionItem") != null) {
                    point_explain_item = (String) ht_table
                            .get("DescriptionItem");
                }
                if (ht_table.get("PrincipleItem") != null) {
                    point_evaluate_item = (String) ht_table
                            .get("PrincipleItem");
                }
            }
            //判断是否要更新直接上级的打分记录
            boolean bUpdScore=false;
            String superMainBodyId="";
            String superBoydid = relateBo.getBodyIdByBodyType(khplan_id,"1");//上级主体Id
            String sql="select * from per_mainbody where  plan_id="+khplan_id
                +" and object_id ='"+objectid+"' and body_id="+superBoydid
                +" and status in (0,1)";
            RowSet rSet= dao.search(sql);
            if(rSet.next()){//未提交的打分可以提交
            	bUpdScore=true;
            	superMainBodyId=rSet.getString("mainbody_id");
            	
            }
            //bUpdScore=false;            
            
            PlanTaskTreeTableBo taskTreeBo= new PlanTaskTreeTableBo(this.conn,Integer.parseInt(p0700));
            FieldItem rankItem=DataDictionary.getFieldItem("rank", "per_task_map");          

            sql =taskTreeBo.getTableDatasql("");            
            RowSet rtaskset = dao.search(sql);
            HashMap p08ScoreMap = new HashMap();
            if (bUpdScore){//获取分值
            	ArrayList dataList = new ArrayList();
            	 while(rtaskset.next()){
            		 String p0800=rtaskset.getString("p0800");
            		 LazyDynaBean bean  =new LazyDynaBean();
            		 bean.set("p0800", p0800);
            		 dataList.add(bean); 
            		 //todo 调用公用取值方法
            	}
            	p08ScoreMap=taskTreeBo.getTaskScoreMap(dataList);
            	rtaskset.beforeFirst();
            }
            ArrayList toDeleteTaskList = new ArrayList();
            while (rtaskset.next()){
                if (isNeedAddToP04(rtaskset,khplan_id,objectid,p0723)){//需要考核
                    double rank=rtaskset.getFloat("rank");
                    String fmtRank=WorkPlanUtil.formatDouble(rank, rankItem.getDecimalwidth());
                    BigDecimal rankDecimal= new BigDecimal(fmtRank);
                    rank=rankDecimal.doubleValue();//解决p04表权重字段小数位过多的问题。
                    
                    String p0823=rtaskset.getString("p0823")==null?"":rtaskset.getString("p0823");
                    if (codeSetId.length()>0) {
                        ;
                    }
                        p0823= AdminCode.getCodeName(codeSetId, p0823);
                    LazyDynaBean bean = relateBo.getTemplateItem(templateItemList, p0823);
                    if (bean==null && p0823.length()>0){
                        relateBo.addTemplateItem(templateId, p0823) ;
                        templateItemList =relateBo.getTemplateItemList(templateId);  
                        bean = relateBo.getTemplateItem(templateItemList, p0823); 
                        if (bean==null){
                            throw new Exception("新增模板项目出错！");   
                        }
                        else {
                            //项目权限表增加字段
                            Table table = new Table("PER_ITEMPRIV_" + khplan_id);
                            Field obj  = new Field("C_" + (String)bean.get("item_id"));
                            obj.setDatatype(DataType.INT);
                            obj.setKeyable(false);
                            table.addField(obj);
                            dbw.addColumns(table);
                            DBMetaModel dbmodel=new DBMetaModel(this.conn);
                            dbmodel.reloadTableModel("PER_ITEMPRIV_" + khplan_id);   
                            sql="update PER_ITEMPRIV_" + khplan_id 
                                  + " set "+ "C_" + (String)bean.get("item_id")+"=1";
                            dao.update(sql);   
                            //结果表增加字段
                            table = new Table("PER_RESULT_" + khplan_id);
                            obj  = new Field("T_" + (String)bean.get("item_id"));
                            obj.setDatatype(DataType.FLOAT);
                            obj.setLength(12);
                            obj.setDecimalDigits(6);
                            obj.setKeyable(false);
                            table.addField(obj);
                            dbw.addColumns(table);
                            dbmodel.reloadTableModel("PER_RESULT_" + khplan_id);  
                        }
                    }
                    if (bean!=null){
                        // 更新目标卡
                        String itemid =(String)bean.get("item_id");
                        String taskname=rtaskset.getString("p0801");
                        String p0800=rtaskset.getString("p0800");                    
                        int p0400 =bo.insertTargetTaskFromP08(taskname,itemid,p0800,rank);
                        //更新任务描述、评价标准
                        if (p0400>0){
                            RecordVo vo = new RecordVo("P04");
                            vo.setInt("p0400", p0400); 
                            vo = dao.findByPrimaryKey(vo);
                            if (point_explain_item!=null && point_explain_item.length()>0){
                                String taskdesc=rtaskset.getString("p0803"); //任务描述
                                if (taskdesc!=null) {
                                    vo.setString(point_explain_item.toLowerCase(), taskdesc);
                                }
                            }
                            if (point_evaluate_item!=null && point_evaluate_item.length()>0){
                                String P0841=rtaskset.getString("p0841"); //评价标准
                                if (P0841!=null) {
                                    vo.setString(point_evaluate_item.toLowerCase(), P0841);
                                }
                            }
                            //完成进度
                            String p0835=rtaskset.getString("p0835"); 
                            if (p0835!=null) {
                                vo.setInt("p0419",Integer.parseInt(p0835));
                            }
	                            
                            //进度说明:p0837=>p0409  ps:中交一公局:p0837=>p04z9
                            String p0837=rtaskset.getString("p0837"); 
                            if (p0837!=null) {
                                vo.setString("p0409", p0837);
                            }
                            
                            dao.updateValueObject(vo);
                            
                            //获取直接上级的评分
                            if (bUpdScore&&p08ScoreMap.get(p0800+"/score")!=null){                            	
                            	Integer intScore= (Integer)p08ScoreMap.get(p0800+"/score");
                            	double p0413 =vo.getDouble("p0413");
                            	double p0415 =vo.getDouble("p0415");
                            	double score=((intScore*1.0000)/10)*p0413;
                            	LazyDynaBean scoreBean  =new LazyDynaBean();
                            	scoreBean.set("objectId", objectid);
                            	scoreBean.set("mainBodyId", superMainBodyId);
                            	scoreBean.set("p0413", p0413+"");
                            	scoreBean.set("p0400", p0400+"");
                            	scoreBean.set("score", score+"");
                            	fScoreList.add(scoreBean);
                            	
                            }
                        }
                    }
                }
                else {//需要删除的任务
                	toDeleteTaskList.add(rtaskset.getString("p0800"));
                }
            }
            deleteTask(toDeleteTaskList,khplan_id,objectid,p0723);
            //更新直接上级评分
            if (bUpdScore&& fScoreList.size()>0){
            	updateTargetScore(khplan_id, superMainBodyId, objectid);
            }
            b=true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
 }
    
    

    /** 
    * @Title: updateTargetScore 
    * @Description: 更新目标卡打分记录
    * @param @param khplan_id 考核计划编号
    * @param @param mainbodyId 考核直接上级
    * @param @param objectId 被考核人
    * @param @return
    * @return boolean
    */ 
    public boolean updateTargetScore(String khplan_id,String mainbodyId,String objectId) 
    {
		boolean b = true;
		try {
			ArrayList gradeList= getStandardGradeList();
			String sql = "select * from per_target_evaluation where plan_id ="
					+ khplan_id + " and object_id ='" + objectId
					+ "' and mainbody_id='" + mainbodyId + "'";
			RowSet rSet = dao.search(sql);
			// 需要更新的打分记录
			ArrayList updList = new ArrayList();
			while (rSet.next()) {
				String id = rSet.getString("id");
				String p0400 = rSet.getString("p0400");
				boolean bExist = false;
				int index = 0;
				LazyDynaBean scoreBean = null;
				for (int i = 0; i < fScoreList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) fScoreList.get(i);
					if (p0400.equals((String) bean.get("p0400"))) {
						bExist = true;
						index = i;
						scoreBean = bean;
						break;
					}
				}
				if (bExist){//需要更新
					String score = (String)scoreBean.get("score");
					String p0413=(String) scoreBean.get("p0413");
					String degreeId= getDegreeIdByScore(gradeList,p0413,score);
					ArrayList list = new ArrayList();
					list.add(score);
					list.add(degreeId);
					list.add(id);
					updList.add(list);
					fScoreList.remove(index);
				}
			}
			// 需要新增的打分记录
			ArrayList newList = new ArrayList();
			for (int i = 0; i < fScoreList.size(); i++) {
				LazyDynaBean scoreBean = (LazyDynaBean) fScoreList.get(i);
				String p0400=(String) scoreBean.get("p0400");
				String score=(String) scoreBean.get("score");
				String p0413=(String) scoreBean.get("p0413");
				String degreeId= getDegreeIdByScore(gradeList,p0413,score);
				
				ArrayList list = new ArrayList();
				IDGenerator idg = new IDGenerator(2, this.conn);
				String id = idg.getId("per_target_evaluation.id");
				list.add(new Integer(id));
				list.add(new Integer(khplan_id));
				list.add(objectId);
				list.add(mainbodyId);
				list.add(new Integer(p0400));
				list.add(new Double(score));
				list.add(degreeId);
				newList.add(list);
			}
			
			//新增记录
			if (newList.size()>0){
				StringBuffer _sql=new StringBuffer("insert into per_target_evaluation ");
				_sql.append("(id,plan_id,object_id,mainbody_id,p0400,score,degree_id)");
				_sql.append("values (?,?,?,?,?,?,?)");
				dao.batchInsert(_sql.toString(),newList); 
			}
			//更新记录
			if (updList.size()>0){
				StringBuffer _sql=new StringBuffer("update per_target_evaluation ");
				_sql.append(" set score=?,degree_id=?");
				_sql.append(" where id =?");
				dao.batchUpdate(_sql.toString(),updList); 
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return b;
 }
     
	/** 
	* @Title: getGradeList 
	* @Description: 获取指标标准标度
	* @param @return
	* @return ArrayList
	*/ 
	public ArrayList getStandardGradeList() {
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select * from per_grade_template order by grade_template_id";
			rs = dao.search(sql);
			LazyDynaBean abean = null;
			while (rs.next()) {
				abean = new LazyDynaBean();
				abean.set("gradedesc", rs.getString("gradedesc"));
				abean.set("grade_template_id", rs.getString("grade_template_id"));
				abean.set("top_value", rs.getString("top_value") != null ? rs
						.getString("top_value") : "");
				abean.set("bottom_value",
						rs.getString("bottom_value") != null ? rs
								.getString("bottom_value") : "");
				abean.set("gradevalue", rs.getString("gradevalue") != null ? rs
						.getString("gradevalue") : "");
				list.add(abean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	
	

	/** 
	* @Title: getDegreeIdByScore 
	* @Description: 通过分数得到 标度值
	* @param @param gradeList 标准标度
	* @param @param p0413 目标卡分值
	* @param @param score 打分分值
	* @param @return
	* @return String
	*/ 
	public String getDegreeIdByScore(ArrayList gradeList,String p0413,String score) {
		String degree_id = "";
		try {
			if (fStandardGradeList==null || fStandardGradeList.size()<1){
				fStandardGradeList=getStandardGradeList();
			}
			for (int j = 0; j < fStandardGradeList.size(); j++) {
				LazyDynaBean gradeBean = (LazyDynaBean) fStandardGradeList.get(j);
				String grade_template_id = (String)gradeBean.get("grade_template_id");
				String _gradevalue = (String)gradeBean.get("gradevalue");
				String top_value = (String)gradeBean.get("top_value");
				String bottom_value = (String)gradeBean.get("bottom_value");

				double value = Double.parseDouble(score);
				double top = Double.parseDouble(PubFunc.multiple(top_value,p0413, 2));
				double bottom = Double.parseDouble(PubFunc.multiple(bottom_value, p0413,2));
				if (value <= top && value >= bottom) {
					degree_id = grade_template_id;
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return degree_id;
	}
	
    
    
    private String quotedDoubleValue(String value) {
        return workPlanUtil.quotedDoubleValue(value);
  }


    public String getPlanType() {
        return planType;
    }


    public void setPlanType(String planType) {
        this.planType = planType;
    }


    public String getPeriodType() {
        return periodType;
    }


    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }


    public String getPeriodYear() {
        return periodYear;
    }


    public void setPeriodYear(String periodYear) {
        this.periodYear = periodYear;
    }


    public String getPeriodMonth() {
        return periodMonth;
    }


    public void setPeriodMonth(String periodMonth) {
        this.periodMonth = periodMonth;
    }


    public String getPeriodWeek() {
        return periodWeek;
    }


    public void setPeriodWeek(String periodWeek) {
        this.periodWeek = periodWeek;
    }


    public String getReturnInfo() {
        return returnInfo;
    }


    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
    public String getQueryType() {
        return queryType;
    }
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    public String getQueryText() {
        return queryText;
    }
    public void setQueryText(String queryText) {
        this.queryText = queryText.trim().replace("＝", "=");
        this.queryText = this.queryText.replace("｜", "|");
        if (queryText.length()>0){
            String arr[]=this.queryText.split("`");
            for(int i=0;i<arr.length;i++){
               String s=arr[i];
               int p=s.indexOf("=");
               if (p>0){
                   String key=s.substring(0,p);
                   String value=s.substring(p+1);
                   value=value.replace("'", "");
                   LazyDynaBean bean =new LazyDynaBean();
                   bean.set("name", key);
                   bean.set("value", value);
                   commonQueryParamList.add(bean);
               }
                
            }
        }
        
    }
    
    /**
   	 * 获取默认查询指标
   	 * @return
   	 */
   	public ArrayList getDefaultQuery(){
   		ArrayList<HashMap<String,String>> defaultQuery = new ArrayList<HashMap<String,String>>();
   		//查询模板预置
           HashMap<String,String> hm = new HashMap<String,String>();
       	hm = new HashMap<String,String>();
       	hm.put("fieldsetid", "A01");
       	hm.put("itemid", "E0122");
       	hm.put("itemdesc", "部门");
       	hm.put("itemtype", "A");
       	hm.put("codesetid", "UM");
       	defaultQuery.add(hm);
       	hm = new HashMap<String,String>();
       	hm.put("fieldsetid", "A01");
       	hm.put("itemid", "A0101");
       	hm.put("itemdesc", "姓名");
       	hm.put("itemtype", "A");
       	hm.put("codesetid", "0");
       	defaultQuery.add(hm);
       	hm = new HashMap<String,String>();
       	hm.put("fieldsetid", "A01");
       	hm.put("itemid", workPlanUtil.getPinYinFld());
       	hm.put("itemdesc", "拼音简码");
       	hm.put("itemtype", "A");
       	hm.put("codesetid", "0");
       	defaultQuery.add(hm);
   		return defaultQuery;
   	}
   	
   	/**
   	 * 公共查询返回info
   	 * @param submit_type
   	 * @param page_size
   	 * @param cur_page
   	 * @return
   	 */
   	public String getCommonQueryDetailList(String submit_type,int page_size,int cur_page) {
        String planInfo="{"+quotedDoubleValue("hjsj")+":"+quotedDoubleValue("1");
        String teamMainInfo="";
        try{
            if ("1".equals(this.viewType)){
                if ("2".equals(this.planType)){
                    teamMainInfo=getMySubDeptMainInfo(submit_type,page_size,cur_page);                        
                }
                else {
                	teamMainInfo=getMyTeamPeopleMainInfo(submit_type,page_size,cur_page);  
                }
            }
            else {
                teamMainInfo=this.getPlanTaskList(submit_type,page_size,cur_page);   
            }
            if (teamMainInfo.length()>0) { //任务视图直接返回， 再包一层不能读取      
                planInfo =planInfo+","+teamMainInfo;
                if ("2".equals(this.viewType)){
                    return "{"+teamMainInfo+"}";
                }
            }
        }     
        catch(Exception e){
            e.printStackTrace();
        }
        planInfo =planInfo+","+getMyTeamInfoTitle(true); 
        planInfo=planInfo+"}";
        String info="{"+quotedDoubleValue("planinfo")+":"+planInfo+"}";    
        
        return info; 
    } 
    
}
