package com.hjsj.hrms.transaction.workplan.plan_track;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.io.FileOutputStream;

/**
 * <p>Title:导出总分或权重超限的对象</p>
 * <p>Description:导出总分或权重超限的对象(人员或部门)名单(funcId: 9028000741)</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2015-4-20:下午14:56:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class ExportRankOrScoreOverflowingTrans extends IBusiness {
	
	private static final long serialVersionUID = 3586556280688053951L;

	public void execute() throws GeneralException {
		FileOutputStream out = null;
		try {
			String content = (String) formHM.get("content");
			if (content == null || "".equals(content.trim())) {return;}
			
			File file = file();
			out = new FileOutputStream(file);
			byte[] b = content.getBytes("gbk");
			out.write(b, 0, b.length);
			
			formHM.put("fileName", PubFunc.encrypt(file.getName()));
			formHM.remove("content"); // 内容包含换行符\n,command.js调用eval函数时报错，因此需删掉
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeIoResource(out);
		}
	}
	
	private File file() {
		String status = (String) formHM.get("status"); // 查询计划模板的类型(per_template.status): '0' = 分值, '1' = 权重
		String object_type = (String) formHM.get("object_type"); // per_plan.object_type(1=团队, 2=人员)
		
		String tplType = status == "0" ? "标准分值" : "权重";
		String objType = object_type == "1" ? "部门" : "人员";
		
		String fileName = tplType + "不符合规定的" + objType + ".txt";
		
		String tmpdir = System.getProperty("java.io.tmpdir");
		String sep = System.getProperty("file.separator");
		return new File(tmpdir + sep + fileName);
	}
	
}
