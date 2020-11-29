/*
 * Created on 2005-12-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.train.resource.course.pos;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SearchPosBusinessCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String param = (String) hm.get("param");// 区分是显示
												// 职务编码、职务级别设置、岗/职位编码或岗/职位级别设置
		//hm.remove("param");
		param = param != null && param.length() > 1 ? param : "PS_CODE";
		String backdate = (String)this.getFormHM().get("backdate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        backdate=backdate==null||backdate.length()==0?date:backdate;
        this.getFormHM().put("backdate", backdate);
		try {
			RecordVo constantuser_vo = ConstantParamter
					.getRealConstantVo(param);
			if (constantuser_vo == null) {
				String temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
				if("PS_CODE".equals(param)){
					temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
				}else if("PS_C_CODE".equals(param)){
					temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
				}
				throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));

			}
			String codesetid = constantuser_vo.getString("str_value");
			if("".equals(codesetid)|| "#".equals(codesetid)){
				String temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
				if("PS_CODE".equals(param)){
					temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
				}else if("PS_C_CODE".equals(param)){
					temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
				}
				throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
			}
			this.getFormHM().put("codesetid", codesetid);
			
			String sql = "select codesetdesc,validateflag from codeset where codesetid='"
					+ codesetid + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			if (this.frowset.next()){
				this.getFormHM().put("codesetdesc",
						this.frowset.getString("codesetdesc"));
				this.getFormHM().put("validateflag",
						this.frowset.getString("validateflag"));
			}
			String state="1";
			if("PS_CODE".equals(param)){
				state="2";
			}
			this.getFormHM().put("state", state);
			
			ArrayList itemlist = new ArrayList();
			String columns="job_id,r5000,r5003,r5012,r5009,r5007";
			//ArrayList list=new ArrayList();
			//list = DataDictionary.getFieldList("r50",
			//		Constant.USED_FIELD_SET);

			FieldItem item = new FieldItem();
			item.setFieldsetid("r50");
			item.setItemid("job_id");
			if("1".equals(state)){
				item.setItemdesc("岗位");
			}else{
				item.setItemdesc("职务");
			}
			item.setItemtype("A");
			item.setCodesetid(codesetid);
			item.setAlign("center");
			item.setReadonly(true);
			itemlist.add(item);
//			item = DataDictionary.getFieldItem("r5000");
//			item.setVisible(false);
//			itemlist.add(item);
			itemlist.add(DataDictionary.getFieldItem("r5003"));
			itemlist.add(DataDictionary.getFieldItem("r5012"));
			itemlist.add(DataDictionary.getFieldItem("r5009"));
			itemlist.add(DataDictionary.getFieldItem("r5007"));
			this.getFormHM().put("columns", columns);
			this.getFormHM().put("itemlist", itemlist);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
