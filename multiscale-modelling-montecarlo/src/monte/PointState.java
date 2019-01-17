package monte;

import javafx.scene.paint.Color;

class PointState {
     int x,y;
     Color pointColor; //color of point
     int state;//flag to mark if its border or nothing or to keep remove grains
     int h;

     PointState (){
        this.pointColor = Color.rgb(255,255,255); // #fff
        this.state = 0;
        h=5;
    }

     PointState(int x, int y) { //points creating
        this.y = y;
    }
}
