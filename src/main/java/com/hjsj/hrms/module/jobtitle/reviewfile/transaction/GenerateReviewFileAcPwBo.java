package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 生成学院聘任组专家帐号和密码
 * @author haosl
 *
 */
public class GenerateReviewFileAcPwBo {
	private Connection conn = null;
	private UserView userView = null; 
	
	
	
	public GenerateReviewFileAcPwBo(Connection conn) {
		this.conn = conn;
	}


	public GenerateReviewFileAcPwBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}


	public Connection getConn() {
		return conn;
	}


	public void setConn(Connection conn) {
		this.conn = conn;
	}


	public UserView getUserView() {
		return userView;
	}


	public void setUserView(UserView userView) {
		this.userView = userView;
	}


	/**
     * 生成材料审查账号（同行不需要材料审查，所以不用生成审查账号）
     * @param w0301
     * 			评审会议编号
     * @param expertIdList
     * 			专家编号
     * @throws GeneralException
     */
    public void createExamineAccounts(String w0301,String w0501,int type,String group_id,List<String> personidList) throws GeneralException{
    	String msg = "";
    	ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rs1 = null;
		try {
	    	for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				StringBuilder sql = new StringBuilder();
				ArrayList list = new ArrayList();
				// 需要新增的专家是否已经存在
				sql.append("select count(user_id) as count ");
				sql.append("From zc_expert_user ");
				sql.append("where W0501=? ");//申报编号
				sql.append("and w0101=? ");//专家编号
				sql.append("and type=? ");//评议组阶段
				sql.append("and "+Sql_switcher.isnull("usetype", "1")+"=1 ");//评审帐号
				list.add(w0501);
				list.add(expertid);
				list.add(type);
				
				rs = dao.search(sql.toString(), list);
				if(rs.next()){
					if(rs.getInt("count") > 0){//存在该专家
						continue;//跳过
					}else {
						String sql1 = "select username,password from zc_expert_user where W0501<>'xxxxxx' and  w0101=? and type=? and w0301=? and "+Sql_switcher.isnull("usetype", "1")+"=1";
						ArrayList list1 = new ArrayList();
						list1.add(expertid);
						list1.add(type);
						list1.add(w0301);
						rs1 = dao.search(sql1.toString(), list1);
						String username = "";
						String password = ""; 
						if(rs1.next()){
							username = rs1.getString("username");
							password = rs1.getString("password");
						}else {
							GenerateAcPwBo gbo = new GenerateAcPwBo(this.conn);//生成账号密码
							ArrayList userList = GenerateAcPwBo.generate(1, dao);
							HashMap map = (HashMap)userList.get(0);
							username = (String)map.get("content");
							password = (String)map.get("pasword");
						}
						
						RecordVo vo = new RecordVo("zc_expert_user");
						//账号表序号、学科组编号、会议ID 、申报人主键序号ID 、帐号、密码、帐号状态、帐号类型、专家编号、描述信息、角色
						IDFactoryBean idf = new IDFactoryBean();
						vo.setString("user_id", idf.getId("zc_expert_user.user_id", "", conn));
						if(type==2)
							vo.setString("group_id", group_id);
						vo.setString("w0301", w0301);
						vo.setString("w0501", w0501);
						vo.setString("username", username);
						vo.setString("password", password);
						vo.setInt("state", 0);
						vo.setInt("type", type);
						vo.setString("w0101", expertid);
						vo.setInt("usetype", 1);//评审帐号
						
						dao.addValueObject(vo);
						//更新模板账号
						sql.setLength(0);
						sql.append("update zc_expert_user set username=?,password=?,usetype=1 ");
						sql.append("where w0301=? and w0501='xxxxxx' and w0101=? and type=?");
						list.clear();
						list.add(username);
						list.add(password);
						list.add(w0301);
						list.add(expertid);
						list.add(type);
						if(type==2){
							sql.append(" and group_id=?");
							list.add(group_id);
						}
						
						dao.update(sql.toString(),list);
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }
    /**
     * 获取选中的人员信息
     * @author liuy
     * @param isSelectAll：表格控件是否全选
     * @param idlist：选中或反选的数据
     * @return
     * @throws GeneralException
     */
    public ArrayList<HashMap<String, String>> getSelectList(String isSelectAll, ArrayList<MorphDynaBean> idlist,UserView userview) throws GeneralException {
    	ArrayList<HashMap<String, String>> selList = new ArrayList<HashMap<String, String>>();//选中数据
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try {
	    	/** 获取选中数据 */
			TableDataConfigCache catche = (TableDataConfigCache)userview.getHm().get("reviewFile");
			String sql = "select * from ("+catche.getTableSql()+") myGridData where 1=1 ";
			if(catche.getQuerySql()!=null)
				sql += catche.getQuerySql();
			rs = dao.search(sql);
			ArrayList<HashMap<String, String>> allList = new ArrayList<HashMap<String, String>>();//页面所有数据
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("w0501", rs.getString("w0501"));
				map.put("b0110", rs.getString("b0110"));
				map.put("group_id", rs.getString("group_id"));
				map.put("w0301", rs.getString("w0301"));
				map.put("w0561", rs.getString("w0561"));
				map.put("w0555",String.valueOf(rs.getInt("w0555")));
				map.put("w0573", String.valueOf(rs.getInt("w0573")));
				allList.add(map);
			}
			if("1".equals(isSelectAll)) {//全选
				for(HashMap<String, String> value : allList){
					boolean flg = true;
					for(MorphDynaBean except : idlist){//idlist 为需排除数据
						String w0501 = (String)except.get("userid");
						w0501 = PubFunc.decrypt(w0501);
						if(w0501.equals(value.get("w0501"))){
							flg = false;
							break;
						}
					}
					if(flg){
						selList.add(value);
					}
				}
			}else {
				for(HashMap<String, String> value : allList){
					boolean flg = false;
					for(MorphDynaBean except : idlist){// idlist 为真正选中数据
						String w0501 = (String)except.get("userid");
						w0501 = PubFunc.decrypt(w0501);
						if(w0501.equals(value.get("w0501"))){
							flg = true;
							break;
						}
					}
					if(flg){
						selList.add(value);
					}
				}
			}
    	} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	return selList;
    }
    
    /**
     * 查询学校聘任组关联 的专家
     * @param w0561
     * 			上会材料中关联学校聘任组的id
     * @return
     */
    public  ArrayList<String> getW0101List(String w0561) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs  = null;
    	ArrayList<String> list = new ArrayList<String>();
    	try {
			StringBuilder sql = new StringBuilder();
			sql.append("select w0101 from zc_judgingpanel_experts where flag=1 and committee_id = "+w0561);
			rs = dao.search(sql.toString());
			while(rs.next()){
				list.add(rs.getString("w0101"));
			}
			return  list;
		} catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }

    /**
     * 获得 专家id列表
     * @param type type=2 时需要传group_id 否则传null即可
     * @param w0301
     * @return
     * @throws GeneralException
     */
	public List<String> getPersonList(int type,String w0301,String group_id) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs  = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select w0101 from zc_expert_user where w0501='xxxxxx' and type=? and w0301=?");
			List values = new ArrayList();
			values.add(type);
			values.add(w0301);
			if(type==2 && StringUtils.isNotBlank(group_id)){//学科组
				sql.append(" and group_id=?");
				values.add(group_id);
			}
			rs = dao.search(sql.toString(), values);
			while(rs.next()){
				list.add(rs.getString("w0101"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return list;
	}
	 /**
     * 生成投票帐号
     * @param type
     *  		帐号类型（哪个投票阶段的帐号）
     * @param w0501
     * 			上会材料标号
     * @param w0301
     * 			评审会议编号
     * @param accountList
     * 			帐号列表
     * @throws GeneralException
     * @author haosl
     */
    public void createVoteAccounts(int type,String w0501,String w0301,String group_id,List accountList)throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	try {
    		/** 删除除同行专家的曾经的投票帐号 */
			String dSql = "delete from zc_expert_user where w0301=? and w0501=? and "+Sql_switcher.isnull("usetype", "1")+"=2 and type=?";
			ArrayList dList = new ArrayList();
			dList.add(w0301);
			dList.add(w0501);
			dList.add(type);
			dao.delete(dSql, dList);
			ArrayList<ArrayList<String>> iList = new ArrayList<ArrayList<String>>();
			StringBuilder insertSql = new StringBuilder();
			insertSql.append("insert into zc_expert_user ");
			insertSql.append("(user_id,W0301,W0501,username,password,state,type,description,role,usetype");
			if(type==2)
				insertSql.append(",group_id");
			insertSql.append(") values ");
			insertSql.append("(?,?,?,?,?,?,?,?,?,?");
			if(type==2)
				insertSql.append(",?");
			insertSql.append(")");
			
			/** 生成新的投票帐号 **/
			for(int i=0;i<accountList.size();i++){
				HashMap  map = (HashMap)accountList.get(i);//帐号密码
				ArrayList vList = new ArrayList();
				IDFactoryBean idf = new IDFactoryBean();
				String user_id = idf.getId("zc_expert_user.user_id", "", this.conn);
				String username = (String)map.get("content");
				String password = (String)map.get("pasword");
				vList.add(user_id);
				vList.add(w0301);
				vList.add(w0501);
				vList.add(username);
				vList.add(password);
				vList.add(0);//账号状态    禁用
				vList.add(type);
				vList.add(null);//描述
				if(type==2)
					vList.add("0");//role
				else {
					vList.add(null);
				}
				vList.add(2);
				if(type==2)
					vList.add(group_id);
				iList.add(vList);
    		}
    		if(iList.size()>0)
    			dao.batchInsert(insertSql.toString(), iList);//批量生成投票帐号
		} catch (Exception e) {
			e.printStackTrace();
 			throw GeneralExceptionHandler.Handle(e);
		}
    }
    /**
     * 获得指定投票阶段的专家人数
     * @throws GeneralException 
     */
    public int getExpertNum(String w0301,int type,String group_id) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		int nums = 0;
		try {
			List values = new ArrayList();
			String sql = "";
			if(type==1){
				sql = "select w0315 as nums from w03 where w0301=?";
	    		values.add(w0301);
	    		
			}else if(type==2){
				sql = "select count(user_id) as nums from zc_expert_user where group_id=? and w0301=? and type=2 and "+Sql_switcher.isnull("usetype", "1")+" = 1 and w0501='xxxxxx'";
				values.add(group_id);
				values.add(w0301);
			}else if(type==4){
				sql = "select w0323 as nums from w03 where w0301=?";
	    		values.add(w0301);
			}
			if(StringUtils.isNotBlank(sql)){
				rs = dao.search(sql,values);
				if(rs.next())
					nums = rs.getInt("nums");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
    	return nums;
    }
}
