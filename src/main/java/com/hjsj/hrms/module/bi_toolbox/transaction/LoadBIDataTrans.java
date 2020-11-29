package com.hjsj.hrms.module.bi_toolbox.transaction;

import com.hjsj.hrms.businessobject.general.template.MatterTaskList;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.infor.PersonMatterTask;
import com.hjsj.hrms.businessobject.kq.interfaces.KqMatterTask;
import com.hjsj.hrms.businessobject.report.report_isApprove.Report_isApproveBo;
import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hjsj.hrms.module.recruitment.util.ZpPendingtaskBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * BI工具箱交易类
 * @author zhangh
 */
public class LoadBIDataTrans extends IBusiness {
    private int total_num = 0;
    private  EncryptLockClient lock = null;
    @Override
    /**
     * 获取menu.xml配置文件中领导桌面的配置数据
     * menu可能会有多层，使用递归获取
     */
    public void execute() throws GeneralException {
        {
            String menuData = "";
            JSONObject jsonObject = new JSONObject();
            //获取锁
            this.lock = (EncryptLockClient)SystemConfig.getServletContext().getAttribute("lock");
            //验证是否有领导桌面的锁权限，也就是是否有买这个模块，固定为34
            boolean haveModule =  this.lock.isBmodule(34,userView.getUserName());
            if(!haveModule){
                jsonObject.put("return_code","fail");
                jsonObject.put("return_msg","没有模块的权限，请先购买该模块！");
                menuData = jsonObject.toString();
                this.getFormHM().put("menuData",menuData);
                return ;
            }
            //判断是否还有可用的点数
            boolean havePoint = this.lock.addUser(userView.getUserName(),34)  ;
            if(!havePoint){
                jsonObject.put("return_code","fail");
                jsonObject.put("return_msg","没有可用的点数，请稍后重试！");
                menuData = jsonObject.toString();
                this.getFormHM().put("menuData",menuData);
                return;
            }
            String xpath="/hrp_menu/menu";
            String menuid = "";
            String name = "";
            String function_id = "";
            //是否有菜单权限
            boolean haveFunc = false;
            jsonObject.put("return_code", "success");
            jsonObject.put("return_msg", "成功");
            //检查当前用户是否有待办
            boolean haveTask = false;
            haveTask = isHaveTask();
            jsonObject.put("haveTask",haveTask);
            try
            {
                MenuMainBo menuMainBo  = new MenuMainBo();
                Document doc = menuMainBo.getDocument();
                // 取得根节点
                XPath reportPath = XPath.newInstance(xpath);
                List list=reportPath.selectNodes(doc);
                for (int i = 0; i < list.size(); i++)
                {
                    Element node = (Element) list.get(i);
                    //获取菜单ID
                    menuid=node.getAttributeValue("id");
                    //获取菜单名称
                    name=node.getAttributeValue("name");
                    //获取领导桌面的菜单数据，菜单号为21
                    if("21".equalsIgnoreCase(menuid))
                    {
                        MenuBean bean = anasisMenu(node);
                        menuData = "{return_code:'success',return_msg:'成功',haveTask:"+haveTask+",return_data:{menuList:[{";
                        if(StringUtils.isNotBlank(bean.toString())){
                            menuData += bean.toString();
                            menuData += "}]}}";
                        }else{
                            jsonObject.clear();
                            jsonObject.put("return_code", "fail");
                            jsonObject.put("return_msg", "没有菜单权限，请先获取权限！");
                            menuData = jsonObject.toString();
                        }
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
            }catch(Exception e)
            {
                jsonObject.clear();
                jsonObject.put("return_code", "fail");
                jsonObject.put("return_msg", "系统异常，请联系系统管理员！");
                menuData = jsonObject.toString();
                e.printStackTrace();
            }finally{

            }
            this.getFormHM().put("menuData",menuData);
        }
    }

    /**
     * 通过递归的方式解析menu菜单
     * @param node
     * @return
     */
    private MenuBean anasisMenu(Element node) {
        MenuBean bean = new MenuBean();
        List<MenuBean> list = new ArrayList<MenuBean>();
        String name = "";
        String menuid = "";
        String url = "";
        String func_id = "";
        String mod_id = "";
        String menuhide = "";
        //检查是否有菜单权限
        boolean havaFunc = false;
        name = node.getAttributeValue("name");
        menuid = node.getAttributeValue("id");
        url = node.getAttributeValue("url");
        func_id = node.getAttributeValue("func_id");
        mod_id = node.getAttributeValue("mod_id");
        menuhide = node.getAttributeValue("menuhide");
        //先进行版本功能控制,连这个都没有，直接返回，就不用判断用户权限了
        if(StringUtils.isNotBlank(mod_id)){
            havaFunc =  haveFuncPriv(func_id,mod_id , this.lock);
            if(!havaFunc){
                return  bean;
            }
        }
        //超级管理员不用检查权限，菜单没设置权限的也不用检查
        if(userView.isSuper_admin()||StringUtils.isBlank(func_id)||this.userView.hasTheFunction(func_id)){
            havaFunc = true;
        }
        if(!havaFunc){
            return bean;
        }
        //【57262】领导桌面与帆软报表集成：页面显示未将后台配置menu.xml中menuhide="false"的菜单项屏蔽掉
        if(StringUtils.isNotBlank(menuhide)&&"false".equalsIgnoreCase(menuhide)){
            return bean;
        }
        bean.setName(name);
        bean.setMenuid(menuid);
        bean.setUrl(url);
        List nodeList = node.getChildren("menu");
        Element chileElement = null;
        for(int x=0;x<nodeList.size();x++) {
            chileElement = (Element) nodeList.get(x);
            MenuBean tempBean = anasisMenu(chileElement);
            if(StringUtils.isNotBlank(tempBean.getMenuid())){
                list.add(tempBean);
            }
        }
        if(list!=null&&list.size()>0){
            bean.setChildren(list);
        }
        return  bean;
    }

    /**
     * 检查当前用户是否有待办需要处理
     * @return
     */
    private  boolean isHaveTask(){
    	String _static="static";
    	if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
    		_static="static_o";
    	}
        boolean haveTask = false;
        {
            try {
                StringBuffer strsql = new StringBuffer();
                String format_str = "yyyy-MM-dd HH:mm";
                if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    format_str = "yyyy-MM-dd hh24:mi";
                }
                strsql.append(
                        "select U.ins_id,case when T.task_topic like '%共0%' then U.name  else T.task_topic end name,U.tabid,U.actorname fullname, U.b0110  unitname,a0101, task_state finished ,"
                                + Sql_switcher.dateToChar("U.start_date", format_str) + " as ins_start_date,"
                                + Sql_switcher.dateToChar("T.end_date", format_str)
                                + " as ins_end_date,T.actor_type,T.actorname,T.task_id ");
                strsql.append(
                        ",U.actor_type actortype,case when (select count(1) from t_wf_task t1  where  t1.task_type='2' and T1.ins_id=u.ins_id and t1.bread=1)>0 then 0  else 1 end  recallflag ");
                strsql.append("from t_wf_task T,t_wf_instance U,template_table tt ");
                strsql.append(" where T.ins_id=U.ins_id ");
                strsql.append(" and  task_topic not like '%共0人%' and  task_topic not like '%共0条%' ");
                strsql.append("  and  U.tabid=tt.tabid and tt."+_static+"!=10 and tt."+_static+"!=11  ");
                strsql.append(" and ( task_type='2' and finished='2' and ( task_state='3'  or task_state='6' ) "
                        + " or (T.task_type='9' and  T.task_state='5') "
                        + " or ( T.task_type='9' and  T.task_state='4' ) )");
                strsql.append(" and (");
                strsql.append(getInsFilterWhere());
                strsql.append(")");
                strsql.append(" and " + Sql_switcher.isnull("T.bs_flag", "'1'") + "='1'  ");

                List<LazyDynaBean> list =null;
                if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                    list= ExecuteSQL.executeMyQuery(
                            " select top 5 * from (" + strsql.toString() + ")mydeclare order by ins_start_date desc ",
                            this.frameconn);
                }else {
                    list=ExecuteSQL.executeMyQuery(
                            " select  *  from (" + strsql.toString() + " order by ins_start_date desc )mydeclare  where rownum<=5 ",
                            this.frameconn);
                }

                if(list!=null&&list.size()>0) {
                    String date_time=(String)list.get(0).get("ins_start_date");
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal=Calendar.getInstance();
                    cal.setTime(sdf.parse(date_time));
                    long times=cal.getTimeInMillis();
                    cal.setTime(new Date());
                    long times_now=cal.getTimeInMillis();
                    if(times_now-times<=15552000000L) {//判断最近一条记录是否是半年内记录
                        this.getFormHM().put("isFirstLoad",true);
                    }else {
                        this.getFormHM().put("isFirstLoad",false);
                    }
                }else {
                    this.getFormHM().put("isFirstLoad",false);
                }

                for (LazyDynaBean bean : list) {
                    String task_id = (String) bean.get("task_id");
                    bean.set("task_id_e", PubFunc.encrypt(task_id));
                    String finished = (String) bean.get("finished");
                    CodeItem item= AdminCode.getCode("38", finished);
                    if(item!=null) {
                        bean.set("finished", finished+"`"+item.getCodename());
                    }else {
                        bean.set("finished", finished+"`"+"");
                    }

                    String encrypt = PubFunc.encrypt("&tab_id=" + (String) bean.get("tabid") + "&task_id="
                            + PubFunc.encrypt(task_id) + "&module_id=1&ins_id=" + (String) bean.get("ins_id")
                            + "&approve_flag=0&sp_flag=1&return_flag=3&callBack_close=function(){window.parent.Ext.getCmp(\"serviceHallWin\").destroy( );}");// bug 48449
                    bean.set("encrypt", encrypt);
                }
                String html=getMatter();
                if(this.total_num>0){
                    haveTask = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  haveTask;
    }
    private String getInsFilterWhere() {
        StringBuffer strwhere = new StringBuffer();
        String dbpre = this.userView.getDbname(); // 库前缀
        String userid = dbpre + this.userView.getA0100();// 人员编号
        if (userid == null || userid.length() == 0)
            userid = "-1";
        strwhere.append(" ( upper(U.actorid) in ('");
        strwhere.append(userid.toUpperCase());
        if(this.userView.getStatus()!=4) {//业务用户登录查看我的申请
            strwhere.append("','");
            strwhere.append(this.userView.getUserName().toUpperCase());
        }
        strwhere.append("'))");
        return strwhere.toString();
    }

    /**
     *
     * @param list
     * @param view_base
     * @param target
     * @return
     * @throws Exception
     */
    public String getTdHtml(ArrayList list,int view_base,String target) throws Exception {
        StringBuffer sbf=new StringBuffer();
        List<CommonData> emptyDate_list=new ArrayList<CommonData>();//为空的日期默认放到最后
        List<CommonData> date_list=new ArrayList<CommonData>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for(Object obj:list) {
            //数据中日期为空的数据过滤出来
            CommonData date=(CommonData)obj;
            if(date.containsKey("date")&&date.get("date")!=null) {
                String date_str=(String)date.get("date");
                date_str=date_str.replace(".", "-");//日期格式统一改为 yyyy-MM-dd HH:mm
                String hour_min="";
                int hour=new Date().getHours();
                hour_min+=hour<10?" 0"+hour+":":" "+hour+":";
                int mintue=new Date().getMinutes();
                hour_min+=mintue<10?"0"+mintue+"":mintue+"";
                if(date_str.length()==10) {//日期长度不够处理
                    date_str+=hour_min;
                }
                if(date_str.length()==19) {//日期带秒格式处理
                    date_str=date_str.substring(0, date_str.lastIndexOf(":"));
                }
                if(date_str.endsWith("00:00")) {
                    date_str=date_str.replace("00:00", hour_min);
                }
                date.put("date_L", sdf.parse(date_str).getTime());
                date.put("date",date_str);
                date_list.add(date);
            }else {
                emptyDate_list.add(date);
            }
        }

        for(int i=0;i<date_list.size()-1;i++) {
            for(int j=0;j<date_list.size()-1-i;j++) {
                CommonData date_now=date_list.get(j);
                long time_now=(Long)date_now.get("date_L");
                CommonData date_next=date_list.get(j+1);
                long time_next=(Long)date_next.get("date_L");
                if(time_now<time_next) {
                    date_list.set(j, date_next);
                    date_list.set(j+1, date_now);
                }
            }
        }
        date_list.addAll(emptyDate_list);
        this.total_num=date_list.size();
        for(Object obj:date_list) {
            view_base++;
            String date="";
            if(view_base>5)
                break;
            String title="";
            String url="";
            CommonData data=(CommonData)obj;
            title=data.getDataName();
            url=data.getDataValue();
            if(data.containsKey("date"))
                date=(String)data.get("date");
            if(StringUtils.isNotEmpty(url)&&url.indexOf("encryptParam")==-1) {
                int index = url.indexOf("&");
                if(index>-1){
                    String allurl = url.substring(0,index);
                    String allparam = url.substring(index);
                    url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
                }
            }
            sbf.append("		<tr class=\"\"\"><td style=\"height:32px;width:60%;font-size:14px \" class=\"RecordRowPo\">");
            sbf.append("			<a  href=\""+ url+"\" target=\""+target+"\">"+title+"</a>");
            sbf.append("		</td><td style=\"font-size:14px\" >"+date+"</td></tr>\n");
        }
        return sbf.toString();
    }

    private String getMatter() throws GeneralException {
        StringBuffer sbf=new StringBuffer("");
        String bosflag = userView.getBosflag();
        boolean isagent = userView.isBAgent();
        String target = "_self";
        int view_base = 0;
        if ("bi".equalsIgnoreCase(bosflag))
            target = "i_body";
        try {
            ZpPendingtaskBo zpbo = new ZpPendingtaskBo(this.frameconn, userView);
            ArrayList zpdatalist = zpbo.getZpapprDta();


            MatterTaskList matterTaskList = new MatterTaskList(this.frameconn, userView);
            matterTaskList.setReturnflag("8");
            ArrayList matterList = matterTaskList.getPendingTask();// new ArrayList(); 20160513 dengc
            matterList.addAll(zpdatalist);
            KqMatterTask kqMatterTask = new KqMatterTask(this.frameconn, userView);
            // 考勤刷卡审批
            matterList = kqMatterTask.getKqCardTask(matterList);
            // 加班申请审批待办
            matterList = kqMatterTask.getKqOvertimeTask(matterList);
            // OKR 应李群要求 okr待办挪到前面
            LazyDynaBean abean = new LazyDynaBean();
            CommonData cData = null;
            ArrayList okrList = matterTaskList.getOKRPending();
            if (okrList != null) {
                int okrCooperationTaskNum = 0;// okr协办任务计数
                CommonData okrCooperationTaskData = new CommonData();
                for (int i = 0; i < okrList.size(); i++) {
                    abean = (LazyDynaBean) okrList.get(i);
                    String name = (String) abean.get("name");
                    if ("部门协作任务申请".equals(name)) {// okr协作任务待办合并 chent 20160623
                        okrCooperationTaskNum += 1;
                        okrCooperationTaskData.setDataName(name + "(" + okrCooperationTaskNum + ")");
                        okrCooperationTaskData.setDataValue((String) abean.get("url"));
                        continue;
                    }
                    cData = new CommonData();
                    cData.setDataName(name);
                    cData.setDataValue((String) abean.get("url"));
                    cData.put("date", (String)abean.get("date"));
                    matterList.add(cData);
                }
                if (okrCooperationTaskNum > 0) {
                    matterList.add(okrCooperationTaskData);
                }
            }

            // 我的工作纪实
            matterTaskList.setReturnURL("/templates/index/portal.do?b_query=link");
            matterTaskList.setTarget("_self");
            if ("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {// 国家电网，代办只要绩效的
                // 代理人则不显示 JiangHe
                if (!isagent) {
                    matterList = matterTaskList.getPerformancePending(matterList);
                }
            } else {
                // 代理人则不显示 JiangHe
                if (!isagent) {
                    // 只有干警考核才有工作纪实
                    if (SystemConfig.getPropertyValue("clientName") != null
                            && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) {
                        matterList = matterTaskList.getWorkPlanList(matterList);
                    }
                }
                matterList = matterTaskList.getWaitTaskList(matterList);
                matterList = matterTaskList.getTmessageList(matterList);
                matterList = matterTaskList.getPerformancePending(matterList);
            }
            SalaryPkgBo salaryPkgBo = new SalaryPkgBo(this.frameconn, userView);
            ArrayList<LazyDynaBean> salarylist = salaryPkgBo.getGzPending(); // 审批薪资 读取待办表中数据 zhaoxg add 2014-7-25
            matterList.addAll(deatilList(salarylist));

            // ------------------------报表审批 zhaoxg 2013-1-28--------------------------------
            Report_isApproveBo report_isApproveBo = new Report_isApproveBo(this.frameconn, userView);
            ArrayList approveList = new ArrayList();
            approveList = report_isApproveBo.getApprovelist(approveList);
            matterList.addAll(deatilList(approveList));

            ArrayList returnList = new ArrayList();
            returnList = report_isApproveBo.getReturnList(returnList);
            matterList.addAll(deatilList(returnList));

            // ------------------人员信息审核审批-----------
            ArrayList personChangeList = new PersonMatterTask(this.frameconn, userView).getPersonInfoChange();
            if(personChangeList!=null) {
                for(int g=0;g<personChangeList.size();g++){
                    CommonData pData=(CommonData)personChangeList.get(g);
                    String url =  pData.getDataValue()+"&home=5&ver=5&returnflag=portal";
                    if(url!=null&&url.indexOf("encryptParam")==-1){
                        //将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
                        int index = url.indexOf("&");
                        if(index>-1){
                            String allurl = url.substring(0,index);
                            String allparam = url.substring(index);
                            url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
                        }
                        //将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
                    }

                    pData.setDataValue(url);
                }
                matterList.addAll(personChangeList);
            }

            sbf.append(getTdHtml(matterList, view_base, target));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return sbf.toString();
    }

    private List<CommonData> deatilList(ArrayList<LazyDynaBean> list){
        List<CommonData> comList=new ArrayList<CommonData>();
        CommonData data=null;
        for(LazyDynaBean bean:list) {
            data=new CommonData();
            data.setDataName((String)bean.get("name"));
            data.setDataValue((String)bean.get("url"));
            if(bean.get("date")!=null) {
                data.put("date", (String)bean.get("date"));
            }
            comList.add(data);
        }
        return comList;
    }
    /**
     * 菜单权限控制   wangb  20170727  29856
     * @param function_id
     * @param module_id
     * @param lock
     * @return
     */
    private boolean haveFuncPriv(String function_id,String module_id ,EncryptLockClient lock)
    {
        boolean bfunc=true,bmodule=true;

        /**
         * 在这里进行权限分析
         */
        /**版本功能控制*/
        VersionControl ver_ctrl=new VersionControl();
        UserView userview=userView;
        ver_ctrl.setVer(lock.getVersion());

        if(!(module_id==null|| "".equals(module_id)))
        {
            String[] modules =StringUtils.split(module_id,",");
            for(int i=0;i<modules.length;i++)
            {
                module_id=modules[i];
                bmodule=lock.isBmodule(Integer.parseInt(module_id),userview.getUserName());
                if(bmodule)
                    break;
            }

        }

        if(!(function_id==null|| "".equals(function_id)))
        {
            String[] funcs =StringUtils.split(function_id,",");
            for(int i=0;i<funcs.length;i++)
            {
                bfunc=ver_ctrl.searchFunctionId(funcs[i],true);
                if(bfunc) {
                    break;
                }
            }
        }
        return (bfunc&bmodule);
    }
}
