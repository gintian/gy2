/**
 * 
 */
package com.hjsj.hrms.transaction.sys.cms;

import com.hjsj.hrms.businessobject.sys.cms.Cms_ChannelBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:CmsReleaseTrans</p>
 * <p>Description:发布频道定义的内容，</p>
 * <p>Company:hjsj</p>
 * <p>create time:2007-4-8:11:51:32</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CmsReleaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String chl_no=(String)this.getFormHM().get("chl_no");
		if(chl_no==null|| "".equalsIgnoreCase(chl_no))
			return;
		Cms_ChannelBo cms_bo=new Cms_ChannelBo(this.getFrameconn());
		ArrayList list=cms_bo.getContentList(chl_no);
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean dynabean=(LazyDynaBean)list.get(i);
			int content_type=Integer.parseInt((String)dynabean.get("content_type"));
			if(content_type==1)//t_cms_content's content
			{
				String content=(String)dynabean.get("content");
				if(content==null|| "".equalsIgnoreCase(content))
					continue;
				/**
				 * html文本串转换成javascript字符串变量时
				 * 必须把\r\n转换成掉，以及把"转换成\"
				 */
				content=content.replaceAll("\r\n", "");
				content=content.replaceAll("\n", " ");
				content=content.replaceAll("\"", "\\\\\"");
				buf.append(content);
				buf.append("<br>");				
			}
			else
			{
				/**外部的超链*/
				if(dynabean.get("out_url")==null)
					continue;
				String out_url=(String)dynabean.get("out_url");
				if("".equalsIgnoreCase(out_url.trim()))
					continue;
				
				buf.append("<br>");
				buf.append("<a href=\\\"");
				buf.append((String)dynabean.get("out_url"));
				if(dynabean.get("params")!=null)
				{
					buf.append("?");
					buf.append((String)dynabean.get("params"));
				}
				buf.append("\\\"");				
				buf.append("  target=\\\"");
				buf.append((String)dynabean.get("target"));
				buf.append("\\\"");					
				buf.append(">");
				buf.append((String)dynabean.get("title"));
				buf.append("</a>");
			}
		}//for i loop end.
		this.getFormHM().clear();
		this.getFormHM().put("cms_txt",buf.toString());
	}

}
