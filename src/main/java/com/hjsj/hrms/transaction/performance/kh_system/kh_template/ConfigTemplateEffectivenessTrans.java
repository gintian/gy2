package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:ConfigTemplateEffectivenessTrans.java</p>
 * <p>Description>:ConfigTemplateEffectivenessTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-14 下午04:24:56</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ConfigTemplateEffectivenessTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String validflag=(String)this.getFormHM().get("type");
			String id=(String)this.getFormHM().get("id");
			String name=SafeCode.decode((String)this.getFormHM().get("name"));
			//=0是模板分类 =1是模板
			String ss=(String)this.getFormHM().get("ss");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			bo.configTemplateEffectiveness(id, validflag, dao,ss);
			this.getFormHM().put("type",validflag);
			this.getFormHM().put("id",id);
			this.getFormHM().put("name",SafeCode.encode(name));
			this.getFormHM().put("ss",ss);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
