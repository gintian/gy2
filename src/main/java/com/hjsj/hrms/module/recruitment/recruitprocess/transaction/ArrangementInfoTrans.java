package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.ArrangementBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.EmailInfoBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ArrangementInfoTrans extends IBusiness  {

	@Override
    public void execute() throws GeneralException {
		try {			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			UserView user = this.userView;
			String nextNum = (String)hm.get("nextNum");
			String flag = "";
			String resume_flag = "";
			String resume_name = "";
		    flag = (String)hm.get("flag");
			String[] a0100s = ((String)hm.get("a0100s")).split(",");//所有选中候选人集合
			String[] nbases = ((String)hm.get("nbases")).split(",");//所有选中候选人人员库
			String z0381 = PubFunc.decrypt((String)hm.get("z0381"));//环节
			String link_id = (String)hm.get("link_id");//流程id
			String node_id = (String)hm.get("node_id");//环节
			resume_flag = (String)hm.get("resume_flag");
			resume_name = (String)hm.get("resume_name");
			String z0301 = PubFunc.decrypt((String)hm.get("z0301"));//职位编号
			String page = (String)hm.get("page");//返回页码
			int num = Integer.parseInt(nextNum);
			String a0100 = PubFunc.decrypt(a0100s[num]);
			String nbase = PubFunc.decrypt(nbases[num]);
			ArrangementBo arrangementbo = new ArrangementBo(this.frameconn, this.userView);
			LazyDynaBean resumeInfo = arrangementbo.getResumeInfo(a0100, nbase,z0301);
			EmailInfoBo emailInfobo = new EmailInfoBo(this.frameconn, this.userView);
			LazyDynaBean info = emailInfobo.getInfo(a0100,z0301,nbase);
			String userName = user.getUserFullName();//发件人名字
			String userPhone= user.getUserTelephone();//发件人电话
			String codeitemdesc = (String)info.get("UN");//单位名字
			String z0351 = (String)info.get("Z0351");//职位名称
			String custom_name = (String)info.get("custom_name");//到岗时间
			LazyDynaBean emailInfo = emailInfobo.getNotice(a0100, codeitemdesc, (String)resumeInfo.get("a0101"), z0351, userName, userPhone, (String)resumeInfo.get("c0102"), custom_name);
			//获取面试安排信息
			LazyDynaBean arrangementInfo = arrangementbo.getArrangementInfo(z0301, a0100, nbase, link_id);
			if((String)arrangementInfo.get("Z0501")!=null&&!"".equalsIgnoreCase((String)arrangementInfo.get("Z0501")))
			{				
				//获取面试官集合
				ArrayList interviewerInfoList = arrangementbo.getInterviewerInfo((String)arrangementInfo.get("Z0501"));
				this.getFormHM().put("interviewerInfoList", interviewerInfoList);
			}else{
				this.getFormHM().put("interviewerInfoList", null);				
			}
			num+=1;//当前候选人下标
			if(a0100s.length==num)
			{
				resumeInfo.set("nextNum", "wu");
			}else{
				resumeInfo.set("nextNum", num+"");
			}
			emailInfo.set("preA0100s", (String)resumeInfo.get("nbase")+(String)resumeInfo.get("a0100"));
			emailInfo.set("z0301s", z0301); 
			emailInfo.set("c0102s", (String)resumeInfo.get("c0102"));  
			
			resumeInfo.set("nbase", PubFunc.encrypt((String)resumeInfo.get("nbase")));
			resumeInfo.set("a0100", PubFunc.encrypt((String)resumeInfo.get("a0100")));
			resumeInfo.set("z0301", PubFunc.encrypt((String)resumeInfo.get("z0301")));
			resumeInfo.set("userName", userName);
			resumeInfo.set("z0325", (String)info.get("UM"));
			resumeInfo.set("link_id", link_id);
			resumeInfo.set("node_id", node_id);
			resumeInfo.set("page", page);
			resumeInfo.set("z0381", PubFunc.encrypt(z0381));
			resumeInfo.set("flag", flag);
			resumeInfo.set("resume_name", resume_name);
			resumeInfo.set("resume_flag", resume_flag);
			this.getFormHM().put("resumeInfo", resumeInfo);
			this.getFormHM().put("emailInfo", emailInfo);
			this.getFormHM().put("arrangementInfo", arrangementInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


