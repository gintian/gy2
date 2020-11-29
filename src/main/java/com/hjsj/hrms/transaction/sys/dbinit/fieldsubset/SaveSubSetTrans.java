package com.hjsj.hrms.transaction.sys.dbinit.fieldsubset;

import com.hjsj.hrms.businessobject.sys.fieldsubset.SubSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:保存子集信息</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SaveSubSetTrans extends IBusiness{

	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
		String name = (String)this.getFormHM().get("name");
		String qobj = (String)this.getFormHM().get("qobj");
		String code = (String)this.getFormHM().get("code");
		String multimedia_file_flag = (String)this.getFormHM().get("multimedia_file_flag");
		/*添加指标集解释（setexplain） guodd 2018-04-24 */
		String setexplain = (String)this.getFormHM().get("setexplain");
		String cDX = code.toUpperCase();
		String settype= (String)this.getFormHM().get("settype");
		
		
		SubSetBo subset = new SubSetBo(this.getFrameconn());
		int cdx = subset.initial(cDX);
		int initid = subset.initorder(cDX);
		subset.setmuster(name, qobj, cDX,cdx,initid,multimedia_file_flag,setexplain);
		this.getFormHM().put("isrefresh","save");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
