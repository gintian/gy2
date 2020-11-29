package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @Title: ChartLinkageTrans.java
 * @Description: 总裁桌面统计图联动--获取需要刷新的区域id
 * @Company: hjsj
 * @Create time: 2015-11-2 上午10:28:34
 * @author chenxg
 * @version 1.0
 */
public class ChartLinkageTrans extends IBusiness {

    public void execute() throws GeneralException {
        String pageid = (String) this.getFormHM().get("pageid");
        if (pageid == null || pageid.length() < 1)
            return;

        String panelids = "";
        try {
            String sql = "select RegionId from t_sys_page_region where Linkage=2 and PageId=" + pageid;
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql);
            while(this.frowset.next()){
                String RegionId = this.frowset.getString("RegionId");
                if(RegionId == null || RegionId.length() < 1)
                    continue;
                
                panelids += RegionId + ",";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(panelids != null && panelids.length() > 0)
            panelids = panelids.substring(0, panelids.length());
        else
            panelids = "";
        this.getFormHM().put("panelids", panelids);

    }

}
