//Name: Ahmet Mete Atay
//Student Number: 2023400240

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


// Assume Tile.java and ArrayPair.java are in the same directory or package
// Assume StdDraw.jar is in the classpath

// --- Utility class for Dijkstra's Algorithm ---
class DijkstraUtil {
    public static class PathData {
        double cost;
        List<Tile> tileSequence;
        int steps;

        PathData(double cost, List<Tile> tileSequence) {
            this.cost = cost;
            this.tileSequence = tileSequence;
            this.steps = tileSequence != null ? Math.max(0, tileSequence.size() - 1) : 0;
        }
    }

    public static PathData findShortestPath(Tile startTile, Tile endTile, Tile[][] grid,
                                            Map<ArrayPair, Double> travelCosts, Collection<Tile> allMapTilesInGrid) {
        for (Tile t : allMapTilesInGrid) {
            t.setDistance(Double.MAX_VALUE);
        }
        startTile.setDistance(0);

        PriorityQueue<Tile> unsettledNodes = new PriorityQueue<>(Comparator.comparingDouble(Tile::getDistance));
        unsettledNodes.add(startTile);

        Map<Tile, Tile> predecessors = new HashMap<>();
        boolean foundTarget = false;

        int numGridCols = grid.length;
        int numGridRows = (grid.length > 0) ? grid[0].length : 0;

        while (!unsettledNodes.isEmpty()) {
            Tile currentTile = unsettledNodes.poll();

            if (currentTile.getDistance() == Double.MAX_VALUE) {
                break;
            }

            if (currentTile.equals(endTile)) {
                foundTarget = true;
            }
            if (foundTarget && currentTile.equals(endTile)) {
                break;
            }


            if (currentTile.getType() == 2) continue;

            List<Tile> currentNeighbors = new ArrayList<>();
            int cx = currentTile.getX();
            int cy = currentTile.getY();

            int[] dx = {0, 0, 1, -1};
            int[] dy = {1, -1, 0, 0};

            for (int i = 0; i < 4; i++) {
                int nx = cx + dx[i];
                int ny = cy + dy[i];

                if (nx >= 0 && nx < numGridCols && ny >= 0 && ny < numGridRows && grid[nx][ny] != null) {
                    currentNeighbors.add(grid[nx][ny]);
                }
            }

            for (Tile neighbor : currentNeighbors) {
                if (neighbor.getType() == 2) continue;

                ArrayPair pairKey = new ArrayPair(new int[]{currentTile.getX(), currentTile.getY()},
                        new int[]{neighbor.getX(), neighbor.getY()});
                Double costToNeighbor = travelCosts.get(pairKey);

                if (costToNeighbor == null) continue;

                if (currentTile.getDistance() + costToNeighbor < neighbor.getDistance()) {
                    neighbor.setDistance(currentTile.getDistance() + costToNeighbor);
                    predecessors.put(neighbor, currentTile);
                    unsettledNodes.remove(neighbor);
                    unsettledNodes.add(neighbor);
                }
            }
        }

        if (!foundTarget || endTile.getDistance() == Double.MAX_VALUE) {
            return new PathData(Double.MAX_VALUE, Collections.emptyList());
        }

        LinkedList<Tile> path = new LinkedList<>();
        Tile step = endTile;
        while (step != null) {
            path.addFirst(step);
            step = predecessors.get(step);
        }
        return new PathData(endTile.getDistance(), path);
    }
}

class ShortestRouteSolver {
    private Tile[][] tileGrid;
    private Map<ArrayPair, Double> travelCostsMap;
    private Tile startKnightTileActual;
    private List<Tile> goldCoinTilesActual; // Original list of all gold coins from input

    private boolean drawFlag;
    private int mapRows, mapCols;

    // For TSP:
    private List<Tile> allPointsOfInterest; // POI: startKnightTileActual + original goldCoinTilesActual
    private int numOriginalGoldCoins;       // Number of coins in goldCoinTilesActual
    private int numPois;                    // Total original POIs = numOriginalGoldCoins + 1

