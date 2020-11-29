package com.hjsj.hrms.businessobject.kq.set;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * <p>Title:UserPriv.java</p>
 * <p>Description:考勤人员权限管理 </p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 28, 2012 1:49:00 PM</p>
 * @author 郑文龙
 * @version 1.0
 */
public class UserPriv {

	private UserView userView = null;
	
	public static int T_E_DBNAME = 0;//权限查询中带 人员库
	
	public static int T_NE_DBNAME = 1;//权限查询中不带 人员库
	
	private String code = "";//查询条件 中的 机构编码
	
	private String value = "";//查询条件 中的 机构编码值
	
	public UserPriv(UserView userView){
		this.userView = userView;
	}
	
	/**
	 * 
	 * @param dblist     查询人员库列表
	 * @param codeValue  查询机构编码
	 * @param isEdbpre   是否判断 人员库 AND nbase = '人员库'
	 * @return 返回sql 条件查询的 where 条件
	 * @throws GeneralException
	 */
	public String getKqUserPriv(ArrayList dblist,String codeValue,int isEdbpre) throws GeneralException{
		StringBuffer wherePriv = new StringBuffer();		
		for(Iterator it = dblist.iterator(); it.hasNext();){
			String dbpre = (String)it.next();
			if(!"".equals(dbpre)){
				String fw = getKqUserPriv(dbpre,codeValue,isEdbpre);
				if(!"".equals(fw)){
					if(wherePriv.length() >1){
						wherePriv.append(" AND EXISTS(SELECT 1 " + fw + ")");
					}else{
						wherePriv.append(" EXISTS(SELECT 1 " + fw + ")");
					}
				}
			}
		}
		return wherePriv.toString();
	}
	
