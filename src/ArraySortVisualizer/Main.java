package ArraySortVisualizer;

import Engine2D.*;
import Engine2D.Rectangle;
import UnityMath.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Random;
public class Main {
    public static int WIDTH = 1200;
    public static int HEIGHT = 800;
    public static boolean flag_sel = false;
    public static boolean flag_bub = false;
    public static boolean flag_sha = false;
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
    Main(){
        System.out.println("Hello world!");
        scene = new Scene(WIDTH, HEIGHT);
        scene.setBorder(20, Color.CYAN);
        scene.setCoordVisible(true);
        scene.setCenterVisible(true);
        scene.setBorderVisible(true);
        frame = new JFrame("3D");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(WIDTH+100, HEIGHT+100));
        frame.setLayout(new BorderLayout());
        frame.add(scene, BorderLayout.CENTER);
        JButton buble = new JButton("Buble");
        JButton insert = new JButton("Insert");
        JButton shafl = new JButton("Shafl");
        buble.setPreferredSize(new Dimension(70, 20));
        insert.setPreferredSize(new Dimension(70, 20));
        shafl.setPreferredSize(new Dimension(70, 20));
        buble.addActionListener(actionEvent -> {if(flag_sort) return;flag_bub = true; flag_sort = true;});
        insert.addActionListener(actionEvent -> {if(flag_sort) return;flag_sel = true; flag_sort = true;});
        shafl.addActionListener(actionEvent -> {if(flag_sort) return;flag_sha = true; flag_sort = true;});

       /* buble.addActionListener(actionEvent -> {
            selectionSort();
            flag_sel = false;
            flag_sort = false;});
        insert.addActionListener(actionEvent -> {
            bubleSort();
            flag_bub = false;
            flag_sort = false;});
        shafl.addActionListener(actionEvent -> {
            shafle();
            flag_sha = false;
            flag_sort = false;});*/

