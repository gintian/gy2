package com.hjsj.hrms.module.projectmanage.project.transaction;

import com.hjsj.hrms.module.projectmanage.project.businessobject.ProjectManageBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 项目管理导出excle
 * 
 * @Title:        ExportProjectManageTrans.java
 * @Description:  项目管理导出excle
 * @Company:      hjsj     
 * @Create time:  2016-1-8 下午05:45:30
 * @author        chenxg
 * @version       1.0
 */
public class ExportProjectManageTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String p1119 = (String) this.getFormHM().get("itemId");
            if(StringUtils.isNotEmpty(p1119))
                p1119 = PubFunc.decrypt(p1119);
            
            String projectIds = (String) this.getFormHM().get("projectIds");
            projectIds = decryption(projectIds);
            String milestoneIds = (String) this.getFormHM().get("milestoneIds");
            milestoneIds = decryption(milestoneIds);
            
            String where = (String) this.userView.getHm().get("projectWhere");
            ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);// 导出工具类
            ProjectManageBo bo = new ProjectManageBo(this.userView, this.frameconn);// 变动比对工具类
            
            String fileName = "pm_" + this.userView.getUserName() + ".xls";
            //获取导出的表头信息
            ArrayList<LazyDynaBean> headList = bo.getExcleHeadList("projectmanage_0001");
            //获取导出数据的查询语句
            String sql = bo.getExcleSql(p1119, where, projectIds, milestoneIds);
            /** 导出excel */
            excelUtil.exportExcelBySql(fileName, null, null, headList, sql, null, 0);

            this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String decryption(String cipherText){
        if(StringUtils.isEmpty(cipherText))
            return "";
        
        String Express = "";
        String[] cipherTexts = cipherText.split(",");
        for(int i = 0; i < cipherTexts.length; i++){
            if(StringUtils.isEmpty(cipherTexts[i]))
                continue;
            
            Express += PubFunc.decrypt(cipherTexts[i]) + ",";
        }
        
        if(Express.endsWith(","))
            Express = Express.substring(0, Express.length() - 1);
        
        return Express;
    }

}
