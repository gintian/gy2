package com.hjsj.hrms.transaction.kq.register.batch;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *批量修改保存 
 * @author Owner
 *wangyao
 */
public class BatchSaveTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//		ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");  //所有人员库
//		String select_pre=(String)this.getFormHM().get("select_pre"); //当前人员库
//		String code=(String)this.getFormHM().get("code"); //人员编号
//		String registerdate = (String) this.getFormHM().get("registerdate");  //当前天
//		ArrayList datelist=(ArrayList) this.getFormHM().get("datelist");  //考勤期间
		String settype = (String) hm.get("settype"); //指标ID
		String value = (String) hm.get("value");//值
//		String kind = (String) hm.get("kind"); 
//		String sp_flag=(String)this.getFormHM().get("sp_flag"); //状态
//		if(kind==null||kind.length()<=0)
//	    {
//	    	kind=RegisterInitInfoData.getKindValue(kind,this.userView);
//	    }
//		if(code==null||code.length()<=0){
//			code="";
//		}
//		if(sp_flag==null||sp_flag.equals(""))
//			sp_flag="all";
//		String cur_date="";   //天数
//		if (datelist != null && datelist.size() > 0)
//		{
//			CommonData vo = (CommonData) datelist.get(0);			
//			// 判断日期是否是从前台传过来的
//			if (registerdate != null && registerdate.length() > 0) 
//			{
//				cur_date = registerdate;
//			} else {
//				cur_date = vo.getDataValue();// 开始日期
//			}
//			ArrayList sql_db_list=new ArrayList();
//			if(select_pre!=null&&select_pre.length()>0&&!select_pre.equals("all"))
//			{
//				sql_db_list.add(select_pre);
//			}else
//			{
//				sql_db_list=kq_dbase_list;
//			}
//			getsql(settype,value,sql_db_list,cur_date,code,"Q03",kind,this.userView,"all",sp_flag);
//			this.getFormHM().put("settype", "");
//		}
		String sql_where = (String)this.getFormHM().get("strwhere");
		StringBuffer condition = new StringBuffer();
		String usernanme = this.userView.getUserName();
		condition.append("update Q03 set "+settype+" = '"+value+"',modusername='"+ usernanme +"',modtime=" + Sql_switcher.sqlNow() + " where ");
		condition.append("exists(SELECT 1 FROM (SELECT A0100,nbase,q03z0 " + sql_where + ") Q1 WHERE Q03.nbase=Q1.nbase AND Q03.A0100=Q1.A0100 AND Q03.q03z0=Q1.q03z0 )");
		condition.append(" AND q03z5 IN('01', '07')");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update(condition.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("settype", "");
	}
	/**
	 * 
	 * @param settype 指标di
	 * @param value 修改的值
	 * @param sql_db_list  当前人员库
	 * @param cur_date  具体那一天
	 * @param code 人员a0110
	 * @param table 那个表
	 * @param kind 1=部门 0=职位
	 * @param sp_flag 状态
	 */
	public void getsql(String settype,String value,ArrayList sql_db_list,String cur_date,String code,String table,String kind,UserView userView,String showtype,String sp_flag)
	{
		StringBuffer condition = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String usernanme = this.userView.getUserName();
			condition.append("update Q03 set "+settype+" = '"+value+"',modusername='"+ usernanme +"',modtime=" + Sql_switcher.sqlNow() + " where ");
			condition.append(" Q03Z0='"+cur_date+"'");
			if(code==null||code.length()<=0)
			{
				code=RegisterInitInfoData.getKqPrivCodeValue(userView);
			}
			if("1".equals(kind))
			{
				condition.append(" and e0122 like '"+code+"%'");
			}else if("0".equals(kind))
			{
				condition.append(" and e01a1 like '"+code+"%'");	
			}else
			{
				condition.append(" and b0110 like '"+code+"%'");	
			}
			if(!"all".equals(showtype))
			{
				condition.append(" and q03z5='"+showtype+"'");	
			}
			for(int i=0;i<sql_db_list.size();i++)
			{
				if(i>0)
				{
					condition.append(" or ");  
				}else
				{
					condition.append(" and ( ");	
				}
				condition.append(" (UPPER(nbase)='"+sql_db_list.get(i).toString().toUpperCase()+"'");
				String dbase=sql_db_list.get(i).toString();			
				String whereIN=getWhereINSql(userView,dbase);
				condition.append(" and a0100 in(select a0100 "+whereIN+") "); 
				condition.append(")");
				if(i==sql_db_list.size()-1)
					   condition.append(")");  
				if(!"all".equalsIgnoreCase(sp_flag))
				{
					condition.append(" and q03z5");
					condition.append("='");
					condition.append(sp_flag);
					condition.append("'");
				}	
			}
//			System.out.println("-------> = "+condition.toString());
			dao.update(condition.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**根据权限,生成select.IN中的查询串
     * @param code
     *        链接级别
     * @param userbase
     *        库前缀
     * @param cur_date
     *        考勤日期
     * @return 返回查询串
     * */
    public static String getWhereINSql(UserView userView,String userbase){
		 String strwhere="";	 
		 String kind="";
		 if(!userView.isSuper_admin())
			{
		           String expr="1";
		           String factor="";
				if("UN".equals(userView.getManagePrivCode()))
				{
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`B0110=`";
					  expr="1+2";
					}
				}
				else if("UM".equals(userView.getManagePrivCode()))
				{
					factor="E0122="; 
				    kind="1";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E0122=`";
					  expr="1+2";
					}
				}
				else if("@K".equals(userView.getManagePrivCode()))
				{
					factor="E01A1=";
					kind="0";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E01A1=`";
					  expr="1+2";
					}
				}
				else
				{
					 expr="1+2";
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
						factor+=userView.getManagePrivCodeValue();
					factor+="%`B0110=`";
				}			
				 ArrayList fieldlist=new ArrayList();
			        try
			        {        
				        
			            /**表过式分析*/
			            /**非超级用户且对人员库进行查询*/
			        	if(userView.getKqManageValue()!=null&&!"".equals(userView.getKqManageValue()))
				        	 strwhere=userView.getKqPrivSQLExpression("",userbase,fieldlist);
				        else
				            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
			           
			        }catch(Exception e){
			          e.printStackTrace();	
			        }
		
			}else{
				StringBuffer wheresql=new StringBuffer();
				wheresql.append(" from ");
				wheresql.append(userbase);
				wheresql.append("A01 ");
				kind="2";
				strwhere=wheresql.toString();
			}
		   // System.out.println(userbase+"---"+strwhere);
	       return strwhere;
	 }

}
