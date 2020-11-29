package com.hjsj.hrms.module.jobtitle.reviewfile.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>Title: OutProficientBo </p>
 * <p>Description: 单独生成专家账号和密码的修改与删除 </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-9-24 下午2:01:17</p>
 * @author liuyang
 * @version 1.0
 */
public class OutProficientBo {
    // 基本属性
    private Connection conn = null;

    /**
     * 构造函数
     * 
     * @param conn
     */
    public OutProficientBo(Connection conn) {
        this.conn = conn;
    }

    /**
     * 
     * @Title:querylist
     * @Description：
     * @author chent
     * @param meetingId
     * 会议id
     * @param reviewPersonId
     * 申请项目id
     * @param type
     * 类型 1_内部评审，2_内部专家，3_外部专家
     * @return 列表数据
     * @throws GeneralException
     */
    public List querylist(String meetingId, String reviewPersonId) throws GeneralException {
        String sqlstr = "";
        ArrayList list = new ArrayList();
        try {
            ContentDAO dao = new ContentDAO(conn);
            sqlstr = "select username,password,state,description,w01.w0103,w01.w0105,w01.w0107 ";
            sqlstr = sqlstr + " from zc_expert_user ";
            sqlstr = sqlstr + " left join w01 on zc_expert_user.w0101=w01.w0101 ";
            sqlstr = sqlstr + " where ";
            sqlstr = sqlstr + " W0301= ? ";
            sqlstr = sqlstr + " and W0501= ? ";
            sqlstr = sqlstr + " and type=3 ";//外部专家
            ArrayList values = new  ArrayList();
            values.add(PubFunc.decrypt(SafeCode.decode(meetingId)));
            values.add(PubFunc.decrypt(SafeCode.decode(reviewPersonId)));
            RowSet rs = dao.search(sqlstr,values);
            while (rs.next()) {
                HashMap map = new HashMap();
                map.put("meetingId", meetingId);
                map.put("reviewPersonId", reviewPersonId);
                map.put("content", rs.getString("username"));
                map.put("pasword", rs.getString("password"));
                if (rs.getInt("state") == 1) {
                    map.put("statment", ResourceFactory.getProperty("column.sys.valid"));
                } else {
                    map.put("statment", ResourceFactory.getProperty("column.sys.invalid"));
                }
                
                map.put("desc", rs.getString("description"));
                map.put("name", rs.getString("w0107"));
                map.put("unit", rs.getString("w0103"));
                map.put("department", rs.getString("w0105"));
                list.add(map);
            }
            PubFunc.closeDbObj(rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }

    /**
     * 
     * @Title:修改
     * @Description：
     * @author liuyang
     * @param meetingId
     * 会议id
     * @param reviewPersonId
     * 申请项目id
     * @param content
     * 修改内容
     * @param pasword
     * 修改密码
     * @param desc
     * 内容描述
     * @param type
     * 类型 1_内部评审，2_内部专家，3_外部专家
     * @param statment
     * 状态 1 启用 0禁用
     * @return 返回是否修改成功
     * @throws GeneralException
     */
    public String update(ArrayList dataList,ArrayList dataUpdataList,String type) throws GeneralException {

        ContentDAO dao = new ContentDAO(this.conn);
        int statment = 0;
        
        
        MorphDynaBean aBean =new  MorphDynaBean();
        MorphDynaBean bBean =new  MorphDynaBean();
        bBean = (MorphDynaBean) dataList.get(0);
        String meetingid = PubFunc.decrypt(SafeCode.decode((String) bBean.get("meetingid")));
        String userid = PubFunc.decrypt(SafeCode.decode((String) bBean.get("userid")));
        String resultTip = "";
        StringBuffer updateAllStr = new StringBuffer("update zc_expert_user set password = ? ,type = ?,description = ? ");
        updateAllStr.append(" where  username = ?");
        StringBuffer updateStateStr = new StringBuffer("update zc_expert_user set state = ? ");
        updateStateStr.append(" where  username = ? and w0301= ? and w0501 = ?");
        try {
            for (int i = 0; i < dataUpdataList.size(); i++) {
                ArrayList valueList = new ArrayList();
                ArrayList valueStat = new ArrayList();
                aBean = (MorphDynaBean) dataUpdataList.get(i);
                valueList.add((String) aBean.get("pasword"));
                valueList.add(Integer.parseInt(type));
                valueList.add((String)  aBean.get("desc"));
                valueList.add((String)  aBean.get("content"));
                dao.update(updateAllStr.toString(),valueList);
                
                valueList.clear();
                if("启用".equals((String)aBean.get("statment")) || "1".equals((String)aBean.get("statment")))
                    statment = 1;
                else 
                    statment = 0;
                valueList.add(statment);
                valueList.add((String) aBean.get("content"));
                valueList.add(meetingid);
                valueList.add(userid);
                
            
                dao.update(updateStateStr.toString(),valueList);
            }
            resultTip = "ok";
        } catch (SQLException e) {
            resultTip = "no";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return resultTip;
    }

    /**
     * 
     * @Title:删除相应账号信息
     * @Description：
     * @author liuyang
     * @param meetingId
     * 会议id
     * @param reviewPersonId
     * 申请项目id
     * @param contentList
     * 账号
     * @param type
     * 评审类型
     * @return
     * @throws GeneralException
     */
    public String dele(String meetingId, String reviewPersonId, String contentList, String type)
            throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList values = new ArrayList();
        StringBuffer delestr = new StringBuffer("");
        String resultTip = "";
        try {
            contentList = contentList.substring(0, contentList.lastIndexOf(","));
            values.add(meetingId);
            values.add(reviewPersonId);
            values.add(Integer.parseInt(type));
            delestr.append(" delete from ");
            delestr.append(" zc_expert_user ");
            delestr.append(" where ");
            delestr.append(" W0301=? and W0501=? and username in (" + contentList + ") and type=? ");
            int a = dao.delete(delestr.toString(), values);
            resultTip = "ok";
            String sql = "and w0301 = '"+meetingId+"'";
            sql = sql +" and w0501 = '"+reviewPersonId+"'";
            GenerateAcPwBo.saveW05(sql,Integer.parseInt(type),dao);
        } catch (SQLException e) {
            resultTip = "no";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return resultTip;
    }

    /**
     * 
     * @Title:判断需要添加的账号是否存在
     * @Description：
     * @author liuyang
     * @param meetingId
     * 会议id
     * @param reviewPersonId
     * 申请项目id
     * @param content
     * 账号
     * @return 是否
     * @throws GeneralException
     */
    public boolean isExist(ArrayList dataList, String content) throws GeneralException {
        String sqlstr = "";
        ArrayList list = new ArrayList();
        boolean result = false;
        MorphDynaBean abean = new MorphDynaBean();
        abean=(MorphDynaBean)dataList.get(0);
        try {
            ContentDAO dao = new ContentDAO(conn);
            sqlstr = "select username ";
            sqlstr = sqlstr + " from zc_expert_user ";
            sqlstr = sqlstr + " where 1=1 ";
            sqlstr = sqlstr + " and W0301= ? ";
            sqlstr = sqlstr + " and W0501= ? ";
            sqlstr = sqlstr + " and username= ? ";
            list.add( PubFunc.decrypt(SafeCode.decode((String)abean.get("meetingid"))));
            list.add( PubFunc.decrypt(SafeCode.decode((String)abean.get("userid"))));
            list.add( content );
            RowSet rs = dao.search(sqlstr,list);
            result = rs.next();
            PubFunc.closeDbObj(rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return result;
    }
    /**
     * 引入专家、并随机创建用户名和密码
     * @param w0501：申报编号
     * @param personidList：引入的专家列表
     * @return
     */
    public String createPerson(String w0301, String w0501, ArrayList<String> personidList) throws GeneralException {
    	String msg = "";
    	ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rs1 = null;
		try {
	    	for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				StringBuilder sql = new StringBuilder();
				ArrayList<String> list = new ArrayList<String>();
				// 需要新增的专家是否已经存在
				sql.append("select count(user_id) as count ");
				sql.append("From zc_expert_user ");
				sql.append("where W0501=? ");//申报编号
				sql.append("and w0101=? ");//专家编号
				sql.append("and type=3 ");//外部专家
				sql.append("and "+Sql_switcher.isnull("usetype", "1")+"=1");//评审帐号
				list.add(w0501);
				list.add(expertid);
				
				rs = dao.search(sql.toString(), list);
				while(rs.next()){
					if(rs.getInt("count") > 0){//存在该专家
						continue;//跳过
					}else {
						String sql1 = "select username,password from zc_expert_user where "+Sql_switcher.isnull("usetype", "1")+"=1 and w0101=? and type=3 and w0301=(select w0301 from w05 where w0501=?)";
						ArrayList<String> list1 = new ArrayList<String>();
						list1.add(expertid);
						list1.add(w0501);
						rs1 = dao.search(sql1.toString(), list1);
						String username = "";
						String password = ""; 
						if(rs1.next()){
							username = rs1.getString("username");
							password = rs1.getString("password");
						}else {
							GenerateAcPwBo gbo = new GenerateAcPwBo(this.conn);//生成账号密码
							ArrayList userList = GenerateAcPwBo.generate(1, dao);
							HashMap map = (HashMap)userList.get(0);
							username = (String)map.get("content");
							password = (String)map.get("pasword");
						}
						
						RecordVo vo = new RecordVo("zc_expert_user");
						//账号表序号、学科组编号、会议ID 、申报人主键序号ID 、帐号、密码、帐号状态、帐号类型、专家编号、描述信息、角色
						IDFactoryBean idf = new IDFactoryBean();
						vo.setString("user_id", idf.getId("zc_expert_user.user_id", "", conn));
						vo.setString("group_id", null);
						vo.setString("w0301", w0301);
						vo.setString("w0501", w0501);
						vo.setString("username", username);
						vo.setString("password", password);
						vo.setInt("state", 0);
						vo.setInt("type", 3);
						vo.setString("w0101", expertid);
						vo.setInt("usetype", 2);
						
						dao.addValueObject(vo);
					}
				}
			}
	    	// 同步外部专家人数
			if(StringUtils.isNotBlank(w0301)){
				ReviewFileBo reviewFileBo = new ReviewFileBo(this.conn);// 工具类
				reviewFileBo.asyncPersonNum(w0301,3);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs1);
		}
    	return msg;
    }
    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

}
