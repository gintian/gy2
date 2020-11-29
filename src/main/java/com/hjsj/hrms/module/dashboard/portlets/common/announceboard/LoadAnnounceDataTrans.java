package com.hjsj.hrms.module.dashboard.portlets.common.announceboard;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 获取公共内容
 * @author ZhangHua
 * @date 17:05 2020/8/3
 */
public class LoadAnnounceDataTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {


        String id= PubFunc.decrypt((String) this.getFormHM().get("id"));

        HashMap resultMap=new HashMap();

        String sql="select topic,content,period,fileid, ";
        sql+= Sql_switcher.dateToChar("approvetime","yyyy-MM-dd hh:mm:ss");
        sql+=" approvetime from announce where id=?";


        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try(RowSet rowSet=dao.search(sql, Arrays.asList(new String[]{id}))){
            if(rowSet.next()){
                resultMap.put("topic",rowSet.getString("topic"));
                resultMap.put("content",rowSet.getString("content"));
                resultMap.put("approvetime",rowSet.getString("approvetime"));
                resultMap.put("period",rowSet.getString("period"));
                resultMap.put("fileid",rowSet.getString("fileid"));
            }


        }catch (Exception e){
            e.printStackTrace();
            throw new GeneralException(e.getMessage());
        }

        this.getFormHM().put("announceData",resultMap);

    }
}
