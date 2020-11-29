/**   
* @Title: RecommendPositionApplypositionTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs   
* @date 2015年2月10日 下午3:46:05 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: RecommendPositionApplypositionTrans 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author xucs
 * @date 2015年2月10日 下午3:46:05 
 *  
 */
public class RecommendPositionApplypositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
		String dbName = employNetPortalBo.getZpkdbName();//招聘人才库
		String recusername = this.userView.getUserName();
		ContentDAO dao = new ContentDAO(this.frameconn);
		//简历中心的bo类,通过这个类的方法得到新简历数目和所有简历数目
		PositionBo positionBo=new PositionBo(this.getFrameconn(),dao,this.userView);
		try{
			StringBuffer queryBuffer = new StringBuffer();
			queryBuffer.append("select a0100,max(Thenumber) as num from zp_pos_tache where a0100 in(");
			ArrayList values = new ArrayList();//存放A01000
			HashMap thenumMap = new HashMap();//每个人的志愿
			String z0301 = (String) hm.get("z0301");//当前选择的职位编号
			ArrayList selectedList = (ArrayList) this.getFormHM().get("selectedlist");
			for(int i=0;i<selectedList.size();i++){
				LazyDynaBean bean = (LazyDynaBean) selectedList.get(i);
				String a0100 = (String) bean.get("a0100");
				a0100 = PubFunc.decrypt(a0100);
				thenumMap.put(a0100,new Integer("1"));
				if(i==selectedList.size()-1){
					queryBuffer.append("?)");
				}else{
					queryBuffer.append("?,");
				}
				values.add(a0100);
			}
			queryBuffer.append(" group by a0100");
			this.frowset=dao.search(queryBuffer.toString(), values);
			while(this.frowset.next()){
				int tempThenumber =  this.frowset.getInt("num");
				String a0100 = this.frowset.getString("a0100");
				thenumMap.put(a0100,new Integer(tempThenumber+1));
			}
			//向数据库中插入对应的数据
			employNetPortalBo.bacthInsetPosition(values,z0301,thenumMap,"0",dbName,recusername);
			//开始更新z03里面的数据,将新简历数目和所有简历数目更新一下
			positionBo.saveCandiatesNumber(z0301, 1);//1更新新简历数目
			positionBo.saveCandiatesNumber(z0301, 3);//3更新所有简历数目
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
