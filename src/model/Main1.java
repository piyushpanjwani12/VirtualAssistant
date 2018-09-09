/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author DELL
 */

import dbutil.DBConnection;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.JOptionPane;
import static model.GoogleSearchJava.GOOGLE_SEARCH_URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tts.TextToSpeech;


public class Main1 extends javax.swing.JFrame {

    // Necessary
	EnglishNumberToString	numberToString	= new EnglishNumberToString();
	EnglishStringToNumber	stringToNumber	= new EnglishStringToNumber();
	TextToSpeech			textToSpeech	= new TextToSpeech();

	// Logger
	private Logger logger = Logger.getLogger(getClass().getName());

	// Variables
	private String result;
        public static int isInit=1;
        public static int checkInput=1;
	// Threads
	Thread	speechThread;
	Thread	resourcesThread;

	// LiveRecognizer
	private LiveSpeechRecognizer recognizer;
        Connection conn;
	
	//Date
	 DateFormat df = new SimpleDateFormat("dd MMM yyyy");
     Date dateobj = new Date();
        Runtime runtime;
        ListAllFiles lf;
        FileUpdate f;
        String link[];
     
    
	/**
	 * Constructor
	 */
    public Main1() {
        initComponents();
        this.setResizable(false);
        this.pack();
        logger.log(Level.INFO, "Loading..\n");

		// Configuration
		Configuration configuration = new Configuration();

		// Load model from the jar
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

		// if you want to use LanguageModelPath disable the 3 lines after which
		// are setting a custom grammar->

		//configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

		// Grammar
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);

		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
                
                //Dont show the inrto part
                
                lblPath.setVisible(false);
                txtPath.setVisible(false);
                
                txtName.setVisible(false);
		// Start recognition process pruning previously cached data.
		recognizer.startRecognition(true);
                
                btnSearch.setVisible(false);
                
