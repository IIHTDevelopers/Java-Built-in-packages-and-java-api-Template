package com.yaksha.assignment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		// Create an ArrayList and call the 'add' method
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("Apple");
		arrayList.add("Banana");
		System.out.println("ArrayList contents: " + arrayList);

		// Create a HashMap and call the 'put' method
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("1", "One");
		hashMap.put("2", "Two");
		System.out.println("HashMap contents: " + hashMap);

		// Create a File object and call 'createNewFile' method
		File file = new File("testfile.txt");
		try {
			if (file.createNewFile()) {
				System.out.println("File created: " + file.getName());
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred while creating the file.");
			e.printStackTrace();
		}
	}
}
