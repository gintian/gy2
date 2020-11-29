package com.hjsj.hrms.businessobject.workplan;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hjsj.hrms.module.workplan.workplanhr.businessobject.WorkPlanHrBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.sun.star.uno.RuntimeException;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:WorkPlanBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-8-11 下午03:20:15</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class WorkPlanBo {
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao;
    private WorkPlanUtil workPlanUtil;
    private PhotoImgBo imgBo;
    private String imgRootPath;
    private DbWizard dbw;
    private String workType; //计划、总结

    //计划所有者
    private String objectId; // 团队id 人员：库+人员编号
    private String nBase; //库
    private String A0100; //人员编号
    private String planType; //查看的计划类型
    private String periodType;//期间类型
    private String periodYear;//年
    private String periodMonth;//月 根据期间类型不同代码月份、季度、上半年
    private String periodWeek;//周
    private int weekNum;//本月周数
    private String deptLeaderId; //团队计划 负责人
    //--------计划信息
    private String planOwner;//计划所有者名字 本人用“我”
    private String P0700;//计划id
    private String P0723;//项目类型 人员计划1 部门2
    private String planSatus;//计划状态
    private String planScope;//计划可见范围
    private RecordVo p07_vo = null;

    //右侧人力地图
    private String curJsp; //当前处理业务的jsp 个人：selfplan 团队： teamplan
    private String subObjectId; //团队成员 用于查看下级成员(下级成员负责岗位  用","连接)
    private String humanMapType=""; //人力地图类型
    private String humanMap_cur_page="1"; //
    private String subPersonFlag;//显示人力地图团队人员时下级岗位是否缺编缺编
    private String needSeeSub;//前台需要穿透查看计划关注人下级的标识

    private String fromflag;

	public void setFromflag(String fromflag) {
		this.fromflag = fromflag;
	}


	public String getNeedSeeSub() {
		return needSeeSub;
	}


	public void setNeedSeeSub(String needSeeSub) {
		this.needSeeSub = needSeeSub;
	}

	//其他
    private String superiorFld; //上级指标 用于多次使用上级指标时
    private String returnInfo; //返回值 其它类调用 存储需返回的信息



    /**与计划无关
     * @param conn
     * @param userview
     */
    public WorkPlanBo(Connection conn,UserView userview) {
        this.conn=conn;
        this.userView=userview;
        dbw = new DbWizard(this.conn);
        dao=new ContentDAO(this.conn);
        workPlanUtil=new WorkPlanUtil(this.conn,userview);
        this.workType =WorkPlanConstant.WorkType.PLAN;

        imgBo = new PhotoImgBo(conn);
        humanMap_cur_page="1";

    }


    /**
     * @throws GeneralException
     * @Title: initPlan
     * @Description: 初始化计划  默认定位我的当前年度计划
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public void initPlan() throws GeneralException {
        // 默认定位年度计划
        String periodtype="";

     	// 填报期间范围权限 chent 20170112 start
        try {
	        WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(this.conn, this.userView);
	        List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
	        String configStr = JSONArray.fromObject(configList).toString();
	        //没有配置年计划时，默认定位到第一个配置的区间上。
	        if(configStr.indexOf("p0") > -1){
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

        initPlan(periodtype);
 }

    public void initPlan(String periodtype) throws GeneralException {
        // 默认定位年度计划
        if (periodtype==null || periodtype.length()<1){
            initPlan();
            return;
        }
        else {
            Date now =new Date();
            int curYear = DateUtils.getYear(now); ;
            int curMonth = DateUtils.getMonth(now);
            this.periodType = periodtype;
            this.periodYear = String.valueOf(curYear);
            this.periodMonth = String.valueOf(curMonth);
            this.nBase= this.userView.getDbname();
            this.A0100= this.userView.getA0100();
            this.objectId = this.nBase+this.A0100;
            this.P0723="1";

            int[] weeks=workPlanUtil.getLocationPeriod(this.periodType,curYear,curMonth);
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

            //工作计划拆分为个人和部门计划 根据plantype来区分查看计划的类型 haosl20161128
            if(!"person".equals(this.planType)){
            	 ArrayList deptlist =workPlanUtil.getDeptList(this.userView.getDbname(),
                         this.userView.getA0100());
                 String b0110 ="";
                 if (deptlist.size()>0){
                     b0110 = (String) ((LazyDynaBean)(deptlist.get(0))).get("b0110");
                     this.objectId= b0110;
                     this.P0723="2";
                     this.deptLeaderId =this.userView.getDbname()+ this.userView.getA0100();
                 }
            }
            initPlanInfo();


        }
 }



    /**
     * @Title: initPlan
     * @Description: 定位当前期间的计划 如果选当前年份，自动定位当前月、季度、半年，如果选当前月份，自动定位当前周
     * @param objectid 所有者
     * @param p0723 计划类型
     *  @param period_type 期间类型
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
     */
    public void LocationCurPeriodPlan(String objectid,String p0723,String periodtype,String periodyear,String periodmonth) {
        this.objectId = objectid;
        this.P0723=p0723;
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
        if (WorkPlanConstant.Cycle.YEAR.equals(this.periodType)){

        }
        else {
            int[] weeks=workPlanUtil.getLocationPeriod(this.periodType,Integer.parseInt(periodyear)
                    ,Integer.parseInt(periodmonth));
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
        initPlanInfo();
    }

    /**
     * @throws GeneralException
     * @Title: initPlan
     * @Description:    初始化计划，通过计划id 初始计划期间信息
     * @param @param p0700 计划id
     * @return void
     * @author:wangrd
     * @throws
    */
    public void initPlan(int p0700) throws GeneralException {
        if (p0700>0) {
            this.p07_vo=getP07Vo(p0700);
        }
        if (this.p07_vo!=null){
            this.P0723=this.p07_vo.getString("p0723");
            this.periodType=this.p07_vo.getString("p0725");
            this.periodYear=this.p07_vo.getString("p0727");
            this.periodMonth=this.p07_vo.getString("p0729");
            this.periodWeek=this.p07_vo.getString("p0731");
            if ("1".equals(this.P0723)){
                this.nBase=this.p07_vo.getString("nbase");
                this.A0100=this.p07_vo.getString("a0100");
                this.objectId=this.nBase+this.A0100;
            }
            else {
                this.objectId=this.p07_vo.getString("p0707");
            }
            initPlanInfo();
        }
        else {
            this.P0700="";
            initPlan();
        }

    }


    /**
     * @Title: initPlan
     * @Description: 年、半年、月度计划
     * @param @param objectid 所有者
     * @param @param p0723 计划类型
     * @param @param periodtype 计划期间
     * @param @param periodyear
     * @param @param periodmonth
     * @param @param periodWeek
     * @return void
     * @author:wangrd
     * @throws
    */
    public void initPlan(String objectid ,
            String p0723,String periodtype,String periodyear,String periodmonth,String periodWeek) {
        this.objectId =objectid;
        this.periodType = periodtype;
        this.periodYear = periodyear;
        this.periodMonth = periodmonth;
        this.periodWeek = periodWeek;

        if (p0723==null || p0723.length()<1){//从我关注的可以切换个人或团队计划 所以这个参数可传过来。
            this.P0723="1";
        }
        else {
            this.P0723=p0723;
        }

        initPlanInfo();

    }

    /**
     * @Title: isMyDirectSubTeamPeople
     * @Description: 当前计划是否是我直接下级的计划
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean isMyDirectSubTeamPeople() {
        boolean b=false;
        if (isMyPlan()){
            return false;
        }
        String myE01a1s=workPlanUtil.getMyE01a1s(this.userView.getDbname(), this.userView.getA0100());
        String e01a1="";
        if ("1".equals(this.P0723)){//个人计划
            if(this.p07_vo!=null){
                e01a1=this.p07_vo.getString("e01a1");
                if (e01a1==null || "".equals(e01a1)){
                	e01a1=workPlanUtil.getPersonVo(this.nBase, this.A0100).getString("e01a1");
                }
            }
            else {
                RecordVo a01vo= workPlanUtil.getPersonVo(this.nBase, this.A0100);
                e01a1=a01vo.getString("e01a1");
            }
        }
        else {
            e01a1 = workPlanUtil.getDeptLeaderE01a1(this.objectId);
        }
        String superE01a1=workPlanUtil.getApprovedSuperE01a1(e01a1);

        //superE01a1为空的时候(",,xxx,").indexOf(",,"); 巧合导致判断错误   haosl  bug 36115
        if (StringUtils.isNotBlank(superE01a1) && (","+myE01a1s+",").indexOf(","+superE01a1+",")>-1){
        	 b=true;
        }

        return b;
    }


    public boolean isMyDirectSubTeamPeople(String parentObjectid) {
        boolean b=false;
        if (isMyPlan()){
            return false;
        }
        String nbase="";
        String a0100="";
        if ( parentObjectid!=null && parentObjectid.length()>0){
            nbase = parentObjectid.substring(0, 3);
            a0100 = parentObjectid.substring(3, this.objectId.length());
        }

        String myE01a1s=workPlanUtil.getMyE01a1s(nbase, a0100);
        String e01a1="";
        if ("1".equals(this.P0723)){//个人计划
            if(this.p07_vo!=null){
                e01a1=this.p07_vo.getString("e01a1");
            }
            else {
                RecordVo a01vo= workPlanUtil.getPersonVo(this.nBase, this.A0100);
                e01a1=a01vo.getString("e01a1");
            }
        }
        else {
            e01a1 = workPlanUtil.getDeptLeaderE01a1(this.objectId);
        }
        if (workPlanUtil.isMySubE01a1(myE01a1s, e01a1)){
            b=true;
        }
        return b;
    }

    /** 当前计划是否是我下级的计划 */
	public boolean isMySubTeamPeople() {
		if (isMyPlan()) {
			return false;
		}
		if(this.fromflag!=null&& "hr_create".equals(this.fromflag)) //来自工作计划制定
        {
            return true;
        }
		if ("1".equals(this.P0723)) { // 个人计划
			return workPlanUtil.isMyTeamPeople(this.nBase, this.A0100);
		} else if ("2".equals(this.P0723)) { // 团队计划
			return workPlanUtil.isMyTeamDept(this.objectId);
		}

		return false;
	}
	 /** 计划部门是否和我的部门相同 */
	public boolean isSameToMyTeamPlan() {
		if (isMyPlan()) {
			return false;
		}

		if ("1".equals(this.P0723)) { // 个人计划
			WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
			return wpbo.isMyDirectSubTeamPeople();
		} else if ("2".equals(this.P0723)) { // 团队计划
			return workPlanUtil.isMyTeamDept(this.objectId);
		}

		return false;
	}
	/**
	 * 判断当前登录人是否是计划指派的审批人
	 * @param p0700 计划号
	 * @return
	 */
	public boolean isP0733(String p0700) {
		boolean isP0733 = false;

		//计划未制定  haosl 20171222
		if("".equals(p0700) || "0".equals(p0700)) {
			return isP0733;
		}

		ContentDAO dao = new ContentDAO(this.conn);
		try {

			RecordVo vo = new RecordVo("p07");
        	int planId = Integer.parseInt(p0700);
        	vo.setInt("p0700", planId);
            vo = dao.findByPrimaryKey(vo);
            String p0733 = vo.getString("p0733");

            String nbsa0100 = this.userView.getDbname()+this.userView.getA0100();
            if(StringUtils.isNotEmpty(p0733) && p0733.equalsIgnoreCase(nbsa0100)) {
            	isP0733 = true;
            }
		} catch(Exception e) {
			e.printStackTrace();
		}

		return isP0733;
	}

    /**
     * @Title: checkIsCanReadPlan
     * @Description: //检查是否有查看此人计划的权限
     * @param
     * @return void
     * @author:wangrd
     * @throws
    */
    public boolean checkIsCanReadPlan() {
        boolean b=false;
        boolean bhr = "".equals(this.userView.getA0100());

        if ("".equals(this.P0700)){//没有制定计划
            return true;
        }
        if(this.p07_vo==null){
            return true;
        }
        if ("teamplan".equals(this.curJsp)){
            return true;
        }
        if (isMyPlan()){
            return true;
        }
        try{
            if(bhr){
                String e01a1="";
                if ("1".equals(this.P0723)){//个人计划
                    if(this.p07_vo!=null){
                        e01a1=this.p07_vo.getString("e01a1");
                    }
                }
                else{
                    e01a1 =workPlanUtil.getDeptLeaderE01a1(this.objectId);
                }
                return workPlanUtil.isMyManageE01a1(e01a1);
            }
            else {
                int rowCount=0;
                String strsql="";
                String b0110=this.userView.getUserOrgId();
                String e0122=this.userView.getUserDeptId();
                if ("1".equals(this.P0723)){//个人计划
                    //是否是我的下级
                    if(this.p07_vo!=null){
                        String e01a1=this.p07_vo.getString("e01a1");
                        String supere01a1=this.p07_vo.getString("supere01a1");
                        String myE01a1s=workPlanUtil.getMyE01a1s(this.userView.getDbname(), this.userView.getA0100());
                        if (workPlanUtil.isMySubE01a1(myE01a1s, e01a1)){
                            return true;
                        }
                        if (supere01a1!=null && supere01a1.length()>0){//按上级岗位查
                            if (workPlanUtil.isMySubE01a1(myE01a1s, supere01a1)){
                                return true;
                            }

                            if ((","+myE01a1s+",").indexOf(supere01a1)>-1){
                                return true;
                            }
                        }

                    }
                    /**
                if (workPlanUtil.isMyTeamPeople(this.nBase, this.A0100)){
                    return true;
                }
                     */

                    //我关注的
                    strsql="select count(*) cnt from  p07,P09 "
                        +" where P09.p0903=P07.p0700 and  "
                        +" P09.P0901=1 and P09.P0905=3 "
                        +" and P09.Nbase='"+this.userView.getDbname()+"'"
                        +" and P09.a0100='"+this.userView.getA0100()+"'"
                        +" and p07.Nbase='"+this.nBase+"'"
                        +" and p07.a0100='"+this.A0100+"'"
                        +" " ;
                    strsql=strsql+" and p07.p0723 =1 and "+getPlanPublicWhereByStatus();
                    try{
                        RowSet rset=dao.search(strsql);
                        if (rset.next()){
                            rowCount=rset.getInt(1);
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    if (rowCount>0){
                        return true;
                    }

                    //可见范围内的
                    strsql="select count(*) cnt from  p07 "
                        +" where  (P0721=3 "
                        +" or (P07.p0721=1 and  p07.b0110 like '"+b0110+"%') "
                        +" or (P07.p0721=2 and  p07.e0122 ='"+e0122+"')"
                        +")"
                        + " and Nbase='"+this.nBase+"'"
                        +" and a0100='"+this.A0100+"'";
                    strsql=strsql+" and p07.p0723 =1 and "+getPlanPublicWhereByStatus();
                    try{
                        RowSet rset=dao.search(strsql);
                        if (rset.next()){
                            rowCount=rset.getInt(1);
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    if (rowCount>0){
                        return true;
                    }
                }
                else{
                    //是否是我的部门
                    if (workPlanUtil.isMyTeamDept(this.objectId)){
                        return true;
                    }

                    //我关注的
                    strsql="select count(*) cnt from  p07,P09 "
                        +" where P09.p0903=P07.p0700 and  "
                        +" P09.P0901=1 and P09.P0905=3 "
                        +" and P09.Nbase='"+this.userView.getDbname()+"'"
                        +" and P09.a0100='"+this.userView.getA0100()+"'"
                        +" and P07.p0707='"+this.objectId+"'"
                        +" " ;
                    strsql=strsql+" and p07.p0723 =2 and "+getPlanPublicWhereByStatus();
                    try{
                        RowSet rset=dao.search(strsql);
                        if (rset.next()){
                            rowCount=rset.getInt(1);
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    if (rowCount>0){
                        return true;
                    }

                    //可见范围内的
                    strsql="select count(*) cnt from  p07 "
                        +" where  (P0721=3 "
                        +" or (P07.p0721=1 and  p07.P0707 like '"+b0110+"%') "
                        +" or (P07.p0721=2 and  p07.P0707 ='"+e0122+"')"
                        +")"
                        + " and p0707='"+this.objectId+"'"
                        +"";
                    strsql=strsql+" and p07.p0723 =2 and "+getPlanPublicWhereByStatus();
                    try{
                        RowSet rset=dao.search(strsql);
                        if (rset.next()){
                            rowCount=rset.getInt(1);
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    if (rowCount>0){
                        return true;
                    }

                }


            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;
    }


    /**
     * @Title: initPlanInfo
     * @Description: 初始当前计划所有者信息 计划所有者objectid，部门计划负责人信息
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    private boolean initPlanInfo() {
        boolean b=false;
        try
        {
            if (!"teamplan".equals(this.curJsp)){
                if ("1".equals(this.P0723)){//个人计划
                    if ( this.objectId==null||"".equals(this.objectId)){
                        this.nBase=this.userView.getDbname();
                        this.A0100=this.userView.getA0100();
                    }
                    else {
                        if (this.objectId.length()<3){
                            return b;
                        }
                        this.nBase = this.objectId.substring(0, 3);
                        this.A0100 = this.objectId.substring(3, this.objectId.length());
                    }
                }
                else if ("2".equals(this.P0723)){//部门计划
                    if ( this.objectId==null||"".equals( this.objectId)){//无部门
                        /*
                   if(deptLeaderId==null || "".equals(deptLeaderId)) {//无负责人  取当前登录用户
                       this.nBase=this.userView.getDbname();
                       this.A0100=this.userView.getA0100();
                       this.deptLeaderId = this.nBase+this.A0100;
                   }
                   else {//有负责人
                       this.nBase =deptLeaderId.substring(0, 3);
                       this.A0100 =deptLeaderId.substring(3, deptLeaderId.length());
                   }
                  //确定负责的第一个部门
                   ArrayList deptlist=workPlanUtil.getDeptList(this.nBase, this.A0100);
                   if (deptlist.size()>0){
                       LazyDynaBean bean= (LazyDynaBean)deptlist.get(0);
                       this.objectId=(String)bean.get("b0110");
                   }
                   if ( this.objectId==null||"".equals( this.objectId)){//无部门
                       this.returnInfo="无负责的部门";//

                   }
                         */
                    }
                    else {//有部门
                        if(deptLeaderId==null || "".equals(deptLeaderId)) {//无负责人  确定负责人 随机查找一个。
                            this.deptLeaderId = workPlanUtil.getFirstDeptLeaders(this.objectId);
                        }
                        if (deptLeaderId==null||"".equals(deptLeaderId)){//无部门
                            this.returnInfo="无负责人";
                        }
                        else {
                            this.nBase =deptLeaderId.substring(0, 3);
                            this.A0100 =deptLeaderId.substring(3);
                        }
                    }
                }
                this.p07_vo=null;
                loadPlanInfo();
            }
            else {
            	if("1".equals(this.P0723)){//个人计划
	            	if ( this.objectId==null||"".equals( this.objectId)){
	                    this.nBase=this.userView.getDbname();
	                    this.A0100=this.userView.getA0100();
	                }
	                else {
	                    if (this.objectId.length()<3){
	                        return b;
	                    }
	                    this.nBase = this.objectId.substring(0, 3);
	                    this.A0100 = this.objectId.substring(3, this.objectId.length());
	                }
            	}else{//部门计划
            		if(StringUtils.isBlank(this.nBase)|| StringUtils.isBlank(this.A0100)){
            			this.nBase = this.userView.getDbname();
            			this.A0100=this.userView.getA0100();
            		}
            	}
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return b;
    }

    public RecordVo getP07Vo(int p0700) {
        RecordVo vo = new RecordVo("p07");
        try {
            vo.setInt("p0700",p0700);
            ContentDAO dao = new ContentDAO(this.conn);
            vo = dao.findByPrimaryKey(vo);
            this.P0700=String.valueOf(p0700);
            this.planSatus= vo.getString("p0719");
            this.planScope= vo.getString("p0721");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    /**
     * @Title: loadPlanInfo
     * @Description:  根据计划期间信息 获取当前计划id等信息
     * @param
     * @return void
     * @author:wangrd
     * @throws
    */
    public void loadPlanInfo() {
        String strsql="select * from p07 where p0723="+this.P0723 +" and p0725="+this.periodType
                       +" and p0727="+this.periodYear;
        if ("2".equals(this.P0723)){
            strsql= strsql+" and p0707 ='"+this.objectId+"'";
        }
        else {
            strsql= strsql+" and nbase ='"+this.nBase+"'" +" and a0100 ='"+this.A0100+"'";

        }
        //32676 linbz SQL拼接错误，增加为空校验
        if (!WorkPlanConstant.Cycle.YEAR.equals(this.periodType) && StringUtils.isNotEmpty(this.periodMonth)){
            strsql= strsql+" and P0729="+this.periodMonth;
        }
        if (WorkPlanConstant.Cycle.WEEK.equals(this.periodType) && StringUtils.isNotEmpty(this.periodWeek)){
            strsql= strsql+" and P0731="+this.periodWeek;
        }

        this.P0700="";
        this.planSatus="";
        try{
            RowSet rset=dao.search(strsql);
            if (rset.next()){
                this.P0700= rset.getString("p0700");
                this.p07_vo=getP07Vo(Integer.parseInt(this.P0700));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @Title: isMyPlan
     * @Description: 是否是本人的计划
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean isMyPlan() {
        boolean b=false   ;
        try{
            if ((this.userView.getDbname().equals(this.nBase))&&(this.userView.getA0100().equals(this.A0100))){
              b=true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }

    public void loadPersonInfo() {
        this.planOwner="";
        String nbase=this.nBase;
        String a0100=this.A0100;
        if ("selfplan".equals(this.curJsp)){
            if (isMyPlan()){
                this.planOwner="我";
                return;
            }
        }
        else {
            if ("2".equals(this.humanMapType)){
                if(this.subObjectId!=null && subObjectId.length()>0) {
                		  nbase =subObjectId.substring(0, 3);
                          a0100 =subObjectId.substring(3);
                }
            }
            else if ("3".equals(this.humanMapType)) {
                if(this.subObjectId!=null && this.subObjectId.length()>0){
                    String leader=workPlanUtil.getFirstE01a1Leaders(this.subObjectId);
                    if (leader.length()>3){
                        nbase =leader.substring(0, 3);
                        a0100 =leader.substring(3);
                    }
                }
            }
            if ((this.userView.getDbname().equals(nbase))&&(this.userView.getA0100().equals(a0100))){
                this.planOwner="我";
                return;
            } else{

            }
        }
        String strsql="";
    	if("false".equals(subPersonFlag)&& "teamplan".equals(curJsp)){
  		    strsql="select * from organization where codeitemid='"+subObjectId+"'";
    	}else{
    		//无负责人时直接返回  haosl  2018-5-11
    		if(StringUtils.isBlank(nbase)&&StringUtils.isBlank(a0100)) {
                return;
            }
			strsql="select * from "+nbase+"A01 where a0100='"+a0100+"'";
    	}
        try{
            RowSet rset=dao.search(strsql);
            if (rset.next()){
            	if("false".equals(subPersonFlag)&& "teamplan".equals(curJsp)){
            		this.planOwner= rset.getString("codeitemdesc");
            	}else{
            		this.planOwner= rset.getString("a0101");
            	}
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


    /**
     * @Title: getPlanPublicInfo
     * @Description: 获取计划公共信息  页头 计划类型 及期间
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getPlanPublicInfo() {
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

        //我的照片
        String my_image =getPhotoPath(this.userView.getDbname(), this.userView.getA0100());
        planInfo =planInfo+","
            +quotedDoubleValue("my_image")+":"+quotedDoubleValue(my_image)
            +","
            +quotedDoubleValue("my_name")+":"+quotedDoubleValue(workPlanUtil.getTruncateA0101(this.userView.getUserFullName()))
            +","
            +quotedDoubleValue("my_fullname")+":"+quotedDoubleValue(this.userView.getUserFullName())
            +"";
        this.weekNum=4;//本月周数
        if (WorkPlanConstant.Cycle.WEEK.equals(this.periodType)){
            this.weekNum=workPlanUtil.getWeekNum(this.periodYear, this.periodMonth);

        }
        planInfo =planInfo+","+quotedDoubleValue("week_num")+":"+quotedDoubleValue(String.valueOf(this.weekNum));

        return planInfo;

    }


    /**
     * @Title: getHumanMap
     * @Description:获取人力地图信息
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getHumanMap(boolean bInit) {
        String info="";
        String concerned_title="";    //关注标题
        //27404 关键目标--个人计划，点击我的团队，标题栏显示不全，如图   haosl 20170508
        String nbase=this.userView.getDbname();
        String a0100=this.userView.getA0100();
        if ("".equals(a0100)){
            return info;
        }
        String objectid=this.nBase+this.A0100;
        String teamMainInfo="";
        //是否显示人力地图下拉图片
        String displayDropDownImg="";
        //是否显示团队列表图片
        String displayTeamListImg="false";
        //是否显示人力地图
        String displayHumanMap="true";
        try{
            ArrayList typelist =new ArrayList();
            if(bInit){
                typelist = getHummapTypeList();
                displayDropDownImg="false";
                if (typelist.size()>0){
                    if (typelist.size()>1) {
                        displayDropDownImg="true";
                    }
                    //验证 当前人力地图类型是否存在
                    boolean b=false;
                    for (int i=0;i<typelist.size();i++){
                        LazyDynaBean bean =(LazyDynaBean)typelist.get(i);
                        if (humanMapType.equals((String)bean.get("type_id"))){
                            b=true;
                            break;
                        }
                    }
                    if( humanMapType.length()<1 || !b){
                        LazyDynaBean bean =(LazyDynaBean)typelist.get(0);
                        humanMapType=(String)bean.get("type_id");
                    }
                }
            }

            ArrayList e01a1list = new ArrayList();
            //
            if (("teamplan".equals(this.curJsp)
                    || "2".equals(humanMapType)|| "3".equals(humanMapType)) || "yes".equals(needSeeSub)) {
                if ("2".equals(humanMapType) || "yes".equals(needSeeSub)){
                      if (subObjectId!=null && !"".equals(subObjectId)){
                    	  if(!"false".equals(subPersonFlag)){
                    		  nbase =subObjectId.substring(0, 3);
                              a0100 =subObjectId.substring(3);
                    	  }

                    }
                }
                else{
                    if (subObjectId!=null && !"".equals(subObjectId)){
                        objectid=subObjectId;
                        String [] arre01a1 =subObjectId.split(",");
                        for (int i=0;i<arre01a1.length;i++){
                            String e01a1= arre01a1[i];
                            if ("".equals(e01a1)) {
                                continue;
                            }
                            LazyDynaBean bean = new LazyDynaBean();
                            bean.set("e01a1", e01a1);
                            e01a1list.add(bean);
                        }
                    }
                    else {
                        e01a1list=  workPlanUtil.getMyE01a1List(nbase, a0100);
                        objectid="";
                    }
                }
            }
       //人力地图
            initSuperiorFld();
            int cur_page=Integer.parseInt(humanMap_cur_page);
            if ("1".equals(humanMapType)){
                concerned_title="我的部门";
                info=getMyDeptJsonList(nbase, a0100,cur_page);
            }
            else if ("2".equals(humanMapType) || "yes".equals(needSeeSub)){
                concerned_title="团队成员";
                if("false".equals(subPersonFlag)){//根据岗位来查找团队成员
                	//由当前岗位查找下级岗位
                	 objectid=subObjectId;
                	 ArrayList e01a1List=new ArrayList();
                	 String sql="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+this.superiorFld+"='"+this.getSubObjectId()+"'";
	       			 RowSet rset=dao.search(sql);
	       			 while(rset.next()){
	       				 LazyDynaBean e01a1bean = new LazyDynaBean();
	       				 e01a1bean.set("e01a1", rset.getString("e01a1"));
	       				 e01a1bean.set("codeitemdesc", rset.getString("codeitemdesc"));
	       				 e01a1List.add(e01a1bean);
	       			 }

                	info=getMyTeamPeopleList(e01a1List,cur_page);
                }else{
            		objectid=nbase+a0100;
            		info=getMyTeamPeopleList(nbase,a0100, cur_page);
                }
                displayTeamListImg="true";
            }
            else if( "3".equals(humanMapType)){
                concerned_title="下属部门";
                info=getMySubDeptList(e01a1list,cur_page);
                displayTeamListImg="true";
            }
            else if ("4".equals(humanMapType) && !"yes".equals(needSeeSub)) {
                concerned_title="我关注的";
                boolean flag = false;//是否需要监控计划关注人的下级成员
                if("HJSJ".equals(SystemConfig.getPropertyValue("clientName"))){
                	flag = true;
                }
                if(flag){//true时,计划关注人能够进行穿透,修改方法
                	info =getMeConcernedAndCteamJson(cur_page);
                }else{
                	info =getMeConcernedJson(cur_page);
                }
                if (info.length()<1){//无关注人
                    if (typelist.size()==1){//不显示人力地图
                      //  displayHumanMap="false";  暂时屏蔽
                    }
                }
            }
        //左侧显示列表信息
            if ("teamplan".equals(this.curJsp)) {
                if ("2".equals(humanMapType)){
                    this.P0723="1";
                    if("false".equals(subPersonFlag)){//根据岗位来显示团队成员左侧列表信息
                    	//由当前岗位查找下级岗位
                    	ArrayList e01a1List=new ArrayList();
                    	 String sql="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+this.superiorFld+"='"+this.getSubObjectId()+"'";
    	       			 RowSet rset=dao.search(sql);
    	       			 while(rset.next()){
    	       				 LazyDynaBean e01a1bean = new LazyDynaBean();
    	       				 e01a1bean.set("e01a1", rset.getString("e01a1"));
    	       				 e01a1bean.set("codeitemdesc", rset.getString("codeitemdesc"));
    	       				 e01a1List.add(e01a1bean);
    	       			 }
                    	teamMainInfo=getMyTeamPeopleMainInfo(e01a1List);
                    }else{
                    	teamMainInfo=getMyTeamPeopleMainInfo(nbase, a0100);
                    }
                }
                else{
                    this.P0723="2";
                    teamMainInfo =getMySubDeptMainInfo(e01a1list);
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        info=quotedDoubleValue("concerneders")+":["+info+"]";

        info =info+","
            +quotedDoubleValue("concerned_title")+":"+quotedDoubleValue(concerned_title);
        info =info+","
            +quotedDoubleValue("isperson")+":"+quotedDoubleValue(subPersonFlag);
        info =info+","
            +quotedDoubleValue("concerned_cur_page")+":"+quotedDoubleValue(humanMap_cur_page+"");
        info =info+","
            +quotedDoubleValue("concerned_bteam")+":"+quotedDoubleValue(humanMapType);
        info =info+","
            +quotedDoubleValue("concerned_objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(objectid));
        info=info+","
            +quotedDoubleValue("display_dropdown_img")+":"+quotedDoubleValue(displayDropDownImg);
        info=info+","
            +quotedDoubleValue("display_team_list_img")+":"+quotedDoubleValue(displayTeamListImg);
        info=info+","
        +quotedDoubleValue("display_human_map")+":"+quotedDoubleValue(displayHumanMap);
        if ("teamplan".equals(this.curJsp)){
            loadPersonInfo();
            info=info+","
                +quotedDoubleValue("team_plan_title")+":"+quotedDoubleValue(getCurFullPlanTitle());
        }
        info=quotedDoubleValue("human_map")+":{"+info+"}";
        //团队主页面展现
        if (teamMainInfo.length()>0) {
            info =info+","+teamMainInfo;
        }

       return info;
    }

    /**
     * 得到导出excel文件的名称
     * @param periodType
     * @param periodYear
     * @param periodMonth
     * @param periodWeek
     * @param objectId
     * @param P0723
     * @return
     */
    public String getExportPlanName(String periodType,String periodYear,String periodMonth,String periodWeek,String objectId,String P0723) {
        String plan_period=workPlanUtil.getPlanPeriodDesc(periodType,periodYear,
                periodMonth,periodWeek);

        String plan_title=this.userView.getUserName()+"_";
            if ("1".equals(P0723)){
                plan_title+=plan_period+"工作计划("+this.userView.getUserFullName()+")";
            }
            else {
                String deptdesc =workPlanUtil.getOrgDesc(objectId);
                plan_title+=plan_period+"工作计划("+deptdesc+")";
            }

        return plan_title;

    }


    private String getCurFullPlanTitle() {
        String plan_period=workPlanUtil.getPlanPeriodDesc(this.periodType,this.periodYear,
                this.periodMonth,this.periodWeek);

        String plan_title="";
        if ("selfplan".equals(this.curJsp)){
            if ("1".equals(this.P0723)){
                plan_title=this.planOwner+"的"+plan_period+"工作计划";
            }
            else {
                ArrayList deptlist=workPlanUtil.getDeptList(this.nBase, this.A0100);
                String deptdesc="";
                if ((this.objectId==null) ||("".equals(this.objectId))){
                    if (deptlist.size()>0){
                        LazyDynaBean bean= (LazyDynaBean)deptlist.get(0);
                        deptdesc =(String)bean.get("deptdesc");
                        this.objectId=(String)bean.get("b0110");
                        loadPlanInfo();
                    }
                }
                else {
                     deptdesc =workPlanUtil.getOrgDesc(this.objectId);
                }

                plan_title= plan_title+"的";
                if (isMyPlan()){
                    plan_title=deptdesc+"("+this.userView.getUserFullName()+") "+plan_period+"计划";
                }
                else {
                    if ("".equals(this.planOwner)) {
                        plan_title=deptdesc+" "+plan_period+"计划";
                    } else {
                        plan_title=deptdesc+"("+this.planOwner+") "+plan_period+"计划";
                    }
                }
            }
        }
        else {
            if ("2".equals(this.humanMapType)){
                plan_title=this.planOwner+"的团队的"+plan_period+"计划";
            }
            else {
                if (isMyPlan()){
                    plan_title=this.planOwner+"的下属部门的"+plan_period+"计划";
                }
                else {
                    if ("".equals(this.planOwner)) {
                        plan_title="下属部门的"+plan_period+"计划";
                    } else {
                        plan_title=this.planOwner+"的下属部门的"+plan_period+"计划";
                    }
                }
            }
        }

        return plan_title;

    }


    /**
     * @Title: getPlanDetailInfo
     * @Description: 获取当前计划的详细信息
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getPlanDetailInfo() {

        String planInfo="";
        String plan_owner_image ="";
        plan_owner_image=getPhotoPath(this.nBase, this.A0100);
        String plan_title=quotedDoubleValue("plan_title")+":"+quotedDoubleValue(getCurFullPlanTitle());

        String object_id=this.objectId;
        planInfo=plan_title;
        planInfo=planInfo+","
            +quotedDoubleValue("object_id")+":"+quotedDoubleValue(WorkPlanUtil.encryption(object_id))
            +"";
        if (!"selfplan".equals(this.curJsp)){
            return planInfo;
        }
        //计划人照片
        planInfo =planInfo+","
            +quotedDoubleValue("plan_owner_image")+":"+quotedDoubleValue(plan_owner_image);
        //计划期间
        planInfo =planInfo+","
            +quotedDoubleValue("p0725")+":"+quotedDoubleValue(WorkPlanUtil.encryption(this.periodType));
        //计划类型
        planInfo =planInfo+","
            +quotedDoubleValue("p0723")+":"+quotedDoubleValue(WorkPlanUtil.encryption(this.P0723));
        //计划类型 不加密 供前端使用
        planInfo =planInfo+","
            +quotedDoubleValue("display_plan_type")+":"+quotedDoubleValue(this.P0723);
        //计划id
        if ("".equals(P0700)){
            planInfo =planInfo+","
            +quotedDoubleValue("p0700")+":"+quotedDoubleValue("");
        }
        else {
            planInfo =planInfo+","
            +quotedDoubleValue("p0700")+":"+quotedDoubleValue(WorkPlanUtil.encryption(this.P0700));
        }
        //计划状态
        planInfo =planInfo+","
            +quotedDoubleValue("plan_status")+":"+quotedDoubleValue(this.planSatus);
        planInfo =planInfo+","
        +quotedDoubleValue("plan_status_desc")+":"+quotedDoubleValue(workPlanUtil.getPlanStatusDesc("0".equals(this.planSatus)?"10":this.planSatus));
        //计划可见范围
        planInfo =planInfo+","
            +quotedDoubleValue("plan_scope")+":"+quotedDoubleValue(this.planScope);
        planInfo =planInfo+","
            +quotedDoubleValue("plan_scope_desc")+":"+quotedDoubleValue(workPlanUtil.getPlanScopeDesc(this.planScope));
        //关注我的
        planInfo =planInfo+","+getFollowerList(this.P0700);
        if ("selfplan".equals(this.curJsp)){
            if ("1".equals(this.P0723)){
                planInfo =planInfo+","+quotedDoubleValue("follow_owner")+":"+quotedDoubleValue(this.planOwner);
            }
            else {
                planInfo =planInfo+","+quotedDoubleValue("follow_owner")+":"+quotedDoubleValue(workPlanUtil.getOrgDesc(this.objectId));
            }
        }

        //部门负责人
        planInfo=planInfo+","
            +quotedDoubleValue("dept_leader")+":"+quotedDoubleValue(WorkPlanUtil.encryption(this.deptLeaderId));
        //是否可编辑
        String is_myplan="false";
        String is_subplan="false";
        if (isMyPlan()){
            is_myplan="true";
        }
        else {
            if (isMySubTeamPeople()){
                is_subplan="true";
            }
        }
        planInfo=planInfo+","
        +quotedDoubleValue("is_subplan")+":"+quotedDoubleValue(is_subplan);
        planInfo=planInfo+","
        +quotedDoubleValue("is_myplan")+":"+quotedDoubleValue(is_myplan);
        //是否是我的直接下级
        boolean bsub=isMyDirectSubTeamPeople();
        planInfo =planInfo+","+quotedDoubleValue("direct_sub_people")+":"+quotedDoubleValue(bsub?"true":"false");

        // 当前登录人是否是计划指派的审批人 chent add 20171218
        planInfo=planInfo+","
        		+quotedDoubleValue("is_p0733")+":"+quotedDoubleValue(String.valueOf(this.isP0733(this.P0700)));

        return planInfo;

    }


    /**
     * @Title: getPlanInfoList
     * @Description:  获取计划信息
     * @param @param bhavePublicInfo 是否取公用信息 页头 及右侧人力地图
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getPlanInfoList(boolean bhavePublicInfo, String needRefresh) {
    	loadPersonInfo();
        String planInfo="{"+quotedDoubleValue("hjsj")+":"+quotedDoubleValue("1");
        if (bhavePublicInfo) {
            planInfo =planInfo+","+getPlanPublicInfo();
        }
        //显示人力地图
        String humanInfo = "";
        if(!"no".equals(needRefresh)){
        	humanInfo =	getHumanMap(bhavePublicInfo);
        }
        if (humanInfo.length()>0){
            planInfo=planInfo+","+humanInfo;
        }
        planInfo =planInfo+","+getPlanDetailInfo();
        planInfo=planInfo+"}";
        String info="{"+quotedDoubleValue("planinfo")+":"+planInfo+"}";
        return info;

    }


    /**
     * @Title: getFollowerList
     * @Description: 获取关注我的人员
     * @param @param planid
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getFollowerList(String planid) {
        return getFollowerList(planid,"","");

    }

    /**
     * @Title: getFollowerList
     * @Description:获取关注我的人员
     * @param @param planid
     * @param @param _nbase 具体到某个人
     * @param @param _a0100
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getFollowerList(String planid,String _nbase,String _a0100) {
        String info="";
        if ("".equals(planid)) {
            planid="0";
        }
        String strsql="select * from p09 where P0901=1 and p0905=3 and p0903="+planid;
        if (_nbase!=null && _nbase.length()>0){
            strsql=strsql+" and nbase ='"+_nbase+"'";
        }
        if (_a0100!=null && _a0100.length()>0){
            strsql=strsql+" and a0100 ='"+_a0100+"'";
        }
        try{
            RowSet rset=dao.search(strsql);
            while (rset.next()){
                String nbase= rset.getString("nbase");
                String a0100= rset.getString("a0100");
                String imagepath =getPhotoPath(nbase, a0100);
                String name=rset.getString("P0913");

                String stritem="{"
                    +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(nbase+a0100))
                    +","
                    +quotedDoubleValue("name")+":"+quotedDoubleValue(workPlanUtil.getTruncateA0101(name))
                    +","
                    +quotedDoubleValue("fullname")+":"+quotedDoubleValue(name)
                    +","
                    +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
                    +"}"
                    ;
                if ("".equals(info)){
                    info = stritem;
                }
                else {
                    info =info+","+ stritem;

                }
            }
            info=quotedDoubleValue("follower")+":["+info+"]";

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return info;

    }


    /**
     * @Title: delFollower
     * @Description:    删除关注人
     * @param @param planid
     * @param @param _nbase
     * @param @param _a0100
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean delFollower(String planid,String _nbase,String _a0100) {
        boolean b=false;
        if ("".equals(planid)) {
            planid="0";
        }

        try{
            String strsql="delete from p09 where P0901=1 and p0905=3 and p0903="+planid;
            	if(StringUtils.isNotEmpty(_nbase) && StringUtils.isNotEmpty(_a0100)) {
            		strsql+=" and nbase ='"+_nbase+"'";
            		strsql+=" and a0100 ='"+_a0100+"'";
            	}
            dao.update(strsql);
            if (this.userView.getDbname().equals(_nbase) && this.userView.getA0100().equals(_a0100)){

            }
            else {
            	if(StringUtils.isNotEmpty(_nbase) && StringUtils.isNotEmpty(_a0100)) {
            		sendEmailToFollower(planid,_nbase,_a0100,false);
            	}
            }
            b=true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;

    }


    /**
     * @Title: getMeConcernedScopeSql
     * @Description:获取我关注的成员 sql
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    private String getMeConcernedScopeSql() {
        String sql="";
        String b0110=this.userView.getUserOrgId();
        String e0122=this.userView.getUserDeptId();//当前人所在的部门

        //sql="select * from organization where parentid='"+e0122+"'and codesetid='um'";
        //所有的上级部门  P0707 in ('研发二部','产品研发部'，'研发中心');
        String deptId=disposeSqlStr(workPlanUtil.getParentDeptId(e0122));

        //所有的上级单位
        String unitId=disposeSqlStr(workPlanUtil.getParentUnitId(b0110));

        //我关注的团队
        sql="select P07.p0700,P07.nbase,P07.a0100,P07.P0707,P07.p0723,O.codesetid,O.A0000 "
            +",O.codeitemdesc as a0101,'' as e01a1,p07.b0110 as b0110,'' as e0122 "
            +" from p07,organization O "
            +" where p07.p0707 = O.codeitemid "
            +" and ( P07.p0700 in (select p0903 from p09 where "
            +" P0901=1 and P0905=3 "
            +" and Nbase='"+this.userView.getDbname()+"'"
            +" and a0100='"+this.userView.getA0100()+"'"
            +") or (P07.P0721=3 "
            +" or (P07.p0721=1 and p07.b0110 in ("+unitId+")) "
            +" or (P07.p0721=2 and p07.P0707 in ("+deptId+"))"
            +")"
            +")";
        sql=sql+" and p07.p0723 =2 and "+getPlanPublicWhereByStatus();

        //我关注的个人
        String [] arrpre=workPlanUtil.getSelfUserDbs();
        for (int i=0;i<arrpre.length;i++){
            String pre = arrpre[i];
            String a01tab=pre+"a01";
            sql=sql+" union "
                +"select P07.p0700,P07.nbase,P07.a0100,P07.P0707 ,P07.p0723,'' as cdoesetid ,A01.A0000"
                +",A01.a0101,p07.e01a1,p07.b0110,p07.e0122"
                +" from p07," +a01tab+" A01"
                +" where p07.a0100 = A01.a0100 "
                +" and  ( P07.p0700 in (select p0903 from p09 where "
                +" P0901=1 and P0905=3 "
                +" and Nbase='"+this.userView.getDbname()+"'"
                +" and a0100='"+this.userView.getA0100()+"'"
                +") or (P07.P0721=3 "
                +" or (P07.p0721=1 and  p07.b0110 in ("+unitId+")) "
                +" or (P07.p0721=2 and p07.e0122 in ("+deptId+"))"
                +")"
                +")"
                +" and p07.nbase='"+pre+"'";
            sql=sql+" and p07.p0723 =1 and "+getPlanPublicWhereByStatus();

        }
        sql=sql+"  order by P0723 desc, a0000 ";
        return sql;
    }
    /**
     * 将以逗号拼接的id字符串处理成能够让sql语句中in识别的字符串
     * @param oldId
     * @return
     */
    private String disposeSqlStr(String oldId){
    	 String [] ids=oldId.split(",");
         String newId="";
         for(int i=0;i<ids.length;i++){
        	 newId="'"+ids[i]+"',"+newId;
         }
         newId=newId.substring(0, newId.length()-1);
		 return newId;
    }


    /**
     * @Title: isMyScopePerson
     * @Description: 判断此人是否是我可见范围内的人 不是@的
     * @param @param p0700
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    private boolean isMyScopePerson(String p0700) {
        boolean b=false;
        String sql="";
        try{
            String b0110=this.userView.getUserOrgId();
            String e0122=this.userView.getUserDeptId();
            //我关注的团队
            sql="select P07.p0700,P07.nbase,P07.a0100,P07.P0707,P07.p0723,O.codesetid,O.A0000 "
                +",O.codeitemdesc as a0101,'' as e01a1,'' as b0110,'' as e0122 "
                +" from p07,organization O "
                +" where p07.p0707 = O.codeitemid "
                +" and ( (P07.P0721=3 "
                +" or (P07.p0721=1 and p07.P0707 like '"+b0110+"%') "
                +" or (P07.p0721=2 and p07.P0707 ='"+e0122+"')"
                +")"
                +")";
            sql=sql+" and p07.p0723 =2 and "+getPlanPublicWhereByStatus();

            //我关注的个人
            String [] arrpre=workPlanUtil.getSelfUserDbs();
            for (int i=0;i<arrpre.length;i++){
                String pre = arrpre[i];
                String a01tab=pre+"a01";
                sql=sql+" union "
                    +"select P07.p0700,P07.nbase,P07.a0100,P07.P0707 ,P07.p0723,'' as cdoesetid ,A01.A0000"
                    +",A01.a0101,p07.e01a1,p07.b0110,p07.e0122"
                    +" from p07," +a01tab+" A01"
                    +" where p07.a0100 = A01.a0100 "
                    +" and  ((P07.P0721=3 "
                    +" or (P07.p0721=1 and  p07.b0110 like '"+b0110+"%') "
                    +" or (P07.p0721=2 and  p07.e0122 ='"+e0122+"')"
                    +")"
                    +")"
                    +" and p07.nbase='"+pre+"'";
                sql=sql+" and p07.p0723 =1 and "+getPlanPublicWhereByStatus();

            }
            sql=sql+"";

            sql="select * from p07 where p0700 in (select p.p0700 from  ("+sql+") P) and p0700="+p0700;
            RowSet rset =dao.search(sql);
            if(rset.next()){
                b=true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }


        return b;
    }

    /**
     * @Title: getPlanPublicWhere
     * @Description: 当前计划的条件 sql语句用
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    private String getPlanPublicWhere() {
        String sql=" p07.p0723="+this.P0723
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
     * @Title: getPlanPublicWhereByStatus
     * @Description: 不区分团队、个人sql
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    private String getPlanPublicWhereByStatus() {
        String sql=" p07.p0725 ="+this.periodType +" and p07.P0727="+this.periodYear;
        if (!WorkPlanConstant.Cycle.YEAR.equals(this.periodType)){
            sql= sql+" and p07.P0729="+this.periodMonth;
        }
        if (WorkPlanConstant.Cycle.WEEK.equals(this.periodType)){
            sql= sql+" and p07.P0731="+this.periodWeek;
        }
        sql= sql+" and p07.P0719 in (1,2)";
        return sql;

    }


    /**
     * @Title: getMeConcernedList
     * @Description:我关注的列表 全部数据
     * @param @return
     * @return ArrayList
     * @author:wangrd
     * @throws
    */
    public ArrayList getMeConcernedList() {
        ArrayList list= new ArrayList();
        initSuperiorFld();
        if (this.superiorFld==null || this.superiorFld.length()<1){
            return list;
        }
        /*
        strsql ="select P07.p0700,P07.nbase,P07.a0100,P07.P0707,p07.p0723,O.codesetid,O.a0000 from p07,p09,organization O "
                +" where p07.p0700 = P09.P0903"
                +" and p07.P0707 = O.codeitemid"
                +" and  P09.P0901=1 and P09.P0905=3 "
                +" and P09.Nbase='"+this.userView.getDbname()+"'"
                +" and P09.a0100='"+this.userView.getA0100()+"'"
                +" p07.p0723=2"
                +" and "+ getPlanPublicWhere1()
                +" order by codesetid desc ,a0000";*/
        try{
            String strsql=getMeConcernedScopeSql();
            RowSet rset=dao.search(strsql);
            ArrayList dataList = dao.getDynaBeanList(rset);
            for (int i=0;i<dataList.size();i++){
                LazyDynaBean bean =(LazyDynaBean)dataList.get(i);
                String _nbase=(String)bean.get("nbase");
                String _a0100=(String)bean.get("a0100");
                if (_nbase!=null&&_nbase.length()>0){
                    if (_nbase.equals(this.userView.getDbname())
                            && _a0100.equals(this.userView.getA0100())){
                        continue;
                    }
                }
                String _p0707=(String)bean.get("p0707");
                String _p0723=(String)bean.get("p0723");

                //排除我的团队成员及下属部门
                if ("1".equals(_p0723)){
                    if(workPlanUtil.isMyTeamPeople(_nbase, _a0100)){
                        continue;
                    }
                }
                else {
                    if(workPlanUtil.isMyTeamDept(_p0707)){
                        continue;
                    }
                }
                //加上关注人能够查看被关注人的下级(OKR关注人穿透查看下属)
                /**
                 * 该计划在p09中有记录,且是个人计划.则能够穿透进行查看下属
                 */
                String _p0700 = (String) bean.get("p0700");
                //
                list.add(bean);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }


    /**
     * @Title: isMeConcernedPlan
     * @Description:是否是我关注的计划
     * @param @param p0700
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean isMeConcernedPlan(String p0700) {
        boolean b=false;
        if (p0700==null || p0700.length()<1){
            return b;
        }
        try{
            String sql=" select p0903 from p09 where "
            +" P0901=1 and P0905=3 "
            +" and Nbase='"+this.userView.getDbname()+"'"
            +" and a0100='"+this.userView.getA0100()+"'"
            +" and p0903="+p0700;
            RowSet rset= dao.search(sql,new ArrayList());
            if (rset.next()){
                b=true;
            }
        }
        catch(Exception e){

        }
        return b;
    }


    public String getMeConcernedAndCteamJson(int cur_page) {
        String info="";
        int subcount=0;
        String info1 = "";
        try{
            ArrayList peopleList = getMeConcernedList();

            int pagecount = 0;
            pagecount = peopleList.size();
            if (cur_page>1){
                while ((cur_page-1)*WorkPlanConstant.PAGESIZE>=pagecount){
                    if (cur_page>1) {
                        cur_page=cur_page-1;
                    }
                }
            }

            int begin_index = (cur_page - 1) * WorkPlanConstant.PAGESIZE;
            int end_index = cur_page * WorkPlanConstant.PAGESIZE;
            if (end_index > pagecount) {
                end_index = pagecount;
            }
            for (int i = begin_index; i < end_index; i++) {
                LazyDynaBean bean = (LazyDynaBean) (peopleList.get(i));
                String _p0700 = (String) bean.get("p0700");
                String _nbase = (String) bean.get("nbase");
                String _a0100 = (String) bean.get("a0100");
                String _p0707 = (String) bean.get("p0707");
                String _p0723 = (String) bean.get("p0723");
                String _a0101 = (String) bean.get("a0101");
                String _e0122 = (String) bean.get("e0122");
                String _e01a1 = (String) bean.get("e01a1");
                String _codesetid = (String) bean.get("codesetid");

                String canDelete="false";
                if (isMeConcernedPlan(_p0700)){
                    if (!isMyScopePerson(_p0700)){
                        canDelete="true";
                    }
                }
                //只穿透主动@的
                String stritem1 = "{";
                if("true".equals(canDelete) && "1".equals(_p0723)){

					 LazyDynaBean e01a1bean = new LazyDynaBean();
                    if (_e0122==null){
                   	 _e0122="";
                    }
                    String imagepath =getPhotoPath(_nbase, _a0100);
                    String subsql=getTeamPeopleSql(this.superiorFld,_nbase,_a0100,true);
                    StringBuffer sql = new StringBuffer();
                    sql.append("select count(*) cnt from "+"("+subsql+") T");
                    try{
                        RowSet rset1=dao.search(sql.toString());
                        if (rset1.next()){
                            subcount=subcount+rset1.getInt(1);
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
	                 String subvalue="";
	                 if (subcount>0){
	                     subvalue= "下属"+subcount+"人";
	                     subcount=0;
	                 }
	                 //数据拼装
	                 stritem1 += quotedDoubleValue("clientName")+":"+quotedDoubleValue("HJSJ")
	                 	+","
	                 	+quotedDoubleValue("flag")+":"+quotedDoubleValue("true")
	                 	+","
	                     +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(_nbase+_a0100))
	                     +","
	                     +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
	                     +","
	                     +quotedDoubleValue("name")+":"+quotedDoubleValue(_a0101)
	                     +","
	                     +quotedDoubleValue("hintinfo")+":"+quotedDoubleValue(_a0101
	                             +"<br /> "+AdminCode.getCodeName("UM",_e0122 ))
	                     +","
	                     +quotedDoubleValue("subpeople")+":"+quotedDoubleValue(subvalue)
	                     +","
//	                     +quotedDoubleValue("totalPageNum")+":"+quotedDoubleValue(totalPageNum + "")
//	                     +","
	                     +quotedDoubleValue("p0723")+":"+quotedDoubleValue(WorkPlanUtil.encryption("1"))
	                     + "," + quotedDoubleValue("canDelete") + ":" + quotedDoubleValue(canDelete)
	                     ;
                }else{
                	//原逻辑
	                String objectid=_nbase+_a0100;
	                String plan_type_desc="";
	                String namedesc=_a0101;
	                String hint=_a0101+"<br />"+AdminCode.getCodeName("UM", _e0122);
	                String leader="";
	                if ("2".equals(_p0723)) {
	                     leader =workPlanUtil.getFirstDeptLeaders(_p0707);
	                    if (leader.length() > 0) {
	                        _nbase =leader.substring(0, 3);
	                        _a0100 =leader.substring(3, leader.length());
	                        //负责人姓名
	                        String leader_name="";
	                        leader_name=workPlanUtil.getUsrA0101(_nbase, _a0100);
	                        namedesc=_a0101;
	                        hint=leader_name+"<br /> "+_a0101;
	                    }
	                    objectid=_p0707;
	                    if ("UN".equals(_codesetid)){
	                    //    plan_type_desc="单位工作计划";
	                    }
	                }
	                else {

	                }
	                String imagepath = getPhotoPath(_nbase, _a0100);
	                stritem1 +=
	                    quotedDoubleValue("objectid") + ":"+ quotedDoubleValue(WorkPlanUtil.encryption(objectid))
	                    +"," + quotedDoubleValue("dept_leader") + ":"+ quotedDoubleValue(WorkPlanUtil.encryption(leader))
	                    + "," + quotedDoubleValue("imagepath") + ":" + quotedDoubleValue(imagepath)
	                    + "," + quotedDoubleValue("name") + ":" + quotedDoubleValue(namedesc)
	                    + "," + quotedDoubleValue("hintinfo") + ":" + quotedDoubleValue(hint)
	                    + "," + quotedDoubleValue("plan_type_desc") + ":" + quotedDoubleValue(plan_type_desc)
	                    + "," + quotedDoubleValue("p0723") + ":" + quotedDoubleValue(WorkPlanUtil.encryption(_p0723))
	                    + "," + quotedDoubleValue("canDelete") + ":" + quotedDoubleValue(canDelete)
	                    ;

                }
                stritem1 += "}";
                if ("".equals(info)) {
                    info = stritem1;
                } else {
                    info = info + "," + stritem1;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return info;
    }

    //我关注的
    /**
     * @Title: getMeConcernedJson
     * @Description:  我关注的json列表
     * @param @param cur_page
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getMeConcernedJson(int cur_page) {
        String info="";
        try{
            ArrayList peopleList = getMeConcernedList();

            int pagecount = 0;
            pagecount = peopleList.size();
            if (cur_page>1){
                while ((cur_page-1)*WorkPlanConstant.PAGESIZE>=pagecount){
                    if (cur_page>1) {
                        cur_page=cur_page-1;
                    }
                }
            }

            int begin_index = (cur_page - 1) * WorkPlanConstant.PAGESIZE;
            int end_index = cur_page * WorkPlanConstant.PAGESIZE;
            if (end_index > pagecount) {
                end_index = pagecount;
            }
            for (int i = begin_index; i < end_index; i++) {
                LazyDynaBean bean = (LazyDynaBean) (peopleList.get(i));
                String _p0700 = (String) bean.get("p0700");
                String _nbase = (String) bean.get("nbase");
                String _a0100 = (String) bean.get("a0100");
                String _p0707 = (String) bean.get("p0707");
                String _p0723 = (String) bean.get("p0723");
                String _a0101 = (String) bean.get("a0101");
                String _e0122 = (String) bean.get("e0122");
                String _e01a1 = (String) bean.get("e01a1");
                String _codesetid = (String) bean.get("codesetid");
                String canDelete="false";
                if (isMeConcernedPlan(_p0700)){
                    if (!isMyScopePerson(_p0700)){
                        canDelete="true";
                    }
                }

                String objectid=_nbase+_a0100;
                //String plan_type_desc="个人工作计划";
                String plan_type_desc="";
                String namedesc=_a0101;
                String hint=_a0101+"<br />"+AdminCode.getCodeName("UM", _e0122);
                String leader="";
                if ("2".equals(_p0723)) {
                     leader =workPlanUtil.getFirstDeptLeaders(_p0707);
                    if (leader.length() > 0) {
                        _nbase =leader.substring(0, 3);
                        _a0100 =leader.substring(3, leader.length());
                        //负责人姓名
                        String leader_name="";
                        leader_name=workPlanUtil.getUsrA0101(_nbase, _a0100);
                        namedesc=_a0101;
                        hint=leader_name+"<br /> "+_a0101;
                    }
                    objectid=_p0707;
                    //plan_type_desc="部门工作计划";
                    if ("UN".equals(_codesetid)){
                    //    plan_type_desc="单位工作计划";
                    }
                }
                else {

                }

                String imagepath = getPhotoPath(_nbase, _a0100);
                String stritem =
                    "{" + quotedDoubleValue("objectid") + ":"+ quotedDoubleValue(WorkPlanUtil.encryption(objectid))
                    +"," + quotedDoubleValue("dept_leader") + ":"+ quotedDoubleValue(WorkPlanUtil.encryption(leader))
                    + "," + quotedDoubleValue("imagepath") + ":" + quotedDoubleValue(imagepath)
                    + "," + quotedDoubleValue("name") + ":" + quotedDoubleValue(namedesc)
                    + "," + quotedDoubleValue("hintinfo") + ":" + quotedDoubleValue(hint)
                    + "," + quotedDoubleValue("plan_type_desc") + ":" + quotedDoubleValue(plan_type_desc)
                    + "," + quotedDoubleValue("p0723") + ":" + quotedDoubleValue(WorkPlanUtil.encryption(_p0723))
                    + "," + quotedDoubleValue("canDelete") + ":" + quotedDoubleValue(canDelete)
                    + "}";
                if ("".equals(info)) {
                    info = stritem;
                } else {
                    info = info + "," + stritem;

                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return info;

    }


    /**
     * @Title: getTeamPeopleSql
     * @Description: 获取团队成员sql
     * @param
     * @param ps_superior 上级岗位字段
     * @param
     * @param nbase
     * @param
     * @param a0100
     * @param
     * @param includeTransferE01a1
     * @param
     * @return
     * @return String
     * @author:wangrd
     * @throws
     */
    private String getTeamPeopleSql(String ps_superior,String e01a1, boolean includeTransferE01a1) {
    	boolean bFindHistory=true;
    	/* //本人
        if (workPlanUtil.getMyE01a1s(this.userView.getDbname(),this.userView.getA0100()).equals(e01a1)){
            bFindHistory=false;
        }
        //我的下级
        if (workPlanUtil.isMyTeamPeople(e01a1)){
            bFindHistory=false;
        }*/


        String [] arrpre=workPlanUtil.getSelfUserDbs();
        if( arrpre.length<1 ) {
            return "";
        }
        String strSql="";
        try{
        	for (int i=0;i<arrpre.length;i++){
                String pre = arrpre[i];
                String a01tab=pre+"A01";
                if (bFindHistory){//查找目前在岗的下级成员
                	String sql="select a0100 from "+a01tab+" where e01a1='"+e01a1+"'";
                    //排除 在当前期间制定计划时岗位不属于我的团队 由其他部门调来的
                	if (includeTransferE01a1){
                        sql=sql+" and a0100 not in  (";
                        sql = sql + " select a0100 from p07,K01 "
                        +" where p07.E01A1 = K01.E01A1"
                        +" and k01.e01a1='"+e01a1+"'";
                        sql=sql+" and p07.nbase ='"+pre+"'"+" and "+this.getPlanPublicWhere();
                        sql=sql+")";
                    }
                	//加上 在当前期间制定计划时属于我的团队，从我部门调走的
                    if (includeTransferE01a1){
	                        //按自己的部门
	                        sql = sql + " union select a0100 from p07,K01 "
	                            +" where p07.E01A1 = K01.E01A1"
	                            +" and k01.e01a1='"+e01a1+"'";
	                            sql=sql+" and p07.nbase ='"+pre+"'" +" and "+this.getPlanPublicWhere();

                            //按上级部门
	                        sql = sql + " union select a0100 from p07 "
	                            +" where p07.superE01a1 ='"+e01a1+"'";
                            sql=sql+" and p07.nbase ='"+pre+"'" +" and "+this.getPlanPublicWhere();

                    }

                    if (!"".equals(strSql)){
                        strSql=strSql+" union ";
                    }
                    strSql=strSql+ "select A0100,a0101,b0110,e0122,e01a1,"+"'"+pre+"' as nbase "+" from  "+a01tab
                                +" where a0100 in (select A.a0100 from ("+ sql+") A )";

                }

        	}
        }catch (Exception e) {
        	e.printStackTrace();
		}
		return strSql;
    }


    private String getTeamPeopleSql(String ps_superior,String nbase,String a0100, boolean includeTransferE01a1) {
      boolean bFindHistory=true;
      //本人
      if ((this.userView.getDbname().equals(nbase))&&(this.userView.getA0100().equals(a0100))){
          bFindHistory=false;
      }
      //我的下级
      if (workPlanUtil.isMyTeamPeople(nbase, a0100) || "HJSJ".equals(SystemConfig.getPropertyValue("clientName"))){
          bFindHistory=false;
      }
      String historyE01a1="";
      if (bFindHistory){
          String sql="select e01a1,supere01a1 from p07 where nbase='"+nbase+"' and a0100='"+a0100+"'"
          +" and "+this.getPlanPublicWhere();

          try{
              RowSet rset=dao.search(sql);
              if (rset.next()){
                  historyE01a1 =rset.getString("e01a1");

                  /*
                  String supere01a1 =rset.getString("supere01a1");
                  if (supere01a1!=null && supere01a1.length()>0){//按上级岗位查
                      String myE01a1s=workPlanUtil.getMyE01a1s(this.userView.getDbname(), this.userView.getA0100());
                      if (workPlanUtil.isMySubE01a1(myE01a1s, supere01a1)){//是我的下级
                          bFindHistory=false;
                      }

                      if ((","+myE01a1s+",").indexOf(supere01a1)>-1){
                          bFindHistory=false;
                      }
                  }
                  */
              }
          }
          catch(Exception e){
              e.printStackTrace();
          }
      }

        //兼职
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");

        String [] arrpre=workPlanUtil.getSelfUserDbs();
        if( arrpre.length<1 ) {
            return "";
        }
        String strSql="";
        try{
            for (int i=0;i<arrpre.length;i++){
                String pre = arrpre[i];
                String cura01tab=nbase+"A01";
                String a01tab=pre+"A01";
                if (!bFindHistory){//查找目前在岗的下级成员
                        //主职的下级人员
                    String sql="select "+a01tab+".a0100 from "+a01tab+",K01"
                        +" where "+a01tab+".E01A1 = K01.E01A1"
                        +" and (exists ("+"select 1 from "+cura01tab+" where a0100='"+a0100
                            +"' and e01a1 =K01."+ps_superior+")"
                        +" ";
                        //兼职的下级人员
                    if("true".equals(flag)){
                        String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                        String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
                        String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                        String curPartTab=nbase+setid;
                        if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
                        {
                            sql=sql+" or exists (select 1 from "+curPartTab+" where a0100='"+a0100
                                +"' and "+e01a1_field+" =K01."+ps_superior
                                +" and "+appoint_field+"='0')";
                        }
                    }
                    sql=sql+")";
                    //排除 在当前期间制定计划时岗位不属于我的团队 由其他部门调来的
                    if (includeTransferE01a1){
                        sql=sql+" and a0100 not in  (";

                        sql = sql + " select a0100 from p07,K01 "
                        +" where p07.E01A1 = K01.E01A1"
                        +" and ( not exists ("+"select 1 from "+cura01tab+" where a0100='"+a0100
                        +"' and e01a1 =K01."+ps_superior+")"
                        +" ";
                        //兼职
                        if("true".equals(flag)){
                            String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                            String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
                            String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                            String curPartTab=nbase+setid;
                            if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
                            {
                                sql=sql+" and not exists (select 1 from "+curPartTab+" where a0100='"+a0100
                                    +"' and "+e01a1_field+" =K01."+ps_superior
                                    +" and "+appoint_field+"='0')";
                            }
                        }
                        sql=sql+")";
                        sql=sql+" and p07.nbase ='"+pre+"'"+" and "+this.getPlanPublicWhere();

                        sql=sql+")";
                    }

                    //加上 在当前期间制定计划时属于我的团队，从我部门调走的
                    if (includeTransferE01a1){
                        //按自己的部门
                        sql = sql + " union select a0100 from p07,K01 "
                            +" where p07.E01A1 = K01.E01A1"
                            +" and (exists ("+"select 1 from "+cura01tab+" where a0100='"+a0100
                            +"' and e01a1 =K01."+ps_superior+")"
                            +" ";
                                //兼职
                            if("true".equals(flag)){
                                String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                                String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
                                String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                                String curPartTab=nbase+setid;
                                if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
                                {
                                    sql=sql+" or exists (select 1 from "+curPartTab+" where a0100='"+a0100
                                        +"' and "+e01a1_field+" =K01."+ps_superior
                                        +" and "+appoint_field+"='0')";
                                }
                            }
                            sql=sql+")";
                            sql=sql+" and p07.nbase ='"+pre+"'" +" and "+this.getPlanPublicWhere();

                            //按上级部门
                            sql = sql + " union select a0100 from p07 "
                            +" where (p07.superE01a1 in "
                            +" (select e01a1 from "+cura01tab+" where a0100='"+a0100
                            +"')"
                            +" ";
                                //兼职
                            if("true".equals(flag)){
                                String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                                String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
                                String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                                String curPartTab=nbase+setid;
                                if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
                                {
                                    sql=sql+" or  p07.superE01a1 in  (select "+e01a1_field+" from "
                                    +curPartTab+" where a0100='"+a0100+"'"
                                        +" and "+appoint_field+"='0')";
                                }
                            }
                            sql=sql+") and p07.nbase ='"+pre+"'" +" and "+this.getPlanPublicWhere();

                    }

                    if (!"".equals(strSql)){
                        strSql=strSql+" union ";
                    }
                    strSql=strSql+ "select A0100,a0101,b0110,e0122,e01a1,"+"'"+pre+"' as nbase "+" from  "+a01tab
                                +" where a0100 in (select A.a0100 from ("+ sql+") A )";
                }
                else {//查找历史中的下级成员
                    //主岗位的下级成员
                    String sql= "  select a0100 from p07,K01 "
                        +" where p07.E01A1 = K01.E01A1"
                        +" and K01."+ps_superior+ "='"+historyE01a1+"'"
                        +" and p07.nbase ='"+pre+"'" +" and "+this.getPlanPublicWhere();

                    if (!"".equals(strSql)){
                        strSql=strSql+" union ";
                    }
                    strSql=strSql+ "select A0100,a0101,b0110,e0122,e01a1,"+"'"+pre+"' as nbase "+" from  "+a01tab
                                +" where a0100 in (select A.a0100 from ("+ sql+") A )";

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return strSql;
    }


    /**
     * @Title: getMyTeamPeopleList
     * @Description:  获取我团队的json列表
     * @param @param nbase
     * @param @param a0100
     * @param @param cur_page
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
	public String getMyTeamPeopleList(String nbase,String a0100, int cur_page) {
    	 String info="";
    	 String info1="";
    	 String info2="";
    	 String sql="";
    	 RowSet rset;
    	 initSuperiorFld();
    	 HashMap existsMap= new HashMap();
    	 ArrayList peopleList=new ArrayList();
    	 int subcount=0;
          	try {
          		    String tablename=getTeamPeopleSql(this.superiorFld,nbase,a0100,true);
          		    // 人力地图人员顺序要根据a0000排列 chent 20171204 modify
          		    sql="select T.*,A01.a0000 from "+"("+tablename+") T left join "+nbase+"A01 A01 on T.a0100=A01.a0100 where T.A0100 not in ('"+a0100+"') order by a0000";//工作计划人力地图排除本人 chent
          		    rset=dao.search(sql);
    				while(rset.next()){
    					 LazyDynaBean e01a1bean = new LazyDynaBean();
        				 String _nbase= rset.getString("nbase");
                         String _a0100= rset.getString("a0100");
                         String _a0101= rset.getString("a0101");
                         String _e0122= rset.getString("e0122");
                         if (_e0122==null){
                        	 _e0122="";
                         }
                         existsMap.put(rset.getString("e01a1"), "1");
                         String imagepath =getPhotoPath(_nbase, _a0100);
                         String subsql=getTeamPeopleSql(this.superiorFld,_nbase,_a0100,true);
                         sql="select count(*) cnt from "+"("+subsql+") T";
                         try{
                             RowSet rset1=dao.search(sql);
                             if (rset1.next()){
                                 subcount=subcount+rset1.getInt(1);
                             }
                         }
                         catch(Exception e){
                             e.printStackTrace();
                         }
		                 String subvalue="";
		                 if (subcount>0){
		                     subvalue= "下属"+subcount+"人";
		                     subcount=0;
		                 }
		                 e01a1bean.set("flag", "true");
		                 e01a1bean.set("nbase", _nbase);
		                 e01a1bean.set("a0100", _a0100);
		                 e01a1bean.set("a0101", (_a0101==null)?"":_a0101);
		                 e01a1bean.set("e0122", _e0122);
		                 e01a1bean.set("imagepath", imagepath);
		                 e01a1bean.set("subvalue", subvalue);
		                 peopleList.add(e01a1bean);
                 }
          	}catch (Exception e) {
          		e.printStackTrace();
			}
           	try {
           		       //获得当前登录人的所有下级岗位
           	           ArrayList e01a1list=getAllSubE01a1List(nbase, a0100);
           	           int pagesize=e01a1list.size();
           	           String datevalue = DateUtils.format(new Date(), "yyyy.MM.dd");
           	           //增加岗位有效截止时间条件 O.end_date
	          		   StringBuffer selectsql = new StringBuffer();
	      	    	   selectsql.append(" select K.e01a1,K.e0122,O.codeitemdesc ");
	      	    	   selectsql.append(" from k01 K,organization O ");
	      	    	   selectsql.append(" where K.e01a1=O.codeitemid ");
	      	    	   selectsql.append(" and O.end_date>=").append(Sql_switcher.dateValue(datevalue));
	          		   selectsql.append(" and "+this.superiorFld+"=?");
           	           for(int i=0;i<pagesize;i++){
           	        	   LazyDynaBean bean = (LazyDynaBean) (e01a1list.get(i));
           	        	   String e01a1= (String) bean.get("e01a1");
           	        	   if (existsMap.containsKey((e01a1))) {
                               continue;//已有此岗位的人
                           }
            		       sql = "select * from "+this.userView.getDbname()+"A01 where E01A1='" + bean.get("e01a1") + "'";//查询当前岗位人
  		          	       rset=dao.search(sql);
  		          	       subcount=0;
	 		          	   if(!rset.next()){//当前岗位没有人，则继续查下级岗位
	 		          		     LazyDynaBean e01a1bean = new LazyDynaBean();
  		          			     String codeitemdesc= (String) bean.get("codeitemdesc");
  		          			     //先查出当前岗位的所有下属岗位
//  		                         sql="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+this.superiorFld+"='"+e01a1+"'";
  		          			     ArrayList list = new ArrayList();
  		          			     list.add(e01a1);
  		          			     RowSet rset1=dao.search(selectsql.toString(), list);
	  		                     while(rset1.next()){
		  		                   	 String sube01a1= (String) rset1.getString("e01a1");
		  		                     String tablename=getTeamPeopleSql(this.superiorFld,sube01a1,true);
		  		                     String sql2="select count (*) from "+"("+tablename+") T";
		  		                   	 RowSet rset2=dao.search(sql2);
		  		                   	 if (rset2.next()){
		  		                            subcount = subcount+rset2.getInt(1);
		  		                        }

	  		                    }

	 		                    String subvalue="";
	 		                    if (subcount>0){
	 		                        subvalue= "下属"+subcount+"人";
	 		                        e01a1bean.set("flag", "false");
	 			                    e01a1bean.set("e01a1", e01a1);
	 			                    e01a1bean.set("codeitemdesc", codeitemdesc);
	 			                    e01a1bean.set("imagepath", "/images/photo.jpg");
	 			                    e01a1bean.set("subvalue", subvalue);
	 			                    peopleList.add(e01a1bean);
	 			                    subcount=0;
	 		          		    }else if(isHasE01A1People(e01a1)){
	 		          		    	subvalue= "下属"+subcount+"人";
	 		                        e01a1bean.set("flag", "false");
	 			                    e01a1bean.set("e01a1", e01a1);
	 			                    e01a1bean.set("codeitemdesc", codeitemdesc);
	 			                    e01a1bean.set("imagepath", "/images/photo.jpg");
	 			                    e01a1bean.set("subvalue", subvalue);
	 			                    peopleList.add(e01a1bean);
	 			                    subcount=0;
	 		          		    }
	 		          	   }
           	           }
           		}catch (Exception e) {
           			e.printStackTrace();
           		}
         //构造传递到前台的json数据
         int pagecount=peopleList.size();
         int totalPageNum = 0;
         if(pagecount%WorkPlanConstant.PAGESIZE > 0){
        	 totalPageNum = pagecount/WorkPlanConstant.PAGESIZE + 1;
         }else{
        	 totalPageNum = pagecount/WorkPlanConstant.PAGESIZE;
         }
         int begin_index=(cur_page-1)*WorkPlanConstant.PAGESIZE;
         //从第二页开始,如果起始索引大于总人数,说明第一页已经显示全部人员,则不必翻页
         if(begin_index > pagecount){
        	 return "";
         }
         int end_index=cur_page*WorkPlanConstant.PAGESIZE;
         if (end_index>pagecount) {
             end_index=pagecount;
         }

         boolean cTeam = false;
         if(!(userView.getDbname()+userView.getA0100()).equals(nbase+a0100) && !workPlanUtil.isMyTeamPeople(nbase, a0100) && "HJSJ".equals(SystemConfig.getPropertyValue("clientName"))){
        	 cTeam = true;
         }

         for (int i=begin_index;i<end_index;i++){
        	 LazyDynaBean personbean = (LazyDynaBean)(peopleList.get(i));
        	 String flag  = (String)personbean.get("flag");
        	 if("true".equals(flag)){
        		 String _nbase= (String)personbean.get("nbase");
                 String _a0100= (String)personbean.get("a0100");
                 String _a0101= (String)personbean.get("a0101");
                 String _e0122="";
                 if (personbean.get("e0122")!=null) {
                     _e0122= (String)personbean.get("e0122");
                 }
                 String imagepath= (String)personbean.get("imagepath");
                 String subvalue= (String)personbean.get("subvalue");

                 String stritem1="{";
                 if(cTeam){
                	 stritem1 += quotedDoubleValue("clientName")+":"+quotedDoubleValue("HJSJ")+",";
                 }
                	 stritem1 += quotedDoubleValue("cTeam")+":"+quotedDoubleValue(cTeam+"")
		                 +","
		                 +quotedDoubleValue("flag")+":"+quotedDoubleValue("true")
		             	 +","
		                 +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(_nbase+_a0100))
		                 +","
		                 +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
		                 +","
		                 +quotedDoubleValue("name")+":"+quotedDoubleValue(_a0101)
		                 +","
		                 +quotedDoubleValue("hintinfo")+":"+quotedDoubleValue(_a0101
		                         +"<br /> "+AdminCode.getCodeName("UM",_e0122 ))
		                 +","
		                 +quotedDoubleValue("subpeople")+":"+quotedDoubleValue(subvalue)
		                 +","
		                 +quotedDoubleValue("totalPageNum")+":"+quotedDoubleValue(totalPageNum + "")
		                 +","
		                 +quotedDoubleValue("p0723")+":"+quotedDoubleValue(WorkPlanUtil.encryption("1"))
		                +"}"
		                 ;
                 if ("".equals(info1)){
                     info1 = stritem1;
                 }
                 else {
                     info1 =info1+","+ stritem1;

                 }
        	 }else{
        		 String e01a1= (String)personbean .get("e01a1");
        		 String codeitemdesc= (String)personbean .get("codeitemdesc");
                 String imagepath= (String)personbean .get("imagepath");
                 String subvalue= (String)personbean .get("subvalue");

                 String stritem2="{"
                	 +quotedDoubleValue("flag")+":"+quotedDoubleValue("false")
                	 +","
                	 +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(e01a1))
                     +","
                	 +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
                     +","
                     +quotedDoubleValue("hintinfo")+":"+quotedDoubleValue(codeitemdesc)
                     +","
                     +quotedDoubleValue("subpeople")+":"+quotedDoubleValue(subvalue)
                     +","
                     +quotedDoubleValue("name")+":"+quotedDoubleValue(codeitemdesc)
                    +"}"
                     ;
                 if ("".equals(info2)){
                     info2 = stritem2;
                 }
                 else {
                     info2 =info2+","+ stritem2;

                 }
        	 }

         }
         if("".equals(info2)){
        	 info=info1;
         }
         else if("".equals(info1)){
        	 info=info2;
         }else{
        	 info=info1+","+info2;
         }

     return info;

  }

  /**
   * @author lis
   * @Description: 当前人员是否所有下级岗位中是否有人
   * @date Jul 4, 2016
   * @param nbase
   * @param a0100
   * @return
   */
  public boolean isHasPeopleInNextE01A1(String nbase, String a0100) {
	  boolean flag = false;
		try {
			int subcount = 0;
			// 获得当前登录人的所有下级岗位
			ArrayList e01a1list = getAllSubE01a1List(nbase, a0100);
			for (int i = 0; i < e01a1list.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) (e01a1list.get(i));
				String e01a1 = (String) bean.get("e01a1");
				flag = isHasE01A1People(e01a1);
				if(flag){
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

  /**
	 * @author lis
	 * @Description: 递归查找，直到获得岗位下人数大于0
	 * @date Jul 4, 2016
	 * @param e01a1 当前岗位
	 * @return
	 */
  public boolean isHasE01A1People(String e01a1) {
		int subCount = 0;
		boolean flag = false;
		try {
			// 先查出当前岗位的所有下属岗位
			String sql = "select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "
					+ this.superiorFld + "='" + e01a1 + "'";
			RowSet rset1 = dao.search(sql);
			while (rset1.next()) {

				String subE01A1 = (String) rset1.getString("e01a1");
				String tablename = getTeamPeopleSql(this.superiorFld, subE01A1,true);
				String sql2 = "select count (*) from " + "(" + tablename + ") T";
				RowSet rset2 = dao.search(sql2);
				if (rset2.next()) {
					subCount = rset2.getInt(1);
				}
				if (subCount > 0){
					flag = true;
					break;
				}
				else {
                    flag = this.isHasE01A1People(subE01A1);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
    /**
	 * 通过岗位获取我团队的json列表
	 *
	 * @param e01a1List
	 *            当前岗位
	 * @param cur_page
	 * @return
	 */
    public String getMyTeamPeopleList(ArrayList e01a1list, int cur_page) {
        	 String info="";
        	 String info1="";
        	 String info2="";
        	 String sql="";
        	 RowSet rset;
        	 initSuperiorFld();
        	 ArrayList peopleList=new ArrayList();
        	 int pagesize=e01a1list.size();
        	 int subcount=0;
             for(int i=0;i<pagesize;i++){
              	try {
              		    LazyDynaBean bean = (LazyDynaBean) (e01a1list.get(i));
              		    String tablename=getTeamPeopleSql(this.superiorFld,(String) bean.get("e01a1"),true);
              		    sql="select * from "+"("+tablename+") T";
	        			rset=dao.search(sql);
        				while(rset.next()){
        					 LazyDynaBean e01a1bean = new LazyDynaBean();
	        				 String _nbase= rset.getString("nbase");
	                         String _a0100= rset.getString("a0100");
	                         String _a0101= rset.getString("a0101");
	                         String _e0122= rset.getString("e0122");
	                         //解决人员没有所属部门时，查看下属个人计划报错问题
                             _e0122 = _e0122 == null ? "": _e0122;
	                         String imagepath =getPhotoPath(_nbase, _a0100);
	                         String subsql=getTeamPeopleSql(this.superiorFld,_nbase,_a0100,true);
	                         sql="select count(*) cnt from "+"("+subsql+") T";
	                         try{
	                             RowSet rset1=dao.search(sql);
	                             if (rset1.next()){
	                                 subcount=subcount+rset1.getInt(1);
	                             }
	                         }
	                         catch(Exception e){
	                             e.printStackTrace();
	                         }
			                 String subvalue="";
			                 if (subcount>0){
			                     subvalue= "下属"+subcount+"人";
			                     subcount=0;
			                 }
			                 e01a1bean.set("flag", "true");
			                 e01a1bean.set("nbase", _nbase);
			                 e01a1bean.set("a0100", _a0100);
			                 e01a1bean.set("a0101", _a0101);
			                 e01a1bean.set("e0122", _e0122);
			                 e01a1bean.set("imagepath", imagepath);
			                 e01a1bean.set("subvalue", subvalue);
			                 peopleList.add(e01a1bean);
	                 }

              	}catch (Exception e) {
              		e.printStackTrace();
    			}
        	 }
             for(int j=0;j<pagesize;j++){
               	try {
    		           	   LazyDynaBean bean = (LazyDynaBean) (e01a1list.get(j));
    		           	   sql = "select * from "+this.userView.getDbname()+"A01 where E01A1='" + bean.get("e01a1") + "'";//01010501
  		          	       rset=dao.search(sql);
    		          	   if(!rset.next()){
    		          		     LazyDynaBean e01a1bean = new LazyDynaBean();
    		          		     String e01a1= (String) bean.get("e01a1");
  		          			     String codeitemdesc= (String) bean.get("codeitemdesc");
	  		          			 //先查出当前岗位的所有下属岗位
	  		                     sql="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+this.superiorFld+"='"+e01a1+"'";
	  		          			 RowSet rset1=dao.search(sql);
	  		                     while(rset1.next()){
		  		                   	 String sube01a1= (String) rset1.getString("e01a1");
		  		                   	 String sql2="select count(*) from "+this.userView.getDbname()+"A01 where E01A1='"+sube01a1+"'";
		  		                   	 RowSet rset2=dao.search(sql2);
		   		                   	 if (rset2.next()){
		   		                            subcount=subcount+rset2.getInt(1);
		   		                        }
		    		                    String subvalue="";
		    		                    if (subcount>0){
		    		                        subvalue= "下属"+subcount+"人";
		    		                        e01a1bean.set("flag", "false");
		    			                    e01a1bean.set("e01a1", e01a1);
		    			                    e01a1bean.set("codeitemdesc", codeitemdesc);
		    			                    e01a1bean.set("imagepath", "/images/photo.jpg");
		    			                    e01a1bean.set("subvalue", subvalue);
		    			                    peopleList.add(e01a1bean);
		    			                    subcount=0;
		    		          		    }else if(this.isHasE01A1People(e01a1)){
		    		          		    	subvalue= "下属"+subcount+"人";
		    		                        e01a1bean.set("flag", "false");
		    			                    e01a1bean.set("e01a1", e01a1);
		    			                    e01a1bean.set("codeitemdesc", codeitemdesc);
		    			                    e01a1bean.set("imagepath", "/images/photo.jpg");
		    			                    e01a1bean.set("subvalue", subvalue);
		    			                    peopleList.add(e01a1bean);
		    			                    subcount=0;
		    		          		    }
    		          	   }
	                     }
               		}catch (Exception e) {
               			e.printStackTrace();
               		}
    			}
             //构造传递到前台的json数据
             int pagecount=peopleList.size();
             int totalPageNum = 0;
             if(pagecount%WorkPlanConstant.PAGESIZE > 0){
            	 totalPageNum = pagecount/WorkPlanConstant.PAGESIZE + 1;
             }else{
            	 totalPageNum = pagecount/WorkPlanConstant.PAGESIZE;
             }
             int begin_index=(cur_page-1)*WorkPlanConstant.PAGESIZE;
             int end_index=cur_page*WorkPlanConstant.PAGESIZE;
             if (end_index>pagecount) {
                 end_index=pagecount;
             }
             for (int i=begin_index;i<end_index;i++){
            	 LazyDynaBean personbean = (LazyDynaBean)(peopleList.get(i));
            	 String flag  = (String)personbean.get("flag");
            	 if("true".equals(flag)){
            		 String _nbase= (String)personbean.get("nbase");
                     String _a0100= (String)personbean.get("a0100");
                     String _a0101= (String)personbean.get("a0101");
                     String _e0122= (String)personbean.get("e0122");
                     String imagepath= (String)personbean.get("imagepath");
                     String subvalue= (String)personbean.get("subvalue");

                     String stritem1="{"
                     	+quotedDoubleValue("flag")+":"+quotedDoubleValue("true")
                     	+","
                         +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(_nbase+_a0100))
                         +","
                         +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
                         +","
                         +quotedDoubleValue("name")+":"+quotedDoubleValue(_a0101)
                         +","
                         +quotedDoubleValue("hintinfo")+":"+quotedDoubleValue(_a0101
                                 +"<br /> "+AdminCode.getCodeName("UM",_e0122 ))
                         +","
                         +quotedDoubleValue("subpeople")+":"+quotedDoubleValue(subvalue)
                         +","
                         +quotedDoubleValue("totalPageNum")+":"+quotedDoubleValue(totalPageNum+"")
                         +","
                         +quotedDoubleValue("p0723")+":"+quotedDoubleValue(WorkPlanUtil.encryption("1"))
                        +"}"
                         ;
                     if ("".equals(info1)){
                         info1 = stritem1;
                     }
                     else {
                         info1 =info1+","+ stritem1;

                     }
            	 }else{
            		 String e01a1= (String)personbean .get("e01a1");
            		 String codeitemdesc= (String)personbean .get("codeitemdesc");
                     String imagepath= (String)personbean .get("imagepath");
                     String subvalue= (String)personbean .get("subvalue");

                     String stritem2="{"
                    	 +quotedDoubleValue("flag")+":"+quotedDoubleValue("false")
                    	 +","
                    	 +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(e01a1))
                         +","
                    	 +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
                         +","
                         +quotedDoubleValue("hintinfo")+":"+quotedDoubleValue(codeitemdesc)
                         +","
                         +quotedDoubleValue("subpeople")+":"+quotedDoubleValue(subvalue)
                         +","
                         +quotedDoubleValue("name")+":"+quotedDoubleValue(codeitemdesc)
                        +"}"
                         ;
                     if ("".equals(info2)){
                         info2 = stritem2;
                     }
                     else {
                         info2 =info2+","+ stritem2;

                     }
            	 }

             }
             if("".equals(info2)){
            	 info=info1;
             }
             else if("".equals(info1)){
            	 info=info2;
             }else{
            	 info=info1+","+info2;
             }
         return info;

      }

    /**
     * 级查看下级时，如果此时下级岗位缺编，但是此岗位下还有下属人员，则需要把此岗位显示出来
     * @Title:
     * @Description:
     * @param @param nbase
     * @param @param a0100
     * @param @return
     * @return String
     * @author:
     * @throws
    */
	public ArrayList getAllSubE01a1List(String nbase,String a0100){
		initSuperiorFld();
    	ArrayList e01a1List =new ArrayList();
    	try {
    		//获取当前登录人的负责所有部门
    		ArrayList myE01a1List = workPlanUtil.getMyE01a1List(nbase, a0100);
    		//得到所有岗位的下级岗位
    		RowSet rset;
    		int pagecount = myE01a1List.size();
    		String datevalue = DateUtils.format(new Date(), "yyyy.MM.dd");
    		//增加岗位有效截止时间条件 O.end_date
			StringBuffer selectsql = new StringBuffer();
	   		selectsql.append(" select K.e01a1,K.e0122,O.codeitemdesc ");
	   		selectsql.append(" from k01 K,organization O ");
	   		selectsql.append(" where K.e01a1=O.codeitemid ");
	   		selectsql.append(" and O.end_date>=").append(Sql_switcher.dateValue(datevalue));
			selectsql.append(" and "+this.superiorFld+"=?");
    		for(int i=0;i<pagecount;i++){
    			 LazyDynaBean bean = (LazyDynaBean) (myE01a1List.get(i));
    			 String e01a1= (String) bean.get("e01a1");
    			 ArrayList list = new ArrayList();
    			 list.add(e01a1);
//    			 sql="select K.e01a1,K.e0122,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+this.superiorFld+"='"+e01a1+"' and O.end_date>="+Sql_switcher.dateValue(datevalue);
    			 rset=dao.search(selectsql.toString(), list);
    			 while(rset.next()){
    				 LazyDynaBean e01a1bean = new LazyDynaBean();
    				 e01a1bean.set("e01a1", rset.getString("e01a1"));
    				 e01a1bean.set("e0122", rset.getString("e0122"));
    				 e01a1bean.set("codeitemdesc", rset.getString("codeitemdesc"));
    				 e01a1List.add(e01a1bean);
    			 }
    		}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return e01a1List;
    }

    /**
     * 如果当前的岗位下没有在职人员而下级岗位有，则显示岗位信息
     * @param e01a1list
     * @return
     */
    public String getE01a1Info(ArrayList e01a1list){
    	String info="";
    	String imagepath = "/images/photo.jpg";
    	try {
    		RowSet rset;
    		String sql="";
    		int pagecount = e01a1list.size();
    		for(int i=0;i<pagecount;i++){
    			 LazyDynaBean bean = (LazyDynaBean) (e01a1list.get(i));
    			 String e01a1= (String) bean.get("e01a1");
    			 String codeitemdesc= (String) bean.get("codeitemdesc");
				 sql="select * from UsrA01 where E01A1='"+e01a1+"'";
    			 rset=dao.search(sql);
    			 if(!rset.next()){//如果当前的岗位下没有在职人员:查看他的下级岗位，就显示岗位信息（（XX岗位） 下属几人）
                     int subcount=0;
                     //先查出当前岗位的所有下属岗位
                     String sql1="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+this.superiorFld+"='"+e01a1+"'";
        			 RowSet rset1=dao.search(sql1);
                     while(rset1.next()){
                    	 String sube01a1= (String) rset1.getString("e01a1");
                    	 String sql2="select count(*) from UsrA01 where E01A1='"+sube01a1+"'";
                    	 RowSet rset2=dao.search(sql2);
                    	 if (rset2.next()){
                             subcount=subcount+rset2.getInt(1);
                         }

                     }
                     String subvalue="";
                     if (subcount>0){
                         subvalue= "下属"+subcount+"人";
                         String stritem="{"
                        	 +quotedDoubleValue("flag")+":"+quotedDoubleValue("false")
                        	 +","
                        	 +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(e01a1))
                             +","
                        	 +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
                             +","
                             +quotedDoubleValue("hintinfo")+":"+quotedDoubleValue(codeitemdesc)
                             +","
                             +quotedDoubleValue("subpeople")+":"+quotedDoubleValue(subvalue)
                             +","
                             +quotedDoubleValue("name")+":"+quotedDoubleValue(codeitemdesc)
                            +"}"
                             ;
                         if ("".equals(info)){
                             info = stritem;
                         }
                         else {
                             info =info+","+ stritem;

                         }
                     }
    			 }

    		}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return info;
    }

    /**
     * @Title: getDeptList_Json
     * @Description:
     * @param @param nbase
     * @param @param a0100
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getMyDeptJsonList(String nbase,String a0100,int cur_page) {
        String menuList="";
        try {
            int pagecount = 0;
            ArrayList deptlist = workPlanUtil.getDeptList(nbase, a0100);
            pagecount = deptlist.size();
            if (cur_page>1){
                while ((cur_page-1)*WorkPlanConstant.PAGESIZE>=pagecount){
                    if (cur_page>1) {
                        cur_page=cur_page-1;
                    }
                }
            }

            int begin_index = (cur_page - 1) * WorkPlanConstant.PAGESIZE;
            int end_index = cur_page * WorkPlanConstant.PAGESIZE;
            if (end_index > pagecount) {
                end_index = pagecount;
            }
            String _nbase = this.userView.getDbname();
            String _a0100 = this.userView.getA0100();
            String _p0723 = "2";
            for (int i = begin_index; i < end_index; i++) {
                LazyDynaBean bean = (LazyDynaBean) (deptlist.get(i));
                String deptdesc = (String) bean.get("deptdesc");
                String b0110 = (String) bean.get("b0110");

                String imagepath = getPhotoPath(_nbase, _a0100);
                String stritem =
                    "{" + quotedDoubleValue("objectid") + ":"+ quotedDoubleValue(WorkPlanUtil.encryption(b0110))
                    +"," + quotedDoubleValue("dept_leader") + ":"+ quotedDoubleValue(WorkPlanUtil.encryption(_nbase+_a0100))
                    + "," + quotedDoubleValue("imagepath") + ":" + quotedDoubleValue(imagepath)
                    + "," + quotedDoubleValue("name") + ":" + quotedDoubleValue(deptdesc)
                    + "," + quotedDoubleValue("p0723") + ":" + quotedDoubleValue(WorkPlanUtil.encryption(_p0723))
                    + "}";
                if ("".equals(menuList)) {
                    menuList = stritem;
                } else {
                    menuList = menuList + "," + stritem;
                }
            }

          } catch (Exception e) {
        }
        return menuList;

    }



     public void initSuperiorFld() {
        if (superiorFld==null || "".equals(superiorFld)){
            RecordVo ps_superior_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",this.conn);
            if(ps_superior_vo!=null)
            {
                superiorFld=ps_superior_vo.getString("str_value");
            }
        }
    }


     /**
      * @Title: getMySubDeptList
      * @Description: 全部下属负责部门
      * @param @param e01a1list 根据下级岗位查询负责的部门
      * @param @return
      * @return String
      * @author:wangrd
      * @throws
     */
     public ArrayList getMySubDeptList(ArrayList e01a1list) {
         ArrayList deptList=new ArrayList();
         initSuperiorFld();
         //
         if (superiorFld==null || "".equals(superiorFld)){
             return deptList;
         }
         if (e01a1list.size()<1){
             return deptList;
         }
         String strsql="";
         String tablename="";

         Map<String,Calendar> dateMap = getPeriodDateRange(this.periodType, this.periodYear, this.periodMonth, this.periodWeek);
         Calendar startD = dateMap.get("start");
         Calendar endD = dateMap.get("end");
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         String startStr = sdf.format(startD.getTime());
         String endStr = sdf.format(endD.getTime());
         if (superiorFld!=null && !"".equals(superiorFld)){
             try{
                 for (int i=0;i<e01a1list.size();i++){
                     LazyDynaBean e01abean= (LazyDynaBean)e01a1list.get(i);
                     String e01a1 =(String )e01abean.get("e01a1");
                     if ("".equals(e01a1)) {
                         continue;
                     }
                     //循环下级岗位 通过岗位关联人员，人员关联不到，显示空。
                     strsql="select k01.e01a1,b01.b0110 from k01 "
                 		 + "left join B01 on k01.e01a1=b01."+WorkPlanConstant.DEPTlEADERFld+" "
                 		 // 排除掉已经失效的岗位 chent add 20180122 start
                 		 + "inner join organization org  ON b01.B0110 = org.codeitemid AND ("
                         +Sql_switcher.dateValue(startStr)+" BETWEEN org.start_date AND org.end_date or "
                         +Sql_switcher.dateValue(endStr)+" BETWEEN org.start_date AND org.end_date or ("
                         +Sql_switcher.dateValue(startStr)+"<org.start_date and "+Sql_switcher.dateValue(endStr)+">org.end_date) )"

                 		 // 排除掉已经失效的岗位 chent add 20180122 end
                         +" where "+superiorFld+"='"+e01a1+"'"
                         ;
                     //考虑下级不负责具体部门但分管部门
                     strsql+=" union     select k01.e01a1,b01.b0110 "
                    	   +" from k01 left join B01 on k01.e01a1=b01."+WorkPlanConstant.DEPTlEADERFld+" "
                    	   +" where "+superiorFld+"='"+e01a1+"' and b01.b0110 is null  "
                    	   +" and exists ( select null from K01 K1,B01 BB WHERE  k01.e01a1=K1."+superiorFld+"  AND K1.E01A1=BB."+WorkPlanConstant.DEPTlEADERFld+" ) ";

                     RowSet k01rset=dao.search(strsql);
                     while (k01rset.next()){
                         String _e01a1=k01rset.getString("e01a1");
                         String _b0110=k01rset.getString("b0110");
                         if(_b0110 == null ) {
                             _b0110 = "";
                         }

                         boolean b_e01a1_leader=false;
                         String isleader="false";
                         if (!"".equals(_b0110)){//是负责岗位
                             b_e01a1_leader=true;
                             isleader="true";
                         }
                         else {//不是负责岗位 继续查看下级是否有负责部门
                             b_e01a1_leader = workPlanUtil.isHasSubDept(_e01a1,false);
                         }
                         if (b_e01a1_leader ){
                             tablename=workPlanUtil.getPeopleSqlByE01a1(_e01a1);
                             boolean b_person_leader=false;//是否有人负责
                             if (!"".equals(tablename)){
                                 tablename="("+tablename+") T";
                                 strsql="select T.* from "+tablename;
                                 RowSet rset=dao.search(strsql);

                                 if (rset.next()){//有人负责
                                     b_person_leader=true;
                                     String _nbase=rset.getString("nbase");
                                     String _a0100=rset.getString("a0100");
                                     String ispart=rset.getString("ispart");
                                     String _a0101=rset.getString("a0101");

                                     LazyDynaBean bean = new LazyDynaBean();
                                     bean.set("nbase", _nbase);
                                     bean.set("a0100", _a0100);
                                     bean.set("ispart", ispart);
                                     bean.set("e01a1",  _e01a1);
                                     bean.set("e0122",  _b0110);
                                     bean.set("a0101",  _a0101);
                                     bean.set("isleader",  isleader);
                                     bean.set("haveleader",  "yes");
                                     deptList.add(bean);
                                 }
                             }
                             if (!b_person_leader) { //无人负责
                                 LazyDynaBean bean = new LazyDynaBean();
                                 bean.set("nbase", "");
                                 bean.set("a0100", "");
                                 bean.set("ispart", "");
                                 bean.set("e01a1",  _e01a1);
                                 bean.set("e0122",  _b0110);
                                 bean.set("a0101",  "");
                                 bean.set("isleader",  isleader);
                                 bean.set("haveleader",  "no");
                                 deptList.add(bean);
                             }
                         }
                     }
                 }
             }
             catch(Exception e){
                 e.printStackTrace();
             }
         }
         return deptList;
     }


    /**
     * @Title: getMySubDeptList
     * @Description: 下属负责部门 暂时采用 先把数据全查出来 再分页
     * @param @param e01a1list 根据下级岗位查询负责的部门
     * @param @param cur_page
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getMySubDeptList(ArrayList e01a1list, int cur_page) {
        String info="";
        ArrayList peopleList=getMySubDeptList(e01a1list);
        int pagecount=peopleList.size();
        if (cur_page>1){
            while ((cur_page-1)*WorkPlanConstant.PAGESIZE>=pagecount){
                if (cur_page>1) {
                    cur_page=cur_page-1;
                }
            }
        }
        String strsql="";
        int begin_index=(cur_page-1)*WorkPlanConstant.PAGESIZE;
        int end_index=cur_page*WorkPlanConstant.PAGESIZE;
        if (end_index>pagecount) {
            end_index=pagecount;
        }

        for (int i=begin_index;i<end_index;i++){
            LazyDynaBean bean = (LazyDynaBean)(peopleList.get(i));
            String _nbase= (String)bean.get("nbase");
            String _a0100= (String)bean.get("a0100");
            String _a0101= (String)bean.get("a0101");
            String _e0122= (String)bean.get("e0122");
            String _e01a1= (String)bean.get("e01a1");
            String ispart= (String)bean.get("ispart");
            String isleader= (String)bean.get("isleader");
            String haveleader= (String)bean.get("haveleader");
            String imagepath =getPhotoPath(_nbase, _a0100);
            int subcount=0;
            //strsql="select count(*) cnt from b01 where "+WorkPlanConstant.DEPTlEADERFld
           // +"='"+_e01a1+"'";
            strsql="select count(*) from k01 inner join B01 "
                +" on k01.e01a1=b01."+WorkPlanConstant.DEPTlEADERFld
                +" where "+superiorFld+"='"+_e01a1+"'";
            try{
                RowSet rset1=dao.search(strsql);
                if (rset1.next()){
                    subcount=subcount+rset1.getInt(1);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            String subvalue="";
            if (subcount>0){
                subvalue= "下辖部门"+subcount+"个";
            }
            else {
                if (workPlanUtil.isHasSubDept(_e01a1,false)){
                    subvalue= "下辖部门";
                }

            }
            String e01a1desc=AdminCode.getCodeName("@K",_e01a1 );
            if ("1".equals(ispart)){
               // e01a1desc=e01a1desc+"(兼职)";
            }
            String e0122desc=AdminCode.getCodeName("UM",_e0122 );
            if ("".equals(e0122desc)){
                e0122desc=AdminCode.getCodeName("UN",_e0122 );
            }
            String stritem="{"
                +quotedDoubleValue("objectid")+":"+quotedDoubleValue(WorkPlanUtil.encryption(_nbase+_a0100))
                +","
                +quotedDoubleValue("e0122")+":"+quotedDoubleValue(WorkPlanUtil.encryption(_e0122))
                +","
                +quotedDoubleValue("imagepath")+":"+quotedDoubleValue(imagepath)
                +","
                +quotedDoubleValue("name")+":"+quotedDoubleValue(_a0101
                        +" "+e01a1desc)
                +","
                +quotedDoubleValue("hintinfo")+":"+quotedDoubleValue(_a0101
                        +"<br /> "+e01a1desc)
                +","
                +quotedDoubleValue("e0122desc")+":"+quotedDoubleValue(e0122desc)
                +","
                +quotedDoubleValue("e01a1")+":"+quotedDoubleValue(WorkPlanUtil.encryption(_e01a1))
                +","
                +quotedDoubleValue("subpeople")+":"+quotedDoubleValue(subvalue)
                +","
                +quotedDoubleValue("isleader")+":"+quotedDoubleValue(isleader)
                +","
                +quotedDoubleValue("haveleader")+":"+quotedDoubleValue(haveleader)
                +","
                +quotedDoubleValue("cur_page")+":"+quotedDoubleValue(cur_page+"")
                +","
                    +quotedDoubleValue("p0723")+":"+quotedDoubleValue(WorkPlanUtil.encryption("2"))
                +"}"
                ;
            //szk 总结需要页数
            if ("".equals(info)){
                info = stritem;
            }
            else {
                info =info+","+ stritem;
            }
        }

        return info;
    }


    private void  getMySubDeptPlanDetail(RowSet rset,ArrayList deptList,boolean bpartE01a1) {
        try
        {
            while (rset.next()){
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("b0110", rset.getString("b0110"));
                String itemdesc=rset.getString("codeitemdesc");
                if (bpartE01a1) {
                    itemdesc=itemdesc+"(兼职)";
                }
                bean.set("codeitemdesc",  itemdesc);
                String p0700=rset.getString("p0700");
                p0700=p0700==null?"":p0700;

                bean.set("p0700", p0700 );
                String p0719=rset.getString("p0719");
                p0719=p0719==null?"":p0719;
                bean.set("p0719",  p0719);

                deptList.add(bean);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /**
     * @Title: getMySubDeptMainInfo
     * @Description: 下属部门主界面展现
     * @param @param e01a1list
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getMySubDeptMainInfo(ArrayList e01a1list) {
        String info = "";
        initSuperiorFld();
        if (superiorFld == null || "".equals(superiorFld)) {
            return "";
        }
        if (e01a1list.size() < 1) {
            return "";
        }

        String strsql = "";
        String tablename = "";
        String e01a1s = "";
        for (int i = 0; i < e01a1list.size(); i++) {
            LazyDynaBean e01abean = (LazyDynaBean) e01a1list.get(i);
            String e01a1 = (String) e01abean.get("e01a1");
            if ("".equals(e01a1)) {
                continue;
            }
            if (e01a1s.length() > 0) {
                e01a1s = e01a1s + ",'" + e01a1 + "'";
            } else {
                e01a1s = e01a1s + "'" + e01a1 + "'";
            }
        }

        try {
            int sum_count = 0;// 应报
            int approve_count = 0;// 已批
            int submit_count = 0;// 已报:已报批+已批准
            int unsubmit_count = 0;// 未报
            int unapprove_count = 0;//未批：已报批
            int change_count = 0;//已变更
               // 有负责部门的岗位。
            tablename = "select k01.e01a1,b01.b0110,O.codeitemdesc from k01,B01,organization O "
                + " where k01.e01a1=b01." + WorkPlanConstant.DEPTlEADERFld
                + " and B01.b0110=O.codeitemid"
                + " and K01." + superiorFld + " in (" + e01a1s + ")" + "";
            //关联p07
            strsql = " Select P07.p0700, T.e01a1,T.B0110,T.codeitemdesc,p07.changeflag,p07.p0719 from (" + tablename + ") T "
            + "left join (select * from P07 where " + this.getPlanPublicWhere()
            + ") P07 on p07.p0707 =T.B0110 ";

            RowSet rset = dao.search(strsql);
            while (rset.next()) {
                String _p0719 = rset.getString("p0719");
                String _e0122 = rset.getString("b0110");
                String _e01a1 = rset.getString("e01a1");
                String _e0122desc = rset.getString("codeitemdesc");
                int changeFlag = rset.getInt("changeflag");
                String _nbase = "";
                String _a0100 = "";
                String _a0101 = "";
                tablename = workPlanUtil.getPeopleSqlByE01a1(_e01a1);
                boolean b_person_leader = false;// 是否有人负责
                if (!"".equals(tablename)) {
                    tablename = "(" + tablename + ") T";
                    strsql = "select T.* from " + tablename;
                    RowSet rset1 = dao.search(strsql);
                    if (rset1.next()) {// 有人负责
                        b_person_leader = true;
                        _nbase = rset1.getString("nbase");
                        _a0100 = rset1.getString("a0100");
                        _a0101 = rset1.getString("a0101");
                    }
                };

                String appstatus = "未提交";
                String submitType="0";
                String remind = "提醒";
                sum_count++;
                if (_p0719 == null) {
                    appstatus = workPlanUtil.getPlanStatusDesc("");
                    unsubmit_count++;
                } else if ("0".equals(_p0719)||"3".equals(_p0719)) {//起草 驳回
                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
                    unsubmit_count++;
                } else if ("1".equals(_p0719)) {//发布:未批
                    appstatus = "已提交";
                    submitType="3";
                    remind="";
                    //submit_count++;
                    unapprove_count++;
                }
                else if ("2".equals(_p0719)) {//批准
                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
                    if(isStateChange(rset.getString("p0700"))){
                    	appstatus = "已变更";
                    }
                    submitType="2";
                    approve_count++;
                    remind="";
                }

                if(changeFlag == 1){//计划已变更 chent 20160415
                	change_count++;
                }
                String imagepath = "";

                imagepath = getPhotoPath(_nbase, _a0100);


                String stritem = "{"
                    + quotedDoubleValue("objectid") + ":"
                    + quotedDoubleValue(WorkPlanUtil.encryption(_nbase + _a0100)) + ","
                    + quotedDoubleValue("imagepath") + ":" + quotedDoubleValue(imagepath)
                    + ","+ quotedDoubleValue("name") + ":" + quotedDoubleValue(_a0101)
                    + "," + quotedDoubleValue("e0122") + ":" + quotedDoubleValue(WorkPlanUtil.encryption(_e0122))
                    + "," + quotedDoubleValue("e0122desc") + ":" + quotedDoubleValue(_e0122desc)
                    + "," + quotedDoubleValue("appstatus") + ":" + quotedDoubleValue(appstatus)
                    + "," + quotedDoubleValue("submittype") + ":" + quotedDoubleValue(submitType)
                    + "," + quotedDoubleValue("remind") + ":" + quotedDoubleValue(remind)
                   +","+quotedDoubleValue("p0723")+":"+quotedDoubleValue(WorkPlanUtil.encryption("2"))
                   +","+quotedDoubleValue("changeflag")+":"+quotedDoubleValue(String.valueOf(changeFlag))
                + "}";
                if ("".equals(info)) {
                    info = stritem;
                } else {
                    info = info + "," + stritem;
                }


            }

            info = quotedDoubleValue("detail_list") + ":[" + info + "]";
            info = info + "," + quotedDoubleValue("team_title") + ":"
                + quotedDoubleValue("下属部门" +
                        workPlanUtil.getPlanPeriodDesc(periodType, periodYear, periodMonth, periodWeek)
                        + "提交情况")
                + "," + quotedDoubleValue("sum_count") + ":" + quotedDoubleValue(sum_count + "")
                //已报人数是已批和未批之和
                + "," + quotedDoubleValue("submit_count") + ":" + quotedDoubleValue(approve_count + unapprove_count + "")
                + "," + quotedDoubleValue("approve_count") + ":" + quotedDoubleValue(approve_count + "")
                + "," + quotedDoubleValue("unsubmit_count") + ":" + quotedDoubleValue(unsubmit_count + "")
                + "," + quotedDoubleValue("unapprove_count") + ":" + quotedDoubleValue(unapprove_count + "")
                + "," + quotedDoubleValue("change_count") + ":" + quotedDoubleValue(change_count + "")
            + "";

        } catch (Exception e) {
            e.printStackTrace();
        }
        info = quotedDoubleValue("teaminfo") + ":{" + info + "}";
        return info;
    }


    /**
     * @Title: getMyTeamPeopleMainInfo
     * @Description: 团队成员主界面展现
     * @param
     * @param nbase
     * @param
     * @param a0100
     * @param
     * @return
     * @return String
     * @author:wangrd
     * @throws
     */
    public String getMyTeamPeopleMainInfo(String nbase,String a0100) {
        String info="";
        initSuperiorFld();
        if (superiorFld==null || "".equals(superiorFld)){
            return quotedDoubleValue("teaminfo")+":{"+info+"}";
        }

        String tablename=getTeamPeopleSql(this.superiorFld,nbase,a0100,true);
        if ("".equals(tablename)){
           return quotedDoubleValue("teaminfo")+":{"+info+"}";
        }
        tablename="("+tablename+") T";

        // 团队成员人员顺序要根据a0000排列 chent 20171204 modify
        String strsql=" select p07.p0700, T.*,P07.p0719,p07.E0122 as p07e0122,p07.e01a1 as p07e01a1,p07.changeflag from " +tablename
                +" left join "+nbase+"a01 A01 on A01.a0100=T.a0100 "
                +" left join p07 on P07.nbase=T.nbase and P07.a0100=T.a0100"
                +" and "+this.getPlanPublicWhere()
                +" order by A01.a0000";

        try{
            int sum_count=0;//应报
            int approve_count=0;//已批
            int submit_count=0;//已报:已报批+已批准
            int unsubmit_count=0;//未报
            int unapprove_count = 0;//未批：已报批
            int change_count = 0;//已变更
            RowSet rset=dao.search(strsql);
            while (rset.next()){
                String _nbase= rset.getString("nbase");
                String _a0100= rset.getString("a0100");
                String _a0101= rset.getString("a0101");
                String _e0122= rset.getString("e0122");
                String _e01a1= rset.getString("e01a1");
                String _p07e01a1= rset.getString("p07e01a1");
                int changeFlag = rset.getInt("changeflag");
                if (_p07e01a1!=null && _p07e01a1.length()>0){
                    _e01a1= _p07e01a1;
                    _e0122= rset.getString("p07e0122");
                }
                String _p0719= rset.getString("p0719");
                String appstatus="未提交";
                String submitType="0";
                String remind="提醒";
                sum_count++;
                if (_p0719 == null) {
                    appstatus = workPlanUtil.getPlanStatusDesc("");
                    unsubmit_count++;
                } else if ("0".equals(_p0719)||"3".equals(_p0719)) {//起草 驳回
                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
                    unsubmit_count++;
                } else if ("1".equals(_p0719)) {//发布:未批
                    submitType="3";
                    appstatus = "已提交";
                    submit_count++;
                    remind="";
                    unapprove_count++;
                }
                else if ("2".equals(_p0719)) {//批准  加入已变更状态,通过查询p08表中的p0833来区分是否变更 wusy
                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
                    if(isStateChange(rset.getString("p0700"))){
                    	appstatus = "已变更";
                    }
                    submitType="2";
                    submit_count++;
                    approve_count++;
                    remind="";
                }
                if(changeFlag == 1){//计划已变更 chent 20160415
                	change_count++;
                }
                String imagepath =getPhotoPath(_nbase, _a0100);
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
                    +","+quotedDoubleValue("changeflag")+":"+quotedDoubleValue(String.valueOf(changeFlag))
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

            info =info+","
            +quotedDoubleValue("team_title")+":"+quotedDoubleValue("团队"
                    + workPlanUtil.getPlanPeriodDesc(periodType, periodYear, periodMonth, periodWeek)
                    +"提交情况")
            +","
            +quotedDoubleValue("sum_count")+":"+quotedDoubleValue(sum_count+"")
            +","
            +quotedDoubleValue("submit_count")+":"+quotedDoubleValue(submit_count+"")
            +","
            +quotedDoubleValue("approve_count")+":"+quotedDoubleValue(approve_count+"")
            +","
            +quotedDoubleValue("unsubmit_count")+":"+quotedDoubleValue(unsubmit_count+"")
            + ","
            + quotedDoubleValue("unapprove_count") + ":" + quotedDoubleValue(unapprove_count + "")
            + "," + quotedDoubleValue("change_count") + ":" + quotedDoubleValue(change_count + "")
            +"";


        }
        catch(Exception e){
            e.printStackTrace();
        }
        info=quotedDoubleValue("teaminfo")+":{"+info+"}";
        return info;
    }

    /**
     * @Title: getMyTeamPeopleMainInfo
     * @Description: 团队成员主界面展现
     * @param e01a1
     * @return String
     * @throws
     */

    public String getMyTeamPeopleMainInfo(ArrayList e01a1List) {
    	 String info="";
         initSuperiorFld();
         if (superiorFld==null || "".equals(superiorFld)){
             return quotedDoubleValue("teaminfo")+":{"+info+"}";
         }
         int pagesize=e01a1List.size();
         int sum_count=0;//应报
         int approve_count=0;//已批
         int submit_count=0;//已报:已报批+已批准
         int unsubmit_count=0;//未报
         int unapprove_count = 0;//未批：已报批
         int change_count = 0;//已变更
         for(int i=0;i<pagesize;i++){
	         try {
	             LazyDynaBean bean = (LazyDynaBean) (e01a1List.get(i));
    			 String e01a1= (String) bean.get("e01a1");
    				 String tablename=getTeamPeopleSql(this.superiorFld,e01a1,true);
 					/* String tablename=getTeamPeopleSql(this.superiorFld,"Usr",a0100,true);*/
 					 tablename="("+tablename+") T";
 					 String strsql= " select T.*,P07.p0700,P07.p0719,p07.E0122 as p07e0122,p07.e01a1 as p07e01a1,p07.changeflag from " +tablename
 		                +" left join p07 on P07.a0100=T.a0100 and p07.nbase=T.nbase"
 		                +" and "+this.getPlanPublicWhere();
 			         RowSet rset1=dao.search(strsql);
 					 while(rset1.next()){
 						    String _nbase= rset1.getString("nbase");
 			                String _a0100= rset1.getString("a0100");
 			                String _e0122= rset1.getString("e0122");
 			                String _e01a1= rset1.getString("e01a1");
 			                String _p0719= rset1.getString("p0719");
 			                String _p07e01a1= rset1.getString("p07e01a1");
 			               int changeFlag = rset1.getInt("changeflag");
 			                if (_p07e01a1!=null && _p07e01a1.length()>0){
 			                    _e01a1= _p07e01a1;
 			                    _e0122= rset1.getString("p07e0122");
 			                }
 			                String appstatus="未提交";
 			                String submitType="0";
 			                String remind="提醒";
 			                sum_count++;
 			                if (_p0719 == null) {
 			                    appstatus = workPlanUtil.getPlanStatusDesc("");
 			                    unsubmit_count++;
 			                } else if ("0".equals(_p0719)||"3".equals(_p0719)) {//起草 驳回
 			                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
 			                    unsubmit_count++;
 			                } else if ("1".equals(_p0719)) {//发布:未批
 			                    submitType="3";
 			                    appstatus = "已提交";
 			                    submit_count++;
 			                    remind="";
 			                   unapprove_count++;
 			                }
 			                else if ("2".equals(_p0719)) {//批准
 			                    appstatus = workPlanUtil.getPlanStatusDesc(_p0719);
 			                   if(isStateChange(rset1.getString("p0700"))){
 			                    	appstatus = "已变更";
 			                    }
 			                    submitType="2";
 			                    approve_count++;
 			                    remind="";
 			                }
 			               if(changeFlag == 1){//计划已变更 chent 20160415
 			                	change_count++;
 			                }
 			                String imagepath =getPhotoPath(_nbase, _a0100);
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
 			                    +quotedDoubleValue("name")+":"+quotedDoubleValue(workPlanUtil.getUsrA0101(_nbase, _a0100))
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
 			                    +","+quotedDoubleValue("changeflag")+":"+quotedDoubleValue(String.valueOf(changeFlag))
 			                   +"}"
 			                    ;
 			                if ("".equals(info)){
 			                    info = stritem;
 			                }
 			                else {
 			                    info =info+","+ stritem;
 			                }

 			            }

			} catch (SQLException e) {
				e.printStackTrace();
			}
		 }
         info=quotedDoubleValue("detail_list")+":["+info+"]";

         info =info+","
         +quotedDoubleValue("team_title")+":"+quotedDoubleValue("团队"
                 + workPlanUtil.getPlanPeriodDesc(periodType, periodYear, periodMonth, periodWeek)
                 +"提交情况")
         +","
         +quotedDoubleValue("sum_count")+":"+quotedDoubleValue(sum_count+"")
         +","
         +quotedDoubleValue("submit_count")+":"+quotedDoubleValue(submit_count+approve_count+"")
         +","
         +quotedDoubleValue("approve_count")+":"+quotedDoubleValue(approve_count+"")
         +","
         +quotedDoubleValue("unsubmit_count")+":"+quotedDoubleValue(unsubmit_count+"")
         + ","
         + quotedDoubleValue("unapprove_count") + ":" + quotedDoubleValue(unapprove_count + "")
         + "," + quotedDoubleValue("change_count") + ":" + quotedDoubleValue(change_count + "")
         +"";
		 info=quotedDoubleValue("teaminfo")+":{"+info+"}";

	     return info;
    }



    /**
     * @Title: isHasTeamPeople
     * @Description:   是否有团队成员
     * @param @param nbase
     * @param @param a0100
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    private boolean isHasTeamPeople(String nbase,String a0100) {
        boolean b=false;
        try
        {   initSuperiorFld();
            String tablename="";
            tablename=getTeamPeopleSql(this.superiorFld,nbase,a0100,true);
            if ("".equals(tablename)){
               return b;
            }

            tablename="("+tablename+") T";
            int recordCount=0;
            String strsql="select count(*) cnt from "+tablename;
            try{
                RowSet rset=dao.search(strsql);
                if (rset.next()){
                    recordCount=rset.getInt(1);
                }
                if (recordCount>0){
                    b=true;
                }else{//当前人员所有下级岗位上都没人，要递归穿透岗位查找下级岗位，直到岗位上有人
    	            b = this.isHasPeopleInNextE01A1(nbase, a0100);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return b;
    }

    /**
     * @Title: isHasMyDept
     * @Description: 有负责的下级机构
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    private boolean isHasMyDept() {
        boolean b=false;
        try
        {
            if(!workPlanUtil.isSelfUser())
            {
                return b;
            }
            String  deptLeaderFld=WorkPlanConstant.DEPTlEADERFld;
            if (!dbw.isExistField("B01",deptLeaderFld ,false)){
                return b;
            }

            String e01a1=this.userView.getUserPosId();
            String str="select count(*) as cnt from B01 where "+deptLeaderFld+" ='"+e01a1+"'";
            RowSet rset =dao.search(str);
            if (rset.getInt(1)>0){
               b=true;
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return b;
    }


    /**
     * @Title: isHasSubDept
     * @Description: 有负责的下下级及下下下级机构
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean isHasSubDept(String nbase,String a0100) {
        boolean b=false;
        try{
            ArrayList e01a1list=  workPlanUtil.getMyE01a1List(nbase, a0100);
            if (e01a1list.size()<1){
                return b;
            }
            for (int i=0;i<e01a1list.size();i++){
                LazyDynaBean e01abean= (LazyDynaBean)e01a1list.get(i);
                String e01a1 =(String )e01abean.get("e01a1");
                b = workPlanUtil.isHasSubDept(e01a1,false);
                if (b) {
                    break;
                }
            }
        }
        catch(Exception e){

        }
        return b;
    }

    /**
     * @Title: getHummapTypeList
     * @Description: 返回人力地图类型列表
     * @param
     * @return ArrayList
     * @author:wangrd

    */
    public ArrayList getHummapTypeList() {
        ArrayList list = new ArrayList();
        String nbase=this.userView.getDbname();
        String a0100=this.userView.getA0100();

        try
        {
           if("".equals(nbase)){
               return list;
           }
            //我的部门
           if("org".equals(this.planType)){
	            ArrayList deptlist=workPlanUtil.getDeptList(nbase,a0100);
	            if (deptlist.size()>0){//有负责的部门
	                LazyDynaBean bean =  new LazyDynaBean();
	                bean.set("type_id", "1");
	                bean.set("type_name", "我的部门");
	                list.add(bean);
	            }
	            //bug16097,修改显示顺序, 我的部门-->下属部门-->团队成员-->我关注的
	          //下属的部门计划
	            if (isHasSubDept(nbase,a0100)){
	                LazyDynaBean bean =  new LazyDynaBean();
	                bean.set("type_id", "3");
	                bean.set("type_name", "下属部门");
	                list.add(bean);
	            }
           }else if("person".equals(this.planType)){
	            //团队成员
	            if (isHasTeamPeople(nbase,a0100)){
	                LazyDynaBean bean =  new LazyDynaBean();
	                bean.set("type_id", "2");
	                bean.set("type_name", "团队成员");
	                list.add(bean);
	            }
	            LazyDynaBean bean =  new LazyDynaBean();
	            bean.set("type_id", "4");
	            bean.set("type_name", "我关注的");
	            list.add(bean);
           }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return list;
 }

    /**
     * @Title: getHumanMapTypeJsonList
     * @Description: 返回人力地图类型列表
     * @param
     * @return void
     * @author:wangrd

    */
    public String getHumanMapTypeJsonList() {
        String typeList="";
        try
        {
            ArrayList list=getHummapTypeList();
            for (int i=0;i<list.size();i++){
                LazyDynaBean bean =(LazyDynaBean)list.get(i);
                String type_id=(String)bean.get("type_id");
                String type_name=(String)bean.get("type_name");

                String stritem="{"
                    +quotedDoubleValue("type_id")+":"+quotedDoubleValue(
                            type_id)
                    +","
                    +quotedDoubleValue("type_name")+":"+quotedDoubleValue(type_name)
                    +"}";
                if (typeList.length()>0){
                    typeList =typeList+",";
                }
                typeList =typeList+ stritem;
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        typeList ="{"
            +quotedDoubleValue("typelist")+":"
            +"[" +typeList+"]"
            +"}";
        return typeList;

 }



    /**
     * @Title: getPeoplePlanId
     * @Description:是否存在人员 某个期间的计划   存在返回计划id 如返回0 标识没有计划
     * @param @param objectid
     * @param @param period_type
     * @param @param period_year
     * @param @param period_month
     * @param @param period_week
     * @return void
     * @author:wangrd
     * @throws
    */
    public int getPeoplePlanId(String nbase,String a0100,
            String period_type,String period_year,String period_month,String period_week) {

        int plan_id=0;
        try{
            String strsql="select * from p07 where p0723=1 and p0725="+period_type
                +" and p0727="+period_year;
            if (period_month!=null && Integer.parseInt(("".equals(period_month)) ? "0" : period_month)>0){
                strsql=strsql+" and p0729="+period_month;
            }
            if (WorkPlanConstant.Cycle.WEEK.equals(period_type)){
                if (period_week!=null && period_week.length()>0){
                    strsql=strsql+" and p0731="+period_week;
                }
            }
            strsql = strsql + " and upper(nbase) ='" +nbase.toUpperCase() + "'" + " and a0100 ='" + a0100 + "'";

            try {
                RowSet rset = dao.search(strsql);
                if (rset.next()) {
                    plan_id= rset.getInt("p0700");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan_id;
    }

    /**
     * @Title: getTeamPlanId
     * @Description:是否存在团队 某个期间的计划   存在返回计划id 如返回0 标识没有计划
     * @param @param objectid
     * @param @param period_type
     * @param @param period_year
     * @param @param period_month
     * @param @param period_week
     * @return int
     * @author:wusy
     * @throws
    */
    public int getTeamPlanId(String p0707, Integer period_type,Integer period_year,Integer period_month,Integer period_week){
    	int teamPlanID = 0;
    	try{
            String strsql="select * from p07 where p0723=2 and p0725="+period_type
                +" and p0727="+period_year;
            if (period_month!=null && period_month>0){
                strsql=strsql+" and p0729="+period_month;
            }
            if (WorkPlanConstant.Cycle.WEEK.equals(period_type)){
                if (period_week!=null && period_week>0){
                    strsql=strsql+" and p0731="+period_week;
                }
            }
            strsql = strsql + " and p0707 ='" +p0707 + "'";

            try {
                RowSet rset = dao.search(strsql);
                if (rset.next()) {
                	teamPlanID= rset.getInt("p0700");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return teamPlanID;
    }

    /**
     * @Title: addPlan
     * @Description: 为负责人、参与人新建计划
     * @param @param p07vo
     * @param @param objectid
     * @return void
     * @author:wangrd
     * @throws
    */
    public void addPlan(RecordVo p07vo,String objectid) {
        try{
            if (p07vo==null) {
                return;
            }
            if (objectid==null||objectid.length()!=11) {
                return;
            }
            //是否存在计划
            String p0723="1";//只是人员计划
            String period_type=p07vo.getString("p0725");
            String period_year=p07vo.getString("p0727");
            String period_month=p07vo.getString("p0729");
            String period_week=p07vo.getString("p0731");
            String  nbase =objectid.substring(0, 3);
            String a0100 =objectid.substring(3, objectid.length());

            int plan_id=getPeoplePlanId(nbase,a0100,
                    period_type,period_year,period_month,period_week);
            if (plan_id>0){
                return;
            }
            WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
            String createUserName = (String) wpUtil.getUserNamePassword(nbase, a0100).get("username");
            String createUserFullName = wpUtil.getUsrA0101(nbase, a0100);
            RecordVo vo=new RecordVo("P07");
            IDGenerator idg=new IDGenerator(2,this.conn);
            String id=idg.getId("P07.P0700");
            vo.setString("p0700",id);
            vo.setString("p0723",p0723);
            vo.setString("p0725",period_type);
            vo.setString("p0727",period_year);
            if (!WorkPlanConstant.Cycle.YEAR.equals(period_type)){
                vo.setString("p0729",period_month);
            }
            if (WorkPlanConstant.Cycle.WEEK.equals(period_type)){
                vo.setString("p0731",period_week);
            }
            vo.setString("nbase",nbase);
            vo.setString("a0100",a0100);
            String strsql="select * from "+nbase+"A01 where a0100='"+a0100+"'";
            RowSet rset=dao.search(strsql);
            if (rset.next()){
                vo.setString("b0110",rset.getString("b0110"));
                vo.setString("e0122",rset.getString("e0122"));
                vo.setString("e01a1",rset.getString("e01a1"));
                if (rset.getString("e01a1")!=null) {
                    vo.setString("supere01a1"
                            ,workPlanUtil.getDirectSuperE01a1(rset.getString("e01a1")));
                }
            }
            vo.setDate("create_time",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
            vo.setString("create_user",createUserName);
            vo.setString("create_fullname",createUserFullName);
            vo.setString("p0719","0");
            vo.setString("p0721",WorkPlanConstant.Scope.SUPERIOR);
            dao.addValueObject(vo);

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    /**
     * @Title: addPlan
     * @Description: 新建计划
     * @param
     * @return void
     * @author:wangrd
     * @throws
    */
    public void addPlan() throws Exception{
    	WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
    	boolean isEditSub = false;
        try{
        	UserView uV = this.userView;
        	if(StringUtils.isBlank(this.nBase) || StringUtils.isBlank(this.A0100)){
        		throw new Exception("该部门没有负责人,不可添加任务!");
        	}
        	if(!(this.nBase+this.A0100).equals((this.userView.getDbname()+this.userView.getA0100()))){
        		String username = (String) wpUtil.getUserNamePassword(this.nBase, this.A0100).get("username");
        		if("".equals(username) || username == null){
        			throw new RuntimeException("该人员用户名不存在或未设置");
        		}
                UserView view = new UserView(username,this.conn);
                view.canLogin(false);
                uV = view;
                isEditSub = true;
            }
            RecordVo vo=new RecordVo("P07");
            IDGenerator idg=new IDGenerator(2,this.conn);
            String id=idg.getId("P07.P0700");
            vo.setString("p0700",id);
            vo.setString("p0723",this.P0723);
            vo.setString("p0725",this.periodType);
            vo.setString("p0727",this.periodYear);
            if (!WorkPlanConstant.Cycle.YEAR.equals(periodType)){
                vo.setString("p0729",this.periodMonth);
            }
            if (WorkPlanConstant.Cycle.WEEK.equals(periodType)){
                vo.setString("p0731",this.periodWeek);
            }
            if ("2".equals(this.P0723)){
                vo.setString("p0707",this.objectId);
				if(!StringUtils.isEmpty(this.objectId)){
					String leader = wpUtil.getFirstDeptLeaders(this.objectId);
					if(!StringUtils.isEmpty(leader)){
						String nbase = leader.substring(0, 3);
						String a0100 = leader.substring(3);
						String guidkey = wpUtil.getInfoByNbsA0100(nbase, a0100).get("guidkey");
						vo.setString("guidkey", guidkey);//唯一标识
					}
				}
            } else {
                vo.setString("nbase",this.nBase);
                vo.setString("a0100",this.A0100);
                vo.setString("b0110",uV.getUserOrgId());
                vo.setString("e0122",uV.getUserDeptId());
                vo.setString("e01a1",uV.getUserPosId());
                vo.setString("guidkey",wpUtil.getInfoByNbsA0100(this.nBase, this.A0100).get("guidkey"));//唯一标识

                if (uV.getUserPosId()!=null) {
                    vo.setString("supere01a1"
                            ,workPlanUtil.getDirectSuperE01a1(uV.getUserPosId()));
                }
            }
            vo.setDate("create_time",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
            vo.setString("create_user",this.userView.getUserName());
            vo.setString("create_fullname",this.userView.getUserFullName());
            vo.setString("p0719","0");
            vo.setString("p0721",WorkPlanConstant.Scope.SUPERIOR);
            dao.addValueObject(vo);

        }
    catch(Exception e){
        e.printStackTrace();
        throw GeneralExceptionHandler.Handle(e);
    }

    }

    /**
     * @Title: addPlan
     * @Description: 制定计划到部门负责人时,p07中要添加一条对应的部门计划
     * @param @param plan	原来的计划
     * @param @param p0707 	部门负责人的部门id
     * @return String
     * @author:wusy
     * @throws
    */
    public String addTeamPlan(RecordVo plan, String p0707){
    	String id = "";
    	try {
    		RecordVo vo = new RecordVo("P07");
    		IDGenerator idg=new IDGenerator(2,this.conn);
			id = idg.getId("P07.P0700");
			vo.setString("p0700", id);
			vo.setString("p0707", p0707);
			vo.setString("create_user", this.userView.getUserName());
			vo.setString("create_fullname", this.userView.getUserFullName());
			vo.setDate("create_time",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
			vo.setInt("p0719", 0);
			vo.setInt("p0721", 4);
			vo.setInt("p0723", 2);
			vo.setInt("p0725", plan.getInt("p0725"));
			vo.setInt("p0727", plan.getInt("p0727"));
			vo.setInt("p0729", plan.getInt("p0729"));
			vo.setInt("p0731", plan.getInt("p0731"));
			dao.addValueObject(vo);


		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
    }


    /**
     * @Title:addDeptTask
     * @Description:分配任务到部门
     * @param b0110:分配到部门负责人所在部门的部门id
     * @param p0700:计划id
     * @param directorId:新负责人id
     * @param p0800:任务id
     * @return void
     * @author:wusy

    public void addDeptTask(String b0110, String p0700, String directorId,String p0800,String belongflag){
    	try {
	    	RecordVo newDirector_map = new RecordVo("per_task_map");
	    	IDGenerator idg = new IDGenerator(2, this.conn);
	    	PlanTaskBo bo = new PlanTaskBo(conn, userView);
	    	RecordVo task = bo.getTask(Integer.parseInt(p0800));
	    	int teamPlanId = addDeptPlan(p0700, b0110);
	    	newDirector_map.setInt("seq", new PlanTaskTreeTableBo(conn, teamPlanId).getSeq(b0110, task.getInt("p0831"), 2));
			newDirector_map.setString("nbase", null);
			newDirector_map.setString("a0100", null);
			newDirector_map.setInt("flag", 1);
			newDirector_map.setInt("p0700", Integer.parseInt(p0700));
			newDirector_map.setInt("belongflag", Integer.parseInt(belongflag));
			newDirector_map.setInt("p0800", Integer.parseInt(p0800));
			newDirector_map.setDate("create_time", new Date());
			newDirector_map.setString("create_user", userView.getUserName());
			newDirector_map.setString("create_fullname", userView.getUserFullName());
			newDirector_map.setString("org_id", b0110);
			String id_newDirector_map = idg.getId("per_task_map.id");
			newDirector_map.setInt("id", Integer.parseInt(id_newDirector_map));
			newDirector_map.setInt("dispatchflag", 1);//分配到部门
			dao.addValueObject(newDirector_map);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
       */
    /**
     * @Title:addDeptPlan
     * @Description:分配任务到部门,返回当前区间下部门计划id,如果不存在添加兵返回id
     * @param b0110:分配到部门负责人所在部门的部门id
     * @param p0700:计划id
     * @return int
     * @author:wusy
     */
    public int addDeptPlan(String p0700,String b0110){
    	PlanTaskBo bo = new PlanTaskBo(conn, userView);
    	int teamPlanId = 0;
		RecordVo plan;
		try {
			plan = bo.getPlan(Integer.parseInt(p0700));
			teamPlanId = getTeamPlanId(b0110, plan.getInt("p0725"), plan.getInt("p0727"), plan.getInt("p0729"), plan.getInt("p0731"));
			if(teamPlanId < 1){
				teamPlanId = Integer.parseInt(addTeamPlan(plan, b0110));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return teamPlanId;
    }

    /**
     * @Title: addTask
     * @Description:增加任务
     * @param @param valuemap
     * @param @return
     * @param @throws Exception
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean addTask(HashMap valuemap) throws Exception  {
        boolean b=false ;
        try {
        	//如果计划id不存在,新增计划
            if(this.P0700==null || this.P0700.length()<1){
                addPlan();
                loadPlanInfo();
                String planscope=(String)valuemap.get("plan_scope");
                if (planscope!=null && planscope.length()>0){
                    if (!WorkPlanConstant.Scope.SUPERIOR.equals(planscope)){
                        updatePlanScope("",planscope);
                    }
                }
            }
            if(this.P0700==null || this.P0700.length()<1){
                return b;
            }
            //计划id存在,就是添加任务
            String othertask=(String)valuemap.get("othertask");//父级是否是穿透任务
            String parent_id=(String)valuemap.get("task_parentid");
            String task_name=(String)valuemap.get("task_name");
            String task_desc=(String)valuemap.get("task_desc");
            String director=(String)valuemap.get("task_cyr");//负责人
            String task_rank=(String)valuemap.get("task_rank");
            task_rank = "0".equals(task_rank) ? "" : task_rank;
            String task_startdate=(String)valuemap.get("task_startdate");
            String task_enddate=(String)valuemap.get("task_enddate");
            String task_seq=(String)valuemap.get("task_seq");
            if (director!=null && director.length()>0){
                director=WorkPlanUtil.decryption(director);
            }

            HashMap params= new HashMap();
            params.put("objectid", this.objectId);
            params.put("p0700", this.P0700);
            params.put("p0723", this.P0723);
            params.put("p0800", parent_id);
            params.put("taskName", task_name);

            params.put("p0813", task_startdate);
            params.put("p0815", task_enddate);
            params.put("director", director);
            params.put("rank", task_rank+"");
            params.put("othertask", othertask);

            PlanTaskBo ptbo = new PlanTaskBo(this.conn, userView);
            if (parent_id!=null&& parent_id.length()>0){
                RecordVo p08Vo= ptbo.getTask(Integer.parseInt(parent_id));
                if (p08Vo.getInt("p0833")==WorkPlanConstant.TaskChangedStatus.Cancel){
                    throw new Exception("此任务已取消，不能新增任务！");
                }
            }

            if("1".equals(othertask)){//是穿透任务
            	int flag = ptbo.findParentNode(parent_id);
            	if(flag == 2){//是任务成员
            		throw new Exception("您没有权限新增任务！");
            	}
            }
            RecordVo subtask = ptbo.addSubtask(params);
            valuemap.put("clearIDs", (String)params.get("clearIDs"));//清除上下级权重的相关任务id lis  20160321


            String p0800=subtask.getString("p0800");
            PlanTaskTreeTableBo taskTreeBo=new PlanTaskTreeTableBo(this.conn,Integer.parseInt(this.P0700),this.userView);
            String _json =taskTreeBo.getRowJson(p0800,task_seq,taskTreeBo.getP07_vo().getInt("p0723"),othertask);
            this.returnInfo =_json;
            b=true;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return b;
    }

    /**
     * @param ranks
     * @Title: copyTask
     * @Description:复制上期任务
     * @param @param valuemap
     * @param @param isCopyInfo 是否复制进度、完成情况(okr复制任务优化，去掉isCopyInfo参数)
     * @param @return
     * @param @throws Exception
     * @return boolean
     * @author:szk
     * @throws
    */
	public boolean copyPirorTask(ArrayList newtableData, HashMap valuemap, String isCopyInfo) throws Exception {
		boolean b = false;
		try {
			/*
	         * 复制的任务 获取有人员信息变动的负责人集合
	         * */
			StringBuffer tasksid = new StringBuffer("");
			for(int i=0; i<newtableData.size(); i++) {
				tasksid.append((String)newtableData.get(i));
				if(i < newtableData.size()-1) {
                    tasksid.append(",");
                }
			}
			//HashMap changeMap = checkCopyPersonChange(tasksid.toString());

			String parent_id = (String) valuemap.get("parent_id"); // 点击的本期任务
			if (this.P0700 == null || this.P0700.length() < 1) {
				addPlan();
				loadPlanInfo();
				String planscope = (String) valuemap.get("plan_scope");
				if (planscope != null && planscope.length() > 0) {
					if (!WorkPlanConstant.Scope.SUPERIOR.equals(planscope)) {
						updatePlanScope("", planscope);
					}
				}
			}
			if (this.P0700 == null || this.P0700.length() < 1) {
				return b;
			}
			PlanTaskTreeTableBo thisTreeTableBo = new PlanTaskTreeTableBo(this.conn, Integer.parseInt(this.P0700));
			PlanTaskBo taskBo = new PlanTaskBo(this.conn, this.userView);
			RecordVo plan = taskBo.getPlan(Integer.parseInt(this.P0700));
			ArrayList oldp0800 = new ArrayList();
			ArrayList newp0800 = new ArrayList();
			boolean isrepeat = false;
			for (int i = 0; i < newtableData.size(); i++) {
				StringBuffer sbf = new StringBuffer();
				sbf.append("select rank from per_task_map where ");
				if("2".equals(this.P0723)){
					sbf.append("org_id = '"+ this.objectId + "'");
				}else{
					sbf.append("nbase = '"+ this.objectId.substring(0, 3) +"' and a0100 = '"+ this.objectId.substring(3) +"'");
				}
				sbf.append(" and p0800 = '"+ newtableData.get(i) +"'");
				RowSet rs = null;
				Object rankobj = null;
				rs = dao.search(sbf.toString());
				while(rs.next()){
					rankobj = rs.getObject("rank");
				}
				Float rank = rankobj == null || "".equals(rankobj) ? null : Float.valueOf(rankobj.toString()); // 权重
				String prior_p0800 = newtableData.get(i).toString();
				if ("".equals(prior_p0800)) {
                    continue;
                }
				// 开始复制
				RecordVo prior_vo = taskBo.getTask(Integer.parseInt(prior_p0800));

				int iPriorP0800 = prior_vo.getInt("p0800");
				boolean isCreater = taskBo.isCreater(iPriorP0800); // 当前用户是不是上期任务的创建人
				boolean isDirector = taskBo.isDirector(iPriorP0800); // 当前用户是不是上期任务的负责人
				String taskName = prior_vo.getString("p0801");

                IDGenerator idg = new IDGenerator(2, conn); // id生成器
                String id_p08 = idg.getId("P08.P0800");
                //本期p08   解决任务有重复的时候复制时，父子任务对应错误的问题  haosl update
                newp0800.add(id_p08);
                String p0811 = "";
                int p0833 = 0;
                // 计划处于报批状态，则新增的子任务默认是报批状态
                if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.HandIn) {
                    p0811 = WorkPlanConstant.TaskStatus.APPROVE;
                }
                else {
                    p0811 = WorkPlanConstant.TaskStatus.DRAFT;
                }

                // 起草中的计划，新增子任务的变更状态是未变更
                if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.Draft) {
                    p0833 = WorkPlanConstant.TaskChangedStatus.Normal;
                }
                else {
                    p0833 = WorkPlanConstant.TaskChangedStatus.add;
                }

                // 如果当前用户是公司最高领导(没有上级)，则新增的任务默认为已报批,未变更
                if (taskBo.isTopLeader(this.objectId, this.P0723)) {
                    // 计划处于已批准的状态
                    if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.Pass) {
                        p0811 = WorkPlanConstant.TaskStatus.APPROVE;
                        p0833 = WorkPlanConstant.TaskChangedStatus.Normal;
                    }
                }
                String P0831 = "";
                if (oldp0800.indexOf(prior_vo.getString("p0831")) == -1) {//p0831不再oldp0800中（是顶级任务）
                    oldp0800.add(prior_p0800); // 上期p0800

                    if (thisTreeTableBo.taskNameIsRepeated(parent_id, "", taskName)) {
                        // throw new GeneralException("已存在同名任务,不能保存");
                        continue;
                    }
                    else {
                        P0831 = "".equals(parent_id) ? id_p08 : parent_id; // 父任务号
                    }
                }
                else{//不是顶级任务
                    P0831 = (String) newp0800.get(oldp0800.indexOf(prior_vo.getString("p0831"))); // 父任务号
                    //把当前上期任务的p0800存入oldp0800用于下次做判断
                    oldp0800.add(prior_vo.getString("p0800"));
                }
                StringBuffer insertsql = new StringBuffer("insert into p08(");
                StringBuffer selectsql = new StringBuffer("select ");
                ArrayList fielditemlist = DataDictionary.getFieldList("P08", Constant.USED_FIELD_SET);// 获取数据字典中的字段
                for (int j = 0; j < fielditemlist.size(); j++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(j);
                    if ("p0700,p0811,p0833,p0800,p0831".indexOf(fielditem.getItemid()) == -1) {
                        //okr复制任务优化，注释isCopyInfo参数
//							if("0".equals(isCopyInfo)){//不复制完成进度、进度说明 chent
//								if("p0835".equalsIgnoreCase(fielditem.getItemid()) || "p0837".equalsIgnoreCase(fielditem.getItemid())){//完成进度、进度说明
//									continue ;
//								}
//							}
                        insertsql.append(fielditem.getItemid() + ",");
                        selectsql.append(fielditem.getItemid() + ",");
                    }
                }
                selectsql.append(this.P0700 + " as p0700,'" + p0811 + "' as p0811," + p0833 + " as p0833," + id_p08 + " as p0800," + P0831 + " as p0831");
                selectsql.append(" from p08 where p0800 = '" + prior_p0800 + "'");
                insertsql.append("p0700,p0811,p0833,p0800,p0831) ");
                insertsql.append(selectsql);

                dao.insert(insertsql.toString(), new ArrayList());
                new WorkPlanOperationLogBo(conn, userView).addLog(Integer.parseInt(id_p08), this.userView.getUserFullName()+"创建了任务(复制上期)");
                /***************** 任务保存完毕 ****************/
                // 清除当前任务一条线上的所有权重
                if (rank != null && rank.floatValue() != 0) {
                    taskBo.clearBranchRank(Integer.parseInt(this.P0700), Integer.parseInt(id_p08));
                    String clearIDs = taskBo.getClearRankTaskIds(Integer.parseInt(this.P0700), Integer.parseInt(id_p08));
                    valuemap.put("clearIDs", clearIDs);
                }

                /** ################################ P09表: 负责人################################ */
                RecordVo director_p09 = new RecordVo("p09");
                // haosl 无条件取p09的数据
                LazyDynaBean priorDirector = getDirectorByP09(iPriorP0800); // 上期任务的负责人
                // linbz isCreater标识是校验是否是创建人，而p09则不需考虑
                String nbase = priorDirector!=null ? (String) priorDirector.get("nbase") : this.nBase;
                nbase = WorkPlanUtil.initcap(nbase); // nbase为首字母大写的3个字母组成的单词

                String a0100 = priorDirector!=null ? (String)priorDirector.get("a0100") : this.A0100;

                String b0110 = "";//单位
                String e0122 = "";//部门
                String e01a1 = "";//岗位
                String a0101 = "";//姓名
                if(priorDirector != null) {
                    b0110 = (String) priorDirector.get("p0907");
                    e0122 = (String) priorDirector.get("p0909");
                    e01a1 = (String) priorDirector.get("p0911");
                    a0101 = (String) priorDirector.get("p0913");
                }else {
                    nbase = this.nBase;
                    a0100 = this.A0100;
                    b0110 = this.userView.getUserOrgId();
                    e0122 = this.userView.getUserDeptId();;
                    e01a1 = this.userView.getUserPosId();
                    a0101 = this.userView.getUserFullName();
                }

                String id_p09 = idg.getId("P09.P0900");
                director_p09.setInt("p0900", Integer.parseInt(id_p09));
                director_p09.setInt("p0901", 2);
                director_p09.setInt("p0903", Integer.parseInt(id_p08));
                director_p09.setString("nbase", nbase);
                director_p09.setString("a0100", a0100);
                director_p09.setInt("p0905", 1);
                director_p09.setString("p0907", b0110);
                director_p09.setString("p0909", e0122);
                director_p09.setString("p0911", e01a1);
                director_p09.setString("p0913", a0101);

                dao.addValueObject(director_p09);

                /** ################################ per_task_map表: 创建人################################ */
                RecordVo builder_map = new RecordVo("per_task_map");
                String id_builder_map = idg.getId("per_task_map.id");
                builder_map.setInt("id", Integer.parseInt(id_builder_map));
                builder_map.setInt("p0800", Integer.parseInt(id_p08));
                builder_map.setInt("flag", 5);
                builder_map.setObject("rank", rank == null ? rank : Float.valueOf(rank.floatValue()));
                builder_map.setInt("p0700", Integer.parseInt(this.P0700));
                builder_map.setDate("create_time", new Date());
                builder_map.setString("create_user", userView.getUserName());
                builder_map.setString("create_fullname", userView.getUserFullName());
                if ("2".equals(this.P0723)) { // 团队计划
                    builder_map.setInt("seq", thisTreeTableBo.getSeq(this.objectId, Integer.parseInt(P0831), 2));
                    builder_map.setString("org_id", this.objectId);
                }
                else {
                    builder_map.setInt("seq", thisTreeTableBo.getSeq(userView.getDbname() + userView.getA0100(), Integer.parseInt(P0831), 1));
                    builder_map.setString("nbase", this.nBase);
                    builder_map.setString("a0100", this.A0100);
                }

                dao.addValueObject(builder_map);

                // 部门计划：需要判断当前部门负责是否有个人计划
                initPlan(Integer.parseInt(this.P0700));
                /** ################################ per_task_map表:* 负责人################################*/
                // 团队的任务或者个人任务且负责人不是本人，则新建一条负责人记录
                if ("2".equals(this.P0723)) {
                    addPlan(this.getP07_vo(), this.userView.getDbname() + this.userView.getA0100());
                    RecordVo director_map = new RecordVo("per_task_map");
                    String id_director_map = idg.getId("per_task_map.id");
                    director_map.setInt("id", Integer.parseInt(id_director_map));
                    director_map.setInt("p0800", Integer.parseInt(id_p08));
                    director_map.setInt("p0700", Integer.parseInt(this.P0700));
                    director_map.setInt("seq", thisTreeTableBo.getSeq(nbase + a0100, Integer.parseInt(P0831), 1));
                    director_map.setDate("create_time", new Date());
                    director_map.setString("create_user", userView.getUserName());
                    director_map.setString("create_fullname", userView.getUserFullName());
                    director_map.setInt("flag", 1);
                    director_map.setString("nbase", nbase);
                    director_map.setString("a0100", a0100);
                    director_map.setDouble("rank", 0.0);
                    dao.addValueObject(director_map);
                }
                if ("1".equals(this.P0723) && !isDirector) {// 个人计划且本人不是上期负责人时，添加一条负责人记录 chent 20170912
                    addPlan(this.getP07_vo(), nbase+a0100);
                    RecordVo director_map = new RecordVo("per_task_map");
                    String id_director_map = idg.getId("per_task_map.id");
                    director_map.setInt("id", Integer.parseInt(id_director_map));
                    director_map.setInt("p0800", Integer.parseInt(id_p08));
                    director_map.setInt("p0700", Integer.parseInt(this.P0700));
                    director_map.setInt("seq", thisTreeTableBo.getSeq(nbase + a0100, Integer.parseInt(P0831), 1));
                    director_map.setDate("create_time", new Date());
                    director_map.setString("create_user", userView.getUserName());
                    director_map.setString("create_fullname", userView.getUserFullName());
                    director_map.setInt("flag", 1);
                    director_map.setString("nbase", nbase);
                    director_map.setString("a0100", a0100);
                    director_map.setDouble("rank", 0.0);
                    dao.addValueObject(director_map);
                }

                if (isCreater || isDirector) { // 复制上期任务的参与人
                    /** ################################ per_task_map表: 参与人 ################################*/
                    List memberMap = taskBo.getMemberFromPTMap(iPriorP0800);
                    for (int n = 0, len = memberMap.size(); n < len; n++) {
                        RecordVo vo = (RecordVo) memberMap.get(n);
                        String _nbase = vo.getString("nbase");
                        String _a0100 = vo.getString("a0100");
                        //暂时注掉此处代码 无条件添加任务成员  haosl  delte 2018-3-1
//							if(changeMap!=null && !changeMap.containsKey(_nbase+_a0100)) {
                            String id = idg.getId("per_task_map.id");
                            vo.setInt("id", Integer.parseInt(id));
                            vo.setInt("p0700", Integer.parseInt(P0700));
                            vo.setInt("p0800", Integer.parseInt(id_p08));
                            vo.setInt("seq", thisTreeTableBo.getSeq(_nbase + _a0100, Integer.parseInt(P0831), 1));
                            vo.setDate("create_time", new Date());
                            vo.setString("create_user", userView.getUserName());
                            vo.setString("create_fullname", userView.getUserFullName());

                            dao.addValueObject(vo);
//							}
                    }

                    /** ################################ p09表: 参与人 ################################*/
                    List memberP09 = taskBo.getMemberFromP09(iPriorP0800);
                    for (int n = 0, len = memberP09.size(); n < len; n++) {
                        RecordVo vo = (RecordVo) memberP09.get(n);
                        //暂时注掉此处代码 无条件添加任务成员  haosl  delte 2018-3-1
//							if(changeMap!=null && !changeMap.containsKey(vo.getString("nbase")+vo.getString("a0100"))) {
                            vo.setInt("p0900", Integer.parseInt(idg.getId("P09.P0900")));
                            vo.setInt("p0903", Integer.parseInt(id_p08));
                            dao.addValueObject(vo);
//							}
                    }

                    /** ################################ 协办任务：协作任务表 p10表 ################################ */
                    WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
                    if(util.isOpenCooperationTask() && util.isCooperationTask(Integer.parseInt(id_p08), true)){//启用协作任务并且是协作任务
                        // 通过任务号初始化协办表。发布协办任务：直接审批,审批状态（P1019）直接为已批（02）
                        newCooperationTask(Integer.parseInt(id_p08), true);
                    }
                }
            } // 上级不重复

			b = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return b;
	}

    /**
     * @Title: getpriorP0700
     * @Description: 获得上期的计划号
     * @param @param task_ids
     * @param @return
     * @return int
     * @author:wangrd
     * @throws
    */
    public int getpriorP0700(String task_ids) {
        int p0700=0;
        try {
            String[] arrids = task_ids.split(",");
            PlanTaskBo taskBo= new PlanTaskBo(this.conn,this.userView);
            for (int i = 0; i < arrids.length; i++) {
                String prior_p0800 = arrids[i];
                if ("".equals(prior_p0800)) {
                    continue;
                }
                //开始复制 找上级节点 重名不复制。通过上期的层级关系 在本期找
                RecordVo prior_vo= taskBo.getTask(Integer.parseInt(prior_p0800));
                p0700= prior_vo.getInt("p0700");
                break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return p0700;
    }


    /**
     * @Title: getParentP0800
     * @Description: 获取需要复制此任务的上级任务
     * @param @param cur_tasklist  本期任务
     * @param @param prior_tasklist 上期任务
     * @param @param prior_p0800 上期任务
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public int getParentP0800( ArrayList cur_tasklist, ArrayList prior_tasklist,String prior_p0800) {
        int p0800=0;
        try {
            DynaBean dynaBean=null;
            ArrayList priorParentP0801s= new ArrayList();
            ArrayList curParentP0801s= new ArrayList();
            setParentP0801s(prior_tasklist,prior_p0800,priorParentP0801s);
            String _prior_parent_p0800="";
            String _prior_parent_p0801="";
            for(int i=0;i<priorParentP0801s.size();i++)
            {
                dynaBean=(DynaBean)priorParentP0801s.get(i);
                _prior_parent_p0800=(String)dynaBean.get("p0800");
                _prior_parent_p0801=(String)dynaBean.get("p0801");
                if (_prior_parent_p0801.length()>0){//存在上级 则按上级名称在本期查找
                    for(int j=0;j<cur_tasklist.size();j++)
                    {
                        dynaBean=(DynaBean)cur_tasklist.get(j);
                        String _p0801=(String)dynaBean.get("p0801");
                        if (_p0801.equals(_prior_parent_p0801)){//在本期查找到 可能有多个
                            curParentP0801s.add(dynaBean);
                        }
                    }
                    //
                    if (curParentP0801s.size()>1){//有多个重名的
                        //check 上级名称是否一样
                        dynaBean=(DynaBean)curParentP0801s.get(0);
                        p0800 = Integer.parseInt((String)dynaBean.get("p0800"));

                    }
                    else if (curParentP0801s.size()==1){
                        dynaBean=(DynaBean)curParentP0801s.get(0);
                        p0800 = Integer.parseInt((String)dynaBean.get("p0800"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p0800;
    }



    /**
     * @Title: setParentP0801s
     * @Description: 获取上级所有任务
     * @param @param prior_tasklist
     * @param @param prior_p0800
     * @param @return
     * @return int
     * @author:wangrd
     * @throws
    */
    public void setParentP0801s(ArrayList prior_tasklist,String prior_p0800,ArrayList priorParentP0801s ) {
        try {
            DynaBean dynaBean=null;
            String _prior_parent_p0800="";
            String _prior_parent_p0801="";
            for(int i=0;i<prior_tasklist.size();i++)
            {
                dynaBean=(DynaBean)prior_tasklist.get(i);
                String _p0800=(String)dynaBean.get("p0800");
                if (_p0800.equals(prior_p0800)){
                    _prior_parent_p0800=(String)dynaBean.get("p0800");
                    if (_prior_parent_p0800.equals(_p0800)){
                        _prior_parent_p0800="";
                    }
                    break;
                }
            }
            if (_prior_parent_p0800.length()>0){//上期有上级任务 但不一定是自己的任务
                for(int i=0;i<prior_tasklist.size();i++)
                {
                    dynaBean=(DynaBean)prior_tasklist.get(i);
                    String _p0800=(String)dynaBean.get("p0800");
                    if (_p0800.equals(_prior_parent_p0800)){//是自己的任务
                        _prior_parent_p0801=(String)dynaBean.get("p0801");
                        priorParentP0801s.add(dynaBean);
                        setParentP0801s(prior_tasklist,_p0800,priorParentP0801s);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * @Title: addTask
     * @Description:  新增任务
     * @param @param valuemap
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    /*
    public boolean addTask1(HashMap valuemap) {
        boolean b=false ;
        if(this.P0700==null || this.P0700.length()<1){
            addPlan();
            loadPlanInfo();
            String planscope=(String)valuemap.get("plan_scope");
            if (planscope!=null && planscope.length()>0){
                if (!WorkPlanConstant.Scope.SUPERIOR.equals(planscope)){
                    updatePlanScope("",planscope);
                }
            }
        }
        if(this.P0700==null || this.P0700.length()<1){
            return b;
        }


        String task_name=(String)valuemap.get("task_name");
        String task_desc=(String)valuemap.get("task_desc");
        String task_cyr=(String)valuemap.get("task_cyr");//负责人
        String task_rank=(String)valuemap.get("task_rank");
        String task_startdate=(String)valuemap.get("task_startdate");
        String task_enddate=(String)valuemap.get("task_enddate");
        String parent_id=(String)valuemap.get("task_parentid");
        String task_seq=(String)valuemap.get("task_seq");
        try{
            if (task_startdate.length()>0){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                Date startdate =dateFormat.parse(task_startdate);
                task_startdate= dateFormat.format(startdate);
            }
        }
        catch(Exception e){
            task_startdate="";
        }
        try{
            if (task_enddate.length()>0){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                Date enddate =dateFormat.parse(task_enddate);
                task_enddate= dateFormat.format(enddate);
            }
        }
        catch(Exception e){
            task_enddate="";
        }
        double rank=0;
        try{
            rank=PubFunc.parseDouble(task_rank);
            rank=rank/100;
        }
        catch(Exception e){
        }
        PlanTaskTreeTableBo taskTreeBo=new PlanTaskTreeTableBo(this.conn,Integer.parseInt(this.P0700));
        try{
            //检查任务是否重名
            if (taskTreeBo.taskNameIsRepeated(parent_id, "",task_name)){
                this.returnInfo="name_repeated";
                return false;
            }
            //增加任务
            RecordVo vo=new RecordVo("P08");
            IDGenerator idg=new IDGenerator(2,this.conn);
            String task_id=idg.getId("P08.P0800");
            vo.setString("p0800",task_id);
            vo.setString("p0700",this.P0700);
            vo.setString("p0801",task_name);
            vo.setString("p0809","1");
            if (String.valueOf( WorkPlanConstant.PlanApproveStatus.HandIn).equals(this.planSatus)){//报批
                //报批 新增任务 待批准、新增状态
                vo.setString("p0811","02");
                vo.setInt("p0833",WorkPlanConstant.TaskChangedStatus.add);
            }
            else if (String.valueOf( WorkPlanConstant.PlanApproveStatus.Pass).equals(this.planSatus)){//批准
                //任务状态 起草、新增状态
                vo.setString("p0811","01");
                vo.setInt("p0833",WorkPlanConstant.TaskChangedStatus.add);
            }
            else if (String.valueOf( WorkPlanConstant.PlanApproveStatus.Reject).equals(this.planSatus)){//退回
                //任务状态 起草、新增状态
                vo.setString("p0811","01");
                vo.setInt("p0833",WorkPlanConstant.TaskChangedStatus.add);
            }
            else {//起草状态
              //任务状态 起草、未变更
                vo.setString("p0811","01");
                vo.setInt("p0833",WorkPlanConstant.TaskChangedStatus.Normal);
            }
            if (task_startdate.length()>0)
                vo.setString("p0813",task_startdate);
            if (task_enddate.length()>0)
                vo.setString("p0815",task_enddate);
            if (parent_id==null || parent_id.length()<1){
                parent_id=task_id;
            }
            vo.setString("p0831",parent_id);//父类id
            vo.setDate("create_time",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
            vo.setString("create_user",this.userView.getUserName());
            vo.setString("create_fullname",this.userView.getUserFullName());
            dao.addValueObject(vo);

            String nbase= this.nBase;
            String a0100=this.A0100;
            if (task_cyr!=null & task_cyr.length()>0){//选择了负责人
                String[] arr=task_cyr.split(",");
                for (int i=0;i<arr.length;i++){
                    String userid=arr[i];
                    if ("".equals(userid)) continue;
                    userid=WorkPlanUtil.decryption(userid);
                    if (userid!=null && userid.length()>3){
                        nbase =userid.substring(0, 3);
                        a0100 =userid.substring(3, userid.length());
                    }
                }
            }
            int parent_p0800=0;
            if (!parent_id.equals(task_id)){
                parent_p0800= Integer.parseInt(parent_id);
            }
            //成员表增加负责人
            HashMap personMap= new HashMap();
            personMap.clear();
            personMap.put("p0901", "2");//任务
            personMap.put("p0903", task_id);//任务id
            personMap.put("p0905", WorkPlanConstant.MemberType.LEADER);//成员标识
            personMap.put("nbase", nbase);
            personMap.put("a0100", a0100);
            addTaskPerson(personMap,false);

            //映射表增加负责人，个人计划：负责人不是创建人 映射表需增加负责人，部门计划：必须增加一条负责人记录
            if ( (
                    "1".equals(this.P0723)&&(!(nbase.equals(this.nBase)&&a0100.equals(this.A0100)))
                 )
                 ||("2".equals(this.P0723)))
            {
                personMap.put("p0700", this.P0700);
                personMap.put("p0800", task_id);//任务id
                personMap.put("flag", WorkPlanConstant.MemberType.LEADER);//成员标识
                personMap.put("nbase", nbase);
                personMap.put("a0100", a0100);
                personMap.put("rank", rank+"");
                int _seq=taskTreeBo.getSeq(nbase+a0100, parent_p0800,1);
                personMap.put("seq", _seq+"");
                addTaskPersonMap(personMap);
                addPlan(this.p07_vo,nbase+a0100);
            }


            //映射表增加创建人
            personMap.clear();
            personMap.put("p0700", this.P0700);
            personMap.put("p0800", task_id);
            personMap.put("flag", WorkPlanConstant.MemberType.CREATOR);//成员标识
            if ("1".equals(this.P0723)){
                personMap.put("nbase", this.nBase);
                personMap.put("a0100", this.A0100);
            }
            else  if ("2".equals(this.P0723)){
                personMap.put("org_id", this.objectId);
                personMap.put("nbase", "");
                personMap.put("a0100", "");
            }
            personMap.put("rank", rank+"");
            int seq=0;
            if ("2".equals(this.P0723)){
                seq=taskTreeBo.getSeq(this.objectId, parent_p0800,2);
            }
            else {
                seq=taskTreeBo.getSeq(this.nBase+this.A0100, parent_p0800,1);
            }
            personMap.put("seq", seq+"");
            addTaskPersonMap(personMap);
            if("2".equals(this.p07_vo.getString("p0723"))){//判断自己有没有计划
                addPlan(this.p07_vo,this.userView.getDbname()+this.userView.getA0100());
            }
            String _json =taskTreeBo.getRowJson(task_id,task_seq,taskTreeBo.getP07_vo().getInt("p0723"));
            this.returnInfo =_json;
            b=true;
            if ("2".equals(this.planSatus)){//发布任务 发邮件
                Map params = new HashMap();
                params.put("p0700", this.P0700);
                params.put("p0723", this.P0723);
                params.put("objectid", this.objectId);
                params.put("p0800", task_id);
                PlanTaskBo taskBo =  new PlanTaskBo(this.conn,this.userView);
                taskBo.sendEmailsToAll(params, "set");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
      return b;
    }
    */
    /**
     * @Title: addTaskPerson
     * @Description: 增加成员   参与人 负责人
     * @param @param valuemap
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean addTaskPerson(HashMap valuemap,boolean bcheck_existed) {
       boolean b=false ;
       String p0901 =(String)valuemap.get("p0901");//1、计划|项目       2、任务      3、工作总结
       String p0903 =(String)valuemap.get("p0903");
       String p0905 =(String)valuemap.get("p0905");//成员标识 、1 负责人    2、参与人(协办人)  3、关注人
       String nbase =(String)valuemap.get("nbase");
       String a0100 =(String)valuemap.get("a0100");
       if (p0901 ==null || p0901.length()<1 ||p0903 ==null || p0903.length()<1
              || p0905 ==null || p0905.length()<1 || nbase ==null || nbase.length()<1) {
           return b;
       }
       //增加成员
       try{
           if (bcheck_existed){
               String strsql ="select count(*) cnt from p09 where p0901="+p0901
                   +" and p0903= "+ p0903 +" and p0905="+p0905
                   +" and nbase= '"+ nbase +"' and a0100='"+a0100+"'";
               RowSet rset=dao.search(strsql);
               if (rset.next()){
                   int cnt=rset.getInt("cnt");
                   if (cnt>0){
                       return b;
                   }
               }
           }
           RecordVo vo=new RecordVo("P09");
           IDGenerator idg=new IDGenerator(2,this.conn);
           String id=idg.getId("P09.P0900");
           vo.setString("p0900",id);
           vo.setString("p0901",p0901);
           vo.setString("p0903",p0903);
           vo.setString("p0905",p0905);
           vo.setString("nbase",nbase);
           vo.setString("a0100",a0100);
           String strsql="select * from "+nbase+"A01 where a0100='"+a0100+"'";
           RowSet rset=dao.search(strsql);
           if (rset.next()){
               vo.setString("p0907",rset.getString("b0110"));
               vo.setString("p0909",rset.getString("e0122"));
               vo.setString("p0911",rset.getString("e01a1"));
               vo.setString("p0913",rset.getString("a0101"));
           }

           dao.addValueObject(vo);
           b=true;

        }
        catch(Exception e){
            e.printStackTrace();
        }
       return b;
    }

    /**
     * @Title: addTaskPersonMap
     * @Description: 增加映射表成员
     * @param @param valuemap
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean addTaskPersonMap(HashMap valuemap) {
        boolean b=false ;
        String org_id =(String)valuemap.get("org_id");
        String p0700 =(String)valuemap.get("p0700");
        String p0800 =(String)valuemap.get("p0800");//
        String nbase =(String)valuemap.get("nbase");
        String a0100 =(String)valuemap.get("a0100");
        String flag =(String)valuemap.get("flag");
        String rank =(String)valuemap.get("rank");
        String seq =(String)valuemap.get("seq");
        if (seq==null || seq.length()<1){seq="1";}
        if (p0700 ==null || p0700.length()<1 ||p0800 ==null || p0800.length()<1
               || flag ==null || flag.length()<1 ) {
            return b;
        }

        //增加成员映射
        try{
            RecordVo vo=new RecordVo("per_task_map");
            IDGenerator idg=new IDGenerator(2,this.conn);
            String id=idg.getId("per_task_map.id");
            vo.setString("id",id);
            vo.setString("p0700",p0700);
            vo.setString("p0800",p0800);
            if (org_id!=null && org_id.length()>0){
                vo.setString("org_id",org_id);
            }
            if (nbase!=null && nbase.length()>0){
                vo.setString("nbase",nbase);
            }

            if (a0100!=null && a0100.length()>0){
                vo.setString("a0100",a0100);
            }
            vo.setString("flag",flag);
            vo.setString("rank",rank);
            vo.setString("seq",seq);
            vo.setDate("create_time",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
            vo.setString("create_user",this.userView.getUserName());
            vo.setString("create_fullname",this.userView.getUserFullName());

            dao.addValueObject(vo);
            b=true;
         }
         catch(Exception e){
             e.printStackTrace();
         }
        return b;
    }


    /**
     * @Title: updatePlanScope
     * @Description: 更改计划可见范围
     * @param @param scope
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean updatePlanScope(String p0700,String scope) {
       boolean b=false ;
       if (p0700==null || p0700.length()<1){
           p0700=this.P0700;
       }
       if (p0700==null || p0700.length()<1) {
           return b;
       }
       if (scope==null || scope.length()<1) {
           return b;
       }
        try{
            String strsql="update p07 set p0721="+scope+" where p0700="+p0700;
            dao.update(strsql);
         }
         catch(Exception e){
             e.printStackTrace();
         }
        return b;
     }

    /**
     * @Title: updatePlanStatus
     * @Description: 更改计划状态 发布 审批
     * @param @param status 1:发布 2：批准 3：退回
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean updatePlanStatus(String p0700,String status) {
        boolean b=false ;
        WorkPlanUtil planUtil = new WorkPlanUtil(conn, userView);
        if (p0700==null || p0700.length()<1){
            p0700=this.P0700;
        }
        if (p0700==null || p0700.length()<1) {
            return b;
        }
        try{
            String strsql="update p07 set p0719="+status+" where p0700="+p0700;
            dao.update(strsql);
            if ("1".equals(status)){
                //任务也发布
                strsql="update p08 set p0811='02' where p0700="+p0700;
                dao.update(strsql);
                //发送邮件给任务成员、计划关注人
                //sendEmailOnUpdatePlanStatus(p0700,status);
                sendEmailOnUpdatePlanStatusNew(p0700,status);
            }
            else if ("2".equals(status)){
                //任务也批准
                //批准未取消的
                strsql="update p08 set p0811='03',p0833="
                    +WorkPlanConstant.TaskChangedStatus.Normal+" where p0811='02' and p0700="+p0700
                    +" and "+Sql_switcher.sqlNull("p0833", 0)+"<>"+WorkPlanConstant.TaskChangedStatus.Cancel;
                dao.update(strsql);
                //批准取消的
                strsql="update p08 set p0811='03' ,p0809='"
                    +WorkPlanConstant.TaskExecuteStatus.CANCEL+"'  where p0811='02' and p0700="+p0700
                    +" and "+Sql_switcher.sqlNull("p0833", 0)+"="+WorkPlanConstant.TaskChangedStatus.Cancel;
                dao.update(strsql);

                RecordVo p07Vo = getP07Vo(Integer.parseInt(p0700));
                // 更新目标卡 chent 20171130 add start
                String p0723 = p07Vo.getString("p0723");
    			String periodType = p07Vo.getString("p0725");
    			String periodYear = p07Vo.getString("p0727");
    			String periodMonth = p07Vo.getString("p0729");
    			String periodWeek = p07Vo.getString("p0731");
    			String objectid = "";
    			String objectid_ = "";
    			if ("1".equals(p0723)) {
    				objectid = p07Vo.getString("nbase") + p07Vo.getString("a0100");
                    objectid_ = p07Vo.getString("a0100");
    			} else {
    				objectid = p07Vo.getString("p0707");
                    objectid_ = p07Vo.getString("p0707");
    			}
                String[] nbasea0100 = {objectid};
    			String relate_plan = p07Vo.getString("relate_planid");
    			//检查是否已有评分记录，如果有的话就不更新了
    			if(StringUtils.isNotBlank(relate_plan) && !checkIsScoring(relate_plan,objectid_)) {
                    WorkPlanHrBo WorkPlanHrBo = new WorkPlanHrBo(this.conn, this.userView);
                    WorkPlanHrBo.batchUpdateTargetCard(nbasea0100, false, p0723, periodType, periodYear, periodMonth, periodWeek);
                }
                // 更新目标卡 chent 20171130 add end

                // 有上级岗位,发送邮件给计划人
                if (planUtil.isHaveDirectSuper(objectid,p0723)){
                	sendEmailOnUpdatePlanStatus(p0700,status);
                }

                //批准后 更新待办信息
                workPlanUtil.updatePending_approvePlan(p0700);


                //更新【变更标识】为未变更 chent 20160415
                String sql="update p07 set changeflag=0 where p0700="+p0700;
                dao.update(sql);

                // 协办任务：计划审批后，把计划下的协办任务推送给协办人上级 chent 20160607
                this.releaseCooperationTasks(p0700);
            }
            else if ("3".equals(status)){//退回
                //任务 起草
                strsql="update p08 set p0811='01',p0833="
                    +WorkPlanConstant.TaskChangedStatus.Normal+" where p0700="+p0700;
                dao.update(strsql);
                workPlanUtil.updatePending_approvePlan(p0700);
                //不发邮件给任务参与者。
              //发送邮件给计划人
                sendEmailOnUpdatePlanStatus(p0700,status);
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }
    /**
     * 检查考核对象是否已经开始评分
     * @return
     */
    private boolean checkIsScoring(String plan_id,String objectid) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        boolean isScored = false;
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select distinct(object_id) from per_mainbody where Plan_id=? and Status in (1,2) and object_id=?");//正在打分|已提交打分
            List<String> values = new ArrayList<String>();
            values.add(plan_id);
            values.add(objectid);
            rs = dao.search(sql.toString(),values);
            if(rs.next()){
                isScored = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return isScored;
    }

    /**
     * @Title: updatePlanStatusDirector
     * @Description: Boss/无上级岗位的人更改计划状态 发布 审批
     * @param @param status
     * @param @return
     * @return boolean
     * @author:wusy
     * @throws
    */
    public boolean updatePlanStatusDirector(String p0700,String status) {
        boolean b=false ;
        if (StringUtils.isBlank(p0700)){
        	return b;
        }

        try{
        	RecordVo p07Vo = new RecordVo("p07");
        	p07Vo.setInt("p0700", Integer.parseInt(p0700));
        	p07Vo = dao.findByPrimaryKey(p07Vo);
        	int p0719 = p07Vo.getInt("p0719");
        	if(p0719 != 2){
        		return b;
        	}
            String strsql="update p07 set p0719= 2 where p0700="+p0700;
            dao.update(strsql);
            //任务也发布
            strsql="update p08 set p0811='03',p0833="
                +WorkPlanConstant.TaskChangedStatus.Normal+" where  p0700="+p0700
                +" and "+Sql_switcher.sqlNull("p0833", 0)+"<>"+WorkPlanConstant.TaskChangedStatus.Cancel;
            dao.update(strsql);

            //发送邮件给任务成员、计划关注人
           // sendEmailOnUpdatePlanStatus(p0700,status);

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }


    /**
     * @Title: getSubTasks
     * @Description: 得到所有的下级任务列表(包括自己)，以逗号分隔
     * @param @param cur_plan_id 判断任务及下级任务是否是自己建的任务
     * @param @param task_id
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getSubTasks(String cur_plan_id,String task_id) {
    	RowSet rset;
        String task_ids="";
        try{
            String strsql="select * from  p08 where p0831="+task_id+" or  p0800="+task_id;
            rset=dao.search(strsql);
            while (rset.next()){
            	String p0800 =rset.getString("p0800");
				String p0700 =rset.getString("p0700");
				String p0811 =rset.getString("p0811");
                if(p0800.equals(task_id)){
                  continue;
                }
                String ids=getSubTasks(cur_plan_id,p0800);
                if (ids.length()>0){
                    if(task_ids.length()>0) {
                        task_ids=task_ids+ ","+ids;
                    } else {
                        task_ids=ids;
                    }
                }
            }
            if(task_ids.length()>0) {
                task_ids=task_ids+","+task_id;
            } else {
                task_ids=task_id;
            }
      }
        catch(Exception e){
            e.printStackTrace();
        }
        return task_ids;
    }
    /**
     * @Title: getSubTasksWithOutSelf
     * @Description: 得到当前的下级任务列表（不包括自己），以逗号分隔
     * @param @param cur_plan_id 判断任务及下级任务是否是自己建的任务
     * @param @param task_id
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getSubTasksWithOutSelf(String cur_plan_id,String task_id) {
        String task_ids="";
        try{
        	RowSet rset;
        	String strsql="";
            strsql="select * from  p08 where p0831="+task_id;
            rset=dao.search(strsql);
            while (rset.next()){
                String p0800 =rset.getString("p0800");
                if(p0800.equals(task_id)){
                  continue;
                }
                String id=rset.getInt("p0800")+"";
                task_ids=task_ids+","+id;

            }

      }
        catch(Exception e){
            e.printStackTrace();
        }
        return task_ids;
    }
    /**
     * @param objectid
     * @param p0723
     * @Title: CheckSubTasks
     * @Description: 判断当前任务是否有下级任务，当前任务可以有多个
     * @param @param cur_plan_id 判断任务及下级任务是否是自己建的任务
     * @param @param task_id
     * @param @return
     * @return boolean
     * @author:
     * @throws
    */
    public boolean CheckSubTask(String cur_plan_id,String ids, String p0723, String objectid){
		boolean flag = false;
		WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
		this.returnInfo="";//是否是其他人建的任务
		String[] task_id = ids.split(",");
		RowSet rset = null;
		for (int i = 0; i <task_id.length; i++) {
			try {
				 StringBuffer strsql = new StringBuffer("");
				if(StringUtils.isBlank(this.returnInfo)){
					String[] idArray = task_id[i].split("_");
					String id = idArray[0];
					String othertask = idArray[1];
					strsql.append("select * from  p08 where p0800="+id);//查当前任务
			        rset = dao.search(strsql.toString());
			        String curUser = this.userView.getUserName();//当前登录人
			        String create_user_p08 = "";
			        String p0801 = "";
			        if(rset.next()) {
			        	create_user_p08 = rset.getString("create_user");//当前任务的创建者
			        	p0801 = rset.getString("p0801");//当前任务名称
			        }

			        strsql.setLength(0);
					//本人的任务
					boolean taskInMyPlan = false;
					strsql.append("select nbase, a0100, p0707 from p07 where p0700 = (select p0700 from  p08 where p0800=" + id+")");
					rset=dao.search(strsql.toString());
					if(rset.next()) {
						if("1".equals(p0723)){
							String planNbase = rset.getString("nbase");
							String planA0100 = rset.getString("a0100");
							if(objectid.equals(planNbase+planA0100)){
								taskInMyPlan = true;
							}
						}else if("2".equals(p0723)){
							String planP0707 = rset.getString("p0707");
							if(objectid.equals(planP0707)){
								taskInMyPlan = true;
							}
						}else{
							this.returnInfo = "参数出错";
						}
					}
					String userName = "";
					PlanTaskBo ptBo = new PlanTaskBo(conn, userView);
					HashMap params = new HashMap();
					params.put("p0700", cur_plan_id);
					params.put("p0800", id);
					params.put("objectid", objectid);
					params.put("p0723", p0723);
					int superiorEdit = ptBo.isSuperiorEdit(params);
					if(taskInMyPlan){
						this.returnInfo = "";
					}else if(superiorEdit>=1 && superiorEdit<=4){
						this.returnInfo = "";
					}else if(!(superiorEdit>=1 && superiorEdit<=4)){
						this.returnInfo = "您没有权限删除任务：" + p0801 + "！";
					}

					String subTaskIds = this.getSubTasks("", id);//查当前任务和他的子任务的id
					strsql.setLength(0);
					strsql.append("select * from  p08 where p0800 in("+subTaskIds+")");//查当前任务和他的子任务
			        rset = dao.search(strsql.toString());
					while (rset.next()) {
							String p0800 =rset.getString("p0800");
							String p0700 =rset.getString("p0700");
							String p0811 =rset.getString("p0811");
							String p0801s = rset.getString("p0801");//当前任务名称
							if ("03".equals(p0811)){
			                    this.returnInfo="任务（" + p0801s + "）已批准，不能删除！";
			                    break;
			                }
			                if(p0800.equals(id)){//是当前节点
			                    continue;
			                }
			                flag=true;
				  }

				  if("1".equals(othertask)){//是穿透任务
					  if(create_user_p08.equalsIgnoreCase(curUser)){//当前任务的创建者与当前计划的创建者相同，可删除当前
						  this.returnInfo="";
					  }
				  }
				}else {
                    break;
                }
			} catch (SQLException e1) {
				e1.printStackTrace();
			}finally{
				PubFunc.closeResource(rset);
			}
		}
		return flag;




    }


    /**
     * @Title: delTask
     * @Description:   删除任务
     * @param @param task_ids 多个任务以逗号分隔
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean delTask(String plan_id,String task_ids, String deltype) {
    	boolean b=false ;
    	PlanTaskTreeTableBo taskTreeBo=new PlanTaskTreeTableBo(this.conn,Integer.parseInt(plan_id));
        String[]  task_id=task_ids.split(",");
			if("withChild".equals(deltype)){
				 String ids="";
				//获取下级节点
		        for (int i=0;i<task_id.length;i++){
		            String id =task_id[i];
		            String[] idArray = id.split("_");
					id = idArray[0];
		            if (id.length()<1) {
                        continue;
                    }
		            if ((","+ids+",").indexOf(","+id+",")>-1) {
                        continue; //已有 就不用查下级了
                    }
		            ids=ids+","+getSubTasks(plan_id,id);

		        }
		        if (ids.length()==0) {
                    return b;
                }
		        //删除
		        task_id=ids.split(",");
		        try{
		            for (int i=0;i<task_id.length;i++){
		                delTask(task_id[i]);
		            }
		            b=true;
		        }
		        catch(Exception e){
		            e.printStackTrace();
		        }
			}
			else if("withoutChild".equals(deltype)){
				for (int k = 0; k < task_id.length; k++) {
					String p0800 =task_id[k];
					String[] p0800Array = p0800.split("_");
					p0800 = p0800Array[0];
					String ids = getSubTasksWithOutSelf(plan_id, p0800);// 获得子任务列表组合成一个字符串
					if (ids.length() == 0) {
						delTask(plan_id, p0800,"withChild");
						continue;
					}
					try {
						// 查询出当前任务的p0831字段:用于判断当前任务是否为一级任务
						String sql = "select p0831 from p08 where p0800 = "
								+ p0800;
						RowSet rs = dao.search(sql);
						int p0831=0;
						if (rs.next()){
							p0831 = rs.getInt(1);
						}
						else {
							return b;
						}

						String[] arrids = ids.split(",");

						// 将子任务升级为父任务：当前任务是一级任务
						if (p0831 == Integer.parseInt(p0800)) {
							p0831=0;
						}
						taskTreeBo.resetChildTaskSeq(Integer
								.parseInt(p0800),p0831);

						// 删除父任务
						delTask(p0800);
						//处理子任务
						for (int i = 1; i <arrids.length; i++) {
							if (p0831!=0){
								sql = "update p08 set p0831=" + p0831
								+ " where p0800=" + arrids[i];
							}
							else {

								sql = "update p08 set p0831=" + arrids[i]
								                                       + " where p0800=" + arrids[i];
							}
							dao.update(sql);
						}


						b = true;

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}//删除多个大循环
			}
		return b;
	}


    public void delTask(String p0800) {
         try{
        	String sql="";
            if (p0800.length()<1) {
                return ;
            }
            // 删除映射表
            sql="delete from per_task_map where p0800 = "+p0800;
            dao.update(sql);
            // 删除成员表
            sql="delete from P09  where p0901 =2 and p0903="+p0800;
            dao.update(sql);
            // 删除任务
            sql="delete from p08 where p0800 ="+p0800;
            dao.update(sql);

        }
        catch(Exception e){
            e.printStackTrace();
        }

	}

    /**
     * @Title: getDropdownPersonJson
     * @Description:    返回待选人记录
     * @param @param keyword 关键字
     * @param @param excludeIds 排除的人
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getDropdownPersonJson(String keyword,String excludeIds) {
        String info="";
        if (!"".equals(keyword)){
            try{
                ArrayList list = (ArrayList)workPlanUtil.getCandidateByKeyword(keyword,excludeIds);
                for (int i=0;i<list.size();i++){
                    LazyDynaBean bean = (LazyDynaBean)list.get(i);
                    String id=(String)bean.get("id");
                    String name=(String)bean.get("name");
                    String photo=(String)bean.get("photo");
                    String unit=(String)bean.get("unit");
                    String email="";
                    if (bean.get("email")!=null) {
                        email=(String)bean.get("email");
                    }

                    String stritem="{"
                        +quotedDoubleValue("id")+":"+quotedDoubleValue(id)
                        +","
                        +quotedDoubleValue("name")+":"+quotedDoubleValue(name)
                        +","
                        +quotedDoubleValue("photo")+":"+quotedDoubleValue(photo)
                        +","
                        +quotedDoubleValue("unit")+":"+quotedDoubleValue(unit)
                        +","
                        +quotedDoubleValue("email")+":"+quotedDoubleValue(email)

                        +"}"
                        ;
                    if ("".equals(info)){
                        info = stritem;
                    }
                    else {
                        info =info+","+ stritem;
                    }

                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        info=quotedDoubleValue("person_list")+":["+info+"]";

        return info;
    }



    /**
     * @Title: getDropdwonFollower
     * @Description:  返回待选关注人
     * @param @param plan_id
     * @param @param keyword
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getDropdwonFollower(String plan_id,String keyword) {
        String info="";
        if (!"".equals(keyword)){
            try{
                //排除自己
                String excludeIds=this.userView.getDbname()+this.userView.getA0100();

                //排除我已经关注的
                String strsql="select * from  p09 where p0901=1 and p0903="+plan_id;
                RowSet rset=dao.search(strsql);
                while (rset.next()){
                    String nbase =rset.getString("nbase");
                    String a0100 =rset.getString("a0100");
                    excludeIds=excludeIds+","+nbase+a0100;
                }
                //排除我的上级
                this.p07_vo=getP07Vo(Integer.parseInt(plan_id));
                if ("1".equals(this.p07_vo.getString("p0723"))){
                    String objectid=this.p07_vo.getString("nbase")+this.p07_vo.getString("a0100");
                    excludeIds=excludeIds+","+
                        workPlanUtil.getMyAllSuperPerson(objectid,"1");
                  //如果是上级指定关注人
                    excludeIds=excludeIds+","+objectid;

                }
                else if ("2".equals(this.p07_vo.getString("p0723"))){
                    String objectid=this.p07_vo.getString("p0707");
                    excludeIds=excludeIds+","+
                        workPlanUtil.getMyAllSuperPerson(objectid,"2");
                  //如果是上级指定关注人
                    excludeIds=excludeIds+","+workPlanUtil.getFirstDeptLeaders(objectid);
                }
                info= getDropdownPersonJson(keyword,excludeIds);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return info;
    }

    /**
     * @Title: getParticipantList
     * @Description: 返回参与人信息
     * @param @param _nbase
     * @param @param _a0100
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getParticipantList(String _nbase,String _a0100) {
        String info="";
        String strsql="select * from "+_nbase+"A01 where a0100='"+_a0100+"'";
        try{
            RowSet rset=dao.search(strsql);
            if (rset.next()){
                String stritem="{"
                    +quotedDoubleValue("id")+":"+quotedDoubleValue(WorkPlanUtil.encryption(_nbase+_a0100))
                    +","
                    +quotedDoubleValue("name")+":"+quotedDoubleValue( rset.getString("a0101"))
                    +"}"
                    ;
                if ("".equals(info)){
                    info = stritem;
                }
                else {
                    info =info+","+ stritem;

                }

            }
            info=quotedDoubleValue("participant")+":["+info+"]";

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return info;

    }
    /**
     * @Title: getEmailBean
     * @Description: 返回邮件信息   bean格式。
     * @param @param objectId
     * @param @param sub_ject
     * @param @param bodyText
     * @param @param href
     * @param @return
     * @return LazyDynaBean
     * @author:wangrd
     * @throws
    */
    public LazyDynaBean getEmailBean(String objectId ,String subject,
            String bodyText,String href)  {
        LazyDynaBean emailBean=getEmailBean(objectId, subject, bodyText, href,"查看");
        return emailBean;
    }

    public LazyDynaBean getEmailBean(String objectId ,String subject,
            String bodyText,String href,String hrefDesc)  {
        LazyDynaBean emailBean = new LazyDynaBean();
        emailBean.set("objectId", objectId);
        emailBean.set("subject", subject);
        emailBean.set("bodyText", bodyText);
        emailBean.set("href", href);
        emailBean.set("hrefDesc", hrefDesc);

        return emailBean;
    }

    public String getParamValue(HashMap paramMap,String paramName)  {
        String value="";
        if (paramMap.get(paramName)!=null){
           value=(String)paramMap.get(paramName);
        }
        return value;
  }

    /**
     * @Title: getRemindEmail_PlanHref
     * @Description:制定、查看计划的url
     * @param @param logonNbase 登陆人库
     * @param @param logonA0100 登陆人id
     * boolean bDesignPlan true： 制定计划 false：查看计划
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getRemindEmail_PlanHref(String logonNbase,String logonA0100,boolean bDesignPlan)  {
        HashMap palnParamMap= new HashMap();
        palnParamMap.put("p0723", this.P0723);
        palnParamMap.put("periodtype", this.periodType);
        palnParamMap.put("periodyear", this.periodYear);
        palnParamMap.put("periodmonth", this.periodMonth);
        palnParamMap.put("periodweek", this.periodWeek);
        palnParamMap.put("objectId", this.objectId);
        palnParamMap.put("dept_leaderid", this.deptLeaderId);

        return getRemindEmail_PlanHref(palnParamMap,logonNbase,logonA0100,bDesignPlan);
    }


    /**
     * @Title: getRemindSubEmail_PlanHref
     * @Description:制定、查看计划的url
     * @param @param logonNbase 登陆人库
     * @param @param logonA0100 登陆人id
     * boolean bDesignPlan true： 制定计划 false：查看计划
     * @param @return
     * @return String
     * @author:wusy
     * @throws
    */
    public String getRemindSubEmail_PlanHref(String logonNbase,String logonA0100, String objectid, String deptLeaderId, String p0723, String p0725, String year, String month, String week, boolean bDesignPlan)  {
        HashMap palnParamMap= new HashMap();
        palnParamMap.put("p0723", p0723);
        palnParamMap.put("periodtype", p0725);
        palnParamMap.put("periodyear", year);
        palnParamMap.put("periodmonth", month);
        palnParamMap.put("periodweek", week);
        palnParamMap.put("objectId", objectid);
        palnParamMap.put("dept_leaderid", deptLeaderId);

        return getRemindEmail_PlanHref(palnParamMap,logonNbase,logonA0100,bDesignPlan);
    }


    public String getRemindEmail_PlanHref(HashMap planParam,String logonNbase,String logonA0100,boolean bDesignPlan)  {
        String url = "";
        LazyDynaBean abean=workPlanUtil.getUserNamePassword(logonNbase , logonA0100);
        if(abean!=null && abean.get("username")!=null)
        {
            String periodtype =getParamValue(planParam,"periodtype");
            String periodyear =getParamValue(planParam,"periodyear");
            String periodmonth =getParamValue(planParam,"periodmonth");
            String periodweek =getParamValue(planParam,"periodweek");
            String p0723 =getParamValue(planParam,"p0723");
            String objectid =getParamValue(planParam,"objectId");
            String dept_leaderid =getParamValue(planParam,"dept_leaderid");

            String username=(String)abean.get("username");
            String pwd=(String)abean.get("password");
            url = this.userView.getServerurl()
            +"/workplan/work_plan.do?br_query=link"
            +"&p0723="+WorkPlanUtil.encryption(p0723)
            +"&objectid="+WorkPlanUtil.encryption(objectid)
            +"&periodtype="+periodtype
            +"&periodyear="+periodyear
            +"&periodmonth="+periodmonth
            +"&fromflag=email"
            +"&periodweek="+periodweek
            +"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(
                    PubFunc.convertTo64Base(username+","+pwd))
                    +"";
            if("2".equals(this.P0723)){
                url=url+"&deptleader=" +WorkPlanUtil.encryption(dept_leaderid);
            }
            if (!bDesignPlan){
                url=url+"&needcheck=true";   //别人取消关注后 不能再查看
            }
        }
        else {
            url = this.userView.getServerurl();
        }
        return url;

    }

    /**
    * @Title: getPalnParamMap
    * @Description: 获取当前类中所初始计划的计划属性
    * @param @return
    * @return HashMap
    */
    public HashMap getPalnParamMap()  {
        HashMap palnParamMap= new HashMap();
        palnParamMap.put("p0723", this.P0723);
        palnParamMap.put("periodtype", this.periodType);
        palnParamMap.put("periodyear", this.periodYear);
        palnParamMap.put("periodmonth", this.periodMonth);
        palnParamMap.put("periodweek", this.periodWeek);
        palnParamMap.put("objectId", this.objectId);
        palnParamMap.put("dept_leaderid", this.deptLeaderId);
        palnParamMap.put("planType", this.planType);
        return palnParamMap;
    }

    /**
    * @Title: getPendingPlanUrl
    * @Description: 获取待办使用的url 参照getRemindEmail_PlanHref
    * @param @param logonNbase
    * @param @param logonA0100
    * @param @return
    * @return String
    */
    public String getPendingPlanUrl()  {
        HashMap palnParamMap= getPalnParamMap();
        return getPendingPlanUrl(palnParamMap);
    }


    public String getPendingPlanUrl(HashMap planParam)  {
        String url = "";
        String periodtype =getParamValue(planParam,"periodtype");
        String periodyear =getParamValue(planParam,"periodyear");
        String periodmonth =getParamValue(planParam,"periodmonth");
        String periodweek =getParamValue(planParam,"periodweek");
        String p0723 =getParamValue(planParam,"p0723");
        String objectid =getParamValue(planParam,"objectId");
        String dept_leaderid =getParamValue(planParam,"dept_leaderid");
        String planType =getParamValue(planParam,"planType");

        url = ""
        +"/workplan/work_plan.do?br_query=link"
        +"&p0723="+WorkPlanUtil.encryption(p0723)
        +"&objectid="+WorkPlanUtil.encryption(objectid)
        +"&periodtype="+periodtype
        +"&periodyear="+periodyear
        +"&periodmonth="+periodmonth
        +"&type="+planType
        +"&periodweek="+periodweek;
        if("2".equals(p0723)){
            url=url+"&deptleader=" +WorkPlanUtil.encryption(dept_leaderid);
        }

        return url;

    }

    /**
     * @Title: getRemindEmail_PlanHref
     * @Description:制定查看计划url
     * @param @param logonUser 登录人
     * @param @param viewer 被查看人的计划
     * @param @param p0800 任务编号
     * @author:wangrd
     * @throws
    */
	public String getRemindEmail_TaskHref(String logonUser, RecordVo viewer, int p0800) {
		StringBuffer url = new StringBuffer(this.userView.getServerurl());
		LazyDynaBean abean = workPlanUtil.getUserNamePassword(logonUser.substring(0, 3), logonUser.substring(3));

		String username = (String) abean.get("username");
		if (abean != null && username != null && username.length() > 0) {
			String pwd = (String) abean.get("password");

			String objectid = null;
			if (viewer.getInt("p0723") == 1) {
				objectid = viewer.getString("nbase") + viewer.getString("a0100");

			} else {
				objectid = viewer.getString("p0707");
			}

			String p0700 = viewer.getString("p0700");
			String p0723 = viewer.getString("p0723");
			if(StringUtils.isEmpty(p0700) || StringUtils.isEmpty(p0723)){
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				try{
					rs = dao.search("select p07.p0700,p0723,nbase,a0100,p0707 from p08,p07 where p07.p0700=p08.p0700 and p0800="+p0800+" order by p07.p0700 ASC");
					if(rs.next()){
						p0700 = rs.getString("p0700");
						p0723 = rs.getString("p0723");
						if(StringUtils.isEmpty(objectid)){
							if ("1".equals(viewer.getInt("p0723"))) {
								objectid = rs.getString("nbase") + rs.getString("a0100");
							} else {
								objectid = rs.getString("p0707");
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					PubFunc.closeDbObj(rs);
				}
			}

			//从邮箱入口进入任务页面时需要在url上连接一个返回计划页面的returnurl
			String returnurl="/workplan/work_plan.do?br_query=link"
				+"&p0700="+WorkPlanUtil.encryption(p0700)
	            +"&p0723="+WorkPlanUtil.encryption(p0723+"")
	            +"&objectid="+WorkPlanUtil.encryption(objectid);

			url.append("/workplan/plan_task.do?br_task=link")
					.append("&p0700=").append(WorkPlanUtil.encryption(p0700))
					.append("&p0800=").append(WorkPlanUtil.encryption(p0800 + ""))
					.append("&objectid=").append(WorkPlanUtil.encryption(objectid))
					.append("&p0723=").append(WorkPlanUtil.encryption(p0723));

			String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username + "," + pwd));
			url.append("&appfwd=1&etoken=").append(etoken);
			url.append("&returnurl=").append(com.hrms.frame.codec.SafeCode.encode(returnurl));
		}
		return url.toString();
	}

    /**
     * @Title: getPeoplePlanVo
     * @Description:  获取某人当前期间的计划
     * @param @param p07vo
     * @param @param objectid 人员编号
     * @param @return
     * @return RecordVo
     * @author:wangrd
     * @throws
    */
    public RecordVo getPeoplePlanVo(RecordVo p07vo,String objectid) {
        RecordVo vo = new RecordVo("p07");
        try{
            if (p07vo==null) {
                return vo;
            }
            if (objectid==null||objectid.length()!=11) {
                return vo;
            }

            String a0100 =objectid.substring(3);
            //获取计划id
            String period_type=p07vo.getString("p0725");
            String period_year=p07vo.getString("p0727");
            String period_month=p07vo.getString("p0729");
            String period_week=p07vo.getString("p0731");
            String  nbase =objectid.substring(0, 3);
            int plan_id=getPeoplePlanId(nbase,a0100,
                    period_type,period_year,period_month,period_week);
            //如没有计划 则增加计划
            if (plan_id<1){
                addPlan(p07vo,objectid);
                plan_id=getPeoplePlanId(nbase,a0100,
                        period_type,period_year,period_month,period_week);
            }
            if (plan_id>0){
                try {
                    vo.setInt("p0700",plan_id);
                    ContentDAO dao = new ContentDAO(this.conn);
                    vo = dao.findByPrimaryKey(vo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return vo;
    }


    /**
     * @Title: sendEmailToFollower
     * @Description:  给关注人发邮件
     * @param @param plan_id
     * @param @param nbase
     * @param @param a0100
     * @param @param bAdd true:新增关注人 false:删除关注人
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean sendEmailToFollower(String plan_id,String nbase,String a0100,boolean bAdd) {
        boolean b=false;
        if ("".equals(plan_id)) {
            plan_id="0";
        }
        try{
            this.p07_vo= getP07Vo(Integer.parseInt(plan_id));
            if (this.p07_vo!=null){
                //计划发布、批准后才发通知
                if (this.p07_vo.getInt("p0719")==1 || this.p07_vo.getInt("p0719")==2) {
                    LazyDynaBean emailBean= getEmailToFollowerBeanInfo(this.p07_vo,nbase,a0100,bAdd);
                    if (emailBean!=null){
                        AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userView);
                        emailBo.send(emailBean);
                        //发送微信
                        workPlanUtil.sendWeixinMessageFromEmail(emailBean);
                    }
                }
            }
            b=true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;

    }
    /**
     * @Title: getEmailToFollowerBeanInfo
     * @Description:  返回 给关注人发送的email信息
     * @param @param p07Vo
     * @param @param nbase
     * @param @param a0100
     * @param @param bAdd true:新增关注人 false:删除关注人
     * @param @return
     * @return LazyDynaBean
     * @author:wangrd
     * @throws
    */
    public LazyDynaBean getEmailToFollowerBeanInfo(RecordVo p07Vo,String nbase,String a0100,boolean bAdd) {
        LazyDynaBean emailBean=null;
        if (p07Vo==null) {
            return emailBean;
        }

        try{
            this.periodType=p07Vo.getString("p0725");
            this.periodYear=p07Vo.getString("p0727");
            this.periodMonth=p07Vo.getString("p0729");
            this.periodWeek=p07Vo.getString("p0731");
            this.P0723=p07Vo.getString("p0723");

            if ("1".equals(this.P0723)){
                this.objectId=p07Vo.getString("nbase")+p07Vo.getString("a0100");
            }
            else {
                this.objectId=p07Vo.getString("p0707");
                //部门负责人
                this.deptLeaderId=this.userView.getDbname()+this.userView.getA0100();
            }

            String plan_period=workPlanUtil.getPlanPeriodDesc(this.periodType,this.periodYear,
                    this.periodMonth,this.periodWeek);
            String  plan_title=plan_period;
            if (!isMyPlan()&& ("1".equals(this.P0723)) ){
                plan_title=plan_title+"("+workPlanUtil.getUsrA0101(p07Vo.getString("nbase")
                        ,p07Vo.getString("a0100"))+")";
            }
            else {
            }
            plan_title=plan_title+"个人工作计划";
            if ("2".equals(this.P0723)){
                plan_title=plan_period+workPlanUtil.getOrgDesc(p07Vo.getString("p0707"))+"部门工作计划";
            }
            //通知关注人
            String subject="工作计划提醒";

            String cur_date=PubFunc.getStringDate("yyyy年MM月dd日");
            StringBuffer bodyText= new StringBuffer();
            String a0101=workPlanUtil.getUsrA0101(nbase,a0100);
            bodyText.setLength(0);
            bodyText.append(a0101).append(", 您好").append("<br />");
            bodyText.append(getHtmlBlank(4));
            bodyText.append("    ");
            bodyText.append(this.userView.getUserFullName());
            if (bAdd){
                bodyText.append("将您指定为  "+plan_title +" 的关注人");
                bodyText.append("，该计划的进展会及时通知您。");
                subject=this.userView.getUserFullName()+"发布了"+plan_title+",请查阅";
            }
            else {
                bodyText.append("将您从计划  "+plan_title +" 的关注人名单中移除。");
                subject=this.userView.getUserFullName()+"取消了您关注他的"+plan_title;
            }
            bodyText.append("<br />");
            bodyText.append("<br />");
            bodyText.append(cur_date);

            if (!bAdd){//删除关注 定位自己的计划
                this.objectId=nbase+a0100;
                this.P0723="1";
            }
            String href = getRemindEmail_PlanHref(nbase,a0100,!bAdd);
            emailBean = getEmailBean(nbase+a0100, subject, bodyText.toString(), href,"去查看计划");
            emailBean.set("bodySubject", "工作计划提醒");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return emailBean;

    }
    /**
     * @Title: sendEmailOnUpdatePlanStatus
     * @Description:  发布计划后 给负责人 参与人 关注人发送邮件
     * @param @param plan_id
     * @param @param planStatus
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public boolean sendEmailOnUpdatePlanStatus(String plan_id,String planStatus) {
        boolean b=false;
        this.p07_vo=getP07Vo(Integer.parseInt(plan_id));
        this.periodType=this.p07_vo.getString("p0725");
        this.periodYear=this.p07_vo.getString("p0727");
        this.periodMonth=this.p07_vo.getString("p0729");
        this.periodWeek=this.p07_vo.getString("p0731");
        this.P0723=this.p07_vo.getString("p0723");
        String nbase="";
        String a0100="";
        if ("1".equals(this.P0723)){
            nbase = this.p07_vo.getString("nbase");
            a0100 = this.p07_vo.getString("a0100");
            this.objectId=this.p07_vo.getString("nbase")+this.p07_vo.getString("a0100");
        }
        else {
            this.objectId=this.p07_vo.getString("p0707");
            //部门负责人
            this.deptLeaderId=workPlanUtil.getFirstDeptLeaders(this.objectId);

            if (this.deptLeaderId != null && !"".equals(this.deptLeaderId)) {
                nbase = this.deptLeaderId.substring(0, 3);
                a0100 = this.deptLeaderId.substring(3);
            }
        }

        String strsql="";
        ArrayList list= new ArrayList();
        try{
            if ("1".equals(planStatus)){//发布
                Map params = new HashMap();
                params.put("p0700", plan_id);
                params.put("p0723", this.P0723);
                params.put("objectid", this.objectId);
                PlanTaskBo taskBo =  new PlanTaskBo(this.conn,this.userView);
                // PlanTaskBo taskBo = new PlanTaskBo(this.conn,this.userView);
                //负责人 参与人 任务关注人
                strsql="select * from p08 where p0700 ="+plan_id;
                RowSet rset=dao.search(strsql);
                while (rset.next()){
                    String p0800 =rset.getString("p0800");
                    params.put("p0800", p0800);

                    taskBo.sendEmailsToAll(params, "set");
                }
                //计划关注人
                strsql="select * from p09 where P0901 =1 and P0905 =3 and p0903 ="+plan_id;
                rset=dao.search(strsql);
                while (rset.next()){
                    String _nbase=rset.getString("nbase");
                    String _a0100=rset.getString("a0100");
                    LazyDynaBean emailBean = getEmailToFollowerBeanInfo(this.p07_vo,_nbase,_a0100,true);
                    list.add(emailBean);
                }
            }
            else if ("2".equals(planStatus)||"3".equals(planStatus)){//批准  退回
                String plan_title=workPlanUtil.getPlanPeriodDesc(this.periodType,this.periodYear,
                        this.periodMonth,this.periodWeek)+"工作计划";
                String a0101 = workPlanUtil.getUsrA0101(nbase, a0100);
                String plan_Owner="您的";
                if ("2".equals(this.P0723)){
                    plan_Owner = "    您负责的"+workPlanUtil.getOrgDesc(this.objectId)+"的";
                }
                String bodyText=this.getApproveEmail_BodyText(a0101,plan_Owner,plan_title,
                        "2".equals(planStatus));
                String href = getRemindEmail_PlanHref(nbase,a0100,true);
                String subject="    " + this.userView.getUserFullName()+"批准了"+plan_Owner+plan_title;
                if ("3".equals(planStatus)){
                	subject="    " + this.userView.getUserFullName()+"退回了"+plan_Owner+plan_title+",请重新填写";
                }
                String strApprove=("3".equals(planStatus))?"退回":"审批";
                LazyDynaBean emailBean = getEmailBean(nbase+a0100, subject, bodyText, href,"去查看计划");
                emailBean.set("bodySubject", "工作计划"+strApprove +"提醒");
                list.add(emailBean);

                if("3".equals(planStatus)){//驳回时发送待办 chent 20160413
                    String pending_title="";
                    if ("1".equals(this.P0723)) {//个人计划
                    	pending_title="您的"+plan_title;
                    } else {//团队计划
                    	pending_title="您负责部门"+workPlanUtil.getOrgDesc(this.objectId)+"的"+plan_title;
                    }
                    pending_title=pending_title+"(驳回)";
                    String pending_url=getPendingPlanUrl();
                    LazyDynaBean pendingBean = new  LazyDynaBean();
                    pendingBean.set("pending_url", pending_url);
                    pendingBean.set("pending_title", pending_title);
                    String receiver = workPlanUtil.getUserNameByA0100(nbase, a0100);
                    workPlanUtil.sendPending_BackPlan(this.userView.getUserName(),
                    		receiver,  pendingBean, this.P0700);
                }
            }

            if ("1".equals(planStatus)){//发布了 也要给上级发邮件
                String plan_title=workPlanUtil.getPlanPeriodDesc(this.periodType,this.periodYear,
                        this.periodMonth,this.periodWeek)+"工作计划";
                String superid=workPlanUtil.getMyApprovedSuperPerson(nbase,a0100);
                String superNbase="";
                String superA0100="";
                String superA0101="";
                if (superid!=null && !"".equals(superid)){
                    superNbase =superid.substring(0, 3);
                    superA0100 =superid.substring(3);
                    superA0101=workPlanUtil.getUsrA0101(superNbase, superA0100);
                }
                if (superA0100.length()>0){
                    String plan_Owner="";
                    if ("2".equals(this.P0723)){
                        plan_Owner = workPlanUtil.getOrgDesc(this.objectId)+"的";
                    }
                    String subject= "    " + this.userView.getUserFullName()+"发布了"+plan_Owner+plan_title+",请批准";
                    String bodyText=this.getPublishEmail_BodyText(superA0101,plan_Owner,plan_title);
                    String href = getRemindEmail_PlanHref(superNbase,superA0100,true);
                    //System.out.println(href);
                    LazyDynaBean emailBean = getEmailBean(superNbase+superA0100,subject, bodyText, href,"去查看计划");
                    emailBean.set("bodySubject", "工作计划审批提醒");
                    list.add(emailBean);
                }

            }

            AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userView);
            emailBo.send(list);
            //发送微信
            workPlanUtil.sendWeixinMessageFromEmail(list);
            b=true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;

    }

    /**
     * @Title: sendEmailOnUpdatePlanStatusNew
     * @Description:  发布计划后 给负责人 参与人 关注人发送邮件  (合并邮件 wusy)
     * @param @param plan_id
     * @param @param planStatus
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public boolean sendEmailOnUpdatePlanStatusNew(String plan_id,String planStatus) {
        boolean b=false;
        this.p07_vo=getP07Vo(Integer.parseInt(plan_id));
        this.periodType=this.p07_vo.getString("p0725");
        this.periodYear=this.p07_vo.getString("p0727");
        this.periodMonth=this.p07_vo.getString("p0729");
        this.periodWeek=this.p07_vo.getString("p0731");
        this.P0723=this.p07_vo.getString("p0723");
        String nbase="";
        String a0100="";
        if ("1".equals(this.P0723)){
            nbase = this.p07_vo.getString("nbase");
            a0100 = this.p07_vo.getString("a0100");
            this.objectId=this.p07_vo.getString("nbase")+this.p07_vo.getString("a0100");
        }
        else {
            this.objectId=this.p07_vo.getString("p0707");
            //部门负责人
            this.deptLeaderId=workPlanUtil.getFirstDeptLeaders(this.objectId);

            if (this.deptLeaderId != null && !"".equals(this.deptLeaderId)) {
                nbase = this.deptLeaderId.substring(0, 3);
                a0100 = this.deptLeaderId.substring(3);
            }
        }

        String strsql="";
        ArrayList list= new ArrayList();
        try{
            if ("1".equals(planStatus)){//发布
                Map params = new HashMap();
                params.put("p0700", plan_id);
                params.put("p0723", this.P0723);
                params.put("objectid", this.objectId);
                PlanTaskBo taskBo =  new PlanTaskBo(this.conn,this.userView);
                //------邮件合并
                StringBuffer sbfsql = new StringBuffer();
                //查询出该计划下的负责人,任务成员,及关注人
                sbfsql.append("select Nbase, A0100 from P09 where P0903 in (select p0800 from P08 where P0700 = ?) and P0901 = 2 group by Nbase, A0100");
                RowSet rs = null;
                List memberList = new ArrayList();//接收该人负责/参与的任务id
                List focusList = new ArrayList();//接收该人关注的任务id
                rs = dao.search(sbfsql.toString(), Arrays.asList(new Object[]{plan_id}));
                //根据人遍历
                while(rs.next()){
                	if ((rs.getString("nbase") + rs.getString("a0100")).equals(userView.getDbname() + userView.getA0100())) { // 当前操作人无需邮件通知
    					continue;
    				}
                	//找出该计划下某人对应的任务id(p0903),角色(负责人,成员,关注)
                	String sql = "select P0903, p0905, nbase, a0100  from P09 where Nbase = ? and A0100 = ? and P0901 = 2 and  P0903 in (select p0800 from P08 where P0700 = ?)";
                	RowSet rst = null;
                	rst = dao.search(sql, Arrays.asList(new Object[]{rs.getString("nbase"), rs.getString("a0100"), plan_id}));
                	while(rst.next()){
                		//将该人员是负责人和成员角色的任务id放入集合
                		if((rst.getInt("p0905") == 1) || (rst.getInt("p0905") == 2)){
                			if(!workPlanUtil.isOpenCooperationTask() || !workPlanUtil.isCooperationTask(rst.getInt("p0903"), true)){//【没有启用协作任务】 或 【不是协作任务】 时才给负责人发送通知
                				memberList.add(rst.getInt("p0903"));
                			}
                		}else{
                			//将该人员是关注人角色的任务id放入集合
                			focusList.add(rst.getInt("p0903"));
                		}
                	}
                	if(memberList.size() > 1){
                		params.put("p0800s", memberList);
                		params.put("flag", "member");//任务负责人\成员标识
                		params.put("nbase", rs.getString("nbase"));
                		params.put("a0100", rs.getString("a0100"));
                		taskBo.sendEmailsToAllMulti(params, "set");
                	}else if(memberList.size() == 1){
                		params.put("p0800", memberList.get(0) + "");
                		params.put("flag", "member");//任务负责人\成员标识
                		params.put("nbase", rs.getString("nbase"));
                		params.put("a0100", rs.getString("a0100"));
                        taskBo.sendEmailsToAllSingle(params, "set");
                	}
                	if(focusList.size() > 1){
                		params.put("p0800s", focusList);
                		params.put("flag", "focus");//任务关注人标识
                		params.put("nbase", rs.getString("nbase"));
                		params.put("a0100", rs.getString("a0100"));
                		taskBo.sendEmailsToAllMulti(params, "set");
                	}else if(focusList.size() == 1){
                		params.put("p0800", focusList.get(0) + "");
                		params.put("flag", "focus");//任务关注人标识
                		params.put("nbase", rs.getString("nbase"));
                		params.put("a0100", rs.getString("a0100"));
                        taskBo.sendEmailsToAllSingle(params, "set");
                	}
                	memberList.removeAll(memberList);
                	focusList.removeAll(focusList);
                }

                //负责人 参与人 任务关注人
//                strsql="select * from p08 where p0700 ="+plan_id;
//                RowSet rset=dao.search(strsql);
//                while (rset.next()){
//                    String p0800 =rset.getString("p0800");
//                    params.put("p0800", p0800);
//
//                    taskBo.sendEmailsToAll(params, "set");
//                }
                //计划关注人
                strsql="select * from p09 where P0901 =1 and P0905 =3 and p0903 ="+plan_id;
                RowSet rset=dao.search(strsql);
                while (rset.next()){
                    String _nbase=rset.getString("nbase");
                    String _a0100=rset.getString("a0100");
                    LazyDynaBean emailBean = getEmailToFollowerBeanInfo(this.p07_vo,_nbase,_a0100,true);
                    list.add(emailBean);
                }
            }
            else if ("2".equals(planStatus)||"3".equals(planStatus)){//批准  退回
                String plan_title=workPlanUtil.getPlanPeriodDesc(this.periodType,this.periodYear,
                        this.periodMonth,this.periodWeek)+"工作计划";
                String a0101 = workPlanUtil.getUsrA0101(nbase, a0100);
                String plan_Owner="您的";
                if ("2".equals(this.P0723)){
                    plan_Owner = "    您负责的"+workPlanUtil.getOrgDesc(this.objectId)+"的";
                }
                String bodyText=this.getApproveEmail_BodyText(a0101,plan_Owner,plan_title,
                        "2".equals(planStatus));
                String href = getRemindEmail_PlanHref(nbase,a0100,true);
                String subject="    " + this.userView.getUserFullName()+"批准了"+plan_Owner+plan_title;
                if ("3".equals(planStatus)){
                	subject="    " + this.userView.getUserFullName()+"退回了"+plan_Owner+plan_title+",请重新填写";
                }
                String strApprove=("3".equals(planStatus))?"退回":"审批";
                LazyDynaBean emailBean = getEmailBean(nbase+a0100, subject, bodyText, href,"去查看计划");
                emailBean.set("bodySubject", "工作计划"+strApprove +"提醒");
                list.add(emailBean);
            }

            //发布了 也要给上级发邮件
            if ("1".equals(planStatus)){
                String plan_title=workPlanUtil.getPlanPeriodDesc(this.periodType,this.periodYear,
                        this.periodMonth,this.periodWeek)+"工作计划";

                // 审批人（p0733）如果有指定则发给指定的审批人，否则发给上级  chent 20171117
                String superid = this.p07_vo.getString("p0733");
                if(StringUtils.isEmpty(superid)) {
                    if ("1".equals(this.P0723)){//个人计划
                        superid=workPlanUtil.getMyApprovedSuperPerson(nbase,a0100);
                    }
                    else {//部门计划
                        //考虑到部门计划的负责人，可能是别人兼职的，这样上报的时候应该报给兼职岗位的直接上级，而不是当前负责人的直接上级
                        String e01a1="";
                        e01a1 = workPlanUtil.getDeptLeaderE01a1(this.objectId);
                        superid = workPlanUtil.getMyApprovedSuperPerson(e01a1);
                    }
                }
                String superNbase="";
                String superA0100="";
                String superA0101="";
                if (superid!=null && !"".equals(superid)){
                    superNbase =superid.substring(0, 3);
                    superA0100 =superid.substring(3);
                    superA0101=workPlanUtil.getUsrA0101(superNbase, superA0100);
                }
                if (superA0100.length()>0){
                    String deptName="";
                    if ("2".equals(this.P0723)){
                    	deptName = workPlanUtil.getOrgDesc(this.objectId)+"的";
                    }
                    String subject= "    " + this.userView.getUserFullName()+"发布了"+deptName+plan_title+",请批准";
                    String bodyText=this.getPublishEmail_BodyText(superA0101,deptName,plan_title);
                    String href = getRemindEmail_PlanHref(superNbase,superA0100,true);
                    LazyDynaBean emailBean = getEmailBean(superNbase+superA0100,subject, bodyText, href,"去查看计划");
                    emailBean.set("bodySubject", "工作计划审批提醒");
                    list.add(emailBean);

                    //发送待办
                    String pending_title="";
                    if (deptName.length()==0){//个人计划
                    	pending_title=this.userView.getUserFullName()+"的"+plan_title;
                    }
                    else {
                    	pending_title=this.userView.getUserFullName()+"负责部门"+deptName+plan_title;//"的"+plan_title; haosl 2018-2-6
                    }
                    pending_title=pending_title+"(审批)";
                    String pending_url=getPendingPlanUrl();
                    LazyDynaBean pendingBean = new  LazyDynaBean();
                    pendingBean.set("pending_url", pending_url);
                    pendingBean.set("pending_title", pending_title);
                    String receiver =workPlanUtil.getUserNameByA0100(superNbase,superA0100);
                    // 审批待办 。清除老领导还没有审批的待办 chent 20170706
                    workPlanUtil.updatePending_approvePlan(this.P0700);
                    // 发送审批待办
                    workPlanUtil.sendPending_publishPlan(this.userView.getUserName(),
                    		receiver,  pendingBean,this.P0700);
                    // 更新提醒的待办为已办
                    workPlanUtil.updatePending_PublishPlan(this.userView.getUserName(), getPalnParamMap());
                    // 更新驳回的待办为已办 chent 20160413
                    workPlanUtil.updatePending_BackPlan(this.P0700);

                }


            }

            AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userView);
            emailBo.send(list);
            //发送微信
            workPlanUtil.sendWeixinMessageFromEmail(list);
            b=true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;

    }

    /**
     * 沟通消息提醒 wusy
     * @param userView
     * @param submitDate
     * @param content 	  沟通内容
     * @param object_Id   计划\任务\总结  id
     * @param type  类型: 1:计划  2:任务  3:总结
     *
     */
    public void sendDiscussionMsg(String type, String object_id, String content, String submitDate, UserView userView){

    	if(StringUtils.isBlank(type) || StringUtils.isBlank(content) || StringUtils.isBlank(object_id)){
    		return;
    	}
    	WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
    	ArrayList emailsList = new ArrayList();
    	ArrayList targetNameList = new ArrayList();
    	RowSet rs = null;
    	StringBuffer sbf = new StringBuffer();
    	//int identity;//身份   1.计划所有者  2.上级  3.任务所有者(负责人)  4.关注人(计划\任务)
    	/**
    	 * int identity 身份
    	 * type==1时(计划沟通) 角色: 1.计划所有者   2.上级   3.计划关注人
    	 * type==2时(任务沟通) 角色: 1.任务创建人   2.任务负责人 3.上级(所属计划人上级)  4.任务关注人  5.任务成员
    	 * type==3时(总结沟通) 角色: 1.总结创建人   2.上级    3.总结关注人
    	 */
    	if("1".equals(type)){
    		try {
    			int p0700 = Integer.parseInt(object_id);
    			RecordVo p07vo = new RecordVo("p07");
    			p07vo.setInt("p0700", p0700);
				p07vo = dao.findByPrimaryKey(p07vo);
				//计划所有者
				String planOwner = "";
				if("1".equals(p07vo.getString("p0723"))){
					planOwner = p07vo.getString("nbase") + p07vo.getString("a0100");
				}else{//p0723==2
					planOwner = wpUtil.getFirstDeptLeaders(p07vo.getString("p0707"));
				}
				//计划所有者上级
				String superior = wpUtil.getMyDirectSuperPerson(planOwner.substring(0, 3), planOwner.substring(3));
				//计划关注人
				List<String> focuserList = new ArrayList<String>();
				sbf.append("select nbase, a0100 from p09 where p0903 = ? and p0901 = 1 and p0905 = 3");

				rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0700}));
				while(rs.next()){
					String planFocuser = rs.getString("nbase") + rs.getString("a0100");
					focuserList.add(planFocuser);
				}
				//获取相关人员完成,对应角色发送不同的微信
				String plan_title = wpUtil.getPlanPeriodDesc(p07vo.getString("p0725"), p07vo.getString("p0727"), p07vo.getString("p0729"), p07vo.getString("p0731"));
				HashMap params = new HashMap();
				params.put("periodtype", p07vo.getString("p0725"));
				params.put("periodyear", p07vo.getString("p0727"));
				params.put("periodmonth", p07vo.getString("p0729"));
				params.put("periodweek", p07vo.getString("p0731"));
				params.put("p0723", p07vo.getString("p0723"));
				if("1".equals(p07vo.getString("p0723"))){
					params.put("objectId", planOwner);
				}else{
					params.put("objectId", p07vo.getString("p0707"));
					params.put("dept_leaderid", planOwner);
				}
				//给计划所有者发送沟通信息,如果该沟通信息是计划所有者发送的,则不发送
				if(StringUtils.isNotBlank(planOwner) && !((userView.getDbname()+userView.getA0100()).equals(planOwner))){
					String subject = "    " + userView.getUserFullName()+"在你的"+plan_title+"工作计划下发布了信息,请查阅";
					String bodyText = getPlanDiscussionTOwnerEmail_BodyText(wpUtil.getUsrA0101(planOwner.substring(0, 3), planOwner.substring(3)), content, plan_title);
					String href = getRemindEmail_PlanHref(params, planOwner.substring(0, 3), planOwner.substring(3), true);
					LazyDynaBean emailBean = getEmailBean(planOwner,subject, bodyText, href,"查看");
					emailBean.set("bodySubject", "工作计划沟通信息提醒");
					emailsList.add(emailBean);

				}
				//给上级和计划关注人发送沟通消息,需要排除当前登陆人
				//上级和计划关注人收到的邮件相同,将上级加入关注人的集合
				focuserList.add(superior);
				for(String usrId : focuserList){
					if(StringUtils.isNotBlank(usrId) && !usrId.equals(userView.getDbname()+userView.getA0100())){
						String subject = "";
						String bodyText = "";
						if(planOwner.equals(userView.getDbname()+userView.getA0100())){
							subject = "    " + userView.getUserFullName()+"在他/她的"+plan_title+"工作计划下发布了信息,请查阅";
							bodyText = getPlanDiscussionToFocuserEmail_BodyText(wpUtil.getUsrA0101(usrId.substring(0, 3), usrId.substring(3)), "他/她", content, plan_title);
						}else{
							subject = "    " + userView.getUserFullName()+"在"+wpUtil.getUsrA0101(planOwner.substring(0, 3), planOwner.substring(3))+"的"+plan_title+"工作计划下发布了信息,请查阅";
							bodyText = getPlanDiscussionToFocuserEmail_BodyText(wpUtil.getUsrA0101(usrId.substring(0, 3), usrId.substring(3)), wpUtil.getUsrA0101(planOwner.substring(0, 3), planOwner.substring(3)), content, plan_title);
						}
						String href = getRemindEmail_PlanHref(params, usrId.substring(0, 3), usrId.substring(3), true);
						LazyDynaBean emailBean = getEmailBean(usrId,subject, bodyText, href,"查看");
						emailBean.set("bodySubject", "工作计划沟通信息提醒");
						emailsList.add(emailBean);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else if("2".equals(type)){//任务沟通
    		try {
	    		int p0800 = Integer.parseInt(object_id);
	    		RecordVo p08vo = new RecordVo("p08");
	    		p08vo.setInt("p0800", p0800);
				p08vo = dao.findByPrimaryKey(p08vo);
				int p0700 = p08vo.getInt("p0700");
				RecordVo p07vo = new RecordVo("p07");
				p07vo.setInt("p0700", p0700);
				p07vo = dao.findByPrimaryKey(p07vo);
				//任务所有者(任务所在计划的创建人)
				String taskOwner = "";
				if("1".equals(p07vo.getString("p0723"))){
					taskOwner = p07vo.getString("nbase") + p07vo.getString("a0100");
				}else{
					taskOwner = wpUtil.getFirstDeptLeaders(p07vo.getString("p0707"));
				}
				String taskOwnerName = wpUtil.getUsrA0101(taskOwner.substring(0, 3), taskOwner.substring(3));
				//任务负责人
				String director = "";
				//任务成员
				List<String> taskMemberList = new ArrayList();
				//任务关注人
				List<String> taskFocuserList = new ArrayList();
				sbf.append("select nbase, a0100, p0905 from p09 where p0901 = 2 and p0903 = ?");
				rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0800}));
				while(rs.next()){
					if("1".equals(rs.getString("p0905"))){
						director = rs.getString("nbase") + rs.getString("a0100");
					}else if("2".equals(rs.getString("p0905"))){
						taskMemberList.add(rs.getString("nbase") + rs.getString("a0100"));
					}else if("3".equals(rs.getString("p0905"))){
						taskFocuserList.add(rs.getString("nbase") + rs.getString("a0100"));
					}
				}
				//上级(此处指任务所在计划所有者的上级,而不是任务负责人的上级)
				String superior = wpUtil.getMyDirectSuperPerson(director.substring(0, 3), director.substring(3));
				//上级和关注人是同一个人是，无需重复添加
				if(!taskFocuserList.contains(superior)) {
                    taskFocuserList.add(superior);
                }
				//获取任务相关人员完成,发通知信息(微信).
				String task_title = "\"" + p08vo.getString("p0801") + "\"任务";
				//按角色分为2中情况
				//1.任务创建人\任务负责人\任务成员
				//将上述人员放入一个集合
				if(!taskFocuserList.contains(taskOwner)) {
                    taskMemberList.add(taskOwner);
                }
				//创建人可能和负责人是同一个人,不用重复添加,同时如果上级/关注人的集合中已经存在了(解决 37142)，也不重复发送邮件了
				if(!taskMemberList.contains(director) && !taskFocuserList.contains(director)){
					taskMemberList.add(director);
				}

				//查询父节点，lis 2016-3-5
				sbf.setLength(0);
				sbf.append("select nbase, a0100 from p09,p08 where p09.p0903=p08.p0831 and p09.p0901 = 2 and p09.P0905=1 and p08.P0800!=p08.P0831 and p08.P0800=?");
				rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0800}));
				while(rs.next()){
					String nbase = rs.getString("nbase");
					String a0100 = rs.getString("a0100");
					if(taskMemberList.contains(nbase + a0100)) {
                        continue;
                    } else {
						//同时如果上级/关注人的集合中已经存在了就不重复发送邮件了  haosl (解决 37142)  2018年5月7日
						if(!taskFocuserList.contains(nbase + a0100)) {
                            taskMemberList.add(nbase + a0100);
                        }
					}
				}

				for(String usrId : taskMemberList){
					if(StringUtils.isBlank(usrId) || usrId.equals(this.userView.getDbname()+this.userView.getA0100())){
						continue;
					}
					String subject = "    " + userView.getUserFullName()+"在你的"+task_title+"下发布了信息,请查阅";

					String targetA0101 = wpUtil.getUsrA0101(usrId.substring(0, 3), usrId.substring(3));
					targetNameList.add(targetA0101);
					String bodyText = getTaskDiscussionToMemberEmail_BodyText(targetA0101, content, task_title);
					String task_href = getRemindEmail_TaskHref(usrId, p07vo, p0800);
					LazyDynaBean emailBean = getEmailBean(usrId,subject, bodyText, task_href,"去查看任务");
					emailBean.set("bodySubject", "工作任务沟通信息提醒");
					emailsList.add(emailBean);
				}
				//2.任务关注人\上级
				for(String usrId : taskFocuserList){
					if(StringUtils.isBlank(usrId) || usrId.equals(this.userView.getDbname()+this.userView.getA0100())){
						continue;
					}
					String subject = "";
					String bodyText = "";
					if(taskMemberList.contains(userView.getDbname()+userView.getA0100())){
						subject = "    " + userView.getUserFullName()+"在他/她的"+task_title+"下发布了信息,请查阅";
						bodyText = getTaskDiscussionToFocuserEmail_BodyText(wpUtil.getUsrA0101(usrId.substring(0, 3), usrId.substring(3)), "他/她", content, task_title);
					}else{
						subject = "    " + userView.getUserFullName()+"在"+taskOwnerName+"的"+task_title+"下发布了信息,请查阅";
						bodyText = getTaskDiscussionToFocuserEmail_BodyText(wpUtil.getUsrA0101(usrId.substring(0, 3), usrId.substring(3)), taskOwnerName, content, task_title);
					}
					String task_href = getRemindEmail_TaskHref(usrId, p07vo, p0800);
					LazyDynaBean emailBean = getEmailBean(usrId,subject, bodyText, task_href,"去查看任务");
					emailBean.set("bodySubject", "工作任务沟通信息提醒");
					emailsList.add(emailBean);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else if("3".equals(type)){//总结沟通
    		try {
	    		int p0100 = Integer.parseInt(object_id);
	    		RecordVo p01vo = new RecordVo("p01");
	    		p01vo.setInt("p0100", p0100);
				p01vo = dao.findByPrimaryKey(p01vo);
				//总结创建人
				String summaryOwner = p01vo.getString("nbase") + p01vo.getString("a0100");
				String summaryOwnerName = wpUtil.getUsrA0101(summaryOwner.substring(0, 3), summaryOwner.substring(3));
				//上级
				String superior = wpUtil.getMyDirectSuperPerson(summaryOwner.substring(0, 3), summaryOwner.substring(3));
				//总结关注人
				List<String> summaryFocuserList = new ArrayList();
				sbf.append("select nbase, a0100 from p09 where p0901 = 3 and p0905 = 3 and p0903 = ?");
				rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0100}));
				while(rs.next()){
					summaryFocuserList.add(rs.getString("nbase") + rs.getString("a0100"));
				}

				//发送提醒
				WorkPlanSummaryBo summaryBo = new WorkPlanSummaryBo(userView, conn);
				String summaryType = "";
				String e0122 = "";
				if("2".equals(p01vo.getString("belong_type"))){
					summaryType = "org";
					e0122 = p01vo.getString("e0122");
				}
				Date dd = p01vo.getDate("p0104");
				String summaryYear = DateUtils.getYear(dd) + "";
				String summaryMonth = DateUtils.getMonth(dd) + "";
				//haosl  沟通信息邮件提醒在月初月末的时候有问题,通过前台传递的参数解决该问题  2017-8-21  start
                if(StringUtils.isNotBlank(this.periodYear) && !"undefined".equals(this.periodYear) && !"null".equals(this.periodYear)) {
                    ;
                }
                    summaryYear = this.periodYear;
				if(StringUtils.isNotBlank(this.periodMonth) && !"undefined".equals(this.periodMonth) && !"null".equals(this.periodMonth)) {
                    ;
                }
					summaryMonth = this.periodMonth;
				String summaryWeek = p01vo.getString("time");
				String summary_title = summaryBo.getSummaryCycleDesc(p01vo.getString("state"), summaryYear, summaryMonth, summaryWeek) + "工作总结";
				//1.通知总结创建人
				if(StringUtils.isNotBlank(summaryOwner) && !summaryOwner.equals(userView.getDbname()+userView.getA0100())){
					String subject = "    " + userView.getUserFullName()+"在你的"+summary_title+"下发布了信息,请查阅";
					String bodyText = summaryBo.getSummaryMsgEmail_BodyText(summaryOwnerName, content, summary_title);
					String summary_href = summaryBo.getSummaryHref(summaryOwner.substring(0, 3), summaryOwner.substring(3), summaryType, summaryOwner, e0122, p01vo.getString("state"), summaryYear, summaryMonth, summaryWeek);
					LazyDynaBean emailBean = getEmailBean(summaryOwner,subject, bodyText, summary_href,"去查看工作总结");
					emailBean.set("bodySubject", "工作总结沟通信息提醒");
					emailsList.add(emailBean);
				}
				//2.通知上级\总结关注人
				summaryFocuserList.add(superior);
				for(String usrId : summaryFocuserList){
					if(StringUtils.isBlank(usrId) || usrId.equals(this.userView.getDbname()+this.userView.getA0100())){
						continue;
					}
					String usrName = wpUtil.getUsrA0101(usrId.substring(0, 3), usrId.substring(3));
					String subject = "";
					String bodyText = "";
					if(summaryOwner.equals(userView.getDbname()+userView.getA0100())){
						subject = "    " + userView.getUserFullName()+"在他/她的"+summary_title+"下发布了信息,请查阅";
						bodyText = summaryBo.getSummaryMsg1Email_BodyText(usrName, "他/她", content, summary_title);
					}else{
						subject = "    " + userView.getUserFullName()+"在"+ summaryOwnerName +"的"+summary_title+"下发布了信息,请查阅";
						bodyText = summaryBo.getSummaryMsg1Email_BodyText(usrName, summaryOwnerName, content, summary_title);
					}
					String summary_href = summaryBo.getSummaryHref(usrId.substring(0, 3), usrId.substring(3), summaryType, summaryOwner, e0122, p01vo.getString("state"), summaryYear, summaryMonth, summaryWeek);
					LazyDynaBean emailBean = getEmailBean(usrId,subject, bodyText, summary_href,"去查看工作总结");
					emailBean.set("bodySubject", "工作总结沟通信息提醒");
					emailsList.add(emailBean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else{
    		//暂时没有别的情况
    	}

    	AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userView);
        //发送邮件,
		emailBo.send(emailsList);
        //发送微信
        workPlanUtil.sendWeixinMessageFromEmail(emailsList);

    }

    /**
     * @Title: getApproveEmail_BodyText
     * @Description: 邮件审批、退回提醒内容
     * @param @param a0101
     * @param @param planOwerName
     * @param @param plan_title
     * @param @param bApprove
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getApproveEmail_BodyText(String a0101,
            String planOwerName,String plan_title,boolean bApprove)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(a0101).append(", 您好！").append("<br/>");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(this.userView.getUserFullName());
        if (bApprove){
            bodytext.append("已经批准了");
        }
        else {
            bodytext.append("已经退回了");
        }
        bodytext.append(planOwerName);
        bodytext.append(plan_title);
        if (!bApprove){
            bodytext.append("，请重新填写、发布工作计划。");
            bodytext.append("<br/>");
            bodytext.append("退回原因：");
            bodytext.append("<br/>");
            bodytext.append(getHtmlBlank(4));
            bodytext.append("    ");
            bodytext.append(this.returnInfo);
        }
        bodytext.append("<br/>");
        bodytext.append("<br/>");
        bodytext.append("<br/>");
        bodytext.append(cur_date);
        return bodytext.toString();
    }

    /**
     * @Title: getPublishEmail_BodyText
     * @Description:发布计划提醒
     * @param @param a0101
     * @param @param planOwerName
     * @param @param plan_title
     * @param @param bApprove
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getPublishEmail_BodyText(String a0101,
            String planOwerName,String plan_title)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(a0101).append(", 您好！").append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(this.userView.getUserFullName());
        bodytext.append("已经发布了");

        bodytext.append(planOwerName);
        bodytext.append(plan_title);
        bodytext.append("，请查阅并审批他/她的工作计划。");

        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(cur_date);
        return bodytext.toString();
    }

    /**
     * 发表计划沟通消息,给计划所有者发送微信提醒 模版
     * @param targetA0101
     * @param content
     * @param plan_title
     * @return
     */
    public String getPlanDiscussionTOwnerEmail_BodyText(String targetA0101,
            String content,String plan_title)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(targetA0101).append(", 您好！").append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(this.userView.getUserFullName());
        bodytext.append("在您的");
        //bodytext.append(planOwerName);
        bodytext.append(plan_title);
        bodytext.append("工作计划下发布了以下消息:");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(content);

        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(cur_date);
        return bodytext.toString();
    }

    /**
     * 任务沟通信息提醒模版(任务成员)
     * @param targetA0101
     * @param content
     * @param plan_title
     * @return
     */
    public String getTaskDiscussionToMemberEmail_BodyText(String targetA0101,
            String content,String plan_title)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(targetA0101).append(", 您好！").append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(this.userView.getUserFullName());
        bodytext.append("在您的");
        //bodytext.append(planOwerName);
        bodytext.append(plan_title);
        bodytext.append("下发布了消息:");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        if(StringUtils.isNotBlank(content)){
            content = content.replaceAll("\r\n|\n", "<br/>"+getHtmlBlank(4));
        }
        bodytext.append(content);

        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(cur_date);
        return bodytext.toString();
    }

    /**
     * 给计划关注人,计划所有者上级发送沟通消息,邮件模版
     * @param targetA0101
     * @param content
     * @param plan_title
     * @return
     */
    public String getPlanDiscussionToFocuserEmail_BodyText(String targetA0101, String planOwnerName,
            String content,String plan_title)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(targetA0101).append(", 您好！").append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(this.userView.getUserFullName());
        bodytext.append("在");
        bodytext.append(planOwnerName);
        bodytext.append("的");
        bodytext.append(plan_title);
        bodytext.append("工作计划下发布了以下消息:");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        if(StringUtils.isNotBlank(content)){
            content = content.replaceAll("\r\n|\n", "<br/>"+getHtmlBlank(4));
        }
        bodytext.append(content);

        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(cur_date);
        return bodytext.toString();
    }

    /**
     * 工作任务沟通信息提醒关注人模版
     * @param targetA0101
     * @param planOwnerName
     * @param content
     * @param plan_title
     * @return
     */
    public String getTaskDiscussionToFocuserEmail_BodyText(String targetA0101, String planOwnerName,
            String content,String plan_title)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(targetA0101).append(", 您好！").append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(this.userView.getUserFullName());
        bodytext.append("在");
        bodytext.append(planOwnerName);
        bodytext.append("的");
        bodytext.append(plan_title);
        bodytext.append("下发布了消息:");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(content);

        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(cur_date);
        return bodytext.toString();
    }

    /**
     * @Title: getRemindSubEmail_BodyText
     * @Description:上级在下级计划中新增任务提醒
     * @param @param a0101
     * @param @param planOwerName
     * @param @param plan_title
     * @param @return
     * @return String
     * @author:wusy
     * @throws
    */
    public String getRemindSubEmail_BodyText(String a0101,
            String planOwerName,String plan_title, String taskName)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(a0101).append(", 您好！").append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append(this.userView.getUserFullName());
        bodytext.append("在您的");

        bodytext.append(planOwerName);
        bodytext.append(plan_title);
        bodytext.append("工作计划新增了任务:  \""+taskName+"\"  ，请查看。");

        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(cur_date);
        return bodytext.toString();
    }


    /**
     * @Title: getRemindEmail_BodyText
     * @Description:提醒制定计划正文
     * @param @param a0101
     * @param @param submitDesc
     * @param @param planOwerName
     * @param @param plan_title
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getRemindEmail_BodyText(String a0101,String submitDesc,
            String planOwerName,String plan_title)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(a0101).append(", 您好！").append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append("请").append(submitDesc).append(planOwerName);
        bodytext.append(plan_title);
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(this.getUserFullName());
        bodytext.append("<br />");
        bodytext.append(cur_date);
        return bodytext.toString();
    }
    /**
     * @Title: getRemindEmail_BodyText
     * @Description:提醒批准计划正文
     * @param @param a0101
     * @param @param submitDesc
     * @param @param planOwerName
     * @param @param plan_title
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
     */
    public String getRemindApproveEmail_BodyText(String a0101,String submitDesc,
            String planOwerName,String plan_title)  {
        String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
        StringBuffer bodytext = new StringBuffer();
        bodytext.setLength(0);
        bodytext.append(a0101).append(", 您好！").append("<br />");
        bodytext.append(getHtmlBlank(4));
        bodytext.append("    ");
        bodytext.append("请").append(submitDesc).append(planOwerName);
        bodytext.append(plan_title);
        bodytext.append("<br />");
        bodytext.append("<br />");
        bodytext.append(this.getUserFullName());
        bodytext.append("<br />");
        bodytext.append(cur_date);
        return bodytext.toString();
    }

    /**
     * @Title: remindAllSubmitPlan
     * @Description:  提醒所有下级制定计划
     * @param @param p0723
     * @param @param periodtype
     * @param @param periodyear
     * @param @param periodmonth
     * @param @param superobjectid
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean remindMyTeamToSubmitPlan(String p0723,String periodtype,String periodyear,
            String periodmonth,String periodweek,String super_objectid )  {
        boolean b=false;
        this.P0723=p0723;
        this.periodType=periodtype;
        this.periodYear=periodyear;
        this.periodMonth=periodmonth;
        this.periodWeek=periodweek;
        initSuperiorFld();
        if (superiorFld==null || "".equals(superiorFld)){
            return b;
        }
        String nbase=this.userView.getDbname();
        String a0100=this.userView.getA0100();
        ArrayList e01a1list=new ArrayList();
        if ("1".equals(p0723)){
            if (super_objectid!=null && !"".equals(super_objectid)){

                nbase =super_objectid.substring(0, 3);
                a0100 =super_objectid.substring(3, super_objectid.length());
            }
        }
        else {
            if (super_objectid!=null && !"".equals(super_objectid)){
                String [] arre01a1 =super_objectid.split(",");
                for (int i=0;i<arre01a1.length;i++){
                    String e01a1= arre01a1[i];
                    if ("".equals(e01a1)) {
                        continue;
                    }
                    LazyDynaBean bean = new LazyDynaBean();
                    bean.set("e01a1", e01a1);
                    e01a1list.add(bean);
                }
            }
            else {
                e01a1list=  workPlanUtil.getMyE01a1List(nbase, a0100);
            }

        }
        try{

            ArrayList list= new ArrayList();
            String strsql = "";
            String tablename = "";
            String e01a1s = "";
            RowSet rset=null;
            if("1".equals(p0723)){
            	if ("false".equals(this.subPersonFlag)){
                     String sql="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+this.superiorFld+"='"+super_objectid+"'";
          			 RowSet rset1=dao.search(sql);
                     while(rset1.next()){
	                   	 String sube01a1= (String) rset1.getString("e01a1");
	                     tablename=getTeamPeopleSql(this.superiorFld,sube01a1,true);
	                     tablename="("+tablename+") T";

	                     strsql=" select T.*,P07.p0719 from " +tablename
	                     +" left join p07 on P07.nbase=T.nbase and P07.a0100=T.a0100"
	                     +" and "+this.getPlanPublicWhere();
	                     rset=dao.search(strsql);
	                    b= remindSubmitPlan(rset, p0723, periodtype, periodyear, periodmonth, periodweek,"0");
	                    b=true;

                    }
                     return b;
            	}
            	else {
            		tablename=getTeamPeopleSql(this.superiorFld,nbase,a0100,true);
            	}
                if ("".equals(tablename)){
                    return b;
                }
                tablename="("+tablename+") T";

                strsql=" select T.*,P07.p0719 from " +tablename
                +" left join p07 on P07.nbase=T.nbase and P07.a0100=T.a0100"
                +" and "+this.getPlanPublicWhere();
                rset=dao.search(strsql);

            }
            else if ("2".equals(p0723)){
                for (int i = 0; i < e01a1list.size(); i++) {
                    LazyDynaBean e01abean = (LazyDynaBean) e01a1list.get(i);
                    String e01a1 = (String) e01abean.get("e01a1");
                    if ("".equals(e01a1)) {
                        continue;
                    }
                    if (e01a1s.length() > 0) {
                        e01a1s = e01a1s + ",'" + e01a1 + "'";
                    } else {
                        e01a1s = e01a1s + "'" + e01a1 + "'";
                    }
                }

                // 有负责部门的岗位。
                tablename = "select k01.e01a1,b01.b0110,O.codeitemdesc from k01,B01,organization O "
                    + " where k01.e01a1=b01." + WorkPlanConstant.DEPTlEADERFld
                    + " and B01.b0110=O.codeitemid"
                    + " and K01." + superiorFld + " in (" + e01a1s + ")" + "";
                //关联p07
                strsql = " Select T.e01a1,T.B0110,T.codeitemdesc,p07.p0700,p07.p0719 from (" + tablename + ") T "
                + "left join (select * from P07 where " + this.getPlanPublicWhere()
                + ") P07 on p07.p0707 =T.B0110 ";

                rset = dao.search(strsql);

            }

            b= remindSubmitPlan(rset, p0723, periodtype, periodyear, periodmonth, periodweek,"0");
            b=true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;

    }

    /**
     * @Title: remindAllSubmitPlan
     * @Description:  提醒所有下级制定计划
     * @param @param p0723
     * @param @param periodtype
     * @param @param periodyear
     * @param @param periodmonth
     * @param @param superobjectid
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean remindSubmitPlan(RowSet rset,String p0723,String periodtype,String periodyear,
            String periodmonth,String periodweek,String submit_type)  {
        initSuperiorFld();
        boolean b=false;
        HashMap palnParamMap= new HashMap();
        palnParamMap.put("p0723", p0723);
        palnParamMap.put("periodtype", periodtype);
        palnParamMap.put("periodyear", periodyear);
        palnParamMap.put("periodmonth", periodmonth);
        palnParamMap.put("periodweek", periodweek);


        try{

            String plan_period=workPlanUtil.getPlanPeriodDesc(periodtype, periodyear, periodmonth, periodweek);
            String  plan_title=plan_period+"工作计划";
            ArrayList list= new ArrayList();

            String subject="提醒写工作计划";
            if ("1".equals(submit_type)){
                subject="提醒批准工作计划";
            }
            while (rset.next()){
            	if("1".equals(submit_type) && !"1".equals(rset.getString("p0719"))){//提醒批准工作计划,只关注发布状态的(p0719=1)
            		continue;
            	}
                String _p0719= rset.getString("p0719");
                if (_p0719==null) {
                    _p0719="";
                }
                String submitDesc="填写";
                if("0".equals(submit_type)){
                    if ("".equals(_p0719)){
                        submitDesc="填写";
                    }
                    else if ("0".equals(_p0719)||"3".equals(_p0719)){
                        submitDesc="提交";
                    }
                    else{
                        continue ;
                    }
                }
                else if ("1".equals(submit_type)){
                    if ("1".equals(_p0719)){
                        submitDesc="批准";
                    }
                    else {
                        continue ;
                    }
                }

                String _nbase= "";
                String _a0100= "";
                String _a0101= "";
                String ownername="本人";
                if("1".equals(p0723)){
                    _nbase= rset.getString("nbase");
                    _a0100= rset.getString("a0100");
                    _a0101= rset.getString("a0101");
                    palnParamMap.put("objectId",_nbase+_a0100);
                    palnParamMap.put("planType","person");
                    if ("1".equals(submit_type)){
                        ownername=_a0101;
                    }
                }
                else if ("2".equals(p0723)){
                    String strsql = "";
                    String tablename = "";
                    String _e01a1 = rset.getString("e01a1");
                    ownername = rset.getString("codeitemdesc");
                    String b0110=rset.getString("b0110");
                    tablename = workPlanUtil.getPeopleSqlByE01a1(_e01a1);
                    boolean b_person_leader = false;// 是否有人负责
                    palnParamMap.put("planType","org");
                    if (!"".equals(tablename)) {
                        tablename = "(" + tablename + ") T";
                        strsql = "select T.* from " + tablename;
                        RowSet rset1 = dao.search(strsql);
                        if (rset1.next()) {// 有人负责
                            b_person_leader = true;
                            _nbase = rset1.getString("nbase");
                            _a0100 = rset1.getString("a0100");
                            _a0101 = rset1.getString("a0101");
                        }
                    };

                    if (b_person_leader) {
                        this.deptLeaderId=_nbase+_a0100;
                        palnParamMap.put("dept_leaderid", _nbase+_a0100);
                        palnParamMap.put("objectId",b0110);
                    }
                    else {
                        continue;
                    }
                }
                String bodyText="";
                String href = "";
                if ("1".equals(submit_type)){
            	   String superid = rset.getString("p0733");
                   if(StringUtils.isEmpty(superid)) {
                   		superid=workPlanUtil.getMyApprovedSuperPerson(_nbase,_a0100);
                   }
                    String superNbase="";
                    String superA0100="";
                    //如果没有审批人则不发送邮件 haosl add 2018-3-21
                    if(StringUtils.isEmpty(superid)) {
                        continue;
                    } else{
                        superNbase =superid.substring(0, 3);
                        superA0100 =superid.substring(3);
                        _a0101=workPlanUtil.getUsrA0101(superNbase, superA0100);
                    }
                    bodyText=getRemindApproveEmail_BodyText(_a0101,submitDesc,ownername,plan_title);
                    href = getRemindEmail_PlanHref(palnParamMap,superNbase,superA0100,true);
                    LazyDynaBean emailBean = getEmailBean(superid, subject, bodyText, href);
                    list.add(emailBean);
                }
                else {
                    bodyText=this.getRemindEmail_BodyText(_a0101,submitDesc,ownername,plan_title);
                    href = getRemindEmail_PlanHref(palnParamMap,_nbase,_a0100,true);
                    LazyDynaBean emailBean = getEmailBean(_nbase+_a0100, subject, bodyText, href);
                    list.add(emailBean);

                }
                //发送待办
                if("0".equals(submit_type)){
                	String pending_title="";
                	pending_title="我的"+plan_title;
                	if ("2".equals(p0723)){
                		pending_title=ownername+"的"+plan_title;
                	}
                	pending_title=pending_title+"(填写)";
                	String pending_url=getPendingPlanUrl(palnParamMap);
                	LazyDynaBean pendingBean = new  LazyDynaBean();
                	pendingBean.set("pending_url", pending_url);
                	pendingBean.set("pending_title", pending_title);
                	String receiver =workPlanUtil.getUserNameByA0100(_nbase,_a0100);
                	workPlanUtil.sendPending_remindPublishPlan(this.userView.getUserName(),
                			receiver, pendingBean,palnParamMap);
                }

            }
            if (list.size()>0){
                AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userView);
                emailBo.send(list);
                workPlanUtil.sendWeixinMessageFromEmail(list);
            }
            b=true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return b;

    }


    /**
     * @Title: remindSubmitPlan
     * @Description: 提醒某人提交计划
     * @param @param p0723
     * @param @param periodtype
     * @param @param periodyear
     * @param @param periodmonth
     * @param @param periodweek
     * @param @param objectid
     * @param @return
     * @return boolean
     * @author:wangrd
     * @throws
    */
    public boolean remindSubmitPlan(String p0723,String periodtype,String periodyear,
            String periodmonth,String periodweek,String objectid,String submit_type )  {
        if("2".equals(submit_type)){
            return true;
        }
        boolean b=false;
        this.P0723=p0723;
        this.periodType=periodtype;
        this.periodYear=periodyear;
        this.periodMonth=periodmonth;
        this.periodWeek=periodweek;
        initSuperiorFld();
        if (superiorFld==null || "".equals(superiorFld)){
            return b;
        }

        try{
            String strsql = "";
            String tablename = "";
            RowSet rset=null;
            if("1".equals(p0723)){
                String nbase=this.userView.getDbname();
                String a0100=this.userView.getA0100();
                if (objectid!=null && !"".equals(objectid)){
                    nbase =objectid.substring(0, 3);
                    a0100 =objectid.substring(3);
                }

                tablename = "select A.*,'"+nbase+"' as nbase from "+nbase+"A01 A where a0100='" +a0100+"'";
                tablename="("+tablename+") T";
                strsql=" select T.*,P07.p0719,p07.p0733 from " +tablename
                +" left join p07 on P07.nbase=T.nbase and P07.a0100=T.a0100"
                +" and "+this.getPlanPublicWhere();
                rset=dao.search(strsql);

            }
            else if ("2".equals(p0723)){
                tablename = "select k01.e01a1,b01.b0110,O.codeitemdesc from k01,B01,organization O "
                    + " where k01.e01a1=b01." + WorkPlanConstant.DEPTlEADERFld
                    + " and B01.b0110=O.codeitemid"
                    + " and B01.b0110='"+objectid+"'";
                //关联p07
                strsql = " Select T.e01a1,T.B0110,T.codeitemdesc,p07.p0700,p07.p0719,p07.p0733 from (" + tablename + ") T "
                + "left join (select * from P07 where " + this.getPlanPublicWhere()
                + ") P07 on p07.p0707 =T.B0110 ";

                rset = dao.search(strsql);
            }

            b= remindSubmitPlan(rset, p0723, periodtype, periodyear, periodmonth, periodweek,submit_type);
            b=true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }



    /**
     * @Title: quotedDoubleValue
     * @Description:变量两边加双引号
     * @param
     * @param value
     * @param
     * @return
     * @return String
     * @author:wangrd
     * @throws
     */
    private String quotedDoubleValue(String value) {
          return workPlanUtil.quotedDoubleValue(value);
    }
    /**
     * @Title: getHtmlBlank
     * @Description:获取html空格
     * @param @param num 空格个数
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    private String getHtmlBlank(int num) {
        String str="";
        for (int i=0;i<num;i++){
            str=str+"&nbsp;";

        }
        return str;
    }

    /**
     * @Title: getPhotoPath
     * @Description:获取人员图像所在路径
     * @param @param nbase
     * @param @param a0100
     * @param @return
     * @return String
     * @author:wangrd
     * @throws
    */
    public String getPhotoPath(String nbase,String a0100) {
    	//调用可压缩图片方法
    	return new PhotoImgBo(conn).getPhotoPathLowQuality(nbase, a0100);
    }

    /**
     * 获取计划信息的简短描述
     * @param plan_vo 计划对象
     * @return 类似"我的人力资源部 2014年5月部门工作计划"
     * @throws Exception
     */
    public String getPlanDescription(RecordVo plan_vo) throws Exception {
    	StringBuffer desc = new StringBuffer();

    	try {
	    	/** ################### 方案一：我的人力资源部 2014年5月部门工作计划: 谁的哪个部门 ################ */
    		/** ################### 方案二：产品部（郭建文）2014年部门工作计划 ################ */
	    	if (plan_vo.getInt("p0723") == 2) { // 团队计划
	    		String p0707 = plan_vo.getString("p0707");

	    		String imagineUnit = AdminCode.getCodeName("UN", p0707); // 假设是单位
	    		String imagineDept = AdminCode.getCodeName("UM", p0707); // 假设是部门
	    		String orgName = "".equals(imagineUnit) ? imagineDept : imagineUnit; // 最终结果:单位或部门的名称

	    		desc.append(orgName);
    			if (!new PlanTaskBo(conn, userView).isMyselfResponsibleUnit(p0707)) { // 是当前用户负责的单位
    				String id = new WorkPlanUtil(conn, userView).getFirstDeptLeaders(plan_vo.getString("p0707"));
    				String leader = new PlanTaskBo(conn, userView).getA0101(id);
					desc.append("(").append(leader).append(")");
    			}
	    	} else if (plan_vo.getInt("p0723") == 1) { // 人员计划
	    		if (userView.getDbname().equals(plan_vo.getString("nbase")) && userView.getA0100().equals(plan_vo.getString("a0100"))) { // 当前用户即是计划的创建者
	    			desc.append("我的");
	    		} else {
	    			desc.append(new PlanTaskBo(conn, userView).getA0101(plan_vo.getString("nbase") + plan_vo.getString("a0100"))).append("的");
	    		}
	    	}

	    	/** ################### 我的人力资源部 2014年5月部门工作计划: 什么时候 ################ */
			/*
	    	switch (plan_vo.getInt("p0725")) {
			case 1: // 年度
				desc.append(plan_vo.getInt("p0727")).append("年");
				break;
			case 2: // 半年
				desc.append(plan_vo.getInt("p0727")).append("年");
				desc.append(plan_vo.getInt("p0729") == 1 ? "上半年" : "下半年");
				break;
			case 3: // 季度
				desc.append(plan_vo.getInt("p0727")).append("年");
				desc.append("第").append(plan_vo.getInt("p0729")).append("季度");
				break;
			case 4: // 月份
				desc.append(plan_vo.getInt("p0727")).append("年");
				desc.append(plan_vo.getInt("p0729")).append("月");
				break;
			case 5: // 周
				desc.append(plan_vo.getInt("p0727")).append("年");
				desc.append("第").append(plan_vo.getInt("p0729")).append("周");
				break;
			case 6: // 不定期
				desc.append(plan_vo.getInt("p0727")).append("年");
				break;
			default:
				break;
			}
			*/

	        desc.append(workPlanUtil.getPlanPeriodDesc(plan_vo.getString("p0725"),plan_vo.getString("p0727"),
                    plan_vo.getString("p0729"),plan_vo.getString("p0731")));

			/** ################### 我的人力资源部 2014年5月部门工作计划: "工作计划"之前的"部门" ################ */
			if (plan_vo.getInt("p0723") == 2) { // 人员计划
    			desc.append(AdminCode.getCodeName("UN", plan_vo.getString("p0707")).length() == 0 ? "部门" : "单位");
			}

			/** ################### 我的人力资源部 2014年5月部门工作计划: 结尾的"工作计划" ################ */
			desc.append("工作计划");
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}

    	return desc.toString();
    }

    /**
     * 计划跟踪页面修改任务进度
     * @param p0800
     * @param p0835
     */
    public void editTaskProgress(String p0800,String p0835){
    	if (p0800 != null && p0835 != null && p0800.length()>0 && p0835.length()>0
				&&!"0".equals(p0800)) {
		int iP0800 = "".equals(p0800) ? 0 : Integer.parseInt(WorkPlanUtil.decryption(p0800));
		int iP0835 = "".equals(p0835) ? 0 : Integer.parseInt(p0835);
		String uSql ="";
		String p0809 = null;
		switch(iP0835) {
			case 0: p0809 = WorkPlanConstant.TaskExecuteStatus.BEFORE_START; break;
			case 100: p0809 = WorkPlanConstant.TaskExecuteStatus.COMPLETE; break;
			default: p0809 =  WorkPlanConstant.TaskExecuteStatus.UNDERWAY;
		}
		// 更新任务进度和任务状态
		     uSql = "UPDATE P08 SET p0835=?,p0809=? WHERE p0800=?";
		     try {
				new ContentDAO(conn).update(uSql, Arrays.asList(new Object[] {
							new Integer(iP0835),
							p0809,
							new Integer(iP0800)
					}));
				RecordVo p08Vo = new RecordVo("p08");
				p08Vo.setInt("p0800", iP0800);
				p08Vo = new ContentDAO(conn).findByPrimaryKey(p08Vo);
				if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
				     String logcontent = "将任务完成进度更新为" + iP0835 + "%";
				     new WorkPlanOperationLogBo(conn, userView).addLog(iP0800, logcontent);
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}

	   }

    }

    /**
     * 计划页面更新一般日期类型(不存在业务规则,如:开始日期需在结束日期之前)扩展
     * @param p0800
     * @param field
     * @param value
     * @return
     */
	public String updateTimeField(String p0800, String field, String value) {
		if(StringUtils.isBlank(p0800) || StringUtils.isBlank(field)){
			return "";
		}
    	try {
	    	Integer ip0800 = Integer.parseInt(WorkPlanUtil.decryption(p0800));
	    	RecordVo p08Vo = new RecordVo("p08");
	    	p08Vo.setInt("p0800", ip0800);
			ContentDAO dao = new ContentDAO(conn);
			p08Vo = dao.findByPrimaryKey(p08Vo);
			Date oldPField = p08Vo.getDate(field);
			DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
    		if (value != null && value.length() > 0) {
    		    try{
    		        df.parse(value);
                }
                catch (Exception e){
                    throw new Exception("该时间格式不正确！");
                }
                p08Vo.setDate(field, df.parse(value));
    		}else{
    			p08Vo.setDate(field, "");
    		}
    		dao.updateValueObject(p08Vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}


    /**
     * 计划页面更新任务开始时间
     * @param p0800
     * @param p0813
     * @return
     */
    public String updateStartTime(String p0800, String p0813) {
    	if(StringUtils.isBlank(p0800)){
			return "P0800 can not be null";
		}
    	try {
	    	Integer ip0800 = Integer.parseInt(WorkPlanUtil.decryption(p0800));
	    	RecordVo p08Vo = new RecordVo("p08");
	    	p08Vo.setInt("p0800", ip0800);
			ContentDAO dao = new ContentDAO(conn);
			p08Vo = dao.findByPrimaryKey(p08Vo);
			Date oldP0813 = p08Vo.getDate("p0813");
			Date p0815 = p08Vo.getDate("p0815");
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			if(StringUtils.isBlank(p0813) && (oldP0813 != null)){
				p08Vo.setDate("p0813", df.parse(null));
			}
    		if (p0813 != null && p0813.length() > 0) {
    		    try{
    		        df.parse(p0813);
                }
                catch (Exception e){
                    throw new Exception("开始日期格式不正确！");
                }
    		}
			if(oldP0813 == null){
				if(StringUtils.isBlank(p0813)){
					return "";
				}else{
					p08Vo.setDate("p0813", df.parse(p0813));
				}
			}else{
				if(df.parse(p0813).equals(p0815)){
					return "开始日期和结束日期相同";
				}
				if(df.parse(p0813).equals(oldP0813)){
					return "";
				}
				if(df.parse(p0813) != null && p0815!=null  && df.parse(p0813).after(p0815)) { // 开始结束日期都不为空且结束日期早于开始日期，抛异常
					return "开始日期大于结束日期, 保存失败";
				}else{
					p08Vo.setDate("p0813", df.parse(p0813));
				}
			}
			dao.updateValueObject(p08Vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

    /**
     * 计划页面更新任务结束时间
     * @param p0800
     * @param p0815
     * @return
     */
    public String updateEndTime(String p0800, String p0815) {
    	if(StringUtils.isBlank(p0800)){
			return "P0800 can not be null";
		}
    	try {
    		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    		if (p0815 != null && p0815.length() > 0) {
    		    try{
    		        df.parse(p0815);
                }
                catch (Exception e){
                    throw new Exception("结束日期格式不正确！");
                }
    		}
	    	Integer ip0800 = Integer.parseInt(WorkPlanUtil.decryption(p0800));
	    	RecordVo p08Vo = new RecordVo("p08");
	    	p08Vo.setInt("p0800", ip0800);
			ContentDAO dao = new ContentDAO(conn);
			p08Vo = dao.findByPrimaryKey(p08Vo);
			Date oldP0815 = p08Vo.getDate("p0815");
			Date p0813 = p08Vo.getDate("p0813");
			if(oldP0815 == null){
				if(StringUtils.isBlank(p0815)){
					return "";
				}else{
					p08Vo.setDate("p0815", df.parse(p0815));
				}
			}else{
				if(StringUtils.isBlank(p0815)){
					p08Vo.setDate("p0815", df.parse(null));
				}
				if(df.parse(p0815).equals(p0813)){
					return "开始日期和结束日期相同";
				}
				if(df.parse(p0815).equals(oldP0815)){
					return "";
				}
				if(df.parse(p0815) != null && p0813!=null  && df.parse(p0815).before(p0813)) {
					return "结束日期小于开始日期, 保存失败";
				}else{
					p08Vo.setDate("p0815", df.parse(p0815));
				}
			}
			dao.updateValueObject(p08Vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

    /**
     * 计划页面更新任务分类
     * @param p0800
     * @param p0823
     * @return
     */
    public String updateTaskCode(String p0800, String field, String value) {
    	if(StringUtils.isBlank(p0800) || StringUtils.isBlank(field) || StringUtils.isBlank(value)){
			return "";
		}
    	try {
			Integer ip0800 = Integer.parseInt(WorkPlanUtil.decryption(p0800));
	    	RecordVo p08Vo = new RecordVo("p08");
	    	p08Vo.setInt("p0800", ip0800);
			ContentDAO dao = new ContentDAO(conn);
			p08Vo = dao.findByPrimaryKey(p08Vo);
			String oldField = p08Vo.getString(field);
			//与原值相同
			if(value.equals(oldField)){
				return "";//same
			}
			//任务是否取消
			if ("5".equals(p08Vo.getString("p0809")) || "2".equals(p08Vo.getInt("p0833") + "")){
    			return "任务已取消";
    		}
			p08Vo.setString(field, value);
			dao.updateValueObject(p08Vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

    /**
     * 更新任务中大文本字段 (P0803,P0841,P0837)
     * @param p0800
     * @param field
     * @param eValue
     * @return
     */
    public String updateBigTextColumn(String p0800, String field, String eValue) {
    	if(StringUtils.isBlank(p0800)){
			return "P0800 can not be null";
		}
		if(StringUtils.isBlank(field)){
			return "";
		}
		try {
			Integer ip0800 = Integer.parseInt(WorkPlanUtil.decryption(p0800));
	    	RecordVo p08Vo = new RecordVo("p08");
	    	p08Vo.setInt("p0800", ip0800);
			ContentDAO dao = new ContentDAO(conn);
			p08Vo = dao.findByPrimaryKey(p08Vo);
			String oldField = p08Vo.getString(field);
			//与原值相同
			if(eValue.equals(oldField)){
				return "";//same
			}
			//任务是否取消
			if ("5".equals(p08Vo.getString("p0809")) || "2".equals(p08Vo.getInt("p0833") + "")){
    			return "任务已取消";
    		}
			p08Vo.setString(field, eValue);
			dao.updateValueObject(p08Vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

    /**
     * 计划页面更新计划工时p0817
     * @param p0800
     * @param p0817
     * @return
     */
    public String PlanedHours(String p0800, String p0817) {
		if(StringUtils.isBlank(p0800)){
			return "P0800 can not be null";
		}

		try {
			Integer ip0800 = Integer.parseInt(WorkPlanUtil.decryption(p0800));
	    	RecordVo p08Vo = new RecordVo("p08");
	    	p08Vo.setInt("p0800", ip0800);
			ContentDAO dao = new ContentDAO(conn);
			p08Vo = dao.findByPrimaryKey(p08Vo);
			String oldP0817 = p08Vo.getString("p0817");
			//与原值相同
			if(p0817.equals(oldP0817)){
				return "";//same
			}
			//任务是否取消
			if ("5".equals(p08Vo.getString("p0809")) || "2".equals(p08Vo.getInt("p0833") + "")){
    			return "任务已取消";
    		}
			p08Vo.setString("p0817", p0817);
			dao.updateValueObject(p08Vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "success";
	}

    /**
     * 更新任务名
     * @param p0800
     * @param taskName
     * @return
     */
    public String editTaskName(String p0800, String taskName){
    	if(StringUtils.isBlank(p0800) && StringUtils.isBlank(taskName)){
			return "canNotBeNull";
    	}
    	RowSet rset = null;
    	try {
    		Integer ip0800 = Integer.parseInt(WorkPlanUtil.decryption(p0800));
        	RecordVo p08Vo = new RecordVo("p08");
        	p08Vo.setInt("p0800", ip0800);
			ContentDAO dao = new ContentDAO(conn);
    		p08Vo = dao.findByPrimaryKey(p08Vo);
    		String oldTaskName = p08Vo.getString("p0801");
    		//不能与同计划下其他任务同名
    		int p0700 = p08Vo.getInt("p0700");
    		int p0831 = p08Vo.getInt("p0831");
    		//检查任务是否重名  lis 20160624
    		PlanTaskTreeTableBo treeBo = new PlanTaskTreeTableBo(conn, p0700);
    		String ip0831=p0831 == ip0800?"":p0831+"";
            if (treeBo.taskNameIsRepeated(ip0831, ip0800.toString(),taskName)){
            	return "repeat";
            }
    		//首先确定任务没被取消
    		if ("5".equals(p08Vo.getString("p0809")) || "2".equals(p08Vo.getInt("p0833") + "")){
    			return "任务已取消";
    		}
    		//对比同名,不更新
    		if(oldTaskName.equals(taskName)){
    			return "";//same
    		}

    		StringBuffer sbf = new StringBuffer();
    		sbf.append("select p0801, p0800, p0831 from p08 where p0700 = ? and p0831 = ?");
    		rset = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0700, p0831}));
    		while(rset.next()){
    			if(taskName.equals(rset.getString("p0801")) && (rset.getInt("p0800") != rset.getInt("p0831"))){
    				return "taskNameCanNotBeTheSame";
    			}
    		}
    		//如果任务没有取消
			p08Vo.setString("p0801", taskName);
			dao.updateValueObject(p08Vo);
			if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
			     String logcontent = "将任务名更新为  \"" + taskName + "\"";
			     new WorkPlanOperationLogBo(conn, userView).addLog(ip0800, logcontent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(rset != null){
				PubFunc.closeDbObj(rset);
			}
		}
		return "success";
    }


    public String getWorkType() {
        return workType;
    }


    public void setWorkType(String workType) {
        this.workType = workType;
    }


    public String getNBase() {
        return nBase;
    }


    public void setNBase(String base) {
        nBase = base;
    }


    public String getA0100() {
        return A0100;
    }


    public void setA0100(String a0100) {
        A0100 = a0100;
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


    public String getSubObjectId() {
        return subObjectId;
    }


    public void setSubObjectId(String subObjectId) {
        this.subObjectId = subObjectId;
    }

    public String getCurJsp() {
        return curJsp;
    }


    public String getHumanMapType() {
        return humanMapType;
    }


    public void setHumanMapType(String humanMapType) {
        this.humanMapType = humanMapType;
    }


    public String getHumanMap_cur_page() {
        return humanMap_cur_page;
    }


    public void setHumanMap_cur_page(String humanMap_cur_page) {
        this.humanMap_cur_page = humanMap_cur_page;
    }


    public void setCurJsp(String curJsp) {
        this.curJsp = curJsp;
    }

    public String getDeptLeaderId() {
        return deptLeaderId;
    }


    public void setDeptLeaderId(String deptLeader) {
        this.deptLeaderId = deptLeader;
    }


    public String getReturnInfo() {
        return returnInfo;
    }


    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }


    public String getObjectId() {
        return objectId;
    }


    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    public String getP0700() {
        return P0700;
    }


    public void setP0700(String p0700) {
        P0700 = p0700;
    }


    public String getP0723() {
        return P0723;
    }


    public void setP0723(String p0723) {
        P0723 = p0723;
    }


    public int getWeekNum() {
        return weekNum;
    }


    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }


    public RecordVo getP07_vo() {
        return p07_vo;
    }


    public void setP07_vo(RecordVo p07_vo) {
        this.p07_vo = p07_vo;
    }


    public String getSubPersonFlag() {
		return subPersonFlag;
	}


	public void setSubPersonFlag(String subPersonFlag) {
		this.subPersonFlag = subPersonFlag;
	}

	/**
	 * 查看人员任务状态是否是已变更
	 * @param p0700
	 * @return
	 */
	public boolean isStateChange(String p0700){
		boolean b = false;
		String sqlstr = "select p0809,p0833 from p08 where p08.p0700 = ?";
    	List list = new ArrayList();
    	list.add(p0700);
    	RowSet rs;
		try {
			rs = dao.search(sqlstr, list );
			while(rs.next()) {
				String p0809 = rs.getString("p0809"); // 任务执行状态
		        if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)) { // 已取消
		           continue;
		        }
	        	if(rs.getInt("p0833")>0){
	        		b = true;
	        		break;
	        	}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return b;
	}
	/**
	 * 同步目标卡：关键目标模块更新【完成进度】、【进度说明】等字段时同步
	 * @param p0800：任务号
	 * @param objectivefield：要更新的字段名
	 * @param value:更新的值
	 * @param type:更新字段的类型
	 * @autor chent
	 */
	public void syncP04(String p0800, String objectivefield, String value, String type) throws Exception {
		PlanTaskBo planTaskBo = new PlanTaskBo(this.conn, this.userView);
		 RecordVo task_vo = planTaskBo.getTask(Integer.parseInt(p0800));
		 int p0700 = task_vo.getInt("p0700");
		 PlanTaskTreeTableBo bo = new PlanTaskTreeTableBo(this.conn, p0700);
		 RecordVo p07vo = bo.getP07_vo();
		 String khplan_id = p07vo.getString("relate_planid");//获取关联的考核计划号
		 if (!StringUtils.isEmpty(khplan_id)){
			 int p0400 = getP0400(p0800, khplan_id);//获取目标卡号
			 if(p0400 != 0){
				 RecordVo vo = new RecordVo("P04");
                 vo.setInt("p0400", p0400);
                 vo = dao.findByPrimaryKey(vo);

                 if (!StringUtils.isEmpty(objectivefield)){
                	 if("M".equalsIgnoreCase(type)){
                		 vo.setString(objectivefield, value);
                	 } else if("N".equalsIgnoreCase(type)){
                		 vo.setInt(objectivefield, Integer.parseInt(value));
                	 }
                 }

                 dao.updateValueObject(vo);
			 }
		 }
	}

	/**
	 * 获取目标卡号
	 * @param p0800：关键目标模块的任务号
	 * @param khplan_id：关联的考核计划号
	 * @return
	 * @throws GeneralException
	 * @autor chent
	 */
	public int getP0400(String p0800, String khplan_id) throws GeneralException {
        int p0400 = 0;
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            String sql="select * from p04 where fromflag =5 and p0401= '"+p0800+"' and plan_id ="+khplan_id;
            rs = dao.search(sql);

            if (rs.next()) {//判断是否存在此任务
            	p0400 = rs.getInt("p0400");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeDbObj(rs);
        }

        return p0400;
    }
	/**
     * @Description:获取协办任务页面url
     * @param @param logonUser 收消息的人:usr000001
     * @author:chent
     * @throws
    */
	public String getRemindEmail_MyCooperationTaskHref(String logonUser) {

		StringBuffer url = new StringBuffer(this.userView.getServerurl());
		LazyDynaBean abean = workPlanUtil.getUserNamePassword(logonUser.substring(0, 3), logonUser.substring(3));

		String username = (String) abean.get("username");
		if (abean != null && username != null && username.length() > 0) {
			String pwd = (String) abean.get("password");

			String htmlHref = "/module/workplan/cooperationtask/MyCooperationTask.html?1=1";
			url.append("/module/utils/jsp.do?br_query=link&param="+SafeCode.encode(htmlHref));

			String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username + "," + pwd));
			url.append("&etoken=").append(etoken);

			url.append("&appfwd=1");
		}

		return url.toString();
	}
	/**
     * @Description:获取协办待办任务页面url
     * @param @param logonUser 收消息的人:usr000001
     * @author:chent
     * @throws
    */
	public String getRemindEmail_CooperationTaskHref(String logonUser) {

		StringBuffer url = new StringBuffer(this.userView.getServerurl());
		LazyDynaBean abean = workPlanUtil.getUserNamePassword(logonUser.substring(0, 3), logonUser.substring(3));

		String username = (String) abean.get("username");
		if (abean != null && username != null && username.length() > 0) {
			String pwd = (String) abean.get("password");

			String htmlHref = "/module/workplan/cooperationtask/CooperationTaskApprove.html?1=1";
			url.append("/module/utils/jsp.do?br_query=link&param="+SafeCode.encode(htmlHref));

			String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username + "," + pwd));
			url.append("&etoken=").append(etoken);

			url.append("&appfwd=1");
		}

		return url.toString();
	}
	/**
	 * 计划审批后，把计划下的协办任务推送给协办人上级
	 * @param p0700：计划号
	 * chent
	 */
	public void releaseCooperationTasks(String p0700){

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
			if(util.isOpenCooperationTask()){//启用协作任务
				// 查询计划下的任务
				String sql = "select p0800 from p08 where p0700=?";
				ArrayList<String> list = new ArrayList<String>();
				list.add(p0700);
				rs = dao.search(sql, list);
				StringBuilder posted = new StringBuilder(",");
				while (rs.next()) {
					int p0800 = rs.getInt("p0800");
					if(util.isCooperationTask(p0800, true)){//是协办任务
						PlanTaskBo bo = new PlanTaskBo(this.conn, userView);
						RecordVo task = bo.getTask(p0800);
						String p0811 = task.getString("p0811");
						if("03".equals(p0811) && "02".equals(getCooperationTaskStatus(p0800))){//如果是已批准的协办任务，不处理。场景：重新发布时，计划中只有一个是驳回的任务，其他的都是已批准，只想报批这一个任务时，应把已批准的任务跳过 chent 20160919。
							continue;
						}

						if(util.isInTeamCooperationTask(p0800)){// 团队内协办
							this.deleteCooperationTask(p0800);//先删除协办任务表中数据
							// 通过任务号初始化协办表。发布协办任务：直接审批,审批状态（P1019）直接为已批（02）
							this.newCooperationTask(p0800, true);

						} else {// 跨团队协办
							String startPerson = util.getTaskRolePersionList(p0800, 5).get(0);//任务发起人
							String startPersonName = util.getUserNameByA0100(startPerson.substring(0, 3), startPerson.substring(3));//任务发起人名字
							String privPerson = util.getTaskRolePersionList(p0800, 1).get(0);//任务负责人
							String privPersonDirect = util.getMyDirectSuperPerson(privPerson.substring(0, 3), privPerson.substring(3)); //任务负责人直接上级
							String privPersonDirectName = util.getUserNameByA0100(privPersonDirect.substring(0, 3), privPersonDirect.substring(3));// 协办人直接上级的名字

							/** 发布协办任务 */
							if(!this.isHasCooperationTask(p0800)) {//不存在该协办任务
								newCooperationTask(p0800, false);// 发布协办任务

							} else {//有协办任务

								// 任务负责人唯一标识（p08表中）
								HashMap<String, String> privPersonMap = util.getInfoByNbsA0100(privPerson.substring(0, 3), privPerson.substring(3));
								String privPersonGuidkey = privPersonMap.get("guidkey");

								// 协办人唯一标识（p10表中）
								String cooperationPersonGuidkey = getCooperationPersonGuidKey(p0800);

								/** 有该协作待办任务并且协办人相同 */
								if(privPersonGuidkey.equalsIgnoreCase(privPersonGuidkey)){//任务负责人和协办表中协办人是同一人
									this.updateCooperationTaskStatus(p0800, "01");//置为待批
									// 不推送消息
									continue ;
								}
								/** 有该协作任务并且协办人不同（说明变更了协办人） */
								else {// 不是同一人，说明变更了协办人

									/** 生成一条新的协作待办申请 */
									this.deleteCooperationTask(p0800);//先删除协办任务表中数据
									this.newCooperationTask(p0800, false);// 重新发布协办任务

								}
							}

							if(this.isHasCooperationTask(p0800) && this.getCooperationTaskStatus(p0800)=="02"){//【有该协办任务】 并且 【协办任务状态为已批】 说明是复制过来的，则不发通知
								continue ;
							}

							/** 生成一条新的协作待办申请、发邮件 */
							util.update_cooperationTask("1", String.valueOf(p0800));// 原待办置为已办
							// 发待办
							util.sendPending_ToCooperationTask(this.userView.getUserName(), privPersonDirectName, String.valueOf(p0800));

							if(posted.toString().indexOf(","+privPersonDirect+",") < 0){//协办任务邮件合并：当前计划下，没有给收件人发过邮件时才去发送邮件
								// 发邮件、微信
								HashMap<String, String> emailInfoMap = util.getEmailTemplateInfo(1);
								String bodyText = emailInfoMap.get("bodyText");
								bodyText = bodyText.replace("{mark1}", util.getInfoByNbsA0100(privPersonDirect.substring(0, 3), privPersonDirect.substring(3)).get("a0101"));
								bodyText = bodyText.replace("{mark2}", (util.getInfoByNbsA0100(startPerson.substring(0, 3), startPerson.substring(3)).get("a0101")+" 等人"));
								bodyText = bodyText.replace("{mark3}", (new PlanTaskBo(this.conn,this.userView).getTask(p0800).getString("p0801")+"等任务"));//任务名称

								String emailUrl = getRemindEmail_CooperationTaskHref(privPersonDirect);
								util.sendEmaiAndWeiXin(emailInfoMap.get("title"), bodyText, emailInfoMap.get("hrefDesc"), emailUrl, privPersonDirect);
								posted.append(privPersonDirect+",");
							}
						}

					} else {//不是协办任务
					}
				}
				util.updatePlanGuidKey(p0700);// 更新计划唯一标识
			} else {//【协作任务处理模式】1: 发布计划（默认流程）,不处理
				return ;

			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 通过任务号，初始化协办表
	 * @param p0800：任务号
	 * @param isApprove：是否直批
	 * @return
	 * chent
	 */
	public boolean newCooperationTask(int p0800, boolean isApprove){
		boolean succeed = false;
		try{
			WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);

			/** 获取发起人、发起人上级、负责人、负责人上级 的信息 */
			//发起人的信息map
			String startPerson = util.getTaskRolePersionList(p0800, 5).get(0);
			HashMap<String, String> startpersonMap = util.getInfoByNbsA0100(startPerson.substring(0, 3), startPerson.substring(3));
			//负责人的信息map
			String privPerson = util.getTaskRolePersionList(p0800, 1).get(0);
			HashMap<String, String> privPersonMap = util.getInfoByNbsA0100(privPerson.substring(0, 3), privPerson.substring(3));
			//发起人的直接上级信息map
			String startPersonDirect = util.getMyDirectSuperPerson(startPerson.substring(0, 3), startPerson.substring(3));
			HashMap<String, String> startPersonDirectMap = util.getInfoByNbsA0100(startPersonDirect.substring(0, 3), startPersonDirect.substring(3));
			//负责人的直接上级信息map
			String privPersonDirect = util.getMyDirectSuperPerson(privPerson.substring(0, 3), privPerson.substring(3));
			HashMap<String, String> privPersonDirectMap = util.getInfoByNbsA0100(privPersonDirect.substring(0, 3), privPersonDirect.substring(3));

			/** 获取任务信息 */
			PlanTaskBo bo = new PlanTaskBo(this.conn, this.userView);
			RecordVo task = bo.getTask(p0800);

			/** 插入协办表 */
			RecordVo vo = new RecordVo("p10");
			vo.setString("p1001", new IDFactoryBean().getId("p10.p1001", "", this.conn));//主键序号ID
			vo.setInt("p0800", p0800);//任务ID
			String dept = AdminCode.getCodeName("UM", startpersonMap.get("e0122"));
			vo.setString("p1003", dept);//发起部门
			vo.setString("p1005", startpersonMap.get("a0101"));//发起人姓名
			vo.setString("guidke_creater", startpersonMap.get("guidkey"));//任务所有者唯一标识
			vo.setDate("p1007", new java.sql.Date(new java.util.Date().getTime()));//发起时间
			vo.setString("p1009", task.getString("p0801"));//协办任务名称
			vo.setString("guidke_creater_sp", startPersonDirectMap.get("guidkey"));//发起人直接领导唯一标识
			vo.setString("p1011", startPersonDirectMap.get("a0101"));//发起人直接领导姓名
			String dept1 = AdminCode.getCodeName("UM", privPersonMap.get("e0122"));
			vo.setString("p1013", dept1);//协办部门
			vo.setString("p1015", privPersonMap.get("a0101"));//协办人姓名
			vo.setString("guidke_owner", privPersonMap.get("guidkey"));//协办人唯一标识
			vo.setString("guidke_owner_sp", privPersonDirectMap.get("guidkey"));//协办人的审批人唯一标识
			vo.setDate("p1017", new java.sql.Date(new java.util.Date().getTime()));//审批时间

			String p1019 = "01";//待批
			if(isApprove){
				p1019 = "02";//已批
			}
			vo.setString("p1019", p1019);//审批状态 01:待批（发起人领导审批后，为待批） 02:已批（发起人和协办人直接领导都批准） 03: 已退回

			int p1021 = 2;//2: 跨团队外部协作
			if(util.isInTeamCooperationTask(p0800)){
				p1021 = 1;//1: 团队内部协作
			}
			vo.setInt("p1021", p1021);//协作任务类型 1:团队内 2：跨团队

			dao.addValueObject(vo);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return succeed;
	}
	/**
	 * 是否已存在该协办任务
	 * @param p0800
	 * @return
	 * chent
	 */
	public boolean isHasCooperationTask(int p0800){
		boolean isExist = false;

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql = "select count(p1001) as count from p10 where p0800=?";
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(p0800);
			rs = dao.search(sql, list);
			if (rs.next() && rs.getInt("count")>0){
				isExist = true;
			}

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return isExist;
	}
	/**
	 * 获取协办人Guidkey（协办表：p10中取）
	 * @param p0800
	 * @return
	 * chent
	 */
	public String getCooperationPersonGuidKey(int p0800){
		String guidkey = "";

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql = "select guidke_owner from p10 where p0800=?";
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(p0800);
			rs = dao.search(sql, list);
			if (rs.next()){
				guidkey = rs.getString("guidke_owner");
			}

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return guidkey;
	}

	/**
	 * 删除协办任务表中数据
	 * @param p0800
	 * @return
	 */
	public boolean deleteCooperationTask(int p0800){
		boolean succeed = false;

		ContentDAO dao = new ContentDAO(this.conn);
		try{
			String sql = "delete from p10 where p0800=?";
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(p0800);
			int result = dao.delete(sql, list);
			if (result == 1){
				succeed = true;
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return succeed;
	}

	/**
	 * 查看协作任务的审批状态（P1019）
	 * @param p0800
	 * @return：01: 待批 02: 已批 03: 已退回 -1:协办任务表中不存在
	 * chent
	 */
	public String getCooperationTaskStatus(int p0800){
		String status = "-1";

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql = "select p1019 from p10 where p0800=?";
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(p0800);
			rs = dao.search(sql, list);
			if (rs.next()){
				status = rs.getString("p1019");
			}

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return status;
	}

	/**
	 * 更新协办任务表审批状态
	 * @param p0800  任务号
	 * @param p1019  01: 待批 02: 已批 03: 已退回
	 * @return
	 * chent
	 */
	public boolean updateCooperationTaskStatus(int p0800, String p1019){
		boolean succeed = false;

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql = "update p10 set p1019=? where p0800=?";
			ArrayList list = new ArrayList();
			list.add(p1019);
			list.add(p0800);
			int result = dao.update(sql, list);
			if (result == 1){
				succeed = true;
			}

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return succeed;
	}
	/**
	 * 上级操作下级任务时，协办任务通知处理
	 * @param p0800
	 * chent
	 */
	public void SuperiorOperation(int p0800){

		WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
		try {

			if(util.isOpenCooperationTask() && util.isCooperationTask(p0800, false)){//启用协作任务并且是协作任务

				String startPerson = util.getTaskRolePersionList(p0800, 5).get(0);//任务发起人
				String startPersonName = util.getUserNameByA0100(startPerson.substring(0, 3), startPerson.substring(3));//任务发起人名字
				String startPersonDirect = util.getMyDirectSuperPerson(startPerson.substring(0, 3), startPerson.substring(3)); //发起人直接上级
				String startPersonDirectName = util.getUserNameByA0100(startPersonDirect.substring(0, 3), startPersonDirect.substring(3));// 发起人直接上级的名字
				String privPerson = util.getTaskRolePersionList(p0800, 1).get(0);//任务负责人
				String privPersonName = util.getUserNameByA0100(privPerson.substring(0, 3), privPerson.substring(3));// 协办人的名字
				String privPersonDirect = util.getMyDirectSuperPerson(privPerson.substring(0, 3), privPerson.substring(3)); //任务负责人直接上级
				String privPersonDirectName = util.getUserNameByA0100(privPersonDirect.substring(0, 3), privPersonDirect.substring(3));// 协办人直接上级的名字
				util.sendPending_ToCooperationTask(this.userView.getUserName(), privPersonDirectName, String.valueOf(p0800));
				if(util.isInTeamCooperationTask(p0800)){// 团队内协办
					// 通过任务号初始化协办表。发布协办任务：直接审批,审批状态（P1019）直接为已批（02）
					this.deleteCooperationTask(p0800);
					this.newCooperationTask(p0800, true);

					HashMap templateInfoTopriPerson = util.getEmailTemplateInfo(4);//发给协办人的邮件模板
					String title = (String)templateInfoTopriPerson.get("title");
					String hrefDesc = (String)templateInfoTopriPerson.get("hrefDesc");
					String bodyText = (String)templateInfoTopriPerson.get("bodyText");
					bodyText = bodyText.replace("{mark1}", privPersonName);
					bodyText = bodyText.replace("{mark2}", startPersonDirectName);
					bodyText = bodyText.replace("{mark3}", (new PlanTaskBo(this.conn,this.userView).getTask(p0800).getString("p0801")));
					String emailUrl = this.getRemindEmail_MyCooperationTaskHref(privPerson);
					util.sendEmaiAndWeiXin(title, bodyText, hrefDesc, emailUrl, privPerson);

				} else {// 跨团队协办
					/** 生成一条新的协作待办申请、发邮件 */
					util.update_cooperationTask("1", String.valueOf(p0800));// 原待办置为已办
					util.sendPending_ToCooperationTask(this.userView.getUserName(), privPersonDirectName, String.valueOf(p0800));// 发待办

					// 发邮件、微信
					HashMap<String, String> emailInfoMap = util.getEmailTemplateInfo(1);
					String bodyText = emailInfoMap.get("bodyText");
					bodyText = bodyText.replace("{mark1}", privPersonDirectName);
					bodyText = bodyText.replace("{mark2}", startPersonName);
					bodyText = bodyText.replace("{mark3}", (new PlanTaskBo(this.conn,this.userView).getTask(p0800).getString("p0801")));//任务名称

					String emailUrl = this.getRemindEmail_CooperationTaskHref(privPersonDirect);
					util.sendEmaiAndWeiXin(emailInfoMap.get("title"), bodyText, emailInfoMap.get("hrefDesc"), emailUrl, privPersonDirect);


					/** 发布协办任务 */
					this.deleteCooperationTask(p0800);
					this.newCooperationTask(p0800, false);// 发布协办任务
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 业务用户时，获取业务用户的全称(this.userView.getUserFullName()这个方法只会得到自助用户的全称)
	 * @return
	 */
	public String getUserFullName(){
		String userFullName = "";

		ContentDAO dao = null;
		RowSet rs = null;
		try{
			int status = this.userView.getStatus();
			if(status == 0){// 业务用户

				String sql = "select fullName from OperUser where UserName=?";
				ArrayList<String> list = new ArrayList<String>();
				list.add(this.userView.getUserName());

				dao = new ContentDAO(this.conn);
				rs = dao.search(sql, list);
				if(rs.next()){
					userFullName = rs.getString(1);
				}

			} else if(status == 2){//自助用户
				userFullName = this.userView.getUserFullName();
			}

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return userFullName;
	}

	/**
     * 复制任务时校验参与的人员身份信息是否变动
     * @param plan_id
     * @returnArrayList 返回 没有 变动信息的负责人
     */
    public HashMap checkCopyPersonChange(String tasks_id) throws GeneralException{

    	HashMap map = new HashMap();
    	if(StringUtils.isEmpty(tasks_id)) {
            return map;
        }

    	RowSet rs = null;
    	StringBuffer info = new StringBuffer("");
    	try{
    		/*linbz
	         * 查询复制的任务之前先校验所选计划的任务中是否有人员信息变动
	         * 返回负责人和任务成员的未变动map
	         * */
    		ArrayList sqllist = getCheckPersonChangeSqlByTaskid(tasks_id);
    		StringBuffer sql = new StringBuffer("");
    		LazyDynaBean abean = new LazyDynaBean();
    		//处理负责人未变动信息
	    	if(StringUtils.isNotEmpty(sqllist.get(0).toString())) {
	    		sql.setLength(0);
	    		sql.append("select * from(").append(sqllist.get(0).toString()).append(") q where q.P0905=1 ");
	    		rs = dao.search(sql.toString(), new ArrayList());
	    		while(rs.next()) {
	    			String nbase = rs.getString("nbase");
	    			String a0100 = rs.getString("a0100");
	    			abean = new LazyDynaBean();
	    			abean.set("p0907", rs.getString("p0907"));
	    			abean.set("p0909", rs.getString("p0909"));
	    			abean.set("p0911", rs.getString("p0911"));
	    			abean.set("p0913", rs.getString("p0913"));
	    			if(StringUtils.isNotEmpty(nbase) && StringUtils.isNotEmpty(a0100)) {
	    				map.put(nbase+a0100, abean);
	    			}
	    		}
	    	}

	    	//暂时注掉此处代码  无条件复制上期成员  haosl  delte 2018-3-1
	    	/*//任务成员未变动信息
	    	if(StringUtils.isNotEmpty(sqllist.get(1).toString())) {
	    		sql.setLength(0);
	    		sql.append("select * from(").append(sqllist.get(1).toString()).append(") q where q.P0905=1 ");
	    		rs = dao.search(sql.toString(), new ArrayList());
	    		while(rs.next()) {
	    			String nbase = rs.getString("nbase");
	    			String a0100 = rs.getString("a0100");
	    			if(map.containsKey(nbase+"a0100")) {
	    				continue;
	    			}
	    			abean = new LazyDynaBean();
	    			abean.set("p0907", rs.getString("p0907"));
	    			abean.set("p0909", rs.getString("p0909"));
	    			abean.set("p0911", rs.getString("p0911"));
	    			abean.set("p0913", rs.getString("p0913"));
	    			if(StringUtils.isNotEmpty(nbase) && StringUtils.isNotEmpty(a0100)) {
	    				map.put(nbase+a0100, abean);
	    			}
	    		}
	    	}*/
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
            PubFunc.closeDbObj(rs);
        }
    	return map;
    }

    /**
     * 发布时校验参与的人员身份信息是否变动
     * @param plan_id
     * @return
     */
    public String checkPersonChange(String plan_id) throws GeneralException{

    	RowSet rs = null;
    	StringBuffer info = new StringBuffer("");
    	try{
	    	ArrayList sqllist = getCheckPersonChangeSql(plan_id);
	    	//linbz 校验是否属于OKR库范围内的人员
	    	if(StringUtils.isNotEmpty(sqllist.get(0).toString())) {
		    	rs = dao.search(sqllist.get(0).toString(), new ArrayList());
		    	info.append(getChangeInfo(rs,"1"));
	    	}
	    	//人员移库或删除的
	    	if(StringUtils.isNotEmpty(sqllist.get(1).toString())) {
	    		rs = dao.search(sqllist.get(1).toString(), new ArrayList());
		    	info.append(getChangeInfo(rs,"2"));
	    	}


    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
            PubFunc.closeDbObj(rs);
        }
    	return info.toString();
    }

    /**
     * 通过计划id 获取人员变动的SQL语句
     * @param plan_id
     * @return
     */
    public ArrayList getCheckPersonChangeSql(String plan_id) throws GeneralException{

    	ArrayList list = new ArrayList();
    	try{
    		//linbz 取人员范围条件参数
    		RecordVo paramsVo=ConstantParamter.getConstantVo("OKR_CONFIG");
    		WorkPlanConfigBo bo = new WorkPlanConfigBo(this.conn, this.userView);
    		String xmlValue = "";
    		Map mapXml = new HashMap();
    		// 有缓存则取缓存数据
    		if(null != paramsVo){
    			xmlValue = paramsVo.getString("str_value");
    		}
    		mapXml = bo.parseXml(xmlValue);
    		String dbValues = mapXml.get("nbases")==null?"":(String)mapXml.get("nbases");
    		String[] nbaseArry=dbValues.split(",");
    		if(nbaseArry.length<1){
    			throw new GeneralException("未设置OKR人员库范围！");
    		}

    		StringBuffer q09sql = new StringBuffer("");
    		q09sql.append("select nbase, a0100, p0905, p0913, p0903, p0801, p0900, p0800 ");
    		q09sql.append(" from p09, p08 ");
    		q09sql.append(" where P0700=").append(plan_id);
    		q09sql.append(" and P0903=p0800 ");
    		q09sql.append(" and P0901 = 2 ");
    		q09sql.append(" and (p0905=1 or p0905=2) ");

    		StringBuffer changesql = new StringBuffer("");
    		StringBuffer nbases = new StringBuffer("");
    		for(int i=0;i<nbaseArry.length;i++) {
    			String nbs = nbaseArry[i];
    			if(StringUtils.isNotEmpty(nbs)) {
    				nbases.append("'").append(nbs.toUpperCase()).append("'");

    				changesql.append(" select a0000 ,b.* ");
    				changesql.append(" from ").append(nbs).append("A01 a ");
    				changesql.append(" right join (");
    				changesql.append(q09sql.toString());
    				changesql.append(" and UPPER(nbase) = '").append(nbs.toUpperCase()).append("' ) b ");
    				changesql.append(" on a.A0100=b.A0100 ");
    				changesql.append("where 1=1 and a.A0000 is null ");

    				if(i<nbaseArry.length-1) {
    					nbases.append(",");
    					changesql.append(" union all ");
    				}
    			}
    		}

	    	StringBuffer sqlnbs = new StringBuffer("");
	    	sqlnbs.append(q09sql.toString());
	    	sqlnbs.append(" and UPPER(nbase) not in("+nbases.toString()+") ");
	    	// 32886 发布时人员变动提示信息增加排序
	    	sqlnbs.append(" order by p08.p0800 ");
	    	//校验是否属于OKR库范围内的人员SQL
	    	list.add(sqlnbs.toString());

	    	//校验人员移库或删除的SQL
	    	StringBuffer chsql = new StringBuffer("");
	    	// 32886 发布时人员变动提示信息增加排序
	    	if(StringUtils.isNotEmpty(changesql.toString())) {
	    		chsql.append(" select * from (");
	    		chsql.append(changesql.toString());
	    		chsql.append(") p  order by p0800 ");

	    	}
	    	list.add(chsql.toString());

    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return list;
    }

    /**
     * 获取变动信息的人员数据
     * @param rs
     * @param valType='1' 校验okr权限范围 ,='2' 校验人员是否移库或删除
     * @return
     * @throws GeneralException
     */
    public String getChangeInfo(RowSet rs,String valType) throws GeneralException{
    	StringBuffer info = new StringBuffer("");
    	try{
    		LazyDynaBean infoBean = new LazyDynaBean();
    		while(rs.next()){
	    		infoBean = new LazyDynaBean();
	    		String nbase = rs.getString("nbase");
	    		String a0100 = rs.getString("a0100");
	    		String perName = rs.getString("p0913");
        		String taskName = rs.getString("p0801");
        		String perType = rs.getString("p0905");
        		String type = "1".equals(perType)?"负责人":"参与人";
        		if("1".equals(valType)) {
        			info.append("【").append(perName).append("】不在“参数配置/填报人员/人员范围设置”的人员库范围内,请重新指定 【").append(taskName).append("】任务的").append(type).append("！</br>");
        		} else {
        			info.append("【").append(perName).append("】已移库或者被删除，请重新指定【").append(taskName).append("】任务的").append(type).append("！</br>");
        		}
	    	}

    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return info.toString();
    }
    /**
     * 通过任务id 获取 未 变动的人员信息SQL语句
     * @param task_ids  1231,2331,2312
     * @return
     */
    public ArrayList getCheckPersonChangeSqlByTaskid(String task_ids) throws GeneralException{

    	ArrayList list = new ArrayList();
    	try{
    		//linbz 取人员范围条件参数
    		RecordVo paramsVo=ConstantParamter.getConstantVo("OKR_CONFIG");
    		WorkPlanConfigBo bo = new WorkPlanConfigBo(this.conn, this.userView);
    		String xmlValue = "";
    		Map mapXml = new HashMap();
    		// 有缓存则取缓存数据
    		if(null != paramsVo){
    			xmlValue = paramsVo.getString("str_value");
    		}
    		mapXml = bo.parseXml(xmlValue);
    		String dbValues = mapXml.get("nbases")==null?"":(String)mapXml.get("nbases");
    		String[] nbaseArry=dbValues.split(",");
    		if(nbaseArry.length<1){
    			throw new GeneralException("未设置OKR人员库范围！");
    		}

    		StringBuffer q09sql = new StringBuffer("");
    		q09sql.append("select * ");
    		q09sql.append(" from p09 ");
    		q09sql.append(" where P0903 in(").append(task_ids).append(") ");
    		q09sql.append(" and P0901 = 2 ");
    		q09sql.append(" and (p0905=1 or p0905=2) ");

    		StringBuffer changesql = new StringBuffer("");
    		StringBuffer nbases = new StringBuffer("");
    		for(int i=0;i<nbaseArry.length;i++) {
    			String nbs = nbaseArry[i];
    			if(StringUtils.isNotEmpty(nbs)) {
    				nbases.append("'").append(nbs.toUpperCase()).append("'");

    				changesql.append(" select a0000 ,b.* ");
    				changesql.append(" from ").append(nbs).append("A01 a ");
    				changesql.append(" right join (");
    				changesql.append(q09sql.toString());
    				changesql.append(" and UPPER(nbase) = '").append(nbs.toUpperCase()).append("' ) b ");
    				changesql.append(" on a.A0100=b.A0100 ");
    				changesql.append("where 1=1 and a.A0000 is not null ");

    				if(i<nbaseArry.length-1) {
    					nbases.append(",");
    					changesql.append(" union all ");
    				}
    			}
    		}

	    	StringBuffer sqlnbs = new StringBuffer("");
	    	sqlnbs.append(q09sql.toString());
	    	sqlnbs.append(" and UPPER(nbase) in("+nbases.toString()+") ");
	    	//OKR库范围未变动人员SQL
	    	list.add(sqlnbs.toString());
	    	//校验人员未变动sql
	    	list.add(changesql.toString());

    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return list;
    }

    /**
     * 获得p09表里的负责人信息
     * @param p0800
     * @return
     * @throws Exception
     */
    public LazyDynaBean getDirectorByP09(int p0800) throws Exception {
		List m = new ArrayList();
		LazyDynaBean bean = null;
		String sql = "SELECT * FROM p09 WHERE p0903="+p0800+" AND p0905=1";
		try {
			ContentDAO dao = new ContentDAO(conn);
			List<LazyDynaBean> list = dao.searchDynaList(sql);
			if(list != null && list.size()>0) {
                bean = list.get(0);
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return bean;
	}

    /**
     * 校验填报期间范围，控制填报计划
     * @param periodWeek2
     * @param periodMonth2
     * @param periodYear2
     * @param periodType2
     * @param validPre
     * @param validNow
     */
	public boolean validPreNow(String periodType, String periodYear, String periodMonth, String periodWeek, int validPre, int validNow) {
		boolean fillPlan = false;//是否可以填报计划 false 不能填报
		Calendar curTime = Calendar.getInstance();//当前时间
       Map<String,Calendar> map = getPeriodDateRange(periodType,periodYear,periodMonth,periodWeek);
        Calendar start = map.get("start");
        Calendar end = map.get("end");
		//填报日期在填报范围内允许填报，否则不可填报
		start.add(Calendar.DATE,-validPre);
		end.add(Calendar.DATE,validNow);
		if(start.before(curTime) && curTime.before(end)) {
			fillPlan = true;
		}
		return fillPlan;
	}

    /**
     * 获得期间日期的开始和结束日期
     * @param periodType
     * @param periodYear
     * @param periodMonth
     * @param periodWeek
     * @return
     */
	private Map<String,Calendar> getPeriodDateRange(String periodType, String periodYear, String periodMonth, String periodWeek){
        Calendar calPre = Calendar.getInstance();//期间起始 日期
        Calendar calNow = Calendar.getInstance();//期间结束 日期
        if(WorkPlanConstant.Cycle.YEAR.equals(periodType)) {//year
            //根据年末的时间计算期间的起始结束日期
            calPre.set(Integer.valueOf(periodYear)-1,Calendar.DECEMBER, 31);
            calNow.set(Integer.valueOf(periodYear), Calendar.JANUARY, 1);
        }else if(WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)) {//半年
            if("1".equals(periodMonth)) {
                calPre.set(Integer.valueOf(periodYear)-1, Calendar.DECEMBER, 31);
                calNow.set(Integer.valueOf(periodYear), Calendar.JANUARY, 1);
            }else if("2".equals(periodMonth)){
                calPre.set(Integer.valueOf(periodYear), Calendar.JUNE, 30);
                calNow.set(Integer.valueOf(periodYear), Calendar.JULY, 1);
            }
        }else if(WorkPlanConstant.Cycle.QUARTER.equals(periodType)) {//季度
            if("1".equals(periodMonth)) {
                calPre.set(Integer.valueOf(periodYear)-1, Calendar.DECEMBER, 31);
                calNow.set(Integer.valueOf(periodYear), Calendar.JANUARY, 1);
            }else if("2".equals(periodMonth)){
                calPre.set(Integer.valueOf(periodYear), Calendar.MARCH, 31);
                calNow.set(Integer.valueOf(periodYear), Calendar.APRIL, 1);
            }else if("3".equals(periodMonth)) {
                calPre.set(Integer.valueOf(periodYear), Calendar.JUNE, 30);
                calNow.set(Integer.valueOf(periodYear), Calendar.JULY, 1);
            }else if("4".equals(periodMonth)) {
                calPre.set(Integer.valueOf(periodYear), Calendar.SEPTEMBER, 30);
                calNow.set(Integer.valueOf(periodYear), Calendar.OCTOBER, 1);
            }
        }else if(WorkPlanConstant.Cycle.MONTH.equals(periodType)) {
            if("1".equals(periodMonth)) {
                calPre.set(Integer.valueOf(periodYear)-1, Calendar.DECEMBER, 31);
            } else{
                calPre.set(Integer.valueOf(periodYear),Integer.valueOf(periodMonth)-1, 1);
                calPre.add(Calendar.DATE,-1);
            }
            calNow.set(Integer.valueOf(periodYear),Integer.valueOf(periodMonth)-1, 1);
        }else if(WorkPlanConstant.Cycle.WEEK.equals(periodType)) {
            WorkPlanSummaryBo wpsBo= new WorkPlanSummaryBo(null, this.conn);
            String startDate = wpsBo.getMondayOfDate(Integer.parseInt(periodYear),
                    Integer.parseInt(periodMonth), Integer.parseInt(periodWeek));
            String endDate = wpsBo.getSunDayOfDate(Integer.parseInt(periodYear),
                    Integer.parseInt(periodMonth), Integer.parseInt(periodWeek));
            calPre.setTime(DateUtils.getDate(startDate, "yyyy-MM-dd"));
            calNow.setTime(DateUtils.getDate(endDate, "yyyy-MM-dd"));
        }

        Map<String,Calendar> map = new HashMap();
        map.put("start",calPre);
        map.put("end", calNow);
        return map;
    }
}
