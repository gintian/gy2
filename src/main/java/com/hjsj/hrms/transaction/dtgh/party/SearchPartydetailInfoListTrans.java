/*
 * Created on 2005-7-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xujian
 *Jan 21, 2010
 */
public class SearchPartydetailInfoListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List rs=null;
		String edit_flag=(String)this.getFormHM().get("type");
		String param = (String)this.getFormHM().get("param");
		if(edit_flag!=null&& "add".equals(edit_flag))
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("dtgh.party."+param+".query"),"",""));
		//HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String fieldsetid=(String)this.getFormHM().get("fieldsetid");     //子集名
		String key = fieldsetid.substring(0,1).toLowerCase()+"0100";
		List infofieldlist=DataDictionary.getFieldList(fieldsetid, 1);   //获得子集指标
		String codeitemid = (String)this.getFormHM().get("codeitemid");
		StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
	    ArrayList list=new ArrayList();                             //封装子集的数据
	   	strsql.append("select * from " + fieldsetid);
		strsql.append(" where "+key+"='" + codeitemid + "' order by i9999");
	    try
		{	     
	    	rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.frameconn);            //获取子集的纪录数据
	    	 for(int r=0;!rs.isEmpty() && r<rs.size();r++)
		  {
	    	 	LazyDynaBean rec=(LazyDynaBean)rs.get(r);
		     RecordVo vo=new RecordVo(fieldsetid.toLowerCase(),1);
		    // System.out.println("vo " + this.getFrowset().getInt("i9999"));
		     vo.setString(key,(String)rec.get(key));
		     Object gg=rec.get("i9999");
		     vo.setInt("i9999",Integer.parseInt(rec.get("i9999")!=null?rec.get("i9999").toString():"1"));
		     if(!infofieldlist.isEmpty())                         //字段s
		     {
		     	for(int i=0;i<infofieldlist.size();i++)
		     	{
		     		FieldItem fielditem=(FieldItem)infofieldlist.get(i);
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
			     		{
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
		   //sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
		 finally
		 {
		    this.getFormHM().put("list",list);                         //压回页面
		    this.getFormHM().put("infofieldlist",infofieldlist);
		 }
	}
}
