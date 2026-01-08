//Name: Ahmet Mete Atay
//Student Number: 2023400240


import java.util.*;

public class PathFinder {
    private Tile source;
    private Tile[][] tileGrid;
    private Tile knightTile;
    private Map<ArrayPair,Double> travelCosts ;
    private int objectiveNumber;
    private int row;
    private ArrayList<int[]>objectives;
    private boolean drawFlag;
    private String output;
    private int totalSteps;
    private double totalCost;
    PathFinder(){

    }
    PathFinder(Tile source,Tile[][] tileGrid,Tile knightTile,Map<ArrayPair,Double> travelCosts,boolean drawFlag,ArrayList<int[]>objectives,int objectiveNumber,String output,int totalSteps,double totalCost,int row){
        this.source = source;
        this.tileGrid = tileGrid;
        this.knightTile = knightTile;
        this.travelCosts = travelCosts;
        this.drawFlag = drawFlag;
        this.objectives = objectives;
        this.objectiveNumber = objectiveNumber;
        this.row = row;
        this.output = output;
        this.totalSteps = totalSteps;
        this.totalCost = totalCost;
    }
    /*
     * Calculates the shortest path by using a dijkstra algorithm
     * Calculates the distance of each tile until reaching the player tile
     * row used in order to make the tiles fit e-for every canvas size since y axis is different from that of StdDraw's
     * returns knight's position after the objective is reached
     */
    public int[] calculateShortestPath(){
        source.setDistance(0);
        Set<Tile> settledTiles= new HashSet<>();
        ArrayList<Tile> unsettledTiles= new ArrayList<>();
        for (Tile[] tileLine:tileGrid){
            unsettledTiles.addAll(Arrays.asList(tileLine));
        }
        ArrayList<int[]> path=new ArrayList<>();
        while (!settledTiles.contains(knightTile)){
            Tile currentTile = findClosestTile(unsettledTiles);
            if (currentTile == null){
                output+= String.format("Objective %d cannot be reached!\n",objectiveNumber );
                System.out.printf("Objective %d cannot be reached!\n",objectiveNumber );
                return new int[]{knightTile.getX(),knightTile.getY()};
            }
            currentTile.updateNeighborDistance(travelCosts);
            unsettledTiles.remove(currentTile);
            settledTiles.add(currentTile);
        }
        Tile currentTile = knightTile;

        int stepCount=0;
        double startingDistance= knightTile.getDistance();
        output+=String.format("Starting position: (%d, %d)\n",knightTile.getX(),knightTile.getY());
        System.out.printf("Starting position: (%d, %d)\n",knightTile.getX(),knightTile.getY());
        while (currentTile!=source){
            path.add(new int[]{currentTile.getX(),currentTile.getY()});

            stepCount+=1;
            Tile nextTile=currentTile.findNextTile(travelCosts);
            output +=String.format("Step Count: %d, move to (%d, %d). Total Cost: %.2f.\n",stepCount,nextTile.getX(),nextTile.getY(),startingDistance-nextTile.getDistance() );
            System.out.printf("Step Count: %d, move to (%d, %d). Total Cost: %.2f.\n",stepCount,nextTile.getX(),nextTile.getY(),startingDistance-nextTile.getDistance());
            currentTile=nextTile;
            if (drawFlag) {
                draw( new int[]{currentTile.getX(), currentTile.getY()}, path);
                StdDraw.pause(50);
            }
        }
        totalSteps+=stepCount;
        totalCost+=knightTile.getDistance();
        output+= String.format("Objective %d reached!\n",objectiveNumber );
        System.out.printf("Objective %d reached!\n",objectiveNumber );
        if (drawFlag) {
            //sets objective coordinates to -1,-1 to avoid drawing
            objectives.set(objectiveNumber - 1, new int[]{-1, -1});
            draw( new int[]{currentTile.getX(), currentTile.getY()}, path);
        }
        //resets the distance of each tile after the way is found
        for (Tile[] tileline:tileGrid){
            for (Tile tile:tileline){
                tile.setDistance(Double.MAX_VALUE);
            }
        }
        return new int[]{source.getX(),source.getY()};
    }
    //Finds the closest tile to the objective from given arraylist
    public static Tile findClosestTile(ArrayList<Tile> tiles){
        double minDistance=Double.MAX_VALUE;
        Tile closestTile=null;
        for (Tile tile:tiles){
            if (tile.getDistance()<minDistance){
                minDistance=tile.getDistance();
                closestTile=tile;
            }
        }
        return closestTile;
    }
    //draws everything to screen, called every time when the knight's position is changed
    public void draw(int[] knight,ArrayList<int[]> path){
        StdDraw.clear();
        for (Tile[] tileLine : tileGrid) {
            for (Tile tile : tileLine) {
                tile.draw();
            }
        }
        for (int[] dot:path)
        {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.filledCircle(0.5+dot[0],row-0.5-dot[1],0.2);
        }
        StdDraw.picture(0.5+knight[0],row-0.5-knight[1],"misc/knight.png",1,1);
        for (int[] objective:objectives){
            StdDraw.picture(0.5+objective[0],row-0.5-objective[1],"misc/coin.png",1,1);
        }
        StdDraw.show();
    }
    public String getOutput(){
       return output;
    }
    public int getTotalSteps(){
        return totalSteps;
    }
    public double getTotalCost(){
        return totalCost;
    }

}