package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPropertyBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import java.util.*;
/**
 * 保存薪资类别属性
 *<p>Title:SaveSalaryTemplatePropertyTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 30, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveSalaryTemplatePropertyTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String condStr=(String)this.getFormHM().get("condStr");
			String cexpr=(String)this.getFormHM().get("cexpr");
			condStr=PubFunc.keyWord_reback(condStr);
			cexpr=PubFunc.keyWord_reback(cexpr);
			String salaryid=(String)this.getFormHM().get("salaryid");
			String[] dbValue=(String[])this.getFormHM().get("dbValue");
			String   personScope=(String)this.getFormHM().get("personScope");
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			String item_str=(String)this.getFormHM().get("item_str");
			String type_str=(String)this.getFormHM().get("type_str");
			String set_str=(String)this.getFormHM().get("set_str");
			set_str = set_str.replaceAll("／", "/");
			String typestr=(String)this.getFormHM().get("typestr");
			typestr = typestr.replaceAll("／", "/");
			String sp_relation_id=(String)this.getFormHM().get("sp_relation_id");
			String default_filterid=(String)this.getFormHM().get("sp_default_filter_id");
			String gz_module=(String)this.getFormHM().get("gz_module");//0:薪资，1:保险 lis添加
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			//-----------------------------------------
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setInt("salaryid", Integer.parseInt(salaryid));
			vo = dao.findByPrimaryKey(vo);
			StringBuffer strxml=new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
			strxml.append("<param>");
			strxml.append("</param>");	
			String lpstr = vo.getString("lprogram")==null||vo.getString("lprogram").length()==0?strxml.toString():vo.getString("lprogram");
			Document doc2=PubFunc.generateDom(lpstr);
			//-----------------------------------------
			
			ArrayList setid=new ArrayList();
			String[] objs=set_str.split("/");
			ArrayList setList=new ArrayList();
			for(int i=0;i<objs.length;i++)
			{
				if(objs[i]!=null&&objs[i].trim().length()>0)
				{
					setid.add(objs[i]);
					setList.add(objs[i]);
				}
			}
			objs=typestr.split("/");
			
			String strExpression=PubFunc.keyWord_reback(SafeCode.decode((String)this.getFormHM().get("strExpression")));
			//	String strExpression=PubFunc.keyWord_reback((String)this.getFormHM().get("strExpression"));
			String royalty_valid=(String)this.getFormHM().get("royalty_valid");
			if(royalty_valid==null|| "-1".equals(royalty_valid))
				royalty_valid="0";
			String royalty_setid=(String)this.getFormHM().get("royalty_setid");
			
			
			/**提交方式*/
			ArrayList type=new ArrayList();
			int n=0;
			for(int i=0;i<objs.length;i++)
			{
				if(objs[i]!=null&&objs[i].trim().length()>0)
				{
					if("1".equals(royalty_valid)&&royalty_setid.trim().length()>0&&royalty_setid.equalsIgnoreCase((String)setList.get(n)))
						type.add("0");
					else
						type.add(objs[i]);
					n++;
				}
			}
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			/**保存数据集提交方式*/
			String subNoShowUpdateFashion=(String)this.getFormHM().get("subNoShowUpdateFashion");
			String subNoPriv=(String)this.getFormHM().get("subNoPriv");
			String allowEditSubdata=(String)this.getFormHM().get("allowEditSubdata");
			if (allowEditSubdata==null)
			    allowEditSubdata="0";
			
			if("-1".equals(subNoShowUpdateFashion))
				subNoShowUpdateFashion="0";
			if("-1".equals(subNoPriv))
				subNoPriv="0";
			
			String lprogram = gzbo.saveSubmitType(setid, type,item_str,type_str,subNoShowUpdateFashion,subNoPriv,allowEditSubdata);
			Document doc3=PubFunc.generateDom(lprogram);
			String _str = this.getChangeContext(doc2, doc3,salaryid,gz_module);
			
			if(personScope!=null&&personScope.trim().length()>0)
			{
				if("1".equals(personScope))
					personScope="0";
				else
					personScope="1";
			}
			String verify_ctrl=(String)this.getFormHM().get("verify_ctrl");
			
			String verify_ctrl_ff=(String)this.getFormHM().get("verify_ctrl_ff");
			String verify_ctrl_sp=(String)this.getFormHM().get("verify_ctrl_sp");
			String amount_ctrl_ff=(String)this.getFormHM().get("amount_ctrl_ff");
			String amount_ctrl_sp=(String)this.getFormHM().get("amount_ctrl_sp");
			String ctrlType=(String)this.getFormHM().get("ctrlType");
			LazyDynaBean paramBean=new LazyDynaBean();
			paramBean.set("verify_ctrl_ff", verify_ctrl_ff);
			paramBean.set("verify_ctrl_sp", verify_ctrl_sp);
			paramBean.set("amount_ctrl_ff", amount_ctrl_ff);
			paramBean.set("amount_ctrl_sp", amount_ctrl_sp);
			paramBean.set("ctrl_type",ctrlType);
			String reject_mode=(String)this.getFormHM().get("reject_mode");
			String amount_ctrl=(String)this.getFormHM().get("amount_ctrl");
			String priv_mode=(String)this.getFormHM().get("priv_mode");
			String moneyType=(String)this.getFormHM().get("moneyType");
			String[] varyModelValue=(String[])this.getFormHM().get("varyModelValue");
			String calculateTaxTime=(String)this.getFormHM().get("calculateTaxTime");
			String appealTaxTime=(String)this.getFormHM().get("appealTaxTime");
			String sendSalaryItem=(String)this.getFormHM().get("sendSalaryItem");
			String taxType=(String)this.getFormHM().get("taxType");
			String ratepayingDecalre=(String)this.getFormHM().get("ratepayingDecalre");
			
			String flow_ctrl=(String)this.getFormHM().get("flow_ctrl");
			String piecerate=(String)this.getFormHM().get("piecerate");
			
			String manager=(String)this.getFormHM().get("manager");
			String smsNotice=(String)this.getFormHM().get("msNotice");
			String mailNotice=(String)this.getFormHM().get("mailNotice");
			String mailTemplateId=(String)this.getFormHM().get("mailTemplateId");
			String a01z0Flag=(String)this.getFormHM().get("a01z0Flag");
			String bonusItemFld = (String)this.getFormHM().get("bonusItemFld");
			String orgid = (String)this.getFormHM().get("orgid");
			String deptid = (String)this.getFormHM().get("deptid");
			String layer = (String)this.getFormHM().get("contrlLevelId");
			String sum_type = (String)this.getFormHM().get("sum_type");
			sum_type=sum_type!=null&&sum_type.trim().length()>0&&!"-1".equalsIgnoreCase(sum_type)?sum_type:"0";
			String lsDept=(String)this.getFormHM().get("lsDept");
			if(lsDept==null)
				lsDept="";
			String field_priv=(String)this.getFormHM().get("field_priv");
			if(field_priv==null)
				field_priv="0";
			String read_field=(String)this.getFormHM().get("read_field");
			if(read_field==null)
				read_field="0";
			String collect_je_field=(String)this.getFormHM().get("collect_je_field");
			if(collect_je_field==null)
				collect_je_field="";
			paramBean.set("read_field",read_field);
			SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn(),salaryid,0,this.getUserView());
			HashMap paramMap=new HashMap();
			if(sp_relation_id==null)
				sp_relation_id="";
			paramMap.put("sp_relation_id",sp_relation_id);
			
			if(default_filterid==null)   default_filterid="";
			paramMap.put("default_filterid",default_filterid);
			
			String priecerate_valid =(String) this.getFormHM().get("priecerate_valid");
			String priecerate_expression_str = PubFunc.keyWord_reback(SafeCode.decode((String) this.getFormHM().get("priecerate_expression_str")));
			String priecerate_zhouq1 = (String) this.getFormHM().get("priecerate_zhouq1");
			String priecerate_zhibiao = (String) this.getFormHM().get("priecerate_zhibiao");
			String priecerate_str = (String) this.getFormHM().get("priecerate_str");
			paramMap.put("priecerate_valid", priecerate_valid);
			paramMap.put("priecerate_expression_str", priecerate_expression_str);
			paramMap.put("priecerate_zhouq1", priecerate_zhouq1);
			paramMap.put("priecerate_zhibiao", priecerate_zhibiao);
			paramMap.put("priecerate_str", priecerate_str);
			this.getFormHM().put("priecerate_valid", "");
			this.getFormHM().put("priecerate_expression_str", "");
			this.getFormHM().put("priecerate_zhouq1", "");
			this.getFormHM().put("priecerate_zhibiao", "");
			this.getFormHM().put("priecerate_str", "");
			String royalty_date=(String)this.getFormHM().get("royalty_date");
			String royalty_period=(String)this.getFormHM().get("royalty_period");
			String royalty_relation_fields=(String)this.getFormHM().get("royalty_relation_fields");
			paramMap.put("royalty_valid",royalty_valid);
			paramMap.put("royalty_setid",royalty_setid);
			paramMap.put("royalty_date",royalty_date);
			paramMap.put("royalty_period",royalty_period);
			paramMap.put("royalty_relation_fields",royalty_relation_fields);
			paramMap.put("strExpression",strExpression);
			ArrayList list = new ArrayList();
			if(dbValue==null||dbValue.length==0){
				throw GeneralExceptionHandler.Handle(new Exception("请检查人员库的设置！"));	
			}
			//添加参数gz_module，0:薪资，1:保险，lis添加
			bo.saveStandardProperty(gz_module,flow_ctrl,piecerate,condStr,cexpr,salaryid,dbValue,personScope,moneyType,varyModelValue,calculateTaxTime,appealTaxTime,sendSalaryItem,taxType,ratepayingDecalre,amount_ctrl,priv_mode,manager,smsNotice,mailNotice,mailTemplateId,a01z0Flag,bonusItemFld,orgid,deptid,layer,sum_type,reject_mode,verify_ctrl,paramBean,lsDept,field_priv,collect_je_field,paramMap,list);
			
			//-----------------------人员库、货币类型变动日志-------------------------------
			StringBuffer basemoney = new StringBuffer();
			StringBuffer cbase=new StringBuffer("");
			for(int i=0;i<dbValue.length;i++)
			{
				if(!"-1".equals(dbValue[i]))
					cbase.append(dbValue[i]+",");
			}
			String nbase = vo.getString("cbase");
			if(!cbase.toString().equals(nbase)){
				basemoney.append("<tr>");
				basemoney.append("<td>人员库</td>");
				basemoney.append("<td>"+getDbName(nbase)+"</td>");
				basemoney.append("<td>"+getDbName(cbase.toString())+"</td>");
				basemoney.append("</tr>");
			}
			String Nmoneyid = vo.getString("nmoneyid");
			if(!moneyType.equals(Nmoneyid)){
				ArrayList moneyTypeList=bo.getMoneyStyleList();
				String _moneyType = moneyType;
				String _Nmoneyid = Nmoneyid;
				for(int i=0;i<moneyTypeList.size();i++){
					CommonData data=(CommonData) moneyTypeList.get(i);
					if(moneyType.equals(data.getDataValue())){
						_moneyType = data.getDataName();
					}
					if(Nmoneyid.equals(data.getDataValue())){
						_Nmoneyid = data.getDataName();
					}
				}
				basemoney.append("<tr>");
				basemoney.append("<td>选用货币</td>");
				basemoney.append("<td>"+_Nmoneyid+"</td>");
				basemoney.append("<td>"+_moneyType+"</td>");
				basemoney.append("</tr>");
			}
			
			String cond = vo.getString("cond");
			String cond1 = vo.getString("cond");
			String Cexpr = vo.getString("cexpr");
			StringBuffer temCond = new StringBuffer();//变化前
			StringBuffer temCondStr = new StringBuffer();//变化后
			Factor factor = null;
			
			if(!condStr.equals(cond)){
				if("0".equals(personScope)){//当是人员范围-简单条件时，对代码类汉化，lis修改
					FactorList factorlist=new FactorList(Cexpr,cond,"");//变化前,cexpr=""是复杂条件
					FactorList factorlist1=new FactorList(cexpr,condStr,"");//变化后
					
					if(!(factorlist==null||factorlist.size()==0))
					{
						for(int i=0;i<factorlist.size();i++)
						{
							factor = (Factor)factorlist.get(i);
							if(temCond.length() == 0)
								temCond.append(factor.getHz()+factor.getOper()+factor.getHzvalue());
							else
								temCond.append("，"+factor.getHz()+factor.getOper()+factor.getHzvalue());
						}
					}
					
					if(!(factorlist1==null||factorlist1.size()==0))
					{
						for(int i=0;i<factorlist1.size();i++)
						{
							factor = (Factor)factorlist1.get(i);
							if(temCondStr.length() == 0)
								temCondStr.append(factor.getHz()+factor.getOper()+factor.getHzvalue());
							else
								temCondStr.append("，"+factor.getHz()+factor.getOper()+factor.getHzvalue());
						}
					}
					if(Cexpr != null && !"".equals(Cexpr))
						cond = temCond.toString();
					condStr = temCondStr.toString();
				}else{
					//要判断是从简单条件到复杂条件，如果是则要汉化简单条件
					if(!"".equals(Cexpr) && Cexpr != null){
						FactorList factorlist=new FactorList(Cexpr,cond,"");//变化前
						
						if(!(factorlist==null||factorlist.size()==0))
						{
							for(int i=0;i<factorlist.size();i++)
							{
								factor = (Factor)factorlist.get(i);
								if(temCond.length() == 0)
									temCond.append(factor.getHz()+factor.getOper()+factor.getHzvalue());
								else
									temCond.append("，"+factor.getHz()+factor.getOper()+factor.getHzvalue());
							}
						}
					}
					cond = temCond.toString();
				}
				//end，lis修改
				basemoney.append("<tr>");
				basemoney.append("<td>条件项</td>");
				basemoney.append("<td>"+cond1+"</td>");
				basemoney.append("<td>"+condStr+"</td>");
				basemoney.append("</tr>");
			}
			
			if(!cexpr.equals(Cexpr)){
				basemoney.append("<tr>");
				basemoney.append("<td>条件表达式</td>");
				basemoney.append("<td>"+Cexpr+"</td>");
				basemoney.append("<td>"+cexpr+"</td>");
				basemoney.append("</tr>");
			}
			//---------------------------end------------------------------------		
			Document doc=(Document) list.get(0);
			Document doc1=PubFunc.generateDom((String) list.get(1));
			String str = this.getChangeContext(doc, doc1,salaryid,gz_module);
			StringBuffer context = new StringBuffer();
			if(str.length()>0||_str.length()>0||basemoney.length()>0){
				context.append(vo.getString("cname")+"("+salaryid+")"+"的属性值进行变更<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>属性名</td>");
				context.append("<td>变化前</td>");
				context.append("<td>变化后</td>");
				context.append("</tr>");
				context.append(basemoney);
				context.append(str);
				context.append(_str);
				context.append("</table>");
				this.getFormHM().put("@eventlog", context.toString());
			}else{
				this.getFormHM().put("@eventlog", vo.getString("cname")+"("+salaryid+")"+"的属性操作过，但是没修过任何东西");
			}
			
			if("1".equals(royalty_valid)&&royalty_setid.trim().length()>0)
			{
				
				dao.update("update salaryset set initflag=2,heapflag=0 where salaryid="+salaryid+" and lower(fieldsetid)='"+royalty_setid.toLowerCase()+"'");
			}
		//	System.out.println("finished...");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 添加参数gz_module，判断是薪资还是保险,lis修改
	 * @param doc
	 * @param doc1
	 * @param salaryid
	 * @param gz_module
	 * @return
	 * @throws GeneralException
	 */
	public String getChangeContext(Document doc,Document doc1,String salaryid,String gz_module) throws GeneralException{
		StringBuffer context = new StringBuffer();
		try
		{	
			//薪资类别属性变化后日志顺序问题
			LinkedHashMap map = new LinkedHashMap();//变化前			
			Element root = doc.getRootElement();
			this.getElementContext(root, map, "");
			
			LinkedHashMap map1 = new LinkedHashMap();//变化后			
			Element root1 = doc1.getRootElement();
			this.getElementContext(root1, map1, "");
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				String val = (String) entry.getValue()==null||" ".equals((String) entry.getValue())?"":(String) entry.getValue();
				String changestr = (String) map1.get(key)==null||" ".equals((String) map1.get(key))?"":(String) map1.get(key);
				if(!val.equals(changestr)){
					context.append("<tr>");
					context.append("<td>"+SalaryTemplateBo.getAttributeName(key.toString(),gz_module)+"</td>");
					context.append("<td>"+SalaryTemplateBo.getAttributeValue(key.toString(),val,this.frameconn,salaryid)+"</td>");
					context.append("<td>"+SalaryTemplateBo.getAttributeValue(key.toString(),changestr,this.frameconn,salaryid)+"</td>");
					context.append("</tr>");
					map1.remove(key);
				}else{
					map1.remove(key);
				}
			}
			Iterator iter1 = map1.entrySet().iterator();
			while (iter1.hasNext()) {
				Map.Entry entry = (Map.Entry) iter1.next();
				Object key = entry.getKey();
				String val = (String) entry.getValue()==null||" ".equals((String) entry.getValue())?"":(String) entry.getValue();
				String changestr = (String) map.get(key)==null||" ".equals((String) map1.get(key))?"":(String) map.get(key);
				if(!val.equals(changestr)){
					context.append("<tr>");
					context.append("<td>"+SalaryTemplateBo.getAttributeName(key.toString(),gz_module)+"</td>");
					context.append("<td>"+SalaryTemplateBo.getAttributeValue(key.toString(),changestr,this.frameconn,salaryid)+"</td>");
					context.append("<td>"+SalaryTemplateBo.getAttributeValue(key.toString(),val,this.frameconn,salaryid)+"</td>");
					context.append("</tr>");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return context.toString();
	}
	/**
	 * 递归循环遍历document，取出所以属性和值
	 * @param ele
	 * @param map
	 * @param ul
	 * @throws GeneralException
	 */
	public void getElementContext(Element ele,HashMap map,String ul) throws GeneralException{
		try{
			List childlist=ele.getChildren();
			if(childlist.size()>0){
				ul=ul+"/"+ele.getName();
				Element element=null;
				List list = null;
				for(int i=0;i<childlist.size();i++){
					element=(Element)childlist.get(i);
					list = element.getAttributes();			
					if(list.size()>0){
						for(int t=0;t<list.size();t++){
							Attribute attr = (Attribute) list.get(t);
							map.put(ul+"/"+element.getName()+"/"+attr.getName(), attr.getValue());
						}
					}
					if(element.getText()!=null&&element.getText().length()>0){
						map.put(ul+"/"+element.getName(), element.getText());
					}else 
						map.put(ul+"/"+element.getName(), "");

					this.getElementContext(element, map,ul);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 人员库转化为汉字  用于日志
	 * @param nbase
	 * @return
	 * @throws GeneralException
	 */
	public String getDbName(String nbase) throws GeneralException{
		StringBuffer str = new StringBuffer();
		try{
			String[] cbase = nbase.split(",");
			DbNameBo dd=new DbNameBo(this.frameconn);
			ArrayList dblist=dd.getAllDbNameVoList(this.userView);
			for(int j=0;j<cbase.length;j++){
				for(int i=0;i<dblist.size();i++)
				{
					RecordVo vo=(RecordVo)dblist.get(i);				
					String dbpre=vo.getString("pre");
					String dbname=vo.getString("dbname");
					if(cbase[j].equalsIgnoreCase(dbpre)){
						str.append(dbname);
						str.append(",");
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return str.toString();
	}
}
