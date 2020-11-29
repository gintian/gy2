package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeCenterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:DeletePositionApplyTrans</p>
 * <p>Description:删除人员信息操作</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-01-26</p>
 * @author wangcq
 * @version 1.0
 */
public class DeletePositionApplyTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		Boolean result = Boolean.FALSE;
		try{
			ContentDAO dao=new ContentDAO(this.frameconn);
			String fromModule=(String)this.getFormHM().get("fromModule");  //resumeCenter:简历中心  talents：人才库 
			String opt=(String)this.getFormHM().get("opt"); //delete:删除  //removeTalents 移出人才库
			ArrayList array = (ArrayList)this.getFormHM().get("array");  //array中是a0100、zp_pos_id、nbase数据 
			StringBuffer info=new StringBuffer("");
			ArrayList values=null;
			ArrayList value_ns=null;
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn());
			ResumeCenterBo resumeCenterBo=new ResumeCenterBo(this.getFrameconn(),this.userView,fromModule);
			PositionBo pbo = new PositionBo(this.getFrameconn(), new ContentDAO(this.getFrameconn()), this.userView);
			deleteRepetition(array);
			for(int i=0; i<array.size(); i++){
				StringBuffer deleteStr = new StringBuffer("");
				
				ArrayList param = (ArrayList)array.get(i);
				String a0100 = (String)param.get(0);
				a0100 = PubFunc.decrypt(a0100);
				String nbase = (String)param.get(2); 
				nbase = PubFunc.decrypt(nbase);
				String a0101=(String)param.get(3);
				String z0301s = "";
				values = new ArrayList();
				value_ns = new ArrayList();
				values.add(a0100);
				value_ns.add(a0100);
				value_ns.add(nbase); 
				
				if("delete".equals(opt)){
                    String name = getNoDeleteName(a0100, nbase, dao);
                    if (name != null && name.length() > 0) {
                        info.append("," + name);
                        continue;
                    }
                    
                    z0301s = getZ0301FromZp(a0100,nbase,dao);
				}
				
				if("delete".equals(opt)) //删除人员信息
				{
					if("talents".equalsIgnoreCase(fromModule))
					{
						int number=resumeCenterBo.deleteZpTalents(a0100,nbase,dao,value_ns); 
						if(number==0)
						{
							info.append(","+a0101);
							continue;
						}
					}
					
				    //将对应招聘信息全部删除(防止存在垃圾数据) 
					ArrayList list=bo.getZpFieldList();
					for(int j=0;j<((ArrayList)list.get(0)).size();j++)
					{
						LazyDynaBean abean=(LazyDynaBean)((ArrayList)list.get(0)).get(j);
						String setid=(String)abean.get("fieldSetId");
						deleteStr.delete(0, deleteStr.length());
						deleteStr.append("delete from "+nbase+setid+" where  a0100=?");
						dao.delete(deleteStr.toString(), values);
					}
					//删除zp_pos_tache中数据
					deleteStr.delete(0, deleteStr.length());
					deleteStr.append("delete from zp_pos_tache where a0100=? and nbase=?");
					dao.delete(deleteStr.toString(), value_ns);
					//删除人才库中数据 
					dao.delete("delete from zp_talents where a0100=? and nbase=? ", value_ns);
					//删除a00中数据
					deleteStr.delete(0, deleteStr.length());
					deleteStr.append("delete from "+nbase+"a00 where a0100=?");
					dao.delete(deleteStr.toString(), values);
				}
				else if("removeTalents".equals(opt)) //移出人才库
				{ 
					int number=resumeCenterBo.deleteZpTalents(a0100,nbase,dao,value_ns); 
					if(number==0)
						info.append(","+a0101);
					
				}
				if(StringUtils.isNotEmpty(z0301s)){
				    String[] split = z0301s.split(",");
				    for (int j = 0; j < split.length; j++) {
				        pbo.saveCandiatesNumber(split[j], 1);
				        pbo.saveCandiatesNumber(split[j], 3);
                        
                    }
                }
			}
			result = Boolean.TRUE;
			if(info.length()>0)
			{
				String msg = "";
				String str = info.substring(1);
				char []strs = str.toCharArray();
				for(int i=0;i<strs.length;i++)
				{
					msg += strs[i];
					if(i!=0&&i%60==0)
					{
						msg += "<br>";
					}
				}
				if("delete".equals(opt)) //删除人员信息
				{
					if("talents".equalsIgnoreCase(fromModule))
					{						
						this.getFormHM().put("info",SafeCode.encode("以下人员为候选人或不是'我的人才库'人员，删除不成功!<br>"+msg)); 
					}
					else{						
						this.getFormHM().put("info",SafeCode.encode("以下人员为已处于流程中，删除不成功!<br>"+msg)); 
					}
				}
				else if("removeTalents".equals(opt)) //移出人才库
				{				
					
					this.getFormHM().put("info",SafeCode.encode("只能移出'我的人才库'人员， 以下人员移出不成功!<br>"+msg)); 
				}
			}
			else
				this.getFormHM().put("info","");
 
		}catch(Exception e){
			e.printStackTrace();
			result = Boolean.FALSE;
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("result",result);
		}
				
	}
	
	
	
	/**
	 * 
	 * @param a0100
	 * @param nbase
	 * @param dao
	 * @return
	 * @throws GeneralException 
	 */
	private String getZ0301FromZp(String a0100, String nbase, ContentDAO dao) throws GeneralException {
	    StringBuffer z0301s = new StringBuffer();
	    try {
	        String sql = "select zp_pos_id from zp_pos_tache where a0100 = ? and nbase = ?";
	        ArrayList list = new ArrayList();
	        list.add(a0100);
	        list.add(nbase);
	        RowSet rs = dao.search(sql, list);
	        while (rs.next()) {
	            z0301s.append(rs.getString("zp_pos_id")+",");
                
            }
	        
	        
	    }catch (Exception e) {
	        e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return z0301s.toString();
    }

    /**
	 * 当应聘人员为候选人时不能删除且返回名字，可删除时返回空字符串
	 * @param a0100
	 * @param nbase
	 * @return
	 */
	private String getNoDeleteName(String a0100,String nbase,ContentDAO dao)
	{
		String name="";
		try
		{
			String setid=nbase+"A01";
			String sql="select "+setid+".a0101 from zp_pos_tache,"+setid+" where "+setid+".a0100=zp_pos_tache.a0100 and  zp_pos_tache.status='1' and zp_pos_tache.nbase=? and zp_pos_tache.a0100=? and zp_pos_tache.resume_flag is not null ";//in ( '0105' ";
			//sql+=",'0205','0306','0307','0406','0407','0503','0603','0705','0904' )";
			ArrayList valueList=new ArrayList();
			valueList.add(nbase);
			valueList.add(a0100);
			this.frowset=dao.search(sql,valueList);
			if(this.frowset.next())
			{
				name=this.frowset.getString("a0101");
			}
		}
		catch(Exception e){
			e.printStackTrace(); 
		}
		return name;
	}
	
	/**
	 * 去除重复人员id
	 * @param array
	 */
	private void deleteRepetition(ArrayList array){
		ArrayList<String> list = new ArrayList<String>();
		for(int i=array.size()-1;i>=0;i--) {
			ArrayList param = (ArrayList)array.get(i);
			String a0100 = (String)param.get(0);
			if(list.contains(a0100)) {
				array.remove(i);
			}else {
				list.add(a0100);
			}
		}
	}

}
