/**   
 * @Title: AddAdvanceTrans.java 
 * @Package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter 
 * @Description: TODO
 * @author xucs
 * @date 2014-7-30 下午04:15:53 
 * @version V1.0   
*/
package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/** 
 * @ClassName: AddAdvanceTrans 
 * @Description: TODO
 * @author xucs 
 * @date 2014-7-30 下午04:15:53 
 *  
 */
public class AddorDelAdvanceTrans extends IBusiness {
    public void execute() throws GeneralException {
        
        String flag=(String) this.getFormHM().get("flag");//确认是增加还是删除
        String hireobjcode=(String) this.getFormHM().get("hireobjcode");//招聘方式
        String mode=(String) this.getFormHM().get("mode");//测评阶段
        String testTemplate=(String) this.getFormHM().get("testTemplate");//测评模版名称
        String markType=(String) this.getFormHM().get("markType");//打分方式
        String itemid=(String) this.getFormHM().get("itemid");//对应指标
        ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
        if("del".equalsIgnoreCase(flag)){
            String sucess=parameterXMLBo.delAdvance(hireobjcode,mode,testTemplate,markType,itemid);//操作参数配置xml
            this.getFormHM().put("sucess", sucess);  
        }else{
            String sucess=parameterXMLBo.addAdvance(hireobjcode,mode,testTemplate,markType,itemid);//操作参数配置xml
            this.getFormHM().put("sucess", sucess);   
        }
    }

}
