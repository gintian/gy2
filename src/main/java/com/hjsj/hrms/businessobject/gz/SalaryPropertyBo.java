package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 操作工资属性类别面板对象
 *<p>Title:SalaryPropertyBo.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 29, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SalaryPropertyBo {
	private Connection conn=null;
	/**薪资类别
	 *=0 薪资 
	 *=1 保险
	 */
	private int gz_type=0;
	private String salaryid="";
	/**薪资控制参数*/
	private SalaryCtrlParamBo ctrlparam=null;
	private RecordVo vo=null;
	private String unit_type="1"; //单位性质 =1,国家机关 =2,事业单位	=3,企业单位 =4,军队使用	=5,其    它
	private UserView userView=null;
	
	public SalaryPropertyBo(Connection con )
	{
		this.conn=con;
	}
	
	public SalaryPropertyBo(Connection con,String salaryid,int gz_type,UserView userView)
	{
		try
		{
			this.conn=con;
			this.salaryid=salaryid;
			this.gz_type=gz_type;
			ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(this.salaryid));
			vo=getRecordVo();
			this.userView=userView;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			if(sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type")!=null&&sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type").length()>0)
				this.unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取工资所有邮件模板
	 * @return
	 */
	public ArrayList getEmailTemplateList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select id,name from email_name where nmodule=2 order by id";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			list.add(new CommonData(" ",ResourceFactory.getProperty("label.select.dot")));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("id"),rs.getString("name")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getEmailTemplateListByNmodule(String nmodule,int type)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select id,name from email_name where nmodule="+nmodule+" order by id";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			list.add(new CommonData(" ",ResourceFactory.getProperty("label.select.dot")));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("id"),rs.getString("name")));
			}
			if(type==1)
			{
				list.add(new CommonData("createnew",ResourceFactory.getProperty("label.gz.new")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 取得薪资项目指标列表
	 * @param type  D：日期  A:字符型  N:数字型
	 * @param codesetid
	 * @return
	 */
	public ArrayList getSalarySetList(String type,String codesetid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from salaryset where salaryid="+this.salaryid+" and itemtype='"+type+"' ";
			if("A".equalsIgnoreCase(type)&&codesetid.length()>0)
				sql+=" and codesetid='"+codesetid+"'";
			
			RowSet rowSet=dao.search(sql);
			list.add(new CommonData("",""));
			while(rowSet.next())
			{
				CommonData data=new CommonData(rowSet.getString("itemid"),rowSet.getString("itemdesc"));
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

    /**   
     * @Title: getFiterItemList   
     * @Description:  //获取共享过滤项目  
     * @param @param fiterItemIds     * 
     * @param @return 
     * @return ArrayList    
     * @author wangrd  
     * @throws   
    */
    public ArrayList getFiterItemList(String fiterItemIds)
    { ArrayList list=new ArrayList();
        try
        {
            StringBuffer buf=new StringBuffer();
            buf.append("select id,chz,cfldname,scope,username from gzitem_filter where ");
            if(fiterItemIds==null|| "".equals(fiterItemIds))
            {
                buf.append(" 1=2 ");
            }
            else
            {
                buf.append("id in (");
                buf.append(fiterItemIds);
                buf.append(")");
            }
            buf.append(" and scope =0 order by norder ");//共享的
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rset=dao.search(buf.toString());
            list.add(new CommonData("",""));
            while(rset.next()){         
                CommonData data=new CommonData(rset.getString("id"),rset.getString("chz"));
                list.add(data);
            
            }  
            rset.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return list;
        
    }
	
	
	/**
	 * 取得审批关系列表
	 * @return
	 */
	public ArrayList getSpRelationList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			DbWizard dbw = new DbWizard(this.conn);
			if(dbw.isExistTable("t_wf_relation",false))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				String sql="select * from t_wf_relation where validflag=1 and actor_type='4'";
				
				RowSet rowSet=dao.search(sql);
				list.add(new CommonData("",""));
				while(rowSet.next())
				{
					CommonData data=new CommonData(rowSet.getString("relation_id"),rowSet.getString("cname"));
					list.add(data);
				}
				if(rowSet!=null)
					rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	
	/**
	 * 取得变动模板列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getVaryModelList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
		    String templateIDs=","+ctrlparam.getValue(SalaryCtrlParamBo.TEMPLATE).toLowerCase()+",";
		    String a_static="";
		    switch(this.gz_type) 
		    {
		    	case 0:
		    		a_static="2";
		    		break;
		    	case 1:
		    		a_static="8";
		    		break;
		    }
		    String _static="static";
		    if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
		    	_static="static_o";
		    }
		    String sql="select tabId, name from template_table  where ("+_static+" ="+a_static+")   order by tabId"; // and Flag ="+this.unit_type+"
		    RowSet rowSet=dao.search(sql);
		    LazyDynaBean abean=null;
		    while(rowSet.next())
		    {
		    	String tabId=rowSet.getString("tabId");
				String name=rowSet.getString("name");
				String isSelected="0";
				if(templateIDs.indexOf(","+tabId.toLowerCase()+",")!=-1)
					isSelected="1";
				abean=new LazyDynaBean();
				abean.set("tabId",tabId);
				abean.set("name",name);
				abean.set("isSelected",isSelected);
				list.add(abean);
		    }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	
	/**
	 * 保存/修改 标准类别属性
	 * @param salaryid
	 * @param dbValue
	 * @param personScope
	 * @param moneyType
	 * @param varyModelValue
	 * @param calculateTaxTime
	 * @param appealTaxTime
	 * @param sendSalaryItem
	 * @param taxType
	 * @param ratepayingDecalre
	 * @throws GeneralException
	 */
	public void saveStandardProperty(String gz_module,String flow_ctrl,String piecerate,String condStr,String cexpr,String salaryid,String[] dbValue,String personScope,String moneyType,String[] varyModelValue,
			String calculateTaxTime,String appealTaxTime,String sendSalaryItem,String taxType,String ratepayingDecalre,String amount_ctrl,String priv_mode,String manager
			,String smsNotice,String mailNotice,String templateId,String a01z0Flag,String bonusItem,String orgid,String deptid,String layer,String sum_type,String reject_mode,String verify_ctrl,LazyDynaBean paramBean,String lsDept,String field_priv,String collect_je_field,HashMap propertyMap,ArrayList list)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer cbase=new StringBuffer("");
			for(int i=0;i<dbValue.length;i++)
			{
				if(!"-1".equals(dbValue[i]))
					cbase.append(dbValue[i]+",");
			}
			
			
			String verify_ctrl_ff=(String)paramBean.get("verify_ctrl_ff");
			String verify_ctrl_sp=(String)paramBean.get("verify_ctrl_sp");
			String amount_ctrl_ff=(String)paramBean.get("amount_ctrl_ff");
			String amount_ctrl_sp=(String)paramBean.get("amount_ctrl_sp");
			String ctrl_type = (String)paramBean.get("ctrl_type");
			String read_field=(String)paramBean.get("read_field");
	    //	RecordVo vo=new RecordVo("salarytemplate");
	    //	vo.setInt("salaryid",Integer.parseInt(salaryid));
	    //	vo=dao.findByPrimaryKey(vo);
        //	vo.setString("cbase",cbase.toString());
		//	if(moneyType.length()>0)
		//	    vo.setInt("nmoneyid",Integer.parseInt(moneyType));
		//	vo.setString("cond",condStr);
		//	vo.setString("cexpr",cexpr);
		 //	dao.updateValueObject(vo);
			
			StringBuffer sql=new StringBuffer("update salarytemplate set cbase=?");
			if(moneyType.length()>0)
				sql.append(",nmoneyid=?");
			else 
				sql.append(",nmoneyid=null");//当货币种类为空时，sli
			sql.append(",cond=?,cexpr=? where salaryid=?");
			ArrayList paramList = new ArrayList();
			paramList.add(cbase.toString());
			if(moneyType.length()>0)
			{
				paramList.add(Integer.parseInt(moneyType));
			}
			paramList.add(condStr);
			paramList.add(cexpr);
			paramList.add(Integer.parseInt(salaryid));
			dao.update(sql.toString(),paramList);
			this.ctrlparam.setValue(SalaryCtrlParamBo.COND_MODE,"flag",personScope);
			StringBuffer varyModel=new StringBuffer("");
			if(varyModelValue!=null)
			{
				for(int i=0;i<varyModelValue.length;i++)
				{
					if(!"-1".equals(varyModelValue[i]))
						varyModel.append(","+varyModelValue[i]);
				}
			}
			if(varyModel.length()>0){
				this.ctrlparam.setValue(SalaryCtrlParamBo.TEMPLATE,varyModel.substring(1));
			}else{
				this.ctrlparam.setValue(SalaryCtrlParamBo.TEMPLATE,"");
			}
			this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"flag",flow_ctrl);
			
			if(flow_ctrl!=null&& "1".equals(flow_ctrl))
			{
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode",reject_mode);
				String sp_relation_id=(String)propertyMap.get("sp_relation_id");
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_relation_id",sp_relation_id);
				String default_filterid=(String)propertyMap.get("default_filterid");
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"default_filterid",default_filterid);
				
			}
			else
			{
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode","1");
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_relation_id","");				
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_default_filter_id","");	
				String str = "update t_hr_pendingtask set Pending_status='4' where (Pending_status='0' or Pending_status='3') and ext_flag like '%_"+salaryid+"'";//设置成不需要审批，把当前薪资类别的待办全置成无效 zhaoxg add 2015-4-13
				dao.update(str);
			}
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "setid",(String)propertyMap.get("royalty_setid"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "date",(String)propertyMap.get("royalty_date"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "period",(String)propertyMap.get("royalty_period"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "relation_fields",(String)propertyMap.get("royalty_relation_fields"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "strExpression",(String)propertyMap.get("strExpression"));
			//根据gz_module判断是薪资还是保险，0:薪资，1:保险，lis添加
			if("0".equals(gz_module)){
				this.ctrlparam.setValue(SalaryCtrlParamBo.AMOUNT_CTRL, "amount_ctrl_ff",amount_ctrl_ff);
				this.ctrlparam.setValue(SalaryCtrlParamBo.AMOUNT_CTRL, "amount_ctrl_sp",amount_ctrl_sp);
				this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "valid",(String)propertyMap.get("priecerate_valid"));
				this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "valid",(String)propertyMap.get("royalty_valid"));
				this.ctrlparam.setValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag", amount_ctrl);
				this.ctrlparam.setValue(SalaryCtrlParamBo.AMOUNT_CTRL, "ctrl_type",ctrl_type);
			}
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "period",(String)propertyMap.get("priecerate_zhouq1"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "firstday",(String)propertyMap.get("priecerate_str"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "strExpression",(String)propertyMap.get("priecerate_expression_str"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "relation_field",(String)propertyMap.get("priecerate_zhibiao"));
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.VERIFY_CTRL,verify_ctrl);
			this.ctrlparam.setValue(SalaryCtrlParamBo.VERIFY_CTRL, "verify_ctrl_ff",verify_ctrl_ff);
			this.ctrlparam.setValue(SalaryCtrlParamBo.VERIFY_CTRL, "verify_ctrl_sp",verify_ctrl_sp);
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECERATE,piecerate);
			this.ctrlparam.setValue(SalaryCtrlParamBo.TAX_DATE_FIELD,calculateTaxTime);
			this.ctrlparam.setValue(SalaryCtrlParamBo.DECLARE_TAX,appealTaxTime);
			this.ctrlparam.setValue(SalaryCtrlParamBo.PAY_FLAG,sendSalaryItem);
			this.ctrlparam.setValue(SalaryCtrlParamBo.TAX_MODE,taxType);
			this.ctrlparam.setValue(SalaryCtrlParamBo.TAX_DESC,ratepayingDecalre);

			if(priv_mode!=null&&priv_mode.trim().length()>0)
		    	this.ctrlparam.setValue(SalaryCtrlParamBo.PRIV_MODE, "flag", priv_mode);
			this.ctrlparam.setValue(SalaryCtrlParamBo.SHARE_SET, "user", manager);
			this.ctrlparam.setValue(SalaryCtrlParamBo.NOTE, templateId);
			this.ctrlparam.setValue(SalaryCtrlParamBo.NOTE,"sms", smsNotice);
			this.ctrlparam.setValue(SalaryCtrlParamBo.NOTE, "mail",mailNotice);
			this.ctrlparam.setValue(SalaryCtrlParamBo.A01Z0,"flag",a01z0Flag);
			this.ctrlparam.setValue(SalaryCtrlParamBo.BONUS, bonusItem);
			this.ctrlparam.setValue(SalaryCtrlParamBo.SUM_FIELD, "orgid",orgid);
			this.ctrlparam.setValue(SalaryCtrlParamBo.SUM_FIELD, "deptid",deptid);
			this.ctrlparam.setValue(SalaryCtrlParamBo.SUM_FIELD, "layer",layer);
			//this.ctrlparam.setValue(SalaryCtrlParamBo.SUM_FIELD, "sum_type",sum_type);
			this.ctrlparam.setValue(SalaryCtrlParamBo.LS_DEPT, lsDept);
			this.ctrlparam.setValue(SalaryCtrlParamBo.FIELD_PRIV, field_priv);
			this.ctrlparam.setValue(SalaryCtrlParamBo.COLLECT_JE_FIELD, collect_je_field);
			this.ctrlparam.setValue(SalaryCtrlParamBo.READ_FIELD, read_field);
			this.ctrlparam.saveParameter();
			list.add(this.ctrlparam.getDoc1());
			list.add(this.ctrlparam.getXml2());
			if(manager!=null&&manager.trim().length()>0)
			{
				dao.update("update salarytemplate set nflag=1 where salaryid="+this.salaryid);
			}
			else
			{
				dao.update("update salarytemplate set nflag=0 where salaryid="+this.salaryid);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 薪资类别中属性变化的具体内容
	 * anthor zhaoxg 2015-4-22
	 * @param value xml路径 即为具体参数的标识
	 * @param str1 变化前值
	 * @param str2 变化后值
	 * @param list 对象list，带着一只走的
	 */
	public void getChangeList(String value,String str1,String str2,ArrayList list){
		if(!str1.equalsIgnoreCase(str2)){
			HashMap changeMap = new HashMap();//变化前后值分别对应map中的键和值
			HashMap valueMap = new HashMap();//键为具体参数标识，值为changeMap
			changeMap.put(str1, str2);
			valueMap.put(value, changeMap);
			list.add(valueMap);
		}
	}
	
	
	/**
	 * 得到标准类别当前纪录信息
	 * @return
	 * @throws GeneralException
	 */
	public RecordVo getRecordVo()throws GeneralException
	{
		RecordVo v=new RecordVo("salarytemplate");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			v.setInt("salaryid",Integer.parseInt(this.salaryid));
			v=dao.findByPrimaryKey(v);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return v;
	}
	
	
	
	
	
	
	/**
	 * 获得币种类型列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getMoneyStyleList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select  nstyleid,cname from moneyStyle");
			list.add(new CommonData("",""));
			while(rowSet.next())
			{
				CommonData data=new CommonData(rowSet.getString("nstyleid"),rowSet.getString("cname")+"          ");
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	
	
	/**
	 * 取得人员范围条件类型
	 * @return
	 * @throws GeneralException
	 */
	public String getPersonScope()throws GeneralException
	{
		String personScope="1";
		try
		{
			String flag=ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");
			if(!"".equals(flag))
			{
				if("0".equals(flag))
					personScope="1";
				else if("1".equals(flag))
					personScope="2";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return personScope;
	}
	
	/**
	 * 获得周期列表
	 * @return
	 */
	public ArrayList getPeriodList()
	{
		ArrayList periodList=new ArrayList();
		CommonData dataobj = new CommonData("1", "月");
        periodList.add(dataobj);
        dataobj = new CommonData("2", "季");
        periodList.add(dataobj);
        dataobj = new CommonData("3", "半年");
        periodList.add(dataobj);
        dataobj = new CommonData("4", "年");
        periodList.add(dataobj); 
        return periodList;
	}
	
	
	/**
	 * 获得子集中的指标
	 * @param type  1： 取日期型指标  2：取非代码型指标
	 * @return
	 */
	public ArrayList getFieldList(int type,String setid,String salaryid)
	{
		ArrayList fieldList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from salaryset where salaryid="+salaryid);
			HashMap existFieldMap=new HashMap();
			while(rowSet.next())
				existFieldMap.put(rowSet.getString("itemid").toLowerCase(),"1");
			
			
			if(setid!=null&&setid.trim().length()>0)
			{
				ArrayList fielditemlist=DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET); 
				if(fielditemlist!=null)
				{
					for(int i=0;i<fielditemlist.size();i++)
				    {
				      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				      if("M".equals(fielditem.getItemtype())|| "N".equals(fielditem.getItemtype()))
				    	  continue;
				      if(type==1&&!"D".equals(fielditem.getItemtype()))
				    	  continue;
				      
				  //    if(type==2&&fielditem.getItemtype().equals("A")&&!fielditem.getCodesetid().equalsIgnoreCase("0"))
				  //  	  continue;
				      
				      if(type==2&&existFieldMap.get(fielditem.getItemid().toLowerCase())==null)
				    	  continue;
				    	
				      
				      
				      fieldList.add(new CommonData(fielditem.getItemid(),fielditem.getItemdesc()));
				    }
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fieldList;
	}
	
	
	/**
	 * 取得人员库列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getDbList() throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			DbNameBo dd=new DbNameBo(this.conn);
			ArrayList dblist=dd.getAllDbNameVoList(this.userView);
			
			ContentDAO dao=new ContentDAO(this.conn);
			String cbase=","+this.vo.getString("cbase").toLowerCase();
			
			
			LazyDynaBean abean=null;
			for(int i=0;i<dblist.size();i++)
			{
				RecordVo vo=(RecordVo)dblist.get(i);
				
				String dbpre=vo.getString("pre");
				String dbname=vo.getString("dbname");
				String isSelected="0";
				if(cbase.indexOf(","+dbpre.toLowerCase()+",")!=-1)
					isSelected="1";
				abean=new LazyDynaBean();
				abean.set("pre",dbpre);
				abean.set("dbname",dbname);
				abean.set("isSelected",isSelected);
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	public SalaryCtrlParamBo getCtrlparam() {
		return ctrlparam;
	}


	public void setCtrlparam(SalaryCtrlParamBo ctrlparam) {
		this.ctrlparam = ctrlparam;
	}


	public int getGz_type() {
		return gz_type;
	}


	public void setGz_type(int gz_type) {
		this.gz_type = gz_type;
	}


	public String getSalaryid() {
		return salaryid;
	}


	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}


	public String getUnit_type() {
		return unit_type;
	}


	public void setUnit_type(String unit_type) {
		this.unit_type = unit_type;
	}


	public RecordVo getVo() {
		return vo;
	}


	public void setVo(RecordVo vo) {
		this.vo = vo;
	}
	
	
	
	
	

}
