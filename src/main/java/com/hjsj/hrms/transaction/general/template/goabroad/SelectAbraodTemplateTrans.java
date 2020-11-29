/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.goabroad;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:SelectAbraodTemplateTrans</p>
 * <p>Description:查询出国政审业务模板</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 23, 20065:46:32 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SelectAbraodTemplateTrans extends IBusiness {
	/**
	 * 取得单位模板列表
	 * @param unittype 单位性质
	 * @param opertationcode 业务代码
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getTemplateList(String unit_type,String opertationcode)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select tabid,name,sp_flag,content from template_table where operationcode='");
			buf.append(opertationcode);
			/*buf.append("' and flag=");
			buf.append(Integer.parseInt(unittype));*/
			buf.append("' and (");
			String units[]=unit_type.split(",");
			for(int i=0;i<units.length;i++)
			{
				buf.append(" flag ="+Integer.parseInt(units[i]));
				if(i<units.length-1)
					buf.append(" or ");
			}			
			buf.append(")");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(buf.toString());
			String sp_flag="0";
			while(rset.next())
			{
				/**此业务模板无权限时，不加(对人事变动)*/
				if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid")))
					continue;
				LazyDynaBean dynabean=new LazyDynaBean();
				dynabean.set("tabid",rset.getString("tabid"));				
				dynabean.set("name",rset.getString("name"));
				sp_flag=rset.getString("sp_flag")==null?"0":"1";
				dynabean.set("sp_flag",sp_flag);
				dynabean.set("content",Sql_switcher.readMemo(rset,"content"));
				list.add(dynabean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	public void execute() throws GeneralException {
		try
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String operationcode=sysbo.getValue(Sys_Oth_Parameter.GOBROAD,"operationcode");
			if(operationcode==null|| "".equalsIgnoreCase(operationcode))
				return;
				//throw new GeneralException(ResourceFactory.getProperty("error.goabroad.notoperation"));
			/**如果未定义，则按企业性质*/
			String unittype=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
			if(unittype==null|| "".equals(unittype))
				unittype="3";
			ArrayList list=getTemplateList(unittype,operationcode);
			this.getFormHM().put("templist",list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
