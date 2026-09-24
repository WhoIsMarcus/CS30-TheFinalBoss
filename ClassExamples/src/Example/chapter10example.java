package Example;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class chapter10example {

	private JFrame frame;
	private JTextField lastName;
	private JTextField firstName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					chapter10example window = new chapter10example();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public chapter10example() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 648, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		lastName = new JTextField();
		lastName.setText("Enter Last Name");
		lastName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) 
			{
				if(lastName.getText().equals("Enter Last Name")) 
				{
					lastName.setText("");
				}
			}
		});
		lastName.setBounds(210, 11, 214, 35);
		panel.add(lastName);
		lastName.setColumns(10);
		
		firstName = new JTextField();
		firstName.setText("Enter First Name");
		firstName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(firstName.getText().equals("Enter First Name")) 
				{
					firstName.setText("");
				}
			}
		});
		firstName.setColumns(10);
		firstName.setBounds(10, 11, 190, 35);
		panel.add(firstName);
		
		JButton submit = new JButton("New button");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		submit.setBounds(434, 11, 164, 239);
		panel.add(submit);
	}
}
