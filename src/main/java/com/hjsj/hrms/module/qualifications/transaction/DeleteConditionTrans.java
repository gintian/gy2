package com.hjsj.hrms.module.qualifications.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;

public class DeleteConditionTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String conditionid = null;
		if(!StringUtils.isEmpty((String)this.getFormHM().get("conditionid")))
			conditionid = (String)this.getFormHM().get("conditionid");
		ArrayList list = new ArrayList();
		try {
			String sql = "delete from zc_condition where condition_id = ?";
			ContentDAO dao = new ContentDAO(this.frameconn);
			list.add(PubFunc.decrypt(conditionid));
			int flag = dao.delete(sql, list);
			if(flag>0){
				ConstantXml constantXml = new ConstantXml(this.frameconn, "FILEPATH_PARAM");
				String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
				if(StringUtils.isBlank(rootDir)) 
					return;
				rootDir = rootDir.replace("\\", File.separator)+File.separator;
				rootDir = rootDir + "multimedia" + File.separator+"jobtitle" + File.separator+"qualifications" + File.separator+PubFunc.decrypt(conditionid);
				File dir = new File(rootDir);
				if(dir.exists()){
					File[] files = dir.listFiles();
					if(files!=null){
						for(int i=0;i<files.length;i++){
							if(files[i].exists())
								files[i].delete();
						}
					}
					dir.delete();
				}
			}
		}catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
			
	}

}
