public class Valuefinder {
  public int[] value(String u) {// takes string in the form of "X Y"
    String[] arr1 = u.split(" ");// splits the string into arrays, splitting from spaces (" ")
    int[] temp = new int[arr1.length];// initate new array that will hold the integer values
    for (int i = 0; i < arr1.length; i++) {
      temp[i] = Integer.parseInt(arr1[i]);// turn strings to integer values
    }
    return temp;// return integer array
  }
}
