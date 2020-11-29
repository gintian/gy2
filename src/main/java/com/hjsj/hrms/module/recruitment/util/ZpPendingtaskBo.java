package com.hjsj.hrms.module.recruitment.util;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * 招聘需求模块：更新查询待办任务表
 * 
 */
public class ZpPendingtaskBo {
    private Connection conn = null;
    private UserView userview = null;

    public ZpPendingtaskBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }

    /**
     * @param username 接收者用户名
     * @param title 待办标题
     */
    public void updatePendingTask(String username, String title) {

        try {
        		HashMap<String, String> params = new HashMap<String, String>();
        		params.put("title", title+"（职位申报）");
        		params.put("url", "/recruitment/position/position.do?b_query=link&amp;pageNum=1&amp;flag=1&amp;pagesize=20&amp;positionType=2");
        		sendPendingTask(username, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	/**
	 * 发送待办
	 * @param receiver 接收者用户名
	 * @param params title待办标题 url待办链接 ext_flag 扩展信息ZP_职位编号_流程环节编号
	 * @throws GeneralException
	 */
	public void sendPendingTask(String receiver, HashMap<String, String> params) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		IDGenerator idg = new IDGenerator(2, this.conn);
		String pending_id = idg.getId("pengdingTask.pengding_id");
		RecordVo vo = getRecordVo(pending_id, receiver, params);
		dao.addValueObject(vo);
	}
	
	/**
	 * 更新待办
	 * @param receiver 接收者用户名
	 * @param params title待办标题 url待办链接 ext_flag 扩展信息ZP_职位编号_流程环节编号
	 * @throws GeneralException
	 * @throws SQLException 
	 */
	public void updatePendingTask(String receiver, HashMap<String, String> params) throws GeneralException, SQLException {
		ContentDAO dao = new ContentDAO(this.conn);
		String pending_id = params.get("pending_id");
		RecordVo vo = getRecordVo(pending_id, receiver, params);
		dao.updateValueObject(vo);
	}

	/**
	 * @param pending_id 待办表主键
	 * @param receiver 接收者账号
	 * @param params title待办标题 url待办链接 ext_flag 扩展信息ZP_职位编号_流程环节编号
	 * @return
	 * @throws GeneralException
	 */
	private RecordVo getRecordVo(String pending_id, String receiver, HashMap<String, String> params) throws GeneralException {
		Date date = new Date();
		Timestamp create_time = new Timestamp(date.getTime());
		
		String sender = this.userview.getUserName();
		// 在待办任务表中新增待办数据
		RecordVo vo = new RecordVo("t_hr_pendingtask");
		vo.setString("pending_id", pending_id);
		vo.setDate("create_time", create_time);
		vo.setDate("lasttime", create_time);
		vo.setString("sender", sender);
		vo.setString("pending_type", "32");
		vo.setString("pending_title", params.get("title"));
		vo.setString("pending_url", params.get("url"));
		vo.setString("pending_status", "0");
		vo.setString("pending_level", "1");
		vo.setString("receiver", receiver);
		vo.setString("ext_flag", params.get("ext_flag"));
		return vo;
	}
    
    /**
     * 我的任务显示待办任务
     * 
     * @return
     */
    public ArrayList getZpapprDta() {
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            String receiver = this.userview.getUserName();
            String username = geta01Username(receiver);

            StringBuffer sql = new StringBuffer();
            sql.append("select pending_id,Pending_title,Pending_url,Pending_status,"+Sql_switcher.dateToChar("create_time", Sql_switcher.searchDbServer()==Constant.ORACEL?"yyyy-MM-dd HH24:mi":"yyyy-MM-dd HH:mm")+" create_time");
            sql.append(" from t_hr_pendingtask");
            sql.append(" where Pending_type='32'");
            sql.append(" and (pending_status='0' or pending_status='3')");

            if (username != null && username.length() > 0)
                sql.append(" and (Receiver='" + receiver + "' or Receiver='" + username + "')");
            else
                sql.append(" and Receiver='" + receiver + "'");

            rs = dao.search(sql.toString());
            while (rs.next()) {
                CommonData cData = new CommonData();
                String Pending_status = "";
                if ("0".equalsIgnoreCase(rs.getString("Pending_status")))
                    Pending_status = "待办";
                else if ("3".equalsIgnoreCase(rs.getString("Pending_status")))
                    Pending_status = "已阅";
                cData.setDataName(rs.getString("Pending_title") + "(" + Pending_status + ")");
                cData.setDataValue(rs.getString("Pending_url") + "&amp;pendingId=" + rs.getString("pending_id") + "&amp;sign=2" );
                String parse_date=rs.getString("create_time");
                cData.put("date", parse_date);
                list.add(cData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeIoResource(rs);
        }
        return list;
    }
    
    /**
     * 检查业务用户关联的人员的登录帐号
     * 
     * @param receiver
     * @return
     */
    private String geta01Username(String receiver) {
        String username = "";
        String a0100 = "";
        String nbase = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search("select a0100,nbase from operuser where username='" + receiver + "'");
            if (rs.next()) {
                a0100 = rs.getString("a0100");
                nbase = rs.getString("nbase");
            }

            if (a0100 != null && a0100.length() > 0 && nbase != null && nbase.length() > 0) {
                AttestationUtils utils = new AttestationUtils();
                LazyDynaBean abean = utils.getUserNamePassField();
                String username_field = (String) abean.get("name");
                StringBuffer sql = new StringBuffer();
                sql.append("select " + username_field + " username");
                sql.append(" from " + nbase + "A01");
                sql.append(" where a0100='" + a0100 + "'");
                rs = dao.search(sql.toString());
                if (rs.next())
                    username = rs.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(rs);
        }

        return username;

    }
    
    
    /**
     * 获取用户名
     * @param value
     * @return
     */
    public String getUserName(String value)
	{
    	String userName = ""; 
		if(StringUtils.isEmpty(value))
			return userName;
		RowSet search = null;
	    try {
	    	ContentDAO dao = new ContentDAO(this.conn);
	    	String nbase=value.substring(0,3);
	    	String a0100=value.substring(3);
	    	DbNameBo bo = new DbNameBo(conn);
	    	String username_field = bo.getLogonUserNameField();
	    	StringBuffer sql=new StringBuffer();
	    	sql.append("select "+username_field+" username ");
	    	sql.append(" from "+nbase+"A01 where a0100='"+a0100+"'");
			search = dao.search(sql.toString());
			if(search.next())
				userName = search.getString("username");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    return userName;
	}
	
	/**
	 * 获取待办标题
	 * @param batchIds 批次编号
	 * @return
	 */
	public ArrayList<String> getTitles(ArrayList<String> batchIds) {
		RowSet rs = null;
		ArrayList<String> titles = new ArrayList<String>();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("select z0101, z0103, z0151 from z01 where z0101 in(");
			int n = 0;
			do {
				n++;
				sql.append("?,");
			}while(n<batchIds.size());
			sql.setLength(sql.length()-1);
			sql.append(")");
			rs = dao.search(sql.toString(), batchIds);
			while(rs.next()) {
				titles.add(rs.getString("z0103"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return titles;
	}
	
	/**
	 * 发送待办
	 * @param persons接收人 nbase+a0100
	 * @param titles 待办标题
	 * @param isSelfUser 是否自助用户
	 */
	public void sendPendingTask(ArrayList<String> persons, ArrayList<String> titles, boolean isSelfUser) {
		try {
			for(String memberId : persons) {
				memberId = PubFunc.decrypt(memberId);
				String userName = "";
				if(isSelfUser)
					userName = getUserName(memberId);
				else
					userName = memberId;
    			if(StringUtils.isNotEmpty(userName)) {
    				for(String title:titles)
    					this.updatePendingTask(userName, title);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 招聘批次发送待办邮件
	 * @param persons 接收人员
	 * @param titles 邮件标题
	 * @param templateId 模板id
	 * @param sub_module 招聘模块号7
	 * @param nModule
	 * @param content
	 */
	public void sendEmailTask(ArrayList<String> persons, ArrayList<String> titles, String templateId, String sub_module, String nModule, String content, boolean isSelfUser) {
		RowSet search = null; 
		try {
			EmailInfoBo emailBo = new EmailInfoBo(this.conn, this.userview);
			ArrayList<LazyDynaBean> beans = new ArrayList<LazyDynaBean>();
			LazyDynaBean bean = emailBo.getTemplateInfo(nModule, sub_module, templateId,"");
			String emailItemId = emailBo.getEmailItemId();//获取邮箱指标
			ArrayList fileList = emailBo.getAttachFileName(templateId);
        	String returnAddress = (String)bean.get("return_address")== null ?"":(String)bean.get("return_address");//邮件回复地址
			
        	if(isSelfUser) {
        		for(String memberId : persons) {//获取自助用户邮件对象
    				memberId = PubFunc.decrypt(memberId);
        			HashMap<String, String> hm = new HashMap<String, String>();
        			hm.put("email", emailItemId);
    			    hm.put("nbase", memberId.substring(0, 3));
    			    hm.put("a0100", memberId.substring(3));
    			    for(String title:titles)
    			    	emailBo.getEmailBean(beans, templateId, emailBo.getEmailAddress(hm), title, content,fileList,returnAddress);
        		}
        	}else {
        		for(String memberId : persons) {//获取业务用户邮件对象
    				memberId = PubFunc.decrypt(memberId);
    			    for(String title:titles)
    			    	emailBo.getEmailBean(beans, templateId, getBusinessEmail(memberId), title, content,fileList,returnAddress);
        		}
        	}
        	
	    	emailBo.bulkSendEmail(beans);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(search);
		}
	}
	
	/**
	 * 根据扩展标识将处理过的待办取消
	 * @param z0301 职位id	
	 * @param linkId 环节id
	 * @return
	 */
	public void cancelPendingTask(String z0301, String linkId) {
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			ArrayList<String> values = new ArrayList<String>();
			//查询某环节第一个状态下是否有简历
			sql.append("select COUNT(1) from ZP_POS_TACHE ");
			sql.append(" where ZP_POS_ID=? and LINK_ID=? ");
			sql.append(" and RESUME_FLAG=(select STATUS from ZP_FLOW_STATUS a ");
			sql.append(" where LINK_ID=? and VALID=1 ");
			sql.append(" and SEQ = ( select min(SEQ) from ZP_FLOW_STATUS where LINK_ID=? and VALID=1))");
			values.add(z0301);
			values.add(linkId);
			values.add(linkId);
			values.add(linkId);
			rs = dao.search(sql.toString(),values);
			if(rs.next()) {
				if(rs.getInt(1)==0) {
					sql.setLength(0);
					values.clear();
					sql.append("update t_hr_pendingtask set pending_status='1' where ext_flag=?");
					values.add("ZP_"+z0301+"_"+linkId);
					dao.update(sql.toString(), values);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}
		
	/**
	 * 根据业务用户名获取用户邮件地址
	 * @param z0301 职位id	
	 * @param linkId 环节id
	 * @return
	 */
	private String getBusinessEmail(String usrName){
		RowSet rs = null;
		String emailaddress ="";
		try {
			StringBuffer sql = new StringBuffer(" select email from OperUser  where username = ?");
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString(),Arrays.asList(usrName));
			if(rs.next())
			{
				emailaddress = rs.getString("email");
			}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    PubFunc.closeResource(rs);
			}
			return emailaddress;
		}
	
}
