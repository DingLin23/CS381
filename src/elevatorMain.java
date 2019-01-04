import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class elevatorMain {
    static double clock = 0.0;
    static elevator[] ele = new elevator[5];
    static double[][] matrix = new double[9][9];
    static int[][] matrix_count = new int[9][9];

    public static void main(String[] args) {
        PrintWriter out = null;
        try {
            out = new PrintWriter("output.txt");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        // 5 elevators
        for (int i = 0; i < 5; i++) {
            ele[i] = new elevator();
        }

        for (int i = 0; i < 10000; i++) {
            /*
             * determine the inter-arrival time
             */

            double inter_arrival_time = 2.0;

            // record new arrival time
            clock += inter_arrival_time;

            // determine which floor people arrive
            int start = startFloor();

            // determine the destination
            int end = destination(start);

            // update elevator data to current clock times
            update();

            // assign elevator to take the person
            assign_elevator(start, end);

        }

        // all people had been arrival
        // but people still in the waiting list and in list

        boolean all_idle = false;

        while (!all_idle) {
            // loop until there is no more people in waiting list and in list of
            // all elevator
            double new_time = 1.0;
            clock += new_time;
            update();

            // check if all elevator are idle
            all_idle = true;
            for (int i = 0; i < 5; i++) {
                if (ele[i].direction != 0) {
                    all_idle = false;
                    break;
                }
            }
        }

        // output

        out.write("  ");
        for(int i=1;i<9;i++){
            out.write(i+"  ");
        }
        out.write("\r\n");

        out.write("  ");
        for(int i=1;i<9;i++){
            out.write("---");
        }
        out.write("\r\n");

        for(int i=1;i<9;i++){
            out.write(i+"|");
            for(int j=1;j<9;j++){
                int result =(int) Math.round(matrix[i][j] / matrix_count[i][j]);
                if(result >=10)
                    out.write(result+" ");
                else
                    out.write(result+"  ");
            }// inner loop
            out.write("\r\n");
        }

        out.close();
    }

    // determine which floor people arrive
    static int startFloor() {
        // evenly distribute the floor people are
        double half = Math.random();
        if (half < 0.5) {
            return 1;
        } else {
            double result = Math.round(Math.random() * 7 + 1);
            if (result == 1.0) {
                return 1;
            } else if (result == 2.0) {
                return 2;
            } else if (result == 3.0) {
                return 3;
            } else if (result == 4.0) {
                return 4;
            } else if (result == 5.0) {
                return 5;
            } else if (result == 6.0) {
                return 6;
            } else if (result == 7.0) {
                return 7;
            } else
                return 8;
        }
    }

    // determine the destination
    static int destination(int start) {
        int result;

        // randomly assign the destination
        // if the destination is same as start point,randomly again
        while (true) {
            result = (int) Math.round((Math.random() * 7 + 1));
            if (result != start)
                break;
        }

        return result;
    }

    // update data of each elevator
    static void update() {
        for (int i = 0; i < 5; i++) {
            ele[i].update(clock);
        }
    }

    // assign elevator
    static void assign_elevator(int start, int end) {
        // determine if elevator has been assigned

        int elevator = -1;

        // case 1 , all or part of elevator are in idle
        // from elevator 1 to 5, checking which one is in idle
        for (int i = 0; i < 5; i++) {
            if (ele[i].direction == 0) {
                elevator = i;
                break;
            }
        }

        // case 2
        // all elevators are in used, but part of elevator with same direction
        // check which elevator has same direction and closest to the people
        if (elevator == -1) {
            int min = -1;// not decide yet which ele person gonna take
            int range = 0;
            double times = 0;
            // check the direction of people
            // 1 == up, -1 == down
            int direction = end - start;
            if (direction > 0)
                direction = 1;
            else
                direction = -1;

            for (int i = 0; i < 5; i++) {
                // only looking at same direction
                if (ele[i].direction == direction) {
                    // then check if the floor people is at is in the elevator
                    // direction
                    int result = start - ele[i].current_floor;

                    // then based on the direction of elevator to determine if
                    // the floor is on its way
                    if (direction > 0 && result > 0) {
                        // if it is going up
                        if (min == -1) {
                            min = i;
                            range = result;
                            times = ele[i].clock;
                        } else {
                            if (result < range) {
                                range = result;
                                min = i;
                                times = ele[i].clock;
                            }else if(result == range && ele[i].clock < times){
                                //since elevator time may be more than current times
                                //so there may be in some case that a elevator is 1 time unit further in that floor which
                                //another elevator is in that floor in current time
                                //so we need to assign the elevator is in that floor in current time
                                min =i;
                                times =ele[i].clock;
                            }
                        }
                    } else if (direction < 0 && result < 0) {
                        // if it is going down
                        result = Math.abs(result);
                        if (min == -1) {
                            min = i;
                            range = result;
                        } else {
                            if (result < range) {
                                range = result;
                                min = i;
                            }
                        }
                    } // end of direction and result if statement
                }
            } // end of for loop
            if (min != -1)
                elevator = min;
        } // end of case 2

        // case 3
        // all elevators dont have same direction with people's destination, or
        // is in opposite floor
        // then find out which elevator has shortest size of waiting list and in
        // list
        if (elevator == -1) {// 0-4 elevator, -1 none
            int min = 0;// assign elevator
            int waiting = ele[0].waiting.size();// waiting people numb
            int in = ele[0].in.size();// people already in elevator
            int length = waiting + in;

            for (int i = 1; i < 5; i++) {
                waiting = ele[i].waiting.size();
                in = ele[i].in.size();

                int result = waiting + in;
                if (result < length) {
                    length = result;
                    min = i;
                }
            } // end of for loop
            elevator = min;
        } // end of case 3

        // add to elevator
        add(elevator, start, end);
    }// end of function

    static void add(int elevator, int start, int end) {
        person p = new person(start, end, clock);
        elevator eletor = ele[elevator];

        eletor.add(p,clock);
    }

    // add the start,end and waiting time to the matrix
    public void addToMatrix(int start, int destination, double times) {
        matrix_count[start][destination]++;
        matrix[start][destination] += times;
    }
}
