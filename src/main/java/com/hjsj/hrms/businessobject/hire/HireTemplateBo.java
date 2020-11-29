package com.hjsj.hrms.businessobject.hire;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;

public class HireTemplateBo {
	private Connection conn;
	public HireTemplateBo(Connection conn)
	{
		this.conn=conn;
	}
	public String getB0110(UserView view) throws GeneralException 
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
				
				codeid=view.getUnitIdByBusi("7");
				if(codeid==null|| "".equals(codeid)|| "UN".equalsIgnoreCase(codeid)){
					throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
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
			throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
		}
		return b0110;
	}

}
