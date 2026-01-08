//Name: Ahmet Mete Atay
//Student Number: 2023400240

import java.util.Arrays;

public class ArrayPair {
    private final int[] pos1,pos2;

    ArrayPair(int[] pos1, int[] pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }
    public int[] getPos1() {
        return pos1;
    }
    public int[] getPos2() {
        return pos2;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayPair that = (ArrayPair) o;
        return Arrays.equals(pos1,that.pos1) && Arrays.equals(pos2,that.pos2);
    }
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(pos1);
        result = 31 * result + Arrays.hashCode(pos2);
        return result;
    }


}
