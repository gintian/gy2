package com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 考生资格校验类
 * @author zhaoxj
 *
 */
public class RecruitExamineeChecker implements IRecruitCheck{
    
    public RecruitExamineeChecker() {
	
    }
    
    /**
     * 资格校验
     * 人大规则：申请正式职位需要首先通过“综合测试”岗考试
     * @param nbase
     * @param a0100
     * @return
     */
    @Override
    public String check(String nbase, String a0100, String posId) {
	String msg = "";
	
	//人大：综合测试岗 招聘流程编号:招聘职位编号
	String zpPrePos = SystemConfig.getPropertyValue("zp_pre_pos");
	//没有定义前置岗位参数 返回合格
	if(StringUtils.isEmpty(zpPrePos)) {
	    return msg;
	}
	
	String[] params = zpPrePos.split(":");
	//参数格式定义错误 返回合格
	if (params == null || params.length != 2) {
	    return msg;
	}
	
	//参数定义不完整 返回合格
	if(StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
	    return msg;
	}
	
	String flowId = params[0].trim();
	String prePosId = params[1].trim();
	
	//如果前置岗位和应聘岗位是同一个职位返回合格
	if(prePosId.equals(posId))
		return msg;
	
	Connection conn = null;
	ContentDAO dao = null;
	RowSet rs = null;
	try {
	    conn = AdminDb.getConnection();
	    dao = new ContentDAO(conn);
	    
	    //检查职位的招聘流程是否为flowId
	    boolean flowIsOK = false;
	    
	    StringBuffer sql = new StringBuffer();
	    sql.append("SELECT 1 FROM Z03");
	    sql.append(" WHERE z0301=? and z0381=?");
	    
	    ArrayList sqlParams = new ArrayList();
	    sqlParams.add(posId);
	    sqlParams.add(flowId);
	    
	    try {
		rs = dao.search(sql.toString(), sqlParams);
		flowIsOK = rs.next();
	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		PubFunc.closeDbObj(rs);
	    }	    
	    
	    //不是前置岗位控制的职位，直接返回合格
	    if (!flowIsOK) {
		return msg;
	    }
	    
	    //检查该人员前置岗位考试是否合格
	    String examResult = null; 
	    sql.setLength(0);
	    sqlParams.clear();
	    
	    sql.append("SELECT z6317 FROM Z63");
	    sql.append(" WHERE nbase=? and a0100=?");
	    sql.append(" and Z0301=?");
	    
	    sqlParams.add(nbase);
	    sqlParams.add(a0100);
	    sqlParams.add(prePosId);
	    try {
		rs = dao.search(sql.toString(), sqlParams);
		if (rs.next()) {
		    examResult = rs.getString("z6317")==null ? "" : rs.getString("z6317");
		}
	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		PubFunc.closeDbObj(rs);
	    }
	    
	    //考试是否合格
	    //没参加考试
	    if(examResult == null) {
		msg = "您没有参加综合测试考试，不符合当前岗位申请要求！";
	    } else if (!"01".equalsIgnoreCase(examResult)) {
		//考试不合格
		msg = "您没有通过综合测试考试，不符合当前岗位申请要求！";
	    } 
	} catch (GeneralException e) {
	    e.printStackTrace();
	} finally {
		PubFunc.closeDbObj(rs);
	    PubFunc.closeDbObj(conn);
	}
	
	
	return msg;
    }

	/**
	 * 资格校验 人大规则：申请正式职位需要首先通过“综合测试”岗考试
	 * 
	 * @param nbase
	 * @param a0100s
	 *            : 多个a0100的字符串
	 * @return
	 */
	@Override
    public String checkA0100s(String nbase, String a0100s, String posId) {
		String msg = "";
		String returnSql = "";
		Connection conn = null;
		ContentDAO dao = null;
		RowSet rs = null;
		RowSet rss = null;
		try {
			// 人大：综合测试岗 招聘流程编号:招聘职位编号
			String zpPrePos = SystemConfig.getPropertyValue("zp_pre_pos");
			// 没有定义前置岗位参数 返回合格
			if (StringUtils.isEmpty(zpPrePos)) {
				return msg;
			}

			String[] params = zpPrePos.split(":");
			// 参数格式定义错误 返回合格
			if (params == null || params.length != 2) {
				return msg;
			}

			// 参数定义不完整 返回合格
			if (StringUtils.isEmpty(params[0]) || StringUtils.isEmpty(params[1])) {
				return msg;
			}

			String flowId = params[0].trim();
			String prePosId = params[1].trim();

			conn = AdminDb.getConnection();
			dao = new ContentDAO(conn);

			// 检查职位的招聘流程是否为flowId
			boolean flowIsOK = false;

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT 1 FROM Z03");
			sql.append(" WHERE z0301=? and z0381=?");

			ArrayList sqlParams = new ArrayList();
			sqlParams.add(posId);
			sqlParams.add(flowId);

			rs = dao.search(sql.toString(), sqlParams);
			flowIsOK = rs.next();

			// 不是前置岗位控制的职位，直接返回合格
			if (!flowIsOK) {
				return msg;
			}

			// 检查该人员前置岗位考试是否合格
			String examResult = null;
			sql.setLength(0);
			sqlParams.clear();
			sqlParams.add(nbase);
			sqlParams.add(prePosId);
			sql.append("SELECT z6317,a0100 FROM Z63");
			sql.append(" WHERE nbase=? and Z0301=? and a0100 in (");
			String placeholder = "";
			String[] a0100Arrays = a0100s.split(",");
			int j = 0;
			for (int i = 0; i < a0100Arrays.length; i++) {
				sqlParams.add(a0100Arrays[i]);
				placeholder += ",?";
				j++;
				// 当 in里面的条件超过900直接走查询遍历 然后在组装SQL继续查询(避免SQL语句中in里面的个数超过999时会报错)
				if (j == 900) {
					placeholder = placeholder.substring(1);
					sql.append(placeholder);
					sql.append(" ) ");
					rs = dao.search(sql.toString(), sqlParams);
					while (rs.next()) {
						examResult = rs.getString("z6317") == null ? "" : rs.getString("z6317");
						if (examResult == null && "01".equalsIgnoreCase(examResult)) {
							String a0100 = rs.getString("a0100");
							returnSql += "," + a0100;
						}
					}
					j = 0;
					sqlParams.clear();
					placeholder = "";
					sqlParams.add(nbase);
					sqlParams.add(prePosId);
					sql.setLength(0);
					sql.append("SELECT z6317,a0100 FROM Z63");
					sql.append(" WHERE nbase=? and Z0301=? and a0100 in (");
				}
			}
			placeholder = placeholder.substring(1);
			sql.append(placeholder);
			sql.append(" ) ");

			rss = dao.search(sql.toString(), sqlParams);
			while (rss.next()) {
				examResult = rss.getString("z6317") == null ? "" : rss.getString("z6317");
				if (examResult != null && "01".equalsIgnoreCase(examResult)) {
					String a0100 = rss.getString("a0100");
					returnSql += "," + a0100;
				}
				if (returnSql.length() > 0)
					returnSql = returnSql.substring(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rss);
			PubFunc.closeDbObj(conn);
		}
		return returnSql;
	}
}
