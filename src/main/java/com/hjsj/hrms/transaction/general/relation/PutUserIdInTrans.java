/**
 * 
 */
package com.hjsj.hrms.transaction.general.relation;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 类名称:PutUserIdInTrans
 * 类描述:把人员ID放入到userViewz中
 * 创建人: xucs
 * 创建时间:2013-10-15 下午03:37:34 
 * 修改人:xucs
 * 修改时间:2013-10-15 下午03:37:34
 * 修改备注:
 * @version
 *
 */
public class PutUserIdInTrans extends IBusiness {

    public void execute() throws GeneralException {
       String flag = (String) this.getFormHM().get("flag");
       String objectIDs=(String) this.getFormHM().get("objectIDs");
       this.userView.getHm().put("objectIDs", objectIDs);
       String dbpre=(String) this.getFormHM().get("dbpre");
       String actor_type=(String) this.getFormHM().get("actor_type");
       String relation_id=(String)this.getFormHM().get("relation_id");
       if("2".equals(flag)){
           String approvalRelation = (String) this.getFormHM().get("approvalRelation");
           this.getFormHM().put("approvalRelation", approvalRelation);
       }
       this.getFormHM().put("dbpre", dbpre);
       this.getFormHM().put("actor_type",actor_type);
       this.getFormHM().put("relation_id", relation_id);
       this.getFormHM().put("flag", flag);
    }

}
