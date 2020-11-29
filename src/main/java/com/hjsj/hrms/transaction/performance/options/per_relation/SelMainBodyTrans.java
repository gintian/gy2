package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:DelKhMainBodyTrans.java
 * </p>
 * <p>
 * Description:考核实施/指定考核主体/条件（手工）选择考核主体
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-06-01 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SelMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String mainBodyType = (String) hm.get("code");
	String sel = (String) hm.get("sel");
	hm.remove("sel");
	String object = (String) this.getFormHM().get("khObject");
	PerRelationBo bo = new PerRelationBo(this.frameconn);
	String str_sql = "";
	if("1".equals(sel)){//手工选人
		str_sql = PubFunc.keyWord_reback((String) this.getFormHM().get("paramStr"));
	}else{
		str_sql = PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("paramStr")));//这个得到的结果已经包含了用户范围的控制
	}
	if ("all".equals(object)) // 将考核主体应用于所有考核对象
	{
	    ArrayList objectList = (ArrayList) this.getFormHM().get("khObjectList");
	    for (int i = 0; i < objectList.size(); i++)
	    {
		CommonData vo = (CommonData) objectList.get(i);
		object = vo.getDataValue();
		bo.selMainBody(str_sql,  mainBodyType, object);
	    }
	} else
	    bo.selMainBody(str_sql,  mainBodyType, object);

    }
}
