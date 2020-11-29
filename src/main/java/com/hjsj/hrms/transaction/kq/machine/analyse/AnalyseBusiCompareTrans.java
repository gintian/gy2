package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.machine.DateAnalyseImp;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/***
 * 处理申请比对
 * <p>
 * Title:AnalyseBusiCompareTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Oct 30, 2007
 * </p>
 * 
 * @author sunxin
 * @version 4.0
 */
public class AnalyseBusiCompareTrans extends IBusiness implements DateAnalyseImp
{
    private String tab_Name = "";

    public void execute() throws GeneralException
    {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String analyse_flag = (String) hm.get("analyse_flag");
        String tab_Name = (String) this.getFormHM().get("busiCompareTab");
        
        this.tab_Name = tab_Name;
        if (tab_Name == null || tab_Name.length() <= 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("", "找不到数据分析结果表", "", ""));
        
        ArrayList selectedinfolist = (ArrayList) this.getFormHM().get("selectedinfolist");
        if (selectedinfolist == null || selectedinfolist.size() == 0)
            return;
        
        if (analyse_flag == null || analyse_flag.length() <= 0)
            return;
        
        KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, this.userView.getUserOrgId(), this.getFrameconn());
        String kq_cardno = kq_paramter.getCardno();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        
        if ("1".equals(analyse_flag))// 以申请为准
        {
            // 申请时长小于8小时,补刷开始时间和结束时间
            // 申请时长大于等于8小时的，加班申请是补刷开始结束时间，其他的清除时间段范围内的刷卡记录
            for (int i = 0; i < selectedinfolist.size(); i++)
            {
                LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
                String busi_timelen = (String) rec.get("busi_timelen");
                String busi_type = (String) rec.get("busi_type");
                if (busi_type == null || busi_type.length() <= 0)
                    continue;
                if (busi_timelen == null || busi_timelen.length() <= 0)
                    busi_timelen = "0";
                float f_v = Float.parseFloat(busi_timelen);
                if (f_v < 8 || checkAppType(kqItem_Overtime, busi_type))// 申请时长小于8小时,补刷开始时间和结束时间
                {
                    appStandardLessThan8(rec, kq_cardno, dao);
                } else
                // 申请时长大于等于8小时的，加班申请是补刷开始结束时间，其他的清除时间段范围内的刷卡记录
                {
                    appStandardBiggish8(rec, dao);
                }
            }
        } else if ("2".equals(analyse_flag))// 以刷卡为准
        {
            for (int i = 0; i < selectedinfolist.size(); i++)
            {
                LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
                upAppRecord(rec, dao);
            }
        }
    }

    /**
     * 以刷卡数据为准修改申请记录
     * 
     * @param rec
     * @param dao
     */
    private void upAppRecord(LazyDynaBean rec, ContentDAO dao)
    {
        String busi_type = (String) rec.get("busi_type");
        String appid = (String) rec.get("appid");
        String fact_begin = (String) rec.get("fact_begin");
        String fact_end = (String) rec.get("fact_end");
        
        if (appid == null || appid.length() <= 0)
            return;
        
        if (fact_begin == null || fact_begin.length() <= 0)
            return;
        
        if (fact_end == null || fact_end.length() <= 0)
            return;
        
        String app_table = "";
        if (checkAppType(kqItem_Overtime, busi_type))
        {
            app_table = "q11";
        } else if (checkAppType(kqItem_Leave, busi_type))
        {
            app_table = "q15";
        } else if (checkAppType(kqItem_Away, busi_type))
        {
            app_table = "q13";
        }
        try
        {
            RecordVo vo = new RecordVo(app_table);
            vo.setString(app_table + "01", appid);
            vo = dao.findByPrimaryKey(vo);
            vo.setDate(app_table + "z1", DateUtils.getTimestamp(DateUtils.getDate(fact_begin.replaceAll("\\.", "-"), "yyyy-MM-dd HH:mm")));
            vo.setDate(app_table + "z3", DateUtils.getTimestamp(DateUtils.getDate(fact_end.replaceAll("\\.", "-"), "yyyy-MM-dd HH:mm")));
            
            dao.updateValueObject(vo);
            
            delBusiCompareRec(dao, rec);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    
    /**
     * 人工对比对结果进行处理后，删除申请比对表中相应记录
     * 
     * @param dao 
     * @param rec 申请比对表中相应记录
     */
    private void delBusiCompareRec(ContentDAO dao, LazyDynaBean rec) throws SQLException
    {
        ArrayList deletelist = new ArrayList();
        deletelist.add((String) rec.get("a0100"));
        deletelist.add((String) rec.get("nbase"));
        deletelist.add((String) rec.get("busi_type"));
        deletelist.add((String) rec.get("id"));
        String de = "delete from " + this.tab_Name + " where a0100=? and nbase=?  and busi_type=? and id=?";        
        dao.delete(de, deletelist);
    }

    /**
     * 申请时长小于8小时,补刷开始时间和结束时间,包括所有的加班，添加刷卡记录
     * 
     * @param rec
     * @param kq_cardno
     * @param dao
     */
    private void appStandardLessThan8(LazyDynaBean rec, String kq_cardno, ContentDAO dao) throws GeneralException
    {
        Calendar now = Calendar.getInstance();
        java.util.Date dd = now.getTime();// 系统时间
        String id = (String) rec.get("id");
        String nbase = (String) rec.get("nbase");
        String a0100 = (String) rec.get("a0100");
        String b0110 = (String) rec.get("b0110");
        String e0122 = (String) rec.get("e0122");
        String a0101 = (String) rec.get("a0101");
        String e01a1 = (String) rec.get("e01a1");
        String type = (String) rec.get("busi_type");
        String sqlstr = "select * from " + this.tab_Name + " where id='" + id + "'";
        RowSet rs = null;
        try
        {
            rs = dao.search(sqlstr);
            String busi_begin = "";
            String busi_end = "";
            if (rs.next())
            {
                Date b_b = rs.getTimestamp("busi_begin");
                Date b_e = rs.getTimestamp("busi_end");
                busi_begin = DateUtils.format(b_b, "yyyy-MM-dd HH:mm");
                busi_end = DateUtils.format(b_e, "yyyy-MM-dd HH:mm");
            }

            if (busi_begin == null || busi_begin.length() <= 0)
                return;
            if (busi_end == null || busi_end.length() <= 0)
                return;

            Date b_date = DateUtils.getDate(busi_begin, "yyyy-MM-dd HH:mm");
            Date e_date = DateUtils.getDate(busi_end, "yyyy-MM-dd HH:mm");
            String work_date_b = DateUtils.format(b_date, "yyyy.MM.dd");
            String work_time_b = DateUtils.format(b_date, "HH:mm");
            String work_date_e = DateUtils.format(e_date, "yyyy.MM.dd");
            String work_time_e = DateUtils.format(e_date, "HH:mm");
            String cardno = getCardno(nbase, a0100, kq_cardno, dao);

            ArrayList one_list = new ArrayList();
            StringBuffer sql = new StringBuffer();
            
            sql.append("insert into kq_originality_data (a0100,nbase,a0101,b0110,e0122,e01a1,card_no,work_date,work_time,");
            sql.append("oper_cause,oper_user,oper_time,sp_user,sp_time,sp_flag,inout_flag)");// 补刷原因，补刷人，补刷时间，审批人，审批时间，审批标志
            sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            RecordVo rVo = new RecordVo("kq_originality_data");
            rVo.setString("a0100", a0100);
            rVo.setString("nbase", nbase);
            rVo.setString("work_date", work_date_b);
            rVo.setString("work_time", work_time_b);            
            
            if (!dao.isExistRecordVo(rVo))
            {
                // 开始时间                
                one_list.add(a0100);
                one_list.add(nbase);
                one_list.add(a0101);
                one_list.add(b0110);
                one_list.add(e0122);
                one_list.add(e01a1);
                one_list.add(cardno);
                one_list.add(work_date_b);
                one_list.add(work_time_b);
                one_list.add("申请比对处理");
                one_list.add(this.userView.getUserFullName());
                one_list.add(DateUtils.getTimestamp(dd));
                one_list.add(this.userView.getUserFullName());
                one_list.add(DateUtils.getTimestamp(dd));
                one_list.add("03");
                one_list.add("0");
                dao.insert(sql.toString(), one_list);
            }
            
            rVo.setString("work_date", work_date_e);
            rVo.setString("work_time", work_time_e);
            
            if (!dao.isExistRecordVo(rVo))
            {
                // 结束时间
                one_list = new ArrayList();
                one_list.add(a0100);
                one_list.add(nbase);
                one_list.add(a0101);
                one_list.add(b0110);
                one_list.add(e0122);
                one_list.add(e01a1);
                one_list.add(cardno);
                one_list.add(work_date_e);
                one_list.add(work_time_e);
                one_list.add("申请比对处理");
                one_list.add(this.userView.getUserFullName());
                one_list.add(DateUtils.getTimestamp(dd));
                one_list.add(this.userView.getUserFullName());
                one_list.add(DateUtils.getTimestamp(dd));
                one_list.add("03");
                one_list.add("0");
                dao.insert(sql.toString(), one_list);
            }

            delBusiCompareRec(dao, rec);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("", "处理失败，刷卡数据已经存在！", "", ""));
        } finally
        {
            if (rs != null)
                try
                {
                    rs.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }
        try
        {

        } catch (Exception e)
        {

        }

    }

    /**
     * 申请时长大于等于8小时的，加班申请是补刷开始结束时间，其他的清除时间段范围内的刷卡记录
     * 
     * @param rec
     * @param kq_cardno
     * @param dao
     */
    private void appStandardBiggish8(LazyDynaBean rec, ContentDAO dao)
    {

        String nbase = (String) rec.get("nbase");
        String a0100 = (String) rec.get("a0100");
        String id = (String) rec.get("id");
        String sqlstr = "select * from " + this.tab_Name + " where id='" + id + "'";
        String busi_begin = "";
        String busi_end = "";
        RowSet rs = null;
        try
        {
            rs = dao.search(sqlstr);

            if (rs.next())
            {
                Date b_b = rs.getTimestamp("busi_begin");
                Date b_e = rs.getTimestamp("busi_end");
                busi_begin = DateUtils.format(b_b, "yyyy-MM-dd HH:mm");
                busi_end = DateUtils.format(b_e, "yyyy-MM-dd HH:mm");
            }
        } catch (Exception e)
        {

        } finally
        {
            if (rs != null)
                try
                {
                    rs.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }
        
        if (busi_begin == null || busi_begin.length() <= 0)
            return;
        
        if (busi_end == null || busi_end.length() <= 0)
            return;
        
        Date b_date = DateUtils.getDate(busi_begin, "yyyy-MM-dd HH:mm");
        Date e_date = DateUtils.getDate(busi_end, "yyyy-MM-dd HH:mm");
        StringBuffer sql = new StringBuffer();
        sql.append("delete from kq_originality_data where a0100='" + a0100 + "' and nbase='" + nbase + "'");
        sql.append(" and work_date" + Sql_switcher.concat() + "work_time>='" + DateUtils.format(b_date, "yyyy.MM.ddHH:mm") + "'");
        sql.append(" and work_date" + Sql_switcher.concat() + "work_time<='" + DateUtils.format(e_date, "yyyy.MM.ddHH:mm") + "'");
        try
        {
            dao.delete(sql.toString(), new ArrayList());
            delBusiCompareRec(dao, rec);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 得到卡号
     * 
     * @param nbase
     * @param a0100
     * @param kq_cardno
     * @param dao
     * @return
     */
    private String getCardno(String nbase, String a0100, String kq_cardno, ContentDAO dao)
    {
        String cardno = "";
        try
        {
            String sql = "select " + kq_cardno + " from " + nbase + "A01 where a0100='" + a0100 + "'";
            this.frowset = dao.search(sql);
            if (this.frowset.next())
                cardno = this.frowset.getString(kq_cardno);
        } catch (Exception e)
        {

        }
        return cardno;
    }

    /**
     * 检测时什么申请类型的
     * 
     * @param apptype
     * @param element
     * @return
     */
    private boolean checkAppType(String apptype, String element)
    {
        if (element == null)
            return false;
        if (apptype == null)
            return false;
        String f_element = element.substring(0, 1);
        if (!apptype.equals(f_element))
            return false;
        return true;
    }
}
