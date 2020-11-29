package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训班</p>
 * <p>Description:培训班排序</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class SortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String model = (String)hm.get("model");
		String sortype=(String)hm.get("sortype");
		sortype=sortype!=null&&sortype.trim().length()>0?sortype:"2";
		model=model!=null&&model.trim().length()>0?model:"1";
		hm.remove("model");
		hm.remove("sortype");
		
		String fieldsetid = (String)hm.get("fieldsetid");
		fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"1";
		hm.remove("fieldsetid");
		
		this.getFormHM().put("tablename",fieldsetid);
		
		TrainCourseBo bo=  new TrainCourseBo(this.userView);
		String a_code=bo.getUnitIdByBusi();
//		if(!this.userView.isSuper_admin()){
//			if(this.userView.getStatus()==4){
//				a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
//			}else if(this.userView.getStatus()==0){
//				a_code = this.userView.getUnit_id();
//				a_code = PubFunc.getTopOrgDept(a_code);
//				if(a_code==null||a_code.length()<3){
//					a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
//				}
//			}
//		}else
//			a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		
		String itemid = "";
		String itemdesc = "";
		String spflag = "";
		if("r31".equalsIgnoreCase(fieldsetid)){
			itemid="R3101";
			itemdesc="R3130";
			spflag="r3127";
		}else if("r25".equalsIgnoreCase(fieldsetid)){
			itemid="R2501";
			itemdesc="R2502";
			spflag="r2509";
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("select "+itemid+","+itemdesc+",I9999 from "+fieldsetid+" where 1=1 ");
		if("2".equals(model)){
			buf.append(" and "+spflag+" in(");
			if("r25".equalsIgnoreCase(fieldsetid) || "R25".equalsIgnoreCase(fieldsetid))
				buf.append("02,03,04,09,06");
			else
				buf.append("02,03,04,06,07");
			buf.append(")");
		}
		if("1".equals(model)){
			buf.append(" and "+spflag+" in(");
			
			if("r25".equalsIgnoreCase(fieldsetid) || "R25".equalsIgnoreCase(fieldsetid))
				buf.append("'01','02','03','04','06','07','09'");
			else{
				if("1".equalsIgnoreCase(sortype)){
					buf.append("'01','02','03','04','06','07'");
				}
				else
					buf.append("'01','02','03','04','06','07','09'");
				}
			
			buf.append(")");
		}
		
		if(a_code!=null&&a_code.trim().length()>2){
			String unitarr[] = a_code.split("`"); 
			String temp="";
			for (int i = 0; i < unitarr.length; i++) {
				if(unitarr[i]!=null&&unitarr[i].length()>2){
					if(unitarr[i].startsWith("UN")){
						temp+=" B0110 like '"+unitarr[i].substring(2)+"%' or";
					}else if(unitarr[i].startsWith("UM")){
						temp+=" E0122 like '"+unitarr[i].substring(2)+"%' or";
					}
				}
			}
			if(temp!=null&&temp.length()>0){
				buf.append(" and ("+temp.substring(0, temp.lastIndexOf("or")-1)+")");
			}
//			if(this.userView.getManagePrivCode().equalsIgnoreCase("UN"))
//				buf.append(" and B0110 like '"+this.userView.getManagePrivCodeValue()+"%'");
//			if(this.userView.getManagePrivCode().equalsIgnoreCase("UM"))
//				buf.append(" and E0122 like '"+this.userView.getManagePrivCodeValue()+"%'");
		}
		if(!this.getUserView().isSuper_admin()){
			if(a_code.trim().length()<1)
				buf.append(" where 1=2 ");	
		}
		buf.append(" order by I9999");
		ArrayList  sortlist = new ArrayList();
		ContentDAO dao  = new ContentDAO(this.getFrameconn());
		try {
			CommonData dataobj = null;
			this.frowset = dao.search(buf.toString());
			while(this.frowset.next()){
				String I9999 = this.frowset.getString("I9999");
				String desc = this.frowset.getString(itemdesc);
				desc = desc==null||desc.length()<1?"":desc;
				desc=desc.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
				I9999=I9999!=null&&I9999.trim().length()>0?I9999:"0";
				dataobj = new CommonData(this.frowset.getString(itemid)+"::"+I9999,desc); 
				sortlist.add(dataobj);
			}
			this.getFormHM().put("sortlist",sortlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
