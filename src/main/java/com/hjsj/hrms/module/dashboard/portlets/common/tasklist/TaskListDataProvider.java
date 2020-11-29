package com.hjsj.hrms.module.dashboard.portlets.common.tasklist;

import com.hjsj.hrms.businessobject.general.template.MatterTaskList;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.infor.PersonMatterTask;
import com.hjsj.hrms.businessobject.kq.interfaces.KqMatterTask;
import com.hjsj.hrms.businessobject.report.report_isApprove.Report_isApproveBo;
import com.hjsj.hrms.module.recruitment.util.ZpPendingtaskBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 待办任务交易类
 * @author zhangh
 */
public class TaskListDataProvider extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        // 获取组件相关参数
        JSONObject portletConfig = (JSONObject)this.getFormHM().get("portletConfig");
        // 获取table表格内容的高度（需要减去header的高度）
        int height = 0;
        if(portletConfig.containsKey("height")){
            height = (int)portletConfig.get("height") -45;
        }else{
            int h = (int)portletConfig.get("h");
            //表格内容区域的高度等于 h*60 - 上下的padding - 自定义的header高度
            height = h * 60 - 28 - 45;
        }
        // 待办表每行数据占的高度是35个像素，根据高度计算每页显示的条数
        int page_num = height / 35 ;
        // 是否显示更多按钮
        boolean showMore = false;
        JSONObject taskData = new JSONObject();
        // 待办数据
        JSONObject todoTask = getTodoTask(page_num);
        // 已办数据
        JSONObject finishTask = getFinishTask(page_num);
        // 我的申请
        JSONObject myApply = getMyApply(page_num);
        if(todoTask.getBoolean("showMore") || finishTask.getBoolean("showMore") ||myApply.getBoolean("showMore") ){
            showMore = true;
        }
        taskData.put("showMore",showMore);
        taskData.put("todoTask",todoTask);
        taskData.put("finishTask",finishTask);
        taskData.put("myApply",myApply);
        this.getFormHM().put("taskData",taskData);
    }

    /**
     * 获取我的申请数据
     * @param  page_num 每页条数
     * @return
     */
    private JSONObject getMyApply(int page_num){
        JSONObject myApply = new JSONObject();
        String _static="static";
        if(Sql_switcher.searchDbServerFlag()== Constant.DAMENG) {
            _static="static_o";
        }
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
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
            strsql.append(",U.actor_type actortype,case when (select count(1) from t_wf_task t1  where  t1.task_type='2' and T1.ins_id=u.ins_id and t1.bread=1)>0 then 0  else 1 end  recallflag ");
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
            String sql = "";
            if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql = " select count(*) count from (" + strsql.toString() + ")mydeclare ";
                list= ExecuteSQL.executeMyQuery(" select top "+page_num+" * from (" + strsql.toString() + ")mydeclare order by ins_start_date desc ", this.frameconn);
            }else {
                sql = " select  count(*) count  from (" + strsql.toString() + ")mydeclare ";
                list=ExecuteSQL.executeMyQuery(" select  *  from (" + strsql.toString() + " order by ins_start_date desc )mydeclare  where rownum<="+page_num, this.frameconn);
            }
            int count = 0;
            rs = dao.search(sql);
            if(rs.next()){
                count = rs.getInt("count");
            }
            boolean showMore = count > page_num ? true : false;
            JSONArray array = new JSONArray();
            myApply.put("count",count);
            for (LazyDynaBean bean : list){
                JSONObject dataObject = new JSONObject();
                dataObject.put("name",(String)bean.get("name"));
                dataObject.put("date",(String)bean.get("ins_start_date"));
                dataObject.put("tabid",(String)bean.get("tabid"));
                dataObject.put("ins_id",(String)bean.get("ins_id"));
                dataObject.put("task_id",(String)bean.get("task_id"));
                dataObject.put("type","template");
                array.add(dataObject);
            }
            myApply.put("list",array);
            myApply.put("showMore",showMore);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return myApply;
    }

    /**
     * 获取待办数据
     * @param page_num 每页条数
     * @return
     */
    private JSONObject getTodoTask(int page_num){
        JSONObject todoTask = new JSONObject();
        ArrayList matterList = null;
        boolean isagent = userView.isBAgent();
        try {
            ZpPendingtaskBo zpbo = new ZpPendingtaskBo(this.frameconn, userView);
            ArrayList zpdatalist = zpbo.getZpapprDta();

            MatterTaskList matterTaskList = new MatterTaskList(this.frameconn, userView);
            matterTaskList.setReturnflag("8");
            matterList = matterTaskList.getPendingTask();
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
            String name = "";
            String date = "";
            String tabid = "";
            String ins_id = "";
            String task_id = "";
            String type = "";
            String url = "";
            String value = "";
            int count = matterList.size();
            todoTask.put("count",count);
            boolean showMore = count > page_num ? true : false;
            int num = showMore ? page_num: count;
            JSONArray arr = new JSONArray();
            for (int i = 0; i < num; i++) {
                CommonData data = (CommonData)matterList.get(i);
                name = data.getDataName();
                url = data.getDataValue();
                value = SafeCode.decode(data.getDataValue());
                if(value.contains("tab_id")){
                    type = "template";
                    url = "";
                    String [] valueArr = value.split("&");
                    for(String val :valueArr){
                        if(val.contains("task_id")){
                            task_id = val.replace("task_id=","").replace(",","");
                            //如果task_id加密了，需要先解密
                            if(!(Pattern.compile("[0-9]*")).matcher(task_id).matches()){
                                task_id = PubFunc.decrypt(task_id);
                            }
                        }else if(val.contains("tab_id")){
                            tabid = val.replace("tab_id=","");
                        }
                        ins_id = getIns(task_id);
                    }
                }else{
                    type = "other";
                }
                date = (String)data.getParameterMap().get("date");
                JSONObject dataObject = new JSONObject();
                dataObject.put("name",name);
                dataObject.put("date",date);
                dataObject.put("tabid",tabid);
                dataObject.put("ins_id",ins_id);
                dataObject.put("task_id",task_id);
                dataObject.put("type",type);
                dataObject.put("url",url);
                arr.add(dataObject);
            }
            todoTask.put("list",arr);
            todoTask.put("showMore",showMore);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return todoTask;
    }

    /**
     * 获取已办数据
     * @param  page_num 每页条数
     * @return
     */
    private JSONObject getFinishTask(int page_num){
        JSONObject finishTask = new JSONObject();
        String module_id = "10";
        String bs_flag = "1";
        String days= "999";
        String query_type = "1";
        String _static = "";
        String static_="static";
        if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            static_="static_o";
        }
        try {
            String _withNoLock = "";
            // 针对SQLSERVER
            if (Sql_switcher.searchDbServer() == Constant.MSSQL)
                _withNoLock = " WITH(NOLOCK) ";

            StringBuffer strsql = new StringBuffer();
            StringBuffer strsql2 = new StringBuffer();
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            String format_str = "yyyy-MM-dd HH:mm";
            if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                format_str = "yyyy-MM-dd hh24:mi";
            if (Sql_switcher.searchDbServerFlag() == Constant.DAMENG) {
                static_ = "static_o";
            }
            strsql.append("select U.ins_id,T.task_id,T.task_topic,U.tabid,tt."+static_+",U.actorname fullname," + Sql_switcher.dateToChar("U.start_date", format_str) + " start_date," + Sql_switcher.dateToChar("T.end_date", format_str) + " end_date ");
            strsql.append(" from t_wf_task T " + _withNoLock + ",t_wf_instance U " + _withNoLock + ",template_table tt " + _withNoLock + "");
            strsql.append(" where  T.ins_id=U.ins_id  and U.tabid=tt.tabid and ((task_type='2' and  (task_state='5'  or task_state='6' )) or(task_type='9' and task_state='4' and U.finished='6' and T.task_topic like '%被撤销)') ) ");
            strsql2.append(strsql);

            if (userView.getStatus() != 4 || !"9".equals(module_id)) {
                strsql.append(" and tt." + static_ + "!=10 and tt." + static_ + "!=11 ");
                strsql2.append(" and tt." + static_ + "!=10 and tt." + static_ + "!=11 ");
            }

            //1：审批任务 2：加签任务 3：报备任务  4：空任务
            strsql.append(" and " + Sql_switcher.isnull("T.bs_flag", "'1'") + "= " + bs_flag);
            strsql2.append(" and " + Sql_switcher.isnull("T.bs_flag", "'1'") + "=" + bs_flag);

            strsql.append("  and ( " + getInsFilterWhere2("T."));
            strsql2.append(" and ( " + getInsFilterWhere2("T."));
            strsql.append("   and " + Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"), "' '") + "<>'重新分派'  )  ");
            strsql2.append("   and " + Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"), "' '") + "<>'重新分派' ) ");

            strsql.append(")  and 1=1 ");
            strsql2.append(")  and 1=1 ");

            //最近多少天
            if ("1".equals(query_type)) {
                String strexpr = Sql_switcher.addDays(Sql_switcher.sqlNow(), "-" + days);
                strsql.append(" and U.start_date>=");
                strsql.append(strexpr);
                strsql2.append(" and U.start_date>=");
                strsql2.append(strexpr);
            }
            strsql.append(" order by T.end_date DESC");
            List<LazyDynaBean> list =  ExecuteSQL.executeMyQuery(strsql.toString(),this.frameconn);
            JSONArray array = new JSONArray();
            int num = page_num > list.size() ? list.size() : page_num;
            boolean showMore = page_num > list.size() ? false: true;
            finishTask.put("count",list.size());
            String task_id = "";
            String tabid = "";
            String topic = "";
            String operationType = "";
            String tabName = "";
            for (int i = 0; i < num; i++) {
                JSONObject dataObject = new JSONObject();
                task_id = (String)list.get(i).get("task_id");
                tabid = (String)list.get(i).get("tabid");
                _static=(String)list.get(i).get(static_);
                operationType = findOperationType(tabid);
                TemplateParam param = new TemplateParam(this.frameconn,this.userView,Integer.valueOf(tabid));
                if(userView.getStatus()!=4){
                    topic = getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,module_id,param);
                }else {
                    if(_static!=null&&("10".equals(_static)|| "11".equals(_static))) {
                        topic = getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,module_id,param);
                    }
                    else{
                        topic = getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,module_id,param);
                    }
                }
                if(topic.indexOf(",共0")!=-1) //撤销任务主题
                {
                    topic = getRecordBusiTopicByState(Integer.parseInt(task_id),3,"templet_"+tabid,dao,Integer.parseInt(operationType), module_id,param);
                }
                tabName = findTabName(tabid);
                topic = tabName + topic;
                dataObject.put("name",topic);
                dataObject.put("date",(String)list.get(i).get("start_date"));
                dataObject.put("tabid",tabid);
                dataObject.put("ins_id",(String)list.get(i).get("ins_id"));
                dataObject.put("task_id",task_id);
                dataObject.put("type","template");
                array.add(dataObject);
            }
            finishTask.put("list",array);
            finishTask.put("showMore",showMore);
        }catch (Exception e){
            e.printStackTrace();
        }
        return finishTask;
    }
    private String getInsFilterWhere2(String othername) {
        String _withNoLock="";
        if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
            _withNoLock=" WITH(NOLOCK) ";
        StringBuffer strwhere=new StringBuffer();
        /**用户号*/
        String dbpre=this.userView.getDbname(); //库前缀
        String userid=dbpre+this.userView.getA0100();//人员编号
        /**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
        if(userid==null||userid.length()==0)
            userid="-1";
        strwhere.append(" upper("+othername+"actorid) in ('");
        strwhere.append(userid.toUpperCase());
        strwhere.append("','");
        strwhere.append(this.userView.getUserName().toUpperCase());
        strwhere.append("') ");
        strwhere.append(" or  ( (");
        if(this.userView.getRolelist().size()>0)
        {
            //strwhere.append(" ( "+othername+"actor_type=2 and upper("+othername+"a0100) in ('"+userid.toUpperCase()+"','"+this.userView.getUserName().toUpperCase()+"') and  upper("+othername+"actorid) in ( ");
            strwhere.append(" ( "+othername+"actor_type=2 and  upper("+othername+"actorid) in ( ");
            String str="";
            for(int i=0;i<this.userView.getRolelist().size();i++)
            {
                str+=",'"+(String)this.userView.getRolelist().get(i)+"'";
            }
            strwhere.append(str.substring(1));
            strwhere.append(" )) or ");
        }
        strwhere.append(othername+"actor_type=5 ) ");
        strwhere.append(" and exists (select null from t_wf_task_objlink "+_withNoLock+" where ins_id=U.ins_id and task_id="+othername+"task_id and node_id="+othername+"node_id and tab_id=U.tabid and (state=1 or state=2) and upper(username) in ('"+this.userView.getUserName().toUpperCase()+"','"+userid.toUpperCase()+"') ) " );
        String a0100=this.userView.getDbname()+this.userView.getA0100();
        if(a0100==null||a0100.length()==0)
            a0100=this.userView.getUserName();

        /**组织元*/
        strwhere.append(" or ( "+othername+"actor_type='3'   and "+othername+"a0100='"+a0100+"')");   //2016-05-20   dengcan 追加了 actor_type='5' ,解决本人审批看不到问题，这块很乱，得花时间重新梳理
        return " ( "+strwhere.toString()+" ) ";
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
    private String findOperationType(String tabid) {
        String operationType="";
        try {
            ContentDAO dao=new ContentDAO(this.frameconn);
            RowSet rowSet=dao.search("select operationtype from operation where operationcode=(select operationcode from template_table where tabid="+tabid+")");
            if(rowSet.next())
                operationType=rowSet.getString("operationtype");
            if(rowSet!=null)
                rowSet.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return operationType;
    }
    private String getTopic(String task_id,String tabname,int operationtype,String tab_id,String type, TemplateParam param) {
        int nmax=0;
        StringBuffer stopic=new StringBuffer();
        stopic.append("(");
        try
        {
            String _withNoLock="";
            if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
                _withNoLock=" WITH(NOLOCK) ";
            ContentDAO dao=new ContentDAO(this.frameconn);
            String a0101="a0101_1";
            RecordVo vo=new RecordVo(tabname);
            if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
            {
                a0101="a0101_2";
            }
            if(param.getInfor_type()==2 || param.getInfor_type()==3){//如果是单位管理机构调整 或 岗位管理机构调整
                //if(type!=null&&(type.equals("7")||type.equals("8")))//如果是单位管理机构调整 或 岗位管理机构调整
                a0101="codeitemdesc_1";
                if(operationtype==5)
                    a0101="codeitemdesc_2";
            }
            String sql="";
            String seqnum="1";
            RowSet rset=null;
            rset=dao.search("select seqnum from "+tabname+""+_withNoLock+" where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )");
            if(rset.next())
                seqnum=rset.getString(1)!=null?rset.getString(1):"";
            if(seqnum.length()>0)
            {
                sql=" select "+a0101+" from "+tabname+""+_withNoLock+",t_wf_task_objlink two "+_withNoLock+" where "+tabname+".seqnum=two.seqnum and "+tabname+".ins_id=two.ins_id "
                        +" and two.task_id="+task_id+" and two.tab_id="+tab_id +" and ( "+Sql_switcher.isnull("two.state","0")+"<>3 )  and ("+Sql_switcher.isnull("two.special_node","0")+"=0  or ( "+Sql_switcher.isnull("two.special_node","0")+"=1 and (lower(two.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(two.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
            }
            else
            {
                sql=" select "+a0101+" from "+tabname+" "+_withNoLock+"  where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )";
            }
            rset=dao.search(sql);
            int i=0;
            while(rset.next())
            {
                if(i>4)
                    break;
                if(i!=0)
                    stopic.append(",");
                stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
                i++;
            }
            if(i>4)
            {
                if(seqnum.length()>0)
                    sql="select count(*)  from t_wf_task_objlink "+_withNoLock+"  where task_id="+task_id+" and tab_id="+tab_id +"  and ( "+Sql_switcher.isnull("state","0")+"<>3 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
                else
                    sql=" select count(*) from "+tabname+" "+_withNoLock+" where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )";
                rset=dao.search(sql);
                if(rset.next())
                    nmax=rset.getInt(1);
            }
            else
                nmax=i;
            stopic.append(",");
            stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
            stopic.append(nmax);
            if(param.getInfor_type()==2 || param.getInfor_type()==3)//如果是单位管理机构调整 或 岗位管理机构调整
                stopic.append("条记录)");
            else
                stopic.append("人)");
            if(rset!=null)
                rset.close();
        }
        catch(Exception ex)
        {
            return stopic.toString();
        }
        return stopic.toString();

    }
    private String findTabName(String tabid) {
        String tabName="";
        try
        {
            ContentDAO dao=new ContentDAO(this.frameconn);
            RowSet rowSet=dao.search("select name from template_table where tabid="+tabid);
            if(rowSet.next())
                tabName=rowSet.getString("name");
            if(rowSet!=null)
                rowSet.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return tabName;
    }
    /**
     * 求实际的业务数,本次模板做了多少人的业务
     * @param param
     * @return
     */
    private String getRecordBusiTopicByState(int task_id,int state,String tabname,ContentDAO dao,int operationtype,String type, TemplateParam param) {
        String _withNoLock="";
        if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
            _withNoLock=" WITH(NOLOCK) ";
        int nmax=0;
        StringBuffer stopic=new StringBuffer();
        stopic.append("(");
        try
        {
            StringBuffer strsql=new StringBuffer();
            String a0101="a0101_1";
            RecordVo vo=new RecordVo(tabname);
            if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
            {
                a0101="a0101_2";
            }
            if(param.getInfor_type()==2 || param.getInfor_type()==3)//如果是单位管理机构调整 或 岗位管理机构调整
            {
                a0101="codeitemdesc_1";
                if(operationtype==5)
                    a0101="codeitemdesc_2";
            }
            String strWhere=" where ";
            String strWhere2="";
            if(state==0)
                strWhere2=" and (state is null or  state=0) ";
            else
                strWhere2=" and state="+state+" ";
            strWhere+=" exists (select null from t_wf_task_objlink "+_withNoLock+" where "+tabname+".seqnum=t_wf_task_objlink.seqnum and task_id="+task_id+" "+strWhere2+"  )";
            strsql.append("select  ");
            strsql.append(a0101);
            strsql.append(" from ");
            strsql.append(tabname+_withNoLock);
            strsql.append(strWhere);
            RowSet rset=dao.search(strsql.toString());
            int i=0;
            while(rset.next())
            {
                if(i>4)
                    break;
                if(i!=0)
                    stopic.append(",");
                stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
                i++;
            }
            strsql.setLength(0);

            strsql.append("select count(*) as nmax from ");
            strsql.append(tabname+_withNoLock);
            strsql.append(strWhere.toString());

            rset=dao.search(strsql.toString());
            if(rset.next())
                nmax=rset.getInt("nmax");
            stopic.append(",");
            stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
            stopic.append(nmax);
            if(type!=null&&("7".equals(type)|| "8".equals(type)))//如果是单位管理机构调整 或 岗位管理机构调整
                stopic.append("条记录 ");
            else
                stopic.append("人");
            if(state==3)
                stopic.append(" 被撤销");
            stopic.append(")");
        }
        catch(Exception ex)
        {
            return stopic.toString();
        }
        return stopic.toString();
    }


    /**
     * 根据task_id获取ins_id
     * @param task_id
     * @return
     */
    private String getIns(String task_id) {
        String ins_id = "";
        RowSet rs = null;
        try {
            String sql = "select ins_id from t_wf_task where task_id='" + task_id + "'";
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            rs = dao.search(sql);
            if(rs.next()){
                ins_id = rs.getString("ins_id");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return ins_id;
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
}
