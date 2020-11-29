package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 自动分配班组 展现时间页面
 * @author Owner
 * wangyao
 * 
 * @modify zhaoxj
 * 注意啦！！！ 凡是author是wangyao的代码普遍存在大量隐含的bug，请大家仔细检查:
 * 比如，判断null时，经常出现 (a!=null || a.size()>0)，不出空指针异常才怪了！
 */
public class OpenZiDongClassTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String cur_course = "";
            String end_date = KqUtilsClass.getDurationLastDay();
            
            /**得到当前考勤期间 **/
            ArrayList list = RegisterDate.getKqDayList(this.getFrameconn());
            if (list != null && list.size() > 0) {
                cur_course = list.get(0).toString(); //当前考勤期间开始时间 1号
            }
          
            this.getFormHM().put("start_date", cur_course);
            this.getFormHM().put("end_date", end_date);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
