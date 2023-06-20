/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame
{
    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.

    static JPanel tools = new JPanel();
    static JPanel topLine = new JPanel();
    static JPanel bottomLine = new JPanel();
    DrawPanel drawPanel = new DrawPanel();


    // add status label
    static JLabel bottomPanel = new JLabel();
    // create the widgets for the firstLine Panel.
    
    static JLabel options = new JLabel("Shape");
    static String optionsList[] = {"Line", "Oval", "Rectangle"};
    static JComboBox shapeOptions = new JComboBox(optionsList);
    static JButton firstColorSelector = new JButton("1st Color...");
    static JButton secondColorSelector = new JButton("2nd Color...");
    static JButton undo = new JButton("Undo");
    static JButton clear = new JButton("Clear");

    //create the widgets for the secondLine Panel.
    
    static JCheckBox filledCheck = new JCheckBox("Filled");
    static JCheckBox gradientCheck = new JCheckBox("Use Gradient");
    static JCheckBox dashedCheck = new JCheckBox("Dashed");
    static JLabel lineWidth = new JLabel("Line Width:");
    static JSpinner lineW = new JSpinner();
    static Stroke stroke;
    static JLabel dashLengthLabel = new JLabel("Dash Length:");
    static JSpinner dash = new JSpinner();
    
    
    //Choosing colors
    


    // Variables for drawPanel.
    static ArrayList<MyShapes> shapesDrawn = new ArrayList<MyShapes>();
    static String shapeSelected;
    static Color firstColor = Color.BLACK;
    static Color secondColor = Color.BLACK;
    static Paint paint;
    static MyShapes currentShape;
    
    
    
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        // add widgets to panels
        tools.setLayout(new GridLayout(2,1));
        Color toolColor = new Color(153,255,255);
        topLine.setBackground(toolColor);
        bottomLine.setBackground(toolColor);

        
        // firstLine widgets
        topLine.setLayout(new FlowLayout());
        topLine.add(options);
        topLine.add(shapeOptions);
        firstColorSelector.addActionListener(new FirstColor());
        topLine.add(firstColorSelector);
        secondColorSelector.addActionListener(new SecondColor());
        topLine.add(secondColorSelector);
        topLine.add(undo);
        topLine.add(clear);

        
        // secondLine widgets
        bottomLine.setLayout(new FlowLayout());
        bottomLine.add(filledCheck);
        bottomLine.add(gradientCheck);
        bottomLine.add(dashedCheck);
        bottomLine.add(lineWidth);
        bottomLine.add(lineW);
        bottomLine.add(dashLengthLabel);
        bottomLine.add(dash);
        lineW.setValue(10);
        dash.setValue(15);

        // add top panel of two panels
        tools.add(topLine);
        tools.add(bottomLine);
        
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        this.setLayout(new BorderLayout());
        this.add(tools, BorderLayout.NORTH);
        this.add(drawPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        
     
        //add listeners and event handlers
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shapesDrawn.size() > 0){
                    shapesDrawn.remove(shapesDrawn.size() - 1);
                    repaint();
                }
            }
        });
        
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shapesDrawn.size() > 0){
                    shapesDrawn.clear();
                    repaint();
                }
            }
        });
        
              
    }

    // Create event handlers, if needed
    public class FirstColor implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            firstColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
        }
        
    }
    
    public class SecondColor implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            secondColor = JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
            
        }
        
    }
    
    
    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {

        public DrawPanel()
        {
            this.setBackground(Color.WHITE);
            int x = 0;
            int y = 0;
            addMouseListener(new MouseHandler());
            addMouseMotionListener(new MouseHandler());
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(firstColor);
            //loop through and draw each shape in the shapes arraylist
            for (MyShapes shape: shapesDrawn){
                shape.draw(g2d);
            }
            
            
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {

            public void mousePressed(MouseEvent event)
            {
                //Choose Shape
                Point p = new Point(event.getX(), event.getY());
                Float width = Float.parseFloat(lineW.getModel().getValue().toString());
                Float lengthvalue = Float.parseFloat(dash.getModel().getValue().toString());
                float[] lengthV= {lengthvalue};
                
                BasicStroke stroke;
                if (dashedCheck.isSelected()){
                    stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, lengthV, 0);
                } else {
                    stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                
                MyShapes rectangle = new MyRectangle(p, p, paint, stroke, false);
                
                if (gradientCheck.isSelected()){
                    paint = new GradientPaint(0,0,firstColor,50,50,secondColor,true);
                }
                else {
                    paint = firstColor;
                }
                
                shapeSelected = shapeOptions.getSelectedItem().toString();  
                    
                if (shapeSelected == "Line")
                {
                    currentShape = new MyLine(p,p,paint,stroke);
                }
                if(shapeSelected == "Rectangle")
                {
                    currentShape = new MyRectangle(p,p,paint,stroke, filledCheck.isSelected());    
                }
                if(shapeSelected == "Oval")
                {
                    currentShape = new MyOval(p, p, paint, stroke, filledCheck.isSelected());  
                }
                
                currentShape.setStroke(stroke);
                shapesDrawn.add(currentShape);
            }

            public void mouseReleased(MouseEvent event)
            {
                currentShape.setEndPoint(event.getPoint());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                //depending on the settings, draw the shape
                currentShape.setEndPoint(event.getPoint());
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                // change the status label
                String position = "(" + event.getPoint().x + "," + event.getPoint().y + ")";
                bottomPanel.setText(position); 
                repaint();
            }
        }

    }
}
