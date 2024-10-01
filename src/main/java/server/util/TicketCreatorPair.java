package server.util;

import objectpack.Ticket;

import java.io.Serializable;

public class TicketCreatorPair<X extends Ticket, Y> extends Pair<X, Y> implements Comparable, Serializable {

    public TicketCreatorPair(X first, Y second) {
        super(first, second);
    }

    @Override
    public int compareTo(Object o) {
        return this.getFirst().compareTo((Ticket) ((Pair)o).getFirst());
    }

    @Override
    public boolean equals(Object o){
        if(!o.getClass().equals(TicketCreatorPair.class))
            return false;
        return this.getFirst().equals(((Pair)o).getFirst());
    }

    @Override
    public int hashCode(){
        return getFirst().hashCode();
    }

    @Override
    public String toString(){
        return this.getFirst() + ", Владелец: " + this.getSecond();
    }
}
