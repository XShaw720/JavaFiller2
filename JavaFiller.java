import java.io.*;
import java.util.*;

class Globals{
	static Scanner s;
	static FileWriter w;
	static String before="";
	static String after="";
	static String between="";
	static TreeMap<Character, Integer> iterators = new TreeMap<>();	//for iterators, handles any character(52 options)
	
	public static void addIterator(char ch, int x){
		iterators.put(ch, x);
	}
	
	public static int getIteratorValue(char ch){	//returns value of iterator and increases it by 1
		int temp=iterators.get(ch);
		iterators.put(ch, temp+1);
		return temp;	
	}
	
	public static boolean isValidIterator(char ch){	//checks whether iterator has been assigned a value
		return iterators.containsKey(ch);
	}
	
	public static void openFiles(String in, String out)throws Exception{	//opens files
		s = new Scanner(new File(in));
		w=new FileWriter(new File(out));	
	}
	
	public static void write(String line, int lineType)throws Exception{	//handles writing the before, after, and between
		switch(lineType){		
			case -2:		//used for a regular line
				writeLine(before);
				writeLine(line);
				writeLine(after);
				if(s.hasNextLine())
					writeLine(between);
				break;
			case -1:		//used for last line from repeated line
				writeLine(before);
				writeLine(line);
				break;
			case 0:			//used for first line from repeated line
				writeLine(line);
				writeLine(after);
				writeLine(between);
				break;
			default:		//used for a line in the middle of a repeated line
				writeLine(before);
				writeLine(line);
				writeLine(after);
				writeLine(between);
		}
	}
	
	public static void writeLine(String line)throws Exception{
		while(line.length()>0){
			switch(line.charAt(0)){
				case '\\':					//handles special characters \n\r\t
					switch(line.charAt(1)){
						case 'n':
							w.write("\n");
							line=line.substring(1);
							break;
						case 'r':
							w.write("\r");
							line=line.substring(1);
							break;
						case 't':
							w.write("\t");
							line=line.substring(1);
							break;
					}
					break;
				case '#':
					char ch=line.charAt(1);
					if(Character.isLetter(ch)){			//prints the value of the iterator
						if(isValidIterator(ch)){
							w.write(getIteratorValue(ch)+"");
							line=line.substring(1);
						}
						else
							w.write("#");
					}
					else if(line.matches("#\\d+<.+>.*")){		//prints repeated lines
						String temp=line.substring(line.indexOf("<")+1, line.indexOf(">"));
						for(int i=0; i<Integer.parseInt(line.substring(1, line.indexOf("<")))-1; i++)
							write(temp, i);
						write(temp, -1);
						line=line.substring(line.indexOf(">"));
					}
					else
						w.write("#");
					break;
				default:
					w.write(line.charAt(0));	//writes character as normal if not special
			}
			line=line.substring(1);
		}
	}
}

public class JavaFiller{
	
	public static void main(String[] args)throws Exception{
		
		String line;		
		
		switch(args.length){		//allows commandline args for i/o files, defaults "input.txt" and "output.txt"
			case 0:
				Globals.openFiles("input.txt", "output.txt");
				break;
			case 1:
				Globals.openFiles(args[0], "output.txt");		//if two files are given the first is used for input and the second for output
				break;
			case 2:
				Globals.openFiles(args[0], args[1]);			//if only one file is given it used as input
				break;
		}			
		
		while(Globals.s.hasNextLine()){
			line=Globals.s.nextLine();
			if(line.matches("#before .*"))		//what between "" will be printed before each line
				Globals.before=line.substring(8);
			
			else if(line.matches("#after .*"))		//whats between "" will be printed after each line
				Globals.after=line.substring(7);
			
			else if(line.matches("#between .*"))	//whats between "" will be printed between each line
				Globals.between=line.substring(9);
			
			else if(line.matches("#print .*"))	//prints whatever's after "$print "
				Globals.writeLine(line.substring(7));
				
			else if(line.matches("#assign [a-zA-Z]=\\d+"))	//allows you to assign a value to an iterator
				Globals.addIterator(line.charAt(8), Integer.parseInt(line.substring(10)));

			else if(line!=""){		//does't print empty lines
				Globals.write(line, -2);
			}
		}	
		Globals.s.close();
		Globals.w.close();
	}
}	