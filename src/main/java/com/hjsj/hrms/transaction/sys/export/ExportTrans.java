package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.ExportXmlBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.HashMap;

public class ExportTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		//获取当前时间
		String start_time = Calendar.getInstance().get(Calendar.YEAR)+"-"
				+(Calendar.getInstance().get(Calendar.MONTH)+1)+"-"
				+Calendar.getInstance().get(Calendar.DATE);

		//定义要打包的文件名
		String file = "yksoft_"+start_time+".rar";
		String pathname = System.getProperty("java.io.tmpdir");
		
		ExportXmlBo exportxml = new ExportXmlBo(this.getFrameconn(),"SYS_EXPORT");
		
		exportxml.export("false",pathname,file);

		String path = pathname+System.getProperty("file.separator")+file;
		path=path.replace("\\","/");
		hm.remove("path");
		hm.put("path",path);
	}
}
