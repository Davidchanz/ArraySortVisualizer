package ArraySortVisualizer;

import com.FXChemiEngine.draw.BufferAWTImageDrawingObject;
import com.FXChemiEngine.engine.Scene;
import com.FXChemiEngine.engine.ShapeObject;
import com.FXChemiEngine.engine.shape.Rectangle;
import com.FXChemiEngine.math.UnityMath.Vector2;
import com.FXChemiEngine.util.Color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Random;
public class Main {
    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    public static boolean flag_sort = false;
    public static Scene scene;
    public static int maxH;
    public static int size;
    public static int itemSize;
    public static int interval;
    public static Color color;
    public static int startX;
    public static int startY;
    public static JFrame frame;
    public static int[] sizeBuff;
    private final JPanel panel;
    private static Array<SortItem<Rectangle>> array;
    private static ShapeObject visual;
    private BufferedImage image;
    private long delay = 10L;
    private Timer t;
    Main(){
        visual = new ShapeObject("array", 1);
        BufferAWTImageDrawingObject drawingObject = new BufferAWTImageDrawingObject();
        scene = new Scene(WIDTH, HEIGHT, Color.DARKGRAY, drawingObject);
        scene.add(visual);

        /*scene.setCoordVisible(true);
        scene.setCenterVisible(true);
        scene.setBorderVisible(true);*/

        frame = new JFrame("Array sort Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH+100, HEIGHT+100));
        frame.setLayout(new BorderLayout());
        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        frame.add(panel, BorderLayout.CENTER);
        JButton buble = new JButton("Buble");
        JButton insert = new JButton("Insert");
        JButton select = new JButton("Select");
        JButton shafl = new JButton("Shafl");
        buble.setPreferredSize(new Dimension(70, 20));
        select.setPreferredSize(new Dimension(70, 20));
        insert.setPreferredSize(new Dimension(70, 20));
        shafl.setPreferredSize(new Dimension(70, 20));

        buble.addActionListener(actionEvent -> {
            flag_sort = true;
            new Thread(() -> bubleSort()).start();
            flag_sort = false;
        });
        select.addActionListener(actionEvent -> {
            flag_sort = true;
            new Thread(() -> selectionSort()).start();
            flag_sort = false;
        });
        insert.addActionListener(actionEvent -> {
            flag_sort = true;
            new Thread(() -> insertSort()).start();
            flag_sort = false;
        });
        shafl.addActionListener(actionEvent -> {
            flag_sort = true;
            shafle(true);
            flag_sort = false;
        });

        size = 200;
        array = new Array<>(size);
        array.setSwapConsumer((i, j) -> {
            array.get(i).getBody().color = Color.GREEN;
            array.get(j).getBody().color = Color.RED;
            SortItem.swap(array.get(i), array.get(j));
            sleep();
            array.get(i).getBody().color = color;
            array.get(j).getBody().color = color;
        });

        array.setAssignConsumer((i, j) -> {
            i.getBody().color = Color.GREEN;
            j.getBody().color = Color.RED;
            SortItem.assign(i, j);
            sleep();
            i.getBody().color = color;
            j.getBody().color = color;
        });

        array.setSaveConsumer((x) -> SortItem.save(x));

        Box vBox = Box.createVerticalBox();
        vBox.add(buble);
        vBox.add(insert);
        vBox.add(select);
        vBox.add(shafl);

        frame.add(vBox, BorderLayout.WEST);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if(!flag_sort) {
                    scene.resize(frame.getWidth() - 100, frame.getHeight() - 100);
                    shafle(false);
                }
            }
        });

        t = new Timer(15, this::draw);

        frame.setVisible(true);
        shafle(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main m = new Main();
                m.t.start();
            }
        });
    }

    /**Sleep for algorithm visible*/
    private void sleep(){
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**Draw frame*/
    private void draw(ActionEvent actionEvent) {
        scene.repaint();
        image = scene.getImage();
        panel.repaint();
    }

    /**Shafl random array*/
    public void shafle(boolean rand){
        if (rand) {
            visual.clear();
            array.resize(size);
            sizeBuff = new int[size];
        }

        interval = 0;
        itemSize = (Main.frame.getWidth()-150)/((size)*(interval+1));
        maxH = (Main.frame.getHeight()-100)/4;
        startX = -(((size-1)*(itemSize*(interval+1))))/2;
        startY = -maxH;
        color = Color.WHITE;

        Random r = new Random();
        int oldMax = Arrays.stream(sizeBuff).max().getAsInt();
        int dif;
        if (oldMax > maxH) {
            dif = Math.abs(Arrays.stream(sizeBuff).max().getAsInt() - maxH);
        } else {
            dif = 0;
        }
        for(int i = 0; i < size; ++i){
            int value;
            if (rand) {
                value = r.nextInt(maxH - 5) + 5;
            } else {
                value = sizeBuff[i] - dif;
                if (value < 0) {
                    value = 0;
                }
            }
            if(rand) {
                SortItem<Rectangle> item = new SortItem<>(new Rectangle(value, itemSize / 2, new Vector2(startX + (i * (itemSize * (interval + 1))), startY + value), color), value);
                array.set(i, item);
                visual.add(item.getBody());
                sizeBuff[i] = item.getValue();
            }
            else {
                SortItem<Rectangle> item = new SortItem<>(new Rectangle(array.get(i).getValue(), itemSize / 2, new Vector2(startX + (i * (itemSize * (interval + 1))), startY + value), color), value);
                array.set(i, item);
                visual.set(i, item.getBody());
            }
        }

    }

    /**Selection Sort*/
    public void selectionSort() {
        array.selectSort();
        sizeBuff = Arrays.stream(sizeBuff).sorted().toArray();
    }

    /**Buble Sort*/
    public void bubleSort(){
        array.bubbleSort();
        sizeBuff = Arrays.stream(sizeBuff).sorted().toArray();
    }

    /**Insert Sort*/
    public void insertSort(){
        array.insertSort();
        sizeBuff = Arrays.stream(sizeBuff).sorted().toArray();

        System.out.println(array);
        Clock clock = Clock.tickMillis(ZoneId.systemDefault());
        System.out.println(clock.millis());//TODO

        System.out.println(clock.millis());
    }
}

