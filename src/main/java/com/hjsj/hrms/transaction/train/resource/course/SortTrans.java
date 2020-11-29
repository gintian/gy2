package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SortTrans
 * </p>
 * <p>
 * Description:排序培训课程记录
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class SortTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String tablename = (String) hm.get("tablename");
		hm.remove("tablename");

		String a_code = (String) hm.get("a_code");
		a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
		a_code = PubFunc.decrypt(SafeCode.decode(a_code));

		StringBuffer buf = new StringBuffer();
		buf.append("select r5000,r5003,norder from "
				+ tablename + " where 1=1 ");
		
//		 判断登录用户为哪种类型的用户：用户管理的还是帐号分配里的
//		if(!this.userView.isSuper_admin())
//		{
//			int status = this.userView.getStatus();	
//		    if (status == 4)// 帐号分配里面的用户
//			{
//		    	if ("UN".equalsIgnoreCase(this.userView.getManagePrivCode())){
//		    		String manamgePrivCode = this.userView.getManagePrivCodeValue();
//		    		buf.append(" and (r5020 like '"+manamgePrivCode+"%' or r5020 = '' or r5020 is null)");
//		    		manamgePrivCode = getSupUnit(manamgePrivCode,1);
//		    		if(manamgePrivCode!=null&&manamgePrivCode.length()>0){
//		    			buf.append(manamgePrivCode);
//		    		}
//		    		buf.append(")");
//		    	}else{
//		    		buf.append(" and (r5020 = '' or r5020 is null)");
//		    	}
//			}
//			else if (status == 0)// 用户管理里面的用户
//			{
//				String code="";
//				code = this.userView.getUnit_id();
//				code = PubFunc.getTopOrgDept(code);
//				String unitarr[] = code.split("`"); 
//				String str="";
//				for(int i=0;i<unitarr.length;i++){
//					if(unitarr[i]!=null&&unitarr[i].trim().length()>2&&unitarr[i].substring(0, 2).equalsIgnoreCase("UN")){
//							String tmpb0110 = unitarr[i].substring(2);
//							str +="r5020 like '"+tmpb0110+"%' or ";
//							tmpb0110=getSupUnit(tmpb0110,0);//上级单位
//							if(tmpb0110!=null&&tmpb0110.length()>0)
//								str += tmpb0110;
//					}
//				}
//				if(str.length()>0){
//					buf.append(" and ("+str.substring(0, str.lastIndexOf("or")-1)+" or r5020 = '' or r5020 is null)");
//				}else{
//					if ("UN".equalsIgnoreCase(this.userView.getManagePrivCode())){
//						String manamgePrivCode = this.userView.getManagePrivCodeValue();
//			    		buf.append(" and (r5020 like '"+manamgePrivCode+"%' or r5020 = '' or r5020 is null");
//			    		manamgePrivCode = getSupUnit(manamgePrivCode,1);//上级
//			    		if(manamgePrivCode!=null&&manamgePrivCode.length()>0)
//			    			buf.append(manamgePrivCode);
//			    		buf.append(")");
//			    	}else{
//			    		buf.append(" and (r5020 = '' or r5020 is null)");
//			    	}
//				}
//			}
//		}
/*		if (!this.userView.isSuper_admin()) {
			String unit = this.userView.getUnitIdByBusi("6");
			String []units = unit.split("`");
			String sql=" and (";
			if (units.length > 0 && unit.length() > 0) {
				for (int i = 0; i < units.length; i++) {
					String b0110s = units[i].substring(2);
					sql+="r5020=" + Sql_switcher.substr("'"+b0110s+"'", "1", Sql_switcher.length("r5020"));
					sql+=" or r5020 like '";
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
			buf.append(sql);
		}
		//if (a_code != null && a_code.trim().length() > 0) {
			
			a_code = SafeCode.decode(a_code);
			//buf.append("and r5004='"+a_code+"'");
			String tmp = "";
			if(this.userView.isSuper_admin()){
				if(a_code.trim().length() > 0)
					tmp=" and R5004 like '"+a_code+"%'";
			}else
				tmp = getWhereCode(a_code);
			buf.append(tmp);
		//}

		buf.append(" order by norder,r5000");
		*/
		String bufstr = (String)this.getFormHM().get("sqlstr");
		ArrayList sortlist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			CommonData dataobj = null;
			this.frowset = dao.search(bufstr);//buf.toString());
			while (this.frowset.next()) {
				String norder = this.frowset.getString("norder");
				String courseName = this.frowset.getString("r5003");
				norder = norder != null && norder.trim().length() > 0 ? norder
						: "0";
				dataobj = new CommonData(this.frowset.getString("r5000") + "::"
						+ norder, courseName);
				sortlist.add(dataobj);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("sortlist", sortlist);
			this.getFormHM().put("tablename", tablename);
		}
	}
	

	private String getWhereCode(String a_code){
		String tmpCodes="";
		if(!userView.isSuper_admin()){
			TrainCourseBo tbo = new TrainCourseBo(this.userView);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select codeitemid,b0110 from codeitem where codesetid='55'");
			if(a_code.trim().length()>0){
				sqlstr.append(" and (codeitemid like '"+a_code+"%')");
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(tmpCodes!=null&&tmpCodes.length()>0){
			tmpCodes=tmpCodes.substring(0, tmpCodes.length()-1);
			if(a_code.trim().length()>0)
				tmpCodes=" and r5004 in ('"+tmpCodes.replaceAll(",", "','")+"')";
			else
				tmpCodes=" and r5004 in ('','"+tmpCodes.replaceAll(",", "','")+"')";
		}else if(a_code.trim().length()>0){
			if(a_code.trim().length()>0)
				tmpCodes=" and r5004<>''";
		}else{
			tmpCodes=" and r5004=''";
		}
		return tmpCodes;
	}


	private String getSupUnit(String b0110,int i){
		boolean flag=true;
		String supunit=b0110;
		StringBuffer sbf = new StringBuffer();
		do{
			flag=false;
			String sql = "select codeitemid from organization where codesetid='UN' and codeitemid=(select parentid from organization where codesetid='UN' and codeitemid<>parentid and codeitemid='"+supunit+"')";
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					supunit=this.frowset.getString("codeitemid");
					if(supunit!=null&&supunit.length()>0){
						flag=true;
						if(i==1)
							sbf.append(" or r5020='"+supunit+"'");
						else
							sbf.append("r5020='"+supunit+"' or ");
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(flag);
		return sbf.toString();
	}
}
