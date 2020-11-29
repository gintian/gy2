package com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ExportBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 获取职称人员工具类
 * @createtime 18/4/11
 * @author xus
 *
 */
public class OutPositionalStaffBo {

    // 基本属性
    private Connection conn = null;
    private UserView userview;
    
    private String w0301 = "";
    private int segment ;
    private int evaluationType ;
    
	/**
     * 构造函数
     * 
     * @param conn
     */
    public OutPositionalStaffBo(Connection conn, UserView userview, String w0301, int segment,
    		int evaluationType) {
    	super();
    	this.conn = conn;
    	this.userview = userview;
    	this.w0301 = w0301;
    	this.segment = segment;
    	this.evaluationType = evaluationType;
    }
    public OutPositionalStaffBo(Connection conn) {
        this.conn = conn;
    }
    public OutPositionalStaffBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
    public String getW0301() {
    	return w0301;
    }
    public void setW0301(String w0301) {
    	this.w0301 = w0301;
    }
    public int getSegment() {
		return segment;
	}
	public void setSegment(int segment) {
		this.segment = segment;
	}
	public int getEvaluationType() {
		return evaluationType;
	}
	public void setEvaluationType(int evaluationType) {
		this.evaluationType = evaluationType;
	}
	/**
     * 获取excel需要的list
     * @param w0301 会议ID
     * @param segment 聘委会/学科组/同行专家/二级单位/论文送审
     * @param evaluationType 审核/投票
     * @return
     * @throws Exception 
     */
    public ArrayList getOutExcelList(String w0301,int segment,int evaluationType,String encodeCateIds) throws Exception{
    	ArrayList outExcelList=new ArrayList();
    	try {
    		//1获取申报人list
    		List applyStaffList = new ArrayList();
    		if("".equals(encodeCateIds)){
    			throw new Exception(ResourceFactory.getProperty("zc_new.zc_reviewfile.selectGroup"));
    		}
    		String columns="";
    		if("noCateIds".equals(encodeCateIds)){
    			columns+=" is not null"; 
    		}else{
	    		String[] categoriesids =encodeCateIds.split(",");
	    		columns+=" in (";
	    		for(int i=0;i<categoriesids.length;i++){
	    			columns+="'"+PubFunc.decrypt(categoriesids[i])+"'";
	    			if(i<categoriesids.length-1){
	    				columns+=",";
	    			}
	    		}
	    		columns+=") ";
    		}
    		applyStaffList=getApplyStaffList(w0301,segment,columns);
    		int perso_count = 0;
    		// 导出审核账号的时候判断
    		if(evaluationType==1){
    			perso_count = getHavePersonInExportVerify(w0301, segment, columns);
    		}
    		if(applyStaffList.isEmpty()){
                //18/7/18 xus 如果状态为审核账号，提示：分组已启动，无法导出审核账号
                if(evaluationType==1 && perso_count > 0){
                    throw new Exception(ResourceFactory.getProperty("zc_new.zc_reviewfile.groupIsStarted"));
                }else
                    throw new Exception(ResourceFactory.getProperty("zc_new.zc_reviewfile.applicantNotExist"));
    		}
    		//2 通过不同的流程生成账号密码 type 2学科组 3评委会 4二级单位 5 同行专家      返回专家账号密码list 
    		if(generateAcPwd(w0301,segment,evaluationType,applyStaffList,columns))
    			//3 获取所有专家list bean  categoriesid username password
    			outExcelList=(ArrayList) getexpertList(w0301,segment,evaluationType,applyStaffList);
    		else
    			throw new Exception(ResourceFactory.getProperty("zc_new.zc_reviewfile.expertNotExist"));
		} catch (GeneralException e) {
			e.printStackTrace();
		}
    	
    	return outExcelList;
    }
    
