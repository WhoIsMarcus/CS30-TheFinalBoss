package Visuals;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class chat extends JFrame { 

    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextArea inputArea;

    public chat() {
        setTitle("Chat");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
        setLocationRelativeTo(null);

        Color background = new Color(33, 33, 33);
        Color inputBackground = new Color(47, 47, 47);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(background);

        // Chat area
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(background);
        chatPanel.setBorder(new EmptyBorder(30, 80, 30, 80));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(background);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scrollPane, BorderLayout.CENTER);

        // Input wrapper
        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBackground(background);
        inputWrapper.setBorder(new EmptyBorder(10, 140, 25, 140));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(inputBackground);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            new EmptyBorder(8, 15, 8, 8)
        ));

        inputArea = new JTextArea(2, 20);
        inputArea.setBackground(inputBackground);
        inputArea.setForeground(Color.WHITE);
        inputArea.setCaretColor(Color.WHITE);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(null);

        JButton sendButton = new JButton("↑");
        sendButton.setFont(new Font("Arial", Font.BOLD, 20));
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(45, 45));
        
        sendButton.setBackground(new Color(70, 70, 70));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorderPainted(false);
        sendButton.setOpaque(true);

        sendButton.addActionListener(e -> sendMessage());

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        return;
                    }
                    e.consume();
                    sendMessage();
                }
            }
        });

        inputPanel.add(inputArea, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        inputWrapper.add(inputPanel, BorderLayout.CENTER);
        root.add(inputWrapper, BorderLayout.SOUTH);

        add(root);

        addAssistantMessage(
            "Hey Marcus. This is your Java chat interface.\n\n" +
            "Type something below and press Enter."
        );

        setVisible(true);
    }

    private void sendMessage() {
        String text = inputArea.getText().trim();

        if (text.isEmpty()) {
            return;
        }

        inputArea.setText("");
        addUserMessage(text);

        System.out.println("USER SAID:");
        System.out.println(text);
        if(text.toLowerCase().equals("hello"))
        {
        		SwingUtilities.invokeLater(() -> {
                    addAssistantMessage("Hello Marcus");
                });
        		
        }
        else if(text.toLowerCase().equals("you wanna fuck"))
        {

    		SwingUtilities.invokeLater(() -> {
                addAssistantMessage("Of course sexy ;)");
            });        	
        	
        }
        else if(text.toLowerCase().equals("write me some code"))
        {

    		SwingUtilities.invokeLater(() -> {
                addAssistantMessage("def penis(size):\r\n"
                		+ "  print('8' + '=' * size + 'D')\r\n"
                		+ "\r\n"
                		+ "penis(12)");
            });        	
        	
        }
        else if(text.toLowerCase().equals("what would this output"))
        {

    		SwingUtilities.invokeLater(() -> {
                addAssistantMessage("I can show you later tonight baby");
            });     	
        	
        }
        
        else if(text.toLowerCase().equals("is eamon gay"))
        {

    		SwingUtilities.invokeLater(() -> {
                addAssistantMessage("yes.");
            });     	
        	
        }
        
        else if(text.toLowerCase().equals("what is the meaning of life"))
        {

    		SwingUtilities.invokeLater(() -> {
                addAssistantMessage("beats me");
            });     	
        	
        }
        
        
        else
        {
        	SwingUtilities.invokeLater(() -> {
                addAssistantMessage("You typed:\n\n" + text);
            });
        }
        
    }

    private void addUserMessage(String text) {
        addMessage(text, true);
    }

    private void addAssistantMessage(String text) {
        addMessage(text, false);
    }

    private void addMessage(String text, boolean user) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);

        JTextArea message = new JTextArea(text);
        message.setEditable(false);
        message.setFocusable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        message.setForeground(Color.WHITE);

        if (user) {
            message.setBackground(new Color(55, 55, 55));
        } else {
            message.setBackground(new Color(43, 43, 43));
        }

        message.setBorder(new EmptyBorder(12, 16, 12, 16));

        int maxWidth = 500;
        message.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        
        Dimension dynamicSize = message.getPreferredSize();
        dynamicSize.width = maxWidth;
        message.setSize(dynamicSize);
        
        message.setMaximumSize(new Dimension(maxWidth, message.getPreferredSize().height));

        if (user) {
            row.add(Box.createHorizontalGlue());
            row.add(message);
        } else {
            row.add(message);
            row.add(Box.createHorizontalGlue());
        }

        chatPanel.add(row);
        chatPanel.add(Box.createVerticalStrut(15));

        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(chat::new);
    }
}
