package com.hjsj.hrms.taglib.kq;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;

/**
 * 
 * <p>
 * Title:代销假，销假以后不销假按钮不显示
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Mar 26, 2010:3:36:26 PM
 * </p>
 * 
 * @author wangyao
 * @version 1.0
 *
 */
public class ConsignmentTag extends BodyTagSupport {
    // 申请单号
    private String id;
    // 自助的还是业务的
    private String flag;
    // 考勤申请表
    private String table;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int doStartTag() throws JspException {
        // if(this.flag!=null&&this.flag.equals("0"))//自助

        if (this.flag != null && "1".equals(this.flag)) {
            if (!reUser(id)) {
                return (EVAL_BODY_INCLUDE);
            }
        }
        return (SKIP_BODY);
    }

    public int doEndTag() throws JspException {
        return (EVAL_PAGE);
    }

    public boolean reUser(String id) {
        StringBuffer sql = new StringBuffer();
        sql.append("select ").append(table.toLowerCase()).append("z5");
        sql.append(" from ").append(table);
        sql.append(" where ").append(table).append("19='" + id + "'");
        sql.append(" and ").append(table).append("17=1");

        ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sql.toString());
        if (mylist == null || mylist.size() <= 0)
            return false;

        boolean isCorrect = false;
        String q15z5 = "";
        try {
            for (int i = 0; i < mylist.size(); i++) {
                LazyDynaBean dynabean = (LazyDynaBean) mylist.get(i);
                q15z5 = (String) dynabean.get(table.toLowerCase() + "z5");
                if (q15z5 == null || q15z5.length() <= 0)
                    q15z5 = "";
                if ("03".equals(q15z5)) {
                    isCorrect = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCorrect;
    }
}