    private DijkstraUtil.PathData[][] pairwisePathData; // [orig_poi_idx_from][orig_poi_idx_to]

    // DP table for TSP: dp[last_visited_filtered_coin_idx][mask_of_visited_filtered_coins]
    private double[][] tspDpTableCosts;
    private int[][] tspDpTableNextChoice;

    private StringBuilder outputBuilder;
    private int overallTotalStepsInTour;
    private double overallTotalCostOfTour;
    private Map<Tile, Integer> goldCoinToOriginalIndexMap; // Maps coin Tile to its 1-based objective number from input
    private List<Tile> allMapTilesInGrid;
    private Set<Tile> allTilesVisitedInTour;

    // New members for handling filtered coins in TSP
    private List<Tile> filteredGoldCoins; // Gold coins that are deemed visitable for TSP
    private List<Integer> originalPoiIndicesOfFilteredCoins; // Maps index_in_filteredGoldCoins to original_POI_index
    private Map<Tile, Integer> tileToOriginalPoiIndexMap;   // Maps any POI Tile to its original POI index (0 for start)

    public ShortestRouteSolver(Tile[][] tileGrid, Map<ArrayPair, Double> travelCostsMap,
                               Tile startKnightTile, List<Tile> goldCoinTilesListInput,
                               boolean drawFlag, int mapRows, int mapCols, List<Tile> allMapTilesInGrid) {
        this.tileGrid = tileGrid;
        this.travelCostsMap = travelCostsMap;
        this.startKnightTileActual = startKnightTile;
        this.goldCoinTilesActual = new ArrayList<>(goldCoinTilesListInput); // Store original list
        this.allMapTilesInGrid = allMapTilesInGrid;

        this.drawFlag = drawFlag;
        this.mapRows = mapRows;
        this.mapCols = mapCols;

        this.numOriginalGoldCoins = this.goldCoinTilesActual.size();

        // Initialize map for 1-based objective numbering (for output messages)
        this.goldCoinToOriginalIndexMap = new HashMap<>();
        for (int i = 0; i < this.goldCoinTilesActual.size(); i++) {
            this.goldCoinToOriginalIndexMap.put(this.goldCoinTilesActual.get(i), i + 1);
        }

        // Initialize tileToOriginalPoiIndexMap (maps Tile object to its 0-based POI index in allPointsOfInterest)
        this.tileToOriginalPoiIndexMap = new HashMap<>();
        if (this.startKnightTileActual != null) {
            this.tileToOriginalPoiIndexMap.put(this.startKnightTileActual, 0);
        }
        for (int i = 0; i < this.goldCoinTilesActual.size(); i++) {
            this.tileToOriginalPoiIndexMap.put(this.goldCoinTilesActual.get(i), i + 1); // Coin POI indices are 1-based
        }

        // Build allPointsOfInterest using the original start and all original coins
        this.allPointsOfInterest = new ArrayList<>();
        this.allPointsOfInterest.add(this.startKnightTileActual);
        this.allPointsOfInterest.addAll(this.goldCoinTilesActual);
        this.numPois = this.allPointsOfInterest.size();

        this.outputBuilder = new StringBuilder();
        this.overallTotalStepsInTour = 0;
        this.overallTotalCostOfTour = 0.0;
        this.allTilesVisitedInTour = new HashSet<>();
        if (startKnightTileActual != null) {
            this.allTilesVisitedInTour.add(startKnightTileActual);
        }
    }

    private void precomputeAllPairwisePaths() {
        pairwisePathData = new DijkstraUtil.PathData[numPois][numPois];
        for (int i = 0; i < numPois; i++) {
            for (int j = 0; j < numPois; j++) {
                if (i == j) {
                    pairwisePathData[i][j] = new DijkstraUtil.PathData(0, Arrays.asList(allPointsOfInterest.get(i)));
                } else {
                    pairwisePathData[i][j] = DijkstraUtil.findShortestPath(
                            allPointsOfInterest.get(i), allPointsOfInterest.get(j),
                            tileGrid, travelCostsMap, allMapTilesInGrid);
                }
            }
        }
    }

