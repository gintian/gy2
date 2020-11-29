/*
 * Created on 2005-6-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.browse;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchDeatilInfoShowTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private String sortname="";
	public void execute() throws GeneralException {
		List rs=null;
    	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
    	String infosort=(String)this.getFormHM().get("infosort");
    	String sortname=(String)this.getFormHM().get("sortname");
    	if(sortname!=null&&sortname.length()>0)
    		this.sortname=sortname;
    	String setname=(String)this.getFormHM().get("setname");    	
    	infosort=infosort!=null&&infosort.length()>0?infosort:"";
    	String flag=(String)hm.get("flag");    	
		if(!("infoself".equalsIgnoreCase(flag) && userView.getStatus()!=4))
		{
			String userbase=(String)this.getFormHM().get("userbase");//人员库
			String A0100=(String)this.getFormHM().get("a0100");                       //获得人员ID
			if("A0100".equals(A0100))
				A0100=userView.getUserId();
			else{
				if(!"infoself".equalsIgnoreCase(flag)){
					String returnvalue = (String)this.getFormHM().get("returnvalue");
					if(!("relation".equals(returnvalue)||"100000".equals(returnvalue))){
						CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
						userbase=checkPrivSafeBo.checkDb(userbase);
						A0100=checkPrivSafeBo.checkA0100("", userbase, A0100, "statnum".equals(returnvalue)?"4":"");
						ContentDAO dao = new ContentDAO(this.frameconn);
						setname=checkPrivSafeBo.checkFieldSet(userbase, setname, A0100, Constant.EMPLOY_FIELD_SET, dao);
					}
				}
			}
			searchMessage(userbase,setname,A0100,flag,infosort);	
		}
		else
		{
			if(this.userView.getA0100()!=null&&this.userView.getA0100().length()>0)
			{
				String userbase=this.userView.getDbname(); //人员库
				String tablename=userbase + "A01";                        //表的名称
				String A0100=userView.getA0100();
				searchMessage(userbase,setname,A0100,flag,infosort);
			}else
			  throw new GeneralException("","非自助平台用户!","","");
		}
		this.getFormHM().put("setname", setname);
		 String virAxx = SystemConfig.getPropertyValue("virtualOrgSet");
         virAxx = StringUtils.isEmpty(virAxx) ? "" : virAxx; 
		this.getFormHM().put("virAxx", virAxx);
  }
	private void searchMessage(String userbase,String setname,String A0100,String flag,String infosort)throws GeneralException
	{
		String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
		List rs=null;
		String tablenamesub=userbase + setname;                        //操纵表的名称
		StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
	    ArrayList list=new ArrayList();                             //封装子集的数据
		strsql.append("select * from " + tablenamesub);
		strsql.append(" where A0100='" + A0100 + "'" );
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    String fenlei_priv=(String)this.getFormHM().get("fenlei_priv");
	    InfoUtils infoUtils=new InfoUtils();				
		String sub_type=infoUtils.getOneselfFenleiType(userbase, A0100, fenlei_priv, dao);//人员分类
		String multimedia_file_flag = "";
	    if(!"A01".equals(setname))
	    {
	    	setOrgInfo(userbase,A0100,dao);
	   	    List infodetailfieldlist=null;
	   	    List infoSetList=null;//zgd 2014-5-16 子集支持附件调用里面的属性
	 	    try
			{	 
	 	     
			  if("infoself".equalsIgnoreCase(flag)){
			      /*
			       * 按分类授权获取到的子集/指标没有区分是不是员工角色特征下的，因此分类授权只能在业务上实现；
			       * 自助服务员工信息要使用分类授权的话，分类授权只能在员工角色特征下的角色授权，
			       * 其它地方的不能进行子集/指标的分类授权否则会显示全部的分类授权的子集/指标
			       */
			      if(sub_type!=null&&sub_type.length()>0) {
			          //得到分类授权子集
			          infodetailfieldlist=infoUtils.getSubPrivFieldList(this.userView,setname,sub_type, 1);
			          infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET, 1);
			          //如果分类中得不到指标则用默认权限的
			          if(infodetailfieldlist==null||infodetailfieldlist.size()<=0)
			              //获得当前子集的所有属性
			              infodetailfieldlist = userView.getPrivFieldList(setname, 0);
			          //获得所有权限的子集
			          if(infoSetList==null||infoSetList.size()<=0)
			              infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
		                    
			      } else {
			          // 获得当前子集的所有属性
			          infodetailfieldlist = userView.getPrivFieldList(setname, 0);
			          // 获得所有权限的子集
			          infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
			      }