        frame.add(buble, BorderLayout.WEST);
        frame.add(insert, BorderLayout.EAST);
        frame.add(shafl, BorderLayout.SOUTH);
        scene.repaint();
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Scene.objects.clear();
                fill(false);
            }
        });
        frame.setVisible(true);
        fill(true);
        /*frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });*/
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });

        while (true){
            if(flag_sel){
                selectionSort();
                flag_sel = false;
                flag_sort = false;
            }else if(flag_bub){
                bubleSort();
                flag_bub = false;
                flag_sort = false;
            }else if(flag_sha){
                shafle();
                flag_sha = false;
                flag_sort = false;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void fill(boolean rand){
        size = 100;
        if(rand)sizeBuff = new int[size];
        interval = 0;
        itemSize = (Main.frame.getWidth()-150)/((size)*(interval+1));
        maxH = (Main.frame.getHeight()-100)/4;
        startX = -(((size-1)*(itemSize*(interval+1))))/2;
        startY = -maxH;
        color = Color.MAGENTA;

        Random r = new Random();
        int oldMax = Arrays.stream(sizeBuff).max().getAsInt();
        int dif;
        if(oldMax > maxH) dif = Math.abs(Arrays.stream(sizeBuff).max().getAsInt() - maxH);
        else dif = 0;
        for(int i = 0; i < size; ++i){
            int value;
            if(rand) value = r.nextInt(maxH-5)+5;
            else {
                value = sizeBuff[i] - dif;
                if (value < 0) value = 0;
            }
            ShapesObject item = new ShapesObject("Grapsh", 1);
            if(i%2 == 0)      item.add(new Triangle(    value, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY + value), color));
            else if(i%3 == 0) item.add(new Circle(      value, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY + value), color));
            else              item.add(new Rectangle(   value, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY + value), color));

            if(i%2 == 0)      item.add(new Rectangle(   itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY+(value*2)), Color.BLUE));
            else if(i%3 == 0) item.add(new Triangle(    itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY+(value*2)), Color.BLUE));
            else              item.add(new Circle(      itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), startY+(value*2)), Color.BLUE));
            if(i%2 == 0)      item.add(new Circle(      itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), itemSize + (startY+(value*2))), Color.PINK));
            else if(i%3 == 0) item.add(new Rectangle(   itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), itemSize + (startY+(value*2))), Color.PINK));
            else              item.add(new Triangle(    itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))), itemSize + (startY+(value*2))), Color.PINK));
            if(i%2 == 0)      item.add(new Triangle(    itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))),  2*itemSize + (startY+(value*2))), Color.ORANGE));
            else if(i%3 == 0) item.add(new Circle(      itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))),  2*itemSize + (startY+(value*2))), Color.ORANGE));
            else              item.add(new Rectangle(   itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))),  2*itemSize + (startY+(value*2))), Color.ORANGE));

            item.add(new Line(Line.TYPE.VERTICAL, itemSize/2, new Vector2(startX + (i*(itemSize*(interval+1))),  3*itemSize + (startY+(value*2))), Color.BLACK));
            scene.add(item);
            if(rand) sizeBuff[i] = item.body.get(0).height;
        }

    }

    public static void shafle(){
        Scene.objects.clear();
        fill(true);
        scene.repaint();
    }
    /**Selection Sort*/
    public static void selectionSort() {
        ShapesObject[] shapes = new ShapesObject[Scene.objects.size()];
        for(var i = 0; i < Scene.objects.size(); ++i){
            shapes[i] = Scene.objects.get(i);
        }
        for (int i = 0; i < shapes.length; i++) {//todo
            int min = (shapes[i]).body.get(0).height;
            int minId = i;
            var minP = (shapes[i]).body.get(0).position.y;
            for (int j = i+1; j < shapes.length; j++) {
                if ((shapes[j]).body.get(0).height < min) {
                    min = (shapes[j]).body.get(0).height;
                    minId = j;
                    minP = (shapes[j]).body.get(0).position.y;
                }
            }
            // swapping
            for(int k = 0; k < shapes[i].body.size(); ++k){
                if(k == 0) {
                    var temp = shapes[i].body.get(k).height;
                    shapes[i].body.get(k).height = min;
                    shapes[minId].body.get(k).height = temp;
                    shapes[i].body.get(k).resize();
                    shapes[minId].body.get(k).resize();
                    sizeBuff[i] = min;
                    sizeBuff[minId] = temp;
                }
                var temp = shapes[i].body.get(k).position.y;
                shapes[i].body.get(k).position.y = shapes[minId].body.get(k).position.y;
                shapes[minId].body.get(k).position.y = temp;

                shapes[i].body.get(k).resize();
                shapes[minId].body.get(k).resize();
            }

            shapes[minId].body.get(0).color = Color.RED;
            shapes[i].body.get(0).color = Color.GREEN;
            scene.repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            shapes[i].body.get(0).color = color;
            shapes[minId].body.get(0).color = color;
        }
        Scene.objects.clear();
        fill(false);
    }

    /**Buble Sort*/
    public static void bubleSort(){
        Clock clock = Clock.tickMillis(ZoneId.systemDefault());
        System.out.println(clock.millis());//todo
        ShapesObject[] shapes = new ShapesObject[Scene.objects.size()];
        for(var i = 0; i < Scene.objects.size(); ++i){
            shapes[i] = Scene.objects.get(i);
        }
        for (int i = 0; i < shapes.length; ++i) {
            for (int j = 0; j < i; ++j) {
                if (shapes[i].body.get(0).height < shapes[j].body.get(0).height) {
                    for(int k = 0; k < shapes[i].body.size(); ++k){
                        if(k == 0) {
                            var temp = shapes[i].body.get(k).height;
                            shapes[i].body.get(k).height = shapes[j].body.get(k).height;
                            sizeBuff[i] = shapes[j].body.get(k).height;
                            shapes[j].body.get(k).height = temp;
                            sizeBuff[j] = temp;
                        }
                        var temp = shapes[i].body.get(k).position.y;
                        shapes[i].body.get(k).position.y = shapes[j].body.get(k).position.y;
                        shapes[j].body.get(k).position.y = temp;

                        shapes[i].body.get(k).resize();
                        shapes[j].body.get(k).resize();
                    }

                    shapes[j].body.get(0).color = Color.GREEN;
                    shapes[i].body.get(0).color = Color.RED;
                    scene.repaint();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    shapes[i].body.get(0).color = color;
                    shapes[j].body.get(0).color = color;
                }
            }
        }
        System.out.println(clock.millis());//todo
    }
}

