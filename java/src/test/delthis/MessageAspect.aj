package test.delthis;

public aspect MessageAspect {
	
	pointcut deliverMessage()
		: call (* Service.doCheck(..));
	
	pointcut addComment(String com) 
		: call (* Client.saySomething(String,String))
			&& args(com,String);
	
	before() : deliverMessage() {
		System.out.println("Bo sebelum deliver ni");
	}

	void around(String com) : addComment(com) {
		if (com.equalsIgnoreCase("pandu")) {
			proceed("sinyore " + com);
		} else {
			proceed("mr. " + com);
		}
		
	}
}
