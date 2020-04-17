package com.squarefong.notisync;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
