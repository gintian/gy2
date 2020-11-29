/**   
* @Title: RecommendApplypositionTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs   
* @date 2015年2月7日 下午3:35:32 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** 
 * @ClassName: RecommendApplypositionTrans 
 * @Description: TODO(为推荐的人员增加应聘职位) 
 * @author xucs
 * @date 2015年2月7日 下午3:35:32 
 *  
 */
public class RecommendApplypositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		try{
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
			String reCommendoption = (String)hm.get("reCommendoption");//推荐类型 one:单人推荐 more:多人推荐
			String dbName = employNetPortalBo.getZpkdbName();//招聘人才库
			String recusername = this.userView.getUserName();
			ContentDAO dao = new ContentDAO(this.frameconn);
			//简历中心的bo类,通过这个类的方法得到新简历数目和所有简历数目
			PositionBo positionBo=new PositionBo(this.getFrameconn(),dao,this.userView);
			if("one".equals(reCommendoption)){
				String a0100 = (String) hm.get("a0100s");//当前被推荐人员的a0100
				a0100 = PubFunc.decrypt(a0100);
				String z0301 = (String) hm.get("z0301");//当前选择的职位编号
				StringBuffer queryBuffer = new StringBuffer();
				queryBuffer.append("select max(Thenumber) as num from zp_pos_tache where a0100=?");
				ArrayList values = new ArrayList();
				values.add(a0100);
				
				this.frowset=dao.search(queryBuffer.toString(), values);
				int thenumber =1;//第几志愿
				if(this.frowset.next()){
					int tempThenumber =  this.frowset.getInt("num");
					thenumber =tempThenumber+1;
				}
				
				Date date = new Date();
				Timestamp create_time = new Timestamp(date.getTime()); 
				queryBuffer.setLength(0);
				queryBuffer.append("insert into zp_pos_tache (A0100,Zp_pos_id,Thenumber,status,Nbase,recusername,recdate,apply_date,relation_type)");
				queryBuffer.append("  values(?,?,?,?,?,?,?,?,?)");
				ArrayList<Object> list = new ArrayList<Object>();
				list.add(a0100);
				list.add(z0301);
				list.add(thenumber);
				list.add("0");
				list.add(dbName);
				list.add(recusername);
				list.add(create_time);
				list.add(create_time);
				list.add("2");
				
				dao.update(queryBuffer.toString(),list);
				//开始更新z03里面的数据,将新简历数目和所有简历数目更新一下
				positionBo.saveCandiatesNumber(z0301, 1);//1更新新简历数目
				positionBo.saveCandiatesNumber(z0301, 3);//3更新所有简历数目
			}else{
				StringBuffer queryBuffer = new StringBuffer();
				queryBuffer.append("select a0100,max(Thenumber) as num from zp_pos_tache where a0100 in(");
				ArrayList values = new ArrayList();//存放A01000
				HashMap thenumMap = new HashMap();//每个人的志愿
				String z0301 = (String) hm.get("z0301");//当前选择的职位编号
				String a0100s = (String) this.getFormHM().get("recommendA0100s");
				String a0100Array[] =a0100s.split(",");
				for(int i=0;i<a0100Array.length;i++){
					String a0100 = a0100Array[i];
					a0100 = PubFunc.decrypt(a0100);
					thenumMap.put(a0100,new Integer("1"));
					if(i==a0100Array.length-1){
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
				employNetPortalBo.bacthInsetPosition(values,z0301,thenumMap,"0",dbName,recusername);
				//开始更新z03里面的数据,将新简历数目和所有简历数目更新一下
				positionBo.saveCandiatesNumber(z0301, 1);//1更新新简历数目
				positionBo.saveCandiatesNumber(z0301, 3);//3更新所有简历数目
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
