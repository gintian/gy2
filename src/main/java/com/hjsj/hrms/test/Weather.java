/**
 * 
 */
package com.hjsj.hrms.test;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2008-1-28:下午02:49:03</p> 
 *@author cmq
 *@version 4.0
 */
public class Weather {
    float temperature;
    String forecast;
    boolean rain;
    float howMuchRain;
    
    public void setTemperature(float temp){
        temperature = temp;
    }

    public float getTemperature(){
        return temperature;
    }
    
    public void setForecast(String fore){
        forecast = fore;
    }

    public String getForecast(){
        return forecast;
    }
    
    public void setRain(boolean r){
        rain = r;
    }

    public boolean getRain(){
        return rain;
    }
    
    public void setHowMuchRain(float howMuch){
        howMuchRain = howMuch;
    }

    public float getHowMuchRain(){
        return howMuchRain;
    }

}
