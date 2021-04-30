package CS4120.ucmo.LaffertyVarvil;

public class Elevator {
    int currentFloor;
    int requestedFloor;
    int max;
    int min;

    public Elevator(int max, int min) {
        this.currentFloor = 0;
        this.requestedFloor = 0;
        this.max = max;
        this.min = min;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getRequestedFloor() {
        return requestedFloor;
    }

    public void setRequestedFloor(int requestedFloor) {
        this.requestedFloor = requestedFloor;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String move(int requestedFloor) {
        if (requestedFloor > max || requestedFloor < min) {
            return null;
        }

        if (this.currentFloor == requestedFloor) {
            return "Door opens";
        } else {
            //else move towards requested floor
            int diff = this.currentFloor - requestedFloor;
            this.currentFloor -= diff;
            return move (requestedFloor);
        }
    }

    /*public static void main(String[] args) {
        Elevator ele = new Elevator(5,0);
        System.out.println("Current floor" + ele.currentFloor);
        ele.move(1);
        System.out.println("Current floor" + ele.currentFloor);
        ele.move(5);
        System.out.println("Current floor" + ele.currentFloor);
        ele.move(2);
        System.out.println("Current floor" + ele.currentFloor);
        ele.move(1);
        System.out.println("Current floor" + ele.currentFloor);
    }*/
}
