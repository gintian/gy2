package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SearchOvertoleaveTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	String overtoleaveTypeString = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
        AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
        HashMap kqItem_hash = annualApply.count_Leave(overtoleaveTypeString);
        String fielditemid = (String) kqItem_hash.get("fielditemid");
        int unit = 1;
        if (fielditemid != null && fielditemid.length() > 0) 
		{
			FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid);
			unit = fieldItem.getDecimalwidth();
		}
         
        HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
        if(hm.get("usableTime")!=null&&hm.get("start_d")!=null){
            this.getFormHM().put("usableTime", hm.get("usableTime"));
            this.getFormHM().put("start_d", hm.get("start_d"));
        }
        String usableTime = (String)this.getFormHM().get("usableTime");
        //String start_d = ((String)this.getFormHM().get("start_d"));
        hm.remove("usableTime");
        
        KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(this.frameconn, this.userView);
        HashMap period = kqOverTimeForLeave.getEffectivePeriod();
        String start_d = ((String)period.get("from")).replaceAll("-", ".");
        String end_d = ((String)period.get("to")).replaceAll("-", ".");
        
        String start_time1 = start_d.replace(".", "-") + " 00:00:00";
        String end_time1 = end_d.replace(".", "-") + " 23:59:59";
        
        StringBuffer sql_q33 = new StringBuffer();
        sql_q33.append("select q3303,q3305,q3307,q3309 from Q33 where ");
        sql_q33.append("nbase = '"+this.userView.getDbname()+"' and a0100 = '"+this.userView.getA0100()+"' ");
        sql_q33.append("and q3303 >= '" + start_d + "' and q3303 <= '" + end_d +"'");
        sql_q33.append(" order by q3303");
        
        StringBuffer sql_app = new StringBuffer();
        switch (Sql_switcher.searchDbServer())
        {
        case Constant.ORACEL:
        	sql_app.append("select to_char(q15z1,'YYYY.MM.DD HH24:MI:SS') as q15z1,to_char(q15z3,'YYYY.MM.DD HH24:MI:SS') as q15z3," +
            		" to_char(q15z7,'YYYY.MM.DD HH24:MI:SS') as q15z7," );
            break;
        default:
        	sql_app.append("select q15z1,q15z3,q15z7," );
            break;
        }
        sql_app.append("q1507,q15z5,q1519 from Q15 where ");
        sql_app.append("nbase = '"+this.userView.getDbname()+"' and a0100 = '"+this.userView.getA0100()+"' ");
        sql_app.append("and q1503 = '"+overtoleaveTypeString+"' and q15z5 = '03' ");
        sql_app.append("and q15z1 >= " + Sql_switcher.dateValue(start_time1) + " and q15z3 <= " + Sql_switcher.dateValue(end_time1) +" ");
        sql_app.append("order by q15z1,q15z3");
        
        ArrayList vo_list=new ArrayList();
        ArrayList vo_List2 = new ArrayList();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try
        {
         	this.frowset = dao.search(sql_q33.toString());
            while(this.frowset.next()){
                RecordVo vo=null;
                vo = new RecordVo("Q33");
                String q3305 = new BigDecimal(this.frowset.getInt("q3305")/60.0).setScale(unit, BigDecimal.ROUND_HALF_UP).toString();
                String q3307 = new BigDecimal(this.frowset.getInt("q3307")/60.0).setScale(unit, BigDecimal.ROUND_HALF_UP).toString();
                String q3309 = new BigDecimal(this.frowset.getInt("q3309")/60.0).setScale(unit, BigDecimal.ROUND_HALF_UP).toString();
                vo.setString("q3303", this.frowset.getString("q3303"));
                vo.setString("q3305", q3305);
                vo.setString("q3307", q3307);
                vo.setString("q3309", q3309);
                vo_list.add(vo);
            }
            this.frowset = dao.search(sql_app.toString());
            while(this.frowset.next()){
                RecordVo vo = null;
                vo = new RecordVo("Q15");
                String appTypeFlag = this.frowset.getString("q1519");
                vo.setString("q15z1", this.frowset.getString("q15z1").substring(0, 16));
                vo.setString("q15z3", this.frowset.getString("q15z3").substring(0, 16));
                vo.setString("q1507", this.frowset.getString("q1507"));
                vo.setString("q15z5", this.frowset.getString("q15z5"));
                vo.setString("q15z7", this.frowset.getString("q15z7").substring(0, 16));
                vo.setString("q1519", appTypeFlag == null?"1":"0");
                vo_List2.add(vo);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        this.getFormHM().put("vo_list", vo_list); 
        this.getFormHM().put("vo_list2", vo_List2);
        this.getFormHM().put("usableTime", usableTime);
        this.getFormHM().put("start_time", start_d);
        this.getFormHM().put("end_time", end_d);
        
        Date startDate = new Date();
        GetValiateEndDate ve = new GetValiateEndDate(this.userView,this.frameconn);
        int timesCount1 = ve.getTimesDetailsCount(startDate, this.userView.getDbname(), this.userView.getA0100(), this.frameconn, "q3305");
        String allOverTime = new BigDecimal(timesCount1/60.0).setScale(unit, BigDecimal.ROUND_HALF_UP).toString();//调休总时长
       
        int timesCount2 = ve.getTimesDetailsCount(startDate, this.userView.getDbname(), this.userView.getA0100(), this.frameconn, "q3307");
        String haveUsedTime = new BigDecimal(timesCount2/60.0).setScale(unit, BigDecimal.ROUND_HALF_UP).toString();//调休倒休总时长
       
        this.getFormHM().put("allOverTime", allOverTime);
        this.getFormHM().put("haveUsedTime", haveUsedTime);
        
    }

}
