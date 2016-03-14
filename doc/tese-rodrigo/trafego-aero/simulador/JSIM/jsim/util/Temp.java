
package jsim.util;

//import jsim.util.*;

class Temp {

public static void main (String [] args)
{
    int    nodeType    = 2;
    String nodeName    = "customer";
    int    numTokens   = 100;
    String distribtion = "Uniform";
    double alpha       = 2000.0;
    double beta        = 1000.0;
    int    stream      = 0;

    System.out.println (
        "new Prop (Node." + Node.TYPE_NAME_CAP [nodeType] + ", \"" +
                   nodeName + "\", " +
                   numTokens + ", new " +
                   distribtion + " (" +
                   alpha + ", " +
                   beta + ", " +
                   stream + "))"
    );

}; // main

}; // class

