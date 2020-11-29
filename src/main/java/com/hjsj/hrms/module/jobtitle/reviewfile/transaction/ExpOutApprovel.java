package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.jobtitle.configfile.businessobject.RepresentativeMaterialsBo;
import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutWordBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
/***
 * 导出外审材料 
 * */
@SuppressWarnings("serial")
public class ExpOutApprovel extends IBusiness{
	private String approvTabid="";//配置公共模板号
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException{
	
		try {
			//导出入口分两块 上会材料导出代表作 评审会议导出代表作 
			String masterpieceType=(String)this.getFormHM().get("masterpieceType");
			String file="";
			RepresentativeMaterialsBo bo=new RepresentativeMaterialsBo(this.userView, this.frameconn);
			HashMap map= RepresentativeMaterialsBo.getParamMap();
			if(map==null||map.size()==0) {
				bo.getParamValue();
			}
			if(StringUtils.isNotEmpty(masterpieceType)) {//评审会议导出代表作 
				String w0301_e=(String)this.getFormHM().get("w0301_e");
				w0301_e=PubFunc.decrypt(w0301_e);
				String groupids=(String)this.getFormHM().get("groupids");
				String groupid="";
				String[] groupArry=groupids.split(",");
				for(int i=0;i<groupArry.length;i++) {
					groupid+=",'"+PubFunc.decrypt(groupArry[i])+"'";
				}
				ArrayList<HashMap> list=getPersonList(w0301_e, groupid.substring(1));
				file=bo.getOutFile(list);
				
			}else {
				ArrayList<MorphDynaBean> list=(ArrayList<MorphDynaBean>)this.getFormHM().get("data");
				ArrayList<HashMap> list_cast=new ArrayList<HashMap>();
				for(MorphDynaBean bean:list) {
					list_cast.add(PubFunc.DynaBean2Map(bean));
				}
				file=bo.getOutFile(list_cast);
			}
			this.getFormHM().put("filename", PubFunc.encrypt(file));
			this.getFormHM().put("flag", true);
			
			/*
			 * 
			 * //flag 1 弹出框页面 显示 String flag=(String)this.getFormHM().get("flag"); DomXml
			 * domxml = new DomXml(); List list=new ArrayList(); //常量表：用于系统所用的控制参数
			 * ContentDAO dao=new ContentDAO(this.frameconn); RowSet rs=null; rs =
			 * dao.search("select Str_Value from constant where Constant=?",Arrays.asList(
			 * new String[]{"JOBTITLE_CONFIG"})); String xmlDoc=""; if(rs.next()){
			 * xmlDoc=rs.getString("Str_Value"); } list =
			 * domxml.parse(xmlDoc,"","","<template type=template_id=/>","initmap");//解析xml，
			 * 需要返回的值的list HashMap map=new HashMap(); map=(HashMap)list.get(1);
			 * 
			 * //论文送审 配置模板号 String paperApprov=(String)map.get("5");
			 * approvTabid=paperApprov; //材料审查 String materiApp=(String)map.get("6");
			 * if("1".equals(flag)){ //页面显示模板内容列表 List itemList=getTemplateSet(paperApprov);
			 * this.getFormHM().put("itemJson", JSONArray.fromObject(itemList).toString());
			 * 
			 * }else if("2".equals(flag)){//导出文件
			 * 
			 * String colStr=(String)this.getFormHM().get("colStr");//"xx`xx`xx`xx,....."
			 * fieldSet fieldItem fieldDesc itemType codeid subflag String
			 * personStr=(String)this.getFormHM().get("personStr");//选择人员
			 * "a0100`ins_id`tabid","","" String
			 * OtherMaterial=SystemConfig.getPropertyValue("OtherMaterial");
			 * //模板号：页签，模板号：页签，。。。。。 String filename=outFile(colStr, personStr,
			 * OtherMaterial, materiApp); this.getFormHM().put("filename",
			 * PubFunc.encrypt(filename)); this.getFormHM().put("flag", true); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("flag", false);
			this.getFormHM().put("emsg", e.getMessage());
		}
		
		
		
	} 
	
	
	private ArrayList<HashMap> getPersonList(String w0301,String groupid) throws Exception{
		ArrayList<HashMap> returnList=new ArrayList<HashMap>();
		String sql="select *from w05 where W0301='"+w0301+"'  and  w0501 in (select w0501 from zc_categories_relations where categories_id in ("+groupid+"))";
		ArrayList<LazyDynaBean> list=(ArrayList<LazyDynaBean>) ExecuteSQL.executeMyQuery(sql, this.frameconn);
		for(LazyDynaBean bean:list) {
			HashMap bean_m= new HashMap();
			String a0100=(String)bean.get("w0505");//a0100
			bean_m.put("a0100", a0100);
			String nbase=(String)bean.get("w0503");//nbase
			bean_m.put("nbase", nbase);
			String link_1=(String)bean.get("w0537");//链接1
			String link_2=(String)bean.get("w0535");//链接2
			String link=link_1;
			///general/template/edit_form.do?b_query=link&tabid=302&ins_id=623&taskid=2c6z3n7Q3cQPAATTP3HJDPAATTP&sp_flag=2&returnflag=noback&taskid_validate=kUNb4wPAATTP2HJBPAATTPsPXjWziiF0L2Q9wPAATTP3HJDPAATTPPAATTP3HJDPAATTP
			if(StringUtils.isEmpty(link_1)) {
				link=link_2;
			}
			//解析链接 获取tabid  ins_id  task_id
			link = link.substring(link.indexOf("tabid=") + 6);
			String tabid = link.substring(0, link.indexOf("&"));
			link = link.substring(link.indexOf("ins_id"));
			String ins_id = link.substring(link.indexOf("ins_id=") + 7, link.indexOf("&"));
			link = link.substring(link.indexOf("taskid"));
			String task_id = link.substring(link.indexOf("taskid=") + 7, link.indexOf("&"));
			bean_m.put("tabid", tabid);
			bean_m.put("ins_id", ins_id);
			bean_m.put("task_id", task_id);
			returnList.add(bean_m);
		}
		return returnList;
	}
	
	/***
	 * 
	 * @param colStr 选择模板指标
	 * @param personStr选择人员
	 * @param OtherMaterial其他材料
	 * @param materiApp 材料审查模板
	 * @return
	 */
	public String outFile(String colStr,String personStr,String OtherMaterial,String materiApp) throws Exception{
		String fileName="";
		String[] personArry=personStr.split(",");
		String[] colArry=colStr.split(",");
		String subSetId="";
		//templet_tabid
		ArrayList<String> personList=new ArrayList<String>();
		StringBuffer sbf=new StringBuffer();
		//按照用户名生成文件夹
		String dirPath=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ this.userView.getUserName()+"_templet";
		RowSet rs=null;
		RowSet rs1=null;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
        String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			org.jdom.Document doc=null;
			XPath findPath=null;
			List childlist=null;
			//分析人员数组数据 personArry a0100`ins_id`tabid`task_id 
			HashMap<String, String> tabidMap=new HashMap<String, String>();//（tabid,a0100`ins_id）
			HashMap<String, String> taskIdMap=new HashMap<String, String>();//人员 task_id  a0100,ins_id:task_id
			
			boolean b01Flag=false;
			String b0110="";
			for (int i = 0; i < personArry.length; i++) {
				String tabid=personArry[i].split("`")[2];
				taskIdMap.put(personArry[i].split("`")[0]+","+personArry[i].split("`")[1],personArry[i].split("`")[3]);
				if(tabidMap.containsKey(tabid)){
					tabidMap.put(tabid, tabidMap.get(tabid)+","+personArry[i].split("`")[0]+"`"+personArry[i].split("`")[1]);
				}else{
					tabidMap.put(tabid, personArry[i].split("`")[0]+"`"+personArry[i].split("`")[1]);
				}
				
			}
			
			StringBuffer sqlCol=new StringBuffer();
			for (int i = 0; i < colArry.length; i++) {
				if(!"D".equals(colArry[i].split("`")[3])){
					sqlCol.append(colArry[i].split("`")[1]);
				}else{
					sqlCol.append(Sql_switcher.dateToChar(colArry[i].split("`")[1])+" as "+colArry[i].split("`")[1]);
				}
				sqlCol.append(",");
				if("b0110_1".equalsIgnoreCase(colArry[i].split("`")[1])||"b0110_2".equalsIgnoreCase(colArry[i].split("`")[1])) {
					b01Flag=true;
					b0110=colArry[i].split("`")[1];
				}
			}
			sqlCol.append(" basepre,A0100,ins_id ");//添加库前缀列
			
			ArrayList<HashMap<String, String>> personMapLis=new ArrayList<HashMap<String,String>>();
			HashMap<Object,Object> subSetListMap=new HashMap<Object, Object>();
			StringBuffer sbfIns=new StringBuffer();
			StringBuffer sbfA01=new StringBuffer();
			sbf.delete(0, sbf.length());
			
			for(String tabid:tabidMap.keySet()){//{221=00000028`182,00000027`182,00000024`182,.....
				sbfIns.delete(0, sbfIns.length());
				sbfA01.delete(0, sbfA01.length());
				if(tabidMap.get(tabid)!=null&&tabidMap.get(tabid).length()>0){
					String[] a01Ins_id=tabidMap.get(tabid).split(","); //a0100`ins_id  
					for (int i = 0; i < a01Ins_id.length; i++) {
						sbfIns.append(a01Ins_id[i].split("`")[1]);
						sbfA01.append("'"+a01Ins_id[i].split("`")[0]+"'");
						if(i<a01Ins_id.length-1){
							sbfA01.append(",");
							sbfIns.append(",");
						}
					}
					sbf.append(" union all ");
					sbf.append("select "+sqlCol.toString()+","+tabid +" as tabid");//基于每个人的模板查询
					sbf.append(" from templet_"+tabid);
					sbf.append(" where ins_id in ( ");
					sbf.append(sbfIns.toString());
					sbf.append(" ) and a0100 in (");
					sbf.append(sbfA01.toString());
					sbf.append(" )");
				}
				
			}
			if(sbf.length()>0){
				rs=dao.search(sbf.toString().substring(10));//去除sql开头的union
				ArrayList<HashMap<String,String>> subSetList=null;
				HashMap<String,String> subSetMap=null;//子集附件内容
				while(rs.next()){
					subSetList=new ArrayList<HashMap<String,String>>();
					HashMap<String,String> map=new HashMap<String,String>();
					String nbase=rs.getString("basepre");
					String a0100=rs.getString("a0100");
					map.put("A0100",a0100);
					map.put("basepre",nbase);//库前缀
					map.put("ins_id", rs.getString("ins_id"));
					map.put("tabid", rs.getString("tabid"));
					map.put("task_id", PubFunc.decrypt(taskIdMap.get(a0100+","+rs.getString("ins_id"))));
					if(b01Flag) {//选择单位指标只选择一条数据查找顶级节点
						if(StringUtils.isNotEmpty(rs.getString(b0110))) {
							b0110=rs.getString(b0110);
							b01Flag=false;
						}
					}
					for (int j = 0; j < colArry.length; j++) {
						if(!"1".equals(colArry[j].split("`")[5])){//附件子集不查
							if("N".equals(colArry[j].split("`")[3]))
								map.put(colArry[j].split("`")[1],rs.getDouble(colArry[j].split("`")[1])+"");
							else{
								String value=rs.getString(colArry[j].split("`")[1]);
								if(!"0".equals(colArry[j].split("`")[4])){//代码类翻译
									if(StringUtils.isNotEmpty(value)){
										if("UN".equalsIgnoreCase(colArry[j].split("`")[4])||"UM".equalsIgnoreCase(colArry[j].split("`")[4]))//部门UM  单位 UN
										{
											map.put(colArry[j].split("`")[1],getCodeName(value, colArry[j].split("`")[4]));
										}else
											map.put(colArry[j].split("`")[1],AdminCode.getCodeName(colArry[j].split("`")[4], value));
										
									}else
										map.put(colArry[j].split("`")[1],"");
								}else {
									if("M".equals(colArry[j].split("`")[3]))//大文本类型填写带有html标签内容过滤
										map.put(colArry[j].split("`")[1],(StringUtils.isEmpty(value)?"":value.replaceAll("<[.[^>]]*>","")));
									else
										map.put(colArry[j].split("`")[1],(StringUtils.isEmpty(value)?"":value));
								}
							}
						}else{//解析子集
							if(StringUtils.isEmpty(subSetId)) {
								subSetId=colArry[j].split("`")[0];
							}
							String subXml=rs.getString(colArry[j].split("`")[1]);
							doc=PubFunc.generateDom(subXml);
							findPath = XPath.newInstance("/records");// 取得符合条件的节点  xpath="/sub_para/para";
							childlist=findPath.selectNodes(doc);	
							int attchindex=0;
							if(childlist!=null&&childlist.size()>0){
								Element element=(Element)childlist.get(0);
								String[] subArry=element.getAttributeValue("columns").split("`");//插入子集列
								for (int i = 0; i < subArry.length; i++) {
									if("attach".equalsIgnoreCase(subArry[i])){//记录上传文件指标下标位置
										attchindex=i;
										break;
									}
								}
								//	text=filename+'|'+path+'|'+localname+'|'+size+'|'+id+'|'+m+'|type:'+filetype ;
								List list=element.getChildren();//子集记录
								
								String filePath="";
								for (int i = 0; i < list.size(); i++) {
									Element el=(Element)list.get(i);//
									String state=el.getAttributeValue("state");
									if("D".equalsIgnoreCase(state))
										continue;
									String record=el.getText();
									subSetMap=new HashMap<String, String>();
									for(int k=0;k<subArry.length;k++) {
										if("attach".equals(subArry[k]))
											continue;
										subSetMap.put(subArry[k].toUpperCase(), record.split("`")[k]);
										
									}
									
									subSetMap.put("index", (i+1)+"");
									subSetMap.put("A0100", a0100);
									subSetList.add(subSetMap);
									
									if(record.indexOf(",")>-1){
										String[] recordArr=record.split("`")[attchindex].split(",");
										for (int k = 0; k < recordArr.length; k++) {
											String[] recordFile=recordArr[k].split("\\|");
											filePath+=","+(i+1)+"`"+recordFile[1]+System.getProperty("file.separator")+recordFile[0]+"`"+recordFile[2];
										}
									}else{
										if(record.split("`")!=null&&record.split("`").length>0) {//上传附件为空时处理
											String[] recordFile=record.split("`")[attchindex].split("\\|");
											//| 分隔 记录当前附件归属子集下标
											filePath+=","+(i+1)+"`"+recordFile[1]+System.getProperty("file.separator")+recordFile[0]+"`"+recordFile[2];//存放文件路径等信息
										}
									}
									
									
								}
								if(StringUtils.isNotEmpty(filePath))
									map.put("filePath", filePath.substring(1));
								
							}
						}
					}
					rs1=dao.search("select "+chk+" from "+nbase+"A01 where a0100='"+a0100+"'");
					if(rs1.next()){
						map.put("uniqueIdenfi", rs1.getString(chk));//唯一标识
						subSetListMap.put(rs1.getString(chk), subSetList);
					}
					personMapLis.add(map);
				}
			}
			
			File file=new File(dirPath);
			if(file.exists()&&file.isDirectory()){
				deleteDirOrFile(dirPath);
			}
			file.mkdir();
			//导出同行评议花名册
			excelFile(personMapLis,null ,colArry,b0110,"template");//excel 汇总文件
			rs=null;
			rs=dao.search("select sub_domain from template_set where tabid="+approvTabid+" and setName='"+subSetId+"'");
			ArrayList subSetNameList=new ArrayList();
			if(rs.next()) {
				String sub_domain=rs.getString("sub_domain");
				if(StringUtils.isNotEmpty(sub_domain)) {
					doc=PubFunc.generateDom(sub_domain);
					findPath = XPath.newInstance("/sub_para/field");// 取得符合条件的节点  xpath="/sub_para/para";
					childlist=null;
					childlist=findPath.selectNodes(doc);	
					if(childlist!=null&&childlist.size()>0){
						for (int i = 0; i < childlist.size(); i++) {
							Element el=(Element)childlist.get(i);
							String name=el.getAttributeValue("name").toUpperCase();
							String title=el.getAttributeValue("title");
							if(!"ATTACH".equals(name)) {
								subSetNameList.add(name+"`"+title);
							}
						}
						subSetListMap.put("colArry", subSetNameList);
					}
					//导出代表作摘要
					excelFile(null,subSetListMap ,null,b0110,"subSet");
				}
			}
			
			//导出pdf文件  一个人三个pdf文件 文件名规则 唯一标识 代表作文件名命名格式：一人三个文件： 唯一标识_D1.pdf 唯一标识_D2.pdf  唯一标识_D3.pdf   其他材料文件名命名格式 唯一标识_C1.pdf（一人一个 配置文件配置）
			if(createPdfFile(personMapLis, OtherMaterial,dirPath)){
				fileName=createZipFile(dirPath);
			}
			
		} catch (Exception e) {
			throw e;
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs1);
			deleteDirOrFile(dirPath);
		}
		return fileName;
	}
	
	
	//根据选择的单位部门指标 
		public String getCodeName(String id,String codeId){
		
			String str="";
			while(id.length()>0){
				if(StringUtils.isNotEmpty(AdminCode.getCodeName(codeId.toUpperCase(), id)))
					str=AdminCode.getCodeName(codeId.toUpperCase(), id)+"/"+str;
				id=id.substring(0,id.length()-1);
			}
			return str.substring(0, str.length()-1);
		}
	
