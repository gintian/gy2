/*
 * Created on 2005-12-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPosBusinessListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String codesetid=(String)this.getFormHM().get("codesetid");
		String a_code=(String)hm.get("a_code");
		String codeitem=(String)hm.get("codeitem");
		String full = (String)hm.get("full");
		String param = (String)this.getFormHM().get("param");
		String fromflag="1";//区分入口：=1或无值 从代码维护进入，=2从能力素质模型中进入 =3培训课程 =5新招聘-考试科目 =6证书管理代码类
		String object_type="";//区分素质模型中的不同模块（主要用于返回不同模块）
		String historyDate="";
		if(hm.get("fromflag")!=null&&!"".equals((String)hm.get("fromflag")))
		{
			fromflag=(String)hm.get("fromflag");
			if("2".equals(fromflag))
			{
		    	object_type=(String)hm.get("object_type");
			    historyDate=(String)hm.get("historyDate");
			}
		}
		//String param = (String) hm.get("param");// 区分是显示
		// 职务编码、职务级别设置、岗/职位编码或岗/职位级别设置
		//hm.remove("param");
		param = param != null && param.length() > 1 ? param : "";
		String cflag;
		hm.remove("codeitem");
		if(full==null)
			if("".equalsIgnoreCase(codesetid)|| "#".equalsIgnoreCase(codesetid))
				throw new GeneralException("", ResourceFactory.getProperty("pos.posbusiness.nosetposcode"),"", "");
				
		if(a_code==null)
			a_code=codesetid;
//		String first = (String)this.getFormHM().get("first");
//		if(first.equalsIgnoreCase("4")){
//			a_code=null;
//			this.getFormHM().put("first","3");
//		}
		
		try
		{	
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    
		    //zxj 20160510 检查代码类是否存在
		    if (a_code.trim().length()>=2) {
		        String codeSetId = a_code.trim().substring(0,2);
    		    this.frowset = dao.search("SELECT 1 FROM codeset WHERE codesetid='" + codeSetId + "'");
    		    if(!this.frowset.next())
    		        throw new GeneralException("", "【" + codeSetId + "】" + ResourceFactory.getProperty("codemaintence.codeset.unpresent"), "", "");
		    }
		    
			String codeflag=SystemConfig.getPropertyValue("dev_flag");
			if(codeflag==null|| "0".equals(codeflag)|| "".equals(codeflag)){
				cflag="0";
			}else{
				cflag="1";
			}
			
			if("68".equalsIgnoreCase(a_code.substring(0,2))){//陈旭光：判断是否是知识点页面的操作
				cflag="1";
			}
			
			ArrayList codeitemlist=new ArrayList();
			StringBuffer strsql=new StringBuffer();
			String value = null;
			String validateflag = null;
			if(a_code!=null && a_code.trim().length()>2){
				strsql.append("select * from codeitem where parentid='");
				strsql.append(a_code.substring(2));
				strsql.append("' and codeitemid<>parentid and  codesetid='" + a_code.substring(0,2) + "'");
			}
			else
			{
				strsql.append("select * from codeitem where codeitemid=parentid and codesetid='" + a_code.substring(0,2) + "'");
			}
			
			strsql.append(" order by a0000,codeitemid");
			cat.debug("-----strsql------>" + strsql.toString());
			String sql = "select status,validateflag from codeset where codesetid='"+a_code.substring(0,2)+"'";
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				value = this.frowset.getString("status");
				validateflag = this.frowset.getString("validateflag");
			}
			
			if("PS_C_CODE".equals(param))
				validateflag= "1";
			
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				RecordVo codeitemvo=new RecordVo("codeitem");
				codeitemvo.setString("codesetid",this.frowset.getString("codesetid"));
				codeitemvo.setString("codeitemid",this.frowset.getString("codeitemid"));
				codeitemvo.setString("codeitemdesc",this.frowset.getString("codeitemdesc"));
				codeitemvo.setString("parentid",this.frowset.getString("parentid"));
				codeitemvo.setString("childid",this.frowset.getString("childid"));
				if(validateflag!=null&& "1".equals(validateflag)){
					codeitemvo.setDate("start_date", this.frowset.getDate("start_date"));
					codeitemvo.setDate("end_date", this.frowset.getDate("end_date"));
				}else{
					codeitemvo.setInt("invalid", this.frowset.getInt("invalid"));
				}
				codeitemvo.setInt("flag",0);
				codeitemvo.setString("corcode",this.frowset.getString("corcode"));
				if("68".equals(a_code.substring(0,2))){
					String b0110=frowset.getString("b0110");
					TrainCourseBo tbo = new TrainCourseBo(userView);
	            	int isP = tbo.isUserParent(b0110);
	            	if(userView!=null&&!userView.isSuper_admin()){
	            		if(isP==-1){
	            			continue;
	            		}else if(isP==2){
	            			codeitemvo.setString("b0110", "0");
	            		}else{
	            			codeitemvo.setString("b0110", "1");
	            		}
	            	}else
	            		codeitemvo.setString("b0110", "1");
				}
				codeitemvo.setInt("a0000", this.frowset.getInt("a0000"));
				codeitemlist.add(codeitemvo);
			}
			
			this.getFormHM().put("codeitemlist",codeitemlist);	
			this.getFormHM().put("isrefresh","no");
			this.getFormHM().put("codeitem",codeitem);
			this.getFormHM().put("cflag",cflag);
			this.getFormHM().put("valueflag",value);
			this.getFormHM().put("validateflag",validateflag==null?"":validateflag);
			this.getFormHM().put("param", param);
			if(param.indexOf("LEVEL")!=-1||"68".equals(param)){//当属于级别设置时控制在jsp不显示职务代码列
				this.getFormHM().put("islevel", "yes");
			}else{
				this.getFormHM().put("islevel", "no");
			}
			this.getFormHM().put("fromflag", fromflag);
			this.getFormHM().put("object_type", object_type);
			this.getFormHM().put("historyDate", historyDate);
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}		
	}
}
