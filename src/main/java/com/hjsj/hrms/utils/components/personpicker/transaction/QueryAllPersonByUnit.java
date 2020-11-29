package com.hjsj.hrms.utils.components.personpicker.transaction;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.personpicker.support.PersonPickerSupport;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.List;
/**
 * 查询指定单位的下的所属人员（包括子单位的人员）
 * @author haosl
 *
 */
public class QueryAllPersonByUnit extends IBusiness {
	private static final long serialVersionUID = 1L;
	private String sexField;
	private String phoneField;
	private String emailField;
	private PersonPickerSupport support;
	private String loginNameField;//用户名指标
	private String onlyField; //人员唯一指标
	public String getLoginNameField() {
		return loginNameField;
	}
	public void setLoginNameField(String loginNameField) {
		this.loginNameField = loginNameField;
	}
	@Override
	public void execute() throws GeneralException {
		try {
			support = new PersonPickerSupport(frameconn,this.userView);
			sexField = support.getSexField(this.frameconn);
			if(StringUtils.isNotBlank(sexField)) {
				sexField = sexField.toLowerCase();
			}
			phoneField = support.getPhoneField(this.frameconn);
			if(StringUtils.isNotBlank(phoneField)) {
				phoneField = phoneField.toLowerCase();
			}
			emailField = support.getEmailField(this.frameconn);
			if(StringUtils.isNotBlank(emailField)) {
				emailField = emailField.toLowerCase();
			}
			DbNameBo db = new DbNameBo(frameconn);
			loginNameField = db.getLogonUserNameField();
			if(StringUtils.isNotBlank(loginNameField)) {
				loginNameField = loginNameField.toLowerCase();
			}
			onlyField = support.getOnlyField(frameconn);
			if(StringUtils.isNotBlank(onlyField)) {
				onlyField = onlyField.toLowerCase();
			}
			String attachTo = (String)formHM.get("attachTo");//单位id
			attachTo = PubFunc.decrypt(attachTo);
			String nbases = (String) formHM.get("nbases");
			boolean isSelfUser = (Boolean) this.getFormHM().get("isSelfUser");//是否自助用户  zhaoxg add 2015-9-7
			boolean selectByNbase = (Boolean) this.getFormHM().get("selectByNbase");//是否按不同人员库显示 chent 20170419
			boolean validateSsLOGIN = (Boolean) this.getFormHM().get("validateSsLOGIN");// 是否启用认证库校验 chent 20170313
			String ancester = (String) formHM.get("ancester");
			String level = (String) formHM.get("level");
			boolean isPrivExpression = (Boolean) this.getFormHM().get("isPrivExpression");//是否启用高级条件 chent 20160520
			String extend_str = (String) formHM.get("extend_str");
			List deprecate = (List) formHM.get("deprecate");//不推荐显示人员
			List d = support._decrypt(deprecate); // 解密后的deprecate
			
			String contentNbs = "";//区分人员库时，要获取当前的人员库
			if(selectByNbase){
				contentNbs = (String) formHM.get("contentNbs");
				if(StringUtils.isNotEmpty(contentNbs)){
					contentNbs = PubFunc.decrypt(contentNbs).substring(4);//substring是截掉前面的"nbs_"
				}
			}
			if(isSelfUser){//自助用户
				formHM.put("person", subordinatePerson(attachTo, ancester, level, nbases, extend_str, isPrivExpression, deprecate, validateSsLOGIN, selectByNbase, contentNbs));
			}else{//业务用户
				List person = new ArrayList();
				getOperUsers(attachTo, person, level, ancester, extend_str);
				formHM.put("person", person);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			
		}
	}
	/** 下属人员 
	 * @throws GeneralException */
	private List subordinatePerson(String attachTo, String ancester, String level, String nbases, String extend_str, Boolean isPrivExpression, List deprecate, boolean validateSsLOGIN, boolean selectByNbase, String contentNbs) throws GeneralException {
		List person = new ArrayList();
		StringBuffer sql = new StringBuffer();
		List params = new ArrayList();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(frameconn);
		try {
			String[] nbase = {};
			if(selectByNbase){//区分人员库时，直接拿当前的人员库
				nbase = new String[]{contentNbs};
			}else {
				if(!StringUtils.isEmpty(nbases)){// 设定的人员库
					nbase = nbases.split(",");
				}else {
					nbase = support.getNbase(validateSsLOGIN);
					if(!userView.isSuper_admin()){//非超级管理员取认证应用库和人员库的交集
						nbase = getNbase(nbase);
					}
				}
				if(nbase==null){
					return person;
				}
			}
			
			for (int i = 0; i < nbase.length; i++) {
				String n = nbase[i];
				if (StringUtils.isBlank(n)) {
					continue;
				}
				String tblName = n + "A01";
				if(sql.length() == 0){
					sql.append("");
				}else{
					sql.append(" UNION ");
				}
				StringBuffer dbperSql = new StringBuffer();
				// sexField性别指标
				dbperSql.append("SELECT guidkey,a0000,a0100,a0101,b0110,e0122,e01a1,(CASE WHEN  nullif (e0122,'')  IS NOT NULL  THEN e0122 ELSE b0110 END) as atta,? AS nbase");
				String sexField = support.getSexField(this.frameconn);
				String phoneField = support.getPhoneField(this.frameconn);
				String emailField = support.getEmailField(this.frameconn);
				if(StringUtils.isNotBlank(sexField) && dbperSql.indexOf(sexField)<0) {
					dbperSql.append(",").append(sexField);
				}
				if (StringUtils.isNotBlank(phoneField) && dbperSql.indexOf(phoneField)<0) {
					dbperSql.append(",").append(phoneField);
				}
				params.add(n);
				if (StringUtils.isNotBlank(emailField) && dbperSql.indexOf(emailField)<0) {
					dbperSql.append(",").append(emailField);
				}
				if(StringUtils.isNotBlank(loginNameField) && dbperSql.indexOf(loginNameField)<0) {
					dbperSql.append(",").append(loginNameField);
				}
				if(StringUtils.isNotBlank(onlyField) && dbperSql.indexOf(onlyField)<0) {
					dbperSql.append(",").append(onlyField);
				}
				dbperSql.append(" FROM ").append(tblName).append(" WHERE");
				dbperSql.append(" (CASE WHEN  nullif (e0122,'')  IS NOT NULL  THEN e0122 ELSE b0110 END) like '"+attachTo+"%'");// 添加所有时，要把下下级人员也同时添加 chent 20161010

				if(StringUtils.isNotBlank(extend_str)&&extend_str.toLowerCase().indexOf("salaryid")!=-1){
					//薪资发放个性化内容 添加薪资类别人员范围的限制 extend_str串内容为 salaryid=xxx;appdate=xxx zhanghua 2019-05-15
					String sql_salary = support.getSql_for_salary(tblName);
					dbperSql.append(sql_salary);
 					if(StringUtils.isNotBlank(sql_salary)) {
						params.add(n.toUpperCase());
					}
				}else{

					if(!StringUtils.isEmpty(extend_str)){
						// 按检索条件和人员范围
						if(extend_str.startsWith("template/"))
						{
							String searchSql=support.getTemplateSearchSql(extend_str,isPrivExpression,n);
							if(!StringUtils.isEmpty(searchSql))
								dbperSql.append(searchSql);
						}
						else
						{
							String extend_sql="";
							extend_sql= extend_str.replace("${nbase}", n);
							dbperSql.append(" and A0100 in("+ extend_sql +")");
						}

					}
				}
				//权限控制sql haosl update 2017-10-27
				if(isPrivExpression) {
					String privSql = support.getPrivSQL(n);
					dbperSql.append(privSql);
				}
				sql.append(dbperSql);
			}
			if(sql.length() > 0){
				sql.append(" ORDER BY a0000");
				rs = dao.search(sql.toString(), params);
				rs.last();//将光标移动到最后一行
				int rowCount = rs.getRow();//得到当前行号，即结果集记录数
                //上限从200调整为500 haosl update 2019年8月15日
                if(rowCount>500)
					throw new Exception("超过500人，单次添加的上限为500人!");
				rs.beforeFirst();
				while (rs.next()) {
					String pre =PersonPickerSupport.nvl(rs.getString("nbase"), "");
					String a0100 = PersonPickerSupport.nvl(rs.getString("a0100"), "");
					String atta = PersonPickerSupport.nvl(rs.getString("atta"), "");  
					if (deprecate.contains(pre + a0100)) {
						continue;
					}
					
					LazyDynaBean bean = support._pack(rs, emailField);
					if (support.isNotEmpty(bean)) {
						bean.set("ancester", support.nvl(ancester, ""));
						bean.set("attachTo", support.nvl(PubFunc.encrypt(attachTo), ""));
						bean.set("realattach", PubFunc.encrypt(atta));
						bean.set("level", Integer.parseInt(support.nvl(level, "0")) + 1 + "");
						person.add(bean);
					}
				}
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return person;
	}
	
	private String[] getNbase(String[] nbase){
		String nbases = userView.getDbpriv().toString();//取人员库
		if(",".equals(nbases)){//没任何人员库权限
			nbase = null;
		}else{//有人员库权限，取交集
			String[] tempNbase = nbases.split(",");
			StringBuffer tempNbases = new StringBuffer();
			for (int i = 0; i < tempNbase.length; i++) {
				if (StringUtils.isBlank(tempNbase[i]))
					continue;
				for (int j = 0; j < nbase.length; j++) {
					if (tempNbase[i].equals(nbase[j])) {
						tempNbases.append(tempNbase[i]+",");
						break;
					}
				}
			}
			if(tempNbases.length()==0)
				nbase = null;
			else 
				nbase = tempNbases.toString().split(",");
		}
		return nbase;
	}
	/**
	 * 因为用户组的id定义没有规则，目前使用递归查询用户组下的用户
	 */
	private void getOperUsers(String attachTo,List person,String level,String ancester,String extend_str) {
		boolean selfUserIsExceptMe = (Boolean) this.getFormHM().get("selfUserIsExceptMe");//业务用户时是否排除自己。chent 20170329
		List groups  = new ArrayList();
		if(!"1".equals(attachTo))
			groups = support.loadUsergrops(attachTo, level,ancester,extend_str);
		for(int i = 0 ; i<groups.size() ; i++) {
			LazyDynaBean bean = (LazyDynaBean)groups.get(i);
			String groupid = (String)bean.get("id");
			groupid = PubFunc.decrypt(groupid);
			String level1 = (String)bean.get("level");
			String ancester1 = (String)bean.get("ancester");
			String extend_str1 = (String)bean.get("extend_str");
			getOperUsers(groupid, person, level1, ancester1, extend_str1);
		}
		if (PersonPickerSupport.isNotEmpty(attachTo)) {
			List users = support.loadUserNodes(attachTo,level,ancester,extend_str,selfUserIsExceptMe);
			person.addAll(users);
		}
	}
}
