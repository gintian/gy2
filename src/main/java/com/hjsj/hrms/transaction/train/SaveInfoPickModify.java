/*
 * 创建日期 2005-8-19
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author luangaojiong
 *	保存修改的采集表名称
 */
public class SaveInfoPickModify extends IBusiness {

	
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String id=this.getFormHM().get("infoId").toString();
		try
		{
			String name=this.getFormHM().get("pickTableName").toString();
			ArrayList infoAddList=(ArrayList)this.getFormHM().get("infoAddList");
			StringBuffer sb=new StringBuffer();
			if(infoAddList.size()<=0)
			{
				sb.append("update R19 set R1910='");
				sb.append(name);
				sb.append("'");
				sb.append(",");
				sb.append("R1906=");
				sb.append(Sql_switcher.dateValue(DateStyle.getSystemTime().substring(0,10)));
				sb.append("");
				sb.append(",");
				sb.append("R1909='");
				sb.append(this.userView.getUserFullName());
				sb.append("'");
				sb.append(",");
				sb.append("E0122='");
				sb.append(PubFunc.nullToStr(this.userView.getUserDeptId()));
				sb.append("'");
				sb.append(",");
				sb.append("B0110='");
				sb.append(this.userView.getUserOrgId());
				sb.append("'");
				sb.append(" where R1901='");
				sb.append(id);
				sb.append("'");
			}
			else
			{
				sb.append("update R19 set R1910='");
				sb.append(name);
				sb.append("'");
				sb.append(",");
				sb.append("R1906=");			
				sb.append(Sql_switcher.dateValue(DateStyle.getSystemTime().substring(0,10)));
				sb.append("");
				sb.append(",");
				sb.append("R1909='");
				sb.append(this.userView.getUserFullName());
				sb.append("'");
				sb.append(",");
				sb.append("E0122='");
				sb.append(PubFunc.nullToStr(this.userView.getUserDeptId()));
				sb.append("'");
				sb.append(",");
				sb.append("B0110='");
				sb.append(this.userView.getUserOrgId());
				sb.append("',");
				for(int i=0;i<infoAddList.size();i++)
				{
					BusifieldBean bsb=(BusifieldBean)infoAddList.get(i);
					if(i==infoAddList.size()-1)
					{
						if("N".equals(bsb.getItemtype()))
						{
							sb.append(bsb.getItemid());
							sb.append("=");
							sb.append(PubFunc.NullToZero(bsb.getValue()));
						}
						else
						{
							if("D".equals(bsb.getItemtype()))
							{
								sb.append(bsb.getItemid());
								sb.append("='");					
								sb.append(PubFunc.FormatDate(bsb.getValue()));
								sb.append("'");
							}
							else
							{
								if("0".equals(bsb.getCodesetid()))
								{
									sb.append(bsb.getItemid());
									sb.append("='");
									sb.append(PubFunc.doStringLength(bsb.getValue(),Integer.parseInt(bsb.getItemlength())));
									sb.append("'");
								}
								else
								{
									sb.append(bsb.getItemid());
									sb.append("='");
									sb.append(bsb.getValue());
									sb.append("'");
								}
							}
						}
					}
					else
					{
						if("N".equals(bsb.getItemtype()))
						{
							sb.append(bsb.getItemid());
							sb.append("=");
							sb.append(PubFunc.NullToZero(bsb.getValue()));
							sb.append(",");
						}
						else
						{
							if("D".equals(bsb.getItemtype()))
							{
								sb.append(bsb.getItemid());
								sb.append("='");
								
								sb.append(PubFunc.FormatDate(bsb.getValue()));
								
								sb.append("',");
							}
							else
							{
								if("0".equals(bsb.getCodesetid()))
								{
									sb.append(bsb.getItemid());
									sb.append("='");
									sb.append(PubFunc.doStringLength(bsb.getValue(),Integer.parseInt(bsb.getItemlength())));
									sb.append("',");
								}
								else
								{
									sb.append(bsb.getItemid());
									sb.append("='");
									sb.append(bsb.getValue());
									sb.append("',");
								}
							
							}
							
						}
					}
				}
				sb.append(" where R1901='");
				sb.append(id);
				sb.append("'");
			}
			dao.update(sb.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}

}
