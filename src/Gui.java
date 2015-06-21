import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.TextArea;

//import javafx.scene.control.ComboBox;















import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;


public class Gui extends JFrame {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 2992990019695863280L;
	private JPanel contentPane;
	private JTextField textField;
	private JComboBox comboBox_1;
	private JTextArea textArea;
	private JComboBox comboBox;
	private JLabel lblKodowanie;
	private JTextPane txtpnWyniki;
	private JLabel lblBufor;
	private JComboBox comboBox_2;
	private JComboBox comboBox_3;
	private JLabel lblWtki;
	
<<<<<<< HEAD
	private JList<String> list;
=======
	private JList<Object> list;
>>>>>>> parent of bb77e1b... a

	/**
	 * Uruchamianie
	 */
	public static void main(String[] args) throws IOException, InterruptedException  {
		//obsługa argumentów z linii komend
		int bufferSize = 8192;
        boolean printStats = false;
        boolean useNaive = false;
        boolean useFastNIO = false;
        String kodowanie = "UTF-8"; //kodowanie  //US-ASCII
        int algo = 0; //algorytm wyszukiwania
        int threadsCount = Runtime.getRuntime().availableProcessors(); //liczba wątków taka jak liczba rdzenii
        
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
                            kodowanie = args[argumentsIndex + 1];
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
        		algo=0; //domyślny
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
            	Search.pomoc();
            }
		
		
	}

	/**
	 * 
	 * Twórz gui!
	 */
	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 189);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setToolTipText("Wpisz tekst do wyszukania");
		textField.setBounds(23, 11, 314, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnSzukaj = new JButton("Szukaj");
<<<<<<< HEAD
		JRootPane rootPane = SwingUtilities.getRootPane(btnSzukaj); 
		//((JcontentPane.setDefaultButton(btnSzukaj);
		
=======
>>>>>>> parent of bb77e1b... a
		btnSzukaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//sprawdź czy podano wymagane opcje
				if(textArea.getText().length() < 1)
					JOptionPane.showMessageDialog(contentPane, "Proszę wybrać folder który ma zostać przeszukany.");
				else if(textField.getText().length() < 1)
					JOptionPane.showMessageDialog(contentPane, "Proszę wpisać frazę która ma zostać wyszukana.");
				else
					
				//usunięcie wcześniejszych wyników
				Search.s1 = new StringBuilder();
				Search.s2.clear();
				txtpnWyniki.setText("");
				//dodanie listy plików
				DefaultListModel<String> modelwyn = new DefaultListModel<>();
				list = new JList<>(modelwyn);
				 
				//uruchom szukanie
				Search.szukaj(textField.getText(), textArea.getText(), comboBox_1.getSelectedIndex(), (Integer) comboBox_3.getSelectedItem(), (Integer) comboBox_2.getSelectedItem(), comboBox.getSelectedItem().toString());
				txtpnWyniki.setText(Search.s1.toString());
				//dodanie wyników do listy
				list.removeAll();
				for(String wyn : Search.s2){
					modelwyn.addElement(wyn);
				}
				list.setBounds(23, 290, 431, 118);
				contentPane.add(list);	

				//dodanie listy plików
				//JScrollPane scrollPane = new JScrollPane(); //scrollbar
<<<<<<< HEAD
				//list = new JList<>(Search.s2.toArray());
=======
				list = new JList<>(Search.s2.toArray());
>>>>>>> parent of bb77e1b... a
				JScrollPane scrollPane_1 = new JScrollPane(list);
				scrollPane_1.setBounds(23, 290, 431, 118);
				//list.setBounds(23, 290, 431, 118);
				contentPane.add(scrollPane_1);
<<<<<<< HEAD

=======
>>>>>>> parent of bb77e1b... a
				//otwiera plik po 2 krotnym kliknięciu
				MouseListener mouseListener = new MouseAdapter() {
				      public void mouseClicked(MouseEvent mouseEvent) {
				        JList theList = (JList) mouseEvent.getSource();
				        if (mouseEvent.getClickCount() == 2) {
				          int index = theList.locationToIndex(mouseEvent.getPoint());
				          if (index >= 0) {
				            Object o = theList.getModel().getElementAt(index);
				            System.out.println("Double-clicked on: " + o.toString());
				            //otwórz plik
				            try {
								//Runtime.getRuntime().exec("\"" +o.toString() +"\"");
				            	//String[] plik = new String[] {o.toString()};
				            	//Runtime.getRuntime().exec(plik);
				            	if (Desktop.isDesktopSupported()) {
				            	    Desktop.getDesktop().edit(new File(o.toString()) );
				            	}
								//ProcessBuilder pb = new ProcessBuilder(o.toString());
								//Process p = pb.start();
							} catch (IOException e) {
								e.printStackTrace();
							}
				          }
				        }
				      }
				    };
				    list.addMouseListener(mouseListener); //dodaje
				//odśwież
				contentPane.revalidate();
				contentPane.repaint();
				//zmiana rozmiaru
				setBounds(100, 100, 480, 550);
				
				//System.out.println("test: " + textField.getText()+textArea.getText()+comboBox_1.getSelectedIndex()+ (Integer) comboBox_2.getSelectedItem()+ (Integer) comboBox_2.getSelectedItem() + comboBox.getSelectedItem().toString());
				
			} 
		});
		btnSzukaj.setBounds(347, 10, 107, 23);
		contentPane.add(btnSzukaj);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"UTF-8", "cp1250", "US-ASCII", "UTF-16"}));
		comboBox.setBounds(207, 119, 66, 20);
		contentPane.add(comboBox);
		
		comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Algorytm KMP", "Algorytm KMP z NIO", "Algorytm naiwny"}));
		comboBox_1.setBounds(23, 119, 121, 20);
		contentPane.add(comboBox_1);
		
		lblKodowanie = new JLabel("kodowanie");
		lblKodowanie.setBounds(154, 122, 60, 14);
		contentPane.add(lblKodowanie);
		
		txtpnWyniki = new JTextPane();
		//txtpnWyniki.setText("wyniki");
		//txtpnWyniki.setBounds(23, 150, 431, 118);
		//contentPane.add(txtpnWyniki);
		JScrollPane scrollPane_2 = new JScrollPane(txtpnWyniki);
		scrollPane_2.setBounds(23, 150, 431, 118);
		contentPane.add(scrollPane_2);
		
		lblBufor = new JLabel("bufor");
		lblBufor.setBounds(283, 122, 34, 14);
		contentPane.add(lblBufor);
		
		comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new Integer[] {8192, 32768, 16384, 4096, 2048, 1024, 512, 256, 128, 64, 32}));
		comboBox_2.setBounds(315, 119, 60, 20);
		contentPane.add(comboBox_2);
		
		//String data[] = {"a","b","c"};
	
		
		textArea = new JTextArea();
		textArea.setBounds(70, 48, 267, 16);
		contentPane.add(textArea);
		
		JLabel lblFolder = new JLabel("folder");
		lblFolder.setBounds(23, 48, 46, 14);
		contentPane.add(lblFolder);
		
		JButton btnDodajFolder = new JButton("Dodaj folder");
		btnDodajFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//kliknięty
				 JFileChooser chooser = new JFileChooser();
				    chooser.setCurrentDirectory(new java.io.File("."));
				    chooser.setDialogTitle("choosertitle");
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    chooser.setAcceptAllFileFilterUsed(false);
	
				    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				    	//wybierz folder
				    
				      textArea.setText(chooser.getSelectedFile().toString());
				    }
			}
		});
		btnDodajFolder.setBounds(347, 44, 107, 23);
		contentPane.add(btnDodajFolder);
		
		comboBox_3 = new JComboBox();
		//generuj listę do wybory wątków
		int threadsCount = Runtime.getRuntime().availableProcessors(); //liczba wątków taka jak liczba rdzenii
        Vector<Integer> comboBox3Items=new Vector();
        for(int i=threadsCount; i> -1; i--){
        	comboBox3Items.add(i);
        }
        final DefaultComboBoxModel model3 = new DefaultComboBoxModel(comboBox3Items);
        comboBox_3.setModel(model3);
		comboBox_3.setBounds(415, 119, 39, 20);
		contentPane.add(comboBox_3);
		
		lblWtki = new JLabel("w\u0105tki");
		lblWtki.setBounds(380, 122, 46, 14);
		contentPane.add(lblWtki);
		

	}
}