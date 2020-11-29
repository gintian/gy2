package com.hjsj.hrms.transaction.sys.dbinit.fieldsubset;


import com.hjsj.hrms.businessobject.sys.fieldsubset.SubSetBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:保存修改集子属性</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 26, 2008:3:18:36 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class AmendSaveSubSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String code=(String)this.getFormHM().get("code");
			String qobj=(String)this.getFormHM().get("qobj");
			String name=(String)this.getFormHM().get("name");
			String multimedia_file_flag=(String)this.getFormHM().get("mff");
			String setexplain = (String)this.getFormHM().get("setexplain");
			name = SafeCode.decode(name); 
			String custom=(String)this.getFormHM().get("customdesc");
			custom = SafeCode.decode(custom); 
			setexplain = SafeCode.decode(setexplain);
	        String oldcustom = DataDictionary.getFieldSetVo(code).getCustomdesc();
			SubSetBo subset = new SubSetBo(this.getFrameconn());
			int initid = subset.initorder(code);
			subset.getamend(code,qobj,name,custom,multimedia_file_flag,setexplain);
			subset.initid(code, qobj, initid);
			SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.frameconn);
			//修改子集构库名称后更新 子集分类中的子集名称 2015-05-08 guodd
	 		if(!custom.equals(oldcustom)){
				infoxml.updateTag(code,oldcustom, custom);
			}
	 		//子集解释有换行符时，返回前端解析参数出问题。此参数是后台保存使用，前端没用，去除此参数
	 		this.formHM.remove("setexplain");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