	public String getKqUserPriv(String dbpre,String codeValue,int isEdbpre) throws GeneralException{
		StringBuffer wherePriv = new StringBuffer();
		String field = KqParam.getInstance().getKqDepartment();//获得考勤部门的参数设置   
		String privCode = "";    //权限机构编码
		String privValue = "";   //权限机构编码值
		String orgField = "";    //机构字段名
		String privOrgField = "";//权限机构字段名
		if(this.userView.isSuper_admin()){
			if(codeValue.length() > 2){
				this.code = codeValue.substring(0, 2);//获取机构编码
				this.value = codeValue.substring(2);  //获取机构编码值
				if(!"".equals(value)){
					//查询条件 机构 查询用字段
					if("UN".equals(code)){//单位
						orgField = "B0110";
					}else if("UM".equals(code)){//部门
						orgField = "E0122";
					}else if("@K".equals(code)){//岗位
						orgField = "E01A1";
					}
				}
				//表达式因子
				String expr="";
		        String factor="";
				if("".equals(this.value)){
					factor = orgField + "=" + this.value + "%`" + field + "=" + this.value +"%`";//因子
					expr = "1+2";// 表达式
				}
				String expression = expr + "|" +factor;
				if(T_E_DBNAME == isEdbpre){//判断  人员库
					wherePriv.append(getUserPriv(expression,dbpre) + " nbase = '" + dbpre + "'");
				}else if(T_NE_DBNAME == isEdbpre){// 不判断  人员库
					wherePriv.append(getUserPriv(expression,dbpre));
				}
			}else{
				if(T_E_DBNAME == isEdbpre){//判断  人员库
					wherePriv.append("FROM " + dbpre + "A01 WHERE nbase = '" + dbpre + "'");
				}else if(T_NE_DBNAME == isEdbpre){// 不判断  人员库
					wherePriv.append("FROM " + dbpre + "A01");
				}
			}
		}else{
			String expression = this.userView.getHighPrivExpression();//高级授权 条件编码
			String privCodeValue = this.userView.getKqManageValue();//获得考勤角色权限
			if(codeValue.length() > 1){
				this.code = codeValue.substring(0, 2);
				this.value = codeValue.substring(2);
			}
			if(privCodeValue.length() > 1){// 现走角色管理范围 如果没有  再走 人员管理范围  
				privCode = privCodeValue.substring(0, 2);//获取角色管理范围代码
				privValue = privCodeValue.substring(2);//获取角色管理范围值
			}else{
				privCode = this.userView.getManagePrivCode();//管理权限
				privValue = this.userView.getManagePrivCodeValue();
			}
			// 以下是组装 权限因子表达式 生产 where 条件
			if(!"".equals(privValue) && !"EP".equals(code)){//授权范围
				if(!"".equals(value)){
					//查询条件 机构 查询用字段
					if("UN".equals(code)){//单位
						orgField = "B0110";
					}else if("UM".equals(code)){//部门
						orgField = "E0122";
					}else if("@K".equals(code)){//岗位
						orgField = "E01A1";
					}
				}
				//权限条件 机构 查询用字段
				if("UN".equals(privCode)){ //单位
					 privOrgField = "B0110";
				}else if("UM".equals(privCode)){//部门
					 privOrgField = "E0122";
				}else if("@K".equals(privCode)){//岗位
					 privOrgField = "E01A1";
				}
				//表达式因子
				String expr="";
		        String factor="";
		        int expn = 1;
		        String _expression[] = expression.split("\\|");
		        //如果有 高级授权 那么  计算高级授权的因子个数
		        if(!"".equals(expression)){
		        	expn = _expression[1].split("\\`").length + 1;//添加因子个数
		        }
				if("".equals(field)){
					if("".equals(this.value)){
				        factor = privOrgField + "=" + privValue + "%`"; //因子
				        expr = "" + expn;// 表达式
					}else{
						this.value = privValue;
						this.code = privCode;
						factor = orgField + "=" + this.value + "%`" + privOrgField + "=" + privValue +"%`";//因子
						expr = expn + "*" + (expn + 1);// 表达式
					}
				}else{
					if("".equals(this.value)){
						factor = privOrgField + "=" + privValue + "%`" + field + "=" + privValue +"%`";//因子
						expr = expn + "+" + (expn + 1);// 表达式
					}else{
						this.value = privValue;
						this.code = privCode;
						factor = privOrgField + "=" + privValue + "%`" + field + "=" + privValue +"%`";//因子
						factor += orgField + "=" + this.value + "%`" + field + "=" + this.value +"%`";//因子
						expr = "(" + expn + "+" + (expn + 1) + ")*(" + (expn + 2) + "+" + (expn + 3) + ")";// 表达式
					}
				}
				//如果有 高级授权 那么  权限管理 和 高级授权 因子表达式合并查询
				if(!"".equals(expression)){
					expr = _expression[0] + "*(" + expr + ")" ;
					factor = _expression[1] + factor;
				}
				expression = expr + "|" +factor;
				if(!"".equals(dbpre)){
					if(T_E_DBNAME == isEdbpre){//判断  人员库
						wherePriv.append(getUserPriv(expression,dbpre));// + "  nbase = '" + dbpre + "'");
					}else if(T_NE_DBNAME == isEdbpre){// 不判断  人员库
						wherePriv.append(getUserPriv(expression,dbpre));
					}
				}
			} else {//未授权管理范围
				wherePriv.append("FROM " + dbpre + "A01 WHERE 1=2");
				if(codeValue != null && codeValue.length() > 0 && "EP".equals(code)) {
                    wherePriv.append(" OR " + dbpre + "A01.A0100 = '" + value + "'");
                }
			}
		}
		return wherePriv.toString();
	}
	
	private String getUserPriv(String expression,String dbpre) throws GeneralException{
		String wherePriv = this.userView.getKqPrivSQLExpression(expression,dbpre,new ArrayList());
		if(wherePriv.endsWith("1=2")){
			wherePriv = wherePriv.substring(0, wherePriv.length() - 4);
		}
		if(wherePriv.endsWith("AND")){
			wherePriv = wherePriv.substring(0, wherePriv.length() - 4);
		}
		return wherePriv;
	}
	
	
	public ArrayList getKqUserDbpre(){
		
		return null;
	}
	
	public boolean setKqUserDbpre(ArrayList dblist){
		
		return true;
	}
	

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}
	
}
