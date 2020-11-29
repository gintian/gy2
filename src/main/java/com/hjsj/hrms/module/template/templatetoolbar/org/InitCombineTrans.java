package com.hjsj.hrms.module.template.templatetoolbar.org;

import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateItem;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 项目名称 ：ehr7x
 * 类名称：InitCombineTrans
 * 类描述：机构合并初始化
 * 创建人： lis
 * 创建时间：Aug 17, 2016
 */
public class InitCombineTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			ArrayList transferorglist=new ArrayList();
			String infor_type =(String)this.getFormHM().get("infor_type");
			String table_name =(String)this.getFormHM().get("table_name");
			table_name = PubFunc.decrypt(SafeCode.decode(table_name));
			String tabid = (String)this.getFormHM().get("tabid");
			this.getFormHM().put("table_name",table_name);
			this.getFormHM().put("infor_type",infor_type);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
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
			transferorglist = getLazyDynaBeanToRecordVo(transferorglist);
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
			}
			this.getFormHM().put("codesetid", firstset);
			TemplateUtilBo bo=new TemplateUtilBo(this.getFrameconn(),this.userView);
			ArrayList templateSetList=bo.getAllTemplateItem(Integer.valueOf(tabid));
			boolean flag1=false;
			boolean flag2=false;
			for(int j=0;j<templateSetList.size();j++){
				TemplateItem templateItem = (TemplateItem)templateSetList.get(j);
				if("parentid_2".equalsIgnoreCase(templateItem.getFieldName()))
					flag1=true;
				
				if("codesetid_2".equalsIgnoreCase(templateItem.getFieldName()))
					flag2=true;
				
			}
			DbWizard dbwizard=new DbWizard(this.getFrameconn());
			String msg ="";
			if(!flag1||!dbwizard.isExistField(table_name, "parentid_2",false)){
				msg="模板中不存在变化后上级组织单元名称!\\n";
			}
			if(infor_type!=null&& "2".equals(infor_type)&&(!flag2||!dbwizard.isExistField(table_name, "codesetid_2",false))){
				msg+="模板中不存在变化后组织单元类型!";
			}
			this.getFormHM().put("msg", msg);
			ArrayList codeitemlist = new ArrayList();
			StringBuffer selectcodeitemids = new StringBuffer();
			for(int i=0;i<transferorglist.size();i++){
        		RecordVo vo=(RecordVo)transferorglist.get(i);
        		codeitemlist.add(new CommonData(vo.getString("codeitemid"), "(" + vo.getString("codeitemid")
        				                    							+ ")" + vo.getString("codeitemdesc")));
        		selectcodeitemids.append(vo.getString("codeitemid")+":"+vo.getString("codeitemdesc")+":"+vo.getString("codesetid")+"`");
			}
			if(selectcodeitemids.length()>1)
			this.getFormHM().put("selectcodeitemids", selectcodeitemids.substring(0, selectcodeitemids.length()-1));
			else
				this.getFormHM().put("selectcodeitemids", "");
			String code = firstparentid;
			if(!firstitemid.equalsIgnoreCase(firstparentid)){
				String first = "1";
				StringBuffer strsql = new StringBuffer();
				strsql
						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
				strsql.append(code);
				strsql.append("' and codeitemid<>parentid ");
	
				strsql.append(" order by codeitemid desc");
				
	
				this.frowset = dao.search(strsql.toString());
				while (this.frowset.next()) {
					first = "0";
					String chilecode = this.frowset.getString("codesetid");
					this.getFormHM().put("codesetid", chilecode);
					String codeitemid = this.frowset.getString("codeitemid");
					String corcode = this.frowset.getString("corcode");
					int grade = this.frowset.getInt("grade");
					if (chilecode != null) {
						codeitemlist.add(new CommonData("", ResourceFactory.getProperty("org.orginfo.neworg")));
						break;
					}
				}
			}else{
				StringBuffer strsql = new StringBuffer();
				strsql
						.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid=");
				strsql.append("codeitemid");
				strsql.append(" order by codeitemid desc");
				this.frowset = dao.search(strsql.toString());
				while (this.frowset.next()) {
					String chilecode = this.frowset.getString("codesetid");
					this.getFormHM().put("codesetid", chilecode);
					String codeitemid = this.frowset.getString("codeitemid");
					String corcode = this.frowset.getString("corcode");
					int grade = this.frowset.getInt("grade");
					if (chilecode != null) {
						codeitemlist.add(new CommonData("", ResourceFactory.getProperty("org.orginfo.neworg")));
						break;
					}
				}
			}

			String orgId = this.userView.getUnitIdByBusiOutofPriv("9");
			
			this.getFormHM().put("orgId", orgId);// 合并后待选代码
			this.getFormHM().put("codeitemlist", codeitemlist);// 合并后待选代码
			this.getFormHM().put("changemsg", "no");
			this.getFormHM().put("codesetid", firstset);
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
