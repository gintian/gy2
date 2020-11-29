package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class CreatKqRegisterTrans extends IBusiness
{
    private String error_return = "/kq/register/daily_registerdata.do?b_query=link";

    public void execute() throws GeneralException
    {
        String error_flag = "0";

        long  beginTime = System.currentTimeMillis();
        try {
            ArrayList datelist = (ArrayList) this.getFormHM().get("datelist");
            
            //日明细生成方式  1：生成整个期间 ；2：生成一个时间段内
            String creat_type = (String) this.getFormHM().get("creat_type");
            if (creat_type == null || creat_type.length() <= 0)
                creat_type = "1";
            
            if (!"1".equals(creat_type) && !"2".equals(creat_type))
                throw GeneralExceptionHandler.Handle(new GeneralException("", "生成日明细数据范围错误，请重新生成！", "", ""));
            
            // 将申请单明细数据统计到考勤日明细
            String creat_pick = (String) this.getFormHM().get("creat_pick");
            if (creat_pick == null || creat_pick.length() <= 0)
                creat_pick = "0";
            
            // 将已上报人员考勤数据重新生成
            String creat_state = (String) this.getFormHM().get("creat_state");
            if (creat_state == null || creat_state.length() <= 0)
                creat_state = "";

            String start_date = "";
            String end_date = "";

            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            String b01100 = managePrivCode.getPrivOrgId();
            
            ArrayList periodlist = new ArrayList();

            if ("1".equals(creat_type))
            {
                if (datelist == null || datelist.size() <= 0)
                    datelist = RegisterDate.registerdate(b01100, this.getFrameconn(), this.userView);

                CommonData vo_date = (CommonData) datelist.get(0);
                start_date = vo_date.getDataValue();
                vo_date = (CommonData) datelist.get(datelist.size() - 1);
                end_date = vo_date.getDataValue();
                periodlist = datelist;
            }
            else if ("2".equals(creat_type))
            {
                start_date = (String) this.getFormHM().get("start_date");
                end_date = (String) this.getFormHM().get("end_date");
                start_date = start_date.replaceAll("-", "\\.");
                end_date = end_date.replaceAll("-", "\\.");
            }
            
            if (start_date == null || start_date.length() <= 0)
                throw GeneralExceptionHandler.Handle(new GeneralException("", "处理起始时间不能为空！", "", ""));

            if (end_date == null || end_date.length() <= 0)
                throw GeneralExceptionHandler.Handle(new GeneralException("", "处理结束时间不能为空！", "", ""));

            if ("2".equals(creat_type))
            {
                KQRestOper kQRestOper = new KQRestOper();
                periodlist = kQRestOper.getDateList(start_date, end_date);
            }

            if (periodlist == null || periodlist.size() <= 0) {
                putErrorMsg(ResourceFactory.getProperty("kq.register.session.nosave"));
                return;
            }
            
            /** ******提取业务参数******* */
            String kq_duration = RegisterDate.getKqDuration(this.getFrameconn());
            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            ArrayList columnlist = new ArrayList();
            for (int i = 0; i < fielditemlist.size(); i++)
            {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                if (!("i9999").equalsIgnoreCase(fielditem.getItemid()))
                {
                    columnlist.add(fielditem);
                }
            }

            KqParameter kq_paramter = new KqParameter(this.userView, "", this.getFrameconn());
            HashMap hashmap = kq_paramter.getKqParamterMap();
            String kq_type = (String) hashmap.get("kq_type");
            if (kq_type == null || kq_type.length() <= 0){
                //没有设置考勤方式指标
                putErrorMsg(ResourceFactory.getProperty("kq.init.kqtype.nosave"));
                return;
            }
            
            String kq_cardno = (String) hashmap.get("cardno");
            if (kq_cardno == null || kq_cardno.length() <= 0) {
                //没有设置考勤卡号指标
                putErrorMsg(ResourceFactory.getProperty("kq.card.nocreate.card_no"));
                return;
            }
            
            String kq_Gno = (String) hashmap.get("g_no");
            
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList nbases = kqUtilsClass.getKqPreList();
            if (null == nbases || nbases.size() <= 0) {
                //没有设置考勤人员库
                putErrorMsg(ResourceFactory.getProperty("kq.card.nocreate.card_no"));
                return;
            }

            this.userView.getHm().put("analyse_result", "begin");
            for (int i = 0; i < nbases.size(); i++)
            {
                String nbase = nbases.get(i).toString();
                String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);

                // synchronizationInitQ03(nbase,whereIN,start_date,end_date);
                synchronizationInitQ05(nbase, whereIN, kq_duration);

                //zxj 20170607 注释原因：不论生成全期间还是一个时间段的日明细，都应该对月汇总数据做处理，否则，如果月汇总已上报，日明细生成了也没法汇总。
                //if (!creat_type.equals("2")){
                	initializtion_Q05(kq_duration, nbase, whereIN, creat_state);
                	initializtion_Q05_irregularly(kq_duration, nbase, whereIN, creat_state);                        	
                //}

                initializtion_Q03(start_date, end_date, nbase, whereIN, creat_state);
            }

            this.userView.getHm().put("analyse_result", "active");
            //调用数据处理过程生成初始考勤数据
            DataProcedureAnalyse dataProcedureAnalyse = new DataProcedureAnalyse(this.getFrameconn(), this.userView,
                    "100", kq_type, kq_cardno, kq_Gno, "0", nbases);
            dataProcedureAnalyse.setInitflag("1");
            dataProcedureAnalyse.setCreat_register("1");
            if (!"1".equals(creat_pick)) {
                dataProcedureAnalyse.setCreat_pick(creat_pick);
                dataProcedureAnalyse.setNo_tranData("1");
            }
            dataProcedureAnalyse.dataAnalys("", "2", start_date, end_date, "all");
            // 更新日期类型
            dataProcedureAnalyse.updateDateType(nbases, start_date, end_date);

            this.getFormHM().put("kq_duration", kq_duration);
            // dataAnalys中已经计算过了，无需再算
            /** 计算* */
            this.getFormHM().put("error_flag", error_flag);
            this.getFormHM().put("changestatus", "true");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.userView.getHm().put("analyse_result", "finished");
            long endTime = System.currentTimeMillis();
            //System.out.println((endTime - beginTime)/1000/60.0);
        }
    }
    
    private void putErrorMsg(String erroMsg) {
        this.getFormHM().put("error_message", erroMsg);
        this.getFormHM().put("error_return", this.error_return);
        this.getFormHM().put("error_stuts", "1");
        this.getFormHM().put("error_flag", "2");
    }

    /**
     * 对q05表进行删除
     * 
     * @param kq_duration
     * @param userbase
     * @param b0110
     * 
     */
    private void initializtion_Q05(String kq_duration, String userbase, String whereIN, String creat_state)
    {
        StringBuffer delete = new StringBuffer();
        delete.append("delete  from Q05 where nbase=? and q03z0=?");
        if (whereIN != null && whereIN.length() > 0)
        {
            if (!this.userView.isSuper_admin())
            {
                delete.append(" and  EXISTS(select a0100 ");
                delete.append(whereIN);
                
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
                    delete.append(" and ");
                else
                    delete.append(" where ");
                
                delete.append(userbase).append("A01.a0100=q05.a0100)");
            }
        }
        //<>1 只删起草和驳回的月汇总
        if (!"1".equals(creat_state))
            delete.append(" and q03z5 in ('01','07')");
        
        ArrayList list = new ArrayList();
        list.add(userbase);
        list.add(kq_duration);
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try
        {
            dao.delete(delete.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 对q05表进行删除（删除不定期汇总数据）
     * 
     * @param kq_duration
     * @param userbase
     * @param b0110
     */
    private void initializtion_Q05_irregularly(String kq_duration, String userbase, String whereIN, String creat_state)
    {
        initializtion_Q05(kq_duration.substring(0,4), userbase, whereIN, creat_state);
    }

    /**
     * 对q03表进行删除
     * 
     * @param kq_duration
     * @param userbase
     * @param b0110
     */
    private void initializtion_Q03(String start_date, String end_date, String userbase, String whereIN,
            String creat_state)
    {
        ArrayList list = new ArrayList();
        StringBuffer delete = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        
        try
        {
            delete.append("delete from Q03 where nbase=? and q03z0>=? and q03z0<=?");
            if (whereIN != null && whereIN.length() > 0)
            {
                if (!this.userView.isSuper_admin())
                {
                    delete.append(" and  EXISTS(select a0100 ");
                    delete.append(whereIN);
                    
                    if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
                        delete.append(" and ");
                    else
                        delete.append(" where ");
                    
                    delete.append(userbase).append("A01.a0100=q03.a0100)");
                }
            }
            
            if (!"1".equals(creat_state))
                delete.append(" and q03z5 in ('01','07')");
            
            list.add(userbase);
            list.add(start_date);
            list.add(end_date);
            
            dao.delete(delete.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void synchronizationInitQ03(String nbase, String whereIN, String start_date, String end_date)
            throws GeneralException
    {

        String destTab = "q03";// 目标表
        String srcTab = nbase + "A01";// 源表
        String strJoin = "q03.A0100=" + srcTab + ".A0100";// 关联串
        // xxx.field_name=yyyy.field_namex,....
        String strSet = "q03.B0110=" + srcTab + ".B0110`q03.E0122=" + srcTab + ".E0122`q03.E01A1=" + srcTab + ".E01A1";// 更新串
        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "q03.nbase='" + nbase + "' and q03.q03z0>='" + start_date + "' and q03.q03z0<='" + end_date
                + "' and q03.nbase='" + nbase + "'";// 更新目标的表过滤条件
        // String strSWhere=srcTab+".a0100 in(select a0100
        // "+whereIN+")";//源表的过滤条件
        // String strSWhere="exists (select a0100 "+whereIN+" and
        // q03.a0100="+nbase+"a01.a0100)";//源表的过滤条件 原来
        String strSWhere = "";
        if (!userView.isSuper_admin())
        {
            strSWhere = "exists (select a0100 " + whereIN + " and q03.a0100=" + nbase + "a01.a0100)";// 源表的过滤条件
        }
        else
        {
            // strSWhere="exists (select a0100 "+whereIN+" where
            // q03.a0100="+nbase+"a01.a0100)";//源表的过滤条件
        }
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ")";
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            putErrorMsg(ResourceFactory.getProperty("kq.error.data.synchronization"));
        }
    }

    private void synchronizationInitQ05(String nbase, String whereIN, String kq_duration) throws GeneralException
    {

        String destTab = "q05";// 目标表
        String srcTab = nbase + "A01";// 源表
        String strJoin = "q05.A0100=" + srcTab + ".A0100";// 关联串
        // xxx.field_name=yyyy.field_namex,....
        String strSet = "q05.B0110=" + srcTab + ".B0110`q05.E0122=" + srcTab + ".E0122`q05.E01A1=" + srcTab + ".E01A1";// 更新串
        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "q05.nbase='" + nbase + "' and q05.q03z0='" + kq_duration + "'";// 更新目标的表过滤条件
        // String strSWhere=srcTab+".a0100 in(select a0100
        // "+whereIN+")";//源表的过滤条件
        // String strSWhere="exists (select a0100 "+whereIN+" and
        // q05.a0100="+nbase+"a01.a0100)";//源表的过滤条件 以前
        String strSWhere = "";
        if (!userView.isSuper_admin())
        {
            strSWhere = "exists (select a0100 " + whereIN + " and q05.a0100=" + nbase + "a01.a0100)";// 源表的过滤条件
        }
        else
        {
            strSWhere = "exists (select a0100 " + whereIN + " where q05.a0100=" + nbase + "a01.a0100)";// 源表的过滤条件
        }
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ") ";
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            putErrorMsg(ResourceFactory.getProperty("kq.error.data.synchronization"));
        }
    }
}
