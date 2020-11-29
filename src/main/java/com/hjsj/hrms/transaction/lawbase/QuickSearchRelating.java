package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:QuickSearchRelating.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class QuickSearchRelating extends IBusiness{
	
	public void execute() throws GeneralException {
		
		String name = (String)this.getFormHM().get("selname");
		String priv = (String)this.getFormHM().get("priv");
		name = SafeCode.decode(name);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if(name==null|| "".equalsIgnoreCase(name)){
			name="";
		}
		String dbpre_str=(String)this.getFormHM().get("dbpre_str");
		String checkflag=(String)this.getFormHM().get("checkflag");
		
	//	System.out.println(dbpre_str+"---------"+checkflag);
		
		checkflag=checkflag==null?"0":checkflag;
		ArrayList dblist = new ArrayList();
		ArrayList userlist = new ArrayList();
		dblist = DataDictionary.getDbpreList();
		/**招聘中特殊处理，除招聘库外的所有登录用户库*/
		if("8".equals(checkflag))
		{
			DbNameBo dbbo=new DbNameBo(this.getFrameconn());				
			ArrayList logdblist=dbbo.getAllLoginDbNameList();
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";
			if(vo!=null)
				dbname=vo.getString("str_value");
			ArrayList  list = new ArrayList();
			for(int i=0;i<logdblist.size();i++)
			{
				RecordVo avo=(RecordVo)logdblist.get(i);
				/*strlog.append(vo.getString("pre"));
				strlog.append(",");*/
				if(avo.getString("pre").equalsIgnoreCase(dbname))
		    		continue;
				list.add(avo.getString("pre"));
			}
			dblist = list;
		}
		else if("11".equals(checkflag))
		{
			ArrayList  list = new ArrayList();
			if(dbpre_str!=null && dbpre_str.trim().length()>0)
			{
				String[] nbase = dbpre_str.split(",");
				for (int i = 0; i < nbase.length; i++)
				{					
					list.add(nbase[i]);				    
				}
				dblist = list;
			}			
		}else if("18".equalsIgnoreCase(checkflag)){
			ArrayList  list = new ArrayList();
			 RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
	          String nbase_str="";
	          if(login_vo!=null) 
	            	nbase_str = login_vo.getString("str_value");//.toLowerCase();
	          if(nbase_str==null||nbase_str.trim().length()==0){
	        	  
	          }else{
	        	  String[] arr=nbase_str.split(",");
	        	  for(int i=0;i<arr.length;i++){
	        		  list.add(arr[i]);	
	        	  }
	          }
	          dblist = list;  
		}
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
	  	String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
	  	String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
		if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
			display_e0122="0";
		String codeid  = userView.getManagePrivCode();
		String codevalue = userView.getManagePrivCodeValue();
		try {
		if(!"".equalsIgnoreCase(name))
			name = PubFunc.getStr(name);
		//田野修改审批关系查找下级是的快速查询要按照登录人控制范围权限控制查询人员
		if("18".equalsIgnoreCase(checkflag)){
			String unsStr = userView.getUnitIdByBusi("4");
			String[] uns = unsStr.split("`");
			for(int j = 0 ;j<uns.length;j++){
				//根据登录人控制的范围在单位、部门或者岗位上的不同处理情况不同
				if(uns[j].length()>=2){
					codeid  = uns[j].substring(0,2);
					codevalue = uns[j].substring(2);
				}
				for(int i=0;i<dblist.size();i++){
					String andsql = " and ";
					if("UM".equalsIgnoreCase(codeid))
						andsql +="e0122 like '";
					else if("UN".equalsIgnoreCase(codeid))
						andsql += "b0110 like '";
					andsql += codevalue +"%' ";
					String usersql = "select A0100,A0101,e0122 from "+dblist.get(i)+"A01 where A0101 like '"+name+"%'";
					if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) ))
						usersql = "select A0100,A0101,e0122 from "+dblist.get(i)+"A01 where (A0101 like '"+name+"%' or "+pinyin_field+" like '"+name+"%')"  ;
					if(priv!=null&& "1".equals(priv)&&!"".equalsIgnoreCase(codevalue))
						usersql += andsql;
					this.frowset = dao.search(usersql);
					String dataName="";
					while(this.frowset.next()){
						if("0".equals(display_e0122))
							dataName=AdminCode.getCodeName("UM",this.frowset.getString("e0122"))+"/"+this.frowset.getString("a0101");
		        		  else{
		        			  CodeItem citem=AdminCode.getCode("UM",this.frowset.getString("e0122"),Integer.parseInt(display_e0122));
		        			  if(citem!=null)
		        				  dataName=citem.getCodename()+seprartor+this.frowset.getString("a0101");
		        			  else
		        				  dataName=AdminCode.getCodeName("UM",this.frowset.getString("e0122"))+"/"+this.frowset.getString("a0101");
		        		  }
						if("18".equalsIgnoreCase(checkflag)){
							CommonData data = new CommonData(dblist.get(i)+this.frowset.getString("A0100"),dataName);
							userlist.add(data);
						}else{
							CommonData data = new CommonData(dblist.get(i)+this.frowset.getString("A0100"),this.frowset.getString("A0101")+"("+this.frowset.getString("A0100")+")");
							userlist.add(data);
						}
					}
				}
			}
				
		}//田野添加结束
		else{//修改前代码为了不影响以前的其他模块的流程 田野添加if else 判断，审批关系走上面的代码其他的还是走原来的else里的代码
			for(int i=0;i<dblist.size();i++){
				//田野修改 权限获取不用每次循环，提出循环外进行优化
				/*String codeid  = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();*/
				String andsql = " and ";
				if("UM".equalsIgnoreCase(codeid))
					andsql +="e0122='";
				else if("UN".equalsIgnoreCase(codeid))
					andsql += "b0110='";
				andsql += codevalue +"' ";
				String usersql = "select A0100,A0101,e0122 from "+dblist.get(i)+"A01 where A0101 like '"+name+"%'";
				if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) ))
					usersql = "select A0100,A0101,e0122 from "+dblist.get(i)+"A01 where (A0101 like '"+name+"%' or "+pinyin_field+" like '"+name+"%')"  ;
				if(priv!=null&& "1".equals(priv)&&!"".equalsIgnoreCase(codevalue))
					usersql += andsql;
				this.frowset = dao.search(usersql);
				String dataName="";
				while(this.frowset.next()){
					if("0".equals(display_e0122))
						dataName=AdminCode.getCodeName("UM",this.frowset.getString("e0122"))+"/"+this.frowset.getString("a0101");
	        		  else{
	        			  CodeItem citem=AdminCode.getCode("UM",this.frowset.getString("e0122"),Integer.parseInt(display_e0122));
	        			  if(citem!=null)
	        				  dataName=citem.getCodename()+seprartor+this.frowset.getString("a0101");
	        			  else
	        				  dataName=AdminCode.getCodeName("UM",this.frowset.getString("e0122"))+"/"+this.frowset.getString("a0101");
	        		  }
					if("18".equalsIgnoreCase(checkflag)){
						CommonData data = new CommonData(dblist.get(i)+this.frowset.getString("A0100"),dataName);
						userlist.add(data);
					}else{
						CommonData data = new CommonData(dblist.get(i)+this.frowset.getString("A0100"),this.frowset.getString("A0101")+"("+this.frowset.getString("A0100")+")");
						userlist.add(data);
					}
				}
			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("namelist",userlist);
	}

}
