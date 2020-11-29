package com.hjsj.hrms.servlet.kq;

import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataAppealMainService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataAppealMainServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 考勤 确认考勤servlet类
 * @author wangbo
 * create time  2018-10-25
 *
 */
public class KqDataConfirmServlet extends HttpServlet{

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		/**
		 * label 值为 show
		 * 		加载考勤确认函 html 代码
		 * label 值为email
		 * 		邮件确认考勤
		 * label 值为 pendingtask
		 * 		办链接确认考勤
		 */
		resp.setContentType("text/html;charset=UTF-8");
		resp.setCharacterEncoding("GBK");
		String scheme_id =req.getParameter("schemeId");
		scheme_id = PubFunc.decrypt(scheme_id);
		String kq_year = req.getParameter("kq_year");
		String kq_duration =req.getParameter("kq_duration");
		// 51293 之前格式错误 其他操作审批 驳回等url链接中机构标识变量为orgId  保持统一
		String org_id = req.getParameter("orgId");
		// 53768 兼容org_id
		if(StringUtils.isBlank(org_id))
			org_id = req.getParameter("org_id");
		org_id = PubFunc.decrypt(org_id);
		String label = req.getParameter("label");
		if("email".equalsIgnoreCase(label)){//发送考勤邮件，有效期30天
			String sendTime = req.getParameter("sendTime");
			SimpleDateFormat sdf=  new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date sendDate = sdf.parse(sendTime);
				Date showDate = new Date();
				int day = (int) ((showDate.getTime()-sendDate.getTime())/(1000*60*60*24));
				if(day > 30){
					resp.getWriter().write("<html><head></head><body><table width='100%' height='100%'><tr><td align='center' valign='middle'>"+ResourceFactory.getProperty("kq.archive.scheme.kqconfrim.chaoshi")+"</td></tr></table></body></html>");
					return;
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
				return;
			}
		}
		Connection conn = null;
		UserView userView = null;
		
		try {
			conn = AdminDb.getConnection();
			if("email".equalsIgnoreCase(label)){//邮件 确认考勤
				String token = req.getParameter("token");
				token = PubFunc.decrypt(token);
				String[] tokens = token.split(",");
				userView = new UserView(tokens[0], conn);
				userView.canLogin(false);
			}else if("show".equalsIgnoreCase(label)){//待办显示考勤确认函
				userView = (UserView) req.getSession().getAttribute(WebConstant.userView);
				KqDataAppealMainService kqDataAppealMainService = new KqDataAppealMainServiceImpl(userView, conn);
				String content = kqDataAppealMainService.getKqConfirmLetter(Integer.parseInt(scheme_id), kq_year, kq_duration, org_id);
				resp.getWriter().write(content);
				return;
			}else if("pendingtask".equalsIgnoreCase(label)){//待办链接确认考勤
				userView = (UserView) req.getSession().getAttribute(WebConstant.userView);
			}
			KqDataAppealMainService kqDataAppealMainService = new KqDataAppealMainServiceImpl(userView, conn);
			String flag =req.getParameter("flag");
			String confirmMemo =req.getParameter("confirm_memo");
			boolean confirmFlag = kqDataAppealMainService.updateKqConfirm(Integer.parseInt(scheme_id), kq_year, kq_duration, org_id, flag, confirmMemo);
			if("email".equalsIgnoreCase(label)){//邮件确认考勤，页面返回值
				if(confirmFlag)
					resp.getWriter().println("<html><head></head><body><table width='100%'><tr><td align='center' valign='middle'>"+ResourceFactory.getProperty("kq.archive.scheme.kqconfrim.success")+"</td></tr></table></body></html>");
				else
					resp.getWriter().println("<html><head></head><body><table width='100%'><tr><td align='center' valign='middle'>"+ResourceFactory.getProperty("kq.archive.scheme.kqconfrim.fail")+"</td></tr></table></body></html>");
			}else{//待办链接确认考勤，页面返回值
				if(confirmFlag)
					resp.getWriter().println("<html><head></head><body><table width='100%'><tr><td align='center' valign='middle' height='30' style='font-size:14px;'>"+ResourceFactory.getProperty("kq.archive.scheme.kqconfrim.success")+"</td></tr><tr><td align='center'><input style='background:url(/images/kq/kqdata/confirm_btn.png) no-repeat;color:#FFF;border:none;background-size:38px 21px;cursor:pointer;' type='button' onclick='window.location=\"/templates/index/hcm_portal.do?b_query=link\";' value='"+ResourceFactory.getProperty("kq.data.appeal.back")+"'/></td></tr></table></body></html>");
				else
					resp.getWriter().println("<html><head></head><body><table width='100%'><tr><td align='center' valign='middle' height='30' style='font-size:14px;'>"+ResourceFactory.getProperty("kq.archive.scheme.kqconfrim.fail")+"</td></tr><tr><td align='center'><input style='background:url(/images/kq/kqdata/confirm_btn.png) no-repeat;color:#FFF;border:none;background-size:38px 21px;cursor:pointer;' type='button' onclick='window.location=\"/templates/index/hcm_portal.do?b_query=link\";' value='"+ResourceFactory.getProperty("kq.data.appeal.back")+"'/></td></tr></table></body></html>");
			}
		} catch (GeneralException e) {
			e.printStackTrace();
			resp.getWriter().println("<html><head></head><body><table width='100%' height='100%'><tr><td align='center' valign='middle'>"+ResourceFactory.getProperty("kq.archive.scheme.kqconfrim.errorfail")+"</td></tr></table></body></html>");
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().println("<html><head></head><body><table width='100%' height='100%'><tr><td align='center' valign='middle'>"+ResourceFactory.getProperty("kq.archive.scheme.kqconfrim.errorfail")+"</td></tr></table></body></html>");
		}finally{
			PubFunc.closeDbObj(conn);
		}
	}
	
}
