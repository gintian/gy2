package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveHighGrade extends IBusiness {

	public void execute() throws GeneralException {
		try
		{	
			String item=(String) this.getFormHM().get("classify_item");
			String name=(String) this.getFormHM().get("name");
			String scope=(String) this.getFormHM().get("scope");
			String values=(String) this.getFormHM().get("values");
			String faNeme=(String) this.getFormHM().get("faNeme");
			String str=faNeme.split("\\.")[0];
			String flag=(String) this.getFormHM().get("flag");
			ContentDAO dao = new ContentDAO(this.frameconn);
			GzAnalyseBo bo = new GzAnalyseBo(this.frameconn,this.userView);
			bo.initXML();
			if("save".equals(flag)){
				if(!"add".equals(faNeme)){
					faNeme=faNeme.split(":")[1];
				}
				int id=bo.getMaxId(this.userView.getUserName());
				bo.setAttributeValue("name", name, this.userView.getUserName(), faNeme, id);
				bo.setAttributeValue("scope", scope, this.userView.getUserName(), faNeme, id);
				bo.setAttributeValue("values", values, this.userView.getUserName(), faNeme, id);
				bo.setAttributeValue("item", item, this.userView.getUserName(), faNeme, id);
				if("add".equals(faNeme)){//新增返回的是当前新建的那个方案
					str=this.userView.getUserName()+":"+(id+1);
				}
			}else if("del".equals(flag)){
				faNeme=faNeme.split(":")[1];
				bo.delFangAn(this.userView.getUserName(), faNeme);
			}

			String xml=bo.saveStrValue();

			RecordVo vo = new RecordVo("reportdetail");
			vo.setInt("rsid", 9);
			vo.setInt("rsdtlid", 0);
			vo = dao.findByPrimaryKey(vo);
			vo.setString("ctrlparam", xml);
			dao.updateValueObject(vo);
			this.getFormHM().put("faNeme", str);
		}		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
