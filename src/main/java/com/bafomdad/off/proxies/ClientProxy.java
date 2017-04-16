package com.bafomdad.off.proxies;

import com.bafomdad.off.init.InitItems;

public class ClientProxy extends CommonProxy {

	@Override
	public void initAllModels() {
		
		InitItems.initModels();
	}
}
