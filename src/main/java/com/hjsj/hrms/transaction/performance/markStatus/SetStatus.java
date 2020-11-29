package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SetStatus extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String  planID=(String)this.getFormHM().get("planID");
			String  objectID=(String)this.getFormHM().get("objectID");
			String  mainbodyID=(String)this.getFormHM().get("mainbodyID");
			String  reasons=(String)this.getFormHM().get("reasons");
			if(mainbodyID==null || mainbodyID.trim().length()<=0)
				mainbodyID=this.userView.getA0100();
			
			if(planID!=null && planID.trim().length()>0 && "~".equalsIgnoreCase(planID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
            { 
                String _temp = planID.substring(1); 
                planID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
            }
			if(objectID!=null && objectID.trim().length()>0 && "~".equalsIgnoreCase(objectID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	            String _temp = objectID.substring(1); 
	            objectID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
	        if(mainbodyID!=null && mainbodyID.trim().length()>0 && "~".equalsIgnoreCase(mainbodyID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	            String _temp = mainbodyID.substring(1); 
	            mainbodyID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
	        
			String  operater=(String)this.getFormHM().get("operater");
			String  type=(String)this.getFormHM().get("type");  // 1:建议和意见
			String  description="";
			if(this.getFormHM().get("description")!=null)
				description=SafeCode.decode(this.getFormHM().get("description").toString());
			String  isNoMark=this.getFormHM().get("isNoMark").toString();
			String  performanceType=(String)this.getFormHM().get("performanceType");
			if(type!=null&& "1".equals(type))
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.update("update per_mainbody set description='"+description+"' where plan_id="+planID+" and object_id='"+objectID+"' and mainbody_id='"+mainbodyID+"'");
			}
			else
			{
				if("123".equals(reasons)){
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					this.frowset = dao.search(" select description from per_mainbody where plan_id="+planID+" and object_id='"+objectID+"' and mainbody_id='"+mainbodyID+"' ");
					while(frowset.next()){
						description = (String) frowset.getString("description");
					}
				}
				if("4".equals(isNoMark))
				{
					try
					{
						if("123".equals(reasons)){
							if(description !=null && !"".equals(description)){
								ContentDAO dao=new ContentDAO(this.getFrameconn());
								dao.delete("delete from per_table_"+planID+" where mainbody_id='"+mainbodyID+"' and object_id='"+objectID+"' ",new ArrayList());
								dao.update(" update per_mainbody set whole_score=0 where plan_id="+planID+" and mainbody_id='"+mainbodyID+"' and object_id='"+objectID+"' ");
							}
						} else{
							ContentDAO dao=new ContentDAO(this.getFrameconn());
							dao.delete("delete from per_table_"+planID+" where mainbody_id='"+mainbodyID+"' and object_id='"+objectID+"' ",new ArrayList());
							dao.update(" update per_mainbody set whole_score=0 where plan_id="+planID+" and mainbody_id='"+mainbodyID+"' and object_id='"+objectID+"' ");
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				MarkStatusBo markStatusBo=new MarkStatusBo(this.getFrameconn(),this.userView);
				if("123".equals(reasons)){
					if(description !=null && !"".equals(description)){
						markStatusBo.saveOrUpdateDesc_status(planID,objectID,mainbodyID,description,isNoMark,operater);
					}
				} else{
					markStatusBo.saveOrUpdateDesc_status(planID,objectID,mainbodyID,description,isNoMark,operater);
				}
			}
			this.getFormHM().put("objectID",objectID);
			this.getFormHM().put("isNoMark",isNoMark);
			this.getFormHM().put("description",SafeCode.encode(description));  // zzk 2014/2/7 解决未结束字符串常量报错
			this.getFormHM().put("reasons",reasons);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
