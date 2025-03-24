package testutils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public class AutoGrader {

	// Test if the code demonstrates proper usage of built-in packages and Java API
	// classes
	public boolean testBuiltInPackagesAndAPI(String filePath) throws IOException {
		System.out.println("Starting testBuiltInPackagesAndAPI with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		Set<String> classes = new HashSet<>();
		Set<String> packages = new HashSet<>();
//		System.out.println("Listing all classes in the project directory:");
		listClassesAndPackages(new File(System.getProperty("user.dir")), classes, packages);

		// Print all unique packages
//		System.out.println("Detected packages:");
//		packages.forEach(System.out::println);

		// Print all unique classes
//		System.out.println("Detected classes:");
//		classes.forEach(System.out::println);

		// Flags to check if classes from Java API are used
		AtomicBoolean arrayListReferenceFound = new AtomicBoolean(false);
		AtomicBoolean hashMapReferenceFound = new AtomicBoolean(false);
		AtomicBoolean fileReferenceFound = new AtomicBoolean(false);
		AtomicBoolean mainMethodFound = new AtomicBoolean(false);

		// Read the Java file and create CompilationUnit to access the classes and
		// methods
		CompilationUnit cu = StaticJavaParser.parse(participantFile);

		// Now perform checks for references and method calls in main method
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;
				System.out.println("Detected class: " + classDecl.getNameAsString());

				// Check for the main method in Main class
				if (classDecl.getNameAsString().equals("Main")) {
					classDecl.getMethods().forEach(method -> {
						if (method.getNameAsString().equals("main")) {
							mainMethodFound.set(true);
							System.out.println("Main method found in 'Main' class.");

							// Check for references to ArrayList, HashMap, and File
							method.getBody().ifPresent(body -> {
								body.findAll(VariableDeclarationExpr.class).forEach(varDecl -> {
									// Check for creation of ArrayList reference
									varDecl.getVariables().forEach(variable -> {
										if (variable.getNameAsString().toLowerCase().contains("arraylist")
												&& variable.getType().asString().toLowerCase().contains("arraylist")) {
											arrayListReferenceFound.set(true);
											System.out.println("Reference of type 'ArrayList' found in main method.");
										}
										// Check for creation of HashMap reference
										if (variable.getNameAsString().toLowerCase().contains("hashmap")
												&& variable.getType().asString().toLowerCase().contains("hashmap")) {
											hashMapReferenceFound.set(true);
											System.out.println("Reference of type 'HashMap' found in main method.");
										}
										// Check for creation of File reference
										if (variable.getNameAsString().toLowerCase().contains("file")
												&& variable.getType().asString().toLowerCase().contains("file")) {
											fileReferenceFound.set(true);
											System.out.println("Reference of type 'File' found in main method.");
										}
									});
								});

								// Check for method calls on ArrayList, HashMap, and File references
								body.findAll(MethodCallExpr.class).forEach(callExpr -> {
									if (callExpr.getNameAsString().equals("add") && arrayListReferenceFound.get()) {
										System.out.println("Method 'add' called on 'ArrayList' reference in main.");
									}
									if (callExpr.getNameAsString().equals("put") && hashMapReferenceFound.get()) {
										System.out.println("Method 'put' called on 'HashMap' reference in main.");
									}
									if (callExpr.getNameAsString().equals("createNewFile")
											&& fileReferenceFound.get()) {
										System.out
												.println("Method 'createNewFile' called on 'File' reference in main.");
									}
								});
							});
						}
					});
				}
			}
		}

		// Ensure the main method was found
		if (!mainMethodFound.get()) {
			System.out.println("Error: Main method not found.");
			return false;
		}

		// Ensure that references were created and methods were called
		if (!arrayListReferenceFound.get()) {
			System.out.println("Error: Reference of type 'ArrayList' not created in main method.");
			return false;
		}

		if (!hashMapReferenceFound.get()) {
			System.out.println("Error: Reference of type 'HashMap' not created in main method.");
			return false;
		}

		if (!fileReferenceFound.get()) {
			System.out.println("Error: Reference of type 'File' not created in main method.");
			return false;
		}

		System.out.println("Test passed: Built-in packages and Java API classes are correctly used.");
		return true;
	}

	// Method to list all classes and packages recursively from the provided
	// directory
	private static void listClassesAndPackages(File dir, Set<String> classes, Set<String> packages) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
//				System.out.println("Entering directory: " + file.getAbsolutePath());
				listClassesAndPackages(file, classes, packages);
			} else if (file.getName().endsWith(".java")) {
//				System.out.println("Found Java file: " + file.getAbsolutePath());
				try {
					CompilationUnit cu = StaticJavaParser.parse(file);
					cu.getPackageDeclaration().ifPresent(pkg -> {
						packages.add(pkg.getNameAsString());
//						System.out.println("Detected package: " + pkg.getNameAsString());
					});
					cu.getTypes().forEach(type -> {
//						System.out.println("Detected class: " + type.getNameAsString());
						classes.add(type.getNameAsString());
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
