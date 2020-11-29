/*
 * Created on 2005-6-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchSelfdetailSortInfoListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List rs=null;
		String userbase=(String)this.getFormHM().get("userbase");   //人员库
		String setname=(String)this.getFormHM().get("setname");     //主集、子集名
		String personsort=(String)this.getFormHM().get("personsort");
		String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String flag=(String)hm.get("flag");
		String setprv="";
		String multimedia_file_flag = "";
		cat.debug("setname="+setname);
		String A0100 = (String)this.getFormHM().get("a0100");
		
		if("infoself".equalsIgnoreCase(flag))
		{
		    A0100 = this.userView.getA0100();
		    userbase = this.userView.getDbname();
		}
		
		if(!"infoself".equalsIgnoreCase(flag)&&null != A0100 && !"".equals(A0100)&&!"A0100".equalsIgnoreCase(A0100)){
			CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
			userbase = cps.checkDb(userbase);
			A0100 = cps.checkA0100("", userbase, A0100, "");
		}
		
		String tablename=userbase + setname;                        //操纵表的名称
		StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
	    ArrayList list=new ArrayList();                             //封装子集的数据
	    ContentDAO connDao=new ContentDAO(this.getFrameconn());
	    String isAble =this.isAble(userbase, A0100, connDao);
	    this.getFormHM().put("isAble", isAble);
		if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
		{
			if("infoself".equalsIgnoreCase(flag))//chen
				A0100=userView.getUserId();                             //如果A0100的值为A0100表示员工资助取其ID
			else
				A0100="-1";  //chenmengqing added for 点新增时，未保存，再点子集时，取得的信息为登录用户的子集信息
		}  //chenmengqing changed at 20061112
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		strsql.append("select * from " + tablename);
		strsql.append(" where A0100='" + A0100 + "'");
		List infoSetList=null;
		List infoFieldList=null;
		
		String fenlei_priv=(String)this.getFormHM().get("fenlei_priv");//人员分类设置
		InfoUtils infoUtils=new InfoUtils();
		String sub_type=infoUtils.getOneselfFenleiType(userbase, A0100, fenlei_priv, dao);//人员分类
		if("infoself".equalsIgnoreCase(flag)){
		    /*
             * 按分类授权获取到的子集/指标没有区分是不是员工角色特征下的，因此分类授权只能在业务上实现；
             * 自助服务员工信息要使用分类授权的话，分类授权只能在员工角色特征下的角色授权，
             * 其它地方的不能进行子集/指标的分类授权否则会显示全部的分类授权的子集/指标
             */
		    if(sub_type!=null&&sub_type.length()>0) {
		        //得到分类授权子集
		        infoFieldList=infoUtils.getSubPrivFieldList(this.userView,setname,sub_type, 1);
		        infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET, 1);
		        //如果分类中得不到指标则用默认权限的
		        if(infoFieldList==null||infoFieldList.size()<=0)
		            //获得当前子集的所有属性
		            infoFieldList = userView.getPrivFieldList(setname, 0);
		        //获得所有权限的子集
		        if(infoSetList==null||infoSetList.size()<=0)
		            infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
                    
            } else {
                // 获得当前子集的所有属性
                infoFieldList = userView.getPrivFieldList(setname, 0);
                // 获得所有权限的子集
                infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
            }
		}else {
			if(sub_type!=null&&sub_type.length()>0) {
				//得到分类授权子集
				infoFieldList=infoUtils.getSubPrivFieldList(this.userView,setname,sub_type);
				infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET);
				if(infoFieldList==null||infoFieldList.size()<=0)//如果分类中得不到指标则用默认权限的
					infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
				if(infoSetList==null||infoSetList.size()<=0)
					infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
					
			}else {
		       infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
			   infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
			}
		}	  
	    try
		{
	    	if(!"infoself".equalsIgnoreCase(flag))
			{
	    	  infoSetList=new SortFilter().getSortPersonFilterSet(infoSetList,personsort,this.getFrameconn());
			  infoFieldList=new SortFilter().getSortPersonFilterField(infoFieldList,personsort,this.getFrameconn());
			  
			  infoSetList=new SortFilter().getPersonDBFilterSet(infoSetList, userbase,this.getFrameconn());
			  infoFieldList=new SortFilter().getPersonDBFilterField(infoFieldList, userbase,this.getFrameconn());
			}
	    	
	    	setOrgInfo(userbase,A0100,dao);
	    	if(infoFieldList==null||infoFieldList.size()<=0)
			{
	    		infoFieldList=new ArrayList();
				  strsql.append(" and 1=2");
			}  
			strsql.append(" order by i9999");
       
	    	rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn(),true); 
            
