
public class person implements Comparable<person>{
    //which floor it arrival
    int start;

    //destination
    int destination;

    //arrive time
    double arrive_time;



    public person(int start,int destination,double arrive_time){
        this.start=start;
        this.destination=destination;
        this.arrive_time=arrive_time;
    }



    @Override
    public int compareTo(person p) {
        Integer one = start;
        return one.compareTo(p.start);
    }
}
