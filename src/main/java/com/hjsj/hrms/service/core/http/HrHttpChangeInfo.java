package com.hjsj.hrms.service.core.http;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Category;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
/**
 * 
 * 其系统主动调用此webservice，来同步人员和组织机构，查询的是触发器的临时表
 * @author Administrator
 *
 */
public class HrHttpChangeInfo {

	
	private String emp_table="t_hr_view";
	private String org_table="t_org_view";
	private String post_table="t_post_view";
	
	/**
	 * 日志对象
	 */
	private Category log = Category.getInstance(getClass().getName());
	
	private String errorMess="";

	public String getErrorMess() {
		return errorMess;
	}
	public void setErrorMess(String errorMess) {
		this.errorMess = errorMess;
	}
	
	/**
	 * 返回符合所带查询条件的人员变动信息数据
	 * @param conn
	 * @param whereStr自定义SQL查询条件	
	 * @return
	 */
	public String getWhereChangeUsers (String whereStr)
	{
		StringBuffer sql=new StringBuffer();		
		sql.append("select * from "+this.emp_table+"");
		if(whereStr!=null&&whereStr.length()>0)
		   sql.append(" where 1=1 and "+whereStr+"");		
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if(rs==null)
			return "";
		return JSONArray.fromObject(rs).toString();		
	}
	
	/**
	 * 返回符合所带查询条件的人员变动信息数据
	 * @param conn
	 * @param whereStr自定义SQL查询条件	
	 * @return
	 */
	public String getWhereChangeOrganizations(String whereStr)
	{
		StringBuffer sql=new StringBuffer();		
		sql.append("select * from "+this.org_table+"");
		if(whereStr!=null&&whereStr.length()>0)
		   sql.append(" where 1=1 and "+whereStr+"");		
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if(rs==null)
			return "";
		return JSONArray.fromObject(rs).toString();
	}
	
	/**
	 * 返回符合所带查询条件的人员变动信息数据
	 * @param conn
	 * @param whereStr自定义SQL查询条件	
	 * @return
	 */
	public String getWhereChangePost(String whereStr)
	{
		StringBuffer sql=new StringBuffer();		
		sql.append("select * from "+this.post_table+"");
		if(whereStr!=null&&whereStr.length()>0)
		   sql.append(" where 1=1 and "+whereStr+"");		
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if(rs==null)
			return "";
		return JSONArray.fromObject(rs).toString();	
	}
	
	
	/**
	 * [{
	 * 	'id': '10882324-8669-48D6-9A73-68C65389A3F2',
	 * 	'hr_flag': '1',
	 * 	'flag': '0'
	 * }, {
	 * 	'id': 'C1F1C5ED-74A9-416B-9282-1D5CA1F61069',
	 * 	'hr_flag': '1',
	 * 	'flag': '0'
	 * }]
	 * 
	 * id:<!—对应用户同步接收信息里的唯一性指标值。例如：身份证号、工号-->
	 * hr_flag:<!—HR系统发送的xml中的flag，即HR系统同步标志。1为新增，2为更新，3为停用 -->
	 * flag:<!—是否保存成功标识。0为成功，1为不成功 -->
	 */
	/**
	 * 以json形式，返回已同步信息，hr系统接收后将变动信息表，对应记录的变动标识置为已同步状态
	 * @param conn 数据库链接
	 * @param jsonStr json格式的数据,格式如上注释.
	 * @param onlyFiled 唯一标识(默认为unique_id)
	 * @param sysFlag 系统标识，由eHR系统提供如AD,OA(默认是flag)
	 * @param type "ORG" 代表机构，"HR"代表人员，"POST"代表岗位
	 * @return true/false
	 */
	public boolean returnSynchroJson(Connection conn,String jsonStr,String onlyFiled,String sysFlag,String type)
	{
		boolean isCorrect = true;
		if(jsonStr==null || jsonStr.trim().length()<=0) {
			this.setErrorMess("传递的Json字符串为空");
			return false;
		}
		JSONArray errorJsonArr = new JSONArray();
		String id = "";
		String hr_flag = "";
		String flag = "";
		ContentDAO dao=new ContentDAO(conn);
		JSONArray jsonArray = null;
		try {
			jsonArray = JSONArray.fromObject(jsonStr);
		} catch (Exception e1) {
			e1.printStackTrace();
			this.setErrorMess("解析Json字符串出错");
			return false;
		}
	    for (int i = 0; i < jsonArray.size(); i++) {
	    	JSONObject obj = (JSONObject) jsonArray.get(i);
	    	try {
				id = (String) obj.get("id");
				hr_flag = (String) obj.get("hr_flag");
				flag = (String) obj.get("flag");
			} catch (Exception e) {
				log.error("数据类型转化出现问题");
				this.setErrorMess("Json数据格式有问题,要求数据全为字符串格式");
				return false;
			}
	    	if(id==null || id.trim().length()<=0)
	    		continue;
	    	if(hr_flag==null || hr_flag.trim().length()<=0)
	    		continue;
	    	if(flag==null || flag.trim().length()<=0 || "1".equals(flag))
	    		continue;		    	
	    	StringBuffer sql = new StringBuffer();
	    	sql.append("update ");
			if ("HR".equalsIgnoreCase(type)) {
				sql.append(" " + this.emp_table + " ");
			} else if ("ORG".equalsIgnoreCase(type)) {
				sql.append(" " + this.org_table + " ");
			} else if ("POST".equalsIgnoreCase(type)) {
				sql.append(" " + this.post_table + " ");
			}
	    	sql.append(" set "+sysFlag+"=0 where "+onlyFiled+"='"+id+"' and " +sysFlag+ "='"+hr_flag+"'");
			try {
				int ss = dao.update(sql.toString());
				if(ss<=0)
				{
					isCorrect = false;
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("id", id);
					jsonObj.put("hr_flag", hr_flag);
					jsonObj.put("flag", "1");
					errorJsonArr.add(jsonObj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				log.error("id为："+id+"更新数据库出现问题!更新的Sql为:"+sql.toString());
				//this.setErrorMess("id为："+id+",更新数据库出现问题!");
				return false;
			}		    	
		}
		if(!isCorrect) {
			this.setErrorMess(errorJsonArr.toString());
		}
		return isCorrect;
	}
}
