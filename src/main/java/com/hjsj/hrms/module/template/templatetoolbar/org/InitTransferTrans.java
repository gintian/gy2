package com.hjsj.hrms.module.template.templatetoolbar.org;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目名称 ：ehr7x
 * 类名称：InitTransferTrans
 * 类描述：划转初始化
 * 创建人： lis
 * 创建时间：Aug 17, 2016
 */
public class InitTransferTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		try {
			HashMap ahm=(HashMap)this.getFormHM();
			String infor_type =(String)ahm.get("infor_type");
			String table_name =(String)ahm.get("table_name");
			table_name = PubFunc.decrypt(SafeCode.decode(table_name));
			ArrayList codesetidlist = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			ArrayList codeitemlist = new ArrayList();
			ArrayList transferorglist=new ArrayList();
			this.frecset=dao.search(" select * from " +table_name);
			while(this.frecset.next()){
				if(this.frecset.getString("submitflag")!=null&& "1".equals(this.frecset.getString("submitflag").trim())){
				if(infor_type!=null&& "2".equals(infor_type)){
					transferorglist.add(this.frecset.getString("b0110"));
				}else{
					transferorglist.add(this.frecset.getString("e01a1"));
				}
				}
			}
			StringBuffer selectcodeitemids = new StringBuffer();
			transferorglist = getLazyDynaBeanToRecordVo(transferorglist);
			if (transferorglist == null || transferorglist.size() == 0)
				return;
			/*for(int i=0;i<transferorglist.size();i++){
        		RecordVo vo=(RecordVo)transferorglist.get(i);
        		codeitemlist.add(new CommonData(vo.getString("codeitemid"), "(" + vo.getString("codeitemid")
        				                    							+ ")" + vo.getString("codeitemdesc")));
        		selectcodeitemids.append(vo.getString("codeitemid")+":"+vo.getString("codeitemdesc")+":"+vo.getString("codesetid")+"`");
			}*/
			
			/*String firstitemid="";
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
			}*/
			//this.getFormHM().put("selectcodeitemids", selectcodeitemids.substring(0, selectcodeitemids.length()-1));
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
