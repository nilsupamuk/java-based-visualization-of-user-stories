package tekDersProje;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;


import java.util.*;
import javax.swing.*;


import java.io.File;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;


public class Main extends JFrame {
	
	
	int width = 30;
    int height = 30;

    ArrayList<Node> nodes = new ArrayList<Node>();;
    ArrayList<edge> edges = new ArrayList<edge>();;

    
    public Main(String name) { 
	this.setTitle(name);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    class Node {
	int x, y;
	String name;
	
	public Node(String myName, int myX, int myY) {
	    x = myX;
	    y = myY;
	    name = myName;
	}
    }
    
    class edge {
	int i,j;
	
	public edge(int ii, int jj) {
	    i = ii;
	    j = jj;	    
	}
    }
    
  //add a node at pixel (x,y)
    public void addNode(String name, int x, int y) { 
	
    	nodes.add(new Node(name,x,y));
    	this.repaint();
    }
    
  //add an edge between nodes i and j
    public void addEdge(int i, int j) {
	
    	edges.add(new edge(i,j));
    	this.repaint();
    }
    
 // draw the nodes and edges
    public void paint(Graphics g) { 
	FontMetrics f = g.getFontMetrics();
	int nodeHeight = Math.max(height, f.getHeight());

	g.setColor(Color.black);
	for (edge e : edges) {
	    g.drawLine(nodes.get(e.i).x, nodes.get(e.i).y,
		     nodes.get(e.j).x, nodes.get(e.j).y);
	}

	for (Node n : nodes) {
	    int nodeWidth = Math.max(width, f.stringWidth(n.name)+width/2);
	    g.setColor(Color.white);
	    g.fillOval(n.x-nodeWidth/2, n.y-nodeHeight/2, 
		       nodeWidth, nodeHeight);
	    g.setColor(Color.black);
	    g.drawOval(n.x-nodeWidth/2, n.y-nodeHeight/2, 
		       nodeWidth, nodeHeight);
	    
	    g.drawString(n.name, n.x-f.stringWidth(n.name)/2,
			 n.y+f.getHeight()/2);
	}
    }

	
	
	
	
	public static void main(String[] args)  throws IOException, InvalidFormatException {
	
		
		//READ THE EXCEL FILE//
		//Get the excel file
		JFileChooser fileChooser = new JFileChooser();
		
	 	fileChooser.showOpenDialog(null);
	 	
	 	String filename = fileChooser.getSelectedFile().getAbsolutePath();
	 	

	 	
	 	// Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(filename));

   
        /*************************************************/
        //Here we should ask the user which sheet he/she wants 
        Sheet sheet = workbook.getSheetAt(1);
      
        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();
        
        //Create vectors for storage of elements
        Vector<String> id = new Vector<String>();
        Vector<String> sentence = new Vector<String>();
        Vector<String> points = new Vector<String>();
        
        Vector<String> actor_first = new Vector<String>();
        
        Vector<String> sentences_short = new Vector<String>();
        Vector<String> sentences_to_parse = new Vector<String>();                 
        Vector<String> actor_to_parse = new Vector<String>();
        
        Vector<String> actor = new Vector<String>();
        Vector<String> verb = new Vector<String>();
        Vector<String> object = new Vector<String>();
        

        Iterator<Row> rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // Now let's iterate over the columns of the current row
            Iterator<Cell> cellIterator = row.cellIterator();
           

            //store the excel cell values in vectors
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String cellValue = dataFormatter.formatCellValue(cell);
                id.add(cellValue);            
                
                cell = cellIterator.next();
                cellValue = dataFormatter.formatCellValue(cell);
                sentence.add(cellValue);
                
                cell = cellIterator.next();
                cellValue = dataFormatter.formatCellValue(cell);
                points.add(cellValue);
               
                
            }
      
        }
	 	
        //SHORTENING THE SENTENCES//
        
