package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamStudentBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * <p>Title:添加参试人员到计划</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time: 2011.11.26
 * @author zxj
 * @version 1.0
 */
public class AddExamStudentToPlanTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String flag = "ok";
		ArrayList objlist = (ArrayList)this.getFormHM().get("objlist");		
		String sql = (String)this.getFormHM().get("sql");
		String planId = (String)this.getFormHM().get("planid");
		planId =PubFunc.decrypt(SafeCode.decode(planId));
		
		if(sql!=null&&sql.length()>0)
			sql=SafeCode.decode(sql);
		sql = PubFunc.keyWord_reback(sql);
		try
		{
			TrainExamStudentBo studentBo = new TrainExamStudentBo(this.getFrameconn());
			
			HashMap hm = new HashMap();
			ArrayList a0100list = new ArrayList();
			
			for(int i=0;i<objlist.size();i++)
			{
				String obj_id=(String)objlist.get(i);
				if(obj_id==null|| "".equals(obj_id))
					continue;
				if("selectall".equalsIgnoreCase(obj_id))
					continue;
				
				String pre=obj_id.substring(0,2).toLowerCase();
				/**对人员信息群时，过滤单位、部门及职位*/
				if("UN".equalsIgnoreCase(pre)|| "UM".equalsIgnoreCase(pre)|| "@K".equalsIgnoreCase(pre))
					continue;
				
				pre=obj_id.substring(0,3).toLowerCase();
				/**按人员库进行分类*/
				if(!hm.containsKey(pre))
				{
					a0100list=new ArrayList();
				}
				else
				{
					a0100list=(ArrayList)hm.get(pre);
				}
				a0100list.add(obj_id.substring(3));
				if(i==0)
				{
//						first_base=pre;
//						a0100=obj_id.substring(3);
				}
				hm.put(pre,a0100list);
			}
				
		
			
//			}
			Iterator iterator=hm.entrySet().iterator();
			ArrayList tempList=null;
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				String pre=entry.getKey().toString();
				a0100list =(ArrayList)entry.getValue();
				if(a0100list.size()==0)
					continue;
				
				if(a0100list.size()<=500)
					studentBo.addStudentToPlan(planId, a0100list,pre);
				else
				{
					
					int size=a0100list.size();
					int n=size/500+1;
					for(int i=0;i<n;i++)
					{
						tempList=new ArrayList();
						for(int j=i*500;j<(i+1)*500;j++)
						{
							if(j<a0100list.size())
								tempList.add((String)a0100list.get(j));
							else
								break;
						}
						if(tempList.size()>0)
							studentBo.addStudentToPlan(planId, tempList,pre);
						
					}
					
				}
			}
			
		}
		catch(Exception ex)
		{
			flag = "error";
			ex.printStackTrace();
			//throw GeneralExceptionHandler.Handle(ex);
		}
		
		this.getFormHM().put("flag", flag);
	}
	
}
