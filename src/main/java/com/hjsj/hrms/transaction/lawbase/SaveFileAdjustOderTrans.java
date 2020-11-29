package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveFileAdjustOderTrans extends IBusiness {
	
	public void execute() throws GeneralException {

        String[] lawbase = (String[]) this.getFormHM().get("lawbase");
        if (lawbase == null || lawbase.length <= 0)
            return;
        
        int index=0;
        ArrayList<ArrayList<String>> valuesList = new ArrayList<ArrayList<String>>();
        for (int i = lawbase.length - 1; i >= 0; i--) {
            ArrayList<String> paramList = new ArrayList<String>();
            paramList.add((i + 1) + "");
            paramList.add(lawbase[index]);
            valuesList.add(paramList);
            index++ ;
        }
        //【59206】v771文档管理：文档维护中，节点下的调整顺序不能调整文档维护，调整的文档浏览也不对，而且被升降序排序后显示的也不对
        this.getFormHM().put("order_name", "");
        this.getFormHM().put("order_type", "");
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            String sql = "update law_base_file set fileorder =? where file_id =?";
            dao.batchUpdate(sql, valuesList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
