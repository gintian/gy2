package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.EvaluationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 转发简历
 * @author wangjl
 *
 */
public class ForwardResumeTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		RowSet rs = null;
		try {
			String nModule = (String)this.getFormHM().get("nModule");			//模块编号
			String sub_module = (String)this.getFormHM().get("sub_module");		//模板编号
			String a0100s = (String)this.getFormHM().get("a0100");
			String nbases = (String)this.getFormHM().get("nbase");
			String z0301 = (String)this.getFormHM().get("z0301");
			String flag = "&flag=1";
			EvaluationBo bo = new EvaluationBo(this.frameconn, this.userView);
			bo.getInfoList(nbases,a0100s);
			bo.setFlag(flag);
			//获取模板名集合
			String templateList = bo.getTemplateList(nModule, sub_module);
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select id from email_name where nModule=? and Sub_module=?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(nModule);
			list.add(sub_module);
			rs = dao.search(sql,list);
			String id = "";
			if(rs.next()){
				id = rs.getString("id");
			}
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
	        this.getFormHM().put("title", "转发简历" );
	        this.getFormHM().put("method", "");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
	}

}
