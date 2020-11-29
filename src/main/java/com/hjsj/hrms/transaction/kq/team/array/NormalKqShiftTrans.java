package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 正常排班
 * <p>Title:NormalKqShiftTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 25, 2006 9:31:02 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class NormalKqShiftTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String a_code = (String) this.getFormHM().get("a_code");
            a_code = PubFunc.decrypt(a_code);
            String nbase = (String) this.getFormHM().get("nbase");
            nbase = PubFunc.decrypt(nbase);
            String session_data = (String) this.getFormHM().get("session_data");
            
            HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
            String week_data = (String) hm.get("week_data");
            String state = (String) hm.get("state");
            // 38786 经查改参数暂时无用，获取班次列表时都需要“休息”班次
//            String isKqShift = (String) hm.get("isKqShift");
            ArrayList list = getClassList("");
            String start_date = "";
            String end_date = "";
            
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            if (session_data != null && session_data.length() > 0 && StringUtils.isNotEmpty(week_data) && session_data.length() < 10) {
            	if(!"全月".equals(week_data)&& "1".equals(state)){
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
            start_date = start_date.replace("-", ".");
            end_date = end_date.replace("-", ".");
            this.getFormHM().put("start_date", start_date);
            this.getFormHM().put("end_date", end_date);
            this.getFormHM().put("rest_postpone", "");
            this.getFormHM().put("feast_postpone", "");
            this.getFormHM().put("group_syn", "");
            this.getFormHM().put("classlist", list);
            //a_code = PubFunc.encrypt(a_code);
            //nbase = PubFunc.encrypt(nbase);
            this.getFormHM().put("a_code", a_code);
            this.getFormHM().put("nbase", nbase);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 
     * @Title:getClassList
     * @Description：提供正常排班班次列表下拉数据
     * @author liuyang
     * @param isKqShift 
     * @return 返回正常排班班次列表下拉数据list
     * @throws GeneralException
     */
    private ArrayList getClassList(String isKqShift) throws GeneralException {
        ArrayList list = new ArrayList();
        ArrayList kqlist = new ArrayList();
        CommonData da = null;
        KqUtilsClass kqcl = new KqUtilsClass(this.getFrameconn(),this.userView);
        kqlist = kqcl.getKqClassListInPriv();
        LazyDynaBean ldb = new LazyDynaBean();

        try {
            for (int i = 0; i < kqlist.size(); i++) {
                String onduty = "";
                String offduty = "";
                ldb = (LazyDynaBean) kqlist.get(i);
                da = new CommonData();
                onduty = (String) ldb.get("onduty_1");
                for (int j = 3; j > 0; j--) {
                    offduty = (String) ldb.get("offduty_" + j);
                    if (offduty != null && offduty.length() == 5)
                        break;
                }
                if (onduty != null && onduty.trim().length() > 0 && offduty != null && offduty.trim().length() > 0) {
                    da.setDataName((String) ldb.get("name") + "(" + onduty + "~" + offduty + ")");
                    da.setDataValue((String) ldb.get("classId"));
                    list.add(da);
                }else{
                    da.setDataName((String) ldb.get("name") + "()");
                    da.setDataValue((String) ldb.get("classId"));
                    list.add(da);
                }
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
        da = new CommonData("-1", "<不排班>");
        list.add(da);
        
        return list;
    }

}
