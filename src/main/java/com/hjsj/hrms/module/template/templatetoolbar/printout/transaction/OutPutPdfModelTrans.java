package com.hjsj.hrms.module.template.templatetoolbar.printout.transaction;

import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutPutModelBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.regex.Pattern;

public class OutPutPdfModelTrans extends IBusiness {
	private String judgeisllexpr=null; 
	
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap formMap= this.getFormHM();
        TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
        String tabid = frontProperty.getTabId();
        String questionid=(String)this.getFormHM().get("questionid");
		String current_id=(String)this.getFormHM().get("current_id");
		String selfapply=(String)this.getFormHM().get("selfapply");
        if(tabid==null||tabid.length()<=0)
        	tabid="0";
        String task_id = frontProperty.getTaskId();
        String infor_type=frontProperty.getInforType();
        String filetype=(String)this.getFormHM().get("filetype");//liuyz 导出登记表 单人模版还是多人模版
		String ins_id=(String)this.getFormHM().get("ins_id");
		ins_id=ins_id==null?"0":ins_id;
		String isPrintWord=(String)this.getFormHM().get("isPrintWord");
		String id=(String)this.getFormHM().get("id");
		if(isPrintWord==null||isPrintWord.trim().length()==0)
		{
			isPrintWord="1";
		}
		//用正则表达式校验tabid、id、task_id，ins_id预防注入攻击。
		String regex="^\\d{0,}$";
		boolean isNumber = Pattern.matches(regex,tabid)&&Pattern.matches(regex,id);
		if(!isNumber)
		{
			throw new GeneralException("参数错误。");
		}
		regex="^[\\d,\\,]{0,}$$";
		isNumber = Pattern.matches(regex,task_id)&&Pattern.matches(regex,ins_id);
		if(!isNumber)
		{
			throw new GeneralException("参数错误。");
		}
		OutPutModelBo outPutBo=new OutPutModelBo(this.frameconn,this.userView);
		String fileName=outPutBo.fileZipName(ins_id, task_id, id, questionid, current_id, selfapply, infor_type, tabid,isPrintWord,filetype);
		/**syl bug 52704V76人事异动 按模板导出，表单中没有人员时应给出提示，不该直接导出空白文件*/
		if(StringUtils.isEmpty(fileName)){
			throw new GeneralException("请选择需要导出的人员！");
		}
		fileName=PubFunc.encrypt(fileName);
		this.getFormHM().put("filename",fileName);
	}
	
}
