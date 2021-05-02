package service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    static Logger logger = LogManager.getLogger("Cache");
    public static ConcurrentHashMap<String,Long> site;
    final static long EXPIRE_TIME = 1000*60*60*72;
    public Cache(){
        site = new ConcurrentHashMap<>();
    }
    public static void removeExpiredSites(){
        for(String key : site.keySet()){
            long time = site.get(key);
            if(time <= System.currentTimeMillis()-(EXPIRE_TIME)){
                site.remove(key);
            }
        }
    }
    public static void set(String url){
        site.put(url,System.currentTimeMillis());
    }
    public static boolean dontHas(String url){
        if(site.containsKey(url)){
//            logger.info("Hit the cache : "+url);
            return false;
        }else{
            return true;
        }
    }
}
