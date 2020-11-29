package com.hjsj.hrms.transaction.gz.gz_amount.tax;

import com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class HireViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		
		String salaryid = (String)reqhm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		reqhm.remove("salaryid");
		String hidenflag ="";
		ArrayList fieldlist = new ArrayList();
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		reqhm.remove("flag");
		HttpSession session2 = (HttpSession)this.getFormHM().get("session2");
		if(session2!=null){
			AcountingForm acountingForm=(AcountingForm)session2.getAttribute("accountingForm");
			if(acountingForm!=null){
				hidenflag = acountingForm.getHidenflag();
		//		fieldlist = acountingForm.getFieldlist();
			}
		}
		if("sp".equals(flag)){//审批审批  搜房网  zhaoxg 2013-11-18
			hm.put("gz_table",gzSpTable(salaryid,flag));
		}else{
			hm.put("gz_table",gzTable(salaryid,flag,hidenflag));
		}		
		hm.put("salaryid",salaryid);
	}
	/**
	 * 薪资审批增加显示/隐藏指标的功能   搜房网需求  zhaoxg 2013-11-18
	 * @param salaryid
	 * @param flag
	 * @return
	 */
	public String gzSpTable(String salaryid,String flag){
		StringBuffer gztable = new StringBuffer();
		try {		
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			ArrayList fieldlist = gzbo.getFieldlist();
			gztable.append("<table width=\"100%\"  border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\">");
			gztable.append("<tr class=\"fixedHeaderTr\">");
			gztable.append("<td width=\"70%\" align=\"center\" class=\"TableRow\"  style=\"border-top:none;border-left:none;\">指标名称&nbsp;</td>");
			gztable.append("<td width=\"20%\" align=\"center\" class=\"TableRow\"  style=\"border-top:none;border-left:none;border-right:none;\">状态&nbsp;</td></tr>");


			RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
			if(vo!=null&&vo.getString("lprogram")!=null&&vo.getString("lprogram").indexOf("hidden_items")!=-1
				&&vo.getString("lprogram").indexOf("hidden_item")!=-1){
		    		Document doc;
					try {
						doc = PubFunc.generateDom(vo.getString("lprogram"));
					String _str=",APPPROCESS,SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,A00Z0,A00Z1,NBASE,";
		    		Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items");
					if(hidden_items!=null){
						List list = hidden_items.getChildren();
						if(list.size()>0){
							for(int i =0;i<list.size();i++){
								Element temp = (Element)list.get(i);
								if(temp.getAttributeValue("user_name")!=null&&temp.getAttributeValue("user_name").equalsIgnoreCase(this.userView.getUserName()))
								{
								if(temp.getAttributeValue("user_name").toString().equalsIgnoreCase(this.userView.getUserName()))
									{
									String str = temp.getText();//隐藏指标
//									if(str.length()>0){//注释掉了，如果没有隐藏即全显示，那么就不对了  zhaoxg 2013-5-9
									str = ","+str+",";
									ArrayList alist = new ArrayList();
									for(int j =0;j<fieldlist.size();j++){
										Field field = (Field)fieldlist.get(j);
										Field cfield = (Field)field.clone();
										if(str.indexOf(","+field.getName()+",")!=-1){
											cfield.setVisible(false);
											
										}else{
											if("a01z0".equalsIgnoreCase(field.getName())){//初试化隐藏
												if(gzbo.getCtrlparam()!=null)
												{
													String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
													if(a01z0Flag==null|| "0".equals(a01z0Flag))
													{

													}
													else
														cfield.setVisible(true);
												}
												else
													cfield.setVisible(true);
											}
											else if("add_flag".equalsIgnoreCase(field.getName())){
												//追加
											}else if("a0000".equalsIgnoreCase(field.getName())|| "a0100".equalsIgnoreCase(field.getName())){
												//系统默认不显示指标
											}
											else if(_str.indexOf(","+field.getName().toUpperCase()+",")==-1&& "0".equalsIgnoreCase(this.userView.analyseFieldPriv(field.getName())))
											{
												 
											}
											else
												cfield.setVisible(true);
										}
										
										//dengcan 北京移动自动隐藏 审批状态和过程
										if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
										{ 
											 	if("sp_flag".equalsIgnoreCase(field.getName())|| "appprocess".equalsIgnoreCase(field.getName()))
											 		continue;
										 
										}
										if(!"add_flag".equalsIgnoreCase(field.getName())){//隐藏追加指标，赵旭光 2013-4-7
											alist.add(cfield);
										}
//										alist.add(cfield);
									}
									if(alist.size()>0)
										fieldlist =alist;
//									}
									break;
									}	
							}else if(temp.getAttributeValue("user_name")==null|| "".equals(temp.getAttributeValue("user_name"))){//如果首次进入没有个人的信息，那么以后台为准   zhaoxg

								String str = temp.getText();//后台为显示指标
//								if(str.length()>0){//注释掉了，如果没有隐藏即全显示，那么就不对了  zhaoxg 2013-5-9
								str = ","+str+",";
								ArrayList alist = new ArrayList();
								for(int j =0;j<fieldlist.size();j++){
									Field field = (Field)fieldlist.get(j);
									Field cfield = (Field)field.clone();
									if(str.indexOf(","+field.getName()+",")!=-1){
										cfield.setVisible(true);
										
									}else{
										cfield.setVisible(false);
									}
									
									//dengcan 北京移动自动隐藏 审批状态和过程
									if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
									{ 
										 	if("sp_flag".equalsIgnoreCase(field.getName())|| "appprocess".equalsIgnoreCase(field.getName()))
										 		continue;
									 
									}
									if(!"add_flag".equalsIgnoreCase(field.getName())){//隐藏追加指标，赵旭光 2013-4-7
										alist.add(cfield);
									}
								}
								if(alist.size()>0)
									fieldlist =alist;							
							}
							}
						}
					}
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}					
			String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  			
			for(int i=0;i<fieldlist.size();i++){
				Field field = (Field)fieldlist.get(i);
				FieldItem fielditem = DataDictionary.getFieldItem(field.getName().toLowerCase());
				if(field==null||field.getName()==null)
					continue;
				int check =0; //0表示隐藏，1表示显示
				if(field.isVisible())
					check=1;
				if("a01z0".equalsIgnoreCase(field.getName())&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
					continue;
				
				if("add_flag".equalsIgnoreCase(field.getName())||
						"A0100".equalsIgnoreCase(field.getName())|| "A0000".equalsIgnoreCase(field.getName()))//不显示追加标记和人员库，搜房网  zhaoxg
				{
					continue;
				}
				gztable.append("<tr>");
				gztable.append("<td class=\"RecordRow\" style=\"border-top:none;border-left:none;\" >"+field.getLabel()+"</td>");
				gztable.append("<td class=\"RecordRow\"  style=\"border-top:none;border-left:none;border-right:none;\">");
				gztable.append("<select name=\""+field.getName()+"\" size=1>");
				if(check>0){
					gztable.append("<option value=\""+check+"\" selected>显示</option>");
					gztable.append("<option value=\"0\">隐藏</option>");
				}else{
				
					gztable.append("<option value=\""+1+"\">显示</option>");
					gztable.append("<option value=\"0\" selected>隐藏</option>");
				}
				gztable.append("</select>");
				gztable.append("</td></tr>");
			}
			gztable.append("</table>");
						
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gztable.toString();
	}
	public String gzTable(String salaryid,String flag,String hidenflag ){
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		try {
			gzbo.syncGzTableStruct();
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList fieldlist = gzbo.getFieldlist();
		StringBuffer gztable = new StringBuffer();
		gztable.append("<table width=\"100%\"  border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\">");
		gztable.append("<tr class=\"fixedHeaderTr\">");
//		gztable.append("<td width=\"10%\" align=\"center\" class=\"TableRow\" nowrap>&nbsp;</td>");
		gztable.append("<td width=\"70%\" align=\"center\" class=\"TableRow\" style=\"border-top:none;border-left:none;\">指标名称&nbsp;</td>");
		gztable.append("<td width=\"20%\" align=\"center\" class=\"TableRow\" style=\"border-top:none;border-left:none;border-right:none;\">状态&nbsp;</td></tr>");
		
		if("1".equals(hidenflag)){
			//xieguiquan  2010-08-25
			RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
			if(vo!=null&&vo.getString("lprogram")!=null&&vo.getString("lprogram").indexOf("hidden_items")!=-1
				&&vo.getString("lprogram").indexOf("hidden_item")!=-1){
			//	System.out.println(vo.getString("lprogram"));
		    		Document doc;
					try {
						doc = PubFunc.generateDom(vo.getString("lprogram"));
					String _str=",APPPROCESS,SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,A00Z0,A00Z1,NBASE,";
		    		Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items");
					if(hidden_items!=null){
						List list = hidden_items.getChildren();
						if(list.size()>0){
							for(int i =0;i<list.size();i++){
								Element temp = (Element)list.get(i);
								if(temp.getAttributeValue("user_name")!=null&&temp.getAttributeValue("user_name").toString().equalsIgnoreCase(this.userView.getUserName()))
									{
									String str = temp.getText();//隐藏指标
//									if(str.length()>0){//注释掉了，如果没有隐藏即全显示，那么就不对了  zhaoxg 2013-5-9
									str = ","+str+",";
									ArrayList alist = new ArrayList();
									for(int j =0;j<fieldlist.size();j++){
										Field field = (Field)fieldlist.get(j);
										Field cfield = (Field)field.clone();
										if(str.indexOf(","+field.getName()+",")!=-1){
											cfield.setVisible(false);
											
										}else{
											if("a01z0".equalsIgnoreCase(field.getName())){//初试化隐藏
												if(gzbo.getCtrlparam()!=null)
												{
													String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
													if(a01z0Flag==null|| "0".equals(a01z0Flag))
													{

													}
													else
														cfield.setVisible(true);
												}
												else
													cfield.setVisible(true);
											}
											else if("add_flag".equalsIgnoreCase(field.getName())){
												//追加
											}else if("a0000".equalsIgnoreCase(field.getName())|| "a0100".equalsIgnoreCase(field.getName())){
												//系统默认不显示指标
											}
											else if(_str.indexOf(","+field.getName().toUpperCase()+",")==-1&& "0".equalsIgnoreCase(this.userView.analyseFieldPriv(field.getName())))
											{
												 
											}
											else
												cfield.setVisible(true);
										}
										
										//dengcan 北京移动自动隐藏 审批状态和过程
										if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
										{ 
											 	if("sp_flag".equalsIgnoreCase(field.getName())|| "appprocess".equalsIgnoreCase(field.getName()))
											 		continue;
										 
										}
										if(!"add_flag".equalsIgnoreCase(field.getName())){//隐藏追加指标，赵旭光 2013-4-7
											alist.add(cfield);
										}
//										alist.add(cfield);
									}
									if(alist.size()>0)
										fieldlist =alist;
//									}
									break;
									}
								
								
							}
						}
					}
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			
			//xieguiquan end
			boolean flagapp = false;
			if(gzbo.isApprove()){
				//gztable.append(gzTable(flag));
				flagapp=true;
				if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				{
						  
				}
				else{
//					gztable.append(gzTable(flag));
				}
					
			}
			String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  			
				for(int i=0;i<fieldlist.size();i++){
					Field field = (Field)fieldlist.get(i);
					FieldItem fielditem = DataDictionary.getFieldItem(field.getName().toLowerCase());
					if(field==null||field.getName()==null)
						continue;
					if(!flagapp&&("sp_flag".equalsIgnoreCase(field.getName())|| "appprocess".equalsIgnoreCase(field.getName())))
						continue;
					int check =0; //0表示隐藏，1表示显示
					if(field.isVisible())
						check=1;
					if("a01z0".equalsIgnoreCase(field.getName())&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
						continue;
					
					if("NBASE".equalsIgnoreCase(field.getName()))
					{
						String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
						if(!this.userView.isSuper_admin()&&manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
						{
							continue;
						}
					}
					else
					{
						if(fielditem!=null){
							if(this.userView.analyseFieldPriv(field.getName())==null)
								continue;
							if("0".equals(this.userView.analyseFieldPriv(field.getName())))
								continue;
							if("".equals(this.userView.analyseFieldPriv(field.getName())))
								continue;
						}
					}
					if("A0100".equalsIgnoreCase(field.getName())|| "A0000".equalsIgnoreCase(field.getName())){
						continue;
					}
					gztable.append("<tr>");
					gztable.append("<td class=\"RecordRow\" style=\"border-top:none;border-left:none;\" >"+field.getLabel()+"</td>");
					gztable.append("<td class=\"RecordRow\" style=\"border-top:none;border-left:none;border-right:none;\" >");
					gztable.append("<select name=\""+field.getName()+"\" size=1>");
					if(check>0){
						gztable.append("<option value=\""+check+"\" selected>显示</option>");
						gztable.append("<option value=\"0\">隐藏</option>");
					}else{
					
						gztable.append("<option value=\""+1+"\">显示</option>");
						gztable.append("<option value=\"0\" selected>隐藏</option>");
					}
					gztable.append("</select>");
					gztable.append("</td></tr>");
				}
				gztable.append("</table>");
						
		}else{
			if(gzbo.isApprove()){
				//dengcan 北京移动自动隐藏 审批状态和过程
				if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				{
					  
				}
				else						
					gztable.append(gzTable(flag));
			}
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select itemid,itemdesc,nwidth from salaryset where salaryid='");
		sqlstr.append(salaryid);
		sqlstr.append("' order by sortid");
		String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  			
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			RowSet rs = dao.search(sqlstr.toString());
//			int n=1;
			while(rs.next()){
				int check = rs.getInt("nwidth");
				String id = rs.getString("itemid");
				FieldItem fielditem = DataDictionary.getFieldItem(id);
				
				if("a01z0".equalsIgnoreCase(id)&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
					continue;
				
				
				
				if("NBASE".equalsIgnoreCase(id))
				{
					String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
					if(!this.userView.isSuper_admin()&&manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
					{
						continue;
					}
				}
				else
				{
					if(fielditem!=null){
						if(this.userView.analyseFieldPriv(id)==null)
							continue;
						if("0".equals(this.userView.analyseFieldPriv(id)))
							continue;
						if("".equals(this.userView.analyseFieldPriv(id)))
							continue;
					}
				}
				if("A0100".equalsIgnoreCase(id)|| "A0000".equalsIgnoreCase(id)){
					continue;
				}
				gztable.append("<tr>");
//				gztable.append("<td class=\"RecordRow\" nowrap>"+n+"</td>");
				gztable.append("<td class=\"RecordRow\" style=\"border-top:none;border-left:none;\" >"+rs.getString("itemdesc")+"</td>");
				gztable.append("<td class=\"RecordRow\"  style=\"border-top:none;border-left:none;border-right:none;\">");
				gztable.append("<select name=\""+id+"\" size=1>");
				if(check>0){
					gztable.append("<option value=\""+check+"\" selected>显示</option>");
					gztable.append("<option value=\"0\">隐藏</option>");
				}else{
					int nwidths = 0;
					if("NBASE".equalsIgnoreCase(id)){
						nwidths=5;
					}else if("b0100".equalsIgnoreCase(id)){
						nwidths=17;
					}else if("a00z0".equalsIgnoreCase(id)){
						nwidths=7;
					}else if("a00z1".equalsIgnoreCase(id)){
						nwidths=5;
					}else if("sp_flag".equalsIgnoreCase(id)){
						nwidths=10;
					}else if("AppProcess".equalsIgnoreCase(id)){
						nwidths=10;
					}else{
						if(fielditem!=null){
							nwidths=fielditem.getItemlength();
						}else{
							for(int i=0;i<fieldlist.size();i++){
								Field field = (Field)fieldlist.get(i);
								if(field.getName().equalsIgnoreCase(id)){
									nwidths=field.getLength();
									break;
								}
							}
						}
					}
					/* 标识：4270 没有设置过显示隐藏指标时，归属日期与归属次数不一致 xiaoyun 2014-9-16 start */
					if("a00z0".equalsIgnoreCase(id)|| "a00z1".equalsIgnoreCase(id)) {
						gztable.append("<option value=\""+nwidths+"\"  selected>显示</option>");
						gztable.append("<option value=\"0\">隐藏</option>");
					} else {
						gztable.append("<option value=\""+nwidths+"\">显示</option>");
						gztable.append("<option value=\"0\" selected>隐藏</option>");
					}
					/* 标识：4270 没有设置过显示隐藏指标时，归属日期与归属次数不一致 xiaoyun 2014-9-16 end */
				}
				gztable.append("</select>");
				gztable.append("</td></tr>");
//				n++;
			}
			gztable.append("</table>");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return gztable.toString();
	
	}
	public String gzTable(String flag){
		String sp_flag = "";
		String app = "";
		String[] arr=null;
		if(flag!=null&&flag.length()>0){
			arr=flag.split(",");
			if(arr.length==2){
				sp_flag=arr[0];
				app = arr[1];
			}
		}
		StringBuffer gztable = new StringBuffer();
		gztable.append("<tr>");
		//gztable.append("<td class=\"RecordRow\" nowrap>1</td>");
		gztable.append("<td class=\"RecordRow\" style=\"border-top:none;border-left:none;\" >审批状态</td>");
		gztable.append("<td class=\"RecordRow\" style=\"border-top:none;border-left:none;border-right:none;\" >");
		gztable.append("<select name=\"sp_flag\" size=1>");
		if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){
			sp_flag="0";
			app="0";
		}
		if("0".equals(sp_flag)){
			gztable.append("<option value=\"1\">显示</option>");
			gztable.append("<option value=\"0\" selected>隐藏</option>");
		}else{
			gztable.append("<option value=\"1\" selected>显示</option>");
			gztable.append("<option value=\"0\">隐藏</option>");
		}
		gztable.append("</select>");
		gztable.append("</td></tr>");
		gztable.append("<tr>");
		//gztable.append("<td class=\"RecordRow\" nowrap>2</td>");
		gztable.append("<td class=\"RecordRow\" style=\"border-top:none;border-left:none;\" >审批意见</td>");
		gztable.append("<td class=\"RecordRow\" style=\"border-top:none;border-left:none;border-right:none;\" >");
		gztable.append("<select name=\"appprocess\" size=1>");
		if("0".equals(app)){
			gztable.append("<option value=\"1\">显示</option>");
			gztable.append("<option value=\"0\" selected>隐藏</option>");
		}else{
			gztable.append("<option value=\"1\" selected>显示</option>");
			gztable.append("<option value=\"0\">隐藏</option>");
		}
		gztable.append("</select>");
		gztable.append("</td></tr>");
		
		return gztable.toString();
	
	}

}
