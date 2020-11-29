package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.module.kq.util.KqPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 删除假期管理数据
 * @Title:        DeleteHolidayTrans.java
 * @Description:  处理删除假期数据的交易类
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:01:46
 * @author        chenxg
 * @version       1.0
 */
public class DeleteHolidayTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        StringBuffer msg = new StringBuffer();
        try {
            ArrayList<String> datas = (ArrayList<String>) this.getFormHM().get("datas");
            ArrayList<ArrayList<String>> valuesList = new ArrayList<ArrayList<String>>();
            for(int i = 0; i < datas.size(); i++){
                String data = datas.get(i);
                String[] valueData = data.split(":");
                if(valueData.length < 3)
                    continue;
                // 34598 删除时传参primaryKey代替库前缀和人员编号，防止栏目设置隐藏
                String primaryKey = valueData[0];
                primaryKey = PubFunc.decrypt(primaryKey);
                if(primaryKey.indexOf("|") == -1)
                	continue;
                String nbase = primaryKey.split("\\|")[0];
                String a0100 = primaryKey.split("\\|")[1];
                
                String q1701 = valueData[1];
                String q1709 = valueData[2];
                q1709 = StringUtils.isEmpty(q1709) ? "" : q1709.substring(0, q1709.indexOf("`"));
                
                ArrayList<String> valueList = new ArrayList<String>();
                
                valueList.add(nbase);
                valueList.add(a0100);
                valueList.add(q1701);
                valueList.add(q1709);
                valuesList.add(valueList);
            }
            
            StringBuffer sql = new StringBuffer();
            sql.append("delete from q17");
            sql.append(" where nbase=?");
            sql.append(" and a0100=?");
            sql.append(" and q1701=?");
            sql.append(" and q1709=?");
            String where = KqPrivBo.getKqEmpPrivWhr(this.frameconn, this.userView, "Q17");
            if(!this.userView.isSuper_admin() && StringUtils.isNotEmpty(where))
                sql.append("and " + where);
            
            ContentDAO dao = new ContentDAO(this.frameconn);
            dao.batchUpdate(sql.toString(), valuesList);
            
        } catch (Exception e) {
            e.printStackTrace();
            msg.append("删除数据失败！");
        } finally {
            if(StringUtils.isEmpty(msg.toString()))
                msg.append("删除数据成功！");
            
            this.getFormHM().put("msg", msg.toString());
        }
        
    }

}
