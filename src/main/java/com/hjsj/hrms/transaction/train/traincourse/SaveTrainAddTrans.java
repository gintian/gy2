package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.train.TrainAddBo;
import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * <p>
 * Title:TrainAddTrans.java
 * </p>
 * <p>
 * Description:保存培训通用添加
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-13 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveTrainAddTrans extends IBusiness {

    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String fieldsetid = (String) hm.get("fieldset");

        TrainAddBo bo = new TrainAddBo(fieldsetid, this.frameconn);
        String primaryField = bo.getPrimaryField();
        String priFldValue = (String) this.getFormHM().get("priFldValue");
        String hideSaveFlds = (String) this.getFormHM().get("hideSaveFlds");
        String initValue = (String) hm.get("initValue");// 需要初始化的字段1:初始值1,初始值字段2:初始值2,
        initValue = SafeCode.decode(initValue); // 初始值是中文名称时候需要转码

        if (priFldValue != null && priFldValue.length() > 0)
            priFldValue = PubFunc.decrypt(SafeCode.decode(priFldValue));
        // zxj 如果remove掉这两个参数，将导致培训班下培训课程无法“保存&继续”方式添加
        // hm.remove("priFldValue");
        // hm.remove("hideSaveFlds");

        HashMap hideSaveFldsMap = bo.getInitValueMap(hideSaveFlds);

        String recTable = bo.getRecTable();
        ContentDAO dao = new ContentDAO(this.getFrameconn());

        boolean isNew = bo.isNew(priFldValue);
        ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldlist");

        String stratdate = "";
        String enddate ="";
        String datestr = "";
        String dateend = "";
        RecordVo vo = new RecordVo(recTable);
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem fieldItem = (FieldItem) fieldlist.get(i);

            String itemid = fieldItem.getItemid();
            String value = fieldItem.getValue();

            if ("D".equals(fieldItem.getItemtype())) {
                if ("r3113".equalsIgnoreCase(itemid)) {
                        stratdate = PubFunc.replace(value, ".", "-");
                } else if ("r3114".equalsIgnoreCase(itemid)) {
                        enddate = PubFunc.replace(value, ".", "-");
                } else if ("r3115".equalsIgnoreCase(itemid)) {// 如果时r31表注意r3115,r3116的日期要精确到时分秒
                    String r3115_time = (String) hm.get("r3115_time");
                    if (value != null && value.length() > 0)
                        value += " " + r3115_time;
                    datestr = value;
                } else if ("r3116".equalsIgnoreCase(itemid)) {
                    String r3116_time = (String) hm.get("r3116_time");
                    if (value != null && value.length() > 0)
                        value += " " + r3116_time;
                    dateend = value;
                }

                Timestamp date = null;

                if(StringUtils.isNotEmpty(value)) {
                    int length = fieldItem.getItemlength();
                    // 61820 兼容前台传过来的数据不是严格符合要求长度的情况，比如本来是需要时间部分的，但只传过来日期
                    if (value.length() < length)
                        length = value.length();
                    value = PubFunc.replace(value, ".", "-");
                    String format = "yyyy-MM-dd";
                    if(length == 4)
                        format = "yyyy";
                    else if(length == 7)
                        format = "yyyy-MM";
                    else if(length == 13)
                        format = "yyyy-MM-dd HH";
                    else if(length == 16)
                        format = "yyyy-MM-dd HH:mm";
                    else if(length >= 18)
                        format = "yyyy-MM-dd HH:mm:ss";

                	date = DateUtils.getTimestamp(value, format);
                }
                
                vo.setDate(itemid, date);
            } else if ("N".equals(fieldItem.getItemtype()))// 对于数值类型，在前后台都要进行控制,前台验证是整数还是小数类型，后台修正小数位数
            {
                value = PubFunc.round(value, fieldItem.getDecimalwidth());
                double number = Double.parseDouble(value);
                vo.setDouble(itemid, number);
            } else if ("r3117".equalsIgnoreCase(itemid)) {
                value = value != null ? value : "";
                value = PubFunc.keyWord_reback(value);
                vo.setString(itemid, value);
            } else if ("r3108".equalsIgnoreCase(itemid)) {
                value = value != null ? value : "";
                value = PubFunc.keyWord_reback(value);
                value = value.replaceAll("\r\n", "\n");
                vo.setString(itemid, value);
            }else
                vo.setString(itemid, value);
        }
        
        if ("r31".equalsIgnoreCase(fieldsetid)) {
            String r3130 = vo.getString("r3130");
            if(r3130 == null || r3130.length() < 1)
                r3130 = ResourceFactory.getProperty("train.info.class.newname");
            String flag = TrainClassBo.checkClassDate(r3130, stratdate, enddate, datestr, dateend);
            if (!"true".equalsIgnoreCase(flag))
                throw new GeneralException("", flag, "", "");

        }
        // 保存主键字段
        vo.setString(primaryField, priFldValue);
        if ("r40".equalsIgnoreCase(fieldsetid)) {
            String classid = (String) this.getFormHM().get("r3101");
            vo.setString("r4005", classid);
            vo.setString("nbase", getNbase(priFldValue, classid));
        }
        // 保存在页面不显示出来，但付了值需要保存的字段
        if (hideSaveFldsMap.size() > 0) {
            Set fields = hideSaveFldsMap.keySet();
            for (Iterator iter = fields.iterator(); iter.hasNext();) {
                String field = (String) iter.next();
                String value = (String) hideSaveFldsMap.get(field);
                if (vo.hasAttribute(field))
                    vo.setString(field, value);
            }
        }

        if (isNew) {
            DbWizard dbw = new DbWizard(this.getFrameconn());
            // 如果存在i9999字段,首先将所有该字段非空记录加1,再将要保存的记录该字段设为1
            if (dbw.isExistField(fieldsetid, "i9999", false)) {
                String sql = "update " + fieldsetid + " set i9999=i9999+1 where i9999 is not null";
                try {
                    dao.update(sql);
                    vo.setInt("i9999", 1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            HashMap initVals = bo.getInitValueMap(initValue);
            Iterator iter = initVals.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String itemid = (String) entry.getKey();
                String value = (String) entry.getValue();
                String val = vo.getString(itemid);
                if(StringUtils.isEmpty(val)) {
                    if("r2508".equals(itemid) || "r3118".equals(itemid))
                        vo.setDate(itemid, value);
                    else
                        vo.setString(itemid, value);
                }
            }
            
            dao.addValueObject(vo);
            // 培训计划预算
            if ("r25".equalsIgnoreCase(fieldsetid) && "03".equals(vo.getString("r2509"))) {
                TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
                if (tbb.getBudget() != null && tbb.getBudget().length() > 0)
                    tbb.updateTrainPlanBudget("0", priFldValue, -999999, false);
            }
            // 培训班预算
            if ("r31".equalsIgnoreCase(fieldsetid) && "03".equals(vo.getString("r3127"))) {
                TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
                if (tbb.getBudget() != null && tbb.getBudget().length() > 0)
                    tbb.updateTrainBudget("0", priFldValue, -999999, null);
            }
        } else
            try {
                // 培训班预算
                if ("r31".equalsIgnoreCase(fieldsetid) && "03".equals(vo.getString("r3127"))) {
                    TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
                    if (tbb.getBudget() != null && tbb.getBudget().length() > 0)
                        tbb.updateTrainBudget("2", vo.getString("r3101"), Double.parseDouble(vo.getString("r3111")), vo.getString("r3125"));
                }

                String classid = this.formHM.get("r3101").toString();
                if ("r41".equals(recTable))
                    vo.setString("r4103", classid);

                dao.updateValueObject(vo);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        if ("r31".equalsIgnoreCase(fieldsetid))// 培训班模块 自动计算学时
        {
            String isAutoHour = (String) hm.get("isAutoHour");

            if (isAutoHour != null && "1".equals(isAutoHour)) {
                TransDataBo bo1 = new TransDataBo(this.getFrameconn());
                String theHour = bo1.getStudyHour();

                if (theHour == null || theHour.trim().length() < 1)
                    throw GeneralExceptionHandler.Handle(new GeneralException("", "标准学时不能为空，请先设置标准学时！", "", ""));

                if (!"".equals(theHour))
                    bo1.autoCalculateHour(theHour, priFldValue);

                String oper = (String) hm.get("oper");
                if ("saveClose".equalsIgnoreCase(oper))
                    hm.remove("isAutoHour");
            }
        }

    }

    private String getNbase(String r4001, String classid) {
        try {
            String sql = "select nbase from r40 where r4001='" + r4001 + "' and r4005='" + classid + "'";
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(sql);
            if (this.frowset.next())
                return this.frowset.getString("nbase");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