	/***
	 * 生成pdf 文件
	 * */
	public boolean createPdfFile(ArrayList<HashMap<String, String>> personMapLis,String otherMaterial,String dirPath) throws Exception{
		try {
			
			HashMap<String, String> othFileMap=new HashMap<String, String>();
			if(StringUtils.isNotEmpty(otherMaterial)) {
				String[] othPri=otherMaterial.split(",");
				for (int i = 0; i < othPri.length; i++) {
					if(othPri[i].indexOf(":")<0) {
						throw new Exception("导出其他材料配置错误，请检查配置！");
					}
					if(othPri[i].split(":")[1].indexOf("`")>-1){
						String[] tabidArr=othPri[i].split(":")[1].split("`");
						String str="";
						for (int j = 0; j < tabidArr.length; j++) {
							str+=","+(Integer.parseInt(tabidArr[j])-1);//页签自动减一
						}
						othFileMap.put(othPri[i].split(":")[0], str.substring(1));//(tabid:页签)
					}else{
						othFileMap.put(othPri[i].split(":")[0], (Integer.parseInt(othPri[i].split(":")[1])-1)+"");//(tabid:页签)
					}
				}
			}
			
			HashMap<String,String> pageNoMap=getNoPritPageNo(othFileMap);
			
			for (int i = 0; i < personMapLis.size(); i++) {
				HashMap<String, String> map=personMapLis.get(i);
				String tabid=map.get("tabid");
				
				
				String identFi=map.get("uniqueIdenfi");//人员唯一标识 内容
				String filePath=map.get("filePath");//文件存放路径 多文件之间以逗号间隔
				if(StringUtils.isNotEmpty(filePath)){
					String[] filePathArr=filePath.split(",");
					for (int j = 0; j < filePathArr.length; j++) {
						File   oldfile=new File(filePathArr[j].split("`")[1]);
						String fileName=identFi+"_D"+(filePathArr[j].split("`")[0])+"_"+filePathArr[j].split("`")[2];
						if(oldfile.exists()){
							File newFile=new File(dirPath+System.getProperty("file.separator")+fileName);
							if(newFile.exists()){
								newFile.delete();
							}
							copyFile(oldfile, newFile);
							//FileUtils.copyFile(oldfile, newFile);
						}
						
						
					}
				}
				
				if(StringUtils.isNotEmpty(otherMaterial)) {
					ArrayList<String> objlist=new ArrayList<String>();
					ArrayList<String> inslist=new ArrayList<String>();
					String task_id=map.get("task_id");
					String ins_id=map.get("ins_id");
					inslist.add(ins_id);
					objlist.add(map.get("basepre")+map.get("A0100"));
					OutWordBo owbo = new OutWordBo(this.getFrameconn(),this.userView,Integer.parseInt(tabid),task_id);
					
				
					owbo.setNoshow_pageno(pageNoMap.get(tabid));//去除模板设置设置某页是否打印 
					owbo.setOuttype("0");
					owbo.setShow_pageno(othFileMap.get(tabid));
					String othFileName=owbo.outword(objlist,1,inslist);
					File oldOthFile=new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+othFileName);
					File newOthFile=new File(dirPath+System.getProperty("file.separator")+identFi+"_C1_业绩一览表.pdf");
					if(newOthFile.exists()){
						newOthFile.delete();
					}
					oldOthFile.renameTo(newOthFile);
				}
				
				
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
		
	}
	//复制文件
	public static void copyFile(File oldFile,File newFile)throws Exception {
		FileChannel in=null;
		FileChannel out=null;
		FileInputStream inStream=null;
		FileOutputStream outStream=null;
		try {
			inStream=new FileInputStream(oldFile);
			outStream=new FileOutputStream(newFile);
			in=inStream.getChannel();
			out=outStream.getChannel();
			ByteBuffer buffer=ByteBuffer.allocate(1024*10);
			while(true) {
				buffer.clear();
				int leng=in.read(buffer);
				if(leng==-1) {
					break;
				}
				buffer.flip();
				out.write(buffer);
			}
		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeIoResource(inStream);
			PubFunc.closeIoResource(outStream);
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(out);
		}
	}
	
	// 设置某页打印 返回其他不打印页的页码
	public HashMap<String,String> getNoPritPageNo(HashMap<String, String> othFileMap){
		HashMap<String,String> pageMap=new HashMap<String, String>();
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		try {
			ArrayList list=null;
			for (String tabid:othFileMap.keySet()) {
				String pageno="";
				rs=null;
				list=new ArrayList();
				list.add(tabid);
				rs=dao.search("select * from Template_Page where tabid=?",list);
				String[] pritArry=othFileMap.get(tabid).split(",");
				ArrayList printlist=new ArrayList();
				for (int i = 0; i < pritArry.length; i++) {
					printlist.add(pritArry[i]);
				}
				while(rs.next()) {
					if(printlist.contains(rs.getString("pageid"))) {//应打印的不统计
						continue;
					}
					pageno+=","+rs.getString("pageid");
				}
				if(StringUtils.isNotEmpty(pageno))
					pageno=pageno.substring(1);
				pageMap.put(tabid, pageno);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return pageMap;
	}
	
	/***
	 * 文件夹压缩
	 * */
	public String createZipFile(String sourceFilePath)throws Exception{
		String tmpFileName=this.userView.getUserName()+"_templet.zip";
        File sourceFile = new File(sourceFilePath);  
        java.io.FileInputStream fis = null;  
        java.io.BufferedInputStream bis = null;  
        FileOutputStream fos = null;  
        java.util.zip.ZipOutputStream zos = null;  
          
        try {  //System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+userview.getUserName()+"_tempCard"
	        if(sourceFile.exists() == false){  
	            throw GeneralExceptionHandler.Handle(new Exception("压缩文件夹不存在！"));
	        }else{  
                File zipFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator") + tmpFileName);  
                if(zipFile.exists()){  
                    zipFile.delete();
                }
                
                File[] sourceFiles = sourceFile.listFiles();  
                if(null == sourceFiles || sourceFiles.length<1){ 
                	throw GeneralExceptionHandler.Handle(new Exception("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩."));
                }else{  
                    fos = new FileOutputStream(zipFile);  
                    zos = new java.util.zip.ZipOutputStream(new java.io.BufferedOutputStream(fos));  
                    byte[] bufs = new byte[1024*10];  
                    for(int i=0;i<sourceFiles.length;i++){  
                        //创建ZIP实体，并添加进压缩包  
                        java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(sourceFiles[i].getName());  
                        zos.putNextEntry(zipEntry);  
                        //读取待压缩的文件并写进压缩包里  
                        fis = new java.io.FileInputStream(sourceFiles[i]);  
                        bis = new java.io.BufferedInputStream(fis, 1024*10);  
                        int read = 0;  
                        while((read=bis.read(bufs, 0, 1024*10)) != -1){  
                            zos.write(bufs,0,read);  
                        }
                        fis.close();
                        bis.close();
                    }
                    zos.closeEntry();
                }  
            }  
        }catch (java.io.IOException e) {  
            e.printStackTrace();  
            throw GeneralExceptionHandler.Handle(e);
        } finally{  
            //关闭流  
            	PubFunc.closeIoResource(bis);
            	PubFunc.closeIoResource(zos);
                PubFunc.closeIoResource(fis);
                PubFunc.closeIoResource(fos);
          
        } 
		return tmpFileName;
	}
	
	
	/**
	 * 删除子文件夹及文件夹内文件
	 * **/
	public void deleteDirOrFile(String path){
		File file=new File(path);
		if(file.exists()){
			if(file.isDirectory()){
				File[] listFile=file.listFiles();
				for (int i = 0; i < listFile.length; i++) {
					String childpath=listFile[i].getAbsolutePath();
					if(listFile[i].isFile()){
						File childFile=new File(childpath);
						childFile.delete();
					}
				}
				file.delete();
			}else{
				file.delete();
			}
		}
	}
	
	/***
	 * 
	 * @param personMapLis
	 * @param colArry
	 * @param type  subSet 导出子集内容详细信息     template  导出模板内容
	 * 生成excle文件
	 * @return
	 */
	public String excelFile(ArrayList<HashMap<String, String>> personMapLis,HashMap<Object,Object> subMap,String[] colArry,String b0110,String type) throws Exception{
		HSSFWorkbook wb=new HSSFWorkbook();
		FileOutputStream fileOut=null;
		HSSFSheet sheet=wb.createSheet("说明");
		HSSFFont font=wb.createFont();//宋体 16
		font.setFontHeightInPoints((short)16);
		font.setFontName("宋体");
		HSSFRow firRow=sheet.createRow(5);
		HSSFCellStyle firStyle=wb.createCellStyle();
		firStyle.setAlignment(HorizontalAlignment.LEFT);
		firStyle.setVerticalAlignment(VerticalAlignment.CENTER);//水平居左 垂直居中
		firStyle.setWrapText(true);
		firStyle.setFont(font);
		HSSFCell firCell=firRow.createCell(1);
		firCell.setCellValue("说明：");
		firCell.setCellStyle(firStyle);
		sheet.setColumnWidth(1,35000);
		
		HSSFRow secRow=sheet.createRow(6);
		HSSFCellStyle secStyle=wb.createCellStyle();
		secStyle.setAlignment(HorizontalAlignment.LEFT);
		secStyle.setVerticalAlignment(VerticalAlignment.TOP);//
		secStyle.setWrapText(true);
		secStyle.setFont(font);
		secRow.setHeight((short)5000);
		HSSFCell secCell=secRow.createCell(1);
		if(!"subSet".equals(type)) {
			secCell.setCellValue(new HSSFRichTextString(" 1:表格中除了部门、手机号、审核人外都是必填项。\n 2:审核人可以不填写、填写一个或两个,只有一个时请填在第一申报人位置 \n 3:一级学科，专业技术职务请选择第二级节点"));
		}else {
			secCell.setCellValue(new HSSFRichTextString(" 1:表格中工号，序号和论文或专著名称为必填项。"
													+ "\n 2:工号与申报人一一对应，同一项目工号不可重复。序号为该申报人的第几份代表作。"
													+ "\n 3:要上传详细介绍必须保证该代表作已经上传或者与excel一起上传，不然无法导入该excel "
													+ "\n 4:出版时间为日期格式：xxxx年x月x日"));
		}
		secCell.setCellStyle(secStyle);
		
		
		HSSFFont font1=wb.createFont();//微软雅黑16
		font1.setFontHeightInPoints((short)16);
		font1.setFontName("微软雅黑");
		font1.setBold(true);
		
		HSSFFont font2=wb.createFont();
		font2.setFontHeightInPoints((short)11);
		font2.setFontName("宋体");
		
		//第二页
		HSSFSheet secSheet=null;
		if(!"subSet".equals(type)) {
			secSheet=wb.createSheet("申报人信息");
		}else {
			secSheet=wb.createSheet("详细介绍");
		}
		
		    HSSFCellStyle cellstyle = wb.createCellStyle();
	        cellstyle.setAlignment(HorizontalAlignment.CENTER);//表头居中
	        cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        cellstyle.setBorderBottom(BorderStyle.THIN);
	        cellstyle.setBorderTop(BorderStyle.THIN);
	        cellstyle.setBorderLeft(BorderStyle.THIN);
	        cellstyle.setBorderRight(BorderStyle.THIN);
	        cellstyle.setFont(font);
	        
	        HSSFCellStyle colStyle = wb.createCellStyle();
	        colStyle.setAlignment(HorizontalAlignment.CENTER);//表头居中
	        colStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        colStyle.setBorderBottom(BorderStyle.THIN);
	        colStyle.setBorderTop(BorderStyle.THIN);
	        colStyle.setBorderLeft(BorderStyle.THIN);
	        colStyle.setBorderRight(BorderStyle.THIN);
	        colStyle.setFont(font1);
	        
	        HSSFCellStyle contentStyle = wb.createCellStyle();
	        contentStyle.setAlignment(HorizontalAlignment.CENTER);//表头居中
	        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        contentStyle.setBorderBottom(BorderStyle.THIN);
	        contentStyle.setBorderTop(BorderStyle.THIN);
	        contentStyle.setBorderLeft(BorderStyle.THIN);
	        contentStyle.setBorderRight(BorderStyle.THIN);
	        contentStyle.setWrapText(true);
	        contentStyle.setFont(font2);
	        
	        
	        try {
		       
		       if(!"subSet".equals(type)) {
		    	   
					String[] copyCol=colArry.clone();
					/**
					 * 按照第一列排序
					 * **/
					if(copyCol.length>1)
						for (int i = 0; i < copyCol.length-1; i++) {
							for (int j = 0; j < copyCol.length-i-1; j++) {
								String temp="";
								if(Integer.parseInt(copyCol[j].split("`")[6])>Integer.parseInt(copyCol[j+1].split("`")[6])) {
									temp=copyCol[j+1];
									copyCol[j+1]=copyCol[j];
									copyCol[j]=temp;
								}
							}
							
						}

		    	   
		    	   //写列头
		        	HSSFRow row=secSheet.createRow(1);
		        	row.setHeight((short)800);
		        	HSSFCell cell=null;
		        	int index=0;
			        for (int i = 0; i < copyCol.length; i++) {
			        	if("1".equals(copyCol[i].split("`")[5])){
			        		index++;
			        		continue;
			        	}
						cell=row.createCell(i-index);
						cell.setCellValue(copyCol[i].split("`")[2].replace("[现]", "").replace("[拟]", ""));
						cell.setCellStyle(colStyle);
						secSheet.setColumnWidth(i-index, 8000);
					}
			        //写数据
			        index=0;
			        for (int i = 0; i < personMapLis.size(); i++) {
			        	
						HashMap<String, String> personMap=personMapLis.get(i);
						//创建行
						row=secSheet.createRow(i+2);
						row.setHeight((short)700);
						for (int j = 0; j < copyCol.length; j++) {// fieldSet fieldItem  fieldDesc itemType
							if("1".equals(copyCol[j].split("`")[5])){
								index++;
				        		continue;
				        	}
							if(j-index<0)
								cell=row.createCell(0);
							else	
								cell=row.createCell(j-index);
							String value=personMap.get(copyCol[j].split("`")[1]);
							cell.setCellStyle(contentStyle);
							cell.setCellValue((StringUtils.isEmpty(value)?"":value));//fieldItem
							
						}
						index=0;
					}
			        index=0;
			       for (int i = 0; i < copyCol.length; i++) {
			    	   if("1".equals(copyCol[i].split("`")[5])){
							index++;
			        	}
			       } 
		    	   
		    	   //生成第一行合并行：
		    	   HSSFRow titRow=secSheet.createRow(0);
		    	   titRow.setHeight((short)1000);
		    	   HSSFCell titCell=titRow.createCell(0);
		    	   titCell.setCellStyle(cellstyle);
		    	   titCell.setCellValue("申报人审核人信息导入");
		    	   if(((colArry.length-1)-index) > 0) {
		    		   CellRangeAddress cra=new CellRangeAddress(0,0,0,(colArry.length-1)-index);
		    		   secSheet.addMergedRegion(cra);
		    		   RegionUtil.setBorderBottom(1, cra, secSheet);
		    		   RegionUtil.setBorderLeft(1, cra, secSheet);
		    		   RegionUtil.setBorderRight(1, cra, secSheet);
		    		   RegionUtil.setBorderTop(1, cra, secSheet);
		    	   }
		       }else {
		    	   //写入标题
		    	   HSSFRow row=secSheet.createRow(0);
		    	   row.setHeight((short)800);
		    	   HSSFCell firstCell=row.createCell(0);//工号
		    	   secSheet.setColumnWidth(0, 8000);
		    	   secSheet.setColumnWidth(1, 8000);
		    	   HSSFCell secondCell=row.createCell(1);//序号
		    	   firstCell.setCellStyle(colStyle);
		    	   firstCell.setCellValue("工号");
		    	   secondCell.setCellStyle(colStyle);
		    	   secondCell.setCellValue("序号");
		    	   ArrayList list=(ArrayList)subMap.get("colArry");
		    	   String[] subColArry=new String[list.size()];
		    	   HSSFCell cell=null;
		    	   for(int i=0;i<list.size();i++) {
		    		  cell=row.createCell(i+2);
		    		  cell.setCellStyle(colStyle); 
		    		  cell.setCellValue(list.get(i).toString().split("`")[1]);
		    		  secSheet.setColumnWidth(i+2, 8000);
		    		  subColArry[i]=list.get(i).toString().split("`")[0];
		    	   }
		    	   subMap.remove("colArry");
		    	   Iterator itor=subMap.entrySet().iterator();
		    	   int rowIndex=0;
		    	   while(itor.hasNext()) {
		    		   Map.Entry entry = (Map.Entry) itor.next();
		    		   String uniqueIdenfi = entry.getKey().toString();//工号
		    		   ArrayList<HashMap<String,String>> subMapList = (ArrayList<HashMap<String,String>>)entry.getValue();
		    		   for(int i=0;i<subMapList.size();i++) {
		    			   HSSFRow contentRow=secSheet.createRow(rowIndex+1);//内容行
		    			   contentRow.setHeight((short)800);
		    			   HSSFCell idenFiCell=contentRow.createCell(0);
		    			   idenFiCell.setCellStyle(contentStyle);
		    			   idenFiCell.setCellValue(uniqueIdenfi);
		    			   HashMap<String,String> map=subMapList.get(i);
		    			   if(map.containsKey("index")) {
		    				   HSSFCell indexCell=contentRow.createCell(1);
		    				   indexCell.setCellStyle(contentStyle);
		    				   indexCell.setCellValue( map.get("index"));//序号
		    				   map.remove("index");
		    			   }
		    			   HSSFCell contentCell=null;
		    			   for(int j=0;j<subColArry.length;j++) {
		    				   contentCell=contentRow.createCell(j+2);
		    				   contentCell.setCellStyle(contentStyle);
		    				  
		    				   String codesetId=DataDictionary.getFieldItem(subColArry[j]).getCodesetid();
		    				   if(!"0".equals(codesetId)) {
		    					   contentCell.setCellValue(AdminCode.getCodeName(codesetId, map.get(subColArry[j])));
		    				   }else
		    					   contentCell.setCellValue(map.get(subColArry[j]));
		    			   }
		    			   rowIndex++;
		    		   }
		    		  
		    	   }
		    	   
		    	   
		    	   
		       }
		       
		       
		       String fileName="同行评议花名册.xls";
		       if(StringUtils.isNotBlank(b0110)) {
		    	  String str=getCodeName(b0110,"UN");
		    	  if(StringUtils.isNotEmpty(str)) {
		    		  fileName=str.split("/")[0]+"同行评议花名册.xls";
		    	  }
		    	  if("subSet".equals(type)) {//导出子集
			    	   fileName=str.split("/")[0]+"代表作摘要.xls";
			       }
		       }
		       if("subSet".equals(type)) {
		    	   fileName="代表作摘要.xls"; 
		       }
		       File oldFile =new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ 
						this.userView.getUserName()+"_templet"+System.getProperty("file.separator")+fileName);
		       if(oldFile.exists()) {
		    	   oldFile.delete();
		       }
		       
		        fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ 
		        								this.userView.getUserName()+"_templet"+System.getProperty("file.separator")+fileName);
		        wb.write(fileOut);
		        wb.close();
		        fileOut.flush();
			} catch (Exception e) {
				throw e;
			}finally{
				PubFunc.closeResource(fileOut);
				PubFunc.closeResource(wb);
			}
	       
	        
		return "";
	}
	
	public ArrayList getTemplateSet(String tabid) throws GeneralException{
		String sql="select * from template_set where TabId=? order by pageid ,RTop,RLeft";
		ContentDAO dao=new ContentDAO(this.frameconn);
		ArrayList<String> list=new ArrayList<String>();
		ArrayList itemList=new ArrayList();
		list.add(tabid);
		RowSet rs=null;
		try {
			rs=dao.search(sql,list);
			
			org.jdom.Document doc=null;
			int index=0;
			while(rs.next()){//附件类型 flag = F 附件类型不显示  flag=A 指标类型    子集附件时加（子集） 模板指标 只需要模板单元格内容 图片 和插入子集除外  附件子集
				HashMap<String, String> map=new HashMap<String, String>();
				String flag=rs.getString("flag");
				boolean fileFlag=false;
				if("A".equals(flag)){
					//指标标题 hz 是否与field_hz 相是否想同 相同加现或拟  不同则取hz内容
					String fieldName=rs.getString("field_name");
					
					boolean nameFlag=true;
					String hz=rs.getString("hz").replaceAll(" ", "").replace("`","");
					String field_hz=rs.getString("field_hz").replace(" ", "");
					if(!hz.equals(field_hz)){
						nameFlag=false;
					}
					/*if(StringUtils.isEmpty(hz))
						hz=rs.getString("hz").replaceAll("`", "").replaceAll(" ", "");
					*/
					String subFlag=rs.getString("subflag");
					String chgState=rs.getString("chgState");//1 变化前 现 2 变化后  拟
					//if(StringUtils.isNotEmpty(fieldName)){//
						map.put("index", (++index)+"");
						map.put("itemSet",rs.getString("setName"));
						map.put("subflag", subFlag);
						map.put("codeid",rs.getString("CodeId"));
						if("0".equals(subFlag)){//单元格插入内容
							map.put("itemType",(StringUtils.isEmpty(rs.getString("Field_type"))?"":rs.getString("Field_type")));
							if("1".equals(chgState)){
								map.put("itemid", fieldName+"_1");
								map.put("itemdesc",(nameFlag?field_hz+"[现]":hz) );
							}else{
								map.put("itemid", fieldName+"_2");
								map.put("itemdesc",(nameFlag?field_hz+"[拟]":hz));
							}
						}else{//子集 判断插入的是子集附件
							//解析插入子集 subdomain  fields包含attach 标识插入附件
							String subdoMain=rs.getString("sub_domain");
							doc=PubFunc.generateDom(subdoMain);
							String xpath="/sub_para/para";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点  xpath="/sub_para/para";
							List childlist=findPath.selectNodes(doc);	
							if(childlist!=null&&childlist.size()>0){
								Element element=(Element)childlist.get(0);
								String fields=element.getAttributeValue("fields");
								if(StringUtils.isNotEmpty(fields)&&fields.indexOf("attach")>-1){
									fileFlag=true;
									map.put("itemType","A");
									if("1".equals(chgState)){
										map.put("itemid", "t_"+rs.getString("setName")+"_1");
										map.put("itemdesc", hz.replaceAll("\\{", "").replaceAll("\\}", "")+"(子集附件)");
									}else{
										map.put("itemid", "t_"+rs.getString("setName")+"_2");
										map.put("itemdesc", hz.replaceAll("\\{", "").replaceAll("\\}", "")+"(子集附件)");
									}
								}
							}
						}
						if("0".equals(subFlag)||fileFlag)
							itemList.add(map);
					//}
					
					
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return itemList;
	}
	
}
