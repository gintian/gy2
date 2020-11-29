/**   
* @Title: HeadhunterValidateReCommendTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs   
* @date 2015年2月6日 下午2:12:34 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/** 
 * @ClassName: HeadhunterValidateReCommendTrans 
 * @Description: 验证被选中的人员是否有人不能继续被推荐岗位(支持批量的验证和单个的验证)
 * @author xucs
 * @date 2015年2月6日 下午2:12:34 
 *  
 */
public class HeadhunterValidateReCommendTrans extends IBusiness {

	public void execute() throws GeneralException {
		String a0100s = (String) this.getFormHM().get("a0100s");//选中人员的a0100,已经加密
		String userNames =(String) this.getFormHM().get("userNames");//选中的人员姓名
		String a0100Array[] = a0100s.split(",");
		String userNameArray[] = userNames.split(",");
		String max_count = "";//最大申请职位数
		String infor ="";
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
		HashMap map=xmlBo.getAttributeValues();
		if(map.get("max_count")!=null)
			max_count = (String)map.get("max_count");
		if(max_count.trim().length()>0){
			EmployNetPortalBo bo = new EmployNetPortalBo(this.frameconn);//招聘外网使用的相关BO
			infor=bo.validationRecommend(a0100Array,userNameArray,max_count);
		}
		this.getFormHM().put("a0100s", a0100s);//单个人员的时候通过连接传递,多个人员时用selectList获得
		this.getFormHM().put("userNames", userNames);
		this.getFormHM().put("infor", infor);
		if(a0100Array.length>1){
			this.getFormHM().put("from", "more");//来自多人推荐
		}else{
			this.getFormHM().put("from", "one");//来自单人推荐
		}
		
	}

}
