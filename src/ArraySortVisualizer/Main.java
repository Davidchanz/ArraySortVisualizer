package ArraySortVisualizer;

import com.FXChemiEngine.draw.BufferAWTImageDrawingObject;
import com.FXChemiEngine.engine.Scene;
import com.FXChemiEngine.engine.ShapeObject;
import com.FXChemiEngine.engine.shape.Rectangle;
import com.FXChemiEngine.math.UnityMath.Vector2;
import com.FXChemiEngine.util.Color;
import com.FXChemiEngine.util.Utils;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static ArraySortVisualizer.Constants.*;

public class Main {
    public static final AtomicBoolean flag_sort = new AtomicBoolean(false);
    public static Scene scene;
    public static Color itemColor = Color.ALICEBLUE;
    public static JFrame frame;
    public static int[] sizeBuff;
    private final JPanel panel;
    private static Array<SortItem<Rectangle>> array;
    private static ShapeObject visual;
    private BufferedImage image;
    private final Timer timer;
    private final JSpinner sizeSpiner;
    private JPanel settingPane;

    private final StopWatchPane stopWatch;
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
        stopWatch = new StopWatchPane();
        frame.add(panel, BorderLayout.CENTER);
        frame.add(stopWatch, BorderLayout.SOUTH);
        JButton buble = new JButton("Buble");
        JButton insert = new JButton("Insert");
        JButton select = new JButton("Select");
        JButton shafl = new JButton("Shafl");

        buble.addActionListener(actionEvent -> {
            new Thread(() -> sort(0)).start();
        });
        select.addActionListener(actionEvent -> {
            new Thread(() -> sort(1)).start();
        });
        insert.addActionListener(actionEvent -> {
            new Thread(() -> sort(2)).start();
        });
        shafl.addActionListener(actionEvent -> {
            shafle(true);
        });

        array = new Array<>(SIZE);
        array.setSwapConsumer((i, j) -> {
            array.get(i).getBody().color = Color.GREEN;
            array.get(j).getBody().color = Color.RED;
            SortItem.swap(array.get(i), array.get(j));
            sleep();
            array.get(i).getBody().color = itemColor;
            array.get(j).getBody().color = itemColor;
        });

        array.setAssignConsumer((i, j) -> {
            i.getBody().color = Color.GREEN;
            j.getBody().color = Color.RED;
            SortItem.assign(i, j);
            sleep();
            i.getBody().color = itemColor;
            j.getBody().color = itemColor;
        });

        array.setSaveConsumer((x) -> SortItem.save(x));

        Box sortingButtonsVBox = Box.createVerticalBox();
        sortingButtonsVBox.add(buble);
        sortingButtonsVBox.add(insert);
        sortingButtonsVBox.add(select);
        sortingButtonsVBox.add(shafl);

        frame.add(sortingButtonsVBox, BorderLayout.WEST);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if(!flag_sort.get()) {
                    scene.resize(frame.getWidth() - 200, frame.getHeight() - 100);
                    shafle(false);
                }
            }
        });

        settingPane = new JPanel();

        GridBagConstraints settingGridBox = new GridBagConstraints();
        settingGridBox.gridx =0;
        settingGridBox.gridy =0;
        settingGridBox.weightx =1;
        settingGridBox.gridwidth =GridBagConstraints.REMAINDER;
        settingGridBox.insets =new Insets(4,4,4,4);

        sizeSpiner = new JSpinner();
        var model = new SpinnerNumberModel();
        model.setMinimum(10);
        model.setMaximum(1000);
        model.setValue(200);
        model.addChangeListener(changeEvent -> {
            SIZE = (int)model.getValue();
            shafle(true);
        });
        sizeSpiner.setModel(model);

        JButton colorChooser = new JButton("Color");
        colorChooser.addActionListener(actionEvent -> {
            var color = JColorChooser.showDialog(
                    settingPane,
                    "Choose SortItem Color", java.awt.Color.WHITE);
            itemColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            shafle(false);
        });


        settingPane.add(sizeSpiner, settingGridBox);

        settingGridBox.gridx =0;
        settingGridBox.gridy++;
        settingGridBox.weightx =0;
        settingGridBox.gridwidth =1;
        settingPane.add(colorChooser, settingGridBox);

        frame.add(settingPane, BorderLayout.EAST);

        sizeSpiner.setPreferredSize(new Dimension(100, 20));

        timer = new Timer(15, this::draw);

        frame.setVisible(true);
        shafle(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main m = new Main();
                m.startTimer();
            }
        });
    }

    public void startTimer(){
        timer.start();
    }

    /**Sleep for algorithm visible*/
    private void sleep(){
        try {
            Thread.sleep(DELAY);
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
            array.resize(SIZE);
            sizeBuff = new int[SIZE];
        }

        int interval = 0;
        int itemSize = (Main.frame.getWidth()-150)/((SIZE)*(interval+1));
        int maxH = (Main.frame.getHeight()-100)/4;
        int startX = -(((SIZE - 1) * (itemSize * (interval + 1)))) / 2;
        int startY = -maxH;

        Random r = new Random();
        int oldMax = Arrays.stream(sizeBuff).max().getAsInt();
        int dif;
        if (oldMax > maxH) {
            dif = Math.abs(Arrays.stream(sizeBuff).max().getAsInt() - maxH);
        } else {
            dif = 0;
        }
        for(int i = 0; i < SIZE; ++i){
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
                SortItem<Rectangle> item = new SortItem<>(new Rectangle(value, itemSize / 2, new Vector2(startX + (i * (itemSize * (interval + 1))), startY + value), itemColor), value);
                array.set(i, item);
                visual.add(item.getBody());
                sizeBuff[i] = item.getValue();
            }
            else {
                SortItem<Rectangle> item = new SortItem<>(new Rectangle(array.get(i).getValue(), itemSize / 2, new Vector2(startX + (i * (itemSize * (interval + 1))), startY + value), itemColor), value);
                array.set(i, item);
                visual.set(i, item.getBody());
            }
        }

    }

    private void setSettingPaneEnable(boolean b){
        Arrays.stream(settingPane.getComponents()).toList().forEach(component -> component.setEnabled(b));
    }

    /**Sorting*/
    public void sort(int type){
        if(!flag_sort.get()) {
            flag_sort.set(true);
            setSettingPaneEnable(false);
            stopWatch.start();
            switch (type) {
                case 0 -> array.bubbleSort();
                case 1 -> array.selectSort();
                case 2 -> array.insertSort();
            }
            sizeBuff = Arrays.stream(sizeBuff).sorted().toArray();
            stopWatch.stop();
            setSettingPaneEnable(true);
            flag_sort.set(false);
        }
    }
}

