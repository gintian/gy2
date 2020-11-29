package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * 职称评审_上会材料_生成账号密码获得学科组
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 */
public class GetSubjectsInfoTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
    public void execute() throws GeneralException {
    	
    	String isSelectAll = (String) this.getFormHM().get("isSelectAll");//表格控件是否全选
    	ArrayList<MorphDynaBean> idlist = (ArrayList<MorphDynaBean>)this.getFormHM().get("idlist");//选中或反选的数据
    	try {
			GenerateAcPwBo generateAcPwBo = new GenerateAcPwBo(this.frameconn, this.userView);

			
			ArrayList<HashMap<String, String>> subjectsList = new ArrayList<HashMap<String, String>>();
			subjectsList = generateAcPwBo.getSubjects(isSelectAll, idlist);//获取学科组
			
			this.getFormHM().put("subjectslist", subjectsList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	
    }

   

}
