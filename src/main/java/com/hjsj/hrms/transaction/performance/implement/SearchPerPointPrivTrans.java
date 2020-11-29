package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
public class SearchPerPointPrivTrans extends IBusiness {

	

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String  plan_id=(String)this.getFormHM().get("dbpre");
		String  object_id=(String)hm.get("object_id");
		String  status=(String)this.getFormHM().get("status");
		
		ArrayList mainBodyList=new ArrayList();  //相应考核计划某对象的主体集合
		ArrayList perPointList=new ArrayList();  //相应考核计划的指标因子集合
		ArrayList perPointPrivList=new ArrayList();  //某考核计划对象相应主体指标权限值集合
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		String khobjname = bo.getKhObjName(object_id, plan_id);
		this.getFormHM().put("khobjname",khobjname);
		
		/*  get 相应考核计划的指标因子集合  */
		StringBuffer pp_sql=new StringBuffer("select e.point_id,e.pointname from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e");
		pp_sql.append(" where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id="+plan_id);
		
		
		try
		{
			this.frowset=dao.search(pp_sql.toString());
			while(this.frowset.next())
			{
				String[] temp=new String[2];
				temp[0]=this.frowset.getString(1);
				temp[1]=Sql_switcher.readMemo(this.frowset,"pointname");
				perPointList.add(temp);
			}
		

			
			/* get 相应考核计划某对象所有主体指标权限纪录  */
			String[] temp=new String[perPointList.size()+2];
			temp[0]=ResourceFactory.getProperty("lable.performance.perMainBodySort");   
			temp[1]=ResourceFactory.getProperty("lable.performance.evaluateMan");   //"评估人";
			for(int i=2;i<temp.length;i++)
			{
				String[] temp1=(String[])perPointList.get(i-2);
				temp[i]=temp1[1];
			}
			perPointPrivList.add(temp);
			
			
			StringBuffer sql=new StringBuffer("select a.*,c.name  from per_pointpriv_"+plan_id+" a,per_mainbody b,per_mainbodyset c");
			sql.append(" where  a.mainbody_id=b.mainbody_id and b.body_id=c.body_id  and b.plan_id="+plan_id+" and b.object_id='"+object_id+"'");
			sql.append(" and  a.object_id='"+object_id+"'");
			
			
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				String[] temp2=new String[perPointList.size()+2];
				temp2[0]=this.frowset.getString("name");
				temp2[1]=this.frowset.getString("bodyname");
				
				for(int i=0;i<perPointList.size();i++)
				{
					String[] perPoint=(String[])perPointList.get(i);
					String value=this.frowset.getString("C_"+perPoint[0]).trim();
					
					String enable="";
					if(!"3".equals(status)&&!"5".equals(status))
						enable=" disabled=\"false\" ";
					if("1".equals(value))
					{

						temp2[i+2]="<input type=\"checkbox\" onclick='setPointPriv(\""+this.frowset.getString("mainbody_id")+"\",\""+perPoint[0]+"\" ,\""+object_id+"\",this)' name=\""+this.frowset.getString("mainbody_id")+"\\C_"+perPoint[0]+"\"  value=\"1\"  "+enable+"    checked>";
					}
					else
					{
						temp2[i+2]="<input type=\"checkbox\" onclick='setPointPriv(\""+this.frowset.getString("mainbody_id")+"\",\""+perPoint[0]+"\" ,\""+object_id+"\",this)' name=\""+this.frowset.getString("mainbody_id")+"\\C_"+perPoint[0]+"\"  value=\"1\"  "+enable+" >";
					}
					
					
				}
				
				
				perPointPrivList.add(temp2);
				
				
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			
			this.getFormHM().put("perPointPrivList",perPointPrivList);
			this.getFormHM().put("objectID",object_id);
		}
		
		
		
		
		
		
		

	}

}
