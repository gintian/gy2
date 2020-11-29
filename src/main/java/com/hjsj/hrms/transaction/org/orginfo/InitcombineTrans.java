/**
 * 
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * 
 */
public class InitcombineTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//			String selectcodeitems = (String) hm.get("selectcodeitemids");
//			selectcodeitems = new String(selectcodeitems.getBytes("ISO8859-1"),
//					"gb2312");
//			String selects[] = selectcodeitems.split("`");
//			ArrayList codeitemlist = new ArrayList();
//			for (int i = 0; i < selects.length; i++) {
//				String item = selects[i];
//				String[] items = item.split(":");
//				if (items.length == 3) {
//					codeitemlist.add(new CommonData(items[0], "(" + items[0]
//							+ ")" + items[1]));
//				}
//			}
			ArrayList delorglist=(ArrayList)this.getFormHM().get("selectedlist");
			if (delorglist == null || delorglist.size() == 0)
				return;
			String firstset = "";
			String firstparentid="";
			String firstitemid="";
			for (int i = 0; i < delorglist.size(); i++) {
				RecordVo vo = (RecordVo) delorglist.get(i);
				if (i == 0){
					firstset = vo.getString("codesetid");
					firstparentid=vo.getString("parentid");
					firstitemid=vo.getString("codeitemid");
				}
				if (!firstset.equals(vo.getString("codesetid")))
					throw GeneralExceptionHandler.Handle(new GeneralException("",
							ResourceFactory.getProperty("label.org.nocombineorg"),
							"", ""));
			}
			ArrayList codeitemlist = new ArrayList();
			for(int i=0;i<delorglist.size();i++){
        		RecordVo vo=(RecordVo)delorglist.get(i);
        		codeitemlist.add(new CommonData(vo.getString("codeitemid"), "(" + vo.getString("codeitemid")
       		                          							+ ")" + vo.getString("codeitemdesc")));
			}
			String code = (String) this.getFormHM().get("code");
			if(!firstitemid.equalsIgnoreCase(firstparentid)){
				
			
				String first = "1";
				StringBuffer strsql = new StringBuffer();
				strsql
						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
				strsql.append(code);
				strsql.append("' and codeitemid<>parentid ");
				
				strsql.append("union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'vorg' as flag,corcode from vorganization where parentid='");
				strsql.append(code);
				strsql.append("' and codeitemid<>parentid ");
		
				strsql.append(" order by codeitemid desc");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
	
				this.frowset = dao.search(strsql.toString());
				while (this.frowset.next()) {
					first = "0";
					this.getFormHM().put("first", first);
					String chilecode = this.frowset.getString("codesetid");
					this.getFormHM().put("codesetid", chilecode);
					String codeitemid = this.frowset.getString("codeitemid");
					String corcode = this.frowset.getString("corcode");
					int grade = this.frowset.getInt("grade");
					this.getFormHM().put("grade", String.valueOf(grade));
					if (chilecode != null) {
						AddOrgInfo addOrgInfo = new AddOrgInfo();
						codeitemid = addOrgInfo.GetNext(codeitemid, code);
						if (corcode != null && corcode.length() > 0)
							corcode = addOrgInfo.GetNext(corcode, code);
						codeitemlist.add(new CommonData(code+codeitemid, "(" + code
								+ codeitemid + ")"+ResourceFactory.getProperty("org.orginfo.neworg")));
						this.getFormHM().put("corcode", corcode);
						break;
					}
				}
			}else{
				StringBuffer strsql = new StringBuffer();
				strsql
						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid=");
				strsql.append("codeitemid");
				strsql.append(" order by codeitemid desc");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset = dao.search(strsql.toString());
				while (this.frowset.next()) {
					this.getFormHM().put("first", "0");
					String chilecode = this.frowset.getString("codesetid");
					this.getFormHM().put("codesetid", chilecode);
					String codeitemid = this.frowset.getString("codeitemid");
					String corcode = this.frowset.getString("corcode");
					int grade = this.frowset.getInt("grade");
					this.getFormHM().put("grade", String.valueOf(grade));
					if (chilecode != null) {
						AddOrgInfo addOrgInfo = new AddOrgInfo();
						codeitemid = addOrgInfo.GetNext(codeitemid, code);
						if (corcode != null && corcode.length() > 0)
							corcode = addOrgInfo.GetNext(corcode, code);
						codeitemlist.add(new CommonData(code+codeitemid, "(" + code
								+ codeitemid + ")"+ResourceFactory.getProperty("org.orginfo.neworg")));
						this.getFormHM().put("corcode", corcode);
						break;
					}
				}
			}
			this.getFormHM().put("codeitemlist", codeitemlist);// 合并后待选代码
			String value = "UNIT_HISTORY_SET";
			RecordVo vo=(RecordVo)delorglist.get(0);
			if("UN".equalsIgnoreCase(vo.getString("codesetid")) || "UM".equalsIgnoreCase(vo.getString("codesetid")))
				value = "UNIT_HISTORY_SET";
			else if("@K".equalsIgnoreCase(vo.getString("codesetid")))
				value = "POST_HISTORY_SET";
			String HISTORY_SET = SystemConfig
			.getPropertyValue(value);
			if (HISTORY_SET != null
					&& HISTORY_SET.trim().length() > 1&&DataDictionary.getFieldSetVo(HISTORY_SET)!=null) {
				ArrayList childfielditemlist = DataDictionary
						.getFieldList(HISTORY_SET.toUpperCase(),
								Constant.USED_FIELD_SET);
				childfielditemlist = childfielditemlist!=null?childfielditemlist:new ArrayList();
				this.getFormHM().put("childfielditemlist", childfielditemlist);
				this.getFormHM().put("HISTORY_SET", HISTORY_SET);
				this.getFormHM().put("changemsg", "yes");
			} else {
				this.getFormHM().put("changemsg", "no");
				this.getFormHM().put("childfielditemlist", new ArrayList());
			}
			this.getFormHM().put("codesetid", firstset);
			String posfillable="0",unitfillable="0";
			RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
			if(pos_code_field_constant_vo!=null)
			{
			  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
			  if(pos_code_field!=null&&pos_code_field.length()>1){
				  FieldItem item = DataDictionary.getFieldItem(pos_code_field);
				  if(item!=null){
					  if(item.isFillable()){
						  posfillable="1";
					  }
				  }
			  }
			}
			RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
			if(unit_code_field_constant_vo!=null)
			{
			  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
			  if(unit_code_field!=null&&unit_code_field.length()>1){
				  FieldItem item = DataDictionary.getFieldItem(unit_code_field);
				  if(item!=null){
					  if(item.isFillable()){
						  unitfillable="1";
					  }
				  }
			  }
			}
			this.getFormHM().put("posfillable", posfillable);
			this.getFormHM().put("unitfillable", unitfillable);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
