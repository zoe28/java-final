package lib;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.NullPointerException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LetterGrader {
  private String inputFileName;
  private String outputFileName;
  private Scanner file;
  private Map<String, String[]> studentGrades = new HashMap<String, String[]>();
  private Map<String, String> studentLetterGrades = new HashMap<String, String>();
  private static final double assignmentWeights[] = new double[] { 0.1, 0.1, 0.1, 0.1, 0.2, 0.15, 0.25 };
  private static final String assignments[] = new String[] { "Q1", "Q2", "Q3", "Q4", "Mid1", "Mid2", "Final" };

  
  public LetterGrader(String inputFileName, String outputFileName) {
    this.inputFileName = inputFileName;
    this.outputFileName = outputFileName;
  }

  public void readScore() {
    try {
      this.file = new Scanner(new FileReader(this.inputFileName));
      while (file.hasNextLine()) {
        String line = file.nextLine();
        String[] values = line.split(", "); // split the line of data into an array
        this.studentGrades.put(values[0], Arrays.copyOfRange(values, 1, values.length)); // organize the data by a map of student name -> array of grades
      }
    } catch (FileNotFoundException e) {
      System.out.printf("Could not find file: %s \n\n", this.inputFileName);
    }
  }

  private static double calculateFinalGrade(String[] grades) {
    double weightedGrades[] = new double[grades.length];
    Arrays.setAll(weightedGrades, i -> Double.parseDouble(grades[i]) * assignmentWeights[i]); // element-wise multiply each grade with its weight
    double finalGrade = Arrays.stream(weightedGrades).reduce(0.0, (a, b) -> a + b); // sum the weighted grades
    return finalGrade;
  }

  private static String calculateLetterGrade(double finalGrade) {
    String letterGrade = "A";
    if (finalGrade < 90 && finalGrade >= 80) {
      letterGrade = "B";
    } else if (finalGrade < 80 && finalGrade >= 70) {
      letterGrade = "C";
    } else if (finalGrade < 70 && finalGrade >= 60) {
      letterGrade = "D";
    } else if (finalGrade < 60) {
      letterGrade = "F";
    }
    return letterGrade;
  }

  public void calcLetterGrade() {
    for (Map.Entry<String, String[]> entry : this.studentGrades.entrySet()) {
      String student = entry.getKey();
      String[] grades = entry.getValue();
      String letterGrade = calculateLetterGrade(calculateFinalGrade(grades));
      this.studentLetterGrades.put(student, letterGrade);
    }
  }

  public void printGrade() {
    String title = String.format("Letter grade for %s students given in input_data.txt file is:\n",
        this.studentLetterGrades.size());
    List<String> lines = new ArrayList<String>();

    for (Map.Entry<String, String> entry : this.studentLetterGrades.entrySet()) {
      String student = entry.getKey();
      String letterGrade = entry.getValue();
      lines.add(String.format("%s\t%s", student, letterGrade));
    }
    Collections.sort(lines);
    lines.add(0, title);

    Path file = Paths.get(this.outputFileName);
    try {
      Files.write(file, lines, Charset.forName("UTF-8"));
    } catch (IOException e) {
      System.out.println("Oops, the output file could not be written.");
    }
  }

  private static String[] convertDoubleArrayToStringArray(double[] doubles) {
    String[] strings = new String[doubles.length];
    for (int i = 0; i < doubles.length; i++) {
      strings[i] = String.format("%.2f", doubles[i]); // precision of 2 decimal places
    }
    return strings;
  }

  public void displayAverages() {
    System.out.printf(
        "\nLetter grade has been calculated for students listed in input file %s and written to output file %s\n\n",
        this.inputFileName, this.outputFileName);
    System.out.println("Here is the class averages:");
    System.out.printf("\t\t%s\n", String.join("\t", assignments));

    double averages[] = new double[assignments.length];
    double minimums[] = new double[assignments.length];
    Arrays.fill(minimums, 100);
    double maximums[] = new double[assignments.length];

    for (String[] grades : this.studentGrades.values()) {
      for (int i = 0; i < assignments.length; i++) {
        double grade = Double.parseDouble((grades[i]));
        averages[i] += grade;
        minimums[i] = (grade < minimums[i]) ? grade : minimums[i];
        maximums[i] = (grade > maximums[i]) ? grade : maximums[i];
      }
    }
    Arrays.setAll(averages, i -> averages[i] / this.studentGrades.size());

    System.out.printf("Average:\t%s\n", String.join("\t", convertDoubleArrayToStringArray(averages)));
    System.out.printf("Minimum:\t%s\n", String.join("\t", convertDoubleArrayToStringArray(minimums)));
    System.out.printf("Maximum:\t%s\n", String.join("\t", convertDoubleArrayToStringArray(maximums)));
  }

  public void doCleanup() {
    System.out.println("\nPress ENTER to continue...");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();
    try {
      this.file.close();
    } catch (NullPointerException e) {
      // file was never open
    }
  }
}