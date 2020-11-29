package com.hjsj.hrms.transaction.sys.dbinit.fieldsubset;

import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * <p>Title:修改</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class AmendSubSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{	
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String fieldsetid=(String)hm.get("id");  //子集代号
			String changeflag=(String)hm.get("flag");
			String useflag = (String)hm.get("useflag");
			String fieldsetdesc = (String)hm.get("fieldsetdesc"); 
			fieldsetdesc = SafeCode.decode(fieldsetdesc);   //这里是转换成中文的；
			String customdesc = (String)hm.get("customdesc");
			customdesc = SafeCode.decode(customdesc);
			String multimedia_file_flag = (String)hm.get("multimedia_file_flag");
			/*添加指标集解释（setexplain） guodd 2018-04-24 */
			FieldSet fi = DataDictionary.getFieldSetVo(fieldsetid);
			/*45660 通过cs程序构建的子集不在业务字典中，此处添加非null的判断，防止出错  guodd 2019-05-13*/
			String setexplain = "";
			if(fi!=null)
				setexplain = fi.getExplain();
			this.getFormHM().put("code", fieldsetid); //代号
			this.getFormHM().put("qobj", changeflag); //集变化
			this.getFormHM().put("name", fieldsetdesc); //构库前名字
			this.getFormHM().put("customdesc", customdesc); //后名字
			this.getFormHM().put("multimedia_file_flag",multimedia_file_flag);//支持附件		
			this.getFormHM().put("setexplain",setexplain);//子集解释
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
