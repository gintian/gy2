package com.hjsj.hrms.module.questionnaire.template.servlet;

import com.hjsj.hrms.module.questionnaire.template.businessobject.AnswerResultBo;
import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: SearchAnswerTemplateServlet </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-9-17 下午2:12:58</p>
 * @author hej
 * @version 1.0
 */
public class SearchAnswerTemplateServlet extends HttpServlet {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int qnId = 0;//需要查询出来
		UserView userView = (UserView)req.getSession().getAttribute(WebConstant.userView); 
		String planid = req.getParameter("planid");//计划id
		String cip = req.getParameter("cip");
		String mainObject = req.getParameter("mainObject");
		String subObject = req.getParameter("subObject");
		String viewtype = "";
		if(mainObject==null||"".equals(mainObject)){
			if(userView!=null){
				mainObject = userView.getA0100();
			}
		}else{
			mainObject = PubFunc.decrypt(mainObject);
			subObject = PubFunc.decrypt(subObject);
		}
		String jsonobject = "";
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		Connection conn = null;
		try{
			conn = AdminDb.getConnection();
			TemplateBo bo = new TemplateBo(conn);
			bo.searchExpirePlan();
			
			planid = SafeCode.keyWord_reback(planid);
			planid = PubFunc.decrypt(planid);
			planid = Integer.parseInt(planid)+"";
			
			String sql = "select qnId from qn_plan where status='1' and planId='"+planid+"'";
			List childList = ExecuteSQL.executeMyQuery(sql);
			if(childList.size()>0){
				LazyDynaBean ldb = (LazyDynaBean)childList.get(0);
				Object object = ldb.get("qnid");
				qnId = Integer.parseInt((String)object);
			}else{
				JSONObject planobj = new JSONObject();
				String errorMSG = ResourceFactory.getProperty("questionnaire.template.nopublisherror");
				planobj.put("success", "false");
				planobj.put("errormessage", errorMSG);
				out.write(planobj.toString());
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("questionnaire.template.nopublisherror")));
			}
			String qn_qnid_data = "qn_"+qnId+"_data";
	        String qn_matrix_qnid_data = "qn_matrix_"+qnId+"_data";
			
			ResultSet  rset = null;
			ResultSet  rset1 = null;
			
			ContentDAO dao = new ContentDAO(conn);
            DbWizard w = new DbWizard(conn);
            int status = 0;
			if(w.isExistTable(qn_qnid_data,false)){
				StringBuffer checksql = new StringBuffer();
				checksql.append( "select status from "+qn_qnid_data+" where planid='"+planid+"' and mainObject='"+mainObject+"' ");
				if(!"".equals(subObject)){
					checksql.append(" and subObject='"+subObject+"'");
				}
				rset = dao.search(checksql.toString());
				if(rset.next()){
					status = rset.getInt("status");
				}
			}
			if(w.isExistTable(qn_matrix_qnid_data,false)){
				StringBuffer checksql = new StringBuffer();
				checksql.append("select status from "+qn_matrix_qnid_data+" where planid='"+planid+"' and mainObject='"+mainObject+"'");
				if(!"".equals(subObject)){
					checksql.append(" and subObject='"+subObject+"'");
				}
				checksql.append(" group by status");
				rset1 = dao.search(checksql.toString());
				if(rset1.next()){
					status = rset1.getInt("status");
				}
			}
			JSONObject obj = null;
			
			Object qnid =  qnId;
			Object[] objs = new Object[1];
			objs[0] = "qnid="+qnid;
			jsonobject = bo.getTemplate(objs);
			//解析参数设置
			obj = JSONObject.fromObject(jsonobject);
			JSONObject qnset = (JSONObject)obj.get("qnset");
			String requiredlogin = "";
			String oneip = "";
			if(!qnset.isNullObject()){
				oneip = (String)qnset.get("oneip");
				requiredlogin = (String)qnset.get("requiredlogin");
				String enddateselected = (String)qnset.get("enddateselected");
				String enddatevalue = (String)qnset.get("enddatevalue");
				if("1".equals(oneip)){//一个ip只能答一次
					//查询数据库qn_qnid_data
					String sqlip = "select cip from "+qn_qnid_data+" where cip='"+cip+"' and status=2";
		            String sqlipmatrix = "select mainObject from "+qn_matrix_qnid_data+" where cip='"+cip+"' and status=2";
		            List ipList = new ArrayList();
		            if(w.isExistTable(qn_qnid_data, false)){
		            		ipList = ExecuteSQL.executeMyQuery(sqlip);
		            }
					List ipmatrixList = new ArrayList();
					if(w.isExistTable(qn_matrix_qnid_data, false)){
						ipmatrixList = ExecuteSQL.executeMyQuery(sqlipmatrix);
					}
					if(ipList.size()>0||ipmatrixList.size()>0){
						JSONObject onlyip = new JSONObject();
						String errorMSG = ResourceFactory.getProperty("questionnaire.template.alreadyanswerqn");
						onlyip.put("success", "false");
						onlyip.put("errormessage", errorMSG);
						out.write(onlyip.toString());
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("questionnaire.template.alreadyanswerqn")));
					}
				}			
				if("1".equals(requiredlogin)){//是否登陆--不登录不允许答题
					if(userView==null){
						String forwordurl = SafeCode.keyWord_reback(req.getParameter("forwordurl"));
						req.getSession().setAttribute("ehr_apply_path",forwordurl);
						JSONObject idlogin = new JSONObject();
						String errorMSG = ResourceFactory.getProperty("questionnaire.template.nologinnoanswer");
						idlogin.put("success", "false");
						idlogin.put("idlogin", "1");
						idlogin.put("errormessage", errorMSG);
						out.write(idlogin.toString());
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("questionnaire.template.nologinnoanswer")));
					}
				}	
				if("1".equals(enddateselected)){//超过问卷有效期
					if(enddatevalue!=null&&!"".equals(enddatevalue)){
						int day=Integer.parseInt(enddatevalue);
						String sqldata = "select pubTime from qn_plan where status='1' and qnId='"+qnId+"'"; 
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date createTime=null;
						List dataList = ExecuteSQL.executeMyQuery(sqldata);
						if (dataList.size()>0){
							LazyDynaBean ldb = (LazyDynaBean)dataList.get(0);
							createTime = sdf.parse((String)ldb.get("pubtime"));//发布时间
						}
						Date time = sdf.parse(sdf.format(new Date()));
						long diff = time.getTime() - createTime.getTime();
						long days = day * 1000 * 60 * 60 * 24;
						if(diff>days){
							JSONObject enddata = new JSONObject();
							String errorMSG = ResourceFactory.getProperty("questionnaire.template.overquestiondata");
							enddata.put("success", "false");
							enddata.put("errormessage", errorMSG);
							out.write(enddata.toString());
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("questionnaire.template.overquestiondata")));
						}
					}
				}
			}
			if(status==1){//在答
				AnswerResultBo arbo = new AnswerResultBo();
				jsonobject = arbo.getTemplateResult(objs, mainObject, subObject, planid);
				obj = JSONObject.fromObject(jsonobject);
				obj.put("status", "1");
			}
			else if(status ==2){//已答
				if("1".equals(requiredlogin)|| "1".equals(oneip)){
					JSONObject onlyone = new JSONObject();
					String errorMSG = ResourceFactory.getProperty("questionnaire.template.loginonlyone");
					onlyone.put("success", "false");
					onlyone.put("status", "2");
					onlyone.put("errormessage", errorMSG);
					out.write(onlyone.toString());
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("questionnaire.template.loginonlyone")));
				}
			}else{//未答
				obj.put("status", "0");
				if(mainObject==null||"".equals(mainObject)){
					if("0".equals(requiredlogin)||userView==null){
						obj.put("saveflag", "false");
					}
				}
			}
			if(userView==null){//未登录
				viewtype = "0";
			}else{
				viewtype = "1";
			}
			obj.put("success", "true");
			obj.put("viewtype", viewtype);
			obj.put("planId", Integer.parseInt(planid));
			out.write(obj.toString());
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeResource(conn);
			out.flush();
			out.close();
		}
	}
}
