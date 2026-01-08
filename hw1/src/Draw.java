public class Draw {
    public static void main(String[] args) {
        StdDraw.setCanvasSize(1600,800);
        StdDraw.setXscale(0, 1600);
        StdDraw.setYscale(0, 800);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.clear();
        StdDraw.filledRectangle(500,650,300,100);
        StdDraw.filledRectangle(1200,650,300,100);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledCircle(500,500,100);
        StdDraw.filledCircle(1200,450,100);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.005);
        StdDraw.line(500,500,500,550);
        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.line(500,500,600,500);
        StdDraw.save("image.png");
    }
}
