package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import java.util.*;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class SetChangeSpTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
	    	HashMap hm=this.getFormHM();
		
    		String flag = (String)hm.get("flag");
	    	/**=1审批比对指标,=0发放比对*/
	    	String entry_type=(String)hm.get("entry_type");
	    	String gz_module=(String)this.getFormHM().get("gz_module");//0:薪资，1:保险 lis添加
	    	flag=flag!=null&&flag.trim().length()>0?flag:"";
	    	entry_type=entry_type!=null&&entry_type.trim().length()>0?entry_type:"";
	    	HashMap reqhm = (HashMap)hm.get("requestPamaHM");
		    if(flag.length()<1){
		    	flag = (String)reqhm.get("flag");
		    	reqhm.remove("flag");
		    	flag=flag!=null&&flag.trim().length()>0?flag:"";
	    	}
		    String param_flag = (String)hm.get("param_flag");
		    param_flag=param_flag!=null&&param_flag.trim().length()>0?param_flag:"";
		    if(param_flag.length()<1){
			    param_flag = (String)reqhm.get("param_flag");
			    reqhm.remove("param_flag");
			    param_flag=param_flag!=null&&param_flag.trim().length()>0?param_flag:"";
		    }
		/*if(entry_type.trim().length()<1)
		{
			entry_type=(String)reqhm.get("entry_type");
			reqhm.remove("entry_type");
			entry_type=entry_type!=null&&entry_type.trim().length()>0?entry_type:"";
		}*/
		
	    	String salaryid = (String)hm.get("salaryid");
	    	salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
	    	//xujian 2009-9-22
	    	if("verify".equalsIgnoreCase(param_flag)){//显示审核报告显示指标 wangrd 2013-11-15
	    		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
	    		String verify_item=ctrl_par.getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_item");
				if(verify_item==null)
					verify_item="";
				verify_item=verify_item!=null?verify_item.replaceAll(",","','"):"";
	    		hm.put("leftlist",leftVerifyList(salaryid,verify_item));
	    		hm.put("rightlist",rightCollectList(salaryid,verify_item));
	    		hm.put("salaryid",salaryid);
	    		hm.put("param_flag","saveverify");	   		
    		}else if("saveverify".equalsIgnoreCase(param_flag)){//保存审核指标设置 wangrd 2013-11-15
                String rightvalue = (String)hm.get("rightvalue");
                rightvalue=rightvalue!=null&&rightvalue.trim().length()>0?rightvalue:"";
                SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
                String verify_item=ctrl_par.getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_item");
                ctrl_par.setValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_item",rightvalue);
                ctrl_par.saveParameter();
                hm.put("check","ok");
	    		StringBuffer str = new StringBuffer();
	    		if(verify_item!=null&&!verify_item.equals(rightvalue)){

//	    			str.append("修改审核指标:"+SalaryTemplateBo.getSalarySet(verify_item)+"--->"+SalaryTemplateBo.getSalarySet(rightvalue));
	    			str.append("<tr>");
	    			str.append("<td>修改审核指标</td>");
	    			str.append("<td>"+SalaryTemplateBo.getSalarySet(verify_item)+"</td>");
	    			str.append("<td>"+SalaryTemplateBo.getSalarySet(rightvalue)+"</td>");
	    			str.append("</tr>");
					StringBuffer context = new StringBuffer();
					SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
					String name = bo.getSalaryName(salaryid);
					context.append(name+"("+salaryid+")修改审核指标");
					context.append("<table>");
					context.append("<tr>");
					context.append("<td>属性名</td>");
					context.append("<td>变化前</td>");
					context.append("<td>变化后</td>");
					context.append("</tr>");
					context.append(str);
					context.append("</table>");
	    			hm.put("@eventlog", context.toString());
	    		}
            }else if("collect".equalsIgnoreCase(param_flag)){//显示设置汇总指标窗体
	    		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
	    		String rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD);
	    		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
	    		String collectPoint = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field");
	    		hm.put("leftlist",leftCollectList(salaryid,rightvalue));
	    		hm.put("rightlist",rightCollectList(salaryid,rightvalue));
	    		hm.put("collectList", CollectList(salaryid));
	    		hm.put("collectPoint", collectPoint);
	    		hm.put("salaryid",salaryid);
	    		hm.put("param_flag","savecollect");
	    	}else if("savecollect".equalsIgnoreCase(param_flag)){//保存汇总指标设置
	    		String rightvalue = (String)hm.get("rightvalue");
		    	rightvalue=rightvalue!=null&&rightvalue.trim().length()>0?rightvalue:"";
		    	String collectPoint = (String)hm.get("collectPoint");
		    	SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
		    	String _collectPoint = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field");
		    	String _rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD);
		    	ctrl_par.setValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field", collectPoint);
		        ctrl_par.setValue(SalaryCtrlParamBo.SUM_FIELD,rightvalue);
		        ctrl_par.saveParameter();
	    		hm.put("check","ok");
	    		StringBuffer str = new StringBuffer();
	    		if(_collectPoint!=null&&!_collectPoint.equals(collectPoint)){
	    			str.append("<tr>");
	    			str.append("<td>修改汇总指标</td>");
	    			str.append("<td>"+SalaryTemplateBo.getSalarySet(_collectPoint)+"</td>");
	    			str.append("<td>"+SalaryTemplateBo.getSalarySet(collectPoint)+"</td>");
	    			str.append("</tr>");
//	    			str.append("修改汇总指标:"+SalaryTemplateBo.getSalarySet(_collectPoint)+"--->"+SalaryTemplateBo.getSalarySet(collectPoint));
	    		}
	    		if(_rightvalue!=null&&!_rightvalue.equals(rightvalue)){
	    			str.append("<tr>");
	    			str.append("<td>修改汇总指标已选指标</td>");
	    			str.append("<td>"+SalaryTemplateBo.getSalarySet(_rightvalue)+"</td>");
	    			str.append("<td>"+SalaryTemplateBo.getSalarySet(rightvalue)+"</td>");
	    			str.append("</tr>");
//	    			str.append("修改汇总指标已选指标:"+SalaryTemplateBo.getSalarySet(_rightvalue)+"--->"+SalaryTemplateBo.getSalarySet(_rightvalue));
	    		}
	    		if(str.length()>0){
					StringBuffer context = new StringBuffer();
					SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
					String name = bo.getSalaryName(salaryid);
					context.append(name+"("+salaryid+")修改汇总指标");
					context.append("<table>");
					context.append("<tr>");
					context.append("<td>属性名</td>");
					context.append("<td>变化前</td>");
					context.append("<td>变化后</td>");
					context.append("</tr>");
					context.append(str);
					context.append("</table>");
	    			hm.put("@eventlog", context.toString());
	    			
	    		}else{
	    			hm.put("@eventlog", "");
	    		}	    		
	    	}else if("sp".equalsIgnoreCase(param_flag)){//设置审批指标  搜房网  zhaoxg 2013-11-18
	    		String rightvalue="";
	    		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
				RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
				if(vo!=null&&vo.getString("lprogram")!=null&&vo.getString("lprogram").indexOf("hidden_items")!=-1
					&&vo.getString("lprogram").indexOf("hidden_item")!=-1){
		    		Document doc = PubFunc.generateDom(vo.getString("lprogram"));
		    		Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items");
					if(hidden_items!=null){
						List list = hidden_items.getChildren();
						if(list.size()>0){
							for(int i =0;i<list.size();i++){
								Element child = (Element)list.get(i);
								if(child.getAttributeValue("user_name")==null)
								{
									rightvalue = child.getText();//显示指标
								}
							}
						}
					}
				}
				
	    		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
	    		hm.put("leftlist",leftCollectList1(salaryid,rightvalue));
	    		hm.put("rightlist",rightCollectList(salaryid,rightvalue));
	    		hm.put("salaryid",salaryid);
	    		hm.put("param_flag","savesp");
	    	}else if("savesp".equalsIgnoreCase(param_flag)){//保存审批指标  搜房网  zhaoxg 2013-11-18
	    		String rightvalue = (String)hm.get("rightvalue");
		    	rightvalue=rightvalue!=null&&rightvalue.trim().length()>0?rightvalue:"";
				SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
				RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
				boolean change = this.rightIsChange(rightvalue, gzbo, salaryid);//判断和原来记录是否不同  不同则删除个人的显示&隐藏指标的记录  zhaoxg add 2014-12-17
				ArrayList list = gzbo.saveSpXml(vo, rightvalue,change);
				if(list.size()>1){
					String str = this.getChangeContext((Document)list.get(0), (Document)list.get(1),salaryid,gz_module);
					StringBuffer context = new StringBuffer();
					SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
					String name = bo.getSalaryName(salaryid);
					context.append(name+"("+salaryid+")修改审批指标");
					context.append("<table>");
					context.append("<tr>");
					context.append("<td>属性名</td>");
					context.append("<td>变化前</td>");
					context.append("<td>变化后</td>");
					context.append("</tr>");
					context.append(str);
					context.append("</tr>");
					context.append("</table>");
					hm.put("@eventlog", context.toString());
				}
				
				hm.put("check","ok");
	    	}else if("alert".equalsIgnoreCase(flag)){//设置对比指标  搜房网 zhaoxg 2013-11-15
	     		String rightvalue = rightValue(salaryid/*,entry_type*/);
	     		String addrightvalue = addrightValue(salaryid);
	     		String delrightvalue = delrightValue(salaryid);
	    		hm.put("leftlist",leftList(salaryid,rightvalue));
	    		hm.put("rightlist",rightList(salaryid,rightvalue));
	    		hm.put("addleftlist",leftList1(salaryid,addrightvalue));
	    		hm.put("addrightlist",rightList(salaryid,addrightvalue));
	    		hm.put("delleftlist",leftList1(salaryid,delrightvalue));
	    		hm.put("delrightlist",rightList(salaryid,delrightvalue));
	    		hm.put("salaryid",salaryid);
	    	}else{
		    	String rightvalue = (String)hm.get("rightvalue");
		    	rightvalue=rightvalue!=null&&rightvalue.trim().length()>0?rightvalue:"";
		    	SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
		    	String _rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.COMPARE_FIELD);

			/*if(entry_type.equals("1"))*/
		        ctrl_par.setValue(SalaryCtrlParamBo.COMPARE_FIELD,rightvalue);
			/*else
				ctrl_par.setValue(SalaryCtrlParamBo.F_COMPARE_FIELD,rightvalue);*/
		        String tempflag=(String) hm.get("tempflag");
		        StringBuffer str = new StringBuffer();
		        if(_rightvalue!=null&&!_rightvalue.equals(rightvalue)){
	        		str.append("<tr>");
	        		str.append("<td>信息变动</td>");
	        		str.append("<td>"+SalaryTemplateBo.getSalarySet(_rightvalue)+"</td>");
	        		str.append("<td>"+SalaryTemplateBo.getSalarySet(rightvalue)+"</td>");
	        		str.append("</tr>");
		        }
		        if("check".equals(tempflag)){
		        	String addrightvalue = (String)hm.get("addrightvalue");
		        	String delrightvalue = (String)hm.get("delrightvalue");
		        	String _addrightvalue = ctrl_par.getValue(SalaryCtrlParamBo.ADD_MAN_FIELD);
		        	String _delrightvalue = ctrl_par.getValue(SalaryCtrlParamBo.DEL_MAN_FIELD);
		        	ctrl_par.setValue(SalaryCtrlParamBo.ADD_MAN_FIELD,addrightvalue);
		        	ctrl_par.setValue(SalaryCtrlParamBo.DEL_MAN_FIELD,delrightvalue);
		        	if(addrightvalue!=null&&!addrightvalue.equals(_addrightvalue)){
		        		str.append("<tr>");
		        		str.append("<td>新增人员</td>");
		        		str.append("<td>"+SalaryTemplateBo.getSalarySet(_addrightvalue)+"</td>");
		        		str.append("<td>"+SalaryTemplateBo.getSalarySet(addrightvalue)+"</td>");
		        		str.append("</tr>");
		        	}
		        	if(delrightvalue!=null&&!delrightvalue.equals(_delrightvalue)){
		        		str.append("<tr>");
		        		str.append("<td>减少人员</td>");
		        		str.append("<td>"+SalaryTemplateBo.getSalarySet(_delrightvalue)+"</td>");
		        		str.append("<td>"+SalaryTemplateBo.getSalarySet(delrightvalue)+"</td>");
		        		str.append("</tr>");
		        	}
		        }
				StringBuffer context = new StringBuffer();
				if(str.length()>0){
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					RecordVo vo=new RecordVo("salarytemplate");
					vo.setInt("salaryid", Integer.parseInt(salaryid));
					vo = dao.findByPrimaryKey(vo);
					context.append(vo.getString("cname")+"("+salaryid+")"+"的属性值进行变更<br>");
					context.append("<table>");
					context.append("<tr>");
					context.append("<td>属性名</td>");
					context.append("<td>变化前</td>");
					context.append("<td>变化后</td>");
					context.append("</tr>");
					context.append(str);
					context.append("</table>");
					this.getFormHM().put("@eventlog", context.toString());
				}
		    	ctrl_par.saveParameter();
	    		hm.put("check","ok");
	    	}
	    	this.getFormHM().put("entry_type",entry_type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	 /**
     * 将设置对比值设置为list
     * @param salaryid
     * @param rightvalue 设置对比值
     * @throws GeneralException
     */
	public ArrayList rightList(String salaryid,String rightvalue){
		ArrayList retlist=new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	
	/**
	 * 获取汇总选中指标
	 * @param salaryid
	 * @param rightvalue
	 * @return
	 */
	public ArrayList rightCollectList(String salaryid,String rightvalue){
		ArrayList retlist=new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			if(rightvalue.indexOf("sp_flag")!=-1){
				CommonData obj=new CommonData("sp_flag","审批状态");
				retlist.add(obj);
			}
			if(rightvalue.indexOf("appprocess")!=-1){
				CommonData obj=new CommonData("appprocess","审批意见");
				retlist.add(obj);
			}
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}

		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	 /**
     * 查询薪资项目子集
     * @param salaryid
     * @param rightvalue 设置对比值
     * @throws GeneralException
     */
	public ArrayList leftList(String salaryid,String rightvalue){
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList retlist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
		sql.append(" and itemtype in ('N','A'");
		sql.append(")");
		if(rightvalue!=null&&!"".equals(rightvalue))
		{
	    	sql.append("and itemid not in ('");
    		sql.append(rightvalue);
	    	sql.append("')");
		}
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	public ArrayList leftList1(String salaryid,String rightvalue){
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList retlist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
//		sql.append(" and itemtype in ('N','A','D'");
//		sql.append(")");
		if(rightvalue!=null&&!"".equals(rightvalue))
		{
	    	sql.append("and itemid not in ('");
    		sql.append(rightvalue);
	    	sql.append("')");
		}
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	/**
	 * 获取汇总指标
	 * @param salaryid
	 * zhaoxg add 2014-11-13 搜房网需求
	 * @return
	 */
	public ArrayList CollectList(String salaryid){
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList retlist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
		sql.append(salaryid);
		sql.append(" and upper(CODESETID) in ('UN','UM') ");//原来可以选择其他代码型指标，但是由于汇总表未完成，暂时仅可选择关联机构的指标 zhanghua2017-8-24
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			CommonData obj1=new CommonData("UNUM","单位&部门");
			retlist.add(obj1);
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return retlist;
	}
	/**
	 * 获取汇总被选指标
	 * @param salaryid
	 * @param rightvalue
	 * @return
	 */
	public ArrayList leftCollectList(String salaryid,String rightvalue){
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList retlist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
		sql.append(salaryid);
		sql.append(" and itemtype in ('N'");
		sql.append(")");
		if(rightvalue!=null&&!"".equals(rightvalue))
		{
	    	sql.append("and itemid not in ('");
    		sql.append(rightvalue);
	    	sql.append("')");
		}
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				if(!"a00z1".equalsIgnoreCase(dynabean.get("itemid").toString().trim())&&!"a0000".equalsIgnoreCase(dynabean.get("itemid").toString().trim())&&!"a00z3".equalsIgnoreCase(dynabean.get("itemid").toString().trim())){
					CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
					retlist.add(obj);
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	/**
	 * 获取审批指标  搜房网  zhaoxg 2013-11-18
	 * @param salaryid
	 * @param rightvalue
	 * @return
	 */
	public ArrayList leftCollectList1(String salaryid,String rightvalue){
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList retlist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
		sql.append(salaryid);
//		sql.append(" and itemtype in ('N'");
//		sql.append(")");
		if(rightvalue!=null&&!"".equals(rightvalue)){
			rightvalue=rightvalue+"','A0100','A0000";//加上不显示的几项  zhaoxg 2013-11-20
		}else{
			rightvalue="A0100','A0000";//加上不显示的几项  zhaoxg 2013-11-20
		}
		
		if(rightvalue!=null&&!"".equals(rightvalue))
		{
	    	sql.append(" and itemid not in ('");
    		sql.append(rightvalue);
	    	sql.append("')");
		}
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			if(rightvalue.indexOf("sp_flag")==-1){
				CommonData obj=new CommonData("sp_flag","审批状态");
				retlist.add(obj);
			}
			if(rightvalue.indexOf("appprocess")==-1){
				CommonData obj=new CommonData("appprocess","审批意见");
				retlist.add(obj);
			}
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}

		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	/**
	 * 
	 * @Title: leftVerifyList   
	 * @Description:  加载备选的审核报告输出指标  
	 * @param @param salaryid
	 * @param @param rightvalue
	 * @param @return 
	 * @author wangrd
	 * @return ArrayList    
	 * @throws
	 */
    public ArrayList leftVerifyList(String salaryid,String rightvalue){
        ContentDAO dao = new ContentDAO(this.frameconn);
        ArrayList retlist=new ArrayList();
        StringBuffer sql=new StringBuffer();
        sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
        sql.append(salaryid);
        if(rightvalue!=null&&!"".equals(rightvalue))
        {
            sql.append("and itemid not in ('");
            sql.append(rightvalue);
            sql.append("')");
        }
        sql.append("group by itemid,itemdesc,sortid order by sortid");
        ArrayList dylist = null;
        try {
            dylist = dao.searchDynaList(sql.toString());
            for(Iterator it=dylist.iterator();it.hasNext();){
                DynaBean dynabean=(DynaBean)it.next();
                String itemid =dynabean.get("itemid").toString().trim();
                if ("A0100".equalsIgnoreCase(itemid) || "A0000".equalsIgnoreCase(itemid) || "Nbase".equalsIgnoreCase(itemid)  )
                    continue; 
                if ("B0110".equalsIgnoreCase(itemid) || "E0122".equalsIgnoreCase(itemid) || "A0101".equalsIgnoreCase(itemid)  )
                    continue;     
                CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
                retlist.add(obj);
            }
        } catch (GeneralException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retlist;
    }
	 /**
     * 获取对比设置的值
     * @param salaryid
     * @return rightvalue
     */
	public String rightValue(String salaryid/*,String entry_type*/){
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
		String rightvalue ="";
		/**审批比对*/
		/*if(entry_type.equals("1"))*/
			rightvalue=ctrl_par.getValue(SalaryCtrlParamBo.COMPARE_FIELD);
		/**发放比对*//*
		else 
			rightvalue=ctrl_par.getValue(SalaryCtrlParamBo.F_COMPARE_FIELD);*/
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		return rightvalue;
	}
	 /**
     * 获取新增人员设置的值 zhaoxg add
     * @param salaryid
     * @return rightvalue
     */
	public String addrightValue(String salaryid){
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
		String rightvalue ="";
		rightvalue=ctrl_par.getValue(SalaryCtrlParamBo.ADD_MAN_FIELD);
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		return rightvalue;
	}
	 /**
     * 获取减少人员设置的值 zhaoxg add
     * @param salaryid
     * @return rightvalue
     */
	public String delrightValue(String salaryid){
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
		String rightvalue ="";
		rightvalue=ctrl_par.getValue(SalaryCtrlParamBo.DEL_MAN_FIELD);
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		return rightvalue;
	}
	/**
	 * 判断和原来记录是否不同
	 * @param value
	 * @param gzbo
	 * @param salaryid
	 * @return
	 */
	public boolean rightIsChange(String value,SalaryTemplateBo gzbo,String salaryid){
		boolean flag = true;
		try{
			String rightvalue="";
			RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
			if(vo!=null&&vo.getString("lprogram")!=null&&vo.getString("lprogram").indexOf("hidden_items")!=-1
				&&vo.getString("lprogram").indexOf("hidden_item")!=-1){
	    		Document doc = PubFunc.generateDom(vo.getString("lprogram"));
	    		Element root = doc.getRootElement();
				Element hidden_items = root.getChild("hidden_items");
				if(hidden_items!=null){
					List list = hidden_items.getChildren();
					if(list.size()>0){
						for(int i =0;i<list.size();i++){
							Element child = (Element)list.get(i);
							if(child.getAttributeValue("user_name")==null)
							{
								rightvalue = child.getText();//显示指标
							}
						}
					}
				}
			}			
    		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
    		rightvalue="'"+rightvalue+"'";
    		String[] _rightvalue = rightvalue.split(",");
    		
    		value=value!=null?value.replaceAll(",","','"):"";
    		value="'"+value+"'";
    		String[] _value = value.split(",");
    		if(_rightvalue.length!=_value.length){
    			flag = false;
    		}else{
    			for(int i=0;i<_value.length;i++){
    				if(rightvalue.indexOf(_value[i])==-1){
    					flag = false;
    					break;
    				}
    			}
    		}
    		
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	public String getChangeContext(Document doc,Document doc1,String salaryid,String gz_module) throws GeneralException{
		StringBuffer context = new StringBuffer();
		try
		{	
			HashMap map = new HashMap();//变化前			
			Element root = doc.getRootElement();
			this.getElementContext(root, map, "");
			
			HashMap map1 = new HashMap();//变化后			
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
					}
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
}