       //Some patterns to identify the text
        Pattern asA = Pattern.compile("(As a )|(As an )", Pattern.CASE_INSENSITIVE);
        Pattern comma = Pattern.compile(", ");
        Pattern soThat = Pattern.compile("(, so that)|(,so that)|(, so)|(,so)", Pattern.CASE_INSENSITIVE);
        
        //Read user stories
        for(int i = 0; i < sentence.size() ; i++) {
        	
        	Matcher m = asA.matcher(sentence.get(i));
        	Matcher mComma = comma.matcher(sentence.get(i));
                	
        	//add actors
        	if(m.find()) {

          		mComma.find();
        		actor_first.add(sentence.get(i).substring(m.end(), mComma.start()));

        		
        	}else {
        		
        		id.remove(i);
        		sentence.remove(i);
        		points.remove(i);
        		i--;
        	}
        	
        	
        }
 
        
        //eliminate "so that"
        for(int i = 0; i < sentence.size() ; i++) {
        	Matcher mComma = comma.matcher(sentence.get(i));
        	Matcher mSo = soThat.matcher(sentence.get(i));
        	mComma.find();
        	if(mSo.find()) {
        		
        		sentences_short.add(sentence.get(i).substring(mComma.end(), mSo.start()));
        	       		
        	}else {
        		       		
        		sentences_short.add(sentence.get(i).substring(mComma.end()));
        		
        	}
        	
        }
        
        //if there is a second "as a .., ý want to..." in the sane cell data
        for(int i = 0; i < sentences_short.size() ; i++) {
        	Pattern second = Pattern.compile("(.As a )|(. As a )|(.As an )|(. As an )", Pattern.CASE_INSENSITIVE);
        	Pattern comma2 = Pattern.compile("(, I)|(,I)");
        	
        	Matcher m = second.matcher(sentences_short.get(i));
        	Matcher mComma = comma2.matcher(sentences_short.get(i));
        	
        	
        	if(m.find()) {

        	
        		mComma.find();
        		actor_first.add(sentences_short.get(i).substring(m.end(), mComma.start()));
        	
        		sentences_short.add(sentences_short.get(i).substring(mComma.end()-1));
        		sentences_short.set(i, sentences_short.get(i).substring(0, m.start()));
      
        		//if there is a "so that" afterwards
        		Matcher mSo = soThat.matcher(sentences_short.lastElement());
        		if(mSo.find()){
   
        			sentences_short.set(sentences_short.size()-1, sentences_short.get(i).substring(mComma.end(-1), mSo.start()));
        		
        		}
        		
        	}
        	
        	
        }
        
        //change "i want to"'s to "i" so that the nlp can read the desired verb
        for(int i = 0; i < sentences_short.size() ; i++) {
        	
        	Pattern want = Pattern.compile("(I want to) ", Pattern.CASE_INSENSITIVE);
        	Pattern dontWant = Pattern.compile("(I don't want to)|(I dont want to)|(I do not want to)", Pattern.CASE_INSENSITIVE);
        	Pattern able = Pattern.compile("(I want to be able to) ", Pattern.CASE_INSENSITIVE);
        	Pattern wantX= Pattern.compile("(I want) ", Pattern.CASE_INSENSITIVE);
        	
        	Matcher mWant = want.matcher(sentences_short.get(i));
        	Matcher mDontWant = dontWant.matcher(sentences_short.get(i));
        	Matcher mAble = able.matcher(sentences_short.get(i));
        	Matcher mWantX = wantX.matcher(sentences_short.get(i));
        	
        	boolean isWant = mWant.find();
        	boolean isDontWant = mDontWant.find();
        	boolean isAble = mAble.find();
        	boolean isWantX = mWantX.find();
        	
        	String str = sentences_short.get(i);
        	String str_new = "";
        	
        	
        	if(isWant && !isAble) {
        		str_new = str.substring(9);
        		str_new = "I " + str_new;
        		sentences_to_parse.add(str_new);
        		actor_to_parse.add(actor_first.get(i));
        		
        		
        	}else if(isAble) {
        		str_new = str.substring(21);
        		str_new = "I " + str_new;
        		sentences_to_parse.add(str_new);
        		actor_to_parse.add(actor_first.get(i));
        	
        		
        	}else if(isDontWant) {
        		//Eliminate this type of sentence 
        		
        	}else if(isWantX) {
        		//Eliminate this type of sentence    				
        	}
        	
        	
        }
        
        
        //NATURAL LANGUAGE PROCESSING//
     
