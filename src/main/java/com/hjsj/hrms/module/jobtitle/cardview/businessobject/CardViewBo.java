package com.hjsj.hrms.module.jobtitle.cardview.businessobject;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;

/**
 * 资格评审_展示
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 */
public class CardViewBo {
	
	// 基本属性
	private Connection conn = null;

	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public CardViewBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	
    /**
	 * 取得人员信息
	 * usetype://1|null：材料评审  2：投票,3打分
	 * @return
	 */
    @SuppressWarnings({ "unchecked", "static-access" })
	public ArrayList getPersoninfo(String queue,String categories_id,String usetype) throws GeneralException {
    	
    	ArrayList infoList = new ArrayList();
    	ArrayList<String> list = new ArrayList<String>();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	RowSet rs1 = null;
		try {
			StringBuilder sql = new StringBuilder(); 
	    	sql.append("select ZU.username,ZU.state,ZU.type,ZD.expert_state,ZD.approval_state,ZU.usetype,W.* ");
	    	sql.append(" from W05 W,zc_expert_user ZU ");
	    	sql.append(" LEFT JOIN zc_data_evaluation ZD  ");
	    	sql.append(" on ZU.W0301=ZD.W0301 and ZU.W0501=ZD.W0501 and ZU.username=ZD.username ");
	    	sql.append(" INNER join zc_categories_relations ZC on ZC.w0501=ZU.W0501 and ZU.categories_id = ZC.categories_id ");
	    	sql.append(" INNER join zc_personnel_categories Zpc on Zpc.categories_id = ZC.categories_id where ");
	    	if(StringUtils.isNotBlank(categories_id)) {
	    		sql.append(" ZC.categories_id=? and ");
	    		list.add(PubFunc.decrypt(categories_id));
	    	}
	    	sql.append(" zpc.approval_state = ? and ");
	    	if("1".equals(usetype)) {
	    		list.add("0");//审核账号只能看到暂停的
	    	}else {
	    		list.add("1");//只有已经启动的才能看到，起草，暂停，结束都不能看
	    	}
	    	sql.append(" queue=? and ZU.username=? and W.W0301=ZU.W0301 and W.W0501=ZU.W0501 and ZU.state<>0 ");

	    	//因程序改版，申报人顺序不应该在按照上会材料的栏目设置指标顺序排序，改为安分组和组内顺序排序 haosl upda2019.12.17
	    	sql.append(" order by Zpc.seq,ZC.seq");
	    	/*ArrayList<String> sortItemList = this.getSchemeSettingSortItemList();//栏目设置的指标排序，如果栏目设置没有设置公有方案则默认按w0501排序。
	    	if(sortItemList.size() > 0){
	    		for(String sortItem : sortItemList){
	    			String[] array = sortItem.split(":");
					String itemid = array[0];
					String type = array[1];
					FieldItem item = DataDictionary.getFieldItem(itemid, "w05");
					if(item == null){
						continue ;
					}
	    			sql.append(" W."+itemid+" "+(Integer.parseInt(type)==1?"asc":"desc")+",");
	    		}
	    	}else {
	    		sql.append(" W.w0501 ,");
	    	}
	    	sql.deleteCharAt(sql.length()-1);*/
	    	
	    	list.add(queue);
	    	list.add(this.userview.getUserName());
			rs = dao.search(sql.toString(), list);
			
			int nameId = 0;
			while (rs.next()) {
				String type = rs.getString("type");// 帐号类型    1：内部评委  2：学科组成员 3：外部鉴定专家 4:学院任聘组
				String expert_state = rs.getString("expert_state");// 专家状态标识    1:待审 2：已审
				if(!StringUtils.isEmpty(expert_state)) {
					/*if("3".equals(expert_state)) {//已提交。已提交的记录不显示 chent 20170428
						continue ;
					} else */if("1".equals(expert_state)){
						expert_state = "待审";
					} else if("2".equals(expert_state)) {
						expert_state = "已审";
					}
				} else {
					expert_state = "待审";
				}
					
				
				String approval_state = rs.getString("approval_state");// 审批状态    1：同意  2：不同意  3：弃权
				if(!StringUtils.isEmpty(approval_state)) {
					if("1".equals(approval_state)){
						approval_state = "同意";
					}else if("2".equals(approval_state)){
						approval_state = "不同意";
					}else if("3".equals(approval_state)){
						approval_state = "弃权";
					}
				} else {
					approval_state = "";
				}
				
				String w0511 = "";
				String imgPath = "";
				String w0533 = "";
				String questionid = "";//问卷号
				HashMap map = new HashMap();//map中数据与前台store关联
				
				// 现注释掉，全部显示。原逻辑：内部评委、学科组不显示图片、姓名。 
//				if("1".equals(type)) {// 1：内部评委
					w0511 = rs.getString("w0511");
					PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
					String imgpath = photoImgBo.getPhotoPathLowQuality(rs.getString("w0503"), rs.getString("w0505"));
					imgPath = imgpath;
					w0533 = approval_state;
					questionid = rs.getString("w0539");//内部评审问卷计划号
//				}
//				else if("2".equals(type)) { // 2：学科组成员
//					//nameId++;
//					//w0511 =  "申报人"+String.valueOf(nameId);
//					//imgPath = "/images/photo.jpg";
//					w0511 = rs.getString("w0511");
//					PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
//					String imgpath = photoImgBo.getPhotoPathLowQuality(rs.getString("w0503"), rs.getString("w0505"));
//					imgPath = imgpath;
//					w0533 = approval_state;
//					questionid = rs.getString("w0539");//内部评审问卷计划号
//				} 
//				else if("3".equals(type)) { // 3：外部鉴定专家
//					nameId++;
//					w0511 =  "申报人"+String.valueOf(nameId);
//					imgPath = "/images/photo.jpg";
//					w0533 = expert_state;
//					questionid = rs.getString("w0541");//专家鉴定问卷计划号
//				} 
				
				map.put("w0301", rs.getString("w0301"));//会议id
				map.put("w0501", rs.getString("w0501"));//申报人主键id
				map.put("w0501_safe", PubFunc.encrypt(rs.getString("w0501")));//申报人主键_e
				map.put("w0301_safe", PubFunc.encrypt(rs.getString("w0301")));//申报人主键_e
				map.put("subObject", PubFunc.encrypt(rs.getString("w0501")+"_"+type));//绩效分析被调查对象subObject
				map.put("w0511", w0511);//姓名
				
				// 现聘职称、申报职称改为字符型 chent 20170413
				String w0513 = rs.getString("w0513");
				if(StringUtils.isEmpty(w0513)){
					w0513 = "";
				}else {
					if(AdminCode.getCode("AJ", w0513) != null) {
						w0513=AdminCode.getCode("AJ", w0513).getCodename();
					}
				}
				map.put("w0513", w0513);
				String w0515 = rs.getString("w0515");
				if(StringUtils.isEmpty(w0515)){
					w0515 = "";
				}else {
					if(AdminCode.getCode("AJ", w0515) != null) {
						w0515=AdminCode.getCode("AJ", w0515).getCodename();
					}
				}
				map.put("w0515", w0515);
				// 现聘职称、申报职称项目名与数据字典一致 chent 20170413
				map.put("w0513itemtext", DataDictionary.getFieldItem("W0513", "W05").getItemdesc());
				map.put("w0515itemtext", DataDictionary.getFieldItem("W0515", "W05").getItemdesc());
				map.put("w0533", w0533);//状态
				map.put("w0535", rs.getString("w0535"));//评审材料访问地址
				map.put("w0536", rs.getString("w0536"));//评审材料word模板访问地址
				map.put("w0537", rs.getString("w0537"));//送审论文材料访问地址
				map.put("w0539", PubFunc.encrypt("0".equals(rs.getString("w0539"))?null:rs.getString("w0539")));//内部评审问卷计划号
				map.put("w0541", PubFunc.encrypt("0".equals(rs.getString("w0541"))?null:rs.getString("w0541")));//专家鉴定问卷计划号
				map.put("w0539_qnid", getQnId(rs.getString("w0539")));//内部评审问卷号
				map.put("w0541_qnid", getQnId(rs.getString("w0541")));//专家鉴定问卷号
				
				map.put("imgpath", imgPath);//图片
				map.put("type", type);//// 帐号类型    1：内部评委  2：学科组成员 3：外部鉴定专家 4:学院任聘组
				map.put("expertState", rs.getString("expert_state"));//专家状态标识
				map.put("approvalState", rs.getString("approval_state"));//审批状态
				map.put("expertName", PubFunc.encrypt(this.userview.getUserName()));
				map.put("nbasea0100_safe", PubFunc.encrypt(rs.getString("w0503")+rs.getString("w0505")));//nabse+a0100加密
				map.put("nbasea0100_safe_1", PubFunc.encrypt(rs.getString("w0503")+"`"+rs.getString("w0505")));//nabse+a0100加密
				/*//int usetype = 1;
				if(rs.getInt("usetype") == 2){
					usetype = 2;
				}*/
				map.put("usetype", rs.getInt("usetype"));//1：查看账号   2：投票账号
				
				// 获取导出模板信息
				HashMap<String, String> exptInfoMap = new HashMap<String, String>();
				exptInfoMap = getExptInfo(rs.getString("w0537"), rs.getString("w0503"), rs.getString("w0505"), questionid);
				map.put("tp_id", ""/*exptInfoMap.get("tp_id")*/);//导出模板信息功能参数配置。前台不需要打印功能，为避免以后又需要，先直接输出“” chent 20160621
		    	map.put("tabid", exptInfoMap.get("tabid"));
		    	map.put("ins_id", exptInfoMap.get("ins_id"));
		    	map.put("taskid", exptInfoMap.get("taskid"));
		    	map.put("sp_batch", exptInfoMap.get("sp_batch"));
		    	map.put("batch_task", exptInfoMap.get("batch_task"));
		    	map.put("pre", exptInfoMap.get("pre"));
		    	map.put("a0100", exptInfoMap.get("a0100"));
		    	map.put("questionid", exptInfoMap.get("questionid"));
		    	
		    	String agreetext = "";
		    	String disagreetext = "";
		    	String giveuptext = "";
		    	
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
				
		    	
		    	// 增加申报人员分类
		    	String sql1 = "select categories_id,c_level,queue from zc_categories_relations where categories_id in ( select categories_id From zc_personnel_categories where w0301=? and  review_links=?) and w0501=?";
		    	rs1 = dao.search(sql1, Arrays.asList(new String[] {rs.getString("w0301"), type, rs.getString("w0501")}));
		    	if(rs1.next()) {
		    		categories_id = rs1.getString("categories_id");
		    		map.put("categories_id", PubFunc.encrypt(categories_id));
		    		map.put("c_level", rs1.getString("c_level"));
		    		map.put("queue", rs1.getString("queue"));//批次
		    	}
		    	
				infoList.add(map);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs1);
		}
		
		return infoList;
    }
    /**
     * 获取设置的分类-职级的人数
     * @return
     * @throws GeneralException
     */
    public LinkedHashMap<String, String> getCategoriesNumMap(String queue,String useType) throws GeneralException {
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	int count = 0;//已经投赞成票的人员
    	ArrayList<String> list = new ArrayList<String>();
		try {
			String sqls = "select count(1) as count from zc_data_evaluation where username = ? and approval_state = ? and "
					+ "w0501 not in (select w0501 from zc_categories_relations where categories_id in "
					+ "(select categories_id from zc_expert_user where username=?) and queue=?)";
			list.add(this.userview.getUserName());
			if("1".equals(useType)) {
	    		list.add("0");//审核账号只能看到暂停的
	    	}else {
	    		list.add("1");//只有已经启动的才能看到，起草，暂停，结束都不能看
	    	}
			list.add(this.userview.getUserName());
			list.add(queue);
			rs = dao.search(sqls,list);
			
			if(rs.next()) {
				count = rs.getInt("count");
			}
			
			String sql = "select * from zc_categories_relations A "
					+ "left join zc_personnel_categories B on A.categories_id=B.categories_id "
					+ "where A.categories_id in ( "
						+ "select categories_id "
						/*+ "From zc_personnel_categories "
						+ "where w0301 in("
								+ "select distinct W0301 From zc_expert_user where username=?"
								+ ") "
							+ "and  review_links in ("
								+ "	select distinct type From zc_expert_user where username=?"
							+ ") and approval_state=1"*/
						+ " from zc_expert_user where username=? group by categories_id"
					+ ") and queue=? order by A.categories_id,A.c_level desc";
			
			rs = dao.search(sql, Arrays.asList(new String[] {this.userview.getUserName(), queue}));
			while(rs.next()) {
				String categories_id = rs.getString("categories_id");
				String c_level = rs.getString("c_level");
				String name = rs.getString("name");
				String c_num = "";
				if("person".equalsIgnoreCase(c_level)) {
					c_num = rs.getString("c_number");
				}else {
					c_num = rs.getString("c_"+c_level);
				}
				
				String w0575_codesetid = DataDictionary.getFieldItem("W0575").getCodesetid();
				String c_level_name = AdminCode.getCodeName(w0575_codesetid, c_level);
				
				String key = PubFunc.encrypt(categories_id) +"_"+ c_level;
				
				String value = "";
				if(StringUtils.isNotBlank(c_num))
					value = c_num+"_"+name;
				else
					value = "_"+name;
				
				if(StringUtils.isNotEmpty(c_level_name)) {
					value += ("-"+c_level_name);
				}
				map.put(key, value+"_"+count);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
    	
    	return map;
    } 
    public LinkedHashMap<String, ArrayList<String>> getCategoriesMap() throws GeneralException {
    	LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	RowSet rs1 = null;
    	try {
    		String sql = "select * from zc_categories_relations A "
    				+ "left join zc_personnel_categories B on A.categories_id=B.categories_id "
    				+ "where A.categories_id in ( "
    				+ "select categories_id "
    				/*+ "From zc_personnel_categories "
    				+ "where w0301 in("
    				+ "select distinct W0301 From zc_expert_user where username=?"
    				+ ") "
    				+ "and  review_links in ("
    				+ "	select distinct type From zc_expert_user where username=?"*/
    				+ " from zc_expert_user where username=? group by categories_id"
    				//+ " and approval_state=1"
    				+ ") order by A.categories_id,A.c_level desc";
    		
    		rs = dao.search(sql, Arrays.asList(new String[] {this.userview.getUserName()}));
    		while(rs.next()) {
    			String categories_id = rs.getString("categories_id");
    			String c_level = rs.getString("c_level");
    			String name = rs.getString("name");
    			int c_num = 0; 
    			if("person".equalsIgnoreCase(c_level)) {
    				c_num = rs.getInt("c_number");
    			}else {
    				c_num = rs.getInt("c_"+c_level);
    			}
    			
    			String w0575_codesetid = DataDictionary.getFieldItem("W0575").getCodesetid();
    			String c_level_name = AdminCode.getCodeName(w0575_codesetid, c_level);
    			
    			String key = PubFunc.encrypt(categories_id)+"_"+name;
    			ArrayList<String> list = map.get(key);
    			if(list == null) {
    				list = new ArrayList<String>();
    			}
    			
    			int personNum = 0;
    			int queue = 0;
    			rs1 = dao.search("select count(w0501) as count,max(queue) as queue from zc_categories_relations where categories_id='"+categories_id+"' and c_level='"+c_level+"'");
    			if(rs1.next()) {
    				personNum = rs1.getInt("count");
    				queue = rs1.getInt("queue");
    			}
    			
    			String value = c_level+"_"+personNum+"_"+c_num+"_"+c_level_name+"_"+queue;
    			if(list.contains(value)) {
    				continue;
    			}
    			list.add(value);
    			
    			map.put(key, list);
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	} finally {
    		PubFunc.closeDbObj(rs);
    	}
    	
    	return map;
    } 
    /**
	 * 取得人员信息_保密
	 * String username 用户名
	 * @return 
	 */
	public HashMap getExpertInfo(String username) throws GeneralException {
    	
    	HashMap map = new HashMap();
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try {
	    	String sql = "select * from zc_expert_user where username=?";
	    	ArrayList<String> list = new ArrayList<String>();
	    	list.add(username);
	    	rs = dao.search(sql, list);
	    	while(rs.next()){
	    		String w0301 = rs.getString("W0301");//会议id
	    		
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return map;
    }
    /**
     * 根据计划号获取问卷号
     * String planId 计划号
     * @return 
     */
    public String getQnId(String planId) throws GeneralException {
    	
    	String qnId = "";
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try {
    		if(!StringUtils.isEmpty(planId)){
    			String sql = "select qnId from qn_plan where planId=?";
    			ArrayList<String> list = new ArrayList<String>();
    			list.add(planId);
    			rs = dao.search(sql, list);
    			while(rs.next()){
    				qnId = rs.getString("qnId");//问卷号
    				
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		PubFunc.closeDbObj(rs);
    	}
    	
    	return qnId;
    }
    /**
     * 获得代码型数据
     * @param item
     * @return
     */
    private String getCodeitem(String item, String val) throws GeneralException {
    	
    	String result = "";
    	
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
		try {
			String sql = "select codeitemdesc from codeitem where CodeSetId=(select codesetid from t_hr_busifield where itemid=?) and codeitemid=?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(item);
			if(StringUtils.isEmpty(val)){
				val = "";
			}
			list.add(val);
			rs = dao.search(sql, list);
			while (rs.next()) {
				result = rs.getString("codeitemdesc");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	
		return result;
    }
    /**
     * 获取导出模板信息
     * @param w0537：送审论文材料访问地址
     * @param nabse
     * @param a0100
     * @param questionid 问卷计划号
     * @return
     */
    private HashMap<String, String> getExptInfo(String w0537, String nabse, String a0100, String questionid){
    	HashMap<String, String> map = new HashMap<String, String>();
    	
    	String tabid = getTemplate("5");//获取论文送审配置的模板号
    	String tp_id = getTpId(tabid);
    	String ins_id = "";
    	String taskid = "";
    	if(!StringUtils.isEmpty(w0537)){
    		String[] tmp = w0537.split("&");
    		for(int i=0; i<tmp.length; i++){
    			if(tmp[i].startsWith("ins_id")){
    				int idx = tmp[i].indexOf("=");
    				ins_id = tmp[i].substring(idx+1);
    			} else if(tmp[i].startsWith("taskid")){
    				int idx = tmp[i].indexOf("=");
    				taskid = tmp[i].substring(idx+1);
    			}
    		}
    	}
    	
    	map.put("tp_id", tp_id);
    	map.put("tabid", tabid);
    	map.put("ins_id", ins_id);
    	map.put("taskid", PubFunc.decrypt(taskid));
    	map.put("sp_batch", "0");
    	map.put("batch_task", "");
    	map.put("pre", nabse);
    	map.put("a0100", a0100);
    	map.put("questionid", questionid);
    	map.put("current_id", a0100);
    	
    	return map;
    }
    /**
     * 获取TpId
     * @param tabid
     * @return
     */
    private String getTpId(String tabid) {
    	String tp_id = "";

    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs = null;
    	 try{
    		String sql = "select tp_id,name,content from t_wf_template where tabid=?";
  		    ArrayList<String> list = new ArrayList<String>();
  		    list.add(tabid);
  		    rs = dao.search(sql, list);
  		    if (rs.next()){
  		    	tp_id = rs.getString("tp_id");
  		    }
  		    if(rs!=null)
  		    	rs.close();
  	   }catch(Exception e){
  		   e.printStackTrace();
  	   }
    	
		return tp_id;
	}

    
	/**
	 * 获取模板id
	 * @param num 几号模板
	 * @return
	 */
	private String getTemplate(String num) {
    	
    	String template = "";
    	 try{
 		    ContentDAO dao = new ContentDAO(conn);
 		    RowSet rs = dao.search("select str_value From Constant where Constant='JOBTITLE_CONFIG'");
 		    if (rs.next()){
 		    	String str_value = rs.getString("str_value");
 		    	if (str_value == null || (str_value != null && "".equals(str_value))){
 		    	}else{

 		    	    Document doc = PubFunc.generateDom(str_value);
 		    	    String xpath = "//templates";
 		    	    XPath xpath_ = XPath.newInstance(xpath);
 		    	    Element ele = (Element) xpath_.selectSingleNode(doc);
 		    	    List subNode = ele.getChildren();  
 		    	    Element child;
 		    	   Element subEt = null;
 		    	   for (int j = 0; j < subNode.size(); j++) {   
 		                subEt = (Element) subNode.get(j); //循环依次得到子节点
	                	if(num.equals(subEt.getAttributeValue("type"))){
	                		template = subEt.getAttributeValue("template_id");
	                	}
 		    	   }
 		    	}
 		    }
 		    if(rs!=null)
 		    	rs.close();
 	   }catch(Exception e){
 		   e.printStackTrace();
 	   }
 	   
 	   return template;
	}
	private ArrayList getSchemeSettingSortItemList() {
		
		ArrayList<String> sortItemList = new ArrayList<String>();
		RowSet rs = null;
		ContentDAO dao = null;
		try{
			String sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = 'reviewFile' and is_share = '1') and is_display = '1' order by displayorder";;
			dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while (rs.next()){
				String itemid = rs.getString("itemid");
				String is_order = rs.getString("is_order");
            	if(StringUtils.isNotBlank(is_order) && !"0".equals(is_order)){
            		sortItemList.add(itemid+":"+is_order);
            	}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return sortItemList;
	}
	public ArrayList getCategoriesList(String useType) {
		
		ArrayList<String> sortItemList = new ArrayList<String>();
		RowSet rs = null;
		ContentDAO dao = null;
		ArrayList<String> list = new ArrayList<String>();
		try{
			String sql = "select zeu.categories_id from zc_expert_user zeu left join zc_personnel_categories zpc on zeu.categories_id = zpc.categories_id "
					+ "where username = ? and zpc.approval_state=? group by zeu.categories_id";
			list.add(this.userview.getUserName());
	    	if("1".equals(useType)) {
	    		list.add("0");//审核账号只能看到暂停的
	    	}else {
	    		list.add("1");//只有已经启动的才能看到，起草，暂停，结束都不能看
	    	}
			dao = new ContentDAO(conn);
			rs = dao.search(sql,list);
			while (rs.next()){
				sortItemList.add(PubFunc.encrypt(rs.getString("categories_id")));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return sortItemList;
	}
	public ArrayList getLevelList() {
		
		ArrayList<String> sortItemList = new ArrayList<String>();
		RowSet rs = null;
		try{
			String w0575_codesetid = DataDictionary.getFieldItem("W0575").getCodesetid();
			
			if("0".equals(w0575_codesetid)) {
				return sortItemList;
			}
			String sql = "select codeitemid from codeitem where codesetid='"+w0575_codesetid+"' order by a0000";;
			rs = new ContentDAO(conn).search(sql);
			while (rs.next()){
				sortItemList.add(rs.getString("codeitemid"));
			}
			sortItemList.add("person");
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return sortItemList;
	}
	
	/**
	 * 获取<申报人分组id，批次>
	 */
	public HashMap<String,Integer> getQueue() {
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		ArrayList<String> list = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(conn);
		String categories_id = "";
		RowSet rs = null;
		String review_links = "";
		String w0301 = "";
		try{
			String sql = "select * from zc_categories_relations A "
    				+ "left join zc_personnel_categories B on A.categories_id=B.categories_id "
    				+ "where A.categories_id in ( "
    				+ "select categories_id "
    				/*+ "From zc_personnel_categories "
    				+ "where w0301 in("
    				+ "select distinct W0301 From zc_expert_user where username=?"
    				+ ") "
    				+ "and  review_links in ("
    				+ "	select distinct type From zc_expert_user where username=?"
    				+ ") and approval_state=1"*/
    				+ " from zc_expert_user where username=? group by categories_id "
    				+ ") order by A.categories_id,A.c_level desc";
    		
    		rs = dao.search(sql, Arrays.asList(new String[] {this.userview.getUserName()}));
    		
    		if(rs.next()) {
    			w0301 = rs.getString("w0301");
				review_links = rs.getString("review_links");
    		}
			sql = "select categories_id,queue from zc_categories_relations where categories_id in ( select categories_id from zc_expert_user where username=? group by categories_id)";
			list.add(this.userview.getUserName());
			rs = dao.search(sql,list);
			while (rs.next()){
				categories_id = rs.getString("categories_id");
				map.put(PubFunc.encrypt(categories_id), rs.getInt("queue"));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
	
	/**
	 * 
	 * @param queueMap
	 * @return
	 */
	public HashMap<String,Object> getObjectId(MorphDynaBean queueMap,String reviewlink,String w0301) {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		ArrayList<String> listObjectId = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		HashMap<String,Object> mapFinally = new HashMap<String,Object>();
		HashMap<String,HashMap<String,String>> mapFile = new HashMap<String,HashMap<String,String>>();//申报材料传给评分界面需要的参数
		ArrayList mapPerson = new ArrayList();
		StringBuffer sql = new StringBuffer("select w0503,w0505,w0535,w0536,w0537 from w05 inner join  (select w0501,zcr.seq from zc_categories_relations zcr inner join zc_personnel_categories Zpc on Zpc.categories_id = zcr.categories_id where 1=2 ");
		try {
			Map map = PubFunc.DynaBean2Map(queueMap);//根据分组id和分组批次z
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String cateId = (String)entry.getKey();
				String queue = (String)entry.getValue();
				sql.append(" or (zcr.categories_id=? and zcr.queue=?)");
				list.add(PubFunc.decrypt(cateId));
				list.add(queue);
			}
			sql.append(") t1 on w05.w0501=t1.w0501 where w0555=? and w0301=?");
			sql.append(" order by t1.seq");
			list.add(reviewlink);
			list.add(w0301);
			rs = dao.search(sql.toString(),list);
			while(rs.next()) {
				listObjectId.add(PubFunc.encrypt(rs.getString("w0505")));
				String w0535 = rs.getString("w0535");
				String w0536 = rs.getString("w0536");
				String w0537 = rs.getString("w0537");
				String nbasea0100_safe = PubFunc.encrypt(rs.getString("w0503")+rs.getString("w0505"));
				StringBuffer needPass = new StringBuffer();//在不同的阶段传不同的参数
				if("1".equals(reviewlink) || "2".equals(reviewlink) || "4".equals(reviewlink)){
					if(StringUtils.isNotBlank(w0535) || StringUtils.isNotBlank(w0536)){
						needPass.append(w0535+"__"+nbasea0100_safe+"__"+reviewlink+"__"+w0536);
					}
				} else if("3".equals(reviewlink)){
					if(StringUtils.isNotBlank(w0537)){
						needPass.append(w0537+"__"+nbasea0100_safe+"__"+reviewlink);
					}else if(StringUtils.isNotBlank(w0535) || StringUtils.isNotBlank(w0536)) {
						needPass.append(w0535+"__"+nbasea0100_safe+"__"+reviewlink+"__"+w0536);
					}
				}
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("a0100",PubFunc.encrypt(rs.getString("w0505")));
				bean.set("value",needPass.toString());

				mapPerson.add(bean );
			}
			mapFinally.put("W0505_ENCODE", listObjectId);
			mapFinally.put("NEEDPASS_PARAMETER", mapPerson);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return mapFinally;
	}
	
	/**
	 * 获取所有已提交的赞成的票数
	 * @return
	 */
	public int getApprovalCount() {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		ArrayList<String> list = new ArrayList<String>();
		String sql = "";
		int count = 0;
		try {
			sql = "select count(1) as count from zc_data_evaluation where username = ? and expert_state = ?";
			list.add(this.userview.getUserName());
			list.add("3");
			rs = dao.search(sql.toString(),list);
			while(rs.next()) {
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
	 * 保存打分提交的数据
	 */
	public void saveSubmitCount(String _categories_id,String w0301, String review_link) {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<String> list = new ArrayList<String>();
		int count = 0;
		boolean flag = false;
		try {
			String sql = "SELECT count(1) as count FROM kh_mainbody km left join kh_object ko on km.kh_object_id=ko.id left join W05 on ko.Object_id=W05.W0505 "
					+ "left join zc_categories_relations zcr on w05.W0501=zcr.w0501 WHERE km.Mainbody_id=? AND (km.Status = ? or km.Status = ?) AND km.Relation_id=? "
					+ "and w05.W0301 = ? and w05.W0555= ? and zcr.categories_id = ?";//查出对应的考核主体还没有提交的
			list.add(this.userview.getUserName());
			list.add("0");
			list.add("1");
			list.add("1_"+w0301+"_"+review_link);
			list.add(w0301);
			list.add(review_link);
			list.add(_categories_id);
			rs = dao.search(sql,list);
			if(rs.next()) {//根据status分组自增得到state，如果第一条status是2则说明全部提交了
				count = rs.getInt("count");
			}
			if(count == 0) {
				RecordVo vo = new RecordVo("zc_personnel_categories");
				vo.setString("categories_id", _categories_id);
				vo = dao.findByPrimaryKey(vo);
				int expertnum = vo.getInt("expertnum");
				int submitnum = vo.getInt("submitnum");
				submitnum = submitnum + 1;
				vo.setInt("submitnum", submitnum);
				/*if(expertnum == submitnum) {// 应投==已投：已结束
					vo.setString("approval_state", "2");
				}*/
				dao.updateValueObject(vo);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
}
