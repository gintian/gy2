package com.hjsj.hrms.transaction.performance.singleGrade;


import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SaveSummaryAffixBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.HashMap;
/**
 * 绩效模板功能交易类
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2014 16:05:00</p>
 * @author zangxj
 * @version 1.0
 *
 */
public class SaveSummaryAffixTrans extends IBusiness  {
	public void execute() throws GeneralException {

		try {
			String opt=(String)this.getFormHM().get("opt");
			if("".equals(opt)||opt==null){
				opt="null";
			}
			if("down".equals(opt)||"flag".equals(opt)){

					String plan_id=(String)this.getFormHM().get("plan_id");
					
					SaveSummaryAffixBo isnullaffix = new SaveSummaryAffixBo(this.userView,this.getFrameconn());
					 String outname = isnullaffix.summaryDown(plan_id,opt);
					 outname = PubFunc.encrypt(outname);
					 String isnull = isnullaffix.isnullArticle_name(plan_id);
						this.getFormHM().put("isnull", isnull);
						this.getFormHM().put("outname", outname);
			}		
			//删除模板文件
			else if ("del".equals(opt)){
				String isnull = "";
				String plan_id=(String)this.getFormHM().get("plan_id");
				SaveSummaryAffixBo isnullaffix = new SaveSummaryAffixBo(this.userView,this.getFrameconn());
				isnull = isnullaffix.isnullArticle_name(plan_id);
					isnullaffix.summaryDel(plan_id);

				this.getFormHM().put("isnull", isnull);
			}
			else{
				HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
				 opt=(String)hm.get("opt");
				 hm.remove("opt");
				String planId = (String) hm.get("plan_id");
				 SaveSummaryAffixBo filename_Articlename = new SaveSummaryAffixBo(this.userView,this.getFrameconn());

				 if(opt==null){
					this.getFormHM().put("filenametemplet", filename_Articlename.filename_Articlename(planId));
				 }
				 
				 //上传模板文件
				if (opt!=null&& "up".equals(opt)){
					FormFile form_file = (FormFile) getFormHM().get("file");
					String fname = form_file.getFileName();
					if( form_file == null || fname.length() <= 0 ){
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("goabroad.collect.no.select")));
					}
					
					int indexInt = fname.lastIndexOf(".");
					String ext = fname.substring(indexInt, fname.length());
	
					if(ext.length()>=10){
						throw GeneralExceptionHandler.Handle(new Exception("文件名后缀不能大于10"));
					}
					
					String _name = fname.substring(0, indexInt);
					String _ext = fname.substring(indexInt + 1, fname.length());
					_name = PerEvaluationBo.substrChinese(_name,50-_ext.length()-1);//去掉后缀长度 .xls .doc等等
					fname = _name+"."+_ext;
					this.getFormHM().put("filenametemplet",fname);
					SaveSummaryAffixBo isnullaffix = new SaveSummaryAffixBo(this.userView,this.getFrameconn());
					isnullaffix.summaryUp(planId,ext,form_file,fname);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
	}
}