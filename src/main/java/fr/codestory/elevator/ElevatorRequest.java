package fr.codestory.elevator;

import java.util.Comparator;
import java.util.Date;

/**
* @author Miguel Basire
*/
class ElevatorRequest {

    public static final ElevatorRequest NONE = new ElevatorRequest();

    public final Date timestamp;
    private int number;

    public ElevatorRequest() {
        this.timestamp = new Date();
        this.number = 1;
    }

    public ElevatorRequest increase() {
        if(this == NONE)
            throw new IllegalStateException("You can not increase ElevatorRequest.None");

        number++;
        return this;
    }

    public int getNumber() {
        return number;
    }

    static class OlderFirst implements Comparator<ElevatorRequest>{
        @Override
        public int compare(ElevatorRequest o1, ElevatorRequest o2) {
            return o1.timestamp.compareTo(o2.timestamp);
        }
    }
}
