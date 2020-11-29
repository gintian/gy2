package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 得到excel模版
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Feb 2, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SelectExcelShiftTrans extends IBusiness implements KqClassConstant {

    public void execute() throws GeneralException {
        String a_code = (String) this.getFormHM().get("a_code");
        if (a_code != null && a_code.length() > 0)
            a_code = PubFunc.decryption(a_code);

        String nbase = (String) this.getFormHM().get("nbase");
        if (null != nbase && nbase.length() > 3)
            nbase = PubFunc.decryption(nbase);

        String session_data = (String) this.getFormHM().get("session_data");
        String week_data = (String) this.getFormHM().get("week_data");
        String state = (String) this.getFormHM().get("state");
        String start_date = "";
        String end_date = "";
        
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
        if (session_data != null && StringUtils.isNotEmpty(week_data) && session_data.length() > 0 && session_data.length() < 10) {
            if(!"全月".equals(week_data) && "1".equals(state)){
            	//获取考勤期间的每周的起止日期
            	HashMap weekStartEnd = kqUtilsClass.getStartAndEndDay(session_data);
            	//获取指定考勤周的起止日期
            	String currentStartEnd = (String) weekStartEnd.get(week_data);
            	start_date = currentStartEnd.split("至")[0];
            	end_date = currentStartEnd.split("至")[1];
            }else{
            	ArrayList date_list = RegisterDate.getOneDurationDate(this.getFrameconn(), session_data);
            	start_date = date_list.get(0).toString().replaceAll("\\.", "-");
            	end_date = date_list.get(date_list.size() - 1).toString().replaceAll("\\.", "-");
            }
        } else if (session_data != null && session_data.length() > 0 && session_data.length() >= 10) {
            start_date = session_data;
            end_date = session_data;
        } else {
            String cur_date = PubFunc.getStringDate("yyyy.MM.dd");
            start_date = cur_date;
            end_date = cur_date;
        }

        KqUtilsClass kqutilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
        if (!("EP".equalsIgnoreCase(a_code.substring(0, 2)))) {
            nbase = "all";
        }

        if (a_code == null || a_code.length() <= 0 || "UN".equalsIgnoreCase(a_code)) {
            this.getFormHM().put("code_mess", "所有考勤人员");
        } else {
            this.getFormHM().put("code_mess", kqutilsClass.getACodeDesc(a_code, nbase));
        }

        this.getFormHM().put("start_date", start_date);
        this.getFormHM().put("end_date", end_date);
        this.getFormHM().put("a_code", a_code);
        this.getFormHM().put("nbase", nbase);

        ArrayList kq_dbase_list = kqutilsClass.getKqPreList();
        this.getFormHM().put("nbase_list", getKqNbaseList(kq_dbase_list));
    }

    private ArrayList getKqNbaseList(ArrayList list) {
        ArrayList kq_list = new ArrayList();
        if (list == null || list.size() <= 0)
            return kq_list;

        StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < list.size(); i++) {
            buf.append(" Upper(pre)='" + list.get(i).toString().toUpperCase() + "'");
            if (i != list.size() - 1)
                buf.append(" or ");
        }
        buf.append(")");

        StringBuffer sql = new StringBuffer();
        sql.append("select dbname,pre from dbname where 1=1 and ");
        if (buf != null && buf.toString().length() > 0)
            sql.append(buf.toString());
        sql.append(" order by dbid");

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());

            CommonData da = new CommonData();
            da.setDataName("全部人员库");
            da.setDataValue("0");
            kq_list.add(da);

            while (rs.next()) {
                da = new CommonData();
                da.setDataName(rs.getString("dbname"));
                da.setDataValue(rs.getString("pre"));
                kq_list.add(da);
            }

            if (kq_list.size() == 2)
                kq_list.remove(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return kq_list;
    }
}