    private double solveTspRecursive(int lastVisitedFilteredCoinIndex, int visitedMask) {
        int numFilteredCoinsForTsp = this.filteredGoldCoins.size();

        if (visitedMask == (1 << numFilteredCoinsForTsp) - 1) { // All filtered coins visited
            int lastCoinOriginalPoiIndex = this.originalPoiIndicesOfFilteredCoins.get(lastVisitedFilteredCoinIndex);
            return pairwisePathData[lastCoinOriginalPoiIndex][0].cost; // Cost to return to actual start (POI 0)
        }

        if (tspDpTableCosts[lastVisitedFilteredCoinIndex][visitedMask] != -1.0) {
            return tspDpTableCosts[lastVisitedFilteredCoinIndex][visitedMask];
        }

        double minCostToFinish = Double.MAX_VALUE;
        int lastCoinOriginalPoiIndex = this.originalPoiIndicesOfFilteredCoins.get(lastVisitedFilteredCoinIndex);

        for (int nextFilteredCoinToVisitIndex = 0; nextFilteredCoinToVisitIndex < numFilteredCoinsForTsp; nextFilteredCoinToVisitIndex++) {
            if ((visitedMask & (1 << nextFilteredCoinToVisitIndex)) == 0) { // If not yet visited
                int nextCoinOriginalPoiIndex = this.originalPoiIndicesOfFilteredCoins.get(nextFilteredCoinToVisitIndex);

                double costFromLastToNext = pairwisePathData[lastCoinOriginalPoiIndex][nextCoinOriginalPoiIndex].cost;
                if (costFromLastToNext == Double.MAX_VALUE) continue;

                double costAfterVisitingNext = solveTspRecursive(nextFilteredCoinToVisitIndex, visitedMask | (1 << nextFilteredCoinToVisitIndex));
                if (costAfterVisitingNext == Double.MAX_VALUE) continue;

                double currentPathTotalCost = costFromLastToNext + costAfterVisitingNext;
                if (currentPathTotalCost < minCostToFinish) {
                    minCostToFinish = currentPathTotalCost;
                    tspDpTableNextChoice[lastVisitedFilteredCoinIndex][visitedMask] = nextFilteredCoinToVisitIndex;
                }
            }
        }
        return tspDpTableCosts[lastVisitedFilteredCoinIndex][visitedMask] = minCostToFinish;
    }

