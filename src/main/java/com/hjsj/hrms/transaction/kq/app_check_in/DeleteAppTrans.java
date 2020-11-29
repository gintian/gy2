/*
 * Created on 2006-2-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.SelectAllOperate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.options.KqParameterRule;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 删除申请记录
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Apr 7, 2008
 * </p>
 * 
 * @author sunxin
 * @version 4.0
 */
public class DeleteAppTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
        ArrayList selectedinfolist = (ArrayList) this.getFormHM().get("selectedinfolist");
        String table = (String) this.getFormHM().get("table");
        String ta = table.toLowerCase();
        SelectAllOperate selectAllOperate = new SelectAllOperate(this.getFrameconn(), this.userView);
        selectAllOperate.allOperate(ta);
        
        String approved_delete = KqParam.getInstance().getApprovedDelete();// 已批申请登记数据是否可以删除;0:不删除；1：删除

        if (approved_delete != null && "0".equals(approved_delete))
        {
            try
            {
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                StringBuffer buf = new StringBuffer();
                buf.append("update ");
                buf.append(table + " set state='1'");
                buf.append(" where ");
                buf.append(ta + "01=?");
                buf.append(" and " + ta + "z5<>'03'");
                ArrayList paralist = new ArrayList();
                for (int i = 0; i < selectedinfolist.size(); i++)
                {
                    LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
                    ArrayList list = new ArrayList();
                    list.add(rec.get(ta + "01").toString());
                    paralist.add(list);
                }
                dao.batchUpdate(buf.toString(), paralist);

                ArrayList dblist = this.userView.getPrivDbList();
                for (int i = 0; i < dblist.size(); i++)
                {
                    String nbase = dblist.get(i).toString();
                    String delete_sql = "delete from " + table + " where nbase='" + nbase + "'  and state='1' and " + table + "z5<>'03'";
                    String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                    whereIN = whereIN.replaceAll("1=2", "A0100='" + this.userView.getA0100() + "'");
                    delete_sql = delete_sql + " and a0100 in(select a0100 " + whereIN + ")";
                    // 35112 由于加班 公出增加销假功能应同步优化删除，防止删除已销单据
                    ArrayList list = getTalble19list(table, dao, nbase);
                    if (list != null && list.size() > 0)
                    {
                    	delete_sql = delete_sql + " and " + table + "01 not in(";
                    	for (int r = 0; r < list.size(); r++)
                    	{
                    		delete_sql = delete_sql + "'" + list.get(r) + "',";
                    	}
                    	delete_sql = delete_sql.substring(0, delete_sql.length() - 1);
                    	delete_sql = delete_sql + ")";
                    }
                    
                    dao.update(delete_sql);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        else
        {
            try
            {
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                StringBuffer buf = new StringBuffer();
                buf.append("update ");
                buf.append(table + " set state='1'");
                buf.append(" where ");
                buf.append(ta + "01=?");
                // 支持销假功能的表
                if(this.enableCacelApp(table))
                    buf.append(" OR " + ta + "19=?");

                ArrayList paralist = new ArrayList();
                for (int i = 0; i < selectedinfolist.size(); i++)
                {
                    LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
                    ArrayList list = new ArrayList();
                    String appId = rec.get(ta + "01").toString();  
                    list.add(appId);
                    // 支持销假功能的表
                    if(this.enableCacelApp(table))
                        list.add(appId);
                    paralist.add(list);
                }
                dao.batchUpdate(buf.toString(), paralist);
                ArrayList dblist = this.userView.getPrivDbList();
                KqParameterRule kqRule = new KqParameterRule(this.getFrameconn());
                AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
                StringBuffer sql = new StringBuffer();
                for (int i = 0; i < dblist.size(); i++)
                {

                    String nbase = dblist.get(i).toString();
                    sql.delete(0, sql.length());
                    sql.append("select a0100,a0101,nbase," + table + "z1 z1," + table + "z3 z3 from " + table + " where nbase='" + nbase + "' and state='1' and " + table + "z5='03' order by " + table + "01 desc");
                    this.frowset = dao.search(sql.toString());
                    while (this.frowset.next())
                    {
                        String a0100 = this.frowset.getString("a0100");
                        String a0101 = this.frowset.getString("a0101");
                        Object oz1 = this.frowset.getObject("z1");
                        Object oz3 = this.frowset.getObject("z3");
                        // *****郑文龙 请假加班和调休掉班 z1 和 z3 字段类型不一致问题（无法删除）
                        Date z1 = null;
                        Date z3 = null;
                        if (oz1 instanceof Date)
                        {
                            z1 = (Date) oz1;
                            z3 = (Date) oz3;
                        }
                        else if (oz1 instanceof String)
                        {
                            z1 = OperateDate.strToDate(oz1.toString(), "yyyy.MM.dd");
                            z3 = OperateDate.strToDate(oz3.toString(), "yyyy.MM.dd");
                        }
                        // *****郑文龙 请假加班和调休掉班 z1 和 z3 字段类型不一致问题（无法删除）
                        if (!annualApply.getKqDataState(nbase, a0100, z1, z3))
                            throw GeneralExceptionHandler.Handle(new GeneralException("", a0101 + "申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做删除操作，请与考勤管理员联系！", "", ""));
                    }
                    sql.delete(0, sql.length());
                    String delete_sql = "delete from " + table + " where nbase='" + nbase + "'  and state='1'";
                    String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                    whereIN = whereIN.replaceAll("1=2", "A0100='" + this.userView.getA0100() + "'");
                    delete_sql = delete_sql + " and a0100 in(select a0100 " + whereIN + ")";
                    // 35112 由于加班 公出增加销假功能应同步优化删除，防止删除已销单据
                    ArrayList list = getTalble19list(table, dao, nbase);
                    String sql_in = "";
                    if (list != null && list.size() > 0)
                    {
                    	sql_in = sql_in + " and " + table + "01 not in(";
                    	for (int r = 0; r < list.size(); r++)
                    	{
                    		sql_in = sql_in + "'" + list.get(r) + "',";
                    	}
                    	sql_in = sql_in.substring(0, sql_in.length() - 1);
                    	sql_in = sql_in + ")";
                    }
                    delete_sql = delete_sql + sql_in;
                    // 单独处理请假的删除
                    if ("q15".equalsIgnoreCase(table)){
                        sql.delete(0, sql.length());
                        sql.append("select * from " + table + " where nbase='" + nbase + "'  and state='1'");
                        sql.append(" and q15z0='01' and q15z5='03' and q1517<>1");
                        sql.append(" and a0100 in(select a0100 " + whereIN + ") ");
                        sql.append(sql_in);
                        sql.append(" order by a0100 desc,q15z7 desc");
                        upYearMess(dao, sql.toString(), kqRule, annualApply);
                    }
                    dao.update(delete_sql);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw GeneralExceptionHandler.Handle(ex);
            }
        }

    }
    
    
    /**
     * 是否可支持销假功能的申请表
     * @param table 表名
     * @return true: 支持， false: 不支持
     */
    private boolean enableCacelApp(String table) {
        return "Q15".equalsIgnoreCase(table) || "Q13".equalsIgnoreCase(table) || "Q11".equalsIgnoreCase(table);
    }

    /**
     * 得到有销假的请假记录
     * 
     * @param dao
     * @return
     */
    private ArrayList getTalble19list(String table, ContentDAO dao, String nbase)
    {
        ArrayList list = new ArrayList();
        
        if (StringUtils.isBlank(table)) {
            return list;
        }
        
        // 非请假、加班、公出无“销假”功能
        if (!enableCacelApp(table)) {
            return list;
        }
        
        StringBuffer sql = new StringBuffer();
        sql.append("select ").append(table).append("19 table19,").append(table).append("01 table01 from ").append(table);
        sql.append(" where nbase='").append(nbase).append("'");
        sql.append(" and ").append(table).append("17=1 ");
        sql.append(" and ").append(Sql_switcher.isnull(table+"19", "'##'")).append("<>'##' and ").append(table).append("z5='03'");
        sql.append(" and ").append(table).append("19 ");
        sql.append( " in( select ").append(table).append("01 from ").append(table).append(" where nbase='").append(nbase).append("' and state='1')");
        RowSet rs = null;
        try
        {
            rs = dao.search(sql.toString());
            while (rs.next())
            {
                list.add(rs.getString("table19"));
                list.add(rs.getString("table01"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    /**
     * 修改已批假期管理天数
     * 
     * @param dao
     * @param sql
     * @param kqRule
     * @param annualApply
     */
    private void upYearMess(ContentDAO dao, String sql, KqParameterRule kqRule, AnnualApply annualApply) throws GeneralException
    {
        RowSet rs = null;
        String leavetime_type_used_overtime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();//调休假
        try
        {
            rs = dao.search(sql);
            String b0110 = "";
            String sele = "";
            while (rs.next())
            {
                b0110 = rs.getString("b0110");
                sele = rs.getString("q1503");
                if (KqParam.getInstance().isHoliday(this.frameconn, b0110, sele))
                {
                    Date kq_start = rs.getTimestamp("q15z1");
                    Date kq_end = rs.getTimestamp("q15z3");
                    String q1501 = rs.getString("q1501");
                    Date sp_D = rs.getTimestamp("q15z7");
                    String start = DateUtils.format(kq_start, "yyyy-MM-dd HH:mm:ss");
                    String end = DateUtils.format(kq_end, "yyyy-MM-dd HH:mm:ss");
                    String sp_time = DateUtils.format(sp_D, "yyyy-MM-dd HH:mm:ss");
                    HashMap kqItem_hash = annualApply.count_Leave(sele);
                    float[] holiday_rules = annualApply.getHoliday_minus_rule();//年假假期规则
                    float leave_tiem = annualApply.getHistoryLeaveTime(kq_start, kq_end, 
                            rs.getString("a0100"), rs.getString("nbase"), rs.getString("b0110"),
                            kqItem_hash, holiday_rules);
                    if (leave_tiem > 0)
                    {
                        String history = rs.getString("history");
                        annualApply.holsBackfill(start, end, rs.getString("a0100"), rs.getString("nbase"), sele, history, leave_tiem);// 反算
                        annualApply.bachReStatHols(sp_time, rs.getString("a0100"), rs.getString("b0110"), rs.getString("nbase"), sele, kqItem_hash);// 批量
                    }
                    dao.delete("delete from q15 where q1519='" + q1501 + "' and q1517=1", new ArrayList());
                    dao.delete("delete from q15 where q1501='" + q1501 + "'", new ArrayList());
                } else if (sele.equals(leavetime_type_used_overtime)) //删除调休假
				{
					String q1501 = rs.getString("q1501");
					RecordVo rv = new RecordVo("q15");
					rv.setString("q1501", q1501);
					rv = dao.findByPrimaryKey(rv);
					UpdateQ33 updateQ33 = new UpdateQ33(userView, frameconn);
					updateQ33.returnLeaveTime(rv);
				}
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
    }
}
