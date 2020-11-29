package com.hjsj.hrms.module.system.qrcard.mobliewriter;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.transaction.mobileapp.template.MobileTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 二维码：填写入职表单后，报批、保存表单的交易类，参考手机端workflowtrans类
 * 原名字叫workflowtrans，为避免同手机端重名，
 * 2019年3月25日重命名，及更改目录，删除掉mobile_wfmapping中的原交易类号，在sys_wfmapping中重新定义
 * @author wangz
 * 2018年6月 
 */
public class SaveQRCardTemplateTrans extends IBusiness {

	private enum TransType{
		/**主页*/
		main
		/**业务申请*/
		,apply
		/**保存模板数据*/
		,save
		/**办理*/
		,deal
		/**审批对象有多个时显示人员照片信息*/
		,showphoto
		/**已批任务*/
		,approved
		/**待办任务**/
		,tasklist
		/**我的申请列表**/
		,ApplyList
	}
	@Override
	public void execute() throws GeneralException {

		String message = "";
		String succeed = "false";
		HashMap hm = this.getFormHM();
		try {
			String transType = (String) hm.get("transType");
			hm.remove("msg");
			hm.remove("flag");
			UserView userView = this.getUserView();
			MobileTemplateBo bo = new MobileTemplateBo(this.frameconn,this.userView);
			bo.setIsWeiX(false);//给MobileTemplateBo传递参数，说明是通过二维码
			// 不同业务流程分支点
			if (transType != null) {
				
				
				if(transType.equals(TransType.apply.toString())){
					String param = (String)hm.get("param");
					
					/**
					   { 
						  "tabid":"1"             //模板ID
						 ,"isEdit":"1"            // 1：数据可编辑  0：只读（浏览已批的任务）
						 ,"taskid":"20"           // 待办任务号（发起任务|通知单 为空值）
						 ."ins_id":"102"          //实例ID（发起任务|通知单 为空值）
						 ,"fromMessage":"1"     //1：来自通知单待办  0：不是
						 ,"object_id":"Usr00000001" //模板数据ID，人员：库前缀+A0100
						                                   单位|岗位：B0100|E01A1
						,”info_type”:”mobile”   // mobile:手机适配页   normal:电脑端页面
						,”page_no”:”1” //页签
						} 
                    */ 
					param = SafeCode.decode(param); 
					String jsonstr = bo.getTemplateInfo(param);  
					hm.put("result", jsonstr);
					succeed="true";
				}
				else if(transType.equals(TransType.save.toString())){
					String param =(String) hm.get("param");
					param= SafeCode.decode(param);
					String returnvalue ="";
					int saveTypeFlag=(Integer)hm.get("saveTypeFlag");//保存类型 0 一次把所有页数据全部保存，1 一次一页保存
					if(saveTypeFlag==0) {
						MorphDynaBean bean=(MorphDynaBean)hm.get("pageParam");
						Map<String,String> map=PubFunc.DynaBean2Map(bean);
						for(String pageno:map.keySet()) {
							returnvalue= bo.saveTemplateInfo(map.get(pageno));
						}
					}else {
						returnvalue= bo.saveTemplateInfo(param);
					}
					HashMap obj = (HashMap)JSON.parse(returnvalue);
					String info =(String) obj.get("info");
					if("success".equals(info)){
						succeed="true";
						hm.put("msg", returnvalue);
					}else{
						hm.put("msg", info);
					}
				}
				else if(transType.equals(TransType.deal.toString())){
					String param =(String) hm.get("param");
					param= SafeCode.decode(param);
				    String returnvalue ="";
					returnvalue= bo.dealTask(param);
					if("success".equals(returnvalue)){
							succeed="true";
					}else{
							hm.put("msg", returnvalue);
					}
				
			    }
				else if(transType.equals(TransType.showphoto.toString())){ 
					String objs =(String) hm.get("param");
					String[] ids = objs.split(",");
					HashMap a0100Map = new HashMap();
					HashMap a0101Map = new HashMap();
					String dbname = "";
					String a0100 = "";	
					for(int i=0;i<ids.length;i++){
						String id = ids[i];
						id = id.trim();
						if(id.length()!=11)
							continue;
						dbname = id.substring(0,3);
						a0100 = id.substring(3);
						StringBuffer a0100str  =null;
						a0100str = (StringBuffer)a0100Map.get(dbname);
						if(a0100str==null){
							a0100str = new StringBuffer();
							a0100str.append("'"+a0100+"'");
							a0100Map.put(dbname, a0100str);
						}else{
							a0100str.append(",'"+a0100+"'");
						}
					}
					ContentDAO dao = new ContentDAO(this.frameconn);
					for(Iterator i =a0100Map.keySet().iterator();i.hasNext();){
						String key = (String)i.next();
						this.frowset = dao.search("select a0101,a0100  from "+key+"A01 where a0100 in ("+a0100Map.get(key)+")");
						while(this.frowset.next()){
							a0101Map.put(key+this.frowset.getString("a0100")
									, this.frowset.getString("a0101"));
						}
					}
					StringBuffer jsonstr = new StringBuffer("[");
					for(int i=0;i<ids.length;i++){
						String id = ids[i];
						id = id.trim();
						if(id.length()!=11)
							continue;
						dbname = id.substring(0,3);
						a0100 = id.substring(3);
						String filename = ""; 
						StringBuffer photourl = new StringBuffer();
						if (a0100 == "") {
							filename = "";
						} else {
						filename = ServletUtilities.createOleFile(dbname+"A00", a0100, this.frameconn);
						}
						if (!"".equals(filename)) {
							photourl.append("servlet/DisplayOleContent?filename=");
							photourl.append(SafeCode.encode(PubFunc.encrypt(filename))).append("&bencrypt=true");
						} else {
							photourl.append("images/photo.jpg");
						}
						
						if(i!=0)
							jsonstr.append(",");
						jsonstr.append("{\"name\":\""+a0101Map.get(id)+"\",\"id\":\""+id+"\",\"photo\":\""+photourl+"\"}");
					}
					jsonstr.append("]");
					hm.put("result", jsonstr.toString());
					succeed="true";
				
				}
				else if(transType.equals(TransType.tasklist.toString())){//待办任务
					
					String param =(String) hm.get("param");
					param= SafeCode.decode(param);
					String returnvalue = bo.getPendingTask(param.toString());
					String result = transferTime(returnvalue,"recieveTime"); 
					hm.put("result", result);
					succeed="true";
				}
				else if(transType.equals(TransType.approved.toString())){ 
						String param =(String) hm.get("param");
						param= SafeCode.decode(param); 
						String jsonstring = bo.getYpTask(param.toString());
						String result = transferTime(jsonstring,"applyTime");
						//System.out.println(result);
						hm.put("result",result);
						succeed="true";
				}
				else if(transType.equals(TransType.ApplyList.toString())){
					String param =(String) hm.get("param");
					param= SafeCode.decode(param); 
					String myApply = bo.getMyApplied(param.toString());
					String result = transferTime(myApply,"applyTime");
					hm.put("result",result);
					succeed="true";
				} 
				else if(transType.equals(TransType.main.toString())){
					StringBuffer html = new StringBuffer();
					String jsonstr = bo.getPendingTaskNumber("{\"flag\":\"1\",\"type\":\"1\"}");
					JSONObject obj = JSONObject.fromObject(jsonstr);
					int num =Integer.parseInt( obj.getString("task_number"));
					html.append("<html><head></head><body>");
					html.append("<div id=\"bh-wzm-index-all\"><div class=\"bj-wzm-all\">");
					html.append("<div class=\"hj-all-one\"><div class=\"hj-all-one-left\"><dl><a href=\"#\" onclick=\"Ext.getCmp('myButton').fireEvent('tap','myTask','我的任务');\"><span style=\"position:relative;\"><img src=\"../../images/rwu.png\" />"+(num>0?"<i class=\"bage\">"+num+"</i>":"")+"</span><dd>我的任务</dd> </dl></a></div><div class=\"hj-all-one-center\"></div> <div class=\"hj-all-one-right\"><dl><a href=\"#\" onclick=\"Ext.getCmp('myButton').fireEvent('tap','myApply','我的申请');\"><dt><img src=\"../../images/shenqing.png\" /></dt><dd>我的申请</dd></a></dl></div></div>");         
					html.append("<div class=\"bh-clear\"></div>");
					html.append("<div class=\"hj-all-two\" style=\"overflow:auto;\"><table   width=\"100%\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\">");
					html.append("<tr>");
					int size = 5;
					jsonstr = bo.getApplyTemplate(1);//获取全部模板 1全部 2考勤业务
					if(jsonstr.length()==0){
						jsonstr="[]";
					}
					//jsonstr = "[{tabid:'xuj',tabname:'23'}]";
					//JSONObject obj = JSONObject.fromObject(jsonstr);
					JSONArray jArray=JSONArray.fromObject(jsonstr);
					size = jArray.size();
					if(size<6)
						size=6;
					for(int i=0;i<size;i++){
						if(i!=0&&i%3==0){
							html.append("</tr><tr>");
						}
						if(i<jArray.size()){
							JSONObject bean = (JSONObject)jArray.get(i);
							String tabid = (String)bean.get("tabid");
							String tabname = (String)bean.get("tabname");
							String newtabname = PubFunc.splitString(tabname, 20);
							tabname = (tabname.equals(newtabname))?tabname:newtabname+"<span>...</span>";
							String infor_type = (String)bean.get("infor_type");
							String busitype = (String)bean.get("busitype");
							String param = "{\"tabid\":\""+tabid+"\",\"isEdit\":\"1\",\"taskid\":\"\",\"ins_id\":\"\",\"fromMessage\":\"0\",\"object_id\":\""+userView.getDbname()+userView.getA0100()+"\"}";
							param = SafeCode.encode(param);
							html.append("<td ><dl><a href=\"#\" onclick=\"Ext.getCmp('myButton').fireEvent('tap','','"+param+"');\"><dt><img src=\"../../images/apply"+busitype+".png\" /></dt><dd  style=\"height:24px;\">"+tabname+"</dd></a></dl></td>");
						}else{
							html.append("<td ><dl><dt></dt><dd></dd></dl></td>");
						}
					}
					if(size!=6){
						//xus 17/10/26 32295 京投发展：微信业务办理界面显示不正常。
						int blanksize=size%3==0?0:3-(size%3);
						for(int i=0;i<blanksize;i++){
							html.append("<td ><dl>&nbsp;</dl></td>");
						}
					}
					html.append("</tr>");
					//html.append("<tr><td ></td><td ></td><td ><button onclick=\"showPicker()\">aaaaa</button></td></tr>");
					html.append("</table></div></div></div></div>");
					html.append("</body></html>");
					hm.put("html", html.toString());
					succeed="true";
				}  
			}else {
				message = ResourceFactory.getProperty("mobileapp.error");
				hm.put("msg", message);
			}
			 hm.put("transType", transType);
		} catch (Exception e) {
			succeed = "false";
			if(message.trim().length()==0){
			    message =e.getMessage();
			}
            hm.put("msg", message);
			e.printStackTrace();
		}finally{
			hm.put("flag", succeed);
		}
	}
	
	
	/**
	 * result 通过bo类获取的json字符串
	 * colum  json字符串中的时间名称
	 * */
	//判断时间是昨天前天区别显示
	public String transferTime(String result,String colum){
		if(result==null||"".equals(result)){
			return "";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			JSONArray arr = JSONArray.fromObject(result);
			for (Object object : arr) {
				JSONObject obj = (JSONObject) object;
				String applyTime = obj.getString(colum);
				if(applyTime!=null&&!"".equals(applyTime)){
					long time;
					try {
						time = PubFunc.getHourSpan(sdf.parse(applyTime), new Date());
						Calendar cal = Calendar.getInstance();
						int hour = cal.get(Calendar.HOUR_OF_DAY);
						time = time - hour;
						if(time <= 0){
							obj.put(colum, new SimpleDateFormat("HH:mm").format(sdf.parse(applyTime)));
						} else if(time > 0 && time <= 24){
							obj.put(colum, "昨天");
						} else if(time > 24 && time < 48){
							obj.put(colum, "前天");
						} else {
							obj.put(colum, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(sdf.parse(applyTime)));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return arr.toString();
		}
	}
}
