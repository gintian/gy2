package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.empchange.KqEmpChangeBo;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 考勤后台处理
 * <p>Title:KqStatJob.java</p>
 * <p>Description:KqStatJob.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 30, 2010 9:37:33 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 * 
 * <p>@modify zxj</P>
 * <p>@modify time: 2013-12-21 11:30:00</p>
 * <P>优化代码逻辑，减少代码量，同时增强代码的健壮性</p>
 */
public class KqStatJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Category cat = Category.getInstance(getClass());
        Connection conn = null;
        try {
            //作业类id
            String jobId = context.getJobDetail().getName();
            
            cat.info("考勤数据处理后台作业开始...");
            
            conn = (Connection) AdminDb.getConnection();
            ArrayList durationlist = RegisterDate.getKqDayList(conn);
            if (durationlist == null || durationlist.size() <= 0) {
                cat.info("考勤数据处理后台作业退出：没有定义考勤期间！");
                return;
            }
            
            UserView uv = new UserView("su", conn);
            uv.canLogin(false);
            
            KqUtilsClass kqUtilsClass = new KqUtilsClass(conn, uv);
            ArrayList kq_dbase_list = kqUtilsClass.setKqPerList("", "2");

            KqParameter kq_paramter = new KqParameter(uv, "", conn);
            HashMap hashmap = kq_paramter.getKqParamterMap();
            String kq_type = (String) hashmap.get("kq_type");
            String kq_cardno = (String) hashmap.get("cardno");
            String kq_Gno = (String) hashmap.get("g_no");

            String dataUpdateType = "0";

            String start_date = (String) durationlist.get(0);
            String end_date = (String) durationlist.get(1);
            

            String dateFormat = "yyyy.MM.dd";
            String cur_date = PubFunc.getStringDate(dateFormat);
            
            // 作业参数-是否包含当天 today=1|0 1:包含（默认），否则不包含。
            String paraValue = getKqStatJobParaValue(conn, "today", jobId, "1");
            if (!"1".equals(paraValue)) {
                Date today = new Date();
                cur_date = DateUtils.FormatDate(DateUtils.addDays(today, -1), dateFormat);
            }
            //数据处理开始日期（默认为期间开始日期）
            Date s_date = DateUtils.getDate(start_date, dateFormat);
            //数据处理结束日期（默认为期间结束日期）
            Date e_date = DateUtils.getDate(end_date, dateFormat);
            //当天
            Date c_date = DateUtils.getDate(cur_date, dateFormat);

            //还没到期间开始日期，不用进行数据处理
            if (c_date.before(s_date)) {
                cat.info("考勤数据处理后台作业退出：还没到期间开始日期，不用进行数据处理！");
                return;
            }
            
            //取处理天数参数
            paraValue = getKqStatJobParaValue(conn, "days", jobId, "2");

            int days = 2;
            try {
                days = (int) Float.parseFloat(paraValue);
            } catch (Exception e) {
            }
            
            //是否启用申请比对
            String empchangeValue = getKqStatJobParaValue(conn, "empchange", jobId, "on");
            boolean empchangeOn = "on".equalsIgnoreCase(empchangeValue);

            //期间已经结束，进入考勤结算阶段，不用再进行本期间数据处理
            if (c_date.after(e_date)) {
                //cat.info("考勤数据处理后台作业：进入考勤结算阶段，本期间不再处理，开始下期间处理！");
                
                //判断当天是否为下个期间内的一天
                ArrayList nextDurationDates = RegisterDate.getKqDayList(conn, DateUtils.addDays(e_date, 1));
                String nextStart = (String) nextDurationDates.get(0);
                String nextEnd = (String)nextDurationDates.get(1);
                
                //如果当天在下个考勤期间，那么利用分用户模式，单独进行下期间开始到当天的数据处理
                if (nextStart.compareTo(cur_date)<=0 && cur_date.compareTo(nextEnd)<=0) {
                    DataProcedureAnalyse dataProcedureAnalyse = null;
                    KqDBHelper dbHelper = new KqDBHelper(conn);
                    
                    //当天没有日明细数据，那么首先生成当天的日明细
                    if (!dbHelper.isRecordExist("Q03", "Q03Z0='" + cur_date + "'")) {
                        String createStartDate = cur_date;
                        String createEndDate = cur_date;
                        
                        //如果当天是下期间第一天，那么生成全月日明细，否则，生成当天日明细
                        if (cur_date.equals(nextStart)) {
                            createEndDate = nextEnd;
                        }
                        
                        //如果当前期间没有日明细，则生成整期间日明细
                        if(!dbHelper.isRecordExist("Q03", "Q03Z0>='" + nextStart + "' and Q03Z0<='" + nextEnd + "'")) {                            
                            createStartDate = nextStart;
                            createEndDate = nextEnd;
                        }
                        
                        cat.info("考勤数据处理后台作业进行中：生成日明细" + createStartDate + "~" + createEndDate);
                        
                        dataProcedureAnalyse = new DataProcedureAnalyse(conn, uv,
                                KqConstant.AnalyseType.ALL, kq_type, kq_cardno, kq_Gno, 
                                "0", kq_dbase_list);
                        dataProcedureAnalyse.setInitflag("1");
                        dataProcedureAnalyse.setCreat_register("1");
                        
                        
                        dataProcedureAnalyse.dataAnalys("", "2", createStartDate, createEndDate, "all");
                        // 更新日期类型
                        dataProcedureAnalyse.updateDateType(kq_dbase_list, createStartDate, createEndDate);
                    }
                    
                    if (days >= 0) {
                        Date date = OperateDate.addDay(c_date, 0 - days);
                        Date nextStartDate = DateUtils.getDate(nextStart, dateFormat);
                        //如果前推days天后，没有超出期间开始日期，那么数据处理开始日期取前推日期
                        if (nextStartDate.before(date)) {
                            nextStart = DateUtils.format(date, dateFormat);
                        }
                    }
                    
                    cat.info("考勤数据处理后台作业进行中：正在处理" + nextStart + "~" + cur_date);
                    //数据处理
                    dataProcedureAnalyse = new DataProcedureAnalyse(conn, uv, 
                            KqConstant.AnalyseType.MACHINE, kq_type, kq_cardno, kq_Gno,
                            dataUpdateType, kq_dbase_list);
                    dataProcedureAnalyse.dataAnalys("", "2", nextStart, cur_date, "all", false); //走数据处理class
                    cat.info("考勤数据处理后台作业进行中：存储过程部分已计算完成！");
                    String fAnalyseTempTab = dataProcedureAnalyse.getFAnalyseTempTab(); //数据处理表
                    HashMap kqItem_hash = dataProcedureAnalyse.getKqItem_hash();
                    dataProcedureAnalyse.setPick_flag("1");
                    //数据确认
                    DbWizard dbWizard = new DbWizard(conn);
                    UpdateQ33 updateQ33 = new UpdateQ33(uv, conn);
                    String overtime_for_leavetime = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME();
                    boolean countOverforleave = false;
                    if (dbWizard.isExistTable("Q33", false) && !"".equals(overtime_for_leavetime)) {
                        countOverforleave = true;
                    }

                    if (kq_dbase_list != null && kq_dbase_list.size() > 0) {
                        for (int i = 0; i < kq_dbase_list.size(); i++) {
                            String nbase = (String) kq_dbase_list.get(i);
                            String whereIN = RegisterInitInfoData.getWhereINSql(uv, nbase);
                            dataProcedureAnalyse.updateDataToQ03(fAnalyseTempTab, kqItem_hash, 
                                    nextStart, cur_date, nbase, whereIN, "", "");
                            if (countOverforleave) {
                                updateQ33.updateQ33(nextStart + "`" + cur_date, nbase);
                            }
                        }
                    }
                    kqUtilsClass.leadingInItemToQ03(kq_dbase_list, nextStart, cur_date,"Q03","");
                    cat.info("考勤数据处理后台作业进行中：导入子集指标数据已完成！");

                    dataProcedureAnalyse.updateCurUser();//更新当前操作人

                    //日明细变动比对
                    if (empchangeOn) {
                        cat.info("考勤数据处理后台作业进行中：日明细数据变动比对开始！" + (String) nextDurationDates.get(0) + "~" + (String) nextDurationDates.get(1));
                        KqEmpChangeBo kqEmpChangeBo = new KqEmpChangeBo(conn, uv);
                        kqEmpChangeBo.ExecuteKqEmpChange((String) nextDurationDates.get(0), (String) nextDurationDates.get(1));
                        cat.info("考勤数据处理后台作业进行中：日明细数据变动比对已完成！");
                    }
                    
                    //月汇总
                    end_date = (String) nextDurationDates.get(1);

                    CollectRegister collectRegister = new CollectRegister(conn);
                    collectRegister.collectData(conn, uv, (String) nextDurationDates.get(0), (String) nextDurationDates.get(1), kq_dbase_list);
                }
             
                //return;
            }

            //如果系统日期在当前期间，按参数设置的处理天数进行
            if (!c_date.after(e_date)) {
                //当天在考勤期间内，那么数据处理结束日期设为当天
                e_date = c_date;
    
                //根据参数确定数据处理开始日期
                //参数小于0时，开始日期为期间开始日期
                //参数大于等于0时，从当天往前推days天
                if (days >= 0) {
                    Date date = OperateDate.addDay(c_date, 0 - days);
                    //如果前推days天后，没有超出期间开始日期，那么数据处理开始日期取前推日期
                    if (date.after(s_date)) {
                        s_date = date;
                    }
                }
            } else {
                //如果系统日期已到当前期间结束日期之后，那么处理当前期间全部数据
                durationlist = RegisterDate.getKqDayList(conn);
                start_date = (String) durationlist.get(0);
                end_date = (String) durationlist.get(1);
            }
            start_date = DateUtils.format(s_date, dateFormat);
            end_date = DateUtils.format(e_date, dateFormat);
            
            cat.info("考勤数据处理后台作业进行中：正在处理" + start_date + "~" + end_date);
            
            DataProcedureAnalyse dataProcedureAnalyse = new DataProcedureAnalyse(conn, uv, 
                    KqConstant.AnalyseType.MACHINE_CENTRAL, kq_type, kq_cardno, kq_Gno,
                    dataUpdateType, kq_dbase_list);
            dataProcedureAnalyse.dataAnalys("", "2", start_date, end_date, "all", false); //走数据处理class
            cat.info("考勤数据处理后台作业进行中：存储过程部分已计算完成！");
            String fAnalyseTempTab = dataProcedureAnalyse.getFAnalyseTempTab(); //数据处理表
            HashMap kqItem_hash = dataProcedureAnalyse.getKqItem_hash();
            dataProcedureAnalyse.setPick_flag("1");
            //数据确认
            DbWizard dbWizard = new DbWizard(conn);
            UpdateQ33 updateQ33 = new UpdateQ33(uv, conn);
            String overtime_for_leavetime = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME();
            boolean countOverforleave = false;
            if (dbWizard.isExistTable("Q33", false) && !"".equals(overtime_for_leavetime)) {
                countOverforleave = true;
            }

            if (kq_dbase_list != null && kq_dbase_list.size() > 0) {
                for (int i = 0; i < kq_dbase_list.size(); i++) {
                    String nbase = (String) kq_dbase_list.get(i);
                    String whereIN = RegisterInitInfoData.getWhereINSql(uv, nbase);
                    dataProcedureAnalyse.updateDataToQ03(fAnalyseTempTab, kqItem_hash, 
                            start_date, end_date, nbase, whereIN, "", "");
                    if (countOverforleave) {
                        updateQ33.updateQ33(start_date + "`" + end_date, nbase);
                    }
                }
            }
            //kqUtilsClass.leadingInItemToQ03(kq_dbase_list, start_date, end_date,"Q03","");
            cat.info("考勤数据处理后台作业进行中：数据已确认至日明细表！");

            dataProcedureAnalyse.updateCurUser();//更新当前操作人

            //日明细变动比对
            if (empchangeOn) {
                cat.info("考勤数据处理后台作业进行中：日明细变动比对开始！" + start_date + "~" + end_date);
                KqEmpChangeBo kqEmpChangeBo = new KqEmpChangeBo(conn, uv);
                kqEmpChangeBo.ExecuteKqEmpChange(start_date, end_date);
                cat.info("考勤数据处理后台作业进行中：日明细变动比对已完成！");
            }
            
            //月汇总
            start_date = (String) durationlist.get(0);
            end_date = (String) durationlist.get(1);

            CollectRegister collectRegister = new CollectRegister(conn);
            collectRegister.collectData(conn, uv, start_date, end_date, kq_dbase_list);
            cat.info("考勤数据处理后台作业进行中：月汇总已完成！");
        } catch (Exception e) {
            cat.info("考勤数据处理后台作业失败！");
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(conn);
            cat.info("考勤数据处理后台作业已完成！");
        }
    }

    /**
     * 根据作业参数名取回参数值
     * @param conn
     * @param paramName 参数名称
     * @param jobId 作业类id
     * @return
     */
    private String getKqStatJobParaValue(Connection conn, String paramName, String jobId, String defaultValue) {
        String returnValue = defaultValue;

        ContentDAO dao = new ContentDAO(conn);
        try {
            //从数据库读取
            RecordVo vo = new RecordVo("t_sys_jobs");
            vo.setInt("job_id", Integer.parseInt(jobId));
            vo = dao.findByPrimaryKey(vo);

            String jobParam = vo.getString("job_param");
            //没有设置参数
            if (null == jobParam) {
                return returnValue;
            }

            jobParam = jobParam.trim();
            //没有设置参数
            if ("".equals(jobParam)) {
                return returnValue;
            }

            //参数格式：param1=value1,param2=value2...
            //解析参数
            String[] params = jobParam.split(",");
            for (int i = 0; i < params.length; i++) {
                String[] param = params[i].split("=");
                if (2 != param.length) {
                    continue;
                }

                if (!paramName.equalsIgnoreCase(param[0].trim())) {
                    continue;
                }

                returnValue = param[1].trim();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnValue;
    }

}