    public String findOptimalTourAndBuildOutput() {
        if (this.goldCoinTilesActual.isEmpty()) { // No coins given initially
            outputBuilder.append(String.format("Total Step: %d, Total Cost: %.2f\n", 0, 0.0));
            return outputBuilder.toString();
        }

        precomputeAllPairwisePaths(); // Based on original start + all original coins

        // Filter gold coins: only include those reachable from start AND can return to start
        this.filteredGoldCoins = new ArrayList<>();
        this.originalPoiIndicesOfFilteredCoins = new ArrayList<>();

        for (Tile coin : this.goldCoinTilesActual) {
            int originalCoinPoiIndex = this.tileToOriginalPoiIndexMap.get(coin); // Get its 0-based POI index (1 to N)

            if (pairwisePathData[0][originalCoinPoiIndex].cost != Double.MAX_VALUE &&
                    pairwisePathData[originalCoinPoiIndex][0].cost != Double.MAX_VALUE) {
                this.filteredGoldCoins.add(coin);
                this.originalPoiIndicesOfFilteredCoins.add(originalCoinPoiIndex);
            } else {
                // Optionally log: System.out.println("Skipping coin: " + coin.getX() + "," + coin.getY());
            }
        }

        int numFilteredCoinsForTsp = this.filteredGoldCoins.size();

        if (numFilteredCoinsForTsp == 0) {
            outputBuilder.append("No reachable objectives found to form a tour.\n");
            outputBuilder.append(String.format("Total Step: %d, Total Cost: %.2f\n", 0, 0.0));
            return outputBuilder.toString();
        }

        // Initialize DP tables for the filtered set of coins
        tspDpTableCosts = new double[numFilteredCoinsForTsp][1 << numFilteredCoinsForTsp];
        tspDpTableNextChoice = new int[numFilteredCoinsForTsp][1 << numFilteredCoinsForTsp];
        for (int i = 0; i < numFilteredCoinsForTsp; i++) {
            Arrays.fill(tspDpTableCosts[i], -1.0);
            Arrays.fill(tspDpTableNextChoice[i], -1);
        }

        double minTourCost = Double.MAX_VALUE;
        int bestFirstFilteredCoinIndex = -1; // Index in filteredGoldCoins

        // Determine the best *first* filtered gold coin to visit from startKnightActual
        for (int firstFilteredCoinIdx = 0; firstFilteredCoinIdx < numFilteredCoinsForTsp; firstFilteredCoinIdx++) {
            int firstCoinOriginalPoiIndex = this.originalPoiIndicesOfFilteredCoins.get(firstFilteredCoinIdx);
            double costFromStartToFirstCoin = pairwisePathData[0][firstCoinOriginalPoiIndex].cost;

            if (costFromStartToFirstCoin == Double.MAX_VALUE) continue; // Should be caught by filter, but good check

            double costOfRemainingTour = solveTspRecursive(firstFilteredCoinIdx, (1 << firstFilteredCoinIdx));
            if (costOfRemainingTour == Double.MAX_VALUE) continue;

            double currentTotalTourCost = costFromStartToFirstCoin + costOfRemainingTour;
            if (currentTotalTourCost < minTourCost) {
                minTourCost = currentTotalTourCost;
                bestFirstFilteredCoinIndex = firstFilteredCoinIdx;
            }
        }

        if (bestFirstFilteredCoinIndex == -1) {
            outputBuilder.append("No route found to connect all (reachable) coins and return to start.\n");
            outputBuilder.append(String.format("Total Step: %d, Total Cost: %.2f\n", 0, 0.0));
            return outputBuilder.toString();
        }

        // Reconstruct the optimal tour path (sequence of POI Tiles) using filtered coins
        List<Tile> optimalPoiSequenceForward = new ArrayList<>();
        optimalPoiSequenceForward.add(startKnightTileActual);

        int currentFilteredGoldCoinIdx = bestFirstFilteredCoinIndex;
        int visitedMask = (1 << currentFilteredGoldCoinIdx);
        optimalPoiSequenceForward.add(this.filteredGoldCoins.get(currentFilteredGoldCoinIdx));

        for (int i = 0; i < numFilteredCoinsForTsp - 1; i++) {
            int nextFilteredGoldCoinIdx = tspDpTableNextChoice[currentFilteredGoldCoinIdx][visitedMask];
            if (nextFilteredGoldCoinIdx == -1) {
                System.err.println("Error reconstructing TSP path: next choice is -1.");
                outputBuilder.append("Error in TSP path reconstruction.\n"); // Or a more user-friendly message
                outputBuilder.append(String.format("Total Step: %d, Total Cost: %.2f\n", overallTotalStepsInTour, overallTotalCostOfTour)); // partial?
                return outputBuilder.toString();
            }
            optimalPoiSequenceForward.add(this.filteredGoldCoins.get(nextFilteredGoldCoinIdx));
            currentFilteredGoldCoinIdx = nextFilteredGoldCoinIdx;
            visitedMask |= (1 << currentFilteredGoldCoinIdx);
        }
        optimalPoiSequenceForward.add(startKnightTileActual);

        // Reverse order of visiting objectives as per previous logic
        List<Tile> optimalPoiSequenceReversed = new ArrayList<>();
        optimalPoiSequenceReversed.add(startKnightTileActual);
        if (optimalPoiSequenceForward.size() > 2) {
            List<Tile> coinsInForwardOrder = optimalPoiSequenceForward.subList(1, optimalPoiSequenceForward.size() - 1);
            List<Tile> coinsInReversedOrder = new ArrayList<>(coinsInForwardOrder);
            Collections.reverse(coinsInReversedOrder);
            optimalPoiSequenceReversed.addAll(coinsInReversedOrder);
        }
        optimalPoiSequenceReversed.add(startKnightTileActual);
        List<Tile> optimalPoiSequenceToUse = optimalPoiSequenceReversed;


        if (drawFlag) {
            allTilesVisitedInTour.clear();
            allTilesVisitedInTour.add(startKnightTileActual);
            drawCurrentStateWithAllVisitedDots(startKnightTileActual);
            StdDraw.pause(200);
        }

        Set<Tile> objectivesReportedAsReached = new HashSet<>();

        for (int i = 0; i < optimalPoiSequenceToUse.size() - 1; i++) {
            Tile segmentStartPoi = optimalPoiSequenceToUse.get(i);
            Tile segmentEndPoi = optimalPoiSequenceToUse.get(i + 1);

            // Get original POI indices for these Tiles to index into pairwisePathData
            int segmentStartOriginalPoiIndex = this.tileToOriginalPoiIndexMap.get(segmentStartPoi);
            int segmentEndOriginalPoiIndex = this.tileToOriginalPoiIndexMap.get(segmentEndPoi);

            DijkstraUtil.PathData segmentPath = pairwisePathData[segmentStartOriginalPoiIndex][segmentEndOriginalPoiIndex];

            int originalObjectiveIndexOfSegmentEnd = -1; // 1-based objective number for message
            boolean isFinalReturnToStartTile = (i == optimalPoiSequenceToUse.size() - 2);

            // goldCoinToOriginalIndexMap maps Tile to its 1-based input objective number
            if (goldCoinToOriginalIndexMap.containsKey(segmentEndPoi)) {
                if (!objectivesReportedAsReached.contains(segmentEndPoi)) {
                    originalObjectiveIndexOfSegmentEnd = goldCoinToOriginalIndexMap.get(segmentEndPoi);
                    objectivesReportedAsReached.add(segmentEndPoi);
                }
            }

            processSegmentToOutput(segmentPath, originalObjectiveIndexOfSegmentEnd);
        }

        outputBuilder.append(String.format("Total Step: %d, Total Cost: %.2f\n", overallTotalStepsInTour, overallTotalCostOfTour));
        return outputBuilder.toString();
    }

