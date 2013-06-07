package test.delthis;

import com.google.inject.Inject;

public class Client {

	private Service mService;
	
	@Inject
	public Client(Service aService) {
		mService = aService;
	}
	
	public void doCheck() {
		mService.doCheck();
	}
	
	
	public void saySomething(String aSome, String a) {
		System.out.println("Woii : " + aSome + ". Dan. " + a);
	}
	
}
