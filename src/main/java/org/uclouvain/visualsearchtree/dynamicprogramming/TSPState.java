package org.uclouvain.visualsearchtree.dynamicprogramming;

import java.util.BitSet;
import java.util.Objects;

public class TSPState extends State {

    /*
     *  Start of my code
     */
    private int currentCity;
    private BitSet visitedCities;

    public BitSet getVisitedCities() {
        return this.visitedCities;
    }
    public int getCurrentCity() {
        return this.currentCity;
    }

    public TSPState(int currentCity, BitSet visitedCities){
        this.currentCity = currentCity;
        this.visitedCities = visitedCities;
    }
    /*
     *  End of my code
     */

    /*
     *  My implementation
     */
    @Override
    int hash() {
         return Objects.hash(this.currentCity, this.visitedCities);
    }

    /*
     *  My implementation
     */
    @Override
    boolean isEqual(State s) {
        if (s instanceof TSPState){
            TSPState other = (TSPState) s;
            return this.currentCity == other.currentCity && this.visitedCities.equals(other.visitedCities);
        }
         return false;
    }
}
