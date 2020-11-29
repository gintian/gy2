package com.hjsj.hrms.module.selfservice.taskcenter.businessobject.impl;

import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.businessobject.general.template.workflow.TemplateSelfServicePlatformBo;
import com.hjsj.hrms.module.selfservice.taskcenter.businessobject.ITaskCenterService;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 模板数据服务接口实现类
 * @Author wangz
 * @Date 2020/5/11 16:36
 * @Version V1.0
 **/
public class ITaskCenterServiceImpl implements ITaskCenterService {
    private Connection conn;
    private UserView userView;

    public Connection getConn() {
        return conn;
    }

    public ITaskCenterServiceImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    @Override
    public String getTaskNumber(String taskType) throws GeneralException {
        return null;
    }

    @Override
    public List getPendingTaskList(LazyDynaBean paramBean) throws GeneralException {
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.conn,this.userView);
        List dbList = templateSelfServicePlatformBo.getDBList(paramBean);
        return dbList;
    }

    @Override
    public List getApprovedTaskList(LazyDynaBean paramBean) throws GeneralException {
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.conn,this.userView);
        List ybTaskList = templateSelfServicePlatformBo.getYpTask(paramBean);
        return ybTaskList;
    }

    @Override
    public Map getTemplateDataInfo(HashMap paramMap) throws GeneralException {
        HashMap data = new HashMap();
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.conn,this.userView);
        try {
            data = templateSelfServicePlatformBo.getTemplateInfo(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("");
        }
        return JSONObject.fromObject(data);
    }

    @Override
    public String saveTemplateDataInfo(String paramStr) throws GeneralException {
        return null;
    }

    @Override
    public String dealTask(List taskList,String operateType) throws GeneralException {
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.conn,this.userView);
        String errorInfo ="";
        for(int i=0;i<taskList.size();i++){
            JSONObject taskDataObject = (JSONObject) taskList.get(i);
            HashMap taskData = (HashMap) JSONObject.toBean(taskDataObject,Map.class);
            String opt="";
            if(StringUtils.equalsIgnoreCase(operateType,"agree")){
                opt = "1";
            }else if(StringUtils.equalsIgnoreCase(operateType,"reject")){
                opt = "2";
            }
            taskData.put("opt",opt);
            String taskid = (String) taskData.get("taskid");
            if(StringUtils.isEmpty(taskid)){
                taskid = "0";
            }else{
                if(!StringUtils.equalsIgnoreCase(taskid,"0")){
                    taskid = PubFunc.decrypt(taskid);
                }
            }
            taskData.put("taskid",taskid);
            errorInfo =  templateSelfServicePlatformBo.dealTask(taskData);
        }
        return errorInfo;
    }
    private String getDealOpt(Map taskData,String operateType){
        //默认报批 (不包括 没有审批过程的提交)
        String opt = "1";
        try {
            String ins_id = (String) taskData.get("ins_id");
            String tabid = (String) taskData.get("tabid");
            RecordVo tab_vo= TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.conn);
            boolean bsp_flag=false;
            String sxml=tab_vo.getString("sp_flag");
            if(sxml==null|| "".equals(sxml)|| "0".equals(sxml)){
                bsp_flag=false;
            }
            else{
                bsp_flag=true;
            }
            if(StringUtils.equalsIgnoreCase(operateType,"reject")){
                //驳回
                opt = "3";
            }else if(StringUtils.equalsIgnoreCase(operateType,"agree")){
                //需要判断是否是直接提交
                if(ins_id.trim().length()==0|| "0".equals(ins_id))
                {
                    if(!bsp_flag) { //是否需要审批
                        //没有审批过程的提交
                        opt = "2";
                    }
                    else{
                        opt="1";
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return opt;
    }
    @Override
    public List getReportTaskList(LazyDynaBean paramBean) throws GeneralException {
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.conn,this.userView);
        List reportTaskList = templateSelfServicePlatformBo.getReportTask(paramBean);
        return reportTaskList;
    }

    @Override
    public List getMyApplyList(LazyDynaBean paramBean) throws GeneralException {
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.conn,this.userView);
        List myApplyTaskList = templateSelfServicePlatformBo.getCtrlTask(paramBean);
        return myApplyTaskList;
    }

    @Override
    public Map getApplyTableData(String tabId, String insId, String taskId,String module_id,int pageSize,int pageNum,String searchName) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        Map tableData = new HashMap();
        List tableList = new ArrayList();
        try {
            TemplateDataBo dataBo = new TemplateDataBo(this.conn,
                    this.userView, Integer.parseInt(tabId));
            String dataTabName = dataBo.getUtilBo().getTableName(module_id,
                    Integer.valueOf(tabId), taskId);
            //获取排序语句
            StringBuffer orderBy = new StringBuffer();
            if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
                    && (dataBo.getParamBo().getOperationType() == 8 || dataBo
                    .getParamBo().getOperationType() == 9)) {
                String key = "b0110";
                if (dataBo.getParamBo().getInfor_type() == 3){
                    key = "e01a1";
                }
                orderBy.append("  order by "
                        + Sql_switcher.isnull("to_id", "100000000")
                        + ",case when " + key
                        + "=to_id then 100000000 else a0000 end asc ");
            } else{
                orderBy.append(" order by a0000");
            }
            String sql = dataBo.getSql(module_id, "1", "1",
                    dataTabName, taskId, "", "", "" );
            String nameColumn = "";
            if (dataBo.getParamBo().getInfor_type() == 2
                    || dataBo.getParamBo().getInfor_type() == 3) {//单位名称
                if (dataBo.getParamBo().getOperationType() == 5) {
                    nameColumn = "codeitemdesc_2";
                } else {
                    nameColumn = "codeitemdesc_1";
                }
            }
            boolean isPersonTemplate = false;
            if (dataBo.getParamBo().getInfor_type() == 1) {
                isPersonTemplate =true;
                DbWizard dbWizard = new DbWizard(this.conn);

                if (dataBo.getParamBo().getOperationType() == 0) {//人员调入型
                    if (dbWizard.isExistField(dataTabName, "a0101_2", false)) {
                        nameColumn = "a0101_2";
                    }
                } else {
                    nameColumn = "a0101_1";
                }
            }
            if(StringUtils.isNotEmpty(searchName)){
                sql+= " and "+nameColumn+" like '%"+searchName+"%'";
            }
            rs = dao.search(sql,pageSize,pageNum);
            while (rs.next()){
                Map rowMap = new HashMap();
                if(!StringUtils.equalsIgnoreCase("0",taskId)){
                    String task_id = rs.getString("realtask_id");
                    rowMap.put("task_id",task_id);
                }
                String submitflag = rs.getString("submitflag2");
                String objectid = rs.getString("objectid");
//                if(isPersonTemplate){
//                    objectid = objectid.replaceAll("`","");
//                }
                //如果是单位模板 则是b0100 如果是岗位模板 则是e01a1
                String name = rs.getString(nameColumn);
                rowMap.put("submitflag",submitflag);
                rowMap.put("objectid",objectid);
                rowMap.put("objectid_encrypt",PubFunc.encrypt(objectid));
                rowMap.put("name",name);
                tableList.add(rowMap);
            }
            //获取总记录数
            sql = "select count(*) from ("+sql+") as t";
            rs =dao.search(sql);
            int totalCount = 0;
            if(rs.next()){
                totalCount = rs.getInt(1);
            }
            tableData.put("totalCount",totalCount);
            tableData.put("tableList",tableList);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return tableData;
    }

    public String getQuerySql(String tabId, String ins_id, String objs) {
        String sql = "";
        StringBuffer wherebuffer = new StringBuffer();
        sql += "select * from templet_" + tabId;

        String[] ids = objs.split(",");
        objs = objs.replaceAll(",", "','");

        if (StringUtils.isNotBlank(objs)) {
            wherebuffer.append(" where lower(basepre)" + Sql_switcher.concat() + "a0100 in('" + objs.toLowerCase() + "')");//oracle库未区分大小写，导致未查询到记录 wangbo 2019-12-09 bug 56144
        } else {
            wherebuffer.append(" where 1=2");
        }
        if (StringUtils.isNotBlank(ins_id) && !"0".equalsIgnoreCase(ins_id)) {
            wherebuffer.append(" and ins_id in (" + ins_id + ")");
        }
        if (StringUtils.isNotBlank(ins_id) && "0".equalsIgnoreCase(ins_id)) {
            sql = sql.replaceAll("templet_" + tabId, "g_templet_" + tabId);
        }
        sql += wherebuffer.toString();
        return sql;
    }

    private boolean isNbase(ContentDAO dao, String pre) throws SQLException {
        boolean flag = false;
        String sql = "select * from DBName where Pre = ? ";
        ArrayList values = new ArrayList();
        values.add(pre);
        RowSet rw = null;
        rw = dao.search(sql, values);
        if (rw.next()) {
            flag = true;
        }
        return flag;
    }


}
