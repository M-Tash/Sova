package com.ntmsecurity.sova.net;

import org.json.JSONObject;

public interface RespListener {

    void onResponseReceived(JSONObject response);

}