    private void processSegmentToOutput(DijkstraUtil.PathData segmentPath, int objectiveNumberForMsg) {
        List<Tile> pathTilesInSegment = segmentPath.tileSequence;

        if (pathTilesInSegment == null || pathTilesInSegment.size() <= 1) {
            if (pathTilesInSegment != null && !pathTilesInSegment.isEmpty()) {
                allTilesVisitedInTour.add(pathTilesInSegment.get(0));
            }
            if (objectiveNumberForMsg != -1) {
                outputBuilder.append(String.format("Objective %d reached!\n", objectiveNumberForMsg));
            }
            if (drawFlag && pathTilesInSegment != null && !pathTilesInSegment.isEmpty()) {
                drawCurrentStateWithAllVisitedDots(pathTilesInSegment.get(0));
            }
            return;
        }

        allTilesVisitedInTour.add(pathTilesInSegment.get(0));

        for (int k = 0; k < pathTilesInSegment.size() - 1; k++) {
            Tile stepFromTile = pathTilesInSegment.get(k);
            Tile stepToTile = pathTilesInSegment.get(k + 1);

            allTilesVisitedInTour.add(stepToTile);

            ArrayPair pairKey = new ArrayPair(new int[]{stepFromTile.getX(), stepFromTile.getY()},
                    new int[]{stepToTile.getX(), stepToTile.getY()});
            Double costOfThisSingleStep = travelCostsMap.get(pairKey);

            if (costOfThisSingleStep == null) {
                System.err.printf("Error: Missing travel cost during segment processing between (%d,%d) and (%d,%d)\n",
                        stepFromTile.getX(), stepFromTile.getY(), stepToTile.getX(), stepToTile.getY());
                continue;
            }

            overallTotalStepsInTour++;
            overallTotalCostOfTour += costOfThisSingleStep;

            outputBuilder.append(String.format("Step Count: %d, move to (%d, %d). Total Cost: %.2f.\n",
                    overallTotalStepsInTour, stepToTile.getX(), stepToTile.getY(), overallTotalCostOfTour));

            if (drawFlag) {
                drawCurrentStateWithAllVisitedDots(stepToTile);
                StdDraw.pause(50);
            }
        }

        if (objectiveNumberForMsg != -1) {
            outputBuilder.append(String.format("Objective %d reached!\n", objectiveNumberForMsg));
        }
    }
    private void drawCurrentStateWithAllVisitedDots(Tile currentKnightTile) {
        if (!drawFlag) return;

        StdDraw.clear();

        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                if (tileGrid[i][j] != null) tileGrid[i][j].draw();
            }
        }

        StdDraw.setPenColor(StdDraw.RED);
        for (Tile visitedTile : allTilesVisitedInTour) {
            StdDraw.filledCircle(0.5 + visitedTile.getX(), mapRows - 0.5 - visitedTile.getY(), 0.2);
        }

        // Draw all originally specified gold coins, even if some are skipped by TSP
        // (Visual distinction for skipped/collected could be added if needed)
        for (Tile coin : this.goldCoinTilesActual) { // Iterate original list for drawing
            StdDraw.picture(0.5 + coin.getX(), mapRows - 0.5 - coin.getY(), "misc/coin.png", 1, 1);
        }

        if (currentKnightTile != null) {
            StdDraw.picture(0.5 + currentKnightTile.getX(), mapRows - 0.5 - currentKnightTile.getY(), "misc/knight.png", 1, 1);
        }

        StdDraw.show();
    }
}


