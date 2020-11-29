package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * <p>Title:SearchMainBodyTrans.java</p>
 * <p>Description:考核实施/指定考核主体/查找考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-08-12 10:45:24</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String plan_id = (String) hm.get("plan_id");
		//【60717】VFS+UTF-8+达梦：绩效管理，指定考核主体时候，后台报错"unterminated quoted string"
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		if(StringUtils.isNotBlank(plan_id)&&!pattern.matcher(plan_id).matches()){
			plan_id =PubFunc.decryption(plan_id);
		}
		hm.remove("plan_id");
		if(plan_id==null|| "".equals(plan_id))
		    plan_id = (String)this.getFormHM().get("planid");
		
		String typeCode = (String) hm.get("code");
		hm.remove("code");
		if(typeCode==null || (typeCode!=null && "".equals(typeCode)))
		    typeCode="all";
		
		String khObject = (String) this.getFormHM().get("khObject");
	
		HashMap objs = new HashMap();
		if(khObject==null || (khObject!=null && "all".equals(khObject)))
		{
		    ArrayList objs1=(ArrayList)this.getFormHM().get("khObjectList");
		    for(int i = 0;i<objs1.size();i++)
		    {
				CommonData cd = (CommonData)objs1.get(i);
				objs.put(cd.getDataValue(),cd.getDataName());      
		    }
		    this.getFormHM().put("khObject", "all");
		}else
		{
		    ArrayList objs1=(ArrayList)this.getFormHM().get("khObjectList");
		    for(int i = 0;i<objs1.size();i++)
		    {
				CommonData cd = (CommonData)objs1.get(i);
				if(khObject.equals(cd.getDataValue()))
				    objs.put(cd.getDataValue(),cd.getDataName());       
		    }
		}
		PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(), this.getUserView());
		ArrayList list = bo.getMainBodyList2(plan_id, objs, typeCode);
		
		this.getFormHM().put("mainbodys", list);
    }

}
