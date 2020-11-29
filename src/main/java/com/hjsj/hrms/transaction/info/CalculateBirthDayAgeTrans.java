package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

/**
 * @author Owner
 *
 */
public class CalculateBirthDayAgeTrans extends IBusiness {

    public void execute() throws GeneralException {
        String idcard = (String) this.getFormHM().get("idcardvalue");
        try {
            String birthdayvalue = new SortFilter().getBirthDay(idcard);
            if(StringUtils.isNotEmpty(birthdayvalue) && "false".equalsIgnoreCase(checkMothAndDay(birthdayvalue))) {
                throw new GeneralException(ResourceFactory.getProperty("workbench.info.idcardValue.error"));
            }
            
            this.getFormHM().put("birthdayvalue", birthdayvalue);
            this.getFormHM().put("agevalue", new SortFilter().getAge(idcard));
            this.getFormHM().put("axvalue", new SortFilter().getSex(idcard));
        } catch (Exception e) {
            throw new GeneralException(ResourceFactory.getProperty("workbench.info.idcardValue.error"));
        }
    }

    /**
     * 校验月与日是否符合规则
     * 
     * @param date
     *            日期数据
     * @return
     */
    private String checkMothAndDay(String date) {
        String tempDate = "false";
        String[] dates = date.split("[.]");
        if (dates[0].length() > 0 && dates[1].length() > 0 && dates[2].length() > 0) {
            int year = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]);
            int day = Integer.parseInt(dates[2]);
            switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: {
                if (1 <= day && day <= 31)
                    tempDate = date;

                break;
            }
            case 4:
            case 6:
            case 9:
            case 11: {
                if (1 <= day && day <= 30)
                    tempDate = date;

                break;
            }
            case 2: {
                if (isLeapYear(year)) {
                    if (1 <= day && day <= 29)
                        tempDate = date;

                } else {
                    if (1 <= day && day <= 28)
                        tempDate = date;
                }
                break;
            }
            }
        }
        return tempDate;
    }

    /**
     * 闰年的条件是： ① 能被4整除，但不能被100整除； ② 能被100整除，又能被400整除。
     * 
     * @param year
     * @return
     */
    private boolean isLeapYear(int year) {
        boolean t = false;
        if (year % 4 == 0) {
            if (year % 100 != 0) {
                t = true;
            } else if (year % 400 == 0) {
                t = true;
            }
        }
        return t;
    }
}
