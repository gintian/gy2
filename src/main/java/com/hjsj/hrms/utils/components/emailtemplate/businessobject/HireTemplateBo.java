package com.hjsj.hrms.utils.components.emailtemplate.businessobject;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;

/**
 * <p>Title:HireTemplateBo</p>
 * <p>Description:获取管理范围</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2015 4:33:55 PM</p>
 * @author sunming
 * @version 1.0
 */
public class HireTemplateBo {
	private Connection conn;
	public HireTemplateBo(Connection conn)
	{
		this.conn=conn;
	}
	public String getB0110(UserView view,String opt) throws GeneralException 
	{
		String b0110="";
		try
		{
			String codeid="";
			if(view.isAdmin()|| "1".equals(view.getGroupId()))
			{
				b0110="HJSJ";
			}
			else
			{
				
				 if("7".equals(opt))//招聘
		            codeid = view.getUnitIdByBusi("7");
		         else if("9".equals(opt))//绩效
		            codeid = view.getUnitIdByBusi("5");
				if(codeid==null|| "".equals(codeid)|| "UN".equalsIgnoreCase(codeid)){
					if("7".equals(opt))//招聘
						throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
					if("9".equals(opt))//绩效
						throw GeneralExceptionHandler.Handle(new Exception("您没有设置绩效模块的管理范围"));
				}
				if(codeid.trim().length()==3)
				{
					b0110="HJSJ";
				}else{
					if(codeid.indexOf("`")==-1)
					{
						if(codeid.startsWith("UN")||codeid.startsWith("UM")){
							b0110=codeid.substring(2);
						}else{
							b0110=	codeid;
						}
					}
					else
					{
	        			String[] temps=codeid.split("`");
	        			codeid="";
	           			for(int i=0;i<temps.length;i++){
	           				if(codeid.startsWith("UN")||codeid.startsWith("UM")){
	           					b0110+=temps[i].substring(2)+"`";
							}else{
								b0110+=temps[i]+"`";
							}
	           				
	           			}
					}
				}
//				/**业务用户*/
//				if(view.getStatus()==0)
//				{
//					String code=view.getUnit_id();
//					if(code==null||code.trim().equals(""))
//					{
//						throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//					}
//					else if(code.trim().length()==3)
//					{
//						b0110="HJSJ";
//					}
//					else
//					{
//						String[] b_arr=code.split("`");
//						for(int i=0;i<b_arr.length;i++)
//						{
//							if(b_arr[i]==null||b_arr[i].equals(""))
//							{
//								continue;
//							}
//							b0110+="`"+b_arr[i].substring(2);
//						}
//						b0110=b0110.substring(1);
//					}
//				}
//				/**自助用户*/
//				else if(view.getStatus()==4)
//				{
//					String code=view.getManagePrivCode();
//					String value=view.getManagePrivCodeValue();
//					if(code==null||code.trim().equals(""))
//					{
//						throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//					}
//					else
//					{
//						if(value==null||value.trim().equals(""))
//						{
//							b0110="HJSJ";
//						}
//						else
//						{
//							b0110=value;
//						}
//					}
//				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("您没有设置该模块的管理范围"));
		}
		return b0110;
	}

}
