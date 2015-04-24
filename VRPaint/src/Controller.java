import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.border.*;

public class Controller extends JFrame
{
    private JMenuItem connectMenuItem;
    private PrintWriter out;
    private JButton xButton;
    private JButton tButton,bButton,lButton,rButton;
    private JButton trButton,tlButton,brButton,blButton;
    private JButton lineButton, circleButton, polygonButton, freeButton, menuButton, exitButton;
    private String message = "";
    private JPanel mainPanel, mainPanel2, mainPanel3;
    public Controller(String st)
    {
        super(st);
        mainPanel3 = new JPanel();
        mainPanel=new JPanel();
        //mainPanel.setBorder(new EmptyBorder(5, 15, 5, 10));
        mainPanel.setLayout( new GridLayout(3,3));
        mainPanel3.add(mainPanel);
        mainPanel2=new JPanel();
        mainPanel2.setLayout( new GridLayout(3,2));
        mainPanel3.add(mainPanel2);
        add(mainPanel3);
        ActionListener al = new ButtonHandler();

        tlButton = new JButton("↖");
        mainPanel.add(tlButton);
        tlButton.addActionListener(al);

        tButton = new JButton("↑");
        mainPanel.add(tButton);
        tButton.addActionListener(al);

        trButton = new JButton("↗");
        mainPanel.add(trButton);
        trButton.addActionListener(al);

        lButton = new JButton("←");
        mainPanel.add(lButton);
        lButton.addActionListener(al);

        xButton = new JButton("Draw");
        mainPanel.add(xButton);
        xButton.addActionListener(al);

        rButton = new JButton("→");
        mainPanel.add(rButton);
        rButton.addActionListener(al);

        blButton = new JButton("↙");
        mainPanel.add(blButton);
        blButton.addActionListener(al);

        bButton = new JButton("↓");
        mainPanel.add(bButton);
        bButton.addActionListener(al);

        brButton = new JButton("↘");
        mainPanel.add(brButton);
        brButton.addActionListener(al);

        lineButton = new JButton("LINE");
        mainPanel2.add(lineButton);
        lineButton.addActionListener(al);

        freeButton = new JButton("Free Drawing");
        mainPanel2.add(freeButton);
        freeButton.addActionListener(al);

        circleButton = new JButton("CIRCLE");
        mainPanel2.add(circleButton);
        circleButton.addActionListener(al);

        polygonButton = new JButton("POLYGON");
        mainPanel2.add(polygonButton);
        polygonButton.addActionListener(al);

        menuButton = new JButton("Menu");
        mainPanel2.add(menuButton);
        menuButton.addActionListener(al);

        exitButton = new JButton("Exit");
        mainPanel2.add(exitButton);
        exitButton.addActionListener(al);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300,300);
        setResizable(false);
        setVisible(true);


        try
        {
            ServerSocket ss = new ServerSocket(5009);
            while(true)
            {
                Socket s = ss.accept();
                new Worker(s).start();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    private class Worker extends Thread
    {
        private Socket s;
        public Worker(Socket s)
        {
            this.s = s;
        }
        public void run()
        {
            try
            {
                out=new PrintWriter(s.getOutputStream(),true);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String st = "";
            if(e.getSource()==tlButton)
            {
                st = "7";
            }
            if(e.getSource()==tButton)
            {
                st = "8";
            }
            if(e.getSource()==trButton)
            {
                st = "9";
            }
            if(e.getSource()==lButton)
            {
                st = "4";
            }
            if(e.getSource()==xButton)
            {
                st = "5";
            }
            if(e.getSource()==rButton)
            {
                st = "6";
            }
            if(e.getSource()==blButton)
            {
                st = "1";
            }
            if(e.getSource()==bButton)
            {
                st = "2";
            }
            if(e.getSource()==brButton)
            {
                st = "3";
            }
            if(e.getSource()==lineButton)
            {
                st = "l";
            }
            if(e.getSource()==circleButton)
            {
                st = "c";
            }
            if(e.getSource()==polygonButton)
            {
                st = "p";
            }
            if(e.getSource()==freeButton)
            {
                st = "f";
            }
            if(e.getSource()==menuButton)
            {
                st = "m";
            }
            if(e.getSource()==exitButton)
            {
                st = "e";
            }
            out.println(st);
        }
    }
    public static void main(String[] args)
    {
        new Controller("Controler");
    }
}