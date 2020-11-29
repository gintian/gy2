package com.hjsj.hrms.module.recruitment.resumecenter.businessobject;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitFlowLink;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:ResumeBo</p>
 * <p>Description:简历类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-02-04</p>
 * @author wangcq
 * @version 1.0
 * 
 */
public class ResumeBo {

	private Connection conn;
	private UserView userview;
	private String resumeid;  //简历ID
	private String pre;  //人员库
	
	public ResumeBo(){}
	
	public ResumeBo(Connection conn, String resumeid, String pre){
		this.conn = conn;
		this.resumeid = resumeid;
		this.pre = pre;
	}
	public ResumeBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
	public ResumeBo(Connection conn){
		this.conn = conn;
	}
	/**
	 * 获取邮件地址指标
	* @Title:getEmailItemId
	* @Description：
	* @author xiexd
	* @return
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	public String getEmailItemId() throws SQLException, GeneralException
	{
		String emailId = "";
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_EMAIL'");
			RowSet rs = dao.search(sql.toString());
			if(rs.next())
			{
				emailId = rs.getString("Str_Value");
			}
			FieldItem fieldItem = DataDictionary.getFieldItem(emailId);
			if("#".equalsIgnoreCase(emailId)||emailId.length()<4||fieldItem==null||!"1".equals(fieldItem.getUseflag()))
			{
				throw GeneralExceptionHandler.Handle(new Exception("电子邮箱指标无效！"));
			}
		return emailId;
	}
	/**
	 * 根据简历id号、岗位号查询姓名、简历投递及状态信息
	 * @param zp_pos_id   岗位ID
	 * @param evaluation true 简历评价 
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getResumeHead(String zp_pos_id,UserView userView, boolean evaluation) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		HashMap map = new HashMap();
		RowSet recset=null; 
		try {
			ArrayList values = new ArrayList();
			StringBuffer sqlstr = new StringBuffer("");
			String emailItemId = this.getEmailItemId();
			//查询用户名
			values.add(this.resumeid);
			sqlstr.append("select a0101");
			if(emailItemId.length()>0)
			{				
				sqlstr.append(","+emailItemId);
			}
			sqlstr.append(" from "+this.pre+"a01 where a0100=?");
			recset = dao.search(sqlstr.toString(), values);
			if(recset.next()){
			    map.put("username", recset.getString("a0101"));
			    if(emailItemId.length()>0)
				{				
					map.put("email", recset.getString(emailItemId));
				}else{
					map.put("email", "");
				}
			}
			
			//查询更新时间、简历状态
			sqlstr.delete(0, sqlstr.length());
			if(zp_pos_id!=null&&zp_pos_id.length()>10)
			{
				zp_pos_id = PubFunc.decrypt(zp_pos_id);
			}
			values.add(zp_pos_id);
			sqlstr.append("select "+Sql_switcher.year("recdate")+"year,"+Sql_switcher.month("recdate")+"month,"+Sql_switcher.day("recdate")+"day,status,z0351 from zp_pos_tache,z03 where zp_pos_tache.zp_pos_id=z03.z0301 and a0100=? and zp_pos_id=? ");
			recset = dao.search(sqlstr.toString(), values);
			if(recset.next()){
				String recdate = "";
				if(StringUtils.isNotEmpty(recset.getString("year")))
					recdate = recset.getString("year") + "年" + recset.getString("month") + "月" + recset.getString("day") + "日";
				map.put("recdate", recdate);
				map.put("status", recset.getString("status"));
			}
			
			RecruitPrivBo rpbo = new RecruitPrivBo();
			//查询最新应聘职位
			sqlstr.delete(0, sqlstr.length());
			values.remove(1);
			values.add(this.resumeid);
			sqlstr.append("select z.z0351,z.z0333,z.z0301 from zp_pos_tache,z03 z where zp_pos_tache.zp_pos_id=z.z0301 and a0100=? and thenumber=(select min(thenumber) from zp_pos_tache where a0100=?) ");
			//邀请简历评价不加权限
			if(!evaluation){
				sqlstr.append(" and ");
				sqlstr.append(rpbo.getPositionWhr(userView));
			}
			recset = dao.search(sqlstr.toString(), values);
			ArrayList lastPosList = new ArrayList();
			while(recset.next()){
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("position", recset.getString("z0351")!=null?recset.getString("z0351"):"");
				abean.set("place", recset.getString("z0333")!=null?recset.getString("z0333"):"");
				abean.set("zp_pos_id", recset.getString("z0301"));
				abean.set("zp_pos_id_encry", PubFunc.encrypt(recset.getString("z0301")));
				lastPosList.add(abean);
			}
			map.put("lastPos", lastPosList);
			
			//查询其它应聘职位
			sqlstr.delete(0, sqlstr.length());
			sqlstr.append("select z.z0351,z.z0333,z.z0301 from zp_pos_tache,z03 z where zp_pos_tache.zp_pos_id=z.z0301 and a0100=? and thenumber>(select min(thenumber) from zp_pos_tache where a0100=?) ");
			//邀请简历评价不加权限
			if(!evaluation){
				sqlstr.append(" and ");
				sqlstr.append(rpbo.getPositionWhr(userView));
			}
			sqlstr.append(" order by thenumber");
			recset = dao.search(sqlstr.toString(), values);
			ArrayList othPosList = new ArrayList();
			while(recset.next()){
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("position", recset.getString("z0351")!=null?recset.getString("z0351"):"");
				abean.set("place", recset.getString("z0333")!=null?recset.getString("z0333"):"");
				abean.set("zp_pos_id", recset.getString("z0301"));
				abean.set("zp_pos_id_encry", PubFunc.encrypt(recset.getString("z0301")));
				othPosList.add(abean);
			}
			map.put("othPos", othPosList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(recset);
		}
		return map;
	}

	/**
	 * 获取简历中各个模块的信息集合
	 * @param z0301 职位id
	 * @return
	 */
	public ArrayList getSubModuleInfo(String z0301) throws Exception{
		ArrayList modulelist = new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			//根据职位id获取招聘渠道
			String sql = "select Z0336 from Z03 where Z0301=?";
			ArrayList<String> value = new ArrayList<String>();
			value.add(z0301);
			rs = dao.search(sql, value);
			String hireChannel = "";
			if(rs.next())
				hireChannel = rs.getString("Z0336");
			
			EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.conn);
            ArrayList list = employNetPortalBo.getSetByWorkExprience(hireChannel);
            ArrayList fieldList = (ArrayList) list.get(0);
			if(list.get(0)==null || fieldList.isEmpty())
				throw new Exception("代码"+hireChannel+"所对应的渠道没有维护简历指标参数！");
            HashMap resumeBrowseSetMap = new HashMap(); //应聘者各子集里的信息集合
            HashMap setShowFieldMap = new HashMap(); //子集显示 列 map
			for (int i = 0; i < ((ArrayList) list.get(0)).size(); i++) {
				LazyDynaBean abean = (LazyDynaBean) ((ArrayList) list.get(0)).get(i);
                String setID = (String)abean.get("fieldSetId");
                if(StringUtils.equalsIgnoreCase(setID, "a01")){
                	ArrayList resumeFieldList = employNetPortalBo.getResumeFieldList2((ArrayList) list.get(0), (HashMap) list.get(2), 0,
                			(HashMap) list.get(1), this.resumeid, this.pre, "0", true);
                	resumeBrowseSetMap.put(setID.toLowerCase(), resumeFieldList);
                }else{
                	ArrayList showFieldList = employNetPortalBo.getShowFieldList(setID, (HashMap) list.get(2), (HashMap) list.get(1), 0); //取得简历子集 列表需显示的 列指标 集合
                    ArrayList showFieldDataList = employNetPortalBo.getShowFieldDataList(showFieldList, this.resumeid, setID, this.pre);
                    resumeBrowseSetMap.put(setID.toLowerCase(), showFieldDataList);
                    setShowFieldMap.put(setID.toLowerCase(), showFieldList);
                }
			}
			modulelist.add(list.get(0));
			modulelist.add(resumeBrowseSetMap);
			modulelist.add(setShowFieldMap);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return modulelist;
	}
	
	
	/**
	 * 获取下个人员简历的相关信息
	 * @param searchSql 简历中心|人才库 查询语句
	 * @param querySql 查询条件
	 * @param filterSql 过滤 
	 * @param sortSql 
	 * @param row  被选中数据在所有数据中的定位
	 * @return
	 */
	public ArrayList<HashMap> getNextResume(String searchSql, String querySql, String filterSql, String a0100, int current, int pagesize, int rowindex, String sortSql){
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		HashMap resumeMap = new HashMap();
		RowSet recset=null;
		RowSet rs=null;
		try {
			if(StringUtils.isBlank(sortSql)){
				sortSql = " order by recdate desc";
			}
			StringBuffer sqlstr = new StringBuffer("");
			sqlstr.append("select * from ( ");
			sqlstr.append(searchSql);
			sqlstr.append(filterSql);
			sqlstr.append(") myGridData where 1=1");
			sqlstr.append(querySql);
			recset = dao.search("select a0100,nbase from ("+sqlstr+") temp "+sortSql);
			
			int row = (current-1)*pagesize + rowindex + 1;
			//取出的数据有同一个人的多条信息，取上一条时需要取row的最小值即循环开始的第一个值，向下时需要取row的最大值即row最终值
			int minIndex = row;
			//取完相同的数据后尽快结束循环
			boolean flag = false;
			
			int n = 0;
			//从所有记录中获取下一条记录的位置
			while(recset.next())
			{
				//找到当前记录的位置
				if(a0100.equalsIgnoreCase(recset.getString("a0100")))
				{	
					flag = true;
					row = recset.getRow();
					if(n == 0) {
						minIndex = row;
					}
					n++;
					continue;
				}
				if(flag) {
					break;
				}
			}
			for(int i=0;i<2;i++){
				resumeMap = new HashMap();
				if(i==0){
					if(recset.absolute(row)){
						if(recset.next()){
							resumeMap = this.getResumeMap(recset, current, pagesize, rowindex);
						}
					}
				}else{
					recset.beforeFirst();
					if(minIndex-2==0){
						recset.beforeFirst();
						if(recset.next())
							resumeMap = this.getResumeMap(recset, current, pagesize, rowindex);
					}else if(minIndex-2==-1){//当前是第一条数据，取上一条信息(修改为第一条数据时不能点上一条)
						/*recset.last();
						resumeMap= this.getResumeMap(recset, current, pagesize, rowindex);*/
					}else if(recset.absolute(minIndex-2)){
						if(recset.next())
							resumeMap= this.getResumeMap(recset, current, pagesize, rowindex);
					}else{
						recset.afterLast();
						recset.previous();
						if(!StringUtils.equalsIgnoreCase(this.resumeid, recset.getString("a0100"))){  //当只有一条人员信息时，便没有下一条人员信息
							resumeMap.put("nextResumeid", recset.getString("a0100"));
							resumeMap.put("nextNbase", recset.getString("nbase"));
							resumeMap.put("nextCurrent", new Integer(1));
							resumeMap.put("nextPagesize", new Integer(pagesize));
							resumeMap.put("nextRowindex", new Integer(0));
						}
					}
				}
				if(resumeMap.get("nextResumeid") != null){
					StringBuffer sql = new StringBuffer("");
					ArrayList values = new ArrayList();
					values.add(resumeMap.get("nextResumeid"));
					values.add(resumeMap.get("nextResumeid"));
					sql.append("select zp_pos_id from zp_pos_tache where a0100=? and thenumber=(select min(thenumber) from zp_pos_tache where a0100=?)");
					rs = dao.search(sql.toString(), values);
					if(rs.next())
						resumeMap.put("nextZp_pos_id", rs.getString("zp_pos_id"));
				}
				list.add(resumeMap);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(recset);
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/***
	 * 获取当前环节下的所有操作状态
	 * @param flowId 流程序号
	 * @param Node_id 环节序号
	 * @return
	 */
	public ArrayList getOperationList(String z0301,String link_id,String flowId, UserView userview)
	{
		RecruitFlowLink rfl = new RecruitFlowLink(userview, z0301, flowId, link_id, z0301, conn);
		ArrayList operationList = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			operationList=rfl.getResumeFunctionList();
			
			RecordVo z03Vo=new RecordVo("z03");
			z03Vo.setString("z0301",z0301);
			z03Vo=dao.findByPrimaryKey(z03Vo);
			if("06".equalsIgnoreCase(z03Vo.getString("z0319"))|| "09".equalsIgnoreCase(z03Vo.getString("z0319")))
			{
				operationList.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return operationList;
	}
	/**
	 * 判断当前人员是否为招聘人员
	 * @param z0301岗位编号
	 * @return
	 */
	private boolean getMembers(String z0301,UserView userview)
	{
		boolean flag = false;
		try {
			if(userview.isSuper_admin())
			{
				return true;
			}
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer();
			sql.append("select * from zp_members where z0301=? and ((nbase=? and a0100=?) or a0101=?) and member_type<>'4'");
			ArrayList list = new ArrayList();
			list.add(z0301);
			list.add(userview.getDbname());
			list.add(userview.getA0100());
			list.add(userview.getUserFullName());
			RowSet rs = dao.search(sql.toString(), list);
			if (rs.next()) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/***
     * 根据流程号获取流程名
     * @param link_id
     * @return
     */
    public LazyDynaBean getCustom_name(String link_id)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select custom_name from zp_flow_links where id=?");
			ArrayList list = new ArrayList();
			list.add(link_id);
			RowSet rs = dao.search(sql.toString(), list);
			if(rs.next())
			{
				bean.set("custom_name", rs.getString("custom_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
    }
    
    /**
     * 判断人才库中是否有相应人员
     * @param a0100
     * @param nbase
     * @param username
     * @return
     */
    public boolean existTalent(String a0100, String nbase, String username){
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet recset=null;
		boolean exist = false;
		StringBuffer sqlstr = new StringBuffer();
		ArrayList list = new ArrayList();
		list.add(a0100);
		list.add(nbase);
		list.add(username);
		sqlstr.append("select * from zp_talents where a0100=? and nbase=? and create_user=?");
		try {
			recset = dao.search(sqlstr.toString(),list);
			exist = recset.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(recset);
		}
		return exist;
	}
    
    /*****
     * 用于控制推荐职位或者接受职位申请
     * 查询当前申请职位流程状态    0:不控制  1：已入职 
    * @Title:getNode_flag
    * @Description：
    * @author xiexd
    * @param a0100 人员编号
    * @param zp_pos_id 职位编号]
    * @param nbase 人员库前缀
    * @return
     */
    public String getNode_flag(String a0100,String zp_pos_id,String nbase){
    	String flag = "0";
    	RowSet rs = null;
    	try {
			String sql = "select 1 from zp_pos_tache where resume_flag='1003' and a0100=? and nbase=?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(a0100.trim());
			list.add(nbase.trim());
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql, list);
			if(rs.next()) {
				flag = "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	return flag;
    }
    
    /****
     * 人员简历头部信息
    * @Title:getResumeInfo
    * @Description：
    * @author xiexd
    * @param nbase 人员库
    * @param a0100 人员编号
    * @param zp_pos_id 职位编号
    * @return
     */
    public LazyDynaBean getResumeInfo(String nbase,String a0100,String zp_pos_id,HashMap map)
    {
    	LazyDynaBean info = new LazyDynaBean();
    	RowSet rs = null;
    	String colorflg = "";
    	ContentDAO dao = new ContentDAO(conn);
    	try {
    		/**************************查询姓名***************************/
			StringBuffer sql = new StringBuffer("select a0101 from "+nbase+"A01 where a0100=?");
    		ArrayList value = new ArrayList();
    		value.add(a0100);
    		rs = dao.search(sql.toString(), value);
    		
    		if(rs.next())
    		{
    			//添加用户姓名
    			info.set("name", rs.getString("a0101")==null?"":rs.getString("a0101").toString());
    		}
    		/**************************查询职位***************************/
			sql = new StringBuffer("select zpt.description,zpt.zp_pos_id,zpt.status,z03.z0319");
			sql.append(" ,case when "+Sql_switcher.isnull("z03.z0333","0")+"='0' then z03.z0351 else z03.z0351 "+Sql_switcher.concat()+"' - '"+Sql_switcher.concat()+" z03.z0333 end as position");
		 	sql.append(" ,case when "+Sql_switcher.isnull("parentitem.codeitemdesc","0")+"='0' then '' else parentitem.codeitemdesc"+Sql_switcher.concat()+"' - '"+Sql_switcher.concat()+"childitem.codeitemdesc end as codeitemdesc");
			sql.append(" from zp_pos_tache zpt");
			sql.append(" left join z03 on zpt.zp_pos_id=z03.z0301");
			sql.append(" left join (select * from codeitem where codesetid='36') childitem on zpt.resume_flag = childitem.codeitemid");
			sql.append(" left join (select * from codeitem where codesetid='36') parentitem on childitem.parentid = parentitem.codeitemid");
			sql.append(" where zpt.a0100=? and zpt.nbase=? order by thenumber");
			value.add(nbase);
			rs = dao.search(sql.toString(), value);
			ArrayList zplist = new ArrayList();
			int positionno = 0;
			
			while(rs.next())
			{
				positionno ++;
				LazyDynaBean zpbean = new LazyDynaBean();
				zpbean.set("zp_pos_id", PubFunc.encrypt(rs.getString("zp_pos_id")));
				String flag = this.getPositionPriv(rs.getString("zp_pos_id"));
				zpbean.set("priv", flag);
				zpbean.set("position", rs.getString("position") == null ? "" : rs.getString("position"));
				zpbean.set("z0319", rs.getString("z0319"));
				Object ss = rs.getString("status");
				String status = "";
				if(ss != null)
				    status = ss.toString();
				
				if(StringUtils.equalsIgnoreCase("0", status) || "".equalsIgnoreCase(status))
				{
					status = "未处理";
				}else if(StringUtils.equalsIgnoreCase("1", status))
				{
					status = "处理中";
				}else if(StringUtils.equalsIgnoreCase("2", status))
				{
					status = "已处理";
				}
				zpbean.set("status", status);
				zpbean.set("codeitemdesc", rs.getString("codeitemdesc"));
				zpbean.set("positionno", "position"+positionno);
				if(StringUtils.equalsIgnoreCase(zp_pos_id, rs.getString("zp_pos_id")))
				{
					colorflg = (String)zpbean.get("positionno");//用于前台渲染颜色
				}
				zpbean.set("description",StringUtils.isEmpty(rs.getString("description"))?0:1);
				zplist.add(zpbean);
			}
			/**************************第一志愿***************************/
			if(zplist.size()>0)
			{				
				info.set("first", (LazyDynaBean)zplist.get(0));
				//移除第一志愿
				zplist.remove(0);
			}else{
				info.set("first", "0");
			}
			/**************************其它志愿***************************/
			if(zplist.size()>0)
			{
				info.set("other", zplist);
			}else{
				info.set("other", "0");
			}
			/**************************当前显示操作***************************/
			info.set("operate", this.getOperateList(zp_pos_id, nbase, a0100, map));
			String link_id = (String)map.get("link_id");
			if(link_id!=null&&!StringUtils.equalsIgnoreCase(link_id,""))
			{
				info.set("link_name", this.getCustom_name(link_id).get("custom_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	info.set("colorflg", colorflg);
    	return info;
    }
    
    /***
     * 获取当前人员是否有操作权限
    * @Title:getPositionPriv
    * @Description：
    * @author xiexd
    * @date 2016-1-20
    * @param z0301
    * @return
     */
    private String getPositionPriv(String z0301)
    {
    	String flag = "0";
    	RowSet rs = null;
    	try {
    		RecruitPrivBo bo = new RecruitPrivBo();
			StringBuffer sql = new StringBuffer(" select z0301 from Z03 where z0301=? ");
			sql.append(" and (");
			if (this.userview.isSuper_admin() || this.userview.isAdmin())
			{
				sql.append(" 1=1");
			}else{				
				sql.append(" ((");
				//拼接需求单位权限
				sql.append("((z0325 is null or z0325='' )");
				sql.append(" and ("+bo.getPrivB0110Whr(this.userview,"z0321",RecruitPrivBo.LEVEL_SELF_CHILD)+"))");
				
				//拼接需求部门权限
				sql.append(" or ((z0325 is not null ");
				if(Sql_switcher.searchDbServer()==Constant.MSSQL){
					sql.append(" and z0325<>''");
				}
				sql.append(" ) ");
				sql.append(" and  ("+bo.getPrivB0110Whr(this.userview,"z0325",RecruitPrivBo.LEVEL_SELF_CHILD)+"))");
				sql.append(" ) "); 
				 //拼接我的职位（创建人）
				sql.append(" or  z0309='"+this.userview.getUserName()+"' "); 
		        //负责人、招聘成员、部门负责人
		        if(this.userview.getA0100().length()>0)
		        {
		        	sql.append(" or z0301 in ( select z0301 from zp_members ");
		        	sql.append(" where a0100 = '"+this.userview.getA0100()+"' and nbase='"+this.userview.getDbname()+"' ) ");
		        }
		        sql.append(" ) "); 
			}
	        sql.append(") ");
			ArrayList list = new ArrayList();
			list.add(z0301);
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString(), list);
			while(rs.next())
			{
				flag = "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return flag;
    }
    /****
     * 当前职位对当前人员所含状态操作
    * @Title:getOperateList
    * @Description：
    * @author xiexd
    * @param zp_pos_id
    * @param nbase
    * @param a0100
    * @return
     */
    public ArrayList getOperateList(String zp_pos_id,String nbase,String a0100,HashMap map){
    	ArrayList operate = new ArrayList();
    	map.put("zp_pos_id", zp_pos_id);
    	map.put("nbase", nbase);
    	map.put("a0100", a0100);
    	String link_id = (String)map.get("link_id");
    	if(StringUtils.isNotEmpty(link_id))
    	{
    		operate = this.getOperationList(zp_pos_id, link_id, (String) map.get("flowId"), userview);
    	}else{    		
    		operate = this.getPositionOperateList(map);
    	}
    	return operate;
    }
    
    /****
     * 获取职位操作的按钮集合
    * @Title:getPositionOperateList
    * @Description：
    * @author xiexd
    * @param map
    * @return
     */
    private ArrayList getPositionOperateList(HashMap map){
    	ArrayList operate = new ArrayList();
    	RowSet rs = null;
    	RowSet search = null;
    	ContentDAO dao = new ContentDAO(conn);
    	try {
    		StringBuffer sql = new StringBuffer("select resume_flag, status,a0100 from zp_pos_tache where a0100=? and nbase=?");
    		ArrayList list = new ArrayList();
    		list.add(map.get("a0100"));
    		list.add(map.get("nbase"));
    		if(map.get("zp_pos_id")!=null&&map.get("zp_pos_id").toString().length()>0)
    		{
    			sql.append(" and zp_pos_id=?");
    			list.add(map.get("zp_pos_id"));
    		}
    		rs = dao.search(sql.toString(),list);
    		String flag = "false";//按钮是否可操作
    		String status = "";
    		Boolean resumeFlag = true;
    		if(rs.next())
    		{
    			status = rs.getString("status");
    			if("1003".equalsIgnoreCase(rs.getString("resume_flag")))
    			{
    				resumeFlag = false;
    			}
    			
    			if(!StringUtils.equalsIgnoreCase("1",status))
    			{
    				flag = "true";
    			}
    		}

			if(map.get("zp_pos_id")!=null&&map.get("zp_pos_id").toString().length()>0)
			{
				/******************接受职位********************/
				if(!"1".equals(status))
				{
					LazyDynaBean ap = new LazyDynaBean();
					ap.set("text", "接受职位申请");
					ap.set("fn", "resume_me.acceptPositionApply()");
					ap.set("flag", flag);
					ap.set("able", "false");
					if(this.userview.hasTheFunction("3110203"))
					{
						ap.set("able", "true");
					}
					operate.add(ap);
				}
				
				if(!"2".equals(status))
				{
					/******************拒绝职位********************/
					if(this.userview.hasTheFunction("3110204"))
					{
						LazyDynaBean rp = new LazyDynaBean();
						rp.set("text", "拒绝职位申请");
						rp.set("fn", "resume_me.rejectPositionApply()");
						rp.set("flag", flag);
						rp.set("able", "false");
						rp.set("able", "true");
						if(!"false".equalsIgnoreCase(flag))
							operate.add(rp);
					}
				}
			}
			/******************推荐职位********************/
			if(this.userview.hasTheFunction("3110206"))
			{
				LazyDynaBean rmp = new LazyDynaBean();
				rmp.set("text", "推荐职位");
				rmp.set("fn", "resume_me.recommendOtherPosition(window.frames['ifra'].Global.resumeid,window.frames['ifra'].Global.nbase,window.frames['ifra'].Global.email)");
				rmp.set("flag", "true");
				rmp.set("able", "false");
				rmp.set("able", "true");
				operate.add(rmp);
			}
			if(this.userview.hasTheFunction("311020104"))
			{
				LazyDynaBean printPDF = new LazyDynaBean();
				printPDF.set("text", "导出简历PDF");
				printPDF.set("fn", "resume_me.exportResumePDF()");
				printPDF.set("flag", "true");
				printPDF.set("able", "false");
				printPDF.set("able", "true");
				operate.add(printPDF);
			}
			if(this.userview.hasTheFunction("311020105"))
			{
				LazyDynaBean printAX = new LazyDynaBean();
				printAX.set("text", "打印简历");
				printAX.set("fn", "resume_me.printAX()");
				printAX.set("flag", "true");
				printAX.set("able", "false");
				printAX.set("able", "true");
				operate.add(printAX);
			}
			/******************转人才库********************/
			if(this.userview.hasTheFunction("3110205") && resumeFlag)
			{
				LazyDynaBean jttp = new LazyDynaBean();
				jttp.set("text", "转人才库");
				jttp.set("id", "addId");
				jttp.set("fn", "window.frames['ifra'].Global.turnTalents()");
				jttp.set("flag", "true");
				if(this.existTalent(map.get("a0100").toString(), map.get("nbase").toString(), this.userview.getUserName()))
				{
					jttp.set("flag", "false");
				}
				jttp.set("able", "false");
				jttp.set("able", "true");
				if(!"false".equalsIgnoreCase((String) jttp.get("flag")))
					operate.add(jttp);
			}
			/******************移出人才库********************/
			if(this.userview.hasTheFunction("3110303"))
			{
				LazyDynaBean retl = new LazyDynaBean();
				retl.set("text", "移出人才库");
				retl.set("id", "removeId");
				retl.set("fn", "window.frames['ifra'].Global.removeTalents()");
				retl.set("flag", "false");
				if(this.existTalent(map.get("a0100").toString(), map.get("nbase").toString(), this.userview.getUserName()))
				{
					retl.set("flag", "true");
				}
				retl.set("able", "false");
				retl.set("able", "true");
				if("true".equalsIgnoreCase((String) retl.get("flag")))
					operate.add(retl);
			}
			/******************处理下一份简历********************/
			String hire_sql = (String)this.userview.getHm().get("hire_sql");//获取用户筛选简历的sql语句
			search = dao.search("select count(a0100) num from ("+hire_sql+") n");
			int row = 0;
			if(search.next())
				row = search.getInt("num");
			search.last();
			if(row>1){
				LazyDynaBean tn = new LazyDynaBean();
				tn.set("text", "上一份");
				tn.set("fn", "window.frames['ifra'].Global.lastResume();");
				tn.set("flag", "true");
				tn.set("able", "true");
				operate.add(tn);
				tn = new LazyDynaBean();
				tn.set("text", "下一份");
				tn.set("fn", "window.frames['ifra'].Global.nextResume();");
				tn.set("flag", "true");
				tn.set("able", "true");
				operate.add(tn);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(search);
		}
    	return operate;
    }
    
    /**
	 * 获取下个人员简历的相关信息
	 * @param 职位候选人查询语句
	 * @param row  被选中数据在所有数据中的定位
	 * @param zp_pos_id 职位id
     * @param sortSql 排序
     * @param querySql 查询条件
     * @param filterSql 过滤
	 * @return
	 */
	public ArrayList<HashMap> getCandidate(String searchSql, String querySql,String filterSql, String sortSql,String zp_pos_id,String a0100, int current, int pagesize, int rowindex){
		ContentDAO dao = new ContentDAO(conn);
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		HashMap resumeMap = new HashMap();
		RowSet recset=null;
		try {
			StringBuffer sqlstr = new StringBuffer("");
			sqlstr.append("select * from ( ");
			sqlstr.append(searchSql);
			sqlstr.append(filterSql);
			sqlstr.append(") myGridData where 1=1");
			sqlstr.append(querySql);
			//表格控件没有排序时，默认按照a0100排序
			//加上这个显示页面和查询下一个简历顺序可能不一致
			/*if(sortSql.length()==0)
				sqlstr.append(" order by myGridData.A0100 ASC");*/
			recset = dao.search(sqlstr.toString());
			int row = (current-1)*pagesize + rowindex + 1;
			//从所有记录中获取下一条记录的位置
			while(recset.next())
			{
				//找到当前记录的位置
				if(a0100.equalsIgnoreCase(recset.getString("a0100")))
				{	
					row = recset.getRow();
				}
			}
			String email = ConstantParamter.getEmailField().toLowerCase();
			for(int i = 0;i<2;i++){
				resumeMap = new HashMap();
				if(i==0){//下一份简历
					if(recset.absolute(row)){
						if(recset.next()){
							resumeMap = this.getResumeMap(recset, zp_pos_id, current, pagesize, rowindex);
						}else{
							recset.beforeFirst();
							recset.next();
							if(!StringUtils.equalsIgnoreCase(a0100, recset.getString("a0100"))){  //当只有一条人员信息时，便没有下一条人员信息
								resumeMap.put("nextResumeid", PubFunc.encrypt(recset.getString("a0100")));
								resumeMap.put("nextNbase", PubFunc.encrypt(recset.getString("nbase")));
								resumeMap.put("resume_flag", recset.getString("resume_flag")+"`"+recset.getString("resume_flag1"));
								resumeMap.put("resume_name", recset.getString("resume_flag1"));
								resumeMap.put("zp_pos_id", PubFunc.encrypt(zp_pos_id));
								if(StringUtils.isNotBlank(email))
									resumeMap.put("email", recset.getString(email));
								resumeMap.put("nextCurrent", new Integer(1));
								resumeMap.put("nextPagesize", new Integer(pagesize));
								resumeMap.put("nextRowindex", new Integer(0));
							}
						}
					}
				}else {//上一份简历
					recset.beforeFirst();
					if(row-2==0){
						recset.beforeFirst();
						if(recset.next())
							resumeMap = this.getResumeMap(recset, zp_pos_id, current, pagesize, rowindex);
					//【60652】没有“上一个”“下一个”按钮
					}else if(row-2>0&&recset.absolute(row-2)){
						if(recset.next())
							resumeMap= this.getResumeMap(recset, zp_pos_id, current, pagesize, rowindex);
					}else{
						recset.afterLast();
						recset.previous();
						if(!StringUtils.equalsIgnoreCase(a0100, recset.getString("a0100"))){  //当只有一条人员信息时，便没有下一条人员信息
							resumeMap.put("nextResumeid", PubFunc.encrypt(recset.getString("a0100")));
							resumeMap.put("nextNbase", PubFunc.encrypt(recset.getString("nbase")));
							resumeMap.put("resume_flag", recset.getString("resume_flag")+"`"+recset.getString("resume_flag1"));
							resumeMap.put("resume_name", recset.getString("resume_flag1"));
							resumeMap.put("zp_pos_id", PubFunc.encrypt(zp_pos_id));
							if(StringUtils.isNotBlank(email))
								resumeMap.put("email", recset.getString(email));
							resumeMap.put("nextCurrent", new Integer(1));
							resumeMap.put("nextPagesize", new Integer(pagesize));
							resumeMap.put("nextRowindex", new Integer(0));
						}
					}
				}
				list.add(resumeMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(recset);
		}
		return list;
	}

		
	/**
	 * @param rowSet
	 * @param zp_pos_id
	 * @param current
	 * @param pagesize
	 * @param rowindex
	 * @return
	 */
	private HashMap<String,Object> getResumeMap(RowSet rowSet,String zp_pos_id, int current, int pagesize, int rowindex){
		HashMap<String, Object> map = new HashMap<String, Object>();
		String email = ConstantParamter.getEmailField().toLowerCase();
		try {
			map.put("nextResumeid", PubFunc.encrypt(rowSet.getString("a0100")));
			map.put("nextNbase", PubFunc.encrypt(rowSet.getString("nbase")));
			map.put("nextPagesize", new Integer(pagesize));
			map.put("resume_flag", rowSet.getString("resume_flag")+"`"+rowSet.getString("resume_flag1"));
			map.put("resume_name", rowSet.getString("resume_flag1"));
			map.put("zp_pos_id", PubFunc.encrypt(zp_pos_id));
			if(StringUtils.isNotBlank(email))
				map.put("email", rowSet.getString(email));
			if(rowindex+2>pagesize){
				map.put("nextCurrent", new Integer(current+1));
				map.put("nextRowindex", new Integer(0));
			}else{
				map.put("nextCurrent", new Integer(current));
				map.put("nextRowindex", new Integer(rowindex+1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
	private HashMap<String,Object> getResumeMap(RowSet rowSet, int current, int pagesize, int rowindex){
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("nextResumeid", rowSet.getString("a0100"));
			map.put("nextNbase", rowSet.getString("nbase"));
			map.put("nextPagesize", new Integer(pagesize));
			if(rowindex+2>pagesize){
				map.put("nextCurrent", new Integer(current+1));
				map.put("nextRowindex", new Integer(0));
			}else{
				map.put("nextCurrent", new Integer(current));
				map.put("nextRowindex", new Integer(rowindex+1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
}