                lblSkip.setVisible(false);
                txtSearch.setBackground(Color.black);
                txtSearch.setBorder(null);
                jScrollPane1.setBorder(null);
                txtSearch.setOpaque(true);
                btnViewMore.setVisible(false);
		// Start the Thread
		startSpeechThread();
		startResourcesThread();
            }
            protected void startSpeechThread() {

		// alive?
		if (speechThread != null && speechThread.isAlive())
			return;

		// initialise
		speechThread = new Thread(() -> {
			logger.log(Level.INFO, "You can start to speak...\n");
                        
			try {
				while (true) {
					/*
					 * This method will return when the end of speech is
					 * reached. Note that the end pointer will determine the end
					 * of speech.
					 */
					SpeechResult speechResult = recognizer.getResult();
					if (speechResult != null) {

						result = speechResult.getHypothesis();
						System.out.println("You said: [" + result + "]\n");
                                                if(checkInput==1){
                                                    lblSpeech.setText("Thinking !!");
                                                    checkInput=0;
                                                }
                                                
//                                                lblSpeech.setText(result);
                                                
                                                
                                                makeDecision(result);
						// logger.log(Level.INFO, "You said: " + result + "\n")

					} else
						logger.log(Level.INFO, "I can't understand what you said.\n");

				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, null, ex);
			}

			logger.log(Level.INFO, "SpeechThread has exited...");
		});

		// Start
		speechThread.start();

	}

	/**
	 * Starting a Thread that checks if the resources needed to the
	 * SpeechRecognition library are available
	 */
	protected void startResourcesThread() {

		// alive?
		if (resourcesThread != null && resourcesThread.isAlive())
			return;
                
                
		resourcesThread = new Thread(() -> {
			try {

				// Detect if the microphone is available
				while (true) {
					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
						// logger.log(Level.INFO, "Microphone is available.\n")
					} else {
						// logger.log(Level.INFO, "Microphone is not
						// available.\n")

					}

					// Sleep some period
					Thread.sleep(350);
				}

			} catch (InterruptedException ex) {
				logger.log(Level.WARNING, null, ex);
				resourcesThread.interrupt();
			}
		});

		// Start
		resourcesThread.start();
                
	}

	/**
	 * Takes a decision based on the given result
	 */
	public void makeDecision(String speech) throws IOException {
            if(speech.equals("hello")){
                if(isInit==1){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    lblIntro.setVisible(true);
                    textToSpeech.speak("hello my friend, ", 1.5f, false, true);
                    lblIntro.setText("Say How are you !!");
                    lblSkip.setVisible(true);
                    lblIntro.setFont(new java.awt.Font("Script MT Bold", 0, 30));
                    isInit=2;
                }
            }
            if(speech.equals("skip")){
                if(isInit>=2 && isInit<=7){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    
                    textToSpeech.speak("welcome", 1.5f, false, true);
                    lblIntro.setVisible(true);
                    lblIntro.setText("Welcome");
                    lblSkip.setVisible(false);
                    lblPath.setVisible(false);
                    txtPath.setVisible(false);
                    txtName.setVisible(false);
                    isInit=0;
                }
            }
            if(speech.equals("how are you")){
                if(isInit==2){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    textToSpeech.speak("I am fine, help me so that I can know you more. Say okay to move forward", 1.5f, false, true);
                    lblIntro.setText("Say Okay");
                    isInit=3;
                    return;
                }
            }
            if(speech.equals("okay")){
                if(isInit==3){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    lblIntro.setVisible(false);
                    lblPath.setVisible(true);
                    txtPath.setVisible(true);
                    textToSpeech.speak("Paste the path to your music directory. Say done when completed.", 1.5f, false, true);
                    
//                    txtPath.getText(); 
                    
                    isInit=4;
                    return;
                }
            }
            
            if(speech.equals("done")){
                if(isInit==4){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    lf=new ListAllFiles();
                    System.out.println("Path"+txtPath.getText());
                    int check=lf.listFiles(txtPath.getText());
                    if(check==0){
                        isInit=4;
                        textToSpeech.speak("Invalid path or directory. Check it and say done", 1.5f, false, true);
                        return;
                    }
                    else
                    {lblPath.setText("path to your video and movies folder");
                    textToSpeech.speak("Paste the path to your video and movies directory. Say done when completed.", 1.5f, false, true);
                    
                    txtPath.getText();
                    isInit=5;
                    }
                    
                    return;
                }
            }
            if(speech.equals("done")){
                if(isInit==5){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    lf=new ListAllFiles();
                    System.out.println("Path"+txtPath.getText());
                    int check=lf.listFiles(txtPath.getText());
                    if(check==0){
                        isInit=5;
                        textToSpeech.speak("Invalid path or directory. Check it and say done", 1.5f, false, true);
                        return;
                    }
                    else{
                        txtName.setVisible(true);
                        lblPath.setText("Add apps which you frequently open...");
                        txtPath.setText("path of the .exe file");
                        textToSpeech.speak("Now enter the name, and path of the applications, and games which you want me to open for you. Say add another, to add another application, or, say done to finish adding", 1.5f, false, true);
                        
                        txtPath.getText();
                        txtName.getText();
                        isInit=6;
                    }
                    return;
                }
            }
            if(speech.equals("add another")){
                if(isInit==6){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    f=new FileUpdate();
                    f.update("open "+txtName.getText());
                    try{
                        conn=DBConnection.getConnection();
                        PreparedStatement ps=conn.prepareStatement("insert into speech values (?,?)");
                        ps.setString(2, "open "+txtName.getText());
                        ps.setString(1, txtPath.getText());
                        int ans=ps.executeUpdate();
                        if(ans==1)
                        {
                            JOptionPane.showMessageDialog(null,"Inserted Successfully to database","Success",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch(SQLException ex){
                        ex.printStackTrace();
                    }
                    
                    txtPath.setText("path of the .exe file");
                    txtName.setText("Name you will use to open the application");
                    textToSpeech.speak("Say add another, to add another application, or, say done to finish adding", 1.5f, false, true);
                    
                    txtPath.getText();
                    isInit=6;
                    return;
                }
            }
            
            if(speech.equals("done")){
                if(isInit==6){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    f=new FileUpdate();
                    f.update("open "+txtName.getText());
                    try{
                        conn=DBConnection.getConnection();
                        PreparedStatement ps=conn.prepareStatement("insert into speech values (?,?)");
                        ps.setString(2, "open "+txtName.getText());
                        ps.setString(1, txtPath.getText());
                        int ans=ps.executeUpdate();
                        if(ans==1)
                        {
                            JOptionPane.showMessageDialog(null,"Inserted Successfully to database","Success",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch(SQLException ex){
                        ex.printStackTrace();
                    }
                    txtName.setVisible(false);
                    lblPath.setText("City Name..");
                    txtPath.setText("name of the city");
                    textToSpeech.speak("Name of the city where you live. Say done when finished", 1.5f, false, true);
                    
                    txtPath.getText();
                    isInit=7;
                    return;
                }
            }
            
            if(speech.equals("done")){
                if(isInit==7){
                   lblSpeech.setText(speech);
                    checkInput=1;
                    try{
                        conn=DBConnection.getConnection();
                        PreparedStatement ps=conn.prepareStatement("insert into speech values (?,?)");
                        ps.setString(2, "city");
                        ps.setString(1, txtPath.getText());
                        int ans=ps.executeUpdate();
                        if(ans==1)
                        {
                            JOptionPane.showMessageDialog(null,"Inserted Successfully to database","Success",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch(SQLException ex){
                        ex.printStackTrace();
                    }
                    lblPath.setVisible(false);
                    txtPath.setVisible(false);
                    lblIntro.setText("Welcome");
                    lblIntro.setVisible(true);
                    textToSpeech.speak("You can now start using the app. You can now search the internet, find meanings, play songs and videos, set remiders, know the time, open apps, perform mathematical calculations and a lot more.", 1.5f, false, true);
                    JOptionPane.showMessageDialog(null,"Search internet -> Say 'search' and write\n Find meaning-> say 'find meaning' \n Play song-> Say 'play <song_name>'","Success",JOptionPane.INFORMATION_MESSAGE);
                    
                    isInit=0;
                    return;
                    
                }
            }
            
            //here we are connecting to the database for general questions and their answers
            try{
                conn=DBConnection.getConnection();
                Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                speech=speech.replaceAll(" ", "_").toLowerCase();
                System.out.println(speech);
                ResultSet rs=st.executeQuery("select reply from speech where question like '%"+speech+"%'");
                String check="pp";
                if(speech.length()>=4)
                    check=speech.substring(0, 4);
                if(check.equalsIgnoreCase("open"))
                {
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    rs.next();
                    
                    textToSpeech.speak("Opening", 1.5f, false, true);
                    runtime = Runtime.getRuntime();     //getting Runtime object
		     try
		     {
		        runtime.exec(rs.getString("reply"));        //opens new notepad instance
                         
		        //OR runtime.exec("notepad");
		     }
		     catch (IOException e)
		     {
		         e.printStackTrace();
		     }
                     return;
                }
                
                
                else if(check.equalsIgnoreCase("play")){
//                    rs.next();
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak("Playing ", 1.5f, false, true);
                        runtime = Runtime.getRuntime();     //getting Runtime object
                        System.out.println("pp1"+rs.next());
                        System.out.println("Link: "+rs.getString("reply"));
                        System.out.println("pp2");
                        String[] s = new String[] {"C:\\Program Files\\Windows Media Player\\wmplayer", rs.getString("reply")};

                        try
                        {
                            runtime.exec(s);        
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return;
                }
                else{
                    int size=0;
                    if(rs!=null){
                        rs.last();
                        size=rs.getRow();
                    }
                    if(size>1)
                        textToSpeech.speak("I have got "+size+" answers for your question.", 1.5f, false, true);
                    rs.beforeFirst();
                    while(rs.next()){
                        lblSpeech.setText(speech);
                        checkInput=1;
                        textToSpeech.speak(rs.getString("reply"), 1.5f, false, true);
                    }
                }
            }
            catch(SQLException ex){
                System.out.println("SQLException"+ex);
                ex.printStackTrace();
            }
            
            //db check complete, if not found , will come here
            speech=speech.replaceAll("_", " ").toLowerCase();
		if ("see you".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak("You too , my friend", 1.5f, false, true);
			return;
		}/*
                else if ("thirty seconds".equalsIgnoreCase(speech)){
                        textToSpeech.speak("Timer scheduled for 30 seconds", 1.5f, false, true);
			CrunchifyTimerTaskExample time=new CrunchifyTimerTaskExample();
               		//System.out.format("Task scheduled.. Now wait for 5 sec to see next message..%n");
                        textToSpeech.speak("Timer for 30 seconds has ended", 1.5f, false, true);
			return;
		}
		
		*/
                
		else if ("what day is today".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak(df.format(dateobj), 1.5f, false, true);
                    return;
		}
                else if ("what is the time".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak(java.time.LocalTime.now().toString().substring(0, 8), 1.5f, false, true);
                    return;
		}
                else if ("exit".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak("Take care", 1.5f, false, true);
                    DBConnection.closeConnection();
                    System.exit(0);
                    return;
		}
                else if ("what is the temperature".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak("Rainy season, 25 degree celcius" , 1.5f, false, true);
                    return;
		}
                else if ("send mail".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtPath.setVisible(true);
                    txtName.setVisible(true);
                    txtPath.setText("Recipient:");
                    txtName.setText("Subject:");
                    txtSearch.setText("Body: ");
                    
                    btnViewMore.setText("Send");
                    btnViewMore.setVisible(true);
                    txtSearch.setEditable(true);
                    txtSearch.setBackground(Color.white);
                    textToSpeech.speak("type the mail address, subject and body of the email" , 1.5f, false, true);
                    return;
		}
                else if ("open chrome".equalsIgnoreCase(speech) || "open browser".equalsIgnoreCase(speech) ){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak("Opening Google Chrome", 1.5f, false, true);
                    runtime = Runtime.getRuntime();     //getting Runtime object
                    String[] s = new String[] {"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", "https://google.com/"};
                    try
                    {
                        runtime.exec(s);        
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                        return;
		}
                else if ("open you tube".equalsIgnoreCase(speech) ){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak("Opening Google Chrome", 1.5f, false, true);
                    runtime = Runtime.getRuntime();     //getting Runtime object
                    String[] s = new String[] {"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", "https://youtube.com/"};
                    try
                    {
                        runtime.exec(s);        
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                        return;
		}
                else if ("browse".equalsIgnoreCase(speech) || "search inter net".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    lblPath.setVisible(true);
                    txtPath.setVisible(true);
                    txtPath.setText(" ");
                    btnSearch.setVisible(true);
                    txtName.setVisible(false);
                    txtSearch.setText(" ");
                    btnViewMore.setText("view more...");
                    btnViewMore.setVisible(false);
                    lblPath.setText("Type what you want to search !");
                    textToSpeech.speak("Type in the box what you want to search and click the search button", 1.5f, false, true);
                    
                    return;
                       
		}
                else if ("find meaning".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    lblPath.setVisible(true);
                    txtPath.setVisible(true);
                    txtPath.setText(" ");
                    btnSearch.setVisible(true);
                    txtName.setVisible(false);
                    txtSearch.setText(" ");
                    btnViewMore.setText("view more...");
                    btnViewMore.setVisible(false);
                    lblPath.setText("Enter the word ");
                    textToSpeech.speak("Type the word in the box whose meaning you want to find and click the search button", 1.5f, false, true);
                    
                    return;
                       
		}
                else if ("search live cricket score".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak("Opening Google Chrome", 1.5f, false, true);
                    runtime = Runtime.getRuntime();     //getting Runtime object
                    String[] s = new String[] {"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", "https://google.com/search?h1=en&q=live+cricket+score&btnG=Google+Search"};

                        try
                        {
                            runtime.exec(s);        
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return;
		}
                else if ("play shot me down".equalsIgnoreCase(speech)){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    textToSpeech.speak("Playing song", 1.5f, false, true);
                        runtime = Runtime.getRuntime();     //getting Runtime object
 
                        String[] s = new String[] {"C:\\Program Files\\Windows Media Player\\wmplayer", "D:\\music\\new songs\\shotmedown.mp3"};

                        try
                        {
                            runtime.exec(s);        
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return;
		}

                //THE CALCULATOR
                if(speech.contains("calculate")){
                    lblSpeech.setText(speech);
                    checkInput=1;
                    txtSearch.setText(" ");
                    btnViewMore.setVisible(false);
                    //split the sentence
                    String[] array = speech.split(" ");
                

                    // return if user said only one number
                    if (array.length != 4)
                            return;

                    // Find the two numbers
                    int number1 = stringToNumber.convert(array[1]);
                    int number2 = stringToNumber.convert(array[3]);

                    // Calculation result in int representation
                    int calculationResult = 0;
                    String symbol = "?";

                    // Find the mathematical symbol
                    if ("plus".equals(array[2])) {
                            calculationResult = number1 + number2;
                            symbol = "+";
                    } else if ("minus".equals(array[2])) {
                            calculationResult = number1 - number2;
                            symbol = "-";
                    } else if ("multiply".equals(array[2])) {
                            calculationResult = number1 * number2;
                            symbol = "*";
                    } else if ("division".equals(array[2])) {
                            calculationResult = number1 / number2;
                            symbol = "/";
                    }

                    String res = numberToString.convert(Math.abs(calculationResult));

                    // With words
                    System.out.println("Said:[ " + speech + " ]\n\t\t which after calculation is:[ "
                                    + (calculationResult >= 0 ? "" : "minus ") + res + " ] \n");

                    // With numbers and math
                    System.out.println("Said:[ " + number1 + " " + symbol + " " + number2 + "]\n\t\t which after calculation is:[ "
                                    + calculationResult + " ]");

                    // Speak Mary Speak
                    textToSpeech.speak((calculationResult >= 0 ? "" : "minus ") + res, 1.5f, false, true);
                }
                
                if(checkInput==0){
                    
                    lblSpeech.setText("Please Repeat !!");
                }
	}

    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel1 = new javax.swing.JPanel();
        jTrain = new javax.swing.JButton();
        lblSpeech = new javax.swing.JLabel();
        lblIntro = new javax.swing.JLabel();
        txtPath = new javax.swing.JTextField();
        lblPath = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(350, 0), new java.awt.Dimension(350, 0), new java.awt.Dimension(350, 32767));
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblSkip = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSearch = new javax.swing.JTextArea();
        btnViewMore = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setForeground(new java.awt.Color(0, 153, 255));

        jTrain.setBackground(new java.awt.Color(0, 0, 0));
        jTrain.setIcon(new javax.swing.ImageIcon(getClass().getResource("/model/menu-button-of-three-horizontal-lines (1).png"))); // NOI18N
        jTrain.setContentAreaFilled(false);
        jTrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTrainActionPerformed(evt);
            }
        });

        lblSpeech.setForeground(new java.awt.Color(0, 153, 255));
        lblSpeech.setText("Speak Out Something....");

        lblIntro.setFont(new java.awt.Font("Script MT Bold", 0, 36)); // NOI18N
        lblIntro.setForeground(new java.awt.Color(0, 153, 255));
        lblIntro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIntro.setText("Say Hello !!");

        txtPath.setText("Path...");
        txtPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPathActionPerformed(evt);
            }
        });

        lblPath.setForeground(new java.awt.Color(0, 153, 255));
        lblPath.setText("Path to your music folder");

        txtName.setText("Name you will use to open the application");
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/model/home.gif"))); // NOI18N

        lblSkip.setForeground(new java.awt.Color(0, 153, 255));
        lblSkip.setText("To skip initialization, say Skip !!");

        btnSearch.setBackground(new java.awt.Color(0, 0, 0));
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/model/magnifier.png"))); // NOI18N
        btnSearch.setBorder(null);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        txtSearch.setEditable(false);
        txtSearch.setBackground(new java.awt.Color(0, 0, 0));
        txtSearch.setColumns(30);
        txtSearch.setForeground(new java.awt.Color(0, 153, 255));
        txtSearch.setRows(5);
        txtSearch.setBorder(null);
        jScrollPane1.setViewportView(txtSearch);

        btnViewMore.setBackground(new java.awt.Color(0, 0, 0));
        btnViewMore.setForeground(new java.awt.Color(0, 153, 255));
        btnViewMore.setText("view more...");
        btnViewMore.setContentAreaFilled(false);
        btnViewMore.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewMore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewMoreActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(new java.awt.Color(51, 102, 255));
        jSeparator1.setForeground(new java.awt.Color(0, 102, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnViewMore, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblPath, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSkip)
                            .addComponent(lblIntro, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPath))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblSpeech, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTrain, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIntro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtPath))
                .addGap(18, 18, 18)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnViewMore)
                .addGap(5, 5, 5)
                .addComponent(lblSkip)
                .addGap(0, 0, 0)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTrain)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(lblSpeech)))
                .addGap(13, 13, 13))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTrainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTrainActionPerformed
        
        
        
        TrainBot tb=new TrainBot();
        tb.setVisible(true);
    }//GEN-LAST:event_jTrainActionPerformed

    private void txtPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPathActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
                lblPath.setVisible(false);
                txtPath.setVisible(false);
                btnSearch.setVisible(false);
		txtSearch.setVisible(true);
                txtSearch.setLineWrap(true);
                btnViewMore.setVisible(true);
                System.out.println("Please enter the search term.");
		String searchTerm = txtPath.getText();
