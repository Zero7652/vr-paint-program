/**
 * Created by Stanly on 4/11/2015.
 */
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Controller extends JFrame
{
    private JMenuItem connectMenuItem;
    private PrintWriter out;
    private JButton xButton;
    private JButton tButton,bButton,lButton,rButton;
    private JButton trButton,tlButton,brButton,blButton;
    private String message = "";
    private JPanel mainPanel;
    public Controller(String st)
    {
        super(st);
        mainPanel=new JPanel();
        mainPanel.setBorder(new EmptyBorder(5, 15, 5, 10));
        mainPanel.setLayout( new GridLayout(3,3));
        add(mainPanel);
        ActionListener al = new ButtonHandler();

        tlButton = new JButton("â†–");
        mainPanel.add(tlButton);
        tlButton.addActionListener(al);

        tButton = new JButton("â†‘");
        mainPanel.add(tButton);
        tButton.addActionListener(al);

        trButton = new JButton("â†—");
        mainPanel.add(trButton);
        trButton.addActionListener(al);

        lButton = new JButton("â†�");
        mainPanel.add(lButton);
        lButton.addActionListener(al);

        xButton = new JButton("Click");
        mainPanel.add(xButton);
        xButton.addActionListener(al);

        rButton = new JButton("â†’");
        mainPanel.add(rButton);
        rButton.addActionListener(al);

        blButton = new JButton("â†™");
        mainPanel.add(blButton);
        blButton.addActionListener(al);

        bButton = new JButton("â†“");
        mainPanel.add(bButton);
        bButton.addActionListener(al);

        brButton = new JButton("â†˜");
        mainPanel.add(brButton);
        brButton.addActionListener(al);

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
                //System.out.println("7 pressed");
                st = "7";
            }
            if(e.getSource()==tButton)
            {
                //System.out.println("8 pressed");
                st = "8";
            }
            if(e.getSource()==trButton)
            {
                //System.out.println("9 pressed");
                st = "9";
            }
            if(e.getSource()==lButton)
            {
                //System.out.println("4 pressed");
                st = "4";
            }
            if(e.getSource()==xButton)
            {
                //System.out.println("5 pressed");
                st = "5";
            }
            if(e.getSource()==rButton)
            {
                //System.out.println("6 pressed");
                st = "6";
            }
            if(e.getSource()==blButton)
            {
                //System.out.println("1 pressed");
                st = "1";
            }
            if(e.getSource()==bButton)
            {
                //System.out.println("2 pressed");
                st = "2";
            }
            if(e.getSource()==brButton)
            {
                //System.out.println("3 pressed");
                st = "3";
            }
            out.println(st);
        }
    }
    public static void main(String[] args)
    {
        new Controller("Controler");
    }
}