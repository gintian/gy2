package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 * <p>Title:AdjustTaskSeqTrans.java</p>
 * <p>Description:调整任务顺序</p> 
 * <p>Company:hjsj</p> 
 * create time at:2014-8-7 上午11:48:02 
 * @author dengcan
 * @version 6.x
 */
public class AdjustTaskSeqTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			
			String p0700=WorkPlanUtil.decryption(this.getFormHM().get("p0700")!=null?(String)this.getFormHM().get("p0700"):"");  //计划id
			String p0723=WorkPlanUtil.decryption(this.getFormHM().get("p0723")!=null?(String)this.getFormHM().get("p0723"):"");  //计划类型 1：人员计划  2：团队计划  3：项目 
			String object_id=WorkPlanUtil.decryption(this.getFormHM().get("object_id")!=null?(String)this.getFormHM().get("object_id"):""); //对象id
			String dropPosition=(String)this.getFormHM().get("dropPosition");
			String to_p0800=WorkPlanUtil.decryption(SafeCode.decode(this.getFormHM().get("to_p0800")!=null?(String)this.getFormHM().get("to_p0800"):"")); 
			String ori_p0800=WorkPlanUtil.decryption(SafeCode.decode(this.getFormHM().get("ori_p0800")!=null?(String)this.getFormHM().get("ori_p0800"):""));  
			
			PlanTaskTreeTableBo planBo=new PlanTaskTreeTableBo(this.getFrameconn(),Integer.parseInt(p0700));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql_str=new StringBuffer("select ptm.id,ptm.p0800,ptm.seq,p08.p0831  from p08,per_task_map ptm,p07 where ptm.p0700=p07.p0700 and ptm.p0800=p08.p0800  and p07.p0723 in (1,2) ");
			if(Integer.parseInt(p0723)==2)  //p0723-> 1:人员计划  2：团队计划  3:项目计划 
				sql_str.append(" and ptm.org_id='"+object_id+"'"); 
			else
				sql_str.append(" and ptm.nbase='"+object_id.substring(0,3)+"' and ptm.a0100='"+object_id.substring(3)+"' ");
			/* 同期计划 */
			sql_str.append(" and p07.p0725="+planBo.getP07_vo().getInt("p0725")+" and p07.p0727="+planBo.getP07_vo().getInt("p0727"));  
			if(planBo.getP07_vo().getInt("p0725")==2||planBo.getP07_vo().getInt("p0725")==3||planBo.getP07_vo().getInt("p0725")==4||planBo.getP07_vo().getInt("p0725")==5)
				sql_str.append(" and p07.p0729="+planBo.getP07_vo().getInt("p0729"));
			if(planBo.getP07_vo().getInt("p0725")==6)
				sql_str.append(" and p07.p0731="+planBo.getP07_vo().getInt("p0731"));  
			sql_str.append(" order by ptm.seq");
			this.frowset=dao.search(sql_str.toString());
			ArrayList list=new ArrayList();
			LazyDynaBean abean=null;
			int ori_id=0;
			int ori_p0831=0;
			int to_id=0;
			int to_p0831=0;
			while(this.frowset.next())
			{
				String p0800=this.frowset.getString("p0800");
				if(to_p0800.equalsIgnoreCase(p0800))
				{
					to_id=this.frowset.getInt("id");
					to_p0831=this.frowset.getInt("p0831");
				}
				if(ori_p0800.equalsIgnoreCase(p0800))
				{
					ori_id=this.frowset.getInt("id");
					ori_p0831=this.frowset.getInt("p0831");
				}
				String seq=this.frowset.getString("seq"); 
				abean=new LazyDynaBean();
				abean.set("id",this.frowset.getString("id"));
				abean.set("p0800",this.frowset.getString("p0800"));
				abean.set("p0831",this.frowset.getString("p0831"));
				abean.set("seq",this.frowset.getString("seq"));
				list.add(abean); 
			}
			
			int isUpdate=0;  //1 2
			for(int i=0;i<list.size();i++)
			{
				abean=(LazyDynaBean)list.get(i);
				String id=(String)abean.get("id");
				String p0800=(String)abean.get("p0800");
				String p0831=(String)abean.get("p0831");
				int seq=Integer.parseInt((String)abean.get("seq"));
				
				if(id.equalsIgnoreCase(String.valueOf(ori_id)))
					continue;
				
				if(id.equalsIgnoreCase(String.valueOf(to_id)))
				{
					if("before".equalsIgnoreCase(dropPosition))
					{
						isUpdate=2;
						dao.update("update per_task_map set seq="+seq+" where id="+ori_id);
						dao.update("update per_task_map set seq="+(seq+1)+" where id="+id);
					}
					else if("after".equalsIgnoreCase(dropPosition))
					{
						isUpdate=1;
						if(i==list.size()-1)
							dao.update("update per_task_map set seq="+(seq+1)+" where id="+ori_id);
						
					}
				}
				else if(isUpdate==1)
				{
					isUpdate=2;
					dao.update("update per_task_map set seq="+seq+" where id="+ori_id);
					dao.update("update per_task_map set seq="+(seq+1)+" where id="+id);
				}
				else if(isUpdate==2)
				{
					if(Integer.parseInt(ori_p0800)==ori_p0831&&p0800.equalsIgnoreCase(p0831))
						dao.update("update per_task_map set seq="+(seq+1)+" where id="+id);
					else if(Integer.parseInt(ori_p0800)!=ori_p0831&&p0831.equalsIgnoreCase(String.valueOf(ori_p0831)))
						dao.update("update per_task_map set seq="+(seq+1)+" where id="+id);
				}
				
			}
			
			
		} catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
	}

}
