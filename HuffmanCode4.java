import java.io.*;
import java.util.*;

//Makes use of the Huffman Coding algorithm created by David Huffman to compress
//files into binary representations using fewer binary digits. Second part of
//the program allows for the user to decompress the file which converts the
//binary pattern into the original text file. In order to compress a file, the
//user simply needs to provide a valid file. To decompress a file, the user
//needs to provide the .short file containing the shortened binary pattern and
//the .code file which contains the method by which the program should convert
//binary into the original characters. The .code file must be in appropriate
//format written in pairs: the first line will include the ASCII representation
//of a particular character. This should be followed by another line which
//includes the binary code that represents the character. User has four options:
//generate .code; compress file and generate .short; decompress and generate
//.new; compress and decompress creating all three files. The other method is
//by passing in a frequency array.
public class HuffmanCode4{

    private HuffmanNode4 root;

    //int[] frequencies parameter must be passed in. Assumption is that
    //frequencies is not empty or does not contain an array of 255 0s or
    //HuffmanCode4 will create a blank .code file.
    //Creates a Huffman encoding format based on array passed in as the argument
    //The array passed in to the function should contain frequencies of the 256
    //ascii character starting with index 0. The index of the array is the
    //ascii character itself and the value at the index is the frequency.
    //This constructor only works for ascii characters since other styles
    //of representing characters are not handled in this program. Therefore,
    //only encodings of 8 bits will work on this program.
    public HuffmanCode4(int[] frequencies){
        Queue<HuffmanNode4> charHolder = new PriorityQueue<HuffmanNode4>();
        for(int i = 0; i < frequencies.length; i++){
            if(frequencies[i] != 0){
                HuffmanNode4 letter = new HuffmanNode4(i, frequencies[i]);
                charHolder.add(letter);
            }
        }
        this.root = treeConvert(charHolder);
    }

    //Using the PriorityQueue charHolder passed in, a tree is constructed that
    //represents Huffman encoding. Critical: the queue passed in must be a
    //PriorityQueue since it is based on the frequency and must be a tree
    //based in the correct order. The tree for huffman encoding
    //is done by removing the first two nodes (least frequent) from charHolder
    //and creating a Huffman node which has the left child as the first removal
    //and the right child as the second removal. This process continues until
    //the priority queue holds the entire tree containing every character
    //in the file in frequency order. In each loop, the new node will be
    //created with ascii character value 256 (outside bounds of ascii) and the
    //frequency will be the summation of the frequencies of the two nodes
    //removed from the queue.
    //tempRoot is returned which after the loop is now the complete tree
    //representing all characters within the file passed in by the user.
    //Parameter: charholder represents nodes of frequency over 1.
    private HuffmanNode4 treeConvert(Queue<HuffmanNode4> charHolder){
        HuffmanNode4 tempRoot = charHolder.peek();
        while(charHolder.size() > 1){
            HuffmanNode4 holder1 = charHolder.remove();
            HuffmanNode4 holder2 = charHolder.remove();
            int totalFrequency = holder1.frequency + holder2.frequency;
            tempRoot = new HuffmanNode4(256, totalFrequency, holder1, holder2);
            charHolder.add(tempRoot);
        }
        return tempRoot;
    }

    //Assumption is that input is not empty because otherwise the .new
    //will be blank as the Scanner is also blank. Another assumption is that the
    //.code file passed in to the scanner is proper since an improper file would
    //generate a wrong tree. Scanner must be attached to legal file in standard
    //format.
    //Creates a Huffman encoding format based on the format passed in through
    //the parameter (scanner input). The input should contain character value
    //and then the encoding that is in binary format. This format represents a
    //node for every two lines and is the standard format.
    //Critical: this constructor uses the input Scanner as a valid scanner that
    //is not null and is using the standard Huffman encoding format.
    //Parameter: input is a Scanner that will read in the encoding that
    //will allow the constructor to generate the general coding format.
    public HuffmanCode4(Scanner input){
        while(input.hasNextLine()) {
            String character = input.nextLine();
            String binary = input.nextLine();
            this.root = makeTree(this.root, character, binary);
        }
    }

    //Adds a node to the tree based on the encoding passed in.
    //If the node is null ascii value is 256. The frequency values are no longer
    //applicable since the frequency counts are not available.
    //Using the data passed in, makeTree places the current data in a node in
    //the appropriate position in the binaryTree. If during the tree traversal a
    //node does not exist, a new blank node is created so that the node data
    //can be placed at the correct location. In every recursive call, the
    //curtailed for every left and the tree until the binary
    //is empty in which case the node is created with the appropriate
    //information. Assumption: since the .code file is created properly, the
    //tree will end up balanced. This is because each node will be placed in
    //the correct position after the entire while loop completes and therefore
    //each node will either have two children or none. This is the private part
    //of the public private pair.
    //Parameter: position is the placeholder that allows for proper
    //traversal. character is the data to be inputted to the node. Binary is the
    //encoding for the associated character.
    private HuffmanNode4 makeTree(HuffmanNode4 position, String character,
                                 String binary) {
        if(binary.isEmpty()){
            return new HuffmanNode4(Integer.parseInt(character), 0);
        }else{
            if(position == null){
                position = new HuffmanNode4(256, 0);
            }
            //checking to see if first binary is 0 or 1. 48 is ascii for 0.
            if(binary.charAt(0) == 48){
                position.left = this.makeTree(position.left, character,
                        binary.substring(1));
            }else{
                position.right = this.makeTree(position.right, character,
                        binary.substring(1));
            }
        }
        return position;
    }

