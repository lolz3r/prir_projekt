import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JTable;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.Charset;


public class Gui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2992990019695863280L;
	private JPanel contentPane;
	private JTextField textField;
	private JComboBox comboBox_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException, InterruptedException  {
		//obs³uga argumentów z linii komend
		int bufferSize = 8192;
        boolean printStats = false;
        boolean useNaive = false;
        boolean useFastNIO = false;
        int threadsCount = Runtime.getRuntime().availableProcessors(); //liczba w¹tków taka jak liczba rdzenii
        Charset kodowanie = Charset.forName("UTF-8"); //kodowanie  //US-ASCII
        int algo = 0; //algorytm wyszukiwania
        
        // Opcje z linii komend
        int argumentsIndex = 0;
        while (args.length > argumentsIndex) {
            final String opts = args[argumentsIndex];
            boolean validArgument = false;
            if (opts.startsWith("-") && opts.length() > 1) {
                switch (opts.charAt(1)) {
                    case 's':
                        printStats = validArgument = true;
                        argumentsIndex++;
                        break;
                    case 'n':
                        useNaive = validArgument = true;
                        argumentsIndex++;
                        break;
                    case 'f':
                        useFastNIO = validArgument = true;
                        argumentsIndex++;
                        break;
                    case 'b':
                        if (!(validArgument = args.length > (argumentsIndex + 1))) {
                            throw new IllegalArgumentException("Argument required for " + opts.charAt(1));
                        }
                        bufferSize = Integer.parseInt(args[argumentsIndex + 1]);
                        argumentsIndex += 2;
                        break;
                    case 't':
                        if (!(validArgument = args.length > (argumentsIndex + 1))) {
                            throw new IllegalArgumentException("Argument required for " + opts.charAt(1));
                        }
                        threadsCount = Integer.parseInt(args[argumentsIndex + 1]);
                        argumentsIndex += 2;
                        break;
                    case 'c':
                        validArgument = args.length > (argumentsIndex + 1);
                        if (validArgument) {
                            kodowanie = Charset.forName(args[argumentsIndex + 1]);
                            if (kodowanie == null) {
                                throw new IllegalArgumentException("Invalid charset specified: " + args[argumentsIndex + 1]);
                            }
                            argumentsIndex += 2;
                        }
                        break;
                    case '-':
                        argumentsIndex++;
                        break;
                }
            }

            if (!validArgument) {
                break;
            }
        }
        
        
        
        
        if (args.length - argumentsIndex > 1) {
            //uruchom szukanie
        	String folder = args[argumentsIndex]; //folder
        	String fraza = args[argumentsIndex + 1]; //fraza do szukania
        			
        	if(useNaive){ //algo
        		algo=2;
        	}else if(useFastNIO){
        		algo=1;
        	}else{
        		algo=0; //domyœlny
        	}
            //szukaj
        	Search.szukaj(fraza,folder,algo,threadsCount,bufferSize,kodowanie);
        	
            } else {
            	//gui
        		EventQueue.invokeLater(new Runnable() {
        			public void run() {
        				try {
        					Gui frame = new Gui();
        					frame.setVisible(true);
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        			}
        		});
        		//pomoc
            	Search.printHelp();
            }
		
		
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 318);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setToolTipText("Wpisz tekst do wyszukania");
		textField.setBounds(23, 11, 269, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnSzukaj = new JButton("Szukaj");
		btnSzukaj.setBounds(302, 10, 107, 23);
		contentPane.add(btnSzukaj);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"UTF-8", "cp1250", "US-ASCII", "UTF-16"}));
		comboBox.setBounds(213, 119, 60, 20);
		contentPane.add(comboBox);
		
		comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Algorytm KMP", "Algorytm KMP z NIO", "Algorytm naiwny"}));
		comboBox_1.setBounds(23, 119, 121, 20);
		contentPane.add(comboBox_1);
		
		JLabel lblKodowanie = new JLabel("kodowanie");
		lblKodowanie.setBounds(154, 122, 60, 14);
		contentPane.add(lblKodowanie);
		
		JTextPane txtpnWyniki = new JTextPane();
		txtpnWyniki.setText("wyniki");
		txtpnWyniki.setBounds(23, 150, 386, 118);
		contentPane.add(txtpnWyniki);
		
		JLabel lblBufor = new JLabel("bufor");
		lblBufor.setBounds(283, 122, 34, 14);
		contentPane.add(lblBufor);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"8192", "32768", "16384", "4096", "2048", "1024", "512", "256", "128", "64", "32"}));
		comboBox_2.setBounds(315, 119, 60, 20);
		contentPane.add(comboBox_2);
		
		JButton btnDodajFolder = new JButton("Dodaj folder");
		btnDodajFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//klikniêty
				System.out.println("klikniêty przycisk szukaj");
			}
		});
		btnDodajFolder.setBounds(302, 44, 107, 23);
		contentPane.add(btnDodajFolder);
		
		JTextArea textArea = new JTextArea();
		textArea.setText("...");
		textArea.setBounds(77, 48, 92, 16);
		contentPane.add(textArea);
		
		JLabel lblFolder = new JLabel("folder:");
		lblFolder.setBounds(33, 48, 46, 14);
		contentPane.add(lblFolder);
	}
}
