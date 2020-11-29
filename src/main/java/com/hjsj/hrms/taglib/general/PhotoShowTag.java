package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.taglib.TagUtility;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 15, 2005:3:26:50 PM
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PhotoShowTag extends BodyTagSupport {
	/** 表单名称 */
	private String name;
	/** 人员库前缀 */
	private String dbpre;
	/** 人员库编号 */
	private String a0100;
	/** 范围 */
	private String scope;
	/***/
	private String width;
	/***/
	private String height;
	/** 图片名称 */
	private String ids;
	/* 是否为照片墙请求 xiaoyun 2014-6-13 start */
	private String photoWall;
	/* 是否为照片墙请求 xiaoyun 2014-6-13 end */

	/**
     * 
     */
	private String div;
	private String href;
	private String target;
	private String onclick;
	private String title;

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getDiv() {
		return div;
	}

	public void setDiv(String div) {
		this.div = div;
	}

	public PhotoShowTag() {
		super();
	}

	public int doStartTag() throws JspException {
		Connection conn = null;
		StringBuffer str_html = new StringBuffer();

		if (scope == null || "".equals(scope))
			scope = "session";
		
		Object value = TagUtility.getClassValue(this.pageContext, dbpre);
		if (value == null)
			return 0;
		
		String dbname = (String) value;
		if (dbname.length() > 3)
		    dbname = PubFunc.decrypt(dbname);
		
		value = TagUtils.getInstance().lookup(pageContext, name, a0100, scope);
		if (value == null)
			return 0;
		
		String a0100 = (String) value;
		if (a0100.length() > 8)
		    a0100 = PubFunc.decrypt(a0100);

		try {
			conn = AdminDb.getConnection();
			PhotoImgBo pib = new PhotoImgBo(conn);
			pib.setIdPhoto(true);
            img2Html(pib.getPhotoPath(dbname, a0100), str_html);
			pageContext.getOut().println(str_html.toString());
			return EVAL_BODY_BUFFERED;
		} catch (Exception ge) {
			ge.printStackTrace();
			return 0;
		} finally {
			PubFunc.closeDbObj(conn);
		}
	}

	private void img2Html(String photourl, StringBuffer str_html) {
		if (div == null || "".equals(div))
			/* 标识：2023 新版照片墙 xiaoyun 2014-6-11 start */
			if (StringUtils.isNotEmpty(photoWall)
					&& StringUtils.equals(photoWall, "true")) {

			} else {
				str_html.append("<div class=\"photo\">");
			}
		/* 标识：2023 新版照片墙 xiaoyun 2014-6-11 end */
		if (target != null && target.length() > 0)
			target = "target=\"" + target + "\"";
		else
			target = "";
		if (onclick == null || onclick.length() <= 0) {
			if (this.href != null && this.href.length() > 0)
				str_html.append("<a href=\"" + this.href + "\" " + target + ">");
		} else {
			str_html.append("<a href=\"" + this.href + "\" onclick=" + onclick
					+ ">");
		}

		/* 标识：2023 新版照片墙(招聘管理问题) xiaoyun 2014-6-18 start */
		if (StringUtils.isNotEmpty(photoWall)
				&& StringUtils.equals(photoWall, "true")) {
			str_html.append("<img class='image' src=\"");
		} else {
			str_html.append("<img src=\"");
		}
		/* 标识：2023 新版照片墙(招聘管理问题) xiaoyun 2014-6-18 end */
		str_html.append(photourl.toString());
		str_html.append("\" ");
		if (ids != null) {
			str_html.append(" id=\"");
			str_html.append(ids);
			str_html.append("\"");
		}
		if (height != null) {
			str_html.append(" height=\"");
			str_html.append(height);
			str_html.append("\"");
		}
		if (width != null) {
			str_html.append(" width=\"");
			str_html.append(width);
			str_html.append("\"");
		}
		if (title != null) {
			str_html.append(" title=\"");
			str_html.append(title);
			str_html.append("\"");
		}
		str_html.append(" border=0 >");
		if (this.href != null && this.href.length() > 0)
			str_html.append("</a>");
		if (div == null || "".equals(div))
			/* 标识：2023 新版照片墙 xiaoyun 2014-6-11 start */
			if (StringUtils.isNotEmpty(photoWall)
					&& StringUtils.equals(photoWall, "true")) {

			} else {
				str_html.append("</div>");
			}
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getPhotoWall() {
		return photoWall;
	}

	public void setPhotoWall(String photoWall) {
		this.photoWall = photoWall;
	}


}
