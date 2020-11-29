package com.hjsj.hrms.module.jobtitle.subjects.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 资格评审_学科组
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 */
public class SubjectsForMeetingBo {
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public SubjectsForMeetingBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
	
	
	/**
	 * 显示评审会议下的学科组
	 * @param w0301：会议编号
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getSubjects(String w0301,String categoriesid,String selectGroupId) throws GeneralException {
		
		ArrayList<HashMap<String, String>> subjectsList = new ArrayList<HashMap<String, String>>();
		
		ArrayList<String> list = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
    		StringBuilder sql = new StringBuilder();
    		
    		sql.append("select distinct A.group_id,B.group_name,B.b0110 ");
    		sql.append("From zc_expert_user A ,zc_subjectgroup B ");
    		sql.append("where A.group_id = B.group_id and A.type=2 and A.w0301=?");
    		list.add(w0301);
    		if(StringUtils.isNotBlank(categoriesid)) {
    			 sql.append(" and A.categories_id=?");
    			 list.add(categoriesid);
    		}
    		if(StringUtils.isNotBlank(selectGroupId)) {
    			sql.append(" and A.group_id=?");
    			list.add(selectGroupId);
    		}
			rs = dao.search(sql.toString(), list);
			
			while (rs.next()) {
				String group_id = rs.getString("group_id");
				String group_name = rs.getString("group_name");
				//组内人数
				int pnumber = this.getGroupCountMap(w0301, null, group_id);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("group_id", PubFunc.encrypt(group_id));
				map.put("group_name", group_name);
				map.put("pnumber", pnumber+"");
				String unitName = "";
				if(!StringUtils.isEmpty(AdminCode.getCodeName("UN", rs.getString("b0110"))))
					unitName = AdminCode.getCodeName("UN", rs.getString("b0110"));
				else 
					unitName = AdminCode.getCodeName("UM", rs.getString("b0110"));
				map.put("b0110", unitName);
				subjectsList.add(map);
			}
			//在投票方式，换了分组进来选人的时候这时候什么都查不到
			if(subjectsList.size() == 0 && StringUtils.isNotBlank(selectGroupId)) {
				subjectsList = getBlankList(rs,dao,w0301,categoriesid,selectGroupId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return subjectsList;
	}
	
	/**
	 * 在投票方式，换了分组进来选人的时候这时候什么都查不到
	 * @param rs
	 * @param dao
	 * @param selectGroupId
	 * @return
	 */
	private ArrayList<HashMap<String, String>> getBlankList(RowSet rs,ContentDAO dao,String w0301,String categoriesid,String selectGroupId) {
		ArrayList<HashMap<String, String>> subjectsList = new ArrayList<HashMap<String, String>>();
		ArrayList<String> list = new ArrayList<String>();
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			list.add(selectGroupId);
			rs = dao.search("select group_name,b0110 from zc_subjectgroup where group_id=?", list);
			if(rs.next()) {
				String group_name = rs.getString("group_name");
				map.put("group_id", PubFunc.encrypt(selectGroupId));
				map.put("group_name", group_name);
				String unitName = "";
				if(!StringUtils.isEmpty(AdminCode.getCodeName("UN", rs.getString("b0110"))))
					unitName = AdminCode.getCodeName("UN", rs.getString("b0110"));
				else 
					unitName = AdminCode.getCodeName("UM", rs.getString("b0110"));
				map.put("b0110", unitName);
				subjectsList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjectsList;
	}
	/**
	 * 获取学科组人员信息
	 * @param w0301会议编号
	 * @param group_id学科组编号
	 * @return ArrayList人员信息
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getSubjectsPerson(String w0301, String group_id,String categoriesid) throws GeneralException {
		
		ArrayList<HashMap<String, String>> personList = new ArrayList<HashMap<String, String>>();
		
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	RowSet dbRs = null;
    	/**获得专家图片保存跟路径**/
		
    	ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
        String fileRootPath = constantXml.getNodeAttributeValue("/filepath", "rootpath");
        fileRootPath = fileRootPath.replace("\\", "/");
        String truthpath = fileRootPath+"/multimedia/jobtitle/qualifications/expert_photo/";
    	DbNameBo dbNameBo = new DbNameBo(this.conn, this.userview);
    	ArrayList dbList = dbNameBo.getAllDbNameVoList();
    	ArrayList<String> list = new ArrayList<String>();
    	try {
    		HashMap a01map = new HashMap();
    		for(int i=0; i<dbList.size(); i++){
    			list = new ArrayList();
				RecordVo vo = (RecordVo)dbList.get(i);
				String pre = vo.getString("pre");
				StringBuilder dbSql = new StringBuilder("select A0100,guidkey,'" + pre.toLowerCase() + "' as pre from "+pre+"A01 where GUIDKEY in (");
				dbSql.append("select B.GUIDKEY ");
				dbSql.append("from zc_expert_user A ");
				dbSql.append("INNER JOIN W01 B ");
				dbSql.append("on A.W0101=B.W0101 ");
				dbSql.append("where A.w0301=? and A.group_id=? and type=2 group by B.GUIDKEY)");
				list.add(w0301);
				list.add(group_id);
				dbRs = dao.search(dbSql.toString(), list);
				while(dbRs.next()){
					a01map.put(dbRs.getString("guidkey"), pre + "`" + dbRs.getString("A0100"));
				}
			}
			
    		list = new ArrayList();
			//获取学科组人员信息
    		StringBuilder sql = new StringBuilder();
    		
			String w0101 = "";//专家id
			String w0107 = "";//专家姓名
			String w0103 = "";//单位名称
			String w0105 = "";//部门
			String b0110 = "";//所属单位
			String flag = "";//聘任标识
			String role = "";//角色
			String imgQuality = "l";//图片质量(低)
			String imgQuality_encr = PubFunc.encrypt(imgQuality);
    		sql.append("select A.W0101,max(B.W0107) W0107,max(A.role) role,max(B.GUIDKEY) GUIDKEY,max(B.W0111) W0111,"
    				+ "max(B.W0103) W0103,max(B.W0105) W0105,max(B.b0110) b0110 ");
    		sql.append("from zc_expert_user A ");
    		sql.append("INNER JOIN W01 B ");
    		sql.append("on A.W0101=B.W0101 ");
    		sql.append("where A.w0301=? and A.group_id=? and type=2");
    		list.add(w0301);
    		list.add(group_id);
    		sql.append(" group by A.w0101 order by max(A.role) desc");
			rs = dao.search(sql.toString(), list);
			while (rs.next()) {
				String imgDbName = "";//人员库(图片用)
				String imgA0100 = "";//人员编号(图片用)
				w0101 = rs.getString("W0101");
				w0107 = rs.getString("W0107");
				w0103 = rs.getString("W0103");
				w0105 = rs.getString("W0105");
				b0110 = rs.getString("b0110");
				role = rs.getString("role");
				String W0111 = rs.getString("W0111");
				String guidkey = rs.getString("GUIDKEY");
				
				if("2".equals(W0111)){//内部专家
					String[] dbnameAndA0100 = String.valueOf(a01map.get(guidkey)).split("`");
					imgDbName = PubFunc.encrypt(dbnameAndA0100[0]);
					imgA0100 = PubFunc.encrypt(dbnameAndA0100[1]);
				}
				HashMap<String, String> map = new HashMap<String, String>();
				String w0101_enc = PubFunc.encrypt(w0101);
				map.put("w0101", w0101_enc);
				map.put("w0107", w0107==null?"":w0107);
				map.put("w0103", w0103==null?"":w0103);
				map.put("w0105", w0105==null?"":w0105);
				String un = AdminCode.getCodeName("UN", b0110);
				String um = AdminCode.getCodeName("UM", b0110);
				String b0110Name = StringUtils.isEmpty(un) ? um : un;
				map.put("b0110", b0110Name);
				map.put("role", role);
				map.put("flag", flag);
				map.put("imgUsr",  imgDbName);
				map.put("imgA0100", imgA0100);
				map.put("imgQuality", imgQuality_encr);
				map.put("w0111", W0111);
				
	            String imagename = "";
				//通过专家编号查找专家对应的图片
		        File f = new File(truthpath);
		        if(f.isDirectory()){
		            File[] s = f.listFiles();
		            for(int i=0;i<s.length;i++) {
		                String name = s[i].getName();
		                int index = name.indexOf(".");
		                String fnameString = name.substring(0,index);
		                if(fnameString.equals(w0101)){
		                    imagename = name;
		                }
		            }
		        }
	            map.put("filePath", truthpath+imagename);
				
				personList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(dbRs);
		}
		
		return personList;
	}
	
	/**
	 * 新增学科组
	 * @param w0301:会议编号
	 * @param group_id:学科组编号
	 * @param w0101:专家编号
	 * @return msg
	 * @throws GeneralException
	 */
	public String createSubjectsPerson(String w0301, String group_id, String w0101,String role,String categoriesid) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("zc_expert_user");
			//账号表序号、学科组编号、会议ID 、申报人主键序号ID 、帐号、密码、帐号状态、帐号类型、专家编号、描述信息、角色
			IDFactoryBean idf = new IDFactoryBean();
			vo.setString("user_id", idf.getId("zc_expert_user.user_id", "", conn));
			vo.setString("group_id", group_id);
			vo.setString("w0301", w0301);
			vo.setString("w0501", "xxxxxx");
			vo.setString("username", null);
			vo.setString("password", null);
			vo.setInt("state", 0);
			vo.setInt("type", 2);
			vo.setString("w0101", w0101);
			vo.setString("description", null);
			vo.setString("role", role);
			vo.setString("categories_id", "xxxxxx");
			
			dao.addValueObject(vo);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return msg;
	}
	public String createSubjects(String w0301, String group_id, String categoriesid) throws GeneralException{
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			if(StringUtils.isBlank(categoriesid))
				categoriesid = savePersonnelCategories(w0301,group_id,dao,"");//保存到申报人分组表中
			
			//获取学科组人员信息
			//haosl 修改 需要联合查询专家库的专家，排除已经在专家库中删掉的人员
    		StringBuilder sql = new StringBuilder();
    		sql.append("select expertid,role ");
    		sql.append("from zc_subjectgroup_experts A INNER JOIN W01 B ");
    		sql.append("on A.expertid=B.W0101 ");
    		sql.append("where A.group_id=? and A.flag='1' ");
    		sql.append(" and ("+ Sql_switcher.today() +" between start_date and end_date or end_date is null)");

    		ArrayList<String> list = new ArrayList<String>();
    		list.add(group_id);
    		
			rs = dao.search(sql.toString(), list);
			
			rs.last();
			int rowcount = rs.getRow();//获得学科组下专家的条数
			if(rowcount == 0){//0条的话，只增加一条模板记录就可以了。
				RecordVo vo = new RecordVo("zc_expert_user");
				//账号表序号、学科组编号、会议ID 、申报人主键序号ID 、帐号、密码、帐号状态、帐号类型、专家编号、描述信息、角色
				IDFactoryBean idf = new IDFactoryBean();
				vo.setString("user_id", idf.getId("zc_expert_user.user_id", "", conn));
				vo.setString("group_id", group_id);
				vo.setString("w0301", w0301);
				vo.setString("w0501", "xxxxxx");
				vo.setString("username", null);
				vo.setString("password", null);
				vo.setInt("state", 0);
				vo.setInt("type", 2);
				vo.setString("w0101", null);
				vo.setString("description", null);
				vo.setString("role", "0");
				vo.setString("categories_id", categoriesid);
				
				dao.addValueObject(vo);
			}else{//不为0条的话，就要增加相应的条数
				rs.beforeFirst();//将游标移到第一行前
				while (rs.next()) {
					String expertid = rs.getString("expertid");
					String role = rs.getString("role");
					this.createSubjectsPerson(w0301, group_id, expertid,role,categoriesid);
				}
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		}  finally{
			PubFunc.closeDbObj(rs);
		}
		
		return msg;
	}
	/**
	 * 修改学科组名称
	 * @param group_id：学科组编号
	 * @param list:可变长的HashMap（key:目标字段  value：值）
	 * @return
	 * @throws GeneralException
	 */
	public String modifySubjects(String group_id, ArrayList<HashMap<String, String>> list) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList<String> sqllist = new ArrayList<String>();
			StringBuilder sql = new StringBuilder();
			sql.append("update zc_subjectgroup ");
			for(int i=0; i<list.size(); i++){
				sql.append("set "+list.get(i).get("key")+"=? ");
//				sqllist.add(list.get(i).get("key"));
				sqllist.add(list.get(i).get("value"));
				if(i != list.size()-1){
					sql.append(", ");
				}
			}
    		sql.append("where group_id=? ");
    		sqllist.add(group_id);
			dao.update(sql.toString(), sqllist);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return msg;
	}
	
	/**
	 * 删除学科组
	 * @param w0301：会议编号
	 * @param group_id：学科组编号
	 * @return msg
	 * @throws GeneralException
	 */
	public void deleteSubjects(String w0301, String group_id, String categoriesid) throws GeneralException{
			
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			if(StringUtils.isBlank(categoriesid)) {
				//删除人员表和人员分组表对应的group_id的内容
				String sqlPerson = "delete from zc_personnel_categories where categories_id in (select categories_id from zc_categories_relations where w0301=? and group_id=?) ";
				
				String sqls = "delete from zc_personnel_categories where w0301=? and group_id=?";
				ArrayList<String> list = new ArrayList<String>();
				list.add(w0301);
				list.add(group_id);
				dao.delete(sqlPerson, list);
				dao.delete(sqls, list);
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append("delete ");
			sql.append("from zc_expert_user ");
			sql.append("where w0301=? and group_id=? and type=2");
			ArrayList<String> list = new ArrayList<String>();
			list.add(w0301);
			list.add(group_id);
			
			dao.delete(sql.toString(), list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 新增学科组组内成员
	 * @param w0301：会议编号
	 * @param group_id：学科组编号
	 * @param personidList：专家编号
	 * @return msg
	 * @throws GeneralException
	 */
	public String createSubjectsPerson(String w0301, String group_id, ArrayList<String> personidList, String categories_id) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				StringBuilder sql = new StringBuilder();
				ArrayList<String> list = new ArrayList<String>();
				// 需要新增的专家是否已经存在
				sql.append("select count(W0101) as count ");
				sql.append("From zc_expert_user ");
				sql.append("where W0301=? ");
				sql.append("and group_id=? ");
				sql.append("and W0101=? ");
				sql.append("and type=2 ");
				list.add(w0301);
				list.add(group_id);
				list.add(expertid);
				rs = dao.search(sql.toString(), list);
				while(rs.next()){
					if(rs.getInt("count") > 0){//存在该专家
						//这里没有对人员启用和非启用的控制，表中没有这个字段
					}else {
						RecordVo vo = new RecordVo("zc_expert_user");
						//账号表序号、学科组编号、会议ID 、申报人主键序号ID 、帐号、密码、帐号状态、帐号类型、专家编号、描述信息、角色
						IDFactoryBean idf = new IDFactoryBean();
						vo.setString("user_id", idf.getId("zc_expert_user.user_id", "", conn));
						vo.setString("group_id", group_id);
						vo.setString("w0301", w0301);
						vo.setString("w0501", "xxxxxx");
						vo.setString("username", null);
						vo.setString("password", null);
						vo.setInt("state", 0);
						vo.setInt("type", 2);
						vo.setString("w0101", expertid);
						vo.setString("description", null);
						vo.setString("role", "0");
						vo.setString("categories_id",categories_id);
						dao.addValueObject(vo);
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return msg;
	}
	/**
	 * 删除学科组组内成员
	 * @param w0301：会议编号
	 * @param group_id：学科组编号
	 * @param personidList：专家编号
	 * @return 
	 * @throws GeneralException
	 */
	public void deleteSubjectsPerson(String w0301, String group_id, ArrayList<String> personidList, String categoriesid) throws GeneralException{
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		int count = 0;//总共有多少人
		try {
			String sqls = "select count(*) as count from zc_expert_user where w0301=? and group_id=? and type=2";
			ArrayList<String> list = new ArrayList<String>();
			list.add(w0301);
			list.add(group_id);
			rs = dao.search(sqls,list);
			if(rs.next()) {
				count = rs.getInt("count");
			}
			//删除最后一个人的时候同时删除对应的分组和分组人员
			if(StringUtils.isBlank(categoriesid) && count == personidList.size()) {
				//删除人员表和人员分组表对应的group_id的内容
				String sqlPerson = "delete from zc_personnel_categories where categories_id in (select categories_id from zc_categories_relations where w0301=? and group_id=?) ";
				
				String sqlCategories = "delete from zc_personnel_categories where w0301=? and group_id=?";
				list.clear();
				list.add(w0301);
				list.add(group_id);
				dao.delete(sqlPerson, list);
				dao.delete(sqlCategories, list);
			}
			for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				
				StringBuilder sql = new StringBuilder();
				sql.append("delete ");
				sql.append("from zc_expert_user ");
				sql.append("where w0301=? ");
				sql.append("and group_id=? ");
				sql.append("and W0101=? ");
				sql.append("and type=2 ");
				
				list = new ArrayList<String>();
				list.add(w0301);
				list.add(group_id);
				list.add(expertid);
				dao.delete(sql.toString(), list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 变更人员信息
	 * @param w0301：会议编号
	 * @param group_id：组编号
	 * @param w0101:专家编号
	 * @param username：用户名
	 * @param password：密码
	 * @param role：角色
	 * @return msg
	 */
	public String changeSubjectsPerson(String w0301, String group_id, String w0101, String username, String password, String role) throws GeneralException{
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			// 判断账户是否存在
			boolean isExist = false;
			StringBuilder sql = new StringBuilder();
			sql.append("select count(username) as count from zc_expert_user where username='"+username+"'");
			rs = dao.search(sql.toString());
			while(rs.next()){
				int count = rs.getInt("count");
				if(count > 0){
					isExist = true;
				}
			}
			
			// 如已存在，查看账号是不是自己创建的
			boolean isSelf = false;
			if(isExist){//账号已存在
				sql.setLength(0);
				sql.append("select count(username) as count from zc_expert_user where username='"+username+"' and w0301 ='"+w0301+"' and group_id ='"+group_id+"' and w0101 ='"+w0101+"' and type=2");
				rs = dao.search(sql.toString());
				while(rs.next()){
					int count = rs.getInt("count");
					if(count > 0){
						isSelf = true;
					}
				}
			}
			//不存在账号或者是本人创建，可以更新
			if(!isExist || isSelf){
				
				// 继续判断组长数量(排除当前账号)，默认每个组的组长只能有一个
				boolean isLeaderNotOver = true;
				if("1".equals(role)) {
					sql.setLength(0);
					sql.append("select count(role) as count from zc_expert_user where w0301 ='"+w0301+"' and type=2 and group_id ='"+group_id+"' and role='1' and w0101 <> '"+ w0101 +"'");
					rs = dao.search(sql.toString());
					if(rs.next()){
						int count = rs.getInt("count");
						if(count > 0){
							isLeaderNotOver = false;
						}
					}
				}
				
				if(isLeaderNotOver) {//check无误，可以更新,更新所有数据（包括：模板记录、已经上会的评审人记录）
					sql.setLength(0);
					sql.append("update zc_expert_user ");
					sql.append("set username=?,password=?,role=? ");
					sql.append("where w0301=? ");
					sql.append("and group_id=? ");
					sql.append("and W0101=? ");
					
					ArrayList<String> list = new ArrayList<String>();
					list.add(username);
					list.add(password);
					list.add(role);
					list.add(w0301);
					list.add(group_id);
					list.add(w0101);
					
					dao.update(sql.toString(), list);
				} else {
					msg = "对不起，该组已经存在组长！";
				}
			} else{
				msg = "对不起，该账号已存在！";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return msg;
	}
	
	/**
	 * 获取人员信息
	 * @param w0301：会议编号
	 * @param group_id：组编号
	 * @param w0101：专家编号
	 * @return：人员信息map
	 * @throws GeneralException
	 */
	public HashMap<String, String> getSubjectsPersonInfo(String w0301, String group_id, String w0101) throws GeneralException{
		HashMap<String, String> map = new HashMap<String, String>();
		
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
		try {
			StringBuilder sql = new StringBuilder(); 
	    	sql.append("select username,password,role ");
	    	sql.append("from zc_expert_user ");
	    	sql.append("where type=2 and w0301=? and group_id=? and W0101=? and w0501='xxxxxx'");
	    	
	    	ArrayList<String> list = new ArrayList<String>();
	    	list.add(w0301);
	    	list.add(group_id);
	    	list.add(w0101);
			rs = dao.search(sql.toString(), list);
			
			while (rs.next()) {
				map.put("username", rs.getString("username"));
				map.put("password", rs.getString("password"));
				String role = rs.getString("role");
				if(StringUtils.isEmpty(role) || "2".equals(role)){//角色：没有保存过也显示【组员】
					role = "2";
				}
				map.put("role", role);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return map;
	}
	/**
     * 获取列头，表格渲染
     * @return
     */
    public ArrayList<ColumnsInfo> getColumnListSubjectPicker(){
		
		/** 获取类型名称 */
		ArrayList columnTmp = new ArrayList();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select * from zc_subjectgroup where 1=2";
			rs = dao.search(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				
				String columnItem = metaData.getColumnName(i).toLowerCase();
				
				ColumnsInfo columnsInfo = new ColumnsInfo();
				if("group_id".equalsIgnoreCase(columnItem)){//学科组编号
					columnsInfo.setEncrypted(true);
					columnsInfo.setColumnId(columnItem);
					columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			        columnTmp.add(columnsInfo);
					
				} else if("group_name".equalsIgnoreCase(columnItem)){//名称
					columnsInfo.setColumnId(columnItem);
			        columnsInfo.setColumnDesc("学科组名称");
			        columnsInfo.setColumnType("M");
			        columnsInfo.setColumnWidth(200);
			        columnTmp.add(columnsInfo);
			        
				} else if("b0110".equalsIgnoreCase(columnItem)){//所属单位
					columnsInfo.setColumnId(columnItem);
					columnsInfo.setColumnDesc("所属机构");
					columnsInfo.setColumnType("A");
					columnsInfo.setCodesetId("UM");
			        columnsInfo.setColumnWidth(200);
			        columnTmp.add(columnsInfo);
			        
				} else if("create_time".equalsIgnoreCase(columnItem)){//创建时间
					columnsInfo.setColumnId(columnItem);
			        columnsInfo.setColumnDesc("创建时间");
			        columnsInfo.setColumnType("D");
			        columnsInfo.setColumnWidth(200);
			        columnTmp.add(columnsInfo);
			        
				} else if("create_fullname".equalsIgnoreCase(columnItem)){//创建人
					columnsInfo.setColumnId(columnItem);
			        columnsInfo.setColumnDesc("创建人");
			        columnsInfo.setColumnType("M");
			        columnsInfo.setColumnWidth(200);
			        columnTmp.add(columnsInfo);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
			
		return columnTmp;
	}
    /**
     * 获得需要查询的sql字段
     * @return
     */
	public String getSqlSubjectPicker() {
		StringBuilder sql =  new StringBuilder();//查询sql
		ArrayList<String> searchItems = new ArrayList<String>();//检索项目
		
		sql.append("select * ");
		sql.append("from zc_subjectgroup ");
		sql.append("where state=1 and group_id in (select distinct(group_id) from zc_subjectgroup_experts)");
		
		String b0110 = this.userview.getUnitIdByBusi("9");//取得所属单位
		if(b0110.split("`")[0].length() > 2){//组织机构不为空：取本级，下级。为空：最高权限
			String whereSql = new JobtitleUtil(this.conn, this.userview).getB0110Sql_down(b0110);
			sql.append(whereSql);
		}
		
		return sql.toString();
	}
	
	/**
	 * 得到当前会议已选择的学科组编号
	 * @param w0301 会议编号
	 * @return
	 * @throws GeneralException
	 */
	public String getGroupIds(String w0301) throws GeneralException{
		String groupIds="";
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {    		
    		StringBuilder sql = new StringBuilder();
    		sql.append("select distinct A.group_id");
    		sql.append(" From zc_expert_user A ,zc_subjectgroup B");
    		sql.append(" where A.group_id = B.group_id and A.type=2 and A.w0301=? ");
    		ArrayList<String> list = new ArrayList<String>();
    		list.add(w0301);
			rs = dao.search(sql.toString(), list);
			while (rs.next()) {
				groupIds += "'"+rs.getString("group_id")+"',";
			}
			if(groupIds.length()>0){
				groupIds = " and group_id not in (" + groupIds.substring(0,groupIds.length()-1) + ") ";
			}
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		return groupIds;
	}
	
	/**
	 * @param sql 原sql
	 * @param personidList 需要排出的组列表
	 * @return 所有人列表
	 * @throws GeneralException
	 */
	public ArrayList<String> getAllSubjects(String sql, ArrayList<String> list) throws GeneralException {
		
		ArrayList<String> allList = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
    		StringBuilder newsql = new StringBuilder(sql);
    		if(list.size() != 0){
    			newsql.append(" and group_id not in ( ");
    		}
    		for(int i=0; i<list.size(); i++){
				String group_id = list.get(i);
				group_id = PubFunc.decrypt(group_id);
				if(i != list.size()-1){
					newsql.append("'" + group_id + "', ");
				}else{
					newsql.append("'" + group_id + "' ");
				}
    		}
    		if(list.size() != 0){
    			newsql.append(" ) ");
    		}
    		rs = dao.search(newsql.toString());
    		while(rs.next()){
    			String group_id = rs.getString("group_id");
    			group_id = PubFunc.encrypt(group_id);
    			allList.add(group_id);
    		}
    		
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		
		
		return allList;
	}
	/**
	 * @param sql 原sql
	 * @param personidList 需要排出的人员列表
	 * @return 所有人列表
	 * @throws GeneralException
	 */
	public ArrayList<String> getAllSubjectsPerson(String sql, ArrayList<String> personidList) throws GeneralException {
		
		ArrayList<String> personList = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
    		StringBuilder newsql = new StringBuilder(sql);
    		if(personidList.size() != 0){
    			newsql.append(" and W0101 not in ( ");
    		}
    		for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				if(i != personidList.size()-1){
					newsql.append("'" + expertid + "', ");
				}else{
					newsql.append("'" + expertid + "' ");
				}
    		}
    		if(personidList.size() != 0){
    			newsql.append(" ) ");
    		}
    		rs = dao.search(newsql.toString());
    		while(rs.next()){
    			String w0101 = rs.getString("W0101");
    			w0101 = PubFunc.encrypt(w0101);
    			personList.add(w0101);
    		}
    		
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return personList;
	}
	
	/**
	 * 组内人数
	 * @return
	 * @throws GeneralException
	 */
	public int getGroupCountMap(String w0301,String categories_id,String selectGroupId) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		int count = 0;
    	try {
    		StringBuffer sql = new StringBuffer();
    		sql.append("select COUNT(1) as count from zc_expert_user where w0301=?");
    		sql.append(" and w0501='xxxxxx'");
    		sql.append(" and group_id=? and type=2");
    		list.add(w0301);
    		list.add(selectGroupId);
    		rs = dao.search(sql.toString(),list);
    		while(rs.next()){
    			count = rs.getInt("count");
    		}
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
    	return count;
	}
	
	/**
	 * 在评审人员设置页面添加专业组的同时，将对应的专业组添加到zc_personnel_categories表中
	 * @param w0301
	 * @param review_link1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
	 * @return
	 */
	public String savePersonnelCategories(String w0301,String group_id,ContentDAO dao,String review_link) {
		ArrayList<String> list = new ArrayList<String>();
		String categories_id = "";
		String group_name = "";
		RowSet rs = null;
		try {
			list.add(group_id);
			rs = dao.search("select group_name from zc_subjectgroup where group_id=?",list);
			if(rs.next()) {
				group_name = rs.getString("group_name");
			}
			
			IDFactoryBean idf = new IDFactoryBean();
			RecordVo vo = new RecordVo("zc_personnel_categories");
			
			categories_id = idf.getId("zc_personnel_categories.categories_id", "", conn);
			vo.setString("categories_id", categories_id);
			vo.setString("w0301", w0301);
			vo.setString("review_links", StringUtils.isBlank(review_link)?"2":review_link);//如果是学科组进来的直接写2就行
			vo.setString("name", group_name);
			vo.setString("approval_state", "0");
			vo.setInt("expertnum", 0);
			vo.setInt("submitnum", 0);
			vo.setString("group_id", group_id);
			
			dao.addValueObject(vo);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return categories_id;
	}
}
