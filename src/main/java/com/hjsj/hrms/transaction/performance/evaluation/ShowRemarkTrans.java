package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:展现 评语或总结</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 8, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ShowRemarkTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");   //  1：评语  2：总结
			String planid=(String)this.getFormHM().get("planid");
			
			String objectid = PubFunc.decrypt((String)hm.get("object_id"));//选中的当前考核对象			
			
//			String objectid=(String)this.getFormHM().get("objectid");
			//限制考核对象的范围
//			String code=(String)this.formHM.get("code");
//			String objStr=(String)this.getFormHM().get("objStr");
//			
//			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());	
//			String whl= pb.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
//			if(code!=null && !code.equals("-1"))
//			{
//				if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0)
//					whl=" and b0110 like '"+code+"%'";
//				else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0)
//					whl=" and e0122 like '"+code+"%'";
//				
//			}
//			if(objStr.length()>0)
//				whl+=" and object_id in ("+objStr+") ";
//			
//			
			PerEvaluationBo bo=new PerEvaluationBo(this.getFrameconn());
//			ArrayList objectList=bo.getObjectList2(planid,whl);
//			if(objectid==null||objectid.length()==0)
//			{
//				CommonData data=(CommonData)objectList.get(0);
//				objectid=data.getDataValue();
//			}
//			else
//			{
//				boolean isExist=false;
//				for(int i=0;i<objectList.size();i++)
//				{
//					CommonData data=(CommonData)objectList.get(i);
//					if(data.getDataValue().equalsIgnoreCase(objectid))
//					{
//						isExist=true;
//						break;
//					}
//				}
//				if(!isExist)
//				{
//					CommonData data=(CommonData)objectList.get(0);
//					objectid=data.getDataValue();
//				}
//			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String objName="";
			String sql = "select * from per_object where plan_id="+planid+" and object_id='"+objectid+"'";
			this.frowset=dao.search(sql);
			if(this.frowset.next())
				objName=this.frowset.getString("a0101");
			
			this.getFormHM().put("objName",objName);
			ArrayList summaryFileIdsList=new ArrayList();
			if("2".equals(opt))
			{
			
				String temp=objectid;
				  //验证是否为非人员的计划 如果是要用团队负责人来保存
	    			sql = "select object_type from per_plan where plan_id="+planid;
	    			this.frowset=dao.search(sql);
	        		if(	this.frowset.next())
	        		{
	        			if(	this.frowset.getInt(1)!=2)
	        			{
	        				
	        				sql = "select mainbody_id from per_mainbody where object_id='"+objectid+"' and plan_id="+planid+" and body_id=-1";
	        				this.frowset=dao.search(sql);
	                		if(	this.frowset.next())
	                			temp=	this.frowset.getString(1);
	        			}
	        		}
				
				StringBuffer strsql=new StringBuffer();
				strsql.append("select * from per_article  where plan_id="+planid+" and a0100='"+temp+"' " );
				strsql.append(" and lower(nbase)='usr' and fileflag=2 and article_type=2 order by fileflag");
				this.frowset=dao.search(strsql.toString());
				while(this.frowset.next())
				{
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("id", this.frowset.getString("Article_id"));
						abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
						summaryFileIdsList.add(abean);
				}
			}
			String summarize=bo.getSummarize(objectid,opt,planid);
			this.getFormHM().put("summaryFileIdsList",summaryFileIdsList);
			this.getFormHM().put("objectid",objectid);
//			this.getFormHM().put("objectList",objectList);
			this.getFormHM().put("summarize",summarize);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
