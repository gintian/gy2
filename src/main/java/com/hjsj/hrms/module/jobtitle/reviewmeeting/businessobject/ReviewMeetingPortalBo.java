package com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsForMeetingBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
public class ReviewMeetingPortalBo {
	private UserView userView = null;
	private Connection conn = null;
	
	public ReviewMeetingPortalBo(UserView userView, Connection conn) {
		super();
		this.userView = userView;
		this.conn = conn;
	}
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	/**
	 * @param scheme
	 *		Scheme[0]：01|05|09|06 //会议状态01=起草;05=执行中 06=结束 09=暂停
	 *		Scheme[1]：年度（2018）
	 *		Scheme[2]：为空本单位 不为空查看下属单位（具体机构号：UN|UM`0002）
	 *    for example:
	 *    	scheme [‘in’,’2018’,’ UM`0002’] 
	 * @throws GeneralException 
	 */
	public ArrayList<LazyDynaBean> schemeMeettingsBySchmeme(String[] scheme,int limit,int page) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<LazyDynaBean> meetingList = new ArrayList<LazyDynaBean>();
		RowSet rs = null;
		try {
			String mStatus = scheme[0];
			if(StringUtils.isBlank(mStatus)||"0".equals(mStatus))
				mStatus = "all";
			String year = scheme[1];
			if(StringUtils.isBlank(year) ||"0".equals(year))
				year = String.valueOf(DateUtils.getYear(new Date()));
			
			String belongOrg = scheme[2];
			//belongOrg 为空则是查看所属机构的会议否则为下属单位
			if(StringUtils.isBlank(belongOrg) || "0".equals(belongOrg)) {
				belongOrg = "";
			}
			//获得查询sql 
			Map map = this.getSchemeSql(mStatus, year, belongOrg);
			String sql = (String)map.get("sql");
			sql = sql+" ORDER BY W0309 DESC";
			List values = (List<String>)map.get("values");
			if(StringUtils.isNotBlank(sql)) {
				rs = dao.search(sql, values, limit, page);
				LazyDynaBean bean = null;
				
				boolean editMeetingFunc = this.userView.hasTheFunction("380050502");//编辑会议
				boolean delMeetingFunc = this.userView.hasTheFunction("380050504");//删除会议
				boolean startMeetingFunc = this.userView.hasTheFunction("380050506");//启动会议
				boolean stopMeetingFunc = this.userView.hasTheFunction("380050507");//暂停会议
				boolean remindFunc = this.userView.hasTheFunction("380050510");//参会提醒
				boolean startReviewFunc = this.userView.hasTheFunction("380050515");//发起评审
				while(rs.next()) {
					bean = new LazyDynaBean();
					String w0301 = PubFunc.encrypt(rs.getString("W0301"));
					String name = rs.getString("W0303");
					String startD = rs.getTimestamp("W0309")==null?"--":DateUtils.format(rs.getTimestamp("W0309"), "yyyy-MM-dd");
					String endD = rs.getTimestamp("W0311")==null?"--":DateUtils.format(rs.getTimestamp("W0311"), "yyyy-MM-dd");
					//所属组织机构名称
					String orgName = "";
					String B0110 = rs.getString("B0110");
					String UNName = AdminCode.getCodeName("UN", B0110);
					String UMName = AdminCode.getCodeName("UM", B0110);
					orgName = StringUtils.isNotEmpty(UNName)?UNName:UMName;
					String meetingstate = rs.getString("W0321");
					
					bean.set("w0301", w0301);
					bean.set("name", name);
					bean.set("startd", startD);
					bean.set("endd", endD);
					bean.set("b0110",B0110);
					bean.set("orgname", orgName);
					bean.set("meetingstate", meetingstate);
					
					//是否可编辑
					//评审会议的所属单位
					boolean readOnly = true;
					
					String b0110 = userView.getUnitIdByBusi("9");
					//超级用户或者最大业务范围
					if(userView.isSuper_admin()|| "UN`".equals(b0110)) {
						readOnly = false;
						
					}else {
						String[] units = ArrayUtils.EMPTY_STRING_ARRAY; 
						units = StringUtils.split(b0110, "`");
						for(int i = 0 ; i<units.length ; i++) {
							//登录人有相匹配的权限时可编辑
							String unit = units[i].substring(2);//用户权限
							//用户操作权限大于等于所属机构可以操作会议
							if(B0110!=null && B0110.length()>=unit.length() && unit.equals(B0110.substring(0,unit.length()))) {
								readOnly = false;
							}
						}
					}
					bean.set("readOnly", readOnly);
					//解析w03的扩展参数xml
					List<LazyDynaBean> segments = this.getXmlParamByW03(rs.getString("extend_param"),rs.getString("W0301"));
					boolean canDel = true;//是否可以删除标记
					for(LazyDynaBean segment : segments) {
						String state = (String) segment.get("state");
						if("2".equals(state)) {
							canDel = false;
							break;
						}
						
					}
					bean.set("canDel", canDel);
					bean.set("segments", segments);
					bean.set("endSegment", segments.get(segments.size()-1).get("flag"));
					bean.set("editMeetingFunc", editMeetingFunc);
					bean.set("delMeetingFunc", delMeetingFunc);
					bean.set("startMeetingFunc", startMeetingFunc);
					bean.set("stopMeetingFunc", stopMeetingFunc);
					bean.set("remindFunc", remindFunc);
					bean.set("startReviewFunc", startReviewFunc);
					
					meetingList.add(bean);
				}
			}
			return meetingList;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 根据查询方案获取sql
	 * 
	 * @param mStatus
	 * @param year
	 * @param belongOrg
	 * @return
	 * @throws GeneralException 
	 */
	private Map getSchemeSql(String mStatus,String year,String belongOrg) throws GeneralException {
		Map map = new HashMap();
		StringBuffer sql;
		try {
			List<String> values = new ArrayList<String>();
			sql = new StringBuffer();
			sql.append("SELECT * FROM W03 WHERE ");
			//以开始时间的年份为年度 查询
			sql.append(Sql_switcher.dateToChar("W0309", "yyyy")+"=? ");
			values.add(year);
			if(!"all".equals(mStatus)) {
				sql.append("AND W0321=? ");
				values.add(mStatus);
			}
			/** 所属单位和下属单位可以支持多个 */
			String[] orgArr = null;
			if(StringUtils.isEmpty(belongOrg)) {
				sql.append("AND B0110 IN ( ");
				String b0110 = userView.getUnitIdByBusi("9");
				//超级用户或者默认最大业务范围
				if(userView.isSuper_admin() || StringUtils.isBlank(b0110) || "UN`".equals(b0110)) {
					JobtitleUtil util = new JobtitleUtil(conn, userView);
					String orgs = util.getTopOrgs();
					orgArr = orgs.split(",");
					
				}else {
					String[] units = ArrayUtils.EMPTY_STRING_ARRAY; 
					units = StringUtils.split(b0110, "`");
					orgArr = new String[units.length];
					for(int i = 0 ; i<units.length ; i++) {
						orgArr[i] = units[i].substring(2);
					}
				}
				for(int i = 0 ; orgArr!=null && i < orgArr.length ; i++) {
					sql.append("?,");
					if(i==orgArr.length-1)
						sql.setLength(sql.length()-1);
					values.add(orgArr[i]);
				}
				
			}else {
				sql.append("AND (");
				orgArr = belongOrg.split("\\|");
				for(int i = 0 ; orgArr!=null && i < orgArr.length ; i++) {
					sql.append("B0110 like ? ");
					if(i<orgArr.length-1)
						sql.append(" or ");
					values.add(orgArr[i]+"%");
				}
			}
			//评审会议界面排除以前程序生成的会议
			sql.append(")").append(" and extend_param is not null ");
			map.put("sql", sql.toString());
			map.put("values", values);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return map;
	}
	/**
	 * w03表的扩展参数，记录评审会议各个环节的状态
	 * @return
	 * @throws GeneralException 
	 */
	public List<LazyDynaBean> getXmlParamByW03(String xmlDoc) throws GeneralException {
		
		List<LazyDynaBean> beans = new ArrayList<LazyDynaBean>();
		try {
			if(StringUtils.isEmpty(xmlDoc)) {
				return beans;
			}
            //通过输入源构造一个Document
            Document doc = PubFunc.generateDom(xmlDoc);
            //取的根元素
            Element root = doc.getRootElement();
           List<Element> segments = root.getChildren("segment");
           LazyDynaBean bean = null;
           int seq = 1;
           for(Element el : segments) {
        	   bean = new LazyDynaBean();
        	   String flag = el.getAttributeValue("flag");//环节序号 1=同行 ；2=二级单位； 3=学科组；4=评委会；
        	   String state = el.getAttributeValue("state");//状态 0：未开始 1：进行中 2：结束
        	   String template = el.getAttributeValue("template");//测评表
        	   String archived = el.getAttributeValue("archived");//是否归档 =1 否 =2 是
        	   String rate_control = el.getAttributeValue("rate_control");
        	   String evaluation_type = el.getAttributeValue("evaluation_type");//=1 投票 =2 评分
        	   String usertype = el.getAttributeValue("usertype");//账号类型 =1随机账号  =2选择专家
        	   String vote_default = el.getAttributeValue("vote_default");//默认投票（1：赞成2：不赞成3：弃权）
        	   bean.set("seq",seq);
        	   bean.set("flag",flag);
        	   bean.set("rate_control",rate_control);
        	   bean.set("evaluation_type",evaluation_type==null?"1":evaluation_type);
        	   bean.set("archived",archived);
        	   bean.set("state",state);
        	   bean.set("template",template==null?"":template);
        	   bean.set("usertype",usertype==null?"":usertype);
        	   bean.set("vote_default",vote_default==null?"0":vote_default);
        	   
        	   beans.add(bean);
        	   seq++;
           }
           
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return beans;
	}
/**
 * getXmlParamByW03 重载方法，查询会议的阶段信息，并查询参会人数和已平人数
 * @param xmlDoc
 * @param w0301
 * @return
 * @throws GeneralException
 */
public List<LazyDynaBean> getXmlParamByW03(String xmlDoc,String w0301) throws GeneralException {
		
		List<LazyDynaBean> beans = new ArrayList<LazyDynaBean>();
		try {
			if(StringUtils.isEmpty(xmlDoc)) {
				return beans;
			}
            //通过输入源构造一个Document
            Document doc = PubFunc.generateDom(xmlDoc);
            //取的根元素
            Element root = doc.getRootElement();
           List<Element> segments = root.getChildren("segment");
           LazyDynaBean bean = null;
           int seq = 1;
           for(Element el : segments) {
        	   bean = new LazyDynaBean();
        	   String flag = el.getAttributeValue("flag");//环节序号 1=同行 ；2=二级单位； 3=学科组；4=评委会；
        	   String state = el.getAttributeValue("state");//状态 0：未开始 1：进行中 2：结束
        	   String usertype = el.getAttributeValue("usertype");//账号类型 =1随机账号  =2选择专家
        	   String template = el.getAttributeValue("template");//测评表
        	   String archived = el.getAttributeValue("archived");//是否归档 =1 否 =2 是
        	   String rate_control = el.getAttributeValue("rate_control");
        	   String evaluation_type = el.getAttributeValue("evaluation_type");//=1 投票 =2 评分
        	   String vote_default = el.getAttributeValue("vote_default");//默认投票（1：赞成2：不赞成3：弃权）
        	   int attendnumber = this.getAttendnumber(w0301,flag);//获得参评人数
        	   int attendednumber = this.getAttendednumber(w0301,flag,evaluation_type);//已评人数
        	   bean.set("seq",seq);
        	   bean.set("flag",flag);
        	   bean.set("rate_control",rate_control);
        	   bean.set("evaluation_type",evaluation_type==null?"1":evaluation_type);
        	   bean.set("archived",archived);
        	   bean.set("state",state);
        	   bean.set("attendnumber",attendnumber);
        	   bean.set("attendednumber",attendednumber);
        	   bean.set("template",template==null?"":template);
        	   bean.set("usertype",usertype==null?"":usertype);
        	   bean.set("vote_default",vote_default==null?"":vote_default);
        	   
        	   beans.add(bean);
        	   seq++;
           }
           
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return beans;
	}
	/**
	 * 获得已评人数
	 * @param w0301 会议id
	 * @param segment  环节号
	 * @param eType 评审方式  =1 投票 =2 评分
	 * @return
	 * @throws GeneralException 
	 */
	private int getAttendednumber(String w0301, String segment,String eType) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		int pnumber = 0;
		try {
			String sql = "";
			//投票
			List<String> values = new ArrayList<String>();
			if("1".equals(eType)) {
				//expert_state=3 页面已提交
				sql = "select count(distinct(w0501)) as pnumber from zc_data_evaluation where expert_state=3 and categories_id in (select categories_id from zc_personnel_categories where w0301=? and review_links=?)";
				values.add(w0301);
				values.add(segment);
			}//评分
			else if("2".equals(eType)) {
				sql += "SELECT COUNT(*) as pnumber FROM (SELECT Object_id FROM kh_object ko INNER JOIN kh_mainbody km ON km.kh_object_id=ko.id ";
				sql += "WHERE  km.Status=2 AND km.Relation_id=? GROUP BY ko.Object_id ) a";
				//Relation_id 职称评审格式设置为:模块ID_评审会议ID_环节ID
				values.add("1_"+w0301+"_"+segment);
			}
			rs = dao.search(sql, values);
			while(rs.next()) {
				pnumber += rs.getInt("pnumber");
			}
			return pnumber;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
}
	/**
	 * 获得参评人数
	 * @param w0301
	 * @param segment
	 * usertype 账号类型  =1 随机账号 =2 选择专家
	 * @return
	 * @throws GeneralException 
	 */
	private int getAttendnumber(String w0301, String segment) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select count(w0501) pnumber from zc_categories_relations where categories_id in ");
			sql.append("(select categories_id from zc_personnel_categories where w0301=? and review_links=?)");
			List values = new ArrayList<String>();
			values.add(w0301);
			values.add(segment);
			rs = dao.search(sql.toString(), values);
			if(rs.next()) {
				return rs.getInt("pnumber");
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return 0;
	}
	public int getCountNum(String[] scheme) throws GeneralException {
		int count=0;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String mStatus = scheme[0];
			if(StringUtils.isBlank(mStatus)||"0".equals(mStatus))
				mStatus = "all";
			String year = scheme[1];
			if(StringUtils.isBlank(year) ||"0".equals(year))
				year = String.valueOf(DateUtils.getYear(new Date()));
			
			String belongOrg = scheme[2];
			if(StringUtils.isBlank(belongOrg) || "0".equals(belongOrg))
				belongOrg = "";
			
			Map map = this.getSchemeSql(mStatus, year, belongOrg);
			String sql = (String)map.get("sql");
			String countSql = "SELECT COUNT(W0301) NUM "+sql.substring(sql.indexOf("FROM"));
			List values = (List<String>)map.get("values");
			if(StringUtils.isNotBlank(sql)) {
				rs = dao.search(countSql, values);
				if(rs.next())
					count = rs.getInt("NUM");
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return count;
	}
	/**
	 * 删除评审会议
	 * @param meetingId
	 * @throws GeneralException 
	 */
	public void deleteMeeting(String meetingId) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String sql = "delete from w03 where w0301=?";
			List<String> values = new ArrayList<String>();
			values.add(meetingId);
			int row = dao.delete(sql, values);
			//删除数据后对应清除无用数据
			if(row>0) {
				clearDataByW0301(meetingId);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}		
	}
	/**
	 * 删除会议后，清除相关数据
	 * @throws GeneralException 
	 */
	private void clearDataByW0301(String w0301) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sql = new StringBuffer();
			List values = new ArrayList();
			values.add(w0301);
			//1、删除评分相关数据
			StartReviewBo sbo = new StartReviewBo(this.conn,this.userView);
			sbo.cleanAllKHTableByW0301(w0301,true);
	        //2、删除w05的申报人
			sql.setLength(0);
			sql.append("delete from w05 where w0301=?");
			dao.delete(sql.toString(), values);
		    //3、删除投票结果
			sql.setLength(0);
			sql.append("delete from zc_data_evaluation where w0301=?");
			dao.delete(sql.toString(), values);
		    //4、专家账号
			sql.setLength(0);
			sql.append("delete from zc_expert_user where w0301=?");
			dao.delete(sql.toString(), values);
			//5、申报人分组
			sql.setLength(0);
			sql.append("delete from zc_categories_relations where categories_id in (select categories_id from zc_personnel_categories where w0301=?)");
			dao.delete(sql.toString(), values);
			sql.setLength(0);
			sql.append("delete from zc_personnel_categories where w0301=?");
			dao.delete(sql.toString(), values);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {

		}
	}
	/**
	 * 暂停会议
	 * @param meetingId
	 * @throws GeneralException 
	 */
	public void stopMeeting(String meetingId) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String sql = "update w03 set w0321='09'  where w0301=?";
			List<String> values = new ArrayList<String>();
			values.add(meetingId);
			dao.update(sql, values);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 启动会议同时更新评审阶段为进行中
	 * @param meetingId
	 * @throws GeneralException 
	 */
	public void startMeeting(String meetingId) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("w03");
			vo.setString("w0301", meetingId);
			vo = dao.findByPrimaryKey(vo);
			//会议状态==》进行中
			vo.setString("w0321", "05");//进行中
			String xmlDoc = vo.getString("extend_param");
			List<LazyDynaBean> segments = getXmlParamByW03(xmlDoc);
			boolean startFirstSegment = true;
			String vote_default = "";
			for(int i=0;segments!=null && i<segments.size();i++) {
				LazyDynaBean bean = segments.get(i);
				String review_links = (String) bean.get("flag");
				//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 (review_links)
				if(StringUtils.equalsIgnoreCase((String) bean.get("evaluation_type"), "1")) {//=1 投票 =2 评分
					vote_default = (String) bean.get("vote_default");
				}
				if(StringUtils.isNotBlank(vote_default) && !"0".equals(vote_default)) {
					updateVoteDefaultState(meetingId, vote_default, review_links);
				}
				
				
				String state = (String)bean.get("state");//阶段状态
				//是否存在进行中的阶段
				if("1".equals(state)) {
					startFirstSegment = false;
					break;
				}
				
			}
			//不存在进行中的阶段时，默认启动第一个阶段
			if(startFirstSegment && segments!=null && segments.size()>0) {
				segments.get(0).set("state", "1");
			}
			//更新阶段状态
			vo.setString("extend_param",this.parseBeans2xml(segments));
			dao.updateValueObject(vo);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	//为了防止在暂停了会议，然后修改默认投票状态后重新启动无法修改
	private void updateVoteDefaultState(String w0301, String vote_default, String reviewlink) {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ReviewConsoleBo bo = new ReviewConsoleBo(this.conn, this.userView);
		try {
			rs = dao.search("select categories_id from zc_personnel_categories where w0301=? and review_links=?", 
					Arrays.asList(new String[] {w0301, reviewlink}));
			while(rs.next()) {
				String categories_id = rs.getString("categories_id");
				bo.putPersonToEvaluation(w0301, vote_default, categories_id);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * bean类型转换成xml
	 * @param segments
	 * @throws GeneralException 
	 */
	private String parseBeans2xml(List<LazyDynaBean> segments) throws GeneralException {
		try {
			Element root = new Element("params");
			Element segment = null;
			for(LazyDynaBean bean : segments) {
				segment = new Element("segment");
				String flag = (String) bean.get("flag");
				String archived = (String) bean.get("archived");
				String rate_control = (String) bean.get("rate_control");
				String state = (String) bean.get("state");
				String evaluation_type = (String) bean.get("evaluation_type");
				String usertype = (String) bean.get("usertype");
				String vote_default = (String) bean.get("vote_default");
				segment.setAttribute("flag",flag);
				segment.setAttribute("archived", archived);//是否归档 =1 否  =2是
				segment.setAttribute("rate_control", rate_control);//=1 通过率按2/3控制
				segment.setAttribute("state", state);
				segment.setAttribute("evaluation_type", evaluation_type);//=1 投票 =2 评分
				segment.setAttribute("usertype", usertype);//账号类型：=1随机账号 =2 选择专家
				segment.setAttribute("vote_default", vote_default==null?"0":vote_default);//默认投票（1：赞成2：不赞成3：弃权）
				if(!"3".equals(flag)) {
					String template = (String) bean.get("template");
					segment.setAttribute("template", template);//测评表
				}
				root.addContent(segment);
			}
			Document myDocument = new Document(root);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			return outputter.outputString(myDocument);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {

		}
	}
	/**
	 * 提醒参会
	 * @param meetingId
	 * @param seq
	 */
	public void remind(String meetingId, int seq) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 发起评审
	 * @param meetingId
	 * @param seq
	 */
	public void iniReview(String meetingId, int seq) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 保存评审会议
	 * 
	 * 返回添加的会议信息
	 * @throws ParseException 
	 * @throws GeneralException 
	 */
	public LazyDynaBean saveCommitee(String opt,MorphDynaBean valueBean,String w0301) throws GeneralException {
		ContentDAO dao = new ContentDAO(conn);
		try {
			String meetingname = (String)valueBean.get("w0303");
			String meetingdesc = (String)valueBean.get("w0305");
			String b0110 = (String)valueBean.get("b0110");
			if(StringUtils.isNotBlank(b0110)) {
				b0110 = b0110.split("`")[0];
			}
			String startD_ = (String)valueBean.get("w0309");
			String endD_ = (String)valueBean.get("w0311");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startD = sdf.parse(startD_);
			Date endD = sdf.parse(endD_);	
			
			RecordVo vo = new RecordVo("W03");
			if("1".equals(opt)) {//add
				IDGenerator idg = new IDGenerator(2,this.conn);//序号生成器
				w0301 = idg.getId("W03.W0301");//序号生成器生成w0301
				vo.setString("w0301", w0301);
				vo.setString("w0321", "01");//起草状态
				vo.setDate("create_time", new Date());
				vo.setString("create_user", this.userView.getUserName());
				vo.setString("create_fullname", this.userView.getUserFullName());
				//生成xml格式数据
				String xml = parseFormData2xml(valueBean);
				if(StringUtils.isNotBlank(xml)) {
					vo.setString("extend_param", xml);
				}
			}else if("2".equals(opt)){//edit
				if(StringUtils.isNotEmpty(w0301))
					w0301 = PubFunc.decrypt(w0301);
				vo.setString("w0301", w0301);
				vo = dao.findByPrimaryKey(vo);
				//更新阶段信息
				String xml = vo.getString("extend_param");
				if(StringUtils.isEmpty(xml)) {
					xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?><params/>";
				}
				vo.setString("extend_param",getUpdateXml(valueBean, xml));
				
			}
			vo.setString("w0303", meetingname);
			vo.setString("w0305", meetingdesc);
			vo.setString("b0110", b0110);
			vo.setDate("w0309", DateUtils.getSqlDate(startD));
			vo.setDate("w0311", DateUtils.getSqlDate(endD));
			if("1".equals(opt))
				dao.addValueObject(vo);
			else
				dao.updateValueObject(vo);
			return  this.getMeetingData(w0301);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	private String getUpdateXml(MorphDynaBean valueBean,String xml) throws GeneralException {
		try {
			// 创建一个新的字符串
			Document doc = PubFunc.generateDom(xml);
			Element root = doc.getRootElement();
			List<Element> segmentEls = root.getChildren("segment");
			String segments = (String) valueBean.get("segments");

			String[] segmentArr = segments.split(",");
			Element newRoot = new Element("params");
			for (int i = 0; i < segmentArr.length; i++) {
				String flag = segmentArr[i];
				Element el = null;
				for (Element e : segmentEls) {
					if (flag.equals(e.getAttributeValue("flag"))) {
						el = e;
						break;
					}
				}
				if (el != null) {
					// 更新
					String evaluationType = "3".equals(flag) ? "1": (String) valueBean.get("evaluationType_" + flag);
					String vote_default = "3".equals(flag) ? "1": (String) valueBean.get("voteDefault_" + flag);
					String template = "3".equals(flag) ? "": (String) valueBean.get("evaluationType_" + flag + "_template");
					String archived = el.getAttributeValue("archived");
					String rate_control = el.getAttributeValue("rate_control");
					String state = el.getAttributeValue("state");
					String usertype = el.getAttributeValue("usertype");
					el = new Element("segment");
					el.setAttribute("flag", flag);
					el.setAttribute("archived", archived);// 是否归档 =1 否 =2是
					el.setAttribute("rate_control", rate_control);// =1 通过率按2/3控制
					el.setAttribute("state", state);
					el.setAttribute("template", template);// 测评表
					el.setAttribute("evaluation_type", evaluationType);// 测评表
					el.setAttribute("usertype", usertype);
					el.setAttribute("vote_default", vote_default==null?"0":vote_default);
				} else {
					// 添加el元素
					el = new Element("segment");
					// 同行专家只有随机账号
					String evaluationType = "3".equals(flag) ? "1": (String) valueBean.get("evaluationType_" + flag);
					String vote_default = "3".equals(flag) ? "1": (String) valueBean.get("voteDefault_" + flag);
					String template = "3".equals(flag) ? "": (String) valueBean.get("evaluationType_" + flag + "_template");
					el.setAttribute("flag", flag);
					el.setAttribute("archived", "1");// 是否归档 =1 否 =2是
					el.setAttribute("rate_control", "1");// =1 通过率按2/3控制
					el.setAttribute("state", "0");
					el.setAttribute("template", template);// 测评表
					el.setAttribute("evaluation_type", evaluationType);// 测评表
					el.setAttribute("usertype", "");
					el.setAttribute("vote_default", vote_default==null?"0":vote_default);
				}
				newRoot.addContent(el);
			}
			Document mydoc = new Document(newRoot);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			return outputter.outputString(mydoc);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 将前台传递的表单数据转换成xml
	 * @return
	 * @throws GeneralException 
	 */
	private String parseFormData2xml(DynaBean valueBean) throws GeneralException {
		try {
			String segments = (String)valueBean.get("segments");
			Element root = new Element("params");
			if(StringUtils.isNotBlank(segments)){
				Element segment = null;
				//按一下顺序添加  同行-->二级单位-->专业组-->评委会
				if(segments.contains("3")) {// 同行
					segment = new Element("segment");
					segment.setAttribute("flag", "3");
					segment.setAttribute("archived", "1");//是否归档 =1 否  =2是
					segment.setAttribute("rate_control", "1");//=1 通过率按2/3控制
					segment.setAttribute("state", "0");
					segment.setAttribute("evaluation_type", "1");//测评表
					segment.setAttribute("template", "");
					segment.setAttribute("usertype", "1");
					root.addContent(segment);
				}
				if(segments.contains("4")) {//二级单位
					segment = new Element("segment");
					String evaluationType_4 = (String)valueBean.get("evaluationType_4");
					String template = (String)valueBean.get("evaluationType_4_template");
					String vote_default = (String)valueBean.get("voteDefault_4");
					segment.setAttribute("flag", "4");
					segment.setAttribute("archived", "1");//是否归档 =1 否  =2是
					segment.setAttribute("rate_control", "1");//=1 通过率按2/3控制
					segment.setAttribute("state", "0");
					segment.setAttribute("template", template);//测评表
					segment.setAttribute("evaluation_type", evaluationType_4);//测评表
					segment.setAttribute("usertype", "");
					segment.setAttribute("vote_default", vote_default==null?"0":vote_default);
					root.addContent(segment);
				}
				if(segments.contains("2")) {//专业组
					segment = new Element("segment");
					String evaluationType_2 = (String)valueBean.get("evaluationType_2");
					String template = (String)valueBean.get("evaluationType_2_template");
					String vote_default = (String)valueBean.get("voteDefault_2");
					segment.setAttribute("flag", "2");
					segment.setAttribute("archived", "1");//是否归档 =1 否  =2是
					segment.setAttribute("rate_control", "1");//=1 通过率按2/3控制
					segment.setAttribute("state", "0");
					segment.setAttribute("template",template);//测评表
					segment.setAttribute("evaluation_type", evaluationType_2);//测评表
					segment.setAttribute("usertype", "");
					segment.setAttribute("vote_default", vote_default==null?"0":vote_default);
					root.addContent(segment);
				}
				if(segments.contains("1")) {//评委会
					segment = new Element("segment");
					String evaluationType_1 = (String)valueBean.get("evaluationType_1");
					String template = (String)valueBean.get("evaluationType_1_template");
					String vote_default = (String)valueBean.get("voteDefault_1");
					segment.setAttribute("flag", "1");
					segment.setAttribute("archived", "1");//是否归档 =1 否  =2是
					segment.setAttribute("rate_control", "1");//=1 通过率按2/3控制
					segment.setAttribute("state", "0");//未开始
					segment.setAttribute("template", template);//测评表
					segment.setAttribute("evaluation_type", evaluationType_1);//测评表
					segment.setAttribute("usertype", "");
					segment.setAttribute("vote_default", vote_default==null?"0":vote_default);
					root.addContent(segment);
				}
				Document myDocument = new Document(root);
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				return outputter.outputString(myDocument);
			}
			
			return "";
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			
		}
	}
	/**
	 * 查询指定会议
	 * @param w0301
	 * @return
	 * @throws GeneralException 
	 */
	public LazyDynaBean getMeetingData(String w0301) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			LazyDynaBean bean = new LazyDynaBean();
			String sql = "select * from w03 where w0301=?";
			List values = new ArrayList();
			values.add(w0301);
			rs = dao.search(sql,values);
			if(rs.next()) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				bean.set("w0301",PubFunc.encrypt(rs.getString("w0301")));
				bean.set("w0303",rs.getString("w0303"));
				bean.set("w0305",rs.getString("w0305")==null?"":rs.getString("w0305"));
				String w0309 = rs.getDate("w0309")==null?"":sdf.format(rs.getDate("w0309"));
				String w0311 = rs.getDate("w0311")==null?"":sdf.format(rs.getDate("w0311"));
				bean.set("w0309", w0309);
				bean.set("w0311", w0311);
				bean.set("b0110", rs.getString("b0110"));
				bean.set("meetingstate", rs.getString("w0321"));
				
				String xml = rs.getString("extend_param");
				List<LazyDynaBean> extentParams = getXmlParamByW03(xml,w0301);
				//启用的评审阶段
				String[] segments = new String[extentParams.size()];
				for(int i=0;i<extentParams.size();i++) {
					
					LazyDynaBean param = extentParams.get(i);
					
					String flag = (String)param.get("flag");
					//测评表  为空就是投票方式
					String template = (String) param.get("template");
					String evaluation_type = (String) param.get("evaluation_type");//  ==1 投票 ==2评分
					bean.set("evaluationType_"+flag,evaluation_type);
					bean.set("evaluationType_"+flag+"_template", template);
					bean.set("evaluationType_"+flag+"_templateName", getTemplatesName(template));
					bean.set("usertype_"+flag, param.get("usertype"));
					bean.set("rate_control_"+flag, param.get("rate_control"));
					bean.set("segmentStatus_"+flag, param.get("state"));//评审环节状态
					bean.set("voteDefault_"+flag, param.get("vote_default")==null?"0":param.get("vote_default"));//默认投票
					
					segments[i]=flag;
				}
				bean.set("segments", segments);
			}
			return bean;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 
	 * 根据测评表id,获得测评表名称
	 * @return
	 * @throws GeneralException 
	 */
	private String getTemplatesName(String templateIds) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String templatesName = "";
		try {
			if(StringUtils.isEmpty(templateIds)) {
				return templatesName;
			}
			String[] templateArr = templateIds.split(",");
			List<String> values = new ArrayList<String>();
			String inStr = "";
			for(int i=0;i<templateArr.length;i++) {
				String templateId = templateArr[i];
					inStr+="?";
				if(i<templateArr.length-1)
					inStr+=",";
				values.add(templateId);
			}
			String sql = "select template_id,name  from per_template where template_id in("+inStr+")  and validflag='1'";
			rs = dao.search(sql, values);
			while(rs.next()) {
				String template_id = rs.getString("template_id");
				String name = rs.getString("name");
				templatesName+="【<font style='font-weight:bold'>"+template_id+"</font>】"+name+"、";
			}
			if(StringUtils.isNotBlank(templatesName))
				templatesName = templatesName.substring(0,templatesName.length()-1);
			return templatesName;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 将权限范围内所有聘委会封装成ArrayList<CommonData>
	 * @return 操作数据operationData
	 * @throws GeneralException 
	 */
	public ArrayList<LazyDynaBean> getCommitteList() throws GeneralException{
		ArrayList<LazyDynaBean> operationData = new ArrayList<LazyDynaBean>();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select committee_id,committee_name from zc_committee ");
			sql.append(" where 1=1 ");
			//排除掉没有添加专家的评委会  haosl 20170620
			sql.append("and committee_id in (select distinct(committee_id) from zc_judgingpanel_experts) ");
			sql.append(new JobtitleUtil(this.conn, this.userView).getB0110Sql_down(this.userView.getUnitIdByBusi("9")));
			rs = dao.search(sql.toString());
			while (rs.next()) {
				String name = rs.getString("committee_name");
				String value = rs.getString("committee_id");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("committee_name", name);
				bean.set("committee_id", value);
				operationData.add(bean);
			}
			if(operationData.size()>0){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("committee_name", "　");
				bean.set("committee_id",null);
				operationData.add(0, bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return operationData;
	}
	/**
	 * 保存评审人员配置信息
	 * @param valueBean
	 * @param w0301
	 * @return
	 * @throws GeneralException 
	 */
	public LazyDynaBean saveExpertSetting(MorphDynaBean valueBean, String w0301) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			if(StringUtils.isEmpty(w0301))
				return null;
			w0301 = PubFunc.decrypt(w0301);
			HashMap<String,String> formMap = PubFunc.DynaBean2Map(valueBean);
			//二级单位和评委会信息存到w03
			RecordVo w03Vo = new RecordVo("w03");
			w03Vo.setString("w0301",w0301);
			w03Vo = dao.findByPrimaryKey(w03Vo);
			/** 账号类型  =1 随机账号 =2 选择专家 */
			String usertype_1 = "";//评委会账号类型
			String usertype_2 = "";//专业组账号类型
			String usertype_3 = "";//同行账号类型（只有随机账号）
			String usertype_4 = "";//评委会账号类型
			boolean isUpdateW03 = false;
			if(formMap.containsKey("usertype_1")) {
				usertype_1 = formMap.get("usertype_1");
				isUpdateW03 = true;
				String usertype_1_1 = formMap.get("usertype_1_1");
				if(StringUtils.isNotBlank(usertype_1_1)) {
					int expertnum = Integer.parseInt(usertype_1_1);
					w03Vo.setInt("w0315",expertnum);//随机账号
					syncCategoriesExpertNumber(expertnum,w0301,1);
				}else {
					//w03Vo.setInt("w0315",0);
					String committee_id = formMap.get("usertype_1_2");
					String committee_id_old = w03Vo.getString("committee_id");
					
					w03Vo.setString("committee_id", committee_id);//选择评委会
					//如果切换了评委会，则删除评委会专家的账号
					if(StringUtils.isEmpty(committee_id_old) && !committee_id_old.equals(committee_id)) {
						String delSql = "delete from zc_expert_user where w0301=? and type=1";
						List values = new ArrayList();
						values.add(w0301);
						dao.delete(delSql, values);
					}
				}
			}
			if(formMap.containsKey("usertype_4")) {
				usertype_4 = formMap.get("usertype_4");
				isUpdateW03 = true;
				String usertype_4_1 = formMap.get("usertype_4_1");
				if(StringUtils.isNotBlank(usertype_4_1)) {
					int expertnum = Integer.parseInt(usertype_4_1);
					w03Vo.setInt("w0323",expertnum);//随机账号
					syncCategoriesExpertNumber(expertnum,w0301,4);
				}else {
					String sub_committee_id = formMap.get("usertype_4_2");
					String sub_committee_id_old = w03Vo.getString("sub_committee_id");
					w03Vo.setString("sub_committee_id",sub_committee_id);//选择二级单位
					if(StringUtils.isEmpty(sub_committee_id) && !sub_committee_id_old.equals(sub_committee_id)) {
						String delSql = "delete from zc_expert_user where w0301=? and type=4";
						List values = new ArrayList();
						values.add(w0301);
						dao.delete(delSql, values);
					}
				}
			}
			if(isUpdateW03)
				dao.updateValueObject(w03Vo);
			
			if(formMap.containsKey("usertype_3")) {//同行
				usertype_3 = "1";
				String sql = "select categories_id from zc_personnel_categories where w0301=? and review_links=3";
				List values = new ArrayList();
				values.add(w0301);
				rs = dao.search(sql,values);
				RecordVo categoriesVo = new RecordVo("zc_personnel_categories");
				String categories_id = "";
				String usertype_3_1 = formMap.get("usertype_3_1");
				if(StringUtils.isNotBlank(usertype_3_1)) {
					int expertnum = Integer.parseInt(usertype_3_1);
					//没有记录则添加，有则更新
					if(rs.next()) {
						syncCategoriesExpertNumber(expertnum,w0301,3);
					}else {
						IDGenerator idg = new IDGenerator(2,this.conn);//序号生成器
						categoriesVo.setString("categories_id", idg.getId("zc_personnel_categories.categories_id"));
						categoriesVo.setString("w0301", w0301);
						categoriesVo.setString("review_links", "3");
						if(StringUtils.isNotBlank(usertype_3_1)) {
							categoriesVo.setInt("expertnum",expertnum);
						}
						dao.addValueObject(categoriesVo);
					}
				}
			}
			
			if(formMap.containsKey("usertype_2")) {
				usertype_2 = formMap.get("usertype_2");
				/*
				if("1".equals(usertype_2)) {
					//选择随机账号时，删除已选择的学科组信息
					List values = new ArrayList();
					values.add(w0301);
					String sql = "delete from zc_expert_user where w0301=? and type=2";
					dao.delete(sql, values);
					String sql = "select categories_id from zc_personnel_categories where w0301=? and review_links=2";
					List values = new ArrayList();
					values.add(w0301);
				 	rs = dao.search(sql,values);
					//没有记录则添加，有则不添加
					if(!rs.next()) {
						RecordVo categoriesVo = new RecordVo("zc_personnel_categories");
						IDGenerator idg = new IDGenerator(2,this.conn);//序号生成器
						categoriesVo.setString("categories_id", idg.getId("zc_personnel_categories.categories_id"));
						categoriesVo.setString("w0301", w0301);
						categoriesVo.setString("review_links", "2");//专业组
						dao.addValueObject(categoriesVo);
					}
				
				}else {
					List values = new ArrayList();
					values.add(w0301);
					String sql = "delete from zc_personnel_categories where w0301=? and review_links=2";
					dao.delete(sql, values);
				}*/
			}
			
			List maps = new ArrayList();
			//评委会
			if(StringUtils.isNotBlank(usertype_1)) {
				HashMap map = map = new HashMap();
				map.put("segemntIndex", "1");
				map.put("usertype", usertype_1);
				maps.add(map);
			}
			//专业组
			if(StringUtils.isNotBlank(usertype_2)) {
				HashMap map = map = new HashMap();
				map.put("segemntIndex", "2");
				map.put("usertype", usertype_2);
				maps.add(map);
			}
			//同行
			if(StringUtils.isNotBlank(usertype_3)) {
				HashMap map = map = new HashMap();
				map.put("segemntIndex", "3");
				map.put("usertype", usertype_3);
				maps.add(map);
			}
			if(StringUtils.isNotBlank(usertype_4)) {
				HashMap map = map = new HashMap();
				map.put("segemntIndex", "4");
				map.put("usertype", usertype_4);
				maps.add(map);
			}
			if(maps.size()>0) {
				batchUpdateSegmentsAttr(w0301, maps);
			}
			return this.getMeetingData(w0301);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {

		}
	}
	/**
	 * 获得评审人员设置的信息，用于回显页面数据  同时 返回的bean的   isBeenSet可以判断是否配置过评审人员
	 * @param w0301
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getExpertSetting(String w0301) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			LazyDynaBean bean = new LazyDynaBean();
			
			RecordVo recordVo = new RecordVo("w03");
			recordVo.setString("w0301", w0301);
			recordVo = dao.findByPrimaryKey(recordVo);
			LazyDynaBean meetinData = getMeetingData(w0301);
			
			String[] segmentArr = (String[])meetinData.get("segments");
			String segments = StringUtils.join(segmentArr, ",");
			
			//评委会id
			boolean isBeenSet = true; //是否设置了评审人员
			if(StringUtils.isNotBlank(segments) && segments.indexOf("1")!=-1){
				
				String usertype = (String) meetinData.get("usertype_1");
				bean.set("usertype_1", usertype);
				int w0315 = recordVo.getInt("w0315");
				if(w0315>0)
					bean.set("usertype_1_1",w0315);
				bean.set("usertype_1_2", recordVo.getString("committee_id"));
				if(("1".equals(usertype)&&w0315>0) ||("2".equals(usertype) && StringUtils.isNotBlank(recordVo.getString("committee_id")))) {
					isBeenSet = true;
				}else {
					isBeenSet = false;
				}
			}
			if(StringUtils.isNotBlank(segments) && segments.indexOf("4")!=-1) {
				String usertype = (String) meetinData.get("usertype_4");
				bean.set("usertype_4", usertype);
				int w0323 = recordVo.getInt("w0323");
				if(w0323>0)
					bean.set("usertype_4_1", w0323);
				bean.set("usertype_4_2", recordVo.getString("sub_committee_id"));
				if(("1".equals(usertype)&&w0323>0) 
						||("2".equals(usertype) && StringUtils.isNotBlank(recordVo.getString("sub_committee_id")))) {
					
				}else {
					isBeenSet = false;
				}
			}
			if(StringUtils.isNotBlank(segments) && segments.indexOf("2")!=-1) {
				//学科组
				String usertype = (String) meetinData.get("usertype_2");
				bean.set("usertype_2", usertype);
				SubjectsForMeetingBo subjectMbo = new SubjectsForMeetingBo(this.conn, this.userView);
				ArrayList subjectlist = subjectMbo.getSubjects(w0301, "", "");
				bean.set("subjectlist", subjectlist);
				if("1".equals(usertype)
						||("2".equals(usertype)&&subjectlist.size()>0)) {
				
				}else {
					isBeenSet = false;
				}
			}
			if(StringUtils.isNotBlank(segments) && segments.indexOf("3")!=-1) {
				//同行专家
				bean.set("usertype_3", "1");
				String sql = "select expertnum from zc_personnel_categories where w0301=? and review_links=3";
				List values = new ArrayList();
				values.add(w0301);
				rs = dao.search(sql,values);
				if(rs.next()) {
					int expernum = rs.getInt("expertnum");
					if(expernum>0) {
						bean.set("usertype_3_1", expernum);
					}else {
						isBeenSet = false;
					}
				}
			}
			
			bean.set("isBeenSet", isBeenSet);
			return bean;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {

		}
		
	}
	/**
	 * 更新会议下的指定阶段的属性
	 * w0301  会议id
	 * segemnt  阶段号  =1 评委会 =2 专业组  =3 同行 =4 二级单位
	 * map  要更新的属性，key为属性，value为属性值
	 * updateFirst true|false[可选]
	 * @throws GeneralException 
	 */
	public void updateSegmentAttr(String w0301,String segemnt,HashMap<String,String> map) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("w03");
			vo.setString("w0301", w0301);
			vo = dao.findByPrimaryKey(vo);
			//评审环节参数
			String xmlDoc = vo.getString("extend_param");
			List<LazyDynaBean> segments = getXmlParamByW03(xmlDoc);
			//是否需要进行更新操作
			boolean isUpdate = false;
			for(LazyDynaBean bean : segments) {
				String flag = (String)bean.get("flag");
				if(flag.equals(segemnt)) {
					isUpdate = true;
					//遍历map的键值对，进行更新
					for(Map.Entry<String, String> entry:map.entrySet()) {
						String attrName = entry.getKey();
						String attrValue = entry.getValue();
						bean.set(attrName, attrValue);
					}
					break;
				}
			}
			if(isUpdate) {
				xmlDoc = this.parseBeans2xml(segments);
				vo.setString("extend_param", xmlDoc);
				dao.updateValueObject(vo);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {

		}
		
	}
	/**
	 * 批量更新会议下的评审阶段属性
	 * @param w0301
	 * @param maps 每个map为环节的属性和值 另外每个map中需要加入环节号 segemntIndex
	 * @throws GeneralException
	 */
	public void batchUpdateSegmentsAttr(String w0301,List<HashMap<String,String>> maps) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("w03");
			vo.setString("w0301", w0301);
			vo = dao.findByPrimaryKey(vo);
			//评审环节参数
			String xmlDoc = vo.getString("extend_param");
			List<LazyDynaBean> segments = getXmlParamByW03(xmlDoc);
			//是否需要进行更新操作
			boolean isUpdate = false;
			for(LazyDynaBean bean : segments) {
				String flag = (String)bean.get("flag");
					//遍历map的键值对，进行更新
					for(HashMap<String,String> map : maps) {
						String segmentIndex = map.get("segemntIndex");
						if(flag.equals(segmentIndex)) {
							isUpdate = true;
							for(Map.Entry<String, String> entry:map.entrySet()) {
								String attrName = entry.getKey();
								if("segemntIndex".equalsIgnoreCase(attrName))
									continue;
								String attrValue = entry.getValue();
								bean.set(attrName, attrValue);
							}
							break;
						}
					}
			}
			if(isUpdate) {
				xmlDoc = this.parseBeans2xml(segments);
				vo.setString("extend_param", xmlDoc);
				dao.updateValueObject(vo);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {

		}
		
	}
	/**
	 * 校验会议完整性，不通过则不能启动会议
	 * @param segments 
	 * @param meetingId 
	 * @throws GeneralException 
	 */
	public List<String> isCommitteConsummate(String meetingId) throws GeneralException {
		List msgList = new ArrayList<String>();
		RowSet rs = null;
		try {
			//第一步：校验评审人员设置
			LazyDynaBean bean = getExpertSetting(meetingId);
			boolean isBeenSet = (Boolean)bean.get("isBeenSet");
			if(!isBeenSet) {
				msgList.add(ResourceFactory.getProperty("zc_new.reviewmeeting.error.nostartmsg.body1"));
			}
			/*//第二步校验申报人分组信息（是否选择了申报人）
			//查询会议信息
			LazyDynaBean meetingData = getMeetingData(meetingId);
			//获得启动的评审环节
			String[] segments = (String[]) meetingData.get("segments");
			for(String segment : segments) {
				//获得阶段的申报人数
				int number = this.getAttendnumber(meetingId, segment);
				if(number==0) {
					if("1".equals(segment)) {
						msgList.add(ResourceFactory.getProperty("zc_new.reviewmeeting.error.nostartmsg.body3"));
					}else if("2".equals(segment)) {
						msgList.add(ResourceFactory.getProperty("zc_new.reviewmeeting.error.nostartmsg.body2")+"("+ResourceFactory.getProperty("zc_new.label.inExpert")+")");
					}else if("3".equals(segment)) {
						msgList.add(ResourceFactory.getProperty("zc_new.reviewmeeting.error.nostartmsg.body2")+"("+ResourceFactory.getProperty("zc_new.label.exExpert")+")");
					}else if("4".equals(segment)) {
						msgList.add(ResourceFactory.getProperty("zc_new.reviewmeeting.error.nostartmsg.body2")+"("+ResourceFactory.getProperty("zc_new.label.inOther")+")");
					}
				}
					
			}
			if(msgList.size()>0) {
				msgList.add(0, ResourceFactory.getProperty("zc_new.reviewmeeting.error.nostartmsg.title"));
			}*/
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return msgList;
		
	}
	public void syncCategoriesExpertNumber(int expertnum,String w0301,int revie_links) throws GeneralException {
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String sql = "update zc_personnel_categories set expertnum=? where w0301=? and review_links=?";
			List values = new ArrayList();
			values.add(expertnum);
			values.add(w0301);
			values.add(revie_links);
			dao.update(sql,values);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
