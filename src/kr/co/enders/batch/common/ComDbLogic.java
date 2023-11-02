package kr.co.enders.batch.common;

public class ComDbLogic extends AbstractDbLogic {
	private static final ComDbLogic dbLogic = new ComDbLogic();
	private ComDbLogic() {	}
	public static ComDbLogic getInstance() {
		return dbLogic;
	}
}
