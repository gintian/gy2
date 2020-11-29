package com.hjsj.hrms.businessobject.general.inform;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;

public class CommonSql {
	/**
	 * 根据机构代码生成sql语句
	 * @param userView
	 * @param a_code
	 * @param dbname
	 * @param mainitemid
	 * @return
	 */
	public static String whereCodeStr(UserView userView,String a_code,String dbname,String mainitemid){
		StringBuffer wherestr=new StringBuffer();
		StringBuffer sexpr=new StringBuffer();
		StringBuffer sfactor=new StringBuffer();
		if(a_code==null||a_code.trim().length()<1){
			a_code = userView.getManagePrivCode()+userView.getManagePrivCodeValue();
		}
		
		if(a_code!=null&&a_code.trim().length()>1){
			String codesetid=a_code.substring(0, 2);
			String value=a_code.substring(2);

			if(value!=null&&value.trim().length()>0){
				if("UN".equalsIgnoreCase(codesetid)){
					sexpr.append("B0110=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else if("UM".equalsIgnoreCase(codesetid)){
					sexpr.append("E0122=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else if("@K".equalsIgnoreCase(codesetid)){
					sexpr.append("E01A1=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else{
					String[] codearr =a_code.split(":");
					if(codearr.length==3){
						sexpr.append(codearr[1]+"=");
						sexpr.append(codearr[2]);
						sexpr.append("*`");
						sfactor.append("1");
					}
				}
			}else{
				sexpr.append("B0110=");
				sexpr.append(value);
				sexpr.append("*`B0110=`");
				sfactor.append("1+2");
			}
		}	
		/**过滤条件*/
		try {
			String strwhere=userView.getPrivSQLExpression(sfactor.toString()+"|"+sexpr.toString(),
					dbname,false,true,new ArrayList());
			wherestr.append("select "+mainitemid+" ");
			wherestr.append(strwhere);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return wherestr.toString();
	}
	/**
	 * 根据机构代码生成sql语句
	 * @param userView
	 * @param a_code
	 * @param dbname
	 * @param mainitemid
	 * @return
	 */
	public static String whereCodeStr(String a_code,String dbname,boolean vorg){
		String wherestr = "";
		if(a_code!=null&&a_code.trim().length()>1){
			String codesetid=a_code.substring(0, 2);
			String value=a_code.substring(2);

			if(value!=null&&value.trim().length()>0){
				if("UN".equalsIgnoreCase(codesetid)){
					wherestr = dbname+"A01.B0110 like '"+value+"%'";
					if(vorg) {
                        wherestr+=" or t_vorg_staff.B0110 like '"+value+"%'";
                    }
				}else if("UM".equalsIgnoreCase(codesetid)){
					wherestr = dbname+"A01.E0122 like '"+value+"%'";
					if(vorg) {
                        wherestr+=" or t_vorg_staff.B0110 like '"+value+"%'";
                    }
				}else if("@K".equalsIgnoreCase(codesetid)){
					wherestr = dbname+"A01.E01A1 like '"+value+"%'";
					if(vorg) {
                        wherestr+=" or t_vorg_staff.B0110 like '"+value+"%'";
                    }
				}else{
					String[] codearr =a_code.split(":");
					if(codearr.length==3){
						wherestr+=codearr[1]+"='";
						wherestr+=codearr[2]+"'";
					}else{
						wherestr="1=1 ";
					}
				}
			}else{
				wherestr ="1=1 ";
			}
		}else{
			wherestr ="1=2 ";
		}	
		return wherestr;
	}
	/**
	 * 根据机构代码生成sql语句
	 * @param userView
	 * @param a_code
	 * @param dbname
	 * @param vorg
	 * @return
	 */
	public static String whereCodeStr(UserView userview,String a_code,String dbname,boolean vorg){
		String wherestr = "";
		wherestr = whereCodeStr(userview,a_code,dbname,"A0100");
		if(wherestr!=null&&wherestr.trim().length()>5){
			wherestr = wherestr.substring(wherestr.indexOf("WHERE")+5);
		}
		if(!userview.isSuper_admin()&&userview.getStatus()==0) {
            wherestr=wherestr.replaceAll(" AND 1=2", "");
        }
		if(a_code!=null&&a_code.trim().length()>1){
			String codesetid=a_code.substring(0, 2);
			String value=a_code.substring(2);
			if(value!=null&&value.trim().length()>0){
				if("UN".equalsIgnoreCase(codesetid)){
					if(vorg) {
                        wherestr+=" or t_vorg_staff.B0110 like '"+value+"%'";
                    }
				}else if("UM".equalsIgnoreCase(codesetid)){
					if(vorg) {
                        wherestr+=" or t_vorg_staff.B0110 like '"+value+"%'";
                    }
				}else if("@K".equalsIgnoreCase(codesetid)){
					if(vorg) {
                        wherestr+=" or t_vorg_staff.B0110 like '"+value+"%'";
                    }
				}
			}
		}
		return wherestr;
	}
}