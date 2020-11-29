package com.hjsj.hrms.redevelopment.extractexperter.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

/**
 * @Title: DeleteProjectInforTrans.java
 * @Description: 项目信息管理主界面--删除项目信息
 * @Company: hjsj
 * @Create time: 2015-11-26 下午01:53:16
 * @author chenxg
 * @version 1.0
 */
public class DeleteProjectInforTrans extends IBusiness {

    public void execute() throws GeneralException {
        String projectid = (String) this.getFormHM().get("projectIds");
        try {
            if (StringUtils.isEmpty(projectid))
                return;
            String id[]=projectid.split(",");
            String message=null;
            ContentDAO dao = new ContentDAO(this.frameconn);
            for(int i=0;i<id.length;i++){
                String sql = "delete from n03 where n0301 = " + id[i] + " and (N0311 is null  or N0311='') and N0312 is null";
               int num= dao.update(sql);
               if(num==0){
            	 message="无法删除已录入招标结果的项目";  
               }else{
            	   sql="delete from n04 where n0402 = "+ id[i] +"";
            	   dao.update(sql);
               }
            }
            this.getFormHM().put("messages", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
