package com.hjsj.hrms.transaction.kq.options.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title: GetCodeTrans </p>
 * <p>Description: 取日明细中某代码指标的代码列表</p>
 * <p>Company: hjsj</p>
 * <p>modify time: 2013-8-29 上午10:38:58</p>
 * @author guodd
 * @version 1.0
 * 
 * @author zhaoxj
 * @version 2.0
 * 更正小写指标查不到的问题；代码逻辑及格式等优化
 */
public class GetCodeTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            this.formHM.put("codelist", null);
            
            String itemid = this.getFormHM().get("itemid").toString();

            ArrayList codelist = new ArrayList();
            ContentDAO dao = new ContentDAO(this.frameconn);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String backdate = sdf.format(new Date());

            StringBuffer sql = new StringBuffer();
            sql.append("select codesetid");
            sql.append(" from t_hr_busifield");
            sql.append(" where fieldsetid='Q03'");
            sql.append(" and UPPER(itemid)='" + itemid.toUpperCase() + "'");

            String codesetid = "";
            this.frowset = dao.search(sql.toString());
            //没找到指标
            if (!frowset.next())
                return;

            codesetid = frowset.getString("codesetid");
            //该指标不是代码项
            if (codesetid == null || codesetid.trim().length() <= 0 || "0".equals(codesetid))
                return;

            sql.delete(0, sql.length());
            if ("@K".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid)) {
                sql.append("select codeitemid codevalue,codeitemdesc codedesc");
                sql.append(" from organization");
                sql.append(" where " + Sql_switcher.dateValue(backdate) + " between start_date and end_date");
                sql.append(" and codesetid='" + codesetid + "'");
            } else if ("@@".equalsIgnoreCase(codesetid))
                sql.append("select Pre as codevalue,DBName as codedesc from dbname");
            else {
                sql.append("select codeitemid codevalue,codeitemdesc codedesc");
                sql.append(" from codeitem");
                sql.append(" where codesetid='");
                sql.append(codesetid);
                sql.append("' order by codeitemid");
            }

            CommonData dataobj = null;
            frowset = dao.search(sql.toString());
            while (frowset.next()) {
                String codevalue = frowset.getString("codevalue");
                String codedesc = frowset.getString("codedesc");

                dataobj = new CommonData(codevalue, codevalue + ":" + codedesc);
                codelist.add(dataobj);
            }
            dataobj = new CommonData("", "");
            codelist.add(0, dataobj);

            this.formHM.put("codelist", codelist);
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

}