//		System.out.println("Please enter the number of results. Example: 5 10 20");
		int num = 1;
		
		
		String searchURL = GOOGLE_SEARCH_URL + "?q="+searchTerm+"&num="+num;
		//without proper User-Agent, we will get 403 error
		Document doc=null;
                try {
                    doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
                } catch (IOException ex) {
                    Logger.getLogger(Main1.class.getName()).log(Level.SEVERE, null, ex);
                }
		
		
                Elements results = doc.select("span.st");
                int count=0;
                link=new String[2];
                resourcesThread.suspend();
                String linkText=" ";
		for (Element result : results) {
                        count++;
//			String linkHref = result.attr("href"); use it for doc.select
                        link[count]=result.firstElementSibling().parent().parent().getElementsByTag("h3").select("a").attr("href");
			linkText = result.text();
                        String URL=link[count].substring(7, link[count].indexOf("&"));
                        
                        if(count==1){
                            
                            
                            
                            txtSearch.setText(linkText);
                        }
                        
//                        
                        
//			System.out.println("Text::" + linkText + ", URL::" + linkHref.substring(6, linkHref.indexOf("&")));
                        System.out.println("Text::" + linkText + ", URL::"+ link[count].substring(7, link[count].indexOf("&")));
                        
		}
                textToSpeech.speak(linkText, 1.5f, false, true);
                resourcesThread.resume();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void btnViewMoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewMoreActionPerformed
        if(btnViewMore.getText().equals("view more...")){
            runtime = Runtime.getRuntime();     //getting Runtime object

            String[] s = new String[] {"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", link[1].substring(7, link[1].indexOf("&"))};
            try{
                runtime.exec(s);        
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }
        else{
            try{
                CheckMail.send("piyushpanjwani12@gmail.com","piyush1112",txtPath.getText(),txtName.getText(),txtSearch.getText());
            JOptionPane.showMessageDialog(null,"Your email to "+txtPath.getText()+" was delivered","E-mail sent",JOptionPane.INFORMATION_MESSAGE);
	    		     //change from, password and to  
            txtPath.setVisible(false);
            txtName.setVisible(false);
            txtPath.setText(" ");
            txtName.setText(" ");
            txtSearch.setText(" ");
            txtSearch.setEditable(false);
            txtSearch.setBackground(Color.black);
            btnViewMore.setText("view more...");
            btnViewMore.setVisible(false);
            
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null,"Your email to "+txtPath.getText()+"was not delivered","Sending failed",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnViewMoreActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new Main1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewMore;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jTrain;
    private javax.swing.JLabel lblIntro;
    private javax.swing.JLabel lblPath;
    private javax.swing.JLabel lblSkip;
    private javax.swing.JLabel lblSpeech;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPath;
    private javax.swing.JTextArea txtSearch;
    // End of variables declaration//GEN-END:variables
}
