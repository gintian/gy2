package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class OpenOrgCardTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String z0321 = (String) hm.get("z0321");
            String isNew = (String) hm.get("new");
            hm.remove("new");
            
            /**招聘安全改造,判断当前操作用户是否有查看的权限
             * 新增需求时不检查
             **/
            if (null == isNew || !"1".equals(isNew)) {
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                String sql = (String) this.userView.getHm().get("hire_sql");
                int index = sql.indexOf("order by");
                if (index != -1) {
                    sql = sql.substring(0, index);
                }
                sql = sql + " and (z0321='" + z0321 + "' or z0325='" + z0321 + "')";
                this.frowset = dao.search(sql);
                if (!this.frowset.next()) {
                    /**将From中的参数清空,否则界面上还是会展示的**/
                    this.getFormHM().put("z0321", "");
                    this.getFormHM().put("orgWillTableId", "");
                    throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.contorl"));
                }
            }

            //取配置参数中的登记表设置
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
            HashMap map = parameterXMLBo.getAttributeValues();
            String orgWillTableId = "#";
            if (map != null && map.get("orgWillTableId") != null) {
                orgWillTableId = (String) map.get("orgWillTableId");
            }
            this.getFormHM().put("z0321", (String) hm.get("z0321"));
            this.getFormHM().put("orgWillTableId", orgWillTableId);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

}
