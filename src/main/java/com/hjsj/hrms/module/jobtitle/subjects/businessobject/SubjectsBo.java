package com.hjsj.hrms.module.jobtitle.subjects.businessobject;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;
import java.util.Map.Entry;

/**
 * 资格评审_学科组
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 */
public class SubjectsBo {
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public SubjectsBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
	
	
	/**
	 * 显示权限范围内的学科组
	 * @param b0110：所属单位
	 * @param isHistory：是否显示历史  1：是 0：否
	 * @param year 创建年
	 * @return 学科组信息
	 */
	public ArrayList<HashMap<String, String>> getSubjects(String b0110, String isHistory, String year) throws GeneralException {
		
		ArrayList<HashMap<String, String>> subjectsList = new ArrayList<HashMap<String, String>>();
		
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
    		StringBuilder sql = new StringBuilder();
    		
    		sql.append("select group_id,group_name,b0110,create_time,history,state ");
    		sql.append("From zc_subjectgroup ");
    		sql.append("where 1=1 ");
    		
    		if(b0110.split("`")[0].length() > 2){//组织机构不为空：取本级，下级。为空：最高权限
    			String whereSql = new JobtitleUtil(this.conn, this.userview).getB0110Sql_down(b0110);
    			sql.append(whereSql);
    		}
    		if("0".equals(isHistory)){
    			sql.append("and state=1 ");
    		}
    		if(StringUtils.isNotEmpty(year))
    			sql.append("and "+Sql_switcher.year("create_time")+"="+year);
    		sql.append(" order by group_id DESC");
			rs = dao.search(sql.toString());
			
			while (rs.next()) {
				String group_id = rs.getString("group_id");
				String group_name = rs.getString("group_name");
				String history = String.valueOf(rs.getInt("history"));
				String state = String.valueOf(rs.getInt("state"));
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("group_id", PubFunc.encrypt(group_id));
				map.put("group_name", group_name);
				map.put("ishistory", history);
				map.put("state", state);
				String unitName = "";
				if(!StringUtils.isEmpty(AdminCode.getCodeName("UN", rs.getString("b0110"))))
					unitName = AdminCode.getCodeName("UN", rs.getString("b0110"));
				else 
					unitName = AdminCode.getCodeName("UM", rs.getString("b0110"));
				map.put("b0110", unitName);
				String create_time = "";
				if(rs.getDate("create_time")!=null){
					Date date = rs.getDate("create_time");					
					create_time =  date.toString().substring(0, 4);
				}
				map.put("create_time", create_time);
				subjectsList.add(map);
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
	 * 得到学科组历史年
	 * @param b0110     所属单位
	 * @param isHistory 是否显示历史  1：是 0：否
	 * @return
	 * @throws GeneralException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getSubjectsHistoryYear(String b0110, String isHistory) throws GeneralException {
		ArrayList yearlist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try {
    		StringBuilder sql = new StringBuilder();
    		sql.append("select ");
    		sql.append(" distinct "+Sql_switcher.year("create_time") + " create_time");
    		sql.append(" from zc_subjectgroup ");
    		sql.append(" where 1=1 ");
    		
    		if(b0110.split("`")[0].length() > 2){//组织机构不为空：取本级，下级。为空：最高权限
    			String whereSql = new JobtitleUtil(this.conn, this.userview).getB0110Sql_down(b0110);
    			sql.append(whereSql);
    		}
    		sql.append("order by create_time DESC");
			rs = dao.search(sql.toString());
			
			while (rs.next()) {
				HashMap map = new HashMap();
				String create_time =  rs.getString("create_time");
				map.put("value", create_time);
				map.put("name", create_time + "年");
				yearlist.add(map);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return yearlist;
	}
	/**
	 * 获取学科组人员信息
	 * @param group_id学科组编号
	 * @return ArrayList人员信息
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getSubjectsPerson(String group_id) throws GeneralException {
		
		ArrayList<HashMap<String, String>> personList = new ArrayList<HashMap<String, String>>();
		
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	RowSet dbRs = null;
    	RowSet infoRs = null;
    	
    	try {
    		//获取历史记录标识
    		String history = "0";
    		StringBuilder sql = new StringBuilder();
    		sql.append("select history  ");
    		sql.append("From  zc_subjectgroup ");
    		sql.append("where ");
    		sql.append("group_id=? ");
    		ArrayList<String> list = new ArrayList<String>();
    		list.add(group_id);
			rs = dao.search(sql.toString(), list);
			while (rs.next()) {
				history = rs.getString("history");
			}
			
			
			//获取学科组人员信息
			sql.setLength(0);
			list.clear();
			String w0101 = "";//专家id
			String w0107 = "";//专家姓名
			String w0103 = "";//单位名称
			String w0105 = "";//部门
			String b0110 = "";//所属单位
			String flag = "";//聘任标识
			String role = "";//专家角色
			String imgDbName = "";//人员库(图片用)
			String imgA0100 = "";//人员编号(图片用)
			String imgQuality = "l";//图片质量(低)
			String fileid = "";//人员照片的文件id
    		sql.append("select W.W0101,W.W0107,W.GUIDKEY,W.W0111,W.W0103,W.W0105,W.b0110,S.flag,S.role,W.FILEID fileid ");
    		sql.append("from zc_subjectgroup_experts S ");
    		sql.append("INNER JOIN W01 W ");
    		sql.append("on w.W0101=S.expertid ");
    		sql.append("where group_id=? ");
    		if("0".equals(history)){
    			sql.append("and S.flag='1' ");
    		}
    		sql.append(" order by S.role desc");
    		list.add(group_id);
    		
			rs = dao.search(sql.toString(), list);
			while (rs.next()) {
				w0101 = rs.getString("W0101");
				w0107 = rs.getString("W0107");
				w0103 = rs.getString("W0103");
				w0105 = rs.getString("W0105");
				b0110 = rs.getString("b0110");
				flag = rs.getString("flag");
				role = rs.getString("role");
				String W0111 = rs.getString("W0111");
				String guidkey = rs.getString("GUIDKEY");
				fileid = rs.getString("fileid");
				//xus 20/5/18 vfs改造 职称评审人员图片从vfs中获取
				if("2".equals(W0111)) {//内部专家
					DbNameBo dbNameBo = new DbNameBo(this.conn, this.userview);
					ArrayList dbList = dbNameBo.getAllDbNameVoList();
					for(int i=0; i<dbList.size(); i++){
						RecordVo vo = (RecordVo)dbList.get(i);
						String pre = vo.getString("pre");
						StringBuilder dbSql = new StringBuilder("select "+pre+"a00.a0100,fileid from "+pre+"a01,"+pre+"a00 where "+pre+"a01.a0100 = "+pre+"a00.a0100 and GUIDKEY='"+guidkey+"' and flag = 'P'");
						dbRs = dao.search(dbSql.toString());
						while(dbRs.next()){
							fileid = dbRs.getString("fileid");
							imgA0100 = dbRs.getString("A0100");
							break;
						}
					}
				}
				
//				if("2".equals(W0111)){//内部专家
//					DbNameBo dbNameBo = new DbNameBo(this.conn, this.userview);
//					ArrayList dbList = dbNameBo.getAllDbNameVoList();
//					for(int i=0; i<dbList.size(); i++){
//						RecordVo vo = (RecordVo)dbList.get(i);
//						String pre = vo.getString("pre");
//						StringBuilder dbSql = new StringBuilder("select A0100 from "+pre+"A01 where GUIDKEY='"+guidkey+"'");
//						dbRs = dao.search(dbSql.toString());
//						while(dbRs.next()){
//							imgDbName = pre;
//							imgA0100 = dbRs.getString("A0100");
//							break;
//						}
//					}
//				}
				
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("w0101", PubFunc.encrypt(w0101));
				map.put("w0107", w0107);
				map.put("w0103", w0103);
				map.put("w0105", w0105);
				String un = AdminCode.getCodeName("UN", b0110);
				String um = AdminCode.getCodeName("UM", b0110);
				String b0110Name = StringUtils.isEmpty(un) ? um : un;
				map.put("b0110", b0110Name);
				map.put("flag", flag);
				map.put("role", role);
//				map.put("imgUsr",  PubFunc.encrypt(imgDbName));
				map.put("fileid", fileid);
				map.put("imgA0100", PubFunc.encrypt(imgA0100));
				map.put("imgQuality", PubFunc.encrypt(imgQuality));
				map.put("w0111", W0111);
				/**获得专家图片保存跟路径**/
//	    		ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
//	            String fileRootPath = constantXml.getNodeAttributeValue("/filepath", "rootpath");
//	            fileRootPath = fileRootPath.replace("\\", "/");
//	            String truthpath = fileRootPath+"/multimedia/jobtitle/qualifications/expert_photo/";
//	            String imagename = "";
//				//通过专家编号查找专家对应的图片
//		        File f = new File(truthpath);
//		        if(f.isDirectory()){
//			        if(f.exists()){
//			        	File s[] = f.listFiles();
//			        	for(int i=0;i<s.length;i++) {
//			        		String name = s[i].getName();
//			        		int index = name.indexOf(".");
//			        		String fnameString = name.substring(0,index);
//			        		if(fnameString.equals(w0101)){
//			        			imagename = name;
//			        		}
//			        	}
//			        }
//		        }
//		        map.put("filePath", truthpath+imagename);
		        ArrayList infoList = getPersonInfoList();
		        String sel_str = getSel_str(infoList);
		        sel_str = sel_str.substring(0,sel_str.length()-1);
		        infoRs = dao.search("select "+sel_str+" from W01 where GUIDKEY='"+guidkey+"'");
		        if(infoRs.next()){
		        	StringBuffer infoBuf = new StringBuffer();
		        	for(int i=0;i<infoList.size();i++){
		            	HashMap hmap = (HashMap)infoList.get(i);
		            	Iterator iter = hmap.entrySet().iterator();
		            	if(iter.hasNext()) {
		            		Entry entry = (Entry) iter.next();
		            		String key = (String)entry.getKey();
		            		String infoValue = infoRs.getString(key);
		            		infoValue = infoValue==null?"":infoValue;
		            		FieldItem item = DataDictionary.getFieldItem(key);
		            		if(!"0".equals(item.getCodesetid())){
		            			infoValue = AdminCode.getCodeName(item.getCodesetid(), infoValue);
		            			if("UN".equalsIgnoreCase(item.getCodesetid())){
		            				infoValue = b0110Name;
		            			}
		            		}
		            		if("D".equals(item.getItemtype())){
		            			if(StringUtils.isNotEmpty(infoValue)){
		            				infoValue = infoValue.substring(0, 10);
		            			}
		            		}
		            		String value = (String)entry.getValue();
		            		infoBuf.append(value+"  :  "+infoValue+"\n");
		            	}
		            }
		        	map.put("info", infoBuf.toString());
		        }
				personList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(dbRs);
			PubFunc.closeDbObj(infoRs);
		}
		
		return personList;
	}
	
	/**
	 * 得到专家库显示的列
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList getPersonInfoList(){
		ArrayList personInfoList = new ArrayList();
		ExpertsBo bo = new ExpertsBo(this.conn,this.userview);
        ArrayList<ColumnsInfo> columnList = bo.getColumnList();
        for(int i=0;i<columnList.size(); i++){
        	ColumnsInfo columnsInfo = columnList.get(i);
        	if(columnsInfo.getLoadtype()==1){
        		HashMap map = new HashMap();
        		map.put(columnsInfo.getColumnId(), columnsInfo.getColumnDesc());
        		personInfoList.add(map);
        	}
        }
		return personInfoList;
	}
	
	/**
	 * 得到W01需要查询的列
	 * @param infoList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getSel_str(ArrayList infoList){
		String sel_str = "";
		for(int i=0;i<infoList.size();i++){
        	HashMap hmap = (HashMap)infoList.get(i);
        	Iterator iter = hmap.entrySet().iterator();
        	if(iter.hasNext()) {
        		Entry entry = (Entry) iter.next();
        		String key = (String)entry.getKey();
        		sel_str += key + ",";
        	}
        }
		return sel_str;
	}
	
	/**
	 * 新增学科组
	 * @param subjectsName:学科组名称
	 * @return msg
	 * @throws GeneralException
	 */
	public String createSubjects(LazyDynaBean bean) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			IDFactoryBean idf = new IDFactoryBean();
			RecordVo vo = new RecordVo("zc_subjectgroup");
			//学科组编号、学科组名称、所属单位、创建时间、创建者用户名、创建者姓名、修改时间、历史记录标识、有效标识
			vo.setString("group_id", idf.getId("zc_subjectgroup.group_id", "", conn));
			vo.setString("group_name",(String)bean.get("subjectsName"));
			//兼容卡片页面没有弹出框填写学科组信息的情况
			if(bean.get("b0110")!=null)
				vo.setString("b0110",(String)bean.get("b0110"));
			else
				vo.setString("b0110", this.userview.getUnitIdByBusi("9").split("`")[0].substring(2));
			//兼容卡片页面没有弹出框填写学科组信息的情况
			if(bean.get("create_time")!=null){
				vo.setDate("create_time",(String)bean.get("create_time"));
				vo.setDate("modify_time",(String)bean.get("create_time"));
			}else{
				vo.setDate("create_time",new java.sql.Date(new Date().getTime()));
				vo.setDate("modify_time",new java.sql.Date(new Date().getTime()));
			}
			//描述信息
			if(bean.get("description")!=null)
				vo.setString("description",(String)bean.get("description"));
			//兼容卡片页面没有弹出框填写学科组信息的情况
			if(bean.get("create_fullname")!=null)
				vo.setString("create_fullname",(String)bean.get("create_fullname"));
			else
				vo.setString("create_fullname", StringUtils.isEmpty(this.userview.getUserFullName()) ? this.userview.getUserName() : this.userview.getUserFullName());
			vo.setString("create_user", this.userview.getUserName());
			vo.setInt("history", 0);
			vo.setInt("state", 1);
			dao.addValueObject(vo);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
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
			if(list.size() > 0){
				sql.append("set ");
			}
			for(int i=0; i<list.size(); i++){
				sql.append(list.get(i).get("key")+"=? ");
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
	 * @param group_id：学科组编号
	 * @return msg
	 * @throws GeneralException
	 */
	public String deleteSubjects(String group_id) throws GeneralException{
			
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		
		try {
			StringBuilder sql = new StringBuilder();
			ArrayList<String> list = new ArrayList<String>();
			
			// 查看该学科组是否关联会议
			sql.append("select COUNT(group_id) as count ");
			sql.append("From zc_expert_user ");
			sql.append("where group_id=? ");
			list.add(group_id);
			rs = dao.search(sql.toString(), list);
			while(rs.next()){
				if(rs.getInt("count") > 0){//存在关联
					msg = "该学科组已关联会议，不能删除！";
					return msg;
				} else {
					sql.setLength(0);
					list.clear();
					sql.append("update zc_subjectgroup ");
					sql.append("set state=0 ");
		    		sql.append("where group_id=? ");
		    		list.add(group_id);
					dao.update(sql.toString(), list);
					//msg = "已把该学科组置为不启用状态";
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
	 * 新增学科组组内成员
	 * @param group_id：学科组编号
	 * @param personidList：专家编号
	 * @return msg
	 * @throws GeneralException
	 */
	public String createSubjectsPerson(String group_id, ArrayList<String> personidList) throws GeneralException{
		
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
				sql.append("select count(group_id) as count ");
				sql.append("From zc_subjectgroup_experts ");
				sql.append("where group_id=? ");
				sql.append("and expertid=? ");
				list.add(group_id);
				list.add(expertid);
				rs = dao.search(sql.toString(), list);
				while(rs.next()){
					if(rs.getInt("count") > 0){//存在该专家
						sql.setLength(0);
						list.clear();
						sql.append("update zc_subjectgroup_experts ");
						sql.append("set flag='1' ");
						sql.append("where group_id=? ");
						sql.append("and expertid=? ");
						list.add(group_id);
						list.add(expertid);
						dao.update(sql.toString(), list);
					}else {
						RecordVo vo = new RecordVo("zc_subjectgroup_experts");
						//学科组编号、专家编号、聘任标识
						vo.setString("group_id", group_id);
						vo.setDate("start_date", new java.sql.Date(new Date().getTime()));
						vo.setString("expertid", expertid);
						vo.setString("flag", "1");
						vo.setString("role", "0");
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
	 * @param group_id：学科组编号
	 * @param personidList：专家编号
	 * @return msg
	 * @throws GeneralException
	 */
	public String deleteSubjectsPerson(String group_id, ArrayList<String> personidList) throws GeneralException{
		
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		
		try {
			boolean delFlag = true;
			String inStr = "";
			List expertidList = new ArrayList();
			StringBuffer noDelPerson = new StringBuffer();
			int cursor = 0;
			for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				StringBuilder sql = new StringBuilder();
				ArrayList<String> list = new ArrayList<String>();
				// 查看该专家是否关联会议
				sql.append("select w0107 ");
				sql.append("from zc_expert_user zc,w01 ");
				sql.append("where zc.group_id=? ");
				sql.append("and zc.w0101=? and zc.w0101 = w01.w0101 ");
				sql.append("and type=2");
				list.add(group_id);
				list.add(expertid);
				rs = dao.search(sql.toString(), list);
				if(rs.next()){//存在关联
						delFlag = false;
						if(cursor<2)
							noDelPerson.append(rs.getString("w0107")+",");
						cursor++;
				}else {
					inStr+="?,";
					expertidList.add(expertid);
				}
				//删除专家
				if(delFlag && expertidList.size()>0 && inStr.length()>0){
					sql.setLength(0);
					list.clear();
					sql.append("delete from zc_subjectgroup_experts ");
					sql.append("where group_id=? ");
					sql.append("and expertid in ( ");
					sql.append(inStr.substring(0, inStr.length()-1)+")");
					list.add(group_id);
					list.addAll(expertidList);
					dao.delete(sql.toString(), list);
					msg = "撤销成功 ！";
				}
			}
			if(!delFlag){
				String error =  "【"+noDelPerson.substring(0,noDelPerson.length()-1)+"】"+(cursor>2?"等"+cursor+"名专家":"")+"已关联评审会议，不允许撤销！";
				throw GeneralExceptionHandler.Handle(new Exception(error));
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
	 * 修改专家角色为组长
	 * @param group_id  学科组编号
	 * @param personidList  专家编号
	 * @return
	 * @throws GeneralException
	 */
	@SuppressWarnings("unchecked")
	public String updateSubjectsPerson(String group_id, ArrayList<String> personidList,String role) throws GeneralException{
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			ArrayList list = new ArrayList();
			if(personidList.size()>0){
				String expertid = personidList.get(0);
				expertid = PubFunc.decrypt(expertid);
				//将所有专家置为组员
				StringBuilder sql = new StringBuilder();
				sql.append("update zc_subjectgroup_experts");
				sql.append(" set role='0'");
				sql.append(" where group_id=?");
				list.add(group_id);
				dao.update(sql.toString(), list);
				
				//将指定专家置为组长
				if("1".equals(role)){
					list.clear();
					sql.setLength(0);
					sql.append("update zc_subjectgroup_experts");
					sql.append(" set role=?");
					sql.append(" where group_id=?");
					sql.append(" and expertid=?");
					list.add(role);
					list.add(group_id);
					list.add(expertid);
					dao.update(sql.toString(), list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "设置组长失败！";
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		return msg;
	}
	
	/**
	 * 获取列头，表格渲染
	 * @param fieldList  数据字典列表
	 * @return
	 */
	public ArrayList<ColumnsInfo> getColumnList(ArrayList fieldList){
    	ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
    	try{
	    	//取得数据字典中设置的w01的构库的所有字段
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);
				
				String itemid = item.getItemid();//字段id
				String itemtype = item.getItemtype();//字段类型
				String codesetid = item.getCodesetid();//关联的代码			
				String columndesc = item.getItemdesc();//字段描述
				int itemlength = item.getItemlength();//字段长度
				String state = item.getState();//0隐藏  1显示
				String fieldsetid = item.getFieldsetid();
				
				ColumnsInfo columnsInfo = getColumnsInfo(itemid, columndesc, 100, itemtype, fieldsetid);
				if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
					if("0".equals(codesetid) || codesetid == null){//非代码字符型
						//获得字段描述
						if("w0101".equals(itemid)){
							columnsInfo.setColumnLength(itemlength);
							columnsInfo.setCodesetId("0");
							columnsInfo.setEncrypted(true);
							columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						}else{
							columnsInfo.setColumnLength(itemlength);
							columnsInfo.setCodesetId("0");
							if("0".equals(state))
								continue;
								//columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
						}
					}else{//代码型字符
						columnsInfo.setColumnLength(itemlength);
						columnsInfo.setCodesetId(codesetid);
						if("0".equals(state))
							continue;
							//columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
					}
				} else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
					columnsInfo.setColumnLength(itemlength);
					columnsInfo.setCodesetId("0");
					if("0".equals(state))
						continue;
						//columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				}
				if("w0103".equals(itemid) || "w0105".equals(itemid) || "w0107".equals(itemid)){
					columnsInfo.setLocked(true);
				}
				if("w0111".equalsIgnoreCase(itemid)){
					columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
					columnsInfo.setEditableValidFunc("false");
				}else
					columnsInfo.setEditableValidFunc("subjectsList_me.w01EditableValid");
				if("w0109".equals(itemid))
					continue;
				columnTmp.add(columnsInfo);
			}
			
			/** 隐藏 */
			// 学科组编号
			ColumnsInfo columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("group_id");
			columnsInfo.setColumnDesc("group_id");
			columnsInfo.setEncrypted(true);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(columnsInfo);
			
			// 组长
			columnsInfo = getColumnsInfo("role", "组长", 100, "A", "");
			columnsInfo.setRendererFunc("subjectsList_me.role");
			columnsInfo.setEditableValidFunc("false");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnsInfo.setOperationData("leader");
			columnTmp.add(columnsInfo);
			
			// 专家聘任标识
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("flag");
			columnsInfo.setColumnDesc("聘任标识");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnsInfo.setColumnType("A");
			columnsInfo.setEditableValidFunc("subjectsList_me.checkCell");
			columnsInfo.setCodesetId("45");
			columnTmp.add(columnsInfo);
			//起始日期
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("start_date");
	        columnsInfo.setColumnDesc("起始日期");
	        columnsInfo.setColumnLength(10);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
	        columnsInfo.setTextAlign("right");
	        columnsInfo.setRendererFunc("subjectsList_me.dateFormat");
	        columnsInfo.setColumnWidth(100);
	        columnsInfo.setColumnType("D");
	        columnsInfo.setEditableValidFunc("subjectsList_me.checkCell");
	        columnTmp.add(columnsInfo);
			//终止日期
	        columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnId("end_date");
			columnsInfo.setColumnLength(10);
	        columnsInfo.setColumnDesc("终止日期");
	        columnsInfo.setColumnWidth(100);
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
	        columnsInfo.setTextAlign("right");
	        columnsInfo.setRendererFunc("subjectsList_me.dateFormat");
	        columnsInfo.setColumnType("D");
	        columnsInfo.setEditableValidFunc("subjectsList_me.checkCell");
	        columnTmp.add(columnsInfo);
	        
	        columnsInfo = getColumnsInfo("b0110", "所属机构", 100, "A", "");
			columnsInfo.setEditableValidFunc("subjectsList_me.b0110EditableValid");
			columnsInfo.setCodesetId("UM");
			columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnsInfo.setCtrltype("3");
			columnsInfo.setNmodule("9");
			columnsInfo.setCodeSetValid(false);
			columnTmp.add(columnsInfo);
    	} catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
    	return columnTmp;
    }
	
	/**
	 * 初始化控件列对象
	 * @param columnId 指标
	 * @param columnDesc 名称
	 * @param columnWidth 显示列宽
	 * @param type 类型
	 * @return
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type, String fieldsetid) {
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        if ("A".equals(type)) {
            columnsInfo.setCodesetId("0");
        }
        columnsInfo.setDecimalWidth(0);// 小数位
        columnsInfo.setFieldsetid(fieldsetid);
        	
        // 数值和日期默认居右
        if ("D".equals(type) || "N".equals(type))
            columnsInfo.setTextAlign("right");

        return columnsInfo;
    }
	
	/**
	 * 获取人员信息查询语句
	 * @param fieldList 数据字典列表
	 * @param group_id 学科组编号
	 * @param isshowall 是否显示全部
	 * @return
	 * @throws GeneralException
	 */
	@SuppressWarnings("unchecked")
	public String getSubjectPersonSql(ArrayList fieldList, String group_id, String isshowall) throws GeneralException {
		StringBuilder sql =  new StringBuilder("select ");//查询sql
		try {
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);		
				String itemid = item.getItemid();//字段id
				sql.append(" "+"w."+itemid+",");
			}
			sql.append(" z.group_id, z.expertid, z.flag,z.role,w.b0110,z.start_date,z.end_date ");
			sql.append(" from w01 w,zc_subjectgroup_experts z ");
			sql.append(" where 1=1 ");
			sql.append(" and w.W0101=z.expertid and z.group_id='"+group_id+"' ");
			if("0".equals(isshowall)){//不显示全部时,只显示任聘人员
				sql.append(" and flag=1 ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}
	
	/**
	 * 获取功能按钮
	 * @return
	 */
	public ArrayList<Object> getButtonList(String state){
		
		ArrayList<Object> buttonList = new ArrayList<Object>();
		try{
			ButtonInfo buttonInfo = new ButtonInfo();
			boolean flag = false;
			if("0".equals(state))
				flag = true;
//			if (this.userview.hasTheFunction("380020304")) {
//				buttonInfo = new ButtonInfo("新增", "");
//				buttonInfo.setId("subjectsList_newPerson");
//				buttonInfo.setDisabled(flag);
//				buttonList.add(buttonInfo);
//			}

			if (this.userview.hasTheFunction("380020312")) {
				buttonInfo = new ButtonInfo("新增外部专家", "subjectsList_me.addExpert");
				buttonInfo.setId("subjectsList_newOutPerson");
				buttonList.add(buttonInfo);
			}
			if (this.userview.hasTheFunction("380020304")) {
				buttonInfo = new ButtonInfo("引入专家", "subjectsList_me.openExpertPicker");
				buttonInfo.setId("subjectsList_newPerson");
				buttonList.add(buttonInfo);
			}
			
			if (this.userview.hasTheFunction("380020307")) {
				buttonInfo = new ButtonInfo("专家抽取", "subjectsList_me.randomSelectionr");
				buttonInfo.setId("subjectsList_randomChoose");
				buttonInfo.setDisabled(flag);
				buttonList.add(buttonInfo);
			}
			if (this.userview.hasTheFunction("380020305")) {
				buttonInfo = new ButtonInfo("撤销", "subjectsList_me.deletePerson");
				buttonInfo.setId("subjectsList_deletePerson");
				buttonInfo.setDisabled(flag);
				buttonList.add(buttonInfo);
			}
			if(this.userview.hasTheFunction("380020309")){
				buttonInfo = new ButtonInfo(ResourceFactory.getProperty("button.save"),ButtonInfo.FNTYPE_SAVE, "ZC00002220");
				buttonInfo.setId("subjectsList_saveInfo");
				buttonInfo.setDisabled(flag);
				buttonList.add(buttonInfo);
			}
			
			ButtonInfo queryBox = new ButtonInfo();
			queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
			queryBox.setText("请输入单位名称、部门、姓名");
			queryBox.setFunctionId("ZC00002218");
			buttonList.add(queryBox);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return buttonList;
	}

	/**
	 * 获得学科组信息
	 * @param type
	 * @param id
	 * @return 
	 * @throws GeneralException 
	 */
	public HashMap<String, String> getSubjectsInfo(String type, String id) throws GeneralException {
		HashMap<String, String> map = new HashMap<String, String>();
		
		if("1".equals(type)){//新建时
			String data = DateUtils.format(new java.sql.Date(new Date().getTime()),"yyyy-MM-dd");
			String b0110 = this.userview.getUserDeptId();//取得所属单位
			String unitName = "";
			unitName = !StringUtils.isEmpty(AdminCode.getCodeName("UN", b0110))?AdminCode.getCodeName("UN", b0110):AdminCode.getCodeName("UM", b0110);
			map.put("b0110", b0110+"`"+unitName);
			map.put("create_time", data);
			map.put("create_fullname", StringUtils.isEmpty(this.userview.getUserFullName()) ? this.userview.getUserName() : this.userview.getUserFullName());
			return map;
		}
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
		try {
			StringBuilder sql = new StringBuilder(); 
	    	sql.append("select group_name,description,b0110,create_time,create_fullname ");
	    	sql.append(" from zc_subjectgroup ");
	    	sql.append(" where  group_id=?");
	    	
	    	ArrayList<String> list = new ArrayList<String>();
	    	list.add(id);
			rs = dao.search(sql.toString(), list);
			
			while (rs.next()) {
				map.put("comsubName", rs.getString("group_name"));
				map.put("description", rs.getString("description"));
				String unitName = "";
				unitName = !StringUtils.isEmpty(AdminCode.getCodeName("UN", rs.getString("b0110")))?AdminCode.getCodeName("UN", rs.getString("b0110")):AdminCode.getCodeName("UM", rs.getString("b0110"));
				map.put("select_b0110", rs.getString("b0110")+"`"+unitName);
				map.put("create_fullname", rs.getString("create_fullname"));
				map.put("create_time", String.valueOf(rs.getDate("create_time")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}


	public ArrayList<HashMap<String, String>> getModifyList(String subjectsName, String description, String b0110) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", "group_name");//聘委会名称
		map.put("value", subjectsName);
		list.add(map);
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("key", "description");//描述
		map3.put("value", description);
		list.add(map3);
		if(StringUtils.isNotBlank(b0110)){//所属单位为空时不更新
			HashMap<String, String> map4 = new HashMap<String, String>();
			map4.put("key", "b0110");//所属单位
			map4.put("value", b0110);
			list.add(map4);
		}
		return list;
	}
}
