/**   
* @Title: HeadhunterSearchPositionTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs   
* @date 2015年2月6日 下午3:57:00 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: HeadhunterSearchPositionTrans 
 * @Description: TODO查询出当前猎头可以推荐的
 * @author xucs
 * @date 2015年2月6日 下午3:57:00 
 *  
 */
public class HeadhunterSearchPositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String reCommendoption = (String) hm.get("reCommendoption");
		if("one".equals(reCommendoption)){
			String a0100 = (String) hm.get("a0100");
			String userName = (String)hm.get("userName");
			this.getFormHM().put("recommendA0100s", a0100);
			this.getFormHM().put("recommendUserNames", userName);
		}else{
			String a0100s = "";
			String userNames ="";
			ArrayList selectedList = (ArrayList) this.getFormHM().get("selectedlist");
			for(int i=0;i<selectedList.size();i++){
				LazyDynaBean bean = (LazyDynaBean) selectedList.get(i);
				String a0100 = (String) bean.get("a0100");
				String userName = (String) bean.get("A0101");
				a0100s=a0100s+a0100+",";
				userNames=userNames+userName+",";
			}
			
			if(selectedList == null || selectedList.size() < 1){
			    a0100s = (String) hm.get("a0100");
			    userNames = (String)hm.get("userName");
			}
			this.getFormHM().put("recommendA0100s", a0100s);
			this.getFormHM().put("recommendUserNames", userNames);
		}
		
		EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
		employNetPortalBo.setHireChannel("headHire");
		employNetPortalBo.setLoginUserName(this.userView.getUserName());
		ArrayList zpPosList=new ArrayList();
		ArrayList kunitList2=new ArrayList();//这里面存放的是所有的招聘单位的codeitemid
		
		//获取猎头所属机构编码
		String zpUnitCode = employNetPortalBo.getHeadhunterUnitCode(this.userView.getUserName());
		String unitLevel = "";
		
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
        HashMap map=xmlBo.getAttributeValues();
        if(map != null && map.get("unitLevel") != null)
            unitLevel = (String) map.get("unitLevel");
        
		/**unitPosMap2中存放着各个招聘职位的相关信息,对应关系单位的codeitemid-->list,一个单位对应一个list,list中的多个bean对应多个职位的招聘详情**/
		HashMap unitPosMap2=employNetPortalBo.getPositionInterviewMap2(kunitList2,"headHire", unitLevel);
		zpPosList=employNetPortalBo.getUnitList(unitPosMap2,kunitList2, unitLevel);
		zpPosList = employNetPortalBo.getShowPostList(zpUnitCode, zpPosList);
		this.getFormHM().put("zpPosList", zpPosList);
		ArrayList tempPosList = employNetPortalBo.getPosListField();
        if (tempPosList == null || tempPosList.size() < 1) {
            FieldItem item = DataDictionary.getFieldItem("z0351","z03");
            if (item != null && "1".equals(item.getUseflag())) {
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("itemid", item.getItemid().toLowerCase());
                bean.set("itemtype", item.getItemtype());
                bean.set("codesetid", item.getCodesetid());
                bean.set("deciwidth", item.getDecimalwidth() + "");
                bean.set("itemdesc", item.getItemdesc());
                tempPosList= new ArrayList();
                tempPosList.add(bean);
            }
        }
        
		if(tempPosList==null)
			throw GeneralExceptionHandler.Handle(new Exception("外网岗位列表显示指标没有设置!"));
		ArrayList posFieldList = new ArrayList();//用于存放外网招聘显示的岗位字段
		/**猎头招聘特别处理一下**/
		for(int i =0;i<tempPosList.size();i++){
			LazyDynaBean bean = (LazyDynaBean) tempPosList.get(i);
			LazyDynaBean newBean = new LazyDynaBean();
			String itemid = (String) bean.get("itemid");
			String itemdesc = (String) bean.get("itemdesc");
			if("yprsl".equals(itemid)){
				itemid = "tjrsl";
				itemdesc = "推荐人数";
			}
			if("ypljl".equals(itemid)){
				itemid = "tjjl";
				itemdesc = "推荐";
			}
			newBean.set("itemtype", bean.get("itemtype"));
			newBean.set("codesetid", bean.get("codesetid"));
			newBean.set("deciwidth", bean.get("deciwidth"));
			newBean.set("itemid", itemid);
			newBean.set("itemdesc", itemdesc);
			posFieldList.add(newBean);
		}
		this.getFormHM().put("posFieldList", posFieldList);
	}

}
