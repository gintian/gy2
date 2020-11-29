/*
 * Created on 2005-7-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dutyinfo;

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
public class SearchDutydetailInfoListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List rs=null;
		String edit_flag=(String)this.getFormHM().get("edit_flag");
		if(edit_flag!=null&& "new".equals(edit_flag))
			throw GeneralExceptionHandler.Handle(new Exception("请先新增岗位的主集信息！"));
		HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
		String setname=(String)map.get("setname");     //主集、子集名
		
		String code=(String)this.getFormHM().get("code");
		StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
	    ArrayList list=new ArrayList();    
		String setprv="0";//封装子集的数据
	   	strsql.append("select * from " + setname);
		strsql.append(" where E01A1='" + code + "'");
		strsql.append(" order by i9999");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
		List infoSetList=userView.getPrivFieldSetList(Constant.POS_FIELD_SET);   //获得所有权限的子集
	    try
		{	     
	      	rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.frameconn);          //获取子集的纪录数据
		 for(int r=0;!rs.isEmpty() && r<rs.size();r++)
		  {
		 	LazyDynaBean rec=(LazyDynaBean)rs.get(r);
		     RecordVo vo=new RecordVo(setname,1);
		    // System.out.println("vo " + this.getFrowset().getInt("i9999"));
		     vo.setString("e01a1",rec.get("e01a1")!=null?rec.get("e01a1").toString():"");
		     vo.setInt("i9999",Integer.parseInt(rec.get("i9999")!=null?rec.get("i9999").toString():"0"));
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
			     		{
			     			if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==10)
			     				vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==4)
                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==7)
                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));
                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==16)// liuy 2015-4-24 9053
                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,16)));
                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==18)
                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,18)));
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
		/*	for(int p=0;p<infoSetList.size();p++)
			{
				FieldSet fieldset=(FieldSet)infoSetList.get(p);
				if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
				{
					setprv=String.valueOf(fieldset.getPriv_status());
					break;
				}
			}*/
		 setprv=getEditSetPriv(infoSetList,infoFieldList,setname);
		 }catch(Exception sqle)
		 {
		   //sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
		 finally
		 {
		 	this.getFormHM().put("setprv",setprv);
		 	//System.out.println("field.size() " + infoFieldList.size());
		    this.getFormHM().put("detailinfolist",list);                         //压回页面
		    this.getFormHM().put("infofieldlist",infoFieldList);
		   
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
		if(bflag)
			return "3";
		else
			return setpriv;
	}
}
