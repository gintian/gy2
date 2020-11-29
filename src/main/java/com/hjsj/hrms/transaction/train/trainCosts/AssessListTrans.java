package com.hjsj.hrms.transaction.train.trainCosts;

import com.hjsj.hrms.businessobject.train.TrainEffectEvalBo;
import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class AssessListTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String table = (String) hm.get("table");
		table = table == null || table.length() < 1 ? "" : table;
		
		if(!TrainResourceBo.hasTrainResourcePrivByTab(table, this.userView))
		    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.function.nopriv"),"",""));
		
		String id = (String) hm.get("id");
		id = id == null || id.length() < 1 ? "" : id;
		id = PubFunc.decrypt(SafeCode.decode(id));
		
		hm.remove("table");
		hm.remove("id");
		    
		ArrayList list = new ArrayList();
		try {
			String sql = "select r3701,r3130,r3703,r3704 from r37,r31 where r3101=r3703 and r3702='"
					+ getCodeItem(table) + "' and r3705='" + id + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				LazyDynaBean ldb = new LazyDynaBean();
				ldb.set("r3701", this.frowset.getString("r3701"));
				ldb.set("r3130", this.frowset.getString("r3130"));
				String r3704 = this.frowset.getString("r3704");
				r3704 = r3704 == null ? "" : r3704;
				ldb.set("r3704", r3704);
				
				String classid = this.frowset.getString("r3703");
				classid = classid == null ? "" : classid;
				ldb.set("classid", SafeCode.encode(PubFunc.encrypt(classid)));
				
		        if ("r04".equalsIgnoreCase(table)&&(!"".equals(classid)))
		        {
		            TrainEffectEvalBo bo = new TrainEffectEvalBo(this.frameconn, classid);
		            
		            LazyDynaBean paramBean = bo.getBean("template", "teacher");
		            if (null != paramBean)
                    {
		                String template = (String)paramBean.get("text");
		                template = template == null ? "" : template;
		                ldb.set("template", template);
                    }
		            
		            paramBean = bo.getBean("questionnaire", "teacher");
                    if (null != paramBean)
                    {
                        String questionnaire = (String)paramBean.get("text");
                        questionnaire = questionnaire == null ? "" : questionnaire;
                        ldb.set("questionnaire", questionnaire);
                    }
		        }
				
				list.add(ldb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.getFormHM().put("assessList", list);
			this.getFormHM().put("recTab", table);
		}
	}

	private String getCodeItem(String table) {
		String code = "";
		if ("r01".equalsIgnoreCase(table))// 培训机构
			code = "02";
		else if ("r04".equalsIgnoreCase(table))// 培训教师
			code = "01";
		else if ("r07".equalsIgnoreCase(table))// 培训资料
			code = "03";
		else if ("r10".equalsIgnoreCase(table))// 培训场所
			code = "04";
		else if ("r13".equalsIgnoreCase(table))// 培训项目
			code = "05";
		return code;
	}	

}
