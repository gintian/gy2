package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;

public class SearchEmpRecordTrans extends IBusiness
{
    /**
     * 判断员工基本表中是否添加有新的员工
     * 
     * */
    public void execute() throws GeneralException
    {
        String userbase = (String) this.getFormHM().get("userbase");
        String registerdate = (String) this.getFormHM().get("registerdate");
        StringBuffer selectsql = new StringBuffer();

        selectsql.append("select A0100,B0110,E0122,A0101 ");
        selectsql.append(" from " + userbase + "A01 ");
        selectsql.append(" where a0100 not in (");
        selectsql.append(" select a0100 from Q03 where ");
        selectsql.append(" nbase='" + userbase + "'");
        selectsql.append("and Q03Z0 ='" + registerdate + "'");
        selectsql.append(")");
        String weekName = "";
        try
        {
            if (registerdate != null && registerdate.length() > 0)
            {
                Date date = DateUtils.getDate(registerdate, "yyyy-mm-dd");
                weekName = KqUtilsClass.getWeekName(date);
            }
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            ArrayList user_list = new ArrayList();

            if (!weekName.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.sunday")) 
                    && !weekName.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.Saturday")))
            {
                this.frowset = dao.search(selectsql.toString());
                while (this.frowset.next())
                {
                    RecordVo vo = new RecordVo("Q03");
                    vo.setString("nbase", userbase);
                    if (this.getFrowset().getString("A0100") != null)
                        vo.setString("a0100", this.getFrowset().getString("A0100"));
                    vo.setString("Q03Z0", registerdate);
                    if (this.getFrowset().getString("B0110") != null)
                        vo.setString("b0110", this.getFrowset().getString("B0110"));
                    if (this.getFrowset().getString("E0122") != null)
                        vo.setString("e0122", this.getFrowset().getString("E0122"));
                    if (this.getFrowset().getString("A0101") != null)
                        vo.setString("a0101", this.getFrowset().getString("A0101"));
                    vo.setString("checkflag", "01");
                    vo.setInt("status", 0);
                    user_list.add(vo);
                }
                if (user_list != null && user_list.size() > 0)
                {
                    dao.addValueObject(user_list);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }
}
