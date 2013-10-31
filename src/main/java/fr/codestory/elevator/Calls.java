package fr.codestory.elevator;

/**
* @author Miguel Basire
*/
class Calls {

    public static final Calls NONE = new Calls(ElevatorRequest.NONE, ElevatorRequest.NONE);

    private ElevatorRequest up;
    private ElevatorRequest down;

    private Calls(ElevatorRequest up, ElevatorRequest down) {
        this.up = up;
        this.down = down;
    }

    public static Calls up() {
        return new Calls(new ElevatorRequest(), ElevatorRequest.NONE);
    }

    public static Calls down() {
        return new Calls(ElevatorRequest.NONE, new ElevatorRequest());
    }

    public void increase(ElevatorCommand.Side side) {
        ElevatorRequest sideToIncrease = side == ElevatorCommand.Side.UP ? up : down;

        if(sideToIncrease == ElevatorRequest.NONE){
            if(side == ElevatorCommand.Side.UP){
                up = new ElevatorRequest();
            }
            else if(side == ElevatorCommand.Side.DOWN){
                down = new ElevatorRequest() ;
            }
        }else{
           sideToIncrease.increase();
        }
    }

    public boolean going(ElevatorCommand.Side side){

        if(side == ElevatorCommand.Side.UP){
            return up != ElevatorRequest.NONE;
        }else{
            return down != ElevatorRequest.NONE;
        }
    }

    public ElevatorRequest goingUpside(){
       return up;
    }

    public ElevatorRequest goingDownside(){
        return down;
    }

}
