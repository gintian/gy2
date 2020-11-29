package com.hjsj.hrms.module.system.security.identification.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title: IdentificationTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-7-6 上午11:25:28</p>
 * @author jingq
 * @version 1.0
 */
public class IdentificationTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		StringBuffer sql = new StringBuffer();
		ArrayList<ColumnsInfo> collist = new ArrayList<ColumnsInfo>();
		String manual = "";
		try{
			//登陆库
			RecordVo dbvo = ConstantParamter.getConstantVo("SS_LOGIN");
			String dbstr = dbvo.getString("str_value");
			ArrayList<String> dblist = new ArrayList<String>();
			if(dbstr.indexOf(",")!=-1){
				String[] arr = dbstr.split(",");
				for (int i = 0; i < arr.length; i++) {
					dblist.add(arr[i]);
				}
			}
			if(dblist.size()<1){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("identification.error")));
			}
			//手机号指标
			RecordVo mobilevo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
			if(mobilevo == null) //手机号没有配置，前台给出提示   wangb 20190425 bug 47288
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("identification.error.phone")));
			String mobile = mobilevo.getString("str_value");
			//【11409】App登陆认证：自助用户通过移动app登录，可以验证成功并登录，但是ehr中的登录认证规则不生成此用户的绑定记录
			//登陆指标
			String username = getUserName();
			
			sql.append("select distinct ");
			sql.append("(select codeitemdesc from organization where codesetid = 'UN' and codeitemid = tab.b0110) b0110,");
			sql.append("(select codeitemdesc from organization where codesetid = 'UM' and codeitemid = tab.e0122) e0122,");
			sql.append("(select codeitemdesc from organization where codesetid = '@K' and codeitemid = tab.e01a1) e01a1,");
			sql.append("'' groupname,a0101,username,");
			sql.append(mobile);
			sql.append(",mobile_model,mobile_bind_time,mobile_bind_id,mobile_oauth_time,mobile_is_oauth from (");
			for (int i = 0; i < dblist.size(); i++) {
				sql.append("select b0110,e0122,e01a1,a0101,mob.username,");
				sql.append(mobile);
				sql.append(",mobile_model,mobile_bind_time,mobile_bind_id,mobile_oauth_time,mobile_is_oauth ");
				sql.append("from t_sys_login_user_info mob join ");
				sql.append(dblist.get(i));
				sql.append("A01 usr on mob.username = usr."+username+" where mob.mobile_bind_id is not null ");
				if(Sql_switcher.searchDbServer()==1)
					sql.append(" and mob.mobile_bind_id <> ''");
				if(i<dblist.size()-1)
					sql.append(" union all ");
			}
			sql.append(") tab union all ");
			sql.append("select '' b0110,'' e0122,'' e01a1,groupname,'' a0101,username,phone ");
			sql.append(mobile);
			sql.append(",mobile_model,mobile_bind_time,mobile_bind_id,mobile_oauth_time,mobile_is_oauth from (");
			sql.append("select groupid,tab.username,phone,mobile_model,mobile_bind_time,mobile_bind_id,mobile_oauth_time,mobile_is_oauth ");
			sql.append("from t_sys_login_user_info tab join OperUser ope on tab.username = ope.UserName ");
			sql.append("where tab.mobile_bind_id is not null ");
			if(Sql_switcher.searchDbServer()==1)
				sql.append(" and tab.mobile_bind_id <> ''");
			sql.append(") bbb  join usergroup gro on bbb.groupid = gro.groupid");
			
			ColumnsInfo column = null;
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("b0110");
			column.setColumnDesc(ResourceFactory.getProperty("label.codeitemid.un"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("e0122");
			column.setColumnDesc(ResourceFactory.getProperty("label.codeitemid.um"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("e01a1");
			column.setColumnDesc(ResourceFactory.getProperty("hmuster.label.post"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("groupname");
			column.setColumnDesc(ResourceFactory.getProperty("label.user.group"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("a0101");
			column.setColumnDesc(ResourceFactory.getProperty("label.title.name"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("username");
			column.setColumnDesc(ResourceFactory.getProperty("label.username"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId(mobile);
			column.setColumnDesc(ResourceFactory.getProperty("system.sms.mobimun"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("mobile_model");
			column.setColumnDesc(ResourceFactory.getProperty("identification.mobile_model"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("mobile_bind_time");
			column.setColumnDesc(ResourceFactory.getProperty("identification.mobile_bind_time"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("mobile_bind_id");
			column.setColumnDesc(ResourceFactory.getProperty("identification.mobile_bind_id"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnId("mobile_oauth_time");
			column.setColumnDesc(ResourceFactory.getProperty("identification.mobile_oauth_time"));
			collist.add(column);
			column = new ColumnsInfo();
			column.setColumnId("mobile_is_oauth");
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnDesc(ResourceFactory.getProperty("identification.label"));
			column.setRendererFunc("addInfo");
			collist.add(column);
			column = new ColumnsInfo();
			column.setTextAlign("center");
			column.setColumnDesc(ResourceFactory.getProperty("column.operation"));
			column.setRendererFunc("addParam");
			collist.add(column);
			
			ConstantXml constant = new ConstantXml(this.getFrameconn(), "SYS_LOGIN_SETTING");
			manual = constant.getTextValue("/params/authentication_type/manual");
		} catch (Exception e){
			e.printStackTrace();
		}
		this.getFormHM().put("columns", collist);
		this.getFormHM().put("sqlstr", sql.toString());
		this.getFormHM().put("orderby", "order by mobile_bind_time");
		this.getFormHM().put("manual", manual);
	}
	
	private String getUserName() {
		String username = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		if (login_vo == null) {
			username = "username";
		} else {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx == -1) {
				username = "username";
			} else {
				username = login_name.substring(0, idx);
				if ("#".equals(username) || "".equals(username)) {
					username = "username";
				}
			}
		}
		return username;
	}

}
