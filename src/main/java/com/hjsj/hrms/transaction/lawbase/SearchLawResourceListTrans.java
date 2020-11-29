package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * create time:2005-6-11:14:39:10
 * 
 * 规章制度搜索列表
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class SearchLawResourceListTrans extends IBusiness {

	/*
	 * 
	 * 入口程序
	 * 
	 */
	public void execute() throws GeneralException {

		String sql = "select file_id,name,title,type,content_type,valid,note_num,issue_org,notes,issue_date,implement_date,valid_date,ext,base_id from law_base_file";
		String flag = "0";
		if (this.getFormHM().get("flag") != null) {
			flag = this.getFormHM().get("flag").toString();
		} else {
			flag = "0";
		}
		if ("1".equals(flag)) {
			RecordVo vo = (RecordVo) this.getFormHM().get("lawResourceov");
			
			/*
			 * 取得提交的查询
			 */
			String[][] str = { { "name", vo.getString("name") },
					{ "title", vo.getString("title") },
					{ "type", vo.getString("type") },
					{ "content_type", vo.getString("content_type") },
					{ "note_num", vo.getString("note_num") },
					{ "issue_org", vo.getString("issue_org") },
					{ "notes", vo.getString("notes") } };
			/*
			 * 遍历循环操作
			 */
			Hashtable searchHt = new Hashtable();

			String strSql = " where ";

			for (int i = 0; i < str.length; i++) {

				String first = str[i][0];
				String second = str[i][1];
				if (!("".equals(second.trim()) || second == null)) {
					searchHt.put(first, second);
				}

			}

			if (searchHt.size() != 0) {
				Enumeration x = searchHt.keys();

				while (x.hasMoreElements()) {
					String typeid = x.nextElement().toString();
					String typeName = searchHt.get(typeid).toString();
					strSql = strSql + " " + typeid + " like '" + typeName
							+ "' and ";
				}
			}
			
			if(searchHt.size()==0)
			{
				strSql=strSql+" 1>2 ";
			}
			else
			{
				strSql = strSql + " 1=1 ";
			}
			sql = sql + strSql;
		}

		StringBuffer strsql = new StringBuffer();
		strsql.append(sql);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		try 
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) {

				RecordVo vo = new RecordVo("law_base_file");
				vo.setString("file_id", PubFunc.nullToStr(this.frowset.getString("file_id")));
				vo.setString("name", PubFunc.nullToStr(this.frowset.getString("name")));
				vo.setString("title", PubFunc.nullToStr(this.frowset.getString("title")));
				vo.setString("type", PubFunc.nullToStr(this.frowset.getString("type")));
				vo.setString("content_type", PubFunc.nullToStr(this.frowset.getString("content_type")));
				vo.setString("valid", PubFunc.nullToStr(this.frowset.getString("valid")));
				vo.setString("note_num", PubFunc.nullToStr(this.frowset.getString("note_num")));
				vo.setString("issue_org", PubFunc.nullToStr(this.frowset.getString("issue_org")));
				vo.setString("notes", PubFunc.nullToStr(this.frowset.getString("notes")));
				vo.setDate("issue_date", PubFunc.FormatDate(this.frowset.getDate("issue_date")));
				vo.setDate("implement_date", PubFunc.FormatDate(this.frowset.getDate("implement_date")));
				vo.setDate("valid_date", PubFunc.FormatDate(this.frowset.getDate("valid_date")));
				vo.setString("ext", PubFunc.nullToStr(this.frowset.getString("ext")));
				vo.setString("base_id", PubFunc.nullToStr(this.frowset.getString("base_id")));
				list.add(vo);
			}
			this.getFormHM().put("lawResourcelist", list);
		} 
		catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} 
	}

}