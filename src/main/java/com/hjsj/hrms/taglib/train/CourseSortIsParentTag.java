package com.hjsj.hrms.taglib.train;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.sql.Connection;

/**
 * 培训课程是否为上级分类下的课程
 * <p>
 * Title:CourseSortIsParentTag.java
 * </p>
 * <p>
 * Description>:CourseSortIsParentTag.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2011-08-17 16:23:00
 * </p>
 * <p>
 * @version: 5.0
 * </p>
 * <p>
 * @author: LiWeiChao
 */
public class CourseSortIsParentTag extends TagSupport {
	private String codeid;// 课程单位编码
	private String isParent;// 1显示上级 0不显示上级

	public int doStartTag() throws JspException {
		boolean isP = false;
		// if(codeid==null||codeid.length()<1)
		// isP = true;
		UserView userview = (UserView) pageContext.getSession().getAttribute(
				WebConstant.userView);
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			TrainCourseBo tbo = new TrainCourseBo(userview, conn);
			if (!userview.isSuper_admin() && (tbo.isUserParent(codeid) == 2 || tbo.isUserParent(codeid) == -1))
				isP = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if ("1".equals(isParent)) {
			if (isP)
				return EVAL_BODY_INCLUDE;
			else
				return SKIP_BODY;
		} else if ("0".equals(isParent)) {
			if (isP)
				return SKIP_BODY;
			else
				return EVAL_BODY_INCLUDE;
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}
}
