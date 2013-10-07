package fr.codestory.elevator;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Miguel Basire
 */
class Destinations<T> {

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

    public T reached(int floor){
         return destinations.remove(floor);
    }

    public void clear(){
         destinations.clear();
    }

    public T at(int floor){
       return destinations.containsKey(floor) ? destinations.get(floor) : none;
    }

    public Destinations<T> above(int floor){
        if(destinations.isEmpty()) return this;
        else return new Destinations<>(destinations.tailMap(Math.min(destinations.lastKey()+1, floor + 1)),none);
    }

    public Destinations<T> below(int floor){
         return new Destinations<>(destinations.headMap(floor),none);
    }

    public Set<Integer> floors(){
       return destinations.keySet();
    }

    public boolean contains(T to) {
        return destinations.containsKey(to);
    }

    public boolean isEmpty(){
        return destinations.isEmpty();
    }
}
