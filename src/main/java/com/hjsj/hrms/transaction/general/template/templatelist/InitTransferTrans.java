/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * 
 */
public class InitTransferTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			HashMap ahm=(HashMap)this.getFormHM().get("requestPamaHM");
			String infor_type =(String)ahm.get("infor_type");
			String table_name =(String)ahm.get("table_name");
			table_name = SafeCode.decode(table_name);
			ArrayList codesetidlist = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.getFormHM().put("table_name",table_name);
			this.getFormHM().put("infor_type",infor_type);
			this.getFormHM().put("tarorgname", "");
			//合并新代码，上级组织单元名称（parentid必填）
//			ArrayList combinefieldlist= (ArrayList)this.getFormHM().get("combinefieldlist");
//			ArrayList newcomlist = new ArrayList();
//			newcomlist =(ArrayList)combinefieldlist.clone();
//			for(int i=0;i<combinefieldlist.size();i++){
//				FieldItem item = (FieldItem)combinefieldlist.get(i);
//				if(item!=null&&item.getItemid()!=null&&item.getItemid().equalsIgnoreCase("start_date_2")){
//					newcomlist.remove(combinefieldlist.get(i));
//					if(item.isFillable())
//						this.getFormHM().put("datefillable", "1");
//					else
//						this.getFormHM().put("datefillable", "0");
//				}
//				
//			}
			
			ArrayList codeitemlist = new ArrayList();
			ArrayList transferorglist=new ArrayList();
			this.frecset=dao.search(" select * from " +table_name);
			while(this.frecset.next()){
				if(this.frecset.getString("submitflag")!=null&& "1".equals(this.frecset.getString("submitflag").trim())){
				if(infor_type!=null&& "2".equals(infor_type)){
					transferorglist.add(this.frecset.getString("b0110"));
				}
					else{
						transferorglist.add(this.frecset.getString("e01a1"));
					}
				}
			}
			StringBuffer selectcodeitemids = new StringBuffer();
			transferorglist = getLazyDynaBeanToRecordVo(transferorglist);
			if (transferorglist == null || transferorglist.size() == 0)
				return;
			for(int i=0;i<transferorglist.size();i++){
        		RecordVo vo=(RecordVo)transferorglist.get(i);
        		codeitemlist.add(new CommonData(vo.getString("codeitemid"), "(" + vo.getString("codeitemid")
        				                    							+ ")" + vo.getString("codeitemdesc")));
        		selectcodeitemids.append(vo.getString("codeitemid")+":"+vo.getString("codeitemdesc")+":"+vo.getString("codesetid")+"`");
			}
			
			String firstitemid="";
			String firstset = "";
			String firstparentid="";
			String firstgrade="";
			for (int i = 0; i < transferorglist.size(); i++) {
				RecordVo vo = (RecordVo) transferorglist.get(i);
				if (i == 0){
					firstset = vo.getString("codesetid");
					firstparentid=vo.getString("parentid");
					firstgrade=vo.getString("grade");
					firstitemid=vo.getString("codeitemid");
				}
//				if (!firstset.equals(vo.getString("codesetid"))||(!firstparentid.equalsIgnoreCase(vo.getString("parentid"))&&!firstitemid.equalsIgnoreCase(firstparentid)&&!vo.getString("codeitemid").equalsIgnoreCase(vo.getString("parentid")))||!firstgrade.equalsIgnoreCase(vo.getString("grade")))
//					throw GeneralExceptionHandler.Handle(new GeneralException("",
//							ResourceFactory.getProperty("label.org.nocombineorg1"),
//							"", ""));
			}
			this.getFormHM().put("selectcodeitemids", selectcodeitemids.substring(0, selectcodeitemids.length()-1));
