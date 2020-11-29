package com.hjsj.hrms.module.jobtitle.cardview.businessobject;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ListViewBo {
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public ListViewBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	
	/**
	 * 获取需要展示的item列
	 * @return
	 */
	public HashMap<String, String> getTableShowItem() {
		String itemShowId = "";//需要展示的列名
		HashMap<String, String> map = null;
		try {
			JobtitleConfigBo jbc = new JobtitleConfigBo(this.conn, this.userview);
			map = jbc.getVoteConfig();
		
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 页面列表需要展示的数据//拼接sql，为了这样用union all就能一次性查出,跟代码就能看出来
	 * 这块有时间可以优化，现在太乱了
	 * @param queue
	 * @param itemShowId
	 * @return
	 */
	//map.put("nbasea0100_safe", PubFunc.encrypt(rs.getString("w0503")+rs.getString("w0505")));//nabse+a0100加密
	public ArrayList<HashMap<String,String>> getPersonDataList(String itemShowId,String sortItemId) {
		ArrayList<HashMap<String,String>> infoList = new ArrayList<HashMap<String,String>>();
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> lists = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);
		String itemOfCate = "";//名称一列显示成
    	RowSet rs = null;
    	String cateItem = "zc.categories_id,'',0,'','','',0,0,max(name) as name,max(queue) as queues,";
    	String neededItem = "ZPC.categories_id,ZD.approval_state,ZD.expert_state,ZC.c_level,W.w0501,W.w0301,ZU.type,ZU.usetype,'',0,";
    	StringBuffer sqlBF = new StringBuffer();//现在要展示第一行名称，后面数据这种形式，这样
    	try {
    		if(itemShowId.toLowerCase().indexOf("w0535") != -1 || itemShowId.toLowerCase().indexOf("w0536") != -1 || itemShowId.toLowerCase().indexOf("w0537") != -1) {
    			cateItem += "'','',";
    			neededItem += "W0503,W0505,";
    			//投票列表去了w0536指标了，这里如果有的话需要加上w0536，界面会根据是否支持投票参数显示word还是列表
    			itemShowId += ",w0536";
    		}
    		itemOfCate = getItemCate(itemShowId).substring(1);//拼接sql，为了这样用union all就能一次性查出
    		
    		String sql = "select categories_id from zc_personnel_categories where categories_id in (select categories_id from zc_expert_user where username=? and state<>? group by categories_id) and approval_state<>?";
    		list.add(this.userview.getUserName());
    		list.add("0");
    		list.add("2");
    		rs = dao.search(sql,list);
    		String categories_id = "";
    		while(rs.next()) {
    			categories_id = rs.getString("categories_id");
    			sqlBF.append(" union all");
    			sqlBF.append(" select 0 as queue," + cateItem + itemOfCate + ",null as create_time,zp.seq as seq,0 as seq1 from zc_personnel_categories zp inner join zc_categories_relations zc on zc.categories_id = zp.categories_id where zp.categories_id=? group by zc.categories_id,zp.seq");
    			sqlBF.append(" union all");
    			sqlBF.append(" select queue," + neededItem +  itemShowId + ",W.create_time,ZPC.seq as seq,ZC.seq as seq1 from W05 W,zc_expert_user ZU "
    					+ " LEFT JOIN zc_data_evaluation ZD  "
    					+ " on ZU.W0301=ZD.W0301 and ZU.W0501=ZD.W0501 and ZU.username=ZD.username "
    					+ "INNER join zc_categories_relations ZC on ZC.w0501=ZU.W0501  and ZU.categories_id = ZC.categories_id "
    					+ "INNER join zc_personnel_categories ZPC on ZC.categories_id=ZPC.categories_id "
    					+ "where ZC.categories_id=? and ZU.username=? "
    					+ "and W.W0301=ZU.W0301 and W.W0501=ZU.W0501 and ZU.state<>0 ");
    			lists.add(categories_id);
    			lists.add(categories_id);
    			lists.add(this.userview.getUserName());
    		}
    		if(sqlBF.length() == 0) {
    			return infoList;
    		}
    		//先按照组排序，再按人员排序
    		sqlBF.append("order by seq,seq1");
    		if(StringUtils.isNotBlank(sortItemId)) {
    			sqlBF.append(","+sortItemId);
    		}
    		//拼接好的sql查询然后进行
    		infoList = getDataList(itemShowId,sqlBF.substring(10),lists,dao);
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		PubFunc.closeDbObj(rs);
    	}
    	return infoList;
	}
	
	/**
	 * 为了拼接sql，第一行显示,前三个字段分别为名称，id，批次，后面的就是空，这样用union all就能一次性查出来
	 * @param itemShowId
	 * @return
	 */
	private String getItemCate(String itemShowId) {
		StringBuffer itemCateId = new StringBuffer();;
		try {
			String[] itemArray = itemShowId.split(",");
			itemCateId.append("");
			String type = "";
			for(int i = 0; i < itemArray.length; i++) {
				type = DataDictionary.getFieldItem(itemArray[i]).getItemtype();
				if("N".equalsIgnoreCase(type))
					itemCateId.append(",0 as "+itemArray[i]);
				else if("A".equalsIgnoreCase(type))
					itemCateId.append(",'' as "+itemArray[i]);
				else 
					itemCateId.append(",null as "+itemArray[i]);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return itemCateId.toString();
	}
	
	/**
	 * 获取最后的数据
	 * @param itemShowId
	 * @param sql
	 * @param list
	 * @param dao
	 * @return
	 */
	private ArrayList<HashMap<String,String>> getDataList(String itemShowId,String sql,ArrayList<String> list,ContentDAO dao) {
		ArrayList<HashMap<String,String>> listMap = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map = new HashMap<String,String>();
		CardViewBo cardViewBo = new CardViewBo(this.conn,this.userview);
		RowSet rs = null;
		FieldItem _tempItem = null;
		String value = null;//因为可能有代码类型的值，这里特殊处理
		String itemid = "";
		String categories_id = "";//申报人分组id
		String approval_state = "";//投票状态
		String c_level = "";
		String w0501 = "";//申报人id
		String w0301 = "";//会议id
		String type = "";//当前阶段
		String usetype = "";
		String name = "";
		int queueMax = 0;
		String expert_state = "";//1:待审 2：已审  3：页面已提交
		String currentItemid = "";
		int flag = 0;
		try {
			String[] itemArray = itemShowId.split(",");
			rs = dao.search(sql,list);
			while(rs.next()) {
				map = new HashMap<String,String>();
				itemid = rs.getString(1);//用来区别是数据行还是分组名行
				categories_id = rs.getString(2);//用来区别是数据行还是分组名行
				approval_state = rs.getString(3);//审批状态 1：同意  2：不同意  3：弃权
				expert_state = rs.getString(4);//专家状态标识 1:待审 2：已审  3：页面已提交 同各评价表的状态，1，保存，2，已提交
				c_level = rs.getString(5);//关联的职级 暂时不用
				w0501 = rs.getString(6);//
				w0301 = rs.getString(7);//
				type = rs.getString(8);//1：评委会 2：学科组成员 3：同行专家 4：二级单位
				usetype = rs.getString(9);//1|null：材料评审  2：投票
				name = rs.getString(10)==null?"":rs.getString(10);
				queueMax = rs.getInt(11);
				map.put("itemid", itemid);
				map.put("categories_id", PubFunc.encrypt(categories_id));
				map.put("approvalState", approval_state);
				map.put("expert_state", expert_state);
				map.put("c_level", c_level);
				map.put("w0501", PubFunc.encrypt(w0501));
				map.put("w0301", PubFunc.encrypt(w0301));
				map.put("type", type);
				map.put("usetype", usetype);
				map.put("name", name);
				map.put("queueMax", String.valueOf(queueMax));
				if(!"0".equals(itemid)) {
					String agreeItem = "";//赞成人数
					String disagreeItem = "";//反对人数
					String giveupItem = "";//弃权人数
					if("1".equals(type)) {//评委会
						agreeItem = "W0553";
						disagreeItem = "W0549";
						giveupItem = "W0551";
						
					} else if("2".equals(type)) {//学科组
						agreeItem = "W0547";
						disagreeItem = "W0543";
						giveupItem = "W0545";
						
					} else if("3".equals(type)) {//外部专家
						agreeItem = "W0531";
						disagreeItem = "W0527";
						giveupItem = "W0529";
					} else if("4".equals(type)) {//学院任聘组
						agreeItem = "W0567";
						disagreeItem = "W0563";
						giveupItem = "W0565";
					}
					FieldItem  agree  = DataDictionary.getFieldItem(agreeItem, 1);
					FieldItem  disagree  = DataDictionary.getFieldItem(disagreeItem, 1);
					FieldItem  giveup  = DataDictionary.getFieldItem(giveupItem, 1);
			    	
			    	map.put("agreetext", agree.getItemdesc().replace("人数", ""));//"赞成"显示文本,去掉“人数”
			    	map.put("disagreetext", disagree.getItemdesc().replace("人数", ""));//"反对"显示文本
			    	map.put("giveuptext", giveup.getItemdesc().replace("人数", ""));//"弃权"显示文本
				}
				flag = 12;
				if(itemShowId.toLowerCase().indexOf("w0535") != -1 || itemShowId.toLowerCase().indexOf("w0536") != -1 || itemShowId.toLowerCase().indexOf("w0537") != -1) {
					map.put("nbasea0100_safe", PubFunc.encrypt(rs.getString(12)+rs.getString(13)));//nabse+a0100加密
					flag = 14;
				}
				for(int i = flag; i <= itemArray.length+flag-1; i++) {
					currentItemid = itemArray[i-flag].toLowerCase();//当前的itemid
					if(!"0".equals(itemid) && ("W0513".equalsIgnoreCase(currentItemid) || "W0515".equalsIgnoreCase(currentItemid))) {
						value = AdminCode.getCodeName("AJ", rs.getString(i));
					}else if("W0503".equalsIgnoreCase(currentItemid)) {//人员库
						value = AdminCode.getCodeName("@@", rs.getString(i));
					}else if("W0555".equalsIgnoreCase(currentItemid)) {//评审环节
						String w0555 = rs.getString(i);
						if("1".equals(w0555)) {
							value = ResourceFactory.getProperty("zc.reviewfile.step1showtext");//评委会阶段
						}else if("2".equals(w0555)) {
							value = ResourceFactory.getProperty("zc.reviewfile.step2showtext");//学科组阶段
						}else if("3".equals(w0555)) {
							value = ResourceFactory.getProperty("zc.reviewfile.step3showtext");//同行专家阶段
						}else if("4".equals(w0555)) {
							value = ResourceFactory.getProperty("zc.reviewfile.step4showtext");//二级单位阶段
						}
					}else if("W0573".equalsIgnoreCase(currentItemid)) {
						String W0573 = rs.getString(i);
						if("1".equals(W0573)) {
							value = ResourceFactory.getProperty("zc.reviewfile.w0573_1");//材料审核阶段
						}else if("2".equals(W0573)) {
							value = ResourceFactory.getProperty("zc.reviewfile.w0573_2");//投票阶段
						}
					}else if("W0539".equalsIgnoreCase(currentItemid)) {
						map.put("w0539_qnid", cardViewBo.getQnId(rs.getString(i)));//内部评审问卷号
						value = PubFunc.encrypt("0".equals(rs.getString(i))?null:rs.getString(i));
					}else if("W0541".equalsIgnoreCase(currentItemid)) {
						map.put("w0541_qnid", cardViewBo.getQnId(rs.getString(i)));//专家鉴定问卷号
						value = PubFunc.encrypt("0".equals(rs.getString(i))?null:rs.getString(i));
					}else {
						_tempItem=DataDictionary.getFieldItem(currentItemid);
						if(!"0".equals(itemid) && "A".equalsIgnoreCase(_tempItem.getItemtype()) && !"0".equals(_tempItem.getCodesetid())) {
							value = AdminCode.getCodeName(_tempItem.getCodesetid(), rs.getString(i));
						}else if("D".equalsIgnoreCase(_tempItem.getItemtype())){
							String itemfmt = "";
							switch (_tempItem.getItemlength()) {
								case 4:
									itemfmt = "yyyy";
									break;
								case 7:
									itemfmt = "yyyy-MM";
									break;
								case 10:
									itemfmt = "yyyy-MM-dd";
									break;
								case 16:
									itemfmt = "yyyy-MM-dd HH:mm";
									break;
								case 18:
									itemfmt = "yyyy-MM-dd HH:mm:ss";
									break;
								default:
									itemfmt = "yyyy-MM-dd";
									break;
							}
							SimpleDateFormat dateFormat = new SimpleDateFormat(itemfmt);
							value = rs.getDate(i)==null?"":dateFormat.format(rs.getDate(i));
						}else {
							value = rs.getString(i)==null?"":rs.getString(i);
						}
					}
					map.put(currentItemid, value);
				}
				map.put("subObject", PubFunc.encrypt(w0501+"_"+type));//绩效分析被调查对象subObject
				map.put("expertName", PubFunc.encrypt(this.userview.getUserName()));
				listMap.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return listMap;
	}
	
	/**
	 * 判断是评分还是投票
	 * @return
	 */
	public String getTypeOfVoteOrScore() {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		String scoreOrVoteType = "";
		try {
			String sql = "select useType from zc_expert_user where username = ? and state<>0 order by useType desc";
			list.add(this.userview.getUserName());
			rs = dao.search(sql,list);
			if(rs.next()) {
				scoreOrVoteType = rs.getString("useType");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return scoreOrVoteType;
	}
	
	/**
	 * 判断是评分还是投票
	 * @param useType//1|null：材料评审  2：投票,3打分
	 * @return
	 */
	public String getShowQueue(String useType) {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		int queue = 0;
		try {
			if("3".equals(useType)) {
				String sqls = "select type,w0301 from zc_expert_user where username=?";
				list.add(this.userview.getUserName());
				rs = dao.search(sqls,list);
				String w0301 = "";
				String reviewlink = "";
				if(rs.next()) {
					reviewlink = rs.getString("type");
					w0301 = rs.getString("w0301");
				}
				//通过考核模版表中找到所有的没有提交的数据，找到对应的批次，这样最小的批次应该就是需要显示的
				String sql = "select min(queue) as queue from zc_categories_relations where w0501 in (select w0501 from W05  where w0505 in "
						+ "( SELECT ko.Object_id FROM kh_mainbody km inner join kh_object ko on km.kh_object_id = ko.id "
						+ "WHERE Mainbody_id=? and (Status = ? or Status = ?) AND km.Relation_id=? group by ko.Object_id) and W0301=? and W0555=? )";
				list.clear();
				list.add(this.userview.getUserName());
				list.add("0");
				list.add("1");
				list.add("1_"+w0301+"_"+reviewlink);
				list.add(w0301);
				list.add(reviewlink);
				rs = dao.search(sql,list);
				if(rs.next()) {
					queue = rs.getInt("queue") == 0?1:rs.getInt("queue");
				}
			}else {
				String sql = "select min(queue) as queue from zc_categories_relations where w0501 not in "
						+ "(select w0501 from zc_data_evaluation where username = ? and "+Sql_switcher.isnull("expert_state", "''")+"='3') and categories_id in (select categories_id from zc_expert_user where username = ?)";
				list.add(this.userview.getUserName());
				list.add(this.userview.getUserName());
				rs = dao.search(sql,list);
				if(rs.next()) {
					queue = rs.getInt("queue");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return String.valueOf(queue);
	}
	
	/**
	 * 返回当前会议的状态
	 * @return
	 */
	public String getW0301State() {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		String state = "";
		try {
			
			String sql = "select W0321 from w03 where w0301 in (select w0301 from zc_expert_user where username=? group by w0301)";
			list.add(this.userview.getUserName());
			rs = dao.search(sql,list);
			if(rs.next()) {
				state = rs.getString("W0321");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return state;
	}
	
	
	/**
	 * 返回当前对应的分组的状态
	 * @return
	 */
	public int getCountState() {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		String state = "";
		int count = 0;
		try {
			
			String sql = "select COUNT(1) as count from zc_personnel_categories where categories_id in (select categories_id from zc_expert_user where username = ? group by categories_id) and approval_state = ?";
			list.clear();
			list.add(this.userview.getUserName());
			list.add("1");
			rs = dao.search(sql,list);
			if(rs.next()) {
				count = rs.getInt("count");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return count;
	}
	
	/**
	 * 获取是否全部提交了
	 * @return
	 */
	public int getApprovelState() {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		int count = 0;
		try {
			String sql = "select count(1) as count from zc_expert_user ZU LEFT JOIN zc_data_evaluation ZD on ZU.W0301=ZD.W0301 and ZU.W0501=ZD.W0501 and "
					+ "ZU.username=ZD.username where ZU.username=? and (ZD.expert_state is null or "+Sql_switcher.isnull("ZD.expert_state", "''")+"<>? )";
			list.add(this.userview.getUserName());
			list.add("3");
			rs = dao.search(sql,list);
			if(rs.next()) {
				count = rs.getInt("count");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return count;
		
	}
	
	/**
	 * 返回当前对应的分组的状态
	 * @return
	 */
	public HashMap<String,Integer> getUserType() {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		try {
			
			String sql = "select usetype,type from zc_expert_user where username = ? order by usetype desc";
			list.add(this.userview.getUserName());
			rs = dao.search(sql,list);
			if(rs.next()) {
				map.put("usetype", rs.getInt("usetype"));
				map.put("type", rs.getInt("type"));
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
	
	/**
	 * 可能是二级单位，评委这种的，会出现多个分组，这样审核账号是一样的，
	 * 这里判断是不是所有的组是不是都是启动的
	 * @param startCount
	 * @return
	 */
	public boolean getSumCategories(int startCount) {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		boolean isAllStart = true;
		int count = 0;
		try {
			
			String sql = "select COUNT(1) as count from zc_personnel_categories where categories_id in (select categories_id from zc_expert_user where username = ?)";
			list.add(this.userview.getUserName());
			rs = dao.search(sql,list);
			if(rs.next()) {
				count = rs.getInt("count");
			}
			if(startCount < count)//如果启动的组小于总的组数，说明还有组是没启动的，这样能够显示
				isAllStart = false;
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return isAllStart;
	}
	
}
