package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:KqSelfAppConvertTrans.java</p> 
 *<p>Description:将加班申请转为可以调休和不可调休</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:2013-8-23下午02:00:44</p> 
 *@author wangmj
 *@version 1.0
 */
public class KqSelfAppConvertTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
        	ContentDAO dao = new ContentDAO(frameconn);
        	StringBuffer sql = new StringBuffer();
        	ArrayList infoList = new ArrayList();
            ArrayList appList = (ArrayList) this.getFormHM().get("appList");
            RecordVo rv;
            for (int i = 0; i < appList.size(); i++) 
			{
				String keyId = (String) appList.get(i);
				rv = new RecordVo("Q11");
				rv.setString("q1101", keyId);
				rv = dao.findByPrimaryKey(rv);
				infoList.add(rv);
			}
            
            String convertFlag = (String) this.getFormHM().get("flag");

            String field = KqUtilsClass.getFieldByDesc("Q11", ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
            String fieldValue = "";
            if ("0".equals(convertFlag))//转调休
                fieldValue = "1";
            else if ("1".equals(convertFlag))//转加班
                fieldValue = "2";
            ArrayList list = new ArrayList();
            ArrayList oneList;

            String nbase = userView.getDbname();
            String a0100 = userView.getA0100();
            
            AnnualApply annualApply = new AnnualApply(userView, frameconn);
            KqParam kqParam = KqParam.getInstance();
            String txjb = kqParam.getOVERTIME_FOR_LEAVETIME();
            txjb = txjb == null ? "" : txjb;
            
            float apptimeLen = 0;
            for (int i = 0; i < infoList.size(); i++) {
                RecordVo recordVo = (RecordVo) infoList.get(i);
                String value = "";
                if(field != null && field.length() > 0)
                	value = (String) recordVo.getString(field);
                if (fieldValue.equals(value))
                    continue;

                String q1103 = (String) recordVo.getString("q1103");
                if ((("," + txjb + ",").indexOf("," + q1103 + ",") == -1) && "0".equals(convertFlag))
                    continue;

                oneList = new ArrayList();
                oneList.add(fieldValue);
                oneList.add(recordVo.getString("q1101"));
                
                float count = annualApply.getOneOverTimelen(recordVo);
                apptimeLen = apptimeLen + count;//累计转加班的时长
                
                list.add(oneList);
            }
            String para = kqParam.getDURATION_OVERTIME_MAX_LIMIT();
            if (para == null || para.length() <= 0)
                para = "-1";
            int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额

            if ("1".equals(convertFlag) && overtimeLimit > 0) //转加班的话，检查考勤期间内的加班时长是否超过加班时间时长限额的限制
            {
                
            	float overtimeLen = 0;
            	overtimeLen = annualApply.getKqdurationOverTimelen(nbase, a0100, "2");//计算考勤期间内的加班时长
            	
                if (overtimeLen + apptimeLen > overtimeLimit && apptimeLen > 0) {
                        this.getFormHM().put("errorMessage",SafeCode.encode("所选申请单时长为" + PubFunc.round(""+apptimeLen,2) + "小时，本期内已申请的加班时长为" 
    							+ PubFunc.round(""+overtimeLen,2) + "小时，合计已超出加班限额规定的" + PubFunc.round(""+overtimeLimit,2) + "小时。"));
                	return;
                }
            }
            sql.setLength(0);
            sql.append("update q11 set " + field + " = ? where q1101 = ? and q11z5 in ('01','07')");
            dao.batchUpdate(sql.toString(), list);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
