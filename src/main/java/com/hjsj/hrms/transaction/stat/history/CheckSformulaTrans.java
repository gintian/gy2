package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class CheckSformulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String flag = "";
		String sformula = (String)this.getFormHM().get("sformula");
		sformula=SafeCode.decode(sformula);
		//liuy 2014-12-23 6235：安徽高速：组织机构-单位管理-信息维护-统计分析-统计方式设置（设置完取不出来） start
		sformula = PubFunc.keyWord_reback(sformula);
		//liuy end
		String stat = (String)this.getFormHM().get("stat");
		this.getFormHM().remove("stat");
		/*
		 * zgd 2014-8-27 缺陷4024 不对历史时点的统计分析进行操作
		if(sformula.indexOf("-")!=-1||sformula.indexOf("*")!=-1||sformula.indexOf("/")!=-1)
			stat="";
		*/
		YksjParser yp = new YksjParser(getUserView(), "stat".equals(stat)||"photo".equals(stat)?DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET):getItemList(new ContentDAO(this.frameconn)), YksjParser.forSearch, "photo".equalsIgnoreCase(stat)?YksjParser.STRVALUE:YksjParser.FLOAT
				, YksjParser.forPerson,"Ht", "");
		
		//System.out.println("ok1...");
		
		yp.setCon(this.getFrameconn());
		boolean b = false;
		try{
			b = yp.Verify_where(sformula.trim());
		}catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
		if (b) {// 校验通过
			flag="ok";
		}else{
			flag = yp.getStrError();
		} 
		this.getFormHM().put("flag", SafeCode.encode(flag));
	}

	private ArrayList getItemList(ContentDAO dao){
		ArrayList itemlist = new ArrayList();
		  try{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from hr_emp_hisdata where 1=2");
			this.frowset = dao.search(sql.toString());
			ResultSetMetaData rsmd = this.frowset.getMetaData();
			int size = rsmd.getColumnCount();

			for (int i = 1; i <= size; i++) {
				String itemid = rsmd.getColumnName(i).toUpperCase();
				if (itemid.length() < 4 || "nbase".equalsIgnoreCase(itemid)
						|| "a0000".equalsIgnoreCase(itemid))
					continue;
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem!=null){
					itemlist.add(fielditem);
				}
			}
			
		  }catch(Exception e){e.printStackTrace();}
		return itemlist;
	  }
}
