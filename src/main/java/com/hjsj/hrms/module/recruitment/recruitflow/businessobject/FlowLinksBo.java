package com.hjsj.hrms.module.recruitment.recruitflow.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:RecruitflowBo.java</p>
 * <p>Description>:招聘流程业务类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-1-22</p>
 * <p>@author:dengcan</p>
 * <p>@version: 7.x</p>
 */
public class FlowLinksBo {
	private Connection conn=null;
    private UserView userview;
    RecruitflowBo rfb = null;
    
    public FlowLinksBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    	 rfb = new RecruitflowBo(this.conn, this.userview);
    }
    /**
     * 更新插入节点之后的流程环节序号
     */
    public void upAfterSeq(String[] nodeids,String flowid,int xh){
    	try{
    		int res=nodeids.length;//插入节点的数量
        	ContentDAO dao = new ContentDAO(this.conn);
           
        	String sql = "update zp_flow_links set seq=seq+? where flow_id=? and seq>=?";
            ArrayList values = new ArrayList();
            values.add(res);
            values.add(flowid);
            values.add(xh);
            dao.update(sql,values);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 向zp_flow_links插入数据(插入的流程环节)
     * @param nodeids
     * @param nodeNames
     * @param flowid
     * @param xh
     */
    public void insertLinks(AddRecruitFlowBo addRecruitFlowBo,String[] nodeids,String[] nodeNames,String flowid,int xh){
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		IDGenerator idg = new IDGenerator(2, this.conn);
    		for (int i = 0; i < nodeids.length; i++) {
                String id=idg.getId("zp_flow_links.id");//参数从系统管理-应用管理-参数设置-序号维护中获取
        		RecordVo recordVo = new RecordVo("zp_flow_links");
        		recordVo.setString("id", id);
        		recordVo.setString("flow_id", flowid);
        		recordVo.setString("node_id", nodeids[i]);
        		recordVo.setInt("valid", 1);
        		recordVo.setString("custom_name", nodeNames[i]);
        		recordVo.setInt("seq", xh);
        		dao.addValueObject(recordVo);
        		addRecruitFlowBo.saveStatus(dao, id, nodeids[i], idg);
        		addRecruitFlowBo.saveFuncs(dao, id, nodeids[i], idg);
        		xh++;
        	}
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    }
    /**
     * 更新环节功能操作或状态信息
     * @param ids
     * @param seqs
     * @param custom_names
     * @param valids
     */
    public void updateLinkFucs(String tableName,ArrayList ids,ArrayList seqs,ArrayList custom_names,ArrayList valids,ArrayList resume_modifys,ArrayList methodNames,String linkid){
    	try{
    		if(ids==null||seqs==null||custom_names==null||valids==null)
    			return;
    		
    		String sql = "";
            ArrayList values = new ArrayList();
            ContentDAO dao = new ContentDAO(this.conn);
            IDGenerator idg = new IDGenerator(2, this.conn);
        	for (int i = 0; i < ids.size(); i++) {
        		/**
        		 * 更新已有记录的变更信息
        		 */
        		if(StringUtils.isNotEmpty((String) ids.get(i))){
        			if("zp_flow_status".equals(tableName)){
        				sql="update "+tableName+" set seq=?,custom_name=?,valid=?,resume_modify=? where id=?";
        				values.add(seqs.get(i));
        				values.add(custom_names.get(i));
        				values.add(valids.get(i));
        				values.add(resume_modifys.get(i));
        				values.add(ids.get(i));
        			}
        			else{
        				sql="update "+tableName+" set seq=?,custom_name=?,function_str=?,valid=? where id=?";
        				values.add(seqs.get(i));
        				values.add(custom_names.get(i));
        				values.add(methodNames.get(i));
        				values.add(valids.get(i));
        				values.add(ids.get(i));
        			}
        			dao.update(sql, values);
        			values.clear();
        		}else{ //保存新增记录
        			sql="insert into zp_flow_functions values(?,?,?,?,?,?,?,?,?)";
        			String id = idg.getId("zp_flow_functions.id");
        			values.add(id);
        			values.add(linkid);
        			values.add(5);
        			values.add(methodNames.get(i));
        			values.add(valids.get(i));
        			values.add(custom_names.get(i));
        			values.add(seqs.get(i));
        			values.add(0);
        			values.add("自定义按钮");
        			dao.insert(sql, values);
        			values.clear();
        		}
			}
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    /**
     * 更新流程环节信息
     * @param ids
     * @param seqs
     * @param custom_names
     * @param remarks
     * @param valids
     * @param org_flags 
     * @param flowid
     */
    public void updateLinks(ArrayList ids,ArrayList seqs,ArrayList custom_names,ArrayList remarks,ArrayList org_flags, ArrayList valids,String flowid){
    	try{
    		String sql = "update zp_flow_links set seq=?,custom_name=?,remark=?,valid=?,org_flag=? where id=? and flow_id=?";
            ArrayList values = new ArrayList();
            ContentDAO dao = new ContentDAO(this.conn);
            if(ids!=null&&seqs!=null&&custom_names!=null&&remarks!=null&&valids!=null){
            	for (int i = 0; i < ids.size(); i++) {
					values.add(seqs.get(i));
					
					//zxj 20160315 custom_name(客户自定义名称）字段长度100
					String customName = PubFunc.keyWord_filter((String) custom_names.get(i));
					customName = customName == null ? "" : customName;
					customName = PubFunc.doStringLength(customName, 100);
					values.add(customName);
					
					//zxj 20160314 remark字段长度200
					String remarkValue = PubFunc.reverseHtml((String) remarks.get(i));
					remarkValue = PubFunc.doStringLength(remarkValue, 200);
					values.add(remarkValue);
					
					values.add(valids.get(i));
					values.add(org_flags.get(i));
					values.add(ids.get(i));
					values.add(flowid);
					dao.update(sql, values);
					values.clear();
				}
            }
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    /**
     * 获取流程节点状态显示信息
     * @param nodeid
     * @param linkid
     * @param isParent 是否为上级流程节点
     * @return
     */
    public StringBuffer getLinkTableData(String nodeid,String linkid,boolean isParent){
    	StringBuffer jsonInfo = new StringBuffer("[");
    	RowSet rs = null;
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select codeitemdesc sysName,custom_name,status,id,seq,valid,resume_modify from zp_flow_status " +
			"left join codeitem on status=codeitemid and codesetid=? where status like ? and link_id=? order by seq asc";
			ArrayList values = new ArrayList();
			values.add("36");
			values.add(nodeid+"%");
			values.add(linkid);
			rs = dao.search(sql, values);
			while (rs.next()) {
				String status =  rs.getString("status");
				String statusList = "'0105','0106','0205','0206','0306','0307','0308','0406','0407','0408','0506',"
						+ "'0507','0508','0603','0604','0703','0704','0805','0806','1003','1004','1005'";
				String sysName = rs.getString("sysName");
				String custom_name = StringUtils.isEmpty(rs.getString("custom_name")) ? "" : rs.getString("custom_name");
				String id = rs.getString("id");
				String seq = rs.getString("seq");
				String valid = rs.getString("valid");
				String resume_modify = rs.getString("resume_modify");
				String input = "<input type=\"checkbox\" onclick=\"change(this,1)\" id=\""+seq+"\"" ;
                if("1".equalsIgnoreCase(valid)){
                	input += " value=\"1\"";
                	input += " checked=\"checked\"";
                }else{
                	input += " value=\"0\"";
                }
                String input1 = "<input type=\"checkbox\" onclick=\"change1(this,1)\" id=\""+seq+"seq"+"\"" ;
                if ("1".equalsIgnoreCase(resume_modify) || 
                		(statusList.contains(status) && resume_modify == null ) ){
                	input1 += " value=\"1\"";
                	input1 += " checked=\"checked\"";
                }else{
                	input1 += " value=\"0\"";
                }
                //上级流程节点不能修改
                if(isParent){
                	input += " disabled=\"disabled\" ";
                	input1 += " disabled=\"disabled\" ";
                }
                input+="/><input type=\"hidden\" value=\""+id+"\"/>";
                input1+="/><input type=\"hidden\" value=\""+id+"\"/>";
                jsonInfo.append("{seq:'" + seq + "',sysName:'" + sysName + "',custom_name:'" + custom_name + "',resume_modify:'"+input1+"',valid:'" + input +"'},");
			}
			if(jsonInfo.length()>0)
            	jsonInfo.setLength(jsonInfo.length() - 1);
			if(jsonInfo.length()>1)
				jsonInfo.append("]");
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	return jsonInfo;
    }
    /**
     * 获取流程id
     * @param linkid
     * @return
     */
    public String getFlowId(String linkid){
    	
    	String sql = "select * from zp_flow_links where id='"+linkid+"'";
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql);
    		if(rs.next())
    			return rs.getString("flow_id");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return "";
    }
    /**
     * 获取流程节点功能操作显示信息
     * @param nodeid
     * @param linkid
     * @return
     */
    public StringBuffer getLinkTableFuns(String linkid,String link){
    	StringBuffer jsonInfo = new StringBuffer("[");
    	RowSet rs = null;
    	try{
    		//是否上级流程
    		boolean isParent = rfb.isParentFlow(this.getFlowId(linkid), "");
    		
    		ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select id,valid,sys_name sysName,custom_name,seq,ownflag,function_str from zp_flow_functions  where link_id='"+linkid+"' order by seq asc";
			rs = dao.search(sql);
			while (rs.next()) {	
				String sysName = rs.getString("sysName");
				if(StringUtils.isEmpty(sysName))
					sysName="";
				String custom_name = StringUtils.isEmpty(rs.getString("custom_name")) ? "" : rs.getString("custom_name");
				String id = rs.getString("id");
				String seq = rs.getString("seq");
				String valid = rs.getString("valid");
				String ownflag = rs.getString("ownflag");
				String methodName = StringUtils.isEmpty(rs.getString("function_str")) ? "" : rs.getString("function_str");
				String input = "<input type=\"checkbox\" onclick=\"change(this,0)\" id=\""+seq+"fuc\"" ;
                if("1".equalsIgnoreCase(valid)){
                	input += " value=\"1\"";
                	input += " checked=\"checked\"";
                }else{
                	input += " value=\"0\"";
                }
                if(isParent)
                	input += " disabled=\"disabled\" ";
                input+="/><input type=\"hidden\" value=\""+id+"\"/>";
                jsonInfo.append("{seq:'" + seq + "',sysName:'" + sysName +"',custom_name:'" + custom_name + "',methodName:'"+methodName+ "',ownflag:" + ownflag + ",valid:'" + input +"'}"+link+"");
			}
			if(jsonInfo.length()>1)
            	jsonInfo.setLength(jsonInfo.length() - 1);
            jsonInfo.append("]");
    	}catch (Exception e) {
    		e.printStackTrace();
    	}finally{
			PubFunc.closeDbObj(rs);
		}
    	return jsonInfo;
    }
    /**
     * 流程环节信息
     * @param flowid
     * @return
     */
    public StringBuffer getLinkInfos(String flowid,String link){
    	StringBuffer jsonInfo = new StringBuffer("[");
    	RowSet rs = null;
    	try{
        	boolean isParent = rfb.isParentFlow(flowid, "");
        	
    		StringBuffer sqlstr = new StringBuffer();
            sqlstr.append("select id,seq,codeitemdesc as sysName,custom_name,remark,org_flag,valid,node_id");
            sqlstr.append(" from zp_flow_links join codeitem on zp_flow_links.node_id=codeitem.codeitemid");
            sqlstr.append(" where codeitem.codesetid='36' and flow_id='" + flowid + "' order by seq asc");

            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sqlstr.toString());
            while (rs.next()) {
                String seq = rs.getString("seq");
                String id = rs.getString("id");
                String sysName = rs.getString("sysName");
                String custom_name = StringUtils.isEmpty(rs.getString("custom_name")) ? "" : rs.getString("custom_name").replace("\'", " ");
                //json中不能有单引号等特殊字符
                String remark = rs.getString("remark") == null ? "" : rs.getString("remark").replaceAll("\'", " ");
                String org_flag = rs.getString("org_flag") == null ? "" : rs.getString("org_flag").replaceAll("\'", " ");
                String org_flag_check = "<input type=\"checkbox\" onclick=\"changeOrg_flag(this)\" id=\"flag"+seq+"\"";
                if("1".equals(org_flag)){
                	org_flag_check += " value=\"1\" checked=\"checked\"";
                }else{
                	org_flag_check += " value=\"0\"";
                }
                org_flag_check+="/><input type=\"hidden\" value=\""+id+"\"/>";
                remark = StringUtils.isEmpty(remark) ? "":PubFunc.toHtml(remark);
                String valid = rs.getString("valid");
                String nodeid = rs.getString("node_id");
                String input = "<input type=\"checkbox\" onclick=\"change(this)\" id=\""+seq+"\"" ;
                if("1".equalsIgnoreCase(valid)){
                	input += " value=\"1\"";
                	if(this.isLinkUsed(id) || isParent)//当前环节有候选人
	                	input += " checked=\"checked\" disabled=\"disabled\" title=\"存在职位候选人\"";
                	else if("10".equalsIgnoreCase(nodeid))//入职环节必须有
                		input += " checked=\"checked\" disabled=\"disabled\" title=\"入职环节必须启用\"";
                	else
                		input += " checked=\"checked\"";
                }else{
                	input += " value=\"0\"";
                }
                input+="/><input type=\"hidden\" value=\""+id+"\"/>";
                jsonInfo.append("{seq:'" + seq + "',sysName:'" + sysName + "',custom_name:'" +custom_name + "',remark:'"+remark+"',org_flag:'" + org_flag_check +"',valid:'" + input +"',nodeid:'"+nodeid+"'}"+link+"");
            }
            if(!"[".equals(jsonInfo)){//排除不存在流程的情况
            	if(jsonInfo.length()>1)
            		jsonInfo.setLength(jsonInfo.length() - 1);
            	jsonInfo.append("]");
            }
    	}catch (Exception e) {
    		e.printStackTrace();
    	}finally{
			PubFunc.closeDbObj(rs);
		}
    	return jsonInfo;
    }
    /**
     * 判断当前环节是否有候选人
     * @param linkid
     * @return
     * @throws SQLException
     */
    public boolean isLinkUsed(String linkid) throws SQLException{
    	RowSet rs = null;
    	boolean res = false;
    	try{
	    	String sql="select A0100 from zp_pos_tache where link_id='"+linkid+"'";
	    	ContentDAO dao = new ContentDAO(this.conn);
	        rs = dao.search(sql);
	        if(rs.next())
	        	res = true;
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
			PubFunc.closeDbObj(rs);
		}
        return res;
    }
}
