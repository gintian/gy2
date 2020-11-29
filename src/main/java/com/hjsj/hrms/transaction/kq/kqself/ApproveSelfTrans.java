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
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 批量报批申请自助 
 * <p>Title:ApproveSelfTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 22, 2007 4:48:55 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class ApproveSelfTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String sp_flag = (String) this.getFormHM().get("sp_flag");
            
            sp_flag = (null == sp_flag || "".equals(sp_flag)) ? "01" : sp_flag;
            
            String table = (String) this.getFormHM().get("table");
            ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("appList");
            if("q19".equals(table) || "q25".equals(table) )
            {
            	selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
            }
            String ta = table.toLowerCase();
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            ArrayList dellist = new ArrayList();
            RecordVo rv = null;
            for (int i = 0; i < selectedinfolist.size(); i++) {
            	String  keyId ="";
            	if("q19".equals(table) || "q25".equals(table) )
                { 
            		//调休，调班
            		LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
            		keyId = rec.get(ta + "01").toString();
                }
            	else {
            		//q11,q13,q15
            		keyId =selectedinfolist.get(i).toString();
				}
                rv = new RecordVo(table);
                rv.setString(ta + "01", keyId);
                rv = dao.findByPrimaryKey(rv);
                
                if (!"01".equals(sp_flag)) {
                    String appReason = rv.getString(ta + "07");
                    if (null == appReason || "".equals(appReason.trim()))
                        throw new GeneralException("请将申请事由填写完整后再进行操作！");
                }
                
                dellist.add(rv);
            }

            ArrayList list = new ArrayList();

            StringBuffer sql = new StringBuffer();

            AnnualApply annualApply = new AnnualApply(userView, frameconn);
            KqParam kqParam = KqParam.getInstance();
            String nbase = userView.getDbname();
            String a0100 = userView.getA0100();
            float apptimeLen = 0;
            String field = KqUtilsClass.getFieldByDesc(ta, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));

            for (int i = 0; i < dellist.size(); i++) {
                ArrayList one_list = new ArrayList();
                RecordVo recordVo = (RecordVo) dellist.get(i);
                if ("q11".equals(ta)) {
	                String fieldValue = "";
	                if (field != null && field.length() > 0){
	                	fieldValue = (String) recordVo.getString(field);
	                	if (fieldValue == null || "".equals(fieldValue)) 
	         		    {
	         			    throw new GeneralException("请确认加班申请单是否调休！");
	         		    }
	                }
                
                    if (!"1".equals(fieldValue)) {
    	            	float count = annualApply.getOneOverTimelen(recordVo);
                        apptimeLen = apptimeLen + count;//累计所选的申请单的时长
                    }
                }

                String str_key = recordVo.getString(ta + "01");
                one_list.add(sp_flag);
                one_list.add(str_key);
                list.add(one_list);
            }

            String para = kqParam.getDURATION_OVERTIME_MAX_LIMIT();
            if (para == null || para.length() <= 0)
            	para = "-1";
            int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额
            
            if ("q11".equals(ta) && overtimeLimit > 0) {
            	float overtimeLen = 0;
            	overtimeLen = annualApply.getKqdurationOverTimelen(nbase, a0100, "1");//计算考勤期间内的加班时长
                
                if ((overtimeLen + apptimeLen > overtimeLimit) && apptimeLen > 0) {
                    this.getFormHM().put("errorMessage",SafeCode.encode("所选申请单时长为" + PubFunc.round(""+apptimeLen,2) + "小时，本期内已申请的加班时长为" 
							+ PubFunc.round(""+overtimeLen,2) + "小时，合计已超出加班限额规定的" + PubFunc.round(""+overtimeLimit,2) + "小时。"));
                    return;
                }
            }
            
            sql.setLength(0);
            sql.append("update " + table + " set ");
            sql.append(ta + "z5 =? ");
            sql.append(" where " + ta + "01=? ");
            sql.append(" and (" + ta + "z5='01' or " + ta + "z5='07')");
            dao.batchUpdate(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        this.getFormHM().put("sp_flag", "");
    }

}
