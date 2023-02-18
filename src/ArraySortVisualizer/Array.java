package ArraySortVisualizer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Array<T extends Comparable<T>> implements Iterable<T>{
    public int lenght;
    private Object[] array;
    private int cur;
    private int compareCount = 0;
    private int swapCount = 0;
    private BiConsumer<Integer, Integer> swapConsumer;
    private BiConsumer<T, T> assignConsumer;

    private Function<T, Float> saveConsumer;
    public Array(int lenght){
       ini(lenght);
    }

    private void ini(int lenght){
        this.lenght = lenght;
        this.array = new Object[lenght];
        this.cur = 0;
    }

    public T get(int index){
        return (T)array[index];
    }

    public void set(int index, T element){
        array[index] = element;
        cur++;
    }

    public void resize(int lenght){
        ini(lenght);
    }

    public int add(T element){
        array[cur++] = element;
        return cur;
    }

    public void insert(int index, T element){
        array[index] = element;
    }

    public void remove(int index){
        for(int i = index; i < this.lenght-1; i++){
            array[i] = array[i+1];
        }
        array[cur-1] = null;
        cur--;
    }

    @Override
    public String toString() {
        return "Array{" +
                "lenght=" + lenght +
                ", array=" + Arrays.toString(array) +
                ", cur=" + cur +
                '}';
    }

    public T search(T element){
        compareCount = 0;
        swapCount = 0;
        for(int i = 0; i < this.lenght; i++){
            compareCount++;
            if((get(i)).compareTo(element) == 0) {
                return get(i);
            }
        }
        return null;
    }

    public void swap(int i, int j){
        swapCount++;
        T tmp = get(i);
        array[i] = array[j];
        array[j] = tmp;
    }

    public void bubbleSort(){
        compareCount = 0;
        swapCount = 0;
        for(int i = this.lenght-1; i > 0; i--){
            for(int j = 0; j < i; j++){
                compareCount++;
                if((get(i)).compareTo(get(j)) < 0) {
                    if(swapConsumer != null)
                        swapConsumer.accept(i, j);
                    swap(i, j);
                }
            }
        }
    }

    public void selectSort(){
        compareCount = 0;
        swapCount = 0;
        int min;
        for(int out = 0; out < this.lenght-1; out++){
            min = out;
            for(int in = out+1; in < this.lenght; in++) {
                compareCount++;
                if (get(in).compareTo(get(min)) < 0)
                    min = in;
            }
            if(swapConsumer != null)
                swapConsumer.accept(out, min);
            swap(out, min);
        }
    }

    public void insertSort(){//TODO
        compareCount = 0;
        swapCount = 0;
        for(int out = 1; out < this.lenght; out++){
            T temp = get(out);
            float tempValue = 0.0F;
            if(saveConsumer != null)
                tempValue = saveConsumer.apply(get(out));
            int in = out;
            while (in > 0 && get(in-1).compareTo(temp)>=0){
                compareCount++;
                if(assignConsumer != null) {
                    float tmpValue = saveConsumer.apply(get(in-1));
                    assignConsumer.accept(get(in), get(in-1));
                }
                array[in] = array[in-1];
                in--;
            }
            swapCount++;
            if(assignConsumer != null) {
                assignConsumer.accept(get(in), temp);
            }
            array[in] = temp;
        }
    }

    public T binary_search(T element){
        compareCount = 0;
        swapCount = 0;
        return binSearch(0, lenght-1, element);
    }

    private T binSearch(int left, int right, T element){
        compareCount++;
        int center = (left + right) / 2;
        if((get(center)).compareTo(element) == 0)
            return get(center);

        if((get(center)).compareTo(element) < 0){
            return binSearch(center+1, right, element);
        }else {
            return binSearch(left, center-1, element);
        }
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            int curr = 0;

            @Override
            public boolean hasNext() {
                return curr < cur;
            }

            @Override
            public T next() {
                return get(curr++);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    public int getSwapCount() {
        return swapCount;
    }

    public int getCompareCount() {
        return compareCount;
    }

    public void setSwapConsumer(BiConsumer<Integer, Integer> swapConsumer) {
        this.swapConsumer = swapConsumer;
    }

    public void setAssignConsumer(BiConsumer<T, T> assignConsumer) {
        this.assignConsumer = assignConsumer;
    }

    public void setSaveConsumer(Function<T, Float> saveConsumer) {
        this.saveConsumer = saveConsumer;
    }
}