        for(int i = 0; i < sentences_to_parse.size(); i++) {
        	
        	String sen = sentences_to_parse.get(i);
            InputStream tokenModelIn = null;
            InputStream posModelIn = null;
            
            try {
                
                // tokenize the sentence
                tokenModelIn = new FileInputStream("en-token.bin");
                TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
                Tokenizer tokenizer = new TokenizerME(tokenModel);
                String tokens[] = tokenizer.tokenize(sen);
     
                // Parts-Of-Speech Tagging
                // reading parts-of-speech model to a stream
                posModelIn = new FileInputStream("en-pos-maxent.bin");
                // loading the parts-of-speech model from stream
                POSModel posModel = new POSModel(posModelIn);
                // initializing the parts-of-speech tagger with model
                POSTaggerME posTagger = new POSTaggerME(posModel);
                // Tagger tagging the tokens
                String tags[] = posTagger.tag(tokens);
               
          
               String a = actor_to_parse.get(i);
               String v = "";
               String o = "";
               Boolean approved = false;
                
                Boolean cont = true;
                Boolean hasNextNN = false;
                int NNcount = 0;
               
               
                if(tokens.length > 2) {
                	
                    if(tokens[1].equalsIgnoreCase("be")||
                    		tokens[1].equalsIgnoreCase("prevent")||
                    		tokens[1].equalsIgnoreCase("ensure") ) {
                    	
                    	//do nothing, these verbs are eliminated
                    	
                    }else if(!(tags[1].equalsIgnoreCase("VB")||tags[1].equalsIgnoreCase("VBP"))
                    		&& tags[2].equalsIgnoreCase("CC") && 
                    		(tags[3].equalsIgnoreCase("VB")||tags[3].equalsIgnoreCase("VBP"))) {
                    	
                    	//get verb
                    	v = tokens[3];
                    	
                    	//get object
                    	for(int j=3;j<tags.length;j++){
                   
                    		if((tags[j].equalsIgnoreCase("NN")|| 
                    				tags[j].equalsIgnoreCase("NNP")||	
                    				tags[j].equalsIgnoreCase("NNS")) && cont){
                    			
                    			
                    			NNcount ++;
                    			if(hasNextNN) {
                    				o = o + " " + tokens[j];
                    				
                    			}else {
                    				o = tokens[j];
                    			}
                    			
                    			
                    			if( (j+1 < tags.length) && 
                    					((tags[j+1].equalsIgnoreCase("NN")|| 
                        				tags[j+1].equalsIgnoreCase("NNP")||	
                        				tags[j+1].equalsIgnoreCase("NNS")))) {
                    				hasNextNN = true;
                    			}else{
                    				hasNextNN = false;
                    			}
                    			
                    			if(NNcount > 0 && !hasNextNN) {
                    				cont = false;
                    			}
                    			
                    		}	                    		
                    	}
                    	
                    	if(NNcount>0) {
                    		approved = true;
                    	}
                    	
                    	
                    }else if(!(tags[1].equalsIgnoreCase("VB")||tags[1].equalsIgnoreCase("VBP"))) {
                    	
                    	//do nothing
                    	
                    }else if(tokens[1].equalsIgnoreCase("help") && 
                    		(tags[2].equalsIgnoreCase("VB")||tags[2].equalsIgnoreCase("VBP"))) {
                    	
                    	//get verb
                    	v = tokens[2];
                    	
                    	//get object
                    	for(int j=2;j<tags.length;j++){
                    		
                    		if((tags[j].equalsIgnoreCase("NN")|| 
                    				tags[j].equalsIgnoreCase("NNP")||	
                    				tags[j].equalsIgnoreCase("NNS")) && cont){
                    			
                    			
                    			NNcount ++;
                    			if(hasNextNN) {
                    				o = o + " " + tokens[j];
                    				
                    			}else {
                    				o = tokens[j];
                    			}
                    			
                    			
                    			if( (j+1 < tags.length) && 
                    					((tags[j+1].equalsIgnoreCase("NN")|| 
                        				tags[j+1].equalsIgnoreCase("NNP")||	
                        				tags[j+1].equalsIgnoreCase("NNS")))) {
                    				hasNextNN = true;
                    			}else{
                    				hasNextNN = false;
                    			}
                    			
                    			if(NNcount > 0 && !hasNextNN) {
                    				cont = false;
                    			}
                    			
                    		}	                    		
                    	}
                    	
                    	if(NNcount>0) {
                    		approved = true;
                    	}
                    	
                    	
                    }else if((tags[1].equalsIgnoreCase("VB")||tags[1].equalsIgnoreCase("VBP"))
                    		&& tags[2].equalsIgnoreCase("IN")) {
                    	
                    	v = tokens[1] + " " +tokens[2];
                    	
                    	//get object
                    	for(int j=2;j<tags.length;j++){
                    		
                    		if((tags[j].equalsIgnoreCase("NN")|| 
                    				tags[j].equalsIgnoreCase("NNP")||	
                    				tags[j].equalsIgnoreCase("NNS")) && cont){
                    			
                    			
                    			NNcount ++;
                    			if(hasNextNN) {
                    				o = o + " " + tokens[j];
                    				
                    			}else {
                    				o = tokens[j];
                    			}
                    			
                    			
                    			if( (j+1 < tags.length) && 
                    					((tags[j+1].equalsIgnoreCase("NN")|| 
                        				tags[j+1].equalsIgnoreCase("NNP")||	
                        				tags[j+1].equalsIgnoreCase("NNS")))) {
                    				hasNextNN = true;
                    			}else{
                    				hasNextNN = false;
                    			}
                    			
                    			if(NNcount > 0 && !hasNextNN) {
                    				cont = false;
                    			}
                    			
                    		}	                    		
                    	}
                    	
                    	if(NNcount>0) {
                    		approved = true;
                    	}
                    	
                    	
                    }else if((tags[1].equalsIgnoreCase("VB")||tags[1].equalsIgnoreCase("VBP"))
                    		&& tags[2].equalsIgnoreCase("TO")) {
                    	
                    	//get verb
                    	v = tokens[1] + " " +tokens[2]; 	
                    	
                    	//get object
                    	for(int j=2;j<tags.length;j++){
                    		
                    		if((tags[j].equalsIgnoreCase("NN")|| 
                    				tags[j].equalsIgnoreCase("NNP")||	
                    				tags[j].equalsIgnoreCase("NNS")) && cont){
                    			
                    			NNcount ++;
                    			if(hasNextNN) {
                    				o = o + " " + tokens[j];
                    				
                    			}else {
                    				o = tokens[j];
                    			}
                    
                    			
                    			if( (j+1 < tags.length) && 
                    					((tags[j+1].equalsIgnoreCase("NN")|| 
                        				tags[j+1].equalsIgnoreCase("NNP")||	
                        				tags[j+1].equalsIgnoreCase("NNS")))) {
                    				hasNextNN = true;
                    			}else{
                    				hasNextNN = false;
                    			}
                    			
                    			if(NNcount > 0 && !hasNextNN) {
                    				cont = false;
                    			}
                    			
                    		}	                    		
                    	}
                    	
                    	if(NNcount>0) {
                    		approved = true;
                    	}
                    	
                    	
                    }else if((tags[1].equalsIgnoreCase("VB")||tags[1].equalsIgnoreCase("VBP"))){
                    	
                    	//get verb
                     	v = tokens[1];
                     	
                     	//get object
                     	for(int j=1;j<tags.length;j++){
                    		
                    		if((tags[j].equalsIgnoreCase("NN")|| 
                    				tags[j].equalsIgnoreCase("NNP")||	
                    				tags[j].equalsIgnoreCase("NNS")) && cont){
                    			
                    			
                    			NNcount ++;
                    			if(hasNextNN) {
                    				o = o + " " + tokens[j];
                    				
                    			}else {
                    				o = tokens[j];
                    			}
                    			
                    			if( (j+1 < tags.length) && 
                    					((tags[j+1].equalsIgnoreCase("NN")|| 
                        				tags[j+1].equalsIgnoreCase("NNP")||	
                        				tags[j+1].equalsIgnoreCase("NNS")))) {
                    				hasNextNN = true;
                    			}else{
                    				hasNextNN = false;
                    			}
                    			
                    			if(NNcount > 0 && !hasNextNN) {
                    				cont = false;
                    			}
                    			
                    		}	                    		
                    	}
                    	
                    	if(NNcount>0) {
                    		approved = true;
                    	}
                                        	
                     	
                    }
                    
                    
                    
                    if(approved) {
                    	actor.add(a.toLowerCase());
                    	verb.add(v.toLowerCase());
                    	object.add(o.toLowerCase());
                    	//this commented code will print the actor, verb and object of user stories
                    	//System.out.println(actor.lastElement()+" > "+verb.lastElement()+" > "+object.lastElement());
                    	approved = false;	
                    }
                                            
                    
                }
               
                
            }
            catch (IOException e) {
                // Model loading failed, handle the error
                e.printStackTrace();
            }
            finally {
                if (tokenModelIn != null) {
                    try {
                        tokenModelIn.close();
                    }
                    catch (IOException e) {
                    }
                }
                if (posModelIn != null) {
                    try {
                        posModelIn.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
    	
        	
        }
        
        //GRAPHICS//
        
        //START: WordCloud
        
        int w = actor.size();
        String[] words= new String[w*3];
       
        for(int i = 0; i< w; i++) {
        	
        	words[i] = actor.get(i);
        	words[i+w] = verb.get(i);
        	words[i+(w*2)] = object.get(i);
        	
        }
        
      
       
        JFrame frame = new JFrame("WordCloud");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        Cloud cloud = new Cloud();
       
        for (String s : words) {
        	cloud.addTag(s);  
        }
       
        
        for (Tag tag : cloud.tags()) {
            final JLabel label = new JLabel(tag.getName());
            label.setOpaque(false);       
            label.setFont(label.getFont().deriveFont((float) tag.getWeight()*30 ));
           
            panel.add(label);
        }
       
        frame.add(panel);
        frame.setSize(1000, 700);
        frame.setVisible(true);
        
        //END: WordCloud//
        
        //START: Tree//
        
        Main frame2 = new Main("Tree");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        String[][] elements = new String[3][w];
        int[][] nodenums = new int[3][w];
        int nodecounter = 0;
      //node zero
        frame2.addNode("actor", 50,300);
        nodecounter++;
        
        for(int i = 0; i< w; i++) {
        	
        	elements[0][i] = actor.get(i);
        	elements[1][i] = verb.get(i);
        	elements[2][i] = object.get(i);
        	
        }
        
        //sort the lists
        for(int i=1; i < w; i++){  
            for(int j=0; j < (w-1); j++){  
            	if(elements[0][j].compareTo(elements[0][j+1])>0){  
                            //swap elements  
                            String temp = elements[0][j];  
                            elements[0][j] = elements[0][j+1];  
                            elements[0][j+1] = temp;  
                            temp = elements[1][j];  
                            elements[1][j] = elements[1][j+1];  
                            elements[1][j+1] = temp; 
                            temp = elements[2][j];  
                            elements[2][j] = elements[2][j+1];  
                            elements[2][j+1] = temp;
                    }  
                     
            }  
        } 
        
        for(int i=1; i < w; i++){  
            for(int j=0; j < (w-1); j++){  
            	if( elements[0][j].equals(elements[0][j+1]) && elements[1][j].compareTo(elements[1][j+1])>0){  
                            //swap elements  
                            String temp = elements[1][j];  
                            elements[1][j] = elements[1][j+1];  
                            elements[1][j+1] = temp; 
                            temp = elements[2][j];  
                            elements[2][j] = elements[2][j+1];  
                            elements[2][j+1] = temp;
                    }  
                     
            }  
        } 
        
        
        frame2.addNode(elements[0][0], 300,50);
        nodecounter++; 
        nodenums[0][0] = nodecounter-1;
        
        frame2.addNode(elements[1][0], 600, 50);
        nodecounter++;   
        nodenums[1][0] = nodecounter-1;
        
        frame2.addNode(elements[2][0], 900, 50);
        nodecounter++;        
        nodenums[2][0] = nodecounter-1;
        
        frame2.addEdge(0, nodenums[0][0]);
        frame2.addEdge(nodenums[0][0], nodenums[1][0]);
        frame2.addEdge(nodenums[1][0], nodenums[2][0]);
        
       
        //draw the tree
        //objects
        int dif = (1000/w); 
        int distance = 50;
        for(int i=1; i < w; i++){  
        	
        	distance = distance + dif;
    		frame2.addNode(elements[2][i], 900, distance);
    		nodecounter++;
    		nodenums[2][i] = nodecounter-1;   	
                 
        } 
        //verb
        distance = 50;
        dif = (1200/w);
        for(int i=1; i < w; i++){  
        	if(elements[1][i].equals(elements[1][i-1]) && elements[0][i].equals(elements[0][i-1]) ) {
        		nodenums[1][i] = nodenums[1][i-1];
        	}else{
        		distance = distance + dif;
    			frame2.addNode(elements[1][i], 600, distance);
    			nodecounter++;
    			nodenums[1][i] = nodecounter-1;
    			
        	}
                 
        } 
        //actor
        distance = 50;
        dif = (3000/w);
        for(int i=1; i < w; i++){  
        	if(elements[0][i].equals(elements[0][i-1])) {
        		nodenums[0][i] = nodenums[0][i-1];
        	}else{
        		distance = distance + dif;
    			frame2.addNode(elements[0][i], 300, distance);
    			nodecounter++;
    			nodenums[0][i] = nodecounter-1;
    			
        	}
                 
        }  
        //actor
        for(int j=1; j < w; j++){ 
    		if(nodenums[0][j-1] == (nodenums[0][j])) {
    			
    		}else{
    			frame2.addEdge(0, nodenums[0][j]);
    		}
    	}
        //verb
        for(int j=1; j < w; j++){ 
        	
        	if(nodenums[1][j-1] == (nodenums[1][j])) {
    			
    		}else{
    			frame2.addEdge(nodenums[0][j], nodenums[1][j]);
    		}
    		
    		
    		
    	}  
        //object
        for(int j=1; j < w; j++){ 
        	frame2.addEdge(nodenums[1][j], nodenums[2][j]);
        }
     
        
        JScrollPane pane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
        frame2.getContentPane().setBackground( Color.white );
        frame2.setContentPane(pane);
                
        frame2.addEdge(0, nodenums[0][0]);
        frame2.addEdge(nodenums[0][0], nodenums[1][0]);
        frame2.addEdge(nodenums[1][0], nodenums[2][0]);
       
       
        frame2.setSize(1000, 700);
        frame2.setVisible(true);
        
    
        workbook.close();
	 	
	}
	
	

}
