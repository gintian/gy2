package com.hjsj.hrms.businessobject.sys.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.EncryptLockClient;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
/**
 * 处理用户功能权限显示
 * @author Owner
 *
 */
public class FunctionWizard {

	private Document doc;
	private EncryptLockClient lock;

	public FunctionWizard(){
		this.init();
	}
	
	public void setLock(EncryptLockClient lock) {
        this.lock = lock;
    }

    public EncryptLockClient getLock() {
        return lock;
    }

    public void init(){
    	InputStream in = null;
	     try {
	    	 in =this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");

			doc = PubFunc.generateDom(in);

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
            e.printStackTrace();
        } finally {
			PubFunc.closeIoResource(in);
		}
	}
	
	
	public String functionXmlToHtml(String functionPrivs){
		if(functionPrivs == null || "".equals(functionPrivs)){
			return "";
		}
		String fp [] = functionPrivs.split(",");
		Arrays.sort(fp);
		List funp = Arrays.asList(fp);

		HashMap map = functionXmlToMap();		
		String html =toHtml(funp,map); // tempHtml(map,funp);
		return html;
	}
	
	
	
	
	public String toHtml(List funp,HashMap map)
	{
		if(map == null){
			return "";
		}
		StringBuffer fps = new StringBuffer();
		Set set=new HashSet();
		for(int i=0;i<funp.size();i++)	
		{
			if(((String)funp.get(i)).length()==0) {
                continue;
            }

			set.add(new Integer(((String)funp.get(i)).length()));

		}
		int[] temps=new int[set.size()];
		int i=0;
		for(Iterator t=set.iterator();t.hasNext();) {
            temps[i++]=((Integer)t.next()).intValue();
        }
		Arrays.sort(temps);
		HashMap existFunc=new HashMap();
		if(temps.length>0)
		{
			List tempList=getTempList(funp,temps[0],"");
			for(int j=0;j<tempList.size();j++)
			{
				String _fun_id=(String)tempList.get(j);
				executeHtml(fps,map,0,temps,_fun_id,funp,existFunc);
			}
			
		}
		
		
		return fps.toString();
	}
	
	
	public void executeHtml(StringBuffer str,HashMap map,int j,int[] temps,String fun_id,List funp,HashMap existFunc)
	{
		if(existFunc.get(fun_id)!=null) {
            return;
        }
		existFunc.put(fun_id, "1");
		Object desc=map.get(fun_id);
		if(desc==null) {
            return;
        }
		for(int k=0; k<j;k++){
			str.append("&nbsp;");
			str.append("&nbsp;");
		}
		str.append(desc);
		str.append("<br>");
		
		for(int i=j+1;i<temps.length;i++)
		{
			int length=temps[i];
			List tempList=getTempList(funp,temps[i],fun_id);
			if(tempList.size()>0)
			{
				for(int n=0;n<tempList.size();n++)
				{
					String _fun_id=(String)tempList.get(n);
					executeHtml(str,map,i,temps,_fun_id,funp,existFunc);
				}
			}
			
		}
		
	}
	
	
	public  List getTempList(List funp,int length,String funid)
	{
		ArrayList tempList=new ArrayList();
		for(int i=0;i<funp.size();i++)
		{
			String temp=(String)funp.get(i);
			if(temp==null||temp.trim().length()==0) {
                continue;
            }
			if(funid.length()>0)
			{
				if(temp.length()==length&&temp.startsWith(funid)) {
                    tempList.add(temp);
                }
			}
			else
			{
				if(temp.length()==length) {
                    tempList.add(temp);
                }
			}
		}
		String[] temps=new String[tempList.size()];
		for(int i=0;i<tempList.size();i++) {
            temps[i]=(String)tempList.get(i);
        }
		Arrays.sort(temps);
		List list = Arrays.asList(temps);
		return list;
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * @param map
	 * @param funp
	 * @return
	 */
	public String tempHtml(HashMap map ,List funp){
		if(map == null){
			return "";
		}
		int n=0;
		int base_length=0;
		if(funp.size()>1){
			base_length = ((String)funp.get(0)).length();
		}
		StringBuffer fps = new StringBuffer();
		for(int i=0; i<funp.size(); i++){
			String fun_id = (String)funp.get(i);


			if(fun_id == null||fun_id == "null"|| "".equals(fun_id)){
				continue;
			}
			
			/*if(i>0){
				if(fun_id.length() < ((String)funp.get(i-1)).length()){
					n = 0;
				}
			}*/
			Object desc=map.get(fun_id);
			if(desc==null) {
                continue;
            }
			
			int flag = fun_id.length()-base_length;
			for(int k=0; k<flag;k++){
				fps.append("&nbsp;");
				fps.append("&nbsp;");
			}
			fps.append(desc);
			fps.append("<br>");
			n++;
			
				
		}
		return fps.toString();
	}
	
	public HashMap functionXmlToMap(){
		HashMap map = new HashMap();
		Element root = doc.getRootElement();
		
		List list = root.getChildren("function");
		for(int i=0; i<list.size(); i++){
			Element node = (Element)list.get(i);

			String fun_id = node.getAttributeValue("id");
            
			if (!haveLockPriv(fun_id)) {
                continue;
            }

			String fun_name = node.getAttributeValue("name");
			map.put(fun_id,fun_name);
			tempMap(map,node);
		} 
		return map;
	}
	
	public void tempMap(HashMap map , Element element){
		List list = element.getChildren("function");
		for(int i=0; i<list.size(); i++){
			Element node = (Element)list.get(i);
			String fun_id = node.getAttributeValue("id");

			//System.out.println("-->"+fun_id);			
			String fun_name = node.getAttributeValue("name");
			map.put(fun_id,fun_name);
			tempMap(map,node);
		} 
	}
	
	public void getFunPrivHtml(TreeMap funPrivMap,String funcpriv){
		Element root = doc.getRootElement();
		List list = root.getChildren("function");
		for(int i=0; i<list.size(); i++){
			Element node = (Element)list.get(i);
			//String fun_id = node.getAttributeValue("id");
			if(node.getAttributeValue("ctrl_ver")!=null&&node.getAttributeValue("ctrl_ver").indexOf((String.valueOf(lock.getVersion())))==-1)
				//zhanghua 2017-6-1 权限判断添加版本控制 ctrl_ver中版本号以逗号分割 
            {
                continue;
            }
			String fun_name = node.getAttributeValue("name");
			StringBuffer sbhtml = new StringBuffer();
			funPrivMap.put(i+fun_name, sbhtml);
			this.getChildPriv(node, sbhtml, funcpriv,0);
		}
	}
	private void getChildPriv(Element node,StringBuffer sbhtml,String funcpriv,int grade){
		List list = node.getChildren();
		for(int i=0; i<list.size(); i++){
			Element _node = (Element)list.get(i);
			String fun_id = _node.getAttributeValue("id");
			
			if(!haveLockPriv(fun_id)) {
                continue;
            }
			if(_node.getAttributeValue("ctrl_ver")!=null&&_node.getAttributeValue("ctrl_ver").indexOf((String.valueOf(lock.getVersion())))==-1)//zhanghua 2017-6-1 权限判断添加版本控制 ctrl_ver中版本号以逗号分割 
            {
                continue;
            }
			String fun_name = _node.getAttributeValue("name");
			if(funcpriv.indexOf(","+fun_id+",")!=-1){
				for(int n=0;n<grade;n++){
					sbhtml.append("&nbsp;&nbsp;");
				}
				sbhtml.append(fun_name+"<br/>");
				this.getChildPriv(_node, sbhtml, funcpriv, grade+1);
			}
		}
	}
	
    /**
     * @Title: haveLockPriv   
     * @Description: 判断外挂模块功能是否有写锁   
     * @param @param func_id 功能权限编号
     * @return boolean 非外挂模块功能或有锁并符合版本要求的返回true, 否则，返回false 
     */
    private boolean haveLockPriv(String func_id) {
        if (null == this.lock) {
            return true;
        }

        if ("0B".equals(func_id) //考勤自助
                && !this.lock.isHaveBM(5)) {
            return false;
        }
        
        if ("0C".equals(func_id) //部门考勤
                && !this.lock.isHaveBM(22)) {
            return false;
        }
        
        if ("270".equals(func_id) //考勤休假
                && !this.lock.isHaveBM(6)) {
            return false;
        }

        if ("09".equals(func_id) //培训自助
                && !this.lock.isHaveBM(2)) {
            return false;
        }
                
        if ("323".equals(func_id) //培训管理
                && !this.lock.isHaveBM(10)) {
            return false;
        }

        //zxj changed at 20130510: 线学习外挂控制
        if (("32306".equals(func_id) //课程体系
                || "32364".equals(func_id) //学习情况分析
                || "3237305".equals(func_id) //流媒体服务器设置
                || "3237307".equals(func_id) //学习进度提醒设置
                || "090903".equals(func_id) //自助：培训课程
                || "090905".equals(func_id) //自助：我的课程
                || "0913".equals(func_id) //自助：上传课程                  
                )
                && !this.lock.isHaveBM(39)) {
            return false;
        }

        //zxj changed at 20130510: 在线考试外挂
        if (("3238".equals(func_id) //培训考试
                || "090907".equals(func_id) //自助：我的考试
                )
                && !this.lock.isHaveBM(40)) {
            return false;
        }

        //zxj changed at 20140210 60及以上版本 规章制度外挂（编号同文档管理 12）
        if (("1107".equals(func_id) //自助: 规章制度
                || "070701".equals(func_id) //业务: 规章制度维护
                || "081006".equals(func_id) //资源分配：规章制度
                || "3003406".equals(func_id) //资源分配：规章制度
                )
                && !this.lock.isHaveBM(12) && this.lock.getVersion() >= 60) {
            return false;
        }

        return true;
    }
	
	public static void main(String [] args){
		String temp = "22001,02001,02008,02002,02007,12000";
		String t []  = temp.split(",");
		Arrays.sort(t);
		List list = Arrays.asList(t);
		for(int i=0; i< list.size();i++){
			System.out.println(" tt=" + (String)list.get(i));
		}
		
		System.out.println("________"+list.contains("1"));
		
	}
	
}