//	    	rs=dao.searchDynaList(strsql.toString());
	    	//获取子集的纪录数据
      	 for(int r=0;!rs.isEmpty() && r<rs.size();r++)
	     {
   	 	     LazyDynaBean rec=(LazyDynaBean)rs.get(r);	
		     RecordVo vo=new RecordVo(tablename,1);
		     vo.setString("a0100",rec.get("a0100")!=null?rec.get("a0100").toString():"");	
		     String i99=rec.get("i9999")!=null?rec.get("i9999").toString():"0";
		     if(i99.indexOf(".")!=-1)
		       vo.setInt("i9999",Integer.parseInt(rec.get("i9999")!=null?rec.get("i9999").toString().substring(0,rec.get("i9999").toString().indexOf(".")):""));
		     else
		       vo.setInt("i9999",Integer.parseInt(rec.get("i9999")!=null?rec.get("i9999").toString():"")); 
		     vo.setString("state",rec.get("state")!=null?rec.get("state").toString():"");
		     if(!infoFieldList.isEmpty())                         //字段s
		     {
		     	for(int i=0;i<infoFieldList.size();i++)
		     	{
		     		FieldItem fielditem=(FieldItem)infoFieldList.get(i);
		     		if(!"0".equals(fielditem.getCodesetid()))                 //是否是代码类型的
		     		{
		     			String codevalue=rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"";        //是,转换代码->数据描述	       //是,转换代码->数据描述	
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
		     					String value=AdminCode.getCode(codesetid,codevalue)!=null && AdminCode.getCode(codesetid,codevalue).getCodename()!=null?AdminCode.getCode(codesetid,codevalue).getCodename():"";
		     					if(value.length()==0&&"UM".equalsIgnoreCase(codesetid))
							    	value=AdminCode.getCode("UN",codevalue)!=null && AdminCode.getCode("UN",codevalue).getCodename()!=null?AdminCode.getCode("UN",codevalue).getCodename():"";

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
                                //业务字典中日期格式为年月日时分秒时，保存的指标长度为18；实际长度为19；此处特殊处理
                                if(itemlen == 18)
                                    itemlen = 19;
                                
                                vo.setString(fielditem.getItemid().toLowerCase(),
                                        new FormatValue().format(fielditem,value.substring(0,itemlen)));
                            }
                            else {                                      
                                vo.setString(fielditem.getItemid().toLowerCase(),""); 
                            }
                            
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
			     			vo.setString(fielditem.getItemid(),PubFunc.DoFormatDecimal2(rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));
			     		}else if("M".equals(fielditem.getItemtype()))
			     		{
			     			String text_m=(String)rec.get(fielditem.getItemid());	
			     			vo.setString(fielditem.getItemid(),text_m);
			     		}else                                                               //其他字符串类型
			     		{
			     			vo.setString(fielditem.getItemid(),rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"");
			     		}
		     		}
		     	}
		     }	
		     list.add(vo);
		  }
      	  /**对子集的修改权限分析chenmengqing added at 20051017*/ 
      	  setprv=getEditSetPriv(infoSetList,infoFieldList,setname);
      	  
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
            VersionControl ver_ctrl = new VersionControl();
            if(!ver_ctrl.searchFunctionId("03040110")){ 
               multimedia_file_flag="";   
           }
            
            String virAxx = SystemConfig.getPropertyValue("virtualOrgSet");
            virAxx = StringUtils.isEmpty(virAxx) ? "" : virAxx; 
            this.getFormHM().put("virAxx", virAxx);
		 }catch(Exception sqle)
		 {
		   sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
		 finally
		 {
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 	String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		 	inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
		 	String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		 	approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
			 
		 	this.getFormHM().put("multimedia_file_flag", multimedia_file_flag);
		 	this.getFormHM().put("setprv",setprv);
		    this.getFormHM().put("detailinfolist",list);                         //压回页面
		    this.getFormHM().put("infofieldlist",infoFieldList);
		    if("1".equals(inputchinfor)&& "1".equals(approveflag)){
		    	this.getFormHM().put("inputchinfor","1");
		    }else{
		    	this.getFormHM().put("inputchinfor","0");
		    }
		 }
		 this.getFormHM().put("emp_cardId",searchCard("1"));
	}
	
	/**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(List infoSetList,List infoFieldList,String setname)
	{
		String setpriv="0";
		boolean bflag=false;
		/**先根据子集分析*/
		for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
			{
				setpriv=String.valueOf(fieldset.getPriv_status());
				break;
			}
		}	
		if("2".equals(setpriv))
			return setpriv;		
		/**分析指标*/
		for(int i=0;i<infoFieldList.size();i++)                            //字段的集合
		{
		    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
		    if(fieldItem.getPriv_status()==2)
		    {
		    	bflag=true;
		    	break;
		    }
		}
		/**子集仅读权限，指标有写权限时返回值为3*/
		if(bflag)
			return "3";
		else
			return setpriv;
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
	 private String searchCard(String infortype)
	    {
			 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			 String cardid="-1";
			 try
			 {
				 if("1".equalsIgnoreCase(infortype))
				 {
					 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
				 }
				 if("2".equalsIgnoreCase(infortype))
				 {
					 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
				 }
				 if("3".equalsIgnoreCase(infortype))
				 {
					 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
				 }
				 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
					 cardid="-1";
			 }
			 catch(Exception ex)
			 {
				 ex.printStackTrace();
			 }
			 return cardid;
	    }
	 private String isAble(String nbase,String a0100,ContentDAO dao) {
		 String isAble = "1";
		 String sql = "select * from t_hr_mydata_chg where nbase='"+nbase+"' and a0100='"+a0100+"' and sp_flag='02'";
		 RowSet rs=null;
		 try {
			 rs = dao.search(sql);
			if (rs.next()) {
				isAble = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		 return isAble;
	 }
}
