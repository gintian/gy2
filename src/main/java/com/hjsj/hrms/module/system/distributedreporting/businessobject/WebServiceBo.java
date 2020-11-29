package com.hjsj.hrms.module.system.distributedreporting.businessobject;

import java.net.HttpURLConnection;
import java.net.URL;

public class WebServiceBo {
	public static boolean testWsdlConnection(String address){  
        boolean flag = false;  
        try {  
            URL urlObj = new URL(address);  
            HttpURLConnection oc = (HttpURLConnection) urlObj.openConnection();  
            oc.setUseCaches(false);  
            oc.setConnectTimeout(3000); //设置超时时间  
            int status = oc.getResponseCode();//请求状态  
            if(200 == status){  
               flag = true;  
            } 
            if (flag&&address.indexOf("HrReceivePackageService/receiveDataPackage")==-1) {
                flag = false;
            }
        }catch (Exception e) {  
            e.printStackTrace();      
        }  
        return flag;  
    } 
}
