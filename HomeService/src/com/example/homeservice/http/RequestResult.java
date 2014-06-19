package com.example.homeservice.http;



/**
 * Created with IntelliJ IDEA.
 * User: weiguo.ren
 * Date: 13-9-16
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class RequestResult {
    public ProtocolType getType(){return null;};
    public void onSuccess(Object o){};
    public void onFailure(CharSequence failure){};
    public void OnErrorResult(CharSequence error){};

}