//			 	  infodetailfieldlist= userView.getPrivFieldList(setname,0);   //获得当前子集的所有属性
//			      infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);   //获得所有权限的子集
			  }else
		      {
		    	  if(sub_type!=null&&sub_type.length()>0)
				  {
						//得到分类授权子集
		    		  infodetailfieldlist=infoUtils.getSubPrivFieldList(this.userView,setname,sub_type);
		    		  infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET);
		    		  if(infodetailfieldlist==null||infodetailfieldlist.size()<=0)//如果分类中得不到指标则用默认权限的
		    			  infodetailfieldlist=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
		    		  if(infoSetList==null||infoSetList.size()<=0)
						  infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		    		  
				  }else	{
					  infodetailfieldlist= userView.getPrivFieldList(setname); 
					  infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
				  }
		      }
			      
			  
			  
			  /*添加人员类别过滤指标*/
			    String personsortfield=new SortFilter().getSortPersonField(this.getFrameconn());
			    
			    StringBuffer strsqlmain=new StringBuffer();
			    strsqlmain.append("select * from ");
			    strsqlmain.append(userbase);
			    strsqlmain.append("A01 where A0100='");
			    strsqlmain.append(A0100);
			    strsqlmain.append("'");
				rs = ExecuteSQL.executeMyQuery(strsqlmain.toString(),this.getFrameconn(),true);
				if(rs==null||rs.isEmpty())
					throw new GeneralException("","没有该用户权限!","","");
				LazyDynaBean reca01=(LazyDynaBean)rs.get(0);
				String personsort=null;
				if(personsortfield!=null && !"infoself".equalsIgnoreCase(flag))
					personsort=reca01.get(personsortfield.toLowerCase())!=null?reca01.get(personsortfield.toLowerCase()).toString():null;
			    if(!"infoself".equalsIgnoreCase(flag)/*&&personsortfield!=null*/)
				{
			    	infoSetList=new SortFilter().getSortPersonFilterSet(infoSetList,personsort,this.getFrameconn());
			    	infoSetList=new SortFilter().getPersonDBFilterSet(infoSetList, userbase,this.getFrameconn());
			      if(personsortfield!=null)
			    	  infodetailfieldlist=new SortFilter().getSortPersonFilterField(infodetailfieldlist,personsort,this.getFrameconn());
				  infodetailfieldlist=new SortFilter().getPersonDBFilterField(infodetailfieldlist, userbase,this.getFrameconn());
			    }
			   if("1".equalsIgnoreCase(infosort))
			   {
				   ArrayList sortlist=disposeSort(setname,sortname,infodetailfieldlist);//子集分类
				   this.getFormHM().put("sortSetlist", sortlist);
				   this.getFormHM().put("sortname", this.sortname);
				   infodetailfieldlist=sortFieldList(sortlist,infodetailfieldlist);
			  }
			  if(infodetailfieldlist==null||infodetailfieldlist.size()<=0)
			  {
				  infodetailfieldlist=new ArrayList();
				  strsql.append(" and 1=2");
			  }  
			  strsql.append(" order by i9999");
			  rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn(),true);             //获取子集的纪录数据
			  for(int r=0;!rs.isEmpty() && r<rs.size();r++)
			  {
			  	LazyDynaBean rec=(LazyDynaBean)rs.get(r);
			     RecordVo vo=new RecordVo(tablenamesub,1);
			     vo.setString("a0100",rec.get("a0100")!=null?rec.get("a0100").toString():"");				     
			     vo.setInt("i9999",Integer.parseInt(rec.get("i9999").toString()));
			     vo.setString("state",rec.get("state").toString());
			     if(!infodetailfieldlist.isEmpty())                         //字段s
			     {
			     	for(int i=0;i<infodetailfieldlist.size();i++)
			     	{
			     		FieldItem fielditem=(FieldItem)infodetailfieldlist.get(i);
			     		if(!"0".equals(fielditem.getCodesetid()))                 //是否是代码类型的
			     		{
			     			String codevalue=rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"";        //是,转换代码->数据描述	
			     			String codesetid=fielditem.getCodesetid();
			     			if(codevalue !=null && codevalue.trim().length()>0 && codesetid !=null && codesetid.trim().length()>0)
			     			{
			     				if(part_unit!=null&&part_unit.equalsIgnoreCase(fielditem.getItemid().toString())&&part_setid!=null&&part_setid.equalsIgnoreCase(setname))
			     				{
			     					String value=AdminCode.getCode("UN",codevalue)!=null && AdminCode.getCode("UN",codevalue).getCodename()!=null?AdminCode.getCode("UN",codevalue).getCodename():"";
			     					if(value==null||value.length()<=0)
			     						value=AdminCode.getCode("UM",codevalue)!=null && AdminCode.getCode("UM",codevalue).getCodename()!=null?AdminCode.getCode("UM",codevalue).getCodename():"";
			     				    vo.setString(fielditem.getItemid(),value);	
			     				}else
			     				{
			     				  String value=AdminCode.getCodeName(codesetid,codevalue);//AdminCode.getCode(codesetid,codevalue)!=null && AdminCode.getCode(codesetid,codevalue).getCodename()!=null?AdminCode.getCode(codesetid,codevalue).getCodename():"";
			     				  if("UM".equals(codesetid)&&(value==null||value.length()<=0)){
			     						  value=AdminCode.getCodeName("UN", codevalue);
			     				  }
			     				  vo.setString(fielditem.getItemid(),value);
							    }
			     			}	
						    else
						    	vo.setString(fielditem.getItemid(),"");
			     		}else
			     		{
				     		if("D".equals(fielditem.getItemtype()))                               //日期类型的有待格式化处理
				     		{
	                            int itemlen =  fielditem.getItemlength();
	                            String value =rec.get(fielditem.getItemid()).toString();
	                            if ((value !=null) && (value.length()>=itemlen)){
	                                vo.setString(fielditem.getItemid().toLowerCase(),
	                                        new FormatValue().format(fielditem,value.substring(0,itemlen)));
	                            }
	                            else {                                      
	                                vo.setString(fielditem.getItemid().toLowerCase(),""); 
	                            }
				     			///String fi=fielditem.getItemid();
				     			///System.out.println(rec.get(fielditem.getItemid()));
				     			///System.out.println(fielditem.getItemlength());
				     			///System.out.println(rec.get(fielditem.getItemid()).toString().length());
	/*			     			if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==18)
				     				vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,18)));
				     			else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==10)
				     				vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
	                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==4)
	                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
	                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==7)
	                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));
	                            else
	                            	vo.setString(fielditem.getItemid().toLowerCase(),"");*/
				     		}else if("N".equals(fielditem.getItemtype()))                        //数值类型的
				     		{
				     			vo.setString(fielditem.getItemid(),PubFunc.DoFormatDecimal(rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));
				     		}else if("M".equals(fielditem.getItemtype()))
				     		{
				     			String content=rec.get(fielditem.getItemid()).toString();
				     			if(content==null||content.length()<=0)
				     				content="";
				     			content=content.replaceAll("\r\n","<br>");
				     			vo.setString(fielditem.getItemid(),content);
				     		}else                                                               //其他字符串类型
				     		{
				     			vo.setString(fielditem.getItemid(),rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"");
				     		}
			     		}
			     	}
			     }	
			     list.add(vo);
			  }
			  for(int p=0;p<infoSetList.size();p++)
	    		{
	    			FieldSet fieldset=(FieldSet)infoSetList.get(p);
	    			if(setname.equals(fieldset.getFieldsetid()))
	    			{
	    				multimedia_file_flag = fieldset.getMultimedia_file_flag();
	    				//setprv=String.valueOf(fieldset.getPriv_status());
	    				break;
	    			}
	    		}
			 }catch(Exception sqle)
			 {
			   //sqle.printStackTrace();
			   throw GeneralExceptionHandler.Handle(sqle);
			 }
			 finally
			 {
				this.getFormHM().put("multimedia_file_flag", multimedia_file_flag);
				this.getFormHM().put("detailinfolist",list);                         //压回页面
			    this.getFormHM().put("infodetailfieldlist",infodetailfieldlist);
			 }
	    }
	}
   private void setOrgInfo(String userbase,String A0100,ContentDAO dao)
   {
		StringBuffer strsql=new StringBuffer();
		String b0110="";
		String e0122="";
		String e01a1="";
		String a0101="";
		try{
		    strsql.append("select b0110,e0122,e01a1,a0101 from ");
		    strsql.append(userbase);
		    strsql.append("A01 where a0100='");
		    strsql.append(A0100);
		    strsql.append("'");
		    this.frowset = dao.search(strsql.toString()); 
		    if(this.frowset.next())
			{
			     b0110=this.getFrowset().getString("B0110");
			     e0122=this.getFrowset().getString("E0122");
			     e01a1=this.getFrowset().getString("E01A1");
			     a0101=this.getFrowset().getString("a0101");			
			 }
		}catch(Exception e){
			
		}
		finally
		{
			if(b0110 !=null && b0110.trim().length()>0)
				 b0110=AdminCode.getCode("UN",b0110)!=null?AdminCode.getCode("UN",b0110).getCodename():"";
			if(e0122 !=null && e0122.trim().length()>0)
				e0122=AdminCode.getCode("UM",e0122)!=null?AdminCode.getCode("UM",e0122).getCodename():"";
			if(e01a1 !=null && e01a1.trim().length()>0)
				e01a1=AdminCode.getCode("@K",e01a1)!=null?AdminCode.getCode("@K",e01a1).getCodename():"";
		}
	    this.getFormHM().put("b0110",b0110);
  	    this.getFormHM().put("e0122",e0122);
  	    this.getFormHM().put("e01a1",e01a1);//压回页面
  	    this.getFormHM().put("a0101",a0101);
   }
   /**
    * 处理子集的分类
    * @param setname
    * @param sortname
    * @param infodetailfieldlist
    * @return
    */
   private ArrayList disposeSort(String setname,String sortname,List infodetailfieldlist)
   {
	   SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());		
	   ArrayList sortlist=infoxml.getView_tag(setname);
	   if(sortlist==null||sortlist.size()<=0)
		   return null;
	   String n_sortname="";
	   sortname=sortname!=null&&sortname.length()>0?sortname:"";
	   ArrayList list=new ArrayList();
	   CommonData co=null;
	   String viewvalue="";
	   String viewFieldid_t = "";
	   for(int i=0;i<sortlist.size();i++)
	   {
		   n_sortname=sortlist.get(i)!=null?sortlist.get(i).toString():"";
		   if(n_sortname==null||n_sortname.length()<=0)
			   continue;
		   else if(sortname.equalsIgnoreCase(n_sortname))
			   this.sortname=n_sortname;
		   viewvalue=infoxml.getView_value(setname,n_sortname);
		   if(viewvalue==null||viewvalue.length()<=0)
			   continue;
		   
		   viewFieldid_t += viewvalue + ",";
		   for(int r=0;r<infodetailfieldlist.size();r++)
		   {
			   FieldItem fielditem=(FieldItem)infodetailfieldlist.get(r);
			   if(viewvalue.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
			   {
				   co=new CommonData();
				   co.setDataName(n_sortname);
				   co.setDataValue(viewvalue);
				   list.add(co);
				   break;
			   }
		   }
	   }
	   //【61401】默认把没有分组的指标放到最后一个分组中
	   if(list != null && list.size() > 0) {
		   CommonData lastCo = (CommonData) list.get(list.size() - 1);
		   viewvalue = lastCo.getDataValue();
		   if(!viewvalue.endsWith(","))
			   viewvalue += ",";
		   
		   for(int r=0;r<infodetailfieldlist.size();r++) {
			   FieldItem fielditem=(FieldItem)infodetailfieldlist.get(r);
			   if(viewFieldid_t.toLowerCase().indexOf(fielditem.getItemid().toLowerCase()) ==-1) {
				   viewvalue += fielditem.getItemid() + ",";
			   }
		   }
		   
		   if(!viewvalue.endsWith(",")) {
			   viewvalue = viewvalue.substring(0, viewvalue.length() - 1);
		   }
		   
		   lastCo.setDataValue(viewvalue);
	   }
	   
	   return list;
   }
   private List sortFieldList(ArrayList sortlist,List infodetailfieldlist)
   {
            if(sortlist==null||sortlist.size()<=0)
            	return infodetailfieldlist;
            if(this.sortname==null||this.sortname.length()<=0)
            {
            	CommonData co=(CommonData)sortlist.get(0);
            	this.sortname=co.getDataName();
            }
            String name="";
            String viewvalue="";
            for(int i=0;i<sortlist.size();i++)
            {
            	CommonData co=(CommonData)sortlist.get(i);
            	name=co.getDataName();
            	if(name.equalsIgnoreCase(this.sortname))
            	{
            		viewvalue=co.getDataValue();            		
            		break;
            	}
            }
            List newfieldlist=new ArrayList();
            if(viewvalue==null||viewvalue.length()<=0)
            	newfieldlist=infodetailfieldlist;
            else
            {
            	for(int r=0;r<infodetailfieldlist.size();r++)
     		    {
     			   FieldItem fielditem=(FieldItem)infodetailfieldlist.get(r);
     			   if(viewvalue.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
   			       {
     				  newfieldlist.add(fielditem); 
   			       }
            	}
            }
            	
            return newfieldlist;
   }
}
