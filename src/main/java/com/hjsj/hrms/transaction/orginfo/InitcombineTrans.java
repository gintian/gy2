/**
 * 
 */
package com.hjsj.hrms.transaction.orginfo;

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
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
			ArrayList transferorglist=(ArrayList)this.getFormHM().get("selectedinfolist");
			transferorglist = getLazyDynaBeanToRecordVo(transferorglist);
			if (transferorglist == null || transferorglist.size() == 0)
				return;
			String firstset = "";
			String firstparentid="";
			String firstgrade="";
			String firstitemid="";
			for (int i = 0; i < transferorglist.size(); i++) {
				RecordVo vo = (RecordVo) transferorglist.get(i);
				if (i == 0){
					firstset = vo.getString("codesetid");
					firstparentid=vo.getString("parentid");
					firstgrade=vo.getString("grade");
					firstitemid=vo.getString("codeitemid");
				}
				if (!firstset.equals(vo.getString("codesetid"))||(!firstparentid.equalsIgnoreCase(vo.getString("parentid"))&&!firstitemid.equalsIgnoreCase(firstparentid)&&!vo.getString("codeitemid").equalsIgnoreCase(vo.getString("parentid")))||!firstgrade.equalsIgnoreCase(vo.getString("grade")))
					throw GeneralExceptionHandler.Handle(new GeneralException("",
							ResourceFactory.getProperty("label.org.nocombineorg1"),
							"", ""));
			}
			this.getFormHM().put("codesetid", firstset);
			ArrayList codeitemlist = new ArrayList();
			StringBuffer selectcodeitemids = new StringBuffer();
			for(int i=0;i<transferorglist.size();i++){
        		RecordVo vo=(RecordVo)transferorglist.get(i);
        		codeitemlist.add(new CommonData(vo.getString("codeitemid"), "(" + vo.getString("codeitemid")
        				                    							+ ")" + vo.getString("codeitemdesc")));
        		selectcodeitemids.append(vo.getString("codeitemid")+":"+vo.getString("codeitemdesc")+":"+vo.getString("codesetid")+"`");
			}
			this.getFormHM().put("selectcodeitemids", selectcodeitemids.substring(0, selectcodeitemids.length()-1));
			String code = (String) this.getFormHM().get("code");
			if(!firstitemid.equalsIgnoreCase(firstparentid)){
				/*if(code.length()==0)
					code=firstparentid;*/
				String first = "1";
				StringBuffer strsql = new StringBuffer();
				strsql
						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
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
			String UNIT_HISTORY_SET = SystemConfig
					.getPropertyValue("UNIT_HISTORY_SET");
			if (UNIT_HISTORY_SET != null
					&& UNIT_HISTORY_SET.trim().length() > 1&&DataDictionary.getFieldSetVo(UNIT_HISTORY_SET)!=null) {
				ArrayList childfielditemlist = DataDictionary
						.getFieldList(UNIT_HISTORY_SET.toUpperCase(),
								Constant.USED_FIELD_SET);
				childfielditemlist = childfielditemlist!=null?childfielditemlist:new ArrayList();
				this.getFormHM().put("childfielditemlist", childfielditemlist);
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

	private ArrayList getLazyDynaBeanToRecordVo(ArrayList transferorglist) throws GeneralException{
		ArrayList list = new ArrayList();
		for(int i=0;i<transferorglist.size();i++)
	    {
			LazyDynaBean rec=(LazyDynaBean)transferorglist.get(i); 
	   	    String codeitemid=rec.get("code").toString();
			StringBuffer sql=new StringBuffer();   
			String table="organization";
			sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from "+table+"");
			sql.append(" where codeitemid='"+codeitemid+"'");
			RowSet rs=null;
			ContentDAO dao=new ContentDAO(this.frameconn);
			RecordVo vo = null;
			try {
				rs=dao.search(sql.toString());
				if(rs.next())
				{
					vo=new RecordVo("organization");
					vo.setString("codesetid",rs.getString("codesetid"));
					vo.setString("codeitemdesc",rs.getString("codeitemdesc"));
					vo.setString("parentid",rs.getString("parentid"));
					vo.setString("childid",rs.getString("childid"));
					vo.setString("codeitemid",rs.getString("codeitemid"));
					vo.setInt("grade", rs.getInt("grade"));
				}else{
					throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构不许合并，操作失败！","",""));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	    if(vo==null)
	   	    	continue;
	   	    list.add(vo);
	    }
		return list;
	}
}
