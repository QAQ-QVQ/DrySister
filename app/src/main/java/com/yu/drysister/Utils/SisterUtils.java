package com.yu.drysister.Utils;

import com.yu.drysister.Bean.Sister;

public  class SisterUtils {
    public static Sister sister;

    //创建 SingleObject 的一个对象
    private static SisterUtils instance = new SisterUtils();
    //让构造函数为 private，这样该类就不会被实例化
    private SisterUtils(){}
    //获取唯一可用的对象
    public static SisterUtils getInstance(){
        return instance;
    }

    public void setInstance(Sister sister){
        this.sister = sister;
    }

}
