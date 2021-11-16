import controllers.OperationMarsController;

public class Main {

	public static void main(String[] args) {
		OperationMarsController operationMarsController = new OperationMarsController();
		String output=operationMarsController.processCommands();
		System.out.println(output);
	}
}
