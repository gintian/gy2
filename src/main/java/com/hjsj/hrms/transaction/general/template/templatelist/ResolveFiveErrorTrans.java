/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 类名称:ResolveFiveErrorTrans
 * 类描述:
 * 创建人: xucs
 * 创建时间:2013-10-26 下午06:11:23 
 * 修改人:xucs
 * 修改时间:2013-10-26 下午06:11:23
 * 修改备注:
 * @version
 *
 */
public class ResolveFiveErrorTrans extends IBusiness {

    public void execute() throws GeneralException {
      this.getFormHM().put("resloveby", "xcs");
      String check = (String) this.getFormHM().get("check");
      if("check".equalsIgnoreCase(check)){
          String tabid=(String) this.getFormHM().get("tabid");
          String query = (String) this.getFormHM().get("query");
          this.getFormHM().put("tabid", tabid);
          this.getFormHM().put("query", query);
      }
      
    }

}
