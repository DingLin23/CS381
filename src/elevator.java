import java.util.ArrayList;
import java.util.List;

    public class elevator {
        //point out the direction
        // 1 mean up,-1 mean down,0 mean idle
        int direction;

        //current floor
        int current_floor;

        // a list that record person who is waiting for the elevator
        List<person> waiting;


        // a list record people is in elevator
        List <person> in;

        //record the time of  updating the data
        double clock;

        public elevator(){
            direction=0;
            current_floor=1;
            clock=0.0;
            waiting=new ArrayList<person>();
            in = new ArrayList<person>();
        }

        public void add(person p,double new_arrival_time){
            if(clock > new_arrival_time){
                //if the clock in elevator is more than new_arrival_time
                //
                waiting.add(p);
            }else{
                //check if the is at same floor as the person
                if(current_floor == p.start){
                    //yes, get on the elevator

                    //elevator going up or down
                    if(direction == 0){
                        if(p.start >p.destination)
                            direction=-1;
                        else
                            direction=1;
                    }

                    in.add(p);

                    //get cause 1 time unit
                    clock++;

                }else{
                    //need to wait for elevator
                    waiting.add(p);

                    if(direction == 0){
                        if(current_floor >p.start)
                            direction=-1;
                        else
                            direction=1;
                    }
                }

            }
        }


        public void update(double new_arrival_times){
//		double elevator_time=0.0;
            double oldTime=clock;
//		System.out.println(current_floor+" "+clock);
            //check if it is in idle
            if(direction !=0){

                //if not , we update it
//			double oldTime=clock;

                //loop until the oldTime is greater than current time
                //or the waiting and in list are empty which is idle
                while(oldTime <=new_arrival_times){
//				System.out.println(current_floor);
//				elevator_time=oldTime;

                    //check if someone need to get off
                    if(waiting.size() !=0 || in.size() != 0){

                        if(getOff(oldTime)){
                            //need to get off
                            oldTime++;
                            if(getOn()){
                                //both get off and get on
                                oldTime++;
                            }
                        }else{
                            //no need to get off
                            //but still need to get on the elevator
                            if(getOn()){
                                oldTime++;
                            }else{
                                //no one get off and get on
                                //then move next floor
                                oldTime+=2;


                                //if elevator reach the 1 floor or 8 floor
                                if(current_floor ==8){
                                    direction=-1;
                                    current_floor--;
                                }else if(current_floor ==1){
                                    direction=1;
                                    current_floor++;

                                }else if(direction >0){
                                    current_floor++;

                                }else
                                    current_floor--;

                                if(current_floor == 8){
                                    direction = -1;
                                }else if(current_floor == 1){
                                    direction =1;
                                }
                            }

                        }
                    }else{
                        direction =0;
                        break;
                    }

                }//end of while loop

            }

            //update current time
            if(oldTime==0.0) clock=new_arrival_times;
            else{
                if(clock < new_arrival_times)
                    clock=oldTime;
            }



        }

        public boolean getOn(){
            for(int i=0;i<waiting.size();i++){
                int result = (waiting.get(i).start - waiting.get(i).destination ) > 0 ? -1 : 1;
                if(waiting.get(i).start == current_floor && result == direction){
                    //elevator and people are in the same floor
                    //add it to in list
                    in.add(waiting.get(i));

                    //remove the person from waiting list
                    waiting.remove(i);
                    return true;
                }
            }

            return false;
        }

        public boolean getOff(double get_off_Time){
            for(int i=0;i<in.size();i++){
                //check if someone need to get off
                if(in.get(i).destination ==current_floor){
                    //get the data from the person
                    person p = in.get(i);

                    //get off cause 1 time unit
                    double service_times =get_off_Time+1-p.arrive_time;

//				System.out.println(get_off_Time +" "+p.arrive_time+" "+service_times);
                    //add to the matrix in the main
                    elevatorMain matrix = new elevatorMain();
                    matrix.addToMatrix(p.start,p.destination,service_times);

                    in.remove(i);

                    if(in.size() ==0 && waiting.size() == 0) direction=0;
                    return true;
                }
            }

            return false;
        }
    }


