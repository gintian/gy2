package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class InitSelectPosTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList list=new ArrayList();
			String employType=(String)this.getFormHM().get("employType");							//0：业务平台  1：自助平台
//			权限控制
			/*if(!this.userView.isSuper_admin()&&employType.equals("0")&&this.getUserView().getUnit_id().length()==0)
				return;
			if(!this.userView.isSuper_admin()&&employType.equals("1")&&(this.getUserView().getManagePrivCodeValue()==null||this.getUserView().getManagePrivCodeValue().length()==0))
				return;*/
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			ParameterXMLBo bo0=new ParameterXMLBo(this.getFrameconn(),"1");
	    	HashMap map1=bo0.getAttributeValues();
	    	String hireMajor="";
	    	boolean isHireMajor=false;
	    	if(map1.get("hireMajor")!=null&&((String)map1.get("hireMajor")).length()>0)
	    		hireMajor=(String)map1.get("hireMajor");
			if(hireMajor!=null&&hireMajor.length()>0)
			{
				
				isHireMajor=true;
			}
			StringBuffer sql = new StringBuffer();
			StringBuffer sql_select=new StringBuffer("select z0301,z0321,z0325,z0311,z0336,org1.codeitemdesc,org2.codeitemdesc un,org3.codeitemdesc um");	
			if(isHireMajor)
				sql_select.append(","+hireMajor);
			StringBuffer sql_from=new StringBuffer(" from z03 left join (select * from organization where codesetid='@K') org1  on z03.z0311=org1.codeitemid");
			sql_from.append(" left join (select * from organization where codesetid='UN') org2   on  z03.z0321=org2.codeitemid");
			sql_from.append(" left join (select * from organization where codesetid='UM') org3   on  z03.z0325=org3.codeitemid");
			sql_from.append(" where z0319='04'");						
			//权限控制
			HashMap map = new HashMap();
			/**原来程序是根据z0311限制，但是校园招聘岗位是同一个岗位，所以改用需求部门来限制了*/
			if(!this.userView.isSuper_admin()||!"1".equals(this.userView.getGroupId()))
			{
				 String unit=userView.getUnitIdByBusi("7");
				  if(unit==null|| "".equals(unit)|| "UN".equalsIgnoreCase(unit)){
					  sql_from.append(" and z03.z0325 like '#'");
				  }
				  if(unit.trim().length()==3){//业务用户去操作单位的时候可能会出现全部 的情况
					  
						
				  }			  
				  unit = PubFunc.getTopOrgDept(unit);
				  if(unit.indexOf("`")==-1){
					  sql_from.append(" and (z03.z0325 like '"+unit.substring(2)+"%' or z03.z0325 is null)");
				  }else{
					 String[] temp=unit.split("`");	
					 StringBuffer tempSql=new StringBuffer("");
					 StringBuffer z0321sql=new StringBuffer();
					 for(int i=0;i<temp.length;i++){
						 if(temp[i]==null|| "".equals(temp[i]))
								continue;
						 if(temp[i].startsWith("UN")||temp[i].startsWith("UM")){
							tempSql.append(" or z03.z0325 like '"+temp[i].substring(2)+"%'");
							z0321sql.append(" or z03.z0321 like '"+temp[i].substring(2)+"%'");
						 }else{
							 tempSql.append(" or z03.z0325 like '"+temp[i]+"%'");
							 z0321sql.append(" or z03.z0321 like '"+temp[i]+"%'");
						 }
					 }
					 sql_from.append(" and (( "+tempSql.substring(3)+" ) or("+z0321sql.substring(3)+"))");
				  }
				 
//				if(this.userView.getStatus()==0)
//				{
//					String codeid=this.userView.getUnit_id();
//					if(codeid==null||codeid.equals(""))
//					{
//						sql_from.append(" and z03.z0325 like '#'");
//					}else if(codeid.trim().length()==3)
//					{
//						
//					}
//					else if(codeid.indexOf("`")==-1)
//					{
//						sql_from.append(" and (z03.z0325 like '"+codeid.substring(2)+"%' or z03.z0325 is null)");
//					}
//					else
//					{
//						StringBuffer tempSql=new StringBuffer("");
//						String[] temp=this.getUserView().getUnit_id().split("`");
//						StringBuffer z0321sql=new StringBuffer();
//						for(int i=0;i<temp.length;i++)
//						{
//							if(temp[i]==null||temp[i].equals(""))
//								continue;
//							tempSql.append(" or z03.z0325 like '"+temp[i].substring(2)+"%'");
//							z0321sql.append(" or z03.z0321 like '"+temp[i].substring(2)+"%'");
//						}
//						sql_from.append(" and (( "+tempSql.substring(3)+" ) or("+z0321sql.substring(3)+"))");
//					}
//				}
//				else if(this.userView.getStatus()==4)
//				{
//					String codeset = this.userView.getManagePrivCode();
//					String codeid=this.userView.getManagePrivCodeValue();
//					if(codeset==null||codeset.equals(""))
//					{
//						sql_from.append(" and z03.z0325 like '#'");
//					}
//					else
//					{
//						if(codeid==null||codeid.equals(""))
//						{		
//							sql_from.append(" and (z03.z0325 like '%' or z03.z0325 is null)");
//						}
//						else
//						{
//							sql_from.append(" and ((z03.z0325 like '"+codeid+"%')or (z03.z0321 like '"+codeid+"'))");
//						}
//					}
//				}
			}
			EmployNetPortalBo enpbo=new EmployNetPortalBo(this.getFrameconn());
			sql_from.append(enpbo.getDateSql(">=","Z0329"));
			sql_from.append(enpbo.getDateSql("<=","Z0331"));
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			if(isHireMajor){
		    	sql.append(" select * from ("+sql_select.toString()+sql_from.toString()+" and z0336<>'01'  ");
		    	sql.append(" and z0311 in(select codeitemid from organization where codesetid='@K' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date)");
		    	sql.append(" union all "+sql_select.toString()+sql_from.toString()+" and z0336='01') T order by un");
			}
			else
			{
				sql.append(sql_select.toString()+sql_from.toString()+" and z0311 in");
				sql.append("(select codeitemid from organization where codesetid='@K' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date)");
				sql.append(" order by un ");
			}
			//sql_from.append(" order by un ");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			int i=0;
			while(this.frowset.next())
			{
				if(map.get(this.frowset.getString("z0301"))!=null)
					continue;
				else
					map.put(this.frowset.getString("z0301"),"1");
				
				if(this.frowset.getString("codeitemdesc")==null)
					continue;
				
				i++;
				LazyDynaBean abean=new LazyDynaBean();
				if(isHireMajor&&this.frowset.getString("z0336")!=null&& "01".equals(this.frowset.getString("z0336")))
				{
					FieldItem item =DataDictionary.getFieldItem(hireMajor.toLowerCase());
					String value=this.frowset.getString(hireMajor);
					if(item!=null&&item.isCode())
					{
						value=AdminCode.getCodeName(item.getCodesetid(), value);
					}
					String z0325=this.frowset.getString("z0325");
//			    	if(z0325==null||z0325.equals(""))
//			    	{
//			    		abean.set("name",i+". "+value+"     [ "+this.frowset.getString("un")+" ]");
//		    		}
//		    		else
//		    		{
			    		if(Integer.parseInt(display_e0122)==0)
			    		{
			    		    String um=this.frowset.getString("um")!=null?this.frowset.getString("um"):"";
                            String un=this.frowset.getString("un")!=null?this.frowset.getString("un"):"";
                            String name=i+". ";
                            if(um.length()>0)
                                name+=um+"/";
                            name+=value;
                            if(un.length()>0)
                                name+="["+un+"]";
                        //  abean.set("name",i+". "+this.frowset.getString("um")+"/"+value+" [ "+this.frowset.getString("un")+" ]");
                            abean.set("name",name);
			    		}
			    		else
			    		{
//		            		CodeItem aitem=AdminCode.getCode("UM",z0325,Integer.parseInt(display_e0122));
//		            		String parent="";
//		        	    	if(aitem!=null)
//		    	        	{
//		    	        		parent+=aitem.getCodename()+"/";
//		            		}
//		        	    	abean.set("name",i+". "+parent+value+" [ "+this.frowset.getString("un")+" ]");
			    			String org="";
			    			// 新增招聘需求 可以在单位下直接建立岗位这样 需要查找z0321 的值 dml s2011年9月9日14:54:39
			    			if(this.frowset.getString("z0325")==null|| "NUll".equalsIgnoreCase(this.frowset.getString("z0325").trim())||this.frowset.getString("z0325").trim().length()==0){
			    				org=AdminCode.getOrgUpCodeDesc(this.frowset.getString("z0321"), Integer.parseInt(display_e0122), 0);
			    			}else{
			    				org=AdminCode.getOrgUpCodeDesc(this.frowset.getString("z0325"), Integer.parseInt(display_e0122), 0);
			    			}
			    			if(org==null||org.trim().length()==0){
								org=this.frowset.getString("un")==null?"":this.frowset.getString("un");
								if(this.frowset.getString("um")!=null)
									org+=" / "+this.frowset.getString("um");
							}
			    			abean.set("name",i+". "+org+"/"+value+" [ "+this.frowset.getString("un")+" ]");
			    		}
		    		//}
				}
				else
				{
			    	String z0325=this.frowset.getString("z0325");
//			    	if(z0325==null||z0325.equals(""))
//			    	{
//			    		abean.set("name",i+". "+this.frowset.getString("codeitemdesc")+"     [ "+this.frowset.getString("un")+" ]");
//		    		}
//		    		else
//		    		{
			    		if(Integer.parseInt(display_e0122)==0)
			    		{
			    		    String value = this.frowset.getString("codeitemdesc");
			    		    String um=this.frowset.getString("um")!=null?this.frowset.getString("um"):"";
                            String un=this.frowset.getString("un")!=null?this.frowset.getString("un"):"";
                            String name=i+". ";
                            if(um.length()>0)
                                name+=um+"/";
                            name+=value;
                            if(un.length()>0)
                                name+="["+un+"]";
                        //  abean.set("name",i+". "+this.frowset.getString("um")+"/"+this.frowset.getString("codeitemdesc")+" [ "+this.frowset.getString("un")+" ]");
                            abean.set("name",name);
			    		}
			    		else
			    		{
//		            		CodeItem item=AdminCode.getCode("UM",z0325,Integer.parseInt(display_e0122));// 中核华兴提出 如果相同部门或单位发布同岗位无法区别希望多些是几层单位 dml2011年9月7日9:46:01
//		            		String parent="";
//		        	    	if(item!=null)
//		    	        	{
//		    	        		parent+=item.getCodename()+"/";
//		            		}
//		        	    	abean.set("name",i+". "+parent+this.frowset.getString("codeitemdesc")+" [ "+this.frowset.getString("un")+" ]");
			    			//dml 2011年9月7日9:54:21
			    			String org="";
			    			// 新增招聘需求 可以在单位下直接建立岗位这样 需要查找z0321 的值 2011年9月9日14:54:39
			    			if(this.frowset.getString("z0325")==null|| "NUll".equalsIgnoreCase(this.frowset.getString("z0325").trim())||this.frowset.getString("z0325").trim().length()==0){
			    				org=AdminCode.getOrgUpCodeDesc(this.frowset.getString("z0321"), Integer.parseInt(display_e0122), 0);
			    			}else{
			    				org=AdminCode.getOrgUpCodeDesc(this.frowset.getString("z0325"), Integer.parseInt(display_e0122), 0);
			    			}
			    			if(org==null||org.trim().length()==0){
								org=this.frowset.getString("un")==null?"":this.frowset.getString("un");
								if(this.frowset.getString("um")!=null)
									org+=" / "+this.frowset.getString("um");
							}
			    			abean.set("name",i+". "+org+"/"+this.frowset.getString("codeitemdesc")+" [ "+this.frowset.getString("un")+" ]");
			    		}
		    		//}
				}
				/**xcs 为应聘简历/推荐岗位时,加密，如果有涉及到其他地方出错,在进行改正**/
				String z0301 = this.frowset.getString("z0301") ==null?"":this.frowset.getString("z0301");
				z0301 = PubFunc.encrypt(z0301);
		    	 abean.set("value",z0301);
				list.add(abean);
			}
			this.getFormHM().put("posList",list);
            // 获取最大推荐岗位数
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
            HashMap initmap = parameterXMLBo.getAttributeValues();
            String max_count = "";
            if (initmap.get("max_count") != null)
                max_count = (String) initmap.get("max_count");

            max_count = max_count == null || max_count.length() < 1 ? "3" : max_count;
	        
	        this.getFormHM().put("max_count",max_count);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
