package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeEvaluationBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class EvaluationResumeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		try {
			
			String a0100_o = (String)hm.get("a0100_o");   //简历id
			a0100_o = PubFunc.decrypt(a0100_o);
			String nbase_o = (String)hm.get("nbase_o");     //人员库前缀
			nbase_o = PubFunc.decrypt(nbase_o);
			String z0301 = (String)hm.get("z0301");   //岗位id
			z0301 = PubFunc.decrypt(z0301);
			String a0100 = this.userView.getA0100();   //评价人人员id
			String nbase = this.userView.getDbname();     //评价人人员库前缀
			String flag = (String)hm.get("flag");   // flag=1 不允许评价
			hm.remove("flag");
			/*
			String etoken = (String)hm.get("etoken");     //单点
			
			etoken = PubFunc.convert64BaseToString(etoken);
			System.out.println(etoken);
			System.out.println(PubFunc.convert64BaseToString(hm.get("etoken").toString()));*/
			ResumeBo resumeBo = new ResumeBo(this.frameconn,a0100_o,nbase_o);
			HashMap headMap = resumeBo.getResumeHead(z0301,this.userView,true);   //根据简历id号、岗位号查询姓名、简历投递及状态信息
			this.getFormHM().put("resumeid", a0100_o);
			this.getFormHM().put("nbase", nbase_o);
			this.getFormHM().put("zp_pos_id", z0301);
			this.getFormHM().put("username", headMap.get("username"));
			this.getFormHM().put("email", headMap.get("email"));
			this.getFormHM().put("recdate", headMap.get("create_time"));
			this.getFormHM().put("status", headMap.get("status"));
			this.getFormHM().put("lastPos", headMap.get("lastPos"));
			this.getFormHM().put("othPos", headMap.get("othPos"));
			
			ArrayList subModuleInfo = resumeBo.getSubModuleInfo(z0301);     //获取简历中各个模块的信息集合 
			this.getFormHM().put("fieldSetList", subModuleInfo.get(0));
			this.getFormHM().put("resumeBrowseSetMap", subModuleInfo.get(1));
            this.getFormHM().put("setShowFieldMap", subModuleInfo.get(2));
            /*
             * 获取上传的文件
             */
            ResumeFileBo resumeFileBo = new ResumeFileBo(this.getFrameconn(), this.userView);
            ArrayList files = resumeFileBo.getFiles(nbase_o, a0100_o, "0");
            ArrayList resume = resumeFileBo.getFiles(nbase_o, a0100_o, "1");//简历
            ArrayList allFiles = new ArrayList();//所有上传的文件
            allFiles.addAll(resume);
//            allFiles.addAll(files);
            this.getFormHM().put("uploadFileList", allFiles);
            
            /***
			 * 简历评价
			 */
				ResumeEvaluationBo evaBo = new ResumeEvaluationBo(this.frameconn, this.userView);
				ArrayList evaluationList = evaBo.getEvaluationList(nbase_o,a0100_o, nbase, a0100,1);//所有评价
				ArrayList list = evaBo.getEvaluationList(nbase_o,a0100_o,  nbase, a0100,0);//我的评价
				LazyDynaBean evaluationBean = new LazyDynaBean();
				if(list.size()>0)
				{
					evaluationBean = (LazyDynaBean)list.get(0);
				}else{
					evaluationBean.set("score", "0");
					evaluationBean.set("content", "");
				}
				this.getFormHM().put("evaluationBean", evaluationBean);
				this.getFormHM().put("evaluation", evaluationList);
            this.getFormHM().put("flag", flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
