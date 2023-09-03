import java.io.Serializable;

public class DataPacket implements Serializable {
    private String taskDescription;
    private int data;

    public DataPacket(String taskDescription, int data) {
        this.taskDescription = taskDescription;
        this.data = data;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public int getData() {
        return data;
    }

    public int calculateFactorial() {
        if (data == 0) {
            return 1;
        }
        int factorial = 1;
        for (int i = 1; i <= data; i++) {
            factorial *= i;
        }
        return factorial;
    }
}
