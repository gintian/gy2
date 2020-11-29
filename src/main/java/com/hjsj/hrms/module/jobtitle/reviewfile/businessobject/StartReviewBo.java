package com.hjsj.hrms.module.jobtitle.reviewfile.businessobject;

import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;
public class StartReviewBo {

 // 基本属性
    private Connection conn = null;
    private UserView userView = null;
    
    /**
     * 构造函数
     * @param conn
     */
    public StartReviewBo(Connection conn){
        this.conn = conn;
    }
    
    public StartReviewBo(Connection conn,UserView userView){
        this.conn = conn;
        this.userView = userView;
    }
    /**
     * 获取选中的人员信息
     * @author liuy
     * @param isSelectAll：表格控件是否全选
     * @param idlist：选中或反选的数据
     * @return
     * @throws GeneralException
     */
    public ArrayList<HashMap<String, String>> getSelectList(ArrayList<MorphDynaBean> idlist,UserView userview) throws GeneralException {
    	ArrayList<HashMap<String, String>> allList = new ArrayList<HashMap<String, String>>();//页面所有数据
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	StringBuffer sbf = new StringBuffer();
    	ArrayList<String> list = new ArrayList<String>();
    	try {
	    	/** 获取选中数据 */
    		
    		for(MorphDynaBean except : idlist){// idlist 为真正选中数据
				String w0501 = (String)except.get("userid");
				w0501 = PubFunc.decrypt(w0501);
				sbf.append(" or w0501 = ?");
				list.add(w0501);
			}
    		String sql = "select w0501,W0507,group_id,w0301,w0561,w0555,w0511,w0539,w0541 from w05 where 1=2 " + sbf;
			rs = dao.search(sql,list);

			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("w0501", rs.getString("w0501"));
				map.put("b0110", rs.getString("W0507"));
				map.put("group_id", rs.getString("group_id"));
				map.put("w0301", rs.getString("w0301"));
				map.put("w0561", rs.getString("w0561"));
				map.put("w0555", rs.getString("w0555"));
				map.put("w0511", rs.getString("w0511"));
				map.put("w0539", rs.getString("w0539")==null?"":rs.getString("w0539"));//内部评审问卷计划号
				map.put("w0541", rs.getString("w0541")==null?"":rs.getString("w0541"));//专家鉴定问卷计划号
				allList.add(map);
			}
    	} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	return allList;
    }
    
    /**
     * 启动评审，清除部分数据
     * @author liuy
     * @param selList 选择的数据的w0501,w0301,group_id
     * @param str 选中的专家类型
     * @param operateType //1：启动，2：重启
     * @throws GeneralException
     */
    public void clearData(ArrayList<HashMap<String, String>> selList ,String str, int operateType) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        StringBuffer updateStr = new StringBuffer();
        StringBuffer whereStr = new StringBuffer();
        try {
        	if(StringUtils.isNotEmpty(str) && operateType == 2){
        		int type = 0;
    			if("1".equals(str)) {//评委会
    				type = 1;
    				updateStr.append("W0519=0,W0553=0,W0549=0,W0551=0,W0559=null");
    			} else if("2".equals(str)) {//学科组
    				type = 2;
    				updateStr.append("W0547=0,W0545=0,W0543=0,W0557=null");
    			} else if("3".equals(str)) {//外部专家
    				type = 3;
					updateStr.append("W0527=0,W0529=0,W0531=0,W0533=null");
    			} else if("4".equals(str)){//学院聘任组
    				type = 4;
    				updateStr.append("W0563=0,W0565=0,W0567=0,W0569=null");
    			}
        		if (selList.size() > 0) {
        			String meetingId = "";
        			String reviewPersonId = "";
        			for (int i = 0; i < selList.size(); i++) {
        				HashMap<String, String> map = (HashMap<String, String>) selList.get(i);
        				meetingId = map.get("w0301");
        				reviewPersonId = map.get("w0501");
        				if(i==0){
        					whereStr.append(" where (W0501 = '"+ reviewPersonId +"'");
        					whereStr.append(" and W0301 = '"+ meetingId +"')");
        				}else {
        					whereStr.append(" or (W0501 = '"+ reviewPersonId +"'");
        					whereStr.append(" and W0301 = '"+ meetingId +"')");
    					}
        				
        				ReviewFileBo reviewFileBo = new ReviewFileBo(this.getConn(), new UserView("", ""));
        				reviewFileBo.updateW0525(reviewPersonId, type, "0");//更新当前启动阶段的导入标识 chent 20160824
        			}
        			dao.update("update W05 set "+updateStr.toString()+whereStr.toString());
        		}
        			
        	}
        	
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 材料审查时修改账号状态为启用,并清除投票帐号
     * @author liuy
     * @param selList 选择的数据的w0501,w0301,group_id
     * @param str 选中的专家类型
     * @throws GeneralException
     */
    public void startUsingAccount(ArrayList<HashMap<String, String>> selList ,Integer type) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        StringBuffer w05Str = new StringBuffer();
        try {
    		if (selList.size() > 0) {
    			String w0501 = "";
    			for (int i = 0; i < selList.size(); i++) {
    				HashMap<String, String> map = (HashMap<String, String>) selList.get(i);
    				w0501 = map.get("w0501");
    				if (i==0)				
    					w05Str.append("'"+ w0501 +"'");
					else 
						w05Str.append(",'"+ w0501 +"'");
    			}
    			//更新专家帐号为启用
    			dao.update("update zc_expert_user set state=1 where "+Sql_switcher.isnull("usetype", "1")+"=1 and W0501 in ("+ w05Str.toString() +") and type in ("+ type +") " +
    					"or (username in (select distinct username from zc_expert_user where W0501 in ("+ w05Str.toString() +") " +
    							"and type in ("+ type +")) and W0501='xxxxxx' and "+Sql_switcher.isnull("usetype", "1")+"=1)");
    			
    		}
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 材料评审时评审环节和评审环节标识（材料审查|投票）,清除投票结果<br />
     * stage和w0573为空时是为了清除当前阶段
     * @param selList 
     * 				选择的数据的w0501,w0301,group_id
     * @param stage 
     * 				选中的专家类型  
     * @throws GeneralException
     */
    public void updateW0555W0573(ArrayList<HashMap<String, String>> selList ,Integer type,Integer w0573) throws GeneralException {
    	ContentDAO dao = new ContentDAO(conn);
        StringBuffer w05Str = new StringBuffer();
        RowSet rs = null;
        try {
    		if (selList.size() > 0) {
    			String w0501 = "";
    			String w0301 = selList.get(0).get("w0301");	//评审会议编号
    			for (int i = 0; i < selList.size(); i++) {
    				HashMap<String, String> map = (HashMap<String, String>) selList.get(i);
    				w0501 = map.get("w0501");
    				if (i==0)				
    					w05Str.append("'"+ w0501 +"'");
					else 
						w05Str.append(",'"+ w0501 +"'");
    			}
    			String sql = "update W05 set W0573=? , W0555=? where w0301 = ? and W0501 in ("+ w05Str.toString()+")";
    			List values = new ArrayList();
    			values.add(w0573);
    			values.add(type);
    			values.add(w0301);
    			dao.update(sql, values);
    			
    			//如果是启动材料审查更新关联的所有阶段投票账号为禁用，如果是投票则更新关联所有阶段的投票账号为启用
    			int state = 0;
    			if(w0573!=null && w0573==1)
    				state=0;
    			else
    				state=1;
    			sql = "update zc_expert_user set state="+state+" where w0301 = ? and w0501 in ("+w05Str.toString()+") and "+Sql_switcher.isnull("usetype", "1")+"=2 or "+Sql_switcher.isnull("usetype", "1")+"=3";
    			values.clear();
    			values.add(w0301);
    			dao.update(sql, values);
    			if(type!=null){//为空时，只是需要清除评审环节，无需清除投票结果
	    			sql = "select distinct(username) from zc_expert_user where w0301=? and w0501 in("+ w05Str.toString()+") and type=? and "+Sql_switcher.isnull("usetype", "1")+"=2 or "+Sql_switcher.isnull("usetype", "1")+"=3";
	    			values.clear();
	    			values.add(w0301);
	    			values.add(type);
	    			rs = dao.search(sql,values);
	    			StringBuffer usernameStr = new StringBuffer();
	    			while(rs.next()){
	    				usernameStr.append("'"+rs.getString("username")+"'");
	    				usernameStr.append(", ");
	    			}
	    			
	    			//清除投票结果
	    			if(usernameStr.length()>0)
	    				clearDataEvaluation(w0301, "", type, w05Str.toString(), usernameStr.substring(0, usernameStr.length()-2));
    			}
    		}
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
			PubFunc.closeResource(rs);
        }
    }
    /**
     * 材料评审时评审环节和评审环节标识（材料审查|投票）,清除投票结果<br />
     * stage和w0573为空时是为了清除当前阶段
     * @param selList 
     * 				选择的数据的w0501,w0301,group_id
     * @param stage 
     * 				选中的专家类型  
     * @throws GeneralException
     */
    public void updateW0555W0573(ArrayList<HashMap<String, String>> selList ,Integer type,Integer w0573,String categories_id_e) throws GeneralException {
    	ContentDAO dao = new ContentDAO(conn);
        StringBuffer w05Str = new StringBuffer();
        RowSet rs = null;
        try {
    		if (selList.size() > 0) {
    			String w0501 = "";
    			String w0301 = selList.get(0).get("w0301");	//评审会议编号
    			for (int i = 0; i < selList.size(); i++) {
    				HashMap<String, String> map = (HashMap<String, String>) selList.get(i);
    				w0501 = map.get("w0501");
    				if (i==0)				
    					w05Str.append("'"+ w0501 +"'");
					else 
						w05Str.append(",'"+ w0501 +"'");
    			}
    			String sql = "update W05 set W0573=? , W0555=? where w0301 = ? and W0501 in ("+ w05Str.toString()+")";
    			List values = new ArrayList();
    			values.add(w0573);
    			values.add(type);
    			values.add(w0301);
    			dao.update(sql, values);
    			
    			//如果是启动材料审查更新关联的所有阶段投票账号为禁用，如果是投票则更新关联所有阶段的投票账号为启用
    			int state = 0;
    			if(w0573!=null && w0573==1)
    				state=0;
    			else
    				state=1;
    			sql = "update zc_expert_user set state="+state+" where w0301 = ? and w0501 in ("+w05Str.toString()+") and "+Sql_switcher.isnull("usetype", "1")+"=2 or "+Sql_switcher.isnull("usetype", "1")+"=3";
    			values.clear();
    			values.add(w0301);
    			dao.update(sql, values);
    			if(type!=null){//为空时，只是需要清除评审环节，无需清除投票结果
					String categories_id = PubFunc.decrypt(categories_id_e);
					//清除投票结果
	    			clearDataEvaluation(w0301, categories_id, type,"","");
    			}
    		}
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
			PubFunc.closeResource(rs);
        }
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
			sql.append("select w0101 from zc_judgingpanel_experts where committee_id = "+w0561);
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
     * 添加学院聘任组的评审结果信息
     * @param w0501 申报人主键
     * @param w0301 会议编号
     * @param w0101List 专家编号列表
     * @throws GeneralException 
     */
    public void createEvaluationState(String w0501,String w0301,ArrayList<String> w0101List) throws GeneralException{
    	ContentDAO dao	= new ContentDAO(this.conn);
    	RowSet rs = null;
    	try{
    		StringBuffer sql = new StringBuffer();
    		List<RecordVo> insertList = new ArrayList<RecordVo>();
    		List<RecordVo> updateList = new ArrayList<RecordVo>();
    		
    		for(int i=0;i<w0101List.size();i++){
    			String w0101 = w0101List.get(i);
    			RecordVo vo = new RecordVo("zc_data_evaluation");
    			vo.setString("w0501", w0501);
    			vo.setString("w0301", w0301);
    			sql.append("select username from zc_expert_user where w0301 = "+w0301+" and w0101="+w0101);
    			rs = dao.search(sql.toString());
    			sql.setLength(0);
    			String username ="";
    			if(rs.next()){
    				username = rs.getString("username");
    				vo.setString("username", username);
    				vo.setString("w0101", w0101);
    				vo.setString("approval_state", "");
    				vo.setString("opinion", "");
    				vo.setInt("expert_state", 1);
    				if(dao.isExistRecordVo(vo)){
    					//已有记录进行更新操作 haosl  20160820
    					updateList.add(vo);
    				}else{
    					//记录不存在则进行新增操作 haosl  20160820
    					insertList.add(vo);
    				}
    			}
    		}
    		//已有记录进行更新操作 haosl  20160820
    		if(updateList.size()>0){
    			dao.updateValueObject(updateList);
    		}
    		//记录不存在则进行新增操作 haosl  20160820
    		if(insertList.size()>0){
    			dao.addValueObject(insertList);
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
		}
    	
    }
    /**
     * 根据会议id查询专家帐号表的该会议下的专家的账号数
     * @param w0301
     * @param type
     * 			帐号类型
     * @param useType
     * 			帐号区分
     * @return
     * @throws GeneralException
     * @author haosl
     */
    public int getExperAccountNum(String w0301,int type,int useType,String group_id,String w0501)throws GeneralException{
 	   ContentDAO dao = new ContentDAO(this.conn);
 	   RowSet rs = null;
 	   try {
	 		//查询评审专家的总人数
			int experNum = 0;
			String sql = "select count(distinct username) experNum from zc_expert_user where w0301=? and type=? and w0501=?";
			ArrayList list = new ArrayList();
			list.add(w0301);
			list.add(type);
			list.add(w0501);
			if(type!=3){
				sql+= " and "+Sql_switcher.isnull("usetype", "1")+"=?";
				list.add(useType);
			}
			if(type==2 && StringUtils.isNotEmpty(group_id)){
				sql+=" and group_id=?";
				list.add(group_id);
			}
			rs = dao.search(sql, list);
			if(rs.next()){
				experNum = rs.getInt("experNum");
			}
			return experNum;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
 	 
    }
    /**
     * 查询关联的专家人数
     * @param w0301
     * @param w0501
     * @param type
     * @param useType
     * @param group_id
     * @return
     * @throws GeneralException
     */
    public int getExperNum(String w0301,String w0501,int type,int useType,String group_id)throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try {
    		//查询评审专家的总人数
    		int experNum = 0;
    		String sql = "select count(distinct username) experNum from zc_expert_user where w0501=? and w0301=? and type=?";
    		ArrayList list = new ArrayList();
    		list.add(w0501);
    		list.add(w0301);
    		list.add(type);
    		if(type!=3){
    			sql+= " and "+Sql_switcher.isnull("usetype", "1")+"=?";
    			list.add(useType);
    		}
    		if(type==2 && StringUtils.isNotEmpty(group_id)){
    			sql+=" and group_id=?";
    			list.add(group_id);
    		}
    		rs = dao.search(sql, list);
    		if(rs.next()){
    			experNum = rs.getInt("experNum");
    		}
    		return experNum;
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	
    }
    
    public Connection getConn() {
        return conn;
    }
    public void setConn(Connection conn) {
        this.conn = conn;
    }
	/**
	 * 清除评审结果
	 * @param w0301
	 * @param w05Str
	 * @param usernameStr
	 * 			in括号内拼接的串 如 username in ("+usernameStr+")"
	 * @throws GeneralException
	 */
	private void clearDataEvaluation(String w0301,String categories_id,int type,String w05Str,String usernameStr)throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		String delSql = "";
		List values = new ArrayList();
		try {
			if(StringUtils.isBlank(categories_id)) {
				delSql = "delete from zc_data_evaluation where w0301 = ? and w0501 in ("+w05Str+") and username in ("+usernameStr+") "
						+ "and w0501 in ("
						+ "	select w0501 "
						+ "	from zc_categories_relations A, zc_personnel_categories B "
						+ "	where A.categories_id=B.categories_id "
							+ "and A.w0501 in ("+w05Str+") "
							+ "and B.review_links in (select type from zc_expert_user where w0301 = ? and w0501 in ("+w05Str+") and username in ("+usernameStr+"))"
							+ "and B.approval_state<>?"
						+ ")";
				values.add(w0301);
				values.add(w0301);
				values.add("3");
			}else {
				//启动，重新启动都是启动一个分组的，zc_data_evaluation有categories_id，直接用就行，暂停状态的不将结果清楚
				delSql = "delete from zc_data_evaluation where w0301 = ? and categories_id = ?"
					+ " and w0501 in ("
					+ "	select w0501 "
					+ "	from zc_categories_relations A, zc_personnel_categories B "
					+ "	where A.categories_id=B.categories_id "
						+ " and A.categories_id = ?"
						+ " and B.approval_state<>?"
					+ ")";
				values.add(w0301);
				values.add(categories_id);
				values.add(categories_id);
				values.add("3");
			}
			
			dao.delete(delSql, values);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 获取当前阶段的考核模板
	 * @param w0301
	 * @param review_links 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 13:41 2018/5/7
	 */
	public ArrayList<String> getKh_Template_Ids(String w0301,String review_links) throws GeneralException {
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList<String> dataList=new ArrayList<String>();
		try{

			RecordVo w03Vo = new RecordVo("w03");
			w03Vo.setString("w0301", w0301);
			w03Vo = dao.findByPrimaryKey(w03Vo);
			String xmlDoc = w03Vo.getString("extend_param");
			ReviewMeetingPortalBo reviewMeetingPortalBo = new ReviewMeetingPortalBo(this.userView, this.conn);
			List<LazyDynaBean> segments = reviewMeetingPortalBo.getXmlParamByW03(xmlDoc);
			LazyDynaBean bean;
			for(int i=0;i<segments.size();i++) {
				bean = segments.get(i);
				// 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
				String flag = (String) bean.get("flag");
				if(flag.equalsIgnoreCase(review_links)){
					String str=(String)bean.get("template");
					if(StringUtils.isNotBlank(str)){
						for (String s : str.split(",")) {
							dataList.add(s);
						}
					}
					break;
				}
			}
		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return dataList;
	}

	/**
	 * 启动打分的时候将 申报人添加到kh_object考核对象表
	 * @param template_id 需要添加的模板
	 * @param categories_id 申报人分组id
	 * @param Relation_id 考核计划标识职称评审格式设置为模块ID_评审会议ID_环节ID
	 * @throws GeneralException
	 */
	public void doInsertKhObjectTable(ArrayList<String> template_id,ArrayList<String> categories_id,String Relation_id) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuffer insertSql = new StringBuffer();
			ArrayList<ArrayList<String>> iList = new ArrayList<ArrayList<String>>();
			insertSql.append("insert into kh_object ");
			insertSql.append("(Id,Relation_id,Module_id,Object_id,Objectname,B0110,E0122,E01A1,template_id,seq)");
			insertSql.append(" values ");
			insertSql.append("(?,?,?,?,?,?,?,?,?,?)");
			ArrayList<String> list = new ArrayList<String>();
			//通过usrA01查出申报人对应的记录
			StringBuffer sql=new StringBuffer( "select A0100,A0101,B0110,E0122,E01A1 from UsrA01 where A0100 in (select W0505 FROM W05 WHERE W0501 IN ");
			sql.append( "(SELECT W0501 FROM zc_categories_relations WHERE categories_id in(");
			if(categories_id.size()>0){
				for (String s : categories_id) {
					sql.append("?,");
					list.add(s);
				}
				sql.deleteCharAt(sql.length()-1);
			}else {
				return;
			}

			sql.append(") and NOT EXISTS (select 1 from kh_object where Relation_id=? and Object_id=W05.W0505 ");
			list.add(Relation_id);
			if(template_id.size()>0){
				sql.append(" and template_id in(");
				for (String s : template_id) {
					sql.append("?,");
					list.add(s);
				}
				sql.deleteCharAt(sql.length()-1);
				sql.append(")");
			}
			sql.append(" )))");

			rs = dao.search(sql.toString(),list);
			
			while(rs.next()) {
				for(int i = 0; i < template_id.size(); i++) {
					ArrayList vList = new ArrayList();
					IDFactoryBean idf = new IDFactoryBean();
					String user_id = idf.getId("kh_object.Id", "", this.conn);
					vList.add(user_id);
					vList.add(Relation_id);
					vList.add(1);//模块ID   1：职称评审
					vList.add(rs.getString("A0100"));
					vList.add(rs.getString("A0101"));
					vList.add(rs.getString("B0110"));
					vList.add(rs.getString("E0122"));
					vList.add(rs.getString("E01A1"));
					vList.add(template_id.get(i));
					vList.add(user_id);//排序和主键应该一样
					iList.add(vList);
				}
			}
			if(iList.size()>0)
    			dao.batchInsert(insertSql.toString(), iList);//批量生成考核对象
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
    		PubFunc.closeDbObj(rs);
    	}
	}

	/**
	 * 清除多余的考核主体
	 * @param Relation_id
	 * @param w0301 会议id
	 * @param type 阶段id
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 13:33 2018/5/22
	 */
	public void delUnnecessaryMainbody(String Relation_id,String w0301,String type) throws GeneralException {
		RowSet rs=null;
		try{
			ArrayList<String> mainBodyKey=new ArrayList<String>();
			ContentDAO dao=new ContentDAO(this.getConn());
			StringBuffer strSql=new StringBuffer();
			ArrayList dataList=new ArrayList();
			strSql.append("select id from kh_mainbody where Relation_id=? and mainbody_id not in (");
			strSql.append(" select username from zc_expert_user where W0301=? and type=? )");
			dataList.add(Relation_id);
			dataList.add(w0301);
			dataList.add(type);
			rs=dao.search(strSql.toString(),dataList);
			while (rs.next()){
				mainBodyKey.add(rs.getString("id"));
			}
			if(mainBodyKey.size()==0) {
				return;
			}
			strSql.setLength(0);
			strSql.append("delete from kh_mainbody where id in(");
			for (int i = 0; i < mainBodyKey.size(); i++) {
				strSql.append("?,");
			}
			strSql.deleteCharAt(strSql.length()-1);
			strSql.append(")");
			dao.update(strSql.toString(),mainBodyKey);//删除主体表
			strSql.setLength(0);
			strSql.append("delete from kh_detail where Kh_mainbody_id in(");
			for (int i = 0; i < mainBodyKey.size(); i++) {
				strSql.append("?,");
			}
			strSql.deleteCharAt(strSql.length()-1);
			strSql.append(")");
			dao.update(strSql.toString(),mainBodyKey);//删除打分表

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}



	/**
	 * 根据申报人即kh_object中信息 同步kh_mainbody考核主体表对应关系
	 * @param categories_id 评审人分组id
	 * @param Relation_id 考核计划标识职称评审格式设置为模块ID_评审会议ID_环节ID
	 * @throws GeneralException
	 */
	public void doInsertKhMainbodyTable(ArrayList<String> categories_id, String Relation_id) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuffer insertSql = new StringBuffer();
			ArrayList<ArrayList<String>> iList = new ArrayList<ArrayList<String>>();
			insertSql.append("insert into kh_mainbody ");
			insertSql.append("(Id,B0110,E0122,E01A1,Relation_id,Module_id,Mainbody_id,Mainbody_name,template_id,kh_object_id,Status)");
			insertSql.append(" values ");
			insertSql.append("(?,?,?,?,?,?,?,?,?,?,?)");
			ArrayList<String> list = new ArrayList<String>();
			StringBuffer strSql = new StringBuffer();
			strSql.append(" SELECT t2.username,t2.id AS object_Key,t2.template_id FROM ( ");
			strSql.append(" SELECT userNameTable.username,ko.id,ko.Object_id,ko.template_id FROM (");
			strSql.append(" select * from (select username,categories_id from zc_expert_user  where categories_id in( ");
			if(categories_id.size()>0){
				for (String s : categories_id) {
					strSql.append("?,");
					list.add(s);
				}
				strSql.deleteCharAt(strSql.length()-1);
			}else {
				return;
			}
			strSql.append(" ) GROUP BY username,categories_id ) t ");
			strSql.append(" ) userNameTable ");
			strSql.append(" ,(SELECT id,Object_id,template_id,categories_id FROM kh_object INNER JOIN   ");
			strSql.append( " (select W05.W0505,zc.categories_id FROM W05 INNER JOIN zc_categories_relations zc ON W05.W0501=zc.W0501 AND zc.categories_id IN ( ");
			for (String s : categories_id) {
				strSql.append("?,");
				list.add(s);
			}
			strSql.deleteCharAt(strSql.length()-1);
			strSql.append(")) w on w.W0505=kh_object.Object_id and Relation_id=? ) ko where ko.categories_id=userNameTable.categories_id");
			list.add(Relation_id);
			strSql.append(") t2 ");
			strSql.append("WHERE NOT EXISTS (SELECT 1 FROM kh_mainbody km WHERE t2.username=km.Mainbody_id AND t2.id=km.kh_object_id)");
			rs = dao.search(strSql.toString(), list);
			while (rs.next()) {
				ArrayList vList = new ArrayList();
				IDFactoryBean idf = new IDFactoryBean();
				String user_id = idf.getId("kh_mainbody.Id", "", this.conn);
				vList.add(user_id);
				vList.add("");
				vList.add("");
				vList.add("");
				vList.add(Relation_id);
				vList.add(1);//模块ID   1：职称评审
				vList.add(rs.getString("username"));//考核主体对象ID为账号
				vList.add("");
				vList.add(rs.getString("template_id"));
				vList.add(rs.getString("object_Key"));
				vList.add(0);//0:未打分1:正在编辑2:已提交
				iList.add(vList);
			}
			if (iList.size() > 0)
				dao.batchInsert(insertSql.toString(), iList);//批量生成考核主体
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}

	/**
	 * 清除打分信息
	 * @param object_KeyList 需要删除的申报人id
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 13:54 2018/5/5
	 */
	private void cleanKHTable(ArrayList<String> object_KeyList,boolean isCleanScore) throws GeneralException {
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			if(object_KeyList.size()==0)
				return;
			StringBuffer strSql=new StringBuffer();
			strSql.append("delete from kh_mainbody where kh_object_id in(");
			for(String str:object_KeyList){
				strSql.append("?,");
			}
			strSql.deleteCharAt(strSql.length()-1);
			strSql.append(")");
			dao.update(strSql.toString(),object_KeyList);//删除主体表
			if(isCleanScore) {
				dao.update(strSql.toString().replace("kh_mainbody", "kh_detail"), object_KeyList);//删除打分表
				dao.update(strSql.toString().replace("kh_mainbody", "kh_detail_archive"), object_KeyList);//删除归档表
				dao.update(strSql.toString().replace("kh_mainbody","kh_object").replace("kh_object_id","id"),object_KeyList);//删除对象表
			}
		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 清除打分信息
	 * @param w0301 会议id1
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 13:54 2018/5/5
	 */
	public void cleanAllKHTableByW0301(String w0301,boolean isCleanScore) throws GeneralException {
		RowSet rs=null;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strSql=new StringBuffer("select id From kh_object where Relation_id like ?");
			rs=dao.search(strSql.toString(),Arrays.asList (new String[]{"1_"+w0301+"_%"}));
			ArrayList<String> list=new ArrayList<String>();
			while (rs.next()){
				list.add(rs.getString("id"));
			}
			if(list.size()>0)
				this.cleanKHTable(list,isCleanScore);
		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}

	/**
	 * 通过a0100删除对象和该对象全部打分数据
	 * @param a0100List
	 * @param w0301 会议id
	 * @param review_links 阶段id
	 * @author ZhangHua
	 * @date 17:45 2018/5/19
	 * @throws GeneralException
	 */
	public void cleanKh_ObjectByA0100(ArrayList<String>a0100List,String w0301,String review_links) throws GeneralException {
		RowSet rs=null;
		try{
			if(a0100List.size() > 0) {
				ContentDAO dao=new ContentDAO(this.conn);
				ArrayList dataList=new ArrayList();
				StringBuffer strSql=new StringBuffer("select id From kh_object where Relation_id=? and object_id in(");
				dataList.add("1_"+w0301+"_"+review_links);
				for (String str : a0100List) {
					strSql.append("?,");
					dataList.add(str);
				}
	
				strSql.deleteCharAt(strSql.length()-1);
				strSql.append(")");
				rs=dao.search(strSql.toString(),dataList);
				dataList.clear();
				while (rs.next()){
					dataList.add(rs.getString("id"));
				}
				this.cleanKHTable(dataList,true);
			}

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}

	/**
	 * 获取所有考核对象id
	 * @param dao
	 * @param Relation_id 考核计划标识，模块ID_评审会议ID_环节ID 职称评审模块id为1
     * @param template_Ids 模板id
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 13:54 2018/5/5
	 */
	private ArrayList<String> getKHObject_Id(ContentDAO dao,String Relation_id,ArrayList<String> template_Ids)throws GeneralException {
		ArrayList<String> object_KeyList=new ArrayList<String>();
		RowSet rs=null;
		try{
			StringBuffer strSql=new StringBuffer();
			ArrayList dataList=new ArrayList();
			dataList.add(Relation_id);
			strSql.append("SELECT id FROM kh_object ");
			strSql.append("WHERE Relation_id=? ");
			if(template_Ids.size()>0){
				strSql.append(" and template_id in(");
				for (String template_id : template_Ids) {
					strSql.append("?,");
					dataList.add(template_id);
				}
				strSql.deleteCharAt(strSql.length()-1);
				strSql.append(")");
			}

			rs=dao.search(strSql.toString(),dataList);
			while (rs.next()){
				object_KeyList.add(rs.getString("id"));
			}

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return object_KeyList;
	}


    /**
     * 获取考核对象id 如果不传object_Ids 则获取当前分组下所有人
     * @param w0301
     * @param review_links
     * @param object_Ids
     * @return
     * @throws GeneralException
	 * @author ZhangHua
	 * @date 16:25 2018/5/7
     */
	private ArrayList<String> getKHObject_Id(ContentDAO dao,String w0301,String review_links,ArrayList<String> object_Ids,String categories_id)throws GeneralException {
		ArrayList<String> object_KeyList=new ArrayList<String>();
		RowSet rs=null;
		try{
            String Relation_id = "1_" + w0301 +"_"+ review_links;//考核计划标识职称评审格式设置为模块ID_评审会议ID_环节ID
			StringBuffer strSql=new StringBuffer();
			ArrayList dataList=new ArrayList();

			strSql.append("SELECT id FROM kh_object ");
			strSql.append("WHERE Relation_id=? ");
            dataList.add(Relation_id);
            strSql.append(" and Object_id in(");
			if(object_Ids!=null&&object_Ids.size()>0){
				for (String object_Id : object_Ids) {
					strSql.append("?,");
					dataList.add(object_Id);
				}
				strSql.deleteCharAt(strSql.length()-1);
			}else{
                strSql.append(" select W0505 FROM W05 WHERE W0501 IN (SELECT W0501 FROM zc_categories_relations WHERE categories_id =?)");
                dataList.add(categories_id);
            }
            strSql.append(")");
			rs=dao.search(strSql.toString(),dataList);
			while (rs.next()){
				object_KeyList.add(rs.getString("id"));
			}

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return object_KeyList;
	}


	/**
	 * 添加或删除打分模板时同步打分信息
	 * @param w0301 会议id
	 * @param review_links 阶段id
	 * @param newTemplate_Id 当前模板
	 * @param oldTemplate_Id 修改前的模板
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 16:25 2018/5/7
	 */
	public void SynchronizeKH_Table(String w0301,String review_links,ArrayList<String> newTemplate_Id,ArrayList<String> oldTemplate_Id) throws GeneralException {
		RowSet rs=null;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String Relation_id = "1_" + w0301 +"_"+ review_links;//考核计划标识职称评审格式设置为模块ID_评审会议ID_环节ID
			ArrayList<String> addTemplate_Id=(ArrayList<String>)newTemplate_Id.clone();
			addTemplate_Id.removeAll(oldTemplate_Id);
			ArrayList<String> removeTemplate_Id= (ArrayList<String>) oldTemplate_Id.clone();
			removeTemplate_Id.removeAll(newTemplate_Id);

			if(addTemplate_Id.size()>0) {//新增模板
				StringBuffer strSql=new StringBuffer();
				strSql.append("select categories_id from zc_personnel_categories where W0301=? and  Review_links=? "); //获取全部分组
				ArrayList list=new ArrayList();
				list.add(w0301);
				list.add(review_links);
				ArrayList<String> categories_id=new ArrayList<String>();
				rs=dao.search(strSql.toString(),list);
				while (rs.next()){
					categories_id.add(rs.getString("categories_id"));
				}
				this.doInsertKhObjectTable(addTemplate_Id, categories_id, Relation_id);
				this.doInsertKhMainbodyTable(categories_id, Relation_id);
			}
			if(removeTemplate_Id.size()>0){//删除模板
				ArrayList<String> removeObject_Id=this.getKHObject_Id(dao,Relation_id,removeTemplate_Id);
				this.cleanKHTable(removeObject_Id,true);
			}

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}


    /**
     * 重新启动时 重置打分状态，但是不清除分数 object_Ids为空 则重置当前分组全部人员
     * @param w0301
     * @param review_links
     * @param categories_id
     * @param object_Ids
     * @throws GeneralException
     * @author ZhangHua
     * @date 16:11 2018/5/7
     */
	public void reStartScore(String w0301,String review_links,String categories_id,ArrayList object_Ids) throws GeneralException {
	    try{
	        ContentDAO dao=new ContentDAO(this.conn);
            String Relation_id = "1_" + w0301 +"_"+ review_links;//考核计划标识职称评审格式设置为模块ID_评审会议ID_环节ID
	        ArrayList<String> reStartobject_ids=this.getKHObject_Id(dao,w0301,review_links,object_Ids,categories_id);
	        if(reStartobject_ids.size()==0)
	            return;
	        //更新提交状态的为已编辑状态，都是重启的时候
	        StringBuffer strSql=new StringBuffer();
	        strSql.append("update kh_mainbody set Status=1 where kh_object_id in(");
            ArrayList<String> list=new ArrayList<String>();
	        for (String reStartobject_id : reStartobject_ids) {
                strSql.append("?,");
                list.add(reStartobject_id);
            }
            strSql.deleteCharAt(strSql.length()-1);
            strSql.append(")");
            strSql.append(" and Status=2 and Relation_id=? ");
            list.add(Relation_id);

            dao.update(strSql.toString(),list);
            
            // 将kh_object中的分数置为null，否则重启导出时，显示为0而不是空，这里对应的就是其实就是w05表中的分值
            strSql.setLength(0);
			list.clear();
            strSql.append("update kh_object set score=null where id in(");
	        for (String reStartobject_id : reStartobject_ids) {
                strSql.append("?,");
                list.add(reStartobject_id);
            }
            strSql.deleteCharAt(strSql.length()-1);
            strSql.append(")");
            strSql.append(" and Relation_id=? ");
            list.add(Relation_id);

            dao.update(strSql.toString(),list);

            //清除w05里的数据，都是重启的时候
            ArrayList<String> template_Ids=this.getKh_Template_Ids(w0301,review_links);
			strSql.setLength(0);
			list.clear();
			strSql.append("update w05 set ");
			for (String template_id : template_Ids) {
				strSql.append(" C_"+template_id+"=null,");
				strSql.append(" C_"+template_id+"_seq=null,");
			}
			strSql.deleteCharAt(strSql.length()-1);
			strSql.append(" where w05.W0505 in (select Object_id from kh_object where Id in(");
			for (String str : reStartobject_ids) {
				strSql.append("?,");
				list.add(str);
			}

			strSql.deleteCharAt(strSql.length() - 1);
			strSql.append(")) and W0301=? and W0555=?");
			list.add(w0301);
			list.add(review_links);
			dao.update(strSql.toString(),list);

	    }catch (Exception e){
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
    }
	
	/**
	 * 重启的时候删除对应的调查问卷
	 * @param selList
	 * @param w0301
	 * @param categories_id
	 * @param review_links
	 * @throws GeneralException
	 */
	public void clearQndata(ArrayList<HashMap<String, String>> selList , String w0301, String categories_id, String review_links) throws GeneralException {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer usbObjects = new StringBuffer();
		Set<String> painIds = new HashSet<String>();
		try {
			list.add(w0301);
			list.add(categories_id);
			for(int i = 0; i < selList.size(); i++) {
				HashMap<String, String> map = (HashMap<String, String>) selList.get(i);
				String w0539 = map.get("w0539");
				String w0541 = map.get("w0541");
				String w0501 = map.get("w0501");
				if(StringUtils.isNotBlank(w0539)) {
					painIds.add(w0539);
				}
				if(StringUtils.isNotBlank(w0541)) {
					painIds.add(w0541);
				}
				usbObjects.append(" or subObject=?");
				list.add(w0501 + "_" + review_links);
				//因为根据subObject进行拼接，每20个人进行删除
				if(i > 0 && i % 20 == 0) {
					deleteQnId(painIds, list, usbObjects.substring(3), rs, dao);
					list = new ArrayList<String>();
					list.add(w0301);
					list.add(categories_id);
					usbObjects = new StringBuffer();
					painIds = new HashSet<String>();
				}
			}
			if(painIds.size() > 0) {
				deleteQnId(painIds, list, usbObjects.substring(3), rs, dao);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
	}
	
	/**
	 * 删除调查问卷数据表中的数据
	 * @param painIds
	 * @param subObject 对象
	 * @param rs
	 * @param dao
	 * @param list
	 */
	private void deleteQnId(Set<String> painIds, ArrayList<String> list, String subObejcts, RowSet rs, ContentDAO dao) {
		try {
			ArrayList<String> list_copy = (ArrayList<String>) list.clone();
			Iterator<String> it = painIds.iterator();
			DbWizard dbw=new DbWizard(this.conn);
			while (it.hasNext()) {
				String planid = it.next();

				String sql = "select qnId from qn_plan where status=? and planId=?";
				rs = dao.search(sql, Arrays.asList(new String[] {"1", planid}));
				if (rs.next()) {
					int qnId = rs.getInt("qnId");
					if (qnId > 0) {
						String qn_qnid_data = "qn_" + qnId + "_data";
						String qn_matrix_qnid_data = "qn_matrix_" + qnId + "_data";
						String where_sql = " where planId=? and mainObject in (select username from zc_expert_user where W0301= ? and categories_id = ?) and (" + subObejcts + ")";
						list_copy.add(0, planid);
						if(dbw.isExistTable(qn_qnid_data,false)) {
							dao.delete("delete from " + qn_qnid_data + where_sql, list_copy);
						}
						if(dbw.isExistTable(qn_matrix_qnid_data,false)) {
							dao.delete("delete from " + qn_matrix_qnid_data + where_sql, list_copy);
						}
						list_copy = (ArrayList<String>) list.clone();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//
//	private HashMap<String ,ArrayList<String>>  getNeedAddObjectId(ContentDAO dao, String categories_id,String Relation_id) throws GeneralException {
//		RowSet rs=null;
//		HashMap<String ,ArrayList<String>>  objectIdMap=new HashMap<String ,ArrayList<String>>();
//		try{
//
//			StringBuffer strSql=new StringBuffer();
//			strSql.append("SELECT tab.Mainbody_id,tab.Object_id FROM (");
//			strSql.append("SELECT allobject.Mainbody_id,allobject.Object_id,nowobject.Object_id AS nowObject_Id FROM (");
//			strSql.append("SELECT t1.Mainbody_id,t3.W0505 AS Object_id FROM ");
//			strSql.append("(SELECT Mainbody_id FROM kh_mainbody WHERE Relation_id=? GROUP BY Mainbody_id) t1");
//			strSql.append("INNER JOIN (");
//			strSql.append("SELECT w05_o.W0505 from (select W0505 FROM W05 where W0501 IN (SELECT W0501 FROM zc_categories_relations WHERE categories_id =?)) w05_o ");
//			strSql.append(")t3 ON 1=1 ");
//			strSql.append(") allobject ");
//			strSql.append("LEFT JOIN (");
//			strSql.append("SELECT kh_mainbody.Mainbody_id AS Mainbody_id,kh_object.Object_id FROM kh_mainbody ");
//			strSql.append("INNER JOIN kh_object ON kh_object.Relation_id = kh_mainbody.Relation_id AND kh_object.template_id = kh_mainbody.template_id AND kh_object.Relation_id=? GROUP BY kh_mainbody.template_id,kh_mainbody.Mainbody_id,kh_object.Object_id");
//			strSql.append(") nowobject ON nowobject.Mainbody_id = allobject.Mainbody_id AND nowobject.Object_id = allobject.Object_id");
//			strSql.append(") tab WHERE tab.nowObject_Id IS NULL order by tab.Mainbody_id");
//			ArrayList dataList=new ArrayList();
//			dataList.add(Relation_id);
//			dataList.add(categories_id);
//			dataList.add(Relation_id);
//			rs=dao.search(strSql.toString(),dataList);
//
//			String Mainbody_id="";
//			ArrayList list=new ArrayList();
//			while (rs.next()){
//				if (!Mainbody_id.equalsIgnoreCase(rs.getString("Mainbody_id"))){
//					if(StringUtils.isNotBlank(Mainbody_id))
//						objectIdMap.put(Mainbody_id,list);
//					Mainbody_id=rs.getString("Mainbody_id");
//					list.add(rs.getString("Object_id"));
//				}else {
//					list.add(rs.getString("Object_id"));
//				}
//			}
//		}catch (Exception e){
//		    e.printStackTrace();
//		    throw GeneralExceptionHandler.Handle(e);
//		}
//		return objectIdMap;
//	}
}
