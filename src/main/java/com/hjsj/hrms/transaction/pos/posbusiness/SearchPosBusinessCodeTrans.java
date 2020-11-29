/*
 * Created on 2005-12-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchPosBusinessCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		//HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		//String param = (String) hm.get("param");// 区分是显示
												// 职务编码、职务级别设置、岗/职位编码或岗/职位级别设置
		//hm.remove("param");
		String param = (String)this.getFormHM().get("param");
		param = param != null && param.length() > 1 ? param : "PS_CODE";
		try {
			String codesetid ="";
			if("68".equals(param)){
				codesetid="68";
			}else{
				RecordVo constantuser_vo = ConstantParamter
						.getRealConstantVo(param);
				if (constantuser_vo == null) {
					String temp=temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
					if("PS_CODE".equals(param)){
						temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
					}else if("PS_LEVEL_CODE".equals(param)){
						temp=ResourceFactory.getProperty("pos.posbusiness.nosetposlevelcode");
					}else if("PS_C_CODE".equals(param)){
						temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
					}else if("PS_C_LEVEL_CODE".equals(param)){
						temp=ResourceFactory.getProperty("pos.posbusiness.nosetposclevelcode");
					}
					throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
	
				}
				codesetid = constantuser_vo.getString("str_value");
				if("".equals(codesetid)|| "#".equals(codesetid)){
					String temp=temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
					if("PS_CODE".equals(param)){
						temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
					}else if("PS_LEVEL_CODE".equals(param)){
						temp=ResourceFactory.getProperty("pos.posbusiness.nosetposlevelcode");
					}else if("PS_C_CODE".equals(param)){
						temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
					}else if("PS_C_LEVEL_CODE".equals(param)){
						temp=ResourceFactory.getProperty("pos.posbusiness.nosetposclevelcode");
					}
					throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
				}
			}
			this.getFormHM().put("codesetid", codesetid);
			String sql = "select codesetdesc,validateflag from codeset where codesetid='"
					+ codesetid + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			if (this.frowset.next()){
				this.getFormHM().put("codesetdesc",
						this.frowset.getString("codesetdesc"));
				this.getFormHM().put("validateflag",this.frowset.getString("validateflag"));
			}
			this.getFormHM().put("param", param);
			if(param.indexOf("LEVEL")!=-1||"68".equals(param)){//当属于级别设置时控制在jsp不显示职务代码列
				this.getFormHM().put("islevel", "yes");
			}else{
				this.getFormHM().put("islevel", "no");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
