package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.module.kq.util.KqPrivBo;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.kq.util.KqVer;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
/**
 * 查询计算指标
 * @Title:        SearchHolidayCalculateFieldTrans.java
 * @Description:  假期管理查询计算指标调用的交易类
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:12:07
 * @author        chenxg
 * @version       1.0
 */
public class SearchHolidayCalculateFieldTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {

        String holidayYear = (String) this.getFormHM().get("holidayYear");
        String holidayType = (String) this.getFormHM().get("holidayType");
        if(StringUtils.isNotEmpty(holidayType))
            holidayType = PubFunc.decrypt(holidayType);
        
        int year = 0;
        if (holidayYear == null || holidayYear.length() <= 0) {
            Calendar now = Calendar.getInstance();
            Date cur_d = now.getTime();
            year = DateUtils.getYear(cur_d);
        } else {
            year = Integer.parseInt(holidayYear);
        }

        Date d1 = DateUtils.getDate(year, 1, 1);
        Date d2 = DateUtils.getDate(year, 12, 31);
        String feast_start = DateUtils.format(d1, "yyyy-MM-dd");
        String feast_end = DateUtils.format(d2, "yyyy-MM-dd");
        HolidayBo bo = new HolidayBo(this.getFrameconn(), this.userView);
        ArrayList<HashMap<String,String>> fieldlist = bo.getKqFormulaFields();
        StringBuffer fieldJson = new StringBuffer("[");
        for (int i = 0; i < fieldlist.size(); i++) {
            HashMap<String,String> map = fieldlist.get(i);
            String field = map.get("itemname");
            String content = bo.getParameter("", field, holidayType, holidayYear);
            if (StringUtils.isNotEmpty(content))
                fieldJson.append("{fielditem:'" + map.get("hzname") + "'},");
        }

        if(fieldJson.toString().endsWith(","))
            fieldJson.setLength(fieldJson.length() - 1);
        
        fieldJson.append("]");
        
        // 是否存在上年结余字段
        if (this.getBalance().length() > 0)
            this.getFormHM().put("existBalance", "1");
        else
            this.getFormHM().put("existBalance", "0");

        // 是否存在结余截止日期字段
        String balanceEnd = KqUtilsClass.getBalanceEnd();
        if (balanceEnd.length() > 0)
            this.getFormHM().put("existBalanceEnd", "1");
        else
            this.getFormHM().put("existBalanceEnd", "0");
        
        this.getFormHM().put("fieldJson", fieldJson.toString());
        this.getFormHM().put("feastStart", feast_start);
        this.getFormHM().put("feastEnd", feast_end);
        this.getFormHM().put("dbpre", "All");
        this.getFormHM().put("nbaseJson", getDbase());

    }

    /**
     * 获得上年结余的字段名称
     * 
     * @return
     */
    public String getBalance() {
        // 获得年假结余的列名
        String balance = "";
        ArrayList fieldList = DataDictionary.getFieldList("q17", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem item = (FieldItem) fieldList.get(i);
            if ("上年结余".equalsIgnoreCase(item.getItemdesc()))
                balance = item.getItemid();
        }

        return balance;
    }
    /**
     * 获取人员库下拉框的数据
     * @return
     * @throws GeneralException
     */
    public String getDbase() throws GeneralException {
        StringBuffer nbaseJson = new StringBuffer("[{id:'All',name:'全部人员库'}");
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            
            String nbases = "";
            
            KqVer kqVer = new KqVer();
            
            // 标准版考勤
            if (kqVer.getVersion() == KqConstant.Version.STANDARD) {
                nbases = KqPrivBo.getKqParameter(this.frameconn).get("nbase");
            } else {
                nbases = (String)KqPrivForHospitalUtil.getKqParameter(this.frameconn).get("nbase");
            }
            
            String[] nbase = nbases.split(","); 
            this.frowset = dao.search("select * from dbname");
            while (this.frowset.next()) {
                String dbpre = this.frowset.getString("pre");
                for (int i = 0; i < nbase.length; i++) {
                    String userbase = nbase[i];
                    if (StringUtils.isEmpty(dbpre) || !dbpre.equalsIgnoreCase(userbase))
                        continue;
                    
                    nbaseJson.append(",{id:'" + this.frowset.getString("pre") + "',");
                    nbaseJson.append("name:'" + this.frowset.getString("dbname") + "'}");
                }
            }
            
            nbaseJson.append("]");
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
        
        return nbaseJson.toString();
    }
}
