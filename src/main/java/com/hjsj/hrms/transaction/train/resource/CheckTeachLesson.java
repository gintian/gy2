package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;

public class CheckTeachLesson extends IBusiness {

    public void execute() throws GeneralException {
        String delStr = (String) this.formHM.get("deletestr");
        String type = (String) this.formHM.get("type");

        String[] ids = null;
        if (delStr.indexOf(",") != -1)
            ids = delStr.split(",");
        else
            ids = delStr.split("/");
        String flag = "true";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                if ("true".equalsIgnoreCase(id))
                    continue;

                id = PubFunc.decrypt(SafeCode.decode(id));
                if (id == null || !(id.length() > 0))
                    continue;
                String sql = "select";
                if ("0".equalsIgnoreCase(type))
                    sql += " r1302 name from r13,r41,r31 where r13.r1301=r41.r4105 and r41.r4103=r31.r3101 and r13.r1301='" + id + "' and r31.r3127 in ('03','04','05','09')";
                else if ("1".equalsIgnoreCase(type))
                    sql += " r0102 name from r01,r31 where r01.r0101=r31.r3128 and r31.r3127 in ('03','04','05','09') and r01.r0101='" + id + "'";
                else if ("2".equalsIgnoreCase(type))
                    sql += " r0402 name from r04,r41,r31 where r04.r0401=r41.r4106 and r41.r4103=r31.r3101 and r04.r0401='" + id + "' and r31.r3127 in ('03','04','05','09')";
                else if ("3".equalsIgnoreCase(type))
                    sql += " r1011 name from r10,r31 where r10.r1001=r31.r3126 and r10.r1001='" + id + "' and r31.r3127 in ('03','04','05','09')";
                else if ("4".equalsIgnoreCase(type))
                    sql += " r1102 name from r11,r59 where r11.r1101=r59.r1101 and r11.r1101='" + id + "'";
                else if ("5".equalsIgnoreCase(type))
                    sql += " r0702 name from r07,r41,r31 where r07.r0701=r41.r4114 and r41.r4103=r31.r3101 and r07.r0701='" + id + "' and r31.r3127 in ('03','04','05','09')";
                else if ("course".equalsIgnoreCase(type)) {
                    sql += " r5003 name from r50,r41,r31 where";
                    this.frowset = dao.search("select 1 from t_hr_busifield where FieldSetId='R41' and itemid='R4118' and state =1 and useflag=1");
                    if (this.frowset.next())
                        sql += " r50.codeitemid=r41.r4118 and";

                    String where = getItermWhere();
                    if (where != null && where.length() > 0)
                        sql += " (" + where + ")  and";
                    else if (sql.indexOf("r4118") == -1)
                        continue;

                    sql += " r41.r4103=r31.r3101 and (r50.r5000='" + id + "' or r50.codeitemid='" + id + "') and r31.r3127 in ('03','04','05','09')";
                }

                this.frowset = dao.search(sql);
                if (this.frowset.next())
                    flag = flag + "," + this.frowset.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (flag.indexOf("true,") != -1)
            flag = flag.substring(5, flag.length());
        this.formHM.put("flag", flag);
    }
    /**
     * 获取r41中为类型为关联表并且关联了r50的指标并拼接成sql语句的查询条件
     * @return
     */
    private String getItermWhere() {
        String where = "";
        RowSet rs = null;
        try {
            String codesetid = "";
            ContentDAO dao = new ContentDAO(this.frameconn);
            String sql = "select codesetid from t_hr_relatingcode where UPPER(codetable)='R50'";
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                codesetid = this.frowset.getString("codesetid");
                String sqlfield = "select itemid from t_hr_busifield where fieldsetid='R41' and codesetid = '" + codesetid + "' and codeflag='1'";
                rs = dao.search(sqlfield);
                while (rs.next()) {
                    where += " r50.r5000=r41." + rs.getString("itemid") + " or ";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        if (where.endsWith("or "))
            where = where.substring(0, where.length() - 3);

        return where;
    }
}
