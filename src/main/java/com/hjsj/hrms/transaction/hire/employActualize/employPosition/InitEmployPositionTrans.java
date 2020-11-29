package com.hjsj.hrms.transaction.hire.employActualize.employPosition;

import com.hjsj.hrms.businessobject.hire.Md5ForHire;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class InitEmployPositionTrans extends IBusiness {
	private final String key="klskuge9723kgs8772k3";
	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String s_startDate=(String)this.getFormHM().get("s_startDate");
			String e_startDate=(String)this.getFormHM().get("e_startDate");
			String s_endDate=(String)this.getFormHM().get("s_endDate");
			String e_endDate=(String)this.getFormHM().get("e_endDate");
			String value=(String)this.getFormHM().get("value");
			String viewvalue=(String)this.getFormHM().get("viewvalue");
			String hirePath=(String)this.getFormHM().get("hirePath");
			String pos_state=(String)this.getFormHM().get("pos_state");     //04 已发布 09暂停  06结束
			String posID=(String)this.getFormHM().get("posID");         //招聘职位id
			String isShowCondition=(String)this.getFormHM().get("isShowCondition");  //不显示查询条件  block：显示
			String order_item=(String)this.getFormHM().get("order_item");    //排序字段
			String order_desc=(String)this.getFormHM().get("order_desc");    // asc:升序  desc:降序
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			ArrayList laborDemandList= getFieldList(); 
			String fielditem1=(String)this.getFormHM().get("fielditem1");
			String fielditem2=(String)this.getFormHM().get("fielditem2");
			
			if("".equals(fielditem1))
				fielditem1 = ((CommonData)laborDemandList.get(0)).getDataValue();
			if("".equals(fielditem2))
				fielditem2 = ((CommonData)laborDemandList.get(1)).getDataValue();
			
				
			
			String dbname="";
			String schoolpostion=(String)this.getFormHM().get("Professional");//dml
			String key="klskuge9723kgs8772k3";
			this.getFormHM().remove("professional");
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设之中配置招聘人才库！"));
			if(dbname==null|| "".equals(dbname))
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设之中配置招聘人才库！"));
			this.deleteNotExistPerson(dbname);
			String returnflag="";
			if(hm.get("returnflag")!=null)
			{
				returnflag=(String)hm.get("returnflag");
			}
			else
			{
				returnflag=(String)this.getFormHM().get("returnflag");
			}
			this.getFormHM().put("returnflag", returnflag);
			/*//权限控制
			if(!this.userView.isSuper_admin()&&(this.getUserView().getUnit_id()==null||this.getUserView().getUnit_id().length()<=2))
				 throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
			*/
			this.getFormHM().put("posIDList",getPosIDList(value));
			
			if(hm.get("operate")!=null&& "init".equals((String)hm.get("operate")))
			{
				this.getFormHM().put("isShowCondition","none");
				hm.remove("operate");
				hirePath="";
				s_startDate="";
				e_startDate="";
				s_endDate="";
				e_endDate="";
				value="";
				this.getFormHM().put("posIDList",new ArrayList());
				viewvalue="";
				pos_state="04";
				posID="";
				
				
			}
			StringBuffer sql_select=new StringBuffer("select org1.corcode,z0336,z0301,z0311,z0325,z0321,");
			sql_select.append("z03."+fielditem1+",z03."+fielditem2+",");
			sql_select.append("org1.codeitemdesc,org2.codeitemdesc un,org3.codeitemdesc um");
			sql_select.append(",z0329,z0331");
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			String schoolPosition="";
			if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0)
				schoolPosition=(String)map.get("schoolPosition"); 
			String hireMajor=(String)map.get("hireMajor");  //招聘专业指标
			if(hireMajor!=null&&hireMajor.length()>0&&",z0329,z0331,z0301,z0311,z0325,".indexOf(hireMajor.toLowerCase())==-1)
				sql_select.append(","+hireMajor);
			StringBuffer sql_from=new StringBuffer(" from Z03 left join (select * from organization where codesetid='@K') org1  on z03.z0311=org1.codeitemid");
			sql_from.append(" left join (select * from organization where codesetid='UN') org2   on  z03.z0321=org2.codeitemid");
			sql_from.append(" left join (select * from organization where codesetid='UM') org3   on  z03.z0325=org3.codeitemid");
			sql_from.append(" where z0319='"+pos_state+"'");
			if(hirePath!=null&&hirePath.trim().length()>0)
			{
				if("01".equals(hirePath))
					posID="";
				sql_from.append(" and Z0336='"+hirePath+"'");
				/**dml
				 * */
				if("01".equals(hirePath)){
					if(schoolpostion!=null&&schoolpostion.trim().length()!=0){
						if(hireMajor!=null&&hireMajor.length()!=0)
							sql_from.append(" and "+hireMajor+"='"+schoolpostion+"' ");
					}
				}
					
				
			}
			/**
			 * dml*/
			if(hireMajor!=null&&hireMajor.length()!=0){
				ArrayList list=this.isCodeList(hireMajor);
				if(list.size()!=0){
					String iscode=(String)list.get(0);
					this.getFormHM().put("isCode", list.get(0));
					if("true".equalsIgnoreCase(iscode)){
						list.remove(0);
						this.getFormHM().put("pflist", list);
					}
				}else{
					this.getFormHM().put("isCode", "false");
				}
				
			}
			if(s_startDate!=null&&s_startDate.trim().length()>0)
			{
				sql_from.append(getDateSql(">=","z0329",s_startDate.trim()));
			}
			if(e_startDate!=null&&e_startDate.trim().length()>0)
			{
				sql_from.append(getDateSql("<=","z0329",e_startDate.trim()));
			}
			if(s_endDate!=null&&s_endDate.trim().length()>0)
			{
				sql_from.append(getDateSql(">=","z0331",s_endDate.trim()));
			}
			if(e_endDate!=null&&e_endDate.trim().length()>0)
			{
				sql_from.append(getDateSql("<=","z0331",e_endDate.trim()));
			}
			if(value!=null&&value.trim().length()>0)
			{
				 String _str=Sql_switcher.isnull("z03.z0336","''");
				sql_from.append(" and ( ( z0311 like '"+value+"%' and  "+_str+"<>'01' ) or ( z03.z0321 like '"+value+"%' and  "+_str+"='01' ) or ( z03.z0325 like '"+value+"%' and  "+_str+"='01' ) ) ");
			}
			if(posID!=null&&posID.trim().length()>0)
				sql_from.append(" and z0311='"+posID+"'");
			
			//权限控制
			/**
			 * modify dml 2012-3-31 14:14:41
			 * reason 权限用户权限规则的改变出现新的方法
			 * 
			 * */
			if(!this.getUserView().isSuper_admin())
			{	
//				if(this.userView.getStatus()==0)
//				{
//					String codeid=this.getUserView().getUnit_id();
//					if(codeid==null||codeid.trim().length()==0||codeid.equalsIgnoreCase("UN"))
//					{
//						
//						codeid="-0";
//						throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//					}else if(codeid.trim().length()==3)
//					{
//						codeid="";
//					}
//					 if(codeid.indexOf("`")==-1)
//					{
//						 if(codeid.trim().length()>0&&!codeid.equals("-0"))
//						 {
//							 codeid=codeid.substring(2);
//						 }
//					//	 sql_from.append(" and z03.z0311 like '"+codeid+"%'");
//						 String _str=Sql_switcher.isnull("z03.z0336","''");
//						 sql_from.append(" and ( ( z03.z0311 like '"+codeid+"%' and  "+_str+"<>'01' ) or ( z03.z0321 like '"+codeid+"%' and  "+_str+"='01' ) or ( z03.z0325 like '"+codeid+"%' and  "+_str+"='01' ) ) ");
//						    
//					}			    	
//		    		else
//		    		{
//		    			StringBuffer tempSql=new StringBuffer("");
//		    			StringBuffer tempSql2=new StringBuffer("");
//						StringBuffer tempSql3=new StringBuffer("");
//					 	String _str=Sql_switcher.isnull("z03.z0336","''");
//					 	
//		    			String[] temp=codeid.split("`");
//		    			for(int i=0;i<temp.length;i++)
//		    			{
//		    				tempSql.append(" or z03.z0311 like '"+temp[i].substring(2)+"%'");
//		    				tempSql2.append(" or z03.z0321 like '"+temp[i].substring(2)+"%'");
//		    				tempSql3.append(" or z03.z0325 like '"+temp[i].substring(2)+"%'");
//		    			}
//		    		//	sql_from.append(" and ( "+tempSql.substring(3)+" ) ");
//		    			sql_from.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
//	    			}
//				}
//				else
//				{
//					String codeid=this.getUserView().getManagePrivCodeValue();
//					String codesetid=this.getUserView().getManagePrivCode();
//					if((codesetid==null||codesetid.trim().length()==0)&&(codeid==null||codeid.trim().length()==0))
//					{
//						codeid="-0";
//						throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//					}
//					 String _str=Sql_switcher.isnull("z03.z0336","''");
//				//	 sql_from.append(" and z03.z0311 like '"+codeid+"%'");
//					 sql_from.append(" and ( ( z03.z0311 like '"+codeid+"%' and  "+_str+"<>'01' ) or ( z03.z0321 like '"+codeid+"%' and  "+_str+"='01' ) or ( z03.z0325 like '"+codeid+"%' and  "+_str+"='01' ) ) "); 
//					 
//				}
				String code=this.userView.getUnitIdByBusi("7");
			
				if(code==null||code.trim().length()==0|| "UN".equalsIgnoreCase(code))
				{
					throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
				}else if(code.trim().length()==3)
				{
					code="";
				}
				
				code=PubFunc.getTopOrgDept(code);
				if(code.indexOf("`")==-1){
					if(code.startsWith("UM")||code.startsWith("UN")){
						code=code.substring(2);
					}
					String _str=Sql_switcher.isnull("z03.z0336","''");
					sql_from.append(" and ( ( z03.z0311 like '"+code+"%' and  "+_str+"<>'01' ) or ( z03.z0321 like '"+code+"%' and  "+_str+"='01' ) or ( z03.z0325 like '"+code+"%' and  "+_str+"='01' ) ) ");
				}else{
					StringBuffer tempSql=new StringBuffer("");
	    			StringBuffer tempSql2=new StringBuffer("");
					StringBuffer tempSql3=new StringBuffer("");
				 	String _str=Sql_switcher.isnull("z03.z0336","''");
				 	String[] temp=code.split("`");
				 	for(int i=0;i<temp.length;i++)
	    			{
				 		if(code.startsWith("UM")||code.startsWith("UN")){
		    				tempSql.append(" or z03.z0311 like '"+temp[i].substring(2)+"%'");
		    				tempSql2.append(" or z03.z0321 like '"+temp[i].substring(2)+"%'");
		    				tempSql3.append(" or z03.z0325 like '"+temp[i].substring(2)+"%'");
				 		}else{
				 			tempSql.append(" or z03.z0311 like '"+temp[i]+"%'");
		    				tempSql2.append(" or z03.z0321 like '"+temp[i]+"%'");
		    				tempSql3.append(" or z03.z0325 like '"+temp[i]+"%'");
				 		}
	    			}
				 	sql_from.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
				}
				
			}
			String hdt =SystemConfig.getPropertyValue("hdtconnect");
			if(hdt!=null&& "true".equalsIgnoreCase(hdt)){
				this.getFormHM().put("canshow", "1");
			}else{
				this.getFormHM().put("canshow", "2");
			}
			ArrayList posDemandList= getPosDemandList(sql_select.toString()+sql_from.toString()+" order by "+order_item+" "+order_desc,pos_state,hireMajor);// order by z03.  oracle库不+z03.会出错（）
			this.getFormHM().put("posDemandList",posDemandList);
			//System.out.println(sql_select.toString()+sql_from.toString());
		//	System.out.println(" order by "+order_item+" "+order_desc);
			String posCount=String.valueOf(posDemandList.size());
			this.getFormHM().put("posCount",posCount);
			this.getFormHM().put("orderDescList",getOrderDescList());
			this.getFormHM().put("orderItemList",getOrderItemList());
			this.getFormHM().put("viewValue",viewvalue);
			this.getFormHM().put("value",value);
			this.getFormHM().put("pos_state",pos_state);
			this.getFormHM().put("hirePath",hirePath);
			this.getFormHM().put("hirePathList",getHirePathList());
			
			this.getFormHM().put("posID",posID);
			this.getFormHM().put("s_startDate",s_startDate);
			this.getFormHM().put("e_startDate",e_startDate);
			this.getFormHM().put("s_endDate",s_endDate);
			this.getFormHM().put("e_endDate",e_endDate);
			this.getFormHM().put("schoolPosition", schoolPosition);
			this.getFormHM().put("laborDemandList",laborDemandList);
			this.getFormHM().put("fielditem1",fielditem1);
			this.getFormHM().put("fielditem2",fielditem2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	
	private ArrayList getHirePathList()
	{
		ArrayList list=new ArrayList();
		try
		{
			list.add(new CommonData("",""));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from codeitem where codesetid='35'");
			while(this.frowset.next())
			{
				CommonData data=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				list.add(data);
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public ArrayList getPosIDList(String value)
	{
		ArrayList list=new ArrayList();
		try
		{
			list.add(new CommonData("",""));
			if(value!=null&&value.trim().length()>0)
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select * from organization where codesetid='@K' and codeitemid like '"+value+"%' and codeitemid in (select E01A1 from K01)");
				while(this.frowset.next())
				{
					CommonData data=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
					list.add(data);
				}		
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	public ArrayList getPosDemandList(String sql,String pos_state,String hireMajor)
	{
		ArrayList list=new ArrayList();
		ArrayList laborDemandList= getFieldList(); 
		String fielditem1=(String)this.getFormHM().get("fielditem1");
		String fielditem2=(String)this.getFormHM().get("fielditem2");
		if("".equals(fielditem1))
			fielditem1 = ((CommonData)laborDemandList.get(0)).getDataValue();
		if("".equals(fielditem2))
			fielditem2 = ((CommonData)laborDemandList.get(1)).getDataValue();
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap posNumMap=new HashMap();
			String sql0="select count(a0100) countNum,zp_pos_id from zp_pos_tache,Z03 where zp_pos_tache.zp_pos_id=Z03.Z0301 and Z03.Z0319='"+pos_state+"' group by zp_pos_id";
			this.frowset=dao.search(sql0);
			while(this.frowset.next())
			{
				posNumMap.put(this.frowset.getString("zp_pos_id"),this.frowset.getString("countNum"));			
			}
			
			this.frowset=dao.search(sql);
			LazyDynaBean abean=null;
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			int floor=Integer.parseInt(display_e0122);
			FieldItem KKK=DataDictionary.getFieldItem(fielditem1);
			FieldItem KKK2=DataDictionary.getFieldItem(fielditem2);
			if("state".equalsIgnoreCase(fielditem1)){
				KKK=new FieldItem();
				KKK.setItemdesc("热点职位");
				KKK.setFieldsetid("z03");
				KKK.setItemid("state");
				KKK.setItemtype("A");
				KKK.setCodesetid("45");
			}
			if("state".equalsIgnoreCase(fielditem2)){
				KKK2=new FieldItem();
				KKK2.setItemdesc("热点职位");
				KKK2.setFieldsetid("z03");
				KKK2.setItemid("state");
				KKK2.setItemtype("A");
				KKK2.setCodesetid("45");
				
			}
			while(this.frowset.next())
			{	
				abean=new LazyDynaBean();
				String z0336 = this.frowset.getString("z0336")!=null?this.frowset.getString("z0336"):"";
				String az0301 = this.frowset.getString("z0301");
				String codeitemdesc="";
				Calendar cd=Calendar.getInstance();
				String corcode=this.frowset.getString("corcode")==null?"":this.frowset.getString("corcode");
				codeitemdesc=this.frowset.getString("codeitemdesc");
				String yy=""+cd.get(Calendar.YEAR);
				String mm=cd.get(Calendar.MONTH)+1<=9?"0"+(cd.get(cd.MONTH)+1):(cd.get(cd.MONTH)+1)+"";
				String dd=cd.get(Calendar.DATE)<=9?"0"+cd.get(Calendar.DATE):cd.get(Calendar.DATE)+"";
				String partime=yy+mm+dd;
				String cer="";
				Md5ForHire md5 =new Md5ForHire();
				cer=md5.getMD5((corcode+key+partime).getBytes());
				String ul="http://jtm.51chinahrd.com/jtmhongjin/jlcx.aspx?jobid="+corcode+"&cer="+cer;
				abean.set("href", ul);
				//System.out.println(ul);
				abean.set("z0301", az0301);
				String name  = "";
				if(KKK!=null&& "D".equalsIgnoreCase(KKK.getItemtype())){
					name=this.frowset.getDate(fielditem1)==null?"":dateFormat.format(this.frowset.getDate(fielditem1));
				}else{
					name=(String)this.frowset.getString(fielditem1)!=null?this.frowset.getString(fielditem1):"";
				}
				//String name  = this.frowset.getDate(fielditem1).toString()!=null?this.frowset.getDate(fielditem1).toString():"";
				String name2  ="";
				if(KKK2!=null&& "D".equalsIgnoreCase(KKK2.getItemtype())){
					name2=this.frowset.getDate(fielditem2)==null?"":dateFormat.format(this.frowset.getDate(fielditem2));
				}else{
					name2=(String)this.frowset.getString(fielditem2)!=null?this.frowset.getString(fielditem2):"";
				}

				name=PubFunc.toHtml(name);
				name2=PubFunc.toHtml(name2);
				FieldItem _item1=DataDictionary.getFieldItem(fielditem1);
				FieldItem _item2=DataDictionary.getFieldItem(fielditem2);
				if("state".equalsIgnoreCase(fielditem1)){
					_item1=new FieldItem();
					_item1.setItemdesc("热点职位");
					_item1.setFieldsetid("z03");
					_item1.setItemid("state");
					_item1.setItemtype("A");
					_item1.setCodesetid("45");
					
				}
				if("state".equalsIgnoreCase(fielditem2)){
					_item2=new FieldItem();
					_item2.setItemdesc("热点职位");
					_item2.setFieldsetid("z03");
					_item2.setItemid("state");
					_item2.setItemtype("A");
					_item2.setCodesetid("45");
					
				}
         		if(KKK.isCode()){
 					//if(KKK.getCodesetid().equals("0")){
 						name=AdminCode.getCodeName(_item1.getCodesetid(),name);
         		}
         		
         		if(KKK2.isCode()){
 					//if(KKK.getCodesetid().equals("0")){
 						name2=AdminCode.getCodeName(_item2.getCodesetid(),name2);	
         		}
 					
				abean.set(fielditem1.toLowerCase(),name);
				abean.set(fielditem2.toLowerCase(),name2);
				abean.set("jobid", corcode);
				abean.set("cer", cer);
				if(posNumMap.get(az0301)!=null)
					abean.set("resumeNum",(String)posNumMap.get(az0301));
				else
					abean.set("resumeNum","0");
				abean.set("z0311",this.frowset.getString("z0311")==null?"":this.frowset.getString("z0311"));
				
				if(hireMajor!=null&&hireMajor.trim().length()>0&& "01".equals(z0336))
				{
					FieldItem _item=DataDictionary.getFieldItem(hireMajor.toLowerCase());
					if(_item.getCodesetid().length()>0&&!"0".equals(_item.getCodesetid()))
					{
						String _value=this.frowset.getString(hireMajor)!=null?this.frowset.getString(hireMajor):"";
						if(_value.length()>0)
							_value=AdminCode.getCodeName(_item.getCodesetid(),_value);
						abean.set("codeitemdesc",_value);
					}
					else
					{
						abean.set("codeitemdesc",this.frowset.getString(hireMajor)!=null?this.frowset.getString(hireMajor):"");
					}
				}
				else
					abean.set("codeitemdesc",codeitemdesc==null?"":codeitemdesc);
				//String org=this.frowset.getString("un")==null?"":this.frowset.getString("un");//中核华兴提出招聘岗位进行推荐时不同岗位相同部门分不清所属的二级单位希望多显示几级单位信息
//				if(this.frowset.getString("um")!=null)
//					org+=" / "+this.frowset.getString("um");
				//dml start 2011年9月6日11:57:34
				String org="";
				if(Integer.parseInt(display_e0122)==0)
	    		{
					
						org=this.frowset.getString("un")==null?"":this.frowset.getString("un");
						if(this.frowset.getString("um")!=null)
							org+=" / "+this.frowset.getString("um");
					
	    		}else{
	    			if(this.frowset.getString("z0325")==null|| "NUll".equalsIgnoreCase(this.frowset.getString("z0325"))||this.frowset.getString("z0325").trim().length()==0){
	    				org=AdminCode.getOrgUpCodeDesc(this.frowset.getString("z0321"), floor, 0);
	    			}else{
	    				org=AdminCode.getOrgUpCodeDesc(this.frowset.getString("z0325"), floor, 0);
	    			}
					if(org==null||org.trim().length()==0){
						org=this.frowset.getString("un")==null?"":this.frowset.getString("un");
						if(this.frowset.getString("um")!=null)
							org+=" / "+this.frowset.getString("um");
					}
	    		}
				// dml end
				abean.set("org",org);
				String e0122="";
				if(this.frowset.getString("z0325")!=null)
					e0122=this.frowset.getString("z0325");
				abean.set("e0122",e0122);
				Date z0329=this.frowset.getDate("z0329");
				Date z0331=this.frowset.getDate("z0331");
				if(z0329==null)
					z0329=new Date();
				if(z0331==null)
					z0331=new Date();
				Calendar today=Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY,0);
				today.set(Calendar.MINUTE,1);
				today.set(Calendar.SECOND,1);
				Calendar appealStart=Calendar.getInstance();
				appealStart.setTime(z0329);
				appealStart.set(Calendar.HOUR_OF_DAY,0);
				appealStart.set(Calendar.MINUTE,1);
				appealStart.set(Calendar.SECOND,1);
				abean.set("isOverTime","0");
				
				if("04".equals(pos_state))
				{
					if(isOverTime(z0329,z0331))
						abean.set("isOverTime","1");
				}
				if(z0329!=null)
					abean.set("startdate",dateFormat.format(z0329));
				else
					abean.set("startdate","");
				if(z0331!=null)
					abean.set("enddate",dateFormat.format(z0331));
				else
					abean.set("enddate","");
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 比较起始时间和结束时间的先后顺
	 * 注释掉的部分在，截止日期和当前日期相同时 会判断为当前日期晚于截止日期，造成职位状态错误
	 * dml2011年12月31日10:10:16
	 * */
	public boolean isOverTime(Date startDate,Date endDate)
	{
		boolean flag=false;
		String srartY=String.valueOf(startDate.getYear());
		if(startDate!=null&&endDate!=null)
		{
			Calendar today=Calendar.getInstance();
			int yyt=today.get(today.YEAR);
			int mnt=today.get(today.MONTH);
			int mmt=today.get(today.DATE);
			
//			today.set(Calendar.HOUR_OF_DAY,0);
//			today.set(Calendar.MINUTE,1);
//			today.set(Calendar.SECOND,1);
			Calendar appealStart=Calendar.getInstance();
			Calendar appealEnd=Calendar.getInstance();
			appealStart.setTime(startDate);
			appealEnd.setTime(endDate);
			int yys=appealStart.get(appealStart.YEAR);
			int mns=appealStart.get(appealStart.MONTH);
			int mms=appealStart.get(appealStart.DATE);
			int yye=appealEnd.get(appealEnd.YEAR);
			int mne=appealEnd.get(appealEnd.MONTH);
			int mme=appealEnd.get(appealEnd.DATE);
			if(yyt<yys){
				flag=false;
				return flag;
			}
			if(yyt==yys){
				if(mnt<mns){
					flag=false;
					return flag;
				}
				if(mnt==mns){
					if(mmt<mms){
						flag=false;
						return flag;
					}
				}
			}
			if(yyt>yye){
				flag=true;
				return flag;
			}
			if(yyt==yye){
				if(mnt>mne){
					flag=true;
					return flag;
				}
				if(mnt==mne){
					if(mmt>mme){
						flag=true;
						return flag;
					}
				}
			}
//			appealStart.set(Calendar.HOUR_OF_DAY,0);
//			appealStart.set(Calendar.MINUTE,1);
//			appealStart.set(Calendar.SECOND,1);
//			appealEnd.setTime(endDate);
//
//			if(today.compareTo(appealStart)<0)
//			{
//				flag=true;
//				return flag;
//			}
//			if(today.compareTo(appealEnd)>0)
//			{
//				flag=true;
//				return flag;
//			}
//			System.out.print(today.compareTo(appealEnd)+"");
		}
		else if(startDate==null&&endDate!=null)
		{
			Calendar today=Calendar.getInstance();
			int yyt=today.get(today.YEAR);
			int mnt=today.get(today.MONTH);
			int mmt=today.get(today.DATE);
			Calendar appealEnd=Calendar.getInstance();
			appealEnd.setTime(endDate);
			int yye=appealEnd.get(appealEnd.YEAR);
			int mne=appealEnd.get(appealEnd.MONTH);
			int mme=appealEnd.get(appealEnd.DATE);
			if(yyt>yye){
				flag=true;
				return flag;
			}
			if(yyt==yye){
				if(mnt>mne){
					flag=true;
					return flag;
				}
				if(mnt==mne){
					if(mmt>mme){
						flag=true;
						return flag;
					}
				}
			}
//			if(today.compareTo(appealEnd)>0)
//				flag=true;
			
		}
		else if(startDate!=null&&endDate==null)
		{
			Calendar today=Calendar.getInstance();
			Calendar appealStart=Calendar.getInstance();
			int yyt=today.get(today.YEAR);
			int mnt=today.get(today.MONTH);
			int mmt=today.get(today.DATE);
			appealStart.setTime(startDate);
			int yys=appealStart.get(appealStart.YEAR);
			int mns=appealStart.get(appealStart.MONTH);
			int mms=appealStart.get(appealStart.DATE);
			if(yyt<yys){
				flag=false;
				return flag;
			}
			if(yyt==yys){
				if(mnt<mns){
					flag=false;
					return flag;
				}
				if(mnt==mns){
					if(mmt<mms){
						flag=false;
						return flag;
					}
				}
			}
//			if(today.compareTo(appealStart)<0)
//				flag=true;
		}
		return flag;
	}
	
	
	public ArrayList getOrderItemList()
	{
		ArrayList list=new ArrayList();
		try
		{
		
		list.add(new CommonData("z0311",ResourceFactory.getProperty("e01a1.label")));
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn(),"1");
		HashMap map=parameterXMLBo.getAttributeValues();
		String hireMajor=(String)map.get("hireMajor");  //招聘专业指标
		if(hireMajor!=null&&hireMajor.length()>0)
		{
			FieldItem item=DataDictionary.getFieldItem(hireMajor.toLowerCase());
			if(item!=null)
			{
				list.add(new CommonData(item.getItemid(),item.getItemdesc()));
			}
		}
		list.add(new CommonData("org2.codeitemdesc","所属机构"));
		list.add(new CommonData("z0329","开始时间"));
		list.add(new CommonData("z0331","结束时间"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	public ArrayList getOrderDescList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("asc","升序"));
		list.add(new CommonData("desc","降序"));
		return list;
	}
	
	public String getDateSql(String operate,String itemid,String value)
	{
		StringBuffer sql=new StringBuffer("");
		value=value.replaceAll("\\.","-");
		String values[]=value.split("-");
		
	//	Calendar d=Calendar.getInstance();
		String year=values[0];
		String month=values[1];
		String day=values[2];
		if(">".equals(operate)|| "<".equals(operate))
		{
			sql.append(" and ( "+Sql_switcher.year(itemid)+operate+year);
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+operate+month+"  )");
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+operate+day+"  )");	
			sql.append(" ) ");
		}
		else if(">=".equals(operate)|| "<=".equals(operate))
		{
			if(">=".equals(operate))
				sql.append(" and ( "+Sql_switcher.year(itemid)+">"+year);
			else
				sql.append(" and ( "+Sql_switcher.year(itemid)+"<"+year);
			
			if(">=".equals(operate))
				sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+">"+month+"  )");
			else
				sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"<"+month+"  )");
			
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+operate+day+"  )");	
			sql.append(" ) ");
		}
		else if("=".equals(operate))
			sql.append(" and ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+"="+day+"  )");	
		
		return sql.toString();
	}
	/**
	 * 在统计每个职位的简历数量之前，先删除在招聘库中不存在，但是存在于表zp_pos_tache表中的记录，
	 * 这样是为了解决在人员管理或者其他地方将招聘库的人员删除（这些地方没有删除人员与应聘职位的对应关系（即zp_pos_tache表中的信息））
	 * ，导致简历数量与实际人员数量不符的问题
	 * @param zpDbname
	 */
	public void deleteNotExistPerson(String zpDbname)
	{
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("delete from zp_pos_tache where a0100 not in (");
			buf.append("select a0100 from "+zpDbname+"a01 )");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.delete(buf.toString() ,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private ArrayList getFieldList(){
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
				String sql_str="select * from t_hr_busifield where fieldsetid='Z03' and useflag='1' and state= '1' and itemid != 'Z0311' and itemid != 'Z0301' and itemid != 'Z0329' and itemid != 'Z0331' and itemid != 'Z0323' and itemid != 'Z0101'";
				
			this.frowset = dao.search(sql_str);
		
			while(this.frowset.next()){
				CommonData obj = new CommonData(this.frowset.getString("itemid").toLowerCase(),this.frowset.getString("itemdesc"));
				list.add(obj);
			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获得校园招聘中招聘专业代码类型的代码值*/
	public ArrayList isCodeList(String hiremajor){
		ArrayList list=new ArrayList();
		StringBuffer sql=new StringBuffer();
		String iscode="false";
		sql.append("select itemtype,codesetid ,itemdesc from t_hr_busiField thr where thr.ItemId='");
		sql.append(hiremajor.toUpperCase());
		sql.append("'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		CommonData cd;
		try {
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				if("A".equalsIgnoreCase(this.frowset.getString("itemtype"))&& "0".equalsIgnoreCase(this.frowset.getString("codesetid"))){
					list.add(iscode);
				}else{
					iscode="true";
					list.add(iscode);
					sql.setLength(0);
					sql.append("select thr.fieldsetid ,thr.itemid,cd.codeitemid value1,cd.codeitemdesc desc1,org.codeitemid value2,org.codeitemdesc desc2  from t_hr_busiField thr left join codeitem cd on thr.codesetid=cd.codesetid left join organization org ");
					sql.append(" on thr.codesetid=org.codesetid where thr.itemid='");
					sql.append(hiremajor.toUpperCase());
					sql.append("'");
					this.frowset=dao.search(sql.toString());
					while(this.frowset.next()){
						cd=new CommonData();
						String value1=this.frowset.getString("value1");
						if(value1!=null&&value1.length()!=0){
							cd.setDataValue(value1);
							if(this.frowset.getString("desc1")!=null){
								cd.setDataName(this.frowset.getString("desc1"));
							}
						}
						if(cd.getDataName()==null&&cd.getDataValue()==null){
							value1=this.frowset.getString("value2");
							if(value1!=null&&value1.length()!=0){
								cd.setDataValue(value1);
							}
							if(this.frowset.getString("desc2")!=null){
								cd.setDataName(this.frowset.getString("desc2"));
							}
						}
						if(cd.getDataName()!=null&&cd.getDataValue()!=null){
							list.add(cd);
						}
					}
				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		
		return list;
	}
}
