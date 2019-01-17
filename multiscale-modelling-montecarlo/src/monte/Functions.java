package monte;

import javafx.scene.paint.Color;
import java.util.*;

class Functions {

    private int width;
    private int height;
     PointState[][] tempTab;
    PointState[][] pointsTab;       //tempTab=tempTab current cellsold tempTab before making dualphase selection
    private List<Color> listColors;

    Functions(int width, int height){
        this.width=width+2;
        this.height=height+2;
        initializeBoard();
        listColors =new ArrayList<>();
    }
    private void initializeBoard() {
        tempTab =new PointState[width][height];
        pointsTab =new PointState[width][height];
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                tempTab[i][j]=new PointState();
                pointsTab[i][j]=new PointState();
            }
        }
    }

    private void vonNeumann(int x, int y){
        if(pointsTab[x][y].pointColor.equals(Color.rgb(255,255,255))){
            List<Color> neumColors=new ArrayList<>();

            if(!pointsTab[x+1][y].pointColor.equals(Color.rgb(255,255,255)) && !pointsTab[x+1][y].pointColor.equals(Color.rgb(0,0,0)) && pointsTab[x+1][y].state !=2)
                neumColors.add(pointsTab[x+1][y].pointColor);

            if(!pointsTab[x-1][y].pointColor.equals(Color.rgb(255,255,255)) && !pointsTab[x-1][y].pointColor.equals(Color.rgb(0,0,0)) && pointsTab[x-1][y].state !=2 )
                neumColors.add(pointsTab[x-1][y].pointColor);

            if(!pointsTab[x][y+1].pointColor.equals(Color.rgb(255,255,255)) && !pointsTab[x][y+1].pointColor.equals(Color.rgb(0,0,0)) && pointsTab[x][y+1].state !=2)
                neumColors.add(pointsTab[x][y+1].pointColor);

            if(!pointsTab[x][y-1].pointColor.equals(Color.rgb(255,255,255)) && !pointsTab[x][y-1].pointColor.equals(Color.rgb(0,0,0)) && pointsTab[x][y-1].state !=2)
                neumColors.add(pointsTab[x][y-1].pointColor);

            if(!neumColors.isEmpty()){
                Map<Color, Integer> map=new HashMap<>();
                for(Color color: neumColors){
                    if(!map.containsKey(color))
                        map.put(color,1);
                    else
                        map.put(color, map.get(color)+1);
                }
                tempTab[x][y].pointColor = Collections.max(map.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
            }
        }
    }

    void setSeeds(int numberOfSeeds){
        Random rng = new Random();
        int newSeeds=0;
        List<Color> colors=new ArrayList<>();

        while(newSeeds<numberOfSeeds) {
            int x=rng.nextInt(width-1)+1;
            int y=rng.nextInt(height-1)+1;
            if(pointsTab[x][y].pointColor.equals(Color.rgb(255,255,255))){
                Color newColor= Color.rgb(rng.nextInt(256),rng.nextInt(256),rng.nextInt(256));
                if(!colors.contains(newColor)){
                    tempTab[x][y].pointColor =newColor;
                    colors.add(newColor);
                    newSeeds++;
                }
            }
        }

        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                pointsTab[i][j].pointColor = tempTab[i][j].pointColor;
            }
        }

    }

    boolean isBoardFull(){
        for(int i=1;i<width-1;i++){
            for(int j=1;j<height-1;j++){
                if(tempTab[i][j].pointColor.equals(Color.rgb(255,255,255))) {
                    return false;
                }
            }
        }
        return true;
    }

    void iterationCA(){
        for(int i=1;i<width-1;i++){
            for(int j=1;j<height-1;j++){

                vonNeumann(i, j);
            }
        }

        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                pointsTab[i][j].pointColor = tempTab[i][j].pointColor;
                pointsTab[i][j].state = tempTab[i][j].state;
            }
        }
    }


    void removeGrains(List<Color> choosenGrains, String structType){
        if(structType.equalsIgnoreCase("Substructure")) {
            for (int i = 1; i < width - 1; i++) {
                for (int j = 1; j < height - 1; j++) {
                    if (!choosenGrains.contains(pointsTab[i][j].pointColor)) {
                        if (!pointsTab[i][j].pointColor.equals(Color.rgb(0,0,0))) {
                            tempTab[i][j].pointColor =Color.rgb(255,255,255);
                        }
                    } else
                        tempTab[i][j].state =2;
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    pointsTab[i][j].pointColor = tempTab[i][j].pointColor;
                    pointsTab[i][j].state = tempTab[i][j].state;
                }
            }
        }
        else if(structType.equalsIgnoreCase("Dual-Phase")){
            for (int i = 1; i < width - 1; i++) {
                for (int j = 1; j < height - 1; j++) {
                    if (!choosenGrains.contains(pointsTab[i][j].pointColor)) {
                        if (!pointsTab[i][j].pointColor.equals(Color.rgb(0,0,0))) {
                            tempTab[i][j].pointColor =Color.rgb(255,255,255);
                        }
                    } else {
                        tempTab[i][j].state =2;
                        tempTab[i][j].pointColor =Color.rgb(128,128,128);
                    }
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    pointsTab[i][j].pointColor =(tempTab[i][j].pointColor);
                    pointsTab[i][j].state =(tempTab[i][j].state);
                }
            }
        }
    }

    void checkNeighbours() {// jezeli sasiad ma różny kolor od jakiejs komorki to to jest granica
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                if(!pointsTab[i][j].pointColor.equals(pointsTab[i][j+1].pointColor))
                    tempTab[i][j].state =1;
                else if(!pointsTab[i][j].pointColor.equals(pointsTab[i][j-1].pointColor))
                    tempTab[i][j].state =1;
                else if(!pointsTab[i][j].pointColor.equals(pointsTab[i+1][j-1].pointColor))
                    tempTab[i][j].state =1;
                else if(!pointsTab[i][j].pointColor.equals(pointsTab[i+1][j].pointColor))
                    tempTab[i][j].state =1;
                else if(!pointsTab[i][j].pointColor.equals(pointsTab[i+1][j+1].pointColor))
                    tempTab[i][j].state =1;
                else if(!pointsTab[i][j].pointColor.equals(pointsTab[i-1][j-1].pointColor))
                    tempTab[i][j].state =1;
                else if(!pointsTab[i][j].pointColor.equals(pointsTab[i-1][j].pointColor))
                    tempTab[i][j].state =1;
                else if(!pointsTab[i][j].pointColor.equals(pointsTab[i-1][j+1].pointColor))
                    tempTab[i][j].state =1;
            }
        }

        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                pointsTab[i][j].state =(tempTab[i][j].state);
            }
        }

        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                if(pointsTab[i][j].state !=1) {
                    tempTab[i][j].pointColor =(Color.rgb(255,255,255));
                    tempTab[i][j].state =0;

                }
                else
                    tempTab[i][j].pointColor =(Color.rgb(0,0,0));
            }
        }

        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                pointsTab[i][j].pointColor =(tempTab[i][j].pointColor);
                pointsTab[i][j].state =(tempTab[i][j].state);
            }
        }
    }

    void initMC(int numberOfSeeds){
        Random rng=new Random();
        while(listColors.size()<numberOfSeeds) {
            Color newColor = Color.rgb(rng.nextInt(256),rng.nextInt(256),rng.nextInt(256));
            if(!listColors.contains(newColor) && !newColor.equals(Color.rgb(255,255,255)) && !newColor.equals(Color.rgb(0,0,0))){
                listColors.add(newColor);
            }
        }

        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                if(tempTab[i][j].pointColor.equals(Color.rgb(255,255,255))) {
                    tempTab[i][j].pointColor =listColors.get(rng.nextInt(listColors.size()));
                }
            }
        }
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                pointsTab[i][j].pointColor =(tempTab[i][j].pointColor);
            }
        }
    }

    private int calculateEnergy(int x, int y){
        int energy=0;
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                if(i==0 && j==0) continue;
                if(!pointsTab[x+i][y+j].pointColor.equals(pointsTab[x][y].pointColor))
                    energy++;
            }
        }
        return energy;
    }

    void clearBorders(){ //without it MC -> CA sie sypie
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                if(i==0 || j==0 || i==(width-1) || j==(height-1)){
                    pointsTab[i][j].pointColor = Color.rgb(255,255,255);
                    tempTab[i][j].pointColor = Color.rgb(255,255,255);
                    pointsTab[i][j].state = 0;
                    tempTab[i][j].state = 0;
                }
            }
        }
    }

    void iterationMC() { // logika MC
        Random rng = new Random();
        List<PointState> pointsList = new ArrayList<>();
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                if(pointsTab[i][j].state!=2 && !pointsTab[i][j].pointColor.equals(Color.rgb(0,0,0))  )
                    pointsList.add(new PointState(i, j));
            }
        }
        for (PointState point : pointsList) { //przechodzimy po kazdym punkcie z listy punktow
            int x, y;
            do {
                x = rng.nextInt(3) - 1;
                y = rng.nextInt(3) - 1;
            } while (x == 0 || y == 0 || pointsTab[point.x + x][point.y + y].pointColor.equals(Color.rgb(128, 128, 128)));

            int energyOld = calculateEnergy(point.x, point.y);
            Color oldColor = pointsTab[point.x][point.y].pointColor;
            pointsTab[point.x][point.y].pointColor = (pointsTab[point.x + x][point.y + y].pointColor);
            int energyNew = calculateEnergy(point.x, point.y);
            if (energyNew > energyOld) {
                pointsTab[point.x][point.y].pointColor = (oldColor);
            }
        }
    }

    private boolean isOnBorder(int x,int y){
        Color color= pointsTab[x][y].pointColor;
        for(int i=x-1;i<=x+1;i++){
            for(int j=y-1;j<=y+1;j++){
                if(i!=0 && j!=0 && i!=width-1 && j!=height-1) {
                    if (!pointsTab[i][j].pointColor.equals(color)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void countEnergy(int min, int max){
        for(int i=1;i<width-1;i++) {
            for (int j = 1; j < height - 1; j++) {
                if(isOnBorder(i,j)) {
                    pointsTab[i][j].h=(max);
                }
                else {
                    pointsTab[i][j].h = (min);
                }
            }
        }
    }
}