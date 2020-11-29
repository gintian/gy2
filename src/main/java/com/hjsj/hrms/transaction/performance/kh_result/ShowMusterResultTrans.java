package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowMusterResultTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String tabid = (String)map.get("tabid");
			if(map.get("tabid")!=null)
				map.remove("tabid");
			String plan_id = (String)this.getFormHM().get("planid");
			String object_id = (String)this.getFormHM().get("object_id");
			
			// 如果plan_id或object_id被加密，需要将其解密 modify by 刘蒙
			String isEncrypted = (String)map.get("isEncrypted");
			map.remove("isEncrypted");
			if (plan_id != null && !plan_id.matches("^\\d+$")) {
				plan_id = PubFunc.decrypt(plan_id);
			}
			//liuy 2015-1-30 7141：自助服务/绩效考评/考评反馈/本人考核结果：按照登记表方式查看，报错 start
			//if ("true".equals(isEncrypted)) {
				object_id = PubFunc.decryption(object_id);
			//}
			//liuy 2015-1-30 end
			
			if(object_id!=null && object_id.trim().length()>0 && "~".equalsIgnoreCase(object_id.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = object_id.substring(1); 
	        	object_id = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
			String model=(String)this.getFormHM().get("model");
			ArrayList list = new ArrayList(); 
			String isLT="0";
			String configButton="0";
			String isCloseButton="0";
			if(map.get("isClose")!=null/*&&model.equals("0")*/)
			{
				isCloseButton=(String)map.get("isClose");
				map.remove("isClose");
			}
			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
				ResultBo bo = new ResultBo(this.getFrameconn());
				String opt=(String)map.get("opt");
				if(opt!=null)
				{
					plan_id=(String)map.get("planid");
					object_id=(String)map.get("object_id");
					
					if(object_id!=null && object_id.trim().length()>0 && "~".equalsIgnoreCase(object_id.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
			        { 
			        	String _temp = object_id.substring(1); 
			        	object_id = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
			        }
					model=(String)map.get("model");
					map.remove("opt");
				}
				if(opt!=null&& "yuangong".equalsIgnoreCase(opt))
				{
					object_id=(String)map.get("object_id");
					
					if(object_id!=null && object_id.trim().length()>0 && "~".equalsIgnoreCase(object_id.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
			        { 
			        	String _temp = object_id.substring(1); 
			        	object_id = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
			        }
					map.remove("opt");
					model=(String)map.get("model");
					plan_id=bo.getMusterList(object_id);
				}
		    	list = bo.getRnameListFromPlanID(plan_id);
		    	if(list==null||list.size()==0)
		    		throw GeneralExceptionHandler.Handle(new Exception("考核计划没指定登记表！"));
		    	if(tabid==null&&list.size()>0)
		    	{
		    		CommonData cd=(CommonData)list.get(0);
		    		tabid=cd.getDataValue();
		    	}
				isLT="1";
				boolean confirm=true;
				if("0".equals(model))
					confirm=bo.isConfirm(plan_id, object_id);
				if(!confirm&& "0".equals(model))
					configButton="1";
			}
			
			String object_name="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from per_object where plan_id="+plan_id+" and object_id='"+object_id+"'");
			if(this.frowset.next())
				object_name=this.frowset.getString("a0101");
			
			this.getFormHM().put("isCloseButton", isCloseButton);
			this.getFormHM().put("selectTabId", tabid);
			this.getFormHM().put("selectTabIdEnc", PubFunc.encrypt(tabid));
			this.getFormHM().put("tableList", list);
			//liuy 2015-1-30 7141：自助服务/绩效考评/考评反馈/本人考核结果：按照登记表方式查看，报错 start
			this.getFormHM().put("planid",PubFunc.encrypt(plan_id));
			this.getFormHM().put("object_id",PubFunc.encrypt(object_id));
			//liuy 2015-1-30 end
			this.getFormHM().put("object_name",object_name);
			if(tabid!=null && tabid.trim().length()>0)
				this.getFormHM().put("tabid",tabid.substring(1));
			else
				this.getFormHM().put("tabid","");
			this.getFormHM().put("isLT", isLT);
			this.getFormHM().put("configButton", configButton);
			this.getFormHM().put("model", model);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
