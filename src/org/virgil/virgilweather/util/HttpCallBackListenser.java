package org.virgil.virgilweather.util;

public interface HttpCallBackListenser {
	void onFinish(String response);

	void onError(Exception exception);
}
