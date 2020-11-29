package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.List;

/**
 * <p>Title: ThemesTag </p>
 * <p>Description:样式皮肤切换按钮 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-4-26 下午05:03:39</p>
 * @author xuj
 * @version 1.0
 */
public class ThemesBarTag extends BodyTagSupport {

	public int doEndTag() throws JspException {
		/*获取系统主题列表 guodd 2020-04-26*/
		List themes= SystemConfig.getSystemThemes();
		StringBuffer strhtml=new StringBuffer();
		try
		{
			UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			if(userView!=null){
				String curthemes = SysParamBo.getSysParamValue("THEMES", userView.getUserName());
				strhtml.append("<div class='list07' id='themesid'>");
				strhtml.append("<ul>");
				for(int i=0;i<themes.size();i++){
					if(curthemes.equals(themes.get(i)))
						continue;
					strhtml.append("<li><a href='####' onclick=\"javascript:showhidden('themesid','none');changeThemes('"+themes.get(i)+"');\"><img class=\"png\" src=\"/images/hcm/themes/"+themes.get(i)+"/nav/huanfu.png\" style='margin-left:6px'/></a></li>");
				}
				strhtml.append("</ul>");
				strhtml.append("</div>");
				pageContext.getOut().println(strhtml.toString());
			}
			return SKIP_BODY;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return SKIP_BODY;			
		}
	}

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}


}
