package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:DelKhMainBodyTrans.java</p>
 * <p>Description:考核实施/指定考核主体/选人</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SelMainBodyTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String mainBodyType = (String) hm.get("code");
	
		String planid = (String) this.getFormHM().get("planid");
		String object = (String) this.getFormHM().get("khObject");
		String flag = (String) hm.get("selType"); //1:手工选人 2:条件选人
		hm.remove("selType");
		
		String accordByDepartment = "false";//条件选人 按部门匹配	
		if(flag!=null && "1".equals(flag))
			 accordByDepartment = "false";
		else if(flag!=null && "2".equals(flag))
		{
			 accordByDepartment = (String) hm.get("accordByDepartment");
		}
		hm.remove("accordByDepartment");
		
		PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(),this.userView,planid);
	
		String str_sql = (String) this.getFormHM().get("str_sql");
		str_sql=PubFunc.hireKeyWord_filter_reback(str_sql);
		//haosl 20170215 浏览器兼容修改 start
		if("1".equals(flag)){
			if(StringUtils.isNotBlank(str_sql)){
				String[] temp = str_sql.split(",");
				for(int i=0;i<temp.length;i++){
					temp[i] = "'"+PubFunc.decrypt(temp[i]).substring(3)+"'";
				}
				
				str_sql = StringUtils.join(temp, ",");
			}
		}
		//haosl 20170215 浏览器兼容修改 end
		if(flag!=null && "2".equals(flag))//条件选人 选择用户权限范围内的人 手工选人在选择时候就加以控制过了
		{
			str_sql=PubFunc.decrypt(SafeCode.decode(str_sql));//条件的加密了，手工的没加密  zhaoxg add 2014-9-22
			String whl = bo.getPrivWhere(this.userView);
			str_sql+=whl;
		}
	
		
		if ("all".equals(object)) // 将考核主体应用于所有考核对象
		{
		    ArrayList objectList = (ArrayList) this.getFormHM().get("khObjectList");
		    for (int i = 0; i < objectList.size(); i++)
		    {
				CommonData vo = (CommonData) objectList.get(i);
				object = vo.getDataValue();
				bo.selMainBody(str_sql, planid, mainBodyType, object,accordByDepartment);
		    }
		}else
		    bo.selMainBody(str_sql, planid, mainBodyType, object,accordByDepartment);
		String method=(String) this.getFormHM().get("method");
		if(method==null || "1".equals(method)){
			bo.agreeSubjectNumber(planid, object, "per_pointpriv_"+planid);
		}

    }
}
