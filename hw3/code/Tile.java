//Name: Ahmet Mete Atay
//Student Number: 2023400240


import java.util.ArrayList;
import java.util.Map;

public class Tile {
    private int x;
    private int y;
    private int type;
    private String image;
    private ArrayList<Tile> neighbors= new ArrayList<>();
    private double distance=Double.MAX_VALUE;
    private int row;
    Tile(){
    }
    Tile(int x, int y,int type,int row){
        this.x = x;
        this.y = y;
        this.type=type;
        this.row=row;
        image= switch (type) {
            case 0 -> "misc/grassTile.jpeg";
            case 1 -> "misc/sandTile.png";
            case 2 -> "misc/impassableTile.jpeg";
            default -> "misc/impassableTile.jpeg";
            };
        }
    //draws tile
    public void draw(){
        StdDraw.picture(x+0.5,row-0.5-y,image,1,1);
    }
    public void addNeighbor(Tile neighbor){
        neighbors.add(neighbor);
    }

    /**
     * Updates each neighbor's minimum distance to the objective
     * @param travelCosts stores travel costs between each tile and its neighbor
     */
    public void updateNeighborDistance(Map<ArrayPair,Double> travelCosts){
        if(type==2){
            return;
        }
        for (Tile neighbor : neighbors) {
            if (neighbor.getType()==2){
                continue;
            }
            ArrayPair pair=new ArrayPair(new int[]{x,y},new int[]{neighbor.getX(),neighbor.getY()});
            double travelCost=Double.MAX_VALUE;
            try {
                travelCost = travelCosts.get(pair);
            }
            catch (Exception e){
                continue;
            }
            if (neighbor.distance>distance+travelCost){
                neighbor.setDistance(distance+travelCost);
            }
        }
    }
    public int getType(){
        return type;
    }
    public double getDistance(){
        return distance;
    }
    public void setDistance(double distance){
        this.distance=distance;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    /**
     * finds the next tile to travel for lowest total cost
     * the next tile is the tile whose distance to objective + travel cost from current tile is smallest
     * @param travelCosts hashMap storing travel costs between each tile
     * @return next tile
     */
    public Tile findNextTile(Map<ArrayPair,Double> travelCosts){
        Tile closestNeighbor=null;
        for (Tile neighbor : neighbors) {
            ArrayPair pair=new ArrayPair(new int[]{x,y},new int[]{neighbor.getX(),neighbor.getY()});
            try{
                if (neighbor.getDistance()+travelCosts.get(pair)==distance) {
                    closestNeighbor=neighbor;
                    return closestNeighbor;
                }
            }
            catch (Exception e){
                continue;
            }
        }
        return closestNeighbor;
    }


}




