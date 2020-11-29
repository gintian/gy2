package com.hjsj.hrms.transaction.train.ilearning.mobile;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.util.regex.Matcher;

/**
 * 更新课件点击次数
 * 
 * @author xuj 2015-5-12
 * 
 */
public class ChangeCourseHitsTrans extends IBusiness {

	public void execute() throws GeneralException {

		String r5000 = (String) this.getFormHM().get("r5000");
		String r5100 = (String) this.getFormHM().get("r5100");
		String filepath = (String)this.getFormHM().get("filepath");
		boolean flag  =true;
		try {
			flag  = this.checkfileexists(filepath);
			if(flag){
				String sql = "update r51 set r5119=("+Sql_switcher.isnull("r5119", "0")+"+1) where r5000="+r5000+" and r5100="+r5100;
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				dao.update(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("flag", String.valueOf(flag));
		}

	}
	
	private boolean checkfileexists(String filepath){
		boolean flag  = true;
		try {
			String url = SafeCode.decode(filepath);
			url = PubFunc.decrypt(SafeCode.decode(url));
			
			String filestep = System.getProperty("file.separator");
			url = url.replaceAll(Matcher.quoteReplacement("\\"), "/");
			int index  = url.lastIndexOf("/");
			String name = url.substring(index + 1, url.length());
			if(name==null||name.length()<1){
				flag = false;
				return flag;
			}
			url = url.replace("'", "/");
			File file = new File(url);
			if(file.exists()){
				
			}else{
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

}
