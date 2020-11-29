package com.hjsj.hrms.utils.components.codeselector;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * <p>Description: 人民大学招聘外网获取发布了职位的单位</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2018-1-19</p>
 * @author wangjl
 * @version 1.0
 */
public class GetZPOrganization extends CodeDataFactory {

	@Override
	public ArrayList createCodeData(String codesetid, String code, UserView userView) {
		ArrayList list = getCodeList(codesetid, userView);
        return list;
    
	}
	
	private ArrayList getCodeList(String codesetid, UserView userView) {
		ArrayList list = new ArrayList();
        RowSet rs = null;
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            Calendar d = Calendar.getInstance();
            int year = d.get(Calendar.YEAR);
            int month = d.get(Calendar.MONTH) + 1;
            int day = d.get(Calendar.DATE);
            StringBuffer str = new StringBuffer();
            str.append("select * from ORGANIZATION ");
            str.append(" left join z03 on codeitemid=z0321 ");
            str.append(" where codeitemid=z0321 and z0319='04' and (codesetid=? or codesetid=?) ");
            str.append(getDateSql(">=", "Z0329"));
            str.append(getDateSql("<=", "Z0331"));
            str.append(" order by a0000");
            ArrayList<String> value = new ArrayList<String>();
            value.add(codesetid);
            value.add(codesetid);
            rs = dao.search(str.toString(), value);
            while (rs.next()) {
                HashMap map = new HashMap();
                map.put("id", rs.getString("codeitemid"));
                map.put("text", rs.getString("codeitemdesc"));
                map.put("parentid", rs.getString("parentid"));
                map.put("leaf", Boolean.TRUE);
                map.put("codesetid", codesetid);
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeDbObj(conn);
            PubFunc.closeResource(rs);
        }
        return list;
	}

	@Override
	public ArrayList searchCodeByText(String codesetid, String text, UserView userView) {
		ArrayList list = getCodeList(codesetid, userView);
		ArrayList searchList = new ArrayList();
		if(StringUtils.isEmpty(text))
			return list;
		for (Object obj : list) {
			HashMap map = (HashMap) obj;
			String codeitemdesc = (String) map.get("text");
			if(codeitemdesc.toLowerCase().contains(text.toLowerCase())) 
				searchList.add(map);
        }
        return searchList;
	}
	
	private String getDateSql(String operate, String itemid) {
        StringBuffer sql = new StringBuffer("");
        Calendar d = Calendar.getInstance();
        int year = d.get(Calendar.YEAR);
        int month = d.get(Calendar.MONTH) + 1;
        int day = d.get(Calendar.DATE);
        if (">".equals(operate) || "<".equals(operate)) {
            sql.append(" and ( " + year + operate + Sql_switcher.year(itemid));
            sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and " + month + operate
                    + Sql_switcher.month(itemid) + "  )");
            sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and "
                    + Sql_switcher.month(itemid) + "=" + month + " and " + day + operate
                    + Sql_switcher.day(itemid) + "  )");
            sql.append(" ) ");
        } else if (">=".equals(operate) || "<=".equals(operate)) {
            if (">=".equals(operate))
                sql.append(" and ( " + year + ">" + Sql_switcher.year(itemid));
            else
                sql.append(" and ( " + year + "<" + Sql_switcher.year(itemid));

            if (">=".equals(operate))
                sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and " + month + ">"
                        + Sql_switcher.month(itemid) + "  )");
            else
                sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and " + month + "<"
                        + Sql_switcher.month(itemid) + "  )");

            sql.append(" or (" + Sql_switcher.year(itemid) + "=" + year + " and "
                    + Sql_switcher.month(itemid) + "=" + month + " and " + day + operate
                    + Sql_switcher.day(itemid) + "  )");
            sql.append(" ) ");
        } else if ("=".equals(operate))
            sql.append(" and (" + Sql_switcher.year(itemid) + "=" + year + " and "
                    + Sql_switcher.month(itemid) + "=" + month + " and " + Sql_switcher.day(itemid)
                    + "=" + day + "  )");

        return sql.toString();
    }

}
