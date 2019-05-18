package sample;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
This class contains details such as the word frequency count for each term and term frequency for each term of the document.
 */
class DocumentProperties{


    public HashMap<String,Double> getTermFreqMap()
    {
        return termFreqMap;
    }

    HashMap<String,Integer> getWordCountMap()
    {
        return DocWordCounts;
    }

    void setTermFreqMap(HashMap<String,Double> inMap)
    {
        termFreqMap = new HashMap<String, Double>(inMap);
    }


    void setWordCountMap(HashMap<String,Integer> inMap)
    {
        DocWordCounts =new HashMap<String, Integer>(inMap);
    }
    private
    HashMap<String,Double> termFreqMap ;
    HashMap<String,Integer> DocWordCounts;
}


class TfidfCalculation {

    SortedSet<String> wordList = new TreeSet(String.CASE_INSENSITIVE_ORDER);

    //calculates Term frequency for all terms
    public HashMap<String,Double> calculateTermFrequency(HashMap<String,Integer> inputMap) {

        HashMap<String ,Double> termFreqMap = new HashMap<>();
        double sum = 0.0;
        //Get the sum of all elements in hashmap
        for (float val : inputMap.values()) {
            sum += val;
        }

        //create a new hashMap with Tf values in it.
        Iterator it = inputMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            double tf = (Integer)pair.getValue()/ sum;
            termFreqMap.put((pair.getKey().toString()),tf);
        }
        return termFreqMap;
    }

    //Returns if input contains numbers or not
    public  boolean isDigit(String input)
    {
        String regex = "(.)*(\\d)(.)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        boolean isMatched = matcher.matches();
        if (isMatched) {
            return true;
        }
        return false;
    }

    //Writes the contents of hashmap to CSV file
    public  void outputAsCSV(HashMap<String,Double> treeMap, String OutputPath) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Double> keymap : treeMap.entrySet()) {
            builder.append(keymap.getKey());
            builder.append(",");
            builder.append(keymap.getValue());
            builder.append("\r\n");
        }
        String content = builder.toString().trim();
        BufferedWriter writer = new BufferedWriter(new FileWriter(OutputPath));
        writer.write(content);
        writer.close();
    }
    //cleaning up the input by removing .,:"
    public String cleanseInput(String input)throws IOException {
//        String newStr = input.replaceAll("hello", "");
/////////////////////////////////////////////////////
        Path stoplist_path = Paths.get("delete.txt");
        Charset charset = Charset.forName("utf-8");

        List<String> stoplist = Files.readAllLines(stoplist_path, charset);


       for (String stp : stoplist) {
            input = input.replaceAll(stp, "");

    }return input ;}
    // Converts the input text file to hashmap and even dumps the final output as CSV files
    public HashMap<String, Integer> getTermsFromFile(String Filename, int count, File folder) {
        HashMap<String,Integer> WordCount = new HashMap<String,Integer>();
        BufferedReader reader = null;
        HashMap<String, Integer> finalMap = new HashMap<>();
        try
        {
            reader = new BufferedReader(new FileReader(Filename));
            String line = reader.readLine();
            while(line!=null)
            {
                String[] words = line.toLowerCase().split(" ");
                for(String term : words)
                {
                    //cleaning up the term ie removing .,:"
                    term = cleanseInput(term);
                    //ignoring numbers
                    if(isDigit(term))
                    {
                        continue;
                    }
                    if(term.length() == 0)
                    {
                        continue;
                    }
                    wordList.add(term);
                    if(WordCount.containsKey(term))
                    {
                        WordCount.put(term,WordCount.get(term)+1);
                    }
                    else
                    {
                        WordCount.put(term,1);
                    }
                }
                line = reader.readLine();
            }
            // sorting the hashmap
            Map<String, Integer> treeMap = new TreeMap<>(WordCount);
            finalMap = new HashMap<String, Integer>(treeMap);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return finalMap;
    }


    public void main4() throws IOException {
        int count = 0;
        TfidfCalculation TfidfObj = new TfidfCalculation();
        File folder = new File("input");
        File folder1 = new File("output");
        // loops over files available in the path except for hidden files.
        File[] listOfFiles = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isHidden();
            }
        });
        int noOfDocs = listOfFiles.length;
        //containers for documents and their properties required to calculate final score
        DocumentProperties[] docProperties = new DocumentProperties[noOfDocs];


        // Get wordcount from file and calculate TermFrequency
        for (File file : listOfFiles) {
            /////////////////////////////////////


            ///////////////////
            if (file.isFile()) {
                docProperties[count] = new DocumentProperties();
                HashMap<String, Integer> wordCount = TfidfObj.getTermsFromFile(file.getAbsolutePath(), count, folder);
                docProperties[count].setWordCountMap(wordCount);
                HashMap<String, Double> termFrequency = TfidfObj.calculateTermFrequency(docProperties[count].DocWordCounts);
                docProperties[count].setTermFreqMap(termFrequency);
                count++;
            }
        }
        //Calculating tf-idf
        count = 0;
        for (File file : listOfFiles) {


            if (file.isFile()) {

                HashMap<String,Double> tfIDF = new HashMap<>();
                HashMap<String,Double> tf = docProperties[count].getTermFreqMap();
                Iterator itTF = tf.entrySet().iterator();
                while (itTF.hasNext()) {
                    Map.Entry pair = (Map.Entry)itTF.next();
                    double tfVal  = (Double)pair.getValue() ;
                    tfIDF.put((pair.getKey().toString()),tfVal);
                }
                int fileNameNumber = (count+1);
                String OutPutPath = folder1.getAbsolutePath()+"/"+file.getName()+".txt";
                TfidfObj.outputAsCSV(tfIDF,OutPutPath);
                count++;
            }
        }

    }
}
