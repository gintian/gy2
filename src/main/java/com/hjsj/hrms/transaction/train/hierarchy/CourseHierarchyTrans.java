package com.hjsj.hrms.transaction.train.hierarchy;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CourseHierarchyTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String a_code = (String) hm.get("a_code");
		
		if(a_code != null && a_code.length() > 0)
		    a_code = PubFunc.decrypt(SafeCode.decode(a_code));
		
		if(a_code == null)
			a_code = (String) this.getFormHM().get("a_code1");
		
		a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
		this.getFormHM().put("a_code1", a_code);
		hm.remove("a_code");
		
		if(this.userView.getA0100()==null || this.userView.getA0100().length()<1)
			throw GeneralExceptionHandler.Handle(new GeneralException("","非自助用户不能使用此功能！","",""));
		TrainCourseBo tb = new TrainCourseBo(this.userView,this.frameconn);
		String searchstr = (String) this.getFormHM().get("searchstr");
		StringBuffer columns=new StringBuffer();
		StringBuffer strwhere=new StringBuffer();
		try {
			strwhere.append("select r.r5000 r5000,r5003,r5004,r5009,r5012,r5016,id,lesson_from from R50 r left join tr_selected_lesson t on t.r5000=r.r5000 and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"' where (((r5016=1 ");
			columns.append("r5000,r5003,r5004,r5009,r5012,r5016,id,lesson_from");

			if (!this.userView.isSuper_admin()) {
				
				String unit = tb.getUnitIdByBusi();//userView.getUserOrgId();//this.userView.getUnitIdByBusi("6");
				String []units = unit.split("`");
				String sql=" and (";
				if (units.length > 0 && unit.length() > 0) {
					for (int i = 0; i < units.length; i++) {
						String b0110s = units[i].substring(2);
						//sql+="r5020=" + Sql_switcher.substr("'"+b0110s+"'", "1", Sql_switcher.length("r5020"));
						//sql+=" or r5020 like '";
						sql+="r5020 like '";
						sql+=b0110s;
						sql+="%'";
						sql+=" or ";
					}
				}
				sql+=Sql_switcher.isnull("r5020", "'-1'");
				sql+="='-1'";
				if (Sql_switcher.searchDbServer() == 1) {
					sql+=" or r5020=''";
				}
				sql+=")";
	    		strwhere.append(sql);
			}

			strwhere.append(")");
			String tmp = "";
			if(!this.userView.isSuper_admin())
				tmp = getWhereCode(a_code);
			strwhere.append(tmp);
			strwhere.append(")");
			if(!this.userView.isSuper_admin())
				strwhere.append(" or r5014=1");//显示公开课
			strwhere.append(")");
			strwhere.append(" and r5022='04'");//已发布
			//strwhere.append(" and (select COUNT(r5100) from R51 where R51.R5000=r.R5000)>0");//课程下须有课件
			if (searchstr != null && searchstr.length()>0) {
				strwhere.append(" and r5003 like '%"+searchstr+"%'");
			}
			if(a_code.trim().length() > 0)
				strwhere.append(" and (R5004 like '" + a_code
						+ "%' or codeitemid='" + a_code + "')");
			Date date = new Date();
			SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
			String date1 = f1.format(date);
			strwhere.append(" and ((" + Sql_switcher.year("r5030") + "*10000+"
					+ Sql_switcher.month("r5030") + "*100+"
					+ Sql_switcher.day("r5030") + "<=" + date1);
			strwhere.append(" and " + Sql_switcher.year("r5031") + "*10000+"
					+ Sql_switcher.month("r5031") + "*100+"
					+ Sql_switcher.day("r5031") + ">=" + date1+")");
			strwhere.append(" or (r5030 is null and "+ Sql_switcher.year("r5031") + "*10000+"
					+ Sql_switcher.month("r5031") + "*100+"
					+ Sql_switcher.day("r5031") + ">=" + date1+")");
			strwhere.append(" or (" + Sql_switcher.year("r5030") + "*10000+"
					+ Sql_switcher.month("r5030") + "*100+"
					+ Sql_switcher.day("r5030") + "<=" + date1+" and r5031 is null)");
			strwhere.append(" or (r5030 is null and r5031 is null))");
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} 
		
		String top = getTop();
		ArrayList ls = this.hotLesson(top); 	//热门课程
		this.getFormHM().put("ls", ls);
		this.getFormHM().put("a_code", SafeCode.encode(PubFunc.encrypt(a_code)));
		this.getFormHM().put("strsql", strwhere.toString());
		this.getFormHM().put("columns", columns.toString());
		this.getFormHM().put("strwhere", "");
	}
	
	//得到
	private String getTop(){
		ConstantXml constantbo = new ConstantXml(this.frameconn,"TR_PARAM");
		String top = constantbo.getNodeAttributeValue("/param/hot_course", "top");
		return top;
	}
	
	//查找热门课程
	private ArrayList hotLesson(String top){
		ArrayList ls = new ArrayList();
		String id = "";
		String sql = "";
		if(IsNum(top)){			
			switch(Sql_switcher.searchDbServer()){
			case Constant.MSSQL:
				sql = "select top "+top+" count(*) as 'counts',r5000 from tr_selected_lesson where exists ( select r5000 from r50 where r50.r5000=tr_selected_lesson.r5000 and r5014 = '1' and r5016 = '1' and r5022 = '04') and lesson_from = 1 group by r5000 order by counts desc";
				break;
			case Constant.ORACEL:
				sql = "select  * from (select  count(*),r5000 from tr_selected_lesson where exists ( select r5000 from r50 where r50.r5000=tr_selected_lesson.r5000 and r5014 = '1' and r5016 = '1' and r5022 = '04') and lesson_from = 1 group by r5000 order by count(*) desc)  c where rownum<="+top;
				break;
			}
		
		//sql = "select top 5 count(*) as 'counts',r5000 from tr_selected_lesson  group by r5000 order by counts desc";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				id = this.frowset.getString("r5000");
				ls.add(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		return ls;
	}


	private String getWhereCode(String a_code){
		String tmpCodes="";
		//if(!userView.isSuper_admin()){
			TrainCourseBo tbo = new TrainCourseBo(this.userView);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select codeitemid,b0110 from codeitem where codesetid='55'");
			if(a_code.trim().length()>0){
				//sqlstr.append(" and (codeitemid like '"+a_code+"%')");
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				this.frowset = dao.search(sqlstr.toString());
				while(this.frowset.next()){
					String b0110=this.frowset.getString("b0110");
					if(tbo.isUserParent(b0110)!=-1){
						tmpCodes+=this.frowset.getString("codeitemid")+",";
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		//}
		if(tmpCodes!=null&&tmpCodes.length()>0){
			tmpCodes=tmpCodes.substring(0, tmpCodes.length()-1);
			if(a_code.trim().length()>0)
				tmpCodes=" and r5004 in ('"+tmpCodes.replaceAll(",", "','")+"')";
			else
				tmpCodes=" and (r5004 in ('"+tmpCodes.replaceAll(",", "','")+"') or "+Sql_switcher.isnull("r5004", "'-1'")+"='-1')";
		}else if(a_code.trim().length()>0){
			if(a_code.trim().length()>0)
				tmpCodes=" and "+Sql_switcher.isnull("r5004", "'-1'")+"<>'-1'";
		}else{
			tmpCodes=" and "+Sql_switcher.isnull("r5004", "'-1'")+"='-1'";
		}
		return tmpCodes;
	}
	
	//判断 是否为大于0的整数
	private boolean IsNum(String s){
		int num = 0 ; 
		try {
			num = Integer.parseInt(s);
			if(num > 0){				
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
