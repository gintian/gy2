/**
 * 
 */
package com.hjsj.hrms.transaction.sys.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 *<p>Title:查询是否已定义啦。。。</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-6-29:上午10:17:10</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchQueryTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
	    String classpre=(String)this.getFormHM().get("base");
	    /**=A人员,=B单位,=K职位
	     *=5(Y) 党组织
	     *=6(V) 团组织
	     *=7(W) 工会组织
	     */
	    if(classpre==null|| "".equals(classpre))
	    	classpre="1";
	    ArrayList paramList=new ArrayList();
		try
		{

			 RecordVo vo=null;
			 if("3".equals(classpre))
				 vo=ConstantParamter.getRealConstantVo("SS_KQUERYTEMPLATE", getFrameconn());
			 else if("2".equals(classpre))
				 vo=ConstantParamter.getRealConstantVo("SS_BQUERYTEMPLATE", getFrameconn());
			 else if("5".equals(classpre))
				 vo=ConstantParamter.getRealConstantVo("SS_YQUERYTEMPLATE", getFrameconn());
			 else if("6".equals(classpre))
				 vo=ConstantParamter.getRealConstantVo("SS_VQUERYTEMPLATE", getFrameconn());
			 else if("7".equals(classpre))
				 vo=ConstantParamter.getRealConstantVo("SS_WQUERYTEMPLATE", getFrameconn());
			 else if("4".equals(classpre))
				 vo=ConstantParamter.getRealConstantVo("SS_HQUERYTEMPLATE", getFrameconn()); 
			 else 
				 vo=ConstantParamter.getRealConstantVo("SS_QUERYTEMPLATE", getFrameconn());
			 if(!(vo==null||vo.getString("constant")==null|| "".equals(vo.getString("constant"))))
			 {
				 String[] fields=StringUtils.split(vo.getString("str_value"),",");
				 for(int i=0;i<fields.length;i++)
				 {
					 String field=fields[i];
					 if("4".equals(classpre)){
//						 if(field.toUpperCase().equals("CORCODE")){
//							 CommonData tempvo = new CommonData("CORCODE","基准岗位代码");
//							 paramList.add(tempvo);
//							 continue;
//						 }
						 if("H0100".equals(field.toUpperCase())){
							 CommonData tempvo = new CommonData("h0100","基准岗位名称");
							 paramList.add(tempvo);
							 continue;
						 }
					 }
					 FieldItem item=DataDictionary.getFieldItem(field);
					 if(item!=null)
					 {
						 CommonData tempvo=new CommonData(item.getItemid(),item.getItemdesc());
						 paramList.add(tempvo);
					 }
					 
				 }
			 }
			 this.getFormHM().put("dlist", paramList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
