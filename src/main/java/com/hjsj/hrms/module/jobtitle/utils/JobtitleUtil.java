package com.hjsj.hrms.module.jobtitle.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;

/**
 * 资格评审_学科组
 * @createtime Mar 31, 2016 9:07:55 AM
 * @author chent
 */
public class JobtitleUtil {
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public JobtitleUtil(Connection conn, UserView userview){
		this.conn = conn;
		this.userview = userview;
	}

	/** 聘委会成员 菜单显示文本 */
	public static final String ZC_MENU_COMMITTEESHOWTEXT = ResourceFactory.getProperty("zc.menu.committeeshowtext");
	/** 学科组成员 菜单显示文本 */
	public static final String ZC_MENU_SUBJECTSSHOWTEXT = ResourceFactory.getProperty("zc.menu.subjectsshowtext");
	/** 评审环节1阶段 显示文本 */
	public static final String ZC_REVIEWFILE_STEP1SHOWTEXT = ResourceFactory.getProperty("zc.reviewfile.step1showtext");
	/** 评审环节2阶段 显示文本 */
	public static final String ZC_REVIEWFILE_STEP2SHOWTEXT = ResourceFactory.getProperty("zc.reviewfile.step2showtext");
	/** 评审环节3阶段 显示文本 */
	public static final String ZC_REVIEWFILE_STEP3SHOWTEXT = ResourceFactory.getProperty("zc.reviewfile.step3showtext");
	/** 评审环节4阶段 显示文本 **/
	public static final String ZC_REVIEWFILE_STEP4SHOWTEXT = ResourceFactory.getProperty("zc.reviewfile.step4showtext");
	/** 上会材料 评审状态 显示文本 **/
	public static final String ZC_REVIEWFILE_W0573_1 = ResourceFactory.getProperty("zc.reviewfile.w0573_1");
	public static final String ZC_REVIEWFILE_W0573_2 = ResourceFactory.getProperty("zc.reviewfile.w0573_2");
	/**
	 * 配置归档方案的源指标串 
	 *
	 * 源指标拼写规则如下:
	 * 每个源指标以“指标名称:指标类型 :代码集id(不是代码类型则为空格):归档属性名称";
	 * 多个元指标中间用”,“隔开；* 多个之间必须用‘,’分开
	 * 例："会议名称:A: :meeting_name,申报职务:A:AJ:Apply_post"
	 */
	public static final String ZC_REVIEWARCHIVE_STR = ResourceFactory.getProperty("zc.resultsArchiving");	
	/**
	 * 获取上级、本级、下级
	 * @param unitIdByBusi 
	 * @return sql
	 */
	public String getB0110Sql_upToDown(String unitIdByBusi) {
		StringBuilder sql = new StringBuilder();
		String[] tmp = unitIdByBusi.split("`");
		
		sql.append("and (");
		
		for(int i=0; i<tmp.length; i++){//上级
			String b = tmp[i].substring(2);
			if(i==0){
				sql.append(" b0110 like '"+b+"%' ");//本级、下级
			}else{
				sql.append(" or b0110 like '"+b+"%' ");//本级、下级
			}
			
			int len = b.length();
			while(len >= 1){
				if(!StringUtils.isEmpty(AdminCode.getCodeName("UN", b)) || !StringUtils.isEmpty(AdminCode.getCodeName("UM", b))){
					sql.append("or b0110 = '"+b+"' ");
				}
				b = b.substring(0, b.length()-1);
				len = b.length();
			}
			sql.append("or b0110 = '' ");//最高权限
			
		}
		
		sql.append(") ");
		
		return sql.toString();
	}
	/**
	 * 获取本级、下级
	 * @param unitIdByBusi
	 * @return sql 权限过滤sql
	 */
    public String getB0110Sql_down(String unitIdByBusi) {
    	StringBuilder sql = new StringBuilder();
    	
    	try{
    		String[] tmp = unitIdByBusi.split("`");
    		sql.append(" and (");
    		for(int i=0; i<tmp.length; i++){
    			String b = tmp[i].substring(2);
    			if (i>0)
    				sql.append(" or ");
    			sql.append("b0110 like '"+b+"%' ");//本级、下级
    		}
    		sql.append(") ");
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return sql.toString();
	}
    
    /**
     * 查询组织机构的顶级机构
     * @return
     * 		以逗号分隔的机构号
     * @throws GeneralException
     */
    public String getTopOrgs() throws GeneralException {
    	ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String orgs = "";
		try {
			String sql = "SELECT CODEITEMID FROM ORGANIZATION WHERE PARENTID=CODEITEMID";
			rs = dao.search(sql);
			while(rs.next()) {
				orgs+=rs.getString("CODEITEMID")+",";
			}
			if(StringUtils.isNotBlank(orgs)) {
				orgs = orgs.substring(0,orgs.length());
			}
			return orgs;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
    }
}