    /**
     * 判断导出审核账号的，有申报人启动了和没有申报人导出提示清楚，上个方法没必要做判断，会导致里面业务复杂，容易出错
     * 这里查询不会消耗性能
     * @param columns
     * @return
     * @throws GeneralException 
     */
    private int getHavePersonInExportVerify(String w0301,int segment,String columns) throws GeneralException {
    	int count = 0;
    	RowSet rs=null;
    	ContentDAO dao = new ContentDAO(conn);
    	try{
    		String sql="select count(*) count_ from zc_personnel_categories zpc inner join zc_categories_relations zcr on zpc.categories_id=zcr.categories_id "
    				+ " where zpc.w0301=? and  zpc.review_links=? and zpc.categories_id "+columns;
    		List values=new ArrayList();
	    	values.add(w0301);
	    	values.add(segment);
	    	rs=dao.search(sql, values);
	    	while (rs.next()) {
	    		count = rs.getInt("count_");
	    	}
    	}catch(Exception e){
    		 e.printStackTrace();
             throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return count;
    }
    /**
     * 获取申报人list
     * @param w0301
     * @param segment
     * @return
     * @throws GeneralException
     */
    public List getApplyStaffList(String w0301,int segment,String columns) throws GeneralException{
    	List applyStaffList=new ArrayList();
    	RowSet rs=null;
    	try{
    		
    		String sql="select w05.w0501 w0501, zpc.categories_id categoriesid,w05.W0511 staffname,zpc.w0301 W0301,zcr.queue queue,zpc.name catename,zpc.approval_state state from zc_personnel_categories zpc,zc_categories_relations zcr,w05 where zpc.categories_id=zcr.categories_id and zcr.w0501=w05.w0501 and zpc.w0301=? and  zpc.review_links=? and zpc.categories_id "+columns;
    		//xus 审核账号只导出未启动的分组
    		if(evaluationType==1){
    			sql+=" and '0' = "+Sql_switcher.isnull("zpc.approval_state", "0");
    		}
    		List values=new ArrayList();
//    		String sql="select w05.w0501 w0501, zpc.categories_id categoriesid,w05.W0511 staffname,zpc.w0301 W0301,zcr.queue queue,zpc.name catename from zc_personnel_categories zpc,zc_categories_relations zcr,w05 where zpc.categories_id=zcr.categories_id and zcr.w0501=w05.w0501 and zpc.approval_state='1' and zpc.w0301=? and zpc.review_links=?";
	    	values.add(w0301);
	    	values.add(segment);
	    	ContentDAO dao = new ContentDAO(conn);
	    	rs=dao.search(sql, values);
	    	while (rs.next()) {
	    		LazyDynaBean bean=new LazyDynaBean();
	    		bean.set("categoriesid", rs.getString("categoriesid"));
	    		bean.set("staffname", rs.getString("staffname")==null?"":rs.getString("staffname"));
	    		bean.set("queue", rs.getInt("queue"));
	    		bean.set("catename", rs.getString("catename")==null?"":rs.getString("catename"));
	    		bean.set("w0501", rs.getString("w0501"));
	    		if(rs.getObject("state") == null){
	    			bean.set("state", "0");
	    		}else{
	    			bean.set("state", rs.getString("state"));
	    		}
	    		//xus 18/7/30 不导出分组为结束的账号
	    		if(!"2".equals(bean.get("state")))
	    		applyStaffList.add(bean);
	    	}
    	}catch(Exception e){
    		 e.printStackTrace();
             throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return applyStaffList;
    }
    /**
     * 获取分组名-申报人 Map
     * @param w0301 会议ID
     * @param segment 聘委会/学科组/同行专家/二级单位
     * @param evaluationType 审核/投票
     * @return
     * @throws GeneralException 
     */
    public Map getApplyStaffMap(List staffList) throws GeneralException{
    	Map staffMap=new HashMap();
    	HashMap<String,HashMap<Integer,String>> groupMap=new HashMap<String,HashMap<Integer,String>>();
    	HashMap<Integer,String> queueMap=new HashMap<Integer,String>();
    	for(Object obj:staffList){
    		LazyDynaBean bean=(LazyDynaBean) obj;
    		String groupid=(String) bean.get("categoriesid");
    		String staffname=(String) bean.get("staffname");
    		int queue=(Integer) bean.get("queue");
    		
    		if(groupMap.containsKey(groupid)&&queueMap.containsKey(queue)){
    			staffname=groupMap.get(groupid).get(queue)+staffname+"、";
    			queueMap=groupMap.get(groupid);
				queueMap.put(queue,staffname);
				groupMap.put(groupid, queueMap);
    		}else if(groupMap.containsKey(groupid)&&!queueMap.containsKey(queue)){
    			queueMap=groupMap.get(groupid);
				queueMap.put(queue,staffname+"、" );
				groupMap.put(groupid, queueMap);
    		}else if(!groupMap.containsKey(groupid)&&queueMap.containsKey(queue)){
    			queueMap=new HashMap();
				queueMap.put(queue,staffname+"、" );
				groupMap.put(groupid, queueMap);
    		}else if(!groupMap.containsKey(groupid)&&!queueMap.containsKey(queue)){
				queueMap.put(queue,staffname+"、" );
				groupMap.put(groupid, queueMap);
    		}
    		
    	}
    	for(Entry<String, HashMap<Integer, String>> entry : groupMap.entrySet()){
    		String value="";
    		for(Entry<Integer, String> e : entry.getValue().entrySet()){
    			String nameLine="";
    			nameLine=e.getValue().substring(0, e.getValue().length()-1);
    			value+=nameLine+"\r\n";
    		}
    		staffMap.put(entry.getKey(), value);
    	}
    	
    	return staffMap;
    }
    
    /**
     * 获取专家的list
     * @param w0301
     * @param segment 评审环节      1:聘委会 2：学科组 3：同行专家 4：二级单位 
     * @param evaluationType 1:投票  2：评分
     * @return
     * @throws GeneralException
     */
    public List getexpertList(String w0301,int segment,int evaluationType,List staffList) throws GeneralException{
    	List expertList=new ArrayList();
    	List totalexpertList=new ArrayList();
    	RowSet rs=null;
    	try{
    		String  wherePlusSql=categoriesid_Where_sql(staffList);
    		String sql="select distinct zeu.categories_id categoriesid,username,password,zpc.seq,zeu.group_id groupid from zc_expert_user zeu left join zc_personnel_categories zpc "
    				+ "on zeu.categories_id=zpc.categories_id where zeu.w0301=? and zeu.type=? and usetype=? and zeu.categories_id in "+wherePlusSql + " order by zpc.seq";
    		List values=new ArrayList();
    		values.add(w0301);
    		values.add(segment);
    		values.add(evaluationType);
    		ContentDAO dao= new ContentDAO(conn);
    		rs=dao.search(sql,values);
    		while(rs.next()){
    			LazyDynaBean bean=new LazyDynaBean();
    			bean.set("categoriesid", rs.getString("categoriesid"));
    			bean.set("username", rs.getString("username"));
    			bean.set("password", rs.getString("password"));
    			bean.set("groupid", rs.getString("groupid"));
    			expertList.add(bean);
    		}
    		Map staffMap=getApplyStaffMap(staffList);
    		
    		Map cateNameMap = new HashMap();
    		if(!staffList.isEmpty()){
    			for(Object obj:staffList){
    				LazyDynaBean bean=(LazyDynaBean) obj;
    				String categoriesid=bean.get("categoriesid").toString();
    				String catename=bean.get("catename").toString();
    				if(!cateNameMap.containsKey(categoriesid))
    					cateNameMap.put(categoriesid, catename);
    			}
    		}
    		
    		if(!expertList.isEmpty()){
    			String allproposer="";
    			String allGroupNames="";
    			List<String> cateidslist=new ArrayList<String>();
    			List newexpertList=new ArrayList();
    			//评委会和二级单位 要合并分组
    			if(segment==1||segment==4){
    				String categoriesid=((LazyDynaBean)expertList.get(0)).get("categoriesid").toString();
//    				newexpertList
    				for(Object obj:expertList){
    					LazyDynaBean bean=(LazyDynaBean) obj;
        				String oldcategoriesid=bean.get("categoriesid").toString();
        				if(categoriesid.equals(oldcategoriesid)){
        					newexpertList.add(bean);
        				}
        				if(!cateidslist.contains(oldcategoriesid)){
        					allproposer +=cateNameMap.get(oldcategoriesid)+":\r\n"+staffMap.get(oldcategoriesid)+"\r\n";
        					allGroupNames+=cateNameMap.get(oldcategoriesid)+"、";
        					cateidslist.add(oldcategoriesid);
        				}
    				}
    				allGroupNames=allGroupNames.substring(0, allGroupNames.length()-1);
    				expertList=newexpertList;
    			}
    			for(Object obj:expertList){
    				//bean 参数groupname  proposer username pwd dimensional
    				//* dataBean可选参数：content、fromRowNum、toRowNum、fromColNum、toColNum
    				LazyDynaBean bean=(LazyDynaBean) obj;
    				String categoriesid=bean.get("categoriesid").toString();
    				String username=bean.get("username").toString();
    				String pwd=bean.get("password").toString();
    				String groupid=bean.get("groupid").toString();
    				
    				String catename="";
    				String staffnames="";
    				if(segment==1||segment==4){
    					catename=allGroupNames;
    					staffnames=allproposer;
    				}else{
    					catename=cateNameMap.get(categoriesid).toString();
    					staffnames=staffMap.get(categoriesid).toString();
    				}
    				
    				
    				
    				LazyDynaBean newbean=new LazyDynaBean();
    				newbean.set("content", catename);
    				LazyDynaBean rowbean=new LazyDynaBean();
    				rowbean.set("groupname", newbean);
    				
    				newbean=new LazyDynaBean();
    				newbean.set("content", staffnames);
    				rowbean.set("proposer", newbean);
    				
    				newbean=new LazyDynaBean();
    				newbean.set("content", username);
    				rowbean.set("username", newbean);
    				
    				newbean=new LazyDynaBean();
    				newbean.set("content", pwd);
    				rowbean.set("pwd", newbean);
    				rowbean.set("groupid", groupid);
    				
    				totalexpertList.add(rowbean);
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return totalexpertList;
    }
    
    /**
     * 根据不同的流程生成用户名密码
     * @param w0301
     * @param segment
     * @param evaluationType
     * @return
     * @throws GeneralException 
     */
    public boolean generateAcPwd(String w0301,int segment,int evaluationType,List applyStaffList,String columns) throws GeneralException{
    	boolean createSuccess=false;
    	//1: 聘委会2：学科组 3：:同行专家4：二级单位 5 论文送审
    	List generateAcPwdList=new ArrayList();
    	RowSet rs=null;
    	ContentDAO dao = new ContentDAO(conn);
    	try{
    		//step:1 获取启动分组的专家数Map(categories_id,count)     
    		Map<String,Integer> categoriesidToCountMap = GetCategoriesidToCountMap(w0301, segment,columns);
    		//step:2  循环categoriesidToCountMap 判断是否为随机账号
    		//xus 判断是否为随机账号的方法 按照郝树林那边来
    		ReviewMeetingPortalBo rmpb = new ReviewMeetingPortalBo(this.userview, conn);
    		LazyDynaBean usertypebean =rmpb.getExpertSetting(w0301);
    		String usertypestate=usertypebean.get("usertype_"+segment).toString();
    		for (Entry<String, Integer> entry : categoriesidToCountMap.entrySet()) { 
    			//判断是否为随机账号
//    			if(entry.getValue()>0){
    			if("1".equals(usertypestate)){
    				//step:3随机账号
    				//获取所有随机专家账号
    				List groupAllExpertList=getRandomAllExpertList(entry.getKey(), entry.getValue());
    				if(groupAllExpertList.size()==0)
    					return false;
    				//当前分组内的所有申请人  生成随机专家账号(多退少补)
        			for(Object obj:applyStaffList){
        				LazyDynaBean bean=(LazyDynaBean)obj;
        				String categoriesid=bean.get("categoriesid").toString();
        				if(!entry.getKey().equals(categoriesid))
        					continue;
        				String w0501=bean.get("w0501").toString();
        				String state=1==evaluationType?"1":bean.get("state").toString();
        				createApplyRandomUser(w0301, w0501,  segment, evaluationType,categoriesid , entry.getValue(),state,groupAllExpertList);
        			}
    			}else{
    				//step:4手动生成
    				//获取当前分组中的 所有专家
    				List groupAllExpertList=getGroupAllExpertList(w0301,segment, evaluationType, entry.getKey());
    				if(groupAllExpertList.size()==0)
    					return false;
    				for(Object obj:applyStaffList){
    					LazyDynaBean bean=(LazyDynaBean)obj;
    					String categoriesid=bean.get("categoriesid").toString();
    					if(!entry.getKey().equals(categoriesid))
    						continue;
    					String w0501=bean.get("w0501").toString();
    					String state=1==evaluationType?"1":bean.get("state").toString();
    					createExistApplyUser(w0301, w0501, segment, evaluationType,categoriesid,state,groupAllExpertList );
    				}
    			}

    		}
			if(this.evaluationType==3){
    			ArrayList<String> list=new ArrayList<String>();
    			list.addAll(categoriesidToCountMap.keySet());
				this.addKh_Table(list);
			}
    		createSuccess=true;
    	}catch(Exception e){
    		 e.printStackTrace();
             throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return createSuccess;
    }
    
    /**
     * 
     * @param w0301
     * @param segment
     * @return map<categories_id,count>
     * @throws GeneralException 
     */
private Map GetCategoriesidToCountMap(String w0301, int segment,String columns) throws GeneralException {
		Map resultMap=new HashMap<String, Integer>();
		RowSet rs=null;
    	try{
    		ContentDAO dao=new ContentDAO(conn);
	    	String sql;
	    	List values=new ArrayList();
	    	int count = 0;
	    	if(segment==1||segment==4){
	    		//评委会、二级单位
	    		String numCol="";
	    		if(segment==1){
	    			numCol="W0315";
	    		}else{
	    			numCol="W0323";
	    		}
	    		sql ="select "+numCol+" count from w03 where W0301=? and W0321=? ";
	    		values.add(w0301);
	    		values.add("05");
	    		rs=dao.search(sql, values);
	    		if(rs.next()){
	    			count=rs.getInt("count");
	    		}
	    		sql="select categories_id categoriesid,expertnum count from zc_personnel_categories where  w0301=? and categories_id "+columns;
	    		//xus 审核账号只导出未启动的分组
	    		if(evaluationType==1){
	    			sql+=" and '0' = "+Sql_switcher.isnull("approval_state", "0");
	    		}
//	    		rs=dao.search(sql);
//	    		sql="select categories_id categoriesid,expertnum count from zc_personnel_categories where w0301=? and review_links=? and approval_state = ? ";
    			values=new ArrayList();
	    		values.add(w0301);
//	    		values.add(segment);
//	    		values.add("1");
	    		rs=dao.search(sql, values);
	    		while(rs.next()){
	    			resultMap.put(rs.getString("categoriesid"),count);
	    		}
	    		
	    	}else if(segment==2||segment==3){
	    		//学科组、同行专家
	    		sql="select categories_id categoriesid,expertnum count from zc_personnel_categories where categories_id "+columns;
	    		rs=dao.search(sql);
//	    		sql="select categories_id categoriesid,expertnum count from zc_personnel_categories where w0301=? and review_links=? and approval_state = ? ";
//    			values=new ArrayList();
//	    		values.add(w0301);
//	    		values.add(segment);
//	    		values.add("1");
//	    		rs=dao.search(sql, values);
	    		while(rs.next()){
	    			resultMap.put(rs.getString("categoriesid"), rs.getInt("count"));
	    		}
	    	}else{
	    		
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
		return resultMap;
	}
    
	/**生成单个申请人对应的专家账号表中的 用户名密码
     * 
     * @param w0301
     * @param w0501
     * @param groupid  学科组id 没有应传 null
     * @param type		1：内部评委	  2：学科组成员      3：外部鉴定专家  4:二级单位 
     * @param usetype  1|null：材料评审  2：投票
     * @param categoriesid
     * @param generateAcPwdList
     * @throws GeneralException
     */
    public void createExistApplyUser(String w0301,String w0501,int type ,int usetype,String categoriesid,String state,List groupAllExpertList) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs=null;
    	try {
    		//查询出当前申请人 的专家账号数据
    		ArrayList<LazyDynaBean> beanList = new ArrayList<LazyDynaBean>();
    		List<String> W0101List=new ArrayList<String>();
    		String selSql = "select W0101 W0101 from zc_expert_user where w0301=?  and type=? and usetype=? and categories_id=? and W0501 = ?";
    		ArrayList values = new ArrayList();
    		values.add(w0301);
    		values.add(type);
    		values.add(usetype);
    		values.add(categoriesid);
    		values.add(w0501);
    		rs=dao.search(selSql, values);
    		while(rs.next()){
    			W0101List.add(rs.getString("W0101"));
    		}
    		
    		if(W0101List.size()==groupAllExpertList.size()){
    			return ;
//    		}else if(W0101List.size()<groupAllExpertList.size()){
    		}else{
    			StringBuilder insertSql = new StringBuilder();
    			insertSql.append("insert into zc_expert_user ");
    			insertSql.append("(user_id,group_id,W0301,W0501,username,password,state,type,W0101,description,role,usetype,categories_id) ");
    			insertSql.append("values ");
    			insertSql.append("(?,?,?,?,?,?,?,?,?,?,?,?,?)");
    			List iList=new ArrayList();
    			for(int i=0;i< groupAllExpertList.size();i++){
    				LazyDynaBean bean=(LazyDynaBean) groupAllExpertList.get(i);
    				String W0101= bean.get("W0101").toString();
    				String role= bean.get("role").toString();
//    				if(!W0101List.contains(W0101)){
    					String username ="";
    					String pwd="";
    					String groupid="";
    					if(type==2){
    						groupid= bean.get("groupid").toString() ;
    						if(bean.get("username")!=null&&!"null".equals(bean.get("username").toString())&&!"".equals(bean.get("username").toString())){
	    						username= bean.get("username").toString() ;
	    						pwd= bean.get("password").toString() ;
    						}else{
    							int count=0;
			    				String selectSql="select  count(username) count,username,password,group_id from zc_expert_user where type = ?  and W0101= ? and W0301 = ? and usetype = ? group by username,password,group_id ";
			    				values=new ArrayList();
			    				values.add(type);
			    				values.add(W0101);
			    				values.add(w0301);
			    				values.add(usetype);
			    				rs=dao.search(selectSql,values);
			    				while(rs.next()){
			    					if(rs.getInt("count")>count){
			    						count=rs.getInt("count");
			    						username = rs.getString("username") ;//生成的用户名
				    					pwd = rs.getString("password") ;
			    					}
			    				}
			    				if(count==0){
			    					ArrayList<HashMap<String, String>>generateAcPwdList = (ArrayList<HashMap<String, String>>)GenerateAcPwBo.generate(1, new ContentDAO(conn));
			    					HashMap<String,String> map=generateAcPwdList.get(0);
			    					username = map.get("content");//生成的用户名
			    					pwd = map.get("pasword");
			    				}
    						}
		    			}else if(type==1||type==4){
		    				
		    				int count=0;
		    				String selectSql="select  count(username) count,username,password from zc_expert_user where type = ?  and W0101= ? and w0301 = ? and usetype=? group by username,password ";
		    				values=new ArrayList();
		    				values.add(type);
		    				values.add(W0101);
		    				values.add(w0301);
		    				values.add(usetype);
		    				rs=dao.search(selectSql,values);
		    				while(rs.next()){
		    					if(rs.getInt("count")>count){
		    						count=rs.getInt("count");
		    						username = rs.getString("username") ;//生成的用户名
			    					pwd = rs.getString("password") ;
		    					}
		    				}
		    				if(count==0){
		    					ArrayList<HashMap<String, String>>generateAcPwdList = (ArrayList<HashMap<String, String>>)GenerateAcPwBo.generate(1, new ContentDAO(conn));
		    					HashMap<String,String> map=generateAcPwdList.get(0);
		    					username = map.get("content");//生成的用户名
		    					pwd = map.get("pasword");
		    				}
		    				
		    			}
    					ArrayList vList = new ArrayList();
    					IDFactoryBean idf = new IDFactoryBean();
    					String user_id = idf.getId("zc_expert_user.user_id", "", this.conn);
    					vList.add(user_id);
    					if("".equals(groupid))
    						vList.add(null);
    					else
    						vList.add(groupid);
    					vList.add(w0301);
    					vList.add(w0501);
    					vList.add(username);
    					vList.add(pwd);
    					vList.add(Integer.parseInt(state));
    					vList.add(type);
    					vList.add(W0101);
    					vList.add(null);
    					vList.add(role);
    					vList.add(usetype);
    					vList.add(categoriesid);
    					iList.add(vList);
//    				}
	    				
    			}
    			String delSql="delete zc_expert_user where w0301=? and categories_id=? and type=? and usetype=? and W0501=?";
    			ArrayList newvalues = new ArrayList();
    			newvalues.add(w0301);
    			newvalues.add(categoriesid);
    			newvalues.add(type);
    			newvalues.add(usetype);
    			newvalues.add(w0501);
    			dao.delete(delSql, newvalues);
	 			dao.batchInsert(insertSql.toString(), iList);
    			
    		}
    	} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }

    /**生成单个申请人对应的随机账号 多退少补
     * 
     * @param w0301
     * @param w0501
     * @param groupid  学科组id 没有应传 null
     * @param type		1：内部评委	  2：学科组成员      3：外部鉴定专家
     * @param usetype  1|null：材料评审  2：投票
     * @param categoriesid
     * @param neededExperCount 需要生成随机专家账号数
     * @throws GeneralException
     */
    @SuppressWarnings("unchecked")
	public void createApplyRandomUser(String w0301,String w0501,int type,int usetype,String categoriesid,int neededExperCount,String state,List groupAllExpertList) throws GeneralException{
    	ArrayList<HashMap<String, String>> generateAcPwdList;
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs=null;
    	try {
    		//1  查出当前 当前申请人所有的专家账号
    		String selSql="select count(*) num from zc_expert_user  where categories_id=? and type=? and usetype=? and w0301=? and w0501=?";
    		List values=new ArrayList();
    		values.add(categoriesid);
			values.add(segment);
			values.add(evaluationType);
			values.add(w0301);
			values.add(w0501);
			rs=dao.search(selSql,values);
			int count=0;
			if(rs.next()){
				count=rs.getInt("num");
			}
			//库中存在的账号比需要生成的账号多 或者相等 则不生成账号（多不退 少补）
//			if(count>=neededExperCount)
			if(count==neededExperCount)
				return;
    		//2  删除 当前申请人所有的专家账号
    		String dSql="delete from zc_expert_user  where categories_id=? and type=? and usetype=? and w0301=? and w0501=?";
			values=new ArrayList();
    		values.add(categoriesid);
			values.add(segment);
			values.add(evaluationType);
			values.add(w0301);
			values.add(w0501);
			dao.delete(dSql, values);
			
			//3 批量插入专家账号
			ArrayList<ArrayList<String>> insertList = new ArrayList<ArrayList<String>>();
			StringBuffer buf = new StringBuffer();
			buf.append(" insert into zc_expert_user ");
			buf.append(" (user_id, group_id, W0301, W0501, username, password, state, type, W0101, description, role,usetype,categories_id) ");
			buf.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?) ");
			for(Object obj : groupAllExpertList){
				LazyDynaBean bean=(LazyDynaBean)obj;
				String username = bean.get("username").toString();//生成的用户名
	 			String pwd = bean.get("password").toString();//生成的密码
	 			
	 			ArrayList tmpList = new ArrayList();
	 			
	 			IDFactoryBean idf = new IDFactoryBean();
	 			String user_id = idf.getId("zc_expert_user.user_id", "", conn);
	 			tmpList.add(user_id);//user_id
	 			tmpList.add(null);
	 			tmpList.add(w0301);//W0301
	 			tmpList.add(w0501);//W0501
	 			tmpList.add(username);//username
	 			tmpList.add(pwd);//password
	 			tmpList.add(Integer.parseInt(state));//state
	 			tmpList.add(type);//type
	 			tmpList.add(null);//W0101
	 			tmpList.add(null);//description
	 			tmpList.add(null);//role
	 			tmpList.add(usetype);//账号类型
	 			tmpList.add(categoriesid);//会议申报人员分组id
	 			
				insertList.add(tmpList);
			}
			dao.batchInsert(buf.toString(), insertList);
//    		// 查出随机账号数量  
//    		ArrayList<String> useridList = new ArrayList<String>();
//    		String selSql = "select user_id userid from zc_expert_user where w0301=? and w0501=? and type=? and usetype=? and categories_id=?";
//    		ArrayList values = new ArrayList();
//    		values.add(w0301);
//    		values.add(w0501);
//    		values.add(type);
//    		values.add(usetype);
//    		values.add(categoriesid);
//    		rs=dao.search(selSql, values);
//			while(rs.next()){
//				useridList.add(rs.getString("userid"));
//			}
//    		//判断需要生成的随机账号数量 与表中存在的随机账号数量 是否一致， 多退少补
//			if(neededExperCount==useridList.size())
//				return;
//			if(neededExperCount>useridList.size()){
//				//少补
//				int count=neededExperCount-useridList.size();
//				generateAcPwdList = (ArrayList<HashMap<String, String>>)GenerateAcPwBo.generate(count, new ContentDAO(conn));
//				ArrayList<ArrayList<String>> insertList = new ArrayList<ArrayList<String>>();
//				
//				StringBuffer buf = new StringBuffer();
//				buf.append(" insert into zc_expert_user ");
//				buf.append(" (user_id, group_id, W0301, W0501, username, password, state, type, W0101, description, role,usetype,categories_id) ");
//				buf.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?) ");
//				for(HashMap<String, String> map : generateAcPwdList){
//					ArrayList tmpList = new ArrayList();
//					
//		 			String username = map.get("content");//生成的用户名
//		 			String pwd = map.get("pasword");//生成的密码
//		 			
//		 			IDFactoryBean idf = new IDFactoryBean();
//		 			String user_id = idf.getId("zc_expert_user.user_id", "", conn);
//		 			tmpList.add(user_id);//user_id
//		 			tmpList.add(null);
//		 			tmpList.add(w0301);//W0301
//		 			tmpList.add(w0501);//W0501
//		 			tmpList.add(username);//username
//		 			tmpList.add(pwd);//password
//		 			tmpList.add(1);//state
//		 			tmpList.add(type);//type
//		 			tmpList.add(null);//W0101
//		 			tmpList.add(null);//description
//		 			tmpList.add(null);//role
//		 			tmpList.add(usetype);//账号类型
//		 			tmpList.add(categoriesid);//会议申报人员分组id
//		 			
//					insertList.add(tmpList);
//				}
//				dao.batchInsert(buf.toString(), insertList);
//			}else{
//				//多退
//				int count=useridList.size()-neededExperCount;
//				String userids="";
//				for(int i=0;i<count;i++){
//					userids+=useridList.get(useridList.size()-1)+",";
//				}
//				userids=userids.substring(0, userids.length()-1);
//				String dSql="delete from zc_expert_user  where 1=? and user_id in ( "+userids+" ) ";
//				// 删除曾经数据 
//				ArrayList dList = new ArrayList();
//				dList.add(1);
//				dao.delete(dSql, dList);
//			}
    	} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
    }
    
    /**
     * 获取申请人所在的分组 拼成where条件
     * @param staffList
     * @return
     */
    public String categoriesid_Where_sql(List staffList){
    	String  wherePlusSql="";
		List<String> categoriesids=new ArrayList<String>();
		if(!staffList.isEmpty()){
			for(Object obj:staffList){
				LazyDynaBean bean=(LazyDynaBean) obj;
				String categoriesid=bean.get("categoriesid").toString();
				if(!categoriesids.contains(categoriesid))
					categoriesids.add(categoriesid);
			}
		}
		
		if(!categoriesids.isEmpty()){
			wherePlusSql += "(";
			String columns="";
			for(String categoriesid:categoriesids){
				columns +="'" + categoriesid + "' ,";
			}
			columns=columns.substring(0, columns.length()-1);
			wherePlusSql += columns;
			wherePlusSql += ")";
		}
    	
    	return wherePlusSql;
    }
    
    public Map GetSubjectGroupIdToNameMap() throws GeneralException{
    	Map returnMap=new HashMap();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs=null;
    	try {
    		String sql="select group_id groupid,group_name name from zc_subjectgroup";
    		rs=dao.search(sql);
    		while(rs.next()){
    			returnMap.put(rs.getString("groupid"), rs.getString("name"));
    		}
    	} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	return returnMap;
    }
    /**
     * 获取随机账号专家的账号密码
     * @param categoriesid
     * @param count
     * @return
     */
    public List getRandomAllExpertList(String categoriesid,int count){
    	List groupAllExpertList=new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs=null;
    	try {
    		String sql="";
    		if(segment ==1||segment==4){
    			sql="select  username,password from zc_expert_user where  type=? and usetype=? and w0301=? group by username ,password";
    		}else{
    			sql="select  username,password from zc_expert_user where  categories_id=? and type=? and usetype=? and w0301=? group by username ,password";
    		}
    		List values=new ArrayList();
    		if(!(segment ==1||segment==4))
    			values.add(categoriesid);
			values.add(segment);
			values.add(evaluationType);
			values.add(w0301);
			rs=dao.search(sql, values);
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("username", rs.getString("username"));
				bean.set("password", rs.getString("password"));
				groupAllExpertList.add(bean);
			}
			if(groupAllExpertList.size()==count)
				return groupAllExpertList;
			if(groupAllExpertList.size()>count){
				//多退
				for(int i=groupAllExpertList.size()-1;i>=count;i--){
					groupAllExpertList.remove(i);
				}
			}else{
				//少补
				int neededExperCount=count-groupAllExpertList.size();
				List<HashMap<String, String>> generateAcPwdList = (ArrayList<HashMap<String, String>>)GenerateAcPwBo.generate(neededExperCount, new ContentDAO(conn));
				for(HashMap<String, String> map : generateAcPwdList){
					ArrayList tmpList = new ArrayList();
		 			String username = map.get("content");//生成的用户名
		 			String pwd = map.get("pasword");//生成的密码
		 			LazyDynaBean bean = new LazyDynaBean();
					bean.set("username", username);
					bean.set("password", pwd);
					groupAllExpertList.add(bean);
				}
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return groupAllExpertList;
    }
    public List getGroupAllExpertList(String w0301,int segment ,int usetype,String categoriesid){
    	List groupAllExpertList=new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs=null;
    	try {
    		String sql="";
    		List values=new ArrayList();
    		
    		if(segment==1||segment==4){
    			//评委会、二级单位
    			String committeeid="";
    			if(segment==1){
    				committeeid="w03.committee_id";
    			}else{
    				committeeid="w03.sub_committee_id";
    			}
    			//flag:1:聘任
    			sql="select zje.W0101 W0101,zje.role role from zc_judgingpanel_experts zje,w03 where ("+ Sql_switcher.today() +" between start_date and end_date or end_date is null) and zje.committee_id="+committeeid+" and w03.W0301=? and zje.flag =1";
    			values.add(w0301);
    		}else if(segment==2){
    			//学科组
//    			sql="select W0101 W0101,role role,group_id from zc_expert_user where  categories_id=? and type=? ";
    			sql="select " + Sql_switcher.isnull("zeu.W0101", "''") + " W0101,zeu.role role,zpc.group_id group_id,zeu.username username,zeu.password password from zc_expert_user zeu,zc_personnel_categories zpc where zeu.group_id=zpc.group_id  and   zpc.categories_id=? and zeu.type=? ";
    			sql+="and zeu.usetype is null ";
    			sql+=" and zeu.w0301=? and zpc.w0301=? group by zeu.w0101 ,zeu.role,zpc.group_id,zeu.username,zeu.password";
    			values.add(categoriesid);
    			values.add(segment);
//    			values.add(usetype);
    			values.add(w0301);
    			values.add(w0301);
    		}
			rs=dao.search(sql, values);
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("W0101", rs.getString("W0101"));
				bean.set("role", rs.getObject("role")==null?"":rs.getString("role"));
				if(segment==2){
					bean.set("groupid", rs.getString("group_id"));
					if(rs.getObject("username") != null){
						bean.set("username", rs.getString("username"));
						bean.set("password", rs.getString("password"));
					}
				}
				groupAllExpertList.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return groupAllExpertList;
    };
    /**
	 * 
	 * @param w0301
	 * @param reviewPersonIds
	 * 			当前申报人主键序号ID 
	 * @param usetype
	 * 			导出的帐号类型 1 = 查看帐号 2 = 投票帐号
	 * @throws Exception
	 */
	public String exportReviewAccounts(String w0301,ArrayList outExcelList,String usetype, String type,Map subjectGroupIdToNameMap)throws Exception{
		ExportBo exBo = new ExportBo(conn, userview);
		ExportExcelUtil excelUtil = new ExportExcelUtil(conn);// 实例化导出Excel工具类
		excelUtil.setRowHeight((short)600);
		excelUtil.setProtect(true);//是否启用锁定页面,先启用，才能设置只读
		String excelName = "";
		if("1".equals(type)){
			excelName = JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT;
		}else if("3".equals(type)){
			excelName = JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT;
		}else if("4".equals(type)){
			excelName = JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT;
		}else if("2".equals(type)){
			excelName = JobtitleUtil.ZC_MENU_SUBJECTSSHOWTEXT;
		}
		ArrayList everyGroupOutExcelList=new ArrayList();
		for(Object obj:outExcelList){
			LazyDynaBean bean=(LazyDynaBean) obj;
			LazyDynaBean newbean = new LazyDynaBean();
			newbean.set("groupname", bean.get("groupname"));
			newbean.set("proposer", bean.get("proposer"));
			newbean.set("username", bean.get("username"));
			newbean.set("pwd", bean.get("pwd"));
			everyGroupOutExcelList.add(newbean);
		}
		if(!everyGroupOutExcelList.isEmpty())
			exportSinglePage(w0301,everyGroupOutExcelList,usetype,type,excelName,exBo,excelUtil);
	
		String fileName = "";
		if("1".equals(usetype)){
			fileName = ResourceFactory.getProperty("zc_new.zc_reviewfile.newverifyAccountPwd")+"_" + userview.getUserName() +".xls";// 材料评审
		}else if("2".equals(usetype)){
			fileName = ResourceFactory.getProperty("zc_new.zc_reviewfile.newvoteAccountPwd")+"_" + userview.getUserName() +".xls";// 投票
		}else{
			fileName = ResourceFactory.getProperty("zc_new.zc_reviewfile.newscoreAccountPwd")+"_" + userview.getUserName() +".xls";// 打分
		}
//		fileName = ResourceFactory.getProperty("zc_new.zc_reviewfile.voteAccountPwd")+"_" + userview.getUserName() +".xls";// 投票
		excelUtil.exportExcel(fileName);// 导出表格
		return fileName;
	}
	
	/**
	 * 导出单页excel
	 * @param w0301
	 * @param outExcelList
	 * @param usetype
	 * @param type
	 * @param excelName
	 * @param exBo
	 * @param excelUtil
	 * @throws Exception
	 */
	private void exportSinglePage(String w0301,ArrayList outExcelList,String usetype, String type,String excelName,ExportBo exBo,ExportExcelUtil excelUtil)throws Exception{
		
		
		ArrayList<LazyDynaBean> firstHeadList = exBo.getNewAllSheetHeadList();
		ArrayList<LazyDynaBean> mergeCellList=new ArrayList();
		String privName="";
		ArrayList<Integer> indexList=new ArrayList<Integer>();
//		HSSFPatriarch patriarch=excelUtil.getSheet().createDrawingPatriarch();
		for(int i=0;i<outExcelList.size();i++){
			LazyDynaBean bean=(LazyDynaBean) outExcelList.get(i);
			String currentName = ((LazyDynaBean)bean.get("groupname")).get("content").toString();
			
			if(!privName.equals(currentName)){
				privName=currentName;
				indexList.add(i+1);
			}
		}
		indexList.add(outExcelList.size()+1);
		for(int i=0;i<indexList.size()-1;i++){
			int fromRowNum=indexList.get(i);
			int toRowNum=indexList.get(i+1)-1;
			LazyDynaBean groupbean=new LazyDynaBean();
			groupbean.set("fromRowNum", fromRowNum);
			groupbean.set("toRowNum", toRowNum);
			groupbean.set("fromColNum", 0);
			groupbean.set("toColNum", 0);
			mergeCellList.add(groupbean);
			
			LazyDynaBean namebean=new LazyDynaBean();
			namebean.set("fromRowNum", fromRowNum);
			namebean.set("toRowNum", toRowNum);
			namebean.set("fromColNum", 1);
			namebean.set("toColNum", 1);
			mergeCellList.add(namebean);
		}
		
		
		excelUtil.exportExcel(excelName, mergeCellList,firstHeadList, outExcelList, null,0);
		
//		HSSFPatriarch patriarch=excelUtil.getSheet().createDrawingPatriarch();
//		for(int i=0;i<outExcelList.size();i++){
//			LazyDynaBean bean=(LazyDynaBean) outExcelList.get(i);
//			String username = ((LazyDynaBean)bean.get("username")).get("content").toString();
//			String pwd = ((LazyDynaBean)bean.get("pwd")).get("content").toString();
//			
//			byte[] outstream=exBo.getdimensional(username,pwd);
//			HSSFClientAnchor anchor=new HSSFClientAnchor(5,5,0,0,(short)4,i+1,(short)5,i+2);
//			patriarch.createPicture(anchor,excelUtil.getWb().addPicture(outstream, HSSFWorkbook.PICTURE_TYPE_PNG));  
//			anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
//			
//		}
	}

	/**
	 * 生成评分人账号时 生成考核评分表
	 * @param categories_id 分组id
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 16:35 2018/5/5
	 */
	private void addKh_Table(ArrayList<String> categories_id) throws GeneralException {
		try{
			StartReviewBo srbo=new StartReviewBo(this.conn,this.userview);
			ArrayList<String> templates = srbo.getKh_Template_Ids(w0301,String.valueOf(this.segment));
			String Relation_id = "1_" + w0301 +"_"+ String.valueOf(this.segment);//考核计划标识职称评审格式设置为模块ID_评审会议ID_环节ID
			srbo.delUnnecessaryMainbody(Relation_id,w0301,String.valueOf(this.segment));
			//添加的规则是1.考核对象表：一个考核对象对应一个模板2.考核主题表：一个考核对象对应一个考核主体对应一个考核模板
			srbo.doInsertKhObjectTable(templates,categories_id,Relation_id);//申报人添加到kh_object考核对象表
			srbo.doInsertKhMainbodyTable(categories_id,Relation_id);//评审人添加到kh_mainbody考核主体表
		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}