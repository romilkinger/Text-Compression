// Romil Kinger
// Homework # 8
// CSE 143, Section BB with Porter Jones
// 03/14/2019

// This class compresses text files into binary notation and decompresses
// those files in the binary notation back in the text notation.

import java.util.*;
import java.io.*;

public class HuffmanCode {
   private HuffmanNode overallRoot;
   
   // constructs the HuffmanCode object with the given array of frequencies.
   public HuffmanCode(int[] frequencies) {
      Queue<HuffmanNode> priority = new PriorityQueue<HuffmanNode>();
      for (int i = 0; i < frequencies.length; i++) {
         int frequency = frequencies[i];
         if(frequency != 0) {
            char value = (char)i;
            priority.add(new HuffmanNode(value, frequency));
         }
      }
      overallRoot = makeTree(priority);
   }
   
   // creates and returns a HuffmanNode tree from the given priority queue.
   private HuffmanNode makeTree(Queue<HuffmanNode> priority){
      HuffmanNode careTaker = priority.peek();
      char defaultValue = (char)256; // uses value of 256 to create empty nodes
      while (priority.size() > 1) {
         HuffmanNode firstLeft = priority.remove();
         HuffmanNode secondRight = priority.remove();
         int addition = firstLeft.frequency + secondRight.frequency;
         careTaker = new HuffmanNode(defaultValue, addition, firstLeft, secondRight);
         priority.add(careTaker);
      }
      return careTaker;
   }
   
   // constructs a HuffmanCode object from the given Scanner,
   // which reads the file containing encoded ascii values and
   // positioning values of the letters of the text.
   public HuffmanCode(Scanner input) {
      while (input.hasNextLine()) {
         int asciiValue = Integer.parseInt(input.nextLine());
         String code = input.nextLine();
         overallRoot = traceTree(overallRoot, asciiValue, code);
      }
   }
   
   // reads the encoded given int asciiValue and given String code to
   // create and return HuffmanNode tree.
   private HuffmanNode traceTree(HuffmanNode root, int asciiValue, String code) {
      if (code.isEmpty()) {
         return new HuffmanNode((char)asciiValue, -1); // uses -1 as default
      } else {                                        // standard frequency
         if (root == null) {
            root = new HuffmanNode((char)256, -1); // uses ascii value of 256
         }                                       // to create blank nodes
         if (code.substring(0,1).equals("1")) {
            root.right = traceTree(root.right, asciiValue, code.substring(1));
         } else {
            root.left = traceTree(root.left, asciiValue, code.substring(1));
         }
      }
      return root;
   }
   
   // prints the HuffmanCode encoding in the given PrintStream output.
   public void save(PrintStream output) {
      save(overallRoot,output, "");
   }
   
   // reads the HuffmanCode Tree by using the given HuffmanNode root,
   // store the decisions made which reading into the given String
   // result and prints the result in the PrintStream output
   private void save(HuffmanNode root, PrintStream output, String result) {
      if (root != null) {
         if (root.left == null && root.right == null) {
            output.println((int)root.letter);
            output.println(result);
         }
         save(root.left, output, result + "0");
         save(root.right, output, result + "1");
      }
   }
   
   // reads from the given BitInputStream input which is the compressed
   // representation, uses that input to decompress into the text file and
   // prints the text in the PrintStream output.
   public void translate(BitInputStream input, PrintStream output) {
      HuffmanNode translater = overallRoot;
      while(input.hasNextBit() || (translater.left == null && translater.right == null)) {
         if (translater.left == null && translater.right == null) {
            output.print(translater.letter);
            translater = overallRoot;
         } else {
            int data = input.nextBit();
            if (data == 0) {
               translater = translater.left;
            } else {
               translater = translater.right;
            }
         }
      }
   }
   
   // This class creates HuffmanNode which contains ascii value of a letter,
   // frequency of that letter in the given text, left, and right references
   // to other HuffmanNode objects giving it a binary structure. Implements
   // comparable to compare it with other HuffmanNodes.
   
   private static class HuffmanNode implements Comparable<HuffmanNode> {
      public char letter;
      public int frequency;
      public HuffmanNode left;
      public HuffmanNode right;
      
      // constructs a HuffmanNode with the given char letter, int frequency,
      // HuffmanNode left, and HuffmanNode right.
      public HuffmanNode(char letter, int frequency, HuffmanNode left, HuffmanNode right) {
         this.letter = letter;
         this.frequency = frequency;
         this.left = left;
         this.right = right;
      }
      
      // constructs a HuffmanNode with the given char letter, and int frequency
      public HuffmanNode(char letter, int frequency) {
         this(letter, frequency, null, null);
      }
      
      // compares the frequency with other HuffmanNode objects.
      public int compareTo(HuffmanNode other) {
         return (this.frequency - other.frequency);
      }
   }
}