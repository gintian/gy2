package com.hjsj.hrms.module.recruitment.recruitflow.businessobject;

import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * <p>Title:RecruitflowBo.java</p>
 * <p>Description>:招聘流程业务类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-1-22</p>
 * <p>@author:dengcan</p>
 * <p>@version: 7.x</p>
 */
public class RecruitflowBo {
	private Connection conn=null;
    private UserView userview;
    
    public RecruitflowBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    
    
    
    /**
     * 基于职位ID获得招聘流程第一环节的状态信息 
     * @param z0301   招聘职位id  
     * @return
     * @throws GeneralException
     */
    public LazyDynaBean getFirstStatusByZ0301(String z0301)throws GeneralException 
    { 
    	LazyDynaBean bean=null;
    	RowSet rowSet=null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		ArrayList valueList=new ArrayList();
    		String sql="select * from  zp_flow_links where flow_id=(select z0381 from z03 where z0301=? ) and valid=1   order by seq"; 
    		valueList.add(z0301);  
   		 
    		rowSet=dao.search(sql,valueList);
    		if(rowSet.next())
    		{
    			String _link_id=rowSet.getString("id"); 
    			rowSet=dao.search("select status from zp_flow_status where link_id='"+_link_id+"' and valid=1 order by seq ");
    			if(rowSet.next())
    			{
    				bean=new LazyDynaBean();
    				bean.set("link_id",_link_id);
    				bean.set("status",rowSet.getString(1)!=null?rowSet.getString(1):"");
    			}else{
    				rowSet=dao.search("select status from zp_flow_status where link_id='"+_link_id+"' order by seq ");//如果都不启用则进入第一个环节
    				if(rowSet.next())
        			{
        				bean=new LazyDynaBean();
        				bean.set("link_id",_link_id);
        				bean.set("status",rowSet.getString(1)!=null?rowSet.getString(1):"");
        			}
    			}
    			
    		}
    	}
    	catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    	finally
    	{
    		PubFunc.closeDbObj(rowSet);
    	}
    	return bean;
    }
    
    
    /**
     * 获得某招聘流程涉及的招聘环节
     * @param flow_id 招聘流程id 
     * @param z0301   招聘职位id
     * @param flag 1:包含所有环节  2：只包含有效环节
     * @return
     * @throws GeneralException
     */
    public ArrayList getLinkList(String flow_id,String z0301,int flag)throws GeneralException 
    {
    	ArrayList list=new ArrayList();
    	RowSet rowSet=null;
    	RowSet rowSet2=null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		ArrayList valueList=new ArrayList();
    		String sql="select * from  zp_flow_links where flow_id=? ";
    		valueList.add(flow_id);
    		if(flag==2) 
    			sql+=" and valid=1";  
    		sql+=" order by seq";
    		rowSet=dao.search(sql,valueList);
    		String statusSql = "select * from  zp_flow_status where link_id=? and valid=1 order by seq";
    		RecruitPrivBo privBo = new RecruitPrivBo();
    		boolean hasFlowLinkPriv = true;
    		while(rowSet.next())
    		{
    			String link_id=rowSet.getString("id");
    			//是否具有环节权限
    			hasFlowLinkPriv = privBo.hasFlowLinkPriv(conn, userview, z0301, flow_id, link_id);
    			ArrayList link = new ArrayList();
    			link.add(link_id);
    			rowSet2=dao.search(statusSql,link);
    			String node_id=rowSet.getString("node_id");
    			String custom_name=rowSet.getString("custom_name")!=null?rowSet.getString("custom_name"):"";
    			String seq=rowSet.getString("seq");
    			String valid=rowSet.getString("valid");
    			String status=rowSet.getString("node_id");
    			if(rowSet2.next()){
    				status=rowSet2.getString("status");
    			}
    			LazyDynaBean bean=new LazyDynaBean();
    			bean.set("linkPriv", Boolean.toString(hasFlowLinkPriv));
				bean.set("status", status);//第一个状态
    			bean.set("link_id",link_id); //招聘环节id
    			bean.set("node_id",node_id);  //招聘阶段id 关联代码36第一级代码
    			if(custom_name.length()==0) //如果没有自定义名称，采用代码名称
    				custom_name=AdminCode.getCodeName("36", node_id);
    			bean.set("custom_name",custom_name);  //名称
    			bean.set("seq",seq);     // 排序号
    			bean.set("valid",valid); //1：启用
    			// 如果z0301!=0 ,获得职位某环节下  新候选人数/所有候选人数
    			if(z0301!=null&&z0301.length()>0)
    			{
    				bean.set("new_number",String.valueOf(getCandidateNumber(z0301,link_id,node_id,1))); //新候选人数
    				bean.set("all_number",String.valueOf(getCandidateNumber(z0301,link_id,node_id,2))); //所有候选人数
    			}
    			list.add(bean);
    		}
    	}
    	catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    	finally
    	{
    		PubFunc.closeDbObj(rowSet);
    		PubFunc.closeDbObj(rowSet2);
    	}
    	return list;
    }
    
    
    /**
     * 获得职位某环节下候选人数
     * @param flag 1：新候选人  2：所有候选人
     * @param link_id:环节id
     * @param z0301:职位id
     * @return
     */
    public int getCandidateNumber(String z0301,String link_id,String node_id,int flag)throws GeneralException 
    {
    	int number=0;
    	RowSet rowSet=null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		String sql="select count(a0100) from zp_pos_tache where zp_pos_id=? and link_id=? ";
    		ArrayList valueList=new ArrayList();
    		valueList.add(z0301);
    		valueList.add(link_id);
    		if(flag==1)
    		{
    			sql+=" and status=1 and resume_flag=? ";
    			valueList.add(node_id+"01");
    		}
    		else
    		{
    			//ArrayList statusList=getValidStatusList();
    			sql+=" and status<>0 ";//and resume_flag not in ( ";
    			/*String temp="";
    			for(int i=0;i<statusList.size();i++)
    			{
    				temp+=",?";
    				valueList.add((String)statusList.get(i));
    			}
    			sql+=temp.substring(1)+" )";*/
    		}
    		rowSet=dao.search(sql,valueList);
    		if(rowSet.next())
    		{
    			number=rowSet.getInt(1);
    		}
    	}
    	catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    	finally
    	{
    		PubFunc.closeDbObj(rowSet);
    	}
    	return number;
    }
    
    
    /**
     * 获得各环节下候选人 已淘汰和放弃的状态
     * @return
     */
    private ArrayList getValidStatusList()
    {
    	ArrayList list=new ArrayList();
    	list.add("0105");
    	list.add("0205");
    	list.add("0306");
    	list.add("0307");
    	list.add("0406");
    	list.add("0407");
    	list.add("0503");
    	list.add("0603");
    	list.add("0705");
    	list.add("0904"); 
    	return list;
    }


    /**
     * 获得流程定义表中的流程
     * @param levelFlag 加载层级标志  0：加载公共+上级+本级+下级   1：加载公共+上级+本级 ； 2：加载本级+下级
     * @param flag   all:取全部满足权限流程     valid：只获取启用的满足权限流程
     * @return 
     * @author xiongyy
     * @throws GeneralException 
     */
    public ArrayList getRecruitflowList(int levelFlag,String flag) throws GeneralException {
    	RecruitPrivBo bo = new RecruitPrivBo();
    	String privB0110 = bo.getPrivB0110Whr(this.userview, "B0110", levelFlag);
        ArrayList list = new ArrayList();
        RowSet rs=null;
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select flow_id,name");
            sql.append(" from zp_flow_definition");
            sql.append(" where 1=1 ");
            if("valid".equalsIgnoreCase(flag))
            	sql.append(" and valid=1");
            sql.append(" and ").append(privB0110);
            sql.append(" order by flow_id");
            
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            while(rs.next()){
                list.add(rs.getString("flow_id")+"`"+rs.getString("name"));
                
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return list;
    }
    /**
     * 生成所有流程的下拉框
     * @return
     * @throws GeneralException 
     */
    public String[] getDefinition(String flowid) throws GeneralException{
    	StringBuffer html = new StringBuffer("<ul style=\"padding: 0;margin: 0;\" id=\"nameli\">");
		ArrayList flowList = this.getRecruitflowList(0,"all");
		try {
		    String flow_id = "";
		    String name = "";
		    String temp = "";
			for (int i = 0; i < flowList.size(); i++) {
				temp = (String) flowList.get(i);
				flow_id = PubFunc.encrypt(temp.split("`")[0]);
				name = temp.split("`")[1];
				if (flowid == null || flowid.length() < 1||"＇＇".equals(flowid)) {
					flowid = flow_id;
				}
				html.append("<li title='"+name+"'><div style='white-space:nowrap;overflow:hidden;text-overflow:ellipsis;width:90%;height:30px;'><a id=\""+PubFunc.decrypt(flow_id)+"\" href=\"javascript:void(0)\" onclick=\"searchFlow('" + flow_id+ "')\">&nbsp;" + name + "&nbsp;</a></div></li>");
			} 
			
			html.append("</ul>");
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return new String[]{flowid,html.toString()};
    }

	public LazyDynaBean getLazyDyna(String flowid) {
		LazyDynaBean flowBean = new LazyDynaBean();
		try {
			RowSet rs = null;
			String name = "";
			int valid = 1;
			String sql = "select flow_id,name,description,valid,b0110,codeitemdesc,seq_flag from zp_flow_definition,organization where flow_id='"+flowid+"' and b0110=codeitemid and b0110<>'UN`'" +
					"union all select flow_id,name,description,valid,b0110,'公共资源'  codeitemdesc,seq_flag from zp_flow_definition where flow_id='"+flowid+"' and b0110='UN`'";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if (rs.next()) {
				flowBean.set("flow_id", rs.getString("flow_id"));
				name = rs.getString("name");
				valid = rs.getInt("valid");
				flowBean.set("valid", valid);
				flowBean.set("b0110", rs.getString("b0110"));
				flowBean.set("codeitemdesc", rs.getString("codeitemdesc"));
				flowBean.set("name", PubFunc.nullToStr(name));
				String description = rs.getString("description");
				flowBean.set("description", PubFunc.nullToStr(description).replaceAll("\r\n", "\n"));
				//是否是上级流程
				flowBean.set("isParent", this.isParentFlow(rs.getString("flow_id"), name));
				flowBean.set("skipflag", rs.getString("seq_flag")==null?"0":rs.getString("seq_flag"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flowBean;
	}
	/**
	 * 判断指定流程是否为上级流程
	 * @param flowid 
	 * @param flowName
	 * @return
	 */
	public boolean isParentFlow(String flowid,String flowName){
		boolean res = false;
		RowSet rs =null;
		try{
			ArrayList allFlow = this.getRecruitflowList(0,"all");
			ArrayList selfAndChild = this.getRecruitflowList(2, "all");
			allFlow.removeAll(selfAndChild);
			
			if(StringUtils.isEmpty(flowName)){
				String sql = "select name from zp_flow_definition where flow_id='"+flowid+"'";
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search(sql);
				if(rs.next())
					flowName = rs.getString("name");
			}
			if(allFlow.indexOf(flowid+"`"+flowName) > 0)
				res = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return res;
	}
	/**
     * 判断未拖动节点后存在几个拖动的节点(当拖动方式为before时,计算后面节点数量，反之则计算前面节点数量)
     * @param dragSeqs
     * @param leftSeqs
     * @return
     */
    public ArrayList getUpSeq(ArrayList dragSeqs,ArrayList leftSeqs,String position,int xh){
    	ArrayList res = new ArrayList();
    	for (int i = 0; i < leftSeqs.size(); i++) {
    		String leftSeq = (String) leftSeqs.get(i);
    		int countNum=0;
    		if(Integer.parseInt(leftSeq)<xh){//除去在拖动节点位置之前的节点
				countNum=0;
			}else{
				for (int j = 0; j < dragSeqs.size(); j++) {
					String dragSeq = (String) dragSeqs.get(j);
					if("before".equalsIgnoreCase(position)&&Integer.parseInt(leftSeq)<=Integer.parseInt(dragSeq)){//计算当前节点后面有几个拖动的节点,往前拖动
						countNum++;
					}
					if("after".equalsIgnoreCase(position)&&Integer.parseInt(leftSeq)>Integer.parseInt(dragSeq)){//计算当前节点后面有几个拖动的节点,往前拖动
						countNum++;
					}
				}
			}
			res.add(countNum);
		}
    	return res;
    }
    /**
     * 更新环节序号
     * @param leftIds
     * @param dragIds
     * @param seqCount
     * @param position
     * @param xh
     */
    public void sortSeq(ArrayList leftIds,ArrayList dragIds,ArrayList updateSeqs,ArrayList dragUpSeqs,String position){
		try {
			
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "";
			ArrayList values = new ArrayList();
			sql = "update zp_flow_links set seq=? where id=?";
			/**
			 * 更新拖动节点的流程环节序号
			 */
			for (int i = 0; i < dragIds.size(); i++) {
				values.clear();
				values.add(dragUpSeqs.get(i));
				values.add(dragIds.get(i));
				dao.update(sql, values);
				values.clear();
			}
			/**
			 * 更新未拖动节点的流程环节序号
			 */
			if("before".equalsIgnoreCase(position))
				sql = "update zp_flow_links set seq=seq+? where id=?";
			else
				sql = "update zp_flow_links set seq=seq-? where id=?";
			for (int i = 0; i < leftIds.size(); i++) {
				values.clear();
				values.add(updateSeqs.get(i));
				values.add(leftIds.get(i));
				dao.update(sql, values);
				values.clear();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 更新环节操作序号
     * @param hjzt 
     * @param leftIds
     * @param dragIds
     * @param seqCount
     * @param position
     * @param xh
     */
    public void sortFuncSeq(String hjzt, Map<String,String> seqs){
    	try {
    		ContentDAO dao = new ContentDAO(this.conn);
    		String sql = "";
    		if("true".equals(hjzt)){
    			sql = "update zp_flow_status set seq=? where id=?";
    		}else{
    			sql = "update zp_flow_functions set seq=? where id=?";
    		}
    		for(Map.Entry<String,String> entry:seqs.entrySet()){    
    			ArrayList values = new ArrayList();
    		    String id =  entry.getKey();
    		    String seq =  entry.getValue(); 
    		    if(StringUtils.isNotEmpty(id)&&StringUtils.isNotEmpty(seq)){
	    		    values.add(seq);
	    			values.add(id);
	    			dao.update(sql, values);
    		    }
    		}   
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    /**
     * 移动流程环节
     * @param seq
     * @param linkid
     * @param flag
     * @param xh
     */
    public void moveSeq(String seq,String linkid,String flag,String othLinkId){
    	try {
    		/**
    		 * 更新移动节点的流程环节序号
    		 */
    		ContentDAO dao = new ContentDAO(this.conn);
    		String sql = "";
    		String sql1="update zp_flow_links set seq=? where id=?";;
    		ArrayList values = new ArrayList();
    		if("1".equals(flag)){
    			sql="update zp_flow_links set seq=seq-1 where id=?";
    		}
    		if("0".equals(flag)){
    			sql = "update zp_flow_links set seq=seq+1 where id=?";
    		}
    		values.add(linkid);
    		dao.update(sql, values);
    		values.clear();
    		values.add(seq);
    		values.add(othLinkId);
    		dao.update(sql1, values);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    /**
     * 判断是否有招聘过程数据
     * @param sql
     * @param values
     * @return
     */
    public String isUsedInProcess(String flowid){
    	String message="";
    	ArrayList values=new ArrayList();
    	String sql = "select Z0301 from z03 where Z0381=?";
    	values.add(flowid);
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if (dao.search(sql, values).next()) {// 该流程在招聘过程中有记录
				message= "该流程已经有招聘数据，不能删除!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
    /**
     * 判断当前环节是否有候选人
     * @param linkid
     * @return
     * @throws SQLException
     */
    public String isLinkUsed(String linkid) throws SQLException{
    	RowSet rs = null;
    	String res = "";
    	try{
	    	String sql="select * from zp_pos_tache a,zp_flow_links l where l.id=a.link_id and a.link_id='"+linkid+"'";
	    	ContentDAO dao = new ContentDAO(this.conn);
	        rs = dao.search(sql);
	        if(rs.next())
	        	res = "第"+rs.getString("seq")+"行"+rs.getString("custom_name")+"环节已有招聘数据，不能删除!";
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
			PubFunc.closeDbObj(rs);
		}
        return res;
    }
    /**
     * 删除流程环节
     * @param ids
     * @param values
     * @param flowid
     */
    public void delFlowLink(ArrayList ids){
    	ArrayList values=new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "delete from zp_flow_links where id=?";
    		String sql1 = "delete from zp_flow_status where link_id=?";
    		String sql2 = "delete from zp_flow_functions where link_id=?";
    		if(ids!=null){
    			for (int i = 0; i < ids.size(); i++) {
    				values.clear();
    				values.add(ids.get(i));
    				dao.delete(sql1, values);
    				dao.delete(sql2, values);
    				dao.delete(sql, values);
    			}
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 删除流程环节可用操作
     * @param id
     */
    public void delLinkFuncs(String id,String linkid,String seq){
    	ArrayList values=new ArrayList();
    	try {
    		ContentDAO dao = new ContentDAO(this.conn);
    		String sql = "delete from zp_flow_functions where id=?";
			values.add(id);
			dao.delete(sql, values);
			/*
			 * 更新序号
			 */
			sql = "update zp_flow_functions set seq=seq-1 where link_id=? and seq>?";
			values.clear();
			values.add(linkid);
			values.add(seq);
			dao.update(sql, values);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    /**
     * 更新序号
     */
    public void upSeq(String flowid){
    	ArrayList values=new ArrayList();
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
        	String sql="select id from zp_flow_links where flow_id=? order by seq asc";
        	values.clear();
        	values.add(flowid);
        	RowSet rs = dao.search(sql, values);
        	int seq = 1;
        	while(rs.next()){
        		sql = "update zp_flow_links set seq=? where id=?";
        		values.clear();
        		values.add(seq);
        		values.add(rs.getString("id"));
        		dao.update(sql, values);
        		seq++;
        	}
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    }
    /**
     * 删除流程
     * @param flowid
     */
    public void delFlow(String flowid){
    	ArrayList values=new ArrayList();
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		String cxsql = "select t2.id from zp_flow_definition t1,zp_flow_links t2 where t1.flow_id=t2.flow_id and t1.flow_id='"+flowid+"'";
    		RowSet rs = dao.search(cxsql);
    		String linkid="";
    		while(rs.next()){
    			values.clear();
    			linkid=rs.getString("id");
    			values.add(linkid);
    			String sql1 = "delete from zp_flow_functions where link_id=?";
    			String sql2 = "delete from zp_flow_status where link_id=?";
    			dao.delete(sql1, values);
    			dao.delete(sql2, values);
    			values.clear();
    		}
    		String sql3 = "delete from zp_flow_links where flow_id=?";
    		String sql4 = "delete from zp_flow_definition where flow_id=?";
    		values.add(flowid);
    		dao.delete(sql3, values);
    		dao.delete(sql4, values);
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    }
    /**
     * 更新流程定义的描述
     * @param description
     * @param flowid
     */
    public void upDescription(String newname,String description,String flowid,String b0110){
    	try{
    		 String sql = "update zp_flow_definition set description=?,name=?,b0110=? where flow_id=?";
             ArrayList values = new ArrayList();
             values.add(description);
             values.add(newname);
             values.add(b0110);
             values.add(flowid);
             ContentDAO dao = new ContentDAO(this.conn);
             dao.update(sql, values);
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    }
    /**
     * 判断流程名称是否已存在
     * @param description
     * @param flowid
     */
    public boolean isNameUsed(String name){
    	RowSet rs = null;
    	try{
    		ArrayList<String> value = new ArrayList<String>();
    		value.add(name);
    		String sql = "select name from zp_flow_definition where name=?";
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(sql,value);
    		if(rs.next())
    			return true;
    	}catch (Exception e) {
    		e.printStackTrace();
    	}finally {
    		PubFunc.closeResource(rs);
    	}
    	return false;
    }
    
    /**
     * 保存招聘环节必须顺序进行参数
     * @param skipflag
     * @param flow_id
     */
    public void saveSkipFlag(String skipflag,String flow_id){
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		String sql = "update zp_flow_definition set seq_flag=? where flow_id=?";
    		ArrayList values = new ArrayList();
            values.add(skipflag);
            values.add(flow_id);
    		dao.update(sql,values);
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
