package com.hjsj.hrms.module.vuesupport.user.transaction;


import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.hjadmin.cache.CacheUtil;
import com.hrms.hjsj.hjadmin.cache.FrameworkCacheKeysEnum;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 在线用户查询
 *
 * @author ZhangHua
 * @date 18:18 2020/6/10
 */
public class SearchOnlineUserTrans extends IBusiness {
    /**
     * 所有的交易的子类须实现的方法
     *
     * @throws Exception
     */
    @Override
    public void execute() throws GeneralException {
        try {

            int page = (int) this.getFormHM().get("page");


            HashMap<String, UserView> hashMap = (HashMap<String, UserView>) CacheUtil.getObject(FrameworkCacheKeysEnum.userViewCache);


            ArrayList<HashMap<String, String>> list = new ArrayList();


            for (String key : hashMap.keySet()) {
                HashMap<String, String> userMap = new HashMap<>();
                UserView userView = hashMap.get(key);
                if (this.getUserView().getUserName().equalsIgnoreCase(userView.getUserName())) {
                    userMap.put("userName", userView.getUserName() + "（本人）");
                } else {
                    userMap.put("userName", userView.getUserName());
                }

                userMap.put("UserFullName", userView.getUserFullName());

                if (StringUtils.isBlank(userView.getUserOrgId())) {
                    userMap.put("un", "");
                    userMap.put("unOrder", "0");
                } else {
                    CodeItem codeItem = AdminCode.getCode("UN", userView.getUserOrgId());
                    userMap.put("un", codeItem.getCodename());
                    userMap.put("unOrder", String.valueOf(codeItem.getA0000()));
                }
                if (StringUtils.isBlank(userView.getUserDeptId())) {
                    userMap.put("um", "");
                    userMap.put("umOrder", "0");
                } else {
                    CodeItem codeItem = AdminCode.getCode("UM", userView.getUserDeptId());
                    userMap.put("um", codeItem.getCodename());
                    userMap.put("umOrder", String.valueOf(codeItem.getA0000()));
                }


                userMap.put("loginTime", (String) userView.getHm().get("loginTime"));
                userMap.put("ip", userView.getRemote_ip());
//            userMap.put("accessModule",this.getEncryptLockUtil().getlockClient().getTheUserAccessModule(userView.getUserName()));
                list.add(userMap);

            }

            Collections.sort(list, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> map1, HashMap<String, String> map2) {

                    SimpleDateFormat simdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    try {
                        Date date1 = simdate.parse(map1.get("loginTime"));
                        Date date2 = simdate.parse(map2.get("loginTime"));
                        return date1.compareTo(date2) * -1;

                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }

                /**

                 //            String un1=map1.get("un");
                 //            String um1=map1.get("um");
                 int umOrder1=Integer.parseInt(map1.get("umOrder"));
                 int unOrder1=Integer.parseInt(map1.get("unOrder"));
                 //            String un2=map2.get("un");
                 //            String um2=map2.get("um");
                 int umOrder2=Integer.parseInt(map2.get("umOrder"));
                 int unOrder2=Integer.parseInt(map2.get("unOrder"));

                 if(unOrder1==unOrder2){
                 if(umOrder1==umOrder2){
                 return 0;

                 }else if(umOrder1<umOrder2){
                 return -1;
                 }else {
                 return 1;
                 }
                 }else if(unOrder1<unOrder2){
                 return -1;
                 }else {
                 return 1;
                 }
                 **/
            });

            ArrayList rlist = new ArrayList();

            for (int i = (page - 1) * 20; i < list.size() && i < page * 20; i++) {
                rlist.add(list.get(i));
            }
            this.getFormHM().put("userList", rlist);
            this.getFormHM().put("total", hashMap.size());

        } catch (Exception e) {
            e.printStackTrace();
            this.getFormHM().put("msg", e.getMessage());
        }
    }
}
