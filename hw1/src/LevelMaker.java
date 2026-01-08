public class LevelMaker {
    public static void main(String[] args) {
        int scaleX=800;
        int scaleY=400;
        int halfWidth=36;
        int halfHeight=8;
        int top=348;
        int x;
        int y;
        int[] rowSize={1,3,5,7,5,3,1};
        for (int i=0;i<rowSize.length;i++) {
            for (int j=0;j<rowSize[i];j++) {
                x=(scaleX/2-(2*j-rowSize[i]+1)*halfWidth);
                y=top-i*halfHeight*2;
                System.out.print("{"+x+","+y+"},");
            }
            System.out.println();
        }

    }
}
