package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:SaveEmailTemplateTrans.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-10 11:51:48</p>
 * @author LiZhenWei
 * @version 4.0
 */

public class SaveEmailTemplateTrans extends IBusiness{

	public void execute() throws GeneralException 
	{
		try
		{
			String subject =(String)this.getFormHM().get("subject");
			String content =(String)this.getFormHM().get("content");
			String name=SafeCode.decode((String)this.getFormHM().get("name"));
			String address=(String)this.getFormHM().get("itemid");
			String nmodule=(String)this.getFormHM().get("nmodule");
			//String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			ArrayList  fieldList= (ArrayList)this.getFormHM().get("email_array");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			int id=0;
			int flag=0;
			String id_str=(String)this.getFormHM().get("id");
			if(id_str!=null&&!"".equals(id_str))
			{
				id=Integer.parseInt(id_str);
			}else
			{
				id=bo.getTemplateId();
				flag=1;
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("email_name");
			vo.setInt("id",id);
			vo.setString("subject",subject);
			vo.setString("content",PubFunc.keyWord_reback(SafeCode.decode(content==null?"":content)));
			vo.setString("address",address);
			vo.setString("name",name);
			vo.setInt("nmodule",Integer.parseInt(nmodule));
			vo.setInt("ninfoclass",1);
			if(flag==1)
			{
				dao.addValueObject(vo);
			}else
			{
				dao.updateValueObject(vo);
			}
			bo.deleteTemplateFields(String.valueOf(id));
			filterFiledList(vo.getString("content"),fieldList);
			bo.addEmailField(id,fieldList,flag);
			this.getFormHM().put("id",id+"");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	private void filterFiledList(String content,ArrayList fieldList){
		if(fieldList==null)
			return;
		for(int i=0;i<fieldList.size();i++)
		{
			String s=SafeCode.decode(((String)fieldList.get(i)==null|| "".equals((String)fieldList.get(i)))?"":(String)fieldList.get(i));
			if(s==null||s.trim().length()==0)
			{
				continue;
			}
			String[] arr=s.split("`");
			if(1==Integer.parseInt(arr[9])){
				if(content.indexOf(arr[2]+"#")==-1){
					fieldList.remove(i);
					i--;
				}
			}else{
				if(content.indexOf(arr[2]+"$")==-1){
					fieldList.remove(i);
					i--;
				}
			}
			
		}
	}

}
