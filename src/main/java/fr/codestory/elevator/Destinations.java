package fr.codestory.elevator;

import java.util.*;

import static java.lang.Math.*;

/**
 * @author Miguel Basire
 */
class Destinations<T> implements Iterable<T>{

    private final SortedMap<Integer, T> destinations ;

    private final T none;

    public Destinations(T noneValue){
        this(new TreeMap<Integer,T>(),noneValue);
    }

    private Destinations(SortedMap<Integer, T> destinations, T noneValue){
        this.destinations = destinations;
        this.none = noneValue;
    }

    public void add(int floor,T value){
         destinations.put(floor,value);
    }

    public void clear(){
         destinations.clear();
    }

    public Destinations<T> above(int floor){
        if(destinations.isEmpty()) return this;
        else return new Destinations<>(destinations.tailMap(min(destinations.lastKey() + 1, floor + 1)),none);
    }

    public Destinations<T> below(int floor){
         return new Destinations<>(destinations.headMap(floor),none);
    }

    public T reached(int floor){
        return destinations.remove(floor);
    }

    public T at(int floor){
        return destinations.containsKey(floor) ? destinations.get(floor) : none;
    }

    public boolean contains(int to) {
        return destinations.containsKey(to);
    }

    public boolean isEmpty(){
        return destinations.isEmpty();
    }

    public Iterator<T> iterator(){
        return destinations.values().iterator();
    }

    public int distanceToFarthestFloorFrom(int floor){

        if(destinations.keySet().isEmpty()){
            return 0;
        }

        int lastFloor = destinations.lastKey();
        int firstFloor = destinations.firstKey();

        return max(abs(floor - lastFloor), abs(floor - firstFloor));
    }

    public int distanceToNearestFloorFrom(int floor){

        if(destinations.keySet().isEmpty()){
            return 0;
        }

        int lastFloor = destinations.lastKey();
        int firstFloor = destinations.firstKey();

        return min(abs(floor - lastFloor), abs(floor - firstFloor));
    }

    List<T> list(){
        return new ArrayList<>(destinations.values());
    }

}
