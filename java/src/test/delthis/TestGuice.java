package test.delthis;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

public class TestGuice {

	public static void main(String[] args) {
		
		Module modues[] = {new TestGuice().new TestModule() };		
		Injector injector = Guice.createInjector(modues[0]);
		
		Client c = injector.getInstance(Client.class);
		c.doCheck();
		c.saySomething("Kaka", "Cumt");
		c.saySomething("Pandu", "Bul");
		
		/* Ain't work but find out later 
		Injector injector2 = Guice.createInjector();	
		Greeter g = injector2.getInstance(Greeter.class);
		g.sayHello();
		*/
		 
	}

	
	private class Greeter {
		
		@Inject Displayer display;
		void sayHello() {
			display.display("Hello boooo");
		}
		
	}
	
	
	private class Displayer {
		void display(String s) {
			System.out.println(s);
		}
	}
	
	
	private class TestModule implements Module {

		public void configure(Binder binder) {
			binder.bind(Service.class).to(DefaultServiceImpl.class);
		}
		
	}
	
}
