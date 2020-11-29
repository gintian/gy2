package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 审批刷卡记录
 *<p>
 * Title:ApproveCardDataTrans.java
 * </p>
 *<p>
 * Description:
 * </p>
 *<p>
 * Company:HJHJ
 * </p>
 *<p>
 * Create time:Oct 19, 2007
 * </p>
 * 
 * @author sunxin
 * @version 4.0
 * 
 * @modify zhaoxj
 * 1、去除sql中的卡号条件，审批时与有没有卡号没关系。
 * 2、增加交易类异常处理
 */
public class ApproveCardDataTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            ArrayList selectedinfolist = (ArrayList) this.getFormHM().get("selectedinfolist");
            String sp_flag = (String) this.getFormHM().get("sp_action");
            if (selectedinfolist == null || selectedinfolist.size() <= 0) {
                throw new GeneralException("请选择记录！");
            }
            
            ArrayList list = new ArrayList();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String strDate = sdf.format(new java.util.Date());
            Date sp_time = DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm");
            //判断 选中是否有已报批的记录
            boolean havePendingRec = false;
            
            for (int i = 0; i < selectedinfolist.size(); i++) {
                ArrayList one_list = new ArrayList();
                LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
                String a0100 = rec.get("a0100").toString();
                String nbase = rec.get("nbase").toString();
                String work_date = rec.get("work_date").toString();
                String work_time = rec.get("work_time").toString();
                String spflag = rec.get("sp_flag").toString();
                if("02".equalsIgnoreCase(spflag)){
                	havePendingRec = true;
                }
                one_list.add(this.userView.getUserFullName());
                one_list.add(DateUtils.getTimestamp(DateUtils.format(sp_time, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
                one_list.add(a0100);
                one_list.add(nbase);
                one_list.add(work_date);
                one_list.add(work_time);
                list.add(one_list);
            }
            
            if(!havePendingRec && selectedinfolist.size()>0){
            	throw new GeneralException(ResourceFactory.getProperty("workdiary.message.select.app.subset"));
            }
            
            StringBuffer sql = new StringBuffer();
            sql.append("update kq_originality_data set");
            sql.append(" sp_flag='" + sp_flag + "',sp_user=?,sp_time=? ");
            sql.append(" where a0100=? and nbase=?");
            sql.append(" and work_date=? and work_time=? ");
            sql.append(" and " + Sql_switcher.isnull("sp_flag", "'02'") + "='02'");
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            try {
                dao.batchUpdate(sql.toString(), list);
            } catch (Exception e) {
                e.printStackTrace();
                throw new GeneralException(ResourceFactory.getProperty("kq.register.work.error"));
            }
            this.getFormHM().put("sp_action", "");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
