package com.hjsj.hrms.module.certificate.utils;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;

/**
 * 资质证书(证照)管理权限类
 * @Title:        
 * @Description:  统一提供资质证书(证照)管理权限相关方法
 * @Company:      hjsj     
 * @Create time:  2018-06-25 上午11:55:23
 * @author        zhaoxj
 * @version       1.0
 * 
 */
public class CertificatePrivBo {
	
    /** 加载公共+上级+本级+下级 */
    public final static int LEVEL_GLOBAL_PARENT_SELF_CHILD = 0;
    /** 加载公共+上级+本级 */
    public final static int LEVEL_GLOBAL_PARENT_SELF = 1;
    /** 加载本级+下级 */
    public final static int LEVEL_SELF_CHILD = 2;
    /** 加载上级 */
    public final static int LEVEL_PARENT = 3;

	
    public CertificatePrivBo() {

    }

    public String getB0110(UserView userView) throws GeneralException {
        String b0110 = "";
        try {
            String codeid = "";
            if (userView.isSuper_admin() || "1".equals(userView.getGroupId()))
                return "HJSJ";
            
            codeid = userView.getUnitIdByBusi("10");
            if (codeid == null || "".equals(codeid) || "UN".equalsIgnoreCase(codeid)
                    || "UM`".equalsIgnoreCase(codeid) || "@K`".equalsIgnoreCase(codeid)) {
                throw new Exception("您没有证照管理范围权限！请联系管理员。");
            }

            if (codeid.trim().length() < 3)
                return "HJSJ";
            
            if (codeid.indexOf("`") == -1) {
                if (codeid.startsWith("UN") || codeid.startsWith("UM")) {
                    b0110 = codeid.substring(2);
                } else {
                    b0110 = codeid;
                }
                
                return b0110;
            } 
            
            String[] temps = codeid.split("`");
            for (int i = 0; i < temps.length; i++) {
                if (temps[i].startsWith("UN") || temps[i].startsWith("UM")) {
                    b0110 += temps[i].substring(2) + "`";
                } else {
                    b0110 += temps[i] + "`";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception("您没有证照管理的管理范围权限！请联系管理员。"));
        }
        return b0110;
    }
    
    /**
     * 获取证书所属机构sql条件
     * sql形如：(B0110='' or B0110 is null or B0110='UN`' or B0110='HJSJ' OR B0110 LIKE '0102%' OR B0110=LEFT('0102',LEN(B0110)))
     * @param conn
     * @param userView
     * @return
     * @throws GeneralException
     */
    public String getCertOrgWhere(Connection conn, UserView userView) throws GeneralException {
    	return getCertOrgWhere(conn, userView, "");
	}
    
    /**
     * 获取证书所属机构sql条件（指标带前缀）
     * sql形如：(UsrAxx.B0110='' or UsrAxx.B0110 is null or UsrAxx.B0110='UN`' or UsrAxx.B0110='HJSJ' OR UsrAxx.B0110 LIKE '0102%' OR UsrAxx.B0110=LEFT('0102',LEN(UsrAxx.B0110)))
     * @param conn
     * @param userView
     * @return
     * @throws GeneralException
     */
    public String getCertOrgWhere(Connection conn, UserView userView, String certOrgFldPre) throws GeneralException {
    	
    	CertificateConfigBo certConfig = new CertificateConfigBo(conn, userView);
    	String certOrgFld = certConfig.getCertOrganization();
    	//没设置证书归属组织指标
    	if (StringUtils.isEmpty(certOrgFld))
    		return "1=1";
    	
    	if (StringUtils.isNotEmpty(certOrgFldPre)) {
			certOrgFld = certOrgFldPre + "." + certOrgFld;
		}    	
    	
    	return getPrivB0110Whr(userView, certOrgFld, CertificatePrivBo.LEVEL_SELF_CHILD);
		
	}

    /**
     * 获取所属单位sql条件
     * sql形如：(B0110='' or B0110 is null or B0110='UN`' or B0110='HJSJ' OR B0110 LIKE '0102%' OR B0110=LEFT('0102',LEN(B0110)))
     * @Title: getPrivB0110Whr   
     * @Description:    
     * @param userView 操作用户
     * @param b0110Fld 所属单位指标 自行更据需要传入是否带前缀，比如 UsrAxx.Axxyy或Axxyy
     * @param levelFlag 加载层级标志  0：加载公共+上级+本级+下级   1：加载公共+上级+本级 ； 2：加载本级+下级；3:加载上级
     * @return 
     * @throws GeneralException
     */
    public String getPrivB0110Whr(UserView userView, String b0110Fld, Integer levelFlag) throws GeneralException{
        if (userView == null)
            return "1=2";
        
        if (userView.isSuper_admin())
            return "1=1";
        
        b0110Fld = (b0110Fld == null || "".equals(b0110Fld)) ? "b0110" : b0110Fld;
        levelFlag = (levelFlag == null) ? 0 : levelFlag;
        
        String privB0110Str = getB0110(userView);
        
        String[] privB0110s = privB0110Str.split("`");
        
        StringBuffer sqlWhr = new StringBuffer();
        sqlWhr.append("(");
        
        // UN`是全权
        if ("UN`".equalsIgnoreCase(privB0110Str) || "`".equalsIgnoreCase(privB0110Str))
            return "1=1";
        
        for (int i = 0; i < privB0110s.length; i++) {
            String privB0110 = privB0110s[i].trim();
            if ("".equals(privB0110))
                continue;
            
            if (privB0110.startsWith("UN") || privB0110.startsWith("UM") || privB0110.startsWith("@K"))
                privB0110 = privB0110.substring(2);
            
            if ("HJSJ".equals(privB0110))
                privB0110 = "";            
            
            if (sqlWhr.length() > 1)
                sqlWhr.append(" OR ");
            
            sqlWhr.append(" 1=2 ");
            //公共
            if (levelFlag != LEVEL_SELF_CHILD && levelFlag != LEVEL_PARENT ) {
                sqlWhr.append(" OR ");
                sqlWhr.append(b0110Fld).append("=''");
                sqlWhr.append(" or ").append(b0110Fld).append(" is null");
                sqlWhr.append(" or ").append(b0110Fld).append("='UN`'");
                sqlWhr.append(" or ").append(b0110Fld).append("='HJSJ'");
            }
            
            if (levelFlag != LEVEL_SELF_CHILD) {
                //上级
                sqlWhr.append(" OR ");
                sqlWhr.append("(");
                sqlWhr.append(b0110Fld).append("=").append(Sql_switcher.left("'" + privB0110 + "'", Sql_switcher.length(b0110Fld)));
                sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
                sqlWhr.append(")");
            }
            
            //下级
            if (levelFlag != LEVEL_GLOBAL_PARENT_SELF && levelFlag != LEVEL_PARENT) {
                sqlWhr.append(" OR ");
                sqlWhr.append("(");
                sqlWhr.append(b0110Fld).append(" LIKE '").append(privB0110).append("%'");
                sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
                sqlWhr.append(")");
            }
            
            //本级
            if (levelFlag != LEVEL_PARENT) {
                sqlWhr.append(" OR ");
                sqlWhr.append(b0110Fld).append("='").append(privB0110).append("'");
            }
            
        }
        sqlWhr.append(")");
        return sqlWhr.toString();
    }
    /**
     * 获取自助用户证书所属机构sql条件（指标带前缀）
     * sql形如：(UsrAxx.B0110='' or UsrAxx.B0110 is null or UsrAxx.B0110='UN`' or UsrAxx.B0110='HJSJ' OR UsrAxx.B0110 LIKE '0102%' OR UsrAxx.B0110=LEFT('0102',LEN(UsrAxx.B0110)))
     * @param conn
     * @param userView
     * @return
     * @throws GeneralException
     */
    public String getSelfCertOrgWhere(Connection conn, UserView userView, String certOrgFldPre) throws GeneralException {
    	
    	CertificateConfigBo certConfig = new CertificateConfigBo(conn, userView);
    	String certOrgFld = certConfig.getCertOrganization();
    	//没设置证书归属组织指标
    	if (StringUtils.isEmpty(certOrgFld))
    		return "1=1";
    	
    	if (StringUtils.isNotEmpty(certOrgFldPre)) {
			certOrgFld = certOrgFldPre + "." + certOrgFld;
		}    	
    	
    	return getSelfPrivB0110Whr(userView, certOrgFld, CertificatePrivBo.LEVEL_GLOBAL_PARENT_SELF);
		
	}
    /**
     * 获取自助用户所属单位sql条件
     * sql形如：(B0110='' or B0110 is null or B0110='UN`' or B0110='HJSJ' OR B0110 LIKE '0102%' OR B0110=LEFT('0102',LEN(B0110)))
     * @Title: getSelfPrivB0110Whr   
     * @Description:    
     * @param userView 操作用户
     * @param b0110Fld 所属单位指标 自行更据需要传入是否带前缀，比如 UsrAxx.Axxyy或Axxyy
     * @param levelFlag 加载层级标志  0：加载公共+上级+本级+下级   1：加载公共+上级+本级 ； 2：加载本级+下级；3:加载上级
     * @return 
     * @throws GeneralException
     */
    public String getSelfPrivB0110Whr(UserView userView, String b0110Fld, Integer levelFlag) throws GeneralException{
        if (userView == null)
            return "1=2";
        
        if (userView.isSuper_admin())
            return "1=1";
        
        b0110Fld = (b0110Fld == null || "".equals(b0110Fld)) ? "b0110" : b0110Fld;
        levelFlag = (levelFlag == null) ? 0 : levelFlag;
        
        StringBuffer sqlWhr = new StringBuffer();
        // 校验是否有业务范围
        String codeid = getBusiPriv("10", userView);
        
        if (StringUtils.isBlank(codeid)) {
        	
        	String privB0110 = userView.getUserDeptId();
        	if (StringUtils.isBlank(privB0110))
        		privB0110 = userView.getUserOrgId();
        	
        	sqlWhr.append("(");
        	
        	if ("".equals(privB0110))
        		return "1=1";
        	
        	if (privB0110.startsWith("UN") || privB0110.startsWith("UM") || privB0110.startsWith("@K"))
        		privB0110 = privB0110.substring(2);
        	
        	if (sqlWhr.length() > 1)
        		sqlWhr.append(" OR ");
        	
        	sqlWhr.append(" 1=2 ");
        	//公共
        	if (levelFlag != LEVEL_SELF_CHILD && levelFlag != LEVEL_PARENT ) {
        		sqlWhr.append(" OR ");
        		sqlWhr.append(b0110Fld).append("=''");
        		sqlWhr.append(" or ").append(b0110Fld).append(" is null");
        		sqlWhr.append(" or ").append(b0110Fld).append("='UN`'");
        		sqlWhr.append(" or ").append(b0110Fld).append("='HJSJ'");
        	}
        	//上级
        	if (levelFlag != LEVEL_SELF_CHILD) {
        		sqlWhr.append(" OR ");
        		sqlWhr.append("(");
        		sqlWhr.append(b0110Fld).append("=").append(Sql_switcher.left("'" + privB0110 + "'", Sql_switcher.length(b0110Fld)));
        		sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
        		sqlWhr.append(")");
        	}
        	//下级
        	if (levelFlag != LEVEL_GLOBAL_PARENT_SELF && levelFlag != LEVEL_PARENT) {
        		sqlWhr.append(" OR ");
        		sqlWhr.append("(");
        		sqlWhr.append(b0110Fld).append(" LIKE '").append(privB0110).append("%'");
        		sqlWhr.append(" and ").append(b0110Fld).append("<>'").append(privB0110).append("'");
        		sqlWhr.append(")");
        	}
        	//本级
        	if (levelFlag != LEVEL_PARENT) {
        		sqlWhr.append(" OR ");
        		sqlWhr.append(b0110Fld).append("='").append(privB0110).append("'");
        	}
        	
        	sqlWhr.append(")");
        }
        // 若存在业务范围则走业务范围以内
        else {
        	sqlWhr.append(this.getPrivB0110Whr(userView, b0110Fld, CertificatePrivBo.LEVEL_SELF_CHILD));
        }
        
        return sqlWhr.toString();
    }
    /**
     * userView中没有获取纯业务范围的方法，通过反射获取业务范围数据 暂时手动获取
     * @param busiId
     * @param userView
     * @return
     */
    private String getBusiPriv(String busiId, UserView userView) {
    	
    	return userView.getBusiPriv(busiId);
    }
}
