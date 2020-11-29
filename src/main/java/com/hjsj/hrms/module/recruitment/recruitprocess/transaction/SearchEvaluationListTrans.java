package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.EvaluationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

/***
 * 查询被评价人员信息列表并生成页面html代码
 * <p>Title: SearchEvaluationListTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-8-5 下午02:06:24</p>
 * @author xiexd
 * @version 1.0
 */
public class SearchEvaluationListTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			String nModule = (String)this.getFormHM().get("nModule");
			String sub_module = (String)this.getFormHM().get("sub_module");
			String a0100s = (String)this.getFormHM().get("a0100");
			String nbases = (String)this.getFormHM().get("nbase");
			String z0301 = (String)this.getFormHM().get("z0301");
			String node_id = (String)this.getFormHM().get("node_id");
			String id = (String)this.getFormHM().get("id"); 
			String method = this.getFormHM().get("method")==null?"":this.getFormHM().get("method").toString(); 
			
			EvaluationBo bo = new EvaluationBo(this.frameconn, this.userView);
			bo.getInfoList(nbases,a0100s);
			//获取模板名集合
			String templateList = bo.getTemplateList(nModule, sub_module);
			//获取邮件内容
			LazyDynaBean infoBean = bo.getTemplateInfo(nModule, sub_module, id, z0301);
			this.getFormHM().put("templateList", templateList);
	        this.getFormHM().put("infoBean", infoBean );
	        this.getFormHM().put("subject", infoBean.get("subject") );
	        this.getFormHM().put("content", infoBean.get("content") );
	        this.getFormHM().put("sub_module", sub_module );
	        this.getFormHM().put("nModule", nModule );
	        this.getFormHM().put("z0301", z0301 );
	        this.getFormHM().put("a0100s", a0100s );
	        this.getFormHM().put("nbases", nbases );
	        this.getFormHM().put("method", method );
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
