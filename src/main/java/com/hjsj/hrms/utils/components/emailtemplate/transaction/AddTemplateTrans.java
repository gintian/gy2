package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.HireTemplateBo;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * <p>Title:AddTemplateTrans</p>
 * <p>Description:新增模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:03:31 PM</p>
 * @author sunming
 * @version 1.0
 */
public class AddTemplateTrans extends IBusiness{
	public void execute() throws GeneralException {
		
			HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
	     try{ 
	    	 String subModule = (String) this.getFormHM().get("subModule");
	    	 String other_flag = (String) this.getFormHM().get("other_flag");
	    	 String name = (String) this.getFormHM().get("name");
	    	 String subject = (String) this.getFormHM().get("subject");
	    	 String returnAddress = (String) this.getFormHM().get("returnAddress");
	    	 String content = (String) this.getFormHM().get("content");
	    	 String ownflag=(String) this.getFormHM().get("ownflag");
	    	 ArrayList  fieldList= (ArrayList)this.getFormHM().get("email_array");
	    	 String opt = (String) this.getFormHM().get("opt");//判断是什么模块进入的，暂时9：绩效,没填为招聘
	    	 TemplateBo bo = new TemplateBo(this.frameconn, new ContentDAO(
	    			 this.frameconn), this.getUserView());
	    	 int template_id = 0; 
	    	 String temp_template_id =((String)this.getFormHM().get("tempalteId"));
	    	 int flag = 0;
	    	 if(temp_template_id != null&&temp_template_id.trim().length() !=0){
	    		 template_id = new Integer(temp_template_id).intValue();
	    	 }else{
	    		 template_id =bo.getTemplateId();
	    		 flag = 1;
	    	 }
	    	 String a0100=this.userView.getA0100();
	    	 String pre=this.userView.getDbname();
		     String unitB0110 ="";//所属机构
		     String temp=this.userView.getUnit_id();
		     if(temp!=null&&temp.trim().length()>3)
		     {
		    	 unitB0110=temp.substring(2);
		     }else
		     {
		    	 if(pre!=null&&pre.trim().length()>0)
		    		 unitB0110=this.getUserUnitId(a0100,pre);
		     }
	    	 HireTemplateBo hbo = new HireTemplateBo(this.getFrameconn());
	    	 String b0110=hbo.getB0110(this.userView,opt);
	    	 String b0110Email = "";
	    	 String templates = "1000,1001,1002,1003,1004,1005,1006,1007,";
	    	 if("HJSJ".equals(b0110)||templates.indexOf(template_id+"")!=-1){
	    		 b0110Email="HJSJ";
	    	 }else{
	    		 b0110Email = this.getB0110(b0110);
	    	 }
	    	 int nModule=7;
	    	 if(StringUtils.isNotBlank(opt) && !"7".equals(opt))
	    		 nModule = Integer.parseInt(opt.toString());
	    	 int nInfoclass = 1;
		     ContentDAO dao = new ContentDAO(this.getFrameconn());
		     RecordVo vo = new RecordVo("email_name");
		     vo.setInt("id",template_id);
		     vo.setString("name",PubFunc.keyWord_reback(name));
		     vo.setInt("nmodule",nModule);
		     vo.setInt("ninfoclass",nInfoclass);
		     vo.setString("subject",PubFunc.keyWord_reback(subject));
		     String emailId = ConstantParamter.getEmailField().toLowerCase();
		     vo.setString("address",emailId.toUpperCase() + ":电子信箱");
		     vo.setInt("sub_module",Integer.parseInt(PubFunc.keyWord_reback(subModule)));
		     vo.setString("return_address",PubFunc.keyWord_reback(returnAddress));
		     vo.setString("b0110",b0110Email);
		     vo.setString("ownflag",ownflag);
		     vo.setString("other_flag",PubFunc.keyWord_reback(other_flag));
		     
		     if(flag == 1){
		          dao.addValueObject(vo);
		          StringBuffer buf = new StringBuffer();
		          /**避免公式保存时ie下出现‘？’**/
		          //修改【39113】基于GIT封版：绩效管理，参数设置，通知模板，ie定义好后，有空格，回来后显示一个？，
		          //ext的htmleditor会出现未知字符换成char类型，出现?，但是这个问号和正常输入的问号不相等，先找所有正常显示的问号，，
		          //在通过字节码替换成gbk的这一步会使未知字符变成?，将这些问号替换掉就行，
                /*改成utf-8后已经没有【39113】问题
                 * ArrayList<Integer> listUnKnown = new ArrayList<Integer>(); for(int i = 0; i <
                 * content.length(); i++) { if(content.charAt(i) == '?') { listUnKnown.add(i); }
                 * } String encoding = PubFunc.getEncoding(content);
                 * if(StringUtils.isNotEmpty(encoding) && !"UTF-8".equals(encoding)) { String
                 * unicode = new String(content.getBytes(), encoding); content = new
                 * String(unicode.getBytes(encoding)); } content = content.replaceAll("<li>\\?",
                 * "<li>"); content = content.replaceAll("<br>", "\r\n"); StringBuffer
                 * contentBuffer = new StringBuffer(content); for(int i = 0; i <
                 * content.length(); i++) { if(content.charAt(i) == '?' &&
                 * !listUnKnown.contains(i)) { contentBuffer = contentBuffer.replace(i,i+1,"");
                 * } }
                 */
		          buf.append("update email_name set content=? where id=?");
		          ArrayList list = new ArrayList();
		          list.add(content.toString());
		          list.add(template_id);
		          dao.update(buf.toString(), list);
		     }else{
		    	 vo.setString("content",content);
			      dao.updateValueObject(vo);
		     }
		   // filterFiledList(vo.getString("content"),fieldList);
		     bo.addEmailField(template_id,fieldList,flag);
		     this.formHM.put("tempalteId", template_id);
		     
	          }catch(Exception e){
		          e.printStackTrace();
		          throw GeneralExceptionHandler.Handle(e);
	          }
	          //hm.remove("b_save");
		}
	
	/**
	 * 取得登录用户的单位编码
	 * @param a0100
	 * @param pre
	 * @return
	 */
	private String getUserUnitId(String a0100,String pre)
	{
		String b0110="";
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append(" select b0110 from ");
			buf.append(pre+"a01 where a0100='");
			buf.append(a0100+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				b0110=this.frowset.getString("b0110");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return b0110;
	}
	
	private String getB0110(String b0110){
		
		 String[] s = b0110.split("`");
		 String b0110Email = s[0].substring(2);
		 return b0110Email;
	}
	private void filterFiledList(String content,ArrayList fieldList){
		if(fieldList==null)
			return;
		for(int i=0;i<fieldList.size();i++)
		{
			String s=SafeCode.decode(((String)fieldList.get(i)==null|| "".equals((String)fieldList.get(i)))?"":(String)fieldList.get(i));
			if(s==null||s.trim().length()==0)
			{
				continue;
			}
			String[] arr=s.split("`");
			if(1==Integer.parseInt(arr[9])){
				if(content.indexOf(arr[2]+"#")==-1){
					fieldList.remove(i);
					i--;
				}
			}else{
				if(content.indexOf(arr[2]+"$")==-1){
					fieldList.remove(i);
					i--;
				}
			}
			
		}
	}

	
	

}
