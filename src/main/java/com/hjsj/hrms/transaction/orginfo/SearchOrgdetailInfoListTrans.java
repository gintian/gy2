/*
 * Created on 2005-7-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

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
public class SearchOrgdetailInfoListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List rs=null;
		String edit_flag=(String)this.getFormHM().get("edit_flag");
		if(edit_flag!=null&& "new".equals(edit_flag))
			throw GeneralExceptionHandler.Handle(new Exception("请先新增单位的主集信息！"));
		HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
		String setname=(String)map.get("setname");     //主集、子集名
		cat.debug("setname="+setname);
		String setprv="0";
		List infoSetList=userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);   //获得所有权限的子集
		String code=(String)this.getFormHM().get("code");
		StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
	    ArrayList list=new ArrayList();                             //封装子集的数据
	    
	    String leader = (String)this.getFormHM().get("leader");
	    String org_m = (String)this.getFormHM().get("org_m");
	    StringBuffer wheresql = new StringBuffer();
	    if("leader".equals(leader) && setname.equalsIgnoreCase(org_m)){
	    	String leaderTypeValue = (String)this.getFormHM().get("leaderTypeValue");
	    	String sessionValue = (String)this.getFormHM().get("sessionValue");
	    	String leaderType  = PubFunc.nullToStr((String)this.getFormHM().get("leaderType"));
	    	FieldItem lfi = DataDictionary.getFieldItem(leaderType);
	    	String sessionitem = PubFunc.nullToStr((String)this.getFormHM().get("sessionitem"));
	    	ArrayList sessionitemList = new ArrayList();
	    	StringBuffer itemString = new StringBuffer();
	    	getSessionItemList(sessionitemList,sessionitem,setname,code,itemString);
	    	if(itemString.indexOf(sessionValue+",") == -1 && !"all".equals(sessionValue))
	    		sessionValue="max";
	    	FieldItem sfi = DataDictionary.getFieldItem(sessionitem);
	    	if(lfi!=null && "1".equals(lfi.getUseflag()) && !"all".equals(leaderTypeValue))
	    		wheresql.append(" and "+leaderType+" ='"+leaderTypeValue+"' ");
	    	if(sfi!=null && "1".equals(sfi.getUseflag()) && !"all".equals(sessionValue) && sessionitemList.size()>1){
	    		if("max".equals(sessionValue))
	    			sessionValue = ((CommonData)sessionitemList.get(1)).getDataValue();
	    		wheresql.append(" and "+sessionitem+" = '"+sessionValue+"' ");
	    	}
	    	
	    		this.getFormHM().put("leaderTypeValue", leaderTypeValue);
	    		this.getFormHM().put("sessionValue", sessionValue);
	    		this.getFormHM().put("sessionitemList", sessionitemList);
	    }
	    //add by wangchaoqun on 2014-9-11 begin 参数code整体加密
	    String encryptParam = PubFunc.encrypt("code="+code);
	    this.getFormHM().put("encryptParam", encryptParam);
	    //add by wangchaoqun on 2014-9-11 end 
	   	strsql.append("select * from " + setname);
		strsql.append(" where B0110='" + code + "' ");
		strsql.append(wheresql.toString());
		strsql.append(" order by i9999");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
	    boolean isWrite=false;
	    try
		{	     
	    	rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.frameconn,true);            //获取子集的纪录数据
	    	 for(int r=0;!rs.isEmpty() && r<rs.size();r++)
		  {
	    	 	LazyDynaBean rec=(LazyDynaBean)rs.get(r);
		     RecordVo vo=new RecordVo(setname,1);
		    // System.out.println("vo " + this.getFrowset().getInt("i9999"));
		     vo.setString("b0110",rec.get("b0110")!=null?rec.get("b0110").toString():"");
		     vo.setInt("i9999",Integer.parseInt(rec.get("i9999")!=null?rec.get("i9999").toString():"0"));
		     if(!infoFieldList.isEmpty())                         //字段s
		     {
		     	for(int i=0;i<infoFieldList.size();i++)
		     	{
		     		FieldItem fielditem=(FieldItem)infoFieldList.get(i);
		     		if(fielditem.getPriv_status()==2&&fielditem.getItemid().indexOf("z0")==-1&&fielditem.getItemid().indexOf("z1")==-1)
					{
		     			isWrite=true;
					}
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
	    	 //System.out.println("dfds");
	    	 setprv=getEditSetPriv(infoSetList,infoFieldList,setname);
		 }catch(Exception sqle)
		 {
		   sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
		 finally
		 {
		 	this.getFormHM().put("setprv",setprv);
		 	//System.out.println("field.size() " + infoFieldList.size());
		    this.getFormHM().put("detailinfolist",list);                         //压回页面
		    this.getFormHM().put("infofieldlist",infoFieldList);
		 }
		 if(isWrite)
			 this.getFormHM().put("isWrite", "1");//子集有写权限指标
		 else
			 this.getFormHM().put("isWrite", "0");
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
	
	private void getSessionItemList(ArrayList sessionitemList,String sessionitem,String setName,String code,StringBuffer itemString){
		FieldItem fi = DataDictionary.getFieldItem(sessionitem);
		if(fi==null || "0".equals(fi.getUseflag())){
			return ;
		}
		
		sessionitemList.add(new CommonData("all", "全部"));
		String sql = "select "+sessionitem+" from "+setName +" where b0110='"+code+"' and "+Sql_switcher.length(sessionitem)+">0 group by "+sessionitem+" order by "+sessionitem+" desc";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				itemString.append(this.frowset.getString(sessionitem)+",");
				CommonData cd = new CommonData(this.frowset.getString(sessionitem),this.frowset.getString(sessionitem));
				sessionitemList.add(cd);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
