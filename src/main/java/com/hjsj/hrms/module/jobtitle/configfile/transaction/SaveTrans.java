package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：ConfigTrans 
 * 类描述：配置页面配置窗口
 * 创建人：gaohy
 * 创建时间：Gao 8, 2015 10:20:54 AM 
 * @version
 */
public class SaveTrans extends IBusiness {


	@Override
    public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		
		
		JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(), this.getUserView());
		String flagmap=(String)this.getFormHM().get("flagmap");//行为标识，判断是存储数据，还是提取数据
		
		// 取消“二级单位评议组”时校验：存在启用“二级单位评议组”的评审会议时不能取消。
		if("college_eval".equals(flagmap) && !(Boolean)this.getFormHM().get("college_eval")){
			Boolean isContinue = (Boolean)this.getFormHM().get("isContinue");
			if(!isContinue && jobtitleConfigBo.isExstOpenCollege()){
				this.getFormHM().put("msg", "存在启用"+JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT+"的评审会议，<br />如果取消，将会隐藏该阶段相关数据！<br />"
						+ "是否继续？");
				return ;
			}
		}

		String flagid=(String)this.getFormHM().get("flagid");//模版类标识
		Object obj= (Object) this.getFormHM().get("value");//存储数据时，要存储的模版值
		String strValue=obj.toString().replaceAll("^.*\\[", "").replaceAll("].*", "");
		String[] value_a=strValue.split(",");//得到一个数组
		String upxml="";//解析xml后返回的值
		List list=new ArrayList();
		Map map=new HashMap();
		for(int i=0;i<value_a.length;i++){//去数组元素前半部分，并去掉空格
			String[] value_b=value_a[i].split("\\.");
			value_a[i]=value_b[0].trim();
		}
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		try {
			String mid=value_a[0];
				for(int e=1;e<value_a.length;e++){
					mid=mid+","+value_a[e];
				}
			String	xmlDocMid="<template type="+flagid+"template_id="+mid+"/>";//拼写数据库中xml的子元素值

			//常量表：用于系统所用的控制参数
			RowSet rs = dao.search("select Str_Value from constant where Constant=?",Arrays.asList(new String[]{"JOBTITLE_CONFIG"}));
			String xmlDoc="";
			if(rs.next()){
				xmlDoc=rs.getString("Str_Value");
			}
			else {
				String sql="insert into Constant(Constant,Type,Describe,str_value) values('JOBTITLE_CONFIG','A','职称评审配置参数','')";
				dao.update(sql);
			}
			if(xmlDoc==null||xmlDoc.length()<1){
				xmlDoc="<?xml version=\"1.0\" encoding=\"GB2312\"?>  <params><templates></templates><college_eval></college_eval><support_checking></support_checking><support_word></support_word><isshowrefresh></isshowrefresh><vote_type type=\"1\" columns=\"\"/></params>";
			}
//			else{
//				String sql="insert into constant";
//			}
			//解析xml
			Boolean college_eval = (Boolean)this.getFormHM().get("college_eval");
			Boolean support_checking = (Boolean)this.getFormHM().get("support_checking");
			Boolean support_word = (Boolean)this.getFormHM().get("support_word");
			Boolean isshowrefresh = (Boolean)this.getFormHM().get("isshowrefresh");
			String vote_type = (String)this.getFormHM().get("vote_type");
			Boolean show_Validatecode = (Boolean)this.getFormHM().get("show_Validatecode");
			HashMap voteMap = null;
			if(vote_type!=null&&!"null".equals(vote_type)){
				voteMap = new HashMap();
				String[] votetypes=vote_type.split("/");
				String columns="";
				if(votetypes.length>1)
					columns=votetypes[1];
				voteMap.put("type", votetypes[0]);
				voteMap.put("columns", columns);
			}
			
			DomXml domxml = new DomXml();
			domxml.setSupport_checking(support_checking);
			domxml.setCollege_eval(college_eval);
			domxml.setSupport_word(support_word);
			domxml.setIsshowrefresh(isshowrefresh);
			domxml.setVote_type(voteMap);
			domxml.setShow_Validatecode(show_Validatecode);
			list = domxml.parse(xmlDoc,flagid,mid,xmlDocMid,flagmap);//解析xml，需要返回的值的list
			
			//存储xml
			if("savemap".equals(flagmap) 
					|| "college_eval".equals(flagmap) 
					||  "support_checking".equals(flagmap)
					||  "support_word".equals(flagmap)
					||"isshowrefresh".equals(flagmap)
					||"show_Validatecode".equals(flagmap)
					||"vote_type".equals(flagmap)){
				upxml=(String) list.get(0);//解析处理后的xml
				jobtitleConfigBo.updateXml(upxml);//存储
			}
			//查询
			else if("initmap".equals(flagmap)){//查询xml
				
				map=(Map) list.get(1);//解析后，取每个业务类对应的模版id
				String TabId="";//xml中的type
				String YeWuId="";//xml中的template_id
				
				Iterator i=map.entrySet().iterator();
				Map value_params=new HashMap();//对结果进行封装
			    List li=new ArrayList(); //对业务类id进行封装
				while(i.hasNext()){//只遍历一次,速度快
					Map.Entry em=(Map.Entry)i.next();
						YeWuId=(String) em.getKey();//业务类id
						TabId=(String) em.getValue();//条件，模版Id,转换成String型
						String Pnvalue="";//按模版id查询后拼接的字符串
						//多选情况
						if("3".equals(YeWuId)|| "6".equals(YeWuId)){
							String []TabId_a=TabId.split(",");//获得模版id数组
							
								for(int n=0;n<TabId_a.length;n++){
									rs = dao.search("select TabId,Name from template_table  where TabId=?",Arrays.asList(new String[]{TabId_a[n]}));
							 
									while(rs.next()){
										Pnvalue=Pnvalue+TabId_a[n]+"."+rs.getString("Name")+",";//返回值
									}
								}
								li.add(YeWuId);
								if(Pnvalue.indexOf(",")>0){//去掉多余的符号,并对结果进行封装
									value_params.put(YeWuId, Pnvalue.substring(0,Pnvalue.lastIndexOf(",")));
								}else{
									value_params.put(YeWuId, Pnvalue);
								}
						}
						//单选情况
						else{
								rs = dao.search("select TabId,Name from template_table  where TabId=?",Arrays.asList(new String[]{TabId}));
								while(rs.next()){
									Pnvalue=TabId+"."+rs.getString("Name");//返回值
								}
								li.add(YeWuId);
								if(Pnvalue.indexOf(",")>0){//去掉多余的符号,并对结果进行封装
									value_params.put(YeWuId, Pnvalue.substring(0,Pnvalue.lastIndexOf(",")));
								}else{
									value_params.put(YeWuId, Pnvalue);
								}
						}
						
					}
				
				// 参数回显
				value_params.put("college_eval", jobtitleConfigBo.getParamConfig("college_eval"));//是否支持学院聘任组
				value_params.put("support_checking", jobtitleConfigBo.getParamConfig("support_checking"));//是否支持审核
				value_params.put("support_word", jobtitleConfigBo.getParamConfig("support_word"));//公示、投票环节显示申报材料表单上传的word模板内容
				value_params.put("isshowrefresh", jobtitleConfigBo.getParamConfig("isshowrefresh"));//是否分批次投票免重复登陆
				//xus 18/4/3 
				HashMap votemap=jobtitleConfigBo.getVoteConfig();
				value_params.put("votetype", votemap.get("voteType"));
				value_params.put("votecolumns", votemap.get("voteColumns"));
				
				// 测评表
				value_params.put("per_templates", jobtitleConfigBo.getJobtitleParamConfig("per_templates"));//
				value_params.put("per_templates_text", jobtitleConfigBo.getSelectidTextSrc(jobtitleConfigBo.getJobtitleParamConfig("per_templates")));//
				value_params.put("show_Validatecode", jobtitleConfigBo.getJobtitleParamConfig("show_Validatecode"));//
				
				//返回值
				this.getFormHM().put("li", li);
				this.getFormHM().put("value_params", value_params);
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
