package misc;

public class IdGenerator {
	
	private long id = 1;
	
	public long getId(){
		return id++;
	}

}
