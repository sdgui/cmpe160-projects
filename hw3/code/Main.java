//Name: Ahmet Mete Atay
//Student Number: 2023400240

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class Main {
    private static int totalSteps=0;
    private static double totalCost=0;
    private static String output="";
    public static void main(String[] args) throws FileNotFoundException {
        boolean drawFlag=false;
        String mapDataFileName;
        String travelCostsFileName;
        String objectivesFileName;

        if (args[0].equals("-draw")){
            drawFlag=true;
            mapDataFileName=args[1];
            travelCostsFileName=args[2];
            objectivesFileName =args[3];
        }
        else{
            mapDataFileName=args[0];
            travelCostsFileName=args[1];
            objectivesFileName =args[2];
        }
        File mapFile = new File(mapDataFileName);
        File travelCostsFile = new File(travelCostsFileName);
        File objectivesFile = new File(objectivesFileName);
        Scanner mapScanner = new Scanner(mapFile);
        int col=mapScanner.nextInt();
        int row=mapScanner.nextInt();

        Tile[][] tileGrid = new Tile[col][row];
        for (int i=0; i<col; i++){
            for (int j=0; j<row; j++){
                //stores tiles according to their x and y coordinates in grid, this makes it easier to detect neighbor tiles.
                int a=mapScanner.nextInt();
                int b=mapScanner.nextInt();
                tileGrid[a][b] = new Tile(a, b, mapScanner.nextInt(),row);
            }
        }
        //adds the neighbor tiles for each tile
        for(int i=0;i<col;i++){
            for (int j=0; j<row; j++){
                if (i>0)
                    tileGrid[i][j].addNeighbor(tileGrid[i-1][j]);
                if (j>0)
                    tileGrid[i][j].addNeighbor(tileGrid[i][j-1]);
                if (j<row-1)
                    tileGrid[i][j].addNeighbor(tileGrid[i][j+1]);
                if (i<col-1 )
                    tileGrid[i][j].addNeighbor(tileGrid[i+1][j]);
            }
        }
        mapScanner.close();
        Scanner objectiveScanner = new Scanner(objectivesFile);
        int[] knightCoords={objectiveScanner.nextInt(),objectiveScanner.nextInt()};
        //creates an arraylist of objectives in order to access them later
        ArrayList<int[]> objectives=new ArrayList<>();
        while (objectiveScanner.hasNextInt()){
            objectives.add(new int[]{objectiveScanner.nextInt(),objectiveScanner.nextInt()});
        }
        objectiveScanner.close();
        Scanner travelCostsScanner = new Scanner(travelCostsFile);
        //I stored the travel costs between tiles in a hashmap for easier access
        Map<ArrayPair, Double> travelCosts=new HashMap<>();
        while (travelCostsScanner.hasNextInt()){
            int[] coord1={travelCostsScanner.nextInt(),travelCostsScanner.nextInt()};
            int[] coord2={travelCostsScanner.nextInt(),travelCostsScanner.nextInt()};
            ArrayPair pair1=new ArrayPair(coord1,coord2);
            ArrayPair pair2=new ArrayPair(coord2,coord1);
            double cost=travelCostsScanner.nextDouble();
            //puts the coordinates in both ways to make access possible from both ways
            travelCosts.put(pair1,cost);
            travelCosts.put(pair2,cost);
        }
        //only draws if -draw is in args
        if (drawFlag) {
            StdDraw.enableDoubleBuffering();
            if (col<row){
                StdDraw.setCanvasSize(750*col/row,750);
            }
            else{
                StdDraw.setCanvasSize(750,750*row/col);
            }
            StdDraw.setXscale(0, col);
            StdDraw.setYscale(0, row);
            StdDraw.clear();
        }
        for (int i=0;i<objectives.size();i++){
            PathFinder pathFinder= new PathFinder(tileGrid[objectives.get(i)[0]][objectives.get(i)[1]],tileGrid,tileGrid[knightCoords[0]][knightCoords[1]],travelCosts,drawFlag,objectives,i+1,output,totalSteps,totalCost,row);
            knightCoords=pathFinder.calculateShortestPath();
            output=pathFinder.getOutput();
            totalCost=pathFinder.getTotalCost();
            totalSteps=pathFinder.getTotalSteps();
        }
        output+=String.format("Total Step: %d, Total Cost: %.2f\n",totalSteps,totalCost);
        System.out.printf("Total Step: %d, Total Cost: %.2f\n",totalSteps,totalCost);
        //creates and writes to output file
        File outputFile = new File("out/output.txt");
        try {
            FileWriter writer = new FileWriter("out/output.txt");
            writer.write(output);
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Error writing to file");
        }


    }



}