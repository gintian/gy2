package com.hjsj.hrms.transaction.general.inform.emp.output;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class PrintRosterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap)this.getFormHM();
		String	dbname = (String)reqhm.get("dbname");
			dbname=dbname!=null&&dbname.trim().length()>0?dbname:"Usr";
			reqhm.remove("dbname");
			this.getFormHM().put("dbname",dbname);
			
		String	a_code = (String)reqhm.get("a_code");
			a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
			a_code= "all".equalsIgnoreCase(a_code)?"":a_code;
			reqhm.remove("a_code");
			this.getFormHM().put("a_code",a_code);
			
		String	inforkind = (String)reqhm.get("infor");
			inforkind=inforkind!=null&&inforkind.trim().length()>0?inforkind:"1";
			reqhm.remove("infor");
			this.getFormHM().put("inforkind",inforkind);
			
		String	result = (String)reqhm.get("flag");
			result=result!=null&&result.trim().length()>0?result:"0";
			reqhm.remove("flag");
			this.getFormHM().put("result",result);
		ContentDAO dao = new ContentDAO(this.frameconn);
		if(!"2".equals(result)){
			updateResult(dao,dbname,a_code,result,inforkind);
		}
	}
	private void updateResult(ContentDAO dao,String dbname,String a_code,String result,String infor){
		String tablename="";
		String itemid="";
		if("1".equals(infor)){
			tablename=dbname+"A01";
			itemid="A0100";
		}else if("2".equals(infor)){
			tablename="B01";
			dbname="b";
			itemid="B0110";
		}else if("3".equals(infor)){
			tablename="K01";
			itemid="E01A1";
			dbname="k";
		}else{
			tablename=dbname+"A01";
			itemid="A0100";
		}
		
		
		StringBuffer sqlstr = new StringBuffer("select "+itemid+" from ");
		sqlstr.append(tablename+" where ");
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);
		String cond=gzbo.whereStra_Code(a_code,infor);
		String code="";
		if(a_code!=null&&a_code.length()>0)
			code=a_code.substring(2);
		/*****sunxin 打印输出登记表，当前显示，结果兼职人员没有出不，不对 0018018******/
		InfoUtils infoUtils=new InfoUtils();
		cond+=infoUtils.getPartwhere(dbname, code, this.getFrameconn(),this.userView,result);
		if(cond!=null&&cond.length()>0)
			cond="("+cond+")";// 需要加括号: (当前节点 或 兼职) 且 在查询结果中
		sqlstr.append(cond);
		if("1".equals(result)){
			sqlstr.append(" and "+itemid+" in (select "+itemid+" from ");
			sqlstr.append(this.getUserView().getUserName()+dbname+"result)");
		}
		try {
			ArrayList recodlist = new ArrayList();
			this.frowset=dao.search(sqlstr.toString());
			while(this.frowset.next()){
				ArrayList list = new ArrayList();
				list.add(this.frowset.getString(itemid));
				recodlist.add(list);
			}
			dao.update("delete from "+this.getUserView().getUserName()+dbname+"result");
			String addsql = "insert into "+this.getUserView().getUserName()+dbname+"result("+itemid+") values(?)";
			dao.batchInsert(addsql,recodlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
