package com.hjsj.hrms.module.gz.salarytype.transaction.salaryproperty;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryPropertyBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
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
public class SaveSalaryTypePropertyTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String gz_module=(String)this.getFormHM().get("gz_module");//0:薪资，1:保险 lis添加
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn(),salaryid,0,this.getUserView());
			
			MorphDynaBean form=(MorphDynaBean)this.getFormHM().get("form");
			
			SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
			
			/** 保存审批方式比对指标到日志    start  */
			String rightvalue = (String)form.get("rightvalue");
	    	String addrightvalue = (String)form.get("addrightvalue");
	    	String delrightvalue = (String)form.get("delrightvalue");
	    	String _rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.COMPARE_FIELD);
	    	String _addrightvalue = ctrl_par.getValue(SalaryCtrlParamBo.ADD_MAN_FIELD);
	    	String _delrightvalue = ctrl_par.getValue(SalaryCtrlParamBo.DEL_MAN_FIELD);
	    	StringBuffer str = new StringBuffer();
	    	if(_rightvalue!=null&&!_rightvalue.equals(rightvalue)){
        		str.append("<tr>");
        		str.append("<td>"+ResourceFactory.getProperty("gz.info.change")+"</td>");//信息变动
        		str.append("<td>"+SalaryPropertyBo.getSalarySet(_rightvalue)+"</td>");
        		str.append("<td>"+SalaryPropertyBo.getSalarySet(rightvalue)+"</td>");
        		str.append("</tr>");
	        }
	    	if(addrightvalue!=null&&!addrightvalue.equals(_addrightvalue)){
        		str.append("<tr>");
        		str.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.add.staff")+"</td>");//新增人员
        		str.append("<td>"+SalaryPropertyBo.getSalarySet(_addrightvalue)+"</td>");
        		str.append("<td>"+SalaryPropertyBo.getSalarySet(addrightvalue)+"</td>");
        		str.append("</tr>");
        	}
        	if(delrightvalue!=null&&!delrightvalue.equals(_delrightvalue)){
        		str.append("<tr>");
        		str.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.minus.staff")+"</td>");//减少人员
        		str.append("<td>"+SalaryPropertyBo.getSalarySet(_delrightvalue)+"</td>");
        		str.append("<td>"+SalaryPropertyBo.getSalarySet(delrightvalue)+"</td>");
        		str.append("</tr>");
        	}
        	StringBuffer context = new StringBuffer();
			if(str.length()>0){
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				RecordVo vo=new RecordVo("salarytemplate");
				vo.setInt("salaryid", Integer.parseInt(salaryid));
				vo = dao.findByPrimaryKey(vo);
				//的属性值进行变更
				context.append(vo.getString("cname")+"("+salaryid+")"+ResourceFactory.getProperty("gz_new.gz_propertyChange")+"<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>"+ResourceFactory.getProperty("gz_new.gz_propertyName")+"</td>");//属性名
				context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.before")+"</td>");//变化前
				context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.affter")+"</td>");//变化后
				context.append("</tr>");
				context.append(str);
				context.append("</table>");
				this.getFormHM().put("@eventlog", context.toString());
			}
			
			/** 保存审批方式比对指标到日志    end  */
			
			/** 保存提交方式 到日志 start  */
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setInt("salaryid", Integer.parseInt(salaryid));
			vo = dao.findByPrimaryKey(vo);
			StringBuffer strxml=new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
			strxml.append("<param>");
			strxml.append("</param>");	
			String lpstr = vo.getString("lprogram")==null||vo.getString("lprogram").length()==0?strxml.toString():vo.getString("lprogram");
			Document doc2 = PubFunc.generateDom(lpstr);
			String confirm_type = (String)form.get("confirm_type");
			String subNoShowUpdateFashion = (String)form.get("subNoShowUpdateFashion");
			String subNoPriv = (String)form.get("subNoPriv");
			String allowEditSubdata = (String)form.get("allowEditSubdata");
			//保存数据条件方式
			ArrayList updateObj = (ArrayList)form.get("updateObj");
			StringBuffer buf=new StringBuffer(confirm_type);
			if(updateObj != null){
				for(int i=0;i<updateObj.size();i++)
				{
					MorphDynaBean bean = (MorphDynaBean)updateObj.get(i);
					String itemid=(String)bean.get("itemid");
					if("0".equals((String)bean.get("flag")))
					{	buf.append(itemid.toUpperCase());
					buf.append(";");
					}
				} 
			}else{
				String update = (String)form.get("buf");
				buf.append(update);
			}
				
			String lprogram = bo.saveSubmitType(buf.toString(),subNoShowUpdateFashion,subNoPriv,allowEditSubdata);
			Document doc3 = PubFunc.generateDom(lprogram);
			String _str = this.getChangeContext(doc2, doc3,salaryid,gz_module);
			
			//-----------------------人员库、货币类型变动日志-------------------------------
			StringBuffer basemoney = new StringBuffer();
			String personScope = (String)form.get("personScope");
			String condStr=(String)form.get("condStr");//简单或复杂条件表达式
			String cexpr=(String)form.get("cexpr");//简单条件关系，1*2+3
			condStr=SafeCode.decode(condStr);
			condStr=condStr.replaceAll("!","\r");
			condStr=condStr.replaceAll("`","\n");
			condStr=PubFunc.keyWord_reback(condStr);
			//由于前台对加号做了特殊处理，这里转回来
			if(cexpr.contains("convert"))
				cexpr = cexpr.replaceAll("convert", "+");
			cexpr=PubFunc.keyWord_reback(SafeCode.decode(cexpr));
			StringBuffer cbase=new StringBuffer("");
			//人员库数据
			ArrayList dbValue = new ArrayList();;
			if(form.get("dbValue") instanceof ArrayList){
				dbValue = (ArrayList)form.get("dbValue");
				for(int i=0;i<dbValue.size();i++)
				{
					if(!"-1".equals((String)dbValue.get(i)))
						cbase.append(dbValue.get(i) + ",");
				}
			}else{
				cbase.append((String)form.get("dbValue") + ",");
			}
			String nbase = vo.getString("cbase");
			if(!cbase.toString().equals(nbase)){
				basemoney.append("<tr>");
				basemoney.append("<td>"+ResourceFactory.getProperty("label.dbase")+"</td>");//人员库
				basemoney.append("<td>"+getDbName(nbase)+"</td>");
				basemoney.append("<td>"+getDbName(cbase.toString())+"</td>");
				basemoney.append("</tr>");
			}
			String Nmoneyid = vo.getString("nmoneyid");
			String moneyType = (String)form.get("moneyType");
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
				basemoney.append("<td>"+ResourceFactory.getProperty("gz.templateset.selectMoney")+"</td>");//选用货币
				basemoney.append("<td>"+_Nmoneyid+"</td>");
				basemoney.append("<td>"+_moneyType+"</td>");
				basemoney.append("</tr>");
			}
			
			String cond = vo.getString("cond");
			String cond1 = vo.getString("cond");
			String Cexpr = vo.getString("cexpr");
			//由于前台对加号做了特殊处理，这里转回来
			if(Cexpr.contains("convert"))
				Cexpr = Cexpr.replaceAll("convert", "+");
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
				basemoney.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.conditionItem")+"</td>");//条件项
				basemoney.append("<td>"+cond+"</td>");//将cond1换成cond，cond是经过代码值转化成汉字的		sunjian2017-05-24
				basemoney.append("<td>"+condStr+"</td>");
				basemoney.append("</tr>");
			}
			
			if(!cexpr.equals(Cexpr)){
				basemoney.append("<tr>");
				basemoney.append("<td>"+ResourceFactory.getProperty("kq.wizard.cond.exp")+"</td>");//条件表达式
				basemoney.append("<td>"+Cexpr+"</td>");
				basemoney.append("<td>"+cexpr+"</td>");
				basemoney.append("</tr>");
			}
			
			ArrayList list = new ArrayList();
			//添加参数gz_module，0:薪资，1:保险，lis添加
			bo.saveStandardProperty(form,gz_module,list);
			
			Document doc=(Document) list.get(0);
			Document doc1 = PubFunc.generateDom((String) list.get(1));
			String str2 = this.getChangeContext(doc, doc1,salaryid,gz_module);
			context.setLength(0);
			context = new StringBuffer();
			if(str2.length()>0||_str.length()>0||basemoney.length()>0){
				//的属性值进行变更
				context.append(vo.getString("cname")+"("+salaryid+")"+ResourceFactory.getProperty("gz_new.gz_propertyChange")+"<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>"+ResourceFactory.getProperty("gz_new.gz_propertyName")+"</td>");//属性名
				context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.before")+"</td>");//变化前
				context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.affter")+"</td>");//变化后
				context.append("</tr>");
				context.append(basemoney);
				context.append(str2);
				context.append(_str);
				context.append("</table>");
				this.getFormHM().put("@eventlog", context.toString());
			}else{
				//的属性操作过，但是没修过任何东西
				this.getFormHM().put("@eventlog", vo.getString("cname")+"("+salaryid+")"+ResourceFactory.getProperty("gz_new.gz_optButNoChange"));
			}
			/** 提交方式  end  */
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
	private String getChangeContext(Document doc,Document doc1,String salaryid,String gz_module) throws GeneralException{
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
			
			SalaryCtrlParamBo salaryCtrlParamBo = new SalaryCtrlParamBo(this.frameconn, Integer.parseInt(salaryid));
			List elementList = salaryCtrlParamBo.getValue(SalaryCtrlParamBo.FILLING_AGENCY, SalaryCtrlParamBo.FILLING_AGENCYS);
			
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				String val = (String) entry.getValue()==null||" ".equals((String) entry.getValue())?"":(String) entry.getValue();
				String changestr = (String) map1.get(key)==null||" ".equals((String) map1.get(key))?"":(String) map1.get(key);
				if(!val.equals(changestr)){
					//在切换共享账套和非共享账套的时候需要清除应用机构设置和下发中非结束状态的下发数据
					if("/param/manager/user".equals(key.toString()) && elementList.size() > 0) {
						salaryCtrlParamBo.removeNode(SalaryCtrlParamBo.FILLING_AGENCYS);
						salaryCtrlParamBo.saveParameter();
						DbWizard dbWizard=new DbWizard(this.getFrameconn());
						if(StringUtils.isBlank(changestr) && dbWizard.isExistTable(val + "_salary_" + salaryid,false)) {//有共享账套改成非共享，删除下发数据还在流程中的
							ContentDAO dao=new ContentDAO(this.getFrameconn());
							String sql = "delete from gz_reporting_log where salaryid = ? and a00z2 = (select max(a00z2) from " + val + "_salary_" + salaryid + " where sp_flag <> ?)";
							dao.delete(sql,Arrays.asList(new String[]{salaryid, "06"}));
						}
					}
					context.append("<tr>");
					context.append("<td>"+SalaryPropertyBo.getAttributeName(key.toString(),gz_module)+"</td>");
					context.append("<td>"+SalaryPropertyBo.getAttributeValue(key.toString(),val,this.frameconn,salaryid)+"</td>");
					context.append("<td>"+SalaryPropertyBo.getAttributeValue(key.toString(),changestr,this.frameconn,salaryid)+"</td>");
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
					context.append("<td>"+SalaryPropertyBo.getAttributeName(key.toString(),gz_module)+"</td>");
					context.append("<td>"+SalaryPropertyBo.getAttributeValue(key.toString(),changestr,this.frameconn,salaryid)+"</td>");
					context.append("<td>"+SalaryPropertyBo.getAttributeValue(key.toString(),val,this.frameconn,salaryid)+"</td>");
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
