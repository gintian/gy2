/*
 * Created on 2005-6-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchSelfdetailInfoListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List rs=null;
		String userbase=(String)this.getFormHM().get("userbase");   //人员库
		String setname=(String)this.getFormHM().get("setname");     //主集、子集名
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String flag=(String)hm.get("flag");
		String setprv="";
		cat.debug("setname="+setname);
		String A0100=(String)this.getFormHM().get("a0100");
		String tablename=userbase + setname;                        //操纵表的名称
		StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
	    ArrayList list=new ArrayList();                             //封装子集的数据
		if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
		{
			if("infoself".equalsIgnoreCase(flag))//chen
				A0100=userView.getUserId();                             //如果A0100的值为A0100表示员工资助取其ID
			else
				A0100="-1";  //chenmengqing added for 点新增时，未保存，再点子集时，取得的信息为登录用户的子集信息
		}  //chenmengqing changed at 20061112
		strsql.append("select * from " + tablename);
		strsql.append(" where A0100='" + A0100 + "' order by i9999");
		 List infoSetList=null;
		 List infoFieldList=null;
		if("infoself".equalsIgnoreCase(flag)){
			  infoFieldList=userView.getPrivFieldList(setname,0);   //获得当前子集的所有属性
			  infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);   //获得所有权限的子集
		}else
		{
		    infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
			infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		}	  
	    try
		{
	    	 ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	setOrgInfo(userbase,A0100,dao);
	    	rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());            //获取子集的纪录数据
      	 for(int r=0;!rs.isEmpty() && r<rs.size();r++)
	    {
   	 	     LazyDynaBean rec=(LazyDynaBean)rs.get(r);	
		     RecordVo vo=new RecordVo(tablename,1);
		     vo.setString("a0100",rec.get("a0100")!=null?rec.get("a0100").toString():"");
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
		     				String value=AdminCode.getCode(codesetid,codevalue)!=null && AdminCode.getCode(codesetid,codevalue).getCodename()!=null?AdminCode.getCode(codesetid,codevalue).getCodename():"";
						    vo.setString(fielditem.getItemid(),value);
		     			}	
					    else
					    	vo.setString(fielditem.getItemid(),"");
		     		}else
		     		{
			     		if("D".equals(fielditem.getItemtype()))                               //日期类型的有待格式化处理
    			     	{   int itemlen =  fielditem.getItemlength();
                            String value =rec.get(fielditem.getItemid()).toString();
                            if ((value !=null) && (value.length()>=itemlen)){
                                vo.setString(fielditem.getItemid().toLowerCase(),
                                        new FormatValue().format(fielditem,value.substring(0,itemlen)));
                            }
                            else {                                      
                                vo.setString(fielditem.getItemid().toLowerCase(),""); 
                            }
			     		    
			     		/*	if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==10)
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
			     			String text_m=(String)rec.get(fielditem.getItemid());			     			
			     			if(text_m!=null&&text_m.length()>0)
			     			{
			     				text_m=text_m.replaceAll("\r\n","<br>");	
			     			}
			     							     			
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
      	  /*
			for(int p=0;p<infoSetList.size();p++)
			{
				FieldSet fieldset=(FieldSet)infoSetList.get(p);
				if(setname.equals(fieldset.getFieldsetid()))
				{
					setprv=String.valueOf(fieldset.getPriv_status());
					break;
				}
			}*/
		 }catch(Exception sqle)
		 {
		   sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
		 finally
		 {
		 	this.getFormHM().put("setprv",setprv);
		    this.getFormHM().put("detailinfolist",list);                         //压回页面
		    this.getFormHM().put("infofieldlist",infoFieldList);
		    //this.getFormHM().put("infosetlist",infoSetList);
		 }
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
}
