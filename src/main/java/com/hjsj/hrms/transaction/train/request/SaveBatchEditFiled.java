/**
 * 
 */
package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:CourseTransAdd
 * </p>
 * <p>
 * Description:培训修改培训学员用户指标
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-01 12:02:55
 * </p>
 * 
 * @author LiWeichao
 * @version 1.0
 * 
 */
public class SaveBatchEditFiled extends IBusiness {

	public void execute() throws GeneralException {
		String stuids = (String)this.getFormHM().get("stuids");
		stuids = stuids!=null&&stuids.length()>0?stuids.substring(0, stuids.length()-1):"";
		String itemids = (String)this.getFormHM().get("itemids");
		itemids = itemids!=null&&itemids.length()>0?itemids.substring(0, itemids.length()-1):"";
		String values = (String)this.getFormHM().get("values");
		values = values==null?"":values.replaceAll(",", " , ");
		values = values!=null&&values.length()>0?values.substring(0, values.length()-1):"";
		String classid = (String)this.getFormHM().get("classid");
		
		String[] stuid = stuids.split(",");
		int n = 0;
        String id = "";
        ArrayList list = new ArrayList();

        for (int i = 0; i < stuid.length; i++) {
            if (n > 0)
                id += ",";
            id += "'" + PubFunc.decrypt(SafeCode.decode(stuid[i])) + "'";
            n++;

            if (n == 1000) {
                list.add(id);
                id = "";
                n = 0;
            }
        }

        if (id != null && id.length() > 0) {
            list.add(id);
        }
		
		try {
			String itemid[] = itemids.split(",");
			String value[] = values.split(",");
			if(itemid.length>0){
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				ArrayList sqlList = new ArrayList();
                for (int m = 0; m < list.size(); m++) {
				StringBuffer strsql = new StringBuffer("update r40 set ");
				for (int i = 0; i < itemid.length; i++) {
					if("r4006".equalsIgnoreCase(itemid[i])||"r4007".equalsIgnoreCase(itemid[i]))
						strsql.append(itemid[i]+"="+Sql_switcher.dateValue(value[i].trim())+",");
					else
						strsql.append(itemid[i]+"='"+value[i].trim()+"',");
				}
				strsql.setLength(strsql.length()-1);
				strsql.append(" where r4001 in ("+list.get(m)+")");
				strsql.append(" and r4005='"+classid+"'");
				 sqlList.add(strsql.toString());
                }
				dao.batchUpdate(sqlList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
