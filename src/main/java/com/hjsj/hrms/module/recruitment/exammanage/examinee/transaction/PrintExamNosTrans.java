package com.hjsj.hrms.module.recruitment.exammanage.examinee.transaction;

import com.hjsj.hrms.businessobject.ykcard.YkcardPdf;
import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import java.io.File;


/**
 * <p>
 * Description:批量打印准考证号
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-11-3 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class PrintExamNosTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	try{
    		ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
    		String cardid = bo.getExamCardId();	
    		if(StringUtils.isEmpty(cardid) || "#".equals(cardid))
				throw new Exception("未设置准考证模板!");
    					
			String a0100s = (String)this.getFormHM().get("a0100s");   
			String nbases = (String)this.getFormHM().get("nbases");
			String z6301s = (String)this.getFormHM().get("z6301s");
			String a0101s = (String)this.getFormHM().get("a0101s");
			String userpriv=(String)this.getFormHM().get("userpriv");
			String fieldpurv=(String)this.getFormHM().get("fieldpurv");
			String istype=(String)this.getFormHM().get("istype");
			String querytype=(String)this.getFormHM().get("queryType");
			String infokind=(String)this.getFormHM().get("infokind");

			String platform = (String)this.getFormHM().get("platform");
			platform = platform == null ? "" : platform;
			
			String[] a0100 = a0100s.split(",");
			String[] nbase = nbases.split(",");
			String[] a0101 = a0101s.split(",");
			String[] z6301 = z6301s.split(",");
			String fileName = "";
			//服务器文件路径
			String servicePath = System.getProperty("java.io.tmpdir")+System.getProperty("file.separator");
			File file = null;
			File destFile = null;
			String[] fileNames = new String[a0100.length];
			
			YkcardPdf ykcardPdf = new YkcardPdf(this.getFrameconn());
			for (int i = 0; i < a0100.length; i++) {
				//文件名
				fileName = ykcardPdf.executePdf(Integer.parseInt(cardid),PubFunc.decrypt(a0100[i]),PubFunc.decrypt(nbase[i]),this.userView,null,querytype,null,userpriv,istype,null,null,null,null,infokind,fieldpurv,platform);
				//重命名文件
				file = new File(servicePath+fileName);
				
				destFile = new File(servicePath+a0101[i]+"__"+z6301[i]+".pdf");
				file.renameTo(destFile);
				
				fileNames[i] = destFile.getPath();
				//删除无用临时文件destFile会在压入压缩包后删除
				file.delete();
			}
			bo.inputFilesToZip(fileNames, this.userView.getUserName()+"_zp.zip");
			
			this.getFormHM().put("url",PubFunc.encrypt(this.userView.getUserName()+"_zp.zip"));
    	}catch(Exception e){
    		this.getFormHM().put("message","未设置准考证模板!");
			throw GeneralExceptionHandler.Handle(e);
    	}
    }

}
