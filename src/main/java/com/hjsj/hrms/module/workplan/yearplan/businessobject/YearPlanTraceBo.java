package com.hjsj.hrms.module.workplan.yearplan.businessobject;

import com.hjsj.hrms.module.workplan.common.WorkPlanPendingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 年计划任务跟踪BO
 * @author haosl
 *
 */
public class YearPlanTraceBo {

	private UserView userView;
	private Connection conn;
	private WorkPlanPendingBo pendingBo;
	public YearPlanTraceBo(Connection conn,UserView userView){
		
		this.userView = userView;
		this.conn = conn;
		pendingBo = new WorkPlanPendingBo(conn,userView);
	}
	public YearPlanTraceBo(Connection conn){
		this.conn = conn;
	}
	/**
	 * 查询计划列表
	 * @return
	 * @throws GeneralException 
	 */
	public List<Map> getPlanList(Integer year) throws GeneralException {
		List<Map> list = new ArrayList<Map>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = this.getPlanSql(year+"");
			//查询待办任务
			rs = dao.search(sql);
			while(rs.next()){
				Map map = new HashMap();
				Integer P1700 = rs.getInt("P1700");
				Integer P1701 = rs.getInt("P1701"); //年度
				String P1705 = rs.getString("P1705");//任务名称
				String P1729 = rs.getString("P1729");//责任岗位 
				String P1709 = rs.getString("P1709");//一季度完成情况
				String P1711 = rs.getString("P1711");//二季度完成情况
				String P1713 = rs.getString("P1713");//三季度完成情况
				String P1715 = rs.getString("P1715");//四季度完成情况
				String P1717 = rs.getString("P1717");//责任单位
				String P1719 = rs.getString("P1719");//牵头单位
				String P1721 = rs.getString("P1721");//公司领导
				String P1720 = rs.getString("P1720");//审批人
				String P1723 = rs.getString("P1723");//审核人
				String P1731 = rs.getString("P1731");//责任人
				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date P1745 = rs.getTimestamp("P1745");
					java.util.Date P1747 = rs.getTimestamp("P1747");
					if(P1745!=null)
						map.put("P1745", sdf.format(P1745));//开始时间
					if(P1747!=null)
						map.put("P1747", sdf.format(P1747));//结束时间
				}else{
					String P1745 = rs.getString("P1745");//审核人
					String P1747 = rs.getString("P1747");//责任人
					map.put("P1745", P1745);
					map.put("P1747", P1747);
				}
				map.put("P1700", P1700);
				map.put("P1701", P1701);
				map.put("P1705", P1705);
				map.put("P1729", P1729);
				map.put("P1709", P1709);
				map.put("P1711", P1711);
				map.put("P1713", P1713);
				map.put("P1715", P1715);
				map.put("P1717", P1717);
				map.put("P1719", P1719);
				map.put("P1721", P1721);
				map.put("P1720", P1720);
				map.put("P1723", P1723);
				map.put("P1731", P1731);
				
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		
		return list;
	}
	/**
	 * 获取所有指定的计划的年份列表
	 * @return
	 * @throws GeneralException 
	 */
	public List<Integer> getYearList() throws GeneralException {
		List<Integer> list = new ArrayList<Integer>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = this.getPlanSql(null);
			rs = dao.search(sql);
			while(rs.next()){
				Integer p1701 = rs.getInt("p1701");
				if(p1701!=null && p1701!=0)
					list.add(p1701);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		
		return list;
	}
	/**
	 * 
	 * @param planId
	 * 			年计划ID
	 * @param itemId
	 * 			季度字段
	 * @param content
	 * 
	 * @param  
	 * 		method save 单纯的保存
	 * 			   toapprove 报批之前保存
	 * 			季度完成情况
	 * @return
	 * @throws GeneralException 
	 */
	public String saveAchievementInfo(Integer planId,String itemId,String content) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		YearPlanBo bo = new YearPlanBo(userView, conn);
		RowSet rs = null;
		try {
			List values = new ArrayList<String>();
			
			String sql = "update p17 set "+itemId+"=? where p1700=?";
			values.add(content);
			values.add(planId);
			dao.update(sql.toString(), values);//保存季度完成情况
			
			//设置为起草状态
			if(StringUtils.isBlank(content))
				return "保存成功";
			Integer quarter = null;
			if("P1709".equalsIgnoreCase(itemId))
				quarter = 1;
			else if("P1711".equalsIgnoreCase(itemId))
				quarter = 2;
			else if("P1713".equalsIgnoreCase(itemId))
				quarter = 3;
			else
				quarter = 4;
			String guidkey = "";
			if(StringUtils.isNotBlank(this.userView.getDbname())){
				String id  = this.userView.getDbname()+this.userView.getA0100();
				guidkey = bo.getGuidKey(id,null);
			}
			if(StringUtils.isBlank(guidkey)){
				return "保存失败！";
			}
			//查询是否已经在审批流程表中保存过了
			sql = "select count(*) c from per_yearplan_approve where P1700=? and quarter=?";
			values.clear();
			values.add(planId);
			values.add(quarter);
			rs = dao.search(sql,values);
			if(rs.next() && rs.getInt("c")>0)
				return "保存成功";//证明审批记录表中已经有该条记录不需要插入。
			sql = "insert into per_yearplan_approve(P1700,quarter,Approve_state,cur_user) values(?,?,'01',?)";
			values.add(guidkey);
			dao.insert(sql, values);
			return "保存成功！";
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 查询当前登录用户的季度完成情况查询
	 * 		以 1,2,3,4为key 代表四个季度，value为季度状态
	 * 		key中没有包含的季度代表未填写。
	 * @param planId
	 * @throws GeneralException 
	 */
	public Map<String,String> getAchievementState(Integer planId) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		Map<String, String> map = new HashMap<String,String>();
		YearPlanBo bo = new YearPlanBo(userView, conn);
		RowSet rs = null;
		try {
			
			String sql = "select quarter,Approve_state,cur_user from per_yearplan_approve where p1700=?";
			List values = new ArrayList();
			values.add(planId);
			rs = dao.search(sql,values);
			while(rs.next()){
				map.put(rs.getString("quarter"),rs.getString("Approve_state"));
				String guidkey = "";
				if(StringUtils.isNotBlank(this.userView.getDbname())){
					String id  = this.userView.getDbname()+this.userView.getA0100();
					guidkey = bo.getGuidKey(id,null);
				}
				String cur_user = rs.getString("cur_user");
				//查询计划的某个季度的当前办理人是否是登录人，跟现实季度总结的状态和是否可编辑等有关
				if(StringUtils.isNotBlank(guidkey) && cur_user!=null && cur_user.contains(guidkey))
					map.put("isSelf_"+rs.getString("quarter"), "1");
				else
					map.put("isSelf_"+rs.getString("quarter"), "0");
			}	
			//查询是否有审核人 1=有   0=没有
			sql = "select * from per_yearplan_obj where P1700="+planId+" and obj_type='3'";
			rs = dao.search(sql);
			if(rs.next() && rs.getString("obj_id")!=null)
				map.put("hasVerifier", "1");//
			else
				map.put("hasVerifier", "0");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}
	/**
	 * 当前登录用户在指定计划下的角色信息（是责任人还是审核或审批人）
	 * @param planId
	 * @return
	 * @throws GeneralException 
	 */
	public List<String> getCurrentRoleByPlan(Integer planId) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		List<String> list = new ArrayList<String>();
		try {
			YearPlanBo bo = new YearPlanBo(userView, conn);
			String guidkey = "";
			if(StringUtils.isNotBlank(this.userView.getDbname())){
				String id  = this.userView.getDbname()+this.userView.getA0100();
				guidkey = bo.getGuidKey(id,null);
			}
			String sql = "select obj_type from per_yearplan_obj where p1700=? and obj_id=?";
			List values = new ArrayList();
			values.add(planId);
			values.add(guidkey);
			rs = dao.search(sql, values);
			while(rs.next()){
				list.add(rs.getString("obj_type"));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 报批
	 * @param planId
	 * @param isApprover  是否是审核人
	 * @return
	 * @throws GeneralException 
	 */
	public String toApprove(Integer planId,Integer quarter,String itemId,String content,boolean isApprover) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		YearPlanBo bo = new YearPlanBo(userView, conn);
		RowSet rs = null;
		String msg = "";
		try {
			//报批之前应该先更新下改季度总结（可能用户不点保存按钮，直接报批）
			this.saveAchievementInfo(planId, itemId, content);
			String sql = "";
			List values = new ArrayList();
			String Approve_state = ""; 
			String Cur_state = ""; //当前审批状态
			String cur_user = "";	//当前办理人
			String receiverType = "";
			if(isApprover){//审核人发布
				Approve_state="02";
				receiverType="8";
				sql = "select * from per_yearplan_obj where P1700="+planId+" and obj_type in ('8')";
				rs = dao.search(sql);
				StringBuffer guidkeBuf = new StringBuffer();//审批人
				while(rs.next()){
					if(rs.getString("obj_id")!=null){
						guidkeBuf.append(rs.getString("obj_id")+",");
						Cur_state += "02,";
					}
				}
				if(guidkeBuf.length()>0){
					cur_user = guidkeBuf.substring(0, guidkeBuf.length()-1);
					msg = "发布成功！";
				}else{
					return "未设置审批人！";
				}
			}else{//责任人报批（1.无审核人报批审批人，2.有审核人报批审核人）
				//查询审核人
				sql = "select * from per_yearplan_obj where P1700="+planId+" and obj_type in ('3','8')";
				rs = dao.search(sql);
				StringBuffer guidkeBuf2 = new StringBuffer();//审批人
				while(rs.next()){
					if("3".equals(rs.getString("obj_type"))){
						if(rs.getString("obj_id")!=null)
							cur_user=rs.getString("obj_id");
					}else if("8".equals(rs.getString("obj_type"))){
						if(rs.getString("obj_id")!=null)
							guidkeBuf2.append(rs.getString("obj_id")+",");
					}
				}
				if(cur_user.length()==0){//没有审核人
					//获得审批人的信息
					receiverType="8";
					Approve_state="02";
					if( guidkeBuf2.length()==0)
						return "未设置审批人";
					cur_user = guidkeBuf2.substring(0, guidkeBuf2.length()-1);
					for(int i=0;i<cur_user.split(",").length;i++){
						Cur_state += "02,";
					}
				}else{//有审核人
					receiverType="3";
					Approve_state="08";
					//获得审核的信息（可能有多人审批或审核）
					Cur_state += "08";
				}
				msg = "报批成功！";
			}
			if(Cur_state.contains(","))
				Cur_state = Cur_state.substring(0, Cur_state.length()-1);
			String Approve_process = getProcessXml(planId,Approve_state,quarter,"");
		    
			sql = "update per_yearplan_approve set approve_state=?,cur_user='"+cur_user+"',Cur_state='"+Cur_state+"',approve_date=?,";
			sql+=" approve_proc=? where P1700=? and quarter=?";
			values.clear();
			
			values.add(Approve_state);
			values.add(new Date(new java.util.Date().getTime()));
			values.add(Approve_process);
			values.add(planId);
			values.add(quarter);
			dao.update(sql, values);

			String ext_flag = "WP_P17_SP_"+planId+"_"+quarter;
			String receiver = this.userView.getUserName();
			if(StringUtils.isBlank(receiver))
				receiver = this.userView.getUserName();
			//将自己的待办置为已办
			this.pendingOK(receiver, ext_flag);
			//发布待办
			toApprovePending(planId,cur_user,quarter, receiverType);
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 返回xml格式数据
	 * @param Approve_state
	 * @param xml
	 * @return
	 * @throws GeneralException
	 */
	private String getProcessXml(Integer planId,String Approve_state,Integer quarter,String advice) throws GeneralException {
	   
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String Approve_process="";
		try {
			//审批过程xml格式
			List values = new ArrayList();
			String sql = "select approve_proc from per_yearplan_approve where P1700=? and quarter=?";
			values.clear();
			values.add(planId);
			values.add(quarter);
			rs = dao.search(sql,values);
			String xml = "";
			if(rs.next())
				xml = rs.getString("approve_proc");
			Document doc = parse2XmlDoc(xml);
			Element root = doc.getRootElement();
			String obj_type = "";
			String toUser = "";
			if("08".equals(Approve_state))
				obj_type = "3";//审核人
			else if("02".equals(Approve_state))
				obj_type="8";//审批人
			else if("07".equals(Approve_state)){
				List roles = this.getCurrentRoleByPlan(planId);
				if(roles.contains("3"))//审核人退回
					obj_type = "7";
				else{//审批人退回
					/**
					 * 审批人退回时可以根据审批流水记录，取最后一条的user就是要退回的人
					 * 因为审批人退回时需要区分退回到审核人或责任人，没有必要浪费资源再查表，偷懒拿审批记录中现成的即可
					 */
					List<Element> children = root.getChildren();
					Element el = children.get(children.size()-1);
					toUser = el.getAttributeValue("user");
				}
			}else if("01".equals(Approve_state)){
				obj_type = "7";//审核人
			}
			if(toUser.length()==0){//查询toUser (审批人退回时上面已经对toUser赋值了 )
				sql = "select obj_id from per_yearplan_obj where P1700=? and obj_type=?";
				values.clear();
				values.add(planId);
				values.add(obj_type);
				rs = dao.search(sql,values);
				StringBuffer guidkeBuf = new StringBuffer();
				while(rs.next()){
					guidkeBuf.append("'"+rs.getString("obj_id")+"',");
				}
				//获得审核（审批人）的信息
				if(guidkeBuf.length()>0 && !"03".equals(Approve_state) ){
					String guidkeys = guidkeBuf.substring(0, guidkeBuf.length()-1);
					String[] nbases = this.getNbase();
					for(int i=0;i<nbases.length;i++){
						String nbase = nbases[i];
						String tabelName = nbase+"A01";
						sql = "select a0101 from "+tabelName+" where GUIDKEY in("+guidkeys+")";
						rs = dao.search(sql);
						while(rs.next()){
							toUser+=rs.getString("a0101")+"、";//多个人加、区分
						}
						if(toUser.length()>0){//查到人员后跳出循环
							toUser = toUser.substring(0, toUser.length()-1);
							break;
						}
					}
					
				}
			}
			//创建记录信息
			Element recordEL = new Element("record");
			recordEL.setAttribute("user", this.userView.getUserFullName());
			recordEL.setAttribute("toUser", toUser);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			recordEL.setAttribute("date", df.format(new java.util.Date()));
			recordEL.setAttribute("state", Approve_state);
			recordEL.setText(advice);//建议
			root.addContent(recordEL);
			Format format=Format.getRawFormat();
			format.setEncoding("UTF-8");
			XMLOutputter output=new XMLOutputter(format);
			Approve_process = output.outputString(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return Approve_process;
	}
	
	/**
	 * 转换成xml Doc对象
	 * @param xml
	 * @return
	 * @throws GeneralException 
	 */
	private Document parse2XmlDoc(String xml) throws GeneralException{
		Document doc = null;
		try {
			 if(StringUtils.isBlank(xml)){
				 doc = new Document();
				 Element el = new Element("records");
				 doc.setRootElement(el);
			 }else{
				 //xus 20/4/23 xml 编码改造
				 doc = PubFunc.generateDom(xml);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		 return doc;
	}
	/**
	 * 查询指定计划的季度汇报过程信息
	 * @param planId
	 * @param quarter
	 * @return
	 * @throws GeneralException 
	 */
	public List<String> getProcessInfo(Integer planId, Integer quarter) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		List<String> infos = new ArrayList<String>();
		try {
			String sql = "select approve_proc from per_yearplan_approve where P1700=? and quarter=?";
			List values = new ArrayList();
			values.add(planId);
			values.add(quarter);
			rs = dao.search(sql, values);
			if(rs.next()){
				String approve_proc = rs.getString("approve_proc");
				infos = parseXml2Map(planId,approve_proc);
			}else{
				infos.add("暂无汇报过程信息！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return infos;
	}
	/**
	 * 将xml格式的过程信息转换成list集合
	 * @return
	 * @throws GeneralException 
	 */
	public List<String> parseXml2Map(Integer planId,String xml) throws GeneralException{
		List<String> list = new ArrayList<String>();
		try {
			Document doc = this.parse2XmlDoc(xml);
			Element root = doc.getRootElement();
			List<Element> children = root.getChildren();
			for(Element el : children){
				String info = "";
				String state = el.getAttributeValue("state");
				String user = el.getAttributeValue("user");
				String toUser = el.getAttributeValue("toUser");
				String date = el.getAttributeValue("date");
				if("08".equals(state)){
					info = date + "　"+user+" 将总结报批给 "+toUser+" 审核。";
				}else if("02".equals(state)){
						info = date + "　"+user+" 将总结报批给 "+toUser+" 审批。";
				}else if("03".equals(state)){
						info = date + "　"+user+" 已批准总结。";
				}else if("07".equals(state)){
					String advise = el.getText();
					info =  date + "　"+user+" 将总结退回给 "+toUser+" 修改。  ";
					if(StringUtils.isNotBlank(advise))
						info+="意见："+advise;
				}else if("01".equals(state)){
					info = date + "　"+user+" 已撤回总结。";
				}
				list.add(info);
			}
			if(list.isEmpty())
				list.add("暂无汇报记录信息！");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return list;
	}
	/**
	 * 审批人批准
	 * @param planId
	 * @param quarter
	 * @return
	 * @throws GeneralException
	 */
	public String approve(Integer planId,Integer quarter) throws GeneralException{
		String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			YearPlanBo bo = new YearPlanBo(userView, conn);
			String approve_state = "";
			String cur_state = "";
			List values = new ArrayList();
			String sql = "select * from per_yearplan_approve where P1700=? and quarter=? and Approve_state='02'";
			values.add(planId);
			values.add(quarter);
			rs = dao.search(sql,values);
			if(rs.next()){
				String cur_user = rs.getString("cur_user");
				cur_state = rs.getString("cur_state");
				if(cur_user.contains(",")){//多人审批
					String guidkey = "";
					if(StringUtils.isNotBlank(this.userView.getDbname())){
						String id  = this.userView.getDbname()+this.userView.getA0100();
						guidkey = bo.getGuidKey(id,null);
					}
					String[] temp = cur_user.split(",");
					for(int i=0;i<temp.length;i++){
						if(guidkey.equals(temp[i])){
							String[] temp2 = cur_state.split(",");
							temp2[i]="03";
							cur_state = StringUtils.join(temp2);
							if(cur_state.contains("02")){
								approve_state="02";
							}else{
								approve_state="03";
							}
							break;
						}
					}
					
				}else{
					approve_state="03";
				}
				//审批过程xml格式
				sql = "select approve_proc from per_yearplan_approve where P1700=? and quarter=?";
				values.clear();
				values.add(planId);
				values.add(quarter);
				rs = dao.search(sql,values);
				String xml = "";
				if(rs.next())
					xml = rs.getString("approve_proc");
				String approve_proc = getProcessXml(planId,approve_state,quarter,"");
				
				sql = "update per_yearplan_approve set Approve_state=?,Cur_state=?,approve_date=?,approve_proc=?";
				sql+=" where P1700=? and quarter=?";
				values.clear();
				values.add(approve_state);
				values.add(cur_state);
				values.add(new java.sql.Date(new java.util.Date().getTime()));
				values.add(approve_proc);
				values.add(planId);
				values.add(quarter);
				dao.update(sql, values);
				//更新代办信息
				String receiver = this.userView.getUserName();//业务用户关联自主用户的用户名
				if(StringUtils.isBlank(receiver))
					receiver = this.userView.getUserName();
				String ext_flag = "WP_P17_SP_"+planId+"_"+quarter;
				this.pendingOK(receiver, ext_flag);
				return "已批准！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return msg;
	}
	/**
	 * 退回
	 * isApprover 是否是审批人
	 * @throws GeneralException 
	 */
	public String reject(Integer planId,Integer quarter,String advice,boolean isApprover) throws GeneralException{
		 ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			//查询当前任务是否有审核人，如果有则退回到审核人
			String obj_id="";
			String approve_state = "07";
			List values = new ArrayList();
			String sql = "select obj_id from per_yearplan_obj where P1700="+planId+" and obj_type='3'";
			rs = dao.search(sql);
			//退回到审核人
			if(isApprover && rs.next() && StringUtils.isNotBlank(rs.getString("obj_id"))){
				obj_id = rs.getString("obj_id");
			}else{//退回到责任人
				sql = "select obj_id from per_yearplan_obj where P1700="+planId+" and obj_type='7'";
				rs = dao.search(sql);
				if(rs.next())
					obj_id = rs.getString("obj_id");
			}
			String approve_proc = this.getProcessXml(planId, approve_state, quarter,advice);
			sql = "update per_yearplan_approve set Approve_state=?,cur_user=?,Cur_state=?,approve_date=?, approve_proc=?";
			sql+=" where P1700=? and quarter=?";
			
			values.add(approve_state);
			values.add(obj_id);
			values.add(approve_state);
			values.add(new java.sql.Date(new java.util.Date().getTime()));
			values.add(approve_proc);
			values.add(planId);
			values.add(quarter);
			dao.update(sql, values);
			//发送代办,将自己的待办置为已办
			this.rejectPending(planId, obj_id, quarter);
			return "已成功退回！";
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 负责人主动撤回总结
	 */
	public void revocation(Integer planId,Integer quarter) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String approve_proc = this.getProcessXml(planId, "01", quarter,"");
			String sql = "select * from per_yearplan_approve where";
			List values = new ArrayList();
			sql = "update per_yearplan_approve set Approve_state='01',cur_user='',approve_proc=?";
			sql+=" where p1700=? and quarter=?";
			values.add(approve_proc);
			values.add(planId);
			values.add(quarter);
			dao.update(sql,values);
			
			
			//代办置为 无效
			String ext_flag = "WP_P17_SP_"+planId+"_"+quarter;
			String receiver = "";
			
			sql = "select * from per_yearplan_obj where P1700="+planId+" and obj_type in ('3','8')";
			rs = dao.search(sql);
			StringBuffer guidkeBuf = new StringBuffer();//审批人
			while(rs.next()){
				if("3".equals(rs.getString("obj_type"))){
					if(rs.getString("obj_id")!=null)
						receiver=rs.getString("obj_id");
				}else if("8".equals(rs.getString("obj_type"))){
					if(rs.getString("obj_id")!=null)
						guidkeBuf.append(rs.getString("obj_id")+",");
				}
			}
			String[] nbases = getNbase();
			String userName = this.getLoginUserName();
			if(receiver.length()==0 && guidkeBuf.length()>1){//没有审核人
				String temp = guidkeBuf.substring(0,guidkeBuf.length()-1);//审批人可能有多个
				String[] arr =  temp.split(",");
				for(int i=0;i<arr.length;i++){
					for(int j=0;j<nbases.length;j++){
						String nbase = nbases[j];
						sql = "select "+userName+" from "+nbase+"A01 where GUIDKEY ='"+arr[i]+"'";//查询审批人的用户名
						rs = dao.search(sql);
						if(rs.next()){//代办为无效
							String pending_id = pendingBo.isHavePendingtask(rs.getString(userName), ext_flag);
							if(StringUtils.isNotBlank(pending_id))
								pendingBo.updatePending("9", pending_id);
							break;
						}
					}
				}
						
			}
			if(receiver.length()>0){//有审核人
				for(int j=0;j<nbases.length;j++){
					String nbase = nbases[j];
					sql = "select "+userName+" from "+nbase+"A01 where GUIDKEY ='"+receiver+"'";//查询审批人的用户名
					rs = dao.search(sql);
					if(rs.next()){//重置代办为未阅 未办
						String pending_id = pendingBo.isHavePendingtask(rs.getString(userName), ext_flag);
						if(StringUtils.isNotBlank(pending_id))
							pendingBo.updatePending("9", pending_id);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * year为空时，返回查询年度sql
	 * 否则     返回指定年度下的计划sql
	 * @param year
	 * @return
	 * @throws GeneralException
	 */
	public String getPlanSql(String year) throws GeneralException {
		StringBuffer sql =new StringBuffer();
		try {
			YearPlanBo bo = new YearPlanBo(userView, conn);
			String guidkey = "";
			if(StringUtils.isNotBlank(this.userView.getDbname())){
				String id  = this.userView.getDbname()+this.userView.getA0100();
				guidkey = bo.getGuidKey(id,null);
			}
			//首先查询处当前登录用户的相关任务
			if(StringUtils.isNotBlank(year))
				sql.append("select * from p17 where p1700 in (");
			else
				sql.append("select distinct(p1701) from p17 where p1700 in (");
			sql.append("(select p17.p1700 from p17,per_yearplan_obj obj where ");
			sql.append("obj.P1700=p17.p1700 and p17.P1743='05' and obj.obj_id='"+guidkey+"' and obj.obj_type='7' ");		
			if(StringUtils.isNotBlank(year)){
				sql.append(" and p17.p1701="+year );
			}
			sql.append(" union all ");		
			sql.append("select p17.p1700 from p17,per_yearplan_obj obj,per_yearplan_approve ap ");		
			sql.append("where obj.P1700=ap.p1700 and obj.P1700=p17.p1700 and p17.P1743='05' AND obj.obj_id='"+guidkey+"' ");	
			if(StringUtils.isNotBlank(year)){
				sql.append(" and p17.p1701="+year );
			}
			sql.append(" AND obj.obj_type='3' AND ap.approve_state NOT IN('01')  AND ap.approve_state IS NOT NULL ");	
			
			sql.append("union all ");		
			sql.append("select p17.p1700 from p17,per_yearplan_obj obj,per_yearplan_approve ap ");		
			sql.append("where obj.P1700=ap.p1700 and obj.P1700=p17.p1700 and p17.P1743='05' AND obj.obj_id='"+guidkey+"' ");	
			if(StringUtils.isNotBlank(year)){
				sql.append(" and p17.p1701="+year );
			}
			sql.append(" AND obj.obj_type='8' AND ap.approve_state NOT IN('01','08')  AND ap.approve_state IS NOT NULL) ");	
			if(StringUtils.isNotBlank(year))
				sql.append(")order by P1735");
			else
				sql.append(")order by p1701");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return sql.toString();
	}
	/**
	 * @throws GeneralException 
	 * 退回时发代办,并且将自己的待办置为已办
	 * @throws  
	 */
	private void rejectPending(Integer planId,String receiver,Integer quarter) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String ext_flag = "WP_P17_SP_"+planId+"_"+quarter;
			String sender = this.userView.getUserName();//业务用户关联自主用户的用户名
			if(StringUtils.isBlank(sender))
				sender = this.userView.getUserName();
			String senderName = userView.getUserFullName();
			
			//将自己待办置为已办
			this.pendingOK(sender, ext_flag);
			
			Map planInfo = getPlanInfo(planId);
			String  planName = (String)planInfo.get("P1705");
			Integer  year = (Integer)planInfo.get("P1701");
			if(planName!=null && planName.length()>10)
				planName = planName.substring(0, 10)+"...";
			
			LazyDynaBean bean = new LazyDynaBean();
			String pending_url = "/module/workplan/yearplan/YearPlanTrace.html?b_query=link`planId="+planId+"`quarter="+quarter+"`year="+year;
			String pending_title = "";
			pending_title =senderName+"退回了您"+year+"年度计划【"+planName+"】的"+convertQuarter(quarter)+"季度总结";
			bean.set("pending_url", pending_url);
			bean.set("pending_title", pending_title);
			
			String[] nbases = getNbase();
			String userName = this.getLoginUserName();
			
			for(int i=0;i<nbases.length;i++){
				String nbase = nbases[i];
				String sql = "select "+userName+" from "+nbase+"A01 where GUIDKEY ='"+receiver+"'";
				rs = dao.search(sql);
				if(rs.next()){
					pendingBo.insertPending(sender, rs.getString(userName), ext_flag, bean);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 报批时添加代办（有退回的待办先置为已办）
	 * @param sender
	 * @param receiver
	 * @param bean
	 * @param senderName,
	 * @param receiverType
	 * 			接受代办的人的角色
	 * @param quarter
	 * @throws GeneralException 
	 */
	private void toApprovePending(Integer planId,String receivers,Integer quarter,String receiverType) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			//待办的发送者
			String sender = userView.getUserName();
			if(StringUtils.isBlank(sender))
				sender=userView.getUserName();
			String senderName = userView.getUserFullName();
			String ext_flag = "WP_P17_SP_"+planId+"_"+quarter;
			Map planInfo = getPlanInfo(planId);
			String  planName = (String)planInfo.get("P1705");
			Integer  year = (Integer)planInfo.get("P1701");
			if(planName!=null && planName.length()>10)
				planName = planName.substring(0, 10)+"...";
			LazyDynaBean bean = new LazyDynaBean();
			String pending_url = "/module/workplan/yearplan/YearPlanTrace.html?b_query=link`planId="+planId+"`quarter="+quarter+"`year="+year;
			String pending_title = "";
			if("3".equals(receiverType))
					pending_title =senderName+year+"年度计划【"+planName+"】的"+convertQuarter(quarter)+"季度总结(审核)";
			else if("8".equals(receiverType))
					pending_title =senderName+year+"年度计划【"+planName+"】的"+convertQuarter(quarter)+"季度总结(审批)";
			bean.set("pending_url", pending_url);
			bean.set("pending_title", pending_title);
			String receiver ="";
			if(!StringUtils.isEmpty(receivers)){
				String[] recevierArr = receivers.split(",");
				String temp = "";
				for(int i=0;i<recevierArr.length;i++){
					if(i==0)
						temp +="'"+recevierArr[i]+"'";
					else
						temp +=",'"+recevierArr[i]+"'";
				}
				String[] nbases = getNbase();
				String userName = this.getLoginUserName();
				for(int i=0;i<nbases.length;i++){
					String nbase = nbases[i];
					String sql = "select "+userName+" from "+nbase+"A01 where GUIDKEY in("+temp+")";
					rs = dao.search(sql);
					while(rs.next()){
						receiver=rs.getString(userName);
						//多个审批人时 插入多个代办
						String pendingId = pendingBo.isHavePendingtask(receiver, ext_flag);
						if(StringUtils.isBlank(pendingId))
							pendingBo.insertPending(sender, receiver, ext_flag, bean);
						else
							pendingBo.updatePending("", pendingId);//重新更新为未办 未阅
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/** 应用库 */
	public String[] getNbase() {
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
		if (login_vo != null) {
			String strpres = login_vo.getString("str_value");
			return strpres.split(",");
		}
		
		return new String[0];
	}
	/**
	 * 查询计划名称
	 * @throws GeneralException 
	 */
	private Map getPlanInfo(Integer planId) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		Map map = new HashMap();
		try {
			String sql = "select * from P17 where P1700=?";
			List values = new ArrayList();
			values.add(planId);
			rs = dao.search(sql,values);
			if(rs.next()){
				map.put("P1705",rs.getString("P1705"));
				map.put("P1701", rs.getInt("P1701"));
			}
			
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 将待办置为已办
	 * @param receiver
	 * @param ext_flag
	 */
	private void pendingOK(String receiver,String ext_flag){
		String pendingId = pendingBo.isHavePendingtask(receiver, ext_flag);
		if(StringUtils.isNotBlank(pendingId)){
			pendingBo.updatePending("1", pendingId);//更新为已办
		}
	}
	/**
	 * 查询 自助用户账号字段。
	 * @return
	 */
	private String getLoginUserName(){
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String username = "username";
        String login_name = login_vo.getString("str_value");
        int idx=login_name.indexOf(",");
        if(idx==-1)
        {
        	
        }else{
        	String temp = login_name.substring(0,idx);
        	if(DataDictionary.getFieldItem(temp)!=null){
        		username = login_name.substring(0,idx).length()==0?"username":temp;
        	}
        	
        }
        
        
        return username;
	}
	/**
	 * 将数字1234转换成一二三四
	 * @param quarter
	 * @return
	 */
	private String convertQuarter(int quarter){
		switch (quarter) {
		case 1:
			return "第一";
		case 2:
			return "第二";
		case 3:
			return "第三";
		case 4:
			return "第四";
		default:
			break;
		}
		return "";
	}
}
