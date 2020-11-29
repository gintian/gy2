package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 
 * 
 * Title:SearchTrainPlanTrans.java
 * Description:
 * Company:hjsj
 * Create time:May 14, 2014:4:51:32 PM
 * @author zhaogd
 * @version 6.x
 */
public class SearchTrainPlanTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
			//对应计划
			String classId=(String)this.getFormHM().get("classId");
			classId = classId==null?"":classId;
			String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
			name = name==null?"":name;
			String xmlsql = getSqlByClassId(classId, name);
			String itemdesc = "";
			String itemid = "";
			ArrayList desclist = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());	
			this.frowset = dao.search(xmlsql);
			while(this.frowset.next()){
				CommonData objvo = new CommonData();
				itemdesc = (String)this.frowset.getString("codeitemdesc");
				itemid = (String)this.frowset.getString("codeitemid");
				objvo.setDataName(itemdesc);
				objvo.setDataValue(itemid);
				desclist.add(objvo);
			}
			this.getFormHM().put("namelist", desclist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getSqlByClassId(String classId, String name) {
		String b0110 = "";
		StringBuffer sql=new StringBuffer();
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(classId!=null&&!"".equals(classId)){
				this.frowset = dao.search("select b0110 from r31 where r3101="+classId);
				if(this.frowset.next()){
					b0110 = this.frowset.getString("b0110");
				}
			}
			sql.append("select R2501 as codeitemid,R2502 as codeitemdesc from R25 where R2509 in ('03','04')");
			if(b0110 == null || "".equals(b0110)){
				ArrayList list=new ArrayList();
				if(this.userView!=null&&!this.userView.isSuper_admin()){
					TrainCourseBo bo = new TrainCourseBo(this.userView);
					String a_code = bo.getUnitIdByBusi();
					if(a_code!=null&&a_code.length()>3){
						String[] strS=StringUtils.split(a_code,"`");
						for(int i=0;i<strS.length;i++){
							String code=strS[i];
							if("UN".equalsIgnoreCase(code.substring(0,2))){
								list.add("b0110 like '"+code.substring(2)+"%'");
							}else if("UM".equalsIgnoreCase(code.substring(0,2))){
								list.add("e0122 like '"+code.substring(2)+"%'");
							}
						}
					}
				}
				
				if(list.size()>0){
					sql.append(" and (");
					for(int i=0;i<list.size();i++){
						sql.append(" "+list.get(i)+" or ");
					}
					sql.setLength(sql.length()-3);
					sql.append(")");
				}
			} else {
				sql.append(" and b0110='"+b0110+"'");
			}
			
			if(name!=null&&name.length()>0){
				sql.append(" and R2502 like '%" + name + "%'");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql.toString();
	}

}
