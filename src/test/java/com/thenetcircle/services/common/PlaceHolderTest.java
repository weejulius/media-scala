package com.thenetcircle.services.common;

import org.junit.Test;

public class PlaceHolderTest {

	@Test
	public void placeHolder() {
		PlaceHolder PLACEHOLDER=PlaceHolder.get("{", "}");
		String re = PLACEHOLDER.replace("dispatch request id : {}", ""+UUID.get());
		System.out.println(re);
	}
}
