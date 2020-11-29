package com.hjsj.hrms.businessobject.infor;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
/**
 *  Title:person
 *  
 *  discription: 人员代办
 *  
 * @author guodd
 *
 */
public class PersonMatterTask {
    
	private Connection conn;
	private UserView userView;
	
	public PersonMatterTask(Connection conn, UserView userView){
		this.conn = conn;
		this.userView = userView;
	}
	
	/**
	 * 人员信息变动审批代办
	 * @return ArrayList
	 */
	public  ArrayList getPersonInfoChange(){
		
		ArrayList taskList = new ArrayList();
		
		try{
		    List rs = null;
		    String sql = "select a0101,content,sp_flag,create_time from t_hr_mydata_chg ";
		    //没有权限 跳出
		    if(userView.hasTheFunction("03082") || userView.hasTheFunction("260632")){
		        //人员修改信息不需要审批 跳出
		        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		        String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		        String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		        if(!"0".equals(approveflag) && !"0".equals(inputchinfor)) {
		            StringBuffer where = new StringBuffer();
		            if(getWhere(where)) {
		                rs = ExecuteSQL.executeMyQuery(sql+where + " order by create_time desc,A0000", conn);
		                if(rs != null && rs.size()  > 0) {
		                    
		                    StringBuffer userName =new StringBuffer();
		                    for(int i=0;i<rs.size();i++){
		                        userName.append(((LazyDynaBean)rs.get(i)).get("a0101"));
		                        if(rs.size()>3 && i==2){
		                            userName.append("....");
		                            break;
		                        }
		                        userName.append("、");
		                    }
		                    userName.deleteCharAt(userName.length()-1);
		                    StringBuffer mess = new StringBuffer("员工变动信息审核（"); 
		                    mess.append(userName+"，共"+rs.size()+"人）_审核");
		                    String url="/general/approve/personinfo/sum.do?b_query=link&returnvalue=&nmodule=4&task=0";
		                    CommonData cData=new CommonData(); 
		                    cData.setDataName(mess.toString());
		                    cData.setDataValue(url);
		                    if(rs.size()>0) {
                                cData.put("date",((LazyDynaBean)rs.get(0)).get("create_time").toString());
                            } else {
                                cData.put("date","" );
                            }
		                    taskList.add(cData);
		                }
		            }
		        }
		    }
			
			sql += " where sp_flag='07' and nbase='" + this.userView.getDbname() + "' and a0100='" + this.userView.getA0100() + "'";
            rs= ExecuteSQL.executeMyQuery(sql, conn);
            if(rs != null && rs.size() > 0) {
                StringBuffer mess = new StringBuffer("员工信息修改申请（驳回）"); 
                String url="/selfservice/selfinfo/addselfinfo.do?b_add=add&a0100=A0100&i9999=I9999&actiontype=update&setname=A01&flag=infoself";
                CommonData cData=new CommonData(); 
                cData.setDataName(mess.toString());
                cData.setDataValue(url);
                if(rs.size()>0) {
                    cData.put("date",((LazyDynaBean)rs.get(0)).get("create_time").toString());
                } else {
                    cData.put("date","" );
                }
                taskList.add(cData);
            }
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return taskList;
	}
	
	private boolean getWhere(StringBuffer where) {
		try {
			where.append(" where sp_flag='02' ");
			// 不是超级用户
			if (!userView.isSuper_admin()) {
				String a_code = this.userView.getManagePrivCodeValue();
				where.append(" and (B0110 like '" + a_code + "%' or E0122 like '" 
						+ a_code + "%' or E01a1 like '" + a_code + "%') and (");
				// 库过滤
				ArrayList nbList = userView.getPrivDbList();
				if (nbList.size() > 0) {
					StringBuffer temp = new StringBuffer();
					for (int i = 0; i < nbList.size(); i++) {
						//temp.append(",'" + nbList.get(i) + "'");
						//通过 数据库前缀+人员id判断权限，因为不同数据库有id会有重复的
						String wherePrivSql = "select '"+nbList.get(i)+"'"+Sql_switcher.concat()+"A0100 " + userView.getPrivSQLExpression(nbList.get(i).toString(), false);
						where.append(" nbase"+Sql_switcher.concat()+"A0100 in (" + wherePrivSql + ") or ");
					}
					where.append(" 1=2 )");
					//where.append(" and nbase in (" + temp.substring(1) + ")");
				} else {
					where.append(" nbase='" + this.userView.getDbname() + "')");
				}
				// 人员权限过滤
				//	String wherePrivSql = "select A0100 " + userView.getPrivSQLExpression(userView.getDbname(), false);
				//	where.append(" and A0100 in (" + wherePrivSql + ") ");
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return true;
	}
}
