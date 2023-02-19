package ArraySortVisualizer;

import com.FXChemiEngine.engine.Shape;
import com.FXChemiEngine.engine.shape.Rectangle;
import com.FXChemiEngine.util.Color;

public class SortItem<T extends Shape> implements Comparable<SortItem<T>>{
    private T body;
    private int value;

    public SortItem(T body, int value){
        this.body = body;
        this.body.setStrokeColor(body.color);
        this.value = value;
    }

    public static void swap(SortItem<?> v1, SortItem<?> v2){
        float tempX = v1.body.position.x;
        v1.body.position.x = v2.body.position.x;
        v2.body.position.x = tempX;
    }

    public static void assign(SortItem<?> v1, SortItem<?> v2){
        v1.body.position.x = v2.body.position.x;
    }

    public static float save(SortItem<?> v1){
        return v1.body.position.x;
    }

    public T getBody() {
        return body;
    }

    @Override
    public int compareTo(SortItem<T> v) {
        if(this.value > v.value)
            return 1;
        else if(this.value < v.value)
            return -1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return "SortItem{" +
                "value=" + value +
                "Pos=" + body.position.x +
                '}';
    }

    public int getValue() {
        return value;
    }
}
