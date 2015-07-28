import java.awt.geom.Point2D;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReturnData {      //Data Structure to return the List of the needed Points and the boolarray of the visited Points

    private CopyOnWriteArrayList<Point2D> copyOnWriteArrayList;
    private boolean[][] booleans;

    public ReturnData() {
        copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        booleans = new boolean[200][200];
    }

    public ReturnData(CopyOnWriteArrayList<Point2D> copyOnWriteArrayList, boolean[][] booleans) {
        this.copyOnWriteArrayList = copyOnWriteArrayList;
        this.booleans = booleans;
    }

    public CopyOnWriteArrayList<Point2D> getCopyOnWriteArrayList() {
        return copyOnWriteArrayList;
    }

    public boolean[][] getBooleans() {
        return booleans;
    }

    public int size() {
        return copyOnWriteArrayList.size();
    }
}