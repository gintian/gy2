package com.hjsj.hrms.module.jobtitle.reviewfile.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 职称评审_上会材料_生成账号密码
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
public class GenerateAcPwBo {

    // 基本属性
    private Connection conn = null;
    private UserView userview;
    private static Random r = new Random();
    /**
     * 构造函数
     * 
     * @param conn
     */
    public GenerateAcPwBo(Connection conn) {
        this.conn = conn;
    }
    public GenerateAcPwBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview = userview;
	}

    /**
     * 
     * @Title:generate
     * @Description：根据需求生成账号密码数量组装list
     * @author liuyang
     * @param number
     *            需要生成账号密码数量
     * @param dao
     * @return
     * @throws GeneralException
     */
    public static ArrayList generate(int number, ContentDAO dao) throws GeneralException {
        Object[] content = generateContent(number, dao).toArray();
        ArrayList list = new ArrayList();
        for (int i = 0; i < number; i++) {
            HashMap map = new HashMap();
            map.put("content", (String) content[i]);
            map.put("pasword", generatePasword());
            list.add(map);
        }
        return list;
    }

    /**
     * 
     * @Title:generateContent
     * @Description：生成与数据库不重复的账号
     * @author liuyang
     * @param number
     *            需要生成账号数量
     * @param dao
     * @return 返回set集合
     * @throws GeneralException
     */
    private static HashSet generateContent(int number, ContentDAO dao) throws GeneralException {
        RowSet rs = null;
        HashMap map = new HashMap();
        HashSet set = new HashSet();
        try {
            StringBuffer str = new StringBuffer("");
            Calendar cal = Calendar.getInstance();// 使用日历类
            String year = (cal.get(Calendar.YEAR) + "").substring(2);// 得到年
            str.append(year);
            int month = cal.get(Calendar.MONTH) + 1;// 得到月，因为从0开始的，所以要加1
            str.append(month > 6 ? "A" : "B");
            while (set.size() <= number) {
            	String ct = str + content();// 生成账号
                rs = dao.search("SELECT username FROM zc_expert_user where username = '"+ct+"'");
                while(true) {// 如果重复跳出循环，重新生成账号
                	if(rs.next() || set.contains(ct)){
                    	ct = str + content();
                    	rs = dao.search("SELECT username FROM zc_expert_user where username = '"+ct+"'");
                	}else{
                		set.add(ct);
                		break;
                	}
                } 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
        	PubFunc.closeDbObj(rs);
        }
        return set;
    }

    /**
     * 
     * @Title:content
     * @Description：生成账号后三位
     * @author liuyang
     * @return 返回账号后三位
     */
    private static String content() {
        StringBuffer st = new StringBuffer("");
        for (int i = 0; i < 3; i++) {
            // 生成97-122随机数
            int randNum = r.nextInt(25) + 97;
            // 拼接字母
            st.append((char) randNum);
        }
        return st.toString();
    }

    /**
     * 
     * @Title:generatePasword
     * @Description：随机生成6位密码
     * @author liuyang
     * @return 返回密码
     */
    private static String generatePasword() {
        
        StringBuffer number = new StringBuffer("");
        for (int i = 0; i < 6; i++) {
            number.append(r.nextInt(10) + "");
        }
        return number.toString();
    }

    /**
     * 
     * @Title:saveGenerateAcPs
     * @Description：随机生成多个和保存一个账号密码
     * @author liuyang
     * @param map
     *            包含组装sql部分数据 参数说明： isSelectAll 是否需要反选数据 description 信息描述
     *            isSingle 是否是单独保存/批量保存 content 单独宝保存时账号 pasword 单独保存时的密码
     *            state状态 -启用、禁用 reviewPersonId 申请人 id meetingId 会议 id
     *            meetingState会 议状态范围
     * @param number
     *            需要生成的账号密码数量
     * @param type
     *            1内部评审，2内部专家，3外部专家
     * @param dao
     * @return 返回保存是否成功
     * @throws GeneralException
     */
    public static synchronized String saveGenerateAcPs(HashMap map, int number, int type, ContentDAO dao, Connection conn)
            throws GeneralException {
        String resultTip = "";
        try {
            int num = 0;
            ArrayList valueList = new ArrayList();
            ArrayList value = new ArrayList();
            int statment = 0;
            if ("0".equals((String) map.get("isSingle"))) {
                value = generate(number, dao);
                num = value.size();
            } else {
                value = (ArrayList) map.get("dataAddList");
                num = number;
            }
            // 获取描述信息,无描述信息则为null
            String desc = "".equals((String) map.get("description")) ? "" : (String) map.get("description");
            String sql = getIdSql((ArrayList) map.get("idlist"), (String) map.get("isSelectAll"), dao,
                    (String) map.get("meetingState"));
            String w0101 = "".equals((String) map.get("reviewPersonId")) ? "" : (String) map.get("reviewPersonId");

            for (int i = 0; i < num; i++) {
                StringBuffer buf = new StringBuffer("");
                //生成id
                IDFactoryBean idf = new IDFactoryBean();
    			String user_id = idf.getId("zc_expert_user.user_id", "", conn);
                
                buf.append(" insert into ");
                buf.append(" zc_expert_user ");
                buf.append(" (user_id,group_id,W0301,W0501,username,password,state,type,W0101,description,role) ");
                buf.append("((select  '"+user_id+"',NULL,w05.W0301,w05.W0501,'");

                if ("0".equals((String) map.get("isSingle"))) {
                    HashMap mapValue = new HashMap();
                    mapValue = (HashMap) value.get(i);
                    buf.append(mapValue.get("content") + "','" + mapValue.get("pasword"));
                    buf.append("'," + map.get("state") + "," + type + ",'"+w0101+"','" + desc + "',NULL from w05,w03 ");
                } else {
                    MorphDynaBean abean = new MorphDynaBean();
                    abean = (MorphDynaBean) value.get(i);
                    if (ResourceFactory.getProperty("zc_new.zc_reviewfile.able").equals((String) abean.get("statment")) || "1".equals((String) abean.get("statment")))
                        statment = 1;
                    else
                        statment = 0;
                    buf.append((String) abean.get("content") + "','" + (String) abean.get("pasword"));
                    buf.append("'," + statment + "," + type + ",'"+w0101+"','");
                    buf.append((String) abean.get("desc") + "',NULL from w05,w03 ");
                }
                buf.append("where 1=1  ");
                buf.append(sql);

                if ("1".equals(map.get("isSelectAll")) && !"0".equals(map.get("meetingState"))) {
                    buf.append(" and w03.w0321='" + map.get("meetingState") + "'");
                }

                buf.append(" and w05.w0301 = w03.w0301  ");
                buf.append("))");
                dao.update(buf.toString());
                saveW05(sql,type,dao);
            }
            resultTip = "1";
        } catch (SQLException e) {
            resultTip = "0";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return resultTip;
    }

    /**
     * 
     * @Title:getIdSql
     * @Description：获取sql
     * @author liuyang
     * @param idlist
     *            会议id 与账号id
     * @param isSelectAll
     *            是否是 全选
     * @param dao
     * @param meetingState
     *            会议状态 06 为结束
     * @return
     * @throws GeneralException
     */
    public static String getIdSql(ArrayList idlist, String isSelectAll, ContentDAO dao, String meetingState)
            throws GeneralException {

        RowSet rs = null;
        String sql = "";
        String meetingId = "";
        String reviewPersonId = "";
        HashMap map = new HashMap();
        try {
            if (idlist.size() > 0 && idlist != null) {
                if ("1".equals(isSelectAll)) {
                    String checkAllSql = "select w05.w0501 as w0501,w05.w0301 as w0301 from w05,w03 ";
                    checkAllSql = checkAllSql + "where 1=1 ";
                    if (!"0".equals(meetingState))
                        checkAllSql = checkAllSql + "and w03.w0321='" + meetingState + "'";
                    checkAllSql = checkAllSql + " and w03.w0301=w05.w0301";
                    rs = dao.search(checkAllSql);

                    while (rs.next()) {
                        map.put(rs.getString("w0501"), rs.getString("w0301"));
                    }
                }

                for (int i = 0; i < idlist.size(); i++) {
                    MorphDynaBean abean = (MorphDynaBean) idlist.get(i);
                    meetingId = PubFunc.decrypt(SafeCode.decode((String) abean.get("meetingid")));
                    reviewPersonId = PubFunc.decrypt(SafeCode.decode((String) abean.get("userid")));
                    if ("1".equals(isSelectAll) && meetingId.equals((String) map.get(reviewPersonId + ""))) {
                        map.remove(reviewPersonId);
                        continue;
                    }

                    sql = sql + "(w05.w0501 = '" + reviewPersonId + "'and w05.w0301 = '" + meetingId + "') or";
                }
                if (map.size() > 0 && map != null) {
                    Iterator iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Entry entry = (Entry) iterator.next();
                        reviewPersonId = (String) entry.getKey();
                        meetingId = (String) entry.getValue();
                        sql = sql + "(w05.w0501 = '" + reviewPersonId + "'and w05.w0301 = '" + meetingId + "') or";
                    }

                }
                if (sql.length() > 0 && sql != null)
                    sql = " and (" + sql.substring(0, sql.length() - 2) + ")";

            }

        } catch (SQLException e) {

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);

        }
        return sql;
    }

    /**
     * 
     @Title:isOver
     * @Description：
     * @author liuyang
     * @param idlist
     *            传入的会议id和申请人id
     * @param isSelectAll
     *            是否需要反选
     * @param meetingState
     *            会议状态--用以反选是确定反选范围
     * @return 返回是否存在会议结束数据
     * @throws GeneralException
     */
    public boolean isOver(ArrayList idlist, String isSelectAll, String meetingState) throws GeneralException {
        ArrayList valueList = new ArrayList();
        boolean result = false;
        String sql = "";
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            sql = getIdSql(idlist, isSelectAll, dao, meetingState);
            StringBuffer buf = new StringBuffer("");
            buf.append("select w03.w0321 as w0321 ");
            buf.append(" from w05,w03   ");
            buf.append(" where 1=1 ");
            buf.append(sql);
            buf.append(" and w05.w0301 = w03.w0301  ");
            buf.append(" and w03.w0321 ='06'");

            RowSet rs = dao.search(buf.toString(), valueList);
            result = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return result;
    }

    /**
     * @Title:saveAcPs
     * @Description：
     * @author liuyang
     * @param tip
     *            返回頁面保存是否成功
     * @param map
     *            組裝數據 包含组装sql部分数据 参数说明： isSelectAll 是否需要反选数据 description 信息描述
     *            isSingle 是否是单独保存/批量保存 content 单独宝保存时账号 pasword 单独保存时的密码
     *            state状态 -启用、禁用 reviewPersonId 申请人 id meetingId 会议 id
     *            meetingState会 议状态范围
     * @param base
     *            返回保存失敗的專家類型
     * @param dao
     * @param inReview
     * @return
     * @throws GeneralException
     */
    public String saveAcPs(String tip, HashMap map, String base, ContentDAO dao, String inReview, int type)
            throws GeneralException {
        String result;
        int number;
        StringBuffer baseTip = new StringBuffer("");

        // 判断是否需要生成
        if (inReview == null)
            return base;
        number = Integer.parseInt(inReview);
        result = GenerateAcPwBo.saveGenerateAcPs(map, number, type, dao, this.conn);// 生成保存账号密码
        if (!"0".equals(result))
            return base;
        if (type == 1)
            baseTip.append(ResourceFactory.getProperty("zc_new.zc_reviewfile.inReview") + "/");
        if (type == 2)
            baseTip.append(ResourceFactory.getProperty("zc_new.zc_reviewfile.inExpert") + "/");
        if (type == 3)
            baseTip.append(ResourceFactory.getProperty("zc_new.zc_reviewfile.exExpert") + "/");
        return base + baseTip.toString();
    }

    /**
     * 
     * @Title:getMeetingStatement
     * @Description：转换会议状态用于sql查询
     * @author liuyang
     * @param MeetingStatement
     *            会议状态 所有-all 进行中(in)-09 暂停(stop)-09 已结束(finish)-06
     * @return 相应代码
     */
    public String getMeetingStatement(String MeetingStatement) {

        String meetingState = "0";
        if (!StringUtils.isEmpty(MeetingStatement) && !"all".equals(MeetingStatement)) {
            if ("in".equals(MeetingStatement)) {// 进行中
                meetingState = "05";
            } else if ("stop".equals(MeetingStatement)) {// 暂停
                meetingState = "09";
            } else if ("finish".equals(MeetingStatement)) {// 已结束
                meetingState = "06";
            }
        }
        return meetingState;

    }

    /**
     * 
     * @Title:saveW05
     * @Description：添加
     * @author liuyang
     * @param sql
     * @param type
     * @param dao
     * @throws GeneralException
     */
    public static void saveW05(String sql, int type, ContentDAO dao) throws GeneralException {
        StringBuffer stf = new StringBuffer("");
        String w05 = "";
        if(type==1)
            w05 = "W0517";
        if(type==2)
            w05 = "W0521";
        if(type==3)
            w05 = "W0523";
        try {
            stf.append("  update W05 set "+w05+"=(select a.username_count from ");
            stf.append("(select W0501,COUNT(username) as username_count  from  zc_expert_user where 1=1 " + sql.replace("w05.", "") + "and type = "
                    + type + " group by w0501) a ");
            stf.append(" where w05.W0501=a.W0501 )");
            if(StringUtils.isNotEmpty(sql))
            stf.append(" where 1=1 " + sql.replace("w05.", ""));
            dao.update(stf.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**
     * 获取【生成鉴定专家账号密码】界面学科组信息
     * @param isSelectAll：表格控件是否全选
     * @param idlist：选中或反选的数据
     * @return
     * @throws GeneralException
     */
    public ArrayList<HashMap<String, String>> getSubjects(String isSelectAll, ArrayList<MorphDynaBean> idlist) throws GeneralException {
		
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
//    		ArrayList<HashMap<String, String>> selList = new ArrayList<HashMap<String, String>>();
//    		selList = getSelectList(isSelectAll, idlist);//获取选中的数据
    		
    		/*boolean isHaveNoGroup = false;// 是否存在没有分配学科组的记录
			ArrayList<String> groupList = new ArrayList<String>();
			for(HashMap<String, String> sList : selList){
				String group_id = sList.get("group_id");
				if(!StringUtils.isEmpty(group_id)) {
					groupList.add(sList.get("group_id"));
				} else {
					isHaveNoGroup = true;
				}
			}
			
			*//** 获取学科组 *//*
			if(groupList.size() != 0){
				groupList  = new ArrayList<String>(new HashSet<String>(groupList));//去重
				StringBuilder subjectSql = new StringBuilder();//重新检索学科组名称
				subjectSql.append("select group_id, group_name from zc_subjectgroup ");
				subjectSql.append("where group_id in (");
				String value = "";
				for(String  lst: groupList){
					value += (lst+",");
				}
				value = value.substring(0, value.length()-1);
				subjectSql.append(value);
				subjectSql.append(" )");
				rs = dao.search(subjectSql.toString());
				while(rs.next()){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("group_id", rs.getString("group_id"));
					map.put("group_name", rs.getString("group_name"));
					list.add(map);
				}
			}
			
			if(isHaveNoGroup){*/
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("group_id", "0");
//				map.put("group_name", "没有分配学科组的");
				map.put("group_name", "需要分配的");
				list.add(map);
			/*}*/
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	
    	return list;
    }
    /**
     * 获取选中的人员信息
     * @param isSelectAll：表格控件是否全选
     * @param idlist：选中或反选的数据
     * @return
     * @throws GeneralException
     */
    public ArrayList<HashMap<String, String>> getSelectList(String isSelectAll, ArrayList<MorphDynaBean> idlist) throws GeneralException {
    	
    	ArrayList<HashMap<String, String>> selList = new ArrayList<HashMap<String, String>>();//选中数据
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
	    	/** 获取选中数据 */
			TableDataConfigCache catche = (TableDataConfigCache)this.userview.getHm().get("reviewFile");
			String sql = catche.getTableSql();// 直接获取页面数据
			String querySql = catche.getQuerySql();
			if(StringUtils.isNotBlank(querySql))
				sql="select * from ("+sql+") t where 1=1 "+querySql;
			rs = dao.search(sql);
			
			ArrayList<HashMap<String, String>> allList = new ArrayList<HashMap<String, String>>();//页面所有数据
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("w0501", rs.getString("w0501"));
				map.put("group_id", rs.getString("group_id"));
				map.put("w0301", rs.getString("w0301"));
				map.put("w0511", rs.getString("w0511"));
				map.put("w0555", rs.getString("w0555"));
				map.put("w0573", rs.getString("w0573"));
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
     * 《评审专家登陆帐号信息表》中写入数据
     * @param w0501：申报人主键序号ID 
     * @param w0301：会议编号
     * @param userPwdList：账号密码list
     * @throws GeneralException
     */
    @SuppressWarnings("unchecked")
	public void createExpertUser(String w0501, String w0301, ArrayList<HashMap<String, String>> userPwdList,int usetype) throws GeneralException {
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	
    	try {
			
    		/** 删除曾经数据 */
			String dSql = "delete from zc_expert_user where w0301=? and w0501=? and type=? ";
			ArrayList<String> dList = new ArrayList<String>();
			dList.add(w0301);
			dList.add(w0501);
			dList.add("3");
			dao.delete(dSql, dList);
    		
    		ArrayList<ArrayList<String>> insertList = new ArrayList<ArrayList<String>>();

    		StringBuffer buf = new StringBuffer();
    		buf.append(" insert into zc_expert_user ");
    		buf.append(" (user_id, group_id, W0301, W0501, username, password, state, type, W0101, description, role,usetype) ");
    		buf.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) ");
    		for(HashMap<String, String> map : userPwdList){
    			
    			ArrayList tmpList = new ArrayList();
    			
     			String username = map.get("content");//生成的用户名
     			String pwd = map.get("pasword");//生成的密码
     			
     			IDFactoryBean idf = new IDFactoryBean();
     			String user_id = idf.getId("zc_expert_user.user_id", "", conn);
     			tmpList.add(user_id);//user_id
     			tmpList.add(null);//group_id
     			tmpList.add(w0301);//W0301
     			tmpList.add(w0501);//W0501
     			tmpList.add(username);//username
     			tmpList.add(pwd);//password
     			tmpList.add(0);//state
     			tmpList.add(3);//type
     			tmpList.add(null);//W0101
     			tmpList.add(null);//description
     			tmpList.add(null);//role
     			tmpList.add(usetype);//账号类型
     			
 				insertList.add(tmpList);
    		}
    		dao.batchInsert(buf.toString(), insertList);
    		
    		
    	} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	
    }
}