// --- Main class for Bonus ---
public class Bonus {
    public static void main(String[] args) {
        boolean drawFlag = false;
        String mapDataFileName = null, travelCostsFileName = null, objectivesFileName = null;

        int argOffset = 0;
        if (args.length > 0 && args[0].equals("-draw")) {
            drawFlag = true;
            argOffset = 1;
        }

        if (args.length < 3 + argOffset) {
            System.err.println("Usage: java Bonus [-draw] <mapData.txt> <travelCosts.txt> <objectives.txt>");
            return;
        }
        mapDataFileName = args[argOffset];
        travelCostsFileName = args[argOffset + 1];
        objectivesFileName = args[argOffset + 2];

        try {
            Scanner mapScanner = new Scanner(new File(mapDataFileName));
            int mapCols = mapScanner.nextInt();
            int mapRows = mapScanner.nextInt();
            Tile[][] tileGrid = new Tile[mapCols][mapRows];
            List<Tile> allMapTilesForReset = new ArrayList<>();

            // Ensure all tiles are created and added to allMapTilesForReset
            for(int r = 0; r < mapRows; r++) { // Iterate by standard row/col to ensure all grid cells considered
                for (int c = 0; c < mapCols; c++) {
                    tileGrid[c][r] = null; // Initialize to null or handle if mapData is sparse
                }
            }

            mapScanner.nextLine(); // Consume rest of the first line

            while(mapScanner.hasNextLine()){ // Read tile by tile
                String line = mapScanner.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length < 3) continue; // Basic check

                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int type = Integer.parseInt(parts[2]);

                if (x >= 0 && x < mapCols && y >= 0 && y < mapRows) {
                    tileGrid[x][y] = new Tile(x, y, type, mapRows);
                    allMapTilesForReset.add(tileGrid[x][y]);
                } else {
                    System.err.println("Warning: Tile coordinate out of bounds: " + x + "," + y);
                }
            }
            mapScanner.close();

            // Fill any remaining null grid cells with a default (e.g., impassable) or ensure mapData is complete
            for(int r = 0; r < mapRows; r++) {
                for (int c = 0; c < mapCols; c++) {
                    if (tileGrid[c][r] == null) {
                        // This case implies sparse mapData or an issue.
                        // For robustness, could create a default tile, e.g., impassable
                        // tileGrid[c][r] = new Tile(c, r, 2, mapRows); // Type 2: Impassable
                        // allMapTilesForReset.add(tileGrid[c][r]);
                        // Or, assume mapData.txt is always complete for all x,y up to mapCols, mapRows
                        // If the problem guarantees mapData.txt defines all tiles, this loop is for safety/debug
                    }
                }
            }


            Scanner travelCostsScanner = new Scanner(new File(travelCostsFileName));
            Map<ArrayPair, Double> travelCostsMap = new HashMap<>();
            while (travelCostsScanner.hasNextInt()) {
                int[] coord1 = {travelCostsScanner.nextInt(), travelCostsScanner.nextInt()};
                int[] coord2 = {travelCostsScanner.nextInt(), travelCostsScanner.nextInt()};
                double cost = travelCostsScanner.nextDouble();
                travelCostsMap.put(new ArrayPair(coord1, coord2), cost);
                travelCostsMap.put(new ArrayPair(coord2, coord1), cost);
            }
            travelCostsScanner.close();

            Scanner objectivesScanner = new Scanner(new File(objectivesFileName));
            Tile startKnightTile = null;
            if (objectivesScanner.hasNextInt()) {
                int startX = objectivesScanner.nextInt();
                int startY = objectivesScanner.nextInt();
                if (startX >= 0 && startX < mapCols && startY >= 0 && startY < mapRows && tileGrid[startX][startY] != null) {
                    startKnightTile = tileGrid[startX][startY];
                } else {
                    System.err.println("Error: Start knight tile coordinates invalid or tile not defined: " + startX + "," + startY);
                    return;
                }
            } else {
                System.err.println("Error: Objectives file is empty or missing start coordinates.");
                return;
            }

            List<Tile> goldCoinTilesList = new ArrayList<>();
            while (objectivesScanner.hasNextInt()) {
                int coinX = objectivesScanner.nextInt();
                int coinY = objectivesScanner.nextInt();
                if (coinX >= 0 && coinX < mapCols && coinY >= 0 && coinY < mapRows && tileGrid[coinX][coinY] != null) {
                    goldCoinTilesList.add(tileGrid[coinX][coinY]);
                } else {
                    System.err.println("Warning: Gold coin tile coordinates invalid or tile not defined: " + coinX + "," + coinY + ". Skipping this coin.");
                }
            }
            objectivesScanner.close();

            if (drawFlag) {
                StdDraw.enableDoubleBuffering();
                if (mapCols == 0 || mapRows == 0) { // Prevent division by zero if map dimensions are invalid
                    System.err.println("Error: Map dimensions are zero, cannot set StdDraw scale.");
                } else {
                    final int CANVAS_BASE_SIZE = 750;
                    if (mapCols < mapRows) {
                        StdDraw.setCanvasSize(Math.max(1, CANVAS_BASE_SIZE * mapCols / mapRows), CANVAS_BASE_SIZE);
                    } else {
                        StdDraw.setCanvasSize(CANVAS_BASE_SIZE, Math.max(1, CANVAS_BASE_SIZE * mapRows / mapCols));
                    }
                    StdDraw.setXscale(0, mapCols);
                    StdDraw.setYscale(0, mapRows);
                }
            }

            ShortestRouteSolver solver = new ShortestRouteSolver(tileGrid, travelCostsMap,
                    startKnightTile, goldCoinTilesList, drawFlag, mapRows, mapCols, allMapTilesForReset);
            String outputContent = solver.findOptimalTourAndBuildOutput();

            File outDir = new File("out");
            if (!outDir.exists()) outDir.mkdirs();
            try (FileWriter writer = new FileWriter("out/bonus.txt")) {
                writer.write(outputContent);
            }
            System.out.print(outputContent); // Also print to console for verification

        } catch (FileNotFoundException e) {
            System.err.println("Error: Input file not found. " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error writing output file. " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}