    //Parameter: PrintStream output provided should be connected to the file name
    //specified .code. Otherwise, the tree will not be saved. This is the
    //encoding format that will be saved into a file.
    //Using the standard format specified (pre-order), the character data
    //will be printed, followed by a new line in which the steps taken to the
    //node is represented by binary. Every left traversal will result in a 0,
    //while every right traversal will result in a 1. This repeats until the
    //node is found in which case the complete String of the binary is outputted
    //This is the standard format for the Huffman encoding.
    public void save(PrintStream output){
        this.save(this.root, output, "");
    }

    //Parameter: PrintStream is connected to the file name.code specified otherwise
    //output is not saved and code file will be blank. output is the file to
    //which the encoding format will be printed to.
    //Position is the current node that is being recursed through.
    //Binary is the current binary representation being considered. Later calls
    //will add 0 and 1 to the next value.
    //The tree is printed through the PrintStream Output in preorder format.
    //Being the private part in the public private pair, this recursive process
    //uses backtracking to eventually print out all nodes in the standard format
    //to the filename.code. While the node being called is not null, the
    //recursive process checks if the position is a leaf node. If it is, the
    //character ascii value and binary are outputted. Otherwise, the tree is
    //traversed to the left or right to find the leaf node and respectively a
    //"0" or "1" is added to the String binary to account for the traversal.
    //This occurs until finally all leaf nodes are found in which case the
    //entire tree will end up being printed in standard format.
    private void save(HuffmanNode4 position, PrintStream output, String binary){
        if (position != null) {
            if (position.left == null && position.right == null) {
                output.println(position.character);
                output.println(binary);
            }
            this.save(position.left, output, binary + "0");
            this.save(position.right, output, binary + "1");
        }
    }

    //Using the huffman encoding, puts input into format .txt which is a
    //character file before compression took place. The input will only be read
    //if the Huffman Encoding is already made for the particular file and the
    //input is in the correct Huffman Coding format. The method will read
    //individual bits from the input. Then it converts the bits into characters
    //from Huffman encoding format. The method assumes that the input has
    //legal format of the characters.
    //BitInputStream will read in the bit values and create the original file
    //from the decompression based on the values inside  Huffman coding format.
    //PrintStream will be connected to file .new.
    public void translate(BitInputStream input, PrintStream output){
        HuffmanNode4 position = this.root;
        while(input.hasNextBit() ||
                (position.left == null && position.right == null)){
            if(position.left == null && position.right == null){
                output.write(position.character);
                position = this.root;
            }else {
                int bit = input.nextBit();
                if (bit == 0) {
                    position = position.left;
                } else {
                    position = position.right;
                }
            }
        }
    }
    //Implements comparable to allow for comparison between frequency for the
   //PriorityQueue. This class is a binary node and contains states for the left
   //and right node. This class is meant to contain info about ascii character and
   //the frequency of the particular ascii character. Invalid ascii code/empty
   //should be shown with ascii code of 256.
   public class HuffmanNode4 implements Comparable<HuffmanNode4>{
      public final int character;
      public final int frequency;
      public HuffmanNode4 left;
      public HuffmanNode4 right;
   //Initializes a new HuffmanNode4 with character, frequency, left and right.
   //Invalid ascii code should be shown with value of 256. character is the
   //ascii code meant to be assigned while frequency is the frequency of the
   //particular ascii code.
   public HuffmanNode4(int character, int frequency, HuffmanNode4 left,
      HuffmanNode4 right){
      this.character = character;
      this.frequency = frequency;
      this.left = left;
      this.right = right;
       }
   //Initializes a new HuffmanNode4 with character and frequency and setting
   //the left and right fields to null. An invalid ascii code is represented
   //by the ascii code of 256. character is the ascii code meant to be assigned
   //while frequency is the frequency of the particular ascii code.
   public HuffmanNode4(int character, int frequency){
      this(character, frequency, null, null);
   }
   //Compares current Node with another valid HuffmanNode4. This is done by
   //using the frequency data field. This method will return 0 if both nodes
   //have the same frequency, 1 if the current node has greater frequency, and
   //-1 if the other node has the greater frequency. This is done by
   //utilizing the Integer class's compareTo.
   public int compareTo(HuffmanNode4 other) {
      return ((Integer) this.frequency).compareTo(other.frequency);
   }
   }
}