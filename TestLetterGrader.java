import lib.LetterGrader;

public class TestLetterGrader {
  public static void main(String args[]) {
    if (args.length < 1) {
      System.out.println("Please enter the filename of the data\n");
    } else if (args.length < 2) {
      System.out.println("Please enter an output filename\n");
    } else {
      String inputFileName = args[0];
      String outputFileName = args[1];
      LetterGrader letterGrader = new LetterGrader(inputFileName, outputFileName);

      letterGrader.readScore();
      letterGrader.calcLetterGrade();
      letterGrader.printGrade();
      letterGrader.displayAverages();
      letterGrader.doCleanup();
    }
  }
}
