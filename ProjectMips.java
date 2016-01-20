import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/* 
 * Student Name: Camillo John (CJ) D'Alimonte
 * Student Number: 212754396
 * 
 * 
 * MAIN OBJECTIVES:
 * 
 * Read input from file, convert from hexadecimal to binary
 * Add adequate number of 0's to set each binary value to 32 bits
 * Split strings into appropriate sections (R vs I Type Registers)
 * Use the encoding info (MIPS Green Card) to match commands with correct opcode
 */

public class ProjectMips {

	private static ArrayList<Integer> memory = new ArrayList<Integer>();

	private static int[] register = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // array of 32 ints for register
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };

	private static void Convert(Integer i) {
		
		StringBuffer in = new StringBuffer(Integer.toBinaryString(i));
		while (in.length() < 32) { // add adequate number of 0's
			in.insert(0, 0);
		}
		String opcode = in.substring(0, 6);
		int shamt, immediate, rt, rs, rd = 0;
		String funct, imm;

		switch (opcode) {
		case "000000":
			rt = Integer.parseInt(in.substring(11, 16), 2);
			rd = Integer.parseInt(in.substring(16, 21), 2);
			rs = Integer.parseInt(in.substring(6, 11), 2);
			shamt = Integer.parseInt(in.substring(21, 26), 2);
			funct = in.substring(26, 32);
			
			
			/*
			 *  				R-Type Register
			 *   --------------------------------------------------------------------------------------------------
			 *   |	opcode (0,6) --> rs (6,11) --> rt (11,16) --> rd (16,21) --> shamt (21,26) --> funct (26,32)  |												
			 *   --------------------------------------------------------------------------------------------------
			 */
			

			if (funct.equals("000000")) {   // shift left logically 
				register[rd] = (int) register[rt] << shamt;
			}
			if (funct.equals("000010")) {   // shift right logically
				register[rd] = (int) register[rt] >> shamt;
			}
			
			if (funct.equals("101010")) {   // set less than
				register[rd] = ((int) register[rs] < (int) register[rt]) ? 1
						: 0;
			}
			
			if (funct.equals("101011")) {   // set less than unsigned
				register[rd] = register[rs] < register[rt] ? 1 : 0;
			}
			
			if (funct.equals("100000")) {   // add
				register[rd] = (int) register[rs] + (int) register[rt];
			}
			if (funct.equals("100001")) {    // add unsigned
				register[rd] = register[rs] + register[rt];
			}
			
			if (funct.equals("100100")) {    // and
				register[rd] = (int) register[rs] & (int) register[rt];
			}
			if (funct.equals("100111")) {    // nor (not or)
				register[rd] = ~((int) register[rt] | (int) register[rs]);
			}
			
			if (funct.equals("001100")) {    // syscall
				System.out.println("Syscall - Program Ended");
				return;
			}
			break;
			
		case "001001": // add immediate unsigned 
			
			imm = in.substring(16, 32);    // I TYPE REGISTER
			immediate = Integer.parseInt(imm, 2);
			rt = Integer.parseInt(in.substring(11, 16), 2);
			rs = Integer.parseInt(in.substring(6, 11), 2);
			register[rt] = register[rs] + immediate;
			break;
			
		case "001010": // set less than immediate
			
			imm = in.substring(16, 32);   // I TYPE REGISTER
			if (imm.charAt(0) == 1)// sign extension for immediate
				imm = "1111111111111111" + imm;
			immediate = Integer.parseInt(imm, 2);
			rt = Integer.parseInt(in.substring(11, 16), 2);
			rs = Integer.parseInt(in.substring(6, 11), 2);
			register[rt] = ((int) register[rs] < immediate) ? 1 : 0; // check if less than
			break;
		
		case "001111": // lui 
			immediate = Integer.parseInt(in.substring(16, 32), 2);
			rt = Integer.parseInt(in.substring(11, 16), 2);
			register[rt] = immediate << 16;  // shift
			break;
		}

	}

	private static void RegistertoString() {
		
		
		String[] reg = new String[32]; // array of 32 registers
		for (int i = 0; i < 32; i++) {
			reg[i] = Integer.toHexString((int) register[i]); // convert to hexadecimal
			while (reg[i].length() < 8) {
				reg[i] = "0" + reg[i];   // add adequate 0's until 8 bits
			}
			reg[i] = "0x" + reg[i];
		}
		
		
		System.out.println("$0: " + reg[0] + " " + "$1: " + reg[1] + " "
				+ "$2: " + reg[2] + " " + "$3: " + reg[3]);
		System.out.println("$4: " + reg[4] + " " + "$5: " + reg[5] + " "   // 4 registers per line
				+ "$6: " + reg[6] + " " + "$7: " + reg[7]);
		System.out.println("$8: " + reg[8] + " " + "$9: " + reg[9] + " "
				+ "$10:" + reg[10] + " " + "$11:" + reg[11]);
		System.out.println("$12:" + reg[12] + " " + "$13:" + reg[13] + " "
				+ "$14:" + reg[14] + " " + "$15:" + reg[15]);
		System.out.println("$16:" + reg[16] + " " + "$17:" + reg[17] + " "
				+ "$18:" + reg[18] + " " + "$19:" + reg[19]);
		System.out.println("$20:" + reg[20] + " " + "$21:" + reg[21] + " "
				+ "$22:" + reg[22] + " " + "$23:" + reg[23]);
		System.out.println("$24:" + reg[24] + " " + "$25:" + reg[25] + " "
				+ "$26:" + reg[26] + " " + "$27:" + reg[27]);
		System.out.println("$28:" + reg[28] + " " + "$29:" + reg[29] + " "
				+ "$30:" + reg[30] + " " + "$31:" + reg[31]);
		System.out.println("========================================"); // 40 ='s

	}
	
	// Main method, get input from file
	public static void main(String[] args) {
				
		File input;
		Scanner inputScanner;
		int mode;

		try {
			inputScanner = new Scanner(new File ("code_1.txt"));
			mode = 0;
			while (inputScanner.hasNext()) {
				memory.add(inputScanner.nextInt(16));
			}

			Iterator<Integer> iterator = memory.iterator();
			if (iterator.hasNext())
				mode = iterator.next();    // first line of file is mode (0 or 1)
			while (iterator.hasNext()) {
				Convert(iterator.next());
				if ((mode == 1) && (iterator.hasNext()))  // if mode = 1, then print registers for every step/input
					RegistertoString();  // print registers
			}
			RegistertoString();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}