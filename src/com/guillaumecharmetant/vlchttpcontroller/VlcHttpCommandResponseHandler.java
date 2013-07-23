package com.guillaumecharmetant.vlchttpcontroller;

public interface VlcHttpCommandResponseHandler {
	public void handleResponse(VlcHttpController controller, VlcHttpResponse response);
}
