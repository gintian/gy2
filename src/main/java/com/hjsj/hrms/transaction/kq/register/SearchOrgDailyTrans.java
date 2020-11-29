package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SearchOrgDailyTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String code = (String) hm.get("code");
            String registerdate = (String) this.getFormHM().get("registerdate");
            ArrayList datelist = (ArrayList) this.getFormHM().get("datelist");
            ArrayList vo_datelist = (ArrayList) this.getFormHM().get("vo_datelist");
            String kq_duration = (String) this.getFormHM().get("kq_duration");
            String kq_period = (String) this.getFormHM().get("kq_period");
            String cur_date = "";
            HashMap maps = new HashMap();
            // 转换小时 1=默认；2=HH:MM
            String selectys = (String) hm.get("selectys");
            if (selectys == null || "".equals(selectys)) {
                String selectyis = (String) this.getFormHM().get("selectys");
                if (selectyis == null || "".equals(selectyis)) {
                    selectys = "1";
                } else {
                    selectys = selectyis;
                }
            }
            this.getFormHM().put("selectys", selectys);

            if (datelist == null || datelist.size() <= 0) {
                datelist = RegisterDate.getKqDurationList(this.frameconn);
            }
            
            String start_date = datelist.get(0).toString();
            String end_date = datelist.get(datelist.size() - 1).toString();
            
            if (vo_datelist == null || vo_datelist.size() <= 0) {
                vo_datelist = RegisterDate.registerdate(this.userView.getUserOrgId(), this.getFrameconn(), this.userView);
            }else {
                // zxj 20180619 vo_datelist中的数据可能是期间改变前的（封存或解封之前的期间）
                CommonData vo = (CommonData) vo_datelist.get(0);
                if (datelist != null && datelist.size() > 0 && !vo.getDataValue().equals((String) datelist.get(0)))
                    vo_datelist = RegisterDate.registerdate(this.userView.getUserOrgId(), this.getFrameconn(),
                            this.userView);
            }

            if (vo_datelist != null && vo_datelist.size() > 0) {
                if (registerdate != null && registerdate.length() > 0) {
                    //显示日期在当前期间内，则显示该日期数据，否则，显示当前期间第一天数据
                    if(registerdate.compareToIgnoreCase(start_date)>=0 && registerdate.compareToIgnoreCase(end_date)<=0)
                        cur_date = registerdate;
                    else
                        cur_date = start_date;
                } else {
                    String dateNow = PubFunc.FormatDate(new Date(), "yyyy.MM.dd");
                    DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                    Date startdate = null;
                    Date enddate = null;
                    try {
                        startdate = df.parse(start_date);
                        enddate = df.parse(end_date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 定位到现在的日期
                    if (new Date().getTime() >= startdate.getTime() && new Date().getTime() <= enddate.getTime()) {
                        cur_date = dateNow;
                    } else {
                        cur_date = start_date;
                    }
                }
            }
            
            String workcalendar = RegisterInitInfoData.getDateSelectHtml(vo_datelist, cur_date);
            String b0110 = code;
            if (b0110 == null || b0110.length() <= 0) {
                b0110 = RegisterInitInfoData.getKqPrivCodeValue(userView);
            }
            // 判断当前操作考勤期间是否有数据

            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            ArrayList list = OrgRegister.newFieldItemList(fielditemlist);
            String codesetid = "UN";
            if (!userView.isSuper_admin()) {
                if ("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
                    codesetid = "UM";
            }
            list = OrgRegister.newFieldItemListQ07(list, codesetid);
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
            ArrayList a0100whereIN = new ArrayList();
            for (int i = 0; i < kq_dbase_list.size(); i++) {
                String dbase = kq_dbase_list.get(i).toString();
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbase);

                a0100whereIN.add(whereA0100In);
            }
            ArrayList sqllist = OrgRegister.getSqlstr(list, start_date, end_date, b0110, "Q07", cur_date, a0100whereIN);
            maps = count_Leave();
            this.getFormHM().put("sqlstr", sqllist.get(0).toString());
            this.getFormHM().put("strwhere", sqllist.get(1).toString());
            this.getFormHM().put("columns", sqllist.get(2).toString());
            this.getFormHM().put("orderby", " order by b0110,q03z0");
            this.getFormHM().put("fielditemlist", list);
            this.getFormHM().put("kq_duration", kq_duration);
            this.getFormHM().put("orgvali", "");
            this.getFormHM().put("datelist", datelist);
            this.getFormHM().put("kq_period", kq_period);
            this.getFormHM().put("action", "collect_orgdailydata");
            this.getFormHM().put("workcalendar", workcalendar);
            this.getFormHM().put("registerdate", cur_date);
            this.getFormHM().put("vo_datelist", vo_datelist);
            this.getFormHM().put("kqItem_hash", maps);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 考勤规则的一个hashmap集
     * 
     * @return
     * @throws GeneralException
     */
    private HashMap count_Leave() throws GeneralException {
        RowSet rs = null;
        String kq_item_sql = "select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";

        ContentDAO dao = new ContentDAO(this.getFrameconn());

        HashMap hashM = new HashMap();
        String fielditemid = "";
        try {
            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            rs = dao.search(kq_item_sql);
            while (rs.next()) {
                fielditemid = rs.getString("fielditemid");
                if (StringUtils.isEmpty(fielditemid))
                    continue;
                
                HashMap hashm_one = new HashMap();
                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    if (fielditemid.equalsIgnoreCase(fielditem.getItemid())) {
                        hashm_one.put("fielditemid", rs.getString("fielditemid"));
                        hashm_one.put("has_rest", PubFunc.DotstrNull(rs.getString("has_rest")));
                        hashm_one.put("has_feast", PubFunc.DotstrNull(rs.getString("has_feast")));
                        hashm_one.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                        hashm_one.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
                        hashM.put(fielditemid, hashm_one);
                        
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return hashM;
    }
}
