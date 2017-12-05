package jp.ne.jinoxst.mas.itg.uriage.util;

import java.util.HashMap;

public class GlobalRegistry {
    private static GlobalRegistry instance;
    private static HashMap<String, Object> registry;
    public static final int NOT_FOUND = -3;

    public synchronized static GlobalRegistry getInstance(){
        if(instance == null){
            instance = new GlobalRegistry();
        }

        return instance;
    }

    private GlobalRegistry(){
        registry = new HashMap<String, Object>();
    }

    public synchronized void setRegistry(String key, String value){
        registry.put(key, value);
    }

    public synchronized void setObject(String key, Object obj){
        registry.put(key, obj);
    }

    public synchronized void setRegistry(String key, int value){
        registry.put(key, value);
    }

    public synchronized String getString(String key){
        Object obj = registry.get(key);
        if(obj != null){
            if(obj instanceof String){
                return (String)obj;
            }else{
                return String.valueOf(obj);
            }
        }else{
            return null;
        }
    }

    public synchronized Object getObject(String key){
        return registry.get(key);
    }

    public synchronized int getInt(String key){
        Object obj = registry.get(key);
        if(obj != null){
            if(obj instanceof Integer){
                return (Integer)obj;
            }else{
                return NOT_FOUND;
            }
        }else{
            return NOT_FOUND;
        }
    }

    public synchronized void removeRegistry(String key){
        registry.remove(key);
    }
}
