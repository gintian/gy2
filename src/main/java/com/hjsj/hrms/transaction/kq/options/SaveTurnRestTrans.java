package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

;

public class SaveTurnRestTrans extends IBusiness {

    public void execute() throws GeneralException {

        try {
            String rdate = (String) this.getFormHM().get("rdate");
            if (rdate != null && rdate.length() > 0) {
                rdate = rdate.substring(0, 10);
            }

            String fid = (String) this.getFormHM().get("tid");
            String tdate = (String) this.getFormHM().get("tdate");
            if (tdate != null && tdate.length() > 0) {
                tdate = tdate.substring(0, 10);
            }

            String b0110 = getB0110ByTurnId(fid);
            if ("".equals(b0110)) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                b0110 = managePrivCode.getUNB0110();
            }
            
            ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.getFrameconn());
            String rest_date = restList.get(0).toString();
            String b0110_return = restList.get(1).toString();
            if (!b0110.equals(b0110_return.trim())) {
                throw new GeneralException("", ResourceFactory.getProperty("kq.kq_rest.this.dept.norest"), "", "");
            }

            String date = is_RestDate2(rdate.replaceAll("-", "\\."), userView, rest_date, b0110, this.getFrameconn());
            String rest_state = ResourceFactory.getProperty("kq.date.work");

            String dates = IfRestDate.is_RestDate2(tdate.replaceAll("-", "\\."), userView, rest_date, b0110, this.getFrameconn());
            String rest = ResourceFactory.getProperty("kq.date.rest");
            if (dates.indexOf(rest) != -1) {
                this.getFormHM().put("mess", "5");
                return;
            }

            if (date.indexOf(rest_state) != -1) {
                this.getFormHM().put("mess", "2");
                this.getFormHM().put("mess2", "2");
                // return ;
            } else {
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                RecordVo vo = new RecordVo("kq_turn_rest");
                StringBuffer sb = new StringBuffer();
                String work = "";
                sb.append("select week_date from kq_turn_rest where week_date=");
                sb.append(Sql_switcher.dateValue(rdate));
                sb.append(" and b0110='" + b0110 + "'");
                if (fid != null && fid.length() > 0) {
                    sb.append(" and turn_id not in('" + fid + "')");
                }
                this.frowset = dao.search(sb.toString());
                if (this.frowset.next()) {
                    Object obj = this.frowset.getObject("week_date");
                    if (obj instanceof Date) {
                        work = OperateDate.dateToStr((Date) obj, "yyyy.MM.dd");
                    } else if (obj instanceof String) {
                        work = (String) obj;
                    }
                }

                String turn = "";
                sb.delete(0, sb.length());
                sb.append("select turn_date from kq_turn_rest where turn_date=");
                sb.append(Sql_switcher.dateValue(tdate));
                sb.append(" and b0110='" + b0110 + "'");
                if (fid != null && fid.length() > 0) {
                    sb.append(" and turn_id not in('" + fid + "')");
                }
                this.frowset = dao.search(sb.toString());
                if (this.frowset.next()) {
                    Object obj = this.frowset.getObject("turn_date");
                    if (obj instanceof Date) {
                        turn = OperateDate.dateToStr((Date) obj, "yyyy.MM.dd");
                    } else if (obj instanceof String) {
                        turn = (String) obj;
                    }
                }

                if ((work == null || "".equals(work)) && (turn == null || "".equals(turn))) {
                    if (fid == null || "".equals(fid)) {
                        IDGenerator idg = new IDGenerator(2, this.getFrameconn());
                        String feast_id = idg.getId("kq_turn_rest.turn_id");
                        vo.setString("turn_id", feast_id);
                        vo.setString("b0110", b0110);
                        vo.setDate("week_date", DateUtils.getDate(rdate.replaceAll("\\.", "-"), "yyyy-MM-dd"));
                        vo.setDate("turn_date", DateUtils.getDate(tdate.replaceAll("\\.", "-"), "yyyy-MM-dd"));
                        dao.addValueObject(vo);
                    } else {
                        vo.setString("turn_id", fid);
                        vo.setString("b0110", b0110);
                        vo.setDate("week_date", DateUtils.getDate(rdate.replaceAll("\\.", "-"), "yyyy-MM-dd"));
                        vo.setDate("turn_date", DateUtils.getDate(tdate.replaceAll("\\.", "-"), "yyyy-MM-dd"));
                        dao.updateValueObject(vo);
                    }
                    this.getFormHM().put("tid", "");
                    this.getFormHM().put("mess", "0");
                    this.getFormHM().put("mess2", "0");
                } else if (work.length() > 0 && (turn == null || "".equals(turn))) {
                    this.getFormHM().put("mess", "3");
                    this.getFormHM().put("mess2", "3");
                } else {
                    this.getFormHM().put("mess", "4");
                    this.getFormHM().put("mess2", "4");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        this.getFormHM().put("turnRest_flag", "0");
        this.getFormHM().put("tid", null);
    }

    private String getB0110ByTurnId(String turnId) {
        String turnB0110 = "";
        if ("".equals(PubFunc.DotstrNull(turnId)))
            return turnB0110;
        
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search("SELECT B0110 FROM kq_turn_rest WHERE turn_id=" + turnId);
            if (this.frowset.next())
                turnB0110 = this.frowset.getString("B0110");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return turnB0110;
    }

    public static String is_RestDate2(String cur_date, UserView userView, String rest_date, String b0110, Connection conn) {
        String rest_state = ResourceFactory.getProperty("kq.date.work");
        String restdate = ResourceFactory.getProperty("kq.date.rest");
      //zxj 20150526 是“公休日倒休”，不是“节假日倒休”，判断节假日干什么！！！
        //String feast_name = IfRestDate.if_Feast(cur_date, conn);        
        //if (feast_name != null && feast_name.length() > 0)// 判断是不是节假日
        //{
            // String turn_date=IfRestDate.getTurn_Date(b0110,cur_date,conn);
            // if(turn_date==null||turn_date.length()<=0)
            // {
            //rest_state = feast_name;
            // }

        //} else {
            // String rest_date=search_RestOfWeek(b0110,userView);
            if (IfRestDate.if_Rest(cur_date, userView, rest_date))// 判断公休日
            {
                rest_state = restdate;
            }
        //}
        return rest_state;
    }

}