//			String code = (String) this.getFormHM().get("codeid");
//			if(!firstitemid.equalsIgnoreCase(firstparentid)){
//				if(code.length()<=2){
//					code=firstparentid;
//				}else{
//					String value=code.substring(2);
//					if(code.substring(0,2).equalsIgnoreCase("UN"))
//					{
//						code=value;
//					}else if(code.substring(0,2).equalsIgnoreCase("@K"))
//					{
//						code=value;
//					}
//				}
//				String first = "1";
//				StringBuffer strsql = new StringBuffer();
//				strsql
//						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
//				strsql.append(code);
//				strsql.append("' and codeitemid<>parentid ");
//	
//				strsql.append(" order by codeitemid desc");
//				
//	
//				this.frowset = dao.search(strsql.toString());
//				while (this.frowset.next()) {
//					first = "0";
//					//this.getFormHM().put("first", first);
//					String chilecode = this.frowset.getString("codesetid");
//					this.getFormHM().put("codesetid", chilecode);
//					String codeitemid = this.frowset.getString("codeitemid");
//					String corcode = this.frowset.getString("corcode");
//					int grade = this.frowset.getInt("grade");
//					//this.getFormHM().put("grade", String.valueOf(grade));
//					if (chilecode != null) {
//						AddOrgInfo addOrgInfo = new AddOrgInfo();
//						codeitemid = addOrgInfo.GetNext(codeitemid, code);
//						if (corcode != null && corcode.length() > 0)
//							corcode = addOrgInfo.GetNext(corcode, code);
//					//	codeitemlist.add(new CommonData("", ResourceFactory.getProperty("org.orginfo.neworg")));
//					//	this.getFormHM().put("corcode", corcode);
//						break;
//					}
//				}
//			}else{
//				StringBuffer strsql = new StringBuffer();
//				strsql
//						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid=");
//				strsql.append("codeitemid");
//				strsql.append(" order by codeitemid desc");
//				this.frowset = dao.search(strsql.toString());
//				while (this.frowset.next()) {
//				//	this.getFormHM().put("first", "0");
//					String chilecode = this.frowset.getString("codesetid");
//					this.getFormHM().put("codesetid", chilecode);
//					String codeitemid = this.frowset.getString("codeitemid");
//					String corcode = this.frowset.getString("corcode");
//					int grade = this.frowset.getInt("grade");
//				//	this.getFormHM().put("grade", String.valueOf(grade));
//					if (chilecode != null) {
//						AddOrgInfo addOrgInfo = new AddOrgInfo();
//						codeitemid = addOrgInfo.GetNext(codeitemid, code);
//						if (corcode != null && corcode.length() > 0)
//							corcode = addOrgInfo.GetNext(corcode, code);
//					//	codeitemlist.add(new CommonData(code+codeitemid, "(" + code
//					//			+ codeitemid + ")"+ResourceFactory.getProperty("org.orginfo.neworg")));
//					//	this.getFormHM().put("corcode", corcode);
//						break;
//					}
//				}
//			}
//
//			this.getFormHM().put("codeitemlist", codeitemlist);// 合并后待选代码
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private ArrayList getLazyDynaBeanToRecordVo(ArrayList transferorglist) throws GeneralException{
		ArrayList list = new ArrayList();
	
		for(int i=0;i<transferorglist.size();i++)
	    {
	   	    String codeitemid=(String)transferorglist.get(i);
	   	    StringBuffer strsql = new StringBuffer();//合并单位：存在上下级单位时，不能合并到下级单位。（解决出现断树状态）
	   		for(int j=0;j<transferorglist.size();j++)
		    {
	   			if(i==j){
	   				
	   			}else{
	   			 String codeitemid2=(String)transferorglist.get(j);
	   			 strsql.append(" and codeitemid not like '"+codeitemid2+"%' ");
	   			}
		   	    
		    }
			StringBuffer sql=new StringBuffer();   
			String table="organization";
			sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from "+table+"");
			sql.append(" where codeitemid='"+codeitemid+"' "+strsql+"");
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
				//	throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构不许合并，操作失败！","",""));
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
