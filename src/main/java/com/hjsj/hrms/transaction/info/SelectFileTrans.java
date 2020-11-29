package com.hjsj.hrms.transaction.info;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 计算单位、部门、职位，减轻分析类的运行时间
 * @author xujian
 *Apr 27, 2010
 */
public class SelectFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String code=(String)this.getFormHM().get("code");
		code=code!=null?code:"";
		String kind=(String)this.getFormHM().get("kind");
		String codesetid="";
		String codeitemid=code;
		if("0".equals(kind)){
			codesetid="@K";
		}else if("1".equals(kind)){
			codesetid="UM";
		}else if("2".equals(kind)){
			codesetid="UN";
		}
		if("".equals(code)){
			codesetid=this.userView.getManagePrivCode();
			codeitemid=this.userView.getManagePrivCodeValue();
		}
		if("".equals(codesetid)||"".equals(codeitemid)){
			this.getFormHM().put("bb0110", "");
			this.getFormHM().put("be0122", "");
			this.getFormHM().put("be01a1", "");
			this.getFormHM().put("codeOfB0110", "");
			this.getFormHM().put("codeOfE0122", "");
			this.getFormHM().put("codeOfE01a1", "");
		}else{
			try {
				getOrg(codesetid,codeitemid,new ContentDAO(this.frameconn));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void getOrg(String codesetid,String codeitemid,ContentDAO dao) throws Exception{
		String b0110="",e0122="",e01a1="",codeOfB0110="",codeOfE0122="",codeOfE01a1="";
		StringBuffer strsql=new StringBuffer();
		String pre=codesetid;
		String code=codeitemid;
		String codeitemdesc="";
		int i=0;//防止死循环
		if("UN".equals(codesetid)){
			strsql.delete(0,strsql.length());
			strsql.append("select codesetid,parentid,codeitemdesc from organization");
			strsql.append(" where codeitemid='");
			strsql.append(code);
			strsql.append("'");					
			this.frowset =dao.search(strsql.toString());	//执行当前查询的sql语句	
			if(this.frowset.next())
			{
				codeitemdesc=this.frowset.getString("codeitemdesc");
			}
			b0110=codeitemdesc;
			codeOfB0110=code;
		}else if("UM".equals(codesetid)){
			codeOfE0122=code;
			strsql.delete(0,strsql.length());
			strsql.append("select codesetid,parentid,codeitemdesc from organization");
			strsql.append(" where codeitemid='");
			strsql.append(code);
			strsql.append("'");	
			this.frowset =dao.search(strsql.toString());	//执行当前查询的sql语句	
			if(this.frowset.next())
			{
				codeitemdesc=this.frowset.getString("codeitemdesc");
				pre=this.frowset.getString("codesetid");
				code=this.frowset.getString("parentid");
			}
			e0122=codeitemdesc;
			while(!"UN".equalsIgnoreCase(pre)&&i<10)
			{
				codeOfB0110=code;
				strsql.delete(0,strsql.length());
				strsql.append("select codesetid,parentid,codeitemdesc from organization");
				strsql.append(" where codeitemid='");
				strsql.append(code);
				strsql.append("'");					
				this.frowset =dao.search(strsql.toString());	//执行当前查询的sql语句	
				if(this.frowset.next())
				{
					codeitemid=code;
					codeitemdesc=this.frowset.getString("codeitemdesc");
					pre=this.frowset.getString("codesetid");
					code=this.frowset.getString("parentid");
				}			
			}
			b0110=codeitemdesc;
		}else if("@K".equals(codesetid)){
			
			codeOfE01a1=code;
			strsql.delete(0,strsql.length());
			strsql.append("select codesetid,parentid,codeitemdesc from organization");
			strsql.append(" where codeitemid='");
			strsql.append(code);
			strsql.append("'");
			this.frowset =dao.search(strsql.toString());	//执行当前查询的sql语句	
			if(this.frowset.next())
			{
				codeitemdesc=this.frowset.getString("codeitemdesc");
				code=this.frowset.getString("parentid");
			}	
			e01a1=codeitemdesc;
			strsql.delete(0,strsql.length());
			strsql.append("select codesetid,parentid,codeitemdesc from organization");
			strsql.append(" where codeitemid='");
			strsql.append(code);
			strsql.append("' and codesetid='UM'");					
			this.frowset =dao.search(strsql.toString());	//执行当前查询的sql语句	
			if(this.frowset.next())
			{
				codeOfE0122=code;
				codeitemdesc=this.frowset.getString("codeitemdesc");
				codeitemid=this.frowset.getString("parentid");
				pre=this.frowset.getString("codesetid");
			}else{
				codeOfE0122="";
				codeitemdesc="";
			}
			e0122=codeitemdesc;
			code=codeitemid;
			while(!"UN".equalsIgnoreCase(pre)&&i<10)
			{
				codeOfB0110=code;
				strsql.delete(0,strsql.length());
				strsql.append("select codesetid,parentid,codeitemdesc from organization");
				strsql.append(" where codeitemid='");
				strsql.append(code);
				strsql.append("'");					
				this.frowset =dao.search(strsql.toString());	//执行当前查询的sql语句	
				if(this.frowset.next())
				{
					codeitemid=code;
					pre=this.frowset.getString("codesetid");
					codeitemdesc=this.frowset.getString("codeitemdesc");
					code=this.frowset.getString("parentid");
				}			
			}
			b0110=codeitemdesc;
		}else{
			
		}
		this.getFormHM().put("bb0110", b0110);
		this.getFormHM().put("be0122", e0122);
		this.getFormHM().put("be01a1", e01a1);
		this.getFormHM().put("codeOfB0110", codeOfB0110);
		this.getFormHM().put("codeOfE0122", codeOfE0122);
		this.getFormHM().put("codeOfE01a1", codeOfE01a1);
	}
}
