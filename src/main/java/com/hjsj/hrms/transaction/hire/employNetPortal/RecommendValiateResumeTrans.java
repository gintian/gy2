/**   
* @Title: RecommendValiateResumeTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs   
* @date 2015年2月10日 下午1:48:09 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: RecommendValiateResumeTrans 
 * @Description: 为岗位推荐人员时,进行验证 
 * @author xucs
 * @date 2015年2月10日 下午1:48:09 
 *  
 */
public class RecommendValiateResumeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
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
			//判断是否有人应聘了当前职位
			if(infor.trim().length()==0){
				StringBuffer queryBuffer = new StringBuffer();
				ArrayList values = new ArrayList();
				HashMap userNameMap = new HashMap();
				String z0301 = (String) this.getFormHM().get("z0301");
				String posName = (String) this.getFormHM().get("posName");
				posName = SafeCode.decode(posName);
				queryBuffer.append("select * from zp_pos_tache where a0100 in(");
				for(int i=0;i<a0100Array.length;i++){
					String a0100 = a0100Array[i];
					a0100 = PubFunc.decrypt(a0100);
					if(i==a0100Array.length-1){
						queryBuffer.append("?");
					}else{
						queryBuffer.append("?,");
					}
					values.add(a0100);
					String userName =userNameArray[i];
					userNameMap.put(a0100, userName);
				}
				queryBuffer.append(") and Zp_pos_id=?");
				values.add(z0301);
				ContentDAO dao = new ContentDAO(this.frameconn);
				this.frowset=dao.search(queryBuffer.toString(), values);
				while(this.frowset.next()){
					String a0100 = this.frowset.getString("a0100");
					String userName = (String) userNameMap.get(a0100);
					infor=infor+userName+",";
				}
				if(infor.trim().length()>1){
					infor = infor.substring(0, infor.length()-1);
					infor = infor+ResourceFactory.getProperty("hire.out.zp_person.alreadyapply")+posName+ResourceFactory.getProperty("hire.out.can.not.continue.recommend");
				}
			}
			this.getFormHM().put("infor", infor);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
