package com.hjsj.hrms.module.hire.businessobject;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 找回注册帐号公共类
 * @Title:        GetZpAccountsBo.java
 * @Description:  找回注册帐号公共类
 * @Company:      hjsj     
 * @Create time:  2016-12-1 下午05:31:44
 * @author        chenxg
 * @version       1.0
 */
public class GetZpAccountsBo {
	private String phoneField = "";
	private String onlynField = "";
	private String phoneFieldDesc = "";
	private String onlynFieldDesc = "";
	private String a0101Desc = "";
	private Connection conn;
	private RowSet frowset;

    public GetZpAccountsBo(Connection conn) {
        this.conn = conn;
        this.getFieldAndDesc();
    }

    public void getFieldAndDesc() {
        try {
            // 获取设置的电话 手机号指标
            this.phoneField = ConstantParamter.getMobilePhoneField().toLowerCase();
            EmployNetPortalBo bo = new EmployNetPortalBo(this.conn);
            // 获取唯一性指标
            this.onlynField = bo.getOnly_field();

			if (StringUtils.isNotEmpty(this.onlynField) && StringUtils.isNotEmpty(this.phoneField)) {
				/*ArrayList list = bo.getZpFieldList();
                ArrayList showFieldList = bo.getShowFieldList("A01", (HashMap) list.get(2), (HashMap) list.get(1), 0);
                for (int i = 0; i < showFieldList.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) showFieldList.get(i);
                    String itemid = ((String) bean.get("itemid")).toLowerCase();
                    if (this.onlynField.equalsIgnoreCase(itemid))
                        this.onlynFieldDesc = (String) bean.get("itemdesc");
                    
                    if (this.phoneField.equalsIgnoreCase(itemid))
                        this.phoneFieldDesc = (String) bean.get("itemdesc");

                    if ("a0101".equalsIgnoreCase(itemid))
                        this.a0101Desc = (String) bean.get("itemdesc");
                }*/
            	FieldItem phone = DataDictionary.getFieldItem(this.phoneField, "A01");
            	if(phone!=null&&"1".equals(phone.getUseflag()))
            		this.phoneFieldDesc = phone.getItemdesc();
            	FieldItem card = DataDictionary.getFieldItem(this.onlynField, "A01");
            	if(card!=null&&"1".equals(card.getUseflag()))
            		this.onlynFieldDesc = card.getItemdesc();
            	FieldItem name = DataDictionary.getFieldItem("a0101", "A01");
            	if(name!=null&&"1".equals(name.getUseflag()))
            		this.a0101Desc = name.getItemdesc();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPasswordInfo(String pw0, String pw1, String email, String guidkey) {
		// 设置新密码
		String info = "success";
		try {
			// 基于安全考虑，避免返回信息中带有password信息
			String emailId = ConstantParamter.getEmailField().toLowerCase();// 邮件指标
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname = ""; // 应聘人员库
			if (vo != null) {
			    dbname = vo.getString("str_value");
			}else {
			    throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append("update " + dbname + "A01");
			sql.append(" set userPassword=?");
			sql.append(" where " + emailId + "=?");
			sql.append(" and guidkey=?");

			ArrayList list = new ArrayList();
			list.add(pw1);
			list.add(email);
			list.add(guidkey);
			dao.update(sql.toString(), list);
			// 更改完密码之后删除该链接验证
			StringBuffer delete_sql = new StringBuffer();
			delete_sql.append("delete from t_sys_resetpassword");
			delete_sql.append(" where guidkey=?");
			list = new ArrayList();
			list.add(guidkey);
			dao.delete(delete_sql.toString(), list);
		} catch (Exception e) {
			e.printStackTrace();
			info = "fail";
		}
		return info;
	}

	public HashMap getUserName(String nameValue, String phoneValue, String onlyValue) {
		String return_code = "true";
		String msg = "";
		HashMap map = new HashMap();
		try {
			if ("true".equals(return_code)) {
				RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
				String dbname = vo.getString("str_value");

				GetZpAccountsBo bo = new GetZpAccountsBo(this.conn);
				String phoneField = bo.getPhoneField();
				String onlyField = bo.getOnlynField();

				StringBuffer sql = new StringBuffer();
				sql.append("SELECT USERNAME FROM ");
				sql.append(dbname + "A01 WHERE ");
				sql.append(" A0101=?");
				sql.append(" AND " + phoneField + "=?");
				sql.append(" AND " + onlyField + "=?");

				ArrayList<String> valueList = new ArrayList<String>();
				valueList.add(nameValue);
				valueList.add(phoneValue);
				valueList.add(onlyValue);

				ContentDAO dao = new ContentDAO(this.conn);
				this.frowset = dao.search(sql.toString(), valueList);
				if (this.frowset.next()) {
					msg = this.frowset.getString("USERNAME");
				} else {
				    return_code = "false";
				}
			}
			map.put("return_code", return_code);
			map.put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}
	
	/**
	 * 设置新密码
	 * @param new_pw
	 * @param a0100
	 * @param dbname
	 * @return
	 */
	public String setPassWord(String new_pw, String a0100, String dbname) {
		String return_code = "fail";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
		    StringBuffer sql = new StringBuffer();
		    sql.append("update "+dbname+"A01 set userPassword=? where a0100=?");
		    ArrayList<String> values = new ArrayList<String>();
		    values.add(new_pw);
		    values.add(a0100);
			int update = dao.update(sql.toString(),values);
			if(update>0)
				return_code = "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		return return_code;	
	}
	
	public String getPhoneField() {
		return phoneField;
	}

	public void setPhoneField(String phoneField) {
		this.phoneField = phoneField;
	}

	public String getOnlynField() {
		return onlynField;
	}

	public void setOnlynField(String onlynField) {
		this.onlynField = onlynField;
	}

	public String getOnlynFieldDesc() {
		return onlynFieldDesc;
	}

	public void setOnlynFieldDesc(String onlynFieldDesc) {
		this.onlynFieldDesc = onlynFieldDesc;
	}

	public String getPhoneFieldDesc() {
		return phoneFieldDesc;
	}

	public void setPhoneFieldDesc(String phoneFieldDesc) {
		this.phoneFieldDesc = phoneFieldDesc;
	}

	public String getA0101Desc() {
		return a0101Desc;
	}

	public void setA0101Desc(String a0101Desc) {
		this.a0101Desc = a0101Desc;
	}

}
