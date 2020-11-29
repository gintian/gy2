/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.statics;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Owner
 *
 */
public class SearchAnalyseDetailInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		List rs=null;
		// TODO Auto-generated method stub
    	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
    	String flag=(String)hm.get("flag");
		if(!("infoself".equalsIgnoreCase(flag) && userView.getStatus()!=4))
		{
			String dbpre=(String)this.getFormHM().get("dbpre");//人员库
			String A0100=(String)this.getFormHM().get("a0100");                       //获得人员ID
			if("A0100".equals(A0100))
				A0100=userView.getUserId();
	       
	    	String setname=(String)this.getFormHM().get("setname");     //子集名
	  		cat.debug("setname="+setname);
				String tablenamesub=dbpre + setname;                        //操纵表的名称
				StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
			    ArrayList list=new ArrayList();                             //封装子集的数据
				strsql.append("select * from " + tablenamesub);
				strsql.append(" where A0100='" + A0100 + "'");
			    ContentDAO dao=new ContentDAO(this.getFrameconn());
			    if(!"A01".equals(setname))
			    {
			    	setOrgInfo(dbpre,A0100,dao);
			   	    List infodetailfieldlist=null;
			 	    try
					{	 
			 	     
					  if("infoself".equalsIgnoreCase(flag))
					 	  infodetailfieldlist= userView.getPrivFieldList(setname,0);   //获得当前子集的所有属性
				      else
					      infodetailfieldlist= userView.getPrivFieldList(setname); 	
					  rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());             //获取子集的纪录数据
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
					     				
					     				String value=AdminCode.getCodeName(codesetid,codevalue);//AdminCode.getCode(codesetid,codevalue)!=null && AdminCode.getCode(codesetid,codevalue).getCodename()!=null?AdminCode.getCode(codesetid,codevalue).getCodename():"";
									    vo.setString(fielditem.getItemid(),value);
					     			}	
								    else
								    	vo.setString(fielditem.getItemid(),"");
					     		}else
					     		{
						     		if("D".equals(fielditem.getItemtype()))                               //日期类型的有待格式化处理
						     		{
						     			///String fi=fielditem.getItemid();
						     			///System.out.println(rec.get(fielditem.getItemid()));
						     			///System.out.println(fielditem.getItemlength());
						     			///System.out.println(rec.get(fielditem.getItemid()).toString().length());
						     			if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==10)
						     				vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
			                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==4)
			                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
			                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==7)
			                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));
			                            else
			                            	vo.setString(fielditem.getItemid().toLowerCase(),"");
						     		}else if("N".equals(fielditem.getItemtype()))                        //数值类型的
						     		{
						     			vo.setString(fielditem.getItemid(),PubFunc.DoFormatDecimal(rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));
						     		}else                                                               //其他字符串类型
						     		{
						     			vo.setString(fielditem.getItemid(),rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"");
						     		}
					     		}
					     	}
					     }	
					     list.add(vo);
					  }
					 }catch(Exception sqle)
					 {
					   sqle.printStackTrace();
					   throw GeneralExceptionHandler.Handle(sqle);
					 }
					 finally
					 {
					    this.getFormHM().put("detailinfolist",list);                         //压回页面
					    this.getFormHM().put("infodetailfieldlist",infodetailfieldlist);
					 }
			    }
		}
		else
		{
			throw new GeneralException("","非自助平台用户!","","");
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

}
