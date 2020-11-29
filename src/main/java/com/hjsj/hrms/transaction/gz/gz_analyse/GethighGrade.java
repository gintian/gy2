package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 按部门各月工资分析表获取高级弹窗
* 
* 类名称：GethighGrade   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Mar 25, 2014 4:01:43 PM   
* 修改人：zhaoxg   
* 修改时间：Mar 25, 2014 4:01:43 PM   
* 修改备注：   
* @version    
*
 */
public class GethighGrade extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn(),this.getUserView());
			StringBuffer str_value1 = new StringBuffer();//方案名title属性
			StringBuffer str_value2 = new StringBuffer();//薪资项目title属性
			
			String salaryid=(String) hm.get("salaryid");
			ArrayList list = new ArrayList();//左面
			ArrayList selectlist = new ArrayList();//右面
			ArrayList nameList = new ArrayList();//方案名
			CommonData data = new CommonData("00", "");

			nameList.add(data);

			bo.initXML();
			bo.getFaname(nameList, this.userView.getUserName(),str_value1);
			ArrayList gzProjectList = new ArrayList();//薪资项目
			gzProjectList=bo.gzProjectList(salaryid,str_value2);
			CommonData data1 = new CommonData("add", "<新建>");
			nameList.add(data1);
			this.getFormHM().put("_subclasslist",list);
			this.getFormHM().put("_selectsubclass",selectlist);
			this.getFormHM().put("nameList",nameList);
			this.getFormHM().put("gzProjectList",gzProjectList);
			if(str_value1.length()>0){
				this.getFormHM().put("faNameStr", SafeCode.encode(str_value1.substring(1)));
			}
			if(str_value2.length()>0){
				this.getFormHM().put("gzprojectStr", SafeCode.encode(str_value2.substring(1)));
			}
			
		}		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